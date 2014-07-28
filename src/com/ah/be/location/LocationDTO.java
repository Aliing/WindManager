/**
 * @filename			LocationDTO.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * DTO for location tracking
 */
public class LocationDTO {
	private TrackingClient 	client;
	
	private List<ReportDTO> reports;
	
	public LocationDTO() {
		reports = new ArrayList<ReportDTO>();
	}
	
	public LocationDTO(TrackingClient client, List<ReportDTO> reports) {
		this.client = client;
		this.reports = reports;
	}

	public void addReport(ReportDTO report) {
		if(this.reports == null) {
			return ;
		}
		
		this.reports.add(report);
	}
	
	public void addReports(Collection<ReportDTO> reports) {
		if(this.reports == null) {
			return ;
		}
		
		this.reports.addAll(reports);
	}
	
	
	/**
	 * getter of client
	 * @return the client
	 */
	public TrackingClient getClient() {
		return client;
	}

	/**
	 * setter of client
	 * @param client the client to set
	 */
	public void setClient(TrackingClient client) {
		this.client = client;
	}

	/**
	 * getter of reports
	 * @return the reports
	 */
	public List<ReportDTO> getReports() {
		return reports;
	}

	/**
	 * setter of reports
	 * @param reports the reports to set
	 */
	public void setReports(List<ReportDTO> reports) {
		this.reports = reports;
	}
	
	
	
}
