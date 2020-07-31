package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.util.remote.BytesBlock;
import com.ontimize.windows.office.WindowsUtils;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.util.DateUtil;

public class IMViewInformeGestor extends IMDataRoot {

	private static final String	ENTIDAD		= "EInformeGestor";

	private Table				tblFicheros	= null;
	private JFileChooser		jfc			= null;

	public IMViewInformeGestor() {
		super();
		this.fieldsChain.clear();
		this.fieldsChain.add(OpentachFieldNames.CIF_FIELD);
		this.fieldsChain.add(OpentachFieldNames.CG_CONTRATO_FIELD);
		this.fieldsChain.add(IMViewInformeGestor.ENTIDAD);
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.setDateTags(new TimeStampDateTags("F_INFORME"));
		this.tblFicheros = (Table) this.managedForm.getDataFieldReference(IMViewInformeGestor.ENTIDAD);
		this.tblFicheros.getJTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (this.tblFicheros != null) {
			this.tblFicheros.getJTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			Button btn = this.managedForm.getButton("btnViewReport");
			if (btn != null) {
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						IMViewInformeGestor.this.viewReport();
					}
				});
			}
			btn = this.managedForm.getButton("btnSaveReport");
			if (btn != null) {
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						IMViewInformeGestor.this.saveReport();
					}
				});
			}

			this.tblFicheros.getJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					ListSelectionModel ls = (ListSelectionModel) e.getSource();
					try {
						Button btnv = IMViewInformeGestor.this.managedForm.getButton("btnViewReport");
						Button btns = IMViewInformeGestor.this.managedForm.getButton("btnSaveReport");
						btnv.setEnabled(!ls.isSelectionEmpty());
						btns.setEnabled(!ls.isSelectionEmpty());
					} catch (Exception ex) {
					}
				}
			});
		}
		Date dEnd = new Date();
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, DateUtil.addDays(dEnd, -28));
		this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, dEnd);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
	}

	// RAFA:
	// sobrecargado para que añada fechas null
	@Override
	@SuppressWarnings("unchecked")
	protected Hashtable getDateFilterValues(List keys) {
		SearchValue vb = null;
		try {
			if ((keys == null) || keys.isEmpty()) {
				return new Hashtable();
			}
			DateDataField cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
			Date selectedfecini = (Date) cf.getDateValue();
			cf = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
			Date selectedfecfin = (Date) cf.getDateValue();
			Calendar cal = Calendar.getInstance();
			cal.setTime(selectedfecfin);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			selectedfecfin = new Timestamp(cal.getTime().getTime());
			if ((selectedfecfin != null) && (selectedfecini != null)) {
				Vector v = new Vector(2);
				v.add(selectedfecini);
				v.add(selectedfecfin);
				vb = new SearchValue(SearchValue.BETWEEN, v);
			} else if (selectedfecfin != null) { // only less condition.
				vb = new SearchValue(SearchValue.LESS_EQUAL, selectedfecfin);
			} else if (selectedfecini != null) { // only less condition.
				vb = new SearchValue(SearchValue.MORE_EQUAL, selectedfecini);
			}
			if (vb != null) {
				BasicExpression be = null;
				BasicExpression be1 = null;
				BasicExpression be2 = null;
				BasicExpression beAux = null;
				Hashtable rtn = new Hashtable();
				for (Iterator iter = keys.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
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
				rtn.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, be);
				return rtn;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private File getReportFile() {
		File tmp = null;
		int row = this.tblFicheros.getSelectedRow();
		if (row != -1) {
			BytesBlock bb = null;
			Hashtable fila = this.tblFicheros.getRowData(row);
			Number idReport = (Number) fila.get("IDINFORMEGESTOR");
			try {
				OpentachClientLocator bref = (OpentachClientLocator) this.formManager.getReferenceLocator();
				Entity eReport = bref.getEntityReference(IMViewInformeGestor.ENTIDAD);
				Vector vq = new Vector(1);
				vq.add("INFORME");
				Hashtable ht = new Hashtable(1);
				ht.put("IDINFORMEGESTOR", idReport);
				EntityResult res = eReport.query(ht, vq, bref.getSessionId());
				if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
					Hashtable datosFila = res.getRecordValues(0);
					bb = (BytesBlock) datosFila.get("INFORME");
				}
				if (bb == null) {
					throw new NullPointerException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.managedForm.message("M_CANNOT_OPEN_FILE", Form.ERROR_MESSAGE);
				return tmp;
			}

			try {
				ByteArrayInputStream zippedstream = new ByteArrayInputStream(bb.getBytes());
				GZIPInputStream zis = new GZIPInputStream(zippedstream);
				tmp = File.createTempFile("InformeGestor", ".pdf");
				tmp.deleteOnExit();
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(tmp);
					byte[] buffer = new byte[10240];
					int bytesRead;
					while ((bytesRead = zis.read(buffer)) != -1) {
						fos.write(buffer, 0, bytesRead);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					if (zis != null) {
						zis.close();
					}
					if (fos != null) {
						fos.close();
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return tmp;
	}

	private void saveReport() {
		File tmp = this.getReportFile();
		if (tmp != null) {
			try {
				if (this.jfc == null) {
					this.jfc = new JFileChooser();
					this.jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					String userhome = System.getProperty("user.home");
					this.jfc.setSelectedFile(new File(userhome, "InformeGestor.pdf"));
				}
				this.jfc.setLocale(ApplicationManager.getLocale());
				this.jfc.updateUI();
				int returnVal = this.jfc.showSaveDialog(SwingUtilities.getWindowAncestor(this.managedForm));
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					InputStream from = null;
					OutputStream to = null;
					try {
						from = new BufferedInputStream(new FileInputStream(tmp));
						to = new FileOutputStream(this.jfc.getSelectedFile());
						byte[] buffer = new byte[40960];
						int bytesRead;
						while ((bytesRead = from.read(buffer)) != -1) {
							to.write(buffer, 0, bytesRead);
						}
					} finally {
						if (from != null) {
							from.close();
						}
						if (to != null) {
							to.close();
						}
					}
				}
			} catch (Exception ex) {
				if (tmp != null) {
					tmp.delete();
				}
			}
		}
	}

	private void viewReport() {
		File tmp = null;
		try {
			tmp = this.getReportFile();
			WindowsUtils.openFile_Script(tmp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
