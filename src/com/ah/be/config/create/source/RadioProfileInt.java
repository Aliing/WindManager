package com.ah.be.config.create.source;

import java.util.List;

import com.ah.be.config.create.CreateXMLException;
import com.ah.bo.wlan.SlaMappingCustomize.ClientPhyMode;
import com.ah.xml.be.config.BandSteeringModeValue;
import com.ah.xml.be.config.ClientLoadBalanceModeValue;

/**
 * 
 * @author zhang
 *
 */
public interface RadioProfileInt {
	
//	public static final String RADIO_PROFILE_MODE_A = "11a";
//	public static final String RADIO_PROFILE_MODE_BG = "11b/g";
	public static enum RadioPhyMode{
		M11a("11a"), M11bg("11b/g"), M11na("11na"), M11ng("11ng"), M11AC("11ac");
		
		private String value;
		
		RadioPhyMode(String value){
			this.value = value;
		}
		
		public String getValue(){
			return this.value;
		}
	}
	
	public static enum RadioChannelWidth{
		_20("20"), _40_above("40-above"), _40_below("40-below");
		
		private String value;
		
		RadioChannelWidth(String value){
			this.value = value;
		}
		
		public String getValue(){
			return this.value;
		}
	}
	
	public static enum RadioClientDenyType{
		_11b("11b"), _11abg("11abg");
		
		private String value;
		
		RadioClientDenyType(String value){
			this.value = value;
		}
		
		public String getValue(){
			return this.value;
		}
	}
	
	public static enum WmmType{
		background, best_effort, video, voice
	}
	
	public static final String RADIO_TX_RATE_AUTO="auto";
	public static final String RADIO_TX_RATE_11="11Mbps";
	public static final String RADIO_TX_RATE_12="12Mbps";
	public static final String RADIO_TX_RATE_18="18Mbps";
	public static final String RADIO_TX_RATE_1="1Mbps";
	public static final String RADIO_TX_RATE_24="24Mbps";
	public static final String RADIO_TX_RATE_2="2Mbps";
	public static final String RADIO_TX_RATE_36="36Mbps";
	public static final String RADIO_TX_RATE_48="48Mbps";
	public static final String RADIO_TX_RATE_5_POINT_5="5.5Mbps";
	public static final String RADIO_TX_RATE_54="54Mbps";
	public static final String RADIO_TX_RATE_6="6Mbps";
	public static final String RADIO_TX_RATE_9="9Mbps";
	
	public static final String LOAD_BALANCE_ROAMING_HIGH="high";
	public static final String LOAD_BALANCE_ROAMING_LOW="low";
	public static final String LOAD_BALANCE_ROAMING_MEDIUM="medium";
	public static final String LOAD_BALANCE_ROAMING_VERY_LOW="very-low";
	public static final String LOAD_BALANCE_ROAMING_VERY_OFF="-95";
	
	public boolean isConfigureRadioTree();
	
	public String getRadioProfileGuiName();
	
	public String getApVersion();

//	public boolean isConfigureRadioTree();
	
	public boolean isConfigureRadioProfile(int index);
	
	public String getUpdateTime();
	
	public String getProfileUpdatTime(int index);
	
	public int getRadioProfileSize();
	
	public String getName(int index);
	
	public int getBeaconPeriod(int index);
	
	public boolean isEnableBackhaulFailover(int index);
	
	public int getMaxClient(int index);
	
	public String getRadioPhyMode(int index);
	
	public boolean isConfigureRadioScan(int index);
	
	public boolean isShortPreamble(int index);
	
	public int getBackHaulTriggerTime(int index);
	
	public int getBackHaulHoldTime(int index);
	
	public int getScanInterval(int index);
	
	public boolean isEnableScanVoice(int index);
	
//	public boolean isConfigRadiuAcsp(int index);
	
	public boolean isConfigAcspMaxPower(int index);
	
	public int getAcspMaxPower(int index);
	
//	public boolean isConfigAcspAccess(int index);
	
	public boolean isConfigAcspAccessChannel(int index);
	
	public boolean isConfigAcspDfs(int index);
	
	public boolean isEnableAcspDfs(int index);
	
	public boolean isRadarDetectOnly(int index);
	
	public String getChannelAutoSelectTimeRange(int index);
	
	public int getChannelAutoSelectStation(int index);
	
//	public boolean isEnableRadioTurbo(int index);
	
	public boolean isHiveAp11n();
	
//	public boolean isConfigChannelWidth(int index);
	
//	public String getRadioChannelWidth(int index);
	
	public boolean isEnableAllow_11b_clients(int index);
	
