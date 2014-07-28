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
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.SingleTableItem;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.MgrUtil;

public class MacAddressAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private String radioMacOrOui;
	
	private short unSupportType;
	
	private static final String MAC_ADDRESS_OBJECT_LOCATION_FLAG = "MAC_ADDRESS_OBJECT_LOCATION_FLAG";

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if (null == operation || L2_FEATURE_MAC_OR_OUI.equals(operation) || "new".equals(operation)
					 || "edit".equals(operation)) {
				MgrUtil.setSessionAttribute(MAC_ADDRESS_OBJECT_LOCATION_FLAG, isJsonMode());
			} else if (null != MgrUtil.getSessionAttribute(MAC_ADDRESS_OBJECT_LOCATION_FLAG)) {
				setJsonMode((Boolean)MgrUtil.getSessionAttribute(MAC_ADDRESS_OBJECT_LOCATION_FLAG));
			}
			
			this.macTypeSupport = new MacTypeSupport(this.macTypeSupported);
			switch(unSupportType){
				case MacOrOui.TYPE_MAC_OUI:
					macTypeSupport.setOui(false);
					break;
				case MacOrOui.TYPE_MAC_RANGE:
					macTypeSupport.setRange(false);
					break;
				case MacOrOui.TYPE_MAC_ADDRESS:
					macTypeSupport.setAddress(false);
					break;
			}
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.macOui"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new MacOrOui());
				hideCreateItem = "none";
				hideNewButton = "";
				if ("oui".equals(radioMacOrOui)) {
					getDataSource().setTypeFlag(MacOrOui.TYPE_MAC_OUI);
				} else if ("macRange".equals(radioMacOrOui)) {
					getDataSource().setTypeFlag(MacOrOui.TYPE_MAC_RANGE);
				}
				return isJsonMode() ? "macAddressDlg" : INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				updateMacItems();
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					if (checkMacName(getDataSource().getMacOrOuiName())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg",MgrUtil.getUserMessage("error.ipOrMacOrService.nameLimit",
								getDataSource().getMacOrOuiName()));
						return "json";
					} else if (!checkGlobalMacExist()) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.config.global.value", getDataSource().getTypeFlag() == MacOrOui.TYPE_MAC_ADDRESS ?
								"MAC Address" : "MAC OUI"));
						return "json";
					}
				} else {
					if (checkMacName(getDataSource().getMacOrOuiName()) || !checkGlobalMacExist()) {
						return getReturnPathWithJsonMode(INPUT, "macAddressDlg");
					}
				}
				
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedName", getDataSource().getMacOrOuiName());
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
						createBo(dataSource);
						return prepareMacAddressBoList();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				if (dataSource == null) {
					return prepareMacAddressBoList();
				} else {
					addLstTitle(getText("config.title.macOui.edit") + " '"
							+ getChangedName() + "'");
					return isJsonMode() ? "macAddressDlg" : INPUT;
				}
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource == null || dataSource.getId() == null || !dataSource.getId().equals(id)) {
					throw new HmException(
							"Update object failed, session must have been shared by another browser window.",
							HmMessageCodes.STALE_SESSION_OBJECT,
							new String[] { "Update" });
				}
				if (dataSource != null) {
					updateMacItems();
					if (!checkGlobalMacExist()) {
						return isJsonMode()? "macAddressDlg" : INPUT;
					}
					/*id = dataSource.getId();*/
				}
				if (isJsonMode() && !isParentIframeOpenFlg()) {
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
				} else {
					if ("update".equals(operation)) {
						updateBo(dataSource);
						return prepareMacAddressBoList();
					} else {
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				MacOrOui profile = (MacOrOui) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setMacOrOuiName("");
				profile.setDefaultFlag(false);
				profile.setOwner(null);
				profile.setVersion(null);
				List<SingleTableItem> items = new ArrayList<SingleTableItem>();
				items.addAll(profile.getItems());
				profile.setItems(items);
				setSessionDataSource(profile);
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("addMacAddress".equals(operation)) {
				if (dataSource == null) {
					return prepareMacAddressBoList();
				} else {
					updateMacItems();
					if (!addSingleMac()) {
						addActionError(MgrUtil
								.getUserMessage("error.addObjectTypeExists"));
					}
					return isJsonMode() ? "macAddressDlg" : INPUT;
				}
			} else if ("removeMacAddress".equals(operation)
					|| "removeMacAddressNone".equals(operation)) {
				hideCreateItem = "removeMacAddressNone".equals(operation) ? ""
						: "none";
				hideNewButton = "removeMacAddressNone".equals(operation) ? "none"
						: "";
				if (dataSource == null) {
					return prepareMacAddressBoList();
				} else {
					updateMacItems();
					removeSelectedMacs();
					return isJsonMode() ? "macAddressDlg" : INPUT;
				}
			} else if("createSimpleMac".equals(operation)){
				jsonObject = createSimpleMac();
				return "json";
			} else if("removeSimpleMac".equals(operation)){
				jsonObject = removeSimpleMac();
				return "json";
			} else {
				baseOperation();
				return prepareMacAddressBoList();
			}
		} catch (Exception e) {
			if (isJsonMode()) {
				addActionError(MgrUtil.getUserMessage(e));
				generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
						+ " " + MgrUtil.getUserMessage(e));
				return  "macAddressDlg";
			}
			reportActionError(e);
			return prepareMacAddressBoList();
		}
	}

	private String prepareMacAddressBoList() throws Exception{
		String str = prepareBoList();
		loadLazyData();
		return str;
	}
	
	private void loadLazyData() {

		if (page.isEmpty())
			return;

		Map<Long, MacOrOui> macOrOuiMap = new HashMap<Long, MacOrOui>();
		StringBuffer buf = new StringBuffer();
		for (Object element : page) {
			MacOrOui macOrOui = (MacOrOui) element;
			macOrOuiMap.put(macOrOui.getId(), macOrOui);
			buf.append(macOrOui.getId());
			buf.append(",");

			macOrOui.setItems(new ArrayList<SingleTableItem>());
		}
		buf.deleteCharAt(buf.length() - 1);

		String sql = "select a.id,b.mac_or_oui_id,b.macEntry,b.type,b.macrangefrom,b.macrangeto"
				+ " from mac_or_oui a "
				+ " inner join mac_or_oui_item b on b.mac_or_oui_id = a.id "
				+ " where a.id in(" + buf.toString() + ")";

		List<?> templates = QueryUtil.executeNativeQuery(sql);

		for (Object obj : templates) {
			Object[] template = (Object[]) obj;
			Long id = Long.valueOf(template[0].toString());

			MacOrOui templateElment = macOrOuiMap.get(id);
			if (templateElment != null) {
				if (StringUtils.isNotBlank(template[1].toString())) {
					SingleTableItem tempClass = new SingleTableItem();
					if (null != template[2]) {
						tempClass.setMacEntry(template[2].toString());
					}
					tempClass.setType(Short.parseShort(template[3].toString()));
					if (template[4] != null && template[5] != null) {
						tempClass.setMacRangeFrom(template[4].toString());
						tempClass.setMacRangeTo(template[5].toString());
					}
					templateElment.getItems().add(tempClass);
				}
			}
		}
	}

	private JSONObject removeSimpleMac() throws JSONException {
		List<Long> ids = new ArrayList<Long>();
		if(null != macIdString){
			String[] array = macIdString.split(",");
			for (String s : array) {
				long id = Long.parseLong(s);
				ids.add(id);
			}
		}
		return CreateObjectAuto.removeSimpleMac(ids);
	}

	private JSONObject createSimpleMac() throws JSONException {
		return CreateObjectAuto.createSimpleMac(mac, getDomain());
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MAC_OR_OUI);
		setDataSource(MacOrOui.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_MAC;
		
		// avoid cancel back error when open two edit view 
		if(L2_FEATURE_MAC_OR_OUI.equals(request.getParameter("operation"))
		        || "create".equals(request.getParameter("operation")) 
		        || ("create" + getLstForward()).equals(request.getParameter("operation"))
		        || "update".equals(request.getParameter("operation")) 
		        || ("update"+ getLstForward()).equals(request.getParameter("operation"))) {
		    if(null == getDataSource()) {
		        setSessionDataSource(new MacOrOui());
		    }
		}
	}

	public MacOrOui getDataSource() {
		return (MacOrOui) dataSource;
	}

	private boolean checkMacName(String name) {
		if (getDataSource().getTypeFlag() == MacOrOui.TYPE_MAC_ADDRESS &&
			MgrUtil.getUserMessage("config.ipPolicy.any").equals(name)) {
			addActionError(MgrUtil.getUserMessage("error.ipOrMacOrService.nameLimit",
				MgrUtil.getUserMessage("config.ipPolicy.any")));
			return true;
		}
		if (checkNameExists("macOrOuiName", name)) {
			return true;
		}
		return false;
	}
	
	private boolean checkGlobalMacExist() {
		if (getDataSource().getTypeFlag() == MacOrOui.TYPE_MAC_RANGE) {
			return true;
		}
		for(SingleTableItem single : getDataSource().getItems()){
			if(SingleTableItem.TYPE_GLOBAL == single.getType()) {
				return true;
			}
		}
		addActionError(MgrUtil.getUserMessage("error.config.global.value", getDataSource().getTypeFlag() == MacOrOui.TYPE_MAC_ADDRESS ?
			"MAC Address" : "MAC OUI"));
		return false;
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public String getChangedName() {
		return getDataSource().getMacOrOuiName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public int getAddressNameLength() {
		return getAttributeLength("macOrOuiName");
	}

	public int getCommentLength() {
		return HmBo.DEFAULT_DESCRIPTION_LENGTH;
	}

	public EnumItem[] getEnumUseType() {
		return SingleTableItem.ENUM_ADDRESS_USE_TYPE;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		MacOrOui source = QueryUtil.findBoById(MacOrOui.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<MacOrOui> list = QueryUtil.executeQuery(MacOrOui.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (MacOrOui profile : list) {
			if (profile.getDefaultFlag()) {
				continue;
			}
			
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			MacOrOui up = source.clone();
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
			up.setMacOrOuiName(profile.getMacOrOuiName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			List<SingleTableItem> items = new ArrayList<SingleTableItem>();
			items.addAll(source.getItems());
			up.setItems(items);
			hmBos.add(up);
		}
		return hmBos;
	}

	protected boolean addSingleMac() throws Exception {
		SingleTableItem oneItem = new SingleTableItem();
		if (getDataSource().getTypeFlag() == MacOrOui.TYPE_MAC_ADDRESS) {
			oneItem.setMacEntry(macAddress);
		} else if (getDataSource().getTypeFlag() == MacOrOui.TYPE_MAC_OUI) {
			oneItem.setMacEntry(macOui);
		}
		oneItem.setDescription(description);
		if (getDataSource().getTypeFlag() == MacOrOui.TYPE_MAC_ADDRESS ||
				getDataSource().getTypeFlag() == MacOrOui.TYPE_MAC_OUI) {
			// address or MAC OUI
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
						case SingleTableItem.TYPE_GLOBAL:
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
		} else {
			oneItem.setLocation(null);
			oneItem.setTypeName("");
			oneItem.setTag1("");
			oneItem.setTag2("");
			oneItem.setTag3("");
//			if (null == macRangeFrom || "".equals(macRangeFrom)) {
//				oneItem.setMacRangeFrom(MacOrOui.MAC_RANGE_FROM);
//			} else {
//				oneItem.setMacRangeFrom(macRangeFrom);
//			}
			oneItem.setMacRangeFrom(macRangeFrom);
//			if (null == macRangeTo || "".equals(macRangeTo)) {
//				oneItem.setMacRangeTo(MacOrOui.MAC_RANGE_TO);
//			} else {
//				oneItem.setMacRangeTo(macRangeTo);
//			}
			oneItem.setMacRangeTo(macRangeTo);
			oneItem.setType(SingleTableItem.TYPE_NONE);
			for (SingleTableItem single : getDataSource().getItems()) {
				if (single.getType() == SingleTableItem.TYPE_NONE) {
					if (oneItem.getMacRangeFrom().equals(single.getMacRangeFrom())
							&& oneItem.getMacRangeTo().equals(single.getMacRangeTo())) {
						hideCreateItem = "";
						hideNewButton = "none";
						return false;
					}
				}
			}
		}
		
		getDataSource().getItems().add(oneItem);
		return true;
	}

	protected void removeSelectedMacs() {
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

	protected void updateMacItems() throws Exception {
		if (null != radioMacOrOui) {
			if (radioMacOrOui.equals("address")) {
				getDataSource().setTypeFlag(MacOrOui.TYPE_MAC_ADDRESS);
			} else if (radioMacOrOui.equals("oui")) {
				getDataSource().setTypeFlag(MacOrOui.TYPE_MAC_OUI);
			} else {
				getDataSource().setTypeFlag(MacOrOui.TYPE_MAC_RANGE);
			}
		}
		if (descriptions != null) {
			SingleTableItem oneItem;
			for (int i = 0; i < descriptions.length
					&& i < getDataSource().getItems().size(); i++) {
				oneItem = getDataSource().getItems().get(i);
				oneItem.setDescription(descriptions[i]);
				if (MacOrOui.TYPE_MAC_RANGE != getDataSource().getTypeFlag() && null != macEntries) {
					oneItem.setMacEntry(macEntries[i]);
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

	public String getRadioMacOrOui() {
		switch (getDataSource().getTypeFlag()) {
		case MacOrOui.TYPE_MAC_ADDRESS:
			radioMacOrOui = "address";
			break;
		case MacOrOui.TYPE_MAC_OUI:
			radioMacOrOui = "oui";
			break;
		case MacOrOui.TYPE_MAC_RANGE:
			radioMacOrOui = "macRange";
			break;
		default:
			radioMacOrOui = "address";
		}
		return radioMacOrOui;
	}

	public void setRadioMacOrOui(String radioMacOrOui) {
		this.radioMacOrOui = radioMacOrOui;
	}

	public String getShowMacAddress() {
		return getDataSource().getTypeFlag() == MacOrOui.TYPE_MAC_ADDRESS ? ""
				: "none";
	}

	public String getShowMacOui() {
		return getDataSource().getTypeFlag() == MacOrOui.TYPE_MAC_OUI ? ""
				: "none";
	}

	public String getShowAddressOrOui() {
		return getDataSource().getTypeFlag() == MacOrOui.TYPE_MAC_ADDRESS
		|| getDataSource().getTypeFlag() == MacOrOui.TYPE_MAC_OUI ? ""
		: "none";
	}

	public String getShowMacRange() {
		return getDataSource().getTypeFlag() == MacOrOui.TYPE_MAC_RANGE ? ""
				: "none";
	}

	private String mac; // for simple object create used
	
	private String macIdString; // for simple object remove used
	
	private String macAddress;

	private String macOui;

	private short useType;

	private Long locationId;

	private String typeName;

	private String macRangeFrom;

	private String macRangeTo;

	private boolean checkTag1 = true;

	private String tag1;
	
	private boolean checkTag2 = true;

	private String tag2;
	
	private boolean checkTag3 = true;

	private String tag3;

	private String description;

	private String[] macEntries;

	private String[] descriptions;

	private Collection<String> ruleIndices;

	public void setMac(String mac) {
		this.mac = mac;
	}

	public void setMacIdString(String macIdString) {
		this.macIdString = macIdString;
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

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setTag1(String tag1) {
		this.tag1 = tag1;
	}

	public void setTag2(String tag2) {
		this.tag2 = tag2;
	}

	public void setTag3(String tag3) {
		this.tag3 = tag3;
	}

	public void setMacEntries(String[] macEntries) {
		this.macEntries = macEntries;
	}

	public void setDescriptions(String[] descriptions) {
		this.descriptions = descriptions;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public void setMacOui(String macOui) {
		this.macOui = macOui;
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

	public void setMacRangeFrom(String macRangeFrom) {
		this.macRangeFrom = macRangeFrom;
	}

	public void setMacRangeTo(String macRangeTo) {
		this.macRangeTo = macRangeTo;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_TYPE = 2;
	
	public static final int COLUMN_COUNT = 3;
	
	public static final int COLUMN_MAC_VALUE = 4;
	
	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return String
	 */
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.macOrOui.name";
			break;
		case COLUMN_TYPE:
			code = "config.ipAddress.type.title";
			break;
		case COLUMN_COUNT:
			code = "config.ipAddress.ipAddress.count";
			break;
		case COLUMN_MAC_VALUE:
			code = "config.macOrOui.macEntry.type";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_TYPE));
		columns.add(new HmTableColumn(COLUMN_COUNT));
		columns.add(new HmTableColumn(COLUMN_MAC_VALUE));
		return columns;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof MacOrOui) {
			MacOrOui macOrOui = (MacOrOui) bo;
			if (null != macOrOui.getItems())
				macOrOui.getItems().size();
		}
		return null;
	}
	
	private String macType;

	public String getMacType() {
		return macType;
	}

	public void setMacType(String macType) {
		this.macType = macType;
	}

	private static final short MAC_TYPE_SUPPORT_ADDRESS = 01;
	private static final short MAC_TYPE_SUPPORT_RANGE = 02;
	private static final short MAC_TYPE_SUPPORT_OUI = 04;
	private short macTypeSupported = 0;	
	class MacTypeSupport {
		boolean address = true;
		boolean range = true;
		boolean oui = true;
		
		public boolean isAddress() {
			return address;
		}

		public void setAddress(boolean address) {
			this.address = address;
		}

		public boolean isRange() {
			return range;
		}

		public void setRange(boolean range) {
			this.range = range;
		}

		public boolean isOui() {
			return oui;
		}

		public void setOui(boolean oui) {
			this.oui = oui;
		}

		MacTypeSupport(short macTypeSupported) {
			if (macTypeSupported <= 0) {
				return;
			}
			this.address = (MAC_TYPE_SUPPORT_ADDRESS & macTypeSupported) > 0;
			this.range = (MAC_TYPE_SUPPORT_RANGE & macTypeSupported) > 0;
			this.oui = (MAC_TYPE_SUPPORT_OUI & macTypeSupported) > 0;
		}
	}
	private MacTypeSupport macTypeSupport;
	
	public short getMacTypeSupported() {
		return macTypeSupported;
	}

	public void setMacTypeSupported(short macTypeSupported) {
		this.macTypeSupported = macTypeSupported;
	}

	public MacTypeSupport getMacTypeSupport() {
		return macTypeSupport;
	}

	public void setMacTypeSupport(MacTypeSupport macTypeSupport) {
		this.macTypeSupport = macTypeSupport;
	}

	public short getUnSupportType() {
		return unSupportType;
	}

	public void setUnSupportType(short unSupportType) {
		this.unSupportType = unSupportType;
	}
	
}