package com.opentach.server.util;

import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.services.user.UserInformation;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.security.ServerSecurityManager;
import com.opentach.common.exception.OpentachSecurityException;
import com.opentach.common.user.IUserData;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.companies.ContractService;

/**
 * The Class SecurityTools.
 */
public final class SecurityTools {
	public static final SimpleGrantedAuthority ADMIN_AUTHORITY = new SimpleGrantedAuthority(IUserData.NIVEL_SUPERVISOR);

	/**
	 * Instantiates a new security tools.
	 */
	private SecurityTools() {
		super();
	}

	/**
	 * Check permissions.
	 *
	 * @param cif
	 *            the cif
	 * @param sessionId
	 *            the session id
	 * @throws Exception
	 *             the exception
	 */
	public static void checkPermissions(IOpentachServerLocator locator, String cif, int sessionId) throws Exception {
		IUserData userData = locator.getUserData(sessionId);
		if (IUserData.NIVEL_SUPERVISOR.equals(userData.getLevel())) {
			return;
		}
		SecurityTools.checkCif(userData.getAllCompaniesList(), cif);
	}

	public static void checkPermissions(IOpentachServerLocator locator, Entity ent, String cif, int sessionId) throws Exception {
		if (sessionId == TableEntity.getEntityPrivilegedId(ent)) {
			return;
		}
		SecurityTools.checkPermissions(locator, cif, sessionId);
	}

	public static void checkPermissions(IOpentachServerLocator locator, List<String> cifs, int sessionId) throws Exception {
		IUserData userData = locator.getUserData(sessionId);
		if (IUserData.NIVEL_SUPERVISOR.equals(userData.getLevel())) {
			return;
		}
		List<String> allowedCifs = userData.getAllCompaniesList();
		for (String cif : cifs) {
			SecurityTools.checkCif(allowedCifs, cif);
		}
	}

	public static void checkPermissions(IOpentachServerLocator locator, Entity ent, List<String> cifs, int sessionId) throws Exception {
		if (sessionId == TableEntity.getEntityPrivilegedId(ent)) {
			return;
		}
		SecurityTools.checkPermissions(locator, cifs, sessionId);
	}

	private static void checkCif(List<String> allowedCifs, String cif) throws OpentachSecurityException {
		if ((cif == null) || !allowedCifs.contains(cif)) {
			throw new OpentachSecurityException(ServerSecurityManager.ACCESS_DENIED);
		}
	}

	public static boolean isAdmin(IOpentachServerLocator locator, int sessionId) throws Exception {
		return IUserData.NIVEL_SUPERVISOR.equals(locator.getUserData(sessionId).getLevel());
	}

	public static void checkVehiclePermissions(IOpentachServerLocator locator, String cif, String vehicle) throws Exception {
		String contractId = locator.getService(ContractService.class).getContratoVigente(cif, -1);
		Entity eVehicleCont = locator.getEntityReferenceFromServer("EVehiculoCont");
		EntityResult res = eVehicleCont.query(EntityResultTools.keysvalues("CG_CONTRATO", contractId, "MATRICULA", vehicle), EntityResultTools.attributes("MATRICULA"),
				TableEntity.getEntityPrivilegedId(eVehicleCont));
		if (res.calculateRecordNumber() <= 0) {
			throw new OpentachSecurityException(ServerSecurityManager.ACCESS_DENIED);
		}
	}

	public static void checkDriverPermissions(IOpentachServerLocator locator, String cif, String driver) throws Exception {
		String contractId = locator.getService(ContractService.class).getContratoVigente(cif, -1);
		Entity eDriverCont = locator.getEntityReferenceFromServer("EConductorCont");
		EntityResult res = eDriverCont.query(EntityResultTools.keysvalues("CG_CONTRATO", contractId, "IDCONDUCTOR", driver), EntityResultTools.attributes("IDCONDUCTOR"),
				TableEntity.getEntityPrivilegedId(eDriverCont));
		if (res.calculateRecordNumber() <= 0) {
			throw new OpentachSecurityException(ServerSecurityManager.ACCESS_DENIED);
		}
	}

	/**
	 * Establishes authentication credentials in this thread's security context.
	 *
	 * Used to access @Secured annotated methods from unauthenticated threads.
	 */
	public static void establishAuthentication() {
		Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(SecurityTools.ADMIN_AUTHORITY.getAuthority());
		UserInformation userInfo = new UserInformation("NonExistingUser", "NonExistingPassword", authorities, null);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userInfo, "NonExistingPassword", authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
