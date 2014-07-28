/**
 * @filename			HmBeMiscUtil.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5
 * 
 * Copyright (c) 2006-2010 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.app;

import java.util.List;

import com.ah.be.misc.teacherview.ClearClassRequest;

/**
 * A utility class of BeMiscModule
 */
public class HmBeMiscUtil {

	public static boolean addClearClassRequest(ClearClassRequest request) 
			throws Exception {
		
		return AhAppContainer.HmBe.getMiscModule().addClearClassRequest(request);
	}
	
	public static boolean removeClearClassRequests(String apAddress, 
			List<ClearClassRequest> requests) throws Exception {
		
		return AhAppContainer.HmBe.getMiscModule()
				.removeClearClassRequests(apAddress, requests);
	}

	public static boolean hasClearClassRequest(String apAddress) {
		return AhAppContainer.HmBe.getMiscModule().hasClearClassRequest(apAddress);
	}
	
	public static List<ClearClassRequest> getClearClassRequest(String apAddress) {
		return AhAppContainer.HmBe.getMiscModule().getClearClassRequest(apAddress);
	}

}