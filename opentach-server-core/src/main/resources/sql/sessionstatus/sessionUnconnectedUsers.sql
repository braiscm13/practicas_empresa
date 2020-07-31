select 
  usu.USUARIO,
  dfemp.nomb as EMPRESA
from
  cdusu usu,
  (select cdlogsesion.USUARIO from cdlogsesion where #TIME_FILTER#)  loguser,
  cdusu_dfemp,
  dfemp
where
  usu.usuario = loguser.usuario (+)
  and usu.usuario  = cdusu_dfemp.usuario (+)
  and cdusu_dfemp.cif  =dfemp.cif
  and loguser.usuario is null
  #WHERE_CONCAT#
order by
	dfemp.nomb,
	usu.USUARIO