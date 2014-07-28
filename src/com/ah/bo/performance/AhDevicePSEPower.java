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
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.MgrUtil;

@Entity
@Table(name="AH_DEVICE_PSE_POWER")
@org.hibernate.annotations.Table(appliesTo = "AH_DEVICE_PSE_POWER", indexes = {
		@Index(name = "DEVICE_PSE_POWER_OWNER", columnNames = { "OWNER" }),
		@Index(name = "DEVICE_PSE_POWER_OWNER_MAC", columnNames = { "OWNER", "MAC" })
		})
public class AhDevicePSEPower implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long		id;
	
	@Column(length = 12, nullable = false)
	private String mac;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	private float totalPower = 0.0f;
	
	private float powerUsed = 0.0f;
	
	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
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

	public float getTotalPower() {
		return totalPower;
	}

	public void setTotalPower(float totalPower) {
		this.totalPower = totalPower;
	}

	public float getPowerUsed() {
		return powerUsed;
	}

	public void setPowerUsed(float powerUsed) {
		this.powerUsed = powerUsed;
	}
	
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	
	@Transient
	public String getTotalPowerString() {
		return String.valueOf(((float)(Math.round(this.getTotalPower()*10))/10))
					+ " " + MgrUtil.getUserMessage("device.eth.pse.power.unit");
	}
	@Transient
	public String getPowerUsedString() {
		return String.valueOf(((float)(Math.round(this.getPowerUsed()*10))/10))
					+ " " + MgrUtil.getUserMessage("device.eth.pse.power.unit");
	}
	@Transient
	public String getRemainingPowerString() {
		return String.valueOf(((float)(Math.round((this.getTotalPower()-this.getPowerUsed())*10))/10))
					+ " " + MgrUtil.getUserMessage("device.eth.pse.power.unit");
	}
	
}
