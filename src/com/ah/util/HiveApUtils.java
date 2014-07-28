package com.ah.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.ah.be.parameter.device.DevicePropertyManage;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.SsidProfile;

/**
 * This class used to aggregate all HiveAP feature related query statements. The
 * purpose is make changes more clear while new HiveAP product add in.
 *
 * @author mfjin
 *
 */
public class HiveApUtils {
	
	public static enum DeviceListType{
		AP, BR, L3_VPN, L2_VPN, Switch
	}

	public static final FilterParams getRadiusProxyApFilter(
			boolean filterNewAp, boolean filterSimulateAp) {
		return getRadiusProxyApFilter(filterNewAp, filterSimulateAp, null);
	}

	public static final FilterParams getRadiusProxyApFilter(
			boolean filterNewAp, boolean filterSimulateAp, Set<Long> idScope) {
		List<Short> apModels = new ArrayList<Short>();
		apModels.add(HiveAp.HIVEAP_MODEL_330);
		apModels.add(HiveAp.HIVEAP_MODEL_350);
		apModels.add(HiveAp.HIVEAP_MODEL_BR200);
		apModels.add(HiveAp.HIVEAP_MODEL_BR200_WP);
		apModels.add(HiveAp.HIVEAP_MODEL_BR200_LTE_VZ);
		apModels.add(HiveAp.HIVEAP_MODEL_SR24);
		apModels.add(HiveAp.HIVEAP_MODEL_SR2024P);
		apModels.add(HiveAp.HIVEAP_MODEL_SR2124P);
		apModels.add(HiveAp.HIVEAP_MODEL_SR2148P);
		List<Short> apStatuses = new ArrayList<Short>();
		apStatuses.add(HiveAp.STATUS_MANAGED);
		if (!filterNewAp) {
			apStatuses.add(HiveAp.STATUS_NEW);
		}
		List<Boolean> apSimulated = new ArrayList<Boolean>();
		apSimulated.add(false);
		if (!filterSimulateAp) {
			apSimulated.add(true);
		}
		List<Short> deviceTypes = new ArrayList<Short>();
		deviceTypes.add(HiveAp.Device_TYPE_BRANCH_ROUTER);
		deviceTypes.add(HiveAp.Device_TYPE_VPN_BR);

		List<Short> deviceTypesApSr = new ArrayList<Short>();
		deviceTypesApSr.add(HiveAp.Device_TYPE_HIVEAP);
		deviceTypesApSr.add(HiveAp.Device_TYPE_SWITCH);

		String where;
		Object[] values;
		if (null == idScope || idScope.isEmpty()) {
			where = "manageStatus in (:s1) AND simulated in (:s2) AND ( (enabledBrAsRadiusServer=:s3 "
					+ "AND deviceType in (:s4) AND hiveApModel in (:s5) AND "
					+ "(radiusProxyProfile != null OR configTemplate.radiusProxyProfile != null)) "
					+ "OR (deviceType in (:s6) AND radiusProxyProfile != null)  )";
			values = new Object[6];
			values[0] = apStatuses;
			values[1] = apSimulated;
			values[2] = true;
			values[3] = deviceTypes;
			values[4] = apModels;
			values[5] = deviceTypesApSr;
		} else {
			where = "manageStatus in (:s1) AND simulated in (:s2) AND id in (:s3) AND ( (enabledBrAsRadiusServer=:s4 "
					+ "AND deviceType in (:s5) AND hiveApModel in (:s6) AND "
					+ "(radiusProxyProfile != null OR configTemplate.radiusProxyProfile != null)) "
					+ "OR (deviceType in (:s7) AND radiusProxyProfile != null) )";
			values = new Object[7];
			values[0] = apStatuses;
			values[1] = apSimulated;
			values[2] = idScope;
			values[3] = true;
			values[4] = deviceTypes;
			values[5] = apModels;
			values[6] = deviceTypesApSr;
		}
		return new FilterParams(where, values);
	}

	public static final FilterParams getRadiusCachingApFilter(
			boolean filterNewAp, boolean filterSimulateAp,
			Set<Long> profileIdScope) {
		return getRadiusServerCachingApFilter(filterNewAp, filterSimulateAp,
				null, profileIdScope);
	}

