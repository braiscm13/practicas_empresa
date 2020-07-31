package com.opentach.server.user;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.user.IUserService;
import com.opentach.server.mail.MailService;
import com.utilmize.server.services.UAbstractService;

/**
 * The Class UserService.
 */
public class UserService extends UAbstractService implements IUserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	/**
	 * Instantiates a new user service.
	 *
	 * @param port
	 *            the port
	 * @param erl
	 *            the erl
	 * @param hconfig
	 *            the hconfig
	 * @throws Exception
	 *             the exception
	 */
	public UserService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.common.user.IUserService#sendWelcomeMail(java.lang.String, int)
	 */
	@Override
	public void sendWelcomeMail(String login, int sesionId) throws Exception {
		try {
			CheckingTools.failIf(login == null, "E_INVALID_LOGIN");
			this.checkPermission("EUsuariosTodos", "Query", sesionId);
			TransactionalEntity eUsers = this.getEntity("EUsuariosTodos");
			EntityResult er = ((Entity) eUsers).query(EntityResultTools.keysvalues("USUARIO", login), EntityResultTools.attributes("PASSWORD", "EMAIL"),
					this.getSessionId(sesionId, eUsers));
			CheckingTools.checkValidEntityResult(er, "E_LOGIN_NOT_FOUND", true, true, new Object[] { login });
			Hashtable<String, Object> record = er.getRecordValues(0);
			String pass = (String) record.get("PASSWORD");
			String mail = (String) record.get("EMAIL");
			CheckingTools.failIf(pass == null, "E_INVALID_PASS");
			CheckingTools.failIf(mail == null, "E_INVALID_MAIL");
			this.getService(MailService.class).sendMail(new WelcomeMailComposer(login, pass, mail));
		} catch (Exception error) {
			UserService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}
}
