package com.opentach.common.employee.services;

import java.rmi.Remote;
import java.sql.Connection;
import java.util.Date;
import java.util.Map;

public interface IConductoresEmp extends Remote {

	public String generateWebUserName(int sessionId, String cif, String employee, String firstName, String secondName) throws Exception;

	public String generateWebPassword(int length) throws Exception;

	public void emailWebAuth(String cif, String employee, int sessionId) throws Exception;

	public void generateFakeData(int sessionId, String cif, String employee, Date since, Date until) throws Exception;

	public Map<String, Object> getDriverInfoByIdConductor(Object idConductor, String companyCif, Connection con, String... attributes) throws Exception;
}
