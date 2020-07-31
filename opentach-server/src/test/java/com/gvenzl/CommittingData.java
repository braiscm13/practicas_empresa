package com.gvenzl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;

/**
 * This class demonstrates the difference in throughput when
 * committing data differently to the Oracle Database.
 * @author gvenzl
 *
 */
public class CommittingData {

	/**
	 * Name of the test table.
	 */
	private static String TESTTABLE = "COMMITDATA";
	/**
	 * Fixed amount of iterations.
	 */
	private static int ITERATIONS = 100000;
	/**
	 * Error logging table for error logging.
	 */
	private static String ERROR_LOG_TABLE = "ERR_LOG_TABLE";
	/**
	 * Append hint for direct path inserts.
	 */
	private static String APPEND = "";

	/**
	 * Input parameter.
	 */
	private static String host = "";
	/**
	 * Input parameter.
	 */
	private static int port = 0;
	/**
	 * Input parameter.
	 */
	private static String serviceName = "";
	/**
	 * Input parameter.
	 */
	private static String userName = "";
	/**
	 * Input parameter.
	 */
	private static String password = "";
	/**
	 * Input parameter.
	 */
	private static boolean commitEveryRow = false;
	/**
	 * Input parameter.
	 */
	private static boolean commitAtEnd = false;
	/**
	 * Input parameter.
	 */
	private static int batchCommit = 0;
	/**
	 * Input parameter.
	 */
	private static boolean saveExceptions = false;

	/**
	 * Database connection.
	 */
	private final Connection myConnection;

	/**
	 * Constructs a CommittingData object.
	 * @throws SQLException Any database error while
	 * opening a database connection
	 */
	public CommittingData() throws SQLException {
		OracleDataSource ods = new OracleDataSource();
		ods.setDriverType("thin");
		ods.setServerName(CommittingData.host);
		ods.setPortNumber(CommittingData.port);
		ods.setServiceName(CommittingData.serviceName);
		ods.setUser(CommittingData.userName);
		ods.setPassword(CommittingData.password);

		this.myConnection = ods.getConnection();
		this.myConnection.setAutoCommit(false);
	}
	/**
	 * Main entry point.
	 * @param args Array with various options on how to execute
	 * and which database to connect to
	 * @throws Exception Any error that happens during execution
	 */
	public static void main(final String[] args) throws Exception {

		if (args.length == 0) {
			CommittingData.printHelp();
		} else {

			for (int i = 0; i < args.length; i++) {
				switch (args[i]) {
					case "-host":
						CommittingData.host = args[++i];
						break;
					case "-port":
						CommittingData.port = Integer.valueOf(args[++i]).intValue();
						break;
					case "-srvn":
						CommittingData.serviceName = args[++i];
						break;
					case "-user":
						CommittingData.userName = args[++i];
						break;
					case "-pass":
						CommittingData.password = args[++i];
						break;
					case "-commitEveryRow":
						CommittingData.commitEveryRow = true;
						break;
					case "-commitAtEnd":
						CommittingData.commitAtEnd = true;
						break;
					case "-batchCommit":
						CommittingData.batchCommit = Integer.valueOf(args[++i]).intValue();
						break;
					case "-saveExceptions":
						CommittingData.saveExceptions = true;
						break;
					case "-directPath":
						CommittingData.APPEND = " /*+ APPEND */ ";
						break;
					default: CommittingData.printHelp();
				}
			}

			CommittingData myApp = new CommittingData();
			myApp.setup();
			myApp.runTests();
			myApp.tearDown();
		}
	}

	/**
	 * This method prepares the Database for testing by creating the test table.
	 * @throws SQLException Any Database related error
	 */
	private void setup() throws SQLException {
		final int ALREADYEXISTS = 955;
		try {
			this.myConnection.prepareStatement(
					"CREATE TABLE " + CommittingData.TESTTABLE
					+ " (ID NUMBER, TXT VARCHAR2(255))").execute();
			this.myConnection.prepareStatement("CREATE UNIQUE INDEX TEST_PK on " + CommittingData.TESTTABLE + "(ID)").execute();

		} catch (SQLException e) {
			// Ignore "ORA-00955: name is already used by an existing object"
			if (e.getErrorCode() != ALREADYEXISTS) {
				throw e;
			}
		}
		if (CommittingData.saveExceptions) {
			try {
				this.myConnection.prepareStatement(
						"BEGIN DBMS_ERRLOG.CREATE_ERROR_LOG('" + CommittingData.TESTTABLE
						+ "', '" + CommittingData.ERROR_LOG_TABLE + "'); END;").execute();
			} catch (SQLException e) {
				if (e.getErrorCode() != ALREADYEXISTS) {
					throw e;
				}
			}
		}
	}

