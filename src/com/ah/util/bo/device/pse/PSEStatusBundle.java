package com.ah.util.bo.device.pse;

import java.util.Comparator;
import java.util.List;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.performance.AhDevicePSEPower;
import com.ah.bo.performance.AhPSEStatus;
import com.ah.util.bo.device.DeviceProperties;

import edu.emory.mathcs.backport.java.util.Collections;

public class PSEStatusBundle {
	private HiveAp hiveAp;
	
	public PSEStatusBundle(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
		preparePSEStatus();
	}
	
	private void preparePSEStatus() {
		if (this.hiveAp == null) {
			return;
		}
		
		DeviceProperties dProperties = new DeviceProperties(hiveAp);
		this.blnPSESupport = dProperties.isPSEPortSupport();
		
		if(!blnPSESupport) {
			return;
		}
		
		if (hiveAp.getDeviceInfo().isDeviceModelInitSwitch()) {
			// for Switch, both Router and Switch mode
			this.pseStatusLst = PSEStatusUtil.getAllExistedPSEInfoFromDb(hiveAp);
		} else {
			// other device, like normal BR
			this.pseStatusLst = PSEStatusUtil.getAllSupportedPSEPorts(this.hiveAp, dProperties);
			Collections.sort(this.pseStatusLst, new Comparator<AhPSEStatus>(){
				@Override
				public int compare(AhPSEStatus pse1, AhPSEStatus pse2) {
					return pse1.getInterfType() - pse2.getInterfType();
				}
			});
		}
		
		AhDevicePSEPower devicePSEPower = PSEStatusUtil.getAhDevicePSEPower(hiveAp);
		if (devicePSEPower != null) {
			this.totalPower = devicePSEPower.getTotalPowerString();
			this.powerUsed = devicePSEPower.getPowerUsedString();
			this.remainingPower = devicePSEPower.getRemainingPowerString();
		}
	}
	
	private List<AhPSEStatus> pseStatusLst;
	
	private String totalPower;
	
	private String powerUsed;
	
	private String remainingPower;
	
	private boolean blnPSESupport;

	public HiveAp getHiveAp() {
		return hiveAp;
	}

	public void setHiveAp(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
	}

	public List<AhPSEStatus> getPseStatusLst() {
		return pseStatusLst;
	}

	public void setPseStatusLst(List<AhPSEStatus> pseStatusLst) {
		this.pseStatusLst = pseStatusLst;
	}

	public String getTotalPower() {
		return totalPower;
	}

	public void setTotalPower(String totalPower) {
		this.totalPower = totalPower;
	}

	public String getPowerUsed() {
		return powerUsed;
	}

	public void setPowerUsed(String powerUsed) {
		this.powerUsed = powerUsed;
	}

	public String getRemainingPower() {
		return remainingPower;
	}

	public void setRemainingPower(String remainingPower) {
		this.remainingPower = remainingPower;
	}

	public boolean isBlnPSESupport() {
		return blnPSESupport;
	}

	public void setBlnPSESupport(boolean blnPSESupport) {
		this.blnPSESupport = blnPSESupport;
	}
}
