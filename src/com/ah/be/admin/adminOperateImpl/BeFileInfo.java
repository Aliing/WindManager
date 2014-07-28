package com.ah.be.admin.adminOperateImpl;

public class BeFileInfo {

	private String fileName;
	
	private String fileSize;
	
	private String createTime;
	
	public void setFileName(String strFileName)
	{
		fileName = strFileName;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public void setFileSize(String strFileSize)
	{
		fileSize = strFileSize;
	}
	
	public String getFileSize()
	{
		return fileSize;
	}

	public String getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(String createTime)
	{
		this.createTime = createTime;
	}
	
}
