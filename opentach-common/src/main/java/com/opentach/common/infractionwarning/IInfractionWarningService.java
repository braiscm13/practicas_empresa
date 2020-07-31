package com.opentach.common.infractionwarning;

import java.rmi.Remote;
import java.util.Date;

import com.ontimize.util.remote.BytesBlock;

public interface IInfractionWarningService extends Remote {


	static final String ID = "InfractionWarningService";

	BytesBlock generateInfractionWarningReport(Object driverId, String cif, String cgContrato, Date from, Date to, int sessionId) throws Exception;
}
