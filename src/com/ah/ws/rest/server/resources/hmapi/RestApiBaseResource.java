package com.ah.ws.rest.server.resources.hmapi;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;

import com.ah.be.rest.server.resources.BaseResource;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.ws.rest.models.hmapi.RestModelUtil;
import com.ah.ws.rest.server.auth.exception.ApiException;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;

public class RestApiBaseResource extends BaseResource {
	public HmDomain hmDomain;
	private String apiUserName;
	public String ownerSql;

	public void generateAuditLog(HttpServletRequest request, short status) {
		try {
			short arg_Status = status;
			String baseUrl = request.getRequestURI().toLowerCase();
			String url = baseUrl.substring(baseUrl.indexOf("api") - 1);
			String arg_Comment = "Call HM API:" + url;
			HmUser userContext = new HmUser();
			userContext.setUserName(apiUserName);
			userContext.setOwner(hmDomain);
			generateAuditLog(request, userContext, arg_Status, arg_Comment);
		} catch (Exception e) {
		}
	}

	public void prepare(HttpServletRequest request, String vhmName,
			boolean VhmIsRequired) throws ApiException {
		hmDomain = new HmDomain();
		String authentication = request
				.getHeader(ContainerRequest.AUTHORIZATION);
		if (null != authentication) {
			authentication = authentication.substring("Basic ".length());
			String[] values = new String(Base64.base64Decode(authentication))
					.split(":");
			apiUserName = values[0];
			List<HMServicesSettings> list = QueryUtil.executeQuery(
					HMServicesSettings.class, null, new FilterParams(
							"apiUserName", apiUserName));
			if (!list.isEmpty()) {
				hmDomain = list.get(0).getOwner();
			}
		}
		ownerSql = getOwnerQuery(vhmName, VhmIsRequired);
	}

	public boolean isSuperDomain() {
		if (null == hmDomain) {
			return false;
		}
		return hmDomain.isHomeDomain();
	}

	private String getOwnerQuery(String vhmName, boolean isRequired)
			throws ApiException {
		boolean vhmIsNull = StringUtils.isBlank(vhmName);
		StringBuffer query = new StringBuffer("");
		if (vhmIsNull) {
			if (!isSuperDomain()) {
				query.append(" and owner='" + hmDomain.getId() + "'");
			} else if (isRequired) {
				throw new ApiException(Status.BAD_REQUEST,
						MgrUtil.getUserMessage("error.rest.required.parameter",
								"vhmName"), "vhmName");
			}
		} else {
			long domainId = 0;
			if (isSuperDomain()) {
				HmDomain hmdomain = QueryUtil.findBoByAttribute(HmDomain.class,
						"domainName", vhmName);
				if (null == hmdomain) {
					throw new ApiException(Status.BAD_REQUEST,
							MgrUtil.getUserMessage("error.rest.vhm.notExist",
									vhmName), "vhmName");
				}
				domainId = hmdomain.getId();
			} else {
				String domainName = hmDomain.getDomainName();
				if (!domainName.equals(vhmName)) {
					throw new ApiException(Status.UNAUTHORIZED,
							MgrUtil.getUserMessage(
									"error.rest.required.permission", vhmName),
							"vhmName");
				}
				domainId = hmDomain.getId();
			}
			query.append(" and owner='" + domainId + "'");
		}
		return query.toString();
	}

	public String getSortQuery(String orderBy, String ending,
			boolean deviceOrder) {
		StringBuffer query = new StringBuffer(" order by ");
		if (deviceOrder) {
			query.append("manageStatus,");
		}
		if (StringUtils.isBlank(orderBy)) {
			orderBy = "id";
		}
		boolean orderByIp = false;
		String lowerStr = orderBy.toLowerCase();
		if ("ipaddress".equals(lowerStr) || "capwapclientip".equals(lowerStr)
				|| "netmask".equals(lowerStr) || "gateway".equals(lowerStr)) {
			orderByIp = true;
		}
		if (orderByIp) {
			query.append("inet(" + orderBy + ")");
		} else {
			query.append(orderBy);
		}
		if (!StringUtils.isBlank(ending)) {
			ending = ending.toLowerCase();
			if (ending.equals("desc")) {
				query.append(" desc");
			}
		}
		return query.toString();
	}

	public static String getInQuery(List<?> list) {
		if (null == list || list.isEmpty()) {
			return "''";
		}
		StringBuffer query = new StringBuffer("");
		int index = 0;
		for (Object obj : list) {
			if (index != 0) {
				query.append(",");
			}
			query.append("'" + obj + "'");
			index++;
		}
		return query.toString();
	}

	public Response getOkResponse(HttpServletRequest request, Object ob) {
		// add Audit Log
		generateAuditLog(request, HmAuditLog.STATUS_SUCCESS);
		return Response.status(Status.OK).entity(ob).build();
	}

	public Response getExResponse(HttpServletRequest request, ApiException ex) {
		// add Audit Log
		generateAuditLog(request, HmAuditLog.STATUS_FAILURE);
		return Response.status(ex.getStatus())
				.entity(RestModelUtil.exceptionToErrorMsgModel(ex)).build();
	}

	public int getPageIndex(int maxResult, int offset) {
		int pageIndex = 0;
		if (offset < 1) {
			offset = 1;
		}
		maxResult = getMaxResult(maxResult);
		pageIndex = (offset - 1) * maxResult;
		return pageIndex;
	}

	public int getMaxResult(int maxResult) {
		if (maxResult < 1) {
			maxResult = Paging.MAX_RESULTS;
		}
		return maxResult;
	}

	public String getPageQuery(int maxResult, int offset) {
		StringBuffer pageSql = new StringBuffer("");
		if (maxResult > 0) {
			pageSql.append(" limit ").append(maxResult);
		} else {
			maxResult = 0;
		}
		if (offset > 0) {
			pageSql.append(" offset ").append((offset - 1) * maxResult);
		}
		return pageSql.toString();
	}
}
