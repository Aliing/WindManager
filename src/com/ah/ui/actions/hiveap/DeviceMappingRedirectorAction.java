package com.ah.ui.actions.hiveap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.Ostermiller.util.CSVParser;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.DeviceInventory;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.wlan.RadioProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.config.ImportCsvFileAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.devices.impl.Device;
import com.ah.ws.rest.client.utils.DeviceImpUtils;
import com.ah.ws.rest.client.utils.DeviceUtils;
import com.ah.ws.rest.models.ModelConstant;
import com.ah.ws.rest.models.SerialNumberList;


public class DeviceMappingRedirectorAction extends BaseAction implements QueryBo {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(DeviceMappingRedirectorAction.class
			.getSimpleName());

	private static final int IMPORT_FILE_SUCCESS=2;
	private static final int IMPORT_FILE_FAILURE=-1;
	private static final int IMPORT_FILE_NORECORD=1;
	
	
	@Override
	public String execute() throws Exception {
		if ("add".equals(operation)) {
			jsonObject = new JSONObject();
			if (!isHMOnline()) {
				jsonObject.put("t", false);
				jsonObject.put("m", "Don't support importing serial numbers when HM is not HMOL.");
				return "json";
			}
			try {
				
				StringBuilder error_sb = new StringBuilder();
				StringBuilder success_sb = new StringBuilder();
				StringBuilder total_sb = new StringBuilder();
				List<String> apMappingList = processAPMappingInfo();
				List<String> apMappingListOk = new ArrayList<String>();
				int importCount=0;
				total_sb.append("<br>====================Begin====================<br>");
				for (String ss: apMappingList){
					String conStr = filter(ss);
					if (ss.equals(conStr)) {
						if (ss.length() == 14) {// enable to reassign the HiveAPs
							apMappingListOk.add(ss);
						} else {
							error_sb.append("Unable to add serial number \"").append(ss).append("\": invalid serial number.<br>");
						}
					} else {
						error_sb.append("Unable to add serial number \"").append(ss).append("\": invalid serial number.<br>");
					}
				}

				if (!apMappingListOk.isEmpty()) {
					DeviceUtils diu = DeviceImpUtils.getInstance();
					List<SerialNumberList> lst =diu.addSerialNumbersToRedirector(apMappingListOk, getDomain(), true);
					if (lst!=null) {
						for(SerialNumberList sl : lst){
							if (sl.getStatus()==ModelConstant.SN_SUCCESS) {
								if (sl.getSn()!=null) {
									for(String ss: sl.getSn()){
										importCount++;
										success_sb.append("Serial number \"").append(ss).append("\" has been added successfully.<br>");
										generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.missionux.importserial.importserial.log",ss));
									}
								}
							} else if (sl.getStatus()==ModelConstant.SN_FAILURE) {
								if (sl.getSn()!=null) {
									for(String ss: sl.getSn()){
										error_sb.append("Unable to add serial number \"").append(ss).append("\": ").append(sl.getDescription()==null?"null": sl.getDescription()).append("<br>");
									}
								}
							} else if (sl.getStatus()==ModelConstant.SN_EXIST) {
								if (sl.getSn()!=null) {
									for(String ss: sl.getSn()){
										error_sb.append("Unable to add serial number \"").append(ss).append("\": it already exists in the system.<br>");
									}
								}
							}
						}
					}
				}
				
				if (importCount>0) {
					total_sb.append("<br>").append("Number of devices added successfully: ").append(importCount).append("<br>");
					//total_sb.append(success_sb.toString());
				} 
				
				if (!apMappingList.isEmpty() && apMappingList.size()-importCount>0) {
					total_sb.append("<br>").append("Number of devices failed to add: ").append(apMappingList.size()-importCount).append("<br><br>");
					total_sb.append(error_sb.toString());
				}
				
				if (importCount>0) {
					if (apMappingList.size()-importCount ==0) {
						jsonObject.put("rt", 1);
						total_sb.append("<br>").append(addSuccessMessage(true, false).toString()).append("<br>");
					} else {
						jsonObject.put("rt", 2);
						total_sb.append("<br>").append(addSuccessMessage(false, false).toString()).append("<br>");
					}
//					error_sb.append("<br>").append(addSuccessMessageToRedirector(false).toString()).append("<br>");
				}
				total_sb.append("<br>====================End====================<br><br>");
				if (importCount >0) {
					jsonObject.put("t", true);
				} else {
					jsonObject.put("t", false);
				}
				jsonObject.put("m", total_sb.toString());
			} catch (Exception e) {
				log.error(e);
				jsonObject.put("t", false);
				jsonObject.put("m", MgrUtil.getUserMessage("error.import.serialnumber.user.display"));
			}
			return "json";
		} else if ("import".equals(operation)) {
			jsonObject = new JSONObject();
			int ret = saveFile();
			
			jsonObject.put("t", ret==IMPORT_FILE_SUCCESS? true: false);
			if(ret>0) {
				jsonObject.put("m", result.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
			}
			return "json";
		}

		return SUCCESS;

	}
	
	private StringBuilder addSuccessMessage(boolean displaySecMessage, boolean isimp) {
		StringBuilder ss = new StringBuilder();
		if (isimp) {
			ss.append("&lt;font color=\"#0093D1\"&gt;").append(MgrUtil.getUserMessage("hm.missionux.importserial.result.note"));
		} else {
			ss.append("<font color=\"#0093D1\">").append(MgrUtil.getUserMessage("hm.missionux.importserial.result.note"));
		}
		if (displaySecMessage) {
			if (isimp) {
				ss.append("&lt;br&gt;");
			} else {
				ss.append("<br>");
			}
			ss.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
				.append(MgrUtil.getUserMessage("hm.missionux.importserial.result.note1"));
		}
//		if (isimp) {
//			ss.append("&lt;br&gt;");
//		} else {
//			ss.append("<br>");
//		}
//		ss.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
//			.append(MgrUtil.getUserMessage("hm.missionux.importserial.result.note2"));
//		
		if (isimp) {
			ss.append("&lt;/font&gt;");
		} else {
			ss.append("</font>");
		}
//		if (isimp) {
//			ss.append("&lt;br&gt;");
//		} else {
//			ss.append("<br>");
//		}
//		ss.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
//			.append(MgrUtil.getUserMessage("hm.missionux.importserial.result.note3"));
		return ss;
	}
	
	private StringBuilder addSuccessMessageToRedirector(boolean isimp) {
		StringBuilder ss = new StringBuilder();
		String aa;
		if (isimp) {
			aa = "&lt;a href='javascript: void(0);' onclick='openRedirectorServerPage();'&gt;" + MgrUtil.getUserMessage("topology.menu.hiveAp.deviceInventory").toLowerCase() + "&lt;/a&gt;";
		} else {
			aa = "<a href='javascript: void(0);' onclick='openRedirectorServerPage();'>" + MgrUtil.getUserMessage("topology.menu.hiveAp.deviceInventory").toLowerCase() + "</a>";
		}
		
		ss.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
			.append(MgrUtil.getUserMessage("hm.missionux.importserial.result.redirectornote", aa));

		return ss;
	}

	private List<String> processAPMappingInfo() {
		List<String> apMappingList = new ArrayList<>();

		String[] array = apMappingInfo.split("\\n");
		for (String apInfo : array) {
			if (apInfo.isEmpty()) {
				continue;
			}

			if (apInfo.contains("\r")) {
				String[] _array = apInfo.split("\\r");
				for (String s : _array) {
					if (s.isEmpty()) {
						continue;
					}
					//apMappingList.add(filter(s));
					apMappingList.add(s);
				}
			} else {
				//apMappingList.add(filter(apInfo));
				apMappingList.add(apInfo);
			}
		}

		return apMappingList;
	}

	private String filter(String str) {
		List<Character> list = new ArrayList<>();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (('0' <= c && '9' >= c) || ('a' <= c && 'f' >= c) || ('A' <= c && 'F' >= c)) {
				list.add(c);
			}
		}

		char[] result = new char[list.size()];
		int index = 0;
		for (Character ch : list) {
			result[index] = ch;
			index++;
		}

		return String.copyValueOf(result);
	}

