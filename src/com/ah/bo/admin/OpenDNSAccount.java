package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;

@Entity
@Table(name = "OPENDNS_ACCOUNT")
@org.hibernate.annotations.Table(appliesTo = "OPENDNS_ACCOUNT", indexes = {
		@Index(name = "OPENDNS_ACCOUNT_OWNER", columnNames = { "OWNER" })
		})
public class OpenDNSAccount implements HmBo {
	private static final long serialVersionUID = 1L;
	public static final String OPENDNS_SERVER_1 = "208.67.222.222";
	public static final String OPENDNS_SERVER_2 = "208.67.220.220";
	
	@Id
	@GeneratedValue
	private Long	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}
	
	@Version
	private Timestamp version;
	
	private String userName;
	
	private String password;
	
	private String token;
	
	private String dnsServer1 = OPENDNS_SERVER_1;
	
	private String dnsServer2 = OPENDNS_SERVER_2;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getVersion() {
		return version;
	}

	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDnsServer1() {
		return dnsServer1;
	}

	public void setDnsServer1(String dnsServer1) {
		this.dnsServer1 = dnsServer1;
	}

	public String getDnsServer2() {
		return dnsServer2;
	}

	public void setDnsServer2(String dnsServer2) {
		this.dnsServer2 = dnsServer2;
	}

	@Override
	public String getLabel() {
		return userName;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
