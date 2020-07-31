select 
 'CONDUCTOR' as ORIGEN,
 a.idconductor AS IDORIGEN,
  a.matricula, 
	a.idconductor,
	to_date(a.fecha,'DD/MM/YYYY') AS FECHA,
	a.NUMREQ, 
	a.CG_CONTRATO,
	a.dni || ',' || a.APELLIDOS || ' '  || a.nombre  as NOMBRE,
	a.dscr,
  min(km_ins) as KM_INS,
  max(km_ext) AS KM_EXT,
  sum(km_ext - km_ins)  as KM_REC,
  SUM(MINUTOS) AS MINUTOS
from 
( select distinct
	cduso_TARJETA_temp.matricula, 
	cdconductor_cont.idconductor,
	to_char(fec_ins,'DD/MM/YYYY') as fecha,
  km_ext,
  km_ins,
  (FEC_EXT - FEC_INS)*24*60 AS MINUTOS,
	cduso_TARJETA_temp.NUMREQ, 
	cduso_TARJETA_temp.NUMREQ as CG_CONTRATO,
	cdconductor_cont.dni,
	cdconductor_cont.nombre,
	cdconductor_cont.APELLIDOS,
	cdvehiculo_cont.dscr
from 
	cduso_TARJETA_temp, 
	cdconductor_cont, 
	cdvehiculo_cont 
WHERE 
km_ins is not null and km_ext is not null and
	cduso_TARJETA_temp.IDCONDUCTOR = cdconductor_cont.IDCONDUCTOR 
	AND cduso_TARJETA_temp.MATRICULA = cdvehiculo_cont.MATRICULA 
	AND  cduso_TARJETA_temp.NUMREQ =cdconductor_cont.CG_CONTRATO
  	AND  cduso_TARJETA_temp.NUMREQ =cdvehiculo_cont.CG_CONTRATO) a
	where  
	a.cg_contrato = ? and to_date(fecha,'DD/MM/YYYY') >= ? and to_date(fecha,'DD/MM/YYYY') <= ? and (#CONDUCTORES#) 
group by
'CONDUCTOR',
	a.matricula, 
	a.idconductor,
	a.fecha,  
	a.NUMREQ,
	a.dni || ',' || a.APELLIDOS || ' '  || a.nombre,
	a.dscr
ORDER BY 
'CONDUCTOR',
	a.IDCONDUCTOR, 
	a.FECHA