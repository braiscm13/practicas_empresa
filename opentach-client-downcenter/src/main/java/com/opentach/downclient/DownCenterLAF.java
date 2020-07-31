package com.opentach.downclient;

import javax.swing.UIDefaults;
import javax.swing.plaf.synth.Region;

import com.imlabs.client.laf.common.UOntimizeLookAndFeel;

public class DownCenterLAF extends UOntimizeLookAndFeel {

	public DownCenterLAF() {
		super();
	}

	@Override
	public UIDefaults getDefaults() {
		if (!this.initialized) {
			UIDefaults defaults = super.getDefaults();
			this.defineCustomColumn("\"BackgroundColumn\"");
			this.defineCustomColumn("\"NewsColumn\"");
			this.defineCustomColumn("\"CenterColumn\"");
			this.defineCustomColumn("\"InnerBorderColumn\"");
			this.defineCustomColumn("\"TechnicalIcon\"");
			this.defineCustomColumn("\"OutBorderColumn\"");
			this.defineCustomColumn("\"MiddleBorderColumn\"");
			this.defineCustomColumn("\"LightBlueHeaderColumn\"");
			this.defineCustomColumn("\"BlueHeaderColumn\"");
			this.defineCustomRow("\"TechnicalRow\"");
			this.defineCustomRow("\"TechnicalRowBright\"");

			this.defineCustomELabel("\"OPEN\"", defaults);
			this.defineCustomELabel("\"TACH\"", defaults);
			this.defineCustomELabel("\"MainRound\"", defaults);
			this.defineCustomELabel("\"MainRoundSmall\"", defaults);
			this.defineCustomELabel("\"SubMainRound\"", defaults);
			this.defineCustomELabel("\"SubMainRound\"", defaults);
			this.defineCustomELabel("\"StandardRound\"", defaults);
			this.defineCustomELabel("\"StandardBoldRound\"", defaults);
			this.defineCustomELabel("\"Subopentach\"", defaults);
			this.defineCustomELabel("\"NewsTitle\"", defaults);
			this.defineCustomELabel("\"TechnicalLabel\"", defaults);
			this.defineCustomELabel("\"TechnicalLabelSmall\"", defaults);
			this.defineCustomELabel("\"TechnicalNumberLabel\"", defaults);
			this.defineCustomELabel("\"WhiteTitle\"", defaults);
			this.defineCustomELabel("\"WhiteStandar\"", defaults);
			this.defineCustomELabel("\"WhiteStandarNumber\"", defaults);
			this.defineCustomELabel("\"WhiteStandarBold\"", defaults);
			this.defineCustomELabel("\"WhiteSmallBold\"", defaults);
			this.defineCustomELabel("\"WhiteTitleBig\"", defaults);
			this.defineCustomELabel("\"RedStandar\"", defaults);

		}
		return super.getDefaults();
	}

	private void defineCustomELabel(String string, UIDefaults defaults) {
		this.defineELabel(string, defaults);
		this.register(Region.LABEL, string);
	}

	// TODO remove when fix in ontimize
	@Override
	protected void defineMonthPlanningComponent(String compName, UIDefaults d) {
		try {
			Class.forName("com.ontimize.planning.component.monthplanning.MonthPlanningConstants");
			super.defineMonthPlanningComponent(compName, d);
		} catch (ClassNotFoundException error) {
			// do nothing
		}
	}
}