select 'sur.CORRECT' as title,
count(r.id_option) as value
from sur_responses r,
sur_options o,
sur_survey_responses sr
where r.id_option = o.id_option(+)
and r.id_response_survey = sr.id_response_survey(+)
and OPTION_CORRECT = 'S'
#OTHER_FILTER#
union all 
select 'sur.WRONG' as title,
count(r.id_option) as value
from sur_responses r,
sur_options o,
sur_survey_responses sr
where r.id_option = o.id_option(+)
and r.id_response_survey = sr.id_response_survey(+)
and OPTION_CORRECT = 'N'
#WHERE_CONCAT#