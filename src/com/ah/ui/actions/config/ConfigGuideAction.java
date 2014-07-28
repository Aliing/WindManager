package com.ah.ui.actions.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.ah.be.cloudauth.HmCloudAuthCertMgmtImpl;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.SendMailUtil;
import com.ah.be.communication.RemotePortalOperationRequest;
import com.ah.be.communication.mo.UserInfo;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateType;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.PagingImpl;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.hiveap.HiveApUpdateAction;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;
import com.ah.util.SupportAccessUtil;
import com.ah.util.Tracer;
import com.ah.util.bo.DeviceFreevalUtil;

public class ConfigGuideAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	
	private static final Tracer log = new Tracer(ConfigGuideAction.class
			.getSimpleName());
	
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("configHiveAp".equals(operation)
					|| "configUserProfile".equals(operation)
					|| "configSsid".equals(operation)
					|| "configRadiusGroup".equals(operation)
					|| "configPskGroup".equals(operation)
					|| "configRadiusUser".equals(operation)
					|| "configPskUser".equals(operation)
					|| "configUser".equals(operation)
					|| "configWlanPolicy".equals(operation)
					|| "configHiveApUpdates".equals(operation)
					|| "newHiveAps".equals(operation)
					|| "managedHiveAps".equals(operation)) {
				log.info("execute", "operation:" + operation);
				if ("configHiveApUpdates".equals(operation)) {
					saveSelectedHiveAPs();
				}
				return operation;
			} else if ("addSsid".equals(operation)
					|| "addUserProfile".equals(operation)
					|| "addWlanPolicy".equals(operation)) {
				log.info("execute", "operation:" + operation);
				addLstForward("guidedConfiguration");
				return operation;
			} else if ("support".equals(operation)) {
				log.info("execute", "operation:" + operation);
				addLstForward("guidedConfiguration");
				sendMail();
				return "json";
			} else if ("tellFriend".equals(operation)) {
				log.info("execute", "operation:" + operation);
				addLstForward("guidedConfiguration");
				sendMail();
				return "json";
			} else if ("logout".equals(operation)) {
				log.info("execute", "operation:" + operation);
				addLstForward("guidedConfiguration");
				CurrentUserCache.getInstance().invalidateSession(
						request.getSession().getId());
				this.jsonObject = new JSONObject();
				jsonObject.put("success", true);
				return "json";
			} else if("checkWLANChangeStatus".equals(operation)){
				log.info("execute", "operation:" + operation);
				jsonObject = new JSONObject();
				jsonObject.put("isChanged", isChangedExWLANConfig());
				
				return "json";
			} else {
				if (!isEasyMode()){
				removeSessionAttributes();
				}
				prepareDependentObjects();
				prepareAvailableHiveAps();
				if(isEasyMode()){
					// prepare SSIDs
					prepareSSIDs();
				}
				
				if("ssid".equals(this.getLastExConfigGuide())) {
					setDataSource(SsidProfile.class);
					if (ssids.size()==0 && (dataSource==null || (getRemoveSsidSession()!=null && dataSource.getId().equals(getRemoveSsidSession())))) {
						setSessionDataSource(new SsidProfile());
					} else {
						if (getRemoveSsidSession()!=null && dataSource.getId().equals(getRemoveSsidSession())) {
							clearDataSource();
							dataSource=null;
							MgrUtil.removeSessionAttribute("express-remove-ssidid");
						}
					}
					setInitSsidProfileId();
					setInitSsidProfileName();
					if ("backExUserProfile".equals(operation)) {
						setDataSource(UserProfile.class);
						setInitUserProfileId();
					}
					
					if ("backExCwp".equals(operation)) {
					}
				}
				
				if("hiveapEx".equals(this.getLastExConfigGuide())){
					HiveAp hiveap = (HiveAp) MgrUtil.getSessionAttribute(HiveAp.class.getSimpleName()
							+ "Source");
					if(hiveap != null && hiveap.getId() != null){
						this.setFromEditAp(hiveap.getId());
					}
				}
				
				if(!isHMOnline()||getUserContext().isVadAdmin()||getUserContext().isSuperUser()||getUserContext().getSwitchDomain()!=null)
				{
					setHideAccessMessage(true);
				}
//				if(!isHMOnline()||getUserContext().isVadAdmin()||getUserContext().getSwitchDomain()!=null)
//				{
//					setHideAccessMessage(true);
//				}
				
				new HmCloudAuthCertMgmtImpl().refreshIDManagerStatus(getDomain().getId(), userContext);
				
				return isEasyMode() ? "successEx" : "success2";
			}
		} catch (Exception e) {
			log.error(e);
			addActionError(MgrUtil.getUserMessage(e));
			return isEasyMode() ? "successEx" : "success2";
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {

		
		if (bo instanceof ConfigTemplate) {
			ConfigTemplate configTemplate = (ConfigTemplate) bo;

			for (ConfigTemplateSsid tmpTemplate : configTemplate.getSsidInterfaces().values()) {
				if (tmpTemplate.getSsidProfile() != null) {
					if (tmpTemplate.getSsidProfile().getRadiusUserProfile() != null) {
						tmpTemplate.getSsidProfile().getRadiusUserProfile().size();
					}
					if (tmpTemplate.getSsidProfile().getUserProfileDefault() != null) {
						tmpTemplate.getSsidProfile().getUserProfileDefault().getId();
					}
					if (tmpTemplate.getSsidProfile().getUserProfileSelfReg() != null) {
						tmpTemplate.getSsidProfile().getUserProfileSelfReg().getId();
					}
					if (null != tmpTemplate.getSsidProfile().getLocalUserGroups()) {
						tmpTemplate.getSsidProfile().getLocalUserGroups().size();
					}
				}
			}
		}
		return null;
	}
	
	private Long ssidProfileId;
	private Long userProfileId;
	private String ssidProfileName;
	public void setInitSsidProfileId(){
		if(null == dataSource) {
			ssidProfileId= null;
		} else {
			if (dataSource instanceof SsidProfile) {
				ssidProfileId = dataSource.getId();
			} else {
				ssidProfileId= null;
			}
		}
	}
	public void setInitUserProfileId(){
		if(null == dataSource) {
			userProfileId= null;
		} else {
			if (dataSource instanceof UserProfile) {
				userProfileId = dataSource.getId();
			} else {
				userProfileId= null;
			}
		}
	}
	public void setInitSsidProfileName(){
		if(null == dataSource) {
			ssidProfileName= null;
		} else {
			if (dataSource instanceof SsidProfile) {
				ssidProfileName = ((SsidProfile)dataSource).getSsidName();
			} else {
				ssidProfileName= null;
			}
		}
	}
	
	public Long getUserProfileId(){
		if (userProfileId==null) {
			setInitUserProfileId();
		}
		return userProfileId;
	}
	/**
	 * Get SSID's id, for Config-Guided. 
	 * 
	 * @author Yunzhi Lin
	 * - Time: Mar 11, 2011 2:43:39 PM
	 * @return
	 */
	public Long getSsidProfileId(){
		if (ssidProfileId==null) {
			setInitSsidProfileId();
		}
		return ssidProfileId;
//		if(null == dataSource)
//			return null;
//		else 
//			if (dataSource instanceof SsidProfile) {
//				return dataSource.getId();
//			}
//		return null;
	}
	
	public String getSsidProfileName(){
		if (ssidProfileName==null) {
			setInitSsidProfileName();
		}
		
		return ssidProfileName==null?null:ssidProfileName.replace("'", "\"");
//		if(null == dataSource)
//			return null;
//		else 
//			if (dataSource instanceof SsidProfile) {
//				return ((SsidProfile)dataSource).getSsidName();
//			}
//			return null;
	}
	
	public int getSsidCount() {
		return null==ssids ? 0 : ssids.size();
	}
	
	/**
	 * Get the page size of the HiveAP
	 *  
	 * @author Yunzhi Lin
	 * - Time: Mar 30, 2011 3:46:54 PM
	 * @return
	 */
	private int getHiveAPPageSize() {
		@SuppressWarnings("unchecked")
		Paging<HiveAp> hiveAPpaging = (Paging<HiveAp>) MgrUtil.getSessionAttribute(HiveAp.class
				.getSimpleName() + "Paging");
		return null == hiveAPpaging ? PagingImpl.DEFAULT_PAGE_SIZE : hiveAPpaging.getPageSize();
	}
	
	/**
	 * check get the hiveAP size(the maxResults equal to the pageSize + 5)
	 * 
	 * @author Yunzhi Lin
	 * - Time: Mar 30, 2011 3:37:57 PM
	 * @return
	 */
	public int getHiveAPsCount() {
		List<Short> status = new ArrayList<Short>();
		status.add(HiveAp.STATUS_MANAGED);
		status.add(HiveAp.STATUS_NEW);
		FilterParams hiveAPfilter = new FilterParams("manageStatus", status);
		List<HiveAp> aplist = QueryUtil.executeQuery(HiveAp.class, null, hiveAPfilter, getHiveAPPageSize() + 5);
		return null==aplist? 0 : aplist.size();
	}
	
	public int getHiveAPHeight() {
		int descriptionHeight = 135, totalHeight = 0, rowHeight = 23, blankHeight = rowHeight;
		int criticalPagesize = 30;
		int count = getHiveAPsCount();
		
		if(count == 0){
			totalHeight = descriptionHeight + blankHeight * 4;
		} else {
			int pageSize = getHiveAPPageSize();
			if(pageSize <= criticalPagesize){
				if(count < pageSize){
					totalHeight = descriptionHeight + count * rowHeight + blankHeight;
				}else {
					totalHeight = descriptionHeight + pageSize * rowHeight + blankHeight;
				}
			}else{
				if(count <= criticalPagesize){
					totalHeight = descriptionHeight + count * rowHeight + blankHeight;
				}else{
					totalHeight = descriptionHeight + criticalPagesize * rowHeight + blankHeight;
				}
			}
		}
		return totalHeight;
	}
	
	/**
	 * prepare SSIDs for new 'Configuration Guide' page
	 * 
	 * @author Yunzhi Lin
	 * - Time: Mar 8, 2011 4:01:01 PM
	 */
	private void prepareSSIDs() {
		
		FilterParams ssidFilter = new FilterParams("ssidName not in(:s1)", 
				new Object[]{Arrays.asList(BeParaModule.SSID_PROFILE_NAMES)});
		ssids = QueryUtil.executeQuery(SsidProfile.class, null, ssidFilter, getDomainId());
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CONFIGURATION_GUIDE);
	}

	private void saveSelectedHiveAPs() {
		if (null != hiveApList) {
			MgrUtil.setSessionAttribute(HiveApUpdateAction.UPDATE_INITIAL_IDs,
					hiveApList);
		} else {
			MgrUtil.setSessionAttribute(HiveApUpdateAction.UPDATE_INITIAL_IDs,
					new HashSet<Long>());
		}
	}

	private void prepareDependentObjects() {
		long managedCount, newCount, managedPendCount, ssidCount, wlanCount;
		long userProfileCount, radiusGroupCount, pskGroupCount, radiusUserCount, pskUserCount;
		// List<?> ssidList, userProfileList, wlanPolicyList;

		Collection<Integer> types = new ArrayList<Integer>(2);
		types.add(LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK);
		types.add(LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK);
		if (getShowDomain()) {
			// HiveAP count
			String where = "manageStatus = :s1";
			Object[] values = new Object[] { HiveAp.STATUS_NEW };
			newCount = QueryUtil.findRowCount(HiveAp.class, new FilterParams(
					where, values));
			where = "manageStatus = :s1";
			values = new Object[] { HiveAp.STATUS_MANAGED };
			managedCount = QueryUtil.findRowCount(HiveAp.class,
					new FilterParams(where, values));
			where = "manageStatus = :s1 and (pending = :s2 or pending_user = :s3)";
			values = new Object[] { HiveAp.STATUS_MANAGED, true, true };
			managedPendCount = QueryUtil.findRowCount(HiveAp.class,
					new FilterParams(where, values));

			/*-
			// SSID Profile
			ssidList = QueryUtil.executeQuery(
					"select id, ssidName, owner.id from "
							+ SsidProfile.class.getSimpleName(),
					new SortParams("id", false), null);

			// WLAN Policy
			wlanPolicyList = QueryUtil.executeQuery(
					"select id, configName, owner.id from "
							+ ConfigTemplate.class.getSimpleName(),
					new SortParams("id", false), null);

			// User Profile
			userProfileList = QueryUtil.executeQuery(
					"select id, userProfileName, owner.id from "
							+ UserProfile.class.getSimpleName(),
					new SortParams("id", false), null);
			 */

			// Local User count
			radiusGroupCount = QueryUtil.findRowCount(LocalUserGroup.class,
					new FilterParams("userType",
							LocalUserGroup.USERGROUP_USERTYPE_RADIUS));
			radiusUserCount = QueryUtil.findRowCount(LocalUser.class,
					new FilterParams("userType",
							LocalUserGroup.USERGROUP_USERTYPE_RADIUS));
			pskGroupCount = QueryUtil.findRowCount(LocalUserGroup.class,
					new FilterParams("userType", types));
			where = "userType in (:s1) and revoked = :s2";
			values = new Object[] { types, false };
			pskUserCount = QueryUtil.findRowCount(LocalUser.class,
					new FilterParams(where, values));
		} else {
			if (isEasyMode()) {
				String where = "manageStatus = :s1 and owner.id = :s2";
				Object[] values = new Object[] { HiveAp.STATUS_NEW, domainId };
				newCount = QueryUtil.findRowCount(HiveAp.class,
						new FilterParams(where, values));
				where = "manageStatus = :s1 and owner.id = :s2";
				values = new Object[] { HiveAp.STATUS_MANAGED, domainId };
				managedCount = QueryUtil.findRowCount(HiveAp.class,
						new FilterParams(where, values));

				where = "manageStatus = :s1 and (pending = :s2 or pending_user = :s3) and owner.id = :s4";
				values = new Object[] { HiveAp.STATUS_MANAGED, true, true,
						domainId };
				managedPendCount = QueryUtil.findRowCount(HiveAp.class,
						new FilterParams(where, values));

				/*-
				// SSID Profile
				ssidList = QueryUtil.executeQuery(
						"select id, ssidName, owner.id from "
								+ SsidProfile.class.getSimpleName(),
						new SortParams("id", false), new FilterParams(
								"owner.id", domainId));

				// WLAN Policy
				wlanPolicyList = QueryUtil.executeQuery(
						"select id, configName, owner.id from "
								+ ConfigTemplate.class.getSimpleName(),
						new SortParams("id", false), new FilterParams(
								"owner.id", domainId));

				// User Profile
				userProfileList = QueryUtil.executeQuery(
						"select id, userProfileName, owner.id from "
								+ UserProfile.class.getSimpleName(),
						new SortParams("id", false), new FilterParams(
								"owner.id", domainId));
				 */

				// Local User count
				where = "userType = :s1 and owner.id = :s2";
				values = new Object[] {
						LocalUserGroup.USERGROUP_USERTYPE_RADIUS, domainId };
				radiusGroupCount = QueryUtil.findRowCount(LocalUserGroup.class,
						new FilterParams(where, values));
				radiusUserCount = QueryUtil.findRowCount(LocalUser.class,
						new FilterParams(where, values));

				where = "userType in (:s1) and owner.id = :s2";
				values = new Object[] { types, domainId };
				pskGroupCount = QueryUtil.findRowCount(LocalUserGroup.class,
						new FilterParams(where, values));
				where = "userType in (:s1) and revoked = :s2 and owner.id = :s3";
				values = new Object[] { types, false, domainId };
				pskUserCount = QueryUtil.findRowCount(LocalUser.class,
						new FilterParams(where, values));
			} else {
				String where = "manageStatus = :s1 and owner.id = :s2";
				Object[] values = new Object[] { HiveAp.STATUS_NEW, domainId };
				newCount = QueryUtil.findRowCount(HiveAp.class,
						new FilterParams(where, values));
				where = "manageStatus = :s1 and owner.id = :s2";
				values = new Object[] { HiveAp.STATUS_MANAGED, domainId };
				managedCount = QueryUtil.findRowCount(HiveAp.class,
						new FilterParams(where, values));

				where = "manageStatus = :s1 and (pending = :s2 or pending_user = :s3) and owner.id = :s4";
				values = new Object[] { HiveAp.STATUS_MANAGED, true, true,
						domainId };
				managedPendCount = QueryUtil.findRowCount(HiveAp.class,
						new FilterParams(where, values));
				/*-
				// SSID Profile
				ssidList = QueryUtil.executeQuery(
						"select id, ssidName, owner.id from "
								+ SsidProfile.class.getSimpleName(),
						new SortParams("id", false), null, domainId);

				// WLAN Policy
				wlanPolicyList = QueryUtil.executeQuery(
						"select id, configName, owner.id from "
								+ ConfigTemplate.class.getSimpleName(),
						new SortParams("id", false), null, domainId);

				// User Profile
				userProfileList = QueryUtil.executeQuery(
						"select id, userProfileName, owner.id from "
								+ UserProfile.class.getSimpleName(),
						new SortParams("id", false), null, domainId);
				 */

				// Local User count
				where = "userType = :s1 and owner.id = :s2";
				values = new Object[] {
						LocalUserGroup.USERGROUP_USERTYPE_RADIUS, domainId };
				radiusGroupCount = QueryUtil.findRowCount(LocalUserGroup.class,
						new FilterParams(where, values));
				radiusUserCount = QueryUtil.findRowCount(LocalUser.class,
						new FilterParams(where, values));

				where = "userType in (:s1) and owner.id = :s2";
				values = new Object[] { types, domainId };
				pskGroupCount = QueryUtil.findRowCount(LocalUserGroup.class,
						new FilterParams(where, values));
				where = "userType in (:s1) and revoked = :s2 and owner.id = :s3";
				values = new Object[] { types, false, domainId };
				pskUserCount = QueryUtil.findRowCount(LocalUser.class,
						new FilterParams(where, values));
			}
		}

		List<?> ssidList = new ArrayList<SsidProfile>();
		List<?> userProfileList = new ArrayList<UserProfile>();
		List<?> wlanPolicyList = new ArrayList<ConfigTemplate>();

		ssidCount = ssidList.size();
		if (!ssidList.isEmpty()) {
			ssidItems = new ArrayList<SsidProfile>();
			for (Object object : ssidList) {
				if (ssidItems.size() >= MAX_COUNT_SHOW) {
					break;
				}
				Object[] attrs = (Object[]) object;
				SsidProfile item = new SsidProfile();
				item.setId((Long) attrs[0]);
				item.setSsidName((String) attrs[1]);
				HmDomain owner = new HmDomain();
				owner.setId((Long) attrs[2]);
				item.setOwner(owner);
				ssidItems.add(item);
			}
		}

		userProfileCount = userProfileList.size();
		if (!userProfileList.isEmpty()) {
			userProfileItems = new ArrayList<UserProfile>();
			for (Object object : userProfileList) {
				if (userProfileItems.size() >= MAX_COUNT_SHOW) {
					break;
				}
				Object[] attrs = (Object[]) object;
				UserProfile item = new UserProfile();
				item.setId((Long) attrs[0]);
				item.setUserProfileName((String) attrs[1]);
				HmDomain owner = new HmDomain();
				owner.setId((Long) attrs[2]);
				item.setOwner(owner);
				userProfileItems.add(item);
			}
		}

		wlanCount = wlanPolicyList.size();
		if (!wlanPolicyList.isEmpty()) {
			wlanPolicyItems = new ArrayList<ConfigTemplate>();
			for (Object object : wlanPolicyList) {
				if (wlanPolicyItems.size() >= MAX_COUNT_SHOW) {
					break;
				}
				Object[] attrs = (Object[]) object;
				ConfigTemplate item = new ConfigTemplate(ConfigTemplateType.WIRELESS);
				item.setId((Long) attrs[0]);
				item.setConfigName((String) attrs[1]);
				HmDomain owner = new HmDomain();
				owner.setId((Long) attrs[2]);
				item.setOwner(owner);
				wlanPolicyItems.add(item);
			}
		}

		if (radiusGroupCount > 0 || pskGroupCount > 0) {
			showUserSection = true;
		}

		hiveApDetail = "(Currently there are <b class='countItem'>"
				+ (managedCount > 0 ? "<a href=\"#managedHiveAps\" onclick=\"submitAction('managedHiveAps')\">"
						+ String.valueOf(managedCount) + "</a>"
						: String.valueOf(managedCount))
				+ "</b> managed "
				+ (managedCount > 1 ? NmsUtil.getOEMCustomer().getAccessPonitName() + "s" : NmsUtil.getOEMCustomer().getAccessPonitName())
				+ " and  <b class='countItem'>"
				+ (newCount > 0 ? "<a href=\"#newHiveAps\" onclick=\"submitAction('newHiveAps')\">"
						+ String.valueOf(newCount) + "</a>"
						: String.valueOf(newCount)) + "</b> new "
				+ (newCount > 1 ? NmsUtil.getOEMCustomer().getAccessPonitName() + "s" : NmsUtil.getOEMCustomer().getAccessPonitName()) + ".)";
		ssidDetail = "(Currently there are <b class='countItem'>"
				+ (ssidCount > 0 ? "<a href=\"#configSsid\" onclick=\"submitAction('configSsid')\">"
						+ String.valueOf(ssidCount) + "</a>"
						: String.valueOf(ssidCount))
				+ "</b> SSIDs"
				+ (ssidCount > MAX_COUNT_SHOW ? ", latest defined: "
						: (ssidCount > 0 ? " defined: " : " defined.)"));

		wlanPolicyDetail = "(Currently there are <b class='countItem'>"
				+ (wlanCount > 0 ? "<a href=\"#configWlanPolicy\" onclick=\"submitAction('configWlanPolicy')\">"
						+ String.valueOf(wlanCount) + "</a>"
						: String.valueOf(wlanCount))
				+ "</b> network policies"
				+ (wlanCount > MAX_COUNT_SHOW ? ", latest defined: "
						: (wlanCount > 0 ? " defined: " : " defined.)"));

		userProfileDetail = "(Currently there are <b class='countItem'>"
				+ (userProfileCount > 0 ? "<a href=\"#configUserProflie\" onclick=\"submitAction('configUserProfile')\">"
						+ String.valueOf(userProfileCount) + "</a>"
						: String.valueOf(userProfileCount))
				+ "</b> user profiles"
				+ (userProfileCount > MAX_COUNT_SHOW ? ", latest defined: "
						: (userProfileCount > 0 ? " defined: " : " defined.)"));

		if (isEasyMode()) {
			userDetail = "(Currently there are <b class='countItem'>"
					+ (radiusUserCount > 0 ? "<a href=\"#configRadiusUser\" onclick=\"submitAction('configRadiusUser')\">"
							+ String.valueOf(radiusUserCount) + "</a>"
							: String.valueOf(radiusUserCount))
					+ "</b> RADIUS users, and <b class='countItem'>"
					+ (pskUserCount > 0 ? "<a href=\"#configPskUser\" onclick=\"submitAction('configPskUser')\">"
							+ String.valueOf(pskUserCount) + "</a>"
							: String.valueOf(pskUserCount))
					+ "</b> private PSK users.)";
		} else {
			userDetail = "(Currently there are <b class='countItem'>"
					+ (radiusGroupCount > 0 ? "<a href=\"#configRadiusGroup\" onclick=\"submitAction('configRadiusGroup')\">"
							+ String.valueOf(radiusGroupCount) + "</a>"
							: String.valueOf(radiusGroupCount))
					+ "</b> RADIUS user groups with <b class='countItem'>"
					+ (radiusUserCount > 0 ? "<a href=\"#configRadiusUser\" onclick=\"submitAction('configRadiusUser')\">"
							+ String.valueOf(radiusUserCount) + "</a>"
							: String.valueOf(radiusUserCount))
					+ "</b> users, and <b class='countItem'>"
					+ (pskGroupCount > 0 ? "<a href=\"#configPskGroup\" onclick=\"submitAction('configPskGroup')\">"
							+ String.valueOf(pskGroupCount) + "</a>"
							: String.valueOf(pskGroupCount))
					+ "</b> private PSK user groups with <b class='countItem'>"
					+ (pskUserCount > 0 ? "<a href=\"#configPskUser\" onclick=\"submitAction('configPskUser')\">"
							+ String.valueOf(pskUserCount) + "</a>"
							: String.valueOf(pskUserCount))
					+ "</b> private PSK users.)";
		}

		updateDetail = "(There " + (managedPendCount > 1 ? "are" : "is")
				+ " currently " + managedPendCount + " managed "
				+ (managedPendCount > 1 ? NmsUtil.getOEMCustomer().getAccessPonitName()+ "s" : NmsUtil.getOEMCustomer().getAccessPonitName())
				+ " with pending changes that need to be updated.)";
	}

	private void prepareAvailableHiveAps() throws Exception {
		List<CheckItem> availableHiveAps = getBoCheckItems("hostName",
				HiveAp.class, null, new SortParams("hostName"));
		// For the OptionsTransfer component
		hiveApOptions = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				availableHiveAps, new ArrayList<CheckItem>(), "id", "value",
				"hiveApList");
	}

	private void sendMail() throws Exception {
		jsonObject = new JSONObject();
		jsonObject.put("success", false);
		/*
		 * get mail settings
		 */
		// HmDomain home = QueryUtil.findBoByAttribute(HmDomain.class,
		// "domainName",
		// HmDomain.HOME_DOMAIN);
		List<MailNotification> mailNotification = QueryUtil.executeQuery(
				MailNotification.class, null, null, BoMgmt.getDomainMgmt()
						.getHomeDomain().getId());

		if (!mailNotification.isEmpty()) {
			MailNotification mail = mailNotification.get(0);
			String serverName = mail.getServerName();
			String mailFrom = mail.getMailFrom();

			if (serverName == null || serverName.equals("")) {
				jsonObject.put("msg", MgrUtil
						.getUserMessage("error.config.guide.no.smtp"));
				return;
			}

			if (mailFrom == null || mailFrom.equals("")) {
				jsonObject.put("msg", MgrUtil
						.getUserMessage("error.config.guide.no.source"));
				return;
			}
		} else {
			jsonObject.put("msg", MgrUtil.getUserMessage(
					"error.gml.email.setting.wrong", "Email setting"));
			return;
		}

		if (send(mailNotification)) {
			jsonObject.put("success", true);

			if ("support".equals(this.operation)) {
				jsonObject
						.put(
								"msg",
								MgrUtil
										.getUserMessage("info.config.guide.support.success")
										+ "\n"
										+ MgrUtil
												.getUserMessage("info.config.guide.support.success.1"));
			} else if ("tellFriend".equals(this.operation)) {
				jsonObject
						.put(
								"msg",
								MgrUtil
										.getUserMessage("info.config.guide.tellFriend.success"));
			}
		} else {
			if ("support".equals(this.operation)) {
				jsonObject.put("msg", MgrUtil
						.getUserMessage("info.config.guide.support.failed"));
			} else if ("tellFriend".equals(this.operation)) {
				if (jsonObject.get("msg") == null) {
					jsonObject
							.put(
									"msg",
									MgrUtil
											.getUserMessage("info.config.guide.tellFriend.failed"));
				}
			}
		}
	}

	private boolean send(List<MailNotification> mailNotification) {
		if (mailNotification != null && !mailNotification.isEmpty()) {
			SendMailUtil mailUtil = new SendMailUtil(mailNotification.get(0));
			mailUtil.setMailContentType("text/html");

			if ("support".equals(this.operation)) {
				mailUtil.setFromEmail(this.contactorEmail);
				mailUtil.setMailTo(NmsUtil.getSupportMail());
				mailUtil.setSubject(MgrUtil
						.getUserMessage("info.email.support.title"));
				mailUtil.setText(getMessageHtml());
			} else if ("tellFriend".equals(this.operation)) {
				mailUtil.setMailTo(mailAddress);

				StringBuffer content = new StringBuffer();
				// title
				if (this.shareAccount) {
					mailUtil.setSubject(MgrUtil
							.getUserMessage("info.email.share.friend.title"));
				} else {
					mailUtil.setSubject(MgrUtil
							.getUserMessage("info.email.tell.friend.title"));
				}

				setTopContentHtml(content, shareAccount);

				if (this.shareAccount) {
					if (!setCenterContentHtml(content)) {
						return false;
					}
				}

				setBottomContentHtml(content);
				mailUtil.setText(content.toString());
				mailUtil.addShowfile(AhDirTools.getHmRoot() + "images"
						+ File.separator + "company_logo.png");
			}

			try {
				mailUtil.startSend();
			} catch (Exception e) {
				return false;
			}
		}

		return true;
	}

	private String getMessageHtml() {
		StringBuffer content = new StringBuffer();
		content.append("<html><body><table border=\"0\">");
		content.append("<tr><td width=\"150px\">");
		content.append("Customer Name:");
		content.append("</td><td>");

		if (this.getUserContext().getUserFullName() != null) {
			content.append(this.getUserContext().getUserFullName());
		} else {
			content.append(this.getUserContext().getUserName());
		}

		content.append("</td></tr><tr><td width=\"150px\">");
		content.append("Email:");
		content.append("</td><td><a href=\"mailto:");
		content.append(this.getUserContext().getEmailAddress());
		content.append("\">").append(this.getUserContext().getEmailAddress())
				.append("</a>");
		content.append("</td></tr><tr><td width=\"150px\">");
		content.append("Company Name:");
		content.append("</td><td>");
		content.append(this.getDomain().getDomainName());
		content.append("</td></tr><tr><td width=\"150px\">");
		content.append(MgrUtil.getUserMessage("config.guide.email.vhmid"));
		content.append("</td><td>");
		content.append(this.getDomain().getVhmID());
		content.append("</td></tr><tr><td width=\"150px\">");
		content.append("Telephone:");
		content.append("</td><td>");
		content.append(this.userPhone == null ? "" : this.userPhone);
		content.append("</td></tr><tr><td width=\"150px\">");
		content.append("Country:");
		content.append("</td><td>");
		content.append(this.country == null ? "" : this.country);
		content.append("</td></tr><tr><td width=\"150px\">");
		content.append("Message:");
		content.append("</td><td>");
		content.append(reportedIssues.replace("\n", "<br>"));
		content.append("</td></tr>");

		content.append("</table></body><html>");

		return content.toString();
	}

	private void setTopContentHtml(StringBuffer content, boolean sharingAccount) {
		content
				.append("<html><body><table style='font-family: \"Times New Roman\"' border=\"0\">");
		content.append("<tr><td>");

		/*
		 * user's name
		 */
		String userName = "";
		String email;
		String label = isPlanningOnly() ? "RF Planner" : "Demo";
		if (this.getUserContext().getUserFullName() != null) {
			userName = this.getUserContext().getUserFullName();
		}
		email = this.getUserContext().getEmailAddress();

		/*
		 * text body
		 */
		if (sharingAccount) {
			content.append(
					MgrUtil.getUserMessage("info.email.share.friend",
							new String[] { userName, email, label,
								NmsUtil.getOEMCustomer().getRegisterUrl()})).append(
					"<br><br>");
			content.append("</td></tr>");
		} else {
			content.append(
					MgrUtil.getUserMessage("info.email.tell.friend",
							new String[] { userName, email, NmsUtil.getOEMCustomer().getRegisterUrl() })).append(
					"<br><br>");
			content.append("</td></tr>");
		}
	}

	private boolean setCenterContentHtml(StringBuffer content) {
		HmUser newUser = generateNewAccount();

		if (newUser == null) {
			return false;
		}

		String url = request.getRequestURL().toString();
		String newUrl = url.substring(0, url.lastIndexOf("/"));

		content
				.append("<tr><td><table style='font-family: \"Times New Roman\"' border=\"0\">");
		content.append("<tr><td>").append("URL:").append("</td><td>");
		content.append("<a href=\"").append(newUrl).append("\">")
				.append(newUrl).append("</a>").append("</td></tr>");
		content.append("<tr><td>").append("Account Login Name:").append(
				"</td><td>");
		content.append(newUser.getUserName());
		content.append("</td></tr>");
		content.append("<tr><td>").append("Password:").append("</td><td>");
		content.append(newUser.getPassword());
		content.append("</td></tr>");
		content.append("</table></td></tr>");
		content.append("<tr><td height=\"50px\">&nbsp;<br><br></td></tr>");

		return true;
	}

	private void setBottomContentHtml(StringBuffer content) {
		/*
		 * personal note
		 */
		if (this.personalNote != null && this.personalNote.trim().length() > 0) {
			content.append("<tr><td>");
			content.append("Personal note from ");

			/*
			 * user's name
			 */
			if (this.getUserContext().getUserFullName() != null) {
				content.append(this.getUserContext().getUserFullName());
			}

			String email = this.getUserContext().getEmailAddress();

			content.append("(<a href=\"mailto:").append(email).append("\">")
					.append(email).append("</a>)");
			content.append(":").append("<br>").append(personalNote).append(
					"<br>");
			content.append("</td></tr>");
		}

		content.append("<br><br>");

		/*
		 * copyright
		 */
		content.append("<tr><td>");
		content.append("Copyright ");

		/*
		 * year
		 */
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		content.append(c.get(Calendar.YEAR));
		content.append(NmsUtil.getOEMCustomer().getCompanyFullName());
		content.append("</td></tr>");

		/*
		 * logo
		 */
		// String imgPath = AhDirTools.getHmRoot() + "images" + File.separator +
		// "company_logo.png";
		content.append("<tr><td>");
		content.append("<img src=\"cid:company_logo.png\" />");
		content.append("</td></tr>");

		content.append("</table></body><html>");
	}

	private HmUser generateNewAccount() {
		/*
		 * check if the user is already existed
		 */
		List<?> users = QueryUtil.executeQuery("SELECT id from "
				+ HmUser.class.getSimpleName(), null, new FilterParams(
				"lower(emailAddress) = :s1 OR lower(userName) = :s2",
				new Object[] { mailAddress, mailAddress }));

		/*
		 * if existed, do not send email
		 */
		if (!users.isEmpty()) {
			try {
				jsonObject.put("msg", MgrUtil.getUserMessage(
						"info.config.guide.tellFriend.account.exist",
						mailAddress));

			} catch (Exception e) {
				log.error("Failed to put a String into jsonObject", e);
			}

			return null;
		}

		/*
		 * if not existed, create a new account
		 */
		HmUser newUser = QueryUtil.findBoById(HmUser.class, this
				.getUserContext().getId());
		newUser.setUserName(mailAddress);
		newUser.setEmailAddress(mailAddress);
		newUser.setDefaultFlag(false);
		newUser.setId(null);
		newUser.setVersion(null);

		String password = NmsUtil.genRandomString(8);

		try {
			newUser.setPassword(MgrUtil.digest(password));
		} catch (Exception ex) {
			log.error("Failed to digest of the password: " + password, ex);

			try {
				jsonObject
						.put(
								"msg",
								MgrUtil
										.getUserMessage("error.config.guide.tellFriend.account.create"));
			} catch (Exception jsonE) {
				log.error("Failed to put a String into jsonObject", jsonE);
			}

			return null;
		}

		try {
			if (NmsUtil.isHostedHMApplication()) {
				UserInfo userInfo = getUserInfo(newUser);

				RemotePortalOperationRequest.createVhmUser(userInfo);
			}
		} catch (Exception e) {
			log.error("Failed to create an user account on remote server", e);
			generateAuditLog(HmAuditLog.STATUS_FAILURE,
					MgrUtil.getUserMessage("hm.audit.log.config.share.create.remote.user.failure") + e.getMessage());

			try {
				jsonObject
						.put(
								"msg",
								MgrUtil
										.getUserMessage("error.config.guide.tellFriend.account.create"));
			} catch (Exception jsonE) {
				log.error("Failed to put a String into jsonObject", jsonE);
			}

			return null;
		}

		try {
			QueryUtil.createBo(newUser);
		} catch (Exception e) {
			log.error("Failed to create an user account on local server", e);
			generateAuditLog(HmAuditLog.STATUS_FAILURE,
					MgrUtil.getUserMessage("hm.audit.log.config.share.create.local.user.failure") + e.getMessage());

			try {
				jsonObject
						.put(
								"msg",
								MgrUtil
										.getUserMessage("error.config.guide.tellFriend.account.create"));
			} catch (Exception jsonE) {
				log.error("Failed to put a String into jsonObject", jsonE);
			}

			return null;
		}

		newUser.setPassword(password);

		return newUser;
	}

	private UserInfo getUserInfo(HmUser user) throws Exception {
		UserInfo info = new UserInfo();
		info.setEmailAddress(user.getEmailAddress());
		info.setFullname(user.getUserFullName());
		info.setPassword(user.getPassword());
		info.setUsername(user.getUserName());
		info.setVhmName(user.getDomain().getDomainName());
		info.setGroupAttribute((short) user.getUserGroup().getGroupAttribute());
		info.setTimeZone(user.getTimeZone());

		return info;
	}

	public String getHiveName() {
		HiveProfile hive = QueryUtil.findBoByAttribute(HiveProfile.class, "defaultFlag", false,
				domainId);
		
		return hive != null ? hive.getHiveName() : "";
	}

	private OptionsTransfer hiveApOptions;
	private Set<Long> hiveApList;

	public static final int MAX_COUNT_SHOW = 5;
	private String ssidDetail = "";
	private String hiveApDetail = "";
	private String wlanPolicyDetail = "";
	private String userProfileDetail = "";
	private String userDetail = "";
	private String updateDetail = "";
	private List<SsidProfile> ssidItems;
	private List<UserProfile> userProfileItems;
	private List<ConfigTemplate> wlanPolicyItems;
	private boolean showUserSection = false;

	private String reportedIssues;
	private String userPhone;
	private String mailAddress;
	private String personalNote;
	private String contactorEmail;
	private String country;

	private boolean shareAccount;

	private long fromEditAp = -1;
	
	private List<SsidProfile> ssids;
	
	private Long removeSsidId;
	
	private String accessMessage;
	
	private boolean hideAccessMessage=false;
	
	public String getLastOpened() {
		String lastOpened = this.getLastExConfigGuide();
		return lastOpened;
	}

	public String getSsidDetail() {
		return ssidDetail;
	}

	public String getHiveApDetail() {
		return hiveApDetail;
	}

	public String getWlanPolicyDetail() {
		return wlanPolicyDetail;
	}

	public String getUserProfileDetail() {
		return userProfileDetail;
	}

	public String getUserDetail() {
		return userDetail;
	}

	public String getUpdateDetail() {
		return updateDetail;
	}

	public List<SsidProfile> getSsidItems() {
		return ssidItems;
	}

	public List<UserProfile> getUserProfileItems() {
		return userProfileItems;
	}

	public List<ConfigTemplate> getWlanPolicyItems() {
		return wlanPolicyItems;
	}

	public boolean getShowUserSection() {
		return showUserSection;
	}

	public void setReportedIssues(String issues) {
		this.reportedIssues = issues;
	}

	public void setContactorEmail(String email) {
		this.contactorEmail = email;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public void setMailAddress(String address) {
		this.mailAddress = address;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setPersonalNote(String personalNote) {
		this.personalNote = personalNote;
	}

	public void setShareAccount(boolean shared) {
		this.shareAccount = shared;
	}

	public void setHiveApList(Set<Long> hiveApList) {
		this.hiveApList = hiveApList;
	}

	public OptionsTransfer getHiveApOptions() {
		return hiveApOptions;
	}

	public List<SsidProfile> getSsids() {
		return ssids;
	}

	public void setSsids(List<SsidProfile> ssids) {
		this.ssids = ssids;
	}
	
	public long getFromEditAp(){
		return this.fromEditAp;
	}
	
	public void setFromEditAp(long fromEditAp){
		this.fromEditAp = fromEditAp;
	}

	public Long getRemoveSsidId() {
		return removeSsidId;
	}
	
	public Long getRemoveSsidSession(){
		return (Long)MgrUtil.getSessionAttribute("express-remove-ssidid");
	}

	public void setRemoveSsidId(Long removeSsidId) {
		this.removeSsidId = removeSsidId;
	}
	
	public boolean isHideAccessMessage() {
		return hideAccessMessage;
	}


	public void setHideAccessMessage(boolean hideAccessMessage) {
		this.hideAccessMessage = hideAccessMessage;
	}

	public String getAccessMessage() {
			//show reminder when user first login after changing access mode
			if(MgrUtil.getSessionAttribute("hm.domain.access.mode")==null)
			{
				HmDomain hmDomain=QueryUtil.findBoById(HmDomain.class, domainId);
				if(hmDomain.isAccessChanged())
				{
					short accessMode=hmDomain.getAccessMode();
					if(accessMode==1||accessMode==2)
					{
						Long endDate=hmDomain.getAuthorizationEndDate();
						Long currentDate=new Date().getTime();
						if(endDate>currentDate)
						{
						int leftHours=(int)((endDate-currentDate)/(long)(60*60*1000));
						accessMessage=MgrUtil.getUserMessage("info.home.start.here.access.popup.accessMode", MgrUtil.getUserMessage(SupportAccessUtil.ACCESS_OPTION_PREFIX+accessMode,String.valueOf(leftHours))+" Hours");
						accessMessage=accessMessage+MgrUtil.getUserMessage("info.home.start.here.access.popup.configGuide");
						}
						else
						{
							accessMode=0;
						}
						
					}
					else{
						accessMessage=MgrUtil.getUserMessage("info.home.start.here.access.popup.accessMode", MgrUtil.getUserMessage(SupportAccessUtil.ACCESS_OPTION_PREFIX+accessMode,""));
						if(!NmsUtil.isPlanner()&&!NmsUtil.isDemoHHM()
								&&!DeviceFreevalUtil.isHMForDeviceFreeval())
						{
							accessMessage=accessMessage+MgrUtil.getUserMessage("info.home.start.here.access.popup.configGuide");
						}
					}
					hmDomain.setAccessChanged(false);
					try {
						BoMgmt.getDomainMgmt().updateDomain(hmDomain);
					} catch (Exception e) {
						log.error("ConfigGuideAction,getAccessMessage()", "update accessMode status fails\n", e);
						e.printStackTrace();
					}
	//				MgrUtil.setSessionAttribute("configGuide.load.first", false);
				}
			}
		return accessMessage;
	}
	
	public boolean isJumpFromIDM() {
	    return false;
	}
}