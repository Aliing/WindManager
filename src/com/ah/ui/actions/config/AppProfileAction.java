package com.ah.ui.actions.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.Application;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.OsObjectVersion;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.AppGroupInfo;

public class AppProfileAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private ApplicationProfile profile;
	
	private List<Application> fixedAppList;
		
	private String selectedAppIds;
	
	private String selectedProfileIds;
	
	private Map<String, AppGroupInfo> applicationMap;
	
	public String init() throws Exception {
		Long id = (profile == null) ? 0 : profile.getId();
		if (id == null || id <= 0) {
			//setSessionDataSource(new ApplicationProfile());
			profile = new ApplicationProfile();
		}
		else {
			profile = QueryUtil.findBoById(ApplicationProfile.class, id, this);			
		}
		applicationMap = new HashMap<>();
		
		fixedAppList = QueryUtil.executeQuery(Application.class, null, null);
		for (Application app : fixedAppList) {
			String groupName = app.getAppGroupName();
			AppGroupInfo appGroupInfo;
			if (applicationMap.get(groupName) == null) {
				appGroupInfo = new AppGroupInfo();
			} else {
				appGroupInfo = applicationMap.get(groupName);
			}
			if (profile.getApplicationList() != null && !profile.getApplicationList().isEmpty()) {
				if (profile.getApplicationList().contains(app)) {
					app.setSelected(true);
					appGroupInfo.setSelected(true);
				}
			}
			appGroupInfo.addApplication(app);
            applicationMap.put(groupName, appGroupInfo);
		}
//		if (profile.getApplicationList() != null && !profile.getApplicationList().isEmpty()) {
//			fixedAppList.removeAll(profile.getApplicationList());
//		}
		if (isJsonMode()) {
			return "appProfileJsonDlg";
		}
		return INPUT;
	}
	
	public String remove() throws Exception {
		if (selectedIds != null && !selectedIds.isEmpty()) {
			QueryUtil.removeBos(ApplicationProfile.class, selectedIds);
		}
		return preparePageBoList();
	}
			
	public String save() throws Exception {
		if (StringUtils.isNotBlank(selectedAppIds)) {
			Set<Application> appList = new HashSet<>();
			String[] array = selectedAppIds.split(",");
			for (String id : array) {
				appList.add(QueryUtil.findBoById(Application.class, Long.parseLong(id)));
			}
			profile.setApplicationList(appList);
		}
		boolean isCreateFlag;
		if (profile.getId() == null || profile.getId() < 1) {
			if (checkNameExists("profileName", profile.getProfileName())) {
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", profile.getProfileName()));
					return "json";
				}
				else {
					fixedAppList = QueryUtil.executeQuery(Application.class, null, null);
					return INPUT;
				}
			}
			profile.setOwner(getUserContext().getDomain());
			profile.setDefaultFlag(false);
			Long id = QueryUtil.createBo(profile);
			profile.setId(id);
			isCreateFlag = true;
		} else {
			ApplicationProfile entity = QueryUtil.findBoById(ApplicationProfile.class, profile.getId());
			//entity.setProfileName(profile.getProfileName());
			entity.setApplicationList(profile.getApplicationList());
			QueryUtil.updateBo(entity);
			isCreateFlag = false;
		}
		if (isJsonMode()) {
			jsonObject = new JSONObject();
			jsonObject.put("profileId", profile.getId());
			jsonObject.put("profileName", profile.getProfileName());
			jsonObject.put("parentDomID", getParentDomID());
			jsonObject.put("isCreateFlag", isCreateFlag);
			return "json";
		}
		return preparePageBoList();
	}
	
	public String cloneProfile() throws Exception {
		long cloneId = getSelectedIds().get(0);
		profile = (ApplicationProfile) findBoById(boClass, cloneId, this);
		profile.setId(null);
		profile.setDefaultFlag(false);
		profile.setProfileName("");
		profile.setOwner(null);
		profile.setVersion(null);
		applicationMap = new HashMap<>();
		fixedAppList = QueryUtil.executeQuery(Application.class, null, null);
		for (Application app : fixedAppList) {
			String groupName = app.getAppGroupName();
			AppGroupInfo appGroupInfo;
			if (applicationMap.get(groupName) == null) {
				appGroupInfo = new AppGroupInfo();
			} else {
				appGroupInfo = applicationMap.get(groupName);
			}
			if (profile.getApplicationList() != null && !profile.getApplicationList().isEmpty()) {
				if (profile.getApplicationList().contains(app)) {
					app.setSelected(true);
					appGroupInfo.setSelected(true);
				}
			}
			appGroupInfo.addApplication(app);
            applicationMap.put(groupName, appGroupInfo);
		}
		return INPUT;
	}

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		if (StringUtils.isBlank(operation) || "sort".equals(operation)) {
			baseOperation();
			return preparePageBoList();
		}	
		try {
			Method method = getClass().getDeclaredMethod(operation);
			return (String) method.invoke(this);
		} catch (Exception e) {
			reportActionError(e);
			return preparePageBoList();
		}
	}

	@Override
	protected void preparePage() throws Exception {
		enableSorting();
		enablePaging();
		page = findBos(this);
	}

	private String preparePageBoList() throws Exception {
		String str = prepareBoList();
		//loadLazyData();
		return str;
	}
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_APP_PROFILE);
		setDataSource(ApplicationProfile.class);
		keyColumnId = COLUMN_NAME;
		tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_OS_OBJECT;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof ApplicationProfile) {
			ApplicationProfile profile = (ApplicationProfile) bo;
			if (profile.getApplicationList() != null) {
				profile.getApplicationList().size();
			}
		}
		return null;
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		OsObject source = QueryUtil.findBoById(OsObject.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<OsObject> list = QueryUtil.executeQuery(OsObject.class, null,
				new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<>(list.size());
		for (OsObject profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			OsObject up = source.clone();
			if (null == up) {
				continue;
			}

			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setOsName(profile.getOsName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			List<OsObjectVersion> items = new ArrayList<>();
			items.addAll(source.getItems());
			up.setItems(items);
			hmBos.add(up);
		}
		return hmBos;
	}

	@Override
	public ApplicationProfile getDataSource() {
		return (ApplicationProfile) dataSource;
	}

	public String getDisplayInHomeDomain() {
		if (HmUser.ADMIN_USER.equals(getUserContext().getUserName())) {
			return "";
		}
		
		return "none";
	}
	
	public int getAddressNameLength() {
		return getAttributeLength("osName");
	}

	public int getCommentLength() {
		return HmBo.DEFAULT_DESCRIPTION_LENGTH;
	}

	private Long osVersionId;
	
	public List<CheckItem> getAvailableOsVersionFields() {
		List<CheckItem> result = new ArrayList<>();
		result.add(0, new CheckItem((long) CHECK_ITEM_ID_BLANK, ""));
		for (int i = 1; i < 11; i++) {
			result.add(new CheckItem((long)i, MgrUtil.getEnumString("enum.config.security.device.detection.os.keywords."+i)));
		}
		sortValuesByAlpha(result);
		return result;
	}
	
	private void sortValuesByAlpha(List<CheckItem> options) {
		 Collections.sort(options, new Comparator<CheckItem>() {
			@Override
			public int compare(CheckItem o1, CheckItem o2) {
				return o1.getValue().compareToIgnoreCase(o2.getValue());
			}
		});
	 }
	
	public ApplicationProfile getProfile() {
		return profile;
	}

	public void setProfile(ApplicationProfile profile) {
		this.profile = profile;
	}

	public String getSelectedAppIds() {
		return selectedAppIds;
	}

	public void setSelectedAppIds(String selectedAppIds) {
		this.selectedAppIds = selectedAppIds;
	}

	// ID of table columns in list view
	public static final int COLUMN_NAME = 1;

	public static final int COLUMN_VERSION = 2;

	@Override
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.osObject.name";
			break;
		case COLUMN_VERSION:
			code = "config.osObject.version";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<>(2);
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_VERSION));
		return columns;
	}

	public Long getOsVersionId() {
		return osVersionId;
	}

	public void setOsVersionId(Long osVersionId) {
		this.osVersionId = osVersionId;
	}
	
	private String errorMsgStr = "";

	public String getErrorMsgStr() {
		return errorMsgStr;
	}

	public void setErrorMsgStr(String errorMsgStr) {
		this.errorMsgStr = errorMsgStr;
	}
		
	public List<Application> getFixedAppList() {
		return fixedAppList;
	}

	public void setFixedAppList(List<Application> fixedAppList) {
		this.fixedAppList = fixedAppList;
	}

	public String getSelectedProfileIds() {
		return selectedProfileIds;
	}

	public void setSelectedProfileIds(String selectedProfileIds) {
		this.selectedProfileIds = selectedProfileIds;
	}

	public Map<String, AppGroupInfo> getApplicationMap() {
		return applicationMap;
	}

	public void setApplicationMap(Map<String, AppGroupInfo> applicationMap) {
		this.applicationMap = applicationMap;
	}
	
}