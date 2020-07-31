package com.opentach.adminclient.modules.reports;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.client.modules.IMRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeResumenEmpresa extends IMReportRoot {

	private static final Logger	logger	= LoggerFactory.getLogger(IMInformeResumenEmpresa.class);

	public IMInformeResumenEmpresa() {
		super("EInformeResumenEmpresas", "informe_resumen_empresas");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		this.setDateTags(new TimeStampDateTags("F_ALTA"));
		mEntityReport.put("informe_resumen_empresas", "EInformeResumenEmpresas");
		this.setEntidadesInformes(mEntityReport);
	}


	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {

		super.registerInteractionManager(f, gf);
		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				IMInformeResumenEmpresa.this.createReports();
			}
		});
		
		Button bRefresh = f.getButton("btnRefrescar2");
		if (bRefresh != null) {
			bRefresh.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Date dini = (Date) IMInformeResumenEmpresa.this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECINI);
						Date dfin = (Date) IMInformeResumenEmpresa.this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECFIN);
						if ((dini == null) || (dfin == null) || dini.after(dfin) || dini.before(IMRoot.BEGININGDATE)) {
							if ((dini != null) && (dfin != null) && dini.after(dfin)) {
								IMInformeResumenEmpresa.this.managedForm.message("M_ERROR_DATE_RANGE", Form.WARNING_MESSAGE);
							}
							return;
						}
						IMInformeResumenEmpresa.this.refreshTable("EInformeResumenEmpresas");
					} catch (Exception ex) {
						MessageManager.getMessageManager().showExceptionMessage(ex, IMInformeResumenEmpresa.logger);
					}
				}
			});
		}

	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		try {
			this.managedForm.getDataFieldReference("SOLO_ALTA_BAJA").setEnabled(true);
			((CheckDataField)this.managedForm.getDataFieldReference("SOLO_ALTA_BAJA")).setValue("N");
			this.refreshTables();
			this.managedForm.getDataFieldReference("FILTERFECINI").setEnabled(true);
			this.managedForm.getDataFieldReference("FILTERFECFIN").setEnabled(true);
			this.managedForm.getButton("btnRefrescar2").setEnabled(true);

		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, IMInformeResumenEmpresa.logger);
		}
	}

	@Override
	protected Map<String, Object> getParams(String title, String delegCol) {
		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("title", ApplicationManager.getTranslation(title));
		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		mParams.put("f_fin", new Timestamp(fecFin.getTime()));
		return mParams;
	}
	
	public void doOnQuery() {

		this.refreshTable(this.tablename);

	}

	public void doOnQuery(boolean alert) {

		this.refreshTable(this.tablename);

	}
	

	private void createReports() {
		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");
		final JRReportDescriptor jrd = jpm.getDataMap().get(49);
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
							Table tb = (Table) IMInformeResumenEmpresa.this.managedForm.getElementReference("EInformeResumenEmpresas");
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
	@SuppressWarnings("unchecked")
	protected void refreshTable(String tablename) {


		Hashtable<String, Object> cv = new Hashtable<String, Object>();
		cv.put(OpentachFieldNames.FILTERFECINI,  this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECINI));
		cv.put(OpentachFieldNames.FILTERFECFIN,  this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECFIN));
		cv.put("SOLO_ALTA_BAJA", this.managedForm.getDataFieldValue("SOLO_ALTA_BAJA"));
		ReferenceLocator buscador = (ReferenceLocator) this.managedForm.getFormManager().getReferenceLocator();
		Table tb = (Table) this.managedForm.getDataFieldReference(tablename);
		try{
			Entity ent = buscador.getEntityReference(tb.getEntityName());
			if (ent != null) {
				Vector<Object> v = tb.getAttributeList();
				EntityResult res = ent.query(cv, v, buscador.getSessionId());
				if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
					if (tb != null) {
						tb.setValue(res);
						tb.setEnabled((res.calculateRecordNumber() > 0) ? true : false);
					}
				}
			}
		} catch (Exception e) {
			IMInformeResumenEmpresa.logger.error(null, e);
		}
	}
}
