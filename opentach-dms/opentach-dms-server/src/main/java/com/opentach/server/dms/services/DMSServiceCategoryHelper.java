package com.opentach.server.dms.services;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.SQLStatement;
import com.ontimize.db.TableEntity;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ListTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.common.dms.DMSCategory;
import com.opentach.common.dms.DMSNaming;
import com.opentach.common.dms.DmsException;
import com.opentach.server.IOpentachServerLocator;

/**
 * The Class DMSServiceCategoryHelper.
 */
public class DMSServiceCategoryHelper extends AbstractDMSServiceHelper {

	private static final Logger		logger	= LoggerFactory.getLogger(DMSServiceCategoryHelper.class);

	/** The dms file helper. */
	protected DMSServiceFileHelper	dmsFileHelper;

	public DMSServiceCategoryHelper(IOpentachServerLocator locator, DMSServiceFileHelper fileHelper) {
		super(locator);
		this.dmsFileHelper = fileHelper;
	}

	/**
	 * Category get for document.
	 *
	 * @param idDocument
	 *            the id document
	 * @param attributes
	 *            the attributes
	 * @return the DMS category
	 * @throws DmsException
	 */
	public DMSCategory categoryGetForDocument(Serializable idDocument, List<?> attributes, Connection con) throws DmsException {

		try {
			CheckingTools.failIfNull(idDocument, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			Vector<?> attribs = (Vector<?>) attributes;
			if (attribs == null) {
				attribs = new Vector<Object>();
			}
			ListTools.safeAdd((List<String>) attribs, this.getColumnHelper().getCategoryIdColumn());
			ListTools.safeAdd((List<String>) attribs, this.getColumnHelper().getCategoryNameColumn());
			ListTools.safeAdd((List<String>) attribs, this.getColumnHelper().getCategoryParentColumn());

			Hashtable<Object, Object> filter = EntityResultTools.keysvalues(this.getColumnHelper().getDocumentIdColumn(), idDocument);
			EntityResult er = this.getCategoryEntity().query(filter, attribs, this.getSessionId(-1, this.getCategoryEntity()), con);
			return this.convertCategoryResultSet(idDocument, er);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Category update.
	 *
	 * @param idCategory
	 *            the id category
	 * @param av
	 *            the av
	 * @throws DmsException
	 */
	public void categoryUpdate(Serializable idCategory, Hashtable<?, ?> av, Connection con) throws DmsException {

		try {
			CheckingTools.failIfNull(idCategory, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			Hashtable<String, Object> filter = new Hashtable<String, Object>();
			MapTools.safePut(filter, this.getColumnHelper().getCategoryIdColumn(), idCategory);
			this.getCategoryEntity().update(av, filter, this.getSessionId(-1, this.getCategoryEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Category delete.
	 *
	 * @param idCategory
	 *            the id category
	 * @throws DmsException
	 */
	public void categoryDelete(Serializable idCategory, Connection con) throws DmsException {

		try {
			CheckingTools.failIfNull(idCategory, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			Hashtable<String, Object> filter = new Hashtable<String, Object>();
			MapTools.safePut(filter, this.getColumnHelper().getCategoryIdColumn(), idCategory);

			// Tenemos que poner a null el id_category_parent de las categorías que tenga a esta como padre
			Hashtable<String, Object> avUpdate = new Hashtable<String, Object>();
			MapTools.safePut(avUpdate, this.getColumnHelper().getCategoryParentColumn(), new NullValue());
			Hashtable<String, Object> kvUpdate = new Hashtable<String, Object>();
			MapTools.safePut(kvUpdate, this.getColumnHelper().getCategoryParentColumn(), idCategory);
			TableEntity categoryEntity = (TableEntity) this.getCategoryEntity();
			SQLStatement stSQL = SQLStatementBuilder.getSQLStatementHandler(categoryEntity.getDatabaseConnectionManager().getDatabase())
					.createUpdateQuery(categoryEntity.getDatabaseConnectionManager()
							.getSchema() != null ? (categoryEntity.getDatabaseConnectionManager().getSchema() + "." + categoryEntity.getTable()) : categoryEntity.getTable(),
									avUpdate, kvUpdate);
			categoryEntity.executePreparedStatement(stSQL.getSQLStatement(), stSQL.getValues(), con, this.getSessionId(-1, this.getCategoryEntity()));

			// Tenemos que quitar todos los ficheros de la categoria
			avUpdate = new Hashtable<String, Object>();
			MapTools.safePut(avUpdate, this.getColumnHelper().getCategoryIdColumn(), new NullValue());
			kvUpdate = new Hashtable<String, Object>();
			MapTools.safePut(kvUpdate, this.getColumnHelper().getCategoryIdColumn(), idCategory);
			TableEntity fileEntity = (TableEntity) this.getFileEntity();
			stSQL = SQLStatementBuilder.getSQLStatementHandler(fileEntity.getDatabaseConnectionManager().getDatabase()).createUpdateQuery(
					fileEntity.getDatabaseConnectionManager()
					.getSchema() != null ? (fileEntity.getDatabaseConnectionManager().getSchema() + "." + fileEntity.getTable()) : fileEntity.getTable(),
							avUpdate, kvUpdate);
			fileEntity.executePreparedStatement(stSQL.getSQLStatement(), stSQL.getValues(), con, this.getSessionId(-1, this.getCategoryEntity()));

			this.getCategoryEntity().delete(filter, this.getSessionId(-1, this.getCategoryEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Category insert.
	 *
	 * @param idDocument
	 *            the id document
	 * @param name
	 *            the name
	 * @param idParentCategory
	 *            the id parent category
	 * @param otherData
	 *            the other data
	 * @return the object
	 * @throws DmsException
	 */
	public Serializable categoryInsert(Serializable idDocument, String name, Serializable idParentCategory, Map<?, ?> otherData, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(idDocument, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			CheckingTools.failIfNull(name, DMSNaming.ERROR_CATEGORY_NAME_MANDATORY);
			Hashtable<Object, Object> av = new Hashtable<>(otherData == null ? new HashMap<Object, Object>() : otherData);
			MapTools.safePut(av, this.getColumnHelper().getDocumentIdColumn(), idDocument);
			MapTools.safePut(av, this.getColumnHelper().getCategoryNameColumn(), name);
			MapTools.safePut(av, this.getColumnHelper().getCategoryParentColumn(), idParentCategory);
			return (Serializable) this.getCategoryEntity().insert(av, this.getSessionId(-1, this.getCategoryEntity()), con).get(this.getColumnHelper().getCategoryIdColumn());
		} catch (Exception err) {
			throw new DmsException(err);
		}
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
	private DMSCategory convertCategoryResultSet(Serializable idDocument, EntityResult er) {
		DMSCategory root = new DMSCategory(idDocument, null, "/", null, null);
		this.expandCategory(root, er, idDocument);
		return root;
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
	private void expandCategory(DMSCategory root, EntityResult er, Serializable idDocument) {
		List<DMSCategory> categories = this.removeCategoriesForParentId(er, root, idDocument);
		root.setChildren(categories);
		for (DMSCategory category : root.getChildren()) {
			this.expandCategory(category, er, idDocument);
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
	private List<DMSCategory> removeCategoriesForParentId(EntityResult er, DMSCategory parentCategory, Serializable idDocument) {
		List<Serializable> listIdParentCategory = (List<Serializable>) er.get(this.getColumnHelper().getCategoryParentColumn());
		List<DMSCategory> res = new ArrayList<DMSCategory>();
		if (listIdParentCategory != null) {
			for (int i = 0; i < listIdParentCategory.size(); i++) {
				if (ObjectTools.safeIsEquals(listIdParentCategory.get(i), parentCategory.getIdCategory())) {
					Map<? extends Serializable, ? extends Serializable> recordValues = er.getRecordValues(i);
					Serializable idCategory = recordValues.remove(this.getColumnHelper().getCategoryIdColumn());
					String categoryName = (String) recordValues.remove(this.getColumnHelper().getCategoryNameColumn());
					res.add(new DMSCategory(idDocument, idCategory, categoryName, recordValues, parentCategory));
					er.deleteRecord(i);
					i--;
				}
			}
		}
		return res;
	}

	public TransactionalEntity getCategoryEntity() {
		return this.getEntity("EDocCategory");
	}

	public TransactionalEntity getFileEntity() {
		return this.getEntity("EDocFile");
	}
}
