package com.opentach.server.maintenance;

import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.maintenance.IMaintenanceService;
import com.opentach.common.maintenance.MaintenanceStatus;
import com.opentach.common.maintenance.MaintenanceStatusTask;
import com.opentach.common.util.concurrent.PoolExecutors;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.PermissionsUtil;

/**
 * The Class TachoFileService.
 */
public class MaintenanceService extends UAbstractService implements IMaintenanceService {

	private static final String							MAINTENANCE_SERVIE	= "MaintenanceServie";

	private static final Logger							logger				= LoggerFactory.getLogger(MaintenanceService.class);

	private static boolean								running;
	private final MaintenanceTaskBackupDeleteTachoFiles	taskFiles			= new MaintenanceTaskBackupDeleteTachoFiles();
	private final List<IMaintenanceTask>				extraTaskList		= Arrays.asList(new IMaintenanceTask[] {			//
			new MaintenanceTaskManagementReports(),																				// Informes gestores
			new MaintenanceTaskVehicleUses(),																					// Usos de vehiculos
			new MaintenanceTaskActivities(),																					// actividades
			new MaintenanceTaskWorkingPeriods(),																				// Periodos trabajo
			new MaintenanceTaskFails(),																							// Fallos
			new MaintenanceTaskControls(),																						// Controles
			new MaintenanceTaskCalibrations(),																					// Calibrados
			new MaintenanceTaskCardUses(),																						// Usos de tarjeta
			new MaintenanceTaskKmDriver(),																						// km conductor
			new MaintenanceTaskKmVehicle(),																						// km vehiculo
			new MaintenanceTaskSpeed()																							// , // velocidad
			// Quieren mantener los certificados de actividades
			// new MaintenanceTaskCertifActiv() // certificados de actividades
	});

	private final Thread								execThread;
	private final LinkedBlockingQueue<ClearParameters>	queue;

	/**
	 * Instantiates a new tacho file service.
	 *
	 * @param port
	 *            the port
	 * @param erl
	 *            the erl
	 * @param hconfig
	 *            the hconfig
	 * @throws Exception
	 *             the exception
	 */
	public MaintenanceService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
		MaintenanceService.running = false;
		this.queue = new LinkedBlockingQueue<>(1);
		this.execThread = new Thread(new ClearTask(), "Clear task thread");
		this.execThread.setDaemon(true);
		this.execThread.start();
	}

	@Override
	public MaintenanceStatus getStatus(int sessionId) throws Exception {
		if (!PermissionsUtil.hasPermission(this.getLocator(), sessionId, MaintenanceService.MAINTENANCE_SERVIE, "do")) {
			throw new GeneralSecurityException("user has no permissons");
		}
		// if (!MaintenanceService.running) {
		// return null;
		// }
		MaintenanceStatus res = new MaintenanceStatus(!MaintenanceService.running);
		res.addStatus(this.taskFiles.getStatus());
		for (final IMaintenanceTask task : this.extraTaskList) {
			res.addStatus(task.getStatus());
		}
		return res;
	}

	@Override
	public void doClear(String backupFilesFolder, Timestamp filterDate, int sessionId) throws Exception {
		if (!PermissionsUtil.hasPermission(this.getLocator(), sessionId, MaintenanceService.MAINTENANCE_SERVIE, "do")) {
			throw new GeneralSecurityException("user has no permissons");
		}
		if (MaintenanceService.running) {
			throw new Exception("Alreadey running");
		}
		MaintenanceService.running = true;
		this.queue.add(new ClearParameters(backupFilesFolder, filterDate));
	}

	private static class ClearParameters {
		private final String	backupFilesFolder;
		private final Timestamp	filterDate;

		public ClearParameters(String backupFilesFolder, Timestamp filterDate) {
			super();
			this.backupFilesFolder = backupFilesFolder;
			this.filterDate = filterDate;
		}

		public String getBackupFilesFolder() {
			return this.backupFilesFolder;
		}

		public Timestamp getFilterDate() {
			return this.filterDate;
		}

	}

	private class ClearTask implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					ClearParameters parameters = MaintenanceService.this.queue.take();
					MaintenanceService.running = true;
					this.doResetStatus(MaintenanceService.this.extraTaskList);
					// Ficheros de tacografo
					MaintenanceService.this.taskFiles.doMaintenance(parameters.getBackupFilesFolder(), parameters.getFilterDate());
					if (!MaintenanceService.this.taskFiles.getStatus().isError()) {
						ExecutorService executor = PoolExecutors.newFixedThreadPool("MaintenanceService", 5);
						executor.invokeAll(this.makeCallable(MaintenanceService.this.extraTaskList, parameters));
						executor.shutdown();
						executor.awaitTermination(8, TimeUnit.HOURS);
					}
				} catch (Exception ex) {
					MaintenanceService.logger.error(null, ex);
				} finally {
					MaintenanceService.running = false;
				}
			}
		}

		private void doResetStatus(List<IMaintenanceTask> taskList) {
			MaintenanceService.this.taskFiles.reset();
			for (final IMaintenanceTask task : taskList) {
				task.reset();
			}
		}

		private Collection<Callable<MaintenanceStatusTask>> makeCallable(final List<IMaintenanceTask> taskList, final ClearParameters parameters) {
			List<Callable<MaintenanceStatusTask>> res = new ArrayList<Callable<MaintenanceStatusTask>>();
			for (final IMaintenanceTask task : taskList) {
				res.add(new Callable<MaintenanceStatusTask>() {

					@Override
					public MaintenanceStatusTask call() throws Exception {
						return task.doMaintenance(parameters.getBackupFilesFolder(), parameters.getFilterDate());
					}
				});
			}
			return res;
		}

	}
}
