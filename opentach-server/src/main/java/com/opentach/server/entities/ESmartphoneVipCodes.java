package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.db.util.CountDBFunctionName;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.smartphonevipcodes.ISmartphoneVipCodeService;
import com.opentach.server.util.db.OracleTableEntity;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.UpdateJdbcTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class ESmartphoneVipCodes.
 */
public class ESmartphoneVipCodes extends OracleTableEntity implements ISmartphoneVipCodeService {
	private SequenceGenerator	generator	= null;

	/**
	 * Instantiates a new e smartphone vip codes.
	 *
	 * @param bRefs
	 *            the b refs
	 * @param gc
	 *            the gc
	 * @param puerto
	 *            the puerto
	 * @throws Exception
	 *             the exception
	 */
	public ESmartphoneVipCodes(EntityReferenceLocator bRefs, DatabaseConnectionManager gc, int puerto) throws Exception {
		super(bRefs, gc, puerto);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sessionID, Connection conn) throws Exception {
		return super.query(cv, v, sessionID, conn);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.TableEntity#insert(java.util.Hashtable, int, java.sql.Connection)
	 */
	@Override
	public EntityResult insert(Hashtable attributesValues, int sessionId, Connection con) throws Exception {
		attributesValues.put("CREATION_DATE", new Date(System.currentTimeMillis()));
		return super.insert(attributesValues, sessionId, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.interfaces.ISmartphoneVipCodeService#generateCodes(int)
	 */
	@Override
	public synchronized void generateCodes(final int numCodes) throws Exception {
		if (this.generator == null) {
			EntityResult query = this.query(new Hashtable<Object, Object>(),
					new Vector<Object>(Arrays.asList(new Object[] { new CountDBFunctionName("CODE") })), TableEntity.getEntityPrivilegedId(this));
			try {
				Number count = (Number) query.getRecordValues(0).get("COUNT");
				this.generator = new SequenceGenerator(count.longValue() + 1, 5);
			} catch (Exception ex) {
				this.generator = new SequenceGenerator(1, 5);
			}
		}

		new OntimizeConnectionTemplate<Void>() {
			@Override
			protected Void doTask(Connection con) throws UException, SQLException {
				String sql = "insert into " + ESmartphoneVipCodes.this.getTable() + " (CODE,CREATION_DATE) values(?,?)";
				new UpdateJdbcTemplate() {
					@Override
					protected Integer innerExecuteUpdate(PreparedStatement pstmt, Object... parameters) throws SQLException {
						Timestamp date = new Timestamp(System.currentTimeMillis());
						for (int i = 0; i < numCodes; i++) {
							this.setStatementParameters(Integer.valueOf(1), pstmt, ESmartphoneVipCodes.this.generator.computeNext(), date);
							pstmt.executeUpdate();
						}
						return null;
					}
				}.execute(con, sql);
				return null;
			}
		}.execute(this.manager, false);

	}

	/**
	 * The Class SequenceGenerator.
	 */
	static class SequenceGenerator {

		/** The characters. */
		private static char[]	CHARACTERS;
		static {
			SequenceGenerator.CHARACTERS = new char[(((('9' - '0') + 1 + 'Z') - 'A') + 1)];
			int idx = 0;
			for (char i = '0'; i <= '9'; i++) {
				SequenceGenerator.CHARACTERS[idx++] = i;
			}
			for (char i = 'A'; i <= 'Z'; i++) {
				SequenceGenerator.CHARACTERS[idx++] = i;
			}
		}

		/** The current. */
		private long			current;

		/** The string lengh. */
		private final int		stringLengh;

		/**
		 * Instantiates a new sequence generator.
		 *
		 * @param begin
		 *            the begin
		 * @param stringLengh
		 *            the string lengh
		 */
		public SequenceGenerator(long begin, int stringLengh) {
			this.current = begin;
			this.stringLengh = stringLengh;
		}

		/**
		 * Compute next.
		 *
		 * @return the string
		 */
		protected String computeNext() {
			char[] res = new char[this.stringLengh];
			Arrays.fill(res, SequenceGenerator.CHARACTERS[0]);
			for (int i = 0; i < res.length; i++) {
				res[i] = SequenceGenerator.CHARACTERS[(int) (((this.current) / Math.pow(SequenceGenerator.CHARACTERS.length, i))) % SequenceGenerator.CHARACTERS.length];
			}
			this.current++;
			return new String(res);
		}

	}
}
