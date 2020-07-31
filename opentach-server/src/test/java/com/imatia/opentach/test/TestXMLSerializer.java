package com.imatia.opentach.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

public class TestXMLSerializer {
	public static void main(String[] args) throws JAXBException {
		Hashtable<String, String> test = new Hashtable<>();
		test.put("hola", "adios");
		test.put("mas", "menos");
		JAXBContext jaxbContext = JAXBContext.newInstance(TestWrap.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		jaxbMarshaller.marshal(new TestWrap(test), baos);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		TestWrap res = (TestWrap) unmarshaller.unmarshal(bais);
		System.out.println(res);
	}

	@XmlRootElement
	public static class TestWrap {
		Hashtable<String, String> data;

		public TestWrap() {
			super();

		}

		public TestWrap(Hashtable<String, String> data) {
			this.data = data;
		}

		public Hashtable<String, String> getData() {
			return this.data;
		}

		public void setData(Hashtable<String, String> data) {
			this.data = data;
		}
	}
}
