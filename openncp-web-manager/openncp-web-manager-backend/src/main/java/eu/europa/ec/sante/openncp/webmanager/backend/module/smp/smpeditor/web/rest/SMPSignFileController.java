package eu.europa.ec.sante.openncp.webmanager.backend.module.smp.smpeditor.web.rest;

import eu.europa.ec.sante.openncp.common.property.PropertyService;
import eu.europa.ec.sante.openncp.webmanager.backend.error.ApiException;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.Constants;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.cfg.ReadSMPProperties;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.domain.*;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.service.SimpleErrorHandler;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.smpeditor.service.BdxSmpValidator;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.smpeditor.service.SMPConverter;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.smpeditor.service.SignFileService;
import eu.europa.ec.sante.openncp.webmanager.backend.service.PermissionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ehdsi.EndpointType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ehdsi.RedirectType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ehdsi.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = "/api")
public class SMPSignFileController {

    private final Logger logger = LoggerFactory.getLogger(SMPSignFileController.class);

    private final SMPConverter smpconverter;

    private final Environment env;

    private final ReadSMPProperties readProperties;

    private final SignFileService signFileService;
    private final PropertyService propertyService;


    @Autowired
    public SMPSignFileController(final SMPConverter smpconverter, final Environment env, final ReadSMPProperties readProperties, final SignFileService signFileService, final PropertyService propertyService) {
        this.smpconverter = smpconverter;
        this.env = env;
        this.readProperties = readProperties;
        this.signFileService = signFileService;
        this.propertyService = Validate.notNull(propertyService, "PropertyService must not be null");
    }

