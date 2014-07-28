package com.ah.bo.performance;

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

/**
 * @author zhang
 * @version 2010-3-10 15:27:03
 */

@Entity
@Table(name = "AH_EVENTS_FILTER")
@org.hibernate.annotations.Table(appliesTo = "AH_EVENTS_FILTER", indexes = {
		@Index(name = "EVENTS_FILTER_OWNER", columnNames = { "OWNER" })
		})
public class AhEventsFilter implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long		id;
	
	@Override
	public Long getId() {
		return this.id;
	}
	
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;
	
	@Override
	public HmDomain getOwner() {
		return this.owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp	version;

	@Override
	public Timestamp getVersion() {
		return this.version;
	}
	
	@Override
	public void setVersion(Timestamp version) {
		
	}
	
	@Transient
	private boolean	selected;

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	private String userName;
	
	private String apId;
	
	private String component;
	
	private Long startTime;
	
	private Long endTime;
	
	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String filterName;

	@Override
	public String getLabel() {
		return this.filterName;
	}
	
	public String getApId(){
		return this.apId;
	}
	
	public void setApId(String apId){
		this.apId = apId;
	}
	
	public String getComponent(){
		return this.component;
	}
	
	public void setComponent(String component){
		this.component = component;
	}
	
	@SuppressWarnings("unused")
	private Long getStartTime(){
		return this.startTime;
	}
	
	public void setStartTime(Long startTime){
		this.startTime = startTime;
	}
	
	public Long getEndTime(){
		return this.endTime;
	}
	
	public void setEndTime(Long endTime){
		this.endTime = endTime;
	}
	
	public String getFilterName(){
		return this.filterName;
	}
	
	public void setFilterName(String filterName){
		this.filterName = filterName;
	}
	
	public String getUserName(){
		return this.userName;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}

}