package com.opentach.server.remotevehicle.provider.opentach;

import java.util.Hashtable;

import org.apache.commons.codec.binary.Base64;

import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.user.Company;
import com.opentach.server.companies.ContractService;
import com.utilmize.server.services.UAbstractService;

public class OpentachRemoteDownloadService extends UAbstractService {

	private static final String	ERROR_SENDING_APDU_TO_SMARTCARD	= "ERROR_SENDING_APDU_TO_SMARTCARD";
	private static final String	ERROR_CHECKING_COMPANY_CARD		= "ERROR_CHECKING_COMPANY_CARD";
	private static final String	ERROR_NO_CONNECTED_COMPANY_CARD	= "NO_CONNECTED_COMPANY_CARD";
	private final CompanyCardRegister	register;

	public OpentachRemoteDownloadService(int port, EntityReferenceLocator locator, Hashtable params) throws Exception {
		super(port, locator, params);
		this.register = new CompanyCardRegister();
	}

	public CompanyCardRegister getRegister() {
		return this.register;
	}

	public String checkRemoteCardAvailable(String user, String password, String companyId) throws Exception {
		try {
			Company company = this.getService(ContractService.class).getUserCompany(user, password, companyId);

			RemoteDownloadEndPoint endPoint = this.getEndPoint(company.getCif());
			endPoint.doCheck();
			return this.toBase64(endPoint.getAtr());
		} catch (Exception ex) {
			throw new Exception(OpentachRemoteDownloadService.ERROR_CHECKING_COMPANY_CARD, ex);
		}
	}

	public String sendApdu(String user, String password, String companyId, String b64Apdu) throws Exception {
		try {
			Company company = this.getService(ContractService.class).getUserCompany(user, password, companyId);
			RemoteDownloadEndPoint endPoint = this.getEndPoint(company.getCif());
			byte[] apdu = this.fromBase64(b64Apdu);
			byte[] res = endPoint.doSendApdu(apdu);
			CheckingTools.failIfNull(res, "");
			return this.toBase64(res);
		} catch (Throwable ex) {
			throw new Exception(OpentachRemoteDownloadService.ERROR_SENDING_APDU_TO_SMARTCARD, ex);
		}
	}

	private byte[] fromBase64(String header) {
		return new Base64().decode(header.getBytes());
	}

	private String toBase64(byte[] atr) {
		return new String(new Base64().encode(atr));
	}

	private RemoteDownloadEndPoint getEndPoint(String cif) throws Exception {
		RemoteDownloadEndPoint endPoint = this.register.getEndPoint(cif);
		if (endPoint == null) {
			throw new Exception(OpentachRemoteDownloadService.ERROR_NO_CONNECTED_COMPANY_CARD);
		}
		return endPoint;
	}

}
