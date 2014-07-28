package com.ah.util.userprofile.attr;

import java.util.HashSet;

public class RevertUserProfile {

    private static final int REASSIGNED = 0b01;
    private static final int PLAIN = 0b10;
    
    private long id;
    private String name;
    
    private int reassginedRule = 0; //initialize
    
    private HashSet<UserProfileParent> parents;
    private HashSet<Long> reassignedIds; // reassigned to specific 
    
    private HashSet<String> attributes;
    
    public RevertUserProfile(long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public boolean isChecked() {
        return !(null == this.attributes || this.attributes.size() == 0);
    }
    public void setPlain() {
        this.reassginedRule |= PLAIN;
    }
    public boolean isPlain() {
        return (this.reassginedRule & PLAIN) == PLAIN;
    }
    public void setReassgined() {
        this.reassginedRule |= REASSIGNED;
    }
    public boolean isReassgined() {
        return (this.reassginedRule & REASSIGNED) == REASSIGNED;
    }
    
    @Override
    public String toString() {
        return new StringBuilder().append("{id: " + this.id + ", name: " + this.name+ "}").toString();
    }
    /*-------Getter/Setter---------*/

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getReassginedRule() {
        return reassginedRule;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReassginedRule(int reassginedRule) {
        this.reassginedRule = reassginedRule;
    }

    public HashSet<UserProfileParent> getParents() {
        return parents;
    }

    public void setParents(HashSet<UserProfileParent> parents) {
        this.parents = parents;
    }

    public HashSet<Long> getReassignedIds() {
        return reassignedIds;
    }

    public void setReassignedIds(HashSet<Long> reassignedIds) {
        this.reassignedIds = reassignedIds;
    }

    public HashSet<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashSet<String> attributes) {
        this.attributes = attributes;
    }

}