	public static final FilterParams getRadiusServerApFilter(
			boolean filterNewAp, boolean filterSimulateAp) {
		return getRadiusServerApFilter(filterNewAp, filterSimulateAp, null);
	}

	public static final FilterParams getRadiusServerApFilter(
			boolean filterNewAp, boolean filterSimulateAp, Set<Long> apIdScope) {
		return getRadiusServerCachingApFilter(filterNewAp, filterSimulateAp,
				apIdScope, null);
	}

	public static final FilterParams getRadiusServerCachingApFilter(
			boolean filterNewAp, boolean filterSimulateAp, Set<Long> apIdScope,
			Set<Long> profileIdScope) {
		List<Short> apModels = DevicePropertyManage.getInstance().getSupportDeviceList(DeviceInfo.SPT_RADIUS_SERVER);
		List<Short> apStatuses = new ArrayList<Short>();
		apStatuses.add(HiveAp.STATUS_MANAGED);
		if (!filterNewAp) {
			apStatuses.add(HiveAp.STATUS_NEW);
		}
		List<Boolean> apSimulated = new ArrayList<Boolean>();
		apSimulated.add(false);
		if (!filterSimulateAp) {
			apSimulated.add(true);
		}
		List<Short> deviceTypes = new ArrayList<Short>();
		deviceTypes.add(HiveAp.Device_TYPE_BRANCH_ROUTER);
		deviceTypes.add(HiveAp.Device_TYPE_VPN_BR);

		List<Short> deviceTypesApSr = new ArrayList<Short>();
		deviceTypesApSr.add(HiveAp.Device_TYPE_HIVEAP);
		deviceTypesApSr.add(HiveAp.Device_TYPE_SWITCH);

		String where;
		Object[] values;
		if (null == apIdScope || apIdScope.isEmpty()) {
			if (null == profileIdScope || profileIdScope.isEmpty()) {
				where = "manageStatus in (:s1) AND simulated in (:s2) AND ( (enabledBrAsRadiusServer=:s3 "
						+ "AND deviceType in (:s4) AND hiveApModel in (:s5) AND "
						+ "(radiusServerProfile != null OR configTemplate.radiusServerProfile != null)) "
						+ "OR (deviceType in (:s6) AND radiusServerProfile != null)  )";
				values = new Object[6];
				values[0] = apStatuses;
				values[1] = apSimulated;
				values[2] = true;
				values[3] = deviceTypes;
				values[4] = apModels;
				values[5] = deviceTypesApSr;
			} else {
				where = "manageStatus in (:s1) AND simulated in (:s2) AND ( (enabledBrAsRadiusServer=:s3 "
						+ "AND deviceType in (:s4) AND hiveApModel in (:s5) AND "
						+ "(radiusServerProfile.id in (:s7) OR configTemplate.radiusServerProfile.id in (:s7))) "
						+ "OR (deviceType in (:s6) AND radiusServerProfile.id in (:s7))  )";
				values = new Object[7];
				values[0] = apStatuses;
				values[1] = apSimulated;
				values[2] = true;
				values[3] = deviceTypes;
				values[4] = apModels;
				values[5] = deviceTypesApSr;
				values[6] = profileIdScope;
			}
		} else {
			if (null == profileIdScope || profileIdScope.isEmpty()) {
				where = "manageStatus in (:s1) AND simulated in (:s2) AND id in (:s3) AND ( (enabledBrAsRadiusServer=:s4 "
						+ "AND deviceType in (:s5) AND hiveApModel in (:s6) AND "
						+ "(radiusServerProfile != null OR configTemplate.radiusServerProfile != null)) "
						+ "OR (deviceType in (:s7) AND radiusServerProfile != null) )";
				values = new Object[7];
				values[0] = apStatuses;
				values[1] = apSimulated;
				values[2] = apIdScope;
				values[3] = true;
				values[4] = deviceTypes;
				values[5] = apModels;
				values[6] = deviceTypesApSr;
			} else {
				where = "manageStatus in (:s1) AND simulated in (:s2) AND id in (:s3) AND ( (enabledBrAsRadiusServer=:s4 "
						+ "AND deviceType in (:s5) AND hiveApModel in (:s6) AND "
						+ "(radiusServerProfile.id in (:s8) OR configTemplate.radiusServerProfile.id in (:s8))) "
						+ "OR (deviceType in (:s7) AND radiusServerProfile.id in (:s8)) )";
				values = new Object[8];
				values[0] = apStatuses;
				values[1] = apSimulated;
				values[2] = apIdScope;
				values[3] = true;
				values[4] = deviceTypes;
				values[5] = apModels;
				values[6] = deviceTypesApSr;
				values[7] = profileIdScope;
			}
		}
		return new FilterParams(where, values);
	}

