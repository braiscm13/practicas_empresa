package com.opentach.client.comp.activitychart;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

public class ImageMarker implements Marker {
	private final Image	image;

	public ImageMarker(URL filename) {
		ImageIcon icon = new ImageIcon(filename);
		this.image = icon.getImage();
	}

	/**
	 * draw
	 *
	 * @param g
	 *            Graphics
	 * @param x
	 *            int
	 * @param y
	 *            int
	 * @param datacolor
	 *            Color
	 */
	@Override
	public void draw(Graphics g, int x, int y, Color datacolor) {
		Graphics2D g2 = (Graphics2D) g;
		if (this.image != null) {
			g2.drawImage(this.image, x - (this.image.getWidth(null) / 2), y - (this.image.getHeight(null) / 2), null);
		}
	}

}
