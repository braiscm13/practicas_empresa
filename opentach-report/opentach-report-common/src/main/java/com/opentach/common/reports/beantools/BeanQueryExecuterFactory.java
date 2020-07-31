package com.opentach.common.reports.beantools;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;

public class BeanQueryExecuterFactory implements QueryExecuterFactory {

	public BeanQueryExecuterFactory() {
		super();
	}

	@Override
	public JRQueryExecuter createQueryExecuter(JRDataset dataset, Map<String, ? extends JRValueParameter> parameters) throws JRException {
		return null;
	}

	@Override
	public Object[] getBuiltinParameters() {
		return null;
	}

	@Override
	public JRQueryExecuter createQueryExecuter(JasperReportsContext jasperReportsContext, JRDataset dataset,
			Map<String, ? extends JRValueParameter> parameters) throws JRException {
		return new UsosVehiculoBeanQueryExecuter(jasperReportsContext, dataset, parameters);
	}

	@Override
	public boolean supportsQueryParameterType(String className) {
		return true;
	}

}