	private String uploadFileName;

	private String[][] allvalue;

	private File upload;

	private String uploadContentType;

	private String inputPath;

	private final StringBuffer result = new StringBuffer();

	private int saveFile() throws JSONException, IOException {
		int successNumber = 0;
		if (null != uploadFileName) {
			if (null != upload && !"".equals(uploadFileName)) {
				// the file format is csv
				if (!uploadFileName.endsWith(".csv")) {
					jsonObject.put("m", MgrUtil.getUserMessage("error.formatInvalid", "CSV File"));
					return IMPORT_FILE_FAILURE;
				}
				// the file cannot be empty
				if (upload.length() == 0) {
					jsonObject.put("m", MgrUtil.getUserMessage("error.licenseFailed.file.invalid"));
					return IMPORT_FILE_FAILURE;
				}
				CSVParser shredder=null;
				try {

					// get the data from file
					shredder = new CSVParser(
							new InputStreamReader(new FileInputStream(upload))
					);
					
					//shredder.setCommentStart("#*//");
					allvalue = shredder.getAllValues();
					if (null == allvalue || allvalue.length == 0) {
						jsonObject.put("m", MgrUtil.getUserMessage("hm.system.log.import.csv.no.valid.value.import"));
						return IMPORT_FILE_FAILURE;
					} else {
						result.append("&lt;br&gt;====================Begin====================&lt;br&gt;&lt;br&gt;");
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.begin.check.csvfile",uploadFileName)).append("&lt;br&gt;");
						
						Map<String,HiveAp> hiveApMapSet = new HashMap<String, HiveAp>();
						List<HiveAp> needCreateApList = new ArrayList<HiveAp>();
						List<String> apMappingList =  readAndCheckHiveApMappingRecord(hiveApMapSet);
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.stop.check.file",uploadFileName)).append("&lt;br&gt;");
						StringBuilder error_sb = new StringBuilder();
						StringBuilder success_sb = new StringBuilder();
						List<String> apMappingListOk = new ArrayList<String>();

						for (String ss: apMappingList){
							String conStr = filter(ss);
							if (ss.equals(conStr)) {
								if (ss.length() == 14) {// enable to reassign the HiveAPs
									apMappingListOk.add(ss);
								} else {
									error_sb.append("Unable to add serial number \"").append(ss).append("\": invalid serial number.").append("&lt;br&gt;");
								}
							} else {
								error_sb.append("Unable to add serial number \"").append(ss).append("\": invalid serial number.").append("&lt;br&gt;");
							}
						}
						
						if (!apMappingListOk.isEmpty()) {
							
							DeviceUtils diu = DeviceImpUtils.getInstance();
							List<SerialNumberList> lst =diu.addSerialNumbersToRedirector(apMappingListOk, getDomain(), true);
							if (lst!=null) {
								for(SerialNumberList sl : lst){
									if (sl.getStatus()==ModelConstant.SN_SUCCESS) {
										if (sl.getSn()!=null) {
											for(String ss: sl.getSn()){
												if (hiveApMapSet.get(ss)!=null) {
													needCreateApList.add(hiveApMapSet.get(ss));
												}
												success_sb.append("Serial number \"").append(ss).append("\" has been added successfully.&lt;br&gt;");
												generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.missionux.importserial.importserial.log",ss));
											}
											successNumber= sl.getSn().size();
										}
									} else if (sl.getStatus()==ModelConstant.SN_FAILURE) {
										if (sl.getSn()!=null) {
											for(String ss: sl.getSn()){
												error_sb.append("Unable to add serial number \"").append(ss).append("\": ").append(sl.getDescription()==null?"null": sl.getDescription()).append("&lt;br&gt;");
											}
										}
									} else if (sl.getStatus()==ModelConstant.SN_EXIST) {
										if (sl.getSn()!=null) {
											for(String ss: sl.getSn()){
												error_sb.append("Unable to add serial number \"").append(ss).append("\": it already exists in the system.&lt;br&gt;");
											}
										}
									}
								}
							}
				
							createHiveAp(needCreateApList);

							if (successNumber>0) {
								result.append("&lt;br&gt;").append("Number of devices added successfully: ").append(successNumber).append("&lt;br&gt;");
								//result.append(success_sb.toString());
							} 
							if (!apMappingList.isEmpty() && apMappingList.size()-successNumber>0) {
								result.append("&lt;br&gt;").append("Number of devices failed to add: ").append(apMappingList.size()-successNumber).append("&lt;br&gt;&lt;br&gt;");
								result.append(error_sb.toString());
							}
							
							if (successNumber>0) {
								if (apMappingList.size()-successNumber ==0) {
									jsonObject.put("rt", 1);
									result.append("&lt;br&gt;").append(addSuccessMessage(true, true).toString()).append("&lt;br&gt;");
								} else {
									jsonObject.put("rt", 2);
									result.append("&lt;br&gt;").append(addSuccessMessage(false, true).toString()).append("&lt;br&gt;");

								}
//								result.append("&lt;br&gt;").append(addSuccessMessageToRedirector(true).toString()).append("&lt;br&gt;");
							}
						} else {
							result.append("&lt;br&gt;").append(MgrUtil.getUserMessage("hm.system.log.import.csv.no.valid.value.import")).append("&lt;br&gt;");
							result.append(error_sb.toString());
							result.append("&lt;br&gt;=====================End=====================&lt;br&gt;&lt;br&gt;");
							return IMPORT_FILE_NORECORD;
						}
					}
					
					result.append("&lt;br&gt;=====================End=====================&lt;br&gt;&lt;br&gt;");

				} catch (Exception e) {
					log.error(e);
					jsonObject.put("m", MgrUtil.getUserMessage("error.import.serialnumber.user.display"));
					return IMPORT_FILE_FAILURE;
				} finally {
					if (shredder != null) {
						shredder.close();
					}
				}
			} else {
				jsonObject.put("m", MgrUtil.getUserMessage("error.fileNotExist"));
				return IMPORT_FILE_FAILURE;
			}
		} else {
			jsonObject.put("m", MgrUtil.getUserMessage("error.fileNotExist"));
			return IMPORT_FILE_FAILURE;
		}
		if (successNumber>0) {
			return IMPORT_FILE_SUCCESS;
		} else {
			return IMPORT_FILE_NORECORD;
		}
	}
	
