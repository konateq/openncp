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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for COCT_MT530000UV.RelatedEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="COCT_MT530000UV.RelatedEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:hl7-org:v3}InfrastructureRootElements"/>
 *         &lt;element name="id" type="{urn:hl7-org:v3}II" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="code" type="{urn:hl7-org:v3}CE" minOccurs="0"/>
 *         &lt;element name="addr" type="{urn:hl7-org:v3}AD" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="telecom" type="{urn:hl7-org:v3}TEL" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;choice>
 *             &lt;element name="relatedPerson" type="{urn:hl7-org:v3}COCT_MT530000UV.Person" minOccurs="0"/>
 *             &lt;element name="relatedAnimal" type="{urn:hl7-org:v3}COCT_MT530000UV.Animal" minOccurs="0"/>
 *           &lt;/choice>
 *           &lt;element name="relatedEntity" type="{urn:hl7-org:v3}COCT_MT530000UV.Entity" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;choice>
 *             &lt;element name="scopingPerson" type="{urn:hl7-org:v3}COCT_MT530000UV.Person" minOccurs="0"/>
 *             &lt;element name="scopingAnimal" type="{urn:hl7-org:v3}COCT_MT530000UV.Animal" minOccurs="0"/>
 *           &lt;/choice>
 *           &lt;element name="scopingEntity" type="{urn:hl7-org:v3}COCT_MT530000UV.Entity" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:hl7-org:v3}InfrastructureRootAttributes"/>
 *       &lt;attribute name="nullFlavor" type="{urn:hl7-org:v3}NullFlavor" />
 *       &lt;attribute name="classCode" use="required" type="{urn:hl7-org:v3}RoleClassMutualRelationship" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "COCT_MT530000UV.RelatedEntity", propOrder = {
    "realmCode",
    "typeId",
    "templateId",
    "id",
    "code",
    "addr",
    "telecom",
    "relatedPerson",
    "relatedAnimal",
    "relatedEntity",
    "scopingPerson",
    "scopingAnimal",
    "scopingEntity"
})
public class COCTMT530000UVRelatedEntity {

