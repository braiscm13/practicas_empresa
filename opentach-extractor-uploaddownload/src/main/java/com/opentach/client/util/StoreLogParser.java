package com.opentach.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class StoreLogParser {

	public static final String	UPLOAD_LOG		= "uploadtgd.log";
	public static final String	DOWNLOAD_LOG	= "downloadtgd.log";

	/**
	 * @param dir
	 * @return Map with the file name as key and date of upload as value.
	 */
	public static Map<String, String> getUploadedFiles(File dir) {
		Map<String, String> rtn = new HashMap<String, String>();
		try {
			List<File> files = new ArrayList<File>();
			Properties log = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(new File(dir.getAbsoluteFile() + File.separator + StoreLogParser.UPLOAD_LOG));
				log.load(fis);
			} catch (Exception ex) {
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
					}
				}
			}
			if (dir == null) {
				return rtn;
			}
			if (dir.listFiles() != null) {
				files.addAll(Arrays.asList(dir.listFiles()));
			}
			for (Iterator<File> iter = files.iterator(); iter.hasNext();) {
				File file = iter.next();
				if (!log.containsKey(file.getName()) || file.getName().equals(StoreLogParser.UPLOAD_LOG)
						|| file.getName().equals(StoreLogParser.DOWNLOAD_LOG)) {
					iter.remove();
				}
			}
			for (File file : files) {
				rtn.put(file.getName(), log.getProperty(file.getName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}

	/**
	 * Files downloade from the CSD.
	 *
	 * @param dir
	 * @return Map with the filename as key and date of download as value.
	 */
	public static Map<String, String> getDownloadedFiles(File dir) {
		HashMap<String, String> rtn = new HashMap<String, String>();
		try {
			List<File> files = new ArrayList<File>();
			Properties log = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(new File(dir.getAbsoluteFile() + File.separator + StoreLogParser.DOWNLOAD_LOG));
				log.load(fis);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
					}
				}
				;
			}
			if (dir == null) {
				return rtn;
			}
			files.addAll(Arrays.asList(dir.listFiles()));
			for (Iterator<File> iter = files.iterator(); iter.hasNext();) {
				File file = iter.next();
				if (!log.containsKey(file.getName()) || file.getName().equals(StoreLogParser.UPLOAD_LOG)
						|| file.getName().equals(StoreLogParser.DOWNLOAD_LOG)) {
					iter.remove();
				}
			}
			for (File file : files) {
				rtn.put(file.getName(), log.getProperty(file.getName()));
			}
		} catch (Exception e) {

		}
		return rtn;
	}

	/**
	 * Recent files are files that not be upload or download.
	 *
	 * @param dir
	 * @return
	 */
	public static Map<String, String> getRecentFiles(File dir) {
		HashMap<String, String> rtn = new HashMap<String, String>();
		try {
			List<File> files = new ArrayList<File>();
			if (dir == null) {
				return new HashMap<String, String>();
			}
			if (dir.listFiles() != null) {
				files.addAll(Arrays.asList(dir.listFiles()));
			}
			Properties log = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(new File(dir.getAbsoluteFile() + File.separator + StoreLogParser.DOWNLOAD_LOG));
				log.load(fis);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
					}
				}
				;
			}
			for (Iterator<File> iter = files.iterator(); iter.hasNext();) {
				File file = iter.next();
				// Remove uplodaded-downloaded and dirs.
				if (file.isDirectory() || log.containsKey(file.getName()) || file.getName().equals(StoreLogParser.UPLOAD_LOG)
						|| file.getName().equals(StoreLogParser.DOWNLOAD_LOG)) {
					iter.remove();
				}
			}
			log.clear();
			try {
				fis = new FileInputStream(new File(dir.getAbsoluteFile() + File.separator + StoreLogParser.UPLOAD_LOG));
				log.load(fis);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
					}
				}
				;
			}

			for (Iterator<File> iter = files.iterator(); iter.hasNext();) {
				File file = iter.next();
				if (log.containsKey(file.getName())) {
					iter.remove();
				}
			}

			for (Iterator<File> iter = files.iterator(); iter.hasNext();) {
				File file = iter.next();
				rtn.put(file.getName(), log.getProperty(file.getName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}

	protected void removeFile(File file) {
		try {
			// Eliminar el dato del fichero de los logs.
			Properties log = new Properties();
			File dir = file.getParentFile();
			FileInputStream fis = null;
			FileOutputStream fos = null;
			try {
				fis = new FileInputStream(new File(dir.getAbsoluteFile() + File.separator + StoreLogParser.DOWNLOAD_LOG));
				log.load(fis);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
					}
				}
			}
			log.remove(file.getName());
			try {
				fos = new FileOutputStream(new File(StoreLogParser.UPLOAD_LOG));
				log.store(fos, "#Last Update at " + DateFormat.getInstance().format(new Date()));
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (Exception e) {
					}
				}
			}
			log.clear();
			try {
				fis = new FileInputStream(new File(dir.getAbsoluteFile() + File.separator + StoreLogParser.UPLOAD_LOG));
				log.load(fis);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
					}
				}
			}
			log.remove(file.getName());
			try {
				fos = new FileOutputStream(new File(StoreLogParser.DOWNLOAD_LOG));
				log.store(fos, "#Last Update at " + DateFormat.getInstance().format(new Date()));
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (Exception e) {
					}
				}
			}
			// Eliminar el fichero.
			file.delete();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void registerUploadFile(File file) {
		StoreLogParser.registerFile(file, StoreLogParser.UPLOAD_LOG);
	}

	public static void registerUploadFiles(List<File> files) {
		StoreLogParser.registerFiles(files, StoreLogParser.UPLOAD_LOG);
	}

	public static void registerDownloadFiles(List<File> files) {
		StoreLogParser.registerFiles(files, StoreLogParser.DOWNLOAD_LOG);
	}

	private static synchronized void registerFile(File file, String logfile) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			Properties log = new Properties();
			File dir = file.getParentFile();
			try {
				fis = new FileInputStream(new File(dir.getAbsoluteFile() + File.separator + logfile));
				log.load(fis);
			} catch (Exception ex) {

			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
					}
				}
			}
			try {
				fos = new FileOutputStream(new File(dir.getAbsoluteFile() + File.separator + logfile));
				log.put(file.getName(), DateFormat.getInstance().format(new Date()));
				log.store(fos, "# Last Update at " + DateFormat.getInstance().format(new Date()));
			} catch (Exception e) {
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void registerFiles(List<File> files, String logfile) {
		try {
			Properties log = new Properties();
			if (files.size() == 0) {
				return;
			}
			File f = files.get(0);
			File dir = files.get(0).getParentFile();
			File file = null;
			FileInputStream fis = null;
			try {
				file = new File(dir.getAbsoluteFile() + File.separator + logfile);
				if (file != null) {
					fis = new FileInputStream(f);
					log.load(fis);
				}
			} catch (Exception ex) {
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
					}
				}
			}
			DateFormat df = DateFormat.getInstance();
			String date = df.format(new Date());
			for (File ifile : files) {
				String fname = ifile.getName();
				log.put(fname, date);
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				log.store(fos, "# Last Update at " + date);
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void registerDownloadFile(File file) {
		StoreLogParser.registerFile(file, StoreLogParser.DOWNLOAD_LOG);
	}
}
