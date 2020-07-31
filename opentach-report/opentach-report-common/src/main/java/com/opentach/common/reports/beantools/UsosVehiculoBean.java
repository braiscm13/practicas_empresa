package com.opentach.common.reports.beantools;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class UsosVehiculoBean {
	private String		CG_CONTRATO;
	private String		DNI;
	private String		NUMREQ;
	private String		IDCONDUCTOR;
	private String		APELLIDOS;
	private String		NOMBRE;
	private String		MATRICULA;
	private String		DSCR;
	private Timestamp	FECINI;
	private Timestamp	FECFIN;
	private BigDecimal	MINUTOS;
	private BigDecimal	KM_INI;
	private BigDecimal	KM_FIN;
	private BigDecimal	KM_REC;
	private String		PROCEDENCIA;
	private String		ORIGEN;
	private String		DESTINO;
	private String		DIA;
	private String		DIA_SEM;
	private String		SEMANA;
	private String		MES;
	private String		MES2;
	private String		HORA;
	private BigDecimal	VEL_MED;
	private String		ANHO;
	private String		NUM_TRJ_CONDU;
	private BigDecimal	HORAS;
	private Timestamp	INI_SEMANA;
	private Timestamp	FIN_SEMANA;
	private String		DIA_ANUAL;

	public UsosVehiculoBean() {
	}

	public UsosVehiculoBean(String cG_CONTRATO, String dNI, String nUMREQ, String iDCONDUCTOR, String aPELLIDOS, String nOMBRE, String mATRICULA,
			String dSCR, Timestamp fECINI, Timestamp fECFIN, BigDecimal mINUTOS, BigDecimal kM_INI, BigDecimal kM_FIN, BigDecimal kM_REC,
			String pROCEDENCIA, String oRIGEN, String dESTINO, String dIA, String dIA_SEM, String sEMANA, String mES, String mES2, String hORA,
			BigDecimal vEL_MED, String aNHO, String nUM_TRJ_CONDU, BigDecimal hORAS, Timestamp iNI_SEMANA, Timestamp fIN_SEMANA, String dIA_ANUAL) {
		super();
		this.CG_CONTRATO = cG_CONTRATO;
		this.DNI = dNI;
		this.NUMREQ = nUMREQ;
		this.IDCONDUCTOR = iDCONDUCTOR;
		this.APELLIDOS = aPELLIDOS;
		this.NOMBRE = nOMBRE;
		this.MATRICULA = mATRICULA;
		this.DSCR = dSCR;
		this.FECINI = fECINI;
		this.FECFIN = fECFIN;
		this.MINUTOS = mINUTOS;
		this.KM_INI = kM_INI;
		this.KM_FIN = kM_FIN;
		this.KM_REC = kM_REC;
		this.PROCEDENCIA = pROCEDENCIA;
		this.ORIGEN = oRIGEN;
		this.DESTINO = dESTINO;
		this.DIA = dIA;
		this.DIA_SEM = dIA_SEM;
		this.SEMANA = sEMANA;
		this.MES = mES;
		this.MES2 = mES2;
		this.HORA = hORA;
		this.VEL_MED = vEL_MED;
		this.ANHO = aNHO;
		this.NUM_TRJ_CONDU = nUM_TRJ_CONDU;
		this.HORAS = hORAS;
		this.INI_SEMANA = iNI_SEMANA;
		this.FIN_SEMANA = fIN_SEMANA;
		this.DIA_ANUAL = dIA_ANUAL;
	}

	public String getCG_CONTRATO() {
		return this.CG_CONTRATO;
	}

	public void setCG_CONTRATO(String cG_CONTRATO) {
		this.CG_CONTRATO = cG_CONTRATO;
	}

	public String getDNI() {
		return this.DNI;
	}

	public void setDNI(String dNI) {
		this.DNI = dNI;
	}

	public String getNUMREQ() {
		return this.NUMREQ;
	}

	public void setNUMREQ(String nUMREQ) {
		this.NUMREQ = nUMREQ;
	}

	public String getIDCONDUCTOR() {
		return this.IDCONDUCTOR;
	}

	public void setIDCONDUCTOR(String iDCONDUCTOR) {
		this.IDCONDUCTOR = iDCONDUCTOR;
	}

	public String getAPELLIDOS() {
		return this.APELLIDOS;
	}

	public void setAPELLIDOS(String aPELLIDOS) {
		this.APELLIDOS = aPELLIDOS;
	}

	public String getNOMBRE() {
		return this.NOMBRE;
	}

	public void setNOMBRE(String nOMBRE) {
		this.NOMBRE = nOMBRE;
	}

	public String getMATRICULA() {
		return this.MATRICULA;
	}

	public void setMATRICULA(String mATRICULA) {
		this.MATRICULA = mATRICULA;
	}

	public String getDSCR() {
		return this.DSCR;
	}

	public void setDSCR(String dSCR) {
		this.DSCR = dSCR;
	}

	public Timestamp getFECINI() {
		return this.FECINI;
	}

	public void setFECINI(Timestamp fECINI) {
		this.FECINI = fECINI;
	}

	public Timestamp getFECFIN() {
		return this.FECFIN;
	}

	public void setFECFIN(Timestamp fECFIN) {
		this.FECFIN = fECFIN;
	}

	public BigDecimal getMINUTOS() {
		return this.MINUTOS;
	}

	public void setMINUTOS(BigDecimal mINUTOS) {
		this.MINUTOS = mINUTOS;
	}

	public BigDecimal getKM_INI() {
		return this.KM_INI;
	}

	public void setKM_INI(BigDecimal kM_INI) {
		this.KM_INI = kM_INI;
	}

	public BigDecimal getKM_FIN() {
		return this.KM_FIN;
	}

	public void setKM_FIN(BigDecimal kM_FIN) {
		this.KM_FIN = kM_FIN;
	}

	public BigDecimal getKM_REC() {
		return this.KM_REC;
	}

	public void setKM_REC(BigDecimal kM_REC) {
		this.KM_REC = kM_REC;
	}

	public String getPROCEDENCIA() {
		return this.PROCEDENCIA;
	}

	public void setPROCEDENCIA(String pROCEDENCIA) {
		this.PROCEDENCIA = pROCEDENCIA;
	}

	public String getORIGEN() {
		return this.ORIGEN;
	}

	public void setORIGEN(String oRIGEN) {
		this.ORIGEN = oRIGEN;
	}

	public String getDESTINO() {
		return this.DESTINO;
	}

	public void setDESTINO(String dESTINO) {
		this.DESTINO = dESTINO;
	}

	public String getDIA() {
		return this.DIA;
	}

	public void setDIA(String dIA) {
		this.DIA = dIA;
	}

	public String getDIA_SEM() {
		return this.DIA_SEM;
	}

	public void setDIA_SEM(String dIA_SEM) {
		this.DIA_SEM = dIA_SEM;
	}

	public String getSEMANA() {
		return this.SEMANA;
	}

	public void setSEMANA(String sEMANA) {
		this.SEMANA = sEMANA;
	}

	public String getMES() {
		return this.MES;
	}

	public void setMES(String mES) {
		this.MES = mES;
	}

	public String getMES2() {
		return this.MES2;
	}

	public void setMES2(String mES2) {
		this.MES2 = mES2;
	}

	public String getHORA() {
		return this.HORA;
	}

	public void setHORA(String hORA) {
		this.HORA = hORA;
	}

	public BigDecimal getVEL_MED() {
		return this.VEL_MED;
	}

	public void setVEL_MED(BigDecimal vEL_MED) {
		this.VEL_MED = vEL_MED;
	}

	public String getANHO() {
		return this.ANHO;
	}

	public void setANHO(String aNHO) {
		this.ANHO = aNHO;
	}

	public String getNUM_TRJ_CONDU() {
		return this.NUM_TRJ_CONDU;
	}

	public void setNUM_TRJ_CONDU(String nUM_TRJ_CONDU) {
		this.NUM_TRJ_CONDU = nUM_TRJ_CONDU;
	}

	public BigDecimal getHORAS() {
		return this.HORAS;
	}

	public void setHORAS(BigDecimal hORAS) {
		this.HORAS = hORAS;
	}

	public Timestamp getINI_SEMANA() {
		return this.INI_SEMANA;
	}

	public void setINI_SEMANA(Timestamp iNI_SEMANA) {
		this.INI_SEMANA = iNI_SEMANA;
	}

	public Timestamp getFIN_SEMANA() {
		return this.FIN_SEMANA;
	}

	public void setFIN_SEMANA(Timestamp fIN_SEMANA) {
		this.FIN_SEMANA = fIN_SEMANA;
	}

	public String getDIA_ANUAL() {
		return this.DIA_ANUAL;
	}

	public void setDIA_ANUAL(String dIA_ANUAL) {
		this.DIA_ANUAL = dIA_ANUAL;
	}

}