	/**
	 * Check the HiveAP file data and save them in database.
	 * @return int : the total number of create successfully
	 */
	private int createHiveAp(List<HiveAp> listAps) {
		int successNumber =0;
		try {
			if (listAps==null || listAps.isEmpty()) {
				return successNumber;
			}
			/*
			 * Check the total number of HiveAPs
			 */
			ConfigTemplate defTemp = HmBeParaUtil.getDefaultTemplate();
			/* Default Radio Profile for A Mode */
			RadioProfile defRadioA = HmBeParaUtil.getDefaultRadioAProfile();
			/* Default Radio Profile for BG Mode */
			RadioProfile defRadioBG = HmBeParaUtil.getDefaultRadioBGProfile();
			/* Default Radio Profile for NG Mode */
			RadioProfile defRadioNG = HmBeParaUtil.getDefaultRadioNGProfile();
			/* Default Radio Profile for NA Mode */
			RadioProfile defRadioNA = HmBeParaUtil.getDefaultRadioNAProfile();

			for (HiveAp ap : listAps) {
				HmDomain domain = ap.getOwner();
				if (domain.getRunStatus() == HmDomain.DOMAIN_DEFAULT_STATUS) {
					//Assign default value
					if(null == ap.getConfigTemplate()){
						ap.setConfigTemplate(defTemp);
					}
					if(null==ap.getWifi0RadioProfile() || null==ap.getWifi0RadioProfile().getId()){
						RadioProfile wifi0RadioProfile = HiveAp.is11nHiveAP(ap.getHiveApModel()) ? defRadioNG
								: defRadioBG;
						ap.setWifi0RadioProfile(wifi0RadioProfile);
					}
					if(null==ap.getWifi1RadioProfile() || null==ap.getWifi1RadioProfile().getId()){
						RadioProfile wifi1RadioProfile = HiveAp.is11nHiveAP(ap.getHiveApModel()) ? defRadioNA
								: defRadioA;
						ap.setWifi1RadioProfile(wifi1RadioProfile);
					}
					BoMgmt.getMapMgmt().createHiveApWithPropagation(ap,
							ap.getMapContainer());
					successNumber++;
				} else {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.haveaps.total.num.check",new String[]{ap.getId().toString(),NmsUtil.getOEMCustomer().getNmsNameAbbreviation(),domain.getDomainName()})).append("&lt;br&gt;");
				}
			}

		} catch (Exception ex) {
			log.error("createHiveAp", "create hiveap when import.", ex);
		}
		return successNumber;
	}

