package com.ah.be.admin.adminOperateImpl;

import java.io.File;
import java.util.List;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.util.MgrUtil;
/**
 * @author zhoushaohua
 * @version v1.0.0
 */
public class BeAppProcessStatusUtil {
    private static final long              DEFAULT_TIMEOUT    = Long.parseLong(MgrUtil.getUserMessage("ha.single.node.operation.timeout"));
	private static final String	           FILE_PATH	      = "/HiveManager/tomcat"+ File.separator+ "hm_soft_upgrade";
	private static final long              SLEEP_TIME		  = Long.parseLong(MgrUtil.getUserMessage("ha.single.node.upgrade.thread.process.sleep.time"));
    
    private static long START;
    
    /**
     * 
     * @param statusFileName
     * @return true:success;false:failure
     */
    public static boolean waitProcess(String desIp,String statusFileName){
    	boolean isFinished = true;
    	START = System.currentTimeMillis();
    	boolean upgradeBol = false;
    	List<String> resultList = null;
    	String strCmd = "ssh -o ConnectTimeout=3 -o PasswordAuthentication=no -o " +
		"IdentityFile=~/.ssh/id_rsa -o StrictHostKeyChecking=no " + desIp + " find "+ FILE_PATH +
		File.separator + " -name " + statusFileName;
    	while(isFinished){
    		if(isOverTime()){
    			isFinished = false;
    			upgradeBol = false;
    			return upgradeBol;
    		}
    		resultList = BeAdminCentOSTools.getOutStreamsExecCmd(strCmd);
    		if(null != resultList && resultList.size() > 0){
    			String temp = resultList.get(0);
    			int statusFileNameLength = statusFileName.length();
    			if(null != temp && !"".equals(temp) && temp.length() > statusFileNameLength){
    				String successFileName = temp.substring(temp.length()-statusFileNameLength, temp.length());
    				if(statusFileName.equals(successFileName)){
    					isFinished = false;
            			upgradeBol = true;
            			return upgradeBol;
    				}
    				
    			}
    		}
    		try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				isFinished = true;
			}
    	}
    	return upgradeBol;
    }
    
    /**
     * timeout control
     */
    private static boolean isOverTime(){
		return System.currentTimeMillis() - START >= DEFAULT_TIMEOUT;
	}
    /**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("-------------start----------------");
    	waitProcess("10.155.20.6","dbservers");
	}
}
