package com.opentach.client.remotevehicle.provider.opentach;

import java.util.Timer;
import java.util.TimerTask;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.xml.bind.DatatypeConverter;

import com.imatia.tacho.extractor.tacho.TachoException;
import com.ontimize.gui.ApplicationManager;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.model.scard.SmartCardMonitor;

public class RemoteDownloadAuthenticationSession {

	private static final long TIMEOUT = 60000;

	public enum FileSelected {
		ICC, IC, OTHER
	}

	// ***Seleccionar fichero/ EF bajo el DF actual/sin respuesta/ICC***
	static byte[]			selectIcc	= DatatypeConverter.parseHexBinary("00A4020C020002");
	// ***Seleccionar fichero/ EF bajo el DF actual/sin respuesta/IC***
	static byte[]			selectIc	= DatatypeConverter.parseHexBinary("00A4020C020005");
	static byte[]			ok			= DatatypeConverter.parseHexBinary("9000");

	protected Card			card;
	protected boolean		sessionActive;
	protected Timer			timerTimeout;
	protected TimerTask		currentTask;
	protected FileSelected	currentFileSelected;

	public RemoteDownloadAuthenticationSession() {
		super();
		this.card = null;
		this.sessionActive = false;
		this.timerTimeout = new Timer();
		this.currentTask = null;
	}

	public ResponseAPDU sendCommandAPDU(CommandAPDU apdu) throws CardException, TachoException {
		if (!this.sessionActive) {
			throw new TachoException("NO_ACTIVE_SESSION");
		}

		this.stopTimeoutTimer();

		if (apdu.getBytes()[1] == (byte) 0xA4) {
			// Si es una instrucción de selección de fichero
			if (this.match(apdu.getBytes(), 0, RemoteDownloadAuthenticationSession.selectIc, 0, RemoteDownloadAuthenticationSession.selectIc.length)) {
				this.currentFileSelected = FileSelected.IC;
				return new ResponseAPDU(RemoteDownloadAuthenticationSession.ok);
			} else if (this.match(apdu.getBytes(), 0, RemoteDownloadAuthenticationSession.selectIcc, 0, RemoteDownloadAuthenticationSession.selectIcc.length)) {
				this.currentFileSelected = FileSelected.ICC;
				return new ResponseAPDU(RemoteDownloadAuthenticationSession.ok);
			} else {
				this.currentFileSelected = FileSelected.OTHER;
			}
		} else if (apdu.getBytes()[1] == (byte) 0xB0) {
			// Instrucción de lectura
			switch (this.currentFileSelected) {
				case IC:
					return this.extractData(this.getSmartCardMonitor().getIc(), apdu);
				case ICC:
					return this.extractData(this.getSmartCardMonitor().getIcc(), apdu);

				default:
					break;
			}
		}
		CardChannel channel = this.card.getBasicChannel();
		ResponseAPDU res = channel.transmit(apdu);
		this.startTimeoutTimer();
		return res;
	}

	public void begin() throws CardException, TachoException {
		if (this.sessionActive) {
			throw new TachoException("Session in use");
		}
		this.end();
		this.card = this.getSmartCardMonitor().getCurrentCard();
		this.sessionActive = true;
		this.timerTimeout = new Timer();
		this.startTimeoutTimer();
	}

	public void end() throws CardException {
		this.sessionActive = false;
		// this.card.disconnect(true);
		this.timerTimeout.cancel();
	}

	protected void stopTimeoutTimer() {
		this.currentTask.cancel();
	}

	protected void startTimeoutTimer() {
		this.currentTask = new TimerTaskTimeout();
		this.timerTimeout.schedule(this.currentTask, RemoteDownloadAuthenticationSession.TIMEOUT);
	}

	protected class TimerTaskTimeout extends TimerTask {
		public TimerTaskTimeout() {
			super();
		}

		@Override
		public void run() {
			try {
				RemoteDownloadAuthenticationSession.this.end();
			} catch (CardException e) {
				e.printStackTrace();
			}
		}
	}

	protected boolean match(byte[] x, int offsetX, byte[] y, int offsetY, int length) {
		for (int i = 0; i < length; i++) {
			if (x[offsetX + i] != y[offsetY + i]) {
				return false;
			}
		}
		return true;
	}

	private ResponseAPDU extractData(byte[] data, CommandAPDU apdu) {
		// en esta primera versión vamos a suponer que siempre viene 0 de offset
		int offset = 0;

		int nbytes = apdu.getBytes()[4];
		byte[] res = new byte[nbytes + 2];
		System.arraycopy(data, offset, res, 0, nbytes);
		res[nbytes] = (byte) 0x90;
		res[nbytes + 1] = (byte) 0x00;
		return new ResponseAPDU(res);
	}

	public boolean isCompanyCardInserted() {
		return this.getSmartCardMonitor().isCompanyCardInserted();
	}

	public byte[] getAtr() {
		return this.getSmartCardMonitor().getCurrentCard().getATR().getBytes();
	}

	private SmartCardMonitor getSmartCardMonitor() {
		return ((AbstractOpentachClientLocator) ApplicationManager.getApplication().getReferenceLocator()).getLocalService(SmartCardMonitor.class);
	}

}
