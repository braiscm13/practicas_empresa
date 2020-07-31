select 
	dfemp.cif, 
	dfemp.nomb, 
	CDVEMPRE_REQ_REALES.U_NUM_MAXIMO,
	nvl(ficheros.numFicheros,0) as numFicheros,  
	nvl(conductores.cond,0) as cond, 
	nvl(vehiculos.veh,0) as veh, 
	nvl(conductores_baja.cond_baja,0) as cond_baja, 
	nvl(vehiculos_baja.veh_baja,0) as veh_baja, 
	nvl(conductores_alta.cond_alta,0) as cond_alta, 
	nvl(vehiculos_alta.veh_alta,0) as veh_alta 
from 
	dfemp , 
	(select CG_CONTRATO,count(*) as numFicheros from cdficheros_contrato where f_alta >= ? and f_alta <= ? group by cg_contrato) ficheros, 
	(select CG_CONTRATO, count(*) as cond from cdconductor_cont group by CG_CONTRATO ) conductores, 
	(select CG_CONTRATO, count(*) as veh from cdvehiculo_cont group by CG_CONTRATO ) vehiculos, 
	(select CG_CONTRATO, count(*) as cond_alta from cdconductor_cont where f_baja is null and f_alta >= ? and f_alta <= ? group by CG_CONTRATO ) conductores_alta, 
	(select CG_CONTRATO, count(*) as veh_alta from cdvehiculo_cont where f_baja is null and f_alta >= ? and f_alta <= ?   group by CG_CONTRATO ) vehiculos_alta, 
	(select CG_CONTRATO, count(*) as cond_baja from cdconductor_cont where f_baja is not null and f_baja >= ? and f_baja <= ?  group by CG_CONTRATO ) conductores_baja, 
	(select CG_CONTRATO, count(*) as veh_baja from cdvehiculo_cont where f_baja is not null and f_baja >= ? and f_baja <= ?  group by CG_CONTRATO ) vehiculos_baja, 
	CDVEMPRE_REQ_REALES 
where  
	CDVEMPRE_REQ_REALES.cif = dfemp.cif and 
	CDVEMPRE_REQ_REALES.numreq = ficheros.cg_contrato(+) and  
	CDVEMPRE_REQ_REALES.numreq = conductores.CG_CONTRATO(+) and  
	CDVEMPRE_REQ_REALES.numreq = vehiculos.CG_CONTRATO(+) and 
	CDVEMPRE_REQ_REALES.numreq = conductores_baja.CG_CONTRATO(+) and  
	CDVEMPRE_REQ_REALES.numreq = vehiculos_baja.CG_CONTRATO(+) and 
	CDVEMPRE_REQ_REALES.numreq = conductores_alta.CG_CONTRATO(+) and  
	CDVEMPRE_REQ_REALES.numreq = vehiculos_alta.CG_CONTRATO(+) 
ORDER BY 
	NOMB