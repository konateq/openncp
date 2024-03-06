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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for COCT_MT080000UV.Container complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="COCT_MT080000UV.Container">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:hl7-org:v3}InfrastructureRootElements"/>
 *         &lt;element name="id" type="{urn:hl7-org:v3}II" minOccurs="0"/>
 *         &lt;element name="code" type="{urn:hl7-org:v3}CE" minOccurs="0"/>
 *         &lt;element name="desc" type="{urn:hl7-org:v3}ED" minOccurs="0"/>
 *         &lt;element name="riskCode" type="{urn:hl7-org:v3}CE" minOccurs="0"/>
 *         &lt;element name="handlingCode" type="{urn:hl7-org:v3}CE" minOccurs="0"/>
 *         &lt;element name="capacityQuantity" type="{urn:hl7-org:v3}PQ" minOccurs="0"/>
 *         &lt;element name="heightQuantity" type="{urn:hl7-org:v3}PQ" minOccurs="0"/>
 *         &lt;element name="diameterQuantity" type="{urn:hl7-org:v3}PQ" minOccurs="0"/>
 *         &lt;element name="capTypeCode" type="{urn:hl7-org:v3}CE" minOccurs="0"/>
 *         &lt;element name="separatorTypeCode" type="{urn:hl7-org:v3}CE" minOccurs="0"/>
 *         &lt;element name="barrierDeltaQuantity" type="{urn:hl7-org:v3}PQ" minOccurs="0"/>
 *         &lt;element name="bottomDeltaQuantity" type="{urn:hl7-org:v3}PQ" minOccurs="0"/>
 *         &lt;element name="asIdentifiedContainer" type="{urn:hl7-org:v3}COCT_MT080000UV.IdentifiedContainer" minOccurs="0"/>
 *         &lt;element name="asContent" type="{urn:hl7-org:v3}COCT_MT080000UV.Content3" minOccurs="0"/>
 *         &lt;element name="asLocatedEntity" type="{urn:hl7-org:v3}COCT_MT070000UV01.LocatedEntity" minOccurs="0"/>
 *         &lt;element name="additive" type="{urn:hl7-org:v3}COCT_MT080000UV.Additive2" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:hl7-org:v3}InfrastructureRootAttributes"/>
 *       &lt;attribute name="nullFlavor" type="{urn:hl7-org:v3}NullFlavor" />
 *       &lt;attribute name="classCode" use="required" type="{urn:hl7-org:v3}EntityClassContainer" />
 *       &lt;attribute name="determinerCode" use="required" type="{urn:hl7-org:v3}EntityDeterminer" fixed="INSTANCE" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "COCT_MT080000UV.Container", propOrder = {
    "realmCode",
    "typeId",
    "templateId",
    "id",
    "code",
    "desc",
    "riskCode",
    "handlingCode",
    "capacityQuantity",
    "heightQuantity",
    "diameterQuantity",
    "capTypeCode",
    "separatorTypeCode",
    "barrierDeltaQuantity",
    "bottomDeltaQuantity",
    "asIdentifiedContainer",
    "asContent",
    "asLocatedEntity",
    "additive"
})
public class COCTMT080000UVContainer {

