package com.ah.be.ts.hiveap.monitor.client;

import com.ah.be.app.AhAppContainer;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.event.BeClientMonitoringEvent;
import com.ah.be.ts.hiveap.DebugException;
import com.ah.be.ts.hiveap.DebugState;
import com.ah.be.ts.hiveap.DebugState.State;
import com.ah.be.ts.hiveap.monitor.Monitor;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class ClientMonitor extends Monitor {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(ClientMonitor.class.getSimpleName());

    //***************************************************************
    // Variables
    //***************************************************************

	private final String clientMac;

	// Indicates if one of the behaviors(802.11, AUTH or DHCP) for the monitored client is active over the current AP. 
	private boolean active;

	// Recent event of interest.
	private ClientMonitorNotification recentInterestEvent;

	// Indicates if need to filter the IEEE80211 probe events.
	private boolean filteringProbeEvents = false;
	
	private boolean enableMonitorPerformance = false;

	public ClientMonitor(HiveAp hiveAp, String sessionId, String clientMac) {
		super.hiveAp = hiveAp;
		super.hiveApMac = hiveAp.getMacAddress();
		super.sessionId = sessionId;
		String description = MgrUtil.getUserMessage("info.debug.state.notInProcess", getName());
		super.debugState = new DebugState(State.UNINITIATED, description);

		// Cookie evaluation during the initialization of this object is very important. 
		super.cookieId = AhAppContainer.getBeCommunicationModule().getSequenceNumber();
		this.clientMac = clientMac;
	}
	
	public ClientMonitor(HiveAp hiveAp, String sessionId, String clientMac, boolean enableMonitorPerformance) {
	    this(hiveAp, sessionId, clientMac);
	    this.enableMonitorPerformance = enableMonitorPerformance;
	}

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

	public String getClientMac() {
		return clientMac;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public ClientMonitorNotification getRecentInterestEvent() {
		return recentInterestEvent;
	}

	public void setRecentInterestEvent(ClientMonitorNotification recentInterestEvent) {
		this.recentInterestEvent = recentInterestEvent;
	}

	public boolean isFilteringProbeEvents() {
		return filteringProbeEvents;
	}

	public void setFilteringProbeEvents(boolean filteringProbeEvents) {
		this.filteringProbeEvents = filteringProbeEvents;
	}
	
	public boolean isEnableMonitorPerformance() {
        return enableMonitorPerformance;
    }
    public void setEnableMonitorPerformance(boolean enableMonitorPerformance) {
        this.enableMonitorPerformance = enableMonitorPerformance;
    }
    @Override
	public short getCapwapType() {
		return BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING;
	}

	@Override
	public Category getCategory() {
		return Category.CLIENT_MONITOR;
	}

	@Override
	public String getName() {
		return "client monitor";
	}

	@Override
	public int initiate() throws DebugException {
		log.info("initiate", "Initiating a " + getName() + " process - " + this);
		BeClientMonitoringEvent clientMonitorReqEvent = new BeClientMonitoringEvent();
		clientMonitorReqEvent.setClientMac(clientMac);
		if(enableMonitorPerformance) {
            clientMonitorReqEvent
                    .setQueryType(BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTPERFORMANCE);
		}
		debugInitEvent = clientMonitorReqEvent;
		return super.initiate();
	}

	@Override
	public int terminate() throws DebugException {
		log.info("terminate", "Terminating a " + getName() + " process - " + this);
		return super.terminate();
	}

	@Override
	public String toString() {
		return "Client Monitor - Client: " + clientMac + "; " + NmsUtil.getOEMCustomer().getAccessPonitName() + ": " + hiveApMac + "; Cookie: " + cookieId;
	}

}