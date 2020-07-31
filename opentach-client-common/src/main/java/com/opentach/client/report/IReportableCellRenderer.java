package com.opentach.client.report;

public interface IReportableCellRenderer {

	/**
	 * Devuelve un patr�n para un formateador
	 * 
	 * @param engineId
	 * @return
	 */
	String getReportPattern(String engineId);

	/**
	 * Devuelve la clase que se usar� como formateador En caso de Jasper la
	 * clase debe extender java.text.Format
	 * 
	 * @param engineId
	 * @return
	 */
	Object getReportExpression(String engineId);

	// TODO ver si hay alg�n problema por devolver directamente el object si no
	// existe el jar de jasper o freereport en el proyecto

	/**
	 * Indica si se le pueden aplicar funciones a la columna
	 */
	boolean isOperable();

}
