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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrimaryDentition.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PrimaryDentition">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="TIDA"/>
 *     &lt;enumeration value="TIDB"/>
 *     &lt;enumeration value="TIDC"/>
 *     &lt;enumeration value="TIDD"/>
 *     &lt;enumeration value="TIDE"/>
 *     &lt;enumeration value="TIDF"/>
 *     &lt;enumeration value="TIDG"/>
 *     &lt;enumeration value="TIDH"/>
 *     &lt;enumeration value="TIDI"/>
 *     &lt;enumeration value="TIDJ"/>
 *     &lt;enumeration value="TIDK"/>
 *     &lt;enumeration value="TIDL"/>
 *     &lt;enumeration value="TIDM"/>
 *     &lt;enumeration value="TIDN"/>
 *     &lt;enumeration value="TIDO"/>
 *     &lt;enumeration value="TIDP"/>
 *     &lt;enumeration value="TIDQ"/>
 *     &lt;enumeration value="TIDR"/>
 *     &lt;enumeration value="TIDS"/>
 *     &lt;enumeration value="TIDT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PrimaryDentition")
@XmlEnum
public enum PrimaryDentition {

    TIDA,
    TIDB,
    TIDC,
    TIDD,
    TIDE,
    TIDF,
    TIDG,
    TIDH,
    TIDI,
    TIDJ,
    TIDK,
    TIDL,
    TIDM,
    TIDN,
    TIDO,
    TIDP,
    TIDQ,
    TIDR,
    TIDS,
    TIDT;

    public String value() {
        return name();
    }

    public static PrimaryDentition fromValue(String v) {
        return valueOf(v);
    }

}
