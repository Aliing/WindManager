/**
 *@filename		RadioProfileAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-29 PM 05:44:33
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.constraints.Range;
import org.json.JSONObject;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.PresenceUtil;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.RadioProfileWmmInfo;
import com.ah.bo.wlan.RadioProfileWmmInfo.AccessCategory;
import com.ah.bo.wlan.SlaMappingCustomize;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.EnumItem;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class RadioProfileAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	
	private static final Tracer log = new Tracer(IdsPolicyAction.class
			.getSimpleName());

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.radio"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new RadioProfile());
				prepareRadioType();
				Map<String, Object> para = new HashMap<String, Object>();
				for (String[] macInfo : BeParaModule.DEFAULT_MAC_OUIS) {
					if(macInfo[0].startsWith("Samsung")){
						para.put("macOrOuiName", macInfo[0]);
						getDataSource().getSupressBprOUIs().add(HmBeParaUtil.getDefaultProfile(MacOrOui.class, para));
					}
				}
				prepareAvailableSupressBprOuis();
				getDataSource().setWmmItems(getDefaultWmmInfo(getDataSource()));
				return isJsonMode() ? "newRadio" : INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("radioName", getDataSource().getRadioName())) {
					setSelectedMacOrOuis();
					prepareAvailableSupressBprOuis();
					return isJsonMode() ? "newRadio" : INPUT;
				}
				setRadioValue();
				setSelectedMacOrOuis();
				if ("create".equals(operation) && !isJsonMode()) {
					return createBo();
				} else {
					id = createBo(dataSource);
					if (isJsonMode()) {
						prepareAvailableSupressBprOuis();
						return "newRadio";
					} else {
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				if (dataSource == null) {
					return prepareBoList();
				} else {
					prepareAvailableSupressBprOuis();
					getDataSource().setWmmItems(getDefaultWmmInfo(getDataSource()));
					addLstTitle(getText("config.title.radio.edit") + " '"
							+ getChangedName() + "'");
					return isJsonMode() ? "newRadio" : INPUT;
				}
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource == null || dataSource.getId() == null || !dataSource.getId().equals(id)) {
					throw new HmException(
							"Update object failed, session must have been shared by another browser window.",
							HmMessageCodes.STALE_SESSION_OBJECT,
							new String[] { "Update" });
				}
				
				if (dataSource != null) {
					setRadioValue();
					setSelectedMacOrOuis();
				}
				if ("update".equals(operation) && !isJsonMode()) {
					return updateBo();
				} else {
					/*id = dataSource.getId();*/
					updateBo(dataSource);
					if (isJsonMode()) {
						prepareAvailableSupressBprOuis();
						return "newRadio";
					} else {
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				RadioProfile profile = (RadioProfile) findBoById(boClass,
						cloneId, this);
				profile.setId(null);
				profile.setRadioName("");
				profile.setDefaultFlag(false);
				profile.setCliDefaultFlag(false);
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				prepareAvailableSupressBprOuis();
				getDataSource().setWmmItems(getDefaultWmmInfo(getDataSource()));
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("changeRate".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("id", "transmitRate");
				List<String> values = new ArrayList<String>();
				for (EnumItem item : getTransmitRateFrom(mode)) {
					values.add(item.getValue());
				}
				jsonObject.put("v", values);
				return "json";
			}else if("newMacOrOui".equals(operation) || "editMacOrOui".equals(operation)){
				setSelectedMacOrOuis();
				clearErrorsAndMessages();
				addLstForward("radioProfile");
				addLstTabId(tabId);
				return operation;
			}else if("continue".equals(operation)) {
				if (null == dataSource) {
					return prepareBoList();
				} 
				setId(dataSource.getId());
				prepareAvailableSupressBprOuis();
				if (getUpdateContext()) {
					removeLstTitle();
					MgrUtil
							.setSessionAttribute("CURRENT_TABID",
									getTabId());
					removeLstForward();
					setUpdateContext(false);
				}
				
				return isJsonMode() ? "newRadio" : INPUT;
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			if(isJsonMode()){
				addActionError(MgrUtil.getUserMessage(e));
				generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
						+ " " + MgrUtil.getUserMessage(e));
				return "newRadio";
			}
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_RADIO_PROFILE);
		setDataSource(RadioProfile.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_RADIO;
		
		// avoid cancel back error when open two edit view 
		if(L2_FEATURE_RADIO_PROFILE.equals(request.getParameter("operation"))
		        || "create".equals(request.getParameter("operation")) 
		        || ("create" + getLstForward()).equals(request.getParameter("operation"))
		        || "update".equals(request.getParameter("operation")) 
		        || ("update"+ getLstForward()).equals(request.getParameter("operation"))) {
		    if(null == getDataSource()) {
		        setSessionDataSource(new RadioProfile());
		    }
		}
	}

	public void setRadioValue() {
		if (!getDataSource().getLoadBalance()) {
			getDataSource().setMinCount((short) 10);
			getDataSource().setThreshold((short) 70);
			getDataSource().setRoamingThreshold(RadioProfile.RADIO_ROAMING_THRESHOLD_OFF);
		}
		if (getDisableMode()) {
			try {
				RadioProfile usedRadio = QueryUtil.findBoById(RadioProfile.class, getDataSource().getId());
				getDataSource().setBackhaulFailover(usedRadio.getBackhaulFailover());
				if (getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A) {
					getDataSource().setTurboMode(usedRadio.isTurboMode());
				}
				if ((getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA || 
						getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
						|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC)
					&& usedRadio.isEnableDfs()) {
					getDataSource().setEnableDfs(true);
				}
			} catch (Exception e) {
			}
		} else {
			if (!getDataSource().getBackhaulFailover()) {
				getDataSource().setTriggerTime((short) 2);
				getDataSource().setHoldTime((short) 30);
			}
		}
		if (!getDataSource().getBackgroundScan()) {
			getDataSource().setInterval(10);
			getDataSource().setTrafficVoice(false);
			getDataSource().setClientConnect(true);
			getDataSource().setPowerSave(false);
		}
		if (!getDataSource().isEnableChannel()) {
			getDataSource().setFromHour((short) 0);
			getDataSource().setFromMinute((short) 0);
			getDataSource().setToHour((short) 0);
			getDataSource().setToMinute((short) 0);
			getDataSource().setChannelClient((short) 0);
		}
		if (!getDataSource().isEnablePower()) {
			getDataSource().setTransmitPower((short)20);
		}
		if (getDataSource().getRadioMode() != RadioProfile.RADIO_PROFILE_MODE_NA && 
				getDataSource().getRadioMode() != RadioProfile.RADIO_PROFILE_MODE_A &&
				getDataSource().getRadioMode() != RadioProfile.RADIO_PROFILE_MODE_AC) {
			getDataSource().setEnableDfs(false);
		}
		if (getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
			|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG) {
			getDataSource().setChannelWidth(RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20);
			getDataSource().setDeny11b(false);
			getDataSource().setDeny11abg(false);
			getDataSource().setGuardInterval(false);
			getDataSource().setAggregateMPDU(true);
			// add the field of use default chain from 3.4r1
			getDataSource().setUseDefaultChain(true);
			getDataSource().setTransmitChain(RadioProfile.RADIO_PROFILE_CHAIN_2);
			getDataSource().setReceiveChain(RadioProfile.RADIO_PROFILE_CHAIN_2);
			getDataSource().setEnableCca(true);
			getDataSource().setDefaultCcaValue((short)33);
			getDataSource().setMaxCcaValue((short)55);
		} else {
			// add the fields from 3.3r3
			getDataSource().setAntennaType20(RadioProfile.RADIO_ANTENNA20_TYPE_I);
			getDataSource().setAntennaType28(RadioProfile.RADIO_ANTENNA28_TYPE_D);
			
			// add the field of use default chain from 3.4r1
			if (getDataSource().isUseDefaultChain()) {
				getDataSource().setTransmitChain(RadioProfile.RADIO_PROFILE_CHAIN_2);
				getDataSource().setReceiveChain(RadioProfile.RADIO_PROFILE_CHAIN_2);
			}
			if (!getDataSource().isEnableCca()) {
				getDataSource().setDefaultCcaValue((short)33);
				getDataSource().setMaxCcaValue((short)55);
			}
		}
		if (getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
			|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA ||
			getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC) {
			getDataSource().setShortPreamble(RadioProfile.RADIO_PROFILE_PREAMBLE_SHORT);
		    getDataSource().setUseDefaultChannelModel(false);
			getDataSource().setChannelRegion(RadioProfile.RADIO_PROFILE_CHANNEL_REGION_US);
			getDataSource().setChannelModel(RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_3);
			getDataSource().setChannelValue(RadioProfile.DEFAULT_CHANNEL_VALUE);
		} else {
			if (getDataSource().isUseDefaultChannelModel()) {
				getDataSource().setChannelRegion(RadioProfile.RADIO_PROFILE_CHANNEL_REGION_US);
				getDataSource().setChannelModel(RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_3);
				getDataSource().setChannelValue(RadioProfile.DEFAULT_CHANNEL_VALUE);
			}
		}
		if (!getDataSource().isChannelSwitch()) {
			// for channel switch
			getDataSource().setStationConnect(false);
			getDataSource().setIuThreshold((short)25);
			getDataSource().setCrcChannelThr((short)25);
		}
		updateWmmInfo();
		
		if(getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A){
			radioType = "a";
		}else if(getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG){
			radioType = "bg";
		}else if(getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA){
			radioType = "na";
		}else if(getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG){
			radioType = "ng";
		}else if(getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC){
			radioType = "ac";
		}
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		RadioProfile source = QueryUtil.findBoById(RadioProfile.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<RadioProfile> list = QueryUtil.executeQuery(RadioProfile.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (RadioProfile profile : list) {
			if (profile.getDefaultFlag()) {
				continue;
			}
			
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			RadioProfile up = source.clone();
			if (null == up) {
				continue;
			}
			
			// category must be same
			if (profile.getRadioMode() != source.getRadioMode()) {
				addActionError(MgrUtil
					.getUserMessage("error.use.paintbrush.objectIsDifferentType", "radio mode"));
				return null;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setRadioName(profile.getRadioName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			up.setWmmItems(getDefaultWmmInfo(source));
			setCloneValues(source,up);
			hmBos.add(up);
		}
		return hmBos;
	}
	
	private void setCloneValues(RadioProfile source, RadioProfile dest){
		Set<MacOrOui> cloned_macOui = new HashSet<MacOrOui>();
		cloned_macOui.addAll(source.getSupressBprOUIs());
		dest.setSupressBprOUIs(cloned_macOui);
	}

	public void prepareRadioType() {
		if (getDataSource() == null)
			return;
		
		// prepare radio type
//		if (getRadioType().equals("bg"))
//			getDataSource().setRadioMode(RadioProfile.RADIO_PROFILE_MODE_BG);
//		else if(getRadioType().equals("a"))
//			getDataSource().setRadioMode(RadioProfile.RADIO_PROFILE_MODE_A);
//		else if (getRadioType().equals("ng"))
//			getDataSource().setRadioMode(RadioProfile.RADIO_PROFILE_MODE_NG);
//		else if (getRadioType().equals("na"))
//			getDataSource().setRadioMode(RadioProfile.RADIO_PROFILE_MODE_NA);
//		else if (getRadioType().equals("ac"))
//			getDataSource().setRadioMode(RadioProfile.RADIO_PROFILE_MODE_AC);
		
		if(!"".equals(getRadioProfileName())){
			getDataSource().setRadioName(radioProfileName);
		}
		if(null != getRadioProfileMode()){
			getDataSource().setRadioMode(radioProfileMode);
		}else{
			if (getRadioType().equals("bg"))
				getDataSource().setRadioMode(RadioProfile.RADIO_PROFILE_MODE_BG);
			else if(getRadioType().equals("a"))
				getDataSource().setRadioMode(RadioProfile.RADIO_PROFILE_MODE_A);
			else if (getRadioType().equals("ng"))
				getDataSource().setRadioMode(RadioProfile.RADIO_PROFILE_MODE_NG);
			else if (getRadioType().equals("na"))
				getDataSource().setRadioMode(RadioProfile.RADIO_PROFILE_MODE_NA);
			else if (getRadioType().equals("ac"))
				getDataSource().setRadioMode(RadioProfile.RADIO_PROFILE_MODE_AC);
		}
		if(null != getRadioProfileChannelWidth()){
			getDataSource().setChannelWidth(radioProfileChannelWidth);
		}
		
	}
	
	public static Map<String, RadioProfileWmmInfo> getDefaultWmmInfo(RadioProfile arg_Radio) {
		if (null == arg_Radio) {
			return null;
		}
		Map<String, RadioProfileWmmInfo> wmmItems = new LinkedHashMap<String, RadioProfileWmmInfo>();
		for (AccessCategory acType : RadioProfileWmmInfo.AccessCategory
				.values()) {
			RadioProfileWmmInfo oneItem = arg_Radio.getWmmInfo(
					acType);
			if (oneItem == null) {
				oneItem = new RadioProfileWmmInfo();
				oneItem.setNoAck(false);
				if(AccessCategory.BG.equals(acType)) {
					oneItem.setMinimum((short)4);
					oneItem.setMaximum((short)10);
					oneItem.setAifs((short)7);
					oneItem.setTxoplimit(0);	
				}
				if(AccessCategory.BE.equals(acType)) {
					oneItem.setMinimum((short)4);
					oneItem.setMaximum((short)6);
					oneItem.setAifs((short)3);
					oneItem.setTxoplimit(0);
				}
				if(AccessCategory.VI.equals(acType)) {
					oneItem.setMinimum((short)3);
					oneItem.setMaximum((short)4);
					oneItem.setAifs((short)1);
					oneItem.setTxoplimit(3008);
				}
				if(AccessCategory.VO.equals(acType)) {
					oneItem.setMinimum((short)2);
					oneItem.setMaximum((short)3);
					oneItem.setAifs((short)1);
					oneItem.setTxoplimit(1504);
				}
			}
			oneItem.setAcType(acType);
			wmmItems.put(oneItem.getkey(), oneItem);
		}
		return wmmItems;
	}
	
	public static Set<MacOrOui> getDefaultSupressPBROuis(RadioProfile rp){
		if(null == rp){
			return null;
		}
		Set<MacOrOui> defaultSelectedMacOrOuis = new HashSet<MacOrOui>();
		
		if(rp.getSupressBprOUIs().isEmpty()){
			Map<String, Object> para = new HashMap<String, Object>();
			for (String[] macInfo : BeParaModule.DEFAULT_MAC_OUIS) {
				if(macInfo[0].startsWith("Samsung")){
					para.put("macOrOuiName", macInfo[0]);
					defaultSelectedMacOrOuis.add(HmBeParaUtil.getDefaultProfile(MacOrOui.class, para));
				}
			}
		}
		return defaultSelectedMacOrOuis;
	}
	
	protected void updateWmmInfo() {
		int i = 0;
		for (RadioProfileWmmInfo oneItem : getDataSource().getWmmItems().values()) {
			boolean enableRow = false;
			if (noAcks != null) {
				for (String noAck : noAcks) {
					if (!noAck.equals("false") && i == Integer.valueOf(noAck)) {
						enableRow = true;
						break;
					}
				}
			}
			oneItem.setMinimum(minimums[i]);
			oneItem.setMaximum(maximums[i]);
			oneItem.setAifs(aifses[i]);
			oneItem.setTxoplimit(txoplimits[i]);
			oneItem.setNoAck(enableRow);	
			i++;
		}
	}

	private String radioType = "bg";

	public String getRadioType() {
		return radioType;
	}

	public void setRadioType(String radioType) {
		this.radioType = radioType;
	}

	public RadioProfile getDataSource() {
		return (RadioProfile) dataSource;
	}

	public int getNameLength() {
		return getAttributeLength("radioName");
	}

	public int getCommentLength() {
		return getAttributeLength("description");
	}

	public Range getPeriodRange() {
		return getAttributeRange("beaconPeriod");
	}

	public Range getClientRange() {
		return getAttributeRange("maxClients");
	}

	public Range getFactorRange() {
		return getAttributeRange("threshold");
	}

	public Range getTrigTimeRange() {
		return getAttributeRange("triggerTime");
	}

	public Range getHoldTimeRange() {
		return getAttributeRange("holdTime");
	}

	public Range getIntervalRange() {
		return getAttributeRange("interval");
	}

	public Range getPowerRange() {
		return getAttributeRange("transmitPower");
	}

	public Range getChannelClientRange() {
		return getAttributeRange("channelClient");
	}

	public Range getRadioValueRange() {
		return getAttributeRange("radioRange");
	}
	
	public Range getCcaValueRange() {
		return getAttributeRange("defaultCcaValue");
	}
	
	public Range getCrcThresholdRange(){
		return getAttributeRange("crcThreshold");
	}
	
	public Range getChannelThresholdRange(){
		return getAttributeRange("channelThreshold");
	}
	
	public Range getAverageIntervalRange(){
		return getAttributeRange("averageInterval");
	}
	
	public Range getChannelSwitchRange() {
		return getAttributeRange("iuThreshold");
	}
	
	public Range getCrcErrorLimitRange(){
		return getAttributeRange("crcErrorLimit");
	}
	
	public Range getCuLimitRange(){
		return getAttributeRange("cuLimit");
	}
	
	public Range getMaxInterferenceRange(){
		return getAttributeRange("maxInterference");
	}
	
	public Range getClientHoldTimeRange(){
		return getAttributeRange("clientHoldTime");
	}
	
	public Range getSafetyNetTimeoutRange(){
		return getAttributeRange("safetyNetTimeout");
	}
	
	public Range getSuppressThresholdRange(){
		return getAttributeRange("suppressThreshold");
	}
	
	public Range getQueryIntervalRange(){
		return getAttributeRange("queryInterval");
	}

	public Range getTrapIntervalRange() {
		return getAttributeRange("trapInterval");
	}
	
	public Range getAgingTimeRange() {
		return getAttributeRange("agingTime");
	}
	
	public Range getAggrIntervalRange() {
		return getAttributeRange("aggrInterval");
	}

	public EnumItem[] getEnumRadioMode() {
		return RadioProfile.ENUM_RADIO_PROFILE_MODE;
	}
	
	public EnumItem[] getEnumTxBeamformingMode() {
		return RadioProfile.ENUM_TX_BEAMFORMING_MODE;
	}

	public EnumItem[] getEnumRate() {
		return getTransmitRateFrom(getDataSource().getRadioMode());
	}

	public EnumItem[] getEnumPreamble() {
		return RadioProfile.ENUM_RADIO_PROFILE_PREAMBLE;
	}

	public EnumItem[] getEnumRoaming() {
		return RadioProfile.RADIO_ROAMING_THRESHOLD;
	}

	public EnumItem[] getEnumHours() {
		return SchedulerAction.ENUM_HOURS;
	}

	public EnumItem[] getEnumMinutes() {
		return SchedulerAction.ENUM_MINUTES;
	}

	public EnumItem[] getEnumWidth() {
		return RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH;
	}
	
	public EnumItem[] getEnumWidth11AC() {
		return RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_11AC;
	}

	public EnumItem[] getEnumChain() {
		return RadioProfile.RADIO_PROFILE_CHAIN;
	}

	public EnumItem[] getEnumRegion() {
		return RadioProfile.RADIO_PROFILE_CHANNEL_REGION;
	}

	public EnumItem[] getEnumChannelModel() {
		return RadioProfile.RADIO_PROFILE_CHANNEL_MODEL;
	}
	
	public EnumItem[] getEnumHighDensityRate(){
		return RadioProfile.HIGH_DENSITY_TRANSMIT_RATE;
	}

	public EnumItem[] getEnumAntenna20() {
		return RadioProfile.RADIO_ANTENNA20_TYPE;
	}
	
	public EnumItem[] getEnumAntenna28() {
		EnumItem[] enumItems = new EnumItem[3];
		enumItems[0] = new EnumItem(RadioProfile.RADIO_ANTENNA28_TYPE_D, MgrUtil.getUserMessage("config.radioProfile.antenna28.type.3"));
		String radioHz = "2.4";
		if (RadioProfile.RADIO_PROFILE_MODE_A == getDataSource().getRadioMode()) {
			radioHz = "5";
		}
		enumItems[1] = new EnumItem(RadioProfile.RADIO_ANTENNA28_TYPE_A, MgrUtil.getUserMessage("config.radioProfile.antenna28.type.1", radioHz));
		enumItems[2] = new EnumItem(RadioProfile.RADIO_ANTENNA28_TYPE_B, MgrUtil.getUserMessage("config.radioProfile.antenna28.type.2", radioHz));
		return enumItems;
	}
	
	public EnumItem[] getSlaThoughputOption1() {
		return new EnumItem[] { new EnumItem(
				SlaMappingCustomize.SLA_THROUGHPUT_HIGH,
				getText("config.radioProfile.sla.throughput.high")) };
	}
	
	public EnumItem[] getSlaThoughputOption2() {
		return new EnumItem[] { new EnumItem(
				SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM,
				getText("config.radioProfile.sla.throughput.medium")) };
	}
	
	public EnumItem[] getSlaThoughputOption3() {
		return new EnumItem[] { new EnumItem(
				SlaMappingCustomize.SLA_THROUGHPUT_LOW,
				getText("config.radioProfile.sla.throughput.low")) };
	}

	public EnumItem[] getTransmitRateFrom(short arg_Mode) {
		switch (arg_Mode) {
			case RadioProfile.RADIO_PROFILE_MODE_A :
				return RadioProfile.RADIO_TRANSMIT_RATE_A;
			case RadioProfile.RADIO_PROFILE_MODE_NA :
				return RadioProfile.RADIO_TRANSMIT_RATE_NA;
			case RadioProfile.RADIO_PROFILE_MODE_BG :
				return RadioProfile.RADIO_TRANSMIT_RATE_BG;
			case RadioProfile.RADIO_PROFILE_MODE_NG :
				return RadioProfile.RADIO_TRANSMIT_RATE_NG;
			default :
				return RadioProfile.RADIO_TRANSMIT_RATE_BG;
		}
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public String getHide11nMode() {
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA
			|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG
			|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC
			? "" : "none";
	}
	
	public String getShowNoteFor11ngMode(){
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG ?  "": "none";
	}
	
	public String getHide11naMode() {
		return (getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA || 
				getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
				|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC)
			? "" : "none";
	}
	
	public String getHideRadarDetect(){
		if (getHide11naMode().equals("")){
			if (getHmSettingEnableRadarDetection() && getDataSource().isEnableDfs()){
				return "";
			}
		}
		return "none";
	}
	
	public String getChannelModelStyle(){
		return getDataSource().isUseDefaultChannelModel() ? "none" : "";
	}
	
	public String getChannelSelectionStyle(){
		return getDataSource().isEnableChannel() ? "" : "none";
	}
	
	public String getBgScanStyle(){
		return getDataSource().getBackgroundScan()? "" : "none";
	}
	
	public String getTurboModeStyle(){
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A ? "": "none";
	}
	
	public String getPreambleStyle(){
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
			|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA
					|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC ? "none" : "";
	}
	
	public String getInterferenceTrStyle(){
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA
			|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG
					|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC ? "" : "none";
	}
	
	public boolean isRadio5G(){
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
				|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA
						|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC;
	}
	
	public String getTxbeamforimgTrStyle(){
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC ? "" : "none";
	}
	
	public String getTxbeamforimgModeStyle(){
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC &&  
			   getDataSource().isEnabledTxbeamforming() == true ? "" : "none";
	}
	
	public String getChainStyleForOem(){
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
			|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG ? "none" : "";
	}

	public String getChangedName() {
		return getDataSource().getRadioName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public boolean getIsAmode() {
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A;
	}
    
    public boolean getDisableTurbomode() {
		return !getIsAmode() || getDisableMode();
	}
	
	public boolean getIsAOrNAmode() {
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
			|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA
			|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC;
	}
	
	public boolean getIsACmode(){
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC;
	}

	public boolean getIs20Channel() {
		return getDataSource().getChannelWidth() == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20;
	}

	public boolean getDisableMode() {
		if (getDataSource().getDefaultFlag() || getDataSource().getId() == null) {
			return false;
		} else {
			String where = "wifi1RadioProfile.id = :s1 OR wifi0RadioProfile.id = :s2";
			Object[] values = new Object[2];
			values[0] = getDataSource().getId();
			values[1] = getDataSource().getId();
			List<?> boIds = QueryUtil.executeQuery("select id from "
				+ (HiveAp.class).getSimpleName(), null,
				new FilterParams(where, values), domainId);
			return !boIds.isEmpty();
		}
	}

	public boolean getDisableDfs() {
		return (getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA || 
				getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
				|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC)
				&& getDisableMode() && getDataSource().isEnableDfs();
	}

//	protected JSONObject jsonObject = null;

//	public String getJSONString() {
//		return jsonObject.toString();
//	}

	private short mode;

	public String getChangeRate() {
		return "changeRate";
	}

	public void setMode(short mode)
	{
		this.mode = mode;
	}
	
	protected short[] minimums;

	protected short[] maximums;

	protected short[] aifses;
	
	protected int[] txoplimits;
	
	protected String[] noAcks;
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_MODE = 2;
	
	public static final int COLUMN_RATE = 3;
	
	public static final int COLUMN_PREAMBLE = 4;
	
	public static final int COLUMN_PERIOD = 5;
	
	public static final int COLUMN_CLIENT = 6;
	
	public static final int COLUMN_DESCRIPTION = 7;
	
	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return String -
	 */
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.radioProfile.name";
			break;
		case COLUMN_MODE:
			code = "config.radioProfile.radioMode";
			break;
		case COLUMN_RATE:
			code = "config.radioProfile.rate";
			break;
		case COLUMN_PREAMBLE:
			code = "config.radioProfile.preamble";
			break;
		case COLUMN_PERIOD:
			code = "config.radioProfile.period";
			break;
		case COLUMN_CLIENT:
			code = "config.radioProfile.client";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.radioProfile.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_MODE));
		columns.add(new HmTableColumn(COLUMN_PREAMBLE));
		columns.add(new HmTableColumn(COLUMN_PERIOD));
		columns.add(new HmTableColumn(COLUMN_CLIENT));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	public short[] getMinimums() {
		return minimums;
	}

	public void setMinimums(short[] minimums) {
		this.minimums = minimums;
	}

	public short[] getMaximums() {
		return maximums;
	}

	public void setMaximums(short[] maximums) {
		this.maximums = maximums;
	}

	public short[] getAifses() {
		return aifses;
	}

	public void setAifses(short[] aifses) {
		this.aifses = aifses;
	}

	public int[] getTxoplimits() {
		return txoplimits;
	}

	public void setTxoplimits(int[] txoplimits) {
		this.txoplimits = txoplimits;
	}

	public String[] getNoAcks() {
		return noAcks;
	}

	public void setNoAcks(String[] noAcks) {
		this.noAcks = noAcks;
	}

	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof RadioProfile) {
			if (null != ((RadioProfile) bo).getWmmItems()) {
				((RadioProfile) bo).getWmmItems().size();
			}
			if(null != ((RadioProfile)bo).getSupressBprOUIs()){
				((RadioProfile) bo).getSupressBprOUIs().size();
			}
		}
		return null;
	}
	

	public EnumItem[] getEnumBandSteeringMode(){
		return RadioProfile.BAND_STEERING_MODE;
	}

	public EnumItem[] getEnumLoadBalancingMode(){
		return RadioProfile.LOAD_BALANCING_MODE;
	}
	
	public Range getLimitNumberRange(){
		return getAttributeRange("limitNumber");
	}
	
	public Range getMinimumRatioRange(){
		return getAttributeRange("minimumRatio");
	}
	
	public String getBandSteeringStyle(){
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG
			|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG ? "" : "none";
	}
	
	public String  getBalanceModeStyle(){
		return getDataSource().getLoadBalancingMode() == RadioProfile.LOAD_BALANCE_MODE_AIRTIME_BASED
		? "" : "none";
	}
	
	public String  getModePreferStyle(){
		return (getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG
				|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG) && (getDataSource().getBandSteeringMode() == RadioProfile.BAND_STEERING_MODE_PREFER5G)
		? "" : "none";
	}
	
	public String  getModeBalanceBandStyle(){
		return (getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG
				|| getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG) &&
			(getDataSource().getBandSteeringMode() == RadioProfile.BAND_STEERING_MODE_BALANCEBAND)
		? "" : "none";
	}
	
	public String getVhtSectionStyle(){
		return getDataSource().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG 
				&& getDataSource().getChannelWidth() == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 
				? "" : "none";
	}

	public boolean isPresenceEnable() {
		return PresenceUtil.isPresenceSettingEnabled();
	}
	
	public boolean getPresenceRegistered() {
		if (!isPresenceEnable()) {
			return false;
		}
		try {
			return PresenceUtil.isCustomerRegistered(domainId);
		} catch (HmException e) {
			return false;
		}
	}
	
	public boolean getHmSettingEnableRadarDetection(){
		List<HmUser> userList = QueryUtil.executeQuery(HmUser.class,
				null, null);
		if(!userList.isEmpty()){
			for(HmUser user :userList){
				if(user.isSuperUser()){
					HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner",
			                user.getDomain());
					if(null != bo && bo.isEnableRadarDetection()){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private String radioProfileName;
	private Short radioProfileMode;
	private Short radioProfileChannelWidth;

	public String getRadioProfileName() {
		return radioProfileName;
	}

	public void setRadioProfileName(String radioProfileName) {
		this.radioProfileName = radioProfileName;
	}

	public Short getRadioProfileMode() {
		return radioProfileMode;
	}

	public void setRadioProfileMode(Short radioProfileMode) {
		this.radioProfileMode = radioProfileMode;
	}

	public Short getRadioProfileChannelWidth() {
		return radioProfileChannelWidth;
	}

	public void setRadioProfileChannelWidth(Short radioProfileChannelWidth) {
		this.radioProfileChannelWidth = radioProfileChannelWidth;
	}
	
	protected OptionsTransfer supressBprOuiOptions;
	
	protected List<Long> macOrOuis;
	
	private long macOrOui;
	
	private void prepareAvailableSupressBprOuis() throws Exception {
		List<MacOrOui> availableMacOrOuis = QueryUtil.executeQuery(MacOrOui.class,
				null,null, domainId);
		List<MacOrOui> macOuis = new ArrayList<MacOrOui>();

		for (MacOrOui macOrOui : availableMacOrOuis) {
			if (macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_OUI) {
				boolean exist = false;
				for (MacOrOui exitOui : macOuis) {
					if (macOrOui.getMacOrOuiName().equals(
							exitOui.getMacOrOuiName())) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					macOuis.add(macOrOui);
					if(null != getDataSource().getSupressBprOUIs()){
						for (Object obj : getDataSource().getSupressBprOUIs()) {
							MacOrOui tmp_macOrOui = (MacOrOui) obj;
							if (tmp_macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_OUI) {
								if (tmp_macOrOui.getMacOrOuiName().equals(
										macOrOui.getMacOrOuiName())) {
									macOuis.remove(macOrOui);
								}
							}
						}
					}
				}
			}
		}
		// For the OptionsTransfer component
		supressBprOuiOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.ids.availableMacOrOuis"), MgrUtil
				.getUserMessage("config.ids.selectedMacOrOuis"), macOuis,
				getDataSource().getSupressBprOUIs(), "id", "macOrOuiName",
				"macOrOuis", "MacOrOui", SIMPLE_OBJECT_MAC, MAC_SUB_OBJECT_OUI,
				"", domainId,"180px",getDataSource().isEnableHighDensity());
	}
	
	private void setSelectedMacOrOuis() throws Exception {
		if(getDataSource().isEnableBroadcastProbe()){
			getDataSource().setEnableSupressBPRByOUI(false);
		}
		
		if(!getDataSource().isEnableBroadcastProbe() && getDataSource().isEnableSupressBPRByOUI()){
			Set<MacOrOui> idsMacOrOui = getDataSource().getSupressBprOUIs();
			idsMacOrOui.clear();
			if (macOrOuis != null) {
	
				for (Long ouiId : macOrOuis) {
					MacOrOui macOrOui = findBoById(MacOrOui.class, ouiId);
					if (macOrOui != null) {
						idsMacOrOui.add(macOrOui);
					}
				}
				if (idsMacOrOui.size() != macOrOuis.size()) {
					String tempStr[] = { getText("config.ids.selectedMacOrOuis") };
					addActionError(getText("info.ssid.warning.deleteRecord",
							tempStr));
				}
			}
			getDataSource().setSupressBprOUIs(idsMacOrOui);
			log.info("setSelectedMacOrOuis", "Radio profile "
					+ getDataSource().getRadioName() + " has "
					+ idsMacOrOui.size() + " MacOrOuis.");
		}else{
			getDataSource().setSupressBprOUIs(null);
		}
	}

	public OptionsTransfer getSupressBprOuiOptions() {
		return supressBprOuiOptions;
	}

	public void setSupressBprOuiOptions(OptionsTransfer supressBprOuiOptions) {
		this.supressBprOuiOptions = supressBprOuiOptions;
	}

	public long getMacOrOui() {
		return macOrOui;
	}

	public void setMacOrOui(long macOrOui) {
		this.macOrOui = macOrOui;
	}

	public List<Long> getMacOrOuis() {
		return macOrOuis;
	}

	public void setMacOrOuis(List<Long> macOrOuis) {
		this.macOrOuis = macOrOuis;
	}
	
	public String getSupressBprByOUIStyle(){
		return !getDataSource().isEnableBroadcastProbe()? "" :"none";
	}
	
	public String getSupressBprByOUIOptionStyle(){
		return (!getDataSource().isEnableBroadcastProbe() && getDataSource().isEnableSupressBPRByOUI()) ? "" :"none";
	}
	
	public String getDefaultChainTip(){
		String result;
		if(isOEMSystem()){
			result = MgrUtil.getResourceString("config.radioProfile.use.default.chain.oem.tip");
		} else if(isEasyMode()) {
			result = MgrUtil.getResourceString("config.radioProfile.use.default.chain.express.tip");
		} else {
			result = MgrUtil.getResourceString("config.radioProfile.use.default.chain.tip");
		}
		return result;
	}
	
	public String getChainTip(){
		String result;
		if(isOEMSystem()){
			result = MgrUtil.getResourceString("config.radioProfile.chain.note.oem");
		} else {
			result = MgrUtil.getResourceString("config.radioProfile.chain.note");
		}
		return result;
	}
}