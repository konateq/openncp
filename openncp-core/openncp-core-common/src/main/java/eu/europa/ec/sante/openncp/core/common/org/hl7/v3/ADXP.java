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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *             A character string that may have a type-tag signifying its
 *             role in the address. Typical parts that exist in about
 *             every address are street, house number, or post box,
 *             postal code, city, country but other roles may be defined
 *             regionally, nationally, or on an enterprise level (e.g. in
 *             military addresses). Addresses are usually broken up into
 *             lines, which are indicated by special line-breaking
 *             delimiter elements (e.g., DEL).
 *          
 * 
 * <p>Java class for ADXP complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ADXP">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}ST">
 *       &lt;attribute name="partType" type="{urn:hl7-org:v3}AddressPartType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ADXP")
@XmlSeeAlso({
    org.hl7.v3.AdxpDeliveryInstallationType.class,
    AdxpPrecinct.class,
    org.hl7.v3.AdxpUnitID.class,
    org.hl7.v3.AdxpCensusTract.class,
    org.hl7.v3.AdxpDeliveryAddressLine.class,
    org.hl7.v3.AdxpPostBox.class,
    org.hl7.v3.AdxpDeliveryInstallationArea.class,
    org.hl7.v3.AdxpDeliveryMode.class,
    org.hl7.v3.AdxpHouseNumber.class,
    org.hl7.v3.AdxpStreetNameType.class,
    org.hl7.v3.AdxpDirection.class,
    org.hl7.v3.AdxpPostalCode.class,
    org.hl7.v3.AdxpStreetNameBase.class,
    org.hl7.v3.AdxpDeliveryInstallationQualifier.class,
    org.hl7.v3.AdxpBuildingNumberSuffix.class,
    org.hl7.v3.AdxpCity.class,
    org.hl7.v3.AdxpState.class,
    org.hl7.v3.AdxpDelimiter.class,
    org.hl7.v3.AdxpStreetAddressLine.class,
    org.hl7.v3.AdxpUnitType.class,
    org.hl7.v3.AdxpCountry.class,
    org.hl7.v3.AdxpHouseNumberNumeric.class,
    org.hl7.v3.AdxpCareOf.class,
    org.hl7.v3.AdxpCounty.class,
    AdxpDeliveryModeIdentifier.class,
    org.hl7.v3.AdxpStreetName.class,
    AdxpAdditionalLocator.class
})
public class ADXP
    extends org.hl7.v3.ST
{

    @XmlAttribute
    protected List<String> partType;

    /**
     * Gets the value of the partType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the partType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPartType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPartType() {
        if (partType == null) {
            partType = new ArrayList<String>();
        }
        return this.partType;
    }

}
