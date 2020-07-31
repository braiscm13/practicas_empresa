package com.opentach.server.remotevehicle.provider.volvo.client.api;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.opentach.server.remotevehicle.provider.common.rest.invoker.ApiClient;
import com.opentach.server.remotevehicle.provider.common.rest.invoker.auth.HttpBasicAuth;
import com.opentach.server.remotevehicle.provider.volvo.client.model.TachoFileResponseObject;

public class VolvoApi {
	private final ApiClient apiClient;

	public VolvoApi(String volvoUri) {
		super();
		this.apiClient = new ApiClient().setBasePath(volvoUri);
	}


	/**
	 * Tachograph Files The tachograph files resource is used to retreive driver card files for one or several drivers and/or tachograph (data memory) files for one or several
	 * vehicles. The result for both driver card files and tachograph files are ordered by ReceivedDateTime, latest entry last. The max number of items returned in one call is OEM
	 * specific. <p><b>200</b> - successful operation <p><b>400</b> - The server cannot or will not process the request due to an apparent client error (e.g., malformed request
	 * syntax, invalid request message framing, or deceptive request routing) Possible reason mandatory field missing, e.g. Authentication Header empty or missing <p><b>401</b> -
	 * Similar to 403 Forbidden, but specifically for use when authentication is required and has failed or has not yet been provided. The response must include a WWW-Authenticate
	 * header field containing a challenge applicable to the requested resource. See Basic access authentication and Digest access authentication. Possible reason Wrong credentials
	 * and/or Login credentials expired <p><b>403</b> - The request was a valid request, but the server is refusing to respond to it. Unlike a 401 Unauthorized response,
	 * authenticating will make no difference. On servers where authentication is required, this commonly means that the provided credentials were successfully authenticated but
	 * that the credentials still do not grant the client permission to access the resource (e.g. a recognized user attempting to access restricted content) Possible reason
	 * Insufficient rights for the service, no rights on any service of this vehicle and/or Response is too large <p><b>404</b> - The requested resource could not be found but may
	 * be available again in the future. Subsequent requests by the client are permissible Possible reason vehicle unknown and/or rFMS-Version not supported <p><b>406</b> -
	 * Possible reason unsupported Accept parameter sent <p><b>429</b> - The user has sent too many requests in a given amount of time. Intended for use with rate limiting schemes
	 * Possible reason Request sent too often and/or Max concurrent calls
	 *
	 * @param starttime
	 *            Only the data received at or after this time should be returned. (i.e. &gt;&#x3D; starttime) (required)
	 * @param xCorrelationId
	 *            A client unique request id used for fault tracing at the API supplier. This shall be unique for each request if used. (optional)
	 * @param stoptime
	 *            Only the data received before this time should be returned. (i.e. &lt; stoptime) (optional)
	 * @param vin
	 *            Only the tachograph files downloaded from vehicles with these VINs should be returned. VIN only affects tachograph files, not driver card files (optional, default
	 *            to new ArrayList&lt;&gt;())
	 * @param driverId
	 *            Only the driver card files related to the drivers with these driverIds should be returned. DriverId only affects driver card files, not tachograph files. The
	 *            driverId shall be CardIssuingMemberState + DriverIdentification CardIssuingMemberState shall be the nation alpha code as specified by
	 *            https://dtc.jrc.ec.europa.eu/dtc_nation_codes.php, i.e. 1-3 characters DriverIdentification shall always be 14 characters Examples S12345678901234 (Card issued in
	 *            Sweden for driver with id 12345678901234) NLABCD1234567890 (Card issued in the Netherlands for driver with id ABCD1234567890) (optional, default to new
	 *            ArrayList&lt;&gt;())
	 * @param contentFilter
	 *            Will only return data defined by the filters. If this filter parameter isn&#39;t supplied the returned reports contain all available blocks. DRIVERCARDFILE -
	 *            Driver card files. TACHOFILE - Tachograph data memory files. (optional, default to new ArrayList&lt;&gt;())
	 * @param fileNameType
	 *            Describes how the filename shall be formatted. Example of values DEFAULT, FR, ES. DEFAULT - Filename set by tachograph manufacturer. FR - French naming format. ES
	 *            - Spanish naming format. If fileNameType isn&#39;t specified, DEFAULT is chosen. This parameter is optional and might not be supported by all OEMs (optional)
	 * @param includeFileContent
	 *            If this is true, the content of the files is delivered in the response (base64 encoded). If no value is provided, file content is delivered (optional)
	 * @return TachoFileResponseObject
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 */
	public TachoFileResponseObject tachofilesGet(String userName, String pass, Date starttime, String xCorrelationId, Date stoptime,
			List<String> vin,
			List<String> driverId,
			List<String> contentFilter, String fileNameType, Boolean includeFileContent) throws RestClientException {
		return this.tachofilesGetWithHttpInfo(userName, pass, starttime, xCorrelationId, stoptime, vin, driverId, contentFilter, fileNameType, includeFileContent).getBody();
	}

