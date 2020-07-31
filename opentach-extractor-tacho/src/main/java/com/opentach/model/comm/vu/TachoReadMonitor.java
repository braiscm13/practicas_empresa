package com.opentach.model.comm.vu;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Observable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;

/**
 * Monitor de descarga del tacografo. Hilo para detectar automaticamente la presencia del tacografo y descargar los ficheros al lamecen
 *
 * @author Pablo Dorgambide
 * @company www.imatia.com
 * @email pdorgambide@imatia.com
 * @date: 03/07/2007
 */
public class TachoReadMonitor extends Observable implements Runnable {

	private static final Logger	logger			= LoggerFactory.getLogger(TachoReadMonitor.class);

	protected TachoRead			tr;
	protected Date				from, to;
	protected boolean			notifyOnFinish	= false;

	protected int				period			= TachoConfig.PERIOD_LAST_MONTH;
	protected boolean			enable			= false;

	protected byte				files2download	= 0x00;
	public static final byte	TC_FILES		= 0x01;
	public static final byte	VU_FILES		= 0x02;
	protected File				repository;

	public TachoReadMonitor() {
		super();
		Thread thread = new Thread(this, TachoReadMonitor.class.getName());
		this.tr = new TachoRead();
		thread.start();
	}

	public void setRepositoryDir(File dir) {
		if ((dir != null) && !dir.isDirectory()) {
			throw new IllegalArgumentException("Repositor argument must be a valid DIRECTORY");
		}
		this.repository = dir;
	}

	@Override
	public void run() {
		while (1 == 1) {
			try {
				Thread.sleep(4000);
				if (this.enable && this.tr.iniciarSesion()) {
					String tachofilename = null;
					TachoFile tfile = null;
					TachoReadMonitor.logger.info("Detectado Tacografo conectado....");
					TachoReadMonitor.logger.info("Descargando datos...");
					File file = null;
					if (this.repository != null) {
						file = new File(this.repository.getAbsolutePath() + File.separator + "tmptachoreaderVU.tgd");
					} else {
						file = File.createTempFile("TachoReadMon_VUFile_", "");
					}
					file.deleteOnExit();
					this.descargarFicheroVU(file);
					// Renombro el fichero con el nombre normalizado.
					tfile = TachoFile.readTachoFile(file);
					tachofilename = tfile.computeFileName(null, com.imatia.tacho.model.TachoFile.FILENAME_FORMAT_SPAIN, null);
					file.renameTo(new File(tachofilename));
					String rightfilename = file.getParentFile().getAbsolutePath() + File.separator + tachofilename;

					// Datos descargados avisar a los escuchadores..
					if (this.notifyOnFinish) {
						this.setChanged();
						this.notifyObservers(file.getAbsolutePath());
					}
					file.delete();

					if (this.repository != null) {
						file = new File(this.repository.getAbsolutePath() + File.separator + "tmptachoreaderTC.tgd");
					} else {
						file = File.createTempFile("TachoReadMon_VUFile_", "");
					}
					file.deleteOnExit();
					this.descargarFicheroTC(file);
					// Renombro el fichero con el nombre normalizado.
					// Renombro el fichero con el nombre normalizado.
					tfile = TachoFile.readTachoFile(file);
					tachofilename = tfile.computeFileName(null, com.imatia.tacho.model.TachoFile.FILENAME_FORMAT_SPAIN, null);
					file.renameTo(new File(tachofilename));
					rightfilename = file.getParentFile().getAbsolutePath() + File.separator + tachofilename;
					file.delete();
					// Datos descargados avisar a los escuchadores..
					if (this.notifyOnFinish) {
						this.setChanged();
						this.notifyObservers(rightfilename);
					}
					file.delete();
				}
			} catch (Exception ex) {
				TachoReadMonitor.logger.warn("" + ex.getMessage() + ex.getStackTrace()[0]);
			}
		}
	}

	public boolean isTachoConnected() {
		return this.tr.iniciarSesion();
	}

