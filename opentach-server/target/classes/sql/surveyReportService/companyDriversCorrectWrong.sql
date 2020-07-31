SELECT s.id_survey
	,s.survey_name
    ,sr.survey_response_date AS sur_date
    ,NVL2(sre.idconductor, sre.idconductor, pers.dni) AS idconductor
    ,srp.idpersonal
	,sum(o.option_correct) AS correct
	,sum(o.option_wrong) AS wrong
    ,NVL2(con.cif, con.cif, pers.cif) AS cif
    ,NVL2(con.nombre, con.nombre, pers.nombre) AS name
    ,NVL2(con.dni, con.dni, pers.dni) AS dni
FROM sur_survey_responses sr
	,sur_surveys_responses_emp sre
	,sur_surveys_responses_pers srp
	,sur_surveys s
	,sur_responses r
	,(
		SELECT cif
			,idconductor
      ,dni
      ,(nombre || ' ' ||apellidos) AS nombre
		FROM cdconductores_emp
		) con
	,(
		SELECT cif
			,idpersonal
      ,dni
      ,(nombre || ' ' ||apellidos) AS nombre
		FROM cdpersonal_emp
		) pers
	,(
		SELECT o.id_option
			,decode(o.option_correct, 'S', 1, 0) AS option_correct
			,decode(o.option_correct, 'N', 1, 0) AS option_wrong
		FROM sur_options o
		) o
WHERE sr.id_response_survey = r.id_response_survey(+)
	AND sr.id_response_survey = sre.id_response_survey(+)
	AND sr.id_response_survey = srp.id_response_survey(+)
	AND sr.id_survey = s.id_survey(+)
	AND r.id_option = o.id_option(+)
	AND sre.idconductor = con.idconductor(+) 
	AND srp.idpersonal = pers.idpersonal(+)
	#WHERE_CONCAT#
GROUP BY s.id_survey
	,s.survey_name
	,sre.idconductor
	,srp.idpersonal
    ,con.nombre
    ,con.cif
    ,pers.nombre
    ,pers.cif
    ,con.dni
    ,pers.dni
    ,sr.survey_response_date
ORDER BY sr.survey_response_date