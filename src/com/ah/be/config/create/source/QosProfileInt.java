package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;

/**
 * 
 * @author zhang
 *
 */
public interface QosProfileInt {
	
	public static final short QOS_ACTION_PERMIT = 1;
	public static final short QOS_ACTION_DENY =2;
	
	public enum QOS_POLICY_QOS_TYPE{
		strict, wrr
	}
	
	public String getQosClassMapGuiName();
	
	public String getQosClassMapName();
	
	public String getQosMarkerMapGuiName();
	
	public String getQosMarkerMapName();
	
	public String getClassAndMarkerGuiName();
	
	public String getQosPolicyGuiName();
	
	public String getApVersion();

//	public boolean isConfigureQos();
	
	public String getUpdateTime();
	
	public String getQosClassUpdateTime();
	
	public String getQosMarkerUpdateTime();
	
	public int getQosClassifierSize();
	
	public int getQosMarkerSize();
	
	public int getQosPolicySize();
	
	public String getQosClisifierProfileName(int index);
	
	public boolean isConfigureClassifier8021p(int index);
	
	public boolean isConfigureClassifier80211e(int index);
	
	public boolean isConfigureClassifierDiffserv(int index);
	
	public boolean isConfigureClassifierMac(int index);
	
	public boolean isConfigureClassifierService(int index);
	
	public boolean isConfigClassInterfaceSsid(int index);
	
	public boolean isConfigClassInterfaceSsidOnly(int index);
	
	//qos policy
	public String getQosPolicyName(int index);
	
	public int getQosPolicyUserLimit(int index);
	
	public boolean isQosPolicyDef(int index);
	
	public String getQosPolicyUserProfileValue(int index);
	
	public int getQosPolicyQosSize(int index);
	
	public int getQosPolicyQosName(int i, int j);
	
//	public boolean isConfigureQosPolicyStrict(int i, int j);
//	
//	public boolean isConfigureQosPolicyWRR(int i, int j);
	
	public String getQosPolicyQosCrValue(int i, int j);
	
//	public String getQosPolicyTypeValue(int i, int j);
	
	//qos marker profile
	public String getQosMarkerProfileName(int index );
	
	public boolean isConfigureMarker80211e(int index );
	
	public boolean isConfigureMarker8021p(int index );
	
	public boolean isConfigureMarkerDiffServ(int index);
	
//	public boolean isConfigureClassMap();
	
	public boolean isConfigureMarkerMap();
	
	public int getClassMap80211eSize();
	
	public String getClassMap80211eName(int index);
	
	public int getClassMap8021pSize();
	
	public String getClassMap8021pName(int index);
	
	public int getClassMapDiffServSize();
	
	public String getClassMapDiffServName(int index);
	
	public int getClassMapOuiSize();
	
	public String getClassMapOuiAddr(int index) throws CreateXMLException;
	
	public int getClassMapOuiQos(int index);
	
	public boolean isConfigureClassOuiActionPermit(int index);
	
	public boolean isConfigureClassOuiActionDeny(int index);
	
	public boolean isConfigureClassOuiActionLog(int index);
	
	public int getClassMapSsidSize();
	
	public String getClassMapSsidName(int index);
	
	public int getClassMapSsidLevel(int index);
	
	public int getClassMapServiceSize();
	
	public String getClassMapServiceName(int index);
	
	public int getClassMapServiceQos(int index);
	
	public boolean isConfigureClassMapActionPermit(int index);
	
	public boolean isConfigureClassMapActionDeny(int index);
	
	public boolean isConfigureClassMapActionLog(int index);
	
	public int getMarkerMap8021pSize();
	
	public int getMarkerMap8021pClass(int index);
	
	public int getMarkerMap8021pPriority(int index);
	
	public int getMarkerMapDiffServSize();
	
	public int getMarkerMapDiffServPriority(int index);
	
	public int getMarkerMapDiffServClass(int index);
	
	public boolean isEnableQosAirTime();
	
	public boolean isConfigOuiComment(int index);
	
	public String getOuiComment(int index);
	
	public boolean isConfigQosSsidLevel(int level);
	
	public boolean isEnableEth0LimitBandwidth();
	
	public boolean isEnableUSBLimitBandwidth();

	public int getQosEth0DownLoadRate();

	public int getQosEth0UploadRate();
	
	public int getQosUsbDownLoadRate();

	public int getQosUsbUploadRate();
	
	public boolean isConfigTunnel();
	
	public int getClassifierMapInterfaceSize();
	
	public String getClassifierMapInterfaceName(int index);
	
	public int getClassifierMapInterfacePriority(int index);
	
	public boolean isQosEnable();
	
	public int getUD8021pSize();
	
	public int getUDDiffservSize();
	
	public String getUD8021pName(int i);
	
	public String getUDDiffservName(int i);
	
	public int getUD8021pContentSize(int i);
	
	public int getUDDiffservContentSize(int i);
	
	public int getUD8021pContentClass(int i, int j);
	
	public int getUD8021pContentPriority(int i, int j);
	
	public int getUDDiffservContentClass(int i, int j);
	
	public int getUDDiffservContentPriority(int i, int j);
}
