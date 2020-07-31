package com.opentach.common.tacho.data;

import java.util.Date;

public class UsoTarjeta extends AbstractData implements Comparable<UsoTarjeta> {

	public Date		fechaHoraInsercion		= null;
	public Date		fechaHoraExtraccion		= null;
	public Integer	KmInsercion				= null;
	public Integer	KmExtraccion			= null;
	public String	numTrjConductor			= null;
	public String	idConductor				= null;

	public String	nombre					= null;
	public String	apellidos				= null;

	public String	matricula				= AbstractData.UNKNOWN_VEHICLE_NUMBER;
	public String	ranura					= null;
	public String	matriculaAnterior		= null;
	public Date		fechaHoraExtAnterior	= null;

	public UsoTarjeta() {}

	public String getRanura() {
		return this.ranura;
	}

	public void setRanura(String ranura) {
		this.ranura = ranura;
	}

	public String getIdConductor() {
		return this.idConductor;
	}

	public void setIdConductor(String idConductor) {
		this.idConductor = idConductor != null ? idConductor.trim() : Conductor.UNKNOWN_DRIVER;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return this.apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getMatricula() {
		return this.matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = this.getCleanVehicleNumber(matricula);
	}

	public String getNumTrjConductor() {
		return this.numTrjConductor;
	}

	public void setNumTrjConductor(String numTrjConductor) {
		this.numTrjConductor = numTrjConductor;
	}

	@Override
	public int compareTo(UsoTarjeta o) {
		return this.fechaHoraInsercion.compareTo(o.fechaHoraInsercion);
	}

	public Date getFechaHoraInsercion() {
		return this.fechaHoraInsercion;
	}

	public void setFechaHoraInsercion(Date fechaHoraInsercion) {
		this.fechaHoraInsercion = fechaHoraInsercion;
	}

	public Date getFechaHoraExtraccion() {
		return this.fechaHoraExtraccion;
	}

	public void setFechaHoraExtraccion(Date fechaHoraExtraccion) {
		this.fechaHoraExtraccion = fechaHoraExtraccion;
	}

	public Integer getKmInsercion() {
		return this.KmInsercion;
	}

	public void setKmInsercion(Integer kmInsercion) {
		this.KmInsercion = kmInsercion;
	}

	public Integer getKmExtraccion() {
		return this.KmExtraccion;
	}

	public void setKmExtraccion(Integer kmExtraccion) {
		this.KmExtraccion = kmExtraccion;
	}

	public String getMatriculaAnterior() {
		return this.matriculaAnterior;
	}

	public void setMatriculaAnterior(String matriculaAnterior) {
		this.matriculaAnterior = this.getCleanVehicleNumber(matriculaAnterior);
	}

	public Date getFechaHoraExtAnterior() {
		return this.fechaHoraExtAnterior;
	}

	public void setFechaHoraExtAnterior(Date fechaHoraExtAnterior) {
		this.fechaHoraExtAnterior = fechaHoraExtAnterior;
	}
}
