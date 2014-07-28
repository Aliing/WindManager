/**
 * @filename			SearchUtil.java
 * @version
 * @author				Administrator
 * @since
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.ah.be.app.AhAppContainer;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAUtil;
import com.ah.util.Tracer;

/**
 * 
 */
public class SearchUtil {
	public static final Tracer	log	= new Tracer(SearchUtil.class.getSimpleName());
	
	private static ReentrantLock methodLock = new ReentrantLock();
	private static AtomicInteger currentRunSearchCount = new AtomicInteger(0);
	private static ThreadLocal<Integer> currentThreadRunSearchCount = new ThreadLocal<Integer>() {
		protected Integer initialValue() {return currentRunSearchCount.get();};
	};
	
	public static int search(SearchParameter param) {
		// start search, increase the current search count
		currentThreadRunSearchCount.set(currentRunSearchCount.incrementAndGet());
		
		// invoke search engine, return the search result total number
		int searchResultCount = AhAppContainer.getBeMiscModule().getSearchEngine().search(param);
		
		// decrease search, decrease the current search count
		currentThreadRunSearchCount.set(currentRunSearchCount.decrementAndGet());
		
		return searchResultCount;
	}
	
	/**
	 * TA2003, judge if the search request exceed the limitation. 
	 * @author Yunzhi Lin
	 * - Time: Oct 18, 2011 2:06:32 PM
	 * @return <code>true</code> if concurrent search count is reach the limitation; <br>else return <code>false</code>
	 */
	public static boolean isExceedPermitSearchAmount() {
		methodLock.lock();
		try {
			log.debug("**** current search request count:"+currentThreadRunSearchCount.get()+" ****");
			int maxPermitNum = 1;
			
			try {
				// get the value from DB
				String sql = "select concurrentSearchUserNum from hmservicessettings h, hm_domain d "
					+ " where d.domainname='home' and d.id = h.owner";
				List<?> list = QueryUtil.executeNativeQuery(sql, 1);
				if(!(null == list || list.isEmpty())) {
					Short object = (Short) list.get(0);
					maxPermitNum = Integer.parseInt(object.toString());
				}
			} catch (Exception e) {
				log.error("isExceedPermitSearchAmount()", 
						"Error when excute native query to find the concurrrent search limitation num. Error:", e);
			}
			
			if(currentThreadRunSearchCount.get().intValue() >= maxPermitNum) {
				log.debug("**** the search count reach the limitation:" + maxPermitNum+ ". ****");
				return true;
			} else {
				return false;
			}
		} finally {
			methodLock.unlock();
		}
	}
	/**
	 * save found targets into database
	 * 
	 * @param targets
	 * @author Joseph Chen
	 */
	public static void saveTargets(List<Target> targets) {
		if(targets == null ||
				targets.size() == 0) {
			return ;
		}
		
		try {
			QueryUtil.bulkCreateBos(targets);
		} catch (Exception e) {
			log.error("saveTargets", 
					"Failed to insert " + targets.size() + " objects into database", 
					e);
		}
	}
	
	/**
	 * clear the results of last search of a user
	 * @param userName
	 * @author Joseph Chen
	 */
	public static void clearSearchResults(HmUser hmUser) {
	    if(HAUtil.isSlave()) {
	        // Do nothing in slave mode
	        return;
	    }
		FilterParams filter = new FilterParams("userName = :s1 AND userDomainId = :s2", 
				new Object[] {hmUser.getUserName(), hmUser.getOwner().getId()});
		
		try {
			QueryUtil.bulkRemoveBos(Target.class, filter);
		} catch (Exception e) {
			log.error("clearSearchResults", 
					"Failed to clear search results of user: " + hmUser.getUserName(), 
					e);
		}
	}
}
