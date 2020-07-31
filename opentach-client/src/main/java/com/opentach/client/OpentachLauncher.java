package com.opentach.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.DefaultInteractionManagerLoader;
import com.ontimize.gui.Form;
import com.ontimize.gui.container.Tab;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.NavigationMenu.DefaultMenuHeaderRenderer;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.field.WWWDataField;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.TableSorter;
import com.ontimize.permission.PermissionButton;
import com.ontimize.report.engine.dynamicjasper5.CustomJasperViewerToolbar;
import com.ontimize.util.JEPUtils;
import com.ontimize.util.swing.table.PrintablePivotTable;
import com.ontimize.util.xls.XLSExporterFactory;
import com.ontimize.xml.DefaultXMLParametersManager;
import com.opentach.client.comp.OpentachExpansibleTable;
import com.opentach.client.comp.OpentachInsertTable;
import com.opentach.client.comp.field.OpentachTable;
import com.opentach.client.comp.fnc.DateDiff;
import com.opentach.client.comp.fnc.NumberDiff;
import com.opentach.client.sessionstatus.StatisticsRecollector;
import com.opentach.client.util.OpentachApplicationLauncher;
import com.opentach.client.util.excel.CustomPoi3_5XLS_ExporterUtils;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.field.table.UExpansibleTable;
import com.utilmize.client.gui.field.table.UTable;

public final class OpentachLauncher {

	private static final Logger logger = LoggerFactory.getLogger(OpentachLauncher.class);

	private OpentachLauncher() {
		super();
	}