	public static final FilterParams getMeshingEnabledApFilter(
			boolean filterNewAp, boolean filterSimulateAp) {
		// With two wireless radio
		List<Short> apModel1 = new ArrayList<Short>();
		apModel1.add(HiveAp.HIVEAP_MODEL_120);
		apModel1.add(HiveAp.HIVEAP_MODEL_121);
		apModel1.add(HiveAp.HIVEAP_MODEL_141);
		apModel1.add(HiveAp.HIVEAP_MODEL_170);
		apModel1.add(HiveAp.HIVEAP_MODEL_20);
		apModel1.add(HiveAp.HIVEAP_MODEL_28);
		apModel1.add(HiveAp.HIVEAP_MODEL_320);
		apModel1.add(HiveAp.HIVEAP_MODEL_330);
		apModel1.add(HiveAp.HIVEAP_MODEL_340);
		apModel1.add(HiveAp.HIVEAP_MODEL_350);
		apModel1.add(HiveAp.HIVEAP_MODEL_370);
		apModel1.add(HiveAp.HIVEAP_MODEL_380);
		apModel1.add(HiveAp.HIVEAP_MODEL_390);
		apModel1.add(HiveAp.HIVEAP_MODEL_230);

		// With only one wireless radio
		List<Short> apModel2 = new ArrayList<Short>();
		apModel2.add(HiveAp.HIVEAP_MODEL_110);
		apModel2.add(HiveAp.HIVEAP_MODEL_BR100);
		apModel2.add(HiveAp.HIVEAP_MODEL_BR200_WP);
		apModel2.add(HiveAp.HIVEAP_MODEL_BR200_LTE_VZ);

		List<Short> meshOptions = new ArrayList<Short>();
		meshOptions.add(AhInterface.OPERATION_MODE_BACKHAUL);
		meshOptions.add(AhInterface.OPERATION_MODE_DUAL);

		List<Short> apStatuses = new ArrayList<Short>();
		apStatuses.add(HiveAp.STATUS_MANAGED);
		if (!filterNewAp) {
			apStatuses.add(HiveAp.STATUS_NEW);
		}
		List<Boolean> apSimulated = new ArrayList<Boolean>();
		apSimulated.add(false);
		if (!filterSimulateAp) {
			apSimulated.add(true);
		}

		String where = "manageStatus in (:s1) AND simulated in (:s2) AND "
				+ "((hiveApModel in (:s3) AND (wifi0.operationMode in (:s4) OR wifi1.operationMode in (:s4))) OR "
				+ "(hiveApModel in (:s5) AND wifi0.operationMode in (:s4)))";
		Object[] values = new Object[5];
		values[0] = apStatuses;
		values[1] = apSimulated;
		values[2] = apModel1;
		values[3] = meshOptions;
		values[4] = apModel2;
		return new FilterParams(where, values);
	}

