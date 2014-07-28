package com.ah.be.rest.server.models;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MACAuth")
public class MACAuthModel extends BaseModel{
	@XStreamAlias("StudentID")
	private String studentId;
	@XStreamAlias("StudentName")
	private String studentName;
	@XStreamAlias("MACAddress")
	private String macAddress;
	@XStreamAlias("SchoolId")
	private String SchoolId;
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress.toLowerCase();
	}
	public String getSchoolId() {
		return SchoolId;
	}
	public void setSchoolId(String schoolId) {
		SchoolId = schoolId;
	}	
}
