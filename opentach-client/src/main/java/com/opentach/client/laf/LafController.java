package com.opentach.client.laf;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imlabs.client.laf.common.UStyleUtil;
import com.imlabs.client.laf.utils.ColorChangerImageManager;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.plaf.utils.StyleUtil;

public class LafController {
	private static LafController	instance;
	public static final String		PROPERTY_LAF_STYLE	= "lafStyle";
	private static final Logger		logger				= LoggerFactory.getLogger(LafController.class);
	public final static String		LAF_BLUE			= "OPENTACH_BLUE";
	public final static String		LAF_WHITE_ORANGE	= "WHITE_ORANGE";
	public final static String		LAF_WHITE			= "WHITE";
	public final static String		LAF_BROWN			= "BROWN";
	public final static String		LAF_BLACK			= "BLACK";
	public final static String		LAF_LIGHT_BLUE		= "LIGHT_BLUE";

	public static LafController getInstance() {
		if (LafController.instance == null) {
			LafController.instance = new LafController();
		}
		return LafController.instance;
	}

	public void installCustomLaf(String user) {
		String preference = ApplicationManager.getApplication().getPreferences().getPreference(user, LafController.PROPERTY_LAF_STYLE);
		// preference = LafController.LAF_LIGHT_BLUE;
		String lafClass = null;
		if (preference != null) {
			switch (preference) {
				case LAF_BLUE:
					System.setProperty(StyleUtil.STYLE_PROPERTY, "com/opentach/client/laf/blue.css");
					lafClass = OpentachLAF.class.getName();
					break;
				case LAF_WHITE_ORANGE:
					System.setProperty(StyleUtil.STYLE_PROPERTY, "com/opentach/client/laf/white-orange.css");
					lafClass = OpentachLAF.class.getName();
					break;
				case LAF_WHITE:
					System.setProperty(StyleUtil.STYLE_PROPERTY, "com/opentach/client/laf/white.css");
					lafClass = OpentachLAF.class.getName();
					break;
				case LAF_BROWN:
					System.setProperty(StyleUtil.STYLE_PROPERTY, "com/opentach/client/laf/brown.css");
					lafClass = OpentachLAF.class.getName();
					break;
				case LAF_BLACK:
					System.setProperty(StyleUtil.STYLE_PROPERTY, "com/opentach/client/laf/black.css");
					lafClass = OpentachLAF.class.getName();
					break;
				case LAF_LIGHT_BLUE:
					System.setProperty(StyleUtil.STYLE_PROPERTY, "com/opentach/client/laf/light_blue_style.css");
					lafClass = OpentachLAF.class.getName();
					break;
				default:
					break;
			}

		}

		String preferenceFontSize = ApplicationManager.getApplication().getPreferences().getPreference(user, UStyleUtil.FONT_SIZE_PROPERTY);
		if (preferenceFontSize != null) {
			System.setProperty(UStyleUtil.FONT_SIZE_PROPERTY, preferenceFontSize);
		}

		if (lafClass != null) {
			try {
				UIManager.setLookAndFeel(lafClass);
				JFrame frame = (JFrame) ApplicationManager.getApplication().getFrame();
				this.updateUI((JComponent) frame.getContentPane());
				((ColorChangerImageManager) ImageManager.getEngine()).resetImageCache();

			} catch (Exception e) {
				LafController.logger.error(null, e);
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception ex) {
					LafController.logger.error(null, ex);
				}

			}
		}
	}

	private void updateUI(JComponent root) {
		root.updateUI();
		if (root instanceof Container) {
			for (Component component : root.getComponents()) {
				if (component instanceof JComponent) {
					this.updateUI((JComponent) component);
				} else {
					LafController.logger.warn("UI element \"" + component.getClass().getName() + "\"cannot update UI");
				}
			}
		}
	}
}
