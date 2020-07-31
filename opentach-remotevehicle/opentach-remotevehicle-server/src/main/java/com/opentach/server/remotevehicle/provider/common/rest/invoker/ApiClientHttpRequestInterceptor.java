package com.opentach.server.remotevehicle.provider.common.rest.invoker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class ApiClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger loggger = LoggerFactory.getLogger(ApiClientHttpRequestInterceptor.class);

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		this.logRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		this.logResponse(response);
		return response;
	}

	private void logRequest(HttpRequest request, byte[] body) {
		ApiClientHttpRequestInterceptor.loggger.info("URI: " + request.getURI());
		ApiClientHttpRequestInterceptor.loggger.info("HTTP Method: " + request.getMethod());
		ApiClientHttpRequestInterceptor.loggger.info("HTTP Headers: " + this.headersToString(request.getHeaders()));
		ApiClientHttpRequestInterceptor.loggger.info("Request Body: " + new String(body, StandardCharsets.UTF_8));
	}

	private void logResponse(ClientHttpResponse response) throws IOException {
		ApiClientHttpRequestInterceptor.loggger.info("HTTP Status Code: " + response.getRawStatusCode());
		ApiClientHttpRequestInterceptor.loggger.info("Status Text: " + response.getStatusText());
		ApiClientHttpRequestInterceptor.loggger.info("HTTP Headers: " + this.headersToString(response.getHeaders()));
		ApiClientHttpRequestInterceptor.loggger.info("Response Body: " + this.bodyToString(response.getBody()));
	}

	private String headersToString(HttpHeaders headers) {
		StringBuilder builder = new StringBuilder();
		for (Entry<String, List<String>> entry : headers.entrySet()) {
			builder.append(entry.getKey()).append("=[");
			for (String value : entry.getValue()) {
				builder.append(value).append(",");
			}
			builder.setLength(builder.length() - 1); // Get rid of trailing comma
			builder.append("],");
		}
		builder.setLength(builder.length() - 1); // Get rid of trailing comma
		return builder.toString();
	}

	private String bodyToString(InputStream body) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8));
		String line = bufferedReader.readLine();
		while (line != null) {
			builder.append(line).append(System.lineSeparator());
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
		return builder.toString();
	}
}