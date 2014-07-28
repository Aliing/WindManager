package com.ah.be.ts;

import com.ah.be.app.BaseModule;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.ts.hiveap.impl.HiveApDebugMgmtImpl;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitor;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorMgmt;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbe;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbeMgmt;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbeNotification;
import com.ah.bo.admin.HmDomain;
import com.ah.events.BoEventFilter;
import com.ah.events.BoEventListener;
import com.ah.events.impl.BoObserver;
import com.ah.util.Tracer;

public class TsModuleImpl extends BaseModule implements TsModule, BoEventListener<HmDomain> {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(TsModuleImpl.class.getSimpleName());

	private HiveApDebugMgmtImpl hiveApDebugMgmt;

	private ClientMonitorMgmt<ClientMonitor, ClientMonitorNotification> clientMonitorMgmt;

	private VlanProbeMgmt<VlanProbe, VlanProbeNotification> vlanProbeMgmt;

	public TsModuleImpl() {
		setModuleId(BaseModule.ModuleID_TroubleShooting);
		setModuleName("BeTroubleShooting");
	}

	@Override
	public boolean init() {
		hiveApDebugMgmt = new HiveApDebugMgmtImpl();
		clientMonitorMgmt = hiveApDebugMgmt.getClientMonitorMgmt();
		vlanProbeMgmt = hiveApDebugMgmt.getVlanProbeMgmt();

		BoObserver.addBoEventListener(this, new BoEventFilter<HmDomain>(HmDomain.class));

		return true;
	}

	@Override
	public boolean run() {
		if (hiveApDebugMgmt != null && !hiveApDebugMgmt.isStarted()) {
			hiveApDebugMgmt.start();
		}

		return true;
	}

	@Override
	public void eventDispatched(BeBaseEvent event) {
		if (event.isShutdownRequestEvent()) {
			shutdown();

			return;
		}

		hiveApDebugMgmt.add(event);
	}

	@Override
	public boolean shutdown() {
		BoObserver.removeBoEventListener(this);

		if (clientMonitorMgmt != null) {
			clientMonitorMgmt.removeRequests();
		}

		if (vlanProbeMgmt != null) {
			vlanProbeMgmt.removeRequests();
		}

		if (hiveApDebugMgmt != null) {
			hiveApDebugMgmt.stop();
		}

		return true;
	}

	@Override
	public HiveApDebugMgmtImpl getHiveApDebugMgmt() {
		return hiveApDebugMgmt;
	}

	@Override
	public ClientMonitorMgmt<ClientMonitor, ClientMonitorNotification> getClientMonitorMgmt() {
		return clientMonitorMgmt;	
	}

	@Override
	public VlanProbeMgmt<VlanProbe, VlanProbeNotification> getVlanProbeMgmt() {
		return vlanProbeMgmt;
	}

	@Override
	public void boCreated(HmDomain hmDomain) {
		log.info("boCreated", "Created domain: " + hmDomain);
	}

	@Override
	public void boUpdated(HmDomain hmDomain) {
		log.info("boUpdated", "Updated domain: " + hmDomain);

		switch (hmDomain.getRunStatus()) {
			case HmDomain.DOMAIN_RESTORE_STATUS:
			case HmDomain.DOMAIN_DISABLE_STATUS:
				removeHiveApDebugs(hmDomain.getDomainName());
				break;
			case HmDomain.DOMAIN_BACKUP_STATUS:
			case HmDomain.DOMAIN_DEFAULT_STATUS:
			case HmDomain.DOMAIN_UPDATE_STATUS:
			case HmDomain.DOMAIN_UNKNOWN_STATUS:
			default:
				break;
		}
	}

	@Override
	public void boRemoved(HmDomain hmDomain) {
		log.info("boRemoved", "Removed domain: " + hmDomain);
		removeHiveApDebugs(hmDomain.getDomainName());
	}

	private void removeHiveApDebugs(String domainName) {
		// Remove client monitor processes.
		clientMonitorMgmt.removeRequests(domainName);

		// Remove VLAN probe processes.
		vlanProbeMgmt.removeRequests(domainName);
	}

}