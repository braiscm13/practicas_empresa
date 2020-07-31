import java.util.Vector;

import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.util.rmi.ConnectionBean;
import com.opentach.common.company.naming.CompanyNaming;

public class LoadTest {

	public LoadTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// System.setProperty("com.ontimize.util.rmitunneling.httpsession", "true");
		for (int i = 0; i < 1; i++) {
			Thread th = new Thread("th-"+i) {
				@Override
				public void run() {
					int sessionId = -1;
					ConnectionBean conBean = null;
					try {
						conBean = new ConnectionBean("connectionbean.properties");
						sessionId = conBean.getSessionId();
						while (true) {
							conBean.getEntityReference(CompanyNaming.ENTITY).query(EntityResultTools.keysvalues("NAME", "bla"), new Vector(), sessionId);
							System.out.println("bla");
							Thread.sleep(10);
						}
						//						IDownCenterReportService service = (IDownCenterReportService) ((UtilReferenceLocator) conBean.getReferenceLocator()).getRemoteReference("DownCenterReportService",
						//								sessionId);
						//						service.
						//						AbstractReportDto queryReportData = service.queryReportData(101835, DownCenterReportType.TCType, true, sessionId);
						//						TestProcessPriorityQueue.logger.error("Thread ends with {}", queryReportData);
						//						System.out.println(sessionId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (sessionId > 0) {
							conBean.endSession(sessionId);
						}
					}

				}
			};
			th.start();
			try {
				Thread.sleep(125);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
