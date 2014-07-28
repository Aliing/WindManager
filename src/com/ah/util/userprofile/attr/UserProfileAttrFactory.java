package com.ah.util.userprofile.attr;

import java.util.HashSet;

import com.ah.bo.HmBo;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.SsidProfile;

public class UserProfileAttrFactory {
    public static RevertUserProfile getPlainObjFromPort(UserProfile bo, PortAccessProfile port, UserProfileType type) {
        return getPlainObj(bo, port, type);
    }
    public static RevertUserProfile getPlainObjFromSSID(UserProfile bo, SsidProfile ssid, UserProfileType type) {
        return getPlainObj(bo, ssid, type);
    }
    public static RevertUserProfile getPlainObj(UserProfile bo, HmBo portOrSsid, UserProfileType type) {
        if(portOrSsid instanceof PortAccessProfile || portOrSsid instanceof SsidProfile) {
            RevertUserProfile profile = new RevertUserProfile(bo.getId(), bo.getUserProfileName());
            profile.setPlain();
            if(null == profile.getParents()) {
                profile.setParents(new HashSet<UserProfileParent>());
            }
            if(portOrSsid instanceof PortAccessProfile) {
                PortAccessProfile port = (PortAccessProfile) portOrSsid; 
                profile.getParents().add(new UserProfileParent(port.getId(), port.getName(), UserProfileParentType.PORT, type));
            } else {
                SsidProfile ssid = (SsidProfile) portOrSsid; 
                profile.getParents().add(new UserProfileParent(ssid.getId(), ssid.getSsidName(), UserProfileParentType.SSID, type));
            }
            return profile;
        }
        return null;
    }
    
    public static RevertUserProfile getReassginedObj(UserProfile bo, UserProfile reassigned) {
        RevertUserProfile profile = new RevertUserProfile(bo.getId(), bo.getUserProfileName());
        profile.setReassgined();
        if(null == profile.getReassignedIds()) {
            profile.setReassignedIds(new HashSet<Long>());
        }
        profile.getReassignedIds().add(reassigned.getId());
        return profile;
    }
}

