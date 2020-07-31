package com.opentach.server.remotevehicle.provider.opentach;

import java.util.EventObject;

public class IncommingDataEvent extends EventObject {

	protected final String	cif;
	protected final byte[]	data;

	public IncommingDataEvent(Object source, String empresa, byte[] data) {
		super(source);
		this.cif = empresa;
		this.data = data;
	}

	public String getCIF() {
		return this.cif;
	}

	public byte[] getData() {
		return this.data;
	}

}
