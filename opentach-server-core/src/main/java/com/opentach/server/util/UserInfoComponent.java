package com.opentach.server.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.jee.common.services.user.UserInformation;
import com.ontimize.jee.common.tools.BasicExpressionTools;
import com.ontimize.jee.common.tools.StringTools;
import com.ontimize.jee.server.spring.SpringTools;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.company.naming.LicenseNaming;
import com.opentach.common.company.service.ILicenseService;
import com.opentach.common.exception.OpentachRuntimeException;
import com.opentach.common.exception.OpentachSecurityException;
import com.opentach.common.report.util.ReportSessionUtils;
import com.opentach.common.user.IUserData;
import com.opentach.server.util.spring.OpentachLocatorReferencer;

/**
 * The Class UserInfoComponent.
 */
@Component
public class UserInfoComponent {

	/** The CONSTANT logger */
	private static final Logger			logger	= LoggerFactory.getLogger(UserInfoComponent.class);

	@Autowired
	private OpentachLocatorReferencer locator;

	@Autowired
	private ILicenseService				licenseService;

	/**
	 * Instantiates a new user info component.
	 */
	public UserInfoComponent() {
		super();
	}

	private Collection<GrantedAuthority> getAuthorities() {
		return this.getPrincipal().getAuthorities();
	}

