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
package eu.europa.ec.sante.openncp.core.common.ihe.datamodel.xsd.rim._3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *         Classification is the mapping of the same named interface in ebRIM.
 *         It extends RegistryObject.
 *         A Classification specifies references to two registry entrys.
 *         The classifiedObject is id of the Object being classified.
 *         The classificationNode is id of the ClassificationNode classying the object
 *       
 * 
 * <p>Java class for ClassificationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClassificationType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;attribute name="classificationScheme" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="classifiedObject" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="classificationNode" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="nodeRepresentation" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}LongName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClassificationType")
public class ClassificationType
    extends RegistryObjectType
{

    @XmlAttribute
    protected String classificationScheme;
    @XmlAttribute(required = true)
    protected String classifiedObject;
    @XmlAttribute
    protected String classificationNode;
    @XmlAttribute
    protected String nodeRepresentation;

    /**
     * Gets the value of the classificationScheme property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassificationScheme() {
        return classificationScheme;
    }

    /**
     * Sets the value of the classificationScheme property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassificationScheme(String value) {
        this.classificationScheme = value;
    }

    /**
     * Gets the value of the classifiedObject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassifiedObject() {
        return classifiedObject;
    }

    /**
     * Sets the value of the classifiedObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassifiedObject(String value) {
        this.classifiedObject = value;
    }

    /**
     * Gets the value of the classificationNode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassificationNode() {
        return classificationNode;
    }

    /**
     * Sets the value of the classificationNode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassificationNode(String value) {
        this.classificationNode = value;
    }

    /**
     * Gets the value of the nodeRepresentation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNodeRepresentation() {
        return nodeRepresentation;
    }

    /**
     * Sets the value of the nodeRepresentation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNodeRepresentation(String value) {
        this.nodeRepresentation = value;
    }

}
