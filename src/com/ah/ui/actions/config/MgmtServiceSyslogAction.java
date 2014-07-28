package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceSyslogInfo;
import com.ah.bo.useraccess.MgmtServiceSyslog.EnumFacility;
import com.ah.bo.useraccess.MgmtServiceSyslog.EnumSeverity;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
//import com.ah.util.Tracer;

/*
 * Modification History
 * set owner to null in cloning
 * joseph chen 05/06/2008
 */

public class MgmtServiceSyslogAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

//	private static final Tracer log = new Tracer(MgmtServiceSyslogAction.class
//			.getSimpleName());

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_FACILITY = 2;
	
	public static final int COLUMN_IP = 3;
	
	public static final int COLUMN_DESCRIPTION = 4;
	
	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.mgmtServiceSyslog"))) {
					return getLstForward();
				}
				setSessionDataSource(new MgmtServiceSyslog());
				initValues();
				return returnResultKeyWord(INPUT, "syslogJson");
			} else if ("create".equals(operation)) {
				updateIpAddressSyslog();
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					
					if (checkNameExists("mgmtName", getDataSource().getMgmtName())) {
						initValues();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getMgmtName()));
						return "json";
					}
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getMgmtName());
					try {
						prepareSaveInfo();
						id = createBo(dataSource);
						jsonObject.put("newObjId", id);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown",e.getMessage()));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				} else {
					
					if(checkNameExists("mgmtName", getDataSource().getMgmtName())){
						initValues();
						return INPUT;
					}
					prepareSaveInfo();
					createBo(dataSource);
					return prepareBoList();
				}
			} else if ("edit".equals(operation)) {
				fw = editBo(this);
				addLstTitle(getSelectedL2Feature().getDescription()
					+ " > Edit '" + getDisplayName() + "'");
				if(getDataSource()!=null){					
					initValues();
					if (getDataSource().getSyslogInfo().size() == 0) {
						buttonShowing = true;
					}
				}
				return returnResultKeyWord(fw, "syslogJson");
			} else if ("update".equals(operation)) {
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					try {
						if(getDataSource()!=null){
							updateIpAddressSyslog();
							prepareSaveInfo();
							initValues();
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
						updateIpAddressSyslog();
						prepareSaveInfo();
						this.initValues();
					}
					return updateBo();
				}
				
			} else if (("update"+ getLstForward()).equals(operation)) {
				if(getDataSource()!=null){
					updateIpAddressSyslog();
					prepareSaveInfo();
					this.initValues();
				}
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			}else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				MgmtServiceSyslog syslog = (MgmtServiceSyslog) findBoById(boClass, cloneId, this);
				syslog.setId(null);
				syslog.setMgmtName("");
				syslog.setSyslogInfo(getSyslogInfo(syslog.getSyslogInfo()));
				syslog.setOwner(null);    // joseph chen, 05/06/2008
				syslog.setVersion(null);  // joseph chen 06/17/2008
				setSessionDataSource(syslog);
				initValues();
				addLstTitle(getText("config.title.mgmtServiceSyslog"));
				return INPUT;
			} else if (("create" + getLstForward()).equals(operation)) {
				updateIpAddressSyslog();
				if(checkNameExists("mgmtName", getDataSource().getMgmtName())){
					initValues();
					return INPUT;
				}
				initValues();
				id = createBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("newIpAddressSyslog".equals(operation)
					|| "editIpAddressSyslog".equals(operation)) {
				updateIpAddressSyslog();
				addLstForward("ipAddressSyslog");
				prepareSaveInfo();
				return operation;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				buttonShowing = true;
				initValues();
				setId(dataSource.getId());
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				return returnResultKeyWord(INPUT,"syslogJson");
			} else if("addSyslog".equals(operation)){
				if (dataSource == null) {
					String returnString = prepareBoList();
					return returnResultKeyWord(returnString,"syslogJson");
				} else {
					updateIpAddressSyslog();
					buttonShowing = false;
					addSelectedIpAddressSyslog();
					prepareSaveInfo();
					this.initValues();
					return returnResultKeyWord(INPUT,"syslogJson");
				}
				
			}else if("removeSyslog".equals(operation)){
				if (dataSource == null) {
					String returnString = prepareBoList();
					return returnResultKeyWord(returnString,"syslogJson");
				} else {
					updateIpAddressSyslog();
					removeSelectedIpAddressSyslog();
					prepareSaveInfo();
					initValues();
					return returnResultKeyWord(INPUT,"syslogJson");
				}				
			}else {
				if (isJsonMode()) {
					prepareBoList();
					return "syslogJson";
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
		setSelectedL2Feature(L2_FEATURE_MGMT_SERVICE_SYSLOG);
		setDataSource(MgmtServiceSyslog.class);
		
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_MANAGEMENT_SERVICE_SYSLOG;
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
		case COLUMN_FACILITY:
			code = "config.mgmtservice.facility";
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
		columns.add(new HmTableColumn(COLUMN_FACILITY));
		columns.add(new HmTableColumn(COLUMN_IP));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}
	
	public Long getIpId() {
		if(this.commonIpAddressList==null || ! operation.equals("continue"))
			return null;
		return commonIpAddressList.get(commonIpAddressList.size()-1).getId();
	}
	
	public void prepareSaveInfo(){
		if(getDataSource()==null)
			return;		
		
		getDataSource().setFacility(getIndexOfFacility(facilitySyslog));
	}
	public MgmtServiceSyslog getDataSource() {
		return (MgmtServiceSyslog) dataSource;
	}
	
	public boolean getDisableName() {
		return getDataSource() != null && getDataSource().getId() != null;
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
		getCommonIpAddress();
		prepareIpAddressSyslog();	
		if(getDataSource()!=null && getDataSource().getFacility()>=0)
			facilitySyslog=getNameOfFacility(getDataSource().getFacility());
		}

	
	/*
	 * To make it look more like a table, if it is empty.
	 */
	public int getGridCount() {
		return getDataSource().getSyslogInfo().size() < 1 ? GRID_COUNT : 0;
		//return getDataSource().getSyslogInfo().size() <GRID_COUNT ? GRID_COUNT-getDataSource().getSyslogInfo().size() : 0;
	}
	private List<MgmtServiceSyslogInfo> getSyslogInfo(List<MgmtServiceSyslogInfo> list_info){
		List<MgmtServiceSyslogInfo> list=new ArrayList<MgmtServiceSyslogInfo>();
		if(list_info==null || list_info.size()<=0)
			return list;
		for(MgmtServiceSyslogInfo dnsInfo:list_info){
			if(dnsInfo!=null ){
				if(dnsInfo.getIpAddress()!=null){
					IpAddress address = QueryUtil
					.findBoById(IpAddress.class, dnsInfo.getIpAddress().getId());
					dnsInfo.setIpAddress(address);
				}
				list.add(dnsInfo);
			}
		}
		return list;
	}		
	protected void removeSelectedIpAddressSyslog(){
		if(ipAddressSyslogIndices==null)
			return;
		Collection<MgmtServiceSyslogInfo> removeList = new Vector<MgmtServiceSyslogInfo>();
		for (String syslog : ipAddressSyslogIndices) {
			try {
				int index = Integer.parseInt(syslog);
				if (index < getDataSource().getSyslogInfo().size()) {
					removeList.add(getDataSource().getSyslogInfo().get(index));					
				}
			} catch (NumberFormatException e) {
				// Bug in struts, shouldn't create a 'false' entry when no
				// check boxes checked.
				return;
			}
		}
		getDataSource().getSyslogInfo().removeAll(removeList);
		ipAddressSyslogId = null;
	}
		
	protected void addSelectedIpAddressSyslog(){
		MgmtServiceSyslogInfo syslog = new MgmtServiceSyslogInfo();
		
		IpAddress address;
		
		/*
		 * select the existing IP object
		 */
		if (ipAddressSyslogId != null && ipAddressSyslogId != -1) {
			address = QueryUtil
			.findBoById(IpAddress.class, ipAddressSyslogId);
		/*
		 * create new IP object by input value
		 */
		} else {
			short ipType = ImportCsvFileAction.getIpAddressWrongFlag(inputIpValue) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
			address = CreateObjectAuto.createNewIP(inputIpValue, ipType, getDomain(), "For Management Service Syslog");
		}
		
		syslog.setIpAddress(address);	
		syslog.setSeverity(getIndexOfSeverity(severitySyslog));
		syslog.setSyslogDescription(syslogDescription);
		
		boolean existed = false;
		for(MgmtServiceSyslogInfo syslogInfo : getDataSource().getSyslogInfo()) {
			IpAddress tempIp = syslogInfo.getIpAddress();
			
			if(tempIp != null && 
					tempIp.getAddressName().equalsIgnoreCase(address.getAddressName())) {
				addActionError(MgrUtil.getUserMessage("error.config.mgmt.service.dns.existedIp", MgrUtil.getUserMessage("config.mgmtservice.syslog.server.address")));
				buttonShowing=true;
				existed = true;
				break;
			}
		}
		
		if(!existed) {
			getDataSource().getSyslogInfo().add(syslog);
			inputIpValue = "";
		}
	}
		
	protected void updateIpAddressSyslog(){
		if(syslogDescriptions!=null)
			for(int i=0;i<syslogDescriptions.length 
			&& i<getDataSource().getSyslogInfo().size();i++){
				getDataSource().getSyslogInfo().get(i).setSyslogDescription(syslogDescriptions[i]);
			}
		if(severitiesSyslog!=null)
			for(int i=0;i<severitiesSyslog.length 
			&& i<getDataSource().getSyslogInfo().size();i++){
				getDataSource().getSyslogInfo().get(i).setSeverity(getIndexOfSeverity(severitiesSyslog[i]));
			}
	}
	
	protected void prepareIpAddressSyslog() throws Exception{
		availableIpAddressSyslog = new ArrayList<CheckItem>();
		for (CheckItem ipAddressSyslog : commonIpAddressList) {
			if(ipAddressSyslog==null)
				continue;
			if (!isExistingIpAddressSyslog(ipAddressSyslog.getId())) {
				availableIpAddressSyslog.add(ipAddressSyslog);
			}
	   }	
		if (availableIpAddressSyslog.size() == 0) {
			availableIpAddressSyslog.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
	}
		    	
	protected boolean isExistingIpAddressSyslog(Long id) {
		if(id==null)
			return false;
		if(getDataSource().getSyslogInfo()==null)
			return false;
		for (MgmtServiceSyslogInfo ipAddressSyslog : getDataSource().getSyslogInfo()) {
			if(ipAddressSyslog!=null && ipAddressSyslog.getIpAddress()!=null){
				if (id.equals(ipAddressSyslog.getIpAddress().getId())) {
					
					return true;
				}	
			}
		}
		return false;
	}
	//Auth,Authpriv,Security,User,Local0,Local1,Local2,Local3,Local4,Local5,Local6,Local7;
	private String getNameOfFacility(short index){
		String name="";
		switch(index){
		case 0:
			name="Auth";
			break;
		case 1:
			name="Authpriv";
			break;
		case 2:
			name="Security";
			break;
		case 3:
			name="User";
			break;
		case 4:
			name="Local0";
			break;
		case 5:
			name="Local1";
			break;
		case 6:
			name="Local2";
			break;
		case 7:
			name="Local3";
			break;
		case 8:
			name="Local4";
			break;
		case 9:
			name="Local5";
			break;
		case 10:
			name="Local6";
			break;
		case 11:
			name="Local7";
			break;
		default:
			break;
		}
		return name;
	}
//	Auth,Authpriv,Security,User,Local0,Local1,Local2,Local3,Local4,Local5,Local6,Local7;
	private short getIndexOfFacility(String name){
		short index=-1;
		if(name==null || name.trim().equals(""))
			return index;
		if(name.trim().equals("Auth"))
			index=0;
		if(name.trim().equals("Authpriv"))
			index=1;
		if(name.trim().equals("Security"))
			index=2;
		if(name.trim().equals("User"))
			index=3;
		if(name.trim().equals("Local0"))
			index=4;
		if(name.trim().equals("Local1"))
			index=5;
		if(name.trim().equals("Local2"))
			index=6;
		if(name.trim().equals("Local3"))
			index=7;
		if(name.trim().equals("Local4"))
			index=8;
		if(name.trim().equals("Local5"))
			index=9;
		if(name.trim().equals("Local6"))
			index=10;
		if(name.trim().equals("Local7"))
			index=11;
		return index;
	}
//	private String getNameOfSeverity(short index){
//		String name="";
//		switch(index){
//		case 0:
//			name="Emergency";
//			break;
//		case 1:
//			name="Alert";
//			break;
//		case 2:
//			name="Critical";
//			break;
//		case 3:
//			name="Error";
//			break;
//		case 4:
//			name="Warning";
//			break;
//		case 5:
//			name="Notification";
//			break;
//		case 6:
//			name="Info";
//			break;
//		case 7:
//			name="Debug";
//			break;
//		default:
//			break;
//		}
//		return name;
//	}
	//Emergency,Alert,Critical,Error,Warning,Notification,Info,Debug
	private short getIndexOfSeverity(String name){
		short index=-1;
		if(name==null || name.trim().equals(""))
			return index;
		if(name.trim().equals("Emergency"))
			index=0;
		if(name.trim().equals("Alert"))
			index=1;
		if(name.trim().equals("Critical"))
			index=2;
		if(name.trim().equals("Error"))
			index=3;
		if(name.trim().equals("Warning"))
			index=4;
		if(name.trim().equals("Notification"))
			index=5;
		if(name.trim().equals("Info"))
			index=6;
		if(name.trim().equals("Debug"))
			index=7;
		return index;
	}
	
	public String getSeverityDefaultValue(){
		return EnumSeverity.Info.getKey();
	}
	public EnumItem[] getEnumIpAddressAndNameValues()
	{
		return EnumConstUtil.enumIpAddressAndName;
	}
	public EnumFacility[] getEnumFacilityValues()
	{
		return EnumFacility.values();
	}
	public EnumSeverity[] getEnumSeverityValues()
	{
		return EnumSeverity.values();
	}
	public void getCommonIpAddress() throws Exception
	{
		commonIpAddressList = getIpObjectsByIpAndName();
		
		List<CheckItem> list=new ArrayList<CheckItem>();
		
		if(commonIpAddressList!=null && commonIpAddressList.size()>0){
			for(CheckItem item : commonIpAddressList){
				int count=0;
				for(CheckItem tempItem : list){
					if(tempItem!=null && tempItem.getId().equals(item.getId())){
						count++;
						break;
					}					
				}
				
				if(count==0)
					list.add(item);	
			}
			
			commonIpAddressList = new ArrayList<CheckItem>();
			commonIpAddressList.addAll(list);
		}
	}

	//for Syslog
	private List<CheckItem> availableIpAddressSyslog;
	private Collection<String> ipAddressSyslogIndices;
	private Long ipAddressSyslogId;
	private String inputIpValue = "";
	private String syslogDescription;
	private String severitySyslog;
//	private String serverName;
	private String facilitySyslog;
	private String[] syslogDescriptions;
	private String[] ipAddressesSyslog;
	private String[] severitiesSyslog;
	private boolean buttonShowing=false;

	public boolean getButtonShowing() {
		return buttonShowing;
	}

	public void setButtonShowing(boolean buttonShowing) {
		this.buttonShowing = buttonShowing;
	}
	//common
	private List<CheckItem> commonIpAddressList;

//	public String[] getSyslogDescriptions() {
//		return syslogDescriptions;
//	}

	public void setSyslogDescriptions(String[] syslogDescriptions) {
		this.syslogDescriptions = syslogDescriptions;
	}

	public void setSyslogDescription(String syslogDescription) {
		this.syslogDescription = syslogDescription;
	}

	public String[] getIpAddressesSyslog() {
		return ipAddressesSyslog;
	}

	public void setIpAddressesSyslog(String[] ipAddressesSyslog) {
		this.ipAddressesSyslog = ipAddressesSyslog;
	}

	public List<CheckItem> getAvailableIpAddressSyslog() {
		return availableIpAddressSyslog;
	}

	public void setIpAddressSyslogId(Long ipAddressSyslogId) {
		this.ipAddressSyslogId = ipAddressSyslogId;
	}

	public Long getIpAddressSyslogId() {
		return this.ipAddressSyslogId;
	}
	
	public String getInputIpValue()
	{
		if (null != ipAddressSyslogId) {
			for (CheckItem item : getAvailableIpAddressSyslog()) {
				if (item.getId().longValue() == ipAddressSyslogId.longValue()) {
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

	public void setIpAddressSyslogIndices(Collection<String> ipAddressSyslogIndices) {
		this.ipAddressSyslogIndices = ipAddressSyslogIndices;
	}

	public String[] getVersionsSyslog() {
		return severitiesSyslog;
	}

	public void setSeveritiesSyslog(String[] severitiesSyslog) {
		this.severitiesSyslog = severitiesSyslog;
	}

//	public void setServerName(String serverName) {
//		this.serverName = serverName;
//	}

	public String[] getSeveritiesSyslog() {
		return severitiesSyslog;
	}

	public void setSeveritySyslog(String severitySyslog) {
		this.severitySyslog = severitySyslog;
	}

	public void setFacilitySyslog(String facilitySyslog) {
		this.facilitySyslog = facilitySyslog;
	}

	public String getFacilitySyslog() {
		return facilitySyslog;
	}

	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		MgmtServiceSyslog source = QueryUtil.findBoById(MgmtServiceSyslog.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<MgmtServiceSyslog> list = QueryUtil.executeQuery(MgmtServiceSyslog.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		
		if (list.isEmpty()) {
			return null;
		}
		
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		
		for (MgmtServiceSyslog profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			MgmtServiceSyslog syslog = source.clone();
			
			if (null == syslog) {
				continue;
			}
			
			syslog.setId(profile.getId());
			syslog.setVersion(profile.getVersion());
			syslog.setMgmtName(profile.getMgmtName());
			syslog.setOwner(profile.getOwner());
			hmBos.add(syslog);
		}
	
		return hmBos;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if(bo instanceof MgmtServiceSyslog) {
			MgmtServiceSyslog syslog = (MgmtServiceSyslog)bo;

			if(syslog.getSyslogInfo() != null) {
				syslog.getSyslogInfo().size();
			}
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