package com.ah.ui.actions.hiveap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.hiveap.UpdateHiveAp;
import com.ah.be.config.hiveap.UpdateObject;
import com.ah.be.config.hiveap.UpdateObjectFactory;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.config.hiveap.cancellation.CancelObject;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApUpdateItem;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.opensymphony.xwork2.ActionContext;

import edu.emory.mathcs.backport.java.util.Arrays;

public class HiveApUpdateResultAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(HiveApUpdateResultAction.class
			.getSimpleName());

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("refresh".equals(operation)) {
				JSONArray jsonList = new JSONArray(getRefreshList());
				jsonObject = new JSONObject();
				jsonObject.put("index", getPageIndex());
				jsonObject.put("count", getPageCount());
				jsonObject.put("list", jsonList);
				if (showRebootNote) {
					jsonObject.put("showRebootNote", true);
				}
				if (showWarningNote) {
					jsonObject.put("showWarningNote", true);
				}
				return "json";
			} else if ("cancel".equals(operation)) {
				log.info("execute", "cancel result id:" + id);
				cancelOperation();
				return "json";
			} else if ("retry".equals(operation)) {
				log.info("execute", "retry result id:" + id);
				retryOperation();
				prepareResultList();
				return SUCCESS;
			} else if ("removeSuccessfulEntries".equals(operation)) {
				log.info("execute", "operation:" + operation);
				removeSuccessfulEntriesOperation();
				prepareResultList();
				return SUCCESS;
			} else if ("rebootFromResultPage".equals(operation)) {
				log.info("execute", "operation:" + operation);
				Set<Long> devcieIds = getRebootHiveAps();
				MgrUtil.setSessionAttribute(HiveApUpdateAction.SIMPLIFIED_UPDATE_SELECTED_IDs, devcieIds);
				return "rebootHiveAPs";
			} else {
				baseOperation();
				prepareResultList();
				return SUCCESS;
			}
		} catch (Exception e) {
			setL3Features(null);
			return prepareActionError(e);
		}
	}

	private void prepareResultList() throws Exception {
		prepareBoList();
		if (null != page) {
			String userTimeZone = userContext.getTimeZone();
			for (Object object : page) {
				HiveApUpdateResult result = (HiveApUpdateResult) object;
				result.setUserTimeZone(userTimeZone);
				boolean hideAction = getShowDomain()
						&& !HmDomain.HOME_DOMAIN.equals(result.getOwner()
								.getDomainName());
				result.setActionTypeString(hideAction);
				if (result.isAutoProvision()) {
					anyAuto = true;
				}
				if (result.isImageDistribution()) {
					anyDistributor = true;
				}
				if (result.needShowReboot()) {
					anyReboot = true;
				}
				if (result.getLevel() == UpdateParameters.LEVEL_IMAGE_RISK) {
					showWarningNote = true;
				}
			}
		}
	}

	private HiveApUpdateResult getResultBoById(Long id) {
		if (null == id) {
			return null;
		}
		return QueryUtil.findBoById(
				HiveApUpdateResult.class, id, this);
	}

	private Set<Long> getOperationIds(short actionType) {
		Set<Long> ids = new HashSet<Long>();
		if (null != id) {
			ids.add(id);
		} else {
			String where = null;
			Object[] objects = null;
			if(actionType == UpdateParameters.ACTION_CANCEL){
				if (allItemsSelected) {
					where = "actionType = :s1";
					objects = new Object[] { UpdateParameters.ACTION_CANCEL };
				} else {
					Set<Long> selIds = getAllSelectedIds();
					where = "actionType = :s1 and id in (:s2)";
					objects = new Object[] { UpdateParameters.ACTION_CANCEL, selIds };
				}
			}else{
				if (!allItemsSelected) {
					Set<Long> selIds = getAllSelectedIds();
					where = "id in (:s1)";
					objects = new Object[] {selIds};
				}
			}
			
			FilterParams fParams = null;
			if(where != null){
				fParams = new FilterParams(where, objects);
			}
			List<?> list = QueryUtil.executeQuery("select id, nodeId from "
					+ HiveApUpdateResult.class.getSimpleName(), new SortParams(
					"id", true), fParams, domainId);
			Map<String, Long> map = new HashMap<String, Long>();
			for (Object object : list) {
				Object[] attr = (Object[]) object;
				Long id = (Long) attr[0];
				String mac = (String) attr[1];
				map.put(mac, id);
			}
			ids.addAll(map.values());
		}
		return ids;
	}

	private void cancelOperation() {
		jsonObject = new JSONObject();
		try {
			Set<Long> ids = getOperationIds(UpdateParameters.ACTION_CANCEL);
			log.info("cancelOperation", "cancel for ids:" + ids);
			if (ids.isEmpty()) {
				String message = MgrUtil
						.getUserMessage("error.hiveAp.update.cannel.noItem");
				jsonObject.put("msg", message);
				return;
			}
			List<HiveApUpdateResult> results = QueryUtil.executeQuery(HiveApUpdateResult.class,
					null, new FilterParams("id", ids));
			List<String> maces = new ArrayList<String>();
			for (HiveApUpdateResult result : results) {
				maces.add(result.getNodeId());
			}
			List<HiveAp> hiveAps = null;
			if (!maces.isEmpty()) {
				hiveAps = QueryUtil.executeQuery(HiveAp.class, null,
						new FilterParams("macAddress", maces));
			}
			if (hiveAps.isEmpty()) {
				return;
			}
			Map<String, HiveAp> maps = new HashMap<String, HiveAp>(hiveAps
					.size());
			for (HiveAp hiveAp : hiveAps) {
				maps.put(hiveAp.getMacAddress(), hiveAp);
			}
			List<CancelObject> list = new ArrayList<CancelObject>(results
					.size());
			List<CancelObject> listDistributor = new ArrayList<CancelObject>();
			List<CancelObject> listStaged = new ArrayList<CancelObject>();
			for (HiveApUpdateResult result : results) {
				CancelObject obj = new CancelObject();
				if(result.getResult() == UpdateParameters.UPDATE_STAGED){
					obj.setHiveAp(maps.get(result.getNodeId()));
					obj.setUpdateResult(result);
					listStaged.add(obj);
				}else{
					obj.setHiveAp(maps.get(result.getNodeId()));
					obj.setUpdateType(UpdateParameters.AH_DOWNLOAD_IMAGE);
					if (result.isImageDistribution()) {
						listDistributor.add(obj);
					} else {
						list.add(obj);
					}
				}
			}
			list = HmBeConfigUtil.getUpdateManager().operateCancelImage(list);
			list.addAll(HmBeConfigUtil.getImageDistributor().operateCancelImage(
					listDistributor));
			list.addAll(HmBeConfigUtil.getUpdateManager().operateCancelStaged(listStaged));
			String displayVer = MgrUtil.getHiveOSDisplayVersion("3.2.1.0");
			String unSupportMsg = MgrUtil.getUserMessage(
					"error.hiveAp.feature.support.version", displayVer);
			String cancelledMsg = MgrUtil
					.getUserMessage("info.hiveAp.update.canceled");
//			String cancelingMsg = MgrUtil
//					.getUserMessage("info.hiveAp.update.cancelling");
			String cancelFailMsg = MgrUtil
					.getUserMessage("info.hiveAp.update.canceled.failed");
			boolean cancelledAll = true; // by default
			String errorMsg = "";
			for (CancelObject object : list) {
				String hostname = object.getHiveAp().getHostName();
				short result = object.getExecuteResult();
				if (result != CancelObject.RESULT_TRUE_CANCELED) {
					cancelledAll = false;
				}
				switch (result) {
				case CancelObject.RESULT_TRUE_CANCELED:
					errorMsg += NmsUtil.getOEMCustomer().getAccessPonitName()+" \"" + hostname + "\": " + cancelledMsg
							+ "\n";
					break;
				case CancelObject.RESULT_FAILED_UNSUPPORT_CANCELE:
					errorMsg += NmsUtil.getOEMCustomer().getAccessPonitName()+" \"" + hostname + "\": " + unSupportMsg
							+ "\n";
					break;
				case CancelObject.RESULT_TRUE_CANCELING:
					//errorMsg += "HiveAP \"" + hostname + "\": " + cancelingMsg
					//		+ "\n";
					break;
				case CancelObject.RESULT_FAILED:
					errorMsg += NmsUtil.getOEMCustomer().getAccessPonitName()+" \"" + hostname + "\": " + cancelFailMsg
							+ "\n";
					break;
				}
			}
			if (cancelledAll) {
				jsonObject.put("isDone", true);
			} else if (null != errorMsg && !"".equals(errorMsg)) {
				jsonObject.put("msg", errorMsg);
			}
		} catch (Exception e) {
			log.error("cancelOperation", "cancel error", e);
		}
	}

	private void retryOperation() {
		Set<Long> ids = getOperationIds(UpdateParameters.ACTION_RETRY);
		log.info("retryOperation", "retry for ids:" + ids);
		List<UpdateHiveAp> list = new ArrayList<UpdateHiveAp>();
		for (Long id : ids) {
			HiveApUpdateResult ur = getResultBoById(id);
			if (null == ur) {
				continue;
			}
			HiveAp hiveAp = QueryUtil.findBoByAttribute(HiveAp.class,
					"macAddress", ur.getNodeId(), ur.getOwner().getId(), new HiveApUpdateAction());
			if (null == hiveAp) {
				continue;
			}

			UpdateHiveAp upAp = getUpdateHiveAp(ur, hiveAp, false);
			if (null != upAp) {
				list.add(upAp);
			} else {
				addActionError(MgrUtil
						.getUserMessage("error.hiveAp.update.unableRetry"));
			}
		}
		log.info("retryOperation", "retry update object size:" + list.size());
		if (list.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("action.error.item.retry"));
			return;
		}
		List<String[]> errList = HmBeConfigUtil.getUpdateManager()
				.addUpdateObjects(list);
		if (null != errList && errList.size() > 0) {
			for (String[] errInfo : errList) {
				addActionError(errInfo[1]);
			}
		} else {
			HmBeConfigUtil.getUpdateManager().updateRetryFlag(ids);
			// clear selection or the previous selected id is still in the
			// list.
			setAllSelectedIds(null);// reset;
		}
	}

	private void removeSuccessfulEntriesOperation() throws Exception {
		int count;
		// if (allItemsSelected) {
		setAllSelectedIds(null);
		String where = "result in (:s1) AND owner.id= :s2";
		Object[] values = new Object[] { Arrays.asList(new Short[]{UpdateParameters.UPDATE_SUCCESSFUL, UpdateParameters.REBOOT_SUCCESSFUL}),
				domainId };
		FilterParams filterParams = new FilterParams(where, values);
		if (getShowDomain()) {
			count = removeAllBos(boClass, filterParams, null);
		} else {
			count = removeAllBos(boClass, filterParams, null);
		}
		log.info("removeSuccessfulEntriesOperation", "Count: " + count);
		if (count < 0) {
			addActionMessage(MgrUtil.getUserMessage(SELECT_OBJECT));
		} else if (count == 0) {
			addActionMessage(MgrUtil.getUserMessage(NO_OBJECTS_REMOVED));
		} else {
			addActionMessage(MgrUtil
					.getUserMessage(OBJECTS_REMOVED, count + ""));
		}
	}

	public static UpdateHiveAp getUpdateHiveAp(HiveApUpdateResult result,
			HiveAp hiveAp, boolean fromStaged) {
		UpdateHiveAp upAp = new UpdateHiveAp();
		upAp.setHiveAp(hiveAp);
		upAp.setUpdateType(result.getUpdateType());
		upAp.setAutoProvision(false);
		if (fromStaged) {
			upAp.setStagedTime(result.getStagedTime() - 1);
		}
		List<HiveApUpdateItem> items = result.getItems();
		if (null == items || items.isEmpty()) {
			return null;
		}
		for (HiveApUpdateItem item : items) {
			UpdateObject object = getUpdateObject(hiveAp, item);
			if (null != object) {
				upAp.addUpdateObject(object);
			}
		}

		if (upAp.getRemainUpdateObjectCount() == 0) {
			return null;
		}
		return upAp;
	}

	// retry operation support image, configuration, cwp, cert, country code
	// upload, signature
	private static UpdateObject getUpdateObject(HiveAp hiveAp,
			HiveApUpdateItem item) {
		UpdateObject updateObject = null;
		short updateType = item.getUpdateType();
		String cliString = item.getClis();
		boolean isActived = item.isActived();
		List<String> clis = new ArrayList<String>();
		if (null != cliString) {
			String cliArray[] = cliString.split("\n");
			for (String cli : cliArray) {
				if (null != cli && !"".equals(cli.trim())) {
					clis.add(cli + "\n");
				}
			}
		}

		switch (updateType) {
		case UpdateParameters.AH_DOWNLOAD_COUNTRY_CODE:
			if (!clis.isEmpty()) {
				updateObject = UpdateObjectFactory.getCountryCodeUpdateObject(
						hiveAp, clis);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_OUTDOORSTTINGS:
			if (!clis.isEmpty()) {
				updateObject = UpdateObjectFactory
						.getOutdoorSettingsUpdateObject(hiveAp, clis);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_CWP:
			if (!clis.isEmpty()) {
				updateObject = UpdateObjectFactory.getCwpUpdateObject(hiveAp,
						clis);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_IMAGE:
			if (!clis.isEmpty()) {
				int imageSize = item.getFileSize();
				updateObject = UpdateObjectFactory.getImageUpdateObject(hiveAp,
						clis, imageSize, isActived);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_L7_SIGNATURE:
			if (!clis.isEmpty()) {
				int size = item.getFileSize();
				updateObject = UpdateObjectFactory.getSignatureUpdateObject(
						hiveAp, clis, size);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_RADIUS_CERTIFICATE:
			if (!clis.isEmpty()) {
				updateObject = UpdateObjectFactory.getCertUpdateObject(hiveAp,
						clis);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_VPN_CERTIFICATE:
			if (!clis.isEmpty()) {
				updateObject = UpdateObjectFactory.getVpnUpdateObject(hiveAp,
						clis);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_SCRIPT:
			short scriptType = item.getScriptType();
			if (scriptType == UpdateParameters.COMPLETE_SCRIPT
					&& (clis.isEmpty())) {
				log.error("retryOperation",
						"Retry a complate script, but no clis in the previsou list..");
			} else {
				updateObject = UpdateObjectFactory.getScriptUpdateObject(
						hiveAp, clis, scriptType, isActived);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_PSK:
			short pskScriptType = item.getScriptType();
			if (pskScriptType == UpdateParameters.COMPLETE_SCRIPT
					&& (clis.isEmpty())) {
				log.error("retryOperation",
						"Retry a complate script, but no clis in the previsou list..");
			} else {
				updateObject = UpdateObjectFactory.getPskUpdateObject(hiveAp,
						clis, pskScriptType, isActived);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_POE:
			if (!clis.isEmpty()) {
				updateObject = UpdateObjectFactory.getPoeUpdateObject(hiveAp,
						clis);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_OS_DETECTION:
			if (!clis.isEmpty()) {
				int fileSize = item.getFileSize();
				updateObject = UpdateObjectFactory.getOsDetectionUpdateObject(
						hiveAp, clis, fileSize, isActived);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_NET_DUMP:
			if (!clis.isEmpty()) {
				updateObject = UpdateObjectFactory.getNetdumpUpdateObject(
						hiveAp, clis);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_CLOUDAUTH_CERTIFICATE:
			if (!clis.isEmpty()) {
				updateObject = UpdateObjectFactory.getCloudAuthUpdateObject(
						hiveAp, clis);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_DS_CONFIG:
			if (!clis.isEmpty()) {
				updateObject = UpdateObjectFactory.getDsConfigUpdateObject(
						hiveAp, clis, UpdateParameters.AH_DOWNLOAD_DS_CONFIG);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_DS_USER_CONFIG:
			if (!clis.isEmpty()) {
				updateObject = UpdateObjectFactory.getDsConfigUpdateObject(
						hiveAp, clis,
						UpdateParameters.AH_DOWNLOAD_DS_USER_CONFIG);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_DS_AUDIT_CONFIG:
			if (!clis.isEmpty()) {
				updateObject = UpdateObjectFactory.getDsConfigUpdateObject(
						hiveAp, clis,
						UpdateParameters.AH_DOWNLOAD_DS_AUDIT_CONFIG);
			}
			break;
		case UpdateParameters.AH_DOWNLOAD_REBOOT:
			if (!clis.isEmpty()) {
				updateObject = UpdateObjectFactory.getRebootUpdateObject(hiveAp,
						clis);
			}
			break;
		}
		return updateObject;
	}

	private Set<Long> getRebootHiveAps(){
		Set<Long> set;
		if (null != id) {
			set = new HashSet<Long>(1);
			set.add(id);
		} else {
			if (allItemsSelected) {
				List<?> ids = QueryUtil.executeQuery("select id from "
						+ HiveApUpdateResult.class.getSimpleName(), null,
						filterParams, domainId);
				set = new HashSet<Long>();
				for (Object object : ids) {
					set.add((Long) object);
				}
			} else {
				set = getAllSelectedIds();
			}
		}
		Set<Long> hiveApIds = new HashSet<Long>();
		if (null != set) {
			List<?> macAddresses = QueryUtil.executeQuery("select nodeId from "
					+ HiveApUpdateResult.class.getSimpleName(), null,
					new FilterParams("id", set));
			List<?> hiveAps = QueryUtil.executeQuery("select id from "
					+ HiveAp.class.getSimpleName(), null, new FilterParams(
					"macAddress", macAddresses), domainId);
			for (Object object : hiveAps) {
				hiveApIds.add((Long) object);
			}
		}
		
		return hiveApIds;
//		return new MapNodeAction().getMultipleHiveApCliInfo(hiveApIds,
//				getText("topology.menu.hiveAp.reboot"), null);
	}

	private boolean anyAuto = false;

	private boolean anyDistributor = false;

	private boolean anyReboot = false;

	public boolean isAnyAuto() {
		return anyAuto;
	}

	public boolean isAnyDistributor() {
		return anyDistributor;
	}

	public boolean isAnyReboot() {
		return anyReboot;
	}

	public String getShowRebootNote() {
		if (anyReboot) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getShowWarningNote() {
		if (showWarningNote) {
			return "";
		} else {
			return "none";
		}
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_HOSTNAME = 1;
	public static final int COLUMN_NODEID = 2;
	public static final int COLUMN_IPADDRESS = 3;
	public static final int COLUMN_UPDATE_TYPE = 4;
	public static final int COLUMN_START_TIME = 5;
	public static final int COLUMN_FINISH_TIME = 6;
	public static final int COLUMN_UPDATE_RATE = 7;
	public static final int COLUMN_STATUS = 8;
	public static final int COLUMN_RESULT = 9;
	public static final int COLUMN_DESC = 10;
	public static final int COLUMN_ACTION = 11;

	/*
	 * get the description of column by id
	 */
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_HOSTNAME:
			code = "hiveAp.hostName";
			break;
		case COLUMN_NODEID:
			code = "hiveAp.macaddress";
			break;
		case COLUMN_IPADDRESS:
			code = "hiveAp.interface.ipAddress";
			break;
		case COLUMN_UPDATE_TYPE:
			code = "hiveAp.update.type";
			break;
		case COLUMN_START_TIME:
			code = "hiveAp.update.startTime";
			break;
		case COLUMN_FINISH_TIME:
			code = "hiveAp.update.finishTime";
			break;
		case COLUMN_UPDATE_RATE:
			code = "hiveAp.update.downloadRate";
			break;
		case COLUMN_STATUS:
			code = "hiveAp.update.state";
			break;
		case COLUMN_RESULT:
			code = "hiveAp.update.result";
			break;
		case COLUMN_DESC:
			code = "hiveAp.update.result.description";
			break;
		case COLUMN_ACTION:
			code = "hiveAp.update.result.action";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_HOSTNAME));
		columns.add(new HmTableColumn(COLUMN_NODEID));
		columns.add(new HmTableColumn(COLUMN_IPADDRESS));
		columns.add(new HmTableColumn(COLUMN_UPDATE_TYPE));
		columns.add(new HmTableColumn(COLUMN_START_TIME));
		columns.add(new HmTableColumn(COLUMN_FINISH_TIME));
		columns.add(new HmTableColumn(COLUMN_UPDATE_RATE));
		columns.add(new HmTableColumn(COLUMN_STATUS));
		columns.add(new HmTableColumn(COLUMN_ACTION));
		columns.add(new HmTableColumn(COLUMN_RESULT));
		columns.add(new HmTableColumn(COLUMN_DESC));

		return columns;
	}

	@Override
	public HiveApUpdateResult getDataSource() {
		return (HiveApUpdateResult) dataSource;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_HIVEAP_UPDATE_RESULTS);
		setDataSource(HiveApUpdateResult.class);
		keyColumnId = COLUMN_HOSTNAME;
		this.tableId = HmTableColumn.TABLE_UPDATE_RESULT;
	}

	@Override
	protected void enableSorting() {
		// default order for update results should be desc by id.
		String sessionKey = boClass.getSimpleName() + "Sorting";
		sortParams = (SortParams) MgrUtil.getSessionAttribute(sessionKey);
		if (sortParams == null) {
			sortParams = new SortParams("id", false);
			MgrUtil.setSessionAttribute(sessionKey, sortParams);
		}
		// So every sort tag doesn't need to specify a session key
		ActionContext.getContext().put(PAGE_SORTING, sortParams);
	}

	boolean showRebootNote = false;
	boolean showWarningNote = false;

	protected Collection<JSONObject> getRefreshList() throws Exception {
		Collection<JSONObject> listResults = new Vector<JSONObject>();
		enableSorting();
		enablePaging();
		page = findBos();
		if (null == page) {
			return listResults;
		}
		String userTimeZone = userContext.getTimeZone();
		for (Object obj : page) {
			HiveApUpdateResult updateResult = (HiveApUpdateResult) obj;
			updateResult.setUserTimeZone(userTimeZone);
			JSONObject result = new JSONObject();
			result.put("id", updateResult.getId());
			JSONObject resultAttributes = new JSONObject();
			resultAttributes.put("d" + COLUMN_HOSTNAME, updateResult
					.getHostname());
			resultAttributes.put("d" + COLUMN_NODEID, updateResult.getNodeId());
			resultAttributes.put("d" + COLUMN_IPADDRESS, updateResult
					.getIpAddress());
			resultAttributes.put("d" + COLUMN_UPDATE_TYPE, updateResult
					.getUpdateTypeHtmlString());
			resultAttributes.put("d" + COLUMN_START_TIME, updateResult
					.getStartTimeString());
			resultAttributes.put("d" + COLUMN_FINISH_TIME, updateResult
					.getFinishTimeString());
			resultAttributes.put("d" + COLUMN_UPDATE_RATE, updateResult
					.getDownloadRateString());
			resultAttributes.put("d" + COLUMN_STATUS, updateResult
					.getStateString());
			resultAttributes.put("d" + COLUMN_RESULT, updateResult
					.getResultString());
			resultAttributes.put("d" + COLUMN_DESC, getJsonDesc(updateResult));
			boolean hideAction = getShowDomain()
					&& !HmDomain.HOME_DOMAIN.equals(updateResult.getOwner()
							.getDomainName());
			updateResult.setActionTypeString(hideAction);
			resultAttributes.put("d" + COLUMN_ACTION, updateResult
					.getActionTypeString());
			result.put("result", resultAttributes);

			listResults.add(result); 
			if (updateResult.needShowReboot()) {
				showRebootNote = true;
			}
			if (updateResult.getLevel() == UpdateParameters.LEVEL_IMAGE_RISK) {
				showWarningNote = true;
			}
		}
		return listResults;
	}

	private static JSONObject getJsonDesc(HiveApUpdateResult ur)
			throws JSONException {
		JSONObject obj = new JSONObject();
		String value = ur.getDescriptionValue();
		String title = ur.getDescriptionTitle();

		obj.put("value", value);
		obj.put("desc", title);
		return obj;
	}
	
	public String getDeviceTypesJsonString() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		if (null != page) {
			Collection<String> nodeIds = new ArrayList<String>();
			for (Object obj : page) {
				HiveApUpdateResult updateResult = (HiveApUpdateResult) obj;
				String nodeId = updateResult.getNodeId();
				if (null != nodeId && !"".equals(nodeId)) {
					nodeIds.add(nodeId);
				}
			}
			log.info("device node id list on page: " + nodeIds);
			Map<String, Short> deviceModels = new HashMap<String, Short>();
			if (!nodeIds.isEmpty()) {
				String query = "select macAddress, deviceType from "
						+ HiveAp.class.getCanonicalName();
				List<?> list = QueryUtil.executeQuery(query, null,
						new FilterParams("macAddress", nodeIds), domainId);
				for (Object object : list) {
					Object[] objects = (Object[]) object;
					deviceModels.put((String) objects[0], (Short) objects[1]);
				}
			}
			log.info("device models of the node id on page: " + deviceModels);
			for (Object obj : page) {
				HiveApUpdateResult updateResult = (HiveApUpdateResult) obj;
				Long id = updateResult.getId();
				String nodeId = updateResult.getNodeId();
				jsonObject.put("_" + id, deviceModels.get(nodeId));
			}
			log.info("device models on page in json string: "
					+ jsonObject.toString());
		}
		return jsonObject.toString();
	}

	protected JSONArray jsonArray = null;

	protected JSONObject jsonObject = null;

	public String getJSONString() {
		if (jsonArray == null) {
			log.debug("getJSONString", "JSON string: " + jsonObject.toString());
			return jsonObject.toString();
		} else {
			log.debug("getJSONString", "JSON string: " + jsonArray.toString());
			return jsonArray.toString();
		}
	}

	public Collection<HmBo> load(HmBo bo) {
		if (null == bo) {
			return null;
		}
		if (bo instanceof HiveApUpdateResult) {
			HiveApUpdateResult ur = (HiveApUpdateResult) bo;
			if (null != ur.getItems()) {
				ur.getItems().size();
			}
		}
		return null;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
	}
	
	protected String selectedDeviceIds;

	public String getSelectedDeviceIds() {
		return selectedDeviceIds;
	}
}