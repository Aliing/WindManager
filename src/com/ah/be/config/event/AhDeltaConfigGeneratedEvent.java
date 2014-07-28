/**
 *@filename		AhDeltaConfigGeneratedEvent.java
 *@version
 *@author		Francis
 *@createtime	Jan 9, 2008 10:25:23 AM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.config.event;

import com.ah.be.event.BeEventConst;
import com.ah.bo.hiveap.HiveAp;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhDeltaConfigGeneratedEvent extends AhConfigGeneratedEvent {

	private static final long serialVersionUID = 1L;

	/**
	 * Specify the unambiguous time beyond which the delta config generation
	 * will be canceled
	 */
	protected long deadline;

	public AhDeltaConfigGeneratedEvent() {
		super.setEventType(BeEventConst.AH_DELTA_CONFIG_GENERATED_EVENT);
	}

	public AhDeltaConfigGeneratedEvent(HiveAp hiveAp) {
		this();
		super.hiveAp = hiveAp;
	}

	public long getDeadline() {
		return deadline;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	public boolean isExecutionTimeExpired() {
		long currentTime = System.currentTimeMillis();

		return deadline != 0 && currentTime > deadline;
	}

}