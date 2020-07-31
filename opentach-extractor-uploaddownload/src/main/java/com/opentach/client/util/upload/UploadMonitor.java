package com.opentach.client.util.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationEvent;
import com.ontimize.gui.ApplicationListener;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.gui.Form;
import com.ontimize.gui.MainApplication;
import com.ontimize.jee.common.tools.MapTools;
import com.opentach.client.util.HideThreadMonitor;
import com.opentach.client.util.StoreLogParser;
import com.opentach.client.util.TGDFileInfo;
import com.opentach.client.util.operationmonitor.OperationMonitor;
import com.opentach.client.util.operationmonitor.OperationMonitor.ExtOpThreadsMonitor;
import com.opentach.common.OpentachFieldNames;

/**
 * Envia de forma automatica los ficheros nuevos encontrados en los directorios que se van insertando al monitor.
 * El log de envio lo mantiene en un fichero residente en el directorio desde el que realiza el envio.
 *
 * @author Pablo Dorgambide
 * @email pablo.dorgambide@imatia.com
 * @modifiedby Rafa
 * @company Imatia
 */
public final class UploadMonitor {

	private static final Logger					logger			= LoggerFactory.getLogger(UploadMonitor.class);

	public static final String					FILE_ENTITY		= "EFicherosTGD";
	public static final String					FILE_COL		= "FICHERO";
	public final static String					RUNNING_TAG		= "uploadmonitor.running";
	public final static String					INTERVAL_TAG	= "uploadmonitor.timeout";

	private static String						logfile			= StoreLogParser.UPLOAD_LOG;

	private int									numThreads		= 4;
	private final Map<String, List<String>>		columnsDirs;
	private Hashtable							kvFile;
	private final Set<ExtendedOperationThread>	fileThreads;
	private final Vector<UploadInfo>			fileQueue		= new Vector<UploadInfo>();
	private final UploadDequeuer				upd				= new UploadDequeuer();
	private final RepositorySender				reps			= new RepositorySender();
	private final UploadSessionManager			upsm;
	private final HideThreadMonitor				htm;
	private final String						uriSonido;
	private ApplicationListener					applistener		= null;

	private class UploadDequeuer extends Thread {
		protected UploadDequeuer() {
			super(UploadDequeuer.class.getName());
			this.setPriority(Thread.MIN_PRIORITY);
		}

		@Override
		public void run() {
			while (true) {
				try {
					synchronized (this) {
						this.wait();
					}
				} catch (Exception ex) {
					UploadMonitor.logger.error(null, ex);
				}
				while ((UploadMonitor.this.fileThreads.size() < UploadMonitor.this.numThreads) && (UploadMonitor.this.fileQueue.size() > 0)) {
					UploadInfo upi = UploadMonitor.this.fileQueue.remove(0);
					UploadMonitor.this.attach(upi);
				}
			}
		}
	}

	private class RepositorySender extends Thread {

		private int			timeout	= 50000;

		private boolean		running	= false;
		private Semaphore	sem		= null;

		protected RepositorySender() {
			super(RepositorySender.class.getName());
			this.setPriority(Thread.MIN_PRIORITY);
			this.sem = new Semaphore(1);
		}

		public boolean isRunning() {
			return this.running;
		}

		public void setRunning(boolean running) {
			if (this.running == running) {
				return;
			}
			if (running) {
				this.sem.release();
			} else {
				this.sem.acquireUninterruptibly();
			}
			this.running = running;
		}

		public int getTimeout() {
			return this.timeout;
		}

		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}

