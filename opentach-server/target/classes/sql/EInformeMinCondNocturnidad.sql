select
  dfemp.nomb,
  dfemp.cif,
  cdvempre_req_reales.numreq as CG_CONTRATO,
  trim(cdconductor_cont.nombre) as nombre,
  trim(cdconductor_cont.apellidos) as apellidos,
  cdconductor_cont.dni,
  cdconductor_cont.idconductor,
  cdconductores_emp.external_employee_id,
  sum(FUN_COMPUTETIME(cdactividades.fec_comienzo,cdactividades.fec_fin,?,?)) as minutos
from
  dfemp,
  cdvempre_req_reales,
  cdconductor_cont,
  cdactividades
where
  dfemp.cif = cdvempre_req_reales.cif
  and cdvempre_req_reales.numreq = cdconductor_cont.cg_contrato
  and cdactividades.numreq = cdconductor_cont.cg_contrato
  and cdactividades.idconductor = cdconductor_cont.idconductor
  and cdconductor_cont.cg_contrato = ? AND dfemp.cif = ?
  and tpactividad = 4 and fec_comienzo >= ? and fec_fin <= ?
group by 
  dfemp.nomb,
  dfemp.cif,
  cdvempre_req_reales.numreq,
  trim(cdconductor_cont.nombre),
  trim(cdconductor_cont.apellidos),
  cdconductor_cont.dni,
  cdconductor_cont.idconductor,
  cdconductores_emp.external_employee_id
  