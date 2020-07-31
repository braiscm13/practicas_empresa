package com.opentach.model.comm.vu;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JTextArea;

public class TachoRead {
	protected Vector				dates;
	protected String				portname;

	private static boolean			PKT_CONTENT_DEBUG						= false;
	public static boolean			PKT_DEBUG								= false;

	public static byte[]			PETICION_INICIO_COMUNICACION			= { (byte) 0x81, (byte) 0xEE, (byte) 0xF0, (byte) 0x81, (byte) 0xE0 };
	public static byte[]			RP_PETICION_INICIO_COMUNICACION			= { (byte) 0x80, (byte) 0xF0, (byte) 0xEE, (byte) 0x03, (byte) 0xC1,
		(byte) 0xEA, (byte) 0x8F, (byte) 0x9B							};

	public static byte[]			PETICION_INICIO_SESION					= { (byte) 0x80, (byte) 0xEE, (byte) 0xF0, (byte) 0x02, (byte) 0x10,
		(byte) 0x81, (byte) 0xF1										};
	public static byte[]			RP_PETICION_INICIO_SESION				= { (byte) 0x80, (byte) 0xF0, (byte) 0xEE, (byte) 0x02, (byte) 0x50,
		(byte) 0x81, (byte) 0x31										};

	public static byte[]			ENVIO_PETICION							= { (byte) 0x80, (byte) 0xEE, (byte) 0xF0, (byte) 0x0A, (byte) 0x35,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x99 };
	public static byte[]			RP_ENVIO_PETICION						= { (byte) 0x80, (byte) 0xF0, (byte) 0xEE, (byte) 0x03, (byte) 0x75,
		(byte) 0x00, (byte) 0xFF, (byte) 0xD5							};

	public static byte[]			PETICION_SALIDA_TRANSFERENCIA			= { (byte) 0x80, (byte) 0xEE, (byte) 0xF0, (byte) 0x01, (byte) 0x37,
		(byte) 0x96													};
	public static byte[]			RP_PETICION_SALIDA_TRANSFERENCIA		= { (byte) 0x80, (byte) 0xF0, (byte) 0xEE, (byte) 0x01, (byte) 0x77,
		(byte) 0xD6													};

	public static byte[]			PETICION_INTERRUPCION_COMUNICACION		= { (byte) 0x80, (byte) 0xEE, (byte) 0xF0, (byte) 0x01, (byte) 0x82,
		(byte) 0xE1													};
	public static byte[]			RP_PETICION_INTERRUPCION_COMUNICACION	= { (byte) 0x80, (byte) 0xF0, (byte) 0xEE, (byte) 0x01, (byte) 0xC2,
		(byte) 0x21													};

	public static byte[]			CABECERA_PETICION						= { (byte) 0x80, (byte) 0xEE, (byte) 0xF0, (byte) 0x02, (byte) 0x36 };
	public static byte				TREP_RESUMEN							= (byte) 0x01;
	public static byte				TREP_ACTIVIDADES						= (byte) 0x02;
	public static byte				TREP_INCIDENTES							= (byte) 0x03;
	public static byte				TREP_VELOCIDAD							= (byte) 0x04;
	public static byte				TREP_DATOST								= (byte) 0x05;
	public static byte				TREP_TRANSFERENCIA						= (byte) 0x06;

	public static byte[]			CABECERA_RESPUESTA_NEGATIVA				= { (byte) 0x80, (byte) 0xF0, (byte) 0xEE, (byte) 0x03, (byte) 0x7F };
	public static byte				ERROR_NO_ACEPTADO						= (byte) 0x10;
	public static byte				ERROR_SERVICIO_NO_ADMITIDO				= (byte) 0x11;
	public static byte				ERROR_SUBFUNCION_NO_ADMITIDA			= (byte) 0x12;
	public static byte				ERROR_LONGITUD							= (byte) 0x13;
	public static byte				ERROR_SECUENCIA							= (byte) 0x22;
	public static byte				ERROR_PETICION							= (byte) 0x31;
	public static byte				ERROR_FALTA_RESPUESTA					= (byte) 0x50;
	public static byte				ERROR_NO_DISPONIBLES					= (byte) 0x78;
	public static byte				ERROR_GENERAL							= (byte) 0xFA;

