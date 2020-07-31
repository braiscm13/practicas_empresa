package com.opentach.client.modules.data;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.comp.calendar.MonthWrapperPanel;
import com.opentach.client.comp.calendar.UCalendarPanel;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.util.ResourceManager;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.client.report.JRDialogViewer;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractSvgRenderer;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.Graphics2DRenderable;

/**
 * The listener interface for receiving IMCalendarioUsosReportBuilderAction events. The class that is interested in processing a IMCalendarioUsosReportBuilderAction event
 * implements this interface, and the object created with that class is registered with a component using the component's <code>addIMCalendarioUsosReportBuilderActionListener<code>
 * method. When the IMCalendarioUsosReportBuilderAction event occurs, that object's appropriate method is invoked.
 *
 * @see IMCalendarioUsosReportBuilderActionEvent
 */
public class IMCalendarioUsosReportBuilderActionListener extends AbstractActionListenerButton implements DataRenderable, Graphics2DRenderable{

	/** The Constant logger. */
	private static final Logger		logger	= LoggerFactory.getLogger(IMCalendarioUsosReportBuilderActionListener.class);

	/** The calendar. */
	@FormComponent(attr = "CALENDAR")
	private UCalendarPanel			calendar;

	/** The cif. */
	@FormComponent(attr = "CIF")
	private UReferenceDataField	cif;

	/** The dni. */
	@FormComponent(attr = OpentachFieldNames.IDCONDUCTOR_FIELD)
	private UReferenceDataField	dni;

	/** The table. */
	@FormComponent(attr = "EInformeUsoVehiculoConductor")
	private Table					table;

	/**
	 * Instantiates a new IM calendario usos report builder action listener.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public IMCalendarioUsosReportBuilderActionListener() throws Exception {
		super();
	}

	/**
	 * Instantiates a new IM calendario usos report builder action listener.
	 *
	 * @param button
	 *            the button
	 * @param formComponent
	 *            the form component
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public IMCalendarioUsosReportBuilderActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	/**
	 * Instantiates a new IM calendario usos report builder action listener.
	 *
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public IMCalendarioUsosReportBuilderActionListener(Hashtable params) throws Exception {
		super(params);
	}

	/**
	 * Instantiates a new IM calendario usos report builder action listener.
	 *
	 * @param button
	 *            the button
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public IMCalendarioUsosReportBuilderActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.buttons.AbstractActionListenerButton#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		new USwingWorker<JasperPrint, Void>() {

			@Override
			protected JasperPrint doInBackground() throws Exception {
				return IMCalendarioUsosReportBuilderActionListener.this.generateReportJasper();
			}

			@Override
			protected void done() {
				try {
					JasperPrint jp = this.uget();
					final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(IMCalendarioUsosReportBuilderActionListener.this.getForm());
					JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation("Calendario"), jd, jp);
					jv.setVisible(true);
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, IMCalendarioUsosReportBuilderActionListener.logger);
				}
			}
		}.executeOperation(this.getForm());
	}

	/**
	 * Generate report jasper.
	 *
	 * @return the jasper print
	 * @throws Exception
	 *             the exception
	 */
	protected JasperPrint generateReportJasper() throws Exception {
		Map<String, Object> companyData = this.cif.getCodeValues(this.cif.getValue());
		Map<String, Object> driverData = this.dni.getCodeValues(this.dni.getValue());
		Map<String, Object> params = new HashMap<>();
		params.put("title", "title_inform_calendario_usos");
		params.put("empresa", companyData.get("NOMB"));
		params.put("conductor", driverData.get("NOMBRE") + " " + driverData.get("APELLIDOS"));
		params.put("f_informe", new Date());
		params.put("calendario", new CalendarComponentRenderer(this.calendar));
		params.put(JRParameter.REPORT_LOCALE, ApplicationManager.getLocale() == null ? new Locale("es", "ES") : ApplicationManager.getLocale());
		ResourceBundle rb = ResourceManager.getBundle((Locale) params.get(JRParameter.REPORT_LOCALE));
		if (rb != null) {
			params.put(JRParameter.REPORT_RESOURCE_BUNDLE, rb);
		}
		EntityResult result = new EntityResult((Hashtable) this.table.getValue());
		return JRReportUtil.fillReport(JRReportUtil.getJasperReport("com/opentach/reports/calendario_uso_vehiculo.jasper"), params, result);
	}


