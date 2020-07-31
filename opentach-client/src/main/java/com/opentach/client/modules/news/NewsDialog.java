package com.opentach.client.modules.news;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.plaf.utils.StyleUtil;
import com.opentach.client.OpentachClientLocator;
import com.utilmize.client.gui.field.UFxHtmlDataField;
import com.utilmize.tools.exception.UException;

public class NewsDialog {

	private static final Logger logger = LoggerFactory.getLogger(NewsDialog.class);

	protected final JDialog				dialog;
	protected final UFxHtmlDataField	jlContent;
	protected final JLabel				jlTitle;

	public NewsDialog(Window parent) {
		this.dialog = new JDialog(parent);
		this.dialog.setTitle(ApplicationManager.getTranslation("NEWS"));
		this.dialog.setModal(true);
		final JPanel jpMain = new JPanel(new BorderLayout());
		final JPanel jpNorth = new JPanel(new BorderLayout());
		final JPanel jpSouth = new JPanel(new BorderLayout());
		jpMain.setFocusable(true);
		jpNorth.setFocusable(true);
		jpSouth.setFocusable(true);
		this.jlContent = new UFxHtmlDataField(EntityResultTools.keysvalues("dim", "text", "labelvisible", "no"));
		this.jlTitle = new JLabel();
		this.dialog.getContentPane().setLayout(new BorderLayout());

		this.dialog.getContentPane().add(jpNorth, BorderLayout.NORTH);
		this.dialog.getContentPane().add(jpMain, BorderLayout.CENTER);
		this.dialog.getContentPane().add(jpSouth, BorderLayout.SOUTH);

		Color borderColor = new Color(0x515151);
		this.jlTitle.setHorizontalAlignment(SwingConstants.CENTER);
		this.jlTitle.setHorizontalTextPosition(SwingConstants.TRAILING);
		this.jlTitle.setVerticalTextPosition(SwingConstants.CENTER);
		this.jlTitle.setPreferredSize(new Dimension(500, 40));
		this.jlTitle.setFont(new Font(Font.DIALOG, Font.BOLD, 20));

		jpSouth.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor));

		// JButton boton = new JButton("");
		// ImageIcon ima = ImageManager.getIcon("com/opentach/client/rsc/exit24.png");
		// boton.setIcon(ima);
		// boton.setToolTipText(ApplicationManager.getTranslation("exit"));
		// boton.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// NewsDialog.this.setVisible(false);
		// }
		// });
		// jpNorth.add(boton, BorderLayout.EAST);
		jpNorth.add(this.jlTitle, BorderLayout.CENTER);
		jpNorth.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));

		this.jlContent.setOpaque(true);
		this.jlContent.setFont(new Font(Font.DIALOG, Font.PLAIN, 15));

		JScrollPane scroll = new JScrollPane(this.jlContent);
		scroll.setAutoscrolls(false);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		jpMain.setBorder(new LineBorder(borderColor, 5, true));
		jpMain.add(jpNorth, BorderLayout.NORTH);
		jpMain.add(scroll, BorderLayout.CENTER);
		jpMain.add(jpSouth, BorderLayout.SOUTH);
		
		Dimension dim = ApplicationManager.getApplication().getFrame().getSize();
		;
		dialog.setSize((int)(dim.getWidth()*0.8), (int)(dim.getHeight()*0.8));
		
		this.dialog.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				NewsDialog.this.dialog.setVisible(false);
			}
		});
		this.dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				jpMain.requestFocus();
			}
		});
		jpMain.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					NewsDialog.this.dialog.setVisible(false);
				}
			}
		});
	}

	public void setVisible(boolean visible) {
		this.dialog.setLocationRelativeTo(this.dialog.getParent());
		this.dialog.setVisible(visible);
	}

	public void showNews(boolean showIfNoRecords) {
		this.jlTitle.setText(ApplicationManager.getTranslation("NEWS"));

		String html = "";
		EntityResult res = null;
		try {
			res = this.getDataNews(ApplicationManager.getApplication().getLocale().toString());
			html = this.queryText(res);
		} catch (Exception ex) {
			NewsDialog.logger.error(null, ex);
		}

		this.jlContent.setValue(html);
		if (showIfNoRecords) {
			this.setVisible(showIfNoRecords);
		} else if ((res != null) && (res.calculateRecordNumber() > 0)) {
			this.setVisible(true);
		}
	}

	private String queryText(EntityResult resNews) throws Exception {
		StringBuilder sb = new StringBuilder();
		int nRows = resNews.calculateRecordNumber();
		String htmlTemplate = this.getHeadHtml();

		HashMap<String, List<String>> mapNews = new HashMap<String, List<String>>();
		for (int i = 0; i < nRows; i++) {
			Hashtable<String, Object> data = resNews.getRecordValues(i);
			String content = (String) data.get("CONTENT");
			String title = (String) data.get("TITLE");

			if (mapNews.containsKey(title)) {
				List<String> valueTitle = mapNews.get(title);
				valueTitle.add(content);
			} else {
				List<String> l = new ArrayList<String>();
				l.add(content);
				mapNews.put(title, l);
			}
		}

		for (Map.Entry<String, List<String>> entry : mapNews.entrySet()) {
			List<String> contentList = entry.getValue();
			String title = entry.getKey();
			sb.append("<details open>  <summary>");
			sb.append(title).append("</summary>");
			sb.append("\n");
			sb.append("<ul>");
			for (String content : contentList) {
				sb.append("<li>");
				sb.append(content);
				sb.append("</li>");
			}
			sb.append("</ul>");
			sb.append("</details>");
		}

		String colorSummary = StyleUtil.getProperty("Constants", "colorsummarynews", "");
		htmlTemplate = htmlTemplate.replaceAll("%COLOR_SUMMARY%", colorSummary);
		htmlTemplate = htmlTemplate.replaceAll("%DATA%", sb.toString());
		return htmlTemplate;
	}

	protected EntityResult getDataNews(String localeValue) throws Exception, UException {
		EntityResult res = ApplicationManager.getApplication().getReferenceLocator().getEntityReference("ENews").query(
				EntityResultTools.keysvalues("ACTIVE", "S", "LOCALE", localeValue), EntityResultTools.attributes("IDNEW", "TITLE", "ACTIVE", "CONTENT", "LOCALE"),
				ApplicationManager.getApplication().getReferenceLocator().getSessionId());
		CheckingTools.checkValidEntityResult(res, "ERROR_GETTING_NEWS");
		return res;
	}

	private String getHeadHtml() throws IOException {
		URL url = this.getClass().getClassLoader().getResource("com/opentach/client/comp/news.html");
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		String inputLine;
		StringBuilder out = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			out.append(inputLine);
		}
		in.close();
		return out.toString();
	}

	public boolean isVisibleMessage() {
		JCheckBox jc = (JCheckBox) ((JPanel) ((JPanel) this.dialog.getContentPane().getComponent(0)).getComponent(2)).getComponent(0);
		Object[] ob = jc.getSelectedObjects();
		if ((ob != null) && (ob.length > 0)) {
			final OpentachClientLocator bref = (OpentachClientLocator) ApplicationManager.getApplication().getReferenceLocator();
			final String user = bref.getUser();
			try {
				ApplicationManager.getApplication().getPreferences().setPreference(user, "initial_information", "false");
				return false;
			} catch (Exception ex) {
			}
		}
		return true;
	}
}
