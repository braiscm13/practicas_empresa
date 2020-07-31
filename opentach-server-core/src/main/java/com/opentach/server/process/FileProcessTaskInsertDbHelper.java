package com.opentach.server.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.Chronometer;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.opentach.common.tacho.data.Conductor;
import com.opentach.server.process.entityInfo.AbstractClassEntityInfo;
import com.opentach.server.process.entityInfo.ClassEntityInfoFactory;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcToEntityResultTemplate;
import com.utilmize.server.tools.sqltemplate.UpdateJdbcTemplate;
import com.utilmize.tools.exception.UException;

/**
 * Tarea encargada de inseratr datos en BD.
 *
 * @company www.imatia.com
 */
public class FileProcessTaskInsertDbHelper {

	private static final Logger	logger				= LoggerFactory.getLogger(FileProcessTaskInsertDbHelper.class);
	private static final int	UNIQUE_CONSTRAINTS	= 1;

	public List<String> execute(final Map data, final String user, ErrorLogTableManagementThread errorLogTableThread, final Connection conn) throws Exception {
		List<AbstractClassEntityInfo<?>> entitiesInfo = ClassEntityInfoFactory.getEntitiesInfo();
		List<String> errors = new ArrayList<String>();
		Chronometer chrono = new Chronometer().start();
		for (AbstractClassEntityInfo entityInfo : entitiesInfo) {
			List listobjs = (List) data.get(entityInfo.getObjectClassName());
			if ((listobjs != null) && !listobjs.isEmpty()) {
				List<String> localErrors = this.addData(listobjs, entityInfo, user, errorLogTableThread, conn);
				errors.addAll(localErrors);
			}

			if (listobjs != null) {
				FileProcessTaskInsertDbHelper.logger.info(" \t\t - {}\t\tsize:{}\t\ttime:{} ms",
						entityInfo.getObjectClassName().substring(entityInfo.getObjectClassName().lastIndexOf(".") + 1), listobjs.size(), chrono.elapsedMs());
			}
		}
		return errors;
	}

	/**
	 * Inseta los objetos de la lista en la BD
	 *
	 * @param cei
	 */
	public List<String> addData(final List listobjs, final AbstractClassEntityInfo cei, final String user, ErrorLogTableManagementThread errorLogTableThread, final Connection conn)
			throws Exception {

		final List<String> errors = new ArrayList<>();
		try {
			if (listobjs.isEmpty()) {
				return errors;
			}

			String errorLogTableName = null;
			try {
				errorLogTableName = errorLogTableThread.createErrorLogTable(cei.getTabla()); // in other conn
				new UpdateJdbcTemplate() {
					@Override
					protected Integer innerExecuteUpdate(PreparedStatement pstmt, Object... parameters) throws SQLException {
						int batchSize = listobjs.size() / 3;
						if (batchSize <= 0) {
							batchSize = 1;
						}
						Date tstamp = new Date();
						int count = 0;
						for (Object object : listobjs) {
							if (cei.passFilter(object)) {
								count++;
								// Para cada objeto establezco los valores dle
								// statement y lo ejecuto.
								Map<String, Object> cv = cei.getCV(object);
								// La columna de autonumerico se autonicrementa.
								MapTools.safePut(cv, cei.getAutonumericCol(), cei.getCountAndIncrement());
								MapTools.safePut(cv, cei.getUserCol(), user);
								MapTools.safePut(cv, cei.getTimeStampCol(), tstamp);

								// Para cada una de las columnas que tengo que
								// insertar y en el orden en el que se genero el sql
								String[] cols = ((List<String>) cei.getColumns()).toArray(new String[0]);
								Object idConductor = "";
								Timestamp expiredCardDate = null;

								for (int idx = 0; idx < cols.length; idx++) {
									String colname = cols[idx];
									Object value = cv.get(colname);
									// FileProcessTaskInsertDbHelper.logger.error("{} = {}", colname, value);
									this.setStatementParameters(idx + 1, pstmt, value);

									if ((value != null) && Conductor.class.getName().equalsIgnoreCase(object.getClass().getName())) {
										if ("IDCONDUCTOR".equals(colname)) {
											idConductor = value;
										} else if ("EXPIRED_DATE_TRJCONDU".equals(colname)) {
											expiredCardDate = FileProcessTaskInsertDbHelper.this.convertToTimestamp(value);
										}
									}
								}

								if (Conductor.class.getName().equalsIgnoreCase(object.getClass().getName())) {
									FileProcessTaskInsertDbHelper.this.updateDriverInfo(idConductor, expiredCardDate, conn);
								}
								pstmt.addBatch();
								if ((count % batchSize) == 0) {
									pstmt.executeBatch();
								}
							}
						}
						pstmt.executeBatch();
						return Integer.valueOf(1);
					}
				}.execute(conn, cei.getSQLPattern(errorLogTableName));
			} finally {
				this.retrieveErrors(errorLogTableName, cei, conn, errors);
				errorLogTableThread.addTableToDrop(errorLogTableName);
			}
		} catch (Exception ex) {
			FileProcessTaskInsertDbHelper.logger.error(null, ex);
			errors.add(ex.getMessage());
		}
		return errors;
	}

