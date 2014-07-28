package com.ah.be.ts.hiveap.monitor.client;

public class ClientMonitorFilterParams {

	public enum LogLevel {
		BASIC, INFO, DETAIL
	}

    //***************************************************************
    // Variables
    //***************************************************************

	private final LogLevel logLevel;

	public ClientMonitorFilterParams(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

	public LogLevel getLogLevel() {
		return logLevel;
	}

}