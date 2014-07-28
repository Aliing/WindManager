package com.ah.ws.rest.models.idm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VHMCustomerInfo {

    private String email; // not in the response, define for update request
    private String primaryEmail;
    private String firstName;
    private String lastName;
    private String companyName;
    private String customerId;
    private String jobTitle;
    private String country;
    private String state;
    private String industry;
    private String phoneNumber;
    private List<String> idmIDs;

	// added in 6.1r4 (Gotham) to enhance IDM trial
	private List<String> acmIds;
	private List<String> primaryUsers;
	private List<String> nonPrimaryUsers;
    
    public VHMCustomerInfo() {}

    public String getIdmID() {
        if(null == idmIDs || idmIDs.isEmpty()) {
            return null;
        } else {
            return idmIDs.get(0);
        }
    }
    //-----------Getter/Setter----------------
    public List<String> getAcmIds() {
		return acmIds;
	}

	public void setAcmIds(List<String> acmIds) {
		this.acmIds = acmIds;
	}

	public List<String> getPrimaryUsers() {
		return primaryUsers;
	}

	public void setPrimaryUsers(List<String> primaryUsers) {
		this.primaryUsers = primaryUsers;
	}

	public List<String> getNonPrimaryUsers() {
		return nonPrimaryUsers;
	}

	public void setNonPrimaryUsers(List<String> nonPrimaryUsers) {
		this.nonPrimaryUsers = nonPrimaryUsers;
	}
	
    public String getFirstName() {
        return firstName;
    }

	public String getLastName() {
        return lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getIndustry() {
        return industry;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    @XmlElement(name="idmIds")
    public List<String> getIdmIDs() {
        return idmIDs;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setIdmIDs(List<String> idmIDs) {
        this.idmIDs = idmIDs;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
