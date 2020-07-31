package com.opentach.ws.common.rest.beans;

import java.io.Serializable;

public class Station implements Serializable {

	/** Unique identifier. */
	protected String	id;

	/** Name. */
	protected String	name;

	/** Textual address */
	protected String	address;

	/** Textual province (separated becaused groupped in UI by this data) */
	protected String	province;

	/** Latitude info of address */
	protected Double	latitude;

	/** Longitude info of address */
	protected Double	longitude;

	/** Define is this station has the petrol service. */
	protected boolean	servicePetrol;

	/** Define is this station has the market service. */
	protected boolean	serviceMarket;

	/** Define is this station has the bank service. */
	protected boolean	serviceBank;

	/** Define is this station is openned all day. */
	protected boolean	serviceFullDay;

	/** Define is this station has the restaurant service. */
	protected boolean	serviceRestaurant;

	/** Define is this station has the accommodation service. */
	protected boolean	serviceAccomodation;

	public Station() {}

	public Station(String id, String name) {
		this(id, name, null, null, null, null);
	}

	public Station(String id, String name, String address, String province, Double latitude, Double longitude) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.province = province;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the province name
	 */
	public String getProvince() {
		return this.province;
	}

	/**
	 * @param province
	 * 		the province name to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * @return the latitude
	 */
	public Double getLatitude() {
		return this.latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public Double getLongitude() {
		return this.longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the servicePetrol
	 */
	public boolean isServicePetrol() {
		return this.servicePetrol;
	}

	/**
	 * @param servicePetrol
	 *            the servicePetrol to set
	 */
	public void setServicePetrol(boolean servicePetrol) {
		this.servicePetrol = servicePetrol;
	}

	/**
	 * @return the serviceMarket
	 */
	public boolean isServiceMarket() {
		return this.serviceMarket;
	}

	/**
	 * @param serviceMarket
	 *            the serviceMarket to set
	 */
	public void setServiceMarket(boolean serviceMarket) {
		this.serviceMarket = serviceMarket;
	}

	/**
	 * @return the serviceBank
	 */
	public boolean isServiceBank() {
		return this.serviceBank;
	}

	/**
	 * @param serviceBank
	 *            the serviceBank to set
	 */
	public void setServiceBank(boolean serviceBank) {
		this.serviceBank = serviceBank;
	}

	/**
	 * @return the serviceFullDay
	 */
	public boolean isServiceFullDay() {
		return this.serviceFullDay;
	}

	/**
	 * @param serviceFullDay
	 *            the serviceFullDay to set
	 */
	public void setServiceFullDay(boolean serviceFullDay) {
		this.serviceFullDay = serviceFullDay;
	}

	/**
	 * @return the serviceRestaurant
	 */
	public boolean isServiceRestaurant() {
		return this.serviceRestaurant;
	}

	/**
	 * @param serviceRestaurant
	 *            the serviceRestaurant to set
	 */
	public void setServiceRestaurant(boolean serviceRestaurant) {
		this.serviceRestaurant = serviceRestaurant;
	}

	/**
	 * @return the serviceAccomodation
	 */
	public boolean isServiceAccomodation() {
		return this.serviceAccomodation;
	}

	/**
	 * @param serviceAccomodation
	 *            the serviceAccomodation to set
	 */
	public void setServiceAccomodation(boolean serviceAccomodation) {
		this.serviceAccomodation = serviceAccomodation;
	}
}
