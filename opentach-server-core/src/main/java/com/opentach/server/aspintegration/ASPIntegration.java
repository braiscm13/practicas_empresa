package com.opentach.server.aspintegration;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ontimize.jee.common.tools.Pair;
import com.opentach.common.exception.OpentachException;

@Component
public class ASPIntegration {
	@Autowired
	private FncOraAspnetMemCreateuser fncCreateUser;
	@Autowired
	private FncOraAspnetMemDeleteuser	fncDeleteUser;
	@Autowired
	private FncOraAspnetMemUpdateuser	fncUpdateUser;
	@Autowired
	private FncOraAspnetMemResetPassword	fncResetPassword;
	@Autowired
	private DataSource						dataSource;

	public ASPIntegration() {
		super();
	}

	public boolean isAspIntegrationEnabled() {
		try {
			new JdbcTemplate(this.dataSource).execute("select 1 from ORA_ASPNET_MEMBERSHIP WHERE rownum = 1");
			return true;
		} catch (Exception err) {
			return false;
		}
	}

	public byte[] createUser(final String appName, final String userName, final String password, final String email) throws OpentachException {
		java.sql.Date tNow = new java.sql.Date(System.currentTimeMillis());
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("APPLICATIONNAME_", appName);
		parameters.put("USERNAME_", userName);
		parameters.put("PASSWORD_", password);
		parameters.put("PASSWORDSALT_", password);
		parameters.put("EMAIL_", email);
		parameters.put("CURRENTTIMEUTC", tNow);
		parameters.put("CREATEDATE_", tNow);
		Pair<Number, byte[]> res = this.fncCreateUser.execute(parameters);
		if (res.getFirst().intValue() != 0) {
			throw new OpentachException("M_ERROR_CREATE_ASP_USER");
		}
		return res.getSecond();
	}

	public void deleteUser(final String appName, final String userName) throws OpentachException {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("APPLICATIONNAME_", appName);
		parameters.put("USERNAME_", userName);
		parameters.put("TABLESTODELETEFROM", 1);
		Number res = this.fncDeleteUser.execute(parameters);
		if (res.intValue() != 0) {
			throw new OpentachException("M_ERROR_CREATE_ASP_USER");
		}
	}

	public void updateUser(final String appName, final String userName, final String email, final String obsr, final Date dLastLogin,
			final Date dLastActivity) throws OpentachException {
		java.sql.Date tNow = new java.sql.Date(System.currentTimeMillis());
		java.sql.Date sLastLogin = null;
		java.sql.Date sLastActivity = null;
		if (dLastLogin != null) {
			sLastLogin = new java.sql.Date(dLastLogin.getTime());
		}
		if (dLastActivity != null) {
			sLastActivity = new java.sql.Date(dLastActivity.getTime());
		} else {
			sLastActivity = new java.sql.Date(System.currentTimeMillis());
		}
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("APPLICATIONNAME_", appName);
		parameters.put("USERNAME_", userName);
		parameters.put("EMAIL_", email);
		parameters.put("COMMENTS_", obsr);
		parameters.put("ISAPPROVED_", 1);
		parameters.put("LASTLOGINDATE_", sLastLogin);
		parameters.put("LASTACTIVITYDATE_", sLastActivity);
		parameters.put("UNIQUEEMAIL", 0);
		parameters.put("CURRENTTIMEUTC_", tNow);
		Number res = this.fncUpdateUser.execute(parameters);
		if (res.intValue() != 0) {
			throw new OpentachException("M_ERROR_CREATE_ASP_USER");
		}
	}

	public void resetPassword(final String appName, final String userName, final String password) throws OpentachException {
		Timestamp tNow = new Timestamp(System.currentTimeMillis());
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("APPLICATIONNAME_", appName);
		parameters.put("USERNAME_", userName);
		parameters.put("NEWPASSWORD", password);
		parameters.put("MAXINVALIDPASSWORDATTEMPTS", 1);
		parameters.put("PASSWORDATTEMPTWINDOW", 1);
		parameters.put("PASSWORDSALT_", password);
		parameters.put("CURRENTTIMEUTC", tNow);
		parameters.put("PASSWORDFORMAT_", 0);
		Number res = this.fncResetPassword.execute(parameters);
		if (res.intValue() != 0) {
			throw new OpentachException("M_ERROR_CREATE_ASP_USER");
		}
	}

}