    protected List<org.hl7.v3.CS> realmCode;
    protected org.hl7.v3.II typeId;
    protected List<org.hl7.v3.II> templateId;
    protected List<org.hl7.v3.II> id;
    protected CE code;
    protected List<org.hl7.v3.AD> addr;
    protected List<org.hl7.v3.TEL> telecom;
    @XmlElementRef(name = "relatedPerson", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<org.hl7.v3.COCTMT530000UVPerson> relatedPerson;
    @XmlElementRef(name = "relatedAnimal", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<org.hl7.v3.COCTMT530000UVAnimal> relatedAnimal;
    @XmlElementRef(name = "relatedEntity", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<COCTMT530000UVEntity> relatedEntity;
    @XmlElementRef(name = "scopingPerson", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<org.hl7.v3.COCTMT530000UVPerson> scopingPerson;
    @XmlElementRef(name = "scopingAnimal", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<org.hl7.v3.COCTMT530000UVAnimal> scopingAnimal;
    @XmlElementRef(name = "scopingEntity", namespace = "urn:hl7-org:v3", type = JAXBElement.class)
    protected JAXBElement<COCTMT530000UVEntity> scopingEntity;
    @XmlAttribute
    protected List<String> nullFlavor;
    @XmlAttribute(required = true)
    protected List<String> classCode;

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
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the id property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.hl7.v3.II }
     * 
     * 
     */
    public List<org.hl7.v3.II> getId() {
        if (id == null) {
            id = new ArrayList<org.hl7.v3.II>();
        }
        return this.id;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link CE }
     *     
     */
    public CE getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link CE }
     *     
     */
    public void setCode(CE value) {
        this.code = value;
    }

    /**
     * Gets the value of the addr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddr().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.hl7.v3.AD }
     * 
     * 
     */
    public List<org.hl7.v3.AD> getAddr() {
        if (addr == null) {
            addr = new ArrayList<org.hl7.v3.AD>();
        }
        return this.addr;
    }

    /**
     * Gets the value of the telecom property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the telecom property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTelecom().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.hl7.v3.TEL }
     * 
     * 
     */
    public List<org.hl7.v3.TEL> getTelecom() {
        if (telecom == null) {
            telecom = new ArrayList<org.hl7.v3.TEL>();
        }
        return this.telecom;
    }

    /**
     * Gets the value of the relatedPerson property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT530000UVPerson }{@code >}
     *     
     */
    public JAXBElement<org.hl7.v3.COCTMT530000UVPerson> getRelatedPerson() {
        return relatedPerson;
    }

    /**
     * Sets the value of the relatedPerson property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT530000UVPerson }{@code >}
     *     
     */
    public void setRelatedPerson(JAXBElement<org.hl7.v3.COCTMT530000UVPerson> value) {
        this.relatedPerson = ((JAXBElement<org.hl7.v3.COCTMT530000UVPerson> ) value);
    }

    /**
     * Gets the value of the relatedAnimal property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT530000UVAnimal }{@code >}
     *     
     */
    public JAXBElement<org.hl7.v3.COCTMT530000UVAnimal> getRelatedAnimal() {
        return relatedAnimal;
    }

    /**
     * Sets the value of the relatedAnimal property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT530000UVAnimal }{@code >}
     *     
     */
    public void setRelatedAnimal(JAXBElement<org.hl7.v3.COCTMT530000UVAnimal> value) {
        this.relatedAnimal = ((JAXBElement<org.hl7.v3.COCTMT530000UVAnimal> ) value);
    }

    /**
     * Gets the value of the relatedEntity property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVEntity }{@code >}
     *     
     */
    public JAXBElement<COCTMT530000UVEntity> getRelatedEntity() {
        return relatedEntity;
    }

    /**
     * Sets the value of the relatedEntity property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVEntity }{@code >}
     *     
     */
    public void setRelatedEntity(JAXBElement<COCTMT530000UVEntity> value) {
        this.relatedEntity = ((JAXBElement<COCTMT530000UVEntity> ) value);
    }

    /**
     * Gets the value of the scopingPerson property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT530000UVPerson }{@code >}
     *     
     */
    public JAXBElement<org.hl7.v3.COCTMT530000UVPerson> getScopingPerson() {
        return scopingPerson;
    }

    /**
     * Sets the value of the scopingPerson property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT530000UVPerson }{@code >}
     *     
     */
    public void setScopingPerson(JAXBElement<org.hl7.v3.COCTMT530000UVPerson> value) {
        this.scopingPerson = ((JAXBElement<org.hl7.v3.COCTMT530000UVPerson> ) value);
    }

    /**
     * Gets the value of the scopingAnimal property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT530000UVAnimal }{@code >}
     *     
     */
    public JAXBElement<org.hl7.v3.COCTMT530000UVAnimal> getScopingAnimal() {
        return scopingAnimal;
    }

    /**
     * Sets the value of the scopingAnimal property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link org.hl7.v3.COCTMT530000UVAnimal }{@code >}
     *     
     */
    public void setScopingAnimal(JAXBElement<org.hl7.v3.COCTMT530000UVAnimal> value) {
        this.scopingAnimal = ((JAXBElement<org.hl7.v3.COCTMT530000UVAnimal> ) value);
    }

    /**
     * Gets the value of the scopingEntity property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVEntity }{@code >}
     *     
     */
    public JAXBElement<COCTMT530000UVEntity> getScopingEntity() {
        return scopingEntity;
    }

    /**
     * Sets the value of the scopingEntity property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCTMT530000UVEntity }{@code >}
     *     
     */
    public void setScopingEntity(JAXBElement<COCTMT530000UVEntity> value) {
        this.scopingEntity = ((JAXBElement<COCTMT530000UVEntity> ) value);
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
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getClassCode() {
        if (classCode == null) {
            classCode = new ArrayList<String>();
        }
        return this.classCode;
    }

}
