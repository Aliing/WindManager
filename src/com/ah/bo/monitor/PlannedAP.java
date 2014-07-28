package com.ah.bo.monitor;

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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "PLANNED_AP")
@org.hibernate.annotations.Table(appliesTo = "PLANNED_AP", indexes = {
		@Index(name = "PLANNED_AP_OWNER", columnNames = { "OWNER" })
		})
public class PlannedAP implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;

	public short apModel, wifi0Power, wifi0Channel, wifi0ChannelWidth,
			wifi1Power, wifi1Channel, wifi1ChannelWidth;

	public boolean wifi0Enabled = true, wifi1Enabled = true;

	public int countryCode;

	public String hostName;

	@Transient
	public short autoWifi0Channel = -1, autoWifi1Channel = -1;

	public double x, y;

	@Transient
	public long[] inNetworkCost;

	@Transient
	public long[] outOfNetworkCost;

	@Transient
	public double r;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_MAP_ID", nullable = true)
	private MapContainerNode parentMap;

	public MapContainerNode getParentMap() {
		return parentMap;
	}

	public void setParentMap(MapContainerNode parentMap) {
		this.parentMap = parentMap;
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

	@Override
	public String getLabel() {
		return hostName;
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

}