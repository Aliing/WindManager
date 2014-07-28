package com.ah.be.ts.hiveap.probe.vlan;

import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.event.BeVLANProbeEvent;
import com.ah.be.ts.hiveap.DebugException;
import com.ah.be.ts.hiveap.probe.Probe;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.Tracer;

public class VlanProbe extends Probe {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(VlanProbe.class.getSimpleName());

    //***************************************************************
    // Variables
    //***************************************************************

	private short vlanFrom;

	private short vlanTo;

	private short retryTimes;

	private short timeout;

	public VlanProbe(HiveAp hiveAp, String sessionId) {
		super.hiveAp = hiveAp;
		super.hiveApMac = hiveAp.getMacAddress();
		super.sessionId = sessionId;
	}

	public VlanProbe(HiveAp hiveAp, String sessionId, int groupId) {
		this(hiveAp, sessionId);
		super.groupId = groupId;
	}

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

	public short getVlanFrom() {
		return vlanFrom;
	}

	public void setVlanFrom(short vlanFrom) {
		this.vlanFrom = vlanFrom;
	}

	public short getVlanTo() {
		return vlanTo;
	}

	public void setVlanTo(short vlanTo) {
		this.vlanTo = vlanTo;
	}

	public short getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(short retryTimes) {
		this.retryTimes = retryTimes;
	}

	public short getTimeout() {
		return timeout;
	}

	public void setTimeout(short timeout) {
		this.timeout = timeout;
	}

	@Override
	public short getCapwapType() {
		return BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE;
	}

	@Override
	public Category getCategory() {
		return Category.VLAN_PROBE;
	}

	@Override
	public String getName() {
		return "VLAN probe";
	}

	@Override
	public int initiate() throws DebugException {
		log.info("initiate", "Initiating a " + getName() + " process - " + this );
		BeVLANProbeEvent vlanProbeReqEvent = new BeVLANProbeEvent();
		vlanProbeReqEvent.setMinVlanId(vlanFrom);
		vlanProbeReqEvent.setMaxVlanId(vlanTo);
		vlanProbeReqEvent.setProbeTimeout(timeout);
		vlanProbeReqEvent.setRetry(retryTimes);
		debugInitEvent = vlanProbeReqEvent;

		return super.initiate();
	}

	@Override
	public int terminate() throws DebugException {
		log.info("terminate", "Terminating a " + getName() + " process - " + this);

		return super.terminate();
	}

	@Override
	public String toString() {
		return "VLAN Probe - " + NmsUtil.getOEMCustomer().getAccessPonitName() + ": " + hiveApMac + "; Cookie: " + cookieId + "; Session: " + sessionId + "; Group: " + groupId;
	}

}