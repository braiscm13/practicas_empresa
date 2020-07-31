package com.opentach.common.interfaces;

import com.ontimize.db.FileManagementEntity;

public interface AdvancedFileManagementEntity extends FileManagementEntity {

	public Object getReceiveRecordKey(String receivingId, int sessionID) throws Exception;

	public Object finishReceivingReturnKey(String rId, int sessionID) throws Exception;

}
