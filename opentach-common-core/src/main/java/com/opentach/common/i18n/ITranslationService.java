package com.opentach.common.i18n;

import java.rmi.Remote;

public interface ITranslationService extends Remote {

	static final Integer	ACTIVITY_TYPE_REST			= 1;
	static final Integer	ACTIVITY_TYPE_AVAILABLE		= 2;
	static final Integer	ACTIVITY_TYPE_WORK			= 3;
	static final Integer	ACTIVITY_TYPE_DRIVING		= 4;
	static final Integer	ACTIVITY_TYPE_INDETERMINATE	= 5;

	/** The Constant ID. */
	static final String		ID							= "TranslationService";

}
