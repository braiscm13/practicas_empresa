package com.opentach.client.modules.report;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.Column;
import com.ontimize.gui.container.DataComponentGroup;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.comp.action.AnalizeInfractionsActionListener;
import com.opentach.client.comp.render.MinutesCellRender;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.client.modules.chart.IMGraficaCond;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.opentach.common.user.IUserData;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeInfracCond extends IMReportRoot {

	private static final Logger	logger			= LoggerFactory.getLogger(IMInformeInfracCond.class);
	private static final String	EINFORME_INFRAC	= "EInformeInfrac";
	protected JDialog			chartdlg		= null;
	protected Form				chartform		= null;
	protected Table				tblInforme		= null;
	private final Vector		v				= new Vector();

	public IMInformeInfracCond() {
		super(IMInformeInfracCond.EINFORME_INFRAC, "informe_infrac_cond");
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD, OpentachFieldNames.FECFIN_FIELD));
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_infrac_cond", IMInformeInfracCond.EINFORME_INFRAC);
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
				Object oconductor = IMInformeInfracCond.this.managedForm.getDataFieldValue("IDCONDUCTOR");
				if (oconductor == null) {
					IMInformeInfracCond.this.managedForm.message("E_MUST_SPECIFY_DRIVER", Form.ERROR_MESSAGE);
					return;
				}

				IMInformeInfracCond.this.createReports();
			}
		});

		// Render para las columnas de minutos--> D HH:mm
		if (this.tblInforme != null) {
			this.tblInforme.setRendererForColumn("TCP", new MinutesCellRender("TCP"));
			this.tblInforme.setRendererForColumn("TDP", new MinutesCellRender("TDP"));
			this.tblInforme.setRendererForColumn("EXCON", new MinutesCellRender("EXCON"));
			this.tblInforme.setRendererForColumn("FADES", new MinutesCellRender("FADES"));
			this.tblInforme.getJTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.tblInforme.getJTable().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {
					try {
						if (event.getClickCount() == 2) {
							int[] i = IMInformeInfracCond.this.tblInforme.getSelectedRows();
							if (i.length == 1) {
								Hashtable reg = IMInformeInfracCond.this.tblInforme.getRowData(i[0]);
								Date start = (Date) reg.get("FECHORAINI");
								Date end = (Date) reg.get("FECHORAFIN");
								Calendar cal = Calendar.getInstance();
								cal.setTime(start);
								cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
								start = cal.getTime();
								Date pEnd = start;
								cal.setTime(start);
								while (pEnd.compareTo(end) < 0) {
									cal.add(Calendar.DATE, 7);
									pEnd = cal.getTime();
								}
								cal.set(Calendar.HOUR_OF_DAY, 23);
								cal.set(Calendar.MINUTE, 59);
								cal.set(Calendar.SECOND, 59);
								pEnd = cal.getTime();
								IMInformeInfracCond.this.openGraficaActiv(start, pEnd);
							}
						}
					} catch (Exception ex) {
						MessageManager.getMessageManager().showExceptionMessage(ex, IMInformeInfracCond.logger);
					}
				}
			});
		}
		try {
			f.getButton("ANALIZAR").addActionListener(new AnalizeInfractionsActionListener(false));
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

	protected void openGraficaActiv(Date from, Date to) throws Exception {
		if (this.chartdlg == null) {
			this.chartform = this.formManager.getFormCopy("formGraficaCond.xml");
			this.chartform.getButton("ANALIZAR").setVisible(false);
			this.chartdlg = this.getFormDialog(this.chartform, false);
			Frame fApp = ApplicationManager.getApplication().getFrame();
			Dimension dSize = fApp.getSize();
			this.chartdlg.setSize((dSize.width * 4) / 5, (dSize.height * 4) / 5);
			this.chartdlg.setLocationRelativeTo(fApp);
		}
		this.chartform.getInteractionManager().setInitialState();
		// Inserto los valors de las claves y desactivo elcampo.
		String[] fields = { OpentachFieldNames.CIF_FIELD, OpentachFieldNames.DNI_FIELD };
		for (int i = 0; (fields != null) && (i < fields.length); i++) {
			String ck = fields[i];
			this.chartform.setDataFieldValue(ck, this.managedForm.getDataFieldValue(ck));
			this.chartform.disableDataField(ck);
		}
		int rowsel = this.tblInforme.getSelectedRow();
		Hashtable fila = this.tblInforme.getRowData(rowsel);
		String idConductor = (String) fila.get(OpentachFieldNames.IDCONDUCTOR_FIELD);
		String cg_contrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
		// coloco los valores de los filtros.
		this.chartform.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD, cg_contrato);
		this.chartform.setDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD, idConductor);
		this.chartform.setDataFieldValue(OpentachFieldNames.FILTERFECINI, from);
		this.chartform.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, to);
		this.chartform.disableDataField(OpentachFieldNames.IDCONDUCTOR_FIELD);
		this.chartform.disableDataField(OpentachFieldNames.FILTERFECINI);
		this.chartform.disableDataField(OpentachFieldNames.FILTERFECFIN);

		// Muestro todas las actividades
		((IMGraficaCond) this.chartform.getInteractionManager()).doOnQuery();
		// Muestro la infraccion de la fila seleccionada.
		EntityResult res = new EntityResult();
		res.addRecord(fila);
		((IMGraficaCond) this.chartform.getInteractionManager()).setInfracciones(res);
		this.chartdlg.setVisible(true);
	}

	// TODO: esto está bien, sanciones en españa??
	@Override
	public void setQueryInsertMode() {
		super.setQueryInsertMode();
		Column cSanc = (Column) this.managedForm.getElementReference("Hechos_sancionables");
		if (cSanc != null) {
			Locale locale = ApplicationManager.getLocale();
			cSanc.setVisible(locale.equals(IUserData.SPANISHLOCALE));
		}
	}

	@Override
	protected void refreshTable(String tablename) {}

	// PABLO: En el analisis de infracciones no se realizan consultas ya que las
	// infracciones deben calcularse
	// en el momento.
	@Override
	public void doOnQuery() {}

	@Override
	public void doOnBuildReport() {
		super.doOnBuildReport();
	}

	protected void createReports() {

		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");

		final JRReportDescriptor jrd = jpm.getDataMap().get(32);

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
							try {
								jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, params, jrd.getMethodAfter(),
										jrd.getMethodBefore(), null, ocl.getSessionId());
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
