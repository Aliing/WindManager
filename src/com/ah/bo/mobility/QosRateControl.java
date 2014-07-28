package com.ah.bo.mobility;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "QOS_RATE_CONTROL")
@org.hibernate.annotations.Table(appliesTo = "QOS_RATE_CONTROL", indexes = {
		@Index(name = "QOS_RATE_CONTROL_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class QosRateControl implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String qosName;

	@Range(min = 0, max = 54000)
	private int rateLimit = 54000;
	
	@Range(min = 0, max = 2000000)
	private int rateLimit11n = 1000000;
	
	@Range(min = 0, max = 2000000)
	private int rateLimit11ac = 1000000;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private boolean defaultFlag;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "QOS_RATE_CONTROL_RATE_LIMIT", joinColumns = @JoinColumn(name = "QOS_RATE_LIMIT_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<QosRateLimit> qosRateLimit = new ArrayList<QosRateLimit>();

	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "qosName", "rateLimit", "rateLimit11n", "defaultFlag", "description","owner", "rateLimit11ac" };
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return this.qosName;
	}

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

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQosName() {
		return qosName;
	}

	public void setQosName(String qosName) {
		this.qosName = qosName;
	}

	public int getRateLimit() {
		return rateLimit;
	}

	public void setRateLimit(int rateLimit) {
		this.rateLimit = rateLimit;
	}

	public List<QosRateLimit> getQosRateLimit() {
		return qosRateLimit;
	}

	public void setQosRateLimit(List<QosRateLimit> qosRateLimit) {
		this.qosRateLimit = qosRateLimit;
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public int getRateLimit11n()
	{
		return rateLimit11n;
	}

	public void setRateLimit11n(int rateLimit11n)
	{
		this.rateLimit11n = rateLimit11n;
	}
	
	public int getRateLimit11ac()
	{
		return rateLimit11ac;
	}

	public void setRateLimit11ac(int rateLimit11ac)
	{
		this.rateLimit11ac = rateLimit11ac;
	}
	
	@Override
	public QosRateControl clone() {
		try {
			return (QosRateControl) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}