/**
 *@filename		BonjourGatewaySettingAction.java
 *@version
 *@author		LiangWenPing
 *@createtime	2012-3-26 PM 02:06:01
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.ui.actions.config;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.AhDataTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.BonjourActiveService;
import com.ah.bo.network.BonjourFilterRule;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.BonjourService;
import com.ah.bo.network.BonjourServiceCategory;
import com.ah.bo.network.BonjourServiceTreeNode;
import com.ah.bo.network.VlanGroup;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CheckItem3;
import com.ah.util.MgrUtil;

/**
 * @author		LiangWenPing
 * @version		V1.0.0.0 
 */
public class BonjourGatewaySettingAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		
		try {
  			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.BonjourGatewaySetting.title"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				
				setSessionDataSource(new BonjourGatewaySettings());
				loadTreeTable(getDataSource().getBonjourServiceTreeNode());
				prepareAhDataTableColumnDefs();
				prepareAhDataTableData();
				
				return isJsonMode() ? "bonjourGatewaySettingDlg" : INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("bonjourGwName", getDataSource().getBonjourGwName())) {
					prepareAhDaTableTransientData();
					prepareAhDataTableColumnDefs();
					prepareAhDataTableDataAfterEdit();
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					return isJsonMode() ? "json" : INPUT;								
				}
				if(!validateVlanRange()){
					prepareAhDaTableTransientData();
					prepareAhDataTableColumnDefs();
					prepareAhDataTableDataAfterEdit();
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					return isJsonMode() ? "json" : INPUT;		
				}
				prepareSelectedServices();
				if(!checkRules()){
					prepareAhDaTableTransientData();
					prepareAhDataTableColumnDefs();
					prepareAhDataTableDataAfterEdit();
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					return isJsonMode() ? "json" : INPUT;		
				}
				updateRules();
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedName", getDataSource().getBonjourGwName());
					try {
						id = createBo(dataSource);
						jsonObject.put("addedId", id);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					return "json";
				} else {
					if ("create".equals(operation)) {
						return createBo();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("edit".equals(operation)) {
				String strForward = editBo(this);
				if (dataSource != null) {
					addLstTitle(getText("config.BonjourGatewaySetting.title.edit") + " '" + getChangedName() + "'");
				}
				loadTreeTable(getDataSource().getBonjourServiceTreeNode());
				setSelectedServiceIDs(setCheckedServices());
				
				prepareAhDataTableColumnDefs();
				prepareAhDataTableData();
				return isJsonMode() ? "bonjourGatewaySettingDlg" : strForward;
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if(!validateVlanRange()){
					prepareAhDaTableTransientData();
					prepareAhDataTableColumnDefs();
					prepareAhDataTableDataAfterEdit();
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					return isJsonMode() ? "json" : INPUT;		
				}
				prepareSelectedServices();
				if(!checkRules()){
					prepareAhDaTableTransientData();
					prepareAhDataTableColumnDefs();
					prepareAhDataTableDataAfterEdit();
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					return isJsonMode() ? "json" : INPUT;		
				}
				updateRules();
				if ("update".equals(operation) && !isJsonMode()) {
					return updateBo();
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					if (isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", true);
						return "json";
					} else {
						return getLstForward();
					}
				}
			} else if ("addCustomService".equals(operation)) {
				addCustomService();
				return "json";
			} else if ("delCustomService".equals(operation)) {
				delCustomService();
				if(dataSource.getId() != null){
					setSessionDataSource(findBoById(boClass, dataSource.getId(), this));
				}
				return "json";
			} else if("newFromVlanGroup".equals(operation) || "newToVlanGroup".equals(operation) ||
					"editToVlanGroup".equals(operation) || "editFromVlanGroup".equals(operation)){
				prepareAhDaTableTransientData();
				getDataSource().setSelectedServiceIDs(getSelectedServiceIDs());
				addLstForward("bonjourGatewaySettings");
				
				setParentDomID(operation);
				
				if ("editToVlanGroup".equals(operation) || "editFromVlanGroup".equals(operation)) {
					operation = "editVlanGroup";
				}
				if("newFromVlanGroup".equals(operation) || "newToVlanGroup".equals(operation)){
					operation = "newVlanGroup";
				}
				
				return operation;
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				BonjourGatewaySettings profile = (BonjourGatewaySettings) findBoById(boClass,
						cloneId, this);
				profile.setId(null);
				profile.setBonjourGwName("");
				profile.setVersion(null);
				profile.setOwner(null);
				setSessionDataSource(profile);
				addLstTitle(getText("config.ethernet.access.title"));
				loadTreeTable(getDataSource().getBonjourServiceTreeNode());
				setSelectedServiceIDs(setCheckedServices());
				prepareAhDataTableColumnDefs();
				prepareAhDataTableData();
				return isJsonMode() ? "bonjourGatewaySettingDlg" : INPUT;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					setSessionDataSource(new BonjourGatewaySettings());
					loadTreeTable(getDataSource().getBonjourServiceTreeNode());
					return prepareBoList();
				} else {
					setId(dataSource.getId());
					if (getUpdateContext()) {
						removeLstTitle();
						removeLstForward();
						setUpdateContext(false);
					}
					loadTreeTable(getDataSource().getBonjourServiceTreeNode());
					setSelectedServiceIDs(getDataSource().getSelectedServiceIDs());
			
					prepareAhDataTableColumnDefs();
					prepareAhDataTableDataAfterEdit();
					editInfo = getDataSource().getEditInfo();
					
					if("newFromVlanGroup".equals(getParentDomID()) 
							|| "newToVlanGroup".equals(getParentDomID())){
						if(vlanGroupId != null){
							VlanGroup vlanGroup = QueryUtil.findBoById(VlanGroup.class, vlanGroupId);
							if(vlanGroup != null){
								String elName = "newFromVlanGroup".equals(getParentDomID()) ? "fromVlanGroup_edit" : "toVlanGroup_edit";
								editInfo = changeAhDataTableEditInfo(editInfo,elName,vlanGroup.getId(),vlanGroup.getVlanGroupName());
							}
						}
					} 
					
					return isJsonMode() ? "bonjourGatewaySettingDlg" : INPUT;
				}
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	private String changeAhDataTableEditInfo(String editInfo, String elName,Long key,String value){
		if(editInfo == null || "".equals(editInfo)) return editInfo;
		if(elName == null || "".equals(elName)) return editInfo;
		
		String newContent = ":[\""+key+"\",\""+value+"\"]";
		int index = editInfo.lastIndexOf(elName);
		if(index == -1){
			return editInfo;
		}
		String frontContent = editInfo.substring(0,index+elName.length());
		String afterElStr = editInfo.substring(index);
		String lastContent = afterElStr.substring(afterElStr.indexOf("\"]")+2);
	
		return frontContent+newContent+lastContent;
		
	}
	
	private void prepareAhDaTableTransientData(){
		getDataSource().setEditInfo(editInfo);
		getDataSource().setMetrics(metrics);
		getDataSource().setFromVlanGroups(fromVlanGroups);
		getDataSource().setRuleIds(ruleIds);
		getDataSource().setToVlanGroups(toVlanGroups);
		getDataSource().setServiceNames(serviceNames);
		getDataSource().setServiceTypes(serviceTypes);
		getDataSource().setRealms(realms);
	}
	private void prepareAhDataTableData() throws JSONException{
		if (getDataSource() == null || getDataSource().getRules() == null || getDataSource().getRules().isEmpty() ) {
			ahDtDatas = "";
			return ;
		}
		
		JSONArray jsonArray = new JSONArray();
		for(BonjourFilterRule rule :getDataSource().getRules()){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ruleIds", rule.getRuleId());
			if(rule.getBonjourService() == null){
				jsonObject.put("serviceNames", -1l);
				jsonObject.put("serviceTypes", "");
			} else {
				jsonObject.put("serviceNames", rule.getBonjourService().getId());
				jsonObject.put("serviceTypes", rule.getBonjourService().getType());
			}
			
			if(rule.getFromVlanGroup() == null){
				jsonObject.put("fromVlanGroups", -1l);
			} else {
				jsonObject.put("fromVlanGroups", rule.getFromVlanGroup().getId());
			}
			
			if(rule.getToVlanGroup() == null){
				jsonObject.put("toVlanGroups", -1l);
			} else {
				jsonObject.put("toVlanGroups", rule.getToVlanGroup().getId());
			}
			jsonObject.put("metrics", rule.getMetric());
			
			jsonObject.put("realms", (rule.getRealmName()==null || "".equals(rule.getRealmName())) ? MgrUtil.getUserMessage("config.ipPolicy.any") : rule.getRealmName().replace("'", "\\'"));
			
			jsonArray.put(jsonObject);
		}

		ahDtDatas = jsonArray.toString();
	}
	private void prepareAhDataTableDataAfterEdit() throws JSONException{
		if (getDataSource() == null || getDataSource().getRuleIds() == null ) {
			ahDtDatas = "";
			return ;
		}
		JSONArray jsonArray = new JSONArray();
		for(int i =0;i<getDataSource().getRuleIds().length;i++){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("metrics", getDataSource().getMetrics()[i]);
			jsonObject.put("fromVlanGroups", getDataSource().getFromVlanGroups()[i]);
			jsonObject.put("toVlanGroups", getDataSource().getToVlanGroups()[i]);
			jsonObject.put("serviceNames", getDataSource().getServiceNames()[i]);
			jsonObject.put("serviceTypes", getDataSource().getServiceTypes()[i]);
			jsonObject.put("realms", getDataSource().getRealms()[i]);
			jsonObject.put("ruleIds", getDataSource().getRuleIds()[i]);
			jsonArray.put(jsonObject);
		}

		ahDtDatas = jsonArray.toString();
	}
	
	private void prepareAhDataTableColumnDefs() throws JSONException{
		List<AhDataTableColumn> ahDataTableColumns = new ArrayList<AhDataTableColumn>();
		AhDataTableColumn column = new AhDataTableColumn();
		column.setMark("serviceNames");
		column.setOptions(getAvailableService());
		column.setDefaultValue(getAvailableService().get(0).getId());
		column.setChangeValue(getAvailableServiceType());
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("serviceTypes");
		column.setDefaultValue(getAvailableServiceType().get(0).getValue());
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("fromVlanGroups");
		column.setOptions(getAvailableVlanGroup());
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("toVlanGroups");
		column.setOptions(getAvailableVlanGroup());
		ahDataTableColumns.add(column);
		
		column = new AhDataTableColumn();
		column.setMark("realms");
		column.setRealmNameOtions(getAvailableRealmName());
		column.setDefaultValue(getAvailableRealmName().get(0).getValue());
		ahDataTableColumns.add(column);
		
		ahDtClumnDefs = generateAhDataTableColumnJsonString(ahDataTableColumns);
	} 
	
	private String ahDtClumnDefs="";
	private String ahDtDatas="";
	private String editInfo="";
	
	private String[] metrics;
	private String[] fromVlanGroups;
	private String[] toVlanGroups;
	private String[] ruleIds;
	private String[] serviceNames;
	private String[] serviceTypes;
	private String[] realms;
	
	private Long vlanGroupId;
	
	public String[] getToVlanGroups() {
		return toVlanGroups;
	}

	public String[] getRuleIds() {
		return ruleIds;
	}

	public String[] getServiceNames() {
		return serviceNames;
	}

	public String[] getServiceTypes() {
		return serviceTypes;
	}

	public String[] getRealms() {
		return realms;
	}

	public void setToVlanGroups(String[] toVlanGroups) {
		this.toVlanGroups = toVlanGroups;
	}

	public void setRuleIds(String[] ruleIds) {
		this.ruleIds = ruleIds;
	}

	public void setServiceNames(String[] serviceNames) {
		this.serviceNames = serviceNames;
	}

	public void setServiceTypes(String[] serviceTypes) {
		this.serviceTypes = serviceTypes;
	}

	public void setRealms(String[] realms) {
		this.realms = realms;
	}

	public String[] getMetrics() {
		return metrics;
	}

	public void setMetrics(String[] metrics) {
		this.metrics = metrics;
	}

	public String[] getFromVlanGroups() {
		return fromVlanGroups;
	}

	public void setFromVlanGroups(String[] fromVlanGroups) {
		this.fromVlanGroups = fromVlanGroups;
	}

	public String getEditInfo() {
		return editInfo == null ? "" : editInfo.replace("'", "\\'");
	}

	public void setEditInfo(String editInfo) {
		this.editInfo = editInfo;
	}

	public String getAhDtDatas() {
		return ahDtDatas == null ? "" : ahDtDatas.replace("'", "\\'");
	}

	public void setAhDtDatas(String ahDtDatas) {
		this.ahDtDatas = ahDtDatas;
	}

	public String getAhDtClumnDefs() {
		return ahDtClumnDefs == null ? "":ahDtClumnDefs.replace("'", "\\'");
	}

	public void setAhDtClumnDefs(String ahDtClumnDefs) {
		this.ahDtClumnDefs = ahDtClumnDefs;
	}

	public Long getVlanGroupId() {
		return vlanGroupId;
	}

	public void setVlanGroupId(Long vlanGroupId) {
		this.vlanGroupId = vlanGroupId;
	}

	private void prepareSelectedServices(){
		String[] selectedServiceIDs = getSelectedServiceIDs().split(",");
		List<BonjourActiveService> bonjourActiveServices = new ArrayList<BonjourActiveService>();
		for (int i = 0; i < selectedServiceIDs.length; i++) {
			if(selectedServiceIDs[i] == null || "".equals(selectedServiceIDs[i])){
				continue;
			}
			BonjourService bonjourService = QueryUtil.findBoById(BonjourService.class, Long.valueOf(selectedServiceIDs[i]));
			BonjourActiveService bonjourActiveService = new BonjourActiveService();
			bonjourActiveService.setBonjourService(bonjourService);
			bonjourActiveServices.add(bonjourActiveService);
		}
		
		getDataSource().setBonjourActiveServices(bonjourActiveServices);
	}
	
	public BonjourServiceTreeNode loadTreeTable(BonjourServiceTreeNode treeInfos){
		treeInfos.getTreeNodes().clear();
		treeInfos.setNodeCount(0);
		setRootNode(treeInfos);
		List<BonjourServiceCategory> bonjourServiceCategorys =QueryUtil.executeQuery(BonjourServiceCategory.class, null, null, domainId,null);
		int i=1;
		for(BonjourServiceCategory bServiceCategory : bonjourServiceCategorys){
			if(BonjourServiceCategory.SERVICE_CATEGORY_ALL.equals(bServiceCategory.getServiceCategoryName())){
				continue;
			}
			createCategoryNode(bServiceCategory,treeInfos);
			List<BonjourService> bonjourServices =QueryUtil.executeQuery(BonjourService.class, new SortParams("id"), new FilterParams("BONJOUR_SERVICE_CATEGRORY_ID",bServiceCategory.getId()), domainId,null);
			for (int j = 0; j < bonjourServices.size(); j++) {
				createServiceNode(bonjourServices.get(j),treeInfos,i);
			}
			i++;
		}
		
		return treeInfos;
	}
	
	private void setRootNode(BonjourServiceTreeNode treeInfos){
		BonjourServiceTreeNode newNode = new BonjourServiceTreeNode();
		newNode.setLabel(BonjourServiceCategory.SERVICE_CATEGORY_ALL);
		newNode.setParentId(-1);
		newNode.setNodeId(1);
		newNode.setRoot(true);
		newNode.setCategory(true);
		newNode.setCustom(false);
		treeInfos.getTreeNodes().add(newNode);
		treeInfos.setNodeCount(treeInfos.getNodeCount() + 1);
	}
	
	private void createCategoryNode(BonjourServiceCategory bServiceCategory, BonjourServiceTreeNode treeInfos) {
		BonjourServiceTreeNode newNode = new BonjourServiceTreeNode();
		newNode.setLabel(bServiceCategory.getServiceCategoryName());
		newNode.setParentId(1);
		newNode.setNodeId(treeInfos.getNodeCount()+1);
		newNode.setCategory(true);
		newNode.setRoot(false);
		newNode.setCustom(false);
		newNode.setCategoryId(bServiceCategory.getId());
		treeInfos.getTreeNodes().add(newNode);
		treeInfos.setNodeCount(treeInfos.getNodeCount() + 1);
	}
	
	private void createServiceNode(BonjourService bonjourService, BonjourServiceTreeNode treeInfos,int index) {
		BonjourServiceTreeNode newNode = new BonjourServiceTreeNode();
		newNode.setLabel(bonjourService.getServiceName());
		newNode.setServiceType(bonjourService.getType());
		newNode.setParentId(treeInfos.getTreeNodes().get(index).getNodeId());
		newNode.setNodeId(treeInfos.getNodeCount()+1);
		newNode.setCategory(false);
		newNode.setRoot(false);
		if(BonjourServiceCategory.SERVICE_CATEGORY_CUSTIOM.equals(bonjourService.getBonjourServiceCategory().getServiceCategoryName())){
			newNode.setCustom(true);
		} else {
			newNode.setCustom(false);
		}
		newNode.setServiceId(bonjourService.getId());
		treeInfos.getTreeNodes().get(index).getTreeNodes().add(newNode);
		treeInfos.setNodeCount(treeInfos.getNodeCount() + 1);
	}
	
	public String getExpandedNodes() throws JSONException {
		return getNodesString(getDataSource().getBonjourServiceTreeNode());
	}
	
	private String getNodesString(BonjourServiceTreeNode treeInfos) throws JSONException {
		JSONArray result = new JSONArray();

		addNodeStringJson(treeInfos, result);
//		StringBuilder result = new StringBuilder();
		//addNodeString(treeInfos, result);
		//if(result.length() > 0){
		//	result.deleteCharAt(result.length() - 1); // delete the last ','
		//}
		return result.toString();
	}
	
	private void addNodeStringJson(BonjourServiceTreeNode treeInfos, JSONArray result) throws JSONException {
		BonjourServiceTreeNode treeNode;
		for (int i = 0; i < treeInfos.getTreeNodes().size(); i++) {
			treeNode = treeInfos.getTreeNodes().get(i);
			JSONObject jo = new JSONObject();
			jo.put("serviceId", treeNode.getServiceId());
			jo.put("label", treeNode.getLabel());
			jo.put("serviceType", treeNode.getServiceType());
			jo.put("parentId", treeNode.getParentId());
			jo.put("nodeId", treeNode.getNodeId());
			jo.put("isCategory", treeNode.isCategory());
			jo.put("isRoot", treeNode.isRoot());
			jo.put("isCustom", treeNode.isCustom());
			result.put(jo);
			if (treeNode.getTreeNodes() != null
					&& treeNode.getTreeNodes().size() > 0) {
				addNodeStringJson(treeNode, result);
			}
		}
	}
	
//	private void addNodeString(BonjourServiceTreeNode treeInfos, StringBuilder result) {
//		BonjourServiceTreeNode treeNode;
//		for (int i = 0; i < treeInfos.getTreeNodes().size(); i++) {
//			treeNode = treeInfos.getTreeNodes().get(i);
//			result.append("{");
//			result.append("\"serviceId\":\"");
//			result.append(treeNode.getServiceId());
//			result.append("\",");
//			result.append("\"label\":\"");
//			result.append(treeNode.getLabel());
//			result.append("\",");
//			result.append("\"serviceType\":\"");
//			result.append(treeNode.getServiceType());
//			result.append("\",");
//			result.append("\"parentId\":");
//			result.append(treeNode.getParentId());
//			result.append(",");
//			result.append("\"nodeId\":");
//			result.append(treeNode.getNodeId());
//			result.append(",");
//			result.append("\"isCategory\":");
//			result.append(treeNode.isCategory());
//			result.append(",");
//			result.append("\"isRoot\":");
//			result.append(treeNode.isRoot());
//			result.append(",");
//			result.append("\"isCustom\":");
//			result.append(treeNode.isCustom());
//			result.append("},");
//			if (treeNode.getTreeNodes() != null
//					&& treeNode.getTreeNodes().size() > 0) {
//				addNodeString(treeNode, result);
//			}
//		}
//	}
	
	private String setCheckedServices(){
		StringBuilder result = new StringBuilder();
		if(getDataSource() == null){
			return "";
		}
		for (BonjourActiveService bonjourActiveService : getDataSource().getBonjourActiveServices()) {
			if (bonjourActiveService == null){
				continue;
			}
			result.append(bonjourActiveService.getBonjourService().getId());
			result.append(",");
		}
		if(result.length() > 0){
			result.deleteCharAt(result.length() - 1);
		}
		return result.toString();
	}
	
	private int getAvailableTypeId(List<BonjourService> services){
		List<Integer> typeIds = new ArrayList<Integer>();
		for(BonjourService service : services){
			typeIds.add(service.getTypeId());
		}
		for(int i=20;i<120;i++){
			if(!typeIds.contains(i)){
				return i;
			}
		}
		return services.size()+1;
	}
	
	private void addCustomService() throws JSONException{
		jsonObject = new JSONObject();
		
		if (customServiceName == null || customServiceType == null) {
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "Please fill in \"Service\" and \"Type\".");
			return;
		}
		BonjourService service = QueryUtil.findBoByAttribute(BonjourService.class, "type", customServiceType,domainId);
		if(service != null){
			jsonObject.put("resultStatus", false);
			jsonObject.put("inputName", "type");
			jsonObject.put("errMsg", "Duplicate Type.");
			return;
		}
		BonjourService service1 = QueryUtil.findBoByAttribute(BonjourService.class, "serviceName", customServiceName,domainId);
		if(service1 != null){
			jsonObject.put("resultStatus", false);
			jsonObject.put("inputName", "service");
			jsonObject.put("errMsg", "Duplicate service.");
			return;
		}
		List<BonjourService> services = QueryUtil.executeQuery(BonjourService.class, null, null, domainId);
		if(services == null){
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "Bonjour Services is empty.");
			return;
		}
		
		if(services.size()>118){
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", getText("config.BonjourGatewaySetting.service.count.tip"));
			return;
		}
		BonjourService bonjourService = new BonjourService();
		bonjourService.setTypeId(getAvailableTypeId(services));
		bonjourService.setServiceName(customServiceName);
		bonjourService.setType(customServiceType);
		bonjourService.setOwner(getDomain());
		BonjourServiceCategory bonjourServiceCategory = QueryUtil.findBoByAttribute(BonjourServiceCategory.class, "serviceCategoryName", BonjourServiceCategory.SERVICE_CATEGORY_CUSTIOM,domainId);
		bonjourService.setBonjourServiceCategory(bonjourServiceCategory);
		
		try {
			BonjourService bService = QueryUtil.updateBo(bonjourService);
			jsonObject.put("serviceId", bService.getId());//serverId,label,serviceType
			jsonObject.put("label", bService.getServiceName());
			jsonObject.put("serviceType", bService.getType());
			jsonObject.put("resultStatus", true);
			prepareAhDataTableColumnDefs();
			prepareAhDaTableTransientData();
			prepareAhDataTableDataAfterEdit();
			jsonObject.put("ahDtClumnDefs", ahDtClumnDefs == null ? "": ahDtClumnDefs.replace("'", "\\'"));
			jsonObject.put("ahDtDatas", ahDtDatas == null  ? "" : ahDtDatas.replace("'", "\\'"));
			jsonObject.put("editInfo", getDataSource().getEditInfo() == null  ? "" : getDataSource().getEditInfo().replace("'", "\\'"));
			
		} catch (Exception e) {
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "Update custom service fail.");
		}
	}
	
	private void delCustomService() throws JSONException{
		jsonObject = new JSONObject();
		if (customServiceId == null ) {
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "Unknow error.");
			return;
		}
		
		try {
			if(dataSource.getId() != null){
				Long bonjourSettingId = null;
				List<BonjourGatewaySettings> bonjourGatewaySettingsList = QueryUtil.executeQuery(BonjourGatewaySettings.class, null, null, domainId,this);
				for(BonjourGatewaySettings bonjourGatewaySettings : bonjourGatewaySettingsList){
					for (BonjourActiveService activeService : bonjourGatewaySettings.getBonjourActiveServices()) {
						long serviceId = activeService.getBonjourService().getId();
						if(serviceId == Long.valueOf(customServiceId)){
							bonjourGatewaySettings.getBonjourActiveServices().remove(activeService);
							bonjourSettingId = getDataSource().getId();
							break;
						}
					}
					if(bonjourSettingId != null){
						QueryUtil.updateBo(bonjourGatewaySettings);
						
					}
				}
			}
			
			if(isServiceUsedByOther(Long.valueOf(customServiceId))){
				jsonObject.put("resultStatus", false);
				jsonObject.put("errMsg", getText("config.BonjourGatewaySetting.service.isuserd"));
				return;
			} else if(isServiceUsedByself(customServiceId)){
				jsonObject.put("resultStatus", false);
				jsonObject.put("errMsg", getText("config.BonjourGatewaySetting.service.isuserd.byself"));
				return;
			} else if(isServiceUsedByselfDb(Long.valueOf(customServiceId))){ // if service which only used by itself was removed in rules, we allow to remove this service.
				Long bonjourSettingId = null;
				for(int i=getDataSource().getRules().size()-1;i>0;i++){
					BonjourFilterRule rule = getDataSource().getRules().get(i);
					long serviceId = rule.getBonjourService().getId();
					if(serviceId == Long.valueOf(customServiceId)){
						getDataSource().getRules().remove(rule);
						bonjourSettingId = getDataSource().getId();
						break;
					}
				}
				if(bonjourSettingId != null){
					QueryUtil.updateBo(getDataSource());
				}
			}

			QueryUtil.removeBo(BonjourService.class, Long.valueOf(customServiceId));
			
			jsonObject.put("customServiceId", customServiceId);
			jsonObject.put("resultStatus", true);
			prepareAhDataTableColumnDefs();
			prepareAhDaTableTransientData();
			prepareAhDataTableDataAfterEdit();
			jsonObject.put("ahDtClumnDefs", ahDtClumnDefs== null ? "": ahDtClumnDefs.replace("'", "\\'"));
			jsonObject.put("ahDtDatas", ahDtDatas== null ? "": ahDtDatas.replace("'", "\\'"));
			jsonObject.put("editInfo", getDataSource().getEditInfo()== null ? "": getDataSource().getEditInfo().replace("'", "\\'"));
		} catch (Exception e) {
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "Delete custom service fail.");
		}
		
	}
	
	private boolean isServiceUsedByself(String customServiceId){
		boolean result = false;
		if(serviceNames != null && Arrays.asList(serviceNames).contains(customServiceId)){
			result = true;
		}
		
		return result;
	}
	
	private boolean isServiceUsedByselfDb(long customServiceId){
		boolean result = false;
			if(getDataSource() != null) {
				for(BonjourFilterRule rule : getDataSource().getRules()){
					if(rule.getBonjourService().getId() == customServiceId){
						result= true;
						break;
					}
					if(result){
						break;
					}
				}
			}
		return result;
	}
	
	private boolean isServiceUsedByOther(long customServiceId){
		boolean result = false;
		List<BonjourGatewaySettings> bonjourGatewaySettingsList = QueryUtil.executeQuery(BonjourGatewaySettings.class, null, null, domainId,this);
		for(BonjourGatewaySettings bonjourGatewaySettings : bonjourGatewaySettingsList ){
			if(getDataSource() != null && getDataSource().equals(bonjourGatewaySettings)) {continue;}
			for(BonjourFilterRule rule : bonjourGatewaySettings.getRules()){
				if(rule.getBonjourService().getId() == customServiceId){
					result= true;
					break;
				}
			}
			if(result){
				break;
			}
		}
		return result;
	}
	
	//1-10,20,30-50
	private List<String> vlanStrToList(String vlans){
		List<String> vlanList = new ArrayList<String>();
		if(vlans != null){
			String[] vlanRanges = vlans.split(",");
			for(String vlanRange : vlanRanges){
				if(vlanRange.contains("-")){
					int start = Integer.valueOf(vlanRange.substring(0, vlanRange.indexOf("-")));
					int end = Integer.valueOf(vlanRange.substring(vlanRange.indexOf("-")+1));
					for(int i = start;i<=end;i++){
						vlanList.add(String.valueOf(i));
					}
				} else {
					vlanList.add(vlanRange);
				}
			}
		}
		
		return vlanList;
	}
	private boolean validateVlanRange(){
		if(getDataSource()!=null){
			List<String> vlanRange = vlanStrToList(getDataSource().getVlans());
			Set<VlanGroup> vlanGroupSet = new HashSet<VlanGroup>();	
			if(fromVlanGroups != null){
				for(int i=0;i<fromVlanGroups.length;i++){
					if(!"".equals(fromVlanGroups[i])){
						VlanGroup fromVlanGroup = QueryUtil.findBoById(VlanGroup.class, Long.valueOf(fromVlanGroups[i]));
						if(fromVlanGroup!=null){
							vlanGroupSet.add(fromVlanGroup);
						}
					}
				}
			}
			
			if(toVlanGroups != null){
				for(int i=0;i<toVlanGroups.length;i++){
					if(!"".equals(toVlanGroups[i])){
						VlanGroup toVlanGroup = QueryUtil.findBoById(VlanGroup.class, Long.valueOf(toVlanGroups[i]));
						if(toVlanGroup!=null){
							vlanGroupSet.add(toVlanGroup);
						}
					}
				}
			}
			for(VlanGroup vlanGroup : vlanGroupSet){
				List<String> groupVlans = vlanStrToList(vlanGroup.getVlans());
				for(String vlan : groupVlans){
					if(!vlanRange.contains(vlan)){
						addActionError("The vlan group ("+vlanGroup.getVlanGroupName()+") is not in VLANs range ("+getDataSource().getVlans()+")");
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public int getNameLength() {
		return getAttributeLength("bonjourGwName");
	}

	public String getChangedName() {
		return getDataSource().getBonjourGwName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}
	
	public boolean isParentIframeOpenFlag4Child() {
		return isContentShownInDlg();
	}
	
	private String selectedServiceIDs;
	
	public String getSelectedServiceIDs() {
		return selectedServiceIDs;
	}

	public void setSelectedServiceIDs(String selectedServiceIDs) {
		this.selectedServiceIDs = selectedServiceIDs;
	}
	
	private String customServiceName;
	private String customServiceType;
	private String customServiceId;
	
	public String getCustomServiceName() {
		return customServiceName;
	}

	public void setCustomServiceName(String customServiceName) {
		try {
			this.customServiceName = URLDecoder.decode(customServiceName,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.customServiceName = customServiceName;
		}
	}

	public String getCustomServiceType() {
		return customServiceType;
	}

	public void setCustomServiceType(String customServiceType) {
		try {
			this.customServiceType = URLDecoder.decode(customServiceType,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.customServiceType = customServiceType;
		}
	}

	public String getCustomServiceId() {
		return customServiceId;
	}

	public void setCustomServiceId(String customServiceId) {
		this.customServiceId = customServiceId;
	}
	
	public List<CheckItem> getAvailableVlanGroup() {
		List<CheckItem> availableVlanGroups = getBoCheckItems("vlanGroupName", VlanGroup.class, null);
		availableVlanGroups.remove(new CheckItem((long) -1, MgrUtil
			.getUserMessage("config.optionsTransfer.none")));
		availableVlanGroups.add(0, new CheckItem((long) -1, MgrUtil
			.getUserMessage("config.ipPolicy.any")));
		return availableVlanGroups;
	}

	public List<CheckItem> getAvailableService() {
		List<CheckItem> availableServices = getBoCheckItems("serviceName", BonjourService.class, null);
		availableServices.remove(new CheckItem((long) -1, MgrUtil
			.getUserMessage("config.optionsTransfer.none")));
		availableServices.add(0, new CheckItem((long) CHECK_ITEM_ID_BLANK, ""));
		return availableServices;
	}
	
	public List<CheckItem3> getAvailableRealmName(){
		List<CheckItem3> availableRealmName = new ArrayList<CheckItem3>();
		
//		String sqlString_bjgw  = "select distinct realmid from BONJOUR_GATEWAY_MONITORING where owner="+domainId;
//		List<?> realmidList_bjgw  =QueryUtil.executeNativeQuery(sqlString_bjgw);
//		for(Object realmId :realmidList_bjgw){
//			if(realmId == null || "".equals(realmId.toString().trim())){
//				continue;
//			}
//			CheckItem3 item = new CheckItem3(realmId.toString(), realmId.toString());
//			if(availableRealmName.contains(item)){
//				continue;
//			}
//			availableRealmName.add(item);
//		}
		String sqlString_hiveAp  = "select distinct realmName from HIVE_AP where owner="+domainId;
		List<?> realmidList_hiveAp =QueryUtil.executeNativeQuery(sqlString_hiveAp);
		for(Object realmId :realmidList_hiveAp){
			if(realmId == null || "".equals(realmId.toString().trim())){
				continue;
			}
			CheckItem3 item = new CheckItem3(realmId.toString(), realmId.toString());
			if(availableRealmName.contains(item)){
				continue;
			}
			availableRealmName.add(item);
		}
		
		availableRealmName.add(0, new CheckItem3(MgrUtil
				.getUserMessage("config.ipPolicy.any"), MgrUtil
				.getUserMessage("config.ipPolicy.any")));
		return availableRealmName;
	}
	
	public List<CheckItem> getAvailableServiceType() {
		List<CheckItem> availableServiceTypes = getBoCheckItems("type", BonjourService.class, null);
		availableServiceTypes.remove(new CheckItem((long) -1, MgrUtil
			.getUserMessage("config.optionsTransfer.none")));
		availableServiceTypes.add(0, new CheckItem((long) CHECK_ITEM_ID_BLANK, ""));
		return availableServiceTypes;
	}

	protected void updateRules() {
		if (null == ruleIds) {
			getDataSource().setRules(new ArrayList<BonjourFilterRule>());
		} else {
			List<BonjourFilterRule> rules = new ArrayList<BonjourFilterRule>();
			
			List<Short> usedRuleIds = new ArrayList<Short>();
			for (int i = 0; i < ruleIds.length; i++) {
				if(ruleIds[i] != null && !"".equals(ruleIds[i])){
					usedRuleIds.add(Short.valueOf(ruleIds[i]));
				}
			}
			
			for (int i = 0; i < ruleIds.length; i++) {
				
				//fix error when new a row which donot save
				if(realms[i] == null || "".equals(realms[i])){
					continue;
				}
				
				BonjourFilterRule bjFilterRule = new BonjourFilterRule();
				if(null != serviceNames[i]){
					bjFilterRule.setBonjourService(QueryUtil.findBoById(BonjourService.class,Long.valueOf(serviceNames[i])));
				}
				bjFilterRule.setFromVlanGroup(QueryUtil.findBoById(VlanGroup.class,Long.valueOf(fromVlanGroups[i])));
				bjFilterRule.setToVlanGroup(QueryUtil.findBoById(VlanGroup.class,Long.valueOf(toVlanGroups[i])));
				bjFilterRule.setMetric(metrics[i]);
				if(!MgrUtil.getUserMessage("config.ipPolicy.any").equals(realms[i])){
					bjFilterRule.setRealmName(realms[i]);
				}
				
				// set rule id
				if(ruleIds[i] == null || "".equals(ruleIds[i])){
					for (short l = 1; l < 129; l++) {
						if(usedRuleIds.contains(l)){
							continue;
						}
						bjFilterRule.setRuleId(l);
						usedRuleIds.add(l);
						break;
					}
				} else {
					bjFilterRule.setRuleId(Short.valueOf(ruleIds[i]));
				}
				
				rules.add(bjFilterRule);
			}
			getDataSource().setRules(rules);
		}		
	}
	
	protected boolean checkRules(){
		if(ruleIds == null ){
			
		} else {
			if(ruleIds.length >128){
				addActionError(MgrUtil.getUserMessage("error.objectReachLimit"));
				return false;
			}
			
			List<Integer> list = new ArrayList<>();
			for(int i=0;i<ruleIds.length;i++){
				String fromVlanGroup = fromVlanGroups[i];
				String toVlanGroup = toVlanGroups[i];
				String serviceType = serviceTypes[i];
				String serviceName = serviceNames[i];
				String realm = realms[i];
				String metric = metrics[i];
				for(int j=i+1;j<ruleIds.length;j++){
					if(((serviceType == null && serviceTypes[j] == null)
							|| (serviceType != null && serviceTypes[j] != null && serviceType.equals(serviceTypes[j])))
						&& (((fromVlanGroup == null && fromVlanGroups[j] == null)
							|| (fromVlanGroup != null && fromVlanGroups[j] != null && fromVlanGroup.equals(fromVlanGroups[j]))))
						&&(((toVlanGroup == null && toVlanGroups[j] == null)
								|| (toVlanGroup != null && toVlanGroups[j] != null && toVlanGroup.equals(toVlanGroups[j]))))
						&&(((serviceName == null && serviceNames[j] == null)
								|| (serviceName != null && serviceNames[j] != null && serviceName.equals(serviceNames[j]))))
						&&(((realm == null && realms[j] == null)
								|| (realm != null && realms[j] != null && realm.equals(realms[j]))))
						&&(((metric == null && metrics[j] == null)
								|| (metric != null && metrics[j] != null && metric.equals(metrics[j]))))
						){
						list.add(i+1);
						break;
					}
				}
			}
			
			if(list.size() > 1){
				addActionError("Some rules in ("+list.toString()+") rows cannot be added because the same ones already exist.");
				return false;
			} else if(list.size() == 1){
				addActionError("The rule in ("+list.toString()+") row cannot be added because the same one already exist.");
				return false;
			}
			
//			if(count == 1){
//				addActionError(MgrUtil.getUserMessage("error.addObjectExists"));
//					return false;
//			} else if(count >1){
//				addActionError(MgrUtil.getUserMessage("error.addSomeObjectExists"));
//				return false;
//			}
			
		}
		
		return true;
		
	}
	
	/************************* Override Method ********************************/
	public boolean isTrackFormChanges() {
		return false;
	}
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_BONJOUR_GATEWAY_SETTINGS);
		setDataSource(BonjourGatewaySettings.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_BONJOUR_GATEEWAY_SETTINGS;
	}

	@Override
	public BonjourGatewaySettings getDataSource() {
		return (BonjourGatewaySettings) dataSource;
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_DESCRIPTION = 2;
	
	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return String
	 */
	@Override
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.pppoe.name";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.BonjourGatewaySetting.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(2);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		BonjourGatewaySettings source = QueryUtil.findBoById(BonjourGatewaySettings.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<BonjourGatewaySettings> list = QueryUtil.executeQuery(BonjourGatewaySettings.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (BonjourGatewaySettings profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			BonjourGatewaySettings up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setBonjourGwName(profile.getBonjourGwName());
			up.setOwner(profile.getOwner());
			hmBos.add(up);
		}
		return hmBos;
	}
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}
		
		if(bo instanceof BonjourGatewaySettings){
			BonjourGatewaySettings bonjourGatewaySettings = (BonjourGatewaySettings)bo;
			if(bonjourGatewaySettings.getBonjourActiveServices() != null){
				bonjourGatewaySettings.getBonjourActiveServices().size();
			}
			if(bonjourGatewaySettings.getRules() != null){
				bonjourGatewaySettings.getRules().size();
			}
		}
		if(bo instanceof BonjourService){
			BonjourService bonjourService = (BonjourService)bo;
			if(bonjourService.getBonjourServiceCategory() != null){
				bonjourService.getBonjourServiceCategory().getId();
			}
		}
		return null;
	}
}