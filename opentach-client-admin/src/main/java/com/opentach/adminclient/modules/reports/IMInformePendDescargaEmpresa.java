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
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformePendDescargaEmpresa extends IMReportRoot {

	private static final Logger	logger	= LoggerFactory.getLogger(IMInformePendDescargaEmpresa.class);

	public IMInformePendDescargaEmpresa() {
		super("EInformePendDescarga", "informe_pend_descarga_empresa");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		this.setDateTags(new TimeStampDateTags("F_ALTA"));
		mEntityReport.put("informe_pend_descarga_empresa", "EInformePendDescarga");
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
				IMInformePendDescargaEmpresa.this.createReports();
			}
		});

	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		try {
			this.refreshTables();
			this.managedForm.getDataFieldReference("CG_PROV").setEnabled(true);
			this.managedForm.getDataFieldReference("ZONA").setEnabled(true);
			this.managedForm.getDataFieldReference("FILTERFECINI").setEnabled(true);
			this.managedForm.getDataFieldReference("FILTERFECFIN").setEnabled(true);
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, IMInformePendDescargaEmpresa.logger);
		}
	}

	@Override
	protected Map<String, Object> getParams(String title, String delegCol) {
		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("title", title);
		mParams.put("f_informe", new Timestamp(new Date().getTime()));
		return mParams;
	}

	private void createReports() {
		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");
		final JRReportDescriptor jrd = jpm.getDataMap().get(36);
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
							Table tb = (Table) IMInformePendDescargaEmpresa.this.managedForm.getElementReference("EInformePendDescarga");
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
}
