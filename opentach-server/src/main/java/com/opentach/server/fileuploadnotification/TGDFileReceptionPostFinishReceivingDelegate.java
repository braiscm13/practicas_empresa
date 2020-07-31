/*
 *
 */
package com.opentach.server.fileuploadnotification;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ontimize.jee.common.tools.Template;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.ICDOUserData;
import com.opentach.common.user.IUserData;
import com.opentach.common.util.concurrent.PoolExecutors;
import com.opentach.messagequeue.api.IMessageQueueListener;
import com.opentach.messagequeue.api.IMessageQueueManager;
import com.opentach.messagequeue.api.IMessageQueueMessage;
import com.opentach.messagequeue.api.QueueNames;
import com.opentach.messagequeue.api.messages.PostFinishReceivingMessage;
import com.opentach.server.filereception.FileUploadComposer;
import com.opentach.server.mail.MailService;
import com.opentach.server.util.spring.ILocatorReferencer;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

@Component
public class TGDFileReceptionPostFinishReceivingDelegate implements InitializingBean, IMessageQueueListener {

	@Autowired
	private IMessageQueueManager	queueService;
	@Autowired
	private ILocatorReferencer		locatorRef;

	private static final Logger		logger			= LoggerFactory.getLogger(TGDFileReceptionPostFinishReceivingDelegate.class);
	private final ExecutorService	mailExecutor	= PoolExecutors.newFixedThreadPool("EFicherosPostFinishReceiving", 4);

	public TGDFileReceptionPostFinishReceivingDelegate() {
		super();
	}

	@Override
	public void afterPropertiesSet() {
		this.queueService.registerListener(QueueNames.POST_FINISH_RECEIVING, this);
	}

	@Override
	public void onQueueEvent(String queueName, IMessageQueueMessage message) {
		try {
			this.postFinishReceiving((PostFinishReceivingMessage) message);
		} catch (Exception err) {
			TGDFileReceptionPostFinishReceivingDelegate.logger.error(null, err);
		}
	}

	/**
	 * Post finish receiving.
	 *
	 * @param receivingId
	 *            the receiving id
	 * @param sesionId
	 *            the sesion id
	 * @param conn
	 *            the conn
	 * @throws Exception
	 *             the exception
	 */
	public void postFinishReceiving(PostFinishReceivingMessage message) throws Exception {
		if (message.isAssignedToContract()) {
			TGDFileReceptionPostFinishReceivingDelegate.logger.info("enqueuing CDOmail");
			this.mailExecutor.submit(() -> {
				new OntimizeConnectionTemplate<Void>() {

					@Override
					protected Void doTask(Connection con) throws UException {
						try {
							Object cgContrato = message.getCgContract();
							final Number idFile = message.getIdFichero();
							final String nomb = message.getFileName();
							TGDFileReceptionPostFinishReceivingDelegate.this.sendMailAfterFileUpload(cgContrato, idFile, nomb, con, message.getSessionId());
							return null;
						} catch (Exception ex) {
							throw new UException(ex);
						}
					}
				}.execute(this.locatorRef.getLocator().getConnectionManager(), true);
				return null;
			});
		}
	}

	/**
	 * Send mail after file upload.
	 *
	 * @param cgContrato
	 *            the cg contrato
	 * @param idFile
	 *            the id file
	 * @param nombFile
	 *            the nomb file
	 * @param conn
	 *            the conn
	 * @param sessionID
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	final void sendMailAfterFileUpload(Object cgContrato, Number idFile, String nombFile, Connection conn, int sessionID) throws Exception {
		IUserData ud = this.locatorRef.getLocator().getUserData(sessionID);
		if (ud != null) {
			if (ud.isAnonymousUser() && ud.sendMail2Company()) {
				Map<String, Object> mParams = this.getMailInfo(ud, cgContrato, idFile, nombFile, conn, sessionID);
				if (mParams != null) {
					String mailto = (String) mParams.remove("MAILTO");
					Locale locale = (Locale) mParams.remove("LOCALE");
					if (mailto != null) {
						this.sendMailAfterFileUpload(mailto, locale, mParams);
					}
				}
			}
		}
	}

	/**
	 * Send mail after file upload.
	 *
	 * @param mailTo
	 *            the mail to
	 * @param locale
	 *            the locale
	 * @param mParams
	 *            the m params
	 */
	private final void sendMailAfterFileUpload(final String mailTo, final Locale locale, Map<String, Object> mParams) {
		try {
			this.locatorRef.getLocator().getService(MailService.class).sendMail(new FileUploadComposer(mailTo, null, locale, mParams));
		} catch (Exception e) {
			TGDFileReceptionPostFinishReceivingDelegate.logger.error(null, e);
		}
	}

	/**
	 * Gets the mail info.
	 *
	 * @param ud
	 *            the ud
	 * @param cgContrato
	 *            the cg contrato
	 * @param idFile
	 *            the id file
	 * @param nombFile
	 *            the nomb file
	 * @param conn
	 *            the conn
	 * @param sessionID
	 *            the session id
	 * @return the mail info
	 * @throws Exception
	 *             the exception
	 */
	private final Map<String, Object> getMailInfo(final IUserData ud, final Object cgContrato, final Number idFile, final String nombFile, Connection conn, int sessionID)
			throws Exception {
		String sql = new Template("tachofiletransfer-sql/EFicherosTGD_getMailInfo.sql").getTemplate();
		return new QueryJdbcTemplate<Map<String, Object>>() {

			@Override
			protected Map<String, Object> parseResponse(ResultSet rset) throws UException {
				try {
					Map<String, Object> mResult = null;
					if (rset.next()) {
						mResult = new HashMap<String, Object>();
						mResult.put(OpentachFieldNames.FILENAME_FIELD, nombFile);
						if (ud instanceof ICDOUserData) {
							mResult.put(FileUploadComposer.CDODSCR, ((ICDOUserData) ud).getDscr());
						}
						mResult.put("MAILTO", rset.getString(1));
						mResult.put(OpentachFieldNames.NAME_FIELD, rset.getString(2));
						String sLocale = rset.getString(3);
						if (sLocale != null) {
							mResult.put("LOCALE", new Locale(sLocale));
						}
					}
					return mResult;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(conn, sql, idFile.intValue());
	}
}
