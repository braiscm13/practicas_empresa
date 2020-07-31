SELECT CIF
  ,sum(correct) as correct
  ,sum(wrong) as wrong
from (
SELECT con.CIF AS cif
	,sum(o.option_correct) AS correct
	,sum(o.option_wrong) AS wrong
FROM sur_survey_responses sr
 	,sur_surveys_responses_emp sre
	,sur_surveys s
	,sur_responses r
	,(
		SELECT cif
			,idconductor
		FROM cdconductores_emp
		) con
    ,(
		SELECT o.id_option
			,decode(o.option_correct, 'S', 1, 0) AS option_correct
			,decode(o.option_correct, 'N', 1, 0) AS option_wrong
		FROM sur_options o
		) o
WHERE sr.id_response_survey = r.id_response_survey(+)
	AND sr.id_response_survey = sre.id_response_survey (+)
	AND sr.id_survey = s.id_survey(+)
	AND r.id_option = o.id_option(+)
	AND sre.idconductor = con.idconductor(+) 
GROUP BY con.CIF
union all
SELECT pers.CIF AS cif
	,sum(o.option_correct) AS correct
	,sum(o.option_wrong) AS wrong
FROM sur_survey_responses sr
 	,sur_surveys_responses_pers srp
	,sur_surveys s
	,sur_responses r
	,(
		SELECT cif
			,idpersonal
		FROM cdpersonal_emp
		) pers
    ,(
		SELECT o.id_option
			,decode(o.option_correct, 'S', 1, 0) AS option_correct
			,decode(o.option_correct, 'N', 1, 0) AS option_wrong
		FROM sur_options o
		) o
WHERE sr.id_response_survey = r.id_response_survey(+)
	AND sr.id_response_survey = srp.id_response_survey (+)
	AND sr.id_survey = s.id_survey(+)
	AND r.id_option = o.id_option(+)
  AND srp.idpersonal = pers.idpersonal(+)
GROUP BY pers.CIF)
#WHERE#
group by CIF