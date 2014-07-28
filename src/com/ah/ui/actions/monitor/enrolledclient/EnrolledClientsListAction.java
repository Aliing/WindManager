package com.ah.ui.actions.monitor.enrolledclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.rest.client.models.ResponseModel;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.monitor.enrolledclient.tools.MacAddressUtil;
import com.ah.ui.actions.monitor.enrolledclient.tools.ResponseModelServiceImpl;
import com.ah.ui.actions.monitor.enrolledclient.tools.TransXMLToObjectImpl;
import com.ah.ui.actions.monitor.enrolledclients.entity.AppInfoForUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.DeviceList;
import com.ah.ui.actions.monitor.enrolledclients.entity.DeviceSecurityInfo;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientInfoUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledDeviceDetailInfo;
import com.ah.ui.actions.monitor.enrolledclients.entity.GeneralInfoForUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.NetworkInfoForUI;
import com.ah.ui.actions.monitor.enrolledclients.entity.RestrictionsInfo;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.opensymphony.xwork2.ActionContext;

public class EnrolledClientsListAction extends BaseAction implements QueryBo{
	
	private static final Tracer log = new Tracer(ResponseModelServiceImpl.class.getSimpleName());
	
	private static final long serialVersionUID = -3932200215720300014L;
	
	/**
	 * For enrolled client list table head
	 */
	public static final int ENROLLED_CLIENT_CHECK_BOX = 0;
	
	public static final int ENROLLED_CLIENT_NAME= 1;
	
	public static final int ENROLLED_CLIENT_USER_NAME= 2;
	
	public static final int ENROLLED_CLIENT_STATUS= 3;
	
	public static final int ENROLLED_CLIENT_OWNERSHIP = 4;
	
	public static final int ENROLLED_CLIENT_MANAGED = 5;
	
	public static final int ENROLLED_CLIENT_MAC_ADDRESS = 6;
	
//	public static final int ENROLLED_CLIENT_VERSION= 7;
	
	public static final int ENROLLED_CLIENT_PLATFORM= 8;
	
//	public static final int ENROLLED_CLIENT_PLATFORM= 9;
	
	public static final int ENROLLED_CLIENT_SYSTEM_MODE= 10;
	
	public static final int ENROLLED_CLIENT_LAST_CONNECTED = 11;
	
	public static final int ENROLLED_CLIENT_USER_GROUP = 12;
	
	public static final String OPERATION_SUCCEDD_MESSAGE = "Succeed";
	
	public static final String OPERATION_FAIL_MESSAGE = "Failed";
	
	public static final String OPERATION_REFRESH = "refresh";
	
	public static final String ENROLLED_DEVICE_REQUEST_ACTIVE_STATUS = "1" ;
	
	//'1,2' is used to get enrolled clients in mdm server
	public static final String ENEOLLED_DEVICE_ACTIVE_STATUS = "1,2";
	
	//'0' is used to get unrolled client 
	public static final String ENROLLED_DEVICE_INACTIVE_STATUS = "0";
	
	//'0,1,2' is used to get all clients(managed or managing)
	public static final String ENEOLLED_DEVICE_STATUS = "0,1,2";
	
	public static final String ENROLLED_DEVICE_ATTRIBUTE_BLANK = "";
	
	public static final String 	ENROLLED_CLIENT_OPERATION_WIPE_VALUE ="Wipe";
	
	public static final String ENROLLED_CLIENT_OPERATION_UNENROLL_VALUE = "UnEnroll";
	
	public static final String ENROLLED_CLIENT_OPERATION_RETRIEVE_VALUE = "Retrieve";
	
	public static final String ENROLLED_CLIENT_OPERATION_DELETE_VALUE = "Delete";
	
	public static final String ENROLLED_CLIETN_OPERATION_LOCK_VALUE = "DeviceLock";
	
	public static final String ENROLLED_CLIETN_OPERATION_CLEAR_PASS_VALUE = "ClearPasscode";
	
	private boolean autoRefresh = true;
	
	private String selectedClientIDStr;
	
	private String selectedMacAddresses;
	
	private String deviceId;
	
	private String name;
	
	private long rcount;
	
	private int pcount = 0;
	
	public String getName(){
		return this.name;
	}
	
	public String getSelectedMacAddresses() {
		return selectedMacAddresses;
	}

	public void setSelectedMacAddresses(String selectedMacAddresses) {
		this.selectedMacAddresses = selectedMacAddresses;
	}

	public void setName(String name){
		this.name = name;
	}
	
