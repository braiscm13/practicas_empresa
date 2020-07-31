package com.opentach.client.comp;

import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.FormComponent;

public class HyperlinkComponent extends JPanel implements FormComponent {

	private static final String	FONT		= "<font color='#32cc32' size='+1'>";
	private static final String	FONT_END	= "</font>";
	private static final String	A_HREF		= "<a href=\"";
	private static final String	HREF_CLOSED	= "\">";
	private static final String	HREF_END	= "</a>";
	private static final String	HTML		= "<html>";
	private static final String	HTML_END	= "</html>";

	protected JLabel			labelTitle;
	protected JLabel			labelLink;
	protected String			title;
	protected ResourceBundle	rb;

	public HyperlinkComponent(Hashtable parameters) throws Exception {
		super();
		this.init(parameters);
	}

	@Override
	public Object getConstraints(LayoutManager parentLayout) {
		return new GridBagConstraints(-1, -1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
	}

	@Override
	public void init(Hashtable parameters) throws Exception {
		this.setOpaque(false);
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.title = (String) parameters.get("title");
		this.labelTitle = new JLabel(this.getTranslation(this.title));
		this.add(this.labelTitle);

		this.labelLink = new JLabel((String) parameters.get("link"));
		this.add(this.labelLink);
		if (HyperlinkComponent.isBrowsingSupported()) {
			HyperlinkComponent.makeLinkable(this.labelLink, new LinkMouseListener());
		}
	}

	private String getTranslation(String s) {
		if (s == null) {
			return null;
		}
		try {
			if (this.rb == null) {
				return ApplicationManager.getTranslation(s);
			} else {
				return ApplicationManager.getTranslation(s, this.rb);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static void makeLinkable(JLabel c, MouseListener ml) {
		assert ml != null;
		c.setText(HyperlinkComponent.htmlIfy(HyperlinkComponent.linkIfy(c.getText())));
		c.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		c.addMouseListener(ml);
	}

	// WARNING
	// This method requires that s is a plain string that requires
	// no further escaping
	public static String linkIfy(String s) {
		return HyperlinkComponent.A_HREF.concat(s).concat(HyperlinkComponent.HREF_CLOSED).concat(s).concat(HyperlinkComponent.HREF_END);
	}

	// WARNING
	// This method requires that s is a plain string that requires
	// no further escaping
	private static String htmlIfy(String s) {
		return HyperlinkComponent.HTML.concat(HyperlinkComponent.FONT).concat(s).concat(HyperlinkComponent.FONT_END)
				.concat(HyperlinkComponent.HTML_END);
	}

	@Override
	public Vector<Object> getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(Locale arg0) {

	}

	@Override
	public void setResourceBundle(ResourceBundle arg0) {
		this.rb = arg0;
		this.labelTitle.setText(this.getTranslation(this.title));
	}

	public static boolean isBrowsingSupported() {
		if (!Desktop.isDesktopSupported()) {
			return false;
		}
		boolean result = false;
		Desktop desktop = java.awt.Desktop.getDesktop();
		if (desktop.isSupported(Desktop.Action.BROWSE)) {
			result = true;
		}
		return result;

	}

	public static String getPlainLink(String s) {
		return s.substring(s.indexOf(HyperlinkComponent.A_HREF) + HyperlinkComponent.A_HREF.length(), s.indexOf(HyperlinkComponent.HREF_CLOSED));
	}

	public static class LinkMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(java.awt.event.MouseEvent evt) {
			JLabel l = (JLabel) evt.getSource();
			try {
				Desktop desktop = java.awt.Desktop.getDesktop();
				URI uri = new java.net.URI(HyperlinkComponent.getPlainLink(l.getText()));
				desktop.browse(uri);
			} catch (URISyntaxException use) {
				throw new AssertionError(use);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				JOptionPane.showMessageDialog(null, "Sorry, a problem occurred while trying to open this link in your system's standard browser.",
						"A problem occured", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
