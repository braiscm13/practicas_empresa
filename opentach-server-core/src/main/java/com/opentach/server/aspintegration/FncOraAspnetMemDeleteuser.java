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

import com.opentach.server.util.dbaspintegration.IDbFunction;

@Component
public class FncOraAspnetMemDeleteuser implements IDbFunction<Number> {

	@Autowired
	private DataSource dataSource;

	public FncOraAspnetMemDeleteuser() {
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
						new SqlParameter("TABLESTODELETEFROM", Types.NUMERIC), //
						new SqlInOutParameter("NUMTABLESDELETEDFROM", Types.NUMERIC) //
						).//
				withReturnValue().//
				withoutProcedureColumnMetaDataAccess();
		SqlParameterSource paramMap = new MapSqlParameterSource(parameters);
		Map<String, Object> res = call.execute(paramMap);
		return (Number) res.get("RESULT");
	}

}
