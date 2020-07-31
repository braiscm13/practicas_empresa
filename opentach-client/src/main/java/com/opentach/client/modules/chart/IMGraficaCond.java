package com.opentach.client.modules.chart;

import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueChangeDataComponent;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.Label;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.comp.action.AbstractAutoSelectDriverVehicleValueChangeListener;
import com.opentach.client.comp.action.AnalizeInfractionsActionListener;
import com.opentach.client.comp.activitychart.ActivityChartControlPanel;
import com.opentach.client.comp.activitychart.ChartDataField;
import com.opentach.client.fim.IIMGraficaCond;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMGraficaCond extends IMDataRoot implements IIMGraficaCond {

	private static final Logger			logger	= LoggerFactory.getLogger(IMGraficaCond.class);

	@FormComponent(attr = "chart")
	protected ChartDataField			chartwpp;
	@FormComponent(attr = "chartControl")
	protected ActivityChartControlPanel	chartControl;
	@FormComponent(attr = "CIF")
	protected UReferenceDataField		creCIF;
	@FormComponent(attr = "NUM_TARJ")
	protected UReferenceDataField	creNumTarj;
	@FormComponent(attr = OpentachFieldNames.IDCONDUCTOR_FIELD)
	protected UReferenceDataField		creIdCond;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD, OpentachFieldNames.FECFIN_FIELD));
		PaisesMouseListener paisesMouseListener = new PaisesMouseListener(this.chartwpp);
		if ((paisesMouseListener != null) && (this.chartwpp != null)) {
			this.chartwpp.getChart().addMouseListener(paisesMouseListener);
			this.chartwpp.getChart().addMouseMotionListener(paisesMouseListener);
			this.chartwpp.getChart().addMouseListener(new ActivityRulerMouseListener(this.chartwpp));
		}

		ValueChangeListener valueChangeListener = new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent ve) {
				try {
					if (ve.getType() == ValueEvent.USER_CHANGE) {
						IMGraficaCond.this.refreshTables();
					}
				} catch (Exception ex) {
					IMGraficaCond.logger.error(null, ex);
				}
			}
		};
		this.creCIF.addValueChangeListener(valueChangeListener);
		if (this.creNumTarj != null) {
			this.creNumTarj.addValueChangeListener(valueChangeListener);
		}
		this.creIdCond.addValueChangeListener(valueChangeListener);

		ValueChangeListener listener = new AbstractAutoSelectDriverVehicleValueChangeListener(IMGraficaCond.this) {

			@Override
			protected void onChange() throws Exception {
				IMGraficaCond.this.refreshTables();
			}

		};
		((ValueChangeDataComponent) this.cgContract).addValueChangeListener(listener);
		if (f.getButton("ANALIZAR") != null) {
			f.getButton("ANALIZAR").addActionListener(new AnalizeInfractionsActionListener(false));
		}
	}

	@Override
	public void setInfracciones(EntityResult res) {
		this.chartwpp.setEntityData("EInfracciones", res);
		this.repaintChart();
	}

	public void setActividades(EntityResult res) {
		this.chartwpp.setEntityData("EActividades", res);
		this.repaintChart();
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECINI);
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECFIN);
		this.managedForm.enableButtons();
		this.chartControl.checkButtonStatus();
	}

	@Override
	public void refreshTables() throws Exception {

		this.chartwpp.setEntityData("EActividades", new EntityResult());
		this.chartwpp.setEntityData("EResumenActividades", new EntityResult());
		this.chartwpp.setEntityData("EInfracciones", new EntityResult());
		IMGraficaCond.this.chartControl.setDateRange(null, null);
		this.chartwpp.clearData();

		try {
			String numReq = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
			String idConductor = null;
			if (this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD) instanceof String) {
				idConductor = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD);
			}else {
				idConductor = (String)((Vector)((SearchValue) this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD)).getValue()).get(0);
			}


			UReferenceDataField rcdf = (UReferenceDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.NUMTARJETA_FIELD);
			BigDecimal bc = (BigDecimal) this.managedForm.getDataFieldValue(OpentachFieldNames.NUMTARJETA_FIELD);
			String numTarj = null;
			if (bc != null) {
				Map<String, Object> av = rcdf.getValuesToCode(bc);
				numTarj = (String) av.get("NUM_TARJETA");
				numTarj += (String) av.get("INDICE_CONS") + (String) av.get("INDICE_RENOV") + (String) av.get("INDICE_SUST");
			}

			Date from = (Date) this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECINI);
			Date to = (Date) this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECFIN);
			if ((from == null) || (to == null)) {
				return;
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(from);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			from = cal.getTime();
			cal.setTime(to);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			to = cal.getTime();

			if ((numReq != null) && (idConductor != null) && (from != null) && (to != null) && from.before(to)) {

				// pongo el valor de la fecha de descarga de datos
				Label et = (Label) this.managedForm.getElementReference("l_grafica_activ");
				DateDataField field = (DateDataField) this.managedForm.getDataFieldReference("F_DESCARGA_DATOS");
				if (field != null) {
					ReferenceLocator buscador = (ReferenceLocator) this.managedForm.getFormManager().getReferenceLocator();

					Entity ent = buscador.getEntityReference("EProcesadoFicheros");

					Hashtable<String, Object> cv = new Hashtable<String, Object>();
					cv.put("IDORIGEN", idConductor);
					cv.put("CG_CONTRATO", this.managedForm.getDataFieldValue("CG_CONTRATO"));
					if (numTarj != null) {
						cv.put("NUM_TARJ", numTarj);
					}
					Vector<Object> v = new Vector<Object>();
					v.add("F_DESCARGA_DATOS");
					v.add("USUARIO_DESCARGA");
					EntityResult res = ent.query(cv, v, buscador.getSessionId());
					if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
						if (res.isEmpty()) {
							et.setText("No hay datos");
							return;
						}

						Vector<Object> vf_descarga = (Vector<Object>) res.get("F_DESCARGA_DATOS");
						Vector<Object> vf_usuario = (Vector<Object>) res.get("USUARIO_DESCARGA");
						Date d = null;
						String usuario = null;
						for (int i = 0; i < vf_descarga.size(); i++) {
							if (d == null) {
								d = (Date) vf_descarga.get(i);
								usuario = (String) vf_usuario.get(i);
							} else {
								if ((vf_descarga.get(i) != null) && d.before((Date) vf_descarga.get(i))) {
									d = (Date) vf_descarga.get(i);
									usuario = (String) vf_usuario.get(i);
								}
							}
						}
						et.setText("Última Descarga: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(d) + "  Usuario Descarga: " + usuario);
					}
				}

				//
				EntityResult activs = this.queryActivities(numReq, idConductor, numTarj, from, to);
				EntityResult ractiv = activs;

				EntityResult periodosTrabajo = new EntityResult();
				if (activs.containsKey("ACTIVIDADES")) {
					ractiv = (EntityResult) ((Vector) activs.get("ACTIVIDADES")).get(0);
					periodosTrabajo = (EntityResult) ((Vector) activs.get("PERIODOS")).get(0);
				}
				int numRecords = ractiv.calculateRecordNumber();
				if (numRecords > 0) {
					Date begin = (Date) ((Vector) ractiv.get("FECINI")).get(0);
					Date end = (Date) ((Vector) ractiv.get("FECFIN")).get(numRecords - 1);
					IMGraficaCond.this.chartControl.setDateRange(begin, end);
				}

				EntityResult resumenact = this.getResumenActividades(ractiv, numReq, idConductor, from, to);
				if (!"managergraficaactivcertifactiv28".equals(this.managedForm.getFormTitle()) && (!this.getFormManager().getApplication().getClass().getName().equals("com.opentach.client.OpentachClientPrimafrio"))) {
					ractiv.remove("ORIGEN");
				}
				this.chartwpp.setEntityData("EActividades", ractiv);
				this.chartwpp.setEntityData("EResumenActividades", resumenact);
				if ((periodosTrabajo != null) && (periodosTrabajo.calculateRecordNumber() > 0)) {
					periodosTrabajo = EntityResultTools.doSort(periodosTrabajo, "FECINI");

				}
				if ((ractiv != null) && (numRecords > 0)) {
					this.chartwpp.setEntityData("EPeriodosTrabajo", periodosTrabajo);
				} else {
					this.chartwpp.setEntityData("EPeriodosTrabajo", null);
				}
				MouseListener[] ml = this.chartwpp.getChart().getMouseListeners();
				for (int i = 0; i < ml.length; i++) {
					if (ml[i] instanceof ActivityRulerMouseListener) {
						ActivityRulerMouseListener ruler = (ActivityRulerMouseListener) ml[i];
						ruler.firstStartDate = null;
						ruler.secondStartDate = null;
					}
				}
			}
		} finally {
			this.refreshChart();

		}
	}

	@Override
	public void refreshChart() {
		try {
			if (this.chartwpp == null) {
				return;
			}
			if ((this.chartControl.getStartDate() != null) && (this.chartControl.getEndDate() != null) && !this.chartControl.getStartDate()
					.before(this.chartControl.getEndDate())) {
				if (this.chartwpp.getChart() != null) {
					this.chartwpp.getChart().removeAllData();
				}
			} else {
				Date fecIni = this.chartControl.getChartStartDate();
				Date fecFin = this.chartControl.getChartEndDate();
				this.chartControl.checkButtonStatus();
				this.chartControl.setTitle(fecIni, fecFin);

				if ((fecIni != null) && (fecFin != null) && fecIni.before(fecFin)) {
					String idConductor = null;
					if (this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD) instanceof String) {
						idConductor = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD);
					}else {
						idConductor = (String)((Vector)((SearchValue) this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD)).getValue()).get(0);
					}

					if ((fecIni == null) || (fecFin == null) || (idConductor == null)) {
						return;
					}

					if ((this.chartwpp != null) && this.chartwpp.isVisible()) {
						this.chartwpp.configurarChart(fecIni, fecFin, 7, "FERRY/OUT_TRAIN", "INFRACCIONES");

					}
				} else if (this.chartwpp.getChart() != null) {
					this.chartwpp.getChart().removeAllData();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			this.managedForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
		}
	}

	@Override
	public void doOnQuery() throws Exception {
		this.refreshTables();
		this.repaintChart();
	}

	private void repaintChart() {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				IMGraficaCond.this.refreshChart();
				IMGraficaCond.this.chartwpp.updateChart();
				// TODO verificar que esto es necesario
				IMGraficaCond.this.chartwpp.invalidate();
				IMGraficaCond.this.chartwpp.revalidate();
				IMGraficaCond.this.chartwpp.validate();
				IMGraficaCond.this.chartwpp.doLayout();
				IMGraficaCond.this.chartwpp.repaint();

			}
		});
	}

	private EntityResult queryActivities(String numReq, String idConductor, String numTarj, Date from, Date to) throws Exception {
		ReferenceLocator locator = (ReferenceLocator) this.managedForm.getFormManager().getReferenceLocator();
		Entity ent = locator.getEntityReference("EInformeActivCond");
		Hashtable<String, Object> cv = new Hashtable<String, Object>();
		cv.put(OpentachFieldNames.NUMREQ_FIELD, numReq);
		cv.put(OpentachFieldNames.IDCONDUCTOR_FIELD, idConductor);
		if (numTarj != null) {
			cv.put(OpentachFieldNames.NUMTARJETA_FIELD, numTarj);
		}
		Vector<Object> v = new Vector<Object>(2);
		v.add(from);
		v.add(to);
		cv.put(OpentachFieldNames.FECINI_FIELD, new SearchValue(SearchValue.BETWEEN, v));
		cv.put(OpentachFieldNames.FECFIN_FIELD, new SearchValue(SearchValue.BETWEEN, v));

		// para que traiga los periodos tb
		cv.put("PERIODOS", "1");
		return ent.query(cv, EntityResultTools.attributes("TPACTIVIDAD", "FECINI", "FECFIN", "MATRICULA", "MINUTOS", "TRANS_TREN", "ORIGEN", "REGIMEN", "ESTADO_TRJ_RANURA",
				"FUERA_AMBITO", "IDCONDUCTOR", "NUMREQ", "DSCR_ACT"), locator.getSessionId());
	}

	private EntityResult getResumenActividades(EntityResult activs, String numReq, String idConductor, Date from, Date to) {
		Hashtable<String, Object> htFila = null;
		int count = activs == null ? 0 : activs.calculateRecordNumber();
		EntityResult res = new EntityResult();
		Map mDates = new HashMap<String, Object>();
		if (count == 0) {
			res = this.fillEmptyDates(res, from, to, mDates);
			return res;
		}
		// inicializo
		res.put("FEC_COMIENZO", new Vector<Object>());
		res.put("TDESCANSO", new Vector<Object>());
		res.put("TDISPONIBILIDAD", new Vector<Object>());
		res.put("TTRABAJO", new Vector<Object>());
		res.put("TCONDUCCION", new Vector<Object>());
		res.put("TINDEFINIDA", new Vector<Object>());
		int tpConduccion = 0;
		int tpDescanso = 0;
		int tpTrabajo = 0;
		int tpDiponibilidad = 0;
		int tpIndefinida = 0;
		Number nTipo = null;
		Number nMinutos = null;
		Date lFecIni = null;
		Date dFecIni = null;
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < count; i++) {
			htFila = activs.getRecordValues(i);
			dFecIni = (Date) htFila.get("FECINI");
			// Trunco fec comienzo
			cal.setTime(dFecIni);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			dFecIni = cal.getTime();
			nTipo = (Number) htFila.get("TPACTIVIDAD");
			nMinutos = (Number) htFila.get("MINUTOS");
			if ((lFecIni == null) || dFecIni.equals(lFecIni)) {
				if ((nTipo != null) && (nMinutos != null)) {
					switch (nTipo.intValue()) {
						case 1: // PAUSA/DESCANSO
							tpDescanso += nMinutos.intValue();
							break;
						case 2: // DISPONIBILIDAD
							tpDiponibilidad += nMinutos.intValue();
							break;
						case 3: // TRABAJO
							tpTrabajo += nMinutos.intValue();
							break;
						case 4: // CONDUCCION
							tpConduccion += nMinutos.intValue();
							break;
						case 5: // CONDUCCION
							tpIndefinida += nMinutos.intValue();
							break;
					}
				}
			} else {
				// añado registro
				Hashtable<String, Object> row = new Hashtable<String, Object>(4);
				row.put("FEC_COMIENZO", lFecIni);
				row.put("TDESCANSO", new BigDecimal((double) tpDescanso));
				row.put("TDISPONIBILIDAD", new BigDecimal((double) tpDiponibilidad));
				row.put("TTRABAJO", new BigDecimal((double) tpTrabajo));
				row.put("TCONDUCCION", new BigDecimal((double) tpConduccion));
				row.put("TINDEFINIDA", new BigDecimal((double) tpIndefinida));
				res.addRecord(row);
				mDates.put(lFecIni, row);
				// reseteo
				tpConduccion = 0;
				tpDescanso = 0;
				tpTrabajo = 0;
				tpDiponibilidad = 0;
				tpIndefinida = 0;

				if ((nTipo != null) && (nMinutos != null)) {
					switch (nTipo.intValue()) {
						case 1: // PAUSA/DESCANSO
							tpDescanso += nMinutos.intValue();
							break;
						case 2: // DISPONIBILIDAD
							tpDiponibilidad += nMinutos.intValue();
							break;
						case 3: // TRABAJO
							tpTrabajo += nMinutos.intValue();
							break;
						case 4: // CONDUCCION
							tpConduccion += nMinutos.intValue();
							break;
						case 5: // INDEFINIDA
							tpIndefinida += nMinutos.intValue();
							break;
					}
				}
			}
			lFecIni = dFecIni;
		}
		// añado registro
		Hashtable<String, Object> row = new Hashtable<String, Object>(6);
		row.put("FEC_COMIENZO", dFecIni);
		row.put("TDESCANSO", new BigDecimal((double) tpDescanso));
		row.put("TDISPONIBILIDAD", new BigDecimal((double) tpDiponibilidad));
		row.put("TTRABAJO", new BigDecimal((double) tpTrabajo));
		row.put("TCONDUCCION", new BigDecimal((double) tpConduccion));
		row.put("TINDEFINIDA", new BigDecimal((double) tpIndefinida));
		res.addRecord(row);
		mDates.put(dFecIni, row);
		// si en un dia no hay huecos, añado resumen vacío
		res = this.fillEmptyDates(res, from, to, mDates);
		return res;
	}

	private EntityResult fillEmptyDates(EntityResult in, Date from, Date to, Map mDates) {
		EntityResult out = new EntityResult();
		if ((from == null) || (to == null) || from.after(to)) {
			return out;
		}
		out.put("FEC_COMIENZO", new Vector<Object>());
		out.put("TDESCANSO", new Vector<Object>());
		out.put("TDISPONIBILIDAD", new Vector<Object>());
		out.put("TTRABAJO", new Vector<Object>());
		out.put("TCONDUCCION", new Vector<Object>());
		out.put("TINDEFINIDA", new Vector<Object>());
		Calendar cal = Calendar.getInstance();
		cal.setTime(from);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date dFecIni = cal.getTime();
		Hashtable<String, Object> htFila = null;
		final BigDecimal bZero = new BigDecimal("0");
		while (dFecIni.before(to)) {
			if (mDates.containsKey(dFecIni)) {
				htFila = (Hashtable<String, Object>) mDates.get(dFecIni);
			} else {
				htFila = new Hashtable<String, Object>(5);
				htFila.put("FEC_COMIENZO", dFecIni);
				htFila.put("TDESCANSO", bZero);
				htFila.put("TDISPONIBILIDAD", bZero);
				htFila.put("TTRABAJO", bZero);
				htFila.put("TCONDUCCION", bZero);
				htFila.put("TINDEFINIDA", bZero);
			}
			out.addRecord(htFila);
			cal.setTime(dFecIni);
			cal.add(Calendar.DATE, 1);
			dFecIni = cal.getTime();
		}
		return out;
	}
}
