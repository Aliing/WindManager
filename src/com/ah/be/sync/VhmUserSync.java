package com.ah.be.sync;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.be.app.HmBeLogUtil;
import com.ah.be.communication.mo.UserInfo;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hhm.SyncTaskOnHmol;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;

public class VhmUserSync {
	private final static Log log = LogFactory.getLog("commonlog.VhmUserSync");

	public static void syncAllUsersAfterRestoreFromWholeHm34() {
		List<HmUser> users = QueryUtil.executeQuery(HmUser.class, null, new FilterParams(
				"owner.domainName<>:s1 and owner.domainName<>:s2",
				new Object[] { "home", "global" }));
		for (HmUser user : users) {
			SyncTaskOnHmol task = new SyncTaskOnHmol();

			task.setSyncType(SyncTaskOnHmol.SYNC_FOR_CREATE_VHMUSER);
			task.setVhmName(user.getDomain().getDomainName());
			task.setVhmUsername(user.getUserName());

			createSyncToAsTask(task);
		}
	}

	public static void syncVhmUserAfterRestoreVhm(String vhmName) {
		List<HmUser> users = QueryUtil
				.findBosByCondition(HmUser.class, "owner.domainName", vhmName);
		for (HmUser user : users) {
			SyncTaskOnHmol task = new SyncTaskOnHmol();

			task.setSyncType(SyncTaskOnHmol.SYNC_FOR_CREATE_VHMUSER);
			task.setVhmName(vhmName);
			task.setVhmUsername(user.getUserName());

			createSyncToAsTask(task);
		}
	}

	public static void syncForModifyVhmUser(UserInfo userInfo) {
		SyncTaskOnHmol task = new SyncTaskOnHmol();

		task.setSyncType(SyncTaskOnHmol.SYNC_FOR_MODIFY_VHMUSER);
		task.setVhmName(userInfo.getVhmName());
		task.setVhmUsername(userInfo.getUsername());

		createSyncToAsTask(task);
	}

	public static void syncForRemoveVhmUser(String vhmName, String username) {
		SyncTaskOnHmol task = new SyncTaskOnHmol();

		task.setSyncType(SyncTaskOnHmol.SYNC_FOR_REMOVE_VHMUSER);
		task.setVhmName(vhmName);
		task.setVhmUsername(username);

		createSyncToAsTask(task);
	}

	private static void createSyncToAsTask(SyncTaskOnHmol task) {
		task.setCreateTime(System.currentTimeMillis());
		task.setOwner(null);

		try {
			QueryUtil.createBo(task);
		} catch (Exception e) {
			String logInfo = MgrUtil.getUserMessage("hm.system.log.vhm.user.sync.create.failed.error",task.getSyncTaskDatas());
			log.error(logInfo, e);

			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL,
					HmSystemLog.FEATURE_ADMINISTRATION, logInfo);
		}
	}

}
