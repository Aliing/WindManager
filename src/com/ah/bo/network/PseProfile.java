/**
 *@filename		PseProfile.java
 *@version
 *@author		LiangWenping
 *@createtime	2012-11-12 PM 01:35:37
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.network;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.AhInterface;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		LiangWenping
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "PSE_PROFILE")
@org.hibernate.annotations.Table(appliesTo = "PSE_PROFILE", indexes = {
		@Index(name = "PSE_PROFILE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class PseProfile implements HmBo {
	
	private static final long	serialVersionUID	= 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String name;
	
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	private boolean defaultFlag;
	
	public static final short PRIORITY_LOW = 0;
	public static final short PRIORITY_MEDIUM = 1;
	public static final short PRIORITY_HIGH = 2;
	public static final short PRIORITY_CRITICAL = 3;
	public static EnumItem[] ENUM_PRIORITY = MgrUtil.enumItems("enum.pse.priority.",
			new int[] { PRIORITY_LOW,PRIORITY_HIGH,PRIORITY_CRITICAL});
	
	public static EnumItem[] ENUM_POWER_MODE = MgrUtil.enumItems("enum.device.eth.pse.pdtype.",
			new int[] { AhInterface.PSE_PDTYPE_8023AF, AhInterface.PSE_PDTYPE_8023AT});

	public static final int THRESHOLD_POWER_AF = 15400;
	public static final int THRESHOLD_POWER_AT=  32000;
	
	public static final int THRESHOLD_POWER_AF_RANGE = 16000;
	public static final int THRESHOLD_POWER_AT_RANGE=  32000;
	
	private short powerMode = AhInterface.PSE_PDTYPE_8023AT;
	private short priority = PRIORITY_LOW;
	private int thresholdPower = THRESHOLD_POWER_AT;
	
	@Transient
	private String thresholdPowerRange;
	
	public String getThresholdPowerRange() {
		return thresholdPowerRange;
	}

	public void setThresholdPowerRange(String thresholdPowerRange) {
		this.thresholdPowerRange = thresholdPowerRange;
	}
	
	public String getPowerModeDisplay(){
		return MgrUtil.getEnumString("enum.device.eth.pse.pdtype."+powerMode);
	}
	
	public String getPriorityDisplay(){
		return MgrUtil.getEnumString("enum.pse.priority."+priority);
	}

	/************** Getter/Setter***************/
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public short getPowerMode() {
		return powerMode;
	}

	public short getPriority() {
		return priority;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPowerMode(short powerMode) {
		this.powerMode = powerMode;
	}

	public void setPriority(short priority) {
		this.priority = priority;
	}

	public int getThresholdPower() {
		return thresholdPower;
	}

	public void setThresholdPower(int thresholdPower) {
		this.thresholdPower = thresholdPower;
	}

	/**************** Override Method *****************/
	@Override
	public Long getId() {
		return this.id;
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
	public String getLabel() {
		return name;
	}

	@Transient
	private boolean selected;

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
		this.version = version;
	}

	@Override
	public PseProfile clone() {
		try {
			return (PseProfile) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}
}