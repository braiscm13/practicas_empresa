package com.opentach.client.util;

import com.ontimize.gui.Form;
import com.ontimize.gui.field.document.CIFDocument;
import com.ontimize.gui.field.document.NIFDocument;
import com.opentach.common.tacho.data.Conductor;

public final class DriverUtil {

	private DriverUtil() {}

	public static final boolean checkValidCIFNIF(Form f, String val, boolean msg) {
		boolean ok = false;
		if (val != null) {
			ok = Conductor.UNKNOWN_DRIVER.equals(val) || NIFDocument.isNIEWellFormed(val) || NIFDocument.isNIFWellFormed(val)
					|| CIFDocument.isCIFWellFormed(val) || BIDocument.esBICorrecto(val);
		}
		if (msg && !ok) {
			f.message("M_NIF_CIF_INCORRECTO", Form.ERROR_MESSAGE);
		}
		return ok;
	}

}
