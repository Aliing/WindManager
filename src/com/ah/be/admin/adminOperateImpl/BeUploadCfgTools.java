package com.ah.be.admin.adminOperateImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import com.ah.be.app.DebugUtil;
import com.ah.be.common.file.XMLFileReadWriter;

public class BeUploadCfgTools {
	
	public static final String AH_UPLOAD_CONF_FILE  = "./conf/uploadfile.xml";
	
	public static void initUploadConfFile(BeUploadCfgInfo oCfgInfo)
	{		
		File fCfg = new File(BeUploadCfgTools.AH_UPLOAD_CONF_FILE);		
		
		Document document = DocumentHelper.createDocument();
		
		try
		{
		    XMLWriter output = new XMLWriter(new FileOutputStream(fCfg));
		    
		    Element eOperation = document.addElement("operation").addAttribute(
					"type", oCfgInfo.getType()).addAttribute("runningflag", 
							oCfgInfo.getRunningFlag()).addAttribute("locationflag", 
									oCfgInfo.getLocation()).addAttribute("uploadfinishflag",
											oCfgInfo.getFinishFlag());
		    
		    
		    Element eUploadfile = eOperation.addElement("uploadfile");
		    
		    eUploadfile.addAttribute("name", oCfgInfo.getName()).addAttribute("size", oCfgInfo.getSize());
		    
		    output.write(document);	    
			
			output.close();	
		}
		catch(Exception ex)
		{
			DebugUtil.adminDebugWarn("BeUploadCfgTools.initUpLoadConfFile() has error!", ex);	
			
			//ex.printStackTrace();
		}
	}

	public static BeUploadCfgInfo getUploadConfInfo()
	{
		File fCfg = new File(BeUploadCfgTools.AH_UPLOAD_CONF_FILE);	
		
		if(!fCfg.exists())
		{
			return null;
		}
		
		BeUploadCfgInfo oInfo = new BeUploadCfgInfo();		
		
		Document doc = XMLFileReadWriter.parser(BeUploadCfgTools.AH_UPLOAD_CONF_FILE);
		
		if(null == doc)
		{
			return null;
		}
		
		Element eRoot = doc.getRootElement();
		
		oInfo.setType(eRoot.attributeValue("type"));
		
		oInfo.setRunningFlag(eRoot.attributeValue("runningflag"));
		
		oInfo.setLocation(eRoot.attributeValue("locationflag"));
		
		oInfo.setFinishFlag(eRoot.attributeValue("uploadfinishflag"));
		
		List<?> eFileList = eRoot.elements();
		
		if(null == eFileList)
		{
			return null;
		}
		
		Element eFile = (Element) eFileList.get(0);
		
		oInfo.setName(eFile.attributeValue("name"));
		
		oInfo.setSize(eFile.attributeValue("size"));		
		
		return oInfo;
	}
	
	public static void finishRunningFlag()
	{
		BeUploadCfgInfo oInfo = BeUploadCfgTools.getUploadConfInfo();
		
		if(null == oInfo)
		{
			return;
		}
		
		oInfo.setRunningFlag(BeUploadCfgInfo.AH_UPLOAD_RUNNINF_FALSE);
		
		BeUploadCfgTools.initUploadConfFile(oInfo);
	}
	
	public static void main(String[] args)
	{
		BeUploadCfgInfo oInfo = new BeUploadCfgInfo();
		
		oInfo.setType(BeUploadCfgInfo.AH_UPLOAD_TYPE_UPDATE);
		oInfo.setLocation(BeUploadCfgInfo.AH_UPLOAD_LOCATION_LOCAL);
		oInfo.setRunningFlag("false");
		oInfo.setFinishFlag("true");
		oInfo.setName("test.tar.gz");
		oInfo.setSize("124");
		
		initUploadConfFile(oInfo);
		
		BeUploadCfgInfo oNewInfo = getUploadConfInfo();
		
		System.out.println(oNewInfo.getType());
		System.out.println(oNewInfo.getRunningFlag());
		System.out.println(oNewInfo.getName());
		System.out.println(oNewInfo.getSize());
		System.out.println(oNewInfo.getLocation());
		System.out.println(oNewInfo.getFinishFlag());
	}

}