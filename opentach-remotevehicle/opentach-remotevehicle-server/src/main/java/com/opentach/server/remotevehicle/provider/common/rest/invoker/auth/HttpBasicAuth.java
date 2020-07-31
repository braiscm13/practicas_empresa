package com.opentach.server.remotevehicle.provider.common.rest.invoker.auth;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.util.Base64Utils;
import org.springframework.util.MultiValueMap;

public class HttpBasicAuth implements Authentication {
	private String	username;
	private String	password;

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void applyToParams(MultiValueMap<String, String> queryParams, HttpHeaders headerParams, MultiValueMap<String, String> cookieParams) {
		if ((this.username == null) && (this.password == null)) {
			return;
		}
		String str = (this.username == null ? "" : this.username) + ":" + (this.password == null ? "" : this.password);
		headerParams.add(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString(str.getBytes(StandardCharsets.UTF_8)));
	}
}
