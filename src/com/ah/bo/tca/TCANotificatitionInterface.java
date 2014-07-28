/**   
* @Title: TCANotificateInterface.java 
* @Package com.ah.bo.tca 
* @Description: TODO(write something)
* @author xxu   
* @date 2012-8-2 
* @version V1.0   
*/
package com.ah.bo.tca;

import com.ah.bo.monitor.AhAlarm;

/** 
 * @ClassName: TCANotificatitionInterface 
 * @Description: raise or clear alarm, show them in the gui and notif
 * @author xxu
 * @date 2012-8-2
 *  
 */
public interface TCANotificatitionInterface {
	
	boolean raiseAlarm(AhAlarm alarm);
	
	boolean clearAlarm(AhAlarm alarm);
	
	boolean sendTrap(AhAlarm alarm);

}
