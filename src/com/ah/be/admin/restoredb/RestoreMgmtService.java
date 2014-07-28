package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.admin.restoredb.AhRestoreCommons;
import com.ah.be.admin.restoredb.RestoreHiveAp;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceDnsInfo;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSnmpInfo;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceSyslogInfo;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.MgmtServiceTimeInfo;
import com.ah.bo.useraccess.MulticastForwarding;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.util.CreateObjectAuto;

/*
 * modification history
 *
 * modify 'saveMgmtServiceOption', handle property 'disableConsolePort'
 * joseph chen, 04/09/2008
 *
 * support restoration for VHM
 * joseph chen, 05/04/2008
 *
 * add 'disable trap over capwap'
 * joseph chen, 07/18/2008
 *
 * add 'saveMgmtServiceIpTrack()'
 * Fiona Feng, 08/05/2008
 *
 * add ICSA in mgmt option
 * Fiona Feng, 09/11/2008
 */

public class RestoreMgmtService {

	private  AhRestoreGetXML xmlFile=null;

	public  void saveToDatabase(){
			try {
				BeLogTools.showshellLog(BeLogTools.INFO, "restore DNS Assignments confituration .........");
				saveMgmtServiceDns();
			} catch (Exception e1) {
				BeLogTools.showshellLog(BeLogTools.ERROR, "restore DNS Assignments confituration failed");
				AhRestoreDBTools.logRestoreMsg(e1.getMessage());
			}
			
			try {
				BeLogTools.showshellLog(BeLogTools.INFO, "restore Syslog Assignments confituration .........");
				saveMgmtServiceSyslog();
			} catch (Exception e1) {
				BeLogTools.showshellLog(BeLogTools.ERROR, "restore Syslog Assignments confituration failed");
				AhRestoreDBTools.logRestoreMsg(e1.getMessage());
			}
			
			try {
				BeLogTools.showshellLog(BeLogTools.INFO, "restore SNMP Assignments confituration .........");
				saveMgmtServiceSnmp();
			} catch (Exception e1) {
				BeLogTools.showshellLog(BeLogTools.ERROR, "restore SNMP Assignments confituration failed");
				AhRestoreDBTools.logRestoreMsg(e1.getMessage());
			}
			
			try {
				BeLogTools.showshellLog(BeLogTools.INFO, "restore NTPs Assignments confituration .........");
				saveMgmtServiceTime();
			} catch (Exception e1) {
				BeLogTools.showshellLog(BeLogTools.ERROR, "restore NTPs Assignments confituration failed");
				AhRestoreDBTools.logRestoreMsg(e1.getMessage());
			}
			
			try {
				BeLogTools.showshellLog(BeLogTools.INFO, "restore IP Tracking confituration .........");
				saveMgmtServiceIpTrack();
			} catch (AhRestoreColNotExistException e1) {
				BeLogTools.showshellLog(BeLogTools.ERROR, "restore IP Tracking confituration column not exist");
				AhRestoreDBTools.logRestoreMsg(e1.getMessage());
			} catch (AhRestoreException e1) {
				BeLogTools.showshellLog(BeLogTools.ERROR, "restore IP Tracking confituration failed");
				AhRestoreDBTools.logRestoreMsg(e1.getMessage());
			}
		
	}
	public void saveOperationToDataBase()
	{
		try {
			saveMgmtServiceOption();
		} catch(Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
		}
	}
	private  boolean saveMgmtServiceDns() throws Exception{
		xmlFile=new AhRestoreGetXML();
		if(!xmlFile.readXMLFile("MGMT_SERVICE_DNS"))
			return false;
		List<String[]> list=xmlFile.getM_lst_xmlFile();
		String[] columns=xmlFile.getM_str_colName();
		xmlFile=null;

		//List<String[]> list_info=null;
		//String[] column_info=null;
		//boolean bln_readFile=false;

		if(list!=null && list.size()>0){
			List<MgmtServiceDns> listBo=new ArrayList<MgmtServiceDns>();
			Map<String, List<MgmtServiceDnsInfo>> list_info = getAllMgmtServiceDnsInfo();
			overlap:
			for (String[] attrs : list) {
				Long id = null;
				MgmtServiceDns bo = new MgmtServiceDns();
				for (int j = 0; j < bo.getFieldValues().length; j++) {
					String colName = bo.getFieldValues()[j];
					int col = AhRestoreCommons.checkColExist(colName, columns);
					if (col >= 0) {
						String value = AhRestoreCommons.convertString(attrs[col]); // joseph chen , 06/02/2008

						if (!"".equals(value)) {
							value = value.trim();
							switch (j) {
								case 0:
									id = Long.parseLong(value);
									bo.setId(id);
									break;
								case 1:
									bo.setDomainName(value);
									break;
								case 2:
									bo.setMgmtName(value);
									break;
								case 3:
									bo.setDescription(value);
									break;
								case 4:
									HmDomain hmDom = AhRestoreNewMapTools.getHmDomain(Long.parseLong(value));
									if (null == hmDom)
										continue overlap;
									bo.setOwner(hmDom);
									break;
								default:
									break;
							}
						}
					}
				}
				
				if (bo.getMgmtName()==null || bo.getMgmtName().equals("")) {
					HmDomain dm = QueryUtil.findBoById(HmDomain.class, bo.getOwner().getId());
					if (dm!=null) {
						bo.setMgmtName(dm.getDomainName());
					}
				}
				
				//read info from xml file and set info
				if (null != list_info) {
					List<MgmtServiceDnsInfo> dnsInfo = list_info.get(String.valueOf(id));
					if (null != dnsInfo) {
						Collections.sort(dnsInfo,
							new Comparator<MgmtServiceDnsInfo>() {
								public int compare(MgmtServiceDnsInfo dns1, MgmtServiceDnsInfo dns2) {
									Integer id1 = dns1.getPosition();
									Integer id2 = dns2.getPosition();
									return id1.compareTo(id2);
								}
							});
						bo.setDnsInfo(dnsInfo);
					}
				}

				// set owner, joseph chen 05/04/2008
				//bo.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);

				listBo.add(bo);
			}
			if (!listBo.isEmpty()) {
				List<Long> lOldId = new ArrayList<Long>();

				for (MgmtServiceDns bo : listBo) {
					lOldId.add(bo.getId());
				}

				QueryUtil.restoreBulkCreateBos(listBo);

				for(int i=0; i<listBo.size(); i++)
				{
					AhRestoreNewMapTools.setMapDns(lOldId.get(i), listBo.get(i).getId());
				}
			}
		}
		return true;
	}
	private  boolean saveMgmtServiceSyslog()throws Exception{
		xmlFile=new AhRestoreGetXML();
		if(!xmlFile.readXMLFile("MGMT_SERVICE_SYSLOG"))
			return false;
		List<String[]> list=xmlFile.getM_lst_xmlFile();
		String[] columns=xmlFile.getM_str_colName();

		List<String[]> list_info=null;
		String[] column_info=null;
		boolean bln_readFile=false;

		if(list!=null && list.size()>0){
			List<MgmtServiceSyslog> listBo=new ArrayList<MgmtServiceSyslog>();
			overlap:
			for (String[] attrs : list) {
				MgmtServiceSyslog bo = new MgmtServiceSyslog();
				Long id = null;
				for (int j = 0; j < bo.getFieldValues().length; j++) {
					String colName = bo.getFieldValues()[j];
					int col = AhRestoreCommons.checkColExist(colName, columns);
					if (col >= 0) {
						String value = AhRestoreCommons.convertString(attrs[col]); // joseph chen , 06/02/2008
						if (!"".equals(value)) {
							value = value.trim();
							switch (j) {
								case 0:
									id = Long.parseLong(value);
									bo.setId(id);
									break;
								case 1:
									//bo.setServerName(value);
									break;
								case 2:
									bo.setMgmtName(value);
									break;
								case 3:
									bo.setDescription(value);
									break;
								case 4:
									bo.setFacility(Short.parseShort(value));
									break;
								case 5:
									bo.setInternalServer(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 6:
									HmDomain hmDom = AhRestoreNewMapTools.getHmDomain(Long.parseLong(value));
									if (null == hmDom)
										continue overlap;
									bo.setOwner(hmDom);
									break;
								default:
									break;
							}
						}
					}
				}
				//read info from xml file
				if (!bln_readFile) {
					xmlFile = new AhRestoreGetXML();
					if (xmlFile.readXMLFile("MGMT_SERVICE_SYSLOG_INFO")) {
						list_info = xmlFile.getM_lst_xmlFile();
						column_info = xmlFile.getM_str_colName();
					}
					bln_readFile = true;
					xmlFile = null;

				}
				//set info
				if (list_info != null && list_info.size() > 0)
					bo.setSyslogInfo(getListOfSyslogInfo(id, bo.getMgmtName(), list_info, column_info, bo.getOwner()));

				// set owner, joseph chen 05/04/2008
				//bo.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);

				listBo.add(bo);
			}
			if (!listBo.isEmpty()) {
				List<Long> lOldId = new ArrayList<Long>();

				for (MgmtServiceSyslog bo : listBo) {
					lOldId.add(bo.getId());
				}

				QueryUtil.restoreBulkCreateBos(listBo);

				for(int i=0; i<listBo.size(); i++)
				{
					AhRestoreNewMapTools.setMapSyslog(lOldId.get(i), listBo.get(i).getId());
				}
			}
		}
		return true;
	}
	private  boolean saveMgmtServiceSnmp() throws Exception{

		xmlFile=new AhRestoreGetXML();
		if(!xmlFile.readXMLFile("MGMT_SERVICE_SNMP"))
			return false;
		List<String[]> list=xmlFile.getM_lst_xmlFile();
		String[] columns=xmlFile.getM_str_colName();
		xmlFile=null;

		List<String[]> list_info=null;
		String[] column_info=null;
		boolean bln_readFile=false;

		if(list!=null && list.size()>0){
			List<MgmtServiceSnmp> listBo=new ArrayList<MgmtServiceSnmp>();
			List<HmUpgradeLog> listLogs=new ArrayList<HmUpgradeLog>();
			overlap:
			for (String[] attrs : list) {
				Long id = null;
				MgmtServiceSnmp bo = new MgmtServiceSnmp();
				for (int j = 0; j < bo.getFieldValues().length; j++) {
					String colName = bo.getFieldValues()[j];
					int col = AhRestoreCommons.checkColExist(colName, columns);
					if (col >= 0) {
						String value = AhRestoreCommons.convertString(attrs[col]); // joseph chen , 06/02/2008
						if (!"".equals(value)) {
							value = value.trim();
							switch (j) {
								case 0:
									id = Long.parseLong(value);
									bo.setId(id);
									break;
								case 1:
									bo.setMgmtName(value);
									break;
								case 2:
									bo.setDescription(value);
									break;
								case 3:
									bo.setEnableSnmp(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 4:
									bo.setDefaultFlag(false);
									break;
								case 5:
									bo.setContact(value);
									break;
								case 6:
									bo.setEnableCapwap(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 7:
									HmDomain hmDom = AhRestoreNewMapTools.getHmDomain(Long.parseLong(value));
									if (null == hmDom)
										continue overlap;
									bo.setOwner(hmDom);
									break;
								default:
									break;
							}
						}
					}
				}
				//read info from xml file
				if (!bln_readFile) {
					xmlFile = new AhRestoreGetXML();
					if (xmlFile.readXMLFile("MGMT_SERVICE_SNMP_INFO")) {
						list_info = xmlFile.getM_lst_xmlFile();
						column_info = xmlFile.getM_str_colName();
					}
					bln_readFile = true;
					xmlFile = null;

				}

				// the default value has been inserted before this
				if (bo.getMgmtName().equals(BeParaModule.DEFAULT_SERVICE_SNMP_NAME)) {
					// set default snmp object new id to map
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("mgmtName", bo.getMgmtName());
					MgmtServiceSnmp newSnmp = HmBeParaUtil.getDefaultProfile(MgmtServiceSnmp.class, map);
					if (null != newSnmp) {
						AhRestoreNewMapTools.setMapSnmp(bo.getId(), newSnmp.getId());
					}
					continue;
				}

				//set info
				if (list_info != null && list_info.size() > 0) {
					List<HmUpgradeLog> lstLogBo = new ArrayList<HmUpgradeLog>();
					bo.setSnmpInfo(getListOfSnmpInfo(id, bo.getMgmtName(), list_info, column_info, bo.getEnableCapwap(), lstLogBo, bo.getOwner()));
					listLogs.addAll(lstLogBo);
				}

				// set owner, joseph chen 05/04/2008
				//bo.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);

				listBo.add(bo);
			}
			if (!listBo.isEmpty()) {
				List<Long> lOldId = new ArrayList<Long>();

				for (MgmtServiceSnmp bo : listBo) {
					lOldId.add(bo.getId());
				}

				QueryUtil.restoreBulkCreateBos(listBo);

				for(int i=0; i<listBo.size(); i++)
				{
					AhRestoreNewMapTools.setMapSnmp(lOldId.get(i), listBo.get(i).getId());
				}
			}
			/*
			 * insert or update the data to database
			 */
			if (listLogs.size() > 0) {
				try {
					QueryUtil.restoreBulkCreateBos(listLogs);
				} catch (Exception e) {
					AhRestoreDBTools.logRestoreMsg("insert ip object or host name option for SNMP upgrade log error");
					AhRestoreDBTools.logRestoreMsg(e.getMessage());
				}
			}
		}
		return true;
	}
	private  boolean saveMgmtServiceTime()throws Exception{

		xmlFile=new AhRestoreGetXML();
		if(!xmlFile.readXMLFile("MGMT_SERVICE_TIME"))
			return false;
		List<String[]> list=xmlFile.getM_lst_xmlFile();
		String[] columns=xmlFile.getM_str_colName();
		xmlFile=null;

		List<String[]> list_info=null;
		String[] column_info=null;
		boolean bln_readFile=false;

		if(list!=null && list.size()>0){
			List<MgmtServiceTime> listBo=new ArrayList<MgmtServiceTime>();
			overlap:
			for (String[] attrs : list) {
				Long id = null;
				MgmtServiceTime bo = new MgmtServiceTime();
				for (int j = 0; j < bo.getFieldValues().length; j++) {
					String colName = bo.getFieldValues()[j];
					int col = AhRestoreCommons.checkColExist(colName, columns);
					if (col >= 0) {
						String value = AhRestoreCommons.convertString(attrs[col]); // joseph chen , 06/02/2008
						if (!"".equals(value)) {
							value = value.trim();
							switch (j) {
								case 0:
									id = Long.parseLong(value);
									bo.setId(id);
									break;
								case 1:
									bo.setMgmtName(value);
									break;
								case 2:
									bo.setDescription(value);
									break;
								case 3:
									bo.setInterval(Integer.parseInt(value));
									break;
								case 4:
									bo.setEnableNtp(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 5:
									bo.setEnableClock(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 6:
									int timezoneIndex = AhRestoreCommons.convertInt(value);
									if (RestoreUsersAndAccess.RESTORE_FROM_40R1_BEFORE) {
										bo.setTimeZoneStr(HmBeOsUtil.getNewTimeZoneByOldOne(timezoneIndex));
									} else {
										bo.setTimeZoneStr(HmBeOsUtil.getTimeZoneString(timezoneIndex));
									}
									break;
								case 7:
									HmDomain hmDom = AhRestoreNewMapTools.getHmDomain(Long.parseLong(value));
									if (null == hmDom)
										continue overlap;
									bo.setOwner(hmDom);
									break;
								case 8:
									bo.setTimeZoneStr(AhRestoreCommons.convertString(value));
									break;
								default:
									break;
							}
						}
					}
				}
				
				if (bo.getMgmtName()==null || bo.getMgmtName().equals("")) {
					HmDomain dm = QueryUtil.findBoById(HmDomain.class, bo.getOwner().getId());
					if (dm!=null) {
						bo.setMgmtName(dm.getDomainName());
					}
				}
				
				//read info from xml file
				if (!bln_readFile) {
					xmlFile = new AhRestoreGetXML();
					if (xmlFile.readXMLFile("MGMT_SERVICE_TIME_INFO")) {
						list_info = xmlFile.getM_lst_xmlFile();
						column_info = xmlFile.getM_str_colName();
					}
					bln_readFile = true;
					xmlFile = null;

				}
				//set info
				if (list_info != null && list_info.size() > 0)
					bo.setTimeInfo(getListOfTimeInfo(id, bo.getMgmtName(), list_info, column_info, bo.getOwner()));

				// set owner, joseph chen 05/04/2008
				//bo.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);

				listBo.add(bo);
			}
			if (!listBo.isEmpty()) {
				List<Long> lOldId = new ArrayList<Long>();

				for (MgmtServiceTime bo : listBo) {
					lOldId.add(bo.getId());
				}

				QueryUtil.restoreBulkCreateBos(listBo);

				for(int i=0; i<listBo.size(); i++)
				{
					AhRestoreNewMapTools.setMapTimeAndDate(lOldId.get(i), listBo.get(i).getId());
				}
			}
		}
		return true;
	}
	private  boolean saveMgmtServiceOption()throws Exception{
		xmlFile=new AhRestoreGetXML();
		if(!xmlFile.readXMLFile("MGMT_SERVICE_OPTION"))
			return false;
		List<String[]> list=xmlFile.getM_lst_xmlFile();
		String[] columns=xmlFile.getM_str_colName();
		xmlFile=null;

		if(list!=null && list.size()>0){
			List<MgmtServiceOption> listBo=new ArrayList<MgmtServiceOption>();
			//add by nxma
			Map<String, List<MulticastForwarding>> allMulticast = null;
			try {
				allMulticast = getAllMultipleVlan();
			} catch (Exception e1) {
				AhRestoreDBTools.logRestoreMsg("Cannot get Multicast Forwarding from xml file.",e1);
			}

			overlap:
			for (String[] attrs : list) {
				Long id;
				MgmtServiceOption bo = new MgmtServiceOption();
				for (int j = 0; j < bo.getFieldValues().length; j++) {

					String colName = bo.getFieldValues()[j];
					int col = AhRestoreCommons.checkColExist(colName, columns);
					if (col >= 0) {
						String value = AhRestoreCommons.convertString(attrs[col]); // joseph chen , 06/02/2008
						if (!"".equals(value)) {
							value = value.trim();
							switch (j) {
								case 0:
									id = Long.parseLong(value);
									bo.setId(id);
									break;
								case 1:
									bo.setMgmtName(value);
									break;
								case 2:
									bo.setDescription(value);
									break;
								case 3:
									bo.setDisableResetButton(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 4:
									bo.setDisableProxyArp(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 5:
									bo.setDisableSsid(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 6:
									bo.setDisableConsolePort(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 7:
									bo.setUserAuth(Short.parseShort(value));
									break;
								case 8:
									Long radiusId = AhRestoreNewMapTools.getMapRadiusServerAssign(AhRestoreCommons.convertLong(value));

									if (radiusId != null)
										bo.setRadiusServer(AhRestoreNewTools.CreateBoWithId(RadiusAssignment.class, radiusId));
									break;
								case 9:
									bo.setTempAlarmThreshold(Short.parseShort(value));
									break;
								case 10: // added by joseph chen, 05/27/2008
									bo.setEnableSmartPoe(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 11: // added by Fiona Feng, 07/03/2008
									bo.setMacAuthDelimiter(Short.parseShort(value));
									break;
								case 12: // added by Fiona Feng, 07/03/2008
									bo.setMacAuthStyle(Short.parseShort(value));
									break;
								case 13: // added by Fiona Feng, 07/03/2008
									bo.setMacAuthCase(Short.parseShort(value));
									break;
								case 14: // added by jospeh chen, 08/04/2008
									bo.setDisableCallAdmissionControl(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 15: // added by jospeh chen, 08/04/2008
									bo.setEnableForwardMaxMac(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 16: // added by jospeh chen, 08/04/2008
									bo.setEnableForwardMaxIp(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 17: // added by joseph chen, 08/04/2008
									bo.setForwardMaxMac(Short.parseShort(value));
									break;
								case 18: // added by joseph chen, 08/04/2008
									bo.setForwardMaxIp(Short.parseShort(value));
									break;
								case 19: // added by joseph chen, 08/29/2008
									bo.setAirtimePerSecond(Short.parseShort(value));
									break;
								case 20: // added by joseph chen, 08/29/2008
									bo.setRoamingGuaranteedAirtime(Byte.parseByte(value));
									break;
								case 21: // added by Fiona Feng, 09/11/2008
									bo.setLogDroppedPackets(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 22: // added by Fiona Feng, 09/11/2008
									bo.setLogFirstPackets(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 23: // added by Fiona Feng, 09/11/2008
									bo.setDropFragmentedIpPackets(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 24: // added by Fiona Feng, 09/11/2008
									bo.setDropNonMgtTraffic(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 25: // added by Fiona Feng, 02/10/2009
									bo.setSystemLedBrightness((short) AhRestoreCommons.convertInt(value));
									break;
								case 26: // added by Fiona Feng, 06/03/2009
									bo.setRadiusAuthType(AhRestoreCommons.convertInt(value));
									break;
								case 27:
									bo.setEnablePCIData(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 28:
									bo.setEnableTcpMss(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 29:
									bo.setTcpMssThreshold(AhRestoreCommons.convertInt(value));
									break;
								case 30:
									bo.setEnableIcmpRedirect(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 31:// added by Dan Li, 23/12/2011 start
									bo.setEnablePMTUD(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 32:
									bo.setMonitorMSS(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 33:
									bo.setThresholdForAllTCP(AhRestoreCommons.convertInt(value));
									break;
								case 34:// added by Dan Li, 23/12/2011 end
									bo.setThresholdThroughVPNTunnel(AhRestoreCommons.convertInt(value));
									break;
								case 35:
									HmDomain hmDom = AhRestoreNewMapTools.getHmDomain(Long.parseLong(value));
									if (null == hmDom)
										continue overlap;
									bo.setOwner(hmDom);
									break;
								case 36:
									bo.setPpskAutoSaveInt(AhRestoreCommons.convertInt(value));
									break;
								case 37:
									bo.setMulticastselect((short)AhRestoreCommons.convertInt(value));
									break;
								case 38:
									bo.setEnableOsdetection(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 39:
									bo.setOsDetectionMethod((short) AhRestoreCommons.convertInt(value));
									break;
								case 40:
									bo.setEnableSyncVlanId(AhRestoreCommons.convertStringToBoolean(value));
									break;
								case 41:
									bo.setFansUnderSpeedAlarmThreshold(Short.parseShort(value));
									break;
								case 42:
									bo.setEnableCaptureDataByCWP(AhRestoreCommons.convertStringToBoolean(value));
									break;
								default:
									break;
							}
						}
					} else {
						// before Dakar, the detection method should be HTTP user agent.
						if (j==38 ) {
							bo.setEnableOsdetection(true);
						}
						if (j == 39) {
							bo.setOsDetectionMethod(MgmtServiceOption.OS_DETECTION_METHOD_HTTP);
						}
						if(j == 31){
							bo.setEnablePMTUD(true);
						}
						if(j == 32){
							bo.setMonitorMSS(true);
						}
					}
				}
				
				if (bo.getMgmtName()==null || bo.getMgmtName().equals("")) {
					HmDomain dm = QueryUtil.findBoById(HmDomain.class, bo.getOwner().getId());
					if (dm!=null) {
						bo.setMgmtName(dm.getDomainName());
					}
				}
				
				if (null == bo.getId() || null == bo.getMgmtName() || null == bo.getOwner()) {
					continue;
				}
				if (allMulticast != null && allMulticast.size() > 0){
					bo.setMultipleVlan(allMulticast.get(bo.getId().toString()));
				}
				// set owner, joseph chen 05/04/2008
				//bo.setOwner(AhRestoreDBTools.HM_RESTORE_DOMAIN);

				listBo.add(bo);
			}
			if (!listBo.isEmpty()) {
				List<Long> lOldId = new ArrayList<Long>();

				for (MgmtServiceOption bo : listBo) {
					lOldId.add(bo.getId());
				}

				QueryUtil.restoreBulkCreateBos(listBo);

				for(int i=0; i<listBo.size(); i++)
				{
					AhRestoreNewMapTools.setMapOption(lOldId.get(i), listBo.get(i).getId());
				}
			}
		}

		return true;
	}
	//add by nxma
	private static Map<String, List<MulticastForwarding>> getAllMultipleVlan() throws AhRestoreException, AhRestoreColNotExistException {

		Map<String, List<MulticastForwarding>> multipleVlanInfo = new HashMap<String, List<MulticastForwarding>>();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		/**
		 * Check validation of multicast_forwarding.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("multicast_forwarding");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read multicast_forwarding.xml file.");
			return multipleVlanInfo;
		}

		int rowCount = xmlParser.getRowCount();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set mgmt_service_id
			 */
			colName = "mgmt_service_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"multicast_forwarding", colName);
			if (!isColPresent) {
				/**
				 * The mgmt_service_id column must be exist in the table of
				 * multicast_forwarding
				 */
				continue;
			}

			String mgmtServiceId = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
			if ("".equals(mgmtServiceId)) {
				continue;
			}

			/**
			 * Set ip
			 */
			colName = "ip";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"multicast_forwarding", colName);
			if (!isColPresent) {
				/**
				 * The gateway column must be exist in the table of
				 * hive_ap_multiple_vlan
				 */
				continue;
			}

			String ip = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
			if ("".equals(ip)) {
				continue;
			}
			/**
			 * Set netmask
			 */
			colName = "netmask";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"multicast_forwarding", colName);
			if (!isColPresent) {
				/**
				 * The gateway column must be exist in the table of
				 * hive_ap_multiple_vlan
				 */
				continue;
			}

			String netmask = AhRestoreCommons.convertString(xmlParser.getColVal(i, colName));
			if ("".equals(netmask)) {
				continue;
			}

			MulticastForwarding s_route = new MulticastForwarding();
			s_route.setIp(ip);
			s_route.setNetmask(netmask);

			if (multipleVlanInfo.get(mgmtServiceId) == null) {
				List<MulticastForwarding> d_routeList = new ArrayList<MulticastForwarding>();
				d_routeList.add(s_route);
				multipleVlanInfo.put(mgmtServiceId, d_routeList);
			} else {
				multipleVlanInfo.get(mgmtServiceId).add(s_route);
			}
		}
		return multipleVlanInfo;
	}

//	private  List<MgmtServiceDnsInfo> getListOfDnsInfo(Long id,List<String[]> list,String[] columns)
//	{
//		List<MgmtServiceDnsInfo> list_info=new ArrayList<MgmtServiceDnsInfo>();
//
//		MgmtServiceDnsInfo bo;
//		overlap:
//		for (String[] attrs : list) {
//			bo = new MgmtServiceDnsInfo();
//			for (int j = 0; j < bo.getFieldValues().length; j++) {
//				String field = bo.getFieldValues()[j];
//				int col_index = AhRestoreCommons.checkColExist(field, columns);
//				if (col_index >= 0) {
//					String value = AhRestoreCommons.convertString(attrs[col_index]); // joseph chen , 06/02/2008
//					if (!"".equals(value)) {
//						value = value.trim();
//						switch (j) {
//							case 0:
//								Long id_value = Long.parseLong(value);
//								if (!id_value.equals(id)) {
//									continue overlap;
//								}
//								break;
//							case 1:
//								Long newIp = AhRestoreNewMapTools.getMapIpAdddress(AhRestoreCommons.convertLong(value));
//
//								if (null != newIp)
//									bo.setIpAddress(AhRestoreNewTools.CreateBoWithId(IpAddress.class, newIp));
//								break;
//							case 2:
//								bo.setServerName(value);
//								break;
//							case 3:
//								bo.setDnsDescription(value);
//								break;
//							case 4:
//								bo.setSeverity(Short.parseShort(value));
//								break;
//							default:
//								break;
//						}
//					}
//				}
//			}
//
//			list_info.add(bo);
//		}
//		return list_info;
//	}

	/**
	 * Get all information from mgmt_service_dns_info table
	 *
	 * @return List<ActiveDirectoryOrLdapInfo> all ActiveDirectoryOrLdapInfo
	 * @throws AhRestoreColNotExistException -
	 *             if mgmt_service_dns_info.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing mgmt_service_dns_info.xml.
	 */
	private static Map<String, List<MgmtServiceDnsInfo>> getAllMgmtServiceDnsInfo() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of mgmt_service_dns_info.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("mgmt_service_dns_info");
		if (!restoreRet)
		{
			return null;
		}

		/**
		 * No one row data stored in mgmt_service_dns_info table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		Map<String, List<MgmtServiceDnsInfo>> dnsInfo = new HashMap<String, List<MgmtServiceDnsInfo>>();

		boolean isColPresent;
		String colName;
		MgmtServiceDnsInfo singleInfo;

		for (int i = 0; i < rowCount; i++)
		{
			singleInfo = new MgmtServiceDnsInfo();

			/**
			 * Set mgmt_service_dns_id
			 */
			colName = "mgmt_service_dns_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mgmt_service_dns_info", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if("".equals(id)) {
				continue;
			}

			/**
			 * Set mgmt_service_ip_address_id
			 */
			colName = "mgmt_service_ip_address_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mgmt_service_dns_info", colName);
			String ipId = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if(!"".equals(ipId)){
				Long newId = AhRestoreNewMapTools.getMapIpAdddress(AhRestoreCommons.convertLong(ipId));
				if(null != newId){
					singleInfo.setIpAddress(AhRestoreNewTools.CreateBoWithId(IpAddress.class, newId));
				} else {
					continue;
				}
			} else {
				continue;
			}

			/**
			 * Set dnsdescription
			 */
			colName = "dnsdescription";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mgmt_service_dns_info", colName);
			String description = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			singleInfo.setDnsDescription(description);

			/**
			 * Set servername
			 */
			colName = "servername";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mgmt_service_dns_info", colName);
			String servername = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			singleInfo.setServerName(servername);

			/**
			 * Set severity
			 */
			colName = "severity";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mgmt_service_dns_info", colName);
			String severity = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			singleInfo.setSeverity((short)AhRestoreCommons.convertInt(severity));

			/**
			 * Set position
			 */
			colName = "position";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"mgmt_service_dns_info", colName);
			String position = isColPresent ? xmlParser.getColVal(i, colName) : "0";
			singleInfo.setPosition(AhRestoreCommons.convertInt(position));

			
			if(singleInfo.getServerName().equals(HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP1_DEPRECATED)) {
			    singleInfo.setServerName(HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP1);
			} else if(singleInfo.getServerName().equals(HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP2_DEPRECATED)) {
			    singleInfo.setServerName(HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP2);
			}
			
			List<MgmtServiceDnsInfo> listInfo = dnsInfo.get(id);
			if (null == listInfo) {
				listInfo = new ArrayList<MgmtServiceDnsInfo>();
				listInfo.add(singleInfo);
				dnsInfo.put(id, listInfo);
			} else {
				listInfo.add(singleInfo);
			}
		}

		return dnsInfo.isEmpty() ? null : dnsInfo;
	}

	private  List<MgmtServiceSyslogInfo> getListOfSyslogInfo(Long id,
				String profileName, List<String[]> list,String[] columns,HmDomain hmDom)
	{
		List<MgmtServiceSyslogInfo> list_info=new ArrayList<MgmtServiceSyslogInfo>();

		MgmtServiceSyslogInfo bo;
		overlap:
		for (String[] attrs : list) {
			bo = new MgmtServiceSyslogInfo();

			for (int j = 0; j < bo.getFieldValues().length; j++) {
				String field = bo.getFieldValues()[j];
				int col_index = AhRestoreCommons.checkColExist(field, columns);
				if (col_index >= 0) {
					String value = AhRestoreCommons.convertString(attrs[col_index]); // joseph chen , 06/02/2008

					if (!"".equals(value)) {
						value = value.trim();
						switch (j) {
							case 0:
								Long id_value = Long.parseLong(value);
								if (!id_value.equals(id)) {
									continue overlap;
								}
								break;
							case 1:
								Long newIp = AhRestoreNewMapTools.getMapIpAdddress(AhRestoreCommons.convertLong(value));

								if (newIp != null)
									bo.setIpAddress(AhRestoreNewTools.CreateBoWithId(IpAddress.class, newIp));

								break;
							case 2:
								break;
							case 3:
								bo.setSyslogDescription(value);
								break;
							case 4:
								bo.setSeverity(Short.parseShort(value));
								break;
							case 5:
								if (bo.getIpAddress() == null) {
									bo.setIpAddress(CreateObjectAuto.createNewIP(value,
											IpAddress.TYPE_HOST_NAME, hmDom, "For Syslog Assignment: " + profileName));
								}

								break;
							default:
								break;
						}
					}
				}
			}
			list_info.add(bo);
		}
		return list_info;
	}
	private  List<MgmtServiceSnmpInfo> getListOfSnmpInfo(Long id, String profileName, List<String[]> list,String[] columns, boolean enableTrap, List<HmUpgradeLog> lstLogBo,HmDomain hmDom)
	{
		List<MgmtServiceSnmpInfo> list_info=new ArrayList<MgmtServiceSnmpInfo>();

		MgmtServiceSnmpInfo bo;
		overlap:
		for (String[] attrs : list) {
			bo = new MgmtServiceSnmpInfo();

			for (int j = 0; j < bo.getFieldValues().length; j++) {
				String field = bo.getFieldValues()[j];
				int col_index = AhRestoreCommons.checkColExist(field, columns);
				if (col_index >= 0) {
					String value = AhRestoreCommons.convertString(attrs[col_index]); // joseph chen , 06/02/2008

					if (!"".equals(value)) {
						value = value.trim();

						switch (j) {
							case 0:
								Long id_value = Long.parseLong(value);
								if (!id_value.equals(id)) {
									continue overlap;
								}
								break;
							case 1:
								bo.setSnmpOperation(Short.parseShort(value));
								break;
							case 2:
								if (value != null && !value.trim().equals("")) {
									HmUpgradeLog upLog = new HmUpgradeLog();
									IpAddress ip = null;
									if (MgmtServiceSnmpInfo.MGMTSNMP_OPERATION_GET != bo.getSnmpOperation() || AhRestoreCommons.checkColExist("encryPass", columns) > 0) {
										Long newIp = AhRestoreNewMapTools.getMapIpAdddress(AhRestoreCommons.convertLong(value));

										if (null != newIp)
											ip = QueryUtil.findBoById(IpAddress.class, newIp, new QueryBo() {

                                                @Override
                                                public Collection<HmBo> load(HmBo bo) {
                                                    if (bo instanceof IpAddress) {
                                                        IpAddress ipAddress = (IpAddress) bo;
                                                        if (null != ipAddress.getItems())
                                                            ipAddress.getItems().size();
                                                    }
                                                    return null;
                                                }
                                            });
									} else {
										ip = RestoreConfigNetwork.getNewIpNetworkObj(value, upLog);
									}

									if (NmsUtil.isLocalAddress(ip)) {
										/*
										 * HiveManager cannot be SNMP trap server from release 3.4
										 */
										HmUpgradeLog upgradeLog = new HmUpgradeLog();
										upgradeLog.setOwner(hmDom);
										upgradeLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(), hmDom.getTimeZoneString()));
										upgradeLog.setAnnotation("Click to add an annotation");
										upgradeLog.setFormerContent("System IP existed in the SNMP trap server list of SNMP Assignment \"" + profileName + "\"");
										upgradeLog.setPostContent("Remove System IP from the SNMP trap server list.");
										upgradeLog.setRecommendAction("Enable trap over CAPWAP in this SNMP Assignment in order to make sure the system can receive trap.");
										lstLogBo.add(upgradeLog);
										continue overlap;
									} else {
										bo.setIpAddress(ip);
									}
									if (null != upLog.getFormerContent() && upLog.getFormerContent().length() > 0) {
										upLog.setFormerContent("The SNMP Server in SNMP Assignment \"" + profileName + "\" " + upLog.getFormerContent());
										upLog.setPostContent(upLog.getPostContent() + " the SNMP Server in SNMP Assignment \"" + profileName + "\".");
										upLog.setRecommendAction("No action is required.");
										upLog.setOwner(hmDom);
										upLog.setLogTime(new HmTimeStamp(System.currentTimeMillis(), hmDom.getTimeZoneString()));
										upLog.setAnnotation("Click to add an annotation");
										lstLogBo.add(upLog);
									}
								}
								break;
							case 3:
								break;
							case 4:
								if (MgmtServiceSnmpInfo.oldDefaultCommunity.equals(value)) {
									value = MgmtServiceSnmpInfo.newDefaultCommunity;
								}
								bo.setCommunity(value);
								break;
							case 5:
								bo.setCommunity(value);
								break;
							case 6:
								bo.setSnmpVersion(Short.parseShort(value));
								break;
							case 7:
								if (bo.getIpAddress() == null) {
									bo.setIpAddress(CreateObjectAuto.createNewIP(value,
											IpAddress.TYPE_HOST_NAME, hmDom, "For SNMP Assignment: " + profileName));
								}
								break;
							case 8:
								bo.setUserName(value);
								break;
							case 9:
								bo.setAuthPassMethod(Short.parseShort(value));
								break;
							case 10:
								bo.setAuthPass(value);
								break;
							case 11:
								bo.setEncryPassMethod(Short.parseShort(value));
								break;
							case 12:
								bo.setEncryPass(value);
								break;
							default:
								break;
						}
					}
				}
			}
			list_info.add(bo);
		}
		return list_info;
	}
	private  List<MgmtServiceTimeInfo> getListOfTimeInfo(Long id, String profileName,List<String[]> list,String[] columns, HmDomain hmDom)
	{
		List<MgmtServiceTimeInfo> list_info=new ArrayList<MgmtServiceTimeInfo>();

		MgmtServiceTimeInfo bo;
		overlap:
		for (String[] attrs : list) {
			bo = new MgmtServiceTimeInfo();

			for (int j = 0; j < bo.getFieldValues().length; j++) {
				String field = bo.getFieldValues()[j];
				int col_index = AhRestoreCommons.checkColExist(field, columns);
				if (col_index >= 0) {
					String value = AhRestoreCommons.convertString(attrs[col_index]); // joseph chen , 06/02/2008

					if (!"".equals(value)) {
						value = value.trim();

						switch (j) {
							case 0:
								Long id_value = Long.parseLong(value);
								if (!id_value.equals(id)) {
									continue overlap;
								}
								break;
							case 1:
								Long newIp = AhRestoreNewMapTools.getMapIpAdddress(AhRestoreCommons.convertLong(value));

								if (null != newIp)
									bo.setIpAddress(AhRestoreNewTools.CreateBoWithId(IpAddress.class, newIp));
								break;
							case 2:
								break;
							case 3:
								bo.setTimeDescription(value);
								break;
							case 4:
								if (bo.getIpAddress() == null) {
									bo.setIpAddress(CreateObjectAuto.createNewIP(value,
											IpAddress.TYPE_HOST_NAME, hmDom, "For NTP Assignment: " + profileName));
								}
								break;
							default:
								break;
						}
					}
				}
			}
			list_info.add(bo);
		}
		return list_info;
	}

	/**
	 * Get and save all the information from mgmt_service_ip_track table
	 *
	 * @return boolean : restore successfully is true otherwise is false
	 * @throws AhRestoreColNotExistException -
	 *             if mgmt_service_ip_track.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing mgmt_service_ip_track.xml.
	 */
	private boolean saveMgmtServiceIpTrack() throws AhRestoreColNotExistException,AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		String tableName = "mgmt_service_ip_track";

		/**
		 * Check validation of mgmt_service_ip_track.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return false;
		}

		int rowCount = xmlParser.getRowCount();
		List<MgmtServiceIPTrack> items = new ArrayList<MgmtServiceIPTrack>();
		//Map<String, MgmtServiceIPTrack> defTrack = new HashMap<String, MgmtServiceIPTrack>();

		boolean isColPresent;
		String colName;
		MgmtServiceIPTrack singleItem;

		for (int i = 0; i < rowCount; i++)
		{
			singleItem = new MgmtServiceIPTrack();

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			singleItem.setId(AhRestoreCommons.convertLong(id));

			/**
			 * Set trackname
			 */
			colName = "trackname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String trackname = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			// there cannot be blank in this field
			if (trackname.contains(" ")) {
				trackname = trackname.replaceAll(" ", "");
			}
			if (BeParaModule.DEFAULT_IP_TRACKING_AP_NAME_NEW.equals(trackname) || BeParaModule.DEFAULT_IP_TRACKING_BR_NAME_NEW.equals(trackname)
				 || BeParaModule.DEFAULT_IP_TRACKING_VPN_GATEWAY_NAME_NEW.equals(trackname)) {
				singleItem = QueryUtil.findBoByAttribute(MgmtServiceIPTrack.class, "trackName", trackname);
				AhRestoreNewMapTools.setMapMgmtIpTracking(AhRestoreCommons.convertLong(id), singleItem.getId());
				continue;
			}
			singleItem.setTrackName(trackname);

			/**
			 * set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			HmDomain hmdom = AhRestoreNewMapTools.getHmDomain(ownerId);
			
			// reset track name when null
			if (hmdom!=null && (singleItem.getTrackName()==null || singleItem.getTrackName().equals(""))) {
				HmDomain dm = QueryUtil.findBoById(HmDomain.class, hmdom.getId());
				if (dm!=null) {
					singleItem.setTrackName(dm.getDomainName());
				}
			}
						
			if(null == hmdom || HmDomain.GLOBAL_DOMAIN.equals(hmdom.getDomainName()))
			{
				if (BeParaModule.DEFAULT_IP_TRACKING_AP_NAME.equals(trackname) || BeParaModule.DEFAULT_IP_TRACKING_BR_NAME.equals(trackname)
					 || BeParaModule.DEFAULT_IP_TRACKING_VPN_GATEWAY_NAME.equals(trackname)) {
					trackname = trackname.replace("Default_", "QS-IP-Track-");
					singleItem = QueryUtil.findBoByAttribute(MgmtServiceIPTrack.class, "trackName", trackname);
					if (null != singleItem) {
						AhRestoreNewMapTools.setMapMgmtIpTracking(AhRestoreCommons.convertLong(id), singleItem.getId());
					}
				} else if (trackname.equals("QuickStart-IP-Track-AP")
						|| trackname.equals("QuickStart-IP-Track-Router")
						|| trackname.equals("QuickStart-IP-Track-VPN_Gateway")) {
					trackname = trackname.replace("QuickStart", "QS");
					singleItem = QueryUtil.findBoByAttribute(MgmtServiceIPTrack.class, "trackName", trackname);
					if (null != singleItem)
						AhRestoreNewMapTools.setMapMgmtIpTracking(AhRestoreCommons.convertLong(id), singleItem.getId());
				} else if (trackname.equals("Quick-Start-IP-Track-AP")
						|| trackname.equals("Quick-Start-IP-Track-Router")
						|| trackname.equals("Quick-Start-IP-Track-VPN_Gateway")) {
					trackname = trackname.replace("Quick-Start", "QS");
					singleItem = QueryUtil.findBoByAttribute(MgmtServiceIPTrack.class, "trackName", trackname);
					if (null != singleItem) {
						AhRestoreNewMapTools.setMapMgmtIpTracking(AhRestoreCommons.convertLong(id), singleItem.getId());
					}
				}

			   continue;
			}
			singleItem.setOwner(hmdom);

			/**
			 * Set ipaddresses
			 */
			colName = "ipaddresses";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String ipaddresses = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setIpAddresses(AhRestoreCommons.convertString(ipaddresses));

			/**
			 * Set usegateway
			 */
			colName = "usegateway";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String usegateway = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setUseGateway(AhRestoreCommons.convertStringToBoolean(usegateway));

			/**
			 * Set enabletrack
			 */
			colName = "enabletrack";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String enabletrack = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setEnableTrack(AhRestoreCommons.convertStringToBoolean(enabletrack));

			/**
			 * Set usedefaultpara
			 */
//			colName = "usedefaultpara";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				tableName, colName);
//			String usedefaultpara = isColPresent ? xmlParser.getColVal(i, colName) : "";
//			singleItem.setUseDefaultPara(AhRestoreCommons.convertStringToBoolean(usedefaultpara));

			/**
			 * Set interval
			 */
			colName = "grouptype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			int type = isColPresent ? Integer.parseInt(xmlParser.getColVal(i, colName)):0;
			if(RestoreHiveAp.restore_from_51r6_before || 
					(!RestoreHiveAp.restore_from_60r1_before && RestoreHiveAp.restore_from_fuji_before)){
				if(type == 0){
					colName = "timeout";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
							tableName, colName);
					String timeout = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(MgmtServiceIPTrack.DEFAULT_VALUE_INTERVAL_FOR_TRACKIP);
					singleItem.setInterval((short) AhRestoreCommons.convertInt(timeout));
				}else{
					colName = "interval";
					isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						tableName, colName);
					String interval = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(MgmtServiceIPTrack.DEFAULT_VALUE_INTERVAL_FOR_TRACKIP);
					singleItem.setInterval((short) AhRestoreCommons.convertInt(interval));
				}
				
			}else{
				colName = "interval";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
				String interval = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(MgmtServiceIPTrack.DEFAULT_VALUE_INTERVAL_FOR_TRACKIP);
				singleItem.setInterval((short) AhRestoreCommons.convertInt(interval));
			}

//			/**
//			 * Set timeout
//			 */
//			colName = "timeout";
//			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
//				tableName, colName);
//			String timeout = isColPresent ? xmlParser.getColVal(i, colName) : "2";
//			singleItem.setTimeout((short) AhRestoreCommons.convertInt(timeout));

			/**
			 * Set retrytime
			 */
			colName = "retrytime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String retrytime = isColPresent ? xmlParser.getColVal(i, colName) : "2";
			singleItem.setRetryTime((short) AhRestoreCommons.convertInt(retrytime));

			/**
			 * Set enableaccess
			 */
			colName = "enableaccess";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String enableaccess = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setEnableAccess(AhRestoreCommons.convertStringToBoolean(enableaccess));

			/**
			 * Set disableradio
			 */
			colName = "disableradio";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String disableradio = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setDisableRadio(AhRestoreCommons.convertStringToBoolean(disableradio));

			/**
			 * Set startfailover
			 */
			colName = "startfailover";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String startfailover = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setStartFailover(AhRestoreCommons.convertStringToBoolean(startfailover));

			/**
			 * Set tracklogic
			 */
			colName = "tracklogic";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			short logic = isColPresent ? (short)AhRestoreCommons.convertInt(xmlParser.getColVal(i, colName))
				: MgmtServiceIPTrack.IP_TRACK_LOGIC_AND;
			singleItem.setTrackLogic(logic);

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				tableName, colName);
			String comment = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleItem.setDescription(AhRestoreCommons.convertString(comment));

			/**
			 * Set grouptype
			 */
			colName = "grouptype";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			int groupType = isColPresent ? Integer.parseInt(xmlParser.getColVal(i, colName)):0;
			singleItem.setGroupType(groupType);
			
			items.add(singleItem);
		}

		try
		{
			// insert all the data to database
			if (!items.isEmpty()) {
				List<Long> lOldId = new ArrayList<Long>();

				for (MgmtServiceIPTrack item : items) {
					lOldId.add(item.getId());
				}

				QueryUtil.restoreBulkCreateBos(items);

				for(int i=0; i<items.size(); i++)
				{
					AhRestoreNewMapTools.setMapMgmtIpTracking(lOldId.get(i), items.get(i).getId());
				}
			}
			// default value
//			if (!defTrack.isEmpty()) {
//				QueryUtil.bulkUpdateBos(defTrack.values());
//			}
		}
		catch(Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

}