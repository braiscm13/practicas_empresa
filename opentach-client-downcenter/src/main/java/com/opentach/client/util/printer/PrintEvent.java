package com.opentach.client.util.printer;

import java.util.EventObject;
import java.util.List;

import com.opentach.client.util.TGDFileInfo;

public class PrintEvent extends EventObject {

	private final int				numCopies;
	private final String			dscr;
	private final List<TGDFileInfo>	lPrintInfo;

	public PrintEvent(Object source, int numCopies, String dscr, List<TGDFileInfo> lPrintInfo) {
		super(source);
		this.numCopies = numCopies;
		this.lPrintInfo = lPrintInfo;
		this.dscr = dscr;
	}

	public int getNumCopies() {
		return this.numCopies;
	}

	public List<TGDFileInfo> getLPrintInfo() {
		return this.lPrintInfo;
	}

	public String getDscr() {
		return this.dscr;
	}

	@Override
	public String toString() {
		return String.format("%s [numCopies=%d, dscr=%s, numFiles=%d]", this.getClass().getName(), this.numCopies, this.dscr,
				this.lPrintInfo == null ? 0 : this.lPrintInfo.size());
	}

}
