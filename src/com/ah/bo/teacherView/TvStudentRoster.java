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
import java.util.HashSet;
import java.util.Set;

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
import com.ah.util.CheckItem;

/**
 * @author Fisher
 * @version V1.0.0.0
 */
@Entity
@Table(name = "TV_STUDENT_ROSTER")
@org.hibernate.annotations.Table(appliesTo = "TV_STUDENT_ROSTER", indexes = {
		@Index(name = "TV_STUDENT_ROSTER_OWNER", columnNames = { "OWNER" })
		})
public class TvStudentRoster implements HmBo {

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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CLASS_ID")
	private TvClass tvClass;

	@Column(length = 128, nullable = false)
	private String studentId;

	@Column(length = 128)
	private String studentName;
	
	@Column(length = 256)
	private String description;

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
		return studentName;
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
	public String getLabel() {
		return studentName;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof TvStudentRoster)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((TvStudentRoster) other).getId());
	}

	/**
	 * @return the tvClass
	 */
	public TvClass getTvClass() {
		return tvClass;
	}

	/**
	 * @param tvClass the tvClass to set
	 */
	public void setTvClass(TvClass tvClass) {
		this.tvClass = tvClass;
	}

	/**
	 * @return the studentName
	 */
	public String getStudentName() {
		return studentName;
	}

	/**
	 * @param studentName the studentName to set
	 */
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
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

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Transient
	private Set<CheckItem> allClasses = new HashSet<CheckItem>();

	public Set<CheckItem> getAllClasses() {
		return allClasses;
	}

	public void setAllClasses(Set<CheckItem> allClasses) {
		this.allClasses = allClasses;
	}

	/**
	 * getter of studentId
	 * @return the studentId
	 */
	public String getStudentId() {
		return studentId;
	}

	/**
	 * setter of studentId
	 * @param studentId the studentId to set
	 */
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	
}