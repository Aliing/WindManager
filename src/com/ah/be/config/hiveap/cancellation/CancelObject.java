package com.ah.be.config.hiveap.cancellation;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApUpdateResult;

public class CancelObject {

	public static final short RESULT_TRUE_CANCELED = 1;
	public static final short RESULT_TRUE_CANCELING = 2;
	public static final short RESULT_FAILED_UNSUPPORT_CANCELE = 3;
	public static final short RESULT_FAILED = 4;

	private HiveAp hiveAp;
	private HiveApUpdateResult updateResult;
	private short updateType;
	private short executeResult;

	public HiveAp getHiveAp() {
		return hiveAp;
	}

	public void setHiveAp(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
	}

	public short getExecuteResult() {
		return executeResult;
	}

	public void setExecuteResult(short executeResult) {
		this.executeResult = executeResult;
	}

	public short getUpdateType() {
		return updateType;
	}

	public void setUpdateType(short updateType) {
		this.updateType = updateType;
	}
	
	public HiveApUpdateResult getUpdateResult() {
		return updateResult;
	}

	public void setUpdateResult(HiveApUpdateResult updateResult) {
		this.updateResult = updateResult;
	}
}
