
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para VehicleType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="VehicleType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Truck"/&gt;
 *     &lt;enumeration value="Bus"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "VehicleType")
@XmlEnum
public enum VehicleType {

    @XmlEnumValue("Truck")
    TRUCK("Truck"),
    @XmlEnumValue("Bus")
    BUS("Bus");
    private final String value;

    VehicleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VehicleType fromValue(String v) {
        for (VehicleType c: VehicleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
