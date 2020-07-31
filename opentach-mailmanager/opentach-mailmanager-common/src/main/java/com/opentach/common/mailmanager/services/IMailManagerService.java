package com.opentach.common.mailmanager.services;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResult;
import com.opentach.common.exception.OpentachException;
import com.opentach.common.mailmanager.dto.Mail;
import com.opentach.common.mailmanager.dto.MailAccount;
import com.opentach.common.mailmanager.dto.MailFolder;

public interface IMailManagerService {

	MailAccount getUserMailAccount() throws OpentachException;

	MailFolder getUserFolders() throws OpentachException;

	BigDecimal folderUserInsert(String folderName, BigDecimal mfdIdParent) throws OpentachException;

	void mailFolderUserDelete(BigDecimal mfdId) throws OpentachException;

	void mailUserDelete(BigDecimal maiId) throws OpentachException;

	EntityResult mailUserQuery(Map<?, ?> filter, List<?> attrs) throws OpentachException;

	void moveMailsToFolder(BigDecimal mfdId, ArrayList<Serializable> data) throws OpentachException;

	void mailSend(Object mailId) throws OpentachException;

	Object mailInsert(Mail mail) throws OpentachException;

	void mailUpdate(Mail mail) throws OpentachException;

	void mailCancel(Object mailId) throws OpentachException;

	Object attachmentQuery(Map<?, ?> filter, List<?> attrs) throws OpentachException;

	Object attachmentAdd(Object mailId, String name, InputStream content) throws OpentachException;

	InputStream attachmentDownload(Object fileId) throws OpentachException;

	void attachmentDelete(Object fileId) throws OpentachException;

	EntityResult mailAdminQuery(Map<?, ?> filter, List<?> attrs) throws OpentachException;

	EntityResult agentQuery(Map<?, ?> filter, List<?> attrs) throws OpentachException;

}
