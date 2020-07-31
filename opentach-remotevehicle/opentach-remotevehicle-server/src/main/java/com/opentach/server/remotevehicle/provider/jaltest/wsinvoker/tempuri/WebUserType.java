
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para WebUserType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="WebUserType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="FleetManager"/&gt;
 *     &lt;enumeration value="Administrator"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "WebUserType")
@XmlEnum
public enum WebUserType {

    @XmlEnumValue("FleetManager")
    FLEET_MANAGER("FleetManager"),
    @XmlEnumValue("Administrator")
    ADMINISTRATOR("Administrator");
    private final String value;

    WebUserType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WebUserType fromValue(String v) {
        for (WebUserType c: WebUserType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
