package com.opentach.client.modules.admin;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.xmlbeans.impl.common.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.TextComboDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.laf.LafController;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.field.UListDataField;
import com.utilmize.client.gui.field.USingleImageDataField;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMLafChooser extends UBasicFIM {

	private static final Logger logger = LoggerFactory.getLogger(IMLafChooser.class);

	@FormComponent(attr = "lafList")
	UListDataField			lafList;
	@FormComponent(attr = "images")
	USingleImageDataField	imageLabel;
	@FormComponent(attr = "fontSize")
	TextComboDataField		fontSize;

	public IMLafChooser() {
		super();
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		DefaultListModel<LafListElement> defaultListModel = new DefaultListModel<LafListElement>();
		defaultListModel.addElement(LafListElement.BLUE);
		defaultListModel.addElement(LafListElement.BLACK);
		defaultListModel.addElement(LafListElement.BROWN);
		defaultListModel.addElement(LafListElement.LIGHT_BLUE);
		defaultListModel.addElement(LafListElement.WHITE);
		defaultListModel.addElement(LafListElement.WHITE_ORANGE);
		((JList) this.lafList.getDataField()).setModel(defaultListModel);
		((JList) this.lafList.getDataField()).setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.lafList.addListSelectionListener(new LafListSelectionListener());
	}

	@Override
	public void setInitialState() {
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
	}

	public static class LafListElement {
		public final static LafListElement	BLUE			= new LafListElement(LafController.LAF_BLUE);
		public final static LafListElement	BLACK			= new LafListElement(LafController.LAF_BLACK);
		public final static LafListElement	BROWN			= new LafListElement(LafController.LAF_BROWN);
		public final static LafListElement	LIGHT_BLUE		= new LafListElement(LafController.LAF_LIGHT_BLUE);
		public final static LafListElement	WHITE			= new LafListElement(LafController.LAF_WHITE);
		public final static LafListElement	WHITE_ORANGE	= new LafListElement(LafController.LAF_WHITE_ORANGE);

		private final String lafName;

		public LafListElement(String name) {
			super();
			this.lafName = name;
		}

		public String getLafName() {
			return this.lafName;
		}

		URI getImageURI() {
			String property = System.getProperty("BASE_JNLP_URI");
			if (property == null) {
				return null;
			}
			try {
				return new URI(property + "/laf/" + this.lafName + ".png");
			} catch (URISyntaxException e) {
				return null;
			}
		}

		@Override
		public String toString() {
			return ApplicationManager.getTranslation(this.lafName, ApplicationManager.getApplicationBundle());
		}
	}

	private class LafListSelectionListener implements ListSelectionListener {

		public LafListSelectionListener() {
			super();
		}

		@Override
		public void valueChanged(ListSelectionEvent event) {
			final int[] selectedIndices = ((JList<LafListElement>) IMLafChooser.this.lafList.getDataField()).getSelectedIndices();
			if ((selectedIndices.length > 1) || (selectedIndices.length < 1)) {
				IMLafChooser.this.imageLabel.setValue(null);
			} else {
				if (event.getValueIsAdjusting()) {
					return;
				}
				new USwingWorker<BytesBlock, Void>() {

					@Override
					protected BytesBlock doInBackground() throws Exception {
						try {
							LafListElement element = ((JList<LafListElement>) IMLafChooser.this.lafList.getDataField()).getModel()
									.getElementAt(selectedIndices[0]);
							if (element.getImageURI() != null) {
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								try (InputStream is = element.getImageURI().toURL().openStream()) {
									IOUtil.copyCompletely(is, baos);
								}
								return new BytesBlock(baos.toByteArray());
							}
						} catch (Exception ex) {
							throw new Exception("IMAGE_NOT_FOUND", ex);
						}
						throw new Exception("IMAGE_NOT_FOUND");
					}

					@Override
					protected void done() {
						try {
							BytesBlock image = super.uget();
							IMLafChooser.this.imageLabel.setValue(image);
						} catch (Throwable error) {
							IMLafChooser.this.imageLabel.setValue(null);
							IMLafChooser.logger.error(null, error);
							// UMessageManager.getMessageManager().showExceptionMessage(error, IMLafChooser.logger);
						}

					}
				}.executeOperation(IMLafChooser.this.managedForm);

			}
		}

	}
}