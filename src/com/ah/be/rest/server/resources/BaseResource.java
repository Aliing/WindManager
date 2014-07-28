package com.ah.be.rest.server.resources;

import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Application;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.rest.server.models.BaseModel;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.util.HmProxyUtil;
import com.thoughtworks.xstream.XStream;

public class BaseResource extends Application{

	protected HmUser getUserContext(String userName){
		List<HmUser> allUsers = QueryUtil.executeQuery(HmUser.class,
				new SortParams("id"),
				new FilterParams("lower(userName)=:s1 and apiAuthFlag=:s2", new Object[]{userName.toLowerCase(), true}));
		if (!allUsers.isEmpty()) {
			return allUsers.get(0);
		}
		return null;
	}

	protected String returnPresentation(Class<? extends BaseModel> modelClass, BaseModel baseModel){
		XStream xStream = new XStream();
		xStream.processAnnotations(modelClass);
		return xStream.toXML(baseModel);
	}

	public void generateAuditLog(HttpServletRequest request, HmUser userContext, short arg_Status, String arg_Comment) {
		HmAuditLog log = new HmAuditLog();
		log.setStatus(arg_Status);
		log.setOpeationComment(arg_Comment);
		log.setHostIP(null == request ? "127.0.0.1" : HmProxyUtil
				.getClientIp(request));
		try {
			HmDomain domain = null;
			if (null != userContext) {
				log.setUserOwner(userContext.getUserName());
				long domainId = QueryUtil.getDependentDomainFilter(userContext);
				domain = QueryUtil.findBoById(HmDomain.class, domainId);
				log.setOwner(domain);
			}
			log.setLogTimeStamp(System.currentTimeMillis());
			log.setLogTimeZone(domain != null ? domain.getTimeZoneString()
					: TimeZone.getDefault().getID());

			BeLogTools.tracerLog(BeLogTools.INFO, "[" + log.getHostIP() + " "
					+ log.getOwner() + "." + log.getUserOwner() + "]" + " "
					+ arg_Comment + ":" + arg_Status);

			QueryUtil.createBo(log);
		} catch (Exception e) {
		}
	}
}
