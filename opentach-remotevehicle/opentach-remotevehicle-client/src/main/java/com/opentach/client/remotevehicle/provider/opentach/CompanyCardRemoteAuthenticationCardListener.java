package com.opentach.client.remotevehicle.provider.opentach;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.user.Company;
import com.opentach.common.user.IUserData;
import com.opentach.model.scard.CardEvent;
import com.opentach.model.scard.CardEvent.CardEventType;
import com.opentach.model.scard.CardListener;
import com.opentach.model.scard.SmartCardMonitor.SmartCardType;

public class CompanyCardRemoteAuthenticationCardListener implements CardListener {
	private static final Logger	logger	= LoggerFactory.getLogger(CompanyCardRemoteAuthenticationCardListener.class);

	public ClientEndPoint		clientEndPoint;

	public CompanyCardRemoteAuthenticationCardListener() {
		super();
	}

	@Override
	public void cardStatusChange(final CardEvent ce) {
		if ((ce.getType() == CardEventType.CARD_INSERTED) && (ce.getSmartCardType() == SmartCardType.COMPANY)) {
			JOptionPane.showMessageDialog(ApplicationManager.getApplication().getFrame(), ApplicationManager.getTranslation("COMPANY_CARD_INSERTED"));
			try {
				final IUserData userData = ((UserInfoProvider) ApplicationManager.getApplication().getReferenceLocator()).getUserData();
				final List<Company> companies = userData.getCompanies();
				Company sel = null;
				if (companies.size() > 1) {
					sel = (Company) JOptionPane.showInputDialog(ApplicationManager.getApplication().getFrame(),
							ApplicationManager.getTranslation("CHOOSE_COMPANY"), ApplicationManager.getTranslation("COMPANY_CARD_INSERTED"),
							JOptionPane.QUESTION_MESSAGE, null, companies.toArray(), companies.get(0));

				} else if (companies.size() == 1) {
					sel = companies.get(0);
				}
				if (sel == null) {
					return;
				}
				final Company selectedCompany = sel;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							CompanyCardRemoteAuthenticationCardListener.this.unregister();

							CompanyCardRemoteAuthenticationCardListener.this.clientEndPoint = new ClientEndPoint(selectedCompany.getCif());
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									JOptionPane.showMessageDialog(ApplicationManager.getApplication().getFrame(),
											ApplicationManager.getTranslation("COMPANY_CARD_INSERTED_REGISTERED"));
								}
							});
						} catch (Exception e) {
							MessageManager.getMessageManager().showExceptionMessage(e, CompanyCardRemoteAuthenticationCardListener.logger);
						}
					}
				}, "Company card remote thread").start();
			} catch (Throwable t) {
				MessageManager.getMessageManager().showExceptionMessage(t, CompanyCardRemoteAuthenticationCardListener.logger);
			}
		} else if ((ce.getType() == CardEventType.CARD_REMOVED) && (ce.getSmartCardType() == SmartCardType.COMPANY)) {
			this.unregister();
		}
	}

	private void unregister() {
		if (CompanyCardRemoteAuthenticationCardListener.this.clientEndPoint != null) {
			CompanyCardRemoteAuthenticationCardListener.this.clientEndPoint.unregister();
			CompanyCardRemoteAuthenticationCardListener.this.clientEndPoint = null;
		}
	}

}