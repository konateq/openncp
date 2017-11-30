package eu.europa.ec.sante.ehdsi.openncp.gateway.smpeditor.web;

import eu.europa.ec.sante.ehdsi.openncp.configmanager.ConfigurationManagerFactory;
import eu.europa.ec.sante.ehdsi.openncp.gateway.smpeditor.entities.Alert;
import eu.europa.ec.sante.ehdsi.openncp.gateway.smpeditor.entities.Countries;
import eu.europa.ec.sante.ehdsi.openncp.gateway.smpeditor.entities.SMPFields;
import eu.europa.ec.sante.ehdsi.openncp.gateway.smpeditor.entities.SMPFile;
import eu.europa.ec.sante.ehdsi.openncp.gateway.smpeditor.service.ReadSMPProperties;
import eu.europa.ec.sante.ehdsi.openncp.gateway.smpeditor.service.SMPConverter;
import eu.europa.ec.sante.ehdsi.openncp.gateway.smpeditor.service.SimpleErrorHandler;
import eu.europa.ec.sante.ehdsi.openncp.gateway.smpeditor.service.XMLValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Inês Garganta
 */

@Controller
@SessionAttributes("smpfile")
public class SMPGenerateFileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMPGenerateFileController.class);

    private final SMPFields smpfields = new SMPFields();

    private SMPConverter smpconverter = new SMPConverter();

    private XMLValidator xmlValidator = new XMLValidator();

    private Environment env;

    private ReadSMPProperties readProperties = new ReadSMPProperties();

    private String type;

    @Autowired
    public SMPGenerateFileController(SMPConverter smpconverter, XMLValidator xmlValidator, Environment env, ReadSMPProperties readProperties) {
        this.smpconverter = smpconverter;
        this.xmlValidator = xmlValidator;
        this.env = env;
        this.readProperties = readProperties;
    }

    /**
     * Generate GenerateSMPFile page
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/smpeditor/generatesmpfile", method = RequestMethod.GET)
    public String generateForm(Model model) {
        LOGGER.debug("\n==== in generateForm ====");
        model.addAttribute("smpfile", new SMPFile());

        readProperties.readPropertiesFile();

        return "smpeditor/generatesmpfile";
    }

    /**
     * Manage Post from GenerateSMPFile page to newsmpfile page
     *
     * @param smpfile
     * @param model
     * @return
     */
    @RequestMapping(value = "/smpeditor/generatesmpfile", method = RequestMethod.POST)
    public String post(@ModelAttribute("smpfile") SMPFile smpfile, Model model) {
        LOGGER.debug("\n==== in post ====" + smpfile.toString());
        model.addAttribute("smpfile", smpfile);
        return "redirect:newsmpfile";
    }

    /**
     * Generate newsmpfile page
     *
     * @param smpfile
     * @param model
     * @return
     */
    @RequestMapping(value = "smpeditor/newsmpfile", method = RequestMethod.GET)
    public String generateFile(@ModelAttribute("smpfile") SMPFile smpfile, Model model) {

        LOGGER.debug("\n==== in generateFile ====");
        model.addAttribute("smpfile", smpfile);
    
    /*
    * Read smpeditor.properties file
    */
        readProperties.readProperties(smpfile);

        type = readProperties.getType();
        model.addAttribute(type, "Type " + type);

        smpfields.setUri(readProperties.getUri());
        smpfields.setIssuanceType(readProperties.getIssuanceType());
        smpfields.setServiceActivationDate(readProperties.getServiceActDate());
        smpfields.setServiceExpirationDate(readProperties.getServiceExpDate());
        smpfields.setCertificate(readProperties.getCertificate());
        smpfields.setServiceDescription(readProperties.getServiceDesc());
        smpfields.setTechnicalContactUrl(readProperties.getTechContact());
        smpfields.setTechnicalInformationUrl(readProperties.getTechInformation());
        smpfields.setExtension(readProperties.getExtension());
        smpfields.setRedirectHref(readProperties.getRedirectHref());
        smpfields.setCertificateUID(readProperties.getCertificateUID());
        smpfields.setRequireBusinessLevelSignature(readProperties.getRequireBusinessLevelSignature());
        smpfields.setMinimumAuthLevel(readProperties.getMinimumAuthLevel());

        model.addAttribute("smpfields", smpfields);
        String uri = env.getProperty(smpfile.getType().name() + ".uri.value");
        smpfile.setEndpointURI(uri);

        LOGGER.debug("\n**** MODEL - " + model.toString());
        return "smpeditor/newsmpfile";
    }


    /**
     * Manage Post from newsmpfile page to savesmpfile page
     * Calls SMPConverter to construct the xml file
     *
     * @param smpfile
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "smpeditor/newsmpfile", method = RequestMethod.POST)
    public String postnewfile(@ModelAttribute("smpfile") SMPFile smpfile, Model model, final RedirectAttributes redirectAttributes) {
        LOGGER.debug("\n==== in postnewfile ==== ");

        model.addAttribute("smpfile", smpfile);

        String timeStamp = "";
        String fileName = "";

        if (smpfile.getType().name() != null) {
            switch (type) {
                case "ServiceInformation":
                    LOGGER.debug("\n****Type Service Information");

          /*Builds final file name*/
                    timeStamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new java.util.Date());
                    fileName = smpfile.getType().name() + "_" + smpfile.getCountry().toUpperCase() + "_" + timeStamp + ".xml";
                    smpfile.setFileName(fileName);

                    if (!smpfields.getCertificate().isEnable()) {
                        smpfile.setCertificateFile(null);
                    } else {

                        String certPath = env.getProperty(smpfile.getType().name() + ".certificate");
                        String certificatePath = ConfigurationManagerFactory.getConfigurationManager().getProperty(certPath);
                        LOGGER.info("Generating SMP file with certificate: '{}' '{}'", certPath, certificatePath);

                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(certificatePath);
                        } catch (FileNotFoundException ex) {
                            LOGGER.error("\n FileNotFoundException - " + SimpleErrorHandler.printExceptionStackTrace(ex));
                        }

                        smpfile.setCertificateFile(fis);
                    }

                    if (!smpfields.getExtension().isEnable()) {
                        smpfile.setExtension(null);
                    }

                    if (smpfile.getIssuanceType() == null) {
                        smpfile.setIssuanceType("");
                    }

                    smpconverter.convertToXml(smpfile.getType().name(), /*clientServer,*/ smpfile.getIssuanceType(), smpfile.getCountry(), smpfile.getEndpointURI(), smpfile.getServiceDescription(),
                            smpfile.getTechnicalContactUrl(), smpfile.getTechnicalInformationUrl(), smpfile.getServiceActivationDate(),
                            smpfile.getServiceExpirationDate(), smpfile.getExtension(), smpfile.getCertificateFile(), smpfile.getFileName(),
                            smpfields.getRequireBusinessLevelSignature(), smpfields.getMinimumAuthLevel(),
                            null, null);

                    if (smpfields.getCertificate().isEnable()) {
                        if (smpconverter.getCertificateSubjectName() == null) {
                            LOGGER.error("\n****NOT VALID Certificate File");
                            String message = env.getProperty("error.certificate.invalid"); //messages.properties
                            redirectAttributes.addFlashAttribute("alert", new Alert(message, Alert.alertType.danger));
                            return "redirect:/smpeditor/newsmpfile";
                        }
                        smpfile.setCertificate(smpconverter.getCertificateSubjectName());
                    }

                    if (smpfields.getExtension().isEnable()) {
                        if (smpconverter.isNullExtension()) {
                            LOGGER.error("\n****NOT VALID Extension File");
                            String message = env.getProperty("error.extension.invalid"); //messages.properties
                            redirectAttributes.addFlashAttribute("alert", new Alert(message, Alert.alertType.danger));
                            return "redirect:/smpeditor/newsmpfile";
                        }
                    }

                    break;
                case "Redirect":
          
          /*
            get documentIdentification and participantIdentification from redirect href
          */
          /*May change if Document or Participant Identifier specification change*/
                    String href = smpfile.getHref();
                    String documentID = "";
                    String participantID = "";
                    Pattern pattern = Pattern.compile(env.getProperty("ParticipantIdentifier.Scheme") + ".*"); //SPECIFICATION
                    Matcher matcher = pattern.matcher(href);
                    if (matcher.find()) {
                        String result = matcher.group(0);
                        try {
                            result = java.net.URLDecoder.decode(result, "UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                            LOGGER.error("\n UnsupportedEncodingException - " + SimpleErrorHandler.printExceptionStackTrace(ex));
                            String message = env.getProperty("error.redirect.href"); //messages.properties
                            redirectAttributes.addFlashAttribute("alert", new Alert(message, Alert.alertType.danger));
                            return "redirect:/smpeditor/newsmpfile";
                        }
                        String[] ids = result.split("/services/");//SPECIFICATION
                        participantID = ids[0];
                        String[] cc = participantID.split(":"); //SPECIFICATION May change if Participant Identifier specification change

                        for (Countries country : Countries.getALL()) {
                            if (cc[4].equals(country.name())) {
                                smpfile.setCountry(cc[4]);
                            }
                        }
                        if (smpfile.getCountry() == null) {
                            String message = env.getProperty("error.redirect.href.participantID"); //messages.properties
                            redirectAttributes.addFlashAttribute("alert", new Alert(message, Alert.alertType.danger));
                            return "redirect:/smpeditor/newsmpfile";
                        }

                        String docID = ids[1];
                        HashMap<String, String> propertiesMap = readProperties.readPropertiesFile();
                        String[] nIDs = docID.split(env.getProperty("DocumentIdentifier.Scheme") + "::"); //SPECIFICATION May change if Document Identifier specification change
                        String docuID = nIDs[1];
                        LOGGER.debug("\n ****** docuID - '{}'", docuID);
                        Set set2 = propertiesMap.entrySet();
                        Iterator iterator2 = set2.iterator();
                        while (iterator2.hasNext()) {
                            Map.Entry mentry2 = (Map.Entry) iterator2.next();
                            // LOGGER.debug("\n ****** " + mentry2.getKey().toString() + " = " + mentry2.getValue().toString());
                            if (docuID.equals(mentry2.getKey().toString())) {
                                String[] docs = mentry2.getValue().toString().split("\\.");
                                documentID = docs[0];
                                LOGGER.debug("\n ****** documentID - '{}'", documentID);
                                break;
                            }
                        }
                    } else {
                        String message = env.getProperty("error.redirect.href"); //messages.properties
                        redirectAttributes.addFlashAttribute("alert", new Alert(message, Alert.alertType.danger));
                        return "redirect:/smpeditor/newsmpfile";
                    }

                    String smpType = documentID; //smpeditor.properties
                    if ("".equals(smpType)) {
                        String message = env.getProperty("error.redirect.href.documentID"); //messages.properties
                        redirectAttributes.addFlashAttribute("alert", new Alert(message, Alert.alertType.danger));
                        return "redirect:/smpeditor/newsmpfile";
                    }

                    // Builds final file name
                    timeStamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new java.util.Date());
                    fileName = smpfile.getType().name() + "_" + smpType + "_" + smpfile.getCountry().toUpperCase() + "_" + timeStamp + ".xml";
                    smpfile.setFileName(fileName);

                    LOGGER.debug("\n****Type Redirect");
                    smpconverter.convertToXml(smpfile.getType().name(), /*0,*/ null, null, null, null, null, null, null, null, null, null,
                            smpfile.getFileName(), null, null, smpfile.getCertificateUID(), smpfile.getHref());
                    break;
            }
        }

        smpfile.setGeneratedFile(smpconverter.getFile());
        boolean valid = xmlValidator.validator(smpfile.getGeneratedFile().getPath());
        if (valid) {
            LOGGER.debug("\n****VALID XML File");
        } else {
            LOGGER.error("\n****NOT VALID XML File");
            smpfile.getGeneratedFile().deleteOnExit();
            String message = env.getProperty("error.file.xsd"); //messages.properties
            redirectAttributes.addFlashAttribute("alert", new Alert(message, Alert.alertType.danger));
            return "redirect:/smpeditor/newsmpfile";
        }

        return "redirect:savesmpfile";
    }

    /**
     * Generate savesmpfile page
     *
     * @param smpfile
     * @param model
     * @return
     */
    @RequestMapping(value = "smpeditor/savesmpfile", method = RequestMethod.GET)
    public String saveFile(@ModelAttribute("smpfile") SMPFile smpfile, Model model) {
        LOGGER.debug("\n==== in saveFile ====");
        model.addAttribute("smpfile", smpfile);
        return "/smpeditor/savesmpfile";

    }

    /**
     * Download SMPFile
     *
     * @param smpfile
     * @param request
     * @param response
     * @param model
     */
    @RequestMapping(value = "smpeditor/savesmpfile/download", method = RequestMethod.GET)
    public void downloadFile(@ModelAttribute("smpfile") SMPFile smpfile, HttpServletRequest request,
                             HttpServletResponse response, Model model) {
        LOGGER.debug("\n==== in downloadFile ====");
        model.addAttribute("smpfile", smpfile);

        response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment; filename=" + smpfile.getFileName());
        response.setContentLength((int) smpfile.getGeneratedFile().length());
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(smpfile.getGeneratedFile()))) {

            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (FileNotFoundException ex) {
            LOGGER.error("\n FileNotFoundException - " + SimpleErrorHandler.printExceptionStackTrace(ex));
        } catch (IOException ex) {
            LOGGER.error("\n IOException - " + SimpleErrorHandler.printExceptionStackTrace(ex));
        }
    }

    /**
     * Sign Generated SMPFile
     *
     * @param smpfile
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/smpeditor/savesmpfile/sign")
    public String signFile(@ModelAttribute("smpfile") SMPFile smpfile, final RedirectAttributes redirectAttributes) {
        LOGGER.debug("\n==== in Generate signFile ====");
        redirectAttributes.addFlashAttribute("smpfile", smpfile);
        return "redirect:/smpeditor/signsmpfile/generated";
    }


    /**
     * Clean SMPFile - clean the generated xml file in server
     *
     * @param smpfile
     * @param model
     * @return
     */
    @RequestMapping(value = "smpeditor/smpeditor/clean", method = RequestMethod.GET)
    public String cleanSmpFile(@ModelAttribute("smpfile") SMPFile smpfile, Model model) {
        LOGGER.debug("\n==== in deletedFile ====");
        model.addAttribute("smpfile", smpfile);
        if (smpfile.getGeneratedFile() != null) {
            LOGGER.debug("\n****DELETED ? " + smpfile.getGeneratedFile().delete());
        }
        return "redirect:/smpeditor/smpeditor";
    }

    /**
     * Clean SMPFile - clean the generated xml file in server
     *
     * @param smpfile
     * @param model
     * @return
     */
    @RequestMapping(value = "smpeditor/newsmpfile/clean", method = RequestMethod.GET)
    public String cleanFile(@ModelAttribute("smpfile") SMPFile smpfile, Model model) {
        LOGGER.debug("\n==== in deleteFile ====");
        model.addAttribute("smpfile", smpfile);
        if (smpfile.getGeneratedFile() != null) {
            LOGGER.debug("\n****DELETED ? " + smpfile.getGeneratedFile().delete());
        }
        return "redirect:/smpeditor/newsmpfile";
    }
}
