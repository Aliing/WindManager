// $Id: AhNotificationMsgPool.java,v 1.13.22.2.4.2.8.1 2014/03/14 09:26:43 huihe Exp $
package com.ah.util.notificationmsg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.notificationmsg.NotificationMessageStatus;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.HmMenuAction;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.notificationmsg.message.HmCriticalAlarmWarningMsg;
import com.ah.util.notificationmsg.message.HmDeviceOlderVersionMsg;
import com.ah.util.notificationmsg.message.HmDisableL7Msg;
import com.ah.util.notificationmsg.message.HmExGuideConfigChangeMsg;
import com.ah.util.notificationmsg.message.HmLicenceWarningMsg;
import com.ah.util.notificationmsg.message.HmNotifyClientManagerWarningMsg;
import com.ah.util.notificationmsg.message.HmPBRWarningMsg;
import com.ah.util.notificationmsg.message.HmPassiveNodeWarningMsg;
import com.ah.util.notificationmsg.message.HmTCADiskUsageWarningMSG;
import com.ah.util.notificationmsg.message.HmUpgradeLogMsg;
import com.ah.util.notificationmsg.message.HmWatchlistCleanMsg;
import com.ah.util.notificationmsg.message.HmWatchlistUpdateMsg;
import com.ah.util.values.BooleanMsgPair;

public class AhNotificationMsgPool {
    private static final Tracer log = new Tracer(AhNotificationMsgPool.class.getSimpleName());
    
    private final AhNotificationMsgComparator comparator = new AhNotificationMsgComparator();
    private TreeSet<AhNotificationMessage> messages = new TreeSet<AhNotificationMessage>(comparator);
    
    private boolean enableLicenceMsgFlag;
    
    public AhNotificationMsgPool() {
        this(null);
    }

    public AhNotificationMsgPool(HmUser userContext) {
       if(null == userContext) {
           userContext = BaseAction.getSessionUserContext();
       }
       
       List<AhNotificationMessage> msgList = new ArrayList<AhNotificationMessage>();
       // add the notification messages
       if (HAUtil.isSlave()) {
           // passive node only has the warning message
    	   msgList.add(new HmPassiveNodeWarningMsg());
       } else {
           msgList.add(new HmExGuideConfigChangeMsg());
           msgList.add(new HmDeviceOlderVersionMsg());
           msgList.add(new HmLicenceWarningMsg());
           msgList.add(new HmPBRWarningMsg());
           msgList.add(new HmWatchlistCleanMsg());
           msgList.add(new HmWatchlistUpdateMsg());
           msgList.add(new HmDisableL7Msg());
           msgList.add(new HmCriticalAlarmWarningMsg());
           msgList.add(new HmUpgradeLogMsg());
           
           // for client manager warn msg in session
           MgrUtil.setSessionAttribute(SessionKeys.NOTIFICATION_MESSAGE_ENABLEACM+userContext.getOwner().getDomainName(), 
        		   MgrUtil.getUserMessage("glasgow_14.notification.message.client.manager"));
           msgList.add(new HmNotifyClientManagerWarningMsg());
       }
       if( !NmsUtil.isHostedHMApplication()){
    	   msgList.add(new HmTCADiskUsageWarningMSG());
       }
       
       initMsgPool(msgList, userContext);
    }
    
    private void initMsgPool(List<AhNotificationMessage> msgList, HmUser userContext) {
        if (null != userContext && null != userContext.getDomain()) {
            HmDomain hmDomain = AhNotificationMsgUtil.getCurrentDomain(userContext);
            if (hmDomain.getDomainName().equals(userContext.getDomain().getDomainName())) {
                try {
                    // update message pool
                    updateNotificationDefinedMsgPool(msgList, userContext);
                    // add new message
                    addMsgs(msgList, userContext);
                } catch (Exception e) {
                    log.error("initMsgPool()", "Error when initialize the notification message pool", e);
                }
            }
        }
    }

