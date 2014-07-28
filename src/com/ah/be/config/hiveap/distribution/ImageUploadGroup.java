package com.ah.be.config.hiveap.distribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.cli.AhCliFactory;
import com.ah.be.config.hiveap.UpdateHiveAp;
import com.ah.be.config.hiveap.UpdateObject;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApUpdateSettings;
import com.ah.bo.hiveap.HiveApUpdateSettings.ActivateType;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.DeviceDaInfo;

public class ImageUploadGroup {

	public static enum ResultType {
		NO_PORTAL, UPLOAD_REQ_FAIL, UPLOAD_RESP_SUC, UPLOAD_RESP_FAIL, UPLOAD_RESL_SUC, UPLOAD_RESL_FAIL, SAVE_REQ_FAIL, SAVE_RESP_SUC, SAVE_RESP_FAIL, PROGRESS, SAVE_RESL_SUC, SAVE_RESL_FAIL, TIMED_OUT, CANCELLED, CANCEL_FAIL
	}

	public static enum StatusType {
		UPLOADING_IMAGE, SAVING_IMAGE
	}

	public static final float RATE_UPLOAD_RESPONSED = 0.2f;

	public static final float RATE_UPLOAD_RESULTED = 0.5f;

	public static final float RATE_SAVE_RESPONSED = 0.55f;

	public static final float RATE_SAVE_FINISHED = 1.0f;

	public static final int HIVE_TIME_OUT = 8 * 60; // seconds

	private String imageName;
	
	private boolean byVersion;

	private String softVer;

	private int portalSequenceNum;

	private HiveApUpdateSettings settings;

	private List<HiveAp> portals;

	private List<UpdateHiveAp> updateList;

	private int portalIndex = 0;

	private StatusType status;

	private int finishedSize;

	private long timeCount;

	public ImageUploadGroup(HiveApUpdateSettings settings) {
		this.settings = settings;
	}

	public boolean isStartSaveImage() {
		return StatusType.SAVING_IMAGE.equals(this.status);
	}

	public boolean isStartUploadImage() {
		return StatusType.UPLOADING_IMAGE.equals(this.status);
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}

	public int getFinishedSize() {
		return finishedSize;
	}

	public void setFinishedSize(int finishedSize) {
		this.finishedSize = finishedSize;
	}

	public long getTimeCount() {
		return timeCount;
	}

	public void setTimeCount(long timeCount) {
		this.timeCount = timeCount;
	}

	public List<HiveAp> getPortals() {
		if(portals == null){
			portals = new ArrayList<HiveAp>();
		}
		return portals;
	}

	public void setPortals(List<HiveAp> portals) {
		this.portals = portals;
	}

	public List<UpdateHiveAp> getUpdateList() {
		if(updateList == null){
			updateList = new ArrayList<UpdateHiveAp>();
		}
		return updateList;
	}

	public void addUpdateHiveAp(UpdateHiveAp updateHiveAp) {
		if (null == updateList) {
			updateList = new ArrayList<UpdateHiveAp>();
		}
		updateList.add(updateHiveAp);
	}

	public UpdateHiveAp getUpdateHiveAp(String macAddress) {
		if (null != updateList) {
			for (UpdateHiveAp upAp : updateList) {
				if (upAp.getHiveAp().getMacAddress().equals(macAddress)) {
					return upAp;
				}
			}
		}
		return null;
	}

	public void removeUpdateHiveAp(String macAddress) {
		if (null != updateList) {
			for (UpdateHiveAp upAp : updateList) {
				if (upAp.getHiveAp().getMacAddress().equals(macAddress)) {
					updateList.remove(upAp);
					break;
				}
			}
		}
	}

	// public void clearCancelRequestNumber(UpdateHiveAp upAp) {
	// UpdateObject object = upAp
	// .getUpdateObject(UpdateParameters.AH_DOWNLOAD_IMAGE);
	// object.setCancelSerial(0);
	// object.setCanceling(false);
	// }

	public List<UpdateHiveAp> getExecSucUpdateHiveAps() {
		List<UpdateHiveAp> list = new ArrayList<UpdateHiveAp>();
		if (null != updateList) {
			for (UpdateHiveAp upAp : updateList) {
				UpdateObject object = upAp
						.getUpdateObject(UpdateParameters.AH_DOWNLOAD_IMAGE);
				if (object.getResult() == UpdateParameters.UPDATE_SUCCESSFUL) {
					list.add(upAp);
				}
			}
		}
		return list;
	}

	public List<UpdateHiveAp> getExecFailUpdateHiveAps() {
		List<UpdateHiveAp> list = new ArrayList<UpdateHiveAp>();
		if (null != updateList) {
			for (UpdateHiveAp upAp : updateList) {
				UpdateObject object = upAp
						.getUpdateObject(UpdateParameters.AH_DOWNLOAD_IMAGE);
				if (object.getResult() == UpdateParameters.UPDATE_FAILED) {
					list.add(upAp);
				}
			}
		}
		return list;
	}

