package com.ah.bo.useraccess;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class MulticastForwarding implements Serializable{

		private static final long serialVersionUID = 1L;

		private String ip;
		
		private String netmask;
		 	

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getNetmask() {
			return netmask;
		}

		public void setNetmask(String netmask) {
			this.netmask = netmask;
		}

}
