/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2013 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Contributors:
 * Ryan Johnson - delscovich@users.sourceforge.net
 * Carlton Moore - cmoore79@users.sourceforge.net
 *  Petr Michalek - pmichalek@users.sourceforge.net
 */
package net.sf.jasperreports.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.ImageMapRenderable;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintAnchorIndex;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintImageAreaHyperlink;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.Renderable;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporterParameter;
import net.sf.jasperreports.engine.print.JRPrinterAWT;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import net.sf.jasperreports.engine.util.SimpleFileResolver;
import net.sf.jasperreports.engine.xml.JRPrintXmlLoader;
import net.sf.jasperreports.view.save.JRPrintSaveContributor;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JRViewer.java 5880 2013-01-07 20:40:06Z teodord $
 */
public class JRViewer extends javax.swing.JPanel implements JRHyperlinkListener
{
	private static final Log log = LogFactory.getLog(JRViewer.class);

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * Maximum size (in pixels) of a buffered image that would be used by {@link JRViewer JRViewer} to render a report page.
	 * <p>
	 * If rendering a report page would require an image larger than this threshold
	 * (i.e. image width x image height > maximum size), the report page will be rendered directly on the viewer component.
	 * </p>
	 * <p>
	 * If this property is zero or negative, buffered images will never be user to render a report page.
	 * By default, this property is set to 0.
	 * </p>
	 */
	public static final String VIEWER_RENDER_BUFFER_MAX_SIZE = JRPropertiesUtil.PROPERTY_PREFIX + "viewer.render.buffer.max.size";

	/**
	 *
	 */
	protected static final int TYPE_FILE_NAME = 1;
	protected static final int TYPE_INPUT_STREAM = 2;
	protected static final int TYPE_OBJECT = 3;

	/**
	 * The DPI of the generated report.
	 */
	public static final int REPORT_RESOLUTION = 72;

	protected final float MIN_ZOOM = 0.5f;
	protected final float MAX_ZOOM = 10f;
	protected int zooms[] = {50, 75, 100, 125, 150, 175, 200, 250, 400, 800};
	protected int defaultZoomIndex = 2;

	protected int type = JRViewer.TYPE_FILE_NAME;
	protected boolean isXML;
	protected String reportFileName;
	JasperPrint jasperPrint;
	private int pageIndex;
	private boolean pageError;
	protected float zoom;

	private JRGraphics2DExporter exporter;

	/**
	 * the screen resolution.
	 */
	private int screenResolution = JRViewer.REPORT_RESOLUTION;

	/**
	 * the zoom ration adjusted to the screen resolution.
	 */
	protected float realZoom;

	private final DecimalFormat zoomDecimalFormat = new DecimalFormat("#.##");
	protected JasperReportsContext jasperReportsContext;
	protected LocalJasperReportsContext localJasperReportsContext;
	private ResourceBundle resourceBundle;

	private int downX;
	private int downY;

	private final List<JRHyperlinkListener> hyperlinkListeners = new ArrayList<JRHyperlinkListener>();
	private Map<JPanel,JRPrintHyperlink> linksMap = new HashMap<JPanel,JRPrintHyperlink>();
	private final MouseListener mouseListener =
			new java.awt.event.MouseAdapter()
	{
		@Override
		public void mouseClicked(java.awt.event.MouseEvent evt)
		{
			JRViewer.this.hyperlinkClicked(evt);
		}
	};

	protected KeyListener keyNavigationListener =
			new KeyListener() {
		@Override
		public void keyTyped(KeyEvent evt)
		{
		}
		@Override
		public void keyPressed(KeyEvent evt)
		{
			JRViewer.this.keyNavigate(evt);
		}
		@Override
		public void keyReleased(KeyEvent evt)
		{
		}
	};

	protected List<JRSaveContributor> saveContributors = new ArrayList<JRSaveContributor>();
	protected File lastFolder;
	protected JRSaveContributor lastSaveContributor;

	/**
	 * @see #JRViewer(JasperReportsContext, String, boolean, Locale, ResourceBundle)
	 */
	public JRViewer(String fileName, boolean isXML) throws JRException
	{
		this(fileName, isXML, null);
	}


	/**
	 * @see #JRViewer(JasperReportsContext, InputStream, boolean, Locale, ResourceBundle)
	 */
	public JRViewer(InputStream is, boolean isXML) throws JRException
	{
		this(is, isXML, null);
	}


	/**
	 * @see #JRViewer(JasperReportsContext, JasperPrint, Locale, ResourceBundle)
	 */
	public JRViewer(JasperPrint jrPrint)
	{
		this(jrPrint, null);
	}


	/**
	 * @see #JRViewer(JasperReportsContext, String, boolean, Locale, ResourceBundle)
	 */
	public JRViewer(String fileName, boolean isXML, Locale locale) throws JRException
	{
		this(fileName, isXML, locale, null);
	}


	/**
	 * @see #JRViewer(JasperReportsContext, InputStream, boolean, Locale, ResourceBundle)
	 */
	public JRViewer(InputStream is, boolean isXML, Locale locale) throws JRException
	{
		this(is, isXML, locale, null);
	}


	/**
	 * @see #JRViewer(JasperReportsContext, JasperPrint, Locale, ResourceBundle)
	 */
	public JRViewer(JasperPrint jrPrint, Locale locale)
	{
		this(jrPrint, locale, null);
	}


	/**
	 * @see #JRViewer(JasperReportsContext, String, boolean, Locale, ResourceBundle)
	 */
	public JRViewer(String fileName, boolean isXML, Locale locale, ResourceBundle resBundle) throws JRException
	{
		this(
				DefaultJasperReportsContext.getInstance(),
				fileName,
				isXML,
				locale,
				resBundle
				);
	}


	/**
	 * @see #JRViewer(JasperReportsContext, InputStream, boolean, Locale, ResourceBundle)
	 */
	public JRViewer(InputStream is, boolean isXML, Locale locale, ResourceBundle resBundle) throws JRException
	{
		this(
				DefaultJasperReportsContext.getInstance(),
				is,
				isXML,
				locale,
				resBundle
				);
	}


	/**
	 * @see #JRViewer(JasperReportsContext, JasperPrint, Locale, ResourceBundle)
	 */
	public JRViewer(JasperPrint jrPrint, Locale locale, ResourceBundle resBundle)
	{
		this(
				DefaultJasperReportsContext.getInstance(),
				jrPrint,
				locale,
				resBundle
				);
	}


	/**
	 *
	 */
	public JRViewer(
			JasperReportsContext jasperReportsContext,
			String fileName,
			boolean isXML,
			Locale locale,
			ResourceBundle resBundle
			) throws JRException
	{
		this.jasperReportsContext = jasperReportsContext;

		this.initResources(locale, resBundle);

		this.setScreenDetails();

		this.setZooms();

		this.initComponents();

		this.loadReport(fileName, isXML);

		this.cmbZoom.setSelectedIndex(this.defaultZoomIndex);

		this.initSaveContributors();

		this.addHyperlinkListener(this);
	}


	/**
	 *
	 */
	public JRViewer(
			JasperReportsContext jasperReportsContext,
			InputStream is,
			boolean isXML,
			Locale locale,
			ResourceBundle resBundle
			) throws JRException
	{
		this.jasperReportsContext = jasperReportsContext;

		this.initResources(locale, resBundle);

		this.setScreenDetails();

		this.setZooms();

		this.initComponents();

		this.loadReport(is, isXML);

		this.cmbZoom.setSelectedIndex(this.defaultZoomIndex);

		this.initSaveContributors();

		this.addHyperlinkListener(this);
	}


