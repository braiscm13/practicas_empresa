/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
package com.ontimize.report.engine.dynamicjasper5;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.print.PageFormat;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.WindowConstants;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.report.utils.PreviewDialog;
import com.ontimize.report.utils.ReportProcessor;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.view.JasperViewer;

public class DynamicJasperPreviewDialog extends PreviewDialog {

	private static final Logger logger = LoggerFactory.getLogger(DynamicJasperPreviewDialog.class);

	protected JasperPrint report;

	public DynamicJasperPreviewDialog(Dialog o, String title, JasperPrint jp) {
		super(o, title, true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.autoPackOnOpen = false;
		this.report = jp;
		this.add(new JasperViewer(this.report));
	}

	public DynamicJasperPreviewDialog(Frame o, String title) {
		super(o, title, true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.autoPackOnOpen = false;
		this.add(new JasperViewer(this.report));
	}

	@Override
	public Object getReport() {
		return this.report;
	}

	@Override
	public void print(boolean showPrintDialog) {
		try {
			JasperPrintManager.printReport(this.report, showPrintDialog);
		} catch (JRException e) {
			DynamicJasperPreviewDialog.logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void setReport(TableModel m, String template, URL reportBase) throws Exception {
		this.report = JasperFillManager.fillReport(template, new Hashtable());
	}

	@Override
	public void setReport(TableModel m, URL template, URL base, String[] order, boolean[] asc, ReportProcessor r, PageFormat pf) throws Exception {
		this.report = JasperFillManager.fillReport(template.getPath(), new Hashtable());
	}

}
