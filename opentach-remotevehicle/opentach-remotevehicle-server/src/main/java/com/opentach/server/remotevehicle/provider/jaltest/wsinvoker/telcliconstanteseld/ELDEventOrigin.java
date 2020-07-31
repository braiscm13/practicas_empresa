
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ELDEventOrigin.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ELDEventOrigin"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="ELDAutomatically"/&gt;
 *     &lt;enumeration value="EditedEnteredDriver"/&gt;
 *     &lt;enumeration value="EditRequestedAuthenticatedUser"/&gt;
 *     &lt;enumeration value="AssumedUnidentifiedDriver"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ELDEventOrigin", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD")
@XmlEnum
public enum ELDEventOrigin {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("ELDAutomatically")
    ELD_AUTOMATICALLY("ELDAutomatically"),
    @XmlEnumValue("EditedEnteredDriver")
    EDITED_ENTERED_DRIVER("EditedEnteredDriver"),
    @XmlEnumValue("EditRequestedAuthenticatedUser")
    EDIT_REQUESTED_AUTHENTICATED_USER("EditRequestedAuthenticatedUser"),
    @XmlEnumValue("AssumedUnidentifiedDriver")
    ASSUMED_UNIDENTIFIED_DRIVER("AssumedUnidentifiedDriver");
    private final String value;

    ELDEventOrigin(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ELDEventOrigin fromValue(String v) {
        for (ELDEventOrigin c: ELDEventOrigin.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
