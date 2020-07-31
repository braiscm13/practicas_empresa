select
	*
from 
(
	select
		coalesce(measuretime_open,measuretime_close) as XAXIS,
		NUMTSK_OPEN,
		NUMTSK_CLOSE,
		(
			select 
				count(1) 
			from 
				tsk_task 
			where 
				TSK_CREATION_DATE < to_date(coalesce(measuretime_open,measuretime_close),'YYYY-MM-DD') +1
				and (tsk_closed_date is null or tsk_closed_date > to_date(coalesce(measuretime_open,measuretime_close),'YYYY-MM-DD')+1 ) 
		) as NUMTSK_REMAIN
	from 
		(
			select 
  				to_char(TSK_CREATION_DATE,'YYYY-MM-DD') as MEASURETIME_OPEN,
  				count(1) as NUMTSK_OPEN
			from
  				tsk_task
  			WHERE
				#OTHER_WHERE#
			group by  
  				to_char(TSK_CREATION_DATE,'YYYY-MM-DD')
		) abiertas
		full outer join
		(
			select 
  				to_char(TSK_CLOSED_DATE,'YYYY-MM-DD') as MEASURETIME_CLOSE,
  				count(1) as NUMTSK_CLOSE
			from
  				tsk_task
			WHERE
  				TSK_CLOSED_DATE IS NOT NULL
  				#WHERE_CONCAT#
			group by  
  				to_char(TSK_CLOSED_DATE,'YYYY-MM-DD')
		) cerradas
		on cerradas.measuretime_close  = abiertas.measuretime_open
) unpivot (
	YAXIS
	for SERIE in ("NUMTSK_OPEN","NUMTSK_CLOSE","NUMTSK_REMAIN")
)
order by
  xaxis