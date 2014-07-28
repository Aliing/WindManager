package com.ah.be.common.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ReportFileCacheMgmt {
		
	public static final int MAX_SIZE = 100000;
	
	public static final long TIMEOUT_SECOND = 2 * 3600 * 1000; //cache 2 hours

	private static ReportFileCacheMgmt instance;
	
	private final Map<String, Long> lprFileCache;
	
	private ReportFileCacheMgmt() {
		lprFileCache = Collections.synchronizedMap(new HashMap<String, Long>());
	}

	public synchronized static ReportFileCacheMgmt getInstance() {
		if(null == instance) {
			instance = new ReportFileCacheMgmt();
		}
		return instance;
	}
		
	public int size() {
		return lprFileCache.size();
	}
	
	public void cleanHistoryData() {
		if (lprFileCache.size() < 1) {
			return;
		}
		long now = System.currentTimeMillis();
		synchronized(lprFileCache) {
			Set<String> set = lprFileCache.keySet();
			for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
				String key = iter.next();
				if (lprFileCache.get(key) != null) {
					Long timstamp = lprFileCache.get(key);
					if (timstamp.longValue() <= now) {
						iter.remove();
					}
				}
			}
		}
	}
	
	public boolean isExistFileName(String fileName) {
		return lprFileCache.containsKey(fileName);
	}
	   
    public void saveFileName(String fileName) {
    	if (lprFileCache.size() >= MAX_SIZE) {
    		return;
    	}
    	if (fileName != null && fileName.endsWith(".lpr")) {
    		lprFileCache.put(fileName, System.currentTimeMillis() + TIMEOUT_SECOND);
    	}
    }
    
    
    
}