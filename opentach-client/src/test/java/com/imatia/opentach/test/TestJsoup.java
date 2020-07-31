package com.imatia.opentach.test;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.ontimize.jee.common.tools.Pair;

public class TestJsoup {
	static String	HOST		= "www.fomento.gob.es";
	static String	URL			= "http://" + TestJsoup.HOST + "/crgt/servlet/ServletController";
	static String	QUERY_STR	= "?modulo=datosconsulta&accion=inicio&lang=es&estilo=default";

	public TestJsoup() {
		// TODO Auto-generated constructor stub
	}

	public Pair<Map<String, String>, Map<String, String>> downloadCaptcha() throws Exception {
		URI uri = URI.create(TestJsoup.URL);
		Response response = Jsoup.connect(TestJsoup.URL + TestJsoup.QUERY_STR).timeout(300000)
				.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0").method(Method.GET).execute();
		Map<String, String> cookies = response.cookies();

		// now we will load form's inputs
		Document doc = response.parse();
		HashMap<String, String> formFields = new HashMap<String, String>();
		for (Element field : doc.select("form[id=formularioBusqueda] input")) {
			formFields.put(field.attr("name"), field.attr("value"));
			System.out.println(field.attr("name"));
		}

		for (Element result : doc.select("form[name=captcha] img")) {
			System.out.println(result.attr("src"));
			URI imgUri = uri.resolve(result.attr("src"));

			Response resultImageResponse = Jsoup.connect(imgUri.toString()).cookies(cookies).ignoreContentType(true).method(Method.GET).timeout(30000).execute();

			// we will need these cookies also!
			cookies.putAll(resultImageResponse.cookies());

			JFrame frame = new JFrame(imgUri.toString());
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(500, 500);
			frame.setLocationRelativeTo(null);
			ImageIcon img = new ImageIcon(resultImageResponse.bodyAsBytes());
			JLabel label = new JLabel();
			label.setIcon(img);
			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add(label, BorderLayout.CENTER);
			frame.setVisible(true);
		}
		return new Pair<>(formFields, cookies);
	}

	public Map<String, String> getData(Map<String, String> formFields, Map<String, String> cookies) throws Exception {
		Connection conn = Jsoup.connect(TestJsoup.URL).//
				userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0")
				// not neccesary but these extra headers won't hurt
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")//
				.header("Accept-Encoding", "gzip, deflate")//
				.header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")//
				.header("Host", TestJsoup.HOST)//
				.header("Referer", TestJsoup.URL + TestJsoup.QUERY_STR)//
				.header("Content-Type", "application/x-www-form-urlencoded")//
				.header("Connection", "keep-alive")//
				.cookies(cookies)//
				.timeout(0)//
				.method(Connection.Method.POST);

		// we send the fields
		conn.data(formFields);
		Response response = conn.execute();
		Document doc = response.parse();
		return this.extractData(doc);
	}

	private Map<String, String> extractData(Document doc) {
		Map<String, String> res = new HashMap<>();
		int i = 0;
		String title = null;
		String value = null;
		for (Element field : doc.select("table.resultados td")) {
			if ((i % 2) == 0) {
				title = field.text();
			} else {
				value = field.text();
				res.put(title, value);
			}
			i++;
		}
		return res;
	}

	private void run() throws Exception, IOException {
		Pair<Map<String, String>, Map<String, String>> fieldsCookies = this.downloadCaptcha();
		Map<String, String> formFields = fieldsCookies.getFirst();
		Map<String, String> cookies = fieldsCookies.getSecond();
		formFields = new HashMap<>();
		formFields.put("modulo", "datosconsulta");
		formFields.put("tpsolic", "P");
		formFields.put("accion", "consultar_nif");
		formFields.put("consulta", "22965318W");
		formFields.put("btnOK", "Consultar");
		// <input id="N" name="accion" value="consultar_nif" type="radio">
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String captcha = br.readLine();
		formFields.put("captcha", captcha);
		this.getData(formFields, cookies);
	}

	public static void main(String[] args) throws Exception {
		TestJsoup main = new TestJsoup();
		main.run();
	}

}
