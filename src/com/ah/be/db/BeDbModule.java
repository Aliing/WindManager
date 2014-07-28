/**
 *@filename		BeDbModule.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 01:51:00 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.db;

// java import
import java.io.Serializable;
import java.util.Map;

// aerohive import
import com.ah.be.event.AhEventMgmt;
import com.ah.be.event.BeBaseEvent;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public interface BeDbModule extends Serializable {

	AhEventMgmt<BeBaseEvent> getDiscoveryMgmt();
	
	Map<String, String> getTableReferences(String tableName);

}