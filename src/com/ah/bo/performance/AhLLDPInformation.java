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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "HM_LLDP_INFORMATION")
@org.hibernate.annotations.Table(appliesTo = "HM_LLDP_INFORMATION", indexes = {
		@Index(name = "HM_LLDP_INFORMATION_REPORTER_OWNER", columnNames = {
				"reporter", "OWNER" }),
		@Index(name = "HM_LLDP_INFORMATION_REPORTER", columnNames = { "reporter" }) })
public class AhLLDPInformation implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false, unique = false)
	private HmDomain owner;

	@Version
	private Timestamp version;

	private int ifIndex;

	private String ifName;

	private String deviceID;

	private String portID;

	private String systemName;

	private int poePower;
	
	/**
	 * 1 LLDP & 2 CDP
	 */
	private short protocol;
	
	public static short PROTOCOL_LLDP    = 1;
	public static short PROTOCOL_CDP     = 2;

	@Column(nullable = false)
	private String reporter;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	public String getIfName() {
		return ifName;
	}

	public void setIfName(String ifName) {
		this.ifName = ifName;
	}

	public String getPortID() {
		return portID;
	}

	public void setPortID(String portID) {
		this.portID = portID;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public int getPoePower() {
		return poePower;
	}

	public void setPoePower(int poePower) {
		this.poePower = poePower;
	}

	public String getReporter() {
		return reporter;
	}

	public void setReporter(String reporter) {
		this.reporter = reporter;
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

	@Override
	public String getLabel() {
		return this.getSystemName();
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
	@Transient
	public String getDeviceMac() {
		String mac = deviceID; // deviceID received from SW is string like '1212:4545:7878(mac address)'
		if (mac != null && mac.length() > 14 && mac.indexOf("(") > 0) {
			mac = mac.substring(0, mac.indexOf("("));
		}
		return mac;
	}

	public short getProtocol() {
		return protocol;
	}

	public void setProtocol(short protocol) {
		this.protocol = protocol;
	}
}
