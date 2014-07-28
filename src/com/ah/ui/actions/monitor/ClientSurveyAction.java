package com.ah.ui.actions.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapSettings;
import com.ah.bo.monitor.Trex;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class ClientSurveyAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(ClientSurveyAction.class
			.getSimpleName());

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		try {
			if ("remove".equals(operation)) {
				MgrUtil.removeSessionAttribute(SessionKeys.MR_HEAT_MAP);
				baseOperation();
				return prepareSurveyList();
			} else if ("editPower".equals(operation)) {
				MapSettings mapSettings = BeTopoModuleUtil
						.getMapGlobalSetting(getDomain());
				jsonObject = new JSONObject();
				jsonObject.put("erp", mapSettings.getSurveyErp());
				jsonObject.put("useErp", mapSettings.isUseSurveyErp());
				return "json";
			} else if ("updatePower".equals(operation)) {
				MapSettings mapSettings = BeTopoModuleUtil
						.getMapGlobalSetting(getDomain());
				mapSettings.setSurveyErp(power);
				mapSettings.setUseSurveyErp(usePower);
				if (mapSettings.getOwner() == null) {
					mapSettings.setOwner(getDomain());
					QueryUtil.createBo(mapSettings);
				} else {
					QueryUtil.updateBo(mapSettings);
				}
				jsonObject = new JSONObject();
				jsonObject.put("suc", true);
				return "json";
			} else if ("editRssi".equals(operation)) {
				MapSettings mapSettings = BeTopoModuleUtil
						.getMapGlobalSetting(getDomain());
				jsonObject = new JSONObject();
				jsonObject.put("lowRssi", mapSettings.getRssiFrom());
				jsonObject.put("highRssi", mapSettings.getRssiUntil());
				return "json";
			} else if ("updateRssi".equals(operation)) {
				MapSettings mapSettings = BeTopoModuleUtil
						.getMapGlobalSetting(getDomain());
				mapSettings.setRssiFrom(-lowRssi);
				mapSettings.setRssiUntil(-highRssi);
				if (mapSettings.getOwner() == null) {
					mapSettings.setOwner(getDomain());
					QueryUtil.createBo(mapSettings);
				} else {
					QueryUtil.updateBo(mapSettings);
				}
				jsonObject = new JSONObject();
				jsonObject.put("suc", true);
				return "json";
			} else if ("search".equals(operation)) {
				log.info("execute", "Filter by map: " + filterMap);
				if (filterMap == null || filterMap <= 0) {
					filterParams = null;
				} else {
					filterParams = new FilterParams("parentMap.id", filterMap);
				}
				setSessionFiltering();
				return prepareSurveyList();
			} else {
				baseOperation();
				return prepareSurveyList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CLIENTSURVEY);
		setDataSource(Trex.class);
	}

	protected String prepareSurveyList() throws Exception {
		clearDataSource();
		MgrUtil.removeSessionAttribute("lstTitle");
		MgrUtil.removeSessionAttribute("lstTabId");
		MgrUtil.removeSessionAttribute("lstForward");
		MgrUtil.removeSessionAttribute("lstFormChanged");
		getSessionFiltering();
		if (filterParams != null) {
			filterMap = (Long) filterParams.getValue();
		}
		preparePage();
		setTableColumns();
		queryLazyInfo(page);
		return SUCCESS;
	}

	private void queryLazyInfo(List<Trex> clients) {
		if (clients.isEmpty()) {
			return;
		}
		Map<Long, Trex> clientMap = new HashMap<Long, Trex>();
		for (Trex client : clients) {
			clientMap.put(client.getId(), client);
		}
		MapSettings mapSettings = BeTopoModuleUtil
				.getMapGlobalSetting(getDomain());
		List<?> mapNames = QueryUtil.executeQuery(
				"select id, parentMap.mapName from "
						+ Trex.class.getSimpleName(), null, new FilterParams(
						"id", clientMap.keySet()));
		for (Object obj : mapNames) {
			Object[] mapName = (Object[]) obj;
			Long id = (Long) mapName[0];
			Trex client = clientMap.get(id);
			client.mapName = (String) mapName[1];
			client.filtered = client.rssi < mapSettings.getRssiFrom()
					|| client.rssi > mapSettings.getRssiUntil();
		}
	}

	public List<CheckItem> getRssiThresholdValues() {
		return MapsAction.getRssiThresholdValues();
	}

	private Long filterMap;

	public Long getFilterMap() {
		return filterMap;
	}

	public void setFilterMap(Long filterMap) {
		this.filterMap = filterMap;
	}

	private List<CheckItem> filterMaps;

	public List<CheckItem> getFilterMaps() {
		Collection<Long> includeOnly = (Collection<Long>) QueryUtil
				.executeQuery("select distinct parentMap.id from "
						+ Trex.class.getSimpleName(), null, null, getDomainId());
		if (filterMap != null) {
			includeOnly.add(filterMap);
		}
		List<CheckItem> maps = BoMgmt.getMapMgmt().getMapListView(
				getDomainId(), includeOnly);
		filterMaps = new ArrayList<CheckItem>();
		filterMaps.addAll(maps);
		return filterMaps;
	}

	double power;

	boolean usePower;

	int lowRssi, highRssi;

	public void setHighRssi(int highRssi) {
		this.highRssi = highRssi;
	}

	public void setLowRssi(int lowRssi) {
		this.lowRssi = lowRssi;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public void setUsePower(boolean usePower) {
		this.usePower = usePower;
	}

	protected JSONObject jsonObject = null;

	public String getJSONString() {
		log.debug("getJSONString", "JSON string: " + jsonObject.toString());
		return jsonObject.toString();
	}
}
