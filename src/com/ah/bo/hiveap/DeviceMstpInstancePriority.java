package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Range;

@Embeddable
public class DeviceMstpInstancePriority implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8797191891220913303L;

	public DeviceMstpInstancePriority() {

	}

	public static final int BASE_PRIORITY = 4096;
	public static final short MAX_TIMES = 15;
	public static final short MIN_TIMES = 0;

	private int priority;
	private short instance;

	@Transient
	@Range(min = MIN_TIMES, max = MAX_TIMES)
	private short times = MIN_TIMES;


	public short getTimes() {
		this.times = (short) (this.priority / BASE_PRIORITY);
		return times;
	}

	public void setTimes(short times) {
		this.times = times;
		this.setPriority(BASE_PRIORITY * times);
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public short getInstance() {
		return instance;
	}

	public void setInstance(short instance) {
		this.instance = instance;
	}
}