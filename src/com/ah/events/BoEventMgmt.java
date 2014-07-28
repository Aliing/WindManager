package com.ah.events;

/*
 * @author Chris Scheers
 */

import com.ah.bo.HmBo;

public interface BoEventMgmt {

	<T extends HmBo> void addBoEventListener(BoEventListener<T> listener,
			BoEventFilter<T> filter);

	<T extends HmBo> void removeBoEventListener(BoEventListener<T> listener);

	/*
	 * Push event on event queue, process event in different thread.
	 */
	<T extends HmBo> void publishBoEvent(BoEvent<T> event);

	/*
	 * Bypass event queue notify listeners in current thread.
	 */
	<T extends HmBo> void notifyListeners(BoEvent<T> event);

	void start();

	void stop();

}