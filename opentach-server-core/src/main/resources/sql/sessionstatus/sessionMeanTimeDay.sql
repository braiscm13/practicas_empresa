select 
  to_char(F_INI,'YYYY-MM-DD') as MEASURETIME,
  avg(round((cdlogsesion.f_fin-cdlogsesion.f_ini)*24*60,2)) as MINUTES
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