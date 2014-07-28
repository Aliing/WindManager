package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.RadioProfileInt;
import com.ah.be.config.create.source.RadioProfileInt.WmmType;
import com.ah.bo.wlan.SlaMappingCustomize.ClientPhyMode;
import com.ah.xml.be.config.*;

/**
 * 
 * @author zhang
 *
 */
public class CreateRadioTree {
	
	private RadioProfileInt radioProfileImpl;
	private RadioObj radioObj;
	
	private  GenerateXMLDebug oDebug;
	
	private List<Object> radioChildList_1 = new ArrayList<Object>();
	private List<Object> radioChildList_2 = new ArrayList<Object>();
	private List<Object> radioChildList_3 = new ArrayList<Object>();
	private List<Object> radioChildList_4 = new ArrayList<Object>();
	private List<Object> radioChildList_5 = new ArrayList<Object>();
	
	private List<Object> radioRateList_1 = new ArrayList<Object>();

	public CreateRadioTree(RadioProfileInt radioProfileImpl, GenerateXMLDebug oDebug){
		this.radioProfileImpl = radioProfileImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		if(radioProfileImpl.isConfigureRadioTree()){
			this.radioObj = new RadioObj();
			generateRadioLevel_1();
		}
	}
	
	public RadioObj getRadioObj(){
		return this.radioObj;
	}
	
	private void generateRadioLevel_1() throws Exception {
		/**
		 * <profile>	RadioObj.Profile
		 */
		
		/** attribute: updateTime*/
		radioObj.setUpdateTime(radioProfileImpl.getUpdateTime());
		
		/** element: profile*/
		for(int i=0; i<radioProfileImpl.getRadioProfileSize(); i++){
			if(radioProfileImpl.isConfigureRadioProfile(i)){
				RadioObj.Profile profileObj = new RadioObj.Profile();
				radioChildList_1.add(profileObj);
				radioObj.getProfile().add(profileObj);
				
				generateRadioLevel_2(i);
			}
		}
	}
	
	private void generateRadioLevel_2(int index) throws Exception {
		/**
		 * <radio>.<profile>		RadioObj.Profile
		 */
		for(Object childObj : radioChildList_1){
			/** element: <radio>.<profile>*/
			if(childObj instanceof RadioObj.Profile){
				RadioObj.Profile profileObj = (RadioObj.Profile)childObj;
				
				/** attribute: updatTime */
				profileObj.setUpdateTime(radioProfileImpl.getProfileUpdatTime(index));
				
				/** attribute: name*/
				oDebug.debug("/configuration/radio", 
						"profile", GenerateXMLDebug.SET_NAME,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				profileObj.setName(radioProfileImpl.getName(index));
				
				/** attribute: operation*/
				profileObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
				
				/** element: <radio>.<profile>.<cr>*/
				profileObj.setCr("");
				
				/** element: <acsp> */
				RadioObj.Profile.Acsp acspObj = new RadioObj.Profile.Acsp();
				radioChildList_2.add(acspObj);
				profileObj.setAcsp(acspObj);
				
				/** element: <radio>.<profile>.<backhaul> */
				RadioObj.Profile.Backhaul backHaulObj = new RadioObj.Profile.Backhaul();
				radioChildList_2.add(backHaulObj);
				profileObj.setBackhaul(backHaulObj);
				
				/** element: <radio>.<profile>.<beacon-period>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"beacon-period", GenerateXMLDebug.SET_VALUE,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				Object[][] beaconPeriodParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getBeaconPeriod(index)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				profileObj.setBeaconPeriod(
						(RadioObj.Profile.BeaconPeriod)
						CLICommonFunc.createObjectWithName(RadioObj.Profile.BeaconPeriod.class, beaconPeriodParm)
				);
				
				/** element: <radio>.<profile>.<max-client>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"max-client", GenerateXMLDebug.SET_VALUE,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				Object[][] maxClientObj = {
						{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getMaxClient(index)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				profileObj.setMaxClient(
						(RadioObj.Profile.MaxClient)
						CLICommonFunc.createObjectWithName(RadioObj.Profile.MaxClient.class, maxClientObj)
				);
				
				/** element: <radio>.<profile>.<phymode>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"phymode", GenerateXMLDebug.SET_VALUE,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				Object[][] phyModeParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getRadioPhyMode(index)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				profileObj.setPhymode(
						(RadioObj.Profile.Phymode)
						CLICommonFunc.createObjectWithName(RadioObj.Profile.Phymode.class, phyModeParm)
				);
				
				/** element: <radio>.<profile>.<scan>*/
//				if(radioProfileImpl.isConfigureRadioScan(index)){
				RadioObj.Profile.Scan scanObj = new RadioObj.Profile.Scan();
				radioChildList_2.add(scanObj);
				profileObj.setScan(scanObj);
//				}
				
				/** element: <radio>.<profile>.<short-preamble>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"short-preamble", GenerateXMLDebug.SET_OPERATION,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				profileObj.setShortPreamble(
					CLICommonFunc.getAhOnlyAct(radioProfileImpl.isShortPreamble(index))
				);
				
//				//3.5r2 hide it
//				/** element: <radio>.<profile>.<turbo> */
//				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
//						"turbo", GenerateXMLDebug.SET_OPERATION,
//						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
//				profileObj.setTurbo(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isEnableRadioTurbo(index)));
				
				/** element: <wmm> don't configure version 2.1*/
				
//				/** element: <radio>.<profile>.<channel-width>*/
//				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
//						"channel-width", GenerateXMLDebug.CONFIG_ELEMENT,
//						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
//				if(radioProfileImpl.isConfigChannelWidth(index)){
//					
//					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
//							"channel-width", GenerateXMLDebug.SET_VALUE,
//							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
//					Object[][] channelParm = {
//							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getRadioChannelWidth(index)},
//							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
//					};
//					profileObj.setChannelWidth(
//							(RadioObj.Profile.ChannelWidth)CLICommonFunc.createObjectWithName(
//									RadioObj.Profile.ChannelWidth.class, channelParm)
//					);
//				}
				
				/** element: <radio>.<profile>.<allow-11b-clients>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"allow-11b-clients", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isHiveAp11n()){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
							"allow-11b-clients", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					profileObj.setAllow11BClients(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isEnableAllow_11b_clients(index)));
				}
				
				/** element: <radio>.<profile>.<11n-clients-only>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"11n-clients-only", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isHiveAp11n()){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
							"11n-clients-only", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					profileObj.set11NClientsOnly(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isEnableOnly_11n_clients(index)));
				}
				
				/** element: <radio>.<profile>.<deny-client>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"deny-client", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigDenyClient(index)){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
							"deny-client", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] denyParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getDenyClientValue(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					profileObj.setDenyClient(
							(RadioObj.Profile.DenyClient)CLICommonFunc.createObjectWithName(RadioObj.Profile.DenyClient.class, denyParm)
					);
				}
				
				/** element: <radio>.<profile>.<short-guard-interval>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"short-guard-interval", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigShortGuardInterval(index)){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
							"short-guard-interval", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					profileObj.setShortGuardInterval(CLICommonFunc.getAhOnlyAct(
							radioProfileImpl.isEnableShortGuardInterval(index))
					);
				}
				
				/** element: <radio>.<profile>.<ampdu>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"ampdu", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isHiveAp11n()){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
							"ampdu", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					profileObj.setAmpdu(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isEnableAmpdu(index)));
				}
				
				/** element: <radio>.<profile>.<transmit-chain>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"transmit-chain", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigReceiveChain(index)){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
							"transmit-chain", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] chainParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getTransmitChain(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					profileObj.setTransmitChain(
							(RadioObj.Profile.TransmitChain)CLICommonFunc.createObjectWithName(
									RadioObj.Profile.TransmitChain.class, chainParm)
					);
				}
				
				/** element: <radio>.<profile>.<receive-chain>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"receive-chain", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigReceiveChain(index)){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
							"receive-chain", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] receiveParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getReceiveChain(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					profileObj.setReceiveChain(
							(RadioObj.Profile.ReceiveChain)CLICommonFunc.createObjectWithName(
									RadioObj.Profile.ReceiveChain.class, receiveParm)
					);
				}
				
				/** element: <radio>.<profile>.<dfs>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"dfs", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigAcspDfs(index)){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
							"dfs", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					RadioProfileDfs dfsObj = new RadioProfileDfs();
					radioChildList_2.add(dfsObj);
					profileObj.setDfs(dfsObj);
				}
				
				/** element: <radio>.<profile>.<wmm> */
				RadioObj.Profile.Wmm wmmObj = new RadioObj.Profile.Wmm();
				radioChildList_2.add(wmmObj);
				profileObj.setWmm(wmmObj);
				
				/** element: <radio>.<profile>.<interference-map> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"interference-map", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigInterMap(index)){
					RadioObj.Profile.InterferenceMap interMap = new RadioObj.Profile.InterferenceMap();
					radioChildList_2.add(interMap);
					profileObj.setInterferenceMap(interMap);
				}
				
				/** element: <radio>.<profile>.<benchmark> */
				RadioObj.Profile.Benchmark benchObj = new RadioObj.Profile.Benchmark();
				radioChildList_2.add(benchObj);
				profileObj.setBenchmark(benchObj);
				
				/** element: <radio>.<profile>.<old-high-density> */
				RadioObj.Profile.OldHighDensity oldhighDensityObj = new RadioObj.Profile.OldHighDensity();
				radioChildList_2.add(oldhighDensityObj);
				profileObj.setOldHighDensity(oldhighDensityObj);
				
				/** element: <radio>.<profile>.<band-steering> */
				RadioBandSteering bandSteering=new RadioBandSteering();
				radioChildList_2.add(bandSteering);
				profileObj.setBandSteering(bandSteering);
				
				/** element: <radio>.<profile>.<client-load-balance> */
				RadioClientLoadBalance loadBalance=new RadioClientLoadBalance();
				radioChildList_2.add(loadBalance);
				profileObj.setClientLoadBalance(loadBalance);
								
				/** element: <radio>.<profile>.<weak-snr-suppress> */
				RadioWeakSnrSuppress weakSnrObj=new RadioWeakSnrSuppress();
				radioChildList_2.add(weakSnrObj);
				profileObj.setWeakSnrSuppress(weakSnrObj);
				
				/** element: <radio>.<profile>.<safety-net> */
				RadioSafetyNet safetyNetObj=new RadioSafetyNet();
				radioChildList_2.add(safetyNetObj);
				profileObj.setSafetyNet(safetyNetObj);
				
				/** element: <radio>.<profile>.<high-density> */
				RadioHighDensity highDensity=new RadioHighDensity();
				radioChildList_2.add(highDensity);
				profileObj.setHighDensity(highDensity);
				
				/** element: <radio>.<profile>.<detect-bssid-spoofing> */
				profileObj.setDetectBssidSpoofing(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isEnableDetectBssidSpoofing(index)));
			
				/** element: <radio>.<profile>.<presence> */
				RadioPresence presenceObj = new RadioPresence();
				radioChildList_2.add(presenceObj);
				profileObj.setPresence(presenceObj);
				
				if (radioProfileImpl.isEnableConnectionAlarm()) {
					RadioConnectionAlarming alarm = new RadioConnectionAlarming();
					profileObj.setConnectionAlarming(alarm);
					radioChildList_2.add(alarm);
				}
				
				/** element: <radio>.<profile>.<primary-channel-offset> */
//				/**fix 27820 bug*/
//				//profileObj.setPrimaryChannelOffset(CLICommonFunc.createAhStringActObj(radioProfileImpl.getChannelOffset(index), true));
//				String offset = radioProfileImpl.getChannelOffset(index);
//				if(!"auto".equals(offset)){
//					profileObj.setPrimaryChannelOffset(CLICommonFunc.createAhNameActObj(offset, true));
//				}
				
				/** element: <radio>.<profile>.<sensor> */
				RadioSensor radioSensor = new RadioSensor();
				profileObj.setSensor(radioSensor);
				radioChildList_2.add(radioSensor);
				
				/** element: <radio>.<profile>.<frameburst>*/				
				profileObj.setFrameburst(
					CLICommonFunc.getAhOnlyAct(radioProfileImpl.isFrameburstEnabled(index))
				);
				
				/** element: <radio>.<profile>.<vht-2g>*/		
				profileObj.setVht2G(
					CLICommonFunc.getAhOnlyAct(radioProfileImpl.isVHTEnabled(index))
				);
				
				RadioTxBeamforming txBeamForming = new RadioTxBeamforming();
				radioChildList_2.add(txBeamForming);				
				profileObj.setTxBeamforming(txBeamForming);
			}
		}
		radioChildList_1.clear();
		generateRadioLevel_3(index);
	}
	
