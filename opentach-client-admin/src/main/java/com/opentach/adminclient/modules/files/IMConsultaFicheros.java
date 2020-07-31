package com.opentach.adminclient.modules.files;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.BooleanCellRenderer;
import com.ontimize.gui.table.Table;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.notice.BundleCellRenderer;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.IOpentachReportService;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMConsultaFicheros extends UBasicFIM {

	protected static final String		TBLFICHEROS		= "EFicherosSubidos";

	protected Table						tbRegistro		= null;
	protected UReferenceDataField	rdfUser			= null;
	protected RadioButtonDataField		cdfUltimosDias	= null;
	protected RadioButtonDataField		cdfRangoFecha	= null;
	protected DateDataField				dfFecIni		= null;
	protected DateDataField				dfFecFin		= null;
	protected TextDataField				tdfName			= null;
	protected TextDataField				tdfNameProc		= null;
	protected Button					bRefresh		= null;
	protected Button					bInforme		= null;

	@Override
	public void registerInteractionManager(Form formulario, IFormManager gestorForms) {
		super.registerInteractionManager(formulario, gestorForms);
		this.bRefresh = formulario.getButton("btnRefrescar");
		if (this.bRefresh != null) {
			this.bRefresh.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMConsultaFicheros.this.refreshTable();
				}
			});
		}

		this.bInforme = formulario.getButton("btnInforme");
		if (this.bInforme != null) {
			this.bInforme.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMConsultaFicheros.this.createReports();
				}
			});
		}

		this.dfFecIni = (DateDataField) formulario.getDataFieldReference("FILTERFECINI");
		this.dfFecFin = (DateDataField) formulario.getDataFieldReference("FILTERFECFIN");
		this.tdfName = (TextDataField) formulario.getDataFieldReference("NOMB");
		this.tdfNameProc = (TextDataField) formulario.getDataFieldReference(OpentachFieldNames.FILENAME_PROCESSED_FIELD);
		this.cdfUltimosDias = (RadioButtonDataField) formulario.getDataFieldReference("ULTIMOS_DIAS");
		this.cdfRangoFecha = (RadioButtonDataField) formulario.getDataFieldReference("RANGO_FECHA");
		if ((this.cdfUltimosDias != null) && (this.cdfRangoFecha != null)) {
			this.cdfUltimosDias.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					if ((IMConsultaFicheros.this.cdfUltimosDias != null) && IMConsultaFicheros.this.cdfUltimosDias.isSelected()
							&& (e.getType() == ValueEvent.USER_CHANGE)) {
						Date dFin = new Date();
						IMConsultaFicheros.this.dfFecFin.setValue(dFin);
						IMConsultaFicheros.this.dfFecIni.setValue(DateUtil.addDays(dFin, -15));
						IMConsultaFicheros.this.dfFecIni.setEnabled(false);
						IMConsultaFicheros.this.dfFecFin.setEnabled(false);
						IMConsultaFicheros.this.dfFecIni.setRequired(false);
						IMConsultaFicheros.this.dfFecFin.setRequired(false);
					}
				}
			});
			this.cdfRangoFecha.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					if ((IMConsultaFicheros.this.cdfRangoFecha != null) && IMConsultaFicheros.this.cdfRangoFecha.isSelected()
							&& (e.getType() == ValueEvent.USER_CHANGE)) {
						IMConsultaFicheros.this.dfFecFin.setValue(null);
						IMConsultaFicheros.this.dfFecIni.setValue(null);
						IMConsultaFicheros.this.dfFecIni.setEnabled(true);
						IMConsultaFicheros.this.dfFecFin.setEnabled(true);
						IMConsultaFicheros.this.dfFecIni.setRequired(true);
						IMConsultaFicheros.this.dfFecFin.setRequired(true);
					}
				}
			});
		}
		this.tbRegistro = (Table) formulario.getElementReference(IMConsultaFicheros.TBLFICHEROS);
		this.rdfUser = (UReferenceDataField) formulario.getElementReference("USUARIO_ALTA");
		if ((this.rdfUser != null) && (this.tdfName != null) && (this.tdfNameProc != null) && (this.dfFecFin != null) && (this.dfFecIni != null)) {
			ValueChangeListener vcl = new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent ve) {
					if (ve.getType() == ValueEvent.USER_CHANGE) {
						IMConsultaFicheros.this.refreshTable();
					}
				}
			};
			this.rdfUser.addValueChangeListener(vcl);
			this.tdfName.addValueChangeListener(vcl);
			this.tdfNameProc.addValueChangeListener(vcl);
			this.dfFecFin.addValueChangeListener(vcl);
			this.dfFecIni.addValueChangeListener(vcl);
		}
		if (this.tbRegistro != null) {
			this.tbRegistro.setRendererForColumn("TIPO", new BundleCellRenderer());
			this.tbRegistro.setRendererForColumn("PDA", new BooleanCellRenderer());
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.tbRegistro != null) {
			this.tbRegistro.setEnabled(true);
		}
		if (this.rdfUser != null) {
			this.rdfUser.setEnabled(true);
		}
		if ((this.dfFecFin != null) && (this.dfFecIni != null)) {
			Date dFin = new Date();
			this.dfFecFin.setValue(dFin);
			this.dfFecIni.setValue(DateUtil.addDays(dFin, -15));
			this.dfFecIni.setEnabled(false);
			this.dfFecFin.setEnabled(false);
		}
		if (this.bRefresh != null) {
			this.bRefresh.setEnabled(true);
		}
		if (this.bInforme != null) {
			this.bInforme.setEnabled(true);
		}
	}

	private Hashtable<Object,Object> getFilters() {
		Hashtable<Object,Object> ht = new Hashtable<Object,Object>();
		Vector<Object> keys = this.tbRegistro.getParentKeys();
		for (Object key : keys) {
			Object value = this.managedForm.getDataFieldValue((String) key);
			if (value == null) {
				continue;
			}
			if ("NOMB".equals(key)) {
				String sValue = (String) value;
				if ((sValue != null) && (sValue.length() > 0)) {
					ht.put(key, value + "*");
				} else {
					ht.put(key, value);
				}
			} else if (OpentachFieldNames.FILENAME_PROCESSED_FIELD.equals(key)) {
				String sValue = (String) value;
				if ((sValue != null) && (sValue.length() > 0)) {
					ht.put(key, value + "*");
				} else {
					ht.put(key, value);
				}
			} else {
				ht.put(key, value);
			}
		}
		Date dFecIni = (Date) this.dfFecIni.getValue();
		Date dFecFin = (Date) this.dfFecFin.getValue();
		if ((dFecFin != null) && (dFecIni != null)) {
			BasicExpression bs = new SQLStatementBuilder.BasicExpression(new BasicField("F_ALTA"), BasicOperator.LESS_EQUAL_OP, dFecFin);
			BasicExpression bs2 = new SQLStatementBuilder.BasicExpression(new BasicField("F_ALTA"), BasicOperator.MORE_EQUAL_OP, dFecIni);
			BasicExpression bs3 = new SQLStatementBuilder.BasicExpression(bs, BasicOperator.AND_OP, bs2);
			ht.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bs3);
		} else if (dFecFin != null) {
			ht.put("F_ALTA", new SearchValue(SearchValue.LESS_EQUAL, dFecFin));
		} else if (dFecIni != null) {
			ht.put("F_ALTA", new SearchValue(SearchValue.MORE_EQUAL, dFecIni));
		}
		return ht;
	}

	private void refreshTable() {

		OperationThread opth = new OperationThread() {
			@Override
			public void run() {
				this.hasStarted = true;
				try {
					Vector<Object> emptyRequiredDataField = IMConsultaFicheros.this.managedForm.getEmptyRequiredDataField();
					if ((emptyRequiredDataField != null) && (emptyRequiredDataField.size() > 0)) {
						return;
					}
					final EntityReferenceLocator erl = IMConsultaFicheros.this.formManager.getReferenceLocator();
					try {
						Entity eQuery = erl.getEntityReference(IMConsultaFicheros.this.tbRegistro.getEntityName());
						Vector<Object> vq = new Vector<Object>();
						Hashtable<Object,Object> htq = IMConsultaFicheros.this.getFilters();
						EntityResult er = eQuery.query(htq, vq, erl.getSessionId());
						if (er.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
							IMConsultaFicheros.this.tbRegistro.setEnabled(true);
							IMConsultaFicheros.this.tbRegistro.setValue(er);
						} else {
							IMConsultaFicheros.this.tbRegistro.setEnabled(false);
							IMConsultaFicheros.this.tbRegistro.setValue(null);
							this.res = "M_CONSULTA_EXCESIVA";
						}
					} catch (Exception e) {
						e.printStackTrace();
						this.res = "M_ERROR_RETRIEVING_DATA";
					}
				} finally {
					this.hasFinished = true;
				}
			}
		};
		ExtendedApplicationManager.proccessOperation((JFrame) SwingUtilities.getWindowAncestor(this.managedForm), opth, 1000);
		Object res = opth.getResult();
		if (res instanceof String) {
			this.managedForm.message(res.toString(), Form.ERROR_MESSAGE);
		}
	}

	private void createReports() {
		try {
			final String usuario_alta = (String) this.managedForm.getDataFieldValue("USUARIO_ALTA");
			final Date fdesde = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
			final Date fhasta = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
			final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
			OperationThread opth = new OperationThread() {
				@Override
				public void run() {
					this.hasStarted = true;
					try {
						JasperPrint jv = ocl.getRemoteService(IOpentachReportService.class).generaInformeConsultaFicheros(usuario_alta, fdesde, fhasta,
								ocl.getSessionId());
						this.res = jv;
					} catch (Exception e) {
						e.printStackTrace();
						this.res = e;
					} finally {
						this.hasFinished = true;
					}
				}
			};
			ExtendedApplicationManager.proccessOperation((JFrame) SwingUtilities.getWindowAncestor(this.managedForm), opth, 1000);
			Object res = opth.getResult();
			if (res instanceof JasperPrint) {
				JRDialogViewer jdv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation("CONSULTA_FICHEROS"), (JFrame) SwingUtilities.getWindowAncestor(this.managedForm),
						(JasperPrint) res);
				jdv.setVisible(true);
			} else {
				Exception e = (Exception) res;
				if (e != null) {
					if ("M_NO_SE_HAN_ENCONTRADO_DATOS".equals(e.getMessage())) {
						this.managedForm.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.ERROR_MESSAGE);
					} else {
						this.managedForm.message("M_PETICION_ERROR", Form.ERROR_MESSAGE);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.managedForm.message("M_PETICION_ERROR", Form.ERROR_MESSAGE);
		}
	}
}
