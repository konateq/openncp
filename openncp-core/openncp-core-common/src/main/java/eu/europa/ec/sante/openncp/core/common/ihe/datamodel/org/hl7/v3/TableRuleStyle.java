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
 * <p>Java class for TableRuleStyle.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TableRuleStyle">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="Botrule"/>
 *     &lt;enumeration value="Lrule"/>
 *     &lt;enumeration value="Rrule"/>
 *     &lt;enumeration value="Toprule"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TableRuleStyle")
@XmlEnum
public enum TableRuleStyle {

    @XmlEnumValue("Botrule")
    BOTRULE("Botrule"),
    @XmlEnumValue("Lrule")
    LRULE("Lrule"),
    @XmlEnumValue("Rrule")
    RRULE("Rrule"),
    @XmlEnumValue("Toprule")
    TOPRULE("Toprule");
    private final String value;

    TableRuleStyle(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TableRuleStyle fromValue(String v) {
        for (TableRuleStyle c: TableRuleStyle.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
