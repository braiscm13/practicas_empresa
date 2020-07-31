package com.opentach.server.checktrucker.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.surveys.ISurveyService;
import com.opentach.common.surveys.OptionTO;
import com.opentach.common.surveys.QuestionTO;
import com.opentach.common.surveys.QuestionaryTO;
import com.opentach.common.surveys.ResultsSurvey;
import com.opentach.common.surveys.SurveyTO;
import com.opentach.server.checktrucker.services.ConductorPersonal.Type;
import com.opentach.server.checktrucker.webservice.utils.OpentachNoDriverException;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcToEntityResultTemplate;
import com.utilmize.tools.exception.UException;

public class SurveyService extends UAbstractService implements ISurveyService {

	public SurveyService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	@Override
	public Number createSurvey(final String title, final Object expirationDate, final List<QuestionTO> questions, final int sessionId) throws Exception {
		return new OntimizeConnectionTemplate<Number>() {
			@Override
			protected Number doTask(Connection con) throws UException {
				try {
					SurveyService.this.checkInput(title, questions);

					TableEntity eSurvey = (TableEntity) SurveyService.this.getEntity("ESurvey");
					TableEntity eSurQuestionsSurvey = (TableEntity) SurveyService.this.getEntity("ESurQuestionsSurvey");
					TableEntity eSurQuestions = (TableEntity) SurveyService.this.getEntity("ESurQuestions");
					TableEntity eSurOptions = (TableEntity) SurveyService.this.getEntity("ESurOptions");

					Hashtable<Object, Object> keysvalues = EntityResultTools.keysvalues("SURVEY_NAME", title);
					if (expirationDate != null) {
						keysvalues.put("SURVEY_EXPIRATION_DATE", expirationDate);
					}
					EntityResult res = eSurvey.insert(keysvalues, SurveyService.this.getSessionId(sessionId, eSurvey), con);
					Number idSurvey = (Number) res.get("ID_SURVEY");
					for (QuestionTO question : questions) {
						String questionTitle = question.getTitle();
						Number questionType = question.getIdType();
						String correctOption = question.getCorrectOption();
						EntityResult resQuestions = eSurQuestions.insert(EntityResultTools.keysvalues("ID_QUESTION_TYPE", questionType, "QUESTION_TEXT", questionTitle),
								SurveyService.this.getSessionId(sessionId, eSurQuestions), con);
						Number idQuestion = (Number) resQuestions.get("ID_QUESTION");
						EntityResult resQuestionSurvey = eSurQuestionsSurvey.insert(EntityResultTools.keysvalues("ID_SURVEY", idSurvey, "ID_QUESTION", idQuestion),
								SurveyService.this.getSessionId(sessionId, eSurQuestionsSurvey), con);

						for (OptionTO option : question.getOptions()) {
							String oCorrect = "N";
							if (ObjectTools.safeIsEquals(correctOption, option.getOptionText())) {
								oCorrect = "S";
							}
							eSurOptions.insert(EntityResultTools.keysvalues("ID_QUESTION", idQuestion, "OPTION_TEXT", option.getOptionText(), "OPTION_CORRECT", oCorrect),
									SurveyService.this.getSessionId(sessionId, eSurOptions), con);
						}

					}
					return idSurvey;

				} catch (Exception ex) {
					throw new UException(ex);
				}
			}

		}.execute(this.getConnectionManager(), false);
	}

