package com.ah.be.config.hiveap;

import com.ah.be.app.HmBeCommunicationUtil;

public class UpdateObject {

	private String[] clis;
	private short updateType;
	private int timeCount;
	private int fileSize;
	// CAPWAP request returned value for result event used.
	private int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
	// CAPWAP request returned value for response used.
	private int serialNum;

	private int maxTimeout = UpdateParameters.DEFAULT_TIMEOUT_MAX;
	// Whether executing this object while previous object executed failed.
	private boolean continued;
	// Whether executing this object using auto provisioning. False by default
	private boolean isAuto;

	private boolean actived = true;// active by default

	private long resultId;

	private short state;

	private short result;

	private float downloadRate;

	private String description;

	private boolean canceling;

	private int cancelSerial;
	
	private int lastfinishedSize;
	
	private short level = UpdateParameters.LEVEL_IMAGE_YES;

	public String[] getClis() {
		return clis;
	}

	public void setClis(String[] clis) {
		this.clis = clis;
	}

	public short getUpdateType() {
		return updateType;
	}

	public void setUpdateType(short updateType) {
		this.updateType = updateType;
	}

	public int getTimeCount() {
		return timeCount;
	}

	public void setTimeCount(int timeCount) {
		this.timeCount = timeCount;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getSequenceNum() {
		return sequenceNum;
	}

	public int getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(int serialNum) {
		this.serialNum = serialNum;
	}

	public int getMaxTimeout() {
		return maxTimeout;
	}

	public void setMaxTimeout(int maxTimeout) {
		this.maxTimeout = maxTimeout;
	}

	/**
	 * This method is used to judge whether to generate this request while the
	 * previous request executed failed.
	 */
	public boolean isContinued() {
		return continued;
	}

	public void setContinued(boolean continued) {
		this.continued = continued;
	}

	public boolean isAuto() {
		return isAuto;
	}

	public void setAuto(boolean isAuto) {
		this.isAuto = isAuto;
	}

	public boolean isActived() {
		return actived;
	}

	public void setActived(boolean actived) {
		this.actived = actived;
	}

	public String getCliString() {
		StringBuilder sb = new StringBuilder();
		if (null != clis) {
			for (String cli : clis) {
				sb.append(cli);
			}
		}
		return sb.toString();
	}

	public long getResultId() {
		return resultId;
	}

	public void setResultId(long resultId) {
		this.resultId = resultId;
	}

	public short getResult() {
		return result;
	}

	public void setResult(short result) {
		this.result = result;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public short getState() {
		return state;
	}

	public void setState(short state) {
		this.state = state;
	}

	public float getDownloadRate() {
		return downloadRate;
	}

	public void setDownloadRate(float downloadRate) {
		this.downloadRate = downloadRate;
	}

	public boolean isCanceling() {
		return canceling;
	}

	public void setCanceling(boolean canceling) {
		this.canceling = canceling;
	}

	public int getCancelSerial() {
		return cancelSerial;
	}

	public void setCancelSerial(int cancelSerial) {
		this.cancelSerial = cancelSerial;
	}

	public int getLastfinishedSize() {
		return lastfinishedSize;
	}

	public void setLastfinishedSize(int lastfinishedSize) {
		this.lastfinishedSize = lastfinishedSize;
	}
	public short getLevel() {
		return level;
	}

	public void setLevel(short level) {
		this.level = level;
	}
}