package eu.europa.ec.sante.openncp.core.common.eadc;

public interface EadcReceiver {

    void process(EadcEntry entry) throws Exception;

    void processFailure(EadcEntry entry, String errorDescription) throws Exception;
}
