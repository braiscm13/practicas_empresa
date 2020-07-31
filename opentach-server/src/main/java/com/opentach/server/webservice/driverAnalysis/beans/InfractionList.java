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
@XmlType(name = "infraction", propOrder = { "infraction" })
public class InfractionList {

	@XmlElement(required = true, nillable = false)
	private List<Infraction>	infraction;

	public List<Infraction> getInfraction() {
		return this.infraction;
	}

	public void setInfraction(List<Infraction> infractions) {
		this.infraction = infractions;
	}
}
