package com.opentach.model.comm.vu;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Utiles {

	//
	// Para imprimir el array de bytes en formato hexadecimal
	//

	private static void imprimeCaracter(int car, StringBuffer sb) {
		if (car < 10) {
			sb.append(car);
		}
		if (car == 10) {
			sb.append("A");
		}
		if (car == 11) {
			sb.append("B");
		}
		if (car == 12) {
			sb.append("C");
		}
		if (car == 13) {
			sb.append("D");
		}
		if (car == 14) {
			sb.append("E");
		}
		if (car == 15) {
			sb.append("F");
		}
	}

	private static void printHexadecimal(byte impr, StringBuffer sb) {
		int bajo = impr & 0x0F;
		int alto = impr & 0xF0;
		alto = alto >> 4;
		Utiles.imprimeCaracter(alto, sb);
		Utiles.imprimeCaracter(bajo, sb);
	}

	/**
	 * Devuelve una cadena formada por los valores en hexadecimal de todos los
	 * bytes del array de entrada.
	 * 
	 * @param bytes
	 *            byte[]
	 * @return String
	 */

	public static String arrayHexToString(byte[] bytes) {
		return Utiles.arrayHexToS(bytes, bytes.length, false);

	}

	/**
	 * Devuelve una cadena formada por los valores en hexadecimal de los
	 * primeros tam bytes del array de entrada.
	 * 
	 * @param bytes
	 *            byte[]
	 * @param tam
	 *            int
	 * @return String
	 */

	public static String arrayHexToString(byte[] bytes, int tam) {
		return Utiles.arrayHexToS(bytes, tam, false);
	}

	private static String arrayHexToS(byte[] bytes, int tam, boolean opcion) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tam; i++) {
			Utiles.printHexadecimal(bytes[i], sb);
			if (opcion) {
				sb.append(" ");
			}
		}
		return sb.toString();

	}

	/**
	 * Devuelve una cadena formada por los valores en hexadecimal de los
	 * primeros tam bytes del array de entrada. Entre los valores de los bytes
	 * se introduce un espacio en blanco de separaci\'on.
	 * 
	 * @param bytes
	 *            byte[]
	 * @param tam
	 *            int
	 * @return String
	 */
	public static String arrayHexToStringConSeparacion(byte[] bytes, int tam) {
		return Utiles.arrayHexToS(bytes, tam, true);
	}

	/**
	 * Devuelve una cadena formada por los valores en hexadecimal del array de
	 * entrada. Entre los valores de los bytes se introduce un espacio en blanco
	 * de separaci\'on.
	 * 
	 * @param bytes
	 *            byte[]
	 * @return String
	 */
	public static String arrayHexToStringConSeparacion(byte[] bytes) {
		return Utiles.arrayHexToS(bytes, bytes.length, true);
	}

	/**
	 * Imprime por la salida est\'andard los valores en hexadecimal de un array
	 * de bytes. Entre dos valores inserta un espacio en blanco.
	 *
	 * @param bytes
	 *            byte[]
	 */
	public static void printArrayHex(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, tam = bytes.length; i < tam; i++) {
			Utiles.printHexadecimal(bytes[i], sb);
			sb.append(" ");
		}
	}

	/**
	 * Imprime en sb los valores en hexadecimal de un array de bytes. Entre dos
	 * valores inserta un espacio en blanco.
	 *
	 * @param bytes
	 *            byte[]
	 * @param sb
	 *            StringBuffer
	 */
	public static void printArrayHex(byte[] bytes, StringBuffer sb) {
		for (int i = 0, tam = bytes.length; i < tam; i++) {
			Utiles.printHexadecimal(bytes[i], sb);
			sb.append(" ");
		}
	}

	//
	// Para imprimir el array de bytes en formato binario
	//

	private static void printBinary(byte b, StringBuffer sb) {
		for (int sh = 7; sh >= 0; sh--) {
			sb.append((b >> sh) & 1);
		}
	}

	/**
	 * Devuelve una cadena formada por los valores en binario de todos los bytes
	 * del array de entrada.
	 * 
	 * @param bytes
	 *            byte[]
	 * @return String
	 */

	public static String arrayBinToString(byte[] bytes) {
		return Utiles.arrayBinToS(bytes, bytes.length, false);
	}

	/**
	 * Devuelve una cadena formada por los valores en binario de los primeros
	 * tam bytes del array de entrada.
	 * 
	 * @param bytes
	 *            byte[]
	 * @param tam
	 *            int
	 * @return String
	 */

	public static String arrayBinToString(byte[] bytes, int tam) {
		return Utiles.arrayBinToS(bytes, tam, false);
	}

	private static String arrayBinToS(byte[] bytes, int tam, boolean opcion) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tam; i++) {
			Utiles.printBinary(bytes[i], sb);
			if (opcion) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	/**
	 * Devuelve una cadena formada por los valores en binario de los primeros
	 * tam bytes del array de entrada. Entre los valores de los bytes se
	 * introduce un espacio en blanco de separaci\'on.
	 * 
	 * @param bytes
	 *            byte[]
	 * @param tam
	 *            int
	 * @return String
	 */
	public static String arrayBinToStringConSeparacion(byte[] bytes, int tam) {
		return Utiles.arrayBinToS(bytes, tam, true);
	}

	/**
	 * Devuelve una cadena formada por los valores en binario del array de
	 * entrada. Entre los valores de los bytes se introduce un espacio en blanco
	 * de separaci\'on.
	 * 
	 * @param bytes
	 *            byte[]
	 * @return String
	 */

	public static String arrayBinToStringConSeparacion(byte[] bytes) {
		return Utiles.arrayBinToS(bytes, bytes.length, true);
	}

	/**
	 * Imprime por la salida est\'andard los valores en binario de un array de
	 * bytes. Entre dos valores inserta un espacio en blanco.
	 *
	 * @param bytes
	 *            byte[]
	 */
	public static void printArrayBin(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, tam = bytes.length; i < tam; i++) {
			Utiles.printBinary(bytes[i], sb);
			sb.append(" ");
		}
	}

	/**
	 * Imprime en sb los valores en binario de un array de bytes. Entre dos
	 * valores inserta un espacio en blanco.
	 *
	 * @param bytes
	 *            byte[]
	 * @param sb
	 *            StringBuffer
	 */
	public static void printArrayBin(byte[] bytes, StringBuffer sb) {
		for (int i = 0, tam = bytes.length; i < tam; i++) {
			Utiles.printBinary(bytes[i], sb);
			sb.append(" ");
		}
	}

	//
	// convertir bytes[2] a int
	//

	/**
	 * Devuelve el valor int del array de bytes.
	 * 
	 * @param p
	 *            byte[]
	 * @return int
	 */
	public static int bytesToInt(byte[] p) {
		return Utiles.bytesToInt(p, p.length);
	}

	/**
	 * Devuelve el valor int de los primeros tam bytes del array.
	 * 
	 * @param p
	 *            byte[]
	 * @param tam
	 *            int
	 * @return int
	 */

	public static int bytesToInt(byte[] p, int tam) {
		int res = 0;
		int[] aux = new int[tam];
		for (int i = 0; i < tam; i++) {
			aux[i] = (p[i] < 0) ? 256 + p[i] : p[i];
		}
		for (int i = 0; i < tam; i++) {
			res = (res * 256) + aux[i];
		}
		return res;
	}

	/**
	 * Devuelve el valor BigInteger del array de bytes.
	 * 
	 * @param p
	 *            byte[]
	 * @return BigInteger
	 */
	public static BigInteger bytesToBigInteger(byte[] p) {
		return Utiles.bytesToBigInteger(p, p.length);
	}

	/**
	 * Devuelve el valor BigInteger de los primeros tam bytes del array de
	 * bytes.
	 * 
	 * @param p
	 *            byte[]
	 * @param tam
	 *            int
	 * @return BigInteger
	 */

	public static BigInteger bytesToBigInteger(byte[] p, int tam) {
		BigInteger bi = new BigInteger("0");
		int[] aux = new int[tam];
		int[] uno = new int[1];

		for (int i = 0; i < tam; i++) {
			aux[i] = (p[i] < 0) ? 256 + p[i] : p[i];
		}

		for (int i = 0; i < tam; i++) {
			bi = bi.shiftLeft(8);
			System.arraycopy(aux, i, uno, 0, 1);
			bi = bi.add(new BigInteger((Integer.valueOf(uno[0])).toString()));
		}

		return bi;
	}

	//
	// Comparar array bytes
	//

	/**
	 * Compara si dos arrays de bytes son iguales o no. Devuelve true si lo son.
	 * 
	 * @param b1
	 *            byte[]
	 * @param b2
	 *            byte[]
	 * @return boolean
	 */

	public static boolean arrayEquals(byte[] b1, byte[] b2) {
		if ((b1 == null) || (b2 == null) || (b1.length != b2.length)) {
			return false;
		}
		for (int i = 0, a = b1.length; i < a; i++) {
			if (b1[i] != b2[i]) {
				return false;
			}
		}
		return true;
	}

	//
	// Comparar patrones
	//
	/**
	 * Compara si el array datos esta formado por bytes de valor todosBytes.
	 * 
	 * @param datos
	 *            byte[]
	 * @param todosBytes
	 *            byte[]
	 * @return boolean
	 */
	public static boolean compruebaEquals(byte[] datos, byte[] todosBytes) {
		return Utiles.compruebaEquals(datos, todosBytes, todosBytes, datos.length);

	}

	/**
	 * Compara si los primeros tam bytes del array de datos estan formados por
	 * bytes de valor todosBytes.
	 * 
	 * @param datos
	 *            byte[]
	 * @param todosBytes
	 *            byte[]
	 * @param tam
	 *            int
	 * @return boolean
	 */
	public static boolean compruebaEquals(byte[] datos, byte[] todosBytes, int tam) {
		return Utiles.compruebaEquals(datos, todosBytes, todosBytes, tam);
	}

	/**
	 * Compara si el primer byte del array datos es igual a primer byte, y los
	 * demás bytes del array son iguales a restoBytes.
	 * 
	 * @param datos
	 *            byte[]
	 * @param primerByte
	 *            byte[]
	 * @param restoBytes
	 *            byte[]
	 * @return boolean
	 */
	public static boolean compruebaEquals(byte[] datos, byte[] primerByte, byte[] restoBytes) {
		return Utiles.compruebaEquals(datos, primerByte, restoBytes, datos.length);
	}

	/**
	 * Comparara si el primer byte del array datos es igual a primerByte y si
	 * los siguientes hasta una longitud de tam son iguales a restoBytes
	 * 
	 * @param datos
	 *            byte[]
	 * @param primerByte
	 *            byte[]
	 * @param restoBytes
	 *            byte[]
	 * @param tam
	 *            int
	 * @return boolean
	 */

	public static boolean compruebaEquals(byte[] datos, byte[] primerByte, byte[] restoBytes, int tam) {

		if (datos[0] != primerByte[0]) {
			return false;
		}
		if (datos.length < tam) {
			return false;
		}
		for (int i = 0; i < tam; i++) {
			if (datos[i] != restoBytes[0]) {
				return false;
			}
		}
		return true;
	}

	//
	// Convertir array a String
	//

	/**
	 * Convierte un array de bytes a una cadena formada por los caracteres ASCII
	 * de los valores de los bytes.
	 * 
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public static String arrayToString(byte[] b) {
		return Utiles.arrayToString(b, b.length);
	}

	/**
	 * Convierte los primeros len bytes de un array a una cadena formada por los
	 * caracteres ASCII de los valores de los bytes.
	 * 
	 * @param b
	 *            byte[]
	 * @param len
	 *            int
	 * @return String
	 */

	public static String arrayToString(byte[] b, int len) {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < len; i++) {
			if (b[i] < 0x20) {
				s.append(" ");
			} else {
				s.append((char) b[i]);
			}
		}
		return s.toString();
	}

	/**
	 * Retorna el numero de minutos desde la medianoche de la fecha que le
	 * introducimos
	 * 
	 * @param d
	 *            Date
	 * @return int
	 */

	public static int minutesFromMidnigth(Date d) {
		Calendar c = new GregorianCalendar();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		c.setTime(d);
		return c.get(Calendar.MINUTE) + (c.get(Calendar.HOUR_OF_DAY) * 60);
	}

	/**
	 * Retorna la fecha que se le pasa pero sin minutos ni segundos
	 * 
	 * @param d
	 *            Date
	 * @return Date
	 */
	public static Date dateWithoutHourMinutesAndSeconds(Date d) {
		Calendar c = new GregorianCalendar();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	/**
	 * A la fecha que se le pasa como paramametro le añade un dia
	 * 
	 * @param d
	 *            Date
	 * @return Date
	 */
	public static Date addOneDay(Date d) {
		return Utiles.addDays(d, 1);
	}

	public static Date addDays(Date d, int days) {
		Calendar c = new GregorianCalendar();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		c.setTime(d);
		c.add(Calendar.DAY_OF_MONTH, days);
		return c.getTime();
	}

	/**
	 *
	 * @param d
	 *            Date
	 * @param minutes
	 *            int
	 * @return Date
	 */
	public static Date beforeOneDay(Date d) {
		Calendar c = new GregorianCalendar();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		c.setTime(d);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}

	/**
	 * Devuelve la fecha más lo minutos
	 * 
	 * @param d
	 *            Date
	 * @param minutes
	 *            int
	 * @return Date
	 */
	public static Date addMinutes(Date d, int minutes) {
		Calendar c = new GregorianCalendar();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		c.setTime(d);
		c.add(Calendar.MINUTE, minutes);
		return c.getTime();
	}

	/**
	 * Devuelve d1 before d2 haciendo la comparacion sin segundos ni
	 * milisegundos
	 * 
	 * @param d1
	 *            Date
	 * @param d2
	 *            Date
	 * @return boolean
	 */
	public static boolean beforeEqualWithoutSeconds(Date d1, Date d2) {
		Calendar c1 = new GregorianCalendar();
		Calendar c2 = new GregorianCalendar();
		c1.setTimeZone(TimeZone.getTimeZone("GMT"));
		c2.setTimeZone(TimeZone.getTimeZone("GMT"));
		c1.setTime(d1);
		c2.setTime(d2);

		c1.set(Calendar.SECOND, 0);
		c1.set(Calendar.MILLISECOND, 0);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		if (c1.equals(c2)) {
			return true;
		}
		return c1.before(c2);
	}

	/**
	 * Devuelve d1 after d2 haciendo la comparacion sin segundos ni milisegundos
	 * 
	 * @param d1
	 *            Date
	 * @param d2
	 *            Date
	 * @return boolean
	 */

	public static boolean afterEqualWithoutSeconds(Date d1, Date d2) {
		Calendar c1 = new GregorianCalendar();
		Calendar c2 = new GregorianCalendar();
		c1.setTimeZone(TimeZone.getTimeZone("GMT"));
		c2.setTimeZone(TimeZone.getTimeZone("GMT"));
		c1.setTime(d1);
		c2.setTime(d2);

		c1.set(Calendar.SECOND, 0);
		c1.set(Calendar.MILLISECOND, 0);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);

		if (c1.equals(c2)) {
			return true;
		}
		return c1.after(c2);
	}

}
