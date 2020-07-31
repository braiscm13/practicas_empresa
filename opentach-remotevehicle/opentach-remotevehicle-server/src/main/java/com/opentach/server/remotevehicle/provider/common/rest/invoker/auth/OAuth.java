package com.opentach.server.remotevehicle.provider.common.rest.invoker.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

public class OAuth implements Authentication {
    private String accessToken;

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void applyToParams(MultiValueMap<String, String> queryParams, HttpHeaders headerParams, MultiValueMap<String, String> cookieParams) {
        if (this.accessToken != null) {
            headerParams.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
        }
    }
}
