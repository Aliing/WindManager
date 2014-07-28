package com.ah.be.admin.cidClients;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jfree.util.Log;

import com.ah.be.common.ConfigUtil;
import com.ah.be.rest.client.models.ResponseModel;
import com.ah.ui.actions.home.clientManagement.service.CertificateGenSV;
import com.ah.util.Tracer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class CidClientsUtil {
	
	private static final Tracer log = new Tracer(CidClientsUtil.class.getSimpleName());
	
	public static final String CID_CLIENTS_QUERY = "/api/hm/cids/query";
	
	public static final String CID_CLIENTS_ADD = "/api/hm/cids/update";
	
	public static final String CID_CLIENTS_ASC = "asc";
	
	public static final String CID_CLIENTS_DESC = "desc";
	
	public static final String CID_CLIENTS_DEFAULT = "macAddress";
	
	public static String getUrl(){
		return ConfigUtil.getACMConfigServerUrl();
//		return ConfigUtil.getConfigInfo(ConfigUtil.SECTION_AEROHIVE_MDM, 
//                ConfigUtil.KEY_URL_ROOT_PATH);
	}
	
	public static String transObjectToXML(String customerId,String pageSize,String pageNumber){
		XStream xs = new XStream(new DomDriver());
		try{
			CidDevicesRequestEBO cidDevices = new CidDevicesRequestEBO();
			List<CidDeviceSortEBO> deviceSort = new ArrayList<CidDeviceSortEBO>();
			deviceSort.add(new CidDeviceSortEBO(CID_CLIENTS_ASC,CID_CLIENTS_DEFAULT));
			cidDevices.setDeviceSort(deviceSort);
			cidDevices.setCustomerId(customerId);
			if(pageSize != "" && pageSize != null){
				cidDevices.setPageSize(pageSize);
			}
			if(pageNumber != "" && pageNumber != null){
				cidDevices.setPageNumber(pageNumber);
			}
			xs.processAnnotations(CidDevicesRequestEBO.class);
			xs.processAnnotations(CidDeviceSortEBO.class);
			return xs.toXML(cidDevices);
		}catch(Exception e){
			log.error(CidClientsUtil.class.getSimpleName()+":transObjectToXML()","Failed when transfer xml to object", e);
			return null;
		}
		
	}
	
	public static String transObjectToXML(String customerId,String pageSize,String pageNumber,
			               String direction,String orderBy){
		XStream xs = new XStream(new DomDriver());
		try{
			CidDevicesRequestEBO cidDevices = new CidDevicesRequestEBO();
			CidDeviceSortEBO sort = new CidDeviceSortEBO();
			List<CidDeviceSortEBO> deviceSort = new ArrayList<CidDeviceSortEBO>();
			if(direction != "" && direction != null){
				sort.setDirectioin(direction);
			}
			if(orderBy != "" && orderBy != null){
				sort.setOrderBy(orderBy);
			}
			deviceSort.add(sort);
			cidDevices.setDeviceSort(deviceSort);
			cidDevices.setCustomerId(customerId);
			if(pageSize != "" && pageSize != null){
				cidDevices.setPageSize(pageSize);
			}
			if(pageNumber != "" && pageNumber != null){
				cidDevices.setPageNumber(pageNumber);
			}
			xs.processAnnotations(CidDevicesRequestEBO.class);
			xs.processAnnotations(CidDeviceSortEBO.class);
			return xs.toXML(cidDevices);
		}catch(Exception e){
			log.error(CidClientsUtil.class.getSimpleName()+":transObjectToXML()","Failed when transfer xml to object", e);
			return null;
		}
		
	}
	
	public static String transAddedCidDevicesToXML(String customerId,List<CidDeviceEBO> cids){
		XStream xs = new XStream(new DomDriver());
		try{
			CidDevicesSendEBO cidDevices = new CidDevicesSendEBO();
			cidDevices.setCustomerId(customerId);
			if(cids != null){
				cidDevices.setCidList(cids);
			}
			xs.processAnnotations(CidDeviceEBO.class);
			xs.processAnnotations(CidDevicesSendEBO.class);
			return xs.toXML(cidDevices);
		}catch(Exception e){
			log.error(CidClientsUtil.class.getSimpleName()+":transAddedCidDevicesToXML()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
	public static CidDevicesResponseEBO transResponseToObject(ResponseModel res){
		XStream xs = new XStream(new DomDriver());
		try{
			String cids = res.getResponseText();
			xs.processAnnotations(CidDeviceEBO.class);
			xs.processAnnotations(CidDevicesResponseEBO.class);
			return (CidDevicesResponseEBO)xs.fromXML(cids);
		}catch(Exception e){
			log.error(CidClientsUtil.class.getSimpleName()+":transResponseToObject()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
	public static boolean writeFile(String fileName,String content) throws IOException{
		File fl = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			fl = new File(fileName);
			if(CertificateGenSV.existFile(fileName)){
				fl.delete();
			}
			fw = new FileWriter(fl);
			bw = new BufferedWriter(fw);
			bw.write(content,0,content.length());
			bw.close();
			fw.close();
			return true;
			}catch(Exception e){
				bw.close();
				fw.close();
				log.error("writeFile()", "Error when write the string to the file" + fileName, e);
			}
		return false;
	}
}
