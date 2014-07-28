package com.ah.util.userprofile.attr.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.ah.bo.useraccess.UserProfile;
import com.ah.util.MgrUtil;
import com.ah.util.userprofile.attr.RevertUserProfile;
import com.ah.util.userprofile.attr.UserProfileParent;

public class UserProfileAttrValidateImpl implements IUserProfileAttrValidator {

    private HashMap<Long, RevertUserProfile> attrMapping;
    private final boolean disabledValidatation = false;
    
    public UserProfileAttrValidateImpl() {
        attrMapping = new HashMap<>();
    }
    
    @Override
    public void add(RevertUserProfile profile) {
        if(null == profile || disabledValidatation) return;
        
        final long profileId = profile.getId();
        if(null == attrMapping.get(profileId)) {
            attrMapping.put(profileId, profile);
        } else {
            RevertUserProfile oldProfile = attrMapping.get(profileId);
            if(profile.isPlain()) {
                oldProfile.setPlain();
                if(null == oldProfile.getParents()) {
                    oldProfile.setParents(new HashSet<UserProfileParent>());
                }
                oldProfile.getParents().addAll(profile.getParents());
            } else {
                oldProfile.setReassgined();
                if(null == oldProfile.getReassignedIds()) {
                    oldProfile.setReassignedIds(new HashSet<Long>());
                }
                oldProfile.getReassignedIds().addAll(profile.getReassignedIds());
            }
        }
    }
    @Override
    public void updateAttrs(long id, Set<String> attributes) {
        if(disabledValidatation) return;
        RevertUserProfile profile = attrMapping.get(id);
        if(null != profile) {
            if(null == profile.getAttributes()) {
                profile.setAttributes(new HashSet<String>());
            }
            profile.getAttributes().addAll(attributes);
        }
    }
    @Override
    public void updateAttr(long id, String attribute) {
        if(disabledValidatation) return;
        RevertUserProfile profile = attrMapping.get(id);
        if(null != profile) {
            if(null == profile.getAttributes()) {
                profile.setAttributes(new HashSet<String>());
            }
            profile.getAttributes().add(attribute);
        }
    }
    @Override
    public String validate(UserProfile conflictProfile, String conflictAttrNum) {
        if(disabledValidatation) return null;
        if(null == conflictProfile) return MgrUtil.getMessageString("error.unknown");
        
        String message = "";
        RevertUserProfile conflict = attrMapping.get(conflictProfile.getId());
        
        for (RevertUserProfile profile : attrMapping.values()) {
            if(conflictProfile.getId().longValue() != profile.getId() && profile.isChecked()) {
                if(profile.getAttributes().contains(conflictAttrNum)) {
                    message = getMessageText(profile);
                    message +=  " and " + getMessageText(conflict) + " are in conflict on attribute " + conflictAttrNum + ".";
                    break;
                }
            }
        }
        return message;
    }

    private String getMessageText(RevertUserProfile profile) {
        if(disabledValidatation) return null;
        if(null == profile) return MgrUtil.getMessageString("error.unknown");
        
        String message = "";
        if(profile.isPlain()) {
            UserProfileParent parent = profile.getParents().iterator().next();
            message = profile.getName() + " on " + parent.getName();
        } else if (profile.isReassgined()) {
            while(profile.getReassignedIds().iterator().hasNext()) {
                RevertUserProfile reassigned = attrMapping.get(profile.getReassignedIds().iterator().next());
                if(null != reassigned && reassigned.isPlain()) {
                    message = profile.getName() + " (reassigned to " + getMessageText(reassigned) + ")";
                    break;
                }
            }
        }
        return message;
    }

}
