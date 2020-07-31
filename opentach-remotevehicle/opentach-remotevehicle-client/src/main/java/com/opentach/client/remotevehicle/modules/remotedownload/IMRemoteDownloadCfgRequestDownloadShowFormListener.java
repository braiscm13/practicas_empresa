package com.opentach.client.remotevehicle.modules.remotedownload;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.field.table.UTable;
import com.utilmize.client.gui.field.table.editor.UXMLButtonCellEditor;

public class IMRemoteDownloadCfgRequestDownloadShowFormListener extends AbstractActionListenerButton implements CellEditorListener {

	private static final Logger	logger	= LoggerFactory.getLogger(IMRemoteDownloadCfgRequestDownloadShowFormListener.class);

	private Form				formToShow;

	/**
	 * Instantiates a new IM informe CAP request cap listener.
	 *
	 * @param editor
	 *            the editor
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public IMRemoteDownloadCfgRequestDownloadShowFormListener(UXMLButtonCellEditor editor, Hashtable params) throws Exception {
		super(editor, editor, params);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.CellEditorListener#editingStopped(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void editingStopped(final ChangeEvent event) {
		// Se llama desde el boton de la tabla
		final UTable table = ((UXMLButtonCellEditor) event.getSource()).getTable();
		final int row = ((UXMLButtonCellEditor) event.getSource()).getCellEditorRow();

		Form formToShow = this.getFormToShow();
		formToShow.deleteDataFields();
		formToShow.getInteractionManager().setUpdateMode();
		String[] attrs = new String[] { "SRCTYPE", "SRC_ID", "CIF" };
		for (String attr : attrs) {
			formToShow.setDataFieldValue(attr, table.getJTable().getValueAt(row, table.getColumnIndex(attr)));
		}
		formToShow.setDataFieldValue("LAST_DOWNLOAD", Boolean.TRUE);
		formToShow.setDataFieldValue("RANGO_FECHA", Boolean.FALSE);
		formToShow.getJDialog().setVisible(true);
	}

	private Form getFormToShow() {
		if (this.formToShow == null) {
			this.formToShow = this.getFormManager().getFormCopy("forms-remotevehicle/formRemoteDownloadCfgRequestDownload.form", UBasicFIM.class.getName());
			this.formToShow.putInModalDialog("remotevehicle.RemoteDownloadCfgRequestDownload", this.button);
		}
		return this.formToShow;
	}

	@Override
	public void editingCanceled(ChangeEvent evt) {}

	@Override
	public void actionPerformed(ActionEvent evt) {}

}
