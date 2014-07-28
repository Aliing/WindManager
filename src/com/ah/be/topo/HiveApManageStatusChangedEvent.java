package com.ah.be.topo;

import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.bo.hiveap.HiveAp;

public class HiveApManageStatusChangedEvent extends BeBaseEvent {

	private static final long serialVersionUID = 1L;
	private HiveAp hiveAp;
	private short previousManageStatus;

	public HiveApManageStatusChangedEvent(HiveAp hiveAp,
			short previousManageStatus) {
		super.setEventType(BeEventConst.HIVE_AP_MANAGE_STATUS_CHANGED);
		this.hiveAp = hiveAp;
		this.previousManageStatus = previousManageStatus;
	}

	public HiveAp getHiveAp() {
		return hiveAp;
	}

	public short getPreviousManageStatus() {
		return previousManageStatus;
	}

}
