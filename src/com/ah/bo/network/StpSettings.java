package com.ah.bo.network;

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

@Entity
@Table(name = "STP_SETTINGS")
@org.hibernate.annotations.Table(appliesTo = "STP_SETTINGS", indexes = {
		@Index(name = "STP_SETTINGS_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class StpSettings implements HmBo {
	
	public StpSettings(){
		
	}
	
	public StpSettings(HmDomain domain){
		this.owner = domain;
	}
	
	private static final long serialVersionUID = -4363453370204533030L;
	public static final short STP_MODE_STP = 1;
	public static final short STP_MODE_RSTP = 2;
	public static final short STP_MODE_MSTP = 3;
	
	public static final String MODE_STP = "stp";
	public static final String MODE_RSTP = "rstp";
	public static final String MODE_MSTP = "mstp";
	
	private boolean enableStp = false;
	
	private short stp_mode = STP_MODE_STP;
	
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
	@JoinColumn(name = "MSTP_REGION_ID")
	private MstpRegion mstpRegion;
	
	public boolean isEnableStp() {
		return enableStp;
	}

	public void setEnableStp(boolean enableStp) {
		this.enableStp = enableStp;
	}

	public MstpRegion getMstpRegion() {
		return mstpRegion;
	}
	
	public void setMstpRegion(MstpRegion mstpRegion) {
		this.mstpRegion = mstpRegion;
	}

	public short getStp_mode() {
		return stp_mode;
	}

	public void setStp_mode(short stp_mode) {
		this.stp_mode = stp_mode;
	}

	@Override
	public HmDomain getOwner() {
		// TODO Auto-generated method stub
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		// TODO Auto-generated method stub
		this.owner = owner;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void setId(Long id) {
		// TODO Auto-generated method stub
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		// TODO Auto-generated method stub
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		// TODO Auto-generated method stub
		this.version = version;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
		this.selected = selected;
	}

	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
}