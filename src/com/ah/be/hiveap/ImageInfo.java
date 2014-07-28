package com.ah.be.hiveap;

import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.bo.hiveap.DeviceInfo.DeviceOption;

/**
 * Information of image file like as below
 *
 * 0000050 $Type: AP350 $
 * 0000100 $Reversion: 6.1.2.0 $
 * 0000130 $DATE: Wed Aug  7 14:45:46 2013 $
 * 0000200 $Size: 23670287 $
 *
 * @author cchen
 *
 */
public class ImageInfo {

	private String type;

	private String targetName;

	private String reversion;

	private String date;

	private String size;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReversion() {
		return reversion;
	}

	public void setReversion(String reversion) {
		if(reversion == null){
			return;
		}
		
		String[] versions = reversion.split("\\.");
		if(versions != null && versions.length > 2){
			this.reversion = versions[0] + "." + versions[1] + "." + versions[2] + ".0";
		}
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getTargetName() {
		return targetName;
	}

	public String getTargetNameByTypeAndVersion() {
		while (true) {
			if (reversion == null || reversion.length() < 7) break;

			String[] versions = reversion.split("\\.");
			if (versions.length < 4) break;

			if (type == null) break;

			List<DeviceOption> targetNames = ImageManager.getHMTargetsByInternalName(type);
			for (DeviceOption deviceOption : targetNames) {
				if (NmsUtil.compareSoftwareVersion(deviceOption.getVersion(), reversion) <= 0) {
					targetName = deviceOption.getValue();
					break;
				}
			}

			break;
		}
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
}
