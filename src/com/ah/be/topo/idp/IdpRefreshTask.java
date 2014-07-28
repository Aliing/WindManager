package com.ah.be.topo.idp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class IdpRefreshTask implements Runnable {

	private static final Tracer log = new Tracer(IdpRefreshTask.class
			.getSimpleName());

	private Map<Long, Integer> waitedTime;

	public IdpRefreshTask() {
		waitedTime = new HashMap<Long, Integer>();
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		idpRefresh();
	}

	private void idpRefresh() {
		try {
			long start = System.currentTimeMillis();
			List<HmDomain> domains = CacheMgmt.getInstance().getCacheDomains();
			Map<Long, Integer> newWaitedTime = new HashMap<Long, Integer>();
			List<HiveAp> total = new ArrayList<HiveAp>();
			for (HmDomain domain : domains) {
				Integer time = waitedTime.get(domain.getId());
				int newTime = 0;
				if (null != time) {
					int interval = BoMgmt.getIdpMgmt().getRefreshInterval(
							domain.getId()) * 60;
					if (time >= interval) {
						// refresh IDP for this domain
						total.addAll(getRefreshedHiveAp(domain));
					} else {
						newTime = time
								+ IdpScheduledExecutor.IDP_SCANNING_INTERVAL;
					}
				}
				newWaitedTime.put(domain.getId(), newTime);
			}
			waitedTime = newWaitedTime;
			if (!total.isEmpty()) {
				log.debug("idpRefresh", "Refresh for HiveAP count:"
						+ total.size());
				BeTopoModuleUtil.sendIDPQuery(total);
				long end = System.currentTimeMillis();
				log.debug("idpRefresh", "Refresh IDP send requests cost:"
						+ (end - start) + "ms.");
			}
		} catch (Exception e) {
			log.error("idpRefresh", "Refresh IDP exception.", e);
		} catch (Error e) {
			log.error("idpRefresh", "Refresh IDP error.", e);
		}
	}

	private List<HiveAp> getRefreshedHiveAp(HmDomain domain) {
		String where = "manageStatus = :s1 AND connected = :s2";
		Object[] values = { HiveAp.STATUS_MANAGED, true };
		return QueryUtil.executeQuery(HiveAp.class, null,
				new FilterParams(where, values), domain.getId());
	}

}