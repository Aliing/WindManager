package com.ah.bo.performance;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "AH_CLIENTEDITVALUES")
@org.hibernate.annotations.Table(appliesTo = "AH_CLIENTEDITVALUES", indexes = {
		@Index(name = "CLIENT_EDIT_VALUES_OWNER", columnNames = { "OWNER" }),
		@Index(name = "CLIENT_EDIT_VALUES_CLIENT_MAC_TYPE", columnNames = { "CLIENTMAC", "TYPE" }),
		@Index(name = "CLIENT_EDIT_VALUES_TYPE_TIME", columnNames = { "TYPE","expirationTime" })
		})
public class AhClientEditValues implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long	id;

	@Column(length = 48, nullable = false)
	private String	clientMac;
	
	public static final short	TYPE_USER_ADD = 0;
	public static final short	TYPE_SELF_REGISTER = 1;
	
	private short	type = TYPE_USER_ADD;
	
	private long	expirationTime;

	
	@Column(length = 32)
	private String	clientHostname = "";

	
	private String	clientIP = "";
	
	@Column(length = 128)
	private String	clientUsername = "";

	
	@Column(length = 32)
	private String	comment1 = "";

	
	@Column(length = 32)
	private String	comment2 = "";
	
	private String	ssidName = "";
	
	@Column(length = 64)
	private String	email = "";
	
	@Column(length = 64)
	private String	companyName = "";

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setSelected(boolean selected) {

	}

	@Override
	public void setVersion(Timestamp version) {

	}

	@Override
	public String getLabel() {
		return clientMac;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public String getClientHostname() {
		return clientHostname;
	}

	public void setClientHostname(String clientHostname) {
		this.clientHostname = clientHostname;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public String getClientMac() {
		return clientMac.toUpperCase();
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

	public String getClientUsername() {
		return clientUsername;
	}

	public void setClientUsername(String clientUsername) {
		this.clientUsername = clientUsername;
	}

	public String getComment1() {
		return comment1;
	}

	public void setComment1(String comment1) {
		this.comment1 = comment1;
	}

	public String getComment2() {
		return comment2;
	}

	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getSsidName() {
		return ssidName;
	}

	public void setSsidName(String ssidName) {
		this.ssidName = ssidName;
	}

}