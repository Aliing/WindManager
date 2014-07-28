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
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.SingleTableItem;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * configuration > advanced configuration > common object > Radius Operator-Name Attribute
 * @author fhu
 *
 */
public class RadiusAttrAction extends BaseAction implements QueryBo{

	private static final long serialVersionUID = 1L;

	private String hideNewButton = "";
	private String hideCreateItem = "none";
	private String[] operatorNames;
	private String[] descriptions;
	private String typeName;
	private String operatorName;
	private String description;
	private short useType;
	private short nameSpaceId;
	private Long locationId;
	private boolean checkTag1 = true;
	private Collection<String> ruleIndices;
	private String tag1;
	private boolean checkTag2 = true;
	private String tag2;
	private boolean checkTag3 = true;
	private String tag3;
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;

//	public static final int COLUMN_TYPE = 2;

	public static final int COLUMN_COUNT = 2;

	public static final int COLUMN_IP_VALUE = 3;


	public String execute() throws Exception{

		String fw = globalForward();
		if (fw != null) {
			return fw;
		}


		try{
			//prepare some fields for jsonMode
			if (isJsonMode() && 
					"continue".equals(operation)) { // user profile
				restoreJsonContext();
			}
			
			if("new".equals(operation)){
				if (!setTitleAndCheckAccess(getText("config.title.radiusAttr.new"))) {
					setUpdateContext(true);
					return getLstForward();
				}

				setSessionDataSource(new RadiusAttrs());
				hideCreateItem = "none";
				hideNewButton = "";

				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(INPUT, "radiusOptNameJson");
			}else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				updateRadiusAttrsItems();
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", true);
				if (isJsonMode()) {
					if (checkNameExists("objectName",getDataSource().getObjectName())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getObjectName()));
						return "json";
					}
					if (!checkGlobalRadiusattrsExist()) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.config.global.value", "RADIUS_OPERATOR_NAME"));
						return "json";
					}
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", ((RadiusAttrs)dataSource).getObjectName());
					try {
						id = createBo(dataSource);
						jsonObject.put("newObjId", id);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					return "json";
				} else {
					if (checkNameExists("objectName", getDataSource().getObjectName())
							|| !checkGlobalRadiusattrsExist()) {
						 return INPUT;
					}
					if ("create".equals(operation)) {
						createBo(dataSource);
						return prepareRadiusAttrsBoList();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			}else if("edit".equals(operation)){
				setSessionDataSource(findBoById(boClass, id, this));
				if (dataSource == null) {
					String rtnStr = prepareRadiusAttrsBoList();
					return getReturnPathWithJsonMode(rtnStr, "radiusOptNameJson");
				} else {
					addLstTitle(getText("config.title.radiusAttr.edit") + " '"
							+ this.getChangedName() + "'");
					

					if (isJsonMode()) {
						storeJsonContext();
					}
					return getReturnPathWithJsonMode(INPUT, "radiusOptNameJson");
				}
			}else if("addOperatorName".equals(operation)){
				if (dataSource == null) {
					String rtnStr = prepareRadiusAttrsBoList();
					return getReturnPathWithJsonMode(rtnStr, "radiusOptNameJson");
				} else {
					updateRadiusAttrsItems();
					if (!addSingleRadiusAttr()) {
						addActionError(MgrUtil
								.getUserMessage("error.addObjectTypeExists"));
					}
					return getReturnPathWithJsonMode(INPUT, "radiusOptNameJson");
				}
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				updateRadiusAttrsItems();
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					if (!checkGlobalRadiusattrsExist()) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.config.global.value", "RADIUS_OPERATOR_NAME"));
						return "json";
					}
					try {
						updateBo(dataSource);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", e.getMessage());
						return "json";
					}
					return "json";
				} else {
					if (!checkGlobalRadiusattrsExist()) {
						return INPUT;
					}
					if ("update".equals(operation)) {
						updateBo(dataSource);
						return prepareRadiusAttrsBoList();
					} else {
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if("clone".equals(operation)){
				long cloneId = getSelectedIds().get(0);
				RadiusAttrs profile = (RadiusAttrs) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setObjectName("");
				profile.setDefaultFlag(false);
				profile.setOwner(null);
				profile.setVersion(null);
				List<SingleTableItem> items = new ArrayList<SingleTableItem>();
				items.addAll(profile.getItems());
				profile.setItems(items);
				setSessionDataSource(profile);
				return getReturnPathWithJsonMode(INPUT, "radiusOptNameJson");
			}else if ("removeOperatorName".equals(operation)
							|| "removeOperatorNameNone".equals(operation)) {
				hideCreateItem = "removeOperatorNameNone".equals(operation) ? ""
					: "none";
				hideNewButton = "removeOperatorNameNone".equals(operation) ? "none"
					: "";
				if (dataSource == null) {
					String rtnStr = prepareRadiusAttrsBoList();
					return getReturnPathWithJsonMode(rtnStr, "radiusOptNameJson");
				} else {
					updateRadiusAttrsItems();
					removeSelectedVlans();
					return getReturnPathWithJsonMode(INPUT, "radiusOptNameJson");
				}
			}else{
				baseOperation();
				String rtnStr = prepareRadiusAttrsBoList();
				return getReturnPathWithJsonMode(rtnStr, "radiusOptNameJson");
			}
		}catch (Exception e){
			reportActionError(e);
			String rtnStr = prepareRadiusAttrsBoList();
			return getReturnPathWithJsonMode(rtnStr, "radiusOptNameJson");
		}

	}
	
