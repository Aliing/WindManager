package com.ah.ws.rest.models.idm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Account")
public class IDMSalesforceAccount {
    public IDMSalesforceAccount() {}
    /*
     * Customer info
     */
    private String name;
    
    private String industry;
    
    private String firstName;
    
    private String lastName;
    
    private String primaryEmail;
    
    private String currentAdminEmail;
    
    private String phone;
    
    private String title;
    
    private String state;
    
    private String country;
	
	// add for IDM trial enhancement in Gotham release
	private List<String> emails;
	
	private String cid;
    
    /*
     * Entitlement info
     */
    private int totalUsers;
    
    private int totalSMSBoughtLifeTime;
    
    private int totalSMSUsedLifeTime;
    
    private boolean directoryIntegration;
    
    private String subscriptionStartDate;
    
    private String subscriptionEndDate;
    
    private String domain;

	@XmlElementWrapper(name="Emails") 
    @XmlElement(name="Email") 
    public List<String> getEmails() {
		return emails;
	}
	
	@XmlElement(name="CID")
	public String getCid() {
		return cid;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	@XmlElement(name="TotalUsers")
    public int getTotalUsers() {
        return totalUsers;
    }

    @XmlElement(name="TotalSMSBoughtLifetime")
    public int getTotalSMSBoughtLifeTime() {
        return totalSMSBoughtLifeTime;
    }

    @XmlElement(name="TotalSMSUsedLifeTime")
    public int getTotalSMSUsedLifeTime() {
        return totalSMSUsedLifeTime;
    }

    @XmlElement(name="DirectoryIntegration")
    public boolean isDirectoryIntegration() {
        return directoryIntegration;
    }

    @XmlElement(name="SubscriptionStartDate")
    public String getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    @XmlElement(name="SubscriptionRenewalDate")
    public String getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    @XmlElement(name="Domain")
    public String getDomain() {
        return domain;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public void setTotalSMSBoughtLifeTime(int totalSMSBoughtLifeTime) {
        this.totalSMSBoughtLifeTime = totalSMSBoughtLifeTime;
    }

    public void setTotalSMSUsedLifeTime(int totalSMSUsedLifeTime) {
        this.totalSMSUsedLifeTime = totalSMSUsedLifeTime;
    }

    public void setDirectoryIntegration(boolean directoryIntegration) {
        this.directoryIntegration = directoryIntegration;
    }

    public void setSubscriptionStartDate(String subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }

    public void setSubscriptionEndDate(String subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @XmlElement(name="Name")
    public String getName() {
        return name;
    }

    @XmlElement(name="Industry")
    public String getIndustry() {
        return industry;
    }

    @XmlElement(name="FirstName")
    public String getFirstName() {
        return firstName;
    }

    @XmlElement(name="LastName")
    public String getLastName() {
        return lastName;
    }

    @XmlElement(name="PrimaryEmail")
    public String getPrimaryEmail() {
        return primaryEmail;
    }

    @XmlElement(name="CurrentAdminEmail")
    public String getCurrentAdminEmail() {
        return currentAdminEmail;
    }

    @XmlElement(name="Phone")
    public String getPhone() {
        return phone;
    }

    @XmlElement(name="Title")
    public String getTitle() {
        return title;
    }

    @XmlElement(name="State")
    public String getState() {
        return state;
    }

    @XmlElement(name="Country")
    public String getCountry() {
        return country;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public void setCurrentAdminEmail(String currentAdminEmail) {
        this.currentAdminEmail = currentAdminEmail;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
