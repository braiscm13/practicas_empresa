package com.opentach.client.modules.inspec;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.modules.chart.IMGraficaCond;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.fim.FIMUtils;

public class IMGestionInspecOpenGraficaActivMouseListener extends MouseAdapter {

	private static final Logger	logger	= LoggerFactory.getLogger(IMGestionInspecOpenGraficaActivMouseListener.class);

	private final Form	form;

	@FormComponent(attr = IMGestionInspec.EINFRACCIONES)
	private Table				tbInfrac;

	protected JDialog			chartdlg;
	protected Form				chartform;

	public IMGestionInspecOpenGraficaActivMouseListener(Form form) {
		super();
		this.form = form;
		FIMUtils.injectAnnotatedFields(this, form);
	}

	public Form getForm() {
		return this.form;
	}

	public IFormManager getFormManager() {
		return this.form.getFormManager();
	}

	public IMGestionInspec getFIM() {
		return (IMGestionInspec) this.form.getInteractionManager();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			if (e.getClickCount() == 2) {
				int[] i = this.tbInfrac.getSelectedRows();
				if (i.length == 1) {
					Hashtable reg = this.tbInfrac.getRowData(i[0]);
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
					this.openGraficaActiv(start, pEnd);
				}
			}

		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, IMGestionInspecOpenGraficaActivMouseListener.logger);
		}
	}

	protected void openGraficaActiv(Date from, Date to) throws Exception {
		if (this.chartdlg == null) {
			this.chartform = this.getFormManager().getFormCopy("formGraficaCond.xml");
			this.chartform.getInteractionManager().setInitialState();
			this.chartform.getButton("ANALIZAR").setVisible(false);
			this.chartdlg = this.getFIM().getFormDialog(this.chartform, false);
			Frame fApp = ApplicationManager.getApplication().getFrame();
			Dimension dSize = fApp.getSize();
			this.chartdlg.setSize((dSize.width * 4) / 5, (dSize.height * 4) / 5);
			this.chartdlg.setLocationRelativeTo(fApp);
		}
		this.chartform.getInteractionManager().setInitialState();
		this.chartform.getInteractionManager().setQueryMode();
		// Inserto los valors de las claves y desactivo elcampo.
		String[] fields = { OpentachFieldNames.CIF_FIELD, OpentachFieldNames.IDCONDUCTOR_FIELD };
		for (int i = 0; (fields != null) && (i < fields.length); i++) {
			String ck = fields[i];
			if ( this.getForm().getDataFieldValue(ck)!=null) {
				this.chartform.setDataFieldValue(ck, this.getForm().getDataFieldValue(ck));
			} else {
				this.chartform.setDataFieldValue(ck, ((Vector)this.tbInfrac.getSelectedRowData().get(ck)).get(0));
			}
			this.chartform.disableDataField(ck);
		}
		int rowsel = this.tbInfrac.getSelectedRow();
		Hashtable fila = this.tbInfrac.getRowData(rowsel);
		String idConductor = (String) fila.get(OpentachFieldNames.IDCONDUCTOR_FIELD);
		String cg_contrato = (String) this.getForm().getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
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
		// Muestro todas las infracciones.
		// ((GIFGraficaCond)
		// chartform.getGestorInteraccion()).setInfracciones((ResultEntidad)
		// tblInforme.getValor());

		this.chartdlg.setVisible(true);
	}

}
