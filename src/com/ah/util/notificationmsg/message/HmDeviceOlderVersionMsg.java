package com.ah.util.notificationmsg.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.notificationmsg.NotificationMessageStatus;
import com.ah.bo.notificationmsg.NotificationMessageStatus.AhMsgDisplayFlag;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgButton;
import com.ah.util.notificationmsg.AhNotificationMsgUtil;
import com.ah.util.values.BooleanMsgPair;

public class HmDeviceOlderVersionMsg extends AhNotificationMessage {

	@Override
	public int initPriority() {
	    return OLDERVER_WARING_MSG_PRIORITY;
	}
	
    @Override
    public boolean isDisplayFlagOn(HmUser userContext) {
        // get the flag in database/session to check if it's necessary to display this warning message.
        boolean flag = false;
        if (null != userContext && null != userContext.getDomain()) {
            HmDomain hmDomain = AhNotificationMsgUtil.getCurrentDomain(userContext);
            if(hmDomain.getDomainName().equals(userContext.getDomain().getDomainName())) {
                try {
                    NotificationMessageStatus msgDisplayStatus = QueryUtil.findBoByAttribute(
                            NotificationMessageStatus.class, "userEmail", userContext.getEmailAddress(), 
                            userContext.getDomain().getId());
                    if (null == msgDisplayStatus) {
                        flag = true;
                    } else {
                        if (AhMsgDisplayFlag.DISPLAY == msgDisplayStatus.getMsgDisplayStatus(this.priority)) {
                            flag = true;
                        }
                    }
                } catch (Exception e) {
                    log.error("isEnableDisplay()", "Error when get the msg display status", e);
                }
            }
        }
        return flag;
    }

	@Override
    public boolean isNeedBuild(HmUser userContext) {
        boolean flag = false;
        flag = isExistOldVersionDevice(userContext);
        return flag;
    }

	/**
	 * Compare the version of devices which are connected to HM, check if there are the older version devices exist.
	 * @author Yunzhi Lin
	 * - Time: Dec 1, 2011 1:54:16 PM
	 * @param userContext
	 * @return <code>True or False</code>
	 */
    private boolean isExistOldVersionDevice(HmUser userContext) {
        Map<EnumItem, String> versionMap = AhNotificationMsgUtil.getLatestDeviceSupportVersionMap();
        if(!versionMap.isEmpty() && null != userContext.getDomain()) {
            StringBuilder sql = new StringBuilder();
            String basicQuerySentence = "select hostname, hiveapmodel, softver, connected, simulated, managestatus, owner " +
            		" from hive_ap where managestatus = 1 and connected = true and simulated = false " +
            		" and owner = " + userContext.getDomain().getId();
//            String basicQuerySentence = "select hostname, hiveapmodel, softver, connected, simulated, managestatus, owner " +
//            " from hive_ap where managestatus = 0 and simulated = false " +
//            " and owner = " + userContext.getDomain().getId();
            sql.append(basicQuerySentence);
            sql.append(" and (");
            for (EnumItem item : versionMap.keySet()) {
                sql.append("(");
                sql.append(" hiveapmodel = " + item.getKey());
                sql.append(" and softver <> \'" + versionMap.get(item).toString()+"\'");
                sql.append(")");
                sql.append(" or ");
            }
            sql.delete(sql.length()-4, sql.length());
            sql.append(")");
            //debug("query sql sentence: "+sql);
            List<?> list = QueryUtil.executeNativeQuery(sql.toString(), 1);
            if(!list.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void build(HmUser userContext) {
        this.contents = MgrUtil.getUserMessage("notification.message.osversions.contents");
        // buttons
        List<AhNotificationMsgButton> buttons = getActionButtonsDescByUserContext(userContext);
        this.actionButtons.addAll(buttons);
        // close icon
        if(null != userContext) {
            this.closeButton = new AhNotificationMsgButton(
                    MgrUtil.getUserMessage("notification.message.osversions.close.tips"),
                    "noDisplayOlderVersion()");
        }
    }

    @Override
    public boolean refresh(HmUser userContext, Object action) {
        return init(userContext);
    }

	private List<AhNotificationMsgButton> getActionButtonsDescByUserContext(HmUser userContext) {
		// get the buttons description by user context. For VA user, it should not display the 'Donot Show' buttons.
		List<AhNotificationMsgButton> buttons = new ArrayList<AhNotificationMsgButton>();
		buttons.add(new AhNotificationMsgButton("click here for more info", "olderVersionMoreDetails()"));
		return buttons;
	}
	
    @Override
    public boolean disableDisplay(HmUser userContext) {
        boolean flag = false;
        if (null != userContext && null != userContext.getDomain()) {
            try {
                NotificationMessageStatus msgDisplayStatus = QueryUtil.findBoByAttribute(
                        NotificationMessageStatus.class, "userEmail", userContext.getEmailAddress(), 
                        userContext.getDomain().getId());
                if (null == msgDisplayStatus) {
                    msgDisplayStatus = new NotificationMessageStatus();
                    msgDisplayStatus.setUserEmail(userContext.getEmailAddress());
                    msgDisplayStatus.setOwner(userContext.getDomain());
                }
                BooleanMsgPair result = msgDisplayStatus.updateMsgDisplayStatus(this.priority, AhMsgDisplayFlag.NODISPLAY);
                if(flag = result.getValue()) {
                    debug(result.getDesc());
                    if(null == msgDisplayStatus.getId() || msgDisplayStatus.getId() == 0L) {
                        QueryUtil.createBo(msgDisplayStatus);
                    } else {
                        QueryUtil.updateBo(msgDisplayStatus);
                    }
                } else {
                    log.error("disableDisplay()", result.getDesc());
                }
                
            } catch (Exception e) {
                log.error("disableDisplay()", "Error when get the msg display status", e);
            }
        }
        return flag;
    }
}
