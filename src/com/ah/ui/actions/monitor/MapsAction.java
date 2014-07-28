package com.ah.ui.actions.monitor;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeTopoUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.MapNodeUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.PlanToolConfig;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateType;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.PlannedApMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapLink;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.MapSettings;
import com.ah.bo.monitor.PlannedAP;
import com.ah.bo.monitor.Trex;
import com.ah.bo.monitor.Vertex;
import com.ah.bo.monitor.Wall;
import com.ah.bo.network.VpnService;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.performance.AhVPNStatus;
import com.ah.bo.performance.AhVPNStatus.VpnStatus;
import com.ah.bo.performance.AhXIf;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.wlan.RadioProfile;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.events.impl.BoObserver;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.tools.PlanToolAction;
import com.ah.util.CheckItem;
import com.ah.util.CountryCode;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.HmException;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.compress.tar.TarArchive;
import com.ah.util.values.BooleanMsgPair;
import com.ah.util.xml.topo.TopoXMLConvertor;

/*
 * @author Chris Scheers
 */

public class MapsAction extends DrawHeatmapAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			MapsAction.class.getSimpleName());

	public String execute() throws Exception {
		Date start = new Date();
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		try {
			planToolConfig = PlanToolAction.getPlanToolConfig(domainId);
			MapContainerNode mapContainerNode = id == null ? null
					: (MapContainerNode) findBoById(MapNode.class, id, this);
			if ("mapClient".equals(operation)) {
				log.info("execute", "locate client: " + clientId + ", circle: "
						+ ch1);
				if (clientId != null) {
					// AhClientSession client = QueryUtil.findBoById(
					// AhClientSession.class, clientId);
					AhClientSession client = DBOperationUtil.findBoById(
							AhClientSession.class, clientId);
					if (client != null) {
						bssid = client.getClientMac();
						bssidType = BSSID_TYPE_CLIENT;
						if (ch1 == 1) {
							bssid = '|' + bssid;
						}
					}
				}
				return mapPage(start);
			} else if ("mapRogue".equals(operation)) {
				log.info("execute", "locate rogue: " + clientId);
				if (clientId != null) {
					Idp rogue = QueryUtil.findBoById(Idp.class, clientId);
					if (rogue != null) {
						bssid = rogue.getIfMacAddress();
						bssidType = BSSID_TYPE_ROGUE;
					}
				}
				return mapPage(start);
			} else if ("mapDetails".equals(operation)) {
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				mapAlarmsCache.setMapId(id, pageId);
				mapAlarmsCache.setTreeNodeExpanded(id, true);
				prepareMapNode(mapContainerNode);
				if (null != HmBeTopoUtil.getPollingController()) {
					// put this map container into polling list.
					HmBeTopoUtil.getPollingController().addContainer(
							this.request.getSession(), id);
				}
				// save the selected map id into session
				setSelectedMapId(id);
				return "json";
			} else if ("nodes".equals(operation)) {
				log.info("execute", "Scale: " + scale + ", apLabels: "
						+ apLabels);
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				jsonObject = prepareNodes(
						mapAlarmsCache.getMapNodes(id, pageId), pageId, scale);
				Map<Short, Short> ch1IndexMap = createChIndexMap(null, null);
				Map<Short, Short> ch2IndexMap = createChIndexMap(null, null);
				List<MapContainerNode> floors = BoMgmt.getLocationTracking()
						.assignChannels(mapContainerNode, planToolConfig,
								ch1IndexMap, ch2IndexMap);
				if (mapContainerNode.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
					// Resetting floor cache
					FloorCache cache = new FloorCache(id);
					saveChannelAssignments(mapContainerNode, floors, cache,
							ch1IndexMap, ch2IndexMap, false);
					log.info("Resetting floor cache.");
					setFloorCache(cache);
				} else {
					removeFloorCache();
				}
				jsonObject.put("pageId", pageId);
				jsonObject.put(
						"planned",
						preparePlannedAPs(mapContainerNode, false, scale, null,
								ch1IndexMap, ch2IndexMap));
				jsonObject
						.put("perim",
								preparePerimeter(
										mapContainerNode.getPerimeter(), scale));
				jsonObject.put("walls",
						prepareWalls(mapContainerNode.getWalls(), scale));
				log.debug("execute", "Prepared nodes: " + jsonObject.toString());
				log.info_ln("maps.action nodes: "
						+ (new Date().getTime() - start.getTime()) + " ms.");
				return "json";
			} else if ("links".equals(operation)) {
				log.info("execute", "Scale: " + scale);
				jsonObject = prepareLinks(mapContainerNode.getChildLinks()
						.values(), pageId, getShowRssi());
				log.debug("execute", "Prepared links: " + jsonObject.toString());
				log.info_ln("maps.action links: "
						+ (new Date().getTime() - start.getTime()) + " ms.");
				return "json";
			} else if ("alarms".equals(operation)) {
				log.info("execute", "Polling for new alarms, scale: " + scale
						+ ", rogueChecked?" + rogueChecked + ", clientChecked?"
						+ clientChecked + ", summaryChecked?" + summaryChecked);
				prepareAlarms(id);
				log.info("execute", "Returning alarms:" + jsonObject.toString());
				return "json";
			} else if ("saveNode".equals(operation)) {
				log.info("execute", "Scale: " + scale + ", nodeId: " + bssid
						+ ", x: " + pwr1 + ", y: " + ch1);
				MgrUtil.removeSessionAttribute(SessionKeys.MR_HEAT_MAP);
				saveNode(Long.parseLong(bssid.substring(1)), pwr1, ch1);
				return "json";
			} else if ("saveNodes".equals(operation)) {
				log.info("execute", "Scale: " + scale);
				MgrUtil.removeSessionAttribute(SessionKeys.MR_HEAT_MAP);
				if (selectedIds != null) {
					for (int i = 0; i < selectedIds.size(); i++) {
						saveNode(selectedIds.get(i), xs.get(i), ys.get(i));
					}
				}
				return null;
			} else if ("validatePerimeter".equals(operation)) {
				log.info("execute", "Scale: " + scale);
				Point2D[] walls = null;
				if (xs != null) {
					walls = new Point2D[xs.size()];
					for (int i = 0; i < xs.size(); i++) {
						walls[i] = new Point2D.Double(xs.get(i), ys.get(i));
						log.info("execute",
								"Perimeter node: " + walls[i].toString());
					}
				}
				jsonArray = new JSONArray(validatePerimeter(walls,
						mapContainerNode.getPerimeter(), scale));
				return "json";
			} else if ("clients".equals(operation)) {
				log.info("execute", "Scale: " + scale);
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				jsonObject = prepareClients(mapContainerNode,
						mapAlarmsCache.getMapNodes(id, pageId), pageId, scale);
				log.debug("execute",
						"Prepared clients: " + jsonObject.toString());
				return "json";
			} else if ("rogues".equals(operation)) {
				log.info("execute", "Scale: " + scale);
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				jsonObject = prepareRogues(mapContainerNode,
						mapAlarmsCache.getMapNodes(id, pageId), pageId, scale);
				log.debug("execute",
						"Prepared rogues: " + jsonObject.toString());
				return "json";
			} else if ("acspNbrRssi".equals(operation)) {
				log.info("execute", "acspNbrRssi Scale: " + scale
						+ ", frequency: " + frequency + ", leafNodeId: "
						+ acspId);
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				jsonObject = BoMgmt.getLocationTracking().acspNbrRssi(
						mapContainerNode,
						mapAlarmsCache.getMapNodes(id, pageId), pageId, scale,
						acspId, (frequency & 1) > 0);
				return "json";
			} else if ("clientRssi".equals(operation)) {
				log.info("execute", "Scale: " + scale + ", bssid: " + bssid);
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				jsonArray = new JSONArray(BoMgmt.getLocationTracking()
						.clientRssi(mapContainerNode,
								mapAlarmsCache.getMapNodes(id, pageId), pageId,
								scale, bssid));
				log.debug("execute", "Prepared rssi: " + jsonArray.toString());
				return "json";
			} else if ("clientRssiArea".equals(operation)) {
				log.info("execute", "Scale: " + scale + ", bssid: " + bssid
						+ ", canvas: (" + canvasWidth + ", " + canvasHeight
						+ ")");
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				BoMgmt.getLocationTracking().clientRssi(mapContainerNode,
						mapAlarmsCache.getMapNodes(id, pageId), pageId, scale,
						bssid);
				streamImage(BoMgmt.getLocationTracking().drawRssiArea(
						mapContainerNode, canvasWidth, canvasHeight));
				return null;
			} else if ("rogueRssi".equals(operation)) {
				log.info("execute", "Scale: " + scale + ", bssid: " + bssid);
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				jsonArray = new JSONArray(BoMgmt.getLocationTracking()
						.rogueRssi(mapContainerNode,
								mapAlarmsCache.getMapNodes(id, pageId), pageId,
								scale, bssid));
				log.debug("execute", "Prepared rssi: " + jsonArray.toString());
				return "json";
			} else if ("rogueRssiArea".equals(operation)) {
				log.info("execute", "Scale: " + scale + ", bssid: " + bssid
						+ ", canvas: (" + canvasWidth + ", " + canvasHeight
						+ ")");
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				BoMgmt.getLocationTracking().rogueRssi(mapContainerNode,
						mapAlarmsCache.getMapNodes(id, pageId), pageId, scale,
						bssid);
				streamImage(BoMgmt.getLocationTracking().drawRssiArea(
						mapContainerNode, canvasWidth, canvasHeight));
				return null;
			} else if ("calibrateClientRssi".equals(operation)) {
				log.info("execute", "Scale: " + scale + ", bssid: " + bssid);
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				log.info("execute", "x: " + xs + ", y: " + ys);
				int added = 0;
				if (xs != null && ys != null) {
					double x = reverseScale(xs.get(0));
					double y = reverseScale(ys.get(0));
					log.info("execute", "x: " + x + ", y: " + y);
					added = BoMgmt.getLocationTracking().calibrateClient(
							mapContainerNode,
							mapAlarmsCache.getMapNodes(id, pageId), pageId,
							scale, bssid, x, y);
				}
				jsonObject = new JSONObject();
				jsonObject.put("added", added);
				jsonObject.put("mac", bssid);
				log.debug("execute", "calibrate rssi: " + jsonObject.toString());
				return "json";
			} else if ("uncalibrateClientRssi".equals(operation)) {
				log.info("execute", "Scale: " + scale + ", bssid: " + bssid);
				MgrUtil.removeSessionAttribute(SessionKeys.MR_HEAT_MAP);
				int removed = QueryUtil.bulkRemoveBos(Trex.class,
						new FilterParams("tid", bssid), mapContainerNode
								.getOwner().getId());
				log.info("execute", removed + " measurements removed.");
				jsonObject = new JSONObject();
				jsonObject.put("removed", removed);
				jsonObject.put("mac", bssid);
				log.debug("execute",
						"uncalibrate rssi: " + jsonObject.toString());
				return "json";
			} else if ("calibrateRogueRssi".equals(operation)) {
				log.info("execute", "Scale: " + scale + ", bssid: " + bssid);
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				log.info("execute", "x: " + xs + ", y: " + ys);
				int added = 0;
				if (xs != null && ys != null) {
					double x = reverseScale(xs.get(0));
					double y = reverseScale(ys.get(0));
					log.info("execute", "x: " + x + ", y: " + y);
					added = BoMgmt.getLocationTracking().calibrateRogue(
							mapContainerNode,
							mapAlarmsCache.getMapNodes(id, pageId), pageId,
							scale, bssid, x, y);
				}
				jsonObject = new JSONObject();
				jsonObject.put("added", added);
				log.debug("execute", "calibrate rssi: " + jsonObject.toString());
				return "json";
			} else if ("uncalibrateRogueRssi".equals(operation)) {
				log.info("execute", "Scale: " + scale + ", bssid: " + bssid);
				MgrUtil.removeSessionAttribute(SessionKeys.MR_HEAT_MAP);
				int removed = QueryUtil.bulkRemoveBos(Trex.class,
						new FilterParams("tid", bssid), mapContainerNode
								.getOwner().getId());
				log.info("execute", removed + " measurements removed.");
				jsonObject = new JSONObject();
				jsonObject.put("removed", removed);
				log.debug("execute",
						"uncalibrate rssi: " + jsonObject.toString());
				return "json";
			} else if ("locateClient".equals(operation)) {
				log.info("execute", "Locating client: " + clientId
						+ ", circle: " + ch1);
				jsonObject = BoMgmt
						.getLocationTracking()
						.validateClientLocation(getDomain(), clientId, ch1 == 1);
				log.debug("execute",
						"locate client result: " + jsonObject.toString());
				return "json";
			} else if ("locateRogue".equals(operation)) {
				log.info("execute", "Locating rogue: " + clientId);
				jsonObject = BoMgmt.getLocationTracking()
						.validateRogueLocation(getDomain(), clientId);
				log.debug("execute",
						"locate rogue result: " + jsonObject.toString());
				return "json";
			} else if ("heatMapHr".equals(operation)) {
				log.info("execute", "HR version of latchId: " + latchId
						+ ", frequency: " + frequency);
				if (BoMgmt.getLocationTracking().estimateRssiHr(
						mapContainerNode, latchId)) {
					streamMapImage(mapContainerNode,
							MapSettings.HEATMAP_RESOLUTION_HIGH, frequency,
							layers, (short) -1, 1, null, (short) 0, 0, 0);
				}
				return null;
			} else if ("heatMap".equals(operation)) {
				log.info("execute", "scale: " + scale);
				log.info("execute", "rssiThreshold: -" + rssiThreshold);
				if (scale > 0) {
					log.info("execute",
							"canvas: (" + canvasWidth + ", " + canvasHeight
									+ "), layers: " + layers + ", mac: "
									+ bssid + ", acspNbr: " + acspId
									+ ", nextId: " + nextId + ", position ("
									+ xs + ", " + ys + ")" + ", actualWidth: "
									+ mapContainerNode.getActualWidth());
					MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
					Set<MapNode> nodes = mapAlarmsCache.getMapNodes(id, pageId);
					log.info("execute", "Preparing heat map, latch ID: "
							+ latchId);
					if (nodes.size() > 0 && mapContainerNode != null
							&& canvasWidth > 0 && canvasHeight > 0
							&& mapContainerNode.getActualWidth() > 0) {
						log.info("execute", "# nodes: " + nodes.size());
						double x = 0, y = 0;
						if (xs != null && ys != null) {
							x = reverseScale(xs.get(0));
							y = reverseScale(ys.get(0));
						}
						log.info("execute", "x: " + x + ", y: " + y);
						int heatmapResolution = BoMgmt.getLocationTracking()
								.computeRssi(mapContainerNode, nodes,
										canvasWidth, canvasHeight, bssid,
										acspId, nextId, x, y,
										(frequency & 1) > 0,
										(frequency & 2) > 0,
										getShadesPerColor(), latchId);
						mapAlarmsCache.createHeatMapLatch(latchId,
								heatmapResolution);
						log.info("execute", "Create channels latch ID: "
								+ latchId);
						if (heatmapResolution < 0) {
							// No measurements or calibration failed (clbrb_w)
							streamImage(new BufferedImage(1, 1,
									BufferedImage.TYPE_INT_ARGB));
						} else {
							streamMapImage(mapContainerNode, heatmapResolution,
									frequency, layers, (short) -1, 1, null,
									(short) 0, 0, 0);
						}
					}
				}
				return null;
			} else if ("channels".equals(operation)) {
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				Set<MapNode> nodes = mapAlarmsCache.getMapNodes(id, pageId);
				log.info("execute", "Get channels latch ID: " + latchId);
				int heatmapResolution = MapSettings.HEATMAP_RESOLUTION_HIGH;
				if (nodes.size() > 0 && mapContainerNode != null) {
					if (latchId == null) {
						BoMgmt.getLocationTracking()
								.fetchRadioAttributes(nodes);
					} else {
						Integer hmr = mapAlarmsCache.getHeatMapLatch(latchId);
						if (hmr == null) {
							log.info("execute",
									"This is an obsoleted channel request, just return empty array.");
							jsonArray = new JSONArray();
							return "json";
						} else {
							heatmapResolution = hmr;
						}
					}
				}
				log.info("execute", "Preparing node labels.");
				jsonArray = new JSONArray(prepareNodeChannels(nodes, pageId,
						(frequency & 1) > 0));
				if (jsonArray.length() > 0) {
					JSONObject fo = ((JSONObject) jsonArray.get(0));
					fo.put("latchId", latchId);
					fo.put("hmr", heatmapResolution);
				} else if (heatmapResolution < 0) {
					JSONObject fo = new JSONObject();
					fo.put("pageId", 0);
					fo.put("latchId", latchId);
					fo.put("hmr", heatmapResolution);
					Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
					jsonNodes.add(fo);
					jsonArray = new JSONArray(jsonNodes);
				}
				log.debug("execute",
						"Prepared node labels: " + jsonArray.toString());
				return "json";
			} else if ("nextIds".equals(operation)) {
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				Set<MapNode> nodes = mapAlarmsCache.getMapNodes(id, pageId);
				log.info("execute", "Get next IDs for: " + latchId
						+ ", frequency: " + frequency);
				jsonObject = new JSONObject();
				int heatmapResolution = BoMgmt.getLocationTracking()
						.fetchActiveNbrs(mapContainerNode, nodes,
								(frequency & 1) > 0, latchId, jsonObject);
				log.info("execute", "Preparing node labels.");
				jsonObject.put("latchId", latchId);
				jsonObject.put("hmr", heatmapResolution);
				jsonObject.put("pageId", pageId);
				jsonObject.put(
						"channels",
						new JSONArray(prepareNodeChannels(nodes, pageId,
								(frequency & 1) > 0)));
				return "json";
			} else if ("rssiRange".equals(operation)) {
				log.info("execute", "rssiThreshold: -" + rssiThreshold);
				setSelectedRSSIThreshold(rssiThreshold);
				jsonArray = new JSONArray(BoMgmt.getLocationTracking()
						.getRssiRange(getShadesPerColor()));
				log.debug("execute", "RSSI range: " + jsonArray.toString());
				return "json";
			} else if ("addSimAp".equals(operation)) {
				log.info("execute", "addSimAp, scale: " + scale
						+ ", rssiThreshold: -" + rssiThreshold
						+ ", snrThreshold: " + snrThreshold
						+ ", rateThreshold: " + rateThreshold);
				if (scale > 0 && mapContainerNode != null && canvasWidth > 0
						&& mapContainerNode.getActualWidth() > 0) {
					log.info("execute",
							"canvas width: " + canvasWidth + ", frequency: "
									+ frequency + ", layers: " + layers
									+ ", hwModel: " + hwModel + ", pwr ("
									+ pwr1 + ", " + pwr2 + "), ch (" + ch1
									+ ", " + ch2 + "), all ch (" + ch1s + ", "
									+ ch2s + "), all chi (" + ch1is + ", "
									+ ch2is + ", actualWidth: "
									+ mapContainerNode.getActualWidth()
									+ ", locate X:" + latchId);
					// disable a radio which has one radio.
					if (hwModel == HiveAp.HIVEAP_MODEL_110
							|| hwModel == HiveAp.HIVEAP_MODEL_BR200_WP
							|| hwModel == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ) {
						planToolConfig
								.setWifi0Enabled((frequency & 1) > 0 ? false
										: true);
						planToolConfig
								.setWifi1Enabled((frequency & 1) > 0 ? true
										: false);
					} else {
						planToolConfig.setWifi0Enabled(true);
						planToolConfig
								.setWifi1Enabled(hwModel != HiveAp.HIVEAP_MODEL_BR100);
					}
					planToolConfig.setDefaultApType(hwModel);
					planToolConfig.setWifi0Channel(ch1);
					planToolConfig.setWifi0Power(pwr1);
					planToolConfig.setWifi1Channel(ch2);
					planToolConfig.setWifi1Power(pwr2);
					PlanToolAction.savePlanToolConfig(planToolConfig,
							getDomain());
					addSimulatedAP(mapContainerNode, planToolConfig, latchId);
				}
				return "json";
			} else if ("autoSimAps".equals(operation)) {
				log.info("execute", "Auto Sim APs, rssiThreshold: -"
						+ rssiThreshold + ", frequency: " + frequency
						+ ", canvas (" + canvasWidth + ", " + canvasHeight
						+ "), hwModel: " + hwModel + ", pwr (" + pwr1 + ", "
						+ pwr2 + "), ch (" + ch1 + ", " + ch2 + ")");
				if (mapContainerNode.getEnvironment() == EnumConstUtil.MAP_ENV_AUTO) {
					mapContainerNode
							.setEnvironment(EnumConstUtil.MAP_ENV_ENTERPRISE);
					QueryUtil.updateBo(mapContainerNode);
				}
				// disable a radio which has one radio.
				if (hwModel == HiveAp.HIVEAP_MODEL_110
						|| hwModel == HiveAp.HIVEAP_MODEL_BR200_WP
						|| hwModel == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ) {
					planToolConfig.setWifi0Enabled((frequency & 1) > 0 ? false
							: true);
					planToolConfig.setWifi1Enabled((frequency & 1) > 0 ? true
							: false);
				} else {
					planToolConfig.setWifi0Enabled(true);
					planToolConfig
							.setWifi1Enabled(hwModel != HiveAp.HIVEAP_MODEL_BR100);
				}
				planToolConfig.setDefaultApType(hwModel);
				planToolConfig.setWifi0Channel(ch1);
				planToolConfig.setWifi0Power(pwr1);
				planToolConfig.setWifi1Channel(ch2);
				planToolConfig.setWifi1Power(pwr2);
				PlanToolAction.savePlanToolConfig(planToolConfig, getDomain());
				createAutoSimAps(
						mapContainerNode,
						planToolConfig,
						BoMgmt.getLocationTracking().autoSimAps(
								mapContainerNode, planToolConfig, canvasWidth,
								canvasHeight, (frequency & 1) > 0,
								getShadesPerColor(), 95.5));
				return "json";
			} else if ("updateSimApLaps".equals(operation)) {
				if (scale > 0 && mapContainerNode != null && canvasWidth > 0
						&& mapContainerNode.getActualWidth() > 0) {
					log.info("execute", "updateSimApLaps, canvas ("
							+ canvasWidth + ", " + canvasHeight
							+ "), plannedId: " + bssid + ", hostName: "
							+ hostName + ", pwr (" + pwr1 + ", " + pwr2
							+ "), ch (" + ch1 + ", " + ch2 + "), all ch ("
							+ ch1s + ", " + ch2s + "), all chi (" + ch1is
							+ ", " + ch2is + "), x: " + pwr2 + ", y: " + ch2
							+ ", frequency: " + frequency + ", channel width: "
							+ channelWidth + ", fadeMargin: " + fadeMargin
							+ ", layers: " + layers + ", rssiThreshold: -"
							+ rssiThreshold + ", snrThreshold: " + snrThreshold
							+ ", rateThreshold: " + rateThreshold + ", radio: "
							+ radio);
					boolean useA = (frequency & 1) > 0;
					PlannedAP plannedAP = null;
					Map<Short, Short> ch1IndexMap = null;
					Map<Short, Short> ch2IndexMap = null;
					if (bssid.length() > 0) {
						/*
						 * Update a single Planned AP, either its position
						 * (drag-n-drop), or the radio attributes (update AP
						 * details).
						 */
						long plannedId = Long.parseLong(bssid.substring(1));
						if (pwr1 > 0) {
							/*
							 * Update planned AP
							 */
							plannedAP = BoMgmt.getPlannedApMgmt()
									.updatePlannedAP(mapContainerNode,
											plannedId, hostName, hwModel,
											(short) ch1, (short) ch2,
											(short) pwr1, (short) pwr2, radio);
						} else {
							/*
							 * Move planned AP
							 */
							plannedAP = BoMgmt.getPlannedApMgmt()
									.movePlannedAP(mapContainerNode, plannedId,
											reverseScale(pwr2),
											reverseScale(ch2));
						}
						FloorCache cache = clearNbrAps(mapContainerNode,
								plannedAP, plannedAP.getId(), getFloorCache(id));
						for (PlannedAP ap : mapContainerNode.getPlannedAPs()) {
							if (ap.getId().equals(plannedAP.getId())) {
								ap.apModel = plannedAP.apModel;
								ap.hostName = plannedAP.hostName;
								ap.countryCode = plannedAP.countryCode;
								ap.wifi0Enabled = plannedAP.wifi0Enabled;
								ap.wifi1Enabled = plannedAP.wifi1Enabled;
								ap.wifi0Channel = plannedAP.wifi0Channel;
								ap.wifi1Channel = plannedAP.wifi1Channel;
								ap.wifi0Power = plannedAP.wifi0Power;
								ap.wifi1Power = plannedAP.wifi1Power;
								ap.x = plannedAP.x;
								ap.y = plannedAP.y;
							}
						}
						ch1IndexMap = createChIndexMap(ch1s, ch1is);
						ch2IndexMap = createChIndexMap(ch2s, ch2is);
						List<MapContainerNode> floors = BoMgmt
								.getLocationTracking().assignChannels(
										mapContainerNode, planToolConfig,
										ch1IndexMap, ch2IndexMap);
						saveChannelAssignments(mapContainerNode, floors, cache,
								ch1IndexMap, ch2IndexMap, true);

					} else {
						setSelectedLayers(layers);
					}
					Collection<JSONObject> jsonNodes = getPredictedLap(
							mapContainerNode, useA, plannedAP, (short) 0,
							channelWidth, fadeMargin, false);
					if (plannedAP != null && jsonNodes.size() > 0) {
						JSONObject jo = jsonNodes.iterator().next();
						jo.put("planned",
								preparePlannedAPs(mapContainerNode, true,
										scale, null, ch1IndexMap, ch2IndexMap));
					}
					jsonArray = new JSONArray(jsonNodes);
				}
				return "json";
			} else if ("predictedLap".equals(operation)) {
				if (scale > 0 && mapContainerNode != null && canvasWidth > 0
						&& mapContainerNode.getActualWidth() > 0) {
					log.info("execute", "predictedLap: canvas (" + canvasWidth
							+ ", " + canvasHeight + "), layers: " + layers
							+ ", power: " + pwr1 + ", chi: " + ch1
							+ ", channel width: " + channelWidth
							+ ", fadeMargin: " + fadeMargin + ", plannedId: "
							+ bssid + ", rssiThreshold: -" + rssiThreshold
							+ ", snrThreshold: " + snrThreshold
							+ ", rateThreshold: " + rateThreshold);
					long plannedId = Long.parseLong(bssid.substring(1));
					PlannedAP plannedAP = BoMgmt.getPlannedApMgmt()
							.findPlannedAP(plannedId);
					if (plannedAP != null) {
						getPredictedLap(mapContainerNode, (frequency & 1) > 0,
								plannedAP, (short) ch1, channelWidth,
								fadeMargin, true);
					}
				}
				return null;
			} else if ("cacheSpill".equals(operation)) {
				jsonObject = new JSONObject();
				FloorCache cache = getFloorCache(mapContainerNode.getId());
				if (cache == null || cache.mapChannels == null) {
					jsonObject.put("success", false);
					return "json";
				}
				jsonObject.put("w", cache.mapChannels.length * cache.imgScale);
				jsonObject.put("h", cache.mapChannels[0].length
						* cache.imgScale);
				cacheDoubleBuffer(mapContainerNode, cache, false);
				return "json";
			} else if ("updateSpillCache".equals(operation)) {
				log.info("execute", "updateSpillCache frequency: " + frequency
						+ ", rssiThreshold: -" + rssiThreshold
						+ ", spillOnly: " + showSpill + ", channel width: "
						+ channelWidth);
				jsonObject = new JSONObject();
				FloorCache cache = getFloorCache(mapContainerNode.getId());
				if (cache == null || cache.mapChannels == null) {
					jsonObject.put("success", false);
					return "json";
				}
				if (!showSpill) {
					cache.useA = (frequency & 1) > 0;
					cache.channelWidth = channelWidth;
					cache.shadesPerColor = getShadesPerColor();
				}
				int imageWidth = cache.spillRssi.length;
				int imageHeight = cache.spillRssi[0].length;
				cache.spillRssi = new float[imageWidth][imageHeight];
				cache.spillChannels = new short[imageWidth][imageHeight];
				if (!showSpill) {
					// Don't recalculate these
					cache.mapRssi = new float[imageWidth][imageHeight];
					cache.mapChannels = new short[imageWidth][imageHeight];
				}
				cacheDoubleBuffer(mapContainerNode, cache, showSpill);
				return "json";
			} else if ("planHeatMap".equals(operation)) {
				log.info("execute", "scale: " + scale);
				if (scale > 0 && mapContainerNode != null && canvasWidth > 0
						&& mapContainerNode.getActualWidth() > 0) {
					log.info("execute", "canvas (" + canvasWidth + ", "
							+ canvasHeight + "), rssiThreshold: -"
							+ rssiThreshold);
					int resolution = BoMgmt.getLocationTracking()
							.simApCoverage(mapContainerNode, planToolConfig,
									canvasWidth, canvasHeight,
									(frequency & 1) > 0, getShadesPerColor());
					streamMapImage(mapContainerNode, resolution, frequency, 1,
							(short) -1, 1, null, (short) 0, 0, 0);
				}
				return null;
			} else if ("updateSnrThreshold".equals(operation)) {
				log.info("execute", "Updating SNR threshold to: "
						+ snrThreshold);
				jsonObject = new JSONObject();
				setSelectedSNRThreshold(snrThreshold);
				jsonObject.put("success", true);
				return "json";
			} else if ("updateFadeMargin".equals(operation)) {
				log.info("execute", "Updating fade margin to: " + fadeMargin);
				jsonObject = new JSONObject();
				planToolConfig.setFadeMargin(fadeMargin);
				PlanToolAction.savePlanToolConfig(planToolConfig, getDomain());
				jsonObject.put("success", true);
				return "json";
			} else if ("updateChannelWidth".equals(operation)) {
				log.info("execute", "New channel width: " + channelWidth
						+ ", model: " + hwModel);
				jsonObject = new JSONObject();
				planToolConfig.setChannelWidth(channelWidth);
				PlanToolAction.savePlanToolConfig(planToolConfig, getDomain());
				jsonObject.put("channels", new JSONArray(
						getChannelNaType(hwModel)));
				Map<Short, Short> ch1IndexMap = createChIndexMap(null, null);
				Map<Short, Short> ch2IndexMap = createChIndexMap(null, null);
				List<MapContainerNode> floors = BoMgmt.getLocationTracking()
						.assignChannels(mapContainerNode, planToolConfig,
								ch1IndexMap, ch2IndexMap);
				saveChannelAssignments(mapContainerNode, floors,
						getFloorCache(id), ch1IndexMap, ch2IndexMap, true);
				jsonObject.put("pageId", pageId);
				jsonObject.put(
						"planned",
						preparePlannedAPs(mapContainerNode, false, scale, null,
								ch1IndexMap, ch2IndexMap));
				return "json";
			} else if ("stopMapRefreshing".equals(operation)) {
				log.info("execute", "stop map refreshing for user:"
						+ getUserContext().getUserName());
				if (null != HmBeTopoUtil.getPollingController()) {
					HmBeTopoUtil.getPollingController().removeContainer(
							request.getSession());
				}
				return null;
			} else if ("expandMapNode".equals(operation)) {
				log.info("execute", "operation:" + operation + ", map node:"
						+ id + ", mapExpanded:" + mapExpanded);
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				mapAlarmsCache.setTreeNodeExpanded(id, mapExpanded);
				return "json";
			} else if ("createDownloadData".equals(operation)) {
				log.info("execute-createDownloadData", "frequency: "
						+ frequency + ", rssiThreshold: -" + rssiThreshold
						+ ", rateThreshold: -" + rateThreshold
						+ ", channelWidth: -" + channelWidth
						+ ", snrThreshold: " + snrThreshold + ", layers: "
						+ layers + ", gridChecked: " + gridChecked);
				jsonObject = new JSONObject();
				String pdfCreateFlagName = getDomain().getDomainName()
						+ "_createingFlag";
				String pdfCreateSuccessFlagName = getDomain().getDomainName()
						+ "_createSuccessFlag";
				String pdfCreateMapName = getDomain().getDomainName()
						+ "_createingMapName";
				if (!"T".equals(MgrUtil.getSessionAttribute(pdfCreateFlagName))) {
					MgrUtil.setSessionAttribute(pdfCreateFlagName, "T");
					MgrUtil.setSessionAttribute(pdfCreateMapName,
							mapContainerNode.getMapName());
				} else {
					jsonObject.put("result", false);
					String msg = "The map of '"
							+ MgrUtil.getSessionAttribute(pdfCreateMapName)
							+ "' is creating PDF report, please wait a moment.";
					jsonObject.put("message", msg);
					return "json";
				}

				MgrUtil.setSessionAttribute(pdfCreateSuccessFlagName, "F");
				
				TopoPdfReport report = new TopoPdfReport(this);

				String reportFile = "/tmp/report_"
						+ getDomain().getDomainName() + ".pdf";
				boolean result = report.createPdf(mapContainerNode.getId(),
						reportFile, gridChecked, channelWidth, acspId,
						planToolConfig);

				jsonObject.put("result", result);

				if (!result) {
					jsonObject
							.put("message",
									MgrUtil.getUserMessage("export.pdf.report.message"));
				} else {
					MgrUtil.setSessionAttribute(pdfCreateSuccessFlagName, "T");
				}
				MgrUtil.setSessionAttribute(pdfCreateFlagName, "F");
				return "json";
			} else if ("getPdfCreateFlag".equals(operation)) {
				String pdfCreateFlagName = getDomain().getDomainName()
						+ "_createingFlag";
				String pdfCreateFlag = (String) MgrUtil
						.getSessionAttribute(pdfCreateFlagName);
				boolean flag = false;
				if ("T".equals(pdfCreateFlag)) {
					flag = true;
				}
				
				boolean success = false;
				String pdfCreateSuccessFlagName = getDomain().getDomainName()
						+ "_createSuccessFlag";
				String pdfCreateSuccessFlag = (String) MgrUtil
						.getSessionAttribute(pdfCreateSuccessFlagName);
				if("T".equals(pdfCreateSuccessFlag)){
					success =true;
				}
				jsonObject = new JSONObject();
				jsonObject.put("pdfCreatingFlag", flag);
				jsonObject.put("pdfCreatSuccessFlag", success);
				return "json";
			} else if ("download".equals(operation)) {
				// check the file if under the allowed folder
				// StringUtils.isBlank(fileName) export pdf file
				boolean fileAllowed = true;
				if (!StringUtils.isBlank(fileName)) {
					fileAllowed = checkFileExistUnderDirectoryAllowed("/tmp/fdData/" + getDomain().getDomainName(), fileName);
				}
				File file = new File(getInputPath());
				if (!fileAllowed || !file.exists()) {
					log.warn("No this file: " + getInputPath());
					addActionError(MgrUtil
							.getUserMessage("action.error.cannot.find.file"));
					return null;
				}
				return "download";
			} else if ("createPlanningData".equals(operation)) {
				jsonObject = new JSONObject();
				if (null != mapContainerNode.getId()) {
					final String time_suffix = "" + new Date().getTime();
					String fileName = mapContainerNode.getMapName() + "_"
							+ time_suffix + ".xml";
					String destFilePath = getFolderDataFileRealPath(fileName);
					TopoXMLConvertor convertor = new TopoXMLConvertor(true);
					MapContainerNode container = (MapContainerNode) QueryUtil
							.findBoById(MapNode.class,
									mapContainerNode.getId(),
									convertor.getLazyBoLoader());
					BooleanMsgPair result = convertor.convert2XML(container,
							destFilePath);

					String name = makeXMLBgImages2Tar(
							mapContainerNode.getMapName(), time_suffix,
							destFilePath, convertor.getBackgroundImageNames());
					if (null != name) {
						fileName = name;
					}

					jsonObject.put("succ", result.getValue());
					jsonObject.put("msg", result.getDesc());
					jsonObject.put("fileName", fileName);
				}
				return "json";
			} else if ("uploadPlanningData".equals(operation)) {
				// TODO limit the size of file
				jsonObject = new JSONObject();
				if (StringUtils.isBlank(uploadXmlFileName) || null == uploadXml) {
					jsonObject
							.put("msg",
									MgrUtil.getUserMessage("error.topo.import.xml.invalid.param"));
				} else {
					if (uploadXmlFileName.endsWith(".xml")) {
						// normal
						parseDataFromXML(uploadXml);
					} else {
						// tarball
						final String time_suffix = "" + new Date().getTime();
						final String destPath = getTarFolderPath() + "/"
								+ time_suffix;
						if (new TarArchive().extract(
								uploadXml.getAbsolutePath(), destPath)) {
							File folder = new File(destPath);
							File[] files = folder.listFiles();

							File[] imageFiles = null;
							boolean parseSucc = false;

							for (File file : files) {
								if (file.isFile()) {
									parseSucc = parseDataFromXML(file);
								} else {
									// copy map background images
									imageFiles = file.listFiles();
								}
							}
							if (parseSucc && null != imageFiles) {
								String imagepath = BeTopoModuleUtil
										.getRealTopoBgImagePath(getDomain()
												.getDomainName());
								if (overrideBg) {
									for (File imageFile : imageFiles) {
										FileUtils.copyFileToDirectory(
												imageFile, new File(imagepath));
									}
								} else {
									for (File imageFile : imageFiles) {
										if (!new File(imagepath,
												imageFile.getName()).exists()) {
											FileUtils.copyFileToDirectory(
													imageFile, new File(
															imagepath));
										}
									}

								}
							}
						} else {
							jsonObject.put("msg", "Unable to extra the file: " + uploadXmlFileName);
						}
					}

				}
				return "json";
			} else if ("viewFloor".equals(operation)) {
				log.info_ln("View floor: " + id);
				setSelectedMapId(id);
				return mapPage(start);
			} else {
				return mapPage(start);
			}
		} catch (Exception e) {
			log.error("execute", "execute error. operation:" + operation, e);
			addActionError(MgrUtil.getUserMessage(e));
			return mapPage(start);
		}
	}

	private String mapPage(Date start) throws Exception {
		if ("viewFullScreen".equals(operation)) {
			fullScreenMode = true;
		} else if ("viewNoFullScreen".equals(operation)) {
			fullScreenMode = false;
		} else {
			Boolean fullScreenSession = (Boolean) MgrUtil
					.getSessionAttribute(SessionKeys.FULL_SCREEN_MODE);
			fullScreenMode = fullScreenSession != null && fullScreenSession;
		}
		MgrUtil.setSessionAttribute(SessionKeys.FULL_SCREEN_MODE,
				fullScreenMode);
		if (getShowDomain()) {
			log.info_ln("maps.action main: "
					+ (new Date().getTime() - start.getTime()) + " ms.");
			return SUCCESS;
		} else {
			Long worldMapId = BoMgmt.getMapMgmt().getWorldMapId(domainId);
			if (null != worldMapId) {
				MapContainerNode vhmRoot = QueryUtil.findBoById(
						MapContainerNode.class, worldMapId);
				if (MapMgmt.VHM_ROOT_MAP_NAME.equals(vhmRoot.getMapName())) {
					id = worldMapId;
					initializePage = true;
					return "initPage";
				}
			}
			log.info_ln("maps.action main: "
					+ (new Date().getTime() - start.getTime()) + " ms.");
			return SUCCESS;
		}
	}

	public List<TextItem> getMapIcons() {
		List<TextItem> mapIcons = new ArrayList<TextItem>();
		List<String[]> icons = BeTopoModuleUtil.getMapIcons();
		if (null != icons) {
			for (String[] icon : icons) {
				TextItem item = new TextItem(icon[0], icon[1]);
				mapIcons.add(item);
			}
		}
		return mapIcons;
	}

	public List<String> getMapImages() {
		HmDomain domain = (userContext.getSwitchDomain() != null) ? userContext
				.getSwitchDomain() : userContext.getDomain();
		return BeTopoModuleUtil.getBackgroundImages(domain.getDomainName());
	}

	public List<TextItem> getMapReviewImages() {
		List<TextItem> list = new ArrayList<TextItem>();
		List<String> images = BeTopoModuleUtil.getBackgroundImages(getDomain()
				.getDomainName());
		if (null != images && !images.isEmpty()) {
			for (String image : images) {
				TextItem item = new TextItem(
						MapsAction.getBackgroundPathByDomain(request,
								getDomain().getDomainName()) + image, image);
				list.add(item);
			}
		}
		return list;
	}

	public String getLocalFileName() {
		return StringUtils.isBlank(fileName) ? NmsUtil.getOEMCustomer()
				.getCompanyNameWithoutBlank() + "PlanningReport.pdf" : fileName;
	}

	public long getImageMaxSize() {
		return BeTopoModuleUtil.IMAGE_MAX_SIZE;
	}

	public String getWebAppHttpUrl() {
		return NmsUtil.getWebAppHttpUrl(request);
	}

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(getInputPath());
	}

	private String getInputPath() {
		return StringUtils.isBlank(fileName) ? "/tmp/report_"
				+ getDomain().getDomainName() + ".pdf"
				: getFolderDataFileRealPath(fileName);
	}

	public boolean getTopoSearch() {
		return true;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MAP_VIEW);
	}

	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}
		MapContainerNode mapContainerNode = (MapContainerNode) bo;
		if ("links".equals(operation)) {
			// Just to trigger load from database
			mapContainerNode.getChildLinks().values();
		}
		if ("nodes".equals(operation) || "addSimAp".equals(operation)
				|| "updateSimApLaps".equals(operation)
				|| "predictedLap".equals(operation)
				|| "autoSimAps".equals(operation)
				|| "updateChannelWidth".equals(operation)
				|| "planHeatMap".equals(operation)) {
			Set<MapNode> childNodes = null;
			try {
				MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
				childNodes = mapAlarmsCache.getMapNodes(id, pageId);
			} catch (Exception e) {
				log.error("load", "Cache issue, just load from DB.");
			}
			BoMgmt.getPlannedApMgmt().loadPlannedAPs(mapContainerNode,
					childNodes);
		}
		if ("nodes".equals(operation) || "updateSimApLaps".equals(operation)
				|| "predictedLap".equals(operation) || true) {
			// Just to trigger load from database
			mapContainerNode.getPerimeter().size();
			mapContainerNode.getWalls().size();
		}
		// Just to trigger load from database
		mapContainerNode.getChildNodes().size();
		if (mapContainerNode.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
			if ("nodes".equals(operation) || "cacheSpill".equals(operation)
					|| "updateSpillCache".equals(operation)
					|| "updateSimApLaps".equals(operation)
					|| "updateChannelWidth".equals(operation)
					|| "addSimAp".equals(operation)
					|| "autoSimAps".equals(operation)) {
				/*
				 * For updateSimApLaps this is only required if bssid.length() >
				 * 0, i.e. if a planned AP is either updated or moved, but this
				 * will improve the db caching.
				 */
				MapContainerNode building = mapContainerNode.getParentMap();
				for (MapNode mapNode : building.getChildNodes()) {
					if (mapNode.isLeafNode()) {
						continue;
					}
					MapContainerNode floor = (MapContainerNode) mapNode;
					floor.getChildNodes().size();
					if ("cacheSpill".equals(operation)
							|| "updateSpillCache".equals(operation)) {
						// Really only need current floor and 1 floor above
						// (walls for floor below not needed
						floor.getPerimeter().size();
						floor.getWalls().size();
					}
					BoMgmt.getPlannedApMgmt().loadPlannedAPs(floor,
							floor.getChildNodes());
					if (floor.getId().equals(mapContainerNode.getId())) {
						mapContainerNode.setPlannedAPs(floor.getPlannedAPs());
					}
				}
			}
		}
		while (mapContainerNode.getParentMap() != null) {
			mapContainerNode = mapContainerNode.getParentMap();
			mapContainerNode.getId();
		}
		return null;
	}

	public int getDomainApCount() {
		return CacheMgmt.getInstance().getApCount(domainId);
	}

	private static List<CheckItem> snrThresholdValues;

	static {
		snrThresholdValues = new ArrayList<CheckItem>();
		for (int snr = 5; snr <= 50; snr++) {
			CheckItem item = new CheckItem((long) snr, "" + snr);
			snrThresholdValues.add(item);
		}
	}

	public static List<CheckItem> getSnrThresholdValues() {
		return snrThresholdValues;
	}

	private static List<CheckItem> rssiThresholdValues;

	static {
		rssiThresholdValues = new ArrayList<CheckItem>();
		for (int rssi = 40; rssi <= 90; rssi++) {
			CheckItem item = new CheckItem((long) rssi, "-" + rssi);
			rssiThresholdValues.add(item);
		}
	}

	public static List<CheckItem> getRssiThresholdValues() {
		return rssiThresholdValues;
	}

	private static List<CheckItem> ht20rateThresholdValues,
			ht40rateThresholdValues, ht80rateThresholdValues;

	static {
		ht20rateThresholdValues = setRateThresholdValues(new short[] { 1, 6,
				12, 18, 24, 36, 52, 78, 104, 117, 130 });
		ht40rateThresholdValues = setRateThresholdValues(new short[] { 1, 6,
				24, 54, 81, 108, 130, 162, 216, 247, 270 });
		ht80rateThresholdValues = setRateThresholdValues(new short[] { 1, 6,
				54, 130, 260, 390, 520, 650, 780, 910, 1040, 1170 });
	}

	private static List<CheckItem> setRateThresholdValues(
			short rate_thresholds[]) {
		List<CheckItem> rateThresholdValues = new ArrayList<CheckItem>();
		for (short rate_threshold : rate_thresholds) {
			CheckItem item = new CheckItem((long) rate_threshold, ""
					+ rate_threshold);
			rateThresholdValues.add(item);
		}
		return rateThresholdValues;
	}

	public List<CheckItem> getRateThresholdHT20Values() {
		return ht20rateThresholdValues;
	}

	public List<CheckItem> getRateThresholdHT40Values() {
		return ht40rateThresholdValues;
	}

	public List<CheckItem> getRateThresholdHT80Values() {
		return ht80rateThresholdValues;
	}

	public int getRateThresholdHT20() {
		return 78;
	}

	public int getRateThresholdHT40() {
		return 162;
	}

	public int getRateThresholdHT80() {
		return 390;
	}

	public void setRateThreshold(int rateThreshold) {
		this.rateThreshold = rateThreshold;
	}

	private static List<CheckItem> opacityValues;

	static {
		opacityValues = new ArrayList<CheckItem>();
		for (int ol = 10; ol <= 100; ol += 10) {
			CheckItem item = new CheckItem((long) ol, ol + "");
			opacityValues.add(item);
		}
	}

	public static List<CheckItem> getOpacityValues() {
		return opacityValues;
	}

	public boolean isHosted() {
		return NmsUtil.isHostedHMApplication();
	}

	public boolean isGme() {
		return BeTopoModuleUtil.getMapGlobalSetting(domainId).isUseStreetMaps();
	}

	public String getGmeKey() {
		String licenseKey = NmsUtil.getGmLicenseKey();
		if (NmsUtil.isPlanner() || NmsUtil.isDemoHHM()) {
			// Usage is free for these servers
			String apiKey = NmsUtil.getGmAPIKey();
			if (apiKey != null && apiKey.length() > 0) {
				return "&key=" + apiKey;
			} else {
				log.info_ln("GM API key missing.");
				return "";
			}
		} else if (NmsUtil.isHostedHMApplication()) {
			// HMOL customers
			if (licenseKey != null && licenseKey.length() > 0) {
				String vhmId = getDomain().getVhmID();
				if (vhmId == null || vhmId.length() == 0) {
					vhmId = "no-vhm-id";
				}
				return "&client=" + licenseKey + "&channel=" + vhmId;
			} else {
				log.info_ln("GM License key missing.");
				return "";
			}
		} else {
			// on-premise customers
			if (licenseKey != null && licenseKey.length() > 0) {
				String systemId = BeLicenseModule.HIVEMANAGER_SYSTEM_ID;
				if (systemId == null || systemId.length() == 0) {
					systemId = "no-system-id";
				}
				return "&client=" + licenseKey + "&channel=" + systemId;
			} else {
				log.info_ln("GM License key missing.");
				return "";
			}
		}
	}

	public String getOnPremiseID() {
		if (NmsUtil.isHostedHMApplication()) {
			return "";
		} else {
			String systemId = BeLicenseModule.HIVEMANAGER_SYSTEM_ID;
			if (systemId == null || systemId.length() == 0) {
				systemId = "none";
			}
			return systemId;
		}
	}

	private String selectedImage;

	private boolean initializePage = false;

	private boolean fullScreenMode = false;

	public boolean isFullScreenMode() {
		return fullScreenMode;
	}

	public String getSelectedImage() {
		return selectedImage;
	}

	public boolean getInitializePage() {
		return initializePage;
	}

	public void setSelectedImage(String selectedImage) {
		this.selectedImage = selectedImage;
	}

	private Collection<JSONObject> preparePerimeter(List<Vertex> perimeter,
			double scale) throws Exception {
		Collection<JSONObject> jsonPerim = new Vector<JSONObject>();
		Collection<JSONObject> jsonNodes = null;
		int perimId = -1;
		short perimTypeIndex = 0;
		for (Vertex vertex : perimeter) {
			if (vertex.getId() != perimId) {
				if (jsonNodes != null) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("prm", jsonNodes);
					jsonObj.put("id", perimId);
					jsonObj.put("tp", perimTypeIndex);
					jsonPerim.add(jsonObj);
					for (int i = 0; i < vertex.getId() - perimId - 1; i++) {
						jsonObj = new JSONObject();
						jsonObj.put("prm", new Vector<JSONObject>());
						jsonPerim.add(jsonObj);
					}
				}
				jsonNodes = new Vector<JSONObject>();
				perimId = vertex.getId();
				perimTypeIndex = Wall.getWallIndex(perimTypeWall(vertex
						.getType()) ? vertex.getType() : 1003);
			}
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("x1", scale(vertex.getX(), scale));
			jsonObj.put("y1", scale(vertex.getY(), scale));
			jsonNodes.add(jsonObj);
		}
		if (jsonNodes != null) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("prm", jsonNodes);
			jsonObj.put("id", perimId);
			jsonObj.put("tp", perimTypeIndex);
			jsonPerim.add(jsonObj);
		}
		return jsonPerim;
	}

	private boolean perimTypeWall(short type) {
		return type == 1003 || type == 1004 || type == 1005;
	}

	private Collection<JSONObject> prepareWalls(List<Wall> walls, double scale)
			throws Exception {
		int from = -1;
		Wall lastWall = null;
		int[] close = new int[walls.size()];
		for (int i = 0; i < walls.size(); i++) {
			Wall wall = walls.get(i);
			close[i] = -1;
			if (lastWall != null) {
				if (lastWall.getX2() == wall.getX1()
						&& lastWall.getY2() == wall.getY1()) {
					if (from < 0) {
						from = i - 1;
					} else if (wall.getX2() == walls.get(from).getX1()
							&& wall.getY2() == walls.get(from).getY1()) {
						close[i] = from;
						close[from] = i;
						from = -1;
					}
				} else {
					from = -1;
				}
			}
			if (close[i] < 0) {
				lastWall = wall;
			} else {
				lastWall = null;
			}
		}
		Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
		for (int i = 0; i < walls.size(); i++) {
			Wall wall = walls.get(i);
			log.debug("Wall close[" + close[i] + "] (" + wall.getX1() + ", "
					+ wall.getY1() + ")  (" + wall.getX2() + ", "
					+ wall.getY2() + ")");
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("x1", scale(wall.getX1(), scale));
			jsonObj.put("y1", scale(wall.getY1(), scale));
			jsonObj.put("x2", scale(wall.getX2(), scale));
			jsonObj.put("y2", scale(wall.getY2(), scale));
			jsonObj.put("tp", Wall.getWallIndex(wall.getType()));
			if (close[i] != -1) {
				if (close[i] < i) {
					// From last wall to first in the loop
					jsonObj.put("close", close[i]);
				} else {
					// At starting wall of the loop
					jsonObj.put("closed", true);
				}
			}
			jsonNodes.add(jsonObj);
		}
		return jsonNodes;
	}

	private static Map<Short, Short> createChIndexMap(List<Short> chs,
			List<Short> chis) {
		Map<Short, Short> chIndexMap = new HashMap<Short, Short>();
		if (chs != null && chis != null && chs.size() == chis.size()) {
			for (int i = 0; i < chs.size(); i++) {
				short ch = (short) Math.abs(chs.get(i));
				short chi = chis.get(i);
				chIndexMap.put(ch, chi);
			}
		}
		return chIndexMap;
	}

	private Collection<JSONObject> preparePlannedAPs(
			MapContainerNode mapContainerNode, boolean radioOnly, double scale,
			Long exclude, Map<Short, Short> ch1IndexMap,
			Map<Short, Short> ch2IndexMap) throws Exception {
		Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
		for (PlannedAP plannedAP : mapContainerNode.getPlannedAPs()) {
			JSONObject jo;
			if (plannedAP.getId().equals(exclude)) {
				jo = jsonObject;
			} else {
				jo = new JSONObject();
				prepareSimulatedAP(mapContainerNode, plannedAP, radioOnly, jo);
				jsonNodes.add(jo);
			}
			short wifi0Channel = plannedAP.wifi0Channel;
			if (wifi0Channel == 0) {
				wifi0Channel = plannedAP.autoWifi0Channel;
			}
			Short ch1i = ch1IndexMap.get(wifi0Channel);
			if (ch1i == null) {
				ch1i = 0; // Must be disabled interface
			}
			short wifi1Channel = plannedAP.wifi1Channel;
			if (wifi1Channel == 0) {
				wifi1Channel = plannedAP.autoWifi1Channel;
			}
			Short ch2i = ch2IndexMap.get(wifi1Channel);
			if (ch2i == null) {
				ch2i = 0; // Must be disabled interface
			}
			jo.put("ch1i", ch1i);
			jo.put("ch2i", ch2i);
		}
		return jsonNodes;
	}

	public static JSONObject prepareNodes(Set<MapNode> nodes, Long pageId,
			double scale) throws Exception {
		JSONObject jsonObject = new JSONObject();
		Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
		boolean hasPageId = false;

		Set<MapNode> mapNodes = new HashSet<MapNode>();
		Set<Long> leafNodeIds = new HashSet<Long>();
		for (MapNode mapNode : nodes) {
			if (mapNode.isLeafNode()) {
				MapLeafNode leafNode = (MapLeafNode) mapNode;
				mapNodes.add(leafNode);
				leafNodeIds.add(mapNode.getId());
			} else {
				mapNodes.add(mapNode);
			}
		}

		Map<Long, HiveAp> hiveApAttrs = new HashMap<Long, HiveAp>();
		if (!leafNodeIds.isEmpty()) {
			// Query for the managed status only
			String query = "select bo.id, bo.hiveAp.manageStatus, bo.hiveAp.hiveApType, "
					+ "bo.hiveAp.radiusServerProfile.id, bo.hiveAp.configTemplate.vpnService.id, "
					+ "bo.hiveAp.dhcpServerCount, bo.hiveAp.vpnMark, bo.hiveAp.simulated, "
					+ "bo.hiveAp.radiusProxyProfile.id, bo.hiveAp.ipAddress, bo.hiveAp.deviceType, "
					+ "bo.hiveAp.hiveApModel from "
					+ MapLeafNode.class.getSimpleName() + " bo";
			List<?> attributes_list = QueryUtil.executeQuery(query, null,
					new FilterParams("id", leafNodeIds));
			for (Object obj : attributes_list) {
				Object[] attributes = (Object[]) obj;
				HiveAp hiveAp = new HiveAp();
				hiveAp.setManageStatus((Short) attributes[1]);
				hiveAp.setHiveApType((Short) attributes[2]);
				Long radiusId = (Long) attributes[3];
				Long vpnId = (Long) attributes[4];
				Integer dhcpCount = (Integer) attributes[5];
				Short vpnMark = (Short) attributes[6];
				Boolean simulated = (Boolean) attributes[7];
				Long radiusProxyId = (Long) attributes[8];
				hiveAp.setIpAddress((String) attributes[9]);
				hiveAp.setDeviceType((Short) attributes[10]);
				hiveAp.setHiveApModel((Short) attributes[11]);
				if (null != radiusId) {// RADIUS Server
					RadiusOnHiveap radius = new RadiusOnHiveap();
					radius.setId(radiusId);
					hiveAp.setRadiusServerProfile(radius);
				}
				if (null != vpnId) {// VPN Server
					VpnService vpn = new VpnService();
					vpn.setId(vpnId);
					ConfigTemplate wlan = new ConfigTemplate(
							ConfigTemplateType.WIRELESS);
					wlan.setVpnService(vpn);
					hiveAp.setConfigTemplate(wlan);
					hiveAp.setVpnMark(vpnMark == null ? HiveAp.VPN_MARK_NONE
							: vpnMark);
				}
				if (null != dhcpCount) {// DHCP Server
					hiveAp.setDhcpServerCount(dhcpCount);
				}
				if (null != simulated) {
					hiveAp.setSimulated(simulated);
				}
				if (null != radiusProxyId) {// set RADIUS Proxy Profile;
					RadiusProxy radius = new RadiusProxy();
					radius.setId(radiusProxyId);
					hiveAp.setRadiusProxyProfile(radius);
				}
				hiveApAttrs.put((Long) attributes[0], hiveAp);
			}
		}

		for (MapNode mapNode : mapNodes) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("nodeId", "n" + mapNode.getId());
			jsonObj.put("x", scale(mapNode.getX(), scale));
			jsonObj.put("y", scale(mapNode.getY(), scale));
			jsonObj.put("s", mapNode.getSeverity());
			jsonObj.put("i", mapNode.getIconName());
			if (!hasPageId) {
				jsonObj.put("pageId", pageId);
				hasPageId = true;
			}
			if (mapNode.isLeafNode()) {
				MapLeafNode mapLeafNode = (MapLeafNode) mapNode;
				jsonObj.put("apId", mapLeafNode.getApId());
				jsonObj.put("apName", mapLeafNode.getApName());
				String ethId = ((MapLeafNode) mapNode).getEthId();
				if (null != ethId && !("".equals(ethId.trim()))) {
					jsonObj.put("ethId", ethId);
				}
				if (mapLeafNode.isFetchLinksTimeout()
						&& mapLeafNode.getApId().charAt(0) != 'M') {
					jsonObj.put("isCritical", true);
				}

				HiveAp hiveAp = hiveApAttrs.get(mapNode.getId());
				if (null != hiveAp) {
					jsonObj.put("ipAddress", hiveAp.getIpAddress());
					// device type
					jsonObj.put("dt", hiveAp.getDeviceType());
					// device model
					jsonObj.put("dm", hiveAp.getHiveApModel());
					String nodeId = ((MapLeafNode) mapNode).getApId();
					// set the managed status
					if (hiveAp.getManageStatus() == HiveAp.STATUS_MANAGED) {
						jsonObj.put("isManaged", true);
					}
					/* AP_TYPE | RADIUS | VPN | DHCP | Proxy Server */
					boolean isPortal = hiveAp.getHiveApType() == HiveAp.HIVEAP_TYPE_PORTAL;
					boolean isRadius = null != hiveAp.getRadiusServerProfile();
					boolean isVpnServer = hiveAp.isVpnServer();
					boolean isVpnClient = hiveAp.isVpnClient();
					boolean isDhcp = 0 < hiveAp.getDhcpServerCount();
					boolean isProxyServer = null != hiveAp
							.getRadiusProxyProfile();

					Long vpnId = null;
					if (null != hiveAp.getConfigTemplate()
							&& null != hiveAp.getConfigTemplate()
									.getVpnService()) {
						vpnId = hiveAp.getConfigTemplate().getVpnService()
								.getId();
					}
					VpnStatus vpnStatus = isVpnServer ? AhVPNStatus
							.isVpnServerUp(nodeId) : AhVPNStatus.isVpnClientUp(
							nodeId, vpnId);
					// set ap type
					String a = isPortal ? "1" : "0";
					String b = isRadius ? "1" : "0";
					String c = isVpnServer ? (VpnStatus.Up.equals(vpnStatus) ? "3"
							: "1")
							: (isVpnClient ? (VpnStatus.Up.equals(vpnStatus) ? "4"
									: (VpnStatus.Half.equals(vpnStatus) ? "5"
											: "2"))
									: "0");
					String d = isDhcp ? "1" : "0";
					String e = isProxyServer ? "1" : "0";
					jsonObj.put("apType", a + "|" + b + "|" + c + "|" + d + "|"
							+ e);
					
					// check whether is SR only
					jsonObject.put("noRadio", jsonObject.optBoolean("noRadio", true) && hiveAp.isSwitchProduct());
				}

			} else {
				jsonObj.put("container", true);
				jsonObj.put("ctp", ((MapContainerNode) mapNode).getMapType());
				jsonObj.put("mapName",
				        StringEscapeUtils.escapeHtml4(((MapContainerNode) mapNode).getMapName()));
				Float latitude = mapNode.getLatitude();
				Float longitude = mapNode.getLongitude();
				if (latitude == null || longitude == null) {
					latitude = mapNode.getCenterLatitude();
					longitude = mapNode.getCenterLongitude();
				}
				jsonObj.put("lat", latitude == null ? "" : latitude);
				jsonObj.put("lng", longitude == null ? "" : longitude);
				boolean na = longitude == null || longitude == null;
				jsonObj.put("na", na);
				if (na) {
					jsonObj.put("address", mapNode.getAddress() == null ? ""
							: mapNode.getAddress());
				}
			}
			jsonNodes.add(jsonObj);
		}
		jsonObject.put("ntp", "nodes");
		jsonObject.put("nodes", jsonNodes);
		return jsonObject;
	}

	public static Collection<JSONObject> prepareNodeChannels(
			Set<MapNode> nodes, Long pageId, boolean useA) throws Exception {
		Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
		boolean hasPageId = false;
		//get wifi0 and wifi1 AhLatestXif data
		Map<String, List<AhLatestXif>> wifiRadioMap =MapNodeUtil.getWifiRadioMap(nodes);
		
		for (MapNode mapNode : nodes) {
			if (mapNode.isLeafNode()) {
				MapLeafNode mapLeafNode = (MapLeafNode) mapNode;
				if(null != mapLeafNode.getHiveAp() && mapLeafNode.getHiveAp().isSwitchProduct()) {
				    // disable power-channel for SR
				    continue;
				}
				short channel = useA ? mapLeafNode.getRadioChannelA()
						: mapLeafNode.getRadioChannelBG();
				boolean autoChannel = useA ? mapLeafNode.isAutoChannelA()
						: mapLeafNode.isAutoChannelBG();
				short power = useA ? mapLeafNode.getRadioTxPowerA()
						: mapLeafNode.getRadioTxPowerBG();
				boolean autoPower = useA ? mapLeafNode.isAutoTxPowerA()
						: mapLeafNode.isAutoTxPowerBG();
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("nodeId", "n" + mapNode.getId());
				jsonObj.put("seen", mapNode.isSelected());
				if (!hasPageId) {
					jsonObj.put("pageId", pageId);
					jsonObj.put("ntp", "nodeLabels");
					hasPageId = true;
				}
				jsonObj.put("apId", mapLeafNode.getApId());
				//get ifMode for wifi interface
				List<AhLatestXif> xifList=wifiRadioMap.get(mapLeafNode.getApId());
				byte ifMode=0;
				if(null!=xifList){
					for(AhLatestXif xif:xifList){
						String ifName=xif.getIfName().toLowerCase().trim();
						if(useA){
							if("wifi1".equals(ifName)){
								ifMode=xif.getIfMode();
								break;
							}
						}else{
							if("wifi0".equals(ifName)){
								ifMode=xif.getIfMode();
								break;
							}
						}
					}
				}
				if (channel == 0 || power == 0 || ifMode==AhXIf.IFMODE_SENSOR) {
					HiveAp hiveAp=mapLeafNode.getHiveAp();
					jsonObj.put("apName", null==hiveAp?"":hiveAp.getHostName());
				} else {
					String ch = channel + (autoChannel ? "" : "*");
					String pwr = power + (autoPower ? "" : "*");
					String apName = "Ch " + ch + " - Pwr " + pwr;
					jsonObj.put("apName", apName);
				}
				int activeClients = 0;
				SimpleHiveAp cachedAp = CacheMgmt.getInstance()
						.getSimpleHiveAp(mapLeafNode.getApId());
				if (cachedAp != null) {
					activeClients = cachedAp.getActiveClientCount();
				}
				String ac = "No clients";
				if (activeClients == 1) {
					ac = "1 client";
				} else if (activeClients > 1) {
					ac = activeClients + " clients";
				}
				jsonObj.put("ac", ac);
				jsonNodes.add(jsonObj);
			}
		}
		return jsonNodes;
	}

	protected void prepareAlarms(Long mapId) throws Exception {
		MapAlarmsCache<?> mapAlarmsCache = getMapAlarmsCache();
		jsonObject = mapAlarmsCache.getNewAlarms(mapId, pageId, scale,
				rogueChecked, clientChecked, summaryChecked);
		log.debug("prepareAlarms", "Prepared alarms: " + jsonObject.toString());
	}

	public static JSONObject prepareLinks(Collection<MapLink> links,
			Long pageId, boolean showRssi) throws Exception {
		JSONObject jsonObject = new JSONObject();
		Collection<JSONObject> jsonLinks = new Vector<JSONObject>();
		boolean hasPageId = false;
		for (MapLink mapLink : links) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("fromId", "n" + mapLink.getFromNode().getId());
			jsonObj.put("toId", "n" + mapLink.getToNode().getId());
			if (mapLink.getFromRssi() < 0 && mapLink.getFromRssi() > -95) {
				jsonObj.put("fromLbl", mapLink.getFromRssi());
			}
			if (mapLink.getToRssi() < 0 && mapLink.getToRssi() > -95) {
				jsonObj.put("toLbl", mapLink.getToRssi());
			}
			if (!hasPageId) {
				jsonObj.put("pageId", pageId);
				hasPageId = true;
			}
			// indicate the link is critical status.
			MapLeafNode fromNode = mapLink.getFromNode();
			MapLeafNode toNode = mapLink.getToNode();
			if ((fromNode.getApId().charAt(0) != 'M' && !fromNode
					.isFetchLinksTimeout())
					|| (toNode.getApId().charAt(0) != 'M' && !toNode
							.isFetchLinksTimeout())) {
				// it's normal, ignore it
			} else {
				jsonObj.put("isCritical", true);
			}
			jsonLinks.add(jsonObj);
		}
		jsonObject.put("mesh", showRssi);
		jsonObject.put("ntp", "links");
		jsonObject.put("links", jsonLinks);
		return jsonObject;
	}

	public static JSONObject prepareClients(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Long pageId, double scale) throws Exception {
		JSONObject jsonObject = new JSONObject();
		Collection<JSONObject> jsonClients = BoMgmt.getLocationTracking()
				.locateClients(mapContainerNode, nodes, pageId,
						mapContainerNode.getWidth(), null, scale);
		jsonObject.put("ntp", "clients");
		jsonObject.put("clients", jsonClients);
		return jsonObject;
	}

	public static JSONObject prepareRogues(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Long pageId, double scale) throws Exception {
		JSONObject jsonObject = new JSONObject();
		Collection<JSONObject> jsonRogues = BoMgmt.getLocationTracking()
				.locateRogues(mapContainerNode, nodes, pageId,
						mapContainerNode.getWidth(), null, scale);
		jsonObject.put("ntp", "rogues");
		jsonObject.put("rogues", jsonRogues);
		return jsonObject;
	}

	protected void prepareMapNode(MapContainerNode mapNode) throws Exception {
		jsonObject = new JSONObject();
		jsonObject.put(
				"path",
				getBackgroundPathByDomain(request, mapNode.getOwner()
						.getDomainName()));
		jsonObject.put("commPath", request.getContextPath());
		jsonObject.put("bg", mapNode.getBackground());
		jsonObject.put("width", mapNode.getWidth());
		jsonObject.put("height", mapNode.getHeight());
		if (mapNode.getActualWidth() == 0) {
			jsonObject.put("gridSize", 0);
			jsonObject.put("address", mapNode.getAddress() == null ? ""
					: mapNode.getAddress());
			Float latitude = mapNode.getCenterLatitude();
			Float longitude = mapNode.getCenterLongitude();
			int zoom = mapNode.getCenterZoom();
			if (latitude == null || longitude == null) {
				latitude = mapNode.getLatitude();
				longitude = mapNode.getLongitude();
				if (latitude != null && longitude != null) {
					zoom = mapNode.getParentMap().getCenterZoom() + 2;
				}
			}
			jsonObject.put("ll", latitude != null);
			jsonObject.put("lat", latitude == null ? "" : latitude);
			jsonObject.put("lng", longitude == null ? "" : longitude);
			jsonObject.put("zm", zoom);
			jsonObject.put("vt",
					mapNode.getViewType() == null ? "" : mapNode.getViewType());
			jsonObject.put("pzm", mapNode.getParentMap().getCenterZoom());
		} else {
			double actualGridSize = getGridSize(mapNode.getActualWidth());
			jsonObject.put("lengthUnit", mapNode.getLengthUnit());
			jsonObject.put("actualHeight", mapNode.getActualHeight());
			jsonObject.put("actualGridSize", actualGridSize);
			jsonObject.put("gridSize", actualGridSize * mapNode.getWidth()
					/ mapNode.getActualWidth());
		}
		// Both feature and instance permission are required
		jsonObject.put("writePermission", getMapWritePermission(mapNode));
		jsonObject.put("leafMapContainer", isLeafNodeMap(mapNode));
	}

	private boolean getMapWritePermission(MapContainerNode mapNode) {
		if (getShowDomain()) {
			if (!HmDomain.HOME_DOMAIN
					.equals(mapNode.getOwner().getDomainName())) {
				return false;
			}
		}
		return getWritePermission() && getWriteInstancePermission();
	}

	private boolean isLeafNodeMap(MapContainerNode mapNode) {
		boolean isLeafNode = true;// no other map container under it
		for (MapNode node : mapNode.getChildNodes()) {
			if (!node.isLeafNode()) {
				isLeafNode = false;
				break;
			}
		}
		return isLeafNode;
	}

	public static String getBackgroundPathByDomain(HttpServletRequest request,
			String domainName) {
		return request.getContextPath() + "/domains/" + domainName + "/maps/";
	}

	private void addSimulatedAP(MapContainerNode mapContainerNode,
			PlanToolConfig planToolConfig, long locateX) throws Exception {
		boolean updateContainer = false;
		if (mapContainerNode.getEnvironment() == EnumConstUtil.MAP_ENV_AUTO) {
			mapContainerNode.setEnvironment(EnumConstUtil.MAP_ENV_ENTERPRISE);
			updateContainer = true;
		}
		PlannedAP plannedAP = new PlannedAP();
		plannedAP.apModel = hwModel;
		plannedAP.countryCode = planToolConfig.getCountryCode();
		plannedAP.wifi0Channel = (short) planToolConfig.getWifi0Channel();
		plannedAP.wifi1Channel = (short) planToolConfig.getWifi1Channel();
		plannedAP.wifi0Power = (short) planToolConfig.getWifi0Power();
		plannedAP.wifi1Power = (short) planToolConfig.getWifi1Power();
		plannedAP.wifi0Enabled = planToolConfig.isWifi0Enabled();
		plannedAP.wifi1Enabled = planToolConfig.isWifi1Enabled();

		if (!BoMgmt.getLocationTracking().findCandidateCoverage(
				mapContainerNode, planToolConfig, canvasWidth, canvasHeight,
				(frequency & 1) > 0, getShadesPerColor(), plannedAP)) {
			plannedAP.x = reverseScale(locateX);
			plannedAP.y = reverseScale(15);
		}
		try {
			plannedAP = BoMgmt.getPlannedApMgmt().createPlannedAP(
					mapContainerNode, plannedAP, getUserContext(),
					getSelectedL2FeatureKey());
		} catch (HmException e) {
			jsonObject = new JSONObject();
			jsonObject.put("error", e.getMessage());
			return;
		}
		mapContainerNode.getPlannedAPs().add(plannedAP);
		FloorCache cache = addToFloorCache(mapContainerNode, plannedAP);
		clearNbrAps(mapContainerNode, null, plannedAP.getId(), cache);
		if (cache != null && cache.repairs != null) {
			cache.repairs.add(plannedAP.getId());
		}
		Map<Short, Short> ch1IndexMap = createChIndexMap(ch1s, ch1is);
		Map<Short, Short> ch2IndexMap = createChIndexMap(ch2s, ch2is);
		List<MapContainerNode> floors = BoMgmt.getLocationTracking()
				.assignChannels(mapContainerNode, planToolConfig, ch1IndexMap,
						ch2IndexMap);
		saveChannelAssignments(mapContainerNode, floors, cache, ch1IndexMap,
				ch2IndexMap, true);
		boolean useA = (frequency & 1) > 0;
		Collection<JSONObject> jsonNodes = getPredictedLap(mapContainerNode,
				useA, plannedAP, (short) 0, (short) -1,
				planToolConfig.getFadeMargin(), false);
		jsonObject = jsonNodes.iterator().next();
		prepareSimulatedAP(mapContainerNode, plannedAP, false, jsonObject);
		jsonObject.put(
				"planned",
				preparePlannedAPs(mapContainerNode, true, scale,
						plannedAP.getId(), ch1IndexMap, ch2IndexMap));
		if (updateContainer) {
			mapContainerNode = QueryUtil.updateBo(mapContainerNode);
		}
	}

	private void prepareSimulatedAP(MapContainerNode mapContainerNode,
			PlannedAP plannedAP, boolean radioOnly, JSONObject jo)
			throws Exception {
		jo.put("nodeId", "s" + plannedAP.getId());
		int ch1;
		if (plannedAP.wifi0Channel == 0) {
			if (plannedAP.wifi0Enabled) {
				ch1 = -plannedAP.autoWifi0Channel;
			} else {
				ch1 = plannedAP.autoWifi0Channel; // disabled, this is -1
			}
		} else {
			ch1 = plannedAP.wifi0Channel;
		}
		jo.put("ch1", ch1);
		int ch2;
		if (plannedAP.wifi1Channel == 0) {
			if (plannedAP.wifi1Enabled) {
				ch2 = -plannedAP.autoWifi1Channel;
			} else {
				ch2 = plannedAP.autoWifi1Channel; // disabled, this is -1
			}
		} else {
			ch2 = plannedAP.wifi1Channel;
		}
		jo.put("ch2", ch2);
		jo.put("hostName", plannedAP.getLabel());
		jo.put("apName",
				MgrUtil.getEnumString("enum.hiveAp.model." + plannedAP.apModel));
		if (radioOnly) {
			return;
		}
		jo.put("md", plannedAP.apModel);
		jo.put("x", scale(plannedAP.x, scale));
		jo.put("y", scale(plannedAP.y, scale));
		jo.put("s", AhAlarm.AH_SEVERITY_UNDETERMINED);
		jo.put("pageId", pageId);
		jo.put("apId", "00");
		jo.put("pwr1", plannedAP.wifi0Power);
		jo.put("pwr2", plannedAP.wifi1Power);
		jo.put("w0Ebd", plannedAP.wifi0Enabled);
		jo.put("w1Ebd", plannedAP.wifi1Enabled);
		jo.put("apType", "");
		jo.put("sim", true);
	}

	private void createAutoSimAps(MapContainerNode mapContainerNode,
			PlanToolConfig planToolConfig, List<PlannedAP> aps)
			throws Exception {
		try {
			for (PlannedAP ap : aps) {
				if (ap.isSelected()) {
					continue;
				}
				ap = BoMgmt.getPlannedApMgmt().createPlannedAP(
						mapContainerNode, ap, getUserContext(),
						getSelectedL2FeatureKey());
				mapContainerNode.getPlannedAPs().add(ap);
			}
		} catch (HmException e) {
			jsonObject = new JSONObject();
			jsonObject.put("error", e.getMessage());
			return;
		}
		jsonObject = new JSONObject();
		Map<Short, Short> ch1IndexMap = createChIndexMap(null, null);
		Map<Short, Short> ch2IndexMap = createChIndexMap(null, null);
		List<MapContainerNode> floors = BoMgmt.getLocationTracking()
				.assignChannels(mapContainerNode, planToolConfig, ch1IndexMap,
						ch2IndexMap);
		saveChannelAssignments(mapContainerNode, floors, getFloorCache(id),
				ch1IndexMap, ch2IndexMap, false);
		jsonObject.put("pageId", pageId);
		jsonObject.put(
				"planned",
				preparePlannedAPs(mapContainerNode, false, scale, null,
						ch1IndexMap, ch2IndexMap));
	}

	protected void saveNode(long nodeId, long x, long y) throws Exception {
		MapNode mapNode = findBoById(MapNode.class, nodeId);
		if (mapNode == null) {
			return;
		}
		mapNode.setX(reverseScale(x));
		mapNode.setY(reverseScale(y));
		BoMgmt.updateBo(mapNode, getUserContext(), L2_FEATURE_MAP_VIEW);
		// Notify MapAlarmsCache<?> objects
		BoObserver.notifyListeners(new BoEvent<MapNode>(mapNode,
				BoEventType.UPDATED));
	}

	protected void disableBrowserCaching() throws Exception {
		// Forces caches to obtain a new copy of the page from the server
		response.setHeader("Cache-Control", "no-cache");
		// Directs caches not to store the page under any circumstance
		response.setHeader("Cache-Control", "no-store");
		// Causes the proxy cache to see the page as "stale"
		response.setDateHeader("Expires", 0);
		// HTTP 1.0 backward compatibility
		response.setHeader("Pragma", "no-cache");
	}

	public EnumItem[] getEnumInterfaceType() {
		return EnumConstUtil.ENUM_INTERFACE_TYPE;
	}

	public EnumItem[] getEnumMapEnv() {
		if (isPlanningOnly()) {
			return EnumConstUtil.ENUM_MAP_ENV_PLANNING;
		} else {
			return EnumConstUtil.ENUM_MAP_ENV;
		}
	}

	private final int mapEnv = EnumConstUtil.MAP_ENV_ENTERPRISE;

	public int getMapEnv() {
		return mapEnv;
	}

	public boolean getPopUpFlag() {
		HmDomain domain = null;
		if (null != userContext) {
			if (null != userContext.getSwitchDomain()) {
				domain = userContext.getSwitchDomain();
			} else {
				domain = userContext.getDomain();
			}
		}
		return BeTopoModuleUtil.getMapGlobalSetting(domain).isSummaryFlag();
	}

	public int getRefreshInterval() {
		HmDomain domain = null;
		if (null != userContext) {
			if (null != userContext.getSwitchDomain()) {
				domain = userContext.getSwitchDomain();
			} else {
				domain = userContext.getDomain();
			}
		}
		return BeTopoModuleUtil.getMapGlobalSetting(domain)
				.getPollingInterval();
	}

	public static boolean showRssi(HmUser userContext) {
		HmDomain domain = null;
		if (null != userContext) {
			if (null != userContext.getSwitchDomain()) {
				domain = userContext.getSwitchDomain();
			} else {
				domain = userContext.getDomain();
			}
		}
		return BeTopoModuleUtil.getMapGlobalSetting(domain)
				.isNeighborRssiFlag();
	}

	public boolean getShowRssi() {
		return showRssi(userContext);
	}

	public boolean getShowOnHover() {
		HmDomain domain = null;
		if (null != userContext) {
			if (null != userContext.getSwitchDomain()) {
				domain = userContext.getSwitchDomain();
			} else {
				domain = userContext.getDomain();
			}
		}
		return BeTopoModuleUtil.getMapGlobalSetting(domain).isOnHoverFlag();
	}

	public boolean getCalibrateHeatmap() {
		HmDomain domain = null;
		if (null != userContext) {
			if (null != userContext.getSwitchDomain()) {
				domain = userContext.getSwitchDomain();
			} else {
				domain = userContext.getDomain();
			}
		}
		return BeTopoModuleUtil.getMapGlobalSetting(domain)
				.isCalibrateHeatmap();
	}

	public boolean getUseHeatmap() {
		HmDomain domain = null;
		if (null != userContext) {
			if (null != userContext.getSwitchDomain()) {
				domain = userContext.getSwitchDomain();
			} else {
				domain = userContext.getDomain();
			}
		}
		return BeTopoModuleUtil.getMapGlobalSetting(domain).isUseHeatmap();
	}

	public boolean getPeriVal() {
		HmDomain domain = null;
		if (null != userContext) {
			if (null != userContext.getSwitchDomain()) {
				domain = userContext.getSwitchDomain();
			} else {
				domain = userContext.getDomain();
			}
		}
		return BeTopoModuleUtil.getMapGlobalSetting(domain).isPeriVal();
	}

	private Collection<JSONObject> validatePerimeter(Point2D[] walls,
			List<Vertex> perimeter, double mapToImage) throws Exception {
		Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
		boolean hasPageId = false;
		if (walls == null) {
			return jsonNodes;
		}
		if (perimeter.size() > 0 && walls.length > 1) {
			Vertex vertex = perimeter.get(0);
			Point2D first = new Point2D.Double(vertex.getX() * mapToImage,
					vertex.getY() * mapToImage);
			Point2D v1 = first;
			int perimId = vertex.getId();
			for (int i = 1; i < perimeter.size(); i++) {
				vertex = perimeter.get(i);
				Point2D v2 = new Point2D.Double(vertex.getX() * mapToImage,
						vertex.getY() * mapToImage);
				if (vertex.getId() != perimId) {
					// Consider vertex v1 -> first
					hasPageId = validatePerimeter(walls, v1, first, hasPageId,
							jsonNodes);
					first = v2;
					perimId = vertex.getId();
				} else {
					// Consider vertex v1 -> v2
					hasPageId = validatePerimeter(walls, v1, v2, hasPageId,
							jsonNodes);
				}
				v1 = v2;
			}
			// Consider vertex v1 -> first
			hasPageId = validatePerimeter(walls, v1, first, hasPageId,
					jsonNodes);
		}
		if (walls.length < 4) {
			return jsonNodes;
		}
		for (int i = 0; i < walls.length - 2; i++) {
			Point2D l1p1 = walls[i];
			Point2D l1p2 = walls[i + 1];
			for (int j = i + 2; j < walls.length; j++) {
				int l2p2i = (j + 1) % walls.length;
				if (i == l2p2i) {
					continue;
				}
				hasPageId = validatePerimeter(l1p1, l1p2, walls[j],
						walls[l2p2i], hasPageId, jsonNodes);
			}
		}
		return jsonNodes;
	}

	protected boolean validatePerimeter(Point2D[] walls, Point2D l1p1,
			Point2D l1p2, boolean hasPageId, Collection<JSONObject> jsonNodes)
			throws Exception {
		Point2D l2p1 = walls[0];
		Point2D first = l2p1;
		for (int i = 1; i < walls.length; i++) {
			Point2D l2p2 = walls[i];
			hasPageId = validatePerimeter(l1p1, l1p2, l2p1, l2p2, hasPageId,
					jsonNodes);
			l2p1 = l2p2;
		}
		return validatePerimeter(l1p1, l1p2, first, l2p1, hasPageId, jsonNodes);
	}

	protected boolean validatePerimeter(Point2D l1p1, Point2D l1p2,
			Point2D l2p1, Point2D l2p2, boolean hasPageId,
			Collection<JSONObject> jsonNodes) throws Exception {
		Point2D ip = BoMgmt.getLocationTracking().lli(l1p1, l1p2, l2p1, l2p2);
		if (ip != null) {
			JSONObject ipo = new JSONObject();
			if (!hasPageId) {
				ipo.put("pageId", pageId);
				hasPageId = true;
			}
			ipo.put("x", ip.getX());
			ipo.put("y", ip.getY());
			jsonNodes.add(ipo);
		}
		return hasPageId;
	}

	protected void saveChannelAssignments(MapContainerNode floor,
			List<MapContainerNode> floors, FloorCache cache,
			Map<Short, Short> ch1IndexMap, Map<Short, Short> ch2IndexMap,
			boolean update) {
		if (floors == null || cache == null) {
			return;
		}
		MapContainerNode higherFloor = null, lowerFloor = null;
		for (MapContainerNode container : floors) {
			if (container.getId().equals(floor.getId())) {
				lowerFloor = container;
			} else if (lowerFloor == null) {
				higherFloor = container;
			} else {
				lowerFloor = container;
				break;
			}
		}
		if (lowerFloor.getId().equals(floor.getId())) {
			lowerFloor = null;
		}
		if (!update) {
			cache.apIndexMap = new HashMap<Long, Short>();
			short apCount = setApIndexMap(floor, cache, (short) 0);
			apCount = setApIndexMap(higherFloor, cache, apCount);
			apCount = setApIndexMap(lowerFloor, cache, apCount);
			cache.ch1is = new short[apCount];
			cache.ch2is = new short[apCount];
		}
		saveChannelAssignments(floor, cache, ch1IndexMap, ch2IndexMap);
		saveChannelAssignments(higherFloor, cache, ch1IndexMap, ch2IndexMap);
		saveChannelAssignments(lowerFloor, cache, ch1IndexMap, ch2IndexMap);
	}

	protected short setApIndexMap(MapContainerNode floor, FloorCache cache,
			short index) {
		if (floor == null) {
			return index;
		}
		for (PlannedAP plannedAP : floor.getPlannedAPs()) {
			cache.apIndexMap.put(plannedAP.getId(), index++);
		}
		return index;
	}

	protected void saveChannelAssignments(MapContainerNode floor,
			FloorCache cache, Map<Short, Short> ch1IndexMap,
			Map<Short, Short> ch2IndexMap) {
		if (floor == null) {
			return;
		}
		for (PlannedAP plannedAP : floor.getPlannedAPs()) {
			short apIndex = cache.apIndexMap.get(plannedAP.getId());
			// g
			short wifi0Channel = plannedAP.autoWifi0Channel;
			if (wifi0Channel < 0) {
				wifi0Channel = plannedAP.wifi0Channel;
			}
			cache.ch1is[apIndex] = ch1IndexMap.get(wifi0Channel);

			// a
			short wifi1Channel = plannedAP.autoWifi1Channel;
			if (wifi1Channel < 0) {
				wifi1Channel = plannedAP.wifi1Channel;
			}
			cache.ch2is[apIndex] = ch2IndexMap.get(wifi1Channel);
		}
	}

	protected void cacheDoubleBuffer(MapContainerNode mapContainerNode,
			FloorCache cache, boolean spillOnly) throws Exception {
		Collection<JSONObject> spilled = BoMgmt.getLocationTracking()
				.cacheDoubleBuffer(mapContainerNode, canvasWidth, canvasHeight,
						cache.apIndexMap, cache.spillRssi, cache.spillChannels,
						cache.mapRssi, cache.mapChannels,
						cache.pixelSizeMetric, cache.imgScale, cache.useA,
						cache.shadesPerColor, spillOnly, cache.channelWidth);
		jsonObject.put("success", spilled != null);
		if (spilled != null) {
			jsonObject.put("spilled", spilled);
		}
		log.info("Spill cache ready.");
	}

	protected Long pageId, latchId, clientId, acspId, nextId;

	protected String bssid, hostName;

	protected short bssidType, hwModel, radio;

	protected boolean summaryChecked;

	protected boolean rogueChecked;

	protected boolean clientChecked;

	protected boolean mapExpanded;

	protected boolean gridChecked;

	public void setGridChecked(boolean gridChecked) {
		this.gridChecked = gridChecked;
	}

	public static final short BSSID_TYPE_CLIENT = 1;

	public static final short BSSID_TYPE_ROGUE = 2;

	protected List<Short> ch1s, ch1is, ch2s, ch2is;

	public void setCh1is(List<Short> ch1is) {
		this.ch1is = ch1is;
	}

	public void setCh1s(List<Short> ch1s) {
		this.ch1s = ch1s;
	}

	public void setCh2is(List<Short> ch2is) {
		this.ch2is = ch2is;
	}

	public void setCh2s(List<Short> ch2s) {
		this.ch2s = ch2s;
	}

	public void setSummaryChecked(boolean summaryChecked) {
		this.summaryChecked = summaryChecked;
	}

	public void setRogueChecked(boolean rogueChecked) {
		this.rogueChecked = rogueChecked;
	}

	public void setClientChecked(boolean clientChecked) {
		this.clientChecked = clientChecked;
	}

	public void setMapExpanded(boolean mapExpanded) {
		this.mapExpanded = mapExpanded;
	}

	public short getBssidType() {
		return bssidType;
	}

	public void setRadio(short radio) {
		this.radio = radio;
	}

	public void setHwModel(short hwModel) {
		this.hwModel = hwModel;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getSnrThreshold() {
		snrThreshold = getSelectedSNRThreshold();
		return snrThreshold;
	}

	public void setSnrThreshold(int snrThreshold) {
		this.snrThreshold = snrThreshold;
	}

	public int getRssiThreshold() {
		rssiThreshold = getSelectedRSSIThreshold();
		return rssiThreshold;
	}

	public void setRssiThreshold(int rssiThreshold) {
		this.rssiThreshold = rssiThreshold;
	}

	public int getIntRssiThreshold() {
		rssiThreshold = getSelectedRSSIThreshold();
		return rssiThreshold;
	}

	public void setIntRssiThreshold(int rssiThreshold) {
		this.rssiThreshold = rssiThreshold;
	}

	public int getRatesRssiThreshold() {
		rssiThreshold = getSelectedRSSIThreshold();
		return rssiThreshold;
	}

	public void setRatesRssiThreshold(int rssiThreshold) {
		this.rssiThreshold = rssiThreshold;
	}

	public void setLayers(int layers) {
		this.layers = layers;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public void setApLabels(String apLabels) {
		this.apLabels = apLabels;
	}

	public void setRapLabels(String rapLabels) {
		this.rapLabels = rapLabels;
	}

	public int getSelectedSNRThreshold() {
		int snrThreshold = PlanToolConfig.DEFAULT_SNR_THRESHOLD;
		Object obj = MgrUtil
				.getSessionAttribute(SessionKeys.SELECTED_SNR_THRESHOLD);
		if (null != obj && obj instanceof Integer) {
			snrThreshold = (Integer) obj;
		}
		return snrThreshold;
	}

	public void setSelectedSNRThreshold(int snrThreshold) {
		MgrUtil.setSessionAttribute(SessionKeys.SELECTED_SNR_THRESHOLD,
				snrThreshold);
	}

	public int getSelectedRSSIThreshold() {
		int rssiThreshold = PlanToolConfig.DEFAULT_RSSI_THRESHOLD;
		Object obj = MgrUtil
				.getSessionAttribute(SessionKeys.SELECTED_RSSI_THRESHOLD);
		if (null != obj && obj instanceof Integer) {
			rssiThreshold = (Integer) obj;
		}
		return rssiThreshold;
	}

	public void setSelectedRSSIThreshold(int rssiThreshold) {
		MgrUtil.setSessionAttribute(SessionKeys.SELECTED_RSSI_THRESHOLD,
				rssiThreshold);
	}

	public int getSelectedLayers() {
		int layers = 1;
		Object obj = MgrUtil.getSessionAttribute(SessionKeys.SELECTED_LAYERS);
		if (null != obj && obj instanceof Integer) {
			layers = (Integer) obj;
		}
		return layers;
	}

	public void setSelectedLayers(int layers) {
		MgrUtil.setSessionAttribute(SessionKeys.SELECTED_LAYERS, layers);
	}

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public Long getLatchId() {
		return latchId;
	}

	public void setLatchId(Long latchId) {
		this.latchId = latchId;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public void setAcspId(Long acspId) {
		this.acspId = acspId;
	}

	public void setNextId(Long nextId) {
		this.nextId = nextId;
	}

	public static EnumItem[] getWifiInterfaceList() {
		return HiveApToolkitAction.getWifiInterfaceList();
	}

	public static EnumItem[] getTransferPtclList() {
		return HiveApToolkitAction.getTransferPtclList();
	}

	public static EnumItem[] getTrafficTypeList() {
		return HiveApToolkitAction.getTrafficTypeList();
	}

	public static EnumItem[] getErrorConditionList() {
		return HiveApToolkitAction.getErrorConditionList();
	}

	public static EnumItem[] getEthValueList() {
		return HiveApToolkitAction.getEthValueList();
	}

	public short getBssidTypeClient() {
		return BSSID_TYPE_CLIENT;
	}

	public short getBssidTypeRogue() {
		return BSSID_TYPE_ROGUE;
	}

	public EnumItem[] getEnumChannelNgType() {
		PlanToolConfig planToolConfig = PlanToolAction
				.getPlanToolConfig(domainId);
		int countryCode = planToolConfig.getCountryCode();
		short channelWidth = planToolConfig.getChannelWidth();
		return PlanToolAction.getWifi0ChannelItems(countryCode, channelWidth);
	}

	public EnumItem[] getEnumPowerType() {
		return PlanToolAction.getEnumPowerType();
	}

	public short getChannelWidth() {
		return planToolConfig.getChannelWidth();
	}

	public String getWallLineColors() {
		JSONArray wallColors = new JSONArray();
		if (null != planToolConfig) {
			for (int i = 0; i < Wall.wallIds.length; i++) {
				short id = Wall.wallIds[i];
				switch (id) {
				case 1001:
					wallColors.put(planToolConfig.getWallColorBookshelf());
					break;
				case 1002:
					wallColors.put(planToolConfig.getWallColorCubicle());
					break;
				case 1003:
					wallColors.put(planToolConfig.getWallColorDryWall());
					break;
				case 1004:
					wallColors.put(planToolConfig.getWallColorBrickWall());
					break;
				case 1005:
					wallColors.put(planToolConfig.getWallColorConcrete());
					break;
				case 1006:
					wallColors.put(planToolConfig.getWallColorElevatorShaft());
					break;
				case 1007:
					wallColors.put(planToolConfig.getWallColorThinDoor());
					break;
				case 1010:
					wallColors.put(planToolConfig.getWallColorThickDoor());
					break;
				case 1009:
					wallColors.put(planToolConfig.getWallColorThinWindow());
					break;
				case 1008:
					wallColors.put(planToolConfig.getWallColorThickWindow());
					break;
				}
			}
		}
		return wallColors.toString();
	}

	public String getWallLineTypes() {
		JSONArray wallTypes = new JSONArray();
		if (null != planToolConfig) {
			for (int i = 0; i < Wall.wallIds.length; i++) {
				short id = Wall.wallIds[i];
				switch (id) {
				case 1001:
					wallTypes.put(planToolConfig.getWallTypeBookshelf() ? "-"
							: "");
					break;
				case 1002:
					wallTypes.put(planToolConfig.getWallTypeCubicle() ? "-"
							: "");
					break;
				case 1003:
					wallTypes.put(planToolConfig.getWallTypeDryWall() ? "-"
							: "");
					break;
				case 1004:
					wallTypes.put(planToolConfig.getWallTypeBrickWall() ? "-"
							: "");
					break;
				case 1005:
					wallTypes.put(planToolConfig.getWallTypeConcrete() ? "-"
							: "");
					break;
				case 1006:
					wallTypes
							.put(planToolConfig.getWallTypeElevatorShaft() ? "-"
									: "");
					break;
				case 1007:
					wallTypes.put(planToolConfig.getWallTypeThinDoor() ? "-"
							: "");
					break;
				case 1010:
					wallTypes.put(planToolConfig.getWallTypeThickDoor() ? "-"
							: "");
					break;
				case 1009:
					wallTypes.put(planToolConfig.getWallTypeThinWindow() ? "-"
							: "");
					break;
				case 1008:
					wallTypes.put(planToolConfig.getWallTypeThickWindow() ? "-"
							: "");
					break;
				}
			}
		}
		return wallTypes.toString();
	}

	public short channelWidth;

	public void setChannelWidth(short channelWidth) {
		this.channelWidth = channelWidth;
	}

	public String getCWHT20() {
		return getChannelWidth() == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 ? ""
				: "none";
	}

	public String getCWHT40() {
		return getChannelWidth() == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A
				|| getChannelWidth() == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40B ? ""
				: "none";
	}

	public String getCWHT80() {
		return getChannelWidth() == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80 ? ""
				: "none";
	}

	public String getWallTypes() throws Exception {
		Collection<JSONObject> wallTypeObjs = new Vector<JSONObject>();
		for (String wallType : Wall.wallTypes) {
			JSONObject wallTypeObj = new JSONObject();
			wallTypeObj.put("text", wallType);
			wallTypeObjs.add(wallTypeObj);
		}
		return new JSONArray(wallTypeObjs).toString();
	}

	public String getPerimTypes() throws Exception {
		Collection<JSONObject> wallTypeObjs = new Vector<JSONObject>();
		for (int i = 0; i < Wall.wallIds.length; i++) {
			if (perimTypeWall(Wall.wallIds[i])) {
				JSONObject wallTypeObj = new JSONObject();
				wallTypeObj.put("text", Wall.wallTypes[i]);
				wallTypeObjs.add(wallTypeObj);
			}
		}
		return new JSONArray(wallTypeObjs).toString();
	}

	public EnumItem[] getApModel() {
		return NmsUtil.filterHiveAPModelPlanning(HiveAp.HIVEAP_MODEL_PLANNING,
				this.isEasyMode());
	}

	public EnumItem[] getRadios() {
		return PlanToolConfig.RADIOS;
	}

	public EnumItem[] getWallTypeInfo() {
		return Wall.WALL_TYPES;
	}

	public int getNewWallType() {
		return 1003;
	}

	public List<Entry<Integer, String>> getCountryCodeValues() {
		return CountryCode.getCountryCodeList();
	}

	public int getCountryCode() {
		return planToolConfig.getCountryCode();
	}

	PlanToolConfig planToolConfig;

	public int getDefApModel() {
		return planToolConfig.getDefaultApType();
	}

	public int getDefWifi0Channel() {
		return planToolConfig.getWifi0Channel();
	}

	public int getDefWifi0Power() {
		return planToolConfig.getWifi0Power();
	}

	public int getDefWifi1Channel() {
		return planToolConfig.getWifi1Channel();
	}

	public int getDefWifi1Power() {
		return planToolConfig.getWifi1Power();
	}

	public EnumItem[] getEnumChannelWidth() {
		return PlanToolAction.getEnumChannelWidth();
	}

	private static EnumItem[] targetAppEnumItems = MgrUtil.enumItems(
			"enum.app.", new int[] { 100, 200, 300, 400 });

	public EnumItem[] getEnumTargetApp() {
		return targetAppEnumItems;
	}

	public EnumItem[] getEnumFadeMargin() {
		return PlanToolAction.getEnumFadeMargin();
	}

	public int getFadeMargin() {
		return planToolConfig.getFadeMargin();
	}

	public void setFadeMargin(int fadeMargin) {
		this.fadeMargin = fadeMargin;
	}

	public int fadeMargin;

	public static class PlannedApMgmtImpl implements PlannedApMgmt {
		public PlannedAP createPlannedAP(MapContainerNode mapContainerNode,
				PlannedAP plannedAP, HmUser user, String feature)
				throws Exception {
			plannedAP.setParentMap(mapContainerNode);
			plannedAP = findPlannedAP(BoMgmt.createBo(plannedAP, user, feature));
			if (plannedAP != null) {
				setHostName(plannedAP);
				plannedAP = QueryUtil.updateBo(plannedAP);
			}
			return plannedAP;
		}

		public PlannedAP findPlannedAP(long plannedId) throws Exception {
			PlannedAP plannedAP = QueryUtil.findBoById(PlannedAP.class,
					plannedId);
			return plannedAP;
		}

		public void loadPlannedAPs(MapContainerNode mapContainerNode,
				Set<MapNode> childNodes) {
			// Load from database
			for (PlannedAP plannedAP : mapContainerNode.getPlannedAPs()) {
			}
		}

		public static void setHostName(PlannedAP plannedAP) {
			String prefix = MgrUtil.getEnumString("enum.hiveAp.model."
					+ plannedAP.apModel);
			plannedAP.hostName = (plannedAP.apModel == HiveAp.HIVEAP_MODEL_20
					|| plannedAP.apModel == HiveAp.HIVEAP_MODEL_28 ? prefix
					.substring(0, 4) : prefix.substring(0, 5))
					+ "-"
					+ Long.toString(1000000000 + plannedAP.getId())
							.substring(3);
		}

		public PlannedAP updatePlannedAP(MapContainerNode mapContainerNode,
				long plannedId, String hostName, short apModel,
				short wifi0Channel, short wifi1Channel, short wifi0Power,
				short wifi1Power, short radio) throws Exception {
			log.info("updatePlannedAP", "Planned AP: " + plannedId);
			PlannedAP plannedAP = findPlannedAP(plannedId);
			if (plannedAP == null) {
				return null;
			}
			plannedAP.hostName = hostName;
			plannedAP.apModel = apModel;
			plannedAP.wifi0Channel = wifi0Channel;
			plannedAP.wifi1Channel = wifi1Channel;
			plannedAP.wifi0Power = wifi0Power;
			plannedAP.wifi1Power = wifi1Power;
			if (apModel == HiveAp.HIVEAP_MODEL_110
					|| apModel == HiveAp.HIVEAP_MODEL_BR200_WP
					|| apModel == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ) {
				if (radio == PlanToolConfig.RADIO_24GHZ) {
					plannedAP.wifi0Enabled = true;
					plannedAP.wifi1Enabled = false;
				} else if (radio == PlanToolConfig.RADIO_5GHZ) {
					plannedAP.wifi0Enabled = false;
					plannedAP.wifi1Enabled = true;
				}
			} else {
				plannedAP.wifi0Enabled = true;
				plannedAP.wifi1Enabled = (apModel != HiveAp.HIVEAP_MODEL_BR100);
			}
			return QueryUtil.updateBo(plannedAP);
		}

		public PlannedAP movePlannedAP(MapContainerNode mapContainerNode,
				long plannedId, double x, double y) throws Exception {
			log.info("updatePlannedAP", "Planned AP: " + plannedId);
			PlannedAP plannedAP = findPlannedAP(plannedId);
			if (plannedAP == null) {
				return null;
			}
			plannedAP.x = x;
			plannedAP.y = y;
			return QueryUtil.updateBo(plannedAP);
		}

		public void removePlannedAP(long plannedId) throws Exception {
			QueryUtil.removeBo(PlannedAP.class, plannedId);
		}
	}

	// export XML file name
	private String fileName;

	// import XML file
	private File uploadXml;
	private String uploadXmlFileName;
	private String uploadXmlContentType;
	private boolean overrideBg;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File getUploadXml() {
		return uploadXml;
	}

	public String getUploadXmlFileName() {
		return uploadXmlFileName;
	}

	public void setUploadXml(File uploadXml) {
		this.uploadXml = uploadXml;
	}

	public void setUploadXmlFileName(String uploadXmlFileName) {
		this.uploadXmlFileName = uploadXmlFileName;
	}

	public String getUploadXmlContentType() {
		return uploadXmlContentType;
	}

	public void setUploadXmlContentType(String uploadXmlContentType) {
		this.uploadXmlContentType = uploadXmlContentType;
	}

	public boolean isOverrideBg() {
		return overrideBg;
	}

	public void setOverrideBg(boolean overrideBg) {
		this.overrideBg = overrideBg;
	}

	/*---------------- Import XML ----------------*/
	private boolean validateMapNode(MapContainerNode importNode,
			MapContainerNode parenMapNode) throws JSONException {
		final short mapType = importNode.getMapType();
		if (parenMapNode.getMapType() == 1
				&& mapType == MapContainerNode.MAP_TYPE_FLOOR) {
			jsonObject.put("msg",
					MgrUtil.getUserMessage("error.topo.import.xml.format.1"));
			return false;
		} else if (parenMapNode.getMapType() == MapContainerNode.MAP_TYPE_BUILDING
				&& mapType != MapContainerNode.MAP_TYPE_FLOOR) {
			// parent node is building, the child should not be building or
			// folder
			jsonObject.put("msg",
					MgrUtil.getUserMessage("error.topo.import.xml.format.2"));
			return false;
		} else {
			validateMapNodeName(importNode, parenMapNode);

			// XML content validate
			for (MapNode child : importNode.getChildNodes()) {
				if (child.isLeafNode()) {
					continue;
				}
				if (!validateMapNode(((MapContainerNode) child), importNode)) {
					return false;
				}
			}
		}
		return true;
	}

	private void validateMapNodeName(MapContainerNode importNode,
			MapContainerNode parenMapNode) {
		// Check whether exists same map name
		String mapName = importNode.getMapName();
		final short mapType = importNode.getMapType();
		final String middleName = "_i_";
		if (mapType == MapContainerNode.MAP_TYPE_FLOOR) {
			if (null != parenMapNode.getId()) {
				// the import XML content only contains the floor element
				Set<String> mapNames = new HashSet<String>();
				for (MapNode node : parenMapNode.getChildNodes()) {
					if (node.isLeafNode()) {
						continue;
					}
					mapNames.add(((MapContainerNode) node).getMapName());
				}
				int index = 1;
				while (mapNames.contains(mapName)) {
					mapName = importNode.getMapName() + middleName + index++;
				}
				importNode.setMapName(mapName);
			}
		} else {
			// Folder/Build
			boolean existedName = false;
			int index = 1;
			do {
				List<?> list = QueryUtil
						.executeNativeQuery(
								"select id, mapType, parent_map_id from map_node where node_type = 'CN' and mapName = '"
										+ NmsUtil.convertSqlStr(importNode.getMapName())
										+ "' and owner = " + domainId, 1);
				existedName = !list.isEmpty();
				if (existedName) {
					importNode.setMapName(mapName + middleName + index++);
				}
			} while (existedName);
		}
	}

	private boolean createMapContainerFromXmlContent(
			MapContainerNode importNode, MapContainerNode parenMapNode,
			boolean subElementFlag) {
		try {
			// auto expand parent node
			MapAlarmsCache<?> cache = (MapAlarmsCache<?>) MgrUtil
					.getSessionAttribute(SessionKeys.MAP_ALARMS_CACHE);
			if (cache != null) {
				cache.setTreeNodeExpanded(parenMapNode.getId(), true);
			}
			BoMgmt.getMapMgmt().placeIcon(parenMapNode, importNode);
			importNode.setParentMap(parenMapNode);
			importNode.setOwner(parenMapNode.getOwner()); // set owner
			if (subElementFlag
					&& importNode.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
				// for the sub floor type element, it is not necessary to
				// calculate the order.
				QueryUtil.createBo(importNode);
			} else {
				BoMgmt.getMapMgmt().createMapContainer(importNode, null);
			}
			BoObserver.notifyListeners(new BoEvent(importNode,
					BoEventType.CREATED));

			for (PlannedAP plannedAp : importNode.getPlannedAPs()) {
				BoMgmt.getPlannedApMgmt().createPlannedAP(importNode,
						plannedAp, getUserContext(), getSelectedL2FeatureKey());
			}
			for (MapNode childNode : importNode.getChildNodes()) {
				if (childNode.isLeafNode()) {
					continue;
				}
				// recursion
				if (!createMapContainerFromXmlContent(
						(MapContainerNode) childNode, importNode, true)) {
					return false;
				}
			}
		} catch (Exception e) {
			log.error("Error occurs when create the map node from xml data.", e);
			return false;
		}
		return true;
	}

	private String makeXMLBgImages2Tar(final String mapName,
			final String time_suffix, String destFilePath,
			Set<String> imageNames) throws IOException {
		if (!(null == imageNames || imageNames.isEmpty())) {
			final String seperator = "/";
			final String ext = ".tar";

			String tarFolderPath = getTarFileRealPath(time_suffix);
			File tarFolder = new File(tarFolderPath);
			if (!tarFolder.exists()) {
				tarFolder.mkdirs();
			}

			// copy the background images to subfolder 'bgImages' under tar
			// folder
			if (null != getDomain()) {
				String imagepath = BeTopoModuleUtil
						.getRealTopoBgImagePath(getDomain().getDomainName());
				for (String imageName : imageNames) {
					FileUtils.copyFileToDirectory(new File(imagepath
							+ seperator + imageName), new File(tarFolder,
							"bgImages"));
				}
			}
			final File xmlFile = new File(destFilePath);
			FileUtils.copyFileToDirectory(xmlFile, tarFolder);

			final String destTarFilePath = tarFolder.getParentFile()
					.getAbsolutePath()
					+ seperator
					+ mapName
					+ "_"
					+ time_suffix + ext;
			if (new TarArchive().create(tarFolderPath, destTarFilePath)) {
				// copy tar file to xml folder
				final File tarFile = new File(destTarFilePath);
				FileUtils.copyFileToDirectory(tarFile, xmlFile.getParentFile());

				// delete files under /tmp/tar
				FileUtils.deleteQuietly(tarFolder);
				FileUtils.deleteQuietly(tarFile);
				return tarFile.getName();
			}
		}
		return null;
	}

	private boolean parseDataFromXML(File file) throws JSONException {
		boolean parseFlag = false;
		// xml data file
		TopoXMLConvertor convertor = new TopoXMLConvertor();
		MapContainerNode importNode = new MapContainerNode();
		BooleanMsgPair result = convertor.convert2Obj(file.getAbsolutePath(),
				importNode);
		if (result.getValue()) {
			MapContainerNode parenMapNode = (MapContainerNode) QueryUtil
					.findBoById(MapNode.class, id, this);
			if (null == parenMapNode) {
				jsonObject
						.put("msg",
								MgrUtil.getUserMessage("error.topo.import.xml.invalid.parentNode"));
			} else {
				if (validateMapNode(importNode, parenMapNode)) {
					if (createMapContainerFromXmlContent(importNode,
							parenMapNode, false)) {

						// refresh the instance permissions
						refreshInstancePermissions();

						jsonObject.put("succ", true);
						parseFlag = true;
					} else {
						jsonObject
								.put("msg",
										MgrUtil.getUserMessage("error.topo.import.xml.create.node"));
					}
				}
			}

		} else {
			jsonObject.put("succ", result.getValue());
			jsonObject.put("msg", result.getDesc());
		}
		return parseFlag;
	}

	private String getTarFolderPath() {
		return "/tmp/tar/" + getDomain().getDomainName();
	}

	private String getTarFileRealPath(final String fileName) {
		return getTarFolderPath() + "/" + fileName;
	}

	private String getFolderDataFileRealPath(final String fileName) {
		return "/tmp/fdData/" + getDomain().getDomainName() + "/" + fileName;
	}
	
}