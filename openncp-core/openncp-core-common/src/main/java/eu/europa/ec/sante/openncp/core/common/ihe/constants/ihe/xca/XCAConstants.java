package eu.europa.ec.sante.openncp.core.common.constants.ihe.xca;

import eu.europa.ec.sante.openncp.core.common.constants.ihe.IheConstants;

/**
 * Holds all the fixed properties used in the XCA Profile transactions.
 *
 * @author Marcelo Fonseca<code> - marcelo.fonseca@iuz.pt</code>
 * @author Luís Pinto<code> - luis.pinto@iuz.pt</code>
 */
public final class XCAConstants {

    public static final String REGREP_QUERY = "urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0";
    /* AdHocQuery */
    public static final String ADHOC_QUERY_REQUEST = "AdhocQueryRequest";
    public static final String ADHOC_QUERY_RESPONSE = "AdhocQueryResponse";
    /* RetrieveDocumentSet */
    public static final String RETRIEVE_DOCUMENT_SET_REQUEST = "RetrieveDocumentSetRequest";
    public static final String RETRIEVE_DOCUMENT_SET_RESPONSE = "RetrieveDocumentSetResponse";
    public static final String RESPONDING_GATEWAY_SERVICE = "RespondingGateway_Service";

    public static final String XDS_DOC_ENTRY_CLASSIFICATION_NODE = "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1";

    private XCAConstants() {
    }

    public static final class AdHocQueryRequest {

        /* AdhocQueryRequest/ResponseOption */
        public static final String RESPONSE_OPTIONS_RETURN_TYPE = "LeafClass";
        public static final String ID = "urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d";
        /* XDSDocumentEntryPatientId Slot */
        public static final String XDS_DOCUMENT_ENTRY_PATIENTID_SLOT_NAME = "$XDSDocumentEntryPatientId";
        /* XDSDocumentEntryStatus Slot */
        public static final String XDS_DOCUMENT_ENTRY_STATUS_SLOT_NAME = "$XDSDocumentEntryStatus";
        public static final String XDS_DOCUMENT_ENTRY_STATUS_SLOT_VALUE = "('urn:oasis:names:tc:ebxml-regrep:StatusType:Approved')";
        /* XDSDocumentEntryClassCode Slot */
        public static final String XDS_DOCUMENT_ENTRY_CLASSCODE_SLOT_NAME = "$XDSDocumentEntryClassCode";
        public static final String XDS_DOCUMENT_ENTRY_CLASSCODE_SLOT_VALUE = "('60591-5^^2.16.840.1.113883.6.1')";
        /* XDSDocumentEntryFilterParameters Slot */
        public static final String XDS_DOCUMENT_ENTRY_FILTERMAXIMUMSIZE_SLOT_NAME = "$XDSDocumentEntryFilterMaximumSize";
        public static final String XDS_DOCUMENT_ENTRY_FILTERCREATEDBEFORE_SLOT_NAME = "$XDSDocumentEntryFilterCreatedBefore";
        public static final String XDS_DOCUMENT_ENTRY_FILTERCREATEDAFTER_SLOT_NAME = "$XDSDocumentEntryFilterCreatedAfter";

        private AdHocQueryRequest() {
        }
    }

    public static final class SOAP_HEADERS {

        public static final String NAMESPACE_URI = "urn:ihe:iti:xds-b:2007";
        public static final String MUST_UNDERSTAND = "mustUnderstand";
        public static final String ADDRESSING_NAMESPACE = "http://www.w3.org/2005/08/addressing/anonymous";
        public static final String SECURITY_XSD = IheConstants.SOAP_HEADERS.SECURITY_XSD;

        private SOAP_HEADERS() {
        }

        public static final class QUERY {

            /* Mixed */
            public static final String REQUEST_ACTION = "urn:ihe:iti:2007:CrossGatewayQuery";
            public static final String OM_NAMESPACE = "http://www.w3.org/2005/08/addressing";
            /* Request */
            public static final String NAMESPACE_REQUEST_LOCAL_PART = "respondingGateway_CrossGatewayQuery";

            private QUERY() {
            }
        }

        public static final class RETRIEVE {

            public static final String REQUEST_ACTION = "urn:ihe:iti:2007:CrossGatewayRetrieve";
            /* Request */
            public static final String NAMESPACE_REQUEST_LOCAL_PART = "respondingGateway_CrossGatewayRetrieve";

            private RETRIEVE() {
            }
        }
    }

    public static final class LOG {

