package com.opentach.common.tacho.data;

public class Tarjeta extends AbstractData {

	public String	driverIdentification;
	public String	ownerIdentification;
	public char		cardConsecutiveIndex;
	public char		cardReplacementIndex;
	public char		cardRenewalIndex;

	public Tarjeta() {}

	public String getDriverIdentification() {
		return this.driverIdentification;
	}

	public void DriverIdentification(String driver) {
		this.driverIdentification = driver.trim();
	}

	public String getOwnerIdentification() {
		return this.ownerIdentification;
	}

	public void setOwnerIdentification(String ownerIdentification) {
		this.ownerIdentification = ownerIdentification;
	}

	public char getCardConsecutiveIndex() {
		return this.cardConsecutiveIndex;
	}

	public void setCardConsecutiveIndex(char cardConsecutiveIndex) {
		this.cardConsecutiveIndex = cardConsecutiveIndex;
	}

	public char getCardReplacementIndex() {
		return this.cardReplacementIndex;
	}

	public void setCardReplacementIndex(char cardReplacementIndex) {
		this.cardReplacementIndex = cardReplacementIndex;
	}

	public char getCardRenewalIndex() {
		return this.cardRenewalIndex;
	}

	public void setCardRenewalIndex(char cardRenewalIndex) {
		this.cardRenewalIndex = cardRenewalIndex;
	}

	public void setDriverIdentification(String driverIdentification) {
		this.driverIdentification = driverIdentification;
	}
	/**
	 * Recupera el identificador del conductor. Corresponde a la subcadena del numero de tarjeta de posiciones 1 a la 10.
	 *
	 * @return
	 */

	// public static String getDNIConductor(String numtarjeta) {
	// try {
	// return numtarjeta.substring(1, 10);
	// } catch (StringIndexOutOfBoundsException iex) {
	// // iex.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return Conductor.UNKNOWN_DRIVER;
	// }
	//
	// public static String getIDConductor(String driverIdentification) {
	// String idconductor = null;
	// try {
	// idconductor = driverIdentification.trim();
	// if (idconductor.length() == 0)
	// idconductor = Conductor.UNKNOWN_DRIVER;
	// } catch (Exception e) {
	// idconductor = Conductor.UNKNOWN_DRIVER;
	// }
	// return idconductor;
	// }

}
