package com.opentach.model.comm.vu;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

public class PuertoSerie implements SerialPortEventListener {
	SerialPort					sp;
	InputStream					is			= null;
	OutputStream				os			= null;
	ByteArrayOutputStream		ba			= new ByteArrayOutputStream();

	private final int			DATABITS	= SerialPort.DATABITS_8;
	private final int			STOPBITS	= SerialPort.STOPBITS_1;
	private final int			PARITY		= SerialPort.PARITY_EVEN;

	public static final String	TACOGRAFO	= "Tacografo";

	public static Vector porSerialList() {
		Vector<Object> v = new Vector<Object>();
		try {
			Enumeration<Object> portList = CommPortIdentifier.getPortIdentifiers();
			boolean in = true;
			while (portList.hasMoreElements()) {
				if (in) {
					v.add("- ");
					in = false;
				}
				String n = ((CommPortIdentifier) portList.nextElement()).getName();
				if (n.indexOf("COM") != -1) {
					v.add(n);
				}
			}
		} catch (NoClassDefFoundError ncex) {
			System.err.println("No se han encontrado puertos disponibles en el sistema");
		} catch (Exception ex) {
			System.err.println("" + ex.getMessage());
		}

		return v;
	}

	private CommPortIdentifier	pid	= null;

	public PuertoSerie(String serialPortName) throws Exception {
		try {
			Enumeration<Object> portList = CommPortIdentifier.getPortIdentifiers();

			while (portList.hasMoreElements()) {
				CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();

				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					if (((portId != null) && (portId.getName().equals(serialPortName)))) {
						try {
							this.pid = portId;
							this.sp = (SerialPort) portId.open(PuertoSerie.TACOGRAFO, 2000);
							this.is = this.sp.getInputStream();
							this.os = this.sp.getOutputStream();
							this.sp.notifyOnDataAvailable(true);
							this.sp.setSerialPortParams(9600, this.DATABITS, this.STOPBITS, this.PARITY);
							this.sp.addEventListener(this);
						} catch (PortInUseException e) {
							System.out.println(portId.getName() + " ESTA SIENDO UTILIZADO POR OTRA APLICACION");
							System.out.println(e);
							throw e;
						} catch (Exception e) {
							e.printStackTrace();
							throw e;
						}
						break;
					}
				}
			}
		} catch (NoClassDefFoundError nex) {
			System.err.println("No se han encontrado puertos disponibles en el sistema.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void changeSpeed(int speed) {
		try {
			this.sp.close();
			this.sp = null;
			this.sp = (SerialPort) this.pid.open(PuertoSerie.TACOGRAFO, 2000);
			this.is = this.sp.getInputStream();
			this.os = this.sp.getOutputStream();
			this.sp.notifyOnDataAvailable(true);
			this.sp.setSerialPortParams(speed, this.DATABITS, this.STOPBITS, this.PARITY);
			this.sp.addEventListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serialEvent(SerialPortEvent event) {

		switch (event.getEventType()) {

		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;

		case SerialPortEvent.DATA_AVAILABLE:

			byte[] readBuffer = new byte[20];

			try {
				while (this.is.available() > 0) {
					int numBytes = this.is.read(readBuffer);
					this.ba.write(readBuffer, 0, numBytes);
				}
			} catch (IOException e) {
			}
			break;
		}
	}

	public byte[] getResponse() {
		byte[] b = this.ba.toByteArray();
		this.ba.reset();
		return b;
	}

	public void send(byte[] b) {
		try {
			this.os.write(b);
		} catch (Exception e) {
			// System.err.println("" + e.getMessage() + ": " +
			// e.getStackTrace()[0]);
		}
	}

	public void close() {
		if (this.sp != null) {
			this.sp.close();
		}
	}

}
