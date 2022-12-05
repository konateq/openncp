package eu.epsos.validation.datamodel.xd;

import eu.epsos.validation.datamodel.common.ObjectType;
import eu.europa.ec.sante.ehdsi.constant.ClassCode;

/**
 * This enumerator gathers all the models used in the XD* Validator at EVS Client.
 *
 * @author Marcelo Fonseca <marcelo.fonseca@iuz.pt>
 * @deprecated
 */
@Deprecated
public enum XdModel {

    EPSOS_ED_INIT_REQUEST("epSOS DispensationService:initialize - request", ObjectType.XDR_SUBMIT_REQUEST),
    EPSOS_ED_INIT_RESPONSE("epSOS DispensationService:initialize - response", ObjectType.XDR_SUBMIT_RESPONSE),
    EPSOS_ED_DISCARD_REQUEST("epSOS DispensationService:discard - request", ObjectType.XDR_SUBMIT_REQUEST),
    EPSOS_ED_DISCARD_RESPONSE("epSOS DispensationService:discard - response", ObjectType.XDR_SUBMIT_RESPONSE),
    EPSOS_CS_PUT_REQUEST("epSOS ConsentService:put - request", ObjectType.XDR_SUBMIT_REQUEST),
    EPSOS_CS_PUT_RESPONSE("epSOS ConsentService:put - response", ObjectType.XDR_SUBMIT_RESPONSE),
    EPSOS_CS_DISCARD_REQUEST("epSOS ConsentService:discard - request", ObjectType.XDR_SUBMIT_REQUEST),
    EPSOS_CS_DISCARD_RESPONSE("epSOS ConsentService:discard - response", ObjectType.XDR_SUBMIT_RESPONSE),
    EPSOS_OS_LIST_REQUEST_XCF("epSOS OrderService:list - request (V2.2 XCF)", ObjectType.XCF_REQUEST),
    EPSOS_OS_LIST_RESPONSE_XCF("epSOS OrderService:list - response (V2.2 XCF)", ObjectType.XCF_RESPONSE),
    EPSOS_OS_LIST_RESPONSE_XCA("epSOS OrderService:list - response (V1 XCA)", ObjectType.XCA_QUERY_RESPONSE),
    EPSOS_PS_LIST_REQUEST_XCF("epSOS PatientService:list - request (V2.2 XCF)", ObjectType.XCF_REQUEST),
    EPSOS_PS_LIST_RESPONSE_XCF("epSOS PatientService:list - response(V2.2 XCF)", ObjectType.XCF_RESPONSE),
    EPSOS_OS_LIST_REQUEST_XCA("epSOS OrderService : list - request (V1 XCA)", ObjectType.XCA_QUERY_REQUEST),
    EPSOS_PS_LIST_REQUEST_XCA("epSOS PatientService:list - request (V1 XCA)", ObjectType.XCA_QUERY_REQUEST),
    EPSOS_PS_LIST_RESPONSE_XCA("epSOS PatientService:list - response (V1 XCA)", ObjectType.XCA_QUERY_RESPONSE),
    EPSOS_OS_RETRIEVE_REQUEST_XCA("epSOS OrderService:retrieve - request (V1 XCA)", ObjectType.XCA_RETRIEVE_REQUEST),
    EPSOS_OS_RETRIEVE_RESPONSE_XCA("epSOS OrderService:retrieve - response (V1 XCA)", ObjectType.XCA_RETRIEVE_RESPONSE),
    EPSOS_PS_RETRIEVE_REQUEST_XCA("epSOS PatientService:retrieve - request (V1 XCA)", ObjectType.XCA_RETRIEVE_REQUEST),
    EPSOS_PS_RETRIEVE_RESPONSE_XCA("epSOS PatientService:retrieve - response (V1 XCA)", ObjectType.XCA_RETRIEVE_RESPONSE),
    EPSOS2_FETCH_DOC_QUERY_REQUEST("epSOS-2 FetchDocumentService QUERY - request", ObjectType.XCA_QUERY_REQUEST),
    EPSOS2_FETCH_DOC_QUERY_RESPONSE("epSOS-2 FetchDocumentService QUERY - response", ObjectType.XCA_QUERY_RESPONSE),
    EPSOS2_FETCH_DOC_RETRIEVE_REQUEST("epSOS-2 FetchDocumentService RETRIEVE - request", ObjectType.XCA_RETRIEVE_REQUEST),
    EPSOS2_FETCH_DOC_RETRIEVE_RESPONSE("epSOS-2 FetchDocumentService RETRIEVE - response", ObjectType.XCA_RETRIEVE_RESPONSE),
    EPSOS2_PROVIDE_DATA_REQUEST("epSOS-2 ProvideDataService - request", ObjectType.XDR_SUBMIT_REQUEST),
    EPSOS2_PROVIDE_DATA_RESPONSE("epSOS-2 ProvideDataService - response", ObjectType.XDR_SUBMIT_RESPONSE);

