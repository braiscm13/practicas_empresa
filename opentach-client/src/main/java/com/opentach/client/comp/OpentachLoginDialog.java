package com.opentach.client.comp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Hashtable;

import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.Label;
import com.ontimize.gui.field.PasswordDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.login.LoginDialog;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.xml.DefaultXMLParametersManager;

public class OpentachLoginDialog extends LoginDialog {

	private static final Logger logger = LoggerFactory.getLogger(OpentachLoginDialog.class);

	protected class LoginButton extends Button {

		public LoginButton(Hashtable parameters) {
			super(parameters);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Shape s = g.getClip();
			Rectangle r = s.getBounds();
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(r.x, r.y, r.width, r.height);
			super.paintComponent(g);
		}

	}

	public OpentachLoginDialog(Application mainApplication, Hashtable parameters, EntityReferenceLocator locator) {
		super(mainApplication, parameters, locator);
	}

	@Override
	protected TextDataField createLogin(Hashtable parameters) {
		Hashtable<String, Object> p = DefaultXMLParametersManager.getParameters("LoginTextDataField");
		p.put("attr", "User_");
		p.put("size", "15");
		p.put("labelfont", "Arial-BOLD-14");
		p.put("labelfontcolor", this.getFontLabelForeground());
		return new TextDataField(p);
	}

	@Override
	protected PasswordDataField createPassword(Hashtable parameters) {
		Hashtable<String, Object> p2 = DefaultXMLParametersManager.getParameters("LoginPasswordDataField");
		p2.put("attr", "Password");
		p2.put("size", "15");
		p2.put("labelfont", "Arial-BOLD-14");
		p2.put("labelfontcolor", this.getFontLabelForeground());
		if (parameters.containsKey("encrypt")) {
			boolean encrypt = ApplicationManager.parseStringValue((String) parameters.get("encrypt"));
			if (encrypt) {
				p2.put("encrypt", "yes");
			}
		}
		return new PasswordDataField(p2);
	}

	@Override
	protected CheckDataField createRememberLogin(Hashtable parameters) {
		Hashtable<String, Object> p = DefaultXMLParametersManager.getParameters("LoginCheckDataField");
		p.put("attr", "RememberLogin");
		p.put("fontsize", "10");
		p.put("labelposition", "right");
		p.put("labelfontcolor", this.getFontLabelForeground());
		return new CheckDataField(p);
	}

	@Override
	protected CheckDataField createRememberPassword(Hashtable parameters) {
		Hashtable<String, Object> p = DefaultXMLParametersManager.getParameters("LoginCheckDataField");
		p.put("attr", "RememberPassword");
		p.put("fontsize", "10");
		p.put("labelposition", "right");
		p.put("labelfontcolor", this.getFontLabelForeground());
		return new CheckDataField(p);
	}

	private Object getFontLabelForeground() {
		return "white";
	}

	@Override
	protected Label createServerLabel() {
		Hashtable<String, Object> pet = new Hashtable<String, Object>();
		pet.put("attr", "et");
		pet.put("text", "mainapplication.connect_to");
		pet.put("fontsize", "10");
		pet.put("fontcolor", "white");
		return new Label(pet);
	}

	@Override
	protected JLabel createStatusLabel(Hashtable parameters) {
		JLabel label = new JLabel("", 0) {

			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				try {
					FontMetrics fontMetrics = this.getFontMetrics(this.getFont().deriveFont(0));
					return new Dimension(d.width, fontMetrics.getHeight());
				} catch (Exception e) {
					e.printStackTrace();
					return d;
				}
			}
		};
		Font font = Font.decode("Arial-BOLD-14");
		label.setFont(font);
		try {
			this.statusBarForeground = ColorConstants.parseColor("#cc3333");
			label.setForeground(this.statusBarForeground);
		} catch (Exception e) {
			e.printStackTrace();
		}
		label.setHorizontalTextPosition(4);
		return label;
	}

	@Override
	protected Button createAcceptButton(Hashtable parameters) {
		Hashtable<String, Object> p = DefaultXMLParametersManager.getParameters("LoginButton");
		p.put("key", "application.accept");
		p.put("text", "application.accept");
		Button b = new LoginButton(p);
		return b;
	}

	@Override
	protected Button createCancelButton(Hashtable parameters) {
		Hashtable<String, Object> p = DefaultXMLParametersManager.getParameters("LoginButton");
		p.put("key", "application.cancel");
		p.put("text", "application.cancel");
		Button b = new LoginButton(p);
		return b;
	}

	@Override
	protected void changePassword() {

	}

}
