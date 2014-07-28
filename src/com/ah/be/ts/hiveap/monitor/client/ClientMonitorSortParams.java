package com.ah.be.ts.hiveap.monitor.client;

public class ClientMonitorSortParams {

	public enum SortType {
		AP_NODE_NAME, BSSID, CLIENT_MAC, LOG_MSG_TIME, MSG_SEQ_NUM
	}

    //***************************************************************
    // Variables
    //***************************************************************

	private final SortType sortType;

	private final boolean ascending;

	public ClientMonitorSortParams(SortType sortType, boolean ascending) {
		this.sortType = sortType;
		this.ascending = ascending;
	}

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

	public SortType getSortType() {
		return sortType;
	}

	public boolean isAscending() {
		return ascending;
	}

}