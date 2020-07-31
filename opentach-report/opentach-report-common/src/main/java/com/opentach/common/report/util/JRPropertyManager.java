package com.opentach.common.report.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class JRPropertyManager {

	private Map<Integer, JRReportDescriptor>	mReports	= null;
	private Vector<JRReportDescriptor>			lReports	= null;

	public JRPropertyManager(Properties p) {
		this.mReports = new HashMap<Integer, JRReportDescriptor>();
		this.lReports = new Vector<JRReportDescriptor>();
		this.parseProperties(p);
	}

	public JRPropertyManager(String url) {
		this.mReports = new HashMap<Integer, JRReportDescriptor>();
		this.lReports = new Vector<JRReportDescriptor>();
		Properties p = new Properties();
		InputStream is = null;
		try {
			is = this.getClass().getClassLoader().getResourceAsStream(url);
			p.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
		this.parseProperties(p);
	}

	private void parseProperties(Properties p) {
		int i = 1;
		String value = null;
		JRReportDescriptor jrd = null;
		String rList = null;
		StringTokenizer strtok = null;
		do {
			value = p.getProperty("report." + i + ".name");
			if ((value == null) || (value.length() == 0)) {
				break;
			}
			jrd = new JRReportDescriptor();
			jrd.setKey(i);
			jrd.setName(p.getProperty("report." + i + ".name"));
			jrd.setDscr(p.getProperty("report." + i + ".dscr"));
			jrd.setDelegCol(p.getProperty("report." + i + ".delegcol"));
			String mAfter = p.getProperty("report." + i + ".after");
			if ((mAfter != null) && (mAfter.length() > 0)) {
				jrd.setMethodAfter(mAfter);
			}
			String mBefore = p.getProperty("report." + i + ".before");
			if ((mBefore != null) && (mBefore.length() > 0)) {
				jrd.setMethodBefore(mBefore);
			}
			Boolean visible = Boolean.valueOf(p.getProperty("report." + i + ".visible"));
			jrd.setVisible(visible.booleanValue());
			jrd.setUrl(p.getProperty("report." + i + ".url"));
			rList = p.getProperty("report." + i + ".list");
			if ((rList != null) && (rList.length() > 0)) {
				strtok = new StringTokenizer(rList, ";");
				while (strtok.hasMoreTokens()) {
					String token = strtok.nextToken();
					try {
						int index = Integer.parseInt(token);
						if (jrd.getLReports() == null) {
							jrd.setLReports(new ArrayList<JRReportDescriptor>());
						}
						Integer rkey = Integer.valueOf(index);
						JRReportDescriptor jrd2 = this.mReports.get(rkey);
						if (jrd2 != null) {
							jrd.getLReports().add(jrd2);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			this.mReports.put(jrd.getKey(), jrd);
			this.lReports.add(jrd);
			i++;
		} while (true);
	}

	public Map<Integer, JRReportDescriptor> getDataMap() {
		return this.mReports;
	}

	public Vector<JRReportDescriptor> getDataVector() {
		Vector<JRReportDescriptor> v = new Vector<JRReportDescriptor>();
		for (JRReportDescriptor jrd : this.lReports) {
			if (jrd.isVisible()) {
				v.add(jrd);
			}
		}
		return v;
	}
}
