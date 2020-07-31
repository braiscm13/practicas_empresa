package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.IReportService;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.client.report.JRDialogViewer;
import com.utilmize.tools.exception.UException;

import net.sf.jasperreports.engine.JasperPrint;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IMInformeCAPReport events. The class that is interested in processing a IMInformeCAPReport event implements this interface, and the object
 * created with that class is registered with a component using the component's <code>addIMInformeCAPReportListener<code> method. When the IMInformeCAPReport event occurs, that
 * object's appropriate method is invoked.
 *
 * @see IMInformeCAPReportEvent
 */
public class IMInformeCAPReportListener extends AbstractActionListenerButton {

	/** The Constant logger. */
	private static final Logger		logger	= LoggerFactory.getLogger(IMInformeCAPReportListener.class);

	/** The table. */
	@FormComponent(attr = "EInformeCAP")
	private Table					table;

	/** The c cif. */
	@FormComponent(attr = "CIF")
	private UReferenceDataField	cCif;

	/**
	 * Instantiates a new IM informe CAP report listener.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public IMInformeCAPReportListener() throws Exception {
		super();
	}

	/**
	 * Instantiates a new IM informe CAP report listener.
	 *
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public IMInformeCAPReportListener(Hashtable params) throws Exception {
		super(params);
	}

	/**
	 * Instantiates a new IM informe CAP report listener.
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
	public IMInformeCAPReportListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	/**
	 * Instantiates a new IM informe CAP report listener.
	 *
	 * @param button
	 *            the button
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public IMInformeCAPReportListener(UButton button, Hashtable params) throws Exception {
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
				return IMInformeCAPReportListener.this.createReports();
			}

			@Override
			protected void done() {
				try {
					JasperPrint jp = this.uget();

					if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
						final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(IMInformeCAPReportListener.this.getForm());
						JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation("Informe_CAP"), jd, jp);
						jv.setVisible(true);
					} else {
						throw new UException("M_NO_SE_HAN_ENCONTRADO_DATOS", null, MessageType.WARNING);
					}
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, IMInformeCAPReportListener.logger);
				}
			}
		}.executeOperation(this.getForm());
	}

	/**
	 * Gets the params.
	 *
	 * @param title
	 *            the title
	 * @param delegCol
	 *            the deleg col
	 * @return the params
	 */
	protected Map<String, Object> getParams(String title, String delegCol) {
		Hashtable<String, Object> mParams = new Hashtable<String, Object>();
		String cif = (String) this.cCif.getValue();
		if (cif != null) {
			Map<String, Object> htRow = this.cCif.getCodeValues(cif);
			String empresa = (String) htRow.get("NOMB");
			String cgContrato = (String) this.getForm().getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
			mParams.put("numreq", cgContrato);
			mParams.put("cif", cif);
			mParams.put("empresa", empresa);
		}
		mParams.put("title", title);
		mParams.put("f_informe", new Timestamp(new Date().getTime()));
		return mParams;
	}

	/**
	 * Creates the reports.
	 *
	 * @return the jasper print
	 * @throws Exception
	 *             the exception
	 */
	private JasperPrint createReports() throws Exception {
		final Map<String, Object> params = this.getParams("Informe_CAP", null);
		final OpentachClientLocator ocl = (OpentachClientLocator) this.getReferenceLocator();
		final String urljr = "com/opentach/reports/cap.jasper";
		return ocl.getRemoteService(IReportService.class).fillReport(urljr, params, null, null, null, ocl.getSessionId());
	}
}
