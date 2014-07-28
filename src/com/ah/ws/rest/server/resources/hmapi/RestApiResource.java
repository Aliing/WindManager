package com.ah.ws.rest.server.resources.hmapi;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.DBOperationUtil;
import com.ah.be.rest.server.business.IRestConstants;
import com.ah.be.rest.server.resources.RestMACAuthResource;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.impl.PagingImpl;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.ws.rest.models.hmapi.RestModelUtil;
import com.ah.ws.rest.models.hmapi.RestModelUtil.LoadQueryBo;
import com.ah.ws.rest.server.auth.exception.ApiException;

@Path("/v1")
public class RestApiResource extends RestApiBaseResource implements
		IRestConstants {
	private static final Tracer log = new Tracer(
			RestMACAuthResource.class.getSimpleName());

	@GET
	@Path("/devices")
	@Produces(MediaType.APPLICATION_JSON)
	public Response queryDeviceList(@Context HttpServletRequest request,
			@QueryParam("vhmName") String vhmName,
			@QueryParam("mapName") String mapName,
			@QueryParam("connect") String connect,
			@QueryParam("page.page") int page,
			@QueryParam("page.size") int pageSize,
			@QueryParam("page.sort") String orderBy,
			@QueryParam("page.sort.dir") String dir,
			@QueryParam("deviceIp") String deviceIp,
			@QueryParam("deviceMac") String deviceMac) {
		boolean mapNameIsNUll = StringUtils.isBlank(mapName);
		boolean connectIsNull = StringUtils.isBlank(connect);
		boolean IpIsNull = StringUtils.isBlank(deviceIp);
		boolean macIsNull = StringUtils.isBlank(deviceMac);
		try {
			super.prepare(request, vhmName, false);
			StringBuffer query = new StringBuffer("from HiveAp where 1=1");
			if (!mapNameIsNUll) {
				List<Long> mapIdList = RestModelUtil.getAllChildrenMapName(
						ownerSql, mapName);
				query.append(getMapIdSql(mapIdList));
			}
			if (!connectIsNull) {
				query.append(" and connected='" + connect + "'");
			}
			if (!IpIsNull) {
				query.append(" and ipAddress='" + deviceIp + "'");
			}
			if (!macIsNull) {
				query.append(" and macAddress='" + deviceMac + "'");
			}
			query.append(ownerSql);
			query.append(getSortQuery(orderBy, dir, true));
			List<?> queryList = QueryUtil.executeQuery(query.toString(),
					getMaxResult(pageSize), getPageIndex(pageSize, page));
			List<HiveAp> list = (List<HiveAp>) queryList;
			return getOkResponse(request,
					RestModelUtil.HiveApsToDeviceModels(list));
		} catch (ApiException ex) {
			return getExResponse(request, ex);
		} catch (Exception e) {
			log.error("queryDeviceList error:", e);
			return getExResponse(request, new ApiException(e));
		}
	}

	@GET
	@Path("/devices/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceCount(@Context HttpServletRequest request,
			@QueryParam("vhmName") String vhmName,
			@QueryParam("mapName") String mapName,
			@QueryParam("connect") String connect) {
		boolean mapNameIsNUll = StringUtils.isBlank(mapName);
		boolean connectIsNull = StringUtils.isBlank(connect);
		long count = 0;
		try {
			super.prepare(request, vhmName, false);
			StringBuffer query = new StringBuffer(
					"select count(id) from HiveAp where 1=1");
			if (!mapNameIsNUll) {
				List<Long> mapIdList = RestModelUtil.getAllChildrenMapName(
						ownerSql, mapName);
				query.append(getMapIdSql(mapIdList));
			}

			if (!connectIsNull) {
				query.append(" and connected='" + connect + "'");
			}
			query.append(ownerSql);
			List<?> list = QueryUtil.executeQuery(query.toString(), 1);
			if (!list.isEmpty()) {
				count = (Long) list.get(0);
			}
			return getOkResponse(request, count);
		} catch (ApiException ex) {
			return getExResponse(request, ex);
		} catch (Exception e) {
			log.error("queryDeviceCount error:", e);
			return getExResponse(request, new ApiException(e));
		}
	}

	@GET
	@Path("/device")
	@Produces(MediaType.APPLICATION_JSON)
	public Response queryDeviceObject(@Context HttpServletRequest request,
			@QueryParam("deviceId") String deviceId,
			@QueryParam("deviceName") String deviceName,
			@QueryParam("deviceMac") String deviceMac,
			@QueryParam("deviceIp") String deviceIp, @QueryParam("q") String q) {
		boolean deviceIdIsNull = StringUtils.isBlank(deviceId);
		boolean deviceNameIsNull = StringUtils.isBlank(deviceName);
		boolean deviceMacIsNull = StringUtils.isBlank(deviceMac);
		boolean deviceIpIsNull = StringUtils.isBlank(deviceIp);
		boolean qIsNull = StringUtils.isBlank(q);
		int index = 0;
		String errorParams = "";
		try {
			super.prepare(request, null, false);
			StringBuffer query = new StringBuffer("from HiveAp where 1=1");
			if (!deviceIdIsNull) {
				query.append(" and serialNumber='" + deviceId + "'");
				index++;
				errorParams = "deviceId";
			}
			if (!deviceNameIsNull) {
				query.append(" and hostName='" + deviceName + "'");
				index++;
				errorParams = "deviceName";
			}
			if (!deviceMacIsNull) {
				query.append(" and macAddress='" + deviceMac + "'");
				index++;
				errorParams = "deviceMac";
			}
			if (!deviceIpIsNull) {
				query.append(" and ipAddress='" + deviceIp + "'");
				index++;
				errorParams = "deviceIp";
			}
			if (!qIsNull) {
				query.append(getQsql(q));
				index++;
				errorParams = "q";
			}
			checkDeviceParam(index, true);
			query.append(ownerSql);
			List<?> queryList = QueryUtil.executeQuery(query.toString(),
					PagingImpl.MAX_RESULTS);
			List<HiveAp> list = (List<HiveAp>) queryList;
			if (list.size() > 1) {
				throw new ApiException(Status.BAD_REQUEST,
						MgrUtil.getUserMessage("error.rest.device.dataCount"),
						errorParams);
			}
			return getOkResponse(request,
					RestModelUtil.HiveApsToDeviceModels(list));
		} catch (ApiException ex) {
			return getExResponse(request, ex);
		} catch (Exception e) {
			log.error("queryDeviceObject error:", e);
			return getExResponse(request, new ApiException(e));
		}
	}

	@GET
	@Path("/device/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response queryDeviceObject(@Context HttpServletRequest request,
			@PathParam("identifier") String identifier) {
		try {
			super.prepare(request, null, false);
			StringBuffer query = new StringBuffer("from HiveAp where 1=1");
			if (!StringUtils.isBlank(identifier)) {
				query.append(" and macAddress='" + identifier + "'");
			}
			query.append(ownerSql);
			List<?> queryList = QueryUtil.executeQuery(query.toString(),
					PagingImpl.MAX_RESULTS);
			List<HiveAp> list = (List<HiveAp>) queryList;
			if (list.size() > 1) {
				throw new ApiException(Status.BAD_REQUEST,
						MgrUtil.getUserMessage("error.rest.device.dataCount"),
						identifier);
			}
			return getOkResponse(request,
					RestModelUtil.HiveApsToDeviceModels(list));
		} catch (ApiException ex) {
			return getExResponse(request, ex);
		} catch (Exception e) {
			log.error("queryDeviceObject by identifier error:", e);
			return getExResponse(request, new ApiException(e));
		}
	}

	@GET
	@Path("/clients")
	@Produces(MediaType.APPLICATION_JSON)
	public Response queryClientList(@Context HttpServletRequest request,
			@QueryParam("deviceId") String deviceId,
			@QueryParam("deviceName") String deviceName,
			@QueryParam("deviceMac") String deviceMac,
			@QueryParam("deviceIp") String deviceIp, @QueryParam("q") String q,
			@QueryParam("page.page") int page,
			@QueryParam("page.size") int pageSize,
			@QueryParam("page.sort") String orderBy,
			@QueryParam("page.sort.dir") String dir) {
		boolean deviceMacIsNull = StringUtils.isBlank(deviceMac);
		int index = 0;
		try {
			super.prepare(request, null, false);
			StringBuffer query = new StringBuffer(
					"select * from ah_clientsession where 1=1");
			if (!deviceMacIsNull) {
				query.append(" and apMac='" + deviceMac + "'");
				index++;
			}
			String macSql = getMacSql(deviceId, deviceIp, deviceName, q, null,
					true, index);
			if (null == macSql) {
				return getOkResponse(request, new ArrayList<Object>());
			}
			query.append(macSql);
			query.append(ownerSql);
			query.append(getSortQuery(orderBy, dir, false));
			query.append(getPageQuery(pageSize, page));
			List<?> queryList = DBOperationUtil.executeQueryBos(query
					.toString());
			List<AhClientSession> list = (List<AhClientSession>) queryList;
			return getOkResponse(request,
					RestModelUtil.clientsToClientModels(list));
		} catch (ApiException ex) {
			return getExResponse(request, ex);
		} catch (Exception e) {
			log.error("queryClientList error:", e);
			return getExResponse(request, new ApiException(e));
		}
	}

	@GET
	@Path("/clients/stats")
	@Produces(MediaType.APPLICATION_JSON)
	public Response queryClientStats(@Context HttpServletRequest request,
			@QueryParam("vhmName") String vhmName,
			@QueryParam("deviceId") String deviceId,
			@QueryParam("deviceName") String deviceName,
			@QueryParam("deviceMac") String deviceMac,
			@QueryParam("deviceIp") String deviceIp, @QueryParam("q") String q,
			@QueryParam("mapName") String mapName,
			@QueryParam("radioType") String radioType,
			@QueryParam("ssid") String ssid,
			@QueryParam("userProfile") String userProfile,
			@QueryParam("from") String from, @QueryParam("to") String to) {
		boolean deviceMacIsNull = StringUtils.isBlank(deviceMac);
		boolean radioTypeIsNUll = StringUtils.isBlank(radioType);
		boolean ssidIsNUll = StringUtils.isBlank(ssid);
		boolean userProfileIsNUll = StringUtils.isBlank(userProfile);
		boolean fromIsNUll = StringUtils.isBlank(from);
		boolean toIsNUll = StringUtils.isBlank(to);
		try {
			super.prepare(request, vhmName, true);
			if (fromIsNUll) {
				throw new ApiException(Status.BAD_REQUEST,
						MgrUtil.getUserMessage("error.rest.required.parameter",
								"from"), "from");
			}
			StringBuffer timeRangeQuery = new StringBuffer("");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssX");
			String errorParams = "";
			long timeStart = 0;
			long timeEnd = 0;
			long timeRange = 0;
			try {
				errorParams = "from";
				from = from.replace(" ", "+");
				timeStart = sdf.parse(from).getTime();
				timeRangeQuery.append(" and timeStamp>='" + timeStart + "'");
				if (!toIsNUll) {
					errorParams = "to";
					to = to.replace(" ", "+");
					timeEnd = sdf.parse(to).getTime();
					timeRangeQuery.append(" and timeStamp<='" + timeEnd + "'");
				} else {
					timeEnd = System.currentTimeMillis();
				}
			} catch (Exception e) {
				throw new ApiException(Status.BAD_REQUEST, e.getMessage(),
						errorParams);
			}
			timeRange = timeEnd - timeStart;
			String queryTable = getQueryClientTable(timeRange);
			String ssidFieldName = "ssid";
			if (queryTable.equals("hm_client_stats")) {
				ssidFieldName = "ssidname";
			}
			StringBuffer query = new StringBuffer(" from " + queryTable
					+ " where 1=1");
			query.append(timeRangeQuery.toString());
			int index = 0;
			if (!deviceMacIsNull) {
				query.append(" and apMac='" + deviceMac + "'");
				index++;
			}
			String macSql = getMacSql(deviceId, deviceIp, deviceName, q,
					mapName, false, index);
			if (null == macSql) {
				return getOkResponse(request, new ArrayList<Object>());
			}
			query.append(macSql);
			int radioTypeValue = 100;
			if (!radioTypeIsNUll) {
				radioType = radioType.toUpperCase().trim();
				if (!"2.4G".equals(radioType) && !"5G".equals(radioType)) {
					throw new ApiException(
							Status.BAD_REQUEST,
							MgrUtil.getUserMessage("error.rest.parameter.radioType"),
							"radioType");
				}
				radioTypeValue = AhClientSession
						.getclientRadioTypeByString(radioType);
				query.append(" and radiotype=" + radioTypeValue + "");
			}
			if (!ssidIsNUll) {
				query.append(" and " + ssidFieldName + "='" + ssid + "'");
			}
			if (!userProfileIsNUll) {
				query.append(" and userprofilename='" + userProfile + "'");
			}
			query.append(ownerSql);
			// Concurrent client counters
			String queryField = "max(totalCount)";
			if (radioTypeValue == AhInterfaceStats.RADIOTYPE_24G) {
				queryField = "max(client24count)";
			} else if (radioTypeValue == AhInterfaceStats.RADIOTYPE_5G) {
				queryField = "max(client5count)";
			}
			StringBuffer maxCountQuery = new StringBuffer("select "
					+ queryField + " from " + getQueryMaxCountTable(timeRange)
					+ " where 1=1");
			maxCountQuery.append(timeRangeQuery.toString());
			maxCountQuery.append(ownerSql);
			JSONArray array = getJSONObj(query, ssidFieldName, maxCountQuery);
			if (null == array) {
				return getOkResponse(request, new ArrayList<Object>());
			}
			return getOkResponse(request, array.toString());
		} catch (ApiException ex) {
			return getExResponse(request, ex);
		} catch (Exception e) {
			log.error("queryClientStats error:", e);
			return getExResponse(request, new ApiException(e));
		}
	}

	@GET
	@Path("/locations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response queryLocations(@Context HttpServletRequest request,
			@QueryParam("vhmName") String vhmName) {
		try {
			super.prepare(request, vhmName, true);
			StringBuffer query = new StringBuffer(
					"from MapContainerNode where 1=1");
			query.append(ownerSql);
			List<?> queryList = QueryUtil.executeQuery(query.toString(), null,
					null, null, new LoadQueryBo());
			List<MapContainerNode> list = (List<MapContainerNode>) queryList;
			return getOkResponse(request,
					RestModelUtil.mapNodesToMapNodeModels(list));
		} catch (ApiException ex) {
			return getExResponse(request, ex);
		} catch (Exception e) {
			log.error("queryLocations error:", e);
			return getExResponse(request, new ApiException(e));
		}
	}

	private JSONArray getJSONObj(StringBuffer query, String ssidFieldName,
			StringBuffer maxQuery) {
		StringBuffer queryOsType = new StringBuffer(query.toString());
		queryOsType.insert(0, "select count(distinct clientmac) ,osname ");
		queryOsType.append(" group by osname");
		List<?> OsTypeList = QueryUtil.executeNativeQuery(queryOsType
				.toString());
		Map<String, Integer> OSSatsMap = new LinkedHashMap<String, Integer>();
		for (Object obj : OsTypeList) {
			Object[] objects = (Object[]) obj;
			BigInteger value = (BigInteger) objects[0];
			OSSatsMap.put(getFormatkey(objects[1]), value.intValue());
		}
		StringBuffer queryRadioType = new StringBuffer(query.toString());
		queryRadioType
				.insert(0, "select count(distinct clientmac) ,radiotype ");
		queryRadioType.append(" group by radiotype");
		List<?> RadioTypeList = QueryUtil.executeNativeQuery(queryRadioType
				.toString());
		Map<String, Integer> radioSatsMap = new LinkedHashMap<String, Integer>();
		for (Object obj : RadioTypeList) {
			Object[] objects = (Object[]) obj;
			int key = (int) objects[1];
			BigInteger value = (BigInteger) objects[0];
			String radioType = "";
			if (key == AhInterfaceStats.RADIOTYPE_24G) {
				radioType = "2.4G";
			} else if (key == AhInterfaceStats.RADIOTYPE_5G) {
				radioType = "5G";
			} else {
				continue;
			}
			radioSatsMap.put(radioType, value.intValue());
		}
		StringBuffer queryUserProfile = new StringBuffer(query.toString());
		queryUserProfile.insert(0,
				"select count(distinct clientmac) ,userprofilename ");
		queryUserProfile.append(" group by userprofilename");
		List<?> UserProfileList = QueryUtil.executeNativeQuery(queryUserProfile
				.toString());
		Map<String, Integer> userProfileSatsMap = new LinkedHashMap<String, Integer>();
		for (Object obj : UserProfileList) {
			Object[] objects = (Object[]) obj;
			BigInteger value = (BigInteger) objects[0];
			userProfileSatsMap.put(getFormatkey(objects[1]), value.intValue());
		}
		StringBuffer querySSIDType = new StringBuffer(query.toString());
		querySSIDType.insert(0, "select count(distinct clientmac) , "
				+ ssidFieldName);
		querySSIDType.append(" group by " + ssidFieldName);
		List<?> SSIDTypeList = QueryUtil.executeNativeQuery(querySSIDType
				.toString());
		Map<String, Integer> SSIDSatsMap = new LinkedHashMap<String, Integer>();
		for (Object obj : SSIDTypeList) {
			Object[] objects = (Object[]) obj;
			BigInteger value = (BigInteger) objects[0];
			SSIDSatsMap.put(getFormatkey(objects[1]), value.intValue());
		}
		List<?> maxCountList = QueryUtil.executeNativeQuery(
				maxQuery.toString(), 1);
		int maxCount = 0;
		if (null != maxCountList.get(0)) {
			maxCount = Integer.parseInt(String.valueOf(maxCountList.get(0)));
		}
		Map<String, Integer> counterSatsMap = new LinkedHashMap<String, Integer>();
		counterSatsMap.put("max", maxCount);
		Map<String, Map<String, Integer>> newSatsMap = new LinkedHashMap<String, Map<String, Integer>>();
		newSatsMap.put("osType", OSSatsMap);
		newSatsMap.put("radioType", radioSatsMap);
		newSatsMap.put("ssid", SSIDSatsMap);
		newSatsMap.put("userProfile", userProfileSatsMap);
		newSatsMap.put("clientCounters", counterSatsMap);
		JSONArray array = JSONArray.fromObject(newSatsMap);
		return array;
	}

	private void checkDeviceParam(int parametersNum, boolean isRequired)
			throws ApiException {
		boolean hasEx = false;
		String message = "";
		if (parametersNum == 0 && isRequired) {
			message = MgrUtil.getUserMessage("error.rest.required.parameter",
					"device parameters");
			hasEx = true;
		} else if (parametersNum > 1) {
			message = MgrUtil.getUserMessage("error.rest.parameterNum");
			hasEx = true;
		}
		if (hasEx) {
			throw new ApiException(Status.BAD_REQUEST, message,
					"device parameters");
		}
	}

	private String getMacSql(String deviceId, String deviceIp, String apName,
			String q, String mapName, boolean paramIsRequired, int index)
			throws ApiException {
		boolean deviceIdIsNull = StringUtils.isBlank(deviceId);
		boolean deviceIpIsNull = StringUtils.isBlank(deviceIp);
		boolean apNameIsNull = StringUtils.isBlank(apName);
		boolean qIsNull = StringUtils.isBlank(q);
		boolean mapNameIsNUll = StringUtils.isBlank(mapName);
		String sql = "";
		StringBuffer query = new StringBuffer(
				"select macAddress from HiveAp where 1=1 ");
		boolean queryAps = false;
		if (!qIsNull) {
			query.append(getQsql(q));
			queryAps = true;
			index++;
		}
		if (!deviceIdIsNull) {
			query.append(" and serialNumber='" + deviceId + "'");
			queryAps = true;
			index++;
		}
		if (!deviceIpIsNull) {
			query.append(" and ipAddress='" + deviceIp + "'");
			queryAps = true;
			index++;
		}
		if (!apNameIsNull) {
			query.append(" and hostName='" + apName + "'");
			queryAps = true;
			index++;
		}
		checkDeviceParam(index, paramIsRequired);
		if (!mapNameIsNUll) {
			List<Long> mapIdList = RestModelUtil.getAllChildrenMapName(
					ownerSql, mapName);
			query.append(getMapIdSql(mapIdList));
			queryAps = true;
		}
		if (!queryAps) {
			return sql;
		}
		query.append(ownerSql);
		List<?> list = QueryUtil.executeQuery(query.toString(),
				PagingImpl.MAX_RESULTS);
		if (list.isEmpty()) {
			return null;
		}
		if (list.size() > 1) {
			sql = " and apMac in(" + getInQuery(list) + ")";
		} else {
			sql = " and apMac=" + getInQuery(list);
		}
		return sql;
	}

	private String getQsql(String q) {
		String sql = " and (serialNumber like '%" + q
				+ "%' or hostName like '%" + q + "%' or macAddress like '%" + q
				+ "%' or ipAddress like '%" + q + "%')";
		return sql;
	}

	private String getMapIdSql(List<Long> mapIdList) {
		String sql = "";
		if (null == mapIdList) {
			return sql;
		}
		if (mapIdList.size() > 1) {
			sql = " and mapContainer.id in(" + getInQuery(mapIdList) + ")";
		} else {
			sql = " and mapContainer.id=" + getInQuery(mapIdList);
		}
		return sql;
	}

	private String getQueryClientTable(long timeRange) {
		String tableName = "";
		if (timeRange <= 3600000 * 2) {
			tableName = "hm_client_stats";
		} else if (timeRange <= 3600000L * 24 * 2) {
			tableName = "hm_repo_client_data_hour";
		} else if (timeRange <= 3600000L * 24 * 35) {
			tableName = "hm_repo_client_data_date";
		} else {
			tableName = "hm_repo_client_data_week";
		}
		return tableName;
	}

	private String getQueryMaxCountTable(long timeRange) {
		String tableName = "";
		if (timeRange <= 3600000 * 2) {
			tableName = "max_clients_count";
		} else if (timeRange <= 3600000L * 24 * 2) {
			tableName = "hm_repo_client_count_hour";
		} else if (timeRange <= 3600000L * 24 * 35) {
			tableName = "hm_repo_client_count_date";
		} else {
			tableName = "hm_repo_client_count_week";
		}
		return tableName;
	}

	private String getFormatkey(Object obj) {
		if (null == obj || StringUtils.isBlank(obj.toString())) {
			return "";
		}
		return String.valueOf(obj);
	}
}
