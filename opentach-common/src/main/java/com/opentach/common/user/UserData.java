package com.opentach.common.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

public class UserData implements ICDOUserData, Serializable {

	private static final long			serialVersionUID	= 4;

	// printconfig
	public static final String			SHOWPRINT			= "0";
	public static final String			ONETICKETAUTO		= "1";
	public static final String			TWOTICKETAUTO		= "2";

	private final String				login;
	private final String				completeName;
	private final String				sCompanyName;
	private final String				sAllCompanyName;
	private final List<String>			lCompanyName;
	private final List<String>			lAllCompanyName;
	private final String				nfirmante;
	private final String				afirmante;
	private final String				cargo;
	private final String				level;
	private final String				levelDscr;
	private final Date					dLogin;
	private final String				dscr;
	private final List<String>			lCifCompanies;
	private final List<String>			lCifAllCompanies;
	private final Map<String, Number>	lDeleg;
	private final Map<String, Number>	lGrupos;
	private final String				printConfig;
	private final boolean				sendMail2Company;
	private final String				expressReport;
	private final boolean				monitor;
	private final Date					dMaxLogin;
	private final boolean				usbLeft;
	private final Map<String, String>	activeContracts;
	private Locale						locale;
	private final String				contractCompany;

	public UserData(String login, String completeName, String nivel, String nivelDscr, List<String[]> lEmpresas, List<String[]> lEmpresasTodas, Map<String, Number> lDeleg,
			Map<String, Number> lGrupos, Map<String, String> contratosActivos, Date dLogin, String dscr, boolean sendMail2Company, String expressReport, String printConfig,
			boolean monitor, Date dMaxLogin, boolean usbLeft, String nfirmante, String afirmante, String cargo, String contractCompany) {
		this.login = login;
		this.completeName = completeName;
		this.level = nivel;
		this.levelDscr = nivelDscr;
		this.contractCompany = contractCompany;
		// System.out.println(contratosActivos);
		this.activeContracts = contratosActivos;
		// System.out.println(this.activeContracts);
		this.dLogin = dLogin;
		this.lDeleg = lDeleg;
		this.lGrupos = lGrupos;
		this.dscr = dscr;
		this.printConfig = printConfig;
		this.sendMail2Company = sendMail2Company;
		this.expressReport = expressReport;
		this.nfirmante = nfirmante;
		this.afirmante = afirmante;
		this.cargo = cargo;
		this.monitor = monitor;
		this.dMaxLogin = dMaxLogin;
		this.usbLeft = usbLeft;
		if ((lEmpresas != null) && (lEmpresas.size() > 0)) {
			this.lCifCompanies = new Vector<String>();
			this.lCompanyName = new Vector<String>();
			StringBuffer sb = new StringBuffer();
			String[] item = null;
			for (int i = 0; i < lEmpresas.size(); i++) {
				item = lEmpresas.get(i);
				this.lCifCompanies.add(item[0]);
				this.lCompanyName.add(item[1]);
				if (item[1] != null) {
					sb.append(item[1]);
				}
				if (i != (lEmpresas.size() - 1)) {
					sb.append(" / ");
				}
			}
			this.sCompanyName = sb.toString();
		} else {
			this.lCompanyName = null;
			this.lCifCompanies = null;
			this.sCompanyName = null;
		}

		if ((lEmpresasTodas != null) && (lEmpresasTodas.size() > 0)) {
			this.lCifAllCompanies = new Vector<String>();
			this.lAllCompanyName = new Vector<String>();
			StringBuffer sb = new StringBuffer();
			String[] item = null;
			for (int i = 0; i < lEmpresasTodas.size(); i++) {
				item = lEmpresasTodas.get(i);
				this.lCifAllCompanies.add(item[0]);
				this.lAllCompanyName.add(item[1]);
				if (item[1] != null) {
					sb.append(item[1]);
				}
				if (i != (lEmpresasTodas.size() - 1)) {
					sb.append(" / ");
				}
			}
			this.sAllCompanyName = sb.toString();
		} else {
			this.lAllCompanyName = null;
			this.lCifAllCompanies = null;
			this.sAllCompanyName = null;
		}
	}

	@Override
	public String getNfirmante() {
		return this.nfirmante;
	}

	@Override
	public String getAfirmante() {
		return this.afirmante;
	}

	@Override
	public String getCargo() {
		return this.cargo;
	}

	@Override
	public String toString() {
		return this.login;
	}

	@Override
	public String getCompleteName() {
		return this.completeName;
	}

	@Override
	public String getLogin() {
		return this.login;
	}

	@Override
	public Date getDLogin() {
		return this.dLogin;
	}

