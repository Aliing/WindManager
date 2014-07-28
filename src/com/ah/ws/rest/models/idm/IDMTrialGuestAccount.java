package com.ah.ws.rest.models.idm;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IDMTrialGuestAccount {

    public IDMTrialGuestAccount() {}
    
    private int maxGuests;
    
    private int smsAccout;
    
    private String description;

    @XmlElement(name="maxguests")
    public int getMaxGuests() {
        return maxGuests;
    }

    @XmlElement(name="smscount")
    public int getSmsAccout() {
        return smsAccout;
    }

    public String getDescription() {
        return description;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public void setSmsAccout(int smsAccout) {
        this.smsAccout = smsAccout;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