    protected List<org.hl7.v3.CS> realmCode;
    protected org.hl7.v3.II typeId;
    protected List<org.hl7.v3.II> templateId;
    protected org.hl7.v3.II id;
    protected org.hl7.v3.CE code;
    protected org.hl7.v3.ED desc;
    protected org.hl7.v3.CE riskCode;
    protected org.hl7.v3.CE handlingCode;
    protected org.hl7.v3.PQ capacityQuantity;
    protected org.hl7.v3.PQ heightQuantity;
    protected org.hl7.v3.PQ diameterQuantity;
    protected org.hl7.v3.CE capTypeCode;
    protected org.hl7.v3.CE separatorTypeCode;
    protected org.hl7.v3.PQ barrierDeltaQuantity;
    protected org.hl7.v3.PQ bottomDeltaQuantity;
    @XmlElementRef(name = "asIdentifiedContainer", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<org.hl7.v3.COCTMT080000UVIdentifiedContainer> asIdentifiedContainer;
    @XmlElementRef(name = "asContent", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<org.hl7.v3.COCTMT080000UVContent3> asContent;
    @XmlElementRef(name = "asLocatedEntity", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<org.hl7.v3.COCTMT070000UV01LocatedEntity> asLocatedEntity;
    @XmlElement(nillable = true)
    protected List<org.hl7.v3.COCTMT080000UVAdditive2> additive;
    @XmlAttribute
    protected List<String> nullFlavor;
    @XmlAttribute(required = true)
    protected org.hl7.v3.EntityClassContainer classCode;
    @XmlAttribute(required = true)
    protected String determinerCode;

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
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.II }
     *     
     */
    public org.hl7.v3.II getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.II }
     *     
     */
    public void setId(org.hl7.v3.II value) {
        this.id = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.CE }
     *     
     */
    public org.hl7.v3.CE getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.CE }
     *     
     */
    public void setCode(org.hl7.v3.CE value) {
        this.code = value;
    }

    /**
     * Gets the value of the desc property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.ED }
     *     
     */
    public org.hl7.v3.ED getDesc() {
        return desc;
    }

    /**
     * Sets the value of the desc property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.ED }
     *     
     */
    public void setDesc(org.hl7.v3.ED value) {
        this.desc = value;
    }

    /**
     * Gets the value of the riskCode property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.CE }
     *     
     */
    public org.hl7.v3.CE getRiskCode() {
        return riskCode;
    }

    /**
     * Sets the value of the riskCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.CE }
     *     
     */
    public void setRiskCode(org.hl7.v3.CE value) {
        this.riskCode = value;
    }

    /**
     * Gets the value of the handlingCode property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.CE }
     *     
     */
    public org.hl7.v3.CE getHandlingCode() {
        return handlingCode;
    }

    /**
     * Sets the value of the handlingCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.CE }
     *     
     */
    public void setHandlingCode(org.hl7.v3.CE value) {
        this.handlingCode = value;
    }

    /**
     * Gets the value of the capacityQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.PQ }
     *     
     */
    public org.hl7.v3.PQ getCapacityQuantity() {
        return capacityQuantity;
    }

    /**
     * Sets the value of the capacityQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.PQ }
     *     
     */
    public void setCapacityQuantity(org.hl7.v3.PQ value) {
        this.capacityQuantity = value;
    }

    /**
     * Gets the value of the heightQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.PQ }
     *     
     */
    public org.hl7.v3.PQ getHeightQuantity() {
        return heightQuantity;
    }

    /**
     * Sets the value of the heightQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.PQ }
     *     
     */
    public void setHeightQuantity(org.hl7.v3.PQ value) {
        this.heightQuantity = value;
    }

    /**
     * Gets the value of the diameterQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.PQ }
     *     
     */
    public org.hl7.v3.PQ getDiameterQuantity() {
        return diameterQuantity;
    }

    /**
     * Sets the value of the diameterQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.PQ }
     *     
     */
    public void setDiameterQuantity(org.hl7.v3.PQ value) {
        this.diameterQuantity = value;
    }

    /**
     * Gets the value of the capTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.CE }
     *     
     */
    public org.hl7.v3.CE getCapTypeCode() {
        return capTypeCode;
    }

    /**
     * Sets the value of the capTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.CE }
     *     
     */
    public void setCapTypeCode(org.hl7.v3.CE value) {
        this.capTypeCode = value;
    }

    /**
     * Gets the value of the separatorTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.CE }
     *     
     */
    public org.hl7.v3.CE getSeparatorTypeCode() {
        return separatorTypeCode;
    }

    /**
     * Sets the value of the separatorTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.CE }
     *     
     */
    public void setSeparatorTypeCode(org.hl7.v3.CE value) {
        this.separatorTypeCode = value;
    }

