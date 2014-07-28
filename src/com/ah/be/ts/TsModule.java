package com.ah.be.ts;

import java.io.Serializable;

import com.ah.be.ts.hiveap.impl.HiveApDebugMgmtImpl;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitor;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorMgmt;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbe;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbeMgmt;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbeNotification;

public interface TsModule extends Serializable {

	HiveApDebugMgmtImpl getHiveApDebugMgmt();

	ClientMonitorMgmt<ClientMonitor, ClientMonitorNotification> getClientMonitorMgmt();

	VlanProbeMgmt<VlanProbe, VlanProbeNotification> getVlanProbeMgmt();

}