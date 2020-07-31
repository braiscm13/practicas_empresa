package com.opentach.client.comp.calendar;

import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.util.ParseUtils;
import com.utilmize.client.gui.field.table.UTable;

import net.sf.nachocalendar.components.MonthPanel;

public abstract class CalendarDatesDescriptionRenderer extends JLabel implements IPanelRenderer {

	protected SimpleDateFormat	dateFormat			= null;
	String						dataSourceName		= null;
	UTable						dataSource;
	Form						form;

	Font						font				= null;
	private Color				foreGroundColor		= null;
	private Color				backGroundColorEven	= null;
	private Color				backGroundColorOdd	= null;
	Border						border				= null;

	JLabel						label				= null;
	protected boolean			leavingEmptyRows;

	public CalendarDatesDescriptionRenderer(Hashtable<String, String> params) {
		this.init(params);
	}

	@Override
	public JComponent getRendererComponent(MonthPanel month) {
		this.updateComponent(month);
		return this.label;
	}

	protected abstract String getDataSourceNameKey();

	protected void init(Hashtable<String, String> params) {
		this.createAppearance(params);
		this.createComponent();
		this.dataSourceName = params.get(this.getDataSourceNameKey());

		String format = params.get("format");

		format = "HH:mm:ss dd/MM/yyyy";

		if ((format != null) && (!"".equals(format))) {
			Locale l = this.getLanguage(params.get("language"), params.get("country"));
			this.dateFormat = new SimpleDateFormat(format, l);
		}
	}

	private void createAppearance(Hashtable<String, String> params) {
		this.font = ParseUtils.getFont(params.get("annotationsfont"), new Font("Verdana", Font.PLAIN, 10));
		this.foreGroundColor = ParseUtils.getColor(params.get("annotationsfontcolor"), Color.decode("#FFFFFF"));
		this.backGroundColorEven = ParseUtils.getColor(params.get("annotationsbgcoloreven"), new Color(0x366581));
		this.backGroundColorOdd = ParseUtils.getColor(params.get("annotationsbgcolorodd"), new Color(0x366581));
		this.border = ParseUtils.getBorder(params.get("annotationsborder"),
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0x6BB0FF)), BorderFactory.createEmptyBorder(2, 2, 2, 2)));

	}

	private Locale getLanguage(String language, String country) {
		if ((language == null) || (country == null)) {
			return Locale.getDefault();
		} else {
			return new Locale(language, country);
		}
	}

	private JComponent createComponent() {
		// if (this.label == null) {
		// this.label = new JLabel();
		// // this.add(this.label, new GridBagLayout());
		// }
		this.label = this;
		return this;
	}

	private JComponent updateComponent(MonthPanel month) {
		Calendar c = new GregorianCalendar();
		c.setTime(month.getMonth());
		int monthNumber = c.get(Calendar.MONTH);

		Hashtable<Integer, ArrayList<String>> dat = this.parseData(c, monthNumber, this.getData());
		this.setDataToComponent(dat, monthNumber);

		return this.label;
	}

	protected abstract Hashtable<Integer, ArrayList<String>> parseData(Calendar c, int monthNumber, EntityResult entityResult);

	private void setDataToComponent(Hashtable<Integer, ArrayList<String>> dat, int month) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><p/>");
		if (this.leavingEmptyRows) {
			sb.append("<p/><p/>");
		}

		if (!dat.keySet().isEmpty()) {

			int lag = 0;

			for (int week = 0; week <= 5; week++) {
				ArrayList<String> l = dat.get(week);
				if (l != null) {
					for (int i = 0; i < l.size(); i++) {
						sb.append(l.get(i));
						sb.append("<p>");
					}
					lag += l.size() - 1;
				} else {
					if (lag > 0) {
						lag--;
					} else {
						// now the labels are too long and they use two rows ,
						// so we have to save space
						if (this.leavingEmptyRows) {
							sb.append("<p>");
						}
					}

				}
			}
		}

		sb.append("</html>");

		this.label.setFont(this.font);
		this.label.setForeground(this.foreGroundColor);
		this.label.setText(sb.toString());
		if ((month % 2) == 1) {
			this.label.setBackground(this.backGroundColorEven);
		} else {
			this.label.setBackground(this.backGroundColorOdd);
		}

		this.label.setBorder(this.border);
		this.label.setOpaque(true);
		this.label.setVerticalAlignment(SwingConstants.TOP);

	}

	protected void addAnnotation(Hashtable<Integer, ArrayList<String>> annotations, String annotation, Date date) {
		Integer week = null;
		week = this.getWeekOfMonth(date);
		if ((annotation != null) && (week != null)) {
			ArrayList<String> l = annotations.get(week);
			if (l == null) {
				l = new ArrayList<String>(0);
				if (week == 0) {
					String s = "-------INICIO ------------------    FIN  ----------------------  CONDUCTOR ---------";
					l.add(s);
				} else if ((week == 1) && (annotations.get(0) == null)) {
					String s = "-------INICIO ------------------    FIN  ----------------------  CONDUCTOR ---------";
					l.add(s);
				}
				annotations.put(week, l);
			}
			l.add(annotation);
		}
	}

	protected abstract String formatAnnotation(Date begMeet, String label, String... params);

	private Integer getWeekOfMonth(Date endMeet) {
		Integer week;
		Calendar cal = Calendar.getInstance();
		cal.setTime(endMeet);
		week = cal.get(Calendar.WEEK_OF_MONTH);
		return week;
	}

	protected String formatDate(Date endMeet) {
		String s = this.dateFormat.format(endMeet);
		// return s;
		char[] chars = s.toCharArray();
		chars[3] = Character.toUpperCase(chars[3]);
		return new String(chars);
	}

	protected EntityResult getData() {
		if (this.dataSource == null) {
			if (this.form == null) {
				return new EntityResult();
			}
			this.dataSource = (UTable) this.form.getDataFieldReference(this.dataSourceName);
		}
		try {
			Object value = this.dataSource.getValue();
			if (value instanceof Hashtable<?, ?>) {
				return new EntityResult((Hashtable) value);
			}
			return (EntityResult) value;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void setParentForm(Form form) {
		this.form = form;
	}

}