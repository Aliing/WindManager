/**
 *@filename		AhConfigEvent.java
 *@version
 *@author		Francis
 *@createtime	Nov 8, 2007 8:36:05 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.config.event;

import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.bo.hiveap.HiveAp;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhConfigEvent extends BeBaseEvent {

	private static final long serialVersionUID = 1L;

	protected HiveAp hiveAp;

	public AhConfigEvent() {
		super.setEventType(BeEventConst.AH_CONFIG_EVENT);
	}

	public AhConfigEvent(HiveAp hiveAp) {
		this();
		this.hiveAp = hiveAp;
	}

	public HiveAp getHiveAp() {
		return hiveAp;
	}

	public void setHiveAp(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
	}

}