/**
 *@filename		IpAddressAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-8-29 PM 03:31:22
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class IpAddressAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private String radioIpOrName;
	
	private String ipType;
	
	public String getIpType() {
		return ipType;
	}

	public void setIpType(String ipType) {
		this.ipType = ipType;
	}

	private static final String IP_HOSTNAME_OBJECT_LOCATION_FLAG = "IP_HOSTNAME_OBJECT_LOCATION_FLAG";

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if (null == operation || L2_FEATURE_IP_ADDRESS.equals(operation) || "new".equals(operation)
				 || "edit".equals(operation)) {
				MgrUtil.setSessionAttribute(IP_HOSTNAME_OBJECT_LOCATION_FLAG, isJsonMode());
			} else if (null != MgrUtil.getSessionAttribute(IP_HOSTNAME_OBJECT_LOCATION_FLAG)){
				setJsonMode((Boolean)MgrUtil.getSessionAttribute(IP_HOSTNAME_OBJECT_LOCATION_FLAG));
			}
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.ipAddress"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new IpAddress());
				hideCreateItem = "none";
				hideNewButton = "";
				if ("name".equals(radioIpOrName)) {
					getDataSource().setTypeFlag(IpAddress.TYPE_HOST_NAME);
				}else if("webPage".equals(radioIpOrName)){
					getDataSource().setTypeFlag(IpAddress.TYPE_WEB_PAGE);
				} else if ("network".equals(radioIpOrName)) {
					getDataSource().setTypeFlag(IpAddress.TYPE_IP_NETWORK);
				} else if ("range".equals(radioIpOrName)) {
					getDataSource().setTypeFlag(IpAddress.TYPE_IP_RANGE);
				} else if ("wildcard".equals(radioIpOrName)) {
					getDataSource().setTypeFlag(IpAddress.TYPE_IP_WILDCARD);
				}
				return isJsonMode() ? "newIpObj" : INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				updateIPItems();
				if (isJsonMode() && (isVpnServiceDnsIpFlag() 
											|| ("createhiveAp".equals(operation) && !isParentIframeOpenFlg()) 
											|| ("createhiveAp2".equals(operation) && !isParentIframeOpenFlg()) 
											|| "createcwpWGIp".equals(operation)
											|| "createhiveApMulti".equals(operation)
											|| "createhiveApMulti2".equals(operation)
											|| "createcwpSuccessURL".equals(operation)
											|| "createcwpFailureURL".equals(operation)
									)) {
					 if (checkIPName(getDataSource().getAddressName())
								|| !checkGlobalIpExist()) {
						 if(!isParentIframeOpenFlg()){
							 jsonObject = new JSONObject();
							jsonObject.put("resultStatus", false);
							jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getAddressName()));
							return "json";
						 }
						 return isJsonMode() ? "newIpObj" : INPUT;
					 }
					
				} else {
					if (checkIPName(getDataSource().getAddressName())
							|| !checkGlobalIpExist()) {
						return isJsonMode() ? "newIpObj" : INPUT;
					}
				}
				
				if (isJsonMode() && (isVpnServiceDnsIpFlag() 
											|| ("createhiveAp".equals(operation) && !isParentIframeOpenFlg())
											|| ("createhiveAp2".equals(operation) && !isParentIframeOpenFlg())
											|| "createcwpWGIp".equals(operation)
											|| "createhiveApMulti".equals(operation)
											|| "createhiveApMulti2".equals(operation)
											|| "createcwpSuccessURL".equals(operation)
											|| "createcwpFailureURL".equals(operation)
									)) {
					if(!isParentIframeOpenFlg()){
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", true);
						jsonObject.put("parentDomID", getParentDomID());
						jsonObject.put("addedName", getDataSource().getAddressName());
						try {
							id = createBo(dataSource);
							jsonObject.put("addedId", id);
						} catch (Exception e) {
							jsonObject.put("resultStatus", false);
							jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
							return "json";
						}
						return "json";
					}else{
						if ("create".equals(operation)) {
							createBo(dataSource);
							return prepareIPAddressBoList();
						} else {
							id = createBo(dataSource);
							setUpdateContext(true);
							return getLstForward();
						}
					}
					
				}else{
					if ("create".equals(operation)) {
						createBo(dataSource);
						return prepareIPAddressBoList();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				if (dataSource == null) {
					return prepareIPAddressBoList();
				} else {
					addLstTitle(getText("config.title.ipAddress.edit") + " '"
							+ this.getChangedName() + "'");
					return isJsonMode() ? "newIpObj" : INPUT;
				}
			} else if ("update".equals(operation) || ("update" + getLstForward()).equals(operation)) {
				if (dataSource == null || dataSource.getId() == null || !dataSource.getId().equals(id)) {
					throw new HmException(
							"Update object failed, session must have been shared by another browser window.",
							HmMessageCodes.STALE_SESSION_OBJECT,
							new String[] { "Update" });
				}
				if (dataSource != null) {
					updateIPItems();
					if (!checkGlobalIpExist()) {
						return isJsonMode() ? "newIpObj" : INPUT;
					}
					/*id = dataSource.getId();*/
				}

				if (isJsonMode() && (isVpnServiceDnsIpFlag() 
											|| ("updatehiveAp".equals(operation) && !isParentIframeOpenFlg())
											|| ("updatehiveAp2".equals(operation) && !isParentIframeOpenFlg())
											|| "updatecwpWGIp".equals(operation)
											|| "updatehiveApMulti".equals(operation)
											|| "updatehiveApMulti2".equals(operation)
											|| "updatecwpSuccessURL".equals(operation)
											|| "updatecwpFailureURL".equals(operation)
									)) {
					if(!isParentIframeOpenFlg()){
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", true);
						jsonObject.put("parentDomID", getParentDomID());
						try {
							updateBo(dataSource);
						} catch (Exception e) {
							jsonObject.put("resultStatus", false);
							jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
							return "json";
						}
						return "json";
					}else{
						if ("update".equals(operation)) {
							updateBo(dataSource);
							return prepareIPAddressBoList();
						} else {
							updateBo(dataSource);
							setUpdateContext(true);
							return getLstForward();
						}
					}
					
				}else{
					if ("update".equals(operation)) {
						updateBo(dataSource);
						return prepareIPAddressBoList();
					} else {
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				IpAddress profile = (IpAddress) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setAddressName("");
				profile.setDefaultFlag(false);
				profile.setOwner(null);
				profile.setVersion(null);
				List<SingleTableItem> items = new ArrayList<SingleTableItem>();
				items.addAll(profile.getItems());
				profile.setItems(items);
				setSessionDataSource(profile);
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if(getDataSource() != null){
					id = getDataSource().getId();
				}
				setUpdateContext(true);
				return getLstForward();
			} else if ("addIPAddress".equals(operation)) {
				if (dataSource == null) {
					return prepareIPAddressBoList();
				} else {
					updateIPItems();
					if (!addSingleIP()) {
						addActionError(MgrUtil
								.getUserMessage("error.addObjectTypeExists"));
					}
					return isJsonMode() ? "newIpObj" : INPUT;
				}
			} else if ("removeIPAddress".equals(operation)
					|| "removeIPAddressNone".equals(operation)) {
				hideCreateItem = "removeIPAddressNone".equals(operation) ? ""
						: "none";
				hideNewButton = "removeIPAddressNone".equals(operation) ? "none"
						: "";
				if (dataSource == null) {
					return prepareIPAddressBoList();
				} else {
					updateIPItems();
					removeSelectedIPs();
					return isJsonMode() ? "newIpObj" : INPUT;
				}
			} else if ("import".equals(operation)) {
				addLstForward("ipAddress");
				clearErrorsAndMessages();
				return operation;
			} else if("createSimpleIp".equals(operation)){
				jsonObject = createSimpleIp();
				return "json";
			} else if("removeSimpleIp".equals(operation)){
				jsonObject = removeSimpleIp();
				return "json";
			} else {
				baseOperation();
				return prepareIPAddressBoList();
			}
		} catch (Exception e) {
			if (isJsonMode()) {
				addActionError(MgrUtil.getUserMessage(e));
				generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
						+ " " + MgrUtil.getUserMessage(e));
				return "newIpObj";
			}
			reportActionError(e);
			return prepareIPAddressBoList();
		}
	}

	private String prepareIPAddressBoList() throws Exception{
	    String str = prepareBoList();
	    loadLazyData();
	    return str;
	}
	
	private void loadLazyData() {
        if (page.isEmpty())
            return;

        Map<Long, IpAddress> ipAddressMap = new HashMap<Long, IpAddress>();
		StringBuilder buf = new StringBuilder();
        for (Object element : page) {
            IpAddress ipAddress = (IpAddress) element;
            ipAddressMap.put(ipAddress.getId(), ipAddress);
            buf.append(ipAddress.getId());
            buf.append(",");

            ipAddress.setItems(new ArrayList<SingleTableItem>());
        }
        buf.deleteCharAt(buf.length()-1);

        String sql = "select a.id,b.ip_address_id,b.ipaddress,b.netmask,b.type"
                + " from ip_address a "
                + " inner join ip_address_item b on b.ip_address_id = a.id "
                + " where a.id in(" + buf.toString() + ")";
        
        List<?> templates = QueryUtil.executeNativeQuery(sql);

        for (Object obj : templates) {
            Object[] template = (Object[]) obj;
            Long id = Long.valueOf(template[0].toString());

            IpAddress templateElement = ipAddressMap.get(id);
            if (templateElement != null) {
                if (StringUtils.isNotBlank(template[1].toString())) {
                    SingleTableItem tempClass = new SingleTableItem();
                    tempClass.setIpAddress((String) template[2]);
                    tempClass.setNetmask((String) template[3]);
                    tempClass.setType(Short.parseShort(template[4].toString()));
                    templateElement.getItems().add(tempClass);
                }
            }
        }
    }

    private JSONObject createSimpleIp() throws JSONException {
		return CreateObjectAuto.createSimpleIp(ip, getDomain());
	}

	private JSONObject removeSimpleIp() throws JSONException {
		List<Long> ids = new ArrayList<Long>();
		if(null != ipIdString){
			String[] array = ipIdString.split(",");
			for (String s : array) {
				long id = Long.parseLong(s);
				ids.add(id);
			}
		}
		return CreateObjectAuto.removeSimpleIp(ids);
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_IP_ADDRESS);
		setDataSource(IpAddress.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_IP;
		
		// avoid cancel back error when open two edit view 
		if(L2_FEATURE_IP_ADDRESS.equals(request.getParameter("operation"))
		        || "create".equals(request.getParameter("operation")) 
		        || ("create" + getLstForward()).equals(request.getParameter("operation"))
		        || "update".equals(request.getParameter("operation")) 
		        || ("update"+ getLstForward()).equals(request.getParameter("operation"))) {
		    if(null == getDataSource()) {
		        setSessionDataSource(new IpAddress());
		    }
		}
	}

	@Override
	public IpAddress getDataSource() {
		return (IpAddress) dataSource;
	}

	private boolean checkIPName(String name) {
		if (MgrUtil.getUserMessage("config.ipPolicy.any").equals(name)) {
			addActionError(MgrUtil.getUserMessage("error.ipOrMacOrService.nameLimit",
				MgrUtil.getUserMessage("config.ipPolicy.any")));
			return true;
		}
		if (checkNameExists("addressName", name)) {
			return true;
		}
		return false;
	}
	
	private boolean checkGlobalIpExist() {
		for(SingleTableItem single : getDataSource().getItems()){
			if(SingleTableItem.TYPE_GLOBAL == single.getType()) {
				return true;
			}
		}
		addActionError(MgrUtil.getUserMessage("error.config.global.value", "IP Address"));
		return false;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		IpAddress source = QueryUtil.findBoById(IpAddress.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<IpAddress> list = QueryUtil.executeQuery(IpAddress.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (IpAddress profile : list) {
			if (profile.isDefaultFlag()) {
				continue;
			}
			
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			IpAddress up = source.clone();
			if (null == up) {
				continue;
			}
			
			// category must be same
			if (profile.getTypeFlag() != source.getTypeFlag()) {
				addActionError(MgrUtil
					.getUserMessage("error.use.paintbrush.objectIsDifferentType", "category"));
				return null;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setAddressName(profile.getAddressName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			List<SingleTableItem> items = new ArrayList<SingleTableItem>();
			items.addAll(source.getItems());
			up.setItems(items);
			hmBos.add(up);
		}
		return hmBos;
	}

	public int getAddressNameLength() {
		return getAttributeLength("addressName");
	}

	public int getCommentLength() {
		return HmBo.DEFAULT_DESCRIPTION_LENGTH;
	}

	public int getIpAddressLength() {
		return HmBo.DEFAULT_DESCRIPTION_LENGTH;
	}

	public int getNetmaskLength() {
		return 15;
	}

	public EnumItem[] getEnumUseType() {
		return SingleTableItem.ENUM_ADDRESS_USE_TYPE;
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().isDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public String getChangedName() {
		return getDataSource().getAddressName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	protected boolean addSingleIP() throws Exception {
		SingleTableItem oneItem = new SingleTableItem();
		switch (getDataSource().getTypeFlag()) {
			case IpAddress.TYPE_IP_ADDRESS:
				oneItem.setIpAddress(ipAddress);
				oneItem.setNetmask(IpAddress.NETMASK_OF_SINGLE_IP);
				break;
			case IpAddress.TYPE_HOST_NAME:
				oneItem.setIpAddress(hostName);
				break;
			case IpAddress.TYPE_IP_NETWORK:
				oneItem.setIpAddress(ipNetwork);
				oneItem.setNetmask(netmask);
				break;
			case IpAddress.TYPE_IP_WILDCARD:
				oneItem.setIpAddress(ipWildcard);
				oneItem.setNetmask(wildcard);
				break;
			case IpAddress.TYPE_IP_RANGE:
				oneItem.setIpAddress(startIp);
				oneItem.setNetmask(endIp);
				break;
			case IpAddress.TYPE_WEB_PAGE:
				oneItem.setIpAddress(webPage);
				break;
			default:
				break;
		}
		oneItem.setDescription(description);
		oneItem.setType(useType);
		switch (useType) {
		case SingleTableItem.TYPE_MAP:
			if (locationId != null && locationId > -1) {
				MapContainerNode location = findBoById(
						MapContainerNode.class, locationId);
				oneItem.setLocation(location);
			}
			oneItem.setTypeName("");
			oneItem.setTag1("");
			oneItem.setTag2("");
			oneItem.setTag3("");
			break;
		case SingleTableItem.TYPE_HIVEAPNAME:
			oneItem.setLocation(null);
			oneItem.setTypeName(typeName);
			oneItem.setTag1("");
			oneItem.setTag2("");
			oneItem.setTag3("");
			break;
		case SingleTableItem.TYPE_CLASSIFIER:
			oneItem.setLocation(null);
			oneItem.setTypeName("");
			oneItem.setTag1Checked(checkTag1);
			oneItem.setTag1(tag1);
			oneItem.setTag2Checked(checkTag2);
			oneItem.setTag2(tag2);
			oneItem.setTag3Checked(checkTag3);
			oneItem.setTag3(tag3);
			break;
		default:
			oneItem.setLocation(null);
			oneItem.setTypeName("");
			oneItem.setTag1("");
			oneItem.setTag2("");
			oneItem.setTag3("");
			break;
		}
		for (SingleTableItem single : getDataSource().getItems()) {
			if (single.getType() == useType) {
				boolean itemSame = false;
				switch (useType) {
					case SingleTableItem.TYPE_MAP:
						if (oneItem.getLocation().getId().equals(single.getLocation().getId())) {
							itemSame = true;
						}
						break;
					case SingleTableItem.TYPE_HIVEAPNAME:
						if (oneItem.getTypeName().equals(single.getTypeName())) {
							itemSame = true;
						}
						break;
					case SingleTableItem.TYPE_CLASSIFIER:
						if (oneItem.getTag1().equals(single.getTag1())
							&& oneItem.getTag2().equals(single.getTag2())
							&& oneItem.getTag3().equals(single.getTag3())) {
							itemSame = true;
						}
						break;
					default:
						itemSame = true;
						break;
				}
				if (itemSame) {
					hideCreateItem = "";
					hideNewButton = "none";
					return false;
				}
			}
		}
		getDataSource().getItems().add(oneItem);
		return true;
	}

	protected void removeSelectedIPs() {
		if (ruleIndices != null) {
			Collection<SingleTableItem> removeList = new Vector<SingleTableItem>();
			for (String serviceIndex : ruleIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getItems().size()) {
						removeList.add(getDataSource().getItems().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getItems().removeAll(removeList);
		}
	}

	protected void updateIPItems() throws Exception {
		if (null != radioIpOrName) {
			if (radioIpOrName.equals("address")) {
				getDataSource().setTypeFlag(IpAddress.TYPE_IP_ADDRESS);
			} else if (radioIpOrName.equals("name")) {
				getDataSource().setTypeFlag(IpAddress.TYPE_HOST_NAME);
			} else if (radioIpOrName.equals("network")) {
				getDataSource().setTypeFlag(IpAddress.TYPE_IP_NETWORK);
			} else if (radioIpOrName.equals("wildcard")) {
				getDataSource().setTypeFlag(IpAddress.TYPE_IP_WILDCARD);
			} else if(radioIpOrName.equals("webPage")){
				getDataSource().setTypeFlag(IpAddress.TYPE_WEB_PAGE);
			}else {
				getDataSource().setTypeFlag(IpAddress.TYPE_IP_RANGE);
			}
		}
		if (descriptions != null) {
			SingleTableItem oneItem;
			for (int i = 0; i < descriptions.length
					&& i < getDataSource().getItems().size(); i++) {
				oneItem = getDataSource().getItems().get(i);
				oneItem.setDescription(descriptions[i]);
				oneItem.setIpAddress(ipAddresses[i]);
				switch (getDataSource().getTypeFlag()) {
					case IpAddress.TYPE_IP_ADDRESS:
						oneItem.setNetmask(IpAddress.NETMASK_OF_SINGLE_IP);
						break;
					case IpAddress.TYPE_HOST_NAME:
						oneItem.setNetmask("");
						break;
					case IpAddress.TYPE_WEB_PAGE:
						oneItem.setNetmask("");
						break;
					case IpAddress.TYPE_IP_RANGE:
					case IpAddress.TYPE_IP_NETWORK:
					case IpAddress.TYPE_IP_WILDCARD:
						oneItem.setNetmask(netmasks[i]);
						break;
					default:
						break;
				}
			}
		}
	}

	public List<CheckItem> getLocation() {
		List<CheckItem> listLocation = getMapListView();
		if (listLocation.size() == 0) {
			listLocation.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		return listLocation;
	}

	public int getGridCount() {
		return getDataSource().getItems().size() == 0 ? 3 : 0;
	}

	private String hideCreateItem = "none";

	public String getHideCreateItem() {
		return hideCreateItem;
	}

	private String hideNewButton = "";

	public String getHideNewButton() {
		return hideNewButton;
	}

	private String ip; // for simple object create used
	
	private String ipIdString; // for simple object create used
	
	private String ipAddress;
	
	private String ipNetwork;
	
	private String ipWildcard;

	private String netmask;
	
	private String wildcard;

	private String hostName;
	
	private String webPage;

	private short useType;

	private Long locationId;

	private String typeName;
	
	private boolean checkTag1 = true;

	private String tag1;
	
	private boolean checkTag2 = true;

	private String tag2;
	
	private boolean checkTag3 = true;

	private String tag3;

	private String description;

	private String[] ipAddresses;

	private String[] netmasks;

	private String[] descriptions;

	private Collection<String> ruleIndices;
	
	private String startIp;
	
	private String endIp;

	public String getStartIp()
	{
		return startIp;
	}

	public void setStartIp(String startIp)
	{
		this.startIp = startIp;
	}

	public String getEndIp()
	{
		return endIp;
	}

	public void setEndIp(String endIp)
	{
		this.endIp = endIp;
	}

	public void setRuleIndices(Collection<String> ruleIndices) {
		this.ruleIndices = ruleIndices;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUseType(short useType) {
		this.useType = useType;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setTag1(String tag1) {
		this.tag1 = tag1;
	}

	public void setIpAddresses(String[] ipAddresses) {
		this.ipAddresses = ipAddresses;
	}

	public void setTag2(String tag2) {
		this.tag2 = tag2;
	}

	public void setTag3(String tag3) {
		this.tag3 = tag3;
	}

	public void setNetmasks(String[] netmasks) {
		this.netmasks = netmasks;
	}

	public void setDescriptions(String[] descriptions) {
		this.descriptions = descriptions;
	}

    public String getRadioIpOrName()
	{	
    	switch(getDataSource().getTypeFlag()) {
    		case IpAddress.TYPE_IP_ADDRESS:
    			radioIpOrName = "address";
    			break;
    		case IpAddress.TYPE_HOST_NAME:
    			radioIpOrName = "name";
    			break;
    		case IpAddress.TYPE_IP_NETWORK:
    			radioIpOrName = "network";
    			break;
    		case IpAddress.TYPE_IP_WILDCARD:
    			radioIpOrName = "wildcard";
    			break;
    		case IpAddress.TYPE_IP_RANGE:
    			radioIpOrName = "range";
    			break;
    		case IpAddress.TYPE_WEB_PAGE:
    			radioIpOrName = "webPage";
    		default:
    			break;
    	}
		return radioIpOrName;
	}

	public void setRadioIpOrName(String radioIpOrName)
	{
		this.radioIpOrName = radioIpOrName;
	}

	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setIpIdString(String ipIdString) {
		this.ipIdString = ipIdString;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_TYPE = 2;
	
	public static final int COLUMN_COUNT = 3;
	
	public static final int COLUMN_IP_VALUE = 4;
	
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
			code = "config.ipAddress.addressName";
			break;
		case COLUMN_TYPE:
			code = "config.ipAddress.type.title";
			break;
		case COLUMN_COUNT:
			code = "config.ipAddress.ipAddress.count";
			break;
		case COLUMN_IP_VALUE:
			code = "config.ipAddress.ipAddress.type";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(4);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_TYPE));
		columns.add(new HmTableColumn(COLUMN_COUNT));
		columns.add(new HmTableColumn(COLUMN_IP_VALUE));
		return columns;
	}
	
	public void setIpNetwork(String ipNetwork)
	{
		this.ipNetwork = ipNetwork;
	}

	public void setIpWildcard(String ipWildcard)
	{
		this.ipWildcard = ipWildcard;
	}

	public void setWildcard(String wildcard)
	{
		this.wildcard = wildcard;
	}

	public void setCheckTag1(boolean checkTag1)
	{
		this.checkTag1 = checkTag1;
	}

	public void setCheckTag2(boolean checkTag2)
	{
		this.checkTag2 = checkTag2;
	}

	public void setCheckTag3(boolean checkTag3)
	{
		this.checkTag3 = checkTag3;
	}

	public boolean isCheckTag1()
	{
		return checkTag1;
	}

	public boolean isCheckTag2()
	{
		return checkTag2;
	}

	public boolean isCheckTag3()
	{
		return checkTag3;
	}

    @Override
    public Collection<HmBo> load(HmBo bo) {
        if(bo instanceof IpAddress){
            IpAddress ipAddress=(IpAddress)bo;
            if(null != ipAddress.getOwner())
                ipAddress.getOwner().getId();
            if (null != ipAddress.getItems())
                ipAddress.getItems().size();
        }
        return null;
    }
    
    private boolean vpnServiceDnsIpFlag=false;

	public boolean isVpnServiceDnsIpFlag() {
		return vpnServiceDnsIpFlag;
	}

	public void setVpnServiceDnsIpFlag(boolean vpnServiceDnsIpFlag) {
		this.vpnServiceDnsIpFlag = vpnServiceDnsIpFlag;
	}
    
	/**
	 * Bug 22879 fix.
	 * @return
	 */
	public String getHostNameHeaderTitleStyle(){
		return getDataSource().getTypeFlag() == IpAddress.TYPE_HOST_NAME ? "" :"none";
	}
	
	public String getWebPageHeaderTitleStyle(){
		return getDataSource().getTypeFlag() == IpAddress.TYPE_WEB_PAGE ? "" :"none";
	}

	public String getIpEntryHeaderTitleStyle(){
		return (getDataSource().getTypeFlag() != IpAddress.TYPE_HOST_NAME 
				&&  getDataSource().getTypeFlag() != IpAddress.TYPE_WEB_PAGE)? "" :"none";
	}

	public void setWebPage(String webPage) {
		this.webPage = webPage;
	}
}