package com.opentach.client.comp;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Types;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.util.ParseUtils;

public class Ticker extends JPanel implements FormComponent, IdentifiedElement, DataComponent {

	private static final Integer	DEFAULT_SPACER		= 20;
	private static String			SENTENCE_SEPARATOR	= "~";

	protected JPanel				tickerpanel;
	protected boolean				paused;
	protected String[]				texts;
	protected Object				attr;
	protected int					speed;
	protected int					spacer;
	protected Font					font;
	protected String				separator;

	public Ticker(Hashtable parameters) throws Exception {
		super();
		this.init(parameters);
		this.setOpaque(true);
		this.setBackground(Color.red);
	}

	@Override
	public Object getConstraints(LayoutManager arg0) {
		return new GridBagConstraints(-1, -1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
	}

	@Override
	public void init(Hashtable parameters) throws Exception {

		PauseMouseListener pauseMouseListener = new PauseMouseListener();
		this.tickerpanel = new JPanel();
		this.addMouseListener(pauseMouseListener);
		this.setOpaque(false);
		this.setLayout(null);
		this.add(this.tickerpanel);

		this.speed = ParseUtils.getInteger((String) parameters.get("speed"), 25);
		this.spacer = ParseUtils.getInteger((String) parameters.get("spacer"), Ticker.DEFAULT_SPACER);
		this.font = ParseUtils.getFont((String) parameters.get("font"), this.getFont());
		this.attr = parameters.get("attr");
		this.paused = false;

		this.tickerpanel.addMouseListener(pauseMouseListener);

		this.updateText((String) parameters.get("text"));

		new Thread(new Runnable() {
			@Override
			public void run() {
				int x = Ticker.this.getWidth();
				while (true) {
					if (!Ticker.this.paused) {
						x -= Ticker.this.speed;
						if (x < -Ticker.this.tickerpanel.getWidth()) {
							x = Ticker.this.getWidth();
						}
						Ticker.this.tickerpanel.setLocation(x, 0);
					}
					try {
						Thread.sleep(25);
					} catch (InterruptedException exception) {
					}
				}
			}
		}).start();
	}

	/**
	 * Messages are separeted by ~ character.
	 * 
	 * @param text
	 */
	protected void updateText(String text) {
		if (this.getValue().equals(text)) {
			return;
		}
		this.texts = (text == null) ? new String[0] : text.split(Ticker.SENTENCE_SEPARATOR);
		this.tickerpanel.removeAll();
		for (int i = 0; i < this.texts.length; i++) {
			// Spacing between messages
			if (i > 0) {
				this.tickerpanel.add(Box.createHorizontalStrut(this.spacer));
			}
			JLabel link = new JLabel(this.texts[i]);
			link.setFont(this.font);
			link.updateUI();
			this.tickerpanel.add(link);
		}
		this.tickerpanel.setLocation(0, 0);
		this.tickerpanel.setOpaque(false);
		this.tickerpanel.setSize(this.tickerpanel.getPreferredSize());
	}

	@Override
	public Vector getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(Locale locale) {}

	@Override
	public void setResourceBundle(ResourceBundle bundle) {}

	@Override
	public Object getAttribute() {
		return this.attr;
	}

	@Override
	public void initPermissions() {}

	@Override
	public boolean isRestricted() {
		return false;
	}

	@Override
	public void deleteData() {
		this.updateText(null);
	}

	@Override
	public String getLabelComponentText() {
		return null;
	}

	@Override
	public int getSQLDataType() {
		return Types.VARCHAR;
	}

	@Override
	public Object getValue() {
		if (this.texts == null) {
			return "";
		}
		StringBuilder value = new StringBuilder();
		for (String s : this.texts) {
			value.append(s + Ticker.SENTENCE_SEPARATOR);
		}
		return value.substring(0, value.length() == 0 ? 0 : value.length() - 1);
	}

	@Override
	public boolean isEmpty() {
		return this.texts.length == 0;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public boolean isModifiable() {
		return false;
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public void setModifiable(boolean flag) {}

	@Override
	public void setRequired(boolean flag) {}

	@Override
	public void setValue(Object obj) {
		this.updateText((String) obj);
	}

	private final class PauseMouseListener extends MouseAdapter {
		@Override
		public void mouseEntered(MouseEvent event) {
			Ticker.this.paused = true;
		}

		@Override
		public void mouseExited(MouseEvent event) {
			Ticker.this.paused = false;
		}
	}
}