package com.opentach.adminclient.modules.reports;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeDemos extends IMReportRoot {

	private static final Logger	logger	= LoggerFactory.getLogger(IMInformeDemos.class);

	public IMInformeDemos() {
		super("EInformeDemos", "informe_demos");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		this.setDateTags(new TimeStampDateTags("F_ALTA"));
		mEntityReport.put("informe_demos", "EInformeDemos");
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
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				IMInformeDemos.this.createReports();
			}
		});

	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		try {
			this.refreshTables();
			this.managedForm.getDataFieldReference("FILTERFECINI").setEnabled(true);
			this.managedForm.getDataFieldReference("FILTERFECFIN").setEnabled(true);
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, IMInformeDemos.logger);
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

	private void createReports() {
		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");
		final JRReportDescriptor jrd = jpm.getDataMap().get(45);
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
							Table tb = (Table) IMInformeDemos.this.managedForm.getElementReference("EInformeDemos");
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
	protected Map<String, Object> getDateFilterValues(List<String> keys) {
		SearchValue vb = null;
		try {
			if ((keys == null) || keys.isEmpty()) {
				return new Hashtable<String, Object>();
			}
			DateDataField cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
			if (cf != null) {
				Date selectedfecini = (Date) cf.getDateValue();
				cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
				Date selectedfecfin = (Date) cf.getDateValue();

				if ((selectedfecfin != null) && (selectedfecini != null)) {
					Vector<Object> v = new Vector<Object>(2);
					v.add(selectedfecini);
					v.add(selectedfecfin);
					vb = new SearchValue(SearchValue.BETWEEN, v);

				} else if (selectedfecfin != null) { // only less condition.
					vb = new SearchValue(SearchValue.LESS_EQUAL, selectedfecfin);
				} else if (selectedfecini != null) { // only less condition.
					vb = new SearchValue(SearchValue.MORE_EQUAL, selectedfecini);
				}
				if (vb != null) {
					Map<String, Object> rtn = new Hashtable<String, Object>();
					for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
						String key = iter.next();
						rtn.put(key, vb);
					}
					return rtn;
				}
			}
		} catch (Exception e) {
			IMInformeDemos.logger.error(null, e);
		}
		return null;
	}
}
