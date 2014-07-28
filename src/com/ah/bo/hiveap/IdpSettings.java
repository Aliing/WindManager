package com.ah.bo.hiveap;

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

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "IDP_SETTINGS")
public class IdpSettings implements HmBo {

	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_INTERVAL = 60; // minutes

	public static final int DEFAULT_THRESHOLD = -75; // dbm

	public static final boolean DEFAULT_FILTER_MANAGED_HIVEAP_BSSID = true;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain owner;

	@Version
	private Timestamp version;

	private int interval = DEFAULT_INTERVAL;

	private int threshold = DEFAULT_THRESHOLD;

	private boolean filterManagedHiveAPBssid = DEFAULT_FILTER_MANAGED_HIVEAP_BSSID;

	@ElementCollection(fetch = FetchType.LAZY)
	@Column(name = "bssid", length = 12)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "IDP_ENCLOSED_ROGUE_AP", joinColumns = @JoinColumn(name = "IDP_SETTING_ID", nullable = true))
	private List<String> enclosedRogueAps = new ArrayList<String>();

	@ElementCollection(fetch = FetchType.LAZY)
	@Column(name = "bssid", length = 12)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "IDP_ENCLOSED_FRIENDLY_AP", joinColumns = @JoinColumn(name = "IDP_SETTING_ID", nullable = true))
	private List<String> enclosedFriendlyAps = new ArrayList<String>();

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public boolean isFilterManagedHiveAPBssid() {
		return filterManagedHiveAPBssid;
	}

	public void setFilterManagedHiveAPBssid(boolean filterManagedHiveAPBssid) {
		this.filterManagedHiveAPBssid = filterManagedHiveAPBssid;
	}

	public List<String> getEnclosedRogueAps() {
		return enclosedRogueAps;
	}

	public void setEnclosedRogueAps(List<String> enclosedRogueAps) {
		this.enclosedRogueAps = enclosedRogueAps;
	}

	public List<String> getEnclosedFriendlyAps() {
		return enclosedFriendlyAps;
	}

	public void setEnclosedFriendlyAps(List<String> enclosedFriendlyAps) {
		this.enclosedFriendlyAps = enclosedFriendlyAps;
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
		return "IDP Setting";
	}

}