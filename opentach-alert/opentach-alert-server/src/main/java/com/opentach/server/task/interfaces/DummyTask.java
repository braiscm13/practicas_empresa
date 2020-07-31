package com.opentach.server.task.interfaces;

import java.util.Date;

import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.ThreadTools;

public class DummyTask implements ITask {

	public DummyTask() {
		// Nothing
	}

	@Override
	public boolean isValidToExecute(Date dispatchDate) {
		return (DateTools.minutesFromMidnigth(dispatchDate) % 2) == 0;// Minutos pares
	}

	@Override
	public void execute() {
		ThreadTools.sleep(3000);
	}

	@Override
	public String toString() {
		return "TareaMinutosPares";
	}
}
