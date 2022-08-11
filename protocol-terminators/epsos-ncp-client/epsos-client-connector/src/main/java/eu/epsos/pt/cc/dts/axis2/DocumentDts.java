package eu.epsos.pt.cc.dts.axis2;

import epsos.openncp.protocolterminator.clientconnector.Author;
import epsos.openncp.protocolterminator.clientconnector.EpsosDocument1;
import epsos.openncp.protocolterminator.clientconnector.ReasonOfHospitalisation;
import eu.europa.ec.sante.ehdsi.constant.ClassCode;
import fi.kela.se.epsos.data.model.OrCDDocumentMetaData;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType.DocumentResponse;
import org.apache.commons.lang3.StringUtils;
import tr.com.srdc.epsos.data.model.xds.XDSDocument;
import tr.com.srdc.epsos.data.model.xds.XDSDocumentAssociation;
import tr.com.srdc.epsos.util.Constants;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This is a Data Transformation Service providing functions to transform data into a Document object.
 *
 * @author Marcelo Fonseca - <marcelo.fonseca@iuz.pt>
 * @author Luís Pinto<code> - luis.pinto@iuz.pt</code>
 */
public class DocumentDts {

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
    public static EpsosDocument1 newInstance(XDSDocument document) {

        if (document == null) {
            return null;
        }
        final EpsosDocument1 result = EpsosDocument1.Factory.newInstance();
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
            result.setAuthorsArray(convertAuthorList(document.getAuthors()));
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

    private static String composeCodeAndText(String code, String text) {
        String label = code;
        if (StringUtils.isNotBlank(text)) {
            label = "[" + code + "] " + text;
        }
        return label;
    }

    private static Author[] convertAuthorList(List<OrCDDocumentMetaData.Author> authors) {

        var convertedAuthors = new Author[authors.size()];
        for (var i = 0; i < authors.size(); i++) {
            var author = authors.get(i);
            String authorPerson = author.getAuthorPerson();
            String[] authorSpecialities = null;
            if (author.getAuthorSpeciality() != null) {
                authorSpecialities = author.getAuthorSpeciality().toArray(new String[author.getAuthorSpeciality().size()]);
            }
            var convertedAuthor = Author.Factory.newInstance();
            convertedAuthor.setPerson(authorPerson);
            convertedAuthor.setSpecialtyArray(authorSpecialities);
            convertedAuthors[i] = convertedAuthor;
        }
        return convertedAuthors;
    }

    private static ReasonOfHospitalisation convertReasonOfHospitalisation(OrCDDocumentMetaData.ReasonOfHospitalisation reasonOfHospitalisation) {

        var convertedReasonOfHospitalisation = ReasonOfHospitalisation.Factory.newInstance();
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
    public static EpsosDocument1[] newInstance(List<XDSDocumentAssociation> documentAssociation) {

        if (documentAssociation == null || documentAssociation.isEmpty()) {
            return new EpsosDocument1[0];
        }

        List<EpsosDocument1> resultList = new ArrayList<>();

        for (XDSDocumentAssociation doc : documentAssociation) {
            EpsosDocument1 xmlDoc = DocumentDts.newInstance(doc.getCdaXML());
            EpsosDocument1 pdfDoc = DocumentDts.newInstance(doc.getCdaPDF());

            //  If CDA L1 and L3 are existing then we shall create an association between the 2 documents.
            if (xmlDoc != null && pdfDoc != null) {
                pdfDoc.setAssociatedDocumentsArray(new EpsosDocument1[]{xmlDoc});
                xmlDoc.setAssociatedDocumentsArray(new EpsosDocument1[]{pdfDoc});
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
        return resultList.toArray(new EpsosDocument1[resultList.size()]);
    }

    /**
     * Converts a DocumentResponse into a Document new instance.
     *
     * @param documentResponse the document to be converted.
     * @return the result of the conversion, as a Document.
     */
    public static EpsosDocument1 newInstance(DocumentResponse documentResponse) {

        if (documentResponse == null) {
            return null;
        }
        final EpsosDocument1 result = EpsosDocument1.Factory.newInstance();
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
     * @return a Calendar instance, with the given String values.
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

        DateFormat formatter;
        Date date;
        Calendar cal;

        formatter = new SimpleDateFormat(selectedPattern);
        try {
            date = formatter.parse(dateString);
            cal = Calendar.getInstance();
            cal.setTime(date);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }

        return cal;
    }
}
