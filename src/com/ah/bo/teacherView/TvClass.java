/**
 *@filename		IpAddress.java
 *@version
 *@author		Fiona
 *@createtime	2007-8-29 PM 03:16:58
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.teacherView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fisher
 * @version V1.0.0.0
 */
@Entity
@Table(name = "TV_CLASS")
@org.hibernate.annotations.Table(appliesTo = "TV_CLASS", indexes = {
		@Index(name = "TV_CLASS_OWNER", columnNames = { "OWNER" })
		})
public class TvClass implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
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

	@Column(length = DEFAULT_STRING_LENGTH,nullable = false)
	private String className;
	
	@Column(length = 128)
	private String subject;
	
	//ID of teacher, which refers to the ID in HM user table
	/*
	 * fix bug 23956 in Geneva, make VHM  with IDM can create teacher. convert value of teacherId from user name to user email
	 * now value of teacherId is user email of teacher user
	 */
	private String teacherId;

	public static final int TV_ROSTER_TYPE_STUDENT=1;
	public static final int TV_ROSTER_TYPE_COMPUTERCART=2;
	public static final int TV_ROSTER_TYPE_MIXED=3;
	
	public static final int TV_MSG_VERSION_BASE=0;
	public static final int TV_MSG_VERSION_MIXEDTYPE=1;
		
	public static final int TV_STUNAME_TYPE_ID=1;
	public static final int TV_STUNAME_TYPE_MACADDRESS=2;
	private int rosterType=TV_ROSTER_TYPE_STUDENT;
	
	public static EnumItem[] TV_ENUM_ROSTER_TYPE = MgrUtil.enumItems(
			"enum.tv.class.rosterType.", new int[] { TV_ROSTER_TYPE_STUDENT,TV_ROSTER_TYPE_COMPUTERCART});
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CART_ID")
	private TvComputerCart computerCart;
	
	@Column(length = 256)
	private String description;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "TV_CLASS_SCHEDULE", joinColumns = @JoinColumn(name = "TV_CLASS_ID", nullable = false))
	private List<TvClassSchedule> items = new ArrayList<TvClassSchedule>();

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

	@Transient
	public String getValue() {
		return className;
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
	
	@Transient
	private Map<String, Map<String, String>> apStudents;
	
	/**
	 * getter of apStudents
	 * @return the apStudents
	 */
	public Map<String, Map<String, String>> getApStudents() {
		return apStudents;
	}

	/**
	 * setter of apStudents
	 * @param apStudents the apStudents to set
	 */
	public void setApStudents(Map<String, Map<String, String>> apStudents) {
		this.apStudents = apStudents;
	}

	@Override
	public String getLabel() {
		return className;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof TvClass)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((TvClass) other).getId());
	}


	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the teacherId
	 */
	public String getTeacherId() {
		return teacherId;
	}

	/**
	 * @param teacherId the teacherId to set
	 */
	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	/**
	 * @return the rosterType
	 */
	public int getRosterType() {
		return rosterType;
	}
	
	public String getRosterTypeString(){
		switch (rosterType) {
		case TV_ROSTER_TYPE_STUDENT:
		case TV_ROSTER_TYPE_COMPUTERCART:
			return MgrUtil.getEnumString("enum.tv.class.rosterType." + rosterType);
		default:
			return "INVALID";
		}
	}

	/**
	 * @param rosterType the rosterType to set
	 */
	public void setRosterType(int rosterType) {
		this.rosterType = rosterType;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public List<TvClassSchedule> getItems() {
		return items;
	}

	public void setItems(List<TvClassSchedule> items) {
		this.items = items;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	/**
	 * @return the computerCart
	 */
	public TvComputerCart getComputerCart() {
		return computerCart;
	}

	/**
	 * @param computerCart the computerCart to set
	 */
	public void setComputerCart(TvComputerCart computerCart) {
		this.computerCart = computerCart;
	}

}