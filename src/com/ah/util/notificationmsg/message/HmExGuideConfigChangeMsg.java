package com.ah.util.notificationmsg.message;

import java.util.List;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.MgrUtil;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgButton;
import com.ah.util.notificationmsg.AhNotificationMsgUtil;

public class HmExGuideConfigChangeMsg extends AhNotificationMessage {

    @Override
    public int initPriority() {
        return EXPRESS_GUIDCONFIG_CHANGE_WARING_MSG_PRIORITY;
    }

    @Override
    public boolean isDisplayFlagOn(HmUser userContext) {
        if (null != userContext && null != userContext.getDomain()) {
            try {
                HmDomain hmDomain = AhNotificationMsgUtil.getCurrentDomain(userContext);
                if (hmDomain.getDomainName().equals(userContext.getDomain().getDomainName())) {
                    HmStartConfig config = QueryUtil.findBoByAttribute(HmStartConfig.class,
                            "owner", userContext.getDomain());
                    if (null != config && config.getModeType() == HmStartConfig.HM_MODE_EASY) {
                        String querySql = "select hostname from hive_ap where managestatus = 1 and connected = true"
                                + " and owner = " + userContext.getDomain().getId();
                        List<?> list = QueryUtil.executeNativeQuery(querySql, 1);
                        return !list.isEmpty();
                    }
                }
            } catch (Exception e) {
                log.error("Error to get the app mode type when query the database", e);
            }
        }
        return false;
    }

    @Override
    public boolean isNeedBuild(HmUser userContext) {
        return MgrUtil.getSessionAttribute(SessionKeys.GUIDED_CONFIG_WARNING_MSG) != null;
    }

    @Override
    public void build(HmUser userContext) {
        this.contents = MgrUtil.getUserMessage("config.guided.warning.msg");
        AhNotificationMsgButton button = new AhNotificationMsgButton("Upload", "gotoUploadPanel()");
        this.actionButtons.add(button);
        
    }
    
/*-    @Override
    public String getMsgStyle() {
        return "max-width: 470px;";
    }
*/
    @Override
    public boolean refresh(HmUser userContext, Object action) {
        return init(userContext);
    }

    @Override
    public boolean disableDisplay(HmUser userContext) {
        MgrUtil.removeSessionAttribute(SessionKeys.GUIDED_CONFIG_WARNING_MSG);
        return true;
    }

}
