package com.ah.ui.actions.monitor;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSessionBindingEvent;

import org.json.JSONObject;

import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.PagingCache;
import com.ah.bo.performance.AhClientSession;
import com.ah.util.Tracer;

/**
 * Paging cache management class for client list feature
 *@filename		ClientPagingCache.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-11-28 10:17:57
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
public class ClientPagingCache extends PagingCache<AhClientSession> {

	private static final Tracer	log	= new Tracer(ClientPagingCache.class.getSimpleName());

	public ClientPagingCache(HmUser user) {
		super(AhClientSession.class, user);
	}

	@Override
	public Collection<JSONObject> getUpdates(int cacheId) throws Exception {
		Collection<JSONObject> updates = new Vector<JSONObject>();
		if (getCacheId() != cacheId) {
			// Invalid refresh request
			return updates;
		}
		List<AhClientSession> clientList = findBos();
		if (hmBos.size() != clientList.size()) {
			// full refresh
			return refreshFromCache(clientList);
		}
		for (int i = 0; i < hmBos.size(); i++) {
			AhClientSession client = hmBos.get(i);
			AhClientSession newClient = clientList.get(i);

			// need refresh page when some attribute changed.
			if (!client.getId().equals(newClient.getId())
					|| client.getConnectstate() != newClient.getConnectstate()
					|| !client.getClientIP().equals(newClient.getClientIP())) {
				// full refresh
				return refreshFromCache(clientList);
			}
		}

		hmBos = clientList;
		return updates;
	}

	public void valueUnbound(HttpSessionBindingEvent arg0) {
		log.info("valueUnbound", "Unbound event: " + arg0.getName());
	}

}