package com.ah.events;

/*
 * @author Chris Scheers
 */

import java.util.EventObject;

import com.ah.bo.HmBo;

public class BoEvent<T extends HmBo> extends EventObject {

	private static final long serialVersionUID = 1L;

	public enum BoEventType {
		CREATED, UPDATED, REMOVED
	}

	protected BoEventType type;

	public T getSource() {
		return (T) source;
	}

	public BoEventType getType() {
		return type;
	}

	public BoEvent(T hmBo, BoEventType type) {
		super(hmBo);
		this.type = type;
	}

}