	public static String[] configureSystemProperties(String[] args) {
		List<String> arguments = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			String value = args[i];
			if (value != null) {
				value = value.trim();
				if (value.startsWith("-D") && (value.length() > 2) && (value.indexOf("=") > 0)) {
					String inputP = value.substring(2);
					StringTokenizer token = new StringTokenizer(inputP, "=");
					if (token.hasMoreTokens()) {
						String propertyKey = token.nextToken();
						if (token.hasMoreTokens()) {
							String propertyValue = token.nextToken();
							System.setProperty(propertyKey, propertyValue);
						}
					}
				} else {
					if (!"ignore".equals(value)) {
						arguments.add(value);
					}
				}
			}
		}
		return arguments.toArray(new String[arguments.size()]);
	}

	public static void setLAF() {
		OpentachLauncher.logger.info("Setting Opentach LAF");
		try {
			DefaultXMLParametersManager.add(Table.class.getName(), "usetableborders", "false");
			DefaultXMLParametersManager.add(OpentachInsertTable.class.getName(), "usetableborders", "false");
			DefaultXMLParametersManager.add(OpentachExpansibleTable.class.getName(), "usetableborders", "false");
			DefaultXMLParametersManager.add(OpentachTable.class.getName(), "usetableborders", "false");
			DefaultXMLParametersManager.add(UTable.class.getName(), "usetableborders", "false");
			DefaultXMLParametersManager.add(WWWDataField.class.getName(), "uppercase", "no");

			// UIManager.getDefaults().put("List.foreground", new Color(0x335971));
			// UIManager.getDefaults().put("List.selectionBackground", new Color(0x335971));

			// NavigatorMenuGroups : Use LAF configuration
			DefaultMenuHeaderRenderer.LEFTIMAGE_URL = null;
			DefaultMenuHeaderRenderer.CENTERIMAGE_URL = null;
			DefaultMenuHeaderRenderer.RIGHTIMAGE_URL = null;

			/** The icon no expanded parent. */
			UExpansibleTable.ICON_NO_EXPANDED_PARENT = ImageManager.getIcon("com/opentach/client/rsc/expansibletable/O1_plus.png");
			/** The icon expanded parent. */
			UExpansibleTable.ICON_EXPANDED_PARENT = ImageManager.getIcon("com/opentach/client/rsc/expansibletable/O1_expandparentblue.png");
			/** The icon parent without children. */
			UExpansibleTable.ICON_PARENT_WITHOUT_CHILDREN = ImageManager.getIcon("com/opentach/client/rsc/expansibletable/O1_lastnodered.png");
			/** The icon child. */
			UExpansibleTable.ICON_CHILD = ImageManager.getIcon("com/opentach/client/rsc/expansibletable/O1_middlnodeblue.png");
			/** The icon last child. */
			UExpansibleTable.ICON_LAST_CHILD = ImageManager.getIcon("com/opentach/client/rsc/expansibletable/O1_lastchildrenblue.png");
		} catch (Exception e) {
			OpentachLauncher.logger.error(null, e);
		}
	}

	public static void setOntimizeSettings() {
		try {
			new UTable(null);
		} catch (Exception e) {
		}

		Table.renderReportValues = false;
		Table.XLS_EXPORT_CLASS = XLSExporterFactory.POI_3_5;
		XLSExporterFactory.registerXLSExporter(XLSExporterFactory.POI_3_5, new CustomPoi3_5XLS_ExporterUtils());
		System.setProperty("com.ontimize.report.templates", "com/opentach/reports/default_portrait_jasper_4.6.jrxml;com/opentach/reports/default_landscape_jasper_4.6.jrxml");
		System.setProperty("com.ontimize.report.saveContributors",
				CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_PDF + ";" + CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_ODT + ";" + CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_DOCX + ";" + CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_HTML + ";" + CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_XLS_SINGLE_SHEET + ";" + CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_XLS_MULTIPLE_SHEET + ";" + CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_CSV);
		PrintablePivotTable.headerImage = "com/opentach/reports/ico.png";
		PermissionButton.setPropertyFile("com/opentach/client/conf/PermissionButton.properties");
		ApplicationManager.CHECK_VISIBLE_PERMISSION_ON_TOOLBAR_COMPONENTS = true;
		// Save system timezone before changing
		System.setProperty("OPENTACH_SYSTEM_DEFAULT_TIMEZONE", String.valueOf(TimeZone.getDefault().getOffset(new Date().getTime())));

		OpentachLauncher.logger.info("TimeZone:     " + TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT));
		OpentachLauncher.logger.info("Version JAVA: " + System.getProperty("java.vm.version"));
		OpentachLauncher.logger.info("Version SO:   " + System.getProperty("sun.os.patch.level"));

		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		System.out.println("Changing TimeZone to " + TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT));
		JEPUtils.registerCustomFunction("DifDate_HH_mm_ss", new DateDiff("HH:mm"));
		JEPUtils.registerCustomFunction("NumberDiff", new NumberDiff());
		ApplicationManager.CHECK_VISIBLE_PERMISSION_ON_TOOLBAR_COMPONENTS = true;

		DataField.DEFAULT_BOTTOM_MARGIN = 1;
		DataField.DEFAULT_TOP_MARGIN = 1;
		DataField.DEFAULT_FIELD_LEFT_MARGIN = 1;
		DataField.DEFAULT_FIELD_RIGHT_MARGIN = 1;
		DataField.DEFAULT_LABEL_LEFT_MARGIN = 5;
		DataField.DEFAULT_LABEL_RIGHT_MARGIN = 5;
		DataField.DEFAULT_PARENT_MARGIN = 0;
		DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL = 0;

		TextDataField.UPPERCASE_DEFAULT_VALUE = true;
		Tab.QUERY_IF_VISIBLE_DEFAULT_VALUE = false;
		Table.DEFAULT_VALUE_SET_HEIGHT_HEAD = false;
		Table.SHOW_OPEN_IN_NEW_WINDOW_MENU = false;
		Table.PERMIT_SAVE_FILTER_ORDER_CONFIGURATION = true;
		Form.defaultTableViewMinRowHeight = 22;
		TableSorter.MULTIORDER_PERMIT = true;
		BasicInteractionManager.CONFIRM_DELETE_DEFAULT_VALUE = true;
		DefaultInteractionManagerLoader.DEFAULT_INTERACTION_MANAGER = UBasicFIM.class.getName();
		OpentachLauncher.setLAF();
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("com.ontimize.util.rmitunneling.httpsession", "true");
		StatisticsRecollector.getInstance().start();
		args = OpentachLauncher.configureSystemProperties(args);

		OpentachLauncher.setOntimizeSettings();

		try {
			if ((args == null) || (args.length == 0)) {
				args = new String[] { "com/opentach/client/labels.xml", "clientapplication.xml" };
				OpentachLauncher.logger.info("Using default lauch parameters: {} , {}", args[0], args[1]);
			}
			System.setProperty(OpentachFieldNames.APP_CODE, OpentachFieldNames.APP_CODE_OPENTACH);
			OpentachApplicationLauncher.main(args);
		} catch (Exception ex) {
			OpentachLauncher.logger.error(null, ex);
			throw ex;
		}
	}

}
