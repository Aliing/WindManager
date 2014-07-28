package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

/**
 * @author		wpliang
 * @version		V1.0.0.0 
 */

@Entity
@Table(name = "BONJOUR_GATEWAY_MONITORING")
@org.hibernate.annotations.Table(appliesTo = "BONJOUR_GATEWAY_MONITORING", indexes = {
		@Index(name = "BONJOUR_GATEWAY_MONITORING_OWNER", columnNames = { "OWNER" })
		})
public class BonjourGatewayMonitoring implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;
	
	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String hostName;
	
	@Column(length = 12, nullable = false, unique = true)
	private String macAddress;
	
	private String realmId;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "BONJOUR_SERVICE_DETAIL", joinColumns = @JoinColumn(name = "BONJOUR_GATEWAY_MONITORING_ID", nullable = false))
	private List<BonjourServiceDetail> bonjourServiceDetails = new ArrayList<BonjourServiceDetail>();

	public List<BonjourServiceDetail> getBonjourServiceDetails() {
		return bonjourServiceDetails;
	}

	public void setBonjourServiceDetails(
			List<BonjourServiceDetail> bonjourServiceDetails) {
		this.bonjourServiceDetails = bonjourServiceDetails;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public String getRealmId() {
		return realmId;
	}

	public void setRealmId(String realmId) {
		this.realmId = realmId;
	}

	private boolean defaultFlag;
	
	public boolean isDefaultFlag()
	{
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag)
	{
		this.defaultFlag = defaultFlag;
	}

	@Override
	public boolean equals(Object osObject) {
		if (!(osObject instanceof BonjourGatewayMonitoring)) {
			return false;
		}
		return null == id ? super.equals(osObject) : id.equals(((BonjourGatewayMonitoring) osObject).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
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
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
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
	public BonjourGatewayMonitoring clone() {
		try {
			return (BonjourGatewayMonitoring) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public String getLabel() {
		return null;
	}

}