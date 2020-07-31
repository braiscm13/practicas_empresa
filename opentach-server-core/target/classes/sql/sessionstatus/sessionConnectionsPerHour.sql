select
	hours.MEASURETIME,
	NVL(data.NUMCONN,0) AS NUMCONN
from
	(
		select 
		  to_char(F_INI,'HH24') as MEASURETIME,
		  count( distinct cdlogsesion.USUARIO) as NUMCONN
		from
		  cdlogsesion,
		  cdusu usu,
		  cdusu_dfemp
		where
		  cdlogsesion.usuario = usu.usuario
		  and usu.usuario  = cdusu_dfemp.usuario
		  #WHERE_CONCAT#
		group by  
		  to_char(F_INI,'HH24')
	) data,
	(
		select '00' as MEASURETIME from dual
		union all
		select '01' as MEASURETIME from dual
		union all
		select '02' as MEASURETIME from dual
		union all
		select '03' as MEASURETIME from dual
		union all
		select '04' as MEASURETIME from dual
		union all
		select '05' as MEASURETIME from dual
		union all
		select '06' as MEASURETIME from dual
		union all
		select '07' as MEASURETIME from dual
		union all
		select '08' as MEASURETIME from dual
		union all
		select '09' as MEASURETIME from dual
		union all
		select '10' as MEASURETIME from dual
		union all
		select '11' as MEASURETIME from dual
		union all
		select '12' as MEASURETIME from dual
		union all
		select '13' as MEASURETIME from dual
		union all
		select '14' as MEASURETIME from dual
		union all
		select '15' as MEASURETIME from dual
		union all
		select '16' as MEASURETIME from dual
		union all
		select '17' as MEASURETIME from dual
		union all
		select '18' as MEASURETIME from dual
		union all
		select '19' as MEASURETIME from dual
		union all
		select '20' as MEASURETIME from dual
		union all
		select '21' as MEASURETIME from dual
		union all
		select '22' as MEASURETIME from dual
		union all
		select '23' as MEASURETIME from dual
	) hours
where
	hours.MEASURETIME = data.MEASURETIME (+)
ORDER BY
	MEASURETIME