	private void generateRadioLevel_3(int index) throws Exception {
		/**
		 * <radio>.<profile>.<backhaul>			RadioObj.Profile.Backhaul
		 * <radio>.<profile>.<scan>				RadioObj.Profile.Scan
		 * <radio>.<profile>.<acsp>				RadioObj.Profile.Acsp
		 * <radio>.<profile>.<wmm>				RadioObj.Profile.Wmm
		 * <radio>.<profile>.<interference-map>	RadioObj.Profile.InterferenceMap
		 * <radio>.<profile>.<benchmark>		RadioObj.Profile.Benchmark
		 * <radio>.<profile>.<old-high-density>	RadioObj.Profile.OldHighDensity
		 * <radio>.<profile>.<band-steering>	RadioBandSteering
		 * <radio>.<profile>.<client-load-balance>	RadioClientLoadBalance
		 * <radio>.<profile>.<weak-snr-suppress>	RadioWeakSnrSuppress
		 * <radio>.<profile>.<safety-net>		RadioSafetyNet
		 * <radio>.<profile>.<high-density>		RadioHighDensity
		 * <radio>.<profile>.<dfs>				RadioProfileDfs
		 * <radio>.<profile>.<presence>			RadioPresence
		 */
		for(Object childObj : radioChildList_2){
			
			/** element: <radio>.<profile>.<backhaul>*/
			if(childObj instanceof RadioObj.Profile.Backhaul){
				RadioObj.Profile.Backhaul backHaulObj = (RadioObj.Profile.Backhaul)childObj;
				
				/** element: <radio>.<profile>.<backhaul>.<failover>*/
				RadioObj.Profile.Backhaul.Failover failoverObj = new RadioObj.Profile.Backhaul.Failover();
				radioChildList_3.add(failoverObj);
				backHaulObj.setFailover(failoverObj);
			}
			
			/** element: <radio>.<profile>.<scan>*/
			if(childObj instanceof RadioObj.Profile.Scan){
				RadioObj.Profile.Scan sanObj = (RadioObj.Profile.Scan)childObj;
				
				/** element: <radio>.<profile>.<scan>.<access>*/
				RadioObj.Profile.Scan.Access accessObj = new RadioObj.Profile.Scan.Access();
				radioChildList_3.add(accessObj);
				sanObj.setAccess(accessObj);
			}
			
			/** element: <radio>.<profile>.<acsp> */
			if(childObj instanceof RadioObj.Profile.Acsp){
				RadioObj.Profile.Acsp acspObj = (RadioObj.Profile.Acsp)childObj;
				
				/** element: <radio>.<profile>.<acsp>.<max-tx-power> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp", 
						"max-tx-power", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigAcspMaxPower(index)){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp", 
							"max-tx-power", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] maxPowerParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getAcspMaxPower(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					acspObj.setMaxTxPower(
							(RadioObj.Profile.Acsp.MaxTxPower)CLICommonFunc.createObjectWithName(
									RadioObj.Profile.Acsp.MaxTxPower.class, maxPowerParm)
					);
				}
				
				/** element: <radio>.<profile>.<acsp>.<access> */
				AcspAccess accessObj = new AcspAccess();
				radioChildList_3.add(accessObj);
				acspObj.setAccess(accessObj);
				
				/** element: <radio>.<profile>.<acsp>.<channel-model> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp", 
						"channel-model", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigChannelMode(index)){
					RadioObj.Profile.Acsp.ChannelModel channelModelObj = new RadioObj.Profile.Acsp.ChannelModel();
					radioChildList_3.add(channelModelObj);
					acspObj.setChannelModel(channelModelObj);
				}
				
				/** element: <radio>.<profile>.<acsp>.<all-channels-model> */
				if(radioProfileImpl.isConfigAllChannelsModel(index)){
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/all-channels-model", 
							"enable", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					acspObj.setAllChannelsModel(
							CLICommonFunc.createAhEnable(radioProfileImpl.isAllChannelsModelEnable(index)));
				}
				
				/** element: <radio>.<profile>.<acsp>.<interference-switch> */
				AcspInterferenceSwitch switchObj = new AcspInterferenceSwitch();
				radioChildList_3.add(switchObj);
				acspObj.setInterferenceSwitch(switchObj);
			}
			
			/** element: <radio>.<profile>.<wmm> */
			if(childObj instanceof RadioObj.Profile.Wmm){
				RadioObj.Profile.Wmm wmmObj = (RadioObj.Profile.Wmm)childObj;
				
				/** element: <radio>.<profile>.<wmm>.<ac> */
				RadioObj.Profile.Wmm.Ac acObj = new RadioObj.Profile.Wmm.Ac();
				radioChildList_3.add(acObj);
				wmmObj.setAc(acObj);
			}
			
			/** element: <radio>.<profile>.<interference-map> */
			if(childObj instanceof RadioObj.Profile.InterferenceMap){
				RadioObj.Profile.InterferenceMap interMap = (RadioObj.Profile.InterferenceMap)childObj;
				
				/** element: <radio>.<profile>.<interference-map>.<enable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/interference-map", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				interMap.setEnable(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isEnableInterMap(index)));
				
				if(radioProfileImpl.isEnableInterMap(index)){
					
					/** element: <radio>.<profile>.<interference-map>.<crc-err-threshold> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/interference-map", 
							"crc-err-threshold", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] crcParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getCrcThreshold(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					interMap.setCrcErrThreshold(
							(RadioObj.Profile.InterferenceMap.CrcErrThreshold)CLICommonFunc.createObjectWithName(
									RadioObj.Profile.InterferenceMap.CrcErrThreshold.class, crcParm)
					);
					
					/** element: <radio>.<profile>.<interference-map>.<cu-threshold> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/interference-map", 
							"cu-threshold", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] cuParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getCuThreshold(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					interMap.setCuThreshold(
							(RadioObj.Profile.InterferenceMap.CuThreshold)CLICommonFunc.createObjectWithName(
									RadioObj.Profile.InterferenceMap.CuThreshold.class, cuParm)
					);
					
					/** element: <radio>.<profile>.<interference-map>.<short-term-interval> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/interference-map", 
							"short-term-interval", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] shortTermParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getShortInterval(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					interMap.setShortTermInterval(
							(RadioObj.Profile.InterferenceMap.ShortTermInterval)CLICommonFunc.createObjectWithName(
									RadioObj.Profile.InterferenceMap.ShortTermInterval.class, shortTermParm)
					);
				}
			}
			
			/** element: <radio>.<profile>.<benchmark> */
			if(childObj instanceof RadioObj.Profile.Benchmark){
				RadioObj.Profile.Benchmark benchObj = (RadioObj.Profile.Benchmark)childObj;
				
				/** element: <radio>.<profile>.<benchmark>.<phymode> */
				BenchmarkPhymode phymodeObj = new BenchmarkPhymode();
				radioChildList_3.add(phymodeObj);
				benchObj.setPhymode(phymodeObj);
			}
			
			/** element: <radio>.<profile>.<old-high-density> */
			if(childObj instanceof RadioObj.Profile.OldHighDensity){
				RadioObj.Profile.OldHighDensity highDensityObj = (RadioObj.Profile.OldHighDensity)childObj;
				
				/** element: <radio>.<profile>.<old-high-density>.<enable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				highDensityObj.setEnable(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isHighDensityEnable(index)));
				
				if(radioProfileImpl.isHighDensityEnable(index)){
					
					/** element: <radio>.<profile>.<old-high-density>.<mgmt-frame-tx-rate> */
					RadioObj.Profile.OldHighDensity.MgmtFrameTxRate txRateObj = new RadioObj.Profile.OldHighDensity.MgmtFrameTxRate();
					radioChildList_3.add(txRateObj);
					highDensityObj.setMgmtFrameTxRate(txRateObj);
					
					/** element: <radio>.<profile>.<old-high-density>.<continuous-probe-suppress> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/continuous-probe-suppress", 
							"enable", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					highDensityObj.setContinuousProbeSuppress(
							CLICommonFunc.createAhEnable(radioProfileImpl.isContinuousSuppressEnable(index)));
					
					/** element: <radio>.<profile>.<old-high-density>.<weak-snr-suppress> */
					RadioObj.Profile.OldHighDensity.WeakSnrSuppress snrObj = new RadioObj.Profile.OldHighDensity.WeakSnrSuppress();
					radioChildList_3.add(snrObj);
					highDensityObj.setWeakSnrSuppress(snrObj);
					
					/** element: <radio>.<profile>.<old-high-density>.<client-load-balance> */
					RadioObj.Profile.OldHighDensity.ClientLoadBalance loadBalanceObj = new RadioObj.Profile.OldHighDensity.ClientLoadBalance();
					radioChildList_3.add(loadBalanceObj);
					highDensityObj.setClientLoadBalance(loadBalanceObj);
					
					/** element: <radio>.<profile>.<old-high-density>.<safety-net> */
					RadioObj.Profile.OldHighDensity.SafetyNet safetyNetObj = new RadioObj.Profile.OldHighDensity.SafetyNet();
					radioChildList_3.add(safetyNetObj);
					highDensityObj.setSafetyNet(safetyNetObj);
					
					/** element: <radio>.<profile>.<old-high-density>.<broadcast-probe-suppress> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/broadcast-probe-suppress", 
							"enable", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					highDensityObj.setBroadcastProbeSuppress(
							CLICommonFunc.createAhEnable(radioProfileImpl.isBroadcastProbeEnable(index))
					);
					
					/** element: <radio>.<profile>.<old-high-density>.<band-steering> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/band-steering", 
							"enable", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					highDensityObj.setBandSteering(
							CLICommonFunc.createAhEnable(radioProfileImpl.isBandSteeringEnable(index))
					);
				}
			}
			
			/** element: <radio>.<profile>.<dfs> */
			if(childObj instanceof RadioProfileDfs){
				RadioProfileDfs dfsObj = (RadioProfileDfs)childObj;
				
				/** element: <radio>.<profile>.<dfs>.<cr> */
				dfsObj.setCr(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isEnableAcspDfs(index)));
				
				/** element: <radio>.<profile>.<dfs>.<radar-detect-only> */
				//dfsObj.setRadarDetectOnly(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isRadarDetectOnly(index)));
			}
			
			/** element: <radio>.<profile>.<band-steering> */
			if(childObj instanceof RadioBandSteering){
				RadioBandSteering bandSteeringObj=(RadioBandSteering)childObj;
				/** element: <radio>.<profile>.<band-steering> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/band-steering", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				bandSteeringObj.setEnable(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isBandSteeringEnable(index)));
				
				if(radioProfileImpl.isBandSteeringEnable(index)){
					/** element: <radio>.<profile>.<band-steering> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/band-steering", 
							"enable", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					
					Object[][] modeParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getBandSteeringModeVlaue(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					bandSteeringObj.setMode((BandSteeringMode)CLICommonFunc.createObjectWithName(BandSteeringMode.class,modeParm));
					
					/** element: <radio>.<profile>.<band-steering>.<prefer-5g> */
					if(radioProfileImpl.isConfigBandSteeringPrefer5G(index)){
						BandSteeringModePrefer5G prefer5G=new BandSteeringModePrefer5G();
						radioChildList_3.add(prefer5G);
						bandSteeringObj.setPrefer5G(prefer5G);
					}
					/** element: <radio>.<profile>.<band-steering>.<balance-band> */
					if(radioProfileImpl.isConfigBandSteeringBalanceBand(index)){
						BandSteeringModeBalanceBand bandBlance=new BandSteeringModeBalanceBand();
						radioChildList_3.add(bandBlance);
						bandSteeringObj.setBalanceBand(bandBlance);
					}
				}
			}
			
			/** element: <radio>.<profile>.<client-load-balance> */
			if(childObj instanceof RadioClientLoadBalance){
				RadioClientLoadBalance loadBalanceObj = (RadioClientLoadBalance)childObj;
				
				/** element: <radio>.<profile>.<client-load-balance>.<enable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/client-load-balance", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				loadBalanceObj.setEnable(CLICommonFunc.getAhOnlyAct(
						radioProfileImpl.isClientLoadBalanceEnable(index)));
				
				if(radioProfileImpl.isClientLoadBalanceEnable(index)){
					
					/** element: <radio>.<profile>.<client-load-balance>.<mode> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/client-load-balance", 
							"mode", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] modeParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getClientLoadBalanceModeValue(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					loadBalanceObj.setMode(
							(ClientLoadBalanceMode)CLICommonFunc.createObjectWithName(ClientLoadBalanceMode.class, modeParm)
					);
					
					/** element: <radio>.<profile>.<client-load-balance>.<hold-time> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/client-load-balance", 
							"hold-time", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] holdTimeParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getLoadBalanceHoldTime(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					loadBalanceObj.setHoldTime(
							(ClientLoadBalanceHoldTime)CLICommonFunc.createObjectWithName(ClientLoadBalanceHoldTime.class, holdTimeParm)
					);
					
					if(radioProfileImpl.isConfigClientLoadBalanceMode(index)){
						/** element: <radio>.<profile>.<client-load-balance>.<sta-minimum-airtime> */
						oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/client-load-balance", 
								"sta-minimum-airtime", GenerateXMLDebug.SET_VALUE,
								radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
						Object[][] cuLimitParm = {
								{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getLoadBalanceCuLimit(index)},
								{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
						};
						loadBalanceObj.setStaMiniAirtime(
								(ClientLoadBalanceStaMiniAirtime)CLICommonFunc.createObjectWithName(ClientLoadBalanceStaMiniAirtime.class, cuLimitParm)
						);
						
						/** element: <radio>.<profile>.<client-load-balance>.<interference-limit> */
						oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/client-load-balance", 
								"interference-limit", GenerateXMLDebug.SET_VALUE,
								radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
						Object[][] intLimitParm = {
								{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getLoadBalanceIntLimit(index)},
								{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
						};
						loadBalanceObj.setInterferenceLimit(
								(ClientLoadBalanceInterferenceLimit)CLICommonFunc.createObjectWithName(ClientLoadBalanceInterferenceLimit.class, intLimitParm)
						);
						
						/** element: <radio>.<profile>.<client-load-balance>.<crc-error-limit> */
						oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/client-load-balance", 
								"crc-error-limit", GenerateXMLDebug.SET_VALUE,
								radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
						Object[][] errLimitParm = {
								{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getLoadBalanceErrorLimit(index)},
								{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
						};
						loadBalanceObj.setCrcErrorLimit(
								(ClientLoadBalanceCrcErrorLimit)CLICommonFunc.createObjectWithName(ClientLoadBalanceCrcErrorLimit.class, errLimitParm)
						);
					}
					
					/** element: <radio>.<profile>.<client-load-balance>.<neighbor-load-query-interval> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/client-load-balance", 
							"neighbor-load-query-interval", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] neighBorParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getNeighborLoadQueryInterval(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					loadBalanceObj.setNeighborLoadQueryInterval(
							(ClientLoadBalanceNeighborLoadQueryInterval)CLICommonFunc.createObjectWithName(ClientLoadBalanceNeighborLoadQueryInterval.class, neighBorParm)
					);
					
				}
			}
			/** element: <radio>.<profile>.<weak-snr-suppress> */
			if(childObj instanceof RadioWeakSnrSuppress){
				RadioWeakSnrSuppress weakSnrObj = (RadioWeakSnrSuppress)childObj;
				
				/** element: <radio>.<profile>.<weak-snr-suppress>.<enable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/weak-snr-suppress", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				weakSnrObj.setEnable(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isWeakSnrSuppressEnable(index)));
				
				/** element: <radio>.<profile>.<weak-snr-suppress>.<threshold> */
				if(radioProfileImpl.isWeakSnrSuppressEnable(index)){
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/weak-snr-suppress", 
							"threshold", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] thresholdParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getWeakSnrSuppressThreshold(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					weakSnrObj.setThreshold(
							(WeakSnrSuppressThreshold)CLICommonFunc.createObjectWithName(WeakSnrSuppressThreshold.class,thresholdParm)
					);
				}
			}
			
			/** element: <radio>.<profile>.<safety-net> */
			if(childObj instanceof RadioSafetyNet){
				RadioSafetyNet safetyNetObj=(RadioSafetyNet)childObj;
				/** element: <radio>.<profile>.<safety-net>.<enable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/safety-net", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				safetyNetObj.setEnable(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isSafetyNetEnable(index)));
				
				/** element: <radio>.<profile>.<safety-net>.<timeout> */
				if(radioProfileImpl.isSafetyNetEnable(index)){
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/safety-net", 
							"timeout", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] timeoutParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getSafetyNetTimeout(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					safetyNetObj.setTimeout(
							(SafetyNetTimeout)CLICommonFunc.createObjectWithName(SafetyNetTimeout.class, timeoutParm)
					);
				}
			}
			
			/** element: <radio>.<profile>.<high-density> */
			if(childObj instanceof RadioHighDensity){
				RadioHighDensity highDensityObj=(RadioHighDensity)childObj;
				/** element: <radio>.<profile>.<high-density>.<enable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				highDensityObj.setEnable(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isHighDensityEnable(index)));
				if(radioProfileImpl.isHighDensityEnable(index)){
					
					/** element: <radio>.<profile>.<high-density>.<mgmt-frame-tx-rate> */
					HighDensityMgmtFrameTxRate txRateObj = new HighDensityMgmtFrameTxRate();
					radioChildList_3.add(txRateObj);
					highDensityObj.setMgmtFrameTxRate(txRateObj);
					
					/** element: <radio>.<profile>.<high-density>.<continuous-probe-suppress> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/high-density/continuous-probe-suppress", 
							"enable", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					highDensityObj.setContinuousProbeSuppress(
							CLICommonFunc.createAhEnable(radioProfileImpl.isContinuousSuppressEnable(index)));
										
					/** element: <radio>.<profile>.<high-density>.<broadcast-probe-suppress> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/high-density/broadcast-probe-suppress", 
							"enable", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					HighDensityBps bps = new HighDensityBps();
					bps.setEnable(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isBroadcastProbeEnable(index)));
					if (radioProfileImpl.isBroadcastOuiEnable(index)) {
						List<String> ouiList = radioProfileImpl.getBroadcastOuis(index);
						if (ouiList != null && ouiList.size() > 0) {
							for (String oui : ouiList) {
								if (StringUtils.isNotBlank(oui)) {
									bps.getOui().add(CLICommonFunc.createAhNameActValue(oui, true));
								}
								
							}
						}
					}
					highDensityObj.setBroadcastProbeSuppress(bps);
					
				}
				
			}
			
			/** element: <radio>.<profile>.<presence> */
			if(childObj instanceof RadioPresence){
				RadioPresence presence = (RadioPresence)childObj;
				
				if (radioProfileImpl.isEnablePresence(index)) {
					
					/** element: <radio>.<profile>.<presence> */
					presence.setEnable(CLICommonFunc.getAhOnlyAct(true));
						
					/** element: <radio>.<profile>.<presence>.<trap-interval> */
					Object[][] trapParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getPresenceTrapInterval(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					presence.setTrapInterval((PresenceTrapInterval)CLICommonFunc.createObjectWithName(
							PresenceTrapInterval.class, trapParm));
					
					/** element: <radio>.<profile>.<presence>.<aging-time> */
					Object[][] agTimeParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getPresenceAgingTime(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					presence.setAgingTime((PresenceAgingTime)CLICommonFunc.createObjectWithName(
							PresenceAgingTime.class, agTimeParm));
					
					/** element: <radio>.<profile>.<presence>.<aggr-interval> */
					PresenceAgingTime aggrInterval = new PresenceAgingTime();
					aggrInterval.setOperation(AhEnumAct.YES);
					aggrInterval.setValue(radioProfileImpl.getPresenceAggrInterval(index));
					presence.setAggrInterval(aggrInterval);
				}
				
			}
			
			if(childObj instanceof RadioConnectionAlarming){
				RadioConnectionAlarming alarm = (RadioConnectionAlarming)childObj;
				ConnectionAlarmingChannelUtilization channelUtilization = new ConnectionAlarmingChannelUtilization(); 
				alarm.setChannelUtilization(channelUtilization);
				radioChildList_3.add(channelUtilization);
			}
			
			/** element: <radio>.<profile>.<sensor> */
			if(childObj instanceof RadioSensor){
				RadioSensor sensor = (RadioSensor)childObj;
				/** element: <radio>.<profile>.<sensor>.<channel-list> */
				sensor.setChannelList(CLICommonFunc.createAhStringActObj(radioProfileImpl.getSensorChannelListValue(index), true));
				
				/** element: <radio>.<profile>.<sensor>.<dwell-time> */
				SensorDwellTime dwellTime = new SensorDwellTime();
				dwellTime.setValue(radioProfileImpl.getSensorDwellTime(index));
				dwellTime.setOperation(AhEnumAct.YES);
				sensor.setDwellTime(dwellTime);
			}
			
			if(childObj instanceof RadioTxBeamforming){
				RadioTxBeamforming txBeamForming = (RadioTxBeamforming)childObj;
				if(radioProfileImpl.isTxBeamformingEnabled(index)){
					if(radioProfileImpl.isTxBeamformingExplicitMode(index)){
						txBeamForming.setExplicitOnly("");
					}else{
						txBeamForming.setAuto("");
					}
					txBeamForming.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				}else{
					txBeamForming.setOperation(AhEnumAct.NO);
				}
			}
			
		}
		radioChildList_2.clear();
		generateRadioLevel_4(index);
	}
	
	private void generateRadioLevel_4(int index) throws Exception {
		/**
		 * <radio>.<profile>.<backhaul>.<failover>				RadioObj.Profile.Backhaul.Failover
		 * <radio>.<profile>.<scan>.<access>						RadioObj.Profile.Scan.Access
		 * <radio>.<profile>.<acsp>.<access>		AcspAccess
		 * <radio>.<profile>.<acsp>.<channel-model>				RadioObj.Profile.Acsp.ChannelModel
		 * <radio>.<profile>.<wmm>.<ac>			RadioObj.Profile.Wmm.Ac
		 * radio>.<profile>.<benchmark>.<phymode>				BenchmarkPhymode
		 * <radio>.<profile>.<old-high-density>.<mgmt-frame-tx-rate>	RadioObj.Profile.HighDensity.MgmtFrameTxRate
		 * <radio>.<profile>.<old-high-density>.<weak-snr-suppress>		RadioObj.Profile.HighDensity.WeakSnrSuppress
		 * <radio>.<profile>.<old-high-density>.<client-load-balance>	RadioObj.Profile.HighDensity.ClientLoadBalance
		 * <radio>.<profile>.<old-high-density>.<safety-net>			RadioObj.Profile.HighDensity.SafetyNet
		 * * <radio>.<profile>.<high-density>.<mgmt-frame-tx-rate>	    HighDensityMgmtFrameTxRate
		 * <radio>.<profile>.<acsp>.<interference-switch>				AcspInterferenceSwitch
		 */
		for(Object childObj : radioChildList_3){
			
			/** element: <radio>.<profile>.<backhaul>.<failover>*/
			if(childObj instanceof RadioObj.Profile.Backhaul.Failover){
				RadioObj.Profile.Backhaul.Failover failOverObj = (RadioObj.Profile.Backhaul.Failover)childObj;
				
				/** attribute: operation*/
				failOverObj.setOperation(
						CLICommonFunc.getAhEnumAct(radioProfileImpl.isEnableBackhaulFailover(index))
				);
				
				if (radioProfileImpl.isEnableBackhaulFailover(index)){
					
					/** element: <radio>.<profile>.<backhaul>.<failover>.<cr>*/
					failOverObj.setCr("");
					
					/** element: <radio>.<profile>.<backhaul>.<failover>.<trigger-time>*/
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/backhaul/failover", 
							"trigger-time", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] triggerTimeParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getBackHaulTriggerTime(index)}
					};
					failOverObj.setTriggerTime(
							(RadioObj.Profile.Backhaul.Failover.TriggerTime)
							CLICommonFunc.createObjectWithName(RadioObj.Profile.Backhaul.Failover.TriggerTime.class, triggerTimeParm)
					);
					
					/** element: <radio>.<profile>.<backhaul>.<failover>.<hold-time>*/
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/backhaul/failover", 
							"hold-time", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] holdTimeParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getBackHaulHoldTime(index)}
					};
					failOverObj.setHoldTime(
							(RadioObj.Profile.Backhaul.Failover.HoldTime)
							CLICommonFunc.createObjectWithName(RadioObj.Profile.Backhaul.Failover.HoldTime.class, holdTimeParm)
					);
				}
				
			}
			
			/** element: <radio>.<profile>.<scan>.<access>*/
			if(childObj instanceof RadioObj.Profile.Scan.Access){
				RadioObj.Profile.Scan.Access accessObj = (RadioObj.Profile.Scan.Access)childObj;
				
				/** element: <radio>.<profile>.<scan>.<access>.<cr>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/scan/access", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				accessObj.setCr(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isConfigureRadioScan(index)));
				
				
				/** element: <radio>.<profile>.<scan>.<access>.<interval>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/scan/access", 
						"interval", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigureRadioScan(index)){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/scan/access", 
							"interval", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] scanIntervalParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getScanInterval(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					accessObj.setInterval(
							(RadioObj.Profile.Scan.Access.Interval)
							CLICommonFunc.createObjectWithName(RadioObj.Profile.Scan.Access.Interval.class, scanIntervalParm)
					);
				}
				
				/** element: <radio>.<profile>.<scan>.<access>.<voice>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/scan/access", 
						"voice", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigureRadioScan(index)){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/scan/access", 
							"voice", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					accessObj.setVoice(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isEnableScanVoice(index)));
				}
				
				/** element: <radio>.<profile>.<scan>.<access>.<client>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/scan/access", 
						"client", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigureRadioScan(index)){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/scan/access", 
							"client", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					ScanAccessClient clientObj = new ScanAccessClient();
					radioChildList_4.add(clientObj);
					accessObj.setClient(clientObj);
				}
				
			}
			
			/** element: <radio>.<profile>.<acsp>.<access> */
			if(childObj instanceof AcspAccess){
				AcspAccess accessObj = (AcspAccess)childObj;
				
				/** element: <radio>.<profile>.<acsp>.<access>.<channel-auto-select> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/access", 
						"channel-auto-select", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigAcspAccessChannel(index)){
					AcspAccess.ChannelAutoSelect autoSelectObj = new AcspAccess.ChannelAutoSelect();
					radioChildList_4.add(autoSelectObj);
					accessObj.setChannelAutoSelect(autoSelectObj);
				}
				
				/** element: <radio>.<profile>.<acsp>.<access>.<dfs> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/access", 
						"dfs", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigAcspDfs(index)){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/access", 
							"dfs", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					accessObj.setDfs(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isEnableAcspDfs(index)));
				}
				
				/** element: <radio>.<profile>.<acsp>.<access>.<interference-switch> */
//				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/access", 
//						"interference-switch", GenerateXMLDebug.CONFIG_ELEMENT,
//						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
//				if(radioProfileImpl.isCofigInterSwitch(index)){
				AcspInterferenceSwitch interSwitchObj = new AcspInterferenceSwitch();
				radioChildList_4.add(interSwitchObj);
				accessObj.setInterferenceSwitch(interSwitchObj);
//				}
			}
			
			/** element: <radio>.<profile>.<acsp>.<channel-model> */
			if(childObj instanceof RadioObj.Profile.Acsp.ChannelModel){
				RadioObj.Profile.Acsp.ChannelModel channelModelObj = (RadioObj.Profile.Acsp.ChannelModel)childObj;
				
				/** attribute: operation */
				channelModelObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <radio>.<profile>.<acsp>.<channel-model>.<_3-channels> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/channel-model", 
						"3-channels", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigChannel_3(index)){
					channelModelObj.set3Channels(this.createChannelModelDetail(index));
				}
				
				/** element: <radio>.<profile>.<acsp>.<channel-model>.<_4-channels> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/channel-model", 
						"4-channels", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigChannel_4(index)){
					channelModelObj.set4Channels(this.createChannelModelDetail(index));
				}
			}
			
			/** element: <radio>.<profile>.<wmm>.<ac> */
			if(childObj instanceof RadioObj.Profile.Wmm.Ac){
				RadioObj.Profile.Wmm.Ac acObj = (RadioObj.Profile.Wmm.Ac)childObj;
				
				/** element: <radio>.<profile>.<wmm>.<ac>.<background> */
				acObj.setBackground(this.createWmmAcType(WmmType.background, index));
				
				/** element: <radio>.<profile>.<wmm>.<ac>.<best-effort> */
				acObj.setBestEffort(this.createWmmAcType(WmmType.best_effort, index));
				
				/** element: <radio>.<profile>.<wmm>.<ac>.<video> */
				acObj.setVideo(this.createWmmAcType(WmmType.video, index));
				
				/** element: <radio>.<profile>.<wmm>.<ac>.<voice> */
				acObj.setVoice(this.createWmmAcType(WmmType.voice, index));
			}
			
			/** element: <radio>.<profile>.<benchmark>.<phymode> */
			if(childObj instanceof BenchmarkPhymode){
				BenchmarkPhymode phymodeObj = (BenchmarkPhymode)childObj;
				
				/** element: <radio>.<profile>.<benchmark>.<phymode>.<_11a> */
				phymodeObj.set11A(this.createPhymodeRate(ClientPhyMode._11a, index));
				
				/** element: <radio>.<profile>.<benchmark>.<phymode>.<_11b> */
				phymodeObj.set11B(this.createPhymodeRate(ClientPhyMode._11b, index));
				
				/** element: <radio>.<profile>.<benchmark>.<phymode>.<_11g> */
				phymodeObj.set11G(this.createPhymodeRate(ClientPhyMode._11g, index));
				
				/** element: <radio>.<profile>.<benchmark>.<phymode>.<_11n> */
				phymodeObj.set11N(this.createPhymodeRate(ClientPhyMode._11n, index));
				
				/** element: <radio>.<profile>.<benchmark>.<phymode>.<_11ac> */
				phymodeObj.set11Ac(this.createPhymodeRate(ClientPhyMode._11ac, index));
				
			}
			
			/** element: <radio>.<profile>.<old-high-density>.<mgmt-frame-tx-rate> */
			if(childObj instanceof RadioObj.Profile.OldHighDensity.MgmtFrameTxRate){
				RadioObj.Profile.OldHighDensity.MgmtFrameTxRate txRateObj = (RadioObj.Profile.OldHighDensity.MgmtFrameTxRate)childObj;
				
				/** attribute: operation */
				txRateObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <radio>.<profile>.<old-high-density>.<mgmt-frame-tx-rate>.<high> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/mgmt-frame-tx-rate", 
						"high", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isTxRateHigh(index)){
					txRateObj.setHigh("");
				}
				
				/** element: <radio>.<profile>.<old-high-density>.<mgmt-frame-tx-rate>.<low> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/mgmt-frame-tx-rate", 
						"low", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isTxRateLow(index)){
					txRateObj.setLow("");
				}
			}
			
			/** element: <radio>.<profile>.<old-high-density>.<weak-snr-suppress> */
			if(childObj instanceof RadioObj.Profile.OldHighDensity.WeakSnrSuppress){
				RadioObj.Profile.OldHighDensity.WeakSnrSuppress snrObj = (RadioObj.Profile.OldHighDensity.WeakSnrSuppress)childObj;
				
				/** element: <radio>.<profile>.<old-high-density>.<weak-snr-suppress>.<enable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/weak-snr-suppress", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				snrObj.setEnable(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isWeakSnrSuppressEnable(index)));
				
				/** element: <radio>.<profile>.<old-high-density>.<weak-snr-suppress>.<threshold> */
				if(radioProfileImpl.isWeakSnrSuppressEnable(index)){
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/weak-snr-suppress", 
							"threshold", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] thresholdParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getWeakSnrSuppressThreshold(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					snrObj.setThreshold(
							(WeakSnrSuppressThreshold)CLICommonFunc.createObjectWithName(WeakSnrSuppressThreshold.class, thresholdParm)
					);
				}
			}
			
			/** element: <radio>.<profile>.<old-high-density>.<client-load-balance> */
			if(childObj instanceof RadioObj.Profile.OldHighDensity.ClientLoadBalance){
				
				RadioObj.Profile.OldHighDensity.ClientLoadBalance loadBalanceObj = (RadioObj.Profile.OldHighDensity.ClientLoadBalance)childObj;
				
				/** element: <radio>.<profile>.<old-high-density>.<client-load-balance>.<enable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/client-load-balance", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				loadBalanceObj.setEnable(CLICommonFunc.getAhOnlyAct(
						radioProfileImpl.isClientLoadBalanceEnable(index)));
				
				if(radioProfileImpl.isClientLoadBalanceEnable(index)){
					
					/** element: <radio>.<profile>.<old-high-density>.<client-load-balance>.<hold-time> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/client-load-balance", 
							"hold-time", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] holdTimeParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getLoadBalanceHoldTime(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					loadBalanceObj.setHoldTime(
							(ClientLoadBalanceHoldTime)CLICommonFunc.createObjectWithName(ClientLoadBalanceHoldTime.class, holdTimeParm)
					);
					
					/** element: <radio>.<profile>.<old-high-density>.<client-load-balance>.<sta-minimum-airtime> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/client-load-balance", 
							"sta-minimum-airtime", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] cuLimitParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getLoadBalanceCuLimit(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					loadBalanceObj.setStaMiniAirtime(
							(ClientLoadBalanceStaMiniAirtime)CLICommonFunc.createObjectWithName(ClientLoadBalanceStaMiniAirtime.class, cuLimitParm)
					);
					
					/** element: <radio>.<profile>.<old-high-density>.<client-load-balance>.<interference-limit> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/client-load-balance", 
							"interference-limit", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] intLimitParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getLoadBalanceIntLimit(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					loadBalanceObj.setInterferenceLimit(
							(ClientLoadBalanceInterferenceLimit)CLICommonFunc.createObjectWithName(ClientLoadBalanceInterferenceLimit.class, intLimitParm)
					);
					
					/** element: <radio>.<profile>.<old-high-density>.<client-load-balance>.<crc-error-limit> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/client-load-balance", 
							"crc-error-limit", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] errLimitParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getLoadBalanceErrorLimit(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					loadBalanceObj.setCrcErrorLimit(
							(ClientLoadBalanceCrcErrorLimit)CLICommonFunc.createObjectWithName(ClientLoadBalanceCrcErrorLimit.class, errLimitParm)
					);
				}
			}
			
			/** element: <radio>.<profile>.<old-high-density>.<safety-net> */
			if(childObj instanceof RadioObj.Profile.OldHighDensity.SafetyNet){
				RadioObj.Profile.OldHighDensity.SafetyNet safetyNetObj = (RadioObj.Profile.OldHighDensity.SafetyNet)childObj;
				
				/** element: <radio>.<profile>.<old-high-density>.<safety-net>.<enable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/safety-net", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				safetyNetObj.setEnable(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isSafetyNetEnable(index)));
				
				/** element: <radio>.<profile>.<old-high-density>.<safety-net>.<timeout> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/old-high-density/safety-net", 
						"timeout", GenerateXMLDebug.SET_VALUE,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				Object[][] timeoutParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getSafetyNetTimeout(index)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				safetyNetObj.setTimeout(
						(SafetyNetTimeout)CLICommonFunc.createObjectWithName(SafetyNetTimeout.class, timeoutParm)
				);
			}
			
			
			/** element: <radio>.<profile>.<acsp>.<interference-switch> */
			if(childObj instanceof AcspInterferenceSwitch){
				AcspInterferenceSwitch switchObj = (AcspInterferenceSwitch)childObj;
				
				if(!radioProfileImpl.isConfigInterSwitchDisable(index)){
					
					/** element: <radio>.<profile>.<acsp>.<interference-switch>.<iu-threshold> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/interference-switch", 
							"iu-threshold", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] iuThrParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getSwitchIuThreshold(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					switchObj.setIuThreshold(
							(InterferenceSwitchIuThreshold)CLICommonFunc.createObjectWithName(InterferenceSwitchIuThreshold.class, iuThrParm)
					);
					
					/** element: <radio>.<profile>.<acsp>.<interference-switch>.<crc-err-threshold> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/interference-switch", 
							"crc-err-threshold", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] crcErrParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getSwitchCrcErrThreshold(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					switchObj.setCrcErrThreshold(
							(InterferenceSwitchCrcErrThreshold)CLICommonFunc.createObjectWithName(InterferenceSwitchCrcErrThreshold.class, crcErrParm)
					);
				}
				
				/** element: <radio>.<profile>.<acsp>.<interference-switch>.<enable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/interference-switch", 
						"enable", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigInterSwitchEnable(index)){
					switchObj.setEnable(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <radio>.<profile>.<acsp>.<interference-switch>.<disable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/interference-switch", 
						"disable", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigInterSwitchDisable(index)){
					switchObj.setDisable(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <radio>.<profile>.<acsp>.<interference-switch>.<no-station-enable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/interference-switch", 
						"no-station-enable", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigInterSwitchNoStation(index)){
					switchObj.setNoStationEnable(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
			}
			/** element: <radio>.<profile>.<high-density>.<mgmt-frame-tx-rate> */
			if(childObj instanceof HighDensityMgmtFrameTxRate){
				HighDensityMgmtFrameTxRate txRateObj = (HighDensityMgmtFrameTxRate)childObj;
				
				/** attribute: operation */
				txRateObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <radio>.<profile>.<high-density>.<mgmt-frame-tx-rate>.<high> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/high-density/mgmt-frame-tx-rate", 
						"high", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isTxRateHigh(index)){
					txRateObj.setHigh("");
				}
				
				/** element: <radio>.<profile>.<old-high-density>.<mgmt-frame-tx-rate>.<low> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/high-density/mgmt-frame-tx-rate", 
						"low", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isTxRateLow(index)){
					txRateObj.setLow("");
				}
			}
			
			/** element: <radio>.<profile>.<band-steering>.<prefer-5g> */
			if(childObj instanceof BandSteeringModePrefer5G){
				BandSteeringModePrefer5G prefer5G=(BandSteeringModePrefer5G)childObj;
				Object[][] supressParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getBandSteeringLimitNumber(index)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				prefer5G.setSuppressionLimit((BandSteeringSuppressionLimit)CLICommonFunc.createObjectWithName(BandSteeringSuppressionLimit.class, supressParm));
			}
			
			/** element: <radio>.<profile>.<band-steering>.<balance-band> */
			if(childObj instanceof BandSteeringModeBalanceBand){
				BandSteeringModeBalanceBand bandBlance=(BandSteeringModeBalanceBand)childObj;
				Object[][] thresoldParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getBandSteeringMinimumRatio(index)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				bandBlance.setThreshold((BandSteeringThreshold)CLICommonFunc.createObjectWithName(BandSteeringThreshold.class, thresoldParm));
			}
			
			if(childObj instanceof ConnectionAlarmingChannelUtilization){
				ConnectionAlarmingChannelUtilization channelUtilization = (ConnectionAlarmingChannelUtilization)childObj;
				ConnectionAlarmingChannelUtilizationThreshold threshold = new ConnectionAlarmingChannelUtilizationThreshold();
				channelUtilization.setThreshold(threshold);
				radioChildList_4.add(threshold);
			}	
			
		}
		radioChildList_3.clear();
		generateRadioLevel_5(index);
	}
	
	private void generateRadioLevel_5(int index) throws Exception{
		/**
		 * <radio>.<profile>.<acsp>.<access>.<channel-auto-select>				AcspAccess.ChannelAutoSelect
		 * <radio>.<profile>.<scan>.<access>.<client>							ScanAccessClient
		 * <radio>.<profile>.<acsp>.<access>.<interference-switch>				AcspInterferenceSwitch
		 */
		for(Object childObj : radioChildList_4){
			
			/** element <radio>.<profile>.<acsp>.<max-tx-power>.<access>.<channel-auto-select> */
			if(childObj instanceof AcspAccess.ChannelAutoSelect){
				AcspAccess.ChannelAutoSelect autoSelectedObj = (AcspAccess.ChannelAutoSelect)childObj;
				
				/** attribute: operation */
				autoSelectedObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <radio>.<profile>.<acsp>.<max-tx-power>.<access>.<channel-auto-select>.<time_range> */
				AcspAccess.ChannelAutoSelect.TimeRange timeRangeObj = new AcspAccess.ChannelAutoSelect.TimeRange();
				radioChildList_5.add(timeRangeObj);
				autoSelectedObj.setTimeRange(timeRangeObj);
			}
			
			/** element <radio>.<profile>.<scan>.<access>.<client> */
			if(childObj instanceof ScanAccessClient){
				ScanAccessClient scanClientObj = (ScanAccessClient)childObj;
				
				/** element <radio>.<profile>.<scan>.<access>.<client>.<cr> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/scan/access/access/client", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				scanClientObj.setCr(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isEnableScanAccessClient(index)));
				
				/** element <radio>.<profile>.<scan>.<access>.<client>.<power-save> */
				if(radioProfileImpl.isEnableScanAccessClient(index)){
					
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/scan/access/access/client", 
							"power-save", GenerateXMLDebug.SET_OPERATION,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					scanClientObj.setPowerSave(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isEnablePowerSave(index)));
				}
			}
			
			/** element: <radio>.<profile>.<acsp>.<access>.<interference-switch> */
			if(childObj instanceof AcspInterferenceSwitch){
				AcspInterferenceSwitch interSwitchObj = (AcspInterferenceSwitch)childObj;
				
				if(!radioProfileImpl.isConfigInterSwitchDisable(index)){
					
//					/** element: <radio>.<profile>.<acsp>.<access>.<interference-switch>.<cu-threshold> */
//					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/access/interference-switch", 
//							"cu-threshold", GenerateXMLDebug.SET_VALUE,
//							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
//					Object[][] cuThrParm = {
//							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getSwitchCuThreshold(index)},
//							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
//					};
//					interSwitchObj.setCuThreshold(
//							(InterferenceSwitchCuThreshold)CLICommonFunc.createObjectWithName(InterferenceSwitchCuThreshold.class, cuThrParm)
//					);
					
					/** element: <radio>.<profile>.<acsp>.<access>.<interference-switch>.<iu-threshold> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/access/interference-switch", 
							"iu-threshold", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] iuThrParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getSwitchIuThreshold(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					interSwitchObj.setIuThreshold(
							(InterferenceSwitchIuThreshold)CLICommonFunc.createObjectWithName(InterferenceSwitchIuThreshold.class, iuThrParm)
					);
					
					/** element: <radio>.<profile>.<acsp>.<access>.<interference-switch>.<crc-err-threshold> */
					oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/access/interference-switch", 
							"crc-err-threshold", GenerateXMLDebug.SET_VALUE,
							radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
					Object[][] crcErrParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getSwitchCrcErrThreshold(index)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					interSwitchObj.setCrcErrThreshold(
							(InterferenceSwitchCrcErrThreshold)CLICommonFunc.createObjectWithName(InterferenceSwitchCrcErrThreshold.class, crcErrParm)
					);
					
				}
				
				
				/** element: <radio>.<profile>.<acsp>.<access>.<interference-switch>.<enable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/access/interference-switch", 
						"enable", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigInterSwitchEnable(index)){
					interSwitchObj.setEnable(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <radio>.<profile>.<acsp>.<access>.<interference-switch>.<disable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/access/interference-switch", 
						"disable", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigInterSwitchDisable(index)){
					interSwitchObj.setDisable(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <radio>.<profile>.<acsp>.<access>.<interference-switch>.<no-station-enable> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/access/interference-switch", 
						"no-station-enable", GenerateXMLDebug.CONFIG_ELEMENT,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				if(radioProfileImpl.isConfigInterSwitchNoStation(index)){
					interSwitchObj.setNoStationEnable(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
			}
			
			if(childObj instanceof ConnectionAlarmingChannelUtilizationThreshold){
				ConnectionAlarmingChannelUtilizationThreshold threshold = (ConnectionAlarmingChannelUtilizationThreshold)childObj;
				threshold.setValue(radioProfileImpl.getChannelUtilizatioThreshold());
				//threshold.setTimeInterval(radioProfileImpl.getChannelUtilizatioInterval());
				threshold.setOperation(AhEnumAct.YES);
			}
			
		}
		
		radioChildList_4.clear();
		generateRadioLevel_6(index);
	}
	
	private void generateRadioLevel_6(int index) throws Exception{
		/**
		 * <radio>.<profile>.<acsp>.<max-tx-power>.<access>.<channel-auto-select>.<time_range>		AcspAccess.ChannelAutoSelect.TimeRange
		 */
		for(Object childObj : radioChildList_5){
			
			/** element: <radio>.<profile>.<acsp>.<max-tx-power>.<access>.<channel-auto-select>.<time_range> */
			if(childObj instanceof AcspAccess.ChannelAutoSelect.TimeRange){
				AcspAccess.ChannelAutoSelect.TimeRange timeRangeObj = (AcspAccess.ChannelAutoSelect.TimeRange)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/max-tx-power/access/channel-auto-select", 
						"time_range", GenerateXMLDebug.SET_VALUE,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				timeRangeObj.setValue(
						radioProfileImpl.getChannelAutoSelectTimeRange(index)
				);
				
				/** attribute: quoteProhibited */
				timeRangeObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element <radio>.<profile>.<acsp>.<max-tx-power>.<access>.<channel-auto-select>.<time_range>.<station>*/
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/acsp/max-tx-power/access/channel-auto-select/time_range", 
						"station", GenerateXMLDebug.SET_VALUE,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
				Object[][] stationParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getChannelAutoSelectStation(index)}
				};
				timeRangeObj.setStation(
						(AcspAccess.ChannelAutoSelect.TimeRange.Station)CLICommonFunc.createObjectWithName(
								AcspAccess.ChannelAutoSelect.TimeRange.Station.class, stationParm)
				);
			}
		}
		radioChildList_5.clear();
	}
	
	private ChannelModelDetail createChannelModelDetail(int index) throws CreateXMLException{
		ChannelModelDetail modelDetailObj = new ChannelModelDetail();
		
		/** attribute: operation */
		modelDetailObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <cr> */
		if(radioProfileImpl.isConfigChannelCr(index)){
			modelDetailObj.setCr(CLICommonFunc.createAhStringObj(radioProfileImpl.getChannelCr(index)));
		}
		
		return modelDetailObj;
	}
	
	private WmmAcType createWmmAcType(WmmType wmmType, int index) throws Exception{
		WmmAcType acTypeObj = new WmmAcType();
		
		/** element: <aifs> */
		oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/wmm/ac/"+wmmType.name(), 
				"aifs", GenerateXMLDebug.SET_VALUE,
				radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
		Object[][] aifsParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getAifsValue(index, wmmType)},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		acTypeObj.setAifs(
				(WmmAcType.Aifs)CLICommonFunc.createObjectWithName(WmmAcType.Aifs.class, aifsParm)
		);
		
		/** element: <cwmax> */
		oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/wmm/ac/"+wmmType.name(), 
				"cwmax", GenerateXMLDebug.SET_VALUE,
				radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
		Object[][] cwmaxParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getCwmaxValue(index, wmmType)},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		acTypeObj.setCwmax(
				(WmmAcType.Cwmax)CLICommonFunc.createObjectWithName(WmmAcType.Cwmax.class, cwmaxParm)
		);
	
		/** element: <cwmin> */
		oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/wmm/ac/"+wmmType.name(), 
				"cwmin", GenerateXMLDebug.SET_VALUE,
				radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
		Object[][] cwmixParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getCwminValue(index, wmmType)},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		acTypeObj.setCwmin(
				(WmmAcType.Cwmin)CLICommonFunc.createObjectWithName(WmmAcType.Cwmin.class, cwmixParm)
		);
		
		/** element: <txoplimit> */
		oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/wmm/ac/"+wmmType.name(), 
				"txoplimit", GenerateXMLDebug.SET_VALUE,
				radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
		Object[][] txLimitParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getTxoplimitValue(index, wmmType)},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		acTypeObj.setTxoplimit(
				(WmmAcType.Txoplimit)CLICommonFunc.createObjectWithName(WmmAcType.Txoplimit.class, txLimitParm)
		);
		
		/** element: <noack> */
		oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(index)+"']/wmm/ac/"+wmmType.name(), 
				"noack", GenerateXMLDebug.SET_OPERATION,
				radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(index));
		acTypeObj.setNoack(CLICommonFunc.getAhOnlyAct(radioProfileImpl.isNoack(index, wmmType)));
		
