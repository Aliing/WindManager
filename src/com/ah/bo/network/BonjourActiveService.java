package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class BonjourActiveService implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BONJOUR_SERVICE_ID", nullable = true)
	private BonjourService bonjourService;
	
	public BonjourService getBonjourService() {
		return bonjourService;
	}

	public void setBonjourService(BonjourService bonjourService) {
		this.bonjourService = bonjourService;
	}
}
