
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telmanagecomunconstants;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para UsuarioWebConstantes.Perfil.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="UsuarioWebConstantes.Perfil"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="JefeDeFlota"/&gt;
 *     &lt;enumeration value="Taller"/&gt;
 *     &lt;enumeration value="Conductor"/&gt;
 *     &lt;enumeration value="Administrador"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "UsuarioWebConstantes.Perfil", namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsManageComun.Constantes")
@XmlEnum
public enum UsuarioWebConstantesPerfil {

    @XmlEnumValue("JefeDeFlota")
    JEFE_DE_FLOTA("JefeDeFlota"),
    @XmlEnumValue("Taller")
    TALLER("Taller"),
    @XmlEnumValue("Conductor")
    CONDUCTOR("Conductor"),
    @XmlEnumValue("Administrador")
    ADMINISTRADOR("Administrador");
    private final String value;

    UsuarioWebConstantesPerfil(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UsuarioWebConstantesPerfil fromValue(String v) {
        for (UsuarioWebConstantesPerfil c: UsuarioWebConstantesPerfil.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
