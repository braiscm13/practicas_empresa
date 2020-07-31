package com.opentach.server.remotevehicle.provider.common.rest.invoker;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZonedDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.threetenbp.ThreeTenModule;
import com.opentach.server.remotevehicle.provider.common.rest.invoker.auth.Authentication;

public class ApiClient {
	public enum CollectionFormat {
		CSV(","), TSV("\t"), SSV(" "), PIPES("|"), MULTI(null);

		private final String separator;

		private CollectionFormat(String separator) {
			this.separator = separator;
		}

		private String collectionToString(Collection<?> collection) {
			return StringUtils.collectionToDelimitedString(collection, this.separator);
		}
	}

	private boolean								debugging		= false;

	private final HttpHeaders					defaultHeaders	= new HttpHeaders();
	private final MultiValueMap<String, String>	defaultCookies	= new LinkedMultiValueMap<String, String>();

	private String								basePath;

	private final RestTemplate					restTemplate;

	private DateFormat							dateFormat;

	public ApiClient() {
		this.restTemplate = this.buildRestTemplate();
		this.init();
	}

	protected void init() {
		// Use RFC3339 format for date and datetime.
		// See http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14
		this.dateFormat = new RFC3339DateFormat();

		// Use UTC as the default time zone.
		this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		// Set default User-Agent.
		this.setUserAgent("Java-SDK");
	}

	/**
	 * Get the current base path
	 *
	 * @return String the base path
	 */
	public String getBasePath() {
		return this.basePath;
	}

	/**
	 * Set the base path, which should include the host
	 *
	 * @param basePath
	 *            the base path
	 * @return ApiClient this client
	 */
	public ApiClient setBasePath(String basePath) {
		this.basePath = basePath;
		return this;
	}

	/**
	 * Set the User-Agent header's value (by adding to the default header map).
	 *
	 * @param userAgent
	 *            the user agent string
	 * @return ApiClient this client
	 */
	public ApiClient setUserAgent(String userAgent) {
		this.addDefaultHeader("User-Agent", userAgent);
		return this;
	}

	/**
	 * Add a default header.
	 *
	 * @param name
	 *            The header's name
	 * @param value
	 *            The header's value
	 * @return ApiClient this client
	 */
	public ApiClient addDefaultHeader(String name, String value) {
		if (this.defaultHeaders.containsKey(name)) {
			this.defaultHeaders.remove(name);
		}
		this.defaultHeaders.add(name, value);
		return this;
	}

	/**
	 * Add a default cookie.
	 *
	 * @param name
	 *            The cookie's name
	 * @param value
	 *            The cookie's value
	 * @return ApiClient this client
	 */
	public ApiClient addDefaultCookie(String name, String value) {
		if (this.defaultCookies.containsKey(name)) {
			this.defaultCookies.remove(name);
		}
		this.defaultCookies.add(name, value);
		return this;
	}

	public void setDebugging(boolean debugging) {
		List<ClientHttpRequestInterceptor> currentInterceptors = this.restTemplate.getInterceptors();
		if (debugging) {
			if (currentInterceptors == null) {
				currentInterceptors = new ArrayList<ClientHttpRequestInterceptor>();
			}
			ClientHttpRequestInterceptor interceptor = new ApiClientHttpRequestInterceptor();
			currentInterceptors.add(interceptor);
			this.restTemplate.setInterceptors(currentInterceptors);
		} else {
			if ((currentInterceptors != null) && !currentInterceptors.isEmpty()) {
				Iterator<ClientHttpRequestInterceptor> iter = currentInterceptors.iterator();
				while (iter.hasNext()) {
					ClientHttpRequestInterceptor interceptor = iter.next();
					if (interceptor instanceof ApiClientHttpRequestInterceptor) {
						iter.remove();
					}
				}
				this.restTemplate.setInterceptors(currentInterceptors);
			}
		}
		this.debugging = debugging;
	}

	/**
	 * Check that whether debugging is enabled for this API client.
	 *
	 * @return boolean true if this client is enabled for debugging, false otherwise
	 */
	public boolean isDebugging() {
		return this.debugging;
	}

	/**
	 * Get the date format used to parse/format date parameters.
	 *
	 * @return DateFormat format
	 */
	public DateFormat getDateFormat() {
		return this.dateFormat;
	}

