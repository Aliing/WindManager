package com.ah.be.config.event;

import com.ah.be.event.BeEventConst;
import com.ah.bo.hiveap.HiveAp;

public class AhConfigGenerationProgressEvent extends AhDeltaConfigGeneratedEvent {

	private static final long serialVersionUID = 1L;

	public enum ConfigGenerationProgress {
		FETCH_HIVEAP_CONFIG, GENERATE_HM_CONFIG, COMPARE_CONFIGS
	}

	protected ConfigGenerationProgress configGenProgress;

	public AhConfigGenerationProgressEvent() {
		super.setEventType(BeEventConst.AH_CONFIG_GENERATION_PROGRESS_EVENT);
	}

	public AhConfigGenerationProgressEvent(HiveAp hiveAp) {
		this();
		super.hiveAp = hiveAp;
	}

	public ConfigGenerationProgress getConfigGenProgress() {
		return configGenProgress;
	}

	public void setConfigGenProgress(ConfigGenerationProgress configGenProgress) {
		this.configGenProgress = configGenProgress;
	}

}