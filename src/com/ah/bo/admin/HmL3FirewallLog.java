package com.ah.bo.admin;

import java.sql.Timestamp;
import java.util.TimeZone;

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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.util.datetime.AhDateTimeUtil;

/**
 * @author wpliang
 */

@Entity
@Table(name = "HM_L3FIREWALLLOG")
@org.hibernate.annotations.Table(appliesTo = "HM_L3FIREWALLLOG", indexes = {
		@Index(name = "HM_L3_FIREWALL_LOG_OWNER", columnNames = { "OWNER" }),
		@Index(name = "HM_L3_FIREWALL_LOG_TIMESTAMP", columnNames = {"OPERATIONTIMESTAMP"})
		})
public class HmL3FirewallLog implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	private String action;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String clientIp;

	private String destination;

	private String device;

	private long operationTimeStamp;

	private String operationTimeZone = TimeZone.getDefault().getID();

	/**
	 * No owner field, because system log is not always driven by user & i have
	 * no idea to get user context in be<br>
	 * <br>
	 * mark: exception when find bo and bo without owner field.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = true)
	private HmDomain owner;

	@Transient
	private boolean selected;

	private String service;

	private String source;

	private String username;

	@Version
	private Timestamp version;

	public String getAction() {
		return action;
	}

	public String getClientIp() {
		return clientIp;
	}

	public String getDestination() {
		return destination;
	}

	public String getDevice() {
		return device;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return "systemlog";
	}
	
	public String getOperationTime() {
		return AhDateTimeUtil.getSpecifyDateTime(operationTimeStamp, TimeZone.getTimeZone(operationTimeZone));
	}

	public long getOperationTimeStamp() {
		return operationTimeStamp;
	}

	public String getOperationTimeZone() {
		return operationTimeZone;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}
	
	public String getService() {
		return service;
	}
	
	public String getSource() {
		return this.source;
	}

	public String getUsername() {
		return username;
	}
	
	/**
	 * logTime keep track of when log, and system log obj can not changed, so
	 * version is unnecessary
	 */
	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public void setOperationTimeStamp(long operationTimeStamp) {
		this.operationTimeStamp = operationTimeStamp;
	}

	public void setOperationTimeZone(String operationTimeZone) {
		this.operationTimeZone = operationTimeZone;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setService(String service) {
		this.service = service;
	}

	public void setSource(String strSrc) {
		this.source = strSrc;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

}