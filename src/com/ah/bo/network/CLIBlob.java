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

@Entity
@Table(name = "CLI_BLOB")
@org.hibernate.annotations.Table(appliesTo = "CLI_BLOB", indexes = {
		@Index(name = "CLI_BLOB_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class CLIBlob implements HmBo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int MAX_CLI_CONTENT_LENGTH = 2048;
	
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;
	
	@Transient
	private boolean selected;
	
	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String supplementalName;
	
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	@Column(length = 2048, nullable = true)
	private String contentAera;
	
	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "supplementalName", "description",
				"contentAera","owner" };
	}

	public String getContentAera() {
		if(null != contentAera){
			contentAera = contentAera.trim();
		}
		return contentAera;
	}

	public void setContentAera(String contentAera) {
		if(null != contentAera){
			contentAera = contentAera.trim();
		}
		this.contentAera = contentAera;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public HmDomain getOwner() {
		return owner;
	}

	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public Timestamp getVersion() {
		return version;
	}

	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getLabel() {
		return supplementalName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSupplementalName() {
		return supplementalName;
	}

	public void setSupplementalName(String supplementalName) {
		this.supplementalName = supplementalName;
	}
	
	@Override
	public CLIBlob clone() {
		try {
			return (CLIBlob) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
