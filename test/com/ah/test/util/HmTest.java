package com.ah.test.util;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * @author Chris Scheers
 */
public abstract class HmTest implements Runnable {
	private final AtomicInteger id;

	private ThreadLocal<Integer> idHolder = new ThreadLocal<Integer>() {
		public Integer initialValue() {
			return id.incrementAndGet();
		}
	};

	public int getId() {
		return idHolder.get();
	}

	public HmTest() {
		id = new AtomicInteger(0);
	}
}
