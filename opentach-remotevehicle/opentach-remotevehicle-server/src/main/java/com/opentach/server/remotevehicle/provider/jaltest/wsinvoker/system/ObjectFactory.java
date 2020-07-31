
package com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.system;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.system package. 
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

    private final static QName _ArrayOfTupleOfstringstring_QNAME = new QName("http://schemas.datacontract.org/2004/07/System", "ArrayOfTupleOfstringstring");
    private final static QName _TupleOfstringstring_QNAME = new QName("http://schemas.datacontract.org/2004/07/System", "TupleOfstringstring");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.system
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ArrayOfTupleOfstringstring }
     * 
     */
    public ArrayOfTupleOfstringstring createArrayOfTupleOfstringstring() {
        return new ArrayOfTupleOfstringstring();
    }

    /**
     * Create an instance of {@link TupleOfstringstring }
     * 
     */
    public TupleOfstringstring createTupleOfstringstring() {
        return new TupleOfstringstring();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfTupleOfstringstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System", name = "ArrayOfTupleOfstringstring")
    public JAXBElement<ArrayOfTupleOfstringstring> createArrayOfTupleOfstringstring(ArrayOfTupleOfstringstring value) {
        return new JAXBElement<ArrayOfTupleOfstringstring>(_ArrayOfTupleOfstringstring_QNAME, ArrayOfTupleOfstringstring.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TupleOfstringstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System", name = "TupleOfstringstring")
    public JAXBElement<TupleOfstringstring> createTupleOfstringstring(TupleOfstringstring value) {
        return new JAXBElement<TupleOfstringstring>(_TupleOfstringstring_QNAME, TupleOfstringstring.class, null, value);
    }

}
