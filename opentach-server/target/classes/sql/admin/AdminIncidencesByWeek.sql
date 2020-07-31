select
  to_char(TSK_CREATION_DATE - 7/24,'IYYY-IW') as WEEK,
  usuario_creator,
  sum(case when tkc_id = 1 then 1 else 0 end) as ACTIVIDAD,
  sum(case when tkc_id = 2 then 1 else 0 end) as INCIDENCIA
from
  tsk_task
#WHERE#
group by  
  usuario_creator,
  to_char(TSK_CREATION_DATE - 7/24,'IYYY-IW')
order by
  usuario_creator,
  to_char(TSK_CREATION_DATE - 7/24,'IYYY-IW') desc