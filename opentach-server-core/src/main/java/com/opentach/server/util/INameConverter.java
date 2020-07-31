package com.opentach.server.util;


/**
 * The Interface IBeanPropertyToDBConverter. Encargada de convertir los nombres de las propiedades de los beans a nombres de base de datos y
 * viceversa.
 */
public interface INameConverter {

	/**
	 * Convert to db name.
	 *
	 * @param beanClass
	 *            the bean class
	 * @param beanProperty
	 *            the bean property
	 * @return the string
	 */
	String convertToDb(Class<?> beanClass, String beanProperty);

	/**
	 * Convert to db name.
	 *
	 * @param beanClass
	 *            the bean class
	 * @param dbColumn
	 *            the db column
	 * @return the string
	 */
	String convertToBean(Class<?> beanClass, String dbColumn);

}
