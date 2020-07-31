package com.opentach.downclient;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.gui.field.UFxHtmlDataField;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.paint.Color;

/**
 * The Class IMMainWizardNewsController.
 */
public class IMMainWizardNewsController {

	/** The Constant logger. */
	private static final Logger			logger					= LoggerFactory.getLogger(IMMainWizardNewsController.class);

	/** The Constant RECHECK_NEWS_TIME_MIN. */
	private final static long			RECHECK_NEWS_TIME	= 10 * 60l * 1000l;

	/** The form manager. */
	private static IFormManager			formManager;


	/** The html field. */
	@FormComponent(attr = "NewsView")
	private UFxHtmlDataField			htmlField;

	/** The news queue. */
	private final BlockingQueue<News>	newsQueue;

	/** The timer. */
	private final Timer					timer;

	/**
	 * Instantiates a new IM main wizard news controller.
	 *
	 * @param form
	 *            the form
	 * @param formManager
	 *            the form manager
	 */
	public IMMainWizardNewsController(Form form, IFormManager formManager) {
		super();
		IMMainWizardNewsController.formManager = formManager;
		FIMUtils.injectAnnotatedFields(this, form);
		this.htmlField.setOpaque(false);
		this.htmlField.getDataField().setOpaque(false);
		// this.htmlField.getDataField().setBackground(java.awt.Color.BLUE);

		((JFXPanel) this.htmlField.getDataField()).getScene().setFill(new Color(0.0f, 0.0f, 0.0f, 0.0f));

		this.htmlField.getWebView().getEngine().documentProperty().addListener(new DocListener());
		this.timer = new Timer();
		this.newsQueue = new LinkedBlockingQueue<News>();
	}

	/**
	 * Start.
	 */
	public void start() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				IMMainWizardNewsController.this.queryNews();
				IMMainWizardNewsController.this.playNextNews();
			}
		}, "news-init-thread").start();

	}

	/**
	 * Query news.
	 */
	private void queryNews() {
		try {
			Entity entity = IMMainWizardNewsController.formManager.getReferenceLocator().getEntityReference("ECDONews");
			String locale = ApplicationManager.getApplication().getLocale().toString();
			EntityResult res = entity.query(EntityResultTools.keysvalues("ACTIVE", "S", "LOCALE", locale), EntityResultTools.attributes("IDCDONEW",
					"CONTENT",
					"TIME_TO_SHOW"), IMMainWizardNewsController.formManager.getReferenceLocator().getSessionId());
			CheckingTools.checkValidEntityResult(res);
			int nregs = res.calculateRecordNumber();
			this.newsQueue.clear();
			for (int i = 0; i < nregs; i++) {
				Hashtable recordValues = res.getRecordValues(i);
				Object id = recordValues.get("IDCDONEW");
				String content = (String) recordValues.get("CONTENT");
				Long timeToShow = ((Number) recordValues.get("TIME_TO_SHOW")).longValue();
				this.newsQueue.add(new News(id, content, timeToShow));
			}
			if (nregs == 0) {
				this.newsQueue.add(new News(-1, "", 5000l));
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
		this.timer.schedule(new RequeryNewsTask(), IMMainWizardNewsController.RECHECK_NEWS_TIME);
	}

	/**
	 * Play next news.
	 */
	private void playNextNews() {
		News current = null;
		while (current == null) {
			try {
				current = this.newsQueue.take();
				this.newsQueue.offer(current);
			} catch (InterruptedException error) {
				IMMainWizardNewsController.logger.error(null, error);
			}
		}
		this.htmlField.setValue(current.getContent());
		this.timer.schedule(new UpdateNewsTask(), current.getDisplayTime());
	}

	/**
	 * The Class RequeryNewsTask.
	 */
	class RequeryNewsTask extends TimerTask {

		/*
		 * (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			IMMainWizardNewsController.this.queryNews();
		}

	}

	/**
	 * The Class UpdateNewsTask.
	 */
	class UpdateNewsTask extends TimerTask {

		/**
		 * Instantiates a new update news task.
		 */
		public UpdateNewsTask() {
			super();
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			IMMainWizardNewsController.this.playNextNews();
		}
	}

	/**
	 * The Class News.
	 */
	private class News {

		/** The id. */
		private final Object	id;

		/** The content. */
		private final String	content;

		/** The display time. */
		private final Long		displayTime;

		/**
		 * Instantiates a new news.
		 *
		 * @param id
		 *            the id
		 * @param content
		 *            the content
		 * @param displayTime
		 *            the display time
		 */
		public News(Object id, String content, Long displayTime) {
			super();
			this.id = id;
			this.content = content;
			this.displayTime = displayTime;
		}

		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		public Object getId() {
			return this.id;
		}

		/**
		 * Gets the content.
		 *
		 * @return the content
		 */
		public String getContent() {
			return this.content;
		}

		/**
		 * Gets the display time.
		 *
		 * @return the display time
		 */
		public Long getDisplayTime() {
			return this.displayTime;
		}
	}

	class DocListener implements ChangeListener<Document> {
		@SuppressWarnings("restriction")
		@Override
		public void changed(ObservableValue<? extends Document> observable, Document oldValue, Document newValue) {
			try {
				// Use reflection to retrieve the WebEngine's private 'page' field.
				com.sun.webkit.WebPage page = (com.sun.webkit.WebPage) ReflectionTools.getFieldValue(IMMainWizardNewsController.this.htmlField
						.getWebView().getEngine(), "page");
				page.setBackgroundColor((new java.awt.Color(1.0f, 0.0f, 0.0f, 0.0f)).getRGB());
			} catch (Exception e) {
			}
		}

	}

}
