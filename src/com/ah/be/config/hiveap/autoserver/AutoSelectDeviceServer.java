package com.ah.be.config.hiveap.autoserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hsqldb.lib.StringUtil;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.DeviceDaInfo;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.ui.actions.hiveap.HiveApUpdateAction;
import com.ah.util.Tracer;

public class AutoSelectDeviceServer {
	
	private static final Tracer log = new Tracer(HiveApUpdateAction.class
			.getSimpleName());
	
	private static final short IDM_PROXY_COUNTS = 2;

	private static AutoSelectDeviceServer instance;
	
	private Map<String, DAGroup> daNetworkMap = new HashMap<String, DAGroup>();
	
	private Map<Long, DeviceDaInfo> daInfoMap = new HashMap<Long, DeviceDaInfo>();
	
	private AutoSelectDeviceServer(){}
	
	public static AutoSelectDeviceServer getInstance(){
		if(instance == null){
			instance = new AutoSelectDeviceServer();
		}
		return instance;
	}
	
	public synchronized void autoSelectIDManagerProxy(Collection<Long> ids, boolean errorContinue) throws Exception{
		try{
			if(ids == null || ids.isEmpty()){
				return;
			}
			
			initialize(ids);
			
			for(DAGroup daGroup : daNetworkMap.values()){
				try{
//					if(daGroup.getIdmanagerProxys() != null && daGroup.getIdmanagerProxys().size() == IDM_PROXY_COUNTS){
//						continue;
//					}else{
//						
//					}
					selectIDMProxy(daGroup, ids, IDM_PROXY_COUNTS);
				}catch(Exception e){
					if(!errorContinue){
						throw e;
					}
				}
			}
		}catch(AhAutoServerException ex){
			throw ex;
		}catch(Exception ex){
			log.error(ex.getMessage(), ex);
			throw new AhAutoServerException(NmsUtil.getUserMessage("error.unknown") + " " + 
					ex.getMessage());
		}
		
	}
	
	private void selectIDMProxy(DAGroup group, Collection<Long> selectedIds, int counts) throws Exception{
		if(counts < 1){
			return;
		}
		
		List<DeviceDaInfo> proxySelectList = new ArrayList<>();
		for(DeviceDaInfo daInfo : group.getMemberList()){
			
			//Simulated device cannot use for a proxy server.
			if(daInfo.getHiveAp().isSimulated()){
				continue;
			}
			
			//BR100 and Switch cannot use for a proxy server.
			if(daInfo.getHiveAp().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 || 
					daInfo.getHiveAp().getDeviceInfo().isSptEthernetMore_24()){
				continue;
			}
			
			//Device version before 5.1.3.0 cannot use for a proxy server.
			if(NmsUtil.compareSoftwareVersion("5.1.3.0", daInfo.getHiveAp().getSoftVer()) > 0){
				continue;
			}
			
			//Device version after 6.2.1.0 device will select radsec proxy auto.
			if(NmsUtil.compareSoftwareVersion(daInfo.getHiveAp().getSoftVer(), "6.2.1.0") >= 0){
				continue;
			}
			
			//Disconnect device cannot use for proxy server
			if(!daInfo.getHiveAp().isConnected()){
				continue;
			}
			
			//No IP address device cannot use as proxy server.
			if(StringUtil.isEmpty(daInfo.getHiveAp().getIpAddress())){
				continue;
			}
			
			//Temp delete BDA cannot as proxy
			if(daInfo.isBDA()){
				continue;
			}
			
			//whether enable IDM on ssid or port profile.
			if(!isDeivceEnableIdm(daInfo.getHiveAp())){
				continue;
			}
			
			daInfo.initPriority();
			proxySelectList.add(daInfo);
		}
		
		Collections.sort(proxySelectList);
		
		int selectCounts = counts < proxySelectList.size() ? counts : proxySelectList.size();
		
		List<Long> addIdmList = new ArrayList<>();
		for(DeviceDaInfo daInfo : proxySelectList){
			if(selectCounts > 0){
				if(daInfo.getHiveAp().isIDMProxy()){
					//already proxy
					selectCounts--;
					addIdmList.add(daInfo.getHiveAp().getId());
				}else if(selectedIds.contains(daInfo.getHiveAp().getId())){
					//not proxy, and in selected list.
					daInfo.getHiveAp().setIDMProxy(true);
					addIdmList.add(daInfo.getHiveAp().getId());
					selectCounts--;
				}else{
					//ignore
				}
			}else{
				break;
			}
		}
		
		if(!addIdmList.isEmpty()){
			QueryUtil.updateBo(HiveAp.class, "IDMProxy = true", new FilterParams("id in (:s1)", new Object[]{addIdmList}) );
		}
		
		List<Long> removeIdmList = new ArrayList<>();
		for(DeviceDaInfo daInfo : group.getMemberList()){
			Long rmId = daInfo.getHiveAp().getId();
			if(!addIdmList.contains(rmId)){
				removeIdmList.add(rmId);
			}
		}
		if(!removeIdmList.isEmpty()){
			QueryUtil.updateBo(HiveAp.class, "IDMProxy = false", new FilterParams("id in (:s1)", new Object[]{removeIdmList}) );
		}
	}
	
