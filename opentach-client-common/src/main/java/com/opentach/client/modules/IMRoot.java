package com.opentach.client.modules;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.DataNavigationEvent;
import com.ontimize.gui.Form;
import com.ontimize.gui.FormManager;
import com.ontimize.gui.ValueChangeDataComponent;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.DateCellRenderer;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.tree.Tree;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.client.comp.render.MinutesCellRender;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.gui.field.table.render.UXmlDateCellRenderer;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.JRViewer;

public class IMRoot extends OpentachBasicInteractionManager {

	private static final Logger		logger	= LoggerFactory.getLogger(IMRoot.class);
	/**
	 * Lista de campos que se iran activando de forma secuencia.<br> Cuando un campo de la cadena establece su valor se activa el primer campo siguiente que exista en el
	 * formulario, cuando borra su valor se borran y desactivan los campos que le siguen.
	 */
	protected List<String>			fieldsChain;
	protected ValueChangeListener	chainlistener;
	protected DataComponent			cgContract;
	protected DataComponent			CIF;

	protected static final Date		BEGININGDATE;

	static {
		Calendar cal = Calendar.getInstance();
		cal.set(104, 0, 1);
		BEGININGDATE = cal.getTime();
	}

	public IMRoot() {
		super();
		this.fieldsChain = new ArrayList<String>();
		this.fieldsChain.add(OpentachFieldNames.CIF_FIELD);
		this.fieldsChain.add(OpentachFieldNames.CG_CONTRATO_FIELD);
		this.fieldsChain.add(OpentachFieldNames.NUMREQ_FIELD);
		this.fieldsChain.add(OpentachFieldNames.DNI_FIELD);
		this.fieldsChain.add(OpentachFieldNames.IDCONDUCTOR_FIELD);
		this.fieldsChain.add(OpentachFieldNames.IDPERSONAL_FIELD);
		this.fieldsChain.add(OpentachFieldNames.MATRICULA_FIELD);
		this.fieldsChain.add(OpentachFieldNames.IDORIGEN_FIELD);
		this.fieldsChain.add(OpentachFieldNames.NUMTARJETA_FIELD);
		// this.fieldsChain.add("ID_SURVEY ");
		this.fieldsChain.add("CIF_CERTIFICADO");

	}

