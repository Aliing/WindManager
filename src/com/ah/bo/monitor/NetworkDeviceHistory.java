package com.ah.bo.monitor;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "network_device_history")
@org.hibernate.annotations.Table(appliesTo = "network_device_history", indexes = {
		@Index(name = "idx_networkdevicehistory_owner", columnNames = {"owner"}),
		@Index(name = "idx_networkdevicehistory_mac", columnNames = {"mac"})
	})
public class NetworkDeviceHistory implements HmBo{
	
	private static final long serialVersionUID = 4468876969963019577L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long  id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;
	
	@Column( columnDefinition = "timestamp with time zone")
	private Timestamp beginTimeStamp ;
	
	@Column( columnDefinition = "timestamp with time zone")
	private Timestamp endTimeStamp ;
	
	@Column(length = 12, nullable = false)
	private String MAC ;
	@Column(nullable = true)
	private long networkPolicy ;
	
	private int milliSeconds2GMT ;
	
	@Column( columnDefinition = "smallint[17]")
	private Short[] vLAN ;
	
	@Column( columnDefinition = "character varying(64)[3]")
	private String[] tags;
	
	@Column( columnDefinition = "bigint[]")
	private Long[] topologyGroup ;
	
	
	public Timestamp getBeginTimeStamp() {
		return beginTimeStamp;
	}

	public void setBeginTimeStamp(Timestamp beginTimeStamp) {
		this.beginTimeStamp = beginTimeStamp;
	}

	public Timestamp getEndTimeStamp() {
		return endTimeStamp;
	}

	public void setEndTimeStamp(Timestamp endTimeStamp) {
		this.endTimeStamp = endTimeStamp;
	}

	public String getMAC() {
		return MAC;
	}

	public void setMAC(String mAC) {
		MAC = mAC;
	}

	public long getNetworkPolicy() {
		return networkPolicy;
	}

	public void setNetworkPolicy(long networkPolicy) {
		this.networkPolicy = networkPolicy;
	}

	public int getMilliSeconds2GMT() {
		return milliSeconds2GMT;
	}

	public void setMilliSeconds2GMT(int milliSeconds2GMT) {
		this.milliSeconds2GMT = milliSeconds2GMT;
	}

	public Short[] getvLAN() {
		return vLAN;
	}

	public void setvLAN(Short[] vLAN) {
		this.vLAN = vLAN;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public Long[] getTopologyGroup() {
		return topologyGroup;
	}

	public void setTopologyGroup(Long[] topologyGroup) {
		this.topologyGroup = topologyGroup;
	}

	@Override
	public String getLabel() {
		return null;
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
		return null;
	}
	
	@Override
	public void setVersion(Timestamp version) {
	}
	
	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
	}
	
	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
}
