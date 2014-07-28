package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.network.NetworkService;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.source.ServiceProfileInt;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.util.MgrUtil;
import com.ah.xml.be.config.AlgValue;

/**
 * 
 * @author zhang
 *
 */
@SuppressWarnings("static-access")
public class ServiceProfileImpl implements ServiceProfileInt {
	
	private final NetworkService netWorkService;
	private final HiveAp hiveAp;
	private static Map<String, NetworkService> serviceDefMap;
	
	public ServiceProfileImpl(NetworkService netWorkService, HiveAp hiveAp){
		this.netWorkService = netWorkService;
		this.hiveAp = hiveAp;
		
		if(serviceDefMap == null){
			serviceDefMap = new HashMap<String, NetworkService>();
			List<NetworkService> boList = MgrUtil.getQueryEntity().executeQuery(NetworkService.class, null, new FilterParams("cliDefaultFlag", true));
			for(NetworkService serviceObj : boList){
				serviceDefMap.put(getServiceDefKey(serviceObj.getProtocolNumber(), serviceObj.getPortNumber()), serviceObj);
			}
		}
	}
	
	public NetworkService getNetworkService(){
		return this.netWorkService;
	}
	
	public String getServiceGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.networkService");
	}
	
	public static String getServiceDefKey(int protocol, int port){
		return protocol + "_" + port;
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	public boolean isConfigServices(){
		return netWorkService!=null && !netWorkService.isCliDefaultFlag();
	}
	
	public String getUpdateTime(){
		List<Object> serviceList = new ArrayList<Object>();
		serviceList.add(netWorkService);
		return CLICommonFunc.getLastUpdateTime(serviceList);
	}
	
	public String getServiceName(){
		return netWorkService.getServiceName();
	}
	
	public boolean isConfigServiceProtocol(){
		short proType = netWorkService.getProtocolId();
		return netWorkService.getServiceType() == NetworkService.SERVICE_TYPE_NETWORK && 
			(proType == NetworkService.PROTOCOL_ID_CUSTOM ||
			proType == NetworkService.PROTOCOL_ID_SVP ||
			proType == NetworkService.PROTOCOL_ID_TCP ||
			proType == NetworkService.PROTOCOL_ID_UDP) ;
	}
	
	public String getProtocolValue(){
		short serviceType = netWorkService.getProtocolId();
		String restValue = "";
		if(serviceType == NetworkService.PROTOCOL_ID_CUSTOM){
			switch(netWorkService.getProtocolNumber()){
				case 6 : restValue = SERVICE_TYPE.tcp.name(); break;
				case 17 : restValue = SERVICE_TYPE.udp.name(); break;
				case 119 : restValue = SERVICE_TYPE.svp.name(); break;
				default : restValue = String.valueOf(netWorkService.getProtocolNumber());
			}
		}else if(serviceType == NetworkService.PROTOCOL_ID_SVP){
			restValue = SERVICE_TYPE.svp.name();
		}else if(serviceType == NetworkService.PROTOCOL_ID_TCP){
			restValue = SERVICE_TYPE.tcp.name();
		}else if(serviceType == NetworkService.PROTOCOL_ID_UDP){
			restValue = SERVICE_TYPE.udp.name();
		}
		return restValue;
	}
	
	public int getServicePort(){
		if(netWorkService.getProtocolId() == NetworkService.PROTOCOL_ID_TCP ||
				netWorkService.getProtocolId() == NetworkService.PROTOCOL_ID_UDP){
			return netWorkService.getPortNumber();
		}else{
			return 65535;
		}
	}
	
	public int getServiceTimeOut(){
		return netWorkService.getIdleTimeout();
	}
	
	public boolean isConfigAlg() {
		return netWorkService.getAlgType() > 0 && netWorkService.getAlgType() != NetworkService.ALG_TYPE_NONE;
	}
	
	public AlgValue getServiceAlgType(){
		short algValue = netWorkService.getAlgType();
		switch(algValue){
			case NetworkService.ALG_TYPE_FTP:
				return AlgValue.FTP;
			case NetworkService.ALG_TYPE_SIP:
				return AlgValue.SIP;
			case NetworkService.ALG_TYPE_TFTP:
				return AlgValue.TFTP;
			case NetworkService.ALG_TYPE_DNS:
				if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.5.3.0") >= 0){
					 return AlgValue.DNS;
				}else{
					return null;
				}
			case NetworkService.ALG_TYPE_HTTP:
				if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "4.0.1.0") >= 0){
					return AlgValue.HTTP;
				}else if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.5.3.0") >= 0){
					return AlgValue.TV;
				}else{
					return null;
				}
			default:
				return null;
		}
	}
	
	public boolean isSpecialDefValue(){
		String key = getServiceDefKey(netWorkService.getProtocolNumber(), netWorkService.getPortNumber());
		NetworkService defService = serviceDefMap.get(key);
		return defService != null && defService.getIdleTimeout() == netWorkService.getIdleTimeout();
	}
	
	public boolean isConfigPort(){
		return netWorkService.getProtocolId() == NetworkService.PROTOCOL_ID_TCP ||
		netWorkService.getProtocolId() == NetworkService.PROTOCOL_ID_UDP;
	}
	
	public boolean isConfigAppid(){
		return netWorkService.getServiceType() == NetworkService.SERVICE_TYPE_L7;
	}
	
	public String getAppid(){
		return String.valueOf(netWorkService.getAppId());
	}
	
}
