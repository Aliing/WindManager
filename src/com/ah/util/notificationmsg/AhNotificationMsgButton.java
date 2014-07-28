package com.ah.util.notificationmsg;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

public class AhNotificationMsgButton {
    public AhNotificationMsgButton() {
    }
    public AhNotificationMsgButton(String desc, String func) {
        this.desc = desc;
        this.func = func;
    }
    public AhNotificationMsgButton(String desc, String func, String width) {
        this.desc = desc;
        this.func = func;
        this.width = width;
    }
    
    private String desc;
    private String icon;
    private String func;
    private String width;
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(this.desc, "Button Value")
                .append(this.func, "onclick event").toString();
    }
    /*-------Getter/Setter--------*/
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getFunc() {
        if(null != func && StringUtils.isBlank(func)) {
            func = null;
        }
        return func;
    }
    public void setFunc(String func) {
        this.func = func;
    }
    public String getWidth() {
        return width;
    }
    public void setWidth(String width) {
        this.width = width;
    }
}
