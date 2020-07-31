package com.opentach.server.remotevehicle.provider.common.rest.invoker.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

public class HttpBearerAuth implements Authentication {
	private final String	scheme;
	private String			bearerToken;

	public HttpBearerAuth(String scheme) {
		this.scheme = scheme;
	}

	public String getBearerToken() {
		return this.bearerToken;
	}

	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}

	@Override
	public void applyToParams(MultiValueMap<String, String> queryParams, HttpHeaders headerParams, MultiValueMap<String, String> cookieParams) {
		if (this.bearerToken == null) {
			return;
		}
		headerParams.add(HttpHeaders.AUTHORIZATION, (this.scheme != null ? HttpBearerAuth.upperCaseBearer(this.scheme) + " " : "") + this.bearerToken);
	}

	private static String upperCaseBearer(String scheme) {
		return ("bearer".equalsIgnoreCase(scheme)) ? "Bearer" : scheme;
	}
}
