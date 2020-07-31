import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.util.rmi.ConnectionBean;
import com.utilmize.tools.sqleditor.IMultiConnectionSqlExecutor;

public class PurgeCDPERIODOS_TRABAJO {

	private static final Logger	logger		= LoggerFactory.getLogger(PurgeCDPERIODOS_TRABAJO.class);
	private static final String	NUM_DAYS	= "900";

	public PurgeCDPERIODOS_TRABAJO() {
		// TODO Auto-generated constructor stub
	}

	private static LinkedBlockingQueue<Pair<Object, Object>>	queueIds	= new LinkedBlockingQueue<>();

	public static void main(String[] args) {
		int sessionId = -1;
		ConnectionBean conBean = null;
		AtomicInteger count = new AtomicInteger(0);
		try {
			conBean = new ConnectionBean("connectionbean.properties");
			sessionId = conBean.getSessionId();
			IMultiConnectionSqlExecutor sqlEntity = (IMultiConnectionSqlExecutor) conBean.getEntityReference("com.utilmize.server.tools.sqleditor.ESQLEntity");
			long startTime = System.currentTimeMillis();

			EntityResult res = sqlEntity.execute(null, "select CG_CONTRATO,IDCONDUCTOR from cdconductor_cont", sessionId);
			System.out.println("Query time: " + (System.currentTimeMillis() - startTime));
			if (res != null) {
				int nregs = res.calculateRecordNumber();
				for (int i = 0; i < nregs; i++) {
					List<? extends Object> listNumreq = (List<? extends Object>) res.get("CG_CONTRATO");
					List<? extends Object> listIdconductor = (List<? extends Object>) res.get("IDCONDUCTOR");
					PurgeCDPERIODOS_TRABAJO.queueIds.add(new Pair(listNumreq.get(i), listIdconductor.get(i)));
				}
			}

			List<Thread> purgeThreads = new ArrayList<Thread>();
			for (int endNumber = 0; endNumber < 15; endNumber++) {
				Thread th = new Thread(new PurgeCdperiodosTrabajoTask(sqlEntity, sessionId, startTime, count), "purge-" + endNumber);
				purgeThreads.add(th);
				th.start();
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
		System.out.println("FINISHED");

	}

	private static class PurgeCdperiodosTrabajoTask implements Runnable {

		private final int							sessionId;
		private final long							startTime;
		private final IMultiConnectionSqlExecutor	sqlEntity;
		private final AtomicInteger					count;

		public PurgeCdperiodosTrabajoTask(IMultiConnectionSqlExecutor sqlEntity, int sessionId, long startTime, AtomicInteger count) {
			this.sessionId = sessionId;
			this.startTime = startTime;
			this.sqlEntity = sqlEntity;
			this.count = count;
		}

		@Override
		public void run() {
			while (true) {
				try {
					Pair<Object, Object> info = PurgeCDPERIODOS_TRABAJO.queueIds.poll();
					if (info == null) {
						System.out.println(Thread.currentThread().getName() + " exits");
						return;
					}
					Object numreq = info.getFirst();
					Object idconductor = info.getSecond();
					long time = System.currentTimeMillis();
					EntityResult response = this.sqlEntity
							.execute(
									null,
									"delete from TACHBB102011.CDPERIODOS_TRABAJO where NUMREQ='" + numreq + "' AND IDCONDUCTOR='" + idconductor + "' AND FECINI < (sysdate - " + PurgeCDPERIODOS_TRABAJO.NUM_DAYS + ")",
									this.sessionId);
					int num = this.count.incrementAndGet();
					System.out
					.println(Thread.currentThread().getName() + ":" + numreq + "_" + idconductor + "(" + num + "," + (((System.currentTimeMillis() - time))) + "):" + response
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
