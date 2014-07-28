package com.ah.be.config.hiveap;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.event.BeCapwapDTLSConfigEvent;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApUpdateResult;

public class UpdateHiveAp {

	private HiveAp hiveAp;

	private List<UpdateObject> updateObjectList;

	private short updateType;

	private boolean isAutoProvision = false;// false by default
	
	private boolean byStaged = false;
	
	private int stagedTime = HiveApUpdateResult.DEFAULT_STAGED_TIME;

	private boolean withReboot;

	private int transactionCode; // transaction id for update used

	private BeCapwapDTLSConfigEvent dtlsEvent;

	public HiveAp getHiveAp() {
		return hiveAp;
	}

	public void setHiveAp(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
	}

	public List<UpdateObject> getUpdateObjectList() {
		return updateObjectList;
	}

	public void setUpdateObjectList(List<UpdateObject> updateObjectList) {
		this.updateObjectList = updateObjectList;
	}

	public void addUpdateObject(UpdateObject updateObject) {
		if (null == updateObjectList) {
			updateObjectList = new ArrayList<UpdateObject>();
		}
		updateObjectList.add(updateObject);
	}

	public int getRemainUpdateObjectCount() {
		if (null == updateObjectList) {
			return 0;
		}
		return updateObjectList.size();
	}

	public void removeFirstUpdateObject() {
		if (null != updateObjectList && !updateObjectList.isEmpty()) {
			updateObjectList.remove(0);
		}
	}

	public UpdateObject getNextUpdateObject() {
		if (null != updateObjectList && !updateObjectList.isEmpty()) {
			return updateObjectList.get(0);
		}
		return null;
	}

	public UpdateObject getUpdateObject(short updateType) {
		if (null != updateObjectList && !updateObjectList.isEmpty()) {
			for (UpdateObject object : updateObjectList) {
				if (object.getUpdateType() == updateType) {
					return object;
				}
			}
		}
		return null;
	}

	public UpdateObject getUpdateObjectForCancel(int serialNum) {
		if (null != updateObjectList && !updateObjectList.isEmpty()) {
			for (UpdateObject object : updateObjectList) {
				if (object.getCancelSerial() == serialNum) {
					return object;
				}
			}
		}
		return null;
	}

	public void removeUpdateObject(UpdateObject object) {
		if (null != updateObjectList && !updateObjectList.isEmpty()
				&& null != object) {
			updateObjectList.remove(object);
		}
	}

	public short getUpdateType() {
		return updateType;
	}

	public void setUpdateType(short updateType) {
		this.updateType = updateType;
	}

	public boolean isAutoProvision() {
		return isAutoProvision;
	}

	public void setAutoProvision(boolean isAutoProvision) {
		this.isAutoProvision = isAutoProvision;
	}

	public boolean isWithReboot() {
		return withReboot;
	}

	public void setWithReboot(boolean withReboot) {
		this.withReboot = withReboot;
	}

	public int getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(int transactionCode) {
		this.transactionCode = transactionCode;
	}
	public boolean isLastItem(UpdateObject item) {
		if (null != updateObjectList && !updateObjectList.isEmpty()) {
			UpdateObject lastItem = updateObjectList.get(updateObjectList
					.size() - 1);
			return lastItem.getUpdateType() == item.getUpdateType();
		}
		return true;
	}

	public BeCapwapDTLSConfigEvent getDtlsEvent() {
		return dtlsEvent;
	}

	public void setDtlsEvent(BeCapwapDTLSConfigEvent dtlsEvent) {
		this.dtlsEvent = dtlsEvent;
	}
	
	public boolean isByStaged() {
		return byStaged;
	}

	public void setByStaged(boolean byStaged) {
		if(!this.byStaged){
			this.byStaged = byStaged;
		}
	}
	
	public int getStagedTime() {
		return stagedTime;
	}

	public void setStagedTime(int stagedTime) {
		this.stagedTime = stagedTime;
	}
}
