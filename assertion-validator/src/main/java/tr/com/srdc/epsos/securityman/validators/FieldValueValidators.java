package tr.com.srdc.epsos.securityman.validators;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.epsos.securityman.exceptions.InvalidFieldException;

public class FieldValueValidators {

    public static final Logger LOGGER = LoggerFactory.getLogger(FieldValueValidators.class);

    private static final int CONDITIONS_SECOND_RANGE = 60; // a tolerance on the clock (second)
    private static final int HCP_MAXIMUM_TIME_SPAN = 4; // maximum timespan for hcp identity assertion (hours)
    private static final int TRC_MAXIMUM_TIME_SPAN = 2; // maximum timespan for trc assertion (hours)

    private FieldValueValidators() {
    }

    public static void validateVersionValue(Assertion assertion) throws InvalidFieldException {
        if (assertion.getVersion().getMajorVersion() != 2 || assertion.getVersion().getMinorVersion() != 0) {
            throw (new InvalidFieldException("Version must be 2.0"));
        }
    }

    public static void validateIssuerValue(Assertion assertion) throws InvalidFieldException {
        if (assertion.getIssuer().getValue() == null) {
            throw (new InvalidFieldException("Issuer should be filled."));
        } else {
            LOGGER.info("Issuer	: " + assertion.getIssuer().getValue());
        }
    }

    public static void validateNameIDValue(Assertion assertion) throws InvalidFieldException {
        if (assertion.getSubject().getNameID().getValue() == null) {
            throw (new InvalidFieldException("NameID should be filled."));
        } else {
            LOGGER.info("Subject Name ID	: " + assertion.getSubject().getNameID().getValue());
        }
    }

    public static void validateMethodValue(Assertion assertion) throws InvalidFieldException {
        if (!assertion.getSubject().getSubjectConfirmations().get(0).getMethod().equals("urn:oasis:names:tc:SAML:2.0:cm:sender-vouches")) {
            throw (new InvalidFieldException("Method must be 'urn:oasis:names:tc:SAML:2.0:cm:sender-vouches'"));
        }
    }

    public static void validateNotBeforeValue(Assertion assertion) throws InvalidFieldException {
        DateTime dtNow = new DateTime().toDateTime(DateTimeZone.forID("UTC"));
        if (assertion.getConditions().getNotBefore().isAfter(dtNow.plusSeconds(CONDITIONS_SECOND_RANGE))) {
            throw (new InvalidFieldException("The assertion has been issued in the future. Current time in server is: " + dtNow + " However, the starting time of your assertion is: " + assertion.getConditions().getNotBefore()));
        }
    }

    public static void validateNotOnOrAfterValue(Assertion assertion) throws InvalidFieldException {
        DateTime dtNow = new DateTime().toDateTime(DateTimeZone.forID("UTC"));
        if (assertion.getConditions().getNotOnOrAfter().isBefore(dtNow.minusSeconds(CONDITIONS_SECOND_RANGE))) {
            throw (new InvalidFieldException("The assertion is not valid now. Current time in server is: " + dtNow + " However, the ending time of your assertion is: " + assertion.getConditions().getNotOnOrAfter()));
        }
    }

    public static void validateTimeSpanForHCP(Assertion assertion) throws InvalidFieldException {
        if (assertion.getConditions().getNotBefore().isBefore(assertion.getConditions().getNotOnOrAfter().minusHours(HCP_MAXIMUM_TIME_SPAN))) {
            throw (new InvalidFieldException("Maximum time span for HCP Identity Assertion can be at most 4 hours."));
        }
    }

    public static void validateTimeSpanForTRC(Assertion assertion) throws InvalidFieldException {
        if (assertion.getConditions().getNotBefore().isBefore(assertion.getConditions().getNotOnOrAfter().minusHours(TRC_MAXIMUM_TIME_SPAN))) {
            throw (new InvalidFieldException("Maximum time span for TRC Assertion can be at most 2 hours."));
        }
    }

    public static void validateAuthnContextClassRefValueForHCP(Assertion assertion) throws InvalidFieldException {
        if (assertion.getAuthnStatements().get(0).getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef() == null) {
            throw (new InvalidFieldException("AuthnContextClassRef should be filled."));
        }
    }

    public static void validateAuthnContextClassRefValueForTRC(Assertion assertion) throws InvalidFieldException {
        if (assertion.getAuthnStatements().get(0).getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef() == null) {
            throw (new InvalidFieldException("AuthnContextClassRef must be 'urn:oasis:names:tc:SAML:2.0:ac:classes:PreviousSession'"));
        }
        if (!assertion.getAuthnStatements().get(0).getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef().equals("urn:oasis:names:tc:SAML:2.0:ac:classes:PreviousSession")) {
            throw (new InvalidFieldException("AuthnContextClassRef must be 'urn:oasis:names:tc:SAML:2.0:ac:classes:PreviousSession'"));
        }
    }

    public static void validateAssertionIdRefValue(Assertion assertion) throws InvalidFieldException {
        if (assertion.getAdvice().getAssertionIDReferences().get(0).equals("")) {
            throw (new InvalidFieldException("AssertionIdRef should be filled."));
        }
    }
}
