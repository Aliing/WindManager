package com.ah.be.config.create.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.AhConfigDisconnectException;
import com.ah.be.config.AhConfigRetrievedException;
import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.CapwapSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.network.PPPoE;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.util.MgrUtil;

@SuppressWarnings("static-access")
public class HmCliSpecialHandling {
	
	private static final String[] AH_AHEAD_IP_CONFIG_CLIS = {"no interface mgt0 ip\n",
			"no interface mgt0 dhcp client fallback-to-static-ip\n",
			"no interface mgt0 dhcp client address-only\n",
			"no interface mgt0 dhcp client timeout\n", "no interface mgt0 dhcp client\n", };

	private static final String[] AH_IP_CONFIG_CLIS = { "interface mgt0 dhcp client\n",
			"interface mgt0 ip " };

	private static final String[] AH_BEHIND_IP_CONFIG_CLIS = { "interface mgt0 dhcp client timeout",
			"interface mgt0 dhcp client address-only\n",
			"interface mgt0 dhcp client fallback-to-static-ip\n", "interface mgt0 ip "};
	
	public static String recoverClis(String clis, String runningCfg, ConfigType configType, HiveAp hiveAp, String softVer) throws AhConfigRetrievedException, AhConfigDisconnectException {
		String newClis = clis;
		String regex;
		Pattern pattern;
		Matcher matcher;
		
		if ((softVer == null || softVer.isEmpty()) && hiveAp != null) {
			softVer = hiveAp.getSoftVer();
		}
		
		if(configType != null && configType == ConfigType.AP_FULL && isExistsSnmpCli(newClis)){
			if (!newClis.endsWith("\n")) {
				newClis = newClis + "\n";
			}
			newClis = newClis + "no snmp reader version any community hivecommunity\n";
		}
		
		// Append new config version in the end.
		if(configType == ConfigType.AP_FULL){
			int newConfigVer = hiveAp.getNewConfigVerNum();
			String configVerCli = AhCliFactory.configVerNum(newConfigVer);
			newClis += configVerCli;
		}else if(configType == ConfigType.USER_FULL){
			if (NmsUtil.compareSoftwareVersion("3.3.1.0", softVer) > 0) {
				String cachedOnlyConfig = HmCliSpecialHandling.generateCachedOnlyConfig(hiveAp, false);
				newClis = AhCliFactory.removeAllLocalUsers() + newClis + cachedOnlyConfig;
			}
		}
		
		if(configType != null && configType == ConfigType.AP_FULL){
			String wanInf = getWanInterfaceName(hiveAp);
			if(isPPPoESettingEnable(hiveAp) && wanInf != null){
				StringBuilder buf = new StringBuilder();
				buf.append("interface "+wanInf+" pppoe enable\n");
				buf.append("interface "+wanInf+" pppoe username ").append(hiveAp.getPppoeAuthProfile().getUsername());
				buf.append(" password ").append(AhConfigUtil.hiveApCommonEncrypt(hiveAp.getPppoeAuthProfile().getPassword())).append("\n");
				if(hiveAp.getPppoeAuthProfile().getEncryptionMethod() == PPPoE.ENCRYPTION_METHOD_CHAP){
					buf.append("interface "+wanInf+" pppoe auth-method chap\n");
				}else if(hiveAp.getPppoeAuthProfile().getEncryptionMethod() == PPPoE.ENCRYPTION_METHOD_PAP){
					buf.append("interface "+wanInf+" pppoe auth-method pap\n");
				}else{
					buf.append("interface "+wanInf+" pppoe auth-method any\n");
				}
				//from 6.1.3.0 no need CLI "save config pppoe"
				if(NmsUtil.compareSoftwareVersion(softVer, "6.1.3.0") < 0){
					buf.append("save config pppoe\n");
				}
				newClis += buf.toString();
			}
		}

		if (configType != null && hiveAp != null && softVer != null) {
			if (configType == ConfigType.AP_FULL
					&& NmsUtil.compareSoftwareVersion(softVer, "3.5.0.0") > 0) {
				if (!newClis.endsWith("\n")) {
					newClis = newClis + "\n";
				}

//				newClis = newClis + "capwap client transport HTTP no-disconnect\n";

				// if (hiveAp.getTransferPort() > 0) {
				// newClis = newClis +
				// "capwap client server port "+hiveAp.getTransferPort()+" no-disconnect\n";
				// }

				String proxyName = hiveAp.getProxyName();
				int proxyPort = hiveAp.getProxyPort();

				if (proxyName != null && !proxyName.isEmpty() && proxyPort > 0) {
					newClis = newClis + "capwap client HTTP proxy name " + proxyName + " port "
							+ proxyPort + " no-disconnect\n";
				}

				String proxyUser = hiveAp.getProxyUsername();
				String proxyPass = hiveAp.getProxyPassword();

				if (proxyUser != null && !proxyUser.isEmpty() && proxyPass != null
						&& !proxyPass.isEmpty()) {
					newClis = newClis + "capwap client HTTP proxy user " + proxyUser + " password "
							+ AhConfigUtil.hiveApCommonEncrypt(proxyPass) + " no-disconnect\n";
				}
			}
		}
		
		regex = "(qos) (classifier-profile|marker-profile) (.+) (_80211e)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 80211e");

