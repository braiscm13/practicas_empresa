package com.opentach.client.util;

import java.net.ConnectException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;

import com.ontimize.jee.common.exceptions.InvalidCredentialsException;
import com.ontimize.jee.common.security.ILoginProvider;
import com.ontimize.jee.common.services.user.IUserInformationService;
import com.ontimize.jee.common.services.user.UserInformation;
import com.ontimize.jee.desktopclient.locator.security.OntimizeLoginProvider;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.exception.OpentachException;
public class OntimizeJEESessionStarter {

	private static final Logger	logger	= LoggerFactory.getLogger(OntimizeJEESessionStarter.class);
	protected UserInformation	userInformation;

	public OntimizeJEESessionStarter() {
		super();
	}

	public void startSession(String user, String password) {
		ILoginProvider bean = null;
		try {
			try {
				bean = BeansFactory.getBean(ILoginProvider.class);
			} catch (NoSuchBeanDefinitionException ex) {
				OntimizeJEESessionStarter.logger.trace(null, ex);
				BeansFactory.registerBeanDefinition("loginProvider", new AnnotatedGenericBeanDefinition(OntimizeLoginProvider.class));
				// yes, duplicate line. seems spring bug... review when possible
				BeansFactory.registerBeanDefinition("loginProvider", new AnnotatedGenericBeanDefinition(OntimizeLoginProvider.class));
				bean = BeansFactory.getBean(ILoginProvider.class);
			}
			String url = System.getProperty("com.ontimize.services.baseUrl");
			URI uri = new URI(url);
			bean.doLogin(uri, user, password);
			IUserInformationService userService = BeansFactory.getBean(IUserInformationService.class);
			this.userInformation = userService.getUserInformation();
			if (this.userInformation == null) {
				throw new OpentachException(user);
			}
			// Save password in local
			this.userInformation.setPassword(password);
		} catch (InvalidCredentialsException ex) {
			throw new SecurityException("E_LOGIN__INVALID_CREDENTIALS", ex);
		} catch (ConnectException ex) {
			throw new SecurityException("E_CONNECT_SERVER", ex);
		} catch (Exception ex) {
			throw new SecurityException("E_LOGIN__ERROR", ex);
		}
	}

}
