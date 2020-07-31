package com.opentach.test.report;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.opentach.common.reports.beantools.UsosVehiculoBean;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class Tester {

	public Tester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		try {
			InputStream inputStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("com/opentach/reports/vehiculosporconductor.jrxml");
			JasperReport report = JasperCompileManager.compileReport(inputStream);
			// report.setProperty(QueryExecuterFactory.QUERY_EXECUTER_FACTORY_PREFIX + "bsql", BeanQueryExecuterFactory.class.getName());
			// JRProperties.setProperty("net.sf.jasperreports.query.executer.factory.bsql", BeanQueryExecuterFactory.class.getName());
			// JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance()).setProperty("net.sf.jasperreports.query.executer.factory.bsql",
			// BeanQueryExecuterFactory.class.getName());
			// JasperReportsContext context = new LocalJasperReportsContext(null);
			// context.setProperty(QueryExecuterFactory.QUERY_EXECUTER_FACTORY_PREFIX + "bsql", BeanQueryExecuterFactory.class.getName());

			ArrayList<UsosVehiculoBean> dataList = Tester.getDataBeanList();

			JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataList);
			Map<String, ArrayList<UsosVehiculoBean>> map = Tester.splitByDriver(dataList);

			Map<String, Object> parameters = new HashMap<>();
			parameters.put("BEAN_MAP", map);
			// parameters.put(QueryExecuterFactory.QUERY_EXECUTER_FACTORY_PREFIX + "bsql", BeanQueryExecuterFactory.class.getName());
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, beanColDataSource);
			// JasperPrint jasperPrint = JRFiller.fill(context, report, parameters, beanColDataSource);
			com.ontimize.report.utils.JasperViewerDialog.viewReport(null, jasperPrint, "titulo");
		} catch (JRException e) {
			e.printStackTrace();
		}

	}

	private static Map<String, ArrayList<UsosVehiculoBean>> splitByDriver(ArrayList<UsosVehiculoBean> dataList) {
		HashMap<String, ArrayList<UsosVehiculoBean>> res = new HashMap<>();
		for (UsosVehiculoBean bean : dataList) {
			String idconductor = bean.getIDCONDUCTOR();
			ArrayList<UsosVehiculoBean> list = res.get(idconductor);
			if (list == null) {
				list = new ArrayList<UsosVehiculoBean>();
				res.put(idconductor, list);
			}
			list.add(bean);

		}
		return res;
	}

	private static ArrayList<UsosVehiculoBean> getDataBeanList() {
		ArrayList<UsosVehiculoBean> list = new ArrayList<UsosVehiculoBean>();
		String cG_CONTRATO = "66666";
		String dNI = "12345678A";
		String nUMREQ = "66666";
		String iDCONDUCTOR = "111";
		String aPELLIDOS = "Romero Riveiro";
		String nOMBRE = "Jok";
		String mATRICULA = "1234HGG";
		String dSCR = "hola";
		list.add(new UsosVehiculoBean(//
				cG_CONTRATO, //
				dNI,//
				nUMREQ,//
				iDCONDUCTOR,//
				aPELLIDOS, nOMBRE,//
				mATRICULA,//
				dSCR, //
				new Timestamp(2015, 5, 5, 8, 40, 25, 0), //
				new Timestamp(2015, 5, 5, 9, 40, 25, 0), //
				new BigDecimal(60), //
				new BigDecimal(55555), //
				new BigDecimal(55666), //
				new BigDecimal(111), //
				"vigo", //
				"santiago", //
				"coruña", //
				"5", //
				"2", //
				"8", //
				"5", //
				"5", //
				"8", //
				new BigDecimal(25), //
				"2015", //
				iDCONDUCTOR, //
				new BigDecimal(1), //
				new Timestamp(2015, 5, 5, 8, 40, 25, 0), //
				new Timestamp(2015, 5, 5, 9, 40, 25, 0), //
				"8"));
		list.add(new UsosVehiculoBean(//
				cG_CONTRATO, //
				dNI,//
				nUMREQ,//
				iDCONDUCTOR,//
				aPELLIDOS, nOMBRE,//
				"4321BBA",//
				dSCR, //
				new Timestamp(2015, 5, 5, 9, 40, 25, 0), //
				new Timestamp(2015, 5, 5, 10, 55, 25, 0), //
				new BigDecimal(75), //
				new BigDecimal(55555), //
				new BigDecimal(55666), //
				new BigDecimal(111), //
				"vigo", //
				"santiago", //
				"coruña", //
				"5", //
				"2", //
				"8", //
				"5", //
				"5", //
				"8", //
				new BigDecimal(25), //
				"2015", //
				iDCONDUCTOR, //
				new BigDecimal(1), //
				new Timestamp(2015, 5, 5, 8, 40, 25, 0), //
				new Timestamp(2015, 5, 5, 9, 40, 25, 0), //
				"8"));
		iDCONDUCTOR = "666";
		dNI = "6666666A";
		nOMBRE = "pepito";
		list.add(new UsosVehiculoBean(//
				cG_CONTRATO, //
				dNI,//
				nUMREQ,//
				iDCONDUCTOR,//
				aPELLIDOS, nOMBRE,//
				mATRICULA,//
				dSCR, //
				new Timestamp(2015, 5, 5, 8, 40, 25, 0), //
				new Timestamp(2015, 5, 5, 9, 40, 25, 0), //
				new BigDecimal(60), //
				new BigDecimal(55555), //
				new BigDecimal(55666), //
				new BigDecimal(111), //
				"vigo", //
				"santiago", //
				"coruña", //
				"5", //
				"2", //
				"8", //
				"5", //
				"5", //
				"8", //
				new BigDecimal(25), //
				"2015", //
				iDCONDUCTOR, //
				new BigDecimal(1), //
				new Timestamp(2015, 5, 5, 8, 40, 25, 0), //
				new Timestamp(2015, 5, 5, 9, 40, 25, 0), //
				"8"));
		list.add(new UsosVehiculoBean(//
				cG_CONTRATO, //
				dNI,//
				nUMREQ,//
				iDCONDUCTOR,//
				aPELLIDOS, nOMBRE,//
				"4321BBA",//
				dSCR, //
				new Timestamp(2015, 5, 5, 9, 40, 25, 0), //
				new Timestamp(2015, 5, 5, 10, 55, 25, 0), //
				new BigDecimal(75), //
				new BigDecimal(55555), //
				new BigDecimal(55666), //
				new BigDecimal(111), //
				"vigo", //
				"santiago", //
				"coruña", //
				"5", //
				"2", //
				"8", //
				"5", //
				"5", //
				"8", //
				new BigDecimal(25), //
				"2015", //
				iDCONDUCTOR, //
				new BigDecimal(1), //
				new Timestamp(2015, 5, 5, 8, 40, 25, 0), //
				new Timestamp(2015, 5, 5, 9, 40, 25, 0), //
				"8"));
		return list;
	}

}
