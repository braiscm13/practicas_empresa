package com.opentach.model.scard.extractor;

import java.io.IOException;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import com.imatia.tacho.extractor.ICommand;
import com.imatia.tacho.extractor.scard.ICommandAPDU;
import com.imatia.tacho.extractor.scard.IResponseAPDU;
import com.imatia.tacho.extractor.scard.ISmartCardAPDUConnector;
import com.imatia.tacho.extractor.tacho.ConnectorHangException;
import com.imatia.tacho.extractor.tacho.TachoException;

public class SmartcardioAPDUConnector implements ISmartCardAPDUConnector {
	private final Card	card;

	public SmartcardioAPDUConnector(Card card) {
		this.card = card;
	}

	@Override
	public void setWriteTimeOut(long timeout) throws IOException {}

	@Override
	public void setReadTimeOut(long timeout) throws IOException {}

	@Override
	public byte[] sendCommand(ICommand command) throws IOException, TachoException, ConnectorHangException {
		IResponseAPDU res = this.sendAPDU((ICommandAPDU) command);
		return res.getAPDU();
	}

	@Override
	public byte[] sendAdministrationCommand(byte[] admCommand, boolean waitResponse) throws IOException, TachoException, ConnectorHangException {
		return null;
	}

	@Override
	public void open() throws IOException {}

	@Override
	public void close() throws IOException {}

	@Override
	public IResponseAPDU sendAPDU(ICommandAPDU command) throws IOException, TachoException, ConnectorHangException {
		CommandAPDU request = null;
		if ((command.getLe() == 0) && (command.getLcdata() == null)) {
			request = new CommandAPDU(command.getCla(), command.getIns(), command.getP1(), command.getP2());
		} else if ((command.getLe() == 0) && (command.getLcdata() != null)) {
			request = new CommandAPDU(command.getCla(), command.getIns(), command.getP1(), command.getP2(), command.getLcdata());
		} else if ((command.getLe() > 0) && (command.getLcdata() == null)) {
			request = new CommandAPDU(command.getCla(), command.getIns(), command.getP1(), command.getP2(), command.getLe());
		} else if ((command.getLe() > 0) && (command.getLcdata() != null)) {
			request = new CommandAPDU(command.getCla(), command.getIns(), command.getP1(), command.getP2(), command.getLcdata(), command.getLe());
		} else {
			throw new TachoException("invalid command apdu");
		}

		try {
			ResponseAPDU response = this.card.getBasicChannel().transmit(request);
			return new SmartcardioResponseAPDUWrapper(response);
		} catch (CardException e) {
			throw new TachoException(e.getMessage());
		}
	}

	@Override
	public void changeBaudrate(byte commandBps) throws IOException, TachoException {

	}

	@Override
	public boolean canNegociateBaudRate() {
		return false;
	}
}