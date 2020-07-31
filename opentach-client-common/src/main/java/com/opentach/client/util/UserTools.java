package com.opentach.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.common.user.IUserData;

public class UserTools {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(UserTools.class);

	private UserTools() {
		// Nothing
	}

	public static String getUserLevel() {
		try {
			AbstractOpentachClientLocator locator = (AbstractOpentachClientLocator) ApplicationManager.getApplication().getReferenceLocator();
			return locator.getUserData().getLevel();
		} catch (Exception err) {
			UserTools.logger.error("E_GETTING_USER_LEVEL", err);
			return null;
		}
	}

	public static boolean isSupervisor() {
		return IUserData.NIVEL_SUPERVISOR.equals(UserTools.getUserLevel());
	}

	public static boolean isOperador() {
		return IUserData.NIVEL_OPERADOR.equals(UserTools.getUserLevel());
	}

	public static boolean isOperadorAvanzado() {
		return IUserData.NIVEL_OPERADOR_AVANZADO.equals(UserTools.getUserLevel());
	}

	public static boolean isDistribuidor() {
		return IUserData.NIVEL_DISTRIBUIDOR.equals(UserTools.getUserLevel());
	}

	public static boolean isAgente() {
		return IUserData.NIVEL_AGENTE.equals(UserTools.getUserLevel());
	}

	public static boolean isEmpresa() {
		return IUserData.NIVEL_EMPRESA.equals(UserTools.getUserLevel());
	}

	public static boolean isEmpresaGF() {
		return IUserData.NIVEL_EMPRESAGF.equals(UserTools.getUserLevel());
	}

}
