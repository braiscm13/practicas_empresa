select
  manager as TITLE,
  count(1) as NUM_TIMES
from
  cdlogsesionstat,
  cdusu_dfemp
where
  cdlogsesionstat.usuario = cdusu_dfemp.usuario (+)
  and (action is null or action ='menu' or action = 'none')
  #WHERE_CONCAT#
group by
  manager