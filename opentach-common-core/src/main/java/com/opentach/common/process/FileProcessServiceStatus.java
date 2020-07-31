package com.opentach.common.process;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileProcessServiceStatus implements Serializable {
	private final List<FileProcessServiceSlotStatus>	slotStatus;
	private long										usedSlots;
	private int											totalSlots;
	private long										processedFiles;
	private long										okCount;
	private long										koCount;

	public FileProcessServiceStatus() {
		super();
		this.slotStatus = new ArrayList<FileProcessServiceSlotStatus>();
	}

	public void addSlotStatus(FileProcessServiceSlotStatus slotStatus) {
		this.slotStatus.add(slotStatus);
	}

	public void setUsedSlots(long usedSlots) {
		this.usedSlots = usedSlots;
	}

	public void setTotalSlots(int totalSlots) {
		this.totalSlots = totalSlots;
	}

	public List<FileProcessServiceSlotStatus> getSlotStatus() {
		return this.slotStatus;
	}

	public long getUsedSlots() {
		return this.usedSlots;
	}

	public int getTotalSlots() {
		return this.totalSlots;
	}

	public void setProcessedFiles(long count) {
		this.processedFiles = count;
	}

	public long getProcessedFiles() {
		return this.processedFiles;
	}

	public void setOkCount(long count) {
		this.okCount = count;
	}

	public void setKoCount(long count) {
		this.koCount = count;
	}

	public long getOkCount() {
		return this.okCount;
	}

	public long getKoCount() {
		return this.koCount;
	}

}
