package com.opentach.client.laf;

import javax.swing.UIDefaults;

import com.imlabs.client.laf.common.UOntimizeLookAndFeel;
import com.imlabs.client.laf.common.UStyleUtil;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.plaf.utils.StyleUtil;
import com.ontimize.util.swing.CollapsibleButtonPanel;
import com.opentach.client.OpentachClientApplication;
import com.opentach.client.comp.activitychart.ActivityChartControlPanel;
import com.opentach.client.comp.activitychart.AxisHorizontalBottom;
import com.opentach.client.comp.activitychart.AxisVertical;
import com.opentach.client.comp.activitychart.Chart;
import com.opentach.client.comp.activitychart.LegendPanel;
import com.opentach.client.comp.activitychart.taskwrapper.ActividadesTaskWrapper;
import com.opentach.client.comp.activitychart.taskwrapper.ResumenActividadesTaskWrapper;
import com.opentach.client.comp.activitychart.taskwrapper.RulerTaskWrapper;

public class OpentachLAF extends UOntimizeLookAndFeel {

	public OpentachLAF() {
		super(System.getProperty(UStyleUtil.FONT_SIZE_PROPERTY));
	}

	@Override
	public UIDefaults getDefaults() {
		if (!this.initialized) {
			UIDefaults defaults = super.getDefaults();
			this.defineCustomButton("\"activitygraphbutton\"", defaults);
			this.defineCustomButton("\"NoMarginsButton\"", defaults);
			this.defineCustomButton("\"PreferencesButton\"", defaults);
			this.defineCustomLabel("\"CHART_RESUME_LABEL\"", defaults);
			this.defineCustomLabel("\"PhoneNumber\"", defaults);
			this.defineCustomLabel("\"homeTitleLabels\"", defaults);

			this.defineStatusBarColors();
			this.defineActivityChart();

			// TODO remove when new icons used
			ImageManager.ALL_LEFT_ARROW = "com/ontimize/plaf/images/allleftarrow.png";
			ImageManager.ALL_RIGHT_ARROW = "com/ontimize/plaf/images/allrightarrow.png";
			CollapsibleButtonPanel.leftIconPath = ImageManager.ALL_LEFT_ARROW;
			CollapsibleButtonPanel.rightIconPath = ImageManager.ALL_RIGHT_ARROW;

			this.defineCustomRow("\"launchpadmenubar\"", defaults);
			this.defineCustomColumn("\"homeLaunchpadSeparator\"", defaults);
		}
		return super.getDefaults();
	}

	protected void defineStatusBarColors() {
		String componentName = "StatusBarLabels";
		OpentachClientApplication.colorUserLabel = StyleUtil.getColor(componentName, "colorUserLabel", "#FFFFFF");
		OpentachClientApplication.colorAppVersion = StyleUtil.getColor(componentName, "colorAppVersion", "#fe9292");

	}

