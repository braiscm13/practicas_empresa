package com.opentach.common.tacho.data;

import java.util.Date;

import com.imatia.tacho.model.vu.ActivityChangeInfo;

public class Actividad extends AbstractData implements Comparable<Actividad> {
	public static final String	DRIVER_SLOT			= "C";
	public static final String	CODRIVER_SLOT		= "S";
	public static final String	CARD_INSERTED		= "I";
	public static final String	CARD_NOT_INSERTED	= "N";

	public static final int		INDEFINIDA			= 5;
	public static final int		CONDUCCION			= 4;
	public static final int		TRABAJO				= 3;
	public static final int		DISPONIBILIDAD		= 2;
	public static final int		PAUSA_DESCANSO		= 1;

	public Integer				idActividad			= null;
	public Number				minutos				= null;
	public Number				tpActividad			= null;
	public Date					fecComienzo			= null;
	public Date					fecFin				= null;
	public String				ranura				= null;
	public String				estTrjRanura		= null;
	public String				fueraAmbito			= "N";
	public String				transTren			= "N";
	public String				procedencia			= null;
	public String				regimen				= null;
	public String				origen				= null;
	public String				idConductor			= Conductor.UNKNOWN_DRIVER;
	public String				matricula			= AbstractData.UNKNOWN_VEHICLE_NUMBER;
	public String				tipoOrigen			= null;
	public String				numTarjeta			= null;

	public Actividad() {
		super();
	}

	public Actividad(String idcond, Date fini, Date ffin) {
		this.setIdConductor(idcond);
		this.fecComienzo = fini;
		if (ffin != null) {
			this.setFecFin(ffin);
		}
	}

	public Actividad(Date day, ActivityChangeInfo begin, ActivityChangeInfo end, String procedencia) {
		if ((begin == null) || (end == null)) {
			throw new IllegalArgumentException("ActivityChangeInfo parameters begin and end must be not null");
		}
		this.tpActividad = new Integer(begin.getActividad());
		this.tipoOrigen = procedencia;
		this.estTrjRanura = (begin.getEstadoTarjeta() == ActivityChangeInfo.ESTADO_TARJETA_INSERTADA ? Actividad.CARD_INSERTED : Actividad.CARD_NOT_INSERTED);
		this.ranura = (begin.getRanura() == ActivityChangeInfo.RANURA_CONDUCTOR ? Actividad.DRIVER_SLOT : Actividad.CODRIVER_SLOT);
		this.setFecComienzo(new Date(day.getTime() + ((long) begin.getMinutosCambio() * 60 * 1000)));
		this.setFecFin(new Date(day.getTime() + ((long) end.getMinutosCambio() * 60 * 1000)));
	}

	public String getNumTarjeta() {
		return this.numTarjeta;
	}

	public void setNumTarjeta(String numTarjeta) {
		this.numTarjeta = numTarjeta;
	}

	public void setTipoOrigen(String tipoOrigen) {
		this.tipoOrigen = tipoOrigen;
	}

	public String getTipoOrigen() {
		return this.tipoOrigen;
	}

	public String getMatricula() {
		return this.matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = this.getCleanVehicleNumber(matricula);
	}

	public Number getMinutos() {
		return this.minutos;
	}

	public void setMinutos(Integer minutos) {
		this.minutos = minutos;
		if (this.fecComienzo != null) {
			this.fecFin = new Date(this.fecComienzo.getTime() + ((long) (minutos.intValue()) * 60 * 1000));
		}
	}

	public String getIdConductor() {
		return this.idConductor;
	}

	public void setIdConductor(String idConductor) {
		this.idConductor = idConductor != null ? idConductor.trim() : Conductor.UNKNOWN_DRIVER;
	}

	public Date getFecFin() {
		return this.fecFin;
	}

	public void setFecFin(Date fecFin) {
		this.fecFin = fecFin;
		if ((this.fecComienzo != null) && (fecFin != null) && (fecFin.equals(this.fecComienzo) || fecFin.after(this.fecComienzo))) {
			this.minutos = new Integer((int) ((fecFin.getTime() - this.fecComienzo.getTime()) / (1000 * 60)));
		}
	}

	public Date getFecComienzo() {
		return this.fecComienzo;
	}

	public void setFecComienzo(Date fecComienzo) {
		this.fecComienzo = fecComienzo;
	}

	public Number getTpActividad() {
		return this.tpActividad;
	}

	public void setTpActividad(Integer tpActividad) {
		this.tpActividad = tpActividad;
	}

	public String getRanura() {
		return this.ranura;
	}

	public void setRanura(String ranura) {
		this.ranura = ranura.trim();
	}

	public String getRegimen() {
		return this.regimen;
	}

	public void setRegimen(String regimen) {
		this.regimen = regimen.trim();
	}

	public String getEstTrjRanura() {
		return this.estTrjRanura;
	}

	public void setEstTrjRanura(String estTrjRanura) {
		this.estTrjRanura = estTrjRanura.trim();
	}

	public String getFueraAmbito() {
		return this.fueraAmbito;
	}

	public void setFueraAmbito(String fueraAmbito) {
		this.fueraAmbito = fueraAmbito.trim();
	}

	public String getTransTren() {
		return this.transTren;
	}

	public void setTransTren(String transTren) {
		this.transTren = transTren.trim();
	}

	public String getProcedencia() {
		return this.procedencia;
	}

	public void setProcedencia(String procedencia) {
		this.procedencia = procedencia.trim();
	}

	public String getOrigen() {
		return this.origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen == null ? null : origen.trim();
	}

	@Override
	public int compareTo(Actividad o) {
		return this.fecComienzo.compareTo(o.fecComienzo);
	}

}
