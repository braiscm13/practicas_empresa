/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
package com.ontimize.report.engine.dynamicjasper5;
import java.awt.Color;

import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;

public abstract class DynamicJasperStyles {

	// Styles for header (this header is only showed when not exist groups)
	public static int defaultHeaderPaddingTop = 0;
	public static int defaultHeaderPaddingBottom = 0;
	public static int defaultHeaderPaddingRight = 0;
	public static int defaultHeaderPaddingLeft = 0;
	public static Font				defaultHeaderFont												= new ar.com.fdvs.dj.domain.constants.Font(10, "Arial", null, null, false);
	public static Color defaultHeaderBackgroundColor = new Color(255, 255, 255); // white
	public static Transparency defaultHeaderTransparency = Transparency.OPAQUE;
	public static Border defaultHeaderBorderTop = Border.NO_BORDER();
	public static Border defaultHeaderBorderBottom = Border.THIN();
	public static Border defaultHeaderBorderRight = Border.NO_BORDER();
	public static Border defaultHeaderBorderLeft = Border.NO_BORDER();
	public static HorizontalAlign defaultHeaderHorizontalAlignment = HorizontalAlign.CENTER;
	public static VerticalAlign defaultHeaderVerticalAlignment = VerticalAlign.TOP;

	// Styles for title
	public static Font				defaultTitleFont												= new ar.com.fdvs.dj.domain.constants.Font(12, "Arial", null, null, false);
	public static Color defaultTitleBackgroundColor = new Color(255, 255, 255); // white
	public static Color defaultTitleFontColor = new Color(52, 52, 52);
	public static Transparency defaultTitleTransparency = Transparency.OPAQUE;

	// Styles for subtitle
	public static Font				defaultSubtitleFont												= new ar.com.fdvs.dj.domain.constants.Font(10, "Arial", null, null, false);
	public static Color defaultSubtitleBackgroundColor = new Color(255, 255, 255); // white
	public static Color defaultSubtitleFontColor = new Color(52, 52, 52);
	public static Transparency defaultSubtitleTransparency = Transparency.OPAQUE;

	// Styles for header when exist GROUPS
	public static int defaultHeaderForGroupPaddingTop = 0;
	public static int defaultHeaderForGroupPaddingBottom = 0;
	public static int defaultHeaderForGroupPaddingRight = 2;
	public static int defaultHeaderForGroupPaddingLeft = 2;
	public static Font				defaultHeaderForGroupFont										= new ar.com.fdvs.dj.domain.constants.Font(10, "Arial", null, null, false);
	public static Color defaultHeaderForGroupFontColor = new Color(255, 255, 255);
	public static Color defaultHeaderForGroupBackgroundColor = new Color(137, 174, 197); // #89aec5
	public static Transparency defaultHeaderForGroupTransparency = Transparency.OPAQUE;
	public static Border defaultHeaderForGroupBorderTop = Border.NO_BORDER();
	public static Border defaultHeaderForGroupBorderBottom = Border.PEN_1_POINT();
	public static Border defaultHeaderForGroupBorderRight = Border.NO_BORDER();
	public static Border defaultHeaderForGroupBorderLeft = Border.NO_BORDER();
	public static HorizontalAlign defaultHeaderForGroupHorizontalAlignmentTitles = HorizontalAlign.LEFT;
	public static HorizontalAlign defaultHeaderForGroupHorizontalAlignmentValues = HorizontalAlign.RIGHT;
	public static VerticalAlign defaultHeaderForGroupVerticalAlignment = VerticalAlign.MIDDLE;
	public static Color defaultHeaderForGroupPaddingColor = new Color(196, 215, 226);
	public static Border defaultHeaderForMultiGroupBorderTop = Border.NO_BORDER();
	public static Border defaultHeaderForMultiGroupBorderBottom = Border.PEN_1_POINT();
	public static Border defaultHeaderForMultiGroupBorderRight = Border.NO_BORDER();
	public static Border defaultHeaderForMultiGroupBorderLeft = Border.NO_BORDER();

	// Styles for data rendered in report detail
	public static int defaultColumnDataPaddingTop = 1;
	public static int defaultColumnDataPaddingBottom = 1;
	public static int defaultColumnDataPaddingRight = 2;
	public static int defaultColumnDataPaddingLeft = 2;
	public static Font				defaultColumnDataFont											= new ar.com.fdvs.dj.domain.constants.Font(9, "Arial", null, null, false);
	public static Color defaultColumnDataFontColor = new Color(117, 118, 122); // #75767A
	public static Transparency defaultColumnDataTransparency = Transparency.OPAQUE;
	public static Border defaultColumnDataBorderTop = Border.NO_BORDER();
	public static Border defaultColumnDataBorderBottom = Border.NO_BORDER();
	public static Border defaultColumnDataBorderRight = Border.NO_BORDER();
	public static Border defaultColumnDataBorderLeft = Border.NO_BORDER();
	public static Border defaultColumnGridDataBorder = Border.THIN();
	public static HorizontalAlign defaultColumnDataHorizontalAlignment = HorizontalAlign.RIGHT;
	public static VerticalAlign defaultColumnDataVerticalAlignment = VerticalAlign.BOTTOM;
	public static Color defaultOddColumnBackgroundColor = new Color(255, 255, 255);

	// Styles for group footer
	public static int defaultGroupFooterVariablePaddingLeft = 3;
	public static Color defaultGroupFooterVariableBackgroundColor = new Color(231, 238, 240); // #e7eef0
	public static Color defaultGroupFooterVariableTextColor = new Color(117, 118, 122); // #75767A
	public static Transparency defaultGroupFooterVariableTransparency = Transparency.OPAQUE;
	public static HorizontalAlign defaultGroupFooterVariableHorizontalAlignment = HorizontalAlign.JUSTIFY;
	public static Font				defaultGroupFooterVariableFont									= new ar.com.fdvs.dj.domain.constants.Font(9, "Arial", null, null, false);

	// Styles for group footer (number of ocurrences)
	public static int defaultGroupFooterNumberOcurrencesVariablePaddingLeft = 3;
	public static Color defaultGroupFooterNumberOcurrencesVariableBackgroundColor = new Color(207, 222, 225); // #cfdee1
	public static Color defaultGroupFooterNumberOcurrencesVariableTextColor = new Color(117, 118, 122); // #75767A
	public static Transparency defaultGroupFooterNumberOcurrencesVariableTransparency = Transparency.OPAQUE;
	public static HorizontalAlign defaultGroupFooterNumberOcurrencesVariableHorizontalAlignment = HorizontalAlign.JUSTIFY;
	public static Font				defaultGroupFooterNumberOcurrencesVariableFont					= new ar.com.fdvs.dj.domain.constants.Font(9, "Arial", null, null, false);

	// Styles for footer variables when not exist groups (showed at the end of
	// report)
	public static int defaultFooterVariablePaddingLeft = 3;
	public static Color defaultFooterVariableBackgroundColor = new Color(51, 89, 113); // #335971
	public static Transparency defaultFooterVariableTransparency = Transparency.OPAQUE;
	public static HorizontalAlign defaultFooterVariableHorizontalAlignment = HorizontalAlign.JUSTIFY;
	public static Color defaultFooterVariableTextColor = new Color(255, 255, 255);
	public static Border defaultFooterVariableBorderTop = Border.NO_BORDER();
	public static Font				defaultFooterVariableFont										= new ar.com.fdvs.dj.domain.constants.Font(10, "Arial", null, null, false);

}