	/**
	 *
	 */
	public JRViewer(
			JasperReportsContext jasperReportsContext,
			JasperPrint jrPrint,
			Locale locale,
			ResourceBundle resBundle
			)
	{
		this.jasperReportsContext = jasperReportsContext;

		this.initResources(locale, resBundle);

		this.setScreenDetails();

		this.setZooms();

		this.initComponents();

		this.loadReport(jrPrint);

		this.cmbZoom.setSelectedIndex(this.defaultZoomIndex);

		this.initSaveContributors();

		this.addHyperlinkListener(this);
	}


	private void setScreenDetails()
	{
		this.screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
	}


	/**
	 *
	 */
	public void clear()
	{
		this.emptyContainer(this);
		this.jasperPrint = null;
	}


	/**
	 *
	 */
	protected void setZooms()
	{
	}


	/**
	 *
	 */
	public void addSaveContributor(JRSaveContributor contributor)
	{
		this.saveContributors.add(contributor);
	}


	/**
	 *
	 */
	public void removeSaveContributor(JRSaveContributor contributor)
	{
		this.saveContributors.remove(contributor);
	}


	/**
	 *
	 */
	public JRSaveContributor[] getSaveContributors()
	{
		return this.saveContributors.toArray(new JRSaveContributor[this.saveContributors.size()]);
	}


	/**
	 * Replaces the save contributors with the ones provided as parameter.
	 */
	public void setSaveContributors(JRSaveContributor[] saveContribs)
	{
		this.saveContributors = new ArrayList<JRSaveContributor>();
		if (saveContribs != null)
		{
			this.saveContributors.addAll(Arrays.asList(saveContribs));
		}
	}


	/**
	 *
	 */
	public void addHyperlinkListener(JRHyperlinkListener listener)
	{
		this.hyperlinkListeners.add(listener);
	}


	/**
	 *
	 */
	public void removeHyperlinkListener(JRHyperlinkListener listener)
	{
		this.hyperlinkListeners.remove(listener);
	}


	/**
	 *
	 */
	public JRHyperlinkListener[] getHyperlinkListeners()
	{
		return this.hyperlinkListeners.toArray(new JRHyperlinkListener[this.hyperlinkListeners.size()]);
	}


	/**
	 *
	 */
	protected void initResources(Locale locale, ResourceBundle resBundle)
	{
		if (locale != null)
		{
			this.setLocale(locale);
		}
		else
		{
			this.setLocale(Locale.getDefault());
		}
		if (resBundle == null)
		{
			this.resourceBundle = ResourceBundle.getBundle("net/sf/jasperreports/view/viewer", this.getLocale());
		}
		else
		{
			this.resourceBundle = resBundle;
		}
	}


	/**
	 *
	 */
	protected JasperReportsContext getJasperReportsContext()
	{
		return this.jasperReportsContext;
	}


	/**
	 *
	 */
	protected String getBundleString(String key)
	{
		return this.resourceBundle.getString(key);
	}


	/**
	 *
	 */
	protected void initSaveContributors()
	{
		List<JRSaveContributor> builtinContributors = SaveContributorUtils.createBuiltinContributors(
				this.jasperReportsContext, this.getLocale(), this.resourceBundle);
		this.saveContributors.addAll(builtinContributors);
	}


	/**
	 *
	 */
	@Override
	public void gotoHyperlink(JRPrintHyperlink hyperlink)
	{
		switch(hyperlink.getHyperlinkTypeValue())
		{
		case REFERENCE :
		{
			if (this.isOnlyHyperlinkListener())
			{
				System.out.println("Hyperlink reference : " + hyperlink.getHyperlinkReference());
				System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
			}
			break;
		}
		case LOCAL_ANCHOR :
		{
			if (hyperlink.getHyperlinkAnchor() != null)
			{
				Map<String,JRPrintAnchorIndex> anchorIndexes = this.jasperPrint.getAnchorIndexes();
				JRPrintAnchorIndex anchorIndex = anchorIndexes.get(hyperlink.getHyperlinkAnchor());
				if (anchorIndex.getPageIndex() != this.pageIndex)
				{
					this.setPageIndex(anchorIndex.getPageIndex());
					this.refreshPage();
				}
				Container container = this.pnlInScroll.getParent();
				if (container instanceof JViewport)
				{
					JViewport viewport = (JViewport) container;

					int newX = (int)(anchorIndex.getElementAbsoluteX() * this.realZoom);
					int newY = (int)(anchorIndex.getElementAbsoluteY() * this.realZoom);

					int maxX = this.pnlInScroll.getWidth() - viewport.getWidth();
					int maxY = this.pnlInScroll.getHeight() - viewport.getHeight();

					if (newX < 0)
					{
						newX = 0;
					}
					if (newX > maxX)
					{
						newX = maxX;
					}
					if (newY < 0)
					{
						newY = 0;
					}
					if (newY > maxY)
					{
						newY = maxY;
					}

					viewport.setViewPosition(new Point(newX, newY));
				}
			}

			break;
		}
		case LOCAL_PAGE :
		{
			int page = this.pageIndex + 1;
			if (hyperlink.getHyperlinkPage() != null)
			{
				page = hyperlink.getHyperlinkPage().intValue();
			}

			if ((page >= 1) && (page <= this.jasperPrint.getPages().size()) && (page != (this.pageIndex + 1)))
			{
				this.setPageIndex(page - 1);
				this.refreshPage();
				Container container = this.pnlInScroll.getParent();
				if (container instanceof JViewport)
				{
					JViewport viewport = (JViewport) container;
					viewport.setViewPosition(new Point(0, 0));
				}
			}

			break;
		}
		case REMOTE_ANCHOR :
		{
			if (this.isOnlyHyperlinkListener())
			{
				System.out.println("Hyperlink reference : " + hyperlink.getHyperlinkReference());
				System.out.println("Hyperlink anchor    : " + hyperlink.getHyperlinkAnchor());
				System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
			}
			break;
		}
		case REMOTE_PAGE :
		{
			if (this.isOnlyHyperlinkListener())
			{
				System.out.println("Hyperlink reference : " + hyperlink.getHyperlinkReference());
				System.out.println("Hyperlink page      : " + hyperlink.getHyperlinkPage());
				System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
			}
			break;
		}
		case CUSTOM:
		{
			if (this.isOnlyHyperlinkListener())
			{
				System.out.println("Hyperlink of type " + hyperlink.getLinkType());
				System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
			}
			break;
		}
		case NONE :
		default :
		{
			break;
		}
		}
	}


	protected boolean isOnlyHyperlinkListener()
	{
		int listenerCount;
		if (this.hyperlinkListeners == null)
		{
			listenerCount = 0;
		}
		else
		{
			listenerCount = this.hyperlinkListeners.size();
			if (this.hyperlinkListeners.contains(this))
			{
				--listenerCount;
			}
		}
		return listenerCount == 0;
	}


	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		this.tlbToolBar = new javax.swing.JPanel();
		this.btnSave = new javax.swing.JButton();
		this.btnPrint = new javax.swing.JButton();
		this.btnReload = new javax.swing.JButton();
		this.pnlSep01 = new javax.swing.JPanel();
		this.btnFirst = new javax.swing.JButton();
		this.btnPrevious = new javax.swing.JButton();
		this.btnNext = new javax.swing.JButton();
		this.btnLast = new javax.swing.JButton();
		this.txtGoTo = new javax.swing.JTextField();
		this.pnlSep02 = new javax.swing.JPanel();
		this.btnActualSize = new javax.swing.JToggleButton();
		this.btnFitPage = new javax.swing.JToggleButton();
		this.btnFitWidth = new javax.swing.JToggleButton();
		this.pnlSep03 = new javax.swing.JPanel();
		this.btnZoomIn = new javax.swing.JButton();
		this.btnZoomOut = new javax.swing.JButton();
		this.cmbZoom = new javax.swing.JComboBox();
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		for(int i = 0; i < this.zooms.length; i++)
		{
			model.addElement("" + this.zooms[i] + "%");
		}
		this.cmbZoom.setModel(model);

