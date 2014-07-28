package com.ah.ui.actions.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBePerformUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.event.BeSpectralAnalysisEvent;
import com.ah.be.communication.mo.SpectralAnalysisData;
import com.ah.be.communication.mo.SpectralAnalysisDataSample;
import com.ah.be.communication.mo.SpectralAnalysisInterference;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.performance.BeSpectralAnalysisProcessor;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhCustomReport;
import com.ah.bo.wlan.RadioProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.hiveap.HiveApAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.devices.impl.Device;

@SuppressWarnings("serial")
public class SpectralAnalysisAction extends BaseAction {

	private static final Tracer log = new Tracer(
			SpectralAnalysisAction.class.getSimpleName());

	public String execute() throws Exception {
		try {
			if ("fft".equals(operation)) {
//				log.info("Fetch FFT data, sample ID: "
//						+ dashboardStatus.sampleId);
				List<SpectralAnalysisData> fftDatas=new ArrayList<SpectralAnalysisData>();
				List<SpectralAnalysisData> maxHoldDatas= new ArrayList<SpectralAnalysisData>();
				long lastTime=0;
				try {
					List<SimpleHiveAp> simpAps = CacheMgmt.getInstance().getManagedApList();
					for(SimpleHiveAp oneAp: simpAps){
						if (dashboardStatus.apId.equals(oneAp.getId())) {
							Map<Short, SpectralAnalysisData> fftMap = HmBePerformUtil.fetchFFTData(oneAp);

							if (fftMap!=null) {
								for(Short key : fftMap.keySet()){
									if (key==0) {
										if (fftMap.get(key)!=null) {
											lastTime= fftMap.get(key).getTimeStamp();
										}
									} else {
										fftDatas.add(fftMap.get(key));
									}
								}
							}
							if (dashboardStatus.fftSample || dashboardStatus.dutySample) {
								Map<Short, SpectralAnalysisData> mapV = HmBePerformUtil.fetchMaxHoldData(oneAp);
								if (mapV!=null) {
									for(Short key : mapV.keySet()){
										maxHoldDatas.add(mapV.get(key));
									}
								}
							}
							break;
						}
					}
				} catch (Exception e) {
					log.error("execute fft", e);
				}
				// test
				jsonObject = new JSONObject();
				// if aid < 0, this will stop the client polling
				jsonObject.put("aid", dashboardStatus.aid);
				jsonObject.put("m", AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().getRunningApInfoRemainTime(dashboardStatus.apId));

				if (fftDatas==null || fftDatas.isEmpty()) {
					return "json";
				}
				if (dashboardStatus.aid > 0) {
					if (dashboardStatus.sampleId < 0) {
						// record ID of first sample
						dashboardStatus.sampleId = lastTime;
					} else {
						// Is a more recent sample available
						if (newFFTDataAvailable(dashboardStatus.sampleId,lastTime)) {
							dashboardStatus.sampleId=lastTime;
						} else {
							// No new samples available, don't return any data,
							// Wait for next client request
							return "json";
						}
					}
					if (dashboardStatus.fftSample) {
						JSONArray retArray = new JSONArray();
						JSONObject maxOb = fetchFFTData(maxHoldDatas);
						if (maxOb!=null) {
							retArray.put(maxOb);
						}
						JSONObject fftOb = fetchFFTData(fftDatas);
						if (fftOb!=null) {
							retArray.put(fftOb);
						}
						jsonObject.put("t0", retArray);
					} else {
						JSONArray retArray = new JSONArray();
						JSONObject fftOb = fetchFFTData(fftDatas);
						if (fftOb!=null) {
							retArray.put(fftOb);
						}
						jsonObject.put("t0", retArray);
					}
					if (dashboardStatus.dutySample) {
						JSONArray retArray = new JSONArray();
						JSONObject maxOb = fetchDutyData(maxHoldDatas);
						if (maxOb!=null) {
							retArray.put(maxOb);
						}
						JSONObject dutyOb = fetchDutyData(fftDatas);
						if (dutyOb!=null) {
							retArray.put(dutyOb);
						}
						jsonObject.put("t1", retArray);
					} else {
						JSONArray retArray = new JSONArray();
						JSONObject dutyOb = fetchDutyData(fftDatas);
						if (dutyOb!=null) {
							retArray.put(dutyOb);
						}
						jsonObject.put("t1", retArray);
					}
					//jsonObject.put("t1", fetchDutyData(fftDatas));
					jsonObject.put("t2", true);
					jsonObject.put("t3", true);
				}
				return "json";
			} else if ("sweptFft".equals(operation)) {
				calculateSweptFft();
				return null;
			} else if ("sweptDuty".equals(operation)) {
				calculateSweptDuty();
				return null;
			} else if ("updateFFTBand".equals(operation)) {
				updateDashboardParam(getFftBand());
				jsonObject.put("channels", getFftChannels());
				jsonObject.put("center", getFftCenter());
				jsonObject.put("span", getFftSpan());
				return "json";
			} else if ("updateFFTChannels".equals(operation)) {
				return updateDashboardParam(getFftChannels());
			} else if ("updateFFTCenter".equals(operation)) {
				return updateDashboardParam(getFftCenter());
			} else if ("updateFFTSpan".equals(operation)) {
				return updateDashboardParam(getFftSpan());
			} else if ("updateFFTRefLevel".equals(operation)) {
				return updateDashboardParam(getFftRefLevel());
			} else if ("updateFFTVertScale".equals(operation)) {
				return updateDashboardParam(getFftVertScale());
			} else if ("updateFFTSample".equals(operation)) {
				return updateDashboardParam(getFftSample());
			} else if ("updateDutySample".equals(operation)) {
				return updateDashboardParam(getDutySample());
			} else if ("updateDutyBand".equals(operation)) {
				updateDashboardParam(getDutyBand());
				jsonObject.put("channels", getDutyChannels());
				jsonObject.put("center", getDutyCenter());
				jsonObject.put("span", getDutySpan());
				return "json";
			} else if ("updateDutyChannels".equals(operation)) {
				return updateDashboardParam(getDutyChannels());
			} else if ("updateDutyCenter".equals(operation)) {
				return updateDashboardParam(getDutyCenter());
			} else if ("updateDutySpan".equals(operation)) {
				return updateDashboardParam(getDutySpan());
			} else if ("updateDutyMin".equals(operation)) {
				return updateDashboardParam(getDutyMin());
			} else if ("updateDutyMax".equals(operation)) {
				return updateDashboardParam(getDutyMax());
			} else if ("updateSweptFFTBand".equals(operation)) {
				updateDashboardParam(getSweptFftBand());
				jsonObject.put("channels", getSweptFftChannels());
				jsonObject.put("center", getSweptFftCenter());
				jsonObject.put("span", getSweptFftSpan());
				return "json";
			} else if ("updateSweptFFTChannels".equals(operation)) {
				return updateDashboardParam(getSweptFftChannels());
			} else if ("updateSweptFFTCenter".equals(operation)) {
				return updateDashboardParam(getSweptFftCenter());
			} else if ("updateSweptFFTSpan".equals(operation)) {
				return updateDashboardParam(getSweptFftSpan());
			} else if ("updateSweptDutyBand".equals(operation)) {
				updateDashboardParam(getSweptDutyBand());
				jsonObject.put("channels", getSweptDutyChannels());
				jsonObject.put("center", getSweptDutyCenter());
				jsonObject.put("span", getSweptDutySpan());
				return "json";
			} else if ("updateSweptDutyChannels".equals(operation)) {
				return updateDashboardParam(getSweptDutyChannels());
			} else if ("updateSweptDutyCenter".equals(operation)) {
				return updateDashboardParam(getSweptDutyCenter());
			} else if ("updateSweptDutySpan".equals(operation)) {
				return updateDashboardParam(getSweptDutySpan());
			} else if ("updateInterferenceData".equals(operation)){
				jsonObject = new JSONObject();
				interferenceLst.clear();
				try {
					List<SimpleHiveAp> simpAps = CacheMgmt.getInstance().getManagedApList();
					for(SimpleHiveAp oneAp: simpAps){
						if (currentApId.equals(oneAp.getId())) {
							interferenceLst = HmBePerformUtil.fetchInterference(oneAp);
							break;
						}
					}
				} catch (Exception e) {
					log.error("updateInterferenceData", e);
				}
				StringBuffer strBuf = new StringBuffer();
				strBuf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
				if (getInterferenceLst().size()==0) {
					strBuf.append("<tr><td colspan=\"100\" style=\"padding: 2px 0 0 5px;\">");
					strBuf.append(getText("info.emptyList"));
					strBuf.append("</td></tr>");
				} else {
					strBuf.append("<tr>");
					strBuf.append("<td>");
					strBuf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
					int i=0;
					for (SpectralAnalysisInterference oneRec: interferenceLst){
						if (i%2==0) {
							strBuf.append("<tr class='even'>");
						} else {
							strBuf.append("<tr class='odd'>");
						}
						strBuf.append("<td class=\"list\" width=\"100px\">").append(oneRec.getApName()).append("</td>");
						strBuf.append("<td class=\"list\" width=\"120px\">").append(oneRec.getDeviceTypeString()).append("</td>");
						strBuf.append("<td class=\"list\" width=\"120px\">").append(oneRec.getTimeString()).append("</td>");
						strBuf.append("<td class=\"list\" width=\"110px\">").append(oneRec.getChannel()).append("</td>");
						strBuf.append("<td class=\"list\" width=\"110px\">").append(oneRec.getCenterFreq()).append("</td>");
						strBuf.append("<td class=\"list\" width=\"110px\">").append(oneRec.getBandwidthValue()).append("</td>");
						strBuf.append("</tr>");
						i++;
					}
					strBuf.append("</table>");
					strBuf.append("</td>");
					strBuf.append("</tr>");
				}
				strBuf.append("</table>");
				jsonObject.put("s",strBuf.toString());
				return "json";
			} else if ("updateSettingsParams".equals(operation)) {
				return updateParamSettings();
			} else if ("updateSettingsParamsFromAp".equals(operation)) {
				if (AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor()
						.isRunningAp(runAP)) {
					return updateParamSettings();
				} else {
					if (!isHMOnline()) {
						if (AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().getRunningAp()
								.size() >= 10) {
							jsonObject = new JSONObject();
							jsonObject.put("e", MgrUtil.getUserMessage("error.spn.max.support.app"));
							jsonObject.put("s",false);
							return "json";
						}
					} else {
						Set<SimpleHiveAp> runSetAp = AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().getRunningAp();
						int runCount =0;
						for (SimpleHiveAp oneRunAp: runSetAp) {
							if (getDomainId().equals(oneRunAp.getDomainId())) {
								runCount++;
							}
						}
						if (runCount >= 2) {
							jsonObject = new JSONObject();
							jsonObject.put("e",MgrUtil.getUserMessage("error.spn.max.support.vhm"));
							jsonObject.put("s",false);
							return "json";
						}

						int maxApCountSa = 20;
						List<?> list = QueryUtil.executeQuery("select snpMaximum from " + HMServicesSettings.class.getSimpleName(),
								null, new FilterParams("owner.domainName", HmDomain.HOME_DOMAIN), 1);
						if (!list.isEmpty()) {
							maxApCountSa = Integer.parseInt(list.get(0).toString());
						}

						if (runSetAp.size()>=maxApCountSa){
							jsonObject = new JSONObject();
							jsonObject.put("e",MgrUtil.getUserMessage("error.spn.max.support.hmol",String.valueOf(maxApCountSa)));
							jsonObject.put("s",false);
							return "json";
						}
					}
				}
				return updateParamSettings();
//			} else if ("start".equals(operation)) {
//				if (!isHMOnline()) {
//					if (BeSpectralAnalysisProcessor.getInstance().getRunningAp()
//							.size() >= 10) {
//						parpareHiveAPLst();
//						prepareInterfaceLst(runAP);
//						return "analysis";
//					}
//				} else {
//					Set<SimpleHiveAp> runSetAp = BeSpectralAnalysisProcessor.getInstance().getRunningAp();
//					int runCount =0;
//					for (SimpleHiveAp oneRunAp: runSetAp) {
//						if (getDomainId().equals(oneRunAp.getDomainId())) {
//							runCount++;
//						}
//					}
//					if (runCount >= 2 || runSetAp.size()>=20) {
//						parpareHiveAPLst();
//						prepareInterfaceLst(runAP);
//						return "analysis";
//					}
//				}
//				if (BeSpectralAnalysisProcessor.getInstance()
//						.isRunningAp(runAP)) {
//					parpareHiveAPLst();
//					prepareInterfaceLst(runAP);
//					return "analysis";
//				}
//				try {
//					for (SimpleHiveAp simpleAP : CacheMgmt.getInstance()
//							.getManagedApList()) {
//						if (simpleAP.getId().equals(runAP)) {
//
//							byte ret = HmBePerformUtil.startSpectralAnalysis(
//									simpleAP, (byte) runInterface,
//									runChannelWifi0, runChannelWifi1,
//									(short) runInterval, runTime * 60);
//							resetDashboardStatus(runAP);
//							if (ret != BeSpectralAnalysisProcessor.STATUS_SUCCESS) {
//								addActionError(getResultErrorMessage(ret));
//							}
//							break;
//						}
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				parpareHiveAPLst();
//				prepareInterfaceLst(runAP);
//				return "analysis";
			} else if ("stop".equals(operation)) {
				if (AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().isRunningAp(
						currentApId)) {
					try {
						for (SimpleHiveAp simpleAP : CacheMgmt.getInstance()
								.getManagedApList()) {
							if (simpleAP.getId().equals(currentApId)) {
								int ret = HmBePerformUtil
										.stopSpectralAnalysis(simpleAP);
								if (ret != BeSpectralAnalysisProcessor.STATUS_SUCCESS) {
									addActionError(getResultErrorMessage(ret));
								}
								break;
							}
						}
					} catch (Exception e) {
						log.error("stop", e);
					}
				}
				return "analysis";
			} else if ("view".equals(operation)) {
				// need save runAP, so the gui can get the json information
				 if (!AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().isRunningAp(id)) {
					 return "analysis";
				 }
				log.info_ln("Analyze AP: " + id);
				dashboardStatus = initDashboardStatus(id);

				short band= AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().getRunningApInterfaceInfo(id);
				if (dashboardStatus.fftBand==2400 &&
						dashboardStatus.dutyBand==2400 &&
						dashboardStatus.dutyBand==2400 &&
						dashboardStatus.dutyBand==2400 &&
						band!=2400){
					setFftBand(band);
					setDutyBand(band);
					setSweptDutyBand(band);
					setSweptFftBand(band);
				}

				prepareInterfaceLst(id);
				interferenceLst.clear();
				try {
					List<SimpleHiveAp> simpAps = CacheMgmt.getInstance().getManagedApList();
					for(SimpleHiveAp oneAp: simpAps){
						if (id.equals(oneAp.getId())) {
							interferenceLst = HmBePerformUtil.fetchInterference(oneAp);
							break;
						}
					}
				} catch (Exception e) {
					log.error("view", e);
				}

				return "analyzer";
			} else if ("fetchApInterface".equals(operation)) {
				jsonObject = new JSONObject();
				HiveAp ap = findBoById(HiveAp.class, runAP);
				if (ap != null) {
					if (AhConstantUtil.isTrueAll(Device.IS_DUALBAND, ap.getHiveApModel())) {
						if (ap.getWifi0() != null
								&& ap.getWifi0().getAdminState() == AhInterface.ADMIN_STATE_UP) {
								if (ap.getWifi0().getRadioMode()==RadioProfile.RADIO_PROFILE_MODE_BG ||
										ap.getWifi0().getRadioMode()==RadioProfile.RADIO_PROFILE_MODE_NG){
									jsonObject.put("v", 1);
								} else {
									jsonObject.put("v", 2);
								}
						} else {
							jsonObject.put("v", 0);
						}
					} else {
						if (ap.getWifi0() != null
								&& ap.getWifi0().getAdminState() == AhInterface.ADMIN_STATE_UP
								&& ap.getWifi1() != null
								&& ap.getWifi1().getAdminState() == AhInterface.ADMIN_STATE_UP) {
							jsonObject.put("v", 3);
						} else if (ap.getWifi0() != null
								&& ap.getWifi0().getAdminState() == AhInterface.ADMIN_STATE_UP) {
							jsonObject.put("v", 1);
						} else if (ap.getWifi1() != null
								&& ap.getWifi1().getAdminState() == AhInterface.ADMIN_STATE_UP) {
							jsonObject.put("v", 2);
						} else {
							jsonObject.put("v", 0);
						}
					}
				} else {
					jsonObject.put("v", 0);
				}
				return "json";
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			log.error("execute", e);
		}
		return "analysis";
	}

	public void prepare() throws Exception {
		super.prepare();
		//setSelectedL2Feature(L2_FEATURE_SPECTRAL_ANALYSIS);// by default
		//setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
		String listTypeFromSession = (String) MgrUtil
				.getSessionAttribute(HiveApAction.HM_LIST_TYPE);
		if("managedVPNGateways".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_VPN_GATEWAYS);
		}else if( "managedRouters".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_BRANCH_ROUTERS);
		}else if( "managedSwitches".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_SWITCHES);
		}else if("managedDeviceAPs".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_DEVICE_HIVEAPS);
		}else if("managedHiveAps".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
		}else{
			setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
		}
		this.boClass = HiveAp.class;
	}

	protected JSONObject jsonObject = null;

	public String getJSONString() {
		if (jsonObject != null) {
			return jsonObject.toString();
		} else {
			return "{}";
		}
	}

	public String updateParamSettings() throws Exception {
		jsonObject = new JSONObject();
		try {
			for (SimpleHiveAp simpleAP : CacheMgmt.getInstance()
					.getManagedApList()) {
				if (simpleAP.getId().equals(runAP)) {

					int ret = HmBePerformUtil.startSpectralAnalysis(
							simpleAP, (byte) runInterface,
							runChannelWifi0, runChannelWifi1,
							(short) runInterval, runTime * 60);
					if (ret != BeSpectralAnalysisProcessor.STATUS_SUCCESS) {
						jsonObject.put("e",getResultErrorMessage(ret));
						jsonObject.put("s",false);
						return "json";
					} else {
						resetDashboardStatus(runAP);
						jsonObject.put("s",true);
						return "json";
					}
				}
			}
		} catch (Exception e) {
			log.error("Spectrum analysis updateParamSettings error", e);
		}
		jsonObject.put("s",false);
		return "json";
	}

	/*
	 * Check if more recent FFT data is available
	 */
	private boolean newFFTDataAvailable(long sampleId, long currentTime) throws Exception {
		if (currentTime!=sampleId) {
			return true;
		}
		return false;
	}

	// type 0:2400-2500, 1:5150-5350, 2:5470-5725, 3:5725-5850
	private  short[] getOnlineFFTData(int type, SpectralAnalysisDataSample datas){
		short[] oneRecData = null;

		if (type==0) {
			oneRecData= datas.getPwrRsp24g();
		} else if (type==1) {
			oneRecData= datas.getPwrRsp5g1();

		} else if (type==2){
			oneRecData= datas.getPwrRsp5g2();
		} else {
			oneRecData= datas.getPwrRsp5g3();
		}
		return oneRecData;
	}

	private short[] getOnlineDutyData(int type, SpectralAnalysisDataSample datas){
		short[] oneRecData = null;
		if (type==0) {
			oneRecData= datas.getDutyCycle24g();
		} else if (type==1) {
			oneRecData= datas.getDutyCycle5g1();
		} else if (type==2){
			oneRecData= datas.getDutyCycle5g2();
		} else {
			oneRecData= datas.getDutyCycle5g3();
		}
		return oneRecData;
	}

	private JSONObject fetchFFTData(List<SpectralAnalysisData>  datas) throws Exception {
		int startChannel = 2402;
		int endChannel = 2482;
		if (dashboardStatus.fftBand == 5150) {
			startChannel = 5160;
			endChannel = 5340;
		} else if (dashboardStatus.fftBand == 5470) {
			startChannel = 5470;
			endChannel = 5725;
		} else if (dashboardStatus.fftBand == 5725) {
			startChannel = 5725;
			endChannel = 5845;
		}

		int v128Count=0;
		int v56Count=0;

		for(SpectralAnalysisData oneRec : datas){
			if (oneRec.getChnFreq()>=startChannel && oneRec.getChnFreq()<=endChannel){
				if (oneRec.getPwrRspLen()==BeSpectralAnalysisEvent.SAMPLE_COUNT_128) {
					v128Count++;
				} else if (oneRec.getPwrRspLen()==BeSpectralAnalysisEvent.SAMPLE_COUNT_56) {
					v56Count++;
				}
			}
		}

		int rec[]=null;
		byte channelWidth;
		short pwrRspLen;
		if (v128Count>0 && v56Count>0) {
			channelWidth = BeSpectralAnalysisEvent.CHANNEL_WIDTH_20;
			pwrRspLen=BeSpectralAnalysisEvent.SAMPLE_COUNT_56;
		} else {
			channelWidth = datas.get(0).getChnWidth();
			pwrRspLen = datas.get(0).getPwrRspLen();
		}
		//short pwrRspLen = datas.get(0).getPwrRspLen();
		int stepWidth = 1000;
		int stepCount=5;
		if (channelWidth==BeSpectralAnalysisEvent.CHANNEL_WIDTH_20) {
			if (pwrRspLen == BeSpectralAnalysisEvent.SAMPLE_COUNT_56){
				stepWidth = 357;
				stepCount = 14;
				rec = new int[(endChannel-startChannel)/5*stepCount + 1];
			} else if (pwrRspLen == BeSpectralAnalysisEvent.SAMPLE_COUNT_128){
				stepWidth = 156;
				stepCount = 32;
				rec = new int[(endChannel-startChannel)/5*stepCount + 1];
			}
		} else if (channelWidth==BeSpectralAnalysisEvent.CHANNEL_WIDTH_40){
			if (pwrRspLen == BeSpectralAnalysisEvent.SAMPLE_COUNT_56){
				stepWidth = 714;
				stepCount = 7;
				rec = new int[(endChannel-startChannel)/5*stepCount + 1];
			} else if (pwrRspLen == BeSpectralAnalysisEvent.SAMPLE_COUNT_128){
				stepWidth = 312;
				stepCount = 16;
				rec = new int[(endChannel-startChannel)/5*stepCount + 1];
			}
		}
		if (rec==null) return null;

		for(int i=0; i<rec.length; i++) {
			rec[i]=-1;
		}
		for(SpectralAnalysisData oneRec : datas){
			if (oneRec.getChnFreq()>=startChannel && oneRec.getChnFreq()<=endChannel){
				if (v128Count>0 && v56Count>0 && (oneRec.getChnWidth()!=channelWidth || oneRec.getPwrRspLen() !=pwrRspLen)){
					short oneFre = oneRec.getChnFreq();
					short values[] = oneRec.getPwrRsp();
					int addIndex = 0;
					while ((oneFre+addIndex-(channelWidth+2)*10)- startChannel<0) {
						addIndex = addIndex+5;
					}
					int startValueIndex;
					int startRecIndex;
					startValueIndex= 8 + addIndex/5*stepCount;
					startRecIndex = ((oneFre-(channelWidth+2)*10)- startChannel)/5 * stepCount;
					if (startRecIndex<0) {
						startRecIndex=0;
					}

					while (startValueIndex<values.length-8 && startRecIndex<rec.length) {
						if (values[startValueIndex]>=0) {
							if (rec[startRecIndex]==-1) {
								rec[startRecIndex] = values[startValueIndex];
							} else {
								rec[startRecIndex] = values[startValueIndex]<rec[startRecIndex]?values[startValueIndex]:rec[startRecIndex];
							}
						}
						startValueIndex++;
						startRecIndex++;
					}
				} else {
//					if ((oneRec.getPwrRspLen()!=BeSpectralAnalysisEvent.SAMPLE_COUNT_56 &&
//							oneRec.getPwrRspLen()!=BeSpectralAnalysisEvent.SAMPLE_COUNT_128) ||
//							oneRec.getPwrRspLen() !=pwrRspLen ||
//							oneRec.getChnWidth()!=channelWidth){
//						continue;
//					}
					short oneFre = oneRec.getChnFreq();
					short values[] = oneRec.getPwrRsp();
					int addIndex = 0;
					while ((oneFre+addIndex-(channelWidth+1)*10)- startChannel<0) {
						addIndex = addIndex+5;
					}
					int startValueIndex= addIndex/5*stepCount;
					int startRecIndex = ((oneFre-(channelWidth+1)*10)- startChannel)/5 * stepCount;
					if (startRecIndex<0) {
						startRecIndex=0;
					}

					while (startValueIndex<values.length && startRecIndex<rec.length) {
						if (values[startValueIndex]>=0) {
							if (rec[startRecIndex]==-1) {
								rec[startRecIndex] = values[startValueIndex];
							} else {
								rec[startRecIndex] = values[startValueIndex]<rec[startRecIndex]?values[startValueIndex]:rec[startRecIndex];
							}
						}
						startValueIndex++;
						startRecIndex++;
					}
				}
			}
		}
//		System.out.print("rec===[");
//		for(int aa :rec){
//			System.out.print(aa + ",");
//		}
//		System.out.println("]");
		JSONArray x = new JSONArray();
		JSONArray y = new JSONArray();
		int recIndex=0;
		DecimalFormat formater = new DecimalFormat("#0.####");
		for (int channel = startChannel; channel <= endChannel;) {
			for (int i = 0; i < stepCount && channel <= endChannel && recIndex<rec.length; i++) {
				x.put(formater.format(channel + ((double)i* stepWidth)/1000));
				double signal = rec[recIndex++]*-1;
				if (signal < -100) {
					signal = -100;
				}
				y.put(Math.round(signal));

			}
			channel = channel+5;
		}
		JSONObject xy_set = new JSONObject();
		xy_set.put("x", x);
		xy_set.put("y", y);
		return xy_set;
//		return new JSONArray().put(xy_set);
	}


	private JSONObject fetchDutyData(List<SpectralAnalysisData>  datas) throws Exception {
		int startChannel = 2402;
		int endChannel = 2482;
		if (dashboardStatus.dutyBand == 5150) {
			startChannel = 5160;
			endChannel = 5340;
		} else if (dashboardStatus.dutyBand == 5470) {
			startChannel = 5470;
			endChannel = 5725;
		} else if (dashboardStatus.dutyBand == 5725) {
			startChannel = 5725;
			endChannel = 5845;
		}

		int v128Count=0;
		int v56Count=0;

		for(SpectralAnalysisData oneRec : datas){
			if (oneRec.getChnFreq()>=startChannel && oneRec.getChnFreq()<=endChannel){
				if (oneRec.getDutyCycleLen()==BeSpectralAnalysisEvent.SAMPLE_COUNT_128) {
					v128Count++;
				} else if (oneRec.getDutyCycleLen()==BeSpectralAnalysisEvent.SAMPLE_COUNT_56) {
					v56Count++;
				}
			}
		}

		int rec[]=null;
		byte channelWidth;
		short dutyLen;
		if (v128Count>0 && v56Count>0) {
			channelWidth = BeSpectralAnalysisEvent.CHANNEL_WIDTH_20;
			dutyLen=BeSpectralAnalysisEvent.SAMPLE_COUNT_56;
		} else {
			channelWidth = datas.get(0).getChnWidth();
			dutyLen = datas.get(0).getDutyCycleLen();
		}
//		int rec[]=null;
//		byte channelWidth = datas.get(0).getChnWidth();
//		short dutyLen = datas.get(0).getDutyCycleLen();
		int stepWidth = 1000;
		int stepCount=5;
		if (channelWidth==BeSpectralAnalysisEvent.CHANNEL_WIDTH_20) {
			if (dutyLen == BeSpectralAnalysisEvent.SAMPLE_COUNT_56){
				stepWidth = 357;
				stepCount = 14;
				rec = new int[(endChannel-startChannel)/5*stepCount + 1];
			} else if (dutyLen == BeSpectralAnalysisEvent.SAMPLE_COUNT_128){
				stepWidth = 156;
				stepCount = 32;
				rec = new int[(endChannel-startChannel)/5*stepCount + 1];
			}
		} else if (channelWidth==BeSpectralAnalysisEvent.CHANNEL_WIDTH_40){
			if (dutyLen == BeSpectralAnalysisEvent.SAMPLE_COUNT_56){
				stepWidth = 714;
				stepCount = 7;
				rec = new int[(endChannel-startChannel)/5*stepCount + 1];
			} else if (dutyLen == BeSpectralAnalysisEvent.SAMPLE_COUNT_128){
				stepWidth = 312;
				stepCount = 16;
				rec = new int[(endChannel-startChannel)/5*stepCount + 1];
			}
		}
		if (rec==null) return null;

		for(int i=0; i<rec.length; i++) {
			rec[i]=-1;
		}
//		for(SpectralAnalysisData oneRec : datas){
//
//				String pwr = "Action -Freq-duty: " + oneRec.getChnFreq();
//				int idx = 0;
//				for (short pwrVal : oneRec.getDutyCycle()) {
//					pwr += ("[" + idx + "]" + pwrVal + " ");
//					idx++;
//				}
//				log.info(pwr);
//
//		}
		for(SpectralAnalysisData oneRec : datas){
			if (oneRec.getChnFreq()>=startChannel && oneRec.getChnFreq()<=endChannel){
				if (v128Count>0 && v56Count>0 && (oneRec.getChnWidth()!=channelWidth || oneRec.getDutyCycleLen() !=dutyLen)){
					short oneFre = oneRec.getChnFreq();
					short values[] = oneRec.getDutyCycle();
					int addIndex = 0;
					while ((oneFre+addIndex-(channelWidth+2)*10)- startChannel<0) {
						addIndex = addIndex+5;
					}
					int startValueIndex;
					int startRecIndex;

					startValueIndex= 8 + addIndex/5*stepCount;
					startRecIndex = ((oneFre-(channelWidth+2)*10)- startChannel)/5 * stepCount;
					if (startRecIndex<0) {
						startRecIndex=0;
					}
					while (startValueIndex<values.length-8 && startRecIndex<rec.length) {
						if (values[startValueIndex]>=0) {
							if (rec[startRecIndex]==-1) {
								rec[startRecIndex] = values[startValueIndex];
							} else {
								rec[startRecIndex] = values[startValueIndex]>rec[startRecIndex]?values[startValueIndex]:rec[startRecIndex];
							}
						}
						startValueIndex++;
						startRecIndex++;
					}
				} else {


//				if ((oneRec.getDutyCycleLen()!=BeSpectralAnalysisEvent.SAMPLE_COUNT_56 &&
//						oneRec.getDutyCycleLen()!=BeSpectralAnalysisEvent.SAMPLE_COUNT_128) ||
//						oneRec.getDutyCycleLen() !=dutyLen ||
//						oneRec.getChnWidth()!=channelWidth){
//					continue;
//				}
					short oneFre = oneRec.getChnFreq();
					short values[] = oneRec.getDutyCycle();
					int addIndex = 0;
					while ((oneFre+addIndex-(channelWidth+1)*10)- startChannel<0) {
						addIndex = addIndex+5;
					}
					int startValueIndex= addIndex/5*stepCount;
					int startRecIndex = ((oneFre-(channelWidth+1)*10)- startChannel)/5 * stepCount;
					if (startRecIndex<0) {
						startRecIndex=0;
					}

					while (startValueIndex<values.length && startRecIndex<rec.length) {
						if (values[startValueIndex]>=0) {
							if (rec[startRecIndex]==-1) {
								rec[startRecIndex] = values[startValueIndex];
							} else {
								rec[startRecIndex] = values[startValueIndex]>rec[startRecIndex]?values[startValueIndex]:rec[startRecIndex];
							}
						}
						startValueIndex++;
						startRecIndex++;
					}
				}
			}
		}
		JSONArray x = new JSONArray();
		JSONArray y = new JSONArray();
		int recIndex=0;
		DecimalFormat formater = new DecimalFormat("#0.####");
		for (int channel = startChannel; channel <= endChannel;) {
			for (int i = 0; i < stepCount && channel <= endChannel && recIndex<rec.length; i++) {
				x.put(formater.format(channel + ((double)i* stepWidth)/1000));
				double busy = rec[recIndex++];
				if (busy > 100) {
					busy = 100;
				}
				y.put(Math.round(busy));

			}
			channel = channel+5;
		}
		JSONObject xy_set = new JSONObject();
		xy_set.put("x", x);
		xy_set.put("y", y);

		return xy_set;
		//return new JSONArray().put(xy_set);
	}

	private void calculateSweptFft() throws Exception {
		SpectralAnalysisDataSample[] datas=null;
		// test
		try {
			List<SimpleHiveAp> simpAps = CacheMgmt.getInstance().getManagedApList();
			for(SimpleHiveAp oneAp: simpAps){
				if (dashboardStatus.apId.equals(oneAp.getId())) {
//					if (dashboardStatus.sweptFftSample==1) {
//						datas = HmBePerformUtil.fetchMaxHoldDatas(oneAp);
//					} else {
						datas = HmBePerformUtil.fetchFFTDatas(oneAp);
//					}

					break;
				}
			}
		} catch (Exception e) {
			log.error("calculateSweptFft", e);
		}

		if (datas==null) {
			return;
		}

//		Date start = new Date();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setStroke(new BasicStroke(1));

		int type=0;
		int startChannel = 2402;
		int endChannel = 2482;

		if (dashboardStatus.sweptFftBand == 5150) {
			startChannel = 5160;
			endChannel = 5340;
			type=1;
		} else if (dashboardStatus.sweptFftBand == 5470) {
			startChannel = 5470;
			endChannel = 5725;
			type=2;
		} else if (dashboardStatus.sweptFftBand == 5725) {
			startChannel = 5725;
			endChannel = 5845;
			type=3;
		}
		int sweptFftIndex = datas.length;

		float scale_x = (float) width / (endChannel - startChannel);
		float scale_y = (float) height / dashboardStatus.sweptFftCount;
		int y2 = height;

		for (int swept = 0; swept < sweptFftIndex; swept++) {
			int x1 = 0;
			int y1 = height - Math.round((swept + 1) * scale_y);
			short[] sample = getOnlineFFTData(type,  datas[sweptFftIndex - 1 - swept]);
			if (sample==null) {
				sample= new short[endChannel-startChannel + 1];
				for(int i=0; i<=endChannel-startChannel; i++) {
					sample[i]=-1;
				}
			}
			for (int channel = 0; channel < sample.length; channel++) {
				int rssi = sample[channel] * -1;
				if (rssi>0) {
					rssi=-100;
				}
				if (rssi > -35) {
					rssi = -35;
				}
				int color = (short) (-35 - rssi);
				if (color >= rssiColors.length) {
					color = (short) (rssiColors.length - 1);
				}
				int x2;
				if (channel==0) {
					x2 = Math.round(scale_x);
				} else {
					x2 = channel == sample.length - 1 ? width : Math
							.round((channel + (float) 0.5) * scale_x);
				}
				g2.setColor(rssiColors[color]);
				if (y2 - y1 > 0) {
					g2.fillRect(x1, y1, x2 - x1, y2 - y1);
				}
				x1 = x2;
			}
			y2 = y1;
		}

//		Date end = new Date();
//		log.info_ln("Swept FFT image(" + width + ", " + height + ") in "
//				+ (end.getTime() - start.getTime()) + " ms.");
		response.setContentType("image/png");
		OutputStream os = response.getOutputStream();
		ImageIO.write(image, "png", os);
		os.close();
	}

	private void calculateSweptDuty() throws Exception {
		SpectralAnalysisDataSample[] datas=null;
		// test
		try {
			List<SimpleHiveAp> simpAps = CacheMgmt.getInstance().getManagedApList();
			for(SimpleHiveAp oneAp: simpAps){
				if (dashboardStatus.apId.equals(oneAp.getId())) {
					datas = HmBePerformUtil.fetchFFTDatas(oneAp);
					break;
				}
			}
		} catch (Exception e) {
			log.error("calculateSweptDuty", e);
		}

		if (datas==null) {
			return;
		}

//		Date start = new Date();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setStroke(new BasicStroke(1));

		int type=0;
		int startChannel = 2402;
		int endChannel = 2482;

		if (dashboardStatus.sweptDutyBand == 5150) {
			startChannel = 5160;
			endChannel = 5340;
			type=1;
		} else if (dashboardStatus.sweptDutyBand == 5470) {
			startChannel = 5470;
			endChannel = 5725;
			type=2;
		} else if (dashboardStatus.sweptDutyBand == 5725) {
			startChannel = 5725;
			endChannel = 5845;
			type=3;
		}
		int sweptDutyIndex = datas.length;

		float scale_x = (float) width / (endChannel - startChannel);
		float scale_y = (float) height / dashboardStatus.sweptFftCount;
		int y2 = height;
		for (int swept = 0; swept < sweptDutyIndex; swept++) {
			int x1 = 0;
			int y1 = height - Math.round((swept + 1) * scale_y);
			short[] sample = getOnlineDutyData(type, datas[sweptDutyIndex - 1 - swept]);
			if (sample==null) {
				sample= new short[endChannel-startChannel + 1];
				for(int i=0; i<=endChannel-startChannel; i++) {
					sample[i]=-1;
				}
			}

			for (int channel = 0; channel < sample.length; channel++) {
				int busy = sample[channel];
				if (busy > 100) {
					busy = 100;
				}
				if (busy<0) {
					busy = 0;
				}
				int color = (short) 50-busy/2;
				if (color >= rssiColors.length) {
					color = (short) (rssiColors.length - 1);
				}
				int x2;
				if (channel==0) {
					x2 = Math.round(scale_x);
				} else {
					x2 = channel == sample.length - 1 ? width : Math
							.round((channel + (float) 0.5) * scale_x);
				}
				g2.setColor(rssiColors[color]);
				if (y2 - y1 > 0) {
					g2.fillRect(x1, y1, x2 - x1, y2 - y1);
				}
				x1 = x2;
			}
			y2 = y1;
		}

//		Date end = new Date();
//		log.info_ln("Swept Duty image(" + width + ", " + height + ") in "
//				+ (end.getTime() - start.getTime()) + " ms.");
		response.setContentType("image/png");
		OutputStream os = response.getOutputStream();
		ImageIO.write(image, "png", os);
		os.close();
	}

	public List<CheckItem> getHiveAPLst() {
		return hiveAPLst;
	}

