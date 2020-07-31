package com.opentach.client.comp.activitychart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.ImageManager;
import com.opentach.client.comp.activitychart.taskwrapper.ActividadesTaskWrapper;

public class LegendPanel extends JPanel {

	private static int			ACTIVITY_X_SQUARE			= 12;
	private static final int	ACTIVITY_SQUARE_HEIGHT		= 12;
	private static final int	ACTIVITY_SQUARE_WIDTH		= 10;
	private static int			ACTIVITY_X_LABEL			= 40;
	private static int			ACTIVITY_RECTANGLE_Y_OFFSET	= 8;
	public static Color			COLOR_FOREGROUND			= new Color(28, 48, 75, 255);
	private static int			X_RECTANGLE					= 10;
	private static int			Y_LABEL_BEGIN				= 20;
	protected String			sublegend1					= null;
	protected String			sublegend2					= null;

	private List<LegendInfo>	squareLegendInfo;

	static class LegendInfo {
		String	text;
		Color	color;
	}

	public LegendPanel(String sub1, String sub2) {
		this.sublegend1 = sub1;
		this.sublegend2 = sub2;
	}

	@Override
	public Dimension getMinimumSize() {
		return this.getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return this.getPreferredSize();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(180, 180);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		int y = LegendPanel.Y_LABEL_BEGIN;

		y = this.paintActivitiesLegend(g, y);
		y += 5;

		y = this.paintOtherLegend(g, y);
		y += 10;

		y = this.paintBeginEnd(g, y);
	}

	private int paintBeginEnd(Graphics g, int y) {
		g.setColor(LegendPanel.COLOR_FOREGROUND);
		g.drawString(ApplicationManager.getTranslation(ApplicationManager.getTranslation("Inicio_Fin_Jornada")), LegendPanel.ACTIVITY_X_LABEL, y);
		g.drawImage(ImageManager.getIcon("com/opentach/client/rsc/chart/inicio1.png").getImage(), 5, y - 10, null);
		g.drawImage(ImageManager.getIcon("com/opentach/client/rsc/chart/fin1.png").getImage(), 20, y - 10, null);
		y += 15;
		return y;
	}

