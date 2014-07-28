package com.ah.be.fault;

import com.ah.be.app.AhAppContainer;
import com.ah.be.communication.event.BeTrapEvent;
import com.ah.bo.hiveap.HiveAp;

/*
 * This class is used to generate HiveAP kernel dump trap.
 */
public class HiveAPKernelDumpTrap {
	public static final String description = "System core dump";

	public HiveAPKernelDumpTrap() {
	}

	public static void generateKernelDumpTrap(HiveAp hiveAp) {
		BeTrapEvent trapEvent = new BeTrapEvent();

		trapEvent.setApMac(hiveAp.getMacAddress());
		trapEvent.setApName(hiveAp.getHostName());
		trapEvent.setTrapType(BeTrapEvent.TYPE_KERNEL_DUMP_EVENT);
		trapEvent.setObjectName(BeFaultConst.TRAP_SEND_MAIL_TYPEX[13]);
		trapEvent.setTimeStamp(System.currentTimeMillis());
		trapEvent.setMessageTimeZone(hiveAp.getOwner().getTimeZoneString());
		trapEvent.setProbableCause((byte) BeFaultConst.ALARM_SUBTYPE_FAILURE_KERNELDUMP);
		trapEvent.setDescribe(description);
		trapEvent.setSeverity((byte)BeFaultConst.ALERT_SERVERITY_CRITICAL);

		AhAppContainer.getBeFaultModule().addTrapToQueue(trapEvent);
	}
}
