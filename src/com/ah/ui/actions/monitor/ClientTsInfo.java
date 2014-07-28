package com.ah.ui.actions.monitor;

public class ClientTsInfo {
	public static final String TID = "Tid";
	public static final String AC = "AC";
	public static final String DIRECTION = "direction";
	public static final String UP_DOT1DTAG = "dot1Dtag";
	public static final String PSB = "psb";
	public static final String MEDIUM_TIME = "medium_time";
	public static final String DIRECTION_BIDI = "BIDI";
	public static final String DIRECTION_UPLINK = "uplink";
	public static final String DIRECTION_DOWNLINK = "downlink";
	public static final String DIRECTION_BI_DIRECTION = "bi-direction";
	public static final String UP ="UP";
	public static final String UP_LEGACY = "Legacy";
	public static final String UP_UAPSD = "UAPSD";
	public static final String STATION = "station";
	public static final String ADMCTL = "ADMCTL";
	
	public String tid;
	public String ac;
	public String direction;
	public String up;
	public String psb;
	public String mediumTime;
	public String admctl;
	
	public String getAdmctl() {
		return admctl;
	}
	public void setAdmctl(String admctl) {
		this.admctl = admctl;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getAc() {
		return ac;
	}
	public void setAc(String ac) {
		this.ac = ac;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getUp() {
		return up;
	}
	public void setUp(String up) {
		this.up = up;
	}
	public String getPsb() {
		return psb;
	}
	public void setPsb(String psb) {
		this.psb = psb;
	}
	public String getMediumTime() {
		return mediumTime;
	}
	public void setMediumTime(String mediumTime) {
		this.mediumTime = mediumTime;
	}
}