	/**
	 * Tears down the setup environment.
	 * @throws SQLException Any database error while tear down.
	 */
	private void tearDown() throws SQLException {
		this.myConnection.prepareStatement("DROP TABLE " + CommittingData.TESTTABLE).execute();
		if (CommittingData.saveExceptions) {
			this.myConnection.
			prepareStatement("DROP TABLE " + CommittingData.ERROR_LOG_TABLE).
			execute();
		}
		this.myConnection.prepareStatement("PURGE USER_RECYCLEBIN").execute();
	}

	/**
	 * Run the tests.
	 * @throws SQLException Any Database error
	 */
	private void runTests() throws SQLException {

		if (CommittingData.commitEveryRow) {
			this.commitEveryRow();
		} else if (CommittingData.commitAtEnd) {
			this.commitAtEnd();
		} else if ((CommittingData.batchCommit > 0) && CommittingData.saveExceptions) {
			this.batchCommitSaveExceptions(CommittingData.batchCommit);
		} else if (CommittingData.batchCommit > 0) {
			this.batchCommit(CommittingData.batchCommit);
		}
	}

	/**
	 * This method loads static data into the test table.
	 * It iterates over a loop as many times as is specified
	 * in the static ITERATIONS variable.
	 * The method commits after every newly inserted row.
	 * @throws SQLException Any Database error that might
	 * occur during the insert
	 */
	private void commitEveryRow() throws SQLException {

		System.out.println("Loading data with committing"
				+ " after every row - " + CommittingData.ITERATIONS + " iterations");

		PreparedStatement stmt = this.myConnection.
				prepareStatement("INSERT " + CommittingData.APPEND + " INTO "
						+ CommittingData.TESTTABLE + " VALUES (?,?)");

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < CommittingData.ITERATIONS; i++) {
			stmt.setInt(1, i);
			stmt.setString(2, ";ajskfj[wig[ajdfkjaw[oeimakldjalksva;djfashdfjksahdf;lkjasdfoiwejaflkf;smvwlknvoaweijfasdfjasldf;kwlvma;dfjlaksjfowemowaivnoawn");
			stmt.execute();
			this.myConnection.commit();
		}
		long endTime = System.currentTimeMillis();

