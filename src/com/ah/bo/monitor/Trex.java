package com.ah.bo.monitor;

import java.sql.Timestamp;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "TREX")
@org.hibernate.annotations.Table(appliesTo = "TREX", indexes = {
		@Index(name = "TREX_OWNER", columnNames = { "OWNER" })
		})
public class Trex implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private Timestamp version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_MAP_ID", nullable = true)
	private MapContainerNode parentMap;

	public MapContainerNode getParentMap() {
		return parentMap;
	}

	public void setParentMap(MapContainerNode parentMap) {
		this.parentMap = parentMap;
	}

	@Embedded
	private HmTimeStamp timeStamp;

	public HmTimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(HmTimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getTimeStampString() {
		if (timeStamp == null) {
			return "-";
		}
		return AhDateTimeUtil.getFormattedDateTime(timeStamp);
	}

	public int rssi, frequency;

	public double tx, ty, rx, ry, elevation, erp;

	@Column(length = MAC_ADDRESS_LENGTH)
	public String tid;

	@Column(length = MAC_ADDRESS_LENGTH)
	public String rid;

	public String note;

	@Transient
	public String mapName;

	@Transient
	public boolean filtered;

	public String getTposition() {
		return String.format("%.2f, %.2f", tx, ty);
	}

	public String getRposition() {
		return String.format("%.2f, %.2f", rx, ry);
	}

	public String getElevationString() {
		return String.format("%.2f", elevation);
	}

	public String getErpString() {
		if ((int) erp == erp) {
			return (int) erp + " dBm";
		} else {
			return String.format("%.2f dBm", erp);
		}
	}

	public String getRssiString() {
		return rssi + " dBm";
	}

	public String getNoteString() {
		return note;
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
	public void setSelected(boolean selected) {
	}

	@Override
	public boolean isSelected() {
		return false;
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

	public String getVersionString() {
		return AhDateTimeUtil
				.getSpecifyDateTime(version, TimeZone.getDefault());
	}

	@Override
	public String getLabel() {
		return null;
	}

}