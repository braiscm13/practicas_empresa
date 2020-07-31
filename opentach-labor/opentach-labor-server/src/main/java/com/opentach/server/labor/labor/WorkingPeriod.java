package com.opentach.server.labor.labor;

import java.util.Date;

import com.ontimize.jee.common.tools.ObjectTools;

public class WorkingPeriod {
	private int		tpPeriodo;
	private Date	when;
	private String	numTrj;

	public WorkingPeriod() {}

	public WorkingPeriod(int tpPeriodo, Date when, String numTrj) {
		this.tpPeriodo = tpPeriodo;
		this.when = when;
		this.numTrj = numTrj;
	}

	public int getTpPeriodo() {
		return this.tpPeriodo;
	}

	public Date getWhen() {
		return this.when;
	}

	public String getNumTrj() {
		return this.numTrj;
	}

	public void setTpPeriodo(int tpPeriodo) {
		this.tpPeriodo = tpPeriodo;
	}

	public void setWhen(Date when) {
		this.when = when;
	}

	public void setNumTrj(String numTrj) {
		this.numTrj = numTrj;
	}

	public boolean isInsertion() {
		return ObjectTools.isIn(this.tpPeriodo, 0, 2, 5);
	}

	public boolean isExtraction() {
		return !this.isInsertion();
	}

	@Override
	public String toString() {
		return String.format("[%tB %<te,  %<tY  %<tT %<Tp %b]", this.when, this.isInsertion());
	}
}