	protected void defineActivityChart() {
		String componentName = "ActivityChart";
		LegendPanel.COLOR_FOREGROUND = StyleUtil.getColor(componentName, "legedForeground", "#1c304bff");
		ActividadesTaskWrapper.COLOR_ACTIVITY_WORK = StyleUtil.getColor(componentName, "activityWork", "#002eedff");
		ActividadesTaskWrapper.COLOR_ACTIVITY_AVAILABLE = StyleUtil.getColor(componentName, "activityAvailable", "#55d500ff");
		ActividadesTaskWrapper.COLOR_ACTIVITY_REST = StyleUtil.getColor(componentName, "activityRest", "#fffe00ff");
		ActividadesTaskWrapper.COLOR_ACTIVITY_DRIVING = StyleUtil.getColor(componentName, "activityDriving", "#e11c00ff");
		ActividadesTaskWrapper.COLOR_ACTIVITY_UNDEFINED = StyleUtil.getColor(componentName, "activityUndefined", "#fe6c0cff");
		ActividadesTaskWrapper.COLOR_ACTIVITY_UNDER_SHADOW = StyleUtil.getColor(componentName, "activityBottomShadow", "#000000ff");
		ActividadesTaskWrapper.COLOR_UNDERLINES_BACKGROUND = StyleUtil.getColor(componentName, "underlinesBackground", "#57627eff");
		ActividadesTaskWrapper.COLOR_UNDERLINES_REGIMEN = StyleUtil.getColor(componentName, "underlinesRegimen", "#FF0000ff");
		ActividadesTaskWrapper.COLOR_UNDERLINES_ORIGIN = StyleUtil.getColor(componentName, "underlinesOrigin", "#ffff00ff");
		ActividadesTaskWrapper.COLOR_UNDERLINES_FUERA_AMBITO = StyleUtil.getColor(componentName, "underlinesOutOfScope", "#000000ff");
		ActividadesTaskWrapper.COLOR_UNDERLINES_TRANS_TREN = StyleUtil.getColor(componentName, "underlinesFerry", "#ffffffff");
		Chart.COLOR_BACKGROUND = StyleUtil.getColor(componentName, "background", "#5b8aabff");
		Chart.COLOR_SPACE = StyleUtil.getColor(componentName, "separatorBackground", "#406e8eff");
		Chart.COLOR_SEPARATOR_SECOND_BORDER = StyleUtil.getColor(componentName, "separatorBorderSecond", "#719dbcff");
		Chart.COLOR_SEPARATOR_FIRST_BORDER = StyleUtil.getColor(componentName, "separatorBorderFirst", "#2d5b7bff");

		RulerTaskWrapper.COLOR_ACTIVITIES_BOX_TOP = StyleUtil.getColor(componentName, "activityBoxTop", "#2d5b7bff");
		RulerTaskWrapper.COLOR_ACTIVITIES_BOX_BOTTOM = StyleUtil.getColor(componentName, "activityBoxBottom", "#7198b4ff");

		RulerTaskWrapper.COLOR_LINE_BOTTOM = StyleUtil.getColor(componentName, "lineBottom", "#7198b4ff");
		RulerTaskWrapper.COLOR_LINE_UPPER = StyleUtil.getColor(componentName, "lineTop", "#2d5b7bff");
		AxisVertical.FONT_COLOR_DATE = StyleUtil.getColor(componentName, "vaxisDateForeground", "#ffffffff");
		AxisVertical.FONT_COLOR_DAY_NAME = StyleUtil.getColor(componentName, "vaxisDayForeground", "#194767ff");
		AxisVertical.BG_GRADIENT_DATE_FIRST_COLOR = StyleUtil.getColor(componentName, "vaxisDateGradientFirst", "#2d5b7bff");
		AxisVertical.BG_GRADIENT_DATE_SECOND_COLOR = StyleUtil.getColor(componentName, "vaxisDateGradientSecond", "#4c7795ff");
		AxisVertical.FONT_LEGEND = StyleUtil.getFont(componentName, "vaxisLegendFont", "Arial-BOLD-8");
		AxisVertical.FONT_DATE = StyleUtil.getFont(componentName, "vaxisDateFont", "Arial-BOLD-12");
		AxisVertical.FONT_DAY_NAME = StyleUtil.getFont(componentName, "vaxisDayFont", "Arial-BOLD-10");
		AxisHorizontalBottom.FONT_COLOR_HOURS = StyleUtil.getColor(componentName, "haxisForeground", "#111188");

		ResumenActividadesTaskWrapper.COLOR_SUM_ICON_FOREGROUND = StyleUtil.getColor(componentName, "sumIconForeground", "#2d5b7bff");
		ResumenActividadesTaskWrapper.COLOR_SUM_ICON_BORDER = StyleUtil.getColor(componentName, "sumIconBorder", "#2d5b7bff");
		ResumenActividadesTaskWrapper.COLOR_SUM_ICON_INNER_BORDER = StyleUtil.getColor(componentName, "sumIconInnerBorder", "#9bbad0ff");
		ResumenActividadesTaskWrapper.COLOR_SUM_ICON_BACKGROUND = StyleUtil.getColor(componentName, "sumIconBackground", "#719dbcff");

		ActivityChartControlPanel.TITLE_FONT = StyleUtil.getFont(componentName, "controlPanelFont", "Arial-BOLD-14");
		ActivityChartControlPanel.TITLE_FOREGROUND = StyleUtil.getColor(componentName, "controlPanelTitleForeground", "#03fb06ff");
		ActivityChartControlPanel.COLOR_TITLE_DATE_BACKGROUND = StyleUtil.getColor(componentName, "controlPanelTitleDateBackground", "#4f7b9aff");
		ActivityChartControlPanel.COLOR_BACGROUND = StyleUtil.getColor(componentName, "controlPanelBackground", "#5b8aabff");
	}

	// TODO remove when fix in ontimize
	// @Override
	// protected void defineMonthPlanningComponent(String compName, UIDefaults d) {
	// try {
	// Class.forName("com.ontimize.planning.component.monthplanning.MonthPlanningConstants");
	// super.defineMonthPlanningComponent(compName, d);
	// } catch (ClassNotFoundException error) {
	// // do nothing
	// }
	// }

	// TODO remove when new icons used

	// @Override
	// protected String getStyleProperty(String style, String name, String key, String defaultValue) {
	// String property = StyleUtil.getProperty(name, key, defaultValue);
	// boolean matches = property.matches("/.*");
	// if (!style.isEmpty() && matches) {// style value path
	// return defaultValue;
	// // String propertyPath = style + property;
	// // if (ImageManager.getIcon(propertyPath) == null) {
	// // return "common" + property;// by default returns the common
	// // }
	// // return propertyPath;
	// }
	// return property;
	// }
}
