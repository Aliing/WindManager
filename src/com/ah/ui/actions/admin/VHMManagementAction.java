package com.ah.ui.actions.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmExpressModeEnable;
import com.ah.bo.admin.HmLoginAuthentication;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.Application;
import com.ah.bo.network.NetworkService;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.util.EnumConstUtil;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class VHMManagementAction extends BaseAction {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer	log					= new Tracer(VHMManagementAction.class
															.getSimpleName());

	/**
	 * max ap Number remaining
	 */
	private int remainingAPNum;

	/**
	 * min ap Number ap Number of Domain should not be updated to a smaller number
	 */
	private int minAPNum;

	private boolean disableVHM;

	private String userName;

	private String adminPassword;

	private String userEmailAddr;
	
	private String vhmID;
	
	private boolean resetDeviceFlag = false;

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		try {
			if ("new".equals(operation)) {
				// check max VHM number
				if (null != HmBeLicenseUtil.getLicenseInfo()) {
					int maxVHMCount = HmBeLicenseUtil.getLicenseInfo().getVhmNumber();
					if (CacheMgmt.getInstance().getCacheDomainCount() >= maxVHMCount) {
						addActionError(MgrUtil.getUserMessage("error.vhm.outofLincense.maximum", String
								.valueOf(maxVHMCount)));
	
						return prepareDomainList();
					}
				}

				// check remaining ap number
				int remaining = BoMgmt.getDomainMgmt().getRemainingMaxAPNum();
				if (remaining <= 0) {
					addActionError(MgrUtil.getUserMessage("error.vhm.create.noremainingap"));

					return prepareDomainList();
				}

				setSessionDataSource(new HmDomain());
				if (!NmsUtil.isHostedHMApplication()) {
					getGroupAttributeForDefaultGroup(true);
				}
				prepareCreatePage();
				return INPUT;
			} else if ("create".equals(operation)) {
				// check domain name
				if (checkNameExistsIgnoreDomain("lower(domainName)", getDataSource()
						.getDomainName().toLowerCase())) {
					prepareCreatePage();
					return INPUT;
				}
				
				// check email address
				if (checkEmailAddressUnique()) {
					prepareCreatePage();
					return INPUT;
				}
				
				// prepare vhm ID
				if (NmsUtil.isHostedHMApplication()) {
					if (checkVHMIDUnique())
					{
						prepareCreatePage();
						return INPUT;
					}
					getDataSource().setVhmID("VHM-"+vhmID);
				} 
				
				// check group ID
				if (!NmsUtil.isHostedHMApplication() && checkGroupAttributeDuplicate()) {
					prepareCreatePage();
					return INPUT;
				}

				// clone or create
				HmDomain cloneObj = (HmDomain) MgrUtil.getSessionAttribute(boClass.getSimpleName()
						+ "clone");
				if (cloneObj != null) {
					try {
						Long domainID = BoMgmt.getDomainMgmt().cloneDomain(cloneObj,
								getDataSource());

						// create default user.
						boolean isSucc = createDefaultUser(domainID,cloneObj);

						generateAuditLog(isSucc ? HmAuditLog.STATUS_SUCCESS
								: HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.vhm.create") + getLastTitle() + "("
								+ getDataSource().getLabel() + ")");
					} catch (Exception e) {
						log.error("execute", "clone domain catch exception.", e);

						generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.vhm.create") + getLastTitle()
								+ " (" + getDataSource().getLabel() + ")");
						addActionError(MgrUtil.getUserMessage("action.error.db.clone.fail"));
					}
					
					CacheMgmt.getInstance().initClientOsInfoCache();
					MgrUtil.removeSessionAttribute(boClass.getSimpleName() + "clone");

					return prepareDomainList();
				}

				// create domain
				try {
					Long domainID = BoMgmt.getDomainMgmt().createDomain(getDataSource());
					initVhmApplicationService(domainID);
					// create default user.
					boolean isSucc = createDefaultUser(domainID,null);
					generateAuditLog(
							isSucc ? HmAuditLog.STATUS_SUCCESS : HmAuditLog.STATUS_FAILURE,
									MgrUtil.getUserMessage("hm.audit.log.vhm.create") + getLastTitle() + " (" + getDataSource().getLabel() + ")");
				} catch (Exception e) {
					log.error("execute", "create domain catch exception.", e);

					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.vhm.create") + getLastTitle() + "("
							+ getDataSource().getLabel() + ")");
					addActionError(MgrUtil.getUserMessage("action.error.db.create.fail"));
				}

				return prepareDomainList();
			} else if ("edit".equals(operation)) {
				forward = editBo();
				if (!NmsUtil.isHostedHMApplication()) {
					getGroupAttributeForDefaultGroup(false);
				}
				prepareEditPage();
				return forward;
			} else if ("update".equals(operation)) {
				if (dataSource == null || dataSource.getId() == null
						|| !dataSource.getId().equals(id)) {
					generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.vhm.update") + getLastTitle() + "("
							+ getDataSource().getLabel() + ")");

					throw new HmException(
							"Update object failed, session must have been shared by another browser window.",
							HmMessageCodes.STALE_SESSION_OBJECT, new String[] { "Update" });
				}

				// check current managed ap number of vhm
				int currentAPCount = getAPCountOfThisVHM();
				if (getDataSource().getMaxApNum() < currentAPCount) {
					addActionError(MgrUtil.getUserMessage("action.error.more.aps.update.fail",
							new String[]{String.valueOf(currentAPCount),String.valueOf(getDataSource().getMaxApNum())}));

					prepareEditPage();
					return INPUT;
				}
				
				// check vhm count
				if (!NmsUtil.isHostedHMApplication() && !disableVHM && getDataSource().isDisabled()) {
					int enableVhm = (int) QueryUtil.findRowCount(HmDomain.class, new FilterParams("domainName != :s1 " +
						"AND runStatus = :s2", new Object[]{HmDomain.GLOBAL_DOMAIN, HmDomain.DOMAIN_DEFAULT_STATUS }));
					int maxVHMCount = HmBeLicenseUtil.getLicenseInfo().getVhmNumber();
					if (maxVHMCount-enableVhm < 1) {
						addActionError(MgrUtil.getUserMessage("action.error.update.fail") + MgrUtil.getUserMessage("error.vhm.outofLincense.maximum", String
							.valueOf(maxVHMCount)));

						prepareEditPage();
						return INPUT;
					}
				}
				
				// check group ID
				if (!NmsUtil.isHostedHMApplication() && checkGroupAttributeDuplicate()) {
					prepareCreatePage();
					return INPUT;
				}
				
				try {
					getDataSource().setRunStatus(
							disableVHM ? HmDomain.DOMAIN_DISABLE_STATUS
									: HmDomain.DOMAIN_DEFAULT_STATUS);
					BoMgmt.getDomainMgmt().updateDomain((HmDomain) dataSource);

					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.vhm.update")+ getLastTitle() + "("
							+ dataSource.getLabel() + ")");

					addActionMessage(MgrUtil.getUserMessage(OBJECT_UPDATED, dataSource.getLabel()));
				} catch (Exception e) {
					log.error("execute", "update domain catch exception.", e);

					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.vhm.update") + getLastTitle() + "("
							+ dataSource.getLabel() + ")");
					addActionError(MgrUtil.getUserMessage("action.error.item.update.fail", dataSource.getLabel()));
				}

				return prepareDomainList();
			} else if ("clone".equals(operation)) {
				// check max VHM number
				int maxVHMCount = HmBeLicenseUtil.getLicenseInfo().getVhmNumber();
				if (CacheMgmt.getInstance().getCacheDomainCount() >= maxVHMCount) {
					addActionError(MgrUtil.getUserMessage("error.vhm.outofLincense.maximum", String
							.valueOf(maxVHMCount)));

					return prepareDomainList();
				}

				// check remaining ap number
				int remaining = BoMgmt.getDomainMgmt().getRemainingMaxAPNum();
				if (remaining <= 0) {
					addActionError(getText("error.config.vhmManagement.noAP"));

					return prepareDomainList();
				}

				long cloneId = getSelectedIds().get(0);
				HmDomain clone = (HmDomain) findBoById(boClass, cloneId);
				clone.setId(null);
				clone.setDomainName("");
				clone.setVhmID("");
				clone.setMaxApNum(clone.getMaxApNum());
				setSessionDataSource(clone);
				prepareCreatePage();
				if (!NmsUtil.isHostedHMApplication()) {
					getGroupAttributeForDefaultGroup(true);
				}

				if (clone.getMaxApNum() > remainingAPNum) {
					clone.setMaxApNum(remainingAPNum);
				}

				MgrUtil.setSessionAttribute(boClass.getSimpleName() + "clone",
						findBoById(boClass, cloneId));

				return INPUT;
			} else if ("remove".equals(operation)) {
				if (allItemsSelected) {
					List<?> boIds = QueryUtil.executeQuery("select id from "
							+ boClass.getSimpleName(), null, new FilterParams(
							"domainName != :s1 and domainName != :s2", new Object[]{HmDomain.HOME_DOMAIN,HmDomain.GLOBAL_DOMAIN}));
					Set<Long> domainIDSet = new HashSet<Long>(boIds.size());
					for (Object o : boIds) {
						domainIDSet.add((Long)o);
					}
					setAllSelectedIds(domainIDSet);
				} else {
					if (getAllSelectedIds().isEmpty()) {
						return prepareDomainList();
					}

					// check 'home' domain
					boolean bln_domain = isRemoveHomeDomain();
					if (bln_domain && getAllSelectedIds().isEmpty()) {
						addActionError(getText("error.config.vhmManagement.removeHome"));
						return prepareDomainList();
					}
				}

				// check login users
				String domainName = getUserLoginDomain();
				if (!domainName.isEmpty()) {
					addActionError(getText("error.config.vhmManagement.userLogged"));
					return prepareDomainList();
				}

				int count = 0;
				try {
					for (Long domainID : getAllSelectedIds()) {
						BoMgmt.getDomainMgmt().removeDomain(domainID, true, resetDeviceFlag);
						count++;
					}

					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.vhm.remove") + count + " "
							+ getSelectedL2Feature().getDescription());
				} catch (Exception e) {
					log.error("execute", "remove domain failed.", e);

					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.vhm.remove")
							+ getSelectedL2Feature().getDescription());
					addActionError(MgrUtil.getUserMessage("action.error.remove.operation"));
				}

				if (count == 0) {
					addActionError(MgrUtil.getUserMessage(NO_OBJECTS_REMOVED));
				} else {
					addActionMessage(MgrUtil.getUserMessage(OBJECTS_REMOVED, count + ""));
				}
				setAllSelectedIds(null);
				return prepareDomainList();
			} else {
				MgrUtil.removeSessionAttribute(boClass.getSimpleName() + "clone");
				baseOperation();
				return prepareDomainList();
			}
		} catch (Exception e) {
			prepareActionError(e);
			return prepareDomainList();
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_VHMMANAGEMENT);
		setDataSource(HmDomain.class);
		keyColumnId = COLUMN_VHMNAME;
		tableId = HmTableColumn.TABLE_VHMMANAGEMENT;
	}

	@Override
	public HmDomain getDataSource() {
		return (HmDomain) dataSource;
	}

	public boolean checkEmailAddressUnique() {
		List<?> boIds = QueryUtil.executeQuery("select id from "
				+ HmUser.class.getSimpleName(), null, new FilterParams("lower(emailAddress)", userEmailAddr.toLowerCase()));
		if (!boIds.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("action.error.email.exist", userEmailAddr));
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkVHMIDUnique() {
		List<?> boIds = QueryUtil.executeQuery("select id from "
				+ HmDomain.class.getSimpleName(), null, new FilterParams("vhmID", "VHM-"+vhmID));
		if (!boIds.isEmpty()) {
			addActionError(getText("error.config.vhmManagement.idExisted", new String[] {vhmID}));
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int	COLUMN_VHMNAME			= 1;

	public static final int	COLUMN_RUNSTATUS		= 2;

	public static final int	COLUMN_MAXAPNUM			= 3;

	public static final int	COLUMN_GMLCAPABILITY	= 4;

	public static final int	COLUMN_DESCRIPTION		= 5;
	
	public static final int	COLUMN_VHMID			= 6;
	
	public static final int	COLUMN_MAX_SINULATED_AP = 7;
	
	public static final int	COLUMN_MAX_SINULATED_CLIENT = 8;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 */
	@Override
	public final String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_VHMNAME:
			code = "admin.vhmMgr.vhm";
			break;
		case COLUMN_MAXAPNUM:
			code = "admin.vhmMgr.maxAPNum";
			break;
		case COLUMN_RUNSTATUS:
			code = "admin.vhmMgr.runStatus";
			break;
		case COLUMN_GMLCAPABILITY:
			code = "admin.vhmMgr.userMgr";
			break;
		case COLUMN_DESCRIPTION:
			code = "admin.vhmMgr.description";
			break;
		case COLUMN_VHMID:
			code = "admin.vhmMgr.vhmID";
			break;
		case COLUMN_MAX_SINULATED_AP:
			code = "admin.vhmMgr.maxSimulatedAPNum";
			break;
		case COLUMN_MAX_SINULATED_CLIENT:
			code = "admin.vhmMgr.maxSimulatedClientNum";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(8);

		columns.add(new HmTableColumn(COLUMN_VHMNAME));
		columns.add(new HmTableColumn(COLUMN_RUNSTATUS));
		columns.add(new HmTableColumn(COLUMN_GMLCAPABILITY));
		columns.add(new HmTableColumn(COLUMN_MAXAPNUM));
		if (NmsUtil.isHostedHMApplication()) {
			columns.add(new HmTableColumn(COLUMN_VHMID));
		}
		columns.add(new HmTableColumn(COLUMN_MAX_SINULATED_AP));
		columns.add(new HmTableColumn(COLUMN_MAX_SINULATED_CLIENT));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));

		return columns;
	}

	private boolean createDefaultUser(Long domainID,HmDomain cloneObj) throws Exception {
		filterParams = new FilterParams("groupName", HmUserGroup.CONFIG);
		List<HmUserGroup> list = QueryUtil.executeQuery(HmUserGroup.class, null, filterParams, domainID);
		if (list.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("action.error.create.default.domain.user",getDataSource().getDomainName()));
			return false;
		}
		HmUserGroup configGroup = list.get(0);

		HmUser user = new HmUser();
		user.setDefaultFlag(true);
		user.setUserName(userName);
		user.setPassword(MgrUtil.digest(adminPassword));
		user.setEmailAddress(userEmailAddr);
		user.setUserGroup(configGroup);
		user.setOwner(CacheMgmt.getInstance().getCacheDomainById(domainID));
		user.setAccessMyhive(true);
		
		if(null != cloneObj){
			user.setTimeZone(cloneObj.getTimeZoneString());
		}
		QueryUtil.createBo(user);

		return true;
	}
	
	private void initVhmApplicationService(Long domainID){
		List<Application> allAppList = QueryUtil.executeQuery(Application.class, null, 
				new FilterParams("appCode > :s1", new Object[] {0}));
		List<NetworkService> insertAppServices = new ArrayList<NetworkService>();
		if(!allAppList.isEmpty()){
			for(Application app : allAppList){
				if(app.getAppCode() != 0){
						NetworkService serviceDto = new NetworkService();
						serviceDto.setServiceName(NetworkService.L7_SERVICE_NAME_PREFIX+app.getAppName());
						serviceDto.setProtocolNumber(0);
						serviceDto.setPortNumber(0);				
						serviceDto.setIdleTimeout(300);
						serviceDto.setDescription(app.getAppName());
						serviceDto.setAlgType((short)0);
						serviceDto.setServiceType(NetworkService.SERVICE_TYPE_L7);
						serviceDto.setAppId(app.getAppCode());
						serviceDto.setDefaultFlag(false);
						serviceDto.setOwner(CacheMgmt.getInstance().getCacheDomainById(domainID));
						serviceDto.setCliDefaultFlag(false);
						insertAppServices.add(serviceDto);
				}
			}
		}
		try {
			QueryUtil.bulkCreateBos(insertAppServices);
		} catch (Exception e) {
			BeLogTools.debug(HmLogConst.M_TRACER, "StandAlone HM Initialize new vhm L7 application service failure, the vhm is: "+
					CacheMgmt.getInstance().getCacheDomainById(domainID).getDomainName());
		}
	}

	private String prepareDomainList() throws Exception {
		String where = "domainName != :s1";
		Object[] values = new Object[1];
		values[0] = HmDomain.GLOBAL_DOMAIN;
		filterParams = new FilterParams(where, values);

		setTableColumns();

		return prepareBoList();
	}

	@Override
	public List<? extends HmBo> findBos() throws Exception {
		AccessControl.checkUserAccess(getUserContext(), getSelectedL2FeatureKey(),
				CrudOperation.READ);
		// Customized to not include an owner filter.
		return paging.executeQuery(sortParams, filterParams);
	}

	/**
	 * get remaining ap number for max ap number field<br>
	 * this function must invoked when redirect to 'INPUT'
	 */
	private void getApNumRange4Modify() {
		// for exception log
		if (getDataSource() == null) {
			getApNumRange4Create();
			return;
		}

		if (getDataSource().isHomeDomain()) {
			minAPNum = getDataSource().getMaxApNum();
			remainingAPNum = getDataSource().getMaxApNum();
		} else {
			int currentAPCount = getAPCountOfThisVHM();
			minAPNum = currentAPCount > 0 ? currentAPCount : 1;
			remainingAPNum = BoMgmt.getDomainMgmt().getRemainingMaxAPNum() + getDataSource().getMaxApNum();
		}
	}

	private void prepareEditPage() {
		getApNumRange4Modify();
		disableVHM = getDataSource() != null
				&& getDataSource().getRunStatus() == HmDomain.DOMAIN_DISABLE_STATUS;
		
		if (NmsUtil.isHostedHMApplication()) {
			vhmID = prepareVHMID();
		}
	}
	
	private String prepareVHMID() {
		if (getDataSource() == null || getDataSource().getVhmID() == null || getDataSource().getVhmID().isEmpty()) {
			return "";
		}
		
		return getDataSource().getVhmID().substring("VHM-".length());
	}

	private int getAPCountOfThisVHM() {
		return (int) QueryUtil.findRowCount(HiveAp.class, new FilterParams(
				"manageStatus=:s1 AND owner.id=:s2 AND simulated=:s3", new Object[] { HiveAp.STATUS_MANAGED, id, false}));
	}

	/**
	 * clone operation special
	 */
	private void getApNumRange4Create() {
		minAPNum = 1;
		remainingAPNum = BoMgmt.getDomainMgmt().getRemainingMaxAPNum();
	}

	private void prepareCreatePage() {
		getApNumRange4Create();
		disableVHM = false;
		if (!HmBeLicenseUtil.GM_LITE_LICENSE_VALID) {
			getDataSource().setSupportGM(false);
		}
	}

	public String getDisplayName() {
		return getDataSource().getDomainName().replace("\\", "\\\\").replace("'", "\\'");
	}

	/**
	 * check whether home domain selected when remove operation
	 *
	 * @return -
	 * @throws Exception -
	 */
	private boolean isRemoveHomeDomain() throws Exception {
		Set<Long> ids = getAllSelectedIds();
		boolean bln = false;
		if (ids != null && !ids.isEmpty()) {
			for (Iterator<Long> iter = ids.iterator(); iter.hasNext();) {
				Long domainId = iter.next();

				HmDomain domain = QueryUtil.findBoById(HmDomain.class, domainId);
				if (domain != null)
					if (domain.getDomainName().trim().equals(HmDomain.HOME_DOMAIN)) {
						// ids.remove(domainId);
						iter.remove();
						setAllSelectedIds(ids);
						bln = true;
					}
			}
		}
		return bln;
	}

	/**
	 * check whether login users belong to remove domain
	 *
	 * @return failed domain name
	 */
	private String getUserLoginDomain() {
		Set<Long> ids = getAllSelectedIds();

		for (HttpSession activeUser : CurrentUserCache.getInstance().getActiveSessions()) {
			HmUser sessionUser;
			try {
				sessionUser = (HmUser) activeUser.getAttribute(USER_CONTEXT);
			} catch (Exception e){
				log.error(e);
				continue;
			}
			if (ids.contains(sessionUser.getDomain().getId())) {
				return sessionUser.getDomain().getDomainName();
			}
		}

		return "";
	}

	/**
	 * we should not allow radius login auth when we new the first VHM
	 * 
	 * @return true: give prompt&return input screen<br>
	 *         false: let's go ahead
	 */
	public boolean isRadiuLoginAuth() {
		// long domainCount = QueryUtil.findRowCount(HmDomain.class, null);
/*		int domainCount = CacheMgmt.getInstance().getCacheDomainCount();
		if (domainCount > 1) {
			// not the first create
			return false;
		}*/

		List<HmLoginAuthentication> list = QueryUtil.executeQuery(HmLoginAuthentication.class, null, null);
		if (list.isEmpty()) {
			return false;
		}

		HmLoginAuthentication loginAuthentication = list.get(0);
		return loginAuthentication.getHmAdminAuth() != EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL;
	}

	public int getDomainNameLength() {
		return getAttributeLength("domainName");
	}

	public boolean getDisableName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	public int getIpAddressLength() {
		return 15;
	}

	public int getVhmMaxAPNumLength() {
		int maxAPNumLength = 0;
		char[] ss = String.valueOf(this.remainingAPNum).toCharArray();
		for (int i = ss.length; i > 0; i--) {
			maxAPNumLength++;
		}
		return maxAPNumLength;
	}

	public int getRemainingAPNum() {
		return remainingAPNum;
	}

	public void setRemainingAPNum(int remainingAPNum) {
		this.remainingAPNum = remainingAPNum;
	}

	public boolean getDisabledVHMIP() {
		return NmsUtil.isHostedHMApplication();
	}

	public String getUpdateDisabled() {
		if (writePermission) {
			return getDataSource().getDomainName().equals(HmDomain.HOME_DOMAIN) ? "disabled" : "";
		} else {
			return "disabled";
		}
	}

	public boolean isTheFirstDomainCreate() {
		int domainCount = CacheMgmt.getInstance().getCacheDomainCount();
		return domainCount == 1;
	}

	public int getMinAPNum() {
		return minAPNum;
	}

	public void setMinAPNum(int minAPNum) {
		this.minAPNum = minAPNum;
	}

	/**
	 * HmDomain have no owner field, so we need override parent function here
	 * 
	 * @see com.ah.ui.actions.BaseAction#getWriteDisabled()
	 */
	@Override
	public String getWriteDisabled() {
		if (writePermission) {
			// if (getShowDomain()) {
			// List<?> boIds = QueryUtil.executeQuery(
			// "select owner.domainName from "
			// + boClass.getSimpleName(), null,
			// new FilterParams("id", id));
			// if (!boIds.isEmpty()
			// && !HmDomain.HOME_DOMAIN
			// .equals(boIds.get(0).toString())) {
			// return "disabled";
			// }
			// }
			return "";
		} else {
			return "disabled";
		}
	}

	public boolean isDisableVHM() {
		return disableVHM;
	}

	public void setDisableVHM(boolean disableVHM) {
		this.disableVHM = disableVHM;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public String getUserEmailAddr() {
		return userEmailAddr;
	}

	public void setUserEmailAddr(String userEmailAddr) {
		this.userEmailAddr = userEmailAddr;
	}

	public boolean getGMDisplay() {
		return HmBeLicenseUtil.GM_LITE_LICENSE_VALID;
	}

	public String getVhmID() {
		return vhmID;
	}

	public void setVhmID(String vhmID) {
		this.vhmID = vhmID;
	}
	
	public String getHide4VHMID() {
		if (NmsUtil.isHostedHMApplication()) {
			return "";
		}

		return "none";
	}
	
	public String getHide4OemSystem(){
		boolean showExpress;
		List<HmExpressModeEnable> settings = QueryUtil.executeQuery(HmExpressModeEnable.class,
				null, null);
		if (settings.isEmpty()) {
			showExpress = NmsUtil.getOEMCustomer().getExpressModeEnable();
		} else {
			showExpress = settings.get(0).isExpressModeEnable();
		}

		if (isOEMSystem() && !showExpress) {
			return "none";
		}
		return "";
	}
	
	private int[] queryGroupAttributeFromUserGroup() {
		int[] ids = new int[6];
		
		List<HmUserGroup> hmUserGroups = QueryUtil.executeQuery(HmUserGroup.class, null, new FilterParams(
				"defaultflag = :s1 and owner = :s2", new Object[]{true, getDataSource()}));
		for (HmUserGroup o : hmUserGroups) {
			if (HmUserGroup.MONITOR.equals(o.getGroupName())) {
				ids[0] = o.getGroupAttribute();
			} else if (HmUserGroup.CONFIG.equals(o.getGroupName())) {
				ids[1] = o.getGroupAttribute();
			} else if (HmUserGroup.PLANNING.equals(o.getGroupName())) {
				ids[2] = o.getGroupAttribute();
			} else if (HmUserGroup.GM_ADMIN.equals(o.getGroupName())) {
				ids[3] = o.getGroupAttribute();
			} else if (HmUserGroup.GM_OPERATOR.equals(o.getGroupName())) {
				ids[4] = o.getGroupAttribute();
			} else if (HmUserGroup.TEACHER.equals(o.getGroupName())) {
				ids[5] = o.getGroupAttribute();
			}
		}
		
		return ids;
	}
	
	private void getGroupAttributeForDefaultGroup(boolean isNew) {
		int[] newIds;
		if (isNew) {
			newIds = BoMgmt.getDomainMgmt().generateGroupAttribute(6);
		} else {
			newIds = queryGroupAttributeFromUserGroup();
		}
		getDataSource().setMonitoringId(newIds[0]);
		getDataSource().setMonitoringConfigId(newIds[1]);
		getDataSource().setRfPlanningId(newIds[2]);
		getDataSource().setUserMngAdminId(newIds[3]);
		getDataSource().setUserMngOperatorId(newIds[4]);
		getDataSource().setTeacherId(newIds[5]);
	}
	
	private boolean checkGroupAttributeDuplicate() {
		boolean isDup = false;
		String[] names = HmUserGroup.defaultVhmGroupNames;
		Set<String> dupGroups = new HashSet<String>();
		List<Integer> attributes = new ArrayList<Integer>();
		attributes.add(getDataSource().getMonitoringId());
		attributes.add(getDataSource().getMonitoringConfigId());
		attributes.add(getDataSource().getRfPlanningId());
		attributes.add(getDataSource().getUserMngAdminId());
		attributes.add(getDataSource().getUserMngOperatorId());
		attributes.add(getDataSource().getTeacherId());
		int i = 0;
		for (Integer integer : attributes) {
			if (BoMgmt.getDomainMgmt().checkGroupAttributeExist(integer, getDataSource(), null)) {
				dupGroups.add(names[i]);
				isDup = true;
			}
			i++;
		}
		
		if (isDup) {
			StringBuilder strBuf = new StringBuilder();
			strBuf.append("[");
			for (Iterator<?> iterator = dupGroups.iterator(); iterator.hasNext();) {
				strBuf.append((String) iterator.next());
				if (iterator.hasNext()) {
					strBuf.append(",");
				}
			}
			strBuf.append("]");
			addActionError(getText("error.config.vhmManagement.groupIdExisted", new String[] {strBuf.toString()}));
		}
		return isDup;
	}
	
	public int getMaxSimuApNumLength() {
		return MgrUtil.getUserMessage("admin.vhmMgr.maxSimulatedAPNum.maxValue").length();
	}
	
	public int getMaxSimuClientNumLength() {
		return MgrUtil.getUserMessage("admin.vhmMgr.maxSimulatedClientNum.maxValue").length();
	}

	public boolean isResetDeviceFlag() {
		return resetDeviceFlag;
	}

	public void setResetDeviceFlag(boolean resetDeviceFlag) {
		this.resetDeviceFlag = resetDeviceFlag;
	}

}