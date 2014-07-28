/**
 * @filename			LocationReport.java
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
 * location report contains a list of LocationDTO
 */
public class LocationReport {
	private List<LocationDTO> reports;
	
	public LocationReport() {
		this.reports = new ArrayList<LocationDTO>(); 
	}
	
	public LocationReport(List<LocationDTO> reports) {
		this.reports = reports;
	}
	
	public void addReport(LocationDTO report) {
		if(this.reports == null) {
			return ;
		}
		
		this.reports.add(report);
	}
	
	public void addReports(Collection<LocationDTO> reports) {
		if(this.reports == null) {
			return ;
		}
		
		this.reports.addAll(reports);
	}

	/**
	 * getter of reports
	 * @return the reports
	 */
	public List<LocationDTO> getReports() {
		return reports;
	}

	/**
	 * setter of reports
	 * @param reports the reports to set
	 */
	public void setReports(List<LocationDTO> reports) {
		this.reports = reports;
	}
	
	
}