//	public void parpareHiveAPLst() {
//		// get list of id and name from database
//		String sql = "SELECT bo.id, bo.hostName" + " FROM "
//				+ HiveAp.class.getSimpleName() + " bo";
//		List<?> bos = QueryUtil.executeQuery(sql, new SortParams("hostName"),
//				new FilterParams(
//						"manageStatus=:s1 and (hiveApModel=:s2 or hiveApModel=:s3"
//								+ " or hiveApModel=:s4 or hiveApModel=:s5) and softVer>=:s6",
//						new Object[] { HiveAp.STATUS_MANAGED,
//								HiveAp.HIVEAP_MODEL_120,
//								HiveAp.HIVEAP_MODEL_110,
//								HiveAp.HIVEAP_MODEL_350,
//								HiveAp.HIVEAP_MODEL_330,
//								"4.0.0.0"}), domainId);
//
//		Set<SimpleHiveAp> runAPs = BeSpectralAnalysisProcessor.getInstance()
//				.getRunningAp();
//		Set<Long> runApId = new HashSet<Long>();
//		for (SimpleHiveAp singleAP : runAPs) {
//			runApId.add(singleAP.getId());
//		}
//		hiveAPLst = new ArrayList<CheckItem>();
//		HashSet<Long> apSetLong = new HashSet<Long>();
//		for (Object obj : bos) {
//			Object[] item = (Object[]) obj;
//			Long apId = Long.parseLong(item[0].toString());
//			if (runApId.contains(apId)) {
//				continue;
//			}
//			String profileName = (String) item[1];
//			CheckItem checkItem = new CheckItem(apId, profileName);
//			hiveAPLst.add(checkItem);
//			apSetLong.add(apId);
//		}
//
//		if (hiveAPLst.size() == 0) {
//			hiveAPLst.add(new CheckItem((long) -1, MgrUtil
//					.getUserMessage("config.optionsTransfer.none")));
//		}
//
//		if (runAP==null || !apSetLong.contains(runAP)) {
//			runAP = hiveAPLst.get(0).getId();
//		}
//
//	}

	public String getCurrentAPHostName() {
		if (id == null)
			return "";
		for (SimpleHiveAp simpleAP : CacheMgmt.getInstance().getManagedApList()) {
			if (simpleAP.getId().equals(id)) {
				return simpleAP.getHostname();
			}
		}
		return "";
	}

	public String getCurrentParamsIf(){
		if (id == null) {
			return "";
		}
		int channels = getRunningApInfoIf();
		StringBuffer sb = new StringBuffer();
		sb.append("Interface: ");
		if (channels==AhCustomReport.REPORT_INTERFACE_WIFI0){
			sb.append("2.4 GHz");
		} else {
			sb.append("5 GHz");
		}
		return sb.toString();
	}

	public String getCurrentParamsChannels(){
		if (id == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		int channels = getRunningApInfoIf();
		sb.append("Channels: ");
		if (channels==AhCustomReport.REPORT_INTERFACE_WIFI0){
			sb.append(getRunningApInfoCh0());
		} else {
			sb.append(getRunningApInfoCh1());
		}
		return sb.toString();
	}

	public String currentParamsRemainTime() {
		if (id == null) {
			return "";
		}
		return AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().getRunningApInfoRemainTime(id);
	}

	public int getRunningApInfoIf() {
		if (id == null)
			return 1;
		return AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().getRunningApInfoIf(id);
	}

	public String getRunningApInfoCh0() {
		if (id == null)
			return "";
		return AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().getRunningApInfoCh0(id);
	}

	public String getRunningApInfoCh1() {
		if (id == null)
			return "";
		return AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().getRunningApInfoCh1(id);
	}

	public int getRunningApInfoTime() {
		if (id == null)
			return 5;
		return AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().getRunningApInfoTime(id);
	}

	public int getRunningApInfoInterval() {
		if (id == null)
			return 1;
		return AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().getRunningApInfoInterval(id);
	}

	private String getResultErrorMessage(int ret) {
		switch (ret) {
		case BeSpectralAnalysisProcessor.STATUS_DOING:
			return "Operation failed. The selected AP already doing spectrum analysis.";
		case BeSpectralAnalysisProcessor.STATUS_NOAP:
			return MgrUtil.getUserMessage("error.spn.ap.notexist");
		case BeSpectralAnalysisProcessor.STATUS_SIMULATED:
			return MgrUtil.getUserMessage("error.spn.ap.simulate");
		case BeSpectralAnalysisProcessor.STATUS_MAXAP:
			return MgrUtil.getUserMessage("error.spn.max.support.hmol","30");
		case BeSpectralAnalysisProcessor.STATUS_ERROR_DB:
			return"Operation failed. DB operation error.";
		case BeSpectralAnalysisProcessor.STATUS_ERROR_CLI:
			return MgrUtil.getUserMessage("error.spn.ap.no.connect");
		case BeSpectralAnalysisProcessor.STATUS_ERROR_CHANNEL:
			return MgrUtil.getUserMessage("error.spn.invalid.channel");
		case BeSpectralAnalysisProcessor.STATUS_ERROR_RADIO:
			return MgrUtil.getUserMessage("error.spn.invalid.if");
		default:
			return NmsUtil.getHiveosErrorMessage(ret);
		}
	}

	private String updateDashboardParam(int param) throws Exception {
		log.info(operation + ": " + param);
		jsonObject = new JSONObject();
		jsonObject.put("aid", dashboardStatus.aid);
		jsonObject.put("value", param);
		return "json";
	}

	private String updateDashboardParam(boolean param) throws Exception {
		log.info(operation + ": " + param);
		jsonObject = new JSONObject();
		jsonObject.put("aid", dashboardStatus.aid);
		jsonObject.put("value", param);
		return "json";
	}

//	public Set<SimpleHiveAp> getListRunAPs() {
//		Set<SimpleHiveAp> lstAllHiveAPs = BeSpectralAnalysisProcessor.getInstance().getRunningAp();
//
//		Set<SimpleHiveAp> lstHiveAP = new HashSet<SimpleHiveAp>();
//		for(SimpleHiveAp oneAP: lstAllHiveAPs){
//			lstHiveAP.add(oneAP);
//		}
//
//		Iterator<SimpleHiveAp> aps = lstHiveAP.iterator();
//		while (aps.hasNext()) {
//			SimpleHiveAp ap = aps.next();
//			if (!ap.getDomainId().equals(domainId)) {
//				aps.remove();
//			}
//		}
//		return lstHiveAP;
//	}

//	public String getNoteInfo() {
//		if (!isHMOnline()) {
//			return getText("hm.tool.snp.note")
//					+ " "
//					+ BeSpectralAnalysisProcessor.getInstance().getRunningAp().size()
//					+ " "
//					+ getText("hm.tool.snp.note2");
//		} else {
//			Set<SimpleHiveAp> runSetAp = BeSpectralAnalysisProcessor.getInstance().getRunningAp();
//			int runCount =0;
//			for (SimpleHiveAp oneRunAp: runSetAp) {
//				if (getDomainId().equals(oneRunAp.getDomainId())) {
//					runCount++;
//				}
//			}
//
//			return getText("hm.tool.snp.vhm.note")
//			+ " "
//			+ runCount
//			+ " "
//			+ getText("hm.tool.snp.vhm.note2");
//		}
//	}

//	public String getRunWriteDisabled() {
//		if ("".equals(getWriteDisabled())) {
//			if (!isHMOnline()) {
//				if (BeSpectralAnalysisProcessor.getInstance().getRunningAp().size() >= 10) {
//					return "disabled";
//				} else {
//					return "";
//				}
//			} else {
//				Set<SimpleHiveAp> runSetAp = BeSpectralAnalysisProcessor.getInstance().getRunningAp();
//				int runCount =0;
//				for (SimpleHiveAp oneRunAp: runSetAp) {
//					if (getDomainId().equals(oneRunAp.getDomainId())) {
//						runCount++;
//					}
//				}
//				if (runCount >= 2 || runSetAp.size()>=20) {
//					return "disabled";
//				} else {
//					return "";
//				}
//			}
//		}
//		return "disabled";
//	}

	private void prepareInterfaceLst(Long runApId) throws Exception {
		HiveAp ap = findBoById(HiveAp.class, runApId);
		if (ap != null) {
			if (AhConstantUtil.isTrueAll(Device.IS_DUALBAND, ap.getHiveApModel())) {
				if (ap.getWifi0() != null
						&& ap.getWifi0().getAdminState() == AhInterface.ADMIN_STATE_UP) {
						if (ap.getWifi0().getRadioMode()==RadioProfile.RADIO_PROFILE_MODE_BG ||
								ap.getWifi0().getRadioMode()==RadioProfile.RADIO_PROFILE_MODE_NG){
							interfaceLst = MgrUtil
							.enumItems(
									"enum.wlanPolicy.radioProfileMode.",
									new int[] { AhCustomReport.REPORT_INTERFACE_WIFI0 });
						} else {
							interfaceLst = MgrUtil
							.enumItems(
									"enum.wlanPolicy.radioProfileMode.",
									new int[] { AhCustomReport.REPORT_INTERFACE_WIFI1 });
						}
				} else {
					interfaceLst = new EnumItem[] { new EnumItem(
							-1,
							MgrUtil.getUserMessage("config.optionsTransfer.none")) };
				}
			} else {
				if (ap.getWifi0() != null
						&& ap.getWifi0().getAdminState() == AhInterface.ADMIN_STATE_UP
						&& ap.getWifi1() != null
						&& ap.getWifi1().getAdminState() == AhInterface.ADMIN_STATE_UP) {
					interfaceLst= MgrUtil
					.enumItems(
							"enum.wlanPolicy.radioProfileMode.",
							new int[] { AhCustomReport.REPORT_INTERFACE_WIFI0,AhCustomReport.REPORT_INTERFACE_WIFI1 });
				} else if (ap.getWifi0() != null
						&& ap.getWifi0().getAdminState() == AhInterface.ADMIN_STATE_UP) {
					interfaceLst = MgrUtil
							.enumItems(
									"enum.wlanPolicy.radioProfileMode.",
									new int[] { AhCustomReport.REPORT_INTERFACE_WIFI0 });
				} else if (ap.getWifi1() != null
						&& ap.getWifi1().getAdminState() == AhInterface.ADMIN_STATE_UP) {
					interfaceLst = MgrUtil
							.enumItems(
									"enum.wlanPolicy.radioProfileMode.",
									new int[] { AhCustomReport.REPORT_INTERFACE_WIFI1 });
				} else {
					interfaceLst = new EnumItem[] { new EnumItem(
							-1,
							MgrUtil.getUserMessage("config.optionsTransfer.none")) };
				}
			}
		} else {
			interfaceLst = new EnumItem[] { new EnumItem(-1,
					MgrUtil.getUserMessage("config.optionsTransfer.none")) };
		}

		runInterface = interfaceLst[0].getKey();
	}

	public EnumItem[] getInterfaceLst() {
		return interfaceLst;
	}

	public List<CheckItem> getTimeLst() {
		List<CheckItem> lstTime = new ArrayList<CheckItem>();
		int intMaxTime = 60;
		if (!isHMOnline() || (isHMOnline() && userContext.isSuperUser())) {
			intMaxTime = 600;
		}
		for (int i = 5; i <= intMaxTime;) {
			if (i < 60) {
				lstTime.add(new CheckItem((long) i, i + " mins"));
				i = i + 5;
			} else if (i == 60) {
				lstTime.add(new CheckItem((long) i, i / 60 + " hour"));
				i = i + 30;
			} else if (i == 90) {
				lstTime.add(new CheckItem((long) i, "1 hour 30 mins"));
				i = i + 30;
			} else {
				lstTime.add(new CheckItem((long) i, i / 60 + " hours"));
				i = i + 60;
			}
		}
		return lstTime;
	}

	private String tilesStatus;
	private DashboardStatus dashboardStatus;
	private int width, height;

	private List<SpectralAnalysisInterference> interferenceLst= new ArrayList<SpectralAnalysisInterference>();

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setAid(int aid) {
		dashboardStatus = getDashboardStatus(aid);
	}

	public int getAid() {
		return dashboardStatus.aid;
	}

	public int getFftBand() {
		return dashboardStatus.fftBand;
	}

	public void setFftBand(int fftBand) {
		dashboardStatus.fftBand = fftBand;
		if (fftBand == 2400) {
			dashboardStatus.fftChannels = 1611;
			dashboardStatus.fftCenter = 2442;
			dashboardStatus.fftSpan = 80;
		} else if (fftBand == 5150) {
			dashboardStatus.fftChannels = 3664;
			dashboardStatus.fftCenter = 5250;
			dashboardStatus.fftSpan = 200;
		} else if (fftBand == 5470) {
			dashboardStatus.fftChannels = 100140;
			dashboardStatus.fftCenter = 5600;
			dashboardStatus.fftSpan = 240;
		} else if (fftBand == 5725) {
			dashboardStatus.fftChannels = 149165;
			dashboardStatus.fftCenter = 5785;
			dashboardStatus.fftSpan = 200;
		}
	}

	public int getFftChannels() {
		return dashboardStatus.fftChannels;
	}

	public void setFftChannels(int fftChannels) {
		dashboardStatus.fftChannels = fftChannels;
	}

	public int getFftCenter() {
		return dashboardStatus.fftCenter;
	}

	public void setFftCenter(int fftCenter) {
		dashboardStatus.fftCenter = fftCenter;
	}

	public int getFftSpan() {
		return dashboardStatus.fftSpan;
	}

	public void setFftSpan(int fftSpan) {
		dashboardStatus.fftSpan = fftSpan;
	}

	public int getFftRefLevel() {
		return dashboardStatus.fftRefLevel;
	}

	public void setFftRefLevel(int fftRefLevel) {
		dashboardStatus.fftRefLevel = fftRefLevel;
	}

	public int getFftVertScale() {
		return dashboardStatus.fftVertScale;
	}

	public void setFftVertScale(int fftVertScale) {
		dashboardStatus.fftVertScale = fftVertScale;
	}

	public boolean getFftSample(){
		return dashboardStatus.fftSample;
	}

	public void setFftSample(boolean fftSample){
		dashboardStatus.fftSample = fftSample;
	}

	public boolean getDutySample(){
		return dashboardStatus.dutySample;
	}

	public void setDutySample(boolean dutySample){
		dashboardStatus.dutySample = dutySample;
	}

	public int getDutyBand() {
		return dashboardStatus.dutyBand;
	}

	public void setDutyBand(int dutyBand) {
		dashboardStatus.dutyBand = dutyBand;
		if (dutyBand == 2400) {
			dashboardStatus.dutyChannels = 1611;
			dashboardStatus.dutyCenter = 2442;
			dashboardStatus.dutySpan = 80;
		} else if (dutyBand == 5150) {
			dashboardStatus.dutyChannels = 3664;
			dashboardStatus.dutyCenter = 5250;
			dashboardStatus.dutySpan = 200;
		} else if (dutyBand == 5470) {
			dashboardStatus.dutyChannels = 100140;
			dashboardStatus.dutyCenter = 5600;
			dashboardStatus.dutySpan = 240;
		} else if (dutyBand == 5725) {
			dashboardStatus.dutyChannels = 149165;
			dashboardStatus.dutyCenter = 5785;
			dashboardStatus.dutySpan = 200;
		}
	}

	public int getDutyChannels() {
		return dashboardStatus.dutyChannels;
	}

	public void setDutyChannels(int dutyChannels) {
		dashboardStatus.dutyChannels = dutyChannels;
	}

	public int getDutyCenter() {
		return dashboardStatus.dutyCenter;
	}

	public void setDutyCenter(int dutyCenter) {
		dashboardStatus.dutyCenter = dutyCenter;
	}

	public int getDutySpan() {
		return dashboardStatus.dutySpan;
	}

	public void setDutySpan(int dutySpan) {
		dashboardStatus.dutySpan = dutySpan;
	}

	public int getDutyMin() {
		return dashboardStatus.dutyMin;
	}

	public void setDutyMin(int dutyMin) {
		dashboardStatus.dutyMin = dutyMin;
	}

	public int getDutyMax() {
		return dashboardStatus.dutyMax;
	}

	public void setDutyMax(int dutyMax) {
		dashboardStatus.dutyMax = dutyMax;
	}

	public int getSweptFftBand() {
		return dashboardStatus.sweptFftBand;
	}

	public void setSweptFftBand(int sweptFftBand) {
		dashboardStatus.sweptFftBand = sweptFftBand;
		if (sweptFftBand == 2400) {
			dashboardStatus.sweptFftChannels = 1611;
			dashboardStatus.sweptFftCenter = 2442;
			dashboardStatus.sweptFftSpan = 80;
		} else if (sweptFftBand == 5150) {
			dashboardStatus.sweptFftChannels = 3664;
			dashboardStatus.sweptFftCenter = 5250;
			dashboardStatus.sweptFftSpan = 200;
		} else if (sweptFftBand == 5470) {
			dashboardStatus.sweptFftChannels = 100140;
			dashboardStatus.sweptFftCenter = 5600;
			dashboardStatus.sweptFftSpan = 240;
		} else if (sweptFftBand == 5725) {
			dashboardStatus.sweptFftChannels = 149165;
			dashboardStatus.sweptFftCenter = 5785;
			dashboardStatus.sweptFftSpan = 200;
		}
	}

	public int getSweptFftChannels() {
		return dashboardStatus.sweptFftChannels;
	}

	public void setSweptFftChannels(int sweptFftChannels) {
		dashboardStatus.sweptFftChannels = sweptFftChannels;
	}

	public int getSweptFftCenter() {
		return dashboardStatus.sweptFftCenter;
	}

	public void setSweptFftCenter(int sweptFftCenter) {
		dashboardStatus.sweptFftCenter = sweptFftCenter;
	}

	public int getSweptFftSpan() {
		return dashboardStatus.sweptFftSpan;
	}

	public void setSweptFftSpan(int sweptFftSpan) {
		dashboardStatus.sweptFftSpan = sweptFftSpan;
	}

//	public int getSweptFftSample(){
//		return dashboardStatus.sweptFftSample;
//	}
//
//	public void setSweptFftSample(int sweptFftSample){
//		dashboardStatus.sweptFftSample = sweptFftSample;
//	}

	public int getSweptDutyBand() {
		return dashboardStatus.sweptDutyBand;
	}

	public void setSweptDutyBand(int sweptDutyBand) {
		dashboardStatus.sweptDutyBand = sweptDutyBand;
		if (sweptDutyBand == 2400) {
			dashboardStatus.sweptDutyChannels = 1611;
			dashboardStatus.sweptDutyCenter = 2442;
			dashboardStatus.sweptDutySpan = 80;
		} else if (sweptDutyBand == 5150) {
			dashboardStatus.sweptDutyChannels = 3664;
			dashboardStatus.sweptDutyCenter = 5250;
			dashboardStatus.sweptDutySpan = 200;
		} else if (sweptDutyBand == 5470) {
			dashboardStatus.sweptDutyChannels = 100140;
			dashboardStatus.sweptDutyCenter = 5600;
			dashboardStatus.sweptDutySpan = 240;
		} else if (sweptDutyBand == 5725) {
			dashboardStatus.sweptDutyChannels = 149165;
			dashboardStatus.sweptDutyCenter = 5785;
			dashboardStatus.sweptDutySpan = 200;
		}
	}

	public int getSweptDutyChannels() {
		return dashboardStatus.sweptDutyChannels;
	}

	public void setSweptDutyChannels(int sweptDutyChannels) {
		dashboardStatus.sweptDutyChannels = sweptDutyChannels;
	}

	public int getSweptDutyCenter() {
		return dashboardStatus.sweptDutyCenter;
	}

	public void setSweptDutyCenter(int sweptDutyCenter) {
		dashboardStatus.sweptDutyCenter = sweptDutyCenter;
	}

	public int getSweptDutySpan() {
		return dashboardStatus.sweptDutySpan;
	}

	public void setSweptDutySpan(int sweptDutySpan) {
		dashboardStatus.sweptDutySpan = sweptDutySpan;
	}

	private DashboardStatus initDashboardStatus(Long apId) {
		DashboardSessionMgmt sessionMgmt = (DashboardSessionMgmt) MgrUtil
				.getSessionAttribute(SessionKeys.SPECTRUM_ANALYSIS_DASHBOARD);
		if (sessionMgmt == null) {
			sessionMgmt = new DashboardSessionMgmt();
			MgrUtil.setSessionAttribute(SPECTRUM_ANALYSIS_DASHBOARD,
					sessionMgmt);
		}
		return sessionMgmt.initDashboardStatus(apId);
	}

	private void resetDashboardStatus(Long apId) {
		DashboardSessionMgmt sessionMgmt = (DashboardSessionMgmt) MgrUtil
				.getSessionAttribute(SessionKeys.SPECTRUM_ANALYSIS_DASHBOARD);
		if (sessionMgmt == null) {
			sessionMgmt = new DashboardSessionMgmt();
			MgrUtil.setSessionAttribute(SPECTRUM_ANALYSIS_DASHBOARD,
					sessionMgmt);
		}
		sessionMgmt.resetDashboardStatus(apId);
	}

	private DashboardStatus getDashboardStatus(int aid) {
		DashboardSessionMgmt sessionMgmt = (DashboardSessionMgmt) MgrUtil
				.getSessionAttribute(SessionKeys.SPECTRUM_ANALYSIS_DASHBOARD);
		return sessionMgmt.getDashboardStatus(aid);
	}

	public String getTilesStatus() {
		return tilesStatus;
	}

	public void setTilesStatus(String tilesStatus) {
		this.tilesStatus = tilesStatus;
	}

	private Long runAP;
	private int runInterface = -1;
	private String runChannelWifi0;
	private String runChannelWifi1;
	private int runInterval = 1;
	private int runTime = 5;
	private Long currentApId;

	private List<CheckItem> hiveAPLst = new ArrayList<CheckItem>();
	private EnumItem[] interfaceLst = null;

	public String getShowWifi0Channel() {
		if (runInterface != AhCustomReport.REPORT_INTERFACE_WIFI1
				&& runInterface != -1) {
			return "";
		}
		return "none";
	}

	public String getShowWifi1Channel() {
		if (runInterface != AhCustomReport.REPORT_INTERFACE_WIFI0
				&& runInterface != -1) {
			return "";
		}
		return "none";
	}

	public Long getCurrentApId() {
		return currentApId;
	}

	public void setCurrentApId(Long currentApId) {
		this.currentApId = currentApId;
	}

	public Long getRunAP() {
		return runAP;
	}

	public void setRunAP(Long runAP) {
		this.runAP = runAP;
	}

	public int getRunInterface() {
		return runInterface;
	}

	public void setRunInterface(int runInterface) {
		this.runInterface = runInterface;
	}

	public int getRunInterval() {
		return runInterval;
	}

	public void setRunInterval(int runInterval) {
		this.runInterval = runInterval;
	}

	public int getRunTime() {
		return runTime;
	}

	public void setRunTime(int runTime) {
		this.runTime = runTime;
	}

	public String getStartSNP() {
		return "startSNP";
	}

	public String getStopSNP() {
		return "stopSNP";
	}

	private static Color[] rssiColors = new Color[] { new Color(128, 0, 0),
			new Color(149, 21, 0), new Color(170, 43, 0),
			new Color(191, 64, 0), new Color(212, 85, 0),
			new Color(234, 107, 0), new Color(255, 128, 0),
			new Color(255, 140, 0), new Color(255, 151, 0),
			new Color(255, 163, 0), new Color(255, 174, 0),
			new Color(255, 186, 0), new Color(255, 197, 0),
			new Color(255, 209, 0), new Color(255, 220, 0),
			new Color(255, 232, 0), new Color(255, 243, 0),
			new Color(255, 255, 0), new Color(212, 255, 11),
			new Color(170, 255, 21), new Color(128, 255, 32),
			new Color(85, 255, 43), new Color(43, 255, 53),
			new Color(0, 255, 64), new Color(0, 255, 88),
			new Color(0, 255, 112), new Color(0, 255, 136),
			new Color(0, 255, 159), new Color(0, 255, 183),
			new Color(0, 255, 207), new Color(0, 255, 231),
			new Color(0, 255, 255), new Color(0, 241, 254),
			new Color(1, 228, 252), new Color(1, 214, 251),
			new Color(1, 200, 249), new Color(2, 187, 248),
			new Color(2, 173, 246), new Color(2, 159, 245),
			new Color(2, 146, 244), new Color(3, 132, 242),
			new Color(3, 118, 241), new Color(6, 109, 227),
			new Color(9, 99, 212), new Color(12, 90, 198),
			new Color(15, 80, 184), new Color(18, 70, 170),
			new Color(21, 61, 156), new Color(24, 51, 141)};
//			new Color(27, 42, 127), new Color(30, 32, 113),
//			new Color(33, 22, 98), new Color(36, 12, 84), new Color(39, 3, 70),
//			new Color(42, 0, 56), new Color(45, 0, 42) };

	private class DashboardStatus {
		private DashboardStatus(int aid) {
			this.aid = aid;
		}

		private int aid;
		private Long apId=-1l;
		private long sampleId;
		private int fftBand = 2400, fftChannels = 1611, fftCenter = 2442,
				fftSpan = 80, fftRefLevel = 0, fftVertScale = 10;
		private boolean fftSample=true, dutySample=true;
		private int dutyBand = 2400, dutyChannels = 1611, dutyCenter = 2442,
				dutySpan = 80, dutyMin = 0, dutyMax = 100;
		private int sweptFftBand = 2400, sweptFftChannels = 1611, sweptFftCenter = 2442,
				sweptFftSpan = 80, sweptFftCount = 200;
		private int sweptDutyBand = 2400, sweptDutyChannels = 1611, sweptDutyCenter = 2442,
				sweptDutySpan = 80;
	}

	private class DashboardSessionMgmt {
		private DashboardSessionMgmt() {
			apMap = new HashMap<Long, DashboardStatus>();
			aidMap = new HashMap<Integer, DashboardStatus>();
			aidGen = new AtomicInteger(0);
		}

		private void resetDashboardStatus(Long apId) {
			apMap.put(apId, null);
		}

		private DashboardStatus initDashboardStatus(Long apId) {
			int aid = aidGen.incrementAndGet();
			DashboardStatus status = apMap.get(apId);
			if (status == null) {
				status = new DashboardStatus(aid);
				status.apId=apId;
				apMap.put(apId, status);
			} else {
				status.aid = aid; // Reuse based on AP id, but update aid
			}
			status.sampleId = -1; // sampleId will be set after first poll
			status.apId=apId;
			aidMap.put(aid, status);
			return status;
		}

		private DashboardStatus getDashboardStatus(int aid) {
			DashboardStatus status = aidMap.get(aid);
			if (status != null && status.aid == aid) {
				return status;
			} else { // this one has become invalid.
				return new DashboardStatus(-1);
			}
		}

		Map<Long, DashboardStatus> apMap;
		Map<Integer, DashboardStatus> aidMap;
		AtomicInteger aidGen;
	}

	public String getRunChannelWifi0() {
		return runChannelWifi0;
	}

	public void setRunChannelWifi0(String runChannelWifi0) {
		this.runChannelWifi0 = runChannelWifi0;
	}

	public String getRunChannelWifi1() {
		return runChannelWifi1;
	}

	public void setRunChannelWifi1(String runChannelWifi1) {
		this.runChannelWifi1 = runChannelWifi1;
	}

	public List<SpectralAnalysisInterference> getInterferenceLst() {
//		for(int i=0;i<20; i++) {
//			SpectralAnalysisInterference aa = new SpectralAnalysisInterference();
//			aa.setApName("apName" + i);
//			interferenceLst.add(aa);
//		}
		return interferenceLst;
	}
}
