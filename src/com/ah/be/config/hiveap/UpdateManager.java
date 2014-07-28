package com.ah.be.config.hiveap;

import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeConfigUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.cloudauth.HmCloudAuthCertMgmtImpl;
import com.ah.be.cloudauth.result.HmCloudAuthCertResult;
import com.ah.be.cloudauth.result.UpdateCAStatus;
import com.ah.be.common.AhDayLightSavingUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAbortEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCapwapDTLSConfigEvent;
import com.ah.be.communication.event.BeCapwapFileDownProgressEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.config.create.common.CVGAndBRIpResourceManage;
import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.be.config.create.source.impl.VPNProfileImpl;
import com.ah.be.config.event.AhBootstrapGeneratedEvent;
import com.ah.be.config.event.AhCloudAuthCAGenerateEvent;
import com.ah.be.config.event.AhConfigGeneratedEvent;
import com.ah.be.config.event.AhConfigGeneratedEvent.ConfigGenResultType;
import com.ah.be.config.event.AhConfigGenerationProgressEvent;
import com.ah.be.config.event.AhConfigGenerationProgressEvent.ConfigGenerationProgress;
import com.ah.be.config.event.AhConfigUpdatedEvent;
import com.ah.be.config.event.AhDeltaConfigGeneratedEvent;
import com.ah.be.config.event.AhDeviceRebootResultEvent;
import com.ah.be.config.hiveap.cancellation.CancelObject;
import com.ah.be.config.hiveap.tools.DownloadServerTool;
import com.ah.be.config.result.AhConfigGenerationResult;
import com.ah.be.config.result.ap.AhApConfigGenerationResult;
import com.ah.be.config.result.user.AhUserConfigGenerationResult;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.be.db.discovery.event.AhDiscoveryEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.ga.GAConfigHepler;
import com.ah.be.ga.IGAConfigHepler;
import com.ah.be.hiveap.L7SignatureMng;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.dataretention.ClientHistoryTracking;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking.InterfaceType;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking.VlanObj;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.DownloadInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApUpdateItem;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.VpnService;
import com.ah.bo.network.VpnServiceCredential;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.RadioProfile;
import com.ah.ui.actions.monitor.LocationClientWatchAction;
import com.ah.ui.actions.monitor.MapNodeAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class UpdateManager implements QueryBo, UpdateParameters {
	
	public static int UPDATE_MAX_COUNT = 10;

	private static final Tracer log = new Tracer(UpdateManager.class.getSimpleName());

	private final Hashtable<String, UpdateHiveAp> updateList;
	private final List<UpdateHiveAp> queueList;
	
	private final Map<Long, List<UpdateHiveAp>> uploadVhmQue;
	private final List<String> queuePostion;
	
	//ap.macaddress map locationWatchlist id
	public final Map<String, Long> locationWatchListMap = new HashMap<>();
	
	private final Set<String> simplifyUpdateRecords = new HashSet<String>();
	
	private static SimpleDateFormat rebootTimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

	public UpdateManager() {
		updateList = new Hashtable<>();
		queueList = new ArrayList<>();
		uploadVhmQue = new HashMap<>();
		queuePostion = new ArrayList<>();
		initializeLocationClientWatchMap();
	}

	public void start() {
		//HmBeCommunicationUtil.getBeCommunicationProcessor().getBeCommunicationRequestManager().start();
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_CONFIG,
				"<BE Thread> BeCommunication RequestManager is running...");
	}

	/**
	 * add update HiveAp into queue.
	 *
	 * @param list
	 *            -
	 * @return return null value when add successfully. return a list of hiveAp
	 *         hostname and error message array([hostname, errorMessage]) which
	 *         occur error when add failed.
	 */
	public synchronized List<String[]> addUpdateObjects(List<UpdateHiveAp> list) {
		if (null == list) {
			return null;
		}

		List<String[]> duplexList = new ArrayList<>();
		Map<String, UpdateHiveAp> aps = new HashMap<>();
		
		//handle for if upload IDM error, next operation continue;
		for (UpdateHiveAp obj : list){
			if(obj.getUpdateObjectList() == null){
				continue;
			}
			Iterator<UpdateObject> itemObj = obj.getUpdateObjectList().iterator();
			while(itemObj.hasNext()){
				UpdateObject idmObj = itemObj.next();
				if(idmObj.getUpdateType() == UpdateParameters.AH_DOWNLOAD_CLOUDAUTH_CERTIFICATE && itemObj.hasNext()){
					itemObj.next().setContinued(true);
					break;
				}
			}
		}

		for (UpdateHiveAp obj : list) {
			String nodeId = obj.getHiveAp().getMacAddress();
			aps.put(nodeId, obj);
		}
		if (duplexList.isEmpty()) {
			// Apply for transaction id
			Map<String, Integer> transactionCodes = HmBeCommunicationUtil.getBeCommunicationProcessor().getBeCommunicationRequestManager()
					.requestConfigurationCliPermit4Transaction(aps.keySet());
			for (String mac : transactionCodes.keySet()) {
				int transactionCode = transactionCodes.get(mac);
				if (transactionCode < 0) {
					// apply failed
					String hostname = aps.get(mac).getHiveAp().getHostName();
					duplexList.add(new String[] {
							hostname,
							NmsUtil.getUserMessage("error.cli.obj.onRequest",
									new String[] { hostname }) });
				} else {
					aps.get(mac).setTransactionCode(transactionCode);
				}
			}
		}

		if (duplexList.isEmpty()) {
			// no duplicate, so add them in.
			joinIn(list);
			DebugUtil.configDebugInfo("Current update HiveAp list size:"
					+ updateList.size());
			return null;
		} else {
			return duplexList;
		}
	}

	private void joinIn(List<UpdateHiveAp> list) {
		for (UpdateHiveAp upAp : list) {
			HiveAp hiveAp = upAp.getHiveAp();
			List<UpdateObject> updateObjectList = upAp.getUpdateObjectList();
			if (null == updateObjectList || updateObjectList.isEmpty()) {
				continue;
			}
			try {
				if(upAp.getUpdateType() == AH_DOWNLOAD_SCRIPT_WIZARD){
					createDownloadInfo(hiveAp, false);
				}
				if (upAp.isAutoProvision()) {
					UpdateObject imageItem = upAp
							.getUpdateObject(AH_DOWNLOAD_IMAGE);
					if (null != imageItem) {
						// create result entry for upload image
						List<UpdateObject> img = new ArrayList<>(1);
						img.add(imageItem);
						// add into Database;
						createResultEntry(hiveAp, img, true, AH_DOWNLOAD_IMAGE, upAp);
					}
					UpdateObject countryItem = upAp
							.getUpdateObject(AH_DOWNLOAD_COUNTRY_CODE);
					if (null != countryItem) {
						// create result entry for update country code
						List<UpdateObject> country = new ArrayList<>(
								1);
						country.add(countryItem);
						// add into Database;
						createResultEntry(hiveAp, country, true,
								AH_DOWNLOAD_COUNTRY_CODE, upAp);
					}
					// create result entry for other items
					List<UpdateObject> configItem = new ArrayList<>();
					for (UpdateObject object : updateObjectList) {
						if (object.getUpdateType() == AH_DOWNLOAD_COUNTRY_CODE
								|| object.getUpdateType() == AH_DOWNLOAD_IMAGE) {
							continue;
						}
						configItem.add(object);
					}
					if (!configItem.isEmpty()) {
						createResultEntry(hiveAp, configItem, true,
								upAp.getUpdateType(), upAp);
					}
					// 2) add into queue list;
//					queueList.add(upAp);
					addUploadVhmQue(upAp);
				} else {
					// 1) add into Database;
					createResultEntry(hiveAp, updateObjectList, false, upAp
							.getUpdateType(), upAp);
					// 2) add into queue list;
//					queueList.add(upAp);
					addUploadVhmQue(upAp);
				}
			} catch (Exception e) {
				HmBeCommunicationUtil.getBeCommunicationProcessor().getBeCommunicationRequestManager().dismissConfigurationCliPermit(hiveAp
						.getMacAddress());
				DebugUtil.configDebugError("Error when apply HiveAp ["
						+ hiveAp.getHostName()
						+ "] into queue list. Dissmiss transaction.", e);
			}
		}
	}

	private void createResultEntry(HiveAp hiveAp, List<UpdateObject> objects,
			boolean isAuto, short updateType, UpdateHiveAp upAp) throws Exception {
		HiveApUpdateResult result = new HiveApUpdateResult();
		result.setTag(isAuto ? HiveApUpdateResult.TAG_AUTO_PROVISION : 0);
		result.setHostname(hiveAp.getHostName());
		result.setIpAddress(hiveAp.getIpAddress());
		result.setNodeId(hiveAp.getMacAddress());
		result.setOwner(hiveAp.getOwner());
		result.setStartTime(System.currentTimeMillis());
		result.setUpdateType(updateType);
		result.setState(PROCESS_NOT_START);
		result.setStagedTime(upAp.getStagedTime());

		List<HiveApUpdateItem> items = new ArrayList<>();
		short firstItemType = -1;
		for (UpdateObject updateObj : objects) {
			if (firstItemType == -1) {
				firstItemType = updateObj.getUpdateType();
			}
			HiveApUpdateItem item = new HiveApUpdateItem();
			item.setActived(updateObj.isActived());
			item.setFileSize(updateObj.getFileSize());
			item.setClis(updateObj.getCliString());
			item.setUpdateType(updateObj.getUpdateType());
			item.setContinued(updateObj.isContinued());
			// set up scriptType parameter while it's script upload
			if (updateObj.getUpdateType() == AH_DOWNLOAD_SCRIPT
					|| updateObj.getUpdateType() == AH_DOWNLOAD_PSK) {
				ScriptConfigObject scObj = (ScriptConfigObject) updateObj;
				item.setScriptType(scObj.getScriptType());
			}
			items.add(item);
		}
		result.setItems(items);
		// actionType to be cancel while the next update is image;
		if (firstItemType == AH_DOWNLOAD_IMAGE) {
			result.setActionType(ACTION_CANCEL);
		}

		//update staged status
		QueryUtil.updateBo(HiveApUpdateResult.class, "result=:s1, actionType=:s2", new FilterParams("nodeId = :s3 and result=:s4",
				new Object[] {UpdateParameters.UPDATE_ABORT, Short.valueOf("-1"), hiveAp.getMacAddress(), UpdateParameters.UPDATE_STAGED}));

		// 1) add into Database;
		Long resultId = QueryUtil.createBo(result);
		for (UpdateObject updateObj : objects) {
			updateObj.setResultId(resultId);
		}
	}

	private void generateRequest(UpdateHiveAp upAp) {
		// fetch the first update object from the list, and generate
		// a request.
		DebugUtil.configDebugInfo("HiveAp [" + upAp.getHiveAp().getHostName()
				+ "] has requests count :" + upAp.getRemainUpdateObjectCount());
		UpdateObject nextItem = upAp.getNextUpdateObject();
		if (null == nextItem) {
			removeHiveApOutOfUpdateList(upAp.getHiveAp().getMacAddress());
			return;
		}

		try {
			int type = nextItem.getUpdateType();

			switch (type) {
			case AH_DOWNLOAD_SCRIPT:
				// First must generate script file!
				ScriptConfigObject scriptItem = (ScriptConfigObject) nextItem;
				AhConfigGeneratedEvent scriptRequest;
				if (COMPLETE_SCRIPT == scriptItem.getScriptType()) {
					scriptRequest = new AhConfigGeneratedEvent(upAp.getHiveAp());
					scriptRequest.setConfigType(ConfigType.AP_FULL);
					scriptRequest.setSeqNum(nextItem.getSequenceNum());
				} else if (DELTA_SCRIPT_LAST == scriptItem.getScriptType()) {
					scriptRequest = new AhDeltaConfigGeneratedEvent(upAp
							.getHiveAp());
					scriptRequest.setConfigType(ConfigType.AP_DELTA);
					scriptRequest.setSeqNum(nextItem.getSequenceNum());
					// ((AhDeltaConfigGeneratedEvent) scriptRequest)
					// .setIncludingTempConfigs(true);
				} else {
					scriptRequest = new AhDeltaConfigGeneratedEvent(upAp
							.getHiveAp());
					scriptRequest.setConfigType(ConfigType.AP_AUDIT);
					scriptRequest.setSeqNum(nextItem.getSequenceNum());
					// ((AhDeltaConfigGeneratedEvent) scriptRequest)
					// .setIncludingTempConfigs(true);
				}
				AhAppContainer.getBeConfigModule().getConfigMgmt().add(
						scriptRequest);
				break;
			case AH_DOWNLOAD_PSK:
				// First must generate PSK file!
				ScriptConfigObject pskItem = (ScriptConfigObject) nextItem;
				AhConfigGeneratedEvent pskRequest;
				if (COMPLETE_SCRIPT == pskItem.getScriptType()) {
					pskRequest = new AhConfigGeneratedEvent(upAp.getHiveAp());
					pskRequest.setConfigType(ConfigType.USER_FULL);
					pskRequest.setSeqNum(nextItem.getSequenceNum());
				} else if (DELTA_SCRIPT_LAST == pskItem.getScriptType()) {
					pskRequest = new AhDeltaConfigGeneratedEvent(upAp
							.getHiveAp());
					pskRequest.setConfigType(ConfigType.USER_DELTA);
					pskRequest.setSeqNum(nextItem.getSequenceNum());
				} else {
					pskRequest = new AhDeltaConfigGeneratedEvent(upAp
							.getHiveAp());
					pskRequest.setConfigType(ConfigType.USER_AUDIT);
					pskRequest.setSeqNum(nextItem.getSequenceNum());
				}
				AhAppContainer.getBeConfigModule().getConfigMgmt().add(
						pskRequest);
				break;
			case AH_DOWNLOAD_BOOTSTRAP:
				// First must generate bootstrap file!
				BootstrapConfigObject bsItem = (BootstrapConfigObject) nextItem;
				AhBootstrapGeneratedEvent bootstrapRequest = new AhBootstrapGeneratedEvent(
						upAp.getHiveAp());
				bootstrapRequest.setSeqNum(bsItem.getSequenceNum());
				bootstrapRequest.setAdminUser(bsItem.getAdminName());
				bootstrapRequest.setAdminPwd(bsItem.getPassword());
				bootstrapRequest.setCapwapServer(bsItem.getCapwapServer());
				bootstrapRequest.setCwpUdpPort(bsItem.getUdpPort());
				bootstrapRequest.setEchoTimeOut(bsItem.getEchoTimeout());
				bootstrapRequest.setDeadInterval(bsItem.getDeadInterval());
				bootstrapRequest.setEnableDtls(bsItem.isEnableDtls());
				bootstrapRequest.setDtlsPassWord(bsItem.getPassPhrase());
				bootstrapRequest.setCapwapServerBackup(bsItem
						.getCapwapServerBackup());
				bootstrapRequest.setEnableNetdump(bsItem.isEnableNetdump());
				bootstrapRequest.setNetdumpServer(bsItem.getNetdumpServer());
				bootstrapRequest.setVhmName(bsItem.getVhmName());
				AhAppContainer.getBeConfigModule().getConfigMgmt().add(
						bootstrapRequest);
				break;
			case AH_DOWNLOAD_CLOUDAUTH_CERTIFICATE:
				AhCloudAuthCAGenerateEvent cloudCaEvent = new AhCloudAuthCAGenerateEvent(upAp.getHiveAp());
				cloudCaEvent.setSeqNum(nextItem.getSequenceNum());
				cloudCaEvent.setClis(nextItem.getClis());
				AhAppContainer.getBeConfigModule().getConfigMgmt().add(cloudCaEvent);
				break;
			case AH_DOWNLOAD_IMAGE:
			case AH_DOWNLOAD_CWP:
			case AH_DOWNLOAD_RADIUS_CERTIFICATE:
			case AH_DOWNLOAD_COUNTRY_CODE:
			case AH_DOWNLOAD_POE:
			case AH_DOWNLOAD_VPN_CERTIFICATE:
			case AH_DOWNLOAD_NET_DUMP:
			case AH_DOWNLOAD_IP_NETMASK_GATEWAY_DNS:
			case AH_DOWNLOAD_OUTDOORSTTINGS:
			case AH_DOWNLOAD_OS_DETECTION:
			case AH_DOWNLOAD_L7_SIGNATURE:
			case AH_DOWNLOAD_REBOOT:
				// Send request directly!
				sendRequest(upAp, nextItem.getClis());
				break;
			case AH_DOWNLOAD_DS_CONFIG:
			case AH_DOWNLOAD_DS_USER_CONFIG:
			case AH_DOWNLOAD_DS_AUDIT_CONFIG:
				if(upAp.getHiveAp().isSimulated() && MgrUtil.isEnableDownloadServer() && 
						MgrUtil.isEnableDSSimulator()){
					String mac = upAp.getHiveAp().getMacAddress();
					String domainName = upAp.getHiveAp().getOwner().getDomainName();
					DownloadServerTool.getInstance().simulateUploadCfg(mac, domainName, nextItem);
				}else{
					sendRequest(upAp, nextItem.getClis());
					break;
				}
			default:
				DebugUtil.configDebugWarn("Unknown update type [" + type
						+ "] of HiveAp " + upAp.getHiveAp().getHostName());
			}
		} catch (Exception e) {
			DebugUtil.configDebugWarn(
					"Error occur when generate new request of HiveAp ["
							+ upAp.getHiveAp().getHostName(), e);
		}
	}

	private void updateResultEntry(UpdateHiveAp upAp, UpdateObject updateObject) {
		short updateType = updateObject.getUpdateType();
		long resultId = updateObject.getResultId();
		short state = updateObject.getState();
		short result = updateObject.getResult();
		float downloadRate = updateObject.getDownloadRate();
		String descri = updateObject.getDescription();
		boolean isLastItemInProcess = upAp.isLastItem(updateObject);
		HiveAp hiveAp = upAp.getHiveAp();
		String mac = hiveAp.getMacAddress();
	
		if (result > 0 && isLastItemInProcess) {
			// end status, the item is finished.
			if (null != upAp.getDtlsEvent()) {
				// send DTLS Event in the end process if needed.
				try {
					HmBeCommunicationUtil.sendRequest(upAp.getDtlsEvent());
					DebugUtil.configDebugWarn("DTLS event is send for HiveAP:"
							+ mac);
				} catch (Exception e) {
					DebugUtil.configDebugError(
							"Error occur when sending DTLS event to HiveAP:"
									+ mac + " after configuration updated.", e);
				}
			}
		}
		try {
			HiveApUpdateResult ur = QueryUtil.findBoById(
					HiveApUpdateResult.class, resultId, this);
			if (null == ur) {
				DebugUtil
						.configDebugError("Cannot find HiveApUpdateResult item to be updated for HiveAP:"
								+ mac + ", updateType:" + updateType);
			} else {
				HiveApUpdateItem item = ur.getItem(updateType);
				if (null == item) {
					DebugUtil
							.configDebugError("Cannot find HiveApUpdateResult item to be updated for HiveAP:"
									+ mac + ", updateType:" + updateType);
				} else {
					int itemCount = ur.getItemCount();
					int itemIndex = ur.getItemIndex(item);
					boolean isLastItem = ur.isLastItem(item);
					boolean isAuto = ur.isAutoProvision();

					boolean allItemActived = ur.isAllItemActived();

					ur.setDescription(descri);

					if (result > 0) {// end status, the item is finished.
						item.setResult(result);// update item result
						item.setDescription(descri);
						if (result == UPDATE_SUCCESSFUL) {
							float rate = ((itemIndex + 1) * 100 / itemCount) / 100f;
							ur.setDownloadRate(rate);// downloadRate
						}
						if (isLastItem) {
							ur.setFinishTime(System.currentTimeMillis());
							ur.setState((short) 0);
							if(upAp.isByStaged() && ur.getStagedTime() > 0){
								result = UPDATE_STAGED;
							}else if(item.getUpdateType() == AH_DOWNLOAD_REBOOT && 
									AhCliFactory.isRebootImmediately(item.getClis()) && 
									result == UPDATE_SUCCESSFUL){
								result = REBOOTING;
							}else if(result == REBOOT_SUCCESSFUL && ur.getUpdateType() != AH_DOWNLOAD_REBOOT){
								result = UPDATE_SUCCESSFUL;
							}
							
							ur.setResult(result);
							if (itemCount > 1) {
								// override description;(do not do it for the
								// layout messy)
								String desc = getFinallyDesc(ur);
								ur.setDescription(desc);
							}
						} else {
							ur.setState(PROCESS_NOT_START);
						}
					} else {// processing status
						ur.setState(state);// status
						if (downloadRate > 0) {
							ur.setDownloadRate(downloadRate);
						}
					}
					ur.setActionType(getActionType(updateType, state, result,
							allItemActived, isLastItem, isAuto));
					// specify for actionType
					boolean withReboot = false;
					if(updateList.get(mac) != null){
						withReboot = updateList.get(mac).isWithReboot();
					}
					if (isLastItem && !allItemActived
							&& (result == UPDATE_SUCCESSFUL) && withReboot) {
						ur.setActionType((short) -1);
						DebugUtil
								.configDebugWarn("actionType reset to -1 for HiveAP:"
										+ mac + ".");
					}
				}
			}

			if (result > 0 && (updateType == AH_DOWNLOAD_SCRIPT || 
					updateType == AH_DOWNLOAD_DS_CONFIG || 
					updateType == AH_DOWNLOAD_DS_AUDIT_CONFIG )) {
				// HiveApUpdateResult and HiveAp updates should
				// be performed within the same transaction.
//				if(updateObject instanceof ScriptConfigObject){
//					ScriptConfigObject scObj = (ScriptConfigObject) updateObject;
//					AhApConfigGenerationResult generationR = scObj
//							.getResultObject();
//					if (result == UPDATE_SUCCESSFUL && null != generationR) {
//						String credential = generationR.getVpnClientPwd();
//						Long vpnServiceId = generationR.getVpnServerId();
//						short primaryRole = generationR.getPrimaryRole();
//						short backupRole = generationR.getBackupRole();
//						updateVpnCredentialStatus(mac, credential, vpnServiceId,
//								primaryRole, backupRole);
//					}
//				}
				
				//update vpn user status
				updateVpnCredentialStatus(mac, result == UPDATE_SUCCESSFUL);
				
				BoMgmt.getHiveApMgmt().updateConfigResult(hiveAp, ur, updateObject,
						hiveAp.getNewConfigVerNum());
				updateVpnNetworkAddress(result, hiveAp);
				deviceUploadTrack(hiveAp);
				//monitor location watch list
				try {
					locationWatchListWatch(hiveAp);
				} catch (Exception e) {
					DebugUtil.configDebugError(
							"Error occur when watch location watch list:", e);
				}
			} else if (result > 0 && updateType == AH_DOWNLOAD_PSK) {
				ScriptConfigObject obj = (ScriptConfigObject) updateObject;
				BoMgmt.getHiveApMgmt()
						.updateUserDatabaseResult(hiveAp, ur, obj);
			} else if (result == UPDATE_SUCCESSFUL
					&& updateType == AH_DOWNLOAD_IMAGE) {
				ur.setLevel(updateObject.getLevel());
				BoMgmt.getHiveApMgmt().updateImageResult(hiveAp, ur);
			} else if (result == UPDATE_SUCCESSFUL
					&& updateType == AH_DOWNLOAD_L7_SIGNATURE) {
				BoMgmt.getHiveApMgmt().updateSignatureResult(hiveAp, ur);
			} else if (null != ur) {
				QueryUtil.updateBo(ur);
			}
		
			/*
			 * Trigger the HiveAP managed status here, assume any update success
			 * will change a new HiveAP to Managed HiveAP.
			 */
			if (isLastItemInProcess && result == UPDATE_SUCCESSFUL
					&& hiveAp.getManageStatus() == HiveAp.STATUS_NEW) {
				hiveAp = QueryUtil.findBoById(HiveAp.class, hiveAp.getId());
				if (null != hiveAp
						&& hiveAp.getManageStatus() == HiveAp.STATUS_NEW) {
					BeTopoModuleUtil.triggerNewHiveAPStatusToManaged(hiveAp);
				}
			}
			
			//set flag for judge complete or delta config.
			if(isLastItemInProcess && result == UPDATE_SUCCESSFUL && 
					(upAp.getUpdateType() == AH_DOWNLOAD_SCRIPT_WIZARD || upAp.getUpdateType() == AH_DOWNLOAD_SCRIPT || 
					upAp.getUpdateType() == AH_DOWNLOAD_DS_CONFIG || upAp.getUpdateType() == AH_DOWNLOAD_DS_AUDIT_CONFIG) ){
				//when config update successful, update completeUpdateTag column.
				setCompleteUpdateTag(result, hiveAp);
			}

		} catch (Exception e) {
			DebugUtil.configDebugError(
					"Error occur when update the Result entry id:" + resultId
							+ ", HiveAP mac:" + mac, e);
		}
	}

	private String getFinallyDesc(HiveApUpdateResult ur) {
		String desc = "";
		List<HiveApUpdateItem> items = ur.getItems();
		for (HiveApUpdateItem item : items) {
			short type = item.getUpdateType();
			String ds = item.getDescription();
			desc += HiveApUpdateResult.getUpdateTypeString(type) + ": " + ds
					+ " \n";
		}
		return desc;
	}

	private short getActionType(short updateType, short state, short result,
			boolean allActived, boolean isLastItem, boolean isAuto) {
		if (result > 0) {
			// end
			if (result == UPDATE_SUCCESSFUL && isLastItem && !allActived) {
				return ACTION_REBOOT;
			}else if (result == UPDATE_STAGED){
				return ACTION_CANCEL;
			}else if(updateType == AH_DOWNLOAD_REBOOT){
				return -1;
			}else if (result != UPDATE_SUCCESSFUL
					&& updateType != AH_DOWNLOAD_BOOTSTRAP && isLastItem
					&& !isAuto) {
				return ACTION_RETRY;
			}
		} else {
			// processing
			if (updateType == AH_DOWNLOAD_IMAGE
					&& (state == PROCESS_NOT_START || state == PROCESS_LOADING)) {
				return ACTION_CANCEL;
			}
		}
		return -1;
	}

	// This method will abort the next request in the list.
	private void abortRequest(UpdateHiveAp upAp) {
		UpdateObject item = upAp.getNextUpdateObject();
		if (null == item) {
			removeHiveApOutOfUpdateList(upAp.getHiveAp().getMacAddress());
			// updateHiveAPQueue();
			return;
		}
		String msg = NmsUtil.getUserMessage("error.hiveAp.update.abort");
		item.setResult(UPDATE_ABORT);
		item.setDescription(msg);
		updateResultEntry(upAp, item);
		prepareNextRequest(upAp);
	}

	// This method is called while the previous request executed failed.
	private void prepareNextRequest(UpdateHiveAp upAp) {
		// 1) remove current item from the current update list;
		upAp.removeFirstUpdateObject();
		// 2) get the next update object;
		UpdateObject nextItem = upAp.getNextUpdateObject();

		if (null != nextItem) {
			if (nextItem.isContinued()) {
				// generate the next request;
				generateRequest(upAp);
			} else {
				// abort the next request
				abortRequest(upAp);
			}
		} else {
			removeHiveApOutOfUpdateList(upAp.getHiveAp().getMacAddress());
		}
	}

	public void sendRequest(UpdateHiveAp upAp, String[] clis) {
		try {
			BeCliEvent downloadRequest = new BeCliEvent();
			downloadRequest.setAp(upAp.getHiveAp());
			downloadRequest.setClis(clis);
			downloadRequest.setSequenceNum(upAp.getNextUpdateObject()
					.getSequenceNum());
			downloadRequest.setCliType(BeCliEvent.CLITYPE_CONFIGURATION);
			downloadRequest.setTransactionCode(upAp.getTransactionCode());
			// downloadRequest.setCliData(new BeCapwapCliEvent(clis, upAp
			// .getNextUpdateObject().getSequenceNum()).buildPacket());
			downloadRequest.buildPacket();
			int serialNums = HmBeCommunicationUtil.sendRequest(downloadRequest,
					upAp.getNextUpdateObject().getMaxTimeout() / 1000);
			upAp.getNextUpdateObject().setSerialNum(serialNums);
			DebugUtil.configDebugInfo("HiveAp ["
					+ upAp.getHiveAp().getHostName()
					+ "] saved its current CAPWAP request serialNum:"
					+ serialNums + ",SequenceNum:"
					+ upAp.getNextUpdateObject().getSequenceNum());
		} catch (Exception e) {
			DebugUtil.configDebugError("send request error.", e);
			dealRequestPackageError(upAp);
		}
	}

	private void dealRequestPackageError(UpdateHiveAp upAp) {
		UpdateObject item = upAp.getNextUpdateObject();
		short updateType = item.getUpdateType();

		DebugUtil.configDebugWarn("HiveAp:" + upAp.getHiveAp().getHostName()
				+ "build request packet error, request type:[" + updateType
				+ "]");
		String msg = NmsUtil
				.getUserMessage("error.hiveAp.update.request.build");
		item.setResult(UPDATE_FAILED);
		item.setDescription(msg);
		updateResultEntry(upAp, item);
		prepareNextRequest(upAp);
	}
	
	private void updateVpnCredentialStatus(String hiveApMac, boolean success) throws Exception{
		if(success){
			String vpnIdSql = "select VPN_SERVICE_ID from VPN_SERVICE_CREDENTIAL where assignedClient = '"+hiveApMac+"'";
			List<?> typeResult = QueryUtil.executeNativeQuery(vpnIdSql);
			if(typeResult == null || typeResult.isEmpty()){
				return;
			}
			long vpnId = Long.valueOf(typeResult.get(0).toString());
			VpnService vpnService = QueryUtil.findBoById(VpnService.class, vpnId, new ConfigLazyQueryBo());
			if(vpnService == null){
				return;
			}
			
			int counts;
			if(vpnService.getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_2){
				counts = vpnService.getVpnServerType() == VpnService.VPN_SERVER_TYPE_SINGLE ? 1 : 2;
			}else{
				counts = vpnService.getVpnGateWaysSetting() == null ? 0 : vpnService.getVpnGateWaysSetting().size();
			}
			
			short server2 = counts > 1 ? VpnServiceCredential.SERVER_ROLE_SERVER2 : VpnServiceCredential.SERVER_ROLE_NONE;
			
			String updateSql_1 = "update "+VPNProfileImpl.VPN_SERVICE_CREDENTIAL +
					" set allocatedStatus = :s1, primaryRole = :s4, backupRole = :s5" +
					" where assignedClient = :s2 and allocatedStatus = :s3";
			QueryUtil.executeNativeUpdate(updateSql_1, 
					new Object[]{VpnServiceCredential.ALLOCATED_STATUS_USED, hiveApMac, 
						VpnServiceCredential.ALLOCATED_STATUS_PRE_USE, VpnServiceCredential.SERVER_ROLE_SERVER1, server2} );
			
			String updateSql_2 = "update "+VPNProfileImpl.VPN_SERVICE_CREDENTIAL +
					" set assignedClient = null, allocatedStatus = :s1, primaryRole = :s4, backupRole = :s5 " +
					" where assignedClient = :s2 and allocatedStatus = :s3";
			QueryUtil.executeNativeUpdate(updateSql_2, 
					new Object[]{VpnServiceCredential.ALLOCATED_STATUS_FREE, hiveApMac, 
						VpnServiceCredential.ALLOCATED_STATUS_PRE_REMOVE,
						VpnServiceCredential.SERVER_ROLE_NONE, VpnServiceCredential.SERVER_ROLE_NONE} );
		}else{
			String updateSql_1 = "update "+VPNProfileImpl.VPN_SERVICE_CREDENTIAL+
					" set allocatedStatus = :s1, assignedClient = null " +
					" where assignedClient = :s2 and allocatedStatus = :s3 ";
			QueryUtil.executeNativeUpdate(updateSql_1, 
					new Object[]{VpnServiceCredential.ALLOCATED_STATUS_FREE,
						hiveApMac, VpnServiceCredential.ALLOCATED_STATUS_PRE_USE} );
			
			String updateSql_2 = "update "+VPNProfileImpl.VPN_SERVICE_CREDENTIAL+
					" set allocatedStatus = :s1, assignedClient = null " +
					" where assignedClient = :s2 and allocatedStatus = :s3 ";
			QueryUtil.executeNativeUpdate(updateSql_2, 
					new Object[]{VpnServiceCredential.ALLOCATED_STATUS_USED,
						hiveApMac, VpnServiceCredential.ALLOCATED_STATUS_PRE_REMOVE} );
		}
	}

