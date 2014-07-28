package com.ah.be.topo.idp;

import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.bo.hiveap.Idp;

public class IdpMitigationEvent extends BeBaseEvent {

	private static final long serialVersionUID = 1L;
	private String idpBssid;
	private String reportNodeId;
	private String mitiMac;
	private String wifix;
	private boolean exec;
	private Idp idp;
	
	public IdpMitigationEvent(String idpBssid, String reportNodeId,
			String wifix, boolean exec, Idp idp, String mitiMac) {
		super.setEventType(BeEventConst.AH_IDP_MITIGATE_EVENT);
		this.idpBssid = idpBssid;
		this.reportNodeId = reportNodeId;
		this.wifix = wifix;
		this.exec = exec;
		this.idp = idp;
		this.mitiMac = mitiMac;
	}

	public String getIdpBssid() {
		return idpBssid;
	}

	public void setIdpBssid(String idpBssid) {
		this.idpBssid = idpBssid;
	}

	public String getReportNodeId() {
		return reportNodeId;
	}

	public void setReportNodeId(String reportNodeId) {
		this.reportNodeId = reportNodeId;
	}

	public String getWifix() {
		return wifix;
	}

	public void setWifix(String wifix) {
		this.wifix = wifix;
	}

	public boolean isExec() {
		return exec;
	}

	public void setExec(boolean exec) {
		this.exec = exec;
	}

	public Idp getIdp() {
		return idp;
	}

	public void setIdp(Idp idp) {
		this.idp = idp;
	}

	public String getMitiMac() {
		return mitiMac;
	}

	public void setMitiMac(String mitiMac) {
		this.mitiMac = mitiMac;
	}

}
