package com.ah.bo.dashboard;

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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "HM_DASHBOARD_APPAP")
@org.hibernate.annotations.Table(appliesTo = "HM_DASHBOARD_APPAP", indexes = {
		@Index(name = "DASHBOARD_APPAP_OWNER", columnNames = { "OWNER" }),
		@Index(name = "DASHBOARD_APPAP_APMAC", columnNames = { "apMac" })
		})
public class AhDashboardAppAp implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	private Long dashId;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String apMac="";

	private long timestamp=System.currentTimeMillis();
	
	@Version
	private Timestamp version;

	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return null;
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
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public Long getDashId() {
		return dashId;
	}

	public void setDashId(Long dashId) {
		this.dashId = dashId;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = (apMac==null?"":apMac);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}