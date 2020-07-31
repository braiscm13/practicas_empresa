package com.opentach.common.tacho.data;

import java.util.Date;

public class CalibradoDataVU extends AbstractData {

	public String	matricula;
	public String	establecimiento;
	public String	dir_establecimiento;
	public String	num_tarjeta;
	public Date		f_expedicion_tarj;
	public String	identificacion_veh;
	public Integer	nacionalidad_veh;
	public Integer	coeficiente_veh;
	public Integer	constante_control;
	public Integer	circuns_neumaticos;
	public String	dir_neumaticos;
	public Integer	velocidad_auto;
	public Integer	old_odometro;
	public Integer	new_odometro;
	public Date		f_old_value;
	public Date		f_new_value;
	public Date		f_next_calibrado;

	public CalibradoDataVU() {}

}
