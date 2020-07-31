select 
  numreq,
  idconductor,
  CDVEMPRE_REQ_REALES.cif
from 
  CDVEMPRE_REQ_REALES,
  cdconductor_cont
where 
  CDVEMPRE_REQ_REALES.numreq = cdconductor_cont.cg_contrato
  and CDVEMPRE_REQ_REALES.f_baja is null 
  and CDVEMPRE_REQ_REALES.fecfind > ?
order by
  numreq