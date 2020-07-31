package com.opentach.client.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.ontimize.gui.ApplicationManager;

public class SpeedChartRender extends JComponent {
	protected byte[]			data;
	protected boolean			showLabels	= false;
	protected static final int	SPEED_RANGE	= 260;

	public SpeedChartRender() {
		this.setBackground(Color.yellow);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void setLabelsVisible(boolean v) {
		this.showLabels = v;
	}

	protected void paintData(Graphics g, AffineTransform af) {
		Color c = g.getColor();
		g.setColor(Color.red);
		for (int i = 0; (this.data != null) && (i < this.data.length); i++) {
			Point2D p = new Point2D.Double((i), 0x00FF & this.data[i]);
			af.transform(p, p);
			g.fillRect((int) p.getX(), (int) p.getY(), 3, 3);
		}
		g.setColor(c);
	}

	protected void paintAxis(Graphics g, AffineTransform af, int margin) {
		Graphics2D g2 = (Graphics2D) g;
		Stroke olds = g2.getStroke();
		g2.setColor(Color.black);

		Point2D paleft = new Point2D.Double(5, 0);
		// af.transform(paleft, paleft);
		int palx = (int) paleft.getX();

		Point2D pabottom = new Point2D.Double(0, this.getHeight() - (margin / 2));
		// af.transform(pabottom, pabottom);
		int paby = (int) pabottom.getY();

		Point2D p0 = new Point2D.Double(0, 0);
		af.transform(p0, p0);
		int p0x = (int) p0.getX();
		int p0y = (int) p0.getY();

		Point2D psz = new Point2D.Double(this.data.length, SpeedChartRender.SPEED_RANGE);
		// Point2D psz = new Point2D.Double((double) getWidth() - (2 * margin),
		// getHeight() - (2 * margin));
		af.transform(psz, psz);
		int pszx = (int) psz.getX();
		int pszy = (int) psz.getY();

		// Eje x.
		Rectangle2D tb = g.getFontMetrics().getStringBounds("Segundo", g);
		if (this.showLabels) {
			g.drawString("Segundos", (int) (this.getWidth() - tb.getWidth() - margin), (int) (this.getHeight() - tb.getHeight() - margin));
		}
		g2.setStroke(olds);
		g.drawLine(p0x, p0y, pszx, p0y);

		for (int i = 0; i < this.data.length; i++) {
			if ((i % 10) == 0) {
				Point2D p = new Point2D.Double((i), 0x00FF & i);

				af.transform(p, p);
				int x = (int) p.getX();
				if ((i % 60) == 0) {
					g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 6.0f, 10.0f }, 0.0f));
				} else {
					g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 2.0f, 10.0f }, 0.0f));
				}
				g.drawLine(x, pszy, x, p0y);
				if (this.showLabels && ((i % 20) == 0)) {
					g2.setStroke(olds);
					g.drawString("" + i, x, paby);
				}
			}
		}
		// Eje y.
		if (this.showLabels) {
			g.drawString("Velocidad (Km/h)", margin, margin);
		}
		g2.setStroke(olds);
		g.drawLine(p0x, p0y, p0x, pszy);

		for (int i = 0; i < 255; i++) {
			Point2D p = new Point2D.Double((i), 0x00FF & i);
			af.transform(p, p);
			int y = (int) p.getY();

			if ((i % 20) == 0) {
				// drawDashedLine(g, p0x, y, pszx, y, 0.5, 0.8);
				g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 2.0f, 20.0f }, 0.0f));
				g2.drawLine(p0x, y, pszx, y);
				if (this.showLabels) {
					g2.setStroke(olds);
					g2.drawString("" + i, palx, y);
				}
			}
		}
		g2.setStroke(olds);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int margin = 0;
		if (this.showLabels) {
			margin = 30;
		}
		AffineTransform af = new AffineTransform();
		// af.translate(2 * margin, getHeight() - (2 * margin));
		af.translate(margin, this.getHeight() - margin);
		af.scale((double) (this.getWidth() - (2 * margin)) / this.data.length, (double) ((2 * margin) - this.getHeight())
				/ SpeedChartRender.SPEED_RANGE);
		this.paintAxis(g, af, margin);
		this.paintData(g, af);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400, 200);
	}

	public void showChart(String title) {
		JDialog dlg = new JDialog();
		JPanel pnl = new JPanel();
		pnl.setMinimumSize(new Dimension(200, 300));
		dlg.setTitle(title);
		dlg.getContentPane().add(this);
		dlg.setSize(new Dimension(600, 300));
		ApplicationManager.center(dlg);
		dlg.setModal(true);
		dlg.setVisible(true);
		// JOptionPane.showMessageDialog(null, pnl);
	}

}
