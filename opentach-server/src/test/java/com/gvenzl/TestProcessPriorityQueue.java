package com.gvenzl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.locator.UtilReferenceLocator;
import com.ontimize.util.rmi.ConnectionBean;
import com.opentach.common.downcenterreport.AbstractReportDto;
import com.opentach.common.downcenterreport.IDownCenterReportService;
import com.opentach.common.downcenterreport.IDownCenterReportService.DownCenterReportType;

public class TestProcessPriorityQueue {

	private static final Logger logger = LoggerFactory.getLogger(TestProcessPriorityQueue.class);

	public TestProcessPriorityQueue() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		int sessionId = -1;
		ConnectionBean conBean = null;
		try {
			conBean = new ConnectionBean("connectionbean.properties");
			sessionId = conBean.getSessionId();
			IDownCenterReportService service = (IDownCenterReportService) ((UtilReferenceLocator) conBean.getReferenceLocator()).getRemoteReference("DownCenterReportService",
					sessionId);
			AbstractReportDto queryReportData = service.queryReportData(DownCenterReportType.TCType, "E77578448T0000", true, sessionId);
			TestProcessPriorityQueue.logger.error("Thread ends with {}", queryReportData);
			System.out.println(sessionId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (sessionId > 0) {
				conBean.endSession(sessionId);
			}
		}

	}

}
