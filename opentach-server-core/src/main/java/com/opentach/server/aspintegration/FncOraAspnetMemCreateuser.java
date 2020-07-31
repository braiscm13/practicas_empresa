package com.opentach.server.aspintegration;

import java.sql.Types;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.ontimize.jee.common.tools.Pair;
import com.opentach.server.util.dbaspintegration.IDbFunction;

import oracle.jdbc.OracleTypes;

@Component
public class FncOraAspnetMemCreateuser implements IDbFunction<Pair<Number, byte[]>> {

	@Autowired
	private DataSource dataSource;

	public FncOraAspnetMemCreateuser() {
		super();
	}

	@Override
	public Pair<Number, byte[]> execute(Map<String, Object> parameters) {
		SimpleJdbcCall call = new SimpleJdbcCall(this.dataSource).//
				withFunctionName("ORA_ASPNET_MEM_CREATEUSER").//
				// withCatalogName("").//
				declareParameters(new SqlOutParameter("RESULT", Types.LONGVARBINARY), //
						new SqlParameter("APPLICATIONNAME_", Types.VARCHAR), //
						new SqlParameter("USERNAME_", Types.VARCHAR), //
						new SqlParameter("PASSWORD_", Types.VARCHAR), //
						new SqlParameter("PASSWORDSALT_", Types.VARCHAR), //
						new SqlParameter("EMAIL_", Types.VARCHAR), //
						new SqlParameter("PASSWORDQUESTION_", Types.VARCHAR), //
						new SqlParameter("PASSWORDANSWER_", Types.VARCHAR), //
						new SqlParameter("ISAPPROVED_", Types.NUMERIC), //
						new SqlParameter("CURRENTTIMEUTC", Types.DATE), //
						new SqlParameter("CREATEDATE_", Types.DATE), //
						new SqlParameter("UNIQUEEMAIL", Types.NUMERIC), //
						new SqlParameter("PASSWORDFORMAT_", Types.NUMERIC), //
						new SqlInOutParameter("USERID_", OracleTypes.RAW)//
						).//
				withReturnValue().//
				withoutProcedureColumnMetaDataAccess();
		SqlParameterSource paramMap = new MapSqlParameterSource(parameters);
		Map<String, Object> res = call.execute(paramMap);
		return new Pair<>((Number) res.get("RESULT"), (byte[]) res.get("UserID_"));
	}

}
