package com.opentach.server.webservice.driverAnalysis.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Listado de iinfracciones.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "workPeriod", propOrder = { "period" })
public class WorkPeriodList {

	@XmlElement(required = true, nillable = false)
	private List<WorkPeriod> period;

	public List<WorkPeriod> getPeriod() {
		return this.period;
	}

	public void setPeriod(List<WorkPeriod> period) {
		this.period = period;
	}

}
