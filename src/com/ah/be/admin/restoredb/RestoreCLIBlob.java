package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.CLIBlob;



public class RestoreCLIBlob {
	
	public void saveToDatabase(){
		try{
			saveCLIBlobs();
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
		}
	}

	private  boolean saveCLIBlobs() throws Exception{
		AhRestoreGetXML xmlFile= new AhRestoreGetXML();
		if(!xmlFile.readXMLFile("cli_blob"))
			return false;
		List<String[]> list_common=xmlFile.getM_lst_xmlFile();
		String[] column_common=xmlFile.getM_str_colName();

		List<CLIBlob> listBo=new ArrayList<CLIBlob>();
		if(list_common!=null && list_common.size()>0)
		{
			for (String[] common : list_common) {
				CLIBlob cliBlob = new CLIBlob();
				for (int j = 0; j < cliBlob.getFieldValues().length; j++) {
					String colName = cliBlob.getFieldValues()[j];
					int col_index = AhRestoreCommons.checkColExist(colName, column_common);
					if (col_index >= 0) {
						String value = AhRestoreCommons.convertString(common[col_index]); 

						if (!"".equals(value)) {
							value = value.trim();
							switch (j) {
								case 0:
									cliBlob.setId(Long.parseLong(value));
									continue;
								case 1:
									cliBlob.setSupplementalName(value);
									continue;
								case 2:
									cliBlob.setDescription(value);
									continue;
								case 3:
									cliBlob.setContentAera(value);
									continue;
								case 4:
									cliBlob.setOwner(AhRestoreNewMapTools.getHmDomain(Long.parseLong(value)));
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

				listBo.add(cliBlob);
			}
		}

		List<Long> lOldId = new ArrayList<Long>();

		for (CLIBlob bo : listBo) {
			lOldId.add(bo.getId());
		}

		QueryUtil.restoreBulkCreateBos(listBo);

		for(int i=0; i < listBo.size(); ++i)
		{
			AhRestoreNewMapTools.setMapCLIBlob(lOldId.get(i), listBo.get(i).getId());
		}
		//QueryUtil.bulkCreateBos(listBo);
		return true;
	}

}
