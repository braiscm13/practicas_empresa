package com.opentach.common.tacho.data;

import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Permite recuperar el valor de velocidad media (total y en circulacion) , velocidad maxima y segundos que ha circulado el vehiculo en un minuto determinado. De los datos de
 * velocidad por segundo extraidos del tacografo se calculan dichos valores.<br> Se entiende por velocidad media en circulaci�n la velocidad media calculada cuando el vehiculo se
 * est� desplzando.
 *
 * @author Pablo Dorgambide
 * @company www.imatia.com
 * @email pdorgambide@imatia.com
 * @date: 15/02/2008
 */
public class Velocidad extends AbstractData implements Comparable<Velocidad> {
	public Date			fechaHora				= null;
	public Float		velocidadMed			= null;
	public Float		velocidadMedCirculacion	= null;
	public Integer		velocidadMax			= null;
	public Integer		segundosVelocidadMax	= null;
	public Integer		aceleracionMax			= null;
	public String		matricula				= null;
	public String		idConductor				= null;
	/**
	 * Array de bytes con el registro de velocidad cada segundo dentro del minuto.
	 */
	public ByteBuffer	velocidadRaw			= null;

	public Integer getVelocidadMaxima() {
		return this.velocidadMax;
	}

	public Float getVelocidadMedia() {
		return this.velocidadMed;
	}

	public Date getFechaHora() {
		return this.fechaHora;
	}

	public void setFechaHora(Date fechaHora) {
		this.fechaHora = fechaHora;
	}

	public String getIdConductor() {
		return this.idConductor;
	}

	public void setIdConductor(String idConductor) {
		this.idConductor = idConductor != null ? idConductor.trim() : Conductor.UNKNOWN_DRIVER;
	}

	public String getMatricula() {
		return this.matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = this.getCleanVehicleNumber(matricula);
	}

	/** Establece los valores de velociad a partir de los 60 registros del tacografo(1 registro por segundo). */
	public void setVelocidades(int[] regs, int vprev) {
		int max = 0, seconds = 0, avg = 0, avgc = 0;
		int ac = 0;
		byte[] speed = new byte[regs.length];

		for (int i = 0; (regs != null) && (i < regs.length); i++) {
			int v = regs[i];
			speed[i] = (byte) regs[i];
			avg += v;
			if (v != 0) {
				avgc += v;
			}
			if (max < v) {
				max = v;
			}
			if (v == max) {
				seconds++;
			}
			if (vprev != -1) {
				ac = Math.abs((v - vprev)) > Math.abs(ac) ? v - vprev : ac;
			}
			vprev = v;
		}

		this.aceleracionMax = new Integer(ac);
		this.velocidadMax = new Integer(max);
		this.velocidadMed = new Float((float) avg / regs.length);
		this.velocidadMedCirculacion = new Float((float) avgc / regs.length);
		this.segundosVelocidadMax = new Integer(seconds);
		this.velocidadRaw = ByteBuffer.wrap(speed);
	}

	public Integer getSegundosVelocidadMax() {
		return this.segundosVelocidadMax;
	}

	public Float getVelocidadMediaCirculacion() {
		return this.velocidadMedCirculacion;
	}

	@Override
	public int compareTo(Velocidad o) {
		return this.fechaHora.compareTo(o.fechaHora);
	}


}
