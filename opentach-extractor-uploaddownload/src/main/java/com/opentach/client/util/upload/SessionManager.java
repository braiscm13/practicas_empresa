package com.opentach.client.util.upload;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.opentach.client.util.TGDFileInfo;

/**
 * To manage the upload of several files and to get notified
 *
 * @author rafael.lopez
 */
public final class SessionManager {

	private final Map<Integer, List<TGDFileInfo>>	mSessions;
	private final Map<Integer, List<TGDFileInfo>>	mSessionsBacked;
	private final Map<TGDFileInfo, Integer>			mFileSession;
	private final List<Integer>						lSessions;

	public SessionManager() {
		this.mSessions = new Hashtable<Integer, List<TGDFileInfo>>();
		this.mSessionsBacked = new Hashtable<Integer, List<TGDFileInfo>>();
		this.mFileSession = new Hashtable<TGDFileInfo, Integer>();
		this.lSessions = new Vector<Integer>();
	}

	public synchronized int newSession() {
		int max = 1;
		int aux = 0;
		for (int i = 0; i < this.lSessions.size(); i++) {
			aux = this.lSessions.get(i);
			if (aux > max) {
				max = aux;
			}
		}
		int key = max + 1;
		this.lSessions.add(key);
		this.mSessions.put(key, new ArrayList<TGDFileInfo>());
		this.mSessionsBacked.put(key, new ArrayList<TGDFileInfo>());
		return max + 1;
	}

	public synchronized void endSession(int session) {
		this.lSessions.remove((Integer) session);
		this.mSessions.remove(session);
		List<TGDFileInfo> lFiles = this.mSessionsBacked.get(session);
		if (lFiles != null) {
			for (TGDFileInfo f : lFiles) {
				this.mFileSession.remove(f);
			}
		}
		this.mSessionsBacked.remove(session);
	}

	public synchronized List<TGDFileInfo> getBackedSession(int session) {
		return this.mSessionsBacked.get(session);
	}

	public synchronized List<TGDFileInfo> getSession(int session) {
		return this.mSessions.get(session);
	}

	public synchronized boolean isSessionTerminated(int session) {
		List<TGDFileInfo> l = this.mSessions.get(session);
		return ((l == null) || (l.size() == 0));
	}

	public synchronized void addForSession(TGDFileInfo ufi, int session) {
		List<TGDFileInfo> l1 = this.mSessions.get(session);
		if (l1 == null) {
			l1 = new ArrayList<TGDFileInfo>();
		}
		l1.add(ufi);
		this.mSessions.put(session, l1);
		this.mFileSession.put(ufi, session);
	}

	public synchronized void addForSession(List<TGDFileInfo> ufi, int session) {
		List<TGDFileInfo> l1 = this.mSessions.get(session);
		if (l1 == null) {
			l1 = new ArrayList<TGDFileInfo>();
		}
		for (TGDFileInfo f : ufi) {
			l1.add(f);
			this.mFileSession.put(f, session);
		}
		this.mSessions.put(session, l1);
	}

	public synchronized void removeForSession(TGDFileInfo ufi, int session) {
		List<TGDFileInfo> l = this.mSessions.get(session);
		if (l != null) {
			l.remove(ufi);
			List<TGDFileInfo> l2 = this.mSessionsBacked.get(session);
			l2.add(ufi);
			this.mFileSession.remove(ufi);
		}
	}

	public synchronized Integer remove(TGDFileInfo ufi) {
		Integer session = this.mFileSession.remove(ufi);
		if (session != null) {
			this.removeForSession(ufi, session);
		}
		return session;
	}
}
