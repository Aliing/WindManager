package com.ah.be.admin.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.log.BeLogTools;

public class HAadminTool {
	
	private final static String RESTORE_TAG_FILE = "/HiveManager/ha/opt/ha_restore_db";
	
	private final static String ERASE_TAG_FILE = "/HiveManager/ha/opt/ha_clear_db";

	public static boolean isHaModel()
    {
       String strShFile = "isHaModel.sh";
       
       return BeOperateHMCentOSImpl.isRslt_0(strShFile);
    	
    }
    
    public static boolean isValidMaster()
    {
    	String strShFile = "isValidMaster.sh";
    	
    	return BeOperateHMCentOSImpl.isRslt_0(strShFile);
    }
    
    public static void tagHADatabaseRestore(){
    	tagHADatabaseOperation(RESTORE_TAG_FILE);
    }
    
    public static void tagHADatabaseErase(){
    	tagHADatabaseOperation(ERASE_TAG_FILE);
    }
    
    /**
     * For restore database: set the flag file - /HiveManager/ha/opt/ha_restore_db<br>
     * For erase database: set the flag file - /HiveManager/ha/opt/ha_clear_db
     *
     * @author Yunzhi Lin
     * - Time: Feb 18, 2011 11:07:10 AM
     * @param fileName
     */
    private static void tagHADatabaseOperation(String fileName){
    	
		BeLogTools.info("tagHADatabaseOperation() : start tag file:"+fileName);
		try {
			FileUtils.touch(new File(fileName));
		} catch (IOException e) {
			BeLogTools.error("tagHADatabaseOperation() : tag file:"+fileName+" error.",e);
		}
		BeLogTools.info("tagHADatabaseOperation() : end tag file:"+fileName);
    }
    
}
