package com.opentach.client.util.screenreport;

import java.util.EventListener;

public interface ReportListener extends EventListener {

	public void reportAction(ReportEvent e);

}
