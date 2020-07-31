package com.opentach.client.modules.data.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.util.AddressButtonCellEditor.MapThread;
import com.opentach.common.smartphonelocation.ISmartphoneLocationService;
import com.opentach.common.smartphonelocation.LocationInfo;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.client.gui.tasks.WorkerStatusInfo;

public class ListenerLocalizeSmarphone extends AbstractActionListenerButton {

	private static final Logger	logger	= LoggerFactory.getLogger(ListenerLocalizeSmarphone.class);

	private static final int	MARGIN_VALID_LOCATION_INFO	= 60 * 1000;

	public ListenerLocalizeSmarphone(UButton button, Hashtable params) throws Exception {
		super(button, ListenerLocalizeSmarphone.completeParams(params));
	}

	private static Hashtable completeParams(Hashtable params) {
		params.put(AbstractActionListenerButton.PARAM_ENABLE_FIM_UPDATE, "yes");
		params.put(AbstractActionListenerButton.PARAM_ENABLE_FIM_INSERT, "no");
		params.put(AbstractActionListenerButton.PARAM_ENABLE_FIM_QUERY, "no");
		params.put(AbstractActionListenerButton.PARAM_ENABLE_FIM_QUERY_INSERT, "no");
		return params;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final Form form = ((UButton) this.getButton()).getParentForm();
		new USwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				this.fireStatusUpdate(new WorkerStatusInfo("M_LOCALIZING", null, null));

				String smartphonePIN = (String) form.getDataFieldValue("PIN");
				ISmartphoneLocationService service = this.getService(form);

				// First step: Check last knowed position
				// Second fire to send notification to retrieve last position -> Check available TokenId
				boolean resSendPushLocation = service.sendPushLocation(smartphonePIN);
				if (!resSendPushLocation) {
					throw new Exception("E_SMARTPHONE_UNREGISTERED_FOR_PUSH");
				}

				return null;
			}

			@Override
			protected void done() {
				try {
					this.uget();
					this.firePropertyChange("status", StateValue.STARTED, StateValue.DONE);

					// At this point we sent push notification to mobile requesting position
					// Now we want to show a map with recopiled info (if available)
					new ThreadRefreshMap(form).start();
				} catch (Throwable ex) {
					MessageManager.getMessageManager().showExceptionMessage(ex, ListenerLocalizeSmarphone.logger);
				}
			}

			private ISmartphoneLocationService getService(final Form form) throws Exception {
				OpentachClientLocator ocl = (OpentachClientLocator) form.getFormManager().getReferenceLocator();
				ISmartphoneLocationService service = (ISmartphoneLocationService) ocl.getRemoteReference("SmartphoneLocationService",
						ocl.getSessionId());
				return service;
			}
		}.executeOperation(form);
	}

	protected class ThreadRefreshMap extends Thread {
		protected Form parentForm;

		public ThreadRefreshMap(Form form) {
			super("Thread-RefreshMap");
			this.parentForm = form;
		}

		@Override
		public void run() {
			super.run();

			try {
				String smartphonePIN = (String) this.parentForm.getDataFieldValue("PIN");
				ISmartphoneLocationService service = this.getService(this.parentForm);

				// First: Show map with last location avialable
				MapThread mapThread = null;
				LocationInfo locationInfo = null;
				try {
					locationInfo = service.getLocationInfo(smartphonePIN);
					mapThread = new MapThread(ListenerLocalizeSmarphone.this.getForm(), locationInfo);
					mapThread.setLoading(true);
					mapThread.start();
				} catch (Exception ex) {
				}

				// Second : query during X time last location avialable and show in map
				try {
					long refreshingTime = 60000;
					long startTime = System.currentTimeMillis();
					while ((System.currentTimeMillis() - startTime) < refreshingTime) {
						try {
							LocationInfo newLocationInfo = service.getLocationInfo(smartphonePIN);
							// If changed, reload info
							if (!ObjectTools.safeIsEquals(locationInfo, newLocationInfo)) {
								mapThread.updateInfo(locationInfo);
							}

							locationInfo = newLocationInfo;
						} catch (Exception ex) {
						}

						Thread.sleep(5000);
					}
				} finally {
					mapThread.setLoading(false);
				}

				this.checkValidLocation(locationInfo, this.parentForm);

			} catch (Exception ex) {
				ListenerLocalizeSmarphone.logger.error("E_LOADING_LOCATION", ex);
				if (this.parentForm.isVisible() && this.parentForm.isShowing()) {
					this.parentForm.message("E_UNKNOWED_LOCATION", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		private void checkValidLocation(LocationInfo locationInfo, Form form) {
			if ((locationInfo == null) || (locationInfo.getDate() == null) || (locationInfo.getLatitude() == null) || (locationInfo
					.getLongitude() == null)) {
				form.message("E_UNKNOWED_LOCATION", JOptionPane.ERROR_MESSAGE);
				return;
			}
			boolean oldData = ((System.currentTimeMillis() - locationInfo.getDate()
					.getTime()) > (ListenerLocalizeSmarphone.MARGIN_VALID_LOCATION_INFO));
			if (oldData) {
				form.message("E_OLD_LOCATION", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		private ISmartphoneLocationService getService(final Form form) throws Exception {
			OpentachClientLocator ocl = (OpentachClientLocator) form.getFormManager().getReferenceLocator();
			ISmartphoneLocationService service = (ISmartphoneLocationService) ocl.getRemoteReference("SmartphoneLocationService", ocl.getSessionId());
			return service;
		}

	}
}