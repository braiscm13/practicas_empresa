
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld package. 
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

    private final static QName _SbTypes_QNAME = new QName("http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD", "SbTypes");
    private final static QName _ELDEventOrigin_QNAME = new QName("http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD", "ELDEventOrigin");
    private final static QName _ELDEventStatus_QNAME = new QName("http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD", "ELDEventStatus");
    private final static QName _ELDEventType_QNAME = new QName("http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD", "ELDEventType");
    private final static QName _ELDDrivingState_QNAME = new QName("http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD", "ELDDrivingState");
    private final static QName _UnidentifiedDrivingState_QNAME = new QName("http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD", "UnidentifiedDrivingState");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.telcliconstanteseld
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SbTypes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD", name = "SbTypes")
    public JAXBElement<SbTypes> createSbTypes(SbTypes value) {
        return new JAXBElement<SbTypes>(_SbTypes_QNAME, SbTypes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ELDEventOrigin }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD", name = "ELDEventOrigin")
    public JAXBElement<ELDEventOrigin> createELDEventOrigin(ELDEventOrigin value) {
        return new JAXBElement<ELDEventOrigin>(_ELDEventOrigin_QNAME, ELDEventOrigin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ELDEventStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD", name = "ELDEventStatus")
    public JAXBElement<ELDEventStatus> createELDEventStatus(ELDEventStatus value) {
        return new JAXBElement<ELDEventStatus>(_ELDEventStatus_QNAME, ELDEventStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ELDEventType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD", name = "ELDEventType")
    public JAXBElement<ELDEventType> createELDEventType(ELDEventType value) {
        return new JAXBElement<ELDEventType>(_ELDEventType_QNAME, ELDEventType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ELDDrivingState }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD", name = "ELDDrivingState")
    public JAXBElement<ELDDrivingState> createELDDrivingState(ELDDrivingState value) {
        return new JAXBElement<ELDDrivingState>(_ELDDrivingState_QNAME, ELDDrivingState.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnidentifiedDrivingState }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/JaltestTelematicsClient.Comun.Constantes.ELD", name = "UnidentifiedDrivingState")
    public JAXBElement<UnidentifiedDrivingState> createUnidentifiedDrivingState(UnidentifiedDrivingState value) {
        return new JAXBElement<UnidentifiedDrivingState>(_UnidentifiedDrivingState_QNAME, UnidentifiedDrivingState.class, null, value);
    }

}
