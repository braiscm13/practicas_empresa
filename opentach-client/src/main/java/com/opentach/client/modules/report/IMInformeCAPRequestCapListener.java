package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.common.OpentachFieldNames;
import com.sun.javafx.webkit.WebConsoleListener;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.table.UTable;
import com.utilmize.client.gui.field.table.editor.UXMLButtonCellEditor;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.client.tools.FxUtils;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * The listener interface for receiving IMInformeCAPRequestCap events. The class
 * that is interested in processing a IMInformeCAPRequestCap event implements
 * this interface, and the object created with that class is registered with a
 * component using the component's <code>addIMInformeCAPRequestCapListener<code>
 * method. When the IMInformeCAPRequestCap event occurs, that object's
 * appropriate method is invoked.
 *
 * @author joaquin.romero
 */
public class IMInformeCAPRequestCapListener extends AbstractActionListenerButton implements CellEditorListener {

	/** The Constant logger. */
	public static final Logger logger = LoggerFactory.getLogger(IMInformeCAPRequestCapListener.class);

	private static final String T_INPUT_IMAGE_TEXT = "T_INPUT_IMAGE_TEXT";

	private static final String E_INVALID_FORMAT = "E_INVALID_FORMAT";
	private static final String E_DNI_ERROR = "E_DNI_ERROR";

	private static final String CAP_MODE_MERCANCIAS = "CAP MERCANCIAS";

	private static final String CAP_MODE_VIAJEROS = "CAP VIAJEROS";

	/** The Constant USER_AGENT. */
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0";

	/** The Constant HOST. */
	private static final String HOST = "apps.fomento.gob.es";

	/** The Constant URL. */
	private static final String URL = "https://" + IMInformeCAPRequestCapListener.HOST
			+ "/crgt/servlet/ServletController";

	/** The Constant QUERY_STR. */
	private static final String QUERY_STR = "?modulo=datosconsulta&accion=inicio&lang=es&estilo=default";

	private WebView				webView;
	private Stage				stage;

	/**
	 * Instantiates a new IM informe CAP request cap listener.
	 *
	 * @param editor the editor
	 * @param params the params
	 * @throws Exception the exception
	 */
	public IMInformeCAPRequestCapListener(UXMLButtonCellEditor editor, Hashtable params) throws Exception {
		super(editor, editor, params);
	}

	/**
	 * Instantiates a new IM informe CAP report listener.
	 *
	 * @throws Exception the exception
	 */
	public IMInformeCAPRequestCapListener() throws Exception {
		super();
	}

	/**
	 * Instantiates a new IM informe CAP report listener.
	 *
	 * @param params the params
	 * @throws Exception the exception
	 */
	public IMInformeCAPRequestCapListener(Hashtable params) throws Exception {
		super(params);
	}

	/**
	 * Instantiates a new IM informe CAP report listener.
	 *
	 * @param button        the button
	 * @param formComponent the form component
	 * @param params        the params
	 * @throws Exception the exception
	 */
	public IMInformeCAPRequestCapListener(AbstractButton button, IUFormComponent formComponent, Hashtable params)
			throws Exception {
		super(button, formComponent, params);
	}

