package com.opentach.client.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.operationmonitor.OperationMonitor;

public final class HideThreadMonitor {

	private static final int											MAXTIME	= 8000;


	private static final Logger											logger	= LoggerFactory.getLogger(HideThreadMonitor.class);
	private final Map<OperationMonitor.ExtOpThreadsMonitor, Integer>	sMon;
	private final Thread												thMon;

	public HideThreadMonitor() {
		this.sMon = new HashMap<OperationMonitor.ExtOpThreadsMonitor, Integer>();
		this.thMon = new Thread(this.getClass().getName()) {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
					synchronized (HideThreadMonitor.this.sMon) {
						Set<OperationMonitor.ExtOpThreadsMonitor> sk = HideThreadMonitor.this.sMon.keySet();
						Iterator<OperationMonitor.ExtOpThreadsMonitor> it = sk.iterator();
						try {
							while (it.hasNext()) {
								final OperationMonitor.ExtOpThreadsMonitor mon = it.next();
								Integer time = HideThreadMonitor.this.sMon.get(mon);
								int iTime = time.intValue();
								iTime -= 1000;
								if (iTime <= 0) {
									if (mon.getAliveThreadsCount() == 0) {
										SwingUtilities.invokeLater(new Runnable() {
											@Override
											public void run() {
												HideThreadMonitor.logger.info("Hiding DownloadMonitor...");
												//												mon.setVisible(false);
											}
										});
										HideThreadMonitor.this.sMon.remove(mon);
									}
								} else {
									HideThreadMonitor.this.sMon.put(mon, Integer.valueOf(iTime));
								}
							}
						} catch (Exception e) {
							HideThreadMonitor.logger.error(null, e);
						}
					}
				}
			}
		};
		this.thMon.setPriority(Thread.MIN_PRIORITY);
		this.thMon.start();
	}

	public void add(OperationMonitor.ExtOpThreadsMonitor mon) {
		synchronized (this.sMon) {
			this.sMon.put(mon, HideThreadMonitor.MAXTIME);
		}
	}

}
