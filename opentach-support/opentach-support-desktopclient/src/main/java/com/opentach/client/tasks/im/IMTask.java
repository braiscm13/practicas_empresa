package com.opentach.client.tasks.im;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.DataNavigationEvent;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.IntegerDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.cache.AbstractGenericCache;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.util.UserTools;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.field.UMemoDataField;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMTask extends UBasicFIM {

	/** The CONSTANT logger */
	private static final Logger									logger					= LoggerFactory.getLogger(IMTask.class);

	/** A cache with initial status */
	protected static AbstractGenericCache<String, List<Number>>	initialStatusCache		= new InitialStatusCache();

	/** A cache with initial priorities */
	protected static AbstractGenericCache<String, List<Number>>	initialPriorityCache	= new InitialPriorityCache();

	@FormComponent(attr = "TSK_ID")
	protected IntegerDataField									tskId;

	@FormComponent(attr = "TSK_DESCRIPTION")
	protected UMemoDataField									descField;

	@FormComponent(attr = "USUARIO_CREATOR")
	protected UReferenceDataField								userCreatorField;

	@FormComponent(attr = "USUARIO_ASIGNEE")
	protected UReferenceDataField								userAsigneeField;

	@FormComponent(attr = "TKS_ID")
	protected UReferenceDataField								statusField;

	@FormComponent(attr = "TPR_ID")
	protected UReferenceDataField								priorityField;

	@FormComponent(attr = "TSK_CREATION_DATE")
	protected DateDataField										creationDateField;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);

		this.setStayInRecordAfterInsert(true);
	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();
		this.configureEnableConditions();
		this.setInsertDefaultValues();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.configureEnableConditions();
		this.managedForm.getDataFieldReference("USUARIO_CREATOR").setEnabled(!UserTools.isAgente());
	}

	@Override
	public void dataChanged(DataNavigationEvent e) {
		super.dataChanged(e);
		this.configureEnableConditions();
		this.setStayInRecordAfterInsert(true);
	}

	protected void configureEnableConditions() {
		this.tskId.setFocusable(false);
		this.tskId.getDataField().setFocusable(false);
		boolean insertMode = this.getCurrentMode() == InteractionManager.INSERT;
		this.descField.getDataField().setFocusable(insertMode);
	}

	protected void setInsertDefaultValues() {
		// Date values
		this.creationDateField.setValue(new Date());

		// user data
		Object userId = this.getUserId();
		this.userCreatorField.setValue(userId);
		this.userAsigneeField.setValue(userId);

		// Status
		this.setValueFromCache(this.statusField, IMTask.initialStatusCache);

		// Priority
		this.setValueFromCache(this.priorityField, IMTask.initialPriorityCache);
	}

	private void setValueFromCache(UReferenceDataField combo, AbstractGenericCache<String, List<Number>> cache) {
		List<Number> value = cache.get("");
		combo.setValue((value == null) || (value.size() == 0) ? null : value.get(0));
	}

	protected Object getUserId() {
		try {
			ReferenceLocator locator = (ReferenceLocator) ApplicationManager.getApplication().getReferenceLocator();
			return locator.getUserId(locator.getSessionId());
		} catch (Exception ex) {
			return null;
		}
	}

	protected static class InitialStatusCache extends AbstractGenericCache<String, List<Number>> {
		public InitialStatusCache() {
			super(10 * 60 * 1000);
		}

		@Override
		protected List<Number> requestData(String dummy) {
			try {
				EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
				Entity entity = locator.getEntityReference("ETaskStatus");
				EntityResult res = entity.query(EntityResultTools.keysvalues("TKS_IS_INITIAL", "S"), EntityResultTools.attributes("TKS_ID"), locator.getSessionId());
				CheckingTools.checkValidEntityResult(res);
				return (Vector<Number>) res.get("TKS_ID");
			} catch (Exception ex) {
				IMTask.logger.error("ERROR_QUERYING_INITIAL_STATUS", ex);
				return new ArrayList<Number>();
			}
		}
	}

	protected static class InitialPriorityCache extends AbstractGenericCache<String, List<Number>> {
		public InitialPriorityCache() {
			super(10 * 60 * 1000);
		}

		@Override
		protected List<Number> requestData(String dummy) {
			try {
				EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
				Entity entity = locator.getEntityReference("ETaskPriority");
				EntityResult res = entity.query(EntityResultTools.keysvalues("TPR_DEFAULT", "S"), EntityResultTools.attributes("TPR_ID"), locator.getSessionId());
				CheckingTools.checkValidEntityResult(res);
				return (Vector<Number>) res.get("TPR_ID");
			} catch (Exception ex) {
				IMTask.logger.error("ERROR_QUERYING_INITIAL_PROPRITY", ex);
				return new ArrayList<Number>();
			}
		}
	}
}
