package com.ah.be.config.hiveap.distribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.event.BeAbortEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCapwapFileDownProgressEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.communication.event.BeHiveCommEvent;
import com.ah.be.communication.event.BeHiveCommResultEvent;
import com.ah.be.config.hiveap.UpdateHiveAp;
import com.ah.be.config.hiveap.UpdateObject;
import com.ah.be.config.hiveap.UpdateObjectException;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.config.hiveap.UpdateUtil;
import com.ah.be.config.hiveap.cancellation.CancelObject;
import com.ah.be.config.hiveap.distribution.ImageUploadGroup.ResultType;
import com.ah.be.config.hiveap.distribution.ImageUploadGroup.StatusType;
import com.ah.be.hiveap.ImageManager;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApUpdateItem;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.hiveap.HiveApUpdateSettings;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.DeviceDaInfo;
import com.ah.util.MgrUtil;

public class ImageDistributor {

	public ImageDistributor() {
		groups = new HashMap<>();
	}

	/*
	 * Map<DomainId|HiveName, ImageUploadGroup>
	 */
	private final Map<String, ImageUploadGroup> groups;

//	private String getGroupKey(Long domainId, String hiveName) {
//		return domainId + "|" + hiveName;
//	}
	
	public static String getGroupKey(HiveAp hiveAp) {
		Long domainId = hiveAp.getOwner().getId();
		String macAddr = hiveAp.getMacAddress();
		String imageType = null;
		String daMac = null;
		
		List<String> itList = ImageManager.getHardwareTargetNamesByMode(hiveAp.getHiveApModel());
		if(itList != null && !itList.isEmpty()){
			imageType = itList.get(0);
		}
		
		List<?> resList = QueryUtil.executeQuery("select DAMac from " +DeviceDaInfo.class.getSimpleName(), null, 
				new FilterParams("macAddress", macAddr));
		if(resList != null && !resList.isEmpty()){
			daMac = (String)resList.get(0);
		}
		
		if(daMac == null || "".equals(daMac)){
			return macAddr;
		}
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100) {
			return macAddr;
		}
		
		return domainId + "|" + daMac + "|" + imageType;
	}

	public synchronized void scanning() {
		try {
			Set<String> keys = groups.keySet();
			for (String key : keys) {
				ImageUploadGroup group = groups.get(key);
				group.setTimeCount(group.getTimeCount()
						+ UpdateParameters.TIMER_INTERVAL);
				if (group.getTimeCount() > group.getMaxTimeoutInSecond() * 1000) {
					updateImageDistributeResult(key, ResultType.TIMED_OUT, null);
					clearImageDistribute(key);
				}
			}
		} catch (Exception e) {
			DebugUtil.configDebugError(
					"Error when scanning image distributor. ", e);
		}
	}

	/*
	 * Add image distribute requests
	 */
	public synchronized void addImageRequest(ImageUploadGroup group)
			throws UpdateObjectException {
		groups.put(group.getKey(), group);
		generateUpdateResult(group);
		sendUploadImageRequest(group);
	}
	
	public Collection<ImageUploadGroup> generateUploadGroup(List<UpdateHiveAp> hiveAps,
			String imageName, boolean isSelectVer, String selectVer, HiveApUpdateSettings settings){
		if(hiveAps == null || hiveAps.isEmpty()){
			return null;
		}
		
		Map<String, ImageUploadGroup> groupMap = new HashMap<>();
		for(UpdateHiveAp up : hiveAps){
			HiveAp hiveAp = up.getHiveAp();
			String key = getGroupKey(hiveAp);
			ImageUploadGroup group = groupMap.get(key);
			if(group == null){
				String deviceImage = imageName;
				group = new ImageUploadGroup(settings);
				group.setImageName(deviceImage);
				group.setByVersion(isSelectVer);
				group.setSoftVer(selectVer);
				groupMap.put(key, group);
			}
			group.getUpdateList().add(up);
		}
		return groupMap.values();
	}
	
	/**
	 * 
	 * @param allIdList -
	 * @param domainId -
	 * @return A map key is hivename, value is a list of hiveap id
	 */
	public Map<String, List<Long>> getPortalAPListByHive(Set<Long> allIdList, Long domainId){
		Map<String, List<Long>> rsMap = new HashMap<>();
		
		if(allIdList == null || (allIdList.isEmpty())){
			return rsMap;
		}
		
		String where = "id in (:s1)";
		Object[] values = new Object[] {allIdList};
		List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null,
				new FilterParams(where, values), domainId);
		
		if(hiveAps != null){
			for(HiveAp hiveAp : hiveAps){
				String hiveName = hiveAp.getRunningHive();
				if(hiveName != null && !"".equals(hiveName)){
					if(rsMap.containsKey(hiveName)){
						continue;
					}
					List<HiveAp> apList = this.findPortalHiveAPs(domainId, hiveName);
					List<Long> apIdList = new ArrayList<>();
					for(HiveAp hiveApID : apList){
						apIdList.add(hiveApID.getId());
					}
					rsMap.put(hiveName, apIdList);
				}
			}
		}
		
		return rsMap;
	}
	
	/**
	 * @param domainId -
	 * @return map key is hiveap id value is hostname
	 */
	public Map<Long, String> getIdAndHostnameMapping(Long domainId){
		Map<Long, String> resMap = new HashMap<>();
		
		List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null,
				null, domainId);
		
		if(hiveAps != null){
			for(HiveAp hiveAp : hiveAps){
				resMap.put(hiveAp.getId(), hiveAp.getHostName());
			}
		}
		
		return resMap;
	}
	
	/**
	 * @param domainId -
	 * @return map key is hiveap id value is hive name
	 */
	public Map<Long, String> getIdAndHiveNameMapping(Long domainId){
		Map<Long, String> resMap = new HashMap<>();

		List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null,
				null, domainId);
		
		if(hiveAps != null){
			for(HiveAp hiveAp : hiveAps){
				resMap.put(hiveAp.getId(), hiveAp.getRunningHive());
			}
		}
		
		return resMap;
	}
	
	/**
	 * 
	 * @param idList -
	 * @param domainId -
	 * @return hive name group by hive name
	 */
	public Set<String> getHiveName(Set<Long> idList, Long domainId){
		Set<String> resSet = new HashSet<>();
		
		String where = "id in (:s1)";
		Object[] values = new Object[] {idList};
		List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null,
				new FilterParams(where, values), domainId);
		if(hiveAps != null){
			for(HiveAp hiveAp : hiveAps){
				resSet.add(hiveAp.getRunningHive());
			}
		}
		return resSet;
	}

