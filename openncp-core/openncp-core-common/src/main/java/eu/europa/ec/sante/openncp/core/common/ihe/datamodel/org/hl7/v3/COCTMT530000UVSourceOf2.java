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
package eu.europa.ec.sante.openncp.core.common.ihe.datamodel.org.hl7.v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for COCT_MT530000UV.SourceOf2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="COCT_MT530000UV.SourceOf2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:hl7-org:v3}InfrastructureRootElements"/>
 *         &lt;element name="sequenceNumber" type="{urn:hl7-org:v3}INT" minOccurs="0"/>
 *         &lt;element name="pauseQuantity" type="{urn:hl7-org:v3}PQ" minOccurs="0"/>
 *         &lt;element name="conjunctionCode" type="{urn:hl7-org:v3}CS" minOccurs="0"/>
 *         &lt;element name="seperatableInd" type="{urn:hl7-org:v3}BL" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="observation" type="{urn:hl7-org:v3}COCT_MT530000UV.Observation"/>
 *           &lt;element name="substanceAdministration" type="{urn:hl7-org:v3}COCT_MT530000UV.SubstanceAdministration"/>
 *           &lt;element name="supply" type="{urn:hl7-org:v3}COCT_MT530000UV.Supply"/>
 *           &lt;element name="procedure" type="{urn:hl7-org:v3}COCT_MT530000UV.Procedure"/>
 *           &lt;element name="encounter" type="{urn:hl7-org:v3}COCT_MT530000UV.Encounter"/>
 *           &lt;element name="act" type="{urn:hl7-org:v3}COCT_MT530000UV.Act"/>
 *           &lt;element name="organizer" type="{urn:hl7-org:v3}COCT_MT530000UV.Organizer"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:hl7-org:v3}InfrastructureRootAttributes"/>
 *       &lt;attribute name="nullFlavor" type="{urn:hl7-org:v3}NullFlavor" />
 *       &lt;attribute name="typeCode" use="required" type="{urn:hl7-org:v3}ActRelationshipType" />
 *       &lt;attribute name="contextControlCode" type="{urn:hl7-org:v3}ContextControl" default="AN" />
 *       &lt;attribute name="contextConductionInd" type="{urn:hl7-org:v3}bl" default="true" />
 *       &lt;attribute name="negationInd" type="{urn:hl7-org:v3}bl" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "COCT_MT530000UV.SourceOf2", propOrder = {
    "realmCode",
    "typeId",
    "templateId",
    "sequenceNumber",
    "pauseQuantity",
    "conjunctionCode",
    "seperatableInd",
    "observation",
    "substanceAdministration",
    "supply",
    "procedure",
    "encounter",
    "act",
    "organizer"
})
public class COCTMT530000UVSourceOf2 {

    protected List<CS> realmCode;
    protected II typeId;
    protected List<II> templateId;
    protected INT sequenceNumber;
    protected PQ pauseQuantity;
    protected CS conjunctionCode;
    protected BL seperatableInd;
    @XmlElementRef(name = "observation", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<COCTMT530000UVObservation> observation;
    @XmlElementRef(name = "substanceAdministration", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<COCTMT530000UVSubstanceAdministration> substanceAdministration;
    @XmlElementRef(name = "supply", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<COCTMT530000UVSupply> supply;
    @XmlElementRef(name = "procedure", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<COCTMT530000UVProcedure> procedure;
    @XmlElementRef(name = "encounter", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<COCTMT530000UVEncounter> encounter;
    @XmlElementRef(name = "act", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<COCTMT530000UVAct> act;
    @XmlElementRef(name = "organizer", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<COCTMT530000UVOrganizer> organizer;
    @XmlAttribute
    protected List<String> nullFlavor;
    @XmlAttribute(required = true)
    protected List<String> typeCode;
    @XmlAttribute
    protected String contextControlCode;
    @XmlAttribute
    protected Boolean contextConductionInd;
    @XmlAttribute
    protected Boolean negationInd;

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
     * {@link CS }
     * 
     * 
     */
    public List<CS> getRealmCode() {
        if (realmCode == null) {
            realmCode = new ArrayList<CS>();
        }
        return this.realmCode;
    }

    /**
     * Gets the value of the typeId property.
     * 
     * @return
     *     possible object is
     *     {@link II }
     *     
     */
    public II getTypeId() {
        return typeId;
    }

    /**
     * Sets the value of the typeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link II }
     *     
     */
    public void setTypeId(II value) {
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
     * {@link II }
     * 
     * 
     */
    public List<II> getTemplateId() {
        if (templateId == null) {
            templateId = new ArrayList<II>();
        }
        return this.templateId;
    }

    /**
     * Gets the value of the sequenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link INT }
     *     
     */
    public INT getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the value of the sequenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link INT }
     *     
     */
    public void setSequenceNumber(INT value) {
        this.sequenceNumber = value;
    }

    /**
     * Gets the value of the pauseQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getPauseQuantity() {
        return pauseQuantity;
    }

    /**
     * Sets the value of the pauseQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setPauseQuantity(PQ value) {
        this.pauseQuantity = value;
    }

    /**
     * Gets the value of the conjunctionCode property.
     * 
     * @return
     *     possible object is
     *     {@link CS }
     *     
     */
    public CS getConjunctionCode() {
        return conjunctionCode;
    }

    /**
     * Sets the value of the conjunctionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CS }
     *     
     */
    public void setConjunctionCode(CS value) {
        this.conjunctionCode = value;
    }

    /**
     * Gets the value of the seperatableInd property.
     * 
     * @return
     *     possible object is
     *     {@link BL }
     *     
     */
    public BL getSeperatableInd() {
        return seperatableInd;
    }

    /**
     * Sets the value of the seperatableInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BL }
     *     
     */
    public void setSeperatableInd(BL value) {
        this.seperatableInd = value;
    }

    /**
     * Gets the value of the observation property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVObservation }{@code >}
     *     
     */
    public JAXBElement<COCTMT530000UVObservation> getObservation() {
        return observation;
    }

    /**
     * Sets the value of the observation property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVObservation }{@code >}
     *     
     */
    public void setObservation(JAXBElement<COCTMT530000UVObservation> value) {
        this.observation = ((JAXBElement<COCTMT530000UVObservation> ) value);
    }

    /**
     * Gets the value of the substanceAdministration property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVSubstanceAdministration }{@code >}
     *     
     */
    public JAXBElement<COCTMT530000UVSubstanceAdministration> getSubstanceAdministration() {
        return substanceAdministration;
    }

    /**
     * Sets the value of the substanceAdministration property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVSubstanceAdministration }{@code >}
     *     
     */
    public void setSubstanceAdministration(JAXBElement<COCTMT530000UVSubstanceAdministration> value) {
        this.substanceAdministration = ((JAXBElement<COCTMT530000UVSubstanceAdministration> ) value);
    }

    /**
     * Gets the value of the supply property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVSupply }{@code >}
     *     
     */
    public JAXBElement<COCTMT530000UVSupply> getSupply() {
        return supply;
    }

    /**
     * Sets the value of the supply property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVSupply }{@code >}
     *     
     */
    public void setSupply(JAXBElement<COCTMT530000UVSupply> value) {
        this.supply = ((JAXBElement<COCTMT530000UVSupply> ) value);
    }

    /**
     * Gets the value of the procedure property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVProcedure }{@code >}
     *     
     */
    public JAXBElement<COCTMT530000UVProcedure> getProcedure() {
        return procedure;
    }

    /**
     * Sets the value of the procedure property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVProcedure }{@code >}
     *     
     */
    public void setProcedure(JAXBElement<COCTMT530000UVProcedure> value) {
        this.procedure = ((JAXBElement<COCTMT530000UVProcedure> ) value);
    }

    /**
     * Gets the value of the encounter property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVEncounter }{@code >}
     *     
     */
    public JAXBElement<COCTMT530000UVEncounter> getEncounter() {
        return encounter;
    }

    /**
     * Sets the value of the encounter property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVEncounter }{@code >}
     *     
     */
    public void setEncounter(JAXBElement<COCTMT530000UVEncounter> value) {
        this.encounter = ((JAXBElement<COCTMT530000UVEncounter> ) value);
    }

    /**
     * Gets the value of the act property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVAct }{@code >}
     *     
     */
    public JAXBElement<COCTMT530000UVAct> getAct() {
        return act;
    }

    /**
     * Sets the value of the act property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVAct }{@code >}
     *     
     */
    public void setAct(JAXBElement<COCTMT530000UVAct> value) {
        this.act = ((JAXBElement<COCTMT530000UVAct> ) value);
    }

    /**
     * Gets the value of the organizer property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVOrganizer }{@code >}
     *     
     */
    public JAXBElement<COCTMT530000UVOrganizer> getOrganizer() {
        return organizer;
    }

    /**
     * Sets the value of the organizer property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVOrganizer }{@code >}
     *     
     */
    public void setOrganizer(JAXBElement<COCTMT530000UVOrganizer> value) {
        this.organizer = ((JAXBElement<COCTMT530000UVOrganizer> ) value);
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
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the typeCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTypeCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTypeCode() {
        if (typeCode == null) {
            typeCode = new ArrayList<String>();
        }
        return this.typeCode;
    }

    /**
     * Gets the value of the contextControlCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContextControlCode() {
        if (contextControlCode == null) {
            return "AN";
        } else {
            return contextControlCode;
        }
    }

    /**
     * Sets the value of the contextControlCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContextControlCode(String value) {
        this.contextControlCode = value;
    }

    /**
     * Gets the value of the contextConductionInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isContextConductionInd() {
        if (contextConductionInd == null) {
            return true;
        } else {
            return contextConductionInd;
        }
    }

    /**
     * Sets the value of the contextConductionInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setContextConductionInd(Boolean value) {
        this.contextConductionInd = value;
    }

    /**
     * Gets the value of the negationInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNegationInd() {
        return negationInd;
    }

    /**
     * Sets the value of the negationInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNegationInd(Boolean value) {
        this.negationInd = value;
    }

}
