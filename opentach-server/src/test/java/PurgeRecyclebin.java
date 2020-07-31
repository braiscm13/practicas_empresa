import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.util.rmi.ConnectionBean;
import com.utilmize.tools.sqleditor.IMultiConnectionSqlExecutor;

public class PurgeRecyclebin {

	private static final Logger	logger	= LoggerFactory.getLogger(PurgeRecyclebin.class);

	public PurgeRecyclebin() {
		// TODO Auto-generated constructor stub
	}

	private static LinkedBlockingQueue<Object>	queueIds	= new LinkedBlockingQueue<>();

	public static void main(String[] args) {
		int sessionId = -1;
		ConnectionBean conBean = null;
		AtomicInteger count = new AtomicInteger(0);
		try {
			conBean = new ConnectionBean("connectionbean.properties");
			sessionId = conBean.getSessionId();
			IMultiConnectionSqlExecutor sqlEntity = (IMultiConnectionSqlExecutor) conBean.getEntityReference("com.utilmize.server.tools.sqleditor.ESQLEntity");
			long startTime = System.currentTimeMillis();
			List<Thread> purgeThreads = new ArrayList<Thread>();
			DecimalFormat df = new DecimalFormat("00");
			for (int endNumber = 0; endNumber < 70; endNumber++) {
				Thread th = new Thread(new PurgeRunnable(sqlEntity, conBean, sessionId, df.format(endNumber), startTime, count), "purge-" + endNumber);
				purgeThreads.add(th);
				// Thread.sleep(200);
				th.start();
			}

			int readed = 1;
			int i = 0;
			while (df != null) {
				if (PurgeRecyclebin.queueIds.size() < 300) {
					EntityResult res = sqlEntity.execute(null, "SELECT ORIGINAL_NAME FROM RECYCLEBIN where rownum < 50 and original_name like '%" + df.format(i) + "'", sessionId);
					i = (i + 1) % 100;
					List<String> resList = ((res == null) || (res.calculateRecordNumber() == 0)) ? Collections.EMPTY_LIST : (List) res.get("ORIGINAL_NAME");
					readed = resList.size();
					if (readed > 0) {
						for (String name : resList) {
							if (!PurgeRecyclebin.queueIds.contains(name)) {
								PurgeRecyclebin.queueIds.add(name);
							}
						}
					}
				} else {
					Thread.sleep(500);
				}
			}

			for (Thread th : purgeThreads) {
				th.join();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (sessionId > 0) {
				conBean.endSession(sessionId);
			}
		}

	}

	private static class PurgeRunnable implements Runnable {

		private final ConnectionBean				conBean;
		private final int							sessionId;
		private final String						endNumber;
		private final long							startTime;
		private final IMultiConnectionSqlExecutor	sqlEntity;
		private final AtomicInteger					count;

		public PurgeRunnable(IMultiConnectionSqlExecutor sqlEntity, ConnectionBean conBean, int sessionId, String endNumber, long startTime, AtomicInteger count) {
			this.conBean = conBean;
			this.sessionId = sessionId;
			this.endNumber = endNumber;
			this.startTime = startTime;
			this.sqlEntity = sqlEntity;
			this.count = count;
		}

		@Override
		public void run() {
			while (true) {
				try {
					// EntityResult res = this.sqlEntity.execute(null, "SELECT ORIGINAL_NAME FROM RECYCLEBIN where rownum < 200 and original_name like '%" + this.endNumber + "'",
					// this.sessionId);
					// List<String> resList = (List) res.get("ORIGINAL_NAME");
					// for (String tableName : resList) {
					Object tableName = PurgeRecyclebin.queueIds.poll(30, TimeUnit.MINUTES);
					if (tableName == null) {
						return;
					}
					EntityResult response = this.sqlEntity.execute(null, "PURGE TABLE " + tableName, this.sessionId);
					int num = this.count.incrementAndGet();
					System.out
					.println(Thread.currentThread().getName() + ":" + tableName + "(" + num + "," + (((System.currentTimeMillis() - this.startTime) / num)) + "):" + response
							.get("RESULT"));
					// }
				} catch (Exception ex) {
					ex.printStackTrace();
					System.err.println(Thread.currentThread().getName());
					return;
				}
			}
		}

	}
}
