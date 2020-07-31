select 
  to_char(cdlogsesion.F_INI, 'YYYY-MM') as MEASURETIME,
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
  to_char(F_INI, 'YYYY-MM')
order by
  to_char(F_INI, 'YYYY-MM')