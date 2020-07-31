package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
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
import com.ontimize.gui.container.DataComponentGroup;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.comp.action.AnalizeInfractionsActionListener;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeInfracAgente extends IMReportRoot {

	private static final Logger	logger			= LoggerFactory.getLogger(IMInformeInfracCond.class);
	private static final String	EINFORME_INFRAC	= "EInformeInfrac";
	protected JDialog			chartdlg		= null;
	protected Form				chartform		= null;
	protected Table				tblInforme		= null;
	private final Vector<Object>		v				= new Vector<Object>();

	public IMInformeInfracAgente() {
		super(IMInformeInfracAgente.EINFORME_INFRAC, "informe_infrac_cond");
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD, OpentachFieldNames.FECFIN_FIELD));
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_infrac_cond", IMInformeInfracAgente.EINFORME_INFRAC);
		this.setEntidadesInformes(mEntityReport);

		this.v.add("ECD");
		this.v.add("ECI");
		this.v.add("ECS");
		this.v.add("ECB");
		this.v.add("FDD");
		this.v.add("FDS");
		this.v.add("FDS45");
		this.v.add("FDSR");
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.IDCONDUCTOR_FIELD, true);
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECINI);
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECFIN);
		Date d = new Date();
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, DateUtil.addDays(d, -365));
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, d);
		this.managedForm.enableDataField(IInfractionService.ENGINE_ANALYZER);
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.tblInforme = (Table) f.getDataFieldReference(this.tablename);

		Button btnInforme2 = f.getButton("btnInforme2");
		btnInforme2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				IMInformeInfracAgente.this.createReports();
			}
		});

		try {
			f.getButton("ANALIZAR").addActionListener(new AnalizeInfractionsActionListener(true));
		} catch (Exception e) {
			if (e.getMessage().startsWith("E_MAX_DRIVERS_LIMIT_")) {
				String s = e.getMessage();
				String aux = s.substring("E_MAX_DRIVERS_LIMIT_".length(), s.length());
				this.managedForm.message(
						ApplicationManager.getTranslation("E_MAX_DRIVERS_LIMIT", ApplicationManager.getApplicationBundle(), new Object[] { aux }),
						Form.ERROR_MESSAGE);

			}
		}
	}

	@Override
	protected void refreshTable(String tablename) {}

	@Override
	public void doOnQuery() {}

	@Override
	public void doOnBuildReport() {
		super.doOnBuildReport();
	}

	protected void createReports() {

		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");

		final JRReportDescriptor jrd = jpm.getDataMap().get(54);

		final Map<String, Object> params = this.getParams(jrd.getDscr(), jrd.getDelegCol());
		final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(this.managedForm);
		final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
		if (jrd != null) {
			final List lReports = jrd.getLReports();
			if ((lReports == null) || (lReports.size() == 0)) {
				try {
					final String urljr = jrd.getUrl();
					OperationThread opth = new OperationThread() {
						@Override
						public void run() {
							this.hasStarted = true;
							JasperPrint jp = null;
							Table tb = (Table) IMInformeInfracAgente.this.managedForm.getElementReference("EInformeInfrac");
							EntityResult res = new EntityResult((Hashtable) tb.getValue());
							try {
								jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, params, null,null,
										res, ocl.getSessionId());
								this.res = jp;
							} catch (Exception e) {
								e.printStackTrace();
								this.res = e;
							} finally {
								this.hasFinished = true;
							}
						}
					};
					ExtendedApplicationManager.proccessOperation(jd, opth, 1000);
					if (opth.getResult() instanceof Throwable) {
						this.managedForm.message(((Throwable) opth.getResult()).getMessage(), Form.ERROR_MESSAGE);
					} else {
						JasperPrint jp = (JasperPrint) opth.getResult();
						if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
							JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation(jrd.getDscr()), jd, jp);
							jv.setVisible(true);
						} else {
							this.managedForm.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.WARNING_MESSAGE);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected Hashtable<String, Object> getParams(String title, String delegCol) {
		Hashtable<String, Object> mParams = new Hashtable<String, Object>();
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		Map htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");
		String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
		cgContrato = this.checkContratoFicticio(cgContrato);
		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
		mParams.put("numreq", cgContrato);
		mParams.put("empresa", empresa);

		mParams.put("title", title);
		mParams.put("f_informe", new Timestamp(fecFin.getTime()));
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		mParams.put("f_fin", new Timestamp(fecFin.getTime()));
		mParams.put("locale", ApplicationManager.getLocale());
		mParams.put("cif", cif);

		String aux = " AND ( IDCONDUCTOR = ";
		Object oconductor = this.managedForm.getDataFieldValue("IDCONDUCTOR");

		Vector<String> vDrivers = new Vector<String>();
		if (oconductor instanceof String) {
			aux += "'" + (String) this.managedForm.getDataFieldValue("IDCONDUCTOR") + "' )";
			vDrivers.add((String) this.managedForm.getDataFieldValue("IDCONDUCTOR"));
		} else if (oconductor instanceof SearchValue) {
			Vector v = (Vector) ((SearchValue) this.managedForm.getDataFieldValue("IDCONDUCTOR")).getValue();
			for (int i = 0; i < v.size(); i++) {
				Object idDriver = v.get(i);
				if (i == 0) {
					aux += "'" + idDriver + "'";
				} else {
					aux += " idconductor = '" + idDriver + "'";
				}
				if ((i + 1) != v.size()) {
					aux += "  OR ";
				} else {
					aux += " ) ";
				}
				vDrivers.add((String) idDriver);
			}

		}
		if (!vDrivers.isEmpty()) {
			mParams.put("DRIVER_FILTER", vDrivers);
		}
		if (aux.length() > 22) {
			mParams.put("idconductor", aux);
		}
		int i = 1;
		DataComponentGroup filtergroup;
		while ((filtergroup = (DataComponentGroup) this.managedForm.getElementReference(this.bandcfgname + "." + i)) != null) {
			Vector attrs = filtergroup.getAttributes();
			for (Enumeration enumeration = attrs.elements(); enumeration.hasMoreElements();) {
				String attr = (String) enumeration.nextElement();
				DataField campo = (DataField) this.managedForm.getDataFieldReference(attr);
				if (campo != null) {
					mParams.put(attr, ((Integer) campo.getValue()).intValue() == 0 ? false : true);
				}
			}
			i++;
		}

		i = 1;

		String auxFilter = " ( TIPO = ";
		while ((filtergroup = (DataComponentGroup) this.managedForm.getElementReference(this.filtergroupname + "." + i)) != null) {
			Vector attrs = filtergroup.getAttributes();
			int j = 0;
			int k = 0;
			for (Enumeration enumeration = attrs.elements(); enumeration.hasMoreElements();) {
				String attr = (String) enumeration.nextElement();
				DataField campo = (DataField) this.managedForm.getDataFieldReference(attr);
				if (campo != null) {
					boolean b = ((Integer) campo.getValue()).intValue() == 0 ? false : true;
					if (b) {
						if (k == 0) {
							auxFilter += "'" + this.v.get(j - 1) + "'";
							k++;
						} else {
							auxFilter += " OR TIPO = '" + this.v.get(j - 1) + "'";
						}
					}
				}
				j++;
			}
			i++;
		}
		auxFilter += " ) AND ";

		mParams.put("pDSCR_FILTER", auxFilter);
		mParams.put(IInfractionService.ENGINE_ANALYZER, this.managedForm.getDataFieldValue(IInfractionService.ENGINE_ANALYZER));
		return mParams;
	}

}
