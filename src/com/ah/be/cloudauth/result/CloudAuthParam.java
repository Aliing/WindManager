package com.ah.be.cloudauth.result;

public enum CloudAuthParam {
    APP_KEY("appKey"), 
    HIVEMANAGER_ID("hiveManagerId"), 
    CUSTOMER_ID("customerId"), 
    DEVICE_ID("deviceId"), 
    CSR("csr"), 
    RETURN_CODE("returnCode"), 
    RETURN_MESSAGE("returnMessage"), 
    RETURN_CONTENT("content");

    private String name;
    private CloudAuthParam(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
