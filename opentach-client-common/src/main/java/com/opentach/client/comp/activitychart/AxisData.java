package com.opentach.client.comp.activitychart;

import java.awt.Point;
import java.awt.Stroke;
import java.text.Format;

public class AxisData {
	public Object	minval;
	public Object	maxval;
	public int		numcells;
	public String	title;
	public int		orientation;
	public Stroke	grindstrk;
	public Point	orig;
	public boolean	showgrind;
	public boolean	showlabels;
	public boolean	showtitle;
	public Format	labelsformat;
	public float	labelssize;
	public int		labelpos;

	public AxisData(String title, int orientation, Point orig, float labelssize, int labelpos) {
		this.title = title;
		this.orientation = orientation;
		this.orig = orig;
		this.labelssize = labelssize;
		this.labelpos = labelpos;
	}
}