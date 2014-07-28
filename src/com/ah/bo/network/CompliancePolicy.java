package com.ah.bo.network;

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
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "COMPLIANCE_POLICY")
@org.hibernate.annotations.Table(appliesTo = "COMPLIANCE_POLICY", indexes = {
		@Index(name = "COMPLIANCE_POLICY_OWNER", columnNames = { "OWNER" })
		})
public class CompliancePolicy implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long	id;

	public static final int COMPLIANCE_POLICY_POOR = 1;
	public static final int COMPLIANCE_POLICY_GOOD = 2;
	public static final int COMPLIANCE_POLICY_EXCELLENT = 3;
	private int	clientOpen = COMPLIANCE_POLICY_POOR;
	private int	clientOpenAuth = COMPLIANCE_POLICY_GOOD;
	private int	clientWep = COMPLIANCE_POLICY_POOR;
	private int	clientPsk = COMPLIANCE_POLICY_GOOD;
	private int	clientPrivatePsk =COMPLIANCE_POLICY_EXCELLENT;
	private int	client8021x = COMPLIANCE_POLICY_EXCELLENT;
	
	private int hiveApSsh = COMPLIANCE_POLICY_EXCELLENT;
	private int hiveApTelnet = COMPLIANCE_POLICY_POOR;
	private int hiveApPing = COMPLIANCE_POLICY_GOOD;
	private int hiveApSnmp = COMPLIANCE_POLICY_POOR;
	
	private boolean passwordSSID = true;
//	private boolean passwordHm = true;
	private boolean passwordHive = true;
	private boolean passwordCapwap = true;
	private boolean passwordHiveap = true;

	// ------------implement interface
	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return "Compliance Policy";
	}

	@ManyToOne(fetch = FetchType.LAZY)
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

	@Version
	private Timestamp	version;

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
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

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public int getClientOpen() {
		return clientOpen;
	}

	public void setClientOpen(int clientOpen) {
		this.clientOpen = clientOpen;
	}

	public int getClientWep() {
		return clientWep;
	}

	public void setClientWep(int clientWep) {
		this.clientWep = clientWep;
	}

	public int getClientPsk() {
		return clientPsk;
	}

	public void setClientPsk(int clientPsk) {
		this.clientPsk = clientPsk;
	}

	public int getClientPrivatePsk() {
		return clientPrivatePsk;
	}

	public void setClientPrivatePsk(int clientPrivatePsk) {
		this.clientPrivatePsk = clientPrivatePsk;
	}

	public int getClient8021x() {
		return client8021x;
	}

	public void setClient8021x(int client8021x) {
		this.client8021x = client8021x;
	}

	public int getHiveApSsh() {
		return hiveApSsh;
	}

	public void setHiveApSsh(int hiveApSsh) {
		this.hiveApSsh = hiveApSsh;
	}

	public int getHiveApTelnet() {
		return hiveApTelnet;
	}

	public void setHiveApTelnet(int hiveApTelnet) {
		this.hiveApTelnet = hiveApTelnet;
	}

	public int getHiveApPing() {
		return hiveApPing;
	}

	public void setHiveApPing(int hiveApPing) {
		this.hiveApPing = hiveApPing;
	}

	public int getHiveApSnmp() {
		return hiveApSnmp;
	}

	public void setHiveApSnmp(int hiveApSnmp) {
		this.hiveApSnmp = hiveApSnmp;
	}

	public int getClientOpenAuth() {
		return clientOpenAuth;
	}

	public void setClientOpenAuth(int clientOpenAuth) {
		this.clientOpenAuth = clientOpenAuth;
	}

	public boolean getPasswordSSID() {
		return passwordSSID;
	}

	public void setPasswordSSID(boolean passwordSSID) {
		this.passwordSSID = passwordSSID;
	}

//	public boolean getPasswordHm() {
//		return passwordHm;
//	}
//
//	public void setPasswordHm(boolean passwordHm) {
//		this.passwordHm = passwordHm;
//	}

	public boolean getPasswordHive() {
		return passwordHive;
	}

	public void setPasswordHive(boolean passwordHive) {
		this.passwordHive = passwordHive;
	}

	public boolean getPasswordCapwap() {
		return passwordCapwap;
	}

	public void setPasswordCapwap(boolean passwordCapwap) {
		this.passwordCapwap = passwordCapwap;
	}

	public boolean getPasswordHiveap() {
		return passwordHiveap;
	}

	public void setPasswordHiveap(boolean passwordHiveap) {
		this.passwordHiveap = passwordHiveap;
	}

}