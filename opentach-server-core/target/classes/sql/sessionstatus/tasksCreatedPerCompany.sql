select 
  count(1) as NUMTSK_OPEN,
  dfemp.nomb as EMPRESA
from 
  tsk_task,
  dfemp
where
  tsk_task.cif = dfemp.cif (+)
  #WHERE_CONCAT#
group by 
  dfemp.nomb