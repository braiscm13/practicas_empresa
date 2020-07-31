package com.opentach.server.aspintegration;

import java.sql.Types;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.opentach.server.util.dbaspintegration.IDbFunction;

@Component
public class FncOraAspnetMemResetPassword implements IDbFunction<Number> {

	@Autowired
	private DataSource dataSource;

	public FncOraAspnetMemResetPassword() {
		super();
	}

	@Override
	public Number execute(Map<String, Object> parameters) {
		SimpleJdbcCall call = new SimpleJdbcCall(this.dataSource).//
				withFunctionName("ORA_ASPNET_MEM_DELETEUSER").//
				// withCatalogName("").//
				declareParameters(new SqlOutParameter("RESULT",Types.NUMERIC), //
						new SqlParameter("APPLICATIONNAME_", Types.VARCHAR), //
						new SqlParameter("USERNAME_", Types.VARCHAR), //
						new SqlParameter("NEWPASSWORD", Types.VARCHAR), //
						new SqlParameter("MAXINVALIDPASSWORDATTEMPTS", Types.NUMERIC), //
						new SqlParameter("PASSWORDATTEMPTWINDOW", Types.NUMERIC), //
						new SqlParameter("PASSWORDSALT_", Types.VARCHAR), //
						new SqlParameter("CURRENTTIMEUTC", Types.DATE), //
						new SqlParameter("PASSWORDFORMAT_", Types.NUMERIC), //
						new SqlParameter("PASSWORDANSWER_", Types.VARCHAR) //
						).//
				withReturnValue().//
				withoutProcedureColumnMetaDataAccess();
		SqlParameterSource paramMap = new MapSqlParameterSource(parameters);
		Map<String, Object> res = call.execute(paramMap);
		return (Number) res.get("RESULT");
	}

}
