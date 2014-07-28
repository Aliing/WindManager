package com.ah.ui.actions.monitor;

import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpSessionBindingEvent;

import org.json.JSONObject;

import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.PagingCache;
import com.ah.bo.monitor.AhAlarm;
import com.ah.events.BoEventFilter;
import com.ah.events.BoEventListener;
import com.ah.events.impl.BoObserver;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */

public class AlarmsPagingCache extends PagingCache<AhAlarm> implements BoEventListener<AhAlarm> {

	private static final Tracer log = new Tracer(AlarmsPagingCache.class
			.getSimpleName());

	public AlarmsPagingCache(HmUser user) {
		super(AhAlarm.class, user);
		BoObserver.addBoEventListener(this, new BoEventFilter<AhAlarm>(AhAlarm.class));
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		log.info("valueUnbound", "Unbound event: " + event.getName());
		BoObserver.removeBoEventListener(this);
	}

	private final AtomicBoolean invalidated = new AtomicBoolean(true);

	public synchronized Collection<JSONObject> getUpdates(int cacheId)
			throws Exception {
		Collection<JSONObject> updates = new Vector<JSONObject>();
		if (getCacheId() != cacheId) {
			// Invalid refresh request
			return updates;
		}
		if (!invalidated.get()) {
			// Alarms in DB untouched
			return updates;
		}
		List<AhAlarm> newAlarms = refreshBos();
		if (hmBos.size() != newAlarms.size()) {
			// full refresh
			return refreshFromCache(newAlarms);
		}
		for (int i = 0; i < hmBos.size(); i++) {
			AhAlarm alarm = hmBos.get(i);
			AhAlarm newAlarm = newAlarms.get(i);
			if (!alarm.getId().equals(newAlarm.getId())) {
				// full refresh
				return refreshFromCache(newAlarms);
			}
			if (alarm.getSeverity() != newAlarm.getSeverity()) {
				JSONObject update = new JSONObject();
				update.put("id", newAlarm.getId());
				update.put("sev", newAlarm.getSeverity());
				update.put("sevs", newAlarm.getSeverityString());
				updates.add(update);
			}
		}
		hmBos = newAlarms;
		invalidated.set(false);
		return updates;
	}

	public void boCreated(AhAlarm alarm) {
		invalidated.set(true);
	}

	public void boUpdated(AhAlarm alarm) {
		invalidated.set(true);
	}

	public void boRemoved(AhAlarm alarm) {
		invalidated.set(true);
	}

}