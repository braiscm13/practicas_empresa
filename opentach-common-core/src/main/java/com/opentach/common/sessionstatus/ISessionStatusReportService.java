package com.opentach.common.sessionstatus;


import java.rmi.Remote;
import java.util.Hashtable;

import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;

public interface ISessionStatusReportService extends Remote {
	public enum GroupingTime {
		MONTH, WEEK, DAY;

		@Override
		public String toString() {
			switch (this) {
				case MONTH:
					return "Month";
				case WEEK:
					return "Week";
				case DAY:
					return "Day";
			}
			throw new RuntimeException("invalid grouping");
		}

		public static GroupingTime fromString(String value) {
			if ("month".equalsIgnoreCase(value)) {
				return MONTH;
			} else if ("week".equalsIgnoreCase(value)) {
				return WEEK;
			} else if ("day".equalsIgnoreCase(value)) {
				return DAY;
			}
			throw new RuntimeException("invalid grouping time");
		}

	}

	/** The Constant ID. */
	final static String	ID	= "SessionStatusReportService";

	EntityResult getSessionMeanTime(BasicExpression queryFilter, GroupingTime grouping, int sessionId) throws Exception;

	EntityResult getSessionNumConnections(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception;

	EntityResult getSessionConnectionsPerHour(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception;

	EntityResult getSessionConnectionsPerDayOfWeek(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception;

	EntityResult getSessionUnconnectedUsers(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception;

	EntityResult getSessionConnectedVsUnconnectedUsers(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception;

	EntityResult getFilesUploadsByCompany(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception;

	EntityResult getFilesUploadsByType(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception;

	EntityResult getFilesUploads(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception;

	EntityResult getFilesUploadsRegistersBySource(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception;

	EntityResult getUsageToolsBySession(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception;

	EntityResult getTaskEvolution(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception;

	EntityResult getTaskCreatedPerCompany(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception;
	
	
	EntityResult getDriversVehiclesWithoutDownload(BasicExpression queryFilter, GroupingTime groupingTime, int sessionId) throws Exception;
	

}
