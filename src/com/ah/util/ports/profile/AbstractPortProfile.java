package com.ah.util.ports.profile;

import org.json.JSONObject;

public abstract class AbstractPortProfile {

    public JSONObject parse2JSON() {
        return new JSONObject(this);
    }
    
    public abstract void init(Object object);
    public abstract String getName();
}