	private int paintOtherLegend(Graphics g, int y) {
		boolean hasSpecialLegend1 = false;
		if (this.sublegend1.contains("FERRY/OUT_TRAIN")) {
			g.setColor(LegendPanel.COLOR_FOREGROUND);
			g.drawString(ApplicationManager.getTranslation("TRANS_TREN"), LegendPanel.ACTIVITY_X_LABEL, y);
			g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_TRANS_TREN);
			g.fillRect(LegendPanel.X_RECTANGLE, y - 5, 15, 5);
			y += 15;
			hasSpecialLegend1 = true;
		}
		if (this.sublegend1.contains("/M")) {
			g.setColor(LegendPanel.COLOR_FOREGROUND);
			g.drawString(ApplicationManager.getTranslation("MANUAL"), LegendPanel.ACTIVITY_X_LABEL, y);
			g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_ORIGIN);
			g.fillRect(LegendPanel.X_RECTANGLE, y - 5, 15, 5);
			y += 15;
			hasSpecialLegend1 = true;
		}
		if (!hasSpecialLegend1) {
			g.setColor(LegendPanel.COLOR_FOREGROUND);
			g.drawString(ApplicationManager.getTranslation(this.sublegend1), LegendPanel.ACTIVITY_X_LABEL, y);
			g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_ORIGIN);
			g.fillRect(LegendPanel.X_RECTANGLE, y - 5, 15, 5);
			y += 15;
		}
		if (this.sublegend2.equals("INFRACCIONES")) {
			g.setColor(LegendPanel.COLOR_FOREGROUND);
			g.drawString(ApplicationManager.getTranslation("FUERA_AMBITO"), LegendPanel.ACTIVITY_X_LABEL, y);
			g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_FUERA_AMBITO);
			g.fillRect(LegendPanel.X_RECTANGLE, y - 5, 15, 5);
		} else {
			g.setColor(LegendPanel.COLOR_FOREGROUND);
			g.drawString(ApplicationManager.getTranslation(this.sublegend2), LegendPanel.ACTIVITY_X_LABEL, y);
			g.setColor(ActividadesTaskWrapper.COLOR_UNDERLINES_REGIMEN);
			g.fillRect(LegendPanel.X_RECTANGLE, y - 5, 15, 5);
		}
		y += 15;
		return y;
	}

	private int paintActivitiesLegend(Graphics g, int y) {

		g.setColor(LegendPanel.COLOR_FOREGROUND);
		g.drawString(ApplicationManager.getTranslation("INDEFINIDA_"), LegendPanel.ACTIVITY_X_LABEL, y);
		g.setColor(ActividadesTaskWrapper.COLOR_ACTIVITY_UNDEFINED);
		g.fillRect(LegendPanel.ACTIVITY_X_SQUARE, y - LegendPanel.ACTIVITY_RECTANGLE_Y_OFFSET, LegendPanel.ACTIVITY_SQUARE_WIDTH,
				LegendPanel.ACTIVITY_SQUARE_HEIGHT);
		y += 15;

		g.setColor(LegendPanel.COLOR_FOREGROUND);
		g.drawString(ApplicationManager.getTranslation("CONDUCCION_"), LegendPanel.ACTIVITY_X_LABEL, y);
		g.setColor(ActividadesTaskWrapper.COLOR_ACTIVITY_DRIVING);
		g.fillRect(LegendPanel.ACTIVITY_X_SQUARE, y - LegendPanel.ACTIVITY_RECTANGLE_Y_OFFSET, LegendPanel.ACTIVITY_SQUARE_WIDTH,
				LegendPanel.ACTIVITY_SQUARE_HEIGHT);
		y += 15;

		g.setColor(LegendPanel.COLOR_FOREGROUND);
		g.drawString(ApplicationManager.getTranslation("PAUSA/DESCANSO_"), LegendPanel.ACTIVITY_X_LABEL, y);
		g.setColor(ActividadesTaskWrapper.COLOR_ACTIVITY_REST);
		g.fillRect(LegendPanel.ACTIVITY_X_SQUARE, y - LegendPanel.ACTIVITY_RECTANGLE_Y_OFFSET, LegendPanel.ACTIVITY_SQUARE_WIDTH,
				LegendPanel.ACTIVITY_SQUARE_HEIGHT);
		y += 15;

		g.setColor(LegendPanel.COLOR_FOREGROUND);
		g.drawString(ApplicationManager.getTranslation("DISPONIBILIDAD_"), LegendPanel.ACTIVITY_X_LABEL, y);
		g.setColor(ActividadesTaskWrapper.COLOR_ACTIVITY_AVAILABLE);
		g.fillRect(LegendPanel.ACTIVITY_X_SQUARE, y - LegendPanel.ACTIVITY_RECTANGLE_Y_OFFSET, LegendPanel.ACTIVITY_SQUARE_WIDTH,
				LegendPanel.ACTIVITY_SQUARE_HEIGHT);
		y += 15;

		g.setColor(LegendPanel.COLOR_FOREGROUND);
		g.drawString(ApplicationManager.getTranslation("TRABAJO_"), LegendPanel.ACTIVITY_X_LABEL, y);
		g.setColor(ActividadesTaskWrapper.COLOR_ACTIVITY_WORK);
		g.fillRect(LegendPanel.ACTIVITY_X_SQUARE, y - LegendPanel.ACTIVITY_RECTANGLE_Y_OFFSET, LegendPanel.ACTIVITY_SQUARE_WIDTH,
				LegendPanel.ACTIVITY_SQUARE_HEIGHT);
		y += 15;

		return y;
	}

}
