package com.opentach.server.mailmanager.services;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.gui.ServerLauncherServlet;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.dao.common.INameConverter;
import com.opentach.common.exception.OpentachException;
import com.opentach.common.mailmanager.MailManagerNaming;
import com.opentach.common.mailmanager.dto.Mail;
import com.opentach.common.mailmanager.dto.MailAccount;
import com.opentach.common.mailmanager.dto.MailAttachment;
import com.opentach.common.mailmanager.dto.MailFolder;
import com.opentach.common.mailmanager.services.IMailManagerService;
import com.opentach.common.util.TemplateTools;
import com.opentach.server.mail.MailInfo;
import com.opentach.server.mail.MailService;
import com.opentach.server.mailmanager.dao.MailAccountDao;
import com.opentach.server.mailmanager.dao.MailAttachmentDao;
import com.opentach.server.mailmanager.dao.MailFolderDao;
import com.opentach.server.mailmanager.dao.MailMailDao;
import com.opentach.server.util.UserInfoComponent;
import com.utilmize.server.UReferenceSeeker;

@Service("MailManagerService")
public class MailManagerServiceImpl implements IMailManagerService {

	private static final Logger			logger	= LoggerFactory.getLogger(MailManagerServiceImpl.class);

	@Autowired
	private ServletContext				servletContext;
	@Autowired
	private DefaultOntimizeDaoHelper	daoHelper;
	@Autowired
	private MailAccountDao				mailAccountDao;
	@Autowired
	private MailFolderDao				mailFolderDao;
	@Autowired
	private MailMailDao					mailDao;
	@Autowired
	private MailAttachmentDao			mailAttachmentDao;
	@Autowired
	private UserInfoComponent			userInfo;
	@Autowired
	private TemplateTools				templateManager;

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public MailAccount getUserMailAccount() {
		Map<Object, Object> kv = EntityResultTools.keysvalues(MailManagerNaming.USUARIO, this.userInfo.getUserLogin());
		List<MailAccount> res = this.daoHelper.query(this.mailAccountDao, kv, null, "default", MailAccount.class);
		if (res.isEmpty()) {
			this.daoHelper.insert(this.mailAccountDao,
					EntityResultTools.keysvalues(MailManagerNaming.USUARIO, this.userInfo.getUserLogin(), MailManagerNaming.MAC_NAME, MailManagerNaming.DEFAULT_MAIL_ACCOUNT_NAME));
			res = this.daoHelper.query(this.mailAccountDao, kv, null, "default", MailAccount.class);
		}
		return res.get(0);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public MailFolder getUserFolders() {
		MailAccount userMailAccount = this.getUserMailAccount();
		Map<?, ?> kv = MapTools.keysvalues(MailManagerNaming.MAC_ID, userMailAccount.getMacId());
		List<?> av = Arrays.asList(MailManagerNaming.MFD_ID, MailManagerNaming.MAC_ID, MailManagerNaming.MFD_NAME, MailManagerNaming.MFD_ID_PARENT);
		EntityResult er = this.daoHelper.query(this.mailFolderDao, kv, av);
		return this.convertCategoryResultSet(userMailAccount.getMacId(), er);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public BigDecimal folderUserInsert(String folderName, BigDecimal mfdIdParent) throws OpentachException {
		MailAccount userMailAccount = this.getUserMailAccount();
		Map<String, Object> av = MapTools.keysvalues(//
				MailManagerNaming.MAC_ID, userMailAccount.getMacId(), //
				MailManagerNaming.MFD_NAME, folderName, //
				MailManagerNaming.MFD_ID_PARENT, mfdIdParent//
				);
		EntityResult er = this.daoHelper.insert(this.mailFolderDao, av);
		return (BigDecimal) er.get(MailManagerNaming.MFD_ID);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public void mailFolderUserDelete(BigDecimal mfdId) throws OpentachException {
		// TODO Auto-generated method stub

	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public void mailUserDelete(BigDecimal maiId) throws OpentachException {
		// TODO Auto-generated method stub

	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public Object mailInsert(Mail mail) throws OpentachException {
		MailAccount userMailAccount = this.getUserMailAccount();
		mail.setMacId(userMailAccount.getMacId());
		mail.setMaiSent("N");
		mail.setMaiDate(null);
		EntityResult er = this.daoHelper.insert(this.mailDao, this.bean2Map(mail, this.mailDao.getNameConverter(), this.mailDao.getDataSource(), false));
		return er.get(MailManagerNaming.MAI_ID);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public void mailUpdate(Mail mail) throws OpentachException {
		CheckingTools.failIf(mail.getMaiId() == null, OpentachException.class, MailManagerNaming.ERR_INVALID_FORMAT, new Object[] {});
		MailAccount userMailAccount = this.getUserMailAccount();
		mail.setMacId(userMailAccount.getMacId());
		mail.setMaiSent("N");
		mail.setMaiDate(null);

		this.daoHelper.update(this.mailDao, this.bean2Map(mail, this.mailDao.getNameConverter(), this.mailDao.getDataSource(), true),
				EntityResultTools.keysvalues(MailManagerNaming.MAI_ID, mail.getMaiId()));
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public void mailCancel(Object mailId) throws OpentachException {
		MailAccount userMailAccount = this.getUserMailAccount();
		this.checkMailOwnershipNotSent(mailId, userMailAccount);
		Map<Object, Object> kv = EntityResultTools.keysvalues(//
				MailManagerNaming.MAI_ID, mailId);
		this.mailAttachmentDao.unsafeDelete(kv);
		this.mailDao.delete(kv);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public void mailSend(Object mailId) throws OpentachException {
		try {
			MailAccount userMailAccount = this.getUserMailAccount();
			this.checkMailOwnershipNotSent(mailId, userMailAccount);
			UReferenceSeeker locator = (UReferenceSeeker) this.servletContext.getAttribute(ServerLauncherServlet.COM_ONTIMIZE_GUI_LOCATOR_ATTRIBUTE_CONTEXT);
			Hashtable<Object, Object> kv = EntityResultTools.keysvalues(MailManagerNaming.MAI_ID, mailId);
			Mail mail = this.mailDao.query(kv, null, "default", Mail.class).get(0);
			List<MailAttachment> attachments = this.mailAttachmentDao.query(kv, null, "default", MailAttachment.class);

			Map<String, Object> templateParameters = new HashMap<>();
			templateParameters.put("body", StringEscapeUtils.escapeHtml4(mail.getMaiBody() == null ? "" : mail.getMaiBody()).replaceAll("\n", "<br>"));
			String body = this.templateManager.fillTemplateByClasspath("templates-mailmanager/agent_mail_template.vm", templateParameters);

			MailInfo info = new MailInfo();
			info.setSubject(mail.getMaiSubject());
			info.setTo(ParseUtilsExtended.getStringList(mail.getMaiTo(), ";", new ArrayList<>()).toArray(new String[] {}));
			info.setCc(ParseUtilsExtended.getStringList(mail.getMaiCc(), ";", new ArrayList<>()).toArray(new String[] {}));
			info.setBcc(ParseUtilsExtended.getStringList(mail.getMaiBcc(), ";", new ArrayList<>()).toArray(new String[] {}));
			info.setContent(body);
			info.setContentType("text/html; charset=utf-8");
			// info.setAckAddress(ackAddress);
			List<File> attach = new ArrayList<>();
			List<String> attachName = new ArrayList<>();
			for (MailAttachment attch : attachments) {
				File tempFile = File.createTempFile("mail_send", "data");
				FileTools.copyFile(new ByteArrayInputStream(attch.getMatContent()), tempFile);
				attach.add(tempFile);
				attachName.add(attch.getMatName());
			}
			info.setAttchFiles(attach.toArray(new File[] {}));
			info.setAttchNames(attachName.toArray(new String[] {}));
			locator.getService(MailService.class).sendMail(new DummyMailComposer(info));
			this.mailDao.update(EntityResultTools.keysvalues(MailManagerNaming.MAI_SENT, "S", MailManagerNaming.MAI_DATE, new Date()), kv);
			try {
				for (File file : attach) {
					file.delete();
				}
			} catch (Exception err) {
				MailManagerServiceImpl.logger.error(null, err);
			}
		} catch (OpentachException err) {
			throw err;
		} catch (Exception err) {
			throw new OpentachException(err.getMessage(), err);
		}
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult mailUserQuery(Map<?, ?> filter, List<?> attrs) throws OpentachException {
		MailAccount userMailAccount = this.getUserMailAccount();
		((Map<Object, Object>) filter).put(MailManagerNaming.MAC_ID, userMailAccount.getMacId());
		return this.daoHelper.query(this.mailDao, filter, attrs, Arrays.asList(MailManagerNaming.MAI_CREATION_DATE), "company");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public void moveMailsToFolder(BigDecimal mfdId, ArrayList<Serializable> data) throws OpentachException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object attachmentQuery(Map<?, ?> filter, List<?> attrs) throws OpentachException {
		Object maiId = filter.get(MailManagerNaming.MAI_ID);
		CheckingTools.failIf(maiId == null, MailManagerNaming.ERR_MANDATORY_MAI_ID, new Object[] {});
		if (!this.userInfo.isAdmin() && !this.userInfo.isOperator()) {
			MailAccount userMailAccount = this.getUserMailAccount();
			this.checkMailOwnership(maiId, userMailAccount);
		}
		return this.daoHelper.query(this.mailAttachmentDao, filter, attrs);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public Object attachmentAdd(Object mailId, String name, InputStream content) throws OpentachException {
		// confirmamos que es un mail del usuario
		MailAccount userMailAccount = this.getUserMailAccount();
		this.checkMailOwnershipNotSent(mailId, userMailAccount);
		EntityResult er = this.mailAttachmentDao.insert(EntityResultTools.keysvalues(//
				MailManagerNaming.MAI_ID, mailId, //
				MailManagerNaming.MAT_CONTENT, content, //
				MailManagerNaming.MAT_NAME, name//
				));
		// TODO: update size
		return er.get(MailManagerNaming.MAT_ID);
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public void attachmentDelete(Object matId) throws OpentachException {
		// confirmamos que es un mail del usuario
		MailAccount userMailAccount = this.getUserMailAccount();
		EntityResult res = this.mailAttachmentDao.query(EntityResultTools.keysvalues(MailManagerNaming.MAT_ID, matId), Arrays.asList(MailManagerNaming.MAI_ID), null, "default");
		CheckingTools.failIf(res.calculateRecordNumber() != 1, OpentachException.class, MailManagerNaming.ERR_INVALID_MAT_ID, new Object[] {});
		Object maiId = res.getRecordValues(0).get(MailManagerNaming.MAI_ID);
		this.checkMailOwnershipNotSent(maiId, userMailAccount);
		this.mailAttachmentDao.delete(EntityResultTools.keysvalues(//
				MailManagerNaming.MAT_ID, matId//
				));
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	@Transactional(rollbackFor = Throwable.class)
	public InputStream attachmentDownload(Object matId) throws OpentachException {
		EntityResult res = this.mailAttachmentDao.query(EntityResultTools.keysvalues(MailManagerNaming.MAT_ID, matId),
				Arrays.asList(MailManagerNaming.MAI_ID, MailManagerNaming.MAT_CONTENT), null, "default");
		CheckingTools.failIf(res.calculateRecordNumber() != 1, OpentachException.class, MailManagerNaming.ERR_INVALID_MAT_ID, new Object[] {});
		Map<String, Object> record = res.getRecordValues(0);
		if (!this.userInfo.isAdmin() && !this.userInfo.isOperator()) {
			// confirmamos que es un mail del usuario
			MailAccount userMailAccount = this.getUserMailAccount();
			Object maiId = record.get(MailManagerNaming.MAI_ID);
			this.checkMailOwnership(maiId, userMailAccount);
		}
		return new ByteArrayInputStream((byte[]) record.get(MailManagerNaming.MAT_CONTENT));
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult mailAdminQuery(Map<?, ?> filter, List<?> attrs) throws OpentachException {
		return this.daoHelper.query(this.mailDao, filter, attrs, Arrays.asList(MailManagerNaming.MAI_CREATION_DATE), "admin");
	}

	@Override
	@Secured({ PermissionsProviderSecured.SECURED })
	public EntityResult agentQuery(Map<?, ?> filter, List<?> attrs) throws OpentachException {
		return this.daoHelper.query(this.mailDao, filter, attrs, Arrays.asList(MailManagerNaming.USUARIO), "agents");
	}

	/**
	 * Convert category result set.
	 *
	 * @param idDocument
	 *            the id document
	 * @param er
	 *            the er
	 * @return the DMS category
	 */
	private MailFolder convertCategoryResultSet(BigDecimal idDocument, EntityResult er) {
		MailFolder root = new MailFolder(idDocument, null, "/", null, null);
		this.expandCategory(root, er);
		return root;
	}

	private void checkMailOwnershipNotSent(Object mailId, MailAccount userMailAccount) throws OpentachException {
		Map<Object, Object> kv = EntityResultTools.keysvalues(//
				MailManagerNaming.MAI_ID, mailId, //
				MailManagerNaming.MAI_SENT, "N", //
				MailManagerNaming.MAC_ID, userMailAccount.getMacId());
		EntityResult er = this.mailDao.query(kv, Arrays.asList(MailManagerNaming.MAI_ID), null, "default");
		CheckingTools.failIf((er.calculateRecordNumber() != 1) || !mailId.equals(er.getRecordValues(0).get(MailManagerNaming.MAI_ID)), OpentachException.class,
				MailManagerNaming.ERR_NOT_CANCELLED, new Object[] {});
	}

	private void checkMailOwnership(Object mailId, MailAccount userMailAccount) throws OpentachException {
		Map<Object, Object> kv = EntityResultTools.keysvalues(//
				MailManagerNaming.MAI_ID, mailId, //
				MailManagerNaming.MAC_ID, userMailAccount.getMacId());
		EntityResult er = this.mailDao.query(kv, Arrays.asList(MailManagerNaming.MAI_ID), null, "default");
		CheckingTools.failIf((er.calculateRecordNumber() != 1) || !mailId.equals(er.getRecordValues(0).get(MailManagerNaming.MAI_ID)), OpentachException.class,
				MailManagerNaming.ERR_NOT_CANCELLED, new Object[] {});
	}

	/**
	 * Expand category.
	 *
	 * @param root
	 *            the root
	 * @param er
	 *            the er
	 * @param idDocument
	 *            the id document
	 */
	private void expandCategory(MailFolder root, EntityResult er) {
		List<MailFolder> categories = this.removeCategoriesForParentId(er, root);
		root.setChildren(categories);
		for (MailFolder category : root.getChildren()) {
			this.expandCategory(category, er);
		}
	}

	/**
	 * Removes the categories for parent id.
	 *
	 * @param er
	 *            the er
	 * @param parentCategory
	 *            the parent category
	 * @param idDocument
	 *            the id document
	 * @return the list
	 */
	private List<MailFolder> removeCategoriesForParentId(EntityResult er, MailFolder parentCategory) {
		if (er.isEmpty()) {
			return new ArrayList<>();
		}
		List<Serializable> listIdParentCategory = (List<Serializable>) er.get(MailManagerNaming.MFD_ID_PARENT);
		List<MailFolder> res = new ArrayList<>();
		if (listIdParentCategory != null) {
			for (int i = 0; i < listIdParentCategory.size(); i++) {
				if (ObjectTools.safeIsEquals(listIdParentCategory.get(i), parentCategory.getMfdId())) {
					Map<? extends Serializable, ? extends Serializable> recordValues = er.getRecordValues(i);
					BigDecimal mfdId = (BigDecimal) recordValues.remove(MailManagerNaming.MFD_ID);
					String categoryName = (String) recordValues.remove(MailManagerNaming.MFD_NAME);
					BigDecimal macId = (BigDecimal) recordValues.remove(MailManagerNaming.MAC_ID);
					res.add(new MailFolder(mfdId, macId, categoryName, null, parentCategory));
					er.deleteRecord(i);
					i--;
				}
			}
		}
		return res;
	}

	protected Map<String, Object> bean2Map(Object bean, INameConverter propertyConverter, DataSource ds, boolean generateNullValue) throws OpentachException {
		try {
			Map<String, Object> res = new HashMap<>();
			PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(bean.getClass());
			for (PropertyDescriptor pd : pds) {
				if (pd.getName().equals("class")) {
					continue;
				}
				String columnName = propertyConverter.convertToDb(bean.getClass(), pd.getName(), ds);
				Object value = this.getPropertyValue(pd, bean);
				if (value != null) {
					res.put(columnName, value);
				} else if (generateNullValue) {
					res.put(columnName, new NullValue());
				}
			}
			return res;
		} catch (Exception err) {
			throw new OpentachException(err);
		}
	}

	private Object getPropertyValue(PropertyDescriptor pd, Object bean) throws Exception {
		final Method readMethod = pd.getReadMethod();
		if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers()) && !readMethod.isAccessible()) {
			if (System.getSecurityManager() != null) {
				AccessController.doPrivileged(new PrivilegedAction<Object>() {
					@Override
					public Object run() {
						readMethod.setAccessible(true);
						return null;
					}
				});
			} else {
				readMethod.setAccessible(true);
			}
		}
		if (System.getSecurityManager() != null) {
			try {
				return AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
					@Override
					public Object run() throws Exception {
						return readMethod.invoke(bean, (Object[]) null);
					}
				}, AccessController.getContext());
			} catch (PrivilegedActionException pae) {
				throw pae.getException();
			}
		}
		return readMethod.invoke(bean, (Object[]) null);
	}
}
