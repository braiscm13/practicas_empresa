select  sr.id_survey,
        sr.survey_name,
        dni.idconductor,
        dni.correct_questions,
        dni.total_questions
from
        (
          --Correct questions and total questions by survey and dni
          select  dni_responses.id_survey,
                  dni_responses.idconductor,
                  dni_responses.correct_questions,
                  question_options.total_questions
          from 
                  --Correct answers for each dni and idSurvey
                  (select sr.id_survey,
                          sre.idconductor,
                          sum (decode (o.option_correct, 'S' ,1,0)) as CORRECT_QUESTIONS
                  from    sur_survey_responses sr,
                          sur_surveys_responses_emp sre,
                          sur_responses r,
                          sur_options o
                  where sr.id_response_survey = r.id_response_survey
                        and sr.id_response_survey = sre.id_response_survey
                        and sre.idconductor = ?
                        and r.id_option = o.id_option
                  group by id_survey, sre.idconductor)dni_responses,
                  --Total questions for idSurvey
                  (select   count(*) as total_questions, 
                            id_survey 
                  from      sur_questions_survey 
                  group by  id_survey)question_options
          where   dni_responses.id_survey = question_options.id_survey(+))dni,
          sur_surveys sr
where     sr.id_survey = dni.id_survey (+)
          --Only questions where current date is less than SURVEY_EXPIRATION_DATE
          and (sr.survey_expiration_date is null or sysdate <= sr.survey_expiration_date)
order by sr.id_survey