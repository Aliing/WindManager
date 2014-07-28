package com.ah.be.config.event;

import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.config.result.AhConfigGenerationResult;
import com.ah.be.event.BeEventConst;
import com.ah.bo.hiveap.HiveAp;

public class AhConfigGeneratedEvent extends AhConfigEvent {

	private static final long serialVersionUID = 1L;

	/** Use Mode */
	public enum UseMode {
		UPLOAD, FULL_AUDIT, RADIUS_USER_AUDIT
	}

	/** Config Generation Result Types */
	public enum ConfigGenResultType {
		SUCC, FAIL, DIFF_FAIL, NO_DIFF, AUDIT_EXPIRED, DISCONNECT
	}

	/** Indicates the mode to be used, UPLOAD by default */
	protected UseMode useMode = UseMode.UPLOAD;

	/** Sequence number which is used for uploading config via CAPWAP */
	protected int seqNum;

	/** The specific message while error occurs in generating config */
	protected String errorMsg;

	/** Indicates the type of config to be generated */
	protected ConfigType configType;

	/** The result type of config generation */
	protected ConfigGenResultType configGenResultType;

	/** The result of config generation */
	protected AhConfigGenerationResult configGenResult;

	public AhConfigGeneratedEvent() {
		super.setEventType(BeEventConst.AH_CONFIG_GENERATED_EVENT);
	}

	public AhConfigGeneratedEvent(HiveAp hiveAp) {
		this();
		super.hiveAp = hiveAp;
	}

	public UseMode getUseMode() {
		return useMode;
	}

	public void setUseMode(UseMode useMode) {
		this.useMode = useMode;
	}

	public int getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public ConfigType getConfigType() {
		return configType;
	}

	public void setConfigType(ConfigType configType) {
		this.configType = configType;
	}

	public ConfigGenResultType getConfigGenResultType() {
		return configGenResultType;
	}

	public void setConfigGenResultType(ConfigGenResultType configGenResultType) {
		this.configGenResultType = configGenResultType;
	}

	public AhConfigGenerationResult getConfigGenResult() {
		return configGenResult;
	}

	public void setConfigGenResult(AhConfigGenerationResult configGenResult) {
		this.configGenResult = configGenResult;
	}

}