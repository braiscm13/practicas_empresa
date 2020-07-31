package com.opentach.server.remotevehicle.provider.man.client.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.threeten.bp.OffsetDateTime;

import com.ontimize.jee.common.settings.ISettingsHelper;
import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;
import com.opentach.server.remotevehicle.provider.common.rest.invoker.ApiClient;
import com.opentach.server.remotevehicle.provider.common.rest.invoker.auth.OAuth;
import com.opentach.server.remotevehicle.provider.man.client.model.FileMetadataResponse;

@Component("com.opentach.server.remotevehicle.provider.man.client.api.ManApi")
public class ManApi implements InitializingBean {
	private static final String	SETTING_REMOTE_VEHICLE_MAN_INTEGRATION_ID	= "remote_vehicle.man.integration_id";
	private static final String	SETTING_REMOTE_VEHICLE_MAN_CLIENT_SECRET	= "remote_vehicle.man.client_secret";
	private static final String	SETTING_REMOTE_VEHICLE_MAN_GRANT_TYPE		= "remote_vehicle.man.grant_type";
	private static final String	SETTING_REMOTE_VEHICLE_MAN_CLIENT_ID		= "remote_vehicle.man.client_id";
	private static final String	SETTING_REMOTE_VEHICLE_MAN_TOKEN_URI		= "remote_vehicle.man.token_uri";
	private static final String	SETTING_REMOTE_VEHICLE_MAN_API_URI			= "remote_vehicle.man.api_uri";
	private ApiClient			apiClient;
	@Autowired
	private ISettingsHelper		settings;

	public ManApi() {
		super();
	}

	@Override
	public void afterPropertiesSet() {
		this.apiClient = new ApiClient()
				.setBasePath(this.settings.getString(ManApi.SETTING_REMOTE_VEHICLE_MAN_API_URI, "https://api.iam.ccp-prod.net/api/tachograph-file-archive"));
	}

	/**
	 * Get a file itself or file metadata via file-id based on content-type header # Example: ## Get file via file ID 148 &#x60;GET /files/148&#x60; <p><b>200</b> - Success
	 *
	 * @param fileId
	 *            File identification (required)
	 * @return FileMetadataResponse
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 * @throws URISyntaxException
	 * @throws RemoteVehicleException
	 */
	public FileMetadataResponse filesFileIdGet(Integer fileId) throws RestClientException, RemoteVehicleException, URISyntaxException {
		return this.filesFileIdGetWithHttpInfo(fileId).getBody();
	}

