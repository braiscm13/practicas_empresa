package com.opentach.server.util.asp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import com.utilmize.server.tools.sqltemplate.FunctionJdbcTemplate;

import oracle.jdbc.OracleTypes;

public final class DeprecatedASPUtils {

	private DeprecatedASPUtils() {}

	public static final byte[] createUser(final String appName, final String userName, final String password, final String email, Connection conn) throws Exception {
		java.sql.Date tNow = new java.sql.Date(System.currentTimeMillis());
		CallableStatement cstmt = null;
		try {
			cstmt = conn.prepareCall("{ ? = call ORA_ASPNET_MEM_CREATEUSER(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			cstmt.registerOutParameter(1, Types.NUMERIC);
			cstmt.setString(2, appName);
			cstmt.setString(3, userName);
			cstmt.setString(4, password);
			cstmt.setString(5, password);
			if (email != null) {
				cstmt.setString(6, email);
			} else {
				cstmt.setNull(6, Types.VARCHAR);
			}
			cstmt.setNull(7, Types.VARCHAR);
			cstmt.setNull(8, Types.VARCHAR);
			cstmt.setInt(9, 1);
			cstmt.setDate(10, tNow);
			cstmt.setDate(11, tNow);
			cstmt.setInt(12, 0);
			cstmt.setInt(13, 0);
			cstmt.registerOutParameter(14, OracleTypes.RAW);
			cstmt.executeUpdate();
			int success = cstmt.getInt(1);
			if (success != 0) {
				throw new Exception("M_ERROR_CREATE_ASP_USER");
			}
			return (byte[]) cstmt.getObject(14);
		} finally {
			if (cstmt != null) {
				try {
					cstmt.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static final void deleteUser(final String appName, final String userName, Connection conn) throws Exception {
		Number success = new FunctionJdbcTemplate<Number>().execute(conn, "ORA_ASPNET_MEM_DELETEUSER", Types.NUMERIC, appName, userName, 1, null);
		if (success.intValue() != 0) {
			throw new Exception("M_ERROR_DELETE_ASP_USER");
		}
	}

	public static final void updateUser(final String appName, final String userName, final String email, final String obsr, final Date dLastLogin, final Date dLastActivity,
			final Connection conn) throws Exception {
		Timestamp tNow = new Timestamp(System.currentTimeMillis());
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

		Number success = new FunctionJdbcTemplate<Number>().execute(conn, "ORA_ASPNET_MEM_UPDATEUSER", Types.NUMERIC, appName, userName, email, obsr, 1, sLastLogin, sLastActivity,
				0, tNow);
		if (success.intValue() != 0) {
			throw new Exception("M_ERROR_UPDATE_ASP_USER");
		}
	}

	public static final void resetPassword(final String appName, final String userName, final String password, final Connection conn) throws Exception {
		Timestamp tNow = new Timestamp(System.currentTimeMillis());
		Number success = new FunctionJdbcTemplate<Number>().execute(conn, "ORA_ASPNET_MEM_RESETPASSWORD", Types.NUMERIC, appName, userName, password, 1, 1, password, tNow, 0,
				null);
		if (success.intValue() != 0) {
			throw new Exception("M_ERROR_UPDATE_ASP_PASSWORD");
		}
	}

}
