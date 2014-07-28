package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;

@Entity
@Table(name = "HmClassifierTag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HmClassifierTag implements HmBo{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_LENGTH = 64;
	public static final int[] tagTypeList = { 1, 2, 3 };
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	@Id
	@GeneratedValue
	private Long id;

	@Column(length = DEFAULT_LENGTH)
	private String tagValue;

	@Range(min = 1, max = 100)
	private int tagType;

	public HmDomain getOwner() {
		return owner;
	}

	public void setOwner(HmDomain owner) {
		this.owner = owner;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;

	}

	public String getTagValue() {
		return tagValue;
	}

	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}

	public int getTagType() {
		return tagType;
	}

	public void setTagType(int tagType) {
		this.tagType = tagType;
	}

	@Override
	public String getLabel() {		
		return null;
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
}