	public static final FilterParams getMeshingApFilter(boolean filterNewAp,
			boolean filterSimulateAp) {
		List<Short> apModel = new ArrayList<Short>();
		apModel.add(HiveAp.HIVEAP_MODEL_120);
		apModel.add(HiveAp.HIVEAP_MODEL_121);
		apModel.add(HiveAp.HIVEAP_MODEL_141);
		apModel.add(HiveAp.HIVEAP_MODEL_170);
		apModel.add(HiveAp.HIVEAP_MODEL_20);
		apModel.add(HiveAp.HIVEAP_MODEL_28);
		apModel.add(HiveAp.HIVEAP_MODEL_320);
		apModel.add(HiveAp.HIVEAP_MODEL_330);
		apModel.add(HiveAp.HIVEAP_MODEL_340);
		apModel.add(HiveAp.HIVEAP_MODEL_350);
		apModel.add(HiveAp.HIVEAP_MODEL_370);
		apModel.add(HiveAp.HIVEAP_MODEL_380);
		apModel.add(HiveAp.HIVEAP_MODEL_390);
		apModel.add(HiveAp.HIVEAP_MODEL_230);
		apModel.add(HiveAp.HIVEAP_MODEL_110);
		apModel.add(HiveAp.HIVEAP_MODEL_BR100);
		apModel.add(HiveAp.HIVEAP_MODEL_BR200);
		apModel.add(HiveAp.HIVEAP_MODEL_BR200_WP);
		apModel.add(HiveAp.HIVEAP_MODEL_BR200_LTE_VZ);

		List<Short> apStatuses = new ArrayList<Short>();
		apStatuses.add(HiveAp.STATUS_MANAGED);
		if (!filterNewAp) {
			apStatuses.add(HiveAp.STATUS_NEW);
		}
		List<Boolean> apSimulated = new ArrayList<Boolean>();
		apSimulated.add(false);
		if (!filterSimulateAp) {
			apSimulated.add(true);
		}
		String where = "manageStatus in (:s1) AND simulated in (:s2) AND hiveApModel in (:s3) AND hiveApType = :s4";
		Object[] values = new Object[4];
		values[0] = apStatuses;
		values[1] = apSimulated;
		values[2] = apModel;
		values[3] = HiveAp.HIVEAP_TYPE_MP;
		return new FilterParams(where, values);
	}

	public static final FilterParams getBridgeApFilter(boolean filterNewAp,
			boolean filterSimulateAp) {
		// With one Ethernet radio
		List<Short> apModel1 = new ArrayList<Short>();
		apModel1.add(HiveAp.HIVEAP_MODEL_120);
		apModel1.add(HiveAp.HIVEAP_MODEL_121);
		apModel1.add(HiveAp.HIVEAP_MODEL_141);
		apModel1.add(HiveAp.HIVEAP_MODEL_170);
		apModel1.add(HiveAp.HIVEAP_MODEL_20);
		apModel1.add(HiveAp.HIVEAP_MODEL_28);
		apModel1.add(HiveAp.HIVEAP_MODEL_110);

		// With two Ethernet port
		List<Short> apModel2 = new ArrayList<Short>();
		apModel2.add(HiveAp.HIVEAP_MODEL_320);
		apModel2.add(HiveAp.HIVEAP_MODEL_330);
		apModel2.add(HiveAp.HIVEAP_MODEL_340);
		apModel2.add(HiveAp.HIVEAP_MODEL_350);
		apModel2.add(HiveAp.HIVEAP_MODEL_370);
		apModel2.add(HiveAp.HIVEAP_MODEL_380);
		apModel2.add(HiveAp.HIVEAP_MODEL_390);
		apModel2.add(HiveAp.HIVEAP_MODEL_230);

		// With bridge always enabled
		List<Short> apModel3 = new ArrayList<Short>();
		apModel3.add(HiveAp.HIVEAP_MODEL_BR100);
		apModel3.add(HiveAp.HIVEAP_MODEL_BR200);
		apModel3.add(HiveAp.HIVEAP_MODEL_BR200_WP);
		apModel3.add(HiveAp.HIVEAP_MODEL_BR200_LTE_VZ);

		List<Short> bridgeOptions = new ArrayList<Short>();
		bridgeOptions.add(AhInterface.OPERATION_MODE_ACCESS);
		bridgeOptions.add(AhInterface.OPERATION_MODE_BRIDGE);

		List<Short> apStatuses = new ArrayList<Short>();
		apStatuses.add(HiveAp.STATUS_MANAGED);
		if (!filterNewAp) {
			apStatuses.add(HiveAp.STATUS_NEW);
		}
		List<Boolean> apSimulated = new ArrayList<Boolean>();
		apSimulated.add(false);
		if (!filterSimulateAp) {
			apSimulated.add(true);
		}

		String where = "manageStatus in (:s1) AND simulated in (:s2) AND ((hiveApModel in (:s3)) "
				+ "OR (hiveApModel in (:s4) AND eth0.operationMode in (:s6)) "
				+ "OR (hiveApModel in (:s5) AND ethConfigType = :s7 AND (eth0.operationMode in (:s6) OR eth1.operationMode in (:s6))))";
		Object[] values = new Object[7];
		values[0] = apStatuses;
		values[1] = apSimulated;
		values[2] = apModel3;
		values[3] = apModel1;
		values[4] = apModel2;
		values[5] = bridgeOptions;
		values[6] = HiveAp.USE_ETHERNET_BOTH;
		return new FilterParams(where, values);
	}

