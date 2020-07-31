package com.opentach.common.company.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.opentach.common.company.naming.LicenseNaming;

/**
 * This beans manage all information about company licenses
 */
public class LicensesBean implements Serializable {

	private boolean	opentach;
	private Date	opentachFrom;
	private Date	opentachTo;
	private boolean	opentachDemo;
	private Date	opentachDemoFrom;
	private Date	opentachDemoTo;

	private boolean	tacholab;
	private Date	tacholabFrom;
	private Date	tacholabTo;
	private Boolean	tacholabDemo;
	private Date	tacholabDemoFrom;
	private Date	tacholabDemoTo;

	private boolean	tacholabPlus;
	private Date	tacholabPlusFrom;
	private Date	tacholabPlusTo;
	private boolean	tacholabPlusDemo;
	private Date	tacholabPlusDemoFrom;
	private Date	tacholabPlusDemoTo;

	private LicensesBean() {}

	public LicensesBean(Map<String, Object> info) {
		this.opentach = ParseUtilsExtended.getBoolean(info.get(LicenseNaming.LIC_OPENTACH), false);
		this.opentachFrom = (Date) info.get(LicenseNaming.LIC_OPENTACH_FROM);
		this.opentachTo = (Date) info.get(LicenseNaming.LIC_OPENTACH_TO);
		this.opentachDemo = ParseUtilsExtended.getBoolean(info.get(LicenseNaming.LIC_OPENTACH_DEMO), false);
		this.opentachDemoFrom = (Date) info.get(LicenseNaming.LIC_OPENTACH_DEMO_FROM);
		this.opentachDemoTo = (Date) info.get(LicenseNaming.LIC_OPENTACH_DEMO_TO);

		this.tacholab = ParseUtilsExtended.getBoolean(info.get(LicenseNaming.LIC_TACHOLAB), false);
		this.tacholabFrom = (Date) info.get(LicenseNaming.LIC_TACHOLAB_FROM);
		this.tacholabTo = (Date) info.get(LicenseNaming.LIC_TACHOLAB_TO);
		this.tacholabDemo = ParseUtilsExtended.getBoolean(info.get(LicenseNaming.LIC_TACHOLAB_DEMO), false);
		this.tacholabDemoFrom = (Date) info.get(LicenseNaming.LIC_TACHOLAB_DEMO_FROM);
		this.tacholabDemoTo = (Date) info.get(LicenseNaming.LIC_TACHOLAB_DEMO_TO);

		this.tacholabPlus = ParseUtilsExtended.getBoolean(info.get(LicenseNaming.LIC_TACHOLABPLUS), false);
		this.tacholabPlusFrom = (Date) info.get(LicenseNaming.LIC_TACHOLABPLUS_FROM);
		this.tacholabPlusTo = (Date) info.get(LicenseNaming.LIC_TACHOLABPLUS_TO);
		this.tacholabPlusDemo = ParseUtilsExtended.getBoolean(info.get(LicenseNaming.LIC_TACHOLABPLUS_DEMO), false);
		this.tacholabPlusDemoFrom = (Date) info.get(LicenseNaming.LIC_TACHOLABPLUS_DEMO_FROM);
		this.tacholabPlusDemoTo = (Date) info.get(LicenseNaming.LIC_TACHOLABPLUS_DEMO_TO);
	}

	public boolean hasOpentach() {
		return this.opentach;
	}

	public Date getOpentachFrom() {
		return this.opentachFrom;
	}

	public Date getOpentachTo() {
		return this.opentachTo;
	}

	public boolean hasOpentachDemo() {
		return this.opentachDemo;
	}

	public Date getOpentachDemoFrom() {
		return this.opentachDemoFrom;
	}

	public Date getOpentachDemoTo() {
		return this.opentachDemoTo;
	}

	public boolean hasTacholab() {
		return this.tacholab;
	}

	public Date getTacholabFrom() {
		return this.tacholabFrom;
	}

	public Date getTacholabTo() {
		return this.tacholabTo;
	}

	public boolean hasTacholabDemo() {
		return this.tacholabDemo;
	}

	public Date getTacholabDemoFrom() {
		return this.tacholabDemoFrom;
	}

	public Date getTacholabDemoTo() {
		return this.tacholabDemoTo;
	}

	public boolean hasTacholabPlus() {
		return this.tacholabPlus;
	}

	public Date getTacholabPlusFrom() {
		return this.tacholabPlusFrom;
	}

	public Date getTacholabPlusTo() {
		return this.tacholabPlusTo;
	}

	public boolean hasTacholabPlusDemo() {
		return this.tacholabPlusDemo;
	}

	public Date getTacholabPlusDemoFrom() {
		return this.tacholabPlusDemoFrom;
	}

	public Date getTacholabPlusDemoTo() {
		return this.tacholabPlusDemoTo;
	}
}
