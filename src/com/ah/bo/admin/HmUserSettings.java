package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;

/*
 * @author cchen
 */

@Entity
@Table(name = "hm_user_settings")
@org.hibernate.annotations.Table(appliesTo = "hm_user_settings", indexes = {
		@Index(name = "hm_user_settings_useremail", columnNames = { "useremail" })
		})
public class HmUserSettings implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	// do not display no device message again
	private boolean dontShowMessageInDashboard = false;

	private boolean endUserLicAgree;

	// for VAD user
	private int maxAPNum = 0;

	@Column(name = "navCustomization",  nullable = false,columnDefinition="INT default 16")
	private int navCustomization=16;

	private boolean orderFolders;

	// default to popup
	private boolean promptChanges = true;

	private short syncResult = HmUser.SYNC_RESULT_OK;

	private short treeWidth = 220;

	@Column(length = 128, nullable = false, unique=true)
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

	public boolean isDontShowMessageInDashboard() {
		return dontShowMessageInDashboard;
	}

	public void setDontShowMessageInDashboard(boolean dontShowMessageInDashboard) {
		this.dontShowMessageInDashboard = dontShowMessageInDashboard;
	}

	public boolean isEndUserLicAgree() {
		return endUserLicAgree;
	}

	public void setEndUserLicAgree(boolean endUserLicAgree) {
		this.endUserLicAgree = endUserLicAgree;
	}

	public int getMaxAPNum() {
		return maxAPNum;
	}

	public void setMaxAPNum(int maxAPNum) {
		this.maxAPNum = maxAPNum;
	}

	public int getNavCustomization() {
		return navCustomization;
	}

	public void setNavCustomization(int navCustomization) {
		this.navCustomization = navCustomization;
	}

	public boolean isOrderFolders() {
		return orderFolders;
	}

	public void setOrderFolders(boolean orderFolders) {
		this.orderFolders = orderFolders;
	}

	public boolean isPromptChanges() {
		return promptChanges;
	}

	public void setPromptChanges(boolean promptChanges) {
		this.promptChanges = promptChanges;
	}

	public short getSyncResult() {
		return syncResult;
	}

	public void setSyncResult(short syncResult) {
		this.syncResult = syncResult;
	}

	public short getTreeWidth() {
		return treeWidth;
	}

	public void setTreeWidth(short treeWidth) {
		this.treeWidth = treeWidth;
	}
}