	public List<UpdateHiveAp> getExecCancelUpdateHiveAps() {
		List<UpdateHiveAp> list = new ArrayList<UpdateHiveAp>();
		if (null != updateList) {
			for (UpdateHiveAp upAp : updateList) {
				UpdateObject object = upAp
						.getUpdateObject(UpdateParameters.AH_DOWNLOAD_IMAGE);
				if (object.getResult() == UpdateParameters.UPDATE_CANCELED) {
					list.add(upAp);
				}
			}
		}
		return list;
	}

	public String getRebootCli() {
		if (null != settings) {
			boolean isRelative = ActivateType.activateAfterTime.equals(settings
					.getImageActivateType());
			String rebootTime = settings.getImageActivateTimeString();
			if (null != rebootTime) {
				if (isRelative) {
					return AhCliFactory.getRebootCli(rebootTime);
				} else {
					String[] time_date = rebootTime.split(" ");
					return AhCliFactory
							.getRebootCli(time_date[1], time_date[0]);
				}
			}
		}
		return null;
	}

	public long getMaxTimeoutInSecond() {
		return null == settings ? 10 * 60 : settings.getImageTimedout() * 60;
	}

	public HiveAp getCurrentPortal() {
		if (portalIndex > portals.size() - 1) {
			return null;
		}
		return portals.get(portalIndex);
	}

	public HiveAp nextPortal() {
		portalIndex++;
		return getCurrentPortal();
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public HiveApUpdateSettings getSettings() {
		return settings;
	}

	public void setSettings(HiveApUpdateSettings settings) {
		this.settings = settings;
	}

	public int getPortalSequenceNum() {
		return portalSequenceNum;
	}

	public void setPortalSequenceNum(int portalSequenceNum) {
		this.portalSequenceNum = portalSequenceNum;
	}
	
	public boolean isByVersion() {
		return byVersion;
	}

	public void setByVersion(boolean byVersion) {
		this.byVersion = byVersion;
	}

	public String getSoftVer() {
		return softVer;
	}

	public void setSoftVer(String softVer) {
		this.softVer = softVer;
	}

	public Map<String, UpdateHiveAp> getCancelUpdateHiveAps(
			int cancelSequenceNum) {
		Map<String, UpdateHiveAp> map = new HashMap<String, UpdateHiveAp>();
		if (null != updateList) {
			for (UpdateHiveAp upAp : updateList) {
				UpdateObject object = upAp
						.getUpdateObject(UpdateParameters.AH_DOWNLOAD_IMAGE);
				if (object.getCancelSerial() == cancelSequenceNum) {
					map.put(upAp.getHiveAp().getMacAddress(), upAp);
				}
			}
		}
		return map;
	}

	public void setCancelSequenceNum(int cancelSequenceNum, List<HiveAp> hiveAps) {
		for (HiveAp hiveAp : hiveAps) {
			UpdateHiveAp upAp = getUpdateHiveAp(hiveAp.getMacAddress());
			if (null != upAp) {
				UpdateObject object = upAp
						.getUpdateObject(UpdateParameters.AH_DOWNLOAD_IMAGE);
				object.setCancelSerial(cancelSequenceNum);
				object.setCanceling(true);
			}
		}
	}
	
	public String getKey(){
		HiveAp hiveAp = null;
		if(portals != null || !portals.isEmpty()){
			hiveAp = portals.get(0);
		}
		if(hiveAp == null && updateList != null && !updateList.isEmpty()){
			hiveAp = updateList.get(0).getHiveAp();
		}
		
		if(hiveAp != null){
			return ImageDistributor.getGroupKey(hiveAp);
		}else{
			return null;
		}
	}
	
	public void countDistributedServer(){
		if(updateList == null){
			return;
		}
		
		Map<String, HiveAp> apMap = new HashMap<String, HiveAp>();
		for(UpdateHiveAp upAp : updateList){
			HiveAp hiveAp = upAp.getHiveAp();
			if(hiveAp != null){
				apMap.put(hiveAp.getMacAddress(), hiveAp);
			}
		}
		
		List<DeviceDaInfo> daList = QueryUtil.executeQuery(
				DeviceDaInfo.class, null, new FilterParams("macAddress", apMap.keySet()));
		for(DeviceDaInfo daInfo : daList){
			daInfo.setHiveAp(apMap.get(daInfo.getMacAddress()));
			
			if(daInfo.isDA()){
				daInfo.setPriority(daInfo.getPriority() + "1");
			}else{
				daInfo.setPriority(daInfo.getPriority() + "0");
			}
			
			if(daInfo.isBDA()){
				daInfo.setPriority(daInfo.getPriority() + "1");
			}else{
				daInfo.setPriority(daInfo.getPriority() + "0");
			}
			
			if(daInfo.isPortal()){
				daInfo.setPriority(daInfo.getPriority() + "1");
			}else{
				daInfo.setPriority(daInfo.getPriority() + "0");
			}
		}
		
		Collections.sort(daList, new Comparator<DeviceDaInfo>(){
			@Override
			public int compare(DeviceDaInfo o1, DeviceDaInfo o2) {
				return o2.getPriority().compareTo(o1.getPriority());
			}
		});
		
		getPortals().clear();
		for(DeviceDaInfo daInfo : daList){
			portals.add(daInfo.getHiveAp());
		}
	}

}