	public int rct = 0;
	public int pct = 0;
	
	public String getDisplayName() {
		if ( null != getName()) {
			return getName().replace("\\", "\\\\").replace("'", "\\'");
		} else {
			return "";
		}
	}
	@Override
	public int getPageIndex(){
		int pageCount = getPageCount();
		if (pageIndex > pageCount || pageIndex < 1) {
			// Go to last page
			pageIndex = pageCount;
		}
		return pageIndex < 1 ? 1 : pageIndex;
	
	}
	
	public long getRowCount(){
		
		return rcount;
	}
	@Override
	public int getPageCount(){
		
		return pcount;
	}

	public void setRowCount(int rc){
		this.rcount = (long)rc;
	}
	
	public void setPageCount(int pc){
		this.pcount = pc;
	}
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getSelectedClientIDStr() {
		return selectedClientIDStr;
	}

	public void setSelectedClientIDStr(String selectedClientIDStr) {
		this.selectedClientIDStr = selectedClientIDStr;
	}

	@Override
	public String execute() throws Exception{
		try{
			if(null == operation){
				return showAll();
			}else if("pollEnrolledClientsList".equals(operation)){
				try{
					super.removeSessionAttributes();
					enableSorting();
					enablePaging();
					jsonArray = new JSONArray(getUpdates());
					return "json";
				}catch(Exception e){
					jsonArray = new JSONArray(new Vector<JSONObject>());
					return "json";
				}
			}else if("refreshFromCache".equals(operation)){
				getInitSelectedColumns();
				enableSorting();
				enablePaging();
				Integer tn = (Integer)MgrUtil
				    .getSessionAttribute(boClass.getSimpleName() + "TotalNumber");
		        Integer tp = (Integer)MgrUtil
				    .getSessionAttribute(boClass.getSimpleName() + "TotalPages");
				
				@SuppressWarnings("unchecked")
				List<EnrolledClientInfoUI> tem = (List<EnrolledClientInfoUI>)MgrUtil
						.getSessionAttribute(boClass.getSimpleName() + "Page");
				try{
					if((tem != null) && (tn != null) && (tp != null)){
						setPageCount(tp);
						setRowCount(tn);
						page = tem;
					}
				    return SUCCESS;
				}catch(Exception e){
					if((tem != null) && (tn != null) && (tp != null)){
						setPageCount(tp);
						setRowCount(tn);
						page = tem;
					}
					return SUCCESS;
				}
			}else if(operation.equals("lockDevices")){
				ResponseModel res = new ResponseModelServiceImpl().operationOnClient(getDomain().getInstanceId(), getStringIdList(selectedIds), ENROLLED_CLIETN_OPERATION_LOCK_VALUE);
				if(res == null){
					clearErrorsAndMessages();
					addActionError("The selected clients cannot be locked. Please try again later");
					return showAll();
				}else{
					addActionMessage("The selected clients were successfully locked");
					return showAll();
				}
			}else if(operation.equals("clearPasscode")){
				ResponseModel res = new ResponseModelServiceImpl().operationOnClient(getDomain().getInstanceId(), getStringIdList(selectedIds), ENROLLED_CLIETN_OPERATION_CLEAR_PASS_VALUE);
				if(res == null){
					clearErrorsAndMessages();
					addActionError("The passcodes on the selected clients cannot be cleared. Please try again later.");
					return showAll();
				}else{
					addActionMessage("The passcodes on the selected clients were successfully cleared.");
					return showAll();
				}
			}else{
				baseCustomizationOperationJson();
				if (jsonObject != null && jsonObject.length() > 0) {
					return "json";
				}
				baseOperation();
				if("lastPage".equals(operation)){
					pageIndex = (Integer)MgrUtil.getSessionAttribute(boClass.getSimpleName() + "TotalPages");
				}else if("gotoPage".equals(operation)){
					int mp = (Integer)MgrUtil.getSessionAttribute(boClass.getSimpleName() + "TotalPages");
					if(pageIndex > mp){
						pageIndex = mp;
					}
				}
				getInitSelectedColumns();
			    preparePage();
			   return SUCCESS;
			}
		}catch(Exception e){
			clearErrorsAndMessages();
			addActionError("The operation is invalid");
			//return prepareActionError(e);
		}
		return SUCCESS;
	}
	
	public String showAll() throws Exception{
		if(QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", getDomain()).isEnableClientManagement()){
			autoRefresh = false;
		}
		getInitSelectedColumns();
		preparePage();
		return SUCCESS;
	}
	
