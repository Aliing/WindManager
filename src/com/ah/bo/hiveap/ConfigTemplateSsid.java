package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
//import com.ah.bo.mobility.QosClassfierAndMarker;

import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;


@Embeddable
@SuppressWarnings("serial")
public class ConfigTemplateSsid implements Serializable {
	private String interfaceName;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SSID_PROFILE_ID")
	private SsidProfile ssidProfile;
	
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "QOS_CLASSFIER_AND_MARKER")
//	private QosClassfierAndMarker classfierAndMarker;
	
	private boolean ssidOnlyEnabled = false;
	
	private boolean networkServicesEnabled = false;

	private boolean macOuisEnabled = false;

	private boolean ssidEnabled = false;

	private boolean checkE;

	private boolean checkP;

	private boolean checkD;

	private boolean checkET;

	private boolean checkPT;

	private boolean checkDT;

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

	public String getInterfaceName() {
		return interfaceName;
	}
	
	public String getInterfaceNameSubstr() {
		if (interfaceName==null) {
			return "";
		}
		if (interfaceName.length()> BaseAction.DISPLAY_LENGTH_IN_GUI_OK) {
			return interfaceName.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI) + "...";
		}
		
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	
	public SsidProfile getSsidProfile() {
		return ssidProfile;
	}

	public void setSsidProfile(SsidProfile ssidProfile) {
		this.ssidProfile = ssidProfile;
	}

	public boolean getSsidOnlyEnabled() {
		return ssidOnlyEnabled;
	}

	public void setSsidOnlyEnabled(boolean ssidOnlyEnabled) {
		this.ssidOnlyEnabled = ssidOnlyEnabled;
	}
	
	public boolean getDisabledField(){
		if (ssidOnlyEnabled){
			return true;
		}
		return false;
	}

//	public QosClassfierAndMarker getClassfierAndMarker() {
//		return classfierAndMarker;
//	}
//
//	public void setClassfierAndMarker(QosClassfierAndMarker classfierAndMarker) {
//		this.classfierAndMarker = classfierAndMarker;
//	}
}
