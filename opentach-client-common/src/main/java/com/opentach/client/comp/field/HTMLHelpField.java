package com.opentach.client.comp.field;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.field.IdentifiedAbstractFormComponent;
import com.ontimize.jee.common.tools.FileTools;

/**
 * <p> Componente html con hiperenlaces para realizar diferentes acciones sobre los elementos de un formulario. </p> <br> El texto que se muestra se
 * corresponde con el <code>attr</code> del componente. Este atributo puede ser de una de las siguientes formas: <ul> <li>El texto html que mostrará
 * el componente.</li> <li>La ruta de un fichero html con el texto que se quiere mostrar.</li> <li>Un valor que se encuentra en el bundle de la
 * aplicación cuya traducción se corresponde con alguna de las dos opciones anteriores</li> </ul> Los hiperenlaces del html se corresponden con
 * acciones sobre los elementos del formulario. <br> Estas acciones se configuran indicando el <code>atributo</code> del elemento del formulario que
 * realiza la acción y el método de este componente al que debe llamarse.<br> Ejemplos:<br>
 *
 * <pre>
 *  &lt;html&gt; &lt;body&gt; &lt;a href=&quot;EPacketsItems.abreFormularioDetalleInsertar()&quot;&gt;&lt;img
 * src=&quot;com/transfrio/client/common/images/information24.png&quot;&gt;Mostrar formulario de inserción de la tabla EPacketsItems&lt;/a&gt; &lt;a
 * href=&quot;EPacketsItems.abreFormularioDetalle(getFilaSeleccionada())&quot;&gt;Abrir el formulario detalle de la fila seleccionada de la tabla
 * EPacketsItems&lt;/a&gt; &lt;a href=&quot;consultar.doClick()&quot;&gt;Hacer click en el botón de buscar del formulario&lt;/a&gt; &lt;/body&gt;
 * &lt;/html&gt;
 * </pre>
 *
 * TODO:<br> Permitir operar con resultados de métodos(meter más de 1 punto), ej: AttrCampo.getCampoDatos().requestFocus()
 *
 * @author joaquin.romero
 */
public class HTMLHelpField extends IdentifiedAbstractFormComponent implements HyperlinkListener {

	private static final Logger		logger				= LoggerFactory.getLogger(HTMLHelpField.class);

	protected MyJEditorPane			htmlViewer			= new MyJEditorPane();
	protected boolean				horizontalScroll;
	protected boolean				verticalScroll;
	protected boolean				scrollable;
	protected int					lastDragYPosition	= 0;
	protected JScrollPane			sp;
	protected boolean				hasHiperlinks		= false;
	protected Map<String, String>	dictionary;