	private boolean isDeivceEnableIdm(HiveAp hiveAp){
		if(hiveAp == null){
			return false;
		}
		
		boolean ssidEnableIdm = false;
		boolean ethernetEnableIdm = false;
		
		//iterator all ssid
		if(hiveAp.getConfigTemplate() != null 
				&& hiveAp.getConfigTemplate().getSsidInterfaces() != null 
				&& hiveAp.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_RADIO_COUNTS) > 0){
			for(ConfigTemplateSsid ssidItem : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
				if(ssidItem.getSsidProfile() == null){
					continue;
				}
				if(ssidItem.getSsidProfile().isEnabledIDM()){
					ssidEnableIdm = true;
					break;
				}
			}
		}
		
		//iterator all port profile
		PortGroupProfile portProfile = hiveAp.getPortGroup();
		if(portProfile != null && portProfile.getBasicProfiles() != null){
			for(PortBasicProfile baseProfile : portProfile.getBasicProfiles()){
				if(baseProfile.getAccessProfile() != null && baseProfile.getAccessProfile().isEnabledIDM()){
					ethernetEnableIdm = true;
					break;
				}
			}
		}
		
		return ssidEnableIdm || ethernetEnableIdm;
	}
	
	private void initialize(Collection<Long> ids) throws Exception{
		if(ids == null || ids.isEmpty()){
			return;
		}
		
		daNetworkMap.clear();
		daInfoMap.clear();
		
		Map<String, HiveAp> deviceMap = new HashMap<>();
		
		List<HiveAp> apList = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams("id", ids), 
				null, new ConfigLazyQueryBo.SimpllyHiveAp() );
		for(HiveAp ap : apList){
			deviceMap.put(ap.getMacAddress(), ap);
		}
		
		List<DeviceDaInfo> allDaInfoList = QueryUtil.executeQuery(DeviceDaInfo.class, null, 
				new FilterParams("macAddress", deviceMap.keySet()) );
		for(DeviceDaInfo daInfo : allDaInfoList){
			daInfo.setHiveAp(deviceMap.get(daInfo.getMacAddress()));
			daInfoMap.put(daInfo.getHiveAp().getId(), daInfo);
			addInDANetworkMap(daInfo);
		}
	}
	
	private void addInDANetworkMap(DeviceDaInfo daInfo){
		String key = daInfo.getDAKey();
		DAGroup group = daNetworkMap.get(key);
		if(group == null){
			group = new DAGroup();
			daNetworkMap.put(key, group);
		}
		group.addMember(daInfo);
		if(group.getDaMac() == null){
			group.setDaMac(daInfo.getDAMac());
		}
		if(group.getBdaMac() == null){
			group.setBdaMac(daInfo.getBDAMac());
		}
		if(daInfo.getHiveAp().isIDMProxy()){
			group.getIdmanagerProxys().add(daInfo.getMacAddress());
		}
	}
	
	class DAGroup{
		
		private List<DeviceDaInfo> memberList = new ArrayList<DeviceDaInfo>();
		
		private String daMac;

		private String bdaMac;
		
		private Set<String> idmanagerProxys;
		
		public String getDaMac() {
			return daMac;
		}

		public void setDaMac(String daMac) {
			this.daMac = daMac;
		}

		public String getBdaMac() {
			return bdaMac;
		}

		public void setBdaMac(String bdaMac) {
			this.bdaMac = bdaMac;
		}

		public Set<String> getIdmanagerProxys() {
			if(idmanagerProxys == null){
				idmanagerProxys = new HashSet<String>();
			}
			return idmanagerProxys;
		}
		
		public void addMember(DeviceDaInfo daInfo){
			memberList.add(daInfo);
		}
		
		public List<DeviceDaInfo> getMemberList(){
			return memberList;
		}
	}
}
