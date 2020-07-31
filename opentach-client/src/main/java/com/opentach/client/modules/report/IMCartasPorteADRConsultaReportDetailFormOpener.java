package com.opentach.client.modules.report;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.waybill.IWaybillService;
import com.opentach.common.waybill.WaybillType;
import com.utilmize.client.gui.field.table.IDetailFormOpener;
import com.utilmize.client.gui.field.table.UTable;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMCartasPorteADRConsultaReportDetailFormOpener implements IDetailFormOpener {

	private static final Logger	logger	= LoggerFactory.getLogger(IMCartasPorteADRConsultaReportDetailFormOpener.class);


	public IMCartasPorteADRConsultaReportDetailFormOpener(Hashtable params) throws Exception {
		super();
		this.init(params);
	}

	protected void init(Map<?, ?> params) throws Exception {
	}


	@Override
	public boolean openDetailForm(UTable table, int row) {
		this.openReport(table, row);
		return true;
	}

	@Override
	public boolean openInsertForm(UTable table) {
		return false;
	}

	private void openReport(UTable table, int row) {
		new USwingWorker<File, Void>() {

			@Override
			protected File doInBackground() throws Exception {
				Hashtable rowData = table.getRowData(row);
				Object wayId = rowData.get("WAY_ID");
				String cif = (String) rowData.get("CIF");
				OpentachClientLocator locator = (OpentachClientLocator) ApplicationManager.getApplication().getReferenceLocator();
				WaybillType a;
				switch ((String)rowData.get("WAY_TYPE")) {
				case "CARTA_PORTE":
					a = WaybillType.CARTA_PORTE;
					break;
					case "ADR_GENERAL_1":
						a = WaybillType.ADR_GENERAL_1;
						break;
					case "ADR_GENERAL_2":
						a = WaybillType.ADR_GENERAL_2;
						break;
					case "ADR_BULTOS":
						a = WaybillType.ADR_BULTOS;
						break;
					default:
						throw new Exception("E_INVALID_TYPE");
				} 
				
				BytesBlock bb = locator.getRemoteService(IWaybillService.class).generateWaybill(a,rowData,cif, ApplicationManager.getLocale(),  locator.getSessionId());
				File file = File.createTempFile("InformeAdr", ".pdf");
				FileTools.copyFile(new ByteArrayInputStream(bb.getBytes()), file);
				return file;
			}

			@Override
			protected void done() {
				try {
					File file = this.uget();
					Desktop.getDesktop().open(file);
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(new Exception("M_PROCESO_INCORRECTO_GENERANDO_INFORME", error),
							IMCartasPorteADRConsultaReportDetailFormOpener.logger);
				}
			}
		}.executeOperation(table);
	}


}
