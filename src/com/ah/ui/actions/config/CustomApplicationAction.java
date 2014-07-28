package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.ah.be.service.ApplicationService;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosCustomService;
import com.ah.bo.network.Application;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.CustomApplicationRule;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.IpPolicyRule;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

public class CustomApplicationAction extends BaseAction  implements QueryBo{

	private static final long serialVersionUID = -943764055701053587L;
	
	public static final short MAX_RULE_SIZE = 64;
	
	public static final short MAX_CUSTOM_APP_SIZE = 100;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {	
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				setSessionDataSource(new CustomApplication());
				return "customApp";
			} else if ("create".equals(operation)) {
				if(!MgrUtil.xssStringCheck(getDataSource().getCustomAppName())){
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					jsonObject.put("m", MgrUtil.getUserMessage("security.xss.specialchars",MgrUtil.getUserMessage("config.application.name")));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Create custom application");
					return "json";
				}
				if(checkSameSystemAppName(getDataSource().getCustomAppName())){
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					jsonObject.put("m", MgrUtil.getUserMessage("geneva_26.error.custom.application.assameas.system.appname",getDataSource().getCustomAppName()));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Create custom application");
					return "json";
				}
				if(checkMaxCustomApp()){
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					jsonObject.put("m", MgrUtil.getUserMessage("geneva_26.error.custom.application.create.maxAllow"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Create custom application");
					return "json";
				}
				if (checkAppName(getDataSource().getCustomAppName(), null)) {
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					if (getActionErrors().size()>0) {
						Object[] errs = getActionErrors().toArray();
						jsonObject.put("m", errs[0].toString());
					}
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Create custom application");
					return "json";
				}
				
				if(!checkRules(null)){
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					if (getActionErrors().size()>0) {
						Object[] errs = getActionErrors().toArray();
						jsonObject.put("m", errs[0].toString());
					}
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Create custom application");
					return "json";
				}
				Integer appcode = CustomApplication.getNextCustomAppCode(getDomain());
				if(-1 == appcode){
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					jsonObject.put("m", MgrUtil.getUserMessage("geneva_26.error.custom.application.create.maxAllow"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Create custom application");
					return "json";
				}
				updateRules();
				getDataSource().setAppCode(appcode);
				getDataSource().setAppGroupName(CustomApplication.DEFAULT_CUSTOM_APP_GROUP_NAME);
				getDataSource().setCustomAppShortName(CustomApplication.CUSTOM_APP_SHORT_NAME_PREFIX+appcode);
				id = createBo(dataSource);
				jsonObject = new JSONObject();
				jsonObject.put("appName", getDataSource().getCustomAppName());
				jsonObject.put("m", "add");
				jsonObject.put("t",true);
				if(isSelectAdd()){
					jsonObject.put("appDesc", getDataSource().getDescription());
					jsonObject.put("appId", id);
					jsonObject.put("appType", 1);
					jsonObject.put("selectAdd", true);
				}else {
					jsonObject.put("selectAdd", false);
				}
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "Create custom application");
				return "json";
			} else if ("edit".equals(operation)) {
				editBo(this);
				return "customApp";
			} else if ("update".equals(operation)) {
				if(!MgrUtil.xssStringCheck(getDataSource().getCustomAppName())){
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					jsonObject.put("m", MgrUtil.getUserMessage("security.xss.specialchars",MgrUtil.getUserMessage("config.application.name")));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Create custom application");
					return "json";
				}		
				if(checkSameSystemAppName(getDataSource().getCustomAppName())){
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					jsonObject.put("m", MgrUtil.getUserMessage("geneva_26.error.custom.application.assameas.system.appname",getDataSource().getCustomAppName()));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Create custom application");
					return "json";
				}
				if (checkAppName(getDataSource().getCustomAppName(),getDataSource().getId())) {
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					if (getActionErrors().size()>0) {
						Object[] errs = getActionErrors().toArray();
						jsonObject.put("m", errs[0].toString());
					}
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Update custom application");
					return "json";
				}
				if(!checkRules(getDataSource().getId())){
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					if (getActionErrors().size()>0) {
						Object[] errs = getActionErrors().toArray();
						jsonObject.put("m", errs[0].toString());
					}
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Update custom application");
					return "json";
				}
				updateRules();
				updateBo();
				jsonObject = new JSONObject();
				jsonObject.put("appName", getDataSource().getCustomAppName());
				jsonObject.put("m", "update");
				jsonObject.put("t",true);
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "Update custom application");
				return "json";
			} else if ("remove".equals(operation)) {
				String sql = "update custom_application set deletedflag=? where id=? and owner=?";
				String[] ids = selectedAppIds.split(",");
				String singleId = checkReferedCustomAppInWatchList(ids);
				if(!"".equals(singleId)){
					String name = getRemovedCustomAppName(singleId);
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					jsonObject.put("m", MgrUtil.getUserMessage("error.objectInUse", name));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Remove custom application");
					return "json";
				}
				String singleRuleId = checkReferedCustomAppInIpPolicyRule(ids);
				if(!"".equals(singleRuleId)){
					String name = getRemovedCustomAppName(singleRuleId);
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					jsonObject.put("m", MgrUtil.getUserMessage("error.objectInUse", name));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Remove custom application");
					return "json";
				}
				String singleQosId = checkReferedCustomAppInQosCustomService(ids);
				if(!"".equals(singleQosId)){
					String name = getRemovedCustomAppName(singleQosId);
					jsonObject = new JSONObject();
					jsonObject.put("t", false);
					jsonObject.put("m", MgrUtil.getUserMessage("error.objectInUse", name));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Remove custom application");
					return "json";
				}
				
				List<Object[]> paraList = new ArrayList<Object[]>();
				if (ids.length > 0) {
					for(String id: ids){
						Object[] objs = new Object[3];
						objs[0] = true;
						objs[1] = Long.valueOf(id);
						objs[2] = getDomain().getId();
						paraList.add(objs);
					}
					QueryUtil.executeBatchUpdate(sql, paraList);
				}
				initData();
				jsonObject = new JSONObject();
				jsonObject.put("t", true);
				jsonObject.put("m", "remove");
				jsonObject.put("removeIdNum", ids.length);
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "Remove custom application");
				return "json";
			} else {
				if(null != oper && !"".equals(oper)){
					if("add".equals(oper)){
						message = MgrUtil.getUserMessage("geneva_26.error.custom.application.create",appName);
						addActionMessage(message);
					}else if("update".equals(oper)){
						message = MgrUtil.getUserMessage("geneva_26.error.custom.application.update",appName);
						addActionMessage(message);
					}else{
						if(1 == removeIdNum){
							message = MgrUtil.getUserMessage("geneva_26.info.custom.application.remove.one");
							addActionMessage(message);
						}else{
							message = MgrUtil.getUserMessage("geneva_26.info.custom.application.remove.more",String.valueOf(removeIdNum));
							addActionMessage(message);
						}
					}
				}
				initData();
				return INPUT;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	public CustomApplication getDataSource() {
		return (CustomApplication) dataSource;
	}
	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_APPLICATION_SERVICE);
		setDataSource(CustomApplication.class);
	}
	
