package com.opentach.adminclient.modules.files;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.BooleanCellEditor;
import com.ontimize.gui.table.BooleanCellRenderer;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.modules.OpentachBasicInteractionManager;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.contract.ITachoFileContractService;
import com.opentach.common.process.ITachoFileProcessService;

public class IMAsociarContrato extends OpentachBasicInteractionManager {

	private static final Logger	logger			= LoggerFactory.getLogger(IMAsociarContrato.class);
	private Table				tbContFicheros	= null;
	private Button				addqueue		= null;

	@Override
	public void setInitialState() {
		this.setUpdateMode();
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.tbContFicheros = (Table) f.getElementReference("ECondVehCont_Ficheros");
		if (this.tbContFicheros != null) {
			this.tbContFicheros.setEditableColumn("ASOCIADO", false, true);
			BooleanCellRenderer cer = new BooleanCellRenderer();
			this.tbContFicheros.setRendererForColumn("ASOCIADO", cer);
			TableColumn tc = this.tbContFicheros.getJTable().getColumn("ASOCIADO");
			int im = tc.getModelIndex();
			int iv = this.tbContFicheros.getJTable().convertColumnIndexToView(im);
			Hashtable<String, Object> ht = new Hashtable<String, Object>();
			ht.put("column", Integer.valueOf(iv));
			ht.put("autostopediting", "true");
			BooleanCellEditor cee = new BooleanCellEditor(ht);
			this.tbContFicheros.setColumnEditor("ASOCIADO", cee);
			this.addqueue = f.getButton("addqueue");
			if (this.addqueue != null) {
				this.addqueue.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						IMAsociarContrato.this.actionBotonPriority();
					}
				});
			}
		}
		Button btn = f.getButton("asociar");
		if (btn != null) {
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						IMAsociarContrato.this.accBotAsociar();
					} catch (Exception error) {
						MessageManager.getMessageManager().showExceptionMessage(error, IMAsociarContrato.logger);
					}
				}
			});
		}
		btn = f.getButton("cancelar");
		if (btn != null) {
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMAsociarContrato.this.accBotCancelar();
				}
			});
		}
	}

	private void actionBotonPriority() {
		int[] filas = this.tbContFicheros.getSelectedRows();
		if (filas.length > 0) {
			List<Number> lIdFiles = new ArrayList<Number>();
			Number idFile = null;
			for (int i = 0; i < filas.length; i++) {
				if (((Number) this.tbContFicheros.getRowValue(filas[i], "ASOCIADO")).intValue() != 0) {
					idFile = (Number) this.tbContFicheros.getRowValue(filas[i], OpentachFieldNames.IDFILE_FIELD);
					lIdFiles.add(idFile);
				}
			}
			if (!lIdFiles.isEmpty()) {
				try {
					int sessionId = this.formManager.getReferenceLocator()
							.getSessionId();
					ITachoFileProcessService processService = (ITachoFileProcessService) ((OpentachClientLocator) this.formManager
							.getReferenceLocator()).getRemoteReference(ITachoFileProcessService.ID, sessionId);
					processService.addPriorityFiles(lIdFiles, sessionId);
				} catch (Exception e) {
					this.managedForm.message("M_ERROR_ENVIO_PRIORIDAD", Form.ERROR_MESSAGE);
				}
			}
		}
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.managedForm.enableButtons();
		if ((this.tbContFicheros != null) && (this.managedForm.getDataFieldValue("IDORIGEN") != null)) {
			this.tbContFicheros.refresh();
		} else {
			this.tbContFicheros.deleteData();
		}
	}

	public void accBotCancelar() {
		Window f = SwingUtilities.getWindowAncestor(this.managedForm);
		f.setVisible(false);
	}

	public void accBotAsociar() throws Exception {
		if (this.tbContFicheros != null) {
			Hashtable<String, Object> htTabla = (Hashtable<String, Object>) this.tbContFicheros.getValue();
			Number idfichero = (Number) this.managedForm.getDataFieldValue(OpentachFieldNames.IDFILE_FIELD);
			List<String> lContr = (List<String>) htTabla.get(OpentachFieldNames.CG_CONTRATO_FIELD);
			List<Number> lActivo = (List<Number>) htTabla.get("ASOCIADO");
			OpentachClientLocator locator = (OpentachClientLocator) this.managedForm.getFormManager().getReferenceLocator();
			try {
				locator.getRemoteService(ITachoFileContractService.class).reassignFileToContracts(idfichero, lContr, lActivo, locator.getSessionId());
			} catch (Exception e) {
				String msg = ApplicationManager.getTranslation("M_ERROR_ASOCIAR_CONTRATO", this.formManager.getResourceBundle());
				this.managedForm.message(msg, JOptionPane.INFORMATION_MESSAGE);
			}
			((IMListadoFicheros) this.managedForm.getDetailComponent().getTable().getParentForm().getInteractionManager()).consultar();
			this.accBotCancelar();
		}
	}

}
