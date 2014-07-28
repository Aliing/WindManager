package com.ah.util.ports.profile;

import com.ah.bo.port.PortAuthProfile;

public class PhoneDataAuth extends AbstractPortProfile {
    
    final int firstSeq = 1; 
    final int secondSeq = 2; 
    
    private PhoneDataSubAuth x;
    private PhoneDataSubAuth m;

    public PhoneDataSubAuth getX() {
        return x;
    }

    public PhoneDataSubAuth getM() {
        return m;
    }

    public void setX(PhoneDataSubAuth x) {
        this.x = x;
    }

    public void setM(PhoneDataSubAuth m) {
        this.m = m;
    }

    @Override
    public String getName() {
        return "pda";
    }

    class PhoneDataSubAuth {
        private boolean e; // enable
        private int seq; // sequence
        private int iv; // interval
        public PhoneDataSubAuth() {
        }
        public PhoneDataSubAuth(boolean enabled, int seqence) {
            this.e = enabled;
            this.seq = seqence;
        }
        public PhoneDataSubAuth(boolean enabled, int seqence, int interval) {
            this.e = enabled;
            this.seq = seqence;
            this.iv = interval;
        }
        public boolean isE() {
            return e;
        }
        public int getSeq() {
            return seq;
        }
        public int getIv() {
            return iv;
        }
        public void setE(boolean e) {
            this.e = e;
        }
        public void setSeq(int seq) {
            this.seq = seq;
        }
        public void setIv(int iv) {
            this.iv = iv;
        }
        
    }

    @Override
    public void init(Object object) {
        if(null != object && object instanceof PortAuthProfile) {
            PortAuthProfile obj = (PortAuthProfile)object;
            this.setX(new PhoneDataSubAuth(obj.isEnabled8021X(), obj.isFirst8021X() ? this.firstSeq
                    : this.secondSeq, obj.getInterval8021X()));
            this.setM(new PhoneDataSubAuth(obj.isEnabledMAC(), obj.isFirst8021X() ? this.secondSeq
                    : this.firstSeq));
        }
    }
}