	public HTMLHelpField(Hashtable p) throws Exception {
		super();
		this.dictionary = new HashMap<>();
		this.init(p);
		this.setLayout(new BorderLayout());
		if (this.scrollable) {
			this.sp = new JScrollPane(this.htmlViewer);
			this.sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			this.sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			this.sp.setBackground(this.getBackground());
			this.sp.getViewport().setBackground(this.getBackground());
			this.sp.setBorder(BorderFactory.createEmptyBorder());
			this.sp.setOpaque(false);
			this.htmlViewer.setFocusable(false);

			this.add(this.sp);
		} else {
			this.add(this.htmlViewer);
		}

		if (p.containsKey("hiperlinks") && p.get("hiperlinks").equals("yes")) {
			this.hasHiperlinks = true;
		}
		this.htmlViewer.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (HTMLHelpField.this.hasHiperlinks) {
					try {
						System.out.println("X vale  " + e.getX() + " Y vale " + e.getY());

						if ((e.getX() > 342) && (e.getX() < 455) && (e.getY() > 260) && (e.getY() < 280)) {
							Desktop.getDesktop().browse(new URI("http://www.opentach.com/condiciones-generales-de-uso.html"));
						} else if ((e.getX() > 30) && (e.getX() < 150) && (e.getY() > 260) && (e.getY() < 280)) {
							Desktop.getDesktop().browse(new URI("http://www.opentach.com/politica-de-privacidad.html"));
						} else if ((e.getX() > 352) && (e.getX() < 460) && (e.getY() > 235) && (e.getY() < 255)) {
							Desktop.getDesktop().browse(new URI("http://www.opentach.com"));
						}

					} catch (Exception e1) {
					}
				}
			}
		});

		this.htmlViewer.setEditable(false);
		this.htmlViewer.setOpaque(false);
		this.setOpaque(false);
		((HTMLDocument) this.htmlViewer.getDocument()).setBase(this.getClass().getClassLoader().getResource(""));
		this.htmlViewer.addHyperlinkListener(this);
	}

	@Override
	public Object getConstraints(LayoutManager parentLayout) {
		// TODO Auto-generated method stub
		Object constraints = super.getConstraints(parentLayout);
		((GridBagConstraints) constraints).insets = new Insets(0, 0, 0, 0);
		return constraints;
	}

	/**
	 * Inicializa el componente con estos parámetros:<br> <ul> <li><b>attr:</b>Atributo de campo</li> <li><b>scroll</b> (yes/<b>no</b>):Indica si el
	 * componente va a usar scroll</li> <li><b>scrollhorizontal</b> (yes/<b>no</b>): ajusta el componente para tener scroll horizontal</li>
	 * <li><b>scrollvertical</b> (yes/<b>no</b>):ajusta el compnente para tener scroll vertical</li> </ul>
	 */

	@Override
	public void init(Hashtable p) throws Exception {
		this.attribute = p.get("attr");
		if (this.attribute == null) {
			throw new IllegalArgumentException(this.getClass().toString() + " -> 'attr' es obligatorio");
		}

		// if ("yes".equalsIgnoreCase((String) p.get("label")))
		// htmlViewer.setFocusable(false);

		if (p.containsKey("dimension") && (p.get("dimension") != null)) {
			String[] dimension = ((String) p.get("dimension")).split(";");
			if (dimension.length == 2) {
				String width = dimension[0];
				String heigth = dimension[1];
				if ((width != null) && (heigth != null)) {
					Dimension d = new Dimension(Integer.valueOf(width), Integer.valueOf(heigth));
					this.setPreferredSize(d);
				}
			}
		}

		Object bgcolor = p.get("bgcolor");
		if (bgcolor != null) {
			String bg = bgcolor.toString();
			if (bg.indexOf(";") > 0) {
				try {
					this.setBackground(ColorConstants.colorRGBToColor(bg));
				} catch (Exception e) {
					System.out.println(this.getClass().toString() + ": Error in parameter 'bgcolor': " + e.getMessage());
				}
			} else {
				try {
					this.setBackground(ColorConstants.parseColor(bg));
				} catch (Exception e) {
					System.out.println(this.getClass().toString() + ": Error in parameter 'bgcolor': " + e.getMessage());
				}
			}
		}

		if ("yes".equalsIgnoreCase((String) p.get("scroll"))) {
			this.scrollable = true;
			if ("yes".equalsIgnoreCase((String) p.get("scrollhorizontal"))) {
				this.horizontalScroll = true;
			} else {
				this.horizontalScroll = false;
			}

			if ("yes".equalsIgnoreCase((String) p.get("scrollvertical"))) {
				this.verticalScroll = true;
			} else {
				this.verticalScroll = false;
			}

		} else {
			this.scrollable = false;
		}

		if ("no".equalsIgnoreCase((String) p.get("border"))) {
			this.htmlViewer.setBorder(BorderFactory.createEmptyBorder());
		}
	}

	@Override
	public void setResourceBundle(ResourceBundle resourceBundle) {
		super.setResourceBundle(resourceBundle);
		String text = null;
		try {
			text = resourceBundle.getString((String) this.attribute);
		} catch (Exception e) {

		}
		this.setText((String) (text == null ? this.attribute : text));
	}

	public void setDictionary(Map<String, String> dictionary) {
		this.dictionary = dictionary;
	}

	public void setText(String templatePath) {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(templatePath);
		String html = null;
		if (is == null) {
			html = templatePath;
		} else {
			try {
				html = new String(FileTools.getBytesFromFile(is));
			} catch (IOException e) {
				HTMLHelpField.logger.error(null, e);
			}
		}

		for (Entry<String, String> entry : this.dictionary.entrySet()) {
			html = ApplicationManager.replaceText(html, entry.getKey(), entry.getValue());
		}
		this.htmlViewer.setText(html);

		if (this.sp != null) {
			this.sp.getViewport().setViewPosition(new Point(0, 0));
		}
	}

	public void clean() {
		this.htmlViewer.setText("");
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent evt) {
		String href = evt.getDescription();
		int indicepunto = href.indexOf(".");
		if (indicepunto == -1) {
			System.err.println("Formato HREF no reconocido");
			return;
		}

		if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
			// ToolTipManager.sharedInstance().registerComponent(label);
			// ToolTipManager.sharedInstance().setInitialDelay(10);
			// ToolTipManager.sharedInstance().setReshowDelay(0);
			// ToolTipManager.sharedInstance().setLightWeightPopupEnabled(true);
		} else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED) {
			// label.setToolTipText(null);
			// ToolTipManager.sharedInstance().unregisterComponent(label);
		} else if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String attr = href.substring(0, indicepunto);
			String method = href.substring(indicepunto + 1);
			Object ob = this.parentForm.getButton(attr);
			if (ob == null) {
				ob = this.parentForm.getDataFieldReference(attr);
			}
			if (ob == null) {
				ob = this.parentForm.getElementReference(attr);
			}
			try {
				this.doAction(ob, method);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void doAction(Object ob, String action) throws Exception {
		this.getParameterInfo(ob, action);
	}

	protected ParameterInfo getParameterInfo(Object ob, String param) throws Exception {
		if (param.indexOf("(") != -1) { // Es un método
			int first = param.indexOf("(");
			int last = param.lastIndexOf(")");
			String methodName = param.substring(0, first);
			String params[] = this.getParameters(param.substring(first + 1, last));
			ParameterInfo[] parameterInfo = new ParameterInfo[params.length];
			for (int i = 0; i < params.length; i++) {
				parameterInfo[i] = this.getParameterInfo(ob, params[i]);
			}
			Method method = this.getMethod(ob, methodName, parameterInfo);
			Object[] paramsValues = new Object[parameterInfo.length];
			for (int i = 0; i < parameterInfo.length; i++) {
				paramsValues[i] = parameterInfo[i].getTheValue();
			}
			return new ParameterInfo(method.getReturnType(), method.invoke(ob, paramsValues));
		} else {
			if ("true".equalsIgnoreCase(param)) {
				return new ParameterInfo(Boolean.TYPE, Boolean.TRUE);
			}
			if ("false".equalsIgnoreCase(param)) {
				return new ParameterInfo(Boolean.TYPE, Boolean.FALSE);
			}
			try {
				return new ParameterInfo(Integer.TYPE, Integer.valueOf(param));
			} catch (Exception e) {
			}
			try {
				return new ParameterInfo(Double.TYPE, Double.valueOf(param));
			} catch (Exception e) {
			}
			return new ParameterInfo(String.class, param);
		}
	}

	protected Method getMethod(Object ob, String methodName, ParameterInfo[] parameters) throws SecurityException, NoSuchMethodException {
		Class[] paramsClass = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			paramsClass[i] = parameters[i].getTheClass();
		}
		return ob.getClass().getMethod(methodName, paramsClass);
	}

	protected String[] getParameters(String paramString) {
		StringTokenizer st = new StringTokenizer(paramString, ",");
		int tokens = st.countTokens();
		String[] response = new String[tokens];
		for (int i = 0; i < tokens; i++) {
			response[i] = st.nextToken();
		}
		return response;
	}

	static class ParameterInfo {
		Class	theClass;
		Object	theValue;

		public ParameterInfo(Class c, Object o) {
			this.theClass = c;
			this.theValue = o;
		}

		public Class getTheClass() {
			return this.theClass;
		}

		public Object getTheValue() {
			return this.theValue;
		}
	}

	class MyJEditorPane extends JEditorPane implements Scrollable {
		public MyJEditorPane() {
			super("text/html", "");
			this.setEditorKit(new HTMLEditorKit() {
				@Override
				public ViewFactory getViewFactory() {
					return new HTMLFactoryX();
				}

				class HTMLFactoryX extends HTMLFactory implements ViewFactory {
					@Override
					public View create(Element elem) {
						Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
						if (o instanceof HTML.Tag) {
							HTML.Tag kind = (HTML.Tag) o;
							if (kind == HTML.Tag.IMG) {
								return new ImageView(elem) {
									@Override
									public URL getImageURL() {
										String s = (String) this.getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
										// URL url = super.getImageURL();
										// if (url == null) {
										URL url = this.getClass().getClassLoader().getResource(s);
										// }
										return url;
									}
								};
							}
						}
						return super.create(elem);
					}
				}
			});
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			if (HTMLHelpField.this.horizontalScroll && HTMLHelpField.this.verticalScroll) {
				return new Dimension(20, 20);
			} else if (HTMLHelpField.this.horizontalScroll) {
				return new Dimension((int) this.getPreferredSize().getWidth(), 20);
			} else if (HTMLHelpField.this.verticalScroll) {
				return new Dimension(20, (int) this.getPreferredSize().getHeight());
			} else {
				return this.getPreferredSize();
			}
		}

		@Override
		public void layout() {
			try {
				super.layout();
			} catch (Exception e) {
			}
		}
	}
}
