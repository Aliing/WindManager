package com.ah.ui.actions.hiveap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpSessionBindingEvent;

import org.json.JSONObject;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.PagingCache;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.performance.AhLatestXif;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class IdpPagingCache extends PagingCache<Idp> {

	private static final Tracer log = new Tracer(IdpPagingCache.class
			.getSimpleName());

	private final HmUser user;

	private int viewMode;// detailed or plain

	private List<Long> selectedIds;

	public IdpPagingCache(HmUser user) {
		super(Idp.class, user);
		this.user = user;
	}

	public void setSelectedIds(List<Long> selectedIds) {
		this.selectedIds = selectedIds;
	}

	public void setViewMode(int viewMode) {
		this.viewMode = viewMode;
	}

	@Override
	protected List<Idp> findBos() {
		SortParams sortParams = (SortParams) MgrUtil
				.getSessionAttribute(Idp.class.getSimpleName() + "Sorting");
		FilterParams filterParams = (FilterParams) MgrUtil
				.getSessionAttribute(Idp.class.getSimpleName() + "Filtering");
		Paging<Idp> paging = (Paging<Idp>) MgrUtil
				.getSessionAttribute(Idp.class.getSimpleName() + "Paging");
		GroupByParams groupParams = new GroupByParams(new String[] {
				"ifMacAddress", "owner.id" });
		if (paging == null) {
			return null;
		}
		paging.clearRowCount();
		Date start = new Date();
		List<Idp> bos;
		if (viewMode == IdpAction.VIEW_MODE_PLAIN) {
			List<?> groupByData = paging.executeQuery(sortParams, filterParams,
					groupParams, user);
			bos = filledData(filterParams, groupByData);
		} else {
			//use another sorting method to get proper objects
			if (sortParams != null && sortParams.getExtOrderBy() != null && !"".equals(sortParams.getExtOrderBy())) {
				bos = paging.executeQueryAll(filterParams, user, false);
			} else {
				bos = paging.executeQuery(sortParams, filterParams, user, false);
			}
		}
		Date end = new Date();
		log.info("findBos", "Page query in: "
				+ (end.getTime() - start.getTime()) + " ms.");
		return bos;
	}

	private List<Idp> filledData(FilterParams filterParams, List<?> groupByData) {
		List<Idp> list = new ArrayList<Idp>();
		if (null != groupByData) {
			List<String> queryStrings = new ArrayList<String>();
			for (Object object : groupByData) {
				Object[] array = (Object[]) object;
				String bssid = (String) array[0];
				Long domainId = (Long) array[1];
				queryStrings.add(bssid + String.valueOf(domainId));
			}
			if (!queryStrings.isEmpty()) {
				FilterParams filter;
				if (null != filterParams && null != filterParams.getWhere()) {
					String newWhere = filterParams.getWhere()
							+ " and ifMacAddress||owner.id in (:s"
							+ (filterParams.getBindings().length + 1) + ")";
					Object[] binds = filterParams.getBindings();
					Object[] newBinds = new Object[binds.length + 1];
					System.arraycopy(binds, 0, newBinds, 0, binds.length);
					newBinds[binds.length] = queryStrings;
					filter = new FilterParams(newWhere, newBinds);
				} else {
					filter = new FilterParams("ifMacAddress||owner.id",
							queryStrings);
				}
				List<Idp> bos = QueryUtil.executeQuery(Idp.class, null, filter);
				log.info("filledData", "find total count: " + bos.size()
						+ " for " + queryStrings.size() + " bssids");
				if (!bos.isEmpty()) {
					Map<String, List<Idp>> mapedList = new HashMap<String, List<Idp>>();
					for (Object object : bos) {
						Idp idp = (Idp) object;
						String key = idp.getIfMacAddress()
								+ String.valueOf(idp.getOwner().getId());
						if (null == mapedList.get(key)) {
							List<Idp> items = new ArrayList<Idp>();
							mapedList.put(key, items);
						}
						mapedList.get(key).add(idp);
					}
					for (String key : queryStrings) {
						List<Idp> items = mapedList.get(key);
						Idp newIdp = new Idp();
						for (Idp item : items) {
							/*
							 * just set the last id into the new one which used
							 * on the page
							 */
							String apName = item.getReportHostName() == null ? item
									.getReportNodeId()
									: item.getReportHostName();
							newIdp.setId(item.getId());
							newIdp.setIfMacAddress(item.getIfMacAddress());
							newIdp.setSsid(item.getSsid());
							newIdp.setChannel(item.getChannel());
							newIdp.setMapId(item.getMapId());
							newIdp.addHightestRSSIs(item.getRssi(), apName);
							newIdp.addLastReportedTime(item.getReportTime(),
									apName);
							newIdp.setReportNodeId(item.getReportNodeId());
							newIdp.setOwner(item.getOwner());
						}
						list.add(newIdp);
					}
				}
			}
			if (selectedIds != null) {
				for (Idp idp : list) {
					idp.setSelected(selectedIds.contains(idp.getId()));
				}
			}
		}
		return list;
	}

	@Override
	public Collection<JSONObject> getUpdates(int cacheId) throws Exception {
		Collection<JSONObject> updates = new Vector<JSONObject>();
		if (getCacheId() != cacheId) {
			// Invalid refresh request
			return updates;
		}
		List<Idp> newIdps = findBos();
		if (hmBos.size() != newIdps.size()) {
			// full refresh
			return refreshFromCache(newIdps);
		}
		if (viewMode == IdpAction.VIEW_MODE_PLAIN) {
			for (int i = 0; i < hmBos.size(); i++) {
				Idp idp = hmBos.get(i);
				Idp newIds = newIdps.get(i);
				if (!idp.getId().equals(newIds.getId())) {
					// full refresh
					return refreshFromCache(newIdps);
				}
			}
		} else {
			for (int i = 0; i < hmBos.size(); i++) {
				Idp idp = hmBos.get(i);
				Idp newIds = newIdps.get(i);
				if (!idp.getId().equals(newIds.getId())
						|| idp.isMitigated() != newIds.isMitigated()) {
					// full refresh
					return refreshFromCache(newIdps);
				}
			}
		}
		hmBos = newIdps;
		return updates;
	}

	@Deprecated
	public static void queryMapContainerInfo(List<Idp> idps) {
		if (null == idps || idps.isEmpty()) {
			return;
		}
		// get map id set from IDP list
		Set<Long> mapIds = new HashSet<Long>();
		for (Idp idp : idps) {
			if (null != idp.getMapId()) {
				mapIds.add(idp.getMapId());
			}
		}
		if (mapIds.isEmpty()) {
			return;
		}
		// query map name for the specify map id set
		List<?> mapInfo = QueryUtil.executeQuery(
				"select bo.id, bo.mapName from "
						+ MapContainerNode.class.getSimpleName() + " bo", null,
				new FilterParams("id", mapIds));
		// put them into a map
		Map<Long, String> mapInfoMap = new HashMap<Long, String>();
		for (Object object : mapInfo) {
			Object[] values = (Object[]) object;
			Long id = (Long) values[0];
			String mapName = (String) values[1];
			mapInfoMap.put(id, mapName);
		}
		// fill into IDP object
		for (Idp idp : idps) {
			if (null != idp.getMapId()) {
				idp.setMapName(mapInfoMap.get(idp.getMapId()));
			}
		}
	}

	public static void queryLocationInfo(HmDomain domain, List<Idp> rogues) {
		try {
			BoMgmt.getLocationTracking().findRogueRssi(domain, rogues);
		} catch (Exception e) {
			log.error("queryLocationInfo", "error,", e);
		}
	}
	
	public static void queryIdsInfo(List<Idp> idps) {
		if (null == idps || idps.isEmpty()) {
			return;
		}
		Idp idp = idps.get(0);
		if (idp.getIdpType() != BeCommunicationConstant.IDP_TYPE_ROGUE
				&& idp.getStationType() != BeCommunicationConstant.IDP_STATION_TYPE_AP) {
			return;// only query for Rogue AP list.
		}
		
		Map<Long, Idp> map = new HashMap<Long, Idp>(idps.size());
		StringBuffer sb = null;
		for (Idp obj : idps) {
			map.put(obj.getId(), obj);
			if (null == sb) {
				sb = new StringBuffer("'" + obj.getReportNodeId() + "'");
			} else {
				sb.append(",'").append(obj.getReportNodeId()).append("'");
			}
		}

		String query = "select a.id, b.mitigationmode from idp a , ids_policy b, hive_ap c, config_template d "
				+ "where a.reportnodeid = c.macaddress and b.id = d.ids_policy_id and c.template_id = d.id and a.reportnodeid in ("
				+ sb.toString() + ") and a.idptype = "
				+ BeCommunicationConstant.IDP_TYPE_ROGUE +" and a.stationtype = " + BeCommunicationConstant.IDP_STATION_TYPE_AP;
		List<?> list = QueryUtil.executeNativeQuery(query);

		for (Object object : list) {
			Object[] attributes = (Object[]) object;
			Long key = ((BigInteger)attributes[0]).longValue();
			Short mode = (Short)attributes[1];
			if (map.containsKey(key)) {
				map.get(key).setMode(mode);
			}
		}
	}
	
	public static void queryMitigateClientInfo(List<Idp> idps) {
		if (null == idps || idps.isEmpty()) {
			return;
		}
		Idp idp = idps.get(0);
		if (idp.getIdpType() != BeCommunicationConstant.IDP_TYPE_ROGUE
				&& idp.getStationType() != BeCommunicationConstant.IDP_STATION_TYPE_AP) {
			return;// only query for Rogue AP list.
		}
		Map<String, Idp> map = new HashMap<String, Idp>(idps.size());
		StringBuffer sb = null;
		for (Idp obj : idps) {
			map.put(obj.getIfMacAddress() + "|" + obj.getReportNodeId(), obj);
			if (null == sb) {
				sb = new StringBuffer("'" + obj.getIfMacAddress() + "'");
			} else {
				sb.append(",'").append(obj.getIfMacAddress()).append("'");
			}
		}

		String query = "select a.ifmacaddress, a.reportnodeid, count(b.parentbssid) from idp a "
				+ "join idp b on a.ifmacaddress =  b.parentbssid and a.reportnodeid = b.reportnodeid and a.ifmacaddress in ("
				+ sb.toString() + ") group by a.ifmacaddress, a.reportnodeid";
		List<?> list = QueryUtil.executeNativeQuery(query);

		for (Object object : list) {
			Object[] attributes = (Object[]) object;
			String key = String.valueOf(attributes[0]) + "|"
					+ String.valueOf(attributes[1]);
			long count = ((BigInteger) attributes[2]).longValue();
			if (null != map.get(key)) {
				map.get(key).setClientCount(count);
			}
		}
	}

	public static void queryReportedBssidInfo(List<Idp> idps) {
		if (null == idps || idps.isEmpty()) {
			return;
		}
		Map<String, Byte> map = new HashMap<String, Byte>(idps.size());
		for (Idp idp : idps) {
			map.put(idp.getReportNodeId(), idp.getIfIndex());
		}
		List<AhLatestXif> list = QueryUtil.executeQuery(AhLatestXif.class,
				null, new FilterParams("apMac", map.keySet()));
		Map<String, String> bssids = new HashMap<String, String>(idps.size());
		for (AhLatestXif xif : list) {
			bssids.put(xif.getApMac()+xif.getIfIndex(), xif.getBssid());
		}
		for (Idp idp : idps) {
			String bssid = bssids.get(idp.getReportNodeId()+idp.getIfIndex());
			if (null == bssid) {
				bssid = " - ";
			}
			idp.setReportedBssid(bssid);
		}
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		log.info("valueUnbound", "Unbound event: " + event.getName());
	}

}