	private void initData(){
		ApplicationService service = new ApplicationService();
		customAppList = service.getCustomApplicationWithBytes(getDomain());
		searchCustomAppList = new ArrayList<>();
		for(CustomApplication app : customAppList){
			String name = app.getCustomAppName().replace("\\", "\\\\");
			searchCustomAppList.add(name);
		}
	}
	
	private String getRemovedCustomAppName(String id){
		String result = "";
		if(null !=id && !"".equals(id)){
			CustomApplication customApp = QueryUtil.findBoById(CustomApplication.class, Long.parseLong(id));
			result = customApp.getCustomAppName();
		}
		return result;
	}
	
	private String checkReferedCustomAppInWatchList(String[] ids){
		boolean exist = false;
		String tempId = "";
		List<ApplicationProfile> appProfileList = QueryUtil.executeQuery(ApplicationProfile.class, null, null,
				getDomain().getId(), this);
		Set<CustomApplication> customAppList = new HashSet<CustomApplication>();
		if(null != appProfileList && !appProfileList.isEmpty()){
			ApplicationProfile appProfile = appProfileList.get(0);
			customAppList = appProfile.getCustomApplicationList();
		}
		if(null != customAppList && !customAppList.isEmpty()){
			for(CustomApplication ca : customAppList){
				for(String id: ids){
					if(ca.getId().toString().equals(id)){
						tempId = id;
						exist = true;
						break;
					}
				}
				if(exist){
					break;
				}
			}
		}
		return tempId;
	}
	
