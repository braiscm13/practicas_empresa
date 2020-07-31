package com.opentach.server.util;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;

import com.ontimize.jee.common.tools.CheckingTools;

/**
 * The Class BeanPropertyRowMapper.
 *
 * @param <T>
 *            the generic type
 */
public class BeanPropertyRowMapper<T> {

	private static final Logger				logger	= LoggerFactory.getLogger(BeanPropertyRowMapper.class);

	/** The class we are mapping to */
	private Class<T>						mappedClass;

	/** Map of the fields we provide mapping for */
	private Map<String, PropertyDescriptor>	mappedFields;

	/** The property converter. */
	private INameConverter		propertyConverter;

	/**
	 * Instantiates a new bean property row mapper.
	 *
	 * @param propertyConverter
	 *            the property converter
	 * @param mappedClass
	 *            the mapped class
	 * @param checkFullyPopulated
	 *            the check fully populated
	 */
	public BeanPropertyRowMapper(INameConverter propertyConverter, Class<T> mappedClass) {
		super();
		this.initialize(propertyConverter, mappedClass);
	}

	/**
	 * Gets the property converter.
	 *
	 * @return the property converter
	 */
	public INameConverter getPropertyConverter() {
		return this.propertyConverter;
	}

	/**
	 * Initialize the mapping metadata for the given class.
	 *
	 * @param mappedClass
	 *            the mapped class.
	 */
	protected void initialize(INameConverter propertyConverter, Class<T> mappedClass) {
		this.propertyConverter = propertyConverter;
		this.mappedClass = mappedClass;
		this.mappedFields = new HashMap<String, PropertyDescriptor>();
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
		for (PropertyDescriptor pd : pds) {
			if (pd.getWriteMethod() != null) {
				this.mappedFields.put(propertyConverter.convertToDb(mappedClass, pd.getName()).toLowerCase(), pd);
			}
		}
	}

	/**
	 * Get the class that we are mapping to.
	 */
	public final Class<T> getMappedClass() {
		return this.mappedClass;
	}

	/**
	 * Extract the values for all columns in the current row. <p>Utilizes public setters and result set metadata.
	 *
	 * @see java.sql.ResultSetMetaData
	 */
	public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
		CheckingTools.failIf(this.mappedClass == null, "Mapped class was not specified");
		T mappedObject = BeanUtils.instantiate(this.mappedClass);
		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
		this.initBeanWrapper(bw);

		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();

		for (int index = 1; index <= columnCount; index++) {
			String column = JdbcUtils.lookupColumnName(rsmd, index);
			PropertyDescriptor pd = this.mappedFields.get(column.toLowerCase());
			if (pd != null) {
				Object value = this.getColumnValue(rs, index, pd);
				if (BeanPropertyRowMapper.logger.isDebugEnabled() && (rowNumber == 0)) {
					BeanPropertyRowMapper.logger.debug("Mapping column '" + column + "' to property '" + pd.getName() + "' of type " + pd
							.getPropertyType());
				}
				try {
					bw.setPropertyValue(pd.getName(), value);
				} catch (TypeMismatchException e) {
					BeanPropertyRowMapper.logger
					.error("Intercepted TypeMismatchException for row " + rowNumber + " and column '" + column + "' with value " + value + " when setting property '" + pd
							.getName() + "' of type " + pd.getPropertyType() + " on object: " + mappedObject);
					throw e;
				}
			}
		}

		return mappedObject;
	}

	/**
	 * Initialize the given BeanWrapper to be used for row mapping. To be called for each row. <p>The default implementation is empty. Can be
	 * overridden in subclasses.
	 *
	 * @param bw
	 *            the BeanWrapper to initialize
	 */
	protected void initBeanWrapper(BeanWrapper bw) {}

	/**
	 * Retrieve a JDBC object value for the specified column. <p>The default implementation calls
	 * {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)}. Subclasses may override this to check specific value types upfront, or to
	 * post-process values return from {@code getResultSetValue}.
	 *
	 * @param rs
	 *            is the ResultSet holding the data
	 * @param index
	 *            is the column index
	 * @param pd
	 *            the bean property that each result object is expected to match (or {@code null} if none specified)
	 * @return the Object value
	 * @throws SQLException
	 *             in case of extraction failure
	 * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)
	 */
	protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
	}

}