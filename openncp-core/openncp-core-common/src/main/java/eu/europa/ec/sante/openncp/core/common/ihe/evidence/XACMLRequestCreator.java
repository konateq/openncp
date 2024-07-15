package eu.europa.ec.sante.openncp.core.common.ihe.evidence;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.xacml.ctx.*;
import org.opensaml.xacml.ctx.impl.*;
import org.w3c.dom.Element;

import java.util.List;

public class XACMLRequestCreator {

    static {
        try {
            InitializationService.initialize();
        } catch (final InitializationException e) {
            throw new IllegalStateException("Unable to bootstrap OpenSAML!!! Did you endorse the XML libraries?", e);
        }
    }

    private final AttributeTypeImplBuilder atib;
    private final AttributeValueTypeImplBuilder avtib;
    private final Element request;

    /**
     * @param messageType
     * @param subjectAttributes
     * @param resourceAttributes
     * @param actionAttributes
     * @param environmentAttributes
     * @throws TOElementException
     */
    public XACMLRequestCreator(final MessageType messageType, final List<XACMLAttributes> subjectAttributes, final List<XACMLAttributes> resourceAttributes,
                               final List<XACMLAttributes> actionAttributes, final List<XACMLAttributes> environmentAttributes)
            throws TOElementException {

        final XMLObjectBuilderFactory bf = XMLObjectProviderRegistrySupport.getBuilderFactory();
        atib = (AttributeTypeImplBuilder) bf.getBuilder(AttributeType.DEFAULT_ELEMENT_NAME);
        avtib = (AttributeValueTypeImplBuilder) bf.getBuilder(AttributeValueType.DEFAULT_ELEMENT_NAME);

        final RequestTypeImplBuilder rtb = (RequestTypeImplBuilder) bf.getBuilder(RequestType.DEFAULT_ELEMENT_NAME);
        final RequestType requestType = rtb.buildObject();

        final SubjectTypeImplBuilder stib = (SubjectTypeImplBuilder) bf.getBuilder(SubjectType.DEFAULT_ELEMENT_NAME);
        final SubjectType subject = stib.buildObject();

        final ResourceTypeImplBuilder rtib = (ResourceTypeImplBuilder) bf.getBuilder(ResourceType.DEFAULT_ELEMENT_NAME);
        final ResourceType resource = rtib.buildObject();

        final ActionTypeImplBuilder actib = (ActionTypeImplBuilder) bf.getBuilder(ActionType.DEFAULT_ELEMENT_NAME);
        final ActionType action = actib.buildObject();

        final EnvironmentTypeImplBuilder etib = (EnvironmentTypeImplBuilder) bf.getBuilder(EnvironmentType.DEFAULT_ELEMENT_NAME);
        final EnvironmentType environment = etib.buildObject();

        requestType.getSubjects().add(subject);
        requestType.getResources().add(resource);
        requestType.setAction(action);
        requestType.setEnvironment(environment);

        if (subjectAttributes != null) {

            for (final XACMLAttributes attributeItem : subjectAttributes) {
                final AttributeType attribute = atib.buildObject();
                attribute.setAttributeID(attributeItem.getIdentifier().toASCIIString());
                attribute.setDataType(attributeItem.getDataType().toASCIIString());

                final AttributeValueType attributeValue = avtib.buildObject();
                attributeValue.setValue(attributeItem.getValue());
                attribute.getAttributeValues().add(attributeValue);
                subject.getAttributes().add(attribute);

            }
        }
        if (resourceAttributes != null) {

            for (final XACMLAttributes attributeItem : resourceAttributes) {
                final AttributeType attribute = atib.buildObject();
                attribute.setAttributeID(attributeItem.getIdentifier().toASCIIString());
                attribute.setDataType(attributeItem.getDataType().toASCIIString());

                final AttributeValueType attributeValue = avtib.buildObject();
                attributeValue.setValue(attributeItem.getValue());
                attribute.getAttributeValues().add(attributeValue);
                resource.getAttributes().add(attribute);
            }
        }

        if (actionAttributes != null) {

            for (final XACMLAttributes attributeItem : actionAttributes) {
                final AttributeType attribute = atib.buildObject();
                attribute.setAttributeID(attributeItem.getIdentifier().toASCIIString());
                attribute.setDataType(attributeItem.getDataType().toASCIIString());

                final AttributeValueType attributeValue = avtib.buildObject();
                attributeValue.setValue(attributeItem.getValue());
                attribute.getAttributeValues().add(attributeValue);
                action.getAttributes().add(attribute);
            }
        }

        if (environmentAttributes != null) {

            for (final XACMLAttributes attributeItem : environmentAttributes) {
                final AttributeType attribute = atib.buildObject();
                attribute.setAttributeID(attributeItem.getIdentifier().toASCIIString());
                attribute.setDataType(attributeItem.getDataType().toASCIIString());

                final AttributeValueType attributeValue = avtib.buildObject();
                attributeValue.setValue(attributeItem.getValue());
                attribute.getAttributeValues().add(attributeValue);
                environment.getAttributes().add(attribute);
            }
        }

        this.request = Utilities.toElement(requestType);
    }

    public Element getRequest() {
        return this.request;
    }
}