//	/*
//	 * Only allow to upload image for those HiveAP version >=3.5
//	 */
//	private void validateImageVersion(List<UpdateHiveAp> hiveAps)
//			throws UpdateObjectException {
//		for (UpdateHiveAp upAp : hiveAps) {
//			HiveAp hiveAp = upAp.getHiveAp();
//			String softVer = hiveAp.getSoftVer();
//			if (NmsUtil.compareSoftwareVersion(softVer, "3.5.0.0") < 0) {
//				throw new UpdateObjectException(MgrUtil.getUserMessage(
//						"error.hiveAp.feature.support.version", "3.5r1"));
//			}
//		}
//	}

	/*
	 * Only allow to upload one type of image
	 */
	public void validateImageType(List<UpdateHiveAp> hiveAps)
			throws UpdateObjectException {
		List<HiveAp> list = new ArrayList<>(hiveAps.size());
		for (UpdateHiveAp upAp : hiveAps) {
			list.add(upAp.getHiveAp());
		}
		boolean result = ImageManager.isSameImageType(list);
		if (!result) {
			throw new UpdateObjectException(MgrUtil
					.getUserMessage("image.distributor.imageType"));
		}
	}

	/*
	 * Only allow to upload image for one Hive, also the Hive is not in the
	 * current upload list. If the Hive available, return it
	 */
//	private Set<String> findAvailableHiveAPHive(List<UpdateHiveAp> hiveAps) {
//		Set<String> hives = new HashSet<String>();
//		for (UpdateHiveAp upAp : hiveAps) {
//			String hive = upAp.getHiveAp().getRunningHive();
//			if (null != hive && !"".equals(hive)) {
//				hives.add(hive);
//			}
//		}
//		return hives;
//	}

	/*
	 * Find portal HiveAPs in the given Hive. Current only return 3 at most.
	 */
	private List<HiveAp> findPortalHiveAPs(Long domainId, String hiveName) {
		List<HiveAp> orderList = new ArrayList<>();
		if(hiveName == null || "".equals(hiveName)){
			return orderList;
		}
		List<Short> notInList = new ArrayList<>();
		notInList.add(HiveAp.HIVEAP_MODEL_BR100);
		notInList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
		notInList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);
		Map<String, HiveAp> orderListMap = new HashMap<>();
		String where = "runningHive = :s1 and connected = :s2 and ((hiveApType = :s3 and distributedPriority = :s4) or (hiveApType = :s5 and distributedPriority != :s6))" +
						" and hiveApModel not in :s7";
		Object[] values = new Object[] { hiveName, true, 
				HiveAp.HIVEAP_TYPE_MP, HiveAp.DISTRIBUTED_PRIORITY_HIGH,
				HiveAp.HIVEAP_TYPE_PORTAL, HiveAp.DISTRIBUTED_PRIORITY_DISABLED,
				notInList};
		List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null,
				new FilterParams(where, values), domainId);
		for (HiveAp hiveAp : hiveAps) {
			String softVer = hiveAp.getSoftVer();
			String priorityStr="";
			if (NmsUtil.compareSoftwareVersion(softVer, "3.5.0.0") >= 0 && hiveAp.is11nHiveAP()) {
				if(hiveAp.getDistributedPriority() == HiveAp.DISTRIBUTED_PRIORITY_HIGH){
					priorityStr = priorityStr + "1";
				}else{
					priorityStr = priorityStr + "0";
				}
				if(hiveAp.getHiveApType() == HiveAp.HIVEAP_TYPE_PORTAL){
					priorityStr = priorityStr + "1";
				}else{
					priorityStr = priorityStr + "0";
				}
				if (hiveAp.is300HiveAP()){
					priorityStr = priorityStr + "1";
				}else{
					priorityStr = priorityStr + "0";
				}
				String keyStr = priorityStr + hiveAp.getMacAddress();
				orderListMap.put(keyStr, hiveAp);
//				if (hiveAp.is11nHiveAP()) {
//					if (hiveAp.is300HiveAP()) {
//						orderList.add(0, hiveAp);
//					} else {
//						orderList.add(hiveAp);
//					}
//				}
			}
		}
		Object[] keyArr = orderListMap.keySet().toArray();
		Arrays.sort(keyArr);
		for(int i=keyArr.length-1; i>=0; i--){
			orderList.add(orderListMap.get(keyArr[i].toString()));
		}
		return orderList;
		
//		return orderList.size() > 3 ? orderList.subList(0, 3) : orderList;
	}
	
