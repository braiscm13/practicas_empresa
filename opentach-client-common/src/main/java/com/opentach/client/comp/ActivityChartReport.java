package com.opentach.client.comp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Activity Chart Report wrapper. Print the activity chart into a report.
 *
 * @author Pablo Dorgambide - IMATIA.
 * @company www.imatia.com
 * @email pdorgambide@imatia.com
 */

public class ActivityChartReport // extends AbstractFunction
implements Serializable {
	protected String		group;
	protected transient int	currentIndex;	// Index of the group.

	// Properties
	int						xfontsize	= 10, yfontsize = 10;
	boolean					showlegend	= true;

	protected class GroupActivityInfo {
		protected List	activs	= new ArrayList();
		protected Date	datefrom, dateto;
	}

	// Resultado para todos los grupos
	protected transient ArrayList	results					= new ArrayList();	;
	protected GroupActivityInfo		groupResult;

	/** Chart config properties */
	public static final String		COLORS_PROPERTY			= "colors";
	public static final String		CHART_TITLE_PROPERTY	= "title";
	public static final String		LABEL_TYPE_PROPERTY		= "labeltype";



	public ActivityChartReport() {
		super();
	}

}
