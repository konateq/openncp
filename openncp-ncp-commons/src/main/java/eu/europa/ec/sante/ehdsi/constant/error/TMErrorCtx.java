package eu.europa.ec.sante.ehdsi.constant.error;

public class TMErrorCtx implements ITMTSAMError {

    /**
     * Exception code
     */
    private String code;
    /**
     * Exception description (issue - is English description/constant enough ?)
     */
    private String description;

    private String context;

    /**
     * Default enum constructor
     *
     * @param error
     * @param ctx
     */
    public TMErrorCtx(TMError error, String ctx) {
        this.code = error.getCode();
        this.description = error.getDescription();
        this.context=ctx;
    }

    public TMErrorCtx() {}

    /**
     *
     * @return String - code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @return String - Description
     */
    public String getDescription() {
        return description +" [" + context + "]";
    }

    @Override
    /**
     * @return String in format code:description
     */
    public String toString() {
        return code + ": " + getDescription();
    }
}
