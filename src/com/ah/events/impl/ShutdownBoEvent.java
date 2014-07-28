package com.ah.events.impl;

/*
 * @author Chris Scheers
 */

import com.ah.events.BoEvent;
import com.ah.bo.monitor.AhEvent;

public final class ShutdownBoEvent extends BoEvent<AhEvent> {

	private static final long serialVersionUID = 1L;

	public ShutdownBoEvent() {
		super(new AhEvent(), null);
	}

}