		return acTypeObj;
	}
	
	private PhymodeRate createPhymodeRate(ClientPhyMode type, int index) throws Exception{
		PhymodeRate rateObj = new PhymodeRate();
		
		for(int i=0; i<radioProfileImpl.getRateSize(index); i++){
			RateHealthPercent rate = this.createRateHealthPercent(type, index, i);
			if(rate != null){
				rateObj.getRate().add(rate);
			}
		}
		
		return rateObj;
	}
	
	private RateHealthPercent createRateHealthPercent(ClientPhyMode type, int indexRad, int indexRate) throws Exception{
		RateHealthPercent rateObj = new RateHealthPercent();
		
		/** attribute: name */
		oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(indexRad)+"']/benchmark/phymode/"+type.name(), 
				"rate", GenerateXMLDebug.SET_NAME,
				radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(indexRad));
		String rateName = radioProfileImpl.getRateName(type, indexRad, indexRate);
		if(rateName != null){
			rateObj.setName(rateName);
		}else{
			return null;
		}
		
		/** attribute: operation */
		rateObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <radio>.<profile>.<benchmark>.<phymode>.<_11a|_11b|_11g|_11n|_11ac>.<rate>.<success> */
		RateHealthPercent.Success successObj = new RateHealthPercent.Success();
		radioRateList_1.add(successObj);
		rateObj.setSuccess(successObj);
		
		generateRateHealthPercentLevel_1(type, indexRad, indexRate);
		
		return rateObj;
	}
	
	private void generateRateHealthPercentLevel_1(ClientPhyMode type, int indexRad, int indexRate) throws Exception{
		/**
		 * <radio>.<profile>.<benchmark>.<phymode>.<_11a|_11b|_11g|_11n|_11ac>.<rate>.<success>		RateHealthPercent.Success
		 */
		for(Object childObj : radioRateList_1){
			
			/** element: <radio>.<profile>.<benchmark>.<phymode>.<_11a|_11b|_11g|_11n|_11ac>.<rate>.<success> */
			if(childObj instanceof RateHealthPercent.Success){
				RateHealthPercent.Success successObj = (RateHealthPercent.Success)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(indexRad)+"']/benchmark/phymode/"+type.name()+"/rate", 
						"success", GenerateXMLDebug.SET_VALUE,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(indexRad));
				successObj.setValue(radioProfileImpl.getSuccessValue(type, indexRad, indexRate));
				
				/** element: <radio>.<profile>.<benchmark>.<phymode>.<_11a|_11b|_11g|_11n|_11ac>.<rate>.<success>.<usage> */
				oDebug.debug("/configuration/radio/profile[@name='"+radioProfileImpl.getName(indexRad)+"']/benchmark/phymode/"+type.name()+"/rate/success", 
						"usage", GenerateXMLDebug.SET_VALUE,
						radioProfileImpl.getRadioProfileGuiName(), radioProfileImpl.getName(indexRad));
				Object[][] usageParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, radioProfileImpl.getUsageValue(type, indexRad, indexRate)}
				};
				successObj.setUsage(
						(RateHealthPercent.Success.Usage)CLICommonFunc.createObjectWithName(RateHealthPercent.Success.Usage.class, usageParm)
				);
			}
		}
		radioRateList_1.clear();
	}
}