	public static final FilterParams getLan8021XFeatureHiveApFilter(
			boolean filterNewAp, boolean filterSimulateAp, Set<Long> idScope) {
		List<Short> apModels = new ArrayList<Short>();
		apModels.add(HiveAp.HIVEAP_MODEL_330);
		apModels.add(HiveAp.HIVEAP_MODEL_350);
		List<Short> apStatuses = new ArrayList<Short>();
		apStatuses.add(HiveAp.STATUS_MANAGED);
		if (!filterNewAp) {
			apStatuses.add(HiveAp.STATUS_NEW);
		}
		List<Boolean> apSimulated = new ArrayList<Boolean>();
		apSimulated.add(false);
		if (!filterSimulateAp) {
			apSimulated.add(true);
		}
		List<Short> deviceTypes = new ArrayList<Short>();
		deviceTypes.add(HiveAp.Device_TYPE_BRANCH_ROUTER);

		String where;
		Object[] values;
		if (null == idScope || idScope.isEmpty()) {
			where = "manageStatus in (:s1) AND simulated in (:s2) AND hiveApModel in (:s3) AND deviceType in (:s4)";
			values = new Object[4];
			values[0] = apStatuses;
			values[1] = apSimulated;
			values[2] = apModels;
			values[3] = deviceTypes;
		} else {
			where = "id in (:s5) AND manageStatus in (:s1) AND simulated in (:s2) AND hiveApModel in (:s3) AND deviceType in (:s4)";
			values = new Object[5];
			values[0] = apStatuses;
			values[1] = apSimulated;
			values[2] = apModels;
			values[3] = deviceTypes;
			values[4] = idScope;
		}
		return new FilterParams(where, values);
	}

	public static final FilterParams getLan8021XFeatureRouterFilter(
			boolean filterNewAp, boolean filterSimulateAp, Set<Long> idScope) {
		List<Short> apModels = new ArrayList<Short>();
		apModels.add(HiveAp.HIVEAP_MODEL_BR100);
		apModels.add(HiveAp.HIVEAP_MODEL_BR200);
		apModels.add(HiveAp.HIVEAP_MODEL_BR200_WP);
		apModels.add(HiveAp.HIVEAP_MODEL_BR200_LTE_VZ);
		List<Short> apStatuses = new ArrayList<Short>();
		apStatuses.add(HiveAp.STATUS_MANAGED);
		if (!filterNewAp) {
			apStatuses.add(HiveAp.STATUS_NEW);
		}
		List<Boolean> apSimulated = new ArrayList<Boolean>();
		apSimulated.add(false);
		if (!filterSimulateAp) {
			apSimulated.add(true);
		}

		String where;
		Object[] values;
		if (null == idScope || idScope.isEmpty()) {
			where = "manageStatus in (:s1) AND simulated in (:s2) AND hiveApModel in (:s3)";
			values = new Object[3];
			values[0] = apStatuses;
			values[1] = apSimulated;
			values[2] = apModels;
		} else {
			where = "id in (:s4) AND manageStatus in (:s1) AND simulated in (:s2) AND hiveApModel in (:s3)";
			values = new Object[4];
			values[0] = apStatuses;
			values[1] = apSimulated;
			values[2] = apModels;
			values[3] = idScope;
		}
		return new FilterParams(where, values);
	}

	public static final FilterParams getCvgModeFilter(boolean filterNewAp,
			boolean filterSimulateAp) {
		List<Short> apModel = new ArrayList<Short>();
		apModel.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
		apModel.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);

