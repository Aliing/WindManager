package com.ah.be.db.configuration;

import java.util.Date;

import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.bo.HmBo;

public class ConfigurationChangedEvent extends BeBaseEvent {

	private static final long serialVersionUID = 1L;

	private HmBo bo;

	private HmBo oldBo;

	private Date oldVer;

	private Operation operation;
	
	private boolean expressMode=false;

	public enum Operation {
		CREATE, UPDATE, REMOVE, REVOKE
	}

	public ConfigurationChangedEvent(HmBo bo, Operation operation, Date oldVer) {
		super.setEventType(BeEventConst.AH_CONFIGURATION_CHANGE_EVENT);
		this.bo = bo;
		this.operation = operation;
		this.oldVer = oldVer;
	}
	
	public ConfigurationChangedEvent(HmBo bo, Operation operation, Date oldVer, boolean exMode) {
		super.setEventType(BeEventConst.AH_CONFIGURATION_CHANGE_EVENT);
		this.bo = bo;
		this.operation = operation;
		this.oldVer = oldVer;
		this.expressMode=exMode;
	}

	public ConfigurationChangedEvent(HmBo oldBo, HmBo bo, Operation operation) {
		super.setEventType(BeEventConst.AH_CONFIGURATION_CHANGE_EVENT);
		this.bo = bo;
		this.operation = operation;
		this.oldBo = oldBo;
		if (null != oldBo) {
			this.oldVer = oldBo.getVersion();
		}
	}

	public HmBo getBo() {
		return bo;
	}

	public Operation getOperation() {
		return operation;
	}

	public boolean isConfigurationChanged() {
		// just check while value existed, for non-value, treat it as changed
		// every time
		if (null != oldVer && null != bo && null != bo.getVersion()) {
			if (oldVer.getTime() == bo.getVersion().getTime()) {
				return false;
			}
		}
		return true;
	}

	public Date getOldVer() {
		return oldVer;
	}

	public HmBo getOldBo() {
		return oldBo;
	}

	public void setOldBo(HmBo oldBo) {
		this.oldBo = oldBo;
	}

	public boolean isExpressMode() {
		return expressMode;
	}

	public void setExpressMode(boolean expressMode) {
		this.expressMode = expressMode;
	}

}
