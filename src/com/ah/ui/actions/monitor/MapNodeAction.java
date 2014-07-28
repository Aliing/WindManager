package com.ah.ui.actions.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.admin.adminOperateImpl.AhHiveAPTech;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeEventUtil;
import com.ah.be.app.HmBePerformUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeCWPDirectoryEvent;
import com.ah.be.communication.event.BeCWPDirectoryResultEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.communication.event.BeGetStatisticEvent;
import com.ah.be.communication.event.BeInterferenceMapResultEvent;
import com.ah.be.config.BeConfigModule;
import com.ah.be.db.configuration.ConfigAuditProcessor;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.topo.BeTopoModuleParameters;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.be.topo.StatisticResultsObject;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhDeviceRebootHistory;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.wlan.RadioProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.hiveap.HiveApMonitor;
import com.ah.util.CheckItem;
import com.ah.util.EnumConstUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.coder.AhEncoder;
import com.ah.util.devices.impl.Device;

public class MapNodeAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			MapNodeAction.class.getSimpleName());

	private boolean noLed;

	private String ledColor;

	private String ledBlink;
	
	//get parent operation from HiveApAction
	private String parentOperation;

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
		    
		    if(StringUtils.isNotBlank(menuText)) {
		        menuText = escapseMenuText(menuText);
		    }
		    
			if ("retrieveClientInfo".equals(operation)) {
				log.info("execute",
						"retrieve client information of : leafNodeId"
								+ leafNodeId + ", hiveApId:" + hiveApId);
				getHiveApClientInfoViaCAPWAP();
				return "json";
			} else if ("syncClientsFromAPs".equals(operation)) {
				log.info("execute", "Synchronize client list from aps.");
				queryHiveAPClients();
				return "json";
			} else if ("clientFirstPage".equals(operation)) {
				clientInfoPagingOperation(OPERATION_FIRST_PAGE);
				return "json";
			} else if ("clientPreviousPage".equals(operation)) {
				clientInfoPagingOperation(OPERATION_PREVIOUS_PAGE);
				return "json";
			} else if ("clientNextPage".equals(operation)) {
				clientInfoPagingOperation(OPERATION_NEXT_PAGE);
				return "json";
			} else if ("clientLastPage".equals(operation)) {
				clientInfoPagingOperation(OPERATION_LAST_PAGE);
				return "json";
			} else if ("clientGotoPage".equals(operation)) {
				clientInfoPagingOperation(OPERATION_GOTO_PAGE);
				return "json";
			} else if ("retrieveNeighborInfo".equals(operation)) {
				log.info("execute",
						"retrieve neighbor information of : leafNodeId"
								+ leafNodeId + ", hiveApId:" + hiveApId);
				getHiveApNeighborInfoViaCAPWAP();
				return "json";
			} else if ("fetchImageVer".equals(operation)) {
				fetchImageVer();
				return "json";
			} else if ("requestSingleItemCli".equals(operation)) {
				log.info("execute", "click menuItem : " + menuText
						+ ",leafNodeId : " + leafNodeId + ", hiveApId:"
						+ hiveApId + ",value1:" + value1);
				jsonObject = getSingleHiveApCliInfo(getSelectedHiveAp(),
						menuText, value1);
				return "json";
			} else if ("requestMultipleItemCli".equals(operation)) {
				log.info("execute", "operation:" + operation + ",leafNodeId : "
						+ leafNodeId + ", allSelectedId:" + getAllSelectedIds());
				Set<Long> selectedIds = null;
				if (null != leafNodeId) {// from topology map
					HiveAp hiveAp = getSelectedHiveAp();
					if (null != hiveAp) {
						selectedIds = new HashSet<Long>();
						selectedIds.add(hiveAp.getId());
					}
				} else {// from managed list view
					selectedIds = getAllSelectedIds();
				}
				
				jsonObject = getMultipleHiveApCliInfo(selectedIds,
						menuText, value1);
				return "json";
			} else if ("clearRadsecCerts".equals(operation) || 
					"disableHiveUIConfig".equals(operation)) {
				log.info("execute", "operation:" + operation + ",leafNodeId : "
						+ leafNodeId + ", allSelectedId:" + getAllSelectedIds());
				Set<Long> selectedIds = null;
				if (null != leafNodeId) {// from topology map
					HiveAp hiveAp = getSelectedHiveAp();
					if (null != hiveAp) {
						selectedIds = new HashSet<Long>();
						selectedIds.add(hiveAp.getId());
					}
				} else {// from managed list view
					selectedIds = getAllSelectedIds();
				}
				jsonObject = getMultipleHiveApCliInfo(selectedIds,
						menuText, value1);
				return "json";
			} else if ("initmulticastMonitor".equals(operation)) {
				HiveAp oneAp = getSelectedHiveAp();
				if (oneAp!=null) {
					stringForTitle = oneAp.getHostName();
					if (hiveApId==null) {
						hiveApId = oneAp.getId();
					}
				} else {
					stringForTitle = "Unknown";
				}
				return "multicastMonitor";
			} else if ("showMulticastMonitor".equals(operation)) {
				log.info("execute", "MulticastMonitor : " + menuText
						+ ",leafNodeId : " + leafNodeId + ", hiveApId:"
						+ hiveApId + ",value1:" + value1);
				jsonObject = getMulticastMonitorCliInfo(
						getSelectedHiveAp(),
						getText("topology.menu.diagnostics.showmulticastmonitor"),
						value1, wifiInterfaceId);
				return "json";
			} else if ("alarm".equals(operation)) {
				log.info("execute", "leafNodeId : " + leafNodeId
						+ ", hiveApId:" + hiveApId);
				getNodeId();
				return "json";
			} else if ("retrieveApInfo".equals(operation)) {
				log.info("execute", "target node Id :" + leafNodeId);
				getApInfo();
				return "json";
			} else if ("syncSsidInfo".equals(operation)) {
				log.info("execute",
						"synchronize SSID info from device, hiveApId :"
								+ hiveApId);
				updateSsidInfo();
				return "json";
			} else if ("syncApInfo".equals(operation)) {
				log.info("execute",
						"synchronize ap info from device, hiveApId :"
								+ hiveApId);
				updateApInfo();
				return "json";
			} else if ("redirect".equals(operation)) {
				log.info("execute", "redirect to map :" + id);
				MgrUtil.setSessionAttribute(SessionKeys.SELECTED_MAP_ID, id);
				return SUCCESS;
			} else if ("lldpcdpclear".equals(operation)) {
				boolean isSuccess = false;
				String resultMsg;

				HiveAp hiveAp = getSelectedHiveAp();
				try {
					resultMsg = validateSelectedHiveAp(hiveAp, "3.2.0.0");
					if (null == resultMsg) {
						isSuccess = lldpCdpClear(hiveAp);
						resultMsg = isSuccess ? "Clear LLDP/CDP table from "
								+ NmsUtil.getOEMCustomer().getAccessPonitName(
										hiveAp.getDeviceType())
								+ " successfully."
								: "Clear LLDP/CDP table from "
										+ NmsUtil.getOEMCustomer()
												.getAccessPonitName(
														hiveAp.getDeviceType())
										+ " failed.";
					}
				} catch (Exception e) {
					log.error("execute",
							"Clear LLDP/CDP table catch exception", e);
					resultMsg = "Clear LLDP/CDP table from "
							+ NmsUtil.getOEMCustomer().getAccessPonitName(
									hiveAp.getDeviceType()) + " error.";
				}

				jsonObject = new JSONObject();
				jsonObject.put("result", isSuccess);
				jsonObject.put("rspMessage", resultMsg);
				return "json";
			} else if ("configurationAudit".equals(operation)) {
				log.info("execute", "operation:" + operation + ",leafNodeId : "
						+ leafNodeId + ", hiveApId:" + hiveApId);
				getAuditMessage();
				return "json";
			} else if ("retrieveCwpDirectory".equals(operation)) {
				log.info("execute", "operation:" + operation + ",leafNodeId : "
						+ leafNodeId + ", allSelectedId:" + getAllSelectedIds());
				getCwpDirectory();
				return "json";
			} else if ("removeCwpDirectory".equals(operation)) {
				log.info("execute", "operation:" + operation + ",leafNodeId : "
						+ leafNodeId + ", allSelectedId:" + getAllSelectedIds());
				removeCwpDirectory();
				return "json";
			} else if ("requestTech".equals(operation)) {
				log.info("execute", "operation:" + operation + ",leafNodeId : "
						+ leafNodeId + ", allSelectedId:" + getAllSelectedIds());
				generateRebootHistoryCsvFile();
				boolean result = requestTech();
				if (result) {
					dumpFileName = AhHiveAPTech.FINAL_TAR_NAME;
					log.info("requestTech", "show tech zip file path:"
							+ inputPath);
					return "download";
				}
				if (null != leafNodeId) {
					return SUCCESS;// map section
				} else {
					return "hiveApPage";// managed HiveAP list view section
				}
			}else if ("requestHiveAPModel".equals(operation)) {
				jsonObject = new JSONObject();
				HiveAp hiveAp = getSelectedHiveAp();
				if (hiveAp == null) { // errors happen
					return "json";
				}
				// Boolean enabled =
				// AhConstantUtil.isTrueAll(Device.SUPPORTED_LOCATE ,
				// hiveAp.getHiveApModel());
				// if(null == enabled || enabled){ //support locate
				jsonObject.put("model", hiveAp.getHiveApModel());
				jsonObject.put("modelStr",
						HiveAp.getModelEnumString(hiveAp.getHiveApModel()));
				// }
				// else{
				// jsonObject.put("e",
				// MgrUtil.getUserMessage("error.locate.ap.status.off",
				// AhConstantUtil.getString(Device.NAME,
				// hiveAp.getHiveApModel())));
				// }
				return "json";
			} else if ("controlLedOfAP".equals(operation)) {
				jsonObject = new JSONObject();
				HiveAp hiveAp = getSelectedHiveAp();
				if (hiveAp == null) {
					jsonObject.put("msg",
							MgrUtil.getUserMessage("error.cli.object.notfind"));
					return "json";
				}
				if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
					jsonObject.put("msg", MgrUtil
							.getUserMessage("error.hiveAp.notManaged.request"));
					return "json";
				}

				boolean result = sendLedControlCli(hiveAp);
				if (!result) {
					jsonObject.put("msg", "Unable to update LED status.");
				}

				return "json";
			} else if("spectralAnalysisSupCheck".equals(operation)){
				jsonObject = new JSONObject();
				HiveAp hiveAp = getSelectedHiveAp();
				if (hiveAp == null) {
					jsonObject.put("v", 0);
					jsonObject.put("m",
							MgrUtil.getUserMessage("error.cli.object.notfind"));
					return "json";
				}
				
				Boolean isTrue = AhConstantUtil.isTrueAll( Device.SUPPORTED_SPECTRUMANALYSIS, hiveAp.getHiveApModel() );
				if (hiveAp.getSoftVer().compareTo("4.0.0.0") < 0
						|| (isTrue == null || !isTrue)) {
					jsonObject.put("v", 0);
					jsonObject.put("m", MgrUtil.getUserMessage(
							"error.spn.no.support",
							AhConstantUtil.getString(Device.NAME,
									hiveAp.getHiveApModel())));
					return "json";
				} else if (hiveAp.getSoftVer().compareTo("6.1.5.0") < 0) {
					jsonObject.put("v", 2);
					return "json";
				}else{
					jsonObject.put("v", 1);
					return "json";
				}
			} else if ("fetchHiveApInterfaceInfo".equals(operation)) {
				jsonObject = new JSONObject();
				HiveAp hiveAp = getSelectedHiveAp();
				if (hiveAp == null) {
					jsonObject.put("v", 0);
					jsonObject.put("m",
							MgrUtil.getUserMessage("error.cli.object.notfind"));
					return "json";
				}
				if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
					jsonObject.put("v", 0);
					jsonObject.put("m", MgrUtil
							.getUserMessage("error.hiveAp.notManaged.request"));
					return "json";
				}
				
				Boolean isTrue = AhConstantUtil.isTrueAll( Device.SUPPORTED_SPECTRUMANALYSIS, hiveAp.getHiveApModel() );
				if (hiveAp.getSoftVer().compareTo("4.0.0.0") < 0
						|| (isTrue == null || !isTrue)) {
					jsonObject.put("v", 0);
					jsonObject.put("m", MgrUtil.getUserMessage(
							"error.spn.no.support",
							AhConstantUtil.getString(Device.NAME,
									hiveAp.getHiveApModel())));
					return "json";
				}

				if (hiveAp.getConfigTemplate() == null
						|| hiveAp.getConfigTemplate().getSsidInterfaces() == null
						|| hiveAp.getConfigTemplate().getSsidInterfaces()
								.size() <= 4) {
					jsonObject.put("v", 0);
					jsonObject.put("m",
							MgrUtil.getUserMessage("error.spn.no.ssid"));
					return "json";
				}

				if (AhConstantUtil.isTrueAll(Device.IS_DUALBAND,
						hiveAp.getHiveApModel())) {
					if (hiveAp.getWifi0() != null
							&& hiveAp.getWifi0().getAdminState() == AhInterface.ADMIN_STATE_UP) {
						if (hiveAp.getWifi0().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG
								|| hiveAp.getWifi0().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG) {
							jsonObject.put("v", 1);
							if (hiveAp.getRegionCode() == BeAPConnectEvent.REGION_CODE_FCC) {
								jsonObject.put("w0", 11);
							}
						} else {
							jsonObject.put("v", 2);
						}
					} else {
						jsonObject.put("v", 0);
						jsonObject.put("m", MgrUtil
								.getUserMessage("error.spn.if.wifi0.down"));
					}
				} else {
					if (hiveAp.getWifi0() != null
							&& hiveAp.getWifi0().getAdminState() == AhInterface.ADMIN_STATE_UP
							&& hiveAp.getWifi0().getOperationMode()!=AhInterface.OPERATION_MODE_SENSOR) {
						jsonObject.put("v", 1);
						if (hiveAp.getRegionCode() == BeAPConnectEvent.REGION_CODE_FCC) {
							jsonObject.put("w0", 11);
						}
					} else if (hiveAp.getWifi1() != null
							&& hiveAp.getWifi1().getAdminState() == AhInterface.ADMIN_STATE_UP) {
						jsonObject.put("v", 2);
					} else {
						jsonObject.put("v", 0);
						jsonObject.put("m", MgrUtil
								.getUserMessage("error.spn.if.both.down"));
					}
				}
				return "json";
			}
			return SUCCESS;
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return SUCCESS;
		}
	}

	private String selectedAPIdStr;

	public String getSelectedAPIdStr() {
		return selectedAPIdStr;
	}

	public void setSelectedAPIdStr(String selectedAPIdStr) {
		this.selectedAPIdStr = selectedAPIdStr;
	}

	private List<Long> getSelectedAPIds() {
		String[] ids = selectedAPIdStr.split(",");
		List<Long> idList = new ArrayList<Long>(ids.length);
		for (String str_id : ids) {
			idList.add(Long.parseLong(str_id));
		}

		return idList;
	}

	private List<SimpleHiveAp> getSelectedAPList() {
		if (allItemsSelected) {
			return CacheMgmt.getInstance().getManagedApList(getDomainId());
		} else {
			List<Long> selectedIDs = getSelectedAPIds();
			List<SimpleHiveAp> apList = new ArrayList<SimpleHiveAp>(
					selectedIDs.size());

			for (Long id : selectedIDs) {
				try {
					HiveAp ap = findBoById(HiveAp.class, id);
					apList.add(CacheMgmt.getInstance().getSimpleHiveAp(ap));
				} catch (Exception e) {
					log.error("getSelectedAPList", "catch exception", e);
				}
			}

			return apList;
		}
	}

	private boolean sendLedControlCli(HiveAp hiveAp) {
		short model = hiveAp.getHiveApModel();
		List<String> clis = new ArrayList<String>();
		switch (model) {

		case HiveAp.HIVEAP_MODEL_20:
		case HiveAp.HIVEAP_MODEL_28: {
			clis.add(AhCliFactory.getLedIndexColorBlinkCli("1", ledColor,
					ledBlink));
			clis.add(AhCliFactory.getLedIndexColorBlinkCli("2", ledColor,
					ledBlink));
			clis.add(AhCliFactory.getLedIndexColorBlinkCli("3", ledColor,
					ledBlink));
			clis.add(AhCliFactory.getLedIndexColorBlinkCli("4", ledColor,
					ledBlink));

			break;
		}

		case HiveAp.HIVEAP_MODEL_380: {
			clis.add(AhCliFactory.getLedIndexColorBlinkCli("1", ledColor,
					ledBlink));
			clis.add(AhCliFactory.getLedIndexColorBlinkCli("2", ledColor,
					ledBlink));
			clis.add(AhCliFactory.getLedIndexColorBlinkCli("3", ledColor,
					ledBlink));
			clis.add(AhCliFactory.getLedIndexColorBlinkCli("4", ledColor,
					ledBlink));
			clis.add(AhCliFactory.getLedIndexColorBlinkCli("5", ledColor,
					ledBlink));

			break;
		}
		
		case HiveAp.HIVEAP_MODEL_BR200:
		case HiveAp.HIVEAP_MODEL_BR200_WP:
		case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
			if (noLed) {
				clis.add(AhCliFactory.getNoLedCli());	
			} else {				
				if(NmsUtil.compareSoftwareVersion("6.1.2.0",hiveAp.getSoftVer()) <= 0){
					clis.add(AhCliFactory.getLedSysColorBlinkCli(ledColor, ledBlink));		
				}else{
					clis.add(AhCliFactory.getLedColorBlinkCli(ledColor, ledBlink));		
				}
				
			}
			break;

		default:
			if (noLed) {
				clis.add(AhCliFactory.getNoLedCli());	
			} else {				
				clis.add(AhCliFactory.getLedColorBlinkCli(ledColor, ledBlink));		
			}
			break;
		}

		return sendCliRequestSync(hiveAp, clis.toArray(new String[clis.size()]));
	}

	private final String CACHEID_CLIENTINFO = "cacheid_clientinfo";
	private final String DEVICETYPE_MAP = "deviceTypeMap";

	private void queryHiveAPClients() throws Exception {
		jsonObject = new JSONObject();
		List<SimpleHiveAp> apList = getSelectedAPList();

		List<SimpleHiveAp> managedList = new ArrayList<SimpleHiveAp>();
		
		//used for record the device and it's type.
		Map<String, Short> deviceTypeMap = new HashMap<String,Short>();
		if (null != apList) {
			for (SimpleHiveAp ap : apList) {
				if (ap.getManageStatus() == HiveAp.STATUS_MANAGED) {
					managedList.add(ap);
					deviceTypeMap.put(ap.getMacAddress(), ap.getHiveApModel());
				}
			}
		}

		if ((null != apList && !apList.isEmpty()) && managedList.isEmpty()) {
			jsonObject.put("e",
					MgrUtil.getUserMessage("error.hiveAp.notManaged.request"));
			return;
		}

		List<AhAssociation> associateList = HmBePerformUtil
				.syncQueryHiveAPsClients(managedList);
		if (associateList == null) {
			jsonObject.put("e", "Unable to finish the request, maybe "
					+ NmsUtil.getOEMCustomer().getNmsName()
					+ " disconnect with CAPWAP, please try again later.");
			return;
		}

		CacheClientPaginator clientInfoPaginator = new CacheClientPaginator(
				associateList);
		clientInfoPaginator.init();

		Collection<JSONObject> jsonClients = getAllClient(clientInfoPaginator
				.getPageResult(),deviceTypeMap);
		if (null != jsonClients && jsonClients.size() > 0) {
			jsonObject.put("data", jsonClients);
			jsonObject.put("pageIndex", clientInfoPaginator.getPageIndex());
			jsonObject.put("pageCount", clientInfoPaginator.getPageCount());

			MgrUtil.setSessionAttribute(CACHEID_CLIENTINFO, clientInfoPaginator);
			MgrUtil.setSessionAttribute(DEVICETYPE_MAP, deviceTypeMap);
		} else {
			jsonObject.put("e",
					MgrUtil.getUserMessage("info.capwap.noClientInfo"));
		}
	}

	private void clientInfoPagingOperation(String operation) throws Exception {
		CacheClientPaginator clientInfoPaginator = (CacheClientPaginator) MgrUtil
				.getSessionAttribute(CACHEID_CLIENTINFO);
		Map<String, Short> deviceTypeMap =  (Map<String, Short>) MgrUtil.getSessionAttribute(DEVICETYPE_MAP);
		
		jsonObject = new JSONObject();
		if (clientInfoPaginator == null) {
			jsonObject.put("e", "No data found.");
			return;
		}

		if (operation.equals(OPERATION_FIRST_PAGE)) {
			clientInfoPaginator.firstPage();
		} else if (operation.equals(OPERATION_PREVIOUS_PAGE)) {
			clientInfoPaginator.previousPage();
		} else if (operation.equals(OPERATION_NEXT_PAGE)) {
			clientInfoPaginator.nextPage();
		} else if (operation.equals(OPERATION_LAST_PAGE)) {
			clientInfoPaginator.lastPage();
		} else if (operation.equals(OPERATION_GOTO_PAGE)) {
			clientInfoPaginator.setPageIndex(pageIndex);
			clientInfoPaginator.init();
		}

		Collection<JSONObject> jsonClients = getAllClient(clientInfoPaginator
				.getPageResult(),deviceTypeMap);
		if (null != jsonClients && jsonClients.size() > 0) {
			jsonObject.put("data", jsonClients);
			jsonObject.put("pageIndex", clientInfoPaginator.getPageIndex());
			jsonObject.put("pageCount", clientInfoPaginator.getPageCount());
		} else {
			jsonObject.put("e",
					MgrUtil.getUserMessage("info.capwap.noClientInfo"));
		}
	}

	public static String validateSelectedHiveAp(HiveAp hiveAp, String supportVer)
			throws Exception {
		String message;
		if (null == hiveAp) {
			message = MgrUtil.getUserMessage("error.cli.object.notfind");
		} else if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			message = MgrUtil.getUserMessage("error.hiveAp.notManaged.request");
		} else {
			boolean passed = checkVersionSupported(hiveAp, supportVer);
			if (!passed) {
				message = MgrUtil.getUserMessage(
						"error.hiveAp.feature.support.version",
						MgrUtil.getHiveOSDisplayVersion(supportVer));
			} else {
				message = null;
			}
		}
		return message;
	}

	public static boolean checkVersionSupported(HiveAp hiveAp, String version) {
		String softVer = hiveAp.getSoftVer();
		return softVer != null
				&& (NmsUtil.compareSoftwareVersion(version, softVer) <= 0);
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (null == bo) {
			return null;
		}
		if (bo instanceof MapLeafNode) {
			((MapLeafNode) bo).getHiveAp().getId();
		} else if (bo instanceof HiveAp) {
			HiveAp oneAp = (HiveAp) bo;
			if (oneAp.getConfigTemplate() != null) {
				oneAp.getConfigTemplate().getId();
				if (oneAp.getConfigTemplate().getSsidInterfaces() != null) {
					oneAp.getConfigTemplate().getSsidInterfaces().size();
				}
			}
		}

		return null;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MAP_VIEW);
	}

	private HiveAp getSelectedHiveAp() throws Exception {
		HiveAp hiveAp = null;
		if (null != hiveApId) {
			if ("fetchHiveApInterfaceInfo".equals(operation)) {
				hiveAp = QueryUtil.findBoById(HiveAp.class, hiveApId, this);
			} else {
				hiveAp = QueryUtil.findBoById(HiveAp.class, hiveApId);
			}
		} else {
			MapLeafNode leafNode = QueryUtil.findBoById(MapLeafNode.class,
					leafNodeId, this);
			if (null != leafNode) {
				hiveAp = leafNode.getHiveAp();
			}
		}
		return hiveAp;
	}

	// e: error message displayed above the panel
	// n: host name of the HiveAp
	// data : table data displayed in the panel table
	// via CAPWAP;
	protected void getHiveApClientInfoViaCAPWAP() throws Exception {
		jsonObject = new JSONObject();
		HiveAp hiveAp = getSelectedHiveAp();
		if (null == hiveAp) {
			// filled content value;
			jsonObject.put("e",
					MgrUtil.getUserMessage("error.cli.object.notfind"));
			return;
		}
		// filled host name value;
		jsonObject.put("h", hiveAp.getHostName());

		if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			jsonObject.put("e",
					MgrUtil.getUserMessage("error.hiveAp.notManaged.request"));
			return;
		}

		BeGetStatisticEvent request = BeTopoModuleUtil
				.getClientStatisticEvent(hiveAp);
		if (null == request) {
			jsonObject.put("e", MgrUtil
					.getUserMessage("error.capwap.client.unknown.reason"));
			return;
		}

		// add sequence number into client refresh cache.
		HmBePerformUtil.addStatsSerialNum4ClientRefresh(request
				.getSequenceNum());

		BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(
				request, BeTopoModuleParameters.POLLING_VIA_CAPWAP_TIMEOUT);
		if (null == response) {
			jsonObject.put("e", MgrUtil
					.getUserMessage("error.capwap.client.unknown.reason"));
			return;
		}

		StatisticResultsObject results = BeTopoModuleUtil
				.getStatisticResult(response);
		if (null == results || null == results.getAssociations()) {
			jsonObject.put("e",
					MgrUtil.getUserMessage("error.capwap.client.timeout"));
			return;
		}

		// put result event into queue
		HmBeEventUtil.eventGenerated(response);
		
		Map<String, Short> deviceTypeMap =  new HashMap<String,Short>();
		deviceTypeMap.put(hiveAp.getMacAddress(), hiveAp.getHiveApModel());
		
		Collection<JSONObject> jsonClients = getAllClient(results
				.getAssociations(),deviceTypeMap);
		if (null != jsonClients && jsonClients.size() > 0) {
			jsonObject.put("data", jsonClients);
		} else {
			jsonObject.put("e",
					MgrUtil.getUserMessage("info.capwap.noClientInfo"));
		}
	}

	private Collection<JSONObject> getAllClient(List<AhAssociation> clients, Map<String, Short> deviceTpyeMap)
			throws JSONException {
		Collection<JSONObject> jsonClients = new Vector<JSONObject>();
		for (AhAssociation assocDto : clients) {
			JSONObject jsonClient = new JSONObject();
			Collection<JSONObject> clientData = new Vector<JSONObject>();
			JSONObject data = new JSONObject();
			data.put("v", assocDto.getClientMac());
			clientData.add(data);
			data = new JSONObject();
			data.put("v", assocDto.getClientIP());
			clientData.add(data);
			data = new JSONObject();
			data.put("v", assocDto.getClientHostname());
			clientData.add(data);
			data = new JSONObject();
			data.put("v", assocDto.getApName());
			clientData.add(data);
			data = new JSONObject();
			data.put("v", assocDto.getClientLinkupTimeShow());
			clientData.add(data);
			
			if(null != deviceTpyeMap){
				if(HiveAp.isSwitchProduct(deviceTpyeMap.get(assocDto.getApMac()))){
					data = new JSONObject();
					data.put("v", "");
					clientData.add(data);
				} else {
					data = new JSONObject();
					data.put("v", assocDto.getClientRSSI4Show());
					clientData.add(data);
				}
			}else{
				data = new JSONObject();
				data.put("v", assocDto.getClientRSSI4Show());
				clientData.add(data);
			}

			data = new JSONObject();
			data.put("v", assocDto.getClientAuthMethodString());
			clientData.add(data);

			data = new JSONObject();
			data.put("v", assocDto.getClientEncryptionMethodString());
			clientData.add(data);

			data = new JSONObject();
			data.put("v", assocDto.getClientCWPUsedString());
			clientData.add(data);
			
			if(null != deviceTpyeMap){
				if(HiveAp.isSwitchProduct(deviceTpyeMap.get(assocDto.getApMac()))){
					data = new JSONObject();
					data.put("v", "");
					clientData.add(data);

					data = new JSONObject();
					data.put("v", "");
					clientData.add(data);
				}else{
					data = new JSONObject();
					data.put("v", assocDto.getClientMacPtlString());
					clientData.add(data);

					data = new JSONObject();
					data.put("v", assocDto.getClientSSID());
					clientData.add(data);
				}
			}else{
				data = new JSONObject();
				data.put("v", assocDto.getClientMacPtlString());
				clientData.add(data);

				data = new JSONObject();
				data.put("v", assocDto.getClientSSID());
				clientData.add(data);
			}

			data = new JSONObject();
			data.put("v", assocDto.getClientVLAN());
			clientData.add(data);

			data = new JSONObject();
			data.put("v", assocDto.getClientUserProfId());
			clientData.add(data);

			if(null != deviceTpyeMap){
				if(HiveAp.isSwitchProduct(deviceTpyeMap.get(assocDto.getApMac()))){
					data = new JSONObject();
					data.put("v", "");
					clientData.add(data);
				}else{
					data = new JSONObject();
					data.put("v", assocDto.getClientChannel());
					clientData.add(data);
				}
			}else{
				data = new JSONObject();
				data.put("v", assocDto.getClientChannel());
				clientData.add(data);
			}

			data = new JSONObject();
			data.put("v", assocDto.getClientLastTxRate());
			clientData.add(data);

			jsonClient.put("rowData", clientData);
			jsonClients.add(jsonClient);
		}
		return jsonClients;
	}
	
	protected void fetchImageVer() throws Exception{
		jsonObject = new JSONObject();
		
		Set<Long> selectedIds = null;
		if (null != leafNodeId) {// from topology map
			HiveAp hiveAp = getSelectedHiveAp();
			if (null != hiveAp) {
				selectedIds = new HashSet<Long>();
				selectedIds.add(hiveAp.getId());
			}
		} else {// from managed list view
			selectedIds = getAllSelectedIds();
		}
		
		if (null == selectedIds || selectedIds.isEmpty()) {
			jsonObject.put("e",
					MgrUtil.getUserMessage("error.cli.object.notfind"));
		}
		HiveAp hiveAp = null;
		if (selectedIds.size() == 1) {
			Long id = null;
			for (Long hiveapId : selectedIds) {
				id = hiveapId;
			}
			hiveAp = QueryUtil.findBoById(HiveAp.class, id);
			jsonObject.put("isConnected",hiveAp.isConnected());
			if(!hiveAp.isConnected()){
				jsonObject.put("v",MgrUtil.getUserMessage("gotham_18.info.hiveos.detail.version.fetch.failed",MgrUtil.getUserMessage("error.capwap.server.nofsm")));
				return;
			} 
		} else {
			boolean flag = false;
			for (Long id : selectedIds) {
				HiveAp ap = QueryUtil.findBoById(HiveAp.class, id);
				if(ap.isConnected()){
					flag = true;
				}
			}
			jsonObject.put("isConnected",flag);
			if(!flag){
				jsonObject.put("v",MgrUtil.getUserMessage("gotham_18.info.hiveos.detail.version.fetch.failed",MgrUtil.getUserMessage("error.capwap.server.nofsm.plural")));
			}
			return;
		}
		
		String cli = "show version detail"+"\n";
		BeCommunicationEvent result = BeTopoModuleUtil
				.sendSyncCliRequest(hiveAp, new String[] { cli },
						getCliTypeValue(menuText),
						getTimeoutValue(menuText, value1));
		String msg  = BeTopoModuleUtil.parseCliRequestResult(result);
		boolean isSuccess = BeTopoModuleUtil.isCliExeSuccess(result);
		if(isSuccess){
			String currentStr = "Current&nbsp;version:";
			String backupStr = "Backup&nbsp;version:";
			String currentVer = hiveAp.isSimulated() ? "N/A" : parseHiveOSVersion(msg,currentStr);
			String backupVer =  hiveAp.isSimulated() ? "N/A" : parseHiveOSVersion(msg,backupStr);
			jsonObject.put("currentVer", currentVer);
			jsonObject.put("backupVer", backupVer);
		} else {
			jsonObject.put("isConnected",false);
			jsonObject.put("v",MgrUtil.getUserMessage("gotham_18.info.hiveos.detail.version.fetch.failed",msg));
		}
		
	}
	
	private String parseHiveOSVersion(String verDetail,String whichVersion){
		String version = "N/A";
		String brStr = "<br />";
		if(null == verDetail){
			return version;
		}
		int index = verDetail.indexOf(whichVersion);
		if(index < 0){
			return version;
		}
		String tempStr = verDetail.substring(index);
		if(tempStr.indexOf(brStr) < 0){
			return version;
		}
		version = tempStr.substring(0, tempStr.indexOf(brStr)).substring(whichVersion.length())
					.replace("&nbsp;", "").replace("HiveOS","").replace("release", "").replace("build", ".");;
		
		return version;
	}

	// e: error message displayed above the panel
	// n: host name of the HiveAp
	// data : table data displayed in the panel table
	// via CAPWAP
	protected void getHiveApNeighborInfoViaCAPWAP() throws Exception {
		jsonObject = new JSONObject();
		HiveAp hiveAp = getSelectedHiveAp();
		if (null == hiveAp) {
			// filled content value;
			jsonObject.put("e",
					MgrUtil.getUserMessage("error.cli.object.notfind"));
			return;
		}
		// filled host name value;
		jsonObject.put("h", hiveAp.getHostName());

		if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			jsonObject.put("e",
					MgrUtil.getUserMessage("error.hiveAp.notManaged.request"));
			return;
		}
		BeGetStatisticEvent request = BeTopoModuleUtil
				.getNeighborStatisticEvent(hiveAp);
		if (null == request) {
			jsonObject.put("e", MgrUtil
					.getUserMessage("error.capwap.neighbor.unknown.reason"));
			return;
		}
		BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(
				request, BeTopoModuleParameters.POLLING_VIA_CAPWAP_TIMEOUT);
		if (null == response) {
			jsonObject.put("e", MgrUtil
					.getUserMessage("error.capwap.neighbor.unknown.reason"));
			return;
		}

		StatisticResultsObject results = BeTopoModuleUtil
				.getStatisticResult(response);
		if (null == results || null == results.getNeighbors()) {
			jsonObject.put("e",
					MgrUtil.getUserMessage("error.capwap.neighbor.timeout"));
			return;
		}
		Collection<JSONObject> jsonNeighbors = getAllNeighbor(hiveAp,
				results.getNeighbors());
		if (null != jsonNeighbors && jsonNeighbors.size() > 0) {
			jsonObject.put("data", jsonNeighbors);
		} else {
			jsonObject.put("e",
					MgrUtil.getUserMessage("info.capwap.noNeighborInfo"));
		}
	}

	private Collection<JSONObject> getAllNeighbor(HiveAp host,
			List<AhNeighbor> neighbors) throws JSONException {
		Collection<JSONObject> jsonNeighbors = new Vector<JSONObject>();
		for (AhNeighbor neighborDto : neighbors) {
			if (null == neighborDto.getNeighborAPID()) {
				continue;
			}
			JSONObject jsonNeighbor = new JSONObject();
			Collection<JSONObject> neighborData = new Vector<JSONObject>();
			List<?> hostNames = QueryUtil.executeQuery(
					"select bo.hostName from " + HiveAp.class.getSimpleName()
							+ " bo", null, new FilterParams("macAddress",
							neighborDto.getNeighborAPID()), host.getOwner()
							.getId());
			JSONObject data = new JSONObject();
			if (hostNames.isEmpty()) {
				data.put("v", "N/A");
			} else {
				data.put("v", hostNames.get(0));
			}
			neighborData.add(data);
			data = new JSONObject();
			data.put("v", neighborDto.getNeighborAPID());
			neighborData.add(data);
			data = new JSONObject();
			data.put("v",
					NmsUtil.transformTime((int) neighborDto.getLinkUpTime()));
			neighborData.add(data);
			data = new JSONObject();
			data.put("v", neighborDto.getLinkCost());
			neighborData.add(data);
			data = new JSONObject();
			data.put("v", neighborDto.getRssiDbm());
			neighborData.add(data);

			int int_linkType = neighborDto.getLinkType();
			String str_linkType;
			switch (int_linkType) {
			case AhNeighbor.LINKTYPE_ETHLINK:
			case AhNeighbor.LINKTYPE_WIRELESSLINK:
				str_linkType = MgrUtil.getEnumString("enum.snmp.mrp.linkType."
						+ int_linkType);
				break;
			default:
				str_linkType = "";
			}
			data = new JSONObject();
			data.put("v", str_linkType);
			neighborData.add(data);

			jsonNeighbor.put("rowData", neighborData);
			jsonNeighbors.add(jsonNeighbor);
		}
		return jsonNeighbors;
	}

	private JSONObject getMulticastMonitorCliInfo(HiveAp hiveAp,
			String menuText, String value1, String wifiInterface)
			throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("t", menuText);
		if (null == hiveAp) {
			// filled content value;
			jsonObject.put("e",
					MgrUtil.getUserMessage("error.cli.object.notfind"));
			return jsonObject;
		}
		// filled host name value;
		jsonObject.put("h", hiveAp.getHostName());
		String errorMsg = validate(hiveAp, menuText, value1, jsonObject);
		if (null != errorMsg) {
			jsonObject.put("v", errorMsg);
			return jsonObject;
		}

		// fix bug 15598 start
		if (getText("topology.menu.diagnostics.showmulticastmonitor").equals(
				menuText)) {
			if (HiveAp.HIVEAP_MODEL_BR100 == hiveAp.getHiveApModel()
					&& "1".equals(wifiInterface)) {
				String paramBr = getText("config.guid.hiveAp.list.branchRouters.simple");
				jsonObject.put("v", MgrUtil.getUserMessage(
						"error.hiveAp.showmulticastmonitor.notsupported.wifi1",
						new String[] { paramBr, "wifi1" }));
				return jsonObject;
			}
		}
		// end

		String cli = AhCliFactory.getMulticastMonitorCli(wifiInterface);
		if (null == cli) {
			String msg = MgrUtil.getUserMessage("error.cli.menuCmd.notfind",
					menuText);
			jsonObject.put("v", msg);
			return jsonObject;
		}
		BeCommunicationEvent result = BeTopoModuleUtil.sendSyncCliRequest(
				hiveAp, new String[] { cli }, getCliTypeValue(menuText),
				getTimeoutValue(menuText, value1));
		String msg = parseResult(result, hiveAp, menuText, value1);
		if (null != msg) {
			jsonObject.put("v", msg);
		}
		return jsonObject;
	}

	private JSONObject getSingleHiveApCliInfo(HiveAp hiveAp, String menuText,
			String value1) throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("t", menuText);
		if (null == hiveAp) {
			// filled content value;
			jsonObject.put("e",
					MgrUtil.getUserMessage("error.cli.object.notfind"));
			return jsonObject;
		}
		// filled host name value;
		jsonObject.put("h", hiveAp.getHostName());
		String errorMsg = validate(hiveAp, menuText, value1, jsonObject);
		if (null != errorMsg) {
			jsonObject.put("v", errorMsg);
			return jsonObject;
		}

		if (getText("topology.menu.statistics.summary").equals(menuText)) {
			String displayMsg = getInterfaceMsg(hiveAp, menuText, value1)
					+ getStationMsg(hiveAp, menuText, value1);
			jsonObject.put("v", displayMsg);
		} else if (getText("topology.menu.diagnostics.showdhcpclientallocation")
				.equals(menuText)) {
			getDhcpclientallocation(hiveAp, menuText, value1, jsonObject);
		} else {
			String cli = getCliString(hiveAp, menuText, value1);
			if (null == cli) {
				String msg = MgrUtil.getUserMessage(
						"error.cli.menuCmd.notfind", menuText);
				jsonObject.put("v", msg);
				return jsonObject;
			}
			String msg = null;
			// if device is not Switch
			if ("deviceIsNotSwitch".equals(cli)) {
				msg=getText("topology.menu.diagnostics.showfdb.error");
			} else {
				BeCommunicationEvent result = BeTopoModuleUtil
						.sendSyncCliRequest(hiveAp, new String[] { cli },
								getCliTypeValue(menuText),
								getTimeoutValue(menuText, value1));
				msg = parseResult(result, hiveAp, menuText, value1);
			}
			if (null != msg) {
				jsonObject.put("v", msg);
			}
			//fix bug 30726
			if(getText("topology.menu.statistics.interface").equals(menuText)) {
				if(value2 != null){
					jsonObject.put("interfacelist", value2.split(","));
				} else {
					jsonObject.put("interfacelist", parseInterfacefromMsg(msg));
				}
				
			}
		}

		return jsonObject;
	}
	
	private List<String> parseInterfacefromMsg(String msg){
		List<String> result = new ArrayList<>();
		result.add("all");
		if(msg == null && "".equals(msg)){
			return result;
		}
		String br = "<br />";
		String[] msgLines = msg.split(br);
		String regex = "^[e|E]th1/([1-9]|[1-4][0-9]|5[0-2])$|^[v|V]lan([1-9]\\d{0,2}|[1-3]\\d{3}|40[0-8][0-9]|409[0-4])$|^[R|r]ed0$|^[m|M]gt0(.[1-9]|.1[0-6])?$|^[a|A]gg([0-9]|[1-2][0-9]|30)$|^[e|E]th[0-4]$|^[w|W]ifi0(.[1-9]|.1[0-6])?$|^[w|W]ifi1(.[1-9]|.1[0-6])?$";
		Pattern pattern = Pattern.compile(regex);
		for (String msgLine : msgLines) {
			int index = msgLine.indexOf("&nbsp;");
			if(index > 0){
				String firstWord = msgLine.substring(0, index);
				if (pattern.matcher(firstWord).find()) {
					result.add(firstWord.toLowerCase());
				}
			}
		}
		
		return result;
	}

	private JSONObject getDhcpclientallocation(HiveAp hiveAp, String menuText,
			String value1, JSONObject jsonObject) throws Exception {
		String runConfigCli_mgt0 = AhCliFactory.getRunningConfig("interface mgt0");
		String runConfigCli_vlan = AhCliFactory.getRunningConfig("interface vlan");
		BeCommunicationEvent result = BeTopoModuleUtil.sendSyncCliRequest(
				hiveAp, new String[] { runConfigCli_mgt0, runConfigCli_vlan},
				getCliTypeValue(menuText), getTimeoutValue(menuText, value1));
		String runConfigMsg = parseResult(result, hiveAp, menuText, value1);
		if (null != runConfigMsg) {
			String br = "<br />";
			String ipStr = "&nbsp;ip&nbsp;";
			String interfaceStr = "interface&nbsp;";
			String[] msgLines = runConfigMsg.split(br);
			List<String> interfaces = new ArrayList<String>();
			List<String> subnets = new ArrayList<String>();
			String regex = "^(interface&nbsp;(mgt(0|0\\.[1-9]|0\\.1[0-6])|vlan([1-9]\\d{0,2}|[1-3]\\d{3}|40[0-8][0-9]|409[0-4]))&nbsp;ip&nbsp;).*$";
			Pattern pattern = Pattern.compile(regex);
			for (String msgLine : msgLines) {
				if (pattern.matcher(msgLine).find()) {
					int start = msgLine.indexOf(interfaceStr)
							+ interfaceStr.length();
					int end = msgLine.indexOf(ipStr);
					if (start < interfaceStr.length() || end < 0 || start > end) {
						continue;
					}
					interfaces.add(msgLine.substring(start, end));
					int subnet_start = msgLine.indexOf(ipStr) + ipStr.length();
					String subnet = msgLine.substring(subnet_start);
					if (subnet.contains("&nbsp;")) {
						// 192.168.85.1&nbsp;255.255.255.0 --> 192.168.85.1/24
						String str_subnet = subnet.split("&nbsp;")[0]
								+ "/"
								+ String.valueOf(AhEncoder.netmask2int(subnet
										.split("&nbsp;")[1]));
						subnets.add(str_subnet);
					} else {
						subnets.add(subnet);
					}
				}
			}
			List<String> dhcpMsgs = new ArrayList<String>();
			for (String inface : interfaces) {
				String dhcpCli = getCliString(hiveAp, menuText, inface);
				BeCommunicationEvent dhcpResult = BeTopoModuleUtil
						.sendSyncCliRequest(hiveAp, new String[] { dhcpCli },
								getCliTypeValue(menuText),
								getTimeoutValue(menuText, value1));

				String dhcpMsg = parseResult(dhcpResult, hiveAp, menuText,
						value1);
				if (null != dhcpMsg) {
					dhcpMsgs.add(dhcpMsg);
				} else {
					dhcpMsgs.add(MgrUtil
							.getUserMessage("error.cli.obj.buildRequest"));
				}
			}

			if (!dhcpMsgs.isEmpty()) {
				jsonObject.put("dhcpMsgs", dhcpMsgs);
				jsonObject.put("subnets", subnets);
			}
		}
		return jsonObject;
	}

	private String getInterfaceMsg(HiveAp hiveAp, String menuText, String value1)
			throws Exception {
		String interfaceMsg = "";
		String[] clis = getCliList(hiveAp, menuText, value1);
		if (null == clis) {
			interfaceMsg = MgrUtil.getUserMessage("error.cli.menuCmd.notfind",
					menuText);
		} else {
			BeCommunicationEvent result = BeTopoModuleUtil.sendSyncCliRequest(
					hiveAp, clis, getCliTypeValue(menuText),
					getTimeoutValue(menuText, value1));
			String msg = parseResult(result, hiveAp, menuText, value1);
			if (null != msg) {
				if (msg.indexOf("Summary&nbsp;state") == -1) {
					interfaceMsg = "<div><fieldset style='width:1200px;padding: 0 10px 8px;'>"
							+ "        <legend>Interface</legend>"
							+ "        <br/>" + msg + "    	 </fieldset>";
				} else {
					interfaceMsg = "<div><fieldset style='width:1200px;height:80px;padding: 0 10px 8px;'>"
							+ "        <legend>Interface</legend>"
							+ "        <br/>"
							+ "        <label>Wifi0:</label><label style='padding-left: 30px;'>"
							+ getInterfaceSummaryStates(msg, 0) + "</label>";
					if (getInterfaceSummaryStates(
							msg,
							msg.indexOf("<br",
									msg.indexOf("Summary&nbsp;state"))) != null) {
						interfaceMsg += "        <label style='padding-left: 100px;'>Wifi1:</label><label style='padding-left: 30px;'>"
								+ getInterfaceSummaryStates(msg, msg.indexOf(
										"<br",
										msg.indexOf("Summary&nbsp;state")))
								+ "</label>";
					}
					interfaceMsg += "    	 </fieldset></div>";
				}
			}
		}

		return interfaceMsg;
	}

	private String getStationMsg(HiveAp hiveAp, String menuText, String value1)
			throws Exception {
		String stationMsg = "";
		String cli = AhCliFactory.getStationCli();
		if (cli == null) {
			stationMsg = MgrUtil.getUserMessage("error.cli.menuCmd.notfind",
					menuText);
		} else {
			BeCommunicationEvent result = BeTopoModuleUtil.sendSyncCliRequest(
					hiveAp, new String[] { cli }, getCliTypeValue(menuText),
					getTimeoutValue(menuText, value1));
			String msg = parseResult(result, hiveAp, menuText, value1);
			stationMsg = "<div style='padding: 30px 0px;'><fieldset style='width: 1200px;padding: 0 10px 8px;'>"
					+ "    	 <legend>Station</legend>"
					+ "        <br/>"
					+ msg
					+ "		 </fieldset></div>";

		}

		return stationMsg;
	}

	private String getInterfaceSummaryStates(String msg, int index) {
		String states = null;
		int wifi0Index = msg.indexOf("Summary&nbsp;state", index);
		if (wifi0Index == -1) {
			return states;
		}
		int wifi0BrIndex = msg.indexOf("<br", wifi0Index);
		if (wifi0BrIndex == -1) {
			return states;
		}
		String wifi0Str = msg.substring(wifi0Index, wifi0BrIndex);
		states = wifi0Str.substring(wifi0Str.indexOf("=") + 1,
				wifi0Str.lastIndexOf(";"));
		return states;
	}

	private String parseResult(BeCommunicationEvent result, HiveAp hiveAp,
			String menuText, String value1) throws Exception {
		String msg = BeTopoModuleUtil.parseCliRequestResult(result);
		boolean isSuccess = BeTopoModuleUtil.isCliExeSuccess(result);

		if (getText("topology.menu.hiveAp.reboot").equals(menuText)) {
			if (isSuccess) {
				msg = MgrUtil.getUserMessage("info.cli.reboot.success");
				// Configuration indication
				try {
					BoMgmt.getHiveApMgmt()
							.updateConfigurationIndicationForReboot(hiveAp);
				} catch (Exception e) {
					log.error("parseResult",
							"Configuration indication in HiveAP reboot case failed.");
				}
			} else {
				if (null == msg || "".equals(msg)) {
					msg = MgrUtil.getUserMessage("info.cli.reboot.failed");
				}
			}
		} else if (getText("topology.menu.hiveAp.invokeBackup")
				.equals(menuText)) {
			// if cli is reboot for backup;
			if (isSuccess) {
				if ("current".equals(value1)) {
					msg = MgrUtil
							.getUserMessage("info.cli.reboot.current.success");
				} else {
					msg = MgrUtil
							.getUserMessage("info.cli.reboot.backup.success");
				}
			} else {
				if (null == msg || "".equals(msg)) {
					if ("current".equals(value1)) {
						msg = MgrUtil
								.getUserMessage("info.cli.reboot.current.failed");
					} else {
						msg = MgrUtil
								.getUserMessage("info.cli.reboot.backup.failed");
					}
				}
			}
		} else if (getText("topology.menu.hiveAp.turboModeToggle").equals(menuText)){
			if (isSuccess) {
				msg = MgrUtil.getUserMessage("info.cli.turbo.success");

			} else {
				if (null == msg || "".equals(msg)) {
					msg = MgrUtil.getUserMessage("info.cli.turbo.failed");
				}
			}
		} else if (getText("topology.menu.hiveAp.pse.reset").equals(menuText)) {
			if (isSuccess) {
				msg = MgrUtil.getUserMessage("info.cli.pse.reset.success");

			} else {
				if (null == msg || "".equals(msg)) {
					msg = MgrUtil.getUserMessage("info.cli.pse.reset.failed");
				}
			}
		} else if (getText("geneva_03.topology.menu.hiveAp.usbmodem.reset").equals(menuText)) {
			if (isSuccess) {
				msg = MgrUtil.getUserMessage("geneva_03.info.cli.usbmodem.reset.success");

			} else {
				if (null == msg || "".equals(msg)) {
					msg = MgrUtil.getUserMessage("geneva_03.info.cli.usbmodem.reset.failed");
				}
			}
		}else if (getText("topology.menu.hiveAp.clear.radsec.credentials")
				.equals(menuText)) {
			if (isSuccess) {
				msg = MgrUtil
						.getUserMessage("info.cli.clear.IDMCredentials.success");
			} else {
				if (null == msg || "".equals(msg)) {
					msg = MgrUtil
							.getUserMessage("info.cli.clear.IDMCredentials.failed");
				}
			}
		} else if (getText("topology.menu.hiveAp.reset.default")
				.equals(menuText)) {
			if (isSuccess) {
				msg = MgrUtil
						.getUserMessage("info.cli.reset.device.success");
				List<Long> idsRemove = new ArrayList<Long>();
				idsRemove.add(hiveAp.getId());
				if (checkCVGUsedForReset(hiveAp)) {
					BoMgmt.getMapMgmt().removeHiveAps(idsRemove, true);
					
				} else {
					msg = msg + "\n\n" +  getText("error.hiveap.cvg.rmError.beBind");
				}
				//QueryUtil.updateBos(HiveAp.class, "manageStatus=:s1 and lastCfgTime=:s2", "id=:s3", new Object[]{HiveAp.STATUS_NEW, 0, hiveAp.getId()});
				//CacheMgmt.getInstance().getSimpleHiveAp(hiveAp).setManageStatus(HiveAp.STATUS_NEW);
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("info.cli.reset.device.successwithName",hiveAp.getHostName()));
			} else {
				if (null == msg || "".equals(msg)) {
					msg = MgrUtil.getUserMessage("info.cli.reset.device.failed", hiveAp.getHostName());
				}
			}
		} else if (getText("topology.menu.hiveAp.disable.hiveui.cfg")
				.equals(menuText)) {
			if (isSuccess) {
				msg = MgrUtil
						.getUserMessage("info.cli.disable.hiveui.success");
			} else {
				if (null == msg || "".equals(msg)) {
					msg = MgrUtil
							.getUserMessage("info.cli.disable.hiveui.failed");
				}
			}
		}
		return msg;
	}
	
	protected boolean checkCVGUsedForReset(HiveAp oneDevice) {
		try {
			if (!oneDevice.isVpnGateway()) {
				return true;
			}
			String sqlStr = "select hiveApId from VPN_GATEWAY_SETTING where hiveApId = "
					+ oneDevice.getId();
			List<?> rmHiveApIds = QueryUtil.executeNativeQuery(sqlStr);
			if (rmHiveApIds!=null && !rmHiveApIds.isEmpty()) {
				return false;
			}
		} catch (Exception e) {
			log.error(e);
		}
		return true;
	}

	public JSONObject getMultipleHiveApCliInfo(Set<Long> hiveApIds,
			String menuText, String value1) throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("t", menuText);
		if (null == hiveApIds || hiveApIds.isEmpty()) {
			jsonObject.put("e",
					MgrUtil.getUserMessage("error.cli.object.notfind"));
			return jsonObject;
		}
		if (hiveApIds.size() == 1) {
			Long id = null;
			for (Long hiveapId : hiveApIds) {
				id = hiveapId;
			}
			HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class, id);
			return getSingleHiveApCliInfo(hiveAp, menuText, value1);
		} else {
			jsonObject.put("h", "Multiple "
					+ NmsUtil.getOEMCustomer().getAccessPonitName() + "s");
			StringBuilder content = new StringBuilder("");
			List<HiveAp> requestHiveAps = new ArrayList<HiveAp>();
			Map<String, String[]> cliMap = new HashMap<String, String[]>();
			for (Long id : hiveApIds) {
				HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class, id);
				if (null == hiveAp) {
					// filled content value;
					content.append(getSeparator())
							.append(MgrUtil
									.getUserMessage("error.cli.object.notfind"))
							.append(getBr());
					continue;
				}
				String errorMsg = validate(hiveAp, menuText, value1, jsonObject);
				if (null != errorMsg) {
					// filled content value;
					content.append(getSeparator(hiveAp.getHostName()))
							.append(errorMsg).append(getBr());
					continue;
				}
				String cli = getCliString(hiveAp, menuText, value1);
				if (null == cli) {
					String msg = MgrUtil.getUserMessage(
							"error.cli.menuCmd.notfind", menuText);
					// filled content value;
					content.append(getSeparator(hiveAp.getHostName()))
							.append(msg).append(getBr());
					continue;
				}
				requestHiveAps.add(hiveAp);
				cliMap.put(hiveAp.getMacAddress(), new String[] { cli });
			}
			if (!requestHiveAps.isEmpty()) {
				Map<String, BeCommunicationEvent> resultMap = sendGroupSyncCliRequests(
						requestHiveAps, cliMap, getCliTypeValue(menuText),
						getTimeoutValue(menuText, value1));
				if (null == resultMap) {
					String msg = MgrUtil
							.getUserMessage("error.cli.obj.buildRequest");
					content.append(getSeparator()).append(msg).append(getBr());
				} else {
					for (String mac : resultMap.keySet()) {
						BeCommunicationEvent result = resultMap.get(mac);
						HiveAp hiveAp = result.getAp();
						String msg = parseResult(result, hiveAp, menuText,
								value1);
						if (null != msg) {
							content.append(getSeparator(hiveAp.getHostName()))
									.append(msg).append(getBr());
						}
					}
				}
			}
			jsonObject.put("v", content.toString());
			return jsonObject;
		}
	}

    /**
     * For XSS issue reported by LinkLater, escape the menu text response to browser to avoid reflected XSS
     * 
     * @author Yunzhi Lin
     * - Time: Dec 20, 2013 4:27:09 PM
     * @param menuText
     * @return
     */
    private String escapseMenuText(String menuText) {
        menuText = StringEscapeUtils.escapeHtml4(menuText);
        return menuText;
    }

	private Map<String, BeCommunicationEvent> sendGroupSyncCliRequests(
			List<HiveAp> hiveAps, Map<String, String[]> cliMap, byte cliType,
			int timeout) {
		List<BeCliEvent> requests = new ArrayList<BeCliEvent>();
		List<BeCommunicationEvent> responses;
		try {
			for (HiveAp hiveAp : hiveAps) {
				String[] clis = cliMap.get(hiveAp.getMacAddress());
				if (null == clis) {
					continue;
				}
				BeCliEvent cliRequest = BeTopoModuleUtil.getCliEvent(hiveAp,
						clis, cliType);
				requests.add(cliRequest);
			}
			responses = HmBeCommunicationUtil.sendSyncGroupRequest(requests,
					timeout);
		} catch (Exception e) {
			log.error("sendGroupSyncCliRequests",
					"catch build packet exception", e);
			return null;
		}
		Map<String, BeCommunicationEvent> resultMap = null;
		if (null != responses) {
			resultMap = new HashMap<String, BeCommunicationEvent>();
			for (BeCommunicationEvent event : responses) {
				resultMap.put(event.getApMac(), event);
			}
		}
		return resultMap;
	}

	private int getTimeoutValue(String menuText, String value1) {
		int timeout = BeTopoModuleParameters.DEFAULT_CLI_TIMEOUT_MAX / 1000;
		if (getText("topology.menu.diagnostics.traceroute").equals(menuText)) {
			if (null != value1 && !"".equals(value1.trim())) {
				try {
					timeout = Integer.parseInt(value2);
				} catch (NumberFormatException e) {
				}
			}
		} else if (getText("topology.menu.diagnostics.showlog")
				.equals(menuText)) {
			timeout = BeTopoModuleParameters.SHOW_LOG_TIMEOUT_MAX / 1000;
		} else if (getText("topology.menu.diagnostics.showrunningconfig")
				.equals(menuText)) {
			timeout = BeTopoModuleParameters.SHOW_RUNNING_CFG_MAX / 1000;
		}
		return timeout;
	}

	private byte getCliTypeValue(String menuText) {
		byte cliType = BeCliEvent.CLITYPE_NORMAL;
		if (getText("topology.menu.diagnostics.ping").equals(menuText)
				|| getText("topology.menu.diagnostics.traceroute").equals(
						menuText)) {
			cliType = BeCliEvent.CLITYPE_TIMECONSUMING;
		}
		return cliType;
	}

	private String[] getCliList(HiveAp hiveAp, String menuText, String value1) {
		String[] cliList = null;
		if (getText("topology.menu.statistics.summary").equals(menuText)) {
			if (hiveAp.isWifi1Available()) {
				cliList = new String[2];
				cliList[0] = AhCliFactory.getInterfaceCli("wifi0");
				cliList[1] = AhCliFactory.getInterfaceCli("wifi1");
			} else {
				cliList = new String[1];
				cliList[0] = AhCliFactory.getInterfaceCli("wifi0");
			}

		}
		return cliList;
	}

	private String getCliString(HiveAp hiveAp, String menuText, String value1) {
		String cli = null;
		if (getText("topology.menu.diagnostics.ping").equals(menuText)) {
			if (null == value1 || "".equals(value1.trim())) {
				cli = AhCliFactory.getPingCli(NmsUtil
						.getRunningCapwapServer(hiveAp));
			} else {
				cli = AhCliFactory.getPingCli(value1);
			}
		} else if (getText("topology.menu.diagnostics.traceroute").equals(
				menuText)) {
			if (null == value1 || "".equals(value1.trim())) {
				cli = AhCliFactory.getTracerouteCli(NmsUtil
						.getRunningCapwapServer(hiveAp));
			} else {
				cli = AhCliFactory.getTracerouteCli(value1);
			}
		} else if (getText("topology.menu.diagnostics.showlog")
				.equals(menuText)) {
			cli = AhCliFactory.getLogCli();
		} else if (getText("topology.menu.diagnostics.showfdb")
				.equals(menuText)) {
			if (hiveAp.isSwitchProduct()) {
				cli = AhCliFactory.getFdbCli();
			} else {
				cli = "deviceIsNotSwitch";
			}
		} else if (getText("topology.menu.diagnostics.showversion").equals(
				menuText)) {
			if (checkVersionSupported(hiveAp, "3.2.0.0")) {
				cli = AhCliFactory.getVersionDetailCli();
			} else {
				cli = AhCliFactory.getVersionCli();
			}
		} else if (getText("topology.menu.diagnostics.showrunningconfig")
				.equals(menuText)) {
			cli = AhCliFactory.showRunningConfig(hiveAp.getSoftVer(), false);
		} else if (getText("topology.menu.diagnostics.showiproutes").equals(
				menuText)) {
			cli = AhCliFactory.getIPRouteCli();
		} else if (getText("topology.menu.diagnostics.showmacroutes").equals(
				menuText)) {
			cli = AhCliFactory.getMACRouteCli();
		} else if (getText("topology.menu.diagnostics.showarpcache").equals(
				menuText)) {
			cli = AhCliFactory.getARPCacheCli();
		} else if (getText("topology.menu.diagnostics.showroamingcache")
				.equals(menuText)) {
			cli = AhCliFactory.getRoamingCacheCli();
		} else if (getText("topology.menu.diagnostics.showl3roamingneighbors")
				.equals(menuText)) {
			// cli = AhCliFactory.getL3RoamingNeighborCli();
		} else if (getText("topology.menu.diagnostics.showl3roamingexstations")
				.equals(menuText)) {
			// cli = AhCliFactory.getL3RoamingExStationCli();
		} else if (getText("topology.menu.diagnostics.showl3roamingimstations")
				.equals(menuText)) {
			// cli = AhCliFactory.getL3RoamingImStationCli();
		} else if (getText("topology.menu.diagnostics.showdnxpneighbor")
				.equals(menuText)) {
			cli = AhCliFactory.getDnxpNeighbor();
		} else if (getText("topology.menu.diagnostics.showdnxpcache").equals(
				menuText)) {
			cli = AhCliFactory.getDnxpCache();
		} else if (getText("topology.menu.diagnostics.showamrptunnel").equals(
				menuText)) {
			cli = AhCliFactory.getAmrpTunnelCli();
		} else if (getText("topology.menu.diagnostics.showvpngretunnel")
				.equals(menuText)) {
			cli = AhCliFactory.getVpnGreTunnelCli();
		} else if (getText("topology.menu.diagnostics.showvpnikeevent").equals(
				menuText)) {
			cli = AhCliFactory.getVpnIkeEventCli();
		} else if (getText("topology.menu.diagnostics.showvpnikesa").equals(
				menuText)) {
			cli = AhCliFactory.getVpnIkeSaCli();
		} else if (getText("topology.menu.diagnostics.showvpnipsecsa").equals(
				menuText)) {
			cli = AhCliFactory.getVpnIpSecSaCli();
		} else if (getText("topology.menu.diagnostics.showvpnipsectunnel")
				.equals(menuText)) {
			cli = AhCliFactory.getVpnIpSecTunnelCli();
		} else if (getText("topology.menu.diagnostics.showcpu")
				.equals(menuText)) {
			cli = AhCliFactory.getCPUCli();
		} else if (getText("topology.menu.diagnostics.showmemory").equals(
				menuText)) {
			cli = AhCliFactory.getMemoryCli();
		} else if (getText("topology.menu.diagnostics.showsystempower").equals(
				menuText)) {
			cli = AhCliFactory.showPoeMaxPowerCli();
		} else if (getText("topology.menu.diagnostics.showpse").equals(
				menuText)) {
			cli = AhCliFactory.showPseCli();
		} else if (getText("topology.menu.statistics.acsp").equals(menuText)) {
			cli = AhCliFactory.getACSPCli();
		} else if (getText("topology.menu.statistics.interface").equals(
				menuText)) {
			if (MgrUtil.getEnumString(
					"enum.interface.type." + EnumConstUtil.INTERFACE_ITEM_ALL)
					.equals(value1)) {
				cli = AhCliFactory.getInterfaceCli(null);
			} else {
				if(hiveAp.isSwitchProduct()){
					String regex = "^vlan\\d+$|ppp0"; //l3 interface
					cli = AhCliFactory.getInterfaceCli(value1,regex);
				} else {
					cli = AhCliFactory.getInterfaceCli(value1);
				}
				
			}
		} else if (getText("topology.menu.hiveAp.reboot").equals(menuText)) {
			cli = AhCliFactory
					.getRebootCli(BeTopoModuleParameters.DEFAULT_REBOOT_OFFSET);
		} else if (getText("topology.menu.hiveAp.invokeBackup")
				.equals(menuText)) {
			if ("current".equals(value1)) {
				cli = AhCliFactory
						.getRebootCurrentCli(BeTopoModuleParameters.DEFAULT_REBOOT_OFFSET);
			} else {
				cli = AhCliFactory
						.getRebootBackupCli(BeTopoModuleParameters.DEFAULT_REBOOT_OFFSET);
			}
		} else if (getText("topology.menu.lldpcdp.showLldpPara").equals(
				menuText)) {
			cli = AhCliFactory.getLldpParameterCli();
		} else if (getText("topology.menu.lldpcdp.showLldpNeighbor").equals(
				menuText)) {
			cli = AhCliFactory.getLldpNeighborCli();
		} else if (getText("topology.menu.lldpcdp.showCdpPara")
				.equals(menuText)) {
			if(hiveAp.isSwitchProduct()){
				cli = AhCliFactory.getCdpParameterCliForSwitch();
			}else{
				cli = AhCliFactory.getCdpParameterCli();
			}
		} else if (getText("topology.menu.lldpcdp.showCdpNeighbor").equals(
				menuText)) {
			if(hiveAp.isSwitchProduct()){
				cli = AhCliFactory.getCdpNeighborCliForSwitch();
			}else{
				cli = AhCliFactory.getCdpNeighborCli();
			}
		} else if (getText("topology.menu.alg.sip.name").equals(menuText)) {
			if (null == value1 || "".equals(value1.trim())) {
				cli = AhCliFactory.getAlgSipCli(null);
			} else {
				cli = AhCliFactory.getAlgSipCli(value1);
			}
		} else if (getText("topology.menu.diagnostics.showpathmtudiscovery")
				.equals(menuText)) {
			cli = AhCliFactory.getPathMtuDiscoveryCli();

		} else if (getText("topology.menu.diagnostics.showtcpmss").equals(
				menuText)) {
			cli = AhCliFactory.getTcpMssThresholdCli();
		} else if (getText("topology.menu.hiveAp.pse.reset").equals(menuText)) {
			cli = AhCliFactory.getPseResetCli();
		} else if (getText("geneva_03.topology.menu.hiveAp.usbmodem.reset").equals(menuText)) {
			cli = AhCliFactory.getUsbModemResetCli();
		} else if (getText("topology.menu.diagnostics.showdhcpclientallocation")
				.equals(menuText)) {
			if (MgrUtil.getEnumString("enum.interface.dhcp.server." + 0)
					.equals(value1)) {
				cli = AhCliFactory.getDhcpClientAllocation(null);
			} else {
				cli = AhCliFactory.getDhcpClientAllocation(value1);
			}
		} else if (getText("topology.menu.hiveAp.clear.radsec.credentials")
				.equals(menuText)) {
			cli = AhCliFactory.getClearRadsecCertCli();
		} else if (getText("topology.menu.hiveAp.reset.default")
				.equals(menuText)) {
			cli = AhCliFactory.getResetDeviceToDefaultCli();
		} else if (getText("topology.menu.hiveAp.disable.hiveui.cfg")
				.equals(menuText)) {
			cli = AhCliFactory.getDisableHiveUIConfigCli();
		} else if (getText("topology.menu.hiveAp.turboModeToggle")
				.equals(menuText)){
			if ("on".equals(value1)) {
				cli = AhCliFactory.getQosEnableCli(false);
			} else {
				cli = AhCliFactory.getQosEnableCli(true);
			}
		}
		return cli;
	}

	private String validate(HiveAp hiveAp, String menuText, String value1,
			JSONObject jsonObject) throws JSONException {
		String errorMsg = null;
		if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			errorMsg = MgrUtil
					.getUserMessage("error.hiveAp.notManaged.request");
		} else if (null == hiveAp.getIpAddress()
				|| "".equals(hiveAp.getIpAddress().trim())) {
			errorMsg = MgrUtil.getUserMessage("error.snmp.unknown.destination");
		} else if (getText("topology.menu.diagnostics.ping").equals(menuText)) {
			if (null == value1 || "".equals(value1.trim())) {
				jsonObject.put("ip", NmsUtil.getRunningCapwapServer(hiveAp));
			}
		} else if (getText("topology.menu.diagnostics.traceroute").equals(
				menuText)) {
			if (null == value1 || "".equals(value1.trim())) {
				jsonObject.put("ip", NmsUtil.getRunningCapwapServer(hiveAp));
			}
		} else if (getText("topology.menu.diagnostics.showsystempower").equals(
				menuText)) {

			// fix bug 15555 start
			if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_330) {
				if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
					String paramBr = getText("config.guid.hiveAp.list.branchRouters.simple");
					errorMsg = MgrUtil.getUserMessage(
							"error.hiveAp.systempower.notsupported", paramBr);
				} else if (hiveAp.isVpnGateway()) {
					errorMsg = MgrUtil.getUserMessage(
							"error.hiveAp.systempower.notsupported", "CVG");
				} else {
					errorMsg = MgrUtil.getUserMessage(
							"error.hiveAp.systempower.notsupported", "HiveAp");
				}
			} else if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100) {
				String paramBr = getText("config.guid.hiveAp.list.branchRouters.simple");
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.systempower.notsupported", paramBr);
			} else if (hiveAp.isCVGAppliance()) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.systempower.notsupported", "CVG");
			} else if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_170) {
				// fix bug 15775
				if (NmsUtil.isHMForOEM()) {
					errorMsg = MgrUtil.getUserMessage(
							"error.hiveAp.systempower.notsupported",
							AhConstantUtil.getString(Device.NAME,
									hiveAp.getHiveApModel()));
				} else {
					errorMsg = MgrUtil.getUserMessage(
							"error.hiveAp.systempower.notsupported", "HiveAp");
				}
				// end
			} else if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP
					|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ
					|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_121
					|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_141) {
				// fix Bug 17479
				errorMsg = MgrUtil
						.getUserMessage("error.hiveAp.cli.model.notSupport");
			} else {
				if (!hiveAp.isPoEAvailable()) {
					errorMsg = MgrUtil
							.getUserMessage("error.hiveAp.cli.model.notSupport");
				} else if (!checkVersionSupported(hiveAp, "3.1.7.0")) {
					errorMsg = MgrUtil.getUserMessage(
							"error.hiveAp.feature.support.version",
							MgrUtil.getHiveOSDisplayVersion("3.1.7.0"));
				}
			}
			// end

		} else if (getText("topology.menu.hiveAp.invokeBackup")
				.equals(menuText)) {
//			if (!hiveAp.isDualImageAvailable()) {
//				errorMsg = MgrUtil
//						.getUserMessage("error.hiveAp.cli.model.notSupport");
//			} else 
			if (!checkVersionSupported(hiveAp, "3.2.1.0")) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.feature.support.version",
						MgrUtil.getHiveOSDisplayVersion("3.2.1.0"));
				// fix bug 16344
			} else if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100) {
				String paramBr = getText("config.guid.hiveAp.list.branchRouters.simple");
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.systempower.notsupported", paramBr);
				// fix bug 17104
			} else if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_121
					|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_141) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.systempower.notsupported", "HiveAp");
			}
		} else if (getText("topology.menu.lldpcdp.showLldpPara").equals(
				menuText)
				|| getText("topology.menu.lldpcdp.showLldpNeighbor").equals(
						menuText)
				|| getText("topology.menu.lldpcdp.showCdpPara")
						.equals(menuText)
				|| getText("topology.menu.lldpcdp.showCdpNeighbor").equals(
						menuText)
				|| getText("topology.menu.alg.sip.name").equals(menuText)) {
			if (!checkVersionSupported(hiveAp, "3.2.1.0")) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.feature.support.version",
						MgrUtil.getHiveOSDisplayVersion("3.2.1.0"));
			}
			if (!getText("topology.menu.alg.sip.name").equals(menuText)) {
				if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100) {
					errorMsg = MgrUtil
							.getUserMessage("error.hiveAp.systempower.notsupported");
				}
			}
		} else if (getText("topology.menu.diagnostics.showdnxpneighbor")
				.equals(menuText)
				|| getText("topology.menu.diagnostics.showdnxpcache").equals(
						menuText)) {
			if (!checkVersionSupported(hiveAp, "3.4.1.0")) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.feature.support.version",
						MgrUtil.getHiveOSDisplayVersion("3.4.1.0"));
			}
		} else if (getText("topology.menu.diagnostics.showamrptunnel").equals(
				menuText)
				|| getText("topology.menu.diagnostics.showvpngretunnel")
						.equals(menuText)
				|| getText("topology.menu.diagnostics.showvpnikeevent").equals(
						menuText)
				|| getText("topology.menu.diagnostics.showvpnikesa").equals(
						menuText)
				|| getText("topology.menu.diagnostics.showvpnipsecsa").equals(
						menuText)
				|| getText("topology.menu.diagnostics.showvpnipsectunnel")
						.equals(menuText)) {
			if (!checkVersionSupported(hiveAp, "3.4.1.0")) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.feature.support.version",
						MgrUtil.getHiveOSDisplayVersion("3.4.1.0"));
			}
		} else if (getText("topology.menu.diagnostics.showmulticastmonitor")
				.equals(menuText)) {
			if (!checkVersionSupported(hiveAp, "4.0.2.0")) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.feature.support.version",
						MgrUtil.getHiveOSDisplayVersion("4.0.2.0"));
			}
			// fix bug 15597 start
			if (hiveAp.isCVGAppliance()) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.systempower.notsupported", "Device");
			}
			// end

		} else if (getText("topology.menu.statistics.summary").equals(menuText)) {
			if (!checkVersionSupported(hiveAp, "5.0.3.0")) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.feature.support.version",
						MgrUtil.getHiveOSDisplayVersion("5.0.3.0"));
			}

			if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.systempower.notsupported", "CVG");
			}

			if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.systempower.notsupported", "BR200");
			}

		} else if (getText("topology.menu.diagnostics.showpathmtudiscovery")
				.equals(menuText)
				|| getText("topology.menu.diagnostics.showtcpmss").equals(
						menuText)) {
			if (!checkVersionSupported(hiveAp, "5.0.3.0")) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.feature.support.version",
						MgrUtil.getHiveOSDisplayVersion("5.0.3.0"));
			}

			if (!hiveAp.isBranchRouter() && !hiveAp.isVpnGateway() && !hiveAp.isSwitch()) {
				errorMsg = MgrUtil
						.getUserMessage("error.hiveAp.cli.model.notSupport");
			}
		} else if (getText("topology.menu.hiveAp.pse.reset").equals(menuText)) {
			if (hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200_WP 
					&& hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200_LTE_VZ
					&& hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_SR24
					&& hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_SR2124P
					&& hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_SR2024P					
					&& hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_SR2148P
					&& hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_SR48) {
				if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
					String paramBr = getText("config.guid.hiveAp.list.branchRouters.simple");
					errorMsg = MgrUtil.getUserMessage(
							"error.hiveAp.systempower.notsupported", paramBr);
				} else if (hiveAp.isVpnGateway()) {
					errorMsg = MgrUtil.getUserMessage(
							"error.hiveAp.systempower.notsupported", "CVG");
				} else {
					errorMsg = MgrUtil.getUserMessage(
							"error.hiveAp.systempower.notsupported", "HiveAp");
				}
			}
		} else if (getText("geneva_03.topology.menu.hiveAp.usbmodem.reset").equals(menuText)) {
			if (hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200_LTE_VZ) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.systempower.notsupported", "HiveAp");
			}
		} else if (getText("topology.menu.hiveAp.clear.radsec.credentials")
				.equals(menuText)) {
			if (!checkVersionSupported(hiveAp, "5.1.1.0")) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.feature.support.version",
						MgrUtil.getHiveOSDisplayVersion("5.1.1.0"));
			} else if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100) {
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.feature.support.model",
						MgrUtil.getEnumString("enum.hiveAp.model."
								+ HiveAp.HIVEAP_MODEL_BR100));
			}
		} else if (getText("topology.menu.hiveAp.disable.hiveui.cfg").equals(menuText)) {
			if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.3.0") < 0){
				errorMsg = MgrUtil.getUserMessage(
						"error.hiveAp.feature.support.version",
						MgrUtil.getHiveOSDisplayVersion("6.1.3.0"));
			}else if(!(hiveAp.isBranchRouter() && hiveAp.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_HIVE_UI))){
				errorMsg = MgrUtil.getUserMessage("error.tool.disable.hiveui.config.notsupported");
			}
		} else if (getText("topology.menu.hiveAp.turboModeToggle").equals(menuText)){
			if (hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_230 ){
				errorMsg = MgrUtil
						.getUserMessage("error.hiveAp.cli.model.notSupport");
			}
		}
		
		if(!isSwitchSupport(menuText)){
			if (hiveAp.isSwitch()) {
				errorMsg = MgrUtil
						.getUserMessage("error.hiveAp.cli.model.notSupport");
			}
		}

		boolean is11n = hiveAp.is11nHiveAP();
		jsonObject.put("is11n", is11n);
		// 1: 8 one radio wifi, 2: 16 one radio wifi, 3: no wifi, 4: cvg
		int radioDsType = 0;
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100) {
			radioDsType = 1;
		}
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP 
				|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ
				|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_110) {
			radioDsType = 2;
		}
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200) {
			radioDsType = 3;
		}

		if (hiveAp.isCVGAppliance()
				|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR24
				|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR2124P
				|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR48) {
			radioDsType = 4;
		}
		jsonObject.put("radioDsType", radioDsType);

		return errorMsg;
	}

	
	private boolean isSwitchSupport(String menuText){
		if(getText("topology.menu.diagnostics.showmacroutes").equals(menuText)
			|| getText("topology.menu.diagnostics.showpathmtudiscovery").equals(menuText)
			|| getText("topology.menu.diagnostics.showtcpmss").equals(menuText)
			|| getText("topology.menu.diagnostics.showamrptunnel").equals(menuText)
			|| getText("topology.menu.diagnostics.showvpngretunnel").equals(menuText)
			|| getText("topology.menu.diagnostics.showvpnikeevent").equals(menuText)
			|| getText("topology.menu.diagnostics.showvpnikesa").equals(menuText)
			|| getText("topology.menu.diagnostics.showvpnipsecsa").equals(menuText)
			|| getText("topology.menu.diagnostics.showvpnipsectunnel").equals(menuText)
			|| getText("topology.menu.diagnostics.showdnxpneighbor").equals(menuText)
			|| getText("topology.menu.diagnostics.showdnxpcache").equals(menuText)
			|| getText("topology.menu.diagnostics.showsystempower").equals(menuText)
			|| getText("topology.menu.diagnostics.showmulticastmonitor").equals(menuText)
			|| getText("topology.menu.troubleshoot.clientTrace").equals(menuText)
			//|| getText("topology.menu.troubleshoot.vlan.probe").equals(menuText)
			//|| getText("topology.menu.remoteSniffer").equals(menuText)
			|| getText("topology.menu.statistics.acsp").equals(menuText)
			//|| getText("topology.menu.statistics.interface").equals(menuText)
			|| getText("topology.menu.statistics.summary").equals(menuText)
			//|| getText("topology.menu.firewall.policy").equals(menuText)
			|| getText("topology.menu.alg.sip.name").equals(menuText)
			//|| getText("topology.menu.hiveAp.locateAP").equals(menuText)
			|| getText("topology.menu.hiveAp.clear.radsec.credentials").equals(menuText)
			|| getText("topology.menu.hiveAp.disable.hiveui.cfg").equals(menuText)){
			return false;
		}
		
		return true;
	}
	
	private String getSeparator() {
		return "----------------------------------------------------------\n\n";
	}

	private String getSeparator(String name) {
		return "---------------------- (" + name
				+ ") ----------------------\n\n";
	}

	private String getBr() {
		return "\n\n\n\n";
	}

	private void getAuditMessage() throws Exception {
		jsonObject = new JSONObject();
		HiveAp hiveAp = getSelectedHiveAp();
		jsonObject
				.put("t", getText("topology.menu.hiveAp.configuration.audit"));
		if (null == hiveAp) {
			jsonObject.put("v",
					MgrUtil.getUserMessage("error.cli.object.notfind"));
		} else {
			jsonObject.put("h", hiveAp.getHostName());
			if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
				jsonObject.put("v", MgrUtil
						.getUserMessage("error.hiveAp.notManaged.request"));
				return;
			}
			String message = ConfigAuditProcessor.view(
					BeConfigModule.ConfigType.AP_AUDIT, hiveAp);
			jsonObject.put("v", message);
		}
	}

	private void getNodeId() throws Exception {
		jsonObject = new JSONObject();
		HiveAp hiveAp = getSelectedHiveAp();
		if (null == hiveAp) {
			jsonObject.put("v", "unknow");
		} else {
			jsonObject.put("v", hiveAp.getMacAddress());
		}
	}

	private void getApInfo() throws Exception {
		jsonObject = new JSONObject();
		HiveAp hiveAp = getSelectedHiveAp();
		if (null == hiveAp) {
			// filled content value;
			jsonObject.put("v",
					MgrUtil.getUserMessage("error.cli.object.notfind"));
			return;
		}
		if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			jsonObject.put("v",
					MgrUtil.getUserMessage("error.hiveAp.notManaged.request"));
			return;
		}
		String hostName = hiveAp.getHostName();
		String ip = hiveAp.getIpAddress();
		String mac = hiveAp.getMacAddress();
		String location = hiveAp.getLocation();
		jsonObject.put("h", hostName);
		jsonObject.put("id", hiveAp.getId());
		jsonObject.put("domainId", hiveAp.getOwner().getId());
		jsonObject.put("apIp", ip == null ? "N/A" : ip);
		jsonObject.put("apMac", mac == null ? "N/A" : mac);
		jsonObject.put("apLocation", location == null ? "N/A" : location);

		long count = HiveApMonitor.getActiveClientCount(hiveAp);
		jsonObject.put("apClientCount", String.valueOf(count));

		String str_ssids = getActiveSsidText(mac);
		jsonObject.put("apActiveSsid", str_ssids);
		
		jsonObject.put("showSSIDs", hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP);
	}

	private void updateSsidInfo() throws Exception {
		jsonObject = new JSONObject();
		HiveAp hiveAp = getSelectedHiveAp();
		if (null == hiveAp) {
			return;
		}
		// to request SSID first;
		List<Byte> tableIDList = new ArrayList<Byte>();
		tableIDList.add(BeCommunicationConstant.STATTABLE_AHXIF);
		Map<Byte, List<HmBo>> results = HmBePerformUtil.syncQueryStatistics(
				hiveAp, tableIDList);
		if (null != results) {
			List<HmBo> result = results
					.get(BeCommunicationConstant.STATTABLE_AHXIF);
			if (null != result && result.size() > 0) {
				// get the new value from database;
				String str_ssids = getActiveSsidText(hiveAp.getMacAddress());
				jsonObject.put("apActiveSsid", str_ssids);
			}
		}
	}

	private String getActiveSsidText(String macAddress) {
		List<?> list_ssid = QueryUtil.executeQuery("select ssidName from "
				+ AhLatestXif.class.getSimpleName(), null, new FilterParams(
				"apMac", macAddress));
		Set<String> activeSsids = new HashSet<String>();
		String str_ssids;

		for (Object obj : list_ssid) {
			String ssid = (String) obj;
			if (null != ssid && !("".equals(ssid.trim()))
					&& !("N/A".equals(ssid.trim()))) {
				activeSsids.add(ssid);
			}
		}

		if (activeSsids.size() == 0) {
			str_ssids = "N/A";
		} else {
			str_ssids = activeSsids.toString();
		}
		return str_ssids;
	}

	private void updateApInfo() throws Exception {
		long startTime = System.currentTimeMillis();
		
		jsonObject = new JSONObject();
		HiveAp hiveAp = getSelectedHiveAp();
		if (null == hiveAp) {
			return;
		}
		if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			jsonObject.put("msg",
					MgrUtil.getUserMessage("error.hiveAp.notManaged.request"));
			return;
		}
		// The function of Request Device ID and Port ID is not supported for
		// HiveAP with the software version lower than 3.4.0.0.
		if (NmsUtil.compareSoftwareVersion("3.4.0.0", hiveAp.getSoftVer()) < 0) {
			BeTopoModuleUtil.sendLldpCdpQuery(hiveAp);
		}
		
		// send PSE query to switch/switch as BR/normal BR
		if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_SWITCH 
				|| hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
			BeTopoModuleUtil.sendInterfacePSEStatusQuery(hiveAp);
			BeTopoModuleUtil.sendInterfaceAvailabilityQuery(hiveAp);
		}
		
		//send query about LTEVZInfo for BR200-LTE-VZ
		BeTopoModuleUtil.sendRouterLTEVZInfoQuery(hiveAp);
				
		// send query to switch/switch as BR
		if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_SWITCH ||
			(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER && hiveAp.getDeviceInfo().isSptEthernetMore_24())) {
			// retrieve port info(up/down/vlan/auth status/uptime/stp info)
			BeTopoModuleUtil.sendSwitchPortInfoQuery(hiveAp);
			// retrieve port stats
			BeTopoModuleUtil.sendSwitchPortStatsQuery(hiveAp);
			
			// retrieve successfully.
			jsonObject.put("success", true);
			jsonObject.put("id", hiveAp.getId());
		} else {
			// to request ap info first; (switch/switch as BR need not retrieve these info)
			List<Byte> tableIDList = new ArrayList<Byte>();
			tableIDList.add(BeCommunicationConstant.STATTABLE_AHXIF);
			tableIDList.add(BeCommunicationConstant.STATTABLE_AHRADIOATTRIBUTE);
			tableIDList.add(BeCommunicationConstant.STATTABLE_AHNEIGHBOR);
			Map<Byte, List<HmBo>> results = HmBePerformUtil.syncQueryStatistics(
					hiveAp, tableIDList);
			BeInterferenceMapResultEvent interferences = null;
			if ((hiveAp.is11nHiveAP() && NmsUtil.compareSoftwareVersion("3.4.1.0",
					hiveAp.getSoftVer()) <= 0)
					|| NmsUtil.compareSoftwareVersion("3.4.2.0",
							hiveAp.getSoftVer()) <= 0) {
				// 11n support it from 3.4r1, ag20/28 support it from 3.4r2
				interferences = HmBePerformUtil.syncQueryInterferenceStats(hiveAp,
						true, null);
			}
			if (null != results || null != interferences) {
				// retrieve successfully.
				jsonObject.put("success", true);
				jsonObject.put("id", hiveAp.getId());
			} else {
				// message
				String code = "error.monitor.activeClient.refresh.failed";
				jsonObject.put("msg", MgrUtil.getUserMessage(code));
			}
		}
		long endTime = System.currentTimeMillis();
		log.info("method updateApInfo ====>: cost time : " + (endTime-startTime)/1000 + "s " + (endTime-startTime)%1000 +"ms");
	}

	Long hiveApId;
	Long leafNodeId;
	String menuText;
	String value1;
	String value2;

	public Long getHiveApId() {
		return hiveApId;
	}

	public void setHiveApId(Long hiveApId) {
		this.hiveApId = hiveApId;
	}

	public void setLeafNodeId(Long leafNodeId) {
		this.leafNodeId = leafNodeId;
	}

	public void setMenuText(String menuText) {
		this.menuText = menuText;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	protected JSONArray jsonArray = null;

	protected JSONObject jsonObject = null;

	public String getJSONString() {
		if (jsonArray == null) {
			log.debug("getJSONString", "JSON string: " + jsonObject.toString());
			return jsonObject.toString();
		} else {
			log.debug("getJSONString", "JSON string: " + jsonArray.toString());
			return jsonArray.toString();
		}
	}

	private boolean chkLldp;
	private boolean chkCdp;

	private String dumpFileName;
	private String description;
	private String inputPath;

	// private List<String> requestMacs;
	// private boolean requireRemoval = false;

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(inputPath);
		// the files need to be removed after download.
		// requireRemoval = true;
		// FileManager.getInstance().deletefile(inputPath);
	}

	// @Override
	// protected void finalize() throws Throwable {
	// if(requireRemoval && null != requestMacs){
	// AhHiveAPKernelDump.del_dump_location(requestMacs);
	// log.info("finalize", "The files has been removed.");
	// }
	// super.finalize();
	// }

	private boolean sendCliRequestSync(HiveAp ap, String[] clis) {
		BeCommunicationEvent response = BeTopoModuleUtil.sendSyncCliRequest(ap,
				clis, BeCliEvent.CLITYPE_NORMAL, 35);
		try {
			return BeTopoModuleUtil.isCliExeSuccess(response);
		} catch (BeCommunicationDecodeException e) {
			log.error("sendCliRequestSync", "sendCli request failed.", e);
			return false;
		}
	}

	private String sendCliRequestGroupSync(List<HiveAp> hiveAps,
			Map<String, List<String>> mapClis) {
		Map<String, String[]> clisArray = null;
		if (null != mapClis) {
			clisArray = new HashMap<String, String[]>();
			for (String mac : mapClis.keySet()) {
				List<String> clis = mapClis.get(mac);
				String[] cliArray = clis.toArray(new String[clis.size()]);
				clisArray.put(mac, cliArray);
			}
		}
		StringBuilder errorMsg = new StringBuilder("");
		try {
			Map<String, BeCommunicationEvent> responses = sendGroupSyncCliRequests(
					hiveAps, clisArray, BeCliEvent.CLITYPE_NORMAL, 35);
			if (null == responses) {
				log.error("sendCliRequestGroupSync",
						"send synchronized group request. return responses is null.");
				errorMsg.append(MgrUtil
						.getUserMessage("error.capwap.cwp.directory.remove.failed"));
				return errorMsg.toString();
			}
			for (BeCommunicationEvent response : responses.values()) {
				if (response.getMsgType() == BeCommunicationConstant.MESSAGETYPE_CLIRSP) {
					errorMsg.append(
							MgrUtil.getUserMessage(
									"error.capwap.cwp.directory.remove.failed.params",
									response.getAp().getHostName())).append(
							"\n");
				} else if (response.getMsgType() == BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT) {
					response.parsePacket();
					BeCapwapCliResultEvent cliResult = (BeCapwapCliResultEvent) response;
					if (cliResult.getCliResult() != BeCommunicationConstant.CLIRESULT_SUCCESS) {
						errorMsg.append(
								MgrUtil.getUserMessage(
										"error.capwap.cwp.directory.remove.failed.params",
										response.getAp().getHostName()))
								.append("\n");
					}
				}
			}
			return errorMsg.toString();
		} catch (Exception e) {
			log.error("sendCliRequestSync", "catch build packet exception", e);
			errorMsg.append(MgrUtil
					.getUserMessage("error.capwap.cwp.directory.remove.failed"));
			return errorMsg.toString();
		}
	}

	private boolean lldpCdpClear(HiveAp hiveAp) throws Exception {
		boolean isSuccess = true;
		if (chkLldp) {
			String cli = AhCliFactory.getSaveLldpClearCli();
			isSuccess = sendCliRequestSync(hiveAp, new String[] { cli });
			if (!isSuccess)
				return isSuccess;
		}
		if (chkCdp) {
			String cli = AhCliFactory.getSaveCdpClearCli(hiveAp);
			isSuccess = sendCliRequestSync(hiveAp, new String[] { cli });
			if (!isSuccess)
				return isSuccess;
		}
		return isSuccess;
	}

	public boolean getChkLldp() {
		return chkLldp;
	}

	public void setChkLldp(boolean chkLldp) {
		this.chkLldp = chkLldp;
	}

	public boolean getChkCdp() {
		return chkCdp;
	}

	public void setChkCdp(boolean chkCdp) {
		this.chkCdp = chkCdp;
	}

	private String cwpDirectory;

	private List<String> selectedDirs;

	public void setCwpDirectory(String cwpDirectory) {
		this.cwpDirectory = cwpDirectory;
	}

	public void setSelectedDirs(List<String> selectedDirs) {
		this.selectedDirs = selectedDirs;
	}

	private void getCwpDirectory() throws Exception {
		jsonObject = new JSONObject();
		HiveAp hiveAp = null;
		if (null != leafNodeId) {
			MapLeafNode leafNode = QueryUtil.findBoById(MapLeafNode.class,
					leafNodeId, this);
			if (null != leafNode) {
				hiveAp = leafNode.getHiveAp();
			}
		} else {
			Set<Long> selectedIds = getAllSelectedIds();
			if (null != selectedIds && selectedIds.size() == 1) {
				hiveAp = QueryUtil.findBoById(HiveAp.class, selectedIds
						.iterator().next());
			}
		}
		if (null == hiveAp) {
			jsonObject.put("msg",
					MgrUtil.getUserMessage("error.cli.object.notfind"));
		} else {
			jsonObject.put("h", hiveAp.getHostName());
			if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
				jsonObject.put("msg", MgrUtil
						.getUserMessage("error.hiveAp.notManaged.request"));
				return;
			}
			if (!checkVersionSupported(hiveAp, "3.2.1.0")) {
				String message = MgrUtil.getUserMessage(
						"error.hiveAp.feature.support.version",
						MgrUtil.getHiveOSDisplayVersion("3.2.1.0"));
				jsonObject.put("msg", message);
				return;
			}

			// request event;
			BeCWPDirectoryEvent request = new BeCWPDirectoryEvent();
			request.setAp(hiveAp);
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.buildPacket();
			BeCommunicationEvent response = HmBeCommunicationUtil
					.sendSyncRequest(request, 30);

			if (null == response) {
				String message = MgrUtil
						.getUserMessage("error.capwap.cwp.directory.unknown.reason");
				jsonObject.put("msg", message);
			} else if (response.getMsgType() == BeCommunicationConstant.MESSAGETYPE_SHOWCWPDIRECTORYRSP) {
				String message = MgrUtil
						.getUserMessage("error.capwap.cwp.directory.request.failed");
				jsonObject.put("msg", message);
				log.error("getCwpDirectory",
						"send getCwpDirectory request failed, response type:"
								+ response.getResult());
			} else if (response.getMsgType() == BeCommunicationConstant.MESSAGEELEMENTTYPE_CWPDIRECTORYRESULT) {
				BeCWPDirectoryResultEvent result = (BeCWPDirectoryResultEvent) response;
				try {
					result.parsePacket();
					Map<String, List<String>> cwpMap = result
							.getCwpDirectoryMap();
					JSONArray dirs = new JSONArray();
					if (null != cwpMap && cwpMap.size() > 0) {
						for (String dirName : cwpMap.keySet()) {
							JSONObject dir = new JSONObject();
							dir.put("name", dirName);
							if (null != cwpMap.get(dirName)
									&& !cwpMap.get(dirName).isEmpty()) {
								dir.put("ssids", cwpMap.get(dirName));
							}
							dirs.put(dir);
						}
					}
					jsonObject.put("dirs", dirs);
					log.info("getCwpDirectory",
							"Cwp Directory size:" + dirs.length());
				} catch (BeCommunicationDecodeException e) {
					String message = MgrUtil
							.getUserMessage("error.capwap.cwp.directory.result.parse.failed");
					jsonObject.put("msg", message);
					log.error("getCwpDirectory", "parse result event failed.",
							e);
				}
			}
		}
	}

	private void removeCwpDirectory() throws Exception {
		jsonObject = new JSONObject();

		Set<Long> selectedIds = null;
		List<HiveAp> selectedHiveAps = new ArrayList<HiveAp>();
		Map<String, List<String>> mapClis = new HashMap<String, List<String>>();

		if (null != leafNodeId) {// from topology map
			MapLeafNode leafNode = QueryUtil.findBoById(MapLeafNode.class,
					leafNodeId, this);
			if (null != leafNode) {
				selectedIds = new HashSet<Long>();
				selectedIds.add(leafNode.getHiveAp().getId());
			}
		} else {// from managed list view
			selectedIds = getAllSelectedIds();
		}
		if (null != selectedIds) {
			for (Long id : selectedIds) {
				HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class, id);
				if ("remove".equals(cwpDirectory)) {
					if (null != selectedDirs && selectedDirs.size() > 0) {
						List<String> clis = new ArrayList<String>(
								selectedDirs.size());
						for (String dirName : selectedDirs) {
							String cli = AhCliFactory
									.getRemoveWebPageDirCli(dirName);
							log.info("removeCwpDirectory",
									"remove specify web directory cli:" + cli);
							clis.add(cli);
						}
						String saveServerFileCli = AhCliFactory
								.getSaveServerFilesCli();
						clis.add(saveServerFileCli);
						selectedHiveAps.add(hiveAp);
						mapClis.put(hiveAp.getMacAddress(), clis);
					} else {
						selectedHiveAps.add(hiveAp);
						mapClis.put(hiveAp.getMacAddress(), null);
					}
				} else {
					String removeAllCli = AhCliFactory
							.getRemoveAllWebPageDirCli();
					log.info("removeCwpDirectory",
							"remove all web directory cli:" + removeAllCli);
					String saveServerFileCli = AhCliFactory
							.getSaveServerFilesCli();
					List<String> clis = new ArrayList<String>(2);
					clis.add(removeAllCli);
					clis.add(saveServerFileCli);
					selectedHiveAps.add(hiveAp);
					mapClis.put(hiveAp.getMacAddress(), clis);
				}
			}
		}
		if (selectedHiveAps.size() == 1) {
			// single request
			HiveAp hiveAp = selectedHiveAps.get(0);
			List<String> clis = mapClis.get(hiveAp.getMacAddress());
			if (null == clis) {
				String message = MgrUtil
						.getUserMessage("error.capwap.cwp.directory.none.selected");
				jsonObject.put("msg", message);
			} else if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
				jsonObject.put("msg", MgrUtil
						.getUserMessage("error.hiveAp.notManaged.request"));
			} else {
				boolean result = sendCliRequestSync(hiveAp,
						clis.toArray(new String[clis.size()]));
				if (result) {
					jsonObject.put("suc", true);
					if("removeAll".equals(cwpDirectory)){
						jsonObject.put("notRetrieve", true);
					}
				} else {
					String message = MgrUtil
							.getUserMessage("error.capwap.cwp.directory.remove.failed");
					jsonObject.put("msg", message);
				}
			}
		} else if (selectedHiveAps.size() > 1) {
			// multiple requests
			String errorMsg = sendCliRequestGroupSync(selectedHiveAps, mapClis);
			if (null == errorMsg || "".equals(errorMsg.trim())) {
				jsonObject.put("suc", true);
				jsonObject.put("notRetrieve", true);
			} else {
				jsonObject.put("msg", errorMsg);
			}
		}
	}

	public String getDumpFileName() {
		return dumpFileName;
	}

	public void setDumpFileName(String dumpFileName) {
		this.dumpFileName = dumpFileName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/* Show Tech feature */
	private boolean requestTech() {
		boolean result = false;
		try {
			// if running win platform, return
			// String os = System.getProperty("os.name");
			// if (os.toLowerCase().contains("windows")) {
			// setDescription(MgrUtil
			// .getUserMessage("error.licenseFailed.system.error"));
			// } else {
			// linux platform.
			Set<Long> selectedIds = null;
			if (null != leafNodeId) {// from topology map
				HiveAp hiveAp = getSelectedHiveAp();
				if (null != hiveAp) {
					selectedIds = new HashSet<Long>();
					selectedIds.add(hiveAp.getId());
				}
			} else {// from managed list view
				selectedIds = getAllSelectedIds();
			}
			if (null != selectedIds && !selectedIds.isEmpty()) {
				String username = NmsUtil.getHMScpUser();
				String password = NmsUtil.getHMScpPsd();
				if (selectedIds.size() == 1) {
					// single HiveAP
					long selId = selectedIds.iterator().next();
					HiveAp hiveAp = findBoById(HiveAp.class, selId);
					String errorMsg = validateSelectedHiveAp(hiveAp, "3.3.1.0");
					if (null != errorMsg && !"".equals(errorMsg.trim())) {
						setDescription(errorMsg);
					} else {
						String host = NmsUtil.getRunningCapwapServer(hiveAp);
						// String location = AhHiveAPTech.get_tech_location();
						String location = AhDirTools.getTechDir();
						String fileName = getFileName(hiveAp);
						String cli;

						if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
							cli = AhCliFactory.uploadTech(username, password,
									host, location, fileName);
						} else {
							String proxy = hiveAp.getProxyName();
							int proxyPort = hiveAp.getProxyPort();
							String proxyLoginUser = hiveAp.getProxyUsername();
							String proxyLoginPwd = hiveAp.getProxyPassword();
							cli = AhCliFactory.uploadTechViaHttp(host,
									fileName, username, password, proxy,
									proxyPort, proxyLoginUser, proxyLoginPwd);
						}

						BeCommunicationEvent event = BeTopoModuleUtil
								.sendSyncCliRequest(hiveAp,
										new String[] { cli },
										BeCliEvent.CLITYPE_NORMAL, 60);
						boolean isSuc = BeTopoModuleUtil.isCliExeSuccess(event);
						String msg = BeTopoModuleUtil
								.parseCliRequestResult(event);
						Map<String, String> sum = new HashMap<String, String>();
						List<String> macs = new ArrayList<String>();
						macs.add(hiveAp.getMacAddress());
						if (!isSuc) {
							setDescription(msg);
						} else {
							sum.put(getShowTechKey(hiveAp), "Successful: "
									+ msg);
						}
						inputPath = AhHiveAPTech.zip_tech_dump_files(sum, macs);
					}
				} else {
					// multiple HiveAPs
					Map<String, String> sum = new HashMap<String, String>();
					List<BeCliEvent> requests = new ArrayList<BeCliEvent>();
					Map<String, HiveAp> map = new HashMap<String, HiveAp>();
					List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class,
							null, new FilterParams("id", selectedIds));
					String errorMsg = "";
					for (HiveAp hiveAp : list) {
						errorMsg = validateSelectedHiveAp(hiveAp, "3.3.1.0");
						if (null == errorMsg || "".equals(errorMsg.trim())) {
							String host = NmsUtil
									.getRunningCapwapServer(hiveAp);
							// String location =
							// AhHiveAPTech.get_tech_location();
							String location = AhDirTools.getTechDir();
							String fileName = getFileName(hiveAp);
							String cli;

							if (hiveAp.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
								cli = AhCliFactory.uploadTech(username,
										password, host, location, fileName);
							} else {
								String proxy = hiveAp.getProxyName();
								int proxyPort = hiveAp.getProxyPort();
								String proxyLoginUser = hiveAp
										.getProxyUsername();
								String proxyLoginPwd = hiveAp
										.getProxyPassword();
								cli = AhCliFactory.uploadTechViaHttp(host,
										fileName, username, password, proxy,
										proxyPort, proxyLoginUser,
										proxyLoginPwd);
							}

							requests.add(BeTopoModuleUtil.getCliEvent(hiveAp,
									new String[] { cli },
									BeCliEvent.CLITYPE_NORMAL));
							map.put(hiveAp.getMacAddress(), hiveAp);
						} else {
							sum.put(getShowTechKey(hiveAp), "Failed: "
									+ errorMsg);
						}
					}
					if (requests.isEmpty()) {
						setDescription(errorMsg);
					} else {
						List<BeCommunicationEvent> resps = HmBeCommunicationUtil
								.sendSyncGroupRequest(requests, 60);
						if (null != resps && !resps.isEmpty()) {
							for (BeCommunicationEvent event : resps) {
								String mac = event.getApMac();
								String msg = BeTopoModuleUtil
										.parseCliRequestResult(event);
								boolean isSuc = BeTopoModuleUtil
										.isCliExeSuccess(event);
								if (isSuc) {
									sum.put(getShowTechKey(map.get(mac)),
											"Successful: " + msg);
								} else {
									sum.put(getShowTechKey(map.get(mac)),
											"Failed: " + msg);
								}
							}
						} else {
							setDescription("Get Tech from multiple "
									+ NmsUtil.getOEMCustomer()
											.getAccessPonitName() + "s failed.");
						}
					}
					inputPath = AhHiveAPTech.zip_tech_dump_files(sum,
							new ArrayList<String>(map.keySet()));
				}
			}else{
				inputPath = AhHiveAPTech.zip_tech_dump_files(null,null);
			}
			if (null == inputPath || "".equals(inputPath.trim())) {
				setDescription(MgrUtil
						.getUserMessage("error.tech.noFiles"));
			} else {
				result = true;
			}
			// }
		} catch (Exception e) {
			log.error("requestTech", "error when getting tech tar file", e);
			setDescription(MgrUtil.getUserMessage("error.tech.tarFiles.failed"));
		}
		return result;
	}
	
   private void  generateRebootHistoryCsvFile(){
		try{
			List<AhDeviceRebootHistory> list=new ArrayList<AhDeviceRebootHistory>();
			if(null==selectedIds || selectedIds.isEmpty()){
				FilterParams queryFilterParams = null;
				if("managedDeviceAPs".equals(parentOperation)){
					queryFilterParams=new FilterParams("deviceType",HiveAp.Device_TYPE_HIVEAP);
				}else if("managedRouters".equals(parentOperation)){
					queryFilterParams=new FilterParams("deviceType",HiveAp.Device_TYPE_BRANCH_ROUTER);
				}else if("managedSwitches".equals(parentOperation)){
					queryFilterParams=new FilterParams("deviceType",HiveAp.Device_TYPE_SWITCH);
				}else if("managedVPNGateways".equals(parentOperation)){
					queryFilterParams=new FilterParams("deviceType",HiveAp.Device_TYPE_VPN_GATEWAY);
				}
				list=QueryUtil.executeQuery(AhDeviceRebootHistory.class, 
						 new SortParams("receivedTimestamp desc,mac", true), queryFilterParams,getUserContext(),100000);
			}else{
				String sql="select macAddress from "+HiveAp.class.getSimpleName();
				List<?> macList=QueryUtil.executeQuery(sql, null, new FilterParams("id", selectedIds));
				if(!macList.isEmpty()){
					list=QueryUtil.executeQuery(AhDeviceRebootHistory.class, 
							 new SortParams("receivedTimestamp desc,mac", true), new FilterParams("mac", macList),100000);
				}
			}
			writeDBDataToCSVFile(list);
		}catch(Exception e){
			log.error("generateRebootHistoryCsvFile","generateRebootHistoryCsvFile error:"+e.getMessage());
		}
   }
   private void writeDBDataToCSVFile(List<AhDeviceRebootHistory> list) throws Exception{
	   String currentFileDir=AhHiveAPTech.REBOOT_HISTORY_HOME;
		File tmpFileDir = new File(currentFileDir);
		if (!tmpFileDir.exists()) {
			tmpFileDir.mkdirs();
		}
		File tmpFile = new File(currentFileDir+ File.separator + AhHiveAPTech.REBOOT_HISTORY_FILE_NAME);
		FileWriter out =null;
		try{
			out=new FileWriter(tmpFile);
			StringBuffer strOutput = new StringBuffer();
			strOutput.append(getText("glasgow_18.config.reboot.history.DeviceName.title")).append(",")
			.append(getText("glasgow_18.config.reboot.history.DeviceMAC.title")).append(",")
			.append(getText("glasgow_18.config.reboot.history.DeviceSN.title")).append(",")
			.append(getText("glasgow_18.config.reboot.history.DeviceModel.title")).append(",")
			.append(getText("glasgow_18.config.reboot.history.DeviceVersion.title")).append(",")
			.append(getText("glasgow_18.config.reboot.history.Topology.title")).append(",")
			.append(getText("glasgow_18.config.reboot.history.RebootType.title")).append(",")
			.append(getText("glasgow_18.config.reboot.history.RebootTimestamp.title")).append(",")
			.append(getText("glasgow_18.config.reboot.history.ReceiveTimestamp.title"));
            if(getShowDomain()){
            	strOutput.append(",").append(getText("config.domain"));
			}
            strOutput.append("\n");
			out.write(strOutput.toString());
			out.flush();
			if(list.isEmpty()){
				return;
			}
			//write db data to file
			strOutput=new StringBuffer();
			int index=0;
			for(AhDeviceRebootHistory rh:list){
				rh.setLogTimeZone(getUserTimeZone());
				String mac=rh.getMac();
				String deviceName="";
				String deviceSN="";
				String deviceModel="";
				String deviceVersion="";
				String topology="";
				String rebootType=rh.getRebootTypeStr();
				long rebootTimestamp=rh.getRebootTimestamp();
				long receiveTimestamp=rh.getReceivedTimestamp();
				String domainName="";
				SimpleHiveAp hiveAp=CacheMgmt.getInstance().getSimpleHiveAp(mac);
				if(null!=hiveAp){
					deviceName=hiveAp.getHostname();
					deviceSN=hiveAp.getSerialNumber();
					deviceModel=HiveAp.getDeviceModelName(hiveAp.getHiveApModel());
					deviceVersion=hiveAp.getSoftVer();
					topology=hiveAp.getMapName();
					HmDomain hmDomain=CacheMgmt.getInstance().getCacheDomainById(hiveAp.getDomainId());
					if(null!=hmDomain){
						domainName=hmDomain.getDomainName();
					}
				}
				strOutput.append(deviceName).append(",").append(mac).append(",");
				strOutput.append(deviceSN).append(",").append(deviceModel).append(",");
				strOutput.append(deviceVersion).append(",").append(topology).append(",");
				strOutput.append(rebootType).append(",");
				strOutput.append(rh.getTimestampStr(rebootTimestamp)).append(",");
				strOutput.append(rh.getTimestampStr(receiveTimestamp));
				if(getShowDomain()){
		          strOutput.append(",").append(domainName);
				}
		        strOutput.append("\n");
				if(index>=1000){
					index=0;
					out.write(strOutput.toString());
					out.flush();
					strOutput=new StringBuffer();
					continue;
				}
			}
			out.write(strOutput.toString());
			out.flush();
		}catch(Exception e){
			throw e;
		}finally{
			if(null!=out){
				out.close();
			}
		}
   }
	private String getShowTechKey(HiveAp hiveAp) {
		return "[" + NmsUtil.getOEMCustomer().getAccessPonitName()
				+ " MAC Address:" + hiveAp.getMacAddress() + ", Host Name:"
				+ hiveAp.getHostName() + "]";
	}

	private String getFileName(HiveAp hiveAp) {
//		if (hiveAp.is11nHiveAP()) {
//			return hiveAp.getMacAddress() + ".tar.gz";
//		}else if(hiveAp.isSwitchProduct()){
//			//when sr2024 work as switch or br, the cli is the same.
//			return hiveAp.getMacAddress() + ".tar.gz";
//		}else{
//			return hiveAp.getMacAddress() + ".txt";
//		}
		
		// fix bug 28278
		return hiveAp.getMacAddress() + ".tar.gz";
	}

	/* Show Tech end */

	public String getLedBlink() {
		return ledBlink;
	}

	public void setLedBlink(String ledBlink) {
		this.ledBlink = ledBlink;
	}

	public String getLedColor() {
		return ledColor;
	}

	public void setLedColor(String ledColor) {
		this.ledColor = ledColor;
	}

	public boolean isNoLed() {
		return noLed;
	}

	public void setNoLed(boolean noLed) {
		this.noLed = noLed;
	}

	/* ======= multicastMonitor start ======= */
	private String wifiInterfaceId;

	public String getWifiInterfaceId() {
		return wifiInterfaceId;
	}

	public void setWifiInterfaceId(String wifiInterfaceId) {
		this.wifiInterfaceId = wifiInterfaceId;
	}

	private String stringForTitle;

	public String getStringForTitle() {
		return stringForTitle;
	}

	public void setStringForTitle(String stringForTitle) {
		this.stringForTitle = stringForTitle;
	}

	public String getParentOperation() {
		return parentOperation;
	}

	public void setParentOperation(String parentOperation) {
		this.parentOperation = parentOperation;
	}

	public List<CheckItem> getAvailableWifiInterface() {
		List<CheckItem> availableWifiInterfaces = new ArrayList<CheckItem>();
		CheckItem item = new CheckItem(0l, "wifi0");
		availableWifiInterfaces.add(item);
		item = new CheckItem(1l, "wifi1");
		availableWifiInterfaces.add(item);
		return availableWifiInterfaces;
	}

	/* ======= multicastMonitor end ======= */
}