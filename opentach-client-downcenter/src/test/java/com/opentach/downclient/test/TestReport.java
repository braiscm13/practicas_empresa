package com.opentach.downclient.test;

import java.util.Hashtable;

import com.ontimize.gui.preferences.BasicApplicationPreferences;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.opentach.common.downcenterreport.AbstractReportDto;
import com.opentach.common.downcenterreport.IDownCenterReportService;
import com.opentach.common.downcenterreport.IDownCenterReportService.DownCenterReportType;
import com.opentach.downclient.DownCenterClientApplication;
import com.opentach.downclient.DownCenterClientLocator;

public final class TestReport {

	private TestReport() {
		super();
	}

	public static void main(String[] args) {
		try {
			// System.setProperty("opentach.proxy.url", "http://localhost:8080");
			// new UploadFileStrategyRestClient().upload(-1, "mificherito.dat", cv, fileData, progressNotifier);
			String user = "ROSA";
			String pass = "rosa";

			DownCenterClientApplication ap = new DownCenterClientApplication(new Hashtable<>()) {
				@Override
				public com.ontimize.gui.preferences.ApplicationPreferences loadApplicationPreferences() {
					return new BasicApplicationPreferences("dummy");
				}

				@Override
				public void setInitialState() {
					this.preferenceFile = "";
					super.setInitialState();
				}

				@Override
				public boolean login() {
					return true;
				}
			};

			Hashtable<String, String> locatorParams = new Hashtable<>();
			locatorParams.put("remotelocatorname", "OpentachServer");
			locatorParams.put("local", "false");
			locatorParams.put("class", "com.opentach.client.OpentachClientLocator");
			locatorParams.put("packageorhostname", "localhost");
			locatorParams.put("port", "34007");
			locatorParams.put("clientpermissioncolumn", "XML_CLIENTE");
			locatorParams.put("checkservermessageperiod", "40000");
			locatorParams.put("checkinternalnoticesperiod", "100000");
			locatorParams.put("checktime", "60000");
			DownCenterClientLocator locator = new DownCenterClientLocator(locatorParams);
			ReflectionTools.setFieldValue(ap, "currentPassword", pass);
			ap.setReferencesLocator(locator);
			ap.setInitialState();
			int sessionId = locator.startSession(user, pass, null);
			try {
				// AbstractReportDto reportData = locator.getRemoteService(IDownCenterReportService.class).queryReportData(114667, false, sessionId);
				AbstractReportDto reportData = locator.getRemoteService(IDownCenterReportService.class).queryReportData(DownCenterReportType.TCType, "114668", false, sessionId);
				System.out.println(reportData);

			} finally {
				// locator.endSession(sessionId);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}
}
