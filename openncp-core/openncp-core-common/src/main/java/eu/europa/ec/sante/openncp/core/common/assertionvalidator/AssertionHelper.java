package eu.europa.ec.sante.openncp.core.common.assertionvalidator;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;

import eu.europa.ec.sante.openncp.core.common.assertionvalidator.exceptions.InsufficientRightsException;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.exceptions.MissingFieldException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssertionHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssertionHelper.class);
    private static final String ERROR_MESSAGE = "{0} - attribute shall contain AttributeValue element.";

    private AssertionHelper() {
    }

    /**
     * Get attribute value from assertion.
     *
     * @param assertion     the assertion
     * @param attributeName the attribute to search for
     * @return the attribute value
     * @throws MissingFieldException If attribute is missing
     */
    public static String getAttributeFromAssertion(final Assertion assertion, final String attributeName) throws MissingFieldException {

        for (final AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
            for (final Attribute attribute : attributeStatement.getAttributes()) {
                if (StringUtils.equals(attribute.getName(), attributeName)) {
                    if (!attribute.getAttributeValues().isEmpty()) {
                        return attribute.getAttributeValues().get(0).getDOM().getTextContent();
                    } else {
                        final String errorMessage = MessageFormat.format(ERROR_MESSAGE, attributeName);
                        throw new MissingFieldException(errorMessage);
                    }
                }
            }
        }
        final String errorMessage = MessageFormat.format(ERROR_MESSAGE, attributeName);
        throw new MissingFieldException(errorMessage);
    }

    /**
     * Get attribute values from assertion.
     *
     * @param assertion     the assertion
     * @param attributeName the attribute to search for
     * @return the attribute values
     * @throws MissingFieldException If attribute is missing
     */
    public static List<XMLObject> getAttributeValuesFromAssertion(final Assertion assertion, final String attributeName)
            throws MissingFieldException {

        for (final AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
            for (final Attribute attribute : attributeStatement.getAttributes()) {
                if (StringUtils.equals(attribute.getName(), attributeName)) {
                    return attribute.getAttributeValues();
                }
            }
        }
        final String errorMessage = MessageFormat.format(ERROR_MESSAGE, attributeName);
        throw new MissingFieldException(errorMessage);
    }

    /**
     * Returns the HL7 permissions list associated to the SAML assertion in parameter.
     *
     * @param assertion - HCP assertions
     * @return List of HL7 permissions.
     * @throws InsufficientRightsException - When no permissions are provided, HCP is not authorized as consequence.
     */
    public static List<XMLObject> getPermissionValuesFromAssertion(final Assertion assertion) throws InsufficientRightsException {

        try {
            return getAttributeValuesFromAssertion(assertion, AssertionConstants.URN_OASIS_NAMES_TC_XSPA_1_0_SUBJECT_HL7_PERMISSION);
        } catch (final MissingFieldException e) {
            // this is to get the behavior as before...
            LOGGER.error("InsufficientRightsException: '{}'", e.getMessage(), e);
            throw new InsufficientRightsException();
        }
    }

    public static String getPurposeOfUseFromAssertion(final Assertion assertion, final String attributeName) throws MissingFieldException {

        for (final AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
            for (final Attribute attribute : attributeStatement.getAttributes()) {
                if (StringUtils.equals(attribute.getName(), attributeName)) {
                    if (!attribute.getAttributeValues().isEmpty()) {
                        //return attribute.getAttributeValues().get(0).getDOM().getTextContent();
                        return attribute.getAttributeValues()
                                        .get(0)
                                        .getDOM()
                                        .getElementsByTagName("PurposeOfUse")
                                        .item(0)
                                        .getAttributes()
                                        .getNamedItem("code")
                                        .getTextContent();
                    } else {
                        final String errorMessage = MessageFormat.format(ERROR_MESSAGE, attributeName);
                        throw new MissingFieldException(errorMessage);
                    }
                }
            }
        }
        final String errorMessage = MessageFormat.format(ERROR_MESSAGE, attributeName);
        throw new MissingFieldException(errorMessage);
    }

    public static String getRoleFromAssertion(final Assertion assertion, final String attributeName) throws MissingFieldException {

        for (final AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
            for (final Attribute attribute : attributeStatement.getAttributes()) {
                if (StringUtils.equals(attribute.getName(), attributeName)) {
                    if (!attribute.getAttributeValues().isEmpty()) {
                        //return attribute.getAttributeValues().get(0).getDOM().getTextContent();
                        return attribute.getAttributeValues()
                                        .get(0)
                                        .getDOM()
                                        .getElementsByTagName("Role")
                                        .item(0)
                                        .getAttributes()
                                        .getNamedItem("code")
                                        .getTextContent();
                    } else {
                        final String errorMessage = MessageFormat.format(ERROR_MESSAGE, attributeName);
                        throw new MissingFieldException(errorMessage);
                    }
                }
            }
        }
        final String errorMessage = MessageFormat.format(ERROR_MESSAGE, attributeName);
        throw new MissingFieldException(errorMessage);
    }

    public static String getRoleNameFromAssertion(final Assertion assertion, final String attributeName) throws MissingFieldException {

        for (final AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
            for (final Attribute attribute : attributeStatement.getAttributes()) {
                if (StringUtils.equals(attribute.getName(), attributeName)) {
                    if (!attribute.getAttributeValues().isEmpty()) {
                        //return attribute.getAttributeValues().get(0).getDOM().getTextContent();
                        return attribute.getAttributeValues()
                                        .get(0)
                                        .getDOM()
                                        .getElementsByTagName("Role")
                                        .item(0)
                                        .getAttributes()
                                        .getNamedItem("displayName")
                                        .getTextContent();
                    } else {
                        final String errorMessage = MessageFormat.format(ERROR_MESSAGE, attributeName);
                        throw new MissingFieldException(errorMessage);
                    }
                }
            }
        }
        final String errorMessage = MessageFormat.format(ERROR_MESSAGE, attributeName);
        throw new MissingFieldException(errorMessage);
    }

    public static boolean isExpired(final Assertion assertion) {

        if (assertion.getConditions().getNotBefore() != null && assertion.getConditions().getNotBefore().isAfter(DateTime.now())) {
            return true;
        }

        return assertion.getConditions().getNotOnOrAfter() != null && (assertion.getConditions().getNotOnOrAfter().isBefore(DateTime.now()) ||
                                                                       assertion.getConditions().getNotOnOrAfter().equals(Instant.now()));
    }
}
