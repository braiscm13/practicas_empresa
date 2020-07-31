package com.opentach.client.dms.transfermanager;

import com.ontimize.gui.ApplicationManager;
import com.opentach.client.dms.transfermanager.AbstractDmsTransferable.Status;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.dms.DmsException;
import com.opentach.common.dms.IDMSService;

public abstract class AbstractDmsTransferer<T extends AbstractDmsTransferable> {

	public void transfer(T transferable) throws DmsException {
		try {
			transferable.setStatus(Status.DOWNLOADING);
			this.doTransfer(transferable);
			transferable.setStatus(Status.COMPLETED);
		} catch (Exception ex) {
			transferable.setStatus(Status.ERROR);
			if (ex instanceof DmsException) {
				throw ex;
			}
			throw new DmsException(ex.getMessage(), ex);
		}
	}

	protected abstract void doTransfer(T transferable) throws DmsException;

	protected IDMSService getDMSService() throws DmsException {
		try {
			UserInfoProvider ocl = (UserInfoProvider) ApplicationManager.getApplication().getReferenceLocator();
			return ocl.getRemoteService(IDMSService.class);
		} catch (Exception error) {
			throw new DmsException(error);
		}
	}

	protected int getSessionId() throws DmsException {
		try {
			return ApplicationManager.getApplication().getReferenceLocator().getSessionId();
		} catch (Exception error) {
			throw new DmsException(error);
		}
	}
}