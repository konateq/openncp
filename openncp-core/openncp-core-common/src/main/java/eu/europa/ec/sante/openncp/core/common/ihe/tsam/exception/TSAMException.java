package eu.europa.ec.sante.openncp.core.common.ihe.tsam.exception;

import eu.europa.ec.sante.openncp.core.common.ihe.tsam.error.TSAMError;

public class TSAMException extends Exception {

    private TSAMError reason;
    private String ctx;

    public TSAMException(TSAMError reason) {
        this.reason = reason;
    }

    public TSAMException(TSAMError reason, String ctx) {
        this.reason = reason;
        this.ctx = ctx;
    }

    public TSAMError getReason() {
        return reason;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(reason.getCode());
        stringBuilder.append(": ");
        stringBuilder.append(reason.getDescription());
        if (ctx != null) {
            stringBuilder.append("[ ");
            stringBuilder.append(ctx);
            stringBuilder.append(" ]");
        }
        return stringBuilder.toString();
    }
}
