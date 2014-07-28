package com.ah.util.userprofile.attr;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UserProfileParent {
    private long id;
    private String name;
    private UserProfileParentType parentType;
    private UserProfileType profileType;
    
    public UserProfileParent(long id, String name, UserProfileParentType parentType, UserProfileType profileType) {
        this.id = id;
        this.name = name;
        this.parentType = parentType;
        this.profileType = profileType;
    }
    @Override
    public String toString() {
        return new StringBuilder().append(
                "{id: " + this.id + ", name: " + this.name + ", parent-type: "
                        + this.parentType + ", type: " + this.profileType + "}").toString();
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(name).append(parentType)
                .append(profileType).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof UserProfileParent)) {
            return false;
        }
        UserProfileParent o = (UserProfileParent) obj;
        return new EqualsBuilder().append(this.id, o.id)
                .append(this.name, o.name)
                .append(this.parentType, o.parentType)
                .append(this.profileType, o.profileType).build();
    }

    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public UserProfileParentType getParentType() {
        return parentType;
    }
    public UserProfileType getProfileType() {
        return profileType;
    }
    public void setId(long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setParentType(UserProfileParentType parentType) {
        this.parentType = parentType;
    }
    public void setProfileType(UserProfileType profileType) {
        this.profileType = profileType;
    }
}
enum UserProfileParentType {
    SSID, PORT;
}
