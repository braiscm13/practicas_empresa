package com.opentach.client.comp;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ApplicationManager.CancelOperationDialog;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.images.ImageManager;

public class ExtendedCancelOperationDialog extends CancelOperationDialog {

	public ExtendedCancelOperationDialog(Dialog arg0, OperationThread arg1) {
		super(arg0, arg1);
		this.init();
	}

	public ExtendedCancelOperationDialog(Frame frame, OperationThread operationthread) {
		super(frame, operationthread);
		// this.init();
	}

	private void init() {

		this.getContentPane().setLayout(new GridBagLayout());
		this.cancelButton.setText(ApplicationManager.getTranslation("application.cancel"));
		this.cancelButton.setMargin(new Insets(2, 2, 0, 2));
		this.cancelButton.setIcon(ImageManager.getIcon(ImageManager.CANCEL));
		this.cancelButton.setOpaque(false);
		this.progressBar.setForeground(Color.yellow);
		this.getContentPane().add(this.progressBar, new GridBagConstraints(1, 1, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(1, 1, 20, 5), 0, 0));
		this.getContentPane().add(this.cancelButton, new GridBagConstraints(1, 2, 1, 1, 1.0D, 0.0D, 10, 0, new Insets(1, 2, 5, 5), 0, 0));

	}

}