	public String showDetails(){
		autoRefresh = false;
		ResponseModel res = new ResponseModelServiceImpl().getNetworkInfo(getDomain().getInstanceId(), deviceId);
		if(res == null){
			return "enrolledList";
		}
		validateGoogleMapKey();
		getEnrolledClientDetailInformation();
		return SUCCESS;
	}

	public String deleteDevices() throws Exception{
		if(OPERATION_SUCCEDD_MESSAGE.equals(doDeleteDevices())){
			doDeleteDevices();
			getInitSelectedColumns();
			preparePage();
			clearErrorsAndMessages();
			addActionMessage(getText("error.enrolled.device.network.del.succ"));
			return SUCCESS;
		}else{
			getInitSelectedColumns();
			preparePage();
			if(hasActionErrors()){
				return SUCCESS;
			}
			clearErrorsAndMessages();
			addActionError(getText("error.enrolled.device.network.del.fail"));
			return SUCCESS;
		}
	}
	


	public String wipeDevices() throws Exception{
		if(OPERATION_SUCCEDD_MESSAGE.equals(doWipeDevices())){
			getInitSelectedColumns();
			preparePage();
			clearErrorsAndMessages();
			addActionMessage(getText("error.enrolled.device.network.wipe.succ"));
			return SUCCESS;
		}else{
			getInitSelectedColumns();
			preparePage();
			if(hasActionErrors()){
				return SUCCESS;
			}
			clearErrorsAndMessages();
			addActionError(getText("error.enrolled.device.network.wipe.fail"));
			return SUCCESS;
		}
	}
	public String refreshClients() throws Exception{
		getInitSelectedColumns();
		doRefreshOperation();
		return SUCCESS;
	}
	
	public String unenrolledClients() throws Exception{
		if(OPERATION_SUCCEDD_MESSAGE.equals(doUnenrollDeviceOp(selectedClientIDStr))){
			getInitSelectedColumns();
			preparePage();
			clearErrorsAndMessages();
			addActionMessage(getText("error.enrolled.device.network.unenroll.succeed"));
			return SUCCESS;
		}else{
			getInitSelectedColumns();
			preparePage();
			if(hasActionErrors()){
				return SUCCESS;
			}
			clearErrorsAndMessages();
			addActionError(getText("error.enrolled.device.network.unenroll.fail"));
			return SUCCESS;
		}
		
		
	}
	
	public String retrieveDevices() throws Exception{
		if(OPERATION_SUCCEDD_MESSAGE.equals(doRetrieveDevices())){
			getInitSelectedColumns();
			preparePage();
			clearErrorsAndMessages();
			addActionMessage(getText("error.enrolled.device.network.retrive.succeed"));
			return SUCCESS;
		}else{
			getInitSelectedColumns();
			preparePage();
			if(hasActionErrors()){
				return SUCCESS;
			}
			clearErrorsAndMessages();
			addActionError(getText("error.enrolled.device.network.retrive.fail"));
			return SUCCESS;
		}
	}

	private String doRetrieveDevices() {
		ResponseModel res =new ResponseModelServiceImpl().operationOnClient(getDomain().getInstanceId(),selectedClientIDStr,ENROLLED_CLIENT_OPERATION_RETRIEVE_VALUE);
		if(res != null) {
			return OPERATION_SUCCEDD_MESSAGE;
		}else{
			return OPERATION_FAIL_MESSAGE;
		}
	}

	private String doDeleteDevices() {
		 ResponseModel res = new ResponseModelServiceImpl().operationOnClient(getDomain().getInstanceId(),selectedClientIDStr,ENROLLED_CLIENT_OPERATION_DELETE_VALUE);
		 if(res != null){
			 return OPERATION_SUCCEDD_MESSAGE;
		 }else{
			 return OPERATION_FAIL_MESSAGE;
		 }
	}
	
	private String doWipeDevices(){
		ResponseModel res = new ResponseModelServiceImpl().operationOnClient(getDomain().getInstanceId(),selectedClientIDStr,ENROLLED_CLIENT_OPERATION_WIPE_VALUE);
		if(res == null){
			return OPERATION_FAIL_MESSAGE;
		}else{
			return OPERATION_SUCCEDD_MESSAGE;
		}
	}
	
	private String doUnenrollDeviceOp(String selectedClientIDStr2) {
		ResponseModel res = new ResponseModelServiceImpl().operationOnClient(getDomain().getInstanceId(),selectedClientIDStr2,ENROLLED_CLIENT_OPERATION_UNENROLL_VALUE);
		if(res != null){
			return OPERATION_SUCCEDD_MESSAGE;
		}else{
			return OPERATION_FAIL_MESSAGE;
		}
	}

