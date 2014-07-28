package com.ah.be.admin.schedulebackup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import com.ah.be.admin.util.HAadminTool;
import com.ah.be.app.DebugUtil;
import com.ah.bo.admin.AhScheduleBackupData;
import com.ah.bo.admin.HmDomain;

public class AhBackupSchedulePool implements AhBackupWatchIF {

	private static final long TIME_UNIT = 60 * 60 * 1000;

	private static final long TIME_UNIT_MIN = 60 * 1000;

	private List<AhNMSBackupTask> oTaskList;

	private Timer m_oTimer;

	public AhBackupSchedulePool() {
	}

	public void initSchedulePool() {
		m_oTimer = new Timer();

		List<AhScheduleBackupData> oDataList = AhNmsScheduleBackupDbImpl
				.initScheduleInfoFromDB();

		/*if(HAadminTool.isHaModel() && !HAadminTool.isValidMaster()){
			oTaskList = new ArrayList<AhNMSBackupTask>();

			return;
		}*/
		
		if (null == oDataList || 0 == oDataList.size()) {
			oTaskList = new ArrayList<AhNMSBackupTask>();

			return;
		}

		oTaskList = new ArrayList<AhNMSBackupTask>();

		for (AhScheduleBackupData backupData : oDataList) {
			if (!backupData.getLiveFlag()) {
				continue;
			}

			AhNMSBackupTask oTask = new AhNMSBackupTask(backupData, this);

			oTaskList.add(oTask);
		}

		for (AhNMSBackupTask backupTask : oTaskList) {
			startupTask(backupTask);
		}
	}

	public AhScheduleBackupData getScheduleData(HmDomain oDomain) {
		if (null == oDomain || null == oDomain.getId()
				|| null == oDomain.getDomainName()) {
			return AhNmsScheduleBackupDbImpl.setDefaultTaskdata();
		}

		for (AhNMSBackupTask backupTask : oTaskList) {
			if (backupTask.getTaskData().getOwner().getDomainName()
					.equalsIgnoreCase(oDomain.getDomainName())) {
				return backupTask.getTaskData();
			}
		}

		return AhNmsScheduleBackupDbImpl.setDefaultTaskdata();
	}

	public synchronized boolean setBackupSchedule(AhScheduleBackupData oData) {		
		if (null == oData || null == oData.getOwner().getId()
				|| null == oData.getOwner().getDomainName()
				|| "".equalsIgnoreCase(oData.getOwner().getDomainName())) {
		    return false;			
		}
		
		//insert or update db
		if(!AhNmsScheduleBackupDbImpl.setScheduleInfoToDB(oData))
		{
			return false;
		}		
		
		for(Iterator<AhNMSBackupTask> it=oTaskList.iterator(); it.hasNext();)
		{
			AhNMSBackupTask oTmp = it.next();
			
			if(oTmp.getTaskData().getOwner().getDomainName()
					.equalsIgnoreCase(oData.getOwner().getDomainName()))
			{
				it.remove();
				if(oTmp.cancel())
				{
					m_oTimer.purge();
				}			
			}
		}	
		
		AhNMSBackupTask oTask = new AhNMSBackupTask(oData, this);
		//add and run task		
		startupTask(oTask);
		
		oTaskList.add(oTask);
		
		return true;
	}
	
	public synchronized boolean cancelBackupTask(HmDomain oDomain)
	{
		if (null == oDomain || null == oDomain.getId()
				|| null == oDomain.getDomainName()) {
			return false;
		}
		
		for(Iterator<AhNMSBackupTask> it=oTaskList.iterator(); it.hasNext();)
		{
			AhNMSBackupTask oTmp = it.next();
			
			if(oTmp.getTaskData().getOwner().getDomainName()
					.equalsIgnoreCase(oDomain.getDomainName()))
			{
                AhScheduleBackupData oTaskData = oTmp.getTaskData();
				
				oTaskData.setLiveFlag(false);
				
				if(!AhNmsScheduleBackupDbImpl.setScheduleInfoToDB(oTaskData))
				{
					return false;
				}
				
				it.remove();
				
				if(oTmp.cancel())
				{
					m_oTimer.purge();
				}		
			}
		}
		
		return true;
	}