	@Override
	public void registerInteractionManager(final Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.setDateTableRenders();
		Button reginfobtn = this.managedForm.getButton("reginfobtn");
		if (reginfobtn != null) {
			reginfobtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JComponent c = (JComponent) f.getElementReference("reginfo");
					if (c != null) {
						c.setVisible(!c.isVisible());
					}
				}
			});
		}
		JComponent c = (JComponent) f.getElementReference("reginfo");
		if (c != null) {
			c.setVisible(false);
		}
		this.chainlistener = new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (e.getType() == ValueEvent.USER_CHANGE) {
					try {
						DataField fld = (DataField) e.getSource();
						IMRoot.this.updateChainStatus("" + fld.getAttribute(), e.getNewValue() == null ? true : false);
					} catch (Exception ex) {
						IMRoot.logger.error(null, ex);
					}
				}
			}
		};
		this.registerFields2EnableChain();
		this.cgContract = f.getDataFieldReference(OpentachFieldNames.CG_CONTRATO_FIELD);
		this.CIF = f.getDataFieldReference(OpentachFieldNames.CIF_FIELD);
		if ((this.cgContract != null) && (this.CIF != null)) {
			((ValueChangeDataComponent) this.cgContract).addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					if (e.getType() == ValueEvent.USER_CHANGE) {

						IMRoot.this.setActiveContract((String) IMRoot.this.CIF.getValue(), (String) IMRoot.this.cgContract.getValue());

					}
				}
			});
			((ValueChangeDataComponent) this.CIF).addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					if ((e.getNewValue() != null) && (e.getOldValue() != e.getNewValue())) {
						String cgActivo = IMRoot.this.getActiveContract();
						IMRoot.this.cgContract.setValue(cgActivo);
						if (cgActivo != null) {
							IMRoot.this.updateChainStatus(OpentachFieldNames.CG_CONTRATO_FIELD, false);
						}
					}
				}
			});
		}
	}

	public String getActiveContract() {
		String cgActivo = null;
		try {
			IUserData user = ((UserInfoProvider) this.formManager.getReferenceLocator()).getUserData();
			cgActivo = user.getActiveContract((String) this.CIF.getValue());
		} catch (Exception e) {
		}
		return cgActivo;
	}

	public void setActiveContract(String cif, String cgcontrato) {
		try {
			IUserData user = ((UserInfoProvider) this.formManager.getReferenceLocator()).getUserData();
			user.addActiveContract((String) this.CIF.getValue(), cgcontrato);
		} catch (Exception e) {
			IMRoot.logger.trace(null, e);
		}
	}

	@Override
	public void dataChanged(DataNavigationEvent e) {
		if (this.cgContract != null) {
			this.cgContract.setValue(this.getActiveContract());
		}
		super.dataChanged(e);
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.setUserData();
		this.managedForm.enableButton("boton");
	}

	@Override
	public void setQueryMode() {
		super.setQueryMode();
		this.setUserData();
	}

	public static void clearTables(Form form) {
		List tables = FIMUtils.getTables(form);
		for (Iterator iter = tables.iterator(); iter.hasNext();) {
			Table table = (Table) iter.next();
			table.deleteData();
		}
	}

	public static void clearTables(Form form, String[] tables) {
		for (String tablename : tables) {
			Table tabla = (Table) form.getDataFieldReference(tablename);
			if (tabla != null) {
				tabla.deleteData();
			}
		}
	}

	/**
	 * Init field chain enabling the first field before clear and disable the rest
	 */
	protected void initFieldChain() {
		if (!this.fieldsChain.isEmpty()) {
			this.managedForm.setDataFieldValue(this.fieldsChain.get(0), null);
			this.updateChainStatus(this.fieldsChain.get(0), true);
		}
	}

	protected void updateChainStatus(String fieldsrc, boolean empty) {
		try {
			int idx = this.fieldsChain.indexOf(fieldsrc);
			if (idx != -1) {
				for (int i = idx + 1; i < this.fieldsChain.size(); i++) {
					String f = this.fieldsChain.get(i);
					if ((this.managedForm.getDataFieldReference(f) != null) && !(this.managedForm.getDataFieldReference(f) instanceof Table)) {

						if (this.managedForm.getDataFieldValue("CIF") != null) {
							if (f.equals("IDCONDUCTOR") || f.equals("MATRICULA") || f.equals("IDORIGEN") || f.equals("ID_SURVEY")) {
								DataField ccrd = (DataField) this.managedForm.getDataFieldReference(f);
								Object resOrigen = ccrd.getValue();
								if ((resOrigen == null)) {
									this.managedForm.deleteDataField(f);
									this.managedForm.disableDataField(f);
								} else {
									this.managedForm.enableDataField(f);
									i++;
									i = this.getNextEnabledField(i);

									if (i <= this.fieldsChain.size()) {
										this.managedForm.enableDataField(this.fieldsChain.get(i));
									}
								}
							} else {
								this.managedForm.deleteDataField(f);
								this.managedForm.disableDataField(f);
							}
						} else {
							this.managedForm.deleteDataField(f);
							this.managedForm.disableDataField(f);
						}
					}
				}
				// Si el elementos no esta vacio activo el siguiente componente
				// PRESENTE en el formulario.
				if (!empty) {
					Vector<Table> toRefreshTables = new Vector<Table>();
					for (int i = idx + 1; i < this.fieldsChain.size(); i++) {
						String f = this.fieldsChain.get(i);
						if (this.managedForm.getElementReference(f) != null) {
							if (this.managedForm.getElementReference(f) instanceof Table) {
								toRefreshTables.add((Table) this.managedForm.getElementReference(f));
							} else {
								if (OpentachFieldNames.NUMTARJETA_FIELD
										.equals(this.fieldsChain.get(i)) && !this.managedForm.getDataFieldReference(OpentachFieldNames.NUMTARJETA_FIELD).isHidden()) {
									this.updateChainStatus(this.fieldsChain.get(i), false);
								}
								this.managedForm.enableDataField(f);
							}
							break;
						}
					}
					this.refreshTables(toRefreshTables);

				} else {
					this.managedForm.enableDataField(fieldsrc);
				}
			}

		} catch (Exception ex) {
			IMRoot.logger.error(null, ex);
		}
	}

	protected void hideTreeNode() {
		if (!(this.formManager instanceof FormManager) || (((FormManager) this.formManager).getTree() == null)) {
			return;
		}
		Tree tree = ((FormManager) this.formManager).getTree();
		TreePath pr = tree.getPathForRow(0).getParentPath();
			if (pr == null) {
			tree.expandPath(tree.getPathForRow(0));
			}
		tree.setRootVisible(false);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.initFieldChain();
		this.setUserData();
		this.hideTreeNode();
	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();
		this.setUserData();
	}

	protected void setUserData() {
		try {
			String cif = null;
			IUserData user = ((UserInfoProvider) this.formManager.getReferenceLocator()).getUserData();
			if (this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD) == null) {
				if ((user.getAllCompanies().size() > 1) || (user.getAllCompanies().size() == 0)) {
					return;
				}
				if (user.getCIF() != null) {
					DataField c = (DataField) this.managedForm.getDataFieldReference(OpentachFieldNames.CIF_FIELD);
					if (c != null) {
						c.setValue(user.getCIF());
						c.setModifiable(false);
						c.setEnabled(false);
						this.updateChainStatus(OpentachFieldNames.CIF_FIELD, false);
						cif = user.getCIF();
					}
				}
			} else {
				cif = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD);
			}

			if (user.getActiveContract(cif) != null) {
				DataField c = (DataField) this.managedForm.getDataFieldReference(OpentachFieldNames.CG_CONTRATO_FIELD);
				if (c != null) {
					c.setValue(user.getActiveContract(cif));
					c.setModifiable(false);
					c.setEnabled(false);
					this.updateChainStatus(OpentachFieldNames.CG_CONTRATO_FIELD, false);
				}
				c = (DataField) this.managedForm.getDataFieldReference(OpentachFieldNames.NUMREQ_FIELD);
				if (c != null) {
					c.setValue(user.getActiveContract(cif));
					c.setModifiable(false);
					c.setEnabled(false);
					this.updateChainStatus(OpentachFieldNames.NUMREQ_FIELD, false);
				}
			}
		} catch (Exception ex) {
			IMRoot.logger.error(null, ex);
		}
	}

	public boolean checkRequiredVisibleDataFields(boolean alert) {
		Vector comp = this.managedForm.getDataComponents();
		for (int i = 0; i < comp.size(); i++) {
			JComponent jc = (JComponent) comp.get(i);
			if ((jc != null) && jc.isShowing()) {
				DataComponent cd = (DataComponent) comp.get(i);
				if (cd.isRequired() && cd.isEmpty()) {
					if (alert) {
						this.managedForm.message(BasicInteractionManager.M_FILL_ALL_REQUIRED_FIELDS, Form.WARNING_MESSAGE);
					}
					// TODO: QUITAR ESTO CUANDO TODOS LOS CONDUCTORES TENGAN MOVIL.
					if ("MOVIL".equals(cd.getAttribute())) {
						continue;
					}
					return false;
				}
			}
		}
		return true;
	}

	public static void refreshTables(Form form, String[] tablas) {
		for (String tablename : tablas) {
			Table tabla = (Table) form.getDataFieldReference(tablename);
			if (tabla != null) {
				tabla.refresh();
			}
		}
	}

	protected void refreshTables(Vector<Table> toRefreshTables) throws Exception {
		if (toRefreshTables.size() > 0) {
			if (this.checkRequiredVisibleDataFields(false)) {
				for (Table tb : toRefreshTables) {
					tb.refresh();
				}
			}
		}
	}

	public void registerFields2EnableChain() {
		for (String fldname : this.fieldsChain) {
			Object fld = this.managedForm.getDataFieldReference(fldname);
			if ((fld != null) && (fld instanceof DataField)) {
				((DataField) fld).addValueChangeListener(this.chainlistener);
			}
		}
	}

	public void unRegisterFields2EnableChain() {
		for (String fldname : this.fieldsChain) {
			DataField fld = (DataField) this.managedForm.getDataFieldReference(fldname);
			if (fld != null) {
				fld.removeValueChangeListener(this.chainlistener);
			}
		}
	}

	public int getNextEnabledField(int i) {

		for (int ifield = i; ifield < this.fieldsChain.size(); ifield++) {
			if ((this.managedForm.getDataFieldReference(this.fieldsChain.get(ifield)) != null) && !(this.managedForm
					.getDataFieldReference(this.fieldsChain.get(ifield)) instanceof Table) && ((DataField) this.managedForm.getDataFieldReference(this.fieldsChain.get(ifield)))
					.isVisible()) {
				return ifield;
			}
		}

		return this.fieldsChain.size() + 1;
	}

	public List<String> getFieldEnableChain() {
		return this.fieldsChain;
	}

	public void setFieldEnableChain(List<String> fieldEnableChain) {
		this.fieldsChain = fieldEnableChain;
	}

	protected void setDateTableRenders() {
		final List tables = FIMUtils.getTables(this.managedForm);

		final String[] datecols = { "FECINI", "FECFIN", "FECHORAINI", "FECHORAFIN", "FECINI", "FEC_FIN", "F_DESCARGA_DATOS", "F_PROCESADO" };
		final String[] mincols = { "MINUTOS", "MINUTOS_TOT", "DURACION", "TDP", "TCP", "EXCON", "FADES", "CONDUCCION", "TRABAJO", "PAUSA_DESCANSO", "INDEFINIDA", "DISPONIBILIDAD_COND", "DISPONIBILIDAD_ACOMP" };

		for (Object oTabla : tables) {
			Table tb = (Table) oTabla;
			Vector cols = tb.getOrderColumnName();
			DateCellRenderer dcr = this.getDateCellRenderer();
			for (String col : datecols) {
				if (cols.contains(col)) {
					tb.setRendererForColumn(col, dcr);
				}
			}
			for (String col : mincols) {
				if (cols.contains(col)) {
					tb.setRendererForColumn(col, new MinutesCellRender(col));
				}
			}
		}
	}

	protected DateCellRenderer getDateCellRenderer() {
		DateCellRenderer dcr;
		try {
			dcr = new UXmlDateCellRenderer(EntityResultTools.keysvalues("conhora", "yes", "horaprimero", "no", "align", "center"));
		} catch (Exception e) {
			IMRoot.logger.error("E_SETTING_RENDERER", e);
			dcr = new DateCellRenderer(true, false);
		}
		return dcr;
	}

	protected String checkContratoFicticio(String cgContrato) {
		try {
			EntityReferenceLocator loc = this.formManager.getReferenceLocator();
			int sesionId = loc.getSessionId();
			Entity eEmpreReq = loc.getEntityReference("EEmpreReq");

			Hashtable<String, Object> avfic = new Hashtable<String, Object>();
			avfic.put("CG_CONTRATO", cgContrato);

			EntityResult res = eEmpreReq.query(avfic, new Vector<Object>(), sesionId);
			if (res.calculateRecordNumber() > 0) {
				if ("S".equals(res.getRecordValues(0).get("FICTICIO"))) {
					Entity eCifEmpreReq = loc.getEntityReference("ECifEmpreReq");
					avfic = new Hashtable<String, Object>();
					avfic.put("CIF", res.getRecordValues(0).get("CIF"));
					EntityResult resCif = eCifEmpreReq.query(avfic, new Vector<Object>(), sesionId);
					if (resCif.calculateRecordNumber() > 0) {
						return (String) resCif.getRecordValues(0).get("CG_CONTRATO");
					}
				} else {
					return cgContrato;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void showReportViewer(String dscr, JasperPrint jp) {
		JRViewer viewer = new JRViewer(jp);
		JRSaveContributor[] jrsc = viewer.getSaveContributors();
		JRSaveContributor[] jrscNuevo = new JRSaveContributor[jrsc.length - 1];
		for (int i = 1; i < jrsc.length; i++) {
			jrscNuevo[i - 1] = jrsc[i];
		}
		viewer.setSaveContributors(jrscNuevo);
		JFrame frame = new JFrame(dscr);
		frame.getContentPane().add(viewer);
		frame.pack();
		frame.setSize(900, 1000);
		frame.setVisible(true);
	}

}
