package com.opentach.server.tdi.sync;

public class TdiDriverAddBean {
	// identificador del conductor. Obligatorio.
	private String	driverid;
	// nombre del conductor.
	private String	drivername;
	// apellido del conductor.
	private String	driversurname;
	// apellido del conductor (continuación)
	private String	driversurname2;
	// DNI del conductor.
	private String	driverdni;
	// apodo del conductor.
	private String	driveralias;
	// dirección de residencia del conductor.
	private String	address;
	// ciudad de residencia del conductor.
	private String	city;
	// provincia de residencia del conductor.
	private String	province;
	// país de residencia del conductor.
	private String	country;
	// primer número de teléfono del conductor.
	private String	tlf1;
	// segundo número de teléfono del conductor.
	private String	tlf2;
	// identificador del grupo al que pertenece el conductor.
	private String	groupid;
	// otro identificador.
	private String	id;
	// otro identificador
	private String	id1;
	// otro identificador
	private String	id2;
	// cliente para el cual trabaja el conductor.
	private String	clientid;
	// fecha de expiración del ADR.
	private String	adr;
	// indica si el conductor está activo (0) o de baja (1).
	private String	inactive;

	public TdiDriverAddBean() {
		super();
	}

	public String getDriverid() {
		return this.driverid;
	}

	public void setDriverid(String driverid) {
		this.driverid = driverid;
	}

	public String getDrivername() {
		return this.drivername;
	}

	public void setDrivername(String drivername) {
		this.drivername = drivername;
	}

	public String getDriversurname() {
		return this.driversurname;
	}

	public void setDriversurname(String driversurname) {
		this.driversurname = driversurname;
	}

	public String getDriversurname2() {
		return this.driversurname2;
	}

	public void setDriversurname2(String driversurname2) {
		this.driversurname2 = driversurname2;
	}

	public String getDriverdni() {
		return this.driverdni;
	}

	public void setDriverdni(String driverdni) {
		this.driverdni = driverdni;
	}

	public String getDriveralias() {
		return this.driveralias;
	}

	public void setDriveralias(String driveralias) {
		this.driveralias = driveralias;
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

	public String getTlf1() {
		return this.tlf1;
	}

	public void setTlf1(String tlf1) {
		this.tlf1 = tlf1;
	}

	public String getTlf2() {
		return this.tlf2;
	}

	public void setTlf2(String tlf2) {
		this.tlf2 = tlf2;
	}

	public String getGroupid() {
		return this.groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getClientid() {
		return this.clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
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