    /**
     * Update message display status and priorities.
     * @author Yunzhi Lin
     * - Time: Dec 15, 2011 2:14:44 PM
     * @param msgList - messages
     * @param userContext - user
     * @throws Exception
     */
    private void updateNotificationDefinedMsgPool(List<AhNotificationMessage> msgList, HmUser userContext)
            throws Exception {
        if(HAUtil.isSlave()) {
            // do nothing in the HA slave mode
            return;
        }
        NotificationMessageStatus msgDisplayStatus = QueryUtil.findBoByAttribute(
                NotificationMessageStatus.class, "userEmail", userContext.getEmailAddress(), 
                userContext.getDomain().getId());
        if (null == msgDisplayStatus) {
            msgDisplayStatus = new NotificationMessageStatus();
            msgDisplayStatus.setUserEmail(userContext.getEmailAddress());
            msgDisplayStatus.setOwner(userContext.getDomain());
        }
        if(!msgList.isEmpty()) {
            int[] newMsgPriorities = new int[msgList.size()];
            int index = 0;
            for (AhNotificationMessage msg : msgList) {
                newMsgPriorities[index++] = msg.initPriority();
            }
            BooleanMsgPair result = msgDisplayStatus.updateLastDefinedMsgPriorities(newMsgPriorities);
            if(result.getValue()) {
                if(null == msgDisplayStatus.getId() || msgDisplayStatus.getId() == 0L) {
                    QueryUtil.createBo(msgDisplayStatus);
                } else {
                    QueryUtil.updateBo(msgDisplayStatus);
                }
                log.debug(result.getDesc());
            } else {
                log.warn(result.getDesc());
            }
        }
    }

    public void addMsgs(List<AhNotificationMessage> msgList, HmUser userContext) {
        for (AhNotificationMessage msg : msgList) {
            addMsg(msg, userContext);
        }
    }
    
    public boolean addMsg(AhNotificationMessage msg, HmUser userContext) {
        boolean flag = false;
        if (null != userContext) {
            if (msg.init(userContext)) {
                log.info("Init the notification message successful.");
                specificMsgFlag(msg);
            } else {
                log.warn("Init the notification message failure. Please check the message="+msg.getClass().getSimpleName());
            }
            flag = messages.add(msg);
        }
        return flag;
    }
    
    /**
     * Disable the specific message by invoke the  {@link AhNotificationMessage}.disableDisplay() method.
     * @author Yunzhi Lin
     * - Time: Dec 7, 2011 5:42:24 PM
     * @param msg
     * @param userContext
     * @return True | False
     */
    public boolean disableMsg(AhNotificationMessage msg, HmUser userContext) {
        if(null != userContext && msg.disableDisplay(userContext)) {
            // set the display flag to false for the message in the pool
            msg.setEnableDisplay(false);
            return true;
        }
        return false;
    }

    /**
     * Refresh the notification message set when the server get a 'POST' request from browser client. <br>
     * It will recursive invoke the {@link AhNotificationMessage}.refresh() method to refresh the message.
     * @param action {@link HmMenuAction}
     * @author Yunzhi Lin
     * - Time: Nov 28, 2011 2:59:25 PM
     */
    public void refreshMsgs(Object action) {
        HmUser userContext = BaseAction.getSessionUserContext();
        if(null == userContext) return;
        for (Iterator<AhNotificationMessage> iterator = messages.iterator(); iterator.hasNext();) {
            AhNotificationMessage msg = iterator.next();
            msg.refresh(userContext, action);
        }
    }
    
    public TreeSet<AhNotificationMessage> getCurrentMessages() {
        return this.messages;
    }
    
    public TreeSet<AhNotificationMessage> getCurrentAvailableMessages() {
        TreeSet<AhNotificationMessage> availableMsgs = new TreeSet<AhNotificationMessage>(comparator);
        for (Iterator<AhNotificationMessage> iterator = messages.iterator(); iterator.hasNext();) {
            AhNotificationMessage msg = iterator.next();
            if(msg.enableDisplay) {
                availableMsgs.add(msg);
            }
        }
        return availableMsgs;
    }
    
    public boolean isEnableLicenceMsgFlag() {
        return enableLicenceMsgFlag;
    }
    
    private void specificMsgFlag(AhNotificationMessage msg) {
        //TODO use common method to handle this situation.
        if(msg.getPriority() == AhNotificationMessage.LICENCE_WARING_MSG_PRIORITY) {
            enableLicenceMsgFlag = true;
        }
    }
    
    private final class AhNotificationMsgComparator implements Comparator<AhNotificationMessage> {
        @Override
           public int compare(AhNotificationMessage msg1, AhNotificationMessage msg2) {
               if(null != msg1 && null != msg2) {
                   return msg1.getPriority() - msg2.getPriority();
               } else {
                   // It will treat the msg1 and msg2 as same message and adding operation will be failure 
                   // if the priority of msg2 is equal to msg1's 
                   return 0;
               }
           }
    }
    
}
