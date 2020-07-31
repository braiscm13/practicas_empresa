package com.opentach.downclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.util.TimeZone;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.container.Tab;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.Label;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.field.WWWDataField;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.TableSorter;
import com.ontimize.plaf.utils.StyleUtil;
import com.ontimize.util.JEPUtils;
import com.ontimize.util.xls.XLSExporterFactory;
import com.ontimize.xml.DefaultXMLParametersManager;
import com.opentach.client.comp.fnc.DateDiff;
import com.opentach.client.comp.fnc.NumberDiff;
import com.opentach.client.util.OpentachApplicationLauncher;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.ClientLauncherUtils;
import com.utilmize.client.gui.field.table.UTable;

public final class DownCenterLauncher {

	private static final Logger	logger	= LoggerFactory.getLogger(DownCenterLauncher.class);

	private DownCenterLauncher() {
		super();
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		System.setProperty("com.ontimize.util.rmitunneling.httpsession", "true");
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		args = ClientLauncherUtils.configureSystemProperties(args);

		// Change LAF CSS depending on frame screen size
		DownCenterLauncher.customizeLAFCss();

		// To improve behaviour loading first tables
		try {
			new UTable(null);
		} catch (Exception e) {
		}

		Table.renderReportValues = false;
		Table.XLS_EXPORT_CLASS = XLSExporterFactory.POI_3_5;
		System.setProperty("com.ontimize.report.templates",
				"com/opentach/reports/default_portrait_jasper_4.6.jrxml;com/opentach/reports/default_landscape_jasper_4.6.jrxml");

		ApplicationManager.CHECK_VISIBLE_PERMISSION_ON_TOOLBAR_COMPONENTS = true;
		try {
			if ((args == null) || (args.length == 0)) {
				args = new String[] { "downcenter_labels.xml", "clientapplication-downcenter.xml" };
				DownCenterLauncher.logger.info("Using default lauch parameters: {} , {}", args[0], args[1]);
			}
			DownCenterLauncher.logger.info("TimeZone:     " + TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT));
			DownCenterLauncher.logger.info("Version JAVA: " + System.getProperty("java.vm.version"));
			DownCenterLauncher.logger.info("Version SO:   " + System.getProperty("sun.os.patch.level"));

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
			Label.DEFAULT_PARENT_MARGIN = 0;
			Label.DEFAULT_BOTTOM_MARGIN = 0;
			Label.DEFAULT_TOP_MARGIN = 0;
			Label.DEFAULT_LABEL_LEFT_MARGIN = 0;
			Label.DEFAULT_LABEL_RIGHT_MARGIN = 0;

			TextDataField.UPPERCASE_DEFAULT_VALUE = true;
			Tab.QUERY_IF_VISIBLE_DEFAULT_VALUE = false;
			Table.DEFAULT_VALUE_SET_HEIGHT_HEAD = false;
			Table.SHOW_OPEN_IN_NEW_WINDOW_MENU = false;
			Table.PERMIT_SAVE_FILTER_ORDER_CONFIGURATION = true;
			Form.defaultTableViewMinRowHeight = 22;
			TableSorter.MULTIORDER_PERMIT = true;
			BasicInteractionManager.CONFIRM_DELETE_DEFAULT_VALUE = true;


			DefaultXMLParametersManager.add(WWWDataField.class.getName(), "uppercase", "no");

			System.setProperty(OpentachFieldNames.APP_CODE, OpentachFieldNames.APP_CODE_OPENTACH);
			OpentachApplicationLauncher.main(args);
			UIManager.getDefaults().put("List.foreground", new Color(0x335971));
			UIManager.getDefaults().put("List.selectionBackground", new Color(0x335971));
		} catch (Exception ex) {
			DownCenterLauncher.logger.error(null, ex);
			throw ex;
		}
	}

	/**
	 * Method to change (if possible) LAF CSS file according to screen size.
	 * For default file configured (argument) will be looked for <fileName><cssSuffix><.css> file when small screens.
	 */
	private static void customizeLAFCss() {
		Dimension dimension = null;
		if ((System.getProperty("screen-width") != null) && (System.getProperty("screen-height") != null)) {
			dimension = new Dimension(Integer.parseInt(System.getProperty("screen-width", "1035")), Integer.parseInt(System.getProperty(
					"screen-height", "800")));
		} else {
			dimension = Toolkit.getDefaultToolkit().getScreenSize();
		}
		String cssToUse = "";
		if ((dimension.width < 1000) || (dimension.height < 700)) {
			cssToUse = "Small";
		}

		if ((cssToUse != null) && !"".equals(cssToUse)) {
			String property = System.getProperty(StyleUtil.STYLE_PROPERTY);
			if (property != null) {
				String customCssFileName = property.substring(0, property.lastIndexOf(".")) + cssToUse + property
						.substring(property.lastIndexOf("."));
				URL input = StyleUtil.class.getClassLoader().getResource(customCssFileName);
				if (input != null) {
					System.setProperty(StyleUtil.STYLE_PROPERTY, customCssFileName);
				} else {
					DownCenterLauncher.logger.warn("LAF CSS suffixed \"" + customCssFileName + "\"not found, will be continued with original.");
				}
			} else {
				DownCenterLauncher.logger.warn("LAF CSS suffix ignored due to not configured CSS file.");
			}
		}
	}
}
