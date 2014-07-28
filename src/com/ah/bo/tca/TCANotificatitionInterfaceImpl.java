/**   
* @Title: TCANotificatitionInterfaceImpl.java 
* @Package com.ah.bo.tca 
* @Description: TODO(write something)
* @author xxu   
* @date 2012-8-2 
* @version V1.0   
*/
package com.ah.bo.tca;

import com.ah.be.app.AhAppContainer;
import com.ah.bo.monitor.AhAlarm;

/** 
 * @ClassName: TCANotificatitionInterfaceImpl 
 * @Description: used to show alarm and clear alarm 
 * @author xxu
 * @date 2012-8-2
 *  
 */
public class TCANotificatitionInterfaceImpl implements TCANotificatitionInterface{

	
	/* (non-Javadoc)
	 * @see com.ah.bo.tca.TCANotificatitionInterface#raiseAlarm(com.ah.bo.monitor.AhAlarm)
	 */
	@Override
	public boolean raiseAlarm(AhAlarm alarm) {
		// TODO Auto-generated method stub
		AhAppContainer.getBeFaultModule().addAlarmToQueue(alarm);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.tca.TCANotificatitionInterface#clearAlarm(com.ah.bo.monitor.AhAlarm)
	 */
	@Override
	public boolean clearAlarm(AhAlarm alarm) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendTrap(AhAlarm alarm) {
		// TODO Auto-generated method stub
		return false;
	}
	


}
