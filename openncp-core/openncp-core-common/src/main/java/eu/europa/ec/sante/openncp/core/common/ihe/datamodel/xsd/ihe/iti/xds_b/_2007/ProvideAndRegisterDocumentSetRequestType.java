/**
 * Copyright (C) 2011, 2012 SRDC Yazilim Arastirma ve Gelistirme ve Danismanlik Tic. Ltd. Sti. <epsos@srdc.com.tr>
 *
 * This file is part of SRDC epSOS NCP.
 *
 * SRDC epSOS NCP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SRDC epSOS NCP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SRDC epSOS NCP. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.ihe.iti.xds_b._2007;

import eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.lcm._3.SubmitObjectsRequest;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ProvideAndRegisterDocumentSetRequestType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ProvideAndRegisterDocumentSetRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0}SubmitObjectsRequest"/>
 *         &lt;sequence minOccurs="0">
 *           &lt;element name="Document" maxOccurs="unbounded">
 *             &lt;complexType>
 *               &lt;simpleContent>
 *                 &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>base64Binary">
 *                   &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                 &lt;/extension>
 *               &lt;/simpleContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvideAndRegisterDocumentSetRequestType", propOrder = {
    "submitObjectsRequest",
    "document"
})
public class ProvideAndRegisterDocumentSetRequestType {

    @XmlElement(name = "SubmitObjectsRequest", namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", required = true)
    protected SubmitObjectsRequest submitObjectsRequest;
    @XmlElement(name = "Document")
    protected List<Document> document;

    /**
     * Gets the value of the submitObjectsRequest property.
     *
     * @return
     *     possible object is
     *     {@link SubmitObjectsRequest }
     *
     */
    public SubmitObjectsRequest getSubmitObjectsRequest() {
        return submitObjectsRequest;
    }

    /**
     * Sets the value of the submitObjectsRequest property.
     *
     * @param value
     *     allowed object is
     *     {@link SubmitObjectsRequest }
     *
     */
    public void setSubmitObjectsRequest(SubmitObjectsRequest value) {
        this.submitObjectsRequest = value;
    }

    /**
     * Gets the value of the document property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the document property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocument().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Document }
     *
     *
     */
    public List<Document> getDocument() {
        if (document == null) {
            document = new ArrayList<Document>();
        }
        return this.document;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>base64Binary">
     *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Document {

        @XmlValue
        protected byte[] value;
        @XmlAttribute(required = true)
        @XmlSchemaType(name = "anyURI")
        protected String id;

        /**
         * Gets the value of the value property.
         *
         * @return
         *     possible object is
         *     byte[]
         */
        public byte[] getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         *
         * @param value
         *     allowed object is
         *     byte[]
         */
        public void setValue(byte[] value) {
            this.value = ((byte[]) value);
        }

        /**
         * Gets the value of the id property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setId(String value) {
            this.id = value;
        }

    }

}
