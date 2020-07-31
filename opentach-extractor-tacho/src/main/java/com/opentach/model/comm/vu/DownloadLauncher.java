package com.opentach.model.comm.vu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.ontimize.gui.ApplicationManager;

public class DownloadLauncher extends Thread {
	public static byte[] intToBytes(int i) {
		byte[] b = new byte[4];
		b[0] = (byte) (i >> 24);
		b[1] = (byte) (i >> 16);
		b[2] = (byte) (i >> 8);
		b[3] = (byte) i;

		return b;
	}

	@Override
	public void run() {
		JFrame f = new JFrame(ApplicationManager.getTranslation("COMUNICACION_VU"));
		f.setIconImage(ApplicationManager.getApplication().getFrame().getIconImage());
		f.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		DownloadFrame df = new DownloadFrame();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
		df.text.setText("VU_" + (int) (9999 * Math.random()) + "_" + sdf.format(new Date()));

		f.getContentPane().setLayout(new GridBagLayout());
		f.getContentPane().add(df,
				new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		f.pack();
		DownloadLauncher.center(f);
		f.setVisible(true);
	}

	public static void center(Component c) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (d.width / 2) - (c.getWidth() / 2);
		int y = (d.height / 2) - (c.getHeight() / 2);
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		if (x > d.width) {
			x = 0;
		}
		if (y > d.height) {
			y = 0;
		}
		c.setLocation(x, y);
	}

	public static void putRightBottom(Component c) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) d.getWidth() - c.getWidth() - 3;
		int y = (int) d.getHeight() - c.getHeight() - 53;

		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		if (x > d.width) {
			x = 0;
		}
		if (y > d.height) {
			y = 0;
		}
		c.setLocation(x, y);
	}

}
