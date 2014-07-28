package com.ah.bo.network;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "SWITCH_SETTINGS")
@org.hibernate.annotations.Table(appliesTo = "SWITCH_SETTINGS", indexes = {
		@Index(name = "SWITCH_SETTINGS_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class SwitchSettings implements HmBo {
	
	public SwitchSettings(){
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -9202054795007873462L;

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Version
	private Timestamp version;
	
	@Transient
	private boolean selected;
	
	@OneToOne(fetch = FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinColumn(name = "stp_settings_id", nullable = true)
	private StpSettings stpSettings;
	
	//for IGMP settings
	private boolean enableIgmpSnooping = false;
	private boolean enableImmediateLeave = false;
	private boolean enableReportSuppression = true;
	@Range(min = 1, max = 25)
	private Integer globalDelayLeaveQueryInterval = 1;
	@Range(min = 1, max = 7)
	private Integer globalDelayLeaveQueryCount = 2;
	@Range(min = 30, max = 1000)
	private Integer globalRouterPortAginTime = 250;
	@Range(min = 1, max = 3)
	private Integer globalRobustnessCount = 2;
	public boolean isEnableIgmpSnooping() {
		return enableIgmpSnooping;
	}

	public void setEnableIgmpSnooping(boolean enableIgmpSnooping) {
		this.enableIgmpSnooping = enableIgmpSnooping;
	}

	public boolean isEnableImmediateLeave() {
		return enableImmediateLeave;
	}

	public void setEnableImmediateLeave(boolean enableImmediateLeave) {
		this.enableImmediateLeave = enableImmediateLeave;
	}

	public boolean isEnableReportSuppression() {
		return enableReportSuppression;
	}

	public void setEnableReportSuppression(boolean enableReportSuppression) {
		this.enableReportSuppression = enableReportSuppression;
	}
	
	public Integer getGlobalDelayLeaveQueryInterval() {
		return globalDelayLeaveQueryInterval;
	}

	public void setGlobalDelayLeaveQueryInterval(
			Integer globalDelayLeaveQueryInterval) {
		this.globalDelayLeaveQueryInterval = globalDelayLeaveQueryInterval;
	}

	public Integer getGlobalDelayLeaveQueryCount() {
		return globalDelayLeaveQueryCount;
	}

	public void setGlobalDelayLeaveQueryCount(Integer globalDelayLeaveQueryCount) {
		this.globalDelayLeaveQueryCount = globalDelayLeaveQueryCount;
	}

	public Integer getGlobalRouterPortAginTime() {
		return globalRouterPortAginTime;
	}

	public void setGlobalRouterPortAginTime(Integer globalRouterPortAginTime) {
		this.globalRouterPortAginTime = globalRouterPortAginTime;
	}

	public Integer getGlobalRobustnessCount() {
		return globalRobustnessCount;
	}

	public void setGlobalRobustnessCount(Integer globalRobustnessCount) {
		this.globalRobustnessCount = globalRobustnessCount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}
	
	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}
	
	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public StpSettings getStpSettings() {
		return stpSettings;
	}
	
	public StpSettings initStpSettings(HmDomain domain) {
		if (stpSettings == null){
			return new StpSettings(domain);
		}
		return stpSettings;
	}
	
	public StpSettings initStpSettings() {
		if (stpSettings == null){
			return new StpSettings();
		}
		return stpSettings;
	}

	public void setStpSettings(StpSettings stpSettings) {
		this.stpSettings = stpSettings;
	}
	
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
}