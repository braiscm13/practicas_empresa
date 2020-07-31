package com.opentach.server.maintenance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.common.maintenance.MaintenanceStatus.MaintenanceStatusType;
import com.opentach.common.util.concurrent.PoolExecutors;
import com.opentach.server.OpentachServerLocator;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class MaintenanceTaskKmDriver extends AbstractMaintenanceTask {
	private static final Logger	logger	= LoggerFactory.getLogger(MaintenanceTaskKmDriver.class);
	private int					totalPairs;
	private final AtomicInteger	count	= new AtomicInteger(0);

	@Override
	protected MaintenanceStatusType getTaskKey() {
		return MaintenanceStatusType.DELETE_REG_KM_DRIVER;
	}

	@Override
	protected void doInnerMaintenance(String backupFolder, final Timestamp filterDate) throws Exception {
		this.totalPairs = 0;
		final ExecutorService executor = PoolExecutors.newFixedThreadPool("MaintenanceTaskKmDriver", 5);
		new OntimizeConnectionTemplate<Void>() {
			@Override
			protected Void doTask(Connection con) throws UException, SQLException {
				return new QueryJdbcTemplate<Void>() {
					@Override
					protected Void parseResponse(ResultSet rs) throws UException {
						try {
							while (rs.next()) {
								MaintenanceTaskKmDriver.this.totalPairs++;
								executor.submit(new DeleteTask(MaintenanceTaskKmDriver.this.count, "delete from cdregkm_conductor where NUMREQ=? AND IDCONDUCTOR=? AND fecha < ?",
										rs.getObject("CG_CONTRATO"), rs.getObject("IDCONDUCTOR"), filterDate));
							}
							return null;
						} catch (Exception ex) {
							throw new UException(ex);
						}
					}
				}.execute(con, "select CG_CONTRATO,IDCONDUCTOR from cdconductor_cont");
			}
		}.execute(OpentachServerLocator.getLocator(), true);
		executor.shutdown();
		executor.awaitTermination(4, TimeUnit.HOURS);
		this.updateStatus("maintenance.deleted", 100, false);
	}

	@Override
	public void reset() {
		super.reset();
		this.count.set(0);
		this.totalPairs = 1;
	}

	@Override
	protected float getCurrentPercent() {
		return super.getCurrentPercent() == 100f ? 100f : (((float) this.count.get() / this.totalPairs) * 100);
	}

}
