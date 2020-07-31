package com.opentach.server.company.service;

import java.util.Date;

import com.opentach.common.company.exception.LicenseException;

public interface ILicenseServiceServer {

	void endDemoInternalUpdate(String cif, String license, Date end) throws LicenseException;

	void endLicenseInternalUpdate(String cif, String license, Date end) throws LicenseException;
}