	private String checkReferedCustomAppInIpPolicyRule(String[] ids){
		boolean exist = false;
		String tempId = "";
		List<IpPolicy> ipPolicyList = QueryUtil.executeQuery(IpPolicy.class, null, 
				null,getDomain().getId(), this);
		List<IpPolicyRule> allRules = new ArrayList<IpPolicyRule>();
		if(null != ipPolicyList && !ipPolicyList.isEmpty()){
			for(IpPolicy ip : ipPolicyList){
				allRules.addAll(ip.getRules());
			}
		}
		if(null != allRules && !allRules.isEmpty()){
			for(IpPolicyRule ipr : allRules){
				CustomApplication customApp = ipr.getCustomApp();
				if(null != customApp){
					for(String id: ids){
						if(customApp.getId().toString().equals(id)){
							tempId = id;
							exist = true;
							break;
						}
					}
				}
				if(exist){
					break;
				}
			}
		}
		return tempId;
	}
	
	private String checkReferedCustomAppInQosCustomService(String[] ids){
		boolean exist = false;
		String tempId = "";
		List<QosClassification> qosList = QueryUtil.executeQuery(QosClassification.class, null, 
				null,getDomain().getId(),this);
		Map<Long, QosCustomService> customServices = new HashMap<Long, QosCustomService>();
		if(null != qosList && !qosList.isEmpty()){
			for(QosClassification qos : qosList){
				Map<Long, QosCustomService> customService = qos.getCustomServices();
				if(null != customService && !customService.isEmpty()){
					customServices.putAll(qos.getCustomServices());
				}
			}
		}
		if(null != customServices && !customServices.isEmpty()){
			List<QosCustomService> list = new ArrayList<QosCustomService>(customServices.values());
			for(QosCustomService qc : list){
				CustomApplication customApp = qc.getCustomAppService();
				if(null != customApp){
					for(String id: ids){
						if(customApp.getId().toString().equals(id)){
							tempId = id;
							exist = true;
							break;
						}
					}
				}
				if(exist){
					break;
				}
			}
		}
		return tempId;
	}
	
	private boolean checkMaxCustomApp(){
		boolean result = false;
		List<CustomApplication> list = QueryUtil.executeQuery(CustomApplication.class, null,
				new FilterParams("deletedFlag = :s1",new Object[]{false}), domainId);
		if(null != list && list.size() >= MAX_CUSTOM_APP_SIZE){
			result = true;
		}
		return result;
	}
	
	private boolean checkSameSystemAppName(String name){
		boolean result = false;
		if (domainId == null) {
			domainId = QueryUtil.getDependentDomainFilter(userContext);
		}
		 List<Application> systemAppList = QueryUtil.executeQuery(Application.class, new SortParams("appName"), 
					new FilterParams("appCode > :s1", new Object[] {0}));
		 if(null != systemAppList && !systemAppList.isEmpty()){
			 for(Application app : systemAppList){
				 if(name.equalsIgnoreCase(app.getAppName())){
					 result = true;
					 break;
				 }
			 }
		 }
		return result;
	}

