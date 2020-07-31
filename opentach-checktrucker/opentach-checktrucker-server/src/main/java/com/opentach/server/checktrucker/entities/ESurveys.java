package com.opentach.server.checktrucker.entities;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;

public class ESurveys extends TableEntity {

	private static final Logger logger = LoggerFactory.getLogger(ESurveys.class);

	public ESurveys(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public ESurveys(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop, Properties aliasProp) throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}

	@Override
	public EntityResult delete(Hashtable keysValues, int sessionId, Connection con) throws Exception {
		TableEntity eSurSurveyQuestions = (TableEntity) this.getEntityReference("ESurQuestionsSurvey");
		EntityResult res = eSurSurveyQuestions.query(keysValues, EntityResultTools.attributes("ID_SURVEY", "ID_QUESTION"), sessionId, con);
		EntityResult resFinal = new EntityResult();
		for (int i = 0; i < res.calculateRecordNumber(); i++) {
			Number idSurvey = (Number) res.getRecordValues(i).get("ID_SURVEY");
			Number idQuestion = (Number) res.getRecordValues(i).get("ID_QUESTION");
			resFinal = eSurSurveyQuestions.delete(EntityResultTools.keysvalues("ID_SURVEY", idSurvey, "ID_QUESTION", idQuestion), sessionId, con);
		}

		return super.delete(keysValues, sessionId, con);
	}
}
