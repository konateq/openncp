package eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xds;

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

    SubstitutionMetaData getSubstitution();

    interface SubstitutionMetaData {

        String getSubstitutionCode();

        String getSubstitutionDisplayName();
    }

    interface ProductMetadata {

        String getProductCode();

        String getProductName();
    }
}
