package com.opentach.client.mailmanager.im.attachments;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.mailmanager.MailManagerNaming;
import com.opentach.common.mailmanager.services.IMailManagerService;
import com.utilmize.client.gui.field.UFileProviderDataField;
import com.utilmize.client.gui.field.UFileProviderDataField.IFileProvider;
import com.utilmize.tools.exception.UException;

public class AttachmentFileProvider implements IFileProvider {

	private static final Logger			logger	= LoggerFactory.getLogger(AttachmentFileProvider.class);
	protected UFileProviderDataField	field;

	public AttachmentFileProvider(UFileProviderDataField field) {
		this.field = field;
	}

	@Override
	public File getFile() throws UException, IOException {
		Object attachId = this.getFileId();
		if (attachId != null) {
			return this.getRemoteFile(attachId);
		}
		return null;
	}

	@Override
	public String getFilename() throws Exception {
		Object value = this.field.getRealValue();
		if (value instanceof Map) {
			return (String) ((Map) value).get(MailManagerNaming.MAT_NAME);
		}
		return null;
	}

	public File getRemoteFile(Object fileId) {
		try {
			InputStream is = BeansFactory.getBean(IMailManagerService.class).attachmentDownload(fileId);
			if (is == null) {
				MessageManager.getMessageManager().showMessage(this.field.getParentForm(), MailManagerNaming.W_UNNAVAILABLE_FILE, MessageType.WARNING, new Object[] {});
				return null;
			}
			// Try to rename like original
			String fileName = this.getFilename();
			File tempFile = FileTools.toTemporaryFile(is, "." + "bla"/* this.parentField.getFilterExtension() */);
			if (tempFile != null) {
				try {
					File fcdst = new File(tempFile.getParentFile(), fileName);
					FileTools.moveFile(tempFile, fcdst);
					tempFile = fcdst;
				} catch (Exception ex) {
					AttachmentFileProvider.logger.debug(null, ex);
				}
			}
			return tempFile;
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, AttachmentFileProvider.logger);
			return null;
		}
	}

	protected Object getFileId() {
		return ((Map) this.field.getRealValue()).get(MailManagerNaming.MAT_ID);
	}

}