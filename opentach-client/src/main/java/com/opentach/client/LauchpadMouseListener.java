package com.opentach.client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.ontimize.gui.InteractionManagerModeEvent;
import com.opentach.client.comp.JMenuAppMode;
import com.utilmize.client.gui.IAbstractListener;

public class LauchpadMouseListener extends MouseAdapter implements IAbstractListener {

	protected JPopupMenu	popupmenu;
	protected JMenuAppMode	jMenuAppMode;

	public LauchpadMouseListener(Hashtable<?, ?> params) {
		super();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		if (SwingUtilities.isRightMouseButton(e)) {
			this.ensurePopup();
			this.addaptPopup();
			this.showPopup(e);
		}
	}

	protected void ensurePopup() {
		if (this.popupmenu == null) {
			this.popupmenu = new JPopupMenu();

			this.jMenuAppMode = new JMenuAppMode();
			this.popupmenu.add(this.jMenuAppMode);
		}
	}

	private void addaptPopup() {
		this.jMenuAppMode.addaptPopupMenu();
	}

	protected void showPopup(MouseEvent e) {
		if (this.popupmenu != null) {
			this.popupmenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent e) {
		// Nothing
	}

	@Override
	public Object getGlobalId() {
		// Nothing
		return null;
	}

	@Override
	public void setGlobalId(Object globalId) {
		// Nothing
	}

	@Override
	public void parentFormSetted() {
		// Nothing
	}
}
