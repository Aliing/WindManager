/**
 *@filename		ActiveDirectoryOrLdapAction.java
 *@version
 *@author		Fiona
 *@createtime	2008-1-15 PM 02:12:58
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

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeOperateException;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.useraccess.ActiveDirectoryDomain;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceDnsInfo;
import com.ah.ui.actions.BaseAction;
import com.ah.util.ActiveDirectoryTool;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class ActiveDirectoryOrLdapAction extends BaseAction implements QueryBo 
{

	private static final long serialVersionUID = 1L;
	
	private String radioType;
	
	private String domainName;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			//prepare some fields for jsonMode
			if (isJsonMode() &&
					("continue".equals(operation)
						|| "continue4".equals(operation)
						|| "continue5".equals(operation)
						|| "continue6".equals(operation))) {
				restoreJsonContext();
			}
			
			domainName = CacheMgmt.getInstance().getCacheDomainById(domainId).getDomainName();
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.activeDirectory"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new ActiveDirectoryOrOpenLdap());
				if ("ldap".equals(radioType)) {
					getDataSource().setTypeFlag(ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP);
				} else if ("directory".equals(radioType)) {
					getDataSource().setTypeFlag(ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY);
				} else if ("openDir".equals(radioType)) {
					getDataSource().setTypeFlag(ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY);
				}
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(INPUT, "activeDirectoryJsonDlg");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				updateItems();
				if (checkNameExists("name", getDataSource().getName()) || !checkCAFiles() || !checkAdDomain()) {
					return getReturnPathWithJsonMode(INPUT, "activeDirectoryJsonDlg");
				}
				if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == getDataSource().getTypeFlag() 
						&& !checkApHasNewWlanPolicy(getDataSource().getApMac(), true)) {
					return getReturnPathWithJsonMode(INPUT, "activeDirectoryJsonDlg");
				}
				createAdOrLdapServer();
				copyOdItemsToDbFields();
				setDomainInfoForAd();
				if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == getDataSource().getTypeFlag()) {
					saveApStaticIp(getDataSource().getApMac(),
							getDataSource().getStaticHiveAPIpAddress(),
							getDataSource().getStaticHiveAPNetmask(),
							getDataSource().getStaticHiveAPGateway());
					saveApDns(getDataSource().getApMac(), getDataSource().getDnsServer());
				}
				if ("create".equals(operation)) {
					return createBo();
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("edit".equals(operation)) {
				String strForward = editBo(this);
				initEditPageItems();
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(strForward, "activeDirectoryJsonDlg");
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					getRadioType();
					updateItems();
					if (!checkCAFiles() || !checkAdDomain()) {
						return getReturnPathWithJsonMode(INPUT, "activeDirectoryJsonDlg");
					}
					if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == getDataSource().getTypeFlag() 
							&& !checkApHasNewWlanPolicy(getDataSource().getApMac(), true)) {
						return getReturnPathWithJsonMode(INPUT, "activeDirectoryJsonDlg");
					}
					createAdOrLdapServer();
					copyOdItemsToDbFields();
					setDomainInfoForAd();
					if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == getDataSource().getTypeFlag()) {
						saveApStaticIp(getDataSource().getApMac(),
								getDataSource().getStaticHiveAPIpAddress(),
								getDataSource().getStaticHiveAPNetmask(),
								getDataSource().getStaticHiveAPGateway());
						saveApDns(getDataSource().getApMac(), getDataSource().getDnsServer());
					}
				}
				if ("update".equals(operation)) {
					return updateBo();
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				ActiveDirectoryOrOpenLdap profile = (ActiveDirectoryOrOpenLdap) findBoById(boClass,
						cloneId, this);
				ActiveDirectoryOrOpenLdap destPro = profile.clone();
				destPro.setId(null);
				destPro.setName("");
				destPro.setOwner(null);
				destPro.setVersion(null);
				
				setCloneDomainValue(profile, destPro);
				
				setSessionDataSource(destPro);
				// get HiveAP's IP and DNS
				getApIpAndDns();
				addLstTitle(getText("config.title.activeDirectory"));
				if (getDataSource().getNonDefDomains().isEmpty()) {
					hideCreateItem = "";
					hideNewButton = "none";
				}
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("newIpAddress".equals(operation) || "editIpAddress".equals(operation)) {
				IpAddress newIP = null;
				if (null != ipAddressId && ipAddressId > -1) {
					newIP = findBoById(IpAddress.class, ipAddressId);
				} else {
					getDataSource().setIpInputValue(inputIpValue);
				}
				if (null != radioType) {
					if (radioType.equals("directory")) {
						getDataSource().setTypeFlag(ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY);
						getDataSource().setAuthTlsEnable(enableAdTls);
					} else {
						getDataSource().setTypeFlag(ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP);
						getDataSource().setAuthTlsEnable(enableLdapTls);
					}
				} 
				if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == getDataSource().getTypeFlag()) {
					getDataSource().setAdServer(newIP);
				} else {
					getDataSource().setLdapServer(newIP);
				}
				addLstForward("activeDirectoryOrLdap");
				updateAdDomains();
				clearErrorsAndMessages();
				return operation;
			} else if ("continue".equals(operation)) {
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "activeDirectoryJsonDlg");
			} else if ("newFile4".equals(operation)
				|| "newFile5".equals(operation)
				|| "newFile6".equals(operation)) {
				updateItems();
				clearErrorsAndMessages();
				addLstForward("activeDirectoryOrLdap" + operation.substring(7));
				return "newFile";
			} else if ("continue4".equals(operation)) {
				if (!"".equals(fileName))
					getDataSource().setCaCertFileO(fileName);
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "activeDirectoryJsonDlg"); 
			} else if ("continue5".equals(operation)) {
				if (!"".equals(fileName))
					getDataSource().setKeyFileO(fileName);
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "activeDirectoryJsonDlg");
			} else if ("continue6".equals(operation)) {
				if (!"".equals(fileName))
					getDataSource().setClientFile(fileName);
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "activeDirectoryJsonDlg");
			} else if ("addAdDomain".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateItems();
					if (!addSelectedDomain()) {
						addActionError(MgrUtil
								.getUserMessage("error.addObjectExists"));
					}
					return getReturnPathWithJsonMode(INPUT, "activeDirectoryJsonDlg");
				}
			} else if ("removeAdDomain".equals(operation)
					|| "removeAdDomainNone".equals(operation)) {
				hideCreateItem = "removeAdDomainNone".equals(operation) ? ""
						: "none";
				hideNewButton = "removeAdDomainNone".equals(operation) ? "none"
						: "";
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateItems();
					removeSelectedDomain();
					return getReturnPathWithJsonMode(INPUT, "activeDirectoryJsonDlg");
				}
			} else if ("retrieveAdInfo".equals(operation)) {
				jsonObject = ActiveDirectoryTool.retrieveOperation(
					jsonObject, apServer, fullName, getDataSource().isDnsApplied());
				return "json";
			} else if ("testJoin".equals(operation)) {
				// add for LDAP SASL wrapping
				String ldapSaslWrappingString = ActiveDirectoryOrOpenLdap.getLdapSaslWrappingString(ldapSaslWrapping);
				
				String[] args = new String[] { userNameA, passwordA, domName,
						fullName, getServerAddress(), baseDn, computerOu,  ldapSaslWrappingString};
				jsonObject = ActiveDirectoryTool.testAAAOperation(
						jsonObject, apServer,
						ActiveDirectoryTool.TEST_TYPE_JOIN, args);
				return "json";
			} else if ("testAuth".equals(operation)) {
				String[] args = new String[] { bindDnName, bindDnpass, domName,
							fullName, getServerAddress(), baseDn };
				jsonObject = ActiveDirectoryTool.testAAAOperation(
						jsonObject, apServer,
						ActiveDirectoryTool.TEST_TYPE_AUTH, args);
				return "json";
			} else if ("getApDomain".equals(operation)) {
				jsonObject = ActiveDirectoryTool.queryApOperation(
						jsonObject, apServer);
				return "json";
			} else if("onChangeHiveAP".equals(operation)){
				if (null == jsonObject)
					jsonObject = new JSONObject();

				jsonObject = ActiveDirectoryTool.queryApOperation(jsonObject, apServer);
				Object[] apInfo = ActiveDirectoryTool.getApIpDns(apServer);
				jsonObject.put("dhcp", apInfo[0]);
				jsonObject.put("ipAddress", ActiveDirectoryTool.objToString(apInfo[1]));
				jsonObject.put("netmask", ActiveDirectoryTool.objToString(apInfo[2]));
				jsonObject.put("gateway", ActiveDirectoryTool.objToString(apInfo[3]));
				jsonObject.put("dnsServer", ActiveDirectoryTool.objToString(apInfo[4]));
				setApInfoToBo(apInfo);
				
			    return "json";
			} else if("pushConfigToAp".equals(operation)){
				if (checkApHasNewWlanPolicy(apServer, false)) {
					String[] args = new String[]{apServer, apIpAddress, apIpNetmask, apIpGateway, apDnsServer};
					
					//upload configuration
					jsonObject = ActiveDirectoryTool.pushConfigToAp(jsonObject, domainId, args);
					if (ActiveDirectoryTool.RESULT_CODE_SUCCESS.equals(jsonObject.get("resCode").toString())) {
						getDataSource().setDnsApplied(true);
					} else {
						getDataSource().setDnsApplied(false);
					}
				} else {
					getDataSource().setDnsApplied(false);
					jsonObject = new JSONObject();
					jsonObject.put("resCode", ActiveDirectoryTool.RESULT_CODE_FAILURE);
					jsonObject.put("msg", MgrUtil.getUserMessage("error.config.hiveAp.has.no.new.wlan.apply"));
				}
			    return "json";	
			} else {
				setUpdateContext(true);
				baseOperation();
				return prepareADBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	private String prepareADBoList() throws Exception{
		String str = prepareBoList();
		loadLazyData();
		return str;
	}
	
	private void loadLazyData(){
		if (page.isEmpty())
			return;

		Map<Long, ActiveDirectoryOrOpenLdap> vlanMap = new HashMap<Long, ActiveDirectoryOrOpenLdap>();
		StringBuilder buf = new StringBuilder();
		for (Object element : page) {
			ActiveDirectoryOrOpenLdap vlan = (ActiveDirectoryOrOpenLdap) element;
			vlanMap.put(vlan.getId(), vlan);
			buf.append(vlan.getId());
			buf.append(",");

			vlan.setAdDomains(new ArrayList<ActiveDirectoryDomain>());
		}
		buf.deleteCharAt(buf.length() - 1);

		String sql = "select a.id,b.domain,b.server,b.fullName,b.bindDnName,b.bindDnPass,b.defaultFlag"
				+ " from active_directory_or_ldap a "
				+ " inner join radius_ad_domain b on b.ad_domain_id = a.id "
				+ " where a.id in(" + buf.toString() + ")";

		List<?> templates = QueryUtil.executeNativeQuery(sql);

		for (Object obj : templates) {
			Object[] template = (Object[]) obj;
			Long id = Long.valueOf(template[0].toString());

			ActiveDirectoryOrOpenLdap templateElement = vlanMap.get(id);
			if (null != templateElement) {
				if (StringUtils.isNotBlank(template[1].toString())) {
					ActiveDirectoryDomain tempClass = new ActiveDirectoryDomain();
					tempClass.setDomain(template[1].toString());
					tempClass.setServer(template[2].toString());
					tempClass.setFullName(template[3].toString());
					tempClass.setBindDnName(template[4].toString());
					tempClass.setBindDnPass(template[5].toString());
					tempClass.setDefaultFlag((Boolean) template[6]);
					templateElement.getAdDomains().add(tempClass);
				}
			}
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_RADIUS_ACTIVE_DIRECTORY);
		setDataSource(ActiveDirectoryOrOpenLdap.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_AUTH_AD_LDAP;
	}
	
	private void setCloneDomainValue(ActiveDirectoryOrOpenLdap source, ActiveDirectoryOrOpenLdap dest) {
		if (ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP != source.getTypeFlag()) {
			List<ActiveDirectoryDomain> adDomains = new ArrayList<ActiveDirectoryDomain>();
			adDomains.addAll(source.getAdDomains());
			dest.setAdDomains(adDomains);
			
			for (ActiveDirectoryDomain oneDomain : adDomains) {
				if (oneDomain.isDefaultFlag()) {
					dest.setDefDomain(oneDomain);
					if (ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY == source.getTypeFlag()) {
						dest.setDefOdDomain(oneDomain);
					}
				} else {
					dest.getNonDefDomains().add(oneDomain);
				}
			}
			
			if (ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY == source.getTypeFlag()) {
				dest.setUserNameOd(source.getUserNameA());
				dest.setPasswordOd(source.getPasswordA());
				dest.setFilterAttrOd(source.getFilterAttr());
				dest.setStripFilterOd(source.isStripFilter());
			}
		}
	}

	@Override
	public ActiveDirectoryOrOpenLdap getDataSource() {
		return (ActiveDirectoryOrOpenLdap) dataSource;
	}

	public int getRadiusNameLength() {
		return getAttributeLength("name");
	}

	public int getCommentLength() {
		return getAttributeLength("description");
	}

	public String getChangedName() {
		return getDataSource().getName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}
	
	public int getGridCount() {
		return getDataSource().getNonDefDomains().isEmpty() ? 3 : 0;
	}
	
	protected void updateItems() throws Exception {
		if (null != radioType) {
			IpAddress newIP = null;
			if (!radioType.equals("openDirect")) {
				if (null != ipAddressId && ipAddressId > -1) {
					newIP = findBoById(IpAddress.class, ipAddressId);
				} else {
					getDataSource().setIpInputValue(inputIpValue);
				}
			}
			if (radioType.equals("ldap")) {
				getDataSource().setTypeFlag(ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP);
				getDataSource().setAdDomains(null);
				getDataSource().setLdapServer(newIP);
				getDataSource().setAdServer(null);
				getDataSource().setUserNameA("");
				getDataSource().setPasswordA("");
				getDataSource().setComputerOU("");
				// add for ad configuration improvement
				getDataSource().setUserNameOd("");
				getDataSource().setPasswordOd("");
				getDataSource().setAuthTlsEnable(enableLdapTls);
				if (!getDataSource().isAuthTlsEnable()) {
					getDataSource().setCaCertFileO(BeAdminCentOSTools.AH_NMS_DEFAULT_CA);
					getDataSource().setKeyFileO(BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_KEY);
					getDataSource().setClientFile(BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_CERT);
					getDataSource().setVerifyServer(ActiveDirectoryOrOpenLdap.RADIUS_VERIFY_SERVER_TRY);
					getDataSource().setKeyPasswordO("");
				}
			} else {
				if (radioType.equals("directory")) {
					getDataSource().setTypeFlag(ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY);
					updateAdDomains();
					getDataSource().setAdServer(newIP);
					// add for ad configuration improvement
					getDataSource().setAuthTlsEnable(enableAdTls);
					if (!getDataSource().isSaveCredentials()) {
						getDataSource().setUserNameA("");
						getDataSource().setPasswordA("");
					}
				} else {
					// open directory
					getDataSource().setComputerOU("");
					getDataSource().setTypeFlag(ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY);
					getDataSource().setAdServer(null);
					// add for ad configuration improvement
					getDataSource().setBasedN("");
					getDataSource().setAuthTlsEnable(enableOdTls);
				}
				getDataSource().setLdapServer(null);
				getDataSource().setBindDnName("");
				getDataSource().setLdapProtocol(ActiveDirectoryOrOpenLdap.LDAP_SERVER_PROTOCOL_LDAP);
				getDataSource().setDestinationPort(389);
				getDataSource().setFilterAttr("cn");
				getDataSource().setKeyPasswordO("");
				getDataSource().setPasswordO("");
				getDataSource().setCaCertFileO(BeAdminCentOSTools.AH_NMS_DEFAULT_CA);
				getDataSource().setKeyFileO(BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_KEY);
				getDataSource().setClientFile(BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_CERT);
				getDataSource().setVerifyServer(ActiveDirectoryOrOpenLdap.RADIUS_VERIFY_SERVER_TRY);
			}
		}
	}
	
	private void createAdOrLdapServer() {
		if (ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY != getDataSource().getTypeFlag()) {
			if (null == ipAddressId || ipAddressId == -1) {
				short ipType = ImportCsvFileAction.getIpAddressWrongFlag(inputIpValue) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
				boolean ifAd = getDataSource().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY;
				IpAddress newIP = CreateObjectAuto.createNewIP(inputIpValue, ipType, getDomain(), "For " +
						(ifAd ? "Active Directory " : "Open LDAP ") + getDataSource().getName());
				if (ifAd) {
					getDataSource().setAdServer(newIP);
				} else {
					getDataSource().setLdapServer(newIP);
				}
			}
		}
	}
	
	private void setDomainInfoForAd() {
		if (getDataSource().getTypeFlag() != ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP) {
			if (null == getDataSource().getAdDomains()) {
				getDataSource().setAdDomains(new ArrayList<ActiveDirectoryDomain>());
			}
			getDataSource().getAdDomains().clear();
			// ad configuration improvement
			if (getDataSource().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY) {
				getDataSource().getDefDomain().setDefaultFlag(true);
				getDataSource().getAdDomains().add(getDataSource().getDefDomain());
				getDataSource().getAdDomains().addAll(getDataSource().getNonDefDomains());
			} else {
				// add for OD
				getDataSource().getDefOdDomain().setDefaultFlag(true);
				getDataSource().getAdDomains().add(getDataSource().getDefOdDomain());
			}
		}
	}
	
	private String setContinueValue() throws Exception {
		if (getUpdateContext()) {
			removeLstTitle();
			removeLstForward();
			setUpdateContext(false);
		}
		if (dataSource == null) {
			return prepareBoList();
		} else {
			setId(dataSource.getId());
			return INPUT;
		}
	}
	
	private boolean checkCAFiles() {
		// check if the certificate file valid
		if (ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP == getDataSource().getTypeFlag()
			&& getDataSource().isAuthTlsEnable()) {
			String clientFile = getDataSource().getClientFile();
			if (!"".equals(clientFile)) {
				try {		
					String keyPass = "".equals(getDataSource().getKeyPasswordO()) ? null : getDataSource().getKeyPasswordO();
					if (!HmBeAdminUtil.verifyCertificate(getDataSource().getCaCertFileO(), clientFile, 
						getDataSource().getKeyFileO(), keyPass, domainName,true)) {
						addActionError(MgrUtil.getUserMessage("error.radius.checkCertificateFile"));
						return false;
					}
				} catch (BeOperateException boe) {
					addActionError(boe.getMessage());
					return false;
				}
			}		
		}
		return true;
	}
	
	private boolean checkAdDomain() {
		if (getDataSource().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY) {
			for (ActiveDirectoryDomain single : getDataSource().getNonDefDomains()) {
				if (getDataSource().getDefDomain().getDomain().equals(single.getDomain())) {
					addActionError(MgrUtil.getUserMessage("error.radius.ad.default.domain"));
					return false;
				}
			}
		}
		return true;
	}
	
	public int getPassLength() {
		return getAttributeLength("keyPasswordO");
	}
	
	public String getHideAuth() {
		return isEnableLdapTls() ? "" : "none";
	}
	
	public String getHideActive() {
		return getDataSource().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY ? "" : "none";
	}
	
	public String getHideOpenDir() {
		return getDataSource().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY ? "" : "none";
	}
	
	public String getHideOpen() {
		return getDataSource().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP ? "" : "none";
	}

	public EnumItem[] getVerify() {
		return ActiveDirectoryOrOpenLdap.RADIUS_VERIFY_SERVER;
	}
	
	public EnumItem[] getProtocol() {
		return ActiveDirectoryOrOpenLdap.LDAP_SERVER_PROTOCOL;
	}
	
	public EnumItem[] getLdapSaslWrappings() {
		return ActiveDirectoryOrOpenLdap.CLIENT_LDAP_SASL_WRAPPING;
	}
	
	public List<String> getAvailableCaFile() {
		List<String> listFile = HmBeAdminUtil.getCAFileList(domainName);
		if (null == listFile || listFile.size() == 0) {
			listFile = new ArrayList<String>();
			listFile.add("");
		}
		return listFile;
	}

	public List<String> getAvailableKeyFile() {
		List<String> listFile = HmBeAdminUtil.getCAFileList(domainName);
		if (null == listFile)
			listFile = new ArrayList<String>();
		listFile.add("");
		return listFile;
	}

	public String getConfPasswordO() {
		return getDataSource().getKeyPasswordO();
	}
	
	public String getConfirmPasswordO() {
		return getDataSource().getPasswordO();
	}
	
	public String getConfirmPasswordA() {
		return getDataSource().getPasswordA();
	}
	
	private String fileName = "";

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRadioType()
	{
		radioType = getDataSource().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY
					? "directory" : (getDataSource().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY ? "openDirect" : "ldap");
		return radioType;
	}

	public void setRadioType(String radioType)
	{
		this.radioType = radioType;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		ActiveDirectoryOrOpenLdap source = QueryUtil.findBoById(ActiveDirectoryOrOpenLdap.class,
				paintbrushSource);
		if (null == source) {
			return null;
		}
		List<ActiveDirectoryOrOpenLdap> list = QueryUtil.executeQuery(ActiveDirectoryOrOpenLdap.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (ActiveDirectoryOrOpenLdap profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			ActiveDirectoryOrOpenLdap up = source.clone();
			if (null == up) {
				continue;
			}
			
			// category must be same
			if (profile.getTypeFlag() != source.getTypeFlag()) {
				addActionError(MgrUtil
					.getUserMessage("error.use.paintbrush.objectIsDifferentType", "category"));
				return null;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setName(profile.getName());
			up.setOwner(profile.getOwner());
			setCloneDomainValue(source, up);
			hmBos.add(up);
		}
		return hmBos;
	}
	
	protected boolean addSelectedDomain() throws Exception {
		ActiveDirectoryDomain oneDomain = new ActiveDirectoryDomain();
		if (null == getDataSource().getNonDefDomains()) {
			getDataSource().setNonDefDomains(new ArrayList<ActiveDirectoryDomain>());
		}
		for (ActiveDirectoryDomain single : getDataSource().getNonDefDomains()) {
			if (adFullName.equals(single.getFullName())) {
				hideCreateItem = "";
				hideNewButton = "none";
				return false;
			}
		}
		oneDomain.setFullName(adFullName);
		oneDomain.setServer(domServer);
		oneDomain.setBindDnName(bindDn);
		oneDomain.setBindDnPass(bindPass);
		getDataSource().getNonDefDomains().add(oneDomain);

		adFullName = "";
		domServer = "";
		bindDn = "";
		bindPass = "";
		return true;
	}

	protected void removeSelectedDomain() {
		if (ruleIndices != null) {
			Collection<ActiveDirectoryDomain> removeList = new Vector<ActiveDirectoryDomain>();
			for (String serviceIndex : ruleIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getNonDefDomains().size()) {
						removeList
								.add(getDataSource().getNonDefDomains().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getNonDefDomains().removeAll(removeList);
		}
	}

	protected void updateAdDomains() {
		ActiveDirectoryDomain updateOne;
		if (domServers != null) {
			for (int i = 0; i < domServers.length
					&& i < getDataSource().getNonDefDomains().size(); i++) {
				updateOne = getDataSource().getNonDefDomains().get(i);
				updateOne.setServer(domServers[i]);
				updateOne.setBindDnName(bindDns[i]);
			}
		}
	}
	
	private String adFullName;
	
	private String domServer;
	
	private String bindDn;
	
	private String bindPass;
	
	private String[] domServers;
	
	private String[] bindDns;
	
	private Collection<String> ruleIndices;

	public void setRuleIndices(Collection<String> ruleIndices)
	{
		this.ruleIndices = ruleIndices;
	}

	public String getAdFullName() {
		return adFullName;
	}

	public void setAdFullName(String adFullName) {
		this.adFullName = adFullName;
	}

	public void setBindDns(String[] bindDns)
	{
		this.bindDns = bindDns;
	}

	public void setBindDn(String bindDn)
	{
		this.bindDn = bindDn;
	}

	public void setBindPass(String bindPass)
	{
		this.bindPass = bindPass;
	}
	
	private String hideCreateItem = "none";

	public String getHideCreateItem() {
		return hideCreateItem;
	}

	private String hideNewButton = "";

	public String getDomServer()
	{
		return domServer;
	}

	public String getHideNewButton() {
		return hideNewButton;
	}

	public String getBindDn()
	{
		return bindDn;
	}

    public String getBindPass()
	{
		return bindPass;
	}
        
    private boolean enableAdTls = false;
    
    private boolean enableLdapTls = false;
    
    private boolean enableOdTls = false;

    private Long ipAddressId;
	
	private String inputIpValue;

	public String getInputIpValue() {
		if (null != ipAddressId && ipAddressId != -1) {
			for (CheckItem item : getAvailableIpAddress()) {
				if (item.getId().longValue() == ipAddressId.longValue()) {
					inputIpValue = item.getValue();
					break;
				}
			}
		} else {
			inputIpValue = getDataSource().getIpInputValue();
		}
		return inputIpValue;
	}

	public void setInputIpValue(String inputIpValue) {
		this.inputIpValue = inputIpValue;
	}
	
	public String getInputAdValue() {
		if (getDataSource().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY) {
			return getInputIpValue();
		}
		return null;
	}
	
	public String getInputLdapValue() {
		if (getDataSource().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP) {
			return getInputIpValue();
		}
		return null;
	}

	public Long getIpAddressId() {
		if (null == ipAddressId) {
			if (null != getDataSource().getAdServer()) {
				ipAddressId = getDataSource().getAdServer().getId();
			}
			if (null != getDataSource().getLdapServer()) {
				ipAddressId = getDataSource().getLdapServer().getId();
			}
		}
		return ipAddressId;
	}

	public void setIpAddressId(Long ipAddressId) {
		this.ipAddressId = ipAddressId;
	}
	
	public List<CheckItem> getAvailableIpAddress() {
		return getIpObjectsByIpAndName();
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_TYPE = 2;
	
	public static final int COLUMN_DESCRIPTION = 3;
	
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
			code = "config.radiusOnHiveAp.radiusName";
			break;
		case COLUMN_TYPE:
			code = "config.ipAddress.type.title";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.radiusOnHiveAp.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_TYPE));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	public boolean isEnableAdTls() {
		if (getDataSource().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY) {
			enableAdTls = getDataSource().isAuthTlsEnable();
		}
		return enableAdTls;
	}

	public void setEnableAdTls(boolean enableAdTls) {
		this.enableAdTls = enableAdTls;
	}

	public boolean isEnableLdapTls() {
		if (getDataSource().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP) {
			enableLdapTls = getDataSource().isAuthTlsEnable();
		}
		return enableLdapTls;
	}

	public void setEnableLdapTls(boolean enableLdapTls) {
		this.enableLdapTls = enableLdapTls;
	}
	
	public boolean isEnableOdTls() {
		if (getDataSource().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY) {
			enableOdTls = getDataSource().isAuthTlsEnable();
		}
		return enableOdTls;
	}

	public void setEnableOdTls(boolean enableOdTls) {
		this.enableOdTls = enableOdTls;
	}

	public void setDomServer(String domServer)
	{
		this.domServer = domServer;
	}

	public void setDomServers(String[] domServers)
	{
		this.domServers = domServers;
	}
	
	/**
	 *  add for ad configuration improvement
	 */
	private String apServer;
	private String domName;
	private String fullName;
	private String server;
	private String baseDn;
	private String computerOu;
	private short ldapSaslWrapping;
	
	// ADMIN account
	private String userNameA;
	private String passwordA;
	
	// bindDn account
	private String bindDnName;
	private String bindDnpass;
	
	// configure AP's IP/NETMASK/GATEWAY/DNS
	private String apIpAddress;
	private String apIpNetmask;
	private String apIpGateway;
	private String apDnsServer;

	public void setLdapSaslWrapping(short ldapSaslWrapping) {
		this.ldapSaslWrapping = ldapSaslWrapping;
	}

	public String getComputerOu() {
		return computerOu;
	}

	public void setComputerOu(String computerOu) {
		this.computerOu = computerOu;
	}

	public String getApIpAddress() {
		return apIpAddress;
	}

	public void setApIpAddress(String apIpAddress) {
		this.apIpAddress = apIpAddress;
	}

	public String getApIpNetmask() {
		return apIpNetmask;
	}

	public void setApIpNetmask(String apIpNetmask) {
		this.apIpNetmask = apIpNetmask;
	}

	public String getApIpGateway() {
		return apIpGateway;
	}

	public void setApIpGateway(String apIpGateway) {
		this.apIpGateway = apIpGateway;
	}

	public String getApDnsServer() {
		return apDnsServer;
	}

	public void setApDnsServer(String apDnsServer) {
		this.apDnsServer = apDnsServer;
	}

	public String getApListString() {
		return ActiveDirectoryTool.getApListString(domainId,
				ActiveDirectoryTool.QUERY_AP_MAC_FOR_KEY, null);
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public String getApServer() {
		return apServer;
	}

	public void setApServer(String apServer) {
		this.apServer = apServer;
	}

	public String getDomName() {
		return domName;
	}

	public void setDomName(String domName) {
		this.domName = domName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUserNameA() {
		return userNameA;
	}

	public void setUserNameA(String userNameA) {
		this.userNameA = userNameA;
	}

	public String getPasswordA() {
		return passwordA;
	}

	public void setPasswordA(String passwordA) {
		this.passwordA = passwordA;
	}

	public String getBindDnName() {
		return bindDnName;
	}

	public void setBindDnName(String bindDnName) {
		this.bindDnName = bindDnName;
	}

	public String getBindDnpass() {
		return bindDnpass;
	}

	public void setBindDnpass(String bindDnpass) {
		this.bindDnpass = bindDnpass;
	}

	public String getHideAdmin() {
		return getDataSource().getAdminIsShow() == 1 ? "" : "none";
	}
	
	public String getHideBindDn() {
		return getDataSource().getDomainUserIsShow() == 1 ? "" : "none";
	}
	
	public String getHideActiveMutipleDomain() {
		return getDataSource().getMultiDomainIsShow() == 1 ? "" : "none";
	}
	
	public String getHaveDnsAndNtp() {
		return getText("info.config.hiveAp.have.dns.ntp");
	}
	
	private void copyOdItemsToDbFields() {
		if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == getDataSource().getTypeFlag()) {
			getDataSource().setBasedN(getDataSource().getHidAdBaseDn());
		} else if (ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY == getDataSource().getTypeFlag()) {
			getDataSource().setUserNameA(getDataSource().getUserNameOd());
			getDataSource().setPasswordA(getDataSource().getPasswordOd());
			getDataSource().setFilterAttr(getDataSource().getFilterAttrOd());
			getDataSource().setStripFilter(getDataSource().isStripFilterOd());
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof ActiveDirectoryOrOpenLdap) {
			//add for ad configuration improvement 
			ActiveDirectoryOrOpenLdap ad = (ActiveDirectoryOrOpenLdap)bo;
			if (ad.getAdDomains() != null) {
				ad.getAdDomains().size();
			}
			if (ad.getAdServer() != null && ad.getAdServer().getItems() != null) {
				ad.getAdServer().getItems().size();
			}
		} else if (bo instanceof IpAddress) {
			IpAddress ip = (IpAddress)bo;
			if (ip.getItems() != null) {
				ip.getItems().size();
			}
		} else if (bo instanceof MgmtServiceDns) {
			MgmtServiceDns dns = (MgmtServiceDns)bo;
			if (dns.getDnsInfo() != null) {
				dns.getDnsInfo().size();
				if (!dns.getDnsInfo().isEmpty()) {
					loadIpItems(dns.getDnsInfo().get(0).getIpAddress());
				}
			}
		}
		return null;
	}
    
    private void loadIpItems(IpAddress ipAddress) {
    	if (null != ipAddress && null != ipAddress.getItems() )
    		ipAddress.getItems().size();
    }
	
	private void initEditPageItems() {
		if (dataSource != null) {
			// get HiveAP's IP and DNS
			getApIpAndDns();
			
			// set default domain for AD or OD
			if (ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP != getDataSource().getTypeFlag()) {
				for (ActiveDirectoryDomain adDom : getDataSource().getAdDomains()) {
					if (adDom.isDefaultFlag()) {
						// AD
						if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == getDataSource().getTypeFlag()) {
							getDataSource().setDefDomain(adDom);
						} else {
							// OD
							getDataSource().setDefOdDomain(adDom);
						}
					} else {
						getDataSource().getNonDefDomains().add(adDom);
					}
				}
				// set hidden items's value
				if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == getDataSource().getTypeFlag()) {
					if (getDataSource().getAdServer() != null
							&& getDataSource().getAdServer().getItems() != null
							&& getDataSource().getAdServer().getItems().size() > 0) {
						getDataSource().setHidAdServer(getDataSource().getAdServer().getItems().get(0).getIpAddress());
					}
					getDataSource().setHidAdBaseDn(getDataSource().getBasedN());
					
					// add for ad configuration improvement
					getDataSource().setRetrieveSuccess(1);
					getDataSource().setTestJoinSuccess(1);
					getDataSource().setTestAuthSuccess(1);
					
					// show all element in page
					getDataSource().setAdminIsShow(1);
					getDataSource().setDomainUserIsShow(1);
					getDataSource().setMultiDomainIsShow(1);
				} else {
					getDataSource().setUserNameOd(getDataSource().getUserNameA());
					getDataSource().setPasswordOd(getDataSource().getPasswordA());
					getDataSource().setFilterAttrOd(getDataSource().getFilterAttr());
					getDataSource().setStripFilterOd(getDataSource().isStripFilter());
				}
			}
			addLstTitle(getText("config.title.activeDirectory.edit")
					+ " '" + getChangedName() + "'");
		}
	}

	private String getServerAddress() throws Exception {
		if (ipAddressId == -1) {
			return inputIpValue;
		} else {
			IpAddress newIP = QueryUtil.findBoById(IpAddress.class, ipAddressId, this);
			if (newIP != null 
					&& newIP.getItems() != null
					&& newIP.getItems().size() > 0) {
				return newIP.getItems().get(0).getIpAddress();
			}
			return "";
		}
	}
	
	private void getApIpAndDns() {
		if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == getDataSource().getTypeFlag()) {
			Object[] apInfo = ActiveDirectoryTool.getApIpDns(getDataSource().getApMac());
			setApInfoToBo(apInfo);
		}
	}
	
	private void setApInfoToBo(Object[] apInfo) {
		if (apInfo[0] != null) {
			getDataSource().setDhcp(Boolean.parseBoolean(apInfo[0].toString()));
		}
		getDataSource().setStaticHiveAPIpAddress(ActiveDirectoryTool.objToString(apInfo[1]));
		getDataSource().setStaticHiveAPNetmask(ActiveDirectoryTool.objToString(apInfo[2]));
		getDataSource().setStaticHiveAPGateway(ActiveDirectoryTool.objToString(apInfo[3]));
		getDataSource().setDnsServer(ActiveDirectoryTool.objToString(apInfo[4]));
	}
	
	public String getNoStaticIporDns() {
		return StringUtils.isNotBlank(getDataSource().getApMac())
				&& (getDataSource().isDhcp()
						|| StringUtils.isBlank(getDataSource()
						.getDnsServer()))? "" : "none";
	}
	
	public String getShowIpArea() {
		return StringUtils.isNotBlank(getDataSource().getApMac()) ? "" : "none";
	}
	
	/* saveApStaticIp */
	private void saveApStaticIp(String apMac, String ipAddress,
			String netMask, String gateWay) throws Exception {
		
		//update DB for HiveAp
		int updateCounts = QueryUtil.executeNativeUpdate("update hive_ap set dhcp=:s1, cfgIpAddress=:s2, cfgNetmask=:s3, cfgGateway=:s4 where macAddress=:s5", 
				new Object[]{false, ipAddress, netMask, gateWay, apMac});
		
		if(updateCounts == 0){
			//no device be updated
			return;
		}
		
		//set HiveAp data source in session
		Object hiveApSource = MgrUtil.getSessionAttribute(HiveAp.class.getSimpleName() + "Source");
		if (hiveApSource != null && (hiveApSource instanceof HiveAp)) { // fix bug 32136
			HiveAp hiveApDataSource = (HiveAp) hiveApSource;
			if(hiveApDataSource != null && hiveApDataSource.getMacAddress().equalsIgnoreCase(apMac)){
				hiveApDataSource.setDhcp(false);
				hiveApDataSource.setCfgIpAddress(ipAddress);
				hiveApDataSource.setCfgNetmask(netMask);
				hiveApDataSource.setCfgGateway(gateWay);
			}
		}
		
		String desc="For AAA Client Setting";
		updateIPAddress(ipAddress, desc);
	}
		
	/**
	 * Update IpAddress:<br> 
	 * if it does not contain the IpAddress object in database, it will create a new IpAddress object;<br>
	 * else update the exist IpAddress Object.
	 * 
	 * @param ipAddress -
	 * @param description -
	 * @return a new IpAddress Object after <b>new</b> or <b>update</b>
	 * @throws Exception -
	 */
	private IpAddress updateIPAddress(String ipAddress, String description) throws Exception {
	    IpAddress ip = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", ipAddress,getDomain().getId(), this);
        if (ip == null) {
            //create a new object
            short ipType = ImportCsvFileAction.getIpAddressWrongFlag(ipAddress) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
            ip = CreateObjectAuto.createNewIP(ipAddress, ipType, getDomain(), 
                    description);
        }else{
            List<SingleTableItem> items=ip.getItems();
            if(items.isEmpty()){
                //insert a subObject
                SingleTableItem item = new SingleTableItem();
                item.setIpAddress(ipAddress);
                item.setNetmask(IpAddress.NETMASK_OF_SINGLE_IP);
                item.setType(SingleTableItem.TYPE_GLOBAL);
                item.setDescription(description);
                
                items.add(item);
            }else if(!items.get(0).getIpAddress().equals(ipAddress)){
                items.get(0).setIpAddress(ipAddress);
            }
            ip = QueryUtil.updateBo(ip);
        }
        return ip;
    }
	/* saveApStaticIp */
	
	/* check current AP whether has new WLAN policy(not default) or not */
	private boolean checkApHasNewWlanPolicy(String apMac, boolean isForSave) {
		HiveAp ap = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", apMac,
				new ConfigLazyQueryBo());
		if (ap != null) {
			if (ap.getConfigTemplate() != null) {
				ConfigTemplate configTemplate = QueryUtil.findBoById(
						ConfigTemplate.class, ap.getConfigTemplate().getId(),
						new ConfigLazyQueryBo());
				if (configTemplate != null && !configTemplate.isDefaultFlag()) {
					return true;
				}
			}
		}
		if(isForSave) {
			addActionError(MgrUtil.getUserMessage("error.config.hiveAp.has.no.new.wlan.save"));
		}
		return false;
	}
	/**/
	
	/* saveApDns */
	private void saveApDns(String apMac, String dnsServerIp)
			throws Exception {
		HiveAp ap = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", apMac,
				new ConfigLazyQueryBo());
		if (ap != null) {
			if (ap.getConfigTemplate() != null) {
				ConfigTemplate configTemplate = QueryUtil.findBoById(
						ConfigTemplate.class, ap.getConfigTemplate().getId(),
						new ConfigLazyQueryBo());
				if (configTemplate != null && !configTemplate.isDefaultFlag()) {
					MgmtServiceDns mgmtServiceDns = configTemplate.getMgmtServiceDns();
					if (mgmtServiceDns != null 
							&& mgmtServiceDns.getDnsInfo() != null 
							&& !mgmtServiceDns.getDnsInfo().isEmpty()) {
						MgmtServiceDnsInfo dnsInfo = mgmtServiceDns.getDnsInfo().get(0);
						if (dnsInfo.getIpAddress() != null
								&& dnsInfo.getIpAddress().getItems() != null
								&& !dnsInfo.getIpAddress().getItems().isEmpty()
								&& dnsServerIp.equals(dnsInfo.getIpAddress().getItems().get(0).getIpAddress())) {
							// same as the DNS save in DB,do not do update.
						} else {
							// update DNS object for WLAN
							updateDnsForWlan(configTemplate, dnsServerIp, ap.getHostName());
						}
					} else {
						// update DNS object for WLAN
						updateDnsForWlan(configTemplate, dnsServerIp, ap.getHostName());
					}
				}
			}
		}
	}

	private void updateDnsForWlan(ConfigTemplate configTemplate, String dnsServerIp, String apHostName) throws Exception {
		Long dnsId = getDnsObjByIpAddress(dnsServerIp, apHostName);
		configTemplate.setMgmtServiceDns(QueryUtil.findBoById(MgmtServiceDns.class, dnsId, this));
		QueryUtil.updateBo(configTemplate);
	}
	
	private Long getDnsObjByIpAddress(String dnsServerIp, String apHostName) throws Exception {
		List<MgmtServiceDns> dns = QueryUtil.executeQuery(MgmtServiceDns.class, null, null, domainId, this);
		
		// get DNS object that has same IP with input value for DNS.
		for (MgmtServiceDns mgmtServiceDns : dns) {
			if (mgmtServiceDns != null 
					&& mgmtServiceDns.getDnsInfo() != null 
					&& mgmtServiceDns.getDnsInfo().size() == 1) {
				MgmtServiceDnsInfo dnsInfo = mgmtServiceDns.getDnsInfo().get(0);
				if (dnsInfo != null 
						&& dnsInfo.getIpAddress() != null
						&& dnsInfo.getIpAddress().getItems() != null
						&& !dnsInfo.getIpAddress().getItems().isEmpty()
						&& dnsServerIp.equals(dnsInfo.getIpAddress().getItems().get(0).getIpAddress())) {
					return mgmtServiceDns.getId();
				}
			}
		}
		
		// create new DNS object
		return createDNSServer(dnsServerIp, apHostName);
	}

	private Long createDNSServer(String dnsServerIp, String apHostName) throws Exception {
		Long dnsId;
		MgmtServiceDns dnsServer = new MgmtServiceDns();
		dnsServer.setDescription("For Aerohive device:"+ apHostName);
		dnsServer.setOwner(getDomain());
		dnsServer.setMgmtName(dnsServerIp);
		
		List<MgmtServiceDnsInfo> dnsInfo = new ArrayList<MgmtServiceDnsInfo>();
		dnsInfo.add(getNewDNSServerInfo(dnsServerIp, apHostName));
		dnsServer.setDnsInfo(dnsInfo);
		
		dnsId = QueryUtil.createBo(dnsServer);
		return dnsId;
	}

	private MgmtServiceDnsInfo getNewDNSServerInfo(String dnsServerIp, String apHostName) {
		// create the ip address object
		IpAddress ipObj = CreateObjectAuto.createNewIP(dnsServerIp, 
				IpAddress.TYPE_IP_ADDRESS,
				getDomain(), "For RADIUS Device DNS Assignment");
		// create the dns server item
		MgmtServiceDnsInfo singleInfo = new MgmtServiceDnsInfo();
		singleInfo.setIpAddress(ipObj);
		singleInfo.setDnsDescription("For Aerohvie device: "+ apHostName);
		return singleInfo;
	}
	/* saveApDns */

	public boolean isParentIframeOpenFlag4Child() {
		return isContentShownInDlg();
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