package com.opentach.server.services;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.db.UserEntity;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.security.SecurityTools;
import com.ontimize.jee.server.security.encrypt.IPasswordEncryptHelper;
import com.opentach.common.exception.OpentachException;
import com.opentach.common.naming.UserNaming;
import com.opentach.common.services.IUserAndRoleService;
import com.opentach.server.aspintegration.ASPIntegration;
import com.opentach.server.dao.RoleDao;
import com.opentach.server.dao.ServerRoleDao;
import com.opentach.server.dao.UserDao;
import com.opentach.server.util.UserInfoComponent;

/**
 * The Class UserAndRoleServiceImpl.
 */
@Service("UserAndRoleService")
public class UserAndRoleServiceImpl implements IUserAndRoleService {

	private static final Logger			logger		= LoggerFactory.getLogger(UserAndRoleServiceImpl.class);

	public static final String			NIVEL_CD	= "NIVEL_CD";
	public static final String			DSCR		= "DSCR";
	public static final String			ACTIVED		= "ACTIVED";
	public static final String			SRP_ID		= "SRP_ID";
	public static final String			RSP_ID		= "RSP_ID";

	@Autowired
	private DefaultOntimizeDaoHelper	daoHelper;

	@Autowired
	private ServerRoleDao				serverRoleDao;
	/** The user dao. */
	@Autowired
	private UserDao						userDao;
	/** The user dao. */
	@Autowired
	private RoleDao						roleDao;
	/** The server role dao. */
	@Autowired
	private UserInfoComponent			userInfoComponent;
	@Autowired
	private ASPIntegration				aspIntegration;

	/** The password encrypter. */
	@Autowired
	private IPasswordEncryptHelper		passwordEncrypter;

