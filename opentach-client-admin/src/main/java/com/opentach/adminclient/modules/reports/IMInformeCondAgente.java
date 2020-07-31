package com.opentach.adminclient.modules.reports;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.report.IReportService;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeCondAgente extends IMReportRoot {

	public IMInformeCondAgente() {
		super("EInformeConductoresAgente", "informe_condagente");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_condagente", "EInformeConductoresAgente");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags("F_ALTA"));
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		managedForm.getDataFieldReference("AGENTE").setEnabled(true);
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);

		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				IMInformeCondAgente.this.createReports();
			}
		});

	}

	protected void createReports() {

		final Map<String, Object> params = this.getParams("informe_condagente", "iddelegacion");
		final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(this.managedForm);
		final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();

		try {
			final String urljr = "com/opentach/reports/condAgente.jasper";
			OperationThread opth = new OperationThread() {
				@Override
				public void run() {
					this.hasStarted = true;
					JasperPrint jp = null;
					try {
						EntityResult res = (EntityResult)((Table)managedForm.getDataFieldReference("EInformeConductoresAgente")).getValue();
						jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, params, null, null, res, ocl.getSessionId());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						this.hasFinished = true;
						this.res = jp;
					}
				}
			};
			ExtendedApplicationManager.proccessOperation(jd, opth, 1000);
			JasperPrint jp = (JasperPrint) opth.getResult();
			if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
				JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation("informe_condagente"), jd, jp);
				jv.setVisible(true);
			} else {
				this.managedForm.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected Hashtable<String, Object> getParams(String title, String delegCol) {
		Hashtable<String, Object> mParams = new Hashtable<String, Object>();

		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");

		mParams.put("title", title);
		mParams.put("f_informe", new Timestamp(fecFin.getTime()));
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		mParams.put("f_fin", new Timestamp(fecFin.getTime()));

		
		return mParams;
	}

}
