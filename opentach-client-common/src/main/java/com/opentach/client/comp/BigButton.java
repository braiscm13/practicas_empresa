package com.opentach.client.comp;

import java.awt.LayoutManager;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;

public class BigButton extends JButton implements FormComponent, IdentifiedElement {

	private Object	attr;

	public BigButton(Hashtable params) throws Exception {
		super();
		this.init(params);
	}

	@Override
	public Object getConstraints(LayoutManager layoutmanager) {
		return null;
	}

	@Override
	public void init(Hashtable params) throws Exception {
		Object attr = params.get("attr");
		if (attr == null) {
			if (ApplicationManager.DEBUG) {
				System.out.println(this.getClass().toString() + ": INFO: No se especificó attr");
			}
		} else {
			this.attr = attr;
		}
		Object bgcolor = params.get("bgcolor");
		if (bgcolor != null) {
			this.setContentAreaFilled(false);
			this.setOpaque(true);
			String bg = bgcolor.toString();
			if (bg.indexOf(";") > 0) {
				try {
					this.setBackground(ColorConstants.colorRGBToColor(bg));
				} catch (Exception exception) {
				}
			} else {
				try {
					this.setBackground(ColorConstants.parseColor(bg));
				} catch (Exception e) {
					System.out.println(this.getClass().toString() + " Error en parámetro 'color':" + e.getMessage());
				}
			}
		}
		Object icon = params.get("icon");
		if (icon != null) {
			String archivoIcono = icon.toString();
			java.net.URL urlArchivoIcono = this.getClass().getClassLoader().getResource(archivoIcono);
			if (urlArchivoIcono != null) {
				this.setIcon(new ImageIcon(urlArchivoIcono));

			}
		}
		this.setText((String) attr);
		this.setVerticalTextPosition(SwingConstants.TOP);
		this.setHorizontalTextPosition(SwingConstants.CENTER);
	}

	@Override
	public Vector getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(Locale locale) {

	}

	@Override
	public void setResourceBundle(ResourceBundle res) {
		String textoSegunLocale = null;
		try {
			String claveTexto = (String) this.attr;
			if (res != null) {
				textoSegunLocale = res.getString(claveTexto);
			}
			if (textoSegunLocale != null) {
				super.setText(textoSegunLocale);
			}
		} catch (Exception e) {
			if (ApplicationManager.DEBUG) {
				System.out.println(this.getClass().toString() + " : " + e.getMessage());
			}
		}
	}

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

}
