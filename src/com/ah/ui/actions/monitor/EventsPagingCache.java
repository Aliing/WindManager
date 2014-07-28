package com.ah.ui.actions.monitor;

import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpSessionBindingEvent;

import org.json.JSONObject;

import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.PagingCache;
import com.ah.bo.monitor.AhEvent;
import com.ah.events.BoEventFilter;
import com.ah.events.BoEventListener;
import com.ah.events.impl.BoObserver;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */

public class EventsPagingCache extends PagingCache<AhEvent> implements BoEventListener<AhEvent> {

	private static final Tracer log = new Tracer(EventsPagingCache.class
			.getSimpleName());

	public EventsPagingCache(HmUser user) {
		super(AhEvent.class, user);
		BoObserver.addBoEventListener(this, new BoEventFilter<AhEvent>(AhEvent.class));
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
			// Events in DB untouched
			return updates;
		}
		List<AhEvent> newEvents = findBos();
		if (hmBos.size() != newEvents.size()) {
			// full refresh
			return refreshFromCache(newEvents);
		}
		for (int i = 0; i < hmBos.size(); i++) {
			AhEvent event = hmBos.get(i);
			AhEvent newEvent = newEvents.get(i);
			if (!event.getId().equals(newEvent.getId())) {
				// full refresh
				return refreshFromCache(newEvents);
			}
		}
		hmBos = newEvents;
		invalidated.set(false);
		return updates;
	}

	public void boCreated(AhEvent event) {
		invalidated.set(true);
	}

	public void boUpdated(AhEvent event) {
		invalidated.set(true);
	}

	public void boRemoved(AhEvent event) {
		invalidated.set(true);
	}

}