package com.ah.util.userprofile.attr.validation;

import java.util.Set;

import com.ah.bo.useraccess.UserProfile;
import com.ah.util.userprofile.attr.RevertUserProfile;

public interface IUserProfileAttrValidator {
    void add(RevertUserProfile profile);
    void updateAttrs(long id, Set<String> attributes);
    void updateAttr(long id, String attribute);
    String validate(UserProfile conflictProfile, String conflictAttrNum);
}
