package com.opentach.client.remotevehicle.modules.remotedownload;

import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.modules.IMRoot;
import com.opentach.common.filereception.UploadSourceType;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.field.table.UTable;
import com.utilmize.client.gui.field.table.editor.UXMLButtonCellEditor;

public class IMRemoteDownloadCfgRequestUploadedFilesShowFormListener extends AbstractActionListenerButton implements CellEditorListener {

	private static final Logger	logger	= LoggerFactory.getLogger(IMRemoteDownloadCfgRequestUploadedFilesShowFormListener.class);

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
	public IMRemoteDownloadCfgRequestUploadedFilesShowFormListener(UXMLButtonCellEditor editor, Hashtable params) throws Exception {
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
		formToShow.setDataFieldValue("CIF", table.getJTable().getValueAt(row, table.getColumnIndex("CIF")));
		formToShow.setDataFieldValue("IDORIGEN", table.getJTable().getValueAt(row, table.getColumnIndex("SRC_ID")));
		formToShow.setDataFieldValue("CG_CONTRATO", ((IMRoot) this.getInteractionManager()).getActiveContract());
		formToShow.setDataFieldValue("SOURCE_TYPE", new SearchValue(SearchValue.IN, EntityResultTools.attributes(UploadSourceType.REMOTA_JALTEST.toString())));
		formToShow.setDataFieldValue("F_ALTA", new SearchValue(SearchValue.MORE_EQUAL, DateTools.add(new Date(), Calendar.YEAR, -1)));
		((Table) formToShow.getElementReference("EFicherosSubidos")).refreshInThread(0);
		formToShow.getJDialog().setVisible(true);
	}

	private Form getFormToShow() {
		if (this.formToShow == null) {
			this.formToShow = this.getFormManager().getFormCopy("forms-remotevehicle/formRemoteDownloadCfgRequestUploadedFiles.form", UBasicFIM.class.getName());
			this.formToShow.putInModalDialog("remotevehicle.RemoteDownloadCfgRequestUploadedFiles", this.button);
		}
		return this.formToShow;
	}

	@Override
	public void editingCanceled(ChangeEvent evt) {}

	@Override
	public void actionPerformed(ActionEvent evt) {}

}
