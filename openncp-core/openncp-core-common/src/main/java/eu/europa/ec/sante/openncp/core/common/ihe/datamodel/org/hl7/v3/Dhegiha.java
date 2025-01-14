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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Dhegiha.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Dhegiha">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="x-KAA"/>
 *     &lt;enumeration value="x-OMA"/>
 *     &lt;enumeration value="x-OSA"/>
 *     &lt;enumeration value="x-QUA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Dhegiha")
@XmlEnum
public enum Dhegiha {

    @XmlEnumValue("x-KAA")
    X_KAA("x-KAA"),
    @XmlEnumValue("x-OMA")
    X_OMA("x-OMA"),
    @XmlEnumValue("x-OSA")
    X_OSA("x-OSA"),
    @XmlEnumValue("x-QUA")
    X_QUA("x-QUA");
    private final String value;

    Dhegiha(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Dhegiha fromValue(String v) {
        for (Dhegiha c: Dhegiha.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
