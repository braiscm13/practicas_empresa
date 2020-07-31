package com.opentach.common.tacho.data;

import java.util.Date;

public class RegistroKmVehiculo extends AbstractData implements Comparable<RegistroKmVehiculo> {
	public String		matricula;
	public Date			fecha;
	public Integer	kilometros;

	@Override
	public int compareTo(RegistroKmVehiculo o) {
		return this.fecha.compareTo(o.fecha);
	}

	public String getMatricula() {
		return this.matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = this.getCleanVehicleNumber(matricula);
	}

	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Integer getKilometros() {
		return this.kilometros;
	}

	public void setKilometros(Integer kilometros) {
		this.kilometros = kilometros;
	}

}