	@SuppressWarnings("unchecked")
	private void prepareEnrolledList() {
		List<EnrolledClientInfoUI> deviceList = new ArrayList<EnrolledClientInfoUI>();
		DeviceList deviceLists = preparePageAttributes(String.valueOf(pageIndex-1),String.valueOf(paging.getPageSize()),ENEOLLED_DEVICE_STATUS,ENROLLED_DEVICE_ATTRIBUTE_BLANK,sortParams);
		rcount = Integer.parseInt(deviceLists.getTotalNumber());
		pcount = Integer.parseInt(deviceLists.getTotalPages());
		deviceList = (List<EnrolledClientInfoUI>)deviceLists.getDeviceList();
		if(deviceList != null){
			forDisplay(deviceList);
			page = deviceList;
			MgrUtil.setSessionAttribute(boClass.getSimpleName() + "Page", page);
			MgrUtil.setSessionAttribute(boClass.getSimpleName() + "TotalNumber", new Integer((int)rcount));
			MgrUtil.setSessionAttribute(boClass.getSimpleName() + "TotalPages", new Integer(pcount));
		}else{
			clearErrorsAndMessages();
			addActionError(getText("error.enrolled.device.network.fail.connect"));
			page = new ArrayList<EnrolledClientInfoUI>();
			MgrUtil.setSessionAttribute(boClass.getSimpleName() + "Page", page);
			MgrUtil.setSessionAttribute(boClass.getSimpleName() + "TotalNumber", new Integer(0));
			MgrUtil.setSessionAttribute(boClass.getSimpleName() + "TotalPages", new Integer(0));
		}
	}
	
	private void forDisplay(List<EnrolledClientInfoUI> deviceList){
		if(deviceList != null){
			for(EnrolledClientInfoUI device: deviceList){
				Long tempLastConnectTime;
				try{
					device.setId(Long.valueOf(device.getDeviceId()));
					tempLastConnectTime = Long.valueOf(device.getLastCon());
					device.setLastCon(new Date(tempLastConnectTime).toString());
					device.setWifiMac(device.getWifiMac().toUpperCase().replaceAll("[:|-]*", ""));
					//device.setWifiMac(MacAddressUtil.addDelimiter(device.getWifiMac(), 2, ":"));
				}catch(Exception e){
					log.error(EnrolledClientsListAction.class.getSimpleName()+":forDisplay()", "ClassCastException", e);
					e.printStackTrace();
				}	
			}
		}
	}
	