	/**
	 * Tachograph Files The tachograph files resource is used to retreive driver card files for one or several drivers and/or tachograph (data memory) files for one or several
	 * vehicles. The result for both driver card files and tachograph files are ordered by ReceivedDateTime, latest entry last. The max number of items returned in one call is OEM
	 * specific. <p><b>200</b> - successful operation <p><b>400</b> - The server cannot or will not process the request due to an apparent client error (e.g., malformed request
	 * syntax, invalid request message framing, or deceptive request routing) Possible reason mandatory field missing, e.g. Authentication Header empty or missing <p><b>401</b> -
	 * Similar to 403 Forbidden, but specifically for use when authentication is required and has failed or has not yet been provided. The response must include a WWW-Authenticate
	 * header field containing a challenge applicable to the requested resource. See Basic access authentication and Digest access authentication. Possible reason Wrong credentials
	 * and/or Login credentials expired <p><b>403</b> - The request was a valid request, but the server is refusing to respond to it. Unlike a 401 Unauthorized response,
	 * authenticating will make no difference. On servers where authentication is required, this commonly means that the provided credentials were successfully authenticated but
	 * that the credentials still do not grant the client permission to access the resource (e.g. a recognized user attempting to access restricted content) Possible reason
	 * Insufficient rights for the service, no rights on any service of this vehicle and/or Response is too large <p><b>404</b> - The requested resource could not be found but may
	 * be available again in the future. Subsequent requests by the client are permissible Possible reason vehicle unknown and/or rFMS-Version not supported <p><b>406</b> -
	 * Possible reason unsupported Accept parameter sent <p><b>429</b> - The user has sent too many requests in a given amount of time. Intended for use with rate limiting schemes
	 * Possible reason Request sent too often and/or Max concurrent calls
	 *
	 * @param starttime
	 *            Only the data received at or after this time should be returned. (i.e. &gt;&#x3D; starttime) (required)
	 * @param xCorrelationId
	 *            A client unique request id used for fault tracing at the API supplier. This shall be unique for each request if used. (optional)
	 * @param stoptime
	 *            Only the data received before this time should be returned. (i.e. &lt; stoptime) (optional)
	 * @param vin
	 *            Only the tachograph files downloaded from vehicles with these VINs should be returned. VIN only affects tachograph files, not driver card files (optional, default
	 *            to new ArrayList&lt;&gt;())
	 * @param driverId
	 *            Only the driver card files related to the drivers with these driverIds should be returned. DriverId only affects driver card files, not tachograph files. The
	 *            driverId shall be CardIssuingMemberState + DriverIdentification CardIssuingMemberState shall be the nation alpha code as specified by
	 *            https://dtc.jrc.ec.europa.eu/dtc_nation_codes.php, i.e. 1-3 characters DriverIdentification shall always be 14 characters Examples S12345678901234 (Card issued in
	 *            Sweden for driver with id 12345678901234) NLABCD1234567890 (Card issued in the Netherlands for driver with id ABCD1234567890) (optional, default to new
	 *            ArrayList&lt;&gt;())
	 * @param contentFilter
	 *            Will only return data defined by the filters. If this filter parameter isn&#39;t supplied the returned reports contain all available blocks. DRIVERCARDFILE -
	 *            Driver card files. TACHOFILE - Tachograph data memory files. (optional, default to new ArrayList&lt;&gt;())
	 * @param fileNameType
	 *            Describes how the filename shall be formatted. Example of values DEFAULT, FR, ES. DEFAULT - Filename set by tachograph manufacturer. FR - French naming format. ES
	 *            - Spanish naming format. If fileNameType isn&#39;t specified, DEFAULT is chosen. This parameter is optional and might not be supported by all OEMs (optional)
	 * @param includeFileContent
	 *            If this is true, the content of the files is delivered in the response (base64 encoded). If no value is provided, file content is delivered (optional)
	 * @return ResponseEntity&lt;TachoFileResponseObject&gt;
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 */
	public ResponseEntity<TachoFileResponseObject> tachofilesGetWithHttpInfo(String userName, String pass, Date starttime, String xCorrelationId,
			Date stoptime,
			List<String> vin,
			List<String> driverId, List<String> contentFilter, String fileNameType, Boolean includeFileContent) throws RestClientException {
		Object postBody = null;

		// verify the required parameter 'starttime' is set
		if (starttime == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'starttime' when calling tachofilesGet");
		}

		String path = this.apiClient.expandPath("/tachofiles", Collections.<String, Object> emptyMap());

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
		final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "starttime", starttime));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "stoptime", stoptime));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "vin", vin));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "driverId", driverId));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "contentFilter", contentFilter));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "fileNameType", fileNameType));
		queryParams.putAll(this.apiClient.parameterToMultiValueMap(null, "includeFileContent", includeFileContent));

		if (xCorrelationId != null) {
			headerParams.add("X-Correlation-Id", this.apiClient.parameterToString(xCorrelationId));
		}

		final String[] accepts = { "application/x.volvogroup.com.tachofiles.v1.0+json; UTF-8" };
		final List<MediaType> accept = this.apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = {};
		final MediaType contentType = this.apiClient.selectHeaderContentType(contentTypes);

		ParameterizedTypeReference<TachoFileResponseObject> returnType = new ParameterizedTypeReference<TachoFileResponseObject>() {};
		HttpBasicAuth httpBasicAuth = new HttpBasicAuth();
		httpBasicAuth.setUsername(userName);
		httpBasicAuth.setPassword(pass);
		return this.apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, cookieParams, formParams, accept, contentType, httpBasicAuth, returnType);
	}
}
