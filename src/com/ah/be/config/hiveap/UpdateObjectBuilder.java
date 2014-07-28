package com.ah.be.config.hiveap;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.hiveap.ImageManager;
import com.ah.be.os.BeOsLayerModule;
import com.ah.be.os.FileManager;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.DeviceInfo.DeviceOption;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApImageInfo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.VpnService;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.useraccess.ActiveDirectoryOrLdapInfo;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.CwpCertificate;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.config.CwpAction;
import com.ah.ui.actions.config.ImportTextFileAction;
import com.ah.ui.actions.hiveap.HiveApFileAction;
import com.ah.util.CountryCode;
import com.ah.util.HiveApUtils;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class UpdateObjectBuilder {

	private static final Tracer log = new Tracer(UpdateObjectBuilder.class);

	public UpdateObjectBuilder() {

	}

	/**
	 *
	 * @param hiveAp
	 *            -
	 * @param saveServerFiles
	 *            -
	 * @param continued
	 *            Whether executing this object while previous object executed
	 *            failed.
	 * @param isAuto
	 *            -
	 * @return -
	 * @throws UpdateObjectException
	 *             -
	 */
	public UpdateObject getCwpUpdateObject(HiveAp hiveAp,
			boolean saveServerFiles, boolean continued, boolean isAuto)
			throws UpdateObjectException {
		if (null != hiveAp) {
			String ip = hiveAp.getIpAddress();
			if (null == ip || "".equals(ip.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApNoIp",
						new String[] { hiveAp.getHostName() }));
			}
			Set<Cwp> cwp_set = getCwpList(hiveAp);
			String domainName = hiveAp.getOwner().getDomainName();
			Map<String, Set<String>> dir_files = new HashMap<String, Set<String>>();
			Map<String, Set<String>> ppsk_dir_files = new HashMap<String, Set<String>>();
			Map<Integer, String> index_keyFiles = new HashMap<Integer, String>();

			for (Cwp cwp : cwp_set) {
				getCwpConfiguratedParams(domainName, dir_files, ppsk_dir_files, index_keyFiles,
						cwp, hiveAp.getHostName());
			}

			// no dir file and key file, do nothing
			if (dir_files.isEmpty() && index_keyFiles.isEmpty() && ppsk_dir_files.isEmpty()) {
				return null;
			}

			//check all cwp directory size for one HiveAp, max size is 3M
			String cwpDir = AhDirTools.getCwpWebDir(domainName);
			long allSize=0;
			for(String directory : dir_files.keySet()){
				String cwpPath = cwpDir + directory;
				allSize +=this.getCwpDirectSize(cwpPath);
			}
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
				if(allSize > HiveApFileAction.CWP_RESOURCE_MAX_SIZE * 128 * 6){
					throw new UpdateObjectException(NmsUtil.getUserMessage(
							"error.file.upload.fail.cwp.directory.limitOnBR",
							new String[] { hiveAp.getHostName() }));
				}
			}else{
				if(allSize > HiveApFileAction.CWP_RESOURCE_MAX_SIZE * 1024 *3){
					throw new UpdateObjectException(NmsUtil.getUserMessage(
							"error.file.upload.fail.cwp.directory.limitOnHiveAp",
							new String[] { hiveAp.getHostName() }));
				}
			}

			allSize=0;
			for(String directory : ppsk_dir_files.keySet()){
				String cwpPath = cwpDir + directory;
				allSize +=this.getCwpDirectSize(cwpPath);
			}
			if(allSize > HiveApFileAction.CWP_RESOURCE_MAX_SIZE * 1024 *3){
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.file.upload.fail.ppsk.directory.limitOnHiveAp",
						new String[] { hiveAp.getHostName() }));
			}

			checkCwp4OldAp(hiveAp,cwpDir,dir_files);
			return UpdateObjectFactory.getCwpUpdateObject(hiveAp, dir_files, ppsk_dir_files,
					index_keyFiles, saveServerFiles, continued, isAuto);
		}
		return null;
	}

	private void checkCwp4OldAp(HiveAp hiveAp,String cwpDir,Map<String, Set<String>> dir_files){
		for(String directory : dir_files.keySet()){
			String cwpPath = cwpDir + directory;
			String newPath = cwpPath  + File.separator + "success.html";
			String desOldPath = cwpPath + File.separator + CwpAction.HTML_SUCCESS_OLD;
			String desNewPath = cwpPath + File.separator +CwpAction.HTML_SUCCESS_NEW;
			try{
				if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.0.1.0") < 0){
					if(FileManager.getInstance().existsFile(desOldPath)){
						if(FileManager.getInstance().existsFile(newPath)){
							FileManager.getInstance().deletefile(newPath);
						}
						FileManager.getInstance().createFile("", newPath);
						FileManager.getInstance().copyFile(desOldPath,newPath );
					}
				}else{
					if(FileManager.getInstance().existsFile(desNewPath)){
						if(FileManager.getInstance().existsFile(newPath)){
							FileManager.getInstance().deletefile(newPath);
						}
						FileManager.getInstance().createFile("", newPath);
						FileManager.getInstance().copyFile(desNewPath, newPath);
					}
				}
			}catch(Exception e){
				log.error("UpdateObjectBuilder", "Error when generated file success.html.", e);
			}
		}
	}

	private long getCwpDirectSize(String path){
		long rest = 0;

		if(path == null || "".equals(path)){
			return rest;
		}
		try{
			List<File> allFile = HmBeOsUtil.getFilesFromFolder(new File(path), false);
			if (allFile != null) {
				for (File dirc : allFile) {
					rest += dirc.length();
				}
			}
		}catch(Exception ex){
			rest = 0;
		}
		return rest;
	}

	private Set<Cwp> getCwpList(HiveAp hiveAp) {
		Set<Cwp> cwp_set = new HashSet<Cwp>();
		ConfigTemplate cfgTmp = hiveAp.getConfigTemplate();
		boolean isPpskServer = HiveApUtils.isPpskServer(hiveAp.getId());
		boolean existsRadio = hiveAp.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_RADIO_COUNTS) > 0;
		boolean supportPPSKCwp = NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "4.0.0.0") >= 0;
		if (null != cfgTmp) {
			for (ConfigTemplateSsid ctSsid : cfgTmp.getSsidInterfaces()
					.values()) {
				if (null == ctSsid.getSsidProfile()) {
					continue;
				}
				if(!existsRadio && isPpskServer && ctSsid.getSsidProfile().getAccessMode() == SsidProfile.ACCESS_MODE_PSK &&
						(ctSsid.getSsidProfile().isEnablePpskSelfReg() || ctSsid.getSsidProfile().getSsidSecurity().isBlnMacBindingEnable()) &&
						NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "5.1.1.0") >= 0){

				}else if(!existsRadio){
					continue;
				}

				Cwp cwp = ctSsid.getSsidProfile().getCwp();
				Cwp userPolicy = ctSsid.getSsidProfile().getUserPolicy();
				Cwp ppskECwp = ctSsid.getSsidProfile().getPpskECwp();
				Cwp wpaECwp = ctSsid.getSsidProfile().getWpaECwp();
				if (null != cwp) {
					cwp_set.add(cwp);
				}
				if (null != userPolicy) {
					cwp_set.add(userPolicy);
				}
				if (supportPPSKCwp && null != ppskECwp){
					cwp_set.add(ppskECwp);
				}
				if (null != wpaECwp){
					cwp_set.add(wpaECwp);
				}
			}
		}

		if(hiveAp.getPortGroup() != null && hiveAp.getPortGroup().getBasicProfiles() != null){
			for(PortBasicProfile pgProfile : hiveAp.getPortGroup().getBasicProfiles()){
				if(pgProfile.getAccessProfile() == null){
					continue;
				}
				Cwp cwplan = pgProfile.getAccessProfile().getCwp();
				if(cwplan != null){
					cwp_set.add(cwplan);
				}
			}
		}

		Cwp ethCwp = hiveAp.getEthCwpCwpProfile();
		if (null != ethCwp) {
			cwp_set.add(ethCwp);
		}

		//Some times CWP no need web director, e.g. ECWP and not show success page and not show failure page.
		Iterator<Cwp> cwpItem = cwp_set.iterator();
		while(cwpItem.hasNext()){
			Cwp cwp = cwpItem.next();
			if(!cwp.isValidWebDirector()){
				cwpItem.remove();
			}
		}

		return cwp_set;
	}

	private void getCwpConfiguratedParams(String domainName,
			Map<String, Set<String>> dir_files,
			Map<String, Set<String>> ppsk_dir_files,
			Map<Integer, String> index_keyFiles, Cwp cwp, String hostname)
			throws UpdateObjectException {
		String directory = cwp.getDirectoryName();
		/*-
		String webPage = cwp.getWebPageName();
		String resultPage = cwp.getResultPageName();
		if (null == directory || "".equals(directory.trim())) {
			throw new UpdateObjectException(NmsUtil
					.getUserMessage("error.hiveap.cwp.no.configured",
							new String[] { hostname }));
		}*/
		if (null == directory || "".equals(directory.trim())) {
			// no directory is allowed.
			return;
		}
		// shouldn't use AhDirTools.getCwpWebPageDir(), it will auto create the
		// directory!
		String realPath = AhDirTools.getCwpWebDir(domainName) + directory;
		if (!HmBeOsUtil.isFileExist(realPath)) {
			throw new UpdateObjectException(NmsUtil.getUserMessage(
					"error.hiveap.cwp.directory.notfound", new String[] {
							hostname, directory }));
		}
		/*-
		String webPagePath = realPath + webPage;
		if (!HmBeOsUtil.isFileExist(webPagePath)) {
			throw new UpdateObjectException(NmsUtil.getUserMessage(
					"error.hiveap.cwp.webpage.notfound", new String[] {
							hostname, webPage }));
		}
		String resultPagePath = realPath + resultPage;
		if (!HmBeOsUtil.isFileExist(resultPagePath)) {
			throw new UpdateObjectException(NmsUtil.getUserMessage(
					"error.hiveap.cwp.resultpage.notfound", new String[] {
							hostname, resultPage }));
		}*/
		// put all this directory files in.
		if (!dir_files.containsKey(directory)) {
			List<String> fileNames = null;
			try {
				fileNames = HmBeOsUtil.getFileAndSubdirectoryNames(realPath,
						BeOsLayerModule.ONLYFILE, false);
			} catch (Exception e) {
				DebugUtil.configDebugError("get cwp files under directory:"
						+ directory + " error.", e);
			}
			if (null != fileNames && fileNames.size() > 0) {
				Set<String> files = new HashSet<String>();
				for (String fileName : fileNames) {
					files.add(fileName);
				}
				dir_files.put(directory, files);
			}
		}
		if (cwp.isPpskServer()){
			if (!ppsk_dir_files.containsKey(directory)) {
				List<String> fileNames = null;
				try {
					fileNames = HmBeOsUtil.getFileAndSubdirectoryNames(realPath,
							BeOsLayerModule.ONLYFILE, false);
				} catch (Exception e) {
					DebugUtil.configDebugError("get cwp files under directory:"
							+ directory + " error.", e);
				}
				if (null != fileNames && fileNames.size() > 0) {
					Set<String> files = new HashSet<String>();
					for (String fileName : fileNames) {
						files.add(fileName);
					}
					ppsk_dir_files.put(directory, files);
				}
			}
		}

		if (cwp.isEnabledHttps()) {
			CwpCertificate cert = cwp.getCertificate();
			if (null == cert) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.cwp.nokey.configured",
						new String[] { hostname }));
			}
			int index = cert.getIndex();
			String keyFileName = cert.getCpskName();
			String keyRealPath = AhDirTools.getCwpServerKeyDir(domainName);
			String keyPath = keyRealPath + keyFileName;
			if (!HmBeOsUtil.isFileExist(keyPath)) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.cwp.key.notfound", new String[] {
								hostname, keyFileName }));
			} else {
				if (!index_keyFiles.containsKey(index)) {
					index_keyFiles.put(index, keyFileName);
				}
			}
		}
	}

	/**
	 *
	 * @param hiveAp
	 *            -
	 * @param saveServerFiles
	 *            -
	 * @param continued
	 *            Whether executing this object while previous object executed
	 *            failed.
	 * @param isAuto
	 *            -
	 * @return -
	 * @throws UpdateObjectException
	 *             -
	 */
	public UpdateObject getCertUpdateObject(HiveAp hiveAp,
			boolean saveServerFiles, boolean continued, boolean isAuto)
			throws UpdateObjectException {
		if (null != hiveAp && (null != hiveAp.getRadiusServerProfile() || null != hiveAp.getConfigTemplate().getRadiusServerProfile())) {
			String ip = hiveAp.getIpAddress();
			if (null == ip || "".equals(ip.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApNoIp",
						new String[] { hiveAp.getHostName() }));
			}
			RadiusOnHiveap rsProfile;
			if(hiveAp.isOverWriteRadiusServer()){
				if(hiveAp.getRadiusServerProfile() != null){
					rsProfile = hiveAp.getRadiusServerProfile();
				}else{
					rsProfile = hiveAp.getConfigTemplate().getRadiusServerProfile();
				}
			}else{
				rsProfile = hiveAp.getRadiusServerProfile();
			}
			if(rsProfile == null){
				return null;
			}
			// if its authentication is LEAP type,needn't check follow 3
			// following 2 items must exist.
			String caCertificate = rsProfile.getCaCertFile();
			String serverCert = rsProfile.getServerFile();
			// if not blank, need to check
			String serverKey = rsProfile.getKeyFile();

			Set<String> serverFiles = new HashSet<String>();
			Set<String> clientFiles = new HashSet<String>();

			if (rsProfile.getAuthType() != RadiusOnHiveap.RADIUS_AUTH_TYPE_LEAP) {
				if (null == caCertificate || "".equals(caCertificate.trim())) {
					throw new UpdateObjectException(NmsUtil.getUserMessage(
							"error.hiveap.radius.noca.configured",
							new String[] { hiveAp.getHostName() }));
				}
				if (null == serverCert || "".equals(serverCert.trim())) {
					throw new UpdateObjectException(NmsUtil.getUserMessage(
							"error.hiveap.radius.noservercert.configured",
							new String[] { hiveAp.getHostName() }));
				}
				// check whether the file exist;
				if (HmBeAdminUtil.IsCAFileExist(caCertificate, hiveAp
						.getOwner().getDomainName())) {
					serverFiles.add(caCertificate);
				} else {
					throw new UpdateObjectException(NmsUtil.getUserMessage(
							"error.hiveap.radius.ca.notfound", new String[] {
									hiveAp.getHostName(), caCertificate }));
				}

				if (HmBeAdminUtil.IsCAFileExist(serverCert, hiveAp.getOwner()
						.getDomainName())) {
					serverFiles.add(serverCert);
				} else {
					throw new UpdateObjectException(NmsUtil.getUserMessage(
							"error.hiveap.radius.servercert.notfound",
							new String[] { hiveAp.getHostName(), serverCert }));
				}

				if (null != serverKey && !("".equals(serverKey.trim()))) {
					// check whether the file exist;
					if (HmBeAdminUtil.IsCAFileExist(serverKey, hiveAp
							.getOwner().getDomainName())) {
						serverFiles.add(serverKey);
					} else {
						throw new UpdateObjectException(
								NmsUtil
										.getUserMessage(
												"error.hiveap.radius.serverkey.notfound",
												new String[] {
														hiveAp.getHostName(),
														serverKey }));
					}
				}
			}

			if (rsProfile.getDatabaseType() == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN
					|| rsProfile.getDatabaseType() == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN) {
				List<ActiveDirectoryOrLdapInfo> ldapList = rsProfile
						.getLdap();
				if (null != ldapList) {
					for (ActiveDirectoryOrLdapInfo ldap : ldapList) {
						ActiveDirectoryOrOpenLdap ldapInfo = ldap
								.getDirectoryOrLdap();
						// if Open LDAP is used, check
						String caCertificate_ldap = ldapInfo
								.getCaCertFileO();
						// if not blank, check
						String clientCert_ldap = ldapInfo.getClientFile();
						String clientKey = ldapInfo.getKeyFileO();

						if (null != caCertificate_ldap
								&& !("".equals(caCertificate_ldap.trim()))) {
							if (HmBeAdminUtil.IsCAFileExist(
									caCertificate_ldap, hiveAp.getOwner()
											.getDomainName())) {
								clientFiles.add(caCertificate_ldap);
							} else {
								throw new UpdateObjectException(
										NmsUtil
												.getUserMessage(
														"error.hiveap.radius.ca.notfound",
														new String[] {
																hiveAp
																		.getHostName(),
																caCertificate_ldap }));
							}
						}
						// check whether the file exist;
						if (null != clientCert_ldap
								&& !("".equals(clientCert_ldap.trim()))) {
							// check whether the file exist;
							if (HmBeAdminUtil.IsCAFileExist(
									clientCert_ldap, hiveAp.getOwner()
											.getDomainName())) {
								clientFiles.add(clientCert_ldap);
							} else {
								throw new UpdateObjectException(
										NmsUtil
												.getUserMessage(
														"error.hiveap.radius.ldapclientcert.notfound",
														new String[] {
																hiveAp
																		.getHostName(),
																clientCert_ldap }));
							}
						}
						if (null != clientKey
								&& !("".equals(clientKey.trim()))) {
							// check whether the file exist;
							if (HmBeAdminUtil.IsCAFileExist(clientKey,
									hiveAp.getOwner().getDomainName())) {
								clientFiles.add(clientKey);
							} else {
								throw new UpdateObjectException(
										NmsUtil
												.getUserMessage(
														"error.hiveap.radius.clientkey.notfound",
														new String[] {
																hiveAp
																		.getHostName(),
																clientKey }));
							}
						}
					}
				}
			}
			// no server file and client file, do nothing
			if (serverFiles.isEmpty() && clientFiles.isEmpty()) {
				return null;
			}
			return UpdateObjectFactory.getCertUpdateObject(hiveAp, serverFiles,
					clientFiles, saveServerFiles, continued, isAuto);
		}
		return null;
	}

	/**
	 *
	 * @param hiveAp
	 *            -
	 * @param saveServerFiles
	 *            -
	 * @param continued
	 *            Whether executing this object while previous object executed
	 *            failed.
	 * @param isAuto
	 *            -
	 * @return -
	 * @throws UpdateObjectException
	 *             -
	 */

	public UpdateObject getCloadAuthUpdateObject(HiveAp hiveAp, boolean continued, boolean isAuto)
			throws UpdateObjectException{
		if (null == hiveAp) {
			return null;
		}
		String ip = hiveAp.getIpAddress();
		if (null == ip || "".equals(ip.trim())) {
			throw new UpdateObjectException(NmsUtil.getUserMessage(
					"error.hiveApNoIp", new String[] { hiveAp.getHostName() }));
		}

		return UpdateObjectFactory.getCloudAuthUpdateObject(hiveAp, continued, isAuto);
	}

	/**
	 *
	 * @param hiveAp
	 *            -
	 * @param saveServerFiles
	 *            -
	 * @param continued
	 *            Whether executing this object while previous object executed
	 *            failed.
	 * @param isAuto
	 *            -
	 * @return -
	 * @throws UpdateObjectException
	 *             -
	 */
	public UpdateObject getVpnUpdateObject(HiveAp hiveAp,
			boolean saveServerFiles, boolean continued, boolean isAuto)
			throws UpdateObjectException {
		if (null == hiveAp || hiveAp.getVpnMark() == HiveAp.VPN_MARK_NONE) {
			return null;
		}
		String ip = hiveAp.getIpAddress();
		if (null == ip || "".equals(ip.trim())) {
			throw new UpdateObjectException(NmsUtil.getUserMessage(
					"error.hiveApNoIp", new String[] { hiveAp.getHostName() }));
		}
		VpnService vpnService;
		if(hiveAp.isVpnGateway()){
			String vpnSql = "select distinct vpn.id from "+VpnService.class.getSimpleName()+" as vpn join vpn.vpnGateWaysSetting as joined ";

			List<?> vpnIds = QueryUtil.executeQuery(vpnSql, null, new FilterParams("joined.hiveApId = :s1", new Object[]{hiveAp.getId()}));
			if(vpnIds == null || vpnIds.isEmpty()){
				return null;
			}
			Long vpnId = (Long)vpnIds.get(0);
			vpnService = QueryUtil.findBoById(VpnService.class, vpnId);
		}else{
			vpnService = hiveAp.getConfigTemplate().getVpnService();
		}
		if (null == vpnService) {
			return null;
		}
		String rootCa = vpnService.getRootCa();
		String serverCert = vpnService.getCertificate();
		String privateKey = vpnService.getPrivateKey();
		short authMethod = vpnService.getPhase1AuthMethod();
		short vpnMark = hiveAp.getVpnMark();
		if (vpnMark == HiveAp.VPN_MARK_SERVER) {
			// HiveAP as VPN Server
			if (null == rootCa || "".equals(rootCa.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.vpn.noca.configured",
						new String[] { hiveAp.getHostName() }));
			}
			if (null == serverCert || "".equals(serverCert.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.vpn.noservercert.configured",
						new String[] { hiveAp.getHostName() }));
			}
			if (null == privateKey || "".equals(privateKey.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.vpn.noserverkey.configured",
						new String[] { hiveAp.getHostName() }));
			}
			// check whether the file exist;
			if (!HmBeAdminUtil.IsCAFileExist(rootCa, hiveAp.getOwner()
					.getDomainName())) {
				throw new UpdateObjectException(NmsUtil
						.getUserMessage("error.hiveap.vpn.ca.notfound",
								new String[] { rootCa }));
			}
			if (!HmBeAdminUtil.IsCAFileExist(serverCert, hiveAp.getOwner()
					.getDomainName())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.vpn.servercert.notfound",
						new String[] { serverCert }));
			}
			if (!HmBeAdminUtil.IsCAFileExist(privateKey, hiveAp.getOwner()
					.getDomainName())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.vpn.serverkey.notfound",
						new String[] { privateKey }));
			}
			if (authMethod == VpnService.PHASE1_AUTH_METHOD_HYBRID) {
				// auth method is hybrid, just load cert and key on server
				return UpdateObjectFactory.getVpnUpdateObject(hiveAp, null,
						serverCert, privateKey, saveServerFiles, continued,
						isAuto);
			} else {
				// need load client CA and server cert and key on server
				return UpdateObjectFactory.getVpnUpdateObject(hiveAp, rootCa,
						serverCert, privateKey, saveServerFiles, continued,
						isAuto);
			}
		} else if (hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT) {
			// HiveAP as VPN Client
			if (null == rootCa || "".equals(rootCa.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.vpn.client.noca.configured",
						new String[] { hiveAp.getHostName() }));
			}
			if (null == serverCert || "".equals(serverCert.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.vpn.client.noservercert.configured",
						new String[] { hiveAp.getHostName() }));
			}
			if (null == privateKey || "".equals(privateKey.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.vpn.client.noserverkey.configured",
						new String[] { hiveAp.getHostName() }));
			}
			// check whether the file exist;
			if (!HmBeAdminUtil.IsCAFileExist(rootCa, hiveAp.getOwner()
					.getDomainName())) {
				throw new UpdateObjectException(NmsUtil
						.getUserMessage("error.hiveap.vpn.ca.notfound",
								new String[] { rootCa }));
			}
			if (!HmBeAdminUtil.IsCAFileExist(serverCert, hiveAp.getOwner()
					.getDomainName())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.vpn.servercert.notfound",
						new String[] { serverCert }));
			}
			if (!HmBeAdminUtil.IsCAFileExist(privateKey, hiveAp.getOwner()
					.getDomainName())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.vpn.serverkey.notfound",
						new String[] { privateKey }));
			}
			if (authMethod == VpnService.PHASE1_AUTH_METHOD_HYBRID) {
				// auth method is hybrid, just load CA on client
				return UpdateObjectFactory.getVpnUpdateObject(hiveAp, rootCa,
						null, null, saveServerFiles, continued, isAuto);
			} else {
				// need load server CA and client cert and key on client
				return UpdateObjectFactory.getVpnUpdateObject(hiveAp, rootCa,
						serverCert, privateKey, saveServerFiles, continued,
						isAuto);
			}
		} else {
			return null;
		}
	}

	/**
	 *
	 * @param hiveAp
	 *            -
	 * @param activateTime
	 *            -
	 * @param isRelativeTime
	 *            -
	 * @param imageName
	 *            -
	 * @param continued
	 *            Whether executing this object while previous object executed
	 *            failed.
	 * @param isAuto
	 *            -
	 * @param tftp
	 *            -
	 * @param limit -
	 * @param maxTimeout -
	 * @return -
	 * @throws UpdateObjectException
	 *             -
	 */
	public UpdateObject getImageUpdateObject(HiveAp hiveAp,
			String activateTime, boolean isRelativeTime, String imageName,
			boolean continued, boolean isAuto, boolean tftp, int limit,
			int maxTimeout, boolean byVer, String version) throws UpdateObjectException {
		if (null != hiveAp) {
			String ip = hiveAp.getIpAddress();
			// if download image from download server file size is unknown, so default 10M.
			int size = 25 * 1024 * 1024;
			if (null == ip || "".equals(ip.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApNoIp",
						new String[] { hiveAp.getHostName() }));
			}
			short level = UpdateParameters.LEVEL_IMAGE_YES;
			if(!MgrUtil.isEnableDownloadServer()){
				if (null == imageName || "".equals(imageName)) {
					throw new UpdateObjectException(NmsUtil.getUserMessage(
							"error.hiveap.image.notfound",
							new String[] { hiveAp.getHostName() }));
				}
				File imageFile = new File(AhDirTools
						.getImageDir(HmDomain.HOME_DOMAIN)
						+ imageName);
				if (!imageFile.exists()) {
					throw new UpdateObjectException(NmsUtil.getUserMessage(
							"error.hiveap.image.image.notfound", new String[] {
									imageName, hiveAp.getHostName() }));
				}
				// validate the image platform
				HiveApImageInfo hiveApImage = ImageManager.findImageByFileName(imageName);
				if (hiveApImage == null) {
					throw new UpdateObjectException(NmsUtil.getUserMessage(
							"error.hiveap.image.image.productname.notmatch", new String[] { imageName }));
				} else {
					boolean chkSucc = true;
					DeviceInfo dInfo = NmsUtil.getDeviceInfo(hiveAp.getHiveApModel());
					String startVer = dInfo.getStringValue(DeviceInfo.START_VERSION);
					List<DeviceOption> options = dInfo.getDeviceOptions(DeviceInfo.SPT_IMAGE_INTERNAL_NAME);

					//check image version must higher than platform start version.
					if(chkSucc){
						chkSucc = NmsUtil.compareSoftwareVersion(hiveApImage.getImageVersionNum(), startVer) >= 0;
					}
					//check image platform name.
					if(chkSucc){
						String internalName = hiveApImage.getProductName();
						chkSucc = false;
						for(DeviceOption option : options){
							if(internalName.equals(option.getValue())){
								chkSucc = true;
								break;
							}
						}
					}
					if (!chkSucc) {
						throw new UpdateObjectException(NmsUtil.getUserMessage(
								"error.hiveap.image.image.productname.notmatch", new String[] { imageName }));
					}
				}

				size = (int) imageFile.length();
				if (!hiveAp.isCVGAppliance() && !isImageMatchMac(AhDirTools
						.getImageDir(HmDomain.HOME_DOMAIN)
						+ imageName, hiveAp.getMacAddress())) {
					throw new UpdateObjectException(NmsUtil.getUserMessage(
							"error.hiveap.image.image.notmatch", new String[] {
									imageName, hiveAp.getMacAddress().substring(0, 6) }));
				}
				if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_141
						|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_121
						|| hiveAp.isSwitchProduct()) {
					level = checkImageVersion(
							AhDirTools.getImageDir(HmDomain.HOME_DOMAIN)
									+ imageName, hiveAp.getSoftVer(), hiveAp.getDisplayVerNoBuild(), hiveAp.getProductName(), hiveAp.getHardwareRevision());
					if (UpdateParameters.LEVEL_IMAGE_NO == level) {
                        if (hiveAp.isSwitchProduct()
                                && UpdateParameters.SWITCH_IMAGE_REVISION_02.equals(hiveAp.getHardwareRevision())) {
                            throw new UpdateObjectException(NmsUtil.getUserMessage(
                                    "error.hiveap.image.image.version.notmatch.pre61r3a",
                                    new String[] { imageName }));
                        }
						throw new UpdateObjectException(NmsUtil.getUserMessage(
								"error.hiveap.image.image.version.notmatch", new String[] {
										imageName }));
					}
				}
				if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200
						|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP
						|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ) {
					if (!checkSwitchChipVersion(
							AhDirTools.getImageDir(HmDomain.HOME_DOMAIN)
									+ imageName, hiveAp.getSwitchChipVersion())) {
						throw new UpdateObjectException(NmsUtil.getUserMessage(
								"error.hiveap.image.image.switchchipversion.notmatch",
								new String[] { imageName,
										String.valueOf(hiveAp.getSwitchChipVersion()) }));
					}
				}
			}
			return UpdateObjectFactory.getImageUpdateObject(hiveAp,
					activateTime, isRelativeTime, imageName, size, continued,
					isAuto, tftp, limit, maxTimeout, byVer, version, level);
		}
		return null;
	}

	public UpdateObject getRebootUpdateObject(HiveAp hiveAp, String offset_str, String date, String time)
			throws UpdateObjectException{
		if (null == hiveAp) {
			return null;
		}
		String ip = hiveAp.getIpAddress();
		if (null == ip || "".equals(ip.trim())) {
			throw new UpdateObjectException(NmsUtil.getUserMessage(
					"error.hiveApNoIp", new String[] { hiveAp.getHostName() }));
		}

		return UpdateObjectFactory.getRebootUpdateObject(hiveAp, offset_str, date, time);
	}

	private boolean checkSwitchChipVersion(String imagePath, int switchChipVersion) {
		StringBuilder cmds = new StringBuilder();
		if (NmsUtil.isHostedHMApplication()) {
			cmds.append(BeAdminCentOSTools.ahShellRoot + "/ahCheckSwitchChipVersion.sh ");
		} else {
			cmds.append(BeAdminCentOSTools.ahShellRoot + "/ah_imginfo_br200_new_switch_chip_check ");
		}

		cmds.append((switchChipVersion == 0 ? 0 : ("0x" + Integer.toHexString(switchChipVersion))) + " ");
		cmds.append(imagePath);
		String msg = BeAdminCentOSTools.getOutStreamExecCmd(cmds.toString());

		return "yes".equalsIgnoreCase(msg);
	}

	private short checkImageVersion(String imagePath, String softVer, String patchVer, String productName, String hardwareRevision) {
		short level = UpdateParameters.LEVEL_IMAGE_YES;
		StringBuilder cmds = new StringBuilder();
		if (NmsUtil.isHostedHMApplication()) {
			cmds.append(BeAdminCentOSTools.ahShellRoot + "/ahCheckImageVersion.sh ");
		} else {
			cmds.append(BeAdminCentOSTools.ahShellRoot + "/ah_imginfo_version_image_update ");
		}

		String hexVer = getHexVersion(softVer, patchVer);
		if (hexVer == null) return level;

		cmds.append(hexVer + " ");
		cmds.append(imagePath + " ");
		cmds.append(productName + " ");
		String revision = "01";
		try {
		    revision = String.format("%02d", Long.parseLong(hardwareRevision));
        }
        catch (Exception e) {
            log.error("The hardware revision : " + hardwareRevision + " invalid.");
        }
		cmds.append(revision);
		String msg = BeAdminCentOSTools.getOutStreamExecCmd(cmds.toString());
		if (msg.contains("no")) {
			level = UpdateParameters.LEVEL_IMAGE_NO;
			log.error("checkImageVersion() has failure", msg);
		} else if (msg.contains("risk")) {
			level = UpdateParameters.LEVEL_IMAGE_RISK;
			log.warn("checkImageVersion() has warning", msg);
		}

		return level;
	}

	private String getHexVersion(String softVer, String displayVersion) {
		String hexVer = null;
		if (softVer != null && displayVersion != null) {
			String[] vers = softVer.split("\\.");
			String tempVer = "", patchVer = "", tempPatchVer = "";

			if (vers.length >= 3) {
				tempPatchVer = vers[0] + "." + vers[1] + "r" + vers[2];
				int vidx = displayVersion.toLowerCase().indexOf(tempPatchVer);

				if (vidx > -1) {
					vidx += tempPatchVer.length();
						try {
							patchVer = displayVersion.substring(vidx, vidx + 1);
							byte pChar = patchVer.getBytes()[0];
							if ((pChar >= 97 && pChar <= 102) || (pChar >= 65 && pChar <= 70)) {
								patchVer = "0" + patchVer.toLowerCase();
							} else {
								patchVer = "";
							}
						} catch (StringIndexOutOfBoundsException e) {
							patchVer = "";
						}
				}
				try {
					String tmp = Integer.toHexString(Integer.parseInt(vers[0]));
					tempVer = tmp.length() <= 1 ? "0" + tmp : tmp;
					tmp = Integer.toHexString(Integer.parseInt(vers[1]));
					tempVer += tmp.length() <= 1 ? "0" + tmp : tmp;
					tmp = Integer.toHexString(Integer.parseInt(vers[2]));
					tempVer += tmp.length() <= 1 ? "0" + tmp : tmp;
				} catch (Exception e) {
					tempVer = "";
				}
			}
			if (!tempVer.isEmpty()) {
				hexVer = "0x" + tempVer + (patchVer.isEmpty() ? "00" : patchVer);
			}
		}

		return hexVer;
	}

	private boolean isImageMatchMac(String imagePath, String macAddress) {
		StringBuilder cmds = new StringBuilder();
		if (NmsUtil.isHostedHMApplication()) {
			cmds.append(BeAdminCentOSTools.ahShellRoot + "/ahCheckImageOui.sh ");
		} else {
			cmds.append(BeAdminCentOSTools.ahShellRoot + "/ah_imginfo_oui_image_update ");
		}
		if (NmsUtil.isHMForOEM()) {
			cmds.append("BB ");
		} else {
			cmds.append("AH ");
			if (macAddress.startsWith(NmsUtil.getHiveApMacOui()[0])) return true;
		}
		macAddress = macAddress.toUpperCase();
		cmds.append(macAddress.substring(0,2) + ":" + macAddress.substring(2,4) + ":" +macAddress.substring(4,6) + " ");
		cmds.append(imagePath);

		String msg = BeAdminCentOSTools.getOutStreamExecCmd(cmds.toString());

		return "yes".equalsIgnoreCase(msg);
	}

	/**
	 * Wrap L7 Signature update object
	 *
	 * @param hiveAp
	 * @param signatureName
	 * @param tftp
	 * @param limit
	 * @param maxTimeout
	 * @return
	 * @throws UpdateObjectException
	 */
	public UpdateObject getSignatureUpdateObject(HiveAp hiveAp,
			String signatureName, int limit, int maxTimeout)
			throws UpdateObjectException {
		if (null != hiveAp) {
			String ip = hiveAp.getIpAddress();
			if (null == ip || "".equals(ip.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApNoIp",
						new String[] { hiveAp.getHostName() }));
			}
			if (null == signatureName || "".equals(signatureName)) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.l7.signature.notfound",
						new String[] { hiveAp.getHostName() }));
			}
			File signatureFile = new File(
					AhDirTools.getL7SignatureDir(HmDomain.HOME_DOMAIN)
							+ signatureName);
			if (!signatureFile.exists()) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.l7.signature.file.notfound",
						new String[] { signatureName }));
			}
			int size = (int) signatureFile.length();
			return UpdateObjectFactory.getSignatureUpdateObject(hiveAp,
					signatureName, size, limit, maxTimeout);
		}
		return null;
	}

	/**
	 *
	 * @param hiveAp
	 *            -
	 * @param fileName
	 *            -
	 * @param continued
	 *            Whether executing this object while previous object executed
	 *            failed.
	 * @param isAuto
	 * @return -
	 * @throws Exception
	 */
	public UpdateObject getOsDetectionUpdateObject(HiveAp hiveAp,
			boolean continued, boolean isAuto
			) throws Exception {
		String fileName = ImportTextFileAction.OS_VERSION_FILE_NAME_TAR;
		if (null != hiveAp) {
			String ip = hiveAp.getIpAddress();
			if (null == ip || "".equals(ip.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApNoIp",
						new String[] { hiveAp.getHostName() }));
			}

			String filePath = AhDirTools.getOsDetectionDir()+ fileName;
			File OsDetectionFile = new File(filePath);
			if (!OsDetectionFile.exists()) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveap.OsDetection.file.notfound", new String[] {
								fileName, hiveAp.getHostName() }));
			}
			String filePath2 = AhDirTools.getOsDetectionDir()+
					ImportTextFileAction.OS_VERSION_FILE_NAME;
			if(BeTopoModuleUtil.chkOsDetectionObject(hiveAp,filePath2)){
				return UpdateObjectFactory.getOsDetectionUpdateObject(hiveAp,fileName,
						continued,	isAuto);
			}

		}
		return null;
	}

	/**
	 *
	 * @param hiveAp
	 *            -
	 * @param scriptType
	 *            -
	 * @param activateTime
	 *            -
	 * @param isRelativeTime
	 *            -
	 * @param saveServerFiles
	 *            -
	 * @param continued
	 *            Whether executing this object while previous object executed
	 *            failed.
	 * @param isAuto
	 *            -
	 * @return -
	 * @throws UpdateObjectException
	 *             -
	 */
	public UpdateObject getConfigUpdateObject(HiveAp hiveAp, short scriptType,
			String activateTime, boolean isRelativeTime,
			boolean saveServerFiles, boolean continued, boolean isAuto, boolean enableDS)
			throws UpdateObjectException {
		if (null != hiveAp) {
			String ip = hiveAp.getIpAddress();
			if (null == ip || "".equals(ip.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApNoIp",
						new String[] { hiveAp.getHostName() }));
			}
			return UpdateObjectFactory.getScriptUpdateObject(hiveAp,
					scriptType, activateTime, isRelativeTime, saveServerFiles,
					continued, isAuto, enableDS);
		}
		return null;
	}

	public UpdateObject getDsConfigUpdateObject(HiveAp hiveAp, short uploadType, boolean isAuto)
			throws UpdateObjectException {
		if (null != hiveAp) {
			String ip = hiveAp.getIpAddress();
			if (null == ip || "".equals(ip.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApNoIp",
						new String[] { hiveAp.getHostName() }));
			}
			return UpdateObjectFactory.getDsCfgUpdateObject(hiveAp, uploadType, isAuto);
		}
		return null;
	}

	public UpdateObject getBootstrapUpdateObject(HiveAp hiveAp,
			BootstrapConfigObject bcObject) throws UpdateObjectException {
		if (null != hiveAp) {
			String ip = hiveAp.getIpAddress();
			if (null == ip || "".equals(ip.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApNoIp",
						new String[] { hiveAp.getHostName() }));
			}
			return UpdateObjectFactory.getBootstrapUpdateObject(hiveAp,
					bcObject);
		}
		return null;
	}

	public UpdateObject getCountryCodeUpdateObject(HiveAp hiveAp,
			boolean continued, int countryCode, String offsetStr)
			throws UpdateObjectException {
		if (null != hiveAp) {
			String ip = hiveAp.getIpAddress();
			int regionCode = hiveAp.getRegionCode();
			if (null == ip || "".equals(ip.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApNoIp",
						new String[] { hiveAp.getHostName() }));
			}
			if (regionCode != BeAPConnectEvent.REGION_CODE_WORLD) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApRegionNotWorld", new String[] { hiveAp
								.getHostName() }));
			}else if(countryCode == CountryCode.COUNTRY_CODE_US){
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.regionWorldToFcc", new String[]{hiveAp.getHostName()}));
			}
			return UpdateObjectFactory.getCountryCodeUpdateObject(hiveAp,
					continued, countryCode, offsetStr);
		}
		return null;
	}

	public UpdateObject getPskUpdateObject(HiveAp hiveAp, short scriptType,
			String activateTime, boolean isRelativeTime,
			boolean saveServerFiles, boolean continued, boolean isAuto, boolean enableDS)
			throws UpdateObjectException {
		if (null != hiveAp) {
			String ip = hiveAp.getIpAddress();
			if (null == ip || "".equals(ip.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApNoIp",
						new String[] { hiveAp.getHostName() }));
			}
			return UpdateObjectFactory.getPskUpdateObject(hiveAp, scriptType,
					activateTime, isRelativeTime, saveServerFiles, continued,
					isAuto, enableDS);
		}
		return null;
	}

	public UpdateObject getPoeUpdateObject(HiveAp hiveAp, int maxPower)
			throws UpdateObjectException {
		if (null != hiveAp) {
			String ip = hiveAp.getIpAddress();
			if (null == ip || "".equals(ip.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApNoIp",
						new String[] { hiveAp.getHostName() }));
			}
			if (hiveAp.isPoEAvailable()) {
				return UpdateObjectFactory.getPoeUpdateObject(hiveAp, maxPower);
			}
		}
		return null;
	}

	public UpdateObject getNetdumpUpdateObject(HiveAp hiveAp, boolean enableNetdump, String netdumpServer,
			String vlanId,String nVlanId,String device,String gateway,String ipMode,String managerPortStr)
			throws UpdateObjectException {
		if (null != hiveAp) {
			String ip = hiveAp.getIpAddress();
			if (null == ip || "".equals(ip.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApNoIp",
						new String[] { hiveAp.getHostName() }));
			}
			if (!checkVersionSupported(hiveAp, "3.5.4.0")) {
				throw new UpdateObjectException(MgrUtil.getUserMessage(
						"error.hiveAp.feature.support.version", MgrUtil
								.getHiveOSDisplayVersion("3.5.4.0")));
			}
			return UpdateObjectFactory.getNetdumpUpdateObject(hiveAp, enableNetdump, netdumpServer,vlanId,nVlanId,device,gateway,ipMode,managerPortStr);
		}
		return null;
	}

	public UpdateObject getApIpDnsUpdateObject(HiveAp hiveAp, String ipAddress, String netMask, String gateWay, String dnsIp)
			throws UpdateObjectException {
		if (null != hiveAp) {
			String ip = hiveAp.getIpAddress();
			if (null == ip || "".equals(ip.trim())) {
				throw new UpdateObjectException(NmsUtil.getUserMessage(
						"error.hiveApNoIp",
						new String[] { hiveAp.getHostName() }));
			}
			return UpdateObjectFactory.getApIpDnsUpdateObject(hiveAp, ipAddress, netMask, gateWay, dnsIp);
		}
		return null;
	}

	public UpdateObject getOutdoorSettingsUpdateObject(HiveAp hiveAp,
			boolean continued, boolean isOutdoor, String offsetStr)
			throws UpdateObjectException {
		if(null == hiveAp){
			return null;
		}
		checkIpNotNull(hiveAp);
		return UpdateObjectFactory.getOutdoorSettingsUpdateObject(hiveAp,
					continued, isOutdoor, offsetStr);
	}
	public void checkIpNotNull(HiveAp hiveAp) throws UpdateObjectException{
		String ip = hiveAp.getIpAddress();
		if (null == ip || "".equals(ip.trim())) {
			throw new UpdateObjectException(NmsUtil.getUserMessage(
					"error.hiveApNoIp",
					new String[] { hiveAp.getHostName() }));
		}
	}
	public static boolean checkVersionSupported(HiveAp hiveAp, String version) {
		String softVer = hiveAp.getSoftVer();
		return softVer != null
				&& (NmsUtil.compareSoftwareVersion(version, softVer) <= 0);
	}

}