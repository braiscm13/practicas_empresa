select
	to_char(CDFICHEROS.F_ALTA,'YYYY-MM') as XAXIS,
	count(cdficheros.tipo) as YAXIS,
	cdficheros.tipo as SERIE
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
group by
	cdficheros.tipo,
	to_char(CDFICHEROS.F_ALTA,'YYYY-MM')
ORDER BY
	to_char(CDFICHEROS.F_ALTA,'YYYY-MM')
