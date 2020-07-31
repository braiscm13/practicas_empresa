select (case when LENGTH(q.question_text) > 15 then SUBSTR(q.question_text,0,15)||'...' else q.question_text end) as XAXIS
, YAXIS
, SERIE
from
(SELECT o.id_question
	,(CASE o.option_correct
			WHEN 'S'
				THEN sum(decode(o.option_correct, 'S', 1, 0))
			ELSE sum(decode(o.option_correct, 'N', 1, 0))
			END
		) AS YAXIS
	,decode(o.option_correct, 'S', 'sur.CORRECT', 'sur.WRONG') AS SERIE
FROM sur_survey_responses sr
	,sur_responses r
	,sur_options o
WHERE r.id_option = o.id_option(+)
	  and r.id_response_survey = sr.id_response_survey (+)
	  #WHERE_CONCAT#
group by o.id_question
, o.option_correct)data_questions
, sur_questions q
where data_questions.id_question = q.id_question (+)