/**
 * 
 */
package com.ah.be.admin.adminOperateImpl;

/**
 * @author root
 *
 */
public class BeCAFileInfo 
{
	private String fileName;
	
	private String fileSize;
	
	private String createTime;
	
	private String domainName;
	
	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

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
