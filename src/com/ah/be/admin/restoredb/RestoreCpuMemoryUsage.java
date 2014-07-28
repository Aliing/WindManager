package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.CpuMemoryUsage;

public class RestoreCpuMemoryUsage {
	public static final String cpuMemoryUsageTableName = "hm_cpu_memory_usage";
	
	public static boolean restoreCpuMemoryUsage(){

        try {
            long start = System.currentTimeMillis();

            List<CpuMemoryUsage> allCpuMemoryUsage = getAllCpuMemoryUsage();

            if (null == allCpuMemoryUsage || allCpuMemoryUsage.isEmpty()) {
                AhRestoreDBTools.logRestoreMsg("CpuMemoryUsage is null or empty.");
            } else {
                AhRestoreDBTools.logRestoreMsg("Restore CpuMemoryUsage size:" + allCpuMemoryUsage.size());
                QueryUtil.restoreBulkCreateBos(allCpuMemoryUsage);
            }
            long end = System.currentTimeMillis();
            AhRestoreDBTools.logRestoreMsg("Restore CpuMemoryUsage completely. Count:"
                    + (null == allCpuMemoryUsage ? "0" : allCpuMemoryUsage.size()) + ", cost:" + (end - start) + " ms.");
        } catch (Exception e) {
            AhRestoreDBTools.logRestoreMsg("Restore CpuMemoryUsage error.", e);
            return false;
        }
        return true;

	}
	
	private static List<CpuMemoryUsage> getAllCpuMemoryUsage() throws AhRestoreException,
			AhRestoreColNotExistException {
			AhRestoreGetXML xmlParser = new AhRestoreGetXML();
			
			/**
			 * Check validation of hm_cpu_memory_usage.xml
			 */
			boolean restoreRet = xmlParser.readXMLFile(cpuMemoryUsageTableName);
			if (!restoreRet) {
			    return null;
			}
			
			/**
			 * No one row data stored in hm_cpu_memory_usage table is allowed
			 */
			int rowCount = xmlParser.getRowCount();
			boolean isColPresent;
			String colName;
			
			List<CpuMemoryUsage> allCpuMemoryUsage = new ArrayList<CpuMemoryUsage>();
			CpuMemoryUsage cpuMemoryUsage;
			
			for (int i = 0; i < rowCount; i++) {
				cpuMemoryUsage = new CpuMemoryUsage();
				
				/**
			      * set timeStamp
			      */
			     colName = "timestamp";
			     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, cpuMemoryUsageTableName, colName);
			     if (!isColPresent) {
			     	BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hm_cpu_memory_usage' data be lost, cause: 'timestamp' column is not exist.");
			         /**
			          * The timestamp column must be exist in the table.
			          */
			         continue;
			     }else{
			    	 cpuMemoryUsage.setTimeStamp(AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)));
			     }
			     /**
			      * set cpuUsage
			      */
			     colName = "cpuusage";
			     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, cpuMemoryUsageTableName, colName);
			     if (!isColPresent) {
			     	BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hm_cpu_memory_usage' data be lost, cause: 'cpuusage' column is not exist.");
			         /**
			          * The cpuusage column must be exist in the table.
			          */
			         continue;
			     }else{
			    	 cpuMemoryUsage.setCpuUsage(AhRestoreCommons.convertFloat(xmlParser.getColVal(i, colName)));
			     }
			     /**
			      * set memUsage
			      */
			     colName = "memusage";
			     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, cpuMemoryUsageTableName, colName);
			     if (!isColPresent) {
			     	BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hm_cpu_memory_usage' data be lost, cause: 'memusage' column is not exist.");
			         /**
			          * The memusage column must be exist in the table.
			          */
			         continue;
			     }else{
			    	 cpuMemoryUsage.setMemUsage(AhRestoreCommons.convertFloat(xmlParser.getColVal(i, colName)));
			     }
			     
			     /**
			      * Set owner
			      */
			     colName = "owner";
			     isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, cpuMemoryUsageTableName, colName);
			     long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i,
			             colName)) : 1;
			
			     if (null == AhRestoreNewMapTools.getHmDomain(ownerId)) {
			     	BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'hm_cpu_memory_usage' data be lost, cause: 'owner' column is not available.");
			         continue;
			     }
			
			     cpuMemoryUsage.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));
			     allCpuMemoryUsage.add(cpuMemoryUsage);
			 }
			 return allCpuMemoryUsage;
	}
}
