package com.opentach.adminclient.modules.files;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.DataLabel;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.process.FileProcessServiceSlotStatus;
import com.opentach.common.process.FileProcessServiceStatus;
import com.opentach.common.process.ITachoFileProcessService;
import com.utilmize.client.fim.UBasicFIM;

public class IMProcessFileStatus extends UBasicFIM {

	private static final Logger	logger	= LoggerFactory.getLogger(IMProcessFileStatus.class);

	private static final long	PERIOD	= 5000;

	@FormComponent(attr = "a")
	private Table				table;
	@FormComponent(attr = "total")
	private DataLabel			totalLabel;
	@FormComponent(attr = "used")
	private DataLabel			usedLabel;
	@FormComponent(attr = "lastUpdate")
	private DataLabel			lastUpdateLabel;
	@FormComponent(attr = "okCount")
	private DataLabel			okCountLabel;
	@FormComponent(attr = "koCount")
	private DataLabel			koCountLabel;
	@FormComponent(attr = "processedCount")
	private DataLabel			processedLabel;
	private Timer				timer;

	@Override
	public void registerInteractionManager(Form formulario, IFormManager gestorForms) {
		super.registerInteractionManager(formulario, gestorForms);
		this.timer = new Timer();
		this.timer.schedule(new RefreshTask(), 0, IMProcessFileStatus.PERIOD);
		this.setFormValues(null, new EntityResult());
	}

	protected void setFormValues(FileProcessServiceStatus status, EntityResult er) {
		IMProcessFileStatus.this.table.setValue(er);
		IMProcessFileStatus.this.processedLabel.setValue(this.translate("PROCESSED_COUNT", status == null ? "-" : status.getProcessedFiles()));
		IMProcessFileStatus.this.totalLabel.setValue(this.translate("TOTAL_SLOTS", status == null ? "-" : status.getTotalSlots()));
		IMProcessFileStatus.this.usedLabel.setValue(this.translate("USED_SLOTS", status == null ? "-" : status.getUsedSlots()));
		IMProcessFileStatus.this.okCountLabel.setValue(this.translate("OK_COUNT", status == null ? "-" : status.getOkCount()));
		IMProcessFileStatus.this.koCountLabel.setValue(this.translate("KO_COUNT", status == null ? "-" : status.getKoCount()));
		IMProcessFileStatus.this.lastUpdateLabel.setValue(this.translate("LAST_UPDATE",
				status == null ? null : new SimpleDateFormat("dd/MM HH:mm:ss").format(new Date())));
	}

	private Object translate(String label, Object value) {
		String translation = ApplicationManager.getTranslation(label, this.getResourceBundle());
		StringBuilder sb = new StringBuilder();
		sb.append(translation);
		sb.append(": ");
		sb.append(value);
		return sb.toString();
	}

	private class RefreshTask extends TimerTask {

		public RefreshTask() {
			super();
		}

		@Override
		public void run() {
			if ((IMProcessFileStatus.this.managedForm.getJDialog() != null) && IMProcessFileStatus.this.managedForm.getJDialog().isVisible()) {
				try {
					FileProcessServiceStatus status = ((OpentachClientLocator) IMProcessFileStatus.this.getReferenceLocator()).getRemoteService(
							ITachoFileProcessService.class).getStatus(IMProcessFileStatus.this.getSessionId());
					final EntityResult er = this.slotsToEntityResult(status);
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							IMProcessFileStatus.this.setFormValues(status, er);
						}

					});
				} catch (Exception e) {
					IMProcessFileStatus.logger.error(null, e);
				}
			}
		}

		private EntityResult slotsToEntityResult(FileProcessServiceStatus status) {
			EntityResult er = new EntityResult();
			EntityResultTools.initEntityResult(er, IMProcessFileStatus.this.table.getAttributeList());
			List<String> av = IMProcessFileStatus.this.table.getAttributeList();
			int nregs = status.getSlotStatus().size();
			for (String col : av) {
				Vector value = new Vector(nregs);
				value.setSize(nregs);
				er.put(col, value);
			}

			List<FileProcessServiceSlotStatus> slotStatus = status.getSlotStatus();
			int i = 0;
			for (FileProcessServiceSlotStatus slot : slotStatus) {
				this.putRecordInfo(i, er, "IDSOURCE", slot.getIdSource());
				this.putRecordInfo(i, er, "BEGINDATE", new Date(slot.getStartTime()));
				this.putRecordInfo(i, er, "DURATION", slot.getDuration() / 1000);
				this.putRecordInfo(i, er, "NUMCONTRACTS", slot.getContracts().size());
				this.putRecordInfo(i, er, "FILETYPE", slot.getFileType());
				i++;
			}

			return er;
		}

		private void putRecordInfo(int index, EntityResult er, String column, Object value) {
			if (value != null) {
				((Vector) er.get(column)).set(index, value);
			}
		}
	}
}
