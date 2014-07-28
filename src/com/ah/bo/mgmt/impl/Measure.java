package com.ah.bo.mgmt.impl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.ah.bo.admin.HmUser;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.Tracer;

@Path("find")
public class Measure {
	private static final Tracer log = new Tracer(Measure.class, "tracerlog");

	@Context
	HttpServletRequest request;

	@Path("aps")
	@GET
	@Produces("text/plain")
	public String getAPs() {
		log.info_ln("Request: " + request);
		if (request != null) {
			HmUser userContext = (HmUser) request.getSession().getAttribute(
					SessionKeys.USER_CONTEXT);
			if (userContext != null) {
				log.info_ln("User context: " + userContext.getUserName());
			}
		}
		return "...";
	}
}
