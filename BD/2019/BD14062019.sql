
create or replace view CDVINFORME_LENGTH_FILES AS
select 
cdvempre_req_reales.CG_CONTRATO,
cdvempre_req_reales.cif,
cdvempre_req_reales.nomb as empresa,
count (cdficheros.idfichero) ficheros , 
SUM(length(cdficheros.fichero))/1024/1024 MB
from cdficheros, cdficheros_contrato, cdvempre_req_reales
where cdficheros.idfichero = cdficheros_contrato.idfichero and
cdvempre_req_reales.cg_contrato = cdficheros_contrato.cg_contrato 
group by cdvempre_req_reales.cg_contrato, cdvempre_req_reales.cif, cdvempre_req_reales.nomb;