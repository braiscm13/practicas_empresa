select
	NULL AS HORA,
	NULL AS NUM_TRJ_CONDU,
	cduso_TARJETA_temp.matricula, 
	cduso_TARJETA_temp.idconductor,
	to_char(fec_ins,'DD/MM/YYYY') as fecha,
	sum(NVL(km,0)) as KM_REC, 
	RANURA, 
	sum(NVL(((fec_ext - fec_ins)*24*60),0)) AS MINUTOS,
	cduso_TARJETA_temp.NUMREQ, 
	cduso_TARJETA_temp.NUMREQ as CG_CONTRATO,
	(CASE WHEN (RANURA = 1) THEN NVL(sum(km),0) ELSE 0 END ) AS KM_REC_1 ,
	(CASE WHEN (RANURA = 0) THEN NVL(sum(km),0) ELSE 0 END ) AS KM_REC_0,
	TO_CHAR(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ),'YYYY') || ' - ' || TO_CHAR (to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ),'month') AS MES_ANYO,
	to_char(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ),    'Day') AS dia_sem,
	to_char(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ),    'D') AS dia_sem_NUM,
	to_char(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ),    'DD') AS dia,
	to_char(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ),    'IW') AS  semana,
	'MES' || to_char(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ), 'MM') || '.' || to_char(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ), 'Month') AS  mes,
	to_char(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ), 'Month') AS mes2,
	to_char(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ),  'YYYY') AS anho,
	to_char(TRUNC(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ),    'iw'),    'DD/MM/YYYY') AS ini_semana,
	to_char(TRUNC(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ),    'iw') + 6,    'DD/MM/YYYY') AS fin_semana,
	cdvconductor_cont.dni,
	cdvconductor_cont.external_employee_id,
	cdvconductor_cont.nombre,
	cdvconductor_cont.APELLIDOS,
	cdvehiculo_cont.dscr
from 
	cduso_TARJETA_temp, 
	cdvconductor_cont, 
	cdvehiculo_cont 
WHERE 
	cduso_TARJETA_temp.IDCONDUCTOR = cdvconductor_cont.IDCONDUCTOR 
	AND cduso_TARJETA_temp.MATRICULA = cdvehiculo_cont.MATRICULA 
	AND  cduso_TARJETA_temp.NUMREQ =cdvconductor_cont.CG_CONTRATO
	AND cduso_TARJETA_temp.NUMREQ = cdvehiculo_cont.CG_CONTRATO(+)
group by
	cduso_TARJETA_temp.matricula, 
	cduso_TARJETA_temp.idconductor,
	to_char(fec_ins,'DD/MM/YYYY'), 
	RANURA, 
	cduso_TARJETA_temp.NUMREQ,
	RANURA, 
	cdvconductor_cont.dni, 
	cdvconductor_cont.external_employee_id,
	cdvconductor_cont.nombre, 
	cdvconductor_cont.APELLIDOS, 
	cdvehiculo_cont.dscr
ORDER BY 
	cduso_TARJETA_temp.NUMREQ, 
	cduso_TARJETA_temp.idconductor,
	to_char(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ),  'YYYY'),
	to_char(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ),    'IW'),
	to_char(to_date(to_char(fec_ins,'DD/MM/YYYY'),'DD/MM/YYYY' ), 'D')