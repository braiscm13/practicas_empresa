package com.opentach.common.report.util;

import java.io.Serializable;
import java.util.List;

public class JRReportDescriptor implements Serializable {

	private int							key;
	private String						name;
	private String						dscr;
	private List<JRReportDescriptor>	lReports;
	private String						url;
	private String						methodBefore;
	private String						methodAfter;
	private String						delegCol;

	private boolean						visible;

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getMethodBefore() {
		return this.methodBefore;
	}

	public void setMethodBefore(String methodBefore) {
		this.methodBefore = methodBefore;
	}

	public String getMethodAfter() {
		return this.methodAfter;
	}

	public void setMethodAfter(String methodAfter) {
		this.methodAfter = methodAfter;
	}

	public int getKey() {
		return this.key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDscr() {
		return this.dscr;
	}

	public void setDscr(String dscr) {
		this.dscr = dscr;
	}

	public List<JRReportDescriptor> getLReports() {
		return this.lReports;
	}

	public void setLReports(List<JRReportDescriptor> reports) {
		this.lReports = reports;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDelegCol() {
		return this.delegCol;
	}

	public void setDelegCol(String delegCol) {
		this.delegCol = delegCol;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
