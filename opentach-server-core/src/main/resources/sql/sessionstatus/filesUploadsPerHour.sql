select
	hours.XAXIS,
	NVL(data.YAXIS,0) AS YAXIS,
	data.SERIE
from
	(
		select
			to_char(CDFICHEROS.F_ALTA,'HH24') as XAXIS,
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
			to_char(CDFICHEROS.F_ALTA,'HH24')
	) data,
	(
		select '00' as XAXIS from dual
		union all
		select '01' as XAXIS from dual
		union all
		select '02' as XAXIS from dual
		union all
		select '03' as XAXIS from dual
		union all
		select '04' as XAXIS from dual
		union all
		select '05' as XAXIS from dual
		union all
		select '06' as XAXIS from dual
		union all
		select '07' as XAXIS from dual
		union all
		select '08' as XAXIS from dual
		union all
		select '09' as XAXIS from dual
		union all
		select '10' as XAXIS from dual
		union all
		select '11' as XAXIS from dual
		union all
		select '12' as XAXIS from dual
		union all
		select '13' as XAXIS from dual
		union all
		select '14' as XAXIS from dual
		union all
		select '15' as XAXIS from dual
		union all
		select '16' as XAXIS from dual
		union all
		select '17' as XAXIS from dual
		union all
		select '18' as XAXIS from dual
		union all
		select '19' as XAXIS from dual
		union all
		select '20' as XAXIS from dual
		union all
		select '21' as XAXIS from dual
		union all
		select '22' as XAXIS from dual
		union all
		select '23' as XAXIS from dual
	) hours
where
	hours.XAXIS = data.XAXIS (+)
ORDER BY
	XAXIS
