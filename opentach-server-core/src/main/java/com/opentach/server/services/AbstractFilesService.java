package com.opentach.server.services;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Map;

import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.interfaces.IFilesService;
import com.opentach.server.entities.EPreferenciasServidor;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

/**
 * Default semi implementation to file services. Will be implementend in other classes like "CloudFilesService" o "JournalAtachmentFilesService"
 */
public abstract class AbstractFilesService extends UAbstractService implements IFilesService {

	protected static final String	E_MANDATORY_VALUES		= "E_MANDATORY_VALUES";
	protected static final String	E_INVALID_USER			= "E_INVALID_USER";
	protected static final String	E_FILENAME_MANDATORY	= "E_FILENAME_MANDATORY";
	protected static final String	E_INPUTSTREAM_MANDATORY	= "E_INPUTSTREAM_MANDATORY";
	protected static final String	E_FILE_ID_MANDATORY		= "E_FILE_ID_MANDATORY";
	protected static final String	E_FILE_NOT_FOUND		= "E_FILE_NOT_FOUND";

	private static final String		EPREFERENCIASSERVIDOR	= "EPreferenciasServidor";

	public AbstractFilesService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	@Override
	public Object uploadFile(final Map<Object, Object> values, final int sessionId, final InputStream inputStream) throws Exception {
		CheckingTools.failIfNull(values, AbstractFilesService.E_MANDATORY_VALUES);
		CheckingTools.failIfNull(inputStream, AbstractFilesService.E_INPUTSTREAM_MANDATORY);

		return new OntimizeConnectionTemplate<Object>() {
			@Override
			protected Object doTask(Connection con) throws UException {
				try {
					return AbstractFilesService.this.uploadFile(values, sessionId, con, inputStream);
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), false);
	}

	protected abstract Object uploadFile(Map<Object, Object> values, int sessionId, Connection con, final InputStream inputStream) throws Exception;

	@Override
	public InputStream downloadFile(final Object fileId, final int sessionId) throws Exception {
		CheckingTools.failIfNull(fileId, AbstractFilesService.E_FILE_ID_MANDATORY);
		CheckingTools.failIf(!this.getLocator().hasSession(sessionId), AbstractFilesService.E_INVALID_USER);

		return new OntimizeConnectionTemplate<InputStream>() {

			@Override
			protected InputStream doTask(Connection con) throws UException {
				try {
					return AbstractFilesService.this.downloadFile(fileId, sessionId, con);
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), false);
	}

	protected abstract InputStream downloadFile(Object fileId, int sessionId, Connection con) throws Exception;

	@Override
	public void deleteFile(final Object fileId, final int sessionId) throws Exception {
		CheckingTools.failIfNull(fileId, AbstractFilesService.E_FILE_ID_MANDATORY);
		CheckingTools.failIf(!this.getLocator().hasSession(sessionId), AbstractFilesService.E_INVALID_USER);

		new OntimizeConnectionTemplate<Void>() {

			@Override
			protected Void doTask(Connection con) throws UException {
				try {
					AbstractFilesService.this.deleteFile(fileId, sessionId, con);
				} catch (Exception ex) {
					throw new UException(ex);
				}
				return null;
			}
		}.execute(this.getConnectionManager(), false);

	}

	protected abstract void deleteFile(Object fileId, int sessionId, Connection con) throws Exception;

	protected Path getRootPath(Connection con) throws Exception {
		EPreferenciasServidor entity = (EPreferenciasServidor) this.getEntity(AbstractFilesService.EPREFERENCIASSERVIDOR);
		String value = entity.getValue(this.getRootFilesPath(), this.getSessionId(-1, entity), con);
		Path path = Paths.get(value);
		return path;
	}

	protected abstract String getRootFilesPath();
}
