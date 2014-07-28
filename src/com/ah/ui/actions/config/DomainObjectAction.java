/**
 *@filename		DomainObjectAction.java
 *@version
 *@author		Fiona
 *@createtime	2011-2-25 AM 10:40:14
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
import org.json.JSONObject;


import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.DomainNameItem;
import com.ah.bo.network.DomainObject;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class DomainObjectAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.domainObject"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new DomainObject());
				hideCreateItem = "";
				hideNewButton = "none";
				setObjTypeSelcted();
				return getReturnPathWithJsonMode(INPUT,"domainObjectOnly");
			} else if ("create".equals(operation)
					|| ("create" + getLstForward()).equals(operation)) {
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					if (!updateListDatas(true)) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", getErrorMsgStr());
						return "json";
					} else if (checkProfileName(getDataSource().getObjName())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getObjName()));
						return "json";
					}
				} else {
					if (!updateListDatas(true) || checkProfileName(getDataSource().getObjName())) {
						return INPUT;
					}
				}
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedName", getDataSource().getObjName());
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
						id = createBo(dataSource);
						return preparePageBoList();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				setObjTypeSelcted();
				if (dataSource == null) {
					return preparePageBoList();
				} else {
					addLstTitle(getText("config.title.domainObject.edit") + " '"
							+ getChangedName() + "'");
					return getReturnPathWithJsonMode(INPUT,"domainObjectOnly");
				}
			} else if ("update".equals(operation)
					|| ("update" + getLstForward()).equals(operation)) {
				if (dataSource != null) {
					if (!updateListDatas(true)) {
						return getReturnPathWithJsonMode(INPUT,"domainObjectOnly");
					}
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
						return preparePageBoList();
					} else {
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				DomainObject profile = (DomainObject) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setObjName("");
				profile.setOwner(null);
				profile.setVersion(null);
				List<DomainNameItem> items = new ArrayList<DomainNameItem>();
				items.addAll(profile.getItems());
				profile.setItems(items);
				setSessionDataSource(profile);
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("addDomName".equals(operation)) {
				if (dataSource == null) {
					return preparePageBoList();
				} else {
					updateListDatas(false);
					addDomainName();
					return getReturnPathWithJsonMode(INPUT,"domainObjectOnly");
				}
			} else if ("removeDomName".equals(operation)
					|| "removeDomNameNone".equals(operation)) {
				hideCreateItem = "removeDomNameNone".equals(operation) ? ""
						: "none";
				hideNewButton = "removeDomNameNone".equals(operation) ? "none"
						: "";
				if (dataSource == null) {
					return preparePageBoList();
				} else {
					updateListDatas(false);
					removeSelectedBos();
					return getReturnPathWithJsonMode(INPUT,"domainObjectOnly");
				}
			} else {
				baseOperation();
				return preparePageBoList();
			}
		} catch (Exception e) {
			reportActionError(e);
			return preparePageBoList();
		}
	}

	private String preparePageBoList() throws Exception {
		filterParams = new FilterParams("autoGenerateFlag", false);
		String str = prepareBoList();
		loadLazyData();
		return str;
	}

	private void loadLazyData() {

		if (page.isEmpty())
			return;

		Map<Long, DomainObject> domObjectMap = new HashMap<Long, DomainObject>();
		StringBuffer buf = new StringBuffer();
		for (Object element : page) {
			DomainObject domObject = (DomainObject) element;
			domObjectMap.put(domObject.getId(), domObject);
			buf.append(domObject.getId());
			buf.append(",");

			domObject.setItems(new ArrayList<DomainNameItem>());
		}
		buf.deleteCharAt(buf.length() - 1);

		String sql = "select a.id,b.domain_object_id,b.domainname"
				+ " from domain_object a "
				+ " inner join domain_name_item b on b.domain_object_id = a.id "
				+ " where a.id in(" + buf.toString() + ")";

		List<?> templates = QueryUtil.executeNativeQuery(sql);

		for (Object obj : templates) {
			Object[] template = (Object[]) obj;
			Long id = Long.valueOf(template[0].toString());

			DomainObject templateElment = domObjectMap.get(id);
			if (templateElment != null) {
				if (StringUtils.isNotBlank(template[1].toString())) {
					DomainNameItem tempClass = new DomainNameItem();
					tempClass.setDomainName(template[2].toString());
					templateElment.getItems().add(tempClass);
				}
			}
		}
	}

	protected boolean updateListDatas(boolean ifCheck) {
		if (descriptions != null) {
			DomainNameItem nameI;
			for (int i = 0; i < descriptions.length; i++) {
				nameI = getDataSource().getItems().get(i);
				nameI.setDomainName(domNames[i]);
				nameI.setDescription(descriptions[i]);
				
				if (ifCheck && checkKeywordExist(domNames[i]) && getDataSource().getItems().size() > 1) {
					addActionError(MgrUtil.getUserMessage("error.config.network.os.object", domNames[i]));
					setErrorMsgStr(MgrUtil.getUserMessage("error.config.network.os.object", domNames[i]));
					return false;
				}
			}
		}
		if (ifCheck) {
			return checkObjectItemList();
		}
		return true;
	}
	
	private boolean checkObjectItemList() {
		DomainNameItem domainI;
		DomainNameItem domainJ;
		for (int i = 0; i < getDataSource().getItems().size()-1; i++) {
			domainI = getDataSource().getItems().get(i);
			for (int j = i+1; j < getDataSource().getItems().size(); j++) {
				domainJ = getDataSource().getItems().get(j);
				if (domainI.getDomainName().equalsIgnoreCase(domainJ.getDomainName())) {
					addActionError(MgrUtil.getUserMessage("error.sameObjectExists", "Domain Name"));
					setErrorMsgStr(MgrUtil.getUserMessage("error.sameObjectExists", "Domain Name"));
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean checkKeywordExist(String keyword) {
		for (int i = 1; i < 3; i ++) {
			if (keyword.equalsIgnoreCase(MgrUtil.getEnumString("enum.config.security.device.detection.domain.keywords."+i))) {
				return true;
			}
		}
		return false;
	}

	private boolean checkProfileName(String name) {
		return checkNameExists("objName", name);
	}

	protected boolean addDomainName() throws Exception {
		for (DomainNameItem nameObj : getDataSource().getItems()) {
			if (nameObj.getDomainName().equals(domName)) {
				addActionError(MgrUtil.getUserMessage("error.addObjectExists"));
				hideCreateItem = "";
				hideNewButton = "none";
				return false;
			}
		}
		DomainNameItem oneItem = new DomainNameItem();
		oneItem.setDomainName(domName);
		oneItem.setDescription(description);
		getDataSource().getItems().add(oneItem);
		domName = "";
		description = "";
		return true;
	}

	protected void removeSelectedBos() {
		if (ruleIndices != null) {
			Collection<DomainNameItem> removeList = new Vector<DomainNameItem>();
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

	private String domName;

	private String description;

	private String[] domNames;

	private String[] descriptions;

	private Collection<String> ruleIndices;

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDomName(String domName)
	{
		this.domName = domName;
	}

	public void setDomNames(String[] domNames)
	{
		this.domNames = domNames;
	}

	public void setDescriptions(String[] descriptions) {
		this.descriptions = descriptions;
	}

	public void setRuleIndices(Collection<String> ruleIndices) {
		this.ruleIndices = ruleIndices;
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_DOMAIN_OBJECT);
		setDataSource(DomainObject.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_DOMAIN_OBJECT;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof DomainObject) {
			DomainObject domObject = (DomainObject) bo;
			if (null != domObject.getItems())
				domObject.getItems().size();
		}
		return null;
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		DomainObject source = QueryUtil.findBoById(DomainObject.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<DomainObject> list = QueryUtil.executeQuery(DomainObject.class, null,
				new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (DomainObject profile : list) {

			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			DomainObject up = source.clone();
			if (null == up) {
				continue;
			}

			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setObjName(profile.getObjName());
			up.setOwner(profile.getOwner());
			List<DomainNameItem> items = new ArrayList<DomainNameItem>();
			items.addAll(source.getItems());
			up.setItems(items);
			hmBos.add(up);
		}
		return hmBos;
	}

	public DomainObject getDataSource() {
		return (DomainObject) dataSource;
	}

	public String getChangedName() {
		return getDataSource().getObjName().replace("\\", "\\\\")
				.replace("'", "\\'");
	}

	public int getNameLength() {
		return getAttributeLength("objName");
	}

	public int getCommentLength() {
		return HmBo.DEFAULT_DESCRIPTION_LENGTH;
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

	// ID of table columns in list view
	public static final int COLUMN_NAME = 1;

	public static final int COLUMN_NAME_LIST = 2;
	
	public static final int COLUMN_TYPE = 3;

	/**
	 * get the description of column by id
	 * 
	 * @param id
	 *            -
	 * @return String
	 */
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.osObject.name";
			break;
		case COLUMN_NAME_LIST:
			code = "config.domainObject.nameList";
			break;
		case COLUMN_TYPE:
			code = "config.domainObject.type";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_NAME_LIST));
		columns.add(new HmTableColumn(COLUMN_TYPE));
		return columns;
	}

	protected String returnResultKeyWord(String normalkey, String expressKey){
		if(isJsonMode()) {
			return expressKey;
		} else {
			return normalkey;
		}
	}
	
	private String errorMsgStr = "";

	public String getErrorMsgStr() {
		return errorMsgStr;
	}

	public void setErrorMsgStr(String errorMsgStr) {
		this.errorMsgStr = errorMsgStr;
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			if (HmDomain.GLOBAL_DOMAIN.equals(getDataSource().getOwner().getDomainName())) {
				return "disabled";
			} else {
				return "";
			}
		}
		return "disabled";
	}

	public String getDomName() {
		return domName;
	}

	public String getDescription() {
		return description;
	}
	
	public EnumItem[] getEnumDomainObjectType(){
		return DomainObject.ENUM_DOMAIN_OBJECT_TYPE;
	}
	
	private short objType = -1;
	public boolean objTypeDisabled = false;
	
	private void setObjTypeSelcted(){
		switch (objType) {
		case 1:
			this.getDataSource().setObjType(DomainObject.CLASSIFICATION_POLICY);
			objTypeDisabled = true;
			break;
		case 2:
			this.getDataSource().setObjType(DomainObject.VPN_TUNNEL);
			objTypeDisabled = true;
			break;
		case 3:
			this.getDataSource().setObjType(DomainObject.WEB_SECURITY);
			objTypeDisabled = true;
			break;
		default:
			break;
		}
	}

	public short getObjType() {
		return objType;
	}

	public void setObjType(short objType) {
		this.objType = objType;
	}
	
	public boolean isObjTypeDisabled() {
		return objTypeDisabled;
	}

	public void setObjTypeDisabled(boolean objTypeDisabled) {
		this.objTypeDisabled = objTypeDisabled;
	}

}
