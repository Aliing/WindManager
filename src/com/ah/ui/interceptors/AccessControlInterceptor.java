package com.ah.ui.interceptors;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAccessControl;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.admin.HmAccessControlAction;
import com.ah.util.HmProxyUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class AccessControlInterceptor implements Interceptor,QueryBo {

	private static final long serialVersionUID = 1L;

	@Override
	public void destroy() {
		//
	}

	@Override
	public void init() {
		//
	}

	private boolean validateHmAccessControl(HmUser userContext, String clientIp) {
		HmAccessControl acl;
		if (null == userContext) {
			// Only when user context is null, try to get the data from
			// database.
			// Note: the acl maybe reset by hm console, so cannot always use
			// the current acl which is in memory
			List<HmAccessControl> list = QueryUtil.executeQuery(HmAccessControl.class, null,
					null);
			if (!list.isEmpty()) {
				acl = HmAccessControlAction.CURRENT_ACL = QueryUtil.findBoById(HmAccessControl.class,
						list.get(0).getId(), this);
			} else {
				acl = HmAccessControlAction.CURRENT_ACL = null; // reset to null
			}
		} else {
			acl = HmAccessControlAction.CURRENT_ACL;
		}
		if (null != acl) {
			boolean allow = acl.isAllowAccess(clientIp);
			if (!allow) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = (HttpServletRequest) invocation
				.getInvocationContext().get(ServletActionContext.HTTP_REQUEST);
		// check hm has access control
		HmUser userContext = null;
		Object action = invocation.getAction();
		if (action instanceof BaseAction) {
			BaseAction myAction = (BaseAction) action;
			userContext = myAction.getUserContext();
		}
		String clientIp = HmProxyUtil.getClientIp(request);
		boolean allowed = validateHmAccessControl(userContext, clientIp);
		if (!allowed) {
			request.getSession().invalidate();
			return "deny";
		}
		return invocation.invoke();
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HmAccessControl) {
			HmAccessControl oneClass = (HmAccessControl) bo;
			if (oneClass.getIpAddresses() != null)
				oneClass.getIpAddresses().size();
		}
		return null;
	}

}