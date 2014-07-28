package com.ah.be.event;

import java.io.Serializable;

public interface AhEventMgmt<E extends BeBaseEvent> extends Serializable {

	void start();

	boolean isStarted();

	void stop();

	void add(E t);

	void notify(E t);

	int getEventQueueSize();

	Thread[] getEventProcessThreads();

}