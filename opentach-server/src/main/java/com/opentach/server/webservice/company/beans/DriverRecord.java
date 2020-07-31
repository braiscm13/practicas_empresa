package com.opentach.server.webservice.company.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "driver", propOrder = { "dni", "name", "surname", "creationDate", "email", "phone" })
public class DriverRecord {

	private String	dni;
	private String	name;
	private String	surname;
	private String	creationDate;
	private String	email;
	private String	phone;

	public DriverRecord() {
		this(null, null, null, null, null, null);
	}

	public DriverRecord(String dni, String name, String surname, String creationDate, String email, String phone) {
		super();
		this.dni = dni;
		this.name = name;
		this.surname = surname;
		this.creationDate = creationDate;
		this.email = email;
		this.phone = phone;
	}

	public String getDni() {
		return this.dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
