package com.opentach.server.task.interfaces;

import java.util.Date;

import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.ThreadTools;

public class DummyTask2 implements ITask {

	public DummyTask2() {
		// Nothing
	}

	@Override
	public boolean isValidToExecute(Date dispatchDate) {
		return (DateTools.minutesFromMidnigth(dispatchDate) % 5) == 0; // Cada 5 minutos
	}

	@Override
	public void execute() {
		ThreadTools.sleep(1000);
	}

	@Override
	public String toString() {
		return "TareaCada5Minutos";
	}

}
