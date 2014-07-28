package com.ah.be.config.hiveap;

public class BootstrapConfigObject extends UpdateObject {

	private String adminName;
	private String password;
	private boolean enableDtls;
	private String passPhrase;
	private String capwapServer;
	private String capwapServerBackup;
	private String vhmName;
	private int udpPort;
	private int echoTimeout;
	private int deadInterval;
	private boolean enableNetdump;
	private String netdumpServer;

	public String getCapwapServer() {
		return capwapServer;
	}

	public void setCapwapServer(String capwapServer) {
		this.capwapServer = capwapServer;
	}

	public int getDeadInterval() {
		return deadInterval;
	}

	public void setDeadInterval(int deadInterval) {
		this.deadInterval = deadInterval;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnableDtls() {
		return enableDtls;
	}

	public void setEnableDtls(boolean enableDtls) {
		this.enableDtls = enableDtls;
	}

	public String getPassPhrase() {
		return passPhrase;
	}

	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	public int getEchoTimeout() {
		return echoTimeout;
	}

	public void setEchoTimeout(int echoTimeout) {
		this.echoTimeout = echoTimeout;
	}

	public String getCapwapServerBackup() {
		return capwapServerBackup;
	}

	public void setCapwapServerBackup(String capwapServerBackup) {
		this.capwapServerBackup = capwapServerBackup;
	}

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}
	
	public boolean isEnableNetdump(){
		return this.enableNetdump;
	}
	
	public void setEnableNetdump(boolean enableNetdump){
		this.enableNetdump = enableNetdump;
	}
	
	public String getNetdumpServer(){
		return this.netdumpServer;
	}
	
	public void setNetdumpServer(String netdumpServer){
		this.netdumpServer = netdumpServer;
	}

}
