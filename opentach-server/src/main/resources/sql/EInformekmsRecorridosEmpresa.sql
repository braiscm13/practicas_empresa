select 
	a.idconductor,
	a.NUMREQ, 
	a.CG_CONTRATO,
	a.dni || ',' || a.APELLIDOS || ' '  || a.nombre  as NOMBRE,
	sum(km_ext - km_ins)  as KM_REC
from 
( select distinct
	cduso_TARJETA_temp.matricula, 
	cdconductor_cont.idconductor,
	to_char(fec_ins,'DD/MM/YYYY') as fecha,
	km_ext,
	km_ins,
	cduso_TARJETA_temp.NUMREQ, 
	cduso_TARJETA_temp.NUMREQ as CG_CONTRATO,
	cdconductor_cont.dni,
	cdconductor_cont.nombre,
	cdconductor_cont.APELLIDOS
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
	a.idconductor,  
	a.NUMREQ,
	a.dni || ',' || a.APELLIDOS || ' '  || a.nombre,
ORDER BY 
	a.IDCONDUCTOR