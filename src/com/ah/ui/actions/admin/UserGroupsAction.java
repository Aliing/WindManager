package com.ah.ui.actions.admin;

/*
 * @author Chris Scheers
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.rest.client.models.UserModel;
import com.ah.be.rest.client.services.UserService;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapNode;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.Navigation;
import com.ah.util.HibernateUtil;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.xml.navigation.XmlNavigationNode;

public class UserGroupsAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer	log					= new Tracer(UserGroupsAction.class.getSimpleName());

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.userGroup"))) {
					return getLstForward();
				}
				setSessionDataSource(new HmUserGroup());
				prepareGroupPermissions();
				return INPUT;
			} else if ("create".equals(operation)) {
				updateGroupPermissions();
				if (checkNameExists("lower(groupName)", getDataSource()
						.getGroupName().toLowerCase())
						|| (getIsInHomeDomain() && !isAttributeUnique())) {
					prepareGroupPermissions();
					return INPUT;
				}
				
				// check group attribute unique
				if (getShowAttribute()
						&& !getDisabledGroupAttribute()
						&& BoMgmt.getDomainMgmt().checkGroupAttributeExist(getDataSource().getGroupAttribute(), null, null)) {
					prepareGroupPermissions();
					addActionError(getText("error.config.userGroup.groupIdExisted"));
					return INPUT;
				}

				// create this user group in MyHive first
				if (NmsUtil.isHostedHMApplication() && null != userContext.getCustomerId() && !getIsInHomeDomain()) {
					UserService myhive = new UserService();
					UserModel result = myhive.syncUserGroup(userContext.getCustomerId(), getDomain().getVhmID(), getDataSource().getGroupName(), (short)0);
					if (null == result || result.getReturnCode() > 0) {
						addActionError(null == result?getText("bringIntoManagedListerror.unknown", "the result is null from MyHive"):result.getMessage());
						return INPUT;
					}
				}
				createBo(dataSource);
				
				return prepareUserGroupList();
			} else if ("edit".equals(operation)) {
				forward = editBo(this);
				if (dataSource != null) {
					prepareGroupPermissions();
				}
				
				return forward;
			} else if ("update".equals(operation)) {
				updateGroupPermissions();
				if (getIsInHomeDomain() && !isAttributeUnique()) {
					prepareGroupPermissions();
					return INPUT;
				}
				
				// check group attribute unique
				if (getShowAttribute()
						&& !getDisabledGroupAttribute()
						&& BoMgmt.getDomainMgmt().checkGroupAttributeExist(getDataSource().getGroupAttribute(), null, getDataSource().getGroupName())) {
					prepareGroupPermissions();
					addActionError(getText("error.config.userGroup.groupIdExisted"));
					return INPUT;
				}

				updateBo(dataSource);
				return prepareUserGroupList();
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				HmUserGroup clone = (HmUserGroup) findBoById(boClass, cloneId, this);
				clone.setId(null);
				clone.setGroupName("");
				clone.setDefaultFlag(false);
				clone.setFeaturePermissions(new HashMap<String, HmPermission>(
						clone.getFeaturePermissions()));
				clone.setInstancePermissions(new HashMap<Long, HmPermission>(
						clone.getInstancePermissions()));
				clone.setOwner(null);
				setSessionDataSource(clone);
				prepareGroupPermissions();
				return INPUT;
			} else {
				baseOperation();
				return prepareUserGroupList();
			}
		} catch (Exception e) {
			log.error("prepareActionError", MgrUtil.getUserMessage(e), e);
			addActionError(MgrUtil.getUserMessage(e));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
					+ " " + MgrUtil.getUserMessage(e));
			try {
				return prepareUserGroupList();
			} catch (Exception ne) {
				return prepareEmptyBoList();
			}
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_ADMIN_GROUPS);
		setDataSource(HmUserGroup.class);
		keyColumnId = COLUMN_USERGROUPNAME;
		tableId = HmTableColumn.TABLE_ADMINGROUP;
	}

	public HmUserGroup getDataSource() {
		return (HmUserGroup) dataSource;
	}
	
	private String prepareUserGroupList() throws Exception {
		List<Object> lstCondition = new ArrayList<Object>();
		String searchSQL = "";
		
		HmDomain domain = QueryUtil.findBoById(HmDomain.class, getDomainId());
		if (!(domain.isSupportGM() && HmBeLicenseUtil.GM_LITE_LICENSE_VALID)) {
			searchSQL = "groupName!=:s1  and groupName!=:s2";
			lstCondition.add(HmUserGroup.GM_ADMIN);
			lstCondition.add(HmUserGroup.GM_OPERATOR);
		}
		
		HMServicesSettings settings = QueryUtil.findBoByAttribute(HMServicesSettings.class,
				"owner", domain);
		if (!(settings.isEnableTeacher() && NmsUtil.TEACHER_VIEW_GLOBAL_ENABLED)) {
			if (lstCondition.size() > 0) {
				searchSQL += " and ";
			}
			
			searchSQL += "groupName!=:s" + (lstCondition.size() + 1);
			lstCondition.add(HmUserGroup.TEACHER);
		}
		
		if (lstCondition.size() > 0) {
			filterParams = new FilterParams(searchSQL, lstCondition.toArray());
		}
		
		String str = prepareBoList();
		// count the domain user default user group(with defaultFlag=true)
		FilterParams countFilter;
		if (lstCondition.isEmpty()){
			countFilter = new FilterParams("defaultFlag",true);
		}else{
			searchSQL +=" and defaultFlag=:s" + (lstCondition.size() + 1);
			lstCondition.add(true);
			countFilter = new FilterParams(searchSQL, lstCondition.toArray());
		}
		List<HmUserGroup> superUserGroupList = QueryUtil.executeQuery(HmUserGroup.class, null, 
				countFilter, getDomainId());
		if (null == superUserGroupList)
			superUserGroupCount = 0;
		else
			superUserGroupCount = superUserGroupList.size();
		// 
		return str;
	}
	
	/**
	 * For the default user groups are unavailable to edit, recalculate all the available rows
	 */
	@Override
	public long getAvailableRowCount() {
		return paging.getAvailableRowCount() - superUserGroupCount;
	}
	
	/**
	 * For the default user groups are unavailable to edit, recalculate current page available rows.
	 * And It is necessary to recalculate available rows if hint the title to sort the page for any attribute.
	 */
	@Override
	public int getAvailablePageRowCount() {
		int availableCount = 0;
		for (Object element : getPage()) {
			HmUserGroup userGroup = (HmUserGroup) element;
			if(userGroup.getOwner().getId() == getDomainId() && !userGroup.getDefaultFlag()) {
				availableCount++;
			}
		}
		return availableCount;
	}
	
	/**
	 * the default user group count
	 */
	public static int superUserGroupCount = 0;
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_USERGROUPNAME = 1;

	public static final int COLUMN_ATTRIBUTE = 2;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 */
	public String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_USERGROUPNAME:
			code = "config.userGroup.groupName";
			break;
		case COLUMN_ATTRIBUTE:
			code = "admin.usergroup.attribute";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_USERGROUPNAME));
		if (getShowAttribute()) {
			columns.add(new HmTableColumn(COLUMN_ATTRIBUTE));
		}

		return columns;
	}

	/*
	 * Prepare feature and map permissions for user group.
	 */
	protected void prepareGroupPermissions() {
		HmDomain hmDomain;
		if (getDataSource().getOwner() == null) {
			if (getUserContext().getSwitchDomain() != null) {
				hmDomain = getUserContext().getSwitchDomain();
			} else {
				hmDomain = getUserContext().getDomain();
			}
		} else {
			hmDomain = getDataSource().getOwner();
		}

		worldMapId = BoMgmt.getMapMgmt().getWorldMapId(hmDomain.getId());
		featurePermissions = new ArrayList<GridNode>();
		
		if (getDataSource().isGMUserGroup()) {
			// create feature permissions tree for gm user group
			createGMFeaturePermissions(getXmlNavigationTree().getTree(), 0);
		} else if (getDataSource().isTcUserGroup()) {
			// create feature permissions tree for teacher view user group
			createTcFeaturePermissions(getXmlNavigationTree().getTree(), 0);
		} else if (getDataSource().isPlUserGroup()) {
			// create feature permissions tree for rf planner user group
			createPlFeaturePermissions(getXmlNavigationTree().getTree(), 0);
		} else {
			// create feature permissions tree for hm user group
			if (hmDomain.getDomainName().equalsIgnoreCase(HmDomain.HOME_DOMAIN)) {
				createFeaturePermissions(getXmlNavigationTree().getTree(), 0, false);
			} else {
				createFeaturePermissions(getXmlNavigationTree().getTree(), 0, true);
			}
		}

		if (featurePermissions.size() > 0) {
			getChildCount(featurePermissions, 0, -1);
			setParentAccess(featurePermissions, null, 0);
			addTreeIndentation(featurePermissions, 0, new ArrayList<Boolean>());
		}
		for(GridNode node : featurePermissions){
			if(getMapFeatureKey().equals(node.getKey())){
				if(node.getWriteAccess()){
					mapInstanceWriteDisabled = "";
					mapInstanceReadDisabled = "";
				}else if(node.getReadAccess()){
					mapInstanceReadDisabled = "";
				}
				break;
			}
		}
		mapPermissions = new ArrayList<GridNode>();
		mapsDepth = 0;

		Set<MapNode> worldMapNodes = new HashSet<MapNode>();
		for (MapNode mapNode : BoMgmt.getMapMgmt().getRootMap().getChildNodes()) {
			if (mapNode.getId().equals(worldMapId)) {
				worldMapNodes.add(mapNode);
			}
		}
		createMapPermissions(worldMapNodes, 0);

		if (mapPermissions.size() > 0) {
			getChildCount(mapPermissions, 0, -1);
			addTreeIndentation(mapPermissions, 0, new ArrayList<Boolean>());
		}
		
		// refresh feature description for Reboot App of VHM
		if (!hmDomain.isHomeDomain()) {
			for (GridNode node : featurePermissions) {
				if (node.getKey().equals(Navigation.L2_FEATURE_REBOOTAPP)) {
					node.setNodeName(Navigation.FEATURENAME_REVERTVERSION);
					break;
				}
			}
		}
	}

	protected List<GridNode> featurePermissions;

	public List<GridNode> getFeaturePermissions() {
		return featurePermissions;
	} 

	protected void createFeaturePermissions(XmlNavigationNode xmlNode,
			int level, boolean inVhm) {
		GridNode lastNode = null, previousNode = null;
		for (XmlNavigationNode xmlChildNode : xmlNode.getNode()) {

			if (xmlChildNode.getKey() != null) {
				// filter gml features from feature tree.
				if (xmlChildNode.getKey().equals(Navigation.L1_FEATURE_USER_MGR)
						|| xmlChildNode.getKey().equals(Navigation.L1_FEATURE_USER_REPORTS)) {
					continue;
				}
				
				// filter some hmol features
				if (xmlChildNode.getKey().equals(L2_FEATURE_SUMMARYUSAGE)
						|| xmlChildNode.getKey().equals(L2_FEATURE_DETAILUSAGE)
						|| xmlChildNode.getKey().equals(L2_FEATURE_PORTAL_SETTINGS)) {
					if (!NmsUtil.isHostedHMApplication()) {
						continue;
					}
				}
			}
			
			// filter all features that belong to home domain only
			if (inVhm && xmlChildNode.isHomeOnly() != null
					&& xmlChildNode.isHomeOnly()) {
				continue;
			}
			
			// filter SSID  display related SSID for different mode
			if(xmlChildNode.getKey().equals(L2_FEATURE_SSID_PROFILES) && isFullMode()){
				continue;
			}
			if(xmlChildNode.getKey().equals(L2_FEATURE_SSID_PROFILES_FULL) && isEasyMode()){
				continue;
			}
			
			// synchronize with navigation tree, hide them
			if(xmlChildNode.getKey().equals(L2_FEATURE_CID_CLIENTS) 
					|| xmlChildNode.getKey().equals(L2_FEATURE_ENROLLED_CLIENTS)
					|| xmlChildNode.getKey().equals(L2_FEATURE_ONBOARD_UI_SETTING)){
				continue;
			}
			
			GridNode gridNode = new GridNode(xmlChildNode.getKey());
			lastNode = gridNode;
			gridNode.setNodeName(xmlChildNode.getDescription());
			gridNode.setLevel(level);
			gridNode.setTreeImage(TREE_GRID_T);
			HmPermission permission = getDataSource().getFeaturePermissions()
					.get(gridNode.getKey());
			if (permission != null) {
				if (permission.hasAccess(HmPermission.OPERATION_READ)) {
					gridNode.setReadAccess(true);
				}
				if (permission.hasAccess(HmPermission.OPERATION_WRITE)) {
					gridNode.setWriteAccess(true);
				}
				// for new user group
			} else {
				String nodeKey = gridNode.getKey();

				// special for user password modify feature
				if (Navigation.L2_FEATURE_USER_PASSWORD_MODIFY.equals(nodeKey)) {
					gridNode.setReadAccess(true);
					gridNode.setWriteAccess(true);

					// special for date time and license management feature
				} else if (Navigation.L2_FEATURE_LICENSEMGR.equals(nodeKey)) {
					gridNode.setReadAccess(true);
				}
			}

			if (previousNode != null) {
				previousNode.setNextSibling(featurePermissions.size());
			}
			featurePermissions.add(gridNode);
			previousNode = gridNode;
			createFeaturePermissions(xmlChildNode, level + 1, inVhm);
		}
		if (lastNode != null) {
			lastNode.setTreeImage(TREE_GRID_L);
		}
	}
	
	boolean isGMFeature = false;
	protected void createGMFeaturePermissions(XmlNavigationNode xmlNode,
			int level) {
		GridNode lastNode = null, previousNode = null;
		for (XmlNavigationNode xmlChildNode : xmlNode.getNode()) {
			// 
			if (xmlNode.getKey() == null) {
				isGMFeature = xmlChildNode.getKey().equals(Navigation.L1_FEATURE_USER_MGR)
						|| xmlChildNode.getKey().equals(Navigation.L1_FEATURE_USER_REPORTS);
			}
				
			if (!isGMFeature) {
				continue;
			}
			
			GridNode gridNode = new GridNode(xmlChildNode.getKey());
			lastNode = gridNode;
			gridNode.setNodeName(xmlChildNode.getDescription());
			gridNode.setLevel(level);
			gridNode.setTreeImage(TREE_GRID_T);
			HmPermission permission = getDataSource().getFeaturePermissions()
					.get(gridNode.getKey());
			if (permission != null) {
				if (permission.hasAccess(HmPermission.OPERATION_READ)) {
					gridNode.setReadAccess(true);
				}
				if (permission.hasAccess(HmPermission.OPERATION_WRITE)) {
					gridNode.setWriteAccess(true);
				}
			}

			if (previousNode != null) {
				previousNode.setNextSibling(featurePermissions.size());
			}
			featurePermissions.add(gridNode);
			previousNode = gridNode;
			createGMFeaturePermissions(xmlChildNode, level + 1);
		}
		if (lastNode != null) {
			lastNode.setTreeImage(TREE_GRID_L);
		}
	}
	
	protected void createTcFeaturePermissions(XmlNavigationNode xmlNode,
		int level) {
		GridNode lastNode = null, previousNode = null;
		for (XmlNavigationNode xmlChildNode : xmlNode.getNode()) {
	
			if (xmlNode.getKey() == null) {
				if (!xmlChildNode.getKey().equals(Navigation.L1_FEATURE_HOME)) {
					continue;
				}
			} else if (!xmlChildNode.getKey().equals(Navigation.L2_FEATURE_USER_PASSWORD_MODIFY)) {
				continue;
			}
			
			GridNode gridNode = new GridNode(xmlChildNode.getKey());
			lastNode = gridNode;
			gridNode.setNodeName(xmlChildNode.getDescription());
			gridNode.setLevel(level);
			gridNode.setTreeImage(TREE_GRID_T);
			gridNode.setReadAccess(true);
			gridNode.setWriteAccess(true);
			
			if (previousNode != null) {
				previousNode.setNextSibling(featurePermissions.size());
			}
			featurePermissions.add(gridNode);
			previousNode = gridNode;
			createTcFeaturePermissions(xmlChildNode, level + 1);
		}
		if (lastNode != null) {
			lastNode.setTreeImage(TREE_GRID_L);
		}
	}
	
	protected void createPlFeaturePermissions(XmlNavigationNode xmlNode,
		int level) {
		GridNode lastNode = null, previousNode = null;
		for (XmlNavigationNode xmlChildNode : xmlNode.getNode()) {
	
			if (xmlNode.getKey() == null) {
				if (!xmlChildNode.getKey().equals(Navigation.L1_FEATURE_TOPOLOGY)) {
					continue;
				}
			} else if (!xmlChildNode.getKey().equals(Navigation.L2_FEATURE_MAP_VIEW)) {
				continue;
			}
			
			GridNode gridNode = new GridNode(xmlChildNode.getKey());
			lastNode = gridNode;
			gridNode.setNodeName(xmlChildNode.getDescription());
			gridNode.setLevel(level);
			gridNode.setTreeImage(TREE_GRID_T);
			gridNode.setReadAccess(true);
			gridNode.setWriteAccess(true);
			
			if (previousNode != null) {
				previousNode.setNextSibling(featurePermissions.size());
			}
			featurePermissions.add(gridNode);
			previousNode = gridNode;
			createPlFeaturePermissions(xmlChildNode, level + 1);
		}
		if (lastNode != null) {
			lastNode.setTreeImage(TREE_GRID_L);
		}
	}

	protected Long worldMapId;

	protected int mapsDepth;

	protected String isDefaultValue = "";

	protected boolean removeDefaultValue = false;
	
	protected String mapInstanceWriteDisabled = "disabled";
	
	protected String mapInstanceReadDisabled = "disabled";

	public String getMapInstanceWriteDisabled() {
		return mapInstanceWriteDisabled;
	}

	public String getMapInstanceReadDisabled() {
		return mapInstanceReadDisabled;
	}

	public Long getWorldMapId() {
		return worldMapId;
	}

	public boolean getRemoveDefaultValue() {
		return this.removeDefaultValue;
	}

	public String getIsDefaultValue() {
		return isDefaultValue;
	}

	public int getMapsDepth() {
		return mapsDepth;
	}

	public int getGroupNameLength() {
		return getAttributeLength("groupName");
	}

	protected List<GridNode> mapPermissions;

	public List<GridNode> getMapPermissions() {
		return mapPermissions;
	}

	/*
	 * Create grid nodes from MapNodes tree
	 */
	protected void createMapPermissions(Set<MapNode> mapNodes, int level) {
		GridNode firstNode = null, lastNode = null, previousNode = null;
		if (level > mapsDepth) {
			mapsDepth = level;
		}
		for (MapNode mapNode : mapNodes) {
			if (!mapNode.isLeafNode()) {
				GridNode gridNode = new GridNode(mapNode.getId());
				if (firstNode == null) {
					firstNode = gridNode;
				}
				lastNode = gridNode;
				gridNode.setNodeName(mapNode.getLabel());
				gridNode.setLevel(level);
				gridNode.setTreeImage(TREE_GRID_T);
				HmPermission permission = getDataSource()
						.getInstancePermissions().get(mapNode.getId());
				if (permission != null) {
					if (permission.hasAccess(HmPermission.OPERATION_READ)) {
						gridNode.setReadAccess(true);
					}
					if (permission.hasAccess(HmPermission.OPERATION_WRITE)) {
						gridNode.setWriteAccess(true);
					}
				}
				if (previousNode != null) {
					previousNode.setNextSibling(mapPermissions.size());
				}
				mapPermissions.add(gridNode);
				previousNode = gridNode;
				createMapPermissions(((MapContainerNode) mapNode)
						.getChildNodes(), level + 1);
			}
		}
		if (lastNode != null) {
			lastNode.setTreeImage(TREE_GRID_L);
			if (level == 0) {
				if (firstNode == lastNode) {
					firstNode.setTreeImage(TREE_GRID_X);
				} else {
					firstNode.setTreeImage(TREE_GRID_Y);
				}
			}
		}
	}

	/*
	 * Calculate child nodes count and set parent reference
	 */
	protected int getChildCount(List<GridNode> tree, int index, int parent) {
		int childCount = 0;
		do {
			GridNode gridNode = tree.get(index);
			gridNode.setParent(parent);
			if (index + 1 < tree.size()) {
				GridNode childNode = tree.get(index + 1);
				if (childNode.getLevel() > gridNode.getLevel()) {
					// Node has children
					gridNode
							.setChildCount(getChildCount(tree, index + 1, index));
				}
			}
			childCount += gridNode.getChildCount() + 1;
			index = gridNode.getNextSibling();
		} while (index != 0);
		return childCount;
	}

	/*
	 * Calculate child nodes count and set parent reference
	 */
	protected void setParentAccess(List<GridNode> tree, GridNode parent,
			int index) {
		boolean readAccess = false;
		boolean writeAccess = true;
		do {
			GridNode gridNode = tree.get(index);
			if (gridNode.getChildCount() > 0) {
				setParentAccess(tree, gridNode, index + 1);
			}
			if (gridNode.getReadAccess()) {
				readAccess = true;
			}
			if (!gridNode.getWriteAccess()) {
				writeAccess = false;
			}
			index = gridNode.getNextSibling();
		} while (index != 0);
		if (parent != null) {
			parent.setReadAccess(readAccess);
			parent.setWriteAccess(writeAccess);
		}
	}

	/*
	 * Add info to create <td> elements for indentation
	 */
	protected void addTreeIndentation(List<GridNode> tree, int index,
			List<Boolean> indentation) {
		if (index == tree.size()) {
			return;
		}
		GridNode gridNode = tree.get(index);
		if (gridNode.getTreeIndentation() == null) {
			gridNode.setTreeIndentation(indentation);
		}
		if (gridNode.getChildCount() > 0) {
			List<Boolean> childIndentation = new ArrayList<Boolean>(indentation);
			childIndentation.add(gridNode.getNextSibling() > 0);
			addTreeIndentation(tree, index + 1, childIndentation);
		}

		if (gridNode.getNextSibling() != 0) {
			addTreeIndentation(tree, gridNode.getNextSibling(), indentation);
		}
	}

	/*
	 * Update permissions from user input.
	 */

	public void updateGroupPermissions() throws Exception {
		getDataSource().getFeaturePermissions().clear();
		updateFeaturePermissions();
		getDataSource().getInstancePermissions().clear();
		updateMapPermissions();
	}

	public void updateFeaturePermissions() throws Exception {
		if (readFeatureIds != null) {
			for (String featureId : readFeatureIds) {
//				if (getL1Feature(featureId) != null) {
//					// Don't store top level features
//					continue;
//				}
				HmPermission permission = new HmPermission();
				permission.setOperations(HmPermission.OPERATION_READ);
				getDataSource().getFeaturePermissions().put(featureId,
						permission);
			}
		}
		if (writeFeatureIds != null) {
			for (String featureId : writeFeatureIds) {
				HmPermission permission = getDataSource()
						.getFeaturePermissions().get(featureId);
				if (permission != null) {
					permission.addOperation(HmPermission.OPERATION_WRITE);
				}
			}
		}

		// special for user password modify feature
		HmPermission permission = new HmPermission();
		permission.setOperations(HmPermission.OPERATION_READ);
		permission.addOperation(HmPermission.OPERATION_WRITE);
		getDataSource().getFeaturePermissions().put(
				Navigation.L2_FEATURE_USER_PASSWORD_MODIFY, permission);
		// special for license management feature
		HmPermission readPermission = new HmPermission();
		readPermission.setOperations(HmPermission.OPERATION_READ);
		getDataSource().getFeaturePermissions().put(
				Navigation.L2_FEATURE_LICENSEMGR, readPermission);
		
		getDataSource().getFeaturePermissions().put(
				Navigation.L2_FEATURE_ADMINISTRATION, readPermission);
		getDataSource().getFeaturePermissions().put(
				Navigation.L1_FEATURE_HOME, readPermission);
	}

	public void updateMapPermissions() throws Exception {
		if (readMapIds != null) {
			for (Long mapId : readMapIds) {
				HmPermission permission = new HmPermission();
				permission.setOperations(HmPermission.OPERATION_READ);
				HmBo hmBo = findBoById(MapNode.class, mapId);
				permission.setLabel(hmBo.getLabel());
				getDataSource().getInstancePermissions().put(mapId, permission);
			}
		}
		if (writeMapIds != null) {
			for (Long mapId : writeMapIds) {
				HmPermission permission = getDataSource()
						.getInstancePermissions().get(mapId);
				if (permission == null) {
					permission = new HmPermission();
					permission.setOperations(HmPermission.OPERATION_WRITE);
				} else {
					permission.addOperation(HmPermission.OPERATION_WRITE);
				}
			}
		}
	}

	/**
	 * Check the goup attribute unique (add by Fiona)
	 *
	 * @return -
	 * @throws Exception -
	 */
	private boolean isAttributeUnique() throws Exception {
		// attribute is in-effective when vhm exist
		if (getSwitchDomains().size() > 1) {
			return true;
		}

		//	default attribute
		if (getDataSource().isDefaultGroupAttribute()) {
			addActionError(MgrUtil.getUserMessage("error.usergroup.attribute.reserved"));
			return false;
		}
		
		List<HmUserGroup> groups = QueryUtil.executeQuery(HmUserGroup.class, null,
				new FilterParams("owner.domainName", HmDomain.HOME_DOMAIN));

		for (HmUserGroup group : groups) {
			if (!group.getGroupName().equals(getDataSource().getGroupName())) {
				if (group.getGroupAttribute() == getDataSource()
						.getGroupAttribute()) {
					addActionError(MgrUtil
							.getUserMessage("error.usergroup.attribute.exist"));
					return false;
				}
			}
		}
		return true;
	}

	protected List<String> readFeatureIds;

	protected List<String> writeFeatureIds;

	public void setReadFeatureIds(List<String> readFeatureIds) {
		this.readFeatureIds = readFeatureIds;
	}

	public void setWriteFeatureIds(List<String> writeFeatureIds) {
		this.writeFeatureIds = writeFeatureIds;
	}

	protected List<Long> readMapIds;

	protected List<Long> writeMapIds;

	public void setReadMapIds(List<Long> readMapIds) {
		this.readMapIds = readMapIds;
	}

	public void setWriteMapIds(List<Long> writeMapIds) {
		this.writeMapIds = writeMapIds;
	}

	public String getDisplayName() {
		return getDataSource().getGroupName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public boolean getDisabledName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public boolean getShowAttribute() {
		return !NmsUtil.isHostedHMApplication();
	}
	
	public boolean getDisabledGroupAttribute() {
		if (getDisabledName()) {
//			return "disabled".equals(getWriteDisabled());  // can edit default group
			return "disabled".equals(getUpdateDisabled()); // can not edit default group
		} else {
			return false;
		}
	}

	public String getMapFeatureKey() {
		return L2_FEATURE_MAP_VIEW;
	}

	public String getMapHierarchyKey() {
		return L1_FEATURE_TOPOLOGY;
	}
	
	public boolean getShowMapTab() {
		return !(getDataSource().isGMUserGroup() || getDataSource().isTcUserGroup());
	}
	
	private StringBuffer sucObj = new StringBuffer();

	private StringBuffer failObj = new StringBuffer();
	
	@Override
	protected int removeAllBos(Class<? extends HmBo> boClass, FilterParams filterParams,
			Collection<Long> defaultIds) throws Exception {
		AccessControl.checkUserAccess(getUserContext(), getSelectedL2FeatureKey(),
				CrudOperation.DELETE);
		int count = 0;
		String query = "select id from " + boClass.getSimpleName() + " bo";
		List<Long> boIds = (List<Long>) QueryUtil.executeQuery(query, null, filterParams, userContext, 1000);
		if (defaultIds != null) {
			boIds.removeAll(defaultIds);
		}
		while (!boIds.isEmpty()) {
			log.info("removeBos", "Removing: " + boIds.size() + " bos.");
			count += removeBos(boClass, boIds);
			boIds = (List<Long>) QueryUtil.executeQuery(query, null, filterParams, userContext, 1000);
			if (defaultIds != null) {
				boIds.removeAll(defaultIds);
			}
		}
		if (count > 0) {
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, "Remove " + count + " User Group(s) : "+ sucObj.toString().trim());
		}
		return count;
	}

	@Override
	protected int removeBos(Class<? extends HmBo> boClass, Collection<Long> ids) throws Exception {
		AccessControl.checkUserAccess(getUserContext(), getSelectedL2FeatureKey(),
				CrudOperation.DELETE);
		try {
			int success = removeUserGroupBos(ids);
			int failCount = ids.size() - success;
			if (success > 0) {
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "Remove " + success + " User Group(s) : "+ sucObj.toString().trim());
			}
			if (failCount > 0) {
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "Remove " + failCount + " User Group(s) : "+ failObj.toString().trim());
			}
			return success;
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * Remove all user group bo
	 *
	 *@param ids
	 *
	 *@return int
	 */
	private int removeUserGroupBos(Collection<Long> ids) throws Exception {
		if (ids == null) {
			return 0;
		}
		int count = 0;
		EntityManager em = null;
		EntityTransaction tx = null;
		HmUserGroup groupInfo = null;
		UserService userSer = new UserService();
		try {
			em = HibernateUtil.getEntityManagerFactory().createEntityManager();
			tx = em.getTransaction();
			tx.begin();
			
			for (Long id : ids) {
				groupInfo = em.getReference(HmUserGroup.class, id);
				try {
					// delete user group in MyHive
					if (NmsUtil.isHostedHMApplication() && null != userContext.getCustomerId() && !getIsInHomeDomain()) {
						UserModel result =userSer.syncUserGroup(userContext.getCustomerId(), groupInfo.getOwner().getVhmID(), groupInfo.getGroupName(), (short)1);
						if (null == result || result.getReturnCode() > 0) {
							addActionError(null == result?getText("bringIntoManagedListerror.unknown", "the result is null from MyHive"):result.getMessage());
							failObj.append(groupInfo.getGroupName() + "  ");
							continue;
						}
					}
					em.remove(groupInfo);
					
					sucObj.append(groupInfo.getGroupName() + "  ");
					count++;
				} catch (EntityNotFoundException e) {
					failObj.append(groupInfo.getGroupName() + "  ");
					log.error("removeUserGroupBos", "HmBo with id: " + id
							+ " not found, must have been removed earlier.");
				}
			}
			tx.commit();
		} catch (Exception e) {
			log.error("removeUserGroupBos", "Remove bo(s) failed.", e);
			MgrUtil.logExceptionCause(e);
			QueryUtil.rollback(tx);
			if (e instanceof PersistenceException
					&& (e.getCause() instanceof ConstraintViolationException || (e.getCause() != null && e
							.getCause().getCause() instanceof ConstraintViolationException))
					&& groupInfo != null) {
				throw new HmException("removeUserGroupBos " + groupInfo.getId()
						+ " failed, stale object state.", e, HmMessageCodes.OBJECT_IN_USE,
						new String[] { groupInfo.getLabel() });
			} else {
				throw e;
			}
		} finally {
			QueryUtil.closeEntityManager(em);
		}
		return count;
	}

	public class GridNode implements Serializable {

		private static final long serialVersionUID = 1L;

		public GridNode(Long id) {
			this.id = id;
			treeIndentation = null;
		}

		public GridNode(String key) {
			this.key = key;
			treeIndentation = null;
		}

		private Long id;

		private String key;

		private String nodeName;

		private String treeImage;

		private int level;

		private int nextSibling;

		private int childCount;

		private int parent;

		private boolean readAccess, writeAccess;

		public boolean getReadAccess() {
			return readAccess;
		}

		public void setReadAccess(boolean readAccess) {
			this.readAccess = readAccess;
		}

		public boolean getWriteAccess() {
			return writeAccess;
		}

		public void setWriteAccess(boolean writeAccess) {
			this.writeAccess = writeAccess;
		}

		public int getParent() {
			return parent;
		}

		public void setParent(int parent) {
			this.parent = parent;
		}

		private List<Boolean> treeIndentation;

		public List<Boolean> getTreeIndentation() {
			return treeIndentation;
		}

		public void setTreeIndentation(List<Boolean> treeIndentation) {
			this.treeIndentation = treeIndentation;
		}

		public int getNextSibling() {
			return nextSibling;
		}

		public void setNextSibling(int nextSibling) {
			this.nextSibling = nextSibling;
		}

		public Long getId() {
			return id;
		}

		public String getKey() {
			return key;
		}

		public String getNodeName() {
			return nodeName;
		}

		public void setNodeName(String nodeName) {
			this.nodeName = nodeName;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public String getTreeImage() {
			return treeImage;
		}

		public void setTreeImage(String treeImage) {
			this.treeImage = treeImage;
		}

		public int getChildCount() {
			return childCount;
		}

		public void setChildCount(int childCount) {
			this.childCount = childCount;
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo)
	{
		if (bo instanceof HmUserGroup) {
			HmUserGroup group = (HmUserGroup) bo;
			if (group.getInstancePermissions() != null)
				group.getInstancePermissions().size();
			if (group.getFeaturePermissions() != null)
				group.getFeaturePermissions().size();
		}
		return null;
	}

}