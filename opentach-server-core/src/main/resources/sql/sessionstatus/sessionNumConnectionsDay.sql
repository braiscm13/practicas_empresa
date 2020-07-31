select 
  to_char(F_INI,'YYYY-MM-DD') as MEASURETIME,
  count(distinct cdlogsesion.USUARIO) as NUMCONN
from
  cdlogsesion,
  cdusu usu,
  cdusu_dfemp
where
  cdlogsesion.usuario = usu.usuario
  and usu.usuario  = cdusu_dfemp.usuario
  #WHERE_CONCAT#
group by  
  to_char(F_INI,'YYYY-MM-DD')
order by
  to_char(F_INI,'YYYY-MM-DD')