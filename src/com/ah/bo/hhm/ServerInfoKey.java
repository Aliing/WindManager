package com.ah.bo.hhm;

import java.io.Serializable;

/*
 * @author wpliang
 */
public class ServerInfoKey implements Serializable{

	private static final long serialVersionUID = -1968476538716821707L;
	
	private String vhmId;
	
	private String versionName;
	
	public String getVhmId() {
		return vhmId;
	}

	public void setVhmId(String vhmId) {
		this.vhmId = vhmId;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	
    @Override  
    public boolean equals(Object o) {  
        if(o instanceof ServerInfoKey){  
        	ServerInfoKey key = (ServerInfoKey)o ;  
            if(this.vhmId.equals(key.getVhmId()) && this.versionName.equals(key.getVersionName())){
                return true ;  
            }  
        }  
        return false ;  
    }  
      
    @Override  
    public int hashCode() {  
        return this.versionName.hashCode();  
    }  

}