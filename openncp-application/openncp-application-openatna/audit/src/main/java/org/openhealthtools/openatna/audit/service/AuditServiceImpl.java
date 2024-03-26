package org.openhealthtools.openatna.audit.service;

import eu.europa.ec.sante.openncp.common.audit.handler.FailedLogsHandlerService;
import eu.europa.ec.sante.openncp.common.audit.handler.FailedLogsHandlerServiceImpl;
import eu.europa.ec.sante.openncp.common.audit.serialization.AuditLogSerializer;
import org.openhealthtools.openatna.anom.AtnaCode;
import org.openhealthtools.openatna.anom.AtnaMessage;
import org.openhealthtools.openatna.anom.codes.CodeParser;
import org.openhealthtools.openatna.anom.codes.CodeRegistry;
import org.openhealthtools.openatna.audit.AtnaFactory;
import org.openhealthtools.openatna.audit.persistence.AtnaPersistenceException;
import org.openhealthtools.openatna.audit.persistence.PersistencePolicies;
import org.openhealthtools.openatna.audit.persistence.dao.CodeDao;
import org.openhealthtools.openatna.audit.persistence.model.codes.CodeEntity;
import org.openhealthtools.openatna.audit.persistence.util.EntityConverter;
import org.openhealthtools.openatna.audit.process.AtnaMessageListener;
import org.openhealthtools.openatna.audit.process.AtnaProcessor;
import org.openhealthtools.openatna.audit.process.ProcessContext;
import org.openhealthtools.openatna.audit.process.ProcessContext.State;
import org.openhealthtools.openatna.audit.process.ProcessorChain;
import org.openhealthtools.openatna.audit.server.AtnaServer;
import org.openhealthtools.openatna.audit.server.ServerConfiguration;
import org.openhealthtools.openatna.syslog.SyslogMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * This pulls together various configurations to create an ATNA Audit service
 *
 * @author Andrew Harrison
 */
public class AuditServiceImpl implements AuditService {

    private final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);
    private final ProcessorChain chain = new ProcessorChain();
    private ServerConfiguration serverConfig;
    private ServiceConfiguration serviceConfig = new ServiceConfiguration();
    private AtnaServer syslogServer;
    private FailedLogsHandlerService failedLogsHandlerService = null;

    /**
     * start the service
     */
    public void start() {

        logger.info("[ATNA Service] Starting OpenATNA service..");
        if (serviceConfig.getLogMessageClass() == null) {
            throw new RuntimeException("No log message defined!");
        }

        loadCodes();

        chain.setPolicies(serviceConfig.getPersistencePolicies());
        Map<ProcessorChain.PHASE, List<String>> processors = serviceConfig.getProcessors();
        for (ProcessorChain.PHASE phase : processors.keySet()) {
            List<String> ap = processors.get(phase);
            for (String atnaProcessor : ap) {
                try {
                    AtnaProcessor proc = (AtnaProcessor) Class.forName(atnaProcessor, true,
                            getClass().getClassLoader()).newInstance();
                    chain.addNext(proc, phase);
                } catch (Exception e) {
                    logger.warn("Could not load processor: '{}'", atnaProcessor, e);
                }
            }
        }
        if (serverConfig != null) {
            serverConfig.load();
            List<AtnaServer> servers = serverConfig.getServers();
            if (servers.isEmpty()) {
                logger.warn("Could not start service. No AtnaServers were loaded!");
            } else {
                for (AtnaServer atnaServer : servers) {
                    logger.info("Server: '{}'", atnaServer.getTlsConnection().getName());
                }
                this.syslogServer = servers.get(0);
                if (syslogServer != null) {

                    SyslogMessageFactory.setDefaultLogMessage(serviceConfig.getLogMessageClass());
                    AtnaMessageListener atnaMessageListener = new AtnaMessageListener(this);
                    syslogServer.start(atnaMessageListener);
                    failedLogsHandlerService = new FailedLogsHandlerServiceImpl(atnaMessageListener, AuditLogSerializer.Type.ATNA);
                    failedLogsHandlerService.start();
                }
            }
        }
    }

    private void loadCodes() {

        URL defCodes = getClass().getResource("/conf/atnacodes.xml");
        if (defCodes != null) {
            serviceConfig.addCodeUrl(defCodes.toString());
        }
        CodeParser.parse(serviceConfig.getCodeUrls());
        List<AtnaCode> atnaCodes = CodeRegistry.allCodes();
        CodeDao codeDao = AtnaFactory.codeDao();
        for (AtnaCode atnaCode : atnaCodes) {

            CodeEntity ce = EntityConverter.createCode(atnaCode, EntityConverter.getCodeType(atnaCode));
            PersistencePolicies policies = new PersistencePolicies();
            policies.setErrorOnDuplicateInsert(false);
            policies.setAllowNewCodes(true);
            try {
                if (codeDao.save(ce, policies) && logger.isDebugEnabled()) {
                    logger.debug("Saving loaded codes: '{}'", atnaCode);
                }
            } catch (AtnaPersistenceException e) {
                logger.error("Exception thrown while storing code: '{}'", e.getMessage(), e);
            }
        }
    }

    /**
     * stop the service
     */
    public void stop() {

        if (syslogServer != null) {
            syslogServer.stop();
        }

        if (failedLogsHandlerService != null) {
            failedLogsHandlerService.stop();
        }
    }

    public ServerConfiguration getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfiguration serverConfig) {
        this.serverConfig = serverConfig;
    }

    /**
     * Return true if persisted
     */
    public boolean process(AtnaMessage message) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug("Process AtnaMessage: '{}'", message.getMessageId());
        }
        ProcessContext context = new ProcessContext(message);
        chain.process(context);
        return context.getState() == State.PERSISTED;
    }

    public ServiceConfiguration getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(ServiceConfiguration serviceConfig) {
        this.serviceConfig = serviceConfig;
    }
}
