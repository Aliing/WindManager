package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSnmpInfo;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.NetTool;

/*
 * Modification History
 * 
 * support VHM
 *     set owner to null when cloning
 *     change commonIpAddressList from List<Object[]> to List<CheckItem>
 *     modify function prepareIpAddressSnmp,getCommonIpAddress
 * joseph chen 05/07/2008
 * 
 */
public class MgmtServiceSnmpAction extends BaseAction implements QueryBo {

    private static final long serialVersionUID = 1L;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_CONTACT = 2;
	
	public static final int COLUMN_ENABLE_SNMP = 3;
	
	public static final int COLUMN_ENABLE_CAPWAP = 4;
	
	public static final int COLUMN_IP = 5;
	
	public static final int COLUMN_DESCRIPTION = 6;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.mgmtServiceSnmp"))) {
					return getLstForward();
				}
				setSessionDataSource(new MgmtServiceSnmp());
				initValues();
				return returnResultKeyWord(INPUT, "snmpJson");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				updateIpAddressSnmp();
				
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					
					if (checkNameExists("mgmtName", getDataSource().getMgmtName())) {
						initValues();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getMgmtName()));
						return "json";
					}
					
					if(!checkIpAddressSnmp()){
						initValues();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", getActionErrors().toArray()[0].toString());
						return "json";
					}
					
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getMgmtName());
					try {
						id = createBo(dataSource);
						jsonObject.put("newObjId", id);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				} else {
					if(checkNameExists("mgmtName", getDataSource().getMgmtName()) || !checkIpAddressSnmp()){
						initValues();
						return INPUT;
					}
					if ("create".equals(operation)) {
						return createBo();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				fw = editBo(this);
				addLstTitle(getSelectedL2Feature().getDescription()
					+ " > Edit '" + getDisplayName() + "'");
				if(getDataSource()!=null){					
					initValues();
					if (getDataSource().getSnmpInfo().size() == 0) {
						buttonShowing = true;
					}
				}
				return returnResultKeyWord(fw, "snmpJson");
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					
					try {
						if(getDataSource()!=null){
							updateIpAddressSnmp();
							initValues();
							if(!checkIpAddressSnmp()){
								jsonObject.put("resultStatus", false);
								jsonObject.put("errMsg", getActionErrors().toArray()[0].toString());
								return "json";
							}
						}
						updateBo();
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				}else{
					if(getDataSource()!=null){
						updateIpAddressSnmp();
						initValues();
						if(!checkIpAddressSnmp()){
							return INPUT;
						}
					}
					if ("update".equals(operation)) {
						return updateBo();
					} else {
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			}else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				MgmtServiceSnmp snmp = (MgmtServiceSnmp) findBoById(boClass, cloneId, this);
				snmp.setId(null);
				snmp.setMgmtName("");
				snmp.setDefaultFlag(false);
				snmp.setSnmpInfo(getSnmpInfo(snmp.getSnmpInfo()));
				snmp.setOwner(null);    // joseph chen
				snmp.setVersion(null);  // joseph chen 06/17/2008
				setSessionDataSource(snmp);
				initValues();
				addLstTitle(getText("config.title.mgmtServiceSnmp"));
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("newIpAddressSnmp".equals(operation)
					|| "editIpAddressSnmp".equals(operation)) {
				updateIpAddressSnmp();
				addLstForward("ipAddressSnmp");
				prepareSaveInfo();
				return operation;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				
				buttonShowing = true;
				initValues();
				setId(dataSource.getId());
				
				if(ipAddressSnmpIds != null) {
					IpAddress address = QueryUtil
						.findBoById(IpAddress.class, ipAddressSnmpIds, this);
		
					if (NmsUtil.isLocalAddress(address)) {
						addActionError(MgrUtil
							.getUserMessage("error.config.mgmt.service.snmp.noHmSNMP"));
					}
				}
				
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				return returnResultKeyWord(INPUT,"snmpJson");
			} else if("addSnmp".equals(operation)){
				if (dataSource == null) {
					String returnString = prepareBoList();
					return returnResultKeyWord(returnString,"snmpJson");
				} else {
					updateIpAddressSnmp();
					buttonShowing = true;
					addSelectedIpAddressSnmp();
					initValues();
					return returnResultKeyWord(INPUT,"snmpJson");
				}
				
			}else if("removeSnmp".equals(operation)){
				if (dataSource == null) {
					String returnString = prepareBoList();
					return returnResultKeyWord(returnString,"snmpJson");
				} else {
					updateIpAddressSnmp();
					removeSelectedIpAddressSnmp();
					initValues();
					return returnResultKeyWord(INPUT,"snmpJson");
				}				
			}else {
				if (isJsonMode()) {
					prepareBoList();
					return "snmpJson";
				}
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MGMT_SERVICE_SNMP);
		setDataSource(MgmtServiceSnmp.class);
		
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_MANAGEMENT_SERVICE_SNMP;

	}

	protected void preparePage() throws Exception {
		enableSorting();
		enablePaging();
		page = findBos(this);
	}

	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.mgmtservice.name";
			break;
		case COLUMN_CONTACT:
			code = "config.mgmtservice.contact";
			break;
		case COLUMN_ENABLE_SNMP:
			code = "config.mgmtservice.enable.snmp";
			break;
		case COLUMN_ENABLE_CAPWAP:
			code = "config.mgmtservice.enable.capwap";
			break;
		case COLUMN_IP:
			code = "config.mgmtservice.ip.addresses";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.mgmtservice.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_CONTACT));
		columns.add(new HmTableColumn(COLUMN_ENABLE_SNMP));
		columns.add(new HmTableColumn(COLUMN_ENABLE_CAPWAP));
		columns.add(new HmTableColumn(COLUMN_IP));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}


	public void prepareSaveInfo(){
		if(getDataSource()==null) {
		}
	}
	
	public MgmtServiceSnmp getDataSource() {
		return (MgmtServiceSnmp) dataSource;
	}
	
	public boolean getDisableName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	private List<MgmtServiceSnmpInfo> getSnmpInfo(List<MgmtServiceSnmpInfo> list_info){
		List<MgmtServiceSnmpInfo> list=new ArrayList<MgmtServiceSnmpInfo>();
		if(list_info==null || list_info.size()<=0)
			return list;
		for(MgmtServiceSnmpInfo dnsInfo:list_info){
			if(dnsInfo!=null ){
				if(dnsInfo.getIpAddress()!=null){
					IpAddress address = QueryUtil
					.findBoById(IpAddress.class, dnsInfo.getIpAddress().getId(),this);
					dnsInfo.setIpAddress(address);
				}
				list.add(dnsInfo);
			}
		}
		return list;
	}
	public Long getIpId() {
		if(this.commonIpAddressList==null || ! operation.equals("continue"))
			return null;
		return commonIpAddressList.get(commonIpAddressList.size()-1).getId();
	}
	
	public String getUpdateDisabled()
	{
		if ("".equals(getWriteDisabled()))
		{
			return getDataSource().isDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}
	
	public String getDisplayName() {
		return getDataSource().getMgmtName().replace("\\", "\\\\").replace("'", "\\'");
	}

	public int getNameLength() {
		return getAttributeLength("mgmtName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public void initValues() throws Exception {
		if(this.commonIpAddressList==null || this.commonIpAddressList.size()<=0)
			getCommonIpAddress();
		prepareIpAddressSnmp();	
		
		snmpVersion = MgmtServiceSnmpInfo.MGMTSNMP_VERSION_V2C;
		snmpOperation = MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_GETANDTRAP;
		community = MgmtServiceSnmpInfo.newDefaultCommunity;
		
		authPassMethod=MgmtServiceSnmpInfo.PASS_AUTH_NONE;
		encryPassMethod=MgmtServiceSnmpInfo.PASS_ENCRYPTION_NONE;
		
		authPass="";
		userName="";
		encryPass="";
	}	

	/*
	 * To make it look more like a table, if it is empty.
	 */
	public int getGridCount() {
		return getDataSource().getSnmpInfo().size() < 1 ? GRID_COUNT : 0;
		//return getDataSource().getSnmpInfo().size() <GRID_COUNT ? GRID_COUNT-getDataSource().getSnmpInfo().size() : 0;
	}
			
	protected void removeSelectedIpAddressSnmp(){
		if(ipAddressSnmpIndices==null)
			return;
		Collection<MgmtServiceSnmpInfo> removeList = new Vector<MgmtServiceSnmpInfo>();
		for (String snmp : ipAddressSnmpIndices) {
			try {
				int index = Integer.parseInt(snmp);
				if (index < getDataSource().getSnmpInfo().size()) {
					removeList.add(getDataSource().getSnmpInfo().get(index));//					
				}
			} catch (NumberFormatException e) {
				// Bug in struts, shouldn't create a 'false' entry when no
				// check boxes checked.
				return;
			}
		}
		getDataSource().getSnmpInfo().removeAll(removeList);
		ipAddressSnmpIds = null;
	}
		
	protected void addSelectedIpAddressSnmp(){
		MgmtServiceSnmpInfo snmp = new MgmtServiceSnmpInfo();

		IpAddress address = null;
		
		/*
		 * select the existing IP object
		 */
		if (ipAddressSnmpIds != null && ipAddressSnmpIds != -1) {
			address = QueryUtil
			.findBoById(IpAddress.class, ipAddressSnmpIds, this);
		/*
		 * create new IP object by input value
		 */
		} else {
			SingleTableItem ipObj = NetTool.getIpObjectByInput(inputIpValue, true);
			if (null != ipObj) {
				address = CreateObjectAuto.createNewIP(ipObj.getIpAddress(), ipObj.getType(), getDomain(), "For Management Service SNMP", ipObj.getNetmask());
				
				//reload the new added address
				address = QueryUtil.findBoById(IpAddress.class, address.getId(), this);
			}
		}
		
		if (NmsUtil.isLocalAddress(address)) {
			addActionError(MgrUtil
					.getUserMessage("error.config.mgmt.service.snmp.noHmSNMP"));
			return;
		}
		
		snmp.setIpAddress(address);		
		snmp.setSnmpVersion(snmpVersion);
		snmp.setSnmpOperation(snmpOperation);
		if (snmpVersion==MgmtServiceSnmpInfo.MGMTSNMP_VERSION_V3){
			snmp.setCommunity("");
		} else {
			snmp.setCommunity(community);
		}
		if (snmpVersion==MgmtServiceSnmpInfo.MGMTSNMP_VERSION_V3){
			snmp.setUserName(userName);
			snmp.setAuthPassMethod(authPassMethod);
			if (authPassMethod==-1){
				snmp.setAuthPass("");
			} else {
				snmp.setAuthPass(authPass);
			}
			snmp.setEncryPassMethod(encryPassMethod);
			if (encryPassMethod==-1){
				snmp.setEncryPass("");
			} else {
				snmp.setEncryPass(encryPass);
			}
		} else {
			snmp.setUserName("");
			snmp.setAuthPassMethod((short)-1);
			snmp.setAuthPass("");
			snmp.setEncryPassMethod((short)-1);
			snmp.setEncryPass("");
		}
		
		boolean existed = false;
		for(MgmtServiceSnmpInfo snmpInfo : getDataSource().getSnmpInfo()) {
			IpAddress tempIp = snmpInfo.getIpAddress();
			
			if(tempIp != null && 
					tempIp.getAddressName().equalsIgnoreCase(address.getAddressName())) {
				addActionError(MgrUtil.getUserMessage("error.config.mgmt.service.dns.existedIp", MgrUtil.getUserMessage("config.mgmtservice.snmp.server.address")));
				buttonShowing=true;
				existed = true;
				break;
			}
		}
		
		if(!existed) {
			getDataSource().getSnmpInfo().add(snmp);
			inputIpValue = "";
		}
	}
	
	private boolean checkIpAddressSnmp(){
		/*
		 * HiveManager should
		 * not be in the SNMP server list
		 */
		for (MgmtServiceSnmpInfo info : getDataSource().getSnmpInfo()) {
			if (NmsUtil.isLocalAddress(info.getIpAddress())) {
				addActionError(MgrUtil.getUserMessage("error.config.mgmt.service.snmp.noHmSNMP"));
				return false;
			}
			
			if (info.getIpAddress()==null) {
			} else {
				if (info.getIpAddress().getTypeFlag()==IpAddress.TYPE_IP_ADDRESS){
					if (info.getSnmpOperation()==MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_GETANDTRAP
						|| info.getSnmpOperation()==MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_TRAP){
						for(SingleTableItem item : info.getIpAddress().getItems()) {
							if(item == null) {
								continue;
							}
							if(!item.getNetmask().equals("255.255.255.255")) {
								addActionError(MgrUtil.getUserMessage("error.config.mgmt.service.snmp.SNMPerror"));
								return false;
							}
						}
					}
				}else {
					if (info.getSnmpOperation()==MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_GETANDTRAP
						|| info.getSnmpOperation()==MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_TRAP){
						for(SingleTableItem item : info.getIpAddress().getItems()) {
							if(item == null) {
								continue;
							}
							String[] ipMask = item.getIpAddress().split("/");
							if (ipMask.length>2) {
								addActionError(MgrUtil.getUserMessage("error.config.mgmt.service.snmp.SNMPerror"));
								return false;
							} else if (ipMask.length==2) {
								if (!MgrUtil.checkIpAddress(ipMask[0])) {
									addActionError(MgrUtil.getUserMessage("error.config.mgmt.service.snmp.SNMPerror"));
									return false;
								}
								if (!ipMask[1].equals("32")){
									addActionError(MgrUtil.getUserMessage("error.config.mgmt.service.snmp.SNMPerror"));
									return false;
								}
							}
						}
					}else if(info.getSnmpOperation()!=MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_NONE) {
						for(SingleTableItem item : info.getIpAddress().getItems()) {
							if(item == null) {
								continue;
							}
							String[] ipMask = item.getIpAddress().split("/");
							if (ipMask.length>2) {
								addActionError(MgrUtil.getUserMessage("error.config.mgmt.service.snmp.SNMPerror"));
								return false;
							} else if (ipMask.length==2){
								if (!MgrUtil.checkIpAddress(ipMask[0])) {
									addActionError(MgrUtil.getUserMessage("error.config.mgmt.service.snmp.SNMPerror"));
									return false;
								}
								try {
									int netMask = Integer.parseInt(ipMask[1]);
									if (netMask<0 || netMask>32){
										addActionError(MgrUtil.getUserMessage("error.config.mgmt.service.snmp.SNMPerror"));
										return false;
									}
								} catch (NumberFormatException e) {
									addActionError(MgrUtil.getUserMessage("error.config.mgmt.service.snmp.SNMPerror"));
									return false;
								}
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
	
	
	protected void updateIpAddressSnmp(){
		if(communities!=null)
			for(int i=0;i<communities.length 
			&& i<getDataSource().getSnmpInfo().size();i++){
				getDataSource().getSnmpInfo().get(i).setCommunity(communities[i]);
			}
		if(snmpOperations!=null)
			for(int i=0;i<snmpOperations.length 
			&& i<getDataSource().getSnmpInfo().size();i++){
				getDataSource().getSnmpInfo().get(i).setSnmpOperation(snmpOperations[i]);
			}
		if(snmpVersions!=null)
			for(int i=0;i<snmpVersions.length 
			&& i<getDataSource().getSnmpInfo().size();i++){
				getDataSource().getSnmpInfo().get(i).setSnmpVersion(snmpVersions[i]);
			}
		if(userNames!=null)
			for(int i=0;i<userNames.length 
			&& i<getDataSource().getSnmpInfo().size();i++){
				getDataSource().getSnmpInfo().get(i).setUserName(userNames[i]);
			}
		if(authMethods!=null)
			for(int i=0;i<authMethods.length 
			&& i<getDataSource().getSnmpInfo().size();i++){
				getDataSource().getSnmpInfo().get(i).setAuthPassMethod(authMethods[i]);
			}
		if(authPasses!=null)
			for(int i=0;i<authPasses.length 
			&& i<getDataSource().getSnmpInfo().size();i++){
				if (getDataSource().getSnmpInfo().get(i).getAuthPassMethod()==-1){
					getDataSource().getSnmpInfo().get(i).setAuthPass("");
				} else {
					getDataSource().getSnmpInfo().get(i).setAuthPass(authPasses[i]);
				}
			}
		if(encryMethods!=null)
			for(int i=0;i<encryMethods.length 
			&& i<getDataSource().getSnmpInfo().size();i++){
				getDataSource().getSnmpInfo().get(i).setEncryPassMethod(encryMethods[i]);
			}
		if(encryPasses!=null)
			for(int i=0;i<encryPasses.length 
			&& i<getDataSource().getSnmpInfo().size();i++){
				if (getDataSource().getSnmpInfo().get(i).getEncryPassMethod()==-1){
					getDataSource().getSnmpInfo().get(i).setEncryPass("");
				} else {
					getDataSource().getSnmpInfo().get(i).setEncryPass(encryPasses[i]);
				}
			}
		
	}
	
	protected void prepareIpAddressSnmp() throws Exception{
		availableIpAddressSnmp = new ArrayList<CheckItem>();
		
		for (CheckItem ipAddressSnmp : commonIpAddressList) {
			if(ipAddressSnmp==null)
				continue;
			if (!isExistingIpAddressSnmp(ipAddressSnmp.getId())) {
				availableIpAddressSnmp.add(ipAddressSnmp);
			}
	   }
		
		/*
		 * remove local IP address
		 */
		removeLocalIP();
		
		if (availableIpAddressSnmp.size() == 0) {
			availableIpAddressSnmp.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
	}
	
	protected boolean isExistingIpAddressSnmp(Long id) {
		if(id==null)
			return false;
		
		if(getDataSource().getSnmpInfo()==null)
			return false;
		
		for (MgmtServiceSnmpInfo ipAddressSnmp : getDataSource().getSnmpInfo()) {
			if(ipAddressSnmp!=null && ipAddressSnmp.getIpAddress()!=null){
				if (id.equals(ipAddressSnmp.getIpAddress().getId())) {
					
					return true;
				}	
			}
		}
		
		return false;
	}

	private void removeLocalIP() {
		Iterator<?> it = availableIpAddressSnmp.iterator();
		
		while(it.hasNext()) {
			CheckItem item = (CheckItem)it.next();
			IpAddress ip = QueryUtil.findBoById(IpAddress.class, item.getId(), this);
			
			if(ip == null) {
				continue;
			}
			
			if(NmsUtil.isLocalAddress(ip)) {
				it.remove();
			}
		}
	}
	
	public EnumItem[] getEnumIpAddressAndNameValues()
	{
		return EnumConstUtil.enumIpAddressAndName;
	}
	
	public EnumItem[] getEnumOperationValues()
	{
		return MgmtServiceSnmpInfo.ENUM_MGMTSNMP_OPERATION;
	}
	
	public EnumItem[] getEnumAuthPassMethodValues()
	{
		return MgmtServiceSnmpInfo.ENUM_PASSWORD_AUTH_METHOD;
	}
	
	public List<CheckItem> getEnumAuthPassMethodValuesNull()
	{
		List<CheckItem> items = new ArrayList<CheckItem>();
		return items;
	}
	
	public EnumItem[] getEnumEncryPassMethodValues()
	{
		return MgmtServiceSnmpInfo.ENUM_PASSWORD_ENCRY_METHOD;
	}
	
	public List<CheckItem> getEnumEncryPassMethodValuesNull()
	{
		List<CheckItem> items = new ArrayList<CheckItem>();
		return items;
	}
	
	public EnumItem[] getEnumVersionV3Values()
	{
		return 	MgrUtil.enumItems(
				"enum.mgmt.snmp.version.", new int[] { 
						MgmtServiceSnmpInfo.MGMTSNMP_VERSION_V3});
	}
	
	public EnumItem[] getEnumVersionV1V2Values()
	{
		return 	MgrUtil.enumItems(
				"enum.mgmt.snmp.version.", new int[] { 
						MgmtServiceSnmpInfo.MGMTSNMP_VERSION_V1, 
						MgmtServiceSnmpInfo.MGMTSNMP_VERSION_V2C});
	}
	
	public EnumItem[] getEnumVersionValues()
	{
		return MgmtServiceSnmpInfo.ENUM_MGMTSNMP_VERSION;
	}

	public void getCommonIpAddress() throws Exception
	{
		commonIpAddressList = getIpObjectsByIpNameAndNet();
		
		List<CheckItem> list=new ArrayList<CheckItem>();
		if(commonIpAddressList!=null && commonIpAddressList.size()>0){
			for(CheckItem item:commonIpAddressList){
				int count=0;
				for(CheckItem tempItem:list){
					if(tempItem!=null && tempItem.getId().equals(item.getId())){
						count++;
						break;
					}					
				}
				if(count==0)
					list.add(item);	
			}
			
			commonIpAddressList=new ArrayList<CheckItem>();
			commonIpAddressList.addAll(list);
		}
	}

	//for Snmp
	private List<CheckItem> availableIpAddressSnmp;
	private Collection<String> ipAddressSnmpIndices;
	private Long ipAddressSnmpIds;
	private String inputIpValue = "";
	private String community = MgmtServiceSnmpInfo.newDefaultCommunity;
	private short snmpVersion = MgmtServiceSnmpInfo.MGMTSNMP_VERSION_V2C;
	private short snmpOperation = MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_GETANDTRAP;
	
	
	private short authPassMethod=MgmtServiceSnmpInfo.PASS_AUTH_NONE;
	private short encryPassMethod=MgmtServiceSnmpInfo.PASS_ENCRYPTION_NONE;
	private String userName;
	private String authPass;
	private String encryPass;
	
	private boolean enableSnmp;
	
	private String[] communities;
	private short[] snmpVersions;
	private short[] snmpOperations;
	
	private String[] userNames;
	private short[] authMethods;
	private String[] authPasses;
	private short[] encryMethods;
	private String[] encryPasses;
	
	private List<CheckItem> commonIpAddressList=new ArrayList<CheckItem>();
	private boolean buttonShowing=false;

	public boolean getButtonShowing() {
		return buttonShowing;
	}

	public void setButtonShowing(boolean buttonShowing) {
		this.buttonShowing = buttonShowing;
	}	
	public String[] getCommunities() {
		return communities;
	}

	public void setCommunities(String[] communities) {
		this.communities = communities;
	}

	public List<CheckItem> getAvailableIpAddressSnmp() {
		return availableIpAddressSnmp;
	}

	public void setIpAddressSnmpIds(Long ipAddressSnmpIds) {
		this.ipAddressSnmpIds = ipAddressSnmpIds;
	}
	
	public Long getIpAddressSnmpIds() {
		return this.ipAddressSnmpIds;
	}
	
	public String getInputIpValue()
	{
		if (null != ipAddressSnmpIds) {
			for (CheckItem item : getAvailableIpAddressSnmp()) {
				if (item.getId().longValue() == ipAddressSnmpIds.longValue()) {
					inputIpValue = item.getValue();
					break;
				}
			}
		}
		return inputIpValue;
	}

	public void setInputIpValue(String inputIpValue)
	{
		this.inputIpValue = inputIpValue;
	}

	public void setIpAddressSnmpIndices(Collection<String> ipAddressSnmpIndices) {
		this.ipAddressSnmpIndices = ipAddressSnmpIndices;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getCommunity() {
		return this.community;
	}
	
	public boolean getEnableSnmp() {
		return enableSnmp;
	}

	public void setEnableSnmp(boolean enableSnmp) {
		this.enableSnmp = enableSnmp;
	}

	public short getSnmpVersion()
	{
		return snmpVersion;
	}

	public void setSnmpVersion(short snmpVersion)
	{
		this.snmpVersion = snmpVersion;
	}

	public short getSnmpOperation()
	{
		return snmpOperation;
	}

	public void setSnmpOperation(short snmpOperation)
	{
		this.snmpOperation = snmpOperation;
	}

	public short[] getSnmpVersions()
	{
		return snmpVersions;
	}

	public void setSnmpVersions(short[] snmpVersions)
	{
		this.snmpVersions = snmpVersions;
	}

	public short[] getSnmpOperations()
	{
		return snmpOperations;
	}

	public void setSnmpOperations(short[] snmpOperations)
	{
		this.snmpOperations = snmpOperations;
	}

	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		MgmtServiceSnmp source = QueryUtil.findBoById(MgmtServiceSnmp.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<MgmtServiceSnmp> list = QueryUtil.executeQuery(MgmtServiceSnmp.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		
		if (list.isEmpty()) {
			return null;
		}
		
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		
		for (MgmtServiceSnmp profile : list) {
			if (profile.isDefaultFlag()) {
				continue;
			}
			
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			MgmtServiceSnmp snmp = source.clone();
			
			if (null == snmp) {
				continue;
			}
			
			snmp.setId(profile.getId());
			snmp.setVersion(profile.getVersion());
			snmp.setMgmtName(profile.getMgmtName());
			snmp.setOwner(profile.getOwner());
			snmp.setDefaultFlag(false);
			hmBos.add(snmp);
		}
	
		return hmBos;
	}

	public short getAuthPassMethod() {
		return authPassMethod;
	}

	public short getEncryPassMethod() {
		return encryPassMethod;
	}

	public String getUserName() {
		return userName;
	}

	public String getAuthPass() {
		return authPass;
	}

	public String getEncryPass() {
		return encryPass;
	}

	public void setAuthPassMethod(short authPassMethod) {
		this.authPassMethod = authPassMethod;
	}

	public void setEncryPassMethod(short encryPassMethod) {
		this.encryPassMethod = encryPassMethod;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setAuthPass(String authPass) {
		this.authPass = authPass;
	}

	public void setEncryPass(String encryPass) {
		this.encryPass = encryPass;
	}

	public String[] getUserNames() {
		return userNames;
	}

	public short[] getAuthMethods() {
		return authMethods;
	}

//	public String[] getAuthPasses() {
//		return authPasses;
//	}

	public short[] getEncryMethods() {
		return encryMethods;
	}

//	public String[] getEncryPasses() {
//		return encryPasses;
//	}

	public void setUserNames(String[] userNames) {
		this.userNames = userNames;
	}

	public void setAuthMethods(short[] authMethods) {
		this.authMethods = authMethods;
	}

//	public void setAuthPasses(String[] authPasses) {
//		this.authPasses = authPasses;
//	}

	public void setEncryMethods(short[] encryMethods) {
		this.encryMethods = encryMethods;
	}

	/**
	 * @return the authPasses
	 */
	public String[] getAuthPasses() {
		return authPasses;
	}

	/**
	 * @param authPasses the authPasses to set
	 */
	public void setAuthPasses(String[] authPasses) {
		this.authPasses = authPasses;
	}

	/**
	 * @return the encryPasses
	 */
	public String[] getEncryPasses() {
		return encryPasses;
	}

	/**
	 * @param encryPasses the encryPasses to set
	 */
	public void setEncryPasses(String[] encryPasses) {
		this.encryPasses = encryPasses;
	}

//	public void setEncryPasses(String[] encryPasses) {
//		this.encryPasses = encryPasses;
//	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if(bo instanceof MgmtServiceSnmp) {
			MgmtServiceSnmp snmp = (MgmtServiceSnmp)bo;

			if(snmp.getSnmpInfo() != null) {
				snmp.getSnmpInfo().size();
				for (MgmtServiceSnmpInfo serviceSnmpInfo : snmp.getSnmpInfo()) {
				    IpAddress ipAddress=serviceSnmpInfo.getIpAddress();
				    if(null != ipAddress && null != ipAddress.getItems())
				        ipAddress.getItems().size();
                }
			}
		}
       if (bo instanceof IpAddress) {
          IpAddress ipAddress = (IpAddress) bo;
          if(null != ipAddress.getItems())
              ipAddress.getItems().size();
       }
		return null;
	}
	
	protected String returnResultKeyWord(String normalkey, String expressKey){
		if(isJsonMode()) {
			return  expressKey;
		} else {
			return normalkey;
		}
	}

}