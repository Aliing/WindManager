package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;

@Entity
@Table(name = "GUEST_ANALYTICS_INFO")
@org.hibernate.annotations.Table(appliesTo = "GUEST_ANALYTICS_INFO", indexes = {
        @Index(name = "GUEST_ANALYTICS_INFO_OWNER", columnNames = { "OWNER" })
        })
public class GuestAnalyticsInfo implements HmBo{
    private static final long serialVersionUID = 1146312635480217257L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "OWNER", nullable = false)
    private HmDomain owner;
    
    @Version
    private Timestamp version;
    
    private boolean enabled;
    private String apiKey;
    private String apiNonce;
    
    public GuestAnalyticsInfo() {
    }
    
    public GuestAnalyticsInfo(boolean enabled, String apiKey, String apiNonce, HmDomain domain) {
        this.enabled = enabled;
        this.apiKey = apiKey;
        this.apiNonce = apiNonce;
        this.owner = domain;
    }
    
    public Long getId() {
        return id;
    }

    public HmDomain getOwner() {
        return owner;
    }

    public Timestamp getVersion() {
        return version;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiNonce() {
        return apiNonce;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOwner(HmDomain owner) {
        this.owner = owner;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setApiNonce(String apiNonce) {
        this.apiNonce = apiNonce;
    }

    @Override
    public String getLabel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isSelected() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setSelected(boolean selected) {
        // TODO Auto-generated method stub
        
    }

}
