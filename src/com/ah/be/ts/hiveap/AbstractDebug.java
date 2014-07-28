package com.ah.be.ts.hiveap;

import java.util.TimeZone;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.bo.hiveap.HiveAp;

public abstract class AbstractDebug implements Debug {

	private static final long serialVersionUID = 1L;

    //***************************************************************
    // Variables
    //***************************************************************

	/** Used to represent an unique debug */
	protected int cookieId;

	/** Time Stamp */
	protected long timstamp;

	/** Time Zone */
	protected TimeZone timeZone;

	/** The MAC of HiveAP to be debugged */
	protected String hiveApMac;

	/** The HiveAP to be debugged */
	protected HiveAp hiveAp;

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

	@Override
	public int getCookieId() {
		return cookieId;
	}

	@Override
	public long getTimstamp() {
		return timstamp;
	}

	@Override
	public void setTimstamp(long timstamp) {
		this.timstamp = timstamp;
	}

	@Override
	public TimeZone getTimeZone() {
		return timeZone;
	}

	@Override
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	@Override
	public String getHiveApMac() {
		return hiveApMac;
	}

	@Override
	public void setHiveApMac(String hiveApMac) {
		this.hiveApMac = hiveApMac;
	}

	@Override
	public HiveAp getHiveAp() {
		return hiveAp;
	}

	@Override
	public void setHiveAp(HiveAp hiveAp) {
		this.hiveAp = hiveAp;

		if (hiveAp != null && (hiveApMac == null || hiveApMac.trim().isEmpty())) {
			hiveApMac = hiveAp.getMacAddress();
		}
	}

	/*
	 * Overwriting equals() and hashCode() is necessary here since we has made use of this class as the key for the map
	 * used to store all active HiveAP debugs in a static instance implementing the AhApDebugMgmt interface.
	 */
	@Override
	public final boolean equals(Object obj) {
		return obj instanceof AbstractDebug &&
			   getCategory().equals(((AbstractDebug) obj).getCategory()) &&
			   hiveApMac.equalsIgnoreCase(((AbstractDebug) obj).getHiveApMac());
	}

	@Override
	public final int hashCode() {
		return getCookieId();
	}

	public static String getDebugName(short capwapDebugEventType) {
		String debugName;

		switch (capwapDebugEventType) {
			// Client Monitor
			case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING:
				debugName = "client monitor";
				break;
			// VLAN Probe
			case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE:
				debugName = "VLAN probe";
				break;
			// Universal Debug Name
			default:
				debugName = "Debug";
				break;
		}

		return debugName;
	}

}