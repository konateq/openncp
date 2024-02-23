package eu.europa.ec.sante.openncp.transformation.exception;

import eu.europa.ec.sante.openncp.tsam.error.ITMTSAMError;
import eu.europa.ec.sante.openncp.tsam.error.TMError;
import eu.europa.ec.sante.openncp.tsam.error.TMErrorCtx;

public class TMException extends Exception {

    private ITMTSAMError reason;

    public TMException(TMError reason) {
        this.reason = reason;
    }

    public TMException(TMErrorCtx reason) {
        this.reason = reason;
    }


    public ITMTSAMError getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return reason.getCode() + ": " + reason.getDescription();
    }
}