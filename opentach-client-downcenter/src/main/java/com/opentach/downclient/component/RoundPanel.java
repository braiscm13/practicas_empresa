package com.opentach.downclient.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.plaf.utils.StyleUtil;
import com.utilmize.client.gui.Row;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class RoundPanel extends Row implements IUFormComponent {

	private static final Logger				logger	= LoggerFactory.getLogger(RoundPanel.class);

	private Color							externalColor;
	private Color							mediumColor;
	private Color							internalColor;
	private Color							enteredExternalColor;
	private Color							enteredMediumColor;
	private Color							enteredInternalColor;
	private boolean							entered;
	private MouseAdapter					actionMouseListener;
	private AbstractActionListenerButton	defaultListener;
	private List<ActionListener>			listeners;

	public RoundPanel(Hashtable parameters) throws Exception {
		super(parameters);
	}

	@Override
	public void init(Hashtable parameters) {
		super.init(parameters);
		this.listeners = new ArrayList<>(1);
		this.entered = false;
		this.setBackground(null);
		this.setOpaque(false);
		this.setLayout(new GridBagLayout());
		this.installListener(parameters);

	}

	/**
	 * Install listener.
	 */
	protected void installListener(Hashtable params) {
		String listenerClassName = ParseUtilsExtended.getString((String) params.get("listener"), null);
		ActionListener al = null;
		if (listenerClassName != null) {
			try {
				al = ReflectionTools.newInstance(listenerClassName, ActionListener.class, this, params);
			} catch (Exception e) {
				try {
					al = ReflectionTools.newInstance(listenerClassName, ActionListener.class, params);
				} catch (Exception ex) {
					RoundPanel.logger.error("no se pudo instalar el escuchador {} se reintentara mas tarde method1", listenerClassName, e);
					RoundPanel.logger.error("no se pudo instalar el escuchador {} se reintentara mas tarde method2", listenerClassName, ex);
				}
			}
		}
		if (al != null) {
			this.addMouseListener(new MouseEnteredListener());
			this.actionMouseListener = new ActionMouseListener();
			this.addMouseListener(this.actionMouseListener);

			if (al instanceof AbstractActionListenerButton) {
				this.defaultListener = (AbstractActionListenerButton) al;
			}
			this.addActionListener(al);
		}
	}

	public void addActionListener(ActionListener al) {
		this.listeners.add(al);
	}

	private void fireActionEvent(ActionEvent ev) {
		for (ActionListener al : this.listeners) {
			al.actionPerformed(ev);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.button.Button#setParentForm(com.ontimize.gui.Form)
	 */
	@Override
	public void setParentForm(Form f) {
		super.setParentForm(f);
		if (this.defaultListener != null) {
			this.defaultListener.parentFormSetted();
		}
	}

	@Override
	public void updateUI() {
		super.updateUI();
		this.externalColor = StyleUtil.getColor("RoundLabel", "externalColor", "#ffffff4c");
		this.mediumColor = StyleUtil.getColor("RoundLabel", "mediumColor", "#ffffff66");
		this.internalColor = StyleUtil.getColor("RoundLabel", "internalColor", "#ffffff7f");
		this.enteredExternalColor = StyleUtil.getColor("RoundLabel", "enteredExternalColor", "#094e9599");
		this.enteredMediumColor = StyleUtil.getColor("RoundLabel", "enteredMediumColor", "#1996d799");
		this.enteredInternalColor = StyleUtil.getColor("RoundLabel", "enteredInternalColor", "#45c9fe");
	}

	@Override
	public void setResourceBundle(ResourceBundle res) {
		super.setResourceBundle(res);
	}

	@Override
	public void add(Component comp, Object constraints) {
		super.add(comp, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		comp.addMouseListener(this.actionMouseListener);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int width = this.getWidth();
		int height = this.getHeight();
		int x = 0;
		int y = 0;
		if (width > height) {
			x = (width - height) / 2;
			width = height;
		} else {
			y = (height - width) / 2;
			height = width;
		}

		Graphics2D graphics = (Graphics2D) g;

		// Sets antialiasing if HQ.
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draws the rounded opaque panel with borders.
		int corona = (int) ((width * 0.15f) / 2f);
		graphics.setColor(this.entered ? this.enteredExternalColor : this.externalColor);
		graphics.setBackground(this.entered ? this.enteredExternalColor : this.externalColor);
		graphics.fillOval(x, y, width, height);

		graphics.setColor(this.entered ? this.enteredMediumColor : this.mediumColor);
		graphics.setBackground(this.entered ? this.enteredMediumColor : this.mediumColor);
		graphics.fillOval(x + corona, y + corona, width - (2 * corona), height - (2 * corona));

		graphics.setColor(this.entered ? this.enteredInternalColor : this.internalColor);
		graphics.setBackground(this.entered ? this.enteredInternalColor : this.internalColor);
		graphics.fillOval(x + (2 * corona), y + (2 * corona), width - (4 * corona), height - (4 * corona));


	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public Dimension getPreferredSize() {
		return super.getPreferredSize();
	}

	protected class MouseEnteredListener extends MouseAdapter {
		@Override
		public void mouseEntered(MouseEvent e) {
			if (!RoundPanel.this.entered) {
				RoundPanel.this.entered = true;
				RoundPanel.this.repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (!RoundPanel.this.entered) {
				RoundPanel.this.entered = true;
				RoundPanel.this.repaint();
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			Component c = SwingUtilities.getDeepestComponentAt(e.getComponent(), e.getX(), e.getY());
			// doesn't work if you move your mouse into the combobox popup
			boolean newEntered = (c != null) && SwingUtilities.isDescendingFrom(c, RoundPanel.this);
			if (newEntered != RoundPanel.this.entered) {
				RoundPanel.this.entered = newEntered;
				RoundPanel.this.repaint();
			}
		}
	}

	protected class ActionMouseListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			RoundPanel.this.entered = false;
			RoundPanel.this.repaint();
			RoundPanel.this.fireActionEvent(new ActionEvent(this, -1, "cmd"));
		}
	}

	@Override
	public Form getParentForm() {
		return this.parentForm;
	}
}
