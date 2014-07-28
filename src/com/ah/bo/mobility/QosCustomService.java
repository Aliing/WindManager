package com.ah.bo.mobility;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.MacFilter;
import com.ah.util.EnumConstUtil;
import com.ah.util.MgrUtil;

/**
 * 
 * Description: L7 Custom application service for qos
 * QosCustomService.java Create on Aug 2, 2013 2:32:36 AM
 * @author Shaohua Zhou
 * @version 1.0
 * Copyright (c) 2013 Aerohive Networks Inc. All Rights Reserved.
 */
@Embeddable
@SuppressWarnings("serial")
public class QosCustomService implements Serializable {

	private short qosClass;

	private short filterAction;

	private short logging;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CUSTOMAPPSERVICE_ID", nullable = true)
	private CustomApplication customAppService;
	
	@Transient
	private boolean networkServiceDisabled;

	@Transient
	public String[] getFieldValues() {
		String[] fieldValues = { "QOS_CLASSIFICATION_ID", "CUSTOMAPPSERVICE_ID",
				"qosClass", "filterAction", "logging" };
		return fieldValues;
	}

	public short getFilterAction() {
		return filterAction;
	}

	public void setFilterAction(short filterAction) {
		this.filterAction = filterAction;
	}

	public String getFilterActionString() {
		return MacFilter.getFilterActionString(filterAction);
	}

	public boolean isEnableLogging() {
		return logging == EnumConstUtil.ENABLE;
	}

	public short getLogging() {
		return logging;
	}

	public void setLogging(short logging) {
		this.logging = logging;
	}

	public String getLoggingString() {
		String loggingString = "Unknown";
		switch (logging) {
		case EnumConstUtil.ENABLE:
		case EnumConstUtil.DISABLE:
			loggingString = MgrUtil.getEnumString("enum.logging." + logging);
			break;
		default:
		}
		return loggingString;
	}

	public short getQosClass() {
		return qosClass;
	}

	public void setQosClass(short qosClass) {
		this.qosClass = qosClass;
	}

	public String getQosClassString() {
		String qosClassString = "Unknown";
		switch (qosClass) {
		case EnumConstUtil.QOS_CLASS_NETWORK_CONTROL:
		case EnumConstUtil.QOS_CLASS_VOICE:
		case EnumConstUtil.QOS_CLASS_VIDEO:
		case EnumConstUtil.QOS_CLASS_CONTROLLED_LOAD:
		case EnumConstUtil.QOS_CLASS_EXCELLENT_EFFORT:
		case EnumConstUtil.QOS_CLASS_BEST_EFFORT_1:
		case EnumConstUtil.QOS_CLASS_BEST_EFFORT_2:
		case EnumConstUtil.QOS_CLASS_BACKGROUND:
			qosClassString = MgrUtil.getEnumString("enum.qosClass." + qosClass);
			break;
		default:
		}
		return qosClassString;
	}

	public boolean isNetworkServiceDisabled() {
		return networkServiceDisabled;
	}

	public void setNetworkServiceDisabled(boolean networkServiceDisabled) {
		this.networkServiceDisabled = networkServiceDisabled;
	}

	public CustomApplication getCustomAppService() {
		return customAppService;
	}

	public void setCustomAppService(CustomApplication customAppService) {
		this.customAppService = customAppService;
	}

}
