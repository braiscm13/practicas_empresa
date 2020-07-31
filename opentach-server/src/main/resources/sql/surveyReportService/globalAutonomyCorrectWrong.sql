SELECT
    s_correct_wrong.auto as XAXIS, NVL(YAXIS, 0) as YAXIS, s_correct_wrong.SERIE
FROM 
    (SELECT
        nvl(pro.auto,'NO_AUTONOMY') as auto, SERIE
      FROM 
        (select pro.auto from provincias pro group by pro.auto)pro,
        (SELECT 'sur.CORRECT' AS SERIE FROM DUAL
         UNION
         SELECT 'sur.WRONG' AS SERIE FROM DUAL
        )v
    ) s_correct_wrong,
    (
     select 
      nvl(pro.auto,'NO_AUTONOMY') as XAXIS, 
      (CASE o.option_correct WHEN 'S' THEN sum(decode(o.option_correct, 'S', 1,0)) ELSE sum(decode(o.option_correct, 'N', 1,0)) END) as YAXIS,
      decode(o.option_correct, 'S', 'sur.CORRECT','sur.WRONG') AS SERIE
      from 
        (select cif, idconductor from cdconductores_emp) con,
        (select cif, idpersonal from cdpersonal_emp) pers,
        dfemp emp,
        provincias pro,
        sur_survey_responses sr,
        sur_surveys_responses_emp sre,
        sur_surveys_responses_pers srp,
        sur_responses r,
        sur_options o
      where  sr.id_response_survey = sre.id_response_survey (+)
      and sr.id_response_survey = srp.id_response_survey (+)
      and sre.idconductor = con.idconductor(+) 
      and srp.idpersonal = pers.idpersonal(+)
      and con.cif = emp.cif(+)
      and emp.cg_prov = pro.cg_prov(+)
      and sr.id_response_survey = r.id_response_survey (+)
      and r.id_option = o.id_option(+)
      #WHERE_CONCAT#
      group by pro.auto, o.option_correct
    )p
WHERE
 s_correct_wrong.auto = P.XAXIS(+)
 AND s_correct_wrong.SERIE = p.SERIE(+)