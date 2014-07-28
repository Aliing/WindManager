package com.ah.ws.rest.models.idm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class IDMTrialSettings {

    public IDMTrialSettings() {}
    
    private int trialPeriod;
    
    private List<IDMTrialGuestAccount> accountList;
    
    @XmlElement(name="days")
    public int getTrialPeriod() {
        return trialPeriod;
    }

    @XmlElement(name="list")
    public List<IDMTrialGuestAccount> getAccountList() {
        return accountList;
    }

    public void setTrialPeriod(int trialPeriod) {
        this.trialPeriod = trialPeriod;
    }

    public void setAccountList(List<IDMTrialGuestAccount> accountList) {
        this.accountList = accountList;
    }
    
}
