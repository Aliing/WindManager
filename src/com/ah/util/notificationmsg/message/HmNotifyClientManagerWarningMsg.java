/**
 *@filename		HmNotifyClientManagerWarningMsg.java
 *@version
 *@author		Fiona
 *@createtime	Sep 25, 2013 1:19:49 PM
 *Copyright (c) 2006-2013 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.util.notificationmsg.message;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.MgrUtil;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgButton;
import com.ah.util.notificationmsg.AhNotificationMsgUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class HmNotifyClientManagerWarningMsg extends AhNotificationMessage {
	@Override
    public int initPriority() {
        return CLIENT_MANAGER_MSG_PRIORITY;
    }
    
    @Override
    public boolean isDisplayFlagOn(HmUser userContext) {
        boolean flag = false;
        if (null != userContext) {
            HmDomain hmDomain = AhNotificationMsgUtil.getCurrentDomain(userContext);
            if(NmsUtil.isHostedHMApplication() && HmDomain.HOME_DOMAIN.equals(hmDomain.getDomainName())) {
            } else if (HmStartConfig.HM_MODE_FULL == userContext.getMode()){
            	// get the enable client manager flag
				List<?> acmEnables = QueryUtil.executeQuery("select enableClientManagement from "+HMServicesSettings.class.getSimpleName(), 
						null, new FilterParams("owner.domainName", hmDomain.getDomainName()));
				if (null == acmEnables || acmEnables.isEmpty()) {
					flag = true;
				} else {
					flag = !(Boolean)acmEnables.get(0);
				}
            }
        }
        return flag;
    }

    @Override
    public boolean isNeedBuild(HmUser userContext) {
        if (null == userContext) {
            return false;
        }
        this.contents = (String)MgrUtil.getSessionAttribute(SessionKeys.NOTIFICATION_MESSAGE_ENABLEACM+userContext.getOwner().getDomainName());
        return StringUtils.isNotBlank(contents);
    }

    @Override
    public void build(HmUser userContext) {
        // buttons
        this.actionButtons.add(new AhNotificationMsgButton("Go", "goToEnableClientManager()"));
        // close icon
        if(null != userContext) {
            this.closeButton = new AhNotificationMsgButton(MgrUtil.getUserMessage("notification.message.osversions.close.tips"),
                    "noDisplayClientManager()");
        }
    }

    @Override
    public boolean refresh(HmUser userContext, Object action) {
        return init(userContext);
    }

	@Override
	public boolean disableDisplay(HmUser userContext) {
		MgrUtil.removeSessionAttribute(SessionKeys.NOTIFICATION_MESSAGE_ENABLEACM+userContext.getOwner().getDomainName());
        return true;
	}

}