	private boolean checkGlobalRadiusattrsExist() {
		for(SingleTableItem single : getDataSource().getItems()){
			if(SingleTableItem.TYPE_GLOBAL == single.getType()) {
				return true;
			}
		}
		addActionError(MgrUtil.getUserMessage("error.config.global.value", "RADIUS_OPERATOR_NAME"));
		return false;
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
	
	protected void updateRadiusAttrsItems() throws Exception {
		if (descriptions != null) {
			SingleTableItem oneItem;
			for (int i = 0; i < descriptions.length
					&& i < getDataSource().getItems().size(); i++) {
				oneItem = getDataSource().getItems().get(i);
				oneItem.setDescription(descriptions[i]);
				oneItem.setOperatorName(operatorNames[i]);
				if(oneItem.getNameSpaceId()==0)
					oneItem.setNameSpaceId(nameSpaceId);
			}
		}
	}

		protected boolean addSingleRadiusAttr() throws Exception
		{
			SingleTableItem oneItem = new SingleTableItem();
			oneItem.setOperatorName(operatorName);
			oneItem.setDescription(description);
			oneItem.setType(useType);
			oneItem.setNameSpaceId(nameSpaceId);
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

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_L3_RADIUSATTR);
		setDataSource(RadiusAttrs.class);
//		enableSorting();
//		// default sorting
//		if (sortParams.getOrderBy() == null) {
//			sortParams.setOrderBy("name");
//		}
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_RADIUS_OPERATOR_NAME_ATTRIBUTE;
	}

	private String prepareRadiusAttrsBoList() throws Exception{
	    String str = prepareBoList();
	    loadLazyData();
	    return str;
	}

	/**
	 * get page data
	 * @return void
	 * @author fhu
	 * @date Dec 6, 2011 1:42:31 PM
	 */
	private void loadLazyData() {
        if (page.isEmpty())
            return;

        Map<Long, RadiusAttrs> radiusAttrsMap = new HashMap<Long, RadiusAttrs>();
		StringBuilder buf = new StringBuilder();
        for (Object element : page) {
        	RadiusAttrs radiusAttrs = (RadiusAttrs) element;
        	radiusAttrsMap.put(radiusAttrs.getId(), radiusAttrs);
            buf.append(radiusAttrs.getId());
            buf.append(",");

            radiusAttrs.setItems(new ArrayList<SingleTableItem>());
        }
        buf.deleteCharAt(buf.length()-1);

        String sql = "select a.id,b.radius_attribute_id,b.operatorName,b.description,b.type,b.namespaceid"
                + " from radius_operator_attribute a "
                + " inner join radius_attribute_item b on b.radius_attribute_id = a.id "
                + " where a.id in(" + buf.toString() + ")";

        List<?> templates = QueryUtil.executeNativeQuery(sql);

        for (Object obj : templates) {
            Object[] template = (Object[]) obj;
            Long id = Long.valueOf(template[0].toString());

            RadiusAttrs templateElement = radiusAttrsMap.get(id);
            if (templateElement != null) {
                if (StringUtils.isNotBlank(template[1].toString())) {
                    SingleTableItem tempClass = new SingleTableItem();
                    tempClass.setOperatorName((String) template[2]);
                    tempClass.setDescription((String) template[3]);
                    tempClass.setType(Short.parseShort(template[4].toString()));
                    tempClass.setNameSpaceId(Short.parseShort(template[5].toString()));
                    templateElement.getItems().add(tempClass);
                }
            }
        }
    }


