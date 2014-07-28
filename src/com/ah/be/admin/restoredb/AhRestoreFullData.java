package com.ah.be.admin.restoredb;

import java.lang.reflect.Method;

import com.ah.be.admin.util.HAadminTool;
import com.ah.be.log.BeLogTools;

public class AhRestoreFullData {
	
	public AhRestoreFullData(){}
	
	public static void main(String[] args)
	{
		try
		{
			
			AhRestoreDBData oRestoreData = new AhRestoreDBData();
			
			oRestoreData.restoreFullData();
			// If is HA master node, tag a restore file
			HAadminTool.tagHADatabaseRestore();
			
			/**
			 * Add Report BackEnd Row-up process
			 * @author zdu zdu@aerohive.com
			 */
			
			launchReportBackEndRowUp( args );
			
		}
		catch(Exception ex)
		{
			//add log
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
			
			return;
		}
	}

    /**
     * launch report BackEnd row-up process.
     * 
     * @author zdu
     * @email zdu@aerohive.com
     * @param args
     */
    private static void launchReportBackEndRowUp( String[] args ) {
	try {
	    Class< ? > clazz = Class
		    .forName( "com.ah.nms.worker.report.rowup.migration.Upgrade4ReportRowup" );
	    Method method = clazz.getDeclaredMethod( "main", String[].class );

	    method.invoke( null, (Object) args );
	} catch ( Exception e ) {
	    BeLogTools.restoreLog( BeLogTools.ERROR, e.getMessage( ) );
	}
    }

}
