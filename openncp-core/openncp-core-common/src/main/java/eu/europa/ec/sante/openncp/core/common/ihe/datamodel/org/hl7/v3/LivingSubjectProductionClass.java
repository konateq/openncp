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
 * <p>Java class for LivingSubjectProductionClass.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LivingSubjectProductionClass">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="BF"/>
 *     &lt;enumeration value="BR"/>
 *     &lt;enumeration value="BL"/>
 *     &lt;enumeration value="CO"/>
 *     &lt;enumeration value="DA"/>
 *     &lt;enumeration value="DR"/>
 *     &lt;enumeration value="DU"/>
 *     &lt;enumeration value="FI"/>
 *     &lt;enumeration value="LY"/>
 *     &lt;enumeration value="MT"/>
 *     &lt;enumeration value="MU"/>
 *     &lt;enumeration value="PL"/>
 *     &lt;enumeration value="RC"/>
 *     &lt;enumeration value="SH"/>
 *     &lt;enumeration value="VL"/>
 *     &lt;enumeration value="WL"/>
 *     &lt;enumeration value="WO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LivingSubjectProductionClass")
@XmlEnum
public enum LivingSubjectProductionClass {

    BF,
    BR,
    BL,
    CO,
    DA,
    DR,
    DU,
    FI,
    LY,
    MT,
    MU,
    PL,
    RC,
    SH,
    VL,
    WL,
    WO;

    public String value() {
        return name();
    }

    public static LivingSubjectProductionClass fromValue(String v) {
        return valueOf(v);
    }

}