	@Override
	public String getActiveContract(String cif) {
		if (cif == null) {
			return null;
		}
		return this.activeContracts.get(cif);
	}

	@Override
	public String getLevel() {
		return this.level;
	}

	@Override
	public void addActiveContract(String cif, String cgContratoActivo) {
		if ((cif != null) && (cgContratoActivo != null)) {
			this.activeContracts.put(cif, cgContratoActivo);
		}
	}

	@Override
	public List<String> getCompaniesList() {
		return this.lCifCompanies;
	}

	@Override
	public List<String> getAllCompaniesList() {
		return this.lCifAllCompanies;
	}

	@Override
	public Map<String, Number> getDelegList() {
		return this.lDeleg;
	}

	@Override
	public String getCIF() {
		if ((this.lCifCompanies == null) || (this.lCifCompanies.size() != 1)) {
			return null;
		}
		return this.lCifCompanies.get(0);
	}

	@Override
	public List<Company> getCompanies() {
		List<Company> res = new ArrayList<Company>();
		List<String> companiesList = this.getCompaniesList();
		if (companiesList != null) {
			for (int i = 0; i < companiesList.size(); i++) {
				String cif = companiesList.get(i);
				res.add(new Company(this.getCompanyNameList().get(i), cif, this.getActiveContract(cif)));
			}
		}
		return res;
	}

	@Override
	public List<Company> getAllCompanies() {
		List<Company> res = new ArrayList<Company>();
		List<String> companiesCoopList = this.getAllCompaniesList();
		if (companiesCoopList != null) {
			for (int i = 0; i < companiesCoopList.size(); i++) {
				String cif = companiesCoopList.get(i);
				res.add(new Company(this.getAllCompanyNameList().get(i), cif, this.getActiveContract(cif)));
			}
		}
		return res;
	}

	@Override
	public Locale getLocale() {
		return this.locale;
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public String getLevelDscr() {
		return this.levelDscr;
	}

	@Override
	public String getCompanyName() {
		return this.sCompanyName;
	}

	@Override
	public String getAllCompanyName() {
		return this.sAllCompanyName;
	}

	@Override
	public List<String> getCompanyNameList() {
		return this.lCompanyName;
	}

	@Override
	public List<String> getAllCompanyNameList() {
		return this.lAllCompanyName;
	}

	@Override
	public String getDscr() {
		return this.dscr;
	}

	@Override
	public boolean sendMail2Company() {
		return this.sendMail2Company;
	}

	@Override
	public String getPrintConfig() {
		return this.printConfig;
	}

	public static boolean isAnonymousUser(String level) {
		return IUserData.NIVEL_DOWNLOAD.equals(level) || IUserData.NIVEL_DOWNLOADADV.equals(level) || IUserData.NIVEL_DOWNLOADURK.equals(level) || IUserData.NIVEL_DOWNLOADTPD1
				.equals(level) || IUserData.NIVEL_DOWNLOADTPD2.equals(
						level) || IUserData.NIVEL_DOWNLOADTPD3.equals(level) || IUserData.NIVEL_DOWNLOADTPD4.equals(level) || IUserData.NIVEL_DOWNLOADADVSMALL.equals(level);
	}

	public static boolean isBasicUser(String level) {
		return IUserData.NIVEL_BASIC.equals(level);
	}

	public boolean isBasicUser() {
		return UserData.isBasicUser(this.level);
	}

	@Override
	public boolean isAnonymousUser() {
		return UserData.isAnonymousUser(this.level);
	}

	@Override
	public boolean isUSBDownloadUser() {
		return IUserData.NIVEL_DOWNLOADADV.equals(this.level) || IUserData.NIVEL_DOWNLOADURK.equals(this.level) || IUserData.NIVEL_DOWNLOADTPD1
				.equals(this.level) || IUserData.NIVEL_DOWNLOADTPD2.equals(this.level) || IUserData.NIVEL_DOWNLOADTPD3
				.equals(this.level) || IUserData.NIVEL_DOWNLOADTPD4.equals(this.level) || IUserData.NIVEL_DOWNLOADADVSMALL.equals(this.level);
	}

	@Override
	public String getExpressReport() {
		return this.expressReport;
	}

	@Override
	public boolean isMonitor() {
		return this.monitor;
	}

	@Override
	public Date getDMaxLogin() {
		return this.dMaxLogin;
	}

	public boolean isUsbLeft() {
		return this.usbLeft;
	}

	@Override
	public Map<String, Number> getlGrupos() {
		return this.lGrupos;
	}

	@Override
	public String getContractCompany() {
		return this.contractCompany;
	}

}