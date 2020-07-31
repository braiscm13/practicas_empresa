package com.opentach.common.tacho.data;

import java.util.Date;

public class Fallo extends AbstractData implements Comparable<Fallo> {

	public Integer tpFallo = null;
	public Integer proposito = null;
	public Date fecHoraIni = null;
	public Date fecHoraFin = null;
	public String numTrjConduIni = null;
	public String numTrjConduFin = null;
	public String numTrjCopiIni = null;
	public String numTrjCopiFin = null;
	public String matricula = AbstractData.UNKNOWN_VEHICLE_NUMBER;


	public Fallo() {
	}

	public String getMatricula(){
		return this.matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula =this.getCleanVehicleNumber(matricula);
	}

	public Integer getTpFallo(){
		return this.tpFallo;
	}

	public void setTpFallo(Integer tipo){
		this.tpFallo = tipo;
	}

	public Date getfecHoraIni(){
		return this.fecHoraIni;
	}

	public void setfecHoraIni(Date fecHoraIni){
		this.fecHoraIni = fecHoraIni;
	}

	public Date getfecHoraFin(){
		return this.fecHoraFin;
	}

	public void setfecHoraFin(Date fecHoraFin){
		this.fecHoraFin = fecHoraFin;
	}


	public String getNumTrjConduIni(){
		return this.numTrjConduIni;
	}

	public void setNumTrjConduIni(String numTrjConduIni){
		this.numTrjConduIni = numTrjConduIni.trim();
	}


	public String getNumTrjConduFin(){
		return this.numTrjConduFin;
	}

	public void setNumTrjConduFin(String numTrjConduFin){
		this.numTrjConduFin = numTrjConduFin.trim();
	}


	public String getNumTrjCopiIni(){
		return this.numTrjCopiIni;
	}

	public void setNumTrjCopiIni(String numTrjCopiIni){
		this.numTrjCopiIni = numTrjCopiIni.trim();
	}

	public String getNumTrjCopiFin(){
		return this.numTrjCopiFin;
	}

	public void setNumTrjCopiFin(String numTrjCopiFin){
		this.numTrjCopiFin = numTrjCopiFin.trim();
	}

	public Integer getProposito() {
		return this.proposito;
	}

	public void setProposito(Integer proposito) {
		this.proposito = proposito;
	}

	@Override
	public int compareTo(Fallo o) {
		return this.fecHoraIni.compareTo(o.fecHoraIni);
	}

}
