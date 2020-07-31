package com.opentach.adminclient.modules.surveys;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.cardboard.CardBoard;
import com.utilmize.client.listeners.UForceQueryWithFiltersListener;

public class SearchSurveyListener extends UForceQueryWithFiltersListener {

	@FormComponent(attr = "CARD_BOARD")
	protected CardBoard cardBoard;

	public SearchSurveyListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void launchSearch() {
		// Override EXPRESSION_KEY_UNIQUE_IDENTIFIER
		this.getExpressionKeys();
		this.cardBoard.refresh();
	}

	protected void getExpressionKeys() {
		// Check required fields
		List<String> notCompleted = new ArrayList<>();
		for (String requiredField : this.requiredFilterFields) {
			DataComponent elementReference = (DataComponent) this.getForm().getElementReference(requiredField);
			if ((elementReference != null) && (elementReference.getValue() == null)) {
				notCompleted.add(elementReference.getLabelComponentText());
			}
		}

		if (!this.requiredFilterFields.isEmpty() && !notCompleted.isEmpty()) {
			StringBuffer sb = new StringBuffer(ApplicationManager.getTranslation("W_REQUIRED_FILTER_FIELDS") + ": ");
			for (String attr : notCompleted) {
				sb.append(attr + (notCompleted.indexOf(attr) != (notCompleted.size() - 1) ? "," : ""));
			}
			MessageManager.getMessageManager().showMessage(this.form, sb.toString(), MessageType.WARNING);
			return;
		}

		BasicExpression basicExpression = this.composeFilter();
		this.getForm().setDataFieldValue(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, basicExpression);
	}

	@Override
	protected boolean getEnableValueToSet() {
		return true;
	}
}
