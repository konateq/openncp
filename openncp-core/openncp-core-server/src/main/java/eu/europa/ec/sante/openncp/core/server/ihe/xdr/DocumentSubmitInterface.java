package eu.europa.ec.sante.openncp.core.server.ihe.xdr;

import eu.europa.ec.sante.openncp.core.common.NationalConnectorInterface;
import eu.europa.ec.sante.openncp.core.common.assertionvalidator.exceptions.InsufficientRightsException;
import eu.europa.ec.sante.openncp.core.common.datamodel.DiscardDispenseDetails;
import eu.europa.ec.sante.openncp.core.common.datamodel.xds.EPSOSDocument;
import eu.europa.ec.sante.openncp.core.common.exception.DocumentProcessingException;
import eu.europa.ec.sante.openncp.core.common.exception.NIException;

/**
 * Interface for XDR document submit service implementation
 */
public interface DocumentSubmitInterface extends NationalConnectorInterface {

    /**
     * Stores a dispensation in the national infrastructure
     *
     * @param dispensationDocument - eDispensation document in epSOS pivot (CDA) form
     */
    void submitDispensation(EPSOSDocument dispensationDocument) throws NIException, InsufficientRightsException;

    /**
     * Discards a previously submitted dispensation
     *
     * @param dispensationToDiscard - Metadata of the dispensation to be discarded (XML and PDF versions)
     */
    void cancelDispensation(DiscardDispenseDetails discardDispenseDetails, EPSOSDocument dispensationToDiscard) throws NIException, InsufficientRightsException;

    /**
     * Stores a patient consent in the national infrastructure
     *
     * @param consentDocument - patient consent document in epSOS pivot (CDA) form
     */
    void submitPatientConsent(EPSOSDocument consentDocument) throws NIException, InsufficientRightsException;

    /**
     * Stores a HCER document in the national infrastructure
     *
     * @param hcerDocument - HCER document in epSOS pivot (CDA) form
     */
    void submitHCER(EPSOSDocument hcerDocument) throws DocumentProcessingException;
}
