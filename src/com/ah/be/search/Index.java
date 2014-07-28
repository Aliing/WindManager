/**
 * @filename			Index.java
 * @version
 * @author				Administrator
 * @since
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.ConfigUtil;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.PagingImpl;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.AhEvent;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;

public class Index implements SearchEngine, Runnable {

	private static final Tracer	log = new Tracer(Index.class.getSimpleName());
	
	/*
	 * index files
	 * 
	 * indexFiles[0]	-	numMap
	 * indexFiles[1]	-	alphaMap
	 * indexFiles[2]	-	numalphaMap
	 * indexFiles[3]	-	macMap
	 * indexFiles[4]	-	otherMap
	 */
	private final String[][] indexFiles = new String[5][INDEX_FILE_ARRAY_SIZE];
	
	/*
	 * index maps
	 * 
	 * map for numeric
	 */
	private Map<String, DocumentList> numMap;
	
	/*
	 * map for alphabetic
	 */
	private Map<String, DocumentList> alphaMap;
	
	/*
	 * map for numeric - alphabetic
	 */
	private Map<String, DocumentList> numalphaMap;
	
	/*
	 * map for mac
	 */
	private Map<String, DocumentList> macMap;
	
	/*
	 * map for others
	 */
	private Map<String, DocumentList> otherMap;
	
	
	/*
	 * min and max id of events/alarms
	 */
	private long events_min_id;
	private long events_max_id;
	private long alarms_min_id;
	private long alarms_max_id;
	
	/*
	 * a scheduler to run indexing
	 */
	private ScheduledExecutorService			scheduler;
	
	public Index() {
	    initParams();
	    load();
	}
	
	private void initParams() {
        numMap = new HashMap<String, DocumentList>(INDEX_MAP_CAPACITY);
        alphaMap = new HashMap<String, DocumentList>(INDEX_MAP_CAPACITY);
        numalphaMap = new HashMap<String, DocumentList>(INDEX_MAP_CAPACITY);
        macMap = new HashMap<String, DocumentList>(INDEX_MAP_CAPACITY);
        otherMap = new HashMap<String, DocumentList>(INDEX_MAP_CAPACITY);
    }

    @Override
	public void start() {
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(this, INDEX_INTERVAL, INDEX_INTERVAL, TimeUnit.SECONDS);
		}
		
		log.info("Index is running...");
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(getClass().getSimpleName());
		index();
	}

	@Override
	public int search(SearchParameter searchParameter) {
		if(searchParameter == null) {
			return 0;
		}
		
		int foundCount = 0;
		String key = searchParameter.getKeyword();
		
		/*
		 * domainId is for searching indexed documents
		 * it will be converted according the user context
		 */
		Long domainId = QueryUtil.getDomainFilter(searchParameter.getUserContext());
		
		String userName = searchParameter.getUserContext().getUserName();
		Long userDomainId = searchParameter.getUserContext().getDomain().getId();
		
		long t = System.currentTimeMillis();
		
		if(searchParameter.isMac()) { // mac
			foundCount += search(macMap, key, domainId, userName, userDomainId);
		} else if(searchParameter.isIp()) {
			foundCount += search(otherMap, key, domainId, userName, userDomainId);
		} else {
			if (StringUtils.isNumeric(key)) { // numeric
				foundCount += search(numMap, key, domainId, userName, userDomainId); // num
				foundCount += search(numalphaMap, key, domainId, userName, userDomainId); // num-alpha
			} else if (StringUtils.isAlpha(key)) { // alphabetic
				foundCount += search(alphaMap, key, domainId, userName, userDomainId); // alpha
				foundCount += search(numalphaMap, key, domainId, userName, userDomainId); // num-alpha
			} else if (StringUtils.isAlphanumeric(key)) { // num-alpha
				foundCount += search(numalphaMap, key, domainId, userName, userDomainId); // num-alpha
			} else { // other
				
			}
			
			foundCount += search(otherMap, key, domainId, userName, userDomainId); // other
		}
		
		log.debug("search", "Index search:   " + foundCount + " items found!");
		log.debug("search", "it takes " + (System.currentTimeMillis() - t) + "ms to search index.");
		
		return foundCount;
	}

	@Override
	public void stop() {
		/*
		 * shutdown executor of index
		 */
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
		
		/*
		 * save table min-max id to configuration file
		 */
		saveMinMax();
		
		/*
		 * save the map in memory into files
		 */
		saveMaps();
		
		log.info("Index is stopped.");
	}
	
	/**
	 * load resources into search engine
	 * 
	 * @author Joseph Chen
	 */
	private void load() {
		/*
		 * load index files
		 */
		loadIndexFiles();
		
		/*
		 * load maps to memory
		 */
		loadMaps();
		
		/*
		 * min and max table id
		 */
		loadMinMax();
	}

	private void loadIndexFiles() {
		log.info("loadIndexFiles", "begin loading index files.");
		
		File path = new File(SEARCH_RESOURCES_PATH);
		List<String> fileList = new ArrayList<String>();
		
		for(String file : path.list()) {
			if(file == null) {
				continue;
			}
			
			if(file.endsWith(".bin")) {
				fileList.add(file);
			}
		}
		
		if(fileList.isEmpty()) {
			log.info("loadIndexFiles", "no index file exists.");
			return ;
		}
		
		for(String file : fileList) {
			if(file == null || file.length() <6) {
				continue;
			}
			
			int i, j;
			try {
				i = Integer.valueOf(file.substring(1, file.indexOf("][")));
				j = Integer.valueOf(file.substring(file.indexOf("][") + 2, file.indexOf("].")));
				log.debug("loadIndexFile", "loaded index file: " + file);
				if(i >= indexFiles.length) {
				    log.error("loadIndexFiles", "error in parsing file name: "+file+", out of bounds array Index "+indexFiles.length);
				} else if (j >= indexFiles[i].length){
				    log.error("loadIndexFiles", "error in parsing file name: "+file+", out of bounds array Index "+indexFiles[i].length);
				} else {
				    indexFiles[i][j] = SEARCH_RESOURCES_PATH + file;
				}
			} catch(Exception e) {
				log.error("loadIndexFiles", "error in parsing file name: "+file, e);
			}
		}
		
		log.info("loadIndexFiles", "loading index files ends.");
	}
	
	private void loadMinMax() {
		try {
			events_min_id = Long.parseLong(ConfigUtil.getConfigInfo("search engine", "events_min_id"));
			events_max_id = Long.parseLong(ConfigUtil.getConfigInfo("search engine", "events_max_id"));
			alarms_min_id = Long.parseLong(ConfigUtil.getConfigInfo("search engine", "alarms_min_id"));
			alarms_max_id = Long.parseLong(ConfigUtil.getConfigInfo("search engine", "alarms_max_id"));
		} catch(Exception e) {
			log.error("loadMinMax", "error in loading min and max id from <config.ini>.", e);
		}
	}
	
	private void saveMinMax() {
		ConfigUtil.setConfigInfo("search engine", "events_min_id", String.valueOf(events_min_id));
		ConfigUtil.setConfigInfo("search engine", "events_max_id", String.valueOf(events_max_id));
		ConfigUtil.setConfigInfo("search engine", "alarms_min_id", String.valueOf(alarms_min_id));
		ConfigUtil.setConfigInfo("search engine", "alarms_max_id", String.valueOf(alarms_max_id));
	}
	
	private String getMapFileName(int i, int j) {
		StringBuilder buffer = new StringBuilder("[");
		
		buffer.append(i).append("][").append(j).append("]").append(".bin");
		
		return buffer.toString();
	}
	
	private void index() {
		log.debug("begin to index events...");
		
		/*
		 * events
		 */
		index(INDEX_TABLE_EVENT);
		
		log.debug("end indexing events...");
		log.debug("begin to index alarms...");
		
		/*
		 * alarms
		 */
		index(INDEX_TABLE_ALARM);
		
		log.debug("end indexing alarms...");
	}
	
	private void index(int tableId) {
		/*
		 * BO class
		 */
		Class<? extends HmBo> boClass = getBoClass(tableId);
		
		if(boClass == null) {
			log.error("index", "table is not supported. table id=" + tableId);
			return ;
		}
		
		/*
		 * min and max id
		 */
		long min_id_mem = 0, max_id_mem = 0;
		
		switch(tableId) {
		case INDEX_TABLE_EVENT:
			min_id_mem = events_min_id;
			max_id_mem = events_max_id;
			break;
		case INDEX_TABLE_ALARM:
			min_id_mem = alarms_min_id;
			max_id_mem = alarms_max_id;
			break;
		default:
			break;
		}
		
		// get min and max id from database
		long min_id, max_id;
		List<Long> minMax = getMinMaxId(boClass);
		
		if(minMax == null) {
			log.debug("index", "no data in table. table id=" + tableId);
			return;
		}
		
		min_id = minMax.get(0);
		max_id = minMax.get(1);
				
		if(min_id_mem == -1 || max_id_mem == -1) {
			// build index
			log.debug("building index...");
			updateIndex(tableId, min_id);
		} else if(min_id_mem < min_id){
			// update index, remove
			log.debug("removing index...");
			removeIndex(tableId, min_id);
		} else if(max_id_mem < max_id) {
			// update index, merge
			log.debug("updating index...");
			updateIndex(tableId, max_id_mem);
		} else {
			log.debug("on items to index at this round.");
		}
		
		switch(tableId) {
		case INDEX_TABLE_EVENT:
			events_min_id = min_id;
			events_max_id = max_id;
			break;
		case INDEX_TABLE_ALARM:
			alarms_min_id = min_id;
			alarms_max_id = max_id;
			break;
		default:
			break;
		}
	}
	
	/**
	 * update index
	 * 
	 * @param tableId	table id
	 * @param min_id	min id, index will be updated from the source records which id >= min_id
	 * @author Joseph Chen
	 */
	private void updateIndex(int tableId, long min_id) {
		if(tableId == INDEX_TABLE_EVENT) {
			updateEventIndex(min_id);
			return ;
		}
		
		Class<? extends HmBo> boClass = getBoClass(tableId);
		
		if(boClass == null) {
			return ;
		}
		
		/*
		 * paging
		 */
		Paging page = new PagingImpl(boClass);
		page.setPageSize(SEARCH_QUERY_STEP);
		page.clearRowCount();
		SortParams sort = new SortParams("id");
		String where = "id >= :s1";
		Object[] values = {min_id};
		FilterParams filter = new FilterParams(where, values);
		
		/*
		 * get BOs from database
		 */
		long t0 = System.currentTimeMillis(), t;
		int times = 0;
		Runtime r = Runtime.getRuntime();
		long total = r.maxMemory();
		
		while(page.hasNext()) {
			/*
			 * query
			 */
			t = System.currentTimeMillis();
			List<?> bos = page.next().executeQuery(sort, filter);
			log.debug("it took " + (System.currentTimeMillis() - t) + "ms to fetch " + bos.size() + " BOs.");
			
			t = System.currentTimeMillis();
			
			// build index
			for(Object bo : bos) {
				buildIndex(tableId, bo);
			}
			
			log.debug("it took " + (System.currentTimeMillis() - t) + "ms to index " + bos.size() + " BOs.");
			log.debug("indexed items: " + (times * SEARCH_QUERY_STEP + bos.size()));
			log.debug("memory Left: " + (r.freeMemory() * 100) / total + "% ," + r.freeMemory() / (1024 * 1024) + "M.");
		
			times++;
			bos.clear();
		}
				
		log.debug("total index time: " + (System.currentTimeMillis() - t0) + "ms");
	}
	
	private void updateEventIndex(long min_id) {
		String sql = new StringBuilder("SELECT event.id,apid,apname,time,time_zone,trapdesc,objectname,owner,domainname ")
								.append("FROM ah_event AS event, hm_domain AS domain ")
								.append("WHERE event.id>").append(min_id).append(" ")
								.append("AND event.owner=domain.id ")
								.append("ORDER BY id ")
								.append("OFFSET ").toString();
		
		int queryNumber = 0;
		long t0 = System.currentTimeMillis();
		long t = System.currentTimeMillis();
		Runtime r = Runtime.getRuntime();
		long total = r.maxMemory();
		
		/*
		 * query
		 */
		List<?> results = QueryUtil.executeNativeQuery(sql + SEARCH_QUERY_STEP * queryNumber++, SEARCH_QUERY_STEP);
		log.debug("it took " + (System.currentTimeMillis() - t) + "ms to fetch " + results.size() + " events.");
		log.debug("memory Left: " + (r.freeMemory() * 100) / total + "% ," + r.freeMemory() / (1024 * 1024) + "M.");
		
		while(!results.isEmpty()) {
			/*
			 * build index
			 */	
			t = System.currentTimeMillis();
			buildEventIndex(results);
			log.debug("it took " + (System.currentTimeMillis() - t) + "ms to index " + results.size() + " events.");
			log.debug("indexed items: " + ( (queryNumber - 1) * SEARCH_QUERY_STEP + results.size()));
			log.debug("memory Left: " + (r.freeMemory() * 100) / total + "% ," + r.freeMemory() / (1024 * 1024) + "M.");
			
			/*
			 * query
			 */
			results.clear();
			t = System.currentTimeMillis();
			results = QueryUtil.executeNativeQuery(sql + SEARCH_QUERY_STEP * (queryNumber++), SEARCH_QUERY_STEP);
			log.debug("it took " + (System.currentTimeMillis() - t) + "ms to fetch " + results.size() + " events.");
			log.debug("memory Left: " + (r.freeMemory() * 100) / total + "% ," + r.freeMemory() / (1024 * 1024) + "M.");
		}
		
		log.debug("total index time: " + (System.currentTimeMillis() - t0) + "ms");
	}
	
	/**
	 * build index for event table with specific raw data
	 * 
	 * @param rawData
	 * 0	-	id
	 * 1	-	apid
	 * 2	-	apname
	 * 3	-	time
	 * 4	-	time_zone
	 * 5	-	trapdesc
	 * 6 	-	objectname
	 * 7	-	owner
	 * 8	-	domainname
	 * @author Joseph Chen
	 */
	private void buildEventIndex(List<?> rawData) {
		if(rawData == null) {
			return ;
		}
		
		for(Object obj : rawData) {
			Object[] row = (Object[])obj;

			Document element = new Document(INDEX_TABLE_EVENT, 
					Long.parseLong(row[7].toString()), 
					Long.parseLong(row[0].toString()));
			
			
			// node id - to mac map
			if(row[1] != null) {
				indexMac(row[1].toString(), element);
				indexing(row[1].toString(), element, false);
					
			}
			
			// host name
			if(row[2] != null)
				indexing(row[2].toString(), element, true);
			
			// time
			if(row[3] != null && row[4] != null)
				indexing(AhDateTimeUtil.getFormattedDateTime(new HmTimeStamp(Long.parseLong(row[3].toString()),
																			row[4].toString())), 
															element, 
															true);
			
			// description
			if(row[5] != null)
				indexing(row[5].toString(), element, true);
			
			// component
			if(row[6] != null)
				indexing(row[6].toString(), element, false);
			
			// vhm
			if(row[8] != null)
				indexing(row[8].toString(), element, false);
		}
	}
	
	/**
	 * remove document id from index
	 * 
	 * @param tableId table id
	 * @param threshold documents of id < threshold are removed from index
	 * @author Joseph Chen
	 */
	private void removeIndex(int tableId, long threshold) {
		/*
		 * remove index in maps in memory
		 */
		removeIndex(numMap, tableId, threshold);
		removeIndex(alphaMap, tableId, threshold);
		removeIndex(numalphaMap, tableId, threshold);
		removeIndex(macMap, tableId, threshold);
		removeIndex(otherMap, tableId, threshold);
		
		/*
		 * remove index in files
		 */
		removeIndexInFile(tableId, threshold);		
	}
	
	/**
	 * get BO class by table id
	 * 
	 * @param tableId -
	 * @return -
	 * @author Joseph Chen
	 */
	private Class<? extends HmBo> getBoClass(int tableId) {
		Class<? extends HmBo> boClass = null;
		
		switch(tableId) {
		case INDEX_TABLE_EVENT:
			boClass = AhEvent.class;
			break;
		case INDEX_TABLE_ALARM:
			boClass = AhAlarm.class;
			break;
		default:
			break;
		}

		return boClass;
	}
	
	private List<Long> getMinMaxId(Class<? extends HmBo> boClass) {
		EntityManager em = null;
		EntityTransaction tx = null;
		List<Long> minMax = new ArrayList<Long>();
		
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			
			List<?> ids = QueryUtil.createQuery(em, 
					"select min(id), max(id) from " + boClass.getSimpleName(),
					null).getResultList();
			Object[] result = (Object[])ids.get(0);
			
			if(result[0] == null ) {
				log.debug("getMinMaxId", "no data in table: " + boClass.getSimpleName());
				tx.commit();
				return null;
			}
			
			minMax.add((Long)result[0]);
			minMax.add((Long)result[1]);
			
			tx.commit();
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("index", "Failed to get min and max id.", e);
			throw e;
		} finally {
			QueryUtil.closeEntityManager(em);
		}
		
		return minMax;
	}
	
	private void buildIndex(int tableId, Object bo) {
		switch(tableId) {
		case INDEX_TABLE_EVENT:
			buildIndex((AhEvent)bo);
			break;
		case INDEX_TABLE_ALARM:
			buildIndex((AhAlarm)bo);
			break;
		default:
			break;
		}
	}
	
	private void buildIndex(AhEvent event) {
		if(event == null) {
			return ;
		}
		
		Document element = new Document(INDEX_TABLE_EVENT, 
				event.getOwner().getId(), 
				event.getId());
		
		// node id - to mac map
		indexMac(event.getApId(), element);
		
		// node id
		indexing(event.getApId(), element, false);
		
		// host name
		indexing(event.getApName(), element, true);
		
		// time
		indexing(event.getTrapTimeStringFromBE(), element, true);
		
		// description
		indexing(event.getTrapDesc(), element, true);
		
		// component
		indexing(event.getObjectName(), element, false);
		
		// vhm
		indexing(event.getOwner().getDomainName(), element, false);
	}
	
	private void buildIndex(AhAlarm alarm) {
		if(alarm == null) {
			return ;
		}
		
		Document element = new Document(INDEX_TABLE_ALARM, 
				alarm.getOwner().getId(), 
				alarm.getId());

		// node id - to mac map
		indexMac(alarm.getApId(), element);
		
		// node id 
		indexing(alarm.getApId(), element, false);
		
		// severity
		indexing(alarm.getSeverityString(), element, false);
				
		// host name
		indexing(alarm.getApName(), element, true);
		
		// time
		indexing(alarm.getTrapTimeStringFromBE(), element, true);
		
		// cleared time
		if(!"-".equals(alarm.getClearTimeStringFromBE()))
			indexing(alarm.getClearTimeStringFromBE(), element, true);
		
		// description
		indexing(alarm.getTrapDesc(), element, true);
		
		// component
		indexing(alarm.getObjectName(), element, false);
		
		// vhm
		indexing(alarm.getOwner().getDomainName(), element, false);
	}
	
	private void indexMac(String string, Document element) {
		if(string == null) {
			return ;
		}
				
		/*
		 * add into map in memory
		 */
		putIntoMap(macMap, 3, string, element);
	}

	/**
	 * index a document
	 * 
	 * @param string		keyword
	 * @param element		-
	 * @param split			whether the keyword should be split.
	 * @author Joseph Chen
	 */
	private void indexing(String string, Document element, boolean split) {
		if(string == null || string.trim().length() == 0) {
			return ;
		}
		
		/*
		 * parse string
		 */
		if(split) {
			String[] splits = string.split(KEY_PATTERN);

			for (String split1 : splits) {
				if (split1.trim().length() == 0) {
					continue;
				}

				putIntoMap(split1, element);
			}
		} else {
			putIntoMap(string, element);
		}
	}
	
	private void putIntoMap(Map<String, DocumentList> map, int fileRow, String key, Document element) {
		if(key == null) {
			return ;
		}
		
		key = key.toLowerCase();
		DocumentList oldIndex = map.get(key);
		
		if(oldIndex == null) { // key not in the map
			/*
			 * if map size exceeds the maximum, dump elements into file
			 */
			if(map.size() >= INDEX_MAX_MAP_SIZE) {
				int i=0;
				
				for(; i<indexFiles[fileRow].length; i++) {
					if(indexFiles[fileRow][i] == null) {
						break;
					}
				}
				
				indexFiles[fileRow][i] = SEARCH_RESOURCES_PATH + getMapFileName(fileRow, i);
				IndexUtil.save(map, indexFiles[fileRow][i]);
				map.clear();
				System.gc();
			}
			
			DocumentList list = new DocumentList(SearchEngine.INDEX_TYPE_DATABASE);
			list.addDocument(element, fileRow, key);
			map.put(key, list);			
		} else { // key already in the map
			/*
			 * key is found, add element to index
			 */
			oldIndex.addDocument(element, fileRow, key);
		}
	}
		
	private void putIntoMap(String key, Document element) {
		if(key == null) {
			return;
		}
		key = key.trim();
		int fileRow;
		Map<String, DocumentList> map;
		
		if(StringUtils.isNumeric(key)){ // numeric
			fileRow = 0;
			map = numMap;
		} else if(StringUtils.isAlpha(key)) { // alphabetic
			fileRow = 1;
			map = alphaMap;
		} else if(StringUtils.isAlphanumeric(key)) { // alphabetic numeric
			fileRow = 2;
			map = numalphaMap;
		} else { // other
			fileRow = 4;
			map = otherMap;
		}
		
		putIntoMap(map, fileRow, key, element);
	}
	
	private int removeIndex(Map<String, DocumentList> map, int tableId, long ceiling) {
		if(map == null) {
			return 0;
		}
		
		Iterator<String> it = map.keySet().iterator();
		
		String key;
		DocumentList docList;
		int removedCount = 0;
		
		while(it.hasNext()) {
			key = it.next();
			
			docList = map.get(key);
			removedCount += docList.removeDocument(tableId, ceiling);
			
			if(!docList.hasDocument()) { // no document
				// remove key
				it.remove();
			}
		}
		
		return removedCount;
	}
	
	private void removeIndexInFile(int tableId, long threshold) {
		for(int i=0; i<indexFiles.length; i++) {
			String[] fileRow = indexFiles[i];
			
			for(int j=0; j<fileRow.length; j++) {
				if(indexFiles[i][j] == null) {
					continue ;
				}
				
				List<Object> mapList = IndexUtil.read(indexFiles[i][j]);
				
				if(mapList == null) {
					continue ;
				}
				
				Map<String, DocumentList> map;
				int removedCount;
				
				for(Object obj : mapList) {
					map = (Map<String, DocumentList>)obj;
					
					removedCount = removeIndex(map, tableId, threshold);
					
					if(removedCount > 0) { // at least one document has been removed
						log.debug("removeIndexInFile",
								removedCount + " documents has been removed from file - " + indexFiles[i][j]);
						/*
						 * update file
						 * 
						 * first, remove the old file
						 */
						File file = new File(indexFiles[i][j]);
						file.delete();
						
						if(map.isEmpty()) { // no entry in map
							log.debug("file " + indexFiles[i][j] + " has been deleted.");
							indexFiles[i][j] = null;
						} else {
							// save the left into file
							IndexUtil.save(map, indexFiles[i][j]);
						}
					}
				}
			}
		}
	}
	
	private int search(Map<String, DocumentList> map, 
			String key, 
			Long domainId,
			String userName,
			Long userDomainId) {
		Iterator<String> it = map.keySet().iterator();
		int foundCount = 0;
		
		while(it.hasNext()) {
			String string = it.next();
			
			if(string.contains(key)) { // found
				DocumentList docList = map.get(string);
				
				foundCount += extractTargets(docList, domainId, userName, userDomainId);
			}
		}
		
		return foundCount;
	}
	
	private int extractTargets(DocumentList docList, 
			Long domainId, 
			String userName, 
			Long userDomainId) {
		int foundCount = 0;
		List<Target> results = new ArrayList<Target>(3000);
		
		/*
		 * documents in memory
		 */
		foundCount += extractTargets(results, 
				docList.getDocuments(), 
				domainId, 
				userName,
				userDomainId);
		
		/*
		 * documents in file
		 */
		if(docList.hasDocumentInFile()) {
			List<Object> list;
			
			for(String fileName : docList.getFiles()) {
				if(fileName == null) {
					continue;
				}
				
				long t = System.currentTimeMillis();
				list = IndexUtil.read(fileName);
				log.debug("extractTargets", "it take " + (System.currentTimeMillis() - t) + "ms to load documents from disk file: " + fileName);
				
				if(list == null) {
					continue;
				}
				
				t = System.currentTimeMillis();

				for (Object obj : list) {
					foundCount += extractTargets(results,
							(List<IDocument>) obj,
							domainId,
							userName,
							userDomainId);

					if (results.size() >= SEARCH_INSERT_STEP) {
						/*
						 * insert into database
						 */
						SearchUtil.saveTargets(results);
						results.clear();
					}
				}
				
				log.debug("extractTargets", "it take " + (System.currentTimeMillis() - t) + "ms to extract " + list.size() * DocumentList.BUFFER_MAX_SIZE + " documents ");
				
				list.clear();
			}
		}
		
		/*
		 * insert into database
		 */
		SearchUtil.saveTargets(results);
		
		return foundCount;
	}
	
	private int extractTargets(List<Target> buffer, 
			List<IDocument> docList,
			Long domainId, 
			String userName,
			Long userDomainId) {
		EntityTarget et;
		int foundCount = 0;
		
		for(IDocument idoc : docList) {
			Document doc = (Document)idoc;
			
			if(domainId == null
					|| domainId == doc.getDomainId()) { 
				et = new EntityTarget();
				et.setAction(getActionByTableId(doc.getTableId()));
				et.setFeature(getFeatureByTableId(doc.getTableId()));
				et.setType(SearchParameter.TYPE_FAULT);
				et.setBoDomainId(doc.getDomainId());
				et.setUserName(userName);
				et.setUserDomainId(userDomainId);
				et.setBoId(doc.getBoId());
				
				if(buffer.contains(et)) {
				    continue;
				}
				
				buffer.add(et);
				foundCount++;
			}
		}
		
		return foundCount;
	}
	
	private String getActionByTableId(int tableId) {
		String action = null;
		
		switch(tableId) {
		case INDEX_TABLE_EVENT:
			action = "events";
			break;
		case INDEX_TABLE_ALARM:
			action = "alarms";
			break;
		default:
			break;
		}
		
		return action;
	}
	
	private String getFeatureByTableId(int tableId) {
		String feature = null;
		
		switch(tableId) {
		case INDEX_TABLE_EVENT:
			feature = "Events";
			break;
		case INDEX_TABLE_ALARM:
			feature = "Alarms";
			break;
		default:
			break;
		}
		
		return feature;
	}
	
	private void saveMaps() {
		saveMap(numMap);
		saveMap(alphaMap);
		saveMap(numalphaMap);
		saveMap(macMap);
		saveMap(otherMap);
	}
	
	private void saveMap(Map<String, DocumentList> map) {
		if(map == null || map.isEmpty()) {
			return ;
		}
		
		/*
		 * get row in file array
		 */
		int row = getRowByMap(map);
		
		if(row == -1) {
			return ;
		}
		
		int col = 0;
		for(; col<INDEX_FILE_ARRAY_SIZE; col++) {
			if(indexFiles[row][col] == null) {
				break;
			}
		}
		
		IndexUtil.save(map, SEARCH_RESOURCES_PATH + getMapFileName(row, col));
	}
	
	private int getRowByMap(Map<String, DocumentList> map) {
		int row = -1;
		
		if(map == numMap) {
			row = 0;
		} else if(map == alphaMap) {
			row = 1;
		} else if(map == numalphaMap) {
			row = 2;
		} else if(map == macMap) {
			row = 3;
		} else if(map == otherMap) {
			row = 4;
		}
		
		return row;
	}
	
	private Map<String, DocumentList> getMapByRow(int row) {
		Map<String, DocumentList> map = null;
		
		switch(row) {
		case 0:
			map = numMap;
			break;
		case 1:
			map = alphaMap;
			break;
		case 2:
			map = numalphaMap;
			break;
		case 3:
			map = macMap;
			break;
		case 4:
			map = otherMap;
			break;
		default:
			break;
		}
		
		return map;
	}
	
	/**
	 * load map in file which is not full. 
	 * that means the file contains less index entries than the max limit
	 * 
	 * @author Joseph Chen
	 */
	private void loadMaps() {
		for(int row=0; row<indexFiles.length; row++) {
			int col=INDEX_FILE_ARRAY_SIZE - 1;
			/*
			 * from end to head, find one not null
			 */
			for(; col>=0; col--) {
				if(indexFiles[row][col] != null) {
					break;
				}
			}
			
			if(col == -1) { // no file
				continue;
			}
			
			/*
			 * load file
			 */
			List<Object> list = IndexUtil.read(indexFiles[row][col]);
			
			if(list == null) {
				continue ;
			}
			
			/*
			 * set maps
			 */
			for(Object obj : list) {
				if(obj == null) {
					continue;
				}
				
				Map<String, DocumentList> map = (Map<String, DocumentList>)obj;
				
				switch(row) {
				case 0:
					numMap = map;
					break;
				case 1:
					alphaMap = map;
					break;
				case 2:
					numalphaMap = map;
					break;
				case 3:
					macMap = map;
					break;
				case 4:
					otherMap = map;
					break;
				default:
					break;
				}
			}
			
			/*
			 * clear file
			 */
			File file = new File(indexFiles[row][col]);
			file.delete();
			indexFiles[row][col] = null;
		}
	}

}