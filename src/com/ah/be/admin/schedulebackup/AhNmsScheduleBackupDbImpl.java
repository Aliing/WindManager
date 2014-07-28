package com.ah.be.admin.schedulebackup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ah.be.app.DebugUtil;
import com.ah.bo.admin.AhScheduleBackupData;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;

public class AhNmsScheduleBackupDbImpl
{

	public static List<AhScheduleBackupData> initScheduleInfoFromDB()
	{		
		List<AhScheduleBackupData> listData = QueryUtil.executeQuery(AhScheduleBackupData.class, null, null);
		
//		if(null == listData || 0 == listData.size())
//		{
//			return setDefaultTaskdata();
//		}
//		
//		if(!((AhScheduleBackupData)listData.get(0)).getLiveFlag())
//		{
//			return setDefaultTaskdata();
//		}
//		
//		return (AhScheduleBackupData)listData.get(0);
		
		return listData;
	}

	public static boolean setScheduleInfoToDB(AhScheduleBackupData oTaskData)
	{		
		List<AhScheduleBackupData> listData = QueryUtil.executeQuery(AhScheduleBackupData.class, null, new FilterParams("owner.domainName", oTaskData.getOwner().getDomainName()));		

		try
		{
			if(listData.isEmpty())
			{
				//create
				QueryUtil.createBo(oTaskData);
				
				return true;
			}
			
			//update	
			
			AhScheduleBackupData oTmp = listData.get(0);
			
			oTmp.setBackupContent(oTaskData.getBackupContent());
			
			oTmp.setEndDate(oTaskData.getEndDate());
			
			oTmp.setEndDateFlag(oTaskData.getEndDateFlag());
			
			oTmp.setEndHour(oTaskData.getEndHour());
			
			oTmp.setEndMinute(oTaskData.getEndMinute());
			
			oTmp.setInterval(oTaskData.getInterval());
			
			oTmp.setLiveFlag(oTaskData.getLiveFlag());
			
			oTmp.setRescurFlag(oTaskData.getRescurFlag());
			
			oTmp.setScpFilePath(oTaskData.getScpFilePath());
			
			oTmp.setScpIpAdd(oTaskData.getScpIpAdd());
			
			oTmp.setScpPort(oTaskData.getScpPort());
			
			oTmp.setScpPsd(oTaskData.getScpPsd());
			
			oTmp.setScpUsr(oTaskData.getScpUsr());
			
			oTmp.setStartDate(oTaskData.getStartDate());
			
			oTmp.setStartHour(oTaskData.getStartHour());
			
			oTmp.setStartMinute(oTaskData.getStartMinute());		
			
			QueryUtil.updateBo(oTmp);
			
			return true;
		}
		catch(Exception ex)
		{
			// add log
			DebugUtil.adminDebugWarn("AhBackupSchedule.setScheduleInfoToDB() catch exception", ex);
			
			return false;
		}
	}


	public static AhScheduleBackupData setDefaultTaskdata()
	{
		AhScheduleBackupData oDefaultInfo = new AhScheduleBackupData();
		
		short sZero = 0;
		
		oDefaultInfo.setBackupContent(sZero);

		SimpleDateFormat stmp = new SimpleDateFormat("yyyy-MM-dd");

		String strDate = stmp.format(new Date());

		oDefaultInfo.setStartDate(strDate);

		short sHour = 23;
		
		oDefaultInfo.setStartHour(sHour);
		
		oDefaultInfo.setStartMinute(sZero);

		oDefaultInfo.setEndDateFlag(false);

		oDefaultInfo.setEndDate(strDate);

		oDefaultInfo.setEndHour(sHour);
		
		oDefaultInfo.setEndMinute(sZero);

		oDefaultInfo.setRescurFlag(false);

		oDefaultInfo.setInterval(0);
		
		oDefaultInfo.setScpIpAdd("0.0.0.0");

		oDefaultInfo.setScpPort(22);

		oDefaultInfo.setScpFilePath("");
		
		oDefaultInfo.setScpUsr("");
		
		oDefaultInfo.setScpPsd("");

		oDefaultInfo.setLiveFlag(false);

		return oDefaultInfo;
	}

}