	private UserInformation getPrincipal() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ((authentication == null) || (authentication.getPrincipal() == null)) {
			UserInfoComponent.logger.warn("Be careful: if this code is executed from server, consider to see com.opentach.server.util.SecurityTools.establishAuthentication()");
		}
		return (UserInformation) authentication.getPrincipal();
	}

	public void establishAuthentication(int sessionId) {
		if (sessionId < 0) {
			SecurityTools.establishAuthentication();
			return;
		}
		try {
			IUserData userData = this.locator.getLocator().getUserData(sessionId);
			Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userData.getLevel());
			UserInformation userInfo = new UserInformation(userData.getLogin(), "xxx", authorities, null);
			Authentication authentication = new UsernamePasswordAuthenticationToken(userInfo, "xxx", authorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception err) {
			throw new OpentachRuntimeException(err);
		}
	}

	private List<String> getProfiles() {
		return this.getAuthorities().stream().map(t -> t.getAuthority()).collect(Collectors.toList());
	}

	public String getUserLogin() {
		return this.getPrincipal().getLogin();
	}

	public Object getUserData(String key) {
		return this.getPrincipal().getOtherData().get(key);
	}

	public String getStringUserData(String key) {
		return (String) this.getUserData(key);
	}

	public boolean isAdmin() {
		return this.getProfiles().contains(IUserData.NIVEL_SUPERVISOR);
	}

	public boolean isOperator() {
		return this.getProfiles().contains(IUserData.NIVEL_OPERADOR);
	}

	public boolean isAdvancedOperator() {
		return this.getProfiles().contains(IUserData.NIVEL_OPERADOR_AVANZADO);
	}

	public boolean isDistributor() {
		return this.getProfiles().contains(IUserData.NIVEL_DISTRIBUIDOR);
	}

	public boolean isCompany() {
		return this.getProfiles().contains(IUserData.NIVEL_EMPRESA);
	}

	public String getProfile() {
		return this.getProfiles().iterator().next();
	}

	public int getSessionId() {
		return this.locator.getLocator().getSessionIdForLogin(this.getUserLogin());
	}

	public List<String> getCompaniesList() {
		try {
			// TODO review if this code must use "getAllCompaniesList()" or "getCompaniesList()"
			return this.locator.getLocator().getUserData(this.getUserLogin()).getAllCompaniesList();
		} catch (Exception ex) {
			throw new OpentachRuntimeException(ex);
		}
	}

	public void checkPermissions(String cif) {
		try {
			SecurityTools.checkPermissions(this.locator.getLocator(), cif, this.getSessionId());
		} catch (OpentachSecurityException err) {
			throw err;
		} catch (Exception err) {
			throw new OpentachSecurityException(err);
		}
	}

	public void checkVehiclePermissions(String cif, String srcId) {
		try {
			SecurityTools.checkPermissions(this.locator.getLocator(), cif, this.getSessionId());
			SecurityTools.checkVehiclePermissions(this.locator.getLocator(), cif, srcId);
		} catch (OpentachSecurityException err) {
			throw err;
		} catch (Exception err) {
			throw new OpentachSecurityException(err);
		}

	}

	public void checkDriverPermissions(String cif, String srcId) {
		try {
			SecurityTools.checkPermissions(this.locator.getLocator(), cif, this.getSessionId());
			SecurityTools.checkDriverPermissions(this.locator.getLocator(), cif, srcId);
		} catch (OpentachSecurityException err) {
			throw err;
		} catch (Exception err) {
			throw new OpentachSecurityException(err);
		}

	}

	/**
	 * This method ensures to complete "keysValues" (typically from query filters) with company filters according to user.
	 *  · SUPERVISOR / OPERADOR : see ALL, not changes
	 *  · DISTRIBUTOR : see ONLY his related companies (alive or "dead")
	 *  · COMPANY: see ONLY his related companies ALIVE (with license/demo enabled)
	 * @param keysValues
	 * @param cif
	 * @throws OpentachRuntimeException when some error checking user permissions
	 */
	public void checkCifFilter(Map<?, ?> keysValues, String cifColumn) {
		try {
			UserInfoComponent.logger.info("checkCifFilter:  USER={}", this.getUserLogin());
			if ((keysValues == null) || StringTools.isEmpty(cifColumn)) {
				UserInfoComponent.logger.warn("checkCifFilter: W_CANNOT_POSSIBLE_TO_SET_COMPANY_FILTER");
				return;
			}
			if (this.isAdmin() || this.isOperator() || this.isAdvancedOperator()) {
				UserInfoComponent.logger.info("checkCifFilter: User admin granted to see everything.");
				return;
			} else if (this.isDistributor()) {
				// All companies (alive or "dead") assigned to user
				UserInfoComponent.logger.info("checkCifFilter: Distributor can see all assigned companies.");
				this.checkCifFilter(keysValues, cifColumn, true);
			} else {
				// Limit to "alive" companies (that is has some license/demo) assigned to user
				UserInfoComponent.logger.info("checkCifFilter: This user can see ONLY alive assigned companies.");
				this.checkCifFilter(keysValues, cifColumn, false);
			}
		} catch (Exception err) {
			UserInfoComponent.logger.error("E_CHECKING_COMPANY_ACCESS", err);
			throw new OpentachRuntimeException("E_CHECKING_COMPANY_ACCESS", err);
		}
	}

	private void checkCifFilter(Map<?, ?> keysValues, String cifColumn, boolean includeDead) {
		// All visible companies
		List<String> companiesList = new ArrayList<String>(this.getCompaniesList());

		// Consider to discard dead
		if (!includeDead) {
			UserInfoComponent.logger.trace("Cleaning dead companies... USER={} INPUT_COMPANIES:{}", this.getUserLogin(), companiesList);
			List<String> discarded = this.getDeadCompaniesForUser(companiesList);
			companiesList.removeAll(discarded);
			UserInfoComponent.logger.trace("Cleaned dead companies...USER={} INVALID_COMPANIES:{}  RESULT_COMPANIES:{}", this.getUserLogin(), discarded, companiesList);
		}

		// Compose filter
		UserInfoComponent.logger.info("checkCifFilter:  USER={}  VALID_COMPANIES:{}", this.getUserLogin(), companiesList);
		if (companiesList.isEmpty()) {
			UserInfoComponent.logger.warn("Warning: no companies to filter -> No access!!");
			// Real (strange) case: distributor just created
			// Put ALWAYS INVALID FILTER to ensure NO RESULTS are valid
			((Map) keysValues).put(CompanyNaming.IS_COOPERATIVA, "W");
			return;
		}
		BasicExpression inputBe = (BasicExpression) keysValues.get(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY);
		BasicExpression be = new BasicExpression(new BasicField(cifColumn), BasicOperator.IN_OP, companiesList);
		((Map) keysValues).put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, BasicExpressionTools.combineExpression(inputBe, be));
	}

	public List<String> getDeadCompaniesForUser(List<String> companiesList) {
		if (ReportSessionUtils.isOpentach()) {
			// In Opentach app we must check Opentach criteria
			return companiesList.stream().filter((c) -> {
				try {
					return this.getLicenseService().hasOpentachAccess(c);
				} catch (Exception err) {
					UserInfoComponent.logger.error("E_CHECKING_ACCESS", err);
					return false;
				}
			}).collect(Collectors.toList());
		}else if (ReportSessionUtils.isTacholab()) {
			// In Tacholab app we must check Tacholab and TacholabPlus criteria
			return companiesList.stream().filter((c) -> {
				try {
					return !(this.getLicenseService().hasTacholabAccess(c) || (LicenseNaming.TACHOLABPLUS_ENABLED && this.getLicenseService().hasTacholabPlusAccess(c)));
				} catch (Exception err) {
					UserInfoComponent.logger.error("E_CHECKING_ACCESS", err);
					return false;
				}
			}).collect(Collectors.toList());
		}
		return new ArrayList<String>();
	}

	private ILicenseService getLicenseService() {
		return SpringTools.getTargetObject(this.licenseService, ILicenseService.class);
	}
}
