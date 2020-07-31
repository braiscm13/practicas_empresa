package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.db.TableEntity;
import com.ontimize.db.UserEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.locator.SecureReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.user.IUserData;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.util.asp.DeprecatedASPUtils;

/**
 * To work whith ASP.NET tables
 *
 * @author rafael.lopez
 *
 */
public class Usuario extends UserEntity {

	private static final Logger logger = LoggerFactory.getLogger(Usuario.class);
	static {
		UserEntity.encrypt = false;
		UserEntity.User = "USUARIO";
	}

	public Usuario(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sesionId, Connection con) throws Exception {
		if ((sesionId > 0) && !cv.containsKey("NIVEL_CD")) {
			final IUserData du = ((OpentachServerLocator) this.locator).getUserData(sesionId);
			final String nivel = du.getLevel();
			final SearchValue vb = new SearchValue(SearchValue.MORE_EQUAL, nivel);
			cv.put("NIVEL_CD", vb);
		}
		// cv.remove("Password");
		return super.query(cv, v, sesionId, con);
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection conn) throws Exception {
		final String userName = (String) av.get("USUARIO");
		final String password = (String) av.get("PASSWORD");
		final String email = (String) av.get("EMAIL");
		final String appName = this.getApplicationName(userName, av.get("NIVEL_CD"), sesionId, conn);
		final byte[] userid = DeprecatedASPUtils.createUser(appName, userName, password, email, conn);
		if (userid == null) {
			throw new Exception("M_USERID_IS_NULL");
		}
		av.put("USERID", new BytesBlock(userid));
		av.put("F_ALTA", new Date());
		EntityResult er = super.insert(av, sesionId, conn);
		return er;
	}

	private String getApplicationName(String user, Object profile, int sesionId, Connection conn) throws Exception {
		if (profile == null) {
			Hashtable<String, Object> ccvv = new Hashtable<String, Object>();
			ccvv.put("USUARIO", user);
			TableEntity entTodos = (TableEntity) this.getEntityReference("EUsuariosTodos");
			EntityResult query = entTodos.query(ccvv, new Vector(Arrays.asList(new String[] { "NIVEL_CD" })), TableEntity.getEntityPrivilegedId(entTodos), conn);
			if ((query.getCode() == EntityResult.OPERATION_SUCCESSFUL) && (query.calculateRecordNumber() == 1)) {
				profile = ((Vector<Object>) query.get("NIVEL_CD")).get(0);
			}
		}
		if (profile == null) {
			throw new Exception("E_UNDEFINED_PROFILE");
		}

		try {
			TableEntity eProfile = (TableEntity) this.getEntityReference("EPerfil");
			Hashtable<String, Object> ccvv2 = new Hashtable<String, Object>();
			ccvv2.put("NIVEL_CD", profile);
			EntityResult query = eProfile.query(ccvv2, new Vector(Arrays.asList(new String[] { "APP" })), this.getSessionId(sesionId, eProfile), conn);
			if ((query.getCode() == EntityResult.OPERATION_SUCCESSFUL) && (query.calculateRecordNumber() == 1)) {
				return (String) ((Vector<Object>) query.get("APP")).get(0);
			}
		} catch (Exception e) {
			throw new Exception("E_UNKNOWED_PROFILE", e);
		}
		throw new Exception("E_UNKNOWED_PROFILE");
	}

	@Override
	public EntityResult delete(Hashtable kv, int sessionId, Connection conn) throws Exception {
		final String userName = (String) kv.get("USUARIO");
		final String appName = this.getApplicationName(userName, null, sessionId, conn);
		EntityResult er = super.delete(kv, sessionId, conn);
		DeprecatedASPUtils.deleteUser(appName, userName, conn);
		return er;
	}

	@Override
	public EntityResult update(Hashtable av, Hashtable kv, int sessionId, Connection conn) throws Exception {
		String userName = (String) kv.get("USUARIO");
		if (userName == null) {
			userName = (String) kv.get("User_");
		}

		final String appName = this.getApplicationName(userName, null, sessionId, conn);

		// Note: If the profile changes -> Check the possibility of App change
		// (now is not possibly in the app UI). In this case remove user from
		// last APP and add to the new

		EntityResult erQuery = this.query(kv, new Vector(0), sessionId, conn);
		final int count = erQuery.calculateRecordNumber();
		if ((erQuery.getCode() != EntityResult.OPERATION_SUCCESSFUL) || (count != 1)) {
			throw new Exception("M_ERROR_QUERY");
		}
		Hashtable<String, Object> currentData = erQuery.getRecordValues(0);
		final String emailCD = (String) currentData.get("EMAIL");
		final String commentsCD = (String) currentData.get("COMMENTS");
		final Date lastLoginCD = (Date) currentData.get("LASTLOGINDATE");
		final Object emailNew = av.get("EMAIL");
		final Object commentsNew = av.get("COMMENTS");
		final Date lastLoginNew = (Date) av.get("LASTLOGINDATE");
		String emailIns;
		String commentsIns;
		if (emailNew instanceof NullValue) {
			emailIns = null;
		} else {
			emailIns = emailNew == null ? emailCD : (String) emailNew;
		}
		if (commentsNew instanceof NullValue) {
			commentsIns = null;
		} else {
			commentsIns = commentsNew == null ? commentsCD : (String) commentsNew;
		}
		try {
			DeprecatedASPUtils.updateUser(appName, userName, emailIns, commentsIns, lastLoginNew == null ? lastLoginCD : lastLoginNew, null, conn);
		} catch (Exception error) {
			Usuario.logger.warn("error updating asp user", error);
		}
		String password = (String) av.get("PASSWORD");
		boolean passwordReset = false;
		if (password != null) {
			DeprecatedASPUtils.resetPassword(appName, userName, password, conn);
			passwordReset = true;
			av.put("LASTPASSWORDUPDATE", new Date());
		}
		EntityResult er = super.update(av, kv, sessionId, conn);
		if ((er.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) && passwordReset) {
			return new EntityResult(EntityResult.OPERATION_SUCCESSFUL, EntityResult.NODATA_RESULT);
		}
		return er;
	}

	/**
	 * Lanza excepción si el usuario existe y ha expirado
	 *
	 * @param user
	 * @throws Exception
	 */
	public static void checkExpired(String user, SecureReferenceLocator locator) throws Exception {
		if (user != null) {
			Entity eUsers = locator.getEntityReferenceFromServer("Usuario");
			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			cv.put("USUARIO", user);
			EntityResult res = eUsers.query(cv, new Vector<String>(Arrays.asList(new String[] { "DMAXLOGIN" })), TableEntity.getEntityPrivilegedId(eUsers));
			if ((res != null) && (res.calculateRecordNumber() == 1)) {
				Date maxDate = (Date) res.getRecordValues(0).get("DMAXLOGIN");
				if ((maxDate != null) && maxDate.before(new Date())) {
					throw new Exception("E_USER_EXPIRED");
				}
			}
		}
	}

	public int getSessionId(int sessionId, Entity ent) {
		return (TableEntity.getEntityPrivilegedId(this) == sessionId) ? TableEntity.getEntityPrivilegedId(ent) : sessionId;
	}

}
