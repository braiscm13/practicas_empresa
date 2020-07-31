/*
 * Tachograph File Archive
 * Access meta data about about stored tachograph files, download and upload tachograph files
 *
 * The version of the OpenAPI document: 1.1.5
 * Contact: team_tachograph_remote_download@rio.cloud
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package com.opentach.server.remotevehicle.provider.man.client.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.threeten.bp.OffsetDateTime;

import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;
import com.opentach.server.remotevehicle.provider.man.client.model.FileMetadataResponse;

/**
 * API tests for ManApi
 */
// @Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ManTestConfig.class })
public class ManApiTest {

	private static final Logger	logger	= LoggerFactory.getLogger(ManApiTest.class);

	@Autowired
	private ManApi				api;

	@Test
	public void customAuthToken() throws URISyntaxException {
		URI tokenUri = new URI("https://auth.iam.rio.cloud/oauth/token");
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("client_id", "5a6097a8-c25f-4842-b239-ffa8f9179a45");
		params.add("grant_type", "partner_integration");
		params.add("client_secret", "MjAwMDhjOWYtZjFmOC00NWM3LWI3MzItNDkxNTQ3MTgyNGI2");
		params.add("integration_id", "9aa6990a-33f6-47c1-897a-cdefce07defd");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		RequestEntity<MultiValueMap<String, String>> req = new RequestEntity<MultiValueMap<String, String>>(params, headers, HttpMethod.POST, tokenUri);
		ResponseEntity<Map> response = restTemplate.exchange(req, Map.class);
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			throw new RuntimeException("error");
		}
		Map<String, String> body = response.getBody();
		String accessToken = body.get("access_token");
		System.out.println(accessToken);
	}


	// @Test
	public void filesFileIdGetTest() throws RestClientException, RemoteVehicleException, URISyntaxException {
		Integer fileId = null;
		FileMetadataResponse response = this.api.filesFileIdGet(fileId);

		// TODO: test validations
	}

	/**
	 * Get (paged) file informations or zip of files based on content-header. Results are sorted by file-name ascending.
	 *
	 * When sending Accept-Header&#x3D;&#39;application/json&#39; API will respond with a description in JSON for the files matching query parameters. When sending
	 * Accept-Header&#x3D;&#39;application/zip&#39; API will respond with a compressed file in ZIP format containing all the tachograph files itself matching request paramters. #
	 * Examples: ## Get all files &#x60;GET /files&#x60; ## Get all files between two dates with paging information. &#x60;From&#x60; and &#x60;to&#x60; refering to
	 * FileMetadataModel::time_created. &#x60;GET /files?from&#x3D;2018-07-01T08%3A42%3A05.346Z&amp;to&#x3D;2018-05-28T08%3A42%3A05.346Z&amp;offset&#x3D;10&amp;limit&#x3D;10&#x60;
	 * ## Get Files from driver between two dates. &#x60;From&#x60; and &#x60;to&#x60; refering to FileMetadataModel::time_created. &#x60;GET
	 * /files?file_type&#x3D;driver&amp;from&#x3D;2018-07-01T08%3A42%3A05.346Z&amp;to&#x3D;2018-05-28T08%3A42%3A05.346Z&#x60; ## Get Files relating to specific driver with paging
	 * information &#x60;GET /files?offset&#x3D;0&amp;limit&#x3D;10&amp;driver_id&#x3D;7b290aff-6eab-47a3-9b61-e9f6c9dfc906&#x60;
	 *
	 * @throws URISyntaxException
	 * @throws RemoteVehicleException
	 * @throws RestClientException
	 *
	 * @throws ApiException
	 *             if the Api call fails
	 */
	@Test
	public void filesGetTest() throws RestClientException, RemoteVehicleException, URISyntaxException {
		String fleetId = null;
		String driverId = null;
		String equipmentId = null;
		OffsetDateTime from = null;
		OffsetDateTime to = null;
		Integer offset = null;
		Integer limit = null;
		String fileType = null;
		FileMetadataResponse response = this.api.filesGet(fleetId, driverId, equipmentId, from, to, offset, limit, fileType);
		System.out.println(response);
		// TODO: test validations
	}

	/**
	 * Upload a file
	 *
	 * # Example: ## Upload a file &#x60;POST /files?fleet_id&#x3D;d304220d-430a-42fc-939a-b01c50ceef04&amp;file_name&#x3D;upload.ddd&#x60; Body must contain file in binary format
	 *
	 * @throws URISyntaxException
	 * @throws RemoteVehicleException
	 * @throws RestClientException
	 *
	 * @throws ApiException
	 *             if the Api call fails
	 */
	// @Test
	public void filesPostTest() throws RestClientException, RemoteVehicleException, URISyntaxException {
		String fleetId = null;
		String fileName = null;
		byte[] fileContent = null;
		this.api.filesPost(fleetId, fileName, fileContent);

		// TODO: test validations
	}

}