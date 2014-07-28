package com.ah.bo.network;

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
@Table(name = "MSTP_REGION")
@org.hibernate.annotations.Table(appliesTo = "MSTP_REGION", indexes = {
		@Index(name = "MSTP_REGION_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class MstpRegion implements HmBo {

	private static final long serialVersionUID = 1L;
	
	public MstpRegion(){
		
	}

	@Id
	@GeneratedValue
	private Long id;
	
	public static final int MIN_REVISION = 0;
	public static final int MAX_REVISION = 65535;
	
	public static final int MIN_HOPS = 1;
	public static final int MAX_HOPS = 40;
	public static final int DEFAULT_REVISION = 0;
	public static final int DEFAULT_HOPS = 20;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String regionName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	@Range(min = MIN_REVISION, max = MAX_REVISION)
	private int revision = DEFAULT_REVISION;
	
	@Range(min = MIN_HOPS, max = MAX_HOPS)
	private int hops = DEFAULT_HOPS;
	
	@ElementCollection(fetch = FetchType.LAZY)
    @OrderColumn(name = "POSITION")
    @CollectionTable(name = "MSTP_REGION_PRIORITY", joinColumns = @JoinColumn(name = "MSTP_REGION_ID", nullable = false))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<MstpRegionPriority> mstpRegionPriorityList = new ArrayList<MstpRegionPriority>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Version
	private Timestamp version;
	
	@Transient
	private boolean selected;
	
	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "regionName", "description", "revision",
				"hops", "owner"};
	}

	@Override
	public MstpRegion clone() {
		try {
			return (MstpRegion) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getLabel() {
		return this.regionName;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}
	
	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}
	
	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}
	
	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public int getHops() {
		return hops;
	}

	public void setHops(int hops) {
		this.hops = hops;
	}
	
	public List<MstpRegionPriority> getMstpRegionPriorityList() {
		return mstpRegionPriorityList;
	}

	public void setMstpRegionPriorityList(
			List<MstpRegionPriority> mstpRegionPriorityList) {
		this.mstpRegionPriorityList = mstpRegionPriorityList;
	}
}