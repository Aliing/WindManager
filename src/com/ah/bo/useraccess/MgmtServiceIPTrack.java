/**
 *@filename		MgmtServiceIPTrack.java
 *@version
 *@author		Fiona
 *@createtime	2008-8-1 PM 03:04:23
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.useraccess;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "MGMT_SERVICE_IP_TRACK")
@org.hibernate.annotations.Table(appliesTo = "MGMT_SERVICE_IP_TRACK", indexes = {
		@Index(name = "MGMT_SERVICE_IP_TRACK_OWNER", columnNames = { "OWNER" }),
		@Index(name = "MGMT_SERVICE_IP_TRACK_NAME", columnNames = { "TRACKNAME" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class MgmtServiceIPTrack implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String trackName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private boolean enableTrack = true;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String ipAddresses;
	
	private boolean useGateway;

	public static short DEFAULT_VALUE_INTERVAL_FOR_TRACKIP = 6;
	public static short DEFAULT_VALUE_INTERVAL_FOR_TRACKWAN = 10;
	
	@Range(min = 1, max = 180)
	private short interval = DEFAULT_VALUE_INTERVAL_FOR_TRACKIP;

	//reomve from dakar_r6, feature from NEC Conf Call Saturday Night
//	@Range(min = 1, max = 180)
//	private short timeout = 2;

	@Range(min = 0, max = 1024)
	private short retryTime = 2;

	private boolean enableAccess;

	private boolean disableRadio;

	private boolean startFailover;

	private int groupType;
	
	
	public static final short IP_TRACK_LOGIC_AND = 1;

	public static final short IP_TRACK_LOGIC_OR = 2;

	public static EnumItem[] ENUM_IP_TRACK_LOGIC = MgrUtil.enumItems(
			"enum.ip.track.logic.", new int[] { IP_TRACK_LOGIC_AND,
					IP_TRACK_LOGIC_OR });

	private short trackLogic = IP_TRACK_LOGIC_AND;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	private boolean selected;
	
	@Transient
	public boolean wanTesting;

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
		return trackName;
	}
	
	@Transient
	public String getValue() {
		return trackName;
	}

	@Transient
	public String[] getIpAddressList() {
		if (null == ipAddresses) {
			ipAddresses = "";
		}
		String[] allIps = ipAddresses.split(",");
		if ("".equals(ipAddresses)) {
			allIps[0] = MgrUtil.getUserMessage("config.optionsTransfer.none");
		}
		return allIps;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public boolean isEnableTrack() {
		return enableTrack;
	}

	public void setEnableTrack(boolean enableTrack) {
		this.enableTrack = enableTrack;
	}

	public String getIpAddresses() {
		return ipAddresses;
	}

	public void setIpAddresses(String ipAddresses) {
		this.ipAddresses = ipAddresses;
	}

	public short getInterval() {
		return interval;
	}

	public void setInterval(short interval) {
		this.interval = interval;
	}

//	public short getTimeout() {
//		return timeout;
//	}
//
//	public void setTimeout(short timeout) {
//		this.timeout = timeout;
//	}

	public short getRetryTime() {
		return retryTime;
	}

	public void setRetryTime(short retryTime) {
		this.retryTime = retryTime;
	}

	public short getTrackLogic() {
		return trackLogic;
	}

	public void setTrackLogic(short trackLogic) {
		this.trackLogic = trackLogic;
	}

	public boolean isEnableAccess() {
		return enableAccess;
	}

	public void setEnableAccess(boolean enableAccess) {
		this.enableAccess = enableAccess;
	}

	public boolean isDisableRadio() {
		return disableRadio;
	}

	public void setDisableRadio(boolean disableRadio) {
		this.disableRadio = disableRadio;
	}

	public boolean isStartFailover() {
		return startFailover;
	}

	public void setStartFailover(boolean startFailover) {
		this.startFailover = startFailover;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof MgmtServiceIPTrack && (null == id ? super.equals(other) : id.equals(((MgmtServiceIPTrack) other).getId()));
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}
	
	@Override
	public MgmtServiceIPTrack clone() {
		try {
			return (MgmtServiceIPTrack) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public boolean isUseGateway()
	{
		return useGateway;
	}

	public void setUseGateway(boolean useGateway)
	{
		this.useGateway = useGateway;
	}
	
	public boolean isWanTesting() {
		return wanTesting;
	}

	public void setWanTesting(boolean wanTesting) {
		this.wanTesting = wanTesting;
	}

	public int getGroupType() {
		return groupType;
	}

	public void setGroupType(int groupType) {
		this.groupType = groupType;
	}

}