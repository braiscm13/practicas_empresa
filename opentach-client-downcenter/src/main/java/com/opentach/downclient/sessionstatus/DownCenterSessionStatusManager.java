package com.opentach.downclient.sessionstatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.sessionstatus.AbstractSessionStatusManager;
import com.opentach.client.util.upload.UploadEvent;
import com.opentach.client.util.upload.UploadEvent.UploadEventType;
import com.opentach.client.util.upload.UploadListener;
import com.opentach.common.sessionstatus.DownCenterStatusDto;
import com.opentach.downclient.DownCenterClientLocator;
import com.utilmize.tools.VersionUtils;

public class DownCenterSessionStatusManager extends AbstractSessionStatusManager<DownCenterStatusDto> implements UploadListener {
	private static final Logger	logger	= LoggerFactory.getLogger(DownCenterSessionStatusManager.class);

	public DownCenterSessionStatusManager(DownCenterClientLocator locator, String user) {
		super(locator, user);
		locator.getUploadMonitor().addUploadListener(this);
	}

	@Override
	public void uploadStatusChange(UploadEvent upe) {
		if ((upe != null) && (upe.getType().equals(UploadEventType.FILE_UPLOAD_END))) {
			this.getStatus().setUploadedFiles(this.getStatus().getUploadedFiles() + 1);
		} else if ((upe != null) && (upe.getType().equals(UploadEventType.FILE_UPLOAD_ERROR))) {
			this.getStatus().setUploadFails(this.getStatus().getUploadFails() + 1);
		}
	}

	@Override
	protected DownCenterStatusDto createStatusDto() {
		DownCenterStatusDto downCenterStatusDto = new DownCenterStatusDto();
		downCenterStatusDto.setVersion(VersionUtils.getVersion(DownCenterSessionStatusManager.class));
		return downCenterStatusDto;
	}

}