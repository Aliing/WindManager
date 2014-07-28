package com.ah.bo.admin;

import java.sql.Timestamp;
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

import com.ah.be.app.HmBeOsUtil;
import com.ah.bo.HmBo;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "HM_ACCESS_CONTROL")
public class HmAccessControl implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Timestamp version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain owner;

	public static final short CONTROL_TYPE_DENY = 0;
	public static final short CONTROL_TYPE_PERMIT = 1;
	public static EnumItem[] PHASE1_AUTH_METHOD = MgrUtil.enumItems(
			"enum.hm.access.control.type.", new int[] { CONTROL_TYPE_DENY,
					CONTROL_TYPE_PERMIT });
	private short controlType = CONTROL_TYPE_DENY;

	@ElementCollection(fetch = FetchType.LAZY)
	@Column(name = "ipAddress")
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HM_ACCESS_CONTROL_IP", joinColumns = @JoinColumn(name = "HM_ACCESS_CONTROL_ID", nullable = true))
	private List<String> ipAddresses;

	public static final short BEHAVIOR_TYPE_PAGE = 0;
	public static final short BEHAVIOR_TYPE_BLANK = 1;
	public static EnumItem[] BEHAVIOR_TYPE = MgrUtil.enumItems(
			"enum.hm.access.control.behavior.", new int[] {
					BEHAVIOR_TYPE_BLANK, BEHAVIOR_TYPE_PAGE });
	private short denyBehavior = BEHAVIOR_TYPE_BLANK;

	@Transient
	private boolean selected;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return "HM Login Access";
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public short getControlType() {
		return controlType;
	}

	public void setControlType(short controlType) {
		this.controlType = controlType;
	}

	public List<String> getIpAddresses() {
		return ipAddresses;
	}

	public void setIpAddresses(List<String> ipAddresses) {
		this.ipAddresses = ipAddresses;
	}

	public short getDenyBehavior() {
		return denyBehavior;
	}

	public void setDenyBehavior(short denyBehavior) {
		this.denyBehavior = denyBehavior;
	}

	@Transient
	public boolean isAllowAccess(String userIp) {
		if (null == userIp) {
			return true;// no IP tread as allow always
		}
		if (controlType == HmAccessControl.CONTROL_TYPE_DENY) {
			// deny mode
			if (null != ipAddresses) {
				for (String ipmask : ipAddresses) {
					String[] ip_mask = ipmask.split("/");
					String ip = ip_mask[0];
					String mask = ip_mask[1];
					boolean isSame = HmBeOsUtil.isInSameSubnet(userIp, ip, mask);
					if (isSame) {
						return false;
					}
				}
			}
			return true;
		} else {
			// permit mode
			if (null != ipAddresses) {
				for (String ipmask : ipAddresses) {
					String[] ip_mask = ipmask.split("/");
					String ip = ip_mask[0];
					String mask = ip_mask[1];
					boolean isSame = HmBeOsUtil.isInSameSubnet(userIp, ip, mask);
					if (isSame) {
						return true;
					}
				}
			}
			return false;
		}
	}

}