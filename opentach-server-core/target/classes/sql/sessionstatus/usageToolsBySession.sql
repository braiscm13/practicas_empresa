select
	manager as TITLE,
	count(1) as NUM_TIMES
from
	cdlogsesionstat
where
	action is null or action ='menu' or action = 'none'
	#WHERE_CONCAT#
group by
	manager