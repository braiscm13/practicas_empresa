package com.opentach.model.scard;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.extractor.scard.CardFileInfoDriver;
import com.imatia.tacho.extractor.scard.SmartCardExtractor;
import com.imatia.tacho.extractor.scard.SmartCardExtractorSettings;
import com.imatia.tacho.model.GenerationEnum;
import com.opentach.model.scard.CardEvent.CardEventType;
import com.opentach.model.scard.extractor.DriverCardExtractorTools;
import com.opentach.model.scard.extractor.SmartcardioAPDUConnector;

/**
 * Smart Card Monitor. Allow check that any card is inserted.
 */
public class SmartCardMonitor {

	private static final int	WAIT_TIME	= 1000;
	private static final Logger	logger	= LoggerFactory.getLogger(SmartCardMonitor.class);

	public enum SmartCardType {
		UNKNOW, DRIVER, WORKSHOP, CONTROL, COMPANY
	}

	public static final String		SMARTCARD_REPOSITORY		= "smartcard.repository";
	public static final String		SMARTCARD_AUTODOWNLOAD		= "smartcard.autodownload";
	public static final String		SMARTCARD_NOTIFYONFINISH	= "smartcard.notifyonfinish";

	protected File					repository;
	protected boolean				autoDownload;
	protected SmartCardType			typeCardInserted;
	protected CardTerminal			cardTerminal;
	protected List<CardListener>	smartCardListeners;
	protected byte[]				ic;
	protected byte[]				icc;
	protected Card					currentCard;

	public SmartCardMonitor() {
		super();
		this.autoDownload = true;
		this.smartCardListeners = new ArrayList<CardListener>();
		this.typeCardInserted = SmartCardType.UNKNOW;
		this.register();
	}

	protected void register() {
		try {
			this.addCardListener(new DriverCardAutoDownloadListener(this));
			new Thread("Thread SmartCardMonitor events") {
				@Override
				public void run() {
					try {
						CardTerminals terminals = TerminalFactory.getDefault().terminals();
						List<CardTerminal> terminalList = terminals.list();
						if ((terminalList == null) || terminalList.isEmpty()) {
							throw new Exception("No terminal found");
						}
						if ((terminalList != null) && (terminalList.get(0) != null)) {
							SmartCardMonitor.this.cardTerminal = terminalList.get(0);
						}


						while (true) {
							try {
								while (!SmartCardMonitor.this.cardTerminal.isCardPresent()) {
									try {
										SmartCardMonitor.this.cardTerminal.waitForCardPresent(Long.MAX_VALUE);
									} catch (Exception e) {
									}
								}
								SmartCardMonitor.this.currentCard = SmartCardMonitor.this.connectCard();
								SmartCardMonitor.this.readCardMasterData();
								// card.disconnect(true);
								if (SmartCardMonitor.this.typeCardInserted != SmartCardType.UNKNOW) {
									SmartCardMonitor.this.fireCardInserted(SmartCardMonitor.this.typeCardInserted,
											SmartCardMonitor.this.currentCard.getATR());
								}
								while (SmartCardMonitor.this.cardTerminal.isCardPresent()) {
									try {
										SmartCardMonitor.this.cardTerminal.waitForCardAbsent(Long.MAX_VALUE);
									} catch (Exception e) {
									}
								}
							} catch (Exception e) {
								SmartCardMonitor.logger.error(null, e);
								try {
									Thread.sleep(SmartCardMonitor.WAIT_TIME);
								} catch (InterruptedException e1) {
									// do nothing
								}
							} finally {
								SmartCardMonitor.this.typeCardInserted = SmartCardType.UNKNOW;
								SmartCardMonitor.this.ic = null;
								SmartCardMonitor.this.icc = null;
								SmartCardMonitor.this.currentCard = null;
								SmartCardMonitor.this.fireCardRemoved();
							}
						}
					} catch (Exception e) {
						SmartCardMonitor.logger.error("No smartcard", e);
					}
				}
			}.start();
		} catch (Exception e) {
			this.cardTerminal = null;
			SmartCardMonitor.logger.error("No smartcard", e);
		}
	}

	public CardTerminal getCardTerminal() {
		return this.cardTerminal;
	}

	private Card connectCard() throws CardException {
		// Card card = cardTerminal.connect("T=1");
		// card.disconnect(true);
		return this.cardTerminal.connect("T=1");
	}

	private void readCardMasterData() throws Exception {
		SmartCardExtractorSettings settings = new SmartCardExtractorSettings(null, null, new SmartcardioAPDUConnector(this.currentCard), null);
		SmartCardExtractor extractor = new SmartCardExtractor(settings);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		extractor.extractTCFile(baos, CardFileInfoDriver.TC_EF_IC, GenerationEnum.FIRST);
		this.ic = baos.toByteArray();
		baos = new ByteArrayOutputStream();
		extractor.extractTCFile(baos, CardFileInfoDriver.TC_EF_ICC, GenerationEnum.FIRST);
		this.icc = baos.toByteArray();
		int type = extractor.readCardType().getCardType();
		switch (type) {
			case 1:
				this.typeCardInserted = SmartCardType.DRIVER;
				break;
			case 2:
				this.typeCardInserted = SmartCardType.WORKSHOP;
				break;
			case 3:
				this.typeCardInserted = SmartCardType.CONTROL;
				break;
			case 4:
				this.typeCardInserted = SmartCardType.COMPANY;
				break;
			default:
				this.typeCardInserted = SmartCardType.UNKNOW;
				break;
		}

	}

	protected void fireCardInserted(SmartCardType smartCardType, ATR atr) {
		this.notifyCardStatusChange(new CardEvent(this, CardEventType.CARD_INSERTED, smartCardType, atr, "Tarjeta insertada"));
	}

	protected void fireCardRemoved() {
		this.notifyCardStatusChange(new CardEvent(this, CardEventType.CARD_REMOVED, (SmartCardType) null, (ATR) null, "Tarjeta extraida"));
	}

	public boolean isDriverCardInserted() {
		return this.typeCardInserted == SmartCardType.DRIVER;
	}

	public boolean isCompanyCardInserted() {
		return this.typeCardInserted == SmartCardType.COMPANY;
	}

	public Card getCurrentCard() {
		return this.currentCard;
	}

	public SmartCardType getTypeCardInserted() {
		return this.typeCardInserted;
	}

	public byte[] getIc() {
		return this.ic;
	}

	public byte[] getIcc() {
		return this.icc;
	}

	public File getRepositoryDir() {
		return this.repository;
	}

	public void setRepositoryDir(File dir) throws IllegalArgumentException {
		if ((dir != null) && !dir.isDirectory()) {
			throw new IllegalArgumentException("Repositor argument must be a valid DIRECTORY");
		}
		this.repository = dir;
	}

	public boolean isAutoDownload() {
		return this.autoDownload;
	}

	public void setAutoDownload(boolean enableautodownload) {
		this.autoDownload = enableautodownload;
	}

	public void notifyCardStatusChange(final CardEvent ce) {
		for (CardListener listener : this.smartCardListeners) {
			listener.cardStatusChange(ce);
		}
	}

	public void addCardListener(CardListener ic) {
		this.smartCardListeners.add(ic);
	}

	public void extractDriverCardFiles(File targetFolder) {
		new DriverCardExtractorTools().extractDriverCardFiles(targetFolder, this);
	}

}
