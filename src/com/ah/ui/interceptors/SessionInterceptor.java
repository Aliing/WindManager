package com.ah.ui.interceptors;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.ah.be.search.PageIndex;
import com.ah.bo.admin.HmUser;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.monitor.MapSettingsAction;
import com.ah.ui.actions.teacherView.StudentRegisterAction;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class SessionInterceptor implements Interceptor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		// TODO Auto-generated method stub
		ActionContext ctx = ActionContext.getContext();  
        Map<String, Object> session = ctx.getSession();  
        Object action = invocation.getAction();
		
        HmUser userName = (HmUser) session.get(BaseAction.USER_CONTEXT);
        
        if(userName == null && !(action instanceof StudentRegisterAction)){
            HttpServletRequest request = (HttpServletRequest) invocation
                    .getInvocationContext().get(ServletActionContext.HTTP_REQUEST);
            if(PageIndex.needBuildIndex() && PageIndex.isLocalRequest(request)) {
                // do nothing, for page index
            } else {
                if (!(invocation.getProxy().getActionName().equals("mapUpload") 
                        && action instanceof MapSettingsAction)) {
                    // avoid the HTTP 302 for SWFUpload, Bug 
                    return "login";
                }
            }
        }
		return invocation.invoke();
	}

}
