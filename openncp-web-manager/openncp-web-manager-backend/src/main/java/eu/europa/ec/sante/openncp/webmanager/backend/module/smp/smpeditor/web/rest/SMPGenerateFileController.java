package eu.europa.ec.sante.openncp.webmanager.backend.module.smp.smpeditor.web.rest;

import eu.europa.ec.sante.openncp.webmanager.backend.error.ApiException;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.cfg.ReadSMPProperties;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.domain.Countries;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.domain.SMPFields;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.domain.SMPFile;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.domain.SMPType;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.smpeditor.service.BdxSmpValidator;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.smpeditor.service.SMPConverter;
import eu.europa.ec.sante.openncp.webmanager.backend.service.PropertyService;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = "/api")
public class SMPGenerateFileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMPGenerateFileController.class);

    private final SMPConverter smpconverter;

    private final Environment env;

    private final ReadSMPProperties readProperties;
    private final PropertyService propertyService;

    public SMPGenerateFileController(final SMPConverter smpconverter, final Environment env, final ReadSMPProperties readProperties, final PropertyService propertyService) {

        this.smpconverter = smpconverter;
        this.env = env;
        this.readProperties = readProperties;
        this.propertyService = Validate.notNull(propertyService, "propertyService must not be null");
    }

    @GetMapping(path = "smpeditor/generate/smptypes")
    public ResponseEntity<Map<String, String>> getSmpTypes() {
        final Map<String, String> map = new HashMap<>();
        Arrays.stream(SMPType.values()).forEach(smpType -> map.put(smpType.name(), smpType.getDescription()));
        return ResponseEntity.ok(map);
    }

    @GetMapping(path = "smpeditor/generate/countries")
    public ResponseEntity<Map<String, String>> getCountries() {
        final Map<String, String> map = new HashMap<>();
        Arrays.stream(Countries.values()).forEach(countries -> map.put(countries.name(), countries.getDescription()));
        return ResponseEntity.ok(map);
    }

    @GetMapping(path = "smpeditor/generate/smpfields")
    public ResponseEntity<SMPFields> getSmpFields(@RequestParam final SMPType smpType) {
        return ResponseEntity.ok(readProperties.readProperties(smpType));
    }

    /*
     * Generate SMPFile data and create SMP file (the file itself)
     */
    @PostMapping(value = "smpeditor/generate/smpfile")
    public ResponseEntity<SMPFile> generateSmpFile(@RequestBody final SMPFile smpfile) throws IOException {

        final String timeStamp;
        final String fileName;

        final SMPFields smpfields = readProperties.readProperties(smpfile.getType());

        final String type = env.getProperty("type." + smpfile.getType().name());

        if ("ServiceInformation".equals(type)) {
            LOGGER.debug("\n****Type Service Information");

            /*Builds final file name*/
            timeStamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
            fileName = smpfile.getType().name() + "_" + smpfile.getCountry().toUpperCase() + "_" + timeStamp + ".xml";
            smpfile.setFileName(fileName);

            if (smpfields.getCertificate().isEnable()) {
                final String certPath = env.getProperty(smpfile.getType().name() + ".certificate");
                final String certificatePath = propertyService.getPropertyValueMandatory(certPath);
                LOGGER.info("Generating SMP file with certificate: '{}' '{}'", certPath, certificatePath);

                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(certificatePath);
                } catch (final FileNotFoundException ex) {
                    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("FileNotFoundException Certificate"));
                }

                smpfile.setCertificateFile(fis);
            } else {
                smpfile.setCertificateFile(null);
            }

            if (!smpfields.getExtension().isEnable()) {
                smpfile.setExtension(null);
            }

            if (smpfile.getIssuanceType() == null) {
                smpfile.setIssuanceType("");
            }

            smpconverter.convertToXml(smpfile.getType().name(),
                    smpfile.getIssuanceType(),
                    smpfile.getCountry(),
                    smpfile.getUri(),
                    smpfile.getServiceDescription(),
                    smpfile.getTechnicalContactUrl(),
                    smpfile.getTechnicalInformationUrl(),
                    smpfile.getServiceActivationDate(),
                    smpfile.getServiceExpirationDate(),
                    smpfile.getExtension(),
                    smpfile.getCertificateFile(),
                    smpfile.getFileName(),
                    smpfields.getRequireBusinessLevelSignature(),
                    smpfields.getMinimumAuthLevel(),
                    null,
                    null);

            if (smpfields.getCertificate().isEnable()) {
                if (smpconverter.getCertificateSubjectName() == null) {
                    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("error.certificate.invalid"));
                }
                smpfile.setCertificate(smpconverter.getCertificateSubjectName());
            }

            if (smpfields.getExtension().isEnable() && smpconverter.isNullExtension()) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("error.extension.invalid"));
            }


        } else if ("Redirect".equals(type)) {
            /*
             * Get documentIdentification and participantIdentification from redirect href.
             * May change if Document or Participant Identifier specification change.
             */
            final String href = smpfile.getHref();
            String documentID = "";
            String participantID = "";
            final Pattern pattern = Pattern.compile(env.getProperty("ParticipantIdentifier.Scheme") + ".*"); //SPECIFICATION
            final Matcher matcher = pattern.matcher(href);
            if (matcher.find()) {
                String result = matcher.group(0);
                result = java.net.URLDecoder.decode(result, StandardCharsets.UTF_8);
                //SPECIFICATION
                final String[] ids = result.split("/services/");
                participantID = ids[0];
                //SPECIFICATION May change if Participant Identifier specification change
                final String[] cc = participantID.split(":");

                for (final Countries country : Countries.values()) {
                    if (cc[4].equals(country.name())) {
                        smpfile.setCountry(cc[4]);
                    }
                }
                if (smpfile.getCountry() == null) {
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
            } else {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("error.redirect.href"));
            }

            // smpeditor.properties
            final String smpType = documentID;
            if ("".equals(smpType)) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("error.redirect.href.documentID"));
            }

            // Builds final file name
            timeStamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
            fileName = smpfile.getType().name() + "_" + smpType + "_" + smpfile.getCountry().toUpperCase() + "_" + timeStamp + ".xml";
            smpfile.setFileName(fileName);

            smpconverter.convertToXml(smpfile.getType().name(), /*0,*/ null, null, null, null, null, null, null, null, null, null,
                    smpfile.getFileName(), null, null, smpfile.getCertificateUID(), smpfile.getHref());
        }

        smpfile.setGeneratedFile(smpconverter.getFile());
        final String content = new String(Files.readAllBytes(Paths.get(smpfile.getGeneratedFile().getPath())));
        if (BdxSmpValidator.validateFile(content)) {
            LOGGER.debug("\n****VALID XML File");
        } else {
            smpfile.getGeneratedFile().deleteOnExit();
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, env.getProperty("error.file.xsd"));
        }
        return ResponseEntity.ok(smpfile);
    }

    @PostMapping(value = "smpeditor/generate/download")
    public void downloadFile(@RequestBody final SMPFile smpfile, final HttpServletResponse response) {
        response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment; filename=" + smpfile.getFileName());
        response.setContentLength((int) smpfile.getGeneratedFile().length());
        try (final InputStream inputStream = new BufferedInputStream(new FileInputStream(smpfile.getGeneratedFile()))) {
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (final IOException ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "IOException");
        }
    }

    @PostMapping(value = "smpeditor/generate/clean")
    public ResponseEntity.BodyBuilder cleanFile(@RequestBody final SMPFile smpFile) {
        if (smpFile.getGeneratedFile() != null) {
            final boolean delete = smpFile.getGeneratedFile().delete();
            LOGGER.debug("\n****DELETED ? '{}'", delete);
        }
        return ResponseEntity.ok();
    }
}
