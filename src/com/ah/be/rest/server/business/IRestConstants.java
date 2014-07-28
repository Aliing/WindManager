package com.ah.be.rest.server.business;

public interface IRestConstants {
	public static final String SUCCESS = "Success";
	//Global Error Message
	public static final String ERROR = "Error";
	public static final String RESULT_ERROR_INTERNALSERVERERROR = "Internal Server Error ";
	public static final String RESULT_ERROR_XMLPARSEERROR = "Can not parse the XML ";
	public static final String RESULT_ERROR_OBJECT_EXIST = "The Object is Already Exists";
	public static final String RESULT_ERROR_INVALIDEPARAMETER = "The Parameter is Invalid";
	public static final String RESULT_ERROR_OBJECT_NOTXEXIST = "The Object is Not Exists";
	//MAC Auth Error Message
	public static final String RESULT_ERROR_MACAUTH_STUDENTID_NULL = "The StudentID can not be null";
	public static final String RESULT_ERROR_MACAUTH_STUDENTNAME_NULL = "The StudentName can not be null";
	public static final String RESULT_ERROR_MACAUTH_SCHOOLNAME_NULL = "The SchoolName can not be null";
	public static final String RESULT_ERROR_MACAUTH_SCHOOLID_NULL = "The SchoolId can not be null";
	public static final String RESULT_ERROR_MACAUTH_MACADDRESS_NULL = "The MACAddress can not be null";
	
	//MACAuth Operation
	public static final String OPERATION_MACAUTH_BULKDELETE = "Bulk Delete MACAuth by StudentId and SchoolName";
	public static final String OPERATION_MACAUTH_SINGLEDELETE = "Delete MACAuth by StudentId and SchoolName";
	public static final String OPERATION_MACAUTH_BULKUPSERT = "Bulk update or insert MACAuth";
	public static final String OPERATION_MACAUTH_SINGLEUPSERT = "Update or insert MACAuth";
}
