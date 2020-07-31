package com.opentach.model.scard.extractor;

import javax.smartcardio.ResponseAPDU;

import com.imatia.tacho.extractor.scard.IResponseAPDU;

public class SmartcardioResponseAPDUWrapper implements IResponseAPDU {
	private final ResponseAPDU	response;

	public SmartcardioResponseAPDUWrapper(ResponseAPDU response) {
		super();
		this.response = response;
	}

	@Override
	public byte getSW1() {
		return (byte) this.response.getSW1();
	}

	@Override
	public byte getSW2() {
		return (byte) this.response.getSW2();
	}

	@Override
	public byte[] getData() {
		return this.response.getData();
	}

	@Override
	public byte[] getAPDU() {
		return this.response.getBytes();
	}

}