	public static byte[]			CABECERA_CONFIRMACION_SUBMENSAJE		= { (byte) 0x80, (byte) 0xEE, (byte) 0xF0 };

	public static byte[]			PETICION_VERIFICAR_VELOCIDAD_9600Bd		= { (byte) 0x80, (byte) 0xEE, (byte) 0xF0, (byte) 0x04, (byte) 0x87,
		(byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0xEC				};
	public static byte[]			PETICION_VERIFICAR_VELOCIDAD_19200Bd	= { (byte) 0x80, (byte) 0xEE, (byte) 0xF0, (byte) 0x04, (byte) 0x87,
		(byte) 0x01, (byte) 0x01, (byte) 0x02, (byte) 0xED				};
	public static byte[]			PETICION_VERIFICAR_VELOCIDAD_38400Bd	= { (byte) 0x80, (byte) 0xEE, (byte) 0xF0, (byte) 0x04, (byte) 0x87,
		(byte) 0x01, (byte) 0x01, (byte) 0x03, (byte) 0xEE				};
	public static byte[]			PETICION_VERIFICAR_VELOCIDAD_57600Bd	= { (byte) 0x80, (byte) 0xEE, (byte) 0xF0, (byte) 0x04, (byte) 0x87,
		(byte) 0x01, (byte) 0x01, (byte) 0x04, (byte) 0xEF				};
	public static byte[]			PETICION_VERIFICAR_VELOCIDAD_115200Bd	= { (byte) 0x80, (byte) 0xEE, (byte) 0xF0, (byte) 0x04, (byte) 0x87,
		(byte) 0x01, (byte) 0x01, (byte) 0x05, (byte) 0xF0				};

	public static byte[]			RP_VELOCIDAD_BAUDIOS					= { (byte) 0x80, (byte) 0xF0, (byte) 0xEE, (byte) 0x02, (byte) 0xC7,
		(byte) 0x01, (byte) 0x28										};
	public static byte[]			VELOCIDAD_BAUDIOS_FASE_2				= { (byte) 0x80, (byte) 0xEE, (byte) 0xF0, (byte) 0x03, (byte) 0x87,
		(byte) 0x02, (byte) 0x03, (byte) 0xED							};

	private static int				TIME_TO_SLEEP_BASIC						= 1000;

	private BufferedOutputStream	bos										= null;

	public static final String		DEBUG_NAME								= "DEBUG";

	private static int				TC_MAX_ERROR							= 35;
	private static int				TC_NO_RESPONSE							= 20;
	private static int				TC_SLEEP_TIME							= 1000;
	public static final String		TC_SLEEP_TIME_NAME						= "TC_SLEEP_TIME";
	public static final String		TC_MAX_ERROR_NAME						= "TC_MAX_ERROR";
	public static final String		TC_NO_RESPONSE_NAME						= "TC_NO_RESPONSE";

	private static int				VU_MAX_ERROR							= 20;
	private static int				VU_NO_RESPONSE							= 20;
	private static int				VU_SLEEP_TIME							= 400;
	public static final String		VU_SLEEP_TIME_NAME						= "VU_SLEEP_TIME";
	public static final String		VU_MAX_ERROR_NAME						= "VU_MAX_ERROR";
	public static final String		VU_NO_RESPONSE_NAME						= "VU_NO_RESPONSE";

	public static final String		propFile								= "TachoRead.properties";

	private final SimpleDateFormat	sdf										= new SimpleDateFormat("d/M/yyyy");

	static {
		try {
			InputStream is = TachoRead.class.getResourceAsStream(TachoRead.propFile);
			if (is != null) {
				Properties p = new Properties();
				p.load(is);
				if (p.get(TachoRead.TC_SLEEP_TIME_NAME) != null) {
					TachoRead.TC_SLEEP_TIME = Integer.valueOf(p.getProperty(TachoRead.TC_SLEEP_TIME_NAME)).intValue();
				}
				if (p.get(TachoRead.TC_MAX_ERROR_NAME) != null) {
					TachoRead.TC_MAX_ERROR = Integer.valueOf(p.getProperty(TachoRead.TC_MAX_ERROR_NAME)).intValue();
				}
				if (p.get(TachoRead.TC_NO_RESPONSE_NAME) != null) {
					TachoRead.TC_NO_RESPONSE = Integer.valueOf(p.getProperty(TachoRead.TC_NO_RESPONSE_NAME)).intValue();
				}

				if (p.get(TachoRead.VU_SLEEP_TIME_NAME) != null) {
					TachoRead.VU_SLEEP_TIME = Integer.valueOf(p.getProperty(TachoRead.VU_SLEEP_TIME_NAME)).intValue();
				}
				if (p.get(TachoRead.VU_MAX_ERROR_NAME) != null) {
					TachoRead.VU_MAX_ERROR = Integer.valueOf(p.getProperty(TachoRead.VU_MAX_ERROR_NAME)).intValue();
				}
				if (p.get(TachoRead.VU_NO_RESPONSE_NAME) != null) {
					TachoRead.VU_NO_RESPONSE = Integer.valueOf(p.getProperty(TachoRead.VU_NO_RESPONSE_NAME)).intValue();
				}

				if ((p.get(TachoRead.DEBUG_NAME) != null) && (((String) p.get(TachoRead.DEBUG_NAME)).equalsIgnoreCase("true"))) {
					TachoRead.PKT_DEBUG = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int intValue(byte b) {
		return (b < 0) ? 256 + b : b;
	}

	private byte calculaCRC(byte[] b, int size) {
		if (size > b.length) {
			return 0x00;
		}
		int tot = 0;
		for (int i = 0; i <= size; i++) {
			tot += this.intValue(b[i]);
		}
		return (byte) (tot % 256);
	}

	private boolean compruebaEquals(byte[] b1, byte[] b2) {
		if (b1.length != b2.length) {
			return false;
		}
		return this.compruebaEquals(b1, b2, b1.length);
	}

	private boolean compruebaEquals(byte[] b1, byte[] b2, int length) {
		for (int i = 0, a = length; i < a; i++) {
			if (b1[i] != b2[i]) {
				return false;
			}
		}
		return true;
	}

	private byte[] creaCabecera(byte trep, byte[] fecha) {
		byte[] cab = null;

		if (trep != TachoRead.TREP_ACTIVIDADES) {
			cab = new byte[7];
		} else {
			cab = new byte[11];
			System.arraycopy(fecha, 0, cab, 6, 4);
		}
		System.arraycopy(TachoRead.CABECERA_PETICION, 0, cab, 0, TachoRead.CABECERA_PETICION.length);
		cab[5] = trep;
		if (trep == TachoRead.TREP_ACTIVIDADES) {
			cab[3] = (byte) 0x06;
		}
		cab[cab.length - 1] = this.calculaCRC(cab, cab.length - 2);
		return cab;
	}

	private byte[] calculaSgte(byte[] indice) {
		byte[] s = new byte[2];
		int i = Utiles.bytesToInt(indice);
		i++;
		s[0] = (byte) (i / 256);
		s[1] = (byte) (i % 256);
		return s;
	}

	private byte[] creaConfirmacion(byte sid, byte[] indice) {
		byte[] pkt = new byte[9];
		byte[] s = this.calculaSgte(indice);
		System.arraycopy(TachoRead.CABECERA_CONFIRMACION_SUBMENSAJE, 0, pkt, 0, TachoRead.CABECERA_CONFIRMACION_SUBMENSAJE.length);
		pkt[3] = 0x04;
		pkt[4] = (byte) 0x83;
		pkt[5] = sid;
		pkt[6] = s[0];
		pkt[7] = s[1];
		pkt[8] = this.calculaCRC(pkt, pkt.length - 2);
		return pkt;
	}

	private void procesaPkt(byte[] pkt, boolean continuacion) {
		int t = (pkt[3] != (byte) 0xFF) ? (Utiles.bytesToInt(new byte[] { pkt[3] }) - 2 - (continuacion ? 2 : 0)) : 251;
		byte[] d = new byte[t];
		System.arraycopy(pkt, 6 + ((pkt[3] != (byte) 0xFF) ? 0 + (continuacion ? 2 : 0) : 2), d, 0, t);
		// if (PKT_CONTENT_DEBUG) System.out.println("Datos "
		// +Utiles.arrayHexToStringConSeparacion(d));
		try {
			this.bos.write(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getErrorMessage(byte b) {

		if (b == TachoRead.ERROR_NO_ACEPTADO) {
			return "NO ACEPTADO";
		}
		if (b == TachoRead.ERROR_SERVICIO_NO_ADMITIDO) {
			return "SERVICIO NO ADMITIDO";
		}
		if (b == TachoRead.ERROR_SUBFUNCION_NO_ADMITIDA) {
			return "SUBFUNCION NO ADMITIDA";
		}
		if (b == TachoRead.ERROR_LONGITUD) {
			return "LONGITUD INCORRECTA";
		}
		if (b == TachoRead.ERROR_SECUENCIA) {
			return "SECUENCIA";
		}
		if (b == TachoRead.ERROR_PETICION) {
			return "PETICION";
		}
		if (b == TachoRead.ERROR_FALTA_RESPUESTA) {
			return "FALTA RESPUESTA";
		}
		if (b == TachoRead.ERROR_NO_DISPONIBLES) {
			return "NO DISPONIBLE";
		}
		if (b == TachoRead.ERROR_GENERAL) {
			return "GENERAL";
		}
		return "UNKNOWN";

	}

	PuertoSerie	ps			= null;
	byte[]		r			= null;
	byte[]		ultimoEnvio	= null;
	int			error		= 0;

	public void setPktDebug(boolean b) {
		TachoRead.PKT_DEBUG = b;
	}

	private JTextArea	log	= null;

	public TachoRead(String serialPortName, JTextArea log) {
		try {
			this.portname = serialPortName;
			this.ps = new PuertoSerie(serialPortName);
		} catch (Exception e) {
			this.clearLog();
			this.appendLog("ERROR\n" + e.getMessage());
		}
		this.log = log;
	}

	public TachoRead() {
		super();
	}

	public void setPort(String portname) {
		try {
			if (this.ps != null) {
				this.ps.close();
			}

			this.ps = new PuertoSerie(portname);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean iniciarSesion() {
		if (this.ps == null) {
			return false;
		}

		this.clearLog();
		this.appendLog("PETICION INICIO COMUNICACION... ");
		this.ps.send(TachoRead.PETICION_INICIO_COMUNICACION);

		try {
			Thread.sleep(TachoRead.TIME_TO_SLEEP_BASIC);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.r = this.ps.getResponse();

		if (!this.compruebaEquals(TachoRead.RP_PETICION_INICIO_COMUNICACION, this.r)) {
			this.appendLog(" ERROR\n");
			return false;
		}

		this.appendLog("  OK\nPETICION INICIO SESION DIAGNOSTICO... ");
		this.ps.send(TachoRead.PETICION_INICIO_SESION);
		try {
			Thread.sleep(TachoRead.TIME_TO_SLEEP_BASIC);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.r = this.ps.getResponse();
		if (!this.compruebaEquals(TachoRead.RP_PETICION_INICIO_SESION, this.r)) {
			this.appendLog(" ERROR");
			return false;
		}

		this.appendLog("  OK\nENVIO PETICION... ");

		this.ps.send(TachoRead.ENVIO_PETICION);
		try {
			Thread.sleep(TachoRead.TIME_TO_SLEEP_BASIC);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.r = this.ps.getResponse();
		if (!this.compruebaEquals(TachoRead.RP_ENVIO_PETICION, this.r)) {
			this.appendLog(" ERROR\n");
			return false;
		}
		this.appendLog("  OK\n");
		return true;
	}

	protected boolean cerrarSesion() {
		this.appendLog("\n\nPETICION SALIDA TRANSFERENCIA... ");
		this.ps.send(TachoRead.PETICION_SALIDA_TRANSFERENCIA);

		try {
			Thread.sleep(TachoRead.TIME_TO_SLEEP_BASIC);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.r = this.ps.getResponse();
		if (!this.compruebaEquals(TachoRead.RP_PETICION_SALIDA_TRANSFERENCIA, this.r)) {
			this.appendLog(" ERROR\n");
			return false;
		}

		this.appendLog("  OK\nPETICION INTERRUPCION COMUNICACION... ");
		this.ps.send(TachoRead.PETICION_INTERRUPCION_COMUNICACION);

		try {
			Thread.sleep(TachoRead.TIME_TO_SLEEP_BASIC);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.r = this.ps.getResponse();

		if (!this.compruebaEquals(TachoRead.RP_PETICION_INTERRUPCION_COMUNICACION, this.r)) {
			this.appendLog(" ERROR\n");
			return false;
		}
		this.appendLog("  OK\n\n");
		return true;
	}

	private String trepName(byte trep) {
		if (trep == ((byte) 0x01)) {
			return "Resumen";
		}
		if (trep == ((byte) 0x02)) {
			return "Actividades";
		}
		if (trep == ((byte) 0x03)) {
			return "Incidentes";
		}
		if (trep == ((byte) 0x04)) {
			return "Velocidad";
		}
		if (trep == ((byte) 0x05)) {
			return "Técnicos";
		}
		if (trep == ((byte) 0x06)) {
			return "Transferencia Tarjeta";
		}
		return "Unknown";
	}

	private String trepNameDownload(byte trep, byte[] date) {
		StringBuffer sb = new StringBuffer();

		sb.append("Descargando datos: ");
		sb.append(this.trepName(trep));
		if (date != null) {
			Calendar c = new GregorianCalendar();
			c.setTime(new java.util.Date((long) Utiles.bytesToInt(date) * 1000));
			c.add(Calendar.DAY_OF_YEAR, -1);
			sb.append(" Dia: " + this.sdf.format(c.getTime()));
		}
		sb.append("\n");
		return sb.toString();
	}

	private void downloadTREP(byte trep, byte[] date) {
		boolean happenedError = false;
		this.error = 0;

		// Asignar valores
		int me = 0;
		int ts = 0;
		int nr = 0;

		if (trep == TachoRead.TREP_TRANSFERENCIA) {
			me = TachoRead.TC_MAX_ERROR;
			ts = TachoRead.TC_SLEEP_TIME;
			nr = TachoRead.TC_NO_RESPONSE;
		} else {
			me = TachoRead.VU_MAX_ERROR;
			ts = TachoRead.VU_SLEEP_TIME;
			nr = TachoRead.VU_NO_RESPONSE;
		}

		this.appendLog(this.trepNameDownload(trep, date));

		if (TachoRead.PKT_DEBUG) {
			System.out.println("\n\nTREP " + Utiles.arrayHexToStringConSeparacion(new byte[] { trep }));
		}
		if (TachoRead.PKT_DEBUG) {
			if (date != null) {
				System.out.println("Fecha Real: " + new java.util.Date((long) Utiles.bytesToInt(date) * 1000));
			}
		}
		if (TachoRead.PKT_DEBUG) {
			System.out.println("\n");
		}

		this.ultimoEnvio = this.creaCabecera(trep, date);
		this.ps.send(this.ultimoEnvio);
		if (TachoRead.PKT_DEBUG) {
			System.out.println("--> " + Utiles.arrayHexToStringConSeparacion(this.ultimoEnvio) + "\n");
		}
		boolean continuacion = false;
		while (true) {
			if (this.error >= me) {
				this.appendLog("TO MUCH ERROR");
				return;
			}
			int i = 0;
			do {
				try {
					Thread.sleep(happenedError ? 3 * ts : 1 * ts);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.r = this.ps.getResponse();
				i++;
				if (TachoRead.PKT_DEBUG) {
					if (i != 1) {
						System.out.print("*");
					}
				}
			} while ((this.r.length == 0) && (i != nr));
			
			if (i == nr) {
				if (TachoRead.PKT_DEBUG) {
					this.appendLog("NO RESPONSE ERROR\n");
				}
				return;
			}
			if (TachoRead.PKT_DEBUG) {
				System.out.print("<-- ");
			}
			if (TachoRead.PKT_CONTENT_DEBUG) {
				System.out.print(Utiles.arrayHexToStringConSeparacion(this.r));
			}
			if (TachoRead.PKT_DEBUG) {
				System.out.print(" ");
			}

			// Comprobamos el CRC
			if (this.r[this.r.length - 1] != this.calculaCRC(this.r, this.r.length - 2)) {
				happenedError = true;
				if (TachoRead.PKT_DEBUG) {
					System.out.print("CRC: ERROR. Repetimos peticion\n");
				}
				this.ps.send(this.ultimoEnvio);
				this.error++;
				continue;
			} else if (TachoRead.PKT_DEBUG) {
				System.out.print("CRC: OK ");
			}

			// Comprobamos no hubo error
			if (this.compruebaEquals(this.r, TachoRead.CABECERA_RESPUESTA_NEGATIVA, TachoRead.CABECERA_RESPUESTA_NEGATIVA.length)) {
				happenedError = true;
				if (TachoRead.PKT_DEBUG) {
					System.out.print("ERROR: " + this.getErrorMessage(this.r[6]) + ". Repetimos peticion\n");
				}

				if ((this.r[6] == TachoRead.ERROR_GENERAL) && (trep == TachoRead.TREP_ACTIVIDADES)) {
					this.appendLog("No hay datos en este dia\n\n");
					return;
				}

				this.ps.send(this.ultimoEnvio);
				this.error++;
				continue;
			} else if (TachoRead.PKT_DEBUG) {
				System.out.print(" ERROR: NO ");
			}

			if (this.r.length != ((Utiles.bytesToInt(new byte[] { this.r[3] })) + 5)) {
				happenedError = true;
				// System.out.println("LENGTH: ERROR. Repetimos peticion");
				this.ps.send(this.ultimoEnvio);
				this.error++;
				continue;
			} else if (TachoRead.PKT_DEBUG) {
				System.out.print(" LENGTH: OK ");
			}

			// Comprobamos el TREP
			if (this.r[5] != trep) {
				happenedError = true;
				// System.out.println(" TREP: ERROR. Repetimos peticion");
				this.ps.send(this.ultimoEnvio);
				this.error++;
				continue;
			} else if (TachoRead.PKT_DEBUG) {
				System.out.print(" TREP: OK ");
			}

			if (continuacion) {

				if ((this.r[6] != this.ultimoEnvio[6]) || (this.r[7] != this.ultimoEnvio[7])) {
					happenedError = true;
					if (TachoRead.PKT_DEBUG) {
						System.out.print(" SECUENCE: ERROR. Repetimos peticion\n");
					}
					this.ps.send(this.ultimoEnvio);
					continue;
				} else if (TachoRead.PKT_DEBUG) {
					System.out.print(" SECUENCE: OK ");
				}
			}

			if (!continuacion) {
				try {
					if (trep != TachoRead.TREP_TRANSFERENCIA) {
						this.bos.write(new byte[] { 0x76, trep });
					}
					this.bos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			this.procesaPkt(this.r, continuacion);
			if (this.r[3] != (byte) 0xFF) {
				// Ultimo pkt
				happenedError = false;
				this.appendLog("\nDatos descargados\n\n");
				break;
			} else {
				happenedError = false;
				this.ultimoEnvio = this.creaConfirmacion(this.r[4], new byte[] { this.r[6], this.r[7] });
				if (TachoRead.PKT_DEBUG) {
					System.out.print("--> " + Utiles.arrayHexToStringConSeparacion(this.ultimoEnvio) + "\n");
				}
				this.ps.send(this.ultimoEnvio);
				if (!continuacion) {
					continuacion = true;
				}
			}
		}
	}

	private boolean velocityChange(byte[] pkt, int speed) {

		this.ps.send(pkt);
		try {
			Thread.sleep(TachoRead.TIME_TO_SLEEP_BASIC);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.r = this.ps.getResponse();

		if (!this.compruebaEquals(TachoRead.RP_VELOCIDAD_BAUDIOS, this.r)) {
			return false;
		}
		this.ps.send(TachoRead.VELOCIDAD_BAUDIOS_FASE_2);

		this.ps.changeSpeed(speed);
		return true;
	}

	public void cambioVelocidad() {
		if (this.velocityChange(TachoRead.PETICION_VERIFICAR_VELOCIDAD_115200Bd, 115200)) {
			this.appendLog("\n\nVELOCIDAD ESTABLECIDA A 115200\n\n");
			return;
		}
		if (this.velocityChange(TachoRead.PETICION_VERIFICAR_VELOCIDAD_57600Bd, 57600)) {
			this.appendLog("\n\nVELOCIDAD ESTABLECIDA A 57600\n\n");
			return;
		}
		if (this.velocityChange(TachoRead.PETICION_VERIFICAR_VELOCIDAD_38400Bd, 38400)) {
			this.appendLog("\n\nVELOCIDAD ESTABLECIDA A 38400\n\n");
			return;
		}
		if (this.velocityChange(TachoRead.PETICION_VERIFICAR_VELOCIDAD_19200Bd, 19200)) {
			this.appendLog("\n\nVELOCIDAD ESTABLECIDA A 19200\n\n");
			return;
		}
		this.appendLog("\n\nVELOCIDAD ESTABLECIDA A 9600\n\n");
	}

	public void readToFile(File f, Vector vt, Vector vdates) {
		if (vdates == null) {
			vdates = this.dates;
		}
		// Asignamos el fichero de salida
		try {
			this.bos = new BufferedOutputStream(new FileOutputStream(f));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		this.ps.changeSpeed(9600);
		if (!this.iniciarSesion()) {
			return;
		}
		this.cambioVelocidad();

		for (int i = 0, a = vt.size(); i < a; i++) {
			byte[] trep = (byte[]) vt.elementAt(i);
			if (trep[0] == TachoRead.TREP_ACTIVIDADES) {
				for (int j = 0, k = vdates.size(); j < k; j++) {
					this.downloadTREP(trep[0], (byte[]) vdates.elementAt(j));
				}
			} else {
				this.downloadTREP(trep[0], null);
			}
		}

		this.cerrarSesion();

		try {
			this.bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// try {
		// lectorXML2.doit(f.getName());
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// }

	}

	private void clearLog() {
		if (this.log != null) {
			this.log.setText("");
		}
	}

	private void appendLog(String text) {
		if (this.log != null) {
			this.log.setText(this.log.getText() + text);
		}
	}

	public void close() {
		this.ps.close();
	}

	public void setDates(Vector dates) {
		this.dates = dates;
	}

}
