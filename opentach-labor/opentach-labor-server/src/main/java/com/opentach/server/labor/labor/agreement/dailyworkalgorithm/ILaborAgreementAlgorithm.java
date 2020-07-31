package com.opentach.server.labor.labor.agreement.dailyworkalgorithm;

import com.opentach.server.labor.labor.DailyWorkRecord;

/**
 * The Interface ILaborAgreement.
 */
public interface ILaborAgreementAlgorithm {

	/**
	 * Process daily work record.
	 *
	 * @param stretchs
	 *            the stretchs
	 * @return the int
	 */
	int processDailyWorkRecord(DailyWorkRecord workRecord);

}
