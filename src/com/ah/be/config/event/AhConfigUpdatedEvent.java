package com.ah.be.config.event;

import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.event.BeEventConst;
import com.ah.bo.hiveap.HiveAp;

public class AhConfigUpdatedEvent extends AhConfigEvent {

	private static final long serialVersionUID = 1L;

	/** Denotes the type of a certain config updated, "AP_FULL" by default */
	private ConfigType configType = ConfigType.AP_FULL;

	/** The qualified result of a certain config updated */
	private byte updateResult = -1;

	public AhConfigUpdatedEvent() {
		super.setEventType(BeEventConst.AH_CONFIG_UPDATED_EVENT);
	}

	public AhConfigUpdatedEvent(HiveAp hiveAp) {
		this();
		super.hiveAp = hiveAp;
	}

	public byte getUpdateResult() {
		return updateResult;
	}

	public void setUpdateResult(byte updateResult) {
		this.updateResult = updateResult;
	}

	public ConfigType getConfigType() {
		return configType;
	}

	public void setConfigType(ConfigType configType) {
		this.configType = configType;
	}

}