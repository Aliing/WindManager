package com.ah.ui.actions.hiveap;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpSessionBindingEvent;

import org.json.JSONObject;

import com.ah.be.app.AhAppContainer;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateType;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.PagingCache;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.CLIBlob;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnService;
import com.ah.bo.performance.AhLatestRadioAttribute;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.wlan.RadioProfile;
import com.ah.integration.airtight.SyncProgressSubscriber;
import com.ah.integration.airtight.SyncProgressEvent;
import com.ah.util.Tracer;

public class HiveApPagingCache extends PagingCache<HiveAp> implements
		SyncProgressSubscriber {

	private static final Tracer log = new Tracer(HiveApPagingCache.class
			.getSimpleName());

	private SyncProgressEvent syncProgressEvent;

	public HiveApPagingCache(HmUser user) {
		super(HiveAp.class, user);
	}

	public SyncProgressEvent getSyncProgressEvent() {
		return syncProgressEvent;
	}

	public void startSyncProgress() {
		syncProgressEvent = null;
		AhAppContainer.getBeMiscModule().getAirTightSgeIntegrator().submit(this);
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		log.info("valueUnbound", "Unbound event: " + event.getName());
	}

	@Override
	public Collection<JSONObject> getUpdates(int cacheId) throws Exception {
		Collection<JSONObject> updates = new Vector<JSONObject>();
		if (getCacheId() != cacheId) {
			// Invalid refresh request
			return updates;
		}
		List<HiveAp> newHiveAps = findBos();
		if (hmBos.size() != newHiveAps.size()) {
			// full refresh
			return refreshFromCache(newHiveAps);
		}
		for (int i = 0; i < hmBos.size(); i++) {
			HiveAp hiveAp = hmBos.get(i);
			HiveAp newHiveAp = newHiveAps.get(i);
			if (!hiveAp.getId().equals(newHiveAp.getId())
					|| hiveAp.getConnectStatus() != newHiveAp.getConnectStatus()
					|| hiveAp.getManageStatus() != newHiveAp.getManageStatus()
					|| hiveAp.getPendingIndex() != newHiveAp.getPendingIndex()) {
				// full refresh
				return refreshFromCache(newHiveAps);
			}
		}
		hmBos = newHiveAps;
		return updates;
	}

	@Override
	public void progressUpdated(SyncProgressEvent event) {
		this.syncProgressEvent = event;
	}

	public static void queryLazyInfo(List<HiveAp> hiveAps) {
		if (hiveAps.isEmpty()) {
			return;
		}
		Map<Long, HiveAp> hiveApMap = new HashMap<Long, HiveAp>(hiveAps.size());
		for (HiveAp hiveAp : hiveAps) {
			hiveApMap.put(hiveAp.getId(), hiveAp);
			// Replace the 'LAZY' template with an empty one
			ConfigTemplate configTemplate = new ConfigTemplate(ConfigTemplateType.WIRELESS);
			HiveProfile hive = new HiveProfile();
			configTemplate.setHiveProfile(hive);
			Vlan vlan = new Vlan();
			configTemplate.setVlan(vlan);
			vlan = new Vlan();
			configTemplate.setVlanNative(vlan);
			CLIBlob cliBlob = new CLIBlob();
			configTemplate.setSupplementalCLI(cliBlob);
			hiveAp.setConfigTemplate(configTemplate);
			RadioProfile wifi0Radio = new RadioProfile();
			RadioProfile wifi1Radio = new RadioProfile();
			hiveAp.setWifi0RadioProfile(wifi0Radio);
			hiveAp.setWifi1RadioProfile(wifi1Radio);
			
			VpnNetwork cvgMgtNetwork = new VpnNetwork();
			Vlan cvgVlan = new Vlan();
			// TODO for remove network object in user profile
//			cvgMgtNetwork.setVlan(cvgVlan);
			hiveAp.getOrCreateCvgDPD().setMgtNetwork(cvgMgtNetwork);
			
			VpnNetwork networkMgtNetwork = new VpnNetwork();
			Vlan networkMgtVlan = new Vlan();
			// TODO for remove network object in user profile
//			networkMgtNetwork.setVlan(networkMgtVlan);
//			hiveAp.getConfigTemplate().setMgtNetwork(networkMgtNetwork);
			
			CLIBlob cliBlobAP = new CLIBlob();
			hiveAp.setSupplementalCLI(cliBlobAP);
		
			MapContainerNode mapContainerNode = new MapContainerNode();
			if(hiveAp.getMapContainer() != null){
				hiveAp.getMapContainer().setParentMap(mapContainerNode);
			}
			
		}
		
		StringBuilder idLstStr = new StringBuilder();
		for(Long idObj:hiveApMap.keySet()){
			if (idLstStr.length()!=0) {
				idLstStr.append(",");
			}
			idLstStr.append(idObj.toString());
		}
		// Query DHCP
		String queryDhcp = "select hiveap_id, enabledhcp from hiveap_device_interface where " +
				" hiveap_id in (" + idLstStr.toString() + ") and deviceIfType = " + AhInterface.DEVICE_IF_TYPE_ETH0;
		List<?> apList = QueryUtil.executeNativeQuery(queryDhcp);
		for (Object obj : apList) {
			Object[] oneObj = (Object[]) obj;
			Long idObj = Long.valueOf(oneObj[0].toString());
			boolean enDhcp = true;
			if (oneObj[1]==null || oneObj[1].toString().equals("") || !Boolean.valueOf(oneObj[1].toString())){
				enDhcp = false;
			}
			HiveAp hiveAp = hiveApMap.get(idObj);
			if (hiveAp != null) {
				if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
					hiveAp.setDhcp(enDhcp);
				}
			}
		}
		
		// Query for the template names, radius server, vpn server, radius proxy
		// server
		String query = "select bo.id, bo.configTemplate.configName, bo.configTemplate.hiveProfile.hiveName, "
				+ "bo.configTemplate.vlan.vlanName, bo.configTemplate.vlanNative.vlanName, bo.radiusServerProfile.id, "
				+ "bo.configTemplate.vpnService.id, bo.radiusProxyProfile.id, "
				+ "bo.configTemplate.radiusServerProfile.id, bo.configTemplate.radiusProxyProfile.id, "
				+ "bo.wifi0RadioProfile.radioName, bo.wifi1RadioProfile.radioName,bo.configTemplate.id "
				+ "from "
				+ HiveAp.class.getSimpleName() + " bo";
		List<?> templates = QueryUtil.executeQuery(query, null,
				new FilterParams("id", hiveApMap.keySet()));
		Set<Long> swId = new HashSet<Long>();
		swId.addAll(hiveApMap.keySet());
		if (!templates.isEmpty()) {
			for (Object obj : templates) {
				Object[] template = (Object[]) obj;
				swId.remove((Long)template[0]);
			}
		}
		List<?> templates1 = null;
		if (!swId.isEmpty()) {
			String query1 = "select bo.id, bo.configTemplate.configName, bo.configTemplate.hiveProfile.hiveName, "
					+ "bo.configTemplate.vlan.vlanName, bo.configTemplate.vlanNative.vlanName, bo.radiusServerProfile.id, "
					+ "bo.configTemplate.vpnService.id, bo.radiusProxyProfile.id, "
					+ "bo.configTemplate.radiusServerProfile.id, bo.configTemplate.radiusProxyProfile.id, "
					+ "bo.configTemplate.id "
					+ "from "
					+ HiveAp.class.getSimpleName() + " bo";
			
			templates1 = QueryUtil.executeQuery(query1, null,
					new FilterParams("id", swId));
		}
		// Fill in the template names
		for (Object obj : templates) {
			Object[] template = (Object[]) obj;
			Long id = (Long) template[0];
			String tempName = (String) template[1];
			String hiveName = (String) template[2];
			String vlanName = (String) template[3];
			String vlanNativeName = (String) template[4];
			Long radiusId = (Long) template[5];
			Long vpnId = (Long) template[6];
			Long radiusProxyId = (Long) template[7];
			Long radiusIdInWlan = (Long) template[8];
			Long radiusProxyIdInWlan = (Long) template[9];
			String wifi0RadioName = (String) template[10];
			String wifi1RadioName = (String) template[11];
			Long tempId = (Long) template[12];
			HiveAp hiveAp = hiveApMap.get(id);
			if (hiveAp != null) {
				hiveAp.getConfigTemplate().setConfigName(tempName);
				hiveAp.getConfigTemplate().setId(tempId);
				hiveAp.getConfigTemplate().getHiveProfile().setHiveName(
						hiveName);
				hiveAp.getConfigTemplate().getVlan().setVlanName(vlanName);
				hiveAp.getConfigTemplate().getVlanNative().setVlanName(
						vlanNativeName);
				if (null != radiusId) {// set RADIUS Profile;
					RadiusOnHiveap radius = new RadiusOnHiveap();
					radius.setId(radiusId);
					hiveAp.setRadiusServerProfile(radius);
				}
				if (null != vpnId) {// set VPN Profile
					VpnService vpn = new VpnService();
					vpn.setId(vpnId);
					hiveAp.getConfigTemplate().setVpnService(vpn);
				}
				if (null != radiusProxyId) {// set RADIUS Proxy Profile;
					RadiusProxy radius = new RadiusProxy();
					radius.setId(radiusProxyId);
					hiveAp.setRadiusProxyProfile(radius);
				}
				
				if (null != radiusIdInWlan) {// set RADIUS Profile;
					RadiusOnHiveap radius = new RadiusOnHiveap();
					radius.setId(radiusId);
					hiveAp.getConfigTemplate().setRadiusServerProfile(radius);
				}
				
				if (null != radiusProxyIdInWlan) {// set RADIUS Proxy Profile;
					RadiusProxy radius = new RadiusProxy();
					radius.setId(radiusProxyId);
					hiveAp.getConfigTemplate().setRadiusProxyProfile(radius);
				}
				if (null != wifi0RadioName) {
					hiveAp.getWifi0RadioProfile().setRadioName(wifi0RadioName);
				}
				if (null != wifi1RadioName) {
					hiveAp.getWifi1RadioProfile().setRadioName(wifi1RadioName);
				}
			}
		}
		if (templates1!=null) {
			for (Object obj : templates1) {
				Object[] template = (Object[]) obj;
				Long id = (Long) template[0];
				String tempName = (String) template[1];
				String hiveName = (String) template[2];
				String vlanName = (String) template[3];
				String vlanNativeName = (String) template[4];
				Long radiusId = (Long) template[5];
				Long vpnId = (Long) template[6];
				Long radiusProxyId = (Long) template[7];
				Long radiusIdInWlan = (Long) template[8];
				Long radiusProxyIdInWlan = (Long) template[9];
				Long tempId = (Long) template[10];
				HiveAp hiveAp = hiveApMap.get(id);
				if (hiveAp != null) {
					hiveAp.getConfigTemplate().setConfigName(tempName);
					hiveAp.getConfigTemplate().setId(tempId);
					hiveAp.getConfigTemplate().getHiveProfile().setHiveName(
							hiveName);
					hiveAp.getConfigTemplate().getVlan().setVlanName(vlanName);
					hiveAp.getConfigTemplate().getVlanNative().setVlanName(
							vlanNativeName);
					if (null != radiusId) {// set RADIUS Profile;
						RadiusOnHiveap radius = new RadiusOnHiveap();
						radius.setId(radiusId);
						hiveAp.setRadiusServerProfile(radius);
					}
					if (null != vpnId) {// set VPN Profile
						VpnService vpn = new VpnService();
						vpn.setId(vpnId);
						hiveAp.getConfigTemplate().setVpnService(vpn);
					}
					if (null != radiusProxyId) {// set RADIUS Proxy Profile;
						RadiusProxy radius = new RadiusProxy();
						radius.setId(radiusProxyId);
						hiveAp.setRadiusProxyProfile(radius);
					}
					
					if (null != radiusIdInWlan) {// set RADIUS Profile;
						RadiusOnHiveap radius = new RadiusOnHiveap();
						radius.setId(radiusId);
						hiveAp.getConfigTemplate().setRadiusServerProfile(radius);
					}
					
					if (null != radiusProxyIdInWlan) {// set RADIUS Proxy Profile;
						RadiusProxy radius = new RadiusProxy();
						radius.setId(radiusProxyId);
						hiveAp.getConfigTemplate().setRadiusProxyProfile(radius);
					}
				}
			}
		}
		
		query = "select ap.id, v1.vlanName as cvgVlan,  v2.vlanName as brVlan "
				+ "from  hive_ap ap "
				+ "left join VLAN v1 on ap.CVG_MGT0_VLAN_ID = v1.id "
				+ "left join CONFIG_TEMPLATE c on ap.TEMPLATE_ID = c.id "
				+ "left join VLAN v2 on c.VLAN_ID = v2.id "
				+ "where ap.id in (" + idLstStr.toString() + ")";
		apList = QueryUtil.executeNativeQuery(query);
		for (Object obj : apList) {
			Object[] oneObj = (Object[]) obj;
			Long id = Long.valueOf(oneObj[0].toString());
			Object cvgNetworkVlan = oneObj[1];
			Object brNetworkVlan = oneObj[2];
			HiveAp hiveAp = hiveApMap.get(id);
			if (hiveAp != null) {
				if (null != cvgNetworkVlan){
					// TODO for remove network object in user profile
//					hiveAp.getOrCreateCvgDPD().getMgtNetwork().getVlan().setVlanName(cvgNetworkVlan.toString());
				}
				if (null != brNetworkVlan){
					// TODO for remove network object in user profile
//					hiveAp.getConfigTemplate().getMgtNetwork().getVlan().setVlanName(brNetworkVlan.toString());
				}
			}
		}
		
		query = "select ap.id,m2.mapName as mapName "
			   + "from  hive_ap ap left join MAP_NODE m1 on ap.MAP_CONTAINER_ID = m1.id "
			   + "left join MAP_NODE m2 on m1.PARENT_MAP_ID = m2.id ";
	    apList = QueryUtil.executeNativeQuery(query);
		for (Object obj : apList) {
		   Object[] oneObj = (Object[]) obj;
		   Long id = Long.valueOf(oneObj[0].toString());
		   Object parentMapName = oneObj[1];
		   HiveAp hiveAp = hiveApMap.get(id);
		   if (hiveAp != null && hiveAp.getMapContainer() != null) {
			   if(null != parentMapName){
				   hiveAp.getMapContainer().getParentMap().setMapName(parentMapName.toString());
			   }
		   }
	    }
		
	
		query = "select ap.id, v1.supplementalName as supplementalNameAP,  v2.supplementalName as supplementalNameNP,c.id as configId,c.supplemental_cli_id "
				+ "from  hive_ap ap "
				+ "left join cli_blob v1 on ap.supplemental_cli_id = v1.id "
				+ "left join CONFIG_TEMPLATE c on ap.TEMPLATE_ID = c.id "
				+ "left join cli_blob v2 on c.supplemental_cli_id = v2.id "
				+ "where ap.id in (" + idLstStr.toString() + ")";
		apList = QueryUtil.executeNativeQuery(query);
		for (Object obj : apList) {
			Object[] oneObj = (Object[]) obj;
			Long id = Long.valueOf(oneObj[0].toString());
			Object supplementalNameAP = oneObj[1];
			Object supplementalNameNP = oneObj[2];
			HiveAp hiveAp = hiveApMap.get(id);
			if (hiveAp != null) {
				if(null != supplementalNameAP){
					hiveAp.getSupplementalCLI().setSupplementalName(supplementalNameAP.toString());
				}else{
					if(null != supplementalNameNP){
						hiveAp.getConfigTemplate().getSupplementalCLI().setSupplementalName(supplementalNameNP.toString());
					}
				}
				
			}
		}
		
	}
	
	public static void filledChannelPowers(List<HiveAp> hiveAps) {
		if (null == hiveAps || hiveAps.isEmpty()) {
			return;
		}
		Map<String, HiveAp> hiveApMap = new HashMap<String, HiveAp>();
		for (HiveAp hiveAp : hiveAps) {
			hiveApMap.put(hiveAp.getMacAddress(), hiveAp);
		}
		List<AhLatestXif> radioList = QueryUtil.executeQuery(AhLatestXif.class,
				null, new FilterParams("apMac", hiveApMap.keySet()));
		List<AhLatestRadioAttribute> attributeList = QueryUtil.executeQuery(
				AhLatestRadioAttribute.class, null, new FilterParams("apMac",
						hiveApMap.keySet()));
		Map<String, String> indexNameMapping = new HashMap<String, String>(
				radioList.size());
		for (AhLatestXif xif : radioList) {
			String mac = xif.getApMac();
			indexNameMapping.put(mac + xif.getIfIndex(), xif.getIfName());
		}
		for (AhLatestRadioAttribute attributes : attributeList) {
			String mac = attributes.getApMac();
			String wifiName = indexNameMapping.get(mac
					+ attributes.getIfIndex());
			if ("wifi0".equalsIgnoreCase(wifiName)) {
				hiveApMap
						.get(mac)
						.getWifi0()
						.setRunningChannel(
								String.valueOf(attributes.getRadioChannel()));
				hiveApMap
						.get(mac)
						.getWifi0()
						.setRunningPower(
								String.valueOf(attributes.getRadioTxPower())
										+ " dBm");
			} else if ("wifi1".equalsIgnoreCase(wifiName)) {
				hiveApMap
						.get(mac)
						.getWifi1()
						.setRunningChannel(
								String.valueOf(attributes.getRadioChannel()));
				hiveApMap
						.get(mac)
						.getWifi1()
						.setRunningPower(
								String.valueOf(attributes.getRadioTxPower())
										+ " dBm");
			}
		}
	}

}