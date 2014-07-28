package com.ah.util.notificationmsg.message;

import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.notificationmsg.AhNotificationMessage;
import com.ah.util.notificationmsg.AhNotificationMsgButton;

public class HmPBRWarningMsg extends AhNotificationMessage {

    private static final String DAKAR_5_1_r_1 = "5.1.1.0";

    @Override
    public int initPriority() {
        return PRE_BR_WARNING_MSG_PRIORITY;
    }

    @Override
    public boolean isDisplayFlagOn(HmUser userContext) {
        if (isDomainUser(userContext)) {
            if (NmsUtil.compareSoftwareVersion(NmsUtil.getHiveOSVersion(NmsUtil.getVersionInfo()),
                    DAKAR_5_1_r_1) >= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isNeedBuild(HmUser userContext) {
        final FilterParams filterParams = new FilterParams(
                "managestatus = :s1 and connected = :s2 and simulated = :s3 and deviceType = :s4 and softVer < :s5",
                new Object[] { HiveAp.STATUS_MANAGED, true, false, HiveAp.Device_TYPE_BRANCH_ROUTER, DAKAR_5_1_r_1 });
        List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, null, filterParams, userContext
                .getDomain().getId(), 1);
        if (list.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void build(HmUser userContext) {
        this.contents = MgrUtil.getUserMessage("notification.message.pbr.contents");
        this.actionButtons.add(new AhNotificationMsgButton("Upload", "gotoBRUploadURL()"));

    }

    @Override
    public boolean refresh(HmUser userContext, Object action) {
        return init(userContext);
    }

    @Override
    public boolean disableDisplay(HmUser userContext) {
        // TODO Auto-generated method stub
        return false;
    }
    
/*-    @Override
    public String getMsgStyle() {
        return "max-width: 445px;";
    }
*/
    @Override
    public String getBtnGroupStyle() {
        return "";
    }
}
