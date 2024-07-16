package eu.europa.ec.sante.openncp.core.common.ihe.evidence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class ObligationHandlerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObligationHandlerFactory.class);
    private static ObligationHandlerFactory instance = null;

    private ObligationHandlerFactory() {
        LOGGER.debug("In the ObligationHandlerFactory constructor");
    }

    public static synchronized ObligationHandlerFactory getInstance() {

        if (instance == null) {
            try {
                instance = new ObligationHandlerFactory();
            } catch (final Exception e) {
                LOGGER.error("Exception: '{}'", e.getMessage(), e);
                throw new IllegalStateException("Unable to instantiate the ObligationHandlerFactory: " + e.getMessage(), e);
            }
        }
        return instance;
    }

    /**
     * Dispatch the correct obligation handler based, on the family of message types
     *
     * @param messageType
     * @param obligations
     * @return
     * @throws ObligationDischargeException
     */
    public List<ObligationHandler> createHandler(final MessageType messageType, final List<ESensObligation> obligations, final Context context)
            throws ObligationDischargeException {

        if (messageType == null) {
            throw new ObligationDischargeException("Message Type is null");
        }

        final int size = obligations.size();

        final LinkedList<ObligationHandler> list = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            final ESensObligation obligation = obligations.get(i);
            final String obligationId = obligation.getObligationID();

            // Here it is static, but it will be a factory upon a configuration file
            switch (obligationId) {
                case "urn:eSENS:obligations:nrr:ATNA":
                    list.add(new ATNAObligationHandler(messageType, obligations, context));
                    break;
                case "urn:eSENS:obligations:nrr:ETSIREM":
                case "urn:eSENS:obligations:nro:ETSIREM":
                case "urn:eSENS:obligations:nrd:ETSIREM":
                    list.add(new ETSIREMObligationHandler(messageType, obligations, context));
                    break;
                default:
                    list.add(new NullObligationHandler());
                    break;
            }
        }
        return list;
    }
}
