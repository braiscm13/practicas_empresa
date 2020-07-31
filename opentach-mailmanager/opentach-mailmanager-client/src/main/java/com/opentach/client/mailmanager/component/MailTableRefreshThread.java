package com.opentach.client.mailmanager.component;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.table.RefreshTableEvent;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.utilmize.client.gui.field.table.UTableRefreshThread;

public class MailTableRefreshThread extends UTableRefreshThread {

	private static final Logger logger = LoggerFactory.getLogger(MailTableRefreshThread.class);

	/**
	 * Creates the thread for the corresponding table.
	 *
	 * @param table
	 *            the table
	 */
	public MailTableRefreshThread(MailTable table) {
		super(table);
	}

	private MailTable getTable() {
		return (MailTable) this.table;
	}

	/**
	 * Executes the table query.
	 */
	@Override
	public void run() {
		boolean ok = true;
		try {
			this.getTable().fireRefreshBackgroundStart();
			if (this.delay > 0) {
				Thread.sleep(this.delay);
			}
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					MailTableRefreshThread.this.getTable().showWaitPanel();
					MailTableRefreshThread.this.getTable().repaint();
				}
			});

			this.getTable().requeryMails();

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					MailTableRefreshThread.this.getTable().hideWaitPanel();
				}
			});
		} catch (final Exception error) {
			ok = false;
			MailTableRefreshThread.logger.error(null, error);
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					Throwable causeEx = MessageManager.getMessageManager().getCauseException(error);
					MailTableRefreshThread.this.getTable().showInformationPanel((causeEx == null ? error : causeEx).getMessage());
				}
			});
		} finally {
			if (!this.stop) {
				this.stop = true;
				final boolean okFinal = ok;
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						synchronized (MailTableRefreshThread.this.getTable().getJTable()) {
							MailTableRefreshThread.this.getTable().fireRefreshBackgroundFinished();
							/*
							 * sin comentarios... ni copiar, ademas encima de usar enumerados meten estaticas...
							 */
							MailTableRefreshThread.this.getTable().fireRefreshTableEvent(
									new RefreshTableEvent(MailTableRefreshThread.this.table, okFinal ? RefreshTableEvent.OK : RefreshTableEvent.ERROR));
						}

					}
				});
			}
		}
	}

}
