package com.opentach.client.mailmanager.component;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DropMode;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.InteractionManagerModeListener;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.table.RefreshTableEvent;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.exception.OpentachException;
import com.opentach.common.mailmanager.MailManagerNaming;
import com.opentach.common.mailmanager.dto.MailFolder;
import com.opentach.common.mailmanager.services.IMailManagerService;
import com.utilmize.client.gui.field.table.UTable;

/**
 * The Class DocumentationTable.
 */
public class MailTable extends UTable implements InteractionManagerModeListener {

	private static final Logger									logger						= LoggerFactory.getLogger(MailTable.class);

	private final MailFolderTree								categoryTree				= null;										// new MailFolderTree();
	private Map<? extends Serializable, ? extends Serializable>	currentFilter				= null;
	// ñapa
	private boolean												deleting					= false;
	private boolean												ignoreEvents				= false;
	private boolean												ignoreCheckRefreshThread	= false;
	private boolean												categoryPanel				= true;

	public MailTable(Hashtable<String, Object> params) throws Exception {
		super(params);
		this.getJTable().setFillsViewportHeight(true);
		if (this.categoryTree != null) {
			this.categoryPanel = ParseUtilsExtended.getBoolean((String) params.get("categorypanel"), true);

			this.categoryTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

				@Override
				public void valueChanged(TreeSelectionEvent event) {
					if (!MailTable.this.isIgnoreEvents()) {
						MailTable.this.refreshIfNeededInThread();
					}
				}
			});
			JScrollPane scrollPane = new JScrollPane(this.categoryTree);
			this.mainPanel.add(scrollPane, BorderLayout.WEST);
		}
		this.getJTable().setDragEnabled(true);
		this.getJTable().setDropMode(DropMode.INSERT_ROWS);
		this.getJTable().setTransferHandler(new MaiAttachmentTransferHandler(this));
		this.scrollPane.setVisible(this.categoryPanel);
	}

	@Override
	protected void installDetailFormListener() {
		super.installDetailFormListener();
	}

	@Override
	public void init(Hashtable params) throws Exception {
		super.init(params);
		if (this.keyFields == null) {
			this.keyFields = new Vector<>(1);
		}
		this.keyFields.clear();
		this.keyFields.add(MailManagerNaming.MAI_ID);
	}

	@Override
	public void refreshInThread(final int delay) {
		if (this.currentFilter != null) {
			MailTable.this.currentFilter.clear();
		}
		this.refreshIfNeededInThread();
	}

	@Override
	public void openInNewWindow(int[] modelSelectedRows) {
		// do nothing
	}

	@Override
	public void refreshInEDT(boolean autoSizeColumns) {
		try {
			this.checkRefreshThread();
			this.requeryMails();
			this.fireRefreshTableEvent(new RefreshTableEvent(this, RefreshTableEvent.OK));
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, MailTable.logger);
			this.fireRefreshTableEvent(new RefreshTableEvent(this, RefreshTableEvent.ERROR));
		}
	}

	/**
	 * Refreshes the rows passed as parameter
	 *
	 * @param viewRowIndexes
	 *            the row indexes
	 */
	@Override
	public void refreshRows(int[] viewRowIndexes) {
		try {
			this.checkRefreshThread();
			Arrays.sort(viewRowIndexes);
			Vector<Object> vRowsValues = new Vector<>();
			for (int k = 0; k < viewRowIndexes.length; k++) {
				int viewRow = viewRowIndexes[k];
				Hashtable<Object, Object> kv = this.getParentKeyValues();
				// Put the row keys
				Vector<?> vKeys = this.getKeys();
				for (int i = 0; i < vKeys.size(); i++) {
					Object oKey = vKeys.get(i);
					kv.put(oKey, this.getRowKey(viewRow, oKey.toString()));
				}
				EntityResult res = this.doQueryDocument(kv, this.attributes);
				Hashtable<?, ?> hRowData = res.getRecordValues(0);
				vRowsValues.add(vRowsValues.size(), hRowData);
			}
			// Update rows data
			this.deleteRows(viewRowIndexes);

			this.addRows(viewRowIndexes, vRowsValues);
		} catch (Exception error) {
			MessageManager.getMessageManager().showExceptionMessage(error, MailTable.logger);
		}
	}

	/**
	 * Refreshes the row passed as parameter.
	 *
	 * @param viewRowIndex
	 *            the index to refresh
	 * @param oldkv
	 */
	@Override
	public void refreshRow(int viewRowIndex, Hashtable oldkv) {
		try {
			this.checkRefreshThread();
			Hashtable<Object, Object> kv = this.getParentKeyValues();
			// Put the row keys
			Vector<?> vKeys = this.getKeys();
			for (int i = 0; i < vKeys.size(); i++) {
				Object oKey = vKeys.get(i);
				if ((oldkv != null) && oldkv.containsKey(oKey)) {
					kv.put(oKey, oldkv.get(oKey));
				} else {
					kv.put(oKey, this.getRowKey(viewRowIndex, oKey.toString()));
				}
			}
			long t = System.currentTimeMillis();
			EntityResult res = this.doQueryDocument(kv, this.attributes);
			if (res.isEmpty()) {
				this.deleteRow(viewRowIndex);
			} else {
				long t2 = System.currentTimeMillis();
				// Update row data
				Hashtable<?, ?> hRowData = res.getRecordValues(0);
				Hashtable<Object, Object> newkv = new Hashtable<>();
				for (int i = 0; i < vKeys.size(); i++) {
					Object oKey = vKeys.get(i);
					newkv.put(oKey, this.getRowKey(viewRowIndex, oKey.toString()));
				}
				this.updateRowData(hRowData, newkv);

				long t3 = System.currentTimeMillis();
				MailTable.logger.trace("Table: Query time: {}  ,  deleteRow-addRow time: {}", t2 - t, t3 - t2);
			}
		} catch (Exception error) {
			MessageManager.getMessageManager().showExceptionMessage(error, MailTable.logger);
		}
	}

	protected EntityResult doQueryDocument(Map<?, ?> filter, List<?> attrs) throws OpentachException {
		return BeansFactory.getBean(IMailManagerService.class).mailUserQuery(filter, attrs);
	}

	protected void requeryMails() throws OpentachException {
		this.deleteData();
		// Consider to refresh tree ----------------------------------------
		boolean oldIgnoreEvents = this.isIgnoreEvents();
		if (this.categoryTree != null) {
			try {
				this.setIgnoreEvents(true);
				this.categoryTree.refreshModel();
			} finally {
				this.setIgnoreEvents(oldIgnoreEvents);
			}
		}

		// Refresh table --------------------------------------------------
		Hashtable<?, ?> kv = this.getParentKeyValues();
		if (ObjectTools.safeIsEquals(this.currentFilter, kv)) {
			return;
		}
		this.currentFilter = (Map<Serializable, Serializable>) kv;
		final EntityResult er = BeansFactory.getBean(IMailManagerService.class).mailUserQuery(kv, this.getAttributeList());
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				MailTable.this.setValue(er, false);
			}
		});
	}

	@Override
	public Hashtable getParentKeyValues() {
		Hashtable<Object, Object> kv = super.getParentKeyValues();
		Object idCategory = this.getCurrentMailFolderIdToFilter();
		if (idCategory != null) {
			kv.put(MailManagerNaming.MFD_ID, idCategory);
		}
		return kv;
	}

	protected Serializable getCurrentMailFolderIdToFilter() {
		if (this.categoryTree == null) {
			return null;
		}
		TreePath selectionPath = this.categoryTree.getSelectionPath();
		if (selectionPath == null) {
			return null;
		}
		Object ob = selectionPath.getLastPathComponent();
		if (!(ob instanceof MailFolder)) {
			return null;
		}
		MailFolder category = (MailFolder) ob;
		if (category.getMfdId() == null) {
			// categoría raíz
			return new SearchValue(SearchValue.NULL, null);
		}
		return category.getMfdId();
	}

	@Override
	public void setParentForm(Form form) {
		super.setParentForm(form);
		form.getDataComponentList().remove(this.getAttribute());
		MailTable.this.refreshInThread(0);
	}

	@Override
	public void deleteData() {
		if (this.deleting) {
			return;
		}
		try {
			this.deleting = true;
			super.deleteData();
			if (this.categoryTree != null) {
				this.categoryTree.deleteData();
			}
		} finally {
			this.deleting = false;
		}
	}

	/**
	 * Deletes from the entity the specified row.
	 *
	 * @param rowIndex
	 *            the row index
	 * @return the result of the execution of the delete instruction
	 * @throws Exception
	 * @see Entity#delete(Hashtable, int)
	 */
	@Override
	public EntityResult deleteEntityRow(int rowIndex) throws Exception {
		if (this.isInsertingEnabled() && this.getTableSorter().isInsertingRow(rowIndex)) {
			this.getTableSorter().clearInsertingRow(this.getParentKeyValues());
		} else if (this.dataBaseRemove) {
			IMailManagerService service = BeansFactory.getBean(IMailManagerService.class);
			BigDecimal maiId = (BigDecimal) this.getRowKey(rowIndex, MailManagerNaming.MAI_ID);
			service.mailUserDelete(maiId);
		}
		return new EntityResult();
	}

	public void refreshIfNeededInThread() {
		try {
			this.silentDeleteData();

			if ((this.uRefreshThread != null) && this.uRefreshThread.isAlive()) {
				MailTable.logger.warn("A thread is already refreshing. Ensure to invoke to checkRefreshThread() to cancel it.");
			}
			this.hideInformationPanel();
			this.uRefreshThread = new MailTableRefreshThread(this);
			this.uRefreshThread.setDelay(0);
			this.uRefreshThread.start();
		} catch (Exception error) {
			MailTable.logger.error(null, error);
		}
	}

	private void silentDeleteData() {
		try {
			this.ignoreCheckRefreshThread = true;
			super.deleteData();
		} finally {
			this.ignoreCheckRefreshThread = false;
		}
	}

	@Override
	public void checkRefreshThread() {
		if (!this.ignoreCheckRefreshThread) {
			super.checkRefreshThread();
		}
	}

	public MailFolderTree getCategoryTree() {
		return this.categoryTree;
	}

	public Serializable getCurrentIdCategory() {
		Serializable idCategory = this.getCurrentMailFolderIdToFilter();
		if ((idCategory instanceof SearchValue) || (idCategory instanceof NullValue)) {
			idCategory = null;
		}
		return idCategory;
	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent event) {
		if (event.getInteractionManagerMode() == InteractionManager.INSERT) {
			this.setEnabled(false);
		} else if (event.getInteractionManagerMode() == InteractionManager.UPDATE) {
			this.setEnabled(true);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		boolean upEnabled = enabled;
		if (this.getParentForm().getInteractionManager().getCurrentMode() == InteractionManager.INSERT) {
			upEnabled = false;
		}
		super.setEnabled(upEnabled);
	}

	public boolean isIgnoreEvents() {
		return this.ignoreEvents;
	}

	/**
	 * Disable events on tree selection, because we are just setting root values.
	 *
	 * @param ignoreEvents
	 */
	public void setIgnoreEvents(boolean ignoreEvents) {
		this.ignoreEvents = ignoreEvents;
	}

	@Override
	public EntityResult updateTable(Hashtable keysValues, int viewColumnIndex, TableCellEditor tableCellEditor, Hashtable otherData, Object previousData) throws Exception {
		return new EntityResult();
	}
}
