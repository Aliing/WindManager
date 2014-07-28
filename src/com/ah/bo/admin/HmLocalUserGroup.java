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
@Table(name = "user_localusergroup_new", uniqueConstraints = { @UniqueConstraint(columnNames = {"useremail", "localusergroup_id" })})
@org.hibernate.annotations.Table(appliesTo = "user_localusergroup_new", indexes = {
		@Index(name = "user_localusergroup_new_useremail", columnNames = { "useremail" })
		})
public class HmLocalUserGroup implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private Long localusergroup_id;

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

	public Long getLocalusergroup_id() {
		return localusergroup_id;
	}

	public void setLocalusergroup_id(Long localusergroup_id) {
		this.localusergroup_id = localusergroup_id;
	}

	public String getUseremail() {
		return useremail;
	}

	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}
}