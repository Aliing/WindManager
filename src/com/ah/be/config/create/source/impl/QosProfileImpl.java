package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.QosProfileInt;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosCustomService;
import com.ah.bo.mobility.QosMacOui;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.mobility.QosNetworkService;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.QosRateLimit;
import com.ah.bo.mobility.QosSsid;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.Navigation;
import com.ah.util.EnumConstUtil;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.ApplicationUtil;

/**
 * 
 * @author zhang
 *
 */

@SuppressWarnings("static-access")
public class QosProfileImpl implements QosProfileInt {
	
	public  static final String[] defaultKey={ "7", "6", "5", "4", "3", "0", "2", "1" };
	public  static final String[] defaultDiffKey={ "7", "6", "5", "4", "3", "2", "1", "0" };
	public static final String[] defaultDiffValue = { "56-63", "48-55", "40-47", "32-39", "24-31", "00-07", "16-23", "08-15" };

	private HiveAp hiveAp;
	private List<UserProfile> allUserProfileList;
	
	private List<QosRateControl> qosPolicyList;
	
	private List<QosSsid> classRelationSsid = new ArrayList<QosSsid>();

	private List<DiffServParm> classDiffParmList;
	
	private List<ConfigTemplateSsid> qosClassList = new ArrayList<ConfigTemplateSsid>();
	private List<ConfigTemplateSsid> qosMarkerList = new ArrayList<ConfigTemplateSsid>();
	private QosClassification qosClassMap;
	private QosMarking	qosMarkerMap;
	
	private List<QosInterface> qosInterfaces;
	
	private List<NetworkService> serviceList;
	
	private List<CustomApplication> customAppserviceList;
	
	private List<QosMarking> markerMap8021p = new ArrayList<QosMarking>();
	
	private List<QosMarking> markerMapDiffserv = new ArrayList<QosMarking>();
	
	private List<QosNetworkService> qosServiceList = new ArrayList<QosNetworkService>();
	
	private static final int qosUnLimitBnadWidthRate = 0 ;
	
	public QosProfileImpl(HiveAp hiveAp, List<UserProfile> allUserProfileList){
		this.hiveAp = hiveAp;
		this.allUserProfileList = allUserProfileList;
		initalLoad();
	}
	
