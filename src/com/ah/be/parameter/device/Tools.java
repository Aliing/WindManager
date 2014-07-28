package com.ah.be.parameter.device;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import edu.emory.mathcs.backport.java.util.Collections;

public class Tools {
	
	private static Map<Short, String> map = new HashMap<>();
	
	static{
		map.put((short)1, "AP20");
		map.put((short)0, "AP28");
		map.put((short)2, "AP320");
		map.put((short)3, "AP340");
		map.put((short)6, "AP110");
		map.put((short)5, "AP120");
		map.put((short)15, "AP121");
		map.put((short)16, "AP141");
		map.put((short)12, "AP170");
		map.put((short)8, "AP330");
		map.put((short)9, "AP350");
		map.put((short)20, "AP370");
		map.put((short)21, "AP390");
		map.put((short)25, "AP230");
		map.put((short)10, "VPN Gateway Virtual Appliance");
		map.put((short)26, "VPN Gateway");
		map.put((short)11, "BR100");
		map.put((short)13, "BR200");
		map.put((short)14, "BR200-WP");
		map.put((short)19, "BR200-LTE-VZ");
		map.put((short)17, "SR2024");
		map.put((short)22, "SR2124P");
		map.put((short)23, "SR2148P");
		map.put((short)24, "SR2024P");
	}
	
	private static String[] test_args = {
		"spt_device_type",
		"spt_radio_counts",
		"spt_ethernet_counts",
		"spt_sfp_counts",
		"spt_sfp_index",
		"spt_usb_counts",
		"spt_dynamic_routing",
		"spt_radius_server",
		"spt_bonjour_service",
		"spt_pse",
		"spt_wifi_client_mode",
		"spt_channel40M_for_2.4g",
		"spt_teacher_view",
		"spt_idm_proxy",
		"spt_L7_service",
		"spt_11ac",
		"spt_image_ls_name",
		"spt_image_internal_name",
		"spt_vpn_service_server",
		"spt_latest_version",
		"start_version",
		"spt_hive_ui",
		"spt_multiple_host",
		"SLA_max_11n_mcs_rate",
		"spt_SFPspeed_only_auto",
		"spt_device_image_counts"
	};

	public static void main(String[] args) throws JSONException{
		JSONObject json = DevicePropertyManage.getInstance().getDevicesJSON();
		System.out.println( DevicePropertyManage.getInstance().getDevicesJSONStr());
		StringBuilder sb = new StringBuilder();
		
		for(String key : test_args){
			List<ResultObj> resultList = new ArrayList<>();
			
			Iterator<Entry<Short, String>> productItems = map.entrySet().iterator();
			while(productItems.hasNext()){
				Entry<Short, String> entryObj = productItems.next();
				Short keyShort = entryObj.getKey();
				String valueStr = null;
				try{
					valueStr =  json.getJSONObject(keyShort.toString()).get(key).toString();
				}catch(Exception e){
					valueStr = "false";
				}
				resultList.add(new ResultObj(entryObj.getValue(), valueStr));
			}
			
			Collections.sort(resultList, new Comparator<ResultObj>(){
				@Override
				public int compare(ResultObj o1, ResultObj o2) {
//					int res = o1.getValue().compareTo(o2.getValue());
//					if(res == 0){
//						res = o1.getDeviceName().compareTo(o2.getDeviceName());
//					}
//					
//					return res;
					
					return o1.getDeviceName().compareTo(o2.getDeviceName());
				}
				
			});
			
			sb.append(key);
			sb.append("\r\n");
			for(ResultObj reObj : resultList){
				sb.append(reObj.toString());
				sb.append("\r\n");
			}
			sb.append("\r\n");
		}
		
		
		System.out.println(sb.toString());
	}
}

class ResultObj{
	private String deviceName;
//	private String key;
	private String value;
	
	public ResultObj(String deviceName, String value){
		this.deviceName = deviceName;
		this.value = value;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(deviceName);
		while(sb.length() < 41){
			sb.append(" ");
		}
		sb.append(value);
		return sb.toString();
	}
	
}