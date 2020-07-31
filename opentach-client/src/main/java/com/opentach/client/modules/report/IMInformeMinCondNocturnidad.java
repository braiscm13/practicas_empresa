package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.IJRConstants;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeMinCondNocturnidad extends IMReportRoot {

	private static final Logger logger = LoggerFactory.getLogger(IMInformeMinCondNocturnidad.class);

	public IMInformeMinCondNocturnidad() {
		super("EInformeMinCondNocturnidad", "informe_min_cond_nocturnidad");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		this.setDateTags(new TimeStampDateTags("FEC_COMIENZO"));
		mEntityReport.put("informe_min_cond_nocturnidad", "EInformeMinCondNocturnidad");
		this.setEntidadesInformes(mEntityReport);
	}

	@Override
	public void doOnQuery(boolean alert) {
		if (this.managedForm.existEmptyRequiredDataField()) {
			if (alert) {
				this.managedForm.message("Establezca filtro de búsqueda.", Form.INFORMATION_MESSAGE);
			}
		} else if (this.checkQuery()) {
			this.refreshTable(this.tablename);
		}
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {

		super.registerInteractionManager(f, gf);
		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				IMInformeMinCondNocturnidad.this.createReports();
			}
		});

	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		try {
			this.setDataFieldsEnable(true, "FILTERFECINI", "FILTERFECFIN", "BEGIN_NOC_TIME", "END_NOC_TIME");
			this.managedForm.setDataFieldValue("BEGIN_NOC_TIME", 22 * 60 * 60 * 1000l);
			this.managedForm.setDataFieldValue("END_NOC_TIME", 6 * 60 * 60 * 1000l);
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, IMInformeMinCondNocturnidad.logger);
		}
	}

	@Override
	protected Map<String, Object> getParams(String title, String delegCol) {
		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("title", ApplicationManager.getTranslation(title));

		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		mParams.put(IJRConstants.JRCIF, cif);
		Map htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");

		mParams.put("empresa", empresa);
		mParams.put("cif", this.managedForm.getDataFieldValue("CIF"));
		mParams.put("cg_contrato", this.managedForm.getDataFieldValue("CG_CONTRATO"));

		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		mParams.put("f_fin", new Timestamp(fecFin.getTime()));

		return mParams;
	}

	private void createReports() {
		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");
		final JRReportDescriptor jrd = jpm.getDataMap().get(50);
		final Map<String, Object> params = this.getParams(jrd.getDscr(), jrd.getDelegCol());
		final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(this.managedForm);
		final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
		if (jrd != null) {

			try {
				final String urljr = jrd.getUrl();
				OperationThread opth = new OperationThread() {
					@Override
					public void run() {
						this.hasStarted = true;
						JasperPrint jp = null;
						try {
							Table tb = (Table) IMInformeMinCondNocturnidad.this.managedForm.getElementReference("EInformeMinCondNocturnidad");
							jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, params, jrd.getMethodAfter(), jrd.getMethodBefore(),
									new EntityResult((Hashtable) tb.getValue()), ocl.getSessionId());
						} catch (Exception e) {
							IMInformeMinCondNocturnidad.logger.error(null, e);
						} finally {
							this.hasFinished = true;
							this.res = jp;
						}
					}
				};
				ExtendedApplicationManager.proccessOperation(jd, opth, 1000);
				JasperPrint jp = (JasperPrint) opth.getResult();
				if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
					JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation(jrd.getDscr()), jd, jp);
					jv.setVisible(true);
				} else {
					this.managedForm.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.WARNING_MESSAGE);
				}
			} catch (Exception e) {
				IMInformeMinCondNocturnidad.logger.error(null, e);
			}
		}
	}
}