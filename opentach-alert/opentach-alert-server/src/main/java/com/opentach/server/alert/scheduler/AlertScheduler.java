package com.opentach.server.alert.scheduler;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.server.spring.SpringTools;
import com.opentach.common.alert.naming.AlertNaming;
import com.opentach.common.alert.services.IAlertService;
import com.opentach.server.alert.service.IAlertServiceServer;

@EnableScheduling
@Component
public class AlertScheduler {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(AlertScheduler.class);

	@Autowired
	protected IAlertService		alertService;

	@Autowired
	protected IAlertServiceServer	alertServiceServer;

	@Scheduled(cron = "0 0/5 * * * *")
	public void checkTasks() {
		AlertScheduler.logger.info("checkAlerts...");
		Date dispatchDate = new Date();
		try {
			EntityResult alertQuery = this.getAlertService().alertSchedulerQuery(EntityResultTools.keysvalues(), EntityResultTools.attributes(AlertNaming.ALR_ID));
			AlertScheduler.logger.info("\tThere are {} alerts defined.", alertQuery.calculateRecordNumber());
			List<Object> alrIds = (List<Object>) alertQuery.get(AlertNaming.ALR_ID);
			if (alrIds != null) {
				for (Object alrId : alrIds) {
					this.executeSingleAlert(alrId, dispatchDate);
				}
			}
			AlertScheduler.logger.info("checkAlerts...FINISHED");
		} catch (Exception err) {
			AlertScheduler.logger.error("checkAlerts...ERROR", err);
		}
	}

	private void executeSingleAlert(Object alrId, Date dispatchDate) {
		try {
			AlertScheduler.logger.debug("executeSingleAlert: {}", alrId);
			this.getAlertServiceServer().executeAlertAsynchOnDate(alrId, false, dispatchDate, null);
			AlertScheduler.logger.debug("executeSingleAlert: {}  QUEUED", alrId);
		} catch (Exception err) {
			AlertScheduler.logger.error("executeSingleAlert: {}  ERROR", alrId, err);
		}
	}

	private IAlertService getAlertService() {
		return SpringTools.getTargetObject(this.alertService, IAlertService.class);
	}

	private IAlertServiceServer getAlertServiceServer() {
		return SpringTools.getTargetObject(this.alertServiceServer, IAlertServiceServer.class);
	}

}
