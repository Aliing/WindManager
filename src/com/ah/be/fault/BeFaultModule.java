/**
 *@filename		BeFaultModule.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 01:52:38 AM.
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.fault;

import java.util.concurrent.BlockingQueue;

import com.ah.be.app.BaseModule;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.fault.hiveap.AhApAlarmMgmt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.monitor.AhAlarm;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public interface BeFaultModule {

	public BlockingQueue<BeBaseEvent> getTrapQueue();

	public BaseModule getModule();

	public BlockingQueue<Object> getTrapToMailQueue();

	public BlockingQueue<AhAlarm> getAlarmQueue();

	public BlockingQueue<BeBaseEvent> getTrapCapwapQueue();

	public boolean isRunTrapToMailProcess();

	public void setRunTrapToMailProcess(boolean isRun);

	public void addTrapToMailQueue(Object arg_Trap) throws Exception;

	public void addAlarmToQueue(AhAlarm alarm);

	public void addTrapToQueue(BeBaseEvent arg_Trap);

	public void sendAlarm(HiveAp hiveAp, short severity, String description);

	public void saveAlarm(String apMac, String desc, int severity, int type, short subType, int tag1, int tag2,
			String objectName, String domainName, String hostName);

	public void setCapwapTrapFilterInterval(int filterInterval);

	AhApAlarmMgmt getHiveApAlarmMgmt();

}