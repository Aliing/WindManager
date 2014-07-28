package com.ah.bo.igmp;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
@Entity
@Table(name = "IGMP_POLICY")
@org.hibernate.annotations.Table(appliesTo = "IGMP_POLICY", indexes = {
		@Index(name = "IGMP_POLICY_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class IgmpPolicy implements HmBo, Comparable<Object> {

	private static final long serialVersionUID = -8698198239708451297L;

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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "HIVE_AP_ID")
	private HiveAp hiveAp;
	
	
	private Integer vlanId;
	private boolean igmpSnooping;
	private boolean immediateLeave;
	private Integer delayLeaveQueryInterval;
	private Integer delayLeaveQueryCount;
	private Integer routerPortAginTime;
	private Integer robustnessCount;
	


	public Integer getVlanId() {
		return vlanId;
	}

	public void setVlanId(Integer vlanId) {
		this.vlanId = vlanId;
	}

	public boolean isIgmpSnooping() {
		return igmpSnooping;
	}

	public void setIgmpSnooping(boolean igmpSnooping) {
		this.igmpSnooping = igmpSnooping;
	}

	public boolean isImmediateLeave() {
		return immediateLeave;
	}

	public void setImmediateLeave(boolean immediateLeave) {
		this.immediateLeave = immediateLeave;
	}

	public Integer getDelayLeaveQueryInterval() {
		return delayLeaveQueryInterval;
	}

	public void setDelayLeaveQueryInterval(Integer delayLeaveQueryInterval) {
		this.delayLeaveQueryInterval = delayLeaveQueryInterval;
	}

	public Integer getDelayLeaveQueryCount() {
		return delayLeaveQueryCount;
	}

	public void setDelayLeaveQueryCount(Integer delayLeaveQueryCount) {
		this.delayLeaveQueryCount = delayLeaveQueryCount;
	}

	public Integer getRouterPortAginTime() {
		return routerPortAginTime;
	}

	public void setRouterPortAginTime(Integer routerPortAginTime) {
		this.routerPortAginTime = routerPortAginTime;
	}

	public Integer getRobustnessCount() {
		return robustnessCount;
	}

	public void setRobustnessCount(Integer robustnessCount) {
		this.robustnessCount = robustnessCount;
	}
	
	public HiveAp getHiveAp() {
		return hiveAp;
	}

	public void setHiveAp(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
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
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof IgmpPolicy)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((IgmpPolicy) other).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}
	@Override
	public IgmpPolicy clone() {
		try {
			return (IgmpPolicy) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public int compareTo(Object o) {
		if(o instanceof IgmpPolicy){
			if(vlanId > ((IgmpPolicy)o).vlanId){
				return 1;
			}else if(vlanId < ((IgmpPolicy)o).vlanId){
				return -1;
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}
}
