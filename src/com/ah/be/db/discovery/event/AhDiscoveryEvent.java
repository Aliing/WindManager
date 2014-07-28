package com.ah.be.db.discovery.event;

import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.bo.hiveap.HiveAp;

public class AhDiscoveryEvent extends BeBaseEvent {

	private static final long serialVersionUID = 1L;

	/** HiveAP Update Type */
	public enum HiveApType {
		CREATED, UPDATED, REMOVED
	}

	/** Indicates the specific HiveAP discovered */
	private HiveAp hiveAp;

	/** Indicates the update type of discovered HiveAP */
	private HiveApType type;

	/**
	 * Indicates whether the HiveAP location reorganized in the process of
	 * discovery needs to be separated
	 */
	private boolean separatingMapLocation;

	/** CAPWAP connect event */
	private BeAPConnectEvent capwapConnectEvent;

	public AhDiscoveryEvent() {
		super.setEventType(BeEventConst.AH_DISCOVERY_EVENT);
	}

	public AhDiscoveryEvent(HiveAp hiveAp) {
		this();
		this.hiveAp = hiveAp;
	}

	public AhDiscoveryEvent(HiveAp hiveAp, HiveApType type) {
		this();
		this.hiveAp = hiveAp;
		this.type = type;
	}

	public HiveAp getHiveAp() {
		return hiveAp;
	}

	public void setHiveAp(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
	}

	public HiveApType getType() {
		return type;
	}

	public void setType(HiveApType type) {
		this.type = type;
	}

	public boolean isSeparatingMapLocation() {
		return separatingMapLocation;
	}

	public void setSeparatingMapLocation(boolean separatingMapLocation) {
		this.separatingMapLocation = separatingMapLocation;
	}

	public BeAPConnectEvent getCapwapConnectEvent() {
		return capwapConnectEvent;
	}

	public void setCapwapConnectEvent(BeAPConnectEvent capwapConnectEvent) {
		this.capwapConnectEvent = capwapConnectEvent;
	}

	@Override
	public String toString() {
		return NmsUtil.getOEMCustomer().getAccessPonitName()+": " + hiveAp + "; Type: " + type;
	}

}