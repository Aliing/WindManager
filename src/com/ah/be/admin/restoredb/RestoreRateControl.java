package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.QosRateLimit;

/*
 * support restoration for VHM
 * joseph chen, 05/04/2008
 */

public class RestoreRateControl {

	public void saveToDataBase() {
		try{
			saveQosRate();
		}
		catch(Exception e){
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
		}
	}

	private boolean saveQosRate() throws Exception{
		AhRestoreGetXML xmlFile= new AhRestoreGetXML();
		QosRateControl qos;
		if(!xmlFile.readXMLFile("QOS_RATE_CONTROL"))
			return false;
		List<String[]> list_common=xmlFile.getM_lst_xmlFile();
		String[] column_common=xmlFile.getM_str_colName();
		boolean bln_readFile=false;

		List<String[]> list_rate=null;
		String[] column_rate=null;

		if(list_common!=null && list_common.size()>0){
			List<QosRateControl> listBo=new ArrayList<QosRateControl>();
			for (String[] common : list_common) {
				qos = new QosRateControl();
				String id = "";
				for (int j = 0; j < qos.getFieldValues().length; j++) {
					String colName = qos.getFieldValues()[j];
					int col_index = AhRestoreCommons.checkColExist(colName, column_common);
					if (col_index >= 0) {
						String value = AhRestoreCommons.convertString(common[col_index]); // joseph chen , 06/02/2008

						if (!"".equals(value)) {
							value = value.trim();
							switch (j) {
								case 0:
									id = value;
									qos.setId(AhRestoreCommons.convertLong(id));
									continue;
								case 1:
									qos.setQosName(value);
									continue;
								case 2:
									int rate = AhRestoreCommons.convertInt(value);
									qos.setRateLimit(rate > 54000 ? 54000 : rate);
									continue;
								case 3:
									qos.setRateLimit11n(AhRestoreCommons.convertInt(value));
									continue;
								case 4:
									qos.setDefaultFlag(false);
									continue;
								case 5:
									qos.setDescription(value);
									continue;
								case 6:
									qos.setOwner(AhRestoreNewMapTools.getHmDomain(Long.parseLong(value)));
									continue;
								case 7:
									qos.setRateLimit11ac(AhRestoreCommons.convertInt(value));
									continue;
								default:
									continue;
							}
						}
					} else if (j == 3) {
						qos.setRateLimit11n(1000000);
					}
				}
				//read QOS_RATE_CONTROL_RATE_LIMIT from xml file
				if (!bln_readFile) {
					xmlFile = new AhRestoreGetXML();
					if (xmlFile.readXMLFile("QOS_RATE_CONTROL_RATE_LIMIT")) {
						list_rate = xmlFile.getM_lst_xmlFile();
						column_rate = xmlFile.getM_str_colName();
					}
					bln_readFile = true;
				}
				// the default value has been inserted before this
				if (BeParaModule.DEFAULT_QOS_RATE_CONTROL_NAME.equals(qos.getQosName())) {
					// set default ip object new id to map
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("qosName", qos.getQosName());
					QosRateControl newQos = HmBeParaUtil.getDefaultProfile(QosRateControl.class, map);
					if (null != newQos) {
						AhRestoreNewMapTools.setMapQosRateControlAndQueun(qos.getId(), newQos.getId());
					}
					continue;
				}

				//set QOS_RATE_CONTROL_RATE_LIMIT
				if (list_rate != null && list_rate.size() > 0)
					qos.setQosRateLimit(getListOfQosRateLimit(id, list_rate, column_rate));

				// set owner, joseph chen 05/04/2008
				//qos.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);

				listBo.add(qos);
			}
			if (!listBo.isEmpty()) {
				List<Long> lOldId = new ArrayList<Long>();

				for (QosRateControl bo : listBo) {
					lOldId.add(bo.getId());
				}

				QueryUtil.restoreBulkCreateBos(listBo);

				for(int i=0; i<listBo.size(); i++)
				{
					AhRestoreNewMapTools.setMapQosRateControlAndQueun(lOldId.get(i), listBo.get(i).getId());
				}
			}
		}

		return true;
	}

	private List<QosRateLimit> getListOfQosRateLimit(String id,List<String[]> list,String[] columns) {
		if(id==null || id.equals(""))
			return null;
		List<QosRateLimit> list_qos=new ArrayList<QosRateLimit>();
		QosRateLimit qos;

		for (String[] attrs : list) {
			qos = new QosRateLimit();
			boolean bln_break = false;
			for (int j = 0; j < qos.getFieldValues().length; j++) {
				String colName = qos.getFieldValues()[j];
				int col_index = AhRestoreCommons.checkColExist(colName, columns);
				if (col_index >= 0) {
					String value = AhRestoreCommons.convertString(attrs[col_index]); // joseph chen , 06/02/2008

					if (!"".equals(value)) {
						value = value.trim();
						switch (j) {
							case 0:
								if (!value.equals(id)) {
									bln_break = true;
									break;
								}
								continue;
							case 1:
								qos.setQosClass(Short.parseShort(value));
								continue;
							case 2:
								qos.setSchedulingType(Short.parseShort(value));
								continue;
							case 3:
								qos.setSchedulingWeight(Integer.parseInt(value));
								continue;
							case 4:
								int rate = AhRestoreCommons.convertInt(value);
								qos.setPolicingRateLimit(rate > 54000 ? 54000 : rate);
								continue;
							case 5:
								qos.setPolicing11nRateLimit(Integer.parseInt(value));
								continue;
							case 6:
								qos.setPolicing11acRateLimit(Integer.parseInt(value));
								continue;
							default:
								continue;
						}
					}
				} else if (j == 5) {
					if (!bln_break) {
						if (qos.getQosClass() == 6 || qos.getQosClass() == 7) {
							qos.setPolicing11nRateLimit(20000);
						} else {
							qos.setPolicing11nRateLimit(1000000);
						}
					}
				} else if (j==6){
					if (!bln_break) {
						if (qos.getQosClass() == 6 || qos.getQosClass() == 7) {
							qos.setPolicing11acRateLimit(20000);
						} else {
							qos.setPolicing11acRateLimit(1000000);
						}
					}
				}
			}
			if (bln_break)
				continue;
			list_qos.add(qos);
		}

		return list_qos.isEmpty() ? null : list_qos;
	}

}
