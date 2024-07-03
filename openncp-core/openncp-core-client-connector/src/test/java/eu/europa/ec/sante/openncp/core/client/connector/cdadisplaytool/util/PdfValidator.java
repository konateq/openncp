package eu.europa.ec.sante.openncp.core.client.connector.cdadisplaytool.util;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Assert;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

public class PdfValidator extends AbstractValidator {

    @Override
    protected void validateActiveIngredients(final XPath xpath, final Document cdaDoc, final Document resultDoc) {

    }

    @Override
    protected void validateTitle(final XPath xpath, final Document cdaDoc, final Document resultDoc) throws XPathExpressionException {
        final String titleCda = (String) xpath.evaluate("/*[local-name()='ClinicalDocument']/*[local-name()='title']/text()",
                cdaDoc, XPathConstants.STRING);
        final String titleHtml = (String) xpath.evaluate("/html/body/h1/text()",
                resultDoc, XPathConstants.STRING);
        Assert.assertEquals(titleCda, titleHtml);
    }

    @Override
    protected void validatePatientName(final XPath xpath, final Document cdaDoc, final Document resultDoc) throws XPathExpressionException {
        final String patientNameCda = xpath.evaluate("/*[local-name()='ClinicalDocument']/*[local-name()='recordTarget']/*[local-name()='patientRole']/*[local-name()='patient']/*[local-name()='name']/*[local-name()='given']/text()",
                cdaDoc, XPathConstants.STRING) +
                " " +
                xpath.evaluate("/*[local-name()='ClinicalDocument']/*[local-name()='recordTarget']/*[local-name()='patientRole']/*[local-name()='patient']/*[local-name()='name']/*[local-name()='family']/text()",
                        cdaDoc, XPathConstants.STRING);
        final String patientNameHtml = StringEscapeUtils.unescapeHtml4(((String) xpath.evaluate("((((/html/body/table[@class='header_table']/tbody/tr)[3]/table[@class='header_table']/tbody/tr)[2]/td)[3])/text()",
                resultDoc, XPathConstants.STRING)).trim() +
                " " +
                ((String) xpath.evaluate("((((/html/body/table[@class='header_table']/tbody/tr)[3]/table[@class='header_table']/tbody/tr)[2]/td)[2])/text()",
                        resultDoc, XPathConstants.STRING)).trim());
        Assert.assertEquals(patientNameCda, patientNameHtml);
    }
}