
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ActivityBlockDownloadType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ActivityBlockDownloadType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Complete"/&gt;
 *     &lt;enumeration value="LastODFDownload"/&gt;
 *     &lt;enumeration value="DatesSelection"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ActivityBlockDownloadType")
@XmlEnum
public enum ActivityBlockDownloadType {

    @XmlEnumValue("Complete")
    COMPLETE("Complete"),
    @XmlEnumValue("LastODFDownload")
    LAST_ODF_DOWNLOAD("LastODFDownload"),
    @XmlEnumValue("DatesSelection")
    DATES_SELECTION("DatesSelection");
    private final String value;

    ActivityBlockDownloadType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ActivityBlockDownloadType fromValue(String v) {
        for (ActivityBlockDownloadType c: ActivityBlockDownloadType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
