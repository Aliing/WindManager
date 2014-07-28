package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacFilterInfo;
import com.ah.bo.network.MacOrOui;

/*
 * modification history
 *
 * support restoration for VHM
 * joseph chen, 05/04/2008
 */

public class RestoreMacFilter {

	public  void saveToDabatase()
	{
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
		if(!xmlFile.readXMLFile("MAC_FILTER"))
			return false;
		List<String[]> list_common=xmlFile.getM_lst_xmlFile();
		String[] column_common=xmlFile.getM_str_colName();

		List<String[]> list_info=null;
		String[] column_info=null;
		boolean bln_readFile=false;

		if(list_common!=null && list_common.size()>0)
		{
			Long id=null;
			List<MacFilter> listBo=new ArrayList<MacFilter>();

			for (String[] common : list_common) {
				MacFilter filter = new MacFilter();
				for (int j = 0; j < filter.getFieldValues().length; j++) {
					String colName = filter.getFieldValues()[j];
					int col_index = AhRestoreCommons.checkColExist(colName, column_common);
					if (col_index >= 0) {
						String value = AhRestoreCommons.convertString(common[col_index]); // joseph chen , 06/02/2008
						if (!"".equals(value)) {
							switch (j) {
								case 0:
									id = Long.parseLong(value);
									filter.setId(id);
									continue;
								case 1:
									filter.setFilterName(value == null ? "" : value);
									continue;
								case 2:
									filter.setDescription(value == null ? "" : value);
									continue;
								case 3:
									filter.setOwner(AhRestoreNewMapTools.getHmDomain(Long.parseLong(value)));
									continue;
								default:
									continue;
							}
						}
					}
				}

				//read MAC_FILTER_MAC_OR_OUI from xml file
				if (!bln_readFile) {
					xmlFile = new AhRestoreGetXML();
					if (xmlFile.readXMLFile("MAC_FILTER_MAC_OR_OUI")) {
						list_info = xmlFile.getM_lst_xmlFile();
						column_info = xmlFile.getM_str_colName();
					}
					bln_readFile = true;
				}
				//set MAC_FILTER_MAC_OR_OUI
				if (list_info != null && list_info.size() > 0)
					filter.setFilterInfo(getListOfMacFilterInfo(id, list_info, column_info));

				// set owner, joseph chen 05/04/2008

				//filter.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);

				listBo.add(filter);
			}
			if (!listBo.isEmpty()) {
				List<Long> lOldId = new ArrayList<Long>();

				for (MacFilter bo : listBo) {
					lOldId.add(bo.getId());
				}

				QueryUtil.restoreBulkCreateBos(listBo);

				for(int i=0; i<listBo.size(); i++)
				{
					AhRestoreNewMapTools.setMapMacFilter(lOldId.get(i), listBo.get(i).getId());
				}
			}
		}

		return true;
	}

	private  List<MacFilterInfo> getListOfMacFilterInfo(Long id,List<String[]> list,String[] columns) {
		if(id==null)
			return null;
		List<MacFilterInfo> list_info=new ArrayList<MacFilterInfo>();
		MacFilterInfo filter_info;

		for (String[] attrs : list) {
			filter_info = new MacFilterInfo();
			boolean bln_break = false;
			for (int j = 0; j < filter_info.getFieldValues().length; j++) {
				String colName = filter_info.getFieldValues()[j];
				int col_index = AhRestoreCommons.checkColExist(colName, columns);
				if (col_index >= 0) {
					String value = AhRestoreCommons.convertString(attrs[col_index]); // joseph chen , 06/02/2008

					if (!"".equals(value)) {
						value = value.trim();
						switch (j) {
							case 0:
								if (!id.equals(Long.parseLong(value))) {
									bln_break = true;
									break;
								}
								continue;
							case 1:
								Long newMac = AhRestoreNewMapTools.getMapMacAddress(AhRestoreCommons.convertLong(value));

								if (null != newMac)
									filter_info.setMacOrOui(AhRestoreNewTools.CreateBoWithId(MacOrOui.class, newMac));
								continue;
							case 2:
								filter_info.setFilterAction(Short.parseShort(value));
								continue;
							default:
								continue;
						}
					}
					if (bln_break)
						break;
				}
			}
			if (bln_break)
				continue;
			list_info.add(filter_info);
		}
		return list_info;
	}

}