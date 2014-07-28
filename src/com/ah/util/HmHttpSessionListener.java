package com.ah.util;

/*
 * Initialize Hibernate at startup
 *
 * @author Chris Scheers
 */


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.dashboard.AhDashboardLayout;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.monitor.DashboardAction;


public final class HmHttpSessionListener implements HttpSessionListener {

	private static final Tracer log = new Tracer(HmHttpSessionListener.class.getSimpleName());

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		removeUnusedMonitorPage(arg0.getSession());
		
	}

	@SuppressWarnings("unchecked")
	private void removeUnusedMonitorPage(HttpSession session){
		try {
			Map<String,String> mon = (Map<String,String>)session.getAttribute(DashboardAction.MONITOR_TAB_ID_USERAPPLICATION);
			if(mon==null || mon.isEmpty()) {
				return;
			}
			List<Long> ids = new ArrayList<Long>();
			for(String key : mon.keySet()){
				ids.add(Long.parseLong(key));
			}
			if(!ids.isEmpty()) {
				
				List<Long> layoutIds = (List<Long>)QueryUtil.executeQuery("select id from "  + AhDashboardLayout.class.getSimpleName()
						, null, new FilterParams("dashboard.id",ids));
				if(!layoutIds.isEmpty()) {
					QueryUtil.removeBos(AhDashboardWidget.class, new FilterParams("daLayout.id",layoutIds));
					QueryUtil.removeBos(AhDashboardLayout.class, layoutIds);
				}
				QueryUtil.removeBos(AhDashboard.class, ids);
			}
			DashboardAction.clearAppAp(ids);
		}catch (Exception e) {
			log.error(e);
		}
	}
	

}