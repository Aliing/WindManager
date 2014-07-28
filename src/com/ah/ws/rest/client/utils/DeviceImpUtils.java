package com.ah.ws.rest.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.DeviceInventory;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.FilterParamsFactory;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.hiveap.HiveApAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.ws.rest.models.DeviceOptModel;
import com.ah.ws.rest.models.ModelConstant;
import com.ah.ws.rest.models.SerialNumberList;
import com.ah.ws.rest.models.SerialNumbers;
import com.ah.ws.rest.models.dto.Device;
import com.ah.ws.rest.models.mo.DeviceOperation;

public class DeviceImpUtils implements DeviceUtils {
	private static final Tracer		log					= new Tracer(DeviceImpUtils.class.getSimpleName());
	private static DeviceUtils instances = null;

	public static DeviceUtils getInstance() {
		if (instances == null) {
			instances = new DeviceImpUtils();
			//instances = new com.ah.test.device.inventory.DeviceImplUtilTest();
		}
		return instances;
	}

	@Override
	public List<SerialNumberList> addSerialNumbersToRedirector(List<String> apMappingListOk, HmDomain domain, boolean addToRedirector) throws Exception {
		List<SerialNumberList> lst=null;
		if (addToRedirector && apMappingListOk!=null && !apMappingListOk.isEmpty()) {
			RedirectorResUtils ru = ClientUtils.getRedirectorResUtils();
			SerialNumbers sns = new SerialNumbers();
			if (domain!=null && domain.getVhmID()!=null) {
				sns.setVhmid(domain.getVhmID());
			} else {
				sns.setVhmid("");
			}
			sns.setSn(apMappingListOk);

			lst = ru.importSerialNumbers(sns);

			if (lst!=null) {
				List<String> addToHMList = new ArrayList<String>();
				for(SerialNumberList sl : lst){
					if (sl.getStatus()==ModelConstant.SN_SUCCESS) {
						if (sl.getSn()!=null) {
							addToHMList.addAll(sl.getSn());
						}
					}
					if (sl.getStatus()==ModelConstant.SN_EXIST) {
						if (sl.getSn()!=null) {
							addToHMList.addAll(sl.getSn());
						}
					}
				}
				if (!addToHMList.isEmpty()) {
					addSerialNumberToHm(addToHMList, domain);
				}
			}
		} else  if (!addToRedirector && apMappingListOk!=null && !apMappingListOk.isEmpty()) {
			List<DeviceInventory> lstSucc = addSerialNumberToHm(apMappingListOk,domain);
			for(DeviceInventory di: lstSucc) {
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.missionux.importserial.importserial.log",di.getSerialNumber()),domain);
			}
		}
		return lst;
	}

	@Override
	public List<DeviceInventory> addSerialNumberToHm(List<String> apMappingListOk, HmDomain domain) {
		List<DeviceInventory> lst = new ArrayList<DeviceInventory>();
		try {
			if (!NmsUtil.isHostedHMApplication()) {
				return lst;
			}
			if (apMappingListOk==null || apMappingListOk.isEmpty()) {
				return lst;
			}
			List<String> existSerials = new ArrayList<String>();
			List<?> existLst = QueryUtil.executeQuery("select serialNumber from " + DeviceInventory.class.getSimpleName(), null, new FilterParams("serialNumber", apMappingListOk), domain.getId());
			if (!existLst.isEmpty()) {
				for(Object ob: existLst) {
					existSerials.add(ob.toString());
				}
			}

			for(String str : apMappingListOk) {
				if (!existLst.isEmpty()) {
					if (existLst.contains(str)) {
						continue;
					}
				}
				DeviceInventory di = new DeviceInventory();
				di.setSerialNumber(str);
				di.setOwner(domain);
				lst.add(di);
			}

			if (!lst.isEmpty()) {
				QueryUtil.restoreBulkCreateBos(lst);
			}
		} catch (Exception e) {
			log.error(e);
		}

		return lst;
	}
	