		regex = "(qos) (classifier-profile|marker-profile) (.+) (_8021p)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 8021p");

		regex = "(qos) (classifier-profile|marker-profile) (.+) (interface-ssid)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 interface/ssid");

		regex = "(qos) (classifier-profile|marker-profile) (.+) (interface-ssid-only)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 interface/ssid-only");

		regex = "(qos) (classifier-map|marker-map) (_80211e)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 80211e");

		regex = "(qos) (classifier-map|marker-map) (_8021p)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 8021p");

		regex = "(ip-policy|mac-policy) (.+) (id) (.+) (before)( {2,})";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 ");

		regex = "(ssid) (.+) (_11a-rate-set)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 11a-rate-set");

		regex = "(ssid) (.+) (_11g-rate-set)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 11g-rate-set");

		regex = "(ssid) (.+) (_11n-mcs-rate-set)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 11n-mcs-rate-set");
		
		regex = "(ssid) (.+) (_11n-mcs-expand-rate-set)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 11n-mcs-expand-rate-set");
		
		regex = "(ssid) (.+) (_11ac-mcs-rate-set)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 11ac-mcs-rate-set");

		regex = "(radio) (profile) (.+) (_11n-clients-only)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 11n-clients-only");

		regex = "(radio) (profile) (.+) (acsp) (channel-model) (_3-channels)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 3-channels");

		regex = "(radio) (profile) (.+) (acsp) (channel-model) (_4-channels)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 4-channels");
		
		regex = "(radio) (profile) (.+) (old-high-density)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 high-density");

		regex = "(auto-psk-user-group) (.+) (auto-generation)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("user-group $2 $3");

		regex = "(aaa) (radius-server) (primary|backup1|backup2|backup3) (.+) (old-shared-secret)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 shared-secret");

		regex = "(ssid) (.+) (security) (aaa) (radius-server) (primary|backup1|backup2|backup3) (.+) (old-shared-secret)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 $6 $7 shared-secret");

		regex = "(security-object) (.+) (security) (aaa) (radius-server) (primary|backup1|backup2|backup3) (.+) (old-shared-secret)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 $6 $7 shared-secret");

		regex = "(radio) (profile) (.+) (benchmark) (phymode) _(11a|11b|11g|11n)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 $6");

		regex = "(interface) (mgt0) (old-default-ip-prefix)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 default-ip-prefix");

		regex = "(ssid) (.+) (security) (protocol-suite) (wpa-auto-psk|wpa-tkip-psk|wpa-aes-psk|wpa2-tkip-psk|wpa2-aes-psk) (hex-key|ascii-key) (.+) (old-rekey-period)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 $6 $7 rekey-period");

		regex = "(security-object) (.+) (security) (protocol-suite) (wpa-auto-psk|wpa-tkip-psk|wpa-aes-psk|wpa2-tkip-psk|wpa2-aes-psk) (hex-key|ascii-key) (.+) (old-rekey-period)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 $6 $7 rekey-period");

		regex = "(ssid) (.+) (security) (protocol-suite) (wpa-auto-psk|wpa-tkip-psk|wpa-aes-psk|wpa2-tkip-psk|wpa2-aes-psk) (hex-key|ascii-key) (.+) (old-gmk-rekey-period)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 $6 $7 gmk-rekey-period");

		regex = "(security-object) (.+) (security) (protocol-suite) (wpa-auto-psk|wpa-tkip-psk|wpa-aes-psk|wpa2-tkip-psk|wpa2-aes-psk) (hex-key|ascii-key) (.+) (old-gmk-rekey-period)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 $6 $7 gmk-rekey-period");

		regex = "(ssid) (.+) (security) (protocol-suite) (wpa-auto-8021x|wpa-tkip-8021x|wpa-aes-8021x|wpa2-tkip-8021x|wpa2-aes-8021x) (old-rekey-period)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 rekey-period");

		regex = "(security-object) (.+) (security) (protocol-suite) (wpa-auto-8021x|wpa-tkip-8021x|wpa-aes-8021x|wpa2-tkip-8021x|wpa2-aes-8021x) (old-rekey-period)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 rekey-period");
		
		regex = "(security-object) (.+) (security) (protocol-suite) _802.1x";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 802.1x");

		regex = "(ssid) (.+) (security) (protocol-suite) (wpa-auto-8021x|wpa-tkip-8021x|wpa-aes-8021x|wpa2-tkip-8021x|wpa2-aes-8021x) (old-gmk-rekey-period)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 gmk-rekey-period");

		regex = "(security-object) (.+) (security) (protocol-suite) (wpa-auto-8021x|wpa-tkip-8021x|wpa-aes-8021x|wpa2-tkip-8021x|wpa2-aes-8021x) (old-gmk-rekey-period)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3 $4 $5 gmk-rekey-period");

		regex = "(capwap) (client) (server) (cr-primary)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3");

		regex = "(capwap) (client) (server) (cr-backup)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 $3");

		regex = "(no) (schedule-ppsk) (.+)\n";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("no schedule $3 ppsk\n");

		regex = "(schedule-ppsk) (.+) ppsk ";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("schedule $2 ppsk ");

		regex = "user-group (.+) auto-generation old-index-range (.+)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("user-group $1 auto-generation index-range $2");
		
		regex = "mobile-device-policy (.+) rule (.+) before (.+) rule (.+)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("mobile-device-policy $1 rule $2 before rule $4");
		
		regex = "security wlan-idp profile (.+) old-mitigate";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("security wlan-idp profile $1 mitigate");
		
		regex = "aaa radius-server local db-type (ldap-server|open-directory) (primary|backup1|backup2|backup3) old-filter_attr";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("aaa radius-server local db-type $1 $2 filter_attr");
		
		regex = "_802.1x-mac-table (expire-time|suppress-interval)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("802.1x-mac-table $1");
		
		regex = "_ip-nat-policy nat-policy";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("ip nat-policy");
		
		regex = "interface eth (1/\\d{1,2})";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("interface eth$1");
		
		regex = "interface (agg|vlan) (\\d)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("interface $1$2");
		
		regex = "interface _vlan-id (\\d)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("interface vlan $1");
		
		regex = "no interface (eth1/\\d{1,2})\n";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll(getSRInterfaceResetCLIs());
		
		regex = "no interface (agg\\d{1,2})\n";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll(getSRInterfaceAggResetCLIs());
		
		regex = "qos marker-map _user-defined-8021p";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("qos marker-map 8021p");
		
		regex = "qos marker-map _user-defined-diffserv";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("qos marker-map diffserv");
		
		regex = "user-profile (.+) qos-marker-map _8021p";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("user-profile $1 qos-marker-map 8021p");
		
		regex = "track (.+) old-interval";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("track $1 interval");
		
		regex = "system power-mode _802.3af";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("system power-mode 802.3af");
		
		regex = "system power-mode _802.3at";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("system power-mode 802.3at");

		// newClis = this.mergeUserIndexRange(newClis);
 
		if (isCapwapRollbackEnable(newClis, hiveAp)) {
			String rollbackEnable = "config rollback enable\n";
			String rollbackPoint = "config rollback capwap-disconnect\n";
			if(configType == ConfigType.AP_AUDIT || configType == ConfigType.AP_DELTA){
				newClis = rollbackEnable + rollbackPoint + newClis;
			}else{
				newClis = rollbackEnable + newClis;
			}
		}
		
		/**add usbmodem restart cli */
		if(configType != null && configType == ConfigType.AP_AUDIT){
			newClis = isExistsUsbModeCli(newClis, runningCfg, hiveAp);
		}

		/** OEM hive|aerohive|hivemanager */
		boolean isOEMHM = false;
		if(hiveAp.getDownloadInfoView() != null){
			isOEMHM = hiveAp.getDownloadInfoView().isOemHm();
		}else if(hiveAp.getDownloadInfo() != null){
			isOEMHM = hiveAp.getDownloadInfo().isOemHm();
		}
		
		if(isOEMHM && NmsUtil.compareSoftwareVersion(softVer, "3.5.3.0") >= 0){
			String[][] newSTR = {
					{"hive", "cluster"},
					{"aerohive", "Black-Box"},
					{"hivemanager", "SmartPath-EMS"},
					{"hiveui", "clusterui"}
			};
			newClis = getOEMClis(newClis, newSTR);
		}
		

			//remove cli when PPPoE enable
		if(configType != null && configType == ConfigType.AP_FULL && isPPPoESettingEnable(hiveAp)){
			regex = "\ninterface eth0 ip (.+)\n";
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(newClis);
			newClis = matcher.replaceAll("\n");
			
//			if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER
//					&& (hiveAp.getHiveApModel()==HiveAp.HIVEAP_MODEL_SR24 || hiveAp.getHiveApModel()==HiveAp.HIVEAP_MODEL_SR48)){
//						//do nothing,don't cut ip router cli
//				}else{	
//			regex = "ip route net (.+) gateway (.+)\n";
//			pattern = Pattern.compile(regex);
//			matcher = pattern.matcher(newClis);
//			newClis = matcher.replaceAll("");
//				}
			
			regex = "\ninterface eth0 dhcp client\n";
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(newClis);
			newClis = matcher.replaceAll("\n");
		}
		
		return newClis;
	}
	
	public static String hideSensitiveMessages(String clis) {
		String regex = "(shared-secret|shared-key|private-key-password|ascii-key|hex-key|passphrase|ascii) (\\S+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(clis);
		String newClis = matcher.replaceAll("$1 ******");

		regex = "(hex|password) (\\S{6}s\\S{5}o\\S{5}r\\S{5}e\\S{5}a\\S{6})";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 ******");

		regex = "(md5) (key) (\\S{6}s\\S{5}o\\S{5}r\\S{5}e\\S{5}a\\S{6})";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("$1 $2 ******");

		regex = "admin root-admin (\\S+) password (\\S+)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("admin root-admin $1 password ******");

		regex = "admin read-only (\\S+) password (\\S+)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("admin read-only $1 password ******");

		regex = "http-auth user (\\S+) password (\\S+)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		newClis = matcher.replaceAll("http-auth user $1 password ******");
		// String regex = " (\\S{6}s\\S{5}o\\S{5}r\\S{5}e\\S{5}a\\S{6}) ";
		// Pattern pattern = Pattern.compile(regex);
		// Matcher matcher = pattern.matcher(clis);
		// String newClis = matcher.replaceAll(" ****** ");

		return newClis;
	}
	
	public static String reorganizeIpRelatedClis(String clis) {
		String reorganizedClis = clis;
		String ipConfigCli = null;
		StringBuilder ipRelatedCliBuf = new StringBuilder();
		if(!reorganizedClis.endsWith("\n")){
			reorganizedClis += "\n";
		}

		// Append the CLIs into buffer which are required to be placed before
		// any IP-related CLI.
		for (String cliPrefix : AH_AHEAD_IP_CONFIG_CLIS) {
			String ipRelatedCli = getTargetCli(reorganizedClis, cliPrefix);

			if (ipRelatedCli != null) {
				// Append the complete CLI searched into buffer.
				ipRelatedCliBuf.append(ipRelatedCli);

				// Remove the complete CLI searched from the original CLI
				// aggregation.
				reorganizedClis = reorganizedClis.replaceFirst(ipRelatedCli, "");
			}
		}

		for (String ipConfigCliPrefix : AH_IP_CONFIG_CLIS) {
			ipConfigCli = getTargetCli(reorganizedClis, ipConfigCliPrefix);

			if (ipConfigCli != null) {
				break;
			}
		}

		if (ipConfigCli != null) {
			// Append the complete IP-related CLI searched into buffer.
			ipRelatedCliBuf.append(ipConfigCli);

			// Append the CLIs into buffer which are required to be placed
			// behind any IP-related CLI.
			for (String cliPrefix : AH_BEHIND_IP_CONFIG_CLIS) {
				String ipRelatedCli = getTargetCli(reorganizedClis, cliPrefix);

				if (ipRelatedCli != null && !ipRelatedCli.equals(ipConfigCli)) {
					// Append the complete CLI searched into buffer.
					ipRelatedCliBuf.append(ipRelatedCli);

					// Remove the complete CLI searched from the original CLI
					// aggregation.
					reorganizedClis = reorganizedClis.replaceFirst(ipRelatedCli, "");
				}
			}

			// Replace the searched IP-related CLI in the original CLI
			// aggregation with the reorganized IP-related CLIs.
			reorganizedClis = reorganizedClis.replace(ipConfigCli, ipRelatedCliBuf.toString());
		} else {
			// Keep the original CLI aggregation if no one IP-related CLI is
			// searched.
			reorganizedClis = clis;
		}

		return reorganizedClis;
	}
	
	public static String runningConfigReplace(String runningCfg){
		// Fix the SNMP issue in the HM3.5r4 release.
		if (runningCfg.contains("no snmp reader version any community hivecommunity")) {
			runningCfg = runningCfg.replace("no snmp reader version any community hivecommunity", "");
		} else if(isExistsSnmpCli(runningCfg)) {
			runningCfg += runningCfg.endsWith("\n")
					? "snmp reader version any community hivecommunity\n"
					: "\nsnmp reader version any community hivecommunity\n";
		}
		
		return runningCfg;
	}
	
	public static boolean isCapwapRollbackSettingEnable(HiveAp hiveAp) {
		if (NmsUtil.compareSoftwareVersion("3.5.0.0", hiveAp.getSoftVer()) > 0) {
			return false;
		}

		List<?> capwapSettings = MgrUtil.getQueryEntity().executeQuery("select enableRollback from " + CapwapSettings.class.getSimpleName(), null,
				new FilterParams("owner.domainName", HmDomain.HOME_DOMAIN), 1);

		return !capwapSettings.isEmpty() && (Boolean) capwapSettings.get(0);
	}
	
	public static String getOEMClis(String targetClis, String[][] replaceSource){
		String hive=null, aerohive=null, hivemanager=null, hiveui=null;
		String rpHive=null, rpAerohive=null, rpHivemanager=null, rphiveui=null;
		String clis = targetClis;
		
		// all cli should start with "\n" and end with "\n"
		if(!clis.startsWith("\n")){
			clis = "\n" + clis;
		}
		if(!clis.endsWith("\n")){
			clis += "\n";
		}
		if(replaceSource == null || replaceSource.length == 0){
			return targetClis;
		}
		
		//get hive, aerohive, hivemanager
		for (String[] replacements : replaceSource) {
			if ("hive".equalsIgnoreCase(replacements[0])) {
				hive = replacements[0];
				rpHive = replacements[1];
			}
			if ("aerohive".equalsIgnoreCase(replacements[0])) {
				aerohive = replacements[0];
				rpAerohive = replacements[1];
			}
			if ("hivemanager".equalsIgnoreCase(replacements[0])) {
				hivemanager = replacements[0];
				rpHivemanager = replacements[1];
			}
			if ("hiveui".equalsIgnoreCase(replacements[0])) {
				hiveui = replacements[0];
				rphiveui = replacements[1];
			}
		}
		if(hive == null || aerohive == null || hivemanager == null){
			for (String[] replacements : replaceSource) {
				if ("hive".equalsIgnoreCase(replacements[1])) {
					hive = replacements[0];
					rpHive = replacements[1];
				}
				if ("aerohive".equalsIgnoreCase(replacements[1])) {
					aerohive = replacements[0];
					rpAerohive = replacements[1];
				}
				if ("hivemanager".equalsIgnoreCase(replacements[1])) {
					hivemanager = replacements[0];
					rpHivemanager = replacements[1];
				}
				if ("hiveui".equalsIgnoreCase(replacements[1])) {
					hiveui = replacements[0];
					rphiveui = replacements[1];
				}
			}
		}
		if(hive == null || aerohive == null || hivemanager == null){
			return targetClis;
		}
		
		String regex;
		Pattern pattern;
		Matcher matcher;
		
		/** OEM hive */
		if(hive != null){
			regex = "\n(no )?"+hive+" (.+) security wlan dos "+hive+"-level";
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(clis);
			clis = matcher.replaceAll("\n$1"+rpHive+" $2 security wlan dos "+rpHive+"-level");
			
			regex = "\n(no )?"+hive+" (.+) (password|neighbor|rts-threshold|security|frag-threshold|manage|)";
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(clis);
			clis = matcher.replaceAll("\n$1"+rpHive+" $2 $3");
			
			regex = "\n(no )?"+hive+" (.+)\n";
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(clis);
			clis = matcher.replaceAll("\n$1"+rpHive+" $2\n");
			
			regex = "\n(no )?interface (\\S+) "+hive;
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(clis);
			clis = matcher.replaceAll("\n$1interface $2 "+rpHive);
			
//			regex = "\n(no )?interface (.+) "+hive+" (.+)\n";
//			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
//			matcher = pattern.matcher(clis);
//			clis = matcher.replaceAll("\n$1interface $2 "+rpHive+" $3\n");
		}
		
		/** OEM aerohive */
		if(aerohive != null){
			regex = "\n(no )?location "+aerohive;
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(clis);
			clis = matcher.replaceAll("\n$1location "+rpAerohive);
		}
		
		/** OEM hivemanager */
		if(hivemanager != null){
			regex = "\n(no )?interface (.+) dhcp-server options "+hivemanager;
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(clis);
			clis = matcher.replaceAll("\n$1interface $2 dhcp-server options "+rpHivemanager);
			
			regex = "\n(no )?interface (.+) dhcp client option custom "+hivemanager;
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(clis);
			clis = matcher.replaceAll("\n$1interface $2 dhcp client option custom "+rpHivemanager);
		}
		
		/** OEM hiveui */
		if(hiveui != null){
			regex = "\n(no )?hiveui (enable|cas)";
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(clis);
			clis = matcher.replaceAll("\n$1"+rphiveui+" $2");
		}
		
		if(clis.startsWith("\n")){
			clis = clis.substring(1);
		}
		if(clis.endsWith("\n")){
			clis = clis.substring(0, clis.length()-1);
		}
//		clis = clis.substring(1, clis.length()-1);
		return clis;
	}
	
	public static String generateCachedOnlyConfig(HiveAp hiveAp, boolean removeCachedOnlyConfigs) {
		if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.2.0.0") < 0) {
			return "";
		}

		Collection<Short> availableUserGroupTypes = new ArrayList<Short>(3);
		availableUserGroupTypes.add(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL);
		availableUserGroupTypes.add(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE);
		availableUserGroupTypes.add(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN);

		List<?> localUserGroupList = MgrUtil.getQueryEntity().executeQuery("select bo.radiusServerProfile.localUserGroup from " + HiveAp.class.getSimpleName() + " bo", null, new FilterParams("bo.id = :s1 and bo.radiusServerProfile.databaseType in (:s2)", new Object[] { hiveAp.getId(), availableUserGroupTypes }));

		for (Iterator<?> localUserGroupIter = localUserGroupList.iterator(); localUserGroupIter.hasNext();) {
			LocalUserGroup localUserGroup = (LocalUserGroup) localUserGroupIter.next();

			if (localUserGroup.getCredentialType() != LocalUserGroup.USERGROUP_CREDENTIAL_DRAM) {
				localUserGroupIter.remove();
			}
		}

		if (localUserGroupList.isEmpty()) {
			return "";
		}

		// load users
		List<LocalUser> localUserList = MgrUtil.getQueryEntity().executeQuery(LocalUser.class, null, new FilterParams("localUserGroup", localUserGroupList), hiveAp.getOwner().getId());
		StringBuilder userBuffer = new StringBuilder();

		if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.3.1.0") >= 0) {
			for (Object obj : localUserGroupList) {
				LocalUserGroup group = (LocalUserGroup) obj;
				/** user-group <group_name> */
				userBuffer.append("user-group ");
				String groupName = CLICommonFunc.escapeSpecialChar(group.getGroupName());
				userBuffer.append(groupName);
				userBuffer.append("\n");

				/** user-group <group_name> vlan */
				if (group.getVlanId() > 0) {
					userBuffer.append("user-group ");
					userBuffer.append(groupName);
					userBuffer.append(" vlan-id ");
					userBuffer.append(group.getVlanId());
					userBuffer.append("\n");
				}

				/** user-group <group_name> user-attribute */
				if (group.getUserProfileId() > 0) {
					userBuffer.append("user-group ");
					userBuffer.append(groupName);
					userBuffer.append(" user-attribute ");
					userBuffer.append(group.getUserProfileId());
					userBuffer.append("\n");
				}

				/** user-group <group_name> reauth-interval */
				userBuffer.append("user-group ");
				userBuffer.append(groupName);
				userBuffer.append(" reauth-interval ");
				userBuffer.append(group.getReauthTime());
				userBuffer.append("\n");

				/** user-group <group_name> cache-mode */
				userBuffer.append("user-group ");
				userBuffer.append(groupName);
				userBuffer.append(" cache-mode temporary\n");
			}

			for (LocalUser user : localUserList) {
				/** user <user_name> */
				String userName = CLICommonFunc.escapeSpecialChar(user.getUserName());
				userBuffer.append("user ");
				userBuffer.append(userName);
				userBuffer.append("\n");

				/** user <user_name> password */
				userBuffer.append("user ");
				userBuffer.append(userName);
				userBuffer.append(" password ");
				userBuffer.append(AhConfigUtil.ahEncrypt(user.getLocalUserPassword()));
				userBuffer.append("\n");

				/** user <user_name> group */
				userBuffer.append("user ");
				userBuffer.append(userName);
				userBuffer.append(" group ");
				String groupName = CLICommonFunc.escapeSpecialChar(user.getLocalUserGroup()
						.getGroupName());
				userBuffer.append(groupName);
				userBuffer.append("\n");
			}

			for (Object obj : localUserGroupList) {
				LocalUserGroup group = (LocalUserGroup) obj;
				/** aaa radius-server local user-group <group_name> */
				String groupName = CLICommonFunc.escapeSpecialChar(group.getGroupName());
				userBuffer.append("aaa radius-server local user-group ");
				userBuffer.append(groupName);
				userBuffer.append("\n");
			}
		} else {
			if (localUserList.isEmpty()) {
				return "";
			}

			if (removeCachedOnlyConfigs) {
				String removeCachedUsersCli = AhCliFactory.removeCachedLocalUsers();
				userBuffer.append(removeCachedUsersCli);
			}

			for (LocalUser localUser : localUserList) {
				String userGroupName = CLICommonFunc.escapeSpecialChar(localUser
						.getUserGroupName());
				String userName = CLICommonFunc.escapeSpecialChar(localUser.getUserName());
				String password = AhConfigUtil.ahEncrypt(localUser.getLocalUserPassword());
				userBuffer.append("exec aaa radius-server local user-name ").append(userName)
						.append(" password ").append(password);

				if (!userGroupName.isEmpty()) {
					userBuffer.append(" user-group ").append(userGroupName);
				}

				userBuffer.append("\n");
			}
		}

		return userBuffer.toString();
	}
	
	/** private function start */
	
	private static boolean isExistsSnmpCli(String clis){
		String regex = "(snmp) (reader) (version) (any|v1|v2c|v3) (community)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(clis);
		return matcher.find();
	}
	
	private static String getSRInterfaceResetCLIs(){
		return "no interface $1 agg\n"+
				"no interface $1 speed\n"+
				"no interface $1 duplex\n"+
				"no interface $1 flow-control\n"+
				"no interface $1 link-debounce\n"+
				"no interface $1 security-object\n"+
				"no interface $1 switchport mode\n"+
				"no interface $1 switchport user-profile-attribute\n"+
				"no interface $1 spanning-tree\n"+
				"no interface $1 shutdown\n"+
				"no interface $1 storm-control rate-limit\n"+
				"no interface $1 storm-control type\n"+
				"no interface $1 qos-classifier\n"+
				"no interface $1 qos-marker\n"+
				"no interface $1 qos-shaper\n"+
				"interface $1 link-discovery cdp receive enable\n"+
				"interface $1 link-discovery lldp receive enable\n"+
				"interface $1 link-discovery lldp transmit enable\n";
	}
	
	private static String getSRInterfaceAggResetCLIs(){
		return "no interface $1 speed\n"+
				"no interface $1 duplex\n"+
				"no interface $1 flow-control\n"+
				"no interface $1 switchport mode\n"+
				"no interface $1 spanning-tree\n"+
				"no interface $1 shutdown\n"+
				"no interface $1 qos-classifier\n"+
				"no interface $1 qos-marker\n"+
				"no interface $1 qos-shaper\n";
	}
	
	private static boolean isCapwapRollbackEnable(String newClis, HiveAp hiveAp) {
		if(!isCapwapRollbackSettingEnable(hiveAp)){
			return false;
		}

		String regex = "(ip) (route) (net) (.+) (.+) (gateway) (.+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(newClis);

		if (matcher.find()) {
			return true;
		}

		regex = "(interface) (mgt0) (dhcp|dhcp-server|hive|ip|ip-helper|l3-roaming-group|mtu|native-vlan|vlan)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);

		if (matcher.find()) {
			return true;
		}

		regex = "(interface) (eth0|eth1|red0|agg0) (bind|duplex|mode|qos-marker|rate-limit|shutdown|speed|allowed-vlan)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);

		if (matcher.find()) {
			return true;
		}

		regex = "(interface) (wifi0|wifi1) (hive|mode|radio)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);

		if (matcher.find()) {
			return true;
		}
		
		regex = "(interface) (wifi0|wifi1) hive (.+) shutdown";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);
		if (matcher.find()) {
			return true;
		}

		regex = "(radio) (profile) (.+) (phymode) (11a|11b/g|11na|11ng)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);

		if (matcher.find()) {
			return true;
		}

		regex = "capwap client server name (.+)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);

		if (matcher.find()) {
			return true;
		}

		regex = "capwap client server backup name (.+)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);

		if (matcher.find()) {
			return true;
		}

		regex = "capwap client server port (.+)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);

		if (matcher.find()) {
			return true;
		}

		regex = "capwap client dtls hm-defined-passphrase (.+) key-id (.+)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(newClis);

		return matcher.find();
	}
	
	private static String isExistsUsbModeCli(String newclis, String runningCfg, HiveAp hiveAp) throws AhConfigRetrievedException, AhConfigDisconnectException{
		String regex = "(usbmodem) (mode) (primary-wan|on-demand|always-connected)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(newclis);
		if(matcher.find()){
			if(matcher.group().contains("primary-wan")){
				if (!newclis.endsWith("\n")) {
					newclis = newclis + "\n";
				}
				newclis = newclis + AhCliFactory.getRebootCli("00:00:10");
			}else if(runningCfg != null){
				regex = "(usbmodem) (mode) (primary-wan)";
				pattern = Pattern.compile(regex);
				matcher = pattern.matcher(runningCfg);
				if(matcher.find()){
					if (!newclis.endsWith("\n")) {
						newclis = newclis + "\n";
					}
					newclis = newclis + AhCliFactory.getRebootCli("00:00:10");
				}
			}
		}
		return newclis;
	}
	
	private static boolean isPPPoESettingEnable(HiveAp hiveAp) {
		return hiveAp != null && hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER 
			&& NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "5.0.2.0") > 0 
			&& hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_330 && hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_350
			&& hiveAp.isEnablePppoe()
			&& hiveAp.getPppoeAuthProfile() != null;
	}
	
	private static String getTargetCli(String clis, String cliPrefix) {
		String targetCli = null;
		int beginIndex = clis.indexOf(cliPrefix);

		if (beginIndex > -1) {
			int endIndex = clis.indexOf("\n", beginIndex);
			targetCli = clis.substring(beginIndex, endIndex + 1);
		}

		return targetCli;
	}
	
	private static String getWanInterfaceName(HiveAp hiveAp){
		hiveAp = MgrUtil.getQueryEntity().findBoById(HiveAp.class, hiveAp.getId(), new QueryBo(){
			@Override
			public Collection<HmBo> load(HmBo bo) {
				if(bo instanceof HiveAp){
					HiveAp hiveAp = (HiveAp)bo;
					if(hiveAp.getDeviceInterfaces() != null){
						hiveAp.getDeviceInterfaces().values();
					}
				}
				return null;
			}
			
		});
		
		if(hiveAp.getDeviceInterfaces() == null){
			return null;
		}
		
		for(DeviceInterface dInter : hiveAp.getDeviceInterfaces().values()){
			if(dInter.getConnectionType().equals(String.valueOf(AhInterface.CONNECTION_PPPOE))){
				return DeviceInfType.getInstance(dInter.getDeviceIfType(), hiveAp.getHiveApModel()).getCLIName(hiveAp.getHiveApModel());
			}
		}
		
		return null;
	}
}
