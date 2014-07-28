package com.ah.be.common.db;

import com.ah.be.performance.appreport.AhReportCollectData;
import com.ah.bo.ApReportData;
import com.ah.bo.HmBo;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.performance.AhACSPNeighbor;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhBandWidthSentinelHistory;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhInterferenceStats;
import com.ah.bo.performance.AhLatestACSPNeighbor;
import com.ah.bo.performance.AhLatestInterferenceStats;
import com.ah.bo.performance.AhLatestNeighbor;
import com.ah.bo.performance.AhLatestRadioAttribute;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhRadioStats;
import com.ah.bo.performance.AhVIfStats;
import com.ah.bo.performance.AhXIf;

public class DefaultBulkUpdate implements BulkUpdateInterface{
	/**
	 * get value by column name and bo
	 * @param object
	 * @param columnName
	 * @param aValue	aValue[0] store the value
	 * @return whether use the value returned.
	 */
	@Override
	public boolean getValue(Object object,String columnName,Object[] aValue)
	{
		boolean used = false;
		Object value = null;
		//column owner
		if(columnName.equalsIgnoreCase("owner")) {
			if (object instanceof ApReportData) {
				value = ((ApReportData)object).getOwnerId();
				used = true;
			}
			else if (object instanceof AhReportCollectData) {
				value = ((AhReportCollectData)object).getOwnerId();
				used = true;
			}
			else {
				value = ((HmBo)object).getOwner().getId();
				used = true;
			}
		}
		else {
			if(object instanceof AhClientStats) {
			}
			else if(object instanceof AhNeighbor) {
				AhNeighbor bo = (AhNeighbor)object;
				if(columnName.equalsIgnoreCase("time")) {
					value = bo.getTimeStamp().getTime();
					used = true;
				}
				else if(columnName.equalsIgnoreCase("time_zone")) {
					value = bo.getTimeStamp().getTimeZone();
					used = true;
				}
			} else if(object instanceof AhAssociation) {
				AhAssociation bo = (AhAssociation)object;
				if(columnName.equalsIgnoreCase("time")) {
					value = bo.getTimeStamp().getTime();
					used = true;
				}
				else if(columnName.equalsIgnoreCase("time_zone")) {
					value = bo.getTimeStamp().getTimeZone();
					used = true;
				}
			}  else if(object instanceof AhEvent) {
				AhEvent bo = (AhEvent)object;
				if(columnName.equalsIgnoreCase("time")) {
					value = bo.getTrapTimeStamp().getTime();
					used = true;
				}
				else if(columnName.equalsIgnoreCase("time_zone")) {
					value = bo.getTrapTimeStamp().getTimeZone();
					used = true;
				} else if(columnName.equalsIgnoreCase("as_time")) {
					value = bo.getAsTimeStamp().getTime();
					used = true;
				}
				else if(columnName.equalsIgnoreCase("as_time_zone")) {
					value = bo.getAsTimeStamp().getTimeZone();
					used = true;
				}
			} else if(object instanceof AhInterferenceStats) {
				AhInterferenceStats bo = (AhInterferenceStats)object;
				if(columnName.equalsIgnoreCase("time")) {
					value = bo.getTimeStamp().getTime();
					used = true;
				}
				else if(columnName.equalsIgnoreCase("time_zone")) {
					value = bo.getTimeStamp().getTimeZone();
					used = true;
				}
			} else if(object instanceof AhACSPNeighbor) {
				AhACSPNeighbor bo = (AhACSPNeighbor)object;
				if(columnName.equalsIgnoreCase("time")) {
					value = bo.getTimeStamp().getTime();
					used = true;
				}
				else if(columnName.equalsIgnoreCase("time_zone")) {
					value = bo.getTimeStamp().getTimeZone();
					used = true;
				}
			} else if(object instanceof AhBandWidthSentinelHistory) {
				AhBandWidthSentinelHistory bo = (AhBandWidthSentinelHistory)object;
				if(columnName.equalsIgnoreCase("time")) {
					value = bo.getTimeStamp().getTime();
					used = true;
				}
				else if(columnName.equalsIgnoreCase("time_zone")) {
					value = bo.getTimeStamp().getTimeZone();
					used = true;
				}
			} else if(object instanceof AhClientSessionHistory) {
				AhClientSessionHistory bo = (AhClientSessionHistory)object;
				if(columnName.equalsIgnoreCase("simulated")) {
					value = bo.isSimulated();
					used = true;
				}
			} else if(object instanceof AhVIfStats) {
				AhVIfStats bo = (AhVIfStats)object;
				if(columnName.equalsIgnoreCase("apname")) {
					value = bo.getXifpk().getApName();
					used = true;
				} else if(columnName.equalsIgnoreCase("ifindex")) {
					value = bo.getXifpk().getIfIndex();
					used = true;
				} else if(columnName.equalsIgnoreCase("stattimestamp")) {
					value = bo.getXifpk().getStatTimeValue();
					used = true;
				}
			} 
			else if(object instanceof AhRadioAttribute) {
				AhRadioAttribute bo = (AhRadioAttribute)object;
				if(columnName.equalsIgnoreCase("apname")) {
					value = bo.getXifpk().getApName();
					used = true;
				} else if(columnName.equalsIgnoreCase("ifindex")) {
					value = bo.getXifpk().getIfIndex();
					used = true;
				} else if(columnName.equalsIgnoreCase("stattimestamp")) {
					value = bo.getXifpk().getStatTimeValue();
					used = true;
				}
			} 
			else if(object instanceof AhRadioStats) {
				AhRadioStats bo = (AhRadioStats)object;
				if(columnName.equalsIgnoreCase("apname")) {
					value = bo.getXifpk().getApName();
					used = true;
				} else if(columnName.equalsIgnoreCase("ifindex")) {
					value = bo.getXifpk().getIfIndex();
					used = true;
				} else if(columnName.equalsIgnoreCase("stattimestamp")) {
					value = bo.getXifpk().getStatTimeValue();
					used = true;
				}
			} 
			else if(object instanceof AhXIf) {
				AhXIf bo = (AhXIf)object;
				if(columnName.equalsIgnoreCase("apname")) {
					value = bo.getXifpk().getApName();
					used = true;
				} else if(columnName.equalsIgnoreCase("ifindex")) {
					value = bo.getXifpk().getIfIndex();
					used = true;
				} else if(columnName.equalsIgnoreCase("stattimestamp")) {
					value = bo.getXifpk().getStatTimeValue();
					used = true;
				}
			} else if(object instanceof AhLatestNeighbor) {
				AhLatestNeighbor bo = (AhLatestNeighbor)object;
				if(columnName.equalsIgnoreCase("time")) {
					value = bo.getTimeStamp().getTime();
					used = true;
				}
				else if(columnName.equalsIgnoreCase("time_zone")) {
					value = bo.getTimeStamp().getTimeZone();
					used = true;
				}
			} else if(object instanceof AhLatestRadioAttribute) {
				AhLatestRadioAttribute bo = (AhLatestRadioAttribute)object;
				if(columnName.equalsIgnoreCase("time")) {
					value = bo.getTimeStamp().getTime();
					used = true;
				}
				else if(columnName.equalsIgnoreCase("time_zone")) {
					value = bo.getTimeStamp().getTimeZone();
					used = true;
				}
			} else if(object instanceof AhLatestXif) {
				AhLatestXif bo = (AhLatestXif)object;
				if(columnName.equalsIgnoreCase("time")) {
					value = bo.getTimeStamp().getTime();
					used = true;
				}
				else if(columnName.equalsIgnoreCase("time_zone")) {
					value = bo.getTimeStamp().getTimeZone();
					used = true;
				}
			} else if(object instanceof AhLatestInterferenceStats) {
				AhLatestInterferenceStats bo = (AhLatestInterferenceStats)object;
				if(columnName.equalsIgnoreCase("time")) {
					value = bo.getTimeStamp().getTime();
					used = true;
				}
				else if(columnName.equalsIgnoreCase("time_zone")) {
					value = bo.getTimeStamp().getTimeZone();
					used = true;
				}
			} else if(object instanceof AhLatestACSPNeighbor) {
				AhLatestACSPNeighbor bo = (AhLatestACSPNeighbor)object;
				if(columnName.equalsIgnoreCase("time")) {
					value = bo.getTimeStamp().getTime();
					used = true;
				}
				else if(columnName.equalsIgnoreCase("time_zone")) {
					value = bo.getTimeStamp().getTimeZone();
					used = true;
				}
			} else if(object instanceof AhLatestNeighbor) {
				AhLatestNeighbor bo = (AhLatestNeighbor)object;
				if(columnName.equalsIgnoreCase("time")) {
					value = bo.getTimeStamp().getTime();
					used = true;
				}
				else if(columnName.equalsIgnoreCase("time_zone")) {
					value = bo.getTimeStamp().getTimeZone();
					used = true;
				}
			}
			
		}
		
		aValue[0] = value;
		return used;
	}
}