	@Override
	public List<DeviceOptModel> addSerialNumberToHm(List<DeviceOptModel> deviceList) {
		if (deviceList==null || deviceList.isEmpty()) {
			return deviceList;
		}
		List<DeviceOptModel> retDevOptModelList = new ArrayList<DeviceOptModel>();
		List<DeviceOperation> successDeviceOption = null;
		List<DeviceOperation> failureDeviceOption = null;
		List<DeviceOperation> existDeviceOption = null;
		
		for(DeviceOptModel dom: deviceList) {

			DeviceOptModel retDeviceOptModel= new DeviceOptModel();
			retDeviceOptModel.setVhmId(dom.getVhmId());
			retDeviceOptModel.setDeviceOperation(new ArrayList<DeviceOperation>());
			
			successDeviceOption = new ArrayList<DeviceOperation>();
			failureDeviceOption = new ArrayList<DeviceOperation>();
			existDeviceOption = new ArrayList<DeviceOperation>();
			try {
				if (dom.getVhmId()==null || dom.getVhmId().isEmpty()) {
					for(DeviceOperation deviceOp: dom.getDeviceOperation()) {
						deviceOp.setOptMessage("VHMID is empty.");
						deviceOp.setOptStatus(DeviceOperation.OPT_STATUS_FAILED);
						failureDeviceOption.add(deviceOp);
					}
					retDeviceOptModel.getDeviceOperation().addAll(failureDeviceOption);
					
				} else {
					HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "vhmID", dom.getVhmId());
					if (domain==null) {
						for(DeviceOperation deviceOp: dom.getDeviceOperation()) {
							deviceOp.setOptMessage("cannot found the VHM by VHMID.");
							deviceOp.setOptStatus(DeviceOperation.OPT_STATUS_FAILED);
							failureDeviceOption.add(deviceOp);
						}
						retDeviceOptModel.getDeviceOperation().addAll(failureDeviceOption);
					} else {
						
						List<String> serialNumbers = new ArrayList<String>();
						for(DeviceOperation deviceOp: dom.getDeviceOperation()) {
							for(Device dv: deviceOp.getDevices()) {
								serialNumbers.add(dv.getSerialNumber());
							}
						}
						if (serialNumbers.isEmpty()) {
							continue;
						}
						List<String> existSerials = new ArrayList<String>();
						List<?> existLst = QueryUtil.executeQuery("select serialNumber from " + DeviceInventory.class.getSimpleName(), null, new FilterParams("serialNumber", serialNumbers), domain.getId());
						if (!existLst.isEmpty()) {
							for(Object ob: existLst) {
								existSerials.add(ob.toString());
							}
						}
						
						List<DeviceInventory> lst = new ArrayList<DeviceInventory>();
	
						for(String str : serialNumbers) {
							if (!existLst.isEmpty()) {
								if (existLst.contains(str)) {
									continue;
								}
							}
							DeviceInventory di = new DeviceInventory();
							di.setSerialNumber(str);
							di.setOwner(domain);
							lst.add(di);
						}
		
						if (!lst.isEmpty()) {
							QueryUtil.restoreBulkCreateBos(lst);
							for(DeviceInventory di: lst) {
								generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.missionux.importserial.importserial.log",di.getSerialNumber()),domain);
							}
						}
						
						// exist Serial number
						if (!existSerials.isEmpty()) {
							DeviceOperation existDevice = new DeviceOperation();
							existDevice.setOptStatus(DeviceOperation.OPT_STATUS_ADD_EXISTED);
							existDevice.setDevices(new ArrayList<Device>());
							for(String str : existSerials) {
								Device oneDevice = new Device();
								oneDevice.setSerialNumber(str);
								existDevice.getDevices().add(oneDevice);
								
							}
							existDeviceOption.add(existDevice);
							retDeviceOptModel.getDeviceOperation().addAll(existDeviceOption);
						}
						// success Serial number
						if (!lst.isEmpty()) {
							DeviceOperation successDevice = new DeviceOperation();
							successDevice.setOptStatus(DeviceOperation.OPT_STATUS_SUCCESS);
							successDevice.setDevices(new ArrayList<Device>());
							for(DeviceInventory di : lst) {
								Device oneDevice = new Device();
								oneDevice.setSerialNumber(di.getSerialNumber());
								successDevice.getDevices().add(oneDevice);
							}
							successDeviceOption.add(successDevice);
							retDeviceOptModel.getDeviceOperation().addAll(successDeviceOption);
						}
					}
				}
			} catch (Exception e) {
				failureDeviceOption = new ArrayList<DeviceOperation>();
				for(DeviceOperation deviceOp: dom.getDeviceOperation()) {
					deviceOp.setOptMessage("error:" + e.getMessage());
					deviceOp.setOptStatus(DeviceOperation.OPT_STATUS_FAILED);
					failureDeviceOption.add(deviceOp);
				}
				retDeviceOptModel.setDeviceOperation(failureDeviceOption);
			}
			
			retDevOptModelList.add(retDeviceOptModel);
		}

