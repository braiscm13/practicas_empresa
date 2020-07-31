
import java.util.Arrays;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.opentach.ws.common.rest.beans.RestLogginRequest;
import com.opentach.ws.common.rest.beans.RestLogginResponse;

/**
 * Ejemplos de invocacion a WS RESTFul de opentach
 */
public class TestRest {

	public static final String BASE_URL = "http://localhost:9080/opentach-war/services/";

	public static void main(String[] args) {
		try {
			TestRest.testOpentach();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	private static void testOpentach() throws Exception {
		System.out.println("################################ TESTING OPENTACH ################################");

		try {
			System.out.println(" --- TESTING QUERY --------------------------------");

			String url = TestRest.BASE_URL + "androidREST/loggin";

			RestLogginRequest filters = new RestLogginRequest();
			filters.setUsrLogin("a");
			filters.setUsrPsswd("a");

			RestTemplate rest = new RestTemplate();
			HttpMessageConverter<?> jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
			rest.setMessageConverters(Arrays.asList(new HttpMessageConverter<?>[] { jsonHttpMessageConverter }));
			RestLogginResponse response = rest.postForObject(url, filters, RestLogginResponse.class);

			response.getSessionId();

			System.out.println("Response. STATUS=" + response.getSessionId() + " Detail: " + response + "!!!!!!");
		} catch (Exception e) {
			throw new Exception("Exception in retrieving info : ", e);
		}
	}

}