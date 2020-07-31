package com.opentach.common.downcenterreport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class TcReportDto.
 */
public class TcReportDto extends AbstractReportDto implements Serializable {

	/** The has erros. */
	private boolean				hasErros;

	/** The has incidences. */
	private boolean				hasIncidences;

	/** The vehicle list. */
	private final List<String>	vehicleList;

	/** The activity summary kilometers. */
	private int					activitySummaryKilometers;

	/** The activity summary working time. */
	private int					activitySummaryWorkingTime;

	/** The activity summary pause time. */
	private int					activitySummaryPauseTime;

	/** The activity summary available time. */
	private int					activitySummaryAvailableTime;

	/** The activity summary driving time. */
	private int					activitySummaryDrivingTime;

	/** The driver id. */
	private String				driverId;

	/** The driver surname. */
	private String				driverSurname;

	/** The driver name. */
	private String				driverName;

	/** The driver dni. */
	private String				driverDni;

	/**
	 * Instantiates a new tc report dto.
	 */
	public TcReportDto() {
		super();
		this.vehicleList = new ArrayList<>();
	}

	/**
	 * Sets the driver dni.
	 *
	 * @param driverDni
	 *            the new driver dni
	 */
	public void setDriverDni(String driverDni) {
		this.driverDni = driverDni;
	}

	/**
	 * Sets the driver name.
	 *
	 * @param driverName
	 *            the new driver name
	 */
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	/**
	 * Sets the driver surname.
	 *
	 * @param driverSurname
	 *            the new driver surname
	 */
	public void setDriverSurname(String driverSurname) {
		this.driverSurname = driverSurname;
	}

	/**
	 * Sets the driver id.
	 *
	 * @param driverId
	 *            the new driver id
	 */
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	/**
	 * Sets the activity summary.
	 *
	 * @param tCond
	 *            the t cond
	 * @param tDisp
	 *            the t disp
	 * @param tPausa
	 *            the t pausa
	 * @param tTrab
	 *            the t trab
	 * @param nKm
	 *            the n km
	 */
	public void setActivitySummary(int tCond, int tDisp, int tPausa, int tTrab, int nKm) {
		this.activitySummaryDrivingTime = tCond;
		this.activitySummaryAvailableTime = tDisp;
		this.activitySummaryPauseTime = tPausa;
		this.activitySummaryWorkingTime = tTrab;
		this.activitySummaryKilometers = nKm;
	}

	/**
	 * Adds the vehicle.
	 *
	 * @param matricula
	 *            the matricula
	 */
	public void addVehicle(String matricula) {
		this.vehicleList.add(matricula);
	}

	/**
	 * Sets the checks for incidences.
	 *
	 * @param b
	 *            the new checks for incidences
	 */
	public void setHasIncidences(boolean b) {
		this.hasIncidences = b;
	}

	/**
	 * Sets the checks for errors.
	 *
	 * @param b
	 *            the new checks for errors
	 */
	public void setHasErrors(boolean b) {
		this.hasErros = b;
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
	 * Checks if is checks for erros.
	 *
	 * @return true, if is checks for erros
	 */
	public boolean isHasErros() {
		return this.hasErros;
	}

	/**
	 * Checks if is checks for incidences.
	 *
	 * @return true, if is checks for incidences
	 */
	public boolean isHasIncidences() {
		return this.hasIncidences;
	}

	/**
	 * Gets the vehicle list.
	 *
	 * @return the vehicle list
	 */
	public List<String> getVehicleList() {
		return this.vehicleList;
	}

	/**
	 * Gets the activity summary kilometers.
	 *
	 * @return the activity summary kilometers
	 */
	public int getActivitySummaryKilometers() {
		return this.activitySummaryKilometers;
	}

	/**
	 * Gets the activity summary working time.
	 *
	 * @return the activity summary working time
	 */
	public int getActivitySummaryWorkingTime() {
		return this.activitySummaryWorkingTime;
	}

	/**
	 * Gets the activity summary pause time.
	 *
	 * @return the activity summary pause time
	 */
	public int getActivitySummaryPauseTime() {
		return this.activitySummaryPauseTime;
	}

	/**
	 * Gets the activity summary available time.
	 *
	 * @return the activity summary available time
	 */
	public int getActivitySummaryAvailableTime() {
		return this.activitySummaryAvailableTime;
	}

	/**
	 * Gets the activity summary driving time.
	 *
	 * @return the activity summary driving time
	 */
	public int getActivitySummaryDrivingTime() {
		return this.activitySummaryDrivingTime;
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
	 * Gets the driver name.
	 *
	 * @return the driver name
	 */
	public String getDriverName() {
		return this.driverName;
	}

	/**
	 * Gets the driver dni.
	 *
	 * @return the driver dni
	 */
	public String getDriverDni() {
		return this.driverDni;
	}

}