	@Override
	public QuestionaryTO getSurvey(final Number idSurvey) throws Exception {
		return new OntimizeConnectionTemplate<QuestionaryTO>() {
			@Override
			protected QuestionaryTO doTask(Connection con) throws UException {
				try {
					QuestionaryTO result = new QuestionaryTO();
					result.setId(idSurvey);

					TableEntity eSurQuestionsSurvey = (TableEntity) SurveyService.this.getEntity("ESurQuestionsSurvey");

					EntityResult resQuestions = eSurQuestionsSurvey.query(EntityResultTools.keysvalues("ID_SURVEY", idSurvey),
							EntityResultTools.attributes("ID_QUESTION", "ID_QUESTION_TYPE", "QUESTION_TEXT"), TableEntity.getEntityPrivilegedId(eSurQuestionsSurvey), con);
					List<QuestionTO> lQuestions = SurveyService.this.getQuestionsTO(idSurvey, resQuestions, con);
					result.addQuestions(lQuestions);

					return result;

				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), false);
	}

	@Override
	public List<QuestionTO> getQuestions(final List<Number> lQuestions) throws Exception {
		return new OntimizeConnectionTemplate<List<QuestionTO>>() {
			@Override
			protected List<QuestionTO> doTask(Connection con) throws UException {
				try {
					TableEntity eSurvey = (TableEntity) SurveyService.this.getEntity("ESurQuestions");
					SearchValue sv = new SearchValue(SearchValue.IN, lQuestions);
					EntityResult resQuestions = eSurvey.query(EntityResultTools.keysvalues("ID_QUESTION", sv),
							EntityResultTools.attributes("ID_QUESTION", "ID_QUESTION_TYPE", "QUESTION_TEXT"), TableEntity.getEntityPrivilegedId(eSurvey), con);
					return SurveyService.this.getQuestionsTO(null, resQuestions, con);

				} catch (Exception ex) {
					throw new UException(ex);
				}
			}

		}.execute(this.getConnectionManager(), false);

	}

	@Override
	public Number updateQuestions(final Number idSurvey, final String title, final Object expirationDate, final List<QuestionTO> questions, final int sessionId) throws Exception {

		return new OntimizeConnectionTemplate<Number>() {
			@Override
			protected Number doTask(Connection con) throws UException {
				try {
					SurveyService.this.checkInput(title, questions);
					TableEntity eSurvey = (TableEntity) SurveyService.this.getEntity("ESurvey");
					TableEntity eSurQuestionsSurvey = (TableEntity) SurveyService.this.getEntity("ESurQuestionsSurvey");
					TableEntity eSurQuestions = (TableEntity) SurveyService.this.getEntity("ESurQuestions");
					TableEntity eSurOptions = (TableEntity) SurveyService.this.getEntity("ESurOptions");

					this.deleteQuestionsAndOptions(idSurvey, questions, sessionId, con, eSurQuestionsSurvey);

					Hashtable<Object, Object> keysvalues = EntityResultTools.keysvalues("SURVEY_NAME", title);
					if (expirationDate != null) {
						keysvalues.put("SURVEY_EXPIRATION_DATE", expirationDate);
					}

					eSurvey.update(keysvalues, EntityResultTools.keysvalues("ID_SURVEY", idSurvey), sessionId, con);

					EntityResult questionsDB = eSurQuestionsSurvey.query(EntityResultTools.keysvalues("ID_SURVEY", idSurvey), EntityResultTools.attributes("ID_QUESTION"),
							sessionId, con);

					for (QuestionTO question : questions) {

						Number questionId = question.getId();
						if (questionId == null) {
							String questionTitle = question.getTitle();
							Number questionType = question.getIdType();
							String correctOption = question.getCorrectOption();
							EntityResult resQuestions = eSurQuestions.insert(EntityResultTools.keysvalues("ID_QUESTION_TYPE", questionType, "QUESTION_TEXT", questionTitle),
									sessionId, con);
							Number idQuestion = (Number) resQuestions.get("ID_QUESTION");
							EntityResult resQuestionSurvey = eSurQuestionsSurvey.insert(EntityResultTools.keysvalues("ID_SURVEY", idSurvey, "ID_QUESTION", idQuestion), sessionId,
									con);

							for (OptionTO option : question.getOptions()) {
								String oCorrect = "N";
								if (ObjectTools.safeIsEquals(correctOption, option.getOptionText())) {
									oCorrect = "S";
								}
								eSurOptions.insert(EntityResultTools.keysvalues("ID_QUESTION", idQuestion, "OPTION_TEXT", option.getOptionText(), "OPTION_CORRECT", oCorrect),
										sessionId, con);
							}
						} else {
							String questionTitle = question.getTitle();
							Number questionType = question.getIdType();
							String correctOption = question.getCorrectOption();
							eSurQuestions.update(EntityResultTools.keysvalues("QUESTION_TEXT", questionTitle, "ID_QUESTION_TYPE", questionType),
									EntityResultTools.keysvalues("ID_QUESTION", questionId), sessionId, con);

							this.insertToQuestionSurvey(questionsDB, eSurQuestionsSurvey, con, questionId);

							for (OptionTO option : question.getOptions()) {
								if (option.getIdOption() == null) {
									String oCorrect = "N";
									if (ObjectTools.safeIsEquals(correctOption, option.getOptionText())) {
										oCorrect = "S";
									}
									eSurOptions.insert(EntityResultTools.keysvalues("ID_QUESTION", questionId, "OPTION_TEXT", option.getOptionText(), "OPTION_CORRECT", oCorrect),
											sessionId, con);
								} else {
									String oCorrect = "N";
									if (ObjectTools.safeIsEquals(correctOption, option.getOptionText())) {
										oCorrect = "S";
									}
									eSurOptions.update(EntityResultTools.keysvalues("OPTION_TEXT", option.getOptionText(), "OPTION_CORRECT", oCorrect),
											EntityResultTools.keysvalues("ID_OPTION", option.getIdOption()), sessionId, con);
								}
							}

						}

					}

					return idSurvey;

				} catch (Exception ex) {
					throw new UException(ex);
				}
			}

			private void insertToQuestionSurvey(EntityResult questionsDB, TableEntity eSurQuestionsSurvey, Connection con, Number idQuestion) throws Exception {
				List<Number> l = (List<Number>) questionsDB.get("ID_QUESTION");
				if (!l.contains(idQuestion)) {
					eSurQuestionsSurvey.insert(EntityResultTools.keysvalues("ID_SURVEY", idSurvey, "ID_QUESTION", idQuestion), sessionId, con);
				}
			}

			private void deleteQuestionsAndOptions(final Number idSurvey, final List<QuestionTO> questions, final int sessionId, Connection con, TableEntity eSurQuestionsSurvey)
					throws Exception {
				// Get delete questions
				List<Number> questionsToDelete = new ArrayList<Number>();

				EntityResult questionsDB = eSurQuestionsSurvey.query(EntityResultTools.keysvalues("ID_SURVEY", idSurvey), EntityResultTools.attributes("ID_QUESTION"), sessionId,
						con);
				Vector<Number> vQuestionsDB = new Vector<Number>();
				if (!questionsDB.isEmpty()) {
					vQuestionsDB = (Vector<Number>) questionsDB.get("ID_QUESTION");
				}
				Vector<Number> vQuestions = this.getVectorIdQuestion(questions);
				for (Number idQuestionDB : vQuestionsDB) {
					if (!vQuestions.contains(idQuestionDB)) {
						questionsToDelete.add(idQuestionDB);
					}
				}

				for (Number question : questionsToDelete) {
					eSurQuestionsSurvey.delete(EntityResultTools.keysvalues("ID_QUESTION", question, "ID_SURVEY", idSurvey), sessionId, con);
				}
			}

			private Vector<Number> getVectorIdQuestion(List<QuestionTO> questions) {
				Vector<Number> vId = new Vector<Number>();
				for (QuestionTO question : questions) {
					vId.add(question.getId());
				}
				return vId;
			}

			private Vector<Number> getVectorIdOption(List<QuestionTO> questions) {
				Vector<Number> vIdOption = new Vector<Number>();
				for (QuestionTO question : questions) {
					for (OptionTO option : question.getOptions()) {
						vIdOption.add(option.getIdOption());
					}
				}
				return vIdOption;
			}

		}.execute(this.getConnectionManager(), false);
	}

	@Override
	public Number deleteSurvey(final Number idSurvey, final int sessionID) throws Exception {
		return null;
	}

	@Override
	public List<SurveyTO> getSurveys(final String dniIdConductor) throws Exception {
		return new OntimizeConnectionTemplate<List<SurveyTO>>() {
			@Override
			protected List<SurveyTO> doTask(Connection con) throws UException {
				try {
					if (dniIdConductor == null) {
						throw new Exception("E_NO_DNI");
					}

					ConductorPersonal condPers = SurveyService.this.checkDNIIdConductor(dniIdConductor, con);
					String sqlSentence = "sql/surveyService/SurveyServiceResponsesIdConductor.sql";

					// If idConductor not found, it searches in CDPERSONAL_EMP
					if (ConductorPersonal.Type.PERSONAL.equals(condPers.getType())) {
						sqlSentence = "sql/surveyService/SurveyServiceResponsesIdPersonal.sql";
					}

					List<SurveyTO> lResult = new ArrayList<SurveyTO>();
					String sql = new Template(sqlSentence).getTemplate();

					EntityResult resQuery = new QueryJdbcToEntityResultTemplate().execute(con, sql, condPers.getId());
					int nRows = resQuery.calculateRecordNumber();
					for (int i = 0; i < nRows; i++) {
						Number idSurvey = ((Vector<Number>) resQuery.get("ID_SURVEY")).get(i);
						String surveyName = ((Vector<String>) resQuery.get("SURVEY_NAME")).get(i);
						Number correctQuestions = ((Vector<Number>) resQuery.get("CORRECT_QUESTIONS")).get(i);
						Number totalQuestions = ((Vector<Number>) resQuery.get("TOTAL_QUESTIONS")).get(i);

						Integer correctQuestionInt = correctQuestions != null ? correctQuestions.intValue() : null;
						Integer totalQuestionsInt = totalQuestions != null ? totalQuestions.intValue() : null;

						lResult.add(new SurveyTO(idSurvey, surveyName, correctQuestionInt, totalQuestionsInt));
					}

					return lResult;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), false);
	}

	@Override
	public ResultsSurvey sendResponses(final Number idSurvey, final String dniIdConductor, final String uuid, final List<Number> options) throws Exception {
		return new OntimizeConnectionTemplate<ResultsSurvey>() {
			@Override
			protected ResultsSurvey doTask(Connection con) throws UException {

				try {
					ConductorPersonal condPers = SurveyService.this.checkDNIIdConductor(dniIdConductor, con);

					ResultsSurvey result = this.getResultSurvey(idSurvey, options, con);

					// Insert SUR_SURVEY_RESPONSES
					TableEntity eSurSurveyResponses = (TableEntity) SurveyService.this.getEntity("ESurSurveyResponses");
					EntityResult resSurSurveyResponses = eSurSurveyResponses.insert(
							EntityResultTools.keysvalues("ID_SURVEY", idSurvey, "SURVEY_RESPONSE_UUID", uuid, "SURVEY_RESPONSE_DATE", new Date()),
							TableEntity.getEntityPrivilegedId(eSurSurveyResponses), con);
					Number idResponseSurvey = (Number) resSurSurveyResponses.get("ID_RESPONSE_SURVEY");

					// Insert SUR_SURVEYS_RESPONSES_EMP
					String entityEmpresaOrPersonal = ConductorPersonal.Type.CONDUCTOR.equals(condPers.getType()) ? "ESurSurveyResponsesEmp" : "ESurSurveyResponsesPers";
					String keyId = ConductorPersonal.Type.CONDUCTOR.equals(condPers.getType()) ? "IDCONDUCTOR" : "IDPERSONAL";

					TableEntity eSurSurveyResponsesCondPers = (TableEntity) SurveyService.this.getEntity(entityEmpresaOrPersonal);
					eSurSurveyResponsesCondPers.insert(EntityResultTools.keysvalues("ID_RESPONSE_SURVEY", idResponseSurvey, keyId, condPers.getId()),
							TableEntity.getEntityPrivilegedId(eSurSurveyResponsesCondPers), con);

					// Insert SUR_RESPONSES
					TableEntity eSurResponses = (TableEntity) SurveyService.this.getEntity("ESurResponses");
					for (Number option : options) {
						eSurResponses.insert(EntityResultTools.keysvalues("ID_RESPONSE_SURVEY", idResponseSurvey, "ID_OPTION", option),
								TableEntity.getEntityPrivilegedId(eSurResponses), con);
					}

					return result;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}

			private ResultsSurvey getResultSurvey(Number idSurvey, List<Number> options, Connection con) throws Exception {
				List<Number> correctOptions = SurveyService.this.getCorrectOptions(idSurvey, con);
				if (options.size() != correctOptions.size()) {
					throw new Exception("E_N_CORRECT_OPTIONS");
				}
				Integer correct = 0;
				Integer wrong = 0;

				for (Number idOption : options) {

					if (correctOptions.contains(new BigDecimal(idOption.intValue()))) {
						correct++;
					} else {
						wrong++;
					}
				}
				return new ResultsSurvey(correct, wrong);
			}

		}.execute(this.getConnectionManager(), false);
	}

	private void checkInput(String title, List<QuestionTO> questions) throws Exception {
		if (title == null) {
			throw new Exception("M_NECCESARY_SURVEY_NAME");
		}
		if ((questions == null) || questions.isEmpty()) {
			throw new Exception("M_NECCESARY_QUESTIONS");
		}

	}

	protected List<QuestionTO> getQuestionsTO(Number idSurvey, final EntityResult resQuestions, Connection con) throws Exception {
		List<QuestionTO> lQuestionsTO = new ArrayList<QuestionTO>();
		int rowsQuestions = resQuestions.calculateRecordNumber();
		for (int i = 0; i < rowsQuestions; i++) {
			Hashtable<String, Object> dataQuestion = resQuestions.getRecordValues(i);
			Number idQuestion = (Number) dataQuestion.get("ID_QUESTION");

			TableEntity eSurOptions;
			Hashtable<Object, Object> kv;
			if (idSurvey == null) {
				eSurOptions = (TableEntity) this.getEntity("ESurOptions");
				kv = EntityResultTools.keysvalues("ID_QUESTION", idQuestion);
			} else {
				eSurOptions = (TableEntity) this.getEntity("ESurQuestionOptions");
				kv = EntityResultTools.keysvalues("ID_QUESTION", idQuestion, "ID_SURVEY", idSurvey);
			}
			EntityResult resOptions = eSurOptions.query(
					kv,
					EntityResultTools.attributes("ID_OPTION", "OPTION_TEXT", "OPTION_CORRECT"), TableEntity.getEntityPrivilegedId(eSurOptions));
			int rowsOptions = resOptions.calculateRecordNumber();
			QuestionTO question = new QuestionTO();
			question.setId(idQuestion);
			question.setIdType((Number) dataQuestion.get("ID_QUESTION_TYPE"));
			question.setTitle((String) dataQuestion.get("QUESTION_TEXT"));

			for (int j = 0; j < rowsOptions; j++) {
				Hashtable<String, Object> dataOption = resOptions.getRecordValues(j);
				Number idOption = (Number) dataOption.get("ID_OPTION");
				String optionText = (String) dataOption.get("OPTION_TEXT");
				String optionCorrect = (String) dataOption.get("OPTION_CORRECT");
				question.addOption(question.getOptions().size(), new OptionTO(idOption, optionText));
				if ("S".equals(optionCorrect)) {
					question.setCorrectOption(optionText);
				}
			}
			lQuestionsTO.add(question);
		}
		return lQuestionsTO;
	}

	private List<Number> getCorrectOptions(Number idSurvey, Connection con) throws Exception {
		TableEntity eSurQuestionOptions = (TableEntity) this.getEntity("ESurQuestionOptions");
		EntityResult resCorrectOptions = eSurQuestionOptions.query(EntityResultTools.keysvalues("ID_SURVEY", idSurvey, "OPTION_CORRECT", "S"),
				EntityResultTools.attributes("ID_OPTION", "OPTION_TEXT", "ID_QUESTION"), TableEntity.getEntityPrivilegedId(eSurQuestionOptions), con);
		int nRows = resCorrectOptions.calculateRecordNumber();

		List<Number> lOptionTO = new ArrayList<Number>();

		for (int i = 0; i < nRows; i++) {
			Number idOption = ((Vector<Number>) resCorrectOptions.get("ID_OPTION")).get(i);
			lOptionTO.add(idOption);
		}

		return lOptionTO;
	}

	/**
	 * Check id DNI, IDCONDUCTOR or IDPERSONAL exists in database. If exists, return {@link ConductorPersonal} with information of IDCONDUCTOR or IDPERSONAL. Otherwise, it returns
	 * null.
	 *
	 * @param dniIdConductor
	 *            it can be dni, idconductor or idpersonal
	 * @param con
	 * @return IDCONDUCTOR
	 * @throws OpentachNoDriverException
	 *
	 */
	protected ConductorPersonal checkDNIIdConductor(String dniIdConductor, Connection con) throws Exception {
		if (dniIdConductor.length() == 8) {
			ConductorPersonal idConductorPersonalFromDNI = this.checkDNI(dniIdConductor, con);
			if (idConductorPersonalFromDNI != null) {
				return idConductorPersonalFromDNI;
			}
			String idConductorFromIdConductor = this.checkIdConductor(dniIdConductor, con);
			if (idConductorFromIdConductor != null) {
				return new ConductorPersonal(Type.CONDUCTOR, idConductorFromIdConductor);
			}
		} else {
			String idConductorFromIdConductor = this.checkIdConductor(dniIdConductor, con);
			if (idConductorFromIdConductor != null) {
				return new ConductorPersonal(Type.CONDUCTOR, idConductorFromIdConductor);
			}
			ConductorPersonal idConductorPersonalFromDNI = this.checkDNI(dniIdConductor, con);
			if (idConductorPersonalFromDNI != null) {
				return idConductorPersonalFromDNI;
			}
		}

		throw new OpentachNoDriverException();
	}

	protected String checkIdConductor(String idConductor, Connection con) throws Exception {
		return this.querySql("sql/surveyService/SurveyServiceCheckIdConductor.sql", "IDCONDUCTOR", idConductor, con);
	}

	protected ConductorPersonal checkDNI(String dni, Connection con) throws Exception {
		String sql = new Template("sql/surveyService/SurveyServiceCheckDNI.sql").getTemplate();
		EntityResult resQuery = new QueryJdbcToEntityResultTemplate().execute(con, sql, dni);
		if (resQuery.calculateRecordNumber() > 0) {
			String idConductor = ((Vector<String>) resQuery.get("IDCONDUCTOR")).get(0);
			String idPersonal = ((Vector<String>) resQuery.get("IDPERSONAL")).get(0);
			if (idConductor != null) {
				return new ConductorPersonal(ConductorPersonal.Type.CONDUCTOR, idConductor);
			} else if (idPersonal != null) {
				return new ConductorPersonal(ConductorPersonal.Type.PERSONAL, idPersonal);
			} else {
				return null;
			}
		}
		return null;

	}

	protected String checkIdPersonal(String idPersonal, Connection con) throws Exception {
		return this.querySql("sql/surveyService/SurveyServiceCheckIdPersonal.sql", "IDPERSONAL", idPersonal, con);
	}

	protected String querySql(String service, String keyId, String valueId, Connection con) throws Exception {
		String sql = new Template(service).getTemplate();
		EntityResult resQuery = new QueryJdbcToEntityResultTemplate().execute(con, sql, valueId);
		if (resQuery.calculateRecordNumber() > 0) {
			return ((Vector<String>) resQuery.get(keyId)).get(0);
		}
		return null;
	}

}
