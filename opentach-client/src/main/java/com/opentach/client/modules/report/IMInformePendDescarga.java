package com.opentach.client.modules.report;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.exception.OpentachException;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformePendDescarga extends IMReportRoot {

	private static final Logger logger = LoggerFactory.getLogger(IMInformePendDescarga.class);

	public IMInformePendDescarga() {
		super("EInformePendDescarga", "informe_pend_descarga");
		this.setDateTags(new TimeStampDateTags("F_DESCARGA_DATOS"));
		HashMap<String, String> mEntityReport = new HashMap<>();
		mEntityReport.put("informe_pend_descarga", "EInformePendDescarga");
		this.setEntidadesInformes(mEntityReport);
	}

	@Override
	public void doOnQuery() {
		if (this.managedForm.existEmptyRequiredDataField()) {
			this.managedForm.message("Establezca filtro de búsqueda.", Form.INFORMATION_MESSAGE);
		} else if (this.checkQuery()) {
			this.refreshTable(this.tablename);
		}
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {

		super.registerInteractionManager(f, gf);
		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(actionevent -> {
			JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");
			final JRReportDescriptor jrd = jpm.getDataMap().get(20);
			new USwingWorker<JasperPrint, Void>() {
				@Override
				protected JasperPrint doInBackground() throws Exception {
					return IMInformePendDescarga.this.createReport(jpm, jrd);
				}

				@Override
				protected void done() {
					try {
						JasperPrint jp = this.uget();
						if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
							final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(IMInformePendDescarga.this.managedForm);
							JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation(jrd.getDscr()), jd, jp);
							jv.setVisible(true);
						} else {
							MessageManager.getMessageManager().showMessage(IMInformePendDescarga.this.managedForm, "M_NO_SE_HAN_ENCONTRADO_DATOS", MessageType.INFORMATION,
									new Object[] {});
						}
					} catch (Exception err) {
						MessageManager.getMessageManager().showExceptionMessage(new OpentachException("M_NO_SE_HAN_ENCONTRADO_DATOS", err), IMInformePendDescarga.logger);
					}
				}
			}.executeOperation(IMInformePendDescarga.this.managedForm);
		});

	}

	@Override
	protected Map<String, Object> getParams(String title, String delegCol) {
		Map<String, Object> mParams = new HashMap<>();
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		Map<String, Object> htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");
		String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
		cgContrato = this.checkContratoFicticio(cgContrato);
		mParams.put("numreq", cgContrato);
		mParams.put("cif", cif);
		mParams.put("empresa", empresa);
		mParams.put("title", title);
		mParams.put("f_informe", new Timestamp(new Date().getTime()));
		return mParams;
	}

	private JasperPrint createReport(JRPropertyManager jpm, JRReportDescriptor jrd) throws Exception {
		final Map<String, Object> params = this.getParams(jrd.getDscr(), jrd.getDelegCol());
		final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
		final String urljr = jrd.getUrl();
		return ocl.getRemoteService(IReportService.class).fillReport(urljr, params, jrd.getMethodAfter(), jrd.getMethodBefore(), null, ocl.getSessionId());
	}

}
