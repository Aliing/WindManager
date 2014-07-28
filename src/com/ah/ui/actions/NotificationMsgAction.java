package com.ah.ui.actions;

import java.util.TreeSet;

import org.json.JSONObject;

import com.ah.util.MgrUtil;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgPool;

@SuppressWarnings("serial")
public class NotificationMsgAction extends BaseAction {
    @Override
    public String execute() throws Exception {
        jsonObject = new JSONObject();
        AhNotificationMsgPool msgPool = getSessionNotificationMessagePool();
        TreeSet<AhNotificationMessage> msgs = msgPool.getCurrentMessages();
        AhNotificationMessage removableMsg = null;
        if("noDisplayOlderVer".equals(operation)) {
            for (AhNotificationMessage msg : msgs) {
                if (msg.getPriority() == AhNotificationMessage.OLDERVER_WARING_MSG_PRIORITY) {
                    removableMsg = msg;
                    break;
                }
            }
            if(null != removableMsg) {
                if(msgPool.disableMsg(removableMsg, getUserContext())) {
                    jsonObject.put("succ", true);
                }
            }
        } else if("removeLsMessageInSession".equals(operation)) {
            for (AhNotificationMessage msg : msgs) {
                if (msg.getPriority() == AhNotificationMessage.LICENCE_WARING_MSG_PRIORITY) {
                    removableMsg = msg;
                    break;
                }
            }
            if(null != removableMsg) {
               msgPool.disableMsg(removableMsg, getUserContext());
            }
            if(null == MgrUtil.getSessionAttribute(SessionKeys.LICENSE_INFO_IN_TITLE_AREA)) {
                jsonObject.put("succ", true);
            }
        } else if("removeTCADiskFullMessageInSession".equals(operation)){
            for (AhNotificationMessage msg : msgs) {
                if (msg.getPriority() == AhNotificationMessage.TCAALARM_WARNING_MSG_PRIORITY) {
                    removableMsg = msg;
                    break;
                }
            }
            if(null != removableMsg) {
               msgPool.disableMsg(removableMsg, getUserContext());
               jsonObject.put("succ", true);
            }
               
        } else if("closeNotifyWatchlistClean".equals(operation)){
            for (AhNotificationMessage msg : msgs) {
                if (msg.getPriority() == AhNotificationMessage.CLEAN_WATCHLIST_PRIORITY) {
                    removableMsg = msg;
                    break;
                }
            }
            if(null != removableMsg) {
               msgPool.disableMsg(removableMsg, getUserContext());
               jsonObject.put("succ", true);
            }
        } else if("closeNotifyWatchlistUpdate".equals(operation)){
            for (AhNotificationMessage msg : msgs) {
                if (msg.getPriority() == AhNotificationMessage.UPDATE_WATCHLIST_PRIORITY) {
                    removableMsg = msg;
                    break;
                }
            }
            if(null != removableMsg) {
               msgPool.disableMsg(removableMsg, getUserContext());
               jsonObject.put("succ", true);
            }
        } else if("closeNotifyL7DisableMsg".equals(operation)){
            for (AhNotificationMessage msg : msgs) {
                if (msg.getPriority() == AhNotificationMessage.L7_DSIABLE_PRIORITY) {
                    removableMsg = msg;
                    break;
                }
            }
            if(null != removableMsg) {
               msgPool.disableMsg(removableMsg, getUserContext());
               jsonObject.put("succ", true);
            }
        }else if("closeCriticalAlarmMsg".equals(operation)){
        	 for (AhNotificationMessage msg : msgs) {
                 if (msg.getPriority() == AhNotificationMessage.CRITICAL_CLEAR_MSG_PRIORITY) {
                     removableMsg = msg;
                     break;
                 }
             }
             if(null != removableMsg) {
                msgPool.disableMsg(removableMsg, getUserContext());
                MgrUtil.setSessionAttribute(getDomainId()+":"+ SessionKeys.CLOSE_CRITICAL_ALARM_MSG, true);
                jsonObject.put("succ", true);
             }
        }else if ("noDisplayClientManager".equals(operation)) {
        	for (AhNotificationMessage msg : msgs) {
                if (msg.getPriority() == AhNotificationMessage.CLIENT_MANAGER_MSG_PRIORITY) {
                    removableMsg = msg;
                    break;
                }
            }
            if(null != removableMsg) {
               msgPool.disableMsg(removableMsg, getUserContext());
               jsonObject.put("succ", true);
            }
        }else if ("closeHmUpgradeLogMsg".equals(operation)){
        	for( AhNotificationMessage msg : msgs){
        		if(msg.getPriority() == AhNotificationMessage.UPGRADE_LOG_MSG_PRIORITY){
        			removableMsg = msg;
        			break;
        		}
        	}
            if(null != removableMsg) {
                msgPool.disableMsg(removableMsg, getUserContext());
                jsonObject.put("succ", true);
             }
        }
        return "json";
    }
}
