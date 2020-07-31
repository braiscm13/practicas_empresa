package com.opentach.model.comm.vu;

import java.io.File;
import java.util.Vector;

import com.ontimize.gui.OperationThread;

public class DownloadThread extends OperationThread {

	File		f	= null;
	Vector		v1	= null;
	Vector		v2	= null;
	TachoRead	tr	= null;

	public void initValues(File f, Vector v1, Vector v2, TachoRead tr) {
		this.f = f;
		this.v1 = v1;
		this.v2 = v2;
		this.tr = tr;
	}

	@Override
	public void run() {
		this.hasStarted = true;
		this.tr.readToFile(this.f, this.v1, this.v2);
		this.hasFinished = true;
	}

	@Override
	public void cancel() {
		super.cancel();
		// tr.cerrarSesion();
	}

}
