select * from
(select
  dfemp.nomb as empresa,
  cdficheros.tipo
from
  CDFICHEROS,
  cdficheros_contrato,
  CDVEMPRE_REQ_REALES,
  dfemp
where
  cdficheros.idfichero = cdficheros_contrato.idfichero
  and cdficheros_contrato.cg_contrato = CDVEMPRE_REQ_REALES.numreq (+)
  and CDVEMPRE_REQ_REALES.cif = dfemp.cif (+)
  #WHERE_CONCAT#
)pivot (
  count(tipo) 
for 
  tipo IN ('VU','TC')
)