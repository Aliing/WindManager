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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "ACTIVECLIENT_FILTER")
@org.hibernate.annotations.Table(appliesTo = "ACTIVECLIENT_FILTER", indexes = {
		@Index(name = "ACTIVE_CLIENT_FILTER_OWNER", columnNames = { "OWNER" })
		})
public class ActiveClientFilter implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long		id;

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

	private String	userName;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String	filterName;

	private String	filterApName;

	private String	filterClientMac;

	private String	filterClientIP;

	private String	filterClientHostName;

	private String	filterClientUserName;

	private Long	filterTopologyMap;
	
	//Added from Dakar
	private byte    filterOverallClientHealth;
	
	private String	filterClientOsInfo;
	
	private int		filterClientVLAN;

	private int		filterClientUserProfId;

	private int		filterClientChannel;

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public String getLabel() {
		return this.filterName;
	}

	@Transient
	private boolean	selected;

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public Long getFilterTopologyMap() {
		return filterTopologyMap;
	}

	public void setFilterTopologyMap(Long filterTopology) {
		this.filterTopologyMap = filterTopology;
	}

	public String getFilterApName() {
		return filterApName;
	}

	public void setFilterApName(String userName) {
		this.filterApName = userName;
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	public String getFilterClientMac() {
		return filterClientMac;
	}

	public void setFilterClientMac(String filterClientMac) {
		this.filterClientMac = filterClientMac;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFilterClientHostName() {
		return filterClientHostName;
	}

	public void setFilterClientHostName(String filterClientHostName) {
		this.filterClientHostName = filterClientHostName;
	}

	public String getFilterClientIP() {
		return filterClientIP;
	}

	public void setFilterClientIP(String filterClientIP) {
		this.filterClientIP = filterClientIP;
	}

	public String getFilterClientUserName() {
		return filterClientUserName;
	}

	public void setFilterClientUserName(String filterClientUserName) {
		this.filterClientUserName = filterClientUserName;
	}

	public String getFilterClientOsInfo() {
		return filterClientOsInfo;
	}

	public void setFilterClientOsInfo(String filterClientOsInfo) {
		this.filterClientOsInfo = filterClientOsInfo;
	}

	public int getFilterClientVLAN() {
		return filterClientVLAN;
	}

	public void setFilterClientVLAN(int filterClientVLAN) {
		this.filterClientVLAN = filterClientVLAN;
	}

	public int getFilterClientUserProfId() {
		return filterClientUserProfId;
	}

	public void setFilterClientUserProfId(int filterClientUserProfId) {
		this.filterClientUserProfId = filterClientUserProfId;
	}

	public int getFilterClientChannel() {
		return filterClientChannel;
	}

	public void setFilterClientChannel(int filterClientChannel) {
		this.filterClientChannel = filterClientChannel;
	}

	public byte getFilterOverallClientHealth() {
		return filterOverallClientHealth;
	}

	public void setFilterOverallClientHealth(byte filterOverallClientHealth) {
		this.filterOverallClientHealth = filterOverallClientHealth;
	}

}