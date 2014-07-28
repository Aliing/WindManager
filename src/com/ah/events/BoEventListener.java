package com.ah.events;

/*
 * @author Chris Scheers
 */

import java.util.EventListener;

import com.ah.bo.HmBo;

public interface BoEventListener<T extends HmBo> extends EventListener {

	void boCreated(T hmBo);

	void boUpdated(T hmBo);

	void boRemoved(T hmBo);

}