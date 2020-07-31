
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para TachoFileExtension.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="TachoFileExtension"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="TGD"/&gt;
 *     &lt;enumeration value="V1BC1B"/&gt;
 *     &lt;enumeration value="DDD"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TachoFileExtension")
@XmlEnum
public enum TachoFileExtension {

    TGD("TGD"),
    @XmlEnumValue("V1BC1B")
    V_1_BC_1_B("V1BC1B"),
    DDD("DDD");
    private final String value;

    TachoFileExtension(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TachoFileExtension fromValue(String v) {
        for (TachoFileExtension c: TachoFileExtension.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
