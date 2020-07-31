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
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeDemosActivas extends IMReportRoot {


	public IMInformeDemosActivas() {
		super("EInformeDemosActivas", "informe_demos_activas");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("EInformeDemosActivas", "EInformeDemosActivas");
		this.setEntidadesInformes(mEntityReport);
	}
	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {

		super.registerInteractionManager(f, gf);
		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				IMInformeDemosActivas.this.createReports();
			}
		});

	}
	@Override
	public void doOnQuery(final boolean alert) {
		if (this.managedForm.existEmptyRequiredDataField()) {
			if (alert) {
				this.managedForm.message("Establezca filtro de búsqueda.", Form.INFORMATION_MESSAGE);
			}
		} else if (this.checkQuery()) {
			this.refreshTable(this.tablename);
		}
	}

	
	private void createReports() {
		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");
		final JRReportDescriptor jrd = jpm.getDataMap().get(56);
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
							Table tb = (Table) IMInformeDemosActivas.this.managedForm.getElementReference("EInformeDemosActivas");
							jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, params, jrd.getMethodAfter(), jrd.getMethodBefore(),
									new EntityResult((Hashtable) tb.getValue()), ocl.getSessionId());
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
					JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation(jrd.getDscr()), jd, jp);
					jv.setVisible(true);
				} else {
					this.managedForm.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.WARNING_MESSAGE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	protected Map<String, Object> getParams(String title, String delegCol) {
		Map<String, Object> mParams = new HashMap<String, Object>();
		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
		mParams.put("title", ApplicationManager.getTranslation(title));
		if (fecIni!=null) {
			mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		}
		if (fecFin!=null) {
			mParams.put("f_fin", new Timestamp(fecFin.getTime()));
		}

		return mParams;
	}
	
	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		this.doOnQuery(false);
		managedForm.getButton("btnInforme2").setEnabled(true);
	}

}
