package com.opentach.client.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.ButtonCellEditor;
import com.ontimize.gui.table.EJTable;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.common.smartphonelocation.LocationInfo;
import com.utilmize.client.gui.Column;
import com.utilmize.client.gui.field.UFxHtmlDataField;

public class AddressButtonCellEditor extends ButtonCellEditor implements FormComponent {

	private static final Logger	logger	= LoggerFactory.getLogger(AddressButtonCellEditor.class);
	public int					currentRow;
	public JTable				table;
	protected String			column;

	public AddressButtonCellEditor(Hashtable<String, Object> params) {
		super(ImageManager.getIcon((String) params.get("icon")));
		this.init(params);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.currentRow = row;
		this.table = table;
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	@Override
	public Object getConstraints(LayoutManager arg0) {
		return this.column;
	}

	@Override
	public void init(Hashtable params) {
		this.addActionListener(new ShowMapListener(params));
		this.column = (String) params.get("column");
	}

	@Override
	public Vector<Object> getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(Locale arg0) {}

	@Override
	public void setResourceBundle(ResourceBundle arg0) {}

	// more configuration options = http://mapki.com/wiki/Google_Map_Parameters
	public static class ShowMapListener implements ActionListener {

		public ShowMapListener(Hashtable<String, Object> parameters) {
			super();
		}

		@Override
		public void actionPerformed(final ActionEvent ae) {
			AddressButtonCellEditor editor = (AddressButtonCellEditor) ae.getSource();
			int idxLatitude = ((EJTable) editor.table).getColumnIndex("LATITUDE");
			int idxLongitude = ((EJTable) editor.table).getColumnIndex("LONGITUDE");
			int idxAccuracy = ((EJTable) editor.table).getColumnIndex("ACCURACY");
			Number latitude = (Number) editor.table.getValueAt(editor.currentRow, idxLatitude);
			Number longitude = (Number) editor.table.getValueAt(editor.currentRow, idxLongitude);
			Number accuracy = idxAccuracy >= 0 ? (Number) editor.table.getValueAt(editor.currentRow, idxAccuracy) : null;
			// prepare final address
			if ((latitude == null) || (longitude == null)) {
				((Table) SwingUtilities.getAncestorOfClass(Table.class, editor.table)).getParentForm().message("E_NO_LOCATION_INFO",
						Form.ERROR_MESSAGE);
			} else {
				AddressButtonCellEditor.showMap(editor.table, new LocationInfo(null, null, null, latitude, longitude, accuracy));
			}
		}
	}

	/**
	 * Show map and return thread with capabillities to update info;
	 *
	 * @param parent
	 * @param latitude
	 * @param longitude
	 * @param accuracy
	 * @return
	 */
	// public static Thread showMap(final Component parent, final Number latitude, final Number longitude, final Number accuracy) {
	public static MapThread showMap(final Component parent, LocationInfo locInfo) {
		MapThread thread = new MapThread(parent, locInfo);
		thread.start();
		return thread;
	}

	public static class MapThread extends Thread {
		protected Component			parentComp;
		protected LocationInfo		locInfo;
		protected boolean			loading;

		protected JDialog			dialog;
		protected Column			labelsPanel;
		protected JLabel			label;
		protected JLabel			labelLoading;
		protected UFxHtmlDataField	htmlField;

		public MapThread(Component parentComp, LocationInfo locInfo) {
			super("ShowMap" + System.currentTimeMillis());
			this.parentComp = parentComp;
			this.locInfo = locInfo;

			this.dialog = this.buildWindow();
		}

		@Override
		public void run() {
			this.updateInfo(this.locInfo);
			this.dialog.setVisible(true);
		}

		private JDialog buildWindow() {
			Window windowAncestor = SwingUtilities.getWindowAncestor(this.parentComp);
			JDialog dialog = new JDialog(windowAncestor, ModalityType.MODELESS);
			dialog.setTitle("Localización con Google Maps");
			dialog.setSize(800, 600);

			// Build content ----------
			JLayeredPane layeredPane = new JLayeredPane();
			layeredPane.setLayout(new GridBagLayout());

			// Build upper level panel with details (loc time, accuracy, ...)
			this.labelsPanel = new Column(EntityResultTools.keysvalues("margin", "5;10;5;10"));
			this.labelsPanel.setBackground(Color.decode("#557b92"));
			this.labelsPanel.setOpaque(true);
			this.labelsPanel.setPreferredSize(new Dimension(300, 50));
			layeredPane.add(this.labelsPanel,
					new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

			this.label = new JLabel("");
			this.label.setHorizontalAlignment(SwingConstants.CENTER);
			this.labelsPanel.add(this.label);

			this.labelLoading = new JLabel("Cargando...");
			this.labelLoading.setHorizontalAlignment(SwingConstants.CENTER);
			this.labelsPanel.add(this.labelLoading);

			// Build low level panel with map
			Hashtable<Object, Object> parameters = EntityResultTools.keysvalues("labelvisible", "no", "expand", "yes", "dim", "text");
			this.htmlField = new UFxHtmlDataField(parameters);
			layeredPane.add(this.htmlField,
					new GridBagConstraints(0, 0, 1, 1, 3, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			dialog.getContentPane().add(layeredPane);
			return dialog;
		}

		public void setLoading(boolean loading) {
			this.loading = loading;
			this.updateInfoLabel(this.locInfo);
		}

		public void updateInfo(LocationInfo locInfo) {
			this.locInfo = locInfo;

			// Update labels
			this.updateInfoLabel(locInfo);

			// Reload map
			this.updateInfoMap();
		}

		protected void updateInfoLabel(LocationInfo locInfo) {
			this.labelsPanel.setPreferredSize(new Dimension(300, this.loading ? 50 : 25));

			String infoText = null;
			if ((locInfo == null) || (locInfo.getLatitude() == null) || (locInfo.getLongitude() == null)) {
				infoText = "-";
				this.label.setText("-");
			} else {
				infoText = "";
				infoText += "Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(locInfo.getDate());
				if (locInfo.getAccuracy() != null) {
					infoText += "   Precisión: " + new DecimalFormat("#0.##").format(locInfo.getAccuracy().doubleValue()) + " m";
				}
			}
			this.label.setText(infoText);
			this.label.setToolTipText(infoText);

			// Update loading label
			String loadingText = (this.loading) ? "Consultando localización..." : "";
			this.labelLoading.setText(loadingText);
			this.labelLoading.setToolTipText(loadingText);
		}

		protected void updateInfoMap() {
			this.htmlField.navigateTo(this.composeUrl());
		}

		protected String composeUrl() {
			String url = "https://www.google.es/maps/place";
			if ((this.locInfo != null) || (this.locInfo.getLatitude() != null) || (this.locInfo.getLongitude() != null)) {
				url += "/" + this.convertLatitudeLongitude(this.locInfo.getLatitude(), this.locInfo.getLongitude());
			}
			return url;
		}

		protected String composeGoogleQuery(Number latitude, Number longitude) {
			String query = "";
			query += "f=q";
			query += "&hl=es";
			query += "&geocode=";
			query += "&q=" + this.convertLatitudeLongitude(latitude, longitude);
			query += "&ie=UTF8";
			query += "&ll=" + latitude.doubleValue() + "," + longitude.doubleValue();
			query += "&t=h";
			query += "&z=16";
			query += "&iwloc=addr";
			query += "&output=embed";
			return query;
		}

		protected String convertLatitudeLongitude(Number latitude, Number longitude) {
			String slatitude = this.convertCoordinateToText(latitude, "N", "S");
			String slongitude = this.convertCoordinateToText(longitude, "E", "W");

			// 10C2%B054%2713.66%22+N+19%C2%B056%2706.15%22+E
			// 10º54'13.66" N 19°56'06.15" E
			return slatitude + "+" + slongitude;
		}

		private String convertCoordinateToText(Number latitude, String positive, String negative) {
			int grad = (int) Math.floor(Math.abs(latitude.doubleValue()));
			double tmp = (Math.abs(latitude.doubleValue()) - grad) * 60;
			int min = (int) Math.floor(tmp);
			double seconds = (tmp - min) * 60;
			String s = grad + "%C2%B0" + min + "%27" + seconds + "%22+";
			return s + ((latitude.doubleValue() > 0) ? positive : negative);
		}

	}
}