        /* Query */
        public static final String OUTGOING_XCA_QUERY_MESSAGE = "Outgoing XCA-Query request message to NCP-A:";
        public static final String INCOMING_XCA_QUERY_MESSAGE = "Incoming XCA-Query response message from NCP-A:";
        /* Retrieve */
        public static final String OUTGOING_XCA_RETRIEVE_MESSAGE = "Outgoing XCA-Retrieve request message to NCP-A:";
        public static final String INCOMING_XCA_RETRIEVE_MESSAGE = "Incoming XCA-Retrieve response message from NCP-A:";

        private LOG() {
        }
    }

    public static final class EXCEPTIONS {

        public static final String ERROR_JAXB_MARSHALLING = "Error in JAXB marshalling";
        public static final String UNABLE_CREATE_JAXB_CONTEXT = "Unable to create JAXBContext:";

        private EXCEPTIONS() {
        }
    }

    public static final class EXTRINSIC_OBJECT {

        private EXTRINSIC_OBJECT() {
        }

        /* Format Code */
        public static final class FormatCode {

            public static final String FORMAT_CODE_SCHEME = IheConstants.FORMAT_CODE_SCHEME;

            private FormatCode() {
            }

            public static final class PatientSummary {

                private PatientSummary() {
                }

                public static final class EpsosPivotCoded {

                    public static final String DISPLAY_NAME = "epSOS coded Patient Summary";
                    public static final String NODE_REPRESENTATION = "urn:epSOS:ps:ps:2010";
                    public static final String CODING_SCHEME = "eHDSI formatCodes";

                    private EpsosPivotCoded() {
                    }
                }

                public static final class PdfSourceCoded {

                    public static final String DISPLAY_NAME = "PDF/A coded document";
                    public static final String NODE_REPRESENTATION = "urn:ihe:iti:xds-sd:pdf:2008";
                    public static final String CODING_SCHEME = "eHDSI formatCodes";

                    private PdfSourceCoded() {
                    }
                }
            }

            public static final class EPrescription {

                private EPrescription() {
                }

                public static final class EpsosPivotCoded {

                    public static final String DISPLAY_NAME = "epSOS coded ePrescription";
                    public static final String NODE_REPRESENTATION = "urn:epsos:ep:pre:2010";
                    public static final String CODING_SCHEME = "eHDSI formatCodes";

                    private EpsosPivotCoded() {
                    }
                }

                public static final class PdfSourceCoded {

                    public static final String DISPLAY_NAME = "PDF/A coded document";
                    public static final String NODE_REPRESENTATION = "urn:ihe:iti:xds-sd:pdf:2008";
                    public static final String CODING_SCHEME = "eHDSI formatCodes";

                    private PdfSourceCoded() {
                    }
                }
            }

            public static final class Mro {

                private Mro() {
                }

                public static final class EpsosPivotCoded {

                    public static final String DISPLAY_NAME = "epSOS Coded MRO";
                    public static final String NODE_REPRESENTATION = "urn:epSOS:mro:mro:2013";
                    public static final String CODING_SCHEME = "eHDSI formatCodes";

                    private EpsosPivotCoded() {
                    }
                }

                public static final class PdfSourceCoded {

                    public static final String DISPLAY_NAME = "PDF/A coded document";
                    public static final String NODE_REPRESENTATION = "urn:ihe:iti:xds-sd:pdf:2008";
                    public static final String CODING_SCHEME = "eHDSI formatCodes";

                    private PdfSourceCoded() {
                    }
                }
            }

            public static final class OrCD {

                private OrCD() {
                }

                public static final class PdfSourceCoded {

                    public static final String DISPLAY_NAME = "PDF/A coded document";
                    public static final String NODE_REPRESENTATION = "urn:eHDSI:orcd:pdf:2021";
                    public static final String CODING_SCHEME = "eHDSI formatCodes";

                    private PdfSourceCoded() {
                    }
                }

                public static final class PngSourceCoded {

                    public static final String DISPLAY_NAME = "PNG coded document";
                    public static final String NODE_REPRESENTATION = "urn:eHDSI:orcd:png:2021";
                    public static final String CODING_SCHEME = "eHDSI formatCodes";

                    private PngSourceCoded() {
                    }
                }

                public static final class JpegSourceCoded {

                    public static final String DISPLAY_NAME = "JPEG coded document";
                    public static final String NODE_REPRESENTATION = "urn:eHDSI:orcd:jpeg:2021";
                    public static final String CODING_SCHEME = "eHDSI formatCodes";

                    private JpegSourceCoded() {
                    }
                }
            }
        }
    }
}
