package eu.europa.ec.sante.openncp.core.client.ihe.dts;

import eu.europa.ec.sante.openncp.common.ClassCode;
import eu.europa.ec.sante.openncp.common.configuration.util.Constants;
import eu.europa.ec.sante.openncp.core.client.Author;
import eu.europa.ec.sante.openncp.core.client.EpsosDocument;
import eu.europa.ec.sante.openncp.core.client.ObjectFactory;
import eu.europa.ec.sante.openncp.core.client.ReasonOfHospitalisation;
import eu.europa.ec.sante.openncp.core.client.ihe.xca.RespondingGateway_ServiceStub;
import eu.europa.ec.sante.openncp.core.common.datamodel.xds.OrCDDocumentMetaData;
import eu.europa.ec.sante.openncp.core.common.datamodel.xds.XDSDocument;
import eu.europa.ec.sante.openncp.core.common.datamodel.xds.XDSDocumentAssociation;
import eu.europa.ec.sante.openncp.core.common.datamodel.xsd.ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is a Data Transformation Service providing functions to transform data into a Document object.
 */
public class DocumentDts {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentDts.class);
    static final ObjectFactory objectFactory = new ObjectFactory();

    /**
     * Private constructor to disable class instantiation.
     */
    private DocumentDts() {
    }

    /**
     * Converts a XDSDocument into a Document.
     *
     * @param document the document to be converted.
     * @return the result of the conversion, as a Document.
     */
    public static EpsosDocument newInstance(XDSDocument document) {

        if (document == null) {
            return null;
        }
        final EpsosDocument result = objectFactory.createEpsosDocument();
        result.setUuid(document.getDocumentUniqueId());
        result.setDescription(document.getDescription());
        result.setCreationDate(convertDate(document.getCreationTime()));
        result.setEventDate(convertDate(document.getEventTime()));
        result.setClassCode(GenericDocumentCodeDts.newInstance(document.getClassCode()));
        result.setFormatCode(GenericDocumentCodeDts.newInstance(document.getFormatCode()));
        result.setRepositoryId(document.getRepositoryUniqueId());
        result.setHcid(document.getHcid());
        if (!StringUtils.isEmpty(document.getSize())) {
            result.setSize(new BigInteger(document.getSize()));
        }
        result.setMimeType(document.getMimeType());
        if (document.getAuthors() != null) {
            result.getAuthors().addAll(convertAuthorList(document.getAuthors()));
        }
        if (document.getReasonOfHospitalisation() != null) {
            result.setReasonOfHospitalisation(convertReasonOfHospitalisation(document.getReasonOfHospitalisation()));
        }

        result.setAtcCode(document.getAtcCode());
        result.setAtcText(document.getAtcText());
        result.setDispensable(document.isDispensable());
        result.setDoseFormCode(document.getDoseFormCode());
        result.setDoseFormText(document.getDoseFormText());
        result.setStrength(document.getStrength());
        result.setSubstitution(document.getSubstitution());

        if (result.getClassCode() != null && !result.getClassCode().getNodeRepresentation().isEmpty()) {
            var classCode = result.getClassCode().getNodeRepresentation();
            switch (ClassCode.getByCode(classCode)) {
                case PS_CLASSCODE:
                    result.setTitle(Constants.PS_TITLE);
                    break;
                case EP_CLASSCODE:
                    result.setTitle(Constants.EP_TITLE);
                    break;
                case ED_CLASSCODE:
                    result.setTitle(Constants.ED_TITLE);
                    break;
                case ORCD_HOSPITAL_DISCHARGE_REPORTS_CLASSCODE:
                    result.setTitle(Constants.ORCD_HOSPITAL_DISCHARGE_REPORTS_TITLE);
                    break;
                case ORCD_LABORATORY_RESULTS_CLASSCODE:
                    result.setTitle(Constants.ORCD_LABORATORY_RESULTS_TITLE);
                    break;
                case ORCD_MEDICAL_IMAGING_REPORTS_CLASSCODE:
                    result.setTitle(Constants.ORCD_MEDICAL_IMAGING_REPORTS_TITLE);
                    break;
                case ORCD_MEDICAL_IMAGES_CLASSCODE:
                    result.setTitle(Constants.ORCD_MEDICAL_IMAGES_TITLE);
                    break;
                default:
                    // Document Type not supported
                    result.setTitle(Constants.UNKNOWN_TITLE);
                    break;
            }
        }

        return result;
    }

    private static List<Author> convertAuthorList(List<OrCDDocumentMetaData.Author> authors) {
        return authors.stream().map(author -> {
            var convertedAuthor = objectFactory.createAuthor();

            String authorPerson = author.getAuthorPerson();
            convertedAuthor.setPerson(authorPerson);
            if (author.getAuthorSpeciality() != null) {
                convertedAuthor.getSpecialty().addAll(author.getAuthorSpeciality());
            }
            return convertedAuthor;
        }).collect(Collectors.toList());
    }

    private static ReasonOfHospitalisation convertReasonOfHospitalisation(OrCDDocumentMetaData.ReasonOfHospitalisation reasonOfHospitalisation) {

        var convertedReasonOfHospitalisation = objectFactory.createReasonOfHospitalisation();
        convertedReasonOfHospitalisation.setCode(reasonOfHospitalisation.getCode());
        convertedReasonOfHospitalisation.setText(reasonOfHospitalisation.getText());
        return convertedReasonOfHospitalisation;
    }

    /**
     * Converts a list of XDSDocument to a list of Document.
     *
     * @param documentAssociation the list of XDSDocument.
     * @return the result of the conversion, as a list of Document.
     */
    public static List<EpsosDocument> newInstance(List<XDSDocumentAssociation> documentAssociation) {

        List<EpsosDocument> resultList = new ArrayList<>();
        if (documentAssociation == null || documentAssociation.isEmpty()) {
            return resultList;
        }

        for (XDSDocumentAssociation doc : documentAssociation) {
            EpsosDocument xmlDoc = DocumentDts.newInstance(doc.getCdaXML());
            EpsosDocument pdfDoc = DocumentDts.newInstance(doc.getCdaPDF());

            //  If CDA L1 and L3 are existing then we shall create an association between the 2 documents.
            if (xmlDoc != null && pdfDoc != null) {
                pdfDoc.getAssociatedDocuments().add(DocumentDts.newInstance(doc.getCdaXML()));
                xmlDoc.getAssociatedDocuments().add(DocumentDts.newInstance(doc.getCdaPDF()));
            }

            // Adding the reference to the L1 CDA document
            if (pdfDoc != null) {
                resultList.add(pdfDoc);
            }

            // Adding the reference to the L3 CDA document
            if (xmlDoc != null) {
                resultList.add(xmlDoc);
            }
        }
        return resultList;
    }

    /**
     * Converts a DocumentResponse into a Document new instance.
     *
     * @param documentResponse the document to be converted.
     * @return the result of the conversion, as a Document.
     */
    public static EpsosDocument newInstance(RetrieveDocumentSetResponseType.DocumentResponse documentResponse) {

        if (documentResponse == null) {
            return null;
        }
        final EpsosDocument result = objectFactory.createEpsosDocument();
        result.setHcid(documentResponse.getHomeCommunityId());
        result.setUuid(documentResponse.getDocumentUniqueId());
        result.setMimeType(documentResponse.getMimeType());
        result.setRepositoryId(documentResponse.getRepositoryUniqueId());
        result.setBase64Binary(documentResponse.getDocument());

        return result;
    }

    /**
     * Converts a string containing a date in the yyyyMMddHHmmss format to a Calendar instance.
     *
     * @param dateString a String representation of the Date.
     * @return an XMLGregorianCalendar instance, with the given String values.
     */
    private static Calendar convertDate(String dateString) {


        var pattern1 = "yyyyMMddHHmmss";
        var pattern2 = "yyyyMMdd";
        String selectedPattern;

        if (dateString != null) {
            if (dateString.length() == pattern1.length()) {
                selectedPattern = pattern1;
            } else if (dateString.length() == pattern2.length()) {
                selectedPattern = pattern2;
            } else {
                return null;
            }
        } else {
            return null;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(selectedPattern);
        try {
            Date date = simpleDateFormat.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}