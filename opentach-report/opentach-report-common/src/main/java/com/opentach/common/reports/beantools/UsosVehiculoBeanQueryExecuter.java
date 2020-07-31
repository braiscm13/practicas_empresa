package com.opentach.common.reports.beantools;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRQueryExecuter;

public class UsosVehiculoBeanQueryExecuter implements JRQueryExecuter {

	private final JasperReportsContext						jasperContext;
	private final JRDataset									dataset;
	private final Map<String, ? extends JRValueParameter>	parameters;

	public UsosVehiculoBeanQueryExecuter(JasperReportsContext jasperReportsContext, JRDataset dataset,
			Map<String, ? extends JRValueParameter> parameters) {
		super();
		this.jasperContext = jasperReportsContext;
		this.dataset = dataset;
		this.parameters = parameters;
	}

	@Override
	public JRDataSource createDatasource() throws JRException {
		Map<String, List<UsosVehiculoBean>> beansMap = (Map<String, List<UsosVehiculoBean>>) this.getParameter("BEAN_MAP");
		String idConductor = (String) this.getParameter("idconductor", "pidconductor");
		List<UsosVehiculoBean> beansList = beansMap.get(idConductor);
		if (this.dataset.getName().contains("Porcent_por_matricula") || this.dataset.getName().contains("Percent_por_dia") || this.dataset.getName()
				.contains("Percent_por_semana") || this.dataset.getName().contains("Percent_por_mes")) {
			String diaAnual = (String) this.getParameter("dia_anual");
			String week = (String) this.getParameter("semana");
			String month = (String) this.getParameter("mes");
			String year = (String) this.getParameter("anho");
			return new BeanDataSource(this.computePercentPerPlate(beansList, diaAnual, week, month, year));
		} else if (this.dataset.getName().contains("resumenDia_nogroup") || this.dataset.getName().contains("resumensemana_nogroup") || this.dataset
				.getName().contains("resumenMensual_nogroup")) {

			String diaAnual = (String) this.getParameter("dia_anual");
			String week = (String) this.getParameter("semana");
			String month = (String) this.getParameter("mes");
			String year = (String) this.getParameter("anho");
			return new BeanDataSource(this.computeResumenNoGroup(beansList, diaAnual, week, month, year));

		} else if (this.dataset.getName().contains("resumenDia") || this.dataset.getName().contains("resumensemana") || this.dataset.getName()
				.contains("resumenMensual")) {
			String diaAnual = (String) this.getParameter("dia_anual");
			String week = (String) this.getParameter("semana");
			String month = (String) this.getParameter("mes");
			String year = (String) this.getParameter("anho");
			return new BeanDataSource(this.computeResumen(beansList, diaAnual, week, month, year));
		}
		return null;
	}

	private Object getParameter(String... names) {
		for (String name : names) {
			JRValueParameter parameter = this.parameters.get(name);
			if (parameter != null) {
				return parameter.getValue();
			}
		}
		return null;
	}

	private List<BeanPercentByPlate> computePercentPerPlate(List<UsosVehiculoBean> beansList, String diaAnual, String week, String month, String year) {
		List<BeanPercentByPlate> res = new ArrayList<>();
		MathContext mc = new MathContext(5, RoundingMode.HALF_UP);

		Map<String, BeanPercentByPlate> mapRes = new HashMap<>();
		double hrs = 0;
		double kms = 0;
		for (UsosVehiculoBean ob : beansList) {
			if (this.safeCompare(diaAnual, ob.getDIA_ANUAL()) && this.safeCompare(week, ob.getSEMANA()) && this.safeCompare(month, ob.getMES()) && this
					.safeCompare(year, ob.getANHO())) {
				String matricula = ob.getMATRICULA();
				if (matricula != null) {
					BeanPercentByPlate bean = mapRes.get(matricula);
					if (bean == null) {
						bean = new BeanPercentByPlate(matricula, ob.getDNI(), new BigDecimal(0, mc), new BigDecimal(0, mc), new BigDecimal(0, mc),
								new BigDecimal(0, mc));
						mapRes.put(matricula, bean);
					}
					BigDecimal horas = ob.getHORAS();
					BigDecimal kmRec = ob.getKM_REC();
					hrs += horas.doubleValue();
					kms += kmRec.doubleValue();
					bean.setS_HORAS(bean.getS_HORAS().add(horas, mc));
					bean.setS_REC(bean.getS_REC().add(kmRec, mc));
				}
			}
		}
		res.addAll(mapRes.values());
		for (BeanPercentByPlate bn : res) {
			if (hrs != 0) {
				bn.setPERC_HORAS(bn.getS_HORAS().divide(new BigDecimal(hrs, mc), mc));
			}
			if (kms != 0) {
				bn.setPERC_REC(bn.getS_REC().divide(new BigDecimal(kms, mc), mc));
			}
		}
		return res;
	}

