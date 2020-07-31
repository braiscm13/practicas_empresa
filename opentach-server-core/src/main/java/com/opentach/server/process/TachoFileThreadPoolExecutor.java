package com.opentach.server.process;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.jee.common.tools.Template;
import com.opentach.common.process.FileProcessServiceSlotStatus;
import com.opentach.common.process.FileProcessServiceStatus;
import com.opentach.common.util.concurrent.PriorityThreadPoolExecutor;
import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class TachoFileThreadPoolExecutor extends PriorityThreadPoolExecutor implements RejectedExecutionHandler {

	private static final Logger					logger							= LoggerFactory.getLogger(TachoFileThreadPoolExecutor.class);

	public static final int						PRIORITY_NONE					= 1000;
	public static final int						PRIORITY_NORMAL					= 500;
	public static final int						PRIORITY_MOBILE					= 100;
	public static final int						PRIORITY_PROCESS_WITH_PRIORITY	= 50;
	public static final int						PRIORITY_PROCESS_NOW			= 1;

	private final PopulatorTask					populatorTask;
	private final AtomicInteger					workerCount						= new AtomicInteger(0);
	private final AtomicLong					processedCount					= new AtomicLong();
	private final AtomicLong					okCount							= new AtomicLong();
	private final AtomicLong					koCount							= new AtomicLong();

	private final Set<String>					idsProcessing					= Collections.synchronizedSet(new HashSet<String>());
	private final List<RunnableFuture<?>>		priorityAnalysis				= new ArrayList<>();
	private boolean								enabled;
	private final IOpentachServerLocator		locator;
	private final Set<FileProcessTask>			processingList;

	private final ErrorLogTableManagementThread	errorLogTableThread;

	public TachoFileThreadPoolExecutor(IOpentachServerLocator locator) {
		super("tgdprocess", 50, 30, TimeUnit.SECONDS);
		this.errorLogTableThread = new ErrorLogTableManagementThread(locator);
		this.processingList = Collections.synchronizedSet(new HashSet<FileProcessTask>());
		this.enabled = true;
		this.locator = locator;
		this.populatorTask = new PopulatorTask();
		this.setRejectedExecutionHandler(this);
		new Thread(this.populatorTask, "Process populator task").start();
		this.errorLogTableThread.start();
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		FileProcessTask processTask = this.extractFileProcessTask(r);
		processTask.setStartTime(System.currentTimeMillis());
		this.processingList.add(processTask);
		this.workerCount.incrementAndGet();
		this.processedCount.incrementAndGet();
		super.beforeExecute(t, r);
	}

	@Override
	protected void afterExecute(Runnable command, Throwable t) {
		FileProcessTask task = this.extractFileProcessTask(command);
		if (task.hasError()) {
			this.koCount.incrementAndGet();
		} else {
			this.okCount.incrementAndGet();
		}
		task.setEndTime(System.currentTimeMillis());
		this.processingList.remove(task);
		synchronized (this.idsProcessing) {
			this.idsProcessing.remove(task.getFileInfo().getIdSource());

		}
		int decrementAndGet = this.workerCount.decrementAndGet();
		if (decrementAndGet == 0) {
			synchronized (this.populatorTask) {
				this.populatorTask.notify();
			}
		}
		super.afterExecute(command, t);
	}

	protected boolean hasPendingTasks() {
		return this.workerCount.get() > 0;
	}

	@Override
	public void execute(Runnable command) {
		FileProcessTask task = this.extractFileProcessTask(command);
		synchronized (this.idsProcessing) {
			if (!this.idsProcessing.contains(task.getFileInfo().getIdSource())) {
				this.idsProcessing.add(task.getFileInfo().getIdSource());
			} else {
				ReflectionTools.invoke(this, "reject", command);
				return;
			}
		}
		super.execute(command);
	}

	private FileProcessTask extractFileProcessTask(Runnable command) {
		FileProcessTask task = null;
		if (command instanceof FileProcessTask) {
			task = (FileProcessTask) command;
		} else {
			task = (FileProcessTask) ((PriorityTask<?>) command).getWrappedObject();
		}
		return task;
	}

	@Override
	public void rejectedExecution(Runnable command, ThreadPoolExecutor executor) {
		FileProcessTask task = this.extractFileProcessTask(command);
		if (task.getPriority() <= TachoFileThreadPoolExecutor.PRIORITY_PROCESS_WITH_PRIORITY) {
			synchronized (this.priorityAnalysis) {
				this.priorityAnalysis.add((RunnableFuture<?>) command);
			}
		}
	}

	public void processWithPriority(Object idFile, int priority) throws Exception {
		FileProcessTask processTask = new FileProcessTask(this.queryFileInfo((Number) idFile, priority), this.errorLogTableThread, this.locator);
		this.submit(processTask);
	}

	public Future<?> processWithPriorityFuture(Number idFile, int priority) throws Exception {
		FileProcessTask processTask = new FileProcessTask(this.queryFileInfo(idFile, priority), this.errorLogTableThread, this.locator);
		return this.submit(processTask);
	}

	/**
	 * Obtiene los datos del fichero en caso de solicitarlos en un process now o en un process priority
	 *
	 * @param idFile
	 * @param conn
	 * @param overwrite
	 * @return
	 * @throws Exception
	 */
	protected FileInfo queryFileInfo(final Number idFile, final int priority) throws Exception {
		// (overwrite ? "" : " AND CDFICHEROS_CONTRATO.F_PROCESADO IS NULL"));
		return new OntimizeConnectionTemplate<FileInfo>() {

			@Override
			protected FileInfo doTask(Connection con) throws UException, SQLException {
				String sql = new Template("sql/process/getFileData.sql").fillTemplate("%OVERWRITE%", "");
				return new QueryJdbcTemplate<FileInfo>() {
					@Override
					protected FileInfo parseResponse(ResultSet rset) throws UException {
						try {
							FileInfo fInfo = null;
							while (rset.next()) {
								if (fInfo == null) {
									fInfo = TachoFileThreadPoolExecutor.this.rs2FileInfo(rset, priority);
								} else {
									FileInfo fTmp = TachoFileThreadPoolExecutor.this.rs2FileInfo(rset, priority);
									fInfo.getContracts().addAll(fTmp.getContracts());
								}
							}
							return fInfo;
						} catch (Exception ex) {
							throw new UException(ex);
						}
					}
				}.execute(con, sql, idFile.intValue(), idFile.intValue());
			}
		}.execute(this.locator.getConnectionManager(), true);
	}

	protected FileInfo rs2FileInfo(ResultSet rset, int forcedPriority) throws SQLException {
		Number idFile = rset.getBigDecimal(1);
		String mobile = rset.getString(10);

		int priority = "S".equals(mobile) ? TachoFileThreadPoolExecutor.PRIORITY_MOBILE : TachoFileThreadPoolExecutor.PRIORITY_NORMAL;
		if (forcedPriority < priority) {
			priority = forcedPriority;
		}
		HashSet<String> sContracts = new HashSet<String>(1);
		sContracts.add(rset.getString(2));
		FileInfo fInfo = new FileInfo(idFile, rset.getString(7), sContracts, priority);
		fInfo.setEmail(rset.getString(3));
		String sAnalizar = rset.getString(4);
		fInfo.setAnalizar((sAnalizar != null) && sAnalizar.equalsIgnoreCase("S"));
		fInfo.setIdSource(rset.getString(5));
		fInfo.setDExtract(rset.getTimestamp(6));
		fInfo.setNombProcesado(rset.getString(8));
		fInfo.setNotifUrl(rset.getString(9));
		fInfo.setMovil("S".equals(mobile));
		fInfo.setAlreadyProcessed("S".equals(rset.getString(11)));
		return fInfo;

	}

	class PopulatorTask implements Runnable {

		@Override
		public void run() {
			while (!TachoFileThreadPoolExecutor.this.isShutdown()) {
				try {
					if (!TachoFileThreadPoolExecutor.this.hasPendingTasks() && TachoFileThreadPoolExecutor.this.isEnabled()) {
						// Incluimos las mas prioritarias
						List<RunnableFuture<?>> priorityTmp = new ArrayList<>();
						synchronized (TachoFileThreadPoolExecutor.this.priorityAnalysis) {
							priorityTmp.addAll(TachoFileThreadPoolExecutor.this.priorityAnalysis);
							TachoFileThreadPoolExecutor.this.priorityAnalysis.clear();
						}
						this.enqueuePriorityTasks(priorityTmp);

						if (TachoFileThreadPoolExecutor.this.getQueue().size() > 150) {
							System.err.println("Elements in queue " + TachoFileThreadPoolExecutor.this.getQueue().size());
						}

						// Luego el resto
						List<FileInfo> toProcess = this.queryFilesToProcess();
						if (!toProcess.isEmpty()) {
							this.enqueueTasks(toProcess);
						}
					}
					synchronized (this) {
						this.wait(5000);
					}

				} catch (Throwable error) {
					TachoFileThreadPoolExecutor.logger.error(null, error);
				}
			}
		}

		private void enqueuePriorityTasks(List<RunnableFuture<?>> queueToProcess) {
			for (RunnableFuture<?> task : queueToProcess) {
				TachoFileThreadPoolExecutor.this.execute(task);
			}
		}

		private void enqueueTasks(List<FileInfo> queueToProcess) {
			for (FileInfo fInfo : queueToProcess) {
				TachoFileThreadPoolExecutor.this.submit(new FileProcessTask(fInfo, TachoFileThreadPoolExecutor.this.errorLogTableThread, TachoFileThreadPoolExecutor.this.locator));
			}
		}

		private List<FileInfo> queryFilesToProcess() throws Exception {
			return new OntimizeConnectionTemplate<List<FileInfo>>() {
				@Override
				protected List<FileInfo> doTask(Connection con) throws UException {
					try {
						return new QueryJdbcTemplate<List<FileInfo>>() {
							@Override
							protected List<FileInfo> parseResponse(ResultSet rset) throws UException {
								try {
									List<FileInfo> fInfoList = new ArrayList<FileInfo>();
									FileInfo lastInfo = null;
									while (rset.next()) {
										FileInfo fInfo = TachoFileThreadPoolExecutor.this.rs2FileInfo(rset, TachoFileThreadPoolExecutor.PRIORITY_NONE);
										if ((lastInfo != null) && lastInfo.getIdFile().equals(fInfo.getIdFile())) {
											lastInfo.getContracts().add(rset.getString(2));
										} else {
											fInfoList.add(fInfo);
											lastInfo = fInfo;
										}
									}
									return fInfoList;
								} catch (Exception ex) {
									throw new UException(ex);
								}
							}
						}.execute(con, new Template("sql/process/queryFiles.sql").getTemplate());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(TachoFileThreadPoolExecutor.this.locator.getConnectionManager(), true);

		}
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public FileProcessServiceStatus getStatus() {
		FileProcessServiceStatus status = new FileProcessServiceStatus();
		status.setUsedSlots(this.getActiveCount());
		status.setTotalSlots(this.getCorePoolSize());
		status.setOkCount(this.okCount.get());
		status.setKoCount(this.koCount.get());
		status.setProcessedFiles(this.processedCount.get());
		for (FileProcessTask task : this.processingList) {
			FileProcessServiceSlotStatus slotStatus = new FileProcessServiceSlotStatus();
			slotStatus.setContracts(task.getFileInfo().getContracts());
			slotStatus.setFileType(task.getFileInfo().getFileType());
			slotStatus.setIdFile(task.getFileInfo().getIdFile());
			slotStatus.setIdSource(task.getFileInfo().getIdSource());
			slotStatus.setName(task.getFileInfo().getNombProcesado());
			slotStatus.setStartTime(task.getStartTime());
			slotStatus.setDuration(System.currentTimeMillis() - task.getStartTime());
			status.addSlotStatus(slotStatus);
		}
		return status;
	}
}
