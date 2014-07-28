package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Range;

import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Embeddable
public class CustomApplicationRule  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Range(min = 1)
	private short ruleId;
	
	@Column( nullable=false )
	private short detectionType;

	@Column( nullable=true )
	private short protocolId;
	
	@Column(length = 256, nullable=true)
	private String ruleValue;
	
	@Column( nullable=true )
	private int portNumber; 
	
	public static final short DETECTION_TYPE_HOSTNAME = 1;
	
	public static final short DETECTION_TYPE_IPADDRESS = 2;
	
	public static final short DETECTION_TYPE_PORT = 3;
	
	public static EnumItem[] ENUM_DETECTION_TYPE = MgrUtil.enumItems(
			"enum.custom.application.detection.type.", new int[] { DETECTION_TYPE_HOSTNAME, 
					DETECTION_TYPE_IPADDRESS ,DETECTION_TYPE_PORT});

	public static final short PROTOCOL_ID_TCP = 1;

	public static final short PROTOCOL_ID_UDP = 2;

	public static final short PROTOCOL_ID_HTTP = 3;

	public static final short PROTOCOL_ID_HTTPS = 4;
	
	public static EnumItem[] ENUM_PROTOCOL_ID = MgrUtil.enumItems(
			"enum.custom.application.protocolId.", new int[] { PROTOCOL_ID_TCP, PROTOCOL_ID_UDP});
	
	public static EnumItem[] ENUM_PROTOCOL_HTTP_ID = MgrUtil.enumItems(
			"enum.custom.application.protocolId.", new int[] { PROTOCOL_ID_HTTP, PROTOCOL_ID_HTTPS});

	@Column( nullable=true )
	private short headerNameType;
	
	public static final short HEADNAME_TYPE_GET = 1;

	public static final short HEADNAME_TYPE_POST = 2;
	
	public static EnumItem[] ENUM_HEADNAME_TYPE = MgrUtil.enumItems(
			"enum.custom.application.headname.type.", new int[] { HEADNAME_TYPE_GET, HEADNAME_TYPE_POST});
	
	@Transient
	private String detectionTypeStr;
	
	@Transient
	private String protocolTypeStr;
	
	
	public String getDetectionTypeStr() {
		return MgrUtil.getEnumString("enum.custom.application.detection.type."+getDetectionType());
	}

	public String getProtocolTypeStr() {
		return MgrUtil.getEnumString("enum.custom.application.protocolId."+getProtocolId());
	}
	
	public String getCdpModule() {
		switch (protocolId) {
		case PROTOCOL_ID_TCP:
			return "TCP";
		case PROTOCOL_ID_UDP:
			return "UDP";
		case PROTOCOL_ID_HTTP:
			return "HTTP";
		case PROTOCOL_ID_HTTPS:
			return "TLS";
		default:
			return "";
		}
	}


	public short getRuleId() {
		return ruleId;
	}

	public void setRuleId(short ruleId) {
		this.ruleId = ruleId;
	}

	public short getDetectionType() {
		return detectionType;
	}

	public void setDetectionType(short detectionType) {
		this.detectionType = detectionType;
	}

	public short getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(short protocolId) {
		this.protocolId = protocolId;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getRuleValue() {
		return ruleValue;
	}

	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}

	public short getHeaderNameType() {
		return headerNameType;
	}

	public void setHeaderNameType(short headerNameType) {
		this.headerNameType = headerNameType;
	}
	
	@Transient
	public String restoreId;

	public String getRestoreId()
	{
		return restoreId;
	}
	public void setRestoreId(String restoreId)
	{
		this.restoreId = restoreId;
	}
	
}
