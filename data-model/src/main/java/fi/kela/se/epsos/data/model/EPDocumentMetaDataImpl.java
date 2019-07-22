package fi.kela.se.epsos.data.model;

@SuppressWarnings({"unused", "WeakerAccess"})
public class EPDocumentMetaDataImpl extends EPSOSDocumentMetaDataImpl implements EPDocumentMetaData {

    private ProductMetadata product;

    private boolean dispensable;

    public EPDocumentMetaDataImpl(EPSOSDocumentMetaData metaData) {
        this(metaData, null);
    }

    public EPDocumentMetaDataImpl(EPSOSDocumentMetaData metaData, ProductMetadata product) {
        this(metaData, product, false);
    }

    public EPDocumentMetaDataImpl(EPSOSDocumentMetaData metaData, boolean dispensable) {
        this(metaData, null, dispensable);
    }

    public EPDocumentMetaDataImpl(EPSOSDocumentMetaData metaData, ProductMetadata product, boolean dispensable) {
        super(metaData);
        this.product = product;
        this.dispensable = dispensable;
    }

    @Override
    public ProductMetadata getProduct() {
        return product;
    }

    @Override
    public boolean isDispensable() {
        return dispensable;
    }

    public static class SimpleProductMetadata implements ProductMetadata {

        private String productCode;

        private String productName;

        public SimpleProductMetadata(String productCode, String productName) {
            this.productCode = productCode;
            this.productName = productName;
        }

        @Override
        public String getProductCode() {
            return productCode;
        }

        @Override
        public String getProductName() {
            return productName;
        }
    }
}
