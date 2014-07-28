package com.ah.bo.wlan;

import java.sql.Timestamp;

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

@Entity
@Table(name = "CWP_CERTIFICATE")
@org.hibernate.annotations.Table(appliesTo = "CWP_CERTIFICATE", indexes = {
		@Index(name = "CWP_CERTIFICATE_OWNER", columnNames = { "OWNER" })
		})
public class CwpCertificate implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long		id;

	private String		certName;

	private String		srcCertName;

	private String		srcKeyName;

	private boolean		encrypted;

	private String		description;

	private int			index;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}
	
	private boolean defaultFlag;

	public boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	@Version
	private Timestamp version;

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
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
	public String getLabel() {
		return this.certName;
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

	public String getCertName() {
		return certName;
	}

	public void setCertName(String certName) {
		this.certName = certName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getSrcCertName() {
		return srcCertName;
	}

	public void setSrcCertName(String srcCertName) {
		this.srcCertName = srcCertName;
	}

	public String getSrcKeyName() {
		return srcKeyName;
	}

	public void setSrcKeyName(String srcKeyName) {
		this.srcKeyName = srcKeyName;
	}

	/* real file name */
	public String getCpskName(){
		return this.certName + ".pem";
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}
	
	public String getEncryptedShow() {
		return encrypted ? "Yes" : "No";
	}

}