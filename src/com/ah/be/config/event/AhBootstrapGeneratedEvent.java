package com.ah.be.config.event;

import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.event.BeEventConst;
import com.ah.bo.hiveap.HiveAp;

/**
 * @author zhang
 * @version 2007-12-11 02:00:08
 */

public class AhBootstrapGeneratedEvent extends AhConfigGeneratedEvent {

	private static final long serialVersionUID = 1L;

	private boolean enableDtls;

	private String capwapServer;
	
	private String capwapServerBackup;
	
	private String vhmName;

	private int cwpUdpPort;

	private int echoTimeOut;

	private int deadInterval;

	private String adminUser;

	private String adminPwd;

	private String dtlsPassWord;
	
	private boolean enableNetdump;
	
	private String netdumpServer;

	public AhBootstrapGeneratedEvent() {
		super.setEventType(BeEventConst.AH_BOOTSTRAP_GENERATED_EVENT);
		super.configType = ConfigType.BOOTSTRAP;
	}

	public AhBootstrapGeneratedEvent(HiveAp hiveAp) {
		this();
		super.hiveAp = hiveAp;
	}

	public boolean isEnableDtls() {
		return enableDtls;
	}

	public void setEnableDtls(boolean enableDtls) {
		this.enableDtls = enableDtls;
	}

	public int getCwpUdpPort() {
		return cwpUdpPort;
	}

	public void setCwpUdpPort(int cwpUdpPort) {
		this.cwpUdpPort = cwpUdpPort;
	}

	public int getEchoTimeOut() {
		return echoTimeOut;
	}

	public void setEchoTimeOut(int echoTimeOut) {
		this.echoTimeOut = echoTimeOut;
	}

	public String getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public String getAdminPwd() {
		return adminPwd;
	}

	public void setAdminPwd(String adminPwd) {
		this.adminPwd = adminPwd;
	}

	public String getDtlsPassWord() {
		return dtlsPassWord;
	}

	public void setDtlsPassWord(String dtlsPassWord) {
		this.dtlsPassWord = dtlsPassWord;
	}

	public void setDeadInterval(int deadInterval) {
		this.deadInterval = deadInterval;
	}

	public int getDeadInterval() {
		return this.deadInterval;
	}

	public String getCapwapServer() {
		return capwapServer;
	}

	public void setCapwapServer(String capwapServer) {
		this.capwapServer = capwapServer;
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