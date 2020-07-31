package com.opentach.model.scard;

import java.io.File;
import java.util.EventObject;

import javax.smartcardio.ATR;

import com.opentach.model.scard.SmartCardMonitor.SmartCardType;

public class CardEvent extends EventObject {

	public static enum CardEventType {
		CARD_INSERTED, CARD_REMOVED, CARD_ERROR, CARD_DOWNLOAD_START, CARD_DOWNLOAD_END, CARD_DOWNLOAD_PROGRESS
	}

	private final CardEventType	type;
	private final String		message;
	private final File			file;
	private final SmartCardType	smartCardType;
	private final ATR			atr;
	private Object[]			msgParameters;
	private int					currentPart;
	private int					totalParts;

	public CardEvent(Object obj, CardEventType type, SmartCardType smartCardType, ATR atr, String msg) {
		this(obj, type, smartCardType, atr, msg, null);
	}

	public CardEvent(Object obj, CardEventType type, SmartCardType smartCardType, ATR atr, String msg, File file) {
		super(obj);
		this.type = type;
		this.message = msg;
		this.file = file;
		this.smartCardType = smartCardType;
		this.atr = atr;
	}

	public CardEvent(Object obj, CardEventType type, SmartCardType smartCardType, ATR atr, String msg, Object[] msgParameters, int currentPart,
			int totalParts) {
		this(obj, type, smartCardType, atr, msg);
		this.msgParameters = msgParameters;
		this.currentPart = currentPart;
		this.totalParts = totalParts;
	}

	public File getFile() {
		return this.file;
	}

	public CardEventType getType() {
		return this.type;
	}

	public String getMessage() {
		return this.message;
	}

	public ATR getAtr() {
		return this.atr;
	}

	public SmartCardType getSmartCardType() {
		return this.smartCardType;
	}

	public Object[] getMsgParameters() {
		return this.msgParameters;
	}

	public int getCurrentPart() {
		return this.currentPart;
	}

	public int getTotalParts() {
		return this.totalParts;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[source=" + this.source + " type=" + this.type + "]";
	}

}
