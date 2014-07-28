package com.ah.be.topo;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import com.ah.be.admin.adminOperateImpl.AhHiveAPKernelDump;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeConfigUtil;
import com.ah.be.app.HmBeEventUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.app.HmBeMiscUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.app.HmBePerformUtil;
import com.ah.be.app.HmBeTopoUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeBonjourGatewayEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCapwapDTLSConfigEvent;
import com.ah.be.communication.event.BeCapwapServerParamConfigEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.communication.event.BeDeleteAPConnectEvent;
import com.ah.be.communication.event.BeGetStatisticEvent;
import com.ah.be.communication.event.BeHostIdentificationKeyEvent;
import com.ah.be.communication.event.BeIDPMitigationQueryEvent;
import com.ah.be.communication.event.BeIDPQueryEvent;
import com.ah.be.communication.event.BeLLDPCDPInfoEvent;
import com.ah.be.communication.event.BeOTPStatusEvent;
import com.ah.be.communication.event.BePOEStatusEvent;
import com.ah.be.communication.event.BePOEStatusResultEvent;
import com.ah.be.communication.event.BePSEStatusEvent;
import com.ah.be.communication.event.BePortAvailabilityEvent;
import com.ah.be.communication.event.BeRadsecProxyInfoQueryEvent;
import com.ah.be.communication.event.BeRebootFailEvent;
import com.ah.be.communication.event.BeRouterLTEVZInfoEvent;
import com.ah.be.communication.event.BeSimulateHiveAPEvent;
import com.ah.be.communication.event.BeStatisticResultEvent;
import com.ah.be.communication.event.BeSwitchPortInfoEvent;
import com.ah.be.communication.event.BeSwitchPortStatsEvent;
import com.ah.be.communication.event.BeTeacherViewClassInfoEvent;
import com.ah.be.communication.event.BeVPNStatusEvent;
import com.ah.be.communication.event.BeVPNStatusResultEvent;
import com.ah.be.communication.event.BeWTPEventControlEvent;
import com.ah.be.communication.mo.SimulateHiveAP;
import com.ah.be.communication.mo.SimulateHiveAPResult;
import com.ah.be.config.create.cli.HmCliSpecialHandling;
import com.ah.be.config.hiveap.provision.ProvisionProcessor;
import com.ah.be.misc.teacherview.ClearClassRequest;
import com.ah.be.os.FileManager;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.performance.BeClearHighIntervalReportAp;
import com.ah.be.topo.idp.IdpStatisticHiveAp;
import com.ah.bo.HmBo;
import com.ah.bo.admin.CapwapSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapSettings;
import com.ah.bo.monitor.PlannedAP;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhPortAvailability;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhSwitchPortInfo;
import com.ah.bo.performance.AhXIf;
import com.ah.ui.actions.config.ImportTextFileAction;
import com.ah.util.HmException;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.bo.device.DeviceProperties;
import com.ah.util.devices.impl.Device;

public class BeTopoModuleUtil {

	private static final Tracer log = new Tracer(BeTopoModuleUtil.class
			.getSimpleName());

	public static final String HM_ROOT = System.getenv("HM_ROOT");

	public static final String FILE_ROOT = HM_ROOT + File.separator + "domains";

	public static final String DEFAULT_IMAGES_ROOT = HM_ROOT + File.separator
			+ "images" + File.separator + "maps";

	private static final String TOPO_CONF_DIR = File.separator + "WEB-INF"
			+ File.separator + "topology";

	private static final String TOPO_CONTAINER_FILE = "topoContainer.txt";

	public static final String DEFAULT_MAP_ICON = "building_32x32.png";

	public static final long IMAGE_MAX_SIZE = 1024 * 500;// 500 KB

	private static List<String[]> icons;

	public static String getRealTopoConfPath() {
		return HM_ROOT + TOPO_CONF_DIR;
	}

	public static String getRealTopoBgImagePath(String domainName) {
		return FILE_ROOT + File.separator + domainName + File.separator
				+ "maps";
	}

