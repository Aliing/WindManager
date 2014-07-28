package com.ah.ui.actions.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import org.json.JSONObject;

import com.ah.be.common.AhDirTools;
import com.ah.be.performance.BeOsInfoProcessor;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.OsObjectVersion;
import com.ah.bo.network.OsVersion;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class OsObjectAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(OsObjectAction.class.getSimpleName());

	public static final int DETECTION_METHOD_DHCP = 1;
	public static final int DETECTION_METHOD_HTTP = 0;
	
	private String exportFileType = EXPORT_FILE_TYPE;
	
	private String exportFileName = EXPORT_FILE_NAME_DEFAULT;
	
	private InputStream inputStream;
	
	private static final String EXPORT_FILE_TYPE = "default";
	private static final String EXPORT_FILE_NAME_DEFAULT = "os_dhcp_fingerprints_default.txt";
	private static final String EXPORT_FILE_NAME_CURRENT = "os_dhcp_fingerprints.txt";
	
	public List<String> DHCP_OSVERSIONS;
	public List<String> DHCP_OPTION55S;
	public List<String> DHCP_OSVERSIONS_DEFAULT;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		getDefaultDhcpOsVersions();
		getDhcpOsVersions();
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.osObject"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new OsObject());
				hideCreateItem = "";
				hideNewButton = "none";
				hideDhcpCreateItem = "";
				hideDhcpNewButton = "none";
				return getReturnPathWithJsonMode(INPUT, "osObjectJsonDlg");
			} else if ("create".equals(operation)
					|| ("create" + getLstForward()).equals(operation)) {
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					if (!checkObjectItemListAllEmpty() || !updateListDatas(true) || !updateDhcpListDatas(true)) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", getErrorMsgStr());
						return "json";
					} else if (checkProfileName(getDataSource().getOsName())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getOsName()));
						return "json";
					}
				} else {
					if (!checkObjectItemListAllEmpty() || !updateListDatas(true) || !updateDhcpListDatas(true) || checkProfileName(getDataSource().getOsName())) {
						return INPUT;
					}
				}
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedName", getDataSource().getOsName());
					try {
						id = createBo(dataSource);
						BeOsInfoProcessor.getInstance().addOsName(id);
						jsonObject.put("addedId", id);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					return "json";
				} else {
					if ("create".equals(operation)) {
						Long createId = createBo(dataSource);
						BeOsInfoProcessor.getInstance().addOsName(createId);
						return preparePageBoList();
					} else {
						id = createBo(dataSource);
						BeOsInfoProcessor.getInstance().addOsName(id);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				if (dataSource == null) {
					String rtnStr = preparePageBoList();
					return getReturnPathWithJsonMode(rtnStr, "osObjectJsonDlg");
				} else {
					addLstTitle(getText("config.title.osObject.edit") + " '"
							+ getChangedName() + "'");
					return getReturnPathWithJsonMode(INPUT, "osObjectJsonDlg");
				}
			} else if ("update".equals(operation)
					|| ("update" + getLstForward()).equals(operation)) {
				if (dataSource != null) {
					if (!checkObjectItemListAllEmpty() || !updateListDatas(true) || !updateDhcpListDatas(true)) {
						return getReturnPathWithJsonMode(INPUT,"osObjectJsonDlg");
					}
				}
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					try {
						updateBo(dataSource);
						setSessionDataSource(findBoById(boClass, id, this));
						BeOsInfoProcessor.getInstance().resetOsName(getDomainId());
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					return "json";
				} else {
					if ("update".equals(operation)) {
						updateBo(dataSource);
						BeOsInfoProcessor.getInstance().resetOsName(getDomainId());
						return preparePageBoList();
					} else {
						updateBo(dataSource);
						BeOsInfoProcessor.getInstance().resetOsName(getDomainId());
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				OsObject profile = (OsObject) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setOsName("");
				profile.setOwner(null);
				profile.setVersion(null);
				profile.setDefaultFlag(false);
				List<OsObjectVersion> items = new ArrayList<>();
				items.addAll(profile.getItems());
				profile.setItems(items);
				List<OsObjectVersion> dhcpItems = new ArrayList<>();
				dhcpItems.addAll(profile.getDhcpItems());
				profile.setDhcpItems(dhcpItems);
				setSessionDataSource(profile);
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("addOsVersion".equals(operation)) {
				if (dataSource == null) {
					return preparePageBoList();
				} else {
					updateListDatas(false);
					addOsVersion();
					return getReturnPathWithJsonMode(INPUT,"osObjectJsonDlg");
				}
			} else if ("addDhcpOsVersion".equals(operation)) {
				if (dataSource == null) {
					return preparePageBoList();
				} else {
					updateDhcpListDatas(false);
					addDhcpOsVersion();
					return getReturnPathWithJsonMode(INPUT,"osObjectJsonDlg");
				}
			} else if ("removeOsVersion".equals(operation)
					|| "removeOsVersionNone".equals(operation)) {
				hideCreateItem = "removeOsVersionNone".equals(operation) ? ""
						: "none";
				hideNewButton = "removeOsVersionNone".equals(operation) ? "none"
						: "";
				
				if (dataSource == null) {
					return preparePageBoList();
				} else {
					updateListDatas(false);
					removeSelectedBos(ruleIndices,getDataSource().getItems());
					return getReturnPathWithJsonMode(INPUT,"osObjectJsonDlg");
				}
			}else if("removeDhcpOsVersion".equals(operation)
					|| "removeDhcpOsVersionNone".equals(operation)) {
				hideDhcpCreateItem = "removeDhcpOsVersionNone".equals(operation) ? ""
						: "none";
				hideDhcpNewButton = "removeDhcpOsVersionNone".equals(operation) ? "none"
						: "";
				
				if (dataSource == null) {
					return preparePageBoList();
				} else {
					updateDhcpListDatas(false);
					removeSelectedBos(dhcpRuleIndices,getDataSource().getDhcpItems());
					return getReturnPathWithJsonMode(INPUT,"osObjectJsonDlg");
				}
			} else if ("import".equals(operation)) {
				addLstForward("osObject");
				clearErrorsAndMessages();
				return operation;
			} else if ("export".equals(operation)) {
				if(!(exportFileType.isEmpty()) && exportFileType.equalsIgnoreCase(EXPORT_FILE_TYPE)){
					exportFileName = EXPORT_FILE_NAME_DEFAULT;
				}else if(!(exportFileType.isEmpty()) && !(exportFileType.equalsIgnoreCase(EXPORT_FILE_TYPE))){
						exportFileName = EXPORT_FILE_NAME_CURRENT;
				}

				String filePath = AhDirTools.getOsDetectionDir();
				String defualtFilePath =filePath + exportFileName;
					
				File file = new File(defualtFilePath);
					
				if(file.exists()){
					byte[] byteArry = readTxtFile(defualtFilePath).getBytes();
					inputStream = new ByteArrayInputStream(byteArry);
					
					return "export";
				}else if(!(exportFileType.equalsIgnoreCase(EXPORT_FILE_TYPE))){
					defualtFilePath = filePath + EXPORT_FILE_NAME_DEFAULT;
					file = new File(defualtFilePath);
					if(file.exists()){
						byte[] byteArry = readTxtFile(defualtFilePath).getBytes();
						inputStream = new ByteArrayInputStream(byteArry);
						
						return "export";
					}else{
						addActionError(MgrUtil.getUserMessage(
								"error.fileNotExist"));
						return preparePageBoList();
					}
				}else{
					addActionError(MgrUtil.getUserMessage(
							"error.fileNotExist"));
					return preparePageBoList();
				}
			} else {
				baseOperation();
				if("paintbrush".equals(operation) || "remove".equals("operation")){
					BeOsInfoProcessor.getInstance().resetOsName(getDomainId());
				}
				return preparePageBoList();
			}
		} catch (Exception e) {
			reportActionError(e);
			return preparePageBoList();
		}
	}

	private String preparePageBoList() throws Exception {
		String str = prepareBoList();
		loadLazyData();
		return str;
	}

	private void loadLazyData() {
		if (page.isEmpty())
			return;

		Map<Long, OsObject> osObjectMap = new HashMap<>();
		StringBuilder buf = new StringBuilder();
		for (Object element : page) {
			OsObject osObject = (OsObject) element;
			osObjectMap.put(osObject.getId(), osObject);
			buf.append(osObject.getId());
			buf.append(",");

			osObject.setItems(new ArrayList<OsObjectVersion>());
			osObject.setDhcpItems(new ArrayList<OsObjectVersion>());
		}
		buf.deleteCharAt(buf.length() - 1);

		String sql = "select a.id,b.os_object_id,b.osversion"
				+ " from os_object a "
				+ " inner join os_object_version b on b.os_object_id = a.id "
				+ " where a.id in(" + buf.toString() + ")";

		List<?> templates = QueryUtil.executeNativeQuery(sql);

		for (Object obj : templates) {
			Object[] template = (Object[]) obj;
			Long id = Long.valueOf(template[0].toString());

			OsObject templateElment = osObjectMap.get(id);
			if (templateElment != null) {
				if (StringUtils.isNotBlank(template[1].toString())) {
					OsObjectVersion tempClass = new OsObjectVersion();
					tempClass.setOsVersion(template[2].toString());
					templateElment.getItems().add(tempClass);
				}
			}
		}
		
		sql = "select a.id,b.os_object_id,b.osversion"
			+ " from os_object a "
			+ " inner join os_object_version_dhcp b on b.os_object_id = a.id "
			+ " where a.id in(" + buf.toString() + ")";

		templates = QueryUtil.executeNativeQuery(sql);
	
		for (Object obj : templates) {
			Object[] template = (Object[]) obj;
			Long id = Long.valueOf(template[0].toString());
	
			OsObject templateElment = osObjectMap.get(id);
			if (templateElment != null) {
				if (StringUtils.isNotBlank(template[1].toString())) {
					OsObjectVersion tempClass = new OsObjectVersion();
					tempClass.setOsVersion(template[2].toString());
					templateElment.getDhcpItems().add(tempClass);
				}
			}
		}
	}
	
	protected boolean checkObjectItemListAllEmpty() {
		if ((getDataSource().getItems() == null || getDataSource().getItems().isEmpty())
				&& (getDataSource().getDhcpItems() == null || getDataSource().getDhcpItems().isEmpty())) {
			addActionError(getText("error.osobjectinput"));
			return false;
		}
		return true;
	}

	protected boolean updateDhcpListDatas(boolean ifCheck) {
		if (option55s != null) {
			OsObjectVersion versionI;
			for (int i = 0; i < option55s.length; i++) {
				versionI = getDataSource().getDhcpItems().get(i);
				versionI.setOsVersion(dhcpOsVersions[i]);
				if (DHCP_OSVERSIONS_DEFAULT.contains(dhcpOsVersions[i])) {
					versionI.setOption55(null);
				} else {
					versionI.setOption55(option55s[i]);
				}
			}
		}
		return !ifCheck || checkObjectItemList(getDataSource().getDhcpItems());
	}
	
	protected boolean updateListDatas(boolean ifCheck) {
		if (descriptions != null) {
			OsObjectVersion versionI;
			for (int i = 0; i < descriptions.length; i++) {
				versionI = getDataSource().getItems().get(i);
				versionI.setOsVersion(osVsersions[i]);
				versionI.setDescription(descriptions[i]);
			}
		}
		return !ifCheck || checkObjectItemList(getDataSource().getItems());
	}
	
	private boolean checkObjectItemList(List<OsObjectVersion> list) {
		OsObjectVersion versionI;
		OsObjectVersion versionJ;
		for (int i = 0; i < list.size()-1; i++) {
			versionI = list.get(i);
			for (int j = i+1; j < list.size(); j++) {
				versionJ = list.get(j);
				if (versionI.getOsVersion().equalsIgnoreCase(versionJ.getOsVersion())) {
					addActionError(MgrUtil.getUserMessage("error.sameObjectExists", getText("config.osObject.version")));
					setErrorMsgStr(MgrUtil.getUserMessage("error.sameObjectExists", getText("config.osObject.version")));
					return false;
				}
				
				if (versionI.getOption55()!=null && versionI.getOption55().equalsIgnoreCase(versionJ.getOption55())) {
					addActionError(MgrUtil.getUserMessage("error.sameObjectExists", getText("config.osObject.option55")));
					setErrorMsgStr(MgrUtil.getUserMessage("error.sameObjectExists", getText("config.osObject.option55")));
					return false;
				}
			}
		}
		return true;
	}

	private boolean checkProfileName(String name) {
		return checkNameExists("osName", name);
	}

	protected boolean addOsVersion() throws Exception {
		// the os version from list
		if (null != osVersionId && osVersionId > 0) {
			osVersion = MgrUtil.getEnumString("enum.config.security.device.detection.os.keywords."+osVersionId);
		}
		for (OsObjectVersion version : getDataSource().getItems()) {
			if (version.getOsVersion().equalsIgnoreCase(osVersion)) {
				addActionError(MgrUtil.getUserMessage("error.addObjectExists"));
				hideCreateItem = "";
				hideNewButton = "none";
				return false;
			}
		}
		OsObjectVersion oneItem = new OsObjectVersion();
		oneItem.setOsVersion(osVersion.trim());
		oneItem.setDescription(description.trim());
		getDataSource().getItems().add(oneItem);
		return true;
	}
	
	protected boolean addDhcpOsVersion() throws Exception {
		// the os version from list
		if (null != dhcpOsVersionId && dhcpOsVersionId > 0) {
			dhcpOsVersion = DHCP_OSVERSIONS.get(dhcpOsVersionId.intValue()-1);
		}
		for (OsObjectVersion version : getDataSource().getDhcpItems()) {
			if (version.getOsVersion().equalsIgnoreCase(dhcpOsVersion)) {
				addActionError(MgrUtil.getUserMessage("error.addObjectExists"));
				hideDhcpCreateItem = "";
				hideDhcpNewButton = "none";
				return false;
			}
			if (version.getOption55()!=null&&version.getOption55().equalsIgnoreCase(option55)) {
				addActionError(MgrUtil.getUserMessage("error.sameObjectExists", getText("config.osObject.option55")));
				hideDhcpCreateItem = "";
				hideDhcpNewButton = "none";
				return false;
			}
		}
		
		OsObjectVersion oneItem = new OsObjectVersion();
		oneItem.setOsVersion(dhcpOsVersion.trim());
		oneItem.setOption55(option55==null ? option55: option55.trim());
		getDataSource().getDhcpItems().add(oneItem);
		return true;
	}
	
	protected void removeSelectedBos(Collection<String> ruleIndices,List<OsObjectVersion> list) {
		if (ruleIndices != null) {
			Collection<OsObjectVersion> removeList = new Vector<>();
			for (String serviceIndex : ruleIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < list.size()) {
						removeList.add(list.get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			list.removeAll(removeList);
		}
	}

	private String osVersion;

	private String description;

	private String[] osVsersions;

	private String[] descriptions;

	private Collection<String> ruleIndices;

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setOsVsersions(String[] osVsersions) {
		this.osVsersions = osVsersions;
	}

	public void setDescriptions(String[] descriptions) {
		this.descriptions = descriptions;
	}

	public void setRuleIndices(Collection<String> ruleIndices) {
		this.ruleIndices = ruleIndices;
	}
	
	private String dhcpOsVersion;

	private String option55;

	private String[]  dhcpOsVersions;

	private String[] option55s;

	private Collection<String> dhcpRuleIndices;
	
	public void setDhcpOsVersion(String dhcpOsVersion) {
		this.dhcpOsVersion = dhcpOsVersion;
	}

	public void setOption55(String option55) {
		this.option55 = option55;
	}

	public void setDhcpOsVersions(String[] dhcpOsVersions) {
		this.dhcpOsVersions = dhcpOsVersions;
	}

	public void setOption55s(String[] option55s) {
		this.option55s = option55s;
	}

	public void setDhcpRuleIndices(Collection<String> dhcpRuleIndices) {
		this.dhcpRuleIndices = dhcpRuleIndices;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_OS_OBJECT);
		setDataSource(OsObject.class);
		keyColumnId = COLUMN_NAME;
		tableId = HmTableColumn.TABLE_CONFIGURATION_NETWORK_OS_OBJECT;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof OsObject) {
			OsObject osObject = (OsObject) bo;
			if (null != osObject.getItems()) {
				osObject.getItems().size();
			}
			if (null != osObject.getDhcpItems()) {
				osObject.getDhcpItems().size();
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
	public OsObject getDataSource() {
		return (OsObject) dataSource;
	}

	public String getChangedName() {
		return getDataSource().getOsName().replace("\\", "\\\\")
				.replace("'", "\\'");
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().isDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}
	
	public String getDisplayInHomeDomain() {
		if(HmUser.ADMIN_USER.equals(getUserContext().getUserName())) {
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

	public int getGridCount() {
		return getDataSource().getItems().isEmpty() ? 3 : 0;
	}
	
	public int getDhcpGridCount(){
		return getDataSource().getDhcpItems().isEmpty() ? 3 : 0;
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
	
	private Long dhcpOsVersionId;
	public List<CheckItem> getAvailableDhcpOsVersionFields() {
		List<CheckItem> result = new ArrayList<>();
		result.add(0, new CheckItem((long) CHECK_ITEM_ID_BLANK, ""));
		List<String> lists = DHCP_OSVERSIONS;
		for (int i = 0; i < lists.size(); i++) {
			result.add(new CheckItem((long)i+1,lists.get(i)));
		}
		//sortValuesByAlpha(result);
		return result;
	}
	
	public List<CheckItem> getAvailableDhcpOption55Fields() {
		List<CheckItem> result = new ArrayList<>();
		result.add(0, new CheckItem((long) CHECK_ITEM_ID_BLANK, ""));
		List<String> lists = DHCP_OPTION55S;
		for (int i = 0; i < lists.size(); i++) {
			result.add(new CheckItem((long)i+1,lists.get(i)));
		}
		return result;
	}
	
	private void getDhcpOsVersions(){
		DHCP_OSVERSIONS = new ArrayList<>();
		DHCP_OPTION55S = new ArrayList<>();
		
		//List<OsVersion> osVersions = QueryUtil.executeQuery(OsVersion.class, new SortParams("osVersion"), null, getDomainId(),new OsObjectAction());
		for(String verName : DHCP_OSVERSIONS_DEFAULT){
			DHCP_OSVERSIONS.add(verName);
			DHCP_OPTION55S.add("");
		}
		
		List<OsVersion> osVersions = QueryUtil.executeQuery(OsVersion.class, new SortParams("osVersion"), new FilterParams("owner.id", getDomainId()));
		for(OsVersion osVersion : osVersions){
			DHCP_OSVERSIONS.add(osVersion.getOsVersion());
			DHCP_OPTION55S.add(osVersion.getOption55());
		}
		
		List<OsVersion> osVers = new ArrayList<>();
		for(int i=0;i<DHCP_OSVERSIONS.size();i++){
			OsVersion version = new OsVersion();
			version.setOption55(DHCP_OPTION55S.get(i));
			version.setOsVersion(DHCP_OSVERSIONS.get(i));
			osVers.add(version);
		}
		
	    Collections.sort(osVers, new Comparator<OsVersion>() {
 			@Override
 			public int compare(OsVersion o1, OsVersion o2) {
 				return o1.getOsVersion().compareToIgnoreCase(o2.getOsVersion());
 			}
 		});
	    DHCP_OSVERSIONS.clear();
	    DHCP_OPTION55S.clear();
	    for(OsVersion version : osVers){
	    	DHCP_OSVERSIONS.add(version.getOsVersion());
	    	DHCP_OPTION55S.add(version.getOption55());
//	    	if(!version.getOption55().isEmpty()){
//	    		boolean isDuplicate = false;
//	    		for(OsVersion ver : osVersions){
//		    		if(DHCP_OSVERSIONS_DEFAULT.contains(ver.getOsVersion())){
//		    			isDuplicate = true;
//		    			break;
//		    		}
//		    	}
//	    		if(isDuplicate){
//	    			DHCP_OSVERSIONS.add(version.getOsVersion()+" ("+ getDomain().getDomainName()+")");
//	    		} else {
//	    			DHCP_OSVERSIONS.add(version.getOsVersion());
//	    		}
//	    	} else {
//	    		DHCP_OSVERSIONS.add(version.getOsVersion());
//	    	}
//	    	DHCP_OPTION55S.add(version.getOption55());
	    }
	}
	
	private void getDefaultDhcpOsVersions(){
		DHCP_OSVERSIONS_DEFAULT = new ArrayList<>();
		List<OsVersion> osVersions = QueryUtil.executeQuery(OsVersion.class, new SortParams("osVersion"), new FilterParams("owner.domainName", HmDomain.GLOBAL_DOMAIN));
		for(OsVersion osVersion : osVersions){
			DHCP_OSVERSIONS_DEFAULT.add(osVersion.getOsVersion());
		}
		
		// remove duplicate
		HashSet<String> h = new HashSet<>(DHCP_OSVERSIONS_DEFAULT);
		DHCP_OSVERSIONS_DEFAULT.clear();
		DHCP_OSVERSIONS_DEFAULT.addAll(h);
	}
	
	private String hideCreateItem = "none";

	public String getHideCreateItem() {
		return hideCreateItem;
	}

	private String hideNewButton = "";

	public String getHideNewButton() {
		return hideNewButton;
	}
	
	private String hideDhcpNewButton = "";
	private String hideDhcpCreateItem = "none";
	
	public String getHideDhcpNewButton() {
		return hideDhcpNewButton;
	}

	public String getHideDhcpCreateItem() {
		return hideDhcpCreateItem;
	}
	
	// ID of table columns in list view
	public static final int COLUMN_NAME = 1;

	public static final int COLUMN_VERSION = 2;

	/**
	 * get the description of column by id
	 * 
	 * @param id
	 *            -
	 * @return String
	 */
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
	
	public Long getDhcpOsVersionId() {
		return dhcpOsVersionId;
	}

	public void setDhcpOsVersionId(Long dhcpOsVersionId) {
		this.dhcpOsVersionId = dhcpOsVersionId;
	}

	private String errorMsgStr = "";

	public String getErrorMsgStr() {
		return errorMsgStr;
	}

	public void setErrorMsgStr(String errorMsgStr) {
		this.errorMsgStr = errorMsgStr;
	}
	
	private void sortValuesByAlpha(List<CheckItem> options) {
		 Collections.sort(options, new Comparator<CheckItem>() {
 			@Override
 			public int compare(CheckItem o1, CheckItem o2) {
 				return o1.getValue().compareToIgnoreCase(o2.getValue());
 			}
 		});
	 }
	 
		private String readTxtFile(String fileName){
			String read;
			FileReader fileread = null;
			BufferedReader bufread = null;
			String readStr = "";
			try {
				fileread = new FileReader(fileName);
				bufread = new BufferedReader(fileread);
				while((read = bufread.readLine()) != null){
					if(!(read.startsWith(ImportTextFileAction.VERSION_STR))){
						readStr += read + "\r\n";
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (bufread != null) {
					try {
						bufread.close();
					} catch (IOException e) {
						log.error("readTxtFile", "IO Close Error", e);
					}
				}

				if (fileread != null) {
					try {
						fileread.close();
					} catch (IOException e) {
						log.error("readTxtFile", "IO Close Error", e);
					}
				}
			}
			
			return readStr;
		}
		
		public InputStream getInputStream() throws Exception {
			return inputStream;
		}
		
		public void setInputStream(InputStream inputStream) {
			this.inputStream = inputStream;
		}
		
		public String getExportFileType() {
			return exportFileType;
		}

		public void setExportFileType(String exportFileType) {
			this.exportFileType = exportFileType;
		}
		
		public String getExportFileName() {
			return exportFileName;
		}

		public void setExportFileName(String exportFileName) {
			this.exportFileName = exportFileName;
		}

}