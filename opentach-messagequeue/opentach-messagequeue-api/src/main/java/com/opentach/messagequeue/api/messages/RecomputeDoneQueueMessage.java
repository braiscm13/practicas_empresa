package com.opentach.messagequeue.api.messages;

import java.util.Date;

/**
 * This message represents an event about labor recompute task was executed successfully for an employee of a company, dated on specific time
 * (execution time). Moreover contains newDirtyDate (because recompute could be executed only until a limit, not fully)
 */
public class RecomputeDoneQueueMessage extends RecomputeDirtyQueueMessage {

	protected Date		doneDate;

	public RecomputeDoneQueueMessage(String cif, String idConductor, Date newDirtyDate, Date doneDate) {
		super(cif, idConductor, newDirtyDate);
		this.doneDate = doneDate;
	}

	public Date getDoneDate() {
		return this.doneDate;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "(CIF:'" + this.getCif() + "', DRIVER:'" + this.getIdConductor() + "', EVENT_DATE:'" + this.getDirtyEventDate() + "', DONE_DATE:'" + this
				.getDoneDate() + "')";
	}
}