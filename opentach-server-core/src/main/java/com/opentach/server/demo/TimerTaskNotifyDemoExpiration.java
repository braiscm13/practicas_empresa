package com.opentach.server.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.mail.MailService;

public class TimerTaskNotifyDemoExpiration extends TimerTask {

	private static final Logger			logger	= LoggerFactory.getLogger(TimerTaskNotifyDemoExpiration.class);
	private final IOpentachServerLocator	locator;

	public TimerTaskNotifyDemoExpiration(IOpentachServerLocator locator) {
		super();
		this.locator = locator;
	}

	@Override
	public void run() {
		try {
			TimerTaskNotifyDemoExpiration.logger.debug("--> TimerTaskNotifyDemoExpiration: Querying...");
			this.execute();
		} catch (final Exception exception) {
			TimerTaskNotifyDemoExpiration.logger.error(null, exception);
		}
	}

	public Date addAndTrimToMidnight(Date d, int nDays) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_YEAR, nDays);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public void execute() throws Exception {
		try {
			// Buscamos usuarios a los que les falte 1 semana
			final List<Object> vUsersToExpire = this.queryToExpireUsersInDate(this.addAndTrimToMidnight(new Date(), 7));
			vUsersToExpire.addAll(this.queryToExpireUsersInDate(this.addAndTrimToMidnight(new Date(), 2)));

			for (final Object idUser : vUsersToExpire) {
				// Para cada usuario consultamos las empresas a las que esta asignado
				final List<Object> vCompanies = this.queryUserCompanies(idUser);
				for (final Object cif : vCompanies) {
					final List<String> emails = this.queryCompanyNotifEmails(cif);
					if ((emails != null) && !emails.isEmpty()) {
						this.sendMail(emails, idUser, cif);
					}
				}
			}

		} catch (final Exception exception) {
			TimerTaskNotifyDemoExpiration.logger.error(null, exception);
		}
	}

	private void sendMail(List<String> emails, Object idUser, Object cif) throws Exception {
		this.locator.getService(MailService.class).sendMail(new NotifyDemoMailComposer(emails, idUser, cif, this.locator));
	}

	private List<String> queryCompanyNotifEmails(Object cif) throws Exception {

		final Entity entity = this.locator.getEntityReferenceFromServer("ENotificacionesEmp");
		final Hashtable<String, Object> cv = new Hashtable<String, Object>();
		cv.put("CIF", cif);
		final EntityResult res = entity.query(cv, EntityResultTools.attributes("EMAIL"), TableEntity.getEntityPrivilegedId(entity));
		final List<String> mails = (List<String>) res.get("EMAIL");
		return mails == null ? new ArrayList<String>() : mails;
	}

	private List<Object> queryUserCompanies(Object idUser) throws Exception {
		final Entity entity = this.locator.getEntityReferenceFromServer("EUsuDfEmp");
		final EntityResult res = entity.query(EntityResultTools.keysvalues("USUARIO", idUser), EntityResultTools.attributes("CIF"), TableEntity.getEntityPrivilegedId(entity));
		final List<Object> cifs = (List<Object>) res.get("CIF");
		return cifs == null ? new ArrayList<Object>() : cifs;
	}

	private List<Object> queryToExpireUsersInDate(Date date) throws Exception {

		final TableEntity entity = (TableEntity) this.locator.getEntityReferenceFromServer("Usuario");
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		final BasicExpression be = new BasicExpression(new BasicField("to_char(DMAXLOGIN,'dd/mm/yyyy')"), BasicOperator.EQUAL_OP, sdf.format(date));
		final Hashtable<Object, Object> cv = new Hashtable<Object, Object>();
		cv.put(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, be);
		final EntityResult res = entity.query(cv, EntityResultTools.attributes("USUARIO"), TableEntity.getEntityPrivilegedId(entity));
		final List<Object> users = (List<Object>) res.get("USUARIO");
		return users == null ? new ArrayList<Object>() : users;
	}

}
