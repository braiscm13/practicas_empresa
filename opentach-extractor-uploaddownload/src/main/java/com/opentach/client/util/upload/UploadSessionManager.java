package com.opentach.client.util.upload;

import java.util.List;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.TGDFileInfo;
import com.opentach.client.util.upload.UploadEvent.UploadEventType;

/**
 * Session events
 *
 * @author rafael.lopez
 */
public class UploadSessionManager implements UploadNotifier {

	private static final Logger		logger	= LoggerFactory.getLogger(UploadSessionManager.class);

	private final SessionManager sm;
	private final EventListenerList	elListeners;
	private final UploadMonitor		um;

	public UploadSessionManager(UploadMonitor um) {
		super();
		this.um = um;
		this.sm = new SessionManager();
		this.elListeners = new EventListenerList();
	}

	@Override
	public synchronized void notifyUploadEvent(TGDFileInfo file, UploadEventType type, String msg) {
		Integer session = this.sm.remove(file);
		if ((session != null) && this.sm.isSessionTerminated(session)) {
			List<TGDFileInfo> lInfo = this.sm.getBackedSession(session);
			this.sm.endSession(session);
			UploadEvent ue = null;
			if ((lInfo != null) && (lInfo.size() == 1)) {
				ue = new UploadEvent(this.um, type, msg, lInfo);
			} else {
				ue = new UploadEvent(this.um, UploadEventType.FILE_UPLOAD_END, msg, lInfo);
			}
			this.notifyUploadStatusChange(ue);
		} else if (session != null) {
			int uploaded = this.sm.getBackedSession(session).size();
			int remain = this.sm.getSession(session).size();
			this.notifyUploadStatusChange(new UploadEvent(this.um, UploadEventType.FILE_UPLOAD_PROGRESS, uploaded, remain + uploaded));
		}
	}

	public synchronized void send(List<TGDFileInfo> listFiles) {
		int session = this.sm.newSession();
		this.sm.addForSession(listFiles, session);
	}

	public void addUploadListener(UploadListener ul) {
		this.elListeners.add(UploadListener.class, ul);
	}

	public void removeUploadListener(UploadListener ul) {
		this.elListeners.remove(UploadListener.class, ul);
	}

	private void notifyUploadStatusChange(UploadEvent ue) {
		UploadListener[] ulArray = this.elListeners.getListeners(UploadListener.class);
		for (UploadListener ul : ulArray) {
			try {
				ul.uploadStatusChange(ue);
			} catch (Exception ex) {
				UploadSessionManager.logger.error(null, ex);
			}
		}
	}

}