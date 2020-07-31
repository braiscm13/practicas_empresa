package com.opentach.server.tdi.sync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TdiDriverQueryBean {
	// identificador del conductor.
	@JsonProperty(value = "DRIVER_ID")
	private String	driverId;
	// nombre del conductor.
	@JsonProperty(value = "NAME")
	private String	name;
	// apellido del conductor.
	@JsonProperty(value = "SURNAME")
	private String	surname;
	// apellido del conductor (continuacion).
	@JsonProperty(value = "SURNAME2")
	private String	surname2;
	// DNI del conductor.
	@JsonProperty(value = "DNI")
	private String	dni;
	// apodo del conductor.
	@JsonProperty(value = "ALIAS")
	private String	alias;
	// dirección de residencia del conductor.
	@JsonProperty(value = "ADDRESS")
	private String	address;
	// ciudad de residencia del conductor.
	@JsonProperty(value = "CITY")
	private String	city;
	// provincia de residencia del conductor.
	@JsonProperty(value = "PROVINCE")
	private String	province;
	// país de residencia del conductor.
	@JsonProperty(value = "COUNTRY")
	private String	country;
	// primer número de teléfono del conductor.
	@JsonProperty(value = "TELEPHONE1")
	private String	telephone1;
	// segundo número de teléfono del conductor.
	@JsonProperty(value = "TELEPHONE2")
	private String	telephone2;
	// identificador del grupo al que pertenece el conductor.
	@JsonProperty(value = "GROUP_ID")
	private String	group_id;
	// otro identificador.
	@JsonProperty(value = "ID1")
	private String	id1;
	// otro identificador.
	@JsonProperty(value = "ID2")
	private String	id2;
	// otro identificador.
	@JsonProperty(value = "ID3")
	private String	id3;
	// cliente para el cual trabaja el conductor.
	@JsonProperty(value = "CLIENT_ID")
	private String	clientId;
	// fecha de caducidad del ADR.
	@JsonProperty(value = "ADR")
	private String	adr;
	// indica si el conductor esta activo (0) o de baja (1).
	@JsonProperty(value = "INACTIVE")
	private String	inactive;

	public TdiDriverQueryBean() {
		super();
	}

	public String getDriverId() {
		return this.driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
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

	public String getSurname2() {
		return this.surname2;
	}

	public void setSurname2(String surname2) {
		this.surname2 = surname2;
	}

	public String getDni() {
		return this.dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTelephone1() {
		return this.telephone1;
	}

	public void setTelephone1(String telephone1) {
		this.telephone1 = telephone1;
	}

	public String getTelephone2() {
		return this.telephone2;
	}

	public void setTelephone2(String telephone2) {
		this.telephone2 = telephone2;
	}

	public String getGroup_id() {
		return this.group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getId1() {
		return this.id1;
	}

	public void setId1(String id1) {
		this.id1 = id1;
	}

	public String getId2() {
		return this.id2;
	}

	public void setId2(String id2) {
		this.id2 = id2;
	}

	public String getId3() {
		return this.id3;
	}

	public void setId3(String id3) {
		this.id3 = id3;
	}

	public String getClientId() {
		return this.clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getAdr() {
		return this.adr;
	}

	public void setAdr(String adr) {
		this.adr = adr;
	}

	public String getInactive() {
		return this.inactive;
	}

	public void setInactive(String inactive) {
		this.inactive = inactive;
	}

}
