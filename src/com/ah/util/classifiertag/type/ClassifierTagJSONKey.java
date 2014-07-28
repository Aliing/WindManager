package com.ah.util.classifiertag.type;

public enum ClassifierTagJSONKey {
    SUCC("succ"),
    ERRMSG("errmsg");
    
    private String keyValue;
    private ClassifierTagJSONKey(String keValue) {
        this.keyValue = keValue;
    }
    @Override
    public String toString() {
        return this.keyValue;
    }
}
