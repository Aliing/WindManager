/**
 *@filename		AhNMSBackupTask.java
 *@version
 *@author		lanbao
 *@createtime	Jul 26, 2007 3:47:31 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.admin.schedulebackup;

/**
 * @author lanbao
 * @version V1.0.0.0
 */
import java.util.TimerTask;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.protocol.AhFtpClient;
import com.ah.be.protocol.ssh.scp.AhScpMgmt;
import com.ah.be.protocol.ssh.scp.AhScpMgmtImpl;
import com.ah.bo.admin.AhScheduleBackupData;
import com.ah.bo.admin.HmSystemLog;
import com.ah.util.EnumConstUtil;
import com.ah.util.MgrUtil;

public class AhNMSBackupTask extends TimerTask
{
	
	private final AhScheduleBackupData m_oBackupDTO;

	private final AhBackupWatchIF		m_oAhBackupWatchIF;

	public AhNMSBackupTask(
		AhScheduleBackupData oAhScheduleBackupDTO,
							AhBackupWatchIF oAhBackupWatchIF)
	{
		m_oBackupDTO = oAhScheduleBackupDTO;

		m_oAhBackupWatchIF = oAhBackupWatchIF;
	}
	
	public AhScheduleBackupData getTaskData()
	{
		return m_oBackupDTO;
	}

	public void run()
	{
		excuteBackup();

		m_oAhBackupWatchIF.watchBackupTask(this);
	}

	private void excuteBackup()
	{
		String strFileName;
		AhScpMgmt scp = null;
		AhFtpClient ftp = null;
		
		try
		{
		    //strFileName = BeOperateHMCentOSImpl.execBackupScript(m_oBackupDTO.getBackupContent());

			if("home".equalsIgnoreCase(m_oBackupDTO.getOwner().getDomainName()))
			{
				strFileName = BeOperateHMCentOSImpl.backupFullData(m_oBackupDTO.getBackupContent());
			}
			else
			{
				strFileName = BeOperateHMCentOSImpl.backupDomainData(m_oBackupDTO.getOwner(), m_oBackupDTO.getBackupContent());
			}
			
			String strErrmsg = "backup_in_progress";

			String strCmd = "chmod u+x "+ BeAdminCentOSTools.ahShellRoot+"/ahScheduleBackupStore.sh";

			BeAdminCentOSTools.execCmdWithErr(strCmd,strErrmsg);

			//strCmd = "sh ./bin/ahScheduleBackupStore.sh  " + strFileName;
			strCmd = "sh "+BeAdminCentOSTools.ahShellRoot+"/ahScheduleBackupStore.sh "+ strFileName;
			
			BeAdminCentOSTools.execCmdWithErr(strCmd, strErrmsg);

			//store scp server
			if (m_oBackupDTO.getProtocol() == EnumConstUtil.RESTORE_PROTOCOL_SCP) {
				scp = new AhScpMgmtImpl(m_oBackupDTO.getScpIpAdd(), m_oBackupDTO.getScpPort(),
						m_oBackupDTO.getScpUsr(), m_oBackupDTO.getScpPsd());
				scp.scpPut(AhNMSBackupStoreTool.BACKUP_SCHEDULE_STORE + "/" + strFileName,
						m_oBackupDTO.getScpFilePath());
			} else if (m_oBackupDTO.getProtocol() == EnumConstUtil.RESTORE_PROTOCOL_FTP) {
				ftp = new AhFtpClient();
				ftp.open(m_oBackupDTO.getScpIpAdd(), m_oBackupDTO.getScpPort(), m_oBackupDTO
						.getScpUsr(), m_oBackupDTO.getScpPsd());
				boolean isSucc = ftp.upload(m_oBackupDTO.getScpFilePath() + "/" + strFileName,
						AhNMSBackupStoreTool.BACKUP_SCHEDULE_STORE + "/" + strFileName);
				if (!isSucc) {
					DebugUtil
							.adminDebugWarn("AhNMSBackupTask.excuteBackup(): unable to transfer schedule backup package via ftp.");
				}
			}

			AhNMSBackupStoreTool
				.removeFile(AhNMSBackupStoreTool.BACKUP_SCHEDULE_STORE + "/"
					+ strFileName);
			
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION, MgrUtil.getUserMessage("hm.system.log.ah.nms.backup.task.success")+strFileName);
		}
		catch (Exception ex)
		{
			//add log
			DebugUtil.adminDebugWarn("AhBackupSchedule.excuteBackup() catch exception", ex);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION, MgrUtil.getUserMessage("hm.system.log.ah.nms.backup.task.failure")+ ex.getMessage());
		} finally {
			try {
				if (scp != null) {
					scp.close();
				}
				if (ftp != null) {
					ftp.close();
				}
			} catch (Exception e) {
				// do nothing
			}
		}
	}

}