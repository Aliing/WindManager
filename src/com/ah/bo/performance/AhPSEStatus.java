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
import com.ah.bo.hiveap.AhInterface;
import com.ah.util.MgrUtil;

@Entity
@Table(name="AH_PSE_STATUS")
@org.hibernate.annotations.Table(appliesTo = "AH_PSE_STATUS", indexes = {
		@Index(name = "PSE_STATUS_OWNER", columnNames = { "OWNER" }),
		@Index(name = "PSE_STATUS_OWNER_MAC_NAME", columnNames = { "OWNER", "MAC", "INTERFNAME" })
		})
public class AhPSEStatus implements HmBo {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long		id;
	
	@Column(length = 12, nullable = false)
	private String mac;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	private byte status = AhInterface.PSE_STATUS_DISABLED;
	
	private float power = 0.0f;
	
	private byte pdType = AhInterface.PSE_PDTYPE_NONE;
	
	private byte pdClass;
	
	private byte powerCutoffPriority = -1;
	
	@Column(length = 32)
	private String interfName;
	
	@Override
	public HmDomain getOwner() {
		return this.owner;
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

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public float getPower() {
		return power;
	}

	public void setPower(float power) {
		this.power = power;
	}

	public byte getPdType() {
		return pdType;
	}

	public void setPdType(byte pdType) {
		this.pdType = pdType;
	}

	public byte getPdClass() {
		return pdClass;
	}

	public void setPdClass(byte pdClass) {
		this.pdClass = pdClass;
	}

	public String getInterfName() {
		return interfName;
	}

	public void setInterfName(String interfName) {
		this.interfName = interfName;
	}
	
	@Transient
	public String getStatusString() {
		return MgrUtil.getEnumString("enum.device.eth.pse.status."+this.getStatus());
	}
	
	@Transient
	public String getPdTypeString() {
/*		if (this.getStatus() == AhInterface.PSE_STATUS_DISABLED) {
			return "";
		}
		return MgrUtil.getEnumString("enum.device.eth.pse.pdtype."+this.getPdType());*/
		
		// fix bug 27996
		if (this.getStatus() == AhInterface.PSE_STATUS_DELIVERING) {
			// only status is delivering, show PD type sent up from HiveOS
			return MgrUtil.getEnumString("enum.device.eth.pse.pdtype."+this.getPdType());
		} else if (this.getStatus() == AhInterface.PSE_STATUS_FAULT
				|| this.getStatus() == AhInterface.PSE_STATUS_OTHER_FAULT) {
			// if status is fault, show Invalid for PD type
			return MgrUtil.getEnumString("enum.device.eth.pse.pdtype."+ AhInterface.PSE_PDTYPE_INVALID);
		} else {
			// other status, show None
			return MgrUtil.getEnumString("enum.device.eth.pse.pdtype."+ AhInterface.PSE_PDTYPE_NONE);
		}
	}
	
	@Transient
	public String getPdClassString() {
		if (this.getStatus() == AhInterface.PSE_STATUS_DISABLED) {
			return "";
		}
		/*if (this.pdClass < 0
				|| this.pdClass > 7) {
			return "";
		}*/
		return MgrUtil.getEnumString("enum.monitor.device.pse.pdclass."+this.getPdClass());
	}
	
	@Transient
	public String getPowerString() {
		if (this.getStatus() == AhInterface.PSE_STATUS_DISABLED) {
			return "";
		}
		return String.valueOf(((float)(Math.round(this.getPower()*10))/10))
					+ " " + MgrUtil.getUserMessage("device.eth.pse.power.unit");
	}

	@Transient
	public String getPowerCutoffPriorityString() {
		if (this.getStatus() == AhInterface.PSE_STATUS_DISABLED) {
			return "";
		}
		return MgrUtil.getEnumString("enum.pse.priority.sw.monotor."+this.getPowerCutoffPriority());
	}
	
	@Transient
	private short interfType;

	public short getInterfType() {
		return interfType;
	}

	public void setInterfType(short interfType) {
		this.interfType = interfType;
	}

	public byte getPowerCutoffPriority() {
		return powerCutoffPriority;
	}

	public void setPowerCutoffPriority(byte powerCutoffPriority) {
		this.powerCutoffPriority = powerCutoffPriority;
	}
}
