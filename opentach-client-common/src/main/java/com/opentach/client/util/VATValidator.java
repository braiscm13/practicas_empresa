package com.opentach.client.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ontimize.gui.field.document.CIFDocument;
import com.ontimize.gui.field.document.NIFDocument;

public class VATValidator {
	private static Pattern[]	pattern	= new Pattern[] { Pattern.compile("(AT)U(\\d{8})"), // **
																							// Austria
		Pattern.compile("(BE)(\\d{9,10})"), // ** Belgium
			Pattern.compile("(BG)(\\d{9,10})"), // Bulgaria
			Pattern.compile("(CY)(\\d{8}[A-Z])"), // ** Cyprus
			Pattern.compile("(CZ)(\\d{8,10})(\\d{3})?"), // ** Czech Republic
		Pattern.compile("(DE)(\\d{9})"), // ** Germany
			Pattern.compile("(DK)((\\d{8}))"), // ** Denmark
			Pattern.compile("(EE)(\\d{9})"), // ** Estonia
			Pattern.compile("(EL)(\\d{8,9})"), // ** Greece
			Pattern.compile("(ES)([A-Z]\\d{8})"), // ** Spain (1)
		Pattern.compile("(ES)(\\d{8}[A-Z])"), // Spain (2)
		Pattern.compile("(ES)([A-Z]\\d{7}[A-Z])"), // ** Spain (3)
		Pattern.compile("(EU)(\\d{9})"), // ** EU-type
			Pattern.compile("(FI)(\\d{8})"), // ** Finland
			Pattern.compile("(FR)(\\d{11})"), // ** France (1)
		Pattern.compile("(FR)[(A-H)|(J-N)|(P-Z)]\\d{10}"), // France (2)
		Pattern.compile("(FR)\\d[(A-H)|(J-N)|(P-Z)]\\d{9}"), // France (3)
		Pattern.compile("(FR)[(A-H)|(J-N)|(P-Z)]{2}\\d{9}"), // France (4)
		Pattern.compile("(GB)(\\d{9})"), // ** UK (standard)
		Pattern.compile("(GB)(\\d{10})"), // ** UK (Commercial)
		Pattern.compile("(GB)(\\d{12})"), // UK (IOM standard)
		Pattern.compile("(GB)(\\d{13})"), // UK (IOM commercial)
		Pattern.compile("(GB)(GD\\d{3})"), // ** UK (Government)
		Pattern.compile("(GB)(HA\\d{3})"), // ** UK (Health authority)
		Pattern.compile("(GR)(\\d{8,9})"), // ** Greece
			Pattern.compile("(HU)(\\d{8})"), // ** Hungary
			Pattern.compile("(IE)(\\d{7}[A-W])"), // ** Ireland (1)
		Pattern.compile("(IE)([7-9][A-Z\\*\\+)]\\d{5}[A-W])"), // ** Ireland
																	// (2)
		Pattern.compile("(IT)(\\d{11})"), // ** Italy
			Pattern.compile("(LV)(\\d{11})"), // ** Latvia
			Pattern.compile("(LT)(\\d{9}|\\d{12})"), // ** Lithunia
		Pattern.compile("(LU)(\\d{8})"), // ** Luxembourg
			Pattern.compile("(MT)(\\d{8})"), // ** Malta
		Pattern.compile("(NL)(\\d{9})B\\d{2}"), // ** Netherlands
		Pattern.compile("(PL)(\\d{10})"), // ** Poland
		Pattern.compile("(PT)(\\d{9})"), // ** Portugal
		Pattern.compile("(RO)(\\d{2,10})"), // ** Romania
		Pattern.compile("(SI)(\\d{8})"), // ** Slovenia
		Pattern.compile("(SK)(\\d{9}|\\d{10})"), // Slovakia Republic
		Pattern.compile("(SE)(\\d{10}\\d[1-4])"), // ** Sweden
										};

	public boolean validate(String vat) {
		boolean valid = false;
		for (int i = 0; i < VATValidator.pattern.length; i++) {
			Matcher m = VATValidator.pattern[i].matcher(vat);
			if (m.matches()) {
				String cCode = m.group(1);
				String cNumber = m.group(2);
				if ((cCode == null) || cCode.equals("ES")) {
					valid = NIFDocument.isNIEWellFormed(cNumber) | NIFDocument.isNIFWellFormed(cNumber) | CIFDocument.isCIFWellFormed(cNumber);
					break;
				} else if (cCode.equals("PT")) {
					valid = BIDocument.esBICorrecto(cNumber);
					break;
				} else {
					valid = true;
					break;
				}
			}
		}
		return valid;
	}
}
