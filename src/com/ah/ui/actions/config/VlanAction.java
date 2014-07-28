/**
 *@filename		VlanAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-21 PM 07:52:10
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
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
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.Vlan;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class VlanAction extends BaseAction implements QueryBo{

	private static final long serialVersionUID = 1L;
	
	private static final String VLAN_DIALOG_MODE = "vlanDlg";
    private static final String VLAN_JSON_MODE = "vlanJson";

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
            if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.vlan"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new Vlan());
				hideCreateItem = "none";
				hideNewButton = "";
				return getReturnPathWithJsonMode(INPUT, VLAN_JSON_MODE, VLAN_DIALOG_MODE);
			} else if ("create".equals(operation) 
			        || ("create" + getLstForward()).equals(operation)) {
				updateVlanItems();
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", true);
				if(checkNameExists("vlanName", getDataSource().getVlanName())) {
				    if(isJsonMode() && isContentShownInSubDrawer()) {
				        return VLAN_JSON_MODE;
				    } else if(isJsonMode() && !isParentIframeOpenFlg()) {
				        jsonObject.put("resultStatus", false);
				        jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getVlanName()));
				        return "json";
				    } else {
				        return getReturnPathWithJsonMode(INPUT, VLAN_DIALOG_MODE);
				    }
				}
				if(!checkGlobalVlanExist()) {
				    if(isJsonMode() && isContentShownInSubDrawer()) {
				        return VLAN_JSON_MODE;
				    } else if(isJsonMode() && !isParentIframeOpenFlg()) {
                        jsonObject.put("resultStatus", false);
                        jsonObject.put("errMsg", MgrUtil.getUserMessage("error.config.global.value", "VLAN"));
				        return "json";
				    } else {
				        return getReturnPathWithJsonMode(INPUT, VLAN_DIALOG_MODE);
				    }
				}
				if(isJsonMode() && isContentShownInSubDrawer()) {
				    if ("create".equals(operation)) {
				        try {
                            id = createBo(dataSource);
                            jsonObject.put("t", true);
                            jsonObject.put("newState", true);
                            jsonObject.put("lanId", selectedLANId);
                            return "json";
                        } catch (Exception e) {
                            addActionError(e.getMessage());
                            return VLAN_JSON_MODE;
                        }
				    }
				    return "json";
				} else if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedName", ((Vlan)dataSource).getVlanName());
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
						return prepareVlanBoList();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				if (dataSource == null) {
					String rtnStr = prepareVlanBoList();
					return getReturnPathWithJsonMode(rtnStr, VLAN_JSON_MODE, VLAN_DIALOG_MODE);
				} else {
					addLstTitle(getText("config.title.vlan.edit") + " '"
							+ getChangedName() + "'");
					return getReturnPathWithJsonMode(INPUT, VLAN_JSON_MODE, VLAN_DIALOG_MODE);
				}
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", true);
                if (dataSource != null) {
                    updateVlanItems();
                    if(getDataSource().getDefaultFlag()) {
                    	addActionError(MgrUtil.getUserMessage("error.use.paintbrush.default.item"));
    				    if(isJsonMode() && isContentShownInSubDrawer()) {
    				        return VLAN_JSON_MODE;
    				    } else if(isJsonMode() && !isParentIframeOpenFlg()) {
    				        jsonObject.put("resultStatus", false);
    				        jsonObject.put("errMsg", MgrUtil.getUserMessage("error.use.paintbrush.default.item"));
    				        return "json";
    				    } else {
    				        return getReturnPathWithJsonMode(INPUT, VLAN_DIALOG_MODE);
    				    }
    				}
                    
                    if (!checkGlobalVlanExist()) {
                        if (isJsonMode() && isContentShownInSubDrawer()) {
                            return VLAN_JSON_MODE;
                        } else if (isJsonMode() && !isParentIframeOpenFlg()) {
                            jsonObject.put("resultStatus", false);
                            jsonObject.put("errMsg",
                                    MgrUtil.getUserMessage("error.config.global.value", "VLAN"));
                            return "json";
                        } else {
                            return getReturnPathWithJsonMode(INPUT, VLAN_DIALOG_MODE);
                        }
                    }
                }
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					try {
						updateBo(dataSource);
						jsonObject.put("t", true);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", e.getMessage());
						return "json";
					}
					return "json";
				} else {
					if ("update".equals(operation)) {
						updateBo(dataSource);
						return prepareVlanBoList();
					} else {
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				Vlan profile = (Vlan) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setVlanName("");
				profile.setDefaultFlag(false);
				profile.setOwner(null);
				profile.setVersion(null);
				List<SingleTableItem> items = new ArrayList<SingleTableItem>();
				items.addAll(profile.getItems());
				profile.setItems(items);
				setSessionDataSource(profile);
				return getReturnPathWithJsonMode(INPUT, VLAN_JSON_MODE, VLAN_DIALOG_MODE);
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("addVlan".equals(operation)) {
				if (dataSource == null) {
					String rtnStr = prepareVlanBoList();
					return getReturnPathWithJsonMode(rtnStr, VLAN_JSON_MODE, VLAN_DIALOG_MODE);
				} else {
					updateVlanItems();
					if (!addSingleVlan()) {
						addActionError(MgrUtil
								.getUserMessage("error.addObjectTypeExists"));
					}
					return getReturnPathWithJsonMode(INPUT, VLAN_JSON_MODE, VLAN_DIALOG_MODE);
				}
			} else if ("removeVlan".equals(operation)
							|| "removeVlanNone".equals(operation)) {
				hideCreateItem = "removeVlanNone".equals(operation) ? ""
					: "none";
				hideNewButton = "removeVlanNone".equals(operation) ? "none"
					: "";
				if (dataSource == null) {
					String rtnStr = prepareVlanBoList();
					return getReturnPathWithJsonMode(rtnStr, VLAN_JSON_MODE, VLAN_DIALOG_MODE);
				} else {
					updateVlanItems();
					removeSelectedVlans();
					return getReturnPathWithJsonMode(INPUT, VLAN_JSON_MODE, VLAN_DIALOG_MODE);
				}
			} else if("createSimpleVlan".equals(operation)){
				jsonObject = createSimpleVlan();
				return "json";
			} else if("removeSimpleVlan".equals(operation)){
				jsonObject = removeSimpleVlan();
				return "json";
			} else {
				if (isJsonMode()) {
					prepareVlanBoList();
					return VLAN_DIALOG_MODE;
				}
				baseOperation();
				return prepareVlanBoList();
			}
		} catch (Exception e) {
			reportActionError(e);
			return getReturnPathWithJsonMode(prepareVlanBoList(), VLAN_JSON_MODE, VLAN_DIALOG_MODE);
		}
	}
	
	private String prepareVlanBoList() throws Exception{
		String str = prepareBoList();
		loadLazyData();
		return str;
	}
	
	private void loadLazyData() {

		if (page.isEmpty())
			return;

		Map<Long, Vlan> vlanMap = new HashMap<Long, Vlan>();
		StringBuffer buf = new StringBuffer();
		for (Object element : page) {
			Vlan vlan = (Vlan) element;
			vlanMap.put(vlan.getId(), vlan);
			buf.append(vlan.getId());
			buf.append(",");

			vlan.setItems(new ArrayList<SingleTableItem>());
		}
		buf.deleteCharAt(buf.length() - 1);

		String sql = "select a.id,b.vlanid,b.type,b.description"
				+ " from vlan a "
				+ " inner join vlan_item b on b.vlan_id = a.id "
				+ " where a.id in(" + buf.toString() + ")";

		List<?> templates = QueryUtil.executeNativeQuery(sql);

		for (Object obj : templates) {
			Object[] template = (Object[]) obj;
			Long id = Long.valueOf(template[0].toString());

			Vlan templateElment = vlanMap.get(id);
			if (templateElment != null) {
				if (StringUtils.isNotBlank(template[1].toString())) {
					SingleTableItem tempClass = new SingleTableItem();
					tempClass.setVlanId(Integer.parseInt(template[1].toString()));
					tempClass.setType(Short.parseShort(template[2].toString()));
					tempClass.setDescription(template[3].toString());
					templateElment.getItems().add(tempClass);
				}
			}
		}
	}

	private JSONObject removeSimpleVlan() throws JSONException {
		List<Long> ids = new ArrayList<Long>();
		if(null != vlanIdString){
			String[] array = vlanIdString.split(",");
			for (String s : array) {
				long id = Long.parseLong(s);
				ids.add(id);
			}
		}
		return CreateObjectAuto.removeSimpleVlan(ids);
	}

	private JSONObject createSimpleVlan() throws JSONException {
		return CreateObjectAuto.createSimpleVlan(getVlan(), getDomain());
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_VLAN);
		setDataSource(Vlan.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_VLAN;
	}

	public Vlan getDataSource()
	{
		this.setDataSource(boClass);
		return (Vlan) dataSource;
	}

	public int getVlanNameLength() {
		return getAttributeLength("vlanName");
	}

	public int getCommentLength() {
		return HmBo.DEFAULT_DESCRIPTION_LENGTH;
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public String getChangedName() {
		return getDataSource().getVlanName().replace("\\", "\\\\").replace("'",
				"\\'");
	}

	public List<CheckItem> getLocation() {
		List<CheckItem> listLocation = getMapListView();
		if (listLocation.size() == 0) {
			listLocation.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		return listLocation;
	}

	public EnumItem[] getEnumUseType() {
		return SingleTableItem.ENUM_ADDRESS_USE_TYPE;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		Vlan source = QueryUtil.findBoById(Vlan.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<Vlan> list = QueryUtil.executeQuery(Vlan.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (Vlan profile : list) {
			if (profile.getDefaultFlag()) {
				continue;
			}
			
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			Vlan up = source.clone();
			if (null == up) {
				continue;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setVlanName(profile.getVlanName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			List<SingleTableItem> items = new ArrayList<SingleTableItem>();
			items.addAll(source.getItems());
			up.setItems(items);
			hmBos.add(up);
		}
		return hmBos;
	}

	protected boolean addSingleVlan() throws Exception
	{
		SingleTableItem oneItem = new SingleTableItem();
		oneItem.setVlanId(vlanId);
		oneItem.setDescription(description);
		oneItem.setType(useType);
		switch (useType)
		{
			case SingleTableItem.TYPE_MAP:
				if (locationId != null && locationId > -1)
				{
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

	protected void removeSelectedVlans() {
		if (ruleIndices != null) {
			Collection<SingleTableItem> removeList = new Vector<SingleTableItem>();
			for (String serviceIndex : ruleIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getItems().size()) {
						removeList
								.add(getDataSource().getItems().get(index));
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

	protected void updateVlanItems() throws Exception {
		if (descriptions != null) {
			SingleTableItem oneItem;
			for (int i = 0; i < descriptions.length
					&& i < getDataSource().getItems().size(); i++) {
				oneItem = getDataSource().getItems().get(i);
				oneItem.setDescription(descriptions[i]);
				oneItem.setVlanId(vlanIds[i]);
			}
		}
	}

	private boolean checkGlobalVlanExist() {
		for(SingleTableItem single : getDataSource().getItems()){
			if(SingleTableItem.TYPE_GLOBAL == single.getType()) {
				return true;
			}
		}
		addActionError(MgrUtil.getUserMessage("error.config.global.value", "VLAN"));
		return false;
	}

	public int getGridCount() {
		return getDataSource().getItems().size() == 0 ? 3 : 0;
	}

	private String hideCreateItem = "none";

	public String getHideCreateItem()
	{
		return hideCreateItem;
	}

	private String hideNewButton = "";

	public String getHideNewButton()
	{
		return hideNewButton;
	}

	private int vlan; // for simple object create used
	
	private String vlanIdString; // for simple object remove used
	
	private int vlanId;

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

	private int[] vlanIds;

	private String[] descriptions;

	private Collection<String> ruleIndices;
	

	

	public void setRuleIndices(Collection<String> ruleIndices)
	{
		this.ruleIndices = ruleIndices;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setUseType(short useType)
	{
		this.useType = useType;
	}

	public void setLocationId(Long locationId)
	{
		this.locationId = locationId;
	}

	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	public void setTag1(String tag1)
	{
		this.tag1 = tag1;
	}

	public void setTag2(String tag2)
	{
		this.tag2 = tag2;
	}

	public void setTag3(String tag3)
	{
		this.tag3 = tag3;
	}

	public void setDescriptions(String[] descriptions)
	{
		this.descriptions = descriptions;
	}

	public void setVlanId(int vlanId)
	{
		this.vlanId = vlanId;
	}

	public void setVlanIds(int[] vlanIds)
	{
		this.vlanIds = vlanIds;
	}
	
	public void setVlan(int vlan) {
		this.vlan = vlan;
	}

	public int getVlan() {
		return vlan;
	}

	public void setVlanIdString(String vlanIdString) {
		this.vlanIdString = vlanIdString;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_COUNT = 2;
	
	public static final int COLUMN_VLAN_VALUE = 3;
	
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
			code = "config.vlan.vlanName";
			break;
		case COLUMN_COUNT:
			code = "config.ipAddress.ipAddress.count";
			break;
		case COLUMN_VLAN_VALUE:
			code = "config.vlan.vlanId.type";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_COUNT));
		columns.add(new HmTableColumn(COLUMN_VLAN_VALUE));
		return columns;
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
		if (bo instanceof Vlan) {
			Vlan vlan = (Vlan) bo;
			if(null != vlan.getItems())
				vlan.getItems().size();
		}
		return null;
	}
	
	// selected LAN id(use for Networks Dialog)
    private Long selectedLANId;

    public Long getSelectedLANId() {
        return selectedLANId;
    }

    public void setSelectedLANId(Long selectedLANId) {
        this.selectedLANId = selectedLANId;
    }
    
}