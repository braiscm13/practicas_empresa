package com.opentach.common.interfaces;

import java.rmi.Remote;
import java.util.Date;

import com.ontimize.db.EntityResult;

public interface IConsultarResumenActividades extends Remote {

	EntityResult consultarResumenUsosVehiculo(String cgContrato, Date fIni, Date fFin, int sessionID) throws Exception;

	EntityResult consultarResumenActividadesConductores(String cgContrato, Date fIni, Date fFin, int sessionID) throws Exception;

	EntityResult consultarResumenFicherosCond(String cgContrato, Date fIni, Date fFin, int sessionID) throws Exception;

	EntityResult consultarResumenFicherosVehi(String cgContrato, Date fIni, Date fFin, int sessionID) throws Exception;

	EntityResult consultarResumenIncidentes(String cgContrato, Date fIni, Date fFin, int sessionID) throws Exception;

	EntityResult consultarInformeLaboral(String cgContrato, Object idCond, Date fIni, Date fFin, int sessionID) throws Exception;

}
