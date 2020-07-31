package com.opentach.adminclient.modules.files;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.ObjectDataField;
import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.gui.field.TextComboDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.CellEditor;
import com.ontimize.gui.table.StringCellEditor;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.comp.action.AbstractDownloadFileActionListener;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.gui.buttons.UButton;

public class IMListadoFicheros extends IMDataRoot {

	private static final Logger		logger		= LoggerFactory.getLogger(IMListadoFicheros.class);
	public static final String		EFICHEROS	= "EFicherosPendientes";

	private Button					btnDownload	= null;
	private Table					tFilePend	= null;
	private CheckDataField			vu			= null;
	private CheckDataField			tc			= null;
	private CheckDataField			da			= null;
	private ObjectDataField			tipo		= null;
	// private CampoComboReferencia usuario = null;
	private TextComboDataField		fecha		= null;
	private TextDataField			origen		= null;
	private RadioButtonDataField	cLastDays	= null;

	@Override
	public void setInitialState() {
		this.setUpdateMode();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		AbstractDownloadFileActionListener.installListener(this.managedForm);
		this.tFilePend = (Table) f.getElementReference(IMListadoFicheros.EFICHEROS);
		this.cLastDays = (RadioButtonDataField) f.getElementReference(IMDataRoot.ULTIMOS_DIAS);
		this.setTableParentKeys(Arrays.asList(new String[] { OpentachFieldNames.IDFILE_FIELD, OpentachFieldNames.TYPE_FIELD /*
		 * , "USUARIO_ALTA"
		 */}));
		this.addDateTags(new TimeStampDateTags("F_DESCARGA_DATOS"));
		// setDateTags(Arrays.asList(new String[] { "F_DESCARGA_DATOS" }));
		this.btnDownload = f.getButton("btnDownload");

		if (this.tFilePend != null) {
			// Editor
			Hashtable htParam = new Hashtable();
			htParam.put(CellEditor.COLUMN_PARAMETER, "OBRS");
			htParam.put("uppercase", "no");
			StringCellEditor ces = new StringCellEditor(htParam);
			this.tFilePend.setColumnEditor("OBSR", ces);
		}
		if (this.btnDownload != null) {
			try {
				ActionListener al = new AbstractDownloadFileActionListener((UButton) this.btnDownload, new Hashtable<Object, Object>()) {
					@Override
					public void actionPerformed(ActionEvent e) {
						this.doOnDownloadFile(IMListadoFicheros.EFICHEROS, null, false);
					}
				};
				this.btnDownload.addActionListener(al);
			} catch (Exception ex) {
				IMListadoFicheros.logger.error(null, ex);
			}
		}
		this.vu = (CheckDataField) f.getDataFieldReference("VU");
		this.tc = (CheckDataField) f.getDataFieldReference("TC");
		this.da = (CheckDataField) f.getDataFieldReference("DA");
		this.tipo = (ObjectDataField) f.getDataFieldReference(OpentachFieldNames.TYPE_FIELD);
		// usuario = (CampoComboReferencia)
		// formularioGestionado.getReferenciaCampo("USUARIO_ALTA");
		this.fecha = (TextComboDataField) f.getDataFieldReference("FECHA_POR");
		this.origen = (TextDataField) f.getDataFieldReference(OpentachFieldNames.IDORIGEN_FIELD);
		if (this.fecha != null) {
			this.fecha.setSelected(1);
		}
		ValueChangeListener vclQuery = new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				try {
					if (e.getType() == ValueEvent.USER_CHANGE) {
						IMListadoFicheros.this.consultar();
					}
				} catch (Exception error) {
					MessageManager.getMessageManager().showExceptionMessage(error, IMListadoFicheros.logger);
				}
			}
		};
		if ((this.tc != null) && (this.vu != null)) {
			this.tc.addValueChangeListener(vclQuery);
			this.vu.addValueChangeListener(vclQuery);
			this.da.addValueChangeListener(vclQuery);
		}
		// if (usuario!=null) {
		// usuario.addValueChangeListener(vclQuery);
		// }
		if (this.fecha != null) {
			this.fecha.addValueChangeListener(vclQuery);
		}
		// TODO: poner o no poner?
		if (this.origen != null) {
			this.origen.addValueChangeListener(vclQuery);
		}
	}

	protected void consultar() throws Exception {
		if (this.fecha.getValue() == null) {
			this.setDateTags(new TimeStampDateTags("F_DESCARGA_DATOS"));
		} else {
			if (((String) this.fecha.getValue()).equals("F_DESCARGA_DATOS")) {
				this.setDateTags(new TimeStampDateTags("F_DESCARGA_DATOS"));
			} else if (((String) this.fecha.getValue()).equals("F_ALTA")) {
				this.setDateTags(new TimeStampDateTags("F_ALTA"));
			} else {
				this.setDateTags(new TimeStampDateTags("F_DESCARGA_DATOS"));
			}
			SearchValue sv = this.getTipoValue();
			this.tipo.setValue(sv);
			if (this.tFilePend != null) {
				this.doOnQuery();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private SearchValue getTipoValue() {
		Vector v = new Vector();
		boolean bVU = this.vu.isSelected();
		boolean bTC = this.tc.isSelected();
		boolean bDA = this.da.isSelected();
		if (!bVU && !bTC && !bDA) {
			return null;
		}
		if (bVU) {
			v.add("VU");
		}
		if (bTC) {
			v.add("TC");
		}
		if (bDA) {
			v.add("DA");
		}
		return new SearchValue(SearchValue.IN, v);
	}

	@Override
	protected void ultimosDatos() {
		if ((this.cLastDays != null) && this.cLastDays.isSelected()) {
			this.managedForm.disableDataField(OpentachFieldNames.FILTERFECINI);
			this.managedForm.disableDataField(OpentachFieldNames.FILTERFECFIN);
			Date end = new Date();
			this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, DateUtil.addDays(end, -15));
			this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, end);
		}
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.tFilePend != null) {
			try {
				this.doOnQuery();
			} catch (Exception error) {
				MessageManager.getMessageManager().showExceptionMessage(error, IMListadoFicheros.logger);
			}
		}
		this.managedForm.enableButton("btnRefrescar");
		this.managedForm.enableButton("btnProcesarPrio");
		this.managedForm.enableButton("btnProcesarNow");
		this.managedForm.enableButton("btnViewTrace");
		this.managedForm.enableButton("btnTryAssign");
		this.managedForm.enableButton("btnDownload");
		this.managedForm.enableButton("btnAbrirFichero");
	}
}
