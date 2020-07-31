select
	days.MEASURETIME,
	NVL(data.NUMCONN,0) AS NUMCONN
from
	(
		select 
		  to_char (f_ini, 'FmDay', 'nls_date_language=italian') as MEASURETIME,
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
		  to_char (f_ini, 'FmDay', 'nls_date_language=italian')
	) data,
	(
		select 'Lunedì' as MEASURETIME, 0 as sorting from dual
		union all
		select 'Martedì' as MEASURETIME, 1 as sorting  from dual
		union all
		select 'Mercoledì' as MEASURETIME, 2 as sorting  from dual
		union all
		select 'Giovedì' as MEASURETIME, 3 as sorting  from dual
		union all
		select 'Venerdì' as MEASURETIME, 4 as sorting  from dual
		union all
		select 'Sabato' as MEASURETIME, 5 as sorting  from dual
		union all
		select 'Domenica' as MEASURETIME, 6 as sorting  from dual
	) days
where
	days.MEASURETIME = data.MEASURETIME (+)
ORDER BY
	sorting
