package com.opentach.client.modules.inspec;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.DataComponentGroup;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.comp.action.AbstractDownloadFileActionListener;
import com.opentach.client.comp.render.MinutesCellRender;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.client.modules.inspec.util.DriverCellRenderer;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.report.util.IJRConstants;
import com.opentach.common.util.concurrent.PoolExecutors;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.tasks.USwingWorker;

import net.sf.jasperreports.engine.data.JRTableModelDataSource;

public class IMGestionInspec extends IMReportRoot {

	private static final Logger		logger			= LoggerFactory.getLogger(IMGestionInspec.class);
	public static final String		EFICHEROS		= "EInspecciones";
	public static final String		EFICHEROSCONDU	= "EInspeccionesCondu";
	public static final String		EINFRACCIONES	= "EInformeInfrac";

	protected String				bandcfgname		= "BANDCFG";

	@FormComponent(attr = "renombrar")
	private CheckDataField			chRenombrar;
	@FormComponent(attr = OpentachFieldNames.IDORIGEN_FIELD)
	private UReferenceDataField	cIdOrigen;
	@FormComponent(attr = "btnDownload")
	private Button					btnDownload;
	@FormComponent(attr = "btnAnalizar")
	private Button					btnAnalizar;
	@FormComponent(attr = IMGestionInspec.EFICHEROS)
	private Table					tbVehic;
	@FormComponent(attr = IMGestionInspec.EFICHEROSCONDU)
	private Table					tbCond;
	@FormComponent(attr = IMGestionInspec.EINFRACCIONES)
	private Table					tbInfrac;
	@FormComponent(attr = "EFallos")
	private Table					tbFallos;
	@FormComponent(attr = "EIncidentes")
	private Table					tbIncidentes;
	@FormComponent(attr = IMGestionInspec.EFICHEROS)
	private Table					tblFicheros;

	public IMGestionInspec() {
		super();
		this.fieldsChain.clear();
		this.fieldsChain.add(OpentachFieldNames.CIF_FIELD);
		this.fieldsChain.add(OpentachFieldNames.CG_CONTRATO_FIELD);
		this.fieldsChain.add(OpentachFieldNames.IDORIGEN_FIELD);
		this.fieldsChain.add(IMGestionInspec.EFICHEROS);
	}

