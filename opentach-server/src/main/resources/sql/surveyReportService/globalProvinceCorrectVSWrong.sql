SELECT 'sur.CORRECT' AS title
	,count(r.id_option) AS value
FROM sur_responses r
	,sur_options o
	,sur_survey_responses sr
	,sur_surveys_responses_emp sre
    ,sur_surveys_responses_pers srp
	,(
		SELECT cif
			,idconductor
		FROM cdconductores_emp
		) con
	,(
        SELECT cif
          ,idpersonal
        FROM cdpersonal_emp
        ) pers
WHERE r.id_option = o.id_option(+)
	AND r.id_response_survey = sr.id_response_survey(+)
	AND sr.id_response_survey = srp.id_response_survey (+)
    AND sr.id_response_survey = sre.id_response_survey (+)
	AND o.OPTION_CORRECT = 'S'
	AND sre.idconductor = con.idconductor(+) 
    AND srp.idpersonal = pers.idpersonal(+)
  	#OTHER_FILTER#
union all 
SELECT 'sur.WRONG' AS title
	,count(r.id_option) AS value
FROM sur_responses r
	,sur_options o
	,sur_survey_responses sr
	,sur_surveys_responses_emp sre
    ,sur_surveys_responses_pers srp
	,(
		SELECT cif
			,idconductor
		FROM cdconductores_emp
		) con
	,(
        SELECT cif
          ,idpersonal
        FROM cdpersonal_emp
        ) pers
WHERE r.id_option = o.id_option(+)
	AND r.id_response_survey = sr.id_response_survey(+)
	AND sr.id_response_survey = srp.id_response_survey (+)
    AND sr.id_response_survey = sre.id_response_survey (+)
	AND o.OPTION_CORRECT = 'N'
  	AND sre.idconductor = con.idconductor(+) 
    AND srp.idpersonal = pers.idpersonal(+)
	#WHERE_CONCAT#