		List<Short> apStatuses = new ArrayList<Short>();
		apStatuses.add(HiveAp.STATUS_MANAGED);
		if (!filterNewAp) {
			apStatuses.add(HiveAp.STATUS_NEW);
		}
		List<Boolean> apSimulated = new ArrayList<Boolean>();
		apSimulated.add(false);
		if (!filterSimulateAp) {
			apSimulated.add(true);
		}

		String where = "manageStatus in (:s1) AND simulated in (:s2) AND hiveApModel in (:s3)";
		Object[] values = new Object[3];
		values[0] = apStatuses;
		values[1] = apSimulated;
		values[2] = apModel;
		return new FilterParams(where, values);
	}

	public static final FilterParams getIdmCertFilter(Set<Long> init_ids){
		FilterParams filter = null;
		List<Short> status = new ArrayList<Short>();
		status.add(HiveAp.STATUS_MANAGED);
		status.add(HiveAp.STATUS_NEW);
		List<Short> idmSupportModel = DevicePropertyManage.getInstance().getSupportDeviceList(DeviceInfo.SPT_IDM_PROXY);
		
		List<Byte> cwpAuthType = new ArrayList<Byte>();
		cwpAuthType.add(Cwp.REGISTRATION_TYPE_REGISTERED);
		cwpAuthType.add(Cwp.REGISTRATION_TYPE_BOTH);
		String verHollywood = "6.2.1.0";				//device version after Hollywood, no need update IDM proxy Cert, let device do it.
		if (null == init_ids || init_ids.size() == 0) {
			String where = " manageStatus in (:s1) AND hiveApModel in (:s2) ( " +
					"(IDMProxy = true AND softVer < :s3) "+
					"or exists (select ap2.id from "+HiveAp.class.getSimpleName()+" as ap2 inner join ap2.configTemplate.ssidInterfaces as ssid where " +
							"ap.id=ap2.id and ssid.ssidProfile != null and ssid.ssidProfile.enabledIDM=true and ssid.ssidProfile.cwp != null " +
							"and ssid.ssidProfile.cwp.registrationType in (:s4) ) "+
					")";
			Object[] values = new Object[4];
			values[0] = status;
			values[1] = idmSupportModel;
			values[2] = verHollywood;
			values[3] = cwpAuthType;
			filter = new FilterParams(where, values);
		} else {
			String where = " ap.id in (:s5) AND manageStatus in (:s1) and hiveApModel in (:s2) AND (" +
					"(IDMProxy = true AND softVer < :s3) "+
					"or exists (select ap2.id from "+HiveAp.class.getSimpleName()+" as ap2 inner join ap2.configTemplate.ssidInterfaces as ssid where " +
							"ap.id=ap2.id and ssid.ssidProfile != null and ssid.ssidProfile.enabledIDM=true and ssid.ssidProfile.cwp != null " +
							"and ssid.ssidProfile.cwp.registrationType in (:s4) ) "+
					")";
			Object[] values = new Object[5];
			values[0] = status;
			values[1] = idmSupportModel;
			values[2] = verHollywood;
			values[3] = cwpAuthType;
			values[4] = init_ids;
			filter = new FilterParams(where, values);
		}
		return filter;
	}
	
	public static final FilterParams getL7SignatureDeviceFilter(
			Set<Long> idScope) {
		List<Short> apModels = new ArrayList<Short>();
		apModels.add(HiveAp.HIVEAP_MODEL_121);
		apModels.add(HiveAp.HIVEAP_MODEL_141);
		apModels.add(HiveAp.HIVEAP_MODEL_330);
		apModels.add(HiveAp.HIVEAP_MODEL_350);
		apModels.add(HiveAp.HIVEAP_MODEL_BR200);
		apModels.add(HiveAp.HIVEAP_MODEL_BR200_WP);
		apModels.add(HiveAp.HIVEAP_MODEL_BR200_LTE_VZ);
		apModels.add(HiveAp.HIVEAP_MODEL_320);
		apModels.add(HiveAp.HIVEAP_MODEL_340);
		apModels.add(HiveAp.HIVEAP_MODEL_120);
		apModels.add(HiveAp.HIVEAP_MODEL_110);
		apModels.add(HiveAp.HIVEAP_MODEL_170);
		apModels.add(HiveAp.HIVEAP_MODEL_370);
		apModels.add(HiveAp.HIVEAP_MODEL_390);
		apModels.add(HiveAp.HIVEAP_MODEL_230);
		
		String where;
		Object[] values;
		if (null == idScope || idScope.size() == 0) {
			where = "hiveApModel in (:s1) and softVer >= :s2";
			values = new Object[2];
			values[0] = apModels;
			values[1] = "6.0.2.0";
		} else {
			where = "id in (:s1) AND hiveApModel in (:s2) and softVer >= :s3";
			values = new Object[3];
			values[0] = idScope;
			values[1] = apModels;
			values[2] = "6.0.2.0";
		}
		return new FilterParams(where, values);
	}
	
	public static final List<Short> getPresenceSupportDeviceFilter() {
		List<Short> apModels = new ArrayList<Short>();
		apModels.add(HiveAp.HIVEAP_MODEL_120);
		apModels.add(HiveAp.HIVEAP_MODEL_121);
		apModels.add(HiveAp.HIVEAP_MODEL_141);
		apModels.add(HiveAp.HIVEAP_MODEL_170);
		apModels.add(HiveAp.HIVEAP_MODEL_230);
		apModels.add(HiveAp.HIVEAP_MODEL_320);
		apModels.add(HiveAp.HIVEAP_MODEL_330);
		apModels.add(HiveAp.HIVEAP_MODEL_340);
		apModels.add(HiveAp.HIVEAP_MODEL_350);
		apModels.add(HiveAp.HIVEAP_MODEL_380);
		apModels.add(HiveAp.HIVEAP_MODEL_BR100);
		apModels.add(HiveAp.HIVEAP_MODEL_BR200_WP);
		apModels.add(HiveAp.HIVEAP_MODEL_BR200_LTE_VZ);

		return apModels;
	}
	
	public static boolean isPpskServer(Long deviceId){
		String sql = "select id from "+HiveAp.class.getSimpleName()+" ap ";
		String where = "id=:s1 and " +
				" exists (select 1 from ap.configTemplate np inner join np.ssidInterfaces sl " +
				" where sl.ssidProfile != null " +
				" and sl.ssidProfile.accessMode = :s2 " +
				" and (sl.ssidProfile.enablePpskSelfReg=true or sl.ssidProfile.ssidSecurity.blnMacBindingEnable=true) " +
				" and (ap.id= sl.ssidProfile.ppskServerId or (blnBrAsPpskServer=true and ap.deviceType in (:s3)) ) )";

		Object[] parmArg = new Object[3];
		parmArg[0] = deviceId;
		parmArg[1] = SsidProfile.ACCESS_MODE_PSK;
		parmArg[2] = Arrays.asList(new Short[]{HiveAp.Device_TYPE_BRANCH_ROUTER, HiveAp.Device_TYPE_VPN_BR});
		FilterParams filter = new FilterParams(where, parmArg);
		
		List<?> resList = QueryUtil.executeQuery(sql, null, filter);
		if(resList != null && !resList.isEmpty()){
			return true;
		}else{
			return false;
		}
	}
	
	public static DeviceListType getDeviceTypeEnum(short hiveApModel, short deviceType){
		if(deviceType == HiveAp.Device_TYPE_HIVEAP && (!HiveAp.isCVGAppliance(hiveApModel))){
			return DeviceListType.AP;
		}else if(deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER){
			return DeviceListType.BR;
		}else if(deviceType == HiveAp.Device_TYPE_VPN_GATEWAY || deviceType == HiveAp.Device_TYPE_VPN_BR){
			return DeviceListType.L3_VPN;
		}else if(deviceType == HiveAp.Device_TYPE_HIVEAP && (HiveAp.isCVGAppliance(hiveApModel))){
			return DeviceListType.L2_VPN;
		} else if (deviceType == HiveAp.Device_TYPE_SWITCH) {
			return DeviceListType.Switch;
		}else{
			return null;
		}
	}
}
