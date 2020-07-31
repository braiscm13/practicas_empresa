select 
	* 
from 
(
	select
		count(nvl2(nullif(cdficheros_registro.ismovil,'S'),null,1)) as movil,
		count(nvl2(cdcdo.usuario,1,null)) as cdo,
		count(1)-count(nvl2(nullif(cdficheros_registro.ismovil,'S'),null,1))- count(nvl2(cdcdo.usuario,1,null)) as desktop
	from
		cdficheros,
		cdficheros_registro,
		cdcdo,		
  		CDVEMPRE_REQ_REALES
	where
		cdficheros.idfichero = cdficheros_registro.idfichero		
		and cdficheros_registro.usuario_alta = cdcdo.usuario (+)
		and cdficheros_registro.cg_contrato = CDVEMPRE_REQ_REALES.cg_contrato (+)
		and cdficheros_registro.tipo = 'UP'
		#WHERE_CONCAT#
)
unpivot
(
	VALOR 
	for NOMBRE in ("MOVIL","CDO","DESKTOP")
)