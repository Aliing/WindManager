package com.ah.be.communication.event;

import com.ah.be.communication.BeCommunicationConstant;

/**
 * used to hold rogue AP query event from DA(Designated Access Point)
 */
public class BeMitigationArbitratorResultEvent extends BeCapwapClientResultEvent {
	
	private static final long serialVersionUID = 1L;

	public BeMitigationArbitratorResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_MITIGATION_ARBITRATOR;
	}
	
	/**
	 * Nothing to do with it now, for there is nothing passed as argument to here.
	 */
}
