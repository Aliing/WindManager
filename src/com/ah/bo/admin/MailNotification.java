package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.bo.HmBo;

@Entity
@Table(name = "MAIL_NOTIFICATION")
public class MailNotification implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long	id;

	@Column(length = 256)
	private String	serverName;

	@Column(length = 256)
	private String	mailFrom;

	@Column(length = 640)
	private String	mailTo;

	private boolean	hdCpu			= true;

	private boolean	hdMemory		= true;

	private boolean	auth			= true;

	private boolean	interfaceValue	= true;

	private boolean	l2Dos			= true;

	private boolean	screen			= true;

	private boolean	vpn				= true;

	private boolean	airScreen		= true;

	private boolean	inNetIdp		= true;
	
	private boolean clientMonitor 	= true;
	
	private boolean clientRegister		= true;

	private byte	hdRadio;

	private byte	capWap;
	
	private byte	system;

	private byte	config;

	private byte	timeBomb;
	
	private byte	security;
	
	private byte	ad;
	
	private byte    tca;
	
	private byte 	client;
	
	private boolean	sendMailFlag;

	private boolean	supportSSL;
	
	private boolean	supportTLS;

	private int		port			= 25;

	private boolean	supportPwdAuth;

	private String	emailUserName;

	private String	emailPassword;

	public String getEmailPassword() {
		return emailPassword;
	}

	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

	public String getEmailUserName() {
		return emailUserName;
	}

	public void setEmailUserName(String emailUserName) {
		this.emailUserName = emailUserName;
	}

	public boolean isSupportPwdAuth() {
		return supportPwdAuth;
	}

	public void setSupportPwdAuth(boolean supportPwdAuth) {
		this.supportPwdAuth = supportPwdAuth;
	}

	public boolean isSupportSSL() {
		return supportSSL;
	}

	public void setSupportSSL(boolean supportSSL) {
		this.supportSSL = supportSSL;
	}

	public boolean getSendMailFlag() {
		return sendMailFlag;
	}

	public void setSendMailFlag(boolean sendMailFlag) {
		this.sendMailFlag = sendMailFlag;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getMailTo() {
		if (mailTo == null) {
			return "";
		}
		
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return "Email Notification Settings";
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain	owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp	version;

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Transient
	private boolean	selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	//
	// @Transient
	// private boolean updateFlag;
	//
	// public boolean isUpdateFlag() {
	// return updateFlag;
	// }
	//
	// public void setUpdateFlag(boolean updateFlag) {
	// this.updateFlag = updateFlag;
	// }

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public byte getCapWap() {
		return capWap;
	}

	public void setCapWap(byte capWap) {
		this.capWap = capWap;
	}
	
	public byte getSystem() {
		return system;
	}

	public void setSystem(byte system) {
		this.system = system;
	}

	public byte getSecurity() {
		return security;
	}

	public void setSecurity(byte security) {
		this.security = security;
	}
	
	public byte getConfig() {
		return config;
	}

	public void setConfig(byte config) {
		this.config = config;
	}

	public byte getHdRadio() {
		return hdRadio;
	}

	public void setHdRadio(byte hdRadio) {
		this.hdRadio = hdRadio;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public boolean isHdCpu() {
		return hdCpu;
	}

	public void setHdCpu(boolean hdCpu) {
		this.hdCpu = hdCpu;
	}

	public boolean isHdMemory() {
		return hdMemory;
	}

	public void setHdMemory(boolean hdMemory) {
		this.hdMemory = hdMemory;
	}

	public boolean isInterfaceValue() {
		return interfaceValue;
	}

	public void setInterfaceValue(boolean interfaceValue) {
		this.interfaceValue = interfaceValue;
	}

	public boolean isL2Dos() {
		return l2Dos;
	}

	public void setL2Dos(boolean dos) {
		l2Dos = dos;
	}

	public boolean isScreen() {
		return screen;
	}

	public void setScreen(boolean screen) {
		this.screen = screen;
	}

	public byte getTimeBomb() {
		return timeBomb;
	}

	public void setTimeBomb(byte timeBomb) {
		this.timeBomb = timeBomb;
	}

	public boolean isVpn() {
		return vpn;
	}

	public void setVpn(boolean vpn) {
		this.vpn = vpn;
	}

	public boolean isAirScreen() {
		return airScreen;
	}

	public void setAirScreen(boolean airScreen) {
		this.airScreen = airScreen;
	}

	public boolean isInNetIdp() {
		return inNetIdp;
	}

	public void setInNetIdp(boolean inNetIdp) {
		this.inNetIdp = inNetIdp;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int sslPort) {
		this.port = sslPort;
	}

	public boolean isSupportTLS() {
		return supportTLS;
	}

	public void setSupportTLS(boolean supportTLS) {
		this.supportTLS = supportTLS;
	}

	public boolean isClientMonitor() {
		return clientMonitor;
	}

	public void setClientMonitor(boolean clientMonitor) {
		this.clientMonitor = clientMonitor;
	}

	public byte getAd() {
		return ad;
	}

	public void setAd(byte ad) {
		this.ad = ad;
	}

	public byte getTca() {
		return tca;
	}

	public void setTca(byte tca) {
		this.tca = tca;
	}

	public byte getClient() {
		return client;
	}

	public void setClient(byte client) {
		this.client = client;
	}

	public boolean isClientRegister() {
		return clientRegister;
	}

	public void setClientRegister(boolean clientRegister) {
		this.clientRegister = clientRegister;
	}
	
	

}