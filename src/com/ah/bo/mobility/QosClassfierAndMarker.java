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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "QOS_CLASSFIER_AND_MARKER")
@org.hibernate.annotations.Table(appliesTo = "QOS_CLASSFIER_AND_MARKER", indexes = {
		@Index(name = "QOS_CLASSFIER_AND_MARKER_OWNER", columnNames = { "OWNER" })
		})
public class QosClassfierAndMarker implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

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

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String qosName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private boolean networkServicesEnabled;

	private boolean macOuisEnabled;

	private boolean marksEnabled;

	private boolean ssidEnabled;

	// @Length(max = 20)
	private boolean checkE;

	// @Length(max = 20)
	private boolean checkP;

	// @Length(max = 20)
	private boolean checkD;
	
	// @Length(max = 20)
	private boolean checkET;

	// @Length(max = 20)
	private boolean checkPT;

	// @Length(max = 20)
	private boolean checkDT;

	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "qosName", "description",
				"networkServicesEnabled", "macOuisEnabled", "ssidEnabled",
				"marksEnabled", "checkD", "checkE", "checkP", "checkDT", "checkET", "checkPT", "owner"};
	}

	public String getQosName() {
		return qosName;
	}

	public void setQosName(String qosName) {
		this.qosName = qosName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getNetworkServicesEnabled() {
		return networkServicesEnabled;
	}

	public void setNetworkServicesEnabled(boolean networkServicesEnabled) {
		this.networkServicesEnabled = networkServicesEnabled;
	}

	public boolean getMacOuisEnabled() {
		return macOuisEnabled;
	}

	public void setMacOuisEnabled(boolean macOuisEnabled) {
		this.macOuisEnabled = macOuisEnabled;
	}

	public boolean getMarksEnabled() {
		return marksEnabled;
	}

	public void setMarksEnabled(boolean marksEnabled) {
		this.marksEnabled = marksEnabled;
	}

	public boolean getSsidEnabled() {
		return ssidEnabled;
	}

	public void setSsidEnabled(boolean ssidEnabled) {
		this.ssidEnabled = ssidEnabled;
	}

	public boolean getCheckE() {
		return checkE;
	}

	public void setCheckE(boolean checkE) {
		this.checkE = checkE;
	}

	public boolean getCheckP() {
		return checkP;
	}

	public void setCheckP(boolean checkP) {
		this.checkP = checkP;
	}

	public boolean getCheckD() {
		return checkD;
	}

	public void setCheckD(boolean checkD) {
		this.checkD = checkD;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return this.qosName;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Transient
	private boolean isSelected;

	@Override
	public boolean isSelected() {
		return isSelected;
	}

	@Transient
	public String getEnableNetworkServicesValue() {
		return networkServicesEnabled ? "Enabled" : "Disabled";
	}

	@Transient
	public String getEnableMacOuiValue() {
		return macOuisEnabled ? "Enabled" : "Disabled";
	}

	@Transient
	public String getSsidValue() {
		if (ssidEnabled) {
			return "Enabled";
		}
		return "Disabled";
	}

	@Transient
	public String getMarkValue() {
		if (marksEnabled)
			return "Enabled";
		return "Disabled";
	}

	// @Transient
	// public boolean getChboxD() {
	// if (prtclD != null && !prtclD.trim().equals(""))
	// return true;
	// return false;
	// }
	//
	// @Transient
	// public boolean getChboxP() {
	// if (prtclP != null && !prtclP.trim().equals(""))
	// return true;
	// return false;
	// }
	//
	// @Transient
	// public boolean getChboxE() {
	// if (prtclE != null && !prtclE.trim().equals(""))
	// return true;
	// return false;
	// }

	@Override
	public void setSelected(boolean selected) {
		this.isSelected = selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public boolean getCheckET() {
		return checkET;
	}

	public void setCheckET(boolean checkET) {
		this.checkET = checkET;
	}

	public boolean getCheckPT() {
		return checkPT;
	}

	public void setCheckPT(boolean checkPT) {
		this.checkPT = checkPT;
	}

	public boolean getCheckDT() {
		return checkDT;
	}

	public void setCheckDT(boolean checkDT) {
		this.checkDT = checkDT;
	}

}