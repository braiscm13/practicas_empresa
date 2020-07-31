package com.opentach.server.filereception;

import java.nio.file.Path;
import java.util.Hashtable;

import com.ontimize.jee.common.tools.MapTools;
import com.opentach.common.OpentachFieldNames;

/**
 * The Class InOutFileLog.
 */
public class InOutFileLog {

	/** The insert file into bd. */
	public static String					INSERT_FILE_INTO_BD		= "BD";
	public static String					INSERT_FILE_INTO_HD		= "HD";
	public static String					INSERT_FILE_INTO_BDHD	= "BDHD";

	/** Identificador del registro */
	private final String					id;
	/** Claves-valor del registro asociado al fichero */
	private final Hashtable<Object, Object>	cv;
	/** Fichero a transferir */
	private final Path						file;
	/** Indica si se asocio el fichero a un contrato */
	private boolean							assignedToContract;
	/** Id del fichero */
	private Number							idFichero;
	private String							message;

	public InOutFileLog(String id, Hashtable<Object, Object> cv, Path file) {
		this.id = id;
		this.cv = cv;
		this.file = file;
		this.idFichero = null;
		this.assignedToContract = false;
	}

	public String getId() {
		return this.id;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}

	public Hashtable<Object, Object> getKeysValues() {
		return this.cv;
	}

	public Path getFile() {
		return this.file;
	}

	public String getMessage() {
		return this.message;
	}

	public void setAssignedToContract(boolean assignedToContract) {
		this.assignedToContract = assignedToContract;
	}

	public boolean isAssignedToContract() {
		return this.assignedToContract;
	}

	public Number getIdFichero() {
		return this.idFichero;
	}

	public void setIdFichero(Number idFichero) {
		this.idFichero = idFichero;
		MapTools.safePut(this.cv, OpentachFieldNames.IDFILE_FIELD, idFichero);
	}
}