	@Override
	public Collection<HmBo> load(HmBo bo) {
		 if(bo instanceof RadiusAttrs){
			 RadiusAttrs radiusAttrs=(RadiusAttrs)bo;
	            if(null != radiusAttrs.getOwner())
	            	radiusAttrs.getOwner().getId();
	            if (null != radiusAttrs.getItems())
	            	radiusAttrs.getItems().size();
	        }
		return null;
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().isDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(4);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_COUNT));
		columns.add(new HmTableColumn(COLUMN_IP_VALUE));
		return columns;
	}
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
			code = "config.radius.objectName";
			break;
		case COLUMN_COUNT:
			code = "config.radius.count";
			break;
		case COLUMN_IP_VALUE:
			code = "config.radius.entry";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		RadiusAttrs source = QueryUtil.findBoById(RadiusAttrs.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<RadiusAttrs> list = QueryUtil.executeQuery(RadiusAttrs.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (RadiusAttrs profile : list) {
			if (profile.isDefaultFlag()) {
				continue;
			}
			
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			RadiusAttrs up = source.clone();
			if (null == up) {
				continue;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setObjectName(profile.getObjectName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			List<SingleTableItem> items = new ArrayList<SingleTableItem>();
			items.addAll(source.getItems());
			up.setItems(items);
			hmBos.add(up);
		}
		return hmBos;
	}
	
	@Override
	public RadiusAttrs clone() {
		try {
			return (RadiusAttrs) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public RadiusAttrs getDataSource() {
		return (RadiusAttrs) dataSource;
	}

	public int getObjectNameLength() {
		return getAttributeLength("objectName");
	}

	public int getNetmaskLength() {
		return 15;
	}

	public int getOperatorNameLength() {
		return HmBo.DEFAULT_DESCRIPTION_LENGTH;
	}

	public int getCommentLength() {
		return HmBo.DEFAULT_DESCRIPTION_LENGTH;
	}

	public int getGridCount() {
		return getDataSource().getItems().size() == 0 ? 3 : 0;
	}

	public EnumItem[] getEnumRadiusAttrType(){
		return SingleTableItem.ENUM_RADIUS_USE_TYPE;
	}

	public EnumItem[] getEnumUseType() {
		return SingleTableItem.ENUM_ADDRESS_USE_TYPE;
	}


	public List<CheckItem> getLocation() {
		List<CheckItem> listLocation = getMapListView();
		if (listLocation.size() == 0) {
			listLocation.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		return listLocation;
	}
	
	public String getChangedName() {
		return getDataSource().getObjectName().replace("\\", "\\\\").replace("'",
				"\\'");
	}

	
	public boolean isCheckTag1() {
		return checkTag1;
	}

	public void setCheckTag1(boolean checkTag1) {
		this.checkTag1 = checkTag1;
	}

	public void setTag1(String tag1) {
		this.tag1 = tag1;
	}

	public boolean isCheckTag2() {
		return checkTag2;
	}

	public void setCheckTag2(boolean checkTag2) {
		this.checkTag2 = checkTag2;
	}

	public void setTag2(String tag2) {
		this.tag2 = tag2;
	}

	public boolean isCheckTag3() {
		return checkTag3;
	}

	public void setCheckTag3(boolean checkTag3) {
		this.checkTag3 = checkTag3;
	}

	public void setTag3(String tag3) {
		this.tag3 = tag3;
	}



	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public void setUseType(short useType) {
		this.useType = useType;
	}

	public void setNameSpaceId(short nameSpaceId) {
		this.nameSpaceId = nameSpaceId;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setDescriptions(String[] descriptions) {
		this.descriptions = descriptions;
	}

	public String getHideCreateItem() {
		return hideCreateItem;
	}
	public String getHideNewButton() {
		return hideNewButton;
	}

	public void setOperatorNames(String[] operatorNames) {
		this.operatorNames = operatorNames;
	}

	public void setRuleIndices(Collection<String> ruleIndices) {
		this.ruleIndices = ruleIndices;
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
}
