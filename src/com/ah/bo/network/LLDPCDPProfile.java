package com.ah.bo.network;

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
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "LLDPCDPPROFILE" , uniqueConstraints = { @UniqueConstraint(columnNames = {
		"OWNER", "PROFILENAME" }) })
@org.hibernate.annotations.Table(appliesTo = "LLDPCDPPROFILE", indexes = {
		@Index(name = "LLDP_CDP_PROFILE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class LLDPCDPProfile implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long		id;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String		profileName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String		description;

	//for aps and brs
	private boolean		enableLLDP		= true;

	private boolean		enableCDP;

	private boolean		lldpReceiveOnly;

	private int			lldpMaxEntries	= 64;

	private int			lldpHoldTime	= 90;

	private int			lldpTimer		= 30;

	private int			cdpMaxEntries	= 64;
	
	private int			lldpMaxPower    = 154;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;
	
	//Added from Chesapeake

	//for switch
	private boolean		enableLLDPHostPorts;
	
	private boolean		enableLLDPNonHostPorts;
	
	private boolean		enableCDPHostPorts;
	
	private boolean		enableCDPNonHostPorts;
	
	private int			delayTime = 2;
	
	private int			repeatCount = 3;

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
	public Long getId() {
		return this.id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public String getLabel() {
		return this.profileName;
	}
	
	@Override
    public LLDPCDPProfile clone() {
       try {
           return (LLDPCDPProfile)super.clone();
       } catch (CloneNotSupportedException e) {
           return null;
       }
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

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public boolean isEnableCDP() {
		return enableCDP;
	}

	public void setEnableCDP(boolean enableCDP) {
		this.enableCDP = enableCDP;
	}

	public boolean isEnableLLDP() {
		return enableLLDP;
	}

	public void setEnableLLDP(boolean enableLLDP) {
		this.enableLLDP = enableLLDP;
	}

	public int getCdpMaxEntries() {
		return cdpMaxEntries;
	}

	public void setCdpMaxEntries(int cdpMaxEntries) {
		this.cdpMaxEntries = cdpMaxEntries;
	}

	public int getLldpHoldTime() {
		return lldpHoldTime;
	}

	public void setLldpHoldTime(int lldpHoldTime) {
		this.lldpHoldTime = lldpHoldTime;
	}

	public int getLldpMaxEntries() {
		return lldpMaxEntries;
	}

	public void setLldpMaxEntries(int lldpMaxEntries) {
		this.lldpMaxEntries = lldpMaxEntries;
	}

	public int getLldpTimer() {
		return lldpTimer;
	}

	public void setLldpTimer(int lldpTimer) {
		this.lldpTimer = lldpTimer;
	}
	
	public int getLldpMaxPower() {
		return lldpMaxPower;
	}

	public void setLldpMaxPower(int lldpMaxPower) {
		this.lldpMaxPower = lldpMaxPower;
	}

	public boolean isLldpReceiveOnly() {
		return lldpReceiveOnly;
	}

	public void setLldpReceiveOnly(boolean lldpReceiveOnly) {
		this.lldpReceiveOnly = lldpReceiveOnly;
	}

	public int getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public boolean isEnableLLDPHostPorts() {
		return enableLLDPHostPorts;
	}

	public void setEnableLLDPHostPorts(boolean enableLLDPHostPorts) {
		this.enableLLDPHostPorts = enableLLDPHostPorts;
	}

	public boolean isEnableLLDPNonHostPorts() {
		return enableLLDPNonHostPorts;
	}

	public void setEnableLLDPNonHostPorts(boolean enableLLDPNonHostPorts) {
		this.enableLLDPNonHostPorts = enableLLDPNonHostPorts;
	}

	public boolean isEnableCDPHostPorts() {
		return enableCDPHostPorts;
	}

	public void setEnableCDPHostPorts(boolean enableCDPHostPorts) {
		this.enableCDPHostPorts = enableCDPHostPorts;
	}

	public boolean isEnableCDPNonHostPorts() {
		return enableCDPNonHostPorts;
	}

	public void setEnableCDPNonHostPorts(boolean enableCDPNonHostPorts) {
		this.enableCDPNonHostPorts = enableCDPNonHostPorts;
	}

}