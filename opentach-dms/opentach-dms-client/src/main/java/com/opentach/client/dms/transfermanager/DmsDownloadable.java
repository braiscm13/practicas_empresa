package com.opentach.client.dms.transfermanager;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.dms.DMSNaming;
import com.opentach.common.dms.DmsException;
import com.opentach.common.dms.IDMSService;

/**
 * The Class DmsDownloadTransferable.
 */
public class DmsDownloadable extends AbstractDmsTransferable {

	/** The id version file. */
	private final Serializable	idVersionFile;
	private final Path			targetFile;

	/**
	 * Instantiates a new dms download transferable.
	 *
	 * @param idVersionFile
	 *            the id version file
	 * @param targetFile
	 *            the target file
	 * @throws Exception
	 */
	public DmsDownloadable(Serializable idVersionFile, Path targetFile) throws Exception {
		super(null, null);
		this.idVersionFile = idVersionFile;
		this.targetFile = targetFile;
		this.init();
	}

	/**
	 * Instantiates a new dms download transferable.
	 *
	 * @param idVersionFile
	 *            the id version file
	 * @param targetFile
	 *            the target file
	 * @throws Exception
	 */
	public DmsDownloadable(Serializable idVersionFile, Path targetFile, String name, Long size) throws Exception {
		super(name, size);
		this.idVersionFile = idVersionFile;
		this.targetFile = targetFile;
		this.init();
	}

	/**
	 * Inits the name and size.
	 *
	 * @throws Exception
	 */
	protected void init() throws Exception {
		if ((this.getName() == null) || (this.getSize() == null)) {
			EntityResult er = this.getDMSService().fileVersionQuery(this.idVersionFile,
					EntityResultTools.attributes(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE, DMSNaming.DOCUMENT_FILE_NAME), this.getSessionId());
			this.setSize(((List<Number>) er.get(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE)).get(0).longValue());
			this.setName(((List<String>) er.get(DMSNaming.DOCUMENT_FILE_NAME)).get(0));
		}
	}

	/**
	 * Gets the id version file.
	 *
	 * @return the id version file
	 */
	public Serializable getIdVersionFile() {
		return this.idVersionFile;
	}

	/**
	 * Gets the target file.
	 *
	 * @return the target file
	 */
	public Path getTargetFile() {
		return this.targetFile;
	}

	protected IDMSService getDMSService() throws DmsException {
		try {
			UserInfoProvider ocl = (UserInfoProvider) ApplicationManager.getApplication().getReferenceLocator();
			return ocl.getRemoteService(IDMSService.class);
		} catch (Exception error) {
			throw new DmsException(error);
		}
	}

	private int getSessionId() throws DmsException {
		try {
			return ApplicationManager.getApplication().getReferenceLocator().getSessionId();
		} catch (Exception error) {
			throw new DmsException(error);
		}
	}
}