		this.pnlMain = new javax.swing.JPanel();
		this.scrollPane = new javax.swing.JScrollPane();
		this.scrollPane.getHorizontalScrollBar().setUnitIncrement(5);
		this.scrollPane.getVerticalScrollBar().setUnitIncrement(5);

		this.pnlInScroll = new javax.swing.JPanel();
		this.pnlPage = new javax.swing.JPanel();
		this.jPanel4 = new javax.swing.JPanel();
		this.pnlLinks = new javax.swing.JPanel();
		this.jPanel5 = new javax.swing.JPanel();
		this.jPanel6 = new javax.swing.JPanel();
		this.jPanel7 = new javax.swing.JPanel();
		this.jPanel8 = new javax.swing.JPanel();
		this.jLabel1 = new javax.swing.JLabel();
		this.jPanel9 = new javax.swing.JPanel();
		this.lblPage = new PageRenderer(this);
		this.pnlStatus = new javax.swing.JPanel();
		this.lblStatus = new javax.swing.JLabel();

		this.setLayout(new java.awt.BorderLayout());

		this.setMinimumSize(new java.awt.Dimension(450, 150));
		this.setPreferredSize(new java.awt.Dimension(450, 150));
		this.tlbToolBar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 2));

		this.btnSave.setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/save.GIF")));
		this.btnSave.setToolTipText(this.getBundleString("save"));
		this.btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnSave.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnSave.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnSave.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnSave.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.btnSaveActionPerformed(evt);
			}
		});
		this.btnSave.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.btnSave);

		this.btnPrint.setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/print.GIF")));
		this.btnPrint.setToolTipText(this.getBundleString("print"));
		this.btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnPrint.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnPrint.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnPrint.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnPrint.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.btnPrintActionPerformed(evt);
			}
		});
		this.btnPrint.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.btnPrint);

		this.btnReload.setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/reload.GIF")));
		this.btnReload.setToolTipText(this.getBundleString("reload"));
		this.btnReload.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnReload.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnReload.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnReload.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnReload.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.btnReloadActionPerformed(evt);
			}
		});
		this.btnReload.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.btnReload);

		this.pnlSep01.setMaximumSize(new java.awt.Dimension(10, 10));
		this.tlbToolBar.add(this.pnlSep01);

		this.btnFirst.setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/first.GIF")));
		this.btnFirst.setToolTipText(this.getBundleString("first.page"));
		this.btnFirst.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnFirst.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnFirst.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnFirst.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnFirst.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.btnFirstActionPerformed(evt);
			}
		});
		this.btnFirst.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.btnFirst);

		this.btnPrevious.setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/previous.GIF")));
		this.btnPrevious.setToolTipText(this.getBundleString("previous.page"));
		this.btnPrevious.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnPrevious.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnPrevious.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnPrevious.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnPrevious.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.btnPreviousActionPerformed(evt);
			}
		});
		this.btnPrevious.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.btnPrevious);

		this.btnNext.setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/next.GIF")));
		this.btnNext.setToolTipText(this.getBundleString("next.page"));
		this.btnNext.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnNext.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnNext.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnNext.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnNext.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.btnNextActionPerformed(evt);
			}
		});
		this.btnNext.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.btnNext);

		this.btnLast.setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/last.GIF")));
		this.btnLast.setToolTipText(this.getBundleString("last.page"));
		this.btnLast.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnLast.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnLast.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnLast.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnLast.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.btnLastActionPerformed(evt);
			}
		});
		this.btnLast.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.btnLast);

		this.txtGoTo.setToolTipText(this.getBundleString("go.to.page"));
		this.txtGoTo.setMaximumSize(new java.awt.Dimension(40, 23));
		this.txtGoTo.setMinimumSize(new java.awt.Dimension(40, 23));
		this.txtGoTo.setPreferredSize(new java.awt.Dimension(40, 23));
		this.txtGoTo.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.txtGoToActionPerformed(evt);
			}
		});
		this.txtGoTo.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.txtGoTo);

		this.pnlSep02.setMaximumSize(new java.awt.Dimension(10, 10));
		this.tlbToolBar.add(this.pnlSep02);

		this.btnActualSize.setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/actualsize.GIF")));
		this.btnActualSize.setToolTipText(this.getBundleString("actual.size"));
		this.btnActualSize.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnActualSize.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnActualSize.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnActualSize.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnActualSize.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.btnActualSizeActionPerformed(evt);
			}
		});
		this.btnActualSize.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.btnActualSize);

		this.btnFitPage.setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/fitpage.GIF")));
		this.btnFitPage.setToolTipText(this.getBundleString("fit.page"));
		this.btnFitPage.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnFitPage.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnFitPage.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnFitPage.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnFitPage.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.btnFitPageActionPerformed(evt);
			}
		});
		this.btnFitPage.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.btnFitPage);

		this.btnFitWidth.setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/fitwidth.GIF")));
		this.btnFitWidth.setToolTipText(this.getBundleString("fit.width"));
		this.btnFitWidth.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnFitWidth.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnFitWidth.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnFitWidth.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnFitWidth.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.btnFitWidthActionPerformed(evt);
			}
		});
		this.btnFitWidth.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.btnFitWidth);

		this.pnlSep03.setMaximumSize(new java.awt.Dimension(10, 10));
		this.tlbToolBar.add(this.pnlSep03);

		this.btnZoomIn.setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/zoomin.GIF")));
		this.btnZoomIn.setToolTipText(this.getBundleString("zoom.in"));
		this.btnZoomIn.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnZoomIn.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnZoomIn.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnZoomIn.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnZoomIn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.btnZoomInActionPerformed(evt);
			}
		});
		this.btnZoomIn.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.btnZoomIn);

		this.btnZoomOut.setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/zoomout.GIF")));
		this.btnZoomOut.setToolTipText(this.getBundleString("zoom.out"));
		this.btnZoomOut.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnZoomOut.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnZoomOut.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnZoomOut.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnZoomOut.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.btnZoomOutActionPerformed(evt);
			}
		});
		this.btnZoomOut.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.btnZoomOut);

		this.cmbZoom.setEditable(true);
		this.cmbZoom.setToolTipText(this.getBundleString("zoom.ratio"));
		this.cmbZoom.setMaximumSize(new java.awt.Dimension(80, 23));
		this.cmbZoom.setMinimumSize(new java.awt.Dimension(80, 23));
		this.cmbZoom.setPreferredSize(new java.awt.Dimension(80, 23));
		this.cmbZoom.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JRViewer.this.cmbZoomActionPerformed(evt);
			}
		});
		this.cmbZoom.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				JRViewer.this.cmbZoomItemStateChanged(evt);
			}
		});
		this.cmbZoom.addKeyListener(this.keyNavigationListener);
		this.tlbToolBar.add(this.cmbZoom);

		this.add(this.tlbToolBar, java.awt.BorderLayout.NORTH);

		this.pnlMain.setLayout(new java.awt.BorderLayout());
		this.pnlMain.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent evt) {
				JRViewer.this.pnlMainComponentResized(evt);
			}
		});

		this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.pnlInScroll.setLayout(new java.awt.GridBagLayout());

		this.pnlPage.setLayout(new java.awt.BorderLayout());
		this.pnlPage.setMinimumSize(new java.awt.Dimension(100, 100));
		this.pnlPage.setPreferredSize(new java.awt.Dimension(100, 100));

		this.jPanel4.setLayout(new java.awt.GridBagLayout());
		this.jPanel4.setMinimumSize(new java.awt.Dimension(100, 120));
		this.jPanel4.setPreferredSize(new java.awt.Dimension(100, 120));

		this.pnlLinks.setLayout(null);
		this.pnlLinks.setMinimumSize(new java.awt.Dimension(5, 5));
		this.pnlLinks.setPreferredSize(new java.awt.Dimension(5, 5));
		this.pnlLinks.setOpaque(false);
		this.pnlLinks.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				JRViewer.this.pnlLinksMousePressed(evt);
			}
			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				JRViewer.this.pnlLinksMouseReleased(evt);
			}
		});
		this.pnlLinks.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseDragged(java.awt.event.MouseEvent evt) {
				JRViewer.this.pnlLinksMouseDragged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		this.jPanel4.add(this.pnlLinks, gridBagConstraints);

		this.jPanel5.setBackground(java.awt.Color.gray);
		this.jPanel5.setMinimumSize(new java.awt.Dimension(5, 5));
		this.jPanel5.setPreferredSize(new java.awt.Dimension(5, 5));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
		this.jPanel4.add(this.jPanel5, gridBagConstraints);

		this.jPanel6.setMinimumSize(new java.awt.Dimension(5, 5));
		this.jPanel6.setPreferredSize(new java.awt.Dimension(5, 5));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		this.jPanel4.add(this.jPanel6, gridBagConstraints);

		this.jPanel7.setBackground(java.awt.Color.gray);
		this.jPanel7.setMinimumSize(new java.awt.Dimension(5, 5));
		this.jPanel7.setPreferredSize(new java.awt.Dimension(5, 5));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		this.jPanel4.add(this.jPanel7, gridBagConstraints);

		this.jPanel8.setBackground(java.awt.Color.gray);
		this.jPanel8.setMinimumSize(new java.awt.Dimension(5, 5));
		this.jPanel8.setPreferredSize(new java.awt.Dimension(5, 5));
		this.jLabel1.setText("jLabel1");
		this.jPanel8.add(this.jLabel1);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		this.jPanel4.add(this.jPanel8, gridBagConstraints);

		this.jPanel9.setMinimumSize(new java.awt.Dimension(5, 5));
		this.jPanel9.setPreferredSize(new java.awt.Dimension(5, 5));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		this.jPanel4.add(this.jPanel9, gridBagConstraints);

		this.lblPage.setBackground(java.awt.Color.white);
		this.lblPage.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
		this.lblPage.setOpaque(true);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		this.jPanel4.add(this.lblPage, gridBagConstraints);

		this.pnlPage.add(this.jPanel4, java.awt.BorderLayout.CENTER);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		this.pnlInScroll.add(this.pnlPage, gridBagConstraints);

		this.scrollPane.setViewportView(this.pnlInScroll);
		this.pnlMain.add(this.scrollPane, java.awt.BorderLayout.CENTER);
		this.add(this.pnlMain, java.awt.BorderLayout.CENTER);

		this.pnlStatus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

		this.lblStatus.setFont(new java.awt.Font("Dialog", 1, 10));
		this.lblStatus.setText("Page i of n");
		this.pnlStatus.add(this.lblStatus);
		this.add(this.pnlStatus, java.awt.BorderLayout.SOUTH);
		this.addKeyListener(this.keyNavigationListener);
	}
	// </editor-fold>//GEN-END:initComponents

	void txtGoToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGoToActionPerformed
		try
		{
			int pageNumber = Integer.parseInt(this.txtGoTo.getText());
			if (
					(pageNumber != (this.pageIndex + 1))
					&& (pageNumber > 0)
					&& (pageNumber <= this.jasperPrint.getPages().size())
					)
			{
				this.setPageIndex(pageNumber - 1);
				this.refreshPage();
			}
		}
		catch(NumberFormatException e)
		{
		}
	}//GEN-LAST:event_txtGoToActionPerformed

	void cmbZoomItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbZoomItemStateChanged
		// Add your handling code here:
		this.btnActualSize.setSelected(false);
		this.btnFitPage.setSelected(false);
		this.btnFitWidth.setSelected(false);
	}//GEN-LAST:event_cmbZoomItemStateChanged

	void pnlMainComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_pnlMainComponentResized
		// Add your handling code here:
		if (this.btnFitPage.isSelected())
		{
			this.fitPage();
			this.btnFitPage.setSelected(true);
		}
		else if (this.btnFitWidth.isSelected())
		{
			this.setRealZoomRatio(((float)this.pnlInScroll.getVisibleRect().getWidth() - 20f) / this.jasperPrint.getPageWidth());
			this.btnFitWidth.setSelected(true);
		}

	}//GEN-LAST:event_pnlMainComponentResized

	void btnActualSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualSizeActionPerformed
		// Add your handling code here:
		if (this.btnActualSize.isSelected())
		{
			this.btnFitPage.setSelected(false);
			this.btnFitWidth.setSelected(false);
			this.cmbZoom.setSelectedIndex(-1);
			this.setZoomRatio(1);
			this.btnActualSize.setSelected(true);
		}
	}//GEN-LAST:event_btnActualSizeActionPerformed

	void btnFitWidthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFitWidthActionPerformed
		// Add your handling code here:
		if (this.btnFitWidth.isSelected())
		{
			this.btnActualSize.setSelected(false);
			this.btnFitPage.setSelected(false);
			this.cmbZoom.setSelectedIndex(-1);
			this.setRealZoomRatio(((float)this.pnlInScroll.getVisibleRect().getWidth() - 20f) / this.jasperPrint.getPageWidth());
			this.btnFitWidth.setSelected(true);
		}
	}//GEN-LAST:event_btnFitWidthActionPerformed

	void btnFitPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFitPageActionPerformed
		// Add your handling code here:
		if (this.btnFitPage.isSelected())
		{
			this.btnActualSize.setSelected(false);
			this.btnFitWidth.setSelected(false);
			this.cmbZoom.setSelectedIndex(-1);
			this.fitPage();
			this.btnFitPage.setSelected(true);
		}
	}//GEN-LAST:event_btnFitPageActionPerformed

	void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
		// Add your handling code here:

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setLocale(this.getLocale());
		fileChooser.updateUI();
		for(int i = 0; i < this.saveContributors.size(); i++)
		{
			fileChooser.addChoosableFileFilter(this.saveContributors.get(i));
		}

		if (this.saveContributors.contains(this.lastSaveContributor))
		{
			fileChooser.setFileFilter(this.lastSaveContributor);
		}
		else if (this.saveContributors.size() > 0)
		{
			int i=0;
			for (i=0;i<this.saveContributors.size();i++){
				Object o = this.saveContributors.get(i);
				if (o instanceof net.sf.jasperreports.view.save.JRPdfSaveContributor){
					fileChooser.setFileFilter(this.saveContributors.get(i));
					break;
				}
			}
			if (i==this.saveContributors.size()) {
				fileChooser.setFileFilter(this.saveContributors.get(0));
			}
		}

		if (this.lastFolder != null)
		{
			fileChooser.setCurrentDirectory(this.lastFolder);
		}

		int retValue = fileChooser.showSaveDialog(this);
		if (retValue == JFileChooser.APPROVE_OPTION)
		{
			FileFilter fileFilter = fileChooser.getFileFilter();
			File file = fileChooser.getSelectedFile();

			this.lastFolder = file.getParentFile();

			JRSaveContributor contributor = null;

			if (fileFilter instanceof JRSaveContributor)
			{
				contributor = (JRSaveContributor)fileFilter;
			}
			else
			{
				int i = 0;
				while((contributor == null) && (i < this.saveContributors.size()))
				{
					contributor = this.saveContributors.get(i++);
					if (!contributor.accept(file))
					{
						contributor = null;
					}
				}

				if (contributor == null)
				{
					contributor = new JRPrintSaveContributor(this.jasperReportsContext, this.getLocale(), this.resourceBundle);
				}
			}

			this.lastSaveContributor = contributor;

			try
			{
				contributor.save(this.jasperPrint, file);
			}
			catch (JRException e)
			{
				if (JRViewer.log.isErrorEnabled())
				{
					JRViewer.log.error("Save error.", e);
				}
				JOptionPane.showMessageDialog(this, this.getBundleString("error.saving"));
			}
		}
	}//GEN-LAST:event_btnSaveActionPerformed

	void pnlLinksMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLinksMouseDragged
		// Add your handling code here:

		Container container = this.pnlInScroll.getParent();
		if (container instanceof JViewport)
		{
			JViewport viewport = (JViewport) container;
			Point point = viewport.getViewPosition();
			int newX = point.x - (evt.getX() - this.downX);
			int newY = point.y - (evt.getY() - this.downY);

			int maxX = this.pnlInScroll.getWidth() - viewport.getWidth();
			int maxY = this.pnlInScroll.getHeight() - viewport.getHeight();

			if (newX < 0)
			{
				newX = 0;
			}
			if (newX > maxX)
			{
				newX = maxX;
			}
			if (newY < 0)
			{
				newY = 0;
			}
			if (newY > maxY)
			{
				newY = maxY;
			}

			viewport.setViewPosition(new Point(newX, newY));
		}
	}//GEN-LAST:event_pnlLinksMouseDragged

	void pnlLinksMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLinksMouseReleased
		// Add your handling code here:
		this.pnlLinks.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}//GEN-LAST:event_pnlLinksMouseReleased

	void pnlLinksMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLinksMousePressed
		// Add your handling code here:
		this.pnlLinks.setCursor(new Cursor(Cursor.MOVE_CURSOR));

		this.downX = evt.getX();
		this.downY = evt.getY();
	}//GEN-LAST:event_pnlLinksMousePressed

	void btnPrintActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPrintActionPerformed
	{//GEN-HEADEREND:event_btnPrintActionPerformed
		// Add your handling code here:

		Thread thread =
				new Thread(
						new Runnable()
						{
							@Override
							public void run()
							{
								try
								{

									JRViewer.this.btnPrint.setEnabled(false);
									JRViewer.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
									JasperPrintManager.getInstance(JRViewer.this.jasperReportsContext).print(JRViewer.this.jasperPrint, true);
								}
								catch (Exception ex)
								{
									if (JRViewer.log.isErrorEnabled())
									{
										JRViewer.log.error("Print error.", ex);
									}
									JOptionPane.showMessageDialog(JRViewer.this, JRViewer.this.getBundleString("error.printing"));
								}
								finally
								{
									JRViewer.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
									JRViewer.this.btnPrint.setEnabled(true);
								}
							}
						}
						);

		thread.start();

	}//GEN-LAST:event_btnPrintActionPerformed

	void btnLastActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnLastActionPerformed
	{//GEN-HEADEREND:event_btnLastActionPerformed
		// Add your handling code here:
		this.setPageIndex(this.jasperPrint.getPages().size() - 1);
		this.refreshPage();
	}//GEN-LAST:event_btnLastActionPerformed

	void btnNextActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnNextActionPerformed
	{//GEN-HEADEREND:event_btnNextActionPerformed
		// Add your handling code here:
		this.setPageIndex(this.pageIndex + 1);
		this.refreshPage();
	}//GEN-LAST:event_btnNextActionPerformed

	void btnPreviousActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPreviousActionPerformed
	{//GEN-HEADEREND:event_btnPreviousActionPerformed
		// Add your handling code here:
		this.setPageIndex(this.pageIndex - 1);
		this.refreshPage();
	}//GEN-LAST:event_btnPreviousActionPerformed

	void btnFirstActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnFirstActionPerformed
	{//GEN-HEADEREND:event_btnFirstActionPerformed
		// Add your handling code here:
		this.setPageIndex(0);
		this.refreshPage();
	}//GEN-LAST:event_btnFirstActionPerformed

	void btnReloadActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnReloadActionPerformed
	{//GEN-HEADEREND:event_btnReloadActionPerformed
		// Add your handling code here:
		if (this.type == JRViewer.TYPE_FILE_NAME)
		{
			try
			{
				this.loadReport(this.reportFileName, this.isXML);
			}
			catch (JRException e)
			{
				if (JRViewer.log.isErrorEnabled())
				{
					JRViewer.log.error("Reload error.", e);
				}
				this.jasperPrint = null;
				this.setPageIndex(0);
				this.refreshPage();

				JOptionPane.showMessageDialog(this, this.getBundleString("error.loading"));
			}

			this.forceRefresh();
		}
	}//GEN-LAST:event_btnReloadActionPerformed

	protected void forceRefresh()
	{
		this.zoom = 0;//force pageRefresh()
		this.realZoom = 0f;
		this.setZoomRatio(1);
	}

	void btnZoomInActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnZoomInActionPerformed
	{//GEN-HEADEREND:event_btnZoomInActionPerformed
		// Add your handling code here:
		this.btnActualSize.setSelected(false);
		this.btnFitPage.setSelected(false);
		this.btnFitWidth.setSelected(false);

		int newZoomInt = (int)(100 * this.getZoomRatio());
		int index = Arrays.binarySearch(this.zooms, newZoomInt);
		if (index < 0)
		{
			this.setZoomRatio(this.zooms[- index - 1] / 100f);
		}
		else if (index < (this.cmbZoom.getModel().getSize() - 1))
		{
			this.setZoomRatio(this.zooms[index + 1] / 100f);
		}
	}//GEN-LAST:event_btnZoomInActionPerformed

	void btnZoomOutActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnZoomOutActionPerformed
	{//GEN-HEADEREND:event_btnZoomOutActionPerformed
		// Add your handling code here:
		this.btnActualSize.setSelected(false);
		this.btnFitPage.setSelected(false);
		this.btnFitWidth.setSelected(false);

		int newZoomInt = (int)(100 * this.getZoomRatio());
		int index = Arrays.binarySearch(this.zooms, newZoomInt);
		if (index > 0)
		{
			this.setZoomRatio(this.zooms[index - 1] / 100f);
		}
		else if (index < -1)
		{
			this.setZoomRatio(this.zooms[- index - 2] / 100f);
		}
	}//GEN-LAST:event_btnZoomOutActionPerformed

	void cmbZoomActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cmbZoomActionPerformed
	{//GEN-HEADEREND:event_cmbZoomActionPerformed
		// Add your handling code here:
		float newZoom = this.getZoomRatio();

		if (newZoom < this.MIN_ZOOM)
		{
			newZoom = this.MIN_ZOOM;
		}

		if (newZoom > this.MAX_ZOOM)
		{
			newZoom = this.MAX_ZOOM;
		}

		this.setZoomRatio(newZoom);
	}//GEN-LAST:event_cmbZoomActionPerformed


	/**
	 */
	void hyperlinkClicked(MouseEvent evt)
	{
		JPanel link = (JPanel)evt.getSource();
		JRPrintHyperlink element = this.linksMap.get(link);
		this.hyperlinkClicked(element);
	}


	protected void hyperlinkClicked(JRPrintHyperlink hyperlink)
	{
		try
		{
			JRHyperlinkListener listener = null;
			for(int i = 0; i < this.hyperlinkListeners.size(); i++)
			{
				listener = this.hyperlinkListeners.get(i);
				listener.gotoHyperlink(hyperlink);
			}
		}
		catch(JRException e)
		{
			if (JRViewer.log.isErrorEnabled())
			{
				JRViewer.log.error("Hyperlink click error.", e);
			}
			JOptionPane.showMessageDialog(this, this.getBundleString("error.hyperlink"));
		}
	}


	/**
	 */
	public int getPageIndex()
	{
		return this.pageIndex;
	}


	/**
	 */
	private void setPageIndex(int index)
	{
		if (
				(this.jasperPrint != null) &&
				(this.jasperPrint.getPages() != null) &&
				(this.jasperPrint.getPages().size() > 0)
				)
		{
			if ((index >= 0) && (index < this.jasperPrint.getPages().size()))
			{
				this.pageIndex = index;
				this.pageError = false;
				this.btnFirst.setEnabled( (this.pageIndex > 0) );
				this.btnPrevious.setEnabled( (this.pageIndex > 0) );
				this.btnNext.setEnabled( (this.pageIndex < (this.jasperPrint.getPages().size() - 1)) );
				this.btnLast.setEnabled( (this.pageIndex < (this.jasperPrint.getPages().size() - 1)) );
				this.txtGoTo.setEnabled(this.btnFirst.isEnabled() || this.btnLast.isEnabled());
				this.txtGoTo.setText("" + (this.pageIndex + 1));
				this.lblStatus.setText(
						MessageFormat.format(
								this.getBundleString("page"),
								new Object[]{Integer.valueOf(this.pageIndex + 1), Integer.valueOf(this.jasperPrint.getPages().size())}
								)
						);
			}
		}
		else
		{
			this.btnFirst.setEnabled(false);
			this.btnPrevious.setEnabled(false);
			this.btnNext.setEnabled(false);
			this.btnLast.setEnabled(false);
			this.txtGoTo.setEnabled(false);
			this.txtGoTo.setText("");
			this.lblStatus.setText("");
		}
	}


	/**
	 */
	protected void loadReport(String fileName, boolean isXmlReport) throws JRException
	{
		if (isXmlReport)
		{
			this.jasperPrint = JRPrintXmlLoader.loadFromFile(this.jasperReportsContext, fileName);
		}
		else
		{
			this.jasperPrint = (JasperPrint)JRLoader.loadObjectFromFile(fileName);
		}

		this.type = JRViewer.TYPE_FILE_NAME;
		this.isXML = isXmlReport;
		this.reportFileName = fileName;

		SimpleFileResolver fileResolver = new SimpleFileResolver(Arrays.asList(new File[]{new File(fileName).getParentFile(), new File(".")}));
		fileResolver.setResolveAbsolutePath(true);
		if (this.localJasperReportsContext == null)
		{
			this.localJasperReportsContext = new LocalJasperReportsContext(this.jasperReportsContext);
			this.jasperReportsContext = this.localJasperReportsContext;
		}
		this.localJasperReportsContext.setFileResolver(fileResolver);

		this.btnReload.setEnabled(true);
		this.setPageIndex(0);
	}


	/**
	 */
	protected void loadReport(InputStream is, boolean isXmlReport) throws JRException
	{
		if (isXmlReport)
		{
			this.jasperPrint = JRPrintXmlLoader.load(this.jasperReportsContext, is);
		}
		else
		{
			this.jasperPrint = (JasperPrint)JRLoader.loadObject(is);
		}

		this.type = JRViewer.TYPE_INPUT_STREAM;
		this.isXML = isXmlReport;
		this.btnReload.setEnabled(false);
		this.setPageIndex(0);
	}


	/**
	 */
	protected void loadReport(JasperPrint jrPrint)
	{
		this.jasperPrint = jrPrint;
		this.type = JRViewer.TYPE_OBJECT;
		this.isXML = false;
		this.btnReload.setEnabled(false);
		this.setPageIndex(0);
	}

	/**
	 */
	protected void refreshPage()
	{
		if (
				(this.jasperPrint == null) ||
				(this.jasperPrint.getPages() == null) ||
				(this.jasperPrint.getPages().size() == 0)
				)
		{
			this.pnlPage.setVisible(false);
			this.btnSave.setEnabled(false);
			this.btnPrint.setEnabled(false);
			this.btnActualSize.setEnabled(false);
			this.btnFitPage.setEnabled(false);
			this.btnFitWidth.setEnabled(false);
			this.btnZoomIn.setEnabled(false);
			this.btnZoomOut.setEnabled(false);
			this.cmbZoom.setEnabled(false);

			if (this.jasperPrint != null)
			{
				JOptionPane.showMessageDialog(this, this.getBundleString("no.pages"));
			}

			return;
		}

		this.pnlPage.setVisible(true);
		this.btnSave.setEnabled(true);
		this.btnPrint.setEnabled(true);
		this.btnActualSize.setEnabled(true);
		this.btnFitPage.setEnabled(true);
		this.btnFitWidth.setEnabled(true);
		this.btnZoomIn.setEnabled(this.zoom < this.MAX_ZOOM);
		this.btnZoomOut.setEnabled(this.zoom > this.MIN_ZOOM);
		this.cmbZoom.setEnabled(true);

		Dimension dim = new Dimension(
				(int)(this.jasperPrint.getPageWidth() * this.realZoom) + 8, // 2 from border, 5 from shadow and 1 extra pixel for image
				(int)(this.jasperPrint.getPageHeight() * this.realZoom) + 8
				);
		this.pnlPage.setMaximumSize(dim);
		this.pnlPage.setMinimumSize(dim);
		this.pnlPage.setPreferredSize(dim);

		long maxImageSize = JRPropertiesUtil.getInstance(this.jasperReportsContext).getLongProperty(JRViewer.VIEWER_RENDER_BUFFER_MAX_SIZE);
		boolean renderImage;
		if (maxImageSize <= 0)
		{
			renderImage = false;
		}
		else
		{
			long imageSize = JRPrinterAWT.getImageSize(this.jasperPrint, this.realZoom);
			renderImage = imageSize <= maxImageSize;
		}

		this.lblPage.setRenderImage(renderImage);

		if (renderImage)
		{
			this.setPageImage();
		}

		this.pnlLinks.removeAll();
		this.linksMap = new HashMap<JPanel,JRPrintHyperlink>();

		this.createHyperlinks();

		if (!renderImage)
		{
			this.lblPage.setIcon(null);

			this.pnlMain.validate();
			this.pnlMain.repaint();
		}
	}


	protected void setPageImage()
	{
		Image image;
		if (this.pageError)
		{
			image = this.getPageErrorImage();
		}
		else
		{
			try
			{
				image = JasperPrintManager.getInstance(this.jasperReportsContext).printToImage(this.jasperPrint, this.pageIndex, this.realZoom);
			}
			catch (Exception e)
			{
				if (JRViewer.log.isErrorEnabled())
				{
					JRViewer.log.error("Print page to image error.", e);
				}
				this.pageError = true;

				image = this.getPageErrorImage();
				JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("net/sf/jasperreports/view/viewer").getString("error.displaying"));
			}
		}
		ImageIcon imageIcon = new ImageIcon(image);
		this.lblPage.setIcon(imageIcon);
	}

	protected Image getPageErrorImage()
	{
		Image image = new BufferedImage(
				(int) (this.jasperPrint.getPageWidth() * this.realZoom) + 1,
				(int) (this.jasperPrint.getPageHeight() * this.realZoom) + 1,
				BufferedImage.TYPE_INT_RGB
				);

		Graphics2D grx = (Graphics2D) image.getGraphics();
		AffineTransform transform = new AffineTransform();
		transform.scale(this.realZoom, this.realZoom);
		grx.transform(transform);

		this.drawPageError(grx);

		return image;
	}

	protected void createHyperlinks()
	{
		List<JRPrintPage> pages = this.jasperPrint.getPages();
		JRPrintPage page = pages.get(this.pageIndex);
		this.createHyperlinks(page.getElements(), 0, 0);
	}

	protected void createHyperlinks(List<JRPrintElement> elements, int offsetX, int offsetY)
	{
		if((elements != null) && (elements.size() > 0))
		{
			for(Iterator<JRPrintElement> it = elements.iterator(); it.hasNext();)
			{
				JRPrintElement element = it.next();

				ImageMapRenderable imageMap = null;
				if (element instanceof JRPrintImage)
				{
					Renderable renderer = ((JRPrintImage) element).getRenderable();
					if (renderer instanceof ImageMapRenderable)
					{
						imageMap = (ImageMapRenderable) renderer;
						if (!imageMap.hasImageAreaHyperlinks())
						{
							imageMap = null;
						}
					}
				}
				boolean hasImageMap = imageMap != null;

				JRPrintHyperlink hyperlink = null;
				if (element instanceof JRPrintHyperlink)
				{
					hyperlink = (JRPrintHyperlink) element;
				}
				boolean hasHyperlink = !hasImageMap
						&& (hyperlink != null) && (hyperlink.getHyperlinkTypeValue() != HyperlinkTypeEnum.NONE);
				boolean hasTooltip = (hyperlink != null) && (hyperlink.getHyperlinkTooltip() != null);

				if (hasHyperlink || hasImageMap || hasTooltip)
				{
					JPanel link;
					if (hasImageMap)
					{
						Rectangle renderingArea = new Rectangle(0, 0, element.getWidth(), element.getHeight());
						link = new ImageMapPanel(renderingArea, imageMap);
					}
					else //hasImageMap
					{
						link = new JPanel();
						if (hasHyperlink)
						{
							link.addMouseListener(this.mouseListener);
						}
					}

					if (hasHyperlink)
					{
						link.setCursor(new Cursor(Cursor.HAND_CURSOR));
					}

					link.setLocation(
							(int)((element.getX() + offsetX) * this.realZoom),
							(int)((element.getY() + offsetY) * this.realZoom)
							);
					link.setSize(
							(int)(element.getWidth() * this.realZoom),
							(int)(element.getHeight() * this.realZoom)
							);
					link.setOpaque(false);

					String toolTip = this.getHyperlinkTooltip(hyperlink);
					if ((toolTip == null) && hasImageMap)
					{
						toolTip = "";//not null to register the panel as having a tool tip
					}
					link.setToolTipText(toolTip);

					this.pnlLinks.add(link);
					this.linksMap.put(link, hyperlink);
				}

				if (element instanceof JRPrintFrame)
				{
					JRPrintFrame frame = (JRPrintFrame) element;
					int frameOffsetX = offsetX + frame.getX() + frame.getLineBox().getLeftPadding().intValue();
					int frameOffsetY = offsetY + frame.getY() + frame.getLineBox().getTopPadding().intValue();
					this.createHyperlinks(frame.getElements(), frameOffsetX, frameOffsetY);
				}
			}
		}
	}


	protected class ImageMapPanel extends JPanel implements MouseListener, MouseMotionListener
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

		protected final List<JRPrintImageAreaHyperlink> imageAreaHyperlinks;

		public ImageMapPanel(Rectangle renderingArea, ImageMapRenderable imageMap)
		{
			try
			{
				this.imageAreaHyperlinks = imageMap.getImageAreaHyperlinks(renderingArea);//FIXMECHART
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}

			this.addMouseListener(this);
			this.addMouseMotionListener(this);
		}

		@Override
		public String getToolTipText(MouseEvent event)
		{
			String tooltip = null;
			JRPrintImageAreaHyperlink imageMapArea = this.getImageMapArea(event);
			if (imageMapArea != null)
			{
				tooltip = JRViewer.this.getHyperlinkTooltip(imageMapArea.getHyperlink());
			}

			if (tooltip == null)
			{
				tooltip = super.getToolTipText(event);
			}

			return tooltip;
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			JRViewer.this.pnlLinksMouseDragged(e);
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			JRPrintImageAreaHyperlink imageArea = this.getImageMapArea(e);
			if ((imageArea != null)
					&& (imageArea.getHyperlink().getHyperlinkTypeValue() != HyperlinkTypeEnum.NONE))
			{
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			else
			{
				e.getComponent().setCursor(Cursor.getDefaultCursor());
			}
		}

		protected JRPrintImageAreaHyperlink getImageMapArea(MouseEvent e)
		{
			return this.getImageMapArea((int) (e.getX() / JRViewer.this.realZoom), (int) (e.getY() / JRViewer.this.realZoom));
		}

		protected JRPrintImageAreaHyperlink getImageMapArea(int x, int y)
		{
			JRPrintImageAreaHyperlink image = null;
			if (this.imageAreaHyperlinks != null)
			{
				for (ListIterator<JRPrintImageAreaHyperlink> it = this.imageAreaHyperlinks.listIterator(this.imageAreaHyperlinks.size()); (image == null) && it.hasPrevious();)
				{
					JRPrintImageAreaHyperlink area = it.previous();
					if (area.getArea().containsPoint(x, y))
					{
						image = area;
					}
				}
			}
			return image;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			JRPrintImageAreaHyperlink imageMapArea = this.getImageMapArea(e);
			if (imageMapArea != null)
			{
				JRViewer.this.hyperlinkClicked(imageMapArea.getHyperlink());
			}
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			JRViewer.this.pnlLinksMousePressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			e.getComponent().setCursor(Cursor.getDefaultCursor());
			JRViewer.this.pnlLinksMouseReleased(e);
		}
	}


	protected String getHyperlinkTooltip(JRPrintHyperlink hyperlink)
	{
		String toolTip;
		toolTip = hyperlink.getHyperlinkTooltip();
		if (toolTip == null)
		{
			toolTip = this.getFallbackTooltip(hyperlink);
		}
		return toolTip;
	}


	protected String getFallbackTooltip(JRPrintHyperlink hyperlink)
	{
		String toolTip = null;
		switch(hyperlink.getHyperlinkTypeValue())
		{
		case REFERENCE :
		{
			toolTip = hyperlink.getHyperlinkReference();
			break;
		}
		case LOCAL_ANCHOR :
		{
			if (hyperlink.getHyperlinkAnchor() != null)
			{
				toolTip = "#" + hyperlink.getHyperlinkAnchor();
			}
			break;
		}
		case LOCAL_PAGE :
		{
			if (hyperlink.getHyperlinkPage() != null)
			{
				toolTip = "#page " + hyperlink.getHyperlinkPage();
			}
			break;
		}
		case REMOTE_ANCHOR :
		{
			toolTip = "";
			if (hyperlink.getHyperlinkReference() != null)
			{
				toolTip = toolTip + hyperlink.getHyperlinkReference();
			}
			if (hyperlink.getHyperlinkAnchor() != null)
			{
				toolTip = toolTip + "#" + hyperlink.getHyperlinkAnchor();
			}
			break;
		}
		case REMOTE_PAGE :
		{
			toolTip = "";
			if (hyperlink.getHyperlinkReference() != null)
			{
				toolTip = toolTip + hyperlink.getHyperlinkReference();
			}
			if (hyperlink.getHyperlinkPage() != null)
			{
				toolTip = toolTip + "#page " + hyperlink.getHyperlinkPage();
			}
			break;
		}
		default :
		{
			break;
		}
		}
		return toolTip;
	}


	/**
	 */
	private void emptyContainer(Container container)
	{
		Component[] components = container.getComponents();

		if (components != null)
		{
			for(int i = 0; i < components.length; i++)
			{
				if (components[i] instanceof Container)
				{
					this.emptyContainer((Container)components[i]);
				}
			}
		}

		components = null;
		container.removeAll();
		container = null;
	}


	/**
	 */
	private float getZoomRatio()
	{
		float newZoom = this.zoom;

		try
		{
			newZoom =
					this.zoomDecimalFormat.parse(
							String.valueOf(this.cmbZoom.getEditor().getItem())
							).floatValue() / 100f;
		}
		catch(ParseException e)
		{
		}

		return newZoom;
	}


	/**
	 */
	public void setZoomRatio(float newZoom)
	{
		if (newZoom > 0)
		{
			this.cmbZoom.getEditor().setItem(
					this.zoomDecimalFormat.format(newZoom * 100) + "%"
					);

			if (this.zoom != newZoom)
			{
				this.zoom = newZoom;
				this.realZoom = (this.zoom * this.screenResolution) / JRViewer.REPORT_RESOLUTION;

				this.refreshPage();
			}
		}
	}


	/**
	 */
	private void setRealZoomRatio(float newZoom)
	{
		if ((newZoom > 0) && (this.realZoom != newZoom))
		{
			this.zoom = (newZoom * JRViewer.REPORT_RESOLUTION) / this.screenResolution;
			this.realZoom = newZoom;

			this.cmbZoom.getEditor().setItem(
					this.zoomDecimalFormat.format(this.zoom * 100) + "%"
					);

			this.refreshPage();
		}
	}


	/**
	 *
	 */
	public void setFitWidthZoomRatio()
	{
		this.setRealZoomRatio(((float)this.pnlInScroll.getVisibleRect().getWidth() - 20f) / this.jasperPrint.getPageWidth());

	}

	public void setFitPageZoomRatio()
	{
		this.setRealZoomRatio(((float)this.pnlInScroll.getVisibleRect().getHeight() - 20f) / this.jasperPrint.getPageHeight());
	}


	/**
	 *
	 */
	protected JRGraphics2DExporter getGraphics2DExporter() throws JRException
	{
		return new JRGraphics2DExporter(this.jasperReportsContext);
	}

	/**
	 *
	 */
	protected void paintPage(Graphics2D grx)
	{
		if (this.pageError)
		{
			this.paintPageError(grx);
			return;
		}

		try
		{
			if (this.exporter == null)
			{
				this.exporter = this.getGraphics2DExporter();
			}
			else
			{
				this.exporter.reset();
			}

			this.exporter.setParameter(JRExporterParameter.JASPER_PRINT, this.jasperPrint);
			this.exporter.setParameter(JRGraphics2DExporterParameter.GRAPHICS_2D, grx.create());
			this.exporter.setParameter(JRExporterParameter.PAGE_INDEX, Integer.valueOf(this.pageIndex));
			this.exporter.setParameter(JRGraphics2DExporterParameter.ZOOM_RATIO, new Float(this.realZoom));
			this.exporter.setParameter(JRExporterParameter.OFFSET_X, Integer.valueOf(1)); //lblPage border
			this.exporter.setParameter(JRExporterParameter.OFFSET_Y, Integer.valueOf(1));
			this.exporter.exportReport();
		}
		catch(Exception e)
		{
			if (JRViewer.log.isErrorEnabled())
			{
				JRViewer.log.error("Page paint error.", e);
			}
			this.pageError = true;

			this.paintPageError(grx);
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					JOptionPane.showMessageDialog(JRViewer.this, JRViewer.this.getBundleString("error.displaying"));
				}
			});
		}

	}

	protected void paintPageError(Graphics2D grx)
	{
		AffineTransform origTransform = grx.getTransform();

		AffineTransform transform = new AffineTransform();
		transform.translate(1, 1);
		transform.scale(this.realZoom, this.realZoom);
		grx.transform(transform);

		try
		{
			this.drawPageError(grx);
		}
		finally
		{
			grx.setTransform(origTransform);
		}
	}

	protected void drawPageError(Graphics grx)
	{
		grx.setColor(Color.white);
		grx.fillRect(0, 0, this.jasperPrint.getPageWidth() + 1, this.jasperPrint.getPageHeight() + 1);
	}

	protected void keyNavigate(KeyEvent evt)
	{
		boolean refresh = true;
		switch (evt.getKeyCode())
		{
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_PAGE_DOWN:
			this.dnNavigate(evt);
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_PAGE_UP:
			this.upNavigate(evt);
			break;
		case KeyEvent.VK_HOME:
			this.homeEndNavigate(0);
			break;
		case KeyEvent.VK_END:
			this.homeEndNavigate(this.jasperPrint.getPages().size() - 1);
			break;
		default:
			refresh = false;
		}

		if (refresh)
		{
			this.refreshPage();
		}
	}

	private void dnNavigate(KeyEvent evt)
	{
		int bottomPosition = this.scrollPane.getVerticalScrollBar().getValue();
		this.scrollPane.dispatchEvent(evt);
		if(((this.scrollPane.getViewport().getHeight() > this.pnlPage.getHeight()) ||
				(this.scrollPane.getVerticalScrollBar().getValue() == bottomPosition)) &&
				(this.pageIndex < (this.jasperPrint.getPages().size() - 1)))
		{
			this.setPageIndex(this.pageIndex + 1);
			if(this.scrollPane.isEnabled())
			{
				this.scrollPane.getVerticalScrollBar().setValue(0);
			}
		}
	}

	private void upNavigate(KeyEvent evt)
	{
		if(((this.scrollPane.getViewport().getHeight() > this.pnlPage.getHeight()) ||
				(this.scrollPane.getVerticalScrollBar().getValue() == 0)) &&
				(this.pageIndex > 0))
		{
			this.setPageIndex(this.pageIndex - 1);
			if(this.scrollPane.isEnabled())
			{
				this.scrollPane.getVerticalScrollBar().setValue(this.scrollPane.getVerticalScrollBar().getMaximum());
			}
		}
		else
		{
			this.scrollPane.dispatchEvent(evt);
		}
	}

	private void homeEndNavigate(int pageNumber)
	{
		this.setPageIndex(pageNumber);
		if(this.scrollPane.isEnabled())
		{
			this.scrollPane.getVerticalScrollBar().setValue(0);
		}
	}

	/**
	 *
	 */
	private void fitPage(){
		float heightRatio = ((float)this.pnlInScroll.getVisibleRect().getHeight() - 20f) / this.jasperPrint.getPageHeight();
		float widthRatio = ((float)this.pnlInScroll.getVisibleRect().getWidth() - 20f) / this.jasperPrint.getPageWidth();
		this.setRealZoomRatio(heightRatio < widthRatio ? heightRatio : widthRatio);
	}

	/**
	 */
	class PageRenderer extends JLabel
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

		private boolean renderImage;
		JRViewer viewer = null;

		public PageRenderer(JRViewer viewer)
		{
			this.viewer = viewer;
		}

		@Override
		public void paintComponent(Graphics g)
		{
			if (this.isRenderImage())
			{
				super.paintComponent(g);
			}
			else
			{
				this.viewer.paintPage((Graphics2D)g.create());
			}
		}

		public boolean isRenderImage()
		{
			return this.renderImage;
		}

		public void setRenderImage(boolean renderImage)
		{
			this.renderImage = renderImage;
		}
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	protected javax.swing.JToggleButton btnActualSize;
	protected javax.swing.JButton btnFirst;
	protected javax.swing.JToggleButton btnFitPage;
	protected javax.swing.JToggleButton btnFitWidth;
	protected javax.swing.JButton btnLast;
	protected javax.swing.JButton btnNext;
	protected javax.swing.JButton btnPrevious;
	protected javax.swing.JButton btnPrint;
	protected javax.swing.JButton btnReload;
	protected javax.swing.JButton btnSave;
	protected javax.swing.JButton btnZoomIn;
	protected javax.swing.JButton btnZoomOut;
	protected javax.swing.JComboBox cmbZoom;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel8;
	private javax.swing.JPanel jPanel9;
	private PageRenderer lblPage;
	protected javax.swing.JLabel lblStatus;
	private javax.swing.JPanel pnlInScroll;
	private javax.swing.JPanel pnlLinks;
	private javax.swing.JPanel pnlMain;
	private javax.swing.JPanel pnlPage;
	protected javax.swing.JPanel pnlSep01;
	protected javax.swing.JPanel pnlSep02;
	protected javax.swing.JPanel pnlSep03;
	protected javax.swing.JPanel pnlStatus;
	private javax.swing.JScrollPane scrollPane;
	protected javax.swing.JPanel tlbToolBar;
	protected javax.swing.JTextField txtGoTo;
	// End of variables declaration//GEN-END:variables

}