	private List<BeanResumenDia> computeResumen(List<UsosVehiculoBean> beansList, String diaAnual, String week, String month, String year) {
		List<BeanResumenDia> res = new ArrayList<>();

		Map<String, BeanResumenDia> mapRes = new HashMap<>();

		for (UsosVehiculoBean ob : beansList) {
			if (this.safeCompare(diaAnual, ob.getDIA_ANUAL()) && this.safeCompare(week, ob.getSEMANA()) && this.safeCompare(month, ob.getMES()) && this
					.safeCompare(year, ob.getANHO())) {
				String matricula = ob.getMATRICULA();
				if (matricula != null) {
					BeanResumenDia beanResumenDia = mapRes.get(matricula);
					if (beanResumenDia == null) {
						beanResumenDia = new BeanResumenDia(matricula, new BigDecimal(0), new BigDecimal(0));
						mapRes.put(matricula, beanResumenDia);
					}
					BigDecimal minutos = ob.getMINUTOS();
					BigDecimal kmRec = ob.getKM_REC();
					beanResumenDia.setMINUTOS(beanResumenDia.getMINUTOS().add(minutos));
					beanResumenDia.setKM_REC(beanResumenDia.getKM_REC().add(kmRec));
				}
			}
		}
		res.addAll(mapRes.values());
		return res;
	}

	private List<UsosVehiculoBean> computeResumenNoGroup(List<UsosVehiculoBean> beansList, String diaAnual, String week, String month, String year) {
		List<UsosVehiculoBean> res = new ArrayList<>();

		for (UsosVehiculoBean ob : beansList) {
			if (this.safeCompare(diaAnual, ob.getDIA_ANUAL()) && this.safeCompare(week, ob.getSEMANA()) && this.safeCompare(month, ob.getMES()) && this
					.safeCompare(year, ob.getANHO())) {
				res.add(ob);
			}
		}
		return res;
	}

	private boolean safeCompare(String fixed, String bean) {
		if (fixed == null) {
			return true;
		}
		return fixed.equals(bean);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean cancelQuery() throws JRException {
		// TODO Auto-generated method stub
		return false;
	}

	private static class BeanResumenDia {
		private String		MATRICULA;
		private BigDecimal	MINUTOS;
		private BigDecimal	KM_REC;

		public BeanResumenDia(String mATRICULA, BigDecimal mINUTOS, BigDecimal kM_REC) {
			super();
			this.MATRICULA = mATRICULA;
			this.MINUTOS = mINUTOS;
			this.KM_REC = kM_REC;
		}

		public String getMATRICULA() {
			return this.MATRICULA;
		}

		public void setMATRICULA(String mATRICULA) {
			this.MATRICULA = mATRICULA;
		}

		public BigDecimal getMINUTOS() {
			return this.MINUTOS;
		}

		public void setMINUTOS(BigDecimal mINUTOS) {
			this.MINUTOS = mINUTOS;
		}

		public BigDecimal getKM_REC() {
			return this.KM_REC;
		}

		public void setKM_REC(BigDecimal kM_REC) {
			this.KM_REC = kM_REC;
		}
	}

	private static class BeanPercentByPlate {
		private String		MATRICULA;
		private String		DNI;
		private BigDecimal	S_HORAS;
		private BigDecimal	S_REC;
		private BigDecimal	PERC_HORAS;
		private BigDecimal	PERC_REC;

		public BeanPercentByPlate(String mATRICULA, String dNI, BigDecimal s_HORAS, BigDecimal s_REC, BigDecimal pERC_HORAS, BigDecimal pERC_REC) {
			super();
			this.MATRICULA = mATRICULA;
			this.DNI = dNI;
			this.S_HORAS = s_HORAS;
			this.S_REC = s_REC;
			this.PERC_HORAS = pERC_HORAS;
			this.PERC_REC = pERC_REC;
		}

		public String getMATRICULA() {
			return this.MATRICULA;
		}

		public void setMATRICULA(String mATRICULA) {
			this.MATRICULA = mATRICULA;
		}

		public String getDNI() {
			return this.DNI;
		}

		public void setDNI(String dNI) {
			this.DNI = dNI;
		}

		public BigDecimal getS_HORAS() {
			return this.S_HORAS;
		}

		public void setS_HORAS(BigDecimal s_HORAS) {
			this.S_HORAS = s_HORAS;
		}

		public BigDecimal getS_REC() {
			return this.S_REC;
		}

		public void setS_REC(BigDecimal s_REC) {
			this.S_REC = s_REC;
		}

		public BigDecimal getPERC_HORAS() {
			return this.PERC_HORAS;
		}

		public void setPERC_HORAS(BigDecimal pERC_HORAS) {
			this.PERC_HORAS = pERC_HORAS;
		}

		public BigDecimal getPERC_REC() {
			return this.PERC_REC;
		}

		public void setPERC_REC(BigDecimal pERC_REC) {
			this.PERC_REC = pERC_REC;
		}

	}

}
