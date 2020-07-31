package com.opentach.downclient.component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Hashtable;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.util.ParseUtils;
import com.utilmize.client.gui.field.ULabel;

public class ResizableLabel extends ULabel {

	protected static final double	SCALE_MIN_SIZE_FULL	= 715d;

	protected double	scale;
	protected double	maxFullHeight;

	public ResizableLabel(Hashtable parameters) {
		super(parameters);
		this.scale = 1.0d;
	}

	@Override
	public void init(Hashtable parameters) {
		super.init(parameters);

		this.maxFullHeight = ParseUtils.getDouble((String) parameters.get("maxfullheight"), ResizableLabel.SCALE_MIN_SIZE_FULL);
	}

	@Override
	public void setParentForm(Form f) {
		super.setParentForm(f);
		f.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				ResizableLabel.this.invalidate();
			}

		});
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		try {
			// We need to scale this to ensure it enters in available space
			// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension screenSize = ApplicationManager.getApplication().getFrame().getSize();

			double diff = this.maxFullHeight - screenSize.getHeight();
			diff = Math.max(diff, 0d);

			this.scale = (this.label.getIcon().getIconHeight() - diff) / this.label.getIcon().getIconHeight();
			this.scale = Math.min(this.scale, 1.0d);

		} catch (Exception e) {
			e.printStackTrace();
		}
		d = new Dimension((int) Math.ceil(d.getWidth() * this.scale), (int) (d.getHeight() * this.scale));
		return d;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.scale(this.scale, this.scale);

		try {
			super.paint(g2d);
		} finally {
			g.dispose();
		}
	}
}