	@Override
	public void registerInteractionManager(Form form, IFormManager gf) {
		super.registerInteractionManager(form, gf);
		AbstractDownloadFileActionListener.installListener(this.managedForm);
		this.setChartEntity(IMGestionInspec.EFICHEROS);
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD, OpentachFieldNames.FECFIN_FIELD));

		ListSelectionListener lseldownload = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel ls = (ListSelectionModel) e.getSource();
				IMGestionInspec.this.btnDownload.setEnabled(!ls.isSelectionEmpty());
			}
		};

		this.tbCond.setCellRendererColorManager(new DriverCellRenderer());
		this.tbCond.getJTable().getSelectionModel().addListSelectionListener(lseldownload);

		this.tbInfrac.getJTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tbInfrac.getJTable().addMouseListener(new IMGestionInspecOpenGraficaActivMouseListener(form));

		this.tbFallos.setRendererForColumn("DURACION_SEGUNDOS", new MinutesCellRender("DURACION_SEGUNDOS", true));

		this.tbIncidentes.setRendererForColumn("DURACION_SEGUNDOS", new MinutesCellRender("DURACION_SEGUNDOS", true));

		this.tbVehic.getJTable().getSelectionModel().addListSelectionListener(lseldownload);

		try {
			ActionListener al = new AbstractDownloadFileActionListener((UButton) this.btnDownload, new Hashtable<Object, Object>()) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Boolean b = (Boolean) IMGestionInspec.this.chRenombrar.getValue();
					String scgContrato = (String) IMGestionInspec.this.cgContract.getValue();
					if (IMGestionInspec.this.tbVehic.isShowing()) {
						this.doOnDownloadFile(IMGestionInspec.EFICHEROS, scgContrato, (b != null) && b.booleanValue());
					} else if (IMGestionInspec.this.tbCond.isShowing()) {
						this.doOnDownloadFile(IMGestionInspec.EFICHEROSCONDU, scgContrato, (b != null) && b.booleanValue());
					}
				}
			};
			this.btnDownload.addActionListener(al);
		} catch (Exception ex) {
			IMGestionInspec.logger.error(null, ex);
		}

		Date end = new Date();
		int nofdays = 28;
		Calendar cal = Calendar.getInstance();
		if (end != null) {
			cal.setTime(end);
			cal.add(Calendar.DATE, -nofdays);
			form.setDataFieldValue(OpentachFieldNames.FILTERFECINI, cal.getTime());
			form.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, end);
		}

	}

	@Override
	protected Hashtable<String, Object> getFilterValues() {
		Hashtable<String, Object> ht = new Hashtable<>();
		Object valor = this.cIdOrigen.getValue();
		if (valor != null) {
			ht.put(OpentachFieldNames.IDORIGEN_FIELD, valor);
		}
		Object cif = this.CIF.getValue();
		if (cif != null) {
			ht.put(OpentachFieldNames.CIF_FIELD, cif);
		}
		return ht;
	}

	@Override
	public void setInitialState() {
		this.setUpdateMode();
		this.initFieldChain();
		this.setUserData();
		this.hideTreeNode();
		this.btnAnalizar.setEnabled(true);
		this.chRenombrar.setEnabled(true);
		this.tbInfrac.setEnabled(false);
		this.tbVehic.setEnabled(false);
		this.tbCond.setEnabled(false);
		this.tbFallos.setEnabled(false);
		this.tbIncidentes.setEnabled(false);
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.managedForm.enableButton("btnRefrescar");
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECINI);
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECFIN);
	}

	@Override
	protected Hashtable<String, Object> getDateFilterValues(List<String> keys) {
		Hashtable<String, Object> cv = new Hashtable<>();
		if ((keys == null) || keys.isEmpty()) {
			return cv;
		}
		DateDataField cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
		Date fIni = (Date) cf.getDateValue();
		cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
		Date fFin = (Date) cf.getDateValue();
		Calendar cal = Calendar.getInstance();
		cal.setTime(fFin);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		fFin = new Timestamp(cal.getTime().getTime());
		SearchValue vbIni = new SearchValue(SearchValue.LESS_EQUAL, fFin);
		SearchValue vbFin = new SearchValue(SearchValue.MORE_EQUAL, fIni);
		cv.put(OpentachFieldNames.FECINI_FIELD, vbIni);
		cv.put(OpentachFieldNames.FECFIN_FIELD, vbFin);
		cv.put(OpentachFieldNames.FILTERFECINI, fIni);
		cv.put(OpentachFieldNames.FILTERFECFIN, fFin);
		return cv;
	}

	@Override
	public void refreshTables() {
		if (!this.comprobacionFechasFiltro(false)) {
			return;
		}
		IMGestionInspec.this.tbCond.setValue(null);
		final List<Table> tables = FIMUtils.getTables(this.managedForm);
		if (tables.isEmpty()) {
			return;
		}
		new USwingWorker<Map<String, EntityResult>, Void>() {

			@Override
			protected Map<String, EntityResult> doInBackground() throws Exception {
				Map<String, EntityResult> res = new HashMap<>();
				ExecutorService executor = PoolExecutors.newCachedThreadPool("IMGestionInspec");
				try {
					IMGestionInspec.this.tbInfrac.setValue(null);
					IMGestionInspec.this.tbInfrac.setEnabled(false);

					Hashtable<String, Object> cvfiltro = IMGestionInspec.this.buildRefreshTablesBaseFilter();

					List<Future<Map<String, EntityResult>>> futures = new ArrayList<>();
					for (Table tb : tables) {
						if ((tb == IMGestionInspec.this.tbCond) || (tb == IMGestionInspec.this.tbInfrac)) {
							continue;
						}
						futures.add(IMGestionInspec.this.refreshTable(cvfiltro, tb, executor));
					}
					for (Future<Map<String, EntityResult>> future : futures) {
						Map<String, EntityResult> map = future.get();
						res.putAll(map);
					}
				} finally {
					executor.shutdownNow();
				}
				return res;
			}

			@Override
			protected void done() {
				super.done();
				try {
					Map<String, EntityResult> uget = this.uget();
					for (Table table : tables) {
						EntityResult res = uget.get(table.getEntityName());
						if ((res == null) || (res.calculateRecordNumber() == 0)) {
							table.setValue(null);
							table.setEnabled(false);
						} else {
							if (table == IMGestionInspec.this.tbInfrac) {
								res = IMGestionInspec.filterEntityResult(res, table.getAttributeList());
							}

							// set value and restore selected row
							if (table.getSelectedRow() > 0) {
								Hashtable selectedRowKeys = table.getRowKeys(table.getSelectedRow());
								table.setValue(res);
								int fila = table.getRowForKeys(selectedRowKeys);
								if (fila >= 0) {
									table.setSelectedRow(fila);
								}
							} else {
								table.setValue(res);
							}
							table.setEnabled(true);
						}
					}

				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, IMGestionInspec.logger);
				}
			}

		}.executeOperation(this.getManagedForm());

	}

	private Future<Map<String, EntityResult>> refreshTable(Hashtable<String, Object> cvfiltro, final Table tb, ExecutorService executor)
			throws Exception {

		final ReferenceLocator buscador = (ReferenceLocator) this.managedForm.getFormManager().getReferenceLocator();

		final Hashtable<String, Object> cv = new Hashtable<>();
		cv.putAll(cvfiltro);

		Vector keys = tb.getParentKeys();
		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			if (cvfiltro.get(key) != null) {
				cv.put(key, cvfiltro.get(key));
			} else if (this.managedForm.getDataFieldValue(key) != null) {
				// Si el valor no esta en el filtro...
				cv.put(key, this.managedForm.getDataFieldValue(key));
			}
		}
		MapTools.safePut(cv, OpentachFieldNames.MATRICULA_FIELD, cv.get(OpentachFieldNames.IDORIGEN_FIELD));
		MapTools.safePut(cv, SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,
				cvfiltro.get(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY));

		final String entityName = tb.getEntityName();
		final Entity ent = buscador.getEntityReference(entityName);

		return executor.submit(new Callable<Map<String, EntityResult>>() {

			@Override
			public Map<String, EntityResult> call() throws Exception {
				EntityResult queryResult = ent.query(cv, tb.getAttributeList(), buscador.getSessionId());
				CheckingTools.checkValidEntityResult(queryResult);
				Map<String, EntityResult> result = new HashMap<String, EntityResult>();
				result.put(entityName, queryResult);
				MapTools.safePut(result, IMGestionInspec.EFICHEROSCONDU, (EntityResult) queryResult.remove(IMGestionInspec.EFICHEROSCONDU));
				MapTools.safePut(result, "EInformeInfrac", (EntityResult) queryResult.remove("EInformeInfrac"));
				return result;
			}
		});
	}

	private Hashtable<String, Object> buildRefreshTablesBaseFilter() {
		Hashtable<String, Object> cvfiltro = this.getFilterValues();
		for (Iterator<TimeStampDateTags> iterator = this.datetags.iterator(); iterator.hasNext();) {
			TimeStampDateTags dt = iterator.next();
			ArrayList<String> dtags = new ArrayList<>();
			if (dt.dateinitag != null) {
				dtags.add(dt.dateinitag);
			}
			if (dt.datefintag != null) {
				dtags.add(dt.datefintag);
			}
			cvfiltro.putAll(this.getDateFilterValues(dtags));
		}
		return cvfiltro;
	}

	private final static EntityResult filterEntityResult(EntityResult erParam, Vector columnList) {
		if (erParam == null) {
			return null;
		}
		EntityResult er = new EntityResult();
		Set sKeys = erParam.keySet();
		for (Object key : sKeys) {
			if (columnList.contains(key)) {
				er.put(key, erParam.get(key));
			}
		}
		return er;
	}

	@Override
	protected Map<String, Object> getBandVisibility() {
		Map<String, Object> bands = new HashMap<String, Object>();
		int i = 1;
		DataComponentGroup filtergroup;
		while ((filtergroup = (DataComponentGroup) this.managedForm.getElementReference(this.bandcfgname + "." + i)) != null) {
			Vector attrs = filtergroup.getAttributes();
			for (Enumeration enumeration = attrs.elements(); enumeration.hasMoreElements();) {
				String attr = (String) enumeration.nextElement();
				DataField campo = (DataField) this.managedForm.getDataFieldReference(attr);
				if (campo != null) {
					bands.put(attr, campo.getValue());
				}
			}
			i++;
		}
		return bands;
	}

	@Override
	protected Hashtable<String, Object> getParams(String title, String delegCol) {
		Hashtable<String, Object> mParams = new Hashtable<String, Object>();
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		mParams.put(IJRConstants.JRCIF, cif);
		Map htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");
		String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
		cgContrato = this.checkContratoFicticio(cgContrato);

		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");

		mParams.put("numreq", cgContrato);
		mParams.put("empresa", empresa);
		mParams.put("title", ApplicationManager.getTranslation(title));
		mParams.put("f_informe", new Timestamp(fecFin.getTime()));
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		mParams.put("f_fin", new Timestamp(fecFin.getTime()));

		try {
			Hashtable<String, Object> av = new Hashtable<String, Object>();
			av.put("numreq", cgContrato);
			av.put("f_inicio", new Timestamp(fecIni.getTime()));
			av.put("f_fin", new Timestamp(fecFin.getTime()));
			av.put("matricula", this.managedForm.getDataFieldValue("IDORIGEN"));
			if ("Informe_incidentes".equals(title)) {
				mParams.put("TIPO_INFORME", "1");
				Entity eResumen = this.formManager.getReferenceLocator().getEntityReference("EIncidentesResumen");
				EntityResult er = eResumen.query(av, new Vector(), this.formManager.getReferenceLocator().getSessionId());
				mParams.put("DATASOURCE_RESUMEN", new JRTableModelDataSource(EntityResultUtils.createTableModel(er)));
			} else if ("Informe_fallos".equals(title)) {
				mParams.put("TIPO_INFORME", "1");
				Entity eResumen = this.formManager.getReferenceLocator().getEntityReference("EFallosResumen");
				EntityResult er = eResumen.query(av, new Vector(), this.formManager.getReferenceLocator().getSessionId());
				mParams.put("DATASOURCE_RESUMEN", new JRTableModelDataSource(EntityResultUtils.createTableModel(er)));
			} else if ("Infracciones_conductor".equals(title)) {
				mParams.put("pDSCR_FILTER",
						"( TIPO = 'ECD' OR TIPO = 'ECI' OR TIPO = 'ECS' OR TIPO = 'ECB' OR TIPO = 'FDD' OR TIPO = 'FDS' OR TIPO = 'FDS45' OR TIPO = 'FDSR' ) AND ");
				Hashtable res = (Hashtable) ((Table) this.managedForm.getElementReference("EInformeInfrac")).getValue();
				Vector vconductor = (Vector) res.get("IDCONDUCTOR");

				String aux = " AND ( IDCONDUCTOR = ";
				for (int i = 0; i < vconductor.size(); i++) {
					if (i == 0) {
						aux += "'" + vconductor.get(i) + "'";
					} else {
						aux += " idconductor = '" + vconductor.get(i) + "'";
					}
					if ((i + 1) != vconductor.size()) {
						aux += "  OR ";
					} else {
						aux += " ) ";
					}
				}
				mParams.put("idconductor", aux);
				mParams.put("SHOW_DETALLES", true);
				mParams.put("DRIVER_FILTER", vconductor);
				// mParams.put("SHOW_MAS_1_INFRAC", true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		mParams.put(IInfractionService.ENGINE_ANALYZER, IMGestionInspec.this.managedForm.getDataFieldValue(IInfractionService.ENGINE_ANALYZER));
		return mParams;
	}

}
