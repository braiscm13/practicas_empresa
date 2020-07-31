package com.opentach.client.comp;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.SwingUtilities;

import com.ontimize.gui.OperationThread;

public class ExtendedApplicationManager {
	public static OperationThread proccessOperation(Window window, OperationThread operationthread, int i) {
		if (window instanceof Dialog) {
			return ExtendedApplicationManager.proccessOperation((Dialog) window, operationthread, i);
		}
		return ExtendedApplicationManager.proccessOperation((Frame) window, operationthread, i);

	}
	public static OperationThread proccessOperation(Dialog dialog, OperationThread operationthread, int i) {
		if (!SwingUtilities.isEventDispatchThread()) {
			System.out.println("ApplicationManager. WARNING: The thread that invokes proccessOperation() is not an EventDispatchThread instance");
		}
		long l = System.currentTimeMillis();
		operationthread.setPriority(10);
		operationthread.start();
		Thread.yield();
		ExtendedCancelOperationDialog canceloperationdialog = null;
		while (!operationthread.isCancelled() && (!operationthread.hasStarted() || operationthread.isAlive())) {
			if (((System.currentTimeMillis() - l) > i) && (canceloperationdialog == null)) {
				canceloperationdialog = new ExtendedCancelOperationDialog(dialog, operationthread);
				canceloperationdialog.setBackground(Color.red);
				canceloperationdialog.setCursor(Cursor.getPredefinedCursor(3));

				canceloperationdialog.setVisible(true);
			}
			try {
				Thread.sleep(100L);
			} catch (Exception exception) {
			}
		}
		if (canceloperationdialog != null) {
			canceloperationdialog.dispose();
		}
		return operationthread;
	}

	public static OperationThread proccessOperation(Frame frame, OperationThread operationthread, int i) {
		if (!SwingUtilities.isEventDispatchThread()) {
			System.out.println("ApplicationManager. WARNING: The thread that invokes proccessOperation() is not an EventDispatchThread instance");
		}
		long l = System.currentTimeMillis();
		operationthread.setPriority(10);
		operationthread.start();
		Thread.yield();
		ExtendedCancelOperationDialog canceloperationdialog = null;
		while (!operationthread.isCancelled() && (!operationthread.hasStarted() || operationthread.isAlive())) {
			if (((System.currentTimeMillis() - l) > i) && (canceloperationdialog == null)) {
				canceloperationdialog = new ExtendedCancelOperationDialog(frame, operationthread);
				canceloperationdialog.setCursor(Cursor.getPredefinedCursor(3));
				canceloperationdialog.setVisible(true);
			}
			try {
				Thread.sleep(100L);
			} catch (Exception exception) {
			}
		}
		if (canceloperationdialog != null) {
			canceloperationdialog.dispose();
		}
		return operationthread;
	}

}
