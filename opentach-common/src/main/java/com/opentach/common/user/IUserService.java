package com.opentach.common.user;

import java.rmi.Remote;

public interface IUserService extends Remote {

	/** The Constant ID. */
	final static String ID = "UserService";

	void sendWelcomeMail(String login, int sesionId) throws Exception;

}
