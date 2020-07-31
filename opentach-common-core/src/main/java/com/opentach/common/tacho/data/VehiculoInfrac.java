package com.opentach.common.tacho.data;

import java.util.List;
import java.util.Vector;

public class VehiculoInfrac extends AbstractData implements Comparable<VehiculoInfrac> {

	public String	matricula			= AbstractData.UNKNOWN_VEHICLE_NUMBER;
	public List<Object>	vehiculosInsp	= null;


	public String getMatricula() {
		return this.matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = this.getCleanVehicleNumber(matricula);
	}

	public void setVehiculoInsp(List<Object> v) {
		this.vehiculosInsp = v;
	}

	public List<Object> getVehiculoInsp() {
		return this.vehiculosInsp;
	}

	public void addVehiculoInsp(Object a) {
		if (this.vehiculosInsp == null) {
			this.vehiculosInsp = new Vector<Object>();
		}
		this.vehiculosInsp.add(a);
	}

	@Override
	public int compareTo(VehiculoInfrac o) {
		return this.matricula.compareTo((o.matricula));
	}

}
