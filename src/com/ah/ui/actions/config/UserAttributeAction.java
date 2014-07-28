/**
 *@filename		UserAttributeAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-26 PM 07:35:29
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

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class UserAttributeAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.attribute"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new UserProfileAttribute());
				hideCreateItem = "none";
				hideNewButton = "";
				return getReturnPathWithJsonMode(INPUT, "userAttributeJson");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				updateAttributeItems();
				if (checkNameExists("attributeName", getDataSource().getAttributeName())) {
					if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getAttributeName()));
						return "json";
					} else {
						return getReturnPathWithJsonMode(INPUT, "userAttributeJson");
					}
				}
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedName", getDataSource().getAttributeName());
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
						String retString =  prepareBoList();
						loadPageLazyData();
						return retString;
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				if (dataSource == null) {
					String retStr= prepareBoList();
					loadPageLazyData();
					return getReturnPathWithJsonMode(retStr, "userAttributeJson");
				} else {
					addLstTitle(getText("config.title.attribute.edit") + " '"
							+ this.getChangedName() + "'");
					return getReturnPathWithJsonMode(INPUT, "userAttributeJson");
				}
			} else if ("update".equals(operation) || ("update" + getLstForward()).equals(operation)) {
				if (dataSource != null) {
					updateAttributeItems();
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
						String retString =  prepareBoList();
						loadPageLazyData();
						return retString;
					} else {
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				UserProfileAttribute profile = (UserProfileAttribute) findBoById(
						boClass, cloneId, this);
				profile.setId(null);
				profile.setAttributeName("");
				profile.setOwner(null);
				profile.setVersion(null);
				List<SingleTableItem> items = new ArrayList<>();
				items.addAll(profile.getItems());
				profile.setItems(items);
				setSessionDataSource(profile);
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("addAttribute".equals(operation)) {
				if (dataSource == null) {
					String retStr= prepareBoList();
					loadPageLazyData();
					return getReturnPathWithJsonMode(retStr, "userAttributeJson");
				} else {
					updateAttributeItems();
					if (!addSingleAttribute()) {
						addActionError(MgrUtil
								.getUserMessage("error.addObjectTypeExists"));
					}
					return getReturnPathWithJsonMode(INPUT, "userAttributeJson");
				}
			} else if ("removeAttribute".equals(operation)
							|| "removeAttributeNone".equals(operation)) {
				hideCreateItem = "removeAttributeNone".equals(operation) ? ""
					: "none";
				hideNewButton = "removeAttributeNone".equals(operation) ? "none"
					: "";
				if (dataSource == null) {
					String retStr= prepareBoList();
					loadPageLazyData();
					return getReturnPathWithJsonMode(retStr, "userAttributeJson");
				} else {
					updateAttributeItems();
					removeSelectedAttributes();
					return getReturnPathWithJsonMode(INPUT, "userAttributeJson");
				}
			} else {
				baseOperation();
				String retStr= prepareBoList();
				loadPageLazyData();
				return retStr;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_USER_PROFILE_ATTRIBUTE);
		setDataSource(UserProfileAttribute.class);
		keyColumnId = COLUMN_NAME;
		tableId = HmTableColumn.TABLE_CONFIGURATION_AUTH_USER_ATTRIBUTE;
	}
	
	public void loadPageLazyData(){
		if (page.isEmpty()){
			return;
		}
		Map<Long, UserProfileAttribute> configMap = new HashMap<>();
		String whereCon = "";
		int i=0;
		// Replace the 'LAZY' template with an empty one
		for (Object objectItem: page){
			UserProfileAttribute oneConfig =(UserProfileAttribute)objectItem;
			configMap.put(oneConfig.getId(), oneConfig);
			whereCon = whereCon + oneConfig.getId();
			i++;
			if (page.size()!=i){
				whereCon = whereCon + ",";
			}
			
			oneConfig.setItems(new ArrayList<SingleTableItem>());
		}
		// Query for the template names only
		String strSql="select a.id,b.attribute_id,b.attributeValue,b.type" +
				" from user_profile_attribute a " +
				" inner join attribute_item b on  b.attribute_id = a.id  " +
				" where a.id in(" + whereCon + ")";
		
		List<?> templates = QueryUtil.executeNativeQuery(strSql);
		// Fill in the template names
		for (Object obj : templates) {
			Object[] template = (Object[]) obj;
			Long id = Long.valueOf(template[0].toString());

			UserProfileAttribute templateSave = configMap.get(id);
			if (templateSave != null) {
				if (template[1]!=null && !template[1].toString().trim().equals("")){
					SingleTableItem tempClass = new SingleTableItem();
					tempClass.setAttributeValue((String) template[2]);
					tempClass.setType(Short.parseShort(template[3].toString()));
					templateSave.getItems().add(tempClass);
				}
			}
		}
	}

	@Override
	public UserProfileAttribute getDataSource()
	{
		return (UserProfileAttribute) dataSource;
	}

	public int getNameLength() {
		return getAttributeLength("attributeName");
	}

	public int getCommentLength() {
		return HmBo.DEFAULT_DESCRIPTION_LENGTH;
	}

	public String getChangedName() {
		return getDataSource().getAttributeName().replace("\\", "\\\\")
				.replace("'", "\\'");
	}

	public List<CheckItem> getLocation() {
		List<CheckItem> listLocation = getMapListView();
		if (listLocation.isEmpty()) {
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
		UserProfileAttribute source = QueryUtil.findBoById(UserProfileAttribute.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<UserProfileAttribute> list = QueryUtil.executeQuery(UserProfileAttribute.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<>(list.size());
		for (UserProfileAttribute profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			UserProfileAttribute up = source.clone();
			if (null == up) {
				continue;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setAttributeName(profile.getAttributeName());
			up.setOwner(profile.getOwner());
			List<SingleTableItem> items = new ArrayList<>();
			items.addAll(source.getItems());
			up.setItems(items);
			hmBos.add(up);
		}
		return hmBos;
	}

	protected boolean addSingleAttribute() throws Exception
	{
		SingleTableItem oneItem = new SingleTableItem();
		oneItem.setAttributeValue(attributeValue);
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

	protected void removeSelectedAttributes() {
		if (ruleIndices != null) {
			Collection<SingleTableItem> removeList = new Vector<>();
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

	protected void updateAttributeItems() throws Exception {
		if (descriptions != null) {
			SingleTableItem oneItem;
			for (int i = 0; i < descriptions.length
					&& i < getDataSource().getItems().size(); i++) {
				oneItem = getDataSource().getItems().get(i);
				oneItem.setDescription(descriptions[i]);
				oneItem.setAttributeValue(attributeValues[i]);
			}
		}
	}

	public int getGridCount() {
		return getDataSource().getItems().isEmpty() ? 3 : 0;
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

	private String attributeValue;

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

	private String[] attributeValues;

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

	public void setAttributeValue(String attributeValue)
	{
		this.attributeValue = attributeValue;
	}

	public void setAttributeValues(String[] attributeValues)
	{
		this.attributeValues = attributeValues;
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_COUNT = 2;
	
	public static final int COLUMN_ATTRIBUTE_VALUE = 3;
	
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
			code = "config.userAttribute.attributeName";
			break;
		case COLUMN_COUNT:
			code = "config.ipAddress.ipAddress.count";
			break;
		case COLUMN_ATTRIBUTE_VALUE:
			code = "config.userAttribute.attributeValue.type";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<>(3);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_COUNT));
		columns.add(new HmTableColumn(COLUMN_ATTRIBUTE_VALUE));
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
		if (bo instanceof UserProfileAttribute) {
			dataSource = bo;
			if (getDataSource().getItems() != null)
				getDataSource().getItems().size();
		}
		return null;
	}

}