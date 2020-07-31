package com.opentach.common.tacho.data;

import java.util.Date;

public class CambioHorario extends AbstractData implements Comparable<CambioHorario> {
	public String	matricula	= null;
	public Date		oldTime;
	public Date		newTime;
	public String	centroEnsayo;
	public String	trjCentroEnsayo;
	public String	direccionCentroEnsayo;

	public Date getOldTime() {
		return this.oldTime;
	}

	public void setOldTime(Date oldTime) {
		this.oldTime = oldTime;
	}

	public Date getNewTime() {
		return this.newTime;
	}

	public void setNewTime(Date newTime) {
		this.newTime = newTime;
	}

	public String getCentroEnsayo() {
		return this.centroEnsayo;
	}

	public void setCentroEnsayo(String centroEnsayo) {
		this.centroEnsayo = centroEnsayo;
	}

	public String getTrjCentroEnsayo() {
		return this.trjCentroEnsayo;
	}

	public void setTrjCentroEnsayo(String trjCentroEnsayo) {
		this.trjCentroEnsayo = trjCentroEnsayo;
	}

	public String getDireccionCentroEnsayo() {
		return this.direccionCentroEnsayo;
	}

	public void setDireccionCentroEnsayo(String direccionCentroEnsayo) {
		this.direccionCentroEnsayo = direccionCentroEnsayo;
	}

	@Override
	public int compareTo(CambioHorario o) {
		return this.oldTime.compareTo(o.oldTime);
	}

	public String getMatricula() {
		return this.matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

}
