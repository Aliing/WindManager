/**
 *@filename		AhNMSBackupStoreTool.java
 *@version
 *@author		lanbao
 *@createtime	Jul 26, 2007 7:24:52 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.admin.schedulebackup;

import java.io.File; 


/**
 * @author lanbao
 * @version V1.0.0.0
 */
public class AhNMSBackupStoreTool
{
	//public static String	BACKUP_SCHEDULE_STORE		= "./webapps/ROOT/WEB-INF/downloads/backupstorage";
    public static String	BACKUP_SCHEDULE_STORE		= System.getenv("HM_ROOT")+"/WEB-INF/downloads/backupstorage";
	
	public static int		BACKUP_SCHEDULE_FILECOUNT	= 14;

	
	public static void removeFile(String filePath)
	{
		File file = new File(filePath);

		if (file.exists())
		{
			file.delete();
		}
	}

	
	public static void delbackupFile(String strFileName)
	{
		removeFile(BACKUP_SCHEDULE_STORE + File.separator + strFileName);
	}

	public static void main(String args[])
	{
		//System.out.println(AhNMSBackupStoreTool.getBackupFileNames()[0]);
	}

}