	/**
	 * Set the date format used to parse/format date parameters.
	 *
	 * @param dateFormat
	 *            Date format
	 * @return API client
	 */
	public ApiClient setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
		for (HttpMessageConverter<?> converter : this.restTemplate.getMessageConverters()) {
			if (converter instanceof AbstractJackson2HttpMessageConverter) {
				ObjectMapper mapper = ((AbstractJackson2HttpMessageConverter) converter).getObjectMapper();
				mapper.setDateFormat(dateFormat);
			}
		}
		return this;
	}

	/**
	 * Parse the given string into Date object.
	 *
	 * @param str
	 *            the string to parse
	 * @return the Date parsed from the string
	 */
	public Date parseDate(String str) {
		try {
			return this.dateFormat.parse(str);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Format the given Date object into string.
	 *
	 * @param date
	 *            the date to format
	 * @return the formatted date as string
	 */
	public String formatDate(Date date) {
		return this.dateFormat.format(date);
	}

	/**
	 * Format the given parameter object into string.
	 *
	 * @param param
	 *            the object to convert
	 * @return String the parameter represented as a String
	 */
	public String parameterToString(Object param) {
		if (param == null) {
			return "";
		} else if (param instanceof Date) {
			return this.formatDate((Date) param);
		} else if (param instanceof Collection) {
			StringBuilder b = new StringBuilder();
			for (Object o : (Collection<?>) param) {
				if (b.length() > 0) {
					b.append(",");
				}
				b.append(String.valueOf(o));
			}
			return b.toString();
		} else {
			return String.valueOf(param);
		}
	}

	/**
	 * Formats the specified collection path parameter to a string value.
	 *
	 * @param collectionFormat
	 *            The collection format of the parameter.
	 * @param values
	 *            The values of the parameter.
	 * @return String representation of the parameter
	 */
	public String collectionPathParameterToString(CollectionFormat collectionFormat, Collection<?> values) {
		// create the value based on the collection format
		if (CollectionFormat.MULTI.equals(collectionFormat)) {
			// not valid for path params
			return this.parameterToString(values);
		}

		// collectionFormat is assumed to be "csv" by default
		if (collectionFormat == null) {
			collectionFormat = CollectionFormat.CSV;
		}

		return collectionFormat.collectionToString(values);
	}

	/**
	 * Converts a parameter to a {@link MultiValueMap} for use in REST requests
	 *
	 * @param collectionFormat
	 *            The format to convert to
	 * @param name
	 *            The name of the parameter
	 * @param value
	 *            The parameter's value
	 * @return a Map containing the String value(s) of the input parameter
	 */
	public MultiValueMap<String, String> parameterToMultiValueMap(CollectionFormat collectionFormat, String name, Object value) {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

		if ((name == null) || name.isEmpty() || (value == null)) {
			return params;
		}

		if (collectionFormat == null) {
			collectionFormat = CollectionFormat.CSV;
		}

		Collection<?> valueCollection = null;
		if (value instanceof Collection) {
			valueCollection = (Collection<?>) value;
		} else {
			params.add(name, this.parameterToString(value));
			return params;
		}

		if (valueCollection.isEmpty()) {
			return params;
		}

		if (collectionFormat.equals(CollectionFormat.MULTI)) {
			for (Object item : valueCollection) {
				params.add(name, this.parameterToString(item));
			}
			return params;
		}

		List<String> values = new ArrayList<String>();
		for (Object o : valueCollection) {
			values.add(this.parameterToString(o));
		}
		params.add(name, collectionFormat.collectionToString(values));

		return params;
	}

	/**
	 * Check if the given {@code String} is a JSON MIME.
	 *
	 * @param mediaType
	 *            the input MediaType
	 * @return boolean true if the MediaType represents JSON, false otherwise
	 */
	public boolean isJsonMime(String mediaType) {
		// "* / *" is default to JSON
		if ("*/*".equals(mediaType)) {
			return true;
		}

		try {
			return this.isJsonMime(MediaType.parseMediaType(mediaType));
		} catch (InvalidMediaTypeException e) {
		}
		return false;
	}

	/**
	 * Check if the given MIME is a JSON MIME. JSON MIME examples: application/json application/json; charset=UTF8 APPLICATION/JSON
	 *
	 * @param mediaType
	 *            the input MediaType
	 * @return boolean true if the MediaType represents JSON, false otherwise
	 */
	public boolean isJsonMime(MediaType mediaType) {
		return (mediaType != null) && (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType) || mediaType.getSubtype().matches("^.*\\+json[;]?\\s*$"));
	}

	/**
	 * Select the Accept header's value from the given accepts array: if JSON exists in the given array, use it; otherwise use all of them (joining into a string)
	 *
	 * @param accepts
	 *            The accepts array to select from
	 * @return List The list of MediaTypes to use for the Accept header
	 */
	public List<MediaType> selectHeaderAccept(String[] accepts) {
		if (accepts.length == 0) {
			return null;
		}
		for (String accept : accepts) {
			MediaType mediaType = MediaType.parseMediaType(accept);
			if (this.isJsonMime(mediaType)) {
				return Collections.singletonList(mediaType);
			}
		}
		return MediaType.parseMediaTypes(StringUtils.arrayToCommaDelimitedString(accepts));
	}

	/**
	 * Select the Content-Type header's value from the given array: if JSON exists in the given array, use it; otherwise use the first one of the array.
	 *
	 * @param contentTypes
	 *            The Content-Type array to select from
	 * @return MediaType The Content-Type header to use. If the given array is empty, JSON will be used.
	 */
	public MediaType selectHeaderContentType(String[] contentTypes) {
		if (contentTypes.length == 0) {
			return MediaType.APPLICATION_JSON;
		}
		for (String contentType : contentTypes) {
			MediaType mediaType = MediaType.parseMediaType(contentType);
			if (this.isJsonMime(mediaType)) {
				return mediaType;
			}
		}
		return MediaType.parseMediaType(contentTypes[0]);
	}

	/**
	 * Select the body to use for the request
	 *
	 * @param obj
	 *            the body object
	 * @param formParams
	 *            the form parameters
	 * @param contentType
	 *            the content type of the request
	 * @return Object the selected body
	 */
	protected Object selectBody(Object obj, MultiValueMap<String, Object> formParams, MediaType contentType) {
		boolean isForm = MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType) || MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType);
		return isForm ? formParams : obj;
	}

	/**
	 * Expand path template with variables
	 *
	 * @param pathTemplate
	 *            path template with placeholders
	 * @param variables
	 *            variables to replace
	 * @return path with placeholders replaced by variables
	 */
	public String expandPath(String pathTemplate, Map<String, Object> variables) {
		return this.restTemplate.getUriTemplateHandler().expand(pathTemplate, variables).toString();
	}

	/**
	 * Invoke API by sending HTTP request with the given options.
	 *
	 * @param <T>
	 *            the return type to use
	 * @param path
	 *            The sub-path of the HTTP URL
	 * @param method
	 *            The request method
	 * @param queryParams
	 *            The query parameters
	 * @param body
	 *            The request body object
	 * @param headerParams
	 *            The header parameters
	 * @param cookieParams
	 *            The cookie parameters
	 * @param formParams
	 *            The form parameters
	 * @param accept
	 *            The request's Accept header
	 * @param contentType
	 *            The request's Content-Type header
	 * @param authNames
	 *            The authentications to apply
	 * @param returnType
	 *            The return type into which to deserialize the response
	 * @return ResponseEntity&lt;T&gt; The response of the chosen type
	 */
	public <T> ResponseEntity<T> invokeAPI(String path, HttpMethod method, MultiValueMap<String, String> queryParams, Object body, HttpHeaders headerParams,
			MultiValueMap<String, String> cookieParams, MultiValueMap<String, Object> formParams, List<MediaType> accept, MediaType contentType,
			Authentication auth,
			ParameterizedTypeReference<T> returnType) throws RestClientException {
		this.updateParamsForAuth(auth, queryParams, headerParams, cookieParams);

		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.basePath).path(path);
		if (queryParams != null) {
			// encode the query parameters in case they contain unsafe characters
			for (List<String> values : queryParams.values()) {
				if (values != null) {
					for (int i = 0; i < values.size(); i++) {
						try {
							values.set(i, URLEncoder.encode(values.get(i), "utf8"));
						} catch (UnsupportedEncodingException e) {

						}
					}
				}
			}
			builder.queryParams(queryParams);
		}

		URI uri;
		try {
			uri = new URI(builder.build().toUriString());
		} catch (URISyntaxException ex) {
			throw new RestClientException("Could not build URL: " + builder.toUriString(), ex);
		}

		final BodyBuilder requestBuilder = RequestEntity.method(method, uri);
		if (accept != null) {
			requestBuilder.accept(accept.toArray(new MediaType[accept.size()]));
		}
		if (contentType != null) {
			requestBuilder.contentType(contentType);
		}

		this.addHeadersToRequest(headerParams, requestBuilder);
		this.addHeadersToRequest(this.defaultHeaders, requestBuilder);
		this.addCookiesToRequest(cookieParams, requestBuilder);
		this.addCookiesToRequest(this.defaultCookies, requestBuilder);

		RequestEntity<Object> requestEntity = requestBuilder.body(this.selectBody(body, formParams, contentType));

		ResponseEntity<T> responseEntity = this.restTemplate.exchange(requestEntity, returnType);

		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			return responseEntity;
		}
		// The error handler built into the RestTemplate should handle 400 and 500 series errors.
		throw new RestClientException("API returned " + responseEntity.getStatusCode() + " and it wasn't handled by the RestTemplate error handler");
	}

	/**
	 * Add headers to the request that is being built
	 *
	 * @param headers
	 *            The headers to add
	 * @param requestBuilder
	 *            The current request
	 */
	protected void addHeadersToRequest(HttpHeaders headers, BodyBuilder requestBuilder) {
		for (Entry<String, List<String>> entry : headers.entrySet()) {
			List<String> values = entry.getValue();
			for (String value : values) {
				if (value != null) {
					requestBuilder.header(entry.getKey(), value);
				}
			}
		}
	}

	/**
	 * Add cookies to the request that is being built
	 *
	 * @param cookies
	 *            The cookies to add
	 * @param requestBuilder
	 *            The current request
	 */
	protected void addCookiesToRequest(MultiValueMap<String, String> cookies, BodyBuilder requestBuilder) {
		if (!cookies.isEmpty()) {
			requestBuilder.header("Cookie", this.buildCookieHeader(cookies));
		}
	}

	/**
	 * Build cookie header. Keeps a single value per cookie (as per <a href="https://tools.ietf.org/html/rfc6265#section-5.3"> RFC6265 section 5.3</a>).
	 *
	 * @param cookies
	 *            map all cookies
	 * @return header string for cookies.
	 */
	private String buildCookieHeader(MultiValueMap<String, String> cookies) {
		final StringBuilder cookieValue = new StringBuilder();
		String delimiter = "";
		for (final Map.Entry<String, List<String>> entry : cookies.entrySet()) {
			final String value = entry.getValue().get(entry.getValue().size() - 1);
			cookieValue.append(String.format("%s%s=%s", delimiter, entry.getKey(), value));
			delimiter = "; ";
		}
		return cookieValue.toString();
	}

	/**
	 * Build the RestTemplate used to make HTTP requests.
	 *
	 * @return RestTemplate
	 */
	protected RestTemplate buildRestTemplate() {
		RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
		for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
			if (converter instanceof AbstractJackson2HttpMessageConverter) {
				ObjectMapper mapper = ((AbstractJackson2HttpMessageConverter) converter).getObjectMapper();
				ThreeTenModule module = new ThreeTenModule();
				module.addDeserializer(Instant.class, CustomInstantDeserializer.INSTANT);
				module.addDeserializer(OffsetDateTime.class, CustomInstantDeserializer.OFFSET_DATE_TIME);
				module.addDeserializer(ZonedDateTime.class, CustomInstantDeserializer.ZONED_DATE_TIME);
				mapper.registerModule(module);
				mapper.registerModule(new JsonNullableModule());
			}
		}
		// This allows us to read the response more than once - Necessary for debugging.
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
		return restTemplate;
	}

	/**
	 * Update query and header parameters based on authentication settings.
	 *
	 * @param authNames
	 *            The authentications to apply
	 * @param queryParams
	 *            The query parameters
	 * @param headerParams
	 *            The header parameters
	 */
	protected void updateParamsForAuth(Authentication auth, MultiValueMap<String, String> queryParams, HttpHeaders headerParams, MultiValueMap<String, String> cookieParams) {
		if (auth == null) {
			throw new RestClientException("Authentication undefined: " + auth);
		}
		auth.applyToParams(queryParams, headerParams, cookieParams);
	}

}
