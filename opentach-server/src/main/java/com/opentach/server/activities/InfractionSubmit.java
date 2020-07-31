package com.opentach.server.activities;

import java.util.concurrent.Future;

/**
 * The Class Submit.
 */
public class InfractionSubmit<T> {

	/** The future. */
	private final Future<T>	future;

	/** The contract. */
	private final Object	contract;

	/** The driver. */
	private final Object	driver;
	private final Object	cif;

	/**
	 * Instantiates a new submit.
	 *
	 * @param future
	 *            the future
	 * @param contract
	 *            the contract
	 * @param driver
	 *            the driver
	 */
	public InfractionSubmit(Future<T> future, Object contract, Object cif, Object driver) {
		super();
		this.future = future;
		this.contract = contract;
		this.driver = driver;
		this.cif = cif;
	}

	/**
	 * Gets the future.
	 *
	 * @return the future
	 */
	public Future<T> getFuture() {
		return this.future;
	}

	/**
	 * Gets the contract.
	 *
	 * @return the contract
	 */
	public Object getContract() {
		return this.contract;
	}

	/**
	 * Gets the driver.
	 *
	 * @return the driver
	 */
	public Object getDriver() {
		return this.driver;
	}

	public Object getCif() {
		return this.cif;
	}

}