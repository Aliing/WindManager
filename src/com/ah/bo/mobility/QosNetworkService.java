package com.ah.bo.mobility;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.network.MacFilter;
import com.ah.bo.network.NetworkService;
import com.ah.util.EnumConstUtil;
import com.ah.util.MgrUtil;

/*
 * @author Chris Scheers
 */

@Embeddable
@SuppressWarnings("serial")
public class QosNetworkService implements Serializable {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "NETWORK_SERVICE_ID", nullable = true)
	private NetworkService networkService;

	private short qosClass;

	private short filterAction;

	private short logging;
	
	@Transient
	private boolean networkServiceDisabled;
	
	public QosNetworkService() {
		
	}
	
	public QosNetworkService(QosCustomService customService) {
		networkService = new NetworkService(customService.getCustomAppService());
		this.filterAction = customService.getFilterAction();
		this.qosClass = customService.getQosClass();
		this.logging = customService.getLogging();
	}

	@Transient
	public String[] getFieldValues() {
		String[] fieldValues = { "QOS_CLASSIFICATION_ID", "NETWORK_SERVICE_ID",
				"qosClass", "filterAction", "logging", "QOS_NETWORK_SERVICE_ID"};
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

	public NetworkService getNetworkService() {
		return networkService;
	}

	public void setNetworkService(NetworkService networkService) {
		this.networkService = networkService;
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

	
}
