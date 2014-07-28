package com.ah.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateStormControl;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.StpSettings;
import com.ah.bo.network.SwitchSettings;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.UserProfileVlanMapping;

public class CloneUtil {
	
	/**
	 * clone fields of configTemplate(networkPolicy)
	 * @param source
	 * @param destination
	 * @throws CloneNotSupportedException 
	 */
	public static void setConfigTemplateCloneFields(ConfigTemplate source, ConfigTemplate destination) throws CloneNotSupportedException{
		Map<Long, ConfigTemplateSsid> cloneSsidInterfaces = new HashMap<Long, ConfigTemplateSsid>();
		for (ConfigTemplateSsid tempClass : source.getSsidInterfaces().values()) {
			if (tempClass.getSsidProfile() != null) {
				cloneSsidInterfaces.put(tempClass.getSsidProfile().getId(), tempClass);
			} else if (MgrUtil.getUserMessage("config.configTemplate.eth0").equals(
					tempClass.getInterfaceName())) {
				cloneSsidInterfaces.put((long) -1, tempClass);
			} else if (MgrUtil.getUserMessage("config.configTemplate.eth1").equals(
					tempClass.getInterfaceName())) {
				cloneSsidInterfaces.put((long) -2, tempClass);
			} else if (MgrUtil.getUserMessage("config.configTemplate.red0").equals(
					tempClass.getInterfaceName())) {
				cloneSsidInterfaces.put((long) -3, tempClass);
			} else if (MgrUtil.getUserMessage("config.configTemplate.eth2").equals(
					tempClass.getInterfaceName())) {
				cloneSsidInterfaces.put((long) -5, tempClass);
			} else if (MgrUtil.getUserMessage("config.configTemplate.eth3").equals(
					tempClass.getInterfaceName())) {
				cloneSsidInterfaces.put((long) -6, tempClass);
			} else if (MgrUtil.getUserMessage("config.configTemplate.eth4").equals(
					tempClass.getInterfaceName())) {
				cloneSsidInterfaces.put((long) -7, tempClass);
			} else {
				cloneSsidInterfaces.put((long) -4, tempClass);
			}
		}
		 destination.setSsidInterfaces(cloneSsidInterfaces);

		Set<MgmtServiceIPTrack> cloneIpTracks = new HashSet<MgmtServiceIPTrack>();
		for (MgmtServiceIPTrack tempClass : source.getIpTracks()) {
			cloneIpTracks.add(tempClass);
		}
		destination.setIpTracks(cloneIpTracks);
		 
		Set<NetworkService> cloneNetworkServices = new HashSet<NetworkService>();
		for (NetworkService tempClass : source.getTvNetworkService()) {
			cloneNetworkServices.add(tempClass);
		}
		destination.setTvNetworkService(cloneNetworkServices);
		
		Set<PortGroupProfile> clonePortTemplateProfiles = new HashSet<PortGroupProfile>();
		for (PortGroupProfile tempClass : source.getPortProfiles()) {
			clonePortTemplateProfiles.add(tempClass);
		}
		destination.setPortProfiles(clonePortTemplateProfiles);
		
		List<ConfigTemplateVlanNetwork> vlanNetwork = new ArrayList<ConfigTemplateVlanNetwork>();
		for (ConfigTemplateVlanNetwork tempClass : source.getVlanNetwork()) {
			vlanNetwork.add(tempClass);
		}
		destination.setVlanNetwork(vlanNetwork);
		
		Set<UserProfileVlanMapping> upVlanMapping = new HashSet<UserProfileVlanMapping>();
		for (UserProfileVlanMapping tempClass : source.getUpVlanMapping()) {
			upVlanMapping.add(new UserProfileVlanMapping(tempClass.getUserProfile(), tempClass.getVlan(), destination, destination.getOwner()));
		}
		destination.setUpVlanMapping(upVlanMapping);
		
		List<ConfigTemplateStormControl> stormControlList = new ArrayList<ConfigTemplateStormControl>();
		for (ConfigTemplateStormControl tempClass : source.getStormControlList()) {
			stormControlList.add(tempClass);
		}
		destination.setStormControlList(stormControlList);
		
		if (source.getSwitchSettings() != null) {
			SwitchSettings switchSettings = new SwitchSettings();
			switchSettings.setId(null);
			switchSettings = (SwitchSettings) source.getSwitchSettings().clone();
			switchSettings.setVersion(null);
			if (source.getSwitchSettings().getStpSettings() != null){
				StpSettings stpSettings = new StpSettings();
				stpSettings = (StpSettings) source.getSwitchSettings().getStpSettings().clone();
				stpSettings.setId(null);
				stpSettings.setVersion(null);
				if(source.getSwitchSettings().getStpSettings().getMstpRegion() != null){
					stpSettings.setMstpRegion(source.getSwitchSettings().getStpSettings().getMstpRegion());
				}
				switchSettings.setStpSettings(stpSettings);
			}
			destination.setSwitchSettings(switchSettings);
		}
	}
}
