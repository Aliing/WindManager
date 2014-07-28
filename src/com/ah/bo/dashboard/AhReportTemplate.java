package com.ah.bo.dashboard;

public class AhReportTemplate {

	private AhDashboard daUsage;
	private AhDashboard daHealth;
	private AhDashboard daSecurity;
	public AhDashboard getDaUsage() {
		return daUsage;
	}
	public void setDaUsage(AhDashboard daUsage) {
		this.daUsage = daUsage;
	}
	public AhDashboard getDaHealth() {
		return daHealth;
	}
	public void setDaHealth(AhDashboard daHealth) {
		this.daHealth = daHealth;
	}
	public AhDashboard getDaSecurity() {
		return daSecurity;
	}
	public void setDaSecurity(AhDashboard daSecurity) {
		this.daSecurity = daSecurity;
	}

}