    @PostMapping(path = "/smpeditor/sign/fromSmpFile")
    public ResponseEntity<SMPFileOps> createSMPFileOps(@RequestBody final SMPFile smpfile) {

        logger.debug("\n==== in signCreatedFile ====");
        if (smpfile.getGeneratedFile() == null) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "The requested file does not exists");
        }
        final File file = new File(smpfile.getGeneratedFile().getPath());
        final SMPFileOps smpFileOps = new SMPFileOps();
        smpFileOps.setFileToSign(file);
        smpFileOps.setSignFileName(file.getName());
        return ResponseEntity.ok(smpFileOps);
    }

    @PostMapping(path = "/smpeditor/sign/upload")
    public ResponseEntity<SMPFileOps> createSMPFileOps(@RequestPart final MultipartFile multipartFile) {

        final SMPFileOps smpFileOps = new SMPFileOps();
        PermissionUtil.initializeFolders(Constants.SMP_DIR_PATH);
        final File convFile = new File(Constants.SMP_DIR_PATH + File.separator + multipartFile.getOriginalFilename());
        try {
            multipartFile.transferTo(convFile);
        } catch (final IOException | IllegalStateException ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getClass().getName());
        }

        smpFileOps.setFileToSign(convFile);
        return ResponseEntity.ok(smpFileOps);

    }

    @PostMapping(path = "/smpeditor/sign/generateSmpFileOpsData")
    public ResponseEntity<SMPFileOps> generateSMPFileOpsData(@RequestBody final SMPFileOps smpFileOps) throws IOException {

        String type = null;

        final String contentFile = new String(Files.readAllBytes(Paths.get(smpFileOps.getFileToSign().getPath())));
        if (!BdxSmpValidator.validateFile(contentFile)) {
            final boolean fileDeleted = smpFileOps.getFileToSign().delete();
            logger.debug("Converted File deleted: '{}'", fileDeleted);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("error.notsmp"));
        }

        final Object fileConverted = smpconverter.convertFromXml(smpFileOps.getFileToSign());

        if (smpconverter.isSignedServiceMetadata(fileConverted)) {
            final boolean fileDeleted = smpFileOps.getFileToSign().delete();
            logger.debug("Converted File deleted: '{}'", fileDeleted);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("warning.isSigned.sigmenu"));
        }

        final ServiceMetadata serviceMetadata = smpconverter.getServiceMetadata(fileConverted);

      /*
       Condition to know the type of file (Redirect|ServiceInformation) in order to build the form
       */

        if (serviceMetadata.getRedirect() != null) {
            logger.debug("\n******** REDIRECT");
            type = "Redirect";
            smpFileOps.setType(SMPType.Redirect);

            if (!serviceMetadata.getRedirect().getExtensions().isEmpty()) {
                smpFileOps.setAlert(new Alert(env.getProperty("warning.isSignedExtension"), Alert.alertType.warning));
            }
        
        /*
          get documentIdentifier and participantIdentifier from redirect href
        */
            final String participantID;
            String documentID = "";
            final Pattern pattern = Pattern.compile(env.getProperty("ParticipantIdentifier.Scheme") + ".*");//SPECIFICATION
            final Matcher matcher = pattern.matcher(serviceMetadata.getRedirect().getHref());
            if (matcher.find()) {
                String result = matcher.group(0);
                result = java.net.URLDecoder.decode(result, StandardCharsets.UTF_8);
                final String[] ids = result.split("/services/");//SPECIFICATION
                participantID = ids[0];
                final String[] cc = participantID.split(":");//SPECIFICATION May change if Participant Identifier specification change

                for (final Countries country : Countries.values()) {
                    if (cc[4].equals(country.name())) {
                        smpFileOps.setCountry(cc[4]);
                    }
                }
                if (smpFileOps.getCountry() == null) {
                    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("error.redirect.href.participantID"));
                }

                final String docID = ids[1];
                final Map<String, String> propertiesMap = readProperties.readPropertiesFile();
                //SPECIFICATION May change if Document Identifier specification change
                final String[] nIDs = docID.split(env.getProperty("DocumentIdentifier.Scheme") + "::");
                final String docuID = nIDs[1];
                final Set<Map.Entry<String, String>> set2 = propertiesMap.entrySet();
                for (final Object aSet2 : set2) {
                    final Map.Entry mentry2 = (Map.Entry) aSet2;

                    if (docuID.equals(mentry2.getKey().toString())) {
                        final String[] docs = mentry2.getValue().toString().split("\\.");
                        documentID = docs[0];
                        break;
                    }
                }

                if (documentID.equals("")) {
                    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("error.redirect.href.documentID"));
                }

                /*Builds final file name*/
                final String timeStamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
                final String fileName = smpFileOps.getType().name() + "_" + documentID + "_" + smpFileOps.getCountry().toUpperCase() + "_Signed_" + timeStamp + ".xml";
                smpFileOps.setFileName(fileName);
            } else {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("error.redirect.href"));
            }

        } else if (serviceMetadata.getServiceInformation() != null) { /*Service Information Type*/
            type = "ServiceInformation";

            if (!serviceMetadata.getServiceInformation().getExtensions().isEmpty()) {
                final Alert alert = new Alert(env.getProperty("warning.isSignedExtension"), Alert.alertType.warning);
                smpFileOps.setAlert(alert);
            }

            smpFileOps.setDocumentIdentifier(serviceMetadata.getServiceInformation().getDocumentIdentifier().getValue());
            smpFileOps.setDocumentIdentifierScheme(serviceMetadata.getServiceInformation().getDocumentIdentifier().getScheme());
            final String documentIdentifier = smpFileOps.getDocumentIdentifier();

            String documentID = "";
            final Map<String, String> propertiesMap = readProperties.readPropertiesFile();
            final Set set2 = propertiesMap.entrySet();
            for (final Object aSet2 : set2) {
                final Map.Entry mentry2 = (Map.Entry) aSet2;

                if (documentIdentifier.equals(mentry2.getKey().toString())) {
                    final String[] docs = mentry2.getValue().toString().split("\\.");
                    documentID = docs[0];
                    break;
                }
            }

            final SMPType[] smptypes = SMPType.values();
            for (final SMPType smptype : smptypes) {
                if (smptype.name().equals(documentID)) {
                    smpFileOps.setType(smptype);
                    break;
                }
            }
            if (smpFileOps.getType() == null) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("error.serviceinformation.documentID"));
            }

            final String participanteID = serviceMetadata.getServiceInformation().getParticipantIdentifier().getValue();
            final String[] cc = participanteID.split(":");

            for (final Countries country : Countries.values()) {
                if (cc[2].equals(country.name())) {
                    smpFileOps.setCountry(cc[2]);
                }
            }
            if (smpFileOps.getCountry() == null) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("error.serviceinformation.participantID"));
            }

            /*Builds final file name*/
            final String timeStamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
            final String fileName = smpFileOps.getType().name() + "_" + smpFileOps.getCountry().toUpperCase() + "_Signed_" + timeStamp + ".xml";
            smpFileOps.setFileName(fileName);

            smpFileOps.setParticipantIdentifier(participanteID);
            smpFileOps.setParticipantIdentifierScheme(serviceMetadata.getServiceInformation().getParticipantIdentifier().getScheme());
            smpFileOps.setProcessIdentifier(serviceMetadata.getServiceInformation().getProcessList().getProcesses().get(0).getProcessIdentifier().getValue());
            smpFileOps.setProcessIdentifierScheme(serviceMetadata.getServiceInformation().getProcessList().getProcesses().get(0).getProcessIdentifier().getScheme());
            smpFileOps.setTransportProfile(serviceMetadata.getServiceInformation().getProcessList().getProcesses().get(0).getServiceEndpointList().getEndpoints().get(0).getTransportProfile());
            smpFileOps.setRequiredBusinessLevelSig(serviceMetadata.getServiceInformation().getProcessList().getProcesses().get(0).getServiceEndpointList().getEndpoints().get(0).isRequireBusinessLevelSignature());
            smpFileOps.setMinimumAutenticationLevel(serviceMetadata.getServiceInformation().getProcessList().getProcesses().get(0).getServiceEndpointList().getEndpoints().get(0).getMinimumAuthenticationLevel());
        }

        final SMPFields smpfields = readProperties.readProperties(smpFileOps.getType());

        // Handling Service Information Type
        if (StringUtils.equals("ServiceInformation", type)) {

            final EndpointType endpoint = serviceMetadata.getServiceInformation().getProcessList().getProcesses().get(0)
                    .getServiceEndpointList().getEndpoints().get(0);

            final X509Certificate cert;
            String subjectName = null;
            if (smpfields.getCertificate().isEnable()) {
                try {
                    final InputStream in = new ByteArrayInputStream(endpoint.getCertificate());
                    logger.debug("Endpoint Certificate PEM:\n'{}'", DatatypeConverter.printBase64Binary(endpoint.getCertificate()));
                    cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);
                    if (cert != null) {
                        subjectName = "Issuer: " + cert.getIssuerX500Principal().getName() + "\nSerial Number #" + cert.getSerialNumber();
                        logger.debug("Certificate: '{}'", subjectName);
                        smpFileOps.setCertificateContent(subjectName);
                        smpFileOps.setCertificate(cert.getEncoded());
                    }
                } catch (final CertificateException ex) {
                    logger.error("CertificateException - '{}'", SimpleErrorHandler.printExceptionStackTrace(ex));
                }
            } else {
                smpFileOps.setCertificate(null);
            }

            smpFileOps.setEndpointURI(endpoint.getEndpointURI());

            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            final Calendar serviceActivationDate = endpoint.getServiceActivationDate();
            if (serviceActivationDate != null && serviceActivationDate.getTime() != null) {
                smpFileOps.setServiceActivationDateS(format.format(serviceActivationDate.getTime()));
            } else {
                logger.error("Mandatory field ServiceActivationDate is not set");
            }
            final Calendar serviceExpirationDate = endpoint.getServiceExpirationDate();
            if (serviceExpirationDate != null && serviceExpirationDate.getTime() != null) {
                smpFileOps.setServiceExpirationDateS(format.format(serviceExpirationDate.getTime()));
            } else {
                logger.error("Mandatory field serviceExpirationDate is not set");
            }
            smpFileOps.setCertificateContent(subjectName);
            smpFileOps.setServiceDescription(endpoint.getServiceDescription());
            smpFileOps.setTechnicalContactUrl(endpoint.getTechnicalContactUrl());
            smpFileOps.setTechnicalInformationUrl(endpoint.getTechnicalInformationUrl());

            if (smpfields.getExtension().isEnable()) {

                try (final Scanner scanner = new Scanner(smpFileOps.getFileToSign(), StandardCharsets.UTF_8.name())) {

                    final String content = scanner.useDelimiter("\\Z").next();
                    final String capturedString = content.substring(content.indexOf("<Extension>"), content.indexOf("</Extension>"));
                    final String[] endA = capturedString.split("<Extension>");
                    smpFileOps.setExtensionContent(endA[1]);
                } catch (final IOException ex) {
                    logger.error("\n IOException - '{}'", SimpleErrorHandler.printExceptionStackTrace(ex));
                }
            }
        } else if ("Redirect".equals(type)) {
            final RedirectType redirect = serviceMetadata.getRedirect();
            smpFileOps.setCertificateUID(redirect.getCertificateUID());
            smpFileOps.setHref(redirect.getHref());
        }

        return ResponseEntity.ok(smpFileOps);
    }

    @PostMapping(value = "/smpeditor/sign/sign")
    public ResponseEntity signSMPFile(@RequestBody final SMPFileOps smpFileOps) {

        final var file = new File(propertyService.getPropertyValueMandatory("NCP_SIG_KEYSTORE_PATH"));
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (final FileNotFoundException ex) {
            logger.error("\n FileNotFoundException - '{}'", SimpleErrorHandler.printExceptionStackTrace(ex));
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(env.getProperty("FileNotFoundException Keystore"));
        }
        MultipartFile keystore = null;
        try {
            keystore = new MockMultipartFile("keystore", file.getName(), "text/xml", input);
        } catch (final IOException ex) {
            logger.error("\n IOException - '{}'", SimpleErrorHandler.printExceptionStackTrace(ex));
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(env.getProperty("IO Exception Keystore"));
        }

        File fileSigned = null;

        try {
            fileSigned = signFileService.signFiles(smpFileOps.getType().name(),
                    smpFileOps.getFileName(),
                    keystore,
                    propertyService.getPropertyValueMandatory("NCP_SIG_KEYSTORE_PASSWORD"),
                    propertyService.getPropertyValueMandatory("NCP_SIG_PRIVATEKEY_ALIAS"),
                    propertyService.getPropertyValueMandatory("NCP_SIG_PRIVATEKEY_PASSWORD"),
                    smpFileOps.getFileToSign());
        } catch (final Exception ex) {
            logger.error("\nException - '{}'", SimpleErrorHandler.printExceptionStackTrace(ex));
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(env.getProperty("Signed error"));
        }

        smpFileOps.setGeneratedFile(fileSigned);

        return ResponseEntity.ok(smpFileOps);
    }

    @PostMapping(value = "smpeditor/sign/download")
    public void downloadGeneratedFile(@RequestBody final SMPFileOps smpFileOps, final HttpServletResponse response) {

        response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment; filename=" + smpFileOps.getFileName());
        response.setContentLength((int) smpFileOps.getGeneratedFile().length());
        try (final InputStream inputStream = new BufferedInputStream(new FileInputStream(smpFileOps.getGeneratedFile()))) {
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (final FileNotFoundException ex) {
            logger.error("FileNotFoundException - '{}'", SimpleErrorHandler.printExceptionStackTrace(ex));
        } catch (final IOException ex) {
            logger.error("IOException - '{}'", SimpleErrorHandler.printExceptionStackTrace(ex));
        }
    }

    @PostMapping(value = "smpeditor/sign/clean")
    public ResponseEntity cleanFile(@RequestBody final SMPFileOps smpFileOps) {

        if (smpFileOps.getFileToSign() != null) {
            final boolean delete = smpFileOps.getFileToSign().delete();
            logger.debug("\n****DELETED ? '{}'", delete);
        }
        if (smpFileOps.getGeneratedFile() != null) {
            final boolean delete = smpFileOps.getGeneratedFile().delete();
            logger.debug("\n****DELETED ? '{}'", delete);
        }
        return ResponseEntity.ok().build();
    }
}
