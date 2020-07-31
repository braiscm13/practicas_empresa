package com.opentach.server.util.db;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.SQLStatement;
import com.ontimize.db.TableEntity;
import com.ontimize.gui.ConnectionOptimizer;
import com.ontimize.locator.EntityReferenceLocator;


public class OracleTableEntity extends TranslateTableEntity {

	private final static Logger	logger	= LoggerFactory.getLogger(OracleTableEntity.class);

	public OracleTableEntity(EntityReferenceLocator locator, DatabaseConnectionManager cm, int puerto) throws Exception {
		super(locator, cm, puerto);
	}

	public OracleTableEntity(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop,
			Properties aliasProp) throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}

	public void writeOracleBLOBStream(File file, String column, Hashtable cv, Connection con) throws Exception {
		this.writeOracleBLOBStream(file, column, cv, con, false);
	}

	public void writeOracleBLOBStream(File file, String column, Hashtable cv, Connection con, boolean gzip) throws Exception {
		FileInputStream isf = null;
		OutputStream bos = null;
		GZIPOutputStream gzos = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		String sql = null;
		Blob bCol = null;
		try {
			final Vector<Object> qcol = new Vector<Object>();
			qcol.add(column);
			final SQLStatement stSQLQS = SQLStatementBuilder.createSelectQuery(this.table, qcol, cv, new Vector());
			sql = stSQLQS.getSQLStatement() + " FOR UPDATE";
			final Vector<Object> valores = stSQLQS.getValues();
			final Connection conn = con;
			stmt = conn.prepareStatement(sql);
			for (int i = 0; i < valores.size(); i++) {
				stmt.setObject(i + 1, valores.get(i));
			}
			rset = stmt.executeQuery();
			if (rset.next()) {
				OracleTableEntity.logger.trace("Record for update found");
				bCol = rset.getBlob(column);
				bos = bCol.setBinaryStream(1);
				isf = new FileInputStream(file);
				final byte[] buffer = new byte[10240];
				int nread = 0; // Number of bytes read
				if (gzip) {
					OracleTableEntity.logger.trace("gzipping");
					gzos = new GZIPOutputStream(bos);
					while ((nread = isf.read(buffer)) != -1) {
						OracleTableEntity.logger.trace("writing {} bytes", nread);
						gzos.write(buffer, 0, nread); // Write to Blob
					}
					gzos.flush();
				} else {
					while ((nread = isf.read(buffer)) != -1) {
						OracleTableEntity.logger.trace("writing {} bytes", nread);
						bos.write(buffer, 0, nread); // Write to Blob
					}
				}
			} else {
				OracleTableEntity.logger.error("Blob not found for update {}", sql);
				for (final Object ob : valores) {
					OracleTableEntity.logger.error("{},", ob);
				}
			}
		} catch (final Exception e) { // Error reading de file->RollBack
			OracleTableEntity.logger.error("SQL: {}", sql);
			OracleTableEntity.logger.error(null, e);
			throw e;
		} finally {
			try {
				if (isf != null) {
					isf.close();
				}
				if (gzos != null) {
					gzos.close();
				}
				if (bos != null) {
					bos.close();
				}
				if (rset != null) {
					rset.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (bCol != null) {
					bCol.free();
				}
			} catch (final Exception e) {
			}
		}
	}

	public File readOracleBLOBStream(String column, Vector atributosValidos, Hashtable cv, Connection conn) throws Exception {
		InputStream blobIn = null;
		BufferedInputStream BblobIn = null;
		FileOutputStream fOut = null;
		BufferedOutputStream bOut = null;
		PreparedStatement stmt = null;
		File fTemporal = null;
		ResultSet rs = null;
		try {
			String tablaAUtilizar = this.table;
			if (this.queryTable != null) {
				tablaAUtilizar = this.queryTable;
			}

			final SQLStatement stSQL = SQLStatementBuilder.createSelectQuery(tablaAUtilizar, atributosValidos, cv, this.wildcardColumns, this.orderColumns,
					this.forceDistinct);
			final String consultaSQL = stSQL.getSQLStatement(); // / + " FOR
			// UPDATE";

			final Vector valores = stSQL.getValues();
			stmt = conn.prepareStatement(consultaSQL);

			for (int i = 0; i < valores.size(); i++) {
				TableEntity.setObject(i + 1, valores.get(i), stmt, this.truncateDates);
			}
			rs = stmt.executeQuery();
			if (rs.next()) {
				final Blob blob = rs.getBlob(column);
				if (blob != null) {
					try {
						blobIn = blob.getBinaryStream();
						BblobIn = new BufferedInputStream(blobIn);

						fTemporal = File.createTempFile(this.entityName, null);
						OracleTableEntity.logger.info("Archivo temporal: {}", fTemporal.toString());

						// Grabamos
						fOut = new FileOutputStream(fTemporal);
						bOut = new BufferedOutputStream(fOut);
						int aux = -1;
						while ((aux = BblobIn.read()) != -1) {
							bOut.write(aux);
						}
						bOut.flush();
						// conn.commit();
					} catch (final Exception ex) {
						fTemporal = null;
						conn.rollback();
						throw new Exception();
					} finally {
						if (bOut != null) {
							bOut.close();
						}
						if (BblobIn != null) {
							BblobIn.close();
						}
						if (blob != null) {
							blob.free();
						}
					}
				}
			}
		} catch (final SQLException e) {
			OracleTableEntity.logger.error(null, e);
		} catch (final Exception e) {
			OracleTableEntity.logger.error(null, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (final Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (final Exception e) {
				}
			}
		}
		return fTemporal;
	}

	public Timestamp getCDateTime(int sesionId) {
		return new Timestamp(System.currentTimeMillis());
	}

	@Override
	protected EntityResult insertOneToOne(Hashtable hValidAttributesValues, Hashtable hLocaleColumns, int autonumerical, Connection con, int sessionId, long t) throws Exception {

		EntityResult erResult = null;
		final Hashtable hValidAttributesMainTable = this.getSubGroupValues(hValidAttributesValues, this.principalColumnNames, sessionId);
		final Hashtable hValidAttributesMainTableWithoutLocale = this.processColumnsWithoutLocale(hValidAttributesMainTable);
		final SQLStatement stSQL = SQLStatementBuilder.getSQLStatementHandler(this.manager.getDatabase()).createInsertQuery(this.principalTable, hValidAttributesMainTableWithoutLocale);
		if (stSQL != null) {
			final String sqlQuery = stSQL.getSQLStatement();
			final Vector vValues = stSQL.getValues();
			erResult = this.executePreparedStatement(sqlQuery, vValues, con, sessionId);
		}
		if (erResult.getCode() != EntityResult.OPERATION_WRONG) {
			if (stSQL != null) {
				this.auditorLog.succesfulAccessSQL("INSERT", this.getUser(sessionId), this.entityName, stSQL.getSQLStatement(), stSQL.getValues());
			}
			final Hashtable hValidAttributesSecondaryTable = this.getSubGroupValues(hValidAttributesValues, this.secondaryColumnsNames, sessionId);
			Hashtable generatedKeys = null;
			// Put the key in the secondary
			// Autonumerical must be the first key

			if (this.autonumericalColumn != null) {
				// If more than one key exist, the index for the
				// autonumerical
				// key is in the main table keys vector
				if (this.principalKeyName.indexOf(this.autonumericalColumn) >= 0) {
					hValidAttributesSecondaryTable.put(this.secondaryKeyName.get(this.principalKeyName.indexOf(this.autonumericalColumn)), new Integer(autonumerical));
				}
			} else if ((this.generatedKeyList != null) && (this.generatedKeyList.size() > 0)) {
				generatedKeys = new Hashtable();
				for (final String currentKey : this.generatedKeyList) {
					final Object currentKeyValue = erResult.get(currentKey);
					generatedKeys.put(currentKey, currentKeyValue);
					if (currentKey != null) {
						hValidAttributesSecondaryTable.put(this.secondaryKeyName.get(this.principalKeyName.indexOf(currentKey)), currentKeyValue);
					}
				}
			} else {
				// If more than one key exist, then put all of them
				for (int i = 0; i < this.secondaryKeyName.size(); i++) {
					hValidAttributesSecondaryTable.put(this.secondaryKeyName.elementAt(i), hValidAttributesMainTable.get(this.principalKeyName.get(i)));
				}
			}

			final Hashtable hValidAttributesSecondaryTableWithoutLocale = this.processColumnsWithoutLocale(hValidAttributesSecondaryTable);
			final SQLStatement stSQLS = SQLStatementBuilder.getSQLStatementHandler(this.manager.getDatabase()).createInsertQuery(this.secondaryTable,
					hValidAttributesSecondaryTableWithoutLocale);
			if (stSQLS != null) {
				final String aqlQuery = stSQLS.getSQLStatement();
				final Vector vValues = stSQLS.getValues();
				erResult = this.executePreparedStatement(aqlQuery, vValues, true, con, sessionId);

				if (erResult.getCode() != EntityResult.OPERATION_WRONG) {
					this.auditorLog.succesfulAccessSQL("INSERT", this.getUser(sessionId), this.entityName, stSQLS.getSQLStatement(), stSQLS.getValues());
				} else {
					this.auditorLog.unsuccesfulAccessSQL("INSERT", this.getUser(sessionId), this.entityName);
				}
			}

			// if (this.autonumericalColumn != null) {
			// erResult.put(this.autonumericalColumn, new Integer(autonumerical));
			// } else if ((this.generatedKeyList != null) && (this.generatedKeyList.size() > 0)) {
			// for (String currentKey : this.generatedKeyList) {
			// Object currentKeyValue = generatedKeys.get(currentKey);
			// if (currentKey != null) {
			// erResult.put(currentKey, currentKeyValue);
			// }
			// }
			// }

			this.insertInLocaleTablesForOneToOne(hLocaleColumns, erResult, sessionId, con);
			OracleTableEntity.logger.trace("Time until insert multilanguage columns: {}", System.currentTimeMillis() - t);

		} else {
			this.auditorLog.unsuccesfulAccessSQL("INSERT", this.getUser(sessionId), this.entityName);
		}
		return erResult;
	}

	@Override
	public EntityResult executePreparedStatement(String sqlSentence, Vector values, Connection con, int sessionId) throws Exception {
		return this.executePreparedStatement(sqlSentence, values, false, con, sessionId);
	}

	public EntityResult executePreparedStatement(String sqlSentence, Vector values, boolean skipGeneratedKeys, Connection con, int sessionId) throws Exception {
		// Execute the sentence with the connection
		final long t = System.currentTimeMillis();
		final EntityResult erResult = new EntityResult();
		PreparedStatement st = null;

		try {
			OracleTableEntity.logger.trace("Query:{}", sqlSentence);
			OracleTableEntity.logger.trace("NaticeSQL: {}", con.nativeSQL(sqlSentence));

			if (!skipGeneratedKeys && (this.generatedKeyList != null) && (this.generatedKeyList.size() > 0) && sqlSentence.startsWith(SQLStatementBuilder.INSERT_INTO)) {
				st = con.prepareStatement(sqlSentence, this.generatedKeyList.toArray(new String[this.generatedKeyList.size()]));
			} else {
				st = con.prepareStatement(sqlSentence);
			}

			for (int i = 0; i < values.size(); i++) {
				TableEntity.setObject(i + 1, values.get(i), st, this.truncateDates);
			}

			OracleTableEntity.logger.trace("Prepared statement time and setObject: {}", System.currentTimeMillis() - t);

			if (this.fetchSize > 0) {
				st.setFetchSize(this.fetchSize);
			}
			final boolean esResultSet = st.execute();
			final long endTime = System.currentTimeMillis();

			this.statementTmeLogger.checkStatementTime(t, endTime, this.auditorLog.createSqlStatement(sqlSentence, values), null);
			OracleTableEntity.logger.trace("{} execute time: {} ms", sqlSentence.substring(0, sqlSentence.trim().indexOf(" ") + 1), endTime - t);
			// Difference between query and the other actions
			if (esResultSet) {

				erResult.setType(EntityResult.DATA_RESULT);
				this.resultSetToEntityResult(st.getResultSet(), erResult, this.columnNames);
				// Checks if a connection optimizer exists
				if ((sessionId > 0) && (this.locator instanceof ConnectionOptimizer)) {
					final int threshold = ((ConnectionOptimizer) this.locator).getDataCompressionThreshold(sessionId);
					erResult.setCompressionThreshold(threshold);

					OracleTableEntity.logger.trace("Set compression threshold  in EntityResult to : {} for sessionId: {}", threshold, sessionId);
				}
			} else {
				try {
					final int i = st.getUpdateCount();
					erResult.setType(EntityResult.NODATA_RESULT);
					if (i == 0) {
						erResult.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
						erResult.setMessage(TableEntity.M_IT_HAS_NOT_CHANGED_ANY_RECORD);
					} else {
						if (!skipGeneratedKeys && (this.generatedKeyList != null) && (this.generatedKeyList.size() > 0) && sqlSentence.trim()
								.startsWith(SQLStatementBuilder.INSERT_INTO.trim())) {
							this.generatedKeysToEntityResult(st.getGeneratedKeys(), erResult, this.generatedKeyList);
							erResult.setType(EntityResult.DATA_RESULT);
						}
					}
				} catch (final Exception e) {
					OracleTableEntity.logger.debug(null, e);
					throw e;
				}

			}
			return erResult;
		} catch (final Exception e) {
			OracleTableEntity.logger.debug(null, e);
			throw e;
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (final Exception e) {
				OracleTableEntity.logger.trace(null, e);
			}
		}
	}
}