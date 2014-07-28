package com.ah.ui.actions.hiveap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.DeviceInventory;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApFilter;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.FilterParamsFactory;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mobility.HiveProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.admin.DeviceTagUtil;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.bo.BoAssistant;
import com.ah.ws.rest.client.utils.ClientUtils;
import com.ah.ws.rest.client.utils.DeviceImpUtils;
import com.ah.ws.rest.client.utils.DeviceUtils;
import com.ah.ws.rest.client.utils.RedirectorResUtils;
import com.ah.ws.rest.models.ModelConstant;
import com.ah.ws.rest.models.SerialNumberList;
import com.ah.ws.rest.models.SerialNumbers;
import com.sun.jersey.api.client.ClientHandlerException;

public class DeviceInventoryAction extends BaseAction implements QueryBo {
	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(DeviceInventoryAction.class
			.getSimpleName());

	@Override
	public String execute() throws Exception {
		this.prepareRightL2MenuFeature();
		resetPermission();
		try {
			if (null == operation
					|| "viewWithFilter".equals(operation)
					|| "removeFilter".equals(operation)
					|| "nothing".equals(operation)) {
				this.prepareCurrentPagingFilter();
			}
			
			if ("refresh".equals(operation)) {
				DeviceImpUtils.getInstance().syncDeviceInventoriesWithRedirector(getDomain());
//				List<DeviceInventory> lst = new ArrayList<DeviceInventory>();
//				for(int i=0; i<30;i++) {
//					DeviceInventory aa = new DeviceInventory();
//					aa.setOwner(getDomain());
//					if (i<10) {
//						aa.setSerialNumber("aaaaaaaaaaaaa" + i);
//					} else {
//						aa.setSerialNumber("aaaaaaaaaaaa" + i);
//					}
//					lst.add(aa);
//				}
//				QueryUtil.bulkCreateBos(lst);
				return prepareDeviceInventoryBoList();
			} else if ("export".equals(operation)) {
				return this.exportDeviceInventories();
			} else if ("requestFilterValues".equals(operation)) {
				log.info("execute", "requestFilterValues operation");
				jsonObject = diFilterHelper.prepareFilterValues(diFilter.getFilterName());
				return "json";
			} else if ("removeFilter".equals(operation)) {
				log.info("execute", "removeFilter operation");
				MgrUtil.setSessionAttribute(UNMANAGED_HIVEAP_CURRENT_FILTER, "-1");
				diFilterHelper.removeHiveApFilter(diFilter.getFilterName());
				this.diFilter = new DeviceInventoryFilterForm();
				return prepareDeviceInventoryBoList();
			} else if ("viewWithFilter".equals(operation)) {
				MgrUtil.setSessionAttribute(UNMANAGED_HIVEAP_CURRENT_FILTER, "-1");
				this.diFilter = new DeviceInventoryFilterForm();
				return prepareDeviceInventoryBoList();
			} else if ("search".equals(operation)) {
				log.info("execute", "search operation");
				diFilterHelper.saveToSessionFilterList(diFilter);
				filterParams = this.diListPageObject.prepareSearchOperation(diFilter);
				setSessionFiltering();
				return prepareDeviceInventoryBoList();
			} else if ("importOPDevice".equals(operation)) {
				addLstForward("unManagedDevice");
				clearErrorsAndMessages();
				return operation;
			} else {
				baseOperation();
				return prepareDeviceInventoryBoList();
			}
		} catch (Exception e) {
			reportActionError(e);
			return prepareDeviceInventoryBoList();
		}
	}
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		//setSelectedL2Feature(L2_FEATURE_DEVICE_INVENTORY);
		keyColumnId = COLUMN_SERIAL_NUMBER;
		//this.tableId = HmTableColumn.TABLE_DEVICE_INVENTORY;
		
		if (this.isHMOnline()) {
			this.diListPageObject = new DiOnlineListPageObject();
		} else {
			this.diListPageObject = new DiOnPremiseListPageObject();
		}
		
