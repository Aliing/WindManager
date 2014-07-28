package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.ah.be.service.ApplicationService;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.Application;
import com.ah.bo.network.NetworkService;
import com.ah.ui.actions.BaseAction;

public class ApplicationServiceAction extends BaseAction {

	private static final long serialVersionUID = -943764055701053587L;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {	
			return fw;
		}

		try {
			if ("settings".equals(operation)) {
				if(ids != null && !"".equals(ids)){
					String[] ss = ids.split(",");
					StringBuffer sb = new StringBuffer();
					if(ss.length > 2){
						for(int i=0; i<2; i++){
							NetworkService ns = QueryUtil.findBoById(NetworkService.class, Long.parseLong(ss[i]));
							sb.append(ns.getServiceName().substring(NetworkService.L7_SERVICE_NAME_PREFIX.length()));
							sb.append(", ");
						}
						sb.append(" ...");
						names = sb.toString();
					}else if(ss.length == 2){
						for(String s: ss){
							NetworkService ns = QueryUtil.findBoById(NetworkService.class, Long.parseLong(s));
							sb.append(ns.getServiceName().substring(NetworkService.L7_SERVICE_NAME_PREFIX.length()));
							sb.append(", ");
						}
						names = sb.toString().substring(0,sb.toString().lastIndexOf(","));
					}else{
						List<?> list = QueryUtil.executeQuery("select serviceName, idleTimeout from "+NetworkService.class.getSimpleName(), null,
								new FilterParams("id", Long.parseLong(ss[0])), 1);
						if(list != null && !list.isEmpty()){
							Object[] values = (Object[])list.get(0);
							names = ((String)values[0]).substring(NetworkService.L7_SERVICE_NAME_PREFIX.length());
							timeout = (Integer)values[1];
						}
					}
					return "appServiceSettings";
				}else{
					addActionError("Please select one item.");
					return "appServiceJson";
				}
				
			} else if ("create".equals(operation)) {
				try{
					if(ids != null && !"".equals(ids)){
						String sql = "UPDATE NETWORK_SERVICE SET idleTimeout="+timeout+" WHERE id in ("+ids+")";
						QueryUtil.executeNativeUpdate(sql);
					}
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus",true);
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, "Update system application idle timeout");
				}catch(Exception e){
					jsonObject.put("resultStatus",false);
					jsonObject.put("errMsg","Unknown Error.");
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Update system application idle timeout");
					return "json";
				}
				return "json";
			} else {
				ApplicationService appService = new ApplicationService();
				List<Application> fixedAppList = appService.getApplicationWithBytes(getDomain());
				List<NetworkService> appServiceList = QueryUtil.executeQuery(NetworkService.class, null, new FilterParams("servicetype = :s1 and owner.id = :s2",
						new Object[]{NetworkService.SERVICE_TYPE_L7, getDomain().getId()}));
				
				List<Integer> appCodes = new ArrayList<Integer>();
				for(NetworkService ns : appServiceList){
					appCodes.add(ns.getAppId());
				}
				for(Application app : fixedAppList){
					Integer appcode = app.getAppCode();
					if(null != appCodes && !appCodes.isEmpty() && appCodes.contains(appcode)){
						for(NetworkService ns : appServiceList){
							if(appcode == ns.getAppId()){
								app.setId(ns.getId());
								break;
							}
						}
					}else{
						NetworkService serviceDto = new NetworkService();
						String appName = NetworkService.L7_SERVICE_NAME_PREFIX+app.getShortName();
						if(appName.length() > 32){
							appName = appName.substring(0, 32);
						}
						serviceDto.setServiceName(appName);
						serviceDto.setProtocolNumber(0);
						serviceDto.setPortNumber(0);				
						serviceDto.setIdleTimeout(300);
						serviceDto.setDescription(app.getAppName());
						serviceDto.setAlgType((short)0);
						serviceDto.setServiceType(NetworkService.SERVICE_TYPE_L7);
						serviceDto.setAppId(app.getAppCode());
						serviceDto.setDefaultFlag(false);
						serviceDto.setOwner(getDomain());
						serviceDto.setCliDefaultFlag(false);
						Long id = QueryUtil.createBo(serviceDto);
						app.setId(id);
					}
				}
//				for (Application app : fixedAppList) {
//					NetworkService service = QueryUtil.findBoByAttribute(NetworkService.class, "appId", app.getAppCode(), getDomain().getId());
//					if(null != service){
//						app.setId(service.getId());
//					}else{
//						NetworkService serviceDto = new NetworkService();
//						serviceDto.setServiceName(NetworkService.L7_SERVICE_NAME_PREFIX+app.getAppName());
//						serviceDto.setProtocolNumber(0);
//						serviceDto.setPortNumber(0);				
//						serviceDto.setIdleTimeout(300);
//						serviceDto.setDescription(app.getAppName());
//						serviceDto.setAlgType((short)0);
//						serviceDto.setServiceType(NetworkService.SERVICE_TYPE_L7);
//						serviceDto.setAppId(app.getAppCode());
//						serviceDto.setDefaultFlag(false);
//						serviceDto.setOwner(getDomain());
//						serviceDto.setCliDefaultFlag(false);
//						Long id = QueryUtil.createBo(serviceDto);
//						app.setId(id);
//					}
//				}
				allApps = fixedAppList;
				
				for(Application app: allApps){
					allAppNames.add(app.getShortName());
					if(null != allGroupNames && !allGroupNames.isEmpty()){
						if(!allGroupNames.contains(app.getAppGroupName())){
							allGroupNames.add(app.getAppGroupName());
						}
					}else{
						allGroupNames.add(app.getAppGroupName());
					}
				}
				return INPUT;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_APPLICATION_SERVICE);
	}
	
	private List<Application> allApps = new ArrayList<Application>();
	private String ids;
	private String names;
	private Integer timeout;
	private List<String> allGroupNames = new ArrayList<>();
	private List<String> allAppNames = new ArrayList<>();
	
	
	
	public List<String> getAllGroupNames() {
		return allGroupNames;
	}

	public void setAllGroupNames(List<String> allGroupNames) {
		this.allGroupNames = allGroupNames;
	}

	public List<String> getAllAppNames() {
		return allAppNames;
	}

	public void setAllAppNames(List<String> allAppNames) {
		this.allAppNames = allAppNames;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public List<Application> getAllApps() {
		return allApps;
	}

	public void setAllApps(List<Application> allApps) {
		this.allApps = allApps;
	}
	
}
