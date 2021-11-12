package fi.kela.se.epsos.data.model;

/**
 * EPrescriptionDocumentMetaData interface
 *
 * @author mimyllyv
 */
public interface EPDocumentMetaData extends EPSOSDocumentMetaData {

    String getDescription();

    ProductMetadata getProduct();

    default boolean hasProduct() {
        return getProduct() != null;
    }

    boolean isDispensable();

    String getAtcCode();
    String getAtcName();
    String getDoseFormCode();
    String getDoseFormName();
    String getStrength();
    String getSubstitution();

    interface ProductMetadata {

        String getProductCode();

        String getProductName();
    }
}
