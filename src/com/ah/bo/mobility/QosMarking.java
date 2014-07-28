package com.ah.bo.mobility;

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
@Table(name = "QOS_MARKING")
@org.hibernate.annotations.Table(appliesTo = "QOS_MARKING", indexes = {
		@Index(name = "QOS_MARKING_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class QosMarking implements HmBo {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String qosName;

	@Column(length = 32)
	private String prtclP;

	@Column(length = 32)
	private String prtclD;

	@Column(length = 32)
	private String prtclE;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "qosName", "prtclP", "prtclD",
				"description", "prtclE", "owner" };
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrtclD() {
		return prtclD;
	}

	public void setPrtclD(String prtclD) {
		this.prtclD = prtclD;
	}

	public String getPrtclP() {
		return prtclP;
	}

	public void setPrtclP(String prtclP) {
		this.prtclP = prtclP;
	}

	public String getQosName() {
		return qosName;
	}

	public void setQosName(String qosName) {
		this.qosName = qosName;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return this.qosName;
	}

	@Version
	private Timestamp version;

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

	@Override
	public Timestamp getVersion() {
		return this.version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Transient
	public String getChboxDValue() {
		if (prtclD != null && !prtclD.trim().equals(""))
			return "Enabled";
		return "Disabled";
	}

	@Transient
	public String getChboxPValue() {
		if (prtclP != null && !prtclP.trim().equals(""))
			return "Enabled";
		return "Disabled";
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getPrtclE() {
		return prtclE;
	}

	public void setPrtclE(String prtclE) {
		this.prtclE = prtclE;
	}

	@Override
	public QosMarking clone() {
		try {
			return (QosMarking) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}