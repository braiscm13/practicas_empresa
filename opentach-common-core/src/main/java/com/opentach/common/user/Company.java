package com.opentach.common.user;

/**
 * The Class Company.
 */
public class Company {

	/** The name. */
	private String	name;

	/** The cif. */
	private String	cif;
	private Object	activeContract;

	/**
	 * The Constructor.
	 */
	public Company() {
		super();
	}

	/**
	 * The Constructor.
	 *
	 * @param name
	 *            the name
	 * @param cif
	 *            the cif
	 */
	public Company(String name, String cif, Object activeContract) {
		super();
		this.name = name;
		this.cif = cif;
		this.activeContract = activeContract;
	}

	public Object getActiveContract() {
		return this.activeContract;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the cif.
	 *
	 * @return the cif
	 */
	public String getCif() {
		return this.cif;
	}

	/**
	 * Sets the cif.
	 *
	 * @param cif
	 *            the cif
	 */
	public void setCif(String cif) {
		this.cif = cif;
	}

	@Override
	public String toString() {
		return this.cif + " - " + this.name;
	}

}