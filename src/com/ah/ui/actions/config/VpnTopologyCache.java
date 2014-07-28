package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBePerformUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeVPNStatusResultEvent;
import com.ah.be.db.configuration.ConfigurationUtils;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.VpnService;
import com.ah.bo.performance.AhVPNStatus;
import com.ah.bo.performance.AhVPNStatus.VpnStatus;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class VpnTopologyCache implements HttpSessionBindingListener {

	private static final Tracer log = new Tracer(VpnTopologyCache.class
			.getSimpleName());

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		log.info("valueBound", "Bound event: " + event.getName());
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		log.info("valueUnbound", "Unbound event: " + event.getName());
	}

	private Long vpnServiceId;

	private Long pageId;

	private Long mapId;

	private boolean updated = false; // for refresh used

	private List<AhVPNStatus> vpnStatuses;
	private Map<String, HiveAp> servers;
	private Map<String, HiveAp> clients;

	/*
	 * Set up vpn service cache for new vpn service.
	 */
	public synchronized void setServiceId(Long vpnServiceId, Long pageId) {
		if (vpnServiceId == null || pageId == null) {
			return;
		}
		log.info("setServiceId",
				"Setting up vpn topology cache for vpn service: "
						+ vpnServiceId + ", page: " + pageId);
		this.vpnServiceId = vpnServiceId;
		this.pageId = pageId;

		this.servers = null;
		this.clients = null;
		this.vpnStatuses = null;
	}

	private boolean validVpnService(Long vpnServiceId, Long pageId) {
		if (this.vpnServiceId == null || this.pageId == null) {
			return false;
		}
		if (!this.vpnServiceId.equals(vpnServiceId)) {
			log.info("validVpnService",
					"Get cached data must be for VPN service: "
							+ this.vpnServiceId);
			return false;
		}
		if (!this.pageId.equals(pageId)) {
			log.info("validVpnService", "Get cached data must be from page: "
					+ this.pageId);
			return false;
		}
		return true;
	}

	public JSONObject refreshVpnTopologys(Long id, Long pageId)
			throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("pageId", pageId);
		if (!validVpnService(id, pageId)) {
			return jsonObject;
		}
		if (null == servers || servers.isEmpty()) {
			log.info("refreshVpnTopologys",
					"cannot find VPN servers in VPN service:" + id);
			jsonObject.put("error",
					"No VPN server binding in VPN service profile.");
			return jsonObject;
		}
		try {
			List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, null,
					new FilterParams("macAddress", servers.keySet()));
			List<BeCommunicationEvent> requests = new ArrayList<BeCommunicationEvent>();
			for (HiveAp hiveAp : list) {
				if (NmsUtil.compareSoftwareVersion("3.4.2.0", hiveAp
						.getSoftVer()) > 0) {
					throw new Exception(MgrUtil.getUserMessage(
							"error.hiveAp.feature.support.version", "3.4r2"));
				}
				requests.add(BeTopoModuleUtil.getVpnStatusRequest(hiveAp));
			}
			List<BeVPNStatusResultEvent> vpnStatus = new ArrayList<BeVPNStatusResultEvent>();
			List<BeCommunicationEvent> results = HmBeCommunicationUtil
					.sendSyncGroupRequest(requests);
			if (null != results) {
				for (BeCommunicationEvent result : results) {
					BeVPNStatusResultEvent event = BeTopoModuleUtil
							.getBeVPNStatusResult(result);
					if (null != event) {
						vpnStatus.add(event);
					}
				}
			}

			if (!vpnStatus.isEmpty()) {
				for (BeVPNStatusResultEvent result : vpnStatus) {
					HmBePerformUtil.handleVPNStatusResultEvent(result);
				}
				updated = true;
				String message = "";
				// partly failed.
				if (vpnStatus.size() != requests.size()) {
					for (BeCommunicationEvent request : requests) {
						String hostName = request.getAp().getHostName();
						String mac = request.getApMac();
						boolean success = false;
						for (BeVPNStatusResultEvent result : vpnStatus) {
							if (mac.equals(result.getApMac())) {
								success = true;
								break;
							}
						}
						message += (message.isEmpty() ? "" : "<br>")
								+ (!success ? MgrUtil.getUserMessage(
										"error.hiveAp.vpn.refresh.failed.m",
										hostName) : MgrUtil.getUserMessage(
										"info.hiveAp.vpn.refresh.success.m",
										hostName));
					}
				}
				if (!message.isEmpty()) {
					throw new Exception(message);
				}
			} else {
				// full failed.
				throw new Exception(MgrUtil
						.getUserMessage("error.hiveAp.vpn.refresh.failed"));
			}
		} catch (BeCommunicationEncodeException e) {
			log.error("refreshVpnTopologys", e);
			jsonObject.put("error", MgrUtil
					.getUserMessage("error.hiveAp.update.request.build"));
		} catch (Exception e) {
			log.error("refreshVpnTopologys", e.getMessage());
			jsonObject.put("error", e.getMessage());
		}
		return jsonObject;
	}

	public JSONObject getVpnTopologys(Long id, Long pageId, Long newMapId)
			throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("pageId", pageId);
		if (!validVpnService(id, pageId)) {
			return jsonObject;
		}
		if (isVpnTopologyChanged(newMapId) && null != servers
				&& null != clients && null != vpnStatuses) {
			jsonObject.put("updated", true);
			// VPN server
			JSONArray jsonServers = new JSONArray();
			for (String nodeId : servers.keySet()) {
				HiveAp hiveAp = servers.get(nodeId);
				short severity = AhVPNStatus.isVpnServerUp(vpnStatuses, nodeId) == VpnStatus.Up ? AhAlarm.AH_SEVERITY_UNDETERMINED
						: AhAlarm.AH_SEVERITY_CRITICAL;
				JSONObject server = new JSONObject();
				server.put("apName", hiveAp.getHostName());
				server.put("nodeId", hiveAp.getMacAddress());
				server.put("s", severity);
				jsonServers.put(server);
			}
			jsonObject.put("servers", jsonServers);

			// VPN client
			JSONArray jsonClients = new JSONArray();
			for (String nodeId : clients.keySet()) {
				HiveAp hiveAp = clients.get(nodeId);
				VpnStatus status = AhVPNStatus.getVpnClientStatus(vpnStatuses,
						nodeId, servers.size() > 1);
				short severity = status == VpnStatus.Up ? AhAlarm.AH_SEVERITY_UNDETERMINED
						: (status == VpnStatus.Half ? AhAlarm.AH_SEVERITY_MAJOR
								: AhAlarm.AH_SEVERITY_CRITICAL);
				JSONObject client = new JSONObject();
				client.put("apName", hiveAp.getHostName());
				client.put("nodeId", hiveAp.getMacAddress());
				client.put("s", severity);
				jsonClients.put(client);
			}
			jsonObject.put("clients", jsonClients);

			// VPN links
			JSONArray jsonLinks = new JSONArray();
			for (AhVPNStatus status : vpnStatuses) {
				String serverNodeId = status.getServerID();
				String clientNodeId = status.getClientID();
				long timeMillis = status.getConnectTimeStamp();
				if (null != serverNodeId && null != clientNodeId) {
					JSONObject link = new JSONObject();
					link.put("from", clientNodeId);
					link.put("to", serverNodeId);
					link.put("connected", true);
					link.put("upTime", System.currentTimeMillis() - timeMillis);
					jsonLinks.put(link);
				}
			}
			jsonObject.put("links", jsonLinks);

			// maps
			if (null != clients && !clients.isEmpty()) {
				Map<Long, String> maps = new HashMap<Long, String>();
				for (String nodeId : clients.keySet()) {
					HiveAp hiveAp = clients.get(nodeId);
					Long mid = null;
					String map = null;
					if (null != hiveAp.getMapContainer()) {
						mid = hiveAp.getMapContainer().getId();
						map = hiveAp.getMapContainer().getMapName();
					}
					maps.put(mid, map);
				}
				if (maps.size() > 1) {
					JSONArray jsonMaps = new JSONArray();
					for (Long mid : maps.keySet()) {
						JSONObject jsonMap = new JSONObject();
						if (null == mid) {
							jsonMap.put("id", 0);
							jsonMap.put("n", " ");
						} else {
							jsonMap.put("id", mid);
							jsonMap.put("n", maps.get(mid));
						}
						jsonMaps.put(jsonMap);
					}
					jsonObject.put("maps", jsonMaps);
				}
			}
		}
		return jsonObject;
	}

	private boolean isVpnTopologyChanged(Long newMapId) {
		Map<String, HiveAp> servers = null;
		Map<String, HiveAp> clients = null;
		List<AhVPNStatus> vpnStatuses = null;
		boolean changed = false;
		if (null != newMapId && !newMapId.equals(this.mapId)) {
			this.mapId = newMapId;
			changed = true;
		}
		if (this.updated) {
			this.updated = false;
			changed = true;
		}

		if (null != vpnServiceId) {
			VpnService vpn = QueryUtil.findBoById(VpnService.class,
					vpnServiceId);
			if (null != vpn) {
				Set<Long> vpnServers = ConfigurationUtils.getRelevantVpnServers(vpn);
				Set<Long> vpnClients = ConfigurationUtils.getRelevantVpnClients(vpn);

				// setup VPN topology needs one server and one client at least!
				if (!vpnServers.isEmpty() && !vpnClients.isEmpty()) {
					// query VPN server status
					Set<Long> total = new HashSet<Long>();
					total.addAll(vpnServers);
					total.addAll(vpnClients);
					List<?> list = QueryUtil.executeQuery(
							"select bo.id, bo.macAddress, bo.hostName from "
									+ HiveAp.class.getSimpleName() + " bo",
							null, new FilterParams("id", total));
					List<?> mapAttrs = QueryUtil.executeQuery(
							"select bo.macAddress, bo.mapContainer.id, bo.mapContainer.mapName from "
									+ HiveAp.class.getSimpleName() + " bo",
							null, new FilterParams("id", total));
					// store map info
					Map<String, MapContainerNode> mapInfos = new HashMap<String, MapContainerNode>();
					for (Object object : mapAttrs) {
						Object[] attrs = (Object[]) object;
						MapContainerNode container = new MapContainerNode();
						container.setId((Long) attrs[1]);
						container.setMapName((String) attrs[2]);
						mapInfos.put((String) attrs[0], container);
					}

					servers = new HashMap<String, HiveAp>(vpnServers.size());
					clients = new HashMap<String, HiveAp>(vpnServers.size());
					for (Object object : list) {
						Object[] attrs = (Object[]) object;
						Long apId = (Long) attrs[0];
						String nodeId = (String) attrs[1];
						String hostName = (String) attrs[2];
						Long mapId = null;
						MapContainerNode container = mapInfos.get(nodeId);
						if (null != container) {
							mapId = container.getId();
						}
						HiveAp hiveAp = new HiveAp();
						hiveAp.setId(apId);
						hiveAp.setMacAddress(nodeId);
						hiveAp.setHostName(hostName);
						hiveAp.setMapContainer(container);

						if (vpnServers.contains(apId)) {
							servers.put(nodeId, hiveAp);
						} else {
							if (null != this.mapId && this.mapId > 0) {
								// filter by a map
								if (this.mapId.equals(mapId)) {
									clients.put(nodeId, hiveAp);
								}
							} else if (null != this.mapId && this.mapId == 0) {
								// filter by blank
								if (null == mapId) {
									clients.put(nodeId, hiveAp);
								}
							} else {
								// no filter
								clients.put(nodeId, hiveAp);
							}
						}
					}

					if (!clients.isEmpty()) {
						// query client status
						String where = "serverID in (:s1) and (clientID in (:s2) or clientID is null)";
						vpnStatuses = QueryUtil.executeQuery(AhVPNStatus.class,
								null, new FilterParams(where, new Object[] {
										servers.keySet(), clients.keySet() }));
					}
				} else {
					log.error("prepareTopologys",
							"no configured VPN Server or Client.");
				}
			}
		}
		if (changed
				|| (null == this.servers && null != servers)
				|| (null != this.servers && null == servers)
				|| (null != this.servers && null != servers && this.servers
						.size() != servers.size())) {
			this.servers = servers;
			changed = true;
		}

		if (changed
				|| (null == this.clients && null != clients)
				|| (null != this.clients && null == clients)
				|| (null != this.clients && null != clients && this.clients
						.size() != clients.size())) {
			this.clients = clients;
			changed = true;
		}

		if (changed
				|| (null == this.vpnStatuses && null != vpnStatuses)
				|| (null != this.vpnStatuses && null == vpnStatuses)
				|| (null != this.vpnStatuses && null != vpnStatuses && this.vpnStatuses
						.size() != vpnStatuses.size())) {
			this.vpnStatuses = vpnStatuses;
			changed = true;
		} else {
			// check item when vpn status size no changed
			if (null != this.vpnStatuses && null != vpnStatuses) {
				// if the summary of the id value changed, the items must be
				// changed
				long value = 0, newvalue = 0;
				for (AhVPNStatus status : this.vpnStatuses) {
					value += status.getId();
				}
				for (AhVPNStatus status : vpnStatuses) {
					newvalue += status.getId();
				}
				if (value != newvalue) {
					this.vpnStatuses = vpnStatuses;
					changed = true;
				}
			}
		}
		return changed;
	}

}
