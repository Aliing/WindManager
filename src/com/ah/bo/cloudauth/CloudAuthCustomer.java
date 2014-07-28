package com.ah.bo.cloudauth;

import java.sql.Timestamp;

import javax.persistence.Column;
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
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "CLOUD_AUTH_CUSTOMER")
@org.hibernate.annotations.Table(appliesTo = "CLOUD_AUTH_CUSTOMER", indexes = {
		@Index(name = "CLOUD_AUTH_CUSTOMER_OWNER", columnNames = { "OWNER" })
		})
public class CloudAuthCustomer implements HmBo {

    private static final long serialVersionUID = -1581636886525834300L;
    
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "OWNER", nullable = false)
    private HmDomain owner;
    
    @Version
    private Timestamp version;
    
    private String userName;
    
    private String password;

    private String customerId;
    
    private boolean tried; // for trial link, if true don't need to show the trial link [HMOL only]
    private String idmanagerId; // if this field is not empty then the ID Manager is enabled for this vHM [HMOL only]
    @Column(length=1024)
    private String trialSettingsText; // For trial link, this field is stored the last settings from Portal [HMOL only]
    
    private boolean usingProxy; // For IDM proxy, using proxy for the IDM connections
    
    // ============ Constructor ============
    public CloudAuthCustomer() {
        // default
    }

    public CloudAuthCustomer(String userName, String password, String customerId, HmDomain domain) {
        this.userName = userName;
        this.password = password;
        this.customerId = customerId;
        this.owner = domain;
    }
    
    public CloudAuthCustomer(String customerId, String idmanagerId, HmDomain domain) {
        this.customerId = customerId;
        this.idmanagerId = idmanagerId;
        this.owner = domain;
    }
    
    // ============ Getter/Setter ============
	@Override
    public Long getId() {
        return id;
    }

	@Override
    public HmDomain getOwner() {
        return owner;
    }

	@Override
    public Timestamp getVersion() {
        return version;
    }

    public String getPassword() {
        return password;
    }

    public String getCustomerId() {
        return customerId;
    }

	@Override
    public void setId(Long id) {
        this.id = id;
    }

	@Override
    public void setOwner(HmDomain owner) {
        this.owner = owner;
    }

	@Override
    public void setVersion(Timestamp version) {
        this.version = version;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isTried() {
        return tried;
    }

    public String getIdmanagerId() {
        return idmanagerId;
    }

    public void setTried(boolean tried) {
        this.tried = tried;
    }

    public void setIdmanagerId(String idmanagerId) {
        this.idmanagerId = idmanagerId;
    }

    public String getTrialSettingsText() {
        return trialSettingsText;
    }

    public void setTrialSettingsText(String trialSettingsText) {
        this.trialSettingsText = trialSettingsText;
    }

    public boolean isUsingProxy() {
        return usingProxy;
    }

    public void setUsingProxy(boolean usingProxy) {
        this.usingProxy = usingProxy;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public void setSelected(boolean selected) {
    }

}