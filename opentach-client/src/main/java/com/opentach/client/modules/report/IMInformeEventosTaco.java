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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.DataComponentGroup;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeEventosTaco extends IMReportRoot {

	private static final Logger	logger	= LoggerFactory.getLogger(IMInformeEventosTaco.class);

	Vector<Object>	v	= new Vector<Object>();

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				IMInformeEventosTaco.this.createReports();
			}
		});
	}

	public IMInformeEventosTaco() {
		super("EInformeEventosTaco", "informe_eventos_tacografo");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD, OpentachFieldNames.FECFIN_FIELD));
		mEntityReport.put("informe_eventos_tacografo", "EInformeEventosTaco");
		this.setEntidadesInformes(mEntityReport);
		this.v.add("EXCESOS_VELOCIDAD");
		this.v.add("CONTROLES");
		this.v.add("CALIBRADOS");

	}

	@Override
	public void setInitialState() {
		this.setDateInterval();
		super.setInitialState();
	}

	protected void setDateInterval() {
		try {
			DateDataField cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
			Date d = new Date();
			cf.setValue(DateUtil.addDays(d, -28));
			cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
			cf.setValue(d);
		} catch (Exception cex) {
		}
	}

	protected void createReports() {

		JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");

		final JRReportDescriptor jrd = jpm.getDataMap().get(33);

		final Map<String, Object> params = this.getParams(jrd.getDscr(), jrd.getDelegCol());
		final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(this.managedForm);
		final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
		if (jrd != null) {
			final List<JRReportDescriptor> lReports = jrd.getLReports();
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
						MessageManager.getMessageManager().showMessage(null, "M_NO_SE_HAN_ENCONTRADO_DATOS", MessageType.WARNING, (Object[]) null, true);
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
		Map<String, Object> htRow = cCif.getCodeValues(cif);
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
		mParams.put("f_fin", new Timestamp(fecFin.getTime() + (24 * 3600000)));
		mParams.put("locale", ApplicationManager.getLocale());

		int i = 1;
		DataComponentGroup filtergroup;
		String auxFilter = " ( GRUPO = ";
		while ((filtergroup = (DataComponentGroup) this.managedForm.getElementReference(this.filtergroupname + "." + i)) != null) {
			Vector<String> attrs = filtergroup.getAttributes();
			int j = 0;
			int k = 0;
			for (Enumeration<String> enumeration = attrs.elements(); enumeration.hasMoreElements();) {
				String attr = enumeration.nextElement();
				DataField campo = (DataField) this.managedForm.getDataFieldReference(attr);
				if (campo != null) {
					boolean b = ((Integer) campo.getValue()).intValue() == 0 ? false : true;
					if (b) {
						if (k == 0) {
							auxFilter += "'" + this.v.get(j - 1) + "'";
							k++;
						} else {
							auxFilter += " OR GRUPO = '" + this.v.get(j - 1) + "'";
						}

					}
				}
				j++;
			}
			i++;
		}
		auxFilter += " ) AND ";

		mParams.put("pDSCR_FILTER", auxFilter);
		return mParams;
	}

}
