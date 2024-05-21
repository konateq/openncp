package eu.europa.ec.sante.openncp.core.common.ihe.eadc;

public interface EadcReceiver {

    void process(EadcEntry entry) throws Exception;

    void processFailure(EadcEntry entry, String errorDescription) throws Exception;
}
