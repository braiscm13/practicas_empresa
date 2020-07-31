package com.opentach.client.modules.chart;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.RectangleReadOnly;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DataLabel;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.comp.activitychart.ChartDataField;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMGraficaCondReportBuilderActionListener extends AbstractActionListenerButton {

	private static final Logger			logger	= LoggerFactory.getLogger(IMGraficaCondReportBuilderActionListener.class);
	@FormComponent(attr = "chart")
	private ChartDataField				chartwpp;
	@FormComponent(attr = "CIF")
	protected UReferenceDataField		cif;
	@FormComponent(attr = OpentachFieldNames.IDCONDUCTOR_FIELD)
	protected UReferenceDataField		dni;
	@FormComponent(attr = "NUM_TARJ")
	protected UReferenceDataField	tarj;
	@FormComponent(attr = "ETI_TIT")
	protected DataLabel					etiTit;

	@FormComponent(attr = "bULTIMA_SEMANA")
	private Button						bLastWeek;
	@FormComponent(attr = "right")
	private Button						bRight;
	@FormComponent(attr = "left")
	private Button						bLeft;

	public IMGraficaCondReportBuilderActionListener() throws Exception {
		super();
	}

	public IMGraficaCondReportBuilderActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMGraficaCondReportBuilderActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMGraficaCondReportBuilderActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		new USwingWorker<File, Void>() {

			@Override
			protected File doInBackground() throws Exception {
				return IMGraficaCondReportBuilderActionListener.this.generateReport();
			}

			@Override
			protected void done() {
				try {
					File ftemp = this.uget();
					Desktop.getDesktop().open(ftemp);
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, IMGraficaCondReportBuilderActionListener.logger);
				}
			}
		}.executeOperation(this.getForm());

	}

	protected File generateReport() throws Exception {
		FileOutputStream fos = null;
		try {
			File ftemp = File.createTempFile("GRAFICA", ".pdf");
			fos = new FileOutputStream(ftemp);

			Map<String, Object> companyData = this.cif.getCodeValues(this.cif.getValue());
			Map<String, Object> driverData = this.dni.getCodeValues(this.dni.getValue());
			Map<String, Object> driverNumTarj = this.tarj.getValuesToCode(this.tarj.getValue());

			this.generateReport(fos, companyData, driverData, driverNumTarj);
			fos.close();
			return ftemp;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	protected void generateReport(FileOutputStream fos, Map<String, Object> companyData, Map<String, Object> driverData, Map<String, Object> driverNumTarj) throws Exception {
		Document document = new Document(new RectangleReadOnly(PageSize.A4.getHeight(), PageSize.A4.getWidth()), 20, 20, 20, 20);
		PdfWriter writer = PdfWriter.getInstance(document, fos);
		document.open();
		PdfContentByte pdfContent = writer.getDirectContent();
		// Prepare graphics
		float availableWidth = document.getPageSize().getWidth();
		float availableHeight = document.getPageSize().getHeight();
		Rectangle bounds = new Rectangle();

		int numWeek = 0;
		while (this.bLastWeek.isEnabled()) {
			this.bRight.doClick();
			numWeek++;
		}

		boolean first = true;
		Graphics2D gFull = null;
		Graphics2D g2d = null;
		boolean hasToPrint = true;
		Thread.sleep(200);
		while (hasToPrint) {
			if (!first) {
				document.newPage();
				document.add(new Paragraph());
			} else {
				first = false;
			}
			gFull = pdfContent.createGraphics(availableWidth, availableHeight, new DefaultFontMapper());
			g2d = (Graphics2D) gFull.create((int) document.left(), (int) document.bottom(), (int) (document.right() - document.left()), (int) (document.top() - document.bottom()));
			this.printHeader(g2d, bounds, pdfContent, document, availableWidth, availableHeight, companyData, driverNumTarj, driverData);
			this.printSpecificGrafics(g2d, bounds, availableWidth, availableHeight);
			g2d.dispose();
			gFull.dispose();
			hasToPrint = this.bLeft.isEnabled();
			this.bLeft.doClick();
			Thread.sleep(100);
		}
		document.close();

		// volvemos a posicionar la gráfica donde estaba antes de generar el pdf
		this.bLastWeek.doClick();
		while (numWeek > 0) {
			this.bLeft.doClick();
			numWeek--;
		}
	}

	private void printSpecificGrafics(Graphics2D g2d, Rectangle bounds, float availableWidth, float availableHeight) {

		bounds.setBounds(this.drawRightString(g2d, ApplicationManager.getTranslation(this.etiTit.getText()), bounds.y, 0));
		int heightRel = bounds.y + bounds.height + 10;
		Graphics2D gAux = (Graphics2D) g2d.create(0, heightRel, (int) (availableWidth), (int) (availableHeight - bounds.y - bounds.height));
		int cW = this.chartwpp.getWidth();
		int cH = this.chartwpp.getHeight();
		double scale = this.calculeScale(cW, cH, gAux.getClipBounds().width, gAux.getClipBounds().height);
		gAux.scale(scale, scale);
		this.chartwpp.print(gAux);
		gAux.dispose();
	}

	private void printHeader(Graphics2D g2d, Rectangle bounds, PdfContentByte pdfContent, Document document, float availableWidth, float availableHeight,
			Map<String, Object> companyData, Map<String, Object> driverNumTarj, Map<String, Object> driverData) {

		// Begin paint
		g2d.setPaint(Color.BLACK);

		// Title
		g2d.setFont(g2d.getFont().deriveFont(16f).deriveFont(Font.BOLD));
		Rectangle localBounds = this.drawCenteredString(g2d, ApplicationManager.getTranslation("GRAFICA_ACTIVIDADES_CONDUCTOR"), 0);
		g2d.setFont(g2d.getFont().deriveFont(12f));

		// Company data
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
		localBounds = this.drawString(g2d, ApplicationManager.getTranslation("EMPRESA"), 0, localBounds.y + localBounds.height + 0);

		g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
		localBounds = this.drawString(g2d, ApplicationManager.getTranslation("CIF") + ": " + companyData.get("CIF"), 0, localBounds.y + localBounds.height + 5);
		localBounds = this.drawString(g2d, ApplicationManager.getTranslation("NAME") + ": " + companyData.get("NOMB"), localBounds.x + localBounds.width + 30, localBounds.y);

		// Driver data
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));

		if (driverNumTarj.get("NUM_TARJETA") != null) {
			localBounds = this
					.drawString(
							g2d,
							ApplicationManager.getTranslation("CONDUCTOR") + "       " + ApplicationManager.getTranslation("NUM_TARJ") + "   " + (String) driverNumTarj
							.get("NUM_TARJETA") + (String) driverNumTarj.get("INDICE_CONS") + (String) driverNumTarj.get("INDICE_RENOV") + (String) driverNumTarj
							.get("INDICE_SUST"), 0, localBounds.y + localBounds.height + 5);
		} else {
			localBounds = this.drawString(g2d, ApplicationManager.getTranslation("CONDUCTOR"), 0, localBounds.y + localBounds.height + 5);
		}

		g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
		localBounds = this.drawString(g2d, ApplicationManager.getTranslation("DNI") + ": " + driverData.get("DNI"), 0, localBounds.y + localBounds.height + 5);
		localBounds = this.drawString(g2d, ApplicationManager.getTranslation("NAME") + ": " + driverData.get("APELLIDOS") + ", " + driverData.get("NOMBRE"),
				localBounds.x + localBounds.width + 30, localBounds.y);
		bounds.setBounds(localBounds);
	}

	private Rectangle drawString(Graphics2D g2d, String str, int x, int yTop) {
		FontMetrics fmetrics = g2d.getFontMetrics();
		int stringWidth = fmetrics.stringWidth(str);
		int baseLine = (yTop + fmetrics.getHeight()) - fmetrics.getDescent();
		g2d.drawString(str, x, baseLine);
		Rectangle res = new Rectangle(x, yTop, stringWidth, fmetrics.getHeight());
		return res;
	}

	private Rectangle drawRightString(Graphics2D g2d, String str, int yTop, int xmargin) {
		double width = g2d.getClipBounds().getWidth();
		FontMetrics fmetrics = g2d.getFontMetrics();
		int stringWidth = fmetrics.stringWidth(str);
		int baseLine = (yTop + fmetrics.getHeight()) - fmetrics.getDescent();
		int x = (int) (width - stringWidth - xmargin);
		g2d.drawString(str, x, baseLine);
		Rectangle res = new Rectangle(x, yTop, stringWidth, fmetrics.getHeight());
		return res;
	}

	private Rectangle drawCenteredString(Graphics2D g2d, String str, int yTop) {
		double width = g2d.getClipBounds().getWidth();
		FontMetrics fmetrics = g2d.getFontMetrics();
		int stringWidth = fmetrics.stringWidth(str);
		int x = (int) ((width - stringWidth) / 2);
		int baseLine = fmetrics.getHeight() - fmetrics.getDescent();
		g2d.drawString(str, x, baseLine);
		Rectangle res = new Rectangle(x, yTop, stringWidth, fmetrics.getHeight());
		return res;
	}

	private double calculeScale(double aW, double aH, double bW, double bH) {
		double wScale = bW / aW;
		double hScale = bH / aH;
		double scale = Math.min(wScale, hScale);
		return scale;
	}
}
