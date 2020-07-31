package com.opentach.client.util;

import com.ontimize.gui.field.document.NIFDocument;

public class BIDocument extends NIFDocument {

	// S=9*N(1)+8*N(2)+7*N(3)+6*N(4)+5*N(5)+4*N(6)+3*N(7)+2*N(8)
	// C(1)=11-S%11; si C(1)=10, C(1)=0; si C(1)=11, C(1)=0
	public static boolean esBICorrecto(String bi) {
		boolean result = false;
		if ((bi == null) || (bi.length() != 9)) {
			return result;
		}
		try {
			if ((bi == null) || ((Integer.parseInt(bi.charAt(0) + "")) <= 0)) {
				return result;
			}
			int valor = 0;
			int l_nbi = bi.length();
			for (int i = 0; i < (l_nbi - 1); i++) {
				valor += Integer.parseInt(bi.charAt(i) + "") * (l_nbi - i);
			}
			int modulo11 = 11 - (valor % 11);
			if ((modulo11 == 11) || (modulo11 == 10)) {
				modulo11 = 0;
			}
			if (modulo11 == Integer.parseInt(bi.charAt(8) + "")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
