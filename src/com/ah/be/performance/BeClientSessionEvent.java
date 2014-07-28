package com.ah.be.performance;

import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.event.BeBaseEvent;

public class BeClientSessionEvent extends BeBaseEvent{

	private static final long serialVersionUID = 1L;
	
	private SimpleHiveAp ap;
	
	public BeClientSessionEvent()
	{
	}
	
	public SimpleHiveAp getAp() {
		return ap;
	}

	public void setAp(SimpleHiveAp ap) {
		this.ap = ap;
	}

}