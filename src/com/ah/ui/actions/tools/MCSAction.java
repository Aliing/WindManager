package com.ah.ui.actions.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.admin.auth.AhAuthException;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.HmProxyUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */

public class MCSAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	private static final Tracer log = new Tracer(
			MCSAction.class.getSimpleName());

	@Override
	public String execute() {
		if (auth) {
			log.info("authenticate user: " + userName);
			try {
				userContext = HmBeAdminUtil.authenticate(userName, password);
			} catch (AhAuthException e) {
				userContext = null;
				log.info(e);
				request.getSession().invalidate();
			}
			if (userContext == null) {
				log.error(MgrUtil.getUserMessage(AUTH_FAILED));
			} else {
				setSessionUserContext(userContext);
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.admin.login",userContext.getUserName()));
				userContext.setUserIpAddress(HmProxyUtil.getClientIp(request));
				int sessionExpiration = 1 * 60; // Seconds
				userContext.setSessionExpiration(sessionExpiration);
				request.getSession().setMaxInactiveInterval(sessionExpiration);
			}
		}
		jsonObject = new JSONObject();
		try {
			if (userContext == null) {
				jsonObject.put("sc", false);
				log.info("User session not active.");
			} else {
				jsonObject.put("sc", true);
				log.info("Operation: " + op);
				if (op != null) {
					processOperation();
				}
			}
		} catch (JSONException e) {
			log.info(e);
		}
		try {
			PrintWriter out = response.getWriter();
			out.println(getJSONString());
			out.flush();
		} catch (IOException e) {
			log.info(e);
		}
		return null;
	}

	private void processOperation() {
		try {
			if (op.equals("lgo")) {
				log.info("Logout");
				request.getSession().invalidate();
			} else if (op.equals("aps")) {
				List<HiveAp> aps = QueryUtil.executeQuery(HiveAp.class, null,
						null, userContext);
				log.info("# APs: " + aps.size());
				Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
				for (HiveAp ap : aps) {
					JSONObject jsonAp = new JSONObject();
					jsonAp.put("id", ap.getId());
					jsonAp.put("hst", ap.getHostName());
					jsonNodes.add(jsonAp);
				}
				jsonObject.put("aps", jsonNodes);
			}
		} catch (JSONException e) {
			log.info(e);
		}
	}

	private String userName, password, op;
	private boolean auth;

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public void setOp(String op) {
		this.op = op;
	}

}