	/*
	 * (non-Javadoc)
	 */
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult userQuery(Map<?, ?> keysValues, List<?> attributes) throws OpentachException {
		if (!this.userInfoComponent.isAdmin()) {
			((Map<Object, Object>) keysValues).put("USUARIO", this.userInfoComponent.getUserLogin());
		}
		return this.daoHelper.query(this.userDao, keysValues, attributes);
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult userInsert(Map<?, ?> av) throws OpentachException {
		if (UserEntity.encrypt) {
			if (av.containsKey(UserNaming.PASSWORD)) {
				String value = av.get(UserNaming.PASSWORD).toString();
				MapTools.safePut((Map<Object, Object>) av, UserNaming.PASSWORD, this.passwordEncrypter.encrypt(value), false);
			}
		}
		if (this.aspIntegration.isAspIntegrationEnabled()) {
			final String userName = (String) av.get("USUARIO");
			final String password = (String) av.get("PASSWORD");
			final String email = (String) av.get("EMAIL");
			final String appName = this.getApplicationName(userName, av.get("NIVEL_CD"));
			final byte[] userid = this.aspIntegration.createUser(appName, userName, password, email);
			if (userid == null) {
				throw new OpentachException("M_USERID_IS_NULL");
			}
			((Map<Object, Object>) av).put("USERID", userid);
		}
		((Map<Object, Object>) av).put("F_ALTA", new Date());
		return this.daoHelper.insert(this.userDao, av);
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult userDelete(Map<?, ?> keysValues) throws OpentachException {
		try {
			if (this.aspIntegration.isAspIntegrationEnabled()) {
				final String userName = (String) keysValues.get("USUARIO");
				final String appName = this.getApplicationName(userName, null);
				this.aspIntegration.deleteUser(appName, userName);
			}
			return this.daoHelper.delete(this.userDao, keysValues);
		} finally {
			this.invalidateSecurityManager();
		}
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult userUpdate(Map<?, ?> av, Map<?, ?> kv) throws OpentachException {
		try {
			String userName = (String) kv.get("USUARIO");
			if (userName == null) {
				userName = (String) kv.get("User_");
			}

			boolean passwordReset = false;
			if (this.aspIntegration.isAspIntegrationEnabled()) {
				EntityResult erQuery = this.daoHelper.query(this.userDao, kv,
						Arrays.asList(new String[] { "EMAIL", "COMMENTS", "LASTLOGINDATE", "EMAIL", "COMMENTS", "LASTLOGINDATE" }));
				CheckingTools.checkValidEntityResult(erQuery, "M_ERROR_QUERY", true, true, new Object[] {});
				Hashtable<String, Object> currentData = erQuery.getRecordValues(0);
				final String appName = this.getApplicationName(userName, null);
				// Note: If the profile changes -> Check the possibility of App change
				// (now is not possibly in the app UI). In this case remove user from
				// last APP and add to the new
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
					this.aspIntegration.updateUser(appName, userName, emailIns, commentsIns, lastLoginNew == null ? lastLoginCD : lastLoginNew, null);
				} catch (Exception error) {
					UserAndRoleServiceImpl.logger.warn("error updating asp user", error);
				}
				String password = (String) av.get("PASSWORD");
				if (password != null) {
					this.aspIntegration.resetPassword(appName, userName, password);
					passwordReset = true;
					((Map<Object, Object>) av).put("LASTPASSWORDUPDATE", new Date());
				}
			}
			EntityResult er = this.daoHelper.update(this.userDao, av, kv);
			if ((er.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) && passwordReset) {
				return new EntityResult(EntityResult.OPERATION_SUCCESSFUL, EntityResult.NODATA_RESULT);
			}
			return er;
		} catch (OpentachException oex) {
			throw oex;
		} catch (Exception err) {
			throw new OpentachException(err.getMessage(), err);
		} finally {
			this.invalidateSecurityManager();
		}
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult roleQuery(Map<?, ?> keysValues, List<?> attributes) throws OpentachException {
		if (!this.userInfoComponent.isAdmin()) {
			((Map<Object, Object>) keysValues).put("NIVEL_CD", this.userInfoComponent.getProfile());
		}
		return this.daoHelper.query(this.roleDao, keysValues, attributes);
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult roleUpdate(Map<?, ?> attributesValues, Map<?, ?> keysValues) throws OpentachException {
		try {
			return this.daoHelper.update(this.roleDao, attributesValues, keysValues);
		} finally {
			this.invalidateSecurityManager();
		}
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult roleDelete(Map<?, ?> keysValues) throws OpentachException {
		try {
			this.serverRoleDao.unsafeDelete(keysValues);
			return this.daoHelper.delete(this.roleDao, keysValues);
		} finally {
			this.invalidateSecurityManager();
		}
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult roleInsert(Map<?, ?> keysValues) throws OpentachException {
		try {
			return this.daoHelper.insert(this.roleDao, keysValues);
		} finally {
			this.invalidateSecurityManager();
		}
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult serverRoleQuery(Map<?, ?> keysValues, List<?> attributes) throws OpentachException {
		if (!keysValues.containsKey(UserAndRoleServiceImpl.NIVEL_CD)) {
			return this.daoHelper.query(this.serverRoleDao, keysValues, attributes, "id_serverRole_all");
		}
		return this.daoHelper.query(this.serverRoleDao, keysValues, attributes, "id_serverRole");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public EntityResult serverRoleUpdate(Map<?, ?> attributesValues, Map<?, ?> keysValues) throws OpentachException {
		try {
			if ("S".equals(attributesValues.get(UserAndRoleServiceImpl.ACTIVED))) {
				// insert
				Map<String, Object> valuesToInsert = new HashMap<>();
				valuesToInsert.put(UserAndRoleServiceImpl.NIVEL_CD, keysValues.get(UserAndRoleServiceImpl.NIVEL_CD));
				valuesToInsert.put(UserAndRoleServiceImpl.SRP_ID, keysValues.get(UserAndRoleServiceImpl.SRP_ID));
				return this.daoHelper.insert(this.serverRoleDao, valuesToInsert);
			} else if (keysValues.get(UserAndRoleServiceImpl.RSP_ID) != null) {
				// delete
				Map<String, Object> valuesToDelete = new HashMap<>();
				valuesToDelete.put(UserAndRoleServiceImpl.RSP_ID, keysValues.get(UserAndRoleServiceImpl.RSP_ID));
				return this.daoHelper.delete(this.serverRoleDao, valuesToDelete);
			}
			return null;
		} finally {
			this.invalidateSecurityManager();
		}
	}

	private void invalidateSecurityManager() {
		SecurityTools.invalidateSecurityManager(this.daoHelper.getApplicationContext());
	}

	private String getApplicationName(String user, Object profile) throws OpentachException {
		if (profile == null) {
			Map<String, Object> ccvv = new HashMap<String, Object>();
			ccvv.put("USUARIO", user);
			EntityResult query = this.daoHelper.query(this.userDao, ccvv, Arrays.asList(new String[] { "NIVEL_CD" }), "usuarios_todos");
			if ((query.getCode() == EntityResult.OPERATION_SUCCESSFUL) && (query.calculateRecordNumber() == 1)) {
				profile = ((List<Object>) query.get("NIVEL_CD")).get(0);
			}
		}

		CheckingTools.failIf(profile == null, OpentachException.class, "E_UNDEFINED_PROFILE", new Object[] {});

		try {
			Map<String, Object> ccvv2 = new HashMap<String, Object>();
			ccvv2.put("NIVEL_CD", profile);
			EntityResult query = this.daoHelper.query(this.userDao, ccvv2, Arrays.asList(new String[] { "APP" }));
			CheckingTools.checkValidEntityResult(query, "E_UNKNOWED_PROFILE", true, true, new Object[] {});
			return (String) ((List<Object>) query.get("APP")).get(0);
		} catch (Exception ex) {
			throw new OpentachException("E_UNKNOWED_PROFILE", ex);
		}
	}
}
