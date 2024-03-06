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
 * <p>Java class for PRPA_MT201310UV02.Subject complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PRPA_MT201310UV02.Subject">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:hl7-org:v3}InfrastructureRootElements"/>
 *         &lt;element name="queryMatchObservation" type="{urn:hl7-org:v3}PRPA_MT201310UV02.QueryMatchObservation"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:hl7-org:v3}InfrastructureRootAttributes"/>
 *       &lt;attribute name="nullFlavor" type="{urn:hl7-org:v3}NullFlavor" />
 *       &lt;attribute name="typeCode" type="{urn:hl7-org:v3}ParticipationTargetSubject" fixed="SBJ" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PRPA_MT201310UV02.Subject", propOrder = {
    "realmCode",
    "typeId",
    "templateId",
    "queryMatchObservation"
})
public class PRPAMT201310UV02Subject {

    protected List<org.hl7.v3.CS> realmCode;
    protected org.hl7.v3.II typeId;
    protected List<org.hl7.v3.II> templateId;
    @XmlElement(required = true, nillable = true)
    protected org.hl7.v3.PRPAMT201310UV02QueryMatchObservation queryMatchObservation;
    @XmlAttribute
    protected List<String> nullFlavor;
    @XmlAttribute
    protected org.hl7.v3.ParticipationTargetSubject typeCode;

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
     * Gets the value of the queryMatchObservation property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.PRPAMT201310UV02QueryMatchObservation }
     *     
     */
    public org.hl7.v3.PRPAMT201310UV02QueryMatchObservation getQueryMatchObservation() {
        return queryMatchObservation;
    }

    /**
     * Sets the value of the queryMatchObservation property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.PRPAMT201310UV02QueryMatchObservation }
     *     
     */
    public void setQueryMatchObservation(org.hl7.v3.PRPAMT201310UV02QueryMatchObservation value) {
        this.queryMatchObservation = value;
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

    /**
     * Gets the value of the typeCode property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.ParticipationTargetSubject }
     *     
     */
    public org.hl7.v3.ParticipationTargetSubject getTypeCode() {
        if (typeCode == null) {
            return org.hl7.v3.ParticipationTargetSubject.SBJ;
        } else {
            return typeCode;
        }
    }

    /**
     * Sets the value of the typeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.ParticipationTargetSubject }
     *     
     */
    public void setTypeCode(org.hl7.v3.ParticipationTargetSubject value) {
        this.typeCode = value;
    }

}