	public static List<String[]> getMapIcons() {
		if (null == icons) {
			String filename = getRealTopoConfPath() + File.separator
					+ TOPO_CONTAINER_FILE;
			try {
				FileInputStream in = new FileInputStream(filename);
				Properties ems_properties = new Properties();
				ems_properties.load(in);

				icons = new ArrayList<String[]>(ems_properties.size());
				for (Object obj : ems_properties.keySet()) {
					String displayName = (String) obj;
					String fileName = ems_properties.getProperty(displayName);

					if (null != displayName && null != fileName) {
						String[] item = new String[] { fileName, displayName };
						icons.add(item);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		return icons;
	}

	/**
	 * Map settings is set by every domain, so there's maybe different value per
	 * domain.
	 *
	 * @param domainId -
	 * @return MapSetting instance.
	 */
	public static MapSettings getMapGlobalSetting(Long domainId) {
		if (null == domainId) {
			return new MapSettings();
		}
		List<MapSettings> settings = QueryUtil.executeQuery(MapSettings.class, null,
				new FilterParams("owner.id", domainId));
		return settings.isEmpty() ? new MapSettings() : settings
				.get(0);
	}

	public static MapSettings getMapGlobalSetting(HmDomain domain) {
		if (null == domain) {
			return new MapSettings();
		} else {
			return getMapGlobalSetting(domain.getId());
		}
	}

	public static List<String> getBackgroundImages(String domainName) {
		return AhAppContainer.HmBe.getBeTopoModule().getBackgroundImages(
				domainName);
	}

	public static boolean deleteBackgroundImage(String imageName,
			String domainName) {
		return AhAppContainer.HmBe.getBeTopoModule().deleteBackgroundImage(
				imageName, domainName);
	}

	public static void addBackgroundImage(String imageName, File imageFile,
			String domainName) throws Exception {
		AhAppContainer.HmBe.getBeTopoModule().addBackgroundImage(imageName,
				imageFile, domainName);
	}

	public static void compressImage(File imageFile, float quality)
			throws Exception {
		if (null == imageFile) {
			return;
		}
		try {
			BufferedImage image = ImageIO.read(imageFile);

			int thumbWidth = 1280;
			int thumbHeight = 1024;
			int imageWidth = image.getWidth(null);
			int imageHeight = image.getHeight(null);

			if (imageWidth < thumbWidth && imageHeight < thumbHeight) {
				thumbWidth = imageWidth;
				thumbHeight = imageHeight;
			}

			// Make sure the aspect ratio is maintained, so the image is not
			// skewed
			double thumbRatio = (double) thumbWidth / (double) thumbHeight;
			double imageRatio = (double) imageWidth / (double) imageHeight;
			if (thumbRatio < imageRatio) {
				thumbHeight = (int) (thumbWidth / imageRatio);
			} else {
				thumbWidth = (int) (thumbHeight * imageRatio);
			}
			// Draw the scaled image
			BufferedImage thumbImage = new BufferedImage(thumbWidth,
					thumbHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = thumbImage.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);

			// Get a ImageWriter for jpeg format.
			Iterator<ImageWriter> writers = ImageIO
					.getImageWritersBySuffix("jpg");
			if (!writers.hasNext())
				throw new IllegalStateException("No writers found");
			ImageWriter writer = writers.next();
			// Create the ImageWriteParam to compress the image.
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);
			// Uncomment code below to save the compressed files.
			// File file = new File(name);
			imageFile.delete();
			FileImageOutputStream output = new FileImageOutputStream(imageFile);
			writer.setOutput(output);
			writer.write(null, new IIOImage(thumbImage, null, null), param);
		} catch (IOException e) {
			throw new Exception("Error occur while resize image file. "
					+ e.getMessage());
		}
	}

	/**
	 * Create the directory under Tomcat container, so far, there's only store
	 * the map images which are user uploaded for map background used.
	 *
	 * @param domainName
	 *            the corresponding domain name.
	 * @return true if the specified repository of domain is exist, false
	 *         otherwise.
	 */
	public static boolean createDomainDirectory(String domainName) {
		String path = FILE_ROOT + File.separator + domainName;
		File file = new File(path);
		return file.exists() || file.mkdirs();
	}

	/**
	 * Delete the directory under Tomcat container, so far, there's only store
	 * the map images which are user upload for map background used.
	 *
	 * @param domainName
	 *            the corresponding domain name.
	 * @return true if the specified repository of domain is deleted, false
	 *         otherwise.
	 */
	public static boolean deleteDomainDirectory(String domainName) {
		try {
			String path = FILE_ROOT + File.separator + domainName;
			return deleteDir(path);
		} catch (Exception e) {
			DebugUtil.topoDebugError(
					"Delete map images directory error for domain:"
							+ domainName, e);
			return false;
		}
	}

	private static boolean deleteDir(String dirPath) {
		File dir = new File(dirPath);

		if (!dir.exists()) {
			return true;
		}

		if (!dir.isDirectory()) {
			return dir.delete();
		}

		if (dir.list().length == 0) {
			return dir.delete();
		}

		File[] files = dir.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				deleteDir(file.getAbsolutePath());
			} else {
				file.delete();
			}
		}
		return dir.delete();
	}

	public static boolean copyDefaultMapImages(String domainName) {
		try {
			String domainMapRoot = getRealTopoBgImagePath(domainName);
			File domainMapDirectory = new File(domainMapRoot);
			if (!domainMapDirectory.exists()) {
				domainMapDirectory.mkdirs();
			}
			FileManager.getInstance().copyDirectory(DEFAULT_IMAGES_ROOT,
					domainMapRoot);
		} catch (Exception e) {
			DebugUtil.topoDebugError("copy default image error.", e);
			return false;
		}
		return true;
	}

	public static BeCommunicationEvent sendSyncCliRequest(HiveAp hiveAp,
			String[] clis, byte cliType, int timeout) {
		try {
			BeCliEvent cliRequest = getCliEvent(hiveAp, clis, cliType);
			return HmBeCommunicationUtil.sendSyncRequest(cliRequest, timeout);
		} catch (Exception e) {
			log.error("sendSyncCliRequest", "catch build packet exception", e);
			return null;
		}
	}

	public static BeCliEvent getCliEvent(HiveAp hiveAp, String[] clis,
			byte cliType) throws BeCommunicationEncodeException {
		BeCliEvent cliRequest = new BeCliEvent();
		int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
		cliRequest.setAp(hiveAp);
		cliRequest.setClis(clis);
		cliRequest.setCliType(cliType);
		cliRequest.setSequenceNum(sequenceNum);
		cliRequest.buildPacket();
		return cliRequest;
	}

	private static String getResponseMessage(String hostname, byte responseType) {
		String msg = "";
		switch (responseType) {
		case BeCommunicationConstant.RESULTTYPE_NOFSM:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.capwap.server.nofsm");
			break;
		case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.capwap.server.fsmnotrun");
			break;
		case BeCommunicationConstant.RESULTTYPE_UNKNOWNMSG:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.capwap.server.unknownmessage",
					new String[] { String.valueOf(responseType) });
			break;
		case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.capwap.server.timeout");
			break;
		case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.hiveAp.update.timeout");
			break;
		case BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.capwap.server.disconnect");
			break;
		case BeCommunicationConstant.RESULTTYPE_MESSAGELENEXCEEDLIMIT:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.capwap.server.messageOverflow");
			break;
		case BeCommunicationConstant.RESULTTYPE_RESOUCEBUSY:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.cli.obj.onRequest",
					new String[] { hostname });
			break;
		}
		return msg;
	}

	private static String getResultHtmlMessage(BeCapwapCliResultEvent cliResult) {
		boolean isSuccessful = cliResult.isCliSuccessful();
		String message;
		if (isSuccessful) {
			String succeedMsg = cliResult.getCliSucceedMessage();
			if (null == succeedMsg) {
				succeedMsg = "";
			}
			
			// fix bug 26940
			if(succeedMsg.contains("Max port supported")){
				message = succeedMsg;
			} else {
				String blankInLog = "                            ";
				message = succeedMsg.replace(blankInLog, "");
			}
			
		} else {
			String errorCli = cliResult.getErrorCli();
			String errorMsg = cliResult.getHiveOSErrorMessage();
			errorCli = errorCli == null ? "" : errorCli.replace(
					NmsUtil.getHMScpUser(), "******").replace(
					NmsUtil.getHMScpPsd(), "******");
			if ("".equals(errorCli)) {
				message = MgrUtil.getUserMessage("info.cli.general.failed");
			} else {
				message = MgrUtil.getUserMessage(
						"info.cli.general.failed.withCli", "\n" + errorCli);
				if (null != errorMsg && !"".equals(errorMsg)) {
					message = message + "\n" + errorMsg;
				}
			}
		}
		message = message.replace(" ", "&nbsp;").replace("<", "&lt;").replace(
				">", "&gt;").replace("\n", "<br />")
				.replace("\t", "&emsp;&emsp;");
		return message;
	}
	
	//don't return cli command to gui
	private static String getResultHtmlMessageForShow(BeCapwapCliResultEvent cliResult) {
		boolean isSuccessful = cliResult.isCliSuccessful();
		String message;
		if (isSuccessful) {
			String succeedMsg = cliResult.getCliSucceedMessage();
			if (null == succeedMsg) {
				succeedMsg = "";
			}
			String blankInLog = "                            ";
			message = succeedMsg.replace(blankInLog, "");
		} else {
			String errorMsg = cliResult.getHiveOSErrorMessage();
			if ("".equals(errorMsg)) {
				message = MgrUtil.getUserMessage("info.cli.general.failed");
			} else {
				message = errorMsg;
			}
		}
		return message;
	}
	

	public static String parseCliRequestResult(BeCommunicationEvent result)
			throws Exception {
		if (null == result) {
			return MgrUtil.getUserMessage("error.cli.obj.buildRequest");
		}
		int msgType = result.getMsgType();
		if (msgType == BeCommunicationConstant.MESSAGETYPE_CLIRSP) {
			SimpleHiveAp ap = result.getSimpleHiveAp();
			String label = ap == null ? result.getApMac() : ap.getHostname();
			return getResponseMessage(label, result.getResult());
		} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT) {
			result.parsePacket();
			BeCapwapCliResultEvent cliResult = (BeCapwapCliResultEvent) result;
			return getResultHtmlMessage(cliResult);
		}
		return null;
	}

	//cli don't should be shown to user
	public static String parseCliRequestResultForShow(BeCommunicationEvent result)
			throws Exception {
		if (null == result) {
			return MgrUtil.getUserMessage("error.cli.obj.buildRequest");
		}
		int msgType = result.getMsgType();
		if (msgType == BeCommunicationConstant.MESSAGETYPE_CLIRSP) {
			SimpleHiveAp ap = result.getSimpleHiveAp();
			String label = ap == null ? result.getApMac() : ap.getHostname();
			return getResponseMessage(label, result.getResult());
		} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT) {
			result.parsePacket();
			BeCapwapCliResultEvent cliResult = (BeCapwapCliResultEvent) result;
			return getResultHtmlMessageForShow(cliResult);
		}
		return null;
	}
	
	public static boolean isCliExeSuccess(BeCommunicationEvent result)
			throws BeCommunicationDecodeException {
		boolean r = false;
		if (null != result) {
			int msgType = result.getMsgType();
			if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT) {
				result.parsePacket();
				BeCapwapCliResultEvent cliResult = (BeCapwapCliResultEvent) result;
				r = cliResult.getCliResult() == BeCommunicationConstant.CLIRESULT_SUCCESS;
			}
		}
		return r;
	}

	/**
	 * Transfer all HiveAPs which belong to a VHM to an new CAPWAP Server
	 *
	 * @param domainId -
	 * @param newServerIp -
	 */
	public static void transferHiveAPs(Long domainId, String newServerIp) {
		List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, null,
				new FilterParams("connected", true), domainId);
		DebugUtil.topoDebugInfo("Try to transfer HiveAPs belong domain:"
				+ domainId + ", connected HiveAP count:" + list.size()
				+ ", to New CAPWAP IP:" + newServerIp);
		for (HiveAp hiveAp : list) {
			transferHiveAP(hiveAp, newServerIp);
		}
	}

	/**
	 * change HiveAP CAPWAP Server IP. It needs two steps: 1: Open WTP switch.
	 * 2: Send CLI to HiveAP
	 *
	 * @param hiveAp -
	 * @param newServerIp -
	 */
	public static void transferHiveAP(HiveAp hiveAp, String newServerIp) {
		hiveAp.setNewTransferCapwap(newServerIp);
		sendWTPControlRequest(hiveAp);
	}

	/**
	 * Send a cli to HiveAP, try to change the CAPWAP ip, if the operation
	 * failed, try to disconnect connection
	 *
	 * @param hiveAp -
	 * @param transferCapwap -
	 */
	public static void sendCliTransferCapwapIp(HiveAp hiveAp,
			String transferCapwap) {
		try {
			String softVer = hiveAp.getSoftVer();
			String capwapServer = hiveAp.getCapwapLinkIp();
			String cli, cli2 = null;
			if (NmsUtil.compareSoftwareVersion(softVer, "3.5.0.0") > 0) {
				cli = AhCliFactory.getConfigCapwapServerCli(softVer,
						transferCapwap, true, true);
				if (null != capwapServer && !"".equals(capwapServer)) {
					cli2 = AhCliFactory.getConfigCapwapServerCli(softVer,
							capwapServer, false, true);
				}
			} else {
				cli = AhCliFactory.getHmConfigNoDisconnectCli(softVer,
						transferCapwap, true);
				if (null != capwapServer && !"".equals(capwapServer)) {
					cli2 = AhCliFactory.getHmConfigNoDisconnectCli(softVer,
							capwapServer, false);
				}
			}
			String cli3 = AhCliFactory.getSaveConfigCli();
			String[] clis = null == cli2 ? new String[] { cli, cli3 }
					: new String[] { cli, cli2, cli3 };
			BeCliEvent event = getCliEvent(hiveAp, clis,
					BeCliEvent.CLITYPE_NORMAL);
			BeCommunicationEvent result = HmBeCommunicationUtil
					.sendSyncRequest(event, 60);
			boolean isSuc = isCliExeSuccess(result);
			if (isSuc) {// log
				DebugUtil.topoDebugInfo("HiveAP:" + hiveAp.getMacAddress()
						+ " transfer CAPWAP Server to: " + transferCapwap
						+ " Successfully.");
				if (NmsUtil.compareSoftwareVersion(softVer, "3.5.0.0") < 0) {
					String msg = "HiveAP:"
							+ hiveAp.getHostName()
							+ " softver lower 3.5, delete HiveAP from CAPWAP server for reconnect.";
					log.info("processCommunicationEvent", msg);
					sendBeDeleteAPConnectRequest(hiveAp, true);
				}
			} else {// send disconnect event
				sendBeDeleteAPConnectRequest(hiveAp, false);
			}
		} catch (Exception e) {
			DebugUtil.topoDebugError("HiveAP:" + hiveAp.getMacAddress()
					+ " send transfer CAPWAP cli request Failed.", e);
		}
	}

	public static void clearHiveAPRelatedData(HiveAp hiveAp) {
		if (null == hiveAp) {
			return;
		}
		log.debug("clearHiveAPRelatedData",
				"Remove related active client, idp, latest stats of HiveAP: "
						+ hiveAp.getMacAddress() + " from database");
		// remove active clients associate to this HiveAP;
		HmBePerformUtil.removeActiveClients(hiveAp);
		// remove IDP information associate to this HiveAP;
		BoMgmt.getIdpMgmt().removeIdps(hiveAp.getMacAddress(),
				hiveAp.getOwner().getId());
		HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor().remove(hiveAp.getMacAddress());
		// remove statistic data associate to this HiveAP;
		HmBePerformUtil.clearLatestStatsData(hiveAp);
		// remove vpn status data
		HmBePerformUtil.clearVpnStatusData(hiveAp.getMacAddress());
		// remove bonjour gateway monitor data
		HmBePerformUtil.clearBonjourGatewayMonitorData(hiveAp.getMacAddress(),hiveAp.getOwner().getId());
		// remove LLDP collection data
		HmBePerformUtil.cleanLLDPInfo(hiveAp.getMacAddress());
		
		try {
			if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_SWITCH ||
				hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
				QueryUtil.bulkRemoveBos(AhSwitchPortInfo.class, new FilterParams("mac", hiveAp.getMacAddress()));
				QueryUtil.bulkRemoveBos(AhPortAvailability.class, new FilterParams("mac", hiveAp.getMacAddress()));
			}
		} catch (Exception e) {
			log.debug("clearHiveAPRelatedData",
					"Remove monitor data of HiveAP: "
							+ hiveAp.getMacAddress() + " from database",e);
		}
	}

	/**
	 * send request tool. Send a set of requests which need to be sent for
	 * managed HiveAP. the request must send after WTP control is opened.
	 *
	 * @param hiveAp
	 *            -
	 */
	public static void sendRequestsForManagedHiveAP(HiveAp hiveAp) {
		// send IDP Query while open the WTP Event switch.
		sendIDPQuery(hiveAp);
		// send Statistic request while open the WTP Event switch.
		sendStatisticQuery(hiveAp);
		// retrieve active clients.
		HmBePerformUtil.retrieveActiveClients(hiveAp);

		// The function of Memory config is not supported for HiveAP with the
		// software version lower than 3.2.0.0.
		if (NmsUtil.compareSoftwareVersion("3.2.0.0", hiveAp.getSoftVer()) < 0) {
			// send CLI command which saved in memory
			sendMemoryConfig(hiveAp);
		}
		// The function of Request Device ID and Port ID is not supported for
		// HiveAP with the software version lower than 3.4.0.0.
		if (NmsUtil.compareSoftwareVersion("3.4.0.0", hiveAp.getSoftVer()) < 0) {
			sendLldpCdpQuery(hiveAp);
		}
		// The function of Request VPN status is not supported for HiveAP with
		// the software version lower than 3.4.2.0.

		if (NmsUtil.compareSoftwareVersion("3.4.2.0", hiveAp.getSoftVer()) <= 0) {
			// The function of Request VPN status is not supported for HiveAP
			// with the software version lower than 3.4.2.0.
			// Just query for VPN server
			// Note: check with the VPN mark only, instead of
			// function:isVpnServer(), for performance issue, also assume the
			// configuration is synchronized with HiveAP.
			if (hiveAp.getVpnMark() == HiveAp.VPN_MARK_SERVER
					|| hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT) {
				sendVpnStatusQuery(hiveAp);
			}
		}

		if (NmsUtil.compareSoftwareVersion("3.5.0.0", hiveAp.getSoftVer()) <= 0) {
			/*
			 * Teacher View
			 *
			 * Check if there is a request should be sent to HiveAP to clear
			 * students of a class.
			 */
			sendTeacherViewRequest(hiveAp);
		}

		//	WAN & VPN availability information
		if (NmsUtil.compareSoftwareVersion("5.0.0.0", hiveAp.getSoftVer()) < 0) {
			// supported only when software version larger than 5.0.0.0(from 5.0r1)
			short deviceType = hiveAp.getDeviceType();
			if (deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER
					|| deviceType == HiveAp.Device_TYPE_VPN_GATEWAY
					|| deviceType == HiveAp.Device_TYPE_VPN_BR) {
				sendInterfaceAvailabilityQuery(hiveAp);
			}
		}

		// PSE status
		if (NmsUtil.compareSoftwareVersion("5.0.1.0", hiveAp.getSoftVer()) < 0) {
			// supported only when software version larger than 5.0.1.0(from 5.0r2)
			if (DeviceProperties.isPSEPortSupport4CertainModel(hiveAp.getHiveApModel())) {
				sendInterfacePSEStatusQuery(hiveAp);
			}
		}

		// Bonjour Gateway
		if (NmsUtil.compareSoftwareVersion("5.1.0.0", hiveAp.getSoftVer()) < 0
				&& hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100) {
			// supported only when software version larger than 5.1.0.0(from 5.1r1)
			sendBonjourGatewayMonitorQuery(hiveAp);
		}

		//osDetection
		if (NmsUtil.compareSoftwareVersion("5.1.1.0", hiveAp.getSoftVer()) < 0) {
			// supported only when software version larger than 5.1.0.0(from 5.1r1)
			sendOsDetectionFileRequest(hiveAp);
		}
		
		//send query to switch
		if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_SWITCH ||
			(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER && hiveAp.getDeviceInfo().isSptEthernetMore_24())) {
			sendSwitchPortInfoQuery(hiveAp);
			sendSwitchPortStatsQuery(hiveAp);
		}
		
		//send query about LTEVZInfo for BR200-LTE-VZ
		sendRouterLTEVZInfoQuery(hiveAp);
		
		//send query about IDM proxy.
		if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.2.1.0") >= 0) {
			sendQueryEvent(BeRadsecProxyInfoQueryEvent.class, hiveAp);
		}
		
		BeClearHighIntervalReportAp.sendCLI4ChangePollPeriod(hiveAp, 10);
	}

	/**
	 * send a request to tell the HiveAp to open its WTP Event switch
	 *
	 * @param hiveAp
	 *            -
	 */
	public static void sendWTPControlRequest(HiveAp hiveAp) {
		try {
			BeWTPEventControlEvent c_event = new BeWTPEventControlEvent();
			c_event.setAp(hiveAp);
			c_event.setEnabling(true);
			// c_event
			// .setWtpControlMsgtData(new BeCapwapWtpEventControlEvent(
			// true).buildPacket());
			c_event.buildPacket();
			HmBeCommunicationUtil.sendRequest(c_event);
			DebugUtil.topoDebugInfo("HiveAP:" + hiveAp.getMacAddress()
					+ " send WTP Control request successfully.");
		} catch (Exception e) {
			DebugUtil.topoDebugError("HiveAP:" + hiveAp.getMacAddress()
					+ " send WTP Event Control request Failed.", e);
		}
	}

	public static void sendIdentification(HiveAp hiveAp, short attempt) {
		try {
			Map<Byte, String> key = AhAppContainer.getBeAdminModule()
					.getSshKeyMgmt().getKeys();
			if (null == key) {
				HmBeLogUtil
						.addSystemLog(
								HmSystemLog.LEVEL_MAJOR,
								HmSystemLog.FEATURE_HIVEAPS,
								""+MgrUtil.getUserMessage("hm.system.log.be.topo.module.util.send.host.identification.error",new String[]{NmsUtil.getOEMCustomer().getAccessPonitName(),hiveAp.getHostName()}));
				return;
			}
			BeHostIdentificationKeyEvent h_event = new BeHostIdentificationKeyEvent();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			h_event.setAttemptCount(attempt);
			h_event.setAp(hiveAp);
			h_event.setKeyMap(key);
			h_event.setSequenceNum(sequenceNum);
			h_event.buildPacket();
			HmBeCommunicationUtil.sendRequest(h_event, 105);
			DebugUtil
					.topoDebugInfo("HiveAP:"
							+ hiveAp.getMacAddress()
							+ " send Identification request successfully. Sequence Num("
							+ sequenceNum + ").");
		} catch (Exception e) {
			DebugUtil
					.topoDebugError(
							"HiveAP:"
									+ hiveAp.getMacAddress()
									+ " send Host Identification request Failed. Attempt count:"
									+ attempt, e);
		}
	}

	/**
	 * This function only used after Host Identification is changed. Then all
	 * Managed HiveAP needed to update its key file
	 */
	public static void sendIdentification() {
		long start = System.currentTimeMillis();
		List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, null,
				new FilterParams("manageStatus", HiveAp.STATUS_MANAGED));
		// int index = 0;
		for (HiveAp hiveAp : list) {
			sendIdentification(hiveAp, (short) 1);
			// if (++index == 200) {
			try {
				Thread.sleep(5);
			} catch (Exception e) {
				log.error("sendIdentification", "Thread sleep error.", e);
			}
			// index = 0;
			// }
		}
		long end = System.currentTimeMillis();
		log.debug("sendIdentification",
				"Send Identification for All Managed HiveAPs:" + list.size()
						+ " cost:" + (end - start) + "ms.");
	}

	private static void sendMemoryConfig(HiveAp hiveAp) {
		if (hiveAp.isReConnectedByReboot()) {
			// reboot is assigned by CAPWAP event, which indicate it is
			// connected by a reboot.
			try {
				String result = HmCliSpecialHandling.generateCachedOnlyConfig(hiveAp, true);
				if (null != result && !"".equals(result.trim())) {
					String[] clis = result.split("\n");
					String[] newClis = new String[clis.length];
					for (int i = 0; i < clis.length; i++) {
						newClis[i] = clis[i] + "\n";
					}
					int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
					BeCliEvent request = new BeCliEvent();
					request.setAp(hiveAp);
					request.setClis(newClis);
					request.setSequenceNum(sequenceNum);
					request.buildPacket();
					HmBeCommunicationUtil.sendRequest(request);
					DebugUtil
							.topoDebugInfo("HiveAP:"
									+ hiveAp.getMacAddress()
									+ " send Memory Config request successfully. Sequence Num("
									+ sequenceNum + ").");
				} else {
					log.debug("sendMemoryConfig",
							"no User Config Date need to be sent.");
				}
			} catch (Exception e) {
				DebugUtil.topoDebugError("HiveAP:" + hiveAp.getMacAddress()
						+ " send Memory Config request Failed.", e);
			}
		}
	}

	/**
	 * send a request that indicate that the HiveAp is disconnected or deleted.
	 *
	 * @param hiveAp -
	 * @param isRemoved -
	 */
	public static void sendBeDeleteAPConnectRequest(HiveAp hiveAp,
			boolean isRemoved) {
		try {
			BeDeleteAPConnectEvent d_event = new BeDeleteAPConnectEvent();
			d_event.setAp(hiveAp);
			d_event.setDeleteAP(isRemoved);
			d_event.buildPacket();
			HmBeCommunicationUtil.sendRequest(d_event);
		} catch (Exception e) {
			DebugUtil.topoDebugError("HiveAP:" + hiveAp.getMacAddress()
					+ " send Delete Ap Connection request failed.", e);
		}
	}

	public static void sendBeDeleteAPConnectRequest(Collection<HiveAp> hiveAps,
			boolean isRemoved) {
		if (null != hiveAps) {
			for (HiveAp hiveAp : hiveAps) {
				sendBeDeleteAPConnectRequest(hiveAp, isRemoved);
			}
		}
	}

	public static void sendBeDeleteAPConnectRequest(String hiveApMac,
			boolean isRemoved) {
		HiveAp hiveAp = new HiveAp();
		hiveAp.setMacAddress(hiveApMac);
		sendBeDeleteAPConnectRequest(hiveAp, isRemoved);
	}

	public static void sendBeDeleteAPConnectRequest(Set<String> hiveApMacs,
			boolean isRemoved) {
		if (null != hiveApMacs) {
			for (String hiveApMac : hiveApMacs) {
				sendBeDeleteAPConnectRequest(hiveApMac, isRemoved);
			}
		}
	}

	public static void sendStatisticQuery(HiveAp hiveAp) {
		try {
			List<Byte> tables = new ArrayList<Byte>(3);
			tables.add(BeCommunicationConstant.STATTABLE_AHXIF);
			tables.add(BeCommunicationConstant.STATTABLE_AHRADIOATTRIBUTE);
			tables.add(BeCommunicationConstant.STATTABLE_AHNEIGHBOR);
			BeGetStatisticEvent event = getStatisticEvent(hiveAp, tables);
			HmBeCommunicationUtil.sendRequest(event);
		} catch (Exception e) {
			DebugUtil.topoDebugError("HiveAP:" + hiveAp.getMacAddress()
					+ " send statistic query request Failed.", e);
		}
	}

	/**
	 * send a request that to query IDP information on HiveAP currently.
	 *
	 * @param hiveAp
	 *            -
	 */
	public static void sendIDPQuery(HiveAp hiveAp) {
		try {
			IdpStatisticHiveAp s_ap = new IdpStatisticHiveAp();
			BeIDPQueryEvent i_event = new BeIDPQueryEvent();
			i_event.setAp(hiveAp);
			i_event.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			i_event.buildPacket();
			int rt = HmBeCommunicationUtil.sendRequest(i_event);
			// add this hiveAp to IDP statistic list.
			s_ap.setSequenceNum(rt);
			s_ap.setNodeId(hiveAp.getMacAddress());
			HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor().add(s_ap);
			DebugUtil.topoDebugInfo("HiveAP:" + hiveAp.getMacAddress()
					+ " send Idp query request successfully. Sequence Num("
					+ s_ap.getSequenceNum() + ").");
		} catch (Exception e) {
			DebugUtil.topoDebugError("HiveAP:" + hiveAp.getMacAddress()
					+ " send Idp query request Failed.", e);
		}
	}

	/**
	 * send a request that to query IDP information on HiveAP currently.
	 *
	 * @param sAp -
	 * @param hiveAp -
	 */
	public static void sendMitigationQuery(IdpStatisticHiveAp sAp, HiveAp hiveAp) {
		try {
			BeIDPMitigationQueryEvent event = new BeIDPMitigationQueryEvent();
			event.setAp(hiveAp);
			event.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			event.buildPacket();
			int rt = HmBeCommunicationUtil.sendRequest(event);
			sAp.setSequenceNum(rt);
			DebugUtil
					.topoDebugInfo("HiveAP:"
							+ hiveAp.getMacAddress()
							+ " send Idp mitigation request successfully. Sequence Num("
							+ sAp.getSequenceNum() + ").");
		} catch (Exception e) {
			DebugUtil.topoDebugError("HiveAP:" + hiveAp.getMacAddress()
					+ " send mitigation query request Failed.", e);
		}
	}

	/**
	 * send bulk requests that to query IDP information.
	 *
	 * @param hiveAps
	 *            -
	 */
	public static void sendIDPQuery(List<HiveAp> hiveAps) {
		if (null == hiveAps) {
			return;
		}
		int index = 0;
		for (HiveAp hiveAp : hiveAps) {
			sendIDPQuery(hiveAp);
			if (++index == 10) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					log.error("sendIDPQuery", "Thread sleep error.", e);
				}
				index = 0;
			}
		}
	}

	public static void sendLldpCdpQuery(HiveAp hiveAp) {
		try {
			BeLLDPCDPInfoEvent event = new BeLLDPCDPInfoEvent();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			event.setAp(hiveAp);
			event.setSequenceNum(sequenceNum);
			event.buildPacket();
			HmBeCommunicationUtil.sendRequest(event);
			DebugUtil.topoDebugInfo("HiveAP:" + hiveAp.getMacAddress()
					+ " send LldpCdp query request successfully. Sequence Num("
					+ sequenceNum + ").");
		} catch (Exception e) {
			DebugUtil.topoDebugError("HiveAP:" + hiveAp.getMacAddress()
					+ " send IldpCdp query request Failed.", e);
		}
	}

	/**
	 * send a event to CAPWAP Server, give it the CAPWAP Server DTLS information
	 * in database;
	 */
	public static void sendCapwapServerParamConfig() {
		BeCapwapServerParamConfigEvent s_event = new BeCapwapServerParamConfigEvent();
		List<CapwapSettings> capwapSettings = QueryUtil.executeQuery(CapwapSettings.class,
				null, null);
		if (!capwapSettings.isEmpty()) {
			try {
				CapwapSettings setting = capwapSettings.get(0);
				s_event.setUdpPort((short) setting.getUdpPort());
				s_event.setEchoTimeout(setting.getTimeOut());
				s_event.setNeighborDeadInterval(setting
						.getNeighborDeadInterval());
				s_event.setPassPhrase(setting.getBootStrap());
				s_event.setDtlsCapability(setting.getDtlsCapability());
				String simulatorFlag = ConfigUtil.getConfigInfo(
						ConfigUtil.SECTION_APPLICATION,
						ConfigUtil.KEY_APPLICATION_SUPPORTSIMULATOR, "1");
				s_event.setSupportSimulator(Integer.valueOf(simulatorFlag) == 1);
				s_event.buildPacket();
				int rt = HmBeCommunicationUtil.sendRequest(s_event);
				if (rt > 0) {
					DebugUtil
							.topoDebugInfo("Send CapwapServer Parameters request successfully.");
				}
			} catch (Exception e) {
				DebugUtil.topoDebugError(
						"Send CapwapServer Parameters Failed.", e);
			}
		} else {
			AhAppContainer.HmBe.setSystemLog(HmSystemLog.LEVEL_CRITICAL,
					HmSystemLog.FEATURE_ADMINISTRATION,
					"No CAPWAP Server Parameters found on " + NmsUtil.getOEMCustomer().getNmsName() + ".");
			DebugUtil.topoDebugError("Send CapwapServer Parameters Failed. "
					+ "reason: no Capwap Server Parameters in database.");
		}
	}

	public static void sendInitHiveApDTLSParamConfig() {
		sendHiveApDTLSParamConfig(null);
	}

	/**
	 * send events to CAPWAP Server, give it the hiveAps DTLS information in
	 * database.
	 *
	 * @param domainId -
	 */
	public static void sendHiveApDTLSParamConfig(Long domainId) {
		try {
			String where = "ipAddress != null AND currentKeyId != :s1 AND currentPassPhrase != null AND simulated = :s2";
			Object[] values = new Object[2];
			values[0] = 0;
			values[1] = false;

			List<HiveAp> hiveAps;
			if (null == domainId) {// Whole
				hiveAps = QueryUtil.executeQuery(HiveAp.class, null,
						new FilterParams(where, values));
			} else {// Domain
				hiveAps = QueryUtil.executeQuery(HiveAp.class, null,
						new FilterParams(where, values), domainId);
			}
			DebugUtil
					.topoDebugInfo("Need to send to CAPWAP server DTLS parameter HiveAP count:"
							+ hiveAps.size());
			if (!hiveAps.isEmpty()) {
				List<HiveAp> list = new ArrayList<HiveAp>(500);
				for (Object obj : hiveAps) {
					list.add((HiveAp) obj);
					if (list.size() >= 500) {
						BeCapwapDTLSConfigEvent d_event = new BeCapwapDTLSConfigEvent();
						d_event.setApList(list);
						d_event.buildPacket();
						HmBeCommunicationUtil.sendRequest(d_event);
						list = new ArrayList<HiveAp>(500);
					}
				}
				if (!list.isEmpty()) {
					BeCapwapDTLSConfigEvent d_event = new BeCapwapDTLSConfigEvent();
					d_event.setApList(list);
					d_event.buildPacket();
					HmBeCommunicationUtil.sendRequest(d_event);
				}
			}
		} catch (Exception e) {
			DebugUtil.topoDebugError("Send HiveAP DTLS Parameters Failed.", e);
		}
	}

	public static void sendInitHiveApSimulateConfig() {
		sendHiveApSimulateConfig(null);
	}

	public static void sendHiveApSimulateConfig(Long domainId) {
		try {
			String where = "simulated = :s1";
			Object[] values = new Object[1];
			values[0] = true;

			List<HiveAp> hiveAps;
			if (null == domainId) {// Whole
				hiveAps = QueryUtil.executeQuery(HiveAp.class, null,
						new FilterParams(where, values));
			} else {// Domain
				hiveAps = QueryUtil.executeQuery(HiveAp.class, null,
						new FilterParams(where, values), domainId);
			}
			DebugUtil
					.topoDebugInfo("Need to send to Simulate parameter HiveAP count:"
							+ hiveAps.size());
			if (!hiveAps.isEmpty()) {
				List<HiveAp> list = new ArrayList<HiveAp>(500);
				for (Object obj : hiveAps) {
					list.add((HiveAp) obj);
					if (list.size() >= 500) {
						BeSimulateHiveAPEvent d_event = new BeSimulateHiveAPEvent();
						d_event.setHiveAPList(list);
						d_event.buildPacket();
						HmBeCommunicationUtil.sendRequest(d_event);
						try {
							// avoid send too many message to CAPWAP
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							log.error("sendHiveApSimulateConfig", e);
						}
						list = new ArrayList<HiveAp>(500);
					}
				}
				if (!list.isEmpty()) {
					BeSimulateHiveAPEvent d_event = new BeSimulateHiveAPEvent();
					d_event.setHiveAPList(list);
					d_event.buildPacket();
					HmBeCommunicationUtil.sendRequest(d_event);
				}
			}
		} catch (Exception e) {
			DebugUtil.topoDebugError("Send Simulate Parameters Failed.", e);
		}
	}

	public static Long sendHiveApSimulateConfigSync(HmDomain domain,
			PlannedAP plannedAp, MapContainerNode parentNode) throws Exception {
		// license check
		String errorMsg = isDomainAllowManageSimHiveAP(domain.getId(), 1);
		if (null != errorMsg) {
			throw new HmException(errorMsg, null);
		}
		byte timezone = (byte) (TimeZone
				.getTimeZone(domain.getTimeZoneString()).getRawOffset() / 3600000);
		String location = HiveAp.DEFAULT_LOCATION + "@" + parentNode.getMapName();
		String vhmName = domain.getDomainName();
		SimulateHiveAP simAp = new SimulateHiveAP((short) 1, timezone,
				plannedAp.countryCode, plannedAp.apModel, vhmName, location,
				plannedAp.wifi0Channel, plannedAp.wifi0Power,
				plannedAp.wifi1Channel, plannedAp.wifi1Power);
		BeSimulateHiveAPEvent event = new BeSimulateHiveAPEvent();
		// return list information CAPWAP will generated simulate HiveAPs
		event.setNeedReturnResult(true);
		List<SimulateHiveAP> list = new ArrayList<SimulateHiveAP>(1);
		list.add(simAp);
		event.setSimulateAPList(list);
		try {
			event.buildPacket();
		} catch (Exception e) {
			throw new HmException(MgrUtil
					.getUserMessage("error.hiveAp.update.request.build"), null);
		}
		BeSimulateHiveAPEvent resp = (BeSimulateHiveAPEvent) HmBeCommunicationUtil
				.sendSyncRequest(event);
		byte type = resp.getResult();
		if (type != BeCommunicationConstant.RESULTTYPE_SUCCESS) {
			throw new HmException(
					"Request simulated "+NmsUtil.getOEMCustomer().getAccessPonitName()+" error, please check CAPWAP server status.",
					null);
		} else {
			List<SimulateHiveAPResult> results = resp.getSimulateAPResultList();
			if (null == results) {
				throw new HmException("Communication Module internal error",
						null);
			}
			SimulateHiveAPResult result = results.get(0);
			// find HiveAP
			HiveAp hiveAp = QueryUtil.findBoByAttribute(HiveAp.class,
					"macAddress", result.getMacAddress());
			if (null == hiveAp) {
				// create
				try {
					hiveAp = createSimulateHiveAp(parentNode, result
							.getMacAddress(), result.getWtpName(), result
							.getCode(), plannedAp.apModel,
							plannedAp.wifi0Channel, plannedAp.wifi0Power,
							plannedAp.wifi1Channel, plannedAp.wifi1Power,
							domain);
				} catch (Exception e) {
					log.error("sendHiveApSimulateConfigSync",
							" create simulated HiveAP error", e);
					// assume the simulated HiveAP has been create by another
					// thread, so try to pick up again
					hiveAp = QueryUtil.findBoByAttribute(HiveAp.class,
							"macAddress", result.getMacAddress());
				}
			}
			if (null == hiveAp) {
				throw new HmException("Create simulated "+NmsUtil.getOEMCustomer().getAccessPonitName()+" error", null);
			}
			return hiveAp.getId();
		}
	}

	private static HiveAp createSimulateHiveAp(MapContainerNode parentNode,
			String macAddress, String hostname, int simulateCode,
			short apModel, int wifi0Channel, int wifi0Power, int wifi1Channel,
			int wifi1Power, HmDomain domain) throws Exception {
		// check license
		String errorMsg = isDomainAllowManageSimHiveAP(domain.getId(), 1);
		if (null != errorMsg) {
			throw new HmException(errorMsg, null);
		}
		HiveAp hiveAp = new HiveAp(apModel);
		hiveAp.setMacAddress(macAddress);
		hiveAp.setHostName(hostname);
		hiveAp.setOwner(domain);
		hiveAp.setManageStatus(HiveAp.STATUS_MANAGED);
		hiveAp.setOrigin(HiveAp.ORIGIN_DISCOVERED);
		hiveAp.setSimulated(true);
		hiveAp.setConfigTemplate(HmBeParaUtil.getDefaultTemplate());
		hiveAp.setHiveApModel(apModel);
		hiveAp.setLocation(HiveAp.DEFAULT_LOCATION);
		hiveAp.setSimulateCode(simulateCode);
		hiveAp.setProductName(SimulateHiveAP.getFullProductName(apModel));
		hiveAp.init();
		hiveAp.initInterface();
		hiveAp.initDeviceStpSettings();
		if (hiveAp.isWifi1Available()) {
			if(hiveAp.is11acHiveAP()){
				hiveAp.setWifi0RadioProfile(HmBeParaUtil
						.getDefaultRadioNGProfile());
				hiveAp.setWifi1RadioProfile(HmBeParaUtil
						.getDefaultRadioACProfile());
			}else if (hiveAp.is11nHiveAP()) {
				hiveAp.setWifi0RadioProfile(HmBeParaUtil
						.getDefaultRadioNGProfile());
				hiveAp.setWifi1RadioProfile(HmBeParaUtil
						.getDefaultRadioNAProfile());
			} else {
				hiveAp.setWifi0RadioProfile(HmBeParaUtil
						.getDefaultRadioBGProfile());
				hiveAp.setWifi1RadioProfile(HmBeParaUtil
						.getDefaultRadioAProfile());
			}
			hiveAp.getWifi0().setChannel(wifi0Channel);
			hiveAp.getWifi0().setPower(wifi0Power);
			hiveAp.getWifi1().setChannel(wifi1Channel);
			hiveAp.getWifi1().setPower(wifi1Power);
		} else {
			if (hiveAp.is11nHiveAP() || hiveAp.is11acHiveAP()) {
				hiveAp.setWifi0RadioProfile(HmBeParaUtil
						.getDefaultRadioNGProfile());
			} else {
				hiveAp.setWifi0RadioProfile(HmBeParaUtil
						.getDefaultRadioBGProfile());
			}
			hiveAp.getWifi0().setChannel(wifi0Channel);
			hiveAp.getWifi0().setPower(wifi0Power);
		}
		return BoMgmt.getMapMgmt().createHiveApWithPropagation(hiveAp,
				parentNode);
	}

	public static void sendKernelDumpFileRequest(HiveAp hiveAp) {
		String mac = hiveAp.getMacAddress();
		String path = AhHiveAPKernelDump.get_dump_location(mac);
		if (null == path || "".equals(path.trim())) {
			log.error("sendKernelDumpFileRequest",
					"Could not get saved location on HM for HiveAP:" + mac);
			return;
		}
		String host = NmsUtil.getRunningCapwapServer(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		String cli = AhCliFactory.uploadKernelDumpFile(userName, password,
				host, path);
		// hide user name and password
		String displayCli = cli.replace(userName, "******").replace(password,
				"******");
		log.debug("sendKernelDumpFileRequest", "Kernel dump file request cli:"
				+ displayCli);
		try {
			String[] clis = { cli };
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			BeCliEvent request = new BeCliEvent();
			request.setAp(hiveAp);
			request.setClis(clis);
			request.setSequenceNum(sequenceNum);
			request.buildPacket();
			HmBeCommunicationUtil.sendRequest(request);
			DebugUtil
					.topoDebugInfo("HiveAP:"
							+ hiveAp.getMacAddress()
							+ " send Kernel Dump file request successfully. Sequence Num("
							+ sequenceNum + ").");
		} catch (Exception e) {
			DebugUtil.topoDebugError("HiveAP:" + hiveAp.getMacAddress()
					+ " send Kernel Dump file request Failed.", e);
		}
	}

	public static void sendVpnStatusQuery(HiveAp hiveAp) {
		try {
			BeVPNStatusEvent event = getVpnStatusRequest(hiveAp);
			HmBeCommunicationUtil.sendRequest(event);
			DebugUtil
					.topoDebugInfo("HiveAP:"
							+ hiveAp.getMacAddress()
							+ " send VPN status query request successfully. Sequence Num("
							+ event.getSequenceNum() + ").");
		} catch (Exception e) {
			DebugUtil.topoDebugError("HiveAP:" + hiveAp.getMacAddress()
					+ " send VPN status query request Failed.", e);
		}
	}

	private static void initializeAvailabilityPortStatus(HiveAp hiveAp) {
		if (hiveAp == null) return;
		try {
			List<AhPortAvailability> ports = QueryUtil.executeQuery(AhPortAvailability.class, null,
					new FilterParams("mac=:s1 and interftype=:s2",
							new Object[]{hiveAp.getMacAddress(), AhPortAvailability.INTERFACE_TYPE_WAN}),
					hiveAp.getOwner().getId());
			if (ports != null
					&& !ports.isEmpty()) {
				for (AhPortAvailability port : ports) {
					port.setInterfStatus(AhPortAvailability.INTERFACE_STATUS_DOWN);
					port.setInterfMode((byte)0);
				}
				QueryUtil.bulkUpdateBos(ports);
			}
		} catch (Exception e) {
			DebugUtil.topoDebugError("Device Mac:" + hiveAp.getMacAddress()
					+ " failed to initialize status of wan ports.", e);
		}
	}

	public static void sendInterfaceAvailabilityQuery(HiveAp hiveAp) {
		try {
			// try to initialize status of wan ports, please refer to bug 17052
			initializeAvailabilityPortStatus(hiveAp);

			BePortAvailabilityEvent event = new BePortAvailabilityEvent();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			event.setAp(hiveAp);
			event.setSequenceNum(sequenceNum);
			event.buildPacket();
			HmBeCommunicationUtil.sendRequest(event);

			DebugUtil.topoDebugInfo("Device Model:" + AhConstantUtil.getString(Device.NAME, hiveAp.getHiveApModel())
							+ " Device Mac:"
							+ hiveAp.getMacAddress()
							+ " send WAN & LAN interface availability query request successfully. Sequence Num("
							+ event.getSequenceNum() + ").");
		} catch (Exception e) {
			DebugUtil.topoDebugError("Device Mac:" + hiveAp.getMacAddress()
					+ " send WAN & LAN interface availability query request Failed.", e);
		}
	}

	public static void sendInterfacePSEStatusQuery(HiveAp hiveAp) {
		try {
			BePSEStatusEvent event = new BePSEStatusEvent();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			event.setAp(hiveAp);
			event.setSequenceNum(sequenceNum);
			event.buildPacket();
			HmBeCommunicationUtil.sendRequest(event);

			DebugUtil.topoDebugInfo("Device Model:" + AhConstantUtil.getString(Device.NAME, hiveAp.getHiveApModel())
							+ " Device Mac:"
							+ hiveAp.getMacAddress()
							+ " send PSE status query request successfully. Sequence Num("
							+ event.getSequenceNum() + ").");
		} catch (Exception e) {
			DebugUtil.topoDebugError("Device Mac:" + hiveAp.getMacAddress()
					+ " send PSE status query request Failed.", e);
		}
	}

	public static void sendBonjourGatewayMonitorQuery(HiveAp hiveAp) {
		try {

			BeBonjourGatewayEvent event = new BeBonjourGatewayEvent();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			event.setAp(hiveAp);
			event.setSequenceNum(sequenceNum);
			event.buildPacket();
			HmBeCommunicationUtil.sendRequest(event);

			DebugUtil.topoDebugInfo("Device Model:" + AhConstantUtil.getString(Device.NAME, hiveAp.getHiveApModel())
							+ " Device Mac:"
							+ hiveAp.getMacAddress()
							+ " send Bonjour Gateway Monitor query request successfully. Sequence Num("
							+ event.getSequenceNum() + ").");
		} catch (Exception e) {
			DebugUtil.topoDebugError("Device Mac:" + hiveAp.getMacAddress()
					+ " send Bonjour Gateway Monitor query request Failed.", e);
		}
	}

	public static void sendOsDetectionFileRequest(HiveAp hiveAp) {
		String host = NmsUtil.getRunningCapwapServer(hiveAp);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		String domainName = hiveAp.getOwner().getDomainName();
		String fileName = ImportTextFileAction.OS_VERSION_FILE_NAME_TAR;
		String filePath = AhDirTools.getOsDetectionDir()+ fileName;
		File OsDetectionFile = new File(filePath);
		if (!OsDetectionFile.exists()) {
			log.debug(NmsUtil.getUserMessage(
					"error.hiveap.OsDetection.file.notfound", new String[] {
							fileName, hiveAp.getHostName() }));
		}
		String filePath2 = AhDirTools.getOsDetectionDir()+
				ImportTextFileAction.OS_VERSION_FILE_NAME;
		if(OsDetectionFile.exists() && chkOsDetectionObject(hiveAp,filePath2)){
			String cli;
			if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
				cli = AhCliFactory.downloadOsDetectionFile(domainName, host, fileName, userName, password);
			} else {
				String proxy = hiveAp.getProxyName();
				int proxyPort = hiveAp.getProxyPort();
				String proxyLoginUser = hiveAp.getProxyUsername();
				String proxyLoginPwd = hiveAp.getProxyPassword();
				cli = AhCliFactory.downloadOsDetectionFileViaHttp(domainName, host, fileName,
						userName, password, proxy, proxyPort, proxyLoginUser, proxyLoginPwd);
			}
			// hide user name and password
			String displayCli = cli.replace(userName, "******").replace(password,
					"******");
			log.debug("sendOsDetectionFileRequest", "osDetection file request cli:"
					+ displayCli);
			try {
				String[] clis = { cli };
				int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
				BeCliEvent request = new BeCliEvent();
				request.setAp(hiveAp);
				request.setClis(clis);
				request.setSequenceNum(sequenceNum);
				request.buildPacket();
				HmBeCommunicationUtil.sendRequest(request);
				DebugUtil
						.topoDebugInfo("HiveAP:"
								+ hiveAp.getMacAddress()
								+ " send osDetection file request successfully. Sequence Num("
								+ sequenceNum + ").");
			} catch (Exception e) {
				DebugUtil.topoDebugError("HiveAP:" + hiveAp.getMacAddress()
						+ " send osDetection file request Failed.", e);
			}
		}
	}

	public static void sendOtpEventQuery(HiveAp hiveAp, byte mode,String password) {
		try {
			//OTP request only for Router.
			if(hiveAp.getDeviceType() != HiveAp.Device_TYPE_BRANCH_ROUTER){
				return;
			}
			BeOTPStatusEvent event = new BeOTPStatusEvent();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			event.setMode(mode);
			event.setPassword(password);
			event.setAp(hiveAp);
			event.setSequenceNum(sequenceNum);
			event.buildPacket();
			HmBeCommunicationUtil.sendRequest(event);

			DebugUtil.topoDebugInfo("Device Model:" + AhConstantUtil.getString(Device.NAME, hiveAp.getHiveApModel())
							+ " Device Mac:"
							+ hiveAp.getMacAddress()
							+ " send OTP query request successfully. Sequence Num("
							+ event.getSequenceNum() + ").");
		} catch (Exception e) {
			log.error("Device Mac:" + hiveAp.getMacAddress()
					+ " send OTP query request Failed.", e);
		}
	}

	public static BeVPNStatusEvent getVpnStatusRequest(HiveAp hiveAp)
			throws BeCommunicationEncodeException {
		BeVPNStatusEvent event = new BeVPNStatusEvent();
		int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
		event.setAp(hiveAp);
		event.setSequenceNum(sequenceNum);
		event.buildPacket();
		return event;
	}

	public static BeVPNStatusResultEvent getBeVPNStatusResult(
			BeCommunicationEvent c_event) {
		try {
			int msgType = c_event.getMsgType();
			int result = c_event.getResult();
			if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
				return (BeVPNStatusResultEvent) c_event;
			} else {
				DebugUtil
						.topoDebugError("getBeVPNStatusResult, cannot get VPN status result of HiveAp:"
								+ c_event.getApMac()
								+ ", msgType:"
								+ msgType
								+ ", result:" + result);
			}
		} catch (Exception e) {
			DebugUtil.topoDebugError("getBeVPNStatusResult", e);
		}
		return null;
	}

	public static void sendSwitchPortInfoQuery(HiveAp hiveAp) {
		try {
			BeSwitchPortInfoEvent event = new BeSwitchPortInfoEvent();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			event.setAp(hiveAp);
			event.setSequenceNum(sequenceNum);
			event.buildPacket();
			HmBeCommunicationUtil.sendRequest(event);

		} catch (Exception e) {
			DebugUtil.topoDebugError("Device Mac:" + hiveAp.getMacAddress()
					+ " send Switch port info query request Failed.", e);
		}
	}

	public static void sendSwitchPortStatsQuery(HiveAp hiveAp) {
		try {
			BeSwitchPortStatsEvent event = new BeSwitchPortStatsEvent();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			event.setAp(hiveAp);
			event.setSequenceNum(sequenceNum);
			event.buildPacket();
			HmBeCommunicationUtil.sendRequest(event);
		} catch (Exception e) {
			DebugUtil.topoDebugError("Device Mac:" + hiveAp.getMacAddress()
					+ " send switch port stats query request Failed.", e);
		}
	}

	
	public static void processAutoProvisioning(HiveAp hiveAp) {
		if(hiveAp == null){
			return;
		}

		HiveApAutoProvision provision = null;
		provision = hiveAp.getAutoProvisioningConfig();
		if(provision == null){
			provision = HmBeConfigUtil.getProvisionProcessor().getDeviceAPMapping().get(hiveAp.getMacAddress());
		}

		if(provision == null){
			return;
		}

		ProvisionProcessor.beginProcessing(hiveAp.getId(), provision);

		//remove mapping
		HmBeConfigUtil.getProvisionProcessor().getDeviceAPMapping().remove(hiveAp.getMacAddress());
	}

	public static void dealHiveAPRebootFailedEvent(BeRebootFailEvent r_event) {
		if (null == r_event) {
			return;
		}
		try {
			r_event.parsePacket();
			String failedMac = r_event.getApMac();
			HiveAp hiveAp = QueryUtil.findBoByAttribute(HiveAp.class,
					"macAddress", failedMac);
			if (null != hiveAp) {
				AhAppContainer.HmBe.setSystemLog(HmSystemLog.LEVEL_MAJOR,
						HmSystemLog.FEATURE_HIVEAPS, ""+NmsUtil.getOEMCustomer().getAccessPonitName()+" ("
								+ hiveAp.getHostName() + ") mac address:"
								+ hiveAp.getMacAddress() + " reboot failed.");
			}
		} catch (Exception e) {
			DebugUtil.topoDebugError(
					"Parse HiveAP reboot failed event error. reason:", e);
		}
	}

	public static BeGetStatisticEvent getNeighborStatisticEvent(HiveAp hiveAp) {
		if (null == hiveAp) {
			return null;
		}
		try {
			List<Byte> tables = new ArrayList<Byte>(1);
			tables.add(BeCommunicationConstant.STATTABLE_AHNEIGHBOR);
			return getStatisticEvent(hiveAp, tables);
		} catch (Exception e) {
			DebugUtil.topoDebugError(
					"build neighbor statistics event error for HiveAp:"
							+ hiveAp.getMacAddress(), e);
			return null;
		}
	}

	public static BeGetStatisticEvent getClientStatisticEvent(HiveAp hiveAp) {
		if (null == hiveAp) {
			return null;
		}
		try {
			List<Byte> tables = new ArrayList<Byte>(1);
			tables.add(BeCommunicationConstant.STATTABLE_AHASSOCIATION);
			return getStatisticEvent(hiveAp, tables);
		} catch (Exception e) {
			DebugUtil.topoDebugError(
					"build client statistics event error for HiveAp:"
							+ hiveAp.getMacAddress(), e);
			return null;
		}
	}

	/**
	 * Links information container Ethernet and wireless links, for now, we must
	 * use different table for getting Ethernet links and wireless links
	 * information. <br>
	 * Ethernet link: BeCommunicationConstant.STATTABLE_AHXIF <br>
	 * Wireless link: BeCommunicationConstant.STATTABLE_AHNEIGHBOR
	 *
	 * @param hiveAp
	 *            the hiveAP which need to get links information.
	 * @return statistic event
	 */
	public static BeGetStatisticEvent getLinksStatisticEvent(HiveAp hiveAp) {
		if (null == hiveAp) {
			return null;
		}
		try {
			List<Byte> tables = new ArrayList<Byte>(3);
			tables.add(BeCommunicationConstant.STATTABLE_AHNEIGHBOR);
			tables.add(BeCommunicationConstant.STATTABLE_AHXIF);
			tables.add(BeCommunicationConstant.STATTABLE_AHRADIOATTRIBUTE);
			return getStatisticEvent(hiveAp, tables);
		} catch (Exception e) {
			DebugUtil.topoDebugError(
					"build links statistics event error for HiveAp:"
							+ hiveAp.getMacAddress(), e);
			return null;
		}
	}

	public static BeGetStatisticEvent getStatisticEvent(HiveAp hiveAp,
			List<Byte> queryTables) throws Exception {
		Map<Byte, List<String>> statsTableInfoMap = HmBePerformUtil
				.createQueryStatsParams(queryTables);
		int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
		BeGetStatisticEvent s_event = new BeGetStatisticEvent();
		s_event.setAp(hiveAp);
		s_event.setSequenceNum(sequenceNum);
		s_event.setStatsTableIndexMap(statsTableInfoMap);
		s_event.buildPacket();
		return s_event;
	}

	public static StatisticResultsObject getStatisticResult(
			BeCommunicationEvent c_event) {
		try {
			HiveAp hiveAp;
			List<AhNeighbor> neighbors = null;
			List<AhXIf> xifs = null;
			List<AhRadioAttribute> radioAttributes = null;
			List<AhAssociation> associations = null;
			int msgType = c_event.getMsgType();
			if (msgType == BeCommunicationConstant.MESSAGETYPE_GETSTATISTICRSP) {
				hiveAp = c_event.getAp();
			} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_STATISTICRESULT) {
				BeStatisticResultEvent r_event = (BeStatisticResultEvent) c_event;
				r_event.parsePacket();
				// get hiveAP after parse!!
				hiveAp = r_event.getAp();
				Map<Byte, List<HmBo>> results = r_event.getStatsRowData();
				neighbors = (List) results
						.get(BeCommunicationConstant.STATTABLE_AHNEIGHBOR);
				xifs = (List) results
						.get(BeCommunicationConstant.STATTABLE_AHXIF);
				radioAttributes = (List) results
						.get(BeCommunicationConstant.STATTABLE_AHRADIOATTRIBUTE);
				associations = (List) results
						.get(BeCommunicationConstant.STATTABLE_AHASSOCIATION);
			} else {
				DebugUtil
						.topoDebugError("Unknown message Type in the statistic response of HiveAp:"
								+ c_event.getApMac());
				hiveAp = c_event.getAp();
			}
			if (null != hiveAp) {
				StatisticResultsObject results = new StatisticResultsObject(
						hiveAp);
				results.setNeighbors(neighbors);
				results.setXifs(xifs);
				results.setRadioAttributes(radioAttributes);
				results.setAssociations(associations);
				return results;
			}
		} catch (Exception e) {
			DebugUtil.topoDebugError("getStatisticResult", e);
		}
		return null;
	}

	public static BePOEStatusEvent getPoERequestEvent(HiveAp hiveAp)
			throws Exception {
		int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
		BePOEStatusEvent event = new BePOEStatusEvent();
		event.setAp(hiveAp);
		event.setSequenceNum(sequenceNum);
		event.buildPacket();
		return event;
	}

	public static BePOEStatusResultEvent getPoEEventResult(
			BeCommunicationEvent c_event) {
		try {
			int msgType = c_event.getMsgType();
			int result = c_event.getResult();
			if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
				return (BePOEStatusResultEvent) c_event;
			} else {
				DebugUtil
						.topoDebugError("BePOEStatusResultEvent, cannot get PoE status result of HiveAp:"
								+ c_event.getApMac()
								+ ", msgType:"
								+ msgType
								+ ", result:" + result);
			}
		} catch (Exception e) {
			DebugUtil.topoDebugError("getPoEEventResult", e);
		}
		return null;
	}

	/**
	 * Get an array of location plus MapContainer object.
	 *
	 * @param combinedLocation
	 *            an String location plus map name values.
	 * @param domain
	 *            the location parsed belongs to.
	 * @return an array combined location and map container.
	 */
	public static Object[] separateLocationAndMap(String combinedLocation,
			HmDomain domain) {
		Object[] locationAndMap = { null, null };

		if (null != domain && combinedLocation != null
				&& !combinedLocation.trim().equals("")) {
			String combinedString = combinedLocation;

			// Get rid of the double quotes beside the combined string given as
			// argument before parsing.
			if (combinedString.startsWith("\"")
					&& combinedString.endsWith("\"")) {
				combinedString = combinedString.substring(1, combinedString
						.length() - 1);
			}

			// Check the existence of '@'.
			int pos = combinedString.indexOf("@");

			if (pos == 0) {
				// Starts with '@'. The substring(1) is the map container name.
				locationAndMap[1] = getMapContainerByName(combinedString
						.substring(1), domain);
			} else if (pos > 0) {
				// Satisfies with the formation : location + '@' + map;
				locationAndMap[0] = combinedString.substring(0, pos);
				locationAndMap[1] = getMapContainerByName(combinedString
						.substring(pos + 1), domain);
			} else {
				// '@' does not exist. Considering it as a map container if an
				// actual 'MapContainerNode' can be looked up with the same map
				// name as the given string, otherwise regarding it as a
				// 'location'.
				MapContainerNode mapContainer = getMapContainerByName(
						combinedString, domain);

				if (mapContainer == null) {
					locationAndMap[0] = combinedString;
				} else {
					locationAndMap[1] = mapContainer;
				}
			}
		}

		return locationAndMap;
	}

	private static MapContainerNode getMapContainerByName(String mapName,
			HmDomain domain) {
		MapContainerNode candidate = null;

		if (!MapMgmt.ROOT_MAP_NAME.equals(mapName) && !MapMgmt.VHM_ROOT_MAP_NAME.equals(mapName)) {
			List<MapContainerNode> mapContainers = QueryUtil.executeQuery(MapContainerNode.class, null, new FilterParams("mapName = :s1 and mapType != :s2 and owner = :s3", new Object[] { mapName, MapContainerNode.MAP_TYPE_BUILDING, domain }), 1);

			if (!mapContainers.isEmpty()) {
				candidate = mapContainers.get(0);
			}
		}

		return candidate;
	}

	/**
	 * send request to capwap for simulate ap.
	 *
	 * @param apModel -
	 * @param productName -
	 * @param apNumber -
	 * @param clientInfo -
	 * @param vhm -
	 * @return -
	 */
	public static String simulateAP(short apModel, String productName,
			int apNumber, String clientInfo, HmDomain vhm) {
		String error_message = null;

		SimulateHiveAP simulateAP = new SimulateHiveAP();
		simulateAP.setSimulateNumber((short) apNumber);
		simulateAP.setVhmName(vhm.getDomainName());
		simulateAP.setClientInfo(clientInfo);
		simulateAP.setProductName(productName);
		simulateAP.setApModel(apModel);
		simulateAP.setTimezone((byte) (vhm.getTimeZone().getOffset(
				System.currentTimeMillis()) / 3600000 + 13));

		List<SimulateHiveAP> apList = new ArrayList<SimulateHiveAP>();
		apList.add(simulateAP);

		BeSimulateHiveAPEvent req = new BeSimulateHiveAPEvent();
		req.setSimulateAPList(apList);

		try {
			req.buildPacket();
		} catch (Exception e) {
			log.error("simulateAP", "build packet error", e);

			return "build packet error.";
		}

		BeCommunicationEvent rsp = HmBeCommunicationUtil.sendSyncRequest(req,
				120);
		if (rsp == null) {
			return "communicate with capwap error.";
		}

		if (rsp.getMsgType() != BeCommunicationConstant.MESSAGETYPE_SIMULATEHIVEAPRSP) {
			return "communication module internal error.";
		}

		BeSimulateHiveAPEvent simulateRsp = (BeSimulateHiveAPEvent) rsp;
		switch (simulateRsp.getResult()) {
		case BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE:
			return "communicate with capwap error.";

		case BeCommunicationConstant.RESULTTYPE_NOFSM:
		case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN:
		case BeCommunicationConstant.RESULTTYPE_UNKNOWNMSG:
		case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
		case BeCommunicationConstant.RESULTTYPE_MESSAGELENEXCEEDLIMIT:
		case BeCommunicationConstant.RESULTTYPE_RESOUCEBUSY:
			return "capwap internal error.";

		case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
			return "request is time out.";

		default:
			break;
		}

		return error_message;
	}

	public static String isDomainAllowManageRealHiveAP(Long domainId,
			int countMore, boolean cvgDevice) {
		String errorMsg = null;
		HmDomain domain = QueryUtil.findBoById(HmDomain.class,
				domainId);
		if (!cvgDevice) {
			domain.computeManagedApNum();
		}

		if (domain.getRunStatus() == HmDomain.DOMAIN_DEFAULT_STATUS) {
			// Device type is CVG
			if (cvgDevice) {
				int supportNum = domain.getMaxCvgSupportNum();
				int usedNumber;
				if (domain.isHomeDomain() || !NmsUtil.isHostedHMApplication()) {
					usedNumber = (int) QueryUtil.findRowCount(HiveAp.class, new FilterParams(
						"manageStatus=:s1 AND simulated=:s2 AND deviceType = :s3",
						new Object[] { HiveAp.STATUS_MANAGED, false, HiveAp.Device_TYPE_VPN_GATEWAY }));
				} else {
					usedNumber = (int) QueryUtil.findRowCount(HiveAp.class, new FilterParams(
						"manageStatus=:s1 AND simulated=:s2 AND deviceType = :s3 AND owner.id = :s4",
						new Object[] { HiveAp.STATUS_MANAGED, false, HiveAp.Device_TYPE_VPN_GATEWAY, domainId }));
				}
				if (supportNum <= usedNumber) {
					if (domain.isHomeDomain() || !NmsUtil.isHostedHMApplication()) {
						errorMsg = MgrUtil
								.getUserMessage("error.cvgAccepted.outofLincense.maximum");
					} else {
						errorMsg = MgrUtil.getUserMessage(
								"error.vhm.cvgAccepted.outofLincense.maximum",
								domain.getDomainName());
					}
				} else if (supportNum < usedNumber + countMore) {
					errorMsg = MgrUtil.getUserMessage(
							"info.license.cvgCount.approachMax", String
									.valueOf(supportNum - usedNumber));
				}
			} else {
				int supportNum = domain.getMaxApSupportNum();
				int existNum = domain.getManagedApNum();
				if (domain.isManagedApNumFull()) {
					if (HmDomain.HOME_DOMAIN.equals(domain.getDomainName())) {
						errorMsg = MgrUtil
								.getUserMessage("error.hiveApAccepted.outofLincense.maximum");
					} else {
						errorMsg = MgrUtil.getUserMessage(
								"error.vhm.hiveApAccepted.outofLincense.maximum",
								domain.getDomainName());
					}
				} else if (supportNum < existNum + countMore) {
					errorMsg = MgrUtil.getUserMessage(
							"info.license.hiveApCount.approachMax", String
									.valueOf(supportNum - existNum));
				}
			}
		} else {
			errorMsg = MgrUtil.getUserMessage("error.vhm.not.regular.state");
		}
		return errorMsg;
	}

	public static String isDomainAllowManageSimHiveAP(Long domainId,
			int countMore) {
		String errorMsg = null;
		HmDomain domain = QueryUtil.findBoById(HmDomain.class,
				domainId);
		domain.computeManagedSimApNum();
		if (domain.getRunStatus() == HmDomain.DOMAIN_DEFAULT_STATUS) {
			int supportNum = domain.getMaxSimuAp();
			int existNum = domain.getManagedSimApNum();
			if (domain.isManagedSimApNumFull()) {
				if (HmDomain.HOME_DOMAIN.equals(domain.getDomainName())) {
					errorMsg = MgrUtil
							.getUserMessage("error.hiveApAccepted.sim.outofLincense.maximum");
				} else {
					errorMsg = MgrUtil
							.getUserMessage(
									"error.vhm.hiveApAccepted.sim.outofLincense.maximum",
									domain.getDomainName());
				}
			} else if ((supportNum - existNum) <= countMore) {
				errorMsg = MgrUtil.getUserMessage(
						"info.license.sim.hiveApCount.approachMax", String
								.valueOf(supportNum - existNum));
			}
		} else {
			errorMsg = MgrUtil.getUserMessage("error.vhm.not.regular.state");
		}
		return errorMsg;
	}

	public static void triggerNewHiveAPStatusToManaged(HiveAp hiveAp) {
		if (null != hiveAp && hiveAp.getManageStatus() == HiveAp.STATUS_NEW) {
			// check license
			String error;
			if (hiveAp.isSimulated()) {
				error = isDomainAllowManageSimHiveAP(hiveAp.getOwner().getId(),
						1);
			} else {
				error = isDomainAllowManageRealHiveAP(hiveAp.getOwner().getId(), 1,
						hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
			}

			if (null != error && !"".equals(error.trim())) {
				log.error("triggerNewHiveAPStatusToManaged", "HiveAP:"
						+ hiveAp.getMacAddress()
						+ " cannot change to managed, since license overflow.");
				return;
			}
			try {
				// update managed status
				short oldManagedStatus = hiveAp.getManageStatus();
				hiveAp.setManageStatus(HiveAp.STATUS_MANAGED);
				BoMgmt.getMapMgmt().updateHiveApWithPropagation(hiveAp,
						hiveAp.getMapContainer(), hiveAp.isConnected(),
						oldManagedStatus);
				// send HiveAP manage status changed event.
				HmBeEventUtil
						.eventGenerated(new HiveApManageStatusChangedEvent(
								hiveAp, oldManagedStatus));
			} catch (Exception e) {
				log.error("triggerNewHiveAPStatusToManaged", "update error", e);
			}
		}
	}

	private static void sendTeacherViewRequest(HiveAp hiveAp) {
		if (hiveAp == null) {
			return;
		}

		if (!HmBeMiscUtil.hasClearClassRequest(hiveAp.getIpAddress())) {
			return;
		}

		List<ClearClassRequest> sentList = new ArrayList<ClearClassRequest>();
		BeTeacherViewClassInfoEvent event = new BeTeacherViewClassInfoEvent();
		int seqNum;
		event.setApMac(hiveAp.getMacAddress());
		List<String> classInfo = new ArrayList<String>();

		/*
		 * send CAPWAP request event
		 */
		for (ClearClassRequest request : HmBeMiscUtil
				.getClearClassRequest(hiveAp.getIpAddress())) {
			if (request == null) {
				continue;
			}

			classInfo.add(request.getClassId());
		}

		event.setRemove_classIDList(classInfo);

		try {
			event.buildPacket();
			seqNum = HmBeCommunicationUtil.sendRequest(event);
		} catch (Exception e) {
			log.error("Failed to sent clearing event to HiveAP - "
					+ hiveAp.getIpAddress() + ".");
			seqNum = BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED;
		}

		if (seqNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
			return;
		}

		/*
		 * remove sent request
		 */
		try {
			HmBeMiscUtil.removeClearClassRequests(hiveAp.getIpAddress(),
					sentList);
		} catch (Exception e) {
			log.error("Failed to remove requests of clearing class.", e);
		}
	}

	public static boolean chkOsDetectionObject(HiveAp hiveAp,String filePath){
		String hmVerson = "0.1";
		String osVerson = "0.1";
		try {
			hmVerson = NmsUtil.getOSOptionFileVersion(filePath);
			String cli = AhCliFactory.showOsDetectionVersion();
			BeCommunicationEvent result = sendSyncCliRequest(hiveAp, new String[] { cli },
							BeCliEvent.CLITYPE_NORMAL, 60);
			osVerson = BeTopoModuleUtil.parseCliRequestResult(result);
			if (null != osVerson && osVerson.contains("=")) {
				int i = osVerson.indexOf("=");
				osVerson = osVerson.substring(i+1,osVerson.indexOf("<"));
				return Float.valueOf(hmVerson) > Float.valueOf(osVerson);
			}
		} catch (IOException e) {
			log.error("Failed to send requests of osDetection.", e);
		} catch (Exception e) {
			log.error("Failed to send requests of osDetection.", e);
		}
		return false;

	}
	
	public static void sendRouterLTEVZInfoQuery(HiveAp hiveAp) {
		if(!hiveAp.isUsbAsCellularModem()){
			return;
	    }
		try {
			BeRouterLTEVZInfoEvent event = new BeRouterLTEVZInfoEvent();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			event.setAp(hiveAp);
			event.setSequenceNum(sequenceNum);
			event.buildPacket();
			HmBeCommunicationUtil.sendRequest(event);

		} catch (Exception e) {
			DebugUtil.topoDebugError("Device Mac:" + hiveAp.getMacAddress()
					+ " send Router LTE VZ info query request Failed.", e);
		}
	}
	
	public static <T extends BeCommunicationEvent> void sendQueryEvent(Class<T> eventClass, HiveAp hiveAp) {
		try {
			BeCommunicationEvent event = eventClass.newInstance();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			event.setAp(hiveAp);
			event.setSequenceNum(sequenceNum);
			event.buildPacket();
			HmBeCommunicationUtil.sendRequest(event);
		} catch (Exception e) {
			DebugUtil.topoDebugError("Device Mac:" + hiveAp.getMacAddress()
					+ " send "+eventClass.getName()+" query request Failed.", e);
		}
	}
}
