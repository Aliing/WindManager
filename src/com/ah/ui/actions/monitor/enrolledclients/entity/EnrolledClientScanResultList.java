package com.ah.ui.actions.monitor.enrolledclients.entity;


import java.util.List;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("content")
public class EnrolledClientScanResultList
{
	@XStreamAlias("ScanList")
	private List<EnrolledClientScanResultItem>	scanResultList;

	public List<EnrolledClientScanResultItem> getScanResultList() {
		return scanResultList;
	}

	public void setScanResultList(List<EnrolledClientScanResultItem> scanResultList) {
		this.scanResultList = scanResultList;
	}


}
