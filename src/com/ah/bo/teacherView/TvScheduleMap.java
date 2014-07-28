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

import javax.persistence.CollectionTable;
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

/**
 * @author Fisher
 * @version V1.0.0.0
 */
@Entity
@Table(name = "TV_SCHEDULE_MAP")
@org.hibernate.annotations.Table(appliesTo = "TV_SCHEDULE_MAP", indexes = {
		@Index(name = "TV_SCHEDULE_MAP_OWNER", columnNames = { "OWNER" })
		})
public class TvScheduleMap implements HmBo {

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

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "TV_SCHEDULE_PERIODTIME", joinColumns = @JoinColumn(name = "TV_SCHEDULE_MAP_ID", nullable = false))
	private List<TvScheduleMapPeriodTime> lstPeriod = new ArrayList<TvScheduleMapPeriodTime>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "TV_SCHEDULE_WEEKDAY", joinColumns = @JoinColumn(name = "TV_SCHEDULE_MAP_ID", nullable = false))
	private List<TvScheduleMapWeekDay> lstWeek = new ArrayList<TvScheduleMapWeekDay>();

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
		return "";
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
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return "Schedule Map";
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof TvScheduleMap)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((TvScheduleMap) other).getId());
	}

	/**
	 * @return the lstPeriod
	 */
	public List<TvScheduleMapPeriodTime> getLstPeriod() {
		return lstPeriod;
	}

	/**
	 * @param lstPeriod the lstPeriod to set
	 */
	public void setLstPeriod(List<TvScheduleMapPeriodTime> lstPeriod) {
		this.lstPeriod = lstPeriod;
	}

	/**
	 * @return the lstWeek
	 */
	public List<TvScheduleMapWeekDay> getLstWeek() {
		return lstWeek;
	}

	/**
	 * @param lstWeek the lstWeek to set
	 */
	public void setLstWeek(List<TvScheduleMapWeekDay> lstWeek) {
		this.lstWeek = lstWeek;
	}
	
}