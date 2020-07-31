package com.opentach.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtils {

	private static final Logger logger = LoggerFactory.getLogger(ZipUtils.class);

	/**
	 * Comprime los datos del stream de entrada como un elemento del fichero zip de salida.
	 *
	 * @param instream
	 *            InputStream de los datos de entrada.
	 * @param desc
	 *            String descripcion del elemento en el zip de salida.
	 * @param zos
	 *            ZipOutputStream zip de salida.
	 */
	public static void zip(InputStream instream, String desc, ZipOutputStream zos) {
		byte[] buf = new byte[1024];
		try {
			try {
				zos.putNextEntry(new ZipEntry(desc));
				for (int len; (len = instream.read(buf, 0, buf.length)) > 0;) {
					zos.write(buf, 0, len);
				}
			} catch (ZipException ex) {
				ZipUtils.logger.error(null, ex);
			}
		} catch (IOException error) {
			ZipUtils.logger.error(null, error);
		} finally {
			try {
				zos.closeEntry();
			} catch (IOException error) {
				ZipUtils.logger.error(null, error);
			}
		}
	}

	// /Extract the zip file entries and return ByteArrayInputStream for each
	// file.
	public static List<ByteArrayInputStream> unzip(InputStream in) {
		// Lista con los bytearrayInputStream con los ficheros descomprimidos.
		List<ByteArrayInputStream> rtn = new ArrayList<ByteArrayInputStream>();
		ZipInputStream zis = null;
		try {
			// String fname;
			zis = new ZipInputStream(in);
			ZipEntry e;
			while ((e = zis.getNextEntry()) != null) {
				if (!e.isDirectory()) {
					// String tempName = e.getName();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] buf = new byte[1024];
					int len;
					while ((len = zis.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.flush();
					out.close();
					rtn.add(new ByteArrayInputStream(out.toByteArray()));
				}
			}
			zis.close();
		} catch (FileNotFoundException error) {
			ZipUtils.logger.error(null, error);
		} catch (Exception error) {
			ZipUtils.logger.error(null, error);
		} finally {
			if (zis != null) {
				try {
					zis.close();
				} catch (IOException error) {
					ZipUtils.logger.error(null, error);
				}
			}
		}
		return rtn;
	}

	public static String unzip(InputStream in, String extractTo) throws ZipException {
		String name = null;
		try (ZipInputStream zis = new ZipInputStream(in);) {
			String xtTo = (extractTo == null) ? "." : extractTo;
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) { // first create directory
					new File(xtTo, entry.getName()).mkdir();
				} else {
					// check, if name contains directory + file name at once
					// (e.g. dir1/dir2/file1).
					// if it true, first create directories and
					// than the file.
					String tempName = entry.getName();
					int indx = tempName.lastIndexOf(File.separator);
					if (indx > 0) {
						String dirs = tempName.substring(0, indx);
						new File(xtTo, dirs).mkdirs();
					} else {
						indx = 0;
					}
					File f = new File(xtTo, entry.getName());
					try {
						f.createNewFile();
					} catch (IOException ioex) {

					}
					ZipUtils.unzipEntry(zis, f);
					name = entry.getName();
				}
			}
		} catch (Exception error) {
			ZipUtils.logger.error(null, error);
		}
		return name;
	}

	public static String unzip(File file, String extractTo) throws FileNotFoundException, IOException {
		try (FileInputStream fis = new FileInputStream(file)) {
			return ZipUtils.unzip(fis, extractTo);
		}
	}

	public static synchronized void unzipEntry(ZipInputStream zin, File outFile) throws IOException {
		FileOutputStream out = new FileOutputStream(outFile);
		byte[] buf = new byte[1024];
		int len;
		while ((len = zin.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
	}
}
