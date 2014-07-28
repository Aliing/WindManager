package com.ah.bo.port;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class PortAuthProfile implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private boolean enabled8021X;
    private boolean first8021X = true;
    private int interval8021X = 30;
    private boolean enabledMAC;
    
    /*-----------Override Object methods--------------*/
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PortAuthProfile && this.equals(obj));
    }

    //========Getter/Setter============
    public boolean isEnabled8021X() {
        return enabled8021X;
    }

    public boolean isFirst8021X() {
        return first8021X;
    }

    public int getInterval8021X() {
        return interval8021X;
    }

    public boolean isEnabledMAC() {
        return enabledMAC;
    }

    public void setEnabled8021X(boolean enabled8021x) {
        enabled8021X = enabled8021x;
    }

    public void setFirst8021X(boolean first8021x) {
        first8021X = first8021x;
    }

    public void setInterval8021X(int interval8021x) {
        interval8021X = interval8021x;
    }

    public void setEnabledMAC(boolean enabledMAC) {
        this.enabledMAC = enabledMAC;
    }    
}
