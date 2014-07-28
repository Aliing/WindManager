package com.ah.events;

/*
 * @author Chris Scheers
 */

import com.ah.bo.HmBo;

public class BoEventFilter<T extends HmBo> {

	private Class<T> boClass;

	public BoEventFilter(Class<T> boClass) {
		this.boClass = boClass;
	}

	public Class<T> getBoClass() {
		return boClass;
	}

}