	public void doRefreshOperation(){
		try {
			preparePage();
			clearErrorsAndMessages();
			addActionMessage(getText("error.enrolled.device.network.refresh.succ"));
		} catch (Exception e) {
			clearErrorsAndMessages();
			addActionError(getText("error.enrolled.device.network.refresh.fail"));
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public DeviceList preparePageAttributes(String pageNum,String pageSize,String status,String active,SortParams sortParam){
		return getDeviceByAtt(getDomain().getInstanceId(), pageNum, pageSize,status,ENROLLED_DEVICE_ATTRIBUTE_BLANK,ENROLLED_DEVICE_ATTRIBUTE_BLANK,active,sortParam);
	}
	
	protected List<HmTableColumn> getInitSelectedColumns() {
		autoRefresh = true;
		List<HmTableColumn> tem = new ArrayList<HmTableColumn>();
		tem.add(new HmTableColumn(ENROLLED_CLIENT_CHECK_BOX));
		tem.add(new HmTableColumn(ENROLLED_CLIENT_NAME));
		tem.add(new HmTableColumn(ENROLLED_CLIENT_USER_NAME));
		tem.add(new HmTableColumn(ENROLLED_CLIENT_STATUS));
		tem.add(new HmTableColumn(ENROLLED_CLIENT_MANAGED));
		tem.add(new HmTableColumn(ENROLLED_CLIENT_OWNERSHIP));
		tem.add(new HmTableColumn(ENROLLED_CLIENT_MAC_ADDRESS));
		tem.add(new HmTableColumn(ENROLLED_CLIENT_PLATFORM));
		tem.add(new HmTableColumn(ENROLLED_CLIENT_SYSTEM_MODE));
	//	tem.add(new HmTableColumn(ENROLLED_CLIENT_PLATFORM));
		tem.add(new HmTableColumn(ENROLLED_CLIENT_LAST_CONNECTED));
	//	tem.add(new HmTableColumn(ENROLLED_CLIENT_USER_NAME));
		tem.add(new HmTableColumn(ENROLLED_CLIENT_USER_GROUP));
		selectedColumns = tem;
		return tem;
	}
	
	private GeneralInfoForUI generalInformation;
	
	private List<AppInfoForUI> appcationInformation;
	
	private NetworkInfoForUI networkInformation;
	
	private List<RestrictionsInfo> restictionInformation;
	
	private EnrolledDeviceDetailInfo enrolledClientDetails;
	
	private DeviceSecurityInfo enrolledSecurity;
	
	private boolean enableGoogleMapKey = false;
	
	private boolean markOnPrimess = false;
	
	public boolean isMarkOnPrimess() {
		return markOnPrimess;
	}

	public void setMarkOnPrimess(boolean markOnPrimess) {
		this.markOnPrimess = markOnPrimess;
	}

	public boolean isEnableGoogleMapKey() {
		return enableGoogleMapKey;
	}

	public void setEnableGoogleMapKey(boolean enableGoogleMapKey) {
		this.enableGoogleMapKey = enableGoogleMapKey;
	}

	public DeviceSecurityInfo getEnrolledSecurity() {
		return enrolledSecurity;
	}

	public void setEnrolledSecurity(DeviceSecurityInfo enrolledSecurity) {
		this.enrolledSecurity = enrolledSecurity;
	}

	public GeneralInfoForUI getGeneralInformation() {
		return generalInformation;
	}

	public void setGeneralInformation(GeneralInfoForUI generalInformation) {
		this.generalInformation = generalInformation;
	}

	public List<AppInfoForUI> getAppcationInformation() {
		return appcationInformation;
	}

	public void setAppcationInformation(List<AppInfoForUI> appcationInformation) {
		this.appcationInformation = appcationInformation;
	}

	public NetworkInfoForUI getNetworkInformation() {
		return networkInformation;
	}

	public void setNetworkInformation(NetworkInfoForUI networkInformation) {
		this.networkInformation = networkInformation;
	}

	public List<RestrictionsInfo> getRestictionInformation() {
		return restictionInformation;
	}

	public void setRestictionInformation(
			List<RestrictionsInfo> restictionInformation) {
		this.restictionInformation = restictionInformation;
	}

	public EnrolledDeviceDetailInfo getEnrolledClientDetails() {
		return enrolledClientDetails;
	}

	public void setEnrolledClientDetails(
			EnrolledDeviceDetailInfo enrolledClientDetails) {
		this.enrolledClientDetails = enrolledClientDetails;
	}

	public void getEnrolledClientDetailInformation(){
		
		// Get Restriction information
		restictionInformation = new TransXMLToObjectImpl().getRestrictionsInfoList(new ResponseModelServiceImpl().getRestrictionInfo(getDomain().getInstanceId(), deviceId));
		if(restictionInformation == null){
			restictionInformation =  new ArrayList<RestrictionsInfo>();
		}
		
		//Get application information
		appcationInformation = new TransXMLToObjectImpl().getApplicationInfo(new ResponseModelServiceImpl().getApplicationInfo(getDomain().getInstanceId(), deviceId));
		 if(appcationInformation == null){
			 appcationInformation = new ArrayList<AppInfoForUI>();
		 }else{
			handleApplicationDataSize(appcationInformation);
		}
		 
		 //Get detail information
		 enrolledClientDetails = new TransXMLToObjectImpl().getDeviceDetail(new ResponseModelServiceImpl().getDeviceDetail(getDomain().getInstanceId(), deviceId));
			if(enrolledClientDetails == null){
				enrolledClientDetails = new EnrolledDeviceDetailInfo();
			}
			
		//Get network information
			networkInformation = new TransXMLToObjectImpl().getNetworkInfo(new ResponseModelServiceImpl().getNetworkInfo(getDomain().getInstanceId(), deviceId));
			try{
				networkInformation.setCellularTech(enrolledClientDetails.getCellularTechnology());
				networkInformation.setIpAddress(enrolledClientDetails.getPublicIp());
				networkInformation.setModemFirmware(enrolledClientDetails.getModemFirmwareVersion());
				networkInformation.setMapAddress(enrolledClientDetails.getAddress());
				networkInformation.setLatitude(enrolledClientDetails.getLatitude());
				networkInformation.setLongitude(enrolledClientDetails.getLongitude());
				networkInformation.setWifiMac(MacAddressUtil.addDelimiter(networkInformation.getWifiMac(), 2, ":"));
				networkInformation.setBlueToothMAC(MacAddressUtil.addDelimiter(networkInformation.getBlueToothMAC(), 2, ":"));
			}catch(Exception e){
				e.printStackTrace();
				log.error(EnrolledClientsListAction.class.getSimpleName() + " :getEnrolledClientDetailInformation()", "throw an exception", e);
			}
			//Get security inforamtion
			enrolledSecurity = new TransXMLToObjectImpl().getSecurityInfo(new ResponseModelServiceImpl().getSecurityinfo(getDomain().getInstanceId(), deviceId));
			
			
		//Get general information
			GeneralInfoForUI u = new GeneralInfoForUI();
			try{
				u.setStatus(String.valueOf(enrolledClientDetails.getActiveStatus()));
				u.setLastConnect(new Date(Long.valueOf(enrolledClientDetails.getLastCon())).toString());
				u.setBatteryLevel(getPercentage(enrolledClientDetails.getBatteryLevel()) + "Capacity");
				u.setBatteryPercentage(getPercentage(enrolledClientDetails.getBatteryLevel()));
				double allSpace=Double.parseDouble(enrolledClientDetails.getDeviceCapacity());
				double availableSpace=Double.parseDouble(enrolledClientDetails.getAvailableDeviceCapacity());
				double usedSpace=allSpace-availableSpace;
				String tempStoragePercentage = String.valueOf(usedSpace/allSpace).toString();
				u.setStoragePercentage(getPercentage(tempStoragePercentage));
				u.setDeviceStorage(enrolledClientDetails.getAvailableDeviceCapacity().substring(0,enrolledClientDetails.getAvailableDeviceCapacity().indexOf(".")+3) 
						+ "GB free of "
						+ enrolledClientDetails.getDeviceCapacity().substring(0,enrolledClientDetails.getDeviceCapacity().indexOf(".")+3) + "GB");
				u.setDeviceType(enrolledClientDetails.getModelName());
				u.setPhoneNum(networkInformation.getPhoneNumber());
				u.setUdid(enrolledClientDetails.getUdid());
				u.setDataProtection(enrolledSecurity.getDataProtection() == true ? "true": "false");
				u.setPasswordPresent(enrolledSecurity.getPasscodePresent());
			}catch(Exception e){
				log.error(EnrolledClientsListAction.class.getSimpleName() + ": getGeneralInformation()",e.getMessage());
				e.printStackTrace();
			}
			generalInformation = u;
	}
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		return null;
	}
	
	public DeviceList getDeviceByAtt(String customId,String pageNum,String pageSize,String status,String ownerType,
			String osType, String active,SortParams sortParam){
		ResponseModel res = new ResponseModelServiceImpl().getDeviceListAll(customId, pageNum, pageSize, status,ownerType, osType, active,sortParam);
		if(res != null){
			if(res.getResponseCode() == 400){
				clearErrorsAndMessages();
				addActionError("Please enable the client onboarding setting first ");
				return new DeviceList();
			}
			DeviceList dList = new TransXMLToObjectImpl().getDeviceListWithTotalNumber(res);
			if(dList != null){
				return dList;
			}else{
				clearErrorsAndMessages();
				addActionError("Time out");
				return new DeviceList();
			}
			
		}else{
			clearErrorsAndMessages();
			addActionError("Time out");
			return new DeviceList();
		}
	}
	
	public boolean getEnablePageAutoRefreshSetting(){
		return autoRefresh;
	}
	
	
	public void preparePage() throws Exception {
		super.removeSessionAttributes();
		enableSorting();
		enablePaging();
		prepareEnrolledList();
	}
	
	public void enableSorting(){
		String sessionKey = boClass.getSimpleName() + "Sorting";
		sortParams = (SortParams) MgrUtil.getSessionAttribute(sessionKey);
		if (sortParams == null) {
			sortParams = new SortParams("DeviceId");

			MgrUtil.setSessionAttribute(sessionKey, sortParams);
		}
		// So every sort tag doesn't need to specify a session key
		ActionContext.getContext().put(PAGE_SORTING, sortParams);
	}
	public void prepare() throws Exception{
		super.prepare();
		setDataSource(EnrolledClientInfoUI.class);
		setSelectedL2Feature(L2_FEATURE_ENROLLED_CLIENTS);
		keyColumnId = ENROLLED_CLIENT_STATUS;
		this.tableId = HmTableColumn.TABLE_ENROLLED_MDM_CLIENT;
	} 
	
	private String getPercentage(String value){
		Double tempValue = Double.parseDouble(value)*100;
		if(String.valueOf(tempValue).indexOf(".") > 0 
				&& String.valueOf(tempValue).substring(String.valueOf(tempValue).indexOf(".") + 1).length() >= 2 ){
					return String.valueOf(tempValue).substring(0,String.valueOf(tempValue).indexOf(".") + 3) + "%";
			}
		if(String.valueOf(tempValue).indexOf(".") > 0
				&& String.valueOf(tempValue).substring(String.valueOf(tempValue).indexOf(".") + 1 ).length() < 2 ){
			return String.valueOf(tempValue) + "0%";
		}
		return value + "%";
	}
	
	@SuppressWarnings("unchecked")
	private Collection<JSONObject> refreshFromSession(List<EnrolledClientInfoUI> enrolledClientList) throws Exception{
		Collection<JSONObject> updates = new Vector<JSONObject>();
		JSONObject update = new JSONObject();
		MgrUtil.setSessionAttribute(boClass.getSimpleName() + "Page",enrolledClientList);
		update.put("id", -1);
		updates.add(update);
		return updates;
	}
	
	@SuppressWarnings("unchecked")
	private Collection<JSONObject> getUpdates()throws Exception{
		Collection<JSONObject> updates = new Vector<JSONObject>();
		
		List<EnrolledClientInfoUI> oldEnrolledClientList = (List<EnrolledClientInfoUI>)MgrUtil
				.getSessionAttribute(boClass.getSimpleName() + "Page");
		int rt = (Integer)MgrUtil
				.getSessionAttribute(boClass.getSimpleName() + "TotalNumber");
		
		DeviceList deviceLists = preparePageAttributes(String.valueOf(pageIndex-1),String.valueOf(paging.getPageSize())
				,ENEOLLED_DEVICE_STATUS,ENROLLED_DEVICE_ATTRIBUTE_BLANK,sortParams);
		
		List<EnrolledClientInfoUI> newEnrolledClientList = deviceLists.getDeviceList();
		
		if((null == newEnrolledClientList) || (newEnrolledClientList.size() == 0)){
			MgrUtil.setSessionAttribute(boClass.getSimpleName() + "TotalNumber",new Integer(rct));
	        MgrUtil.setSessionAttribute(boClass.getSimpleName() + "TotalPages",new Integer(pct));
		    return refreshFromSession(new ArrayList<EnrolledClientInfoUI>());
		}
		
		forDisplay(newEnrolledClientList);
		
		rct = Integer.parseInt(deviceLists.getTotalNumber());
		pct = Integer.parseInt(deviceLists.getTotalPages());
		
		if((oldEnrolledClientList == null) || (rt != rct)){
            MgrUtil
				.setSessionAttribute(boClass.getSimpleName() + "TotalNumber",new Integer(rct));
		    MgrUtil
				.setSessionAttribute(boClass.getSimpleName() + "TotalPages",new Integer(pct));
			return refreshFromSession(newEnrolledClientList);
		}
		if (oldEnrolledClientList.size() != newEnrolledClientList.size()) {
			// full refresh
			MgrUtil
			    .setSessionAttribute(boClass.getSimpleName() + "TotalNumber",new Integer(rct));
	        MgrUtil
			    .setSessionAttribute(boClass.getSimpleName() + "TotalPages",new Integer(pct));
			return refreshFromSession(newEnrolledClientList);
		}
		for (int i = 0; i < oldEnrolledClientList.size(); i++) {
			EnrolledClientInfoUI client = oldEnrolledClientList.get(i);
			EnrolledClientInfoUI newClient = newEnrolledClientList.get(i);

			// need refresh page when some attribute changed.
			if(client.getDeviceId().equals(newClient.getDeviceId())){
				if (!client.getStatus().equals(newClient.getStatus())
						|| !client.getManaged().equals(newClient.getManaged())
//						|| !(client.getDataProtected().equals(newClient.getDataProtected()))
						|| !(client.getWifiMac().equals(newClient.getWifiMac()))
						|| !(client.getOsVersion().equals(newClient.getOsVersion()))
						|| !(client.getName().equals(newClient.getName()))
						|| !(client.getEnrollUserName().equals(newClient.getEnrollUserName()))
						|| !(client.getOwnerShip().equals(newClient.getOwnerShip()))
						|| !(client.getLastCon().equals(newClient.getLastCon()))
						|| !(client.getSysMode().equals(newClient.getSysMode()))
//						|| !(client.getPasscodePresented().equals(newClient.getPasscodePresented()))
//						|| !(client.getEnrollUserGroup().equals(newClient.getEnrollUserGroup()))
//						|| !(client.getEnrollUserName().equals(newClient.getEnrollUserName()))
						) {
					// full refresh
					MgrUtil
					.setSessionAttribute(boClass.getSimpleName() + "TotalNumber",new Integer(rct));
			        MgrUtil
					.setSessionAttribute(boClass.getSimpleName() + "TotalPages",new Integer(pct));
					return refreshFromSession(newEnrolledClientList);
				}
			}
			
		}

		oldEnrolledClientList = newEnrolledClientList;
		return updates;
	}
	public String getStringIdList(List<Long> idList){
		String str = "";
		if(idList.size() == 1){
			return idList.get(0).toString();
		}
		if(idList.size() > 1){
			str = idList.get(0).toString();
			for(int i = 1 ; i<idList.size() ; i++){
				str += ","+ idList.get(i);
			}
		}
		return str;
	}
	
	private void handleApplicationDataSize(List<AppInfoForUI> appList){
		for(AppInfoForUI app : appList){
			app.setBundleSize(applicationDataTransfer(Double.parseDouble(app.getBundleSize())));
			app.setDynamicSize(applicationDataTransfer(Double.parseDouble(app.getDynamicSize())));
		}
	}
	private String applicationDataTransfer(double size){
		double tempsize = size/1024/1024/1024;
		double tempMbSize = size/1024/1024;
		double tempKbSize = size/1024;
		try{
			if(tempsize > 1){
				if((tempsize + "").length() - (tempsize + "").indexOf(".") >= 2){
					return (tempsize + "" ).substring(0,(tempsize + "").indexOf(".") + 3 ) + "GB";
				}else{
					return tempsize + "GB";
				}
			}else if(tempMbSize > 1){
				if((tempMbSize + "").length() - (tempMbSize + "").indexOf(".") >= 2){
					return (tempMbSize + "" ).substring(0,(tempMbSize + "").indexOf(".") + 3 ) + "MB";
				}else{
					return tempMbSize + "MB";
				}
			}else if(tempKbSize> 1){
				if((tempKbSize+ "").length() - (tempKbSize+ "").indexOf(".") >= 2){
					return (tempKbSize+ "" ).substring(0,(tempKbSize+ "").indexOf(".") + 3 ) + "KB";
				}else{
					return tempKbSize + "KB";
				}
			}else{
				if((size + "").length() - (size + "").indexOf(".") >= 2){
					return (size + "" ).substring(0,(size + "").indexOf(".") + 3 ) + "B";
				}else{
					return size + "KB";
				}
			}
		}catch(Exception e){
			return null;
		}
		
	}
	//add to get Google Map Key
	public void validateGoogleMapKey(){
		getGmeKey();
	}
	public String getGmeKey() {
		String licenseKey = NmsUtil.getGmLicenseKey();
		if (NmsUtil.isPlanner() || NmsUtil.isDemoHHM()) {
			// Usage is free for these servers
			markOnPrimess =false;
			String apiKey = NmsUtil.getGmAPIKey();
			if (apiKey != null && apiKey.length() > 0) {
				return "&key=" + apiKey;
			} else {
				log.info_ln("GM API key missing.");
				return "";
			}
		} else if (NmsUtil.isHostedHMApplication()) {
			// HMOL customers
			markOnPrimess = false;
			if (licenseKey != null && licenseKey.length() > 0) {
				String vhmId = getDomain().getVhmID();
				if (vhmId == null || vhmId.length() == 0) {
					log.info_ln("Missing - vhm - id");
					vhmId = "no-vhm-id";
				}
				return "&client=" + licenseKey + "&channel=" + vhmId;
			} else {
				log.info_ln("GM License key missing.");
				return "";
			}
		} else {
			// on-premise customers
			markOnPrimess = true;
			if (licenseKey != null && licenseKey.length() > 0) {
				String systemId = BeLicenseModule.HIVEMANAGER_SYSTEM_ID;
				if (systemId == null || systemId.length() == 0) {
					log.info_ln("Missing system id");
					/*systemId = "no-system-id";*/
					enableGoogleMapKey = true;
					return NmsUtil.getGmAPIKey();
				}
				return "&client=" + licenseKey + "&channel=" + systemId;
			} else {
				log.info_ln("GM License key missing.");
				enableGoogleMapKey = true;
				return NmsUtil.getGmAPIKey();
			}
		}
	}
}
