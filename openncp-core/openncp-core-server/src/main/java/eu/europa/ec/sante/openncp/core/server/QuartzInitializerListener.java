package eu.europa.ec.sante.openncp.core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class QuartzInitializerListener implements ServletContextListener {

    private final Logger logger = LoggerFactory.getLogger(QuartzInitializerListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if (logger.isDebugEnabled()) {
            logger.debug("Web Application Initialization");
        }
/*
        try {
            AbuseDetectionHelper.abuseDetectionInit();
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }

 */
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (logger.isDebugEnabled()) {
            logger.info("Web Application Destroyed");
        }

        /*
        try {
            AbuseDetectionHelper.abuseDetectionShutdown();
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }

         */
    }
}