	public void setEnable(boolean v) {
		TachoReadMonitor.logger.info("Monitor de descarga de tacógrafo " + (v ? "ACTIVADO" : "DESACTIVADO"));
		this.enable = v;
	}

	public void setPort(String portname) {
		this.tr.setPort(portname);
	}

	public void updateDownloadPeriod() {
		this.setDownloadPeriod(this.period, this.from, this.to);
	}

	public int getDownloadPeriod() {
		return this.period;
	}

	public void setDownloadPeriod(int type, Date from, Date to) {
		try {
			Calendar c = Calendar.getInstance();
			switch (type) {
				case TachoConfig.PERIOD_LAST_MONTH:
					to = (Date) c.getTime().clone();
					c.add(Calendar.DATE, -30);
					from = c.getTime();
					break;
				case TachoConfig.PERIOD_LAST_TWO_WEEKS:
					to = (Date) c.getTime().clone();
					c.add(Calendar.DATE, -14);
					from = c.getTime();
					break;
				case TachoConfig.PERIOD_LAST_WEEK:
					to = (Date) c.getTime().clone();
					c.add(Calendar.DATE, -7);
					from = c.getTime();
					break;
			}
			if (from.after(to)) {
				throw new IllegalArgumentException("ERR_DATE_FROM_AFTER_DATE_TO");
			}

			this.period = type;
			this.from = from;
			this.to = to;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Date getFromDate() {
		return this.from;
	}

	public Date getToDate() {
		return this.to;
	}

	public void setNotifyOnFinish(boolean val) {
		this.notifyOnFinish = val;
	}

	public void setFiles2download(byte files2download) {
		this.files2download = files2download;
	}

	private Vector createDatesBetween(Date ini, Date fin) {
		Vector v = new Vector();

		Calendar c1 = this.onlyDays(ini);
		c1.add(Calendar.DAY_OF_YEAR, 1);
		Calendar c2 = this.onlyDays(fin);
		c2.add(Calendar.DAY_OF_YEAR, 1);
		do {
			v.add(TachoReadMonitor.intToBytes((int) (c1.getTime().getTime() / 1000)));
			c1.add(Calendar.DAY_OF_YEAR, 1);
		} while (c1.getTime().getTime() <= c2.getTime().getTime());
		return v;
	}

	private static byte[] intToBytes(int i) {
		byte[] b = new byte[4];
		b[0] = (byte) (i >> 24);
		b[1] = (byte) (i >> 16);
		b[2] = (byte) (i >> 8);
		b[3] = (byte) i;

		return b;
	}

	private Calendar onlyDays(Date d) {
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}

	public void descargarFicheroVU(File dst) {
		try {
			this.updateDownloadPeriod();

			Vector vdates = this.createDatesBetween(this.from, this.to);
			Vector v = new Vector();
			// if (jcResumen.isSelected())
			v.add(new byte[] { TachoRead.TREP_RESUMEN });
			// if (jcIncidentes.isSelected())
			v.add(new byte[] { TachoRead.TREP_INCIDENTES });
			// if (jcDatosV.isSelected())
			v.add(new byte[] { TachoRead.TREP_VELOCIDAD });
			// if (jcDatosT.isSelected())
			v.add(new byte[] { TachoRead.TREP_DATOST });
			// if (vdates != null)
			v.add(new byte[] { TachoRead.TREP_ACTIVIDADES });

			if (!v.isEmpty()) {
				this.tr.readToFile(dst, v, vdates);

			}
		} catch (Exception err) {
			TachoReadMonitor.logger.error(null, err);
		}

	}

	public void descargarFicheroTC(File dst) {
		try {
			this.updateDownloadPeriod();

			Vector vdates = this.createDatesBetween(this.from, this.to);
			Vector v = new Vector();
			v.add(new byte[] { TachoRead.TREP_TRANSFERENCIA });

			if (!v.isEmpty()) {
				this.tr.readToFile(dst, v, vdates);

			}
		} catch (Exception err) {
			TachoReadMonitor.logger.error(null, err);
		}

	}

}
