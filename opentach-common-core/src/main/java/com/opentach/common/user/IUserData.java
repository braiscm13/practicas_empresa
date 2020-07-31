package com.opentach.common.user;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface IUserData {

	static final Locale	SPANISHLOCALE			= new Locale("es", "ES");

	// profiles -----------------------------------
	// SUPER ADMIN: control total
	static final String	NIVEL_SUPERVISOR		= "0";						// Super admin
	// SAT - Servicio atención cliente
	static final String	NIVEL_OPERADOR			= "1";						// SAT - Operador basico
	static final String	NIVEL_OPERADOR_AVANZADO	= "79";						// SAT - Operador avanzado

	// Cliente: Empresa / Subempresa
	static final String	NIVEL_EMPRESA			= "2";
	// Distribuidores y sus agentes
	static final String	NIVEL_AGENTE			= "3";						// Agente ¿de un distribuidor?
	static final String	NIVEL_DISTRIBUIDOR		= "89";						// Distribuidor

	static final String	NIVEL_DOWNLOAD			= "4";
	static final String	NIVEL_TITULARCDO		= "5";
	static final String	NIVEL_EMPRESAGF			= "6";
	static final String	NIVEL_DOWNLOADADV		= "7";
	static final String	NIVEL_DOWNLOADURK		= "8";
	static final String	NIVEL_DOWNLOADTPD1		= "9";
	static final String	NIVEL_DOWNLOADTPD2		= "10";
	static final String	NIVEL_DOWNLOADTPD3		= "11";
	static final String	NIVEL_DOWNLOADTPD4		= "12";
	static final String	NIVEL_BASIC				= "15";
	static final String	NIVEL_DOWNLOADADVSMALL	= "16";
	static final String	NIVEL_TDI				= "17";
	static final String	NIVEL_EMPRESAGTARJETAS	= "18";

	// Staff legal - bufete de abodados
	static final String	STAFF_LEGAL_BASICO		= "98";						// Staff legal - basico
	static final String	STAFF_LEGAL_AVANZADO	= "99";						// Staff legal - avanzado

	String getNfirmante();

	String getAfirmante();

	String getCargo();

	String getCompleteName();

	String getLogin();

	Date getDLogin();

	String getActiveContract(String cif);

	String getLevel();

	void addActiveContract(String cif, String cgContratoActivo);

	List<String> getCompaniesList();

	List<String> getAllCompaniesList();

	Map<String, Number> getDelegList();

	String getCIF();

	List<Company> getCompanies();

	List<Company> getAllCompanies();

	Locale getLocale();

	void setLocale(Locale locale);

	String getLevelDscr();

	String getCompanyName();

	String getAllCompanyName();

	List<String> getCompanyNameList();

	List<String> getAllCompanyNameList();

	Date getDMaxLogin();

	Map<String, Number> getlGrupos();

	boolean isAnonymousUser();

	boolean sendMail2Company();

	String getContractCompany();

}