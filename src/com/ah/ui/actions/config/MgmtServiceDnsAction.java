package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.hibernate.validator.constraints.Range;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceDnsInfo;
import com.ah.bo.useraccess.MgmtServiceSyslogInfo.EnumFacility;
import com.ah.bo.useraccess.MgmtServiceSyslogInfo.EnumSeverity;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.MgrUtil;

/*
 * Modification History
 * 
 *  setOwner(null) in cloning
 *  joseph chen 05/06/2008
 */

public class MgmtServiceDnsAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_DOMAIN = 2;
	
	public static final int COLUMN_IP = 3;
	
	public static final int COLUMN_DESCRIPTION = 4;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			//prepare some fields for jsonMode
			if (isJsonMode() && 
					"continue".equals(operation)) { // user profile
				restoreJsonContext();
			}
			
			if ("new".equals(operation)) {	
				if (!setTitleAndCheckAccess(getText("config.title.mgmtServiceDns"))) {
					return getLstForward();
				}
				setSessionDataSource(new MgmtServiceDns());
				initValues();
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(INPUT, "dnsJson");
			} else if ("create".equals(operation)) {
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					if (checkNameExists("mgmtName", getDataSource().getMgmtName())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getMgmtName()));
						return "json";
					}
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getMgmtName());
					try {
						updateIpAddressDns();
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
					if(checkNameExists("mgmtName", getDataSource().getMgmtName())){
						initValues();
						return INPUT;
					}
					updateIpAddressDns();
					createBo(dataSource);
					return prepareBoList();
				}
			} else if ("edit".equals(operation)) {
				fw = editBo(this);
				addLstTitle(getSelectedL2Feature().getDescription()
					+ " > Edit '" + getDisplayName() + "'");
				if(getDataSource()!=null){					
					initValues();
				}
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(fw, "dnsJson");
			} else if ("update".equals(operation)) {
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					try {
						if(getDataSource()!=null){
							updateIpAddressDns();
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
				} else {
					if(getDataSource()!=null){
						updateIpAddressDns();
						initValues();
					}
					return updateBo();
				}
			} else if (("update"+ getLstForward()).equals(operation)) {
				if(getDataSource()!=null){
					updateIpAddressDns();
					initValues();
				}
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			}else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				MgmtServiceDns dns = (MgmtServiceDns) findBoById(boClass, cloneId, this);
				dns.setId(null);
				dns.setMgmtName("");
				dns.setDnsInfo(getDnsInfo(dns.getDnsInfo()));
				dns.setOwner(null);    // joseph chen 05/06/2008
				dns.setVersion(null);  // joseph chen 06/17/2008
				setSessionDataSource(dns);
				initValues();
				addLstTitle(getText("config.title.mgmtServiceDns"));
				return INPUT;
			} else if (("create" + getLstForward()).equals(operation)) {
				if(checkNameExists("mgmtName", getDataSource().getMgmtName())){
					initValues();
					return getReturnPathWithJsonMode(INPUT, "dnsJson");
				}
				updateIpAddressDns();
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
			} else if ("newIpAddress".equals(operation)
					|| "editIpAddress".equals(operation)) {
				updateIpAddressDns();
				addLstForward("ipAddressDns");
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
				return getReturnPathWithJsonMode(INPUT, "dnsJson");
			} else if("addDns".equals(operation)){
				if (dataSource == null) {
					String rtnStr = prepareBoList();
					return getReturnPathWithJsonMode(rtnStr, "dnsJson");
				} else {
					buttonShowing = false;
					updateIpAddressDns();
					addSelectedIpAddressDns();
					initValues();
					return getReturnPathWithJsonMode(INPUT, "dnsJson");
				}
				
			}else if("removeDns".equals(operation)){
				if (dataSource == null) {
					String rtnStr = prepareBoList();
					return getReturnPathWithJsonMode(rtnStr, "dnsJson");
				} else {
					updateIpAddressDns();
					removeSelectedIpAddressDns();
					initValues();
					return getReturnPathWithJsonMode(INPUT, "dnsJson");
				}				
			}else {
				if (isJsonMode()) {
					prepareBoList();
					return "dnsJson";
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
		setSelectedL2Feature(L2_FEATURE_MGMT_SERVICE_DNS);
		setDataSource(MgmtServiceDns.class);
		
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_MANAGEMENT_SERVICE_DNS;
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
		case COLUMN_DOMAIN:
			code = "config.mgmtservice.domain.name";
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
		columns.add(new HmTableColumn(COLUMN_DOMAIN));
		columns.add(new HmTableColumn(COLUMN_IP));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}
	
	
	public MgmtServiceDns getDataSource() {
		return (MgmtServiceDns) dataSource;
	}
	public Range getNumberRange() {
		return super.getAttributeRange("userProfileId");
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
	
	public int getDomainNameLength() {
		return getAttributeLength("domainName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}
	
	public void initValues() throws Exception {
		getCommonIpAddress();
		prepareIpAddressDns();
		getInputIpValue();
	}	
	
	/*
	 * To make it look more like a table, if it is empty.
	 */
	public int getGridCount() {
		return getDataSource().getDnsInfo().size() < 1 ? GRID_COUNT : 0;
		//return getDataSource().getDnsInfo().size() <GRID_COUNT ? GRID_COUNT-getDataSource().getDnsInfo().size() : 0;
	}
	
	protected void prepareIpAddressDns() throws Exception{
		availableIpAddressDns = new ArrayList<CheckItem>();
		for (CheckItem ipAddressDns : commonIpAddressList) {
			
			if(ipAddressDns==null)
				continue;
			
			if (!isExistingIpAddressDns(ipAddressDns.getId())) {
				availableIpAddressDns.add(ipAddressDns);
			}
		}
		
		if (availableIpAddressDns.size() == 0) {
			availableIpAddressDns.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
	}
		
	protected void removeSelectedIpAddressDns(){
		if(ipAddressDnsIndices==null)
			return;
		Collection<MgmtServiceDnsInfo> removeList = new Vector<MgmtServiceDnsInfo>();
		for (String dns : ipAddressDnsIndices) {
			try {
				int index = Integer.parseInt(dns);
				if (index < getDataSource().getDnsInfo().size()) {
					removeList.add(getDataSource().getDnsInfo().get(index));		
				}
			} catch (NumberFormatException e) {
				// Bug in struts, shouldn't create a 'false' entry when no
				// check boxes checked.
				return;
			}
		}
		getDataSource().getDnsInfo().removeAll(removeList);
	}
		
	private List<MgmtServiceDnsInfo> getDnsInfo(List<MgmtServiceDnsInfo> list_info){
		List<MgmtServiceDnsInfo> list=new ArrayList<MgmtServiceDnsInfo>();
		if(list_info==null || list_info.size()<=0)
			return list;
		for(MgmtServiceDnsInfo dnsInfo:list_info){
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
	protected void addSelectedIpAddressDns() throws Exception {
		MgmtServiceDnsInfo dns = new MgmtServiceDnsInfo();
		IpAddress ip;
		
		/*
		 * select the existing IP object
		 */
		if (ipAddress != null && ipAddress != -1) {
			ip = findBoById(IpAddress.class,
					ipAddress);
		/*
		 * create new IP object by input value
		 */
		} else {
			short ipType = ImportCsvFileAction.getIpAddressWrongFlag(inputIpValue) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
			ip = CreateObjectAuto.createNewIP(inputIpValue, ipType, getDomain(), "For Management Service DNS");
		}
		
		
		dns.setIpAddress(ip);	
		dns.setDnsDescription(dnsDescription);
		dns.setServerName(ip.getAddressName());
		
		boolean existed = false;
		
		for(MgmtServiceDnsInfo dnsInfo : getDataSource().getDnsInfo()) {
			IpAddress tempIp = dnsInfo.getIpAddress();
			
			if(tempIp != null && 
					tempIp.getAddressName().equalsIgnoreCase(ip.getAddressName())) {
				addActionError(MgrUtil.getUserMessage("error.config.mgmt.service.dns.existedIp", MgrUtil.getUserMessage("config.mgmtservice.address")));
				buttonShowing=true;
				existed = true;
				break;
			}
		}
		
		if(!existed) {
			getDataSource().getDnsInfo().add(dns);
			inputIpValue = "";
		}
	}
	
	
	protected void updateIpAddressDns(){
		if(descriptionDns!=null)
			for(int i=0;i<descriptionDns.length 
			&& i<getDataSource().getDnsInfo().size();i++){
				getDataSource().getDnsInfo().get(i).setDnsDescription(descriptionDns[i]);
			}
	}
	
	protected boolean isExistingIpAddressDns(Long id) {
		if(id==null)
			return false;
		if(getDataSource().getDnsInfo()==null)
			return false;
		for (MgmtServiceDnsInfo ipAddressDns : getDataSource().getDnsInfo()) {
			if(ipAddressDns!=null && ipAddressDns.getIpAddress()!=null){
				if (id.equals(ipAddressDns.getIpAddress().getId())) {
					
					return true;
				}	
			}
		}
		return false;
	}
		
	public EnumSeverity[] getEnumSeverityValues()
	{
		return EnumSeverity.values();
	}
	public EnumFacility[] getEnumFacilityValues()
	{
		return EnumFacility.values();
	}

	public void getCommonIpAddress() throws Exception
	{
//		commonIpAddressList = this.getBoCheckItems("addressName", 
//				IpAddress.class, 
//				new FilterParams("typeFlag", IpAddress.TYPE_IP_ADDRESS),
//				CHECK_ITEM_BEGIN_BLANK, 
//				CHECK_ITEM_END_NO);
		commonIpAddressList = getIpObjectsBySingleIp(CHECK_ITEM_BEGIN_BLANK,
				CHECK_ITEM_END_NO);
		
		List<CheckItem> list=new ArrayList<CheckItem>();
		if(commonIpAddressList!=null && commonIpAddressList.size()>0){
			for(CheckItem item :commonIpAddressList){
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
			
			commonIpAddressList=new ArrayList<CheckItem>();
			commonIpAddressList.addAll(list);
		}
	}
	
	//for dns
	private List<CheckItem> availableIpAddressDns;
	private Collection<String> ipAddressDnsIndices;
	private String dnsDescription;
	private String[] descriptionDns;
	
	private List<CheckItem> commonIpAddressList;
	private boolean buttonShowing=false;

	private Long ipAddress;
	private String inputIpValue = "";
	
	public boolean getButtonShowing() {
		return buttonShowing;
	}

	public void setButtonShowing(boolean buttonShowing) {
		this.buttonShowing = buttonShowing;
	}	
	public Long getIpId() {
		if(this.commonIpAddressList==null || ! operation.equals("continue"))
			return null;
		return commonIpAddressList.get(commonIpAddressList.size()-1).getId();
	}


	public void setDnsDescription(String dnsDescription) {
		this.dnsDescription = dnsDescription;
	}

	public void setDescriptionDns(String[] descriptionDns) {
		this.descriptionDns = descriptionDns;
	}

	public void setIpAddressDnsIndices(Collection<String> ipAddressDnsIndices) {
		this.ipAddressDnsIndices = ipAddressDnsIndices;
	}

	public List<CheckItem> getAvailableIpAddressDns(){
		return this.availableIpAddressDns;
	}

	public String[] getDescriptionDns() {
		return descriptionDns;
	}

	public Long getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(Long ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public String getInputIpValue()
	{
		if (null != ipAddress) {
			for (CheckItem item : getAvailableIpAddressDns()) {
				if (item.getId().longValue() == ipAddress.longValue()) {
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
	
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		MgmtServiceDns source = QueryUtil.findBoById(MgmtServiceDns.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<MgmtServiceDns> list = QueryUtil.executeQuery(MgmtServiceDns.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		
		if (list.isEmpty()) {
			return null;
		}
		
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		
		for (MgmtServiceDns profile : list) {
			if (profile.isDefaultFlag()) {
				continue;
			}
			
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			MgmtServiceDns dns = source.clone();
			
			if (null == dns) {
				continue;
			}
			
			dns.setId(profile.getId());
			dns.setVersion(profile.getVersion());
			dns.setMgmtName(profile.getMgmtName());
			dns.setOwner(profile.getOwner());
			dns.setDefaultFlag(false);
			hmBos.add(dns);
		}
	
		return hmBos;
	}
	
	private void storeJsonContext() {
		getDataSource().setParentDomID(getParentDomID());
		getDataSource().setParentIframeOpenFlg(isParentIframeOpenFlg());
		getDataSource().setContentShowType(getContentShowType());
	}
	
	private void restoreJsonContext() {
		setParentDomID(getDataSource().getParentDomID());
		setParentIframeOpenFlg(getDataSource().isParentIframeOpenFlg());
		setContentShowType(getDataSource().getContentShowType());
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if(bo instanceof MgmtServiceDns) {
			MgmtServiceDns dns = (MgmtServiceDns)bo;

			if(dns.getDnsInfo() != null) {
				dns.getDnsInfo().size();
			}
		}

		return null;
	}
}