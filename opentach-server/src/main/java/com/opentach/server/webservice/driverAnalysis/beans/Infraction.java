package com.opentach.server.webservice.driverAnalysis.beans;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "infractionSingle", propOrder = { "init", "end", "infractionType", "severity" })
public class Infraction {

	// /** The scale entry. */
	// private final ScaleEntry scaleEntry;
	//
	// /** The type. */
	// private final InfractionType type;
	//
	// /** The period. */
	// private final Period period;
	//
	// /** The excess. */
	// private final int excess;
	//
	// /** The time. */
	// private final int time;
	//
	// /** The end date. */
	// private final Date endDate;
	//
	// /** The begin date. */
	// private final Date beginDate;ç

	private Date	init;

	private Date	end;

	private String	infractionType;
	private String	severity;

	public String getSeverity() {
		return this.severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public Date getInit() {
		return this.init;
	}

	public void setInit(Date init) {
		this.init = init;
	}

	public Date getEnd() {
		return this.end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getInfractionType() {
		return this.infractionType;
	}

	public void setInfractionType(String infractionType) {
		this.infractionType = infractionType;
	}

}
