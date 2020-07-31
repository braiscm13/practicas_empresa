package com.opentach.client.comp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.client.util.LocalPreferencesManager;
import com.utilmize.tools.VersionUtils;

public class LicenseAgreementDialog extends EJDialog {

	private static final Logger logger = LoggerFactory.getLogger(LicenseAgreementDialog.class);

	public LicenseAgreementDialog(Window owner, String license) throws Exception {
		super(owner);
		this.buildDialog(license);
	}

	private void buildDialog(String license) throws Exception {
		this.setModal(true);
		this.setTitle(ApplicationManager.getTranslation("license.title"));
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.getContentPane().add(this.buildBottomPanel(), BorderLayout.SOUTH);
		this.getContentPane().add(this.buildCentralPanel(this.queryLicenseText(license)), BorderLayout.CENTER);
		this.setSize(730, 400);
		this.setLocationRelativeTo(null);
	}

	private String queryLicenseText(String license) throws Exception {
		EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
		Entity entityReference = locator.getEntityReference("EAgreement");
		Hashtable<String, Object> kv = new Hashtable<>();
		MapTools.safePut(kv, "AGR_NAME", license);
		EntityResult res = entityReference.query(kv, EntityResultTools.attributes("AGR_VALUE"), locator.getSessionId());
		CheckingTools.checkValidEntityResult(res, "E_AGREEMENT_NOT_FOUND", true, true, new Object[] {});
		return (String) res.getRecordValues(0).get("AGR_VALUE");
	}

	@Override
	public void pack() {}

	private Component buildCentralPanel(String licenseText) {
		JPanel panel = new JPanel(new BorderLayout(0, 0));
		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		textPane.setText(licenseText);
		textPane.setCaretPosition(0);
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);
		return panel;
	}

	private Component buildBottomPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		JPanel panelButtons = new JPanel(new GridLayout(1, 2));

		JCheckBox agreeCheckbox = new JCheckBox(ApplicationManager.getTranslation("iagree"));
		JButton cancelButton = new JButton(ApplicationManager.getTranslation("cancel"));
		JButton agreeButton = new JButton(ApplicationManager.getTranslation("ok"));
		panelButtons.add(cancelButton);
		panelButtons.add(agreeButton);
		panel.add(agreeCheckbox, new GridBagConstraints(0, 0, 1, 1, 1.0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(panelButtons, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		agreeButton.setEnabled(false);
		agreeCheckbox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				agreeButton.setEnabled(agreeCheckbox.isSelected());

			}
		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(-1);
			}
		});
		agreeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String version = VersionUtils.getVersion(LicenseAgreementDialog.class);
				LocalPreferencesManager.getInstance().setPreference(LocalPreferencesManager.PROP_AGREEMENT_READED + version, "YES");
				LicenseAgreementDialog.this.setVisible(false);
				LicenseAgreementDialog.this.dispose();
			}
		});
		return panel;
	}

	public static boolean needToShow() {
		String version = VersionUtils.getVersion(LicenseAgreementDialog.class);
		String preference = LocalPreferencesManager.getInstance().getPreference(LocalPreferencesManager.PROP_AGREEMENT_READED + version);
		return !"YES".equals(preference);
	}
}
