package com.ah.util.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.network.VpnNetwork;

/**
 * 
 * @date Jan 30, 2012
 * @author wx
 *
 * used for: deal with pre-defined auto provisioning profile for BR100/BR200/BR200-WP freeval
 */
public class DeviceFreevalUtil {

	private static final String AUTO_PROVISIONING_NAME_PRE_DEFINED_BR100 = "BR100Freeval";
	private static final String AUTO_PROVISIONING_NAME_PRE_DEFINED_BR200 = "BR200Freeval";
	private static final String AUTO_PROVISIONING_NAME_PRE_DEFINED_BR200WP = "BR200WPFreeval";
	
	private static final List<String> FREEVAL_PROVISIONS_LIST = new ArrayList<String>();
	static {
		FREEVAL_PROVISIONS_LIST.add(AUTO_PROVISIONING_NAME_PRE_DEFINED_BR100);
		FREEVAL_PROVISIONS_LIST.add(AUTO_PROVISIONING_NAME_PRE_DEFINED_BR200);
		FREEVAL_PROVISIONS_LIST.add(AUTO_PROVISIONING_NAME_PRE_DEFINED_BR200WP);
	}
	
	private static boolean checkAutoProvisionAndDeviceType(HiveApAutoProvision autoProvision) {
		if (autoProvision != null) {
			if (AUTO_PROVISIONING_NAME_PRE_DEFINED_BR100.equals(autoProvision.getName())) {
				if (HiveAp.HIVEAP_MODEL_BR100 == autoProvision.getModelType()) {
					return true;
				}
			} else if (AUTO_PROVISIONING_NAME_PRE_DEFINED_BR200.equals(autoProvision.getName())) {
				if (HiveAp.HIVEAP_MODEL_BR200 == autoProvision.getModelType()) {
					return true;
				}
			} else if (AUTO_PROVISIONING_NAME_PRE_DEFINED_BR200WP.equals(autoProvision.getName())) {
				if (HiveAp.HIVEAP_MODEL_BR200_WP == autoProvision.getModelType()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static boolean checkAutoProvisionAndDeviceType(List<HiveApAutoProvision> autoProvisions) {
		boolean result = false;
		if (autoProvisions != null
				&& !autoProvisions.isEmpty()) {
			for (HiveApAutoProvision autoProvision : autoProvisions) {
				if (checkAutoProvisionAndDeviceType(autoProvision)) {
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	public static boolean isHMForDeviceFreeval() {
		return NmsUtil.isHostedHMApplication()
				&& isAutoProvisioningForFreevalDefinedInHome();
		
	}
	
	public static boolean isDeviceFreevalDefinedInVHM(HmDomain owner) {
		return NmsUtil.isHostedHMApplication()
				&& isAutoProvisioningForFreevalDefinedInVHM(owner);
	}
	
	public static HiveProfile getFirstHiveProfileNotDeviceFreeval(HmDomain owner, List<ConfigTemplate> networkPolicys) {
		if (networkPolicys == null) {
			networkPolicys = getNetworkPolicysOfDeviceFreeval(owner);
		}
		if (networkPolicys == null
				|| networkPolicys.isEmpty()) {
			return null;
		}
		
		List<String> names = new ArrayList<String>();
		for (ConfigTemplate np : networkPolicys) {
			names.add(np.getHiveProfile().getHiveName());
		}
		
		List<HiveProfile> list = QueryUtil.executeQuery(HiveProfile.class, null, 
				new FilterParams("defaultFlag is false and hiveName not in (:s1)", names.toArray()),	
				owner.getId());
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}
	
	public static ConfigTemplate getFirstConfigTemplateNotDeviceFreeval(HmDomain owner, List<ConfigTemplate> networkPolicys, String preferName) {
		if (networkPolicys == null) {
			networkPolicys = getNetworkPolicysOfDeviceFreeval(owner);
		}
		if (networkPolicys == null
				|| networkPolicys.isEmpty()) {
			return null;
		}
		
		List<String> names = new ArrayList<String>();
		for (ConfigTemplate np : networkPolicys) {
			names.add(np.getConfigName());
		}
		
		List<ConfigTemplate> list = QueryUtil.executeQuery(ConfigTemplate.class, null, 
				new FilterParams("defaultFlag is false and configName not in (:s1)", names.toArray()),	
				owner.getId());
		if (list != null && list.size() > 0) {
			if (!StringUtils.isBlank(preferName)) {
				for (ConfigTemplate config : list) {
					if (preferName.equals(config.getConfigName())) {
						return config;
					}
				}
			}
			return list.get(0);
		}
		
		return null;
	}
	
	public static List<String> getNetworkObjectNamesOfDeviceFreeval(HmDomain owner, List<ConfigTemplate> networkPolicys) {
		/*if (networkPolicys == null) {
			networkPolicys = getNetworkPolicysOfDeviceFreeval(owner);
		}
		if (networkPolicys == null
				|| networkPolicys.isEmpty()) {
			return null;
		}*/
		
		// for now, do not modify those network object name which are defined in home
		List<VpnNetwork> vpnNetworks = QueryUtil.executeQuery(VpnNetwork.class, null, null, 
				BoMgmt.getDomainMgmt().getHomeDomain().getId());
		
		if (vpnNetworks != null && vpnNetworks.size() > 0) {
			List<String> networkNames = new ArrayList<String>();
			for (VpnNetwork network : vpnNetworks) {
				if (!networkNames.contains(network.getNetworkName())) {
					networkNames.add(network.getNetworkName());
				}
			}
			
			return networkNames;
		}
		
		return null;
	}
	
	
	private static List<ConfigTemplate> getNetworkPolicysOfDeviceFreeval(HmDomain owner) {
		List<HiveApAutoProvision> autoProvisions = QueryUtil.executeQuery(HiveApAutoProvision.class, 
				null, 
				new FilterParams("name", FREEVAL_PROVISIONS_LIST), 
				owner.getId());
		if (autoProvisions == null
				|| autoProvisions.isEmpty()) {
			return null;
		}
		
		List<Long> ids = new ArrayList<Long>();
		for (HiveApAutoProvision autoProvision : autoProvisions) {
			if (checkAutoProvisionAndDeviceType(autoProvision)) {
				ids.add(autoProvision.getConfigTemplateId());
			}
		}
		
		if (ids == null
				|| ids.isEmpty()) {
			return null;
		}
		
		return QueryUtil.executeQuery(ConfigTemplate.class,
				null, new FilterParams("id", ids), owner.getId(), new ImplQueryBo());
	}
	
	private static boolean isAutoProvisioningForFreevalDefinedInHome() {
		return isAutoProvisioningForFreevalDefinedInVHM(BoMgmt.getDomainMgmt().getHomeDomain());
	}
	
	private static boolean isAutoProvisioningForFreevalDefinedInVHM(HmDomain owner) {
		return checkAutoProvisionAndDeviceType(QueryUtil.executeQuery(HiveApAutoProvision.class, 
				null, 
				new FilterParams("name", FREEVAL_PROVISIONS_LIST), 
				owner.getId()));
	}
	
	static class ImplQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof ConfigTemplate) {
				ConfigTemplate config = (ConfigTemplate)bo;
				if (config.getHiveProfile() != null) {
					config.getHiveProfile().getId();
				}
			}
			
			return null;
		}
	}

}
