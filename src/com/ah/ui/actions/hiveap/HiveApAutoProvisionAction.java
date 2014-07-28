package com.ah.ui.actions.hiveap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.PresenceUtil;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.parameter.BeParaModule;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.AutoProvisionDeviceInterface;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.DeviceIPSubNetwork;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.hiveap.HiveApImageInfo;
import com.ah.bo.hiveap.HiveApSerialNumber;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.IpAddress;
import com.ah.bo.wlan.RadioProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.admin.DeviceTagUtil;
import com.ah.ui.actions.config.ImportCsvFileAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.CountryCode;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.bo.device.DeviceProperties;
import com.ah.util.devices.impl.Device;

public class HiveApAutoProvisionAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			HiveApAutoProvisionAction.class.getSimpleName());

	private String customTag1=null;

	private String customTag2=null;

	private String customTag3=null;

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_HIVEAP_AUTO_PROVISIONING);
		setDataSource(HiveApAutoProvision.class);
		keyColumnId = COLUMN_PROFILE_NAME;
		this.tableId = HmTableColumn.TABLE_AUTO_PROVISION;
	}

	@Override
	public HiveApAutoProvision getDataSource() {
		return (HiveApAutoProvision) dataSource;
	}

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		Map<String, String> cusMap = DeviceTagUtil.getInstance().getClassifierCustomTag(this.getDomainId());
		customTag1 = cusMap.get(DeviceTagUtil.CUSTOM_TAG1);
		customTag2 = cusMap.get(DeviceTagUtil.CUSTOM_TAG2);
		customTag3 = cusMap.get(DeviceTagUtil.CUSTOM_TAG3);
		try {
			if (!"continue".equals(operation) && !"continue1".equals(operation)) {
				saveBrEthx2DataSource();
			}

			if ("new".equals(operation)) {
				log.info("execute", "operation:" + operation);
				if (!setTitleAndCheckAccess(getText("hiveAp.title.autoProvisioning"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new HiveApAutoProvision());
				prepareDependentObjects();
				prepareBr100InterfaceSetting();
				blnNoChangeDeviceType = false;
				return INPUT;
			} else if ("create".equals(operation)) {
				log.info("execute", "operation:" + operation);
				if (null == getDataSource()
						|| checkProvisionExisted()
						|| checkNameExists("name", getDataSource()
								.getName())
						|| !setSelectedObjects()
						|| !checkRouterMinVersion()) {
					prepareDependentObjects();
					return INPUT;
				}
				// default select wlan policy
				if (isEasyMode()) {
					ConfigTemplate defaultTemplate = HmBeParaUtil
							.getEasyModeDefaultTemplate(domainId);
					getDataSource()
							.setConfigTemplateId(defaultTemplate.getId());
				}
				if (!verify11nInterfaces(getDataSource())) {
					prepareDependentObjects();
					return INPUT;
				}
				if (HiveAp.isWifi1Available(getDataSource().getModelType()) && getDataSource().isUploadImage()) {
					if (!verifyRadioFailover(getDataSource(), null, getDataSource().getImageVersion())) {
						prepareDependentObjects();
						return INPUT;
					}
				}
				if (!verifyConfigTemplate(getDataSource())) {
					prepareDependentObjects();
					return INPUT;
				}
				if (!checkIsNetworkPolicyProper4Device(getDataSource())) {
					prepareDependentObjects();
					return INPUT;
				}
				if (getDataSource().getMacAddresses() == null && getDataSource().getIpSubNetworks() == null) {
					getDataSource().setAccessControled(false);
				}
				// create capwapIp object if needed;
				IpAddress capwapIp = autoCreateIpAddress();
				if (null != capwapIp) {
					getDataSource().setCfgCapwapIpId(capwapIp.getId());
				}
				IpAddress capwapBackupIp = autoCreateBackupIpAddress();
				if (null != capwapBackupIp) {
					getDataSource().setCapwapBackupIpId(capwapBackupIp.getId());
				}
				//add br100 interface settings to bo
				convertBr100InterfaceSetting();
				String rs = createBo();
				removeHomeHiveAPs(getDataSource());
				return rs;
			} else if ("edit".equals(operation)) {
				log.info("execute", "operation:" + operation + ", id:" + id);
				String returnWord = editBo(this);
				if (dataSource != null) {
					prepareDependentObjects();
					prepareBr100InterfaceSetting();
				}
				addLstTitle(getText("hiveAp.title.autoProvisioning.edit")
						+ " '" + getChangedName() + "'");
				return returnWord;
			} else if ("update".equals(operation)) {
				log.info("execute", "operation:" + operation + ", id:" + id);
				if (null == getDataSource() || !setSelectedObjects() || !checkRouterMinVersion()) {
					prepareDependentObjects();
					return INPUT;
				}
				// default select wlan policy
				if (isEasyMode()) {
					ConfigTemplate defaultTemplate = HmBeParaUtil
							.getEasyModeDefaultTemplate(domainId);
					getDataSource()
							.setConfigTemplateId(defaultTemplate.getId());
				}
				if (!verify11nInterfaces(getDataSource())) {
					prepareDependentObjects();
					return INPUT;
				}
				if (HiveAp.isWifi1Available(getDataSource().getModelType()) && getDataSource().isUploadImage()) {
					if (!verifyRadioFailover(getDataSource(), null, getDataSource().getImageVersion())) {
						prepareDependentObjects();
						return INPUT;
					}
				}
				if (!verifyConfigTemplate(getDataSource())) {
					prepareDependentObjects();
					return INPUT;
				}
				if (!checkIsNetworkPolicyProper4Device(getDataSource())) {
					prepareDependentObjects();
					return INPUT;
				}
				if (getDataSource().getMacAddresses() == null && getDataSource().getIpSubNetworks() == null) {
					getDataSource().setAccessControled(false);
				}
				// create capwapIp object if needed;
				IpAddress capwapIp = autoCreateIpAddress();
				if (null != capwapIp) {
					getDataSource().setCfgCapwapIpId(capwapIp.getId());
				}
				IpAddress capwapBackupIp = autoCreateBackupIpAddress();
				if (null != capwapBackupIp) {
					getDataSource().setCapwapBackupIpId(capwapBackupIp.getId());
				}
				//add br100 interface settings to bo
				convertBr100InterfaceSetting();
				String rs = updateBo();
				removeHomeHiveAPs(getDataSource());
				return rs;
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				HiveApAutoProvision profile = (HiveApAutoProvision) findBoById(
						boClass, cloneId, this);
				if (null != profile) {
					profile.setOwner(null);
					profile.setId(null);
					profile.setVersion(null);
					profile.setProvisioningName("");

					/*
					 * for now, serial number can be used in one profile, so, when cloning a profile,
					 * all selected serial numbers will be removed.
					 *
					List<String> list = new ArrayList<String>();
					for (String sc : profile.getMacAddresses()) {
						list.add(sc);
					}
					profile.setMacAddresses(list);
					*/
					profile.setMacAddresses(null);

					setSessionDataSource(profile);
					prepareDependentObjects();
					prepareBr100InterfaceSetting();
					addLstTitle(getText("hiveAp.title.autoProvisioning"));
					return INPUT;
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("newTemplate".equals(operation)
					|| "newWifi0RadioProfile".equals(operation)
					|| "newWifi1RadioProfile".equals(operation)
					|| "newCapwapIp".equals(operation)
					|| "newCapwapBackupIp".equals(operation)
					|| "editTemplate".equals(operation)
					|| "editCapwapIp".equals(operation)
					|| "editCapwapBackupIp".equals(operation)
					|| "importSn".equals(operation)
					|| "importIpSubNetwork".equals(operation)) {
				log.info("execute", "operation:" + operation);
				setSelectedObjects();
				clearErrorsAndMessages();
				MgrUtil.setSessionAttribute(getSelectedL2FeatureKey()+"_radioPsePriority", radioPsePriority);
				if ("newCapwapBackupIp".equals(operation)
						|| "editCapwapBackupIp".equals(operation)
						|| "newWifi1RadioProfile".equals(operation)) {
					addLstForward("autoProvisioningConfig2");
				} else if ("importIpSubNetwork".equals(operation)) {
					addLstForward("autoProvisioningConfigIpSubNetworks");
				}
				else {
					addLstForward("autoProvisioningConfig");
				}
				return operation;
			} else if ("importSn2".equals(operation)) {
				clearErrorsAndMessages();
				addLstForward("autoProvisioningConfig");
				return "importSn";
			}  else if ("importIpSubNetwork2".equals(operation)) {
				clearErrorsAndMessages();
				addLstForward("autoProvisioningConfigIpSubNetworks");
				return "importIpSubNetwork";
			} else if ("continue".equals(operation) || "continue1".equals(operation)) {
				log.info("execute", "operation:" + operation);
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				if (null == getDataSource()) {
					return prepareBoList();
				} else {
					setSelectedIds();
					prepareDependentObjects();
					fetchBrEthxFromDataSource();
					radioPsePriority = (String) MgrUtil.getSessionAttribute(getSelectedL2FeatureKey()+"_radioPsePriority");
					MgrUtil.removeSessionAttribute(getSelectedL2FeatureKey()+"_radioPsePriority");
					setId(dataSource.getId());
					return INPUT;
				}
			} else if ("removeSn".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", removed sn string:" + snString);
				Set<String> sns = new HashSet<String>();
				if (null != snString) {
					String[] array = snString.split(",");
					sns.addAll(Arrays.asList(array));
				}
				removeSerialNumbers(sns);
				prepareDependentObjects();
				return INPUT;
			}  else if ("removeIpSubNetworks".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", removed ipSubNetworks string:" + ipSubNetworkString);
				Set<String> ips = new HashSet<String>();
				if (null != ipSubNetworkString) {
					String[] array = ipSubNetworkString.split(",");
					ips.addAll(Arrays.asList(array));
				}
				removeIpSubNetworks(ips);
				prepareDependentObjects();
				return INPUT;
			} else if ("fetchRadioProfiles".equals(operation)) {
				log.info("execute", "operation:" + operation + ", apModelType:"
						+ apModelType + ", apDeviceType:" + apDevicelType + ", countryCode:" + countryCode
						+ ", wifi0OperationMode:" + wifi0OperationMode
						+ ", wifi1OperationMode:" + wifi1OperationMode);
				if (HiveAp.is11nHiveAP(apModelType)) {
					if ((AhConstantUtil.isTrueAll(Device.IS_SINGLERADIO, apModelType)
							&& !AhConstantUtil.isTrueAll(Device.IS_DUALBAND, apModelType))
							|| !AhConstantUtil.isTrueAll(Device.IS_SINGLERADIO, apModelType)) {
						jsonObject = get11nDualRadioProfiles(countryCode,
								wifi0OperationMode, wifi1OperationMode, apModelType);
					} else {
						jsonObject = get11nSingleRadioProfiles(countryCode,
								wifi0OperationMode);
					}
				} else {
					jsonObject = getAg20RadioProfiles(countryCode,
							wifi0OperationMode, wifi1OperationMode);
				}
				jsonObject.put("versionList", getVersionJSONList(apModelType));
				return "json";
			} else if ("fetchImageVersionInfos".equals(operation)) {
				log.info("execute", "operation: " + operation
						+ ", apModelType: " + apModelType);
				jsonObject = new JSONObject();
				jsonObject.put("versionList", getVersionJSONList(apModelType));
				return "json";
			} else if ("getRadioMode".equals(operation)) {
				log.info("execute", "operation:" + operation + ", profileId:"
						+ id + ", countryCode:" + countryCode
						+ ", operationMode:" + operationMode + ", apModeType:"
						+ apModelType);
				jsonObject = getRadioMode(apModelType, id, countryCode,
						operationMode);
				return "json";
			} else if ("requestChannels".equals(operation)) {
				log.info("requestChannels", "operation:" + operation
						+ ", wifi0 profileId:" + wifi0ProfileId
						+ ", wifi1 profileId:" + wifi1ProfileId
						+ ", countryCode:" + countryCode
						+ ", wifi0OperationMode:" + wifi0OperationMode
						+ ", wifi1OperationMode:" + wifi1OperationMode
						+ ", apModeType:" + apModelType);
				jsonObject = requestChannels(apModelType, wifi0ProfileId,
						wifi1ProfileId, countryCode, wifi0OperationMode,
						wifi1OperationMode);
				return "json";
			} else if ("editSnOut".equals(operation)) {
				log.info("execute", "operation:" + operation);
				jsonObject = getJsonSerialNumbers();
				return "json";
			} else if ("ipManagementOut".equals(operation)) {
				log.info("execute", "operation:" + operation);
				jsonObject = this.getJsonIpSubNetworks();
				return "json";
			} else if ("removeSnOut".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", removed sn string:" + snString);
				Set<String> sns = new HashSet<String>();
				if (null != snString) {
					String[] array = snString.split(",");
					sns.addAll(Arrays.asList(array));
				}
				jsonObject = removeJsonSerialNumbers(sns);
				return "json";
			} else if ("removeIpSubNetworkOut".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", removed ipSubNetworks string:" + ipSubNetworkString);
				Set<String> ips = new HashSet<String>();
				if (null != ipSubNetworkString) {
					String[] array = ipSubNetworkString.split(",");
					ips.addAll(Arrays.asList(array));
				}
				jsonObject = removeJsonIpSubNetworks(ips);
				return "json";
			} else if ("enterIpSubNetworkOut".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", enter ipSubNetworks string:" + ipSubNetworkString);
				Set<String> ips = new HashSet<String>();
				if (null != ipSubNetworkString) {
					String[] array = ipSubNetworkString.split(",");
					ips.addAll(Arrays.asList(array));
				}
				jsonObject = addJsonIpSubNetworks(ips);
				return "json";
			} else if ("saveSn".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", scanSerialNumbers:" + scanSerialNumbers);
				saveSn(scanSerialNumbers);
				return prepareBoList();
			} else if ("fetchAllSerialNumber".equals(operation)) {
				log.info("execute", "operation:" + operation);
				jsonArray = getAllSerialNumbers();
				return "json";
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception ex) {
			return prepareActionError(ex);
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HiveApAutoProvision) {
			HiveApAutoProvision autoProvision = (HiveApAutoProvision) bo;

			if (autoProvision.getMacAddresses() != null) {
				autoProvision.getMacAddresses().size();
			}

			if (autoProvision.getIpSubNetworks() != null) {
				autoProvision.getIpSubNetworks().size();
			}

			if (autoProvision.getDeviceInterfaces() != null) {
				autoProvision.getDeviceInterfaces().size();
			}
			if(autoProvision.getOwner() !=null){
				autoProvision.getOwner().getId();
			}
		}

		return null;
	}

	public String getChangedName() {
		return getDataSource().getName().replace("\\", "\\\\")
				.replace("'", "\\'");
	}

	/*
	 * Currently only one provision for one model type;
	 */
	private boolean checkProvisionExisted() {
		short apModelType = getDataSource().getModelType();
		short apDeviceType = getDataSource().getDeviceType();
	//	HiveApAutoProvision autoProvision = QueryUtil.findBoByAttribute(HiveApAutoProvision.class,
	//			"modelType", apModelType, domainId);

		//long count = QueryUtil.findRowCount(HiveApAutoProvision.class, new FilterParams("modelType = :s1 and owner.id = :s2 and deviceType = :s3", new Object[] { apModelType, domainId, apDeviceType }));

		boolean isAccessControledTmp = getDataSource().isAccessControled();
		if (isAccessControledTmp
				&& (getIpSubNetworks() == null || getIpSubNetworks().size() < 1)
				&& (getSerialNumbers() == null || getSerialNumbers().size() < 1)) {
			isAccessControledTmp = false;
		}
	//	if (null != autoProvision) {
		/*if (count > 0 && !isAccessControledTmp) {
			addActionError(MgrUtil
					.getUserMessage("error.provision.cannotCreateMore"));
			return true;
		} else { */
		if (getDataSource() != null) {
			getDataSource().setProvisioningName(HiveApAutoProvision.getProvisionName(apModelType, apDeviceType));
		}
		return false;
	}

	private void setSelectedIds() throws Exception {
		if (null == getDataSource()) {
			return;
		}
		if (null != configTemplateId) {
			getDataSource().setConfigTemplateId(configTemplateId);
		}
		if (null != capwapIpId) {
			getDataSource().setCfgCapwapIpId(capwapIpId);
		}
		if (null != capwapBackupIpId) {
			getDataSource().setCapwapBackupIpId(capwapBackupIpId);
		}
		if (null != wifi0ProfileId) {
			getDataSource().setWifi0ProfileId(wifi0ProfileId);
		}
		if (null != wifi1ProfileId) {
			getDataSource().setWifi1ProfileId(wifi1ProfileId);
		}
		if (null == getDataSource().getWifi0ProfileId()
				|| getDataSource().getWifi0ProfileId() == -2) {
			getDataSource().setWifi0ProfileId(0L);
		}
		if (null == getDataSource().getWifi1ProfileId()
				|| getDataSource().getWifi1ProfileId() == -2) {
			getDataSource().setWifi1ProfileId(0L);
		}
	}

	private IpAddress autoCreateIpAddress() {
		if ((null == capwapIpId || capwapIpId == -1)
				&& null != getDataSource().getCapwapText()
				&& !"".equals(getDataSource().getCapwapText())) {
			short ipType = ImportCsvFileAction
					.getIpAddressWrongFlag(getDataSource().getCapwapText()) ? IpAddress.TYPE_HOST_NAME
					: IpAddress.TYPE_IP_ADDRESS;
			return CreateObjectAuto.createNewIP(getDataSource()
					.getCapwapText(), ipType, getDomain(), MgrUtil
					.getUserMessage("hiveAp.capwap.server")
					+ " for Auto Provisioning:"
					+ getDataSource().getProvisioningName());
		}
		return null;
	}

	private IpAddress autoCreateBackupIpAddress() {
		if ((null == capwapBackupIpId || capwapBackupIpId == -1)
				&& null != getDataSource().getCapwapBackupText()
				&& !"".equals(getDataSource().getCapwapBackupText())) {
			short ipType = ImportCsvFileAction
					.getIpAddressWrongFlag(getDataSource()
							.getCapwapBackupText()) ? IpAddress.TYPE_HOST_NAME
					: IpAddress.TYPE_IP_ADDRESS;
			return CreateObjectAuto.createNewIP(getDataSource()
					.getCapwapBackupText(), ipType, getDomain(), MgrUtil
					.getUserMessage("hiveAp.capwap.server.backup")
					+ " for Auto Provisioning:"
					+ getDataSource().getProvisioningName());
		}
		return null;
	}

	private boolean setSelectedObjects() throws Exception {
		boolean rs1 = setSelectedConfigTemplate();
		boolean rs2 = setSelectedTopology();
		boolean rs3 = setSelectedRadioProfiles();
		boolean rs5 = setSelectedCapwapIp();
		boolean rs7 = setSelectedHiveAPs();
		boolean rs8 = setSelectedIpSubNetworks();
		if (!getDataSource().isUploadImage()) {
			// de-select image while un-check this check box;
			getDataSource().setImageName(null);
			getDataSource().setImageVersion(null);
		}
		return rs1 && rs2 && rs3 && /* rs4 && */rs5 && rs7 && rs8;
	}

	protected boolean setSelectedCapwapIp() throws Exception {
		boolean result = true;
		if (null != capwapIpId) {
			IpAddress cp = findBoById(IpAddress.class, capwapIpId);
			if (cp == null && capwapIpId != -1) {
				String tempStr[] = { getText("hiveAp.capwap.server") };
				addActionError(getText("info.ssid.warning", tempStr));
				result = false;
			}
			if (null != getDataSource()) {
				getDataSource()
						.setCfgCapwapIpId(cp == null ? null : capwapIpId);
			}
		}
		if (null != capwapBackupIpId) {
			IpAddress cp = findBoById(IpAddress.class,
					capwapBackupIpId);
			if (cp == null && capwapBackupIpId != -1) {
				String tempStr[] = { getText("hiveAp.capwap.server.backup") };
				addActionError(getText("info.ssid.warning", tempStr));
				result = false;
			}
			if (null != getDataSource()) {
				getDataSource().setCapwapBackupIpId(
						cp == null ? null : capwapBackupIpId);
			}
		}
		return result;
	}

	protected boolean setSelectedConfigTemplate() throws Exception {
		boolean result = true;
		Long configTemplate = getDataSource().getConfigTemplateId();
		if (null != configTemplate) {
			ConfigTemplate template = findBoById(
					ConfigTemplate.class, configTemplate);
			if (template == null && configTemplate != -1) {
				String tempStr[] = { getText("hiveAp.template") };
				addActionError(getText("info.ssid.warning", tempStr));
				result = false;
			}
			if (null != getDataSource()) {
				getDataSource().setConfigTemplateId(
						template == null ? null : configTemplate);
			}
		}
		return result;
	}

	protected boolean setSelectedTopology() throws Exception {
		boolean result = true;
		Long topoMap = getDataSource().getMapContainerId();
		if (null != topoMap) {
			if (topoMap == -2L) {
				getDataSource().setRewriteMap(false);
				getDataSource().setMapContainerId(null);
				return true;
			}
			MapContainerNode container = findBoById(
					MapContainerNode.class, topoMap);
			if (container == null && topoMap != -1) {
				String tempStr[] = { getText("hiveAp.autoProvisioning.defaultTopoMap.label") };
				addActionError(getText("info.ssid.warning", tempStr));
				result = false;
			}
			if (null != getDataSource()) {
				getDataSource().setRewriteMap(true);
				getDataSource().setMapContainerId(topoMap);
			}
		}
		return result;
	}

	private boolean setSelectedRadioProfiles() throws Exception {
		boolean result = true;
		short apModelType = getDataSource().getModelType();

		Long wifi0RadioProfile = getDataSource().getWifi0ProfileId();
		Long wifi1RadioProfile = getDataSource().getWifi1ProfileId();
		if (HiveAp.is11nHiveAP(apModelType)) {
			if (null != wifi0RadioProfile) {
				RadioProfile radioProfile = findBoById(
						RadioProfile.class, wifi0RadioProfile);
				if (null == radioProfile && wifi0RadioProfile != -1) {
					String tempStr[] = { getText("hiveAp.if.radioProfile") };
					addActionError(getText("info.ssid.warning", tempStr));
					result = false;
				}
			}
			if (HiveAp.isWifi1Available(apModelType)
					&& null != wifi1RadioProfile) {
				RadioProfile radioProfile = findBoById(
						RadioProfile.class, wifi1RadioProfile);
				if (null == radioProfile && wifi1RadioProfile != -1) {
					String tempStr[] = { getText("hiveAp.if.radioProfile") };
					addActionError(getText("info.ssid.warning", tempStr));
					result = false;
				}
			} else {
				getDataSource().setWifi1ProfileId(0L);
			}
		} else {
			if (null != wifi0RadioProfile) {
				RadioProfile radioProfile = findBoById(
						RadioProfile.class, wifi0RadioProfile);
				if (null == radioProfile && wifi0RadioProfile != -1) {
					String tempStr[] = { getText("hiveAp.if.radioProfile") };
					addActionError(getText("info.ssid.warning", tempStr));
					result = false;
				}
			}
			if (HiveAp.isWifi1Available(apModelType)
					&& null != wifi1RadioProfile) {
				RadioProfile radioProfile = findBoById(
						RadioProfile.class, wifi1RadioProfile);
				if (null == radioProfile && wifi1RadioProfile != -1) {
					String tempStr[] = { getText("hiveAp.if.radioProfile") };
					addActionError(getText("info.ssid.warning", tempStr));
					result = false;
				}
			} else {
				getDataSource().setWifi1ProfileId(0L);
			}
		}
		return result;
	}

	protected boolean setSelectedHiveAPs() {
		boolean result = true;
		if (getDataSource().isAccessControled()) {
			if (getDataSource().getAclType() == HiveApAutoProvision.ACL_MANUAL_AP) {
				short type = getDataSource().getModelType();
				switch (type) {
				case HiveAp.HIVEAP_MODEL_110:
					if (null != hiveAps_110 && hiveAps_110.size() > 0) {
						getDataSource().setMacAddresses(hiveAps_110);
					} else {
						getDataSource().setMacAddresses(null);
					}
					break;
				case HiveAp.HIVEAP_MODEL_120:
					if (null != hiveAps_120 && hiveAps_120.size() > 0) {
						getDataSource().setMacAddresses(hiveAps_120);
					} else {
						getDataSource().setMacAddresses(null);
					}
					break;
				case HiveAp.HIVEAP_MODEL_320:
					if (null != hiveAps_320 && hiveAps_320.size() > 0) {
						getDataSource().setMacAddresses(hiveAps_320);
					} else {
						getDataSource().setMacAddresses(null);
					}
					break;
				case HiveAp.HIVEAP_MODEL_340:
					if (null != hiveAps_340 && hiveAps_340.size() > 0) {
						getDataSource().setMacAddresses(hiveAps_340);
					} else {
						getDataSource().setMacAddresses(null);
					}
					break;
				case HiveAp.HIVEAP_MODEL_380:
					if (null != hiveAps_380 && hiveAps_380.size() > 0) {
						getDataSource().setMacAddresses(hiveAps_380);
					} else {
						getDataSource().setMacAddresses(null);
					}
					break;
				case HiveAp.HIVEAP_MODEL_330:
					if (null != hiveAps_330 && hiveAps_330.size() > 0) {
						getDataSource().setMacAddresses(hiveAps_330);
					} else {
						getDataSource().setMacAddresses(null);
					}
					break;
				case HiveAp.HIVEAP_MODEL_350:
					if (null != hiveAps_350 && hiveAps_350.size() > 0) {
						getDataSource().setMacAddresses(hiveAps_350);
					} else {
						getDataSource().setMacAddresses(null);
					}
					break;
				case HiveAp.HIVEAP_MODEL_370:
					if (null != hiveAps_370 && hiveAps_370.size() > 0) {
						getDataSource().setMacAddresses(hiveAps_370);
					} else {
						getDataSource().setMacAddresses(null);
					}
					break;
				case HiveAp.HIVEAP_MODEL_390:
					if (null != hiveAps_390 && hiveAps_390.size() > 0) {
						getDataSource().setMacAddresses(hiveAps_390);
					} else {
						getDataSource().setMacAddresses(null);
					}
					break;
				case HiveAp.HIVEAP_MODEL_230:
					if (null != hiveAps_230 && hiveAps_230.size() > 0) {
						getDataSource().setMacAddresses(hiveAps_230);
					} else {
						getDataSource().setMacAddresses(null);
					}
					break;
				case HiveAp.HIVEAP_MODEL_20:
					if (null != hiveAps && hiveAps.size() > 0) {
						getDataSource().setMacAddresses(hiveAps);
					} else {
						getDataSource().setMacAddresses(null);
					}
					break;
				case HiveAp.HIVEAP_MODEL_28:
					if (null != hiveAps_28 && hiveAps_28.size() > 0) {
						getDataSource().setMacAddresses(hiveAps_28);
					} else {
						getDataSource().setMacAddresses(null);
					}
					break;
				}
			} else if (getDataSource().getAclType() == HiveApAutoProvision.ACL_IMPORT_SN) {
				if (null != serialNumbers && serialNumbers.size() > 0) {
					getDataSource().setMacAddresses(serialNumbers);
				} else {
					getDataSource().setMacAddresses(null);
				}
			}
		} else {
			getDataSource().setMacAddresses(null);
		}
		return result;
	}

	protected boolean setSelectedIpSubNetworks() {
		boolean result = true;
		if (getDataSource().isAccessControled()) {
			if (isFullMode()) {
				if (null != ipSubNetworks && ipSubNetworks.size() > 0) {
					getDataSource().setIpSubNetworks(ipSubNetworks);
				} else {
					getDataSource().setIpSubNetworks(null);
				}
			}
		} else {
			getDataSource().setIpSubNetworks(null);
		}
		return result;
	}

	private boolean verify11nInterfaces(HiveApAutoProvision provision)
			throws Exception {
		/* just need eth0 & eth1 to verify */
		HiveAp sample = new HiveAp();
		sample.setHiveApModel(provision.getModelType());
		sample.setEth0(provision.getEth0());
		sample.setEth1(provision.getEth1());
		List<String> errorMsg = new ArrayList<String>();
		boolean result = new HiveApAction().verify11nInterfacesPublic(sample,
				errorMsg);

		if (!errorMsg.isEmpty()) {
			addActionError(errorMsg.get(0));
		}
		return result;
	}

	private boolean verifyRadioFailover(HiveApAutoProvision provision,
			String withHostname, String softver) {
		RadioProfile wifi0Profile = provision.getWifi0RadioProfile();
		RadioProfile wifi1Profile = provision.getWifi1RadioProfile();
		List<String> errorMsg = new ArrayList<String>();
		boolean result = new HiveApAction().verifyRadioFailoverPublic(
				wifi0Profile, wifi1Profile, withHostname, errorMsg, softver);
		if (!errorMsg.isEmpty()) {
			addActionError(errorMsg.get(0));
		}
		return result;
	}

	private boolean verifyConfigTemplate(HiveApAutoProvision provision)
			throws Exception {
		Long configTemplateId = provision.getConfigTemplateId();
		// Long lldpId = provision.getLldpProfileId();
		/* need modelType and eth0 & eth1 to verify */
		HiveAp hiveAp = new HiveAp(provision.getModelType());
		hiveAp.setEth0(provision.getEth0());
		hiveAp.setEth1(provision.getEth1());

		ConfigTemplate configTemp = null;
		if (configTemplateId != null) {
			configTemp = findBoById(ConfigTemplate.class,
					configTemplateId, new HiveApAction());
		}

		List<String> errorMsg = new ArrayList<String>();
		boolean result = new HiveApAction().verifyConfigTemplatePublic(hiveAp,
				configTemp, errorMsg, null);
		if (!errorMsg.isEmpty()) {
			addActionError(errorMsg.get(0));
		}
		return result;
	}

	private void prepareDependentObjects() throws Exception {
		prepareConfigTemplate();
		prepareRadioProfiles();
		prepareTopoMap();
		prepareCapwapIps();
		prepareManuallyHiveAps();
		prepareSerialNumbers();
		prepareIpSubNetworks();
	}

	protected void prepareRadioProfiles() throws Exception {
		if (null != getDataSource()) {
			short model = getDataSource().getModelType();
			if (HiveAp.is11nHiveAP(model)) {
				if ((AhConstantUtil.isTrueAll(Device.IS_SINGLERADIO, model)
						&& !AhConstantUtil.isTrueAll(Device.IS_DUALBAND, model))
						|| !AhConstantUtil.isTrueAll(Device.IS_SINGLERADIO, model)) {
					//for AP370/AP390 support 11ac radio mode
					if(HiveAp.is11acHiveAP(model)){
						wifi1RadioProfiles = getRadioProfile(new Short[] {
								RadioProfile.RADIO_PROFILE_MODE_A,
								RadioProfile.RADIO_PROFILE_MODE_NA,
								RadioProfile.RADIO_PROFILE_MODE_AC});
						if(null == getDataSource().getWifi1ProfileId()){
							RadioProfile defaultWifi1Profile = QueryUtil.findBoByAttribute(RadioProfile.class, "radioName",
									BeParaModule.DEFAULT_RADIO_PROFILE_NAME_AC);
							wifi1RadioModeLabel = defaultWifi1Profile.getRadioModeString();
						}
					}else{
						wifi1RadioProfiles = getRadioProfile(new Short[] {
								RadioProfile.RADIO_PROFILE_MODE_A,
								RadioProfile.RADIO_PROFILE_MODE_NA });
					}
					wifi0RadioProfiles = getRadioProfile(new Short[] {
							RadioProfile.RADIO_PROFILE_MODE_BG,
							RadioProfile.RADIO_PROFILE_MODE_NG });
				} else {
					wifi0RadioProfiles = getRadioProfile(new Short[] {
							RadioProfile.RADIO_PROFILE_MODE_BG,
							RadioProfile.RADIO_PROFILE_MODE_NG,
							RadioProfile.RADIO_PROFILE_MODE_A,
							RadioProfile.RADIO_PROFILE_MODE_NA,
							RadioProfile.RADIO_PROFILE_MODE_AC });
					// avoid exception
					wifi1RadioProfiles = wifi0RadioProfiles;
				}
			} else {
				wifi1RadioProfiles = getRadioProfile(new Short[] { RadioProfile.RADIO_PROFILE_MODE_A });
				wifi0RadioProfiles = getRadioProfile(new Short[] { RadioProfile.RADIO_PROFILE_MODE_BG });
			}
			// update radio mode label && radio label && channel list
			if (null != getDataSource().getWifi0ProfileId()) {
				RadioProfile profile = findBoById(
						RadioProfile.class, getDataSource().getWifi0ProfileId());
				if (null != profile) {
					wifi0RadioModeLabel = profile.getRadioModeString();
					if (profile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG
							|| profile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG) {
						wifi0Label = MgrUtil.getUserMessage("hiveAp.if.24G");
					} else {
						wifi0Label = MgrUtil.getUserMessage("hiveAp.if.5G");
					}
					wifi0ChannelList = getChannelList(model, getDataSource()
							.getCountryCode(), profile, getDataSource()
							.getWifi0().getOperationMode());
				}
				//the default for BR100 like devices should be ng
				else {
					wifi0RadioModeLabel = MgrUtil
					.getEnumString("enum.radioProfileMode."
							+ RadioProfile.RADIO_PROFILE_MODE_NG);
				}
			}
			if (null != getDataSource().getWifi1ProfileId()) {
				RadioProfile profile = findBoById(
						RadioProfile.class, getDataSource().getWifi1ProfileId());
				if (null != profile) {
					wifi1RadioModeLabel = profile.getRadioModeString();
					if (profile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
							|| profile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA
							|| profile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC) {
						wifi1Label = MgrUtil.getUserMessage("hiveAp.if.5G");
					} else {
						wifi1Label = MgrUtil.getUserMessage("hiveAp.if.24G");
					}
					wifi1ChannelList = getChannelList(model, getDataSource()
							.getCountryCode(), profile, getDataSource()
							.getWifi1().getOperationMode());
				}
			}
		}
	}

	protected List<CheckItem> getRadioProfile(Short[] radioMode) {
		List<CheckItem> radioProfiles = getBoCheckItems("radioName",
				RadioProfile.class, new FilterParams("radioMode", Arrays
						.asList(radioMode)));
		if (radioProfiles.size() == 0) {
			radioProfiles.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		if ("".equals(getWriteDisabled())) {
			radioProfiles.add(new CheckItem((long) -2, "[-New-]"));
		}
		return radioProfiles;
	}

	private void prepareSerialNumbers() {
		List<CheckItem> serialNumbers = getBoCheckItems("serialNumber",
				HiveApSerialNumber.class, null);
		List<CheckItem> removeList = new ArrayList<CheckItem>();

		if (getDataSource().getAclType() == HiveApAutoProvision.ACL_IMPORT_SN) {
			for (CheckItem oneItem : serialNumbers) {
				if (oneItem.getId() == CHECK_ITEM_ID_NONE) {
					continue;
				}
				if (null != getDataSource().getMacAddresses()) {
					for (String item : getDataSource().getMacAddresses()) {
						if (item.equals(oneItem.getValue())) {
							removeList.add(oneItem);
						}
					}
				}
			}
			serialNumbers.removeAll(removeList);
		}

		/*
		 * query serial numbers which are used
		 */
		String queryStr = "select distinct b.id, a.macaddress from hive_ap_auto_provision_maces a, "
								+ "hive_ap_serial_number b where a.macaddress = b.serialnumber";
		List<?> usedSerialNumbers = QueryUtil.executeNativeQuery(queryStr);
		List<CheckItem> usedList = new ArrayList<CheckItem>();
		if (usedSerialNumbers != null && usedSerialNumbers.size() > 0) {
			for (Object obj : usedSerialNumbers) {
				Object[] objs = (Object[])obj;
				usedList.add(new CheckItem(Long.valueOf(objs[0].toString()), (String)objs[1]));
			}
		}
		serialNumbers.removeAll(usedList);

		// For the OptionsTransfer component
		snOptions = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.serialNumber.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.serialNumber.selected"),
				serialNumbers, removeList, "value", "value", "serialNumbers");
	}

	private void prepareIpSubNetworks() {
		List<CheckItem> ipSubNetworkItems = getBoCheckItems("ipSubNetwork",
				DeviceIPSubNetwork.class, null);
		List<CheckItem> selectedList = new ArrayList<CheckItem>();

		for (CheckItem oneItem : ipSubNetworkItems) {
			if (oneItem.getId() == CHECK_ITEM_ID_NONE) {
				continue;
			}
			if (null != getDataSource().getIpSubNetworks()) {
				for (String item : getDataSource().getIpSubNetworks()) {
					if (item.equals(oneItem.getValue())) {
						selectedList.add(oneItem);
					}
				}
			}
		}
		ipSubNetworkItems.removeAll(selectedList);

		ipSubNetworkOptions = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.ipSubNetwork.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.ipSubNetwork.selected"),
						ipSubNetworkItems, selectedList, "value", "value", "ipSubNetworks");
	}

	private void prepareManuallyHiveAps() {
		String where = "manageStatus = :s1 AND origin = :s2";
		Object[] values = new Object[2];
		values[0] = HiveAp.STATUS_NEW;
		values[1] = HiveAp.ORIGIN_CREATE;
		List<?> hiveApFields = QueryUtil
				.executeQuery(
						"select bo.macAddress, bo.hostName, bo.hiveApModel from " + HiveAp.class.getSimpleName() + " bo",
						null, new FilterParams(where, values), domainId);
		Collection<SimpleHiveAp> totalHiveAps_20 = new ArrayList<SimpleHiveAp>();
		Collection<SimpleHiveAp> totalHiveAps_28 = new ArrayList<SimpleHiveAp>(); // 28
		Collection<SimpleHiveAp> totalHiveAps_340 = new ArrayList<SimpleHiveAp>();// 340
		Collection<SimpleHiveAp> totalHiveAps_320 = new ArrayList<SimpleHiveAp>();// 320
		Collection<SimpleHiveAp> totalHiveAps_vi = new ArrayList<SimpleHiveAp>();// 320
		Collection<SimpleHiveAp> totalHiveAps_380 = new ArrayList<SimpleHiveAp>();// 380
		Collection<SimpleHiveAp> totalHiveAps_120 = new ArrayList<SimpleHiveAp>();// 120
		Collection<SimpleHiveAp> totalHiveAps_110 = new ArrayList<SimpleHiveAp>();// 110
		Collection<SimpleHiveAp> totalHiveAps_330 = new ArrayList<SimpleHiveAp>();// 330
		Collection<SimpleHiveAp> totalHiveAps_350 = new ArrayList<SimpleHiveAp>();// 350
		Collection<SimpleHiveAp> totalHiveAps_370 = new ArrayList<SimpleHiveAp>();// 370
		Collection<SimpleHiveAp> totalHiveAps_390 = new ArrayList<SimpleHiveAp>();// 390
		Collection<SimpleHiveAp> totalHiveAps_230 = new ArrayList<SimpleHiveAp>();// 230
		Collection<SimpleHiveAp> selectedHiveAps_20 = new ArrayList<SimpleHiveAp>();
		Collection<SimpleHiveAp> selectedHiveAps_28 = new ArrayList<SimpleHiveAp>();
		Collection<SimpleHiveAp> selectedHiveAps_340 = new ArrayList<SimpleHiveAp>();
		Collection<SimpleHiveAp> selectedHiveAps_320 = new ArrayList<SimpleHiveAp>();
		Collection<SimpleHiveAp> selectedHiveAps_vi = new ArrayList<SimpleHiveAp>();
		Collection<SimpleHiveAp> selectedHiveAps_380 = new ArrayList<SimpleHiveAp>();
		Collection<SimpleHiveAp> selectedHiveAps_120 = new ArrayList<SimpleHiveAp>();
		Collection<SimpleHiveAp> selectedHiveAps_110 = new ArrayList<SimpleHiveAp>();
		Collection<SimpleHiveAp> selectedHiveAps_330 = new ArrayList<SimpleHiveAp>();
		Collection<SimpleHiveAp> selectedHiveAps_350 = new ArrayList<SimpleHiveAp>();
		Collection<SimpleHiveAp> selectedHiveAps_370 = new ArrayList<SimpleHiveAp>();
		Collection<SimpleHiveAp> selectedHiveAps_390 = new ArrayList<SimpleHiveAp>();
		Collection<SimpleHiveAp> selectedHiveAps_230 = new ArrayList<SimpleHiveAp>();

		List<String> existed = getDataSource().getMacAddresses();
		for (Object object : hiveApFields) {
			Object[] fields = (Object[]) object;
			String mac = (String) fields[0];
			Short type = (Short) fields[2];
			SimpleHiveAp hiveAp = new SimpleHiveAp();
			hiveAp.setMacAddress(mac);
			hiveAp.setHostname((String) fields[1]);
			if (null != type) {
				switch (type) {
				case HiveAp.HIVEAP_MODEL_110:
					if (null != existed && existed.contains(mac)) {
						selectedHiveAps_110.add(hiveAp);
					} else {
						totalHiveAps_110.add(hiveAp);
					}
					break;
				case HiveAp.HIVEAP_MODEL_120:
					if (null != existed && existed.contains(mac)) {
						selectedHiveAps_120.add(hiveAp);
					} else {
						totalHiveAps_120.add(hiveAp);
					}
					break;
				case HiveAp.HIVEAP_MODEL_320:
					if (null != existed && existed.contains(mac)) {
						selectedHiveAps_320.add(hiveAp);
					} else {
						totalHiveAps_320.add(hiveAp);
					}
					break;
				case HiveAp.HIVEAP_MODEL_340:
					if (null != existed && existed.contains(mac)) {
						selectedHiveAps_340.add(hiveAp);
					} else {
						totalHiveAps_340.add(hiveAp);
					}
					break;
				case HiveAp.HIVEAP_MODEL_380:
					if (null != existed && existed.contains(mac)) {
						selectedHiveAps_380.add(hiveAp);
					} else {
						totalHiveAps_380.add(hiveAp);
					}
					break;
				case HiveAp.HIVEAP_MODEL_330:
					if (null != existed && existed.contains(mac)) {
						selectedHiveAps_330.add(hiveAp);
					} else {
						totalHiveAps_330.add(hiveAp);
					}
					break;
				case HiveAp.HIVEAP_MODEL_350:
					if (null != existed && existed.contains(mac)) {
						selectedHiveAps_350.add(hiveAp);
					} else {
						totalHiveAps_350.add(hiveAp);
					}
					break;
				case HiveAp.HIVEAP_MODEL_370:
					if (null != existed && existed.contains(mac)) {
						selectedHiveAps_370.add(hiveAp);
					} else {
						totalHiveAps_370.add(hiveAp);
					}
					break;
				case HiveAp.HIVEAP_MODEL_390:
					if (null != existed && existed.contains(mac)) {
						selectedHiveAps_390.add(hiveAp);
					} else {
						totalHiveAps_390.add(hiveAp);
					}
					break;
				case HiveAp.HIVEAP_MODEL_230:
					if (null != existed && existed.contains(mac)) {
						selectedHiveAps_230.add(hiveAp);
					} else {
						totalHiveAps_230.add(hiveAp);
					}
					break;					
				case HiveAp.HIVEAP_MODEL_20:
					if (null != existed && existed.contains(mac)) {
						selectedHiveAps_20.add(hiveAp);
					} else {
						totalHiveAps_20.add(hiveAp);
					}
					break;
				case HiveAp.HIVEAP_MODEL_28:
					if (null != existed && existed.contains(mac)) {
						selectedHiveAps_28.add(hiveAp);
					} else {
						totalHiveAps_28.add(hiveAp);
					}
					break;
				}
			}
		}

		// For the OptionsTransfer component
		hiveApOptions_20 = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				totalHiveAps_20, selectedHiveAps_20, "macAddress", "hostname",
				"hiveAps");
		hiveApOptions_28 = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				totalHiveAps_28, selectedHiveAps_28, "macAddress", "hostname",
				"hiveAps_28");
		hiveApOptions_340 = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				totalHiveAps_340, selectedHiveAps_340, "macAddress",
				"hostname", "hiveAps_340");
		hiveApOptions_320 = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				totalHiveAps_320, selectedHiveAps_320, "macAddress",
				"hostname", "hiveAps_320");
		hiveApOptions_330 = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				totalHiveAps_330, selectedHiveAps_330, "macAddress",
				"hostname", "hiveAps_330");
		hiveApOptions_350 = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				totalHiveAps_350, selectedHiveAps_350, "macAddress",
				"hostname", "hiveAps_350");
		setHiveApOptions_370(new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				totalHiveAps_370, selectedHiveAps_370, "macAddress",
				"hostname", "hiveAps_370"));
		setHiveApOptions_390(new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				totalHiveAps_390, selectedHiveAps_390, "macAddress",
				"hostname", "hiveAps_390"));
		setHiveApOptions_230(new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				totalHiveAps_230, selectedHiveAps_230, "macAddress",
				"hostname", "hiveAps_230"));
		hiveApOptions_vi = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				totalHiveAps_vi, selectedHiveAps_vi, "macAddress", "hostname",
				"hiveAps_vi");
		hiveApOptions_380 = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				totalHiveAps_380, selectedHiveAps_380, "macAddress",
				"hostname", "hiveAps_380");
		hiveApOptions_120 = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				totalHiveAps_120, selectedHiveAps_120, "macAddress",
				"hostname", "hiveAps_120");
		hiveApOptions_110 = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.available"),
				MgrUtil
						.getUserMessage("hiveAp.autoProvisioning.access.control.selected"),
				totalHiveAps_110, selectedHiveAps_110, "macAddress",
				"hostname", "hiveAps_110");
	}

	private void prepareConfigTemplate() {
		// def-policy-template is not in use now
		List<CheckItem> configTemplates = getBoCheckItems("configName",
				ConfigTemplate.class, new FilterParams("lower(configName) <> :s1", new Object[]{BeParaModule.DEFAULT_DEVICE_GROUP_NAME.toLowerCase()}));
		if (configTemplates.size() == 0) {
			configTemplates.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		this.configTemplates = configTemplates;
	}

	private void prepareTopoMap() {
		List<CheckItem> maps = getMapListView();
		List<CheckItem> topoMaps = new ArrayList<CheckItem>();
		topoMaps.add(new CheckItem((long) -2, MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.topoInfo")));
		if (maps.size() == 0) {
			topoMaps.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));

		} else {
			topoMaps.add(new CheckItem((long) -1, ""));
		}
		topoMaps.addAll(maps);

		this.topoMaps = topoMaps;
		if (null != getDataSource()) {
			if (getDataSource().isRewriteMap()) {
				if (getDataSource().getMapContainerId() == null) {
					getDataSource().setMapContainerId(-1L);
				}
			} else {
				getDataSource().setMapContainerId(-2L);
			}
		}
	}

	protected void prepareCapwapIps() {
		capwapIps = getIpObjectsByIpAndName();

		if (null != getDataSource()) {
			IpAddress capwap = getDataSource().getCapwapIpAddress();
			IpAddress capwapBackup = getDataSource().getCapwapBackupIpAddress();
			if (null != capwap) {
				getDataSource().setCapwapText(capwap.getAddressName());
			}
			if (null != capwapBackup) {
				getDataSource().setCapwapBackupText(
						capwapBackup.getAddressName());
			}
		}
	}

	public List<TextItem> getVersionList() {
		List<TextItem> versions = HiveApAutoProvision.getImageVersions(getDataSource().getModelType());
		if(NmsUtil.isHMForOEM()){
			String lowestVer = NmsUtil.getOEMCustomer().getApLowestVersion();
			if(lowestVer != null){
				List<TextItem> rmList = new ArrayList<TextItem>();
				for(TextItem item : versions){
					if(NmsUtil.compareSoftwareVersion(item.getKey(), lowestVer) >= 0){
						rmList.add(item);
					}
				}
				return rmList;
			}
		}
		return versions;
	}

	public String getAg20StuffStyle() {
		if (HiveAp.is20HiveAP(getDataSource().getModelType())) {
			return "";
		} else {
			return "none";
		}
	}

	public String getAg28StuffStyle() {
		if (HiveAp.is28HiveAP(getDataSource().getModelType())) {
			return "";
		} else {
			return "none";
		}
	}

	public String getAg340StuffStyle() {
		if (HiveAp.is340HiveAP(getDataSource().getModelType())) {
			return "";
		} else {
			return "none";
		}
	}

	public String getAg320StuffStyle() {
		if (HiveAp.is320HiveAP(getDataSource().getModelType())) {
			return "";
		} else {
			return "none";
		}
	}

	public String getAg330StuffStyle() {
		if (HiveAp.is330HiveAP(getDataSource().getModelType())) {
			return "";
		} else {
			return "none";
		}
	}

	public String getAg350StuffStyle() {
		if (HiveAp.is350HiveAP(getDataSource().getModelType())) {
			return "";
		} else {
			return "none";
		}
	}

	public String getAg370StuffStyle() {
		if (HiveAp.is370HiveAP(getDataSource().getModelType())) {
			return "";
		} else {
			return "none";
		}
	}

	
	public String getAg380StuffStyle() {
		if (HiveAp.is380HiveAP(getDataSource().getModelType())) {
			return "";
		} else {
			return "none";
		}
	}

	public String getAg120StuffStyle() {
		if (HiveAp.is120HiveAP(getDataSource().getModelType())) {
			return "";
		} else {
			return "none";
		}
	}

	public String getAg110StuffStyle() {
		if (HiveAp.is110HiveAP(getDataSource().getModelType())) {
			return "";
		} else {
			return "none";
		}
	}

	public String getEth1StuffStyle() {
		if (HiveAp.isEth1Available(getDataSource().getModelType())) {
			return "";
		} else {
			return "none";
		}
	}

	public String getWifi1StuffStyle() {
		if (HiveAp.isWifi1Available(getDataSource().getModelType())) {
			return "";
		} else {
			return "none";
		}
	}

	public String getACLStatus() {
		if (null != getDataSource() && getDataSource().isAccessControled()) {
			return "";
		} else {
			return "none";
		}
	}

	public String getManullySectionStyle() {
		if (null != getDataSource()
				&& getDataSource().getAclType() == HiveApAutoProvision.ACL_MANUAL_AP) {
			return "";
		} else {
			return "none";
		}
	}

	public String getSnSectionStyleStyle() {
		if (null != getDataSource()
				&& getDataSource().getAclType() == HiveApAutoProvision.ACL_IMPORT_SN) {
			return "";
		} else {
			return "none";
		}
	}

	public String getFullModeConfigStyle() {
		if (isFullMode()) {
			return "";
		} else {
			return "none";
		}
	}

	public String getBR100LikeStyle() {
		if (HiveAp.isBR100LikeHiveAP(getDataSource().getModelType())
				|| HiveAp.is330HiveAPAsRouter(getDataSource().getModelType(), getDataSource().getDeviceType())
				|| HiveAp.is350HiveAPAsRouter(getDataSource().getModelType(), getDataSource().getDeviceType())
			) {
			return "";
		} else {
			return "none";
		}
	}

	public String getNotBR100LikeStyle() {
		if (HiveAp.isBR100LikeHiveAP(getDataSource().getModelType())
				|| HiveAp.is330HiveAPAsRouter(getDataSource().getModelType(), getDataSource().getDeviceType())
				|| HiveAp.is350HiveAPAsRouter(getDataSource().getModelType(), getDataSource().getDeviceType())
			) {
			return "none";
		} else {
			return "";
		}
	}

	public String getBrWith4LanPortString() {
		if (HiveAp.isBR100LikeHiveAP(getDataSource().getModelType())) {
			return "";
		} else {
			return "none";
		}
	}

	// define for change CAPWAP pass phrase
	private boolean changePassPhrase;

	public boolean isChangePassPhrase() {
		return changePassPhrase;
	}

	public void setChangePassPhrase(boolean changePassPhrase) {
		this.changePassPhrase = changePassPhrase;
	}

	public String getPassPhraseDisabled() {
		if (changePassPhrase) {
			return "false";
		} else {
			return "true";
		}
	}
	
	public boolean isDsEnable(){
		return MgrUtil.isEnableDownloadServer();
	}

	public String getAllowedVlanTitle() {
		return MgrUtil.getUserMessage("hiveAp.if.allowedVlan.note");
	}

	/* Enum values */
	public EnumItem[] getApModel() {
		return NmsUtil.filterHiveAPModel(HiveAp.HIVEAP_MODEL_NO_VPN, this.isEasyMode());
	}

	public EnumItem[] getApModelAp() {
		return HiveAp.HIVEAP_MODEL_ONLY_HIVEAP_TYPE;
	}

	public EnumItem[] getApModelBranchRouter() {
		return HiveAp.HIVEAP_MODEL_TYPE_BRANCH_ROUTER;
	}

	public EnumItem[] getApModelVpnGateway() {
		return HiveAp.HIVEAP_MODEL_TYPE_VPN_GATEWAY;
	}

	public List<Entry<Integer, String>> getCountryCodeValues() {
		return CountryCode.getCountryCodeList();
	}

	public EnumItem[] getEnumAdminStateType() {
		return AhInterface.ADMIN_STATE_TYPE;
	}

	public EnumItem[] getEnumWifiOperationMode() {
		boolean uploadImage = this.getDataSource().isUploadImage();
		String softver = this.getDataSource().getImageVersion();
		if(uploadImage && softver != null && !"".equals(softver) && NmsUtil.compareSoftwareVersion("4.0.1.0", softver) > 0){
			return MgrUtil.enumItems("enum.interface.operationMode.", new int[] {
					AhInterface.OPERATION_MODE_ACCESS,
					AhInterface.OPERATION_MODE_BACKHAUL });
		}else if(uploadImage && softver != null && !"".equals(softver) && NmsUtil.compareSoftwareVersion("5.1.2.0", softver) > 0){
			return MgrUtil.enumItems("enum.interface.operationMode.", new int[] {
					AhInterface.OPERATION_MODE_ACCESS,
					AhInterface.OPERATION_MODE_BACKHAUL,
					AhInterface.OPERATION_MODE_DUAL});
		}else{
		    return MgrUtil.enumItems("enum.interface.operationMode.",
						new int[] { AhInterface.OPERATION_MODE_ACCESS,
								AhInterface.OPERATION_MODE_BACKHAUL,
								AhInterface.OPERATION_MODE_DUAL,
								AhInterface.OPERATION_MODE_SENSOR });
		}
	}

	public EnumItem[] getEnumEthOperationMode() {
		return MgrUtil.enumItems("enum.interface.eth.operationMode.",
				new int[] { AhInterface.OPERATION_MODE_ACCESS,
						AhInterface.OPERATION_MODE_BACKHAUL,
						AhInterface.OPERATION_MODE_BRIDGE });
	}

	public EnumItem[] getEnumRedOperationMode() {
//		return MgrUtil.enumItems("enum.interface.eth.operationMode.",
//				new int[] { AhInterface.OPERATION_MODE_ACCESS,
//						AhInterface.OPERATION_MODE_BACKHAUL,
//						AhInterface.OPERATION_MODE_BRIDGE });
		return MgrUtil.enumItems("enum.interface.eth.operationMode.",
				new int[] {AhInterface.OPERATION_MODE_BACKHAUL});
	}

	public EnumItem[] getAclType1() {
		return new EnumItem[] { new EnumItem(HiveApAutoProvision.ACL_MANUAL_AP,
				getText("hiveAp.autoProvisioning.aclType.manual")) };
	}

	public EnumItem[] getAclType2() {
		return new EnumItem[] { new EnumItem(HiveApAutoProvision.ACL_IMPORT_SN,
				getText("hiveAp.autoProvisioning.aclType.sn")) };
	}

	public EnumItem[] getEnumDuplexType() {
		return AhInterface.ETH_DUPLEX_TYPE;
	}

	public EnumItem[] getEnumSpeedType() {
		return AhInterface.ETH_SPEED_TYPE;
	}

	public EnumItem[] getEnumPseType() {
		//Fix bug 27293, BR200_LTE_VZ not support the 802.3at
		if(getDataSource().getModelType() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ){
			return AhInterface.ETH_PSE_WITHOUT_AT_TYPE_ONLY_TYPE;
		}
		
		return AhInterface.ETH_PSE_TYPE_ONLY_TYPE;
	}

	public EnumItem[] getEnumBindInterface() {
		return AhInterface.ETH_BIND_IF;
	}

	public EnumItem[] getEnumBindRole() {
		return AhInterface.ETH_BIND_ROLE;
	}

	public EnumItem[] getWifi0Channel() {
		if (null == wifi0ChannelList) {
			wifi0ChannelList = CountryCode.getChannelList_2_4GHz(
					getDataSource().getCountryCode(),
					RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20);
		}
		return MgrUtil.enumItems("enum.interface.channel.", wifi0ChannelList);
	}

	public EnumItem[] getWifi1Channel() {
		if (null == wifi1ChannelList) {
			wifi1ChannelList = CountryCode.getChannelList_5GHz(getDataSource()
					.getCountryCode(),
					RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20, false, false,getDataSource().getModelType());
		}
		return MgrUtil.enumItems("enum.interface.channel.", wifi1ChannelList);
	}

	public EnumItem[] getEnumPowerType() {
		return AhInterface.POWER_TYPE;
	}

	public int getCfgAdminUserLength() {
		return getAttributeLength("cfgAdminUser");
	}

	public int getCfgReadOnlyUserLength() {
		return getAttributeLength("cfgReadOnlyUser");
	}

	private void removeHomeHiveAPs(HiveApAutoProvision autoProvision) {
		try {
			if (isHMOnline()) {
				if (null != autoProvision
						&& autoProvision.isAutoProvision()
						&& autoProvision.isAccessControled()
						&& autoProvision.getAclType() == HiveApAutoProvision.ACL_IMPORT_SN
						&& autoProvision.getMacAddresses() != null
						&& !autoProvision.getMacAddresses().isEmpty()) {
					List<String> sn = autoProvision.getMacAddresses();
					String where = "hiveApModel = :s1 and manageStatus =:s2 and serialNumber in (:s3)";
					Object[] objects = new Object[] {
							autoProvision.getModelType(), HiveAp.STATUS_NEW, sn };
					List<Long> ids = (List<Long>) QueryUtil.executeQuery("select id from "
							+ HiveAp.class.getSimpleName(), null,
							new FilterParams(where, objects), BoMgmt
									.getDomainMgmt().getHomeDomain().getId());
					log.info("removeHomeHiveAPs", "HiveAPs" + ids
							+ " in home match this auto provision config:"
							+ autoProvision.getProvisioningName()
							+ ", domainId:" + domainId);
					if (!ids.isEmpty()) {
						BoMgmt.getMapMgmt().removeHiveAps(ids);
					}
				} else {
					log.info("removeHomeHiveAPs",
							"no HiveAPs in home match this auto provision config:"
									+ autoProvision.getProvisioningName()
									+ ", domainId:" + domainId);
				}
			}
		} catch (Exception e) {
			log.error("removeHomeHiveAPs", "error.", e);
		}
	}

	private void saveSn(String scanSerialNumbers) throws Exception {
		if (null != scanSerialNumbers) {
			List<HiveApSerialNumber> bos = new ArrayList<HiveApSerialNumber>();
			String[] serials = scanSerialNumbers.split("\r\n");
			scope:
			for (String serial : serials) {
				if (null != serial && serial.length() > 0) {
					// check format
					if (serial.length() != 14) {
						addActionError(getText(
								"error.provision.serialNumber.format",
								new String[]{serial}));
						continue;
					}
					// check database exist
					if (QueryUtil.findBoByAttribute(HiveApSerialNumber.class,
							"lower(serialNumber)", serial.toLowerCase()) != null) {
						addActionError(getText(
								"error.provision.serialNumber.exist",
								new String[]{serial}));
						continue;
					}
					// check duplex
					for (HiveApSerialNumber s : bos) {
						if (serial.equalsIgnoreCase(s.getSerialNumber())) {
							addActionError(getText(
									"error.provision.serialNumber.repeat",
									new String[]{serial}));
							continue scope;
						}
					}
					HiveApSerialNumber bo = new HiveApSerialNumber();
					bo.setSerialNumber(serial);
					bo.setOwner(getDomain());
					bos.add(bo);
				}
			}
			if (!bos.isEmpty()) {
				QueryUtil.bulkCreateBos(bos);
				addActionMessage(bos.size()
						+ " serial numbers were created successfully.");
			}
		}
	}

	private void removeSerialNumbers(Set<String> sns) throws Exception {
		int count = QueryUtil.bulkRemoveBos(HiveApSerialNumber.class,
				new FilterParams("serialNumber", sns), domainId);
		// remove from auto provisioning bo
		List<HiveApAutoProvision> list = QueryUtil.executeQuery(HiveApAutoProvision.class, null,
				new FilterParams("aclType", HiveApAutoProvision.ACL_IMPORT_SN),
				domainId, this);
		if (!list.isEmpty()) {
			List<HiveApAutoProvision> autos = new ArrayList<HiveApAutoProvision>();

			for (HiveApAutoProvision config : list) {
				if (null != config.getMacAddresses()) {
					config.getMacAddresses().removeAll(sns);
					autos.add(config);
				}
			}

			if (!autos.isEmpty()) {
				Collection<HmBo> coll = QueryUtil.bulkUpdateBos(autos);

				// reset the version value
				for (HmBo object : coll) {
					HiveApAutoProvision config = (HiveApAutoProvision) object;
					if (null != getDataSource()
							&& config.getModelType() == getDataSource()
									.getModelType()) {
						getDataSource().setMacAddresses(
								config.getMacAddresses());
						getDataSource().setVersion(config.getVersion());
						break;
					}
				}
			}
		}
		addActionMessage(MgrUtil.getUserMessage(OBJECTS_REMOVED, count + ""));
	}

	private void removeIpSubNetworks(Set<String> ips) throws Exception {
		int count = QueryUtil.bulkRemoveBos(DeviceIPSubNetwork.class,
				new FilterParams("ipSubNetwork", ips), domainId);
		// remove from auto provisioning bo
		List<HiveApAutoProvision> list = QueryUtil.executeQuery(HiveApAutoProvision.class, null, null,
				domainId, this);
		if (!list.isEmpty()) {
			List<HiveApAutoProvision> autos = new ArrayList<HiveApAutoProvision>();

			for (HiveApAutoProvision config : list) {
				if (null != config.getIpSubNetworks()) {
					config.getIpSubNetworks().removeAll(ips);
					autos.add(config);
				}
			}

			if (!autos.isEmpty()) {
				Collection<HmBo> coll = QueryUtil.bulkUpdateBos(autos);

				// reset the version value
				for (HmBo object : coll) {
					HiveApAutoProvision config = (HiveApAutoProvision) object;
					if (null != getDataSource()
							&& config.getModelType() == getDataSource()
									.getModelType()
							&& config.getDeviceType() == getDataSource()
							.getDeviceType()) {
						getDataSource().setIpSubNetworks(
								config.getIpSubNetworks());
						getDataSource().setVersion(config.getVersion());
						break;
					}
				}
			}
		}
		addActionMessage(MgrUtil.getUserMessage(OBJECTS_REMOVED, count + ""));
	}

	private JSONObject getJsonSerialNumbers() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		List<CheckItem> serialNumbers = getBoCheckItems("serialNumber",
				HiveApSerialNumber.class, null);
		Set<String> set = new HashSet<String>();
		if (null != serialNumbers) {
			for (CheckItem item : serialNumbers) {
				if (item.getId() > 0) {
					set.add(item.getValue());
				}
			}
		}
		JSONArray jsonArray = wrapJsonArray(set);
		jsonObject.put("obj", jsonArray);
		return jsonObject;
	}

	private JSONObject getJsonIpSubNetworks() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		List<CheckItem> ipSubNetworksTmp = getBoCheckItems("ipSubNetwork",
				DeviceIPSubNetwork.class, null);
		Set<String> set = new HashSet<String>();
		if (null != ipSubNetworksTmp) {
			for (CheckItem item : ipSubNetworksTmp) {
				if (item.getId() > 0) {
					set.add(item.getValue());
				}
			}
		}
		JSONArray jsonArray = wrapJsonArray(set);
		jsonObject.put("obj", jsonArray);
		return jsonObject;
	}

	private JSONObject removeJsonSerialNumbers(Set<String> sns)
			throws Exception {
		if (null != sns && sns.size() > 0) {
			// remove first
			removeSerialNumbers(sns);
		}
		// get remain second
		JSONObject jsonObject = getJsonSerialNumbers();
		if (null != sns && !sns.isEmpty()) {
			jsonObject.put("info", MgrUtil.getUserMessage(
					"info.objectsRemoved", String.valueOf(sns.size())));
		} else {
			jsonObject.put("info", "Remove Serial Number error.");
		}
		return jsonObject;
	}

	private JSONObject addJsonIpSubNetworks(Set<String> ips)
			throws Exception {
		int createdCount = 0;
		List<String> dbExistedIps = new ArrayList<String>();
		if (null != ips && ips.size() > 0) {
			List<DeviceIPSubNetwork> deviceIPSubNetworkLstExists = QueryUtil.executeQuery(DeviceIPSubNetwork.class, null,
						new FilterParams("ipSubNetwork", ips), getDomainId());
			List<String> ipExists = new ArrayList<String>();
			for (DeviceIPSubNetwork deviceIPSubNetwork : deviceIPSubNetworkLstExists) {
				ipExists.add(deviceIPSubNetwork.getIpSubNetwork());
			}
			// create first
			List<DeviceIPSubNetwork> deviceIPSubNetworkLst = new ArrayList<DeviceIPSubNetwork>();
			for (String str : ips) {
				if (str == null || "".equals(str.trim())) continue;
				if (ipExists.contains(str)) {
					dbExistedIps.add(str);
					continue;
				}
				DeviceIPSubNetwork deviceIPSubNetworkTmp = new DeviceIPSubNetwork();
				deviceIPSubNetworkTmp.setIpSubNetwork(str);
				deviceIPSubNetworkTmp.setOwner(getDomain());
				deviceIPSubNetworkLst.add(deviceIPSubNetworkTmp);
				createdCount++;
			}
			QueryUtil.bulkUpdateBos(deviceIPSubNetworkLst);
		}
		String dbExistStr = "";
		if (dbExistedIps.size() > 0) {
			for (String ip : dbExistedIps) {
				if ("".equals(dbExistStr)) {
					dbExistStr += ip;
				} else {
					dbExistStr += "," + ip;
				}
			}
		}
		if (!"".equals(dbExistStr)) {
			dbExistStr += " has existed in database.";
		}
		// get remain second
		JSONObject jsonObject = this.getJsonIpSubNetworks();
		if (null != ips && !ips.isEmpty()) {
			jsonObject.put("info", MgrUtil.getUserMessage(
					"info.objectsCreated", String.valueOf(createdCount)) + "<br><br>" + dbExistStr);
		} else {
			jsonObject.put("info", "Create IP SubNetworks error.");
		}
		return jsonObject;
	}

	private JSONObject removeJsonIpSubNetworks(Set<String> ips)
			throws Exception {
		if (null != ips && ips.size() > 0) {
			// remove first
			removeIpSubNetworks(ips);
		}
		// get remain second
		JSONObject jsonObject = this.getJsonIpSubNetworks();
		if (null != ips && !ips.isEmpty()) {
			jsonObject.put("info", MgrUtil.getUserMessage(
					"info.objectsRemoved", String.valueOf(ips.size())));
		} else {
			jsonObject.put("info", "Remove IP SubNetworks error.");
		}
		return jsonObject;
	}

	private JSONArray wrapJsonArray(Set<String> set) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for (String bssid : set) {
			JSONObject obj = new JSONObject();
			obj.put("key", bssid);
			obj.put("value", bssid);
			jsonArray.put(obj);
		}
		if (jsonArray.length() == 0) {
			JSONObject obj = new JSONObject();
			obj.put("key", "-1");
			obj.put("value", MgrUtil
					.getUserMessage("config.optionsTransfer.none"));
			jsonArray.put(obj);
		}
		return jsonArray;
	}

	private Collection<JSONObject> getRadioProfiles(String radioMode)
			throws Exception {
		Collection<JSONObject> profiles = new ArrayList<JSONObject>();
		List<CheckItem> profList = new ArrayList<CheckItem>();

		if ("bg".equals(radioMode)) {
			profList = getRadioProfile(new Short[] { RadioProfile.RADIO_PROFILE_MODE_BG });
		} else if ("a".equals(radioMode)) {
			profList = getRadioProfile(new Short[] { RadioProfile.RADIO_PROFILE_MODE_A });
		} else if ("ng".equals(radioMode)) {
			profList = getRadioProfile(new Short[] {
					RadioProfile.RADIO_PROFILE_MODE_BG,
					RadioProfile.RADIO_PROFILE_MODE_NG });
		} else if ("na".equals(radioMode)) {
			profList = getRadioProfile(new Short[] {
					RadioProfile.RADIO_PROFILE_MODE_A,
					RadioProfile.RADIO_PROFILE_MODE_NA });
		} else if ("abgn".equals(radioMode)) {
			profList = getRadioProfile(new Short[] {
					RadioProfile.RADIO_PROFILE_MODE_A,
					RadioProfile.RADIO_PROFILE_MODE_NA,
					RadioProfile.RADIO_PROFILE_MODE_BG,
					RadioProfile.RADIO_PROFILE_MODE_NG });
		} else if ("ac".equals(radioMode)){
			profList = getRadioProfile(new Short[] {
					RadioProfile.RADIO_PROFILE_MODE_A,
					RadioProfile.RADIO_PROFILE_MODE_NA,
					RadioProfile.RADIO_PROFILE_MODE_AC});
		}
		JSONObject attribute;
		for (CheckItem prof : profList) {
			attribute = new JSONObject();
			attribute.put("id", prof.getId());
			attribute.put("v", prof.getValue());
			profiles.add(attribute);
		}
		return profiles;
	}

	private JSONObject getAg20RadioProfiles(int countryCode,
			short wifi0OperationMode, short wifi1OperationMode)
			throws Exception {
		JSONObject object = new JSONObject();
		RadioProfile bg = HmBeParaUtil.getDefaultRadioBGProfile();
		RadioProfile a = HmBeParaUtil.getDefaultRadioAProfile();
		Collection<JSONObject> wifi0Profiles = getRadioProfiles("bg");
		Collection<JSONObject> wifi1Profiles = getRadioProfiles("a");
		object.put("wifi0", wifi0Profiles);
		object.put("wifi1", wifi1Profiles);
		object.put("wifi0d", bg.getId());
		object.put("wifi1d", a.getId());
		object.put("wifi0c", getChannelJSONList(HiveAp.HIVEAP_MODEL_20,
				countryCode, bg, wifi0OperationMode));
		object.put("wifi1c", getChannelJSONList(HiveAp.HIVEAP_MODEL_20,
				countryCode, a, wifi1OperationMode));
		object.put("wifi0dl", MgrUtil.getEnumString("enum.radioProfileMode."
				+ RadioProfile.RADIO_PROFILE_MODE_BG));
		object.put("wifi1dl", MgrUtil.getEnumString("enum.radioProfileMode."
				+ RadioProfile.RADIO_PROFILE_MODE_A));
		object.put("wifi0label", getWifiInterfaceLabel(bg));
		object.put("wifi1label", getWifiInterfaceLabel(a));
		return object;
	}

	private JSONObject get11nDualRadioProfiles(int countryCode,
			short wifi0OperationMode, short wifi1OperationMode, short model)
			throws Exception {
		RadioProfile ng = HmBeParaUtil.getDefaultRadioNGProfile();
		RadioProfile na = HmBeParaUtil.getDefaultRadioNAProfile();
		JSONObject object = new JSONObject();
		Collection<JSONObject> wifi0Profiles = getRadioProfiles("ng");
		Collection<JSONObject> wifi1Profiles = getRadioProfiles("na");
		//for 11ac mode
		if(HiveAp.is11acHiveAP(model)){
			wifi1Profiles = getRadioProfiles("ac");
			na = QueryUtil.findBoByAttribute(RadioProfile.class, "radioName",
						BeParaModule.DEFAULT_RADIO_PROFILE_NAME_AC);
		}
		object.put("wifi0", wifi0Profiles);
		object.put("wifi1", wifi1Profiles);
		object.put("wifi0d", ng.getId());
		object.put("wifi1d", na.getId());
		object.put("wifi0c", getChannelJSONList(model,
				countryCode, ng, wifi0OperationMode));
		object.put("wifi1c", getChannelJSONList(model,
				countryCode, na, wifi1OperationMode));
		object.put("wifi0dl", MgrUtil.getEnumString("enum.radioProfileMode."
				+ RadioProfile.RADIO_PROFILE_MODE_NG));
		if(HiveAp.is11acHiveAP(model)){
			object.put("wifi1dl", MgrUtil.getEnumString("enum.radioProfileMode."
					+ RadioProfile.RADIO_PROFILE_MODE_AC));
		}else{
			object.put("wifi1dl", MgrUtil.getEnumString("enum.radioProfileMode."
					+ RadioProfile.RADIO_PROFILE_MODE_NA));
		}
		object.put("wifi0label", getWifiInterfaceLabel(ng));
		object.put("wifi1label", getWifiInterfaceLabel(na));
		return object;
	}

	private JSONObject get11nSingleRadioProfiles(int countryCode,
			short wifi0OperationMode) throws Exception {
		RadioProfile ng = HmBeParaUtil.getDefaultRadioNGProfile();
		JSONObject object = new JSONObject();
		Collection<JSONObject> wifi0Profiles = getRadioProfiles("abgn");
		object.put("wifi0", wifi0Profiles);
		object.put("wifi0d", ng.getId());
		object.put("wifi0c", getChannelJSONList(HiveAp.HIVEAP_MODEL_110,
				countryCode, ng, wifi0OperationMode));
		object.put("wifi0dl", MgrUtil.getEnumString("enum.radioProfileMode."
				+ RadioProfile.RADIO_PROFILE_MODE_NG));
		object.put("wifi0label", getWifiInterfaceLabel(ng));
		return object;
	}

	private JSONObject getRadioMode(short hiveApModel, Long profileId,
			int countryCode, short operationMode) throws Exception {
		JSONObject jsonObject = new JSONObject();
		if (null != profileId) {
			RadioProfile profile = findBoById(
					RadioProfile.class, profileId);
			if (null != profile) {
				jsonObject.put("m", profile.getRadioModeString());
				jsonObject.put("l", getWifiInterfaceLabel(profile));
				jsonObject.put("c", getChannelJSONList(hiveApModel,
						countryCode, profile, operationMode));
			}
		}
		return jsonObject;
	}

	private JSONObject requestChannels(short hiveApModel, Long wifi0ProfileId,
			Long wifi1ProfileId, int countryCode, short wifi0OperationMode,
			short wifi1OperationMode) throws Exception {
		JSONObject jsonObject = new JSONObject();
		if (null != wifi0ProfileId) {
			RadioProfile profile = findBoById(
					RadioProfile.class, wifi0ProfileId);
			if (null != profile) {
				jsonObject.put("wifi0Channel", getChannelJSONList(hiveApModel,
						countryCode, profile, wifi0OperationMode));
			}
		}
		if (null != wifi1ProfileId) {
			RadioProfile profile = findBoById(
					RadioProfile.class, wifi1ProfileId);
			if (null != profile) {
				jsonObject.put("wifi1Channel", getChannelJSONList(hiveApModel,
						countryCode, profile, wifi1OperationMode));
			}
		}
		return jsonObject;
	}

	private int[] getChannelList(short hiveApModel, int countryCode,
			RadioProfile radioProfile, short operationMode) {
		if (null != radioProfile) {
			short radioMode = radioProfile.getRadioMode();
			short channelWidth = radioProfile.getChannelWidth();
			boolean dfsEnabled = radioProfile.isEnableDfs();
			boolean turboEnable = radioProfile.isTurboMode();
			Boolean isOutdoor = AhConstantUtil.isTrueAll(Device.IS_OUTDOOR, hiveApModel);

			boolean isDfsChannel = dfsEnabled
					&& (operationMode == AhInterface.OPERATION_MODE_ACCESS
					||operationMode == AhInterface.OPERATION_MODE_WAN_ACCESS
							|| operationMode == AhInterface.OPERATION_MODE_DUAL);
			boolean isTurboChannel = turboEnable
					&& !HiveAp.is11nHiveAP(hiveApModel);
			switch (radioMode) {
			case RadioProfile.RADIO_PROFILE_MODE_A:
			case RadioProfile.RADIO_PROFILE_MODE_NA:
			case RadioProfile.RADIO_PROFILE_MODE_AC:
				return CountryCode.getChannelList_5GHz(countryCode,
						channelWidth, isDfsChannel, isTurboChannel,hiveApModel,isOutdoor);
			case RadioProfile.RADIO_PROFILE_MODE_BG:
			case RadioProfile.RADIO_PROFILE_MODE_NG:
				return CountryCode.getChannelList_2_4GHz(countryCode,
						channelWidth);
			}
		}
		return null;
	}

	private JSONArray getChannelJSONList(short hiveApModel, int countryCode,
			RadioProfile radioProfile, short operationMode)
			throws JSONException {
		int[] list = getChannelList(hiveApModel, countryCode, radioProfile,
				operationMode);
		if (null != list) {
			JSONArray array = new JSONArray();
			for (int i : list) {
				JSONObject object = new JSONObject();
				if (i == 0) {
					object.put("key", "0");
					object.put("value", "Auto");
				} else {
					object.put("key", i);
					object.put("value", i);
				}
				array.put(object);
			}
			return array;
		}
		return null;
	}

	private JSONArray getVersionJSONList(short hiveApModel)
			throws JSONException {
		JSONArray array = new JSONArray();
		List<TextItem> versions = HiveApAutoProvision
				.getImageVersions(hiveApModel);
		boolean isDsEnabled = isDsEnable();
		for (TextItem version : versions) {
			JSONObject object = new JSONObject();
			String imageName = null;
			if (!isDsEnabled) {
				HiveApImageInfo imageInfo = com.ah.be.config.image.ImageManager.getLatestImageName(hiveApModel, version.getValue());
				imageName = imageInfo == null ? null : imageInfo.getImageName();
			}
			object.put("key", version.getKey());
			object.put("value", version.getValue());
			object.put(
					"img",
					StringUtils.isEmpty(imageName) ? HiveApAutoProvision.HIVEOS_IMAGE_NOT_FOUND
							: imageName);
			array.put(object);
		}
		return array;
	}

	private String getWifiInterfaceLabel(RadioProfile radioProfile) {
		if (null != radioProfile) {
			short radioMode = radioProfile.getRadioMode();
			switch (radioMode) {
			case RadioProfile.RADIO_PROFILE_MODE_A:
			case RadioProfile.RADIO_PROFILE_MODE_NA:
			case RadioProfile.RADIO_PROFILE_MODE_AC:
				return MgrUtil.getUserMessage("hiveAp.if.5G");
			case RadioProfile.RADIO_PROFILE_MODE_BG:
			case RadioProfile.RADIO_PROFILE_MODE_NG:
				return MgrUtil.getUserMessage("hiveAp.if.24G");
			}
		}
		return "";
	}

	private JSONArray getAllSerialNumbers() throws JSONException {
		JSONArray array = new JSONArray();
		List<HiveApSerialNumber> list = QueryUtil.executeQuery(
				HiveApSerialNumber.class, null, null);
		for (HiveApSerialNumber hs : list) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(getText("hiveAp.autoProvisioning.search.col.sn"), hs
					.getSerialNumber());
			jsonObj.put(getText("hiveAp.autoProvisioning.search.col.vhm"), hs
					.getOwner().getDomainName());
			array.put(jsonObj);
		}
		return array;
	}

	private List<CheckItem> configTemplates;
	private List<CheckItem> wifi0RadioProfiles;
	private List<CheckItem> wifi1RadioProfiles;
	private int[] wifi0ChannelList;
	private int[] wifi1ChannelList;
	private List<CheckItem> topoMaps;
	private List<CheckItem> capwapIps;
	private OptionsTransfer hiveApOptions_20; // AG20 boxes;
	private OptionsTransfer hiveApOptions_28; // AG28 boxes;
	private OptionsTransfer hiveApOptions_340; // 340 boxes;
	private OptionsTransfer hiveApOptions_320; // 320 boxes;
	private OptionsTransfer hiveApOptions_380; // 380 boxes;
	private OptionsTransfer hiveApOptions_330; // 320 boxes;
	private OptionsTransfer hiveApOptions_350; // 350 boxes;
	private OptionsTransfer hiveApOptions_370; // 370 boxes;
	private OptionsTransfer hiveApOptions_390; // 390 boxes;
	private OptionsTransfer hiveApOptions_230; // 230 boxes;
	private OptionsTransfer hiveApOptions_120; // 120 boxes;
	private OptionsTransfer hiveApOptions_110; // 110 boxes;
	private OptionsTransfer hiveApOptions_vi; // VI boxes;
	private OptionsTransfer snOptions;
	private OptionsTransfer ipSubNetworkOptions;
	private List<String> hiveAps;
	private List<String> hiveAps_28;
	private List<String> hiveAps_340;
	private List<String> hiveAps_380;
	private List<String> hiveAps_320;
	private List<String> hiveAps_330;
	private List<String> hiveAps_350;
	private List<String> hiveAps_370;
	private List<String> hiveAps_390;
	private List<String> hiveAps_230;
	private List<String> hiveAps_vi;
	private List<String> hiveAps_120;
	private List<String> hiveAps_110;
	private List<String> serialNumbers;
	private List<String> ipSubNetworks;
	private String snString;
	private String ipSubNetworkString;
	private String scanSerialNumbers;
	private short apModelType;
	private int countryCode;
	private String radioType;
	private Long configTemplateId;
	private Long capwapIpId;
	private Long capwapBackupIpId;
	private String wifi0RadioModeLabel = MgrUtil
			.getEnumString("enum.radioProfileMode."
					+ RadioProfile.RADIO_PROFILE_MODE_BG);
	private String wifi1RadioModeLabel = MgrUtil
			.getEnumString("enum.radioProfileMode."
					+ RadioProfile.RADIO_PROFILE_MODE_A);
	private String wifi0Label = MgrUtil.getUserMessage("hiveAp.if.24G");
	private String wifi1Label = MgrUtil.getUserMessage("hiveAp.if.5G");
	private Long wifi0ProfileId;
	private Long wifi1ProfileId;
	private short wifi0OperationMode;
	private short wifi1OperationMode;
	private short operationMode;

	public void setOperationMode(short operationMode) {
		this.operationMode = operationMode;
	}

	public void setWifi0OperationMode(short wifi0OperationMode) {
		this.wifi0OperationMode = wifi0OperationMode;
	}

	public void setWifi1OperationMode(short wifi1OperationMode) {
		this.wifi1OperationMode = wifi1OperationMode;
	}

	public void setWifi0ProfileId(Long wifi0ProfileId) {
		this.wifi0ProfileId = wifi0ProfileId;
	}

	public void setWifi1ProfileId(Long wifi1ProfileId) {
		this.wifi1ProfileId = wifi1ProfileId;
	}

	public String getWifi0RadioModeLabel() {
		return wifi0RadioModeLabel;
	}

	public String getWifi1RadioModeLabel() {
		return wifi1RadioModeLabel;
	}

	public String getWifi0Label() {
		return wifi0Label;
	}

	public String getWifi1Label() {
		return wifi1Label;
	}

	public void setConfigTemplateId(Long configTemplateId) {
		this.configTemplateId = configTemplateId;
	}

	public void setCapwapIpId(Long capwapIpId) {
		this.capwapIpId = capwapIpId;
	}

	public Long getConfigTemplateId() {
		return configTemplateId;
	}

	public Long getCapwapIpId() {
		return capwapIpId;
	}

	public Long getCapwapBackupIpId() {
		return capwapBackupIpId;
	}

	public void setCapwapBackupIpId(Long capwapBackupIpId) {
		this.capwapBackupIpId = capwapBackupIpId;
	}

	public void setRadioType(String radioType) {
		this.radioType = radioType;
	}

	public String getRadioType() {
		return radioType;
	}

	public void setApModelType(short apModelType) {
		this.apModelType = apModelType;
	}

	public void setCountryCode(int countryCode) {
		this.countryCode = countryCode;
	}

	public void setSnString(String snString) {
		this.snString = snString;
	}

	public void setScanSerialNumbers(String scanSerialNumbers) {
		this.scanSerialNumbers = scanSerialNumbers;
	}

	public List<CheckItem> getConfigTemplates() {
		return configTemplates;
	}

	public List<CheckItem> getWifi0RadioProfiles() {
		return wifi0RadioProfiles;
	}

	public List<CheckItem> getWifi1RadioProfiles() {
		return wifi1RadioProfiles;
	}

	public List<CheckItem> getTopoMaps() {
		return topoMaps;
	}

	public List<CheckItem> getCapwapIps() {
		return capwapIps;
	}

	public OptionsTransfer getHiveApOptions_20() {
		return hiveApOptions_20;
	}

	public OptionsTransfer getHiveApOptions_28() {
		return hiveApOptions_28;
	}

	public OptionsTransfer getHiveApOptions_340() {
		return hiveApOptions_340;
	}

	public OptionsTransfer getHiveApOptions_320() {
		return hiveApOptions_320;
	}

	public OptionsTransfer getHiveApOptions_330() {
		return hiveApOptions_330;
	}

	public OptionsTransfer getHiveApOptions_350() {
		return hiveApOptions_350;
	}

	public OptionsTransfer getHiveApOptions_vi() {
		return hiveApOptions_vi;
	}

	public OptionsTransfer getHiveApOptions_380() {
		return hiveApOptions_380;
	}

	public OptionsTransfer getHiveApOptions_120() {
		return hiveApOptions_120;
	}

	public OptionsTransfer getHiveApOptions_110() {
		return hiveApOptions_110;
	}

	public OptionsTransfer getSnOptions() {
		return snOptions;
	}

	public List<String> getHiveAps() {
		return hiveAps;
	}

	public List<String> getHiveAps_28() {
		return hiveAps_28;
	}

	public List<String> getHiveAps_340() {
		return hiveAps_340;
	}

	public void setHiveAps_340(List<String> hiveAps_340) {
		this.hiveAps_340 = hiveAps_340;
	}

	public List<String> getHiveAps_380() {
		return hiveAps_380;
	}

	public void setHiveAps_380(List<String> hiveAps_380) {
		this.hiveAps_380 = hiveAps_380;
	}

	public List<String> getHiveAps_320() {
		return hiveAps_320;
	}

	public void setHiveAps_320(List<String> hiveAps_320) {
		this.hiveAps_320 = hiveAps_320;
	}

	public List<String> getHiveAps_330() {
		return hiveAps_330;
	}

	public void setHiveAps_330(List<String> hiveAps_330) {
		this.hiveAps_330 = hiveAps_330;
	}

	public List<String> getHiveAps_350() {
		return hiveAps_350;
	}

	public void setHiveAps_350(List<String> hiveAps_350) {
		this.hiveAps_350 = hiveAps_350;
	}

	public List<String> getHiveAps_vi() {
		return hiveAps_vi;
	}

	public void setHiveAps_vi(List<String> hiveAps_vi) {
		this.hiveAps_vi = hiveAps_vi;
	}

	public List<String> getHiveAps_120() {
		return hiveAps_120;
	}

	public void setHiveAps_120(List<String> hiveAps_120) {
		this.hiveAps_120 = hiveAps_120;
	}

	public List<String> getHiveAps_110() {
		return hiveAps_110;
	}

	public void setHiveAps_110(List<String> hiveAps_110) {
		this.hiveAps_110 = hiveAps_110;
	}

	public List<String> getSerialNumbers() {
		return serialNumbers;
	}

	public void setSerialNumbers(List<String> serialNumbers) {
		this.serialNumbers = serialNumbers;
	}

	public void setHiveAps(List<String> hiveAps) {
		this.hiveAps = hiveAps;
	}

	public void setHiveAps_28(List<String> hiveAps_28) {
		this.hiveAps_28 = hiveAps_28;
	}

	protected JSONArray jsonArray;
	protected JSONObject jsonObject;

	@Override
	public String getJSONString() {
		if (jsonArray == null) {
			log.debug("getJSONString", "JSON string: " + jsonObject.toString());
			return jsonObject.toString();
		} else {
			log.debug("getJSONString", "JSON string: " + jsonArray.toString());
			return jsonArray.toString();
		}
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_PROFILE_NAME = 1;
	public static final int COLUMN_AUTO_PROVISION = 2;
	public static final int COLUMN_MODEL_TYPE = 3;
	public static final int COLUMN_UPLOAD_IMAGE = 4;
	public static final int COLUMN_HIVEOS_VERSION = 5;
	public static final int COLUMN_HIVEOS_IMAGE = 6;
	public static final int COLUMN_UPLOAD_CONFIGURATION = 7;
	public static final int COLUMN_WLAN = 8;
	public static final int COLUMN_WIFI0_PROFILE = 9;
	public static final int COLUMN_WIFI1_PROFILE = 10;
	public static final int COLUMN_DEVICE_TYPE = 11;

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
		case COLUMN_PROFILE_NAME:
			code = "hiveAp.autoProvisioning.name";
			break;
		case COLUMN_AUTO_PROVISION:
			code = "hiveAp.autoProvisioning.title.flag";
			break;
		case COLUMN_MODEL_TYPE:
			code = "hiveAp.model";
			break;
		case COLUMN_DEVICE_TYPE:
			code = "hiveAp.device.type";
			break;
		case COLUMN_UPLOAD_IMAGE:
			code = "hiveAp.autoProvisioning.title.uploadImage";
			break;
		case COLUMN_HIVEOS_VERSION:
			code = "hiveAp.autoProvisioning.imageVersion.label";
			break;
		case COLUMN_HIVEOS_IMAGE:
			code = "hiveAp.autoProvisioning.imageName.label";
			break;
		case COLUMN_UPLOAD_CONFIGURATION:
			code = "hiveAp.autoProvisioning.title.uploadConfig";
			break;
		case COLUMN_WLAN:
			code = "hiveAp.template";
			break;
		case COLUMN_WIFI0_PROFILE:
			code = "hiveAp.autoProvisioning.title.defaultRadioProfile.0";
			break;
		case COLUMN_WIFI1_PROFILE:
			code = "hiveAp.autoProvisioning.title.defaultRadioProfile.1";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(11);
		columns.add(new HmTableColumn(COLUMN_PROFILE_NAME));
		columns.add(new HmTableColumn(COLUMN_AUTO_PROVISION));
		columns.add(new HmTableColumn(COLUMN_MODEL_TYPE));
		columns.add(new HmTableColumn(COLUMN_DEVICE_TYPE));
		columns.add(new HmTableColumn(COLUMN_UPLOAD_IMAGE));
		columns.add(new HmTableColumn(COLUMN_HIVEOS_VERSION));
		columns.add(new HmTableColumn(COLUMN_HIVEOS_IMAGE));
		columns.add(new HmTableColumn(COLUMN_UPLOAD_CONFIGURATION));
		if (isFullMode()) {// show WLAN only in full mode
			columns.add(new HmTableColumn(COLUMN_WLAN));
		}
		columns.add(new HmTableColumn(COLUMN_WIFI0_PROFILE));
		columns.add(new HmTableColumn(COLUMN_WIFI1_PROFILE));
		return columns;
	}

	public String getWirelessRoutingStyle() {
		if	(isFullMode()) {
			return "";
		} else {
			return "none";
		}
	}

	public EnumItem[] getEnumDeviceType() {
		if (isFullMode()) {
			return MgrUtil.enumItems("enum.hiveAp.deviceType.",
					new int[] { HiveAp.Device_TYPE_HIVEAP,
							HiveAp.Device_TYPE_BRANCH_ROUTER,
							HiveAp.Device_TYPE_VPN_GATEWAY,
							HiveAp.Device_TYPE_VPN_BR,
							HiveAp.Device_TYPE_SWITCH});
		} else {
			return MgrUtil.enumItems("enum.hiveAp.deviceType.",
					new int[] { HiveAp.Device_TYPE_HIVEAP});
		}
	}

	public EnumItem[] getEnumDeviceInterfaceRole() {
		return AhInterface.ETHX_DEVICE_INTERFACE_ROLE;
	}

	public String getIpSubNetworkString() {
		return ipSubNetworkString;
	}

	public void setIpSubNetworkString(String ipSubNetworkString) {
		this.ipSubNetworkString = ipSubNetworkString;
	}

	public OptionsTransfer getIpSubNetworkOptions() {
		return ipSubNetworkOptions;
	}

	public List<String> getIpSubNetworks() {
		return ipSubNetworks;
	}

	public void setIpSubNetworks(List<String> ipSubNetworks) {
		this.ipSubNetworks = ipSubNetworks;
	}

	public int getClassificationTag1Length() {
		return getAttributeLength("classificationTag1");
	}

	public int getClassificationTag2Length() {
		return getAttributeLength("classificationTag2");
	}

	public int getClassificationTag3Length() {
		return getAttributeLength("classificationTag3");
	}

	//used for br100 interface setting
	private short apDevicelType;

	private AutoProvisionDeviceInterface br100Eth0;
	private AutoProvisionDeviceInterface br100Eth1;
	private AutoProvisionDeviceInterface br100Eth2;
	private AutoProvisionDeviceInterface br100Eth3;
	private AutoProvisionDeviceInterface br100Eth4;
	private AutoProvisionDeviceInterface br100Usb;

	private void prepareBr100InterfaceSetting() {
		br100Eth0 = new AutoProvisionDeviceInterface(AhInterface.DEVICE_IF_TYPE_ETH0);
		br100Eth1 = new AutoProvisionDeviceInterface(AhInterface.DEVICE_IF_TYPE_ETH1);
		br100Eth2 = new AutoProvisionDeviceInterface(AhInterface.DEVICE_IF_TYPE_ETH2);
		br100Eth3 = new AutoProvisionDeviceInterface(AhInterface.DEVICE_IF_TYPE_ETH3);
		br100Eth4 = new AutoProvisionDeviceInterface(AhInterface.DEVICE_IF_TYPE_ETH4);
		br100Usb = new AutoProvisionDeviceInterface(AhInterface.DEVICE_IF_TYPE_USB);
		//set default values
		//br100Eth0.setInterfaceDownstreamBandwidth(AhInterface.ETH0_DEVICE_DOWNSTREAM_BANDWIDTH);
		br100Eth0.setInterfaceRole(AhInterface.ROLE_PRIMARY);
		//br100Usb.setInterfaceDownstreamBandwidth(AhInterface.USB_DEVICE_DOWNSTREAM_BANDWIDTH);
		br100Usb.setInterfaceRole(AhInterface.ROLE_BACKUP);
		if (getDataSource().getDeviceInterfaces() != null) {
			for (AutoProvisionDeviceInterface entry : getDataSource().getDeviceInterfaces()) {
				if (entry.getInterfacePort() == AhInterface.DEVICE_IF_TYPE_ETH0) {
					br100Eth0 = entry;
				} else if (entry.getInterfacePort() == AhInterface.DEVICE_IF_TYPE_ETH1) {
					br100Eth1 = entry;
				} else if (entry.getInterfacePort() == AhInterface.DEVICE_IF_TYPE_ETH2) {
					br100Eth2 = entry;
				} else if (entry.getInterfacePort() == AhInterface.DEVICE_IF_TYPE_ETH3) {
					br100Eth3 = entry;
				} else if (entry.getInterfacePort() == AhInterface.DEVICE_IF_TYPE_ETH4) {
					br100Eth4 = entry;
				} else if (entry.getInterfacePort() == AhInterface.DEVICE_IF_TYPE_USB) {
					br100Usb = entry;
				}
			}
		}

		if (AhInterface.ETH_PSE_PRIORITY_ETH1.equals(br100Eth1.getPsePriority())){
			setRadioPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH1);
		} else {
			setRadioPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH2);
		}
	}

	private void convertBr100InterfaceSetting() {
		if (getDataSource().getDeviceInterfaces() != null) {
			getDataSource().getDeviceInterfaces().clear();
		}

		if (AhInterface.ETH_PSE_PRIORITY_ETH1.equals(getRadioPsePriority())) {
			br100Eth1.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH1);
			br100Eth2.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH2);
		} else {
			br100Eth2.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH1);
			br100Eth1.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH2);
		}

		getDataSource().getDeviceInterfaces().add(br100Eth0);
		getDataSource().getDeviceInterfaces().add(br100Eth1);
		getDataSource().getDeviceInterfaces().add(br100Eth2);
		getDataSource().getDeviceInterfaces().add(br100Eth3);
		getDataSource().getDeviceInterfaces().add(br100Eth4);
		getDataSource().getDeviceInterfaces().add(br100Usb);
	}

	public short getApDevicelType() {
		return apDevicelType;
	}

	public void setApDevicelType(short apDevicelType) {
		this.apDevicelType = apDevicelType;
	}

	public AutoProvisionDeviceInterface getBr100Eth0() {
		return br100Eth0;
	}

	public void setBr100Eth0(AutoProvisionDeviceInterface br100Eth0) {
		this.br100Eth0 = br100Eth0;
	}

	public AutoProvisionDeviceInterface getBr100Eth1() {
		return br100Eth1;
	}

	public void setBr100Eth1(AutoProvisionDeviceInterface br100Eth1) {
		this.br100Eth1 = br100Eth1;
	}

	public AutoProvisionDeviceInterface getBr100Eth2() {
		return br100Eth2;
	}

	public void setBr100Eth2(AutoProvisionDeviceInterface br100Eth2) {
		this.br100Eth2 = br100Eth2;
	}

	public AutoProvisionDeviceInterface getBr100Eth3() {
		return br100Eth3;
	}

	public void setBr100Eth3(AutoProvisionDeviceInterface br100Eth3) {
		this.br100Eth3 = br100Eth3;
	}

	public AutoProvisionDeviceInterface getBr100Eth4() {
		return br100Eth4;
	}

	public void setBr100Eth4(AutoProvisionDeviceInterface br100Eth4) {
		this.br100Eth4 = br100Eth4;
	}

	public AutoProvisionDeviceInterface getBr100Usb() {
		return br100Usb;
	}

	public void setBr100Usb(AutoProvisionDeviceInterface br100Usb) {
		this.br100Usb = br100Usb;
	}

	public String getHiveApName() {
		return MgrUtil.getEnumString("enum.hiveAp.deviceType." + HiveAp.Device_TYPE_HIVEAP);
	}

	public String getBRName() {
		return MgrUtil.getEnumString("enum.hiveAp.deviceType." + HiveAp.Device_TYPE_BRANCH_ROUTER);
	}

	public String getCVGName() {
		return MgrUtil.getEnumString("enum.hiveAp.deviceType." + HiveAp.Device_TYPE_VPN_GATEWAY);
	}

	public String getSwitchName() {
		return MgrUtil.getEnumString("enum.hiveAp.deviceType." + HiveAp.Device_TYPE_SWITCH);
	}
	
	public String getEth_PSE_8023af(){
		return MgrUtil.getEnumString("enum.interface.eth.pse." + AhInterface.ETH_PSE_8023af);
	}
	
	public String getEth_PSE_8023af_EXTENDED(){
		return MgrUtil.getEnumString("enum.interface.eth.pse." + AhInterface.ETH_PSE_8023af_EXTENDED);
	}
	
	public String getEth_PSE_8023at(){
		return MgrUtil.getEnumString("enum.interface.eth.pse." + AhInterface.ETH_PSE_8023at);
	}
	
	public boolean getDisableName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	public int getNameLength() {
		return getAttributeLength("name");
	}

	private boolean checkIsNetworkPolicyProper4Device(HiveApAutoProvision autoProvision) {
		// only work in fullMode
		if (!isFullMode()) {
			return true;
		}
		/*boolean isWirelessRouterNp = autoProvision.getConfigTemplate().isBlnWirelessRouter();*/
//		boolean isWirelessRouterNp = autoProvision.getConfigTemplate().getConfigType().isTypeSupportOr(ConfigTemplateType.ROUTER|ConfigTemplateType.SWITCH);
//		boolean isDeviceHiveAp = true;
//		if (autoProvision.getDeviceType() != HiveAp.Device_TYPE_HIVEAP) {
//			isDeviceHiveAp = false;
//		}
//
//		// can not assign wireless only network policy to BR & CVG.
//		if (!isDeviceHiveAp && !isWirelessRouterNp) {
//			addActionError(MgrUtil.getUserMessage("error.config.hiveAp.wireless.only.for.hiveAp", autoProvision.getConfigTemplate().getConfigName()));
//			return false;
//		}
		if (autoProvision.isBranchRouter() &&
				!autoProvision.getConfigTemplate().getConfigType().isRouterContained()) {
			addActionError(MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
					new String[]{autoProvision.getConfigTemplate().getConfigName(), "routers", "routing"}));
			return false;
		}
		if (autoProvision.isSwitch() &&
				!autoProvision.getConfigTemplate().getConfigType().isSwitchContained()
				&& !autoProvision.getConfigTemplate().getConfigType().isBonjourOnly()) {
			addActionError(MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
					new String[]{autoProvision.getConfigTemplate().getConfigName(), "switches", "switching"}));
			return false;
		}
		if (autoProvision.getDeviceType() == HiveAp.Device_TYPE_HIVEAP &&
				!autoProvision.getConfigTemplate().getConfigType().isWirelessContained()
				&& !autoProvision.getConfigTemplate().getConfigType().isBonjourOnly()) {
			addActionError(MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
					new String[]{autoProvision.getConfigTemplate().getConfigName(), "APs", "wireless access"}));
			return false;
		}

		return true;
	}

	private void saveBrEthx2DataSource() {
		if (getDataSource() != null) {
			getDataSource().setBr100Eth0(br100Eth0);
			getDataSource().setBr100Eth1(br100Eth1);
			getDataSource().setBr100Eth2(br100Eth2);
			getDataSource().setBr100Eth3(br100Eth3);
			getDataSource().setBr100Eth4(br100Eth4);
			getDataSource().setBr100Usb(br100Usb);
		}
	}

	private void fetchBrEthxFromDataSource() {
		if (getDataSource() != null) {
			br100Eth0 = getDataSource().getBr100Eth0();
			br100Eth1 = getDataSource().getBr100Eth1();
			br100Eth2 = getDataSource().getBr100Eth2();
			br100Eth3 = getDataSource().getBr100Eth3();
			br100Eth4 = getDataSource().getBr100Eth4();
			br100Usb = getDataSource().getBr100Usb();
		}
	}

	private boolean blnNoChangeDeviceType = true;

	public boolean isBlnNoChangeDeviceType() {
		return blnNoChangeDeviceType;
	}

	public void setBlnNoChangeDeviceType(boolean blnNoChangeDeviceType) {
		this.blnNoChangeDeviceType = blnNoChangeDeviceType;
	}

	public String getProvision4RouterStyle() {
		if( this.getDataSource() != null
					&& this.getDataSource().getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
			return "";
		}

		return "none";
	}

	public String getUsbModemSettingShownStyle() {
		if( this.getDataSource() != null
					&& this.getDataSource().getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
			if ((this.getDataSource().getModelType() == HiveAp.HIVEAP_MODEL_BR200_WP || this.getDataSource().getModelType() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ)
					&& this.getBr100Usb() != null
					&& this.getBr100Usb().getInterfaceRole() == AhInterface.ROLE_PRIMARY) {
				return "none";
			}
			return "";
		}

		return "none";
	}

	public EnumItem[] getUsbConnectNeeded() {
		return new EnumItem[] { new EnumItem(HiveAp.USB_CONNECTION_MODEL_NEEDED,
				getText("hiveAp.brRouter.usb.connect.needed")) };
	}

	public EnumItem[] getUsbConnectAlways() {
		return new EnumItem[] { new EnumItem(HiveAp.USB_CONNECTION_MODEL_ALWAYS,
				getText("hiveAp.brRouter.usb.connect.always")) };
	}

	public EnumItem[] getEnumUsbRoleType() {
		return AhInterface.ROLE_TYPE;
	}

	private String radioPsePriority;

	public String getRadioPsePriority() {
		return radioPsePriority;
	}

	public void setRadioPsePriority(String radioPsePriority) {
		this.radioPsePriority = radioPsePriority;
	}

	public String getPseSettingsDisplayStyle() {
		if (this.getDataSource() != null) {
			if (DeviceProperties.isPSEPortSupport4CertainModel(this.getDataSource().getModelType())) {
				return "";
			}
		}

		return "none";
	}
	
	private boolean shouldLimitImageVersion(HiveApAutoProvision autoProvision) {
		if (autoProvision != null) {
			return autoProvision.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER
					&& autoProvision.getModelType() != HiveAp.HIVEAP_MODEL_SR24
					&& autoProvision.getModelType() != HiveAp.HIVEAP_MODEL_SR2124P;
		}
		return false;
	}
	private boolean checkRouterMinVersion() {
		boolean result = true;
		if (this.shouldLimitImageVersion(this.getDataSource())) {
			if (this.getDataSource().isUploadImage()) {
				if (NmsUtil.compareSoftwareVersion(this.getDataSource().getImageVersion(), this.getRouterSupportMinVersion()) >= 0) {
					result = true;
				} else {
					result = false;
				}
			} else {
				result = false;
			}
		}
		
		if (!result) {
			addActionError(getErrorMsgForRouterMinVersion());
		}
		return result;
	}
	
	private String getErrorMsgForRouterMinVersion() {
		return MgrUtil.getUserMessage("error.provision.router.min.version", new String[]{MgrUtil.getHiveOSDisplayVersion(getRouterSupportMinVersion())});
	}
	
	public String getRouterSupportMinVersion() {
		return "5.1.1.0";
	}
	
	// One-time password
	public String getOtpTrStyle() {
		if (null != getDataSource() && getDataSource().getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
			if(getDataSource().getModelType() == HiveAp.HIVEAP_MODEL_SR24
					|| getDataSource().getModelType() == HiveAp.HIVEAP_MODEL_SR2124P){
				return "none";
			}
			return "";
		} else {
			return "none";
		}
	}
	
	public String getInterfacePortOfUsbDisplayString() {
		if (this.getDataSource() != null
				&& this.getDataSource().getModelType() == HiveAp.HIVEAP_MODEL_BR100
				&& this.getDataSource().getDeviceType() == HiveAp.Device_TYPE_HIVEAP) {
			return "none";
		}
		
		return "";
	}
	
	public String getBRPortUSBModeDisplayString(){
		if(this.getDataSource() != null 
				&& this.getDataSource().getModelType() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ){
			return MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.port.cellularmodem");
		}else{
			return MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.usb");
		}
	}
	
	private boolean isProfileForSwitch(HiveApAutoProvision autoProvision) {
		if (autoProvision != null
				&& (autoProvision.getModelType() == HiveAp.HIVEAP_MODEL_SR24
					|| autoProvision.getModelType() == HiveAp.HIVEAP_MODEL_SR2124P
					|| autoProvision.getModelType() == HiveAp.HIVEAP_MODEL_SR2024P
					|| autoProvision.getModelType() == HiveAp.HIVEAP_MODEL_SR2148P
					|| autoProvision.getModelType() == HiveAp.HIVEAP_MODEL_SR48)
				) {
			return true;
		}
		return false;
	}
	
	public String getInterfaceSettingStyle() {
		if (this.getDataSource() != null
				&& isProfileForSwitch(this.getDataSource())) {
			return "none";
		}
		
		return "";
	}

	public String getCustomTag1() {
		return customTag1;
	}

	public void setCustomTag1(String customTag1) {
		this.customTag1 = customTag1;
	}

	public String getCustomTag2() {
		return customTag2;
	}

	public void setCustomTag2(String customTag2) {
		this.customTag2 = customTag2;
	}

	public String getCustomTag3() {
		return customTag3;
	}

	public void setCustomTag3(String customTag3) {
		this.customTag3 = customTag3;
	}

	public OptionsTransfer getHiveApOptions_370() {
		return hiveApOptions_370;
	}

	public void setHiveApOptions_370(OptionsTransfer hiveApOptions_370) {
		this.hiveApOptions_370 = hiveApOptions_370;
	}
	
	public OptionsTransfer getHiveApOptions_390() {
		return hiveApOptions_390;
	}

	public void setHiveApOptions_390(OptionsTransfer hiveApOptions_390) {
		this.hiveApOptions_390 = hiveApOptions_390;
	}
	
	public OptionsTransfer getHiveApOptions_230() {
		return hiveApOptions_230;
	}
	
	public void setHiveApOptions_230(OptionsTransfer hiveApOptions_230) {
		this.hiveApOptions_230 = hiveApOptions_230;
	}
	
}