		return retDevOptModelList;
	}

	/**
	 * @author fxr
	 * @param arg_Status
	 *            : HmAuditLog.STATUS_SUCCESS;HmAuditLog.STATUS_FAILURE
	 * @param arg_Comment
	 *            : the comment of this operation
	 */
	public void generateAuditLog(short arg_Status, String arg_Comment, HmDomain domain) {
		// passive node cannot operate database
		if (HAUtil.isSlave()) {
			return;
		}
		HmAuditLog auditLog = new HmAuditLog();
		auditLog.setStatus(arg_Status);
		String commentStr = checkValueLength(arg_Comment, 256);
		auditLog.setOpeationComment(commentStr);
		auditLog.setHostIP("127.0.0.1");
		try {

			auditLog.setUserOwner("Admin");
			auditLog.setOwner(domain);

			auditLog.setLogTimeStamp(System.currentTimeMillis());
			auditLog.setLogTimeZone(domain != null ? domain.getTimeZoneString()
					: TimeZone.getDefault().getID());

			BeLogTools.info(HmLogConst.M_GUIAUDIT, "[" + auditLog.getHostIP() + " "
					+ auditLog.getOwner() + "." + auditLog.getUserOwner() + "]" + " "
					+ arg_Comment + ":" + arg_Status);

			QueryUtil.createBo(auditLog);
		} catch (Exception e) {
			log.error(e);
		}
	}

	private String checkValueLength(String arg_Comment, int len) {
		if (arg_Comment != null && arg_Comment.length() > len) {
			arg_Comment = arg_Comment.substring(0, len);
		}
		return arg_Comment;
	}
	
	@Override
	public List<DeviceOptModel> syncSerialNumbersFromRedirector(List<DeviceOptModel> deviceList)  {
		if (deviceList==null || deviceList.isEmpty()) {
			return deviceList;
		}
		
		List<DeviceOptModel> retDevOptModelList = new ArrayList<DeviceOptModel>();
		List<DeviceOperation> successDeviceOption = null;
		List<DeviceOperation> failureDeviceOption = null;
		List<DeviceOperation> notExistDeviceOption = null;
		
		for(DeviceOptModel dom: deviceList) {
			DeviceOptModel retDeviceOptModel= new DeviceOptModel();
			retDeviceOptModel.setVhmId(dom.getVhmId());
			retDeviceOptModel.setDeviceOperation(new ArrayList<DeviceOperation>());
			
			successDeviceOption = new ArrayList<DeviceOperation>();
			failureDeviceOption = new ArrayList<DeviceOperation>();
			notExistDeviceOption = new ArrayList<DeviceOperation>();
			
			try {
				if (dom.getVhmId()==null || dom.getVhmId().isEmpty()) {
					for(DeviceOperation deviceOp: dom.getDeviceOperation()) {
						deviceOp.setOptMessage("VHMID is empty.");
						deviceOp.setOptStatus(DeviceOperation.OPT_STATUS_FAILED);
						failureDeviceOption.add(deviceOp);
					}
					retDeviceOptModel.getDeviceOperation().addAll(failureDeviceOption);
					
				} else {
					HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "vhmID", dom.getVhmId());
					if (domain==null) {
						for(DeviceOperation deviceOp: dom.getDeviceOperation()) {
							deviceOp.setOptMessage("cannot found the VHM by VHMID.");
							deviceOp.setOptStatus(DeviceOperation.OPT_STATUS_FAILED);
							failureDeviceOption.add(deviceOp);
						}
						retDeviceOptModel.getDeviceOperation().addAll(failureDeviceOption);
					} else {
						List<String> serialNumbers = new ArrayList<String>();
						for(DeviceOperation deviceOp: dom.getDeviceOperation()) {
							for(Device dv: deviceOp.getDevices()) {
								serialNumbers.add(dv.getSerialNumber());
							}
						}
						if (serialNumbers.isEmpty()) {
							continue;
						}
						List<String> existSerials = new ArrayList<String>();
						List<?> existLst = QueryUtil.executeQuery(DeviceInventory.class, null, new FilterParams("serialNumber", serialNumbers), domain.getId());
						if (!existLst.isEmpty()) {
							for(Object ob: existLst) {
								DeviceInventory div = (DeviceInventory)ob;
								existSerials.add(div.getSerialNumber());
							}
						}
						
						List<DeviceInventory> updateLst = new ArrayList<DeviceInventory>();
						List<DeviceInventory> insertLst = new ArrayList<DeviceInventory>();
						
						for(DeviceOperation deviceOp: dom.getDeviceOperation()) {
							for(Device dv: deviceOp.getDevices()) {
								if (!existLst.isEmpty() && existSerials.contains(dv.getSerialNumber())) {
									for (Object ob : existLst) {
										DeviceInventory div = (DeviceInventory)ob;
										if (div.getSerialNumber().equalsIgnoreCase(dv.getSerialNumber())) {
											div.setConnectStatus(getDeviceConnectionStatus(dv.getConnectionStatus()));
											updateLst.add(div);
										} 
									}
								} else {
									DeviceInventory di = new DeviceInventory();
									di.setSerialNumber(dv.getSerialNumber());
									di.setConnectStatus(getDeviceConnectionStatus(dv.getConnectionStatus()));
									di.setOwner(domain);
									insertLst.add(di);
								}
							}
						}
						
		
						if (!insertLst.isEmpty()) {
							QueryUtil.restoreBulkCreateBos(insertLst);
							for(DeviceInventory di: insertLst) {
								generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.missionux.importserial.importserial.log",di.getSerialNumber()),domain);
							}
						}
						if (!updateLst.isEmpty()) {
							QueryUtil.bulkUpdateBos(updateLst);
						}
						
						// exist Serial number
						if (!existSerials.isEmpty()) {
							DeviceOperation successDevice = new DeviceOperation();
							successDevice.setOptStatus(DeviceOperation.OPT_STATUS_SUCCESS);
							successDevice.setDevices(new ArrayList<Device>());
							for(String str : existSerials) {
								Device oneDevice = new Device();
								oneDevice.setSerialNumber(str);
								successDevice.getDevices().add(oneDevice);
								
							}
							successDeviceOption.add(successDevice);
							retDeviceOptModel.getDeviceOperation().addAll(successDeviceOption);
						}
						// success Serial number
						if (!insertLst.isEmpty()) {
							DeviceOperation notExistDevice = new DeviceOperation();
							notExistDevice.setOptStatus(DeviceOperation.OPT_STATUS_SYN_ADDED);
							notExistDevice.setDevices(new ArrayList<Device>());
							for(DeviceInventory di : insertLst) {
								Device oneDevice = new Device();
								oneDevice.setSerialNumber(di.getSerialNumber());
								notExistDevice.getDevices().add(oneDevice);
							}
							notExistDeviceOption.add(notExistDevice);
							retDeviceOptModel.getDeviceOperation().addAll(notExistDeviceOption);
						}
					}
				}
			} catch (Exception e) {
				failureDeviceOption = new ArrayList<DeviceOperation>();
				for(DeviceOperation deviceOp: dom.getDeviceOperation()) {
					deviceOp.setOptMessage("error:" + e.getMessage());
					deviceOp.setOptStatus(DeviceOperation.OPT_STATUS_FAILED);
					failureDeviceOption.add(deviceOp);
				}
				retDeviceOptModel.setDeviceOperation(failureDeviceOption);
			}
			
			retDevOptModelList.add(retDeviceOptModel);
		}
		
		return retDevOptModelList;
	}

	@Override
	public List<DeviceOptModel> removeSerialNumbersFromRedirector(List<DeviceOptModel> deviceList) {
		if (deviceList==null || deviceList.isEmpty()) {
			return deviceList;
		}
		List<DeviceOptModel> retDevOptModelList = new ArrayList<DeviceOptModel>();
		List<DeviceOperation> successDeviceOption = null;
		List<DeviceOperation> failureDeviceOption = null;
		List<DeviceOperation> notExistDeviceOption = null;
		
		for(DeviceOptModel dom: deviceList) {

			DeviceOptModel retDeviceOptModel= new DeviceOptModel();
			retDeviceOptModel.setVhmId(dom.getVhmId());
			retDeviceOptModel.setDeviceOperation(new ArrayList<DeviceOperation>());
			
			successDeviceOption = new ArrayList<DeviceOperation>();
			failureDeviceOption = new ArrayList<DeviceOperation>();
			notExistDeviceOption = new ArrayList<DeviceOperation>();
			
			try {
				if (dom.getVhmId()==null || dom.getVhmId().isEmpty()) {
					for(DeviceOperation deviceOp: dom.getDeviceOperation()) {
						deviceOp.setOptMessage("VHMID is empty.");
						deviceOp.setOptStatus(DeviceOperation.OPT_STATUS_FAILED);
						failureDeviceOption.add(deviceOp);
					}
					retDeviceOptModel.getDeviceOperation().addAll(failureDeviceOption);
					
				} else {
					HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "vhmID", dom.getVhmId());
					if (domain==null) {
						for(DeviceOperation deviceOp: dom.getDeviceOperation()) {
							deviceOp.setOptMessage("cannot found the VHM by VHMID.");
							deviceOp.setOptStatus(DeviceOperation.OPT_STATUS_FAILED);
							failureDeviceOption.add(deviceOp);
						}
						retDeviceOptModel.getDeviceOperation().addAll(failureDeviceOption);
					} else {
						Collection<Long> removeIds = new ArrayList<Long>();
						List<String> serialNumbers = new ArrayList<String>();
						for(DeviceOperation deviceOp: dom.getDeviceOperation()) {
							for(Device dv: deviceOp.getDevices()) {
								serialNumbers.add(dv.getSerialNumber());
							}
						}
						if (serialNumbers.isEmpty()) {
							continue;
						}
						
						List<HiveAp> removedAps = QueryUtil.executeQuery(HiveAp.class, null,
								new FilterParams("serialNumber", serialNumbers), domain.getId());
						for(HiveAp oneAp: removedAps){
							removeIds.add(oneAp.getId());
						}
						
						Collection<Long> successLst = BoMgmt.getMapMgmt().removeHiveApsReturnRemovedIds(removeIds, true, false,dom.getVhmId(), true);

						// add audit log
						for (HiveAp hiveAp : removedAps) {
							if (!successLst.contains(hiveAp.getId())) {
								continue;
							}
							generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("geneva_08.hm.missionux.removeserial.log",hiveAp.getSerialNumber()),domain);

						}

						DeviceUtils diu = DeviceImpUtils.getInstance();
						diu.removeSerialNumberFromHm(removedAps, successLst, false, domain);
						
						// remove serial numbers success
						for (HiveAp hiveAp : removedAps) {
							serialNumbers.remove(hiveAp.getSerialNumber());
						}
						if (!serialNumbers.isEmpty()) {
							if (!serialNumbers.isEmpty()) {
								QueryUtil.removeBos(DeviceInventory.class, new FilterParams("serialNumber", serialNumbers));
								for(String str: serialNumbers) {
									generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("geneva_08.hm.missionux.removeserial.log",str),domain);
								}
							}
						}
						
						// not exist Serial number
						if (!serialNumbers.isEmpty()) {
							DeviceOperation notExistDevice = new DeviceOperation();
							notExistDevice.setOptStatus(DeviceOperation.OPT_STATUS_DEL_NOTEXISTED);
							notExistDevice.setDevices(new ArrayList<Device>());
							for(String str : serialNumbers) {
								Device oneDevice = new Device();
								oneDevice.setSerialNumber(str);
								notExistDevice.getDevices().add(oneDevice);
								
							}
							notExistDeviceOption.add(notExistDevice);
							retDeviceOptModel.getDeviceOperation().addAll(notExistDeviceOption);
						}
						// success Serial number
						if (!successLst.isEmpty()) {
							DeviceOperation successDevice = new DeviceOperation();
							successDevice.setOptStatus(DeviceOperation.OPT_STATUS_SUCCESS);
							successDevice.setDevices(new ArrayList<Device>());
							for (HiveAp hiveAp : removedAps) {
								if (!successLst.contains(hiveAp.getId())) {
									continue;
								}
								Device oneDevice = new Device();
								oneDevice.setSerialNumber(hiveAp.getSerialNumber());
								successDevice.getDevices().add(oneDevice);
								successDeviceOption.add(successDevice);
								retDeviceOptModel.getDeviceOperation().addAll(successDeviceOption);
							}
						}
						
						if (successLst.size()!=removedAps.size()) {
							DeviceOperation failureDevice = new DeviceOperation();
							failureDevice.setOptStatus(DeviceOperation.OPT_STATUS_FAILED);
							failureDevice.setDevices(new ArrayList<Device>());
							for (HiveAp hiveAp : removedAps) {
								if (!successLst.contains(hiveAp.getId())) {
									Device oneDevice = new Device();
									oneDevice.setSerialNumber(hiveAp.getSerialNumber());
									failureDevice.getDevices().add(oneDevice);
									failureDevice.setOptMessage("Cannot remove devices, Please contact Aerohive Technical Support for assistance. ");
									failureDeviceOption.add(failureDevice);
									retDeviceOptModel.getDeviceOperation().addAll(failureDeviceOption);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				failureDeviceOption = new ArrayList<DeviceOperation>();
				for(DeviceOperation deviceOp: dom.getDeviceOperation()) {
					deviceOp.setOptMessage("error:" + e.getMessage());
					deviceOp.setOptStatus(DeviceOperation.OPT_STATUS_FAILED);
					failureDeviceOption.add(deviceOp);
				}
				retDeviceOptModel.setDeviceOperation(failureDeviceOption);
			}
			
			retDevOptModelList.add(retDeviceOptModel);
			
		}
		
		return retDevOptModelList;
	}

	@Override
	public List<String> removeSerialNumberFromHm(
			List<HiveAp> removeAps, Collection<Long> successRemovedLst, boolean generateLog,HmDomain domain) {
		List<String> removeSerials = new ArrayList<String>();
		try {
			if (!NmsUtil.isHostedHMApplication()) {
				return removeSerials;
			}
			if (null == removeAps || removeAps.isEmpty() || successRemovedLst==null || successRemovedLst.isEmpty()) {
				return removeSerials;
			}

			for (HiveAp hiveAp : removeAps) {
				if (!successRemovedLst.contains(hiveAp.getId())) {
					continue;
				}
				removeSerials.add(hiveAp.getSerialNumber());
			}

			if (!removeSerials.isEmpty()) {
				QueryUtil.removeBos(DeviceInventory.class, new FilterParams("serialNumber", removeSerials));

				if (generateLog) {
					for(String str: removeSerials) {
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("geneva_08.hm.missionux.removeserial.log",str),domain);
					}
				}
			}

		} catch (Exception e) {
			log.error(e);
		}

		return removeSerials;

	}

	
	private boolean isSyncForWholeInstance(HmDomain domain) {
		return domain == null
				|| domain.isHomeDomain();
	}
	private short getDeviceConnectionStatus(short status) {
		short result = DeviceInventory.STATUS_DISCONNECT_REDIRECTOR;
		
		if (status == Device.CONNECTION_STATUS_CONNECTED) {
			result = DeviceInventory.STATUS_CONNECT_REDIRECTOR;
		}
		
		return result;
	}
	private List<DeviceInventory> encapDeviceInventoryInfoReturnedByRedirector(List<DeviceOptModel> lists) {
		List<DeviceInventory> result = null;
		
		if (lists != null
				&& !lists.isEmpty()) {
			result = new ArrayList<>();
			for (DeviceOptModel deviceModel : lists) {
				if (StringUtils.isBlank(deviceModel.getVhmId())) {
					continue;
				}
				HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "vhmID", deviceModel.getVhmId());
				if (domain == null) {
					continue;
				}
				if (deviceModel.getDeviceOperation() != null
						&& !deviceModel.getDeviceOperation().isEmpty()) {
					for (DeviceOperation deviceOp : deviceModel.getDeviceOperation()) {
						if (deviceOp.getOptStatus() != DeviceOperation.OPT_STATUS_FAILED
								&& deviceOp.getDevices() != null
								&& !deviceOp.getDevices().isEmpty()) {
							for (Device device : deviceOp.getDevices()) {
								DeviceInventory di = new DeviceInventory();
								di.setSerialNumber(device.getSerialNumber());
								di.setConnectStatus(this.getDeviceConnectionStatus(device.getConnectionStatus()));
								di.setOwner(domain);
								result.add(di);
							}
						}
					}
				}
			}
		}
		
		return result;
	}
	
	@Override
	public boolean syncDeviceInventoriesWithRedirector(HmDomain domain) {
		if (!NmsUtil.isHostedHMApplication()) {
			return false;
		}
		List<String> vhmIds = new ArrayList<>();
		if (this.isSyncForWholeInstance(domain)) {
			List<?> vhmObjs = QueryUtil.executeQuery(
					"select distinct domainName, vhmID from " + HmDomain.class.getSimpleName(),
					null,
					null);
			if (vhmObjs != null
					&& !vhmObjs.isEmpty()) {
				for (Object obj : vhmObjs) {
					Object[] objs = (Object[])obj;
					String domainName = objs[0] == null ? null : objs[0].toString(),
							vhmId = objs[1] == null ? null : objs[1].toString();
					if (StringUtils.isBlank(domainName)
							|| StringUtils.isBlank(vhmId)
							|| HmDomain.HOME_DOMAIN.equals(domainName)
							|| HmDomain.GLOBAL_DOMAIN.equals(domainName)) {
						continue;
					}
					vhmIds.add(vhmId);
				}
			}
		} else {
			if (!StringUtils.isBlank(domain.getVhmID())) {
				vhmIds.add(domain.getVhmID());
			}
		}

		if (vhmIds.isEmpty()) {
			return false;
		}

		List<DeviceOptModel> lists = null;
		try {
			lists = ClientUtils.getRedirectorResUtils().syncDeviceInventories(vhmIds);
		} catch (Exception e) {
			if (this.isSyncForWholeInstance(domain)) {
				log.error("Failed to get device inventory info from Redirector for whole instance.", e);
			} else {
				log.error("Failed to get device inventory info from Redirector for domain: " + domain.getDomainName(), e);
			}
		}
		
		List<DeviceInventory> deviceInventories = this.encapDeviceInventoryInfoReturnedByRedirector(lists);

		return this.syncDeviceInventoriesWithRedirector(deviceInventories, domain);
	}

	
	private static final String DB_OP_ADD = "add";
	private static final String DB_OP_UPDATE = "update";
	private static final String DB_OP_REMOVE = "remove";
	/**
	 * deviceInventories must contain items, otherwise, do not call this method!!!!
	 * @param deviceInventories
	 * @param domain
	 * @return
	 */
	private Map<String, List<DeviceInventory>> getDifferentCasesOfDeviceInventories(List<DeviceInventory> deviceInventories, HmDomain domain) {
		Map<String, List<DeviceInventory>> result = new HashMap<>();
		
		List<DeviceInventory> diInDB = null;
		if (this.isSyncForWholeInstance(domain)) {
			diInDB = QueryUtil.executeQuery(DeviceInventory.class, null, null);
		} else {
			diInDB = QueryUtil.executeQuery(DeviceInventory.class, null, new FilterParams("owner.id", domain.getId()));
		}
		
		Map<String, DeviceInventory> argDIs = new HashMap<>();
		for (DeviceInventory di : deviceInventories) {
			argDIs.put(di.getSerialNumber(), di);
		}
		
		Map<String, DeviceInventory> existDIs = new HashMap<>();
		if (diInDB != null
				&& !diInDB.isEmpty()) {
			for (DeviceInventory di : diInDB) {
				existDIs.put(di.getSerialNumber(), di);
			}
			
			List<DeviceInventory> newDIs = new ArrayList<>();
			List<DeviceInventory> updateDIs = new ArrayList<>();
			List<DeviceInventory> removeDIs = new ArrayList<>();
			for (String key : argDIs.keySet()) {
				if (!existDIs.containsKey(key)) {
					newDIs.add(argDIs.get(key));
				} else {
					DeviceInventory di1 = existDIs.get(key);
					DeviceInventory di2 = argDIs.get(key);
					di1.setSerialNumber(di2.getSerialNumber());
					di1.setConnectStatus(di2.getConnectStatus());
					di1.setOwner(di2.getOwner());
					updateDIs.add(di1);
				}
			}
			
			for (String key : existDIs.keySet()) {
				if (!argDIs.containsKey(key)) {
					removeDIs.add(existDIs.get(key));
				}
			}
			
			result.put(DB_OP_ADD, newDIs);
			result.put(DB_OP_UPDATE, updateDIs);
			result.put(DB_OP_REMOVE, removeDIs);
		} else {
			result.put(DB_OP_ADD, deviceInventories);
		}
		
		return result;
	}
	@Override
	public boolean syncDeviceInventoriesWithRedirector(List<DeviceInventory> deviceInventories, HmDomain domain) {
		if (deviceInventories != null) {
			try {
				if (deviceInventories.isEmpty()) {
					if (this.isSyncForWholeInstance(domain)) {
						QueryUtil.bulkRemoveBos(DeviceInventory.class, null);
					} else {
						QueryUtil.bulkRemoveBos(DeviceInventory.class, null, domain.getId());
					}
				} else {
					Map<String, List<DeviceInventory>> diMap = this.getDifferentCasesOfDeviceInventories(deviceInventories, domain);
					if (diMap.containsKey(DB_OP_REMOVE)
							&& !diMap.get(DB_OP_REMOVE).isEmpty()) {
						Set<String> serials = new HashSet<>();
						for (DeviceInventory di : diMap.get(DB_OP_REMOVE)) {
							serials.add(di.getSerialNumber());
						}
						QueryUtil.bulkRemoveBos(DeviceInventory.class, new FilterParams("serialNumber", serials));
					}
					
					if (diMap.containsKey(DB_OP_ADD)
							&& !diMap.get(DB_OP_ADD).isEmpty()) {
						QueryUtil.restoreBulkCreateBos(diMap.get(DB_OP_ADD));
					}
					
					if (diMap.containsKey(DB_OP_UPDATE)
							&& !diMap.get(DB_OP_UPDATE).isEmpty()) {
						QueryUtil.bulkUpdateBos(diMap.get(DB_OP_UPDATE));
					}
				}
				
			} catch (Exception e) {
				if (this.isSyncForWholeInstance(domain)) {
					log.error("Failed to update device inventory for whole instance.", e);
				} else {
					log.error("Failed to update device inventory for domain: " + domain.getDomainName(), e);
				}
				return false;
			}
		}

		return true;
	}

	@Override
	public String getDeviceInventoryCSVString(
			List<DeviceInventory> deviceInventories, byte type, boolean easyMode) {
		if (type == DeviceUtils.EXPORT_CSV_TYPE_CONFIGURATION) {
			return this.getDeviceInventoryCSVStringWithConfiguration(deviceInventories, easyMode);
		}
		return this.getDeviceInventoryCSVStringOnlySerialNumber(deviceInventories,easyMode);
	}
	private String getDeviceInventoryCSVStringOnlySerialNumber(
			List<DeviceInventory> deviceInventories, boolean easyMode) {
		StringBuilder sb = new StringBuilder();
		boolean blnFirstEl = true;
		if (deviceInventories != null
				&& !deviceInventories.isEmpty()) {
			for (DeviceInventory di : deviceInventories) {
				if (blnFirstEl) {
					blnFirstEl = false;
					sb.append(this.getCSVFieldString(di.getSerialNumber()));
				} else {
					sb.append("\r\n").append(this.getCSVFieldString(di.getSerialNumber()));
				}
			}
		}
		return sb.toString();
	}
	private String getDeviceInventoryCSVStringWithConfiguration(
			List<DeviceInventory> deviceInventories, boolean easyMode) {
		StringBuilder sb = new StringBuilder();
		if (deviceInventories != null
				&& !deviceInventories.isEmpty()) {
			
			Set<String> sns = new HashSet<>();
			for (DeviceInventory di : deviceInventories) {
				if (StringUtils.isNotBlank(di.getSerialNumber())
						&& di.getHiveAp() == null) {
					sns.add(di.getSerialNumber());
				}
			}
			if (!sns.isEmpty()) {
				List<HiveAp> devices = QueryUtil.executeQuery(HiveAp.class, 
						null, 
						FilterParamsFactory.getInstance().fieldIsIn("serialNumber", sns),
						null,
						new HiveApAction());
				if (devices != null
						&& !devices.isEmpty()) {
					Map<String, HiveAp> serailAps = new HashMap<>();
					for (HiveAp device : devices) {
						serailAps.put(device.getSerialNumber(), device);
					}
					for (DeviceInventory di : deviceInventories) {
						if (serailAps.containsKey(di.getSerialNumber())) {
							di.setHiveAp(serailAps.get(di.getSerialNumber()));
						}
					}
				}
			}
			
			sb.append("//Serial Number");
			sb.append(",Node ID");
			sb.append(",Host Name");
			sb.append(",Device Model");
			sb.append(",Device Function");
			if (!easyMode) {
				sb.append(",Network Policy");
			}
			sb.append(",Location");
			sb.append(",Static IP Address");
			sb.append(",Netmask");
			sb.append(",Default Gateway");
			sb.append(",Topology Map");
			sb.append(",VHM Name");
			
			for (DeviceInventory di : deviceInventories) {
				if (di.getSerialNumber()==null || di.getSerialNumber().isEmpty()) {
					sb.append("\r\n").append(this.getCSVFieldString(di.getHiveAp().getSerialNumber()));
				} else {
					sb.append("\r\n").append(this.getCSVFieldString(di.getSerialNumber()));
				}
				
				if (di.getHiveAp() != null) {
					sb.append("," + this.getCSVFieldString(di.getHiveAp().getMacAddress()));
					sb.append("," + this.getCSVFieldString(di.getHiveAp().getHostName()));
					sb.append("," + this.getCSVFieldString(di.getHiveAp().getDeviceModelName()));
					sb.append("," + this.getCSVFieldString(di.getHiveAp().getDeviceCategory()));
					if (!easyMode) {
						if (di.getHiveAp().getDeviceType()==HiveAp.Device_TYPE_VPN_GATEWAY) {
							sb.append("," + this.getCSVFieldString("N/A"));
						} else {
							sb.append("," + this.getCSVFieldString(di.getHiveAp().getConfigTemplateName()));
						}
					}
					sb.append("," + this.getCSVFieldString(di.getHiveAp().getLocation()));
					if (di.getHiveAp().isDhcp()) {
						sb.append(",");
						sb.append(",");
						sb.append(",");
					} else {
						sb.append("," + this.getCSVFieldString(di.getHiveAp().getCfgIpAddress()));
						sb.append("," + this.getCSVFieldString(di.getHiveAp().getCfgNetmask()));
						sb.append("," + this.getCSVFieldString(di.getHiveAp().getCfgGateway()));
					}
					sb.append("," + this.getCSVFieldString(di.getHiveAp().getMapContainerName()));
					sb.append("," + this.getCSVFieldString(di.getOwner().getDomainName()));
				} else {
					sb.append(",")
						.append(",")
						.append(",")
						.append(",");
						if (!easyMode) {
							sb.append(",");
						}
						sb.append(",")
						.append(",")
						.append(",")
						.append(",")
						.append(",");
						sb.append("," + this.getCSVFieldString(di.getOwner().getDomainName()));
				}
			}
		}
		return sb.toString();
	}
	private String getCSVFieldString(String field) {
		if (StringUtils.isNotBlank(field)) {
			return field;
		}
		return "";
	}

	public static void main(String[] args) {
		DeviceImpUtils deviceUtil = new DeviceImpUtils();
		List<DeviceInventory> deviceInventories = new ArrayList<>();
		DeviceInventory di = new DeviceInventory();
		di.setSerialNumber("123325435345");
		deviceInventories.add(di);

		di = new DeviceInventory();
		di.setSerialNumber("222222222222");
		deviceInventories.add(di);

		System.out.println("------------------------>>");
		System.out.println(deviceUtil.getDeviceInventoryCSVString(deviceInventories, DeviceUtils.EXPORT_CSV_TYPE_SERIALNUMBER, false));
		System.out.println("------------------------<<");
		
		List<DeviceOptModel> aa = new ArrayList<DeviceOptModel>();
		DeviceOptModel dom = new DeviceOptModel();
		dom.setDeviceOperation(new ArrayList<DeviceOperation>());
		dom.setVhmId("111111");
		DeviceOperation do1 = new DeviceOperation();
		do1.setDevices(new ArrayList<Device>());
		for(int i=0;  i<5;i++) {
			Device aa1 = new Device();
			aa1.setSerialNumber("1111111111000" + i);
			do1.getDevices().add(aa1);
		}
		
		dom.getDeviceOperation().add(do1);
		aa.add(dom);
		
		aa = deviceUtil.addSerialNumberToHm(aa);
		
		for(DeviceOptModel d1: aa) {
			System.out.println("VHMID: " + d1.getVhmId());
		}
		
	}

}