	/**
	 * Read the serial number file and get the data.
	 */
	private List<String> readAndCheckHiveApMappingRecord(Map<String, HiveAp> hiveApMapSet) {
		List<String> serialNumSet = new ArrayList<String>();
		List<String> macAddressSet = new ArrayList<String>();
		List<String> hostNames = new ArrayList<String>();
		HiveAp singleAP;
		//long dateTime = System.currentTimeMillis();
		try {

			int intLine = 0;
			//lineValue:
			for (String[] value : allvalue) {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : value) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				if (lineBool) {
					result.append("Check line ").append(oldLine).append(" - ").append(intLine).append(
						" failed : it contains embedded line-breaks.")
						.append("&lt;br&gt;");
					continue;
				}

				if (!checkTheLineValue(value, intLine)) {
					continue;
				}
				
				HmDomain domain = getDomain();
				int maxLength = isFullMode() ? 12 : 11;
				
				if (value.length <1) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.node.id.value.contain.check",String.valueOf(1)))
						.append("&lt;br&gt;");
					continue;
				}
				// serial number
				if (value[0].length() != 14) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.serialNum.valid.check"))
						.append(value[0]).append("&lt;br&gt;");
					continue;
				}
				if (!isNumber(value[0])) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.serialNum.valid.check")).append(
							value[0]).append("&lt;br&gt;");
					continue;
				}
				
				// it cannot exist in this file
				if (serialNumSet.contains(value[0].toLowerCase())) {
					result.append("Check line ").append(intLine)
						.append(" failed : the serial number already exists in this file :: ")
						.append(value[0]).append("&lt;br&gt;");
					continue;
				}
				
				DeviceInventory di = QueryUtil.findBoByAttribute(DeviceInventory.class, "serialNumber", value[0], getDomain().getId());
				if (di!=null) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.serialNum.exist.check",value[0]))
					.append("&lt;br&gt;");
					continue;
				}
				//mac address
				if ((value.length>1 && value[1].length()==0) || value.length==1) {
					serialNumSet.add(value[0].toLowerCase());
					continue;
				}
				
				if (value[1].length() != 12) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.node.id.length.check"))
						.append(value[1]).append("&lt;br&gt;");
					continue;
				}
				if (!isHex(value[1])) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.node.id.valid.check")).append(
							value[1]).append("&lt;br&gt;");
					continue;
				}
				
				if (value.length !=maxLength) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.node.id.value.contain.check",
							new String[] {"1", String.valueOf(maxLength)}))
						.append("&lt;br&gt;");
					continue;
				} else {
					if (!domain.getDomainName().equals(value[maxLength-1])){
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("glasgow_05_hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
							.append(value[maxLength-1]).append("&lt;br&gt;");
						continue;
					}
				}
				
				// mac address of hiveap
				singleAP = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", value[1].toUpperCase());

				// create new hiveap
				if (null == singleAP) {
					singleAP = new HiveAp();
					singleAP.setManageStatus(HiveAp.STATUS_PRECONFIG);
					// for the error line number
					singleAP.setId(Long.valueOf(intLine));
					singleAP.setMacAddress(value[1].toUpperCase());
					singleAP.setSerialNumber(value[0]);
				} else {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.node.id.exist.v.check",value[1])).append("&lt;br&gt;");
					continue;
				}
				
				// default host name
				String hostName = "AH-" + value[1].substring(6).toLowerCase();
				if (value[2].length() > 32) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.host.name.length.check"))
						.append(value[2]).append("&lt;br&gt;");
					continue;
				} else if (value[2].length() > 0) {
					String strResult = checkTheSpecialCharacter(value[2], ImportCsvFileAction.SINGLE_STRING_LIMIT);
					if (!"".equals(strResult)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.host.name.contain.check"))
						.append(" '").append(strResult).append("' :: ").append(value[2]).append("&lt;br&gt;");
						continue;
					}
					hostName = value[2];
				}

				if (HiveApAction.isHiveApHostNameExist(domain.getId(),
						hostName, null)) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.host.name.db.check",hostName))
					.append("&lt;br&gt;");
					continue;
				}
				singleAP.setHostName(hostName);
				
				// ap mode
				if (value[3].length() > 0) {
					short apModel;
					boolean isMatched = false;
					if (value[3] != null) {
						try {
							Device hiveap = Device.valueOf(value[3].toUpperCase().replace('-', '_'));

							List<Device> deviceObjects = AhConstantUtil.getDeviceObjects(Device.ALL);
							if (deviceObjects != null && deviceObjects.contains(hiveap)) {
								apModel = AhConstantUtil.getModelByDevice(hiveap);
								Boolean isTrue = AhConstantUtil.isTrueAll(Device.SUPPORTED_IMPORT_CSV, apModel);
								if (isTrue == null ? false : isTrue) {
									singleAP.setHiveApModel(apModel);
									singleAP.setDeviceType(AhConstantUtil.getDeviceTypeByDevice(hiveap));
									isMatched = true;
								}
							}
						} catch (Exception e) {
							log.error("readAndCheckHiveAPNewInfo", "Reading/Checking AP information error.", e);
						}
					}

					if (!isMatched) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ap.model.check")).append(
								value[3]).append("&lt;br&gt;");
						continue;
					} 
				} else {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ap.model.check")).append(
							value[3]).append("&lt;br&gt;");
					continue;
				}
				
				// Device function
				if (!value[4].equalsIgnoreCase("AP") && !value[4].equalsIgnoreCase("Router")
						&& !value[4].equalsIgnoreCase("Switch")) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.ap.function.check"))
						.append(value[4]).append("&lt;br&gt;");
					continue;
				}
				if (isEasyMode() && !value[3].toUpperCase().startsWith("AP")) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ap.model.check"))
						.append(value[3]).append("&lt;br&gt;");
					continue;
				}
				if (isEasyMode() && !value[4].equalsIgnoreCase("AP")) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.ap.function.check"))
						.append(value[4]).append("&lt;br&gt;");
					continue;
				}
				if (value[4].equalsIgnoreCase("AP")) {
					singleAP.setDeviceType(HiveAp.Device_TYPE_HIVEAP);
				} else if (value[4].equalsIgnoreCase("Router")) {
					singleAP.setDeviceType(HiveAp.Device_TYPE_BRANCH_ROUTER);
				} else if (value[4].equalsIgnoreCase("L2 VPN Gateway")) {
					singleAP.setDeviceType(HiveAp.Device_TYPE_HIVEAP);
				} else if (value[4].equalsIgnoreCase("L3 VPN Gateway")) {
					singleAP.setDeviceType(HiveAp.Device_TYPE_VPN_GATEWAY);
				} else if (value[4].equalsIgnoreCase("Switch")) {
					singleAP.setDeviceType(HiveAp.Device_TYPE_SWITCH);
				}
				
				if (!AhConstantUtil.getHiveApModelSupportType(singleAP.getHiveApModel(), singleAP.getDeviceType())) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.ap.function.notmatch"))
						.append(value[4]).append("&lt;br&gt;");
					continue;
				}

				
				// wlan policy
				// full mode, wlan policy can be selected
				int k = 5;
				if (isFullMode()) {
					if (value[k].length() > 0) {
						ConfigTemplate config = QueryUtil
							.findBoByAttribute(ConfigTemplate.class, "configName",
									value[k], domain.getId());
						if (null == config) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.network.policy.check")).append(
									value[k]).append("&lt;br&gt;");
							continue;
						}
						singleAP.setConfigTemplate(config);
