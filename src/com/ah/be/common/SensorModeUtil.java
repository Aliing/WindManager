package com.ah.be.common;

import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApWifi;
import com.ah.bo.wlan.RadioProfile;
import com.ah.util.MgrUtil;

public class SensorModeUtil {

	public static boolean isSupportSensorAp(short hiveApModel) {
		return hiveApModel != HiveAp.HIVEAP_MODEL_20
				&& hiveApModel != HiveAp.HIVEAP_MODEL_28
				&& hiveApModel != HiveAp.HIVEAP_MODEL_110
				&& hiveApModel != HiveAp.HIVEAP_MODEL_370
				&& hiveApModel != HiveAp.HIVEAP_MODEL_390
				&& !isSupportSensorBr(hiveApModel);
	}

	public static boolean isSupportSensorBr(short hiveApModel) {
		return hiveApModel == HiveAp.HIVEAP_MODEL_BR200_WP
				|| hiveApModel == HiveAp.HIVEAP_MODEL_BR100
				|| hiveApModel == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ;
	}

	public static boolean isSupportWIPSOnSensor(HiveAp hiveAp,
			short operationMode) {
		if (null == hiveAp) {
			return false;
		}
		// BR100 don't support WIPS at any operationMode mode
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100) {
			return false;
		}
		// bug 32280 is invalid for sensor mode2 now, but need to deal with the
		// situation that HOS version less than Hollywood_6.2.1.0
		if (operationMode == AhInterface.OPERATION_MODE_SENSOR
				&& NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),
						"6.2.1.0") < 0) {
			return false;
		}
		return true;
	}

	public static void checkSensorFeatures(HiveAp hiveAp) throws Exception {
		HiveApWifi hiveApWifi0 = hiveAp.getWifi0();
		HiveApWifi hiveApWifi1 = hiveAp.getWifi1();
		if (null != hiveApWifi0
				&& hiveApWifi0.getOperationMode() == AhInterface.OPERATION_MODE_SENSOR) {
			RadioProfile wifi0Radio = hiveAp.getWifi0RadioProfile();
			if (!wifi0Radio.isEnableWips() && !wifi0Radio.isEnabledPresence()) {
				throw new Exception(MgrUtil.getUserMessage(
						"config.radioProfile.sensorMode.noFeature", "wifi0"));
			}
		}
		if (!hiveAp.isWifi1Available()) {
			return;
		}
		if (null != hiveApWifi1
				&& hiveApWifi1.getOperationMode() == AhInterface.OPERATION_MODE_SENSOR) {
			RadioProfile wifi1Radio = hiveAp.getWifi1RadioProfile();
			if (!wifi1Radio.isEnableWips() && !wifi1Radio.isEnabledPresence()) {
				throw new Exception(MgrUtil.getUserMessage(
						"config.radioProfile.sensorMode.noFeature", "wifi1"));
			}
		}
	}

	public static void checkWIPSConfiguration(HiveAp hiveAp) throws Exception {
		if (null != hiveAp.getConfigTemplate().getIdsPolicy()) {
			return;
		}
		RadioProfile wifi0Radio = hiveAp.getWifi0RadioProfile();
		RadioProfile wifi1Radio = hiveAp.getWifi1RadioProfile();
		if (null != wifi0Radio && wifi0Radio.isEnableWips()) {
			throw new Exception(MgrUtil.getUserMessage(
					"config.wips.server.enabled.errorMsg", "wifi0"));
		}
		if (null != wifi1Radio && wifi1Radio.isEnableWips()) {
			throw new Exception(MgrUtil.getUserMessage(
					"config.wips.server.enabled.errorMsg", "wifi0"));
		}
	}

	public static void setSensorWifiChannel(HiveAp hiveAp) throws Exception {
		HiveApWifi wifi0 = hiveAp.getWifi0();
		HiveApWifi wifi1 = hiveAp.getWifi1();
		if (wifi0.getOperationMode() == AhInterface.OPERATION_MODE_SENSOR) {
			wifi0.setChannel(AhInterface.CHANNEL_BG_AUTO);
			wifi0.setAdminState(AhInterface.ADMIN_STATE_UP);
		}
		if (wifi1.getOperationMode() == AhInterface.OPERATION_MODE_SENSOR
				&& hiveAp.isWifi1Available()) {
			wifi1.setChannel(AhInterface.CHANNEL_A_AUTO);
			wifi1.setAdminState(AhInterface.ADMIN_STATE_UP);
		}
	}

}
