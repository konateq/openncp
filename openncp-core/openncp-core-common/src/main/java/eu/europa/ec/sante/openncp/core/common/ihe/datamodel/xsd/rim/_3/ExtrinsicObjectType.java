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

import javax.xml.bind.annotation.*;


/**
 * 
 *         ExtrinsicObject is the mapping of the same named interface in ebRIM.
 *         It extends RegistryObject.
 *       
 * 
 * <p>Java class for ExtrinsicObjectType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtrinsicObjectType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element name="ContentVersionInfo" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}VersionInfoType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="mimeType" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}LongName" default="application/octet-stream" />
 *       &lt;attribute name="isOpaque" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtrinsicObjectType", propOrder = {
    "contentVersionInfo"
})
public class ExtrinsicObjectType
    extends RegistryObjectType
{

    @XmlElement(name = "ContentVersionInfo")
    protected VersionInfoType contentVersionInfo;
    @XmlAttribute
    protected String mimeType;
    @XmlAttribute
    protected Boolean isOpaque;

    /**
     * Gets the value of the contentVersionInfo property.
     * 
     * @return
     *     possible object is
     *     {@link VersionInfoType }
     *     
     */
    public VersionInfoType getContentVersionInfo() {
        return contentVersionInfo;
    }

    /**
     * Sets the value of the contentVersionInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link VersionInfoType }
     *     
     */
    public void setContentVersionInfo(VersionInfoType value) {
        this.contentVersionInfo = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        if (mimeType == null) {
            return "application/octet-stream";
        } else {
            return mimeType;
        }
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the isOpaque property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsOpaque() {
        if (isOpaque == null) {
            return false;
        } else {
            return isOpaque;
        }
    }

    /**
     * Sets the value of the isOpaque property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsOpaque(Boolean value) {
        this.isOpaque = value;
    }

}
