package com.opentach.client.labor.modules.report;

import java.awt.Color;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.CellRenderer.CellRendererColorManager;
import com.ontimize.jee.common.tools.Pair;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.labor.laboral.ILaborService;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.gui.field.table.UTable;

public class IMInformeLaboral extends IMReportRoot {
	private static final Color	ERRORCOLOR	= new Color(0xff, 0x66, 0x00);
	@FormComponent(attr = "Table")
	private UTable	table;

	public IMInformeLaboral() {
		super("Tabla", "informe_laboral");
		this.dateEntity = "EUFecha";
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_laboral.xml", "EInformeLaboral");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD));
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.IDCONDUCTOR_FIELD, true);
		this.table.setCellRendererColorManager(new CellRendererColorManager() {

			@Override
			public Color getForeground(JTable t, int row, int col, boolean sel) {
				return null;
			}

			@Override
			public Color getBackground(JTable t, int row, int col, boolean sel) {
				if ("MANUAL".equals(t.getModel().getValueAt(row, t.getColumn("ORIGEN").getModelIndex()))) {
					return IMInformeLaboral.ERRORCOLOR;
				}
				return null;
			}
		});

	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, null);
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, null);
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECINI);
		this.managedForm.enableDataField(OpentachFieldNames.FILTERFECFIN);
	}

	protected void createReports() {
	}

	public Pair<Date, Date> computeFilterDates() {
		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
		return new Pair<>(fecIni, fecFin);
	}

	@Override
	protected Hashtable<String, Object> getParams(String title, String delegCol) {
		return null;
	}

	@Override
	public void refreshTables() throws Exception {
		if (!this.comprobacionFechasFiltro(false)) {
			return;
		}
		final AbstractOpentachClientLocator ocl = (AbstractOpentachClientLocator) this.formManager.getReferenceLocator();
		Pair<Date, Date> computeFilterDates = this.computeFilterDates();
		String companyCif = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD);
		Object driverId = this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD);
		if (driverId instanceof SearchValue) {
			driverId = ((SearchValue) driverId).getValue();
		} else {
			driverId = Arrays.asList(driverId);
		}
		EntityResult er = ocl.getRemoteService(ILaborService.class).queryLaborReport((List<Object>) driverId, companyCif, computeFilterDates.getFirst(),
				computeFilterDates.getSecond(),
				ocl.getSessionId());
		SwingUtilities.invokeLater(() -> FIMUtils.setTableValue(this.table, er));
	}

}