	public boolean isEnableOnly_11n_clients(int index);
	
	public boolean isConfigShortGuardInterval(int index);
	
	public boolean isEnableShortGuardInterval(int index);
	
	public boolean isEnableAmpdu(int index);
	
	public int getTransmitChain(int index) throws CreateXMLException;
	
	public int getReceiveChain(int index) throws CreateXMLException;
	
	public boolean isConfigReceiveChain(int index);
	
	public boolean isConfigDenyClient(int index);
	
	public String getDenyClientValue(int index);
	
	public boolean isEnableScanAccessClient(int index);
	
	public boolean isEnablePowerSave(int index);
	
	public boolean isConfigChannelMode(int index);
	
	public boolean isConfigChannel_3(int index);
	
	public boolean isConfigChannel_4(int index);
	
	public boolean isConfigChannelCr(int index) throws CreateXMLException;
	
	public String getChannelCr(int index);
	
	public int getAifsValue(int index, WmmType wmmType);
	
	public int getCwmaxValue(int index, WmmType wmmType);
	
	public int getCwminValue(int index, WmmType wmmType);
	
	public int getTxoplimitValue(int index, WmmType wmmType);
	
	public boolean isNoack(int index, WmmType wmmType);
	
	public boolean isConfigInterMap(int index);
	
	public boolean isEnableInterMap(int index);
	
	public int getCrcThreshold(int index);
	
	public int getCuThreshold(int index);
	
	public int getShortInterval(int index);
	
	public int getRateSize(int index);
	
	public String getRateName(ClientPhyMode type, int indexRad, int indexRate);
	
	public int getSuccessValue(ClientPhyMode type, int indexRad, int indexRate);
	
	public int getUsageValue(ClientPhyMode type, int indexRad, int indexRate);
	
	public boolean isHighDensityEnable(int index);
	
	public boolean isTxRateHigh(int index);
	
	public boolean isTxRateLow(int index);
	
	public boolean isContinuousSuppressEnable(int index);
	
	public boolean isWeakSnrSuppressEnable(int index);
	
	public int getWeakSnrSuppressThreshold(int index);
	
	public boolean isClientLoadBalanceEnable(int index);
	
	public int getLoadBalanceHoldTime(int index);
	
	public int getLoadBalanceCuLimit(int index);
	
	public int getLoadBalanceIntLimit(int index);
	
	public int getLoadBalanceErrorLimit(int index);
	
	public boolean isSafetyNetEnable(int index);
	
	public int getSafetyNetTimeout(int index);
	
	public boolean isBroadcastProbeEnable(int index);
	
	public boolean isBroadcastOuiEnable(int index);
	
	public List<String> getBroadcastOuis(int index);
	
	public boolean isBandSteeringEnable(int index);
	
	public boolean isAllChannelsModelEnable(int index);
	
//	public boolean isCofigInterSwitch(int index);
	
//	public int getSwitchCuThreshold(int index);
	
	public int getSwitchIuThreshold(int index);
	
	public int getSwitchCrcErrThreshold(int index);
	
	public boolean isConfigInterSwitchEnable(int index);
	
	public boolean isConfigInterSwitchDisable(int index);
	
	public boolean isConfigInterSwitchNoStation(int index);
	
	public boolean isConfigAllChannelsModel(int index);
	
	public boolean isEnableDetectBssidSpoofing(int index);
	
	public BandSteeringModeValue getBandSteeringModeVlaue(int index);
	
	public boolean isConfigBandSteeringPrefer5G(int index);
	
	public boolean isConfigBandSteeringBalanceBand(int index);
	
	public int getBandSteeringMinimumRatio(int index);
	
	public int getBandSteeringLimitNumber(int index);
	
	public ClientLoadBalanceModeValue getClientLoadBalanceModeValue(int index);
	
	public boolean isConfigClientLoadBalanceMode(int index);
	
	public int getNeighborLoadQueryInterval(int index);
	
	public boolean isEnablePresence(int index);
	
	public int getPresenceTrapInterval(int index);
	
	public int getPresenceAgingTime(int index);
	
	public boolean isEnableConnectionAlarm();
	
	public int getChannelUtilizatioThreshold();
	
	public int getChannelUtilizatioInterval();
	
	public String getSensorChannelListValue(int index);
	
	public int getSensorDwellTime(int index);
	
	public int getPresenceAggrInterval(int index);
	
//	public String getChannelOffset(int index);
	
	public boolean isFrameburstEnabled(int index);
	
	public boolean isVHTEnabled(int index);
	
	public boolean isTxBeamformingExplicitMode(int index);
	
	public boolean isTxBeamformingEnabled(int index);
}
