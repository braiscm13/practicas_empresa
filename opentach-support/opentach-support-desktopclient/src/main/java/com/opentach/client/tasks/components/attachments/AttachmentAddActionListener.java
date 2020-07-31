package com.opentach.client.tasks.components.attachments;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.util.HessianFactory;
import com.opentach.common.tasks.ITaskAttachmentFilesHessianService;
import com.opentach.common.tasks.ITaskAttachmentService;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.UFileProviderDataField;
import com.utilmize.client.listeners.USaveListener;

public class AttachmentAddActionListener extends USaveListener {

	public AttachmentAddActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected EntityResult insertToServerInternal(Entity entity, Map<?, ?> attributesValuesToInsert) throws Exception {
		try {
			this.notifyPreInsert(attributesValuesToInsert);
			File selectedFile = ((UFileProviderDataField) this.getForm().getDataFieldReference("TAT_NAME")).getSelectedFile();
			CheckingTools.failIf((selectedFile == null) || !selectedFile.exists(), "E_REQUIRED_FIELD_TAT_NAME");

			try (InputStream is = new FileInputStream(selectedFile)) {
				EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();

				Object uploadedFileId = this.getService().uploadFile((Map<Object, Object>) attributesValuesToInsert, locator.getSessionId(), is);

				EntityResult resInsert = new EntityResult(EntityResult.OPERATION_SUCCESSFUL, EntityResult.DATA_RESULT);
				resInsert.put(ITaskAttachmentService.COLUMN_ID, uploadedFileId);
				MapTools.safePut(resInsert, ITaskAttachmentService.COLUMN_USER, ((ReferenceLocator) locator).getUser());
				this.notifyPostInsert(attributesValuesToInsert, resInsert);
				return resInsert;
			}
		} catch (Exception ex) {
			this.notifyIncorrectInsert(attributesValuesToInsert, ex);
			if ((ex.getMessage() != null) && ex.getMessage().startsWith("E_")) {
				throw ex;
			}
			throw new Exception("E_PROCESSING_VALUE_ATTACHMENT", ex);
		}
	}

	private ITaskAttachmentFilesHessianService getService() {
		return HessianFactory.getFactory().getService(ITaskAttachmentFilesHessianService.class, "/taskAttachment");
	}

}
