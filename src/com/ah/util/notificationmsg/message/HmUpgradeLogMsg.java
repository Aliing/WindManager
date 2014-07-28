package com.ah.util.notificationmsg.message;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.util.MgrUtil;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgButton;
import com.ah.util.notificationmsg.AhNotificationMsgUtil;

public class HmUpgradeLogMsg extends AhNotificationMessage {

	@Override
	public int initPriority() {
		return UPGRADE_LOG_MSG_PRIORITY;
	}

	@Override
	public boolean isDisplayFlagOn(HmUser userContext) { 
        boolean flag = false;
        if (null != userContext && null != userContext.getDomain()) {
            HmDomain hmDomain = AhNotificationMsgUtil.getCurrentDomain(userContext);
            if(hmDomain.getDomainName().equals(userContext.getDomain().getDomainName())) {
               flag = true;
            }
        }
        return flag;
	}

	@Override
	public boolean isNeedBuild(HmUser userContext) {
		return NmsUtil.isShowUpdateLog(getDomain());
	}

	@Override
	public void build(HmUser userContext) {
        this.contents = MgrUtil.getUserMessage("guadalupe_17.notification.message.hmupgradelog.contents");        
        // buttons
        this.actionButtons.add(new AhNotificationMsgButton("Go", "goToHmUpgradeLogList()"));
        // close icon
        if(null != userContext) {
            this.closeButton = new AhNotificationMsgButton(MgrUtil.getUserMessage("notification.message.osversions.close.tips"),
                    "closeHmUpgradeLogMsg()");
        }
	}
	
	@Override
	public boolean refresh(HmUser userContext, Object action) {
		return init(userContext);
	}

	@Override
	public boolean disableDisplay(HmUser userContext) {
		NmsUtil.clearShowUpdateLogFlag(getDomain());
		return true;
	}

}
