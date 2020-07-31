
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ELDEventType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ELDEventType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="DriverMark"/&gt;
 *     &lt;enumeration value="ChangeDriverStatus"/&gt;
 *     &lt;enumeration value="IntermediateLog"/&gt;
 *     &lt;enumeration value="ChangeDriverIndication"/&gt;
 *     &lt;enumeration value="DriverCertification"/&gt;
 *     &lt;enumeration value="LoginLogout"/&gt;
 *     &lt;enumeration value="Engine"/&gt;
 *     &lt;enumeration value="Malfunction"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ELDEventType", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD")
@XmlEnum
public enum ELDEventType {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("DriverMark")
    DRIVER_MARK("DriverMark"),
    @XmlEnumValue("ChangeDriverStatus")
    CHANGE_DRIVER_STATUS("ChangeDriverStatus"),
    @XmlEnumValue("IntermediateLog")
    INTERMEDIATE_LOG("IntermediateLog"),
    @XmlEnumValue("ChangeDriverIndication")
    CHANGE_DRIVER_INDICATION("ChangeDriverIndication"),
    @XmlEnumValue("DriverCertification")
    DRIVER_CERTIFICATION("DriverCertification"),
    @XmlEnumValue("LoginLogout")
    LOGIN_LOGOUT("LoginLogout"),
    @XmlEnumValue("Engine")
    ENGINE("Engine"),
    @XmlEnumValue("Malfunction")
    MALFUNCTION("Malfunction");
    private final String value;

    ELDEventType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ELDEventType fromValue(String v) {
        for (ELDEventType c: ELDEventType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