	/**
	 * Get a file itself or file metadata via file-id based on content-type header # Example: ## Get file via file ID 148 &#x60;GET /files/148&#x60; <p><b>200</b> - Success
	 *
	 * @param fileId
	 *            File identification (required)
	 * @return ResponseEntity&lt;FileMetadataResponse&gt;
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 * @throws URISyntaxException
	 * @throws RemoteVehicleException
	 */
	public ResponseEntity<FileMetadataResponse> filesFileIdGetWithHttpInfo(Integer fileId) throws RestClientException, RemoteVehicleException, URISyntaxException {
		Object postBody = null;

		// verify the required parameter 'fileId' is set
		if (fileId == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'fileId' when calling filesFileIdGet");
		}

		// create path and map variables
		final Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("file-id", fileId);
		String path = this.apiClient.expandPath("/files/{file-id}", uriVariables);

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
		final MultiValueMap formParams = new LinkedMultiValueMap();

		final String[] accepts = { "application/json", "application/octet-stream" };
		final List<MediaType> accept = this.apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = {};
		final MediaType contentType = this.apiClient.selectHeaderContentType(contentTypes);

		ParameterizedTypeReference<FileMetadataResponse> returnType = new ParameterizedTypeReference<FileMetadataResponse>() {};
		OAuth auth = new OAuth();
		auth.setAccessToken(this.getAccessToken());
		return this.apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, cookieParams, formParams, accept, contentType, auth, returnType);
	}

	private String	accessToken;
	private long	requestTime;
	private long	durationMs;

	private String getAccessToken() throws URISyntaxException, RemoteVehicleException {
		if (this.tokenIsValid()) {
			return this.accessToken;
		}
		this.requestTime = System.currentTimeMillis();
		URI tokenUri = new URI(this.settings.getString(ManApi.SETTING_REMOTE_VEHICLE_MAN_TOKEN_URI, "https://auth.iam.rio.cloud/oauth/token"));
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("client_id", this.settings.getString(ManApi.SETTING_REMOTE_VEHICLE_MAN_CLIENT_ID, "5a6097a8-c25f-4842-b239-ffa8f9179a45"));
		params.add("grant_type", this.settings.getString(ManApi.SETTING_REMOTE_VEHICLE_MAN_GRANT_TYPE, "partner_integration"));
		params.add("client_secret", this.settings.getString(ManApi.SETTING_REMOTE_VEHICLE_MAN_CLIENT_SECRET, "MjAwMDhjOWYtZjFmOC00NWM3LWI3MzItNDkxNTQ3MTgyNGI2"));
		params.add("integration_id", this.settings.getString(ManApi.SETTING_REMOTE_VEHICLE_MAN_INTEGRATION_ID, "9aa6990a-33f6-47c1-897a-cdefce07defd"));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		RequestEntity<MultiValueMap<String, String>> req = new RequestEntity<MultiValueMap<String, String>>(params, headers, HttpMethod.POST, tokenUri);
		ResponseEntity<Map> response = restTemplate.exchange(req, Map.class);
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			throw new RemoteVehicleException("Error getting token from MAN");
		}
		Map<String, Object> body = response.getBody();
		this.accessToken = (String) body.get("access_token");
		this.durationMs = (((Number) body.get("expires_in")).longValue() - 10) * 1000l;
		return this.accessToken;
	}

	private boolean tokenIsValid() {
		return (this.accessToken != null) && ((System.currentTimeMillis() - this.requestTime) < this.durationMs);
	}

	/**
	 * Get (paged) file informations or zip of files based on content-header. Results are sorted by file-name ascending. When sending Accept-Header&#x3D;&#39;application/json&#39;
	 * API will respond with a description in JSON for the files matching query parameters. When sending Accept-Header&#x3D;&#39;application/zip&#39; API will respond with a
	 * compressed file in ZIP format containing all the tachograph files itself matching request paramters. # Examples: ## Get all files &#x60;GET /files&#x60; ## Get all files
	 * between two dates with paging information. &#x60;From&#x60; and &#x60;to&#x60; refering to FileMetadataModel::time_created. &#x60;GET
	 * /files?from&#x3D;2018-07-01T08%3A42%3A05.346Z&amp;to&#x3D;2018-05-28T08%3A42%3A05.346Z&amp;offset&#x3D;10&amp;limit&#x3D;10&#x60; ## Get Files from driver between two dates.
	 * &#x60;From&#x60; and &#x60;to&#x60; refering to FileMetadataModel::time_created. &#x60;GET
	 * /files?file_type&#x3D;driver&amp;from&#x3D;2018-07-01T08%3A42%3A05.346Z&amp;to&#x3D;2018-05-28T08%3A42%3A05.346Z&#x60; ## Get Files relating to specific driver with paging
	 * information &#x60;GET /files?offset&#x3D;0&amp;limit&#x3D;10&amp;driver_id&#x3D;7b290aff-6eab-47a3-9b61-e9f6c9dfc906&#x60; <p><b>200</b> - Success
	 *
	 * @param fleetId
	 *            Fleet identification (optional)
	 * @param driverId
	 *            Driver identification (optional)
	 * @param equipmentId
	 *            Equipment identification (optional)
	 * @param from
	 *            UTC value formatted as date-time (see: RFC 3339, section 5.6). Refering to FileMetadataModel::time_created. (optional)
	 * @param to
	 *            UTC value formatted as date-time (see: RFC 3339, section 5.6). Refering to FileMetadataModel::time_created. (optional)
	 * @param offset
	 *            Pagination: number of elements to skip (optional, default to 0)
	 * @param limit
	 *            Pagination: number of elements on the page (optional, default to 10)
	 * @param fileType
	 *            defines the type of file (optional)
	 * @return FileMetadataResponse
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 * @throws URISyntaxException
	 * @throws RemoteVehicleException
	 */
	public FileMetadataResponse filesGet(String fleetId, String driverId, String equipmentId, OffsetDateTime from, OffsetDateTime to, Integer offset, Integer limit,
			String fileType) throws RestClientException, RemoteVehicleException, URISyntaxException {
		return this.filesGetWithHttpInfo(fleetId, driverId, equipmentId, from, to, offset, limit, fileType).getBody();
	}

	/**
	 * Get (paged) file informations or zip of files based on content-header. Results are sorted by file-name ascending. When sending Accept-Header&#x3D;&#39;application/json&#39;
	 * API will respond with a description in JSON for the files matching query parameters. When sending Accept-Header&#x3D;&#39;application/zip&#39; API will respond with a
	 * compressed file in ZIP format containing all the tachograph files itself matching request paramters. # Examples: ## Get all files &#x60;GET /files&#x60; ## Get all files
	 * between two dates with paging information. &#x60;From&#x60; and &#x60;to&#x60; refering to FileMetadataModel::time_created. &#x60;GET
	 * /files?from&#x3D;2018-07-01T08%3A42%3A05.346Z&amp;to&#x3D;2018-05-28T08%3A42%3A05.346Z&amp;offset&#x3D;10&amp;limit&#x3D;10&#x60; ## Get Files from driver between two dates.
	 * &#x60;From&#x60; and &#x60;to&#x60; refering to FileMetadataModel::time_created. &#x60;GET
	 * /files?file_type&#x3D;driver&amp;from&#x3D;2018-07-01T08%3A42%3A05.346Z&amp;to&#x3D;2018-05-28T08%3A42%3A05.346Z&#x60; ## Get Files relating to specific driver with paging
	 * information &#x60;GET /files?offset&#x3D;0&amp;limit&#x3D;10&amp;driver_id&#x3D;7b290aff-6eab-47a3-9b61-e9f6c9dfc906&#x60; <p><b>200</b> - Success
	 *
	 * @param fleetId
	 *            Fleet identification (optional)
	 * @param driverId
	 *            Driver identification (optional)
	 * @param equipmentId
	 *            Equipment identification (optional)
	 * @param from
	 *            UTC value formatted as date-time (see: RFC 3339, section 5.6). Refering to FileMetadataModel::time_created. (optional)
	 * @param to
	 *            UTC value formatted as date-time (see: RFC 3339, section 5.6). Refering to FileMetadataModel::time_created. (optional)
	 * @param offset
	 *            Pagination: number of elements to skip (optional, default to 0)
	 * @param limit
	 *            Pagination: number of elements on the page (optional, default to 10)
	 * @param fileType
	 *            defines the type of file (optional)
	 * @return ResponseEntity&lt;FileMetadataResponse&gt;
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 * @throws URISyntaxException
	 * @throws RemoteVehicleException
	 */
	public ResponseEntity<FileMetadataResponse> filesGetWithHttpInfo(String fleetId, String driverId, String equipmentId, OffsetDateTime from, OffsetDateTime to, Integer offset,
			Integer limit, String fileType) throws RestClientException, RemoteVehicleException, URISyntaxException {
		Object postBody = null;

		String path = this.apiClient.expandPath("/files", Collections.<String, Object> emptyMap());

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
		final MultiValueMap formParams = new LinkedMultiValueMap();

		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "fleet_id", fleetId));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "driver_id", driverId));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "equipment_id", equipmentId));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "from", from));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "to", to));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "offset", offset));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "limit", limit));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "file_type", fileType));

		final String[] accepts = { "application/json", "application/zip" };
		final List<MediaType> accept = this.apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = {};
		final MediaType contentType = this.apiClient.selectHeaderContentType(contentTypes);

		ParameterizedTypeReference<FileMetadataResponse> returnType = new ParameterizedTypeReference<FileMetadataResponse>() {};
		OAuth auth = new OAuth();
		auth.setAccessToken(this.getAccessToken());
		return this.apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, cookieParams, formParams, accept, contentType, auth, returnType);
	}

	/**
	 * Upload a file # Example: ## Upload a file &#x60;POST /files?fleet_id&#x3D;d304220d-430a-42fc-939a-b01c50ceef04&amp;file_name&#x3D;upload.ddd&#x60; Body must contain file in
	 * binary format <p><b>201</b> - Success
	 *
	 * @param fleetId
	 *            Fleet identification (required)
	 * @param fileName
	 *            Name of the file (required)
	 * @param fileContent
	 *            The raw file (required)
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 * @throws URISyntaxException
	 * @throws RemoteVehicleException
	 */
	public void filesPost(String fleetId, String fileName, byte[] fileContent) throws RestClientException, RemoteVehicleException, URISyntaxException {
		this.filesPostWithHttpInfo(fleetId, fileName, fileContent);
	}

	/**
	 * Upload a file # Example: ## Upload a file &#x60;POST /files?fleet_id&#x3D;d304220d-430a-42fc-939a-b01c50ceef04&amp;file_name&#x3D;upload.ddd&#x60; Body must contain file in
	 * binary format <p><b>201</b> - Success
	 *
	 * @param fleetId
	 *            Fleet identification (required)
	 * @param fileName
	 *            Name of the file (required)
	 * @param fileContent
	 *            The raw file (required)
	 * @return ResponseEntity&lt;Void&gt;
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 * @throws URISyntaxException
	 * @throws RemoteVehicleException
	 */
	public ResponseEntity<Void> filesPostWithHttpInfo(String fleetId, String fileName, byte[] fileContent) throws RestClientException, RemoteVehicleException, URISyntaxException {
		Object postBody = fileContent;

		// verify the required parameter 'fleetId' is set
		if (fleetId == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'fleetId' when calling filesPost");
		}

		// verify the required parameter 'fileName' is set
		if (fileName == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'fileName' when calling filesPost");
		}

		// verify the required parameter 'fileContent' is set
		if (fileContent == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'fileContent' when calling filesPost");
		}

		String path = this.apiClient.expandPath("/files", Collections.<String, Object> emptyMap());

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
		final MultiValueMap formParams = new LinkedMultiValueMap();

		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "fleet_id", fleetId));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "file_name", fileName));

		final String[] accepts = {};
		final List<MediaType> accept = this.apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = { "application/octet-stream" };
		final MediaType contentType = this.apiClient.selectHeaderContentType(contentTypes);

		ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {};
		OAuth auth = new OAuth();
		auth.setAccessToken(this.getAccessToken());
		return this.apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, cookieParams, formParams, accept, contentType, auth, returnType);
	}
}
