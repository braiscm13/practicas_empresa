package com.opentach.common.interfaces;

import java.rmi.Remote;
import java.util.Locale;

import com.opentach.common.user.IUserData;

public interface IOpentachLocator extends Remote {

	public IUserData getUserData(int sesionId) throws Exception;


	public void setLocale(Locale locale, int sessionID) throws Exception;

}