    /**
     * Gets the value of the barrierDeltaQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.PQ }
     *     
     */
    public org.hl7.v3.PQ getBarrierDeltaQuantity() {
        return barrierDeltaQuantity;
    }

    /**
     * Sets the value of the barrierDeltaQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.PQ }
     *     
     */
    public void setBarrierDeltaQuantity(org.hl7.v3.PQ value) {
        this.barrierDeltaQuantity = value;
    }

    /**
     * Gets the value of the bottomDeltaQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.PQ }
     *     
     */
    public org.hl7.v3.PQ getBottomDeltaQuantity() {
        return bottomDeltaQuantity;
    }

    /**
     * Sets the value of the bottomDeltaQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.PQ }
     *     
     */
    public void setBottomDeltaQuantity(org.hl7.v3.PQ value) {
        this.bottomDeltaQuantity = value;
    }

    /**
     * Gets the value of the asIdentifiedContainer property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT080000UVIdentifiedContainer }{@code >}
     *     
     */
    public JAXBElement<org.hl7.v3.COCTMT080000UVIdentifiedContainer> getAsIdentifiedContainer() {
        return asIdentifiedContainer;
    }

    /**
     * Sets the value of the asIdentifiedContainer property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT080000UVIdentifiedContainer }{@code >}
     *     
     */
    public void setAsIdentifiedContainer(JAXBElement<org.hl7.v3.COCTMT080000UVIdentifiedContainer> value) {
        this.asIdentifiedContainer = ((JAXBElement<org.hl7.v3.COCTMT080000UVIdentifiedContainer> ) value);
    }

    /**
     * Gets the value of the asContent property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT080000UVContent3 }{@code >}
     *     
     */
    public JAXBElement<org.hl7.v3.COCTMT080000UVContent3> getAsContent() {
        return asContent;
    }

    /**
     * Sets the value of the asContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT080000UVContent3 }{@code >}
     *     
     */
    public void setAsContent(JAXBElement<org.hl7.v3.COCTMT080000UVContent3> value) {
        this.asContent = ((JAXBElement<org.hl7.v3.COCTMT080000UVContent3> ) value);
    }

    /**
     * Gets the value of the asLocatedEntity property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT070000UV01LocatedEntity }{@code >}
     *     
     */
    public JAXBElement<org.hl7.v3.COCTMT070000UV01LocatedEntity> getAsLocatedEntity() {
        return asLocatedEntity;
    }

    /**
     * Sets the value of the asLocatedEntity property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT070000UV01LocatedEntity }{@code >}
     *     
     */
    public void setAsLocatedEntity(JAXBElement<org.hl7.v3.COCTMT070000UV01LocatedEntity> value) {
        this.asLocatedEntity = ((JAXBElement<org.hl7.v3.COCTMT070000UV01LocatedEntity> ) value);
    }

    /**
     * Gets the value of the additive property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additive property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditive().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.hl7.v3.COCTMT080000UVAdditive2 }
     * 
     * 
     */
    public List<org.hl7.v3.COCTMT080000UVAdditive2> getAdditive() {
        if (additive == null) {
            additive = new ArrayList<org.hl7.v3.COCTMT080000UVAdditive2>();
        }
        return this.additive;
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
     * Gets the value of the classCode property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.EntityClassContainer }
     *     
     */
    public org.hl7.v3.EntityClassContainer getClassCode() {
        return classCode;
    }

    /**
     * Sets the value of the classCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.EntityClassContainer }
     *     
     */
    public void setClassCode(org.hl7.v3.EntityClassContainer value) {
        this.classCode = value;
    }

    /**
     * Gets the value of the determinerCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeterminerCode() {
        if (determinerCode == null) {
            return "INSTANCE";
        } else {
            return determinerCode;
        }
    }

    /**
     * Sets the value of the determinerCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeterminerCode(String value) {
        this.determinerCode = value;
    }

}
