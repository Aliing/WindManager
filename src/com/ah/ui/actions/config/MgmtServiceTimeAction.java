package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.hibernate.validator.constraints.Range;
import org.json.JSONObject;

import com.ah.be.app.HmBeOsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.MgmtServiceTimeInfo;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/*
 * Modification History
 * 
 * support VHM
 *     set owner to null when cloning
 *     change commonIpAddressList from List<Object[]> to List<CheckItem>
 *     modify function prepareIpAddressSnmp,getCommonIpAddress
 * joseph chen 05/07/2008
 * 
 * support input ntp server directly
 * Fiona Feng 06/03/2009
 */

public class MgmtServiceTimeAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_TIME_ZONE = 2;
	
	public static final int COLUMN_CLIENT_SERVICE = 3;
	
	public static final int COLUMN_CLOCK_SYNC = 4;
	
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
				if (!setTitleAndCheckAccess(getText("config.title.mgmtServiceTime"))) {
					return getLstForward();
				}
				setSessionDataSource(new MgmtServiceTime());
				initTimeZone();
				return returnResultKeyWord(INPUT, "mstJson");
			} else if ("create".equals(operation)) {
				updateIpAddressTime();
				
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					if (checkNameExists("mgmtName", getDataSource().getMgmtName())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getMgmtName()));
						return "json";
					}
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getMgmtName());
					try {
						prepareSaveInfo();
						getDataSource().setTimeZoneStr(HmBeOsUtil.getTimeZoneString(getDataSource().getTimeZone()));
						id = createBo(dataSource);
						jsonObject.put("newObjId", id);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown",e.getMessage()));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				}else{
					
					if(checkNameExists("mgmtName", getDataSource().getMgmtName())){
						return INPUT;
					}
					prepareSaveInfo();
					// timezone str
					getDataSource().setTimeZoneStr(HmBeOsUtil.getTimeZoneString(getDataSource().getTimeZone()));
					createBo(dataSource);
					return prepareBoList();
				}
			} else if ("edit".equals(operation)) {
				fw = editBo(this);
				addLstTitle(getSelectedL2Feature().getDescription()
					+ " > Edit '" + getDisplayName() + "'");
				if(getDataSource()!=null){					
					if (getDataSource().getTimeInfo().size() == 0) {
						buttonShowing = true;
					}
					getDataSource().setTimeZone(HmBeOsUtil.getServerTimeZoneIndex(getDataSource().getTimeZoneStr()));
				}
				//return fw;
				return returnResultKeyWord(fw, "mstJson");
			} else if ("update".equals(operation)) {
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					try {
						if(getDataSource()!=null){
							updateIpAddressTime();
							prepareSaveInfo();
							getDataSource().setTimeZoneStr(HmBeOsUtil.getTimeZoneString(getDataSource().getTimeZone()));
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
						updateIpAddressTime();
						prepareSaveInfo();
						// timezone str
						getDataSource().setTimeZoneStr(HmBeOsUtil.getTimeZoneString(getDataSource().getTimeZone()));
					}
					return updateBo();
				}
			} else if (("update"+ getLstForward()).equals(operation)) {
				if(getDataSource()!=null){
					updateIpAddressTime();
					prepareSaveInfo();
					// timezone str
					getDataSource().setTimeZoneStr(HmBeOsUtil.getTimeZoneString(getDataSource().getTimeZone()));
				}
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				MgmtServiceTime time = (MgmtServiceTime) findBoById(boClass, cloneId, this);
				time.setId(null);
				time.setMgmtName("");
				time.setTimeInfo(getTimeInfo(time.getTimeInfo()));
				time.setOwner(null);      // joseph chen
				time.setVersion(null);    // joseph chen 06/17/2008
				time.setTimeZone(HmBeOsUtil.getServerTimeZoneIndex(time.getTimeZoneStr()));
				
				setSessionDataSource(time);
				addLstTitle(getText("config.title.mgmtServiceTime"));
				return INPUT;
			}else if (("create" + getLstForward()).equals(operation)) {
				updateIpAddressTime();
				if(checkNameExists("mgmtName", getDataSource().getMgmtName())){
					return returnResultKeyWord(INPUT, "mstJson");
				}
				// timezone str
				getDataSource().setTimeZoneStr(HmBeOsUtil.getTimeZoneString(getDataSource().getTimeZone()));
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
			} else if ("newIpAddressTime".equals(operation)
					|| "editIpAddressTime".equals(operation)) {
				updateIpAddressTime();
				addLstForward("ipAddressTime");
				prepareSaveInfo();
				return operation;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				buttonShowing = true;
				setId(dataSource.getId());
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				//return INPUT;
				return returnResultKeyWord(INPUT,"mstJson");
			} else if("addTime".equals(operation)){
				if (dataSource == null) {
					//return prepareBoList();
					String returnString = prepareBoList();
					return returnResultKeyWord(returnString,"mstJson");
				} else {
					updateIpAddressTime();
					buttonShowing = true;
					addSelectedIpAddressTime();
					//return INPUT;
					return returnResultKeyWord(INPUT,"mstJson");
				}
				
			}else if("removeTime".equals(operation)){
				if (dataSource == null) {
					//return prepareBoList();
					String returnString = prepareBoList();
					return returnResultKeyWord(returnString,"mstJson");
				} else {
					updateIpAddressTime();
					removeSelectedIpAddressTime();
					//return INPUT;
					return returnResultKeyWord(INPUT,"mstJson");
				}				
			}else {
				if (isJsonMode()) {
					prepareBoList();
					return "mstJson";
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
		setSelectedL2Feature(L2_FEATURE_MGMT_SERVICE_TIME);
		setDataSource(MgmtServiceTime.class);
		
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_MANAGEMENT_SERVICE_NTP;

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
		case COLUMN_TIME_ZONE:
			code = "config.mgmtservice.timeZone";
			break;
		case COLUMN_CLIENT_SERVICE:
			code = "config.mgmtservice.clientService";
			break;
		case COLUMN_CLOCK_SYNC:
			code = "config.mgmtservice.clockSync";
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
		columns.add(new HmTableColumn(COLUMN_TIME_ZONE));
		columns.add(new HmTableColumn(COLUMN_CLIENT_SERVICE));
		columns.add(new HmTableColumn(COLUMN_CLOCK_SYNC));
		columns.add(new HmTableColumn(COLUMN_IP));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}

	
	private List<MgmtServiceTimeInfo> getTimeInfo(List<MgmtServiceTimeInfo> list_info){
		List<MgmtServiceTimeInfo> list=new ArrayList<MgmtServiceTimeInfo>();
		if(list_info==null || list_info.size()<=0)
			return list;
		for(MgmtServiceTimeInfo dnsInfo:list_info){
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
	public void prepareSaveInfo(){
		if(getDataSource()==null)
			return;		
	
		if(getDataSource().getEnableClock() || !getDataSource().getEnableNtp())
			getDataSource().setTimeInfo(new ArrayList<MgmtServiceTimeInfo>());
			
	}
	public MgmtServiceTime getDataSource() {
		return (MgmtServiceTime) dataSource;
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

	public Range getNumberRange() {
		return super.getAttributeRange("interval");
	} 
	
	/*
	 * To make it look more like a table, if it is empty.
	 */
	public int getGridCount() {
		return getDataSource().getTimeInfo().size() < 1 ? GRID_COUNT : 0;
		//return getDataSource().getTimeInfo().size() <GRID_COUNT ? GRID_COUNT-getDataSource().getTimeInfo().size() : 0;
	}
			
	protected void removeSelectedIpAddressTime(){
		if(ipAddressTimeIndices==null)
			return;
		Collection<MgmtServiceTimeInfo> removeList = new Vector<MgmtServiceTimeInfo>();
		for (String time : ipAddressTimeIndices) {
			try {
				int index = Integer.parseInt(time);
				if (index < getDataSource().getTimeInfo().size()) {
					removeList.add(getDataSource().getTimeInfo().get(index));					
				}
			} catch (NumberFormatException e) {
				// Bug in struts, shouldn't create a 'false' entry when no
				// check boxes checked.
				return;
			}
		}
		getDataSource().getTimeInfo().removeAll(removeList);
	}
		
	protected void addSelectedIpAddressTime() throws Exception{
		IpAddress newIP;
		if (null != ipAddressId && ipAddressId > -1) {
			newIP = findBoById(IpAddress.class,
					ipAddressId);
		} else {
			for (MgmtServiceTimeInfo ipAddressTime : getDataSource().getTimeInfo()) {
				if (inputIpValue.equalsIgnoreCase(ipAddressTime.getServerName())) {
					return;
				}
			}
			short ipType = ImportCsvFileAction.getIpAddressWrongFlag(inputIpValue) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
			String description = "".equals(getDataSource().getMgmtName()) ? "For NTP Assignment" : "For NTP Assignment : "
				+ getDataSource().getMgmtName();
			newIP = CreateObjectAuto.createNewIP(inputIpValue, ipType, getDomain(), description);
		}

		MgmtServiceTimeInfo time = new MgmtServiceTimeInfo();
		time.setIpAddress(newIP);					
		time.setTimeDescription(timeDescription);
		getDataSource().getTimeInfo().add(time);
		inputIpValue = "";	
	}
		
	protected void updateIpAddressTime(){
		if(timeDescriptions!=null)
			for(int i=0;i<timeDescriptions.length 
			&& i<getDataSource().getTimeInfo().size();i++){
				getDataSource().getTimeInfo().get(i).setTimeDescription(timeDescriptions[i]);
			}		
	}

	public EnumItem[] getEnumTimeZoneValues()
	{
		return HmBeOsUtil.getEnumsTimeZone();
	}
	
	private Collection<String> ipAddressTimeIndices;
	private Long ipAddressId;
	private String timeDescription;
	private String[] timeDescriptions;
	private boolean buttonShowing=false;
	private final String displayIp="none";
	
	private String inputIpValue = "";

	public String getInputIpValue() {
		if (null != getIpAddressId()) {
			for (CheckItem item : getAvailableIpAddressTime()) {
				if (item.getId().longValue() == getIpAddressId().longValue()) {
					inputIpValue = item.getValue();
					break;
				}
			}
		}
		return inputIpValue;
	}

	public void setInputIpValue(String inputIpValue) {
		this.inputIpValue = inputIpValue;
	}
	
	public String getDisplayIp(){
		if(getDataSource()!=null && !getDataSource().getEnableClock()
				&& getDataSource().getEnableNtp())
			return "";
		return this.displayIp;
	}
	public boolean getButtonShowing() {
		return buttonShowing;
	}

	public void setButtonShowing(boolean buttonShowing) {
		this.buttonShowing = buttonShowing;
	}

	public List<CheckItem> getAvailableIpAddressTime() {
		List<CheckItem> commonIpAddressList = getIpObjectsByIpAndName();
		List<CheckItem> removeList = new ArrayList<CheckItem>();
		
		for (CheckItem oneItme : commonIpAddressList) {
			for (MgmtServiceTimeInfo ipAddressTime : getDataSource().getTimeInfo()) {
				if (oneItme.getValue().equals(ipAddressTime.getServerName())) {
					removeList.add(oneItme);
					break;
				}
			}
		}
		if (!removeList.isEmpty()) {
			commonIpAddressList.removeAll(removeList);
		}
		return commonIpAddressList;
	}

	public void setIpAddressTimeIndices(Collection<String> ipAddressTimeIndices) {
		this.ipAddressTimeIndices = ipAddressTimeIndices;
	}

	public String[] getTimeDescriptions() {
		return timeDescriptions;
	}

	public void setTimeDescriptions(String[] timeDescriptions) {
		this.timeDescriptions = timeDescriptions;
	}

	public void setTimeDescription(String timeDescription) {
		this.timeDescription = timeDescription;
	}

	public Long getIpAddressId() {
		return ipAddressId;
	}

	public void setIpAddressId(Long ipAddressId) {
		this.ipAddressId = ipAddressId;
	}

//	public void setServerName(String serverName) {
//		this.serverName = serverName;
//	}
	
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		MgmtServiceTime source = QueryUtil.findBoById(MgmtServiceTime.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<MgmtServiceTime> list = QueryUtil.executeQuery(MgmtServiceTime.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		
		if (list.isEmpty()) {
			return null;
		}
		
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		
		for (MgmtServiceTime profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			MgmtServiceTime time = source.clone();
			
			if (null == time) {
				continue;
			}
			
			time.setId(profile.getId());
			time.setVersion(profile.getVersion());
			time.setMgmtName(profile.getMgmtName());
			time.setOwner(profile.getOwner());
			hmBos.add(time);
		}
	
		return hmBos;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if(bo instanceof MgmtServiceTime) {
			MgmtServiceTime time = (MgmtServiceTime)bo;

			if(time.getTimeInfo() != null) {
				time.getTimeInfo().size();
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
	
	private void initTimeZone(){
		if(null != getDataSource()){
			int timezone = HmBeOsUtil.getServerTimeZoneIndex(null);
			if (getUserContext().getDomain().isHomeDomain()) {
				if (getUserContext().getSwitchDomain() != null) {
					timezone = HmBeOsUtil.getServerTimeZoneIndex(getUserContext().getSwitchDomain()
							.getTimeZoneString());
				}
			} else {
				timezone = HmBeOsUtil.getServerTimeZoneIndex(getUserContext().getDomain()
						.getTimeZoneString());
			}
			
			getDataSource().setTimeZone(timezone);
		}
	}
}