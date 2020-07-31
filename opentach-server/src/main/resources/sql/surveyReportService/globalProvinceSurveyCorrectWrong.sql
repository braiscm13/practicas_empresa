SELECT
    (case when LENGTH(s_correct_wrong.survey_name) > 10 then SUBSTR(s_correct_wrong.survey_name,0,10)||'...' else s_correct_wrong.survey_name end) as XAXIS, 
    NVL(YAXIS, 0) as YAXIS, 
    s_correct_wrong.SERIE
FROM 
    (SELECT
        s.survey_name, SERIE
      FROM 
        sur_surveys s,
        (SELECT 'sur.CORRECT' AS SERIE FROM DUAL
         UNION
         SELECT 'sur.WRONG' AS SERIE FROM DUAL
        )v
    ) s_correct_wrong,
    (
     select 
      s.survey_name as XAXIS, 
      (CASE o.option_correct WHEN 'S' THEN sum(decode(o.option_correct, 'S', 1,0)) ELSE sum(decode(o.option_correct, 'N', 1,0)) END) as YAXIS,
      decode(o.option_correct, 'S', 'sur.CORRECT','sur.WRONG') AS SERIE
      from 
        sur_surveys s,
        sur_survey_responses sr,
        sur_surveys_responses_emp sre,
    	sur_surveys_responses_pers srp,
        sur_responses r,
        sur_options o,
        (SELECT cif
			,idconductor
		FROM cdconductores_emp
		) con,
		(SELECT cif
          ,idpersonal
        FROM cdpersonal_emp
        ) pers
      where s.id_survey = sr.id_survey (+)
      and sr.id_response_survey = r.id_response_survey (+)
  	  and sr.id_response_survey = srp.id_response_survey (+)
      and sr.id_response_survey = sre.id_response_survey (+)
      and r.id_option = o.id_option(+)
      and sre.idconductor = con.idconductor(+) 
      and srp.idpersonal = pers.idpersonal(+)
      #WHERE_CONCAT#
      group by s.survey_name, o.option_correct
    )p
WHERE
 s_correct_wrong.survey_name = P.XAXIS(+)
 AND s_correct_wrong.SERIE = p.SERIE(+)