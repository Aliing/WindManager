package com.ah.ui.actions;

/**
 * Modification History
 *
 * add function - getBoCheckItems() and related constants
 * joseph chen, 05/05/2008
 */

/*
 * @author Chris Scheers
 */

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.validator.constraints.Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.Ostermiller.util.Base64;
import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeEventUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.db.configuration.ConfigurationChangedEvent;
import com.ah.be.db.configuration.ConfigurationUtils;
import com.ah.be.license.HM_License;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.os.NetConfigImplInterface;
import com.ah.be.parameter.BeParaModule;
import com.ah.be.rest.CustomerInfoUtil;
import com.ah.be.search.SearchResultSet;
import com.ah.be.search.SearchUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmAutoRefresh;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmTableSize;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.admin.PlanToolConfig;
import com.ah.bo.admin.UserRegInfoForLs;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.lan.LanProfile;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.mgmt.AhDataTableColumn;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.PagingImpl;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.Vlan;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ha.HAStatus;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.admin.NavigationCustomizationUtil;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.ui.actions.monitor.HiveApToolkitAction;
import com.ah.ui.actions.monitor.ReportListAction;
import com.ah.ui.actions.monitor.SystemStatusCache;
import com.ah.ui.actions.monitor.VpnReportListAction;
import com.ah.util.CheckItem;
import com.ah.util.CheckItem3;
import com.ah.util.EnumItem;
import com.ah.util.HibernateUtil;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.HmProxyUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;
import com.ah.util.notificationmsg.AhNotificationMsgPool;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;

