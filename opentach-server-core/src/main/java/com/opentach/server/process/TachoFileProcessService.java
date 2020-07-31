package com.opentach.server.process;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.process.FileProcessServiceStatus;
import com.opentach.common.process.ITachoFileProcessService;
import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.services.UAbstractService;

public class TachoFileProcessService extends UAbstractService implements ITachoFileProcessService {

	private static final Logger					logger	= LoggerFactory.getLogger(TachoFileProcessService.class);
	protected final TachoFileThreadPoolExecutor	tachoExecutor;
	protected final IFileProcessTaskPostOperationHelper	processTaskPostOperationHelper;

	public TachoFileProcessService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
		this.tachoExecutor = new TachoFileThreadPoolExecutor((IOpentachServerLocator) this.getLocator());

		this.processTaskPostOperationHelper = ParseUtilsExtended.getClazzInstance((String) hconfig.get("finishprocessimpl"), new Object[] { erl },
				new FileProcessTaskDummyPostOperationHelper());

	}

	public IFileProcessTaskPostOperationHelper getProcessTaskPostOperationHelper() {
		return this.processTaskPostOperationHelper;
	}

	@Override
	public void addPriorityFiles(List<Number> lFileId, int sessionId) throws Exception {
		for (Number idFile : lFileId) {
			this.tachoExecutor.processWithPriority(idFile, TachoFileThreadPoolExecutor.PRIORITY_PROCESS_WITH_PRIORITY);
		}
	}

	@Override
	public boolean isProcessEnabled(int sessionId) throws Exception {
		return this.tachoExecutor.isEnabled();
	}

	@Override
	public void setProcessEnabled(boolean enable, int sessionId) throws Exception {
		this.setProcessEnabled(enable);
	}

	@Override
	public void processNow(Number idFile, int sessionId) throws Exception {
		this.tachoExecutor.processWithPriority(idFile, TachoFileThreadPoolExecutor.PRIORITY_PROCESS_NOW);
	}

	@Override
	public FileProcessServiceStatus getStatus(int sessionId) throws Exception {
		return this.tachoExecutor.getStatus();
	}

	public Future<?> processNowFuture(Number idFile) throws Exception {
		return this.tachoExecutor.processWithPriorityFuture(idFile, TachoFileThreadPoolExecutor.PRIORITY_PROCESS_NOW);
	}

	public void shutdown() throws Exception {
		this.tachoExecutor.shutdownNow();
	}

	public void setProcessEnabled(boolean enable) throws Exception {
		this.tachoExecutor.setEnabled(enable);
	}

}
