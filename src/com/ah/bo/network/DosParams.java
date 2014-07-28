package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.util.MgrUtil;

/*
 * @author Chris Scheers
 */

@Embeddable
public class DosParams implements Serializable {

	private static final long serialVersionUID = 1L;

	@Transient
	private FrameType frameType = null;

	/*
	 * For MAC DoS
	 */
	public enum FrameType {
		PROBE_REQ, PROBE_RESP, ASSOC_REQ, ASSOC_RESP, DISASSOC, AUTH, DEAUTH, EAPOL
	}

	@Transient
	private ScreeningType screeningType = null;

	/*
	 * For IP DoS
	 */
	public enum ScreeningType {
		ICMP_FLOOD, UDP_FLOOD, SYN_FLOOD,ARP_FLOOD, ADDRESS_SWEEP, PORT_SCAN, IP_SPOOF, RADIUS_ATTACK
	}

	public FrameType getFrameType() {
		return frameType;
	}

	public void setFrameType(FrameType frameType) {
		this.frameType = frameType;
	}

	public ScreeningType getScreeningType() {
		return screeningType;
	}

	public void setScreeningType(ScreeningType screeningType) {
		this.screeningType = screeningType;
	}

	/*
	 * At least one of the two need to be non-null
	 */
	public String getkey() {
		if (frameType == null) {
			return screeningType.name();
		} else {
			return frameType.name();
		}
	}

	public String getValue() {
		if (frameType == null) {
			return MgrUtil.getUserMessage("enum.screeningType."
					+ screeningType.name());
		} else {
			return MgrUtil.getUserMessage("enum.frameType." + frameType.name());
		}
	}

	public boolean isOfType(String enumName) {
		if (frameType == null) {
			return screeningType.equals(ScreeningType.valueOf(enumName));
		} else {
			return frameType.equals(FrameType.valueOf(enumName));
		}
	}

	private int alarmInterval;

	private int alarmThreshold;

	public enum DosAction {
		ALARM, DROP, DISCONNECT, BAN, BAN_FOREVER;
		public String getKey() {
			return name();
		}

		public String getValue() {
			return MgrUtil.getUserMessage("enum.dosAction." + name());
		}
	}

	private DosAction dosAction;

	private int dosActionTime;

	private boolean enabled;

	public int getAlarmInterval() {
		return alarmInterval;
	}

	public void setAlarmInterval(int alarmInterval) {
		this.alarmInterval = alarmInterval;
	}

	public int getAlarmThreshold() {
		return alarmThreshold;
	}

	public void setAlarmThreshold(int alarmThreshold) {
		this.alarmThreshold = alarmThreshold;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public DosAction getDosAction() {
		return dosAction;
	}

	public void setDosAction(DosAction dosAction) {
		this.dosAction = dosAction;
	}

	public int getDosActionTime() {
		return dosActionTime;
	}

	public void setDosActionTime(int dosActionTime) {
		this.dosActionTime = dosActionTime;
	}
	
	@Transient
	private String restoreId;

	public String getRestoreId() {
		return restoreId;
	}

	public void setRestoreId(String restoreId) {
		this.restoreId = restoreId;
	}

}