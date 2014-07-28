package com.ah.util.notificationmsg.message;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.ui.actions.HmMenuAction;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.MgrUtil;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgButton;
import com.ah.util.notificationmsg.AhNotificationMsgUtil;

public class HmLicenceWarningMsg extends AhNotificationMessage {

    @Override
    public int initPriority() {
        return LICENCE_WARING_MSG_PRIORITY;
    }
    
    @Override
    public boolean isDisplayFlagOn(HmUser userContext) {
        boolean flag = false;
        if (null != userContext) {
            HmDomain hmDomain = AhNotificationMsgUtil.getCurrentDomain(userContext);
            if(HmDomain.HOME_DOMAIN.equals(hmDomain.getDomainName())) {
                flag = userContext.isSuperUser();
            } else {
                if (NmsUtil.isHostedHMApplication()) {
                    flag = userContext.getDefaultFlag();
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
        this.contents = (String) MgrUtil.getSessionAttribute(SessionKeys.LICENSE_INFO_IN_TITLE_AREA);
        return StringUtils.isNotBlank(contents);
    }

    @Override
    public void build(HmUser userContext) {
        this.contents = "Warning: " +this.contents;
        if(this.contents.contains("You must enter a valid")
                && !this.contents.contains("or entitlement key within")) {
            this.contents = this.contents + getEmptyString(5);
        }
        // buttons
        List<AhNotificationMsgButton> buttons = getActionButtonsDescByUserContext(userContext);
        this.actionButtons.addAll(buttons);
        // close icon
        if(null != userContext && userContext.getId() > 0L) {
            this.closeButton = new AhNotificationMsgButton("Hide this message.", "hideMessageInSession()");
        }
    }
    
/*-    @Override
    public String getMsgStyle() {
        if(this.contents.contains("You must enter a valid")) {
            return "text-align: center;";
        }
        return null;
    }
*/
    private String getEmptyString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append("&nbsp;");
        }
        return builder.toString();
    }

    private List<AhNotificationMsgButton> getActionButtonsDescByUserContext(HmUser userContext) {
        List<AhNotificationMsgButton> buttons = new ArrayList<AhNotificationMsgButton>();
        buttons.add(new AhNotificationMsgButton("Enter Now", "changeEnterPanel(true)"));
        return buttons;
    }

    @Override
    public boolean refresh(HmUser userContext, Object action) {
        boolean flag = init(userContext);
        if(null != action && action instanceof HmMenuAction) {
            String operation = ((HmMenuAction)action).getOperation();
            if("licenseMgr".equals(operation)) {
                this.enableDisplay = false;
            }
        }
        return flag;
    }

    @Override
    public boolean disableDisplay(HmUser userContext) {
        MgrUtil.removeSessionAttribute(SessionKeys.LICENSE_INFO_IN_TITLE_AREA);
        return true;
    }

}