    private String name;
    private ObjectType objectType;

    XdModel(String s, ObjectType ot) {
        name = s;
        objectType = ot;
    }

    public static XdModel checkModel(String model) {

        for (XdModel m : XdModel.values()) {
            if (model.equals(m.toString())) {
                return m;
            }
        }

        return null;
    }

    /**
     * This method will look into an XCA message and obtain the proper model to
     * validate it at Gazelle
     *
     * @param message the XCA message to be validated
     * @return the proper model to be used in the validation
     */
    public static XdModel obtainModelXca(String message) {

        final String QUERY_REQUEST = "AdhocQueryRequest";
        final String QUERY_RESPONSE = "AdhocQueryResponse";
        final String RETRIEVE_REQUEST = "RetrieveDocumentSetRequest";
        final String RETRIEVE_RESPONSE = "RetrieveDocumentSetResponse";

        XdModel result = null;

        // Query / List operations
        if (message.contains(QUERY_REQUEST)) { // Request
            if (message.contains(ClassCode.EP_CLASSCODE.getCode())) {
                result = EPSOS_OS_LIST_REQUEST_XCA;
            } else if (message.contains(ClassCode.PS_CLASSCODE.getCode())) {
                result = EPSOS_PS_LIST_REQUEST_XCA;
            } else {
                result = EPSOS2_FETCH_DOC_QUERY_REQUEST;
            }
        } else if (message.contains(QUERY_RESPONSE)) { // Response
            if (message.contains(ClassCode.EP_CLASSCODE.getCode())) {
                result = EPSOS_OS_LIST_RESPONSE_XCA;
            } else if (message.contains(ClassCode.PS_CLASSCODE.getCode())) {
                result = EPSOS_PS_LIST_RESPONSE_XCA;
            } else {
                result = EPSOS2_FETCH_DOC_QUERY_RESPONSE;
            }
        }
        // Retrieve operations
        if (message.contains(RETRIEVE_REQUEST)) {  // Request
            if (message.contains(ClassCode.EP_CLASSCODE.getCode())) {
                result = EPSOS_OS_RETRIEVE_REQUEST_XCA;
            } else if (message.contains(ClassCode.PS_CLASSCODE.getCode())) {
                result = EPSOS_PS_RETRIEVE_REQUEST_XCA;
            } else {
                result = EPSOS2_FETCH_DOC_RETRIEVE_REQUEST;
            }
        } else if (message.contains(RETRIEVE_RESPONSE)) { // Response
            if (message.contains(ClassCode.EP_CLASSCODE.getCode())) {
                result = EPSOS_OS_RETRIEVE_RESPONSE_XCA;
            } else if (message.contains(ClassCode.PS_CLASSCODE.getCode())) {
                result = EPSOS_PS_RETRIEVE_RESPONSE_XCA;
            } else {
                result = EPSOS2_FETCH_DOC_RETRIEVE_RESPONSE;
            }
        }
        return result;
    }

    /**
     * This method will look into an XDR message and obtain the proper model to
     * validate it at Gazelle
     *
     * @param message the XCA message to be validated
     * @return the proper model to be used in the validation
     */
    public static XdModel obtainModelXdr(String message) {

        final String PROVIDE_AND_REGISTER_REQUEST = "ProvideAndRegisterDocumentSetRequest";
        final String PROVIDE_AND_REGISTER_RESPONSE = "RegistryResponse";

        XdModel result = null;


        if (message.contains(PROVIDE_AND_REGISTER_REQUEST)) {
            if (message.contains(ClassCode.CONSENT_CLASSCODE.getCode())) {
                result = EPSOS_CS_PUT_REQUEST;
            } else if (message.contains(ClassCode.ED_CLASSCODE.getCode())) {
                result = EPSOS_ED_INIT_REQUEST;
            } else {
                result = EPSOS2_PROVIDE_DATA_REQUEST;
            }
        } else if (message.contains(PROVIDE_AND_REGISTER_RESPONSE)) {
            if (message.contains(ClassCode.CONSENT_CLASSCODE.getCode())) {
                result = EPSOS_CS_PUT_RESPONSE;
            } else if (message.contains(ClassCode.ED_CLASSCODE.getCode())) {
                result = EPSOS_ED_INIT_RESPONSE;
            } else {
                result = EPSOS2_PROVIDE_DATA_RESPONSE;
            }
        }
        return result;
    }

    public static XdModel obtainResponseModelXdr(String message) {

        XdModel result;

        if (message.contains(ClassCode.CONSENT_CLASSCODE.getCode())) {
            result = EPSOS_CS_PUT_RESPONSE;
        } else if (message.contains(ClassCode.ED_CLASSCODE.getCode())) {
            result = EPSOS_ED_INIT_RESPONSE;
        } else {
            result = EPSOS2_PROVIDE_DATA_RESPONSE;
        }
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

    public ObjectType getObjectType() {
        return objectType;
    }
}
