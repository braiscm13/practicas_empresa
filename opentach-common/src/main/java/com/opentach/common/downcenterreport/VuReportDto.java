package com.opentach.common.downcenterreport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Class VuReportDto.
 */
public class VuReportDto extends AbstractReportDto implements Serializable {

	/** The vehicle. */
	private String					vehicle;

	/** The km end. */
	private int						kmEnd;

	/** The km tot. */
	private int						kmTot;

	/** The km ini. */
	private int						kmIni;

	/** The driver list. */
	private final List<Driver>		driverList;

	/** The incidentes list. */
	private final List<Incidence>	incidentesList;

	/** The control list. */
	private final List<Control>		controlList;

	/** The calibrados list. */
	private final List<Calibrado>	calibradosList;

	/**
	 * Instantiates a new vu report dto.
	 */
	public VuReportDto() {
		super();
		this.driverList = new ArrayList<>();
		this.incidentesList = new ArrayList<>();
		this.controlList = new ArrayList<>();
		this.calibradosList = new ArrayList<>();
	}

	/**
	 * Sets the vehicle.
	 *
	 * @param vehicle
	 *            the new vehicle
	 */
	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}

	/**
	 * Sets the kms.
	 *
	 * @param kmini
	 *            the kmini
	 * @param kmend
	 *            the kmend
	 * @param kmtot
	 *            the kmtot
	 */
	public void setKms(int kmini, int kmend, int kmtot) {
		this.kmIni = kmini;
		this.kmEnd = kmend;
		this.kmTot = kmtot;
	}

	/**
	 * Adds the driver.
	 *
	 * @param idConductor
	 *            the id conductor
	 * @param nombre
	 *            the nombre
	 * @param apellidos
	 *            the apellidos
	 */
	public void addDriver(String idConductor, String nombre, String apellidos, String dni) {
		this.driverList.add(new Driver(idConductor, nombre, apellidos, dni));
	}

	/**
	 * Adds the incidete fallo.
	 *
	 * @param type
	 *            the type
	 * @param getfecHoraIni
	 *            the getfec hora ini
	 */
	public void addIncideteFallo(String type, Date getfecHoraIni) {
		this.incidentesList.add(new Incidence(type, getfecHoraIni));
	}

	/**
	 * Adds the control.
	 *
	 * @param fechaHora
	 *            the fecha hora
	 * @param type
	 *            the type
	 * @param numTrjControl
	 *            the num trj control
	 */
	public void addControl(Date fechaHora, String type, String numTrjControl) {
		this.controlList.add(new Control(fechaHora, type, numTrjControl));
	}

	/**
	 * Adds the calibrado.
	 *
	 * @param type
	 *            the type
	 * @param numTrjTaller
	 *            the num trj taller
	 * @param fecProximo
	 *            the fec proximo
	 */
	public void addCalibrado(String type, String numTrjTaller, Date fecProximo) {
		this.calibradosList.add(new Calibrado(type, numTrjTaller, fecProximo));
	}

	/**
	 * Gets the vehicle.
	 *
	 * @return the vehicle
	 */
	public String getVehicle() {
		return this.vehicle;
	}

	/**
	 * Gets the km end.
	 *
	 * @return the km end
	 */
	public int getKmEnd() {
		return this.kmEnd;
	}

	/**
	 * Gets the km tot.
	 *
	 * @return the km tot
	 */
	public int getKmTot() {
		return this.kmTot;
	}

	/**
	 * Gets the km ini.
	 *
	 * @return the km ini
	 */
	public int getKmIni() {
		return this.kmIni;
	}

	/**
	 * Gets the driver list.
	 *
	 * @return the driver list
	 */
	public List<Driver> getDriverList() {
		return this.driverList;
	}

	/**
	 * Gets the incidentes list.
	 *
	 * @return the incidentes list
	 */
	public List<Incidence> getIncidentesList() {
		return this.incidentesList;
	}

	/**
	 * Gets the control list.
	 *
	 * @return the control list
	 */
	public List<Control> getControlList() {
		return this.controlList;
	}

	/**
	 * Gets the calibrados list.
	 *
	 * @return the calibrados list
	 */
	public List<Calibrado> getCalibradosList() {
		return this.calibradosList;
	}

	/**
	 * The Class Driver.
	 */
	public static class Driver implements Serializable {

		/** The driver id. */
		private final String	driverId;

		/** The driver name. */
		private final String	driverName;

		/** The driver surname. */
		private final String	driverSurname;

		/** The driver DNI */
		private final String	driverDNI;

		/**
		 * Instantiates a new driver.
		 *
		 * @param idConductor
		 *            the id conductor
		 * @param nombre
		 *            the nombre
		 * @param apellidos
		 *            the apellidos
		 */
		public Driver(String idConductor, String nombre, String apellidos, String dni) {
			this.driverId = idConductor;
			this.driverName = nombre;
			this.driverSurname = apellidos;
			this.driverDNI = dni;
		}

		/**
		 * Gets the driver id.
		 *
		 * @return the driver id
		 */
		public String getDriverId() {
			return this.driverId;
		}

		/**
		 * Gets the driver name.
		 *
		 * @return the driver name
		 */
		public String getDriverName() {
			return this.driverName;
		}

		/**
		 * Gets the driver surname.
		 *
		 * @return the driver surname
		 */
		public String getDriverSurname() {
			return this.driverSurname;
		}

		/**
		 * Gets the driver DNI
		 * 
		 * @return
		 */
		public String getDriverDNI() {
			return this.driverDNI;
		}

	}

	/**
	 * The Class Incidence.
	 */
	public static class Incidence implements Serializable {

		/** The type. */
		private final String	type;

		/** The date. */
		private final Date		date;

		/**
		 * Instantiates a new incidence.
		 *
		 * @param type
		 *            the type
		 * @param getfecHoraIni
		 *            the getfec hora ini
		 */
		public Incidence(String type, Date getfecHoraIni) {
			this.type = type;
			this.date = getfecHoraIni;
		}

		/**
		 * Gets the type.
		 *
		 * @return the type
		 */
		public String getType() {
			return this.type;
		}

		/**
		 * Gets the date.
		 *
		 * @return the date
		 */
		public Date getDate() {
			return this.date;
		}

	}

	/**
	 * The Class Control.
	 */
	public static class Control implements Serializable {

		/** The date. */
		private final Date		date;

		/** The type. */
		private final String	type;

		/** The control card number. */
		private final String	controlCardNumber;

		/**
		 * Instantiates a new control.
		 *
		 * @param fechaHora
		 *            the fecha hora
		 * @param type
		 *            the type
		 * @param numTrjControl
		 *            the num trj control
		 */
		public Control(Date fechaHora, String type, String numTrjControl) {
			this.date = fechaHora;
			this.type = type;
			this.controlCardNumber = numTrjControl;
		}

		/**
		 * Gets the control card number.
		 *
		 * @return the control card number
		 */
		public String getControlCardNumber() {
			return this.controlCardNumber;
		}

		/**
		 * Gets the date.
		 *
		 * @return the date
		 */
		public Date getDate() {
			return this.date;
		}

		/**
		 * Gets the type.
		 *
		 * @return the type
		 */
		public String getType() {
			return this.type;
		}

	}

	/**
	 * The Class Calibrado.
	 */
	public static class Calibrado implements Serializable {

		/** The type. */
		private final String	type;

		/** The workshop card number. */
		private final String	workshopCardNumber;

		/** The next date. */
		private final Date		nextDate;

		/**
		 * Instantiates a new calibrado.
		 *
		 * @param type
		 *            the type
		 * @param numTrjTaller
		 *            the num trj taller
		 * @param fecProximo
		 *            the fec proximo
		 */
		public Calibrado(String type, String numTrjTaller, Date fecProximo) {
			this.type = type;
			this.workshopCardNumber = numTrjTaller;
			this.nextDate = fecProximo;
		}

		/**
		 * Gets the next date.
		 *
		 * @return the next date
		 */
		public Date getNextDate() {
			return this.nextDate;
		}

		/**
		 * Gets the type.
		 *
		 * @return the type
		 */
		public String getType() {
			return this.type;
		}

		/**
		 * Gets the workshop card number.
		 *
		 * @return the workshop card number
		 */
		public String getWorkshopCardNumber() {
			return this.workshopCardNumber;
		}

	}
}
