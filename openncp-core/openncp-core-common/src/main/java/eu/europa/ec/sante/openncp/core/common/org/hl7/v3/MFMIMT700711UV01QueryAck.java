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
package eu.europa.ec.sante.openncp.core.common.org.hl7.v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MFMI_MT700711UV01.QueryAck complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MFMI_MT700711UV01.QueryAck">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:hl7-org:v3}InfrastructureRootElements"/>
 *         &lt;element name="queryId" type="{urn:hl7-org:v3}II" minOccurs="0"/>
 *         &lt;element name="statusCode" type="{urn:hl7-org:v3}CS" minOccurs="0"/>
 *         &lt;element name="queryResponseCode" type="{urn:hl7-org:v3}CS"/>
 *         &lt;element name="resultTotalQuantity" type="{urn:hl7-org:v3}INT" minOccurs="0"/>
 *         &lt;element name="resultCurrentQuantity" type="{urn:hl7-org:v3}INT" minOccurs="0"/>
 *         &lt;element name="resultRemainingQuantity" type="{urn:hl7-org:v3}INT" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:hl7-org:v3}InfrastructureRootAttributes"/>
 *       &lt;attribute name="nullFlavor" type="{urn:hl7-org:v3}NullFlavor" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MFMI_MT700711UV01.QueryAck", propOrder = {
    "realmCode",
    "typeId",
    "templateId",
    "queryId",
    "statusCode",
    "queryResponseCode",
    "resultTotalQuantity",
    "resultCurrentQuantity",
    "resultRemainingQuantity"
})
public class MFMIMT700711UV01QueryAck {

    protected List<org.hl7.v3.CS> realmCode;
    protected org.hl7.v3.II typeId;
    protected List<org.hl7.v3.II> templateId;
    protected org.hl7.v3.II queryId;
    protected org.hl7.v3.CS statusCode;
    @XmlElement(required = true)
    protected org.hl7.v3.CS queryResponseCode;
    protected org.hl7.v3.INT resultTotalQuantity;
    protected org.hl7.v3.INT resultCurrentQuantity;
    protected org.hl7.v3.INT resultRemainingQuantity;
    @XmlAttribute
    protected List<String> nullFlavor;

    /**
     * Gets the value of the realmCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the realmCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRealmCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.hl7.v3.CS }
     * 
     * 
     */
    public List<org.hl7.v3.CS> getRealmCode() {
        if (realmCode == null) {
            realmCode = new ArrayList<org.hl7.v3.CS>();
        }
        return this.realmCode;
    }

    /**
     * Gets the value of the typeId property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.II }
     *     
     */
    public org.hl7.v3.II getTypeId() {
        return typeId;
    }

    /**
     * Sets the value of the typeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.II }
     *     
     */
    public void setTypeId(org.hl7.v3.II value) {
        this.typeId = value;
    }

    /**
     * Gets the value of the templateId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the templateId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTemplateId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.hl7.v3.II }
     * 
     * 
     */
    public List<org.hl7.v3.II> getTemplateId() {
        if (templateId == null) {
            templateId = new ArrayList<org.hl7.v3.II>();
        }
        return this.templateId;
    }

    /**
     * Gets the value of the queryId property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.II }
     *     
     */
    public org.hl7.v3.II getQueryId() {
        return queryId;
    }

    /**
     * Sets the value of the queryId property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.II }
     *     
     */
    public void setQueryId(org.hl7.v3.II value) {
        this.queryId = value;
    }

    /**
     * Gets the value of the statusCode property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.CS }
     *     
     */
    public org.hl7.v3.CS getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the value of the statusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.CS }
     *     
     */
    public void setStatusCode(org.hl7.v3.CS value) {
        this.statusCode = value;
    }

    /**
     * Gets the value of the queryResponseCode property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.CS }
     *     
     */
    public org.hl7.v3.CS getQueryResponseCode() {
        return queryResponseCode;
    }

    /**
     * Sets the value of the queryResponseCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.CS }
     *     
     */
    public void setQueryResponseCode(org.hl7.v3.CS value) {
        this.queryResponseCode = value;
    }

    /**
     * Gets the value of the resultTotalQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.INT }
     *     
     */
    public org.hl7.v3.INT getResultTotalQuantity() {
        return resultTotalQuantity;
    }

    /**
     * Sets the value of the resultTotalQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.INT }
     *     
     */
    public void setResultTotalQuantity(org.hl7.v3.INT value) {
        this.resultTotalQuantity = value;
    }

    /**
     * Gets the value of the resultCurrentQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.INT }
     *     
     */
    public org.hl7.v3.INT getResultCurrentQuantity() {
        return resultCurrentQuantity;
    }

    /**
     * Sets the value of the resultCurrentQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.INT }
     *     
     */
    public void setResultCurrentQuantity(org.hl7.v3.INT value) {
        this.resultCurrentQuantity = value;
    }

    /**
     * Gets the value of the resultRemainingQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.INT }
     *     
     */
    public org.hl7.v3.INT getResultRemainingQuantity() {
        return resultRemainingQuantity;
    }

    /**
     * Sets the value of the resultRemainingQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.INT }
     *     
     */
    public void setResultRemainingQuantity(org.hl7.v3.INT value) {
        this.resultRemainingQuantity = value;
    }

    /**
     * Gets the value of the nullFlavor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nullFlavor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNullFlavor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNullFlavor() {
        if (nullFlavor == null) {
            nullFlavor = new ArrayList<String>();
        }
        return this.nullFlavor;
    }

}
