package com.opentach.adminclient.modules.files;

import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.ObjectDataField;
import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.BooleanCellRenderer;
import com.ontimize.gui.table.CellEditor;
import com.ontimize.gui.table.StringCellEditor;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.util.notice.BundleCellRenderer;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMListadoRegistro extends IMDataRoot {

	private static final Logger			logger		= LoggerFactory.getLogger(IMListadoRegistro.class);

	@FormComponent(attr = "UP")
	private final CheckDataField		up			= null;
	@FormComponent(attr = "DW")
	private final CheckDataField		dw			= null;
	@FormComponent(attr = "PR")
	private final CheckDataField		pr			= null;

	@FormComponent(attr = OpentachFieldNames.TYPE_FIELD)
	private final ObjectDataField		tipo		= null;
	@FormComponent(attr = IMDataRoot.ULTIMOS_DIAS)
	private final RadioButtonDataField	cLastDays	= null;
	@FormComponent(attr = IMListadoRegistro.EFICHEROS)
	private final Table					tFilePend	= null;

	private static final String			EFICHEROS	= "EFicherosRegistro";

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		UReferenceDataField usuario = null;
		UReferenceDataField cif = null;
		TextDataField nombre = null;
		TextDataField nombreproc = null;
		TextDataField origen = null;

		this.setTableParentKeys(Arrays.asList(new String[] { OpentachFieldNames.IDFILE_FIELD, OpentachFieldNames.TYPE_FIELD, "USUARIO_ALTA" }));
		this.setDateTags(new TimeStampDateTags("F_ALTA"));

		if (this.tFilePend != null) {
			// Editor
			Hashtable<String, String> htParam = new Hashtable<>();
			htParam.put(CellEditor.COLUMN_PARAMETER, "OBRS");
			htParam.put("uppercase", "no");
			StringCellEditor ces = new StringCellEditor(htParam);
			this.tFilePend.setColumnEditor("OBSR", ces);
			this.tFilePend.setRendererForColumn(OpentachFieldNames.TYPE_FIELD, new BundleCellRenderer());
			this.tFilePend.setRendererForColumn("PDA", new BooleanCellRenderer());
			this.tFilePend.setRendererForColumn("ANALIZAR", new BooleanCellRenderer());
		}

		usuario = (UReferenceDataField) f.getDataFieldReference("USUARIO_ALTA");
		cif = (UReferenceDataField) f.getDataFieldReference(OpentachFieldNames.CIF_FIELD);
		nombre = (TextDataField) f.getDataFieldReference("NOMB");
		nombreproc = (TextDataField) f.getDataFieldReference(OpentachFieldNames.FILENAME_PROCESSED_FIELD);
		origen = (TextDataField) f.getDataFieldReference(OpentachFieldNames.IDORIGEN_FIELD);
		ValueChangeListener vclQuery = new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				try {
					if (e.getType() == ValueEvent.USER_CHANGE) {
						IMListadoRegistro.this.consultar();
					}
				} catch (Exception error) {
					MessageManager.getMessageManager().showExceptionMessage(error, IMListadoRegistro.logger);
				}
			}
		};
		if ((this.up != null) && (this.pr != null) && (this.dw != null)) {
			this.up.addValueChangeListener(vclQuery);
			this.pr.addValueChangeListener(vclQuery);
			this.dw.addValueChangeListener(vclQuery);
		}
		if (usuario != null) {
			usuario.addValueChangeListener(vclQuery);
		}

		if (cif != null) {
			cif.addValueChangeListener(vclQuery);
		}
		if (nombre != null) {
			nombre.addValueChangeListener(vclQuery);
		}
		if (nombreproc != null) {
			nombreproc.addValueChangeListener(vclQuery);
		}
		if (origen != null) {
			origen.addValueChangeListener(vclQuery);
		}
	}

	protected void consultar() throws Exception {
		this.setDateTags(new TimeStampDateTags("F_ALTA"));
		Vector<Object> v = new Vector<Object>(3);
		if (this.up.isSelected()) {
			v.add("UP");
		}
		if (this.dw.isSelected()) {
			v.add("DW");
		}
		if (this.pr.isSelected()) {
			v.add("PR");
		}
		SearchValue vb = new SearchValue(SearchValue.IN, v);
		if (v.size() > 0) {
			this.tipo.setValue(vb);
		} else {
			this.tipo.setValue(null);
		}
		if (this.tFilePend != null) {
			this.doOnQuery();
		}
	}

	@Override
	protected void ultimosDatos() {
		if ((this.cLastDays != null) && this.cLastDays.isSelected()) {
			this.managedForm.disableDataField(OpentachFieldNames.FILTERFECINI);
			this.managedForm.disableDataField(OpentachFieldNames.FILTERFECFIN);
			Date dEnd = new Date();
			this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, DateUtil.addDays(dEnd, -15));
			this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, dEnd);
		}
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.tFilePend != null) {
			try {
				this.doOnQuery();
			} catch (Exception error) {
				MessageManager.getMessageManager().showExceptionMessage(error, IMListadoRegistro.logger);
			}
		}
	}

	@Override
	public void setInitialState() {
		this.setUpdateMode();
		this.managedForm.setDataFieldValue("UP", Integer.valueOf(1));
		this.managedForm.setDataFieldValue("DW", Integer.valueOf(1));
		this.managedForm.setDataFieldValue("PR", Integer.valueOf(1));
	}

}
