package com.ah.bo.monitor;

import java.sql.Timestamp;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "MAP_NODE")
@org.hibernate.annotations.Table(appliesTo = "MAP_NODE", indexes = {
		@Index(name = "MAP_NODE_OWNER", columnNames = { "OWNER" })
		})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "NODE_TYPE", discriminatorType = DiscriminatorType.STRING)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class MapNode implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
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

	private double x, y;

	private short severity;

	private String iconName;

	private String address;

	private Float latitude, longitude, centerLatitude, centerLongitude;

	private short centerZoom = -1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_MAP_ID", nullable = true)
	@Index(name = "map_node_parent")
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
	public Timestamp getVersion() {
		return version;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public short getSeverity() {
		return severity;
	}

	public void setSeverity(short severity) {
		this.severity = severity;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public Float getCenterLatitude() {
		return centerLatitude;
	}

	public void setCenterLatitude(Float centerLatitude) {
		this.centerLatitude = centerLatitude;
	}

	public Float getCenterLongitude() {
		return centerLongitude;
	}

	public void setCenterLongitude(Float centerLongitude) {
		this.centerLongitude = centerLongitude;
	}

	public short getCenterZoom() {
		return centerZoom;
	}

	public void setCenterZoom(short centerZoom) {
		this.centerZoom = centerZoom;
	}

	public abstract boolean isLeafNode();

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MapNode)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((MapNode) other)
				.getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
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
	public void setVersion(Timestamp version) {
		this.version = version;
	}

}