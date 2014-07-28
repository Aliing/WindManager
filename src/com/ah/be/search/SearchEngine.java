/**
 * @filename			SearchEngine.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import java.io.File;

import com.ah.be.common.AhDirTools;

/**
 * HM Search Engine
 * 
 * Search following data in HM
 * <p>feature name in navigation tree</p>
 * <p>table names in list view</p>
 * <p>database</p>
 */
public interface SearchEngine {

	public static final String 		SEARCH_RESOURCES_PATH = AhDirTools.getHmRoot() +  
																		"resources" + File.separator + 
																		"search" + File.separator;

	public static final int SEARCH_QUERY_STEP = 10000;
	
	public static final int SEARCH_INSERT_STEP = 5000;
	
	public static final int SEARCH_RESULT_LIFE_TIME = 1; // HOUR
	
	public static final int INDEX_MAX_MAP_SIZE = 40000;

	public static final int INDEX_MAP_ARRAY_SIZE = 10000;

	public static final int INDEX_MAP_CAPACITY = 5000;
	
	public static final int INDEX_INTERVAL = 300;		// UNIT: second
	
	public static final int INDEX_TABLE_EVENT = 1;
	
	public static final int INDEX_TABLE_ALARM = 2;
	
	public static final int INDEX_FILE_ARRAY_SIZE = 64;
	
	public static final String KEY_PATTERN = " |,|\\.|/";

	public static final short INDEX_TYPE_DATABASE = 1;
	
	public static final short INDEX_TYPE_PAGE = 2;
	
	/**
	 * run search engine
	 * 
	 * @author Joseph Chen
	 */
	void start();
	
	/**
	 * stop search engine
	 * 
	 * @author Joseph Chen
	 */
	void stop();
	
	/**
	 * search data in HM
	 * 
	 * @param searchParameter	parameters of search
	 * @return	SearchResultSet object contain search result
	 * @author Joseph Chen
	 */
	int search(SearchParameter searchParameter);
	
}