package com.ah.bo.hiveap;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.ArrayUtils;

@Embeddable
public class ConfigMDMAirWatchNonCompliance {

    public final static int NOTIFY_METHOD_PUSH = 1;
    public final static int NOTIFY_METHOD_SMS = 2;
    public final static int NOTIFY_METHOD_EMAIL = 4;
    
    private boolean enabledNonCompliance;
    
    private boolean notifyViaPush;
    private boolean notifyViaSMS;
    private boolean notifyViaEmail;
    
    private String title;
    private String content;
    
    private boolean disconnectVlanChanged;
    
    private int pollingInterval = 60;

    //--------------Getter/Setter----------------
    public boolean isEnabledNonCompliance() {
        return enabledNonCompliance;
    }

    public boolean isNotifyViaPush() {
        return notifyViaPush;
    }

    public boolean isNotifyViaSMS() {
        return notifyViaSMS;
    }

    public boolean isNotifyViaEmail() {
        return notifyViaEmail;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setEnabledNonCompliance(boolean enabledNonCompliance) {
        this.enabledNonCompliance = enabledNonCompliance;
    }

    public void setNotifyViaPush(boolean notifyViaPush) {
        this.notifyViaPush = notifyViaPush;
    }

    public void setNotifyViaSMS(boolean notifyViaSMS) {
        this.notifyViaSMS = notifyViaSMS;
    }

    public void setNotifyViaEmail(boolean notifyViaEmail) {
        this.notifyViaEmail = notifyViaEmail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }
    
    public boolean isDisconnectVlanChanged() {
        return disconnectVlanChanged;
    }

    public void setDisconnectVlanChanged(boolean disconnectVlanChanged) {
        this.disconnectVlanChanged = disconnectVlanChanged;
    }

    public void convertMethods(int[] notificationMethods) {
        this.notifyViaPush = false;
        this.notifyViaSMS = false;
        this.notifyViaEmail = false;
        for (int method : notificationMethods) {
            switch (method) {
            case NOTIFY_METHOD_PUSH:
                this.notifyViaPush = true;
                break;
            case NOTIFY_METHOD_SMS:
                this.notifyViaSMS = true;
                break;
            case NOTIFY_METHOD_EMAIL:
                this.notifyViaEmail = true;
                break;
            default:
                //do nothing
                break;
            }
        }
    }

    public int[] initMethods() {
        int[] notificationMethods = null;
        if(this.notifyViaPush) {
            notificationMethods = ArrayUtils.add(notificationMethods, NOTIFY_METHOD_PUSH);
        }
        if(this.notifyViaSMS) {
            notificationMethods = ArrayUtils.add(notificationMethods, NOTIFY_METHOD_SMS);
        }
        if(this.notifyViaEmail) {
            notificationMethods = ArrayUtils.add(notificationMethods, NOTIFY_METHOD_EMAIL);
        }
        return notificationMethods;
    }
}
