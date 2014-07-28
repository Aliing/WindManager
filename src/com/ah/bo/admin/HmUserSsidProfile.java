package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;

/*
 * @author cchen
 */

@Entity
@Table(name = "user_ssidprofile_new", uniqueConstraints = { @UniqueConstraint(columnNames = {"useremail", "ssidprofile_id" })})
@org.hibernate.annotations.Table(appliesTo = "user_ssidprofile_new", indexes = {
		@Index(name = "user_ssidprofile_new_useremail", columnNames = { "useremail" })
		})
public class HmUserSsidProfile implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private Long ssidprofile_id;

	@Column(length = 128, nullable = false)
	private String useremail;

	@Version
	private Timestamp version;

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

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
	}

	public String getUseremail() {
		return useremail;
	}

	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}

	public Long getSsidprofile_id() {
		return ssidprofile_id;
	}

	public void setSsidprofile_id(Long ssidprofile_id) {
		this.ssidprofile_id = ssidprofile_id;
	}
}