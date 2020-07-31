package com.opentach.client.modules.files;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.DateTools;
import com.opentach.client.comp.action.AbstractDownloadFileActionListener;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.gui.buttons.UButton;

public class IMFicherosTGD extends IMDataRoot {

	private static final Logger	logger		= LoggerFactory.getLogger(IMFicherosTGD.class);
	private static final String	EFICHEROS	= "EFicherosSubidos";

	private CheckDataField		chRenombrar	= null;
	private Button				btnDownload	= null;

	public IMFicherosTGD() {
		super();
		this.fieldsChain.clear();
		this.fieldsChain.add(OpentachFieldNames.CIF_FIELD);
		this.fieldsChain.add(OpentachFieldNames.CG_CONTRATO_FIELD);
		this.fieldsChain.add(IMFicherosTGD.EFICHEROS);
		this.setDateTags(new TimeStampDateTags("F_ALTA"));
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		AbstractDownloadFileActionListener.installListener(this.managedForm);
		// this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD, OpentachFieldNames.FECFIN_FIELD));
		this.chRenombrar = (CheckDataField) this.managedForm.getDataFieldReference("renombrar");
		Table tblFicheros = (Table) this.managedForm.getDataFieldReference(IMFicherosTGD.EFICHEROS);
		if (tblFicheros != null) {
			Button btn = this.managedForm.getButton("btnDownload");
			if (btn == null) {
				Hashtable cv = new Hashtable();
				cv.put("icon", "com/opentach/client/rsc/server_to_client16.png");
				cv.put("text", ApplicationManager.getTranslation("DESCARGAR_FICHEROS_CSD"));
				cv.put("tip", ApplicationManager.getTranslation("DESCARGAR_FICHEROS_CSD"));
				btn = new UButton(cv);
				JPanel p = new JPanel();
				p.setPreferredSize(new Dimension(120, 35));
				p.add(btn);
				tblFicheros.addComponentToControls(p);
			}
			if (btn != null) {
				try {
					ActionListener al = new AbstractDownloadFileActionListener((UButton) btn, new Hashtable<Object, Object>()) {
						@Override
						public void actionPerformed(ActionEvent e) {
							Boolean b = (Boolean) IMFicherosTGD.this.chRenombrar.getValue();
							String scgContrato = (String) IMFicherosTGD.this.cgContract.getValue();
							this.doOnDownloadFile(IMFicherosTGD.EFICHEROS, scgContrato, (b != null) && b.booleanValue());
						}
					};
					btn.addActionListener(al);
				} catch (Exception e) {
					IMFicherosTGD.logger.error(null, e);
				}
			}
			this.btnDownload = btn;
			tblFicheros.getJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					ListSelectionModel ls = (ListSelectionModel) e.getSource();
					IMFicherosTGD.this.btnDownload.setEnabled(!ls.isSelectionEmpty());
				}
			});
		}
		Date end = new Date();
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, DateUtil.addDays(end, -28));
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, end);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
	}

	// RAFA: sobrecargado para que añada fechas null
	@Override
	protected Map<String, Object> getDateFilterValues(List keys) {
		Map<String, Object> rtn = super.getDateFilterValues(keys);
		// En los ficheros hay que mostrar tambien los que no posean fechas
		// inicio -fin.
		DateDataField cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
		Date selectedfecini = (Date) cf.getDateValue();
		cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
		Date selectedfecfin = DateTools.addDays((Date) cf.getDateValue(), 1);

		BasicExpression be = null;
		BasicExpression be1 = null;
		BasicExpression be2 = null;
		BasicExpression beAux = null;
		Hashtable rtn2 = new Hashtable();

		if (rtn.containsKey(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY)) {
			be = (BasicExpression) rtn.get(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY);
			beAux = be;
			for (Iterator iter = keys.iterator(); iter.hasNext();) {
				beAux = new SQLStatementBuilder.BasicExpression(beAux, BasicOperator.OR_OP, new SQLStatementBuilder.BasicExpression(new BasicField(
						(String) iter.next()), BasicOperator.NULL_OP, null));
			}
			rtn2.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, beAux);
		} else {
			for (Iterator iter = rtn.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				SearchValue vb = (SearchValue) rtn.get(key);
				if (vb != null) {
					if (vb.getCondition() == SearchValue.LESS_EQUAL) {
						beAux = new SQLStatementBuilder.BasicExpression(new BasicField(key), BasicOperator.LESS_EQUAL_OP, vb.getValue());
					} else if (vb.getCondition() == SearchValue.MORE_EQUAL) {
						beAux = new SQLStatementBuilder.BasicExpression(new BasicField(key), BasicOperator.MORE_EQUAL_OP, vb.getValue());
					} else {
						be1 = new SQLStatementBuilder.BasicExpression(new BasicField(key), BasicOperator.LESS_EQUAL_OP, selectedfecfin);
						be2 = new SQLStatementBuilder.BasicExpression(new BasicField(key), BasicOperator.MORE_EQUAL_OP, selectedfecini);
						beAux = new SQLStatementBuilder.BasicExpression(be1, BasicOperator.AND_OP, be2);
					}
					beAux = new SQLStatementBuilder.BasicExpression(beAux, BasicOperator.OR_OP, new SQLStatementBuilder.BasicExpression(
							new BasicField(key), BasicOperator.NULL_OP, null));
					if (be == null) {
						be = beAux;
					} else {
						be = new SQLStatementBuilder.BasicExpression(beAux, BasicOperator.AND_OP, be);
					}
				}
			}
			rtn2.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, be);
		}
		return rtn2;
	}
}
