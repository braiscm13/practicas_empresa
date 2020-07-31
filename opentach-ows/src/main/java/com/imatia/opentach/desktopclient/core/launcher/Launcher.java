package com.imatia.opentach.desktopclient.core.launcher;


import com.imatia.utilwebstart.UpdateChecker;

public final class Launcher {
	private Launcher() {
		super();
	}

	public static void main(String[] args) {
		UpdateChecker.DEFAULT_NUM_THREADS = 4;
		com.imatia.utilwebstart.swing.SwingLauncher.main(args);
	}

}
