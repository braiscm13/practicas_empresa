
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ELDEventStatus.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ELDEventStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="Active"/&gt;
 *     &lt;enumeration value="InactiveChanged"/&gt;
 *     &lt;enumeration value="InactiveChangeRequested"/&gt;
 *     &lt;enumeration value="InactiveChangeRejected"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ELDEventStatus", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD")
@XmlEnum
public enum ELDEventStatus {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("Active")
    ACTIVE("Active"),
    @XmlEnumValue("InactiveChanged")
    INACTIVE_CHANGED("InactiveChanged"),
    @XmlEnumValue("InactiveChangeRequested")
    INACTIVE_CHANGE_REQUESTED("InactiveChangeRequested"),
    @XmlEnumValue("InactiveChangeRejected")
    INACTIVE_CHANGE_REJECTED("InactiveChangeRejected");
    private final String value;

    ELDEventStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ELDEventStatus fromValue(String v) {
        for (ELDEventStatus c: ELDEventStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
