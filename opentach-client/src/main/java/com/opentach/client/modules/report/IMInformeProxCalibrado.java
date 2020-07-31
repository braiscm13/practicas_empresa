package com.opentach.client.modules.report;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.CellRenderer.CellRendererColorManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeProxCalibrado extends IMReportRoot {

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);

		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				IMInformeProxCalibrado.this.createReports();
			}
		});

		Table t = (Table) f.getDataFieldReference("EInformeProxCalibrado");

		t.setCellRendererColorManager(new CellRendererColorManager() {
			private final Color	ERRORCOLOR	= new Color(0xff, 0x66, 0x00);

			@Override
			public Color getForeground(JTable arg0, int arg1, int arg2, boolean arg3) {
				return null;
			}

			@Override
			@SuppressWarnings("deprecation")
			public Color getBackground(JTable jt, int row, int col, boolean selected) {
				if (!selected && (jt != null)) {
					TableColumn tcfecha = jt.getColumn("FEC_PROXIMO");
					int im = tcfecha.getModelIndex();
					Object fecTable = jt.getValueAt(row, im);
					if ((fecTable == null) || fecTable.toString().startsWith("NO")) {
						return this.ERRORCOLOR;
					} else {
						String dia = ((String) fecTable).substring(0, 2);
						String mes = ((String) fecTable).substring(3, 5);
						String anyo = ((String) fecTable).substring(6);
						Date dfecTable = new Date((new Integer(anyo)).intValue() - 1900, (new Integer(mes)).intValue(), (new Integer(dia)).intValue());
						if (dfecTable.getTime() < (new Date()).getTime()) {

							return this.ERRORCOLOR;
						}
					}

				}
				return null;
			}
		});

	}

	public IMInformeProxCalibrado() {
		super("EInformeProxCalibrado", "informe_prox_calibrado");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		// setDateTags(new TimeStampDateTags("F_DESCARGA_DATOS"));
		mEntityReport.put("EInformeProxCalibrado", "EInformeProxCalibrado");
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

	protected void createReports() {

		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");

		final JRReportDescriptor jrd = jpm.getDataMap().get(19);

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

	@Override
	protected Hashtable<String, Object> getParams(String title, String delegCol) {
		Hashtable<String, Object> mParams = new Hashtable<String, Object>();
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		Map htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");
		mParams.put("cif", cif);
		mParams.put("empresa", empresa);
		mParams.put("title", title);
		mParams.put("f_informe", new Timestamp((new Date()).getTime()));
		mParams.put("numreq", this.managedForm.getDataFieldValue("CG_CONTRATO"));

		return mParams;
	}

}
