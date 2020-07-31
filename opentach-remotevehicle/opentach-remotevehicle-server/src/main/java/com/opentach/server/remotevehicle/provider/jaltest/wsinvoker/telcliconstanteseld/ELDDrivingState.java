
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ELDDrivingState.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ELDDrivingState"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Error"/&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="OFF"/&gt;
 *     &lt;enumeration value="SB"/&gt;
 *     &lt;enumeration value="D"/&gt;
 *     &lt;enumeration value="ON"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ELDDrivingState", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD")
@XmlEnum
public enum ELDDrivingState {

    @XmlEnumValue("Error")
    ERROR("Error"),
    @XmlEnumValue("None")
    NONE("None"),
    OFF("OFF"),
    SB("SB"),
    D("D"),
    ON("ON");
    private final String value;

    ELDDrivingState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ELDDrivingState fromValue(String v) {
        for (ELDDrivingState c: ELDDrivingState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
