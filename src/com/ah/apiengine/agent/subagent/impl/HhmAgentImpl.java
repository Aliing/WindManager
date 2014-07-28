package com.ah.apiengine.agent.subagent.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ah.apiengine.agent.HmApiEngineException;
import com.ah.apiengine.agent.subagent.HhmAgent;
import com.ah.apiengine.element.HhmInfo;
import com.ah.apiengine.element.HhmList;
import com.ah.apiengine.element.StringList;
import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HASettings;
import com.ah.bo.hhm.DenyUpgradeEmailSuffix;
import com.ah.bo.hhm.HhmUpgradeVersionInfo;
import com.ah.bo.mgmt.QueryUtil;

public class HhmAgentImpl implements HhmAgent {

	@Override
	public void updateHhmList(HhmList hhmList) throws HmApiEngineException {
		if (null != hhmList && null != hhmList.getHhms()) {
			try {
				// remove the old records
				QueryUtil.bulkRemoveBos(HhmUpgradeVersionInfo.class, null);
				// insert the new records
				QueryUtil.bulkCreateBos(hhmList.getHhms());
			} catch (Exception e) {
				DebugUtil.commonDebugError("HhmAgentImpl.updateHhmInfoList() : Insert new hhm version info error (" + e.getMessage() + ")");
			}
		}
	}

	@Override
	public HhmInfo queryHhmInfo() throws HmApiEngineException {
		try {
			HhmInfo info = new HhmInfo();
			
			// version info
			BeVersionInfo versionInfo = NmsUtil.getVersionInfo();
			info.setVersion(versionInfo.getMainVersion() + "r" + versionInfo.getSubVersion());
			
			// buildTime info
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date buildTime = format.parse(versionInfo.getBuildTime());
			info.setBuildTime((int)(buildTime.getTime() / 1000));
			
			// modelNum info
			info.setModelNum(HmBeAdminUtil.getHmKernelModel());
			
			// systemUpTime
			info.setSystemUpTime((int)HmBeOsUtil.getStartupTime());
			
			// ha status
			byte haStatus = HASettings.HASTATUS_DIABLE;
			List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, null);
			if (!list.isEmpty()) {
				HASettings haSettings = list.get(0);
				haStatus = haSettings.getHaStatus();
			}
			info.setHaStatus(haStatus);
			
//			LicenseInfo licInfo = HmBeLicenseUtil.getLicenseInfo();
//			// maybe there is no license information
//			if (null != licInfo) {
//				// max ap number
//				info.setMaxApNum(licInfo.getHiveAps());
//				
//				// max vhm number
//				info.setMaxVhmNum((short)licInfo.getVhmNumber());
//			}
			
			return info;
		} catch (Exception e) {
			DebugUtil.commonDebugError("HhmAgentImpl.queryHhmInfo() : catch exception.",e);
			throw new HmApiEngineException("Query HHM info error. "+ e.getMessage());
		}
	}

	@Override
	public void updateDenyEmailInfo(StringList mailList) throws HmApiEngineException {
		if (null != mailList && null != mailList.getStrs()) {
			List<DenyUpgradeEmailSuffix> allEmails = new ArrayList<DenyUpgradeEmailSuffix>(mailList.getStrs().size());

			for (String suffix : mailList.getStrs()) {
				DenyUpgradeEmailSuffix oneSuffix = new DenyUpgradeEmailSuffix();
				oneSuffix.setEmailSuffix(suffix);
				allEmails.add(oneSuffix);
			}
			
			try {
				// remove the old records
				QueryUtil.bulkRemoveBos(DenyUpgradeEmailSuffix.class, null);
				// insert the new records
				QueryUtil.bulkCreateBos(allEmails);
			} catch (Exception e) {
				DebugUtil.commonDebugError("HhmAgentImpl.updateDenyEmailInfo() : Insert deny upgrade email suffix info error (" + e.getMessage() + ")");
			}
		}
	}

}