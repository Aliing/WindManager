package com.ah.be.config.hiveap.provision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeConfigUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.communication.event.BeOTPStatusEvent;
import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.be.config.hiveap.UpdateHiveAp;
import com.ah.be.config.hiveap.UpdateObject;
import com.ah.be.config.hiveap.UpdateObjectBuilder;
import com.ah.be.config.hiveap.UpdateObjectException;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.db.discovery.AhDiscoveryProcessor;
import com.ah.be.topo.BeTopoModuleParameters;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.OneTimePassword;
import com.ah.ui.actions.hiveap.HiveApAction;
import com.ah.ui.actions.hiveap.HiveApUpdateAction;
import com.ah.ui.actions.monitor.MapNodeAction;
import com.ah.util.HiveApUtils;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class ProvisionProcessor implements QueryBo {

	private static final Tracer log = new Tracer(ProvisionProcessor.class
			.getSimpleName());
	
	private Map<String, HiveApAutoProvision> deviceAPMapping = new HashMap<String, HiveApAutoProvision>();
	
	private static enum OtpCheckResult {
		SUCCESS, FAILED, NO_NEED, NO_OPT
	}

	public ProvisionProcessor() {

	}

	public static void beginProcessing(Long id, HiveApAutoProvision config) {
		if (null == id) {
			return;
		}

		if (null == config) {
			log
					.info("beginProcessing",
							"Auto provisioning config is not found, didn't do provision.");
			return;
		}

		if (!config.isAutoProvision()) {
			log.info("beginProcessing",
					"Auto provision is disabled, didn't do provision.");
			return;
		}
		
		HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class, id,
				new HiveApUpdateAction());
		
		//OTP check
		OtpCheckResult checkRes = checkOTP(hiveAp, config);
		if(OtpCheckResult.FAILED.equals(checkRes)){
			log.error("beginProcessing", "OTP check failed for Device:"
					+ hiveAp.getHostName() + ", stop doing auto provision.");
			return;
		}else if(OtpCheckResult.NO_OPT.equals(checkRes)){
			String message = MgrUtil.getUserMessage("hm.system.log.provision.processor.device.code.check.message",hiveAp.getHostName());
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
					HmSystemLog.FEATURE_HIVEAPS, message);
			sendOptRequest(hiveAp, BeOTPStatusEvent.OTP_MODE_QUERY,null);
			return;
		}
		try {
			QueryUtil.updateBos(OneTimePassword.class, 
					"hiveApAutoProvision = :s1", 
					"upper(macAddress) = :s2", 
					new Object[]{config,hiveAp.getMacAddress().toUpperCase()});
		} catch (Exception e1) {
			log.error("beginProcessing","update OneTimePassword HiveApAutoProvision error ",e1);
		}
		
		if (!config.isUploadImage() && !config.isUploadConfig()) {
			log
					.info(
							"beginProcessing",
							"Auto provision is enabled, but both upload configuration and image is disabled, didn't do provision.");
			return;
		}

		if (null == hiveAp) {
			log.error("beginProcessing", "Cannot find HiveAp by id:" + id
					+ " to do provision.");
			return;
		}

		if (null == hiveAp.getConfigTemplate()) {
			log.error("beginProcessing", "No config template for HiveAp:"
					+ hiveAp.getHostName() + ", stop doing auto provision.");
			return;
		}

		if (null == hiveAp.getWifi0RadioProfile()) {
			log.error("beginProcessing", "No wifi0 radio profile for HiveAp:"
					+ hiveAp.getHostName() + ", stop doing auto provision.");
			return;
		}

		if (null == hiveAp.getWifi1RadioProfile()) {
			log.error("beginProcessing", "No wifi1 radio profile for HiveAp:"
					+ hiveAp.getHostName() + ", stop doing auto provision.");
			return;
		}

		boolean uploadConfig = config.isUploadConfig();
		boolean uploadImage = config.isUploadImage();
		boolean isRebooting = config.isRebooting();
		String imageName = config.getImageName();
		String imageVer = config.getImageVersion();
		int countryCode = config.getCountryCode();

		try {
			process(hiveAp, isRebooting, uploadConfig, uploadImage, imageName,
					imageVer, countryCode, UpdateParameters.COMPLETE_SCRIPT);
		} catch (Exception e) {
			log.error("beginProcessing", "Process auto provisioning error", e);
		}
	}
	
	public static void doAutoProvisionForReset (HiveAp hiveAp) throws Exception {
		hiveAp = QueryUtil.findBoById(HiveAp.class, hiveAp.getId(), new HiveApAction());
		
		List<HiveApAutoProvision> provisionList = QueryUtil.executeQuery(HiveApAutoProvision.class, 
				null, new FilterParams("autoProvision", true), hiveAp.getOwner().getId(), new ConfigLazyQueryBo());
			
		HiveApAutoProvision provision = AhDiscoveryProcessor.getAutoProvisionByDevice(hiveAp, provisionList);
		if(provision == null){
			return;
		}

		BoMgmt.getHiveApMgmt().setAutoProvisioningConfig(hiveAp, provision);
		hiveAp.setConfigVer(hiveAp.getConfigVer() + 1);
		QueryUtil.updateBo(hiveAp);
		
		//change config version
		BeCliEvent cliEvent = new BeCliEvent();
		cliEvent.setAp(hiveAp);
		String[] cliArray = new String[]{AhCliFactory.configVerNum(hiveAp.getConfigVer() + 1), AhCliFactory.getSaveConfigCli()};
		cliEvent.setClis(cliArray);
		cliEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		try {
			cliEvent.buildPacket();
		} catch (Exception e) {
			log.error(e);
		}
		HmBeCommunicationUtil.sendRequest(cliEvent);
		
		beginProcessing(hiveAp.getId(), provision);
	}
	
	public static void doAutoProvision(String macAddress) throws Exception{
		HiveAp hiveAp = (HiveAp) QueryUtil.findBoByAttribute(HiveAp.class, "upper(macAddress)", macAddress.toUpperCase(), 
				new HiveApAction());
		if(hiveAp == null || hiveAp.getManageStatus() == HiveAp.STATUS_MANAGED){
			return;
		}
		
		List<HiveApAutoProvision> provisionList = QueryUtil.executeQuery(HiveApAutoProvision.class, 
				null, new FilterParams("autoProvision", true), hiveAp.getOwner().getId(), new ConfigLazyQueryBo());
			
		HiveApAutoProvision provision = AhDiscoveryProcessor.getAutoProvisionByDevice(hiveAp, provisionList);
		if(provision != null){
			BoMgmt.getHiveApMgmt().setAutoProvisioningConfig(hiveAp, provision);
			QueryUtil.updateBo(hiveAp);
			
			beginProcessing(hiveAp.getId(), provision);
		}
	}
	
	private static void sendOptRequest(HiveAp hiveAp, byte mode,String password){
		BeTopoModuleUtil.sendOtpEventQuery(hiveAp, mode,password);
	}

	public static void process(HiveAp hiveAp, boolean isRebooting,
			boolean uploadConfig, boolean uploadImage, String imageName,
			String imageVer, int countryCode, short scriptType) {
		log.info("process", "HiveAP:" + hiveAp.getHostName() + ", countryCode:"
				+ hiveAp.getCountryCode() + ", isReboot:" + isRebooting
				+ ", uploadConfig:" + uploadConfig + ", uploadImage:"
				+ uploadImage + ", imageName:" + imageName + ", imageVer:"
				+ imageVer + ", config countryCode:" + countryCode);
		UpdateObjectBuilder builder = HmBeConfigUtil.getUpdateObjectBuilder();
		UpdateObject imageUpdateObj = null;
		UpdateObject countryCodeObj = null;
		UpdateObject cwpUpdateObj = null;
		UpdateObject certUpdateObj = null;
		UpdateObject vpnUpdateObj = null;
		UpdateObject configUpdateObj = null;
		UpdateObject pskUpdateObj = null;
		UpdateObject idmUpdateObj = null;
		// For reboot feature after upload successfully.
		String rebootTime_im = null;
		String rebootTime_cfg = null;
		boolean continueLoad = true;
		if (uploadConfig && isRebooting) {
			rebootTime_cfg = BeTopoModuleParameters.DEFAULT_REBOOT_OFFSET;
		} else if (uploadImage && isRebooting) {
			rebootTime_im = BeTopoModuleParameters.DEFAULT_REBOOT_OFFSET;
		}
		boolean processedImage = false;
		if (uploadImage) {
			// check for the version number first;
			String hostname = hiveAp.getHostName();
			String currentVer = hiveAp.getSoftVer();
			boolean result = validate(hostname, currentVer, imageVer);
			if (result) {
				try {
					imageUpdateObj = builder.getImageUpdateObject(hiveAp,
							rebootTime_im, true, imageName, true, true, false,
							0, UpdateParameters.IMAGE_TIMEOUT_MAX, true, imageVer);
					processedImage = true;
					continueLoad = false;
				} catch (UpdateObjectException e) {
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
							HmSystemLog.FEATURE_HIVEAPS, e.getMessage());
					log.error("process",
							"error on setup image update object on provision",
							e);
				}
			}
		}
		if (uploadConfig) {
			try {
				// check country code update, when runtime code not equals
				// config code, do update.
				if (hiveAp.getCountryCode() != countryCode) {
					try{
						countryCodeObj = builder.getCountryCodeUpdateObject(hiveAp,
								continueLoad, countryCode, null);
						continueLoad = false;
					}catch(Exception e){
						log.error("getCountryCodeUpdateObject",
								"error on setup configurate update object on provision",
								e);
					}
					
				}
				cwpUpdateObj = builder.getCwpUpdateObject(hiveAp, false,
						continueLoad, true);
				certUpdateObj = builder.getCertUpdateObject(hiveAp, false,
						continueLoad, true);
				vpnUpdateObj = builder.getVpnUpdateObject(hiveAp, false,
						continueLoad, true);
				if(isIdmOrAuthProxy(hiveAp)){
					idmUpdateObj = builder.getCloadAuthUpdateObject(hiveAp, continueLoad, true);
				}

				boolean saveServerFiles = false;
				if (null != cwpUpdateObj || null != certUpdateObj
						|| null != vpnUpdateObj) {
					saveServerFiles = true;
				}
				if (processedImage || null != cwpUpdateObj
						|| null != certUpdateObj || null != vpnUpdateObj) {
					// while configuration has previous action, if action
					// failed, not to upload configuration.
					continueLoad = false;
				}

				// while do upload image also, then set the new config version
				// to hiveAP for generate configuration file used.
				// FIX bug :17326 we should distinguish softVer from imageVer
				// image version contains build id like 5.1.1.0716 but soft version just like 5.1.1.0
				if (processedImage) {
					imageVer = imageVer.substring(0, imageVer.lastIndexOf(".")) + ".0";
					hiveAp.setSoftVer(imageVer);
				}
				
				boolean enableDS = MgrUtil.isEnableDownloadServer();
				configUpdateObj = builder.getConfigUpdateObject(hiveAp,
						scriptType, null, false,
						saveServerFiles, continueLoad, true, enableDS);
				// check version for auto provision
				boolean passed = MapNodeAction.checkVersionSupported(hiveAp,
						"3.2.0.0");
				if (passed) { // generate user database
					pskUpdateObj = builder.getPskUpdateObject(hiveAp,
							scriptType, rebootTime_cfg,
							true, saveServerFiles, continueLoad, true, enableDS);

				}
			} catch (UpdateObjectException e) {
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
						HmSystemLog.FEATURE_HIVEAPS, e.getMessage());
				log
						.error(
								"process",
								"error on setup configurate update object on provision",
								e);
			}
		}
		List<UpdateObject> updateList = new ArrayList<UpdateObject>();
		if (null != imageUpdateObj) {
			updateList.add(imageUpdateObj);
		}
		if (null != countryCodeObj) {
			updateList.add(countryCodeObj);
		}
		if (null != cwpUpdateObj) {
			updateList.add(cwpUpdateObj);
		}
		if (null != certUpdateObj) {
			updateList.add(certUpdateObj);
		}
		if (null != vpnUpdateObj) {
			updateList.add(vpnUpdateObj);
		}
		if (null != idmUpdateObj) {
			updateList.add(idmUpdateObj);
		}
		if (null != configUpdateObj) {
			updateList.add(configUpdateObj);
		}
		if (null != pskUpdateObj) {
			updateList.add(pskUpdateObj);
		}
		if (!updateList.isEmpty()) {
			UpdateHiveAp upAp = new UpdateHiveAp();
			upAp.setHiveAp(hiveAp);
			upAp.setAutoProvision(true);
			upAp.setUpdateObjectList(updateList);
			upAp.setWithReboot(isRebooting);

			List<UpdateHiveAp> upAp_list = new ArrayList<UpdateHiveAp>();
			upAp_list.add(upAp);
			List<String[]> errors = HmBeConfigUtil.getUpdateManager()
					.addUpdateObjects(upAp_list);

			if (null != errors && !errors.isEmpty()) {
				String[] error = errors.get(0);
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
						HmSystemLog.FEATURE_HIVEAPS, error[1]);
			}
		}

	}

	private static boolean validate(String hostname, String currentVer,
			String configVer) {
		if (null == currentVer || "".equals(currentVer.trim())) {
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
					HmSystemLog.FEATURE_HIVEAPS, NmsUtil.getUserMessage(
							"error.provision.noCurrentVersion",
							new String[] { hostname }));
			return false;
		}
		if (null == configVer || "".equals(configVer.trim())) {
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
					HmSystemLog.FEATURE_HIVEAPS, NmsUtil.getUserMessage(
							"error.provision.noConfigVersion",
							new String[] { hostname }));
			return false;
		}
		if (currentVer.equals(configVer)) {
			log
					.error(
							"validate",
							"HiveAP "
									+ hostname
									+ " needn't upload image, because of the same version number ["
									+ configVer
									+ "] between current and config value.");
			return false;
		}
		return true;
	}
	
	private static OtpCheckResult checkOTP(HiveAp hiveAp, HiveApAutoProvision config){
		if(hiveAp == null || config == null){
			return OtpCheckResult.FAILED;
		}
		
		if(!config.isEnableOneTimePassword() || 
				config.getDeviceType() != HiveAp.Device_TYPE_BRANCH_ROUTER || 
				hiveAp.getDeviceType() != HiveAp.Device_TYPE_BRANCH_ROUTER || 
				NmsUtil.compareSoftwareVersion("5.0.4.0", hiveAp.getSoftVer()) > 0){
			return OtpCheckResult.NO_NEED;
		}
		
		String sqlStr = "select oneTimePassword from " + OneTimePassword.class.getSimpleName();
		List<?> optRes = QueryUtil.executeQuery(sqlStr, null, 
							new FilterParams("upper(macAddress)", hiveAp.getMacAddress().toUpperCase()), hiveAp.getOwner().getId());
		if(optRes == null || optRes.isEmpty()){
			return OtpCheckResult.NO_OPT;
		}
		
		return OtpCheckResult.SUCCESS;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if(bo instanceof HiveAp){
			HiveAp hiveAp = (HiveAp)bo;
			if(hiveAp.getOwner() != null){
				hiveAp.getOwner().getId();
			}
		}
		return null;
	}
	
	public synchronized Map<String, HiveApAutoProvision> getDeviceAPMapping(){
		return this.deviceAPMapping;
	}
	
	public static boolean isIdmOrAuthProxy(HiveAp hiveAp){
		if(hiveAp == null){
			return false;
		}
		
		Set<Long> idSet = new HashSet<Long>();
		idSet.add(hiveAp.getId());
		FilterParams filter = HiveApUtils.getIdmCertFilter(idSet);
		FilterParams filterRad =null;
		List<?> authList = null;
		List<?> rsList = QueryUtil.executeQuery("select id from " + HiveAp.class.getSimpleName()+" as ap", 
				null, filter);
		
		if(NmsUtil.isVhmEnableIdm(hiveAp.getOwner().getId()) && 
				hiveAp.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_IDM_PROXY)){
			filterRad = HiveApUtils.getRadiusServerApFilter(false, false, idSet);
			authList = QueryUtil.executeQuery("select id from " + HiveAp.class.getSimpleName()+" as ap", 
					null, filterRad);
		}
		return (rsList != null && !rsList.isEmpty()) || (authList != null && !authList.isEmpty());
	}

}