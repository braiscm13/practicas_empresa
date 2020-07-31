package com.opentach.client.labor.modules.report;

import java.awt.Desktop;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.utilmize.client.gui.field.table.IDetailFormOpener;
import com.utilmize.client.gui.field.table.UTable;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMInformeLaboralGeneradoDetailFormOpener implements IDetailFormOpener {

	private static final Logger	logger	= LoggerFactory.getLogger(IMInformeLaboralGeneradoDetailFormOpener.class);

	public IMInformeLaboralGeneradoDetailFormOpener(Hashtable parameters) {
		super();
	}

	@Override
	public boolean openDetailForm(UTable table, int row) {
		new USwingWorker<Path, Void>() {

			@Override
			protected Path doInBackground() throws Exception {
				Hashtable rowData = table.getRowData(row);
				Object repId = rowData.get("REP_ID");
				EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
				EntityResult er = locator.getEntityReference("ELaborReportWarehouse").query(EntityResultTools.keysvalues("REP_ID", repId),
						EntityResultTools.attributes("REP_CONTENT"),
						locator.getSessionId());
				CheckingTools.checkValidEntityResult(er, null, true, true, new Object[] {});
				Path tempFile = Files.createTempFile("laborreport", ".pdf");
				OutputStream os = Files.newOutputStream(tempFile);
				os.write(((BytesBlock) er.getRecordValues(0).get("REP_CONTENT")).getBytes());
				os.close();
				return tempFile;
			}

			@Override
			protected void done() {
				try {
					Desktop.getDesktop().open(this.uget().toFile());
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, IMInformeLaboralGeneradoDetailFormOpener.logger);
				}
			}

		}.executeOperation(table);
		return true;
	}

	@Override
	public boolean openInsertForm(UTable table) {
		return false;
	}

}
