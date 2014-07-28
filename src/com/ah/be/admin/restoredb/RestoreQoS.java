package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.QosClassfierAndMarker;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosCustomService;
import com.ah.bo.mobility.QosMacOui;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.mobility.QosNetworkService;
import com.ah.bo.mobility.QosSsid;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.NetworkService;
import com.ah.bo.wlan.SsidProfile;

/*
 * support restoration for VHM
 * joseph chen, 05/04/2008
 *
 * enhancement some codes
 * Fiona Feng, 10/07/2008
 */

public class RestoreQoS {

//	private Long domainId = AhRestoreDBTools.HM_RESTORE_DOMAIN.getId();

	public void saveToDatabase() {
		try {

			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Qos Classifier and Marker configuration .........");
			saveClassifierMarker();
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Qos Classifier Map configuration .........");
			saveQosClassification();
//			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Qos Marker Map configuration .........");
//			saveQosMarking();
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
		}
	}
	
	public void saveToDatabaseForMarking(){
		try {
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Qos Marker Map configuration .........");
			saveQosMarking();
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
		}
	}

	public boolean saveClassifierMarker() throws Exception{
		AhRestoreGetXML xmlFile = new AhRestoreGetXML();
		QosClassfierAndMarker qos;
		if (!xmlFile.readXMLFile("QOS_CLASSFIER_AND_MARKER"))
			return false;
		List<String[]> list_common = xmlFile.getM_lst_xmlFile();
		String[] column_common = xmlFile.getM_str_colName();
		if (list_common != null && list_common.size() > 0) {
			List<QosClassfierAndMarker> listBo = new ArrayList<QosClassfierAndMarker>();
			for (String[] common : list_common) {
				Long id;
				qos = new QosClassfierAndMarker();
				for (int j = 0; j < qos.getFieldValues().length; j++) {
					String colName = qos.getFieldValues()[j];
					int col_index = AhRestoreCommons.checkColExist(colName,
							column_common);
					if (col_index >= 0) {
						String value = AhRestoreCommons.convertString(common[col_index]); // joseph chen , 06/02/2008

						if (!"".equals(value)) {
							value = value.trim();
							switch (j) {
								case 0:
									id = Long.parseLong(value);
									qos.setId(id);
									continue;
								case 1:
									qos.setQosName(value);
									continue;
								case 2:
									qos.setDescription(value);
									continue;
								case 3:
									qos.setNetworkServicesEnabled(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 4:
									qos.setMacOuisEnabled(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 5:
									qos.setSsidEnabled(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 6:
									qos.setMarksEnabled(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 7:
									qos.setCheckD(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 8:
									qos.setCheckE(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 9:
									qos.setCheckP(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 10:
									qos.setCheckDT(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 11:
									qos.setCheckET(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 12:
									qos.setCheckPT(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 13:
									qos.setOwner(AhRestoreNewMapTools.getHmDomain(Long.parseLong(value)));
									continue;
								default:
									continue;
							}
						}
					}
				}
				//AhRestoreMapTool.setMapQosClassificationAndMark(String.valueOf(qos.getId()), qos.getQosName());

				// set owner, joseph chen 05/04/2008
				//qos.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);
				if (qos.getQosName() != null && !"".equals(qos.getQosName().trim())) {
					listBo.add(qos);
				}
			}

			List<Long> lOldId = new ArrayList<Long>();

			for (QosClassfierAndMarker bo : listBo) {
				lOldId.add(bo.getId());
			}

			QueryUtil.restoreBulkCreateBos(listBo);

			for(int i=0; i < listBo.size(); ++i)
			{
				AhRestoreNewMapTools.setMapQosClassificationAndMark(lOldId.get(i), listBo.get(i).getId());
			}

			//QueryUtil.bulkCreateBos(listBo);
		}

		return true;
	}
	private boolean saveQosClassification() throws Exception {
		AhRestoreGetXML xmlFile = new AhRestoreGetXML();
		QosClassification qos;
		if (!xmlFile.readXMLFile("QOS_CLASSIFICATION"))
			return false;
		List<String[]> list_common = xmlFile.getM_lst_xmlFile();
		String[] column_common = xmlFile.getM_str_colName();

		List<String[]> list_service = null;
		String[] column_service = null;
		boolean bln_readFile_service = false;
		
		List<String[]> list_customservice = null;
		String[] column_customservice = null;
		boolean bln_readFile_customservice = false;

		List<String[]> list_mac = null;
		String[] column_mac = null;
		boolean bln_readFile_mac = false;

		List<String[]> list_ssid = null;
		String[] column_ssid = null;
		boolean bln_readFile_ssid = false;

		if (list_common != null && list_common.size() > 0) {
			List<QosClassification> listBo = new ArrayList<QosClassification>();
			List<QosClassification> policyNameEmpty = new ArrayList<QosClassification>();
			
			for (String[] common : list_common) {
				Long id = null;
				qos = new QosClassification();
				for (int j = 0; j < qos.getFieldValues().length; j++) {
					String colName = qos.getFieldValues()[j];
					int col_index = AhRestoreCommons.checkColExist(colName,
							column_common);
					if (col_index >= 0) {
						String value = AhRestoreCommons.convertString(common[col_index]); // joseph chen , 06/02/2008

						if (!"".equals(value)) {
							value = value.trim();
							switch (j) {
								case 0:
									id = Long.parseLong(value);
									qos.setId(id);
									continue;
								case 1:
									qos.setClassificationName(value);
									continue;
								case 2:
									qos.setDescription(value);
									continue;
								case 3:
									qos.setNetworkServicesEnabled(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 4:
									qos.setMacOuisEnabled(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 5:
									qos.setSsidEnabled(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 6:
									qos.setPrtclE(value);
									continue;
								case 7:
									qos.setPrtclP(value);
									continue;
								case 8:
									qos.setPrtclD(value);
									continue;
								case 9:
									qos.setMarksEnabled(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 10:
									qos.setGeneralEnabled(AhRestoreCommons.convertStringToBoolean(value));
									continue;
								case 11:
									qos.setOwner(AhRestoreNewMapTools.getHmDomain(Long.parseLong(value)));
									continue;
								default:
									continue;
							}
						}
					}
				}
				
				if (qos.getClassificationName()==null || qos.getClassificationName().equals("")) {
					BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'QOS_CLASSIFICATION' will reset the hive name, cause: 'ClassificationName' column value is null.");
					HmDomain dm = QueryUtil.findBoById(HmDomain.class, qos.getOwner().getId());
					if (dm!=null) {
						qos.setClassificationName(dm.getDomainName());
						policyNameEmpty.add(qos);
					} else {
						BeLogTools.debug(BeLogTools.DEBUG, "Restore table 'QOS_CLASSIFICATION' will lost data, cause: 'ClassificationName' column value is null.");
						continue;
					}
				}
				
				// read QOS_CLASSIFICATION_SERVICE from xml file
				if (!bln_readFile_service) {
					xmlFile = new AhRestoreGetXML();
					if (xmlFile.readXMLFile("QOS_CLASSIFICATION_SERVICE")) {
						list_service = xmlFile.getM_lst_xmlFile();
						column_service = xmlFile.getM_str_colName();
					}
					bln_readFile_service = true;
				}
				// set QOS_CLASSIFICATION_SERVICE
				if (list_service != null && list_service.size() > 0)
					qos.setNetworkServices(getListOfNetworkService(id,
							list_service, column_service));
				
				// read QOS_CLASSIFICATION_CUSTOMSERVICE from xml file
				if (!bln_readFile_customservice) {
					xmlFile = new AhRestoreGetXML();
					if (xmlFile.readXMLFile("QOS_CLASSIFICATION_CUSTOMSERVICE")) {
						list_customservice = xmlFile.getM_lst_xmlFile();
						column_customservice = xmlFile.getM_str_colName();
					}
					bln_readFile_customservice = true;
				}
				// set QOS_CLASSIFICATION_CUSTOMSERVICE
				if (list_customservice != null && list_customservice.size() > 0)
					qos.setCustomServices(getListOfCustomService(id,list_customservice, column_customservice));

				// read QOS_CLASSIFICATION_MAC from xml file
				if (!bln_readFile_mac) {
					xmlFile = new AhRestoreGetXML();
					if (xmlFile.readXMLFile("QOS_CLASSIFICATION_MAC")) {
						list_mac = xmlFile.getM_lst_xmlFile();
						column_mac = xmlFile.getM_str_colName();
					}
					bln_readFile_mac = true;
				}
				// set QOS_CLASSIFICATION_MAC
				if (list_mac != null && list_mac.size() > 0)
					qos.setQosMacOuis(getListOfQosMacOui(id, list_mac,
							column_mac));

				// read QOS_CLASSIFICATION_SSID from xml file
				if (!bln_readFile_ssid) {
					xmlFile = new AhRestoreGetXML();
					if (xmlFile.readXMLFile("QOS_CLASSIFICATION_SSID")) {
						list_ssid = xmlFile.getM_lst_xmlFile();
						column_ssid = xmlFile.getM_str_colName();
					}
					bln_readFile_ssid = true;
				}
				// set QOS_CLASSIFICATION_MAC
				if (list_ssid != null && list_ssid.size() > 0)
					qos.setQosSsids(getListOfSsid(id, list_ssid,
							column_ssid));

//				AhRestoreMapTool.setMapQosClassification(String.valueOf(qos
//						.getId()), qos.getClassificationName());

				/*
								 * set general
								 * in case the version restored is earlier than 3.5
								 */
				if (qos.getPrtclD() != null
						|| qos.getPrtclE() != null
						|| qos.getPrtclP() != null) {
					qos.setGeneralEnabled(true);
				}

				// set owner, joseph chen 05/04/2008
				//qos.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);
				if (qos.getClassificationName() != null && !"".equals(qos.getClassificationName().trim())) {
					listBo.add(qos);
				}
			}
			
			// fix bug 27502
			List<QosClassification> policyNameExist = new ArrayList<QosClassification>();
			if (!policyNameEmpty.isEmpty()) {
				for(QosClassification hm: policyNameEmpty) {
					for(QosClassification h: listBo) {
						if (!hm.getId().equals(h.getId()) 
								&& hm.getClassificationName().equals(h.getClassificationName())
								&& hm.getOwner().getId().equals(h.getOwner().getId())) {
							policyNameExist.add(hm);
							break;
						}
					}
				}
			}
			if (!policyNameExist.isEmpty()) {
				int i=1;
				for(QosClassification h: policyNameExist){
					boolean loopFlg=true;
					while (loopFlg) {
						boolean existFlg = false;
						String hName = h.getClassificationName();
						if (hName.length()>28) {
							hName=hName.substring(0, 28) + "_" + i++;
						} else {
							hName=hName + "_" + i++;
						}
						for(QosClassification hif: listBo) {
							if (hif.getClassificationName().equals(hName)
									&& hif.getOwner().getId().equals(h.getOwner().getId())) {
								existFlg = true;
								break;
							}
						}
						if (existFlg==false) {
							loopFlg=false;
							h.setClassificationName(hName);
						}
					}
				}
			}
			for(QosClassification h: policyNameExist){
				for(QosClassification hif : listBo){
					if(hif.getId().equals(h.getId())){
						hif.setClassificationName(h.getClassificationName());
					}
				}
			}
			
			//end fix bug 27502
			
			List<Long> lOldId = new ArrayList<Long>();

			for (QosClassification bo : listBo) {
				lOldId.add(bo.getId());
			}

			QueryUtil.restoreBulkCreateBos(listBo);

			for(int i=0; i < listBo.size(); ++i)
			{
				AhRestoreNewMapTools.setMapQosClassification(lOldId.get(i), listBo.get(i).getId());
			}

			//QueryUtil.bulkCreateBos(listBo);
		}

		return true;
	}

	private boolean saveQosMarking() throws Exception {
		AhRestoreGetXML xmlFile = new AhRestoreGetXML();
		QosMarking qos;
		if (!xmlFile.readXMLFile("QOS_MARKING"))
			return false;
		List<String[]> list_common = xmlFile.getM_lst_xmlFile();
		String[] column_common = xmlFile.getM_str_colName();

		if (list_common != null && list_common.size() > 0) {
			List<QosMarking> listBo = new ArrayList<QosMarking>();
			for (String[] common : list_common) {
				qos = new QosMarking();
				for (int j = 0; j < qos.getFieldValues().length; j++) {
					String colName = qos.getFieldValues()[j];
					int col_index = AhRestoreCommons.checkColExist(colName,
							column_common);
					if (col_index >= 0) {
						String value = AhRestoreCommons.convertString(common[col_index]); // joseph chen , 06/02/2008

						if (!"".equals(value)) {
							value = value.trim();
							switch (j) {
								case 0:
									qos.setId(Long.parseLong(value));
									continue;
								case 1:
									qos.setQosName(value);
									continue;
								case 2:
									qos.setPrtclP(value);
									continue;
								case 3:
									qos.setPrtclD(value);
									continue;
								case 4:
									qos.setDescription(value);
									continue;
								case 5:
									qos.setPrtclE(value);
									continue;
								case 6:
									qos.setOwner(AhRestoreNewMapTools.getHmDomain(Long.parseLong(value)));
									continue;
								default:
									continue;
							}
						}
					}
				}
				//AhRestoreMapTool.setMapMarking(String.valueOf(qos.getId()), qos
				//		.getQosName());

				// set owner, joseph chen 05/04/2008
				//qos.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);

				listBo.add(qos);
			}


			List<Long> lOldId = new ArrayList<Long>();

			for (QosMarking bo : listBo) {
				lOldId.add(bo.getId());
			}

			QueryUtil.restoreBulkCreateBos(listBo);

			for(int i=0; i < listBo.size(); ++i)
			{
				AhRestoreNewMapTools.setMapMarking(lOldId.get(i), listBo.get(i).getId());
			}
			//QueryUtil.bulkCreateBos(listBo);
		}

		return true;
	}

	private Map<Long, QosNetworkService> getListOfNetworkService(Long id,
			List<String[]> list, String[] columns) {
		if (id == null)
			return null;
		Map<Long, QosNetworkService> list_qos = new HashMap<Long, QosNetworkService>();
		QosNetworkService qos;

		for (String[] attrs : list) {
			qos = new QosNetworkService();
			boolean bln_break = false;
			for (int j = 0; j < qos.getFieldValues().length; j++) {
				String colName = qos.getFieldValues()[j];
				int col_index = AhRestoreCommons
						.checkColExist(colName, columns);
				if (col_index >= 0) {
					String value = AhRestoreCommons.convertString(attrs[col_index]); // joseph chen , 06/02/2008

					if (!"".equals(value)) {
						value = value.trim();
						switch (j) {
							case 0:
								Long id_value = Long.parseLong(value);
								if (!id_value.equals(id)) {
									bln_break = true;
									break;
								}
								continue;
							case 1:
								Long newNetworkServiceId = AhRestoreNewMapTools.getMapNetworkService(Long.parseLong(value.trim()));
								qos.setNetworkService(AhRestoreNewTools.CreateBoWithId(NetworkService.class, newNetworkServiceId));
								continue;
							case 2:
								qos.setQosClass(Short.parseShort(value));
								continue;
							case 3:
								qos.setFilterAction(Short.parseShort(value));
								continue;
							case 4:
								qos.setLogging(Short.parseShort(value));
								continue;
							default:
								continue;
						}
					}
				}
			}
			if (bln_break)
				continue;
			if (null != qos.getNetworkService()) {
				list_qos.put(qos.getNetworkService().getId(), qos);
			}
		}

		return list_qos;
	}
	
	private Map<Long, QosCustomService> getListOfCustomService(Long id,
			List<String[]> list, String[] columns) {
		if (id == null)
			return null;
		Map<Long, QosCustomService> list_qos = new HashMap<Long, QosCustomService>();
		QosCustomService qcs;

		for (String[] attrs : list) {
			qcs = new QosCustomService();
			boolean bln_break = false;
			for (int j = 0; j < qcs.getFieldValues().length; j++) {
				String colName = qcs.getFieldValues()[j];
				int col_index = AhRestoreCommons
						.checkColExist(colName, columns);
				if (col_index >= 0) {
					String value = AhRestoreCommons.convertString(attrs[col_index]);

					if (!"".equals(value)) {
						value = value.trim();
						switch (j) {
							case 0:
								Long id_value = Long.parseLong(value);
								if (!id_value.equals(id)) {
									bln_break = true;
									break;
								}
								continue;
							case 1:
								CustomApplication newCustomApp = AhRestoreNewMapTools.getMapCustomApplication(Long.parseLong(value.trim()));
								qcs.setCustomAppService(newCustomApp);
								continue;
							case 2:
								qcs.setQosClass(Short.parseShort(value));
								continue;
							case 3:
								qcs.setFilterAction(Short.parseShort(value));
								continue;
							case 4:
								qcs.setLogging(Short.parseShort(value));
								continue;
							default:
								continue;
						}
					}
				}
			}
			if (bln_break)
				continue;
			if (null != qcs.getCustomAppService()) {
				list_qos.put(qcs.getCustomAppService().getId(), qcs);
			}
		}

		return list_qos;
	}

	private Map<Long,QosMacOui> getListOfQosMacOui(Long id, List<String[]> list,
			String[] columns) {
		if (id == null)
			return null;
		Map<Long,QosMacOui> list_qos =new HashMap<Long, QosMacOui>();
		QosMacOui qos;

		for (String[] attrs : list) {
			qos = new QosMacOui();
			boolean bln_break = false;
			for (int j = 0; j < qos.getFieldValues().length; j++) {
				String colName = qos.getFieldValues()[j];
				int col_index = AhRestoreCommons
						.checkColExist(colName, columns);
				if (col_index >= 0) {
					String value = AhRestoreCommons.convertString(attrs[col_index]); // joseph chen , 06/02/2008

					if (!"".equals(value)) {
						value = value.trim();
						switch (j) {
							case 0:
								Long id_value = Long.parseLong(value);
								if (!id_value.equals(id)) {
									bln_break = true;
									break;
								}
								continue;
							case 1:
								Long newMacOrOuiId = AhRestoreNewMapTools.getMapMacAddress(Long.parseLong(value.trim()));
								qos.setMacOui(AhRestoreNewTools.CreateBoWithId(MacOrOui.class, newMacOrOuiId));
								continue;
							case 2:
								qos.setMacEntry(value);
								continue;
							case 3:
								qos.setQosClassMacOuis(Short.parseShort(value));
								continue;
							case 4:
								qos.setFilterActionMacOuis(Short.parseShort(value));
								continue;
							case 5:
								qos.setLoggingMacOuis(Short.parseShort(value));
								continue;
							case 6:
								qos.setComment(value);
								continue;
							default:
								continue;
						}
					}
					// from 3.1 to 3.2
				} else if (j == 6) {
					// set the description of default mac oui to this
					if (null != qos.getMacOui() && qos.getMacOui().getDefaultFlag()) {
						qos.setComment(qos.getMacOui().getItems().get(0).getDescription());
					} else {
						qos.setComment("");
					}
				}
			}
			if (bln_break)
				continue;
			if (null != qos.getMacOui()) {
				list_qos.put(qos.getMacOui().getId(), qos);
			}
		}

		return list_qos;
	}
	private Map<Long,QosSsid> getListOfSsid(Long id, List<String[]> list,
			String[] columns) {
		if (id == null)
			return null;
		Map<Long,QosSsid> list_qos =new HashMap<Long, QosSsid>();
		QosSsid qos;

		for (String[] attrs : list) {
			qos = new QosSsid();
			boolean bln_break = false;
			for (int j = 0; j < qos.getFieldValues().length; j++) {
				String colName = qos.getFieldValues()[j];
				int col_index = AhRestoreCommons
						.checkColExist(colName, columns);
				if (col_index >= 0) {
					String value = AhRestoreCommons.convertString(attrs[col_index]); // joseph chen , 06/02/2008

					if (!"".equals(value)) {
						value = value.trim();
						switch (j) {
							case 0:
								Long id_value = Long.parseLong(value);
								if (!id_value.equals(id)) {
									bln_break = true;
									break;
								}
								continue;
							case 1:
								Long newSsidProfileId = AhRestoreNewMapTools.getMapSsid(Long.parseLong(value.trim()));
								qos.setSsid(AhRestoreNewTools.CreateBoWithId(SsidProfile.class, newSsidProfileId));
								continue;
							case 2:
								qos.setQosClassSsids(Short.parseShort(value));
								continue;
							default:
								continue;
						}
					}
				}
			}
			if (bln_break)
				continue;
			if (null != qos.getSsid()) {
				list_qos.put(qos.getSsid().getId(), qos);
			}
		}

		return list_qos;
	}

}