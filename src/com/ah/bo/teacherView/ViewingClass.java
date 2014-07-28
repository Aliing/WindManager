/**
 * @filename			ViewingClass.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5R1
 * 
 * Copyright (c) 2006-2010 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.bo.teacherView;

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
 * The Class is used to record classes which are currently being selected 
 * to view the student status.
 */
@Entity
@Table(name = "VIEWING_CLASS")
@org.hibernate.annotations.Table(appliesTo = "VIEWING_CLASS", indexes = {
		@Index(name = "VIEWING_CLASS_OWNER", columnNames = { "OWNER" })
		})
public class ViewingClass implements HmBo {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;

	/**
	 * ID or name of the selected class
	 */
	@Column(length = DEFAULT_STRING_LENGTH,nullable = false)
	private String className;
	
	/**
	 * IP Address of the designated HiveAP
	 */
	@Column(length = DEFAULT_STRING_LENGTH,nullable = false)
	private String apIpAddress;
	
	/**
	 * MAC Address of the designated HiveAP
	 */
	@Column(length = 12, nullable = false, unique = true)
	private String apMacAddress;
	
	/**
	 * Time when the class is selected to view. This time is 
	 * different from the beginning time of the class 
	 */
	private long selectedTime;
	
	/**
	 * <p>End time of the class, in millisecond</p>
	 * 
	 * 
	 */
	private long endTime;
	
	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#getVersion()
	 */
	@Override
	public Timestamp getVersion() {
		return version;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#isSelected()
	 */
	@Override
	public boolean isSelected() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean selected) {

	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#setVersion(java.sql.Timestamp)
	 */
	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#getLabel()
	 */
	@Override
	public String getLabel() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#getOwner()
	 */
	@Override
	public HmDomain getOwner() {
		return this.owner;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#setOwner(com.ah.bo.admin.HmDomain)
	 */
	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	/**
	 * getter of className
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * setter of className
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getApIpAddress() {
		return apIpAddress;
	}

	public void setApIpAddress(String apIpAddress) {
		this.apIpAddress = apIpAddress;
	}

	public String getApMacAddress() {
		return apMacAddress;
	}

	public void setApMacAddress(String apMacAddress) {
		this.apMacAddress = apMacAddress;
	}

	/**
	 * getter of selectedTime
	 * @return the selectedTime
	 */
	public long getSelectedTime() {
		return selectedTime;
	}

	/**
	 * setter of selectedTime
	 * @param selectedTime the selectedTime to set
	 */
	public void setSelectedTime(long selectedTime) {
		this.selectedTime = selectedTime;
	}

	/**
	 * getter of endTime
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * setter of endTime
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
}