	/**
	 * The Class CalendarComponentRenderer.
	 */
	public static class CalendarComponentRenderer extends JRAbstractSvgRenderer  implements DataRenderable, Graphics2DRenderable  {

		/** The Constant serialVersionUID. */
		private static final long		serialVersionUID	= 1L;

		/** The component. */
		private final UCalendarPanel	component;

		/**
		 * Instantiates a new calendar component renderer.
		 *
		 * @param component
		 *            the component
		 */
		public CalendarComponentRenderer(UCalendarPanel component) {
			this.component = component;
		}

		/*
		 * (non-Javadoc)
		 * @see net.sf.jasperreports.engine.JRAbstractSvgRenderer#getDimension(net.sf.jasperreports.engine.JasperReportsContext)
		 */
		@Override
		public Dimension2D getDimension(JasperReportsContext jasperReportsContext) {
			// return component.getSize();
			return null;
		}

		/**
		 * Gets the dimension.
		 *
		 * @return the dimension
		 * @deprecated Replaced by {@link #getDimension(JasperReportsContext)}.
		 */
		@Deprecated
		@Override
		public Dimension2D getDimension() {
			return this.getDimension(DefaultJasperReportsContext.getInstance());
		}

		/*
		 * (non-Javadoc)
		 * @see net.sf.jasperreports.engine.JRAbstractRenderer#render(net.sf.jasperreports.engine.JasperReportsContext, java.awt.Graphics2D, java.awt.geom.Rectangle2D)
		 */
		@Override
		public void render(JasperReportsContext jasperReportsContext, Graphics2D gr, Rectangle2D rectangle) {
			Graphics2D grx = (Graphics2D) gr.create((int) rectangle.getX(), (int) rectangle.getY(), (int) rectangle.getWidth(), (int) rectangle.getHeight());
			try {
				MonthWrapperPanel[] months = this.component.getCalendarPanel().getMonths();
				int[] layout = this.computeLayout(months.length);
				int rowCount = layout[0];
				int columnCount = layout[1];
				Dimension size = months[0].getSize();

				Dimension monthSize = new Dimension((int) Math.floor(rectangle.getWidth() / columnCount), (int) Math.floor(rectangle.getHeight() / rowCount));
				double sx = monthSize.getWidth() / size.getWidth();
				double sy = monthSize.getHeight() / size.getHeight();
				double scale = sx < sy ? sx : sy;

				for (int i = 0; i < months.length; i++) {
					AffineTransform transform = grx.getTransform();
					try {
						grx.translate((i * monthSize.width) % (monthSize.width * columnCount), Math.floor(i / columnCount) * monthSize.height);
						grx.scale(scale, scale);
						months[i].print(grx);
					} finally {
						grx.setTransform(transform);
					}
				}
			} catch (Exception error) {
				throw new JRRuntimeException(error);
			} finally {
				grx.dispose();
			}
		}

		/**
		 * Compute layout.
		 *
		 * @param length
		 *            the length
		 * @return the int[]
		 */
		private int[] computeLayout(int length) {
			int rowCount = 0;
			int columnCount = 0;
			switch (length) {
				case 0:
				case 1:
					rowCount = 1;
					columnCount = 1;
					break;
				case 2:
					rowCount = 1;
					columnCount = 2;
					break;
				case 3:
				case 4:
					rowCount = 2;
					columnCount = 2;
					break;
				case 5:
				case 6:
					rowCount = 3;
					columnCount = 2;
					break;
				case 7:
				case 8:
				case 9:
					rowCount = 3;
					columnCount = 3;
					break;
				default:
					rowCount = 4;
					columnCount = 3;
					break;
			}
			return new int[] { rowCount, columnCount };
		}

		/*
		 * (non-Javadoc)
		 * @see net.sf.jasperreports.engine.JRRenderable#render(java.awt.Graphics2D, java.awt.geom.Rectangle2D)
		 */
		@Override
		public void render(Graphics2D grx, Rectangle2D rectangle) {
			this.render(DefaultJasperReportsContext.getInstance(), grx, rectangle);
		}

		@Override
		public byte[] getData(JasperReportsContext jasperReportsContext) throws JRException {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Override
	public byte[] getData(JasperReportsContext jasperReportsContext) throws JRException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void render(JasperReportsContext jasperReportsContext, Graphics2D grx, Rectangle2D rectangle)
			throws JRException {
		// TODO Auto-generated method stub
		
	}
}