	private boolean checkAppName(String name,Long id){
		if (domainId == null) {
			domainId = QueryUtil.getDependentDomainFilter(userContext);
		}
		List<CustomApplication> idList = null;
		if("update".equals(operation)){
			idList = QueryUtil.executeQuery(CustomApplication.class, null,
					new FilterParams("customAppName = :s1 and deletedFlag = :s2 and id != :s3",new Object[]{name,false,id}), domainId);
		}else{
			idList = QueryUtil.executeQuery(CustomApplication.class, null,
					new FilterParams("customAppName = :s1 and deletedFlag = :s2",new Object[]{name,false}), domainId);
		}
		if (!idList.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("error.objectExists", name));
			return true;
		}
		return false;
	}

	private boolean checkRules(Long id){
		if(customDetectionTypes != null){
			if (customDetectionTypes.length > MAX_RULE_SIZE) {
				String[] params = new String[]{String.valueOf(customDetectionTypes.length), String.valueOf(MAX_RULE_SIZE)};
				addActionError(MgrUtil.getUserMessage("geneva_26.error.custom.application.create.rule.maxAllow ", params));
				return false;
			}
			List<Integer> list = new ArrayList<>();
			for(int i=0 ; i<customDetectionTypes.length; i++){
				String customDetectionType = customDetectionTypes[i];
				String customProtocol = customProtocols[i];
				String customRule = customRules[i];
				String customPort = customPorts[i];
				
				for(int j=i+1;j<customDetectionTypes.length;j++){
					if(customDetectionType.equals(customDetectionTypes[j]) && customProtocol.equals(customProtocols[j])
							&& customRule.equals(customRules[j]) && customPort.equals(customPorts[j])){
						list.add(j+1);
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
			} else{
				List<Integer> existList = new ArrayList<>();
				List<CustomApplicationRule> rules = getAllCustomRules(id);
				if(null != rules){
					for(CustomApplicationRule car: rules){
						String customDetectionTypeExist = String.valueOf(car.getDetectionType());
						String customProtocolExist = String.valueOf(car.getProtocolId());
						String customRuleExist = car.getRuleValue();
						String customPortExist = String.valueOf(car.getPortNumber());
						for(int i=0 ; i<customDetectionTypes.length; i++){
							String customDetectionType = customDetectionTypes[i];
							String customProtocol = customProtocols[i];
							String customRule = customRules[i];
							String customPort = customPorts[i].equals("") ? "-1" : customPorts[i];
						
							if(customDetectionType.equals(customDetectionTypeExist) && customProtocol.equals(customProtocolExist)
									&& customRule.equals(customRuleExist) && customPort.equals(customPortExist)){
								existList.add(i+1);
								break;
							}
						}
					}
					if(existList.size() > 1){
						addActionError("Some rules in ("+existList.toString()+") rows cannot be added because the same ones already exist.");
						return false;
					} else if(existList.size() == 1){
						addActionError("The rule in ("+existList.toString()+") row cannot be added because the same one already exist.");
						return false;
					}
				}
			}
			
		}
		return true;
	}
	
	private List<CustomApplicationRule> getAllCustomRules(Long id){
		List<CustomApplicationRule> rules = null;
		List<CustomApplication> list = null;
		if("update".equals(operation)){
			list = QueryUtil.executeQuery(CustomApplication.class, null,
					new FilterParams("deletedFlag = :s1 and id != :s2",new Object[]{false, id}), domainId,this);
		}else{
			list = QueryUtil.executeQuery(CustomApplication.class, null,
					new FilterParams("deletedFlag = :s1",new Object[]{false}), domainId,this);
		}
		
		if(null != list && !list.isEmpty()){
			rules = new ArrayList<CustomApplicationRule>();
			for(CustomApplication ca : list){
				List<CustomApplicationRule> apprules = ca.getRules();
				if(null != apprules && !apprules.isEmpty()){
					rules.addAll(ca.getRules());
				}
			}
		}
		return rules;
	}
	
	protected void updateRules() {
		if(!getDataSource().getRules().isEmpty()){
			getDataSource().getRules().removeAll(getDataSource().getRules());
		}
		
		if(customDetectionTypes == null){
			getDataSource().setRules(new ArrayList<CustomApplicationRule>());
		}else {
			List<Short> usedRuleIds = new ArrayList<Short>();
			for (int i = 0; i < customDetectionTypes.length; i++) {
				if(ruleIds[i] != null && !"".equals(ruleIds[i])){
					usedRuleIds.add(Short.valueOf(ruleIds[i]));
				}
			}
			for(int i=0; i<customDetectionTypes.length; i++){
				CustomApplicationRule customApplicationRule = new CustomApplicationRule();
				customApplicationRule.setDetectionType(Short.parseShort(customDetectionTypes[i]));
				customApplicationRule.setProtocolId(Short.parseShort(customProtocols[i]));
				customApplicationRule.setRuleValue(customRules[i]);
				int port = -1;
				if(Short.parseShort(customDetectionTypes[i]) != CustomApplicationRule.DETECTION_TYPE_HOSTNAME){
					port = "".equals(customPorts[i]) ? -1 : Integer.parseInt(customPorts[i]);
				}
				customApplicationRule.setPortNumber(port);
				// set rule id
				if(ruleIds[i] == null || "".equals(ruleIds[i])){
					for (short l = 1; l < 65; l++) {
						if(usedRuleIds.contains(l)){
							continue;
						}
						customApplicationRule.setRuleId(l);
						usedRuleIds.add(l);
						break;
					}
				} else {
					customApplicationRule.setRuleId(Short.valueOf(ruleIds[i]));
				}
				getDataSource().getRules().add(customApplicationRule);
			}
		}
	}
	
	public EnumItem[] getEnumDetectionType() {
		return CustomApplicationRule.ENUM_DETECTION_TYPE;
	}

	public EnumItem[] getEnumIpPortProtocol() {
		return CustomApplicationRule.ENUM_PROTOCOL_ID;
	}
	
	public EnumItem[] getEnumHostNameProtocol() {
		return CustomApplicationRule.ENUM_PROTOCOL_HTTP_ID;
	}
	
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof CustomApplication) {
			CustomApplication customApplication = (CustomApplication) bo;
			if (customApplication.getRules() != null)
				customApplication.getRules().size();
		}else if (bo instanceof IpPolicy) {
			IpPolicy policy = (IpPolicy) bo;
			if (policy.getRules() != null)
				policy.getRules().size();
		}else if (bo instanceof QosClassification) {
			QosClassification qosClassification = (QosClassification)bo;
			qosClassification.getCustomServices().values();
		}else if (bo instanceof ApplicationProfile){
			ApplicationProfile profile = (ApplicationProfile)bo;
			if(profile.getCustomApplicationList() != null){
				profile.getCustomApplicationList().size();
			}
		}
		return null;
	}
	
	private List<CustomApplication> customAppList;
	private String selectedAppIds;
	private String[] ruleIds;
	private String[] customDetectionTypes;
	private String[] customProtocols;
	private String[] customRules;
	private String[] customPorts;
	private String message;
	private String oper;
	private String appName;
	private Integer removeIdNum; 
	private boolean selectAdd;
	private List<String> searchCustomAppList;
	
	
	public List<String> getSearchCustomAppList() {
		return searchCustomAppList;
	}

	public void setSearchCustomAppList(List<String> searchCustomAppList) {
		this.searchCustomAppList = searchCustomAppList;
	}

	public boolean isSelectAdd() {
		return selectAdd;
	}

	public void setSelectAdd(boolean selectAdd) {
		this.selectAdd = selectAdd;
	}

	public Integer getRemoveIdNum() {
		return removeIdNum;
	}

	public void setRemoveIdNum(Integer removeIdNum) {
		this.removeIdNum = removeIdNum;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getOper() {
		return oper;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSelectedAppIds() {
		return selectedAppIds;
	}

	public void setSelectedAppIds(String selectedAppIds) {
		this.selectedAppIds = selectedAppIds;
	}

	public List<CustomApplication> getCustomAppList() {
		return customAppList;
	}

	public void setCustomAppList(List<CustomApplication> customAppList) {
		this.customAppList = customAppList;
	}
	
	public String[] getRuleIds() {
		return ruleIds;
	}

	public void setRuleIds(String ruleIds[]) {
		this.ruleIds = ruleIds;
	}

	public String[] getCustomDetectionTypes() {
		return customDetectionTypes;
	}

	public void setCustomDetectionTypes(String[] customDetectionTypes) {
		this.customDetectionTypes = customDetectionTypes;
	}

	public String[] getCustomProtocols() {
		return customProtocols;
	}

	public void setCustomProtocols(String[] customProtocols) {
		this.customProtocols = customProtocols;
	}

	public String[] getCustomRules() {
		return customRules;
	}

	public void setCustomRules(String[] customRules) {
		this.customRules = customRules;
	}

	public String[] getCustomPorts() {
		return customPorts;
	}

	public void setCustomPorts(String[] customPorts) {
		this.customPorts = customPorts;
	}

	
}
