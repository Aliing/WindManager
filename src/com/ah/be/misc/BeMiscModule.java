/**
 * @filename			BeMiscModule.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5R1
 * 
 * Copyright (c) 2006-2010 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.misc;

import java.util.List;

import com.ah.be.misc.teacherview.ClearClassRequest;
import com.ah.be.search.SearchEngine;
import com.ah.integration.airtight.SgeIntegrator;

/**
 * The interface could be used to do miscellaneous tasks and things 
 * which are not easy to be classified into other modules.
 */
public interface BeMiscModule {

	/**
	 * Add a request to clear class students into container
	 * 
	 * @param request	the request to be added
	 * @return	true if the request is added successfully, else false
	 * @throws Exception -
	 * @author Joseph Chen
	 */
	boolean addClearClassRequest(ClearClassRequest request) throws Exception;
	
	/**
	 * Remove a request of clearing class students from container
	 *
	 * @param apAddress -
	 * @param requests	the requests to be removed
	 * @return	true if the requests are removed successfully, else false
	 * @throws Exception -
	 * @author Joseph Chen
	 */
	boolean removeClearClassRequests(String apAddress, List<ClearClassRequest> requests) throws Exception;
	
	/**
	 * Check if there is request to clear class students or not
	 * 
	 * @param apAddress	address of HiveAP
	 * @return	true if there is such request, false otherwise
	 * @author Joseph Chen
	 */
	boolean hasClearClassRequest(String apAddress);
	
	/**
	 * Get request of clearing class students for a HiveAP
	 * 
	 * @param apAddress address of HiveAP
	 * @return	a list of <code>ClearClassRequest</code> objects
	 * @author Joseph Chen
	 */
	List<ClearClassRequest> getClearClassRequest(String apAddress);

	SgeIntegrator getAirTightSgeIntegrator();
	
	SearchEngine getSearchEngine();

}