//	private List<HiveAp> findDownloadPortalHiveAPs(String hiveName, Long domainId, Long selectedAp){
//		List<HiveAp> rest = new ArrayList<HiveAp>();
//		HiveAp selectedAP = QueryUtil.findBoById(HiveAp.class, selectedAp);
//		rest.add(selectedAP);
//		List<HiveAp> fList =  this.findPortalHiveAPs(domainId, hiveName);
//		short index=0;
//		for(HiveAp ap : fList){
//			if(index == 2){
//				break;
//			}
//			if(!ap.getId().equals(selectedAp)){
//				rest.add(ap);
//				index++;
//			}
//		}
//		
//		return rest;
//	}

	/*
	 * For those HiveAP upload image, need to apply for cli transaction code
	 * first (no needed)
	 */
	/*-private void applyTransactionCode(List<HiveAp> hiveAps) {}*/

	/*
	 * Generate Update result BO, then add the update HiveAP to current list
	 */
	private void generateUpdateResult(ImageUploadGroup group) {
		if(group == null){
			return;
		}
		for (UpdateHiveAp upAp : group.getUpdateList()) {
			HiveAp hiveAp = upAp.getHiveAp();
			try {
				HiveApUpdateResult result = new HiveApUpdateResult();
				// do not show distribution tag
				DebugUtil.configDebugWarn("HiveAp [" + hiveAp.getHostName()
						+ "] upload image with distribution way...");
				// result.setTag(HiveApUpdateResult.TAG_IMAGE_DISTRIBUTION);
				result.setHostname(hiveAp.getHostName());
				result.setIpAddress(hiveAp.getIpAddress());
				result.setNodeId(hiveAp.getMacAddress());
				result.setOwner(hiveAp.getOwner());
				result.setStartTime(System.currentTimeMillis());
				result.setUpdateType(UpdateParameters.AH_DOWNLOAD_IMAGE);
				result.setState(UpdateParameters.PROCESS_NOT_START);
				result.setActionType(UpdateParameters.ACTION_CANCEL);

				UpdateObject updateObj = upAp
						.getUpdateObject(UpdateParameters.AH_DOWNLOAD_IMAGE);
				HiveApUpdateItem item = new HiveApUpdateItem();
				item.setActived(updateObj.isActived());
				item.setFileSize(updateObj.getFileSize());
				item.setClis(updateObj.getCliString());
				item.setUpdateType(updateObj.getUpdateType());
				item.setContinued(updateObj.isContinued());
				result.getItems().add(item);

				// add into Database;
				Long resultId = QueryUtil.createBo(result);
				updateObj.setResultId(resultId);
			} catch (Exception e) {
				DebugUtil.configDebugError("Error when apply HiveAp ["
						+ hiveAp.getHostName()
						+ "] into Image distributor group. ", e);
			}
		}
	}

	/*
	 * Generate upload image request to Portal HiveAP
	 */
	private void sendUploadImageRequest(ImageUploadGroup group) {
		HiveAp portal = group.getCurrentPortal();
//		String hiveName = group.getHiveName();
//		Long domainId = group.getDomainId();
		String imageName = group.getImageName();
		HiveApUpdateSettings settings = group.getSettings();
		if (null == portal) {
			updateImageDistributeResult(group.getKey(),
					ResultType.NO_PORTAL, null);
			clearImageDistribute(group);
		} else {
			try {
				String host = NmsUtil.getRunningCapwapServer(portal);
				String userName = NmsUtil.getHMScpUser();
				String password = NmsUtil.getHMScpPsd();
				String clearCli = AhCliFactory.clearPortalImage();
				String saveCli;

				if(MgrUtil.isEnableDownloadServer()){
					String proxy = portal.getProxyName();
					int proxyPort = portal.getProxyPort();
					String proxyLoginUser = portal.getProxyUsername();
					String proxyLoginPwd = portal.getProxyPassword();
					if(group.isByVersion()){
						saveCli = AhCliFactory.downloadPortalImageViaDS(
								portal.getHiveApModel(), group.getSoftVer(), userName, password, 
								proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
					}else{
						saveCli = AhCliFactory.downloadPortalImageViaDS(
								group.getImageName(), userName, password,
								proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
					}
				}else if (portal.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
					saveCli = AhCliFactory.downloadPortalImage(host, imageName, userName, password, settings.isTftpImageTransferType(), settings.getConnectionLimit());
				} else {
					String proxy = portal.getProxyName();
					int proxyPort = portal.getProxyPort();
					String proxyLoginUser = portal.getProxyUsername();
					String proxyLoginPwd = portal.getProxyPassword();
					saveCli = AhCliFactory.downloadPortalImageViaHttp(host, imageName, userName, password, proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
				}

				BeCliEvent request = new BeCliEvent();
				request.setAp(portal);
				request.setClis(new String[] { clearCli, saveCli });
				request.setSequenceNum(HmBeCommunicationUtil
						.getSequenceNumber());
				request.setCliType(BeCliEvent.CLITYPE_TIMECONSUMING);
				// request.setTransactionCode(upAp.getTransactionCode());
				request.buildPacket();
				int serialNums = HmBeCommunicationUtil.sendRequest(request,
						(int) settings.getImageTimedout() * 60);
				group.setPortalSequenceNum(serialNums);
				DebugUtil.configDebugWarn("Portal HiveAp ["
						+ portal.getHostName()
						+ "] send upload image, request SequenceNum:"
						+ serialNums);
			} catch (Exception e) {
				DebugUtil.configDebugError("Portal HiveAp ["
						+ portal.getHostName() + "] send upload image error.",
						e);
				processPortalResult(group, ResultType.UPLOAD_REQ_FAIL, null);
			}
		}
	}

	private void processPortalResult(ImageUploadGroup group, ResultType type,
			String errorMessage) {
		HiveAp hiveAp = group.nextPortal();
//		Long domainId = group.getDomainId();
//		String hiveName = group.getHiveName();
		if (null == hiveAp) {
			updateImageDistributeResult(group.getKey(), type,
					errorMessage);
			clearImageDistribute(group);
		} else {
			DebugUtil.configDebugWarn("Try next Portal HiveAp: "
					+ hiveAp.getHostName());
			sendUploadImageRequest(group);
		}
	}

	private void updateImageDistributeResult(String groupKey, ResultType type,
			String message) {
		try {
			ImageUploadGroup group = groups.get(groupKey);
			if (null == group) {
				return;
			}
			List<UpdateHiveAp> list = group.getUpdateList();
			if (null == list || list.isEmpty()) {
				return;
			}
			switch (type) {
			case PROGRESS:
				UpdateHiveAp u = group.getUpdateList().get(0);
				UpdateObject i = u
						.getUpdateObject(UpdateParameters.AH_DOWNLOAD_IMAGE);
				int summarySize = i.getFileSize();
				int finishedSize = group.getFinishedSize();
				DebugUtil
						.configDebugWarn("Portal HiveAP progress event finished size:"
								+ finishedSize
								+ ", file summary size:"
								+ summarySize);
				List<Long> resultIds = new ArrayList<>();
				for (UpdateHiveAp upAp : list) {
					UpdateObject imageObject = upAp
							.getUpdateObject(UpdateParameters.AH_DOWNLOAD_IMAGE);
					resultIds.add(imageObject.getResultId());
				}
				if (summarySize > 0) {
					float downloadRate = (finishedSize * 100 / summarySize) / 100f;
					float scale = ImageUploadGroup.RATE_UPLOAD_RESPONSED
							+ (ImageUploadGroup.RATE_UPLOAD_RESULTED - ImageUploadGroup.RATE_UPLOAD_RESPONSED)
							* downloadRate;
					if (!resultIds.isEmpty()) {
						QueryUtil.updateBos(HiveApUpdateResult.class,
								"downloadRate = :s1", "id in (:s2)",
								new Object[] { scale, resultIds });
					}
				}
				break;
			case NO_PORTAL:
			case UPLOAD_REQ_FAIL:
			case UPLOAD_RESP_FAIL:
			case UPLOAD_RESL_FAIL:
			case SAVE_REQ_FAIL:
			case SAVE_RESP_FAIL:
			case TIMED_OUT:
				String description;
				short result = UpdateParameters.UPDATE_FAILED;
				// if (message == null) {//use default message
				switch (type) {
				case UPLOAD_REQ_FAIL:
					description = MgrUtil
							.getUserMessage("image.distributor.image.upload.req.error");
					break;
				case UPLOAD_RESP_FAIL:
					description = MgrUtil
							.getUserMessage("image.distributor.image.upload.resp.error");
					break;
				case UPLOAD_RESL_FAIL:
					description = MgrUtil
							.getUserMessage("image.distributor.image.upload.resl.error");
					break;
				case SAVE_REQ_FAIL:
					description = MgrUtil
							.getUserMessage("image.distributor.image.save.req.error");
					break;
				case SAVE_RESP_FAIL:
					description = MgrUtil
							.getUserMessage("image.distributor.image.save.resp.error");
					break;
				case TIMED_OUT:
					description = MgrUtil
							.getUserMessage("image.distributor.image.timeout.error");
					result = UpdateParameters.UPDATE_TIMEOUT;
					break;
				case NO_PORTAL:
				default:
					description = MgrUtil
							.getUserMessage("image.distributor.image.noportal.error");
				}

				long endTime = System.currentTimeMillis();
				resultIds = new ArrayList<>();
				for (UpdateHiveAp upAp : list) {
					UpdateObject imageObject = upAp
							.getUpdateObject(UpdateParameters.AH_DOWNLOAD_IMAGE);
					imageObject.setResult(result);
					imageObject.setDescription(description);

					resultIds.add(imageObject.getResultId());
				}
				if (!resultIds.isEmpty()) {
					QueryUtil
							.updateBos(
									HiveApUpdateResult.class,
									"finishTime = :s1, result = :s2, actionType = :s3, state = :s4, description = :s5",
									"id in (:s6)", new Object[] { endTime,
											result, (short) -1, (short) 0,
											description, resultIds });
				}
				break;
			case UPLOAD_RESP_SUC:
			case UPLOAD_RESL_SUC:
			case SAVE_RESP_SUC:
				float rate;
				switch (type) {
				case UPLOAD_RESP_SUC:
					rate = ImageUploadGroup.RATE_UPLOAD_RESPONSED;
					break;
				case UPLOAD_RESL_SUC:
					rate = ImageUploadGroup.RATE_UPLOAD_RESULTED;
					break;
				case SAVE_RESP_SUC:
					rate = ImageUploadGroup.RATE_SAVE_RESPONSED;
					break;
				default:
					rate = 1.0f;
				}
				short state = UpdateParameters.PROCESS_LOADING;
				resultIds = new ArrayList<>();
				for (UpdateHiveAp upAp : list) {
					UpdateObject imageObject = upAp
							.getUpdateObject(UpdateParameters.AH_DOWNLOAD_IMAGE);
					imageObject.setState(state);
					imageObject.setDownloadRate(rate);
					resultIds.add(imageObject.getResultId());
				}
				if (!resultIds.isEmpty()) {
					QueryUtil.updateBos(HiveApUpdateResult.class,
							"state = :s1, downloadRate = :s2", "id in (:s3)",
							new Object[] { state, rate, resultIds });
				}
				break;
			default:
				DebugUtil.configDebugWarn("Update HiveName: " + groupKey
						+ " unmatched ResultTpe:" + type.name());
			}
		} catch (Exception e) {
			DebugUtil.configDebugError(
					"updateImageDistributeResult, update error. HiveName: "
							+ groupKey, e);
		}
	}

	private void updateImageDistributeResult(UpdateHiveAp upAp,
			ResultType type, int finishedSize, String message) {
		String hostname = upAp.getHiveAp().getHostName();
		try {
			UpdateObject imageObject = upAp
					.getUpdateObject(UpdateParameters.AH_DOWNLOAD_IMAGE);
			HiveApUpdateResult resultEntry = QueryUtil.findBoById(
					HiveApUpdateResult.class, imageObject.getResultId());
			switch (type) {
			case PROGRESS:
				int summarySize = imageObject.getFileSize();
				DebugUtil.configDebugWarn("This HiveAp " + hostname
						+ " get event finished size:" + finishedSize
						+ ", file summary size:" + summarySize);
				if (summarySize > 0) {
					float downloadRate = (finishedSize * 100 / summarySize) / 100f;
					boolean needUpdate = true;// By default
					if (downloadRate < 1) {
						imageObject.setState(UpdateParameters.PROCESS_LOADING);
						imageObject
								.setDownloadRate(ImageUploadGroup.RATE_SAVE_RESPONSED
										+ (ImageUploadGroup.RATE_SAVE_FINISHED - ImageUploadGroup.RATE_SAVE_RESPONSED)
										* downloadRate);
					} else {
						if (imageObject.getDownloadRate() == ImageUploadGroup.RATE_SAVE_FINISHED) {
							// Already finished download, needn't update again.
							needUpdate = false;
						}
						imageObject
								.setState(UpdateParameters.PROCESS_EXTRACTING);
						imageObject
								.setDownloadRate(ImageUploadGroup.RATE_SAVE_FINISHED);
					}
					if (null != resultEntry) {
						if (needUpdate) {
							resultEntry.setState(imageObject.getState());
							resultEntry.setDownloadRate(imageObject
									.getDownloadRate());
							if (downloadRate >= 1) {
								resultEntry.setActionType((short) -1);
							}
							QueryUtil.updateBo(resultEntry);
						}
					} else {
						DebugUtil
								.configDebugWarn("Cannot find correspond HiveApUpdateResult entry for HiveAP:"
										+ hostname);
					}
				}
				break;
			case CANCEL_FAIL:
				imageObject.setCancelSerial(0);
				imageObject.setCanceling(false);
				imageObject.setDescription(message);
				if (null != resultEntry) {
					resultEntry.setDescription(imageObject.getDescription());
					QueryUtil.updateBo(resultEntry);
				} else {
					DebugUtil
							.configDebugWarn("Cannot find correspond HiveApUpdateResult entry for HiveAP:"
									+ hostname);
				}
				break;
			case SAVE_RESL_SUC:
				boolean withReboot = upAp.isWithReboot();
				imageObject.setState((short) 0);
				imageObject.setResult(UpdateParameters.UPDATE_SUCCESSFUL);
				imageObject.setDescription(message);
				imageObject.setDownloadRate(1.0f);
				long endTime = System.currentTimeMillis();

				if (null != resultEntry) {
					resultEntry.setState(imageObject.getState());
					resultEntry.setResult(imageObject.getResult());
					resultEntry.setDescription(imageObject.getDescription());
					resultEntry.setDownloadRate(imageObject.getDownloadRate());
					resultEntry.setFinishTime(endTime);
					if (withReboot) {
						resultEntry.setActionType((short) -1);
					} else {
						resultEntry
								.setActionType(UpdateParameters.ACTION_REBOOT);
					}
					BoMgmt.getHiveApMgmt().updateImageResult(upAp.getHiveAp(),
							resultEntry);
				} else {
					DebugUtil
							.configDebugWarn("Cannot find correspond HiveApUpdateResult entry for HiveAP:"
									+ hostname);
				}
				HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class, upAp
						.getHiveAp().getId());
				if (null != hiveAp
						&& hiveAp.getManageStatus() == HiveAp.STATUS_NEW) {
					BeTopoModuleUtil.triggerNewHiveAPStatusToManaged(hiveAp);
				}
				break;
			case SAVE_RESL_FAIL:
				imageObject.setState((short) 0);
				imageObject.setResult(UpdateParameters.UPDATE_FAILED);
				imageObject.setDescription(message);
				endTime = System.currentTimeMillis();

				if (null != resultEntry) {
					resultEntry.setState(imageObject.getState());
					if(upAp.isByStaged() && upAp.getStagedTime() > 0){
						resultEntry.setResult(UpdateParameters.UPDATE_STAGED);
					}else{
						resultEntry.setResult(imageObject.getResult());
					}
					resultEntry.setDescription(imageObject.getDescription());
					resultEntry.setFinishTime(endTime);
					resultEntry.setActionType((short) -1);

					QueryUtil.updateBo(resultEntry);
				} else {
					DebugUtil
							.configDebugWarn("Cannot find correspond HiveApUpdateResult entry for HiveAP:"
									+ hostname);
				}
				break;
			case CANCELLED:
				imageObject.setState((short) 0);
				imageObject.setResult(UpdateParameters.UPDATE_CANCELED);
				imageObject.setDescription(message);
				endTime = System.currentTimeMillis();

				if (null != resultEntry) {
					resultEntry.setState(imageObject.getState());
					resultEntry.setResult(imageObject.getResult());
					resultEntry.setDescription(imageObject.getDescription());
					resultEntry.setFinishTime(endTime);
					resultEntry.setActionType((short) -1);

					QueryUtil.updateBo(resultEntry);
				} else {
					DebugUtil
							.configDebugWarn("Cannot find correspond HiveApUpdateResult entry for HiveAP:"
									+ hostname);
				}
				break;
			default:
				DebugUtil.configDebugWarn("Update HiveAP: " + hostname
						+ " unmatched ResultTpe:" + type.name());
			}
		} catch (Exception e) {
			DebugUtil.configDebugError(
					"updateImageDistributeResult, update error. HiveAP: "
							+ hostname, e);
		}
	}

	private void clearImageDistribute(ImageUploadGroup group) {
		clearImageDistribute(group.getKey());
	}

	private void clearImageDistribute(String key) {
		ImageUploadGroup group = groups.remove(key);
		if (null != group) {
			DebugUtil
					.configDebugWarn("clearImageDistribute, ImageUploadGroup key: "
							+ key + " removed from update list.");
		}
	}

	/*
	 * Deal with upload image response from Portal HiveAP
	 */
	public void dealUploadImageResponse(BeCliEvent response) {
		HiveAp hiveAp = response.getApNoQuery();
		byte resultType = response.getResult();
		int reqSeq = response.getSerialNum();
		if (null == hiveAp) {
			return;
		}
//		String hiveName = hiveAp.getRunningHive();
//		Long domainId = hiveAp.getOwner().getId();
//		if (null == hiveName || "".equals(hiveName)) {
//			DebugUtil
//					.configDebugError("dealUploadImageResponse, No HiveName found in HiveAP:"
//							+ hiveAp.getHostName());
//			return;
//		}
		ImageUploadGroup group = groups.get(getGroupKey(hiveAp));
		if (null == group) {
			return;
		}
		int seq = group.getPortalSequenceNum();
		if (reqSeq != seq) {
			return;
		}
		DebugUtil
				.configDebugWarn("dealUploadImageResponse, the result value of the response:"
						+ resultType + ", HiveAP:" + hiveAp.getHostName());
		synchronized (this) {
			if (resultType == BeCommunicationConstant.RESULTTYPE_SUCCESS) {
				// request is successful;
				updateImageDistributeResult(getGroupKey(hiveAp),
						ResultType.UPLOAD_RESP_SUC, null);
				group.setStatus(StatusType.UPLOADING_IMAGE); // set flag
			} else {
				String errorMsg = UpdateUtil
						.getCommonResponseMessage(resultType);
				processPortalResult(group, ResultType.UPLOAD_RESP_FAIL,
						errorMsg);
			}
		}
	}

	/*
	 * Deal with upload image result from Portal HiveAP
	 */
	public void dealUploadImageResult(BeCapwapCliResultEvent event) {
		HiveAp hiveAp = event.getApNoQuery();
		byte resultType = event.getCliResult();
		int reqSeq = event.getSerialNum();
		if (null == hiveAp) {
			return;
		}
//		String hiveName = hiveAp.getRunningHive();
//		Long domainId = hiveAp.getOwner().getId();
//		if (null == hiveName || "".equals(hiveName)) {
//			DebugUtil
//					.configDebugError("dealUploadImageResult, No HiveName found in HiveAP:"
//							+ hiveAp.getHostName());
//			return;
//		}
		ImageUploadGroup group = groups.get(getGroupKey(hiveAp));
		if (null == group) {
			return;
		}
		int seq = group.getPortalSequenceNum();
		if (reqSeq != seq) {
			return;
		}
		DebugUtil
				.configDebugWarn("dealUploadImageResult, the cli result value of the result event:"
						+ resultType + ", HiveAP:" + hiveAp.getHostName());
		synchronized (this) {
			if (resultType == BeCommunicationConstant.CLIRESULT_SUCCESS) {
				// request is successful;
				updateImageDistributeResult(getGroupKey(hiveAp),
						ResultType.UPLOAD_RESL_SUC, null);
				sendSaveImageRequest(hiveAp);
			} else {
				String errorMsg = UpdateUtil.getCliResultMessage(
						UpdateParameters.AH_DOWNLOAD_IMAGE, event);
				processPortalResult(group, ResultType.UPLOAD_RESL_FAIL,
						errorMsg);
			}
		}
	}

	/*
	 * Generate save image request to Portal HiveAP
	 */
	private void sendSaveImageRequest(HiveAp portal) {
//		Long domainId = portal.getOwner().getId();
		ImageUploadGroup group = groups.get(getGroupKey(portal));
		List<UpdateHiveAp> list = group.getUpdateList();
		List<HiveAp> hiveAps = new ArrayList<>();
		for (UpdateHiveAp upAp : list) {
			hiveAps.add(upAp.getHiveAp());
		}
		try {
			BeHiveCommEvent request = new BeHiveCommEvent();
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.setAp(portal);
			request.setApList(hiveAps);
			request.setHiveMessageType(BeHiveCommEvent.HIVEMESSAGE_SAVEIMAGE);
			request.setHiveReturnFlag(BeHiveCommEvent.RETURNFLAG_ONEBYONE);
			request.setHiveTimeout(ImageUploadGroup.HIVE_TIME_OUT);
			request.buildPacket();
			int serialNums = HmBeCommunicationUtil.sendRequest(request,
					UpdateParameters.IMAGE_TIMEOUT_MAX / 1000);
			group.setPortalSequenceNum(serialNums);
			DebugUtil.configDebugWarn("Portal HiveAp [" + portal.getHostName()
					+ "] send save image, request SequenceNum:" + serialNums);
		} catch (Exception e) {
			DebugUtil.configDebugError("Portal HiveAp [" + portal.getHostName()
					+ "] send save image error.", e);
			processPortalResult(group, ResultType.SAVE_REQ_FAIL, null);
		}
	}

	/*
	 * Deal with save image response from Portal HiveAP
	 */
	private void dealSaveImageResponse(byte resultType, HiveAp hiveAp,
			ImageUploadGroup group) {
		DebugUtil
				.configDebugWarn("dealSaveImageResponse, the result value of the response:"
						+ resultType + ", HiveAP:" + hiveAp.getHostName());
//		Long domainId = group.getDomainId();
//		String hiveName = group.getHiveName();
		if (resultType == BeCommunicationConstant.RESULTTYPE_SUCCESS) {
			// request is successful;
			updateImageDistributeResult(group.getKey(),
					ResultType.SAVE_RESP_SUC, null);
			group.setStatus(StatusType.SAVING_IMAGE); // set flag
		} else {
			String errorMsg = UpdateUtil.getCommonResponseMessage(resultType);
			processPortalResult(group, ResultType.SAVE_RESP_FAIL, errorMsg);
		}
	}

	/*
	 * Deal with image progress from special HiveAP, it includes two steps.
	 */
	public void dealImageProgress(BeCapwapFileDownProgressEvent event) {
		HiveAp hiveAp = event.getApNoQuery();
		int finishedSize = event.getFinishSize();

		if (null == hiveAp) {
			return;
		}
//		String hiveName = hiveAp.getRunningHive();
//		Long domainId = hiveAp.getOwner().getId();
//		if (null == hiveName || "".equals(hiveName)) {
//			DebugUtil
//					.configDebugError("dealSaveImageProgress, No HiveName found in HiveAP:"
//							+ hiveAp.getHostName());
//			return;
//		}
		ImageUploadGroup group = groups.get(getGroupKey(hiveAp));
		if (null == group) {
			return;
		}
		synchronized (this) {
			if (group.isStartUploadImage()) {
				// upload image to portal HiveAP moment
				DebugUtil
						.configDebugWarn("dealSaveImageProgress, the finished size of Portal: "
								+ hiveAp.getHostName() + " is " + finishedSize);
				group.setFinishedSize(finishedSize);
				updateImageDistributeResult(getGroupKey(hiveAp),
						ResultType.PROGRESS, null);
			} else if (group.isStartSaveImage()) {
				// save image to special HiveAP moment
				UpdateHiveAp upAp = group.getUpdateHiveAp(hiveAp
						.getMacAddress());
				if (null == upAp) {
					DebugUtil
							.configDebugWarn("dealSaveImageProgress, No Update HiveAP running for:"
									+ hiveAp.getHostName());
					return;
				}
				DebugUtil
						.configDebugWarn("dealSaveImageProgress, the finished size of  HiveAP "
								+ hiveAp.getHostName() + " is " + finishedSize);
				updateImageDistributeResult(upAp, ResultType.PROGRESS,
						finishedSize, null);
			}
		}
	}

	/*
	 * Deal with save image result from portal HiveAP
	 */
	private void dealSaveImageResult(Map<String, Integer> results,
			HiveAp hiveAp, ImageUploadGroup group) {
		DebugUtil.configDebugWarn("dealSaveImageResult, related HiveAP size:"
				+ results.size() + ", HiveAP:" + hiveAp.getHostName());
		for (String macAddress : results.keySet()) {
			UpdateHiveAp upAp = group.getUpdateHiveAp(macAddress);
			if (null == upAp) {
				continue;
			}
			Integer returnCode = results.get(macAddress);
			DebugUtil.configDebugWarn("dealSaveImageResult, HiveAP:"
					+ upAp.getHiveAp().getHostName() + ", returnCode:"
					+ returnCode);
			if (null == returnCode) {
				continue;
			}
			if (returnCode == BeCommunicationConstant.RESULTTYPE_SUCCESS) {
				// successfully
				String successMsg = UpdateUtil
						.getCustomizedSucDesc(UpdateParameters.AH_DOWNLOAD_IMAGE);
				updateImageDistributeResult(upAp, ResultType.SAVE_RESL_SUC, 0,
						successMsg);

			} else {
				// failed
				// if the image upload is under cancelling, treat as cancel suc
				ResultType returnType = ResultType.SAVE_RESL_FAIL;
				String errorMsg = NmsUtil.getHiveosErrorMessage(returnCode);
				UpdateObject object = upAp
						.getUpdateObject(UpdateParameters.AH_DOWNLOAD_IMAGE);
				if (null != object && object.isCanceling()) {
					returnType = ResultType.CANCELLED;
					errorMsg = MgrUtil
							.getUserMessage("info.hiveAp.update.canceled");
				}
				updateImageDistributeResult(upAp, returnType, 0, errorMsg);
			}
		}
		boolean isAll = isAllResultReceived(group);
		if (isAll) {
			processAfterAllResultReceived(group);
		}
	}

	private boolean isAllResultReceived(ImageUploadGroup group) {
		int suc = group.getExecSucUpdateHiveAps().size();
		int fail = group.getExecFailUpdateHiveAps().size();
		int canc = group.getExecCancelUpdateHiveAps().size();
		int total = 0;
		if (null != group.getUpdateList()) {
			total = group.getUpdateList().size();
		}
		DebugUtil
				.configDebugWarn("isAllResultReceived, Update HiveAP size of received/total:"
						+ (suc + fail + canc) + " / " + total);
		return (suc + fail + canc) == total;
	}

	private void processAfterAllResultReceived(ImageUploadGroup group) {
//		Long domainId = group.getDomainId();
//		String hiveName = group.getHiveName();
		List<UpdateHiveAp> sucList = group.getExecSucUpdateHiveAps();
		int total = 0;
		if (null != group.getUpdateList()) {
			total = group.getUpdateList().size();
		}
		DebugUtil
				.configDebugWarn("processRebootAction, Update HiveAP size of success/total:"
						+ sucList.size() + " / " + total);
		String rebootCli = group.getRebootCli();
		if (null != rebootCli && !sucList.isEmpty()) {
			sendRebootImageRequest(group, sucList, rebootCli);
		}
		// clear group directly, do not handle reboot request result
		clearImageDistribute(group);
	}

	private void sendRebootImageRequest(ImageUploadGroup group,
			List<UpdateHiveAp> list, String rebootCli) {
		DebugUtil
				.configDebugWarn("sendRebootImageRequest, reboot image request cli:"
						+ rebootCli);
		List<HiveAp> hiveAps = new ArrayList<>();
		for (UpdateHiveAp upAp : list) {
			hiveAps.add(upAp.getHiveAp());
		}
		HiveAp portal = group.getCurrentPortal();
		try {
			BeHiveCommEvent request = new BeHiveCommEvent();
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.setAp(portal);
			request.setApList(hiveAps);
			request.setHiveMessageType(BeHiveCommEvent.HIVEMESSAGE_CLI);
			request.setHiveReturnFlag(BeHiveCommEvent.RETURNFLAG_NEEDALL);
			request.setHiveTimeout(ImageUploadGroup.HIVE_TIME_OUT);
			request.setCli(rebootCli);
			request.buildPacket();
			int serialNums = HmBeCommunicationUtil.sendRequest(request,
					ImageUploadGroup.HIVE_TIME_OUT);
			group.setPortalSequenceNum(serialNums);
			DebugUtil.configDebugWarn("Portal HiveAp [" + portal.getHostName()
					+ "] send reboot image, request SequenceNum:" + serialNums);
		} catch (Exception e) {
			DebugUtil.configDebugError("Portal HiveAp [" + portal.getHostName()
					+ "] send reboot image error.", e);
		}
	}

	public synchronized List<CancelObject> operateCancelImage(
			List<CancelObject> list) {
		Map<String, List<CancelObject>> map = new HashMap<>();
		for (CancelObject object : list) {
//			Long domainId = object.getHiveAp().getOwner().getId();
//			String hiveName = object.getHiveAp().getRunningHive();
			String key = getGroupKey(object.getHiveAp());
			if (map.get(key) == null) {
				List<CancelObject> items = new ArrayList<>();
				map.put(key, items);
			}
			map.get(key).add(object);
		}
		Set<String> keys = map.keySet();
		short FAILED = CancelObject.RESULT_FAILED;
		short SUCCESS = CancelObject.RESULT_TRUE_CANCELED;
		short CANCELING = CancelObject.RESULT_TRUE_CANCELING;
		String message = MgrUtil.getUserMessage("info.hiveAp.update.canceled");
		for (String key : keys) {
			ImageUploadGroup group = groups.get(key);
			List<CancelObject> items = map.get(key);
			if (null == group) {
				// assume as cancel failed.
				for (CancelObject item : items) {
					item.setExecuteResult(FAILED);
				}
				continue;
			}
			HiveAp portal = group.getCurrentPortal();
			String hiveName = portal.getRunningHive();
			if (group.isStartSaveImage()) {
				// at this time, need to send request for cancelling.
				List<HiveAp> hiveAps = new ArrayList<>();
				for (CancelObject item : items) {
					hiveAps.add(item.getHiveAp());
					item.setExecuteResult(CANCELING);
				}
				sendCancelImageRequest(hiveName, portal, hiveAps);
				continue;
			}
			// just remove from the group list, as cancelled.
			for (CancelObject item : items) {
				String macAddress = item.getHiveAp().getMacAddress();
				UpdateHiveAp upAp = group.getUpdateHiveAp(macAddress);
				if (null == upAp) {
					// specify HiveAP not exist, assume as cancel failed.
					item.setExecuteResult(FAILED);
				} else {
					updateImageDistributeResult(upAp, ResultType.CANCELLED, 0,
							message);
					group.removeUpdateHiveAp(macAddress);
					item.setExecuteResult(SUCCESS);
				}
			}

			boolean isAll = isNoHiveApAvailable(group);
			if (isAll) {
				// clear group directly when there's no HiveAPs
				clearImageDistribute(key);
			}
		}
		return list;
	}

	/*
	 * Generate save image request to Portal HiveAP
	 */
	private void sendCancelImageRequest(String hiveName, HiveAp portal,
			List<HiveAp> hiveAps) {
		ImageUploadGroup group = groups.get(getGroupKey(portal));
		try {
			BeHiveCommEvent request = new BeHiveCommEvent();
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.setAp(portal);
			request.setApList(hiveAps);
			request.setHiveMessageType(BeHiveCommEvent.HIVEMESSAGE_CANCELIMAGE);
			request.setHiveReturnFlag(BeHiveCommEvent.RETURNFLAG_ONEBYONE);
			request.setHiveTimeout(ImageUploadGroup.HIVE_TIME_OUT);
			request.buildPacket();
			int serialNums = HmBeCommunicationUtil.sendRequest(request,
					UpdateParameters.IMAGE_TIMEOUT_MAX / 1000);
			group.setCancelSequenceNum(serialNums, hiveAps);
			DebugUtil.configDebugWarn("Send cancel image to Portal HiveAp ["
					+ portal.getHostName() + "], request SequenceNum:"
					+ serialNums);
		} catch (Exception e) {
			DebugUtil.configDebugError("Send cancel image to Portal HiveAp ["
					+ portal.getHostName() + "] error.", e);
		}
	}

	private boolean isNoHiveApAvailable(ImageUploadGroup group) {
		List<UpdateHiveAp> list = group.getUpdateList();
		return list == null || list.isEmpty();
	}

	private void dealCancelImageResponse(Map<String, UpdateHiveAp> map,
			byte resultType, HiveAp hiveAp, ImageUploadGroup group) {
		DebugUtil
				.configDebugWarn("dealCancelImageResponse, the result value of the response:"
						+ resultType + ", HiveAP:" + hiveAp.getHostName());

		String msg_failed = NmsUtil
				.getUserMessage("info.hiveAp.update.canceled.failed");
		if (resultType != BeCommunicationConstant.RESULTTYPE_SUCCESS) {
			Collection<UpdateHiveAp> upAps = map.values();
			for (UpdateHiveAp upAp : upAps) {
				updateImageDistributeResult(upAp, ResultType.CANCEL_FAIL, 0,
						msg_failed);
			}
		}
	}

	/*
	 * Deal with cancel image result from portal HiveAP
	 */
	private void dealCancelImageResult(Map<String, UpdateHiveAp> map,
			Map<String, Integer> results, HiveAp hiveAp, ImageUploadGroup group) {
		DebugUtil.configDebugWarn("dealCancelImageResult, related HiveAP size:"
				+ results.size() + ", Portal HiveAP:" + hiveAp.getHostName());

//		Long domainId = group.getDomainId();
//		String hiveName = group.getHiveName();
		String msg_suc = MgrUtil.getUserMessage("info.hiveAp.update.canceled");
		String msg_failed = NmsUtil
				.getUserMessage("info.hiveAp.update.canceled.failed");
		String msg_writeFlag = NmsUtil
				.getUserMessage("info.hiveAp.update.canceled.writeFlash");

		for (String macAddress : results.keySet()) {
			UpdateHiveAp upAp = map.get(macAddress);
			if (null == upAp) {
				continue;
			}
			Integer returnCode = results.get(macAddress);
			DebugUtil.configDebugWarn("dealCancelImageResult, HiveAP:"
					+ upAp.getHiveAp().getHostName() + ", returnCode:"
					+ returnCode);
			if (null == returnCode) {
				continue;
			}
			if (returnCode == BeAbortEvent.RESULT_SUCCESS
					|| returnCode == BeAbortEvent.RESULT_NO_PROCESS) {
				// cancel successful;
				updateImageDistributeResult(upAp, ResultType.CANCELLED, 0,
						msg_suc);
				group.removeUpdateHiveAp(macAddress);
			} else {
				// cancel failed
				String msg = msg_failed;
				if (returnCode == BeAbortEvent.RESULT_WRITE_FLASH) {
					msg = msg_writeFlag;
				}
				updateImageDistributeResult(upAp, ResultType.CANCEL_FAIL, 0,
						msg);
			}
		}
		boolean isAll = isNoHiveApAvailable(group);
		if (isAll) {
			// clear group directly when there's no HiveAPs
			clearImageDistribute(group);
		}
	}

	/*
	 * Deal with Hive Comm response from Portal HiveAP
	 */
	public synchronized void dealHiveCommEventResponse(BeHiveCommEvent response) {
		HiveAp hiveAp = response.getApNoQuery();
		byte resultType = response.getResult();
		int respSeq = response.getSerialNum();
		if (null == hiveAp) {
			return;
		}
//		String hiveName = hiveAp.getRunningHive();
//		Long domainId = hiveAp.getOwner().getId();
//		if (null == hiveName || "".equals(hiveName)) {
//			DebugUtil
//					.configDebugError("dealHiveCommEventResponse, No HiveName found in HiveAP:"
//							+ hiveAp.getHostName());
//			return;
//		}
		ImageUploadGroup group = groups.get(getGroupKey(hiveAp));
		if (null == group) {
			return;
		}
		int seq1 = group.getPortalSequenceNum();
		if (respSeq == seq1) {
			dealSaveImageResponse(resultType, hiveAp, group);
		} else {
			Map<String, UpdateHiveAp> map = group
					.getCancelUpdateHiveAps(respSeq);
			if (!map.isEmpty()) {
				dealCancelImageResponse(map, resultType, hiveAp, group);
			} else {
				DebugUtil
						.configDebugError("dealHiveCommEventResponse, the seq from response event:"
								+ respSeq
								+ " is not match with the value in group, PortalSequenceNum:"
								+ seq1 + ", and no cancel object found.");
			}
		}
	}

	/*
	 * Deal with Hive Comm result from portal HiveAP
	 */
	public synchronized void dealHiveCommEventResult(BeHiveCommResultEvent event) {
		HiveAp hiveAp = event.getApNoQuery();
		if(hiveAp == null && event.getApMac() != null){
			hiveAp = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", event.getApMac());
		}
		int respSeq = event.getSerialNum();
		Map<String, Integer> results = event.getHivecommResult();
		if (null == hiveAp) {
			return;
		}
//		String hiveName = hiveAp.getRunningHive();
//		Long domainId = hiveAp.getOwner().getId();
//		if (null == hiveName || "".equals(hiveName)) {
//			DebugUtil
//					.configDebugError("dealHiveCommEventResult, No HiveName found in HiveAP:"
//							+ hiveAp.getHostName());
//			return;
//		}
		ImageUploadGroup group = groups.get(getGroupKey(hiveAp));
		if (null == group) {
			return;
		}
		int seq1 = group.getPortalSequenceNum();
		if (respSeq == seq1) {
			dealSaveImageResult(results, hiveAp, group);
		} else {
			Map<String, UpdateHiveAp> map = group
					.getCancelUpdateHiveAps(respSeq);
			if (!map.isEmpty()) {
				dealCancelImageResult(map, results, hiveAp, group);
			} else {
				DebugUtil
						.configDebugError("dealHiveCommEventResult, the seq from response event:"
								+ respSeq
								+ " is not match with the value in group, PortalSequenceNum:"
								+ seq1 + ", and no cancel object found.");
			}
		}
	}

	public boolean isCanceling(String macAddress, long resultId,
			short updateType) {
		for (ImageUploadGroup group : groups.values()) {
			UpdateHiveAp upAp = group.getUpdateHiveAp(macAddress);
			if (null != upAp) {
				UpdateObject item = upAp.getUpdateObject(updateType);
				if (null != item) {
					if (resultId == item.getResultId() && item.isCanceling()) {
						return true;
					}
				}
			}
		}
		return false;
	}

}