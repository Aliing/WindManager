package com.ah.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import arlut.csd.ganymede.boolean_field;
import arlut.csd.ganymede.string_field;

import com.ah.bo.mgmt.QueryUtil;

public class DBFunction {
	
	private static final Tracer		log					= new Tracer(DBFunction.class.getSimpleName());
	
	//read file and create to db
    private static void createDBFunction(String strFile)
    {
    	try
    	{
    		BufferedReader brFstab = new BufferedReader(new FileReader(strFile));
    		String strTmp;
    		StringBuffer sql = new StringBuffer();
    		while ((strTmp = brFstab.readLine()) != null)
    		{
    			
    			if (strTmp.trim().startsWith("--")){
    				continue;
    			}
    			sql.append("  "+strTmp);
    		}
    		
    		QueryUtil.executeNativeUpdate(sql.toString());
    	}
    	catch(Exception ex)
    	{
    		log.error(ex.getMessage(),ex);
    	}
    }
    
    //function for xiaoping
    //hex-->int in db
    public static void createHex2Int()
    {
    	String strconfig = System.getenv("HM_ROOT")+ File.separator + "WEB-INF" 
    	                    + File.separator +"dbfunction"+File.separator+"hex2int.conf";
    	createDBFunction(strconfig);
    }
    
    public static void createDBRollUp()
    {
    	String strconfig = System.getenv("HM_ROOT")+ File.separator + "WEB-INF" 
    	                    + File.separator +"dbfunction"+File.separator+"client_stats_roll_up.conf";
    	createDBFunction(strconfig);
    	
    	strconfig = System.getenv("HM_ROOT")+ File.separator + "WEB-INF" 
        + File.separator +"dbfunction"+File.separator+"interface_stats_roll_up.conf";
    	createDBFunction(strconfig);
    	
    	strconfig = System.getenv("HM_ROOT")+ File.separator + "WEB-INF" 
        + File.separator +"dbfunction"+File.separator+"ssid_client_count_roll_up.conf";
    	createDBFunction(strconfig);
    	
    	strconfig = System.getenv("HM_ROOT")+ File.separator + "WEB-INF" 
        + File.separator +"dbfunction"+File.separator+"client_osinfo_count_roll_up.conf";
    	createDBFunction(strconfig);
    	
    	strconfig = System.getenv("HM_ROOT")+ File.separator + "WEB-INF" 
    	        + File.separator +"dbfunction"+File.separator+"sla_stats_roll_up.conf";
    	    	createDBFunction(strconfig);
    }
    public  static boolean createRepoRollUp() {
    	String configurePath =  System.getenv("HM_ROOT")+ File.separator + "WEB-INF" 
    			+ File.separator +"dbfunction"
    	        + File.separator +"repofunction";
    	String TASK_SUFFIX = ".sql";
		try {
			// read configuration
			File fileQzconf = new File(configurePath);
			if (!fileQzconf.exists()) {
				return false;
			}
			File[] arrFile = fileQzconf.listFiles();
			for (int i = 0; i < arrFile.length; i++) {
				if (arrFile[i].isFile()
						&& arrFile[i].getName().endsWith(TASK_SUFFIX)) {
					createDBFunction(arrFile[i].getAbsolutePath());
					log.debug("Initialize report Stored procedure script : "
							+ arrFile[i].getAbsolutePath());
				}
			}
		} catch (Exception e) {
			log.debug("Initialize report Stored procedure script failed ", e);
			return false;
		}
    	return true;
	}
    
    
}
