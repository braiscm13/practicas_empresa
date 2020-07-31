package com.opentach.common.tasks;

import java.rmi.Remote;
import java.util.Hashtable;

import net.sf.jasperreports.engine.JasperPrint;

public interface ITasksReport extends Remote {

	JasperPrint generateReport(Hashtable<Object, Object> filters, int sessionId) throws Exception;


}
