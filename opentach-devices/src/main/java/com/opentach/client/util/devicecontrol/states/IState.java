package com.opentach.client.util.devicecontrol.states;

import java.util.EventObject;

public interface IState {

	// public IState execute(CardEvent e);
	//
	// public IState execute(USBEvent e);
	//
	// public IState execute(UploadEvent e);
	//
	// public IState execute(ActionEvent e);
	//
	// public IState execute(PrintEvent e);
	//
	// public IState execute(ReportEvent e);
	//
	// public IState execute(NetworkEvent e);
	//
	public IState execute(EventObject e);

	public void handle();

}