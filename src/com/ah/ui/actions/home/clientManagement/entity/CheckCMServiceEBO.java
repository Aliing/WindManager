package com.ah.ui.actions.home.clientManagement.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("content")
public class CheckCMServiceEBO {
    
    @XStreamAsAttribute
    private String version="1.0";
    
    @XStreamAlias("CustomerId")
    private String customId;
    
    @XStreamAlias("HmId")
    private String hmId;
    
    public String getCustomId() {
        return customId;
    }

    public String getHmId() {
        return hmId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public void setHmId(String hmId) {
        this.hmId = hmId;
    }
    
}
