select
	days.MEASURETIME,
	NVL(data.NUMCONN,0) AS NUMCONN
from
	(
		select 
		  to_char (f_ini, 'FmDay', 'nls_date_language=spanish') as MEASURETIME,
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
		  to_char (f_ini, 'FmDay', 'nls_date_language=spanish')
	) data,
	(
		select 'Lunes' as MEASURETIME, 0 as sorting from dual
		union all
		select 'Martes' as MEASURETIME, 1 as sorting  from dual
		union all
		select 'Miércoles' as MEASURETIME, 2 as sorting  from dual
		union all
		select 'Jueves' as MEASURETIME, 3 as sorting  from dual
		union all
		select 'Viernes' as MEASURETIME, 4 as sorting  from dual
		union all
		select 'Sábado' as MEASURETIME, 5 as sorting  from dual
		union all
		select 'Domingo' as MEASURETIME, 6 as sorting  from dual
	) days
where
	days.MEASURETIME = data.MEASURETIME (+)
ORDER BY
	sorting