		this.diListPageObject.prepareDataSource();
	}
	
	@Override
	protected int removeBos(Class<? extends HmBo> boClass, Collection<Long> ids)
			throws Exception {
		log.info("DeviceInventoryAction", "Customized remove.");
		AccessControl.checkUserAccess(getUserContext(),
				getSelectedL2FeatureKey(), CrudOperation.DELETE);
		int successRemovedCount = 0;
		boolean urlErrorMsg=false;
		if (isHMOnline()) {
			List<DeviceInventory> removeDevices = QueryUtil.executeQuery(DeviceInventory.class, null,
					new FilterParams("id", ids), getDomain().getId());
			
			List<String> serialNumbers = new ArrayList<String>();
			Collection<Long> removeIds = new ArrayList<Long>();
			
			for(DeviceInventory di: removeDevices){
				serialNumbers.add(di.getSerialNumber());
			}
			
			
			try {
				if (!serialNumbers.isEmpty()) {
					List<HiveAp> removedAps = QueryUtil.executeQuery(HiveAp.class, null,
							new FilterParams("serialNumber", serialNumbers), getDomain().getId());
					for(HiveAp oneAp: removedAps){
						removeIds.add(oneAp.getId());
					}
					
					Collection<Long> successLst = BoMgmt.getMapMgmt().removeHiveApsReturnRemovedIds(removeIds, true, true, null, false);
					successRemovedCount = successRemovedCount + successLst.size();
					removeApPropagated(removedAps, successLst);
					
					DeviceUtils diu = DeviceImpUtils.getInstance();
					diu.removeSerialNumberFromHm(removedAps, successLst, false, getDomain());
					
					// remove serial numbers success
					for (HiveAp hiveAp : removedAps) {
						serialNumbers.remove(hiveAp.getSerialNumber());
					}
					if (!serialNumbers.isEmpty()) {
						RedirectorResUtils ru = ClientUtils.getRedirectorResUtils();
						SerialNumbers sns = new SerialNumbers();
						sns.setVhmid(getDomain().getVhmID()==null ? "home": getDomain().getVhmID());
						sns.setSn(serialNumbers);
						
						List<SerialNumberList> retSerialNumbers = ru.removeSerialNumbers(sns);
						List<String> successSerial = new ArrayList<String>();
						for (SerialNumberList lst : retSerialNumbers) {
							if (lst.getStatus() == ModelConstant.SN_SUCCESS) {
								if (lst.getSn() != null) {
									successSerial.addAll(lst.getSn());
								}
							}
							if (lst.getStatus() == ModelConstant.SN_NOTEXIST) {
								if (lst.getSn() != null) {
									successSerial.addAll(lst.getSn());
								}
							}
						}
					
						successRemovedCount = successRemovedCount + successSerial.size();
						if (!successSerial.isEmpty()) {
							QueryUtil.removeBos(DeviceInventory.class, new FilterParams("serialNumber", successSerial));
							for(String str: successSerial) {
								generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("geneva_08.hm.missionux.removeserial.log",str));
							}
						}
					}
				}
			} catch (ClientHandlerException ex) {
				log.error(ex);
				urlErrorMsg=true;
			} catch (Exception e) {
				log.error(e);
			}
		} else {
			try {
				List<HiveAp> removedAps = QueryUtil.executeQuery(HiveAp.class, null,
						new FilterParams("id", ids),getDomain().getId());
	
				Collection<Long> successLst = BoMgmt.getMapMgmt().removeHiveApsReturnRemovedIds(ids, false, false, null, false);
				successRemovedCount = successRemovedCount + successLst.size();
				// propagate to other section;
				removeApPropagated(removedAps, successLst);
			} catch (Exception e) {
				log.error(e);
			}

		}

		int excepIds = ids.size()- successRemovedCount;
		if (excepIds>0) {
			String msg = "";
			if (excepIds==1) {
				msg="1 item";
			} else {  
				msg=excepIds + " items";
			}
			if (urlErrorMsg) {
				addActionError(MgrUtil.getUserMessage("error.remove.exception.urlerror",msg));
			} else {
				addActionError(MgrUtil.getUserMessage("error.remove.exception",msg));
			}
		}
		
		return successRemovedCount;
	}
	
	private int removeAllBos(Class<? extends HmBo> boClass,
			Collection<Long> defaultIds) throws Exception {
		log.info("DeviceInventoryAction", "Customized remove.");
		AccessControl.checkUserAccess(getUserContext(),
				getSelectedL2FeatureKey(), CrudOperation.DELETE);

		int totalRmCount=0;
		int count = 0;
		int batchSize = 20;
		getSessionFiltering();
		boolean urlErrorMsg=false;

		if (isHMOnline()) {

			Set<Long> allSelectedDiids = ((DiOnlineListPageObject)this.diListPageObject).getAllDiidsWithFilter();
			Set<Long> exceptionIds = new HashSet<Long>();
			Map<String,Long> serialNumIDMap = new HashMap<String, Long>();
			
//			List<?> totalCount = QueryUtil.executeQuery("select count(id) from " +  DeviceInventory.class.getSimpleName(), null,
//					filterParams,domainId);
//			totalRmCount = Integer.parseInt(totalCount.get(0).toString());
			totalRmCount = allSelectedDiids.size();
			
			List<DeviceInventory> removeDevices = QueryUtil.executeQuery(DeviceInventory.class, null,
					new FilterParams("id", allSelectedDiids),domainId, batchSize);
			try {
				while (!removeDevices.isEmpty()) {
					List<String> serialNumbers = new ArrayList<String>();
					Collection<Long> removeIds = new ArrayList<Long>();
					
					for(DeviceInventory di: removeDevices){
						serialNumbers.add(di.getSerialNumber());
						serialNumIDMap.put(di.getSerialNumber(), di.getId());
					}
					List<HiveAp> removedAps = QueryUtil.executeQuery(HiveAp.class, null,
							new FilterParams("serialNumber", serialNumbers), getDomain().getId());
					for(HiveAp oneAp: removedAps){
						removeIds.add(oneAp.getId());
					}

					Collection<Long> successLst = BoMgmt.getMapMgmt().removeHiveApsReturnRemovedIds(removeIds, true, true, null, false);
					count= count + successLst.size();
					removeApPropagated(removedAps, successLst);
					
					DeviceUtils diu = DeviceImpUtils.getInstance();
					diu.removeSerialNumberFromHm(removedAps, successLst, false, getDomain());
					
					// remove serial numbers success
					for(HiveAp oneAp: removedAps){
						if (!successLst.contains(oneAp.getId())) {
							exceptionIds.add(serialNumIDMap.get(oneAp.getSerialNumber()));
						}
						serialNumbers.remove(oneAp.getSerialNumber());
					}
					
					if (!serialNumbers.isEmpty()) {
						RedirectorResUtils ru = ClientUtils.getRedirectorResUtils();
						SerialNumbers sns = new SerialNumbers();
						sns.setVhmid(getDomain().getVhmID()==null ? "home": getDomain().getVhmID());
						sns.setSn(serialNumbers);
						
						List<SerialNumberList> retSerialNumbers = ru.removeSerialNumbers(sns);
						List<String> successSerial = new ArrayList<String>();
						for (SerialNumberList lst : retSerialNumbers) {
							if (lst.getStatus() == ModelConstant.SN_SUCCESS) {
								if (lst.getSn() != null) {
									successSerial.addAll(lst.getSn());
								}
							}
							if (lst.getStatus() == ModelConstant.SN_NOTEXIST) {
								if (lst.getSn() != null) {
									successSerial.addAll(lst.getSn());
								}
							}
						}

					
						count= count + successSerial.size();
						if (!successSerial.isEmpty()) {
							QueryUtil.removeBos(DeviceInventory.class, new FilterParams("serialNumber", successSerial));
							for(String str: successSerial) {
								generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("geneva_08.hm.missionux.removeserial.log",str));
							}
						}
						serialNumbers.removeAll(successSerial);
						if (!serialNumbers.isEmpty()) {
							for(String sn: serialNumbers) {
								exceptionIds.add(serialNumIDMap.get(sn));
							}
						}
					}
		
					removeDevices = QueryUtil.executeQuery(DeviceInventory.class, null,
							HiveApAction.fetchNewfilterParams(new FilterParams("id", allSelectedDiids),exceptionIds), domainId,
						batchSize);
					
				}
			} catch (ClientHandlerException ex) {
				log.error(ex);
				urlErrorMsg=true;
			} catch (Exception e) {
				log.error(e);
			}
		} else {
			Set<Long> exceptionIds = new HashSet<Long>();
			List<?> totalCount = QueryUtil.executeQuery("select count(id) from " +  HiveAp.class.getSimpleName(), null,
					filterParams,domainId);
			totalRmCount = Integer.parseInt(totalCount.get(0).toString());
			
			List<Long> removeIds = (List<Long>) QueryUtil.executeQuery("select id from "
					+ HiveAp.class.getSimpleName(), null, filterParams, domainId,
					batchSize);
			
			while (!removeIds.isEmpty()) {
				List<HiveAp> removedAps = QueryUtil.executeQuery(HiveAp.class,
						null, new FilterParams("id", removeIds));
				Collection<Long> successLst =  BoMgmt.getMapMgmt().removeHiveApsReturnRemovedIds(removeIds, false, false, null, false);
				
				count= count + successLst.size();
				
				removeIds.removeAll(successLst);
				exceptionIds.addAll(removeIds);
				// propagate to other section;
				removeApPropagated(removedAps,successLst);
				
				removeIds = (List<Long>) QueryUtil.executeQuery("select id from "
						+ boClass.getSimpleName(), null, HiveApAction.fetchNewfilterParams(filterParams, exceptionIds), domainId,
						batchSize);
			}
		}

		int excepIds = totalRmCount- count;
		if (excepIds>0) {
			String msg = "";
			if (excepIds==1) {
				msg="1 item";
			} else {  
				msg=excepIds + " items";
			}
			if (urlErrorMsg) {
				addActionError(MgrUtil.getUserMessage("error.remove.exception.urlerror",msg));
			} else {
				addActionError(MgrUtil.getUserMessage("error.remove.exception",msg));
			}
			
		}
		
		return count;
	}
	
	private void removeApPropagated(List<HiveAp> removedAps, Collection<Long> successIds) {
		if (null == removedAps || removedAps.isEmpty() || successIds==null || successIds.isEmpty()) {
			return;
		}

		for (HiveAp hiveAp : removedAps) {
			if (!successIds.contains(hiveAp.getId())) {
				continue;
			}
			short managedStatus = hiveAp.getManageStatus();
			if (StringUtils.isBlank(hiveAp.getSerialNumber())) {
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, 
						MgrUtil.getUserMessage("hm.audit.log.remove.ap.from.hiveaplist"
									, new String[]{hiveAp.getHostName()
											, getHiveApListName(managedStatus)}));
			} else {
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, 
						MgrUtil.getUserMessage("hm.audit.log.remove.ap.from.hiveaplist.withserialnumber"
									, new String[]{hiveAp.getHostName()
											, hiveAp.getSerialNumber()
											, getHiveApListName(managedStatus)}));
			}
		}
	}
	
	private String getHiveApListName(short type) {
		if (HiveAp.STATUS_MANAGED == type) {
			return "Configured "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s";
		} else if (HiveAp.STATUS_NEW == type) {
			return "Unconfigured "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s";
		} else if (HiveAp.STATUS_PRECONFIG == type) {
			return "UnManaged "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s";
		} else {
			return "Unknown "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s";
		}
	}


	@Override
	protected int removeAllBos(Class<? extends HmBo> boClass,
			FilterParams filterParams, Collection<Long> defaultIds)
			throws Exception {
		return removeAllBos(boClass, defaultIds);
	}
	
	private void prepareCurrentPagingFilter() {
		this.diListPageObject.preparePagingFilterParams();
		setSessionFiltering();
	}
	
	private String prepareDeviceInventoryBoList() throws Exception{
		getSessionFiltering();
		this.diListPageObject.preparePagingFilterParams();
		String str = prepareBoList();
		return str;
	}
	
	@Override
	protected void preparePage() throws Exception {
		enableSorting();
		enablePaging();
		page = this.diListPageObject.preparePageList();
	}
	
	// fields/methods to check which menu item is clicked: start
	private static final String DI_MENU_TYPE_CONFIG = "config";
	private static final String DI_MENU_TYPE_MONITOR = "monitor";
	private String diMenuType = DI_MENU_TYPE_CONFIG;
	public String getDiMenuType() {
		return diMenuType;
	}

	public void setDiMenuType(String diMenuType) {
		this.diMenuType = diMenuType;
	}
	
	private String diMenuTypeKey = null;

	private static final String SESSION_KEY_DI_MENU_TYPE_KEY = "my-session-menukey";
	private void prepareRightL2MenuFeature() {
		if (StringUtils.isNotBlank(this.getDiMenuTypeKey())) {
			MgrUtil.setSessionAttribute(SESSION_KEY_DI_MENU_TYPE_KEY, this.getDiMenuTypeKey());
		} else {
			if (MgrUtil.getSessionAttribute(SESSION_KEY_DI_MENU_TYPE_KEY) != null) {
				this.setDiMenuTypeKey((String)MgrUtil.getSessionAttribute(SESSION_KEY_DI_MENU_TYPE_KEY));
			}
		}
		if (StringUtils.isBlank(this.getDiMenuTypeKey())) {
			this.setDiMenuTypeKey(L2_FEATURE_CONFIG_HIVE_APS);
		}
		if (diMenuTypeKey==null) {
			diMenuTypeKey = L2_FEATURE_CONFIG_HIVE_APS;
		}
		this.setSelectedL2Feature(diMenuTypeKey);

		setCurrentTableId(diMenuTypeKey);
		
//		if (DI_MENU_TYPE_MONITOR.equals(this.getDiMenuType())) {
//			this.setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
//		} else {
//			this.setSelectedL2Feature(L2_FEATURE_CONFIG_HIVE_APS);
//		}
		}
	// fields/methods to check which menu item is clicked: end
	
		
	private void setCurrentTableId(String lsKey) {
		if (L2_FEATURE_CONFIG_HIVE_APS.equals(lsKey)) {
			tableId = HmTableColumn.TABLE_DI_CONFIG_HIVE_APS;
		} else if (L2_FEATURE_CONFIG_DEVICE_HIVEAPS.equals(lsKey)) {
			tableId = HmTableColumn.TABLE_DI_CONFIG_DEVICE_HIVEAPS;
		} else if (L2_FEATURE_CONFIG_BRANCH_ROUTERS.equals(lsKey)) {
			tableId = HmTableColumn.TABLE_DI_CONFIG_BRANCH_ROUTERS;
		} else if (L2_FEATURE_CONFIG_SWITCHES.equals(lsKey)) {
			tableId = HmTableColumn.TABLE_DI_CONFIG_SWITCHES;
		} else if (L2_FEATURE_CONFIG_VPN_GATEWAYS.equals(lsKey)) {
			tableId = HmTableColumn.TABLE_DI_CONFIG_VPN_GATEWAYS;
		} else if (L2_FEATURE_MANAGED_HIVE_APS.equals(lsKey)) {
			tableId = HmTableColumn.TABLE_DI_MANAGED_HIVE_APS;
		} else if (L2_FEATURE_DEVICE_HIVEAPS.equals(lsKey)) {
			tableId = HmTableColumn.TABLE_DI_DEVICE_HIVEAPS;
		} else if (L2_FEATURE_BRANCH_ROUTERS.equals(lsKey)) {
			tableId = HmTableColumn.TABLE_DI_BRANCH_ROUTERS;
		} else if (L2_FEATURE_SWITCHES.equals(lsKey)) {
			tableId = HmTableColumn.TABLE_DI_SWITCHES;
		} else if (L2_FEATURE_VPN_GATEWAYS.equals(lsKey)) {
			tableId = HmTableColumn.TABLE_DI_VPN_GATEWAYS;
		}
	}
	public EnumItem[] getApModel() {
		return NmsUtil.filterHiveAPModel(HiveAp.HIVEAP_MODEL, this.isEasyMode());
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_SERIAL_NUMBER = 1;
	public static final int COLUMN_NODEID = 2;
	public static final int COLUMN_HOSTNAME = 3;
	public static final int COLUMN_CONNECT_STATUS = 4;
	public static final int COLUMN_NETWORK_POLICY = 5;
	public static final int COLUMN_HIVE = 6;
	public static final int COLUMN_TOPOLOGY = 7;
	public static final int COLUMN_DEVICE_MODEL = 8;
	public static final int COLUMN_DEVICE_TYPE = 9;
	
	public static final int COLUMN_IPADDRESS = 10;
	public static final int COLUMN_DHCP = 11;
	public static final int COLUMN_NETMASK = 12;
	public static final int COLUMN_GATEWAY = 13;
	public static final int COLUMN_LOCATION = 14;
	public static final int COLUMN_NATIVE_VLAN = 15;
	public static final int COLUMN_MGT0_VLAN = 16;
	public static final int COLUMN_CAPWAP_CLIENT_IP = 17;
	
	
	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return String
	 */
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
			case COLUMN_SERIAL_NUMBER:
				code = "hiveAp.serialNumber";
				break;
			case COLUMN_NODEID:
				code = "hiveAp.macaddress";
				break;
			case COLUMN_HOSTNAME:
				code = "hiveAp.hostName";
				break;
			case COLUMN_CONNECT_STATUS:
				code = "geneva_08.device.inventory.connect.status";
				break;
			case COLUMN_NETWORK_POLICY:
				code = "geneva_08.device.inventory.list.title.networkpolicy";
				break;
			case COLUMN_HIVE:
				code = "geneva_08.device.inventory.list.title.hive";
				break;
			case COLUMN_TOPOLOGY:
				code = "geneva_08.device.inventory.list.title.topology";
				break;
			case COLUMN_DEVICE_MODEL:
				code = "hiveAp.model";
				break;
			case COLUMN_DEVICE_TYPE:
				code = "hiveAp.device.type";
				break;
			case COLUMN_IPADDRESS:
				code = "hiveAp.interface.ipAddress";
				break;
			case COLUMN_DHCP:
				code = "hiveAp.head.dhcp";
				break;
			case COLUMN_NETMASK:
				code = "hiveAp.netmask";
				break;
			case COLUMN_GATEWAY:
				code = "hiveAp.gateway";
				break;
			case COLUMN_LOCATION:
				code = "hiveAp.location";
				break;
			case COLUMN_NATIVE_VLAN:
				code = "config.configTemplate.vlanNative";
				break;
			case COLUMN_MGT0_VLAN:
				code = "config.configTemplate.vlan";
				break;
			case COLUMN_CAPWAP_CLIENT_IP:
				code = "hiveAp.capwapIpAddress";
				break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	private static final byte _COLUMN_TYPE_AVAILABLE = 1;
	private static final byte _COLUMN_TYPE_SELECTED = 2;
	private List<HmTableColumn> _getNeededSelectedColums(byte columnType) {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		if (isHMOnline()) {
			columns.add(new HmTableColumn(COLUMN_SERIAL_NUMBER));
			columns.add(new HmTableColumn(COLUMN_CONNECT_STATUS));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_NODEID));
		} else {
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_NODEID));
			columns.add(new HmTableColumn(COLUMN_SERIAL_NUMBER));
		}
	
		columns.add(new HmTableColumn(COLUMN_DEVICE_MODEL));
		columns.add(new HmTableColumn(COLUMN_DEVICE_TYPE));
		if (!isEasyMode()) {
			columns.add(new HmTableColumn(COLUMN_NETWORK_POLICY));
		}
		columns.add(new HmTableColumn(COLUMN_HIVE));
		columns.add(new HmTableColumn(COLUMN_TOPOLOGY));
		
		columns.add(new HmTableColumn(COLUMN_IPADDRESS));
		columns.add(new HmTableColumn(COLUMN_DHCP));
		columns.add(new HmTableColumn(COLUMN_NETMASK));
		columns.add(new HmTableColumn(COLUMN_GATEWAY));
		//columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
		
		if (_COLUMN_TYPE_AVAILABLE == columnType) {
			columns.add(new HmTableColumn(COLUMN_LOCATION));
			columns.add(new HmTableColumn(COLUMN_NATIVE_VLAN));
			columns.add(new HmTableColumn(COLUMN_MGT0_VLAN));
		}
		
		return columns;
	}
	protected List<HmTableColumn> getDefaultSelectedColums() {
		return this._getNeededSelectedColums(_COLUMN_TYPE_AVAILABLE);
	}
	protected List<HmTableColumn> getInitSelectedColumns() {
		return this._getNeededSelectedColums(_COLUMN_TYPE_SELECTED);
	}
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		
		return null;
	}

	
	private InputStream inputStream;
	public InputStream getInputStream() throws Exception {
		return inputStream;
	}
	public String getLocalCSVFileName() {
		return "Device Inventory of " + this.getDomain().getDomainName() + ".csv";
	}
	private String exportDeviceInventories() throws Exception {
		getSessionFiltering();
		List<DeviceInventory> deviceInventories = null;
		if (this.isHMOnline()) {
			Set<Long> allSelectedDiids = null;
			if (allItemsSelected) {
				allSelectedDiids = ((DiOnlineListPageObject)this.diListPageObject).getAllDiidsWithFilter();
			} else {
				allSelectedDiids = this.getAllSelectedIds();
			}
			deviceInventories = QueryUtil
					.executeQuery(
							DeviceInventory.class, 
							new SortParams("serialNumber"), 
							FilterParamsFactory.getInstance().fieldIsIn("id", allSelectedDiids), 
							this.getDomain().getId());
		} else {
			Set<Long> allSelectedDeviceIds = null;
			if (allItemsSelected) {
				allSelectedDeviceIds = ((DiOnPremiseListPageObject)this.diListPageObject).getAllSelectedDeviceIds();
			} else {
				allSelectedDeviceIds = this.getAllSelectedIds();
			}
			List<HiveAp> devices = QueryUtil
					.executeQuery(
							HiveAp.class, 
							null, 
							FilterParamsFactory.getInstance().fieldIsIn("id", allSelectedDeviceIds), 
							this.getDomain().getId(),
							new HiveApAction());
			if (devices != null
					&& !devices.isEmpty()) {
				deviceInventories = new ArrayList<>();
				for (HiveAp device : devices) {
					DeviceInventory di = new DeviceInventory();
					di.setHiveAp(device);
					di.setOwner(device.getOwner());
					di.setSerialNumber(device.getSerialNumber());
					deviceInventories.add(di);
				}
			}
		}
		String csvString = DeviceImpUtils.getInstance()
				.getDeviceInventoryCSVString(deviceInventories, DeviceUtils.EXPORT_CSV_TYPE_CONFIGURATION, isEasyMode());
		inputStream = new ByteArrayInputStream(csvString.getBytes());
		return "export";
	}
	
	public class DeviceInventoryItem {
		private Long id;
		private String serialNumber;
		private String macAddress;
		private String hostName;
		private short connectStatus;
		private short hiveApModel;
		private short deviceType;
		private String networkPolicyName;
		private String hiveName;
		private String mapName;
		private String classificationtag1;
		private String classificationtag2;
		private String classificationtag3;
		private HmDomain owner;
		
		private Long hiveApId;
		private HiveAp hiveAp = null;
		
		public String getHiveApModelString(){
			if (hiveApModel == -1) {
				return "";
			}
			return MgrUtil.getEnumString("enum.hiveAp.model." + hiveApModel);
		}
		
		public String getDeviceTypeString(){
			if (hiveApModel == -1) {
				return "";
			}
			return MgrUtil.getEnumString("enum.hiveAp.deviceType." + deviceType);
		}
		
		public String getConnectStatusDesc() {
			return MgrUtil.getEnumString("geneva_08.enum.device.inventory.connection.status." + this.getConnectStatus());
		}
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getSerialNumber() {
			return serialNumber;
		}
		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}
		public String getMacAddress() {
			return macAddress;
		}
		public void setMacAddress(String macAddress) {
			this.macAddress = macAddress;
		}
		public String getHostName() {
			return hostName;
		}
		public void setHostName(String hostName) {
			this.hostName = hostName;
		}
		public short getConnectStatus() {
			return connectStatus;
		}
		public void setConnectStatus(short connectStatus) {
			this.connectStatus = connectStatus;
		}
		public short getHiveApModel() {
			return hiveApModel;
		}
		public void setHiveApModel(short hiveApModel) {
			this.hiveApModel = hiveApModel;
		}
		public short getDeviceType() {
			return deviceType;
		}
		public void setDeviceType(short deviceType) {
			this.deviceType = deviceType;
		}
		public String getClassificationtag1() {
			return classificationtag1;
		}
		public void setClassificationtag1(String classificationtag1) {
			this.classificationtag1 = classificationtag1;
		}
		public String getClassificationtag2() {
			return classificationtag2;
		}
		public void setClassificationtag2(String classificationtag2) {
			this.classificationtag2 = classificationtag2;
		}
		public String getClassificationtag3() {
			return classificationtag3;
		}
		public void setClassificationtag3(String classificationtag3) {
			this.classificationtag3 = classificationtag3;
		}
		public HmDomain getOwner() {
			return owner;
		}
		public void setOwner(HmDomain owner) {
			this.owner = owner;
		}
		public String getNetworkPolicyName() {
			return networkPolicyName;
		}
		public void setNetworkPolicyName(String networkPolicyName) {
			this.networkPolicyName = networkPolicyName;
		}
		public String getHiveName() {
			return hiveName;
		}
		public void setHiveName(String hiveName) {
			this.hiveName = hiveName;
		}
		public String getMapName() {
			return mapName;
		}
		public void setMapName(String mapName) {
			this.mapName = mapName;
		}
		
		public Long getHiveApId() {
			return hiveApId;
		}

		public void setHiveApId(Long hiveApId) {
			this.hiveApId = hiveApId;
		}
		
		public HiveAp getHiveAp() {
			return hiveAp;
		}

		public void setHiveAp(HiveAp hiveAp) {
			this.hiveAp = hiveAp;
		}
		
		
		public String getIpAddress() {
			if (this.getHiveApId() != null) {
				return this.getHiveAp().getIpAddress();
			}
			return "";
		}
		
		public String getCfgIpAddress() {
			if (this.getHiveApId() != null) {
				return this.getHiveAp().getCfgIpAddress();
			}
			return "";
		}
		public String getDhcpString() {
			if (this.getHiveApId() != null) {
				return this.getHiveAp().getDhcpString();
			}
			return "";
		}
		public String getNetmask() {
			if (this.getHiveApId() != null) {
				return this.getHiveAp().getNetmask();
			}
			return "";
		}
		
		public String getCfgNetmask() {
			if (this.getHiveApId() != null) {
				return this.getHiveAp().getCfgNetmask();
			}
			return "";
		}
		public String getGateway() {
			if (this.getHiveApId() != null) {
				return this.getHiveAp().getGateway();
			}
			return "";
		}
		public String getCfgGateway() {
			if (this.getHiveApId() != null) {
				return this.getHiveAp().getCfgGateway();
			}
			return "";
		}
		public String getLocation() {
			if (this.getHiveApId() != null) {
				return this.getHiveAp().getLocation();
			}
			return "";
		}
		public String getNativeVlanName() {
			if (this.getHiveApId() != null) {
				return this.getHiveAp().getNativeVlanName();
			}
			return "";
		}
		public String getVlanName() {
			if (this.getHiveApId() != null) {
				return this.getHiveAp().getVlanName();
			}
			return "";
		}
		public String getCapwapClientIp() {
			if (this.getHiveApId() != null) {
				return this.getHiveAp().getCapwapClientIp();
			}
			return "";
		}
	}
	
	private DeviceInventoryFilterForm diFilter = new DeviceInventoryFilterForm();
	private DeviceInventoryFilterHelper diFilterHelper = new DeviceInventoryFilterHelper(this.isEasyMode());
	
	public class DeviceInventoryFilterHelper {
		private boolean isEasyMode;
		
		private List<?> filterList;
		private List<CheckItem> filterTemplates;
		private List<CheckItem> filterHives;
		private List<CheckItem> filterTopologys;
		private EnumItem[] enumHiveApModel;
		private EnumItem[] enumHiveFilterDeviceType;
		private List<TextItem> classificationTag1List;
		private List<TextItem> classificationTag2List;
		private List<TextItem> classificationTag3List;
		
		public DeviceInventoryFilterHelper(boolean isEasyMode) {
			this.isEasyMode = isEasyMode;
		}
		
		public List<?> getFilterList() {
			if (this.filterList == null) {
				List<?> list = QueryUtil.executeQuery("select filterName from "
						+ HiveApFilter.class.getSimpleName(), null, new FilterParams(
						"userName=:s1 AND typeOfThisFilter=:s2", new Object[]{getUserContext().getUserName(), HiveApFilter.FILTER_TYPE_UNMANAGED_DEVICE}), domainId);
				List<String> filters = new ArrayList<String>();
				for (Object o : list) {
					filters.add((String) o);
				}
				// order by name
				Collections.sort(filters, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareToIgnoreCase(o2);
					}
				});
				this.filterList = filters;
			}
			
			return this.filterList;
		}
		
		public List<CheckItem> getFilterTemplates() {
			if (this.filterTemplates == null) {
				this.filterTemplates = getBoCheckItems("configName", ConfigTemplate.class, null, new SortParams("configName"));
			}
			return this.filterTemplates;
		}

		private List<CheckItem> prepareTopologys(boolean autoFillNone) {
			List<CheckItem> maps = getMapListView();
			List<CheckItem> topologys = new ArrayList<CheckItem>();
			if (maps.isEmpty()) {
				if (autoFillNone) {
					topologys.add(new CheckItem((long) -1, MgrUtil
							.getUserMessage("config.optionsTransfer.none")));
				}
			} else {
				topologys.add(new CheckItem((long) -1, ""));
			}
			topologys.addAll(maps);
			
			return topologys;
		}
		public List<CheckItem> getFilterTopologys() {
			if (this.filterTopologys == null) {
				this.filterTopologys = prepareTopologys(false);
			}
			return this.filterTopologys;
		}

		public List<CheckItem> getFilterHives() {
			if (this.filterHives == null) {
				List<CheckItem> hiveProfiles = getBoCheckItems("hiveName",
						HiveProfile.class, null,new SortParams("hiveName"));
				if (hiveProfiles.isEmpty()) {
					hiveProfiles.add(new CheckItem((long) -1, MgrUtil
							.getUserMessage("config.optionsTransfer.none")));
				}
				this.filterHives = hiveProfiles;
			}
			return this.filterHives;
		}
		
		public EnumItem[] getEnumHiveApModel() {
			if (this.enumHiveApModel == null) {
				this.enumHiveApModel = NmsUtil.filterHiveAPModel(HiveAp.HIVEAP_MODEL, this.isEasyMode);
			}
			return this.enumHiveApModel;
		}

		public EnumItem[] getEnumHiveFilterDeviceType() {
			if (this.enumHiveFilterDeviceType == null) {
				this.enumHiveFilterDeviceType = HiveAp.DEVICE_TYPE_USED_4_FILTER;
			}
			return this.enumHiveFilterDeviceType;
		}
		
		public List<TextItem> getClassificationTag1List(){
			if (this.classificationTag1List == null) {
				List<TextItem> retLst = new ArrayList<TextItem>();
				retLst.add(new TextItem("","All"));
				StringBuilder sql = new StringBuilder();
				sql.append("select distinct classificationTag1 from hive_ap a");
				sql.append(" where a.owner=").append(getDomain().getId());
				sql.append(" and a.classificationTag1!='' and a.classificationTag1 is not null");
				sql.append(" order by classificationTag1");
				List<?> lst = QueryUtil.executeNativeQuery(sql.toString());
				if (!lst.isEmpty()) {
					for(Object onb: lst) {
						retLst.add(new TextItem(onb.toString(),onb.toString()));
					}
				}
				this.classificationTag1List = retLst;
			}
			return this.classificationTag1List;
		}
		public List<TextItem> getClassificationTag2List(){
			if (this.classificationTag2List == null) {
				List<TextItem> retLst = new ArrayList<TextItem>();
				retLst.add(new TextItem("","All"));
				StringBuilder sql = new StringBuilder();
				sql.append("select distinct classificationTag2 from hive_ap a");
				sql.append(" where a.owner=").append(getDomain().getId());
				sql.append(" and a.classificationTag2!='' and a.classificationTag2 is not null");
				sql.append(" order by classificationTag2");
				List<?> lst = QueryUtil.executeNativeQuery(sql.toString());
				if (!lst.isEmpty()) {
					for(Object onb: lst) {
						retLst.add(new TextItem(onb.toString(),onb.toString()));
					}
				}
				this.classificationTag2List = retLst;
			}
			return this.classificationTag2List;
		}
		public List<TextItem> getClassificationTag3List(){
			if (this.classificationTag3List == null) {
				List<TextItem> retLst = new ArrayList<TextItem>();
				retLst.add(new TextItem("","All"));
				StringBuilder sql = new StringBuilder();
				sql.append("select distinct classificationTag3 from hive_ap a");
				sql.append(" where a.owner=").append(getDomain().getId());
				sql.append(" and a.classificationTag3!='' and a.classificationTag3 is not null");
				sql.append(" order by classificationTag3");
				List<?> lst = QueryUtil.executeNativeQuery(sql.toString());
				if (!lst.isEmpty()) {
					for(Object onb: lst) {
						retLst.add(new TextItem(onb.toString(),onb.toString()));
					}
				}
				this.classificationTag3List = retLst;
			}
			return this.classificationTag3List;
		}
		
		
		public JSONObject prepareFilterValues(String filterName) throws JSONException {
			JSONObject jsonObject = new JSONObject();
			if (null == filterName) {
				return jsonObject;
			}
			jsonObject.put("fname", filterName);

			List<HiveApFilter> filterMap = QueryUtil.executeQuery(
					HiveApFilter.class, null, new FilterParams(
							"filterName=:s1 and userName=:s2 and typeOfThisFilter=:s3", new Object[] {
									filterName, getUserContext().getUserName(), HiveApFilter.FILTER_TYPE_UNMANAGED_DEVICE }),
					domainId);

			if (filterMap.isEmpty()) {
				return jsonObject;
			}
			HiveApFilter flt = filterMap.get(0);
			if (null != flt.getFilterTemplate()) {
				jsonObject.put("ftemp", flt.getFilterTemplate());
			}
			if (null != flt.getFilterHive()) {
				jsonObject.put("fhive", flt.getFilterHive());
			}
			if (null != flt.getFilterTopology()) {
				jsonObject.put("ftopo", flt.getFilterTopology());
			}
			if (null != flt.getFilterIp()) {
				jsonObject.put("fip", flt.getFilterIp());
			}
			if (null != flt.getHostname()) {
				jsonObject.put("fHostname", flt.getHostname());
			}
			if (null != flt.getClassificationTag1()) {
				jsonObject.put("fTag1", flt.getClassificationTag1());
			}
			if (null != flt.getClassificationTag2()) {
				jsonObject.put("fTag2", flt.getClassificationTag2());
			}
			if (null != flt.getClassificationTag3()) {
				jsonObject.put("fTag3", flt.getClassificationTag3());
			}

			jsonObject.put("fvpn", flt.isFilterVpnServer());
			jsonObject.put("fvpnClient", flt.isFilterVpnClient());
			jsonObject.put("fradius", flt.isFilterRadiusServer());
			jsonObject.put("fradiusProxy", flt.isFilterRadiusProxy());
			jsonObject.put("fdhcp", flt.isFilterDhcpServer());
			jsonObject.put("fApModel", flt.getHiveApModel());
			jsonObject.put("fDeType", flt.getFilterDeviceType());
			jsonObject.put("fBEth0", flt.isEth0Bridge());
			jsonObject.put("fBEth1", flt.isEth1Bridge());
			jsonObject.put("fBRed0", flt.isRed0Bridge());
			jsonObject.put("fBAgg0", flt.isAgg0Bridge());
			jsonObject.put("fSerialNumber", flt.getSerialNumber());

			return jsonObject;
		}
		
		public void removeHiveApFilter(String filterName) {
			if (StringUtils.isBlank(filterName)) {
				return;
			}
			try {
				List<HiveApFilter> hiveApFilterList = QueryUtil
						.executeQuery(HiveApFilter.class, null, new FilterParams(
								"filterName=:s1 AND userName=:s2 and typeOfThisFilter=:s3",
								new Object[] { filterName,
										getUserContext().getUserName(), HiveApFilter.FILTER_TYPE_UNMANAGED_DEVICE }), domainId);
				if (hiveApFilterList != null
						&& !hiveApFilterList.isEmpty()) {
					HiveApFilter hiveApFilter = hiveApFilterList.get(0);
					HiveApFilter rmbos = findBoById(HiveApFilter.class, hiveApFilter
							.getId());
					QueryUtil.removeBoBase(rmbos);
				}
			} catch (Exception e) {
				addActionError(MgrUtil.getUserMessage("action.error.remove.filter.fail",NmsUtil.getOEMCustomer().getAccessPonitName()));
			}
		}
		
		public HiveApFilter saveToSessionFilterList(DeviceInventoryFilterForm filterForm) {
			if (filterForm == null
					|| StringUtils.isBlank(filterForm.getFilterName())) {
				return null;
			}

			HiveApFilter result = null;
			try {
				List<HiveApFilter> hiveApFilterList = QueryUtil
						.executeQuery(HiveApFilter.class, null, new FilterParams(
								"filterName=:s1 AND userName=:s2 AND typeOfThisFilter=:s3",
								new Object[] { filterForm.getFilterName(),
										getUserContext().getUserName(), HiveApFilter.FILTER_TYPE_UNMANAGED_DEVICE }), domainId);
				if (!hiveApFilterList.isEmpty()) {
					HiveApFilter hiveApFilter = hiveApFilterList.get(0);
					hiveApFilter.setFilterTemplate(filterForm.getFilterTemplate());
					hiveApFilter.setFilterHive(filterForm.getFilterHive());
					hiveApFilter.setFilterTopology(filterForm.getFilterTopology());
					hiveApFilter.setFilterIp(filterForm.getFilterIp());
					hiveApFilter.setFilterDhcpServer(filterForm.isFilterDhcpServer());
					hiveApFilter.setFilterVpnServer(filterForm.isFilterVpnServer());
					hiveApFilter.setFilterRadiusServer(filterForm.isFilterRadiusServer());
					hiveApFilter.setFilterRadiusProxy(filterForm.isFilterRadiusProxy());
					hiveApFilter.setFilterVpnClient(filterForm.isFilterVpnClient());
					hiveApFilter.setHiveApModel(filterForm.getFilterApModel());
					hiveApFilter.setFilterDeviceType(filterForm.getFilterDeviceType());
					hiveApFilter.setHostname(filterForm.getFilterHostname());
					hiveApFilter.setClassificationTag1(filterForm.getClassificationTag1());
					hiveApFilter.setClassificationTag2(filterForm.getClassificationTag2());
					hiveApFilter.setClassificationTag3(filterForm.getClassificationTag3());
					hiveApFilter.setEth0Bridge(filterForm.isFilterEth0Bridge());
					hiveApFilter.setEth1Bridge(filterForm.isFilterEth1Bridge());
					hiveApFilter.setRed0Bridge(filterForm.isFilterRed0Bridge());
					hiveApFilter.setAgg0Bridge(filterForm.isFilterAgg0Bridge());
					hiveApFilter.setSerialNumber(filterForm.getFilterSerialNumber());
					QueryUtil.updateBo(hiveApFilter);
					result = hiveApFilter;
				} else {
					HiveApFilter hiveApFilter = new HiveApFilter();
					hiveApFilter.setFilterName(filterForm.getFilterName());
					hiveApFilter.setTypeOfThisFilter(HiveApFilter.FILTER_TYPE_UNMANAGED_DEVICE);
					hiveApFilter.setUserName(getUserContext().getUserName());
					
					hiveApFilter.setFilterTemplate(filterForm.getFilterTemplate());
					hiveApFilter.setFilterHive(filterForm.getFilterHive());
					hiveApFilter.setFilterTopology(filterForm.getFilterTopology());
					hiveApFilter.setFilterIp(filterForm.getFilterIp());
					hiveApFilter.setFilterDhcpServer(filterForm.isFilterDhcpServer());
					hiveApFilter.setFilterVpnServer(filterForm.isFilterVpnServer());
					hiveApFilter.setFilterRadiusServer(filterForm.isFilterRadiusServer());
					hiveApFilter.setFilterRadiusProxy(filterForm.isFilterRadiusProxy());
					hiveApFilter.setFilterVpnClient(filterForm.isFilterVpnClient());
					hiveApFilter.setHiveApModel(filterForm.getFilterApModel());
					hiveApFilter.setFilterDeviceType(filterForm.getFilterDeviceType());
					hiveApFilter.setHostname(filterForm.getFilterHostname());
					hiveApFilter.setClassificationTag1(filterForm.getClassificationTag1());
					hiveApFilter.setClassificationTag2(filterForm.getClassificationTag2());
					hiveApFilter.setClassificationTag3(filterForm.getClassificationTag3());
					hiveApFilter.setEth0Bridge(filterForm.isFilterEth0Bridge());
					hiveApFilter.setEth1Bridge(filterForm.isFilterEth1Bridge());
					hiveApFilter.setRed0Bridge(filterForm.isFilterRed0Bridge());
					hiveApFilter.setAgg0Bridge(filterForm.isFilterAgg0Bridge());
					hiveApFilter.setSerialNumber(filterForm.getFilterSerialNumber());
					hiveApFilter.setOwner(getDomain());
					QueryUtil.createBo(hiveApFilter);
					result = hiveApFilter;
				}

				MgrUtil.setSessionAttribute(UNMANAGED_HIVEAP_CURRENT_FILTER,
						filterForm.getFilterName());

				return result;
			} catch (Exception e) {
				addActionError(MgrUtil.getUserMessage("action.error.add.filter.fail",NmsUtil.getOEMCustomer().getAccessPonitName()));
				return null;
			}
		}
	}
	public static final String UNMANAGED_HIVEAP_CURRENT_FILTER = "unmanaged_hiveAp_current_filter";
	
	public class DeviceInventoryFilterForm {
		private String filterName;
		private Long filterTemplate;
		private Long filterTopology;
		private Long filterHive;
		private String filterIp;
		private boolean filterVpnServer;
		private boolean filterRadiusServer;
		private boolean filterDhcpServer;
		private boolean filterRadiusProxy;
		private boolean filterVpnClient;
		private short filterApModel;
		private short filterDeviceType=-2;
		private String filterHostname;
		private boolean filterEth0Bridge;
		private boolean filterEth1Bridge;
		private boolean filterRed0Bridge;
		private boolean filterAgg0Bridge;
		private String classificationTag1;
		private String classificationTag2;
		private String classificationTag3;
		private String filterSerialNumber;
		
		public String getFilterName() {
			return filterName;
		}
		public void setFilterName(String filterName) {
			this.filterName = filterName;
		}
		public Long getFilterTemplate() {
			return filterTemplate;
		}
		public void setFilterTemplate(Long filterTemplate) {
			this.filterTemplate = filterTemplate;
		}
		public Long getFilterTopology() {
			return filterTopology;
		}
		public void setFilterTopology(Long filterTopology) {
			this.filterTopology = filterTopology;
		}
		public Long getFilterHive() {
			return filterHive;
		}
		public void setFilterHive(Long filterHive) {
			this.filterHive = filterHive;
		}
		public String getFilterIp() {
			return filterIp;
		}
		public void setFilterIp(String filterIp) {
			this.filterIp = filterIp;
		}
		public boolean isFilterVpnServer() {
			return filterVpnServer;
		}
		public void setFilterVpnServer(boolean filterVpnServer) {
			this.filterVpnServer = filterVpnServer;
		}
		public boolean isFilterRadiusServer() {
			return filterRadiusServer;
		}
		public void setFilterRadiusServer(boolean filterRadiusServer) {
			this.filterRadiusServer = filterRadiusServer;
		}
		public boolean isFilterDhcpServer() {
			return filterDhcpServer;
		}
		public void setFilterDhcpServer(boolean filterDhcpServer) {
			this.filterDhcpServer = filterDhcpServer;
		}
		public boolean isFilterRadiusProxy() {
			return filterRadiusProxy;
		}
		public void setFilterRadiusProxy(boolean filterRadiusProxy) {
			this.filterRadiusProxy = filterRadiusProxy;
		}
		public boolean isFilterVpnClient() {
			return filterVpnClient;
		}
		public void setFilterVpnClient(boolean filterVpnClient) {
			this.filterVpnClient = filterVpnClient;
		}
		public short getFilterApModel() {
			return filterApModel;
		}
		public void setFilterApModel(short filterApModel) {
			this.filterApModel = filterApModel;
		}
		public short getFilterDeviceType() {
			return filterDeviceType;
		}
		public void setFilterDeviceType(short filterDeviceType) {
			this.filterDeviceType = filterDeviceType;
		}
		public String getFilterHostname() {
			return filterHostname;
		}
		public void setFilterHostname(String filterHostname) {
			this.filterHostname = filterHostname;
		}
		public boolean isFilterEth0Bridge() {
			return filterEth0Bridge;
		}
		public void setFilterEth0Bridge(boolean filterEth0Bridge) {
			this.filterEth0Bridge = filterEth0Bridge;
		}
		public boolean isFilterEth1Bridge() {
			return filterEth1Bridge;
		}
		public void setFilterEth1Bridge(boolean filterEth1Bridge) {
			this.filterEth1Bridge = filterEth1Bridge;
		}
		public boolean isFilterRed0Bridge() {
			return filterRed0Bridge;
		}
		public void setFilterRed0Bridge(boolean filterRed0Bridge) {
			this.filterRed0Bridge = filterRed0Bridge;
		}
		public boolean isFilterAgg0Bridge() {
			return filterAgg0Bridge;
		}
		public void setFilterAgg0Bridge(boolean filterAgg0Bridge) {
			this.filterAgg0Bridge = filterAgg0Bridge;
		}
		public String getClassificationTag1() {
			return classificationTag1;
		}
		public void setClassificationTag1(String classificationTag1) {
			this.classificationTag1 = classificationTag1;
		}
		public String getClassificationTag2() {
			return classificationTag2;
		}
		public void setClassificationTag2(String classificationTag2) {
			this.classificationTag2 = classificationTag2;
		}
		public String getClassificationTag3() {
			return classificationTag3;
		}
		public void setClassificationTag3(String classificationTag3) {
			this.classificationTag3 = classificationTag3;
		}
		public String getFilterSerialNumber() {
			return filterSerialNumber;
		}
		public void setFilterSerialNumber(String filterSerialNumber) {
			this.filterSerialNumber = filterSerialNumber;
		}
	}
	
	private DiListPageObject diListPageObject;
	
	public abstract class DiListPageObject {
		public abstract void preparePagingFilterParams();
		public abstract List<?> preparePageList() throws Exception;
		public abstract FilterParams prepareSearchOperation(DeviceInventoryFilterForm filterForm);
		public abstract void prepareDataSource();
		
		protected String getSqlFieldWithPrefix(String field, String prefix) {
			if (StringUtils.isNotBlank(prefix)) {
				return prefix + "." + field;
			}
			return field;
		}
		public abstract String prepareDeviceTypeFilter(String where, List<Object> values, String fieldPrefix, List<Short> deviceTypes);
		public abstract JSONObject getListObjectConfig();
	}
	
	public class DiOnlineListPageObject extends DiListPageObject {
		
		@Override
		public String prepareDeviceTypeFilter(String where, List<Object> values, String fieldPrefix, List<Short> deviceTypes) {
			
			if (StringUtils.isNotBlank(where)) {
				where = " AND ";
			} else {
				where = "";
			}
			deviceTypes = new ArrayList<Short>();
			
			String curMenu = getDiMenuTypeKey();
			if("managedHiveAps".equals(curMenu)
					|| "configHiveAps".equals(curMenu)){
				List<Short> status = new ArrayList<Short>();
				status.add(HiveAp.STATUS_PRECONFIG);
	
				where += "(" + this.getSqlFieldWithPrefix("manageStatus", fieldPrefix) + " is null or " 
						+ this.getSqlFieldWithPrefix("manageStatus", fieldPrefix) + " in (:s" + (values.size() + 1) + "))";
				values.add(status);
			} else {
				List<Short> status = new ArrayList<Short>();
				status.add(HiveAp.STATUS_PRECONFIG);
				
				where += this.getSqlFieldWithPrefix("manageStatus", fieldPrefix) + " in (:s" + (values.size() + 1) + ")";
				values.add(status);
			}
			
			if("vpnGateways".equals(curMenu)
					|| "configVpnGateways".equals(curMenu)){				
				where = where + " AND ( " + this.getSqlFieldWithPrefix("deviceType", fieldPrefix) + " in (:s" + (values.size() + 1) + ")";
				deviceTypes.add(HiveAp.Device_TYPE_VPN_GATEWAY);
				deviceTypes.add(HiveAp.Device_TYPE_VPN_BR);
				values.add(deviceTypes);

				where = where + " OR " + this.getSqlFieldWithPrefix("hiveApModel", fieldPrefix) + " in (:s" + (values.size() + 1) + ")" + ")";
				values.add(getCVGList());
			}else if("branchRouters".equals(curMenu)
					|| "configBranchRouters".equals(curMenu)){
				where = where + " AND  " + this.getSqlFieldWithPrefix("deviceType", fieldPrefix) + " = :s" + (values.size() + 1);
				values.add(HiveAp.Device_TYPE_BRANCH_ROUTER);
			}else if("switches".equals(curMenu)
					|| "configSwitches".equals(curMenu)){
				where = where + " AND " + this.getSqlFieldWithPrefix("deviceType", fieldPrefix) + " = :s" + (values.size() + 1);
				values.add(HiveAp.Device_TYPE_SWITCH);
			}else if("deviceHiveAps".equals(curMenu)
					|| "configDeviceHiveAps".equals(curMenu)){
				where = where + " AND " + this.getSqlFieldWithPrefix("deviceType", fieldPrefix) + " = :s" + (values.size() + 1);
				values.add(HiveAp.Device_TYPE_HIVEAP);

				List<Short> modelList = new ArrayList<Short>();
				if(isEasyMode()){
					modelList.addAll(getCVGList());
					modelList.addAll(getBRList());
				}else{
					modelList.addAll(getCVGList());
				}
				where = where + " AND " + this.getSqlFieldWithPrefix("hiveApModel", fieldPrefix) + " not in (:s" + (values.size() + 1) + ")";
				values.add(modelList);
			}
			
			return where;
		}
		
		@Override
		public void preparePagingFilterParams() {
			if (filterParams == null) {
				List<Object> values = new ArrayList<Object>();
				String where = this.getDomainFilterString(1);
				values.add(getDomain().getId());
				where += this.prepareDeviceTypeFilter(where, values, "hiveap", null);
				filterParams = FilterParamsFactory.getInstance().customizedFilter(where, values.toArray());
				setSessionFiltering();
			}
		}

		@Override
		public List<?> preparePageList() throws Exception {
			List<DeviceInventoryItem> items = this.getAdditionalHiveApInfo(
					this.convertRawColumnsToPage(
						findBos(this.getQueryString())
					)
				);
			this.queryListObjectConfig();
			return items;
		}

		@Override
		public FilterParams prepareSearchOperation(
				DeviceInventoryFilterForm filterForm) {
			List<Object> values = new ArrayList<Object>();
			List<Short> deviceTypes = new ArrayList<Short>();
			String where = this.prepareDeviceTypeFilter(null, values, "hiveap", deviceTypes);
			
			if (null != filterForm.getFilterTemplate() 
					&& filterForm.getFilterTemplate() > 0) {
				where = where + " AND np.id = :s" + (values.size() + 1);
				values.add(filterForm.getFilterTemplate());
				//fix bug 18894
				if(!deviceTypes.isEmpty() && deviceTypes.
						contains(HiveAp.Device_TYPE_VPN_GATEWAY)){
					deviceTypes.remove((Object)HiveAp.Device_TYPE_VPN_GATEWAY);
				}
			}
			if (null != filterForm.getFilterHive() 
					&& filterForm.getFilterHive() > 0) {
				where = where + " AND hive.id = :s"
						+ (values.size() + 1);
				values.add(filterForm.getFilterHive());
				//fix bug 18894
				if(!deviceTypes.isEmpty() && deviceTypes.
						contains(HiveAp.Device_TYPE_VPN_GATEWAY)){
					deviceTypes.remove((Object)HiveAp.Device_TYPE_VPN_GATEWAY);
				}
			}
			if (null != filterForm.getFilterTopology() 
					&& filterForm.getFilterTopology() > 0) {
				where = where + " AND map.id = :s" + (values.size() + 1);
				values.add(filterForm.getFilterTopology());
			} else if (null != filterForm.getFilterTopology() 
					&& filterForm.getFilterTopology() == -1) {
				where = where + " AND map.id is null";
			}
			if (StringUtils.isNotBlank(filterForm.getFilterIp())) {
				where = where + " AND hiveap.cfgIpAddress like :s" + (values.size() + 1);
				values.add(filterForm.getFilterIp().trim() + '%');
			}
			if (StringUtils.isNotBlank(filterForm.getFilterHostname())) {
				where = where + " AND lower(hiveap.hostName) like :s"
						+ (values.size() + 1);
				values.add('%' + filterForm.getFilterHostname().trim().toLowerCase() + '%');
			}
			if (filterForm.isFilterRadiusServer() 
					|| filterForm.isFilterVpnServer() 
					|| filterForm.isFilterVpnClient()
					|| filterForm.isFilterDhcpServer()
					|| filterForm.isFilterRadiusProxy()) {
				String filter = "";
				if (filterForm.isFilterRadiusServer()) {
					filter += "".equals(filter) ? "hiveap.RADIUS_SERVER_ID is not null"
							: " or hiveap.RADIUS_SERVER_ID is not null";
				}
				if (filterForm.isFilterRadiusProxy()) {
					filter += "".equals(filter) ? "hiveap.RADIUS_PROXY_ID is not null"
							: " or hiveap.RADIUS_PROXY_ID is not null";
				}
				if (filterForm.isFilterVpnServer()) {
					filter += "".equals(filter) ? "(np.VPN_SERVICE_ID is not null AND hiveap.vpnMark = "
							+ HiveAp.VPN_MARK_SERVER + ")"
							: " or (np.VPN_SERVICE_ID is not null AND hiveap.vpnMark = "
									+ HiveAp.VPN_MARK_SERVER + ")";
				}
				if (filterForm.isFilterVpnClient()) {
					filter += "".equals(filter) ? "(np.VPN_SERVICE_ID is not null AND hiveap.vpnMark = "
							+ HiveAp.VPN_MARK_CLIENT + ")"
							: " or (np.VPN_SERVICE_ID is not null AND hiveap.vpnMark = "
									+ HiveAp.VPN_MARK_CLIENT + ")";
				}
				if (filterForm.isFilterDhcpServer()) {
					filter += "".equals(filter) ? "hiveap.dhcpServerCount > 0"
							: " or hiveap.dhcpServerCount > 0";
				}
				where = where + " AND (" + filter + ")";
			}

			if (filterForm.getFilterApModel() >= 0) {
				where = where + " AND hiveap.hiveApModel = :s" + (values.size() + 1);
				values.add(filterForm.getFilterApModel());
			}
			if (filterForm.getFilterDeviceType() >=0) {
				if (filterForm.getFilterDeviceType() == 2){
					where = where + " AND (hiveap.deviceType = :s" + (values.size() + 1);
					values.add(HiveAp.Device_TYPE_VPN_GATEWAY);
					where = where + " OR hiveap.deviceType = :s" + (values.size() + 1);
					values.add(HiveAp.Device_TYPE_VPN_BR);
					where = where + " OR hiveap.hiveApModel in (:s" + (values.size() + 1) + "))";
					values.add(getCVGList());
				} else {
					if (filterForm.getFilterDeviceType() == 0) {
						where = where + " AND hiveap.hiveApModel not in (:s" + (values.size() + 1) + ")";
						values.add(getCVGList());
					}
					where = where + " AND hiveap.deviceType = :s" + (values.size() + 1);
					values.add(filterForm.getFilterDeviceType());
				}
			}
			if (filterForm.isFilterEth0Bridge() 
					|| filterForm.isFilterEth1Bridge() 
					|| filterForm.isFilterRed0Bridge()
					|| filterForm.isFilterAgg0Bridge()) {
				String filter = "";
				if (filterForm.isFilterEth0Bridge()) {
					String query = "(hiveap.eth0_operation_mode = "
							+ AhInterface.OPERATION_MODE_ACCESS
							+ " OR hiveap.eth0_operation_mode = "
							+ AhInterface.OPERATION_MODE_BRIDGE + ")";

					filter += "".equals(filter) ? query : " or " + query;
				}

				if (filterForm.isFilterEth1Bridge()) {
					String query = "(hiveap.eth1_operation_mode = "
							+ AhInterface.OPERATION_MODE_ACCESS
							+ " OR hiveap.eth1_operation_mode = "
							+ AhInterface.OPERATION_MODE_BRIDGE + ")";
					filter += "".equals(filter) ? query : " or " + query;
				}

				if (filterForm.isFilterRed0Bridge()) {
					String query = "(hiveap.red0_operation_mode = "
							+ AhInterface.OPERATION_MODE_ACCESS
							+ " OR hiveap.red0_operation_mode = "
							+ AhInterface.OPERATION_MODE_BRIDGE + ")";
					filter += "".equals(filter) ? query : " or " + query;
				}

				if (filterForm.isFilterAgg0Bridge()) {
					String query = "(hiveap.agg0_operation_mode = "
							+ AhInterface.OPERATION_MODE_ACCESS
							+ " OR hiveap.agg0_operation_mode = "
							+ AhInterface.OPERATION_MODE_BRIDGE + ")";
					filter += "".equals(filter) ? query : " or " + query;
				}
				where = where + " AND (" + filter + ")";
			}
			
			if (StringUtils.isNotBlank(filterForm.getClassificationTag1())) {
				where = where + " AND hiveap.classificationTag1 = :s" + (values.size() + 1);
				values.add(filterForm.getClassificationTag1());
			}
			if (StringUtils.isNotBlank(filterForm.getClassificationTag2())) {
				where = where + " AND hiveap.classificationTag2 = :s" + (values.size() + 1);
				values.add(filterForm.getClassificationTag2());
			}
			if (StringUtils.isNotBlank(filterForm.getClassificationTag3())) {
				where = where + " AND hiveap.classificationTag3 = :s" + (values.size() + 1);
				values.add(filterForm.getClassificationTag3());
			}
			
			if (StringUtils.isNotBlank(filterForm.getFilterSerialNumber())) {
				if (filterForm.getFilterSerialNumber().length() == 14) {
					where = where + " AND di.serialNumber = :s" + (values.size() + 1);
					values.add(filterForm.getFilterSerialNumber());
				} else {
					where = where + " AND di.serialNumber like :s" + (values.size() + 1);
					values.add("%" + filterForm.getFilterSerialNumber() + "%");
				}
			}
			
			where = where + " AND " + getDomainFilterString(values.size() + 1);
			values.add(getDomain().getId());
			
			if (!values.isEmpty()) {
				return FilterParamsFactory.getInstance().customizedFilter(where, values.toArray());
			}
			
			return null;
		}
		
		private String getDomainFilterString(int paramOrder) {
			StringBuffer buf = new StringBuffer();
			buf.append("(");
			String gdc = null;
			if (BoMgmt.getDomainMgmt().getGlobalDomain() != null) {
				gdc = " di.owner = " + BoMgmt.getDomainMgmt().getGlobalDomain().getId();
			}
			if (gdc == null) {
				buf.append(" di.owner = :s" + paramOrder + " )");
			} else {
				buf.append(gdc);
				buf.append(" OR di.owner = :s" + paramOrder + " )");
			}
			
			return buf.toString();
		}
		
		private String getQueryString() {
			String result = "select di.id, di.serialnumber, di.owner, di.connectstatus, hiveap.id as hiveapid, hiveap.hostname, hiveap.macaddress, "
					+ " hiveap.hiveapmodel, hiveap.devicetype, np.configname as networkpolicyname, "
					+ " hive.hivename, map.mapname, "
					+ " hiveap.classificationtag1, hiveap.classificationtag2, hiveap.classificationtag3 "
					+ " from device_inventory di "
					+ " left join hive_ap hiveap "
					+ " on di.serialnumber = hiveap.serialnumber "
					+ " and di.owner = hiveap.owner "
					+ " left join config_template np "
					+ " on hiveap.template_id = np.id "
					+ " left join hive_profile hive "
					+ " on np.hive_profile_id = hive.id "
					+ " left join map_node map "
					+ " on hiveap.map_container_id = map.id ";
			return result;
		}
		
		private List<DeviceInventoryItem> convertRawColumnsToPage(List<?> rows) {
			if (rows == null
					|| rows.isEmpty()) {
				return null;
			}
			List<DeviceInventoryItem> diList = new ArrayList<>(rows.size());
			
			//column order is:
			// id, serialnumber, owner, connectstatus, hostname, macaddress, 
			// hiveapmodel, devicetype, networkpolicyname, hivename, mapname,
			// classificationtag1, classificationtag2, classificationtag3
			for (Object row : rows) {
				DeviceInventoryItem item = new DeviceInventoryItem();
				Object[] columns = (Object[])row;
				int curIdx = 0;
				
				item.setId(((BigInteger)columns[curIdx]).longValue());
				curIdx++;item.setSerialNumber(columns[curIdx]==null?"":columns[curIdx].toString());
				curIdx++;item.setOwner(columns[curIdx]==null?null:CacheMgmt.getInstance().getCacheDomainById(((BigInteger)columns[curIdx]).longValue()));
				curIdx++;item.setConnectStatus(columns[curIdx]==null?DeviceInventory.STATUS_DISCONNECT_REDIRECTOR:(Short)columns[curIdx]);
				curIdx++;item.setHiveApId(columns[curIdx]==null?null:((BigInteger)columns[curIdx]).longValue());
				curIdx++;item.setHostName(columns[curIdx]==null?"":columns[curIdx].toString());
				curIdx++;item.setMacAddress(columns[curIdx]==null?"":columns[curIdx].toString());
				curIdx++;item.setHiveApModel(columns[curIdx]==null?-1:(Short)columns[curIdx]);
				curIdx++;item.setDeviceType(columns[curIdx]==null?-1:(Short)columns[curIdx]);
				curIdx++;item.setNetworkPolicyName(columns[curIdx]==null?"":columns[curIdx].toString());
				curIdx++;item.setHiveName(columns[curIdx]==null?"":columns[curIdx].toString());
				curIdx++;item.setMapName(columns[curIdx]==null?"-":columns[curIdx].toString());
				curIdx++;item.setClassificationtag1(columns[curIdx]==null?"":columns[curIdx].toString());
				curIdx++;item.setClassificationtag2(columns[curIdx]==null?"":columns[curIdx].toString());
				curIdx++;item.setClassificationtag3(columns[curIdx]==null?"":columns[curIdx].toString());
				
				diList.add(item);
			}
			
			return diList;
		}
		
		private List<DeviceInventoryItem> getAdditionalHiveApInfo(List<DeviceInventoryItem> rows) {
			if (rows == null
					|| rows.isEmpty()) {
				return new ArrayList<DeviceInventoryItem>();
			}
			
			List<Long> hiveApIds = new ArrayList<>();
			for (DeviceInventoryItem item : rows) {
				if (item.getHiveApId() != null) {
					hiveApIds.add(item.getHiveApId());
				}
			}
			
			if (!hiveApIds.isEmpty()) {
				Map<Long, HiveAp> hiveAps = BoAssistant.getIdObjectMap(HiveAp.class, hiveApIds, getDomain(), new HiveApAction());
				if (hiveAps != null) {
					for (DeviceInventoryItem item : rows) {
						Long idTmp = item.getHiveApId();
						if (idTmp != null
								&& hiveAps.containsKey(idTmp)) {
							item.setHiveAp(hiveAps.get(idTmp));
						} else {
							item.setHiveAp(new HiveAp());
						}
					}
				}
			}
			
			return rows;
		}

		@Override
		public void prepareDataSource() {
			setDataSource(DeviceInventory.class);
		}

		public Set<Long> getAllDiidsWithFilter() throws Exception {
			AccessControl.checkUserAccess(getUserContext(), getSelectedL2FeatureKey(), CrudOperation.READ);
			Set<Long> result = new HashSet<>();
			List<?> diItems = QueryUtil.executeNativeQuery(this.getQueryPreConfigString(), sortParams, filterParams);
			if (diItems != null
					&& !diItems.isEmpty()) {
				for (Object obj : diItems) {
					Object[] columns = (Object[])obj;
					result.add(((BigInteger)columns[0]).longValue());
				}
			}
			return result;
		}
		private String getQueryPreConfigString() {
			String result = "select di.id, di.serialnumber, hiveap.id as hiveapid "
					+ " from device_inventory di "
					+ " left join hive_ap hiveap "
					+ " on di.serialnumber = hiveap.serialnumber "
					+ " and di.owner = hiveap.owner "
					+ " left join config_template np "
					+ " on hiveap.template_id = np.id "
					+ " left join hive_profile hive "
					+ " on np.hive_profile_id = hive.id "
					+ " left join map_node map "
					+ " on hiveap.map_container_id = map.id ";
			return result;
		}
		private void queryListObjectConfig() throws Exception {
			AccessControl.checkUserAccess(getUserContext(), getSelectedL2FeatureKey(), CrudOperation.READ);
			List<?> diItems = QueryUtil.executeNativeQuery(this.getQueryPreConfigString(), sortParams, filterParams);
			if (diItems != null
					&& !diItems.isEmpty()) {
				List<DeviceInventoryItem> items = new ArrayList<>();
				for (Object obj : diItems) {
					DeviceInventoryItem item = new DeviceInventoryItem();
					Object[] columns = (Object[])obj;
					int curIdx = 0;
					item.setId(((BigInteger)columns[curIdx]).longValue());
					curIdx++;item.setSerialNumber(columns[curIdx]==null?"":columns[curIdx].toString());
					curIdx++;item.setHiveApId(columns[curIdx]==null?null:((BigInteger)columns[curIdx]).longValue());
					
					items.add(item);
				}
				
				this.prepareListObjectConfig(items);
			}
		}
		private JSONObject configObj = null;
		private void prepareListObjectConfig(List<DeviceInventoryItem> items) throws Exception {
			if (items != null
					&& !items.isEmpty()) {
				this.configObj = new JSONObject();
				for (DeviceInventoryItem item : items) {
					JSONObject singleObj = new JSONObject();
					this.configObj.put(item.getId().toString(), singleObj);
					if (item.getHiveApId() != null) {
						singleObj.put("id", item.getHiveApId());
						singleObj.put("precfg", true);
						singleObj.put("serialNum", item.getSerialNumber());
					} else {
						singleObj.put("id", "-1");
						singleObj.put("precfg", false);
						singleObj.put("serialNum", item.getSerialNumber());
					}
				}
			}
		}
		@Override
		public JSONObject getListObjectConfig() {
			return this.configObj;
		}
		
	}
	
	public class DiOnPremiseListPageObject extends DiListPageObject {

		@Override
		public String prepareDeviceTypeFilter(String where, List<Object> values, String fieldPrefix, List<Short> deviceTypes) {
			List<Short> status = new ArrayList<Short>();
			status.add(HiveAp.STATUS_PRECONFIG);

			deviceTypes = new ArrayList<Short>();
			if (StringUtils.isNotBlank(where)) {
				where = " AND ";
			} else {
				where = "";
			}
			where += this.getSqlFieldWithPrefix("manageStatus", fieldPrefix) + " in (:s" + (values.size() + 1) + ")";
			values.add(status);
			
			String curMenu = getDiMenuTypeKey();
			if("managedHiveAps".equals(curMenu)
					|| "configHiveAps".equals(curMenu)){
				where = where + " AND " + this.getSqlFieldWithPrefix("deviceType", fieldPrefix) + " in (:s" + (values.size() + 1) + ")";
				deviceTypes.add(HiveAp.Device_TYPE_HIVEAP);
				deviceTypes.add(HiveAp.Device_TYPE_BRANCH_ROUTER);
				deviceTypes.add(HiveAp.Device_TYPE_SWITCH);
				deviceTypes.add(HiveAp.Device_TYPE_VPN_GATEWAY);
				deviceTypes.add(HiveAp.Device_TYPE_VPN_BR);
				values.add(deviceTypes);
			}else if("vpnGateways".equals(curMenu)
					|| "configVpnGateways".equals(curMenu)){
				where = where + " AND ( " + this.getSqlFieldWithPrefix("deviceType", fieldPrefix) + " in (:s" + (values.size() + 1)+")";
				deviceTypes.add(HiveAp.Device_TYPE_VPN_GATEWAY);
				deviceTypes.add(HiveAp.Device_TYPE_VPN_BR);
				values.add(deviceTypes);

				where = where + " OR " + this.getSqlFieldWithPrefix("hiveApModel", fieldPrefix) + " in (:s" + (values.size() + 1) + "))";
				values.add(getCVGList());
			}else if("branchRouters".equals(curMenu)
					|| "configBranchRouters".equals(curMenu)){
				where = where + " AND  " + this.getSqlFieldWithPrefix("deviceType", fieldPrefix) + " = :s" + (values.size() + 1);
				values.add(HiveAp.Device_TYPE_BRANCH_ROUTER);
			}else if("switches".equals(curMenu)
					|| "configSwitches".equals(curMenu)){
				where = where + " AND " + this.getSqlFieldWithPrefix("deviceType", fieldPrefix) + " = :s" + (values.size() + 1);
				values.add(HiveAp.Device_TYPE_SWITCH);
			}else if("deviceHiveAps".equals(curMenu)
					|| "configDeviceHiveAps".equals(curMenu)){
				where = where + " AND " + this.getSqlFieldWithPrefix("deviceType", fieldPrefix) + " = :s" + (values.size() + 1);
				values.add(HiveAp.Device_TYPE_HIVEAP);

				List<Short> modelList = new ArrayList<Short>();
				if(isEasyMode()){
					modelList.addAll(getCVGList());
					modelList.addAll(getBRList());
				}else{
					modelList.addAll(getCVGList());
				}
				where = where + " AND " + this.getSqlFieldWithPrefix("hiveApModel", fieldPrefix) + " not in (:s" + (values.size() + 1) + ")";
				values.add(modelList);
			}
			
			return where;
		}
		
		@Override
		public void preparePagingFilterParams() {
			if (filterParams == null) {
				List<Object> values = new ArrayList<Object>();
				String where = this.prepareDeviceTypeFilter(null, values, null, null);
				filterParams = FilterParamsFactory.getInstance().customizedFilter(where, values.toArray());
				setSessionFiltering();
			}
		}

		@Override
		public List<?> preparePageList() throws Exception {
			List<?> items = findBos(new HiveApAction());
			this.queryListObjectConfig();
			return items;
		}

		@Override
		public FilterParams prepareSearchOperation(
				DeviceInventoryFilterForm filterForm) {
			List<Object> values = new ArrayList<Object>();
			List<Short> deviceTypes = new ArrayList<Short>();
			String where = this.prepareDeviceTypeFilter(null, values, null, deviceTypes);

			if (null != filterForm.getFilterTemplate() 
					&& filterForm.getFilterTemplate() > 0) {
				where = where + " AND configTemplate.id = :s" + (values.size() + 1);
				values.add(filterForm.getFilterTemplate());
				//fix bug 18894
				if(!deviceTypes.isEmpty() && deviceTypes.
						contains(HiveAp.Device_TYPE_VPN_GATEWAY)){
					deviceTypes.remove((Object)HiveAp.Device_TYPE_VPN_GATEWAY);
				}
			}
			if (null != filterForm.getFilterHive() 
					&& filterForm.getFilterHive() > 0) {
				where = where + " AND configTemplate.hiveProfile.id = :s"
						+ (values.size() + 1);
				values.add(filterForm.getFilterHive());
				//fix bug 18894
				if(!deviceTypes.isEmpty() && deviceTypes.
						contains(HiveAp.Device_TYPE_VPN_GATEWAY)){
					deviceTypes.remove((Object)HiveAp.Device_TYPE_VPN_GATEWAY);
				}
			}
			if (null != filterForm.getFilterTopology() 
					&& filterForm.getFilterTopology() > 0) {
				where = where + " AND mapContainer.id = :s" + (values.size() + 1);
				values.add(filterForm.getFilterTopology());
			} else if (null != filterForm.getFilterTopology() 
					&& filterForm.getFilterTopology() == -1) {
				where = where + " AND mapContainer.id is null";
			}
			if (StringUtils.isNotBlank(filterForm.getFilterIp())) {
				where = where + " AND cfgIpAddress like :s" + (values.size() + 1);
				values.add(filterForm.getFilterIp().trim() + '%');
			}
			if (StringUtils.isNotBlank(filterForm.getFilterHostname())) {
				where = where + " AND lower(hostName) like :s"
						+ (values.size() + 1);
				values.add('%' + filterForm.getFilterHostname().trim().toLowerCase() + '%');
			}
			if (filterForm.isFilterRadiusServer()
					|| filterForm.isFilterVpnServer() 
					|| filterForm.isFilterVpnClient()
					|| filterForm.isFilterDhcpServer()
					|| filterForm.isFilterRadiusProxy()) {
				String filter = "";
				if (filterForm.isFilterRadiusServer()) {
					filter += "".equals(filter) ? "radiusServerProfile != null"
							: " or radiusServerProfile != null";
				}
				if (filterForm.isFilterRadiusProxy()) {
					filter += "".equals(filter) ? "radiusProxyProfile != null"
							: " or radiusProxyProfile != null";
				}
				if (filterForm.isFilterVpnServer()) {
					filter += "".equals(filter) ? "(configTemplate.vpnService != null AND vpnMark = "
							+ HiveAp.VPN_MARK_SERVER + ")"
							: " or (configTemplate.vpnService != null AND vpnMark = "
									+ HiveAp.VPN_MARK_SERVER + ")";
				}
				if (filterForm.isFilterVpnClient()) {
					filter += "".equals(filter) ? "(configTemplate.vpnService != null AND vpnMark = "
							+ HiveAp.VPN_MARK_CLIENT + ")"
							: " or (configTemplate.vpnService != null AND vpnMark = "
									+ HiveAp.VPN_MARK_CLIENT + ")";
				}
				if (filterForm.isFilterDhcpServer()) {
					filter += "".equals(filter) ? "dhcpServerCount > 0"
							: " or dhcpServerCount > 0";
				}
				where = where + " AND (" + filter + ")";
			}

			if (filterForm.getFilterApModel() >= 0) {
				where = where + " AND hiveApModel = :s" + (values.size() + 1);
				values.add(filterForm.getFilterApModel());
			}
			if (filterForm.getFilterDeviceType() >= 0) {
				if (filterForm.getFilterDeviceType() == 2){
					where = where + " AND (deviceType = :s" + (values.size() + 1);
					values.add(HiveAp.Device_TYPE_VPN_GATEWAY);
					where = where + " OR deviceType = :s" + (values.size() + 1);
					values.add(HiveAp.Device_TYPE_VPN_BR);
					where = where + " OR hiveApModel in (:s" + (values.size() + 1) + "))";
					values.add(getCVGList());
				} else {
					if (filterForm.getFilterDeviceType() == 0) {
						where = where + " AND hiveApModel not in (:s" + (values.size() + 1) + ")";
						values.add(getCVGList());
					}
					where = where + " AND deviceType = :s" + (values.size() + 1);
					values.add(filterForm.getFilterDeviceType());
				}
			}
			if (filterForm.isFilterEth0Bridge() 
					|| filterForm.isFilterEth1Bridge() 
					|| filterForm.isFilterRed0Bridge()
					|| filterForm.isFilterAgg0Bridge()) {
				String filter = "";
				if (filterForm.isFilterEth0Bridge()) {
					String query = "(eth0.operationMode = "
							+ AhInterface.OPERATION_MODE_ACCESS
							+ " OR eth0.operationMode = "
							+ AhInterface.OPERATION_MODE_BRIDGE + ")";

					filter += "".equals(filter) ? query : " or " + query;
				}

				if (filterForm.isFilterEth1Bridge()) {
					String query = "(eth1.operationMode = "
							+ AhInterface.OPERATION_MODE_ACCESS
							+ " OR eth1.operationMode = "
							+ AhInterface.OPERATION_MODE_BRIDGE + ")";
					filter += "".equals(filter) ? query : " or " + query;
				}

				if (filterForm.isFilterRed0Bridge()) {
					String query = "(red0.operationMode = "
							+ AhInterface.OPERATION_MODE_ACCESS
							+ " OR red0.operationMode = "
							+ AhInterface.OPERATION_MODE_BRIDGE + ")";
					filter += "".equals(filter) ? query : " or " + query;
				}

				if (filterForm.isFilterAgg0Bridge()) {
					String query = "(agg0.operationMode = "
							+ AhInterface.OPERATION_MODE_ACCESS
							+ " OR agg0.operationMode = "
							+ AhInterface.OPERATION_MODE_BRIDGE + ")";
					filter += "".equals(filter) ? query : " or " + query;
				}
				where = where + " AND (" + filter + ")";
			}

			if (StringUtils.isNotBlank(filterForm.getClassificationTag1())) {
				where = where + " AND classificationTag1 = :s" + (values.size() + 1);
				values.add(filterForm.getClassificationTag1());
			}
			if (StringUtils.isNotBlank(filterForm.getClassificationTag2())) {
				where = where + " AND classificationTag2 = :s" + (values.size() + 1);
				values.add(filterForm.getClassificationTag2());
			}
			if (StringUtils.isNotBlank(filterForm.getClassificationTag3())) {
				where = where + " AND classificationTag3 = :s" + (values.size() + 1);
				values.add(filterForm.getClassificationTag3());
			}
			
			if (StringUtils.isNotBlank(filterForm.getFilterSerialNumber())) {
				if (filterForm.getFilterSerialNumber().length() == 14) {
					where = where + " AND serialNumber = :s" + (values.size() + 1);
					values.add(filterForm.getFilterSerialNumber());
				} else {
					where = where + " AND serialNumber like :s" + (values.size() + 1);
					values.add("%" + filterForm.getFilterSerialNumber() + "%");
				}
			}
			
			if (!values.isEmpty()) {
				return new FilterParams(where, values.toArray());
			}
			
			return null;
		}

		@Override
		public void prepareDataSource() {
			setDataSource(HiveAp.class);
		}

		public Set<Long> getAllSelectedDeviceIds() {
			Set<Long> result = new HashSet<>();
			List<HiveAp> devices = QueryUtil.executeQuery(
					HiveAp.class, sortParams, filterParams, getUserContext());
			if (devices != null
					&& !devices.isEmpty()) {
				for (HiveAp device : devices) {
					result.add(device.getId());
				}
			}
			return result;
		}
		private void queryListObjectConfig() throws Exception {
			this.prepareListObjectConfig(
					QueryUtil.executeQuery(
							HiveAp.class, sortParams, filterParams, getUserContext()));
		}
		private JSONObject configObj = null;
		private void prepareListObjectConfig(List<HiveAp> items) throws Exception {
			if (items != null
					&& !items.isEmpty()) {
				this.configObj = new JSONObject();
				for (HiveAp item : items) {
					JSONObject singleObj = new JSONObject();
					this.configObj.put(item.getId().toString(), singleObj);
					singleObj.put("id", item.getId().toString());
					singleObj.put("precfg", true);
					singleObj.put("serialNum", item.getSerialNumber());
				}
			}
		}
		@Override
		public JSONObject getListObjectConfig() {
			return this.configObj;
		}
		
	}
	
	public String getListObjectConfigString() {
		if (this.diListPageObject.getListObjectConfig() != null) {
			return this.diListPageObject.getListObjectConfig().toString();
		}
		return "{}";
	}

	public DeviceInventoryFilterForm getDiFilter() {
		return diFilter;
	}

	public void setDiFilter(DeviceInventoryFilterForm diFilter) {
		this.diFilter = diFilter;
	}

	public DeviceInventoryFilterHelper getDiFilterHelper() {
		return diFilterHelper;
	}
	
	public String getDiMenuTypeKey() {
		return diMenuTypeKey;
	}

	public void setDiMenuTypeKey(String diMenuTypeKey) {
		this.diMenuTypeKey = diMenuTypeKey;
	}
	
	public String getDeviceStatusHMConnectedStr() {
		return MgrUtil.getEnumString("geneva_08.enum.device.inventory.connection.status.4");
	}
	public String getDeviceStatusHMNotConnectedStr() {
		return MgrUtil.getEnumString("geneva_08.enum.device.inventory.connection.status.3");
	}
	
	public String getFullModeConfigStyle() {
		if (isFullMode()) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getCustomTag1String() {
		Map<String,String> cusMap=DeviceTagUtil.getInstance().getClassifierCustomTag(this.getDomainId());
		return cusMap.get(DeviceTagUtil.CUSTOM_TAG1);
	}
	public String getCustomTag2String() {
		Map<String,String> cusMap=DeviceTagUtil.getInstance().getClassifierCustomTag(this.getDomainId());
		return cusMap.get(DeviceTagUtil.CUSTOM_TAG2);
	}
	public String getCustomTag3String() {
		Map<String,String> cusMap=DeviceTagUtil.getInstance().getClassifierCustomTag(this.getDomainId());
		return cusMap.get(DeviceTagUtil.CUSTOM_TAG3);
	}
	
	private List<Short> cvgList;
	
	private List<Short> brList;
	
	
	public List<Short> getCVGList(){
		if(cvgList == null){
			cvgList = new ArrayList<Short>();
			cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
			cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);
			return cvgList;
		}
		
		return cvgList;
	}
	
	public List<Short> getBRList(){
		if(brList == null){
			brList = new ArrayList<Short>();
			brList.add(HiveAp.HIVEAP_MODEL_BR100);
			brList.add(HiveAp.HIVEAP_MODEL_BR200);
			brList.add(HiveAp.HIVEAP_MODEL_BR200_WP);
			brList.add(HiveAp.HIVEAP_MODEL_BR200_LTE_VZ);
			return brList;
		}
		
		return brList;
	}
}