	final static AtomicLong posfix = new AtomicLong(0);

	private void retrieveErrors(final String errorLogTableName, final AbstractClassEntityInfo cei, Connection conn, List<String> errors) {
		if (errorLogTableName == null) {
			return;
		}
		try {
			EntityResult res = new QueryJdbcToEntityResultTemplate().execute(conn, "SELECT * from " + errorLogTableName + " where ORA_ERR_NUMBER$<>?",
					FileProcessTaskInsertDbHelper.UNIQUE_CONSTRAINTS);
			int nregs = res.calculateRecordNumber();
			if (nregs > 0) {
				// TODO append errors to list
				FileProcessTaskInsertDbHelper.logger.error("Errors batch inserting in {}:\n{}", cei.getTabla(), EntityResultTools.toString(res));
			}
		} catch (SQLException err) {
			if (err.getErrorCode() == 942) {
				FileProcessTaskInsertDbHelper.logger.warn("La tabla de errores ya no existia");
			} else {
				FileProcessTaskInsertDbHelper.logger.error(null, err);
			}
		} catch (Exception error) {
			FileProcessTaskInsertDbHelper.logger.error(null, error);
		}

	}

	private void updateDriverInfo(final Object idConductor, final Timestamp expiredCardDate, final Connection conn) {
		try {
			new QueryJdbcTemplate<Void>() {
				@Override
				protected Void parseResponse(ResultSet rs) throws UException {
					try {
						if (rs.next()) {
							Timestamp o = FileProcessTaskInsertDbHelper.this.convertToTimestamp(rs.getObject(1));
							if ((expiredCardDate != null) && ((o == null) || (o.getTime() < expiredCardDate.getTime()))) {
								new UpdateJdbcTemplate().execute(conn, "UPDATE CDCONDUCTORES_EMP SET EXPIRED_DATE_TRJCONDU = ? WHERE IDCONDUCTOR = ?", expiredCardDate,
										idConductor);
							}
						}
						return null;
					} catch (Exception err) {
						throw new UException(err);
					}
				}
			}.execute(conn, "SELECT EXPIRED_DATE_TRJCONDU FROM CDCONDUCTORES_EMP WHERE IDCONDUCTOR = ?", idConductor);
		} catch (Exception ex) {
			FileProcessTaskInsertDbHelper.logger.error(null, ex);
		}
	}

	private Timestamp convertToTimestamp(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Timestamp) {
			return (Timestamp) value;
		}
		if (value instanceof java.sql.Date) {
			return new Timestamp(((java.sql.Date) value).getTime());
		}
		if (value instanceof java.util.Date) {
			return new Timestamp(((java.util.Date) value).getTime());
		}
		FileProcessTaskInsertDbHelper.logger.error("No puedo convertir a timestamp un " + value.getClass().getName());
		return null;
	}

}