	public String getQosClassMapGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.qosClassification");
	}
	
	public String getQosClassMapName(){
		if(qosClassMap != null){
			return qosClassMap.getClassificationName();
		}else{
			return null;
		}
	}
	
	public String getQosMarkerMapGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.qosMarking");
	}
	
	public String getQosMarkerMapName(){
		if(qosMarkerMap != null){
			return qosMarkerMap.getQosName();
		}else{
			return null;
		}
	}
	
	public String getClassAndMarkerGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.qosClassfierAndMarker");
	}
	
	public String getQosPolicyGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.qosRateControl");
	}
	
	public List<NetworkService> getServiceList(){
		if(serviceList == null){
			serviceList = new ArrayList<NetworkService>();
		}
		if(qosClassMap != null && qosClassMap.getNetworkServicesEnabled() && qosClassMap.getNetworkServices() != null){
			for(QosNetworkService qosServiceObj : qosClassMap.getNetworkServices().values()){
				serviceList.add(qosServiceObj.getNetworkService());
			}
		}
		return serviceList;
	}
	
	public List<CustomApplication> getCustomAppServiceList(){
		if(customAppserviceList == null){
			customAppserviceList = new ArrayList<CustomApplication>();
		}
		if(qosClassMap != null && qosClassMap.getCustomServices() != null){
			for(QosCustomService customService : qosClassMap.getCustomServices().values()){
				customAppserviceList.add(customService.getCustomAppService());
			}
		}
		return customAppserviceList;
	}
	
	private void initalLoad(){
		
		//qosPolicyList
		if(allUserProfileList != null){
			ConcurrentMap<String, QosRateControl> qosPolicyMap = new ConcurrentHashMap<String,QosRateControl>();
			for(UserProfile upObj : allUserProfileList){
				if(upObj != null){
					QosRateControl qrc = upObj.getQosRateControl();
					
					if (qrc != null){
						String qosName = qrc.getQosName();
						qosPolicyMap.putIfAbsent(qosName, qrc);
					}
				}
			}
			qosPolicyList = new ArrayList<QosRateControl>(qosPolicyMap.values());
		}
		
		// find qos class map
		qosClassMap = hiveAp.getConfigTemplate().getClassifierMap();
//		if(Navigation.isExpressHMMode(hiveAp.getOwner()) /*** && isExistsVoiceSsid(hiveAp) **/){
//			if(qosClassMap == null){
//				qosClassMap = new QosClassification();
//			}
//			qosClassMap.setNetworkServicesEnabled(true);
//			
//			Map<Long, QosNetworkService> serviceMap = this.LoadNetworkServicesMap();
//			if(qosClassMap.getNetworkServices() == null){
//				qosClassMap.setNetworkServices(serviceMap);
//			}else{
//				Iterator<Entry<Long, QosNetworkService>> entryService = serviceMap.entrySet().iterator();
//				for(;entryService.hasNext();){
//					Entry<Long, QosNetworkService> entryNext = entryService.next();
//					qosClassMap.getNetworkServices().put(entryNext.getKey(), entryNext.getValue());
//				}
//			}
//			
//			qosClassMap.setSsidEnabled(true);
//			qosClassMap.setQosSsids(this.getQosSsidList());
//		}
		qosMarkerMap = hiveAp.getConfigTemplate().getMarkerMap();
		hiveAp.getConfigTemplate().setClassifierMap(qosClassMap);
		
		//new
		if(hiveAp.getConfigTemplate().getEnabledMapOverride() && !hiveAp.getDeviceInfo().isSptEthernetMore_24()){
			for(ConfigTemplateSsid templateSsid : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
//				if((hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_110 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_120 
//						|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_121 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_141) && 
//						("eth1".equals(templateSsid.getInterfaceName())
//						|| "eth2".equals(templateSsid.getInterfaceName())
//						|| "eth3".equals(templateSsid.getInterfaceName())
//						|| "eth4".equals(templateSsid.getInterfaceName())
//						|| "red0".equals(templateSsid.getInterfaceName())
//						|| "agg0".equals(templateSsid.getInterfaceName()))){
//					continue;
//				}
				
				if(templateSsid.getNetworkServicesEnabled() || templateSsid.getMacOuisEnabled() || 
						templateSsid.getSsidEnabled() || templateSsid.getCheckE() ||
						templateSsid.getCheckP() || templateSsid.getCheckD() || templateSsid.getSsidOnlyEnabled()){
					qosClassList.add(templateSsid);
				}
				
				if(templateSsid.getCheckET() || templateSsid.getCheckPT() || templateSsid.getCheckDT()){
					qosMarkerList.add(templateSsid);
				}
			}
		}else{
			if(qosClassMap != null){
				for(ConfigTemplateSsid templateSsid : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
//					if((hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_110 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_120 || 
//							hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_121 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_141) && 
//							("eth1".equals(templateSsid.getInterfaceName())
//							|| "red0".equals(templateSsid.getInterfaceName())
//							|| "agg0".equals(templateSsid.getInterfaceName()))){
//						continue;
//					}
					
					ConfigTemplateSsid cloneTempSsid = new ConfigTemplateSsid();
					cloneTempSsid.setInterfaceName(templateSsid.getInterfaceName());
					cloneTempSsid.setSsidProfile(templateSsid.getSsidProfile());
					if(qosClassMap.getMacOuisEnabled()){
						cloneTempSsid.setMacOuisEnabled(true);
					}
					if(qosClassMap.getNetworkServicesEnabled()){
						cloneTempSsid.setNetworkServicesEnabled(true);
					}
					if(qosClassMap.getSsidEnabled()){
						cloneTempSsid.setSsidEnabled(true);
					}
					if(qosClassMap.getGeneralEnabled()){
						if(qosClassMap.getPrtclD() != null && !"".equals(qosClassMap.getPrtclD())){
							cloneTempSsid.setCheckD(true);
						}else{
							if(qosClassMap.getPrtclP() != null && !"".equals(qosClassMap.getPrtclP()) && cloneTempSsid.getSsidProfile() == null){
								cloneTempSsid.setCheckP(true);
							}
							if(qosClassMap.getPrtclE() != null && !"".equals(qosClassMap.getPrtclE()) && cloneTempSsid.getSsidProfile() != null){
								cloneTempSsid.setCheckE(true);
							}
						}
					}
					qosClassList.add(cloneTempSsid);
				}
			}
			
			//qos for switch
			if(hiveAp.getDeviceInfo().isSptEthernetMore_24() && hiveAp.getPortGroup() != null && 
					hiveAp.getPortGroup().getBasicProfiles() != null 
					&& isSwitchQosEnable(this.hiveAp)){
				qosInterfaces = new ArrayList<QosInterface>();
				for(PortBasicProfile portBase : hiveAp.getPortGroup().getBasicProfiles()){
					PortAccessProfile accessProfile = portBase.getAccessProfile();
					if(accessProfile == null){
						continue;
					}else if(accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_MONITOR || 
							accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_WAN){
						continue;
					}
					
					//qos-classifier
					if(accessProfile.getQosClassificationMode() == PortAccessProfile.QOS_CLASSIFICATION_MODE_TRUSTED){
						ConfigTemplateSsid qosProfile = new ConfigTemplateSsid();
						qosProfile.setInterfaceName(accessProfile.getName());
						if(accessProfile.getQosClassificationTrustMode() == PortAccessProfile.QOS_CLASSIFICATION_TRUST_DSCP){
							qosProfile.setCheckD(true);
						}else if(accessProfile.getQosClassificationTrustMode() == PortAccessProfile.QOS_CLASSIFICATION_TRUST_8021P){
							qosProfile.setCheckP(true);
						}
						qosClassList.add(qosProfile);
					}
					
					//qos-marker
					if(accessProfile.isEnableQosMark()){
						ConfigTemplateSsid qosProfile = new ConfigTemplateSsid();
						qosProfile.setInterfaceName(accessProfile.getName());
						if(accessProfile.getQosMarkMode() == PortAccessProfile.QOS_CLASSIFICATION_TRUST_DSCP){
							qosProfile.setCheckDT(true);
						}
						if(accessProfile.getQosMarkMode() == PortAccessProfile.QOS_CLASSIFICATION_TRUST_8021P){
							qosProfile.setCheckPT(true);
						}
						qosMarkerList.add(qosProfile);
					}
					
					//qos interface
					if(accessProfile.getQosClassificationMode() == PortAccessProfile.QOS_CLASSIFICATION_MODE_UNTRUSTED || 
							(accessProfile.getQosClassificationMode() == PortAccessProfile.QOS_CLASSIFICATION_MODE_TRUSTED && 
							accessProfile.isEnableTrustedProiority()) ){
						int priority = accessProfile.getUntrustedPriority();
						if(accessProfile.getQosClassificationMode() == PortAccessProfile.QOS_CLASSIFICATION_MODE_UNTRUSTED){
							priority = accessProfile.getUntrustedPriority();
						}else{
							priority = accessProfile.getTrustedPriority();
						}
						String[] eths = portBase.getETHs();
						String[] sfps = portBase.getSFPs();
						
						//port channel
						if(portBase.isEnabledlinkAggregation()){
							short portChannel = portBase.getPortChannel();
							QosInterface qosInf = new QosInterface();
							qosInf.setInfName(DeviceInfType.PortChannel.getCLIName(Integer.valueOf(portChannel), hiveAp.getHiveApModel()));
							qosInf.setPriority(priority);
							qosInterfaces.add(qosInf);
							continue;
						}
						
						if(eths != null && eths.length > 0){
							for(int i=0; i<eths.length; i++){
								QosInterface qosInf = new QosInterface();
								qosInf.setInfName(DeviceInfType.Gigabit.getCLIName(Integer.valueOf(eths[i]), hiveAp.getHiveApModel()));
								qosInf.setPriority(priority);
								qosInterfaces.add(qosInf);
							}
						}
						if(sfps != null && sfps.length > 0){
							for(int i=0; i<sfps.length; i++){
								QosInterface qosInf = new QosInterface();
								qosInf.setInfName(DeviceInfType.SFP.getCLIName(Integer.valueOf(sfps[i]), hiveAp.getHiveApModel()));
								qosInf.setPriority(priority);
								qosInterfaces.add(qosInf);
							}
						}
					}
				}
			}
			if(qosMarkerMap != null){
				for(ConfigTemplateSsid templateSsid : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
					if((hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_110 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_120 || 
							hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_121 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_141) && 
							("eth1".equals(templateSsid.getInterfaceName())
							|| "red0".equals(templateSsid.getInterfaceName())
							|| "agg0".equals(templateSsid.getInterfaceName()))){
						continue;
					}
					ConfigTemplateSsid cloneTempSsid = new ConfigTemplateSsid();
					cloneTempSsid.setInterfaceName(templateSsid.getInterfaceName());
					cloneTempSsid.setSsidProfile(templateSsid.getSsidProfile());
					if(qosMarkerMap.getPrtclD() != null && !"".equals(qosMarkerMap.getPrtclD())){
						cloneTempSsid.setCheckDT(true);
					}else{
						if(qosMarkerMap.getPrtclP() != null && !"".equals(qosMarkerMap.getPrtclP()) && cloneTempSsid.getSsidProfile() == null){
							cloneTempSsid.setCheckPT(true);
						}
						if(qosMarkerMap.getPrtclE() != null && !"".equals(qosMarkerMap.getPrtclE()) && cloneTempSsid.getSsidProfile() != null){
							cloneTempSsid.setCheckET(true);
						}
					}
					qosMarkerList.add(cloneTempSsid);
				}
			}

		}
		
		if (qosClassMap != null && qosClassMap.getNetworkServicesEnabled()) {
			qosServiceList.addAll(qosClassMap.getNetworkServices().values());
			if (ApplicationUtil.isSupportL7Service(hiveAp)) {
				for (QosCustomService customService : qosClassMap.getCustomServices().values()) {
					qosServiceList.add(new QosNetworkService(customService));
				}
			}
		}
		
		
		//List for qos  ssid
		
		for(ConfigTemplateSsid configSsid: hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			SsidProfile ssidProfileObj = configSsid.getSsidProfile();
			if(ssidProfileObj != null){
				QosSsid qosSsidnew = new QosSsid();
				qosSsidnew.setSsid(ssidProfileObj);
				qosSsidnew.setQosClassSsids((short) -1);
				classRelationSsid.add(qosSsidnew);
			}
		}
		if(qosClassMap != null && qosClassMap.getSsidEnabled() && qosClassMap.getQosSsids() != null){
			for(QosSsid qosSsid : qosClassMap.getQosSsids().values()){
				boolean isFoundSsid = false;
				for(QosSsid qosSsidObj : classRelationSsid){
					if(qosSsidObj.getSsid().getSsidName().equals(qosSsid.getSsid().getSsidName())){
						isFoundSsid = true;
						classRelationSsid.remove(qosSsidObj);
						break;
					}
				}
				if(isFoundSsid){
					classRelationSsid.add(qosSsid);
				}
			}
		}
		
		// load qosMarkMap from UserProfile
		if(allUserProfileList != null){
			Set<String> mapNameSet = new HashSet<String>();
			for(UserProfile userProfile : allUserProfileList){
				QosMarking markMap = userProfile.getMarkerMap();
				if(markMap == null){
					continue;
				}
				if(mapNameSet.contains(markMap.getQosName())){
					continue;
				}
				if(markMap.getPrtclP() != null && !"".equals(markMap.getPrtclP())){
					markerMap8021p.add(markMap);
					mapNameSet.add(markMap.getQosName());
				}
				if(markMap.getPrtclD() != null && !"".equals(markMap.getPrtclD())){
					markerMapDiffserv.add(markMap);
					mapNameSet.add(markMap.getQosName());
				}
			}
		}
	}
	
	public static boolean isSwitchQosEnable(HiveAp hiveAp){
		if(hiveAp == null){
			return false;
		}
		return hiveAp.isEnableSwitchQosSettings();
	}
	
	public static boolean isExistsVoiceSsid(HiveAp hiveAp){
		List<SsidProfile> ssidList = new ArrayList<SsidProfile>();
		for(ConfigTemplateSsid ctSsid : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			if(ctSsid.getSsidProfile() != null){
				ssidList.add(ctSsid.getSsidProfile());
			}
		}
		for(SsidProfile ssid : ssidList){
			if(ssid.getUserCategory() == SsidProfile.USER_CATEGORY_VOICE){
				return true;
			}
		}
		return false;
	}
	
	private Map<Long, QosNetworkService> LoadNetworkServicesMap(){
		Map<Long, QosNetworkService> serviceList = new HashMap<Long, QosNetworkService>();
		
		QosNetworkService qosService;
		NetworkService networkService;
		
		/** HTTP Class 2 */
		qosService = new QosNetworkService();
		networkService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "HTTP");
		qosService.setNetworkService(networkService);
		qosService.setQosClass(EnumConstUtil.QOS_CLASS_BEST_EFFORT_1);
		qosService.setFilterAction(QosProfileInt.QOS_ACTION_PERMIT);
		qosService.setLogging(EnumConstUtil.DISABLE);
		serviceList.put(networkService.getId(), qosService);
		
		/** HTTPS Class 2 */
		qosService = new QosNetworkService();
		networkService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "HTTPS");
		qosService.setNetworkService(networkService);
		qosService.setQosClass(EnumConstUtil.QOS_CLASS_BEST_EFFORT_1);
		qosService.setFilterAction(QosProfileInt.QOS_ACTION_PERMIT);
		qosService.setLogging(EnumConstUtil.DISABLE);
		serviceList.put(networkService.getId(), qosService);
		
		/** SSH Class 2 */
		qosService = new QosNetworkService();
		networkService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "SSH");
		qosService.setNetworkService(networkService);
		qosService.setQosClass(EnumConstUtil.QOS_CLASS_BEST_EFFORT_1);
		qosService.setFilterAction(QosProfileInt.QOS_ACTION_PERMIT);
		qosService.setLogging(EnumConstUtil.DISABLE);
		serviceList.put(networkService.getId(), qosService);
		
		/** FTP Class 2 */
		qosService = new QosNetworkService();
		networkService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "FTP");
		qosService.setNetworkService(networkService);
		qosService.setQosClass(EnumConstUtil.QOS_CLASS_BEST_EFFORT_1);
		qosService.setFilterAction(QosProfileInt.QOS_ACTION_PERMIT);
		qosService.setLogging(EnumConstUtil.DISABLE);
		serviceList.put(networkService.getId(), qosService);
		
		/** TFTP Class 2 */
		qosService = new QosNetworkService();
		networkService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "TFTP");
		qosService.setNetworkService(networkService);
		qosService.setQosClass(EnumConstUtil.QOS_CLASS_BEST_EFFORT_1);
		qosService.setFilterAction(QosProfileInt.QOS_ACTION_PERMIT);
		qosService.setLogging(EnumConstUtil.DISABLE);
		serviceList.put(networkService.getId(), qosService);
		
		/** DNS Class 4 */
		qosService = new QosNetworkService();
		networkService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "DNS");
		qosService.setNetworkService(networkService);
		qosService.setQosClass(EnumConstUtil.QOS_CLASS_CONTROLLED_LOAD);
		qosService.setFilterAction(QosProfileInt.QOS_ACTION_PERMIT);
		qosService.setLogging(EnumConstUtil.DISABLE);
		serviceList.put(networkService.getId(), qosService);

		/** DHCP-Client Class 4 */
		qosService = new QosNetworkService();
		networkService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "DHCP-Client");
		qosService.setNetworkService(networkService);
		qosService.setQosClass(EnumConstUtil.QOS_CLASS_CONTROLLED_LOAD);
		qosService.setFilterAction(QosProfileInt.QOS_ACTION_PERMIT);
		qosService.setLogging(EnumConstUtil.DISABLE);
		serviceList.put(networkService.getId(), qosService);
		
		/** DHCP-Server Class 4 */
		qosService = new QosNetworkService();
		networkService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "DHCP-Server");
		qosService.setNetworkService(networkService);
		qosService.setQosClass(EnumConstUtil.QOS_CLASS_CONTROLLED_LOAD);
		qosService.setFilterAction(QosProfileInt.QOS_ACTION_PERMIT);
		qosService.setLogging(EnumConstUtil.DISABLE);
		serviceList.put(networkService.getId(), qosService);
		
		/** TELNET Class 4 */
		qosService = new QosNetworkService();
		networkService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "TELNET");
		qosService.setNetworkService(networkService);
		qosService.setQosClass(EnumConstUtil.QOS_CLASS_CONTROLLED_LOAD);
		qosService.setFilterAction(QosProfileInt.QOS_ACTION_PERMIT);
		qosService.setLogging(EnumConstUtil.DISABLE);
		serviceList.put(networkService.getId(), qosService);
		
		/** VOCERA-Control Class 5 */
		qosService = new QosNetworkService();
		networkService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "VOCERA-Control");
		qosService.setNetworkService(networkService);
		qosService.setQosClass(EnumConstUtil.QOS_CLASS_VIDEO);
		qosService.setFilterAction(QosProfileInt.QOS_ACTION_PERMIT);
		qosService.setLogging(EnumConstUtil.DISABLE);
		serviceList.put(networkService.getId(), qosService);
		
		/** VOCERA-Media Class 6 */
		qosService = new QosNetworkService();
		networkService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "VOCERA-Media");
		qosService.setNetworkService(networkService);
		qosService.setQosClass(EnumConstUtil.QOS_CLASS_VOICE);
		qosService.setFilterAction(QosProfileInt.QOS_ACTION_PERMIT);
		qosService.setLogging(EnumConstUtil.DISABLE);
		serviceList.put(networkService.getId(), qosService);
		
		/** VOIP-SVP Class 6 */
		qosService = new QosNetworkService();
		networkService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "VOIP-SVP");
		qosService.setNetworkService(networkService);
		qosService.setQosClass(EnumConstUtil.QOS_CLASS_VOICE);
		qosService.setFilterAction(QosProfileInt.QOS_ACTION_PERMIT);
		qosService.setLogging(EnumConstUtil.DISABLE);
		serviceList.put(networkService.getId(), qosService);
		
		/** SIP Class Class 6 */
		qosService = new QosNetworkService();
		networkService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "SIP");
		qosService.setNetworkService(networkService);
		qosService.setQosClass(EnumConstUtil.QOS_CLASS_VOICE);
		qosService.setFilterAction(QosProfileInt.QOS_ACTION_PERMIT);
		qosService.setLogging(EnumConstUtil.DISABLE);
		serviceList.put(networkService.getId(), qosService);
		
		return serviceList;
	}
	
	private Map<Long, QosSsid> getQosSsidList(){
		Map<Long, QosSsid> ssidList = new HashMap<Long, QosSsid>();
		List<SsidProfile> ssidProfileList = new ArrayList<SsidProfile>();
		for(ConfigTemplateSsid ctSsid : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			if(ctSsid.getSsidProfile() != null){
				ssidProfileList.add(ctSsid.getSsidProfile());
			}
		}
		for(SsidProfile ssidProfile :  ssidProfileList){
			if(ssidProfile.getUserCategory() == SsidProfile.USER_CATEGORY_VOICE){
				QosSsid qosSsid = new QosSsid();
				qosSsid.setSsid(ssidProfile);
				qosSsid.setQosClassSsids((short)6);
				ssidList.put(ssidProfile.getId(), qosSsid);
			}
		}
		return ssidList;
	}
	
	private class DiffServParm {
		private int dscpClass;
		private int qosClass;
		
		public void setDscpClass(int value){
			this.dscpClass = value;
		}
		
		public int getDscpClass(){
			return this.dscpClass;
		}
		
		public void setQosClass(int value){
			this.qosClass = value;
		}
		
		public int getQosClass(){
			return this.qosClass;
		}
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
//	public boolean isConfigureQos(){
//		return !qosClassList.isEmpty() || !qosMarkerList.isEmpty() ||
//				qosClassMap != null || qosMarkerMap != null ||
//				(qosPolicyList != null && qosPolicyList.size() >0) ;
//	}
	
	public String getUpdateTime(){
//		List<Object> qosUpdateList = new ArrayList<Object>();
//		qosUpdateList.add(hiveAp);
//		qosUpdateList.add(qosClass);
//		if(qosClass != null){
//			if(qosClass.getNetworkServices() != null){
//				for(QosNetworkService qosServiceObj : qosClass.getNetworkServices().values()){
//					if(qosServiceObj != null){
//						qosUpdateList.add(qosServiceObj.getNetworkService());
//					}
//				}
//			}
//			if(qosClass.getQosMacOuis() != null){
//				for(QosMacOui qosMac : qosClass.getQosMacOuis().values()){
//					if(qosMac != null){
//						qosUpdateList.add(qosMac.getMacOui());
//					}
//				}
//			}
//		}
//		qosUpdateList.add(qosMarker);
//		if(qosPolicyList != null){
//			qosUpdateList.addAll(qosPolicyList);
//		}
		return CLICommonFunc.getLastUpdateTime(null);
	}
	
	public String getQosClassUpdateTime(){
//		List<Object> qosTimeList = new ArrayList<Object>();
//		qosTimeList.add(hiveAp);
//		qosTimeList.add(qosClass);
//		if(qosClass.getNetworkServices() != null){
//			for(QosNetworkService qosServiceObj : qosClass.getNetworkServices().values()){
//				qosTimeList.add(qosServiceObj.getNetworkService());
//			}
//		}
//		if(qosClass.getQosMacOuis() != null){
//			for(QosMacOui qosMac : qosClass.getQosMacOuis().values()){
//				qosTimeList.add(qosMac.getMacOui());
//			}
//		}
		return CLICommonFunc.getLastUpdateTime(null);
	}
	
	public String getQosMarkerUpdateTime(){
//		List<Object> qosMarkerList = new ArrayList<Object>();
//		qosMarkerList.add(qosMarker);
		return CLICommonFunc.getLastUpdateTime(null);
	}
	
	public int getQosClassifierSize(){
		if(qosClassList == null ){
			return 0;
		}else{
			return qosClassList.size();
		}
	}
	
	public int getQosMarkerSize(){
		if(qosMarkerList == null ){
			return 0;
		}else{
			return qosMarkerList.size();
		}
	}
	
	public int getQosPolicySize(){
		if(qosPolicyList == null ){
			return 0;
		}else{
			return qosPolicyList.size();
		}
	}
	
	public String getQosClisifierProfileName(int index){
		return qosClassList.get(index).getInterfaceName();
	}
	
	public boolean isConfigureClassifier8021p(int index){
		return qosClassList.get(index).getCheckP();
	}
	
	public boolean isConfigureClassifier80211e(int index){
		return qosClassList.get(index).getCheckE();
	}
	
	public boolean isConfigureClassifierDiffserv(int index){
		return qosClassList.get(index).getCheckD();
	}
	
	public boolean isConfigureClassifierMac(int index){
		return qosClassList.get(index).getMacOuisEnabled();
	}
	
	public boolean isConfigureClassifierService(int index){
		return qosClassList.get(index).getNetworkServicesEnabled();
	}
	
	public boolean isConfigClassInterfaceSsid(int index){
//		return qosClassList.get(index).getSsidProfile() != null && qosClassList.get(index).getSsidEnabled();
		return qosClassList.get(index).getSsidEnabled();
	}
	
	public boolean isConfigClassInterfaceSsidOnly(int index){
		return qosClassList.get(index).getSsidOnlyEnabled();
	}
	
	//qos policy
	public String getQosPolicyName(int index){
		return qosPolicyList.get(index).getQosName();
	}
	
	public int getQosPolicyUserLimit(int index){
		if(this.isHiveAp11ac()){
			return qosPolicyList.get(index).getRateLimit11ac();
		}else if(this.isHiveAp11n()){
			return qosPolicyList.get(index).getRateLimit11n();
		}else{
			return qosPolicyList.get(index).getRateLimit();
		}
	}
	
	public String getQosPolicyUserProfileValue(int index){
//		String qosName = qosPolicyList.get(index).getQosName();
//		List<ConfigTemplateQos> userProfileA = new ArrayList<ConfigTemplateQos>();
//		List<ConfigTemplateQos> userProfileB = new ArrayList<ConfigTemplateQos>();
//		for(ConfigTemplateQos policyTempObj : hiveAp.getConfigTemplate().getQosPolicies().values()){
//			if(qosName.equals(policyTempObj.getUserProfile().getQosRateControl().getQosName()) ){
//				if(policyTempObj.getRadioMode() == SsidProfile.RADIOMODE_A){
//					userProfileA.add(policyTempObj);
//				}else if(policyTempObj.getRadioMode() == SsidProfile.RADIOMODE_BG){
//					userProfileB.add(policyTempObj);
//				}
//			}
//		}
//		
//		//unite list B to list A
//		for(ConfigTemplateQos policyTempB : userProfileB){
//			String qosNameB = policyTempB.getUserProfile().getUserProfileName();
//			boolean isFoundUser = false;
//			for(ConfigTemplateQos policyTempA : userProfileA){
//				if(qosNameB.equals(policyTempA.getUserProfile().getUserProfileName())){
//					isFoundUser = true;
//					break;
//				}
//			}
//			if(!isFoundUser){
//				userProfileA.add(policyTempB);
//			}
//		}
		
//		//generate result string
//		int PolicyRate=0, PolicyWeight=0;
//		for(ConfigTemplateQos policyTempA : userProfileA){
//			if(policyTempA.getPolicingRate() > PolicyRate){
//				if(this.isHiveAp11n()){
//					PolicyRate = policyTempA.getPolicingRate11n();
//				}else{
//					PolicyRate = policyTempA.getPolicingRate();
//				}
//			}
//			PolicyWeight = policyTempA.getSchedulingWeight();
//		}
		
		String qosName = qosPolicyList.get(index).getQosName();
		int PolicyRate=0, PolicyWeight=0;
		for(UserProfile up : allUserProfileList){
			if(qosName.equals(up.getQosRateControl().getQosName())){
				int rate;
				if(this.hiveAp.is11acHiveAP()){
					rate = up.getPolicingRate11ac();
				}else if(this.hiveAp.is11nHiveAP()){
					rate = up.getPolicingRate11n();
				}else{
					rate = up.getPolicingRate();
				}
				if(rate > PolicyRate){
					PolicyRate = rate;
				}
				if(up.getSchedulingWeight() > PolicyWeight){
					PolicyWeight = up.getSchedulingWeight();
				}
			}
		}
		
		
		return String.valueOf(PolicyRate) + " " + String.valueOf(PolicyWeight);
//		if(PolicyRate >0 && PolicyWeight >0){
//			return String.valueOf(PolicyRate) + " " + String.valueOf(PolicyWeight);
//		}else{
//			if(this.isHiveAp11n() || this.isHiveAp11ac()){
//				return "2000000 10";
//			}else{
//				return "54000 10";
//			}
//		}
	}
	
	public int getQosPolicyQosSize(int index){
		return qosPolicyList.get(index).getQosRateLimit().size();
	}
	
	public int getQosPolicyQosName(int i, int j){
		return qosPolicyList.get(i).getQosRateLimit().get(j).getQosClass();
	}
	
//	public boolean isConfigureQosPolicyStrict(int i, int j){
//		return qosPolicyList.get(i).getQosRateLimit().get(j).getSchedulingType() == QosRateLimit.STRICT;
//	}
//	
//	public boolean isConfigureQosPolicyWRR(int i, int j){
//		return qosPolicyList.get(i).getQosRateLimit().get(j).getSchedulingType() == QosRateLimit.WEIGHTED_ROUND_ROBIN;
//	}
	
	public String getQosPolicyQosCrValue(int i, int j){
		return getQosPolciyCrType(i, j).name() + " " + getQosPolicyTypeValue(i, j);
	}
	
	private QOS_POLICY_QOS_TYPE getQosPolciyCrType(int i, int j){
		if(qosPolicyList.get(i).getQosRateLimit().get(j).getSchedulingType() == QosRateLimit.STRICT){
			return QOS_POLICY_QOS_TYPE.strict;
		}else if(qosPolicyList.get(i).getQosRateLimit().get(j).getSchedulingType() == QosRateLimit.WEIGHTED_ROUND_ROBIN){
			return QOS_POLICY_QOS_TYPE.wrr;
		}else{
			return null;
		}
	}
	
	private String getQosPolicyTypeValue(int i, int j){
		QosRateLimit qosStrick = qosPolicyList.get(i).getQosRateLimit().get(j);
		if(this.isHiveAp11ac()){
			return qosStrick.getPolicing11acRateLimit() + " " + qosStrick.getSchedulingWeight();
		}else if(this.isHiveAp11n()){
			return qosStrick.getPolicing11nRateLimit() + " " + qosStrick.getSchedulingWeight();
		}else{
			return qosStrick.getPolicingRateLimit() + " " + qosStrick.getSchedulingWeight();
		}
	}
	
	//qos marker profile 
	public String getQosMarkerProfileName(int index ){
		return qosMarkerList.get(index).getInterfaceName();
	}
	
	public boolean isConfigureMarker80211e(int index ){
		return qosMarkerList.get(index).getCheckET();
	}
	
	public boolean isConfigureMarker8021p(int index ){
		return qosMarkerList.get(index).getCheckPT();
	}
	
	public boolean isConfigureMarkerDiffServ(int index){
		return qosMarkerList.get(index).getCheckDT();
	}
	
//	public boolean isConfigureClassMap(){
//		return qosClassMap != null;
//	}
	
	public boolean isConfigureMarkerMap(){
		return qosMarkerMap != null || !markerMap8021p.isEmpty() || !markerMapDiffserv.isEmpty();
	}
	
	public int getClassMap80211eSize(){
		if(qosClassMap == null || qosClassMap.getPrtclE() == null || "".equals(qosClassMap.getPrtclE())){
			return 0;
		}else{
			return qosClassMap.getPrtclE().length(); 
		}
	}
	
	public String getClassMap80211eName(int index){
		String prtclE = qosClassMap.getPrtclE();
		return defaultKey[index] + " " + prtclE.substring(index, index+1);
	}
	
	public int getClassMap8021pSize(){
		if(qosClassMap == null || qosClassMap.getPrtclP() == null || "".equals(qosClassMap.getPrtclP())){
			return 0;
		}else{
			return defaultKey.length;
//			return qosClassMap.getPrtclP().length();
		}
	}
	
	public String getClassMap8021pName(int index){
		String prtclP = qosClassMap.getPrtclP();
		return defaultKey[index] + " " + prtclP.substring(index, index+1);
	}
	
	public int getClassMapDiffServSize(){
		if(qosClassMap == null || qosClassMap.getPrtclD() == null || "".equals(qosClassMap.getPrtclD()) ){
			return 0;
		}
		if(classDiffParmList == null){
			classDiffParmList = new ArrayList<DiffServParm>();
			for(int i=0; i<qosClassMap.getPrtclD().length(); i++){
				int qosClas = Integer.valueOf( qosClassMap.getPrtclD().substring(i, i+1) );
				String dscpClas = defaultDiffValue[i];
				String[] rangeArg = dscpClas.split("-");
				int lowerLimit = Integer.valueOf(rangeArg[0]);
				int upperLimit = Integer.valueOf(rangeArg[1]);
				while(lowerLimit<=upperLimit){
					DiffServParm diffParm = new DiffServParm();
					diffParm.setDscpClass(lowerLimit);
					diffParm.setQosClass(qosClas);
					classDiffParmList.add(diffParm);
					lowerLimit ++;
				}
			}
		}
		return classDiffParmList.size();
	}
	
	public String getClassMapDiffServName(int index){
		DiffServParm diffParm = classDiffParmList.get(index);
		return diffParm.getDscpClass() + " " + diffParm.getQosClass();
	}
	
	public int getClassMapOuiSize(){
		if(qosClassMap == null || !qosClassMap.getMacOuisEnabled()){
			return 0;
		}else{
			return qosClassMap.getQosMacOuis().values().size();
		}
	}
	
	public String getClassMapOuiAddr(int index) throws CreateXMLException{
		MacOrOui macOui = ((QosMacOui)qosClassMap.getQosMacOuis().values().toArray()[index]).getMacOui();
		SingleTableItem macItem = CLICommonFunc.getMacAddressOrOui(macOui, hiveAp);
		return CLICommonFunc.transFormMacAddrOrOui(macItem.getMacEntry());
	}
	
	public int getClassMapOuiQos(int index){
		return ((QosMacOui)qosClassMap.getQosMacOuis().values().toArray()[index]).getQosClassMacOuis();
	}
	
	public boolean isConfigureClassOuiActionPermit(int index){
		return ((QosMacOui)qosClassMap.getQosMacOuis().values().toArray()[index]).getFilterActionMacOuis() == QosProfileInt.QOS_ACTION_PERMIT;
	}
	
	public boolean isConfigureClassOuiActionDeny(int index){
		return ((QosMacOui)qosClassMap.getQosMacOuis().values().toArray()[index]).getFilterActionMacOuis() == QosProfileInt.QOS_ACTION_DENY;
	}
	
	public boolean isConfigureClassOuiActionLog(int index){
		return ((QosMacOui)qosClassMap.getQosMacOuis().values().toArray()[index]).isEnableLoggingMacOuis();
	}
	
	public int getClassMapSsidSize(){
		return classRelationSsid.size();
	}
	
	public String getClassMapSsidName(int index){
		return classRelationSsid.get(index).getSsid().getSsid();
//		return (String)classRelationSsid.toArray()[index];
		
	}
	
	public int getClassMapSsidLevel(int index){
		return classRelationSsid.get(index).getQosClassSsids();
//		return qosClassMap.getSsidIndex();
	}
	
	public int getClassMapServiceSize(){
		return this.qosServiceList.size();
	}
	
	public String getClassMapServiceName(int index){
		//NetworkService serviceObj = ((QosNetworkService)qosClassMap.getNetworkServices().values().toArray()[index]).getNetworkService();
		NetworkService serviceObj = qosServiceList.get(index).getNetworkService();
		if(serviceObj.isCliDefaultFlag()){
			return serviceObj.getServiceName().toLowerCase();
		}else{
			return serviceObj.getServiceName();
		}
	}
	
	public int getClassMapServiceQos(int index){
		//return ((QosNetworkService)qosClassMap.getNetworkServices().values().toArray()[index]).getQosClass();
		return qosServiceList.get(index).getQosClass();
	}
	
	public boolean isConfigureClassMapActionPermit(int index){
		//return ((QosNetworkService)qosClassMap.getNetworkServices().values().toArray()[index]).getFilterAction() == QosProfileInt.QOS_ACTION_PERMIT;
		return (qosServiceList.get(index).getFilterAction() == QosProfileInt.QOS_ACTION_PERMIT);
	}
	
	public boolean isConfigureClassMapActionDeny(int index){
		//return((QosNetworkService)qosClassMap.getNetworkServices().values().toArray()[index]).getFilterAction() == QosProfileInt.QOS_ACTION_DENY;
		return (qosServiceList.get(index).getFilterAction() == QosProfileInt.QOS_ACTION_DENY);
	}
	
	public boolean isConfigureClassMapActionLog(int index){
		//return ((QosNetworkService)qosClassMap.getNetworkServices().values().toArray()[index]).isEnableLogging();
		return qosServiceList.get(index).isEnableLogging();
	}
	
	public int getMarkerMap8021pSize(){
		return getMarkerMap8021pSize(qosMarkerMap);
	}
	
	private int getMarkerMap8021pSize(QosMarking map){
		if(map == null || map.getPrtclP() == null || "".equals(map.getPrtclP()) ){
			return 0;
		}else{
			return map.getPrtclP().split(":").length;
		}
	}
	
	public int getMarkerMap8021pPriority(int index){
		String res = getMarkerMap8021pPriority(index, qosMarkerMap);
		return Integer.valueOf(res);
	}
	
	public int getMarkerMap8021pClass(int index){
		return Integer.valueOf(defaultDiffKey[index]);
	}
	
	private String getMarkerMap8021pPriority(int index, QosMarking map){
		return map.getPrtclP().split(":")[index];
	}
	
	public int getMarkerMapDiffServSize(){
		return getMarkerMapDiffServSize(qosMarkerMap);
	}
	
	private int getMarkerMapDiffServSize(QosMarking map){
		if(map == null || map.getPrtclD() == null || "".equals(map.getPrtclD())){
			return 0;
		}else{
			return map.getPrtclD().split(":").length;
		}
	}
	
	public int getMarkerMapDiffServPriority(int index){
		String priority = getMarkerMapDiffServPriority(index, qosMarkerMap);
		return Integer.valueOf(priority);
	}
	
	public int getMarkerMapDiffServClass(int index){
		return Integer.valueOf(defaultDiffKey[index]);
	}
	
	private String getMarkerMapDiffServPriority(int index, QosMarking map){
		return map.getPrtclD().split(":")[index];
	}
	
	public boolean isQosPolicyDef(int index){
		return qosPolicyList.get(index).isDefaultFlag();
	}
	
	private boolean isHiveAp11n(){
		return hiveAp.is11nHiveAP();
	}
	
	private boolean isHiveAp11ac(){
		return hiveAp.is11acHiveAP();
	}
	
	public boolean isEnableQosAirTime(){
		if(Navigation.isExpressHMMode(hiveAp.getOwner())){
			return hiveAp.isEnableDas();
		}else{
			return hiveAp.getConfigTemplate().getEnableAirTime();
		}
	}
	
	public boolean isConfigOuiComment(int index){
		QosMacOui ouiObj = ((QosMacOui)qosClassMap.getQosMacOuis().values().toArray()[index]);
		return ouiObj.getComment() != null && !"".equals(ouiObj.getComment());
	}
	
	public String getOuiComment(int index){
		return ((QosMacOui)qosClassMap.getQosMacOuis().values().toArray()[index]).getComment();
//		return CLICommonFunc.addQutoTwoSide(comment);
	}
	
	public boolean isConfigQosSsidLevel(int level){
		return level >= 0;
	}

	public boolean isEnableEth0LimitBandwidth(){
		if(hiveAp.isEnabledOverrideVoipSetting()){
			return hiveAp.getEth0Interface().isEnableMaxDownload() ||
				hiveAp.getEth0Interface().isEnableMaxUpload();
		}else{
			return hiveAp.getConfigTemplate().isEnableEth0LimitDownloadBandwidth() || 
				hiveAp.getConfigTemplate().isEnableEth0LimitUploadBandwidth();
		}
	}

	public int getQosEth0DownLoadRate() {
		if(hiveAp.isEnabledOverrideVoipSetting()){
			if(hiveAp.getEth0Interface().isEnableMaxDownload()){
				return hiveAp.getEth0Interface().getMaxDownload();
			}else{
				return qosUnLimitBnadWidthRate;
			}
			
		}else{
			if(hiveAp.getConfigTemplate().isEnableEth0LimitDownloadBandwidth()){
				return hiveAp.getConfigTemplate().getEth0LimitDownloadRate();
			}else{
				return qosUnLimitBnadWidthRate;
			}
		}
	}

	public int getQosEth0UploadRate() {
		if(hiveAp.isEnabledOverrideVoipSetting()){
			if(hiveAp.getEth0Interface().isEnableMaxUpload()){
				return hiveAp.getEth0Interface().getMaxUpload();
			}else{
				return qosUnLimitBnadWidthRate;
			}
			
		}else{
			if(hiveAp.getConfigTemplate().isEnableEth0LimitUploadBandwidth()){
				return hiveAp.getConfigTemplate().getEth0LimitUploadRate();
			}else{
				return qosUnLimitBnadWidthRate;
			}
		}
	}
	
	public boolean isEnableUSBLimitBandwidth(){
		if(hiveAp.isEnabledOverrideVoipSetting()){
			return hiveAp.getUSBInterface().isEnableMaxDownload() ||
				hiveAp.getUSBInterface().isEnableMaxUpload();
			
		}else{
			return hiveAp.getConfigTemplate().isEnableUSBLimitDownloadBandwidth() ||
				hiveAp.getConfigTemplate().isEnableUSBLimitUploadBandwidth();
		}
	}
	
	public int getQosUsbDownLoadRate() {
		if(hiveAp.isEnabledOverrideVoipSetting()){
			if(hiveAp.getUSBInterface().isEnableMaxDownload()){
				return hiveAp.getUSBInterface().getMaxDownload();
			}else{
				return qosUnLimitBnadWidthRate;
			}
		}else{
			if(hiveAp.getConfigTemplate().isEnableUSBLimitDownloadBandwidth()){
				return hiveAp.getConfigTemplate().getUsbLimitDownloadRate();
			}else{
				return qosUnLimitBnadWidthRate;
			}
			
		}
	}

	public int getQosUsbUploadRate() {
		if(hiveAp.isEnabledOverrideVoipSetting()){
			if(hiveAp.getUSBInterface().isEnableMaxUpload()){
				return hiveAp.getUSBInterface().getMaxUpload();
			}else{
				return qosUnLimitBnadWidthRate;
			}
			
		}else{
			if(hiveAp.getConfigTemplate().isEnableUSBLimitUploadBandwidth()){
				return hiveAp.getConfigTemplate().getUsbLimitUploadRate();
			}else{
				return qosUnLimitBnadWidthRate;
			}
			
		}
	}
	
	public boolean isConfigTunnel(){
		if(hiveAp.isEnabledOverrideVoipSetting()){
			return  hiveAp.getUSBInterface().isEnableMaxDownload() && hiveAp.getUSBInterface().getMaxDownload() > 0 ||
			hiveAp.getEth0Interface().isEnableMaxDownload() && hiveAp.getEth0Interface().getMaxDownload() > 0;
		}else{
			return hiveAp.getConfigTemplate().isEnableEth0LimitDownloadBandwidth() && hiveAp.getConfigTemplate().getEth0LimitDownloadRate() > 0 ||
			hiveAp.getConfigTemplate().isEnableUSBLimitDownloadBandwidth() && hiveAp.getConfigTemplate().getUsbLimitDownloadRate() > 0;
		}
	}
	
	public int getClassifierMapInterfaceSize(){
		if(qosInterfaces == null){
			return 0;
		}else{
			return qosInterfaces.size();
		}
	}
	
	public String getClassifierMapInterfaceName(int index){
		return qosInterfaces.get(index).getInfName();
	}
	
	public int getClassifierMapInterfacePriority(int index){
		return qosInterfaces.get(index).getPriority();
	}
	
	public boolean isQosEnable(){
		if(hiveAp.getDeviceInfo().isSptEthernetMore_24()){
			return isSwitchQosEnable(this.hiveAp);
		}else{
			return true;
		}
	}
	
	private class QosInterface{
		private String infName;
		private int priority;
		public String getInfName() {
			return infName;
		}
		public void setInfName(String infName) {
			this.infName = infName;
		}
		public int getPriority() {
			return priority;
		}
		public void setPriority(int priority) {
			this.priority = priority;
		}
	}
	
	public int getUD8021pSize() {
		return markerMap8021p.size();
	}
	
	public int getUDDiffservSize() {
		return markerMapDiffserv.size();
	}
	
	public String getUD8021pName(int i) {
		return markerMap8021p.get(i).getQosName();
	}
	
	public String getUDDiffservName(int i) {
		return markerMapDiffserv.get(i).getQosName();
	}
	
	public int getUD8021pContentSize(int i) {
		QosMarking map8021p = markerMap8021p.get(i);
		return this.getMarkerMap8021pSize(map8021p);
	}
	
	public int getUDDiffservContentSize(int i) {
		QosMarking mapDiffserv = markerMapDiffserv.get(i);
		return this.getMarkerMapDiffServSize(mapDiffserv);
	}
	
	public int getUD8021pContentClass(int i, int j){
		return getMarkerMap8021pClass(j);
	}
	
	public int getUD8021pContentPriority(int i, int j){
		QosMarking map8021p = markerMap8021p.get(i);
		String res = getMarkerMap8021pPriority(j, map8021p);
		return Integer.valueOf(res);
	}
	
	public int getUDDiffservContentClass(int i, int j){
		return Integer.valueOf(defaultDiffKey[j]);
	}
	
	public int getUDDiffservContentPriority(int i, int j){
		QosMarking mapDiffserv = markerMapDiffserv.get(i);
		String priority = getMarkerMapDiffServPriority(j, mapDiffserv);
		return Integer.valueOf(priority);
	}
}
