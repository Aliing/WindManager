package com.ah.be.sync;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.be.app.HmBeLogUtil;
import com.ah.be.communication.RemotePortalOperationRequest;
import com.ah.be.communication.mo.UserInfo;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hhm.SyncTaskOnHmol;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.util.MgrUtil;
import com.ah.util.UserSettingsUtil;

public class TaskProcess extends Thread {
	
	private final static Log log = LogFactory.getLog("commonlog.TaskProcess");

	public static final Set<String> vhmTask = new HashSet<String>();

	public static boolean isDoingSync(String vhmName) {
		return vhmTask.contains(vhmName);
	}

	private String vhmName;

	public static void setDoingSync(String vhmName) {
		vhmTask.add(vhmName);
	}

	public static void endDoingSync(String vhmName) {
		vhmTask.remove(vhmName);
	}

	public TaskProcess(String vhmName) {
		this.vhmName = vhmName;
	}

	private static final int MAX_TRY_TIMES_FOR_COMMON = 360;
	private static final int MAX_TRY_TIMES_FOR_CREATE_VHM_USER = 3;

	@Override
	public void run() {
		List<SyncTaskOnHmol> tasks = QueryUtil.executeQuery(SyncTaskOnHmol.class, new SortParams("createTime",
				true), new FilterParams("vhmName", vhmName));
		SyncTaskOnHmol task = null;

		int maxTryTimes = -1;
		try {
			for (SyncTaskOnHmol t : tasks) {
				task = t;
				if (task.getSyncType() == SyncTaskOnHmol.SYNC_FOR_CREATE_VHMUSER) {
					maxTryTimes = MAX_TRY_TIMES_FOR_CREATE_VHM_USER;
				} else {
					maxTryTimes = MAX_TRY_TIMES_FOR_COMMON;
				}
				processSyncTask(task);
			}
		} catch (Exception e) {
			log.warn("Run sync task failed!", e);

			if (!e.getMessage().equals("Communication time out.")
					&& task.getSyncTimes() >= maxTryTimes) {
				try {
					QueryUtil.removeBoBase(task);
					String logInfo = MgrUtil.getUserMessage("hm.system.log.task.process.loginfo",new String[]{task.getSyncTaskDatas(),String.valueOf(maxTryTimes)});
					log.error(logInfo);

					// create system log
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL,
							HmSystemLog.FEATURE_ADMINISTRATION, logInfo);

				} catch (Exception e2) {
					log.error("catch exception", e2);
				}
			} else {
				task.setSyncTime(System.currentTimeMillis());
				task.setSyncTimes(task.getSyncTimes() + 1);

				try {
					QueryUtil.updateBo(task);
				} catch (Exception e2) {
					log.error("update task bo failed! " + e2.getMessage());
				}
			}
		}

		TaskProcess.endDoingSync(vhmName);
	}

	private void processSyncTask(SyncTaskOnHmol task) throws Exception {
		HmUser user = null;
		try {
			switch (task.getSyncType()) {
			case SyncTaskOnHmol.SYNC_FOR_CREATE_VHMUSER:
				// note that creating vhm user doesn't need more sync when failed, just log it
				List<HmUser> users = QueryUtil.executeQuery(HmUser.class, null, new FilterParams(
						"userName = :s1 and owner.domainName= :s2", new Object[] { task
								.getVhmUsername(),
								task.getVhmName() }));
				if (!users.isEmpty()) {
					user = users.get(0);
					RemotePortalOperationRequest.createVhmUser(UserInfo.getUserInfo(user));
				} else {
					log.warn("create vhm user: no this user:[" + task.getVhmUsername() + "]");
				}
				break;
			case SyncTaskOnHmol.SYNC_FOR_REMOVE_VHMUSER:
				RemotePortalOperationRequest
						.removeVhmUser(task.getVhmName(), task.getVhmUsername());
				break;
			case SyncTaskOnHmol.SYNC_FOR_MODIFY_VHMUSER:
				users = QueryUtil.executeQuery(HmUser.class, null, new FilterParams(
						"userName = :s1 and owner.domainName= :s2", new Object[] { task
								.getVhmUsername(),
								task.getVhmName() }));
				if (!users.isEmpty()) {
					user = users.get(0);
					RemotePortalOperationRequest.modifyVhmUser(UserInfo.getUserInfo(user));
				} else {
					log.warn("modify vhm user: no this user:[" + task.getVhmUsername() + "]");
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			String logInfo = MgrUtil.getUserMessage("hm.system.log.task.process.sync.failed.loginfo",new String[]{task.getSyncTaskDatas(),e.getMessage()})
				+ task.getSyncTimes();
			log.warn(logInfo);
			if (task.getSyncType() == SyncTaskOnHmol.SYNC_FOR_CREATE_VHMUSER) {
				if (task.getSyncTimes() == MAX_TRY_TIMES_FOR_CREATE_VHM_USER) {
					logInfo = MgrUtil.getUserMessage("hm.system.log.task.process.sync.vhm.user.failed.loginfo",new String[]{task.getVhmUsername(),e.getMessage()});
					log.error(logInfo);
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL,
							HmSystemLog.FEATURE_ADMINISTRATION, logInfo);
					/*user.setSyncResult(HmUser.SYNC_RESULT_DUPLICATED);
					QueryUtil.updateBo(user);*/
					// changed in Geneva, for user setting columns separated from hm_user
					UserSettingsUtil.updateSyncResult(user.getEmailAddress(), HmUser.SYNC_RESULT_DUPLICATED);
				}
			} else {
				if (task.getSyncTimes() == 1 || task.getSyncTimes() == 10
						|| task.getSyncTimes() % 60 == 0) {
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL,
							HmSystemLog.FEATURE_ADMINISTRATION, logInfo);
				}
			}
			throw e;
		}

		// post process
		QueryUtil.removeBoBase(task);
	}

}