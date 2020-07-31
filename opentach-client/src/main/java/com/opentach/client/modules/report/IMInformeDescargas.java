package com.opentach.client.modules.report;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import com.opentach.common.user.IUserData;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeDescargas extends IMReportRoot {

	private static final Logger	logger		= LoggerFactory.getLogger(IMInformeDescargas.class);

	private Button				bInforme	= null;

	public IMInformeDescargas() {
		super("EInformePendDescargaDetail", "informe_pend_descarga");
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.bInforme = f.getButton("btnInforme2");
		this.cgContract = f.getDataFieldReference(OpentachFieldNames.CG_CONTRATO_FIELD);
		this.bInforme.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IMInformeDescargas.this.accBotInforme();
			}
		});
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
	public void setInitialState() {

		super.setInitialState();
		this.managedForm.enableButton("btnInforme2");
		this.managedForm.setDataFieldValue("MAXVAL.2", new Date());
	}

	private void accBotInforme() {
		if (!this.checkRequiredVisibleDataFields(true)) {
			return;
		}

		new USwingWorker<JasperPrint, Void>() {
			@Override
			protected JasperPrint doInBackground() throws Exception {
				return IMInformeDescargas.this.createReport();
			}

			@Override
			protected void done() {
				try {
					JasperPrint jp = this.uget();
					if ((jp != null) && (jp.getPages() != null) && (!jp.getPages().isEmpty())) {
						final Window jd = SwingUtilities.getWindowAncestor(IMInformeDescargas.this.managedForm);
						JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation("Informe_resumen_de_descargas"), jd, jp);
						jv.setVisible(true);
					} else {
						MessageManager.getMessageManager().showMessage(IMInformeDescargas.this.managedForm, "M_NO_SE_HAN_ENCONTRADO_DATOS", MessageType.INFORMATION,
								new Object[] {});
					}
				} catch (Exception err) {
					MessageManager.getMessageManager().showExceptionMessage(new OpentachException("M_NO_SE_HAN_ENCONTRADO_DATOS", err), IMInformeDescargas.logger);
				}
			}
		}.executeOperation(this.managedForm);

	}

	protected JasperPrint createReport() throws Exception {
		final Map<String, Object> params = this.getParams("Informe_resumen_de_descargas", "iddelegacion");
		final String urljr = "com/opentach/reports/descargas.jasper";
		final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
		return ocl.getRemoteService(IReportService.class).fillReport(urljr, params, null, null, null, ocl.getSessionId());

	}

	@Override
	protected Map<String, Object> getParams(String title, String delegCol) throws Exception {
		Map<String, Object> mParams = new HashMap<String, Object>();
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		Map<String, Object> htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");
		String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
		cgContrato = this.checkContratoFicticio(cgContrato);
		mParams.put(IMInformeSelec.pNUMREQ, cgContrato);
		mParams.put(IMInformeSelec.pCIF, cif);
		mParams.put(IMInformeSelec.pEMPRESA, empresa);
		mParams.put(IMInformeSelec.pTITLE, title);
		mParams.put(IMInformeSelec.pFINFORME, new Timestamp(new Date().getTime()));
		this.putSqlDelegParam(mParams, title, delegCol);
		return mParams;
	}

	private void putSqlDelegParam(Map<String, Object> mParams, String title, String delegCol) throws Exception {

		StringBuffer sb = new StringBuffer();
		IUserData du = null;
		final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();

		du = ocl.getUserData();
		String prefix = "CDVEMPRE_REQ_REALES.";

		Object selectedCif = this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD);
		if (selectedCif == null) {
			if ((du.getDelegList() != null) && (!du.getDelegList().isEmpty())) {
				sb.append("  AND ").append(" ( ");
				Map<String, Number> l = du.getDelegList();
				Set<String> values = l.keySet();
				Iterator<String> itr = values.iterator();
				while (itr.hasNext()) {
					Object key = itr.next();
					Object value = l.get(key);

					sb.append(" ( ").append(prefix).append("CIF = '").append(key).append("' ");
					if ((value == null) || (((Number) value).intValue() == -1)) {
						// sb.append(" IS NULL )");
						sb.append(")");
					} else {
						sb.append("AND IDDELEGACION").append(" =  '").append(value).append("' )");
					}
					if (itr.hasNext()) {
						sb.append(" OR ");
					} else {
						sb.append(" ) ");
					}
				}
			}
		} else {
			sb.append("  AND ").append(" ( ");
			sb.append(" ( ").append(prefix).append("CIF = '").append(selectedCif).append("' ");
			if ((du.getDelegList() != null) && (du.getDelegList().get(selectedCif) != null)) {
				Number value = du.getDelegList().get(selectedCif);
				if ((value == null) || (value.intValue() == -1)) {
					sb.append(")");
				} else {
					sb.append("' AND IDDELEGACION").append(" =  '").append(value).append("' )");
				}
			} else {
				sb.append(" ) ");
			}
			sb.append(" ) ");
		}

		mParams.put(IMInformeSelec.pSQLDELEG, sb.toString());
		if (!"".equals(prefix)) {
			mParams.put(IMInformeSelec.pSQLDELEG + "_base", sb.toString().replaceAll(prefix, ""));
		}
	}

}
