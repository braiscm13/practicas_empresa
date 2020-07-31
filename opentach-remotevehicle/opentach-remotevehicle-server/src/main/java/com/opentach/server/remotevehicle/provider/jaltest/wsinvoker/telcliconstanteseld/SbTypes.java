
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para SbTypes.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="SbTypes"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Error"/&gt;
 *     &lt;enumeration value="SB_EIGHT_TWO"/&gt;
 *     &lt;enumeration value="SB_MIN_TOTAL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "SbTypes", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD")
@XmlEnum
public enum SbTypes {

    @XmlEnumValue("Error")
    ERROR("Error"),
    SB_EIGHT_TWO("SB_EIGHT_TWO"),
    SB_MIN_TOTAL("SB_MIN_TOTAL");
    private final String value;

    SbTypes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SbTypes fromValue(String v) {
        for (SbTypes c: SbTypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
