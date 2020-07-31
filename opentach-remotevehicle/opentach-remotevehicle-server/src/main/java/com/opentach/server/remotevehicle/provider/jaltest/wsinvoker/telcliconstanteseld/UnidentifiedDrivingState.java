
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para UnidentifiedDrivingState.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="UnidentifiedDrivingState"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Error"/&gt;
 *     &lt;enumeration value="Unassigned"/&gt;
 *     &lt;enumeration value="Assigned"/&gt;
 *     &lt;enumeration value="Approved"/&gt;
 *     &lt;enumeration value="Rejected"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "UnidentifiedDrivingState", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD")
@XmlEnum
public enum UnidentifiedDrivingState {

    @XmlEnumValue("Error")
    ERROR("Error"),
    @XmlEnumValue("Unassigned")
    UNASSIGNED("Unassigned"),
    @XmlEnumValue("Assigned")
    ASSIGNED("Assigned"),
    @XmlEnumValue("Approved")
    APPROVED("Approved"),
    @XmlEnumValue("Rejected")
    REJECTED("Rejected");
    private final String value;

    UnidentifiedDrivingState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UnidentifiedDrivingState fromValue(String v) {
        for (UnidentifiedDrivingState c: UnidentifiedDrivingState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
