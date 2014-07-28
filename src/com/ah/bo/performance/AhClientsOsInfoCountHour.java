package com.ah.bo.performance;

import java.sql.Timestamp;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.admin.HmDomain;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "clients_osinfo_count_hour")
@org.hibernate.annotations.Table(appliesTo = "clients_osinfo_count_hour", indexes = {
		@Index(name = "IDX_OSINFO_CLIENTCOUNT_HOUR_TIMESTAMP", columnNames = {"timeStamp","APMAC","OWNER"}),
		@Index(name = "IDX_OSINFO_CLIENTCOUNT_HOUR_OWNER", columnNames = {"OWNER"})
		})
public class AhClientsOsInfoCountHour implements AhClientsOsInfoCountInterface {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String		apName;

	@Column(length = MAC_ADDRESS_LENGTH)
	private String		apMac;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String 		ssid;
	
	private String		osInfo;
	
	private int		clientCount = 0;

	private long		timeStamp = System.currentTimeMillis();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;

	@Version
	private Timestamp		version;

	@Transient
	private TimeZone		tz;
	
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
		return "AhClientsOsInfoCountHour";
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

	@Override
	public void setVersion(Timestamp version) {

	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public int getClientCount() {
		return clientCount;
	}

	public void setClientCount(int clientCount) {
		this.clientCount = clientCount;
	}

	public String getTimeStampString() {
		return AhDateTimeUtil.getSpecifyDateTimeReport(timeStamp, tz);
	}

	public TimeZone getTz() {
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.setTz(tz);
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public String getOsInfo() {
		return osInfo;
	}

	public void setOsInfo(String osInfo) {
		this.osInfo = osInfo;
	}

}