//					} else {
//						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
//						.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.network.policy.length.check")).append(
//								value[k]).append("&lt;br&gt;");
//						continue;
					} else {
						singleAP.setConfigTemplate(HmBeParaUtil.getDefaultTemplate());
					}
					k++;
				// express mode use the default wlan policy
				} else {
					singleAP.setConfigTemplate(HmBeParaUtil.getEasyModeDefaultTemplate(domain.getId()));
				}
				
				// set version information
				String softVer = NmsUtil.getHiveOSVersion(versionInfo);
				singleAP.setSoftVer(softVer);
				singleAP.setDisplayVer(getText("monitor.hiveAp.DisplayVer", 
						new String[]{singleAP.getSoftVerString()}));
				singleAP.setPriority(HiveAp.getDefaultBonjourPriority(singleAP.getHiveApModel()));
			
				if (!value[k].equals("")) {
					if (value[k].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.location.length.check"))
							.append(value[k]).append("&lt;br&gt;");
						continue;
					}
					String strResultLocation = checkTheSpecialCharacter(value[k], ImportCsvFileAction.LOCATION_STRING_LIMIT);
					if (!"".equals(strResultLocation)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.location.contain.check"))
						.append(" '").append(strResultLocation).append("' :: ").append(value[k]).append("&lt;br&gt;");
						continue;
					}
					singleAP.setLocation(value[k]);
				}

				k++;
				
				if (!value[k].equals("")) {
					if (ImportCsvFileAction.getIpAddressWrongFlag(value[k])) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.valid.check"))
							.append(value[k]).append("&lt;br&gt;");
						continue;
					}
					singleAP.setCfgIpAddress(value[k]);
					singleAP.setIpAddress(value[k]);

					if (!value[k+1].equals("")) {
						if (ImportCsvFileAction.getNetmaskWrongFlag(value[k+1])) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.subnet.mask.check"))
								.append(value[k+1]).append("&lt;br&gt;");
							continue;
						}
						singleAP.setCfgNetmask(value[k+1]);
						singleAP.setNetmask(value[k+1]);

						if (!value[k+2].equals("")) {
							if (ImportCsvFileAction.getIpAddressWrongFlag(value[k+2]
									)) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.gateway.address.check"))
									.append(value[k+2]).append("&lt;br&gt;");
								continue;
							}
							if (!HmBeOsUtil.isInSameSubnet(singleAP.getCfgIpAddress(),
									value[k+2], singleAP.getCfgNetmask())) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.gateway.subnet.mask.same.check"))
									.append("&lt;br&gt;");
								continue;
							}
							singleAP.setCfgGateway(value[k+2]);
							singleAP.setGateway(value[k+2]);
						}

					} else {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.subnet.mask.empty.check"))
							.append("&lt;br&gt;");
						continue;
					}
					singleAP.setDhcp(false);
				} else {
					if (value.length > k+1) {
						if (!value[k+1].equals("")) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.address.length.check"))
								.append("&lt;br&gt;");
							continue;
						}
						if (value.length > k+2) {
							if (!value[k+2].equals("")) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.address.subnet.mask.check"))
									.append("&lt;br&gt;");
								continue;
							}
						}
					}
				}

				k+=3;
				if (value.length > k) {
					if (!value[k].equals("")) {
						if (MapMgmt.ROOT_MAP_NAME.equals(value[k]) || MapMgmt.VHM_ROOT_MAP_NAME.equals(value[k])) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.topology.name.check"))
								.append(value[k]).append("&lt;br&gt;");
							continue;
						}

						MapContainerNode topology = QueryUtil
								.findBoByAttribute(MapContainerNode.class,
										"mapName", value[k], domain
												.getId());
						if (null == topology) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.topology.type.exist.check"))
								.append(value[k]).append("&lt;br&gt;");
							continue;
						}
						if (MapContainerNode.MAP_TYPE_BUILDING == topology.getMapType()) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.topology.type.check"))
								.append(value[k]).append("&lt;br&gt;");
							continue;
						}

						singleAP.setMapContainer(topology);

					}
				}

				singleAP.setOwner(domain);
				
				// Node ID cannot repeat in this file
				boolean boolMac = false;
				for (String nodeId : macAddressSet) {
					if (nodeId.equalsIgnoreCase(value[1])) {
						boolMac = true;
						break;
					}
				}
				if (boolMac) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.node.id.check",value[1]))
					.append("&lt;br&gt;");
					continue;
				}

				// Host Name cannot repeat in this file
				boolean boolName = false;
				for (String name : hostNames) {
					if (name.equals(hostName)) {
						boolName = true;
						break;
					}
				}
				if (boolName) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.host.name.check",hostName))
					.append("&lt;br&gt;");
					continue;
				}
				if (!boolMac && !boolName) {
					macAddressSet.add(value[1]);
					hostNames.add(hostName);
				}

				// it cannot exist in this file
				if (serialNumSet.contains(value[0].toLowerCase())) {
					result.append("Check line ").append(intLine)
						.append(" failed : the serial number already exists in this file :: ")
						.append(value[0]).append("&lt;br&gt;");
					continue;
				}
				serialNumSet.add(value[0].toLowerCase());
				
				hiveApMapSet.put(value[0],singleAP);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return serialNumSet;
	}

	/**
	 * Check the values in one line.
	 *
	 *@param arg_Value : line value
	 *@param arg_Line : the line number
	 *@return boolean : true : the value is valid; false : the value is invalid
	 */
	private boolean checkTheLineValue(String[] arg_Value, int arg_Line) {
		boolean boolResult = true;
		if (null == arg_Value || arg_Value.length == 0) {
			result.append("Check line ").append(arg_Line).append(
				" failed, there is no value in this record.")
				.append("&lt;br&gt;");
			boolResult = false;
		}
		if (arg_Value[0].startsWith("*") || arg_Value[0].startsWith("#")) {
			result.append("Ignored line ").append(arg_Line).append(", the record start with the comment char '").append(arg_Value[0].charAt(0)).append("'.")
				.append("&lt;br&gt;");
			boolResult = false;
		}
		if (arg_Value[0].startsWith("//")) {
			result.append("Ignored line ").append(arg_Line).append(
				", the record start with the comment char '//'.")
				.append("&lt;br&gt;");
			boolResult = false;
		}
		return boolResult;
	}

	public static boolean isNumber(String value) {
		if (value == null || value.equals(""))
			return false;
		for (int i = 0; i < value.length(); i++)
			if (!"0123456789".contains(value.substring(i, i + 1)))
				return false;
		return true;
	}
	
	private boolean isHex(String value) {
		if (value == null || value.equals(""))
			return false;
		for (int i = 0; i < value.length(); i++)
			if (!"0123456789ABCDEFabcdef".contains(value.substring(i, i + 1)))
				return false;
		return true;
	}
	
	/**
	 * The special characters cannot be input.
	 *
	 * @param str_Value : the checked character;
	 * @param str_Limit : the limit characters
	 * @return String : the result
	 */
	private String checkTheSpecialCharacter(String str_Value, String[] str_Limit) {
		if (null == str_Limit) {
			return "";
		}
		for (String signal : str_Limit) {
			if (str_Value.contains(signal)) {
				return signal;
			}
		}
		return "";
	}


	private String apMappingInfo="";

	@Override
	public Collection<HmBo> load(HmBo bo) {
		return null;
	}

	public String getApMappingInfo() {
		return apMappingInfo;
	}

	public void setApMappingInfo(String apMappingInfo) {
		this.apMappingInfo = apMappingInfo;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(inputPath);
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	public String[][] getAllvalue() {
		return allvalue;
	}

	public void setAllvalue(String[][] allvalue) {
		this.allvalue = allvalue;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}



}