//	@SuppressWarnings("unchecked")
//	private void updateVpnCredentialStatus(String hiveApMac, String credential,
//			Long vpnServiceId, short primaryRole, short backupRole) {
//		DebugUtil
//				.configDebugWarn("HiveAP:" + hiveApMac + " credential:"
//						+ credential + ", vpnServiceId:" + vpnServiceId
//						+ ", primaryRole:" + primaryRole + ", backupRole:"
//						+ backupRole);
//		if (null == vpnServiceId) {
//			List<VpnService> vpnServices = (List<VpnService>) QueryUtil.executeQuery("select distinct bo from " + VpnService.class.getSimpleName() + " as bo join bo.vpnCredentials as joined", null, new FilterParams("joined.assignedClient", hiveApMac), null, this);
//
//			if (!vpnServices.isEmpty()) {
//				for (VpnService vpnService : vpnServices) {
//					for (VpnServiceCredential vsc : vpnService.getVpnCredentials()) {
//						if (vsc.getAssignedClient().equals(hiveApMac)) {
//							vsc.setAssignedClient("");
//							vsc.setAllocated(false);
//							vsc.setPrimaryRole(VpnServiceCredential.SERVER_ROLE_NONE);
//							vsc.setBackupRole(VpnServiceCredential.SERVER_ROLE_NONE);
//						}
//					}
//				}
//
//				try {
//					QueryUtil.bulkUpdateBos(vpnServices);
//				} catch (Exception e) {
//					log.error("updateVpnCredentialStatus", "Update VPN credential failed.", e);
//				}
//			}
//		} else {
//			List<VpnService> updatedVpnServices = new ArrayList<>();
//
//			List<VpnService> vpnServices = (List<VpnService>) QueryUtil.executeQuery("select distinct bo from " + VpnService.class.getSimpleName() + " as bo join bo.vpnCredentials as joined", null, new FilterParams("bo.id != :s1 and joined.assignedClient = :s2", new Object[] { vpnServiceId, hiveApMac }), null, this);
//
//			if (!vpnServices.isEmpty()) {
//				for (VpnService vpnService : vpnServices) {
//					for (VpnServiceCredential vsc : vpnService.getVpnCredentials()) {
//						if (vsc.getAssignedClient().equals(hiveApMac)) {
//							vsc.setAssignedClient("");
//							vsc.setAllocated(false);
//							vsc.setPrimaryRole(VpnServiceCredential.SERVER_ROLE_NONE);
//							vsc.setBackupRole(VpnServiceCredential.SERVER_ROLE_NONE);
//						}
//					}
//				}
//
//				updatedVpnServices.addAll(vpnServices);
//			}
//
//			List<VpnService> vpnServices2 = (List<VpnService>) QueryUtil.executeQuery("select distinct bo from " + VpnService.class.getSimpleName() + " as bo join bo.vpnCredentials as joined", null, new FilterParams("bo.id = :s1 and joined.credential = :s2", new Object[] { vpnServiceId, credential }), null, this);
//
//			if (!vpnServices2.isEmpty()) {
//				for (VpnService vpnService : vpnServices2) {
//					for (VpnServiceCredential vsc : vpnService.getVpnCredentials()) {
//						if (vsc.getCredential().equals(credential)) {
//							vsc.setAssignedClient(hiveApMac);
//							vsc.setAllocated(true);
//							vsc.setPrimaryRole(primaryRole);
//							vsc.setBackupRole(backupRole);
//						}
//					}
//				}
//
//				updatedVpnServices.addAll(vpnServices2);
//			}
//
//			if (!updatedVpnServices.isEmpty()) {
//				try {
//					QueryUtil.bulkUpdateBos(updatedVpnServices);
//				} catch (Exception e) {
//					log.error("updateVpnCredentialStatus", "Update VPN credential failed.", e);
//				}
//			}
//		}
//	}

	public synchronized void scanning() {
		try {
			// clear timeout object;
			clearTimeoutObject();
			//distribute vhm queue to waiting queue
			distributeUploadVhmQue();
			// update HiveAP queue
			updateHiveAPQueue();
			// update device in queue position
			updateQueuePosition();
		} catch (Exception e) {
			DebugUtil.configDebugError(
					"Exception occur in UpdateManager periodic scanning.", e);
		} catch (Error e) {
			DebugUtil.configDebugError(
					"Error occur in UpdateManager periodic scanning.", e);
		}
	}

	private void clearTimeoutObject() {
		for (Enumeration<UpdateHiveAp> e = updateList.elements(); e
				.hasMoreElements();) {
			UpdateHiveAp upAp = e.nextElement();
			String nodeId = upAp.getHiveAp().getMacAddress();
			UpdateObject item = upAp.getNextUpdateObject();
			if (null == item) {
				removeHiveApOutOfUpdateList(nodeId);
				continue;
			}
			int timeCount = item.getTimeCount();
			int maxTimeout = item.getMaxTimeout();
			short updateType = item.getUpdateType();

			DebugUtil.configDebugInfo("Update HiveAp ["
					+ upAp.getHiveAp().getHostName() + "], update type ["
					+ updateType + "] current time count is:" + timeCount
					+ ", Max timeout is:" + maxTimeout);
			if (timeCount < maxTimeout) {
				item.setTimeCount(timeCount + TIMER_INTERVAL);
			} else {
				// 1)update database;
				String msg = NmsUtil
						.getUserMessage("error.hiveAp.update.timeout");
				item.setResult(UPDATE_TIMEOUT);
				item.setDescription(msg);
				updateResultEntry(upAp, item);
				DebugUtil.configDebugInfo("Update the HiveAp ["
						+ upAp.getHiveAp().getHostName() + "], update type ["
						+ updateType + "] Timeout.");
				// 2) prepare next request;
				prepareNextRequest(upAp);
			}
		}
	}

	private void updateHiveAPQueue() {
		int capability = getUpdateCapability();
		int updateSize = updateList.size();
		int queueSize = queueList.size();
		if(updateSize > 0) {
			DebugUtil.configDebugInfo("Current update List size is:" + updateSize
					+ ", capability size:" + capability + ", MAX size is: "
					+ UPDATE_MAX_COUNT + ". Waiting queue size is:" + queueSize);
		}
		while (getUpdateCapability() < UPDATE_MAX_COUNT) {
			if (!queueList.isEmpty()) {
				UpdateHiveAp upAp = queueList.remove(0);
				if(updateList.containsKey(upAp.getHiveAp().getMacAddress())){
					addUploadVhmQue(upAp);
					log.info("updateHiveAPQueue()","Device "+upAp.getHiveAp().getHostName()+" has been in upload list, move this upload to queue end.");
				}else{
					updateList.put(upAp.getHiveAp().getMacAddress(), upAp);
				}
				generateRequest(upAp);
			} else {
				break;
			}
		}
	}

	/*
	 * The capability will ignore the case of download image begin extracting.
	 */
	private int getUpdateCapability() {
		int capability = 0;
		for (Enumeration<UpdateHiveAp> e = updateList.elements(); e
				.hasMoreElements();) {
			UpdateHiveAp upAp = e.nextElement();
			String nodeId = upAp.getHiveAp().getMacAddress();
			UpdateObject item = upAp.getNextUpdateObject();
			if (null == item) {
				continue;
			}
			if (item.getUpdateType() == AH_DOWNLOAD_IMAGE
					&& item.getDownloadRate() == 1) {
				String msg = "HiveAP:"
						+ nodeId
						+ " downloaded image, being extracting, ignore the capability in update list.";
				DebugUtil.configDebugInfo(msg);
				continue;
			}
			if (item.getUpdateType() == AH_DOWNLOAD_REBOOT) {
				String msg = "HiveAP:"
						+ nodeId
						+ " rebooting ignore the capability in update list.";
				DebugUtil.configDebugInfo(msg);
				continue;
			}
			capability++;
		}
		return capability;
	}

	/**
	 * Deal with the response event of generate script file, if successful,
	 * generate a request to capwap, or record it in database, and remove it
	 * from the list.
	 *
	 * @param scriptResponse
	 *            -
	 */
	public synchronized void dealScriptResponseEvent(
			AhConfigGeneratedEvent scriptResponse) {
		ConfigGenResultType resultType = scriptResponse
				.getConfigGenResultType();
		ConfigType configType = scriptResponse.getConfigType();
		AhConfigGenerationResult genResult = scriptResponse
				.getConfigGenResult();
		String nodeId = scriptResponse.getHiveAp().getMacAddress();
		int seqNum = scriptResponse.getSeqNum();

		try {
			UpdateHiveAp upAp = updateList.get(nodeId);
			if (null == upAp) {
				DebugUtil
						.configDebugWarn("This HiveAp Script response event which mac address["
								+ nodeId
								+ "] it not exist in the update list, ignore this event.");
				return;
			}
			UpdateObject item = upAp.getNextUpdateObject();
			short updateType = item.getUpdateType();
			int sequenceNum = item.getSequenceNum();

			if (sequenceNum != seqNum) {
				DebugUtil.configDebugWarn("This HiveAp which mac address["
						+ nodeId + "] Config Generated event sequenceNum["
						+ sequenceNum
						+ "] is not match its request sequenceNum[" + seqNum
						+ "], ignore this event.");
				return;
			}

			if (updateType == AH_DOWNLOAD_SCRIPT) {
				ScriptConfigObject scriptUpdateObj = (ScriptConfigObject) item;
				if (resultType.equals(ConfigGenResultType.SUCC) || 
						resultType.equals(ConfigGenResultType.NO_DIFF)) {
					// save result object into cache;
					AhApConfigGenerationResult resultObject = (AhApConfigGenerationResult) genResult;
					scriptUpdateObj.setResultObject(resultObject);
					// send request to CAPWAP.
					sendScriptDownloadRequest(scriptUpdateObj, configType,
							resultObject, upAp);
				} else {
					// add failed result into database
					String msg = "";
					short update_result = UPDATE_FAILED;
					if (resultType.equals(ConfigGenResultType.FAIL)) {
						msg = scriptResponse.getErrorMsg();
					} else if (resultType.equals(ConfigGenResultType.DIFF_FAIL)) {
						msg = scriptResponse.getErrorMsg();
					} else if (resultType.equals(ConfigGenResultType.DISCONNECT)) {
						msg = scriptResponse.getErrorMsg();
					} 
//					else if (resultType.equals(ConfigGenResultType.NO_DIFF)) {
//						msg = NmsUtil
//								.getUserMessage("error.hiveAp.update.script.generate.compare");
//						update_result = UPDATE_SUCCESSFUL;
//					}
					item.setResult(update_result);
					item.setDescription(msg);
					updateResultEntry(upAp, item);
					// System log
					if (update_result == UPDATE_FAILED) {
						String errMsg = scriptResponse.getErrorMsg();
						if (null != errMsg && !"".equals(errMsg.trim())) {
							HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
									HmSystemLog.FEATURE_HIVEAPS,
									MgrUtil.getUserMessage("hm.system.log.update.manager.upload.configuration.error",new String[]{NmsUtil.getOEMCustomer().getAccessPonitName(),nodeId})
									+ errMsg);
						}
						prepareNextRequest(upAp);
					} else {
						// 1) remove from the current update list;
						upAp.removeFirstUpdateObject();
						UpdateObject nextItem = upAp.getNextUpdateObject();
						if (null == nextItem) {
							removeHiveApOutOfUpdateList(upAp.getHiveAp()
									.getMacAddress());
							return;
						}
						// 2) generate next request
						generateRequest(upAp);
					}
				}
			} else if (updateType == AH_DOWNLOAD_PSK) {
				ScriptConfigObject scriptUpdateObj = (ScriptConfigObject) item;
				if (resultType.equals(ConfigGenResultType.SUCC)) {
					// send request to CAPWAP.
					AhUserConfigGenerationResult resultObject = (AhUserConfigGenerationResult) genResult;
					sendPskDownloadRequest(scriptUpdateObj, configType,
							resultObject, upAp);
				} else {
					// System log
					String errMsg = scriptResponse.getErrorMsg();
					if (null != errMsg && !"".equals(errMsg.trim())) {
						HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
								HmSystemLog.FEATURE_HIVEAPS,
								MgrUtil.getUserMessage("hm.system.log.update.manager.upload.database.error",new String[]{NmsUtil.getOEMCustomer().getAccessPonitName(),nodeId})
								+ errMsg);
					}
					// add failed result into database
					String msg = "";
					short update_result = UPDATE_FAILED;
					if (resultType.equals(ConfigGenResultType.FAIL)) {
						msg = scriptResponse.getErrorMsg();
					} else if (resultType.equals(ConfigGenResultType.DIFF_FAIL)) {
						msg = scriptResponse.getErrorMsg();
					} else if (resultType.equals(ConfigGenResultType.DISCONNECT)) {
						msg = scriptResponse.getErrorMsg();
					} else if (resultType.equals(ConfigGenResultType.NO_DIFF)) {
						msg = NmsUtil
								.getUserMessage("error.hiveAp.update.psk.generate.compare");
						update_result = UPDATE_SUCCESSFUL;
					}
					item.setResult(update_result);
					item.setDescription(msg);
					updateResultEntry(upAp, item);
					// prepare next request;
					if (update_result == UPDATE_FAILED) {
						prepareNextRequest(upAp);
					} else {
						// 1) remove from the current update list;
						upAp.removeFirstUpdateObject();
						UpdateObject nextItem = upAp.getNextUpdateObject();
						if (null == nextItem) {
							removeHiveApOutOfUpdateList(upAp.getHiveAp()
									.getMacAddress());
							return;
						}
						// 2) generate next request
						generateRequest(upAp);
					}
				}
			} else {
				DebugUtil.configDebugWarn("The HiveAp which mac address["
						+ nodeId
						+ "] in the update list is current do update ["
						+ updateType
						+ "], ignore this Script update response event.");
			}
		} catch (Exception e) {
			DebugUtil
					.configDebugWarn(
							"Error occur when deal with this HiveAp Script response event which mac address["
									+ nodeId + "].", e);
		}
	}

	/**
	 * Deal with the response event of generate bootstrap file, if successful,
	 * generate a request to capwap, or record it in database, and remove it
	 * from the list.
	 *
	 * @param bootstrapResponse
	 *            -
	 */
	public synchronized void dealBootstrapResponseEvent(
			AhBootstrapGeneratedEvent bootstrapResponse) {
		ConfigGenResultType resultType = bootstrapResponse
				.getConfigGenResultType();
		String nodeId = bootstrapResponse.getHiveAp().getMacAddress();
		int seqNum = bootstrapResponse.getSeqNum();

		try {
			UpdateHiveAp upAp = updateList.get(nodeId);

			if (null == upAp) {
				DebugUtil
						.configDebugWarn("This HiveAp Bootstrap response event which mac address["
								+ nodeId
								+ "] it not exist in the update list, ignore this event.");
				return;
			}

			UpdateObject item = upAp.getNextUpdateObject();
			short objUpdateType = item.getUpdateType();
			int sequenceNum = item.getSequenceNum();

			if (sequenceNum != seqNum) {
				DebugUtil.configDebugWarn("This HiveAp which mac address["
						+ nodeId + "] Bootstrap Generated event sequenceNum["
						+ sequenceNum
						+ "] is not match its request sequenceNum[" + seqNum
						+ "], ignore this event.");
				return;
			}

			if (objUpdateType == AH_DOWNLOAD_BOOTSTRAP) {
				BootstrapConfigObject bootObj = (BootstrapConfigObject) item;
				if (resultType.equals(ConfigGenResultType.SUCC)) {
					// send request to CAPWAP.
					sendBootstrapDownloadRequest(bootObj, upAp);
				} else {
					// add failed result into database
					String msg = bootstrapResponse.getErrorMsg();
					item.setResult(UPDATE_FAILED);
					item.setDescription(msg);
					updateResultEntry(upAp, item);
					// prepare next request;
					prepareNextRequest(upAp);
				}
			} else {
				DebugUtil.configDebugWarn("The HiveAp which mac address["
						+ nodeId
						+ "] in the update list is current do update ["
						+ objUpdateType
						+ "], ignore this Bootstrap update response event.");
			}
		} catch (Exception e) {
			DebugUtil
					.configDebugWarn(
							"Error occur when deal with this HiveAp Bootstrap response event which mac address["
									+ nodeId + "].", e);
		}
	}
	
	/**
	 * Deal with the response event of request Cloud authentication certificate, if successful,
	 * generate a request to capwap, or record it in database, and remove it
	 * from the list.
	 *
	 * @param cloudResponse
	 *            -
	 */
	public synchronized void dealCloudAuthCAResponseEvent(
			AhCloudAuthCAGenerateEvent cloudResponse) {
	    HmCloudAuthCertResult result = cloudResponse.getResult();
		String nodeId = cloudResponse.getHiveAp().getMacAddress();
		int seqNum = cloudResponse.getSeqNum();

		try {
			UpdateHiveAp upAp = updateList.get(nodeId);

			if (null == upAp) {
				DebugUtil
						.configDebugWarn("This Device CloudAuth cert response event which mac address["
								+ nodeId
								+ "] it not exist in the update list, ignore this event.");
				return;
			}

			UpdateObject item = upAp.getNextUpdateObject();
			short objUpdateType = item.getUpdateType();
			int sequenceNum = item.getSequenceNum();

			if (sequenceNum != seqNum) {
				DebugUtil.configDebugWarn("This Device which mac address["
						+ nodeId + "] CloudAuth cert Generated event sequenceNum["
						+ sequenceNum
						+ "] is not match its request sequenceNum[" + seqNum
						+ "], ignore this event.");
				return;
			}
			
			item.setDescription(result.getResultMessage());
			UpdateCAStatus status = result.getStatus();
			if (UpdateCAStatus.SUCCESS.equals(status) ||
					UpdateCAStatus.NOUPDATE.equals(status)) {
				item.setResult(UPDATE_SUCCESSFUL);
			}else{
				item.setResult(UPDATE_FAILED);
			}
			updateResultEntry(upAp, item);

			if (objUpdateType == AH_DOWNLOAD_CLOUDAUTH_CERTIFICATE) {
				if (UpdateCAStatus.SUCCESS.equals(status)) {
					// send request to CAPWAP.
					sendCloudAuthCertDownloadRequest(cloudResponse, upAp);
				}else if(UpdateCAStatus.NOUPDATE.equals(status)){
					// 1) remove from the current update list;
					upAp.removeFirstUpdateObject();
					UpdateObject nextItem = upAp.getNextUpdateObject();
					if (null == nextItem) {
						removeHiveApOutOfUpdateList(upAp.getHiveAp().getMacAddress());
						return;
					}
					// 2) generate next request;
					generateRequest(upAp);
				} else {
					// add failed result into database
					String msg = result.getResultMessage();
					item.setResult(UPDATE_FAILED);
					item.setDescription(msg);
					updateResultEntry(upAp, item);
					// prepare next request;
					prepareNextRequest(upAp);
				}
			} else {
				DebugUtil.configDebugWarn("The HiveAp which mac address["
						+ nodeId
						+ "] in the update list is current do update ["
						+ objUpdateType
						+ "], ignore this CloudAuth cert update response event.");
			}
		} catch (Exception e) {
			DebugUtil
					.configDebugWarn(
							"Error occur when deal with this Device CloudAuth cert response event which mac address["
									+ nodeId + "].", e);
		}
	}

	private void sendPskDownloadRequest(ScriptConfigObject scriptObj,
			ConfigType configType, AhUserConfigGenerationResult result,
			UpdateHiveAp upAp) {
		try {
			String softVer = upAp.getHiveAp().getSoftVer();
			// generate a CAPWAP request to do upload script;
			if (ConfigType.USER_FULL.equals(configType)) {
				if (NmsUtil.compareSoftwareVersion(softVer, "3.3.1.0") >= 0) {
					String[] clis = scriptObj.getClis();
					sendRequest(upAp, clis);
				} else {// special for 3.2 or lower versions
					String[] preClis = scriptObj.getClis();
					String realScript = result.getGenClis();
					if (null != preClis && preClis.length > 0) {
						String rebootCli = preClis[preClis.length - 1]
								.startsWith("reboot ") ? preClis[preClis.length - 1]
								: "";
						realScript += "\n" + rebootCli;
					}

					boolean saveServerFiles = scriptObj.isSaveServerFiles();
					String clis[] = getDeltaCliArrayForPsk(realScript,
							saveServerFiles, upAp.getHiveAp().getSoftVer());
					sendRequest(upAp, clis);
				}
			} else {
				String realScript = result.getGenClis();
				boolean saveServerFiles = scriptObj.isSaveServerFiles();
				String clis[] = getDeltaCliArrayForPsk(realScript,
						saveServerFiles, upAp.getHiveAp().getSoftVer());
				sendRequest(upAp, clis);
			}
		} catch (Exception e) {
			DebugUtil
					.configDebugError(
							"UpdateManager.sendPskDownloadRequest() catch exception",
							e);
		}
	}

	private void sendScriptDownloadRequest(ScriptConfigObject scriptObj,
			ConfigType configType, AhApConfigGenerationResult result,
			UpdateHiveAp upAp) {
		try {
			// generate a CAPWAP request to do upload script;
			if (ConfigType.AP_FULL.equals(configType)) {
				String[] clis = scriptObj.getClis();
				sendRequest(upAp, clis);
			} else {
				String realScript = result.getGenClis();
				boolean saveServerFiles = scriptObj.isSaveServerFiles();
				int configVer = upAp.getHiveAp().getNewConfigVerNum();
				String clis[] = getDeltaCliArray(realScript, saveServerFiles,
						configVer);
				sendRequest(upAp, clis);
			}
		} catch (Exception e) {
			DebugUtil
					.configDebugError(
							"UpdateManager.sendScriptDownloadRequest() catch exception",
							e);
		}
	}

	private void sendBootstrapDownloadRequest(BootstrapConfigObject bootObj,
			UpdateHiveAp upAp) {
		try {
			String[] clis = bootObj.getClis();
			sendRequest(upAp, clis);
		} catch (Exception e) {
			DebugUtil
					.configDebugWarn(
							"UpdateManager.sendBootstrapDownloadRequest() catch exception",
							e);
		}
	}
	
	private void sendCloudAuthCertDownloadRequest(AhCloudAuthCAGenerateEvent cloudResponse,
			UpdateHiveAp upAp) {
		try {
			String[] clis = cloudResponse.getClis();
			sendRequest(upAp, clis);
		} catch (Exception e) {
			DebugUtil
					.configDebugWarn(
							"UpdateManager.sendCloudAuthCertDownloadRequest() catch exception",
							e);
		}
	}

	// split by '\n', then add back of '\n';
	private String[] getDeltaCliArrayForPsk(String cliMsg,
			boolean saveServerFiles, String version) {
		String clis[] = cliMsg.split("\n");

		int length = saveServerFiles ? clis.length + 2 : clis.length + 1;

		String[] newClis = new String[length];
		for (int i = 0; i < clis.length; i++) {
			newClis[i] = clis[i] + "\n";
		}

		newClis[clis.length] = AhCliFactory.saveUserConfig(version);
		if (saveServerFiles) {
			newClis[clis.length + 1] = AhCliFactory.getSaveServerFilesCli();
		}
		return newClis;
	}

	// split by '\n', then add back of '\n';
	private String[] getDeltaCliArray(String cliMsg, boolean saveServerFiles,
			int configVer) {
		String clis[] = cliMsg.split("\n");

		int length = saveServerFiles ? clis.length + 3 : clis.length + 2;

		String[] newClis = new String[length];
		for (int i = 0; i < clis.length; i++) {
			newClis[i] = clis[i] + "\n";
		}

		newClis[clis.length] = AhCliFactory.configVerNum(configVer);
		newClis[clis.length + 1] = AhCliFactory.getSaveConfigCli();

		if (saveServerFiles) {
			newClis[clis.length + 2] = AhCliFactory.getSaveServerFilesCli();
		}
		return newClis;
	}

	/**
	 * Deal with the progress event of generate script file, it just update the
	 * entry current status in the database
	 *
	 * @param progress_event
	 *            -
	 */
	public synchronized void dealScriptProgressEvent(
			AhConfigGenerationProgressEvent progress_event) {
		int sequenceNum = progress_event.getSeqNum();
		String nodeId = progress_event.getHiveAp().getMacAddress();
		ConfigGenerationProgress type = progress_event.getConfigGenProgress();

		try {
			UpdateHiveAp upAp = updateList.get(nodeId);

			if (null == upAp) {
				return;
			}

			UpdateObject item = upAp.getNextUpdateObject();
			int objSequenceNum = item.getSequenceNum();

			if (sequenceNum != objSequenceNum) {
				DebugUtil.configDebugWarn("This HiveAp which mac address["
						+ nodeId + "] update progress event sequenceNum["
						+ sequenceNum
						+ "] is not match its request sequenceNum["
						+ objSequenceNum + "], ignore this event.");
				return;
			}
			if (type == ConfigGenerationProgress.FETCH_HIVEAP_CONFIG) {
				item.setState(PROCESS_RETRIEVING);
				updateResultEntry(upAp, item);
			} else if (type == ConfigGenerationProgress.GENERATE_HM_CONFIG) {
				item.setState(PROCESS_GENERATING);
				updateResultEntry(upAp, item);
			} else if (type == ConfigGenerationProgress.COMPARE_CONFIGS) {
				item.setState(PROCESS_COMPARING);
				updateResultEntry(upAp, item);
			}
		} catch (Exception e) {
			DebugUtil
					.configDebugWarn(
							"Error occur when deal with this HiveAp Script process event which mac address["
									+ nodeId + "].", e);
		}
	}

	public synchronized void dealCliResponse(BeCliEvent response) {
		byte resultType = response.getResult();
		String nodeId = response.getApMac();
		int serialNum = response.getSerialNum();

		try {
			UpdateHiveAp upAp = updateList.get(nodeId);
			if (null == upAp) {
				DebugUtil
						.configDebugWarn("This HiveAp update response event which mac address["
								+ nodeId
								+ "] is not exist in the update list, ignore this event.");
				return;
			}

			UpdateObject item = upAp.getNextUpdateObject();
			int reqSerialNum = item.getSerialNum();

			if (serialNum != reqSerialNum) {
				DebugUtil.configDebugWarn("This HiveAp which mac address["
						+ nodeId + "] update response event serialNum["
						+ serialNum + "] is not match its request serialNum["
						+ reqSerialNum + "], ignore this event.");
				return;
			}

			if (resultType == BeCommunicationConstant.RESULTTYPE_SUCCESS) {
				// request is successful;
				item.setState(PROCESS_LOADING);
				updateResultEntry(upAp, item);

				// the flag indicate if need to turn on checkout flag for next
				// discovery event used.
				// use APConnectionEvent instead
				// boolean flag = isDeltaConfiguration(item);
				// if (flag) {
				// BoMgmt.getHiveApMgmt().updateStateToDelta(upAp.getHiveAp(),
				// sequenceNum);
				// }
			} else {
				String msg = UpdateUtil.getCommonResponseMessage(resultType);
				short update_result = UPDATE_FAILED;
//				if (resultType == BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT) {
//					update_result = UPDATE_TIMEOUT;
//				}
				if(UpdateUtil.isStagedStatus(resultType)){
					update_result = UPDATE_STAGED;
					msg = NmsUtil.getUserMessage("error.capwap.server.nofsm.staged");
				}
				item.setResult(update_result);
				item.setDescription(msg);
				updateResultEntry(upAp, item);
				// prepare next request;
				prepareNextRequest(upAp);
			}
		} catch (Exception e) {
			DebugUtil
					.configDebugWarn(
							"Error occur when deal with this HiveAp update response event which mac address["
									+ nodeId + "].", e);
		}
	}

	public synchronized void dealFileDownloadProgressEvent(
			BeCapwapFileDownProgressEvent event) {
		short downloadType = event.getDownloadType();
		String nodeId = event.getApMac();

		try {
			UpdateHiveAp upAp = updateList.get(nodeId);
			if (null == upAp) {
				DebugUtil
						.configDebugWarn("This HiveAp update progress event which mac address["
								+ nodeId
								+ "] is not exist in the update list, ignore this event.");
				return;
			}

			UpdateObject item = upAp.getNextUpdateObject();
			short updateType = item.getUpdateType();
			if (updateType == downloadType) {
				int finishedSize = event.getFinishSize();
				int lastFinishedSize = item.getLastfinishedSize();
				int summarySize = item.getFileSize();
				DebugUtil.configDebugInfo("This HiveAp which mac address["
						+ nodeId + "], updateType [" + updateType
						+ "] get event finished size:" + finishedSize
						+ ", file summary size:" + summarySize);
				if (0 != summarySize) {
//					boolean needUpdate = true;
					float downloadRate = finishedSize/(float)summarySize;
					if(finishedSize == lastFinishedSize){
						//download rate no change, image download finished
						item.setState(PROCESS_EXTRACTING);
						item.setDownloadRate(1.0f);
					}else if(downloadRate < 1){
						item.setState(PROCESS_LOADING);
						item.setDownloadRate(downloadRate);
					}else{
						item.setState(PROCESS_EXTRACTING);
						item.setDownloadRate(1.0f);
					}
					item.setLastfinishedSize(finishedSize);
					// update status when size change
					if (finishedSize != lastFinishedSize) {
						updateResultEntry(upAp, item);
					}
				}
			} else {
				DebugUtil
						.configDebugWarn("The update type["
								+ downloadType
								+ "] of HiveAp update progress event which mac address["
								+ nodeId
								+ "] is not match which is in the update list type["
								+ updateType + "].");
			}
		} catch (Exception e) {
			DebugUtil
					.configDebugWarn(
							"Error occur when deal with this HiveAp update progress event which mac address["
									+ nodeId + "].", e);
		}
	}

	public synchronized void dealCliFinishEvent(BeCapwapCliResultEvent event) {
		String nodeId = event.getApMac();
		int sequenceNum = event.getSequenceNum();
		byte result = event.getCliResult();
		UpdateHiveAp upAp = updateList.get(nodeId);
		if (null == upAp) {
			DebugUtil
					.configDebugWarn("This HiveAp update finish event which mac address["
							+ nodeId
							+ "] is not exist in the update list, ignore this event.");
			return;
		}
		UpdateObject item = upAp.getNextUpdateObject();

		int reqSequenceNum = item.getSequenceNum();
		if (sequenceNum != reqSequenceNum) {
			DebugUtil.configDebugWarn("This HiveAp which mac address[" + nodeId
					+ "] update finish event sequenceNum[" + sequenceNum
					+ "] is not match its request sequenceNum["
					+ reqSequenceNum + "], ignore this event.");
			return;
		}

		short updateType = item.getUpdateType();
		boolean isCanceling = item.isCanceling();
		String msg;
		short update_result = UPDATE_FAILED;// by default
		if (updateType == AH_DOWNLOAD_IMAGE && isCanceling
				&& result != BeCommunicationConstant.CLIRESULT_SUCCESS) {
			// In case of Canceling image upload process, whatever the
			// cancel result, this CLI finished result will received always,
			// it will conflict. so here needs an exceptive process.
			msg = NmsUtil.getUserMessage("info.hiveAp.update.canceled");
			update_result = UPDATE_CANCELED;
		} else {
			// generic process
			msg = UpdateUtil.getCliResultMessage(updateType, event);
			if (result == BeCommunicationConstant.CLIRESULT_SUCCESS /*|| event.isFailedContinue(upAp.getHiveAp().getHiveApModel())*/) {
				update_result = UPDATE_SUCCESSFUL;
			}
		}
		item.setResult(update_result);
		item.setDescription(msg);

		try {
			if (updateType == AH_DOWNLOAD_SCRIPT) {
				// The result of script or delta config uploading should be
				// informed to other modules e.g. configuration even if the
				// corresponding HiveApUpdateResult persistent object has
				// been removed out of database.
				doAfterScriptUpload(upAp, item, upAp.getHiveAp(), result);
			} else if (updateType == AH_DOWNLOAD_PSK) {
				doAfterPskUpload(item, upAp.getHiveAp(), result);
			}
			updateResultEntry(upAp, item);// must after doAfterScriptUpload
		} catch (Exception e) {
			DebugUtil.configDebugWarn(
					"Error occur when deal with this HiveAp update finish event which mac address["
							+ nodeId + "].", e);
		}

		if(result == BeCommunicationConstant.CLIRESULT_SUCCESS && 
				item.getUpdateType() == AH_DOWNLOAD_REBOOT && 
				AhCliFactory.isRebootImmediately(item.getCliString()) ){
			//reboot cli successful, update device status to disconnect and waiting device reconnect.
			try{
				QueryUtil.updateBo(HiveAp.class, "connected = :s1, connectStatus = :s2", 
						new FilterParams("id = :s3", new Object[]{false, HiveAp.CONNECT_DOWN, upAp.getHiveAp().getId()}));
			}catch(Exception e){
				log.error("dealCliFinishEvent", 
						"Reboot Device successful, but update device status to disconnect failed.", e);
			}
			return;
		}else if(result == BeCommunicationConstant.CLIRESULT_SUCCESS /*|| event.isFailedContinue(upAp.getHiveAp().getHiveApModel())*/) {
			// 1) remove from the current update list;
			upAp.removeFirstUpdateObject();
			UpdateObject nextItem = upAp.getNextUpdateObject();
			if (null == nextItem) {
				removeHiveApOutOfUpdateList(upAp.getHiveAp().getMacAddress());
				return;
			}
			// 2) generate next request;
			generateRequest(upAp);
		} else {
			// prepare next requests;
			prepareNextRequest(upAp);
		}
	}

	private void doAfterPskUpload(UpdateObject obj, HiveAp hiveAp, byte result) {
		ScriptConfigObject configObj = (ScriptConfigObject) obj;
		short scriptType = configObj.getScriptType();
		// script config, send result event.
		AhConfigUpdatedEvent configUpdateResultEvent = new AhConfigUpdatedEvent(
				hiveAp);
		configUpdateResultEvent.setConfigType(transferType(AH_DOWNLOAD_PSK,
				scriptType));
		configUpdateResultEvent.setUpdateResult(result);
		AhAppContainer.getBeConfigModule().getConfigMgmt().add(
				configUpdateResultEvent);
	}

	private void doAfterScriptUpload(UpdateHiveAp upAp, UpdateObject obj,
			HiveAp hiveAp, byte result) {
		ScriptConfigObject configObj = (ScriptConfigObject) obj;
		AhApConfigGenerationResult generatetionResult = configObj
				.getResultObject();
		short scriptType = configObj.getScriptType();
		// script config, send result event.
		AhConfigUpdatedEvent configUpdateResultEvent = new AhConfigUpdatedEvent(
				hiveAp);
		configUpdateResultEvent.setConfigType(transferType(AH_DOWNLOAD_SCRIPT,
				scriptType));
		configUpdateResultEvent.setUpdateResult(result);
		AhAppContainer.getBeConfigModule().getConfigMgmt().add(
				configUpdateResultEvent);

		// while successfully, update HiveAP database
		if (result == BeCommunicationConstant.CLIRESULT_SUCCESS) {
			try {
				String cfgAdmin = null, cfgPsd = null;
				if (null != generatetionResult) {
					cfgAdmin = generatetionResult.getRootAdmin();
					cfgPsd = generatetionResult.getRootPassword();
				}

				HiveAp ap = BoMgmt.getHiveApMgmt().updateAdminDtlsInfo(hiveAp,
						cfgAdmin, cfgPsd);

				// send CAPWAP Dtls event
				BeCapwapDTLSConfigEvent d_event = new BeCapwapDTLSConfigEvent();
				List<HiveAp> hiveApList = new ArrayList<>();
				if (null == ap) {
					// CAPWAP dtls
					int cfgKeyId = hiveAp.getKeyId();
					String cfgPassPhrase = hiveAp.getPassPhrase();
					hiveAp.setCurrentPassPhrase(cfgPassPhrase);
					hiveAp.setCurrentKeyId(cfgKeyId);
					ap = hiveAp;
				}
				hiveApList.add(ap);
				d_event.setApList(hiveApList);
				// if it is full script update, do not disconnect ap;
				if (COMPLETE_SCRIPT == scriptType) {
					d_event
							.setOperationType(BeCommunicationConstant.DTLSOPERTYPE_NOTREMOVEAPCONNECTION);
					DebugUtil
							.configDebugInfo("Complete Script upload, set event operation type to 'not remove AP connection.'");
				}
				d_event.buildPacket();
				upAp.setDtlsEvent(d_event);// store this event, send it
				// until upload finished
				// HmBeCommunicationUtil.sendRequest(d_event);
			} catch (Exception e) {
				DebugUtil
						.configDebugWarn(
								"Error occur while HiveAp script update finished, and update hiveAp database which mac address["
										+ hiveAp.getMacAddress() + "].", e);
			}
		}
	}

	private ConfigType transferType(short updateType, short scriptType) {
		if (updateType == AH_DOWNLOAD_SCRIPT) {
			switch (scriptType) {
			case DELTA_SCRIPT_LAST:
				return ConfigType.AP_DELTA;
			case DELTA_SCRIPT_RUNNING:
				return ConfigType.AP_AUDIT;
			case COMPLETE_SCRIPT:
			default:
				return ConfigType.AP_FULL;
			}
		} else if (updateType == AH_DOWNLOAD_PSK) {
			switch (scriptType) {
			case DELTA_SCRIPT_LAST:
				return ConfigType.USER_DELTA;
			case DELTA_SCRIPT_RUNNING:
				return ConfigType.USER_AUDIT;
			case COMPLETE_SCRIPT:
			default:
				return ConfigType.USER_FULL;
			}
		}
		return null;
	}

	public synchronized void dealReConnectionEvent(AhDiscoveryEvent event) {
		String nodeId = event.getHiveAp().getMacAddress();
		int configVer = event.getHiveAp().getConfigVer();
		UpdateHiveAp upAp = updateList.get(nodeId);

		try {
			if (null == upAp) {
				// Do not do config version comparison.
			//	compareConfigVer(event);
			} else {
				UpdateObject item = upAp.getNextUpdateObject();
				int expectCfgVer = upAp.getHiveAp().getNewConfigVerNum();
				short updateType = item.getUpdateType();

				// by default
				// String msg = NmsUtil
				// .getUserMessage("error.hiveAp.update.capwap.error");
				// short update_result = UPDATE_FAILED;
				// byte result = BeCommunicationConstant.CLIRESULT_FAIL;

				// Only deal with the success case, ignore failed case in this
				// event.
				if (updateType == AH_DOWNLOAD_SCRIPT && expectCfgVer == configVer) {
					// new configuration take effect;
					String msg = UpdateUtil.getCustomizedSucDesc(updateType);
					short update_result = UPDATE_SUCCESSFUL;

					item.setResult(update_result);
					item.setDescription(msg);

					doAfterScriptUpload(upAp, item, upAp.getHiveAp(),
							BeCommunicationConstant.CLIRESULT_SUCCESS);
					updateResultEntry(upAp, item);// must after doAfterScriptUpload

					// if (result == BeCommunicationConstant.CLIRESULT_SUCCESS) {
					// 1) remove from the current update list;
					upAp.removeFirstUpdateObject();
					UpdateObject nextItem = upAp.getNextUpdateObject();
					if (null == nextItem) {
						removeHiveApOutOfUpdateList(upAp.getHiveAp()
								.getMacAddress());
						return;
					}
					// 2) generate next request;
					generateRequest(upAp);
					// } else {
					// // prepare next requests;
					// prepareNextRequest(upAp);
					// }
			//	} else {
					// Do not do config version comparison.
			//		compareConfigVer(event);
				}
			}
		} catch (Exception e) {
			DebugUtil.configDebugError(
					"Error occur when deal with this HiveAp ReConnectionEvent which mac address["
							+ nodeId + "].", e);
		}
	}

	private UpdateHiveAp getFromQueueList(String macAddress) {
		for(List<UpdateHiveAp> vhmList : uploadVhmQue.values()){
			for(UpdateHiveAp upAp : vhmList){
				if (macAddress.equals(upAp.getHiveAp().getMacAddress())) {
					return upAp;
				}
			}
		}
		for (UpdateHiveAp upAp : queueList) {
			if (macAddress.equals(upAp.getHiveAp().getMacAddress())) {
				return upAp;
			}
		}
		return null;
	}

	private UpdateHiveAp getFromUpdateList(String macAddress) {
		return updateList.get(macAddress);
	}

	public synchronized List<CancelObject> operateCancelImage(
			List<CancelObject> list) {
		if (null != list) {
			for (CancelObject item : list) {
				String macAddress = item.getHiveAp().getMacAddress();
				short updateType = item.getUpdateType();
				UpdateHiveAp upAp = getFromQueueList(macAddress);
				if (null != upAp) {
					DebugUtil
							.configDebugInfo("Find UpdateHiveAP from Waiting Queue for MacAddress:"
									+ macAddress);
					UpdateObject object = upAp.getUpdateObject(updateType);
					if (null != object) {
						object.setResult(UPDATE_CANCELED);
						object.setDescription(NmsUtil
								.getUserMessage("info.hiveAp.update.canceled"));
						updateResultEntry(upAp, object);
						// remove from UpdateHiveAP
						upAp.removeUpdateObject(object);
						item
								.setExecuteResult(CancelObject.RESULT_TRUE_CANCELED);
						UpdateObject nextItem = upAp.getNextUpdateObject();
						if (null == nextItem) {
							removeHiveApOutOfQueueList(upAp);
						}
					}
				} else {
					upAp = getFromUpdateList(macAddress);
					if (null == upAp) {
						continue;
					}
					// check version
					boolean passed = MapNodeAction.checkVersionSupported(item
							.getHiveAp(), "3.2.0.0");
					if (!passed) {
						item
								.setExecuteResult(CancelObject.RESULT_FAILED_UNSUPPORT_CANCELE);
						continue;
					}
					UpdateObject object = upAp.getUpdateObject(updateType);
					if (null == object) {
						continue;
					}
					// send request to cancel operation
					try {
						BeAbortEvent event = new BeAbortEvent();
						event.setAp(item.getHiveAp());
						event.setAbortType(UpdateUtil
								.getUploadProtocolType(object.getCliString()));
						event.buildPacket();
						int result = HmBeCommunicationUtil.sendRequest(event);
						if (result > 0) {
							item
									.setExecuteResult(CancelObject.RESULT_TRUE_CANCELING);
							object.setCanceling(true);
							object.setCancelSerial(result);
						} else {
							item.setExecuteResult(CancelObject.RESULT_FAILED);
						}
					} catch (Exception e) {
						DebugUtil
								.configDebugError("Build Cancel request failed for HiveAP:"
										+ macAddress);
						item.setExecuteResult(CancelObject.RESULT_FAILED);
					}
				}
			}
		}
		return list;
	}
	
	public synchronized List<CancelObject> operateCancelStaged(List<CancelObject> list){
		if (null == list){
			return list;
		}
		for(CancelObject item : list){
			HiveApUpdateResult updateResult = item.getUpdateResult();
			updateResult.setResult(UpdateParameters.UPDATE_ABORT);
			updateResult.setActionType(UpdateParameters.ACTION_RETRY);
			try{
				QueryUtil.updateBo(updateResult);
				item.setExecuteResult(CancelObject.RESULT_TRUE_CANCELED);
			}catch(Exception ex){
				item.setExecuteResult(CancelObject.RESULT_FAILED);
			}
		}
		return list;
	}

	/**
	 * While cancel the process successfully, remove the update request from the
	 * update list, also update the entry in database. While cancel the process
	 * failed, just set the description only.Currently, the cancel function only
	 * support for image upload.
	 *
	 * @param event
	 *            -
	 * @throws Exception
	 *             -
	 */
	public synchronized void dealCancelOperationEvent(BeAbortEvent event) {
		String macAddress = event.getApMac();
		byte result = event.getResult();
		int abortResult = event.getAbortResult();
		int serialNum = event.getSerialNum();
		DebugUtil.configDebugInfo("Cancel operation for HiveAP:" + macAddress
				+ ", result:" + result + ", abort result:" + abortResult
				+ ", serialNum:" + serialNum);

		UpdateHiveAp upAp = updateList.get(macAddress);// search from update
		// list;
		if (null != upAp && serialNum > 0) {
			DebugUtil
					.configDebugInfo("Find UpdateHiveAP from UpdateList for MacAddress:"
							+ macAddress);
			UpdateObject object = upAp.getUpdateObjectForCancel(serialNum);
			if (null != object) {
				if (result == BeCommunicationConstant.RESULTTYPE_SUCCESS
						&& (abortResult == BeAbortEvent.RESULT_SUCCESS || abortResult == BeAbortEvent.RESULT_NO_PROCESS)) {
					object.setResult(UPDATE_CANCELED);
					object.setDescription(NmsUtil
							.getUserMessage("info.hiveAp.update.canceled"));
					updateResultEntry(upAp, object);
					// remove from UpdateHiveAP
					upAp.removeUpdateObject(object);
					UpdateObject nextItem = upAp.getNextUpdateObject();
					if (null == nextItem) {
						removeHiveApOutOfUpdateList(upAp.getHiveAp()
								.getMacAddress());
					}
				} else {
					String msg = NmsUtil
							.getUserMessage("info.hiveAp.update.canceled.failed");
					if (result != BeCommunicationConstant.RESULTTYPE_SUCCESS) {
						msg = UpdateUtil.getCommonResponseMessage(result);
					} else {
						if (abortResult == BeAbortEvent.RESULT_WRITE_FLASH) {
							msg = NmsUtil
									.getUserMessage("info.hiveAp.update.canceled.writeFlash");
						}
					}
					object.setCanceling(false);
					object.setCancelSerial(0);
					object.setDescription(msg);
					updateResultEntry(upAp, object);
				}
			}
		}
	}
	
	/**
	 * when device reconnect change reboot status, if exists CLI error waiting trap.
	 * 
	 * @param rebootEvent
	 * @throws Exception 
	 */
	
	public synchronized void dealDeviceRebootResultEvent(AhDeviceRebootResultEvent rebootEvent) throws Exception{
		String deviceMac = rebootEvent.getDeviceMac();
		short operation = rebootEvent.getOperation();
		if(operation == AhDeviceRebootResultEvent.OPERATION_UPDATE_RESULT){
			UpdateHiveAp upAp = updateList.get(deviceMac);
			if(upAp == null){
				return;
			}
			
			UpdateObject updateObj = upAp.getNextUpdateObject();
			if(updateObj == null){
				return;
			}
			
			short resultType = rebootEvent.getResultType();
			switch(resultType){
			case AhDeviceRebootResultEvent.RESULT_TYPE_SUCCESSFUL:
				updateObj.setResult(REBOOT_SUCCESSFUL);
				String timeStr = rebootTimeFormat.format(new Date(System.currentTimeMillis()));
				updateObj.setDescription(NmsUtil.getUserMessage("geneva_06.info.hiveAp.update.reboot.successful", new String[]{timeStr}));
				break;
			case AhDeviceRebootResultEvent.RESULT_TYPE_CLI_ERROR:
				updateObj.setResult(WARNING);
				updateObj.setDescription(NmsUtil.getUserMessage("geneva_06.info.hiveAp.update.reboot.config.error"));
				break;
			case AhDeviceRebootResultEvent.RESULT_TYPE_CONFIG_ROLLBACK:
				updateObj.setResult(WARNING);
				updateObj.setDescription(NmsUtil.getUserMessage("geneva_06.info.hiveAp.update.reboot.config.rollback"));
				break;
			case AhDeviceRebootResultEvent.RESULT_TYPE_IMAGE_ROLLBACK:
				updateObj.setResult(WARNING);
				updateObj.setDescription(NmsUtil.getUserMessage("geneva_06.info.hiveAp.update.reboot.image.rollback"));
				break;
			}
			
			this.updateResultEntry(upAp, updateObj);
			removeHiveApOutOfUpdateList(deviceMac);
			
			if(resultType == AhDeviceRebootResultEvent.RESULT_TYPE_SUCCESSFUL){
				BoMgmt.getHiveApMgmt().updateConfigurationIndicationForReboot(upAp.getHiveAp());
			}
		}else if(operation == AhDeviceRebootResultEvent.OPERATION_UPDATE_MESSAGE){
			//get error CLIs from alarm.
			List<HiveApUpdateResult> resultList = QueryUtil.executeQuery(HiveApUpdateResult.class, new SortParams("id", false), 
					new FilterParams("nodeId = :s1 and updateType = :s2 and result = :s3", new Object[]{deviceMac, AH_DOWNLOAD_REBOOT, WARNING}));
			if(resultList != null && !resultList.isEmpty()){
				HiveApUpdateResult resultObj = resultList.get(0);
				HiveApUpdateItem rebootItem = resultObj.getItem(AH_DOWNLOAD_REBOOT);
				String message = rebootEvent.getMessage();
				message = rebootItem.getDescription() + message;
				rebootItem.setDescription(message);
				
				QueryUtil.updateBo(resultObj);
			}
		}
	}

	/**
	 * Update the retry flag, since the retry should be done only once.
	 *
	 * @param ids
	 *            -
	 */
	public synchronized void updateRetryFlag(Set<Long> ids) {
		if (null == ids) {
			return;
		}
		try {
			QueryUtil.updateBos(HiveApUpdateResult.class,
					"actionType = :s1", "id in (:s2)", new Object[] {
							(short) 0, ids });
		} catch (Exception e) {
			DebugUtil.configDebugError("update retry flag error", e);
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HiveApUpdateResult) {
			HiveApUpdateResult result = (HiveApUpdateResult) bo;
			if (result.getItems() != null) {
				result.getItems().size();
			}
		} else if (bo instanceof VpnService) {
			VpnService vpnService = (VpnService) bo;

			if (vpnService.getVpnCredentials() != null) {
				vpnService.getVpnCredentials().size();
			}
		}
		return null;
	}

	public boolean isCanceling(String macAddress, long resultId,
			short updateType) {
		UpdateHiveAp upAp = updateList.get(macAddress);
		if (null != upAp) {
			UpdateObject item = upAp.getUpdateObject(updateType);
			if (null != item) {
				if (resultId == item.getResultId() && item.isCanceling()) {
					return true;
				}
			}
		}
		return false;
	}

	public void stop() {
		BeLogTools
				.info(HmLogConst.M_TRACER | HmLogConst.M_CONFIG,
						"<BE Thread> BeCommunication RequestManager is shutdown gracefully");
	}

	private synchronized void removeHiveApOutOfUpdateList(String macAddress) {
		updateList.remove(macAddress);
		HmBeCommunicationUtil.getBeCommunicationProcessor().getBeCommunicationRequestManager().dismissConfigurationCliPermit(macAddress);

		//distribute vhm queue to waiting queue
		distributeUploadVhmQue();
		// update updateList immediately
		updateHiveAPQueue();
		// update device in queue position
		//updateQueuePosition();
	}

	private void removeHiveApOutOfQueueList(UpdateHiveAp upAp) {
		for(List<UpdateHiveAp> vhmList : uploadVhmQue.values()){
			if(vhmList.contains(upAp)){
				vhmList.remove(upAp);
				break;
			}
		}
		queueList.remove(upAp);
		HmBeCommunicationUtil.getBeCommunicationProcessor().getBeCommunicationRequestManager().dismissConfigurationCliPermit(upAp.getHiveAp()
				.getMacAddress());
	}

	public int getWaitingQueueSize(){
		return queueList.size();
	}

