package com.ah.util.ports.profile;


public class GroupDesc extends AbstractPortProfile {
    private String pd; //port desc
    private String pt; //port type
    private String ad; // aggregation desc
    
    public GroupDesc() {
    }
    
    public GroupDesc(String portDesc, String portType) {
        this.pd = portDesc;
        this.pt = portType;
    }
    
    public GroupDesc(String portDesc, String portType, String aggDesc) {
        this.pd = portDesc;
        this.pt = portType;
        this.ad = aggDesc;
    }
    
    public String getPd() {
        return pd;
    }

    public String getPt() {
        return pt;
    }

    public String getAd() {
        return ad;
    }

    public void setPd(String pd) {
        this.pd = pd;
    }

    public void setPt(String pt) {
        this.pt = pt;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    @Override
    public String getName() {
        return "gd";
    }

    @Override
    public void init(Object object) {
        // TODO Auto-generated method stub
    }

}
