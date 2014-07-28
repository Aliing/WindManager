/**
 * @filename			HAStatusListener.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ha;

import com.ah.ha.event.HAStatusChangedEvent;

public interface HAStatusListener {

	enum HAMode {
		ACTIVE, PASSIVE
	}

	void statusChanged(HAStatusChangedEvent event);

}