	/**
	 * Instantiates a new IM informe CAP report listener.
	 *
	 * @param button the button
	 * @param params the params
	 * @throws Exception the exception
	 */
	public IMInformeCAPRequestCapListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.event.CellEditorListener#editingStopped(javax.swing.event.
	 * ChangeEvent)
	 */
	@Override
	public void editingStopped(final ChangeEvent event) {
		// Se llama desde el boton de la tabla
		final UTable table = ((UXMLButtonCellEditor) event.getSource()).getTable();
		final int row = ((UXMLButtonCellEditor) event.getSource()).getCellEditorRow();
		Object dni = table.getJTable().getValueAt(row, table.getColumnIndex(OpentachFieldNames.DNI_FIELD));
		Object idConductor = table.getJTable().getValueAt(row,
				table.getColumnIndex(OpentachFieldNames.IDCONDUCTOR_FIELD));
		Object cif = table.getJTable().getValueAt(row, table.getColumnIndex(OpentachFieldNames.CIF_FIELD));
		CheckingTools.failIf(dni == null, IMInformeCAPRequestCapListener.E_DNI_ERROR);
		IMInformeCAPRequestCapListener.this.run((String) dni,idConductor,cif,table,row);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.utilmize.client.gui.buttons.AbstractActionListenerButton#actionPerformed(
	 * java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		final UTable table = (UTable) this.getForm().getElementReference("EInformeCAP");
		// se llama desde el boton del formulario
		new USwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				EntityResult er = new EntityResult((Hashtable) table.getValue());
				int nregs = er.calculateRecordNumber();
				for (int i = 0; i < nregs; i++) {
					if (this.isCancelled()) {
						return null;
					}
					Hashtable recordValues = er.getRecordValues(i);
					Date capViajeros = (Date) recordValues.get("CAP_VIAJEROS");
					Date capMercancias = (Date) recordValues.get("CAP_MERCANCIAS");
					try {
						if (//
								((capViajeros == null) && (capMercancias == null)) //
								|| //
								((capViajeros != null) && capViajeros.before(new Date())) //
								|| //
								((capMercancias != null) && capMercancias.before(new Date())) //
								) {
							Object dni = recordValues.get(OpentachFieldNames.DNI_FIELD);
							Object idConductor = recordValues.get(OpentachFieldNames.IDCONDUCTOR_FIELD);
							Object cif = recordValues.get(OpentachFieldNames.CIF_FIELD);

							//							Map<String, String> res = IMInformeCAPRequestCapListener.this.run((String) dni);
							//							IMInformeCAPRequestCapListener.this.updateDb(res, idConductor, cif);
						}
					} catch (Exception error) {
						IMInformeCAPRequestCapListener.logger.error(null, error);
					}
				}
				return null;
			}

			@Override
			protected void done() {
				try {
					this.uget();
					table.refreshInThread(0);
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error,
							IMInformeCAPRequestCapListener.logger);
				}
			}
		}.executeOperation(this.getForm());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.event.CellEditorListener#editingCanceled(javax.swing.event.
	 * ChangeEvent)
	 */
	@Override
	public void editingCanceled(ChangeEvent e) {

	}

	/**
	 * Update db.
	 *
	 * @param res   the res
	 * @param table the table
	 * @param row   the row
	 * @throws Exception the exception
	 */
	protected void updateDb(Map<String, String> res, Object idConductor, Object cif) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		for (String key : new String[] { IMInformeCAPRequestCapListener.CAP_MODE_VIAJEROS,
				IMInformeCAPRequestCapListener.CAP_MODE_MERCANCIAS }) {
			String dateString = res.get(key);
			if (dateString != null) {
				Date date = sdf.parse(dateString);
				this.getEntity("EConductoresCAPMinisterio").insert(EntityResultTools.keysvalues(//
						OpentachFieldNames.IDCONDUCTOR_FIELD, idConductor, //
						OpentachFieldNames.CIF_FIELD, cif, //
						"F_HASTA", date, //
						"SECCION", IMInformeCAPRequestCapListener.CAP_MODE_VIAJEROS.equals(key) ? "VJ" : "ME"),
						this.getSessionId());
			}
		}
	}

	/**
	 * Gets the data.
	 *
	 * @param formFields the form fields
	 * @param cookies    the cookies
	 * @return the data
	 * @throws Exception the exception
	 */
	private Map<String, String> getData(Map<String, String> formFields, Map<String, String> cookies) throws Exception {
		Connection conn = Jsoup.connect(IMInformeCAPRequestCapListener.URL).//
				userAgent(IMInformeCAPRequestCapListener.USER_AGENT)
				// not neccesary but these extra headers won't hurt
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")//
				.header("Accept-Encoding", "gzip, deflate")//
				.header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")//
				.header("Host", IMInformeCAPRequestCapListener.HOST)//
				.header("Referer", IMInformeCAPRequestCapListener.URL + IMInformeCAPRequestCapListener.QUERY_STR)//
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

	/**
	 * Extract data.
	 *
	 * @param doc the doc
	 * @return the map
	 * @throws Exception the exception
	 */
	private Map<String, String> extractData(Document doc) throws Exception {
		Map<String, String> res = new HashMap<>();
		int i = 0;
		for (Element row : doc.select("table.resultados tr")) {
			Elements columns = row.select("td");
			if (columns.size() == 2) {
				res.put(columns.get(0).text(), columns.get(1).text());
			} else if (columns.size() == 3) {
				res.put(columns.get(1).text(), columns.get(2).text());
			} else if (columns.size() != 0) { // la cabecera tiene solo th
				IMInformeCAPRequestCapListener.logger.warn("column number unknow {}", columns.size());
			}
			i++;
		}
		if (i == 0) {
			Elements select = doc.select("p.mensajeError");
			if (select.size() == 1) {
				throw new Exception(select.get(0).text());
			}
			throw new Exception("E_UNKNOW_ERROR");
		}
		return res;
	}
	private void ensureWebview() {
		if (this.webView != null) {
			return;
		}
		FxUtils.runSync(() -> {
			this.stage = new Stage();
			// Create the WebView
			this.webView = new WebView();
			// Create the WebEngine
			final WebEngine webEngine = this.webView.getEngine();
			webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36");
			this.stage.setTitle("CAP");
			WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> {
				System.out.println(message + "[at " + lineNumber + "]");
			});

			// Create the VBox
			VBox root = new VBox();
			// Add the WebView to the VBox
			root.getChildren().add(this.webView);

			// Set the Style-properties of the VBox
			root.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
					+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: blue;");

			// Create the Scene
			Scene scene = new Scene(root);
			// Add the Scene to the Stage
			this.stage.setScene(scene);
			// Display the Stage
		});
	}

	/**
	 * Run.
	 *
	 * @param dni the dni
	 * @param row
	 * @param table
	 * @param cif
	 * @param idConductor
	 * @return the map
	 * @throws Exception   the exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void run(String dni, Object idConductor, Object cif, UTable table, int row)  {
		this.ensureWebview();

		final WebEngine webEngine = this.webView.getEngine();
		ChangeListener<State> listener = new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				IMInformeCAPRequestCapListener.this.onWebchange(ov, oldState, newState,dni,idConductor,cif,table,row,this);
			}
		};
		Platform.runLater(() -> webEngine.loadContent("<html><body>hola</body></html>"));
		Platform.runLater(() -> {
			webEngine.getLoadWorker().stateProperty().addListener(listener);
			// LOad the Start-Page
			webEngine.load("https://apps.fomento.gob.es/crgt/servlet/ServletController?modulo=datosconsulta&accion=inicio&lang=es&estilo=default");
			this.stage.show();
		});
	}

	private void onWebchange(ObservableValue<? extends State> ov, State oldState, State newState, String dni, Object idConductor, Object cif, UTable table, int row, ChangeListener<? super State> listener) {
		final WebEngine webEngine = this.webView.getEngine();
		String url = webEngine.getLocation();
		if ((newState == State.SUCCEEDED) && url.equals(
				"https://apps.fomento.gob.es/crgt/servlet/ServletController?modulo=datosconsulta&accion=inicio&lang=es&estilo=default")) {

			// function check()document.getElementById("P").checked = true;}
			webEngine.executeScript("function check(){" + "document.getElementById(\"P\").checked = true;"
					+ "document.getElementById(\"consulta\").value = \"" + dni + "\";"
					+ "document.getElementsByTagName('h1')[0].style.display = 'none';"
					+ "document.getElementsByTagName('p')[0].style.display = 'none';"
					+ "document.getElementsByTagName('legend')[0].style.display = 'none';"
					+ "document.getElementById(\"Cabecera\").style.display = \"none\";"
					+ "document.getElementById(\"N\").style.display = \"none\";"
					+ "document.getElementById(\"labelN\").style.display = \"none\";"
					+ "document.getElementById(\"NO\").style.display = \"none\";"
					+ "document.getElementById(\"labelNO\").style.display = \"none\";"
					+ "document.getElementById(\"MA\").style.display = \"none\";"
					+ "document.getElementById(\"labelMA\").style.display = \"none\";"
					+ "document.getElementById(\"Pie1_TablaPie\").style.display = \"none\";"
					+ "document.getElementById(\"consulta\").style.display = \"none\";"
					+ "getElementsByClassName('region region-miga-pan-cabecera');"
					+ "getElementsByClassName('dropdownmenu');"
					+ "getElementsByClassName('aside_left');"
					+ "getElementsByClassName('aside_right');"
					+ "getElementsByClassName('left200');"
					+ "getElementsByClassName('footer_group');"
					+ ""
					+ "		function getElementsByClassName(classname, node)  {"
					+ "			if(!node) node = document.getElementsByTagName(\"body\")[0];"
					+ "			var a = [];"
					+ "			var re = new RegExp('\\\\b' + classname + '\\\\b');"
					+ "			var els = node.getElementsByTagName(\"*\");"
					+ "			for(var i=0,j=els.length; i<j; i++)"
					+ "				if(re.test(els[i].className))a.push(els[i]);"
					+ "			for(i in a ){"
					+ "				a[i].style.display = \"none\";"
					+ "			}"
					+ "		}"
					+ "}"
					+ "\n"
					+ "check();");
		}
		if ((newState == State.SUCCEEDED) && url.equals("https://apps.fomento.gob.es/crgt/servlet/ServletController")) {

			// use jsoup helper methods to convert it to string
			String html = new org.jsoup.helper.W3CDom().asString(webEngine.getDocument());
			// create jsoup document by parsing html
			Document doc = Jsoup.parse(html);
			Map<String, String> res = new HashMap<String, String>();
			try {
				res = this.extractData(doc);
				this.onCapExtractedOk(res,dni,idConductor,cif,table,row);
			} catch (Exception err) {
				this.onCapExtractedError(err);
			} finally {
				webEngine.getLoadWorker().stateProperty().addListener(listener);
			}

		}
	}

	private void onCapExtractedError(Exception err) {
		this.stage.hide();
		SwingUtilities.invokeLater(()-> MessageManager.getMessageManager().showExceptionMessage(err, IMInformeCAPRequestCapListener.logger));
	}

	private void onCapExtractedOk(Map<String, String> res,String dni, Object idConductor, Object cif,UTable table, int row) {
		this.stage.hide();
		new USwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				IMInformeCAPRequestCapListener.this.updateDb(res, idConductor, cif);
				return null;
			}
			@Override
			protected void done() {
				try {
					this.uget();
					table.refreshRow(row);
				} catch (Exception err) {
					MessageManager.getMessageManager().showExceptionMessage(err, IMInformeCAPRequestCapListener.logger);
				}

			};
		}.executeOperation(this.getForm());

	}

}