package com.ah.bo.performance;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;


@Entity
@Table(name = "ah_app_flow_month")
@org.hibernate.annotations.Table(appliesTo = "ah_app_flow_month", indexes = {
		@Index(name = "IDX_AH_APP_FLOW_MONTH_OWNER_CREATED_AT", columnNames ={"owner","created_at"}),
		@Index(name = "IDX_AH_APP_FLOW_MONTH_OWNER", columnNames = {"OWNER"})
	    })

public class AhAppFlowMonth implements HmBo {
	private static final long serialVersionUID = 1L;	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	
	
	private int appCode;
	
	private long byteNum;
	
	private long packetNum;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;
	
	@Column(name="created_at")
	private long createdAt;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public int getAppCode() {
		return appCode;
	}

	public void setAppCode(int appCode) {
		this.appCode = appCode;
	}

	public long getByteNum() {
		return byteNum;
	}

	public void setByteNum(long byteNum) {
		this.byteNum = byteNum;
	}

	public long getPacketNum() {
		return packetNum;
	}

	public void setPacketNum(long packetNum) {
		this.packetNum = packetNum;
	}

	public HmDomain getOwner() {
		return owner;
	}

	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
		
	}

}