
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.gesttelematica.telmanagecomunwebuser.UsuarioWebDto;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _WebResultOfUsuarioWebDto1YD7O3EO_QNAME = new QName("http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto", "WebResultOfUsuarioWebDto1YD7O3EO");
    private final static QName _WebResult_QNAME = new QName("http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto", "WebResult");
    private final static QName _WebResultErrorCode_QNAME = new QName("http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto", "ErrorCode");
    private final static QName _WebResultMessage_QNAME = new QName("http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto", "Message");
    private final static QName _WebResultOfUsuarioWebDto1YD7O3EOResult_QNAME = new QName("http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto", "Result");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telapidto
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WebResultOfUsuarioWebDto1YD7O3EO }
     * 
     */
    public WebResultOfUsuarioWebDto1YD7O3EO createWebResultOfUsuarioWebDto1YD7O3EO() {
        return new WebResultOfUsuarioWebDto1YD7O3EO();
    }

    /**
     * Create an instance of {@link WebResult }
     * 
     */
    public WebResult createWebResult() {
        return new WebResult();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WebResultOfUsuarioWebDto1YD7O3EO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto", name = "WebResultOfUsuarioWebDto1YD7O3EO")
    public JAXBElement<WebResultOfUsuarioWebDto1YD7O3EO> createWebResultOfUsuarioWebDto1YD7O3EO(WebResultOfUsuarioWebDto1YD7O3EO value) {
        return new JAXBElement<WebResultOfUsuarioWebDto1YD7O3EO>(_WebResultOfUsuarioWebDto1YD7O3EO_QNAME, WebResultOfUsuarioWebDto1YD7O3EO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WebResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto", name = "WebResult")
    public JAXBElement<WebResult> createWebResult(WebResult value) {
        return new JAXBElement<WebResult>(_WebResult_QNAME, WebResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto", name = "ErrorCode", scope = WebResult.class)
    public JAXBElement<String> createWebResultErrorCode(String value) {
        return new JAXBElement<String>(_WebResultErrorCode_QNAME, String.class, WebResult.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto", name = "Message", scope = WebResult.class)
    public JAXBElement<String> createWebResultMessage(String value) {
        return new JAXBElement<String>(_WebResultMessage_QNAME, String.class, WebResult.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UsuarioWebDto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Ws_JaltestTelematicsAPI.Dto", name = "Result", scope = WebResultOfUsuarioWebDto1YD7O3EO.class)
    public JAXBElement<UsuarioWebDto> createWebResultOfUsuarioWebDto1YD7O3EOResult(UsuarioWebDto value) {
        return new JAXBElement<UsuarioWebDto>(_WebResultOfUsuarioWebDto1YD7O3EOResult_QNAME, UsuarioWebDto.class, WebResultOfUsuarioWebDto1YD7O3EO.class, value);
    }

}
