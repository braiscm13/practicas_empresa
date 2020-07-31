package com.opentach.common.interfaces;

import java.io.File;
import java.rmi.Remote;
import java.util.Date;

public interface ICartasPorte extends Remote {

	void  saveCartasPorte(final File f, final String nombre, final String tipo, final Date dInforme,  final int sessionID) throws Exception;

}
