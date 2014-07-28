package com.ah.be.sa3party;

public interface Sa3Infc {
    public void sendSAdata(String strMac,byte[] bsData);     
    
    public long getChannelID();
}
