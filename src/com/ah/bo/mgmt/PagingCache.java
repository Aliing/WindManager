package com.ah.bo.mgmt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmUser;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */

public abstract class PagingCache<T extends HmBo> implements HttpSessionBindingListener {

	private static final Tracer log = new Tracer(PagingCache.class
			.getSimpleName());

	public void valueBound(HttpSessionBindingEvent event) {
		log.info("valueBound", "Bound event: " + event.getName());
	}

	/*
	 * Create cache for list of boClass objects.
	 */
	protected PagingCache(Class<T> boClass, HmUser user) {
		this.boClass = boClass;
		this.user = user;
	}

	/*
	 * Initialize cache.
	 */
	public synchronized int init() {
		hmBos = findBos();
		return ++cacheId;
	}

	/*
	 * Get cached list of BOs
	 */
	public synchronized List<T> getBos(int cacheId) {
		if (this.cacheId != cacheId) {
			// Invalid refresh request
			return new ArrayList<T>();
		}
		return hmBos;
	}

	public abstract Collection<JSONObject> getUpdates(int cacheId)
			throws Exception;

	private final Class<T> boClass;

	private final HmUser user;

	private int cacheId = 0;

	protected int getCacheId() {
		return cacheId;
	}

	protected List<T> hmBos;

	protected List<T> findBos(boolean refresh) {
		SortParams sortParams = (SortParams) MgrUtil
				.getSessionAttribute(boClass.getSimpleName() + "Sorting");
		FilterParams filterParams = (FilterParams) MgrUtil
				.getSessionAttribute(boClass.getSimpleName() + "Filtering");
		Paging<T> paging = (Paging<T>) MgrUtil.getSessionAttribute(boClass
				.getSimpleName()
				+ "Paging");
		if (paging == null) {
			return null;
		}
		paging.clearRowCount();
		Date start = new Date();
		List<T> bos = paging.executeQuery(sortParams, filterParams, user, refresh);
		Date end = new Date();
		log.info("findBos", "Page query in: "
				+ (end.getTime() - start.getTime()) + " ms.");
		return bos;
	}

	protected List<T> findBos() {
		return findBos(false);
	}

	protected List<T> refreshBos() {
		return findBos(true);
	}

	protected Collection<JSONObject> refreshFromCache(List<T> newHmBos)
			throws Exception {
		hmBos = newHmBos;
		Collection<JSONObject> updates = new Vector<JSONObject>();
		JSONObject update = new JSONObject();
		update.put("id", -1);
		updates.add(update);
		return updates;
	}

}