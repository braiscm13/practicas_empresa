package com.opentach.client.modules.report;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.util.Calendar;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.Label;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ColorCellRender;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.client.modules.chart.IMGraficaCond;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.utilmize.client.gui.UJFrame;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeConduDescCond extends IMReportRoot {

	private static final Logger	logger					= LoggerFactory.getLogger(IMInformeConduDescCond.class);

	private static final String	EINFORME_INFRAC			= "EInformeConduDescCond";
	protected JDialog			chartdlg				= null;
	protected Form				chartform				= null;
	protected Table				tbInformeConduDescCond	= null;

	private Form				fInfoReport				= null;
	private JDialog				jInfoReport				= null;


	public IMInformeConduDescCond() {
		super(IMInformeConduDescCond.EINFORME_INFRAC, "informe_infrac_cond");
		// this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD));
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_infrac_cond", IMInformeConduDescCond.EINFORME_INFRAC);
		this.setEntidadesInformes(mEntityReport);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.IDCONDUCTOR_FIELD, true);
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.IDCONDUCTOR_FIELD, true);

		this.tbInformeConduDescCond = (Table) this.managedForm.getElementReference("EInformeConduDescCond");
		this.tbInformeConduDescCond.getJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (((Table) IMInformeConduDescCond.this.managedForm.getElementReference("EInformeConduDescCond")).getSelectedRows().length > 0) {
					// IMInformeConduDescCond.this.managedForm.getButton("btnEmail").setEnabled(true);
				} else {
					// IMInformeConduDescCond.this.managedForm.getButton("btnEmail").setEnabled(false);
				}
			}
		});
		this.tbInformeConduDescCond.setRendererForColumn("expanded_driving", new ColorCellRender("expanded_driving", 2, ">"));
		this.tbInformeConduDescCond.setRendererForColumn("available_reduced_rest", new ColorCellRender("available_reduced_rest_eval", 0, "<"));
		this.tbInformeConduDescCond.setRendererForColumn("last_recorded_activity_date", new ColorCellRender("updated", "N", "="));
		// tbInformeConduDescCond.setRendererManager(manager);
		this.tbInformeConduDescCond.getJTable().getTableHeader().resize(this.tbInformeConduDescCond.getJTable().getTableHeader().getWidth(), 100);
		Button btnInforme2 = f.getButton("btnInforme2");
		btnInforme2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				Object oconductor = IMInformeConduDescCond.this.managedForm.getDataFieldValue("IDCONDUCTOR");
				if (oconductor == null) {
					IMInformeConduDescCond.this.managedForm.message("E_MUST_SPECIFY_DRIVER", Form.ERROR_MESSAGE);
					return;
				}
				IMInformeConduDescCond.this.createReports(oconductor, true);
			}
		});

		Button btnRefresh = f.getButton("btnRefresh");
		btnRefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				IMInformeConduDescCond.this.updateTable();
			}
		});

		Button btnInfoInforme = f.getButton("btnInfoInforme");
		btnInfoInforme.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final Form managedForm = (Form) SwingUtilities.getAncestorOfClass(Form.class, (Component) e.getSource());
				final IFormManager formManager = managedForm.getFormManager();

				if (IMInformeConduDescCond.this.jInfoReport == null) {
					IMInformeConduDescCond.this.fInfoReport = formManager.getFormCopy("formInfoDriveReport.xml");

					IMInformeConduDescCond.this.jInfoReport = IMInformeConduDescCond.this.fInfoReport.putInModalDialog();
					IMInformeConduDescCond.this.jInfoReport.setTitle(ApplicationManager.getTranslation("INFORMACION"));
					IMInformeConduDescCond.this.jInfoReport.setPreferredSize(new Dimension(500, 600));
				}

				IMInformeConduDescCond.this.fInfoReport.getInteractionManager().setUpdateMode();
				IMInformeConduDescCond.this.jInfoReport.setVisible(true);
			}
		});

		Button btng = this.managedForm.getButton("btnGrafica");
		if (btng != null) {
			btng.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						if (chartdlg == null) {
							chartform = ApplicationManager.getApplication().getFormManager("managergraficacond")
									.getFormCopy("formGraficaCond.xml");
							chartdlg = getFormDialog(chartform, false);
							Frame fApp = ApplicationManager.getApplication().getFrame();
							Dimension dSize = fApp.getSize();
							chartdlg.setSize((dSize.width * 4) / 5, (dSize.height * 4) / 5);
							chartdlg.setLocationRelativeTo(fApp);
						}
						chartform.getInteractionManager().setInitialState();
						// Inserto los valors de las claves y desactivo elcampo.

//						for (Iterator iter = keys.iterator(); iter.hasNext();) {
//							String ck = (String) iter.next();
//							Object vk = managedForm.getDataFieldValue(ck);
//							chartform.setDataFieldValue(ck, vk);
//							chartform.disableDataField(ck);
//							if (ck.equals(OpentachFieldNames.IDCONDUCTOR_FIELD)) {
//								ck = ck + "2";
//								chartform.setDataFieldValue(ck, vk);
//							}
//
//						}
						// coloco los valores de los filtros.
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date());
						calendar.add(Calendar.DAY_OF_MONTH, -28);
						Date fecIni = calendar.getTime();
						Date fecFin = new Date();

						chartform.setDataFieldValue(OpentachFieldNames.FILTERFECINI, fecIni);
						chartform.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, fecFin);
						chartform.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD,
								managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));
						chartform.setDataFieldValue(OpentachFieldNames.CIF_FIELD,
								managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD));
						// TODO: IDCONDUCTOR-DNI
						chartform.setDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD,
								(String)((Vector)((SearchValue) managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD)).getValue()).get(0));

						// Borro las infracciones.
						((IMGraficaCond) chartform.getInteractionManager()).setInfracciones(new EntityResult());
						((IMGraficaCond) chartform.getInteractionManager()).doOnQuery();
						chartdlg.setVisible(true);

					} catch (Exception ex) {
						MessageManager.getMessageManager().showExceptionMessage(ex, logger);
					}
				}
			});
		}
		// Button btnEmail = f.getButton("btnEmail");
		// btnEmail.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent arg0) {
		// Object idconductor = IMInformeConduDescCond.this.tbInformeConduDescCond.getSelectedRowData().get("idconductor");
		// JasperPrint jv = IMInformeConduDescCond.this.createReports(((Vector) idconductor).get(0), false);
		// FileOutputStream fos = null;
		// try {
		// File f = File.createTempFile("InformeConduccionDescanso", ".pdf");
		// fos = new FileOutputStream(f);
		// JRReportUtil.exportToPDF(jv, fos);
		// // try {
		// // Mail mail = this.getValueToInsert();
		// // BeansFactory.getBean(IMailManagerService.class).mailUpdate(mail);
		// // BeansFactory.getBean(IMailManagerService.class).mailSend(mail.getMaiId());
		// // ((IMMailSend) this.getInteractionManager()).setMessageSent();
		// // IDetailForm detailForm = form.getDetailComponent();
		// // detailForm.getTable().refreshInThread(0);
		// // this.hideDialog();
		// // } finally {
		// // form.setCursor(Cursor.getDefaultCursor());
		// // }
		// } catch (Exception e) {
		// e.printStackTrace();
		// // throw e;
		// } finally {
		// if (fos != null) {
		// try {
		// fos.close();
		// } catch (Exception e) {
		// }
		// }
		// }
		// }
		// });
	}

	// private Mail getValueToInsert() {
	// Mail res = new Mail();
	// res.setMaiId((BigDecimal) this.getForm().getDataFieldValue(MailManagerNaming.MAI_ID));
	// res.setMaiSubject((String) this.getForm().getDataFieldValue(MailManagerNaming.MAI_SUBJECT));
	// res.setMaiBody((String) this.getForm().getDataFieldValue(MailManagerNaming.MAI_BODY));
	// res.setMaiTo((String) this.getForm().getDataFieldValue(MailManagerNaming.MAI_TO));
	// res.setMaiCc((String) this.getForm().getDataFieldValue(MailManagerNaming.MAI_CC));
	// res.setCif((String) this.getForm().getDataFieldValue(MailManagerNaming.CIF));
	// return res;
	// }

	private void updateTable() {

		new USwingWorker<Object, Void>() {
			@Override
			protected Object doInBackground() throws Exception {
				IMInformeConduDescCond.this.tbInformeConduDescCond.refresh();
				return IMInformeConduDescCond.this.tbInformeConduDescCond.getValue();
			}

			@Override
			protected void done() {
				try {
					Object value = this.uget();
					Hashtable reg0 = null;
					if (value instanceof EntityResult) {
						reg0 = ((EntityResult) (IMInformeConduDescCond.this.tbInformeConduDescCond).getValue()).getRecordValues(0);
					} else {
						reg0 = (Hashtable) value;
					}
					if (reg0 != null) {
						Label et = (Label) IMInformeConduDescCond.this.managedForm.getElementReference("CURRENT_WEEK");
						if (et != null) {
							et.setText(ApplicationManager.getTranslation("SEMANA_ANALIZADA") + " " + (String) reg0.get("current_week"));
						}
					}
					IMInformeConduDescCond.this.tbInformeConduDescCond.setEnabled(true);
				} catch (Exception err) {
					MessageManager.getMessageManager().showExceptionMessage(err, IMInformeConduDescCond.logger);
				}
			}

		}.executeOperation(IMInformeConduDescCond.this.getManagedForm());

	}

	@Override
	protected void refreshTable(String tablename) {
		this.updateTable();
	}

	@Override
	public void doOnQuery() {
		if (this.managedForm.existEmptyRequiredDataField()) {
			this.managedForm.message("Establezca filtro de búsqueda.", Form.INFORMATION_MESSAGE);
		} else if (this.checkQuery()) {
			this.refreshTable(this.tablename);
		}
	}

	// @Override
	// protected void refreshTable(String tablename) {
	// }

	@Override
	public void doOnBuildReport() {
		super.doOnBuildReport();
	}

	protected JasperPrint createReports(Object idconductor, boolean isVisible) {

		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");

		final JRReportDescriptor jrd = jpm.getDataMap().get(37);

		final Map<String, Object> params = this.getParams(jrd.getDscr(), jrd.getDelegCol(), idconductor);
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
								jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, params, jrd.getMethodAfter(), jrd.getMethodBefore(), null, ocl.getSessionId());
								this.res = jp;
							} catch (Exception e) {
								e.printStackTrace();
								this.res = e;
							} finally {
								this.hasFinished = true;
							}
						}
					};
					ExtendedApplicationManager.proccessOperation((UJFrame) SwingUtilities.getWindowAncestor(this.managedForm), opth, 1000);
					if (opth.getResult() instanceof Throwable) {
						this.managedForm.message(((Throwable) opth.getResult()).getMessage(), Form.ERROR_MESSAGE);
					} else {
						JasperPrint jp = (JasperPrint) opth.getResult();
						if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
							JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation(jrd.getDscr()), SwingUtilities.getWindowAncestor(this.managedForm),
									jp);
							if (isVisible) {
								jv.setVisible(true);
								return jp;
							} else {
								return jp;
							}
						} else {
							this.managedForm.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.WARNING_MESSAGE);
							return null;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return null;
	}

	protected Hashtable<String, Object> getParams(String title, String delegCol, Object idconductor) {
		Hashtable<String, Object> mParams = new Hashtable<String, Object>();
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		Map htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");
		String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
		cgContrato = this.checkContratoFicticio(cgContrato);

		mParams.put("numreq", cgContrato);
		mParams.put("empresa", empresa);
		mParams.put("CIF", cif);
		mParams.put("title", title);
		mParams.put("locale", ApplicationManager.getLocale());

		// Object ob = this.managedForm.getDataFieldValue("IDCONDUCTOR");
		if (idconductor instanceof SearchValue) {
			idconductor = ((SearchValue) idconductor).getValue();
			if (idconductor instanceof List) {
				idconductor = ((List<Object>) idconductor).get(0);
			}
		}
		mParams.put("idconductor", idconductor);

		mParams.put(IInfractionService.ENGINE_ANALYZER, this.managedForm.getDataFieldValue(IInfractionService.ENGINE_ANALYZER));
		return mParams;
	}

}
