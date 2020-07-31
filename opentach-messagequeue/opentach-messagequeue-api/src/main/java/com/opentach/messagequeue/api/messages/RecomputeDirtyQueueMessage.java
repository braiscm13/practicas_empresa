package com.opentach.messagequeue.api.messages;

import java.util.Date;

import com.opentach.messagequeue.api.IMessageQueueMessage;

/**
 * This message represents an event about some fact that affects to labor recompute task of an employee of a company, dated on specific time (latest)
 */
public class RecomputeDirtyQueueMessage implements IMessageQueueMessage {

	protected String	cif;

	protected String	idConductor;

	protected Date		dirtyEventDate;

	public RecomputeDirtyQueueMessage(String cif, String idConductor, Date dirtyEventDate) {
		super();
		this.cif = cif;
		this.idConductor = idConductor;
		this.dirtyEventDate = dirtyEventDate;
	}

	public String getCif() {
		return this.cif;
	}

	public String getIdConductor() {
		return this.idConductor;
	}

	public Date getDirtyEventDate() {
		return this.dirtyEventDate;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "(CIF:'" + this.getCif() + "', DRIVER:'" + this.getIdConductor() + "', EVENT_DATE:'" + this.getDirtyEventDate() + "')";
	}

}
