/*
 * @author Chris Scheers
 */
package com.ah.bo.monitor;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

/**
 * BO of RSSI report in location tracking
 */
@Entity
@Table(name = "LOCATION_HISTORY")
@org.hibernate.annotations.Table(appliesTo = "LOCATION_HISTORY", indexes = {
		@Index(name = "LOCATION_HISTORY_OWNER", columnNames = { "OWNER" }),
		@Index(name = "LOCATION_HISTORY_CLIENT_MAC", columnNames = { "CLIENTMAC" })
		})
public class LocationHistory implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;

	@Column(length = MAC_ADDRESS_LENGTH, nullable = false)
	public String clientMac;

	public double x1, x2, x3;

	public int weight;

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
		return clientMac;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
	}

}