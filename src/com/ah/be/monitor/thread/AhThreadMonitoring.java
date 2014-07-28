package com.ah.be.monitor.thread;

public interface AhThreadMonitoring {

	void register(Thread thread);

	void deregister(Thread thread);

	void start();

	void stop();

}