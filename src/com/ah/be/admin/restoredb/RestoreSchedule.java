package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.wlan.Scheduler;

/*
 * support restoration for VHM
 * joseph chen, 05/04/2008
 */

public class RestoreSchedule {

	public void saveToDatabase(){
		try{
			saveValues();
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
		}
	}

	private  boolean saveValues() throws Exception{
		AhRestoreGetXML xmlFile= new AhRestoreGetXML();
		if(!xmlFile.readXMLFile("SCHEDULER"))
			return false;
		List<String[]> list_common=xmlFile.getM_lst_xmlFile();
		String[] column_common=xmlFile.getM_str_colName();

		List<Scheduler> listBo=new ArrayList<Scheduler>();
		if(list_common!=null && list_common.size()>0)
		{
			for (String[] common : list_common) {
				Scheduler schedule = new Scheduler();
				for (int j = 0; j < schedule.getFieldValues().length; j++) {
					String colName = schedule.getFieldValues()[j];
					int col_index = AhRestoreCommons.checkColExist(colName, column_common);
					if (col_index >= 0) {
						String value = AhRestoreCommons.convertString(common[col_index]); // joseph chen , 06/02/2008

						if (!"".equals(value)) {
							value = value.trim();
							switch (j) {
								case 0:
									schedule.setId(Long.parseLong(value));
									continue;
								case 1:
									schedule.setSchedulerName(value);
									continue;
								case 2:
									schedule.setDescription(value);
									continue;
								case 3:
									schedule.setBeginDate(value);
									continue;
								case 4:
									schedule.setEndDate(value);
									continue;
								case 5:
									schedule.setWeeks(value);
									continue;
								case 6:
									schedule.setBeginTime(value);
									continue;
								case 7:
									schedule.setEndTime(value);
									continue;
								case 8:
									schedule.setBeginTimeS(value);
									continue;
								case 9:
									schedule.setEndTimeS(value);
									continue;
								case 10:
									schedule.setType(Integer.parseInt(value));
									continue;
								case 11:
									schedule.setOwner(AhRestoreNewMapTools.getHmDomain(Long.parseLong(value)));
									continue;
								default:
									continue;
							}
						}
					}
				}

				//AhRestoreMapTool.setMapSchedule(schedule.getId().toString(), schedule.getSchedulerName());

				// set owner, joseph chen 05/04/2008
				//schedule.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);

				listBo.add(schedule);
			}
		}

		List<Long> lOldId = new ArrayList<Long>();

		for (Scheduler bo : listBo) {
			lOldId.add(bo.getId());
		}

		QueryUtil.restoreBulkCreateBos(listBo);

		for(int i=0; i < listBo.size(); ++i)
		{
			AhRestoreNewMapTools.setMapSchedule(lOldId.get(i), listBo.get(i).getId());
		}
		//QueryUtil.bulkCreateBos(listBo);
		return true;
	}

}