	private void startupTask(AhNMSBackupTask oTask) {
		if (!oTask.getTaskData().getLiveFlag()) {
			return;
		}

		SimpleDateFormat stmp = new SimpleDateFormat("yyyy-MM-dd");

		Date dTmp;

		try {
            long lTmp = stmp.parse(oTask.getTaskData().getStartDate()).getTime();
            
            lTmp = lTmp + oTask.getTaskData().getStartHour()
		       * AhBackupSchedulePool.TIME_UNIT
		       + oTask.getTaskData().getStartMinute()
		       * AhBackupSchedulePool.TIME_UNIT_MIN;
            
           // long lNow = stmp.parse(stmp.format(new Date())).getTime();
            long lNow = System.currentTimeMillis();
            
            if(lNow > lTmp)
            {
            	if(!isContinueExcute(oTask.getTaskData()))
            	{
            		lTmp = lNow+5000;
            	}
            	else
            	{
            		int iTmp = (int)((lNow - lTmp)/(oTask.getTaskData().getInterval() * 24* AhBackupSchedulePool.TIME_UNIT));
            		
            		lTmp = lTmp + (iTmp +1)*oTask.getTaskData().getInterval() * 24* AhBackupSchedulePool.TIME_UNIT;            		
            	}            	
            }           
		
			DebugUtil.adminDebugWarn("leaft run time is :"+(lTmp-System.currentTimeMillis()));
			dTmp = new Date(lTmp);

			if (!oTask.getTaskData().getRescurFlag()) {
				m_oTimer.schedule(oTask, dTmp);

				return;
			}

			m_oTimer.scheduleAtFixedRate(oTask, dTmp, oTask.getTaskData()
					.getInterval()
					* 24 * AhBackupSchedulePool.TIME_UNIT);
		} catch (Exception ex) {
			DebugUtil.adminDebugWarn(
					"AhBackupSchedule.generateTask() catch exception", ex);
		}
	}
	
	public void stop() {
		if (m_oTimer != null) {
			m_oTimer.cancel();
		}
	}
	
	//after execute task then judge the task status
	public void watchBackupTask(AhNMSBackupTask oTask) {
		if(null == oTask || null == oTask.getTaskData() || null == oTask.getTaskData().getOwner())
		{
			return;
		}
		
		if(isContinueExcute(oTask.getTaskData()))
		{
			return;
		}
		
		AhScheduleBackupData oTaskData = oTask.getTaskData();
		
		oTaskData.setLiveFlag(false);
		
		if(!AhNmsScheduleBackupDbImpl.setScheduleInfoToDB(oTaskData))
		{
			return;
		}	
		
		for(Iterator<AhNMSBackupTask> it=oTaskList.iterator(); it.hasNext();)
		{
			AhNMSBackupTask oTmp = it.next();
			
			if(oTmp.getTaskData().getOwner().getDomainName()
					.equalsIgnoreCase(oTaskData.getOwner().getDomainName()))
			{
				it.remove();
				if(oTmp.cancel())
				{
					m_oTimer.purge();
				}			
			}
		}
	}
	
	private boolean isContinueExcute(AhScheduleBackupData oData)
	{
		if (!oData.getRescurFlag())
		{
			return false;
		}
		
		if (!oData.getEndDateFlag())
		{
			return true;
		}

		SimpleDateFormat stmp = new SimpleDateFormat("yyyy-MM-dd");

		try
		{
			long lTmp = stmp.parse(oData.getEndDate()).getTime()
				+ oData.getEndHour() * AhBackupSchedulePool.TIME_UNIT
				+ oData.getEndMinute() * AhBackupSchedulePool.TIME_UNIT_MIN
				- System.currentTimeMillis();

			return lTmp > oData.getInterval() * 24
						  * AhBackupSchedulePool.TIME_UNIT;
		}
		catch (Exception ex)
		{
			DebugUtil.adminDebugWarn("AhBackupSchedule.isContinueExcute() catch exception", ex);
			
			return true;
		}
	}

}