public class BaseAction extends Navigation implements SessionKeys,
		HmMessageCodes, Preparable, ServletRequestAware, ServletResponseAware {

	public static final int GRID_COUNT = 3;

	private static final long serialVersionUID = 1L;

	// do not add item before the list
	public static final short CHECK_ITEM_BEGIN_NO = 0;

	// add a blank item before the list
	public static final short CHECK_ITEM_BEGIN_BLANK = 1;

	// do not add item at the end of list
	public static final short CHECK_ITEM_END_NO = 0;

	// add an item "[-New-]" at the end of list
	public static final short CHECK_ITEM_END_NEW = 1;

	// id of black check item
	public static final short CHECK_ITEM_ID_BLANK = -1;

	// id of check item 'None'
	public static final short CHECK_ITEM_ID_NONE = -1;

	// id of check item '[-New-]'
	public static final short CHECK_ITEM_ID_NEW = -2;

	// check item '[-New-]'
	public static final String CHECK_ITEM_NEW = "[-New-]";

	public static final int DISPLAY_LENGTH_IN_GUI = 16;
	public static final int DISPLAY_LENGTH_IN_GUI_OK = 18;

	private static final Tracer log = new Tracer(
			BaseAction.class.getSimpleName());

	protected HttpServletRequest request;

	protected HttpServletResponse response;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
		this.response.setCharacterEncoding("utf-8");// encoding to UTF-8;
	}

	@Override
	public void prepare() throws Exception {
		// get version info
		versionInfo = getSessionVersionInfo();
		// fix version bug
		if (null == versionInfo) {
			versionInfo = NmsUtil.getVersionInfo();
			if (null != versionInfo) {
				setSessionVersionInfo(versionInfo);
			}
		}

		// get user info
		userContext = getSessionUserContext();
		if (userContext != null) {
			request.getSession().setMaxInactiveInterval(
					userContext.getSessionExpiration());
			mode = userContext.getMode();
		}
		// get user's domain, or switch domain.
		// If domain ID is passed through the URL, it will overwrite this ID.
		domainId = QueryUtil.getDependentDomainFilter(userContext);
		// default value for wirelessRoutingEnable, if passed from URL, will
		// overwrite.
		//wirelessRoutingEnable = (mode == HmStartConfig.HM_MODE_FULL);
	}

	public String globalForward() {
		return null;
	}

	protected boolean readPermission;

	public boolean getReadPermission() {
		return readPermission;
	}

	public void setReadPermission(boolean readPermission) {
		this.readPermission = readPermission;
	}

	protected boolean writePermission;

	public boolean getWritePermission() {
		return writePermission;
	}

	public void setWritePermission(boolean writePermission) {
		this.writePermission = writePermission;
	}

	public String getWriteDisabled() {
		if (writePermission) {
			if (getShowDomain() && null != boClass) {
				List<?> boIds = QueryUtil.executeQuery(
						"select owner.domainName from "
								+ boClass.getSimpleName(), null,
						new FilterParams("id", id));
				if (!boIds.isEmpty()
						&& !HmDomain.HOME_DOMAIN
								.equals(boIds.get(0).toString())) {
					return "disabled";
				}
			}
			return "";
		} else {
			return "disabled";
		}
	}

	/**
	 * return disable value to struts tag
	 *
	 * @return -
	 */
	public boolean getWriteDisable4Struts() {
		return !getWriteDisabled().isEmpty();
	}

	public String getWriteDisabled4HHM() {
		if (NmsUtil.isHostedHMApplication()
				&& getUserContext().getDomain().isHomeDomain()) {
			return "disabled";
		}

		return getWriteDisabled();
	}

	public boolean getWriteDisabled4HHMStruts() {
		return NmsUtil.isHostedHMApplication()
				&& getUserContext().getDomain().isHomeDomain()
				|| !getWriteDisabled().isEmpty();
	}

	public boolean getWriteInstancePermission() {
		if (getUserContext() == null) {
			return false;
		}
		if (getUserContext().getUserGroup().isAdministrator()) {
			return true;
		}
		HmPermission instancePermission = getUserContext().getUserGroup()
				.getInstancePermissions().get(id);
		return instancePermission != null
				&& instancePermission.hasAccess(HmPermission.OPERATION_WRITE);
	}

	public static void refreshInstancePermissions() {
		HmUser user = BaseAction.getSessionUserContext();
		if (user != null && user.getSwitchDomain() != null) {
			if (user.getUserGroup() != null
					&& !user.getUserGroup().isAdministrator()) {
				final class UserGroupQueryBo implements QueryBo {
					public Collection<HmBo> load(HmBo bo) {
						if (bo != null) {
							((HmUserGroup) bo).getInstancePermissions().size();
						}
						return null;
					}
				}
				HmUserGroup userGroup = QueryUtil.findBoById(HmUserGroup.class,
						user.getUserGroup().getId(), new UserGroupQueryBo());
				user.getUserGroup().setInstancePermissions(
						userGroup.getInstancePermissions());
			}
		}
	}

	public boolean getSuperAdminPermission(){
		if(getUserContext() == null){
			return false;
		}
		if(getUserContext().isSuperUser()){
			return true;
		}
		return false;
	}

	public static synchronized HmUser getSessionUserContext() {
		return (HmUser) MgrUtil.getSessionAttribute(USER_CONTEXT);
	}

	public static synchronized void setSessionUserContext(HmUser userContext) {
		MgrUtil.setSessionAttribute(USER_CONTEXT, userContext);
	}

	public static synchronized BeVersionInfo getSessionVersionInfo() {
		return (BeVersionInfo) MgrUtil.getSessionAttribute(HIVEMANAGER_VERSION);
	}

	public static synchronized void setSessionVersionInfo(
			BeVersionInfo versionInfo) {
		MgrUtil.setSessionAttribute(HIVEMANAGER_VERSION, versionInfo);
	}

	public static synchronized AhNotificationMsgPool getSessionNotificationMessagePool() {
		return (AhNotificationMsgPool) MgrUtil
				.getSessionAttribute(SessionKeys.NOTIFICATION_MESSAGE_POOL);
	}

	public static synchronized Set<?> getSessionNotificationMessages() {
		AhNotificationMsgPool pool = getSessionNotificationMessagePool();
		if (null != pool) {
			return pool.getCurrentAvailableMessages();
		}
		return null;
	}

	public static synchronized boolean isLsMessageEnableDisplay() {
		AhNotificationMsgPool pool = getSessionNotificationMessagePool();
		return null != pool && pool.isEnableLicenceMsgFlag();
	}

    public static synchronized void setSessionNotificationMessagePool() {
        final String groupName = null == getSessionUserContext() ? "" : getSessionUserContext()
                .getUserGroup().getGroupName();
        if (groupName.equals(HmUserGroup.PLANNING)
                || groupName.equals(HmUserGroup.GM_ADMIN)
                || groupName.equals(HmUserGroup.GM_OPERATOR)
                || groupName.equals(HmUserGroup.TEACHER)) {
            return;
        }
        // only allow above user group to display the notification
        MgrUtil.setSessionAttribute(SessionKeys.NOTIFICATION_MESSAGE_POOL,
                new AhNotificationMsgPool());
    }

	public int getAttributeLength(String name) {
		/*- hibernate annotation doesn't work after hibernate upgraded to 3.5.x, use JPA annotation instead
		try {
			Field field = boClass.getDeclaredField(name);
			Length length = field.getAnnotation(Length.class);
			if (length != null) {
				log.debug("getAttributeLength", "Attribute '" + name
						+ "' length: " + length.max());
				return length.max();
			}
		} catch (NoSuchFieldException e) {
			log.error("getAttributeLength", "Attribute '" + name
					+ "' does not exist in class '" + boClass.getName() + "'.");
		}*/
		if (boPersistentClass == null) {
			log.error("getAttributeLength",
					"boPersistentClass has not been initialized.");
			return 0;
		}
		Property property = boPersistentClass.getProperty(name);
		if (property == null) {
			log.error("getAttributeLength", "Attribute '" + name
					+ "' does not exist in class '" + boClass.getName() + "'.");
			return 0;
		}
		if (property.getColumnIterator().hasNext()) {
			Column column = (Column) property.getColumnIterator().next();
			log.debug("getAttributeLength", "Attribute '" + name
					+ "' length is: " + column.getLength());
			return column.getLength();
		} else {
			log.error("getAttributeLength", "Attribute '" + name
					+ "' in class '" + boClass.getName()
					+ "' does not map to a column.");
			return 0;
		}
	}

	public Range getAttributeRange(String name) {
		try {
			Field field = boClass.getDeclaredField(name);
			Range range = field.getAnnotation(Range.class);
			if (range != null) {
				log.debug("getAttributeRange", "Attribute '" + name + "' min: "
						+ range.min() + ", max: " + range.max());
				return range;
			}
		} catch (NoSuchFieldException nsfe) {
			log.error("getAttributeRange", "Attribute '" + name
					+ "' does not exist in class '" + boClass.getName() + "'.");
		}
		return null;
	}

	public Range getAttributeRange(String name,Class<? extends HmBo> boObjectClass) {
		try {
			Field field = boObjectClass.getDeclaredField(name);
			Range range = field.getAnnotation(Range.class);
			if (range != null) {
				log.debug("getAttributeRange", "Attribute '" + name + "' min: "
						+ range.min() + ", max: " + range.max());
				return range;
			}
		} catch (NoSuchFieldException nsfe) {
			log.error("getAttributeRange", "Attribute '" + name
					+ "' does not exist in class '" + boObjectClass.getName() + "'.");
		}
		return null;
	}

	public IntRangeFieldValidator getAttributeRangeValidator(String name) {
		String methodName = "get" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		log.debug("getAttributeRangeValidator", "Looking for method: "
				+ methodName);
		try {
			Method method = boClass.getDeclaredMethod(methodName);
			IntRangeFieldValidator range = method
					.getAnnotation(IntRangeFieldValidator.class);
			if (range != null) {
				log.debug("getAttributeRangeValidator", "Method '" + methodName
						+ "' min: " + range.min() + ", max: " + range.max());
				return range;
			}
		} catch (NoSuchMethodException nsme) {
			log.debug("getAttributeRangeValidator", "Method: " + methodName
					+ " not found.");
		}
		return null;
	}

	protected Class<? extends HmBo> boClass;

	protected PersistentClass boPersistentClass;

	protected HmBo dataSource;

	public void setDataSource(Class<? extends HmBo> boClass) {
		this.boClass = boClass;
		this.boPersistentClass = HibernateUtil.getPersistentClass(boClass);
		getSessionDataSource();
	}

	public HmBo getDataSource() {
		return dataSource;
	}

	public void getSessionDataSource() {
		dataSource = (HmBo) MgrUtil.getSessionAttribute(boClass.getSimpleName()
				+ "Source");
	}

	public void setSessionDataSource(HmBo dataSource) {
		this.dataSource = dataSource;
		MgrUtil.setSessionAttribute(boClass.getSimpleName() + "Source",
				dataSource);
	}

	public void clearDataSource() {
		MgrUtil.removeSessionAttribute(boClass.getSimpleName() + "Source");
		id = null;
	}

	public boolean baseOperation() throws Exception {
		if (OPERATION_SORT.equals(operation)) {
			updateSortParams();
			return true;
		} else if ("saveColumns".equals(operation)) {
			updateTableView();
			return true;
		} else if ("resetColumns".equals(operation)) {
			selectedColumnIds = null;
			updateTableView();
			return true;
		} else if ("cancelColumns".equals(operation)) {
			return true;
		} else if ("paintbrush".equals(operation)) {
			paintbrushOperation();
			return true;
		} else if ("cancelPaintbrush".equals(operation)) {
			setAllSelectedIds(null);
			return true;
		} else {
			return pagingOperation() || removeOperation();
		}
	}

	protected void paintbrushOperation() throws Exception {
		Set<Long> destinationIds = null;
		if (allItemsSelected) {
			this.getSessionFiltering();
			List<Long> list = (List<Long>) QueryUtil.executeQuery(
					"select id from " + boClass.getSimpleName(), null,
					filterParams, domainId);
			Collection<Long> defaultIds = getDefaultIds();
			if (defaultIds != null && list.removeAll(defaultIds)) {
				addActionMessage(MgrUtil
						.getUserMessage("error.use.paintbrush.default.item"));
			}
			destinationIds = new HashSet<Long>(list);
		} else if (allSelectedIds != null && !allSelectedIds.isEmpty()) {
			Collection<Long> defaultIds = getDefaultIds();
			if (defaultIds != null && allSelectedIds.removeAll(defaultIds)) {
				addActionMessage(MgrUtil
						.getUserMessage("error.use.paintbrush.default.item"));
			}
			destinationIds = allSelectedIds;
		}
		log.info("paintbrushOperation", "operation:" + operation
				+ ", paintbrushSource:" + paintbrushSource + ", destination:"
				+ (destinationIds == null ? "null" : destinationIds));
		if (null == paintbrushSource || null == destinationIds
				|| destinationIds.isEmpty()) {
			setAllSelectedIds(null);
			setPaintbrushSource(null);
			return;
		}
		List<HmBo> bos = paintbrushBos(paintbrushSource, destinationIds);
		if (null != bos && !bos.isEmpty()) {
			Map<Long, Date> versions = new HashMap<Long, Date>();
			for (HmBo bo : bos) {
				versions.put(bo.getId(), bo.getVersion());
			}
			try {
				Collection<HmBo> hmBos = BoMgmt.bulkUpdateBos(bos,
						getUserContext(), getSelectedL2FeatureKey());
				for (HmBo bo : hmBos) {
					if (bo instanceof LocalUser) {
						log.info("updateBo",
								"Update LocalUser is customized for configuration indication.");
					} else {
						// generate an event to configuration indication process
						HmBeEventUtil
								.eventGenerated(new ConfigurationChangedEvent(
										bo,
										ConfigurationChangedEvent.Operation.UPDATE,
										versions.get(bo.getId())));
					}
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.base.operation.update",new String[]{getLastTitle(),bo.getLabel()}));
					addActionMessage(MgrUtil.getUserMessage(OBJECT_UPDATED,
							bo.getLabel()));
				}
			} catch (Exception e) {
				for (HmBo bo : bos) {
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.base.operation.update",new String[]{getLastTitle(),bo.getLabel()}));
				}
				throw e;
			}
		}
		setAllSelectedIds(null);
		setPaintbrushSource(null);
	}

	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		return null;
	}

	public void getPaintbrushSourceInfo() throws Exception {
		log.info("getPaintbrushSourceInfo", "Paintbrush source id:" + id);
		jsonObject = new JSONObject();
		if (null != id) {
			HmBo profile = QueryUtil.findBoById(boClass, id);
			if (null != profile) {
				jsonObject.put("n", profile.getLabel());
			}
		}
	}

	protected void updateTableSize() {
		log.info("updateTableSize", "saving page size for table: " + tableId
				+ ", page size:" + pageSize);
		if (pageSize > 0) {
			try {
				HmUser hmUser = QueryUtil.findBoById(HmUser.class,
						getUserContext().getId(), new ImplQueryBo());
				if (hmUser == null) hmUser = getUserContext();
				HmTableSize tableSize = getUserContext().getTableSizeMappings()
						.get(tableId);
				if (null == tableSize) {
					tableSize = new HmTableSize(tableId, pageSize);
					tableSize.setUseremail(hmUser.getEmailAddress());
				}
				if (hmUser != null && !HAUtil.isSlave()) {
					hmUser.removeTableSizes(tableId);
					hmUser.getTableSizes().remove(tableSize);

					HmTableSize hmTableSize = new HmTableSize(tableId, pageSize);
					hmTableSize.setUseremail(hmUser.getEmailAddress());
					hmUser.getTableSizes().add(hmTableSize);

					hmUser.addTableSizes(hmTableSize);
//					hmUser = QueryUtil.updateBo(hmUser);
					getUserContext().setTableSizes(hmUser.getTableSizes());
				} else {
					if (getUserContext().getTableSizes() != null) {
						getUserContext().getTableSizes().remove(tableSize);

						HmTableSize tSize = new HmTableSize(tableId, pageSize);
						tSize.setUseremail(hmUser == null ? "null_value" : hmUser.getEmailAddress());
						getUserContext().getTableSizes().add(tSize);
					} else {
						List<HmTableSize> tmpTableSize = new ArrayList<HmTableSize>();
						getUserContext().setTableSizes(tmpTableSize);

						HmTableSize tSize = new HmTableSize(tableId, pageSize);
						tSize.setUseremail(hmUser == null ? "null_value" : hmUser.getEmailAddress());
						getUserContext().getTableSizes().add(tSize);
					}
				}
				getUserContext().createTableSizeMappings();
			} catch (Exception e) {
				log.error("updateTableSize", e);
			}
		}
	}

	protected void updateTableView() throws Exception {
		log.info("updateTableView", "saving columns for table: " + tableId
				+ ", " + selectedColumnIds);
		LinkedHashSet<HmTableColumn> columns = new LinkedHashSet<HmTableColumn>();
		HmUser hmUser = QueryUtil.findBoById(HmUser.class, getUserContext()
				.getId(), new ImplQueryBo());
		if (hmUser == null) hmUser = getUserContext();
		if (selectedColumnIds != null) {
			// key column should not be unselected
			if (keyColumnId != NO_KEY_COLUMN
					&& !selectedColumnIds
							.contains(Integer.valueOf(keyColumnId))) {
				selectedColumnIds.add(0, keyColumnId);
				addActionMessage(MgrUtil.getUserMessage(KEY_COLUMN_UNSELECTED));
			}

			int i = 0;
			for (Integer columnId : selectedColumnIds) {
				HmTableColumn column = new HmTableColumn(columnId);
				column.setTableId(tableId);
				column.setUseremail(hmUser == null ? "null_value" : hmUser.getEmailAddress());
				column.setPosition(i++);
				columns.add(column);
			}
		}

		List<HmTableColumn> oldColumns = getUserContext().getTableViews().get(
				tableId);
		if (hmUser != null && !HAUtil.isSlave()) {
			if (oldColumns != null) {
				hmUser.removeTableColumns(tableId);
				hmUser.getTableColumns().removeAll(oldColumns);
			}
			hmUser.getTableColumns().addAll(columns);
			hmUser.addTableColumns(new ArrayList<HmTableColumn>(columns));
//			hmUser = QueryUtil.updateBo(hmUser);
			getUserContext().setTableColumns(hmUser.getTableColumns());
		} else {
			if (oldColumns != null) {
				if (getUserContext().getTableColumns() != null) {
					getUserContext().getTableColumns().removeAll(oldColumns);
				}
			}
			if (getUserContext().getTableColumns() != null) {
				getUserContext().getTableColumns().addAll(columns);
			} else {
				getUserContext().setTableColumns(new ArrayList<HmTableColumn>(columns));
			}
		}
		getUserContext().createTableViews();
	}

	/**
	 * set the description of columns
	 *
	 * @param columns
	 *            -
	 * @author Joseph Chen
	 */
	protected void setColumnDescription(List<HmTableColumn> columns) {
		for (HmTableColumn column : columns) {
			column.setColumnDescription(getColumnDescription(column
					.getColumnId()));
			column.setTableId(tableId);
		}
	}

	/**
	 * get the description of column by id this function will be hidden by
	 * sub-class
	 *
	 * @param id
	 *            -
	 * @return -
	 * @author Joseph Chen
	 */
	protected String getColumnDescription(int id) {
		return null;
	}

	protected void setTableColumns() {
		/*modify the method of assignment for selectedColumns  
		 in case the session value is changed*/
		if(!selectedColumns.isEmpty()){
			selectedColumns.clear();
		}
		
		if(null != getUserContext().getTableViews().get(tableId)){
			selectedColumns.addAll(getUserContext().getTableViews().get(tableId));
		}else{
			selectedColumns = getInitSelectedColumns();
		}
		
		setColumnDescription(selectedColumns);
		/*
		 * do this in case some columns are modified or removed during
		 * restoration
		 */
		removeEmptyColumn(selectedColumns);
		availableColumns = getDefaultSelectedColums();
		setColumnDescription(availableColumns);
		availableColumns.removeAll(selectedColumns);
		// sorting available column list
		Collections.sort(availableColumns, new Comparator<HmTableColumn>() {
			@Override
			public int compare(HmTableColumn o1, HmTableColumn o2) {
				return o1.getColumnDescription().compareToIgnoreCase(
						o2.getColumnDescription());
			}
		});
	}

	protected void removeEmptyColumn(List<HmTableColumn> columns) {
		if (columns == null || columns.isEmpty()) {
			return;
		}

		List<HmTableColumn> emptyCols = new ArrayList<HmTableColumn>();

		for (HmTableColumn col : columns) {
			if (col.getColumnDescription() == null
					|| col.getColumnDescription().isEmpty()) {
				emptyCols.add(col);
			}
		}

		columns.removeAll(emptyCols);
	}

	protected List<HmTableColumn> getInitSelectedColumns() {
		return getDefaultSelectedColums();
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		return new ArrayList<HmTableColumn>();// avoid exception
	}

	protected String prepareBoList() throws Exception {
		clearDataSource();
		setFormChanged(false);
		removeSessionAttributes();
		preparePage();
		setTableColumns();

		if (isEasyMode()) {
			removeLastExConfigGuide();
			removeExConfigGuideFeature();
		}

		return SUCCESS;
	}

	protected String prepareBoListForExpressLocalUser() throws Exception {
		clearDataSource();
		setFormChanged(false);
		MgrUtil.removeSessionAttribute("lstTitle");
		MgrUtil.removeSessionAttribute("lstTabId");
		MgrUtil.removeSessionAttribute("lstForward");
		MgrUtil.removeSessionAttribute("lstFormChanged");
		ReportListAction.clearOldSession();
		VpnReportListAction.clearOldSession();
		preparePage();
		setTableColumns();
		return "localUserListEx";
	}

	protected String prepareEmptyBoList() throws Exception {
		clearDataSource();
		setFormChanged(false);
		page = new ArrayList<>();
		return SUCCESS;
	}

	public void removeSessionAttributes() {
		MgrUtil.removeSessionAttribute("lstTitle");
		MgrUtil.removeSessionAttribute("lstTabId");
		MgrUtil.removeSessionAttribute("lstForward");
		MgrUtil.removeSessionAttribute("lstFormChanged");
		removeLastExConfigGuide();
		removeExConfigGuideFeature();
		ReportListAction.clearOldSession();
		VpnReportListAction.clearOldSession();
		HiveApToolkitAction.clearSessions();
	}

	protected String prepareActionError(Exception e) throws Exception {
		log.error("prepareActionError", MgrUtil.getUserMessage(e), e);
		addActionError(MgrUtil.getUserMessage(e));
		generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
				+ " " + MgrUtil.getUserMessage(e));
		try {
			return prepareBoList();
		} catch (Exception ne) {
			return prepareEmptyBoList();
		}
	}

	protected void reportActionError(Exception e) {
		log.error("prepareActionError", MgrUtil.getUserMessage(e), e);
		addActionError(MgrUtil.getUserMessage(e));
		generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
				+ " " + MgrUtil.getUserMessage(e));
	}

	/*
	 * Paging
	 */

	public int pageIndex = 1;

	public int pageSize = 0;

	public String gotoPage;

	public List page;

	public int getPageIndex() {
		return paging.getPageIndex();
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public long getRowCount() {
		return paging.getRowCount();
	}

	public int getPageCount() {
		return paging == null ? 0 : paging.getPageCount();
	}

	public int getPageSize() {
		return paging.getPageSize();
	}

	public long getAvailableRowCount() {
		return paging.getAvailableRowCount();
	}

	public int getAvailablePageRowCount() {
		return paging.getAvailablePageRowCount();
	}

	public String getPagePlus() {
		return paging.getPagePlus();
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getGotoPage() {
		return gotoPage;
	}

	public void setGotoPage(String gotoPage) {
		this.gotoPage = gotoPage;
	}

	public List getPage() {
		return page;
	}

	protected void preparePage() throws Exception {
		enableSorting();
		enablePaging();
		page = findBos();
	}

	private boolean pageIndexChanged;

	protected boolean pagingOperation() {
		if ("firstPage".equals(operation)) {
			pageIndex = 1;
		} else if ("resizePage".equals(operation)) {
			pageIndex = 1;
			updateTableSize();
		} else if ("previousPage".equals(operation)) {
			pageIndex--;
		} else if ("nextPage".equals(operation)) {
			pageIndex++;
		} else if ("lastPage".equals(operation)) {
			pageIndex = -1;
		} else if ("gotoPage".equals(operation)) {
			try {
				pageIndex = Integer.parseInt(gotoPage);
				if (pageIndex < 1) {
					pageIndex = 1;
				}
			} catch (NumberFormatException e) {
				pageIndex = 1;
			}
		} else {
			return false;
		}
		pageIndexChanged = true;
		//if (allItemsSelected) {
			// do not save the selected id when all Item Selected
			allSelectedIds = null;
		//}
		return true;
	}

	protected Paging<? extends HmBo> paging;

	protected void enablePaging() {
		String sessionKey = boClass.getSimpleName() + "Paging";
		paging = (Paging<? extends HmBo>) MgrUtil.getSessionAttribute(sessionKey);
		if (paging == null) {
			paging = new PagingImpl<>(boClass);
			MgrUtil.setSessionAttribute(sessionKey, paging);
		}
		paging.clearNext();
		if (pageIndexChanged) {
			paging.setPageIndex(pageIndex);
		}
		paging.clearRowCount();
		
		if (isEasyMode() && getLastExConfigGuide() != null) {
			paging.setPageSize(15);
		}

		// set the page size if already persistent in db
		HmTableSize tableSize = null;
		if (userContext.getTableSizeMappings()!=null) {
			tableSize = userContext.getTableSizeMappings().get(tableId);
		}
		if (null != tableSize && tableSize.getTableSize() > 0) {
			paging.setPageSize(tableSize.getTableSize());
		} else {
			paging.setPageSize(15);
		}
		
		if (pageSize != 0) {
			paging.setPageSize(pageSize);
		}
		gotoPage = "";
		paging.setSelectedIds(allSelectedIds);
	}

	/*
	 * Sorting
	 */

	protected SortParams sortParams;

	protected void enableSorting() {
		String sessionKey = boClass.getSimpleName() + "Sorting";
		sortParams = (SortParams) MgrUtil.getSessionAttribute(sessionKey);
		if (sortParams == null) {
			sortParams = new SortParams("id");

			MgrUtil.setSessionAttribute(sessionKey, sortParams);
		}
		// So every sort tag doesn't need to specify a session key
		ActionContext.getContext().put(PAGE_SORTING, sortParams);
	}

	/*
	 * Filtering
	 */
	protected FilterParams filterParams;

	protected void setSessionFiltering() {
		String sessionKey = boClass.getSimpleName() + "Filtering";
		MgrUtil.setSessionAttribute(sessionKey, filterParams);
	}

	protected void getSessionFiltering() {
		String sessionKey = boClass.getSimpleName() + "Filtering";
		filterParams = (FilterParams) MgrUtil.getSessionAttribute(sessionKey);
	}

	protected void updateSortParams() {
		enableSorting();
		sortParams.setOrderBy(orderBy);
		sortParams.setAscending(ascending);
		sortParams.setExtOrderBy(extOrderBy);
		sortParams.setExtAscending(extAscending);
		sortParams.setOrderByNumber(orderByNumber);
		sortParams.setOrderByIp(orderByIp);
	}

	public boolean hasDefaultValues() {
		try {
			boClass.getDeclaredField("defaultFlag");
			return true;
		} catch (NoSuchFieldException e) {
			return false;
		}
	}

	public Collection<Long> getDefaultIds() {
		List<?> boIds;
		if (boClass.getName().equals("com.ah.bo.useraccess.MgmtServiceIPTrack")) {
			boIds = QueryUtil
					.executeQuery("select id from " + boClass.getSimpleName(),
							null, new FilterParams("owner.domainName",
									HmDomain.GLOBAL_DOMAIN));
		} else {
			if (!hasDefaultValues()) {
				return null;
			}
			boIds = QueryUtil.executeQuery(
					"select id from " + boClass.getSimpleName(), null,
					new FilterParams("defaultFlag", true));
		}
		if (boIds.isEmpty()) {
			return null;
		}
		Collection<Long> defaultIds = new ArrayList<Long>(boIds.size());
		for (Object obj : boIds) {
			defaultIds.add((Long) obj);
		}
		return defaultIds;
	}

	public Collection<Long> getDefaultIds(Class<? extends HmBo> hmBoClass) {
		boolean haveDefaultValue;
		try {
			hmBoClass.getDeclaredField("defaultFlag");
			haveDefaultValue = true;
		} catch (NoSuchFieldException e) {
			haveDefaultValue = false;
		}

		if (!haveDefaultValue) {
			return null;
		}
		List<?> boIds = QueryUtil.executeQuery(
				"select id from " + hmBoClass.getSimpleName(), null,
				new FilterParams("defaultFlag", true));
		if (boIds.isEmpty()) {
			return null;
		}
		Collection<Long> defaultIds = new ArrayList<Long>(boIds.size());
		for (Object obj : boIds) {
			defaultIds.add((Long) obj);
		}
		return defaultIds;
	}

	protected List<String> removeOperationJson(Class<? extends HmBo> boToRemove) throws Exception {
		return removeOperationJson(boToRemove, selectedIds);
	}
	/**
	 * Remove objects whose type is boToRemove, ids are defined by selectedIds.
	 * Returned value contains list of messages for error or message.
	 * @param boToRemove -
	 * @param idsToRemoveArg -
	 * @return -
	 * @throws Exception -
	 */
	protected List<String> removeOperationJson(Class<? extends HmBo> boToRemove, List<Long> idsToRemoveArg) throws Exception {
		if (idsToRemoveArg == null
				|| idsToRemoveArg.isEmpty()
				|| boToRemove == null) {
			return null;
		}

		List<String> result = new ArrayList<String>();

		int count = -1;
		boolean hasRemoveDefaultValue = false;

		Collection<Long> defaultIds = getDefaultIds(boToRemove);
		if (defaultIds != null && idsToRemoveArg.removeAll(defaultIds)) {
			hasRemoveDefaultValue = true;
			result.add(MgrUtil
					.getUserMessage(OBJECT_IS_DEFAULT_VALUE));
		}
		Collection<Long> toRemoveIds = new ArrayList<Long>(idsToRemoveArg);

		if (!toRemoveIds.isEmpty()) {
			try {
				count = removeBos(boToRemove, toRemoveIds);
			} catch (Exception e) {
				result.add(MgrUtil.getUserMessage(e));
				return result;
			}
		}

		log.info("removeOperationJson", "Count: " + count);

		if (count < 0) {
			result.add(MgrUtil.getUserMessage(SELECT_OBJECT));
		} else if (count == 0) {
			result.add(MgrUtil.getUserMessage(NO_OBJECTS_REMOVED));
		} else if (count == 1) {
			if (hasRemoveDefaultValue) {
				result.add(MgrUtil
						.getUserMessage(OBJECT_REMOVED_WITH_DEFAULT));
			} else {
				result.add(MgrUtil.getUserMessage(OBJECT_REMOVED));
			}
		} else {
			if (hasRemoveDefaultValue) {
				result.add(MgrUtil.getUserMessage(
						OBJECTS_REMOVED_WITH_DEFAULT, count + ""));
			} else {
				result.add(MgrUtil.getUserMessage(OBJECTS_REMOVED, count
						+ ""));
			}
		}

		return result;
	}

	protected boolean removeOperation() throws Exception {
		if (!"remove".equals(operation)) {
			return false;
		}
		int count = -1;
		boolean hasRemoveDefaultValue = false;
		if (allItemsSelected) {
			setAllSelectedIds(null);
			this.getSessionFiltering();

			/*
			 * in easy mode of HM, the profile reference in ConfigTemplate
			 * should be cleared first
			 */
			if (isEasyMode()) {
				updateConfigTemplate();
			}

//			if (boClass.equals(HiveAp.class)) {
//				CVGAndBRIpResourceManage.updateSubNetworkResourceRemoveBR();
//			}
			if (getShowDomain()) {
				count = removeAllBos(boClass, filterParams,
						getNonHomeDataInHomeDomain());
				addActionMessage(MgrUtil
						.getUserMessage(OBJECT_IS_NONHOME_DOMAIN_VALUE));
			} else {
				Collection<Long> defaultIds = getDefaultIds();
				count = removeAllBos(boClass, filterParams, defaultIds);
				if (null != defaultIds && !defaultIds.isEmpty()) {
					hasRemoveDefaultValue = true;
					addActionMessage(MgrUtil
							.getUserMessage(OBJECT_IS_DEFAULT_VALUE));
				}
			}
		} else if (allSelectedIds != null && !allSelectedIds.isEmpty()) {
			Collection<Long> defaultIds = getDefaultIds();
			if (defaultIds != null && allSelectedIds.removeAll(defaultIds)) {
				hasRemoveDefaultValue = true;
				addActionMessage(MgrUtil
						.getUserMessage(OBJECT_IS_DEFAULT_VALUE));
			}
			Collection<Long> toRemoveIds = new ArrayList<Long>(allSelectedIds);

			//just put the object has a relationship into the end of list for displaying
			//correct error message in the page
			if("radiusAttrs".equals(getSelectedL2FeatureKey())){
				List<Long> relationshipList = new ArrayList<Long>();
				Iterator<Long> it = toRemoveIds.iterator();
				while(it.hasNext()){
					long id = it.next();
					if(!checkDoRemoveForSelected(boClass,id)){
						relationshipList.add(id);
					     it.remove();
					}
				}
				toRemoveIds.addAll(relationshipList);
			}

			/*
			 * in easy mode of HM, the profile reference in ConfigTemplate
			 * should be cleared first
			 */
			if (isEasyMode()) {
				updateConfigTemplate();
			}

			if (!checkUsedProfile(toRemoveIds)) {
				return false;
			}
			if (!checkCVGUsed(toRemoveIds)) {
				return false;
			}
			setAllSelectedIds(null);
//			if (boClass.equals(HiveAp.class)) {
//				CVGAndBRIpResourceManage
//						.updateSubNetworkResourceRemoveBR(toRemoveIds);
//			}
			if (!toRemoveIds.isEmpty()) {
				count = removeBos(boClass, toRemoveIds);
			}
		}

		log.info("removeOperation", "Count: " + count);

		if (count < 0) {
			addActionMessage(MgrUtil.getUserMessage(SELECT_OBJECT));
		} else if (count == 0) {
			addActionMessage(MgrUtil.getUserMessage(NO_OBJECTS_REMOVED));
		} else if (count == 1) {
			if (hasRemoveDefaultValue) {
				addActionMessage(MgrUtil
						.getUserMessage(OBJECT_REMOVED_WITH_DEFAULT));
			} else {
				addActionMessage(MgrUtil.getUserMessage(OBJECT_REMOVED));
			}
		} else {
			if (hasRemoveDefaultValue) {
				addActionMessage(MgrUtil.getUserMessage(
						OBJECTS_REMOVED_WITH_DEFAULT, count + ""));
			} else {
				addActionMessage(MgrUtil.getUserMessage(OBJECTS_REMOVED, count
						+ ""));
			}
		}

		return true;
	}

	protected boolean checkDoRemoveForSelected(Class<? extends HmBo> radiusAttr,
			long id) throws Exception {
		boolean flag = true;
		String sql = "select count(id) from " +
				"config_template where radius_attrs_id = " + id;
		List<?> boIds = QueryUtil.executeNativeQuery(sql);
		for(Object ob:boIds){
			if(!ob.toString().equals("0")) flag = false;
		}
		return flag;
	}

	/**
	 * update the embedded global ConfigTemplate object in easy mode of HM
	 * before removing BOs.
	 *
	 * This method should be override in sub-class of BaseAction where updating
	 * ConfigTemplate is needed.
	 *
	 * @throws Exception
	 *             -
	 * @author Joseph Chen
	 */
	protected void updateConfigTemplate() throws Exception {

	}

	/**
	 * Get the ids which cannot be removed in home domain
	 *
	 * @return The ids which cannot be removed in home domain
	 * @throws Exception
	 *             -
	 */
	public Collection<Long> getNonHomeDataInHomeDomain() throws Exception {
		domainId = QueryUtil.getDependentDomainFilter(userContext);

		Collection<Long> cannotIds = new ArrayList<Long>();
		List<?> boIds = QueryUtil.executeQuery("select id, owner.id from "
				+ boClass.getSimpleName(), null, null);
		for (Object obj : boIds) {
			Object[] item = (Object[]) obj;
			if (!domainId.equals(item[1])) {
				cannotIds.add((Long) item[0]);
			}
		}
		return cannotIds.isEmpty() ? null : cannotIds;
	}

	/**
	 * Get the ids which cannot be removed in home domain
	 *
	 * @param hmBoClass
	 *            -
	 * @return The ids which cannot be removed in home domain
	 * @throws Exception
	 *             -
	 */
	public Collection<Long> getNonHomeDataInHomeDomain(
			Class<? extends HmBo> hmBoClass) throws Exception {
		domainId = QueryUtil.getDependentDomainFilter(userContext);

		Collection<Long> cannotIds = new ArrayList<Long>();
		List<?> boIds = QueryUtil.executeQuery("select id, owner.id from "
				+ boClass.getSimpleName(), null, null);
		for (Object obj : boIds) {
			Object[] item = (Object[]) obj;
			if (!domainId.equals(item[1])) {
				cannotIds.add((Long) item[0]);
			}
		}
		return cannotIds.isEmpty() ? null : cannotIds;
	}

	protected boolean checkUsedProfile(Collection<Long> toRemoveIds) {
		if (getSelectedL2FeatureKey().equals(L2_FEATURE_SSID_PROFILES)) {
			for (Long rmId : toRemoveIds) {
				Set<String> configNames = ConfigurationUtils.getRelevantConfigTemplateFromSsid(rmId.toString());
				if (!configNames.isEmpty()) {
					String msg = "";
					for (String name : configNames) {
						if (!msg.equals("")) {
							msg = msg + ",";
						}
						msg = msg + "'" + name + "'";
					}
					List<?> boNames = QueryUtil.executeQuery(
							"select ssidName from "
									+ SsidProfile.class.getSimpleName(), null,
							new FilterParams("id", rmId));
					String tempStr[] = { boNames.get(0).toString(), msg };
					addActionError(getText("error.objectInUseConfigTemplate",
							tempStr));
					return false;
				}
			}
		}
		return true;
	}

	protected boolean checkCVGUsed(Collection<Long> toRemoveIds) {
		if ((getSelectedL2FeatureKey().equals(L2_FEATURE_MANAGED_HIVE_APS)
				|| getSelectedL2FeatureKey().equals(L2_FEATURE_VPN_GATEWAYS)
				|| getSelectedL2FeatureKey().equals(L2_FEATURE_CONFIG_HIVE_APS)
				|| getSelectedL2FeatureKey().equals(L2_FEATURE_CONFIG_VPN_GATEWAYS))
				&& toRemoveIds != null
				&& !toRemoveIds.isEmpty()) {
			String rmIds = null;
			boolean bindCvg = false;
			for (Long rmId : toRemoveIds) {
				if (rmIds == null || "".equals(rmIds)) {
					rmIds = String.valueOf(rmId);
				} else {
					rmIds += "," + String.valueOf(rmId);
				}
			}
			if (rmIds != null && !"".equals(rmIds)) {
				rmIds = "(" + rmIds + ")";
			}
			String sqlStr = "select hiveApId from VPN_GATEWAY_SETTING where hiveApId in "
					+ rmIds;
			List<?> rmHiveApIds = QueryUtil.executeNativeQuery(sqlStr);
			for (Object rmId : rmHiveApIds) {
				Long id = Long.valueOf(String.valueOf(rmId));
				toRemoveIds.remove(id);
				bindCvg = true;
			}
			if (bindCvg) {
				addActionError(getText("error.hiveap.cvg.rmError.beBind"));
				return false;
			}
		}
		return true;
	}

	/*
	 * Commonly used request params
	 */
	protected String operation;

	protected String key;

	protected Long id;

	protected Long selectedId;

	protected List<Long> selectedIds;

	private Set<Long> allSelectedIds;

	private Set<Long> pageIds;

	protected boolean allItemsSelected;

	protected String orderBy;

	protected boolean ascending;

	protected String extOrderBy;

	protected boolean extAscending;

	protected boolean orderByNumber;

	protected boolean orderByIp;

	protected int tabId;

	protected String forward;

	protected Long paintbrushSource;

	protected String paintbrushSourceName; // for display used

	protected Long start;

	// specified action may triggered by many redirect actions, this indicates where is the source.
	protected String ts; 

	// -------- For Notification Message ---start--//
	protected boolean autoPlayMsg;

	// -------- For Notification Message ---end---//

	protected String baseRedirectUrl;

	protected boolean messagePermanent;

	public boolean getMessagePermanent() {
		return messagePermanent;
	}

	public String getBaseRedirectUrl() {
		return baseRedirectUrl;
	}

	public void setBaseRedirectUrl(String baseRedirectUrl) {
		this.baseRedirectUrl = baseRedirectUrl;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public void setPaintbrushSource(Long paintbrushSource) {
		this.paintbrushSource = paintbrushSource;
	}

	public Long getPaintbrushSource() {
		return paintbrushSource;
	}

	public String getPaintbrushSourceName() {
		return paintbrushSourceName;
	}

	public void setPaintbrushSourceName(String paintbrushSourceName) {
		this.paintbrushSourceName = paintbrushSourceName;
	}

	public String getForward() {
		return forward;
		// return getLstForward();
	}

	public void setForward(String forward) {
		this.forward = forward;
	}

	public int getTabId() {
		return tabId;
	}

	public void setTabId(int tabId) {
		this.tabId = tabId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSelectedId() {
		return selectedId;
	}

	public void setSelectedId(Long selectedId) {
		this.selectedId = selectedId;
	}

	public List<Long> getSelectedIds() {
		return selectedIds;
	}

	public void setSelectedIds(List<Long> selectedIds) {
		log.debug("setSelectedIds", "Entered: " + selectedIds);
		this.selectedIds = selectedIds;
	}

	public Long getFirstSelectedId() {
		if (selectedIds != null && !selectedIds.isEmpty()) {
			return selectedIds.get(0);
		} else {
			return null;
		}
	}

	public Set<Long> getAllSelectedIds() {
		return allSelectedIds;
	}

	public void setAllSelectedIds(Set<Long> carryIds) {
		this.allSelectedIds = carryIds;
	}

	public Set<Long> getPageIds() {
		return pageIds;
	}

	public void setPageIds(Set<Long> pageIds) {
		this.pageIds = pageIds;
	}

	public boolean isAllItemsSelected() {
		return allItemsSelected;
	}

	public void setAllItemsSelected(boolean allSelected) {
		this.allItemsSelected = allSelected;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setExtAscending(boolean extAscending) {
		this.extAscending = extAscending;
	}

	public void setExtOrderBy(String extOrderBy) {
		this.extOrderBy = extOrderBy;
		this.orderBy = "id";
	}

	/*
	 * BO mgmt APIs
	 */

	protected String createBo() throws Exception {
		createBo(dataSource);
		return prepareBoList();
	}

	protected String editBo() throws Exception {
		setSessionDataSource(findBoById(boClass, id));
		if (dataSource == null) {
			return prepareBoList();
		} else {
			return INPUT;
		}
	}

	protected String editBo(QueryBo queryBo) throws Exception {
		setSessionDataSource(findBoById(boClass, id, queryBo));
		if (dataSource == null) {
			return prepareBoList();
		} else {
			return INPUT;
		}
	}

	protected String updateBo() throws Exception {
		updateBo(dataSource);
		return prepareBoList();
	}

	/*
	 * Create Hive Manager Business Object
	 */
	protected Long createBo(HmBo hmBo) throws Exception {
		if (hmBo == null || hmBo.getId() != null) {
			throw new HmException(
					"Create object failed, session must have been shared by another browser window.",
					HmMessageCodes.STALE_SESSION_OBJECT,
					new String[] { "Create" });
		}
		Long thisId;
		try {
			thisId = BoMgmt.createBo(hmBo, getUserContext(),
					getSelectedL2FeatureKey());
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.base.operation.create",new String[]{getLastTitle(),hmBo.getLabel()}));
		} catch (Exception e) {
			generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.base.operation.create",new String[]{getLastTitle(),hmBo.getLabel()}));
			throw e;
		}
		addActionMessage(MgrUtil
				.getUserMessage(OBJECT_CREATED, hmBo.getLabel()));
		return thisId;
	}

	/*
	 * Update Hive Manager Business Object
	 */
	protected <E extends HmBo> E updateBo(E hmBo) throws Exception {
		if (hmBo == null || hmBo.getId() == null || !hmBo.getId().equals(id)) {
			throw new HmException(
					"Update object failed, session must have been shared by another browser window.",
					HmMessageCodes.STALE_SESSION_OBJECT,
					new String[] { "Update" });
		}
		try {
			Date oldVer = hmBo.getVersion();
			hmBo = BoMgmt.updateBo(hmBo, getUserContext(),
					getSelectedL2FeatureKey());
			if (hmBo instanceof LocalUser) {
				log.info("updateBo",
						"Update LocalUser is customized for configuration indication.");
			} else {
				// generate an event to configuration indication process
				HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
						hmBo, ConfigurationChangedEvent.Operation.UPDATE,
						oldVer));
			}
			generateAuditLog(HmAuditLog.STATUS_SUCCESS,  MgrUtil.getUserMessage("hm.audit.log.base.operation.update",new String[]{getLastTitle(),hmBo.getLabel()}));
		} catch (Exception e) {
			generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.base.operation.update",new String[]{getLastTitle(),hmBo.getLabel()}));
			throw e;
		}
		addActionMessage(MgrUtil
				.getUserMessage(OBJECT_UPDATED, hmBo.getLabel()));
		return hmBo;
	}

	// update BO and send match and mismatch event.
	public <T extends HmBo> T updateBoWithEvent(T hmBo) throws Exception {
		String title = hmBo.getClass().getSimpleName();
		if (hmBo instanceof SsidProfile) {
			title = "SSIDs";
		} else if(hmBo instanceof LanProfile) {
			title = "LANs";
		} else if (hmBo instanceof ConfigTemplate) {
			title = "Network Policies";
		}
		try {
			Date oldVer = hmBo.getVersion();
			hmBo = QueryUtil.updateBo(hmBo);
			if (hmBo instanceof LocalUser) {
				log.info("updateBo",
						"Update LocalUser is customized for configuration indication.");
			} else {
				// generate an event to configuration indication process
				HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
						hmBo, ConfigurationChangedEvent.Operation.UPDATE,
						oldVer));
			}
			generateAuditLog(HmAuditLog.STATUS_SUCCESS,  MgrUtil.getUserMessage("hm.audit.log.base.operation.update",new String[]{title,hmBo.getLabel()}));
		} catch (Exception e) {
			generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.base.operation.update",new String[]{title,hmBo.getLabel()}));
			throw e;
		}
		return hmBo;
	}

	/*
	 * Find Hive Manager Business Object by ID
	 */
	protected <E extends HmBo> E findBoById(Class<E> boClass, Long id)
			throws Exception {
		return BoMgmt.findBoById(boClass, id, getUserContext(),
				getSelectedL2FeatureKey());
	}

	/*
	 * Find Hive Manager Business Object by ID, call QueryBo.load
	 */
	protected <E extends HmBo> E findBoById(Class<E> boClass, Long id,
			QueryBo queryBo) throws Exception {
		return BoMgmt.findBoById(boClass, id, queryBo, getUserContext(),
				getSelectedL2FeatureKey());
	}

	/*
	 * Remove Hive Manager Business Objects by ID
	 */
	protected int removeBos(Class<? extends HmBo> boClass, Collection<Long> ids)
			throws Exception {
		int count = BoMgmt.removeBos(boClass, ids, getUserContext(),
				getSelectedL2FeatureKey());
		if (count > 0) {
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.base.operation.remove") + " "+ count + " "
					+ convertL2FeatureDescription(getSelectedL2Feature().getDescription(), count));
		}
		return count;
	}

	protected String convertL2FeatureDescription(String desc, int count){
		if (desc==null) {
			return "unknown profiles";
		}

		if (count>1) {
			return desc;
		} else {
			if (desc.equalsIgnoreCase("Alarms")) {
				return "Alarm";
			} else if (desc.equalsIgnoreCase("Events")){
				return "Event";
			}
			return desc;
		}
	}

	/*
	 * Remove Hive Manager Business Objects by class
	 */
	/*- the function is not used already, next function instead.
	 protected int removeAllBos(Class<? extends HmBo> boClass,
	 Collection<Long> defaultIds) throws Exception {
	 int count = BoMgmt.removeAllBos(boClass, null, getUserContext(),
	 defaultIds, getSelectedL2FeatureKey());
	 if (count > 0) {
	 generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.base.operation.remove") + count + " "
	 + getSelectedL2Feature().getDescription());
	 }
	 return count;
	 }*/

	/**
	 * Remove HiveManager business objects excluding default ones by class and
	 * filter
	 *
	 * @param boClass
	 *            the Class object of business object
	 * @param filterParams
	 *            filter parameters
	 * @param defaultIds
	 *            collection of id of default objects
	 * @return the count of objects have been removed
	 * @throws Exception
	 *             -
	 * @author Joseph Chen
	 */
	protected int removeAllBos(Class<? extends HmBo> boClass,
			FilterParams filterParams, Collection<Long> defaultIds)
			throws Exception {
		int count = BoMgmt.removeAllBos(boClass, filterParams,
				getUserContext(), defaultIds, getSelectedL2FeatureKey());
		if (count > 0) {
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.base.operation.remove") + " "+ count + " "
					+ convertL2FeatureDescription(getSelectedL2Feature().getDescription(), count));
		}
		return count;
	}

	/*
	 * Find Hive Manager Business Objects by page
	 */
	public List<? extends HmBo> findBos() throws Exception {
		return BoMgmt.findBos(paging, sortParams, filterParams,
				getUserContext(), getSelectedL2FeatureKey());
	}

	/*
	 * Find Hive Manager Business Objects by page
	 */
	public List<? extends HmBo> findBos(QueryBo queryBo) throws Exception {
		return BoMgmt.findBos(paging, sortParams, filterParams,
				getUserContext(), getSelectedL2FeatureKey(), queryBo);
	}

	/*
	 * Find Hive Manager Business Objects by page
	 */
	public List<? extends HmBo> findBos(Long domainId) throws Exception {
		return BoMgmt.findBos(paging, sortParams, filterParams,
				getUserContext(), getSelectedL2FeatureKey(), domainId);
	}

	/*
	 * Find Hive Manager Business Objects by class
	 */
	public <E extends HmBo> List<E> findBos(Class<E> boClass) throws Exception {
		return BoMgmt.findBos(boClass, sortParams, getUserContext(),
				getSelectedL2FeatureKey());
	}

	/*
	 * Find Hive Manager Business Objects by page
	 */
	public List<?> findBos(String sql) throws Exception {
		return BoMgmt.findBos(paging, sql, sortParams, filterParams,
				getUserContext(), getSelectedL2FeatureKey());
	}

	/*
	 * Find Hive Manager Business Objects by class
	 */
	public <E extends HmBo> List<E> findBos(Class<E> boClass,
			SortParams sortParams) throws Exception {
		return QueryUtil.executeQuery(boClass, sortParams, null,
				QueryUtil.getDependentDomainFilter(userContext));
	}

	/*
	 * Find Topology Map Hierarchy by dependent domain filter.
	 */
	public List<CheckItem> getMapListView() {
		// if (domainId == null) {
		// domainId = QueryUtil.getDependentDomainFilter(userContext);
		// }
		return BoMgmt.getMapMgmt().getMapListView(
				QueryUtil.getDependentDomainFilter(userContext));
	}

	/*
	 * set the Name editable
	 */
	public boolean getDisabledName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	/*
	 * Check if BO with attribute 'name' and value 'value' already exists.
	 */
	public boolean checkNameExists(String name, Object value) {
		// if (domainId == null) {
		// domainId = QueryUtil.getDependentDomainFilter(userContext);
		// }

		List<?> boIds = QueryUtil.executeQuery(
				"select id from " + boClass.getSimpleName(), null,
				new FilterParams(name, value), domainId);
		if (!boIds.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("error.objectExists",
					value.toString()));
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Check if BO with attribute 'name' and value 'value' already exists.
	 */
	public boolean checkNameExists(String name, Object value,
			Class<? extends HmBo> boClass) {
		// if (domainId == null) {
		// domainId = QueryUtil.getDependentDomainFilter(userContext);
		// }

		List<?> boIds = QueryUtil.executeQuery(
				"select id from " + boClass.getSimpleName(), null,
				new FilterParams(name, value), domainId);
		if (!boIds.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("error.objectExists",
					value.toString()));
			return true;
		} else {
			return false;
		}
	}

	public boolean checkNameExistsIgnoreDomain(String name, Object value) {
		List<?> boIds = QueryUtil.executeQuery(
				"select id from " + boClass.getSimpleName(), null,
				new FilterParams(name, value));
		if (!boIds.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("error.objectExists",
					value.toString()));
			return true;
		} else {
			return false;
		}
	}

	public boolean checkNameExists(String where, Object[] values) {
		// if (domainId == null) {
		// domainId = QueryUtil.getDependentDomainFilter(userContext);
		// }

		List<?> boIds = QueryUtil.executeQuery(
				"select id from " + boClass.getSimpleName(), null,
				new FilterParams(where, values), domainId);
		if (!boIds.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("error.objectExists",
					values[0].toString()));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * get check items for BOs by class check items are the pare of id and name
	 * of BOs this function could be used to get content to fill the Select
	 * control in GUI
	 *
	 * @param fieldName
	 *            name of the field
	 * @param boClass
	 *            Class of the BO
	 * @param filterPa
	 *            - filter parameter
	 * @param beginWith
	 *            indicate the format of the first item in the list under some
	 *            circumstances, the list could begin with an blank item or None
	 *            0 - no special item 1 - blank
	 * @param endWith
	 *            indicate the format of the last item in the list under some
	 *            circumstances, the list could end with item "[-New-]" 0 - no
	 *            special item 1 - New
	 *
	 * @return a List contains elements of CheckItem. the returned list could be
	 *         null if error occurs.
	 *
	 * @author Joseph Chen
	 * @since 05/05/2008
	 */
	public List<CheckItem> getBoCheckItems(String fieldName,
			Class<? extends HmBo> boClass, FilterParams filterPa,
			short beginWith, short endWith) {
		return getBoCheckItems(fieldName, boClass, filterPa, new SortParams(
				"id"), beginWith, endWith, true);
	}

	private List<CheckItem> getBoCheckItems(String fieldName,
			Class<? extends HmBo> boClass, FilterParams filterPa,
			SortParams sortParams, short beginWith, short endWith, boolean escape) {
		// check input params
		if (fieldName == null || fieldName.isEmpty() || boClass == null) {
			return null;
		}

		// get list of id and name from database
		String sql = "SELECT bo.id, bo." + fieldName + " FROM "
				+ boClass.getSimpleName() + " bo";
		List<?> bos = QueryUtil.executeQuery(sql, sortParams, filterPa,
				domainId);

		List<CheckItem> items = new ArrayList<CheckItem>();

		// some ssid cannot to be used
		boolean ifIsSsid = boClass.getName().equals(
				"com.ah.bo.wlan.SsidProfile");
		for (Object obj : bos) {
			Object[] item = (Object[]) obj;
			String profileName = (String) item[1];
			if (ifIsSsid) {
				if (BeParaModule.SSID_PROFILE_TEMPLATE_SYMBOL_SCANNER
						.equals(profileName)
						|| BeParaModule.SSID_PROFILE_TEMPLATE_LEGACY_CLIENTS
								.equals(profileName)
						|| BeParaModule.SSID_PROFILE_TEMPLATE_HIGH_CAPACITY
								.equals(profileName)
						|| BeParaModule.SSID_PROFILE_TEMPLATE_BLACK_BERRY
								.equals(profileName)
						|| BeParaModule.SSID_PROFILE_TEMPLATE_SPECTRA_LINK
								.equals(profileName)) {
					continue;
				}
			}
			CheckItem checkItem = new CheckItem((Long) item[0], escape ? StringEscapeUtils.escapeHtml4(profileName) : profileName);
			items.add(checkItem);
		}

		// add the special item
		switch (beginWith) {
		case CHECK_ITEM_BEGIN_BLANK:
			if (boClass.getName().equals(Vlan.class.getName())) {
				items.add(0, new CheckItem((long) CHECK_ITEM_ID_BLANK,
						"Create new VLAN"));
			} else {
				items.add(0, new CheckItem((long) CHECK_ITEM_ID_BLANK, ""));
			}
			break;
		case CHECK_ITEM_BEGIN_NO:
			// fall through
		default:
			break;
		}

		if (items.isEmpty()) {
			items.add(new CheckItem((long) CHECK_ITEM_ID_NONE, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}

		// add the special item
		switch (endWith) {
		case CHECK_ITEM_END_NEW:
			if (this.getWriteDisabled().isEmpty())
				items.add(new CheckItem((long) CHECK_ITEM_ID_NEW,
						CHECK_ITEM_NEW));
			break;
		case CHECK_ITEM_END_NO:
			// fall through
		default:
			break;
		}
		return items;
	}

	/**
	 * get check items for BOs by class check items are the pare of id and name
	 * of BOs this function could be used to get content to fill the Select
	 * control in GUI
	 *
	 * @param fieldName
	 *            name of the field
	 * @param boClass
	 *            Class of the BO
	 * @param filterPar
	 *            filter parameter
	 *
	 * @return a List contains elements of CheckItem. the returned list could be
	 *         null if error occurs.
	 *
	 * @author Joseph Chen
	 * @since 05/05/2008
	 */
	public List<CheckItem> getBoCheckItems(String fieldName,
			Class<? extends HmBo> boClass, FilterParams filterPar) {
		return getBoCheckItems(fieldName, boClass, filterPar,
				CHECK_ITEM_BEGIN_NO, CHECK_ITEM_END_NO);
	}

	public List<CheckItem> getBoCheckItems(String fieldName,
			Class<? extends HmBo> boClass, FilterParams filterPar,
			SortParams sortParams) {
		return getBoCheckItems(fieldName, boClass, filterPar, sortParams,
				CHECK_ITEM_BEGIN_NO, CHECK_ITEM_END_NO, true);
	}

	public List<CheckItem> getBoCheckItemsSort(String fieldName,
			Class<? extends HmBo> boClass, FilterParams filterPar,
			SortParams sortParams, short beginWith, short endWith) {
		return getBoCheckItems(fieldName, boClass, filterPar, sortParams,
				beginWith, endWith, true);
	}

	public List<String> getLstTitle() {
		if (MgrUtil.getSessionAttribute("lstTitle") != null) {
			return (List<String>) MgrUtil.getSessionAttribute("lstTitle");
		} else {
			return new ArrayList<String>();
		}
	}

	public boolean getFirstTitleStartLocalUser() {
		return getLstTitle() != null && !getLstTitle().isEmpty()
				&& getLstTitle().get(0).startsWith("Local Users");
	}

	public String getLastTitle() {
		String titleName;
		if (null != getLstTitle() && getLstTitle().size() > 1) {
			titleName = getLstTitle().get(getLstTitle().size() - 1);
			if (titleName.split(">").length > 2) {
				titleName = titleName.substring(titleName.indexOf(">") + 1,
						titleName.lastIndexOf(">"));
			} else {
				titleName = getSelectedL2Feature().getDescription();
			}
		} else {
			titleName = getSelectedL2Feature().getDescription();
		}
		return titleName.trim();
	}

	private boolean removeAllLstTitle;

	public void addLstTitle(String strTitle) {
		if (removeAllLstTitle) {
			MgrUtil.removeSessionAttribute("lstTitle");
		}

		List<String> tmpLstTitle = (List<String>) MgrUtil
				.getSessionAttribute("lstTitle");
		if (tmpLstTitle == null) {
			tmpLstTitle = new ArrayList<String>();
		}
		if (tmpLstTitle.isEmpty()) {
			tmpLstTitle.add(strTitle);
			MgrUtil.setSessionAttribute("lstTitle", tmpLstTitle);
		} else if (!tmpLstTitle.get(tmpLstTitle.size() - 1).equals(
				" > " + strTitle)
				&& !tmpLstTitle.get(tmpLstTitle.size() - 1).equals(strTitle)) {
			tmpLstTitle.add(" > " + strTitle);
			MgrUtil.setSessionAttribute("lstTitle", tmpLstTitle);
		}
	}

	public void addLstTabId(Integer strTabId) {
		List<Integer> tmpLstTabId = (List<Integer>) MgrUtil
				.getSessionAttribute("lstTabId");
		if (tmpLstTabId == null) {
			tmpLstTabId = new ArrayList<Integer>();
		}
		tmpLstTabId.add(strTabId);
		MgrUtil.setSessionAttribute("lstTabId", tmpLstTabId);
	}

	public void addLstForward(String strForward) {
		List<String> tmpLstForward = (List<String>) MgrUtil
				.getSessionAttribute("lstForward");
		if (tmpLstForward == null) {
			tmpLstForward = new ArrayList<String>();
		}
		tmpLstForward.add(strForward);
		MgrUtil.setSessionAttribute("lstForward", tmpLstForward);
		// add last form change state
		addLstFormChanged(formChanged);
	}

	private void addLstFormChanged(boolean changed) {
		List<Boolean> tmpLstFormChanged = (List<Boolean>) MgrUtil
				.getSessionAttribute("lstFormChanged");
		if (tmpLstFormChanged == null) {
			tmpLstFormChanged = new ArrayList<Boolean>();
		}
		tmpLstFormChanged.add(changed);
		MgrUtil.setSessionAttribute("lstFormChanged", tmpLstFormChanged);
	}

	public void removeLstTitle() {
		List<String> tmpLstTitle = (List<String>) MgrUtil
				.getSessionAttribute("lstTitle");
		if (tmpLstTitle == null || tmpLstTitle.isEmpty()) {
			return;
		}
		tmpLstTitle.remove(tmpLstTitle.size() - 1);
		MgrUtil.setSessionAttribute("lstTitle", tmpLstTitle);
	}

	public void removeLstTabId() {
		List<String> tmpLstTabId = (List<String>) MgrUtil
				.getSessionAttribute("lstTabId");
		if (tmpLstTabId == null || tmpLstTabId.isEmpty()) {
			return;
		}
		tmpLstTabId.remove(tmpLstTabId.size() - 1);
		MgrUtil.setSessionAttribute("lstTabId", tmpLstTabId);
	}

	public void removeLstForward() {
		List<String> tmpLstForward = (List<String>) MgrUtil
				.getSessionAttribute("lstForward");
		if (tmpLstForward == null || tmpLstForward.isEmpty()) {
			return;
		}
		tmpLstForward.remove(tmpLstForward.size() - 1);
		MgrUtil.setSessionAttribute("lstForward", tmpLstForward);
		// get last form change state
		formChanged = removeLstFormChanged();
	}

	private boolean removeLstFormChanged() {
		boolean lastValue;
		List<Boolean> tmpLstFormChanged = (List<Boolean>) MgrUtil
				.getSessionAttribute("lstFormChanged");
		if (tmpLstFormChanged == null || tmpLstFormChanged.isEmpty()) {
			lastValue = false;
		}
		lastValue = tmpLstFormChanged.remove(tmpLstFormChanged.size() - 1);
		MgrUtil.setSessionAttribute("lstFormChanged", tmpLstFormChanged);
		return lastValue;
	}

	public String getLstForward() {
		List<String> tmpLstForward = (List<String>) MgrUtil
				.getSessionAttribute("lstForward");
		if (tmpLstForward != null && !tmpLstForward.isEmpty()) {
			return tmpLstForward.get(tmpLstForward.size() - 1);
		} else {
			return "";
		}
	}

	public int getLstTabId() {
		List<Integer> tmpLstTabId = (List<Integer>) MgrUtil
				.getSessionAttribute("lstTabId");
		if (tmpLstTabId != null && !tmpLstTabId.isEmpty()) {
			return tmpLstTabId.get(tmpLstTabId.size() - 1);
		} else {
			return 0;
		}
	}

	public String getLastExConfigGuide() {
		return (String) MgrUtil.getSessionAttribute("exConfigGuide");
	}

	public void setLastExConfigGuide(String feature) {
		MgrUtil.setSessionAttribute("exConfigGuide", feature);
	}

	public void removeLastExConfigGuide() {
		MgrUtil.removeSessionAttribute("exConfigGuide");
	}

	public String getExConfigGuideFeature() {
		return (String) MgrUtil.getSessionAttribute("exConfigGuideFeature");
	}

	public void setExConfigGuideFeature(String exConfigGuide) {
		MgrUtil.setSessionAttribute("exConfigGuideFeature", exConfigGuide);
		this.setLastExConfigGuide(exConfigGuide);
	}

	public void removeExConfigGuideFeature() {
		MgrUtil.removeSessionAttribute("exConfigGuideFeature");
	}

	public String getSlideStyleStatus() {
		if (L2_FEATURE_CONFIGURATION_GUIDE.equals(getSelectedL2FeatureKey())
				&& !"top".equals(ts)) {
			return "collapsed";
		}
		String result="expanded";
		if (this.userContext == null)
			return result;
		try {

			result=NavigationCustomizationUtil.isNeedDisplay(
					NavigationCustomizationUtil.getNavCustomizationByUser(
							this.userContext.getId(),
							this.userContext.getEmailAddress()), getMenuId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 *
	 * @return -
	 */
	public int getMenuId() {
		if(this.getSelectedL1Feature()==null)return 1;
		return NavigationCustomizationUtil.getMenuIdByName(this.getSelectedL1Feature().getKey());
	}

	public String getMenuDisplayStyleString() {
		return "collapsed".equals(getSlideStyleStatus()) ? "none" : "";
	}

	/*
	 * Check if BO with attribute 'name' and value 'value' already exists while
	 * do update operation.
	 */
	public boolean checkNameExistsWhileUpdate(Long currentId, String name,
			Object value) {
		List<?> boIds = QueryUtil.executeQuery(
				"select id from " + boClass.getSimpleName(), null,
				new FilterParams(name, value),domainId);
		if (!boIds.isEmpty()) {
			for (Object obj : boIds) {
				Long id = (Long) obj;
				if (id.longValue() != currentId.longValue()) {
					addActionError(MgrUtil.getUserMessage(
							"error.constraintViolation", value.toString()));
					return true;
				}
			}
		}
		return false;
	}

	public boolean checkNameExistsWhileUpdate(Long currentId, String where,
			Object[] values) {
		List<?> boIds = QueryUtil.executeQuery(
				"select id from " + boClass.getSimpleName(), null,
				new FilterParams(where, values), domainId);
		if (!boIds.isEmpty()) {
			for (Object obj : boIds) {
				Long id = (Long) obj;
				if (id.longValue() != currentId.longValue()) {
					addActionError(MgrUtil.getUserMessage(
							"error.constraintViolation", values[0].toString()));
					return true;
				}
			}
		}
		return false;
	}

	public boolean setTitleAndCheckAccess(String strTitle) {
		List<String> tmpLstTitle = (List<String>) MgrUtil
				.getSessionAttribute("lstTitle");
		if (tmpLstTitle != null && !tmpLstTitle.isEmpty()) {
			if (!tmpLstTitle.get(tmpLstTitle.size() - 1).equals(
					" > " + strTitle)
					&& !tmpLstTitle.get(tmpLstTitle.size() - 1)
							.equals(strTitle)) {
				tmpLstTitle.add(" > " + strTitle);
			}
		} else {
			tmpLstTitle = new ArrayList<String>();
			tmpLstTitle.add(strTitle);
		}
		MgrUtil.setSessionAttribute("lstTitle", tmpLstTitle);
		try {
			/*
			 * if the request is for displaying HM search result, permit it.
			 */
			if (this.getSearchResult() != null) {
				return true;
			}

			AccessControl.checkUserAccess(getUserContext(),
					getSelectedL2FeatureKey(), CrudOperation.CREATE);
		} catch (HmException ex) {
			MgrUtil.setSessionAttribute("errorMessage",
					MgrUtil.getUserMessage(ex));
			return false;
		}
		return true;
	}

	public long getCriticalAlarmCount() {
		return SystemStatusCache.getInstance().getAlarmCount(
				AhAlarm.AH_SEVERITY_CRITICAL, userContext);
	}

	public long getMajorAlarmCount() {
		return SystemStatusCache.getInstance().getAlarmCount(
				AhAlarm.AH_SEVERITY_MAJOR, userContext);
	}

	public long getMinorAlarmCount() {
		return SystemStatusCache.getInstance().getAlarmCount(
				AhAlarm.AH_SEVERITY_MINOR, userContext);
	}

	public long getClearedAlarmCount() {
		return SystemStatusCache.getInstance().getAlarmCount(
				AhAlarm.AH_SEVERITY_UNDETERMINED, userContext);
	}

	public long getNewHiveAPCount() {
		return SystemStatusCache.getInstance().getNewHiveAPCount(userContext);
	}

	public long getRogueClientCount() {
		return SystemStatusCache.getInstance().getRogueClientCount(userContext);
	}

	public long getInnetRogueCount() {
		return SystemStatusCache.getInstance().getInnetRogueCount(userContext);
	}

	public long getOnmapRogueCount() {
		return SystemStatusCache.getInstance().getOnmapRogueCount(userContext);
	}

	public long getStrongRogueCount() {
		return SystemStatusCache.getInstance().getStrongRogueCount(userContext);
	}

	public long getWeakRogueCount() {
		return SystemStatusCache.getInstance().getWeakRogueCount(userContext);
	}

	public boolean getUpdateContext() {
		Boolean update = (Boolean) MgrUtil
				.getSessionAttribute(SessionKeys.UPDATE_CONTEXT);
		return update != null && update;
	}

	public void setUpdateContext(boolean value) {
		MgrUtil.setSessionAttribute(SessionKeys.UPDATE_CONTEXT, value);
	}

	/**
	 * @author fxr
	 * @param arg_Status
	 *            : HmAuditLog.STATUS_SUCCESS;HmAuditLog.STATUS_FAILURE
	 * @param arg_Comment
	 *            : the comment of this operation
	 */
	public void generateAuditLog(short arg_Status, String arg_Comment) {
		// passive node cannot operate database
		if (HAUtil.isSlave()) {
			return;
		}
		HmAuditLog log = new HmAuditLog();
		log.setStatus(arg_Status);
		String commentStr = checkValueLength(arg_Comment, 256);
		log.setOpeationComment(commentStr);
		log.setHostIP(null == request ? "127.0.0.1" : HmProxyUtil
				.getClientIp(request));
		try {
			HmDomain domain = null;
			if (null != userContext) {
				log.setUserOwner(userContext.getUserName());
				if (null == domainId) {
					domainId = QueryUtil.getDependentDomainFilter(userContext);
				}
				domain = QueryUtil.findBoById(HmDomain.class, domainId);
				log.setOwner(domain);
			}
			log.setLogTimeStamp(System.currentTimeMillis());
			log.setLogTimeZone(domain != null ? domain.getTimeZoneString()
					: TimeZone.getDefault().getID());

			BeLogTools.info(HmLogConst.M_GUIAUDIT, "[" + log.getHostIP() + " "
					+ log.getOwner() + "." + log.getUserOwner() + "]" + " "
					+ arg_Comment + ":" + arg_Status);

			QueryUtil.createBo(log);
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
	}

	private String checkValueLength(String arg_Comment, int len) {
		if (arg_Comment != null && arg_Comment.length() > len) {
			arg_Comment = arg_Comment.substring(0, len);
		}
		return arg_Comment;
	}

	public String getLocalHost() throws Exception {
		String localHost = null;
		try {
			localHost = HmBeOsUtil.getNetConfig().getIpAddress_eth0();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return localHost;
	}

	/*
	 * Get the help file information.
	 */
	public String getVideoGuideLink() {
		if (userContext.getUserGroup().getHelpURL() == null
				|| userContext.getUserGroup().getHelpURL().equals("")) {
			if (isEasyMode()) {
				return NmsUtil.getOEMCustomer().getHelpLink()
						+ "/"
						+ NmsUtil.getOEMCustomer().getNmsNameAbbreviation()
								.toLowerCase() + "/express/Content/ref/doc.htm";
			} else {
				// if (isOEMSystem()) {
				// return NmsUtil.getOEMCustomer().getHelpLink()
				// + "/" +
				// NmsUtil.getOEMCustomer().getNmsNameAbbreviation().toLowerCase()
				// + "/Content/ref/doc.htm";
				// } else {
				return NmsUtil.getOEMCustomer().getHelpLink()
						+ "/"
						+ NmsUtil.getOEMCustomer().getNmsNameAbbreviation()
								.toLowerCase() + "/full/Content/ref/doc.htm";
				// }
			}

		}
		if (userContext.getUserGroup().getHelpURL().startsWith("http://")) {
			if (isEasyMode()) {
				return userContext.getUserGroup().getHelpURL()
						+ "/"
						+ NmsUtil.getOEMCustomer().getNmsNameAbbreviation()
								.toLowerCase() + "/express/Content/ref/doc.htm";
			} else {
				// if (isOEMSystem()) {
				// return userContext.getUserGroup().getHelpURL()
				// + "/" +
				// NmsUtil.getOEMCustomer().getNmsNameAbbreviation().toLowerCase()
				// + "/Content/ref/doc.htm";
				// } else {
				return userContext.getUserGroup().getHelpURL()
						+ "/"
						+ NmsUtil.getOEMCustomer().getNmsNameAbbreviation()
								.toLowerCase() + "/full/Content/ref/doc.htm";
				// }
			}
		} else {
			if (isEasyMode()) {
				return "file:///"
						+ userContext.getUserGroup().getHelpURL()
						+ "/"
						+ NmsUtil.getOEMCustomer().getNmsNameAbbreviation()
								.toLowerCase() + "/express/Content/ref/doc.htm";
			} else {
				// if (isOEMSystem()) {
				// return "file:///" + userContext.getUserGroup().getHelpURL()
				// + "/" +
				// NmsUtil.getOEMCustomer().getNmsNameAbbreviation().toLowerCase()
				// + "/Content/ref/doc.htm";
				// } else {
				return "file:///"
						+ userContext.getUserGroup().getHelpURL()
						+ "/"
						+ NmsUtil.getOEMCustomer().getNmsNameAbbreviation()
								.toLowerCase() + "/full/Content/ref/doc.htm";
				// }
			}
		}
	}

	/*
	 * Get the help file information.
	 */
	public String getHelpLink() {
		String endWord;
		if (isPlanningOnly() || isPlanner()) {
			endWord = "/help_CSH.htm#planner.htm";
		} else {
			endWord = "/help_CSH.htm"
					+ getText("help.link." + selectedL2Feature.getKey());
		}
		if (userContext.getUserGroup().getHelpURL() == null
				|| userContext.getUserGroup().getHelpURL().equals("")) {
			return NmsUtil.getOEMCustomer().getHelpLink()
					+ getHMModeHelpString() + endWord;
		}
		if (userContext.getUserGroup().getHelpURL().startsWith("http://")) {
			return userContext.getUserGroup().getHelpURL()
					+ getHMModeHelpString() + endWord;
		} else {
			return "file:///" + userContext.getUserGroup().getHelpURL()
					+ getHMModeHelpString() + endWord;
		}
	}

	/*
	 * Get the support page link
	 */
	public String getSupportPageLink() {
		return NmsUtil.getSupportPageUrl();
	}

	public String getHMModeHelpString() {
		if (getUserContext().getUserGroup().getGroupName()
				.equals(HmUserGroup.GM_ADMIN)) {
			return "/um/admin";
		} else if (getUserContext().getUserGroup().getGroupName()
				.equals(HmUserGroup.GM_OPERATOR)) {
			return "/um/op";
		} else {
			if (isPlanningOnly() || isPlanner()) {
				return "/planner";
			} else {
				if (isEasyMode()) {
					return "/"
							+ NmsUtil.getOEMCustomer().getNmsNameAbbreviation()
									.toLowerCase() + "/express";
				} else {
					// if (isOEMSystem()) {
					// return "/" +
					// NmsUtil.getOEMCustomer().getNmsNameAbbreviation().toLowerCase();
					// } else {
					return "/"
							+ NmsUtil.getOEMCustomer().getNmsNameAbbreviation()
									.toLowerCase() + "/full";
					// }
				}
			}
		}
	}

	/*
	 * Get the version String append to imported js/css
	 */
	public String getVerParam() {
		return getVersionInfo() == null ? "-1" : getVersionInfo()
				.getMainVersion() + "." + getVersionInfo().getSubVersion();
	}

	public List<HmDomain> getSwitchDomains() {
		return CacheMgmt.getInstance().getCacheDomains();
	}

	public int getSwitchDomainDataSize() {
		if (getSwitchDomainData().size() % SwitchDomainAction.ONEPAGE_SIZE == 0) {
			return getSwitchDomainData().size()
					/ SwitchDomainAction.ONEPAGE_SIZE;
		}
		return getSwitchDomainData().size() / SwitchDomainAction.ONEPAGE_SIZE
				+ 1;
	}

	public List<HmDomain> getSwitchDomainDataCurrent() {
		List<HmDomain> domains = getSwitchDomainData();
		List<HmDomain> resultDomain = new ArrayList<HmDomain>();
		for (int i = 0; i < SwitchDomainAction.ONEPAGE_SIZE
				&& i < domains.size(); i++) {
			resultDomain.add(domains.get(i));
		}
		return resultDomain;
	}

	public List<HmDomain> getSwitchDomainData() {
		if (null == userContext || !userContext.isSuperUser()) {
			return new ArrayList<HmDomain>();
		}
		List<HmDomain> domains = getSwitchDomains();
		try {
			Long switchDomainId = null;
			if (userContext != null && userContext.getSwitchDomain() != null) {
				switchDomainId = userContext.getSwitchDomain().getId();
			}
			for (HmDomain domain : domains) {
				if (domain.getId().equals(switchDomainId)) {
					domain.setSelected(true);
				} else {
					domain.setSelected(false);
				}
			}
		} catch (Exception e) {
			log.error("getSwitchDomainData", e.getMessage());
		}

		// filter domain if user is vad admin
		if (userContext != null && userContext.isVadAdmin()) {
			for (Iterator<HmDomain> iterator = domains.iterator(); iterator
					.hasNext();) {
				HmDomain domain = iterator.next();

				// filter domain which not belong to this vad admin
				if (domain.getPartnerId() == null
						|| !domain.getPartnerId().equals(userContext.getCustomerId())) {
					iterator.remove();
				}
			}
		}
		HmDomain homeDomain = null;
		for (int i = 0; i < domains.size(); i++) {
			if (HmDomain.HOME_DOMAIN.equals(domains.get(i).getDomainName())) {
				homeDomain = domains.remove(i);
				break;
			}
		}
		// order by name
		Collections.sort(domains, new Comparator<HmDomain>() {
			@Override
			public int compare(HmDomain o1, HmDomain o2) {
				String n1 = o1.getDomainName();
				String n2 = o2.getDomainName();
				return n1.compareToIgnoreCase(n2);
			}
		});
		if (null != homeDomain) {
			domains.add(0, homeDomain);
		}
		return domains;
	}

	public List<HmDomain> getReassignDomains() {
		List<HmDomain> reassignDomains = new ArrayList<HmDomain>();
		try {
			List<HmDomain> domains = getSwitchDomains();
			for (HmDomain domain : domains) {
				if (HmDomain.HOME_DOMAIN.equals(domain.getDomainName())) {
					continue;
				}
				reassignDomains.add(domain);
			}
		} catch (Exception e) {
		}
		// order by name
		Collections.sort(reassignDomains, new Comparator<HmDomain>() {
			@Override
			public int compare(HmDomain o1, HmDomain o2) {
				String n1 = o1.getDomainName();
				String n2 = o2.getDomainName();
				return n1.compareToIgnoreCase(n2);
			}
		});
		return reassignDomains;
	}

	public boolean getShowReassignMenu() {
		return userContext != null
				&& userContext.isSuperUser()
				&& !getReassignDomains().isEmpty()
				&& (userContext.getSwitchDomain() == null || userContext
						.getSwitchDomain().isHomeDomain());
	}

	public boolean getShowLogoutMenu() {
		return userContext != null && userContext.isSuperUser() && !userContext.isRedirectUser()
				&& getSwitchDomains().size() > 1;
	}

	public boolean getShowDomain() {
		return userContext != null && userContext.isSuperUser()
				&& userContext.getSwitchDomain() == null
				&& getSwitchDomains().size() > 1;
	}

	public boolean getShowStagingSwitch() {
		if (null == userContext) {
			return false;
		}

		if (userContext.isVadAdmin()) {
			return true;
		}

		if (NmsUtil.isHostedHMApplication()) {
			if (null == userContext.getCustomerId()) {
				if (userContext.isRedirectUser()) {
					return true;
				} else if (!HmUserGroup.PLANNING.equals(userContext.getUserGroup().getGroupName())) {
					return userContext.isAccessMyhive();
				}
			} else {
				return true;
			}
		}
		return false;
	}

	public boolean getShowAuthenticationSwitch() {
		return null != userContext && NmsUtil.isHostedHMApplication()
				&& (userContext.getOwner().isHomeDomain());
	}

	public boolean getShowHiveApInfo() {
		if(HAUtil.isSlave()) {
			return false;
		}
		if (null == userContext) {
			return false;
		}

		if (isTeacherView()) {
			return false;
		}

		HmDomain hmDom = userContext.getSwitchDomain() == null ? userContext
				.getDomain() : userContext.getSwitchDomain();

		if (hmDom == null) {
			return false;
		}
		// the GUI is not relate with license
		// String domainName = hmDom.getDomainName();
		// if (HmDomain.HOME_DOMAIN.equals(domainName) ||
		// !NmsUtil.isHostedHMApplication()) {
		// return HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID ==
		// HmBeLicenseUtil.LICENSE_VALID;
		// } else {
		// LicenseInfo orderInfo = HmBeLicenseUtil.VHM_ORDERKEY_INFO
		// .get(domainName);
		// if (null == orderInfo) {
		// orderInfo = LicenseOperationTool.getInstance()
		// .getOrderKeyInfoFromDatabase(domainName, false);
		// }
		// if (BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(orderInfo
		// .getLicenseType())) {
		// return false;
		// } else if (orderInfo.getTotalDays() > 0
		// && orderInfo.getLeftHours() <= 0) {
		// return false;
		// }
		// return true;
		// }
		return true;
	}

	public boolean getIsInHomeDomain() {
		if (null == getUserContext()) {
			return false;
		}
		HmDomain hmDomain = getUserContext().getSwitchDomain() != null ? getUserContext()
				.getSwitchDomain() : getUserContext().getDomain();
		return HmDomain.HOME_DOMAIN.equals(hmDomain.getDomainName());
	}

	public boolean getShowSyncSGEMenu() {
		return getIsInHomeDomain() && AhAppContainer.getBeMiscModule().getAirTightSgeIntegrator().isStarted();
	}

	/**
	 * get domain object
	 *
	 * @return -
	 */
	public HmDomain getDomain() {
		return userContext.getSwitchDomain() == null ? userContext.getDomain()
				: userContext.getSwitchDomain();
	}

	public TimeZone getUserTimeZone() {
		return TimeZone.getTimeZone(userContext.getTimeZone());
	}

	protected Long domainId;

	protected short mode;

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public short getMode() {
		return mode;
	}

	public void setMode(short mode) {
		this.mode = mode;
	}

	public boolean isEasyMode() {
		return mode == HmStartConfig.HM_MODE_EASY;
	}

	public boolean isFullMode() {
		return mode == HmStartConfig.HM_MODE_FULL;
	}

	protected List<HmTableColumn> selectedColumns = new ArrayList<HmTableColumn>();

	protected List<Integer> selectedColumnIds;

	protected List<HmTableColumn> availableColumns = new ArrayList<HmTableColumn>();

	protected int tableId;

	protected int keyColumnId = NO_KEY_COLUMN;

	protected static final int NO_KEY_COLUMN = -1;

	public List<HmTableColumn> getSelectedColumns() {
		return selectedColumns;
	}

	public List<HmTableColumn> getAvailableColumns() {
		return availableColumns;
	}

	public void setSelectedColumnIds(List<Integer> selectedColumnIds) {
		this.selectedColumnIds = selectedColumnIds;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public boolean isPlanningOnly() {
		return userContext != null
				&& userContext.getUserGroup().isPlUserGroup();
	}

	public boolean isShowPlanningTool() {
		if (isPlanningOnly()) {
			List<PlanToolConfig> list = QueryUtil.executeQuery(
					PlanToolConfig.class, null, null, domainId);
			if (list.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	Boolean splitContainer;

	public boolean isSplitContainer() {
		if (splitContainer == null) {
			if (request == null) {
				splitContainer = false;
			} else {
				String userAgent = request.getHeader("user-agent");
				splitContainer = null != userAgent
						&& userAgent.contains("MSIE");
				splitContainer = false; // Not even on IE
			}
		}
		return splitContainer;
	}

	/*
	 * Track form change feature. When new/modify profile a inside profile b,
	 * save the profile b status(changed/unchanged), used to restore status when
	 * back from a to b.
	 */
	private boolean formChanged;

	public boolean isFormChanged() {
		return formChanged;
	}

	public void setFormChanged(boolean formChanged) {
		this.formChanged = formChanged;
	}

	public void resetPermission() {
		// if (getUserContext().getUserGroup().isAdministrator()) {
		// setReadPermission(true);
		// setWritePermission(true);
		// } else {
		if (getSelectedL2Feature() != null) {
			HmPermission featurePermission = getUserContext().getUserGroup()
					.getFeaturePermissions()
					.get(getSelectedL2Feature().getKey());
			if (featurePermission != null) {
				if (featurePermission.hasAccess(HmPermission.OPERATION_READ)) {
					setReadPermission(true);
				}
				if (featurePermission.hasAccess(HmPermission.OPERATION_WRITE)) {
					setWritePermission(true);
				}
			}
		}
		// }
	}

	public boolean isTrackFormChanges() {
		boolean track = true;
		// 1)user disabled prompt
		// 2)in the list view
		// 3)not have write permission
		// 4)not have dataSource(not persistent in DB)
		if (!userContext.isPromptChanges() || null != page
				|| getWriteDisable4Struts() || null == getDataSource()) {
			track = false;
		}
		return track;
	}

	public String getTrackChangeTitle() {
		return Navigation.getFeatureName(L2_FEATURE_USER_PASSWORD_MODIFY);
	}

	// get session timeout value
	public int getSessionTimeOut() {
		List<HMServicesSettings> list = QueryUtil.executeQuery(
				HMServicesSettings.class, null, new FilterParams("owner.id",
						getDomainId()));
		if (list.isEmpty()) {
			return NmsUtil.isHostedHMApplication() ? NmsUtil.CAS_SERVER_SESSION_TIME_OUT
					: 15;
			// default session time out value is 15 minutes for simple HM.
		}

		HMServicesSettings clientMonitorSettings = list.get(0);
		if (clientMonitorSettings.isInfiniteSession()) {
			return Integer.MAX_VALUE; // never time out
		}

		return clientMonitorSettings.getSessionExpiration();
	}

	// ---------------------------- HM Search ------------------------

	private static final String KEY_SEARCHRESULT = "searchResultSet";

	public void setSearchResult(SearchResultSet searchResultSet) {
		MgrUtil.setSessionAttribute(KEY_SEARCHRESULT, searchResultSet);
	}

	public SearchResultSet getSearchResult() {
		return (SearchResultSet) MgrUtil.getSessionAttribute(KEY_SEARCHRESULT);
	}

	public void clearSearchResult() {
		MgrUtil.removeSessionAttribute(KEY_SEARCHRESULT);
		SearchUtil.clearSearchResults(getUserContext());
	}

	public String getHideSearchConfig() {
		if (getUserContext().isSuperUser()) {
			return "";
		}

		HmPermission featurePermission = getUserContext().getUserGroup()
				.getFeaturePermissions()
				.get(Navigation.L1_FEATURE_CONFIGURATION);
		if (featurePermission != null) {
			if (featurePermission.hasAccess(HmPermission.OPERATION_READ)
					|| featurePermission
							.hasAccess(HmPermission.OPERATION_WRITE)) {
				return "";
			}
		}

		return "none";
	}

	public String getHideSearchFault() {
		if (getUserContext().isSuperUser()) {
			return "";
		}

		HmPermission featurePermission = getUserContext().getUserGroup()
				.getFeaturePermissions().get(Navigation.L2_FEATURE_EVENTS);
		if (featurePermission != null) {
			if (featurePermission.hasAccess(HmPermission.OPERATION_READ)
					|| featurePermission
							.hasAccess(HmPermission.OPERATION_WRITE)) {
				return "";
			}
		}

		featurePermission = getUserContext().getUserGroup()
				.getFeaturePermissions().get(Navigation.L2_FEATURE_ALARMS);
		if (featurePermission != null) {
			if (featurePermission.hasAccess(HmPermission.OPERATION_READ)
					|| featurePermission
							.hasAccess(HmPermission.OPERATION_WRITE)) {
				return "";
			}
		}

		return "none";
	}

	public String getHideSearchTool() {
		if (getUserContext().isSuperUser()) {
			return "";
		}

		HmPermission featurePermission = getUserContext().getUserGroup()
				.getFeaturePermissions().get(Navigation.L1_FEATURE_TOOLS);
		if (featurePermission != null) {
			if (featurePermission.hasAccess(HmPermission.OPERATION_READ)
					|| featurePermission
							.hasAccess(HmPermission.OPERATION_WRITE)) {
				return "";
			}
		}

		return "none";
	}

	public String getHideSearchAdmin() {
		if (getUserContext().isSuperUser()) {
			return "";
		}

		HmPermission featurePermission = getUserContext().getUserGroup()
				.getFeaturePermissions().get(Navigation.L1_FEATURE_HOME);
		if (featurePermission != null) {
			if (featurePermission.hasAccess(HmPermission.OPERATION_READ)
					|| featurePermission
							.hasAccess(HmPermission.OPERATION_WRITE)) {
				return "";
			}
		}

		return "none";
	}

	public String getHideSearchAP() {
		if (getUserContext().isSuperUser()) {
			return "";
		}

		HmPermission featurePermission = getUserContext().getUserGroup()
				.getFeaturePermissions()
				.get(Navigation.L2_FEATURE_ACCESSPOINTS);
		if (featurePermission != null) {
			if (featurePermission.hasAccess(HmPermission.OPERATION_READ)
					|| featurePermission
							.hasAccess(HmPermission.OPERATION_WRITE)) {
				return "";
			}
		}

		return "none";
	}

	public String getHideSearchClients() {
		if (getUserContext().isSuperUser()) {
			return "";
		}

		HmPermission featurePermission = getUserContext().getUserGroup()
				.getFeaturePermissions().get(Navigation.L2_FEATURE_CLIENTS);
		if (featurePermission != null) {
			if (featurePermission.hasAccess(HmPermission.OPERATION_READ)
					|| featurePermission
							.hasAccess(HmPermission.OPERATION_WRITE)) {
				return "";
			}
		}

		return "none";
	}

	public boolean isShowSearchField() {
		return (NmsUtil.isSearchEnabled() && !getUserContext().getUserGroup()
				.isGMUserGroup())
				&& !this.isTeacherView()
				&& getShowHiveApInfo();
	}

	// for HM update available
	public boolean getShowUpdateIcon() {
		if (isOEMSystem()) {
			return false;
		} else {
			boolean fileExist = false;
			try {
				// get download file
				List<String> fileNames = HmBeOsUtil
						.getFileNamesOfDirecotry(AhDirTools
								.getHiveManagerImageDir());
				if (null != fileNames && fileNames.size() == 1) {
					// the file must have finished download
					if (!fileNames.get(0).endsWith(".download")) {
						fileExist = true;
					}
				}
			} catch (Exception ex) {
				log.debug("getShowUpdateIcon", ex.getMessage());
			}
			// for activation key
			LicenseServerSetting lserverInfo = HmBeActivationUtil
					.getLicenseServerInfo();

			// whole hm
			if (getIsInHomeDomain()) {
				return userContext != null
						&& userContext.getUserGroup().isAdministrator()
						&& null != lserverInfo && lserverInfo.isSendStatistic()
						&& (lserverInfo.isAvailableSoftToUpdate() || fileExist);
			} else {
				// hm online vhm, default admin user of vhm
				if (NmsUtil.isHostedHMApplication() && userContext != null
						&& null == userContext.getSwitchDomain()
						&& userContext.getDefaultFlag()
						&& !userContext.isPlannerUser()) {
					return ConfigUtil.existUpgradeInfoForVHM(getDomain()
							.getDomainName(), getDomain().getMaxApNum());
				}
				return false;
			}
		}
	}

	public boolean isRemoveAllLstTitle() {
		return removeAllLstTitle;
	}

	public void setRemoveAllLstTitle(boolean removeAllLstTitle) {
		this.removeAllLstTitle = removeAllLstTitle;
	}

	public boolean hasBoInEasyMode() {
		if (isEasyMode()) {
			List<?> list = QueryUtil
					.executeQuery(boClass, null, null, domainId);

			if (!list.isEmpty()) {
				addActionError(MgrUtil.getUserMessage(
						"error.profile.create.easy.mode.only.one",
						getSelectedL2Feature().getDescription()));
				return true;
			}
		}

		return false;
	}

	// add json object in BaseAction
	protected JSONArray jsonArray;

	protected JSONObject jsonObject;

	public String getJSONString() {
		if (jsonArray == null) {
			log.debug("getJSONString", "JSON string: " + jsonObject.toString());
			return jsonObject.toString();
		} else {
			log.debug("getJSONString", "JSON string: " + jsonArray.toString());
			return jsonArray.toString();
		}
	}

	// simple object definition of new/edit/remove for easy mode
	public static final String SIMPLE_OBJECT_IP = "ip";

	public static final String SIMPLE_OBJECT_MAC = "mac";

	public static final String SIMPLE_OBJECT_VLAN = "vlan";

	public static final String SIMPLE_OBJECT_ATTRI = "attribute";

	public static final String IP_SUB_OBJECT_IP = "ipAddress";

	public static final String IP_SUB_OBJECT_HOSTNAME = "hostName";

	public static final String IP_SUB_OBJECT_NETWORK = "network";

	public static final String IP_SUB_OBJECT_WILDCARD = "wildcard";

	public static final String MAC_SUB_OBJECT_MAC = "macAddress";

	public static final String MAC_SUB_OBJECT_OUI = "macOui";

	public boolean getShowUpgradeWarning() {
		// if (!NmsUtil.isHostedHMApplication()) {
		// return false;
		// }
		if (MgrUtil.getSessionAttribute("SHOW_UPGRADE_WORNING") == null) {
			List<HMServicesSettings> settings = QueryUtil.executeQuery(
					HMServicesSettings.class, null, new FilterParams(
							"owner.domainName", "home"));
			if (!settings.isEmpty()) {
				HMServicesSettings oneClass = settings.get(0);
				if (oneClass.isShowNotifyInfo()) {
					MgrUtil.setSessionAttribute("SHOW_UPGRADE_WORNING", true);
					setUpgradeWarningInfo(oneClass.getNotifyInformation());
					setUpgradeWarningTitle(oneClass.getNotifyInformationTitle());
					return true;
				} else {
					MgrUtil.setSessionAttribute("SHOW_UPGRADE_WORNING", null);
					return false;
				}
			} else {
				MgrUtil.setSessionAttribute("SHOW_UPGRADE_WORNING", null);
				return false;
			}
		}
		return false;
	}

	private String upgradeWarningInfo;
	private String upgradeWarningTitle;

	public String getUpgradeWarningInfo() {
		return upgradeWarningInfo;
	}

	public void setUpgradeWarningInfo(String upgradeWarningInfo) {
		this.upgradeWarningInfo = upgradeWarningInfo;
	}

	public String getUpgradeWarningTitle() {
		return upgradeWarningTitle;
	}

	public void setUpgradeWarningTitle(String upgradeWarningTitle) {
		this.upgradeWarningTitle = upgradeWarningTitle;
	}

	public boolean getShowUserRegInfoWarning() {
		userRegInfo = QueryUtil.findBoByAttribute(UserRegInfoForLs.class,
				"owner.domainName", getDomain().getDomainName());
		if (null != userRegInfo && !isOEMSystem()) {
			if (NmsUtil.isHostedHMApplication()) {
				if (!getIsInHomeDomain() && userContext.getDefaultFlag()
						&& !userRegInfo.isActiveBySelf()) {
					return true;
				}
			} else {
				if (getIsInHomeDomain() && userContext.isSuperUser()
						&& !userRegInfo.isActiveBySelf()) {
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * check if the platform is for HM-Online or not
	 *
	 * @return -
	 * @author Joseph Chen
	 */
	public boolean isHMOnline() {
		return NmsUtil.isHostedHMApplication();
	}

	public boolean isUseCdn() {
		return isHMOnline() && NmsUtil.isEnableYUICDN();
	}

	/**
	 * Protocol-less URL
	 */
	public String getYuiBase() {
		return "//ajax.googleapis.com/ajax/libs/yui/2.9.0/build"; // Google CDN
		// return "http://yui.yahooapis.com/2.9.0/build"; // Yahoo CDN
	}

	/**
	 * check if the platform is just for planner or not
	 *
	 * @return -
	 * @author Joseph Chen
	 */
	public boolean isPlanner() {
		return NmsUtil.isPlanner();
	}

	/**
	 * check if the platform is currently under debug mode
	 *
	 * @return true, the platform is under debug mode; false, under release mode
	 *
	 * @author Joseph Chen
	 */
	public boolean isDebugMode() {
		return NmsUtil.isDebugModel();
	}

	public boolean isPopupSimulator() {
		if (MgrUtil.getSessionAttribute("hasPopupSimulator") != null) {
			return false;
		}

		if (!NmsUtil.isDemoHHM()) {
			return false;
		}

		if (getUserContext().getDomain().isHomeDomain()) {
			return false;
		}

		// check permission
		HmPermission featurePermission = getUserContext().getUserGroup()
				.getFeaturePermissions()
				.get(Navigation.L2_FEATURE_HM_SIMULATOR);
		if (featurePermission == null
				|| !featurePermission.hasAccess(HmPermission.OPERATION_WRITE)) {
			return false;
		}

		long apCount = QueryUtil.findRowCount(HiveAp.class, new FilterParams(
				"owner", getUserContext().getDomain()));
		if (apCount > 0) {
			return false;
		}

		HmDomain _domain = QueryUtil.findBoByAttribute(HmDomain.class,
				"domainName", getDomain().getDomainName());

		int remaining = _domain.getMaxApNum()
				- _domain.computeManagedSimApNum();

		return remaining > 0;
	}

	/**
	 * Refresh the tree node on different operation
	 */
	public void refreshNavigationTree() {
		refreshNavigationTree(userContext, new ImplQueryBo());
	}

	public static void refreshNavigationTree(HmUser hmUser, QueryBo queryBo) {
		if (null == hmUser.getSwitchDomain()) {
			// refresh navigation tree for user
			createNavigationTree(hmUser);
		} else {
			HmDomain switched = hmUser.getSwitchDomain();
			if (HmDomain.HOME_DOMAIN.equals(switched.getDomainName())) {
				// switch to home
				createNavigationTree(hmUser);
			} else {
				// switch to other domain
				String where = "owner = :s1 AND groupName = :s2";
				Object[] values = new Object[2];
				values[0] = switched;
				if (HAUtil.isSlave()) {
					values[1] = HmUserGroup.MONITOR;
				} else {
				values[1] = HmUserGroup.CONFIG;
				}
				FilterParams filterParams = new FilterParams(where, values);
				List<HmUserGroup> list = QueryUtil.executeQuery(
						HmUserGroup.class, null, filterParams, null, queryBo);
				if (!list.isEmpty()) {
					HmUser user = new HmUser();
					user.setUserGroup(list.get(0));
					user.setId(Long.valueOf(1));
					user.setOwner(switched);
					createNavigationTree4VHM(user);
				}
			}
		}
	}

	public String getMyHivePage() {
		String myhiveURL = NmsUtil.getMyHiveServiceURL();

		if (!myhiveURL.endsWith("/")) {
			myhiveURL += "/";
		}

		return myhiveURL + "defaultApp.action?operation=defaultApp";
	}

	/*
	 * Get ip objects list by different parameter
	 */
	public List<CheckItem> getIpObjectsBySingleIp(short beginWith, short endWith) {
		return getBoCheckItems("addressName", IpAddress.class,
				new FilterParams("typeFlag", IpAddress.TYPE_IP_ADDRESS),
				beginWith, endWith);
	}

	public List<CheckItem> getIpObjectsByHostName(short beginWith, short endWith) {
		return getBoCheckItems("addressName", IpAddress.class,
				new FilterParams("typeFlag", IpAddress.TYPE_HOST_NAME),
				beginWith, endWith);
	}

	public List<CheckItem> getIpObjectsByIpAndName() {
		int status = HAUtil.getHAMonitor().getCurrentStatus().getStatus();

		if (HAStatus.STATUS_HA_SLAVE != status &&
				HAStatus.STATUS_HA_MASTER != status) {
			return getBoCheckItems("addressName", IpAddress.class,
					new FilterParams("(typeFlag = :s1 OR typeFlag = :s2)",
							new Object[] { IpAddress.TYPE_IP_ADDRESS,
									IpAddress.TYPE_HOST_NAME }),
					CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		} else {
			return getBoCheckItems("addressName", IpAddress.class,
					new FilterParams("(typeFlag = :s1 OR typeFlag = :s2) and defaultFlag = :s3",
							new Object[] { IpAddress.TYPE_IP_ADDRESS,
									IpAddress.TYPE_HOST_NAME, false }),
					CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		}
	}

	public List<CheckItem> getIpObjectsByIpNameAndNet() {
		return getBoCheckItems("addressName", IpAddress.class,
				new FilterParams("typeFlag != :s1 and typeFlag != :s2",
						new Object[] { IpAddress.TYPE_IP_WILDCARD,IpAddress.TYPE_WEB_PAGE }),
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	public List<CheckItem> getIpObjectsByNetwork(short beginWith, short endWith) {
		return getBoCheckItems("addressName", IpAddress.class,
				new FilterParams("(typeFlag = :s1 OR typeFlag = :s2)",
						new Object[] { IpAddress.TYPE_IP_ADDRESS,
								IpAddress.TYPE_IP_NETWORK }), beginWith,
				endWith);
	}

	public List<CheckItem> getIpObjectsByNetwork() {
		return getIpObjectsByNetwork(CHECK_ITEM_BEGIN_NO, CHECK_ITEM_END_NO);
	}

	public List<CheckItem> getIpObjectsByWildcard() {
		return getBoCheckItems("addressName", IpAddress.class,
				new FilterParams("typeFlag != :s1 and typeFlag != :s2",
						new Object[] { IpAddress.TYPE_HOST_NAME,IpAddress.TYPE_WEB_PAGE }),
				CHECK_ITEM_BEGIN_NO, CHECK_ITEM_END_NO);
	}

	public boolean isTeacherViewEnabled() {
		HMServicesSettings settings = QueryUtil.findBoByAttribute(
				HMServicesSettings.class, "owner", getDomain());

		return settings.isEnableTeacher()
				&& NmsUtil.TEACHER_VIEW_GLOBAL_ENABLED;
	}

	public boolean isTeacherView() {
		return null != userContext
				&& userContext.getUserGroup().isTcUserGroup();
	}

	public class ImplQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (null == bo) {
				return null;
			}

			if (bo instanceof HmUser) {
				HmUser user = (HmUser) bo;
// cchen DONE
//				if (user.getTableColumns() != null) {
//					user.getTableColumns().size();
//				}
//
//				if (user.getTableSizes() != null) {
//					user.getTableSizes().size();
//				}
//
//				if (user.getAutoRefreshs() != null) {
//					user.getAutoRefreshs().size();
//				}
			}

			if (bo instanceof HmUserGroup) {
				HmUserGroup profile = (HmUserGroup) bo;
				if (profile.getFeaturePermissions() != null) {
					profile.getFeaturePermissions().size();
				}
				if (profile.getInstancePermissions() != null) {
					profile.getInstancePermissions().size();
				}
			}
			return null;
		}
	}

	public String getSystemNmsNameTop() {
		if("HiveManager".equalsIgnoreCase(NmsUtil.getOEMCustomer().getNmsName())){
			return "<b>Hive</b>Manager";
		}
		return "<b>" + NmsUtil.getOEMCustomer().getNmsName() + "</b>";
	}
	
	public String getSystemNmsName() {
		return NmsUtil.getOEMCustomer().getNmsName();
	}

	public String getSwitchVHMText() {
		return getText("config.toppane.switchVHM");
	}

	public String getAllVHMsText() {
		return getText("config.toppane.allVHMs");
	}

	public String getFilterVHMText() {
		return getText("config.toppane.filterVHM");
	}

	public boolean isOEMSystem() {
		return NmsUtil.isHMForOEM();
	}

	private boolean searchFlg;

	/**
	 * @return the searchFlg
	 */
	public boolean isSearchFlg() {
		return searchFlg;
	}

	/**
	 * @param searchFlg
	 *            the searchFlg to set
	 */
	public void setSearchFlg(boolean searchFlg) {
		this.searchFlg = searchFlg;
	}

	public String getLicenseInfoInTitle() {
		boolean checkSession = false;
		if (null != userContext) {
			if (getIsInHomeDomain()) {
				checkSession = userContext.isSuperUser();
			} else {
				if (NmsUtil.isHostedHMApplication()) {
					checkSession = userContext.getDefaultFlag();
				}
			}
		}
		if (checkSession) {
			String lsInfo = (String) MgrUtil
					.getSessionAttribute(LICENSE_INFO_IN_TITLE_AREA);
			return null == lsInfo ? "" : lsInfo;
		}
		return "";
	}

	/**
	 * get WLAN changed session for express mode
	 *
	 * @author Yunzhi Lin - Time: Mar 28, 2011 6:33:23 PM
	 * @return -
	 */
	public boolean isChangedExWLANConfig() {
		return isEasyMode()
				&& MgrUtil.getSessionAttribute(GUIDED_CONFIG_WARNING_MSG) != null;
	}

	// public boolean isEnableDistributedUpgrade() {
	// HMServicesSettings settings =
	// QueryUtil.findBoByAttribute(HMServicesSettings.class,
	// "owner", getDomain());
	//
	// if (settings.isEnableDistributedUpgrade()) {
	// return true;
	// }
	//
	// return false;
	// }

	/**
	 * For license in title area
	 */
	public final boolean ifVmware = HM_License.getInstance()
			.isVirtualMachineSystem();

	public final String os = System.getProperty("os.name");

	public String[] twoSystemId;

	public static final int LICENSE_METHOD_GET = 1;

	public static final int LICENSE_METHOD_IMPORT = 2;

	private int radioMethod = LICENSE_METHOD_GET;

	public int getRadioMethod() {
		return radioMethod;
	}

	public void setRadioMethod(int radioMethod) {
		this.radioMethod = radioMethod;
	}

	public EnumItem[] getRadioEmailMessage() {
		return new EnumItem[] { new EnumItem(LICENSE_METHOD_GET,
				MgrUtil.getUserMessage("admin.license.orderKey")) };
	}

	public EnumItem[] getRadioImportMessage() {
		return new EnumItem[] { new EnumItem(LICENSE_METHOD_IMPORT,
				MgrUtil.getUserMessage(
						"license.radio.import.license.or.activation.warning",
						"License Key")) };
	}

	public String getOldLicenseDisplay() {
		return (getIsInHomeDomain() && !ifVmware) ? "" : "none";
	}

	public boolean getShowTwoSystemId() {
		return getIsInHomeDomain() && null != twoSystemId && !ifVmware;
	}

	public String getPrimarySystemId() {
		if (getShowTwoSystemId()) {
			return MgrUtil.getUserMessage(
					"admin.license.systemId.primary.or.secondary",
					new String[] { "primary", twoSystemId[0] });
		}
		return "";
	}

	public String getSecondarySystemId() {
		if (getShowTwoSystemId()) {
			return MgrUtil.getUserMessage(
					"admin.license.systemId.primary.or.secondary",
					new String[] { "secondary", twoSystemId[1] });
		}
		return "";
	}

	public UserRegInfoForLs userRegInfo;

	public UserRegInfoForLs getUserRegInfo() {
		return userRegInfo;
	}

	public void setUserRegInfo(UserRegInfoForLs userRegInfo) {
		this.userRegInfo = userRegInfo;
	}

	public EnumItem[] getStrCountry() {
		int strLeng = UserRegInfoForLs.USER_COUNTRY.length;
		EnumItem[] enumItems = new EnumItem[strLeng + 3];

		List<String> countryLs = Arrays.asList(UserRegInfoForLs.USER_COUNTRY);
		Collections.sort(countryLs, String.CASE_INSENSITIVE_ORDER);

		enumItems[0] = new EnumItem(0, UserRegInfoForLs.USER_COUNTRY_0);
		enumItems[1] = new EnumItem(1, UserRegInfoForLs.USER_COUNTRY_1);

		for (int k = 0; k < countryLs.size(); k++) {
			enumItems[k + 2] = new EnumItem(k + 2, countryLs.get(k));
		}
		enumItems[strLeng + 2] = new EnumItem(strLeng + 2,
				UserRegInfoForLs.USER_COUNTRY_LAST);
		return enumItems;
	}

	/**
	 * get the flag of this vhm enable wireless + routing
	 *
	 * @return if enable
	 */
	/*
	 * public boolean isWirelessRoutingEnable() { // check HM mode has
	 * configured List<HmStartConfig> list =
	 * QueryUtil.executeQuery(HmStartConfig.class,null, null,
	 * getDomain().getId());
	 *
	 * if (!list.isEmpty()) { return list.get(0).getModeType() ==
	 * HmStartConfig.HM_MODE_FULL; } return false; }
	 */

	/**
	 * used to indicate whether a network policy is wireless+routing
	 */
	//private boolean wirelessRoutingEnable;

	/*
	 * it's only used for network policy, if you need a global flag, please use
	 * fullMode.
	 */
	//public boolean isWirelessRoutingEnable() {
	//	return wirelessRoutingEnable;
	//}

	//public void setWirelessRoutingEnable(boolean wirelessRoutingEnable) {
	//	this.wirelessRoutingEnable = wirelessRoutingEnable;
	//}

	/**
	 * This field indicates the request is of AJAX JSON. It could be used to
	 * deal with different cases of traditional HM or new style of HM in the
	 * same JSP pages or JAVA actions
	 */
	private boolean jsonMode;

	/**
	 * this field is for select object id when request is of ajax json
	 */
	private String parentDomID = "";

	private boolean parentIframeOpenFlg;

	public boolean isJsonMode() {
		return jsonMode;
	}

	public void setJsonMode(boolean jsonMode) {
		this.jsonMode = jsonMode;
	}

	public boolean isOrderByNumber() {
		return orderByNumber;
	}

	public void setOrderByNumber(boolean orderByNumber) {
		this.orderByNumber = orderByNumber;
	}

	public boolean isOrderByIp() {
		return orderByIp;
	}

	public void setOrderByIp(boolean orderByIp) {
		this.orderByIp = orderByIp;
	}

	public String getParentDomID() {
		return parentDomID;
	}

	public void setParentDomID(String parentDomID) {
		this.parentDomID = parentDomID;
	}

	public boolean isParentIframeOpenFlg() {
		return parentIframeOpenFlg;
	}

	public void setParentIframeOpenFlg(boolean parentIframeOpenFlg) {
		this.parentIframeOpenFlg = parentIframeOpenFlg;
	}

	/**
	 * subdrawer: content will be shown in a subdrawer, no extra title. dlg:
	 * content will be shown in a iframe dialog
	 */
	private String contentShowType = "subdrawer";

	public String getContentShowType() {
		return contentShowType;
	}

	public void setContentShowType(String contentShowType) {
		this.contentShowType = contentShowType;
	}

	public boolean isContentShownInDlg() {
		return "dlg".equals(getContentShowType());
	}

	public boolean isContentShownInSubDrawer() {
		return "subdrawer".equals(getContentShowType())
				|| "".equals(getContentShowType())
				|| getContentShowType() == null;
	}

	/**
	 * used to get different return paths
	 *
	 * @param normalPath
	 *            not in json mode
	 * @param jsonModePath
	 *            json mode and shown in subdrawer
	 * @param jsonModeDlgPath
	 *            json mode and shown in iframe dialog
	 * @return -
	 */
	protected String getReturnPathWithJsonMode(String normalPath,
			String jsonModePath, String jsonModeDlgPath) {
		if (isJsonMode() && isContentShownInDlg()) {
			return jsonModeDlgPath;
		} else if (isJsonMode()) {
			return jsonModePath;
		} else {
			return normalPath;
		}
	}

	/**
	 * used to get different return paths
	 *
	 * @param normalPath
	 *            not in json mode
	 * @param jsonModePath
	 *            json mode and shown in subdrawer
	 * @return -
	 */
	protected String getReturnPathWithJsonMode(String normalPath,
			String jsonModePath) {
		if (isJsonMode()) {
			return jsonModePath;
		} else {
			return normalPath;
		}
	}

	/**
	 * Handle the error message: add error message into JSON object if the
	 * <b>jsonMode</b> is true and <b>first Opened in dialog</b>; <br>
	 * else add into struts error handler
	 *
	 * @author Yunzhi Lin - Time: Aug 12, 2011 1:47:59 PM
	 * @param msg
	 *            -
	 * @throws JSONException
	 *             -
	 */
	protected void addActionErrorMsg(String msg) throws JSONException {
		if (isJsonMode() && !isParentIframeOpenFlg()) {
			jsonObject = new JSONObject();
			jsonObject.put("error", true);
			jsonObject.put("msg", msg);
		} else {
			addActionError(msg);
		}
	}

	protected void addActionPermanentErrorMsg(String msg){
		this.messagePermanent = true;
		addActionError(msg);
	}

	/**
	 * used for network policy drawer only set a positive value to it if you
	 * want to edit a network policy in drawer
	 */
	private Long networkPolicyId4Drawer = -1L;

	public Long getNetworkPolicyId4Drawer() {
		return networkPolicyId4Drawer;
	}

	public void setNetworkPolicyId4Drawer(Long networkPolicyId4Drawer) {
		this.networkPolicyId4Drawer = networkPolicyId4Drawer;
	}

	private boolean pageAutoRefresh;

	public boolean isPageAutoRefresh() {
		HmAutoRefresh autoRefresh = getUserContext().getAutoRefreshMappings()
				.get(tableId);
		return autoRefresh == null || autoRefresh.isAutoRefresh();
	}

	public void setPageAutoRefresh(boolean pageAutoRefresh) {
		this.pageAutoRefresh = pageAutoRefresh;
	}

	private String pageRefInterval;

	public String getPageRefInterval() {
		HmAutoRefresh autoRefresh = null;
		if(null != getUserContext().getAutoRefreshMappings()) {
			autoRefresh = getUserContext().getAutoRefreshMappings()
					.get(tableId);
		}
		return autoRefresh == null ? HmAutoRefresh.DEFAULT_INTERVAL : autoRefresh.getRefInterval();
	}

	public void setPageRefInterval(String pageRefInterval) {
		this.pageRefInterval = pageRefInterval;
	}

	public EnumItem[] getEnumRefIntervalType() {
		return AhInterface.AUTO_REFRESH_INTERVAL;
	}

	protected boolean enablePageAutoRefreshSetting;

	public boolean isEnablePageAutoRefreshSetting() {
		return enablePageAutoRefreshSetting;
	}

	public void setEnablePageAutoRefreshSetting(
			boolean enablePageAutoRefreshSetting) {
		this.enablePageAutoRefreshSetting = enablePageAutoRefreshSetting;
	}

	protected void updateAutoRefreshSettings() {
		log.info("updateAutoRefreshSettings",
				"saving auto refresh setting for table: " + tableId
						+ ", page auto refresh:" + pageAutoRefresh);
		try {
			HmUser hmUser = QueryUtil.findBoById(HmUser.class, getUserContext()
					.getId(), new ImplQueryBo());
			if (hmUser == null) hmUser = getUserContext();
			HmAutoRefresh autoRefresh = getUserContext()
					.getAutoRefreshMappings().get(tableId);
			if (null == autoRefresh) {
				autoRefresh = new HmAutoRefresh(tableId, pageAutoRefresh,pageRefInterval);
				autoRefresh.setUseremail(hmUser.getEmailAddress());
			}
			if (hmUser != null) {
				hmUser.removeAutoRefreshs(tableId);
				hmUser.getAutoRefreshs().remove(autoRefresh);

				HmAutoRefresh autoref = new HmAutoRefresh(tableId, pageAutoRefresh,pageRefInterval);
				autoref.setUseremail(hmUser.getEmailAddress());
				hmUser.getAutoRefreshs().add(autoref);

				hmUser.addAutoRefreshs(autoref);
//				hmUser = QueryUtil.updateBo(hmUser);
				getUserContext().setAutoRefreshs(hmUser.getAutoRefreshs());
			} else {
				if (getUserContext().getAutoRefreshs() != null) {
					getUserContext().getAutoRefreshs().remove(autoRefresh);

					HmAutoRefresh autoref = new HmAutoRefresh(tableId, pageAutoRefresh,pageRefInterval);
					autoref.setUseremail(hmUser == null ? "null_value" : hmUser.getEmailAddress());
					getUserContext().getAutoRefreshs().add(autoref);
				} else {
					List<HmAutoRefresh> tmpAutoRefreshes = new ArrayList<HmAutoRefresh>();
					getUserContext().setAutoRefreshs(tmpAutoRefreshes);

					HmAutoRefresh autoref = new HmAutoRefresh(tableId, pageAutoRefresh,pageRefInterval);
					autoref.setUseremail(hmUser == null ? "null_value" : hmUser.getEmailAddress());
					getUserContext().getAutoRefreshs().add(autoref);
				}
			}
			getUserContext().createAutoRefreshMappings();
		} catch (Exception e) {
			log.error("updateAutoRefreshSettings", e);
		}
	}

	/**
	 * used to deal with auto refresh setting
	 *
	 * @throws Exception -
	 */
	public void baseCustomizationOperationJson() throws Exception {
		jsonObject = new JSONObject();
		if ("autoRefreshSetting".equals(operation)) {
			jsonObject.put("refreshOnce", false);
			// if previous setting is off and current setting is on, tell page
			// to refresh immediately
			if (pageAutoRefresh) {
				// if pageAutoRefresh is true,set refresh immediately.
				jsonObject.put("refreshOnce", true);
			}
			// then, save auto refresh setting into session and database
			updateAutoRefreshSettings();
			jsonObject.put("autoOn", isPageAutoRefresh());
		}
	}

	// -------- For Notification Message ---start--//
	/**
	 * Get the auto display flag for the NotificationBox.<br>
	 * Currently, flag is true only in the 'Dashboard' page.<br>
	 * <b>Please notice that the auto play action will be stop when try to
	 * manually select the message item.</b>
	 *
	 * @return <code>True</code> or <code>False</code>
	 */
	public boolean isAutoPlayMsg() {
		return autoPlayMsg;
	}

	/**
	 * Configure current display number item in the NotificationBox.
	 *
	 * @author Yunzhi Lin - Time: Dec 7, 2011 4:34:45 PM
	 * @return int
	 */
	public int getMsgPlayNum() {
		int num = 0;
		Object obj = MgrUtil
				.getSessionAttribute(NOTIFICATION_MESSAGE_DISPLAY_NUM);
		if (null != obj) {
			try {
				num = Integer.parseInt(obj.toString());
				Set<?> availableMsgs = getSessionNotificationMessages();
				if (num < 0) {
					num = 0;
				} else if (null != availableMsgs && !availableMsgs.isEmpty()) {
					int totalNum = availableMsgs.size();
					if (num + 1 < totalNum) {
						num += 1;
					} else {
						num = 0;
					}
				}
			} catch (NumberFormatException e) {
				log.error(
						"Error when parse the session value: NOTIFICATION_MESSAGE_DISPLAY_NUM",
						e);
			}
		}
		MgrUtil.setSessionAttribute(NOTIFICATION_MESSAGE_DISPLAY_NUM, num);
		return num;
	}
	// -------- For Notification Message ---end--//

	/**
	 * get the Master's IP in HA mode
	 *
	 * @return -
	 * @author Joseph Chen
	 */
	public String getMasterURL() {
		String masterIP;

		// query database for HA settings
		HASettings settings = QueryUtil.findBoByAttribute(HASettings.class, "haStatus", HASettings.HASTATUS_ENABLE);

		// get local MGT address
		NetConfigImplInterface networkService = AhAppContainer.getBeOsLayerModule().getNetworkService();

		String localIP = networkService.getIP_eth0();

		if (null == settings) {
			log.info("getMasterURL", "Cannot get HA settings from database.");
			return localIP;
		}

		// hm online
		if (NmsUtil.isHostedHMApplication()) {
			String hostName = networkService.getHostName();
			String masterName;
			if (hostName.equals(settings.getPrimaryHostName())) {
				if(settings.isUseExternalIPHostname()) {
					masterName = settings.getSecondaryExternalIPHostname();
				} else {
					masterName = settings.getSecondaryMGTIP();
				}
			} else {
				if(settings.isUseExternalIPHostname()) {
					masterName = settings.getPrimaryExternalIPHostname();
				} else {
					masterName = settings.getPrimaryMGTIP();
				}
			}
			if (null != masterName && !"".equals(masterName)) {
				return "https://"+masterName;
			} else {
				return "https://"+hostName+"."+networkService.getDomainName();
			}
		}

		/*
		 * the local machine must be Slave when this method is invoked. so
		 * Master's IP is the address which is not the same with local MGT
		 * address
		 */
		if (localIP.equals(settings.getPrimaryMGTIP())) {
			if(settings.isUseExternalIPHostname()) {
				masterIP = settings.getSecondaryExternalIPHostname();
			} else {
				masterIP = settings.getSecondaryMGTIP();
			}
		} else {
			if(settings.isUseExternalIPHostname()) {
				masterIP = settings.getPrimaryExternalIPHostname();
			} else {
				masterIP = settings.getPrimaryMGTIP();
			}
		}

//		if (null != masterIP && !"".equals(masterIP)) {
//
//			/*
//			 * replace the address in request URL with master's IP
//			 */
//			StringBuffer requestURL = request.getRequestURL();
//
//			/*
//			 * get the begin and end index of IP address in URL
//			 */
//			int beginIndex = 0, endIndex;
//
//			if(requestURL.indexOf("://") != -1) {
//				beginIndex = requestURL.indexOf("://") + "://".length();
//			}
//
//			if(requestURL.indexOf(":", beginIndex) != -1) {
//				endIndex = requestURL.indexOf(":", beginIndex);
//			} else {
//				if(requestURL.indexOf("/", beginIndex) != -1) {
//					endIndex = requestURL.indexOf("/", beginIndex);
//				} else {
//					endIndex = requestURL.length();
//				}
//			}
//
//			requestURL.replace(beginIndex, endIndex, masterIP);
//			return requestURL.toString();
//		}
		return "https://"+masterIP+"/hm";
	}

	 // passive node
    public boolean getInPassiveNode() {
    	return HAUtil.isSlave();
    }

    private String manualLstForward;

	public String getManualLstForward() {
		return manualLstForward;
	}

	public void setManualLstForward(String manualLstForward) {
		this.manualLstForward = manualLstForward;
	}

	public String getTmpAccessAuthorizedLeftTime() {
		HmDomain switchDomain = userContext.getSwitchDomain();
		if (userContext.isShowLeftAccessTimeForSwitchDomain() && switchDomain != null) {

			// only show once, reset show left time flag after first show
			userContext.setShowLeftAccessTimeForSwitchDomain(false);

			Long authorizedEndDate = switchDomain.getAuthorizationEndDate();
			String endDateStr = AhDateTimeUtil.getDateStrFromLong(authorizedEndDate, new SimpleDateFormat(
					AhDateTimeUtil.DATA_FORMAT_MM_DD_YYYY_HH_MI_AMPM), userContext
					.getTimeZone());
			String leftTime = AhDateTimeUtil.getDateDiffFromNow(authorizedEndDate, AhDateTimeUtil.DIFF_FORMAT_HOUR_MIN);
			return MgrUtil.getUserMessage("info.vhm.tmp.access.remaining.time", new String[]{endDateStr, leftTime});
		}
		return null;
	}

	public String getSingleSignOutURL() {
		String authServerURL = NmsUtil.getAuthServiceURL();

		if (!authServerURL.endsWith("/")) {
			authServerURL += "/";
		}

		String myhiveURL = NmsUtil.getMyHiveServiceURL();

		if (!myhiveURL.endsWith("/")) {
			myhiveURL += "/";
		}

		// Need to redirect to MyHive logout action until clustering is supported by the new SessionMappingStorage implementation since CAS Client 3.3.0.
		return authServerURL + "logout?service=" + myhiveURL + "logout.action";
	}

	// jump from other application
	public String source;

	public int tid;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getUserInfoForSSO(String vhmId) {
		try {
			// get user from cas server
			String user = request.getRemoteUser();
			if (null != user) {
				userContext = CustomerInfoUtil.getUserInfoFromMyhive(user, vhmId, false);

				// the user info is not right
				if (null == userContext) {
					return "error.authentication.credentials.bad";
				}
				// the HiveManager is in maintenance mode
				if (HmBeOsUtil.getMaintenanceModeFromDb() && !userContext.isSuperUser()) {
					return "error.authentication.credentials.bad.maintenance";
				}
				// only supper admin can access passive node
				if (HAUtil.isSlave() && !userContext.isSuperUser()) {
					return "slave";
				}
				// not permit vad user
				if (userContext.isVadAdmin()) {
					return "error.authentication.vad.user.not.allowed";
				}

				HmDomain hmDomain = userContext.getDomain();

				String errorCode = null;
				// Check for the current status of VHM.
				switch (hmDomain.getRunStatus()) {
					case HmDomain.DOMAIN_RESTORE_STATUS:
						log.warn("login", "The user {" + user + "} login request is refused due to restoration for VHM " + hmDomain.getDomainName());
						errorCode = "error.authentication.vhm.restoring";
						break;
					case HmDomain.DOMAIN_BACKUP_STATUS:
						log.warn("login", "The user {" + user + "} login request is refused due to backup for VHM " + hmDomain.getDomainName());
						errorCode = "error.authentication.vhm.backuping";
						break;
					case HmDomain.DOMAIN_UPDATE_STATUS:
						log.warn("login", "The user {" + user + "} login request is refused due to upgrade for VHM " + hmDomain.getDomainName());
						errorCode = "error.authentication.vhm.updating";
						break;
					case HmDomain.DOMAIN_DISABLE_STATUS:
						log.warn("login", "The user {" + user + "} login request is refused due to disablement for VHM " + hmDomain.getDomainName());
						errorCode = "error.authentication.vhm.disabled";
						break;
					default:
						break;
				}
				if (null != errorCode) {
					return errorCode;
				}

				try {
					//createNavigationTree(userContext);
					userContext.setSourceUrl(source);
					userContext.setSourceId(tid);
					userContext.createTableViews();
					userContext.createTableSizeMappings();
					userContext.createAutoRefreshMappings();
					setSessionUserContext(userContext);
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.user.login",userContext.getUserName()));
					userContext.setUserIpAddress(HmProxyUtil.getClientIp(request));
					request.getSession().setAttribute(
							userContext.getId().toString(),
							CurrentUserCache.getInstance());
					initSessionExpiration();

					mode = userContext.getMode();

					String groupName = userContext.getUserGroup().getGroupName();

					// user manager license
					if (groupName.equals(HmUserGroup.GM_ADMIN) || groupName.equals(HmUserGroup.GM_OPERATOR)) {
						// check license and activation key
						if (HmBeLicenseUtil.GM_LITE_LICENSE_VALID) {
							if (!getIsInHomeDomain()) {
								// vhm does not support user manager
								if (!userContext.getOwner().isSupportGM()) {
									return "error.authentication.user.manager.not.support";
								}
							}

						// system does not support user manager
						} else {
							return "error.authentication.user.manager.not.support";
						}
					}

					refreshNavigationTree();

					return null;
				} catch (Exception e) {
					log.error("getUserInfoForSSO", e.getMessage(), e);
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION,
							MgrUtil.getUserMessage("hm.system.log.user.login.exception", user)+e.getMessage());
					return "exception";
				}
			} else {
				return "login";
			}
		} catch (Exception e) {
			log.error("getUserInfoForSSO", e.getMessage(), e);
			return "exception";
		}
	}

	public void initSessionExpiration() {
		domainId = QueryUtil.getDependentDomainFilter(userContext);

		int sessionExpiration = 15 * 60;

		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class,
			null, new FilterParams("owner.id", domainId));
		HMServicesSettings settings = null;
		if (list.isEmpty()) {
			log.info("initSessionExpiration", "Error find no active client refresh interval settings in db!");

			// create session bo for this domain
			try {
				settings = new HMServicesSettings();
				settings.setOwner(getDomain());
				settings.setEnableClientRefresh(false);
				settings.setRefreshInterval(60);
				settings.setRefreshFilterName("");
				settings.setSessionExpiration(NmsUtil.isHostedHMApplication() ? NmsUtil.CAS_SERVER_SESSION_TIME_OUT : 15);
				settings.setInfiniteSession(false);
				QueryUtil.createBo(settings);
			} catch (Exception e) {
				log.error("initSessionExpiration","create bo",e);
			}
		} else {
			settings = list.get(0);
			if (NmsUtil.isHostedHMApplication() && settings.getSessionExpiration() != NmsUtil.CAS_SERVER_SESSION_TIME_OUT) {
				settings.setSessionExpiration(NmsUtil.CAS_SERVER_SESSION_TIME_OUT);
				settings.setInfiniteSession(false);
				// update session bo for this domain
				try {
					QueryUtil.updateBo(settings);
				} catch (Exception e) {
					log.error("initSessionExpiration","update bo",e);
				}
			}
		}
		if (settings != null) {
			boolean sessionInfinite = settings.isInfiniteSession();
			if (sessionInfinite) {
				sessionExpiration = -1;
			} else {
				sessionExpiration = settings.getSessionExpiration() * 60;
			}
		}

		userContext.setSessionExpiration(sessionExpiration);
		request.getSession().setMaxInactiveInterval(sessionExpiration);
	}

	protected String checkProfileNameAllowed(String name, String[] forbids) {
		if (name != null
				&& forbids != null
				&& forbids.length > 0) {
			String lname = name.toLowerCase();
			for (String forbid : forbids) {
				if (lname.equals(forbid)) {
					String names = null;
					for (String forbidTemp : forbids) {
						if (names == null) {
							names = forbidTemp;
						} else {
							names += ", " + forbidTemp;
						}
					}
					return MgrUtil.getUserMessage("common.profile.names.not.allowed", new String[]{name, names});
				}
			}
		}

		return null;
	}

	public boolean isUserSourceFromIdm() {
		return this.getUserContext().isSourceFromIdm();
	}

	public String generateAhDataTableColumnJsonString(List<AhDataTableColumn> dataTableColumnList)
			throws JSONException{
		JSONArray jsonArray = new JSONArray();

		if(dataTableColumnList == null){
			return null;
		}
		for(AhDataTableColumn column : dataTableColumnList){
			JSONObject jsonObject = new JSONObject();
			if(column.getType() != null){
				jsonObject.put("type", column.getType());
			}
			if(column.getMark() != null){
				jsonObject.put("mark", column.getMark());
			}
			if(column.getEditMark() != null){
				jsonObject.put("editMark", column.getEditMark());
			}
			if(column.getDisplay() != null){
				jsonObject.put("display", column.getDisplay());
			}
			if(column.getDefaultValue() != null){
				jsonObject.put("defaultValue", column.getDefaultValue());
			}
			if(column.isDisabled()){
				jsonObject.put("disabled", column.isDisabled());
			}
			if(column.getOptions() != null){
				JSONArray jsonArray_option = new JSONArray();
				for(CheckItem item : column.getOptions()){
					JSONObject jsonObject_option = new JSONObject();
					jsonObject_option.put("label", item.getValue());
					jsonObject_option.put("value", item.getId());
					jsonArray_option.put(jsonObject_option);
				}
				if(jsonArray_option.length() != 0){
					jsonObject.put("options", jsonArray_option);
				}
			}
			if(column.getRealmNameOtions() != null){
				JSONArray jsonArray_option = new JSONArray();
				for(CheckItem3 item : column.getRealmNameOtions()){
					JSONObject jsonObject_option = new JSONObject();
					jsonObject_option.put("label", item.getValue());
					jsonObject_option.put("value", item.getId());
					jsonArray_option.put(jsonObject_option);
				}
				if(jsonArray_option.length() != 0){
					jsonObject.put("options", jsonArray_option);
				}
			}
			if(column.getChangeValue() != null){
				String[] changeValue = new String[column.getChangeValue().size()];
				for(int i=0;i<column.getChangeValue().size();i++){
					CheckItem item = column.getChangeValue().get(i);
					changeValue[i] = item.getValue();
				}
				if(changeValue.length > 0){
					jsonObject.put("changeValue", changeValue);
				}

			}
			if(column.getEvents() != null){
				JSONObject jsonObject_events = new JSONObject();
				Iterator<?> it = column.getEvents().keySet().iterator();
				while(it.hasNext()){
					String key = it.next().toString();
					String value = column.getEvents().get(key);
					jsonObject_events.put(key,value);
				}
				if(jsonObject_events.length()!=0){
					jsonObject.put("events", jsonObject_events);
				}
			}
			jsonArray.put(jsonObject);
		}


		return jsonArray.toString();
	}

	/**
	 * bug 24326 fix start
	 * @return
	 */
	public boolean getShowStatusNewDevicesInfo() {
		if(null == userContext || null == userContext.getNavigationTree()){
			return false;
		}

		List<NavigationNode> accessibleL1Feature = userContext
				.getNavigationTree().getChildNodes();
		for (NavigationNode l1Node : accessibleL1Feature){
			List<NavigationNode> nodesL2 = l1Node.getChildNodes();
			for(NavigationNode l2Node : nodesL2){
				if(Navigation.L2_FEATURE_DEVICES.equals(l2Node.getKey())){
					List<NavigationNode> nodesL3 = l2Node.getChildNodes();
					for(NavigationNode l3Node : nodesL3){
						if(Navigation.L2_FEATURE_MANAGED_HIVE_APS.equals(l3Node.getKey()) || Navigation.L2_FEATURE_CONFIG_HIVE_APS.equals(l3Node.getKey())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean getShowStatusRogueClientsInfo() {
		if(null == userContext || null == userContext.getNavigationTree()){
			return false;
		}

		List<NavigationNode> accessibleL1Feature = userContext
				.getNavigationTree().getChildNodes();
		for (NavigationNode l1Node : accessibleL1Feature){
			List<NavigationNode> nodesL2 = l1Node.getChildNodes();
			for(NavigationNode l2Node : nodesL2){
				if(Navigation.L2_FEATURE_CLIENTS.equals(l2Node.getKey())){
					List<NavigationNode> nodesL3 = l2Node.getChildNodes();
					for(NavigationNode l3Node : nodesL3){
						if(Navigation.L2_FEATURE_ROGUECLIENT.equals(l3Node.getKey())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean getShowStatusRogueApsInfo() {
		if(null == userContext || null == userContext.getNavigationTree()){
			return false;
		}

		List<NavigationNode> accessibleL1Feature = userContext
				.getNavigationTree().getChildNodes();
		for (NavigationNode l1Node : accessibleL1Feature){
			List<NavigationNode> nodesL2 = l1Node.getChildNodes();
			for(NavigationNode l2Node : nodesL2){
				if(Navigation.L2_FEATURE_DEVICES.equals(l2Node.getKey())){
					List<NavigationNode> nodesL3 = l2Node.getChildNodes();
					for(NavigationNode l3Node : nodesL3){
						if(Navigation.L2_FEATURE_ACCESSPOINTS.equals(l3Node.getKey())){
							List<NavigationNode> nodesL4 = l3Node.getChildNodes();
							for(NavigationNode l4Node : nodesL4){
								if(Navigation.L2_FEATURE_ROGUE_APS.equals(l4Node.getKey()))
									return true;
							}

						}
					}
				}
			}
		}
		return false;
	}

	public boolean getShowStatusAlarmsInfo() {
		if(null == userContext || null == userContext.getNavigationTree()){
			return false;
		}

		List<NavigationNode> accessibleL1Feature = userContext
				.getNavigationTree().getChildNodes();
		for (NavigationNode l1Node : accessibleL1Feature){
			List<NavigationNode> nodesL2 = l1Node.getChildNodes();
			for(NavigationNode l2Node : nodesL2){
				if(Navigation.L2_FEATURE_ALARMS.equals(l2Node.getKey())){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * bug 24326 fix end
	 */
	public boolean hasAccessPermission(String featurekey, short oper) {
		try {
			if (getUserContext() == null
					|| HmUserGroup.ADMINISTRATOR.equals(getUserContext().getUserGroup()
							.getGroupName())) {
				return true;
			}
			HmPermission permission = getUserContext().getUserGroup().getFeaturePermissions()
					.get(featurekey);
			if (permission == null) {
				return false;
			} else {
				if (!permission.hasAccess(oper)){
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public String getRedirectorServiceURL() {
		if (isHMOnline()) {
			return NmsUtil.getRedirectorServiceURL() +
					"/switchDomain.action?operation=redirectToVHM&productId=" + getDomain().getVhmID();
		} else {
			return "";
		}
	}

	public boolean getUserHasAccessMyHive() {
		if (userContext==null) {
			return false;
		}

		return userContext.isAccessMyhive();
	}
	
	public String getCsvImportFormatInfo() {
		if (isEasyMode()) {
			return MgrUtil.getUserMessage("glasgow_05.hm.missionux.importserial.importserial.note.express",
					NmsUtil.filterHiveAPModelString(HiveAp.HIVEAP_MODEL,true))
					+ getText("glasgow_05.config.csvfile.message.domain");
		} else {
			return MgrUtil.getUserMessage("glasgow_05.hm.missionux.importserial.importserial.note.full",
					NmsUtil.filterHiveAPModelString(HiveAp.HIVEAP_MODEL,false))
					+ getText("glasgow_05.config.csvfile.message.domain");
		}
		
	}

	/**
	 * @description: generate session token to protect CSRF attack
	 * @author: huihe@aerohive.com
	 * 
	 */
	protected void injectCSRFToken(){		
		String randomStr = NmsUtil.genRandomString(12);
		MgrUtil.setSessionAttribute(SESSION_TOKEN_FOR_CSRF_ATTACK, Base64.encode(randomStr));
	}
	
	public static synchronized String getSessionTokenInfo() {
		return (String) MgrUtil.getSessionAttribute(SESSION_TOKEN_FOR_CSRF_ATTACK);
	}
	
	private String sessionToken;

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}
	
	public boolean isCSRFTokenValida(){
		return null != sessionToken && sessionToken.equals(getSessionTokenInfo());
	}
	
	/*
	 * Check the select file if exist under the directory which allowed access
	 */
	public boolean checkFileExistUnderDirectoryAllowed(String filePath, String fileName) {
		if (StringUtils.isBlank(filePath) || StringUtils.isBlank(fileName)) {
			return false;
		}
		try {
			List<String> fileNames = HmBeOsUtil.getFileNamesOfDirecotry(filePath);
			if (null != fileNames && !fileNames.isEmpty()) {
				boolean fileExist = false;
				for (String fileStr : fileNames) {
					if (fileName.equalsIgnoreCase(fileStr)) {
						fileExist = true;
						break;
					}
				}
				return fileExist;
			}
		} catch (Exception ex) {
			log.error("checkFileExistUnderDirectoryAllowed()", ex.getMessage());
		}
		return false;
	}
}