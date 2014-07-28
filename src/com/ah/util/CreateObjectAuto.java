/**
 *@filename		CreateObjectAuto.java
 *@version
 *@author		Fiona
 *@createtime	2009-03-27 PM 03:16:49
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.Vlan;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.ui.actions.config.ImportCsvFileAction;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class CreateObjectAuto {

	public final static Object vlanLock=new Object();
	
	/**
	 * Create the new IP which does not exist in database.
	 * 
	 * @param value the ip or host name
	 * @param type ip or name
	 * @param arg_Domain domain
	 * @param arg_Des description
	 * @return IpAddress
	 */
	public static IpAddress createNewIP(String value, short type,
			HmDomain arg_Domain, String arg_Des) {
		return createNewIP(value, type, arg_Domain, arg_Des, null);
	}
	
	public static IpAddress createNewIP(String ip, short type,
			HmDomain arg_Domain, String arg_Des, String netmask) {
		IpAddress resultObj = null;
		if (null != ip && !"".equals(ip) && ip.length() <= 32) {
			
			String ipName = ip;
			
			// network and wildcard name contains netmask and wildcard value
			if (IpAddress.TYPE_IP_NETWORK == type || IpAddress.TYPE_IP_WILDCARD == type) {
				ipName = ip + "/" + netmask;
			} else if (IpAddress.TYPE_IP_RANGE == type) {
				ipName = ip + "-" + netmask;
			}
			
			if (ipName.length() > 32) {
				ipName = ipName.substring(0, 32);
			}
			resultObj = QueryUtil.findBoByAttribute(
					IpAddress.class, "addressName", ipName, arg_Domain.getId());
			
			if (null == resultObj) {
				resultObj = getNewIpObject(ipName, ip, netmask, type, arg_Domain, arg_Des);

				// create the new object
				try {
					QueryUtil.createBo(resultObj);
				} catch (Exception dbexc) {
				}
			}
		}
		return resultObj;
	}

	public static IpAddress getNewIpObject(String profileName, String ip, String netmask, short type,
			HmDomain arg_Domain, String arg_Des) {
		IpAddress resultObj = new IpAddress();
		resultObj.setAddressName(profileName);
		resultObj.setId(null);
		resultObj.setVersion(null);
		resultObj.setTypeFlag(type);
		resultObj.setDefaultFlag(false);
		List<SingleTableItem> items = new ArrayList<SingleTableItem>();
		SingleTableItem item = new SingleTableItem();
		item.setIpAddress(ip);
		if (IpAddress.TYPE_IP_ADDRESS == type){
			item.setNetmask(IpAddress.NETMASK_OF_SINGLE_IP);
		} else if(IpAddress.TYPE_HOST_NAME == type){
			item.setNetmask("");
		} else {
			item.setNetmask(netmask);
		}
		item.setType(SingleTableItem.TYPE_GLOBAL);
		item.setDescription(arg_Des);
		items.add(item);
		resultObj.setItems(items);
		resultObj.setOwner(arg_Domain);
		return resultObj;
	}

	/**
	 * Create the new Vlan which does not exist in database.
	 * 
	 * @param value the vlan
	 * @param arg_Domain domain
	 * @param arg_Des description
	 * @return Vlan
	 */
	public static Vlan createNewVlan(String value, HmDomain arg_Domain,
			String arg_Des) {
		Vlan resultObj = null;
		synchronized (vlanLock) {
			if (null != value && !"".equals(value) && value.length() <= 32) {
				resultObj = QueryUtil.findBoByAttribute(Vlan.class,
						"vlanName", value, arg_Domain.getId());
	
				// it does not exist in database
				if (null == resultObj) {
					resultObj = new Vlan();
					resultObj.setVlanName(value);
					resultObj.setId(null);
					resultObj.setVersion(null);
					resultObj.setDefaultFlag(false);
					List<SingleTableItem> items = new ArrayList<SingleTableItem>();
					SingleTableItem item = new SingleTableItem();
					item.setVlanId(Integer.parseInt(value));
					item.setType(SingleTableItem.TYPE_GLOBAL);
					item.setDescription(arg_Des);
					items.add(item);
					resultObj.setItems(items);
					resultObj.setOwner(arg_Domain);
	
					// create the new object
					try {
						QueryUtil.createBo(resultObj);
					} catch (Exception dbexc) {
					}
				}
			}
		}
		return resultObj;
	}

	/**
	 * Create the new MAC or OUI which does not exist in database.
	 * 
	 * @param value the mac or oui
	 * @param type mac or oui
	 * @param arg_Domain domain
	 * @param arg_Des description
	 * @return MacOrOui
	 */
	public static MacOrOui createNewMAC(String value, short type,
			HmDomain arg_Domain, String arg_Des) {
		MacOrOui resultObj = null;
		if (null != value && !"".equals(value) && value.length() <= 32) {
			resultObj = QueryUtil.findBoByAttribute(MacOrOui.class,
					"macOrOuiName", value, arg_Domain.getId());

			// it does not exist in database
			if (null == resultObj) {
				resultObj = new MacOrOui();
				resultObj.setMacOrOuiName(value);
				resultObj.setId(null);
				resultObj.setVersion(null);
				resultObj.setTypeFlag(type);
				resultObj.setDefaultFlag(false);
				List<SingleTableItem> items = new ArrayList<SingleTableItem>();
				SingleTableItem item = new SingleTableItem();
				item.setMacEntry(value);
				item.setType(SingleTableItem.TYPE_GLOBAL);
				item.setDescription(arg_Des);
				items.add(item);
				resultObj.setItems(items);
				resultObj.setOwner(arg_Domain);

				// create the new object
				try {
					QueryUtil.createBo(resultObj);
				} catch (Exception dbexc) {
				}
			}
		}
		return resultObj;
	}
	
	/**
	 * Create the new UserProfileAttribute which does not exist in database.
	 * 
	 * @param value the attribute value
	 * @param arg_Domain domain
	 * @param arg_Des description
	 * @return UserProfileAttribute
	 */
	public static UserProfileAttribute createNewUserAttribute(String value, HmDomain arg_Domain,
			String arg_Des) {
		UserProfileAttribute resultObj = null;
		if (null != value && !"".equals(value) && value.length() <= 32) {
			resultObj = QueryUtil.findBoByAttribute(UserProfileAttribute.class,
					"attributeName", value, arg_Domain.getId());

			// it does not exist in database
			if (null == resultObj) {
				resultObj = new UserProfileAttribute();
				resultObj.setAttributeName(value);
				resultObj.setId(null);
				resultObj.setVersion(null);
				List<SingleTableItem> items = new ArrayList<SingleTableItem>();
				SingleTableItem item = new SingleTableItem();
				item.setAttributeValue(value);
				item.setType(SingleTableItem.TYPE_GLOBAL);
				item.setDescription(arg_Des);
				items.add(item);
				resultObj.setItems(items);
				resultObj.setOwner(arg_Domain);

				// create the new object
				try {
					QueryUtil.createBo(resultObj);
				} catch (Exception dbexc) {
				}
			}
		}
		return resultObj;
	}

	public static JSONObject createSimpleVlan(int vlanValue, HmDomain domain)
			throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "add");
		if (vlanValue > 0 && null != domain) {
			Vlan vlan = QueryUtil.findBoByAttribute(Vlan.class, "vlanName",
					String.valueOf(vlanValue), domain.getId());
			if (null != vlan) {
				jsonObject.put("msg", MgrUtil
						.getUserMessage("info.simple.object.already.exist"));
			} else {
				vlan = createNewVlan(String.valueOf(vlanValue), domain,
						"Auto created");
				if (null != vlan) {
					JSONObject item = new JSONObject();
					item.put("key", vlan.getId());
					item.put("value", vlan.getVlanName());
					jsonObject.put("item", item);
				} else {// create failed.
					String msg = MgrUtil
							.getUserMessage("info.simple.object.create.failed");
					jsonObject.put("msg", msg);
				}
			}
		} else {
			jsonObject.put("msg", "input value invalid.");
		}
		return jsonObject;
	}

	public static JSONObject createSimpleIp(String ipValue, HmDomain domain)
			throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "add");
		if (null != ipValue && null != domain) {
			// parse the ipValue string
			String ipAddress, netmask = null;
			if (ipValue.indexOf("/") > -1) {
				String[] strs = ipValue.split("/");
				String str1 = strs[0];
				String str2 = strs[1];
				if (ImportCsvFileAction.getIpAddressWrongFlag(str1)) {
					// if ip format is wrong, treat the ipValue as host name
					ipAddress = ipValue;
				} else {
					// ip format is correct
					if (str2.indexOf(".") > -1) {
						// if netmask format is wrong
						if (ImportCsvFileAction.getNetmaskWrongFlag(str2)) {
							if (ImportCsvFileAction.getIpAddressWrongFlag(str2)) {
								ipAddress = ipValue;
							} else {
								// treat as wildcard
								ipAddress = str1;
								netmask = str2;
							}
						} else {
							// treat as network
							ipAddress = str1;
							netmask = str2;
						}
					} else {
						try {
							int maskInt = Integer.parseInt(str2);
							if (maskInt < 1 || maskInt > 32) {
								ipAddress = ipValue;
							} else {
								// treat as network
								ipAddress = str1;
								netmask = NmsUtil.getNetmask(maskInt);
							}
						} catch (Exception e) {
							// not a number, treat the ipValue as host name
							ipAddress = ipValue;
						}
					}
				}
			} else {
				// no split
				ipAddress = ipValue;
			}
			
			short type;
			if (null != netmask) {
				// check as network
				if (ImportCsvFileAction.getNetmaskWrongFlag(netmask)) {
					type = IpAddress.TYPE_IP_WILDCARD;
				} else {
					type = IpAddress.TYPE_IP_NETWORK;
				}
			} else {
				// check as ip address
				if (ImportCsvFileAction.getIpAddressWrongFlag(ipAddress)) {
					type = IpAddress.TYPE_HOST_NAME;
				} else {
					type = IpAddress.TYPE_IP_ADDRESS;
				}
			}
			String ipName = ipAddress;
			// network and wildcard name contains netmask and wildcard value
			if (IpAddress.TYPE_IP_NETWORK == type || IpAddress.TYPE_IP_WILDCARD == type) {
				ipName = ipAddress + "/" + netmask;
			}
			IpAddress ip = QueryUtil.findBoByAttribute(IpAddress.class,
					"addressName", String.valueOf(ipName), domain.getId());
			if (null != ip) {
				jsonObject.put("msg", MgrUtil
						.getUserMessage("info.simple.object.already.exist"));
			} else {
				ip = createNewIP(ipAddress, type, domain, "Auto created",
						netmask);
				if (null != ip) {
					JSONObject item = new JSONObject();
					item.put("key", ip.getId());
					item.put("value", ip.getAddressName());
					jsonObject.put("item", item);
				} else {// create failed.
					String msg = MgrUtil
							.getUserMessage("info.simple.object.create.failed");
					jsonObject.put("msg", msg);
				}
			}
		} else {
			jsonObject.put("msg", "input value invalid.");
		}
		return jsonObject;
	}

	public static JSONObject createSimpleMac(String macValue, HmDomain domain)
			throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "add");
		if (null != macValue && null != domain) {
			MacOrOui oui = QueryUtil.findBoByAttribute(MacOrOui.class,
					"macOrOuiName", String.valueOf(macValue), domain.getId());
			if (null != oui) {
				jsonObject.put("msg", MgrUtil
						.getUserMessage("info.simple.object.already.exist"));
			} else {
				short type = macValue.length() == 6 ? MacOrOui.TYPE_MAC_OUI
						: MacOrOui.TYPE_MAC_ADDRESS;
				oui = createNewMAC(macValue, type, domain, "Auto created");
				if (null != oui) {
					JSONObject item = new JSONObject();
					item.put("key", oui.getId());
					item.put("value", oui.getMacOrOuiName());
					jsonObject.put("item", item);
				} else {// create failed.
					String msg = MgrUtil
							.getUserMessage("info.simple.object.create.failed");
					jsonObject.put("msg", msg);
				}
			}
		} else {
			jsonObject.put("msg", "input value invalid.");
		}
		return jsonObject;
	}

	public static JSONObject removeSimpleVlan(List<Long> ids)
			throws JSONException {
		return removeSimpleObject(Vlan.class, ids);
	}

	public static JSONObject removeSimpleIp(List<Long> ids)
			throws JSONException {
		return removeSimpleObject(IpAddress.class, ids);
	}

	public static JSONObject removeSimpleMac(List<Long> ids)
			throws JSONException {
		return removeSimpleObject(MacOrOui.class, ids);
	}

	private static JSONObject removeSimpleObject(Class<? extends HmBo> boClass,
			List<Long> ids) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "remove");
		if (null != ids && !ids.isEmpty()) {
			try {
				String where = "id in (:s1) and defaultFlag = :s2";
				Object[] values = new Object[] { ids, true };
				List<?> defaultIds = QueryUtil.executeQuery("select id from "
						+ boClass.getSimpleName(), null, new FilterParams(
						where, values));
				List<Long> removalIds = new ArrayList<Long>();
				if(defaultIds.isEmpty()){
					removalIds.addAll(ids);
				}else{
					for(Long id: ids){
						if(defaultIds.contains(id)){
							continue;
						}
						removalIds.add(id);
					}
				}
				
				if (!removalIds.isEmpty()) {
					QueryUtil.removeBos(boClass,removalIds);
					JSONArray jsonArray = new JSONArray(removalIds);
					jsonObject.put("items", jsonArray);
				} else { // must be default items
					jsonObject.put("msg", MgrUtil
							.getUserMessage("error.objectIsDefault"));
				}
			} catch (Exception e) { // remove failed.
				jsonObject.put("msg", MgrUtil.getUserMessage(e));
			}
		} else {
			jsonObject.put("msg", "selected value invalid.");
		}
		return jsonObject;
	}

}