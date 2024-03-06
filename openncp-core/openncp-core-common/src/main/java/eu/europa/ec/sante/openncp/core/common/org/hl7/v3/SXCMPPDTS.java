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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SXCM_PPD_TS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SXCM_PPD_TS">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}PPD_TS">
 *       &lt;attribute name="operator" type="{urn:hl7-org:v3}SetOperator" default="I" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SXCM_PPD_TS")
@XmlSeeAlso({
    org.hl7.v3.PIVLPPDTS.class,
    org.hl7.v3.IVLPPDTS.class,
    org.hl7.v3.EIVLPPDTS.class
})
public class SXCMPPDTS
    extends org.hl7.v3.PPDTS
{

    @XmlAttribute
    protected org.hl7.v3.SetOperator operator;

    /**
     * Gets the value of the operator property.
     * 
     * @return
     *     possible object is
     *     {@link org.hl7.v3.SetOperator }
     *     
     */
    public org.hl7.v3.SetOperator getOperator() {
        if (operator == null) {
            return org.hl7.v3.SetOperator.I;
        } else {
            return operator;
        }
    }

    /**
     * Sets the value of the operator property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.hl7.v3.SetOperator }
     *     
     */
    public void setOperator(org.hl7.v3.SetOperator value) {
        this.operator = value;
    }

}
