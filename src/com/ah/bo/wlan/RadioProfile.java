/**
 *@filename		RadioProfile.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-29 PM 03:57:53
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *add wmm from 3.3r1
 */
package com.ah.bo.wlan;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.wlan.RadioProfileWmmInfo.AccessCategory;
import com.ah.bo.wlan.SlaMappingCustomize.ClientPhyMode;
import com.ah.ui.actions.config.SlaMappingCustomizeAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "RADIO_PROFILE", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"OWNER", "RADIONAME" }) })
@org.hibernate.annotations.Table(appliesTo = "RADIO_PROFILE", indexes = { @Index(name = "RADIO_PROFILE_OWNER", columnNames = { "OWNER" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RadioProfile implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String radioName = "";

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	// modify by zy
	private boolean scanAllChannel = true;
    
	@Column(length = 64)
	private String scanChannels;

	@Range(min = 10, max = 30000)
	private int dellTime = 1200;

	private boolean defaultFlag;

	private boolean cliDefaultFlag;

	private boolean enabledBssidSpoof;
	
	private boolean enableVHT;
	
	private boolean enableFrameburst;

	public static final short RADIO_PROFILE_MODE_BG = 1;

	public static final short RADIO_PROFILE_MODE_A = 2;

	public static final short RADIO_PROFILE_MODE_NA = 4;

	public static final short RADIO_PROFILE_MODE_NG = 5;
	
	//add 11ac mode
	public static final short RADIO_PROFILE_MODE_AC = 6;

	public static EnumItem[] ENUM_RADIO_PROFILE_MODE = MgrUtil.enumItems(
			"enum.radioProfileMode.", new int[] { RADIO_PROFILE_MODE_BG,
					RADIO_PROFILE_MODE_A, RADIO_PROFILE_MODE_NA,
					RADIO_PROFILE_MODE_NG, RADIO_PROFILE_MODE_AC });
	
	public static final short TXBEAMFORMING_MODE_AUTO = 1;
	
	public static final short TXBEAMFORMING_MODE_EXPLICIT = 2;
	
	public static EnumItem[] ENUM_TX_BEAMFORMING_MODE = MgrUtil.enumItems(
			"enum.txbeamformingMode.", new int[] { TXBEAMFORMING_MODE_AUTO, TXBEAMFORMING_MODE_EXPLICIT});
	
	private boolean enabledTxbeamforming = false;
	
	private short txBeamformingMode = TXBEAMFORMING_MODE_AUTO;

	private short radioMode = RADIO_PROFILE_MODE_BG;

	private boolean turboMode;

	public static final short RADIO_PROFILE_PREAMBLE_SHORT = 1;

	public static final short RADIO_PROFILE_PREAMBLE_LONG = 2;

	public static EnumItem[] ENUM_RADIO_PROFILE_PREAMBLE = MgrUtil
			.enumItems("enum.radioProfilePreamble.", new int[] {
					RADIO_PROFILE_PREAMBLE_SHORT, RADIO_PROFILE_PREAMBLE_LONG });

	private short shortPreamble = RADIO_PROFILE_PREAMBLE_SHORT;

	public static final short RADIO_TRANSMIT_RATE_AUTO = 1;

	public static EnumItem[] RADIO_TRANSMIT_RATE_BG = MgrUtil.enumItems(
			"enum.radioTransmitRateBG.", getIndexForRate(13));

	public static EnumItem[] RADIO_TRANSMIT_RATE_NG = MgrUtil.enumItems(
			"enum.radioTransmitRateNG.", getIndexForRate(29));

	public static EnumItem[] RADIO_TRANSMIT_RATE_A = MgrUtil.enumItems(
			"enum.radioTransmitRateA.", getIndexForRate(9));

	public static EnumItem[] RADIO_TRANSMIT_RATE_NA = MgrUtil.enumItems(
			"enum.radioTransmitRateNA.", getIndexForRate(33));

	public static final short RADIO_PROFILE_CHANNEL_WIDTH_20 = 1;

	public static final short RADIO_PROFILE_CHANNEL_WIDTH_40A = 2;

	public static final short RADIO_PROFILE_CHANNEL_WIDTH_40B = 3;

	public static final short RADIO_PROFILE_CHANNEL_WIDTH_80 = 4;
	
	public static final short RADIO_PROFILE_CHANNEL_WIDTH_40 = 5;

	public static EnumItem[] RADIO_PROFILE_CHANNEL_WIDTH = MgrUtil.enumItems(
			"enum.radioProfileChannelWidth.", new int[] {
					RADIO_PROFILE_CHANNEL_WIDTH_20,
					RADIO_PROFILE_CHANNEL_WIDTH_40A,
					RADIO_PROFILE_CHANNEL_WIDTH_40B });
	
	public static EnumItem[] RADIO_PROFILE_CHANNEL_WIDTH_11AC = MgrUtil.enumItems(
			"enum.radioProfileChannelWidth.", new int[] {
					RADIO_PROFILE_CHANNEL_WIDTH_20,
					RADIO_PROFILE_CHANNEL_WIDTH_40,
					RADIO_PROFILE_CHANNEL_WIDTH_80 });

	private short channelWidth = RADIO_PROFILE_CHANNEL_WIDTH_20;

	@Range(min = 40, max = 3500)
	private short beaconPeriod = 100;

	@Range(min = 1, max = 100)
	private short maxClients = 100;

	private boolean loadBalance;

	@Range(min = 1, max = 100)
	private short minCount = 10;

	@Range(min = 30, max = 100)
	private short threshold = 70;

	public static final short RADIO_ROAMING_THRESHOLD_VERYLOW = 1;

	public static final short RADIO_ROAMING_THRESHOLD_LOW = 2;

	public static final short RADIO_ROAMING_THRESHOLD_MEDIUM = 3;

	public static final short RADIO_ROAMING_THRESHOLD_HIGH = 4;

	public static final short RADIO_ROAMING_THRESHOLD_OFF = 5;

	public static EnumItem[] RADIO_ROAMING_THRESHOLD = MgrUtil
			.enumItems("enum.radioRoamingThreshold.", new int[] {
					RADIO_ROAMING_THRESHOLD_VERYLOW,
					RADIO_ROAMING_THRESHOLD_LOW,
					RADIO_ROAMING_THRESHOLD_MEDIUM,
					RADIO_ROAMING_THRESHOLD_HIGH, RADIO_ROAMING_THRESHOLD_OFF });

	private short roamingThreshold = RADIO_ROAMING_THRESHOLD_OFF;

	/*
	 * add antenna 20 type from 3.3r3
	 */
	public static final short RADIO_ANTENNA20_TYPE_I = 1;

	public static final short RADIO_ANTENNA20_TYPE_E = 2;

	public static EnumItem[] RADIO_ANTENNA20_TYPE = MgrUtil.enumItems(
			"enum.radio.profile.antenna20.type.", new int[] {
					RADIO_ANTENNA20_TYPE_I, RADIO_ANTENNA20_TYPE_E });

	private short antennaType20 = RADIO_ANTENNA20_TYPE_I;

	/*
	 * add antenna 28 type from 3.3r3
	 */
	public static final short RADIO_ANTENNA28_TYPE_A = 1;

	public static final short RADIO_ANTENNA28_TYPE_B = 2;

	public static final short RADIO_ANTENNA28_TYPE_D = 3;

	private short antennaType28 = RADIO_ANTENNA28_TYPE_D;

	@Range(min = 300, max = 10000)
	private int radioRange = 300;

	private boolean enableDfs;

	private boolean enableRadarDetect;
	/*
	 * add fixed antenna for 3.2r1 end
	 */

	private boolean backhaulFailover = true;

	@Range(min = 1, max = 5)
	private short triggerTime = 2;

	@Range(min = 1, max = 300)
	private short holdTime = 30;

	private boolean backgroundScan = true;

	private boolean trafficVoice;

	private boolean clientConnect = true;

	// add from 3.4r3
	private boolean powerSave;

	@Range(min = 1, max = 1440)
	private int interval = 10;

	private boolean enableChannel;

	@Range(min = 0, max = 23)
	private short fromHour = 0;

	@Range(min = 0, max = 59)
	private short fromMinute = 0;

	@Range(min = 0, max = 23)
	private short toHour = 0;

	@Range(min = 0, max = 59)
	private short toMinute = 0;

	@Range(min = 0, max = 100)
	private short channelClient = 0;

	// change from true to false in 3.5r1 fcs
	private boolean useDefaultChannelModel;

	public static final short RADIO_PROFILE_CHANNEL_REGION_US = 1;

	public static final short RADIO_PROFILE_CHANNEL_REGION_EUR = 2;

	public static EnumItem[] RADIO_PROFILE_CHANNEL_REGION = MgrUtil.enumItems(
			"enum.radioProfile.channel.region.", new int[] {
					RADIO_PROFILE_CHANNEL_REGION_US,
					RADIO_PROFILE_CHANNEL_REGION_EUR });

	private short channelRegion = RADIO_PROFILE_CHANNEL_REGION_US;

	public static final short RADIO_PROFILE_CHANNEL_MODEL_3 = 1;

	public static final short RADIO_PROFILE_CHANNEL_MODEL_4 = 2;

	public static EnumItem[] RADIO_PROFILE_CHANNEL_MODEL = MgrUtil.enumItems(
			"enum.radioProfile.channel.model.", new int[] {
					RADIO_PROFILE_CHANNEL_MODEL_3,
					RADIO_PROFILE_CHANNEL_MODEL_4 });

	private short channelModel = RADIO_PROFILE_CHANNEL_MODEL_3;

	public static final String DEFAULT_CHANNEL_VALUE = "01-06-11";

	@Column(length = 11)
	private String channelValue = DEFAULT_CHANNEL_VALUE;

	private boolean enablePower = true;

	@Range(min = 10, max = 20)
	private short transmitPower = 20;

	private boolean deny11b;

	private boolean deny11abg;

	private boolean guardInterval;

	private boolean aggregateMPDU = true;

	public static final short RADIO_PROFILE_CHAIN_1 = 1;

	public static final short RADIO_PROFILE_CHAIN_2 = 2;

	public static final short RADIO_PROFILE_CHAIN_3 = 3;

	// the new CLI remove auto from the select items
	// add 1 for HiveAP 320 and 340 from 3.4r1
	private boolean useDefaultChain = true;

	public static EnumItem[] RADIO_PROFILE_CHAIN = MgrUtil.enumItems(
			"enum.radioProfileChain.", new int[] { RADIO_PROFILE_CHAIN_1, RADIO_PROFILE_CHAIN_2,
					RADIO_PROFILE_CHAIN_3 });

	private short transmitChain = RADIO_PROFILE_CHAIN_2;

	private short receiveChain = RADIO_PROFILE_CHAIN_2;

	// add CCA from 3.1r7 begin
	private boolean enableCca = true;

	@Range(min = 15, max = 65)
	private short defaultCcaValue = 33;

	@Range(min = 15, max = 65)
	private short maxCcaValue = 55;
	// add CCA from 3.1r7 end

	// add interference params from 3.4r1
	private boolean enableInterfernce;

	@Range(min = 15, max = 60)
	private short crcThreshold = 20;

	@Range(min = 15, max = 60)
	private short channelThreshold = 20;

	@Range(min = 5, max = 30)
	private short averageInterval = 5;
	// end

	// add SLA params from 3.4r1
	private short slaThoughput = SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM;
	// end

	// add channel switch params from 3.5r1 the default is no-station-enable
	private boolean channelSwitch = true;

	private boolean stationConnect;

	@Range(min = 10, max = 80)
	private short iuThreshold = 25;

	@Range(min = 10, max = 80)
	private short crcChannelThr = 25;
	// end

	// add high density from 3.5r1
	private boolean enableHighDensity;

	public static final short HIGH_DENSITY_TRANSMIT_RATE_HIGH = 1;

	public static final short HIGH_DENSITY_TRANSMIT_RATE_LOW = 2;

	public static EnumItem[] HIGH_DENSITY_TRANSMIT_RATE = MgrUtil.enumItems(
			"enum.radio.profile.highDensity.", new int[] {
					HIGH_DENSITY_TRANSMIT_RATE_HIGH,
					HIGH_DENSITY_TRANSMIT_RATE_LOW });

	private short highDensityTransmitRate = HIGH_DENSITY_TRANSMIT_RATE_LOW;

	private boolean enableBroadcastProbe;

	private boolean enableBandSteering;

	private boolean enableContinuousProbe;

	private boolean enableClientLoadBalance;

	public static final int DEFAULT_MAX_CRC_ERROR_LIMIT = 30;
	public static final int DEFAULT_SA_MINIMUM = 4;
	public static final int DEFAULT_MAX_INTERFERENCE = 40;
	public static final int DEFAULT_CLIENT_HOLD_TIME = 60;
	
	public static final int DEFAULT_PRESENCE_TIME=120;

	@Range(min = 1, max = 99)
	private int crcErrorLimit = DEFAULT_MAX_CRC_ERROR_LIMIT;

	// the field change to station airtime
	@Range(min = 1, max = 5)
	private int cuLimit = DEFAULT_SA_MINIMUM;

	@Range(min = 1, max = 99)
	private int maxInterference = DEFAULT_MAX_INTERFERENCE;

	@Range(min = 10, max = 600)
	private int clientHoldTime = DEFAULT_CLIENT_HOLD_TIME;

	private boolean enableSafetyNet = true;

	public static final int DEFAULT_SAFETY_NET_TIMEOUT = 15;

	@Range(min = 5, max = 300)
	private int safetyNetTimeout = DEFAULT_SAFETY_NET_TIMEOUT;

	private boolean enableSuppress;

	public static final int DEFAULT_SUPPRESS_THRESHOLD = 15;

	@Range(min = 1, max = 100)
	private int suppressThreshold = DEFAULT_SUPPRESS_THRESHOLD;
	// end

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name = "mapkey")
	@CollectionTable(name = "RADIO_PROFILE_WMM_INFO", joinColumns = @JoinColumn(name = "RADIO_PROFILE_ID"))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Map<String, RadioProfileWmmInfo> wmmItems = new HashMap<String, RadioProfileWmmInfo>();

	// Added for optimize management from Casablanca
	public static final short BAND_STEERING_MODE_PREFER5G = 1;

	public static final short BAND_STEERING_MODE_BALANCEBAND = 2;

	public static final short BAND_STEERING_MODE_FORCE5G = 3;

	public static EnumItem[] BAND_STEERING_MODE = MgrUtil
			.enumItems("enum.band.steering.mode.",
					new int[] { BAND_STEERING_MODE_PREFER5G,
							BAND_STEERING_MODE_BALANCEBAND,
							BAND_STEERING_MODE_FORCE5G });

	private short bandSteeringMode = BAND_STEERING_MODE_BALANCEBAND;

	public static final int DEFAULT_LIMIT_NUMBER = 5;

	@Range(min = 1, max = 100)
	private int limitNumber = DEFAULT_LIMIT_NUMBER;

	public static final int DEFAULT_MINIMUM_RATION = 80;

	@Range(min = 1, max = 100)
	private int minimumRatio = DEFAULT_MINIMUM_RATION;

	public static final short LOAD_BALANCE_MODE_AIRTIME_BASED = 1;

	public static final short LOAD_BALANCE_MODE_STATION_NUMBER = 2;

	public static EnumItem[] LOAD_BALANCING_MODE = MgrUtil.enumItems(
			"enum.load.balancing.mode.", new int[] {
					LOAD_BALANCE_MODE_AIRTIME_BASED,
					LOAD_BALANCE_MODE_STATION_NUMBER });

	private short loadBalancingMode = LOAD_BALANCE_MODE_AIRTIME_BASED;

	public static final int DEFAULT_QUERY_INTERVAL_TIME = 60;
	@Range(min = 1, max = 600)
	private int queryInterval = DEFAULT_QUERY_INTERVAL_TIME;

	private boolean enableWips;
	
	private boolean enabledPresence;

	@Range(min = 15, max = 600)
	private int trapInterval = DEFAULT_PRESENCE_TIME;

	@Range(min = 15, max = 600)
	private int agingTime = DEFAULT_PRESENCE_TIME;
	
	@Range(min = 15, max = 600)
	private int aggrInterval = DEFAULT_PRESENCE_TIME;
	
	private boolean enableSupressBPRByOUI = true;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "RADIO_PROFILE_SUPRESS_BPR_OUI", joinColumns = { @JoinColumn(name = "RADIO_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "OUI_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<MacOrOui> supressBprOUIs = new HashSet<MacOrOui>();

	public short getChannelWidth() {
		return channelWidth;
	}

	public void setChannelWidth(short channelWidth) {
		this.channelWidth = channelWidth;
	}

	public boolean isGuardInterval() {
		return guardInterval;
	}

	public void setGuardInterval(boolean guardInterval) {
		this.guardInterval = guardInterval;
	}

	public boolean isAggregateMPDU() {
		return aggregateMPDU;
	}

	public void setAggregateMPDU(boolean aggregateMPDU) {
		this.aggregateMPDU = aggregateMPDU;
	}

	public short getTransmitChain() {
		return transmitChain;
	}

	public void setTransmitChain(short transmitChain) {
		this.transmitChain = transmitChain;
	}

	public short getReceiveChain() {
		return receiveChain;
	}

	public void setReceiveChain(short receiveChain) {
		this.receiveChain = receiveChain;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public static int[] getIndexForRate(int arg_Index) {
		int[] bgIndex = new int[arg_Index];
		for (int i = 0; i < arg_Index; i++) {
			bgIndex[i] = i + 1;
		}
		return bgIndex;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getLabel() {
		return radioName;
	}

	@Transient
	public String getRadioModeString() {
		return MgrUtil.getEnumString("enum.radioProfileMode." + radioMode);
	}

	@Transient
	public String getPreambleString() {
		return MgrUtil.getEnumString("enum.radioProfilePreamble."
				+ shortPreamble);
	}

	@Transient
	public String getRoamingString() {
		return MgrUtil.getEnumString("enum.radioRoamingThreshold."
				+ roamingThreshold);
	}

	public String getRadioName() {
		return radioName;
	}

	public void setRadioName(String radioName) {
		this.radioName = radioName;
	}

	public short getRadioMode() {
		return radioMode;
	}

	public void setRadioMode(short radioMode) {
		this.radioMode = radioMode;
	}

	public short getShortPreamble() {
		return shortPreamble;
	}

	public void setShortPreamble(short shortPreamble) {
		this.shortPreamble = shortPreamble;
	}

	public short getBeaconPeriod() {
		return beaconPeriod;
	}

	public void setBeaconPeriod(short beaconPeriod) {
		this.beaconPeriod = beaconPeriod;
	}

	public short getMaxClients() {
		return maxClients;
	}

	public void setMaxClients(short maxClients) {
		this.maxClients = maxClients;
	}

	public boolean getLoadBalance() {
		return loadBalance;
	}

	public void setLoadBalance(boolean loadBalance) {
		this.loadBalance = loadBalance;
	}

	public short getMinCount() {
		return minCount;
	}

	public void setMinCount(short minCount) {
		this.minCount = minCount;
	}

	public short getThreshold() {
		return threshold;
	}

	public void setThreshold(short threshold) {
		this.threshold = threshold;
	}

	public boolean getBackhaulFailover() {
		return backhaulFailover;
	}

	public void setBackhaulFailover(boolean backhaulFailover) {
		this.backhaulFailover = backhaulFailover;
	}

	public short getTriggerTime() {
		return triggerTime;
	}

	public void setTriggerTime(short triggerTime) {
		this.triggerTime = triggerTime;
	}

	public short getHoldTime() {
		return holdTime;
	}

	public void setHoldTime(short holdTime) {
		this.holdTime = holdTime;
	}

	public boolean getBackgroundScan() {
		return backgroundScan;
	}

	public void setBackgroundScan(boolean backgroundScan) {
		this.backgroundScan = backgroundScan;
	}

	public boolean getTrafficVoice() {
		return trafficVoice;
	}

	public void setTrafficVoice(boolean trafficVoice) {
		this.trafficVoice = trafficVoice;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public boolean isScanAllChannel() {
		return scanAllChannel;
	}

	public void setScanAllChannel(boolean scanAllChannel) {
		this.scanAllChannel = scanAllChannel;
	}

	public String getScanChannels() {
		return scanChannels;
	}

	public void setScanChannels(String scanChannels) {
		this.scanChannels = scanChannels;
	}

	public int getDellTime() {
		return dellTime;
	}

	public void setDellTime(int dellTime) {
		this.dellTime = dellTime;
	}

	public boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public short getRoamingThreshold() {
		return roamingThreshold;
	}

	public void setRoamingThreshold(short roamingThreshold) {
		this.roamingThreshold = roamingThreshold;
	}

	public boolean isEnableChannel() {
		return enableChannel;
	}

	public void setEnableChannel(boolean enableChannel) {
		this.enableChannel = enableChannel;
	}

	public short getFromHour() {
		return fromHour;
	}

	public void setFromHour(short fromHour) {
		this.fromHour = fromHour;
	}

	public short getFromMinute() {
		return fromMinute;
	}

	public void setFromMinute(short fromMinute) {
		this.fromMinute = fromMinute;
	}

	public short getToHour() {
		return toHour;
	}

	public void setToHour(short toHour) {
		this.toHour = toHour;
	}

	public short getToMinute() {
		return toMinute;
	}

	public void setToMinute(short toMinute) {
		this.toMinute = toMinute;
	}

	public short getChannelClient() {
		return channelClient;
	}

	public void setChannelClient(short channelClient) {
		this.channelClient = channelClient;
	}

	public boolean isEnablePower() {
		return enablePower;
	}

	public void setEnablePower(boolean enablePower) {
		this.enablePower = enablePower;
	}

	public short getTransmitPower() {
		return transmitPower;
	}

	public void setTransmitPower(short transmitPower) {
		this.transmitPower = transmitPower;
	}

	public boolean isTurboMode() {
		return turboMode;
	}

	public void setTurboMode(boolean turboMode) {
		this.turboMode = turboMode;
	}

	public boolean isDeny11b() {
		return deny11b;
	}

	public void setDeny11b(boolean deny11b) {
		this.deny11b = deny11b;
	}

	public boolean isDeny11abg() {
		return deny11abg;
	}

	public void setDeny11abg(boolean deny11abg) {
		this.deny11abg = deny11abg;
	}

	public boolean isClientConnect() {
		return clientConnect;
	}

	public void setClientConnect(boolean clientConnect) {
		this.clientConnect = clientConnect;
	}

	public short getChannelRegion() {
		return channelRegion;
	}

	public void setChannelRegion(short channelRegion) {
		this.channelRegion = channelRegion;
	}

	public short getChannelModel() {
		return channelModel;
	}

	public void setChannelModel(short channelModel) {
		this.channelModel = channelModel;
	}

	public String getChannelValue() {
		return channelValue;
	}

	public void setChannelValue(String channelValue) {
		this.channelValue = channelValue;
	}

	public boolean isUseDefaultChannelModel() {
		return useDefaultChannelModel;
	}

	public void setUseDefaultChannelModel(boolean useDefaultChannelModel) {
		this.useDefaultChannelModel = useDefaultChannelModel;
	}

	public boolean isCliDefaultFlag() {
		return cliDefaultFlag;
	}

	public void setCliDefaultFlag(boolean cliDefaultFlag) {
		this.cliDefaultFlag = cliDefaultFlag;
	}

	public int getRadioRange() {
		return radioRange;
	}

	public void setRadioRange(int radioRange) {
		this.radioRange = radioRange;
	}

	public boolean isEnableDfs() {
		return enableDfs;
	}

	public void setEnableDfs(boolean enableDfs) {
		this.enableDfs = enableDfs;
	}

	public boolean isEnableCca() {
		return enableCca;
	}

	public void setEnableCca(boolean enableCca) {
		this.enableCca = enableCca;
	}

	public short getDefaultCcaValue() {
		return defaultCcaValue;
	}

	public void setDefaultCcaValue(short defaultCcaValue) {
		this.defaultCcaValue = defaultCcaValue;
	}

	public short getMaxCcaValue() {
		return maxCcaValue;
	}

	public void setMaxCcaValue(short maxCcaValue) {
		this.maxCcaValue = maxCcaValue;
	}

	public RadioProfileWmmInfo getWmmInfo(AccessCategory type) {
		return wmmItems.get(type.name());
	}

	public Map<String, RadioProfileWmmInfo> getWmmItems() {
		return wmmItems;
	}

	public void setWmmItems(Map<String, RadioProfileWmmInfo> wmmItems) {
		this.wmmItems = wmmItems;
	}

	public boolean isEnableInterfernce() {
		return enableInterfernce;
	}

	public void setEnableInterfernce(boolean enableInterfernce) {
		this.enableInterfernce = enableInterfernce;
	}

	public short getCrcThreshold() {
		return crcThreshold;
	}

	public void setCrcThreshold(short crcThreshold) {
		this.crcThreshold = crcThreshold;
	}

	public short getChannelThreshold() {
		return channelThreshold;
	}

	public void setChannelThreshold(short channelThreshold) {
		this.channelThreshold = channelThreshold;
	}

	public short getAverageInterval() {
		return averageInterval;
	}

	public void setAverageInterval(short averageInterval) {
		this.averageInterval = averageInterval;
	}

	public short getSlaThoughput() {
		return slaThoughput;
	}

	public void setSlaThoughput(short slaThoughput) {
		this.slaThoughput = slaThoughput;
	}

	public boolean isEnableHighDensity() {
		return enableHighDensity;
	}

	public void setEnableHighDensity(boolean enableHighDensity) {
		this.enableHighDensity = enableHighDensity;
	}

	public short getHighDensityTransmitRate() {
		return highDensityTransmitRate;
	}

	public void setHighDensityTransmitRate(short highDensityTransmitRate) {
		this.highDensityTransmitRate = highDensityTransmitRate;
	}

	public boolean isEnableBroadcastProbe() {
		return enableBroadcastProbe;
	}

	public void setEnableBroadcastProbe(boolean enableBroadcastProbe) {
		this.enableBroadcastProbe = enableBroadcastProbe;
	}

	public boolean isEnableBandSteering() {
		return enableBandSteering;
	}

	public void setEnableBandSteering(boolean enableBandSteering) {
		this.enableBandSteering = enableBandSteering;
	}

	public boolean isEnableContinuousProbe() {
		return enableContinuousProbe;
	}

	public void setEnableContinuousProbe(boolean enableContinuousProbe) {
		this.enableContinuousProbe = enableContinuousProbe;
	}

	public boolean isEnableClientLoadBalance() {
		return enableClientLoadBalance;
	}

	public void setEnableClientLoadBalance(boolean enableClientLoadBalance) {
		this.enableClientLoadBalance = enableClientLoadBalance;
	}

	public int getCrcErrorLimit() {
		return crcErrorLimit;
	}

	public void setCrcErrorLimit(int crcErrorLimit) {
		this.crcErrorLimit = crcErrorLimit;
	}

	public int getCuLimit() {
		return cuLimit;
	}

	public void setCuLimit(int cuLimit) {
		this.cuLimit = cuLimit;
	}

	public int getMaxInterference() {
		return maxInterference;
	}

	public void setMaxInterference(int maxInterference) {
		this.maxInterference = maxInterference;
	}

	public int getClientHoldTime() {
		return clientHoldTime;
	}

	public void setClientHoldTime(int clientHoldTime) {
		this.clientHoldTime = clientHoldTime;
	}

	public boolean isEnableSafetyNet() {
		return enableSafetyNet;
	}

	public void setEnableSafetyNet(boolean enableSafetyNet) {
		this.enableSafetyNet = enableSafetyNet;
	}

	public int getSafetyNetTimeout() {
		return safetyNetTimeout;
	}

	public void setSafetyNetTimeout(int safetyNetTimeout) {
		this.safetyNetTimeout = safetyNetTimeout;
	}

	public boolean isEnableSuppress() {
		return enableSuppress;
	}

	public void setEnableSuppress(boolean enableSuppress) {
		this.enableSuppress = enableSuppress;
	}

	public int getSuppressThreshold() {
		return suppressThreshold;
	}

	public void setSuppressThreshold(int suppressThreshold) {
		this.suppressThreshold = suppressThreshold;
	}

	@Transient
	private List<SlaMappingCustomize> mappings;

	private void initSlaMappingCustomize() {
		mappings = QueryUtil.executeQuery(SlaMappingCustomize.class, null,
				null, owner.getId());
	}

	@Transient
	public String getSLATopRate(ClientPhyMode mode) {
		if (null == mappings) {
			initSlaMappingCustomize();
		}
		String s = SlaMappingCustomizeAction.getRate(mappings, mode,
				slaThoughput, SlaMappingCustomize.ITEM_ORDER_TOP);
		return null == s ? SlaMappingCustomize.getDefaultSLATopRate(mode,
				slaThoughput) : s;
	}

	@Transient
	public String getSLABottomRate(ClientPhyMode mode) {
		if (null == mappings) {
			initSlaMappingCustomize();
		}
		String s = SlaMappingCustomizeAction.getRate(mappings, mode,
				slaThoughput, SlaMappingCustomize.ITEM_ORDER_BOTTOM);
		return null == s ? SlaMappingCustomize.getDefaultSLABottomRate(mode,
				slaThoughput) : s;
	}

	@Transient
	public int getSLATopSuccessPercent(ClientPhyMode mode) {
		if (null == mappings) {
			initSlaMappingCustomize();
		}
		int s = SlaMappingCustomizeAction.getSuccess(mappings, mode,
				slaThoughput, SlaMappingCustomize.ITEM_ORDER_TOP);
		return 0 == s ? SlaMappingCustomize.getDefaultSLATopSuccessPercent(
				mode, slaThoughput) : s;
	}

	@Transient
	public int getSLABottomSuccessPercent(ClientPhyMode mode) {
		if (null == mappings) {
			initSlaMappingCustomize();
		}
		int s = SlaMappingCustomizeAction.getSuccess(mappings, mode,
				slaThoughput, SlaMappingCustomize.ITEM_ORDER_BOTTOM);
		return 0 == s ? SlaMappingCustomize.getDefaultSLABottomSuccessPercent(
				mode, slaThoughput) : s;
	}

	@Transient
	public int getSLATopUsagePercent(ClientPhyMode mode) {
		if (null == mappings) {
			initSlaMappingCustomize();
		}
		int s = SlaMappingCustomizeAction.getUsage(mappings, mode,
				slaThoughput, SlaMappingCustomize.ITEM_ORDER_TOP);
		return 0 == s ? SlaMappingCustomize.getDefaultSLATopUsagePercent(mode,
				slaThoughput) : s;
	}

	@Transient
	public int getSLABottomUsagePercent(ClientPhyMode mode) {
		if (null == mappings) {
			initSlaMappingCustomize();
		}
		int s = SlaMappingCustomizeAction.getUsage(mappings, mode,
				slaThoughput, SlaMappingCustomize.ITEM_ORDER_BOTTOM);
		return 0 == s ? SlaMappingCustomize.getDefaultSLABottomUsagePercent(
				mode, slaThoughput) : s;
	}

	@Transient
	private String channelPowerStyle = "none"; // default
	@Transient
	private String radioSettingStyle = "none"; // default
	@Transient
	private String wmmQosStyle = "none"; // default
	@Transient
	private String chainStyle = "none"; // default
	@Transient
	private String interferenceStyle = "none"; // default
	@Transient
	private String loadBalanceStyle = "none"; // default
	@Transient
	private String backhaulStyle = "none"; // default
	@Transient
	private String clientStyle = "none"; // default
	@Transient
	private String slaStyle = "none"; // default
	@Transient
	private String highDensityStyle = "none"; // default
	@Transient
	private String wipsServerStyle = "none"; // default
	@Transient
	private String presenceServerStyle = "none"; // default
	@Transient
	private String sensorScanStyle = "none"; // default

	// Added from Casablanca
	@Transient
	private String optimizManagementStyle = "none"; // default

	public String getChannelPowerStyle() {
		return channelPowerStyle;
	}

	public void setChannelPowerStyle(String channelPowerStyle) {
		this.channelPowerStyle = channelPowerStyle;
	}

	public String getRadioSettingStyle() {
		return radioSettingStyle;
	}

	public void setRadioSettingStyle(String radioSettingStyle) {
		this.radioSettingStyle = radioSettingStyle;
	}

	public String getWmmQosStyle() {
		return wmmQosStyle;
	}

	public void setWmmQosStyle(String wmmQosStyle) {
		this.wmmQosStyle = wmmQosStyle;
	}

	public String getChainStyle() {
		return chainStyle;
	}

	public void setChainStyle(String chainStyle) {
		this.chainStyle = chainStyle;
	}

	public String getInterferenceStyle() {
		return interferenceStyle;
	}

	public void setInterferenceStyle(String interferenceStyle) {
		this.interferenceStyle = interferenceStyle;
	}

	public String getLoadBalanceStyle() {
		return loadBalanceStyle;
	}

	public void setLoadBalanceStyle(String loadBalanceStyle) {
		this.loadBalanceStyle = loadBalanceStyle;
	}

	public String getBackhaulStyle() {
		return backhaulStyle;
	}

	public void setBackhaulStyle(String backhaulStyle) {
		this.backhaulStyle = backhaulStyle;
	}

	public String getClientStyle() {
		return clientStyle;
	}

	public void setClientStyle(String clientStyle) {
		this.clientStyle = clientStyle;
	}

	public String getSlaStyle() {
		return slaStyle;
	}

	public void setSlaStyle(String slaStyle) {
		this.slaStyle = slaStyle;
	}

	public String getHighDensityStyle() {
		return highDensityStyle;
	}

	public void setHighDensityStyle(String highDensityStyle) {
		this.highDensityStyle = highDensityStyle;
	}

	public boolean isUseDefaultChain() {
		return useDefaultChain;
	}

	public void setUseDefaultChain(boolean useDefaultChain) {
		this.useDefaultChain = useDefaultChain;
	}

	@Override
	public RadioProfile clone() {
		try {
			return (RadioProfile) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public short getAntennaType20() {
		return antennaType20;
	}

	public void setAntennaType20(short antennaType20) {
		this.antennaType20 = antennaType20;
	}

	public short getAntennaType28() {
		return antennaType28;
	}

	public void setAntennaType28(short antennaType28) {
		this.antennaType28 = antennaType28;
	}

	public boolean isPowerSave() {
		return powerSave;
	}

	public void setPowerSave(boolean powerSave) {
		this.powerSave = powerSave;
	}

	public short getIuThreshold() {
		return iuThreshold;
	}

	public void setIuThreshold(short iuThreshold) {
		this.iuThreshold = iuThreshold;
	}

	public short getCrcChannelThr() {
		return crcChannelThr;
	}

	public void setCrcChannelThr(short crcChannelThr) {
		this.crcChannelThr = crcChannelThr;
	}

	public boolean isChannelSwitch() {
		return channelSwitch;
	}

	public void setChannelSwitch(boolean channelSwitch) {
		this.channelSwitch = channelSwitch;
	}

	public boolean isStationConnect() {
		return stationConnect;
	}

	public void setStationConnect(boolean stationConnect) {
		this.stationConnect = stationConnect;
	}

	/**
	 * @return the enabledBssidSpoof
	 */
	public boolean getEnabledBssidSpoof() {
		return enabledBssidSpoof;
	}

	/**
	 * @param enabledBssidSpoof
	 *            the enabledBssidSpoof to set
	 */
	public void setEnabledBssidSpoof(boolean enabledBssidSpoof) {
		this.enabledBssidSpoof = enabledBssidSpoof;
	}

	/**
	 * @return the enableRadarDetect
	 */
	public boolean isEnableRadarDetect() {
		return enableRadarDetect;
	}

	/**
	 * @param enableRadarDetect
	 *            the enableRadarDetect to set
	 */
	public void setEnableRadarDetect(boolean enableRadarDetect) {
		this.enableRadarDetect = enableRadarDetect;
	}

	public String getOptimizManagementStyle() {
		return optimizManagementStyle;
	}

	public void setOptimizManagementStyle(String optimizManagementStyle) {
		this.optimizManagementStyle = optimizManagementStyle;
	}

	public short getBandSteeringMode() {
		return bandSteeringMode;
	}

	public void setBandSteeringMode(short bandSteeringMode) {
		this.bandSteeringMode = bandSteeringMode;
	}

	public short getLoadBalancingMode() {
		return loadBalancingMode;
	}

	public void setLoadBalancingMode(short loadBalancingMode) {
		this.loadBalancingMode = loadBalancingMode;
	}

	public int getLimitNumber() {
		return limitNumber;
	}

	public void setLimitNumber(int limitNumber) {
		this.limitNumber = limitNumber;
	}

	public int getMinimumRatio() {
		return minimumRatio;
	}

	public void setMinimumRatio(int minimumRatio) {
		this.minimumRatio = minimumRatio;
	}

	public int getQueryInterval() {
		return queryInterval;
	}

	public void setQueryInterval(int queryInterval) {
		this.queryInterval = queryInterval;
	}

	public boolean isEnableWips() {
		return enableWips;
	}

	public void setEnableWips(boolean enableWips) {
		this.enableWips = enableWips;
	}

	public boolean isEnabledPresence() {
		return enabledPresence;
	}

	public void setEnabledPresence(boolean enabledPresence) {
		this.enabledPresence = enabledPresence;
	}

	public int getTrapInterval() {
		return trapInterval;
	}

	public void setTrapInterval(int trapInterval) {
		this.trapInterval = trapInterval;
	}

	public int getAgingTime() {
		return agingTime;
	}

	public void setAgingTime(int agingTime) {
		this.agingTime = agingTime;
	}

	public int getAggrInterval() {
		return aggrInterval;
	}

	public void setAggrInterval(int aggrInterval) {
		this.aggrInterval = aggrInterval;
	}

	public String getWipsServerStyle() {
		return wipsServerStyle;
	}

	public void setWipsServerStyle(String wipsServerStyle) {
		this.wipsServerStyle = wipsServerStyle;
	}

	public String getPresenceServerStyle() {
		return presenceServerStyle;
	}

	public void setPresenceServerStyle(String presenceServerStyle) {
		this.presenceServerStyle = presenceServerStyle;
	}

	public String getSensorScanStyle() {
		return sensorScanStyle;
	}

	public void setSensorScanStyle(String sensorScanStyle) {
		this.sensorScanStyle = sensorScanStyle;
	}

	public short getTxBeamformingMode() {
		return txBeamformingMode;
	}

	public void setTxBeamformingMode(short txBeamformingMode) {
		this.txBeamformingMode = txBeamformingMode;
	}

	public boolean isEnabledTxbeamforming() {
		return enabledTxbeamforming;
	}

	public void setEnabledTxbeamforming(boolean enabledTxbeamforming) {
		this.enabledTxbeamforming = enabledTxbeamforming;
	}
	
	public boolean isEnableVHT() {
		return enableVHT;
	}

	public void setEnableVHT(boolean enableVHT) {
		this.enableVHT = enableVHT;
	}

	public boolean isEnableFrameburst() {
		return enableFrameburst;
	}

	public void setEnableFrameburst(boolean enableFrameburst) {
		this.enableFrameburst = enableFrameburst;
	}

		
	public boolean isEnableSupressBPRByOUI() {
		return enableSupressBPRByOUI;
	}

	public void setEnableSupressBPRByOUI(boolean enableSupressBPRByOUI) {
		this.enableSupressBPRByOUI = enableSupressBPRByOUI;
	}

	public Set<MacOrOui> getSupressBprOUIs() {
		return supressBprOUIs;
	}

	public void setSupressBprOUIs(Set<MacOrOui> supressBprOUIs) {
		this.supressBprOUIs = supressBprOUIs;
	}

}