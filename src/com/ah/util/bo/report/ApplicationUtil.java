package com.ah.util.bo.report;

import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.coder.AhEncoder;

public class ApplicationUtil {

	public static final int SIGNATURE_VER_3_1_0 = AhEncoder.ip2Int("3.1.0.0");
   
	public static final int SIGNATURE_VER_4_0_0 = AhEncoder.ip2Int("4.0.0.0");
	
	public static int getMaxSupportedAppCode(HiveAp hiveAp) {
		if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.1.0") < 0){
			return 711;
		}
		if (hiveAp.getSignatureVer() >= SIGNATURE_VER_4_0_0) {
			return 1257;
		}
		else if (hiveAp.getSignatureVer() >= SIGNATURE_VER_3_1_0) {
			return 712;
		} else {
			return 711;
		}
	}
	
	public static int getMinCustomAppCode() {
		return 19000;
	}
	
	public static int getWatchlistLimitation() {
		int watchlistLimitation = 7;
		try {
			watchlistLimitation = Integer.parseInt(ConfigUtil.getConfigInfo("gui", "watchlist_limitation", "7"));
		} catch(NumberFormatException e) {
			watchlistLimitation = 7;
		}
		return watchlistLimitation;
	}
	
	public static boolean isSupportL7Service(HiveAp hiveAp){
		return !hiveAp.isBranchRouter() && !hiveAp.isVpnGateway() &&
				hiveAp.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_L7_SERVICE);
	}
	
	
	public static void main(String[] args) {
		HiveAp hiveAp = new HiveAp();
		hiveAp.setSoftVer("6.0.2.0");
		hiveAp.setSignatureVer(SIGNATURE_VER_3_1_0);
		System.out.println(getMaxSupportedAppCode(hiveAp));
		
		hiveAp.setSoftVer("6.1.1.0");
		hiveAp.setSignatureVer(SIGNATURE_VER_3_1_0);
		System.out.println(getMaxSupportedAppCode(hiveAp));
		
		hiveAp.setSoftVer("6.1.1.0");
		hiveAp.setSignatureVer(SIGNATURE_VER_3_1_0 - 1);
		System.out.println(getMaxSupportedAppCode(hiveAp));		
		
	}

}
