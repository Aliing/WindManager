package com.ah.bo.monitor;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "hm_cpu_memory_usage")
@org.hibernate.annotations.Table(appliesTo = "hm_cpu_memory_usage", indexes = {
		@Index(name = "hm_cpu_memory_usage_owner", columnNames = { "OWNER" })
		})
public class CpuMemoryUsage  implements HmBo {
	private static final long serialVersionUID = 8583911018332891334L;

	@Id
	@GeneratedValue
	private Long  id;
	
	@Column( nullable=false)
	private long timeStamp ;
	
	@Column(nullable = false,columnDefinition = "Numeric(5,2)")
	private float  cpuUsage ;
	
	@Column(nullable = false,columnDefinition = "Numeric(5,2)")
	private float  memUsage ;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public float getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(float cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public float getMemUsage() {
		return memUsage;
	}

	public void setMemUsage(float memUsage) {
		this.memUsage = memUsage;
	}

	@Override
	public String getLabel() {
		return null;
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
	
	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
}
