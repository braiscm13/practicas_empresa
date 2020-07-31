package com.opentach.model.scard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.model.scard.CardEvent.CardEventType;
import com.opentach.model.scard.SmartCardMonitor.SmartCardType;

public class DriverCardAutoDownloadListener implements CardListener {

	private static final Logger	logger	= LoggerFactory.getLogger(DriverCardAutoDownloadListener.class);
	private final SmartCardMonitor	smon;

	public DriverCardAutoDownloadListener(SmartCardMonitor smon) {
		super();
		this.smon = smon;
	}

	@Override
	public void cardStatusChange(CardEvent ce) {
		if (this.smon.isAutoDownload() && (ce.getType() == CardEventType.CARD_INSERTED)
				&& (ce.getSmartCardType() == SmartCardType.DRIVER)) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException error) {
						DriverCardAutoDownloadListener.logger.error(null, error);
					}
					DriverCardAutoDownloadListener.this.smon.extractDriverCardFiles(null);
				}
			};
			Thread thr = new Thread(runnable, this.getClass().getName());
			thr.start();
		}
	}

}