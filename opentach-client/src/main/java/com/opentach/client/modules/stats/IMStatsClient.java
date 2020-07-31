package com.opentach.client.modules.stats;

import java.util.Calendar;
import java.util.Date;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.TextComboDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.tools.DateTools;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.modules.stats.cards.DummyCard;
import com.opentach.client.modules.stats.cards.FilesUploadsByCompanyCard;
import com.opentach.client.modules.stats.cards.FilesUploadsByTypeCard;
import com.opentach.client.modules.stats.cards.FilesUploadsCard;
import com.opentach.client.modules.stats.cards.FilesUploadsRegistersBySourceCard;
import com.opentach.client.modules.stats.cards.SessionConnectedVsUnconnectedUsersCard;
import com.opentach.client.modules.stats.cards.SessionConnectionsPerDayOfWeekCard;
import com.opentach.client.modules.stats.cards.SessionConnectionsPerHourCard;
import com.opentach.client.modules.stats.cards.SessionMeanTimeCard;
import com.opentach.client.modules.stats.cards.SessionNumConnectionsCard;
import com.opentach.client.modules.stats.cards.SessionUnconnectedUsersCard;
import com.opentach.client.modules.stats.cards.TasksCreatedPerCompany;
import com.opentach.client.modules.stats.cards.TasksEvolutionCard;
import com.opentach.client.modules.stats.cards.UsageToolsBySessionCard;
import com.utilmize.client.fim.advanced.UBasicFIMSearch;
import com.utilmize.client.gui.cardboard.CardBoard;

public class IMStatsClient extends UBasicFIMSearch{

	@FormComponent(attr = "CARD_BOARD")
	protected CardBoard cardBoard;

	@FormComponent(attr = "sta.DATE_FROM")
	protected DateDataField			dateFromField;
	@FormComponent(attr = "sta.DATE_TO")
	protected DateDataField			dateToField;
	@FormComponent(attr = "GROUPING_TIME")
	protected TextComboDataField	groupingField;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		this.cardBoard.addCard("sta.Sessions", new SessionMeanTimeCard((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sta.Sessions", new SessionNumConnectionsCard((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sta.Sessions", new SessionConnectionsPerHourCard((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sta.Sessions", new SessionConnectionsPerDayOfWeekCard((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sta.Sessions", new SessionUnconnectedUsersCard((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sta.Sessions", new SessionConnectedVsUnconnectedUsersCard((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));

		this.cardBoard.addCard("sta.Files", new FilesUploadsByCompanyCard((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sta.Files", new FilesUploadsByTypeCard((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sta.Files", new FilesUploadsCard((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sta.Files", new FilesUploadsRegistersBySourceCard((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));

		this.cardBoard.addCard("sta.Usage", new UsageToolsBySessionCard((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));

		this.cardBoard.addCard("sta.Tasks", new TasksEvolutionCard((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sta.Tasks", new TasksCreatedPerCompany((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sta.Tasks", new DummyCard((OpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));

	}

	@Override
	public void setInitialState() {
		super.setInitialState();

		// Default values
		this.dateFromField.setValue(DateTools.add(new Date(), Calendar.MONTH, -1));
		this.dateToField.setValue(new Date());
		this.groupingField.setValue("WEEK");
	}

}

