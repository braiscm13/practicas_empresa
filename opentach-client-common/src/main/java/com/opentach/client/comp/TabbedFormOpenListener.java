package com.opentach.client.comp;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.utilmize.client.gui.UTabbedDetailForm;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class TabbedFormOpenListener extends AbstractActionListenerButton {

	private static final Logger	logger	= LoggerFactory.getLogger(TabbedFormOpenListener.class);

	public TabbedFormOpenListener() throws Exception {
		super();
	}

	public TabbedFormOpenListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public TabbedFormOpenListener(Hashtable params) throws Exception {
		super(params);
	}

	public TabbedFormOpenListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO in uswingworker?
		final Window win = SwingUtilities.getWindowAncestor(this.getButton());
		if (!(win instanceof JFrame)) {
			TabbedFormOpenListener.logger.error("not in a jframe");
			return;
		}
		final JFrame frame = (JFrame) win;
		final Container contentPane = frame.getContentPane();
		if (contentPane instanceof TabbedFormPane) {
			this.addTab((TabbedFormPane) contentPane);
		} else {
			final TabbedFormPane tabbedPane = this.addTabbedPane(frame);
			this.addTab(tabbedPane);
		}
	}

	private void addTab(TabbedFormPane contentPane) {
		final Form form = this.getForm();
		final IFormManager formManager = form.getFormManager();
		final Form formCopy = formManager.getFormCopy(form.getArchiveName());
		final UTabbedDetailForm tabbedDetailForm = new UTabbedDetailForm(formCopy, new Hashtable<>(), new Vector<>(), null, new Hashtable<>(), new Hashtable<>());
		formCopy.getInteractionManager().setInitialState();
		contentPane.addTab(this.computeTabName(contentPane), tabbedDetailForm);
	}

	private TabbedFormPane addTabbedPane(JFrame frame) {
		final Container contentPane = frame.getContentPane();
		final TabbedFormPane tabbedPane = new TabbedFormPane();
		frame.setContentPane(tabbedPane);
		tabbedPane.addTab(this.computeTabName(tabbedPane), contentPane);
		return tabbedPane;
	}

	private String computeTabName(TabbedFormPane tabbedPane) {
		final int count = tabbedPane.getTabCount();
		final String prefix = ApplicationManager.getTranslation("TabTitle");
		if (count == 0) {
			return prefix + " " + "1";
		}
		int maxIdx = 1;
		for (int i = 0; i < count; i++) {
			final String titleAt = tabbedPane.getTitleAt(i);
			final Integer value = Integer.valueOf(titleAt.substring(prefix.length()+1));
			if (maxIdx<value.intValue()) {
				maxIdx = value.intValue();
			}
		}
		maxIdx++;
		return prefix + " " + maxIdx;
	}

}
