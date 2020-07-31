package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.tempuri;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 3.2.0
 * 2018-06-20T10:59:16.165+02:00
 * Generated source version: 3.2.0
 * 
 */
@WebServiceClient(name = "WsJaltestTelematicsAPI", 
                  wsdlLocation = "classpath:remotevehicle-providers/jaltest/jaltestAPI.wsdl",
                  targetNamespace = "http://tempuri.org/") 
public class WsJaltestTelematicsAPI extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://tempuri.org/", "WsJaltestTelematicsAPI");
    public final static QName BasicHttpBindingIWsJaltestTelematicsAPI = new QName("http://tempuri.org/", "BasicHttpBinding_IWsJaltestTelematicsAPI");
    static {
        URL url = WsJaltestTelematicsAPI.class.getClassLoader().getResource("remotevehicle-providers/jaltest/jaltestAPI.wsdl");
        if (url == null) {
            java.util.logging.Logger.getLogger(WsJaltestTelematicsAPI.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "classpath:remotevehicle-providers/jaltest/jaltestAPI.wsdl");
        }       
        WSDL_LOCATION = url;   
    }

    public WsJaltestTelematicsAPI(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public WsJaltestTelematicsAPI(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WsJaltestTelematicsAPI() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    public WsJaltestTelematicsAPI(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public WsJaltestTelematicsAPI(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public WsJaltestTelematicsAPI(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }    




    /**
     *
     * @return
     *     returns IWsJaltestTelematicsAPI
     */
    @WebEndpoint(name = "BasicHttpBinding_IWsJaltestTelematicsAPI")
    public IWsJaltestTelematicsAPI getBasicHttpBindingIWsJaltestTelematicsAPI() {
        return super.getPort(BasicHttpBindingIWsJaltestTelematicsAPI, IWsJaltestTelematicsAPI.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns IWsJaltestTelematicsAPI
     */
    @WebEndpoint(name = "BasicHttpBinding_IWsJaltestTelematicsAPI")
    public IWsJaltestTelematicsAPI getBasicHttpBindingIWsJaltestTelematicsAPI(WebServiceFeature... features) {
        return super.getPort(BasicHttpBindingIWsJaltestTelematicsAPI, IWsJaltestTelematicsAPI.class, features);
    }

}
