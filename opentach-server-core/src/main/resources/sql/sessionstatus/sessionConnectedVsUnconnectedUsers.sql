select
	'sta.UNCONNECTED' as title,
	count(distinct usu.USUARIO) as value  
from
	cdusu usu,
	(select cdlogsesion.USUARIO from cdlogsesion where #TIME_FILTER#)  loguser,
	cdusu_dfemp
where
	usu.usuario = loguser.usuario (+)
	and usu.usuario  = cdusu_dfemp.usuario (+)
	and loguser.usuario is null
	#OTHER_FILTER#
union all
select
	'sta.CONNECTED' as title,
	count(distinct cdlogsesion.USUARIO) as value
from
	cdlogsesion,
	cdusu usu,
	cdusu_dfemp
where
	cdlogsesion.usuario = usu.usuario
	and usu.usuario  = cdusu_dfemp.usuario
	#WHERE_CONCAT#