		System.out.println("Data loaded in: " + (endTime - startTime) + "ms");
	}

	/**
	 * This method loads static data into the test table.
	 * It iterates over a loop as many times as is specified
	 * in the static ITERATIONS variable.
	 * The method commits only once after all the data is fully loaded.
	 * @throws SQLException Any database error that may occurs during the insert
	 */
	private void commitAtEnd() throws SQLException {

		System.out.println("Loading data with committing after"
				+ " the entire set is loaded - " + CommittingData.ITERATIONS + " iterations");

		PreparedStatement stmt = this.myConnection.prepareStatement(
				"INSERT " + CommittingData.APPEND + " INTO " + CommittingData.TESTTABLE + " VALUES (?,?)");

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < CommittingData.ITERATIONS; i++) {
			stmt.setInt(1, i);
			stmt.setString(2, ";ajskfj[wig[ajdfkjaw[oeimakldjalksva;djfashdfjksahdf;lkjasdfoiwejaflkf;smvwlknvoaweijfasdfjasldf;kwlvma;dfjlaksjfowemowaivnoawn");
			stmt.execute();
		}
		this.myConnection.commit();
		long endTime = System.currentTimeMillis();

		System.out.println("Data loaded in: " + (endTime - startTime) + "ms");

	}

	/**
	 * This method loads static data into the test table.
	 * It used the JDBC batching functionality and iterates over a loop
	 * as many times as is specified in the static ITERATIONS variable.
	 * @param batchSize The size of the batch before executing it.
	 * @throws SQLException Any database error that may occurs during the insert
	 */
	private void batchCommit(final int batchSize) throws SQLException {

		System.out.println("Batch loading data with committing "
				+ "after the entire set is loaded - "
				+ CommittingData.ITERATIONS + " iterations");

		PreparedStatement stmt = this.myConnection.prepareStatement(
				"INSERT " + CommittingData.APPEND + " INTO " + CommittingData.TESTTABLE + " VALUES (?,?)");

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < CommittingData.ITERATIONS; i++) {
			stmt.setInt(1, i);
			stmt.setString(2, ";ajskfj[wig[ajdfkjaw[oeimakldjalksva;djfashdfjksahdf;lkjasdfoiwejaflkf;smvwlknvoaweijfasdfjasldf;kwlvma;dfjlaksjfowemowaivnoawn");
			stmt.addBatch();
			// Execute batch if batch size is reached
			if ((i % batchSize) == 0) {
				try {
					stmt.executeBatch();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
		}
		try {
			stmt.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}
		this.myConnection.commit();
		long endTime = System.currentTimeMillis();

		System.out.println("Data loaded in: " + (endTime - startTime) + "ms");

	}

	/**
	 * This method loads static data into the test table.
	 * It used the JDBC batching functionality and iterates over a loop
	 * as many times as is specified in the static ITERATIONS variable.
	 * It also uses the LOG ERRORS clause that will prevent the
	 * execution from failing if any exception occurs within the batch.
	 * @param batchSize The size of the batch before executing it.
	 * @throws SQLException Any database error that may occurs during the insert
	 */
	private void batchCommitSaveExceptions(final int batchSize)
			throws SQLException {

		System.out.println("Batch loading data (save exceptions)"
				+ " with committing after the entire set is loaded - "
				+ CommittingData.ITERATIONS + " iterations");

		PreparedStatement stmt = this.myConnection.prepareStatement(
				"INSERT " + CommittingData.APPEND + " INTO " + CommittingData.TESTTABLE
				+ " VALUES (?,?) LOG ERRORS INTO " + CommittingData.ERROR_LOG_TABLE
				+ " REJECT LIMIT UNLIMITED");

		long startTime = System.currentTimeMillis();
		stmt.setInt(1, 400);
		stmt.setString(2,
				";ajskfj[wig[ajdfkjaw[oeimakldjalksva;djfashdfjksahdf;lkjasdfoiwejaflkf;smvwlknvoaweijfasdfjasldf;kwlvma;dfjlaksjfowemowaivnoawn");
		stmt.addBatch();

		for (int i = 0; i < CommittingData.ITERATIONS; i++) {
			stmt.setInt(1, i);
			stmt.setString(2, ";ajskfj[wig[ajdfkjaw[oeimakldjalksva;djfashdfjksahdf;lkjasdfoiwejaflkf;smvwlknvoaweijfasdfjasldf;kwlvma;dfjlaksjfowemowaivnoawn");
			stmt.addBatch();
			// Execute batch if batch size is reached
			if ((i % batchSize) == 0) {
				stmt.executeBatch();
			}
		}
		stmt.executeBatch();
		this.myConnection.commit();
		long endTime = System.currentTimeMillis();

		System.out.println("Data loaded in: " + (endTime - startTime) + "ms");

	}
	/**
	 * Prints the help and exits the program.
	 */
	private static void printHelp() {
		System.out.println("Committing data to the Oracle Database - Usage:");
		System.out.println();
		System.out.println("java com.gvenzl.CommittingData -host [host]"
				+ " -port [port] -srvn [service name] -user [username]"
				+ " -pass [password] -commitEveryRow -commitAtEnd"
				+ " -batchCommit [batch size] -saveExceptions -directPath");
		System.out.println();
		System.out.println("host: 		The database host name");
		System.out.println("port: 		The database listener port");
		System.out.println("service name: 	The database service name");
		System.out.println("username: 	The database username");
		System.out.println("password: 	The database user password");
		System.out.println("commitEveryRow: Commit data after every row");
		System.out.println("commitAtEnd: 	Commit data only once at"
				+ " the end of a load");
		System.out.println("batch size: 	The size of the loading batch"
				+ " to execute at once");
		System.out.println("saveExceptions: Specify whether you would like"
				+ " to save exceptions during batch loading");
		System.out.println("directPath: 	Specify whether you would like"
				+ " to use DIRECT PATH loading");

		System.exit(0);
	}

}
