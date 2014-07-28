package com.ah.bo.useraccess;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.be.app.HmBeOsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "MGMT_SERVICE_TIME")
@org.hibernate.annotations.Table(appliesTo = "MGMT_SERVICE_TIME", indexes = {
		@Index(name = "MGMT_SERVICE_TIME_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class MgmtServiceTime implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Range(min = 60, max = 10080)
	private int interval = 1440;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String mgmtName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private boolean enableNtp = true;

	private boolean enableClock = true;

	private String timeZoneStr = "America/Los_Angeles";

	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "mgmtName", "description", "interval",
				"enableNtp", "enableClock", "timeZone","owner", "timeZoneStr" };
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "MGMT_SERVICE_TIME_INFO", joinColumns = @JoinColumn(name = "MGMT_SERVICE_TIME_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<MgmtServiceTimeInfo> timeInfo = new ArrayList<MgmtServiceTimeInfo>();

	public List<MgmtServiceTimeInfo> getTimeInfo() {
		return timeInfo;
	}

	public void setTimeInfo(List<MgmtServiceTimeInfo> timeInfo) {
		this.timeInfo = timeInfo;
	}

	public boolean getEnableNtp() {
		return enableNtp;
	}

	public void setEnableNtp(boolean enableNtp) {
		this.enableNtp = enableNtp;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return this.mgmtName;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMgmtName() {
		return mgmtName;
	}

	public void setMgmtName(String mgmtName) {
		this.mgmtName = mgmtName;
	}

	public boolean getEnableClock() {
		return enableClock;
	}

	public void setEnableClock(boolean enableClock) {
		this.enableClock = enableClock;
	}

	@Transient
	private String timeRadio = "server";


	public String getTimeRadio() {
		return timeRadio;
	}

	public void setTimeRadio(String timeRadio) {
		this.timeRadio = timeRadio;
	}

	@Transient
	public String getEnableNtpValue() {
		return enableNtp ? "Enabled" : "Disabled";
	}

	@Transient
	public String getEnableClockValue() {
		return enableClock ? "Enabled" : "Disabled";
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	public String getTimeZoneOffSet() {
		return HmBeOsUtil.getTimeZoneOffSet(timeZoneStr);
	}

	@Transient
	public String getTimeZoneString() {
		return HmBeOsUtil.getTimeZoneWholeStr(timeZoneStr);
	}

	@Transient
	public Map<String, String> getIpAddressValue() {
		Map<String, String> ipAddress = new HashMap<String, String>();

		if (timeInfo == null || timeInfo.size() <= 0) {
			ipAddress.put("none", MgrUtil
				.getUserMessage("config.optionsTransfer.none"));
			return ipAddress;
		}

		for (MgmtServiceTimeInfo time : timeInfo) {
			if (time != null && time.getIpAddress() != null) {
				ipAddress.put(time.getIpAddress().getAddressName(),
						time.getIpAddress().getAddressName());
			}
		}

		return ipAddress;
	}

	public String getTimeZoneStr()
	{
		return timeZoneStr;
	}

	public void setTimeZoneStr(String timeZoneStr)
	{
		this.timeZoneStr = timeZoneStr;
	}
	
	@Transient
	private int timeZone = HmBeOsUtil.getServerTimeZoneIndex(null);

	@Override
	public MgmtServiceTime clone() {
		try {
			return (MgmtServiceTime) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public int getTimeZone()
	{
		return timeZone;
	}

	public void setTimeZone(int timeZone)
	{
		this.timeZone = timeZone;
	}

}