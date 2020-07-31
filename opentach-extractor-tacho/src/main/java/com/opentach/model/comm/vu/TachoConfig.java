package com.opentach.model.comm.vu;

public class TachoConfig {
	public static final String	TACHO_AUTODOWNLOAD		= "tachograph.autodownload";
	public static final String	TACHO_NOTIFYONFINISH	= "tachograph.notifyonfinish";
	public static final String	TACHO_PORT				= "tachograph.port";
	// Periodo de descarga de adtos de la vu: ultima semana, ultimos 15 dias,
	// ultimo mes, rango de fechas.
	public static final String	TACHO_DOWLOAD_PERIOD	= "tachograph.period";
	// Preferencia del usuario en la qeu se guarda el rango de fechas a
	// descargar ( dia de inicio-dia fin).
	public static final String	TACHO_DOWNLOAD_DAYFROM	= "tachograph.dayfrom";
	public static final String	TACHO_DOWNLOAD_DAYTO	= "tachograph.dayto";

	public static final int		PERIOD_LAST_WEEK		= 3;
	public static final int		PERIOD_LAST_TWO_WEEKS	= 2;
	public static final int		PERIOD_LAST_MONTH		= 1;
	public static final int		PERIOD_DATE_RANGE		= 0;

}
