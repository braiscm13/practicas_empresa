package com.opentach.client.modules.inspec;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMDataRoot.TimeStampDateTags;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMGestionInspecBuildReportActionListenerButton extends AbstractActionListenerButton {
	private static final Logger	logger	= LoggerFactory.getLogger(IMGestionInspecBuildReportActionListenerButton.class);
	private Map<String, String>	reportKeyMap;
	private Map<String, String>	reportEntityMap;

	public IMGestionInspecBuildReportActionListenerButton() throws Exception {
		super();
	}

	public IMGestionInspecBuildReportActionListenerButton(Hashtable params) throws Exception {
		super(params);
	}

	public IMGestionInspecBuildReportActionListenerButton(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMGestionInspecBuildReportActionListenerButton(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		this.reportKeyMap = new HashMap<String, String>();
		this.reportKeyMap.put("bReportInfrac", "informe_infrac_cond");
		this.reportKeyMap.put("bReportFallos", "informe_fallos");
		this.reportKeyMap.put("bReportIncidentes", "informe_incidentes");
		this.reportEntityMap = new HashMap<String, String>();
		this.reportEntityMap.put("informe_incidentes", "EIncidentes");
		this.reportEntityMap.put("informe_fallos", "EFallos");
		this.reportEntityMap.put("informe_infrac_cond", "EInformeInfrac");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String key = ((Button) this.getButton()).getKey();
		String report = this.reportKeyMap.get(key);
		if (report != null) {
			final Hashtable<Object, Object> cv = this.getForm().getDataFieldValues(false);
			Hashtable<String, Object> cvfiltro = this.getImGestionInspec().getFilterValues();
			// Añado los valores de los filtros de campos diferentes a las
			// fechas.
			if (cvfiltro != null) {
				cv.putAll(cvfiltro);
			}
			// Añado el filtro por rango de fechas.
			for (TimeStampDateTags dt : this.getImGestionInspec().getDateTags()) {
				List<String> dtags = new ArrayList<String>();
				if (dt.dateinitag != null) {
					dtags.add(dt.dateinitag);
				}
				if (dt.datefintag != null) {
					dtags.add(dt.datefintag);
				}
				Hashtable<String, Object> av = this.getImGestionInspec().getDateFilterValues(dtags);
				if (av != null) {
					cvfiltro.putAll(av);
				}
			}
			cv.putAll(cvfiltro);
			cv.put(IInfractionService.ENGINE_ANALYZER, this.getForm().getDataFieldValue(IInfractionService.ENGINE_ANALYZER));
			final String thinforme = report;
			OperationThread infoInforme = new OperationThread("Generando_informe_") {
				@Override
				public void run() {
					try {
						this.hasStarted = true;
						IMGestionInspecBuildReportActionListenerButton.this.buildReport(thinforme,
								IMGestionInspecBuildReportActionListenerButton.this.getImGestionInspec().getBandVisibility(), cv);
					} catch (Exception ex) {
						IMGestionInspecBuildReportActionListenerButton.this.getForm().message(ex.getMessage(), Form.ERROR_MESSAGE);
					} finally {
						this.hasFinished = true;
					}
				}
			};
			infoInforme.run();
		}

	}

	private IMGestionInspec getImGestionInspec() {
		return (IMGestionInspec) this.getInteractionManager();
	}

	protected void buildReport(String report, Map<String, Object> groupcfg, Hashtable cv) throws Exception {

		if ("informe_fallos".equals(report)) {
			this.getImGestionInspec().createReports(29, "EFallos", null);
		} else if ("informe_incidentes".equals(report)) {
			this.getImGestionInspec().createReports(28, "EIncidentes", null);
		} else if ("informe_infrac_cond".equals(report)) {
			JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");
			final JRReportDescriptor jrd = jpm.getDataMap().get(32);
			final Map<String, Object> params = this.getImGestionInspec().getParams(jrd.getDscr(), jrd.getDelegCol());
			final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(this.getForm());
			final OpentachClientLocator ocl = (OpentachClientLocator) this.getReferenceLocator();
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
							this.getForm().message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