		@Override
		public void run() {
			while (true) {
				if (this.running) {
					this.sem.acquireUninterruptibly();
					for (String col : UploadMonitor.this.columnsDirs.keySet()) {
						List<String> dirs = UploadMonitor.this.columnsDirs.get(col);
						for (String dir : dirs) {
							UploadMonitor.this.sendRecentFiles(dir, col, UploadMonitor.this.uriSonido);
						}
					}
					this.sem.release();
				}
				try {
					Thread.sleep(this.timeout);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public UploadMonitor(Hashtable cv, HideThreadMonitor htm, String uriSonido) {
		super();
		this.htm = htm;
		this.uriSonido = uriSonido;
		this.setKVFile(cv);
		this.upd.start();
		this.reps.start();
		this.upsm = new UploadSessionManager(this);
		this.fileThreads = Collections.synchronizedSet(new HashSet<ExtendedOperationThread>());
		this.columnsDirs = Collections.synchronizedMap(new HashMap<String, List<String>>());
	}

	public void addDir(String col, String dir) {
		List<String> dirs = this.columnsDirs.get(col);
		if (dirs == null) {
			dirs = new ArrayList<String>();
		}
		if (!dirs.contains(dir)) {
			dirs.add(dir);
		}
		this.columnsDirs.put(col, dirs);
	}

	public void setDir(String col, String dir) {
		List<String> dirs = new ArrayList<String>();
		dirs.add(dir);
		this.columnsDirs.put(col, dirs);
	}

	public void removeDir(String col, String dir) {
		List dirs = this.columnsDirs.get(col);
		if (dirs != null) {
			if (dirs.indexOf(dir) != -1) {
				dirs.remove(dirs.indexOf(dir));
			}
		}
	}

	public boolean isRunning() {
		return this.reps.isRunning();
	}

	public void restart() {
		UploadMonitor.logger.info("Subida automatica de ficheros activada");
		for (String col : this.columnsDirs.keySet()) {
			UploadMonitor.logger.info("\tCol: {} DIR: {}", col, this.columnsDirs.get(col));
		}
		this.reps.setRunning(true);
	}

	public void pause() {
		UploadMonitor.logger.info("Subida automatica de ficheros detenida");
		this.reps.setRunning(false);
	}

	public synchronized void sendFiles(List<String> filenames, String dir, String col, Hashtable cv, Form f, boolean showProgress, String uriSound) {
		List<TGDFileInfo> files = new ArrayList<>();
		for (String filename : filenames) {
			String path = dir == null ? filename : dir + File.separator + filename;
			files.add(new TGDFileInfo(new File(path)));
		}
		this.sendTGDFiles(files, col, cv, f, showProgress, uriSound);
	}

	public synchronized void sendTGDFiles(List<TGDFileInfo> files, String col, Hashtable cv, Form f, boolean showProgress, String uriSound) {
		if (this.upsm != null) {
			try {
				this.upsm.send(files);
			} catch (Exception error) {
				UploadMonitor.logger.error(null, error);
			}
		}
		for (TGDFileInfo fileInfo : files) {
			Hashtable kv = new Hashtable<>(cv);
			if (!cv.containsKey(OpentachFieldNames.FILENAME_FIELD)) {
				MapTools.safePut(kv, OpentachFieldNames.FILENAME_FIELD, fileInfo.getUsbFile() != null ? fileInfo.getUsbFile().getName() : fileInfo.getOrigFile().getName());
			}
			this.enQueue(fileInfo, col, kv, f, showProgress, uriSound);
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void sendRecentFiles(String dirname, String col, String uriSound) {
		File dir = new File(dirname);
		if (!dir.isDirectory()) {
			UploadMonitor.logger.info("Ruta especificada no valida. Debe seleccionar un directorio");
			return;
		}
		Map<String, String> recentfiles = StoreLogParser.getRecentFiles(dir);
		Hashtable cv = new Hashtable();
		cv.putAll(this.getKVFile());
		this.sendFiles(new ArrayList<String>(recentfiles.keySet()), dirname, col, cv, null, true, uriSound);
	}

	private ExtendedOperationThread createSendThread(final Form form, final String column, final TGDFileInfo fSeleccionado,
			final String uriSonido,
			Hashtable cv) {
		return new TGDFileSendThread(form, cv, fSeleccionado, uriSonido, this.upsm) {
			@Override
			protected void onSuccess() {
				super.onSuccess();
				try {
					StoreLogParser.registerUploadFile(fSeleccionado.getOrigFile());
				} catch (Exception e) {
					UploadMonitor.logger.error(null, e);
				}
			}

			@Override
			protected void onFinished() {
				UploadMonitor.this.fileThreads.remove(this);
				try {
					synchronized (UploadMonitor.this.upd) {
						UploadMonitor.this.upd.notify();
					}
				} catch (Exception e) {
					UploadMonitor.logger.error(null, e);
				}
			}
		};
	}

	private void enQueue(TGDFileInfo fileInfo, String column, Hashtable cv, Form f, boolean showProgress, String uriSound) {
		UploadInfo upi = new UploadInfo(fileInfo, column, cv, f, showProgress, uriSound);
		if (!this.fileQueue.contains(upi)) {
			this.fileQueue.addElement(upi);
		}
		try {
			synchronized (this.upd) {
				this.upd.notify();
			}
		} catch (Exception e) {
			UploadMonitor.logger.error(null, e);
		}
	}

	private void attach(UploadInfo upi) {
		this.attach(upi.getFileInfo(), upi.getCol(), upi.getCv(), upi.getForm(), upi.isShowProgress(), upi.getUriSound());
	}

	private void attach(TGDFileInfo fileInfo, String column, Hashtable cv, Form form, boolean showProgress, String uri) {
		if (fileInfo != null) {
			ExtendedOperationThread opThread = this.createSendThread(form, column, fileInfo, uri, cv);
			this.fileThreads.add(opThread);
			if (showProgress) {
				OperationMonitor.ExtOpThreadsMonitor mon = this.installThreadsMonitor(form);
				mon.addExtOpThread(opThread);
			} else {
				opThread.start();
			}
		}
	}

	public void attach(ExtendedOperationThread opThread, Form form, boolean showProgress) {
		if (opThread != null) {
			this.fileThreads.add(opThread);
			if (showProgress) {
				OperationMonitor.ExtOpThreadsMonitor mon = this.installThreadsMonitor(form);
				mon.addExtOpThread(opThread);
			} else {
				opThread.start();
			}
		}
	}

	private ExtOpThreadsMonitor installThreadsMonitor(Form f) {
		OperationMonitor.ExtOpThreadsMonitor mon = OperationMonitor.getExtOpThreadsMonitor();
		mon.setVisible(true);
		this.htm.add(mon);
		MainApplication apg = (MainApplication) ApplicationManager.getApplication();
		if (this.applistener == null) {
			this.applistener = new ApplicationListener() {
				@Override
				public boolean applicationClosing(ApplicationEvent applicationEvent) {
					if ((UploadMonitor.this.fileThreads != null) && !UploadMonitor.this.fileThreads.isEmpty()) {
						for (ExtendedOperationThread opt : UploadMonitor.this.fileThreads) {
							if (opt.isAlive()) {
								MainApplication apg = (MainApplication) applicationEvent.getSource();
								String msg = ApplicationManager.getTranslation("M_ENVIANDO_FICHEROS_MSG", apg.getResourceBundle());
								String title = ApplicationManager.getTranslation("M_ENVIANDO_FICHEROS_TITLE", apg.getResourceBundle());
								JOptionPane.showMessageDialog(apg, msg, title, JOptionPane.WARNING_MESSAGE);
							}
						}
					}
					return true;
				}
			};
			apg.addApplicationListener(this.applistener);
		}
		return mon;
	}

	protected void setKVFile(Hashtable cv) {
		this.kvFile = cv;
	}

	public Hashtable getKVFile() {
		return this.kvFile;
	}

	public static String getLogfilename() {
		return UploadMonitor.logfile;
	}

	public static void setLogfilename(String logfilename) {
		UploadMonitor.logfile = logfilename;
	}

	public int getNumThreads() {
		return this.numThreads;
	}

	public void addUploadListener(UploadListener ul) {
		this.upsm.addUploadListener(ul);
	}

	public void removeUploadListener(UploadListener ul) {
		this.upsm.removeUploadListener(ul);
	}

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}

	public void setTimeout(int timeout) {
		this.reps.setTimeout(timeout);
	}
}
