package com.opentach.client.modules.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.IdentifiedAbstractFormComponent;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.comp.activitychart.ActivityChartControlPanel;
import com.opentach.client.comp.activitychart.ChartDataField;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMGraficaFicheros extends IMReportRoot {
	private static final String	EFICHEROS_SUBIDOS	= "EFicherosSubidos";
	protected ReferenceLocator	buscador			= null;
	private ChartDataField		chartwpp			= null;
	private DateDataField		cfi					= null;
	private DateDataField		cff					= null;

	@FormComponent(attr = "chartControl")
	protected ActivityChartControlPanel	chartControl;

	protected EntityResult		resultInfracciones;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD, OpentachFieldNames.FECFIN_FIELD));
		this.buscador = (ReferenceLocator) this.formManager.getReferenceLocator();
		this.chartwpp = (ChartDataField) this.managedForm.getElementReference("chart");

		UReferenceDataField creCIF = (UReferenceDataField) this.managedForm.getDataFieldReference("CIF");
		if (creCIF != null) {
			creCIF.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent ve) {
					try {
						if (ve.getType() == ValueEvent.USER_CHANGE) {
							IMGraficaFicheros.this.refreshTables();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		}

		this.cfi = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
		this.cff = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
		ValueChangeListener datelist = new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent ve) {
				IMGraficaFicheros.this.chartControl.setDateRange((Date) IMGraficaFicheros.this.cfi.getValue(),
						(Date) IMGraficaFicheros.this.cff.getValue());
			}
		};
		this.cfi.addValueChangeListener(datelist);
		this.cff.addValueChangeListener(datelist);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.enableButtons();
		this.managedForm.enableDataFields();
		this.initFieldChain();
		this.chartControl.checkButtonStatus();
	}

	@Override
	public void refreshTables() {
		EntityResult activs = this.getFicheros();
		this.chartwpp.setEntityData(IMGraficaFicheros.EFICHEROS_SUBIDOS, activs);
		this.refreshChart();
	}

	@Override
	public void refreshChart() {
		try {
			if (this.chartwpp == null) {
				return;
			}

			if ((this.chartControl.getStartDate() != null) && (this.chartControl.getEndDate() != null) && !this.chartControl.getStartDate().before(
					this.chartControl.getEndDate())) {
				if (this.chartwpp.getChart() != null) {
					this.chartwpp.getChart().removeAllData();
				}
			} else {
				Date fecIni = this.chartControl.getChartStartDate();
				Date fecFin = this.chartControl.getChartEndDate();

				this.chartControl.checkButtonStatus();
				this.chartControl.setTitle(fecIni, fecFin);

				if ((fecIni != null) && (fecFin != null) && fecIni.before(fecFin)) {
					String idorigen = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.IDORIGEN_FIELD);

					if ((fecIni == null) || (fecFin == null) || (idorigen == null)) {
						return;
					}
					// // Coloco la etiqueta que permite conocer el resumen de
					// los datos.
					// if (chartwpp.chart == null)
					// chartwpp.setInfoLabel(overview);

					if ((this.chartwpp != null) && this.chartwpp.isVisible()) {
						this.chartwpp.configurarChart(fecIni, fecFin, 7,"FERRY/OUT_TRAIN" ,"INFRACCIONES");
					}

				} else {
					if (this.chartwpp.getChart() != null) {
						this.chartwpp.getChart().removeAllData();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			this.managedForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
		}
	}


	@Override
	protected void doOnBuildReport() {
		final IdentifiedAbstractFormComponent grafica = (IdentifiedAbstractFormComponent) this.managedForm.getElementReference("chart");
		if (grafica != null) {

			Printable p = new Printable() {
				@Override
				public int print(Graphics g, PageFormat pageFormat, int page) {
					// --- Create the Graphics2D object
					Graphics2D g2d = (Graphics2D) g;
					// --- Translate the origin to 0,0 for the top left corner
					g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
					// --- Set the drawing color to black
					g2d.setPaint(Color.black);
					// --- Draw a border arround the page using a 12 point
					// border
					// Rectangle2D.Double border = new Rectangle2D.Double(0, 0,
					// pageFormat.getImageableWidth(),
					// pageFormat.getImageableHeight());
					// g2d.draw(border);
					StringBuffer sb = new StringBuffer();
					sb.append("");

					g2d.setFont(g2d.getFont().deriveFont(14));
					g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
					g2d.drawString("GRÁFICA DE ACTIVIDADES DE CONDUCTOR", 0, 0);

					g2d.setFont(g2d.getFont().deriveFont(12));
					g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
					UReferenceDataField cif = (UReferenceDataField) IMGraficaFicheros.this.managedForm.getDataFieldReference("CIF");
					Map ve = cif.getCodeValues(cif.getValue());

					g2d.drawString("Empresa", 0, g2d.getFontMetrics().getHeight() + g2d.getFontMetrics().getDescent());
					g2d.drawString("CIF: " + ve.get("CIF"), 0, 2 * (g2d.getFontMetrics().getHeight() + g2d.getFontMetrics().getDescent()));
					g2d.drawString("Nombre: " + ve.get("NOMB"), 110, 2 * (g2d.getFontMetrics().getHeight() + g2d.getFontMetrics().getDescent()));

					UReferenceDataField dni = (UReferenceDataField) IMGraficaFicheros.this.managedForm
							.getDataFieldReference(OpentachFieldNames.IDCONDUCTOR_FIELD);
					Map v = dni.getCodeValues(dni.getValue());
					g2d.drawString("Conductor", 0, 50 + g2d.getFontMetrics().getHeight() + g2d.getFontMetrics().getDescent());
					g2d.drawString("DNI: " + v.get("DNI"), 0, 50 + (2 * (g2d.getFontMetrics().getHeight() + g2d.getFontMetrics().getDescent())));
					g2d.drawString("Nombre: " + v.get("APELLIDOS") + ", " + v.get("NOMBRE"), 110, 50 + (2 * (g2d.getFontMetrics().getHeight() + g2d
							.getFontMetrics().getDescent())));

					BufferedImage buf = new BufferedImage(grafica.getWidth(), grafica.getHeight(), BufferedImage.TYPE_INT_ARGB);
					grafica.print(buf.createGraphics());
					double scalefactor = Math.min((pageFormat.getImageableWidth()) / grafica.getWidth(),
							(pageFormat.getImageableHeight()) / grafica.getHeight());
					scalefactor -= 0.05;
					AffineTransform af = AffineTransform.getScaleInstance(scalefactor, scalefactor);
					af.translate(0, 200);
					// AffineTransform
					// af=AffineTransform.getRotateInstance(Math.PI/2);
					// g2d.setTransform(af);
					g2d.drawImage(buf, af, grafica);

					// --- Print the text one inch from the top and laft margins
					// g2d.drawString("This the content page", POINTS_PER_INCH,
					// POINTS_PER_INCH);
					// --- Validate the page
					return (Printable.PAGE_EXISTS);

				}
			};
			// --- Create a new PrinterJob object
			PrinterJob printJob = PrinterJob.getPrinterJob();
			Book book = new Book();

			// --- Add the cover page using the default page format for this
			// print
			// job
			// book.append(p, printJob.defaultPage());

			// --- Add the document page using a landscape page format
			PageFormat documentPageFormat = new PageFormat();
			documentPageFormat.setOrientation(PageFormat.LANDSCAPE);
			book.append(p, documentPageFormat);

			// --- Tell the printJob to use the book as the pageable object
			printJob.setPageable(book);

			if (printJob.printDialog()) {
				try {
					printJob.print();
				} catch (Exception PrintException) {
					PrintException.printStackTrace();
				}
			}
		}
	}

	private EntityResult getFicheros() {
		try {
			String numReq = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
			String idorigen = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.IDORIGEN_FIELD);
			ReferenceLocator buscador = (ReferenceLocator) this.managedForm.getFormManager().getReferenceLocator();
			Entity ent = buscador.getEntityReference(IMGraficaFicheros.EFICHEROS_SUBIDOS);
			Hashtable cv = new Hashtable();
			cv.put(OpentachFieldNames.NUMREQ_FIELD, numReq);
			cv.put(OpentachFieldNames.CG_CONTRATO_FIELD, numReq);
			cv.put(OpentachFieldNames.IDORIGEN_FIELD, idorigen);
			Vector v = new Vector(2);
			v.add(OpentachFieldNames.FECINI_FIELD);
			v.add(OpentachFieldNames.FECFIN_FIELD);
			cv.putAll(this.getDateFilterValues(v));
			EntityResult res = ent.query(cv, new Vector(0), buscador.getSessionId());
			return res;
		} catch (Exception nex) {
			// System.err.println("" + ex.getMessage());
		}
		return null;
	}
}
