SELECT 
	MATRICULA,
	HORAS_EXCESO_VEL,
	HORAS_COND_SIN_TARJ,
	HORAS_SIN_SUM_ELEC,
	HORAS_TOTALES,
	(HORAS_TOTALES - (HORAS_EXCESO_VEL/3600) - HORAS_COND_SIN_TARJ - HORAS_SIN_SUM_ELEC) AS HORAS_OTRAS_INCI 
FROM
	(
		SELECT 
			I.MATRICULA, 
			SUM(DECODE (C.DSCR,'EXCESO_DE_VELOCIDAD',(I.FECFIN - I.FECINI)*24*60*60)) AS HORAS_EXCESO_VEL,
			SUM(DECODE (C.DSCR,'CONDUCCION_SIN_TARJETA_ADECUADA',(I.FECFIN - I.FECINI)*24)) AS HORAS_COND_SIN_TARJ,
			SUM(DECODE (C.DSCR,'INTERRUPCION_DEL_SUMINISTRO_ELECTRICO',(I.FECFIN - I.FECINI)*24)) AS HORAS_SIN_SUM_ELEC, 
			SUM((I.FECFIN - I.FECINI)*24) AS HORAS_TOTALES 
		FROM 
			CDINCIDENTES I , 
			CDTIPO_FALLO C, 
			CDVVEHICULO_CONT V 
		WHERE 
			I.TIPO = C.TPFALLO 
			AND I.NUMREQ = ? 
			AND I.FECINI >= ? 
			AND I.FECINI <= ? 
			AND I.FECFIN <= ? 
			AND I.FECFIN >= ? 
			AND V.MATRICULA = I.MATRICULA 
			AND I.NUMREQ = V.CG_CONTRATO 
			#SQLDELEG# 
		GROUP BY 
			I.MATRICULA 
	)