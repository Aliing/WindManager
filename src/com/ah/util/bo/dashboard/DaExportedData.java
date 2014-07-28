package com.ah.util.bo.dashboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.ah.bo.dashboard.AhDashboard;

import edu.emory.mathcs.backport.java.util.Collections;

public class DaExportedData {
	private String daName;
	private List<DaExportedSingleData> data;
	
//	private Long startTime;
//	private Long endTime;
//	private String exportTime;
	
	private AhDashboard dashboard;
	
	public String getDaName() {
		return daName;
	}
	public void setDaName(String daName) {
		this.daName = daName;
	}
	public List<DaExportedSingleData> getData() {
		return data;
	}
	public void setData(List<DaExportedSingleData> data) {
		this.data = data;
	}
	
	public void addData(DaExportedSingleData aData) {
		if (data == null) {
			data = new ArrayList<>();
		}
		
		data.add(aData);
	}
//	public Long getStartTime() {
//		return startTime;
//	}
//	public void setStartTime(Long startTime) {
//		this.startTime = startTime;
//	}
//	public Long getEndTime() {
//		return endTime;
//	}
//	public void setEndTime(Long endTime) {
//		this.endTime = endTime;
//	}
//	public String getExportTime() {
//		return exportTime;
//	}
//	public void setExportTime(String exportTime) {
//		this.exportTime = exportTime;
//	}
	public AhDashboard getDashboard() {
		return dashboard;
	}
	public void setDashboard(AhDashboard dashboard) {
		this.dashboard = dashboard;
	}
	
	public void sortData() {
		if (this.data == null
				|| this.data.isEmpty()) {
			return;
		}
		Collections.sort(this.data, new Comparator<DaExportedSingleData>() {
			@Override
			public int compare(DaExportedSingleData o1, DaExportedSingleData o2) {
				if (o1.getOrder() > o2.getOrder()) {
					return 1;
				} else if (o1.getOrder() < o2.getOrder()) {
					return -1;
				} else {
					if (o1.getColumn() > o2.getColumn()) {
						return 1;
					} else {
						return -1;
					}
				}
			}
		});
	}
	
}