//	public boolean isExistsHiveApUploadImage(long domainId){
//		boolean isExists = false;
//		
//		List<UpdateHiveAp> vhmList = uploadVhmQue.get(domainId);
//		if(vhmList != null){
//			for(UpdateHiveAp upAp : vhmList){
//				if(upAp.getUpdateType() == UpdateParameters.AH_DOWNLOAD_IMAGE &&
//						upAp.getHiveAp().getOwner().getId() == domainId){
//					isExists = true;
//					return isExists;
//				}
//			}
//		}
//		
//		for(UpdateHiveAp upAp : queueList){
//			if(upAp.getUpdateType() == UpdateParameters.AH_DOWNLOAD_IMAGE &&
//					upAp.getHiveAp().getOwner().getId() == domainId){
//				isExists = true;
//				return isExists;
//			}
//		}
//		
//		for(UpdateHiveAp upAp : updateList.values()){
//			if(upAp.getUpdateType() == UpdateParameters.AH_DOWNLOAD_IMAGE &&
//					upAp.getHiveAp().getOwner().getId() == domainId){
//				isExists = true;
//				return isExists;
//			}
//		}
//		return isExists;
//	}
	
	private void updateQueuePosition(){
		queuePostion.clear();
		int maxQueue = 0;
		UpdateHiveAp upAp;
		for(List<UpdateHiveAp> vhmList : uploadVhmQue.values()){
			if(vhmList.size() > maxQueue){
				maxQueue = vhmList.size();
			}
		}
		
		for(UpdateHiveAp queueAp : queueList){
			queuePostion.add(queueAp.getHiveAp().getMacAddress());
		}
		
		for(int i=0; i<maxQueue; i++){
			for(List<UpdateHiveAp> vhmList : uploadVhmQue.values()){
				if(vhmList.size() > i){
					upAp = vhmList.get(i);
					if(upAp != null){
						queuePostion.add(upAp.getHiveAp().getMacAddress());
					}
				}
			}
		}
	}

	public int getUploadWaitingQueueNum(String nodeId){
		for(int index=0; index<queuePostion.size(); index++){
			if(nodeId.equals(queuePostion.get(index))){
				return queueList.size() + index + 1;
			}
		}
		return -1;
	}

	private void updateVpnNetworkAddress(short updateStatus, HiveAp hiveAp) throws Exception{
		if(updateStatus == UPDATE_SUCCESSFUL){
			CVGAndBRIpResourceManage.updateSubNetworkResourceSucc(hiveAp.getOwner(), hiveAp.getMacAddress(), hiveAp.getOwner().getId());
		}else{
			CVGAndBRIpResourceManage.updateSubNetworkResourceFaild(hiveAp.getOwner(), hiveAp.getMacAddress());
		}
	}
	
	public void setStagedUpdateHiveAp(BeBaseEvent beEvent){
		String macAddress;
		UpdateHiveAp upAp;
		
		if(beEvent instanceof AhConfigGeneratedEvent){
			AhConfigGeneratedEvent cfgEvent = (AhConfigGeneratedEvent)beEvent;
			macAddress = cfgEvent.getHiveAp().getMacAddress();
			upAp = updateList.get(macAddress);
			if(upAp != null){
				upAp.setByStaged(ConfigGenResultType.DISCONNECT.equals(cfgEvent.getConfigGenResultType()));
			}
		}
		
		if(beEvent instanceof BeCommunicationEvent){
			BeCommunicationEvent event = (BeCommunicationEvent)beEvent;
			macAddress = event.getApMac();
			upAp = updateList.get(macAddress);
			if(upAp != null){
				upAp.setByStaged(UpdateUtil.getUploadStagedType(event.getResult()));
			}
		}
	}
	
	private void addUploadVhmQue(UpdateHiveAp up){
		if(up == null || up.getHiveAp() == null){
			return;
		}
		long vhmId = up.getHiveAp().getOwner().getId();
		if(!uploadVhmQue.containsKey(vhmId)){
			uploadVhmQue.put(vhmId, new ArrayList<UpdateHiveAp>());
		}
		uploadVhmQue.get(vhmId).add(up);
	}
	
	private void distributeUploadVhmQue(){
		int emptyVhmQueue = 0;
		while((queueList.size() < uploadVhmQue.size() || queueList.size() < UPDATE_MAX_COUNT) 
				&& emptyVhmQueue < uploadVhmQue.size()){
			emptyVhmQueue = 0;
			for(List<UpdateHiveAp> vhmList : uploadVhmQue.values()){
				if(vhmList != null && !vhmList.isEmpty()){
					queueList.add(vhmList.remove(0));
				}else{
					emptyVhmQueue++;
				}
			}
		}
	}
	
	public static void deviceUploadTrack(HiveAp hiveAp) throws DocumentException{
		String deviceMAC = hiveAp.getMacAddress();
		boolean wiFi0is5GHz;
		Long networkPolicyPK = getConfigTemplateId(hiveAp);
		String[] ssids;
		long[] userProfilePK = null;
		VlanObj[] vLanIds;
				
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_110){
			short radioModel = hiveAp.getWifi0RadioProfile().getRadioMode();
			wiFi0is5GHz = radioModel == RadioProfile.RADIO_PROFILE_MODE_A || radioModel == RadioProfile.RADIO_PROFILE_MODE_NA;
		}else if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 || 
				hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200 || 
				hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP || 
				hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ || 
				hiveAp.isCVGAppliance()){
			wiFi0is5GHz = false;
		}else{
			wiFi0is5GHz = true;
		}

        final HmDomain domain = hiveAp.getOwner();
		String newXmlCfgPath = AhConfigUtil.getFullNewXmlConfigPath(domain.getDomainName(),
				hiveAp.getMacAddress());
		SAXReader saxReader = new SAXReader();
		Document cfgDocument = saxReader.read(new File(newXmlCfgPath));
		
		String ssidXpath ="/configuration/ssid";
		List<?> ssidList = cfgDocument.selectNodes(ssidXpath);
		ssids = new String[ssidList.size()];
		for(int i=0; i<ssidList.size(); i++){
			if(ssidList.get(i) instanceof Element){
				Element ssidEle = (Element)ssidList.get(i);
				ssids[i] = ssidEle.attributeValue("name");
			}
		}
		
		List<String> userProfileNameList = new ArrayList<>();
		String userProfileXpath ="/configuration/user-profile";
		List<?> userProfileList = cfgDocument.selectNodes(userProfileXpath);
		for(Object obj : userProfileList){
			if(obj instanceof Element){
				Element userProfileEle = (Element)obj;
				userProfileNameList.add(userProfileEle.attributeValue("name"));
			}
		}
		if(!userProfileNameList.isEmpty()){
			String upSql ="select id from "+UserProfile.class.getSimpleName();
			List<?> upIds = QueryUtil.executeQuery(upSql, null, new FilterParams(" userProfileName in :s1 and owner=:s2 ", 
					new Object[]{userProfileNameList, domain}));
			userProfilePK = new long[upIds.size()];
			for(int i=0; i<upIds.size(); i++){
				userProfilePK[i] = Long.valueOf(upIds.get(i).toString());
			}
		}
		
		String vlanXpath ="//vlan";
		List<?> vlanList = cfgDocument.selectNodes(vlanXpath);
		vLanIds = new VlanObj[vlanList.size()];
		int index = 0;
		for(Object obj : vlanList){
			if(obj instanceof Element){
				Element vlanEle = (Element)obj;
				String vlanValue = vlanEle.attributeValue("value");
				if(vlanValue == null){
					continue;
				}
				VlanObj vlan = new VlanObj();
				vlan.setVlan(Integer.valueOf(vlanValue));
				if(vlanEle.getParent().getName().equals("mgt0")){
					vlan.setIntVlan(InterfaceType.mgt0);
				}else if(vlanEle.getParent().getName().equals("mgt0.1")){
					vlan.setIntVlan(InterfaceType.mgt0_1);
				}else if(vlanEle.getParent().getName().equals("mgt0.2")){
					vlan.setIntVlan(InterfaceType.mgt0_2);
				}else if(vlanEle.getParent().getName().equals("mgt0.3")){
					vlan.setIntVlan(InterfaceType.mgt0_3);
				}else if(vlanEle.getParent().getName().equals("mgt0.4")){
					vlan.setIntVlan(InterfaceType.mgt0_4);
				}else if(vlanEle.getParent().getName().equals("mgt0.5")){
					vlan.setIntVlan(InterfaceType.mgt0_5);
				}else if(vlanEle.getParent().getName().equals("mgt0.6")){
					vlan.setIntVlan(InterfaceType.mgt0_6);
				}else if(vlanEle.getParent().getName().equals("mgt0.7")){
					vlan.setIntVlan(InterfaceType.mgt0_7);
				}else if(vlanEle.getParent().getName().equals("mgt0.8")){
					vlan.setIntVlan(InterfaceType.mgt0_8);
				}else if(vlanEle.getParent().getName().equals("mgt0.9")){
					vlan.setIntVlan(InterfaceType.mgt0_9);
				}else if(vlanEle.getParent().getName().equals("mgt0.10")){
					vlan.setIntVlan(InterfaceType.mgt0_10);
				}else if(vlanEle.getParent().getName().equals("mgt0.11")){
					vlan.setIntVlan(InterfaceType.mgt0_11);
				}else if(vlanEle.getParent().getName().equals("mgt0.12")){
					vlan.setIntVlan(InterfaceType.mgt0_12);
				}else if(vlanEle.getParent().getName().equals("mgt0.13")){
					vlan.setIntVlan(InterfaceType.mgt0_13);
				}else if(vlanEle.getParent().getName().equals("mgt0.14")){
					vlan.setIntVlan(InterfaceType.mgt0_14);
				}else if(vlanEle.getParent().getName().equals("mgt0.15")){
					vlan.setIntVlan(InterfaceType.mgt0_15);
				}else if(vlanEle.getParent().getName().equals("mgt0.16")){
					vlan.setIntVlan(InterfaceType.mgt0_16);
				}
				vLanIds[index++] = vlan;
			}
		}
		String[] tags = null;
		List<String> tagsStr = new ArrayList<>();
		
		if(null != hiveAp.getClassificationTag1() && !"".equals(hiveAp.getClassificationTag1())){
			tagsStr.add(hiveAp.getClassificationTag1());
		}
		if(null != hiveAp.getClassificationTag2() && !"".equals(hiveAp.getClassificationTag2())){
			tagsStr.add(hiveAp.getClassificationTag2());
		}
		if(null != hiveAp.getClassificationTag3() && !"".equals(hiveAp.getClassificationTag3())){
			tagsStr.add(hiveAp.getClassificationTag3());
		}
		if(null != tagsStr && tagsStr.size() > 0){
			tags = new String[tagsStr.size()];
			tagsStr.toArray(tags);
		}
		NetworkDeviceConfigTracking.policyChanged(Calendar.getInstance(), domain.getId(), deviceMAC, wiFi0is5GHz, 
				networkPolicyPK, ssids, userProfilePK, vLanIds, hiveAp.getTimeZoneOffset() , tags);
	}
	
	private static Long getConfigTemplateId(HiveAp hiveAp){
		String networkPKSql = "select configTemplate.id from "+HiveAp.class.getSimpleName();
		List<?> resList = QueryUtil.executeQuery(networkPKSql, null, new FilterParams("id", hiveAp.getId()));
		Long networkPoicyId = null;
		if(resList != null && !resList.isEmpty()){
			networkPoicyId = (Long)(resList.get(0));
		}
		return networkPoicyId;
	}
	
	private void locationWatchListWatch(HiveAp ap){
		if(null != ap){
			//remove before location watch list
			Long locationClientWacthId = locationWatchListMap.get(ap.getMacAddress());
			if(null != locationClientWacthId){
				LocationClientWatch lcw = QueryUtil.findBoById(LocationClientWatch.class, locationClientWacthId,new LocationClientWatchAction());
				if(null != lcw){
					List<SingleTableItem>	items = lcw.getItems();
					for(SingleTableItem sti:items){
						ClientHistoryTracking.watched
						(
							Long.parseLong(sti.getMacEntry(), 16),
							ap.getOwner().getId(),
							false
						);
					}
				}
				locationWatchListMap.remove(ap.getMacAddress());
			}
			//add current location watch list
			Long configTempId = getConfigTemplateId(ap);
			ConfigTemplate lcw = QueryUtil.findBoById(ConfigTemplate.class, configTempId,new ImplQueryBo()) ;
			if(null != lcw.getLocationServer()){
				if(null != lcw && null != lcw.getClientWatch()){
					List<SingleTableItem>	items = lcw.getClientWatch().getItems();
					for(SingleTableItem sti:items){
						ClientHistoryTracking.watched
						(
							Long.parseLong(sti.getMacEntry(), 16),
							ap.getOwner().getId(),
							true
						);
					}
				}
				locationWatchListMap.put(ap.getMacAddress(), lcw.getId());
			}
		}
	}
	
	static class ImplQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			 if(bo instanceof ConfigTemplate){
				ConfigTemplate cTemplate = (ConfigTemplate)bo;
				if(null != cTemplate.getClientWatch()){
					cTemplate.getClientWatch().getId();
					if(null != cTemplate.getClientWatch().getItems()){
						cTemplate.getClientWatch().getItems().size();
					}
				}
			}

			return null;
		}
	}
	
	private void initializeLocationClientWatchMap(){
		String sql = "select ap.macAddress,lcw.id from hive_ap ap,config_template ct,locationclientwatch lcw where ap.template_id=ct.id and ct.client_watch_id = lcw.id";
		List<?> apMapWacthList = QueryUtil.executeNativeQuery(sql);
		for (Object onePro : apMapWacthList) {
			Object[] tmp = (Object[]) onePro;
			if (tmp[0] != null && !tmp[0].toString().equals("")
					&& tmp[1] != null && !tmp[1].toString().equals("")) {
				Long locationWatchList = ((BigInteger) tmp[1]).longValue();
				locationWatchListMap.put(tmp[0].toString(),locationWatchList);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	public static DownloadInfo createDownloadInfo(HiveAp hiveAp, boolean view) throws Exception{
		Long ownerId = hiveAp.getOwner().getId();
		
		String timeZoneStr = null;
		String timeZoneSql = null;
		String timeZoneWhere = null;
		
		if(hiveAp.getDeviceInfo().isOnlyCVG()){
			timeZoneSql = "select ap.cvgDPD.ntpForCVG.timeZoneStr from "+HiveAp.class.getSimpleName()+" ap";
			timeZoneWhere = "ap.id=:s1 and ap.cvgDPD.ntpForCVG != null";
		}else{
			timeZoneSql = "select ap.configTemplate.mgmtServiceTime.timeZoneStr from "+HiveAp.class.getSimpleName()+" ap";
			timeZoneWhere = "ap.id=:s1 and ap.configTemplate != null and ap.configTemplate.mgmtServiceTime != null";
		}
		
		List<?> timeZoneRes = QueryUtil.executeQuery(timeZoneSql, null, 
				new FilterParams(timeZoneWhere, new Object[]{hiveAp.getId()}));
		if(timeZoneRes != null && !timeZoneRes.isEmpty()){
			timeZoneStr = (String)timeZoneRes.get(0);
		}
		
		DownloadInfo dInfo = new DownloadInfo();
		
		dInfo.setMacAddress(hiveAp.getMacAddress());
		dInfo.setView(view);
		dInfo.setEcwpDefault(NmsUtil.isEcwpDefault());
		dInfo.setEcwpDepaul(NmsUtil.isEcwpDepaul());
		dInfo.setEcwpNnu(NmsUtil.isEcwpNnu());
		dInfo.setHHMApp(NmsUtil.isHostedHMApplication());
		dInfo.setHmIpAddress(HmBeOsUtil.getHiveManagerIPAddr());
		dInfo.setEnableIdm(NmsUtil.isVhmEnableIdm(ownerId));
		dInfo.setIdmRadSecConfig(new HmCloudAuthCertMgmtImpl().getRadSecConfig(ownerId));
		dInfo.setOemHm(NmsUtil.isHMForOEM());
		dInfo.setTimeZoneOffSet(HmBeOsUtil.getTimeZoneOffSet(timeZoneStr));
		dInfo.setTimeZoneString(HmBeOsUtil.getTimeZoneWholeStr(timeZoneStr));
		
		AhDayLightSavingUtil dayLightTime = new AhDayLightSavingUtil(dInfo.getTimeZoneString());
		dInfo.setUseDayLightSaving(dayLightTime.isUseDayLightSaving());
		dInfo.setDayLightTime(dayLightTime.getDayLightTime());
		
//		dInfo.setMdmURLPath(ConfigUtil.getConfigInfo(ConfigUtil.SECTION_AEROHIVE_MDM, ConfigUtil.KEY_URL_ROOT_PATH));
		dInfo.setMdmURLPath(ConfigUtil.getACMConfigServerUrl());
		dInfo.setVhmInstanceId(hiveAp.getOwner().getAcmInstanceId());
		
		//set cloud cwp attributes
		IGAConfigHepler cloudObj = new GAConfigHepler(hiveAp.getOwner().getId());
		dInfo.setAPIKey(cloudObj.getAPIKey());
		dInfo.setAPINonce(cloudObj.getAPINonce());
		dInfo.setRootURL(cloudObj.getRootURL());
		dInfo.setCustomerId(cloudObj.getCustomerId());
		dInfo.setServiceId(cloudObj.getServiceId());
		
		dInfo.setOwner(hiveAp.getOwner());
		
		Long dInfoId = QueryUtil.createBo(dInfo);
		dInfo.setId(dInfoId);
		
		//set downloadInfo into HiveAp
		hiveAp.setDownloadInfo(dInfo);
		
		//update hiveap
		if(!view){
			String updateSql = "downloadInfo = :s1";
			String updateWhere = "id = :s2";
			MgrUtil.getQueryEntity().updateBos(HiveAp.class, updateSql, updateWhere, 
					new Object[]{dInfo, hiveAp.getId()});
		}
		
		dayLightTime = null;
		
		return dInfo;
	}
	
	private void setCompleteUpdateTag(short updateStatus, HiveAp hiveAp) throws Exception {
		if(updateStatus == UPDATE_SUCCESSFUL){
			hiveAp.setCompleteUpdateTag(hiveAp.generateCompleteUpdateTag());
			QueryUtil.updateBos(HiveAp.class, "completeUpdateTag = :s1", "id = :s2", 
					new Object[]{hiveAp.getCompleteUpdateTag(), hiveAp.getId()});
		}
	}
	
	public void simplifyUpdateTag(String macAddress){
		simplifyUpdateRecords.add(macAddress);
	}
	
	public void l7_signatureUpdate(HiveAp hiveAp){
		if(hiveAp == null){
			return;
		}
		String macAddress = hiveAp.getMacAddress();
		UpdateHiveAp upObj = getFromQueueList(macAddress);
		if(upObj != null){
			//exists unfinished update process.
			return;
		}
		
		if(!simplifyUpdateRecords.contains(macAddress)){
			return;
		}
		simplifyUpdateRecords.remove(macAddress);
		
		hiveAp = QueryUtil.findBoById(HiveAp.class, hiveAp.getId());
		L7SignatureMng l7MngObj = new L7SignatureMng();
		if(l7MngObj.isLatestSupportedVersion(hiveAp.getSignatureVerString(), hiveAp.getHiveApModel())){
			//no need update
			log.info("Device \""+macAddress+"\" no need update application signatures file");
			return;
		}
		
		//get latest signature file name
		String latestVer = l7MngObj.findLatestSupportedVersion(hiveAp.getSignatureVerString(), hiveAp.getHiveApModel());
		String signatureName = l7MngObj.findSignatureFileNameByVersion(latestVer, hiveAp.getHiveApModel());
		if(StringUtils.isEmpty(signatureName)){
			log.error("Cannot get signatures file through version:"+latestVer+" hiveApModel:"+hiveAp.getHiveApModel());
			return;
		}
		
		UpdateObject updateObj = null;
		try{
			updateObj = HmBeConfigUtil.getUpdateObjectBuilder().getSignatureUpdateObject(
					hiveAp, signatureName, 0, UpdateParameters.L7_SIGNATURE_TIMEOUT_MAX);
		}catch(Exception e){
			log.error("l7_signatureUpdate", e);
		}
		if(updateObj == null){
			return;
		}
		
		UpdateHiveAp upHiveAp = new UpdateHiveAp();
		upHiveAp.setHiveAp(hiveAp);
		upHiveAp.setUpdateType(UpdateParameters.AH_DOWNLOAD_L7_SIGNATURE);
		upHiveAp.setWithReboot(false);
		upHiveAp.setAutoProvision(true);
		upHiveAp.addUpdateObject(updateObj);
		
		List<UpdateHiveAp> upAp_list = new ArrayList<UpdateHiveAp>();
		upAp_list.add(upHiveAp);
		HmBeConfigUtil.getUpdateManager().addUpdateObjects(upAp_list);
	}

}