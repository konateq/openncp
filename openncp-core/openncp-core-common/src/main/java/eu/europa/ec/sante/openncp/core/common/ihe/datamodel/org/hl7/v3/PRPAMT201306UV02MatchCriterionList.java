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
 * <p>Java class for PRPA_MT201306UV02.MatchCriterionList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PRPA_MT201306UV02.MatchCriterionList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:hl7-org:v3}InfrastructureRootElements"/>
 *         &lt;element name="id" type="{urn:hl7-org:v3}II" minOccurs="0"/>
 *         &lt;element name="matchAlgorithm" type="{urn:hl7-org:v3}PRPA_MT201306UV02.MatchAlgorithm" minOccurs="0"/>
 *         &lt;element name="matchWeight" type="{urn:hl7-org:v3}PRPA_MT201306UV02.MatchWeight" minOccurs="0"/>
 *         &lt;element name="minimumDegreeMatch" type="{urn:hl7-org:v3}PRPA_MT201306UV02.MinimumDegreeMatch" minOccurs="0"/>
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
@XmlType(name = "PRPA_MT201306UV02.MatchCriterionList", propOrder = {
    "realmCode",
    "typeId",
    "templateId",
    "id",
    "matchAlgorithm",
    "matchWeight",
    "minimumDegreeMatch"
})
public class PRPAMT201306UV02MatchCriterionList {

    protected List<CS> realmCode;
    protected II typeId;
    protected List<II> templateId;
    protected II id;
    @XmlElementRef(name = "matchAlgorithm", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<PRPAMT201306UV02MatchAlgorithm> matchAlgorithm;
    @XmlElementRef(name = "matchWeight", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<PRPAMT201306UV02MatchWeight> matchWeight;
    @XmlElementRef(name = "minimumDegreeMatch", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<PRPAMT201306UV02MinimumDegreeMatch> minimumDegreeMatch;
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
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link II }
     *     
     */
    public II getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link II }
     *     
     */
    public void setId(II value) {
        this.id = value;
    }

    /**
     * Gets the value of the matchAlgorithm property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link PRPAMT201306UV02MatchAlgorithm }{@code >}
     *     
     */
    public JAXBElement<PRPAMT201306UV02MatchAlgorithm> getMatchAlgorithm() {
        return matchAlgorithm;
    }

    /**
     * Sets the value of the matchAlgorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link PRPAMT201306UV02MatchAlgorithm }{@code >}
     *     
     */
    public void setMatchAlgorithm(JAXBElement<PRPAMT201306UV02MatchAlgorithm> value) {
        this.matchAlgorithm = ((JAXBElement<PRPAMT201306UV02MatchAlgorithm> ) value);
    }

    /**
     * Gets the value of the matchWeight property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link PRPAMT201306UV02MatchWeight }{@code >}
     *     
     */
    public JAXBElement<PRPAMT201306UV02MatchWeight> getMatchWeight() {
        return matchWeight;
    }

    /**
     * Sets the value of the matchWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link PRPAMT201306UV02MatchWeight }{@code >}
     *     
     */
    public void setMatchWeight(JAXBElement<PRPAMT201306UV02MatchWeight> value) {
        this.matchWeight = ((JAXBElement<PRPAMT201306UV02MatchWeight> ) value);
    }

    /**
     * Gets the value of the minimumDegreeMatch property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link PRPAMT201306UV02MinimumDegreeMatch }{@code >}
     *     
     */
    public JAXBElement<PRPAMT201306UV02MinimumDegreeMatch> getMinimumDegreeMatch() {
        return minimumDegreeMatch;
    }

    /**
     * Sets the value of the minimumDegreeMatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link PRPAMT201306UV02MinimumDegreeMatch }{@code >}
     *     
     */
    public void setMinimumDegreeMatch(JAXBElement<PRPAMT201306UV02MinimumDegreeMatch> value) {
        this.minimumDegreeMatch = ((JAXBElement<PRPAMT201306UV